package com.jowen.smartqa.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jowen.smartqa.annotation.AuthCheck;
import com.jowen.smartqa.common.BaseResponse;
import com.jowen.smartqa.common.DeleteRequest;
import com.jowen.smartqa.common.ErrorCode;
import com.jowen.smartqa.common.ResultUtils;
import com.jowen.smartqa.constant.UserConstant;
import com.jowen.smartqa.exception.BusinessException;
import com.jowen.smartqa.exception.ThrowUtils;
import com.jowen.smartqa.manager.AiManager;
import com.jowen.smartqa.model.dto.question.*;
import com.jowen.smartqa.model.entity.App;
import com.jowen.smartqa.model.entity.Question;
import com.jowen.smartqa.model.entity.User;
import com.jowen.smartqa.model.enums.AppTypeEnum;
import com.jowen.smartqa.model.vo.QuestionVO;
import com.jowen.smartqa.service.AppService;
import com.jowen.smartqa.service.QuestionService;
import com.jowen.smartqa.service.UserService;
import io.reactivex.Flowable;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private AppService appService;

    @Resource
    private Scheduler vipScheduler;

    // region 增删改查

    /**
     * 创建题目
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        question.setQuestionContent(JSONUtil.toJsonStr(questionAddRequest.getQuestionContent()));
        // 数据校验
        questionService.validQuestionCreate(question);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setQuestionContent(JSONUtil.toJsonStr(questionAddRequest.getQuestionContent()));
        // 写入数据库
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除题目
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目（仅管理员可用）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        question.setQuestionContent(JSONUtil.toJsonStr(questionUpdateRequest.getQuestionContent()));
        // 数据校验
        questionService.validQuestionUpdate(question);
        // 判断是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        question.setQuestionContent(JSONUtil.toJsonStr(questionUpdateRequest.getQuestionContent()));
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取题目列表（仅管理员可用）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取题目列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前登录用户创建的题目列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 编辑题目（给用户使用）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        question.setQuestionContent(JSONUtil.toJsonStr(questionEditRequest.getQuestionContent()));
        // 数据校验
        questionService.validQuestionUpdate(question);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        question.setQuestionContent(JSONUtil.toJsonStr(questionEditRequest.getQuestionContent()));
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    private static final String GENERATE_QUESTION_SYSTEM_MESSAGE = "你是一位严谨的出题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称\n" +
            "【【【应用描述】】】\n" +
            "应用类别\n" +
            "要生成的题目数\n" +
            "每个题目的选项数\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来出题：\n" +
            "1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复\n" +
            "2. 严格按照下面的 json 格式输出题目和选项\n" +
            "```\n" +
            "[{\"options\":[{\"value\":\"选项内容\",\"key\":\"A\"},{\"value\":\"\",\"key\":\"B\"}],\"title\":\"题目标题\"}]\n" +
            "```\n" +
            "title 是题目，options 是选项，每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容\n" +
            "3. 检查题目是否包含序号，若包含序号则去除序号\n" +
            "4. 返回的题目列表格式必须为 JSON 数组";

    /**
     * 生成题目的用户消息
     *
     * @param app
     * @param questionNumber
     * @param optionNumber
     * @return
     */
    private String getGenerateQuestionUserMessage(App app, int questionNumber, int optionNumber) {
        return app.getAppName() + "\n" +
                "【【【" + app.getAppDesc() + "】】】" + "\n" +
                AppTypeEnum.getEnumByValue(app.getAppType()).getText() + "类" + "\n" +
                questionNumber + "\n" +
                optionNumber;
    }

    @PostMapping("/ai_generate")
    public BaseResponse<List<QuestionContentDTO>> aiGenerateQuestion(
            @RequestBody AiGenerateQuestionRequest aiGenerateQuestionRequest) {
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        Integer questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        Integer optionNumber = aiGenerateQuestionRequest.getOptionNumber();

        // 获取应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);

        // 生成用户消息
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);
        String result = aiManager.doRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage);

        // 截取json
        int start = result.indexOf("[");
        int end = result.lastIndexOf("]");
        String json = result.substring(start, end + 1);

        return ResultUtils.success(JSONUtil.toList(json, QuestionContentDTO.class));
    }

    /**
     * AI流式生成题目
     *
     * @param aiGenerateQuestionRequest
     * @return
     */
    @GetMapping("/ai_generate/sse")
    public SseEmitter aiGenerateQuestionSSE(AiGenerateQuestionRequest aiGenerateQuestionRequest, boolean isVip) {
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        Integer questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        Integer optionNumber = aiGenerateQuestionRequest.getOptionNumber();

        // 获取应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);

        // 生成用户消息
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);

        // 创建sse
        SseEmitter sseEmitter = new SseEmitter();

        // 截取
        Flux<String> resultFlux = aiManager.doStreamRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage);
        AtomicInteger count = new AtomicInteger(0);
        StringBuilder data = new StringBuilder();

        // 判断是vip用户还是普通用户
//        User loginUser = userService.getLoginUser(request);
//        Scheduler scheduler = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole()) ? vipScheduler : Schedulers.io();
        Scheduler scheduler = isVip ? vipScheduler : Schedulers.single();

        resultFlux.publishOn(scheduler)
                .map(content -> content.replaceAll("\\s", ""))
                .flatMap(content -> {
                    // 将String转换为List<Character>
                    List<Character> characters = new ArrayList<>();
                    for (char c : content.toCharArray()) {
                        characters.add(c);
                    }
                    return Flowable.fromIterable(characters);
                })
                .doOnNext(character -> {
                    if (character.equals('{')) count.incrementAndGet();
                    if (count.get() > 0) data.append(character);
                    if (character.equals('}')) {
                        count.decrementAndGet();
                        if (count.get() == 0) {
                            // 测试输出当前线程名称
                            System.out.println(Thread.currentThread().getName());
                            try {
                                sseEmitter.send(JSONUtil.toJsonStr(data.toString()));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            // 清空
                            data.setLength(0);
                        }
                    }
                })
                .doOnError(throwable -> {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI生成题目失败");
                })
                .doOnComplete(sseEmitter::complete)
                .subscribe();
        return sseEmitter;
    }
}
