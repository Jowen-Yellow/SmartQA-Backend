package com.jowen.smartqa.scoring;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jowen.smartqa.common.ErrorCode;
import com.jowen.smartqa.exception.BusinessException;
import com.jowen.smartqa.manager.AiManager;
import com.jowen.smartqa.model.dto.question.QuestionAnswerDTO;
import com.jowen.smartqa.model.dto.question.QuestionContentDTO;
import com.jowen.smartqa.model.entity.App;
import com.jowen.smartqa.model.entity.Question;
import com.jowen.smartqa.model.entity.UserAnswer;
import com.jowen.smartqa.model.enums.AppScoringStrategyEnum;
import com.jowen.smartqa.model.enums.AppTypeEnum;
import com.jowen.smartqa.model.vo.QuestionVO;
import com.jowen.smartqa.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AiTestScoringStrategy implements ScoringStrategy {
    private final ChatClient chatClient;
    private final AiManager aiManager;
    private final QuestionService questionService;
    private final RedissonClient redissonClient;
    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";
    /**
     * 缓存
     */
    private final Cache<String, String> answerCacheMap = Caffeine.newBuilder()
            .initialCapacity(1024)
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .build();
    /**
     * AI 评分系统消息
     */
    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象";

    /**
     * AI 评分用户消息封装
     *
     * @param app
     * @param questionContentDTOList
     * @param choices
     * @return
     */
    private String getAiTestScoringUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append("【【【").append(app.getAppDesc()).append("】】】").append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionContentDTO questionContentDTO = questionContentDTOList.get(i);
            String choice = choices.get(i);
            String title = questionContentDTO.getTitle();
            String answer = questionContentDTO.getOptions().stream()
                    .filter(option -> option.getKey().equals(choice))
                    .map(QuestionContentDTO.Option::getValue)
                    .findFirst()
                    .orElse("");

            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(title);
            questionAnswerDTO.setAnswer(answer);
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }

    @Override
    public UserAnswer scoring(List<String> choices, App app) {
        String choicesStr = JSONUtil.toJsonStr(choices);
        String value = answerCacheMap.getIfPresent(buildCacheKey(app.getId(), choicesStr));
        // 如果缓存中存在，则直接返回
        if (StringUtils.hasText(value)) {
            UserAnswer userAnswer = JSONUtil.toBean(value, UserAnswer.class);
            userAnswer.setAppId(app.getId());
            userAnswer.setScoringStrategy(getScoringStrategy().getValue());
            userAnswer.setAppType(getAppType().getValue());
            userAnswer.setChoices(choicesStr);

            return userAnswer;
        }

        RLock lock = redissonClient.getLock(AI_ANSWER_LOCK);
        try {
            // 竞争分布式锁
            if (lock.tryLock(3, 15, TimeUnit.SECONDS)) {
                // 获取锁成功
                Question question = questionService.getOne(
                        Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, app.getId()));
                QuestionVO questionVO = QuestionVO.objToVo(question);
                List<QuestionContentDTO> questionContentDTOList = questionVO.getQuestionContent();
                String userMessage = getAiTestScoringUserMessage(app, questionContentDTOList, choices);

                String result = aiManager.doRequest(AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);

                int start = result.indexOf("{");
                int end = result.lastIndexOf("}");
                String json = result.substring(start, end + 1);

                UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
                userAnswer.setAppId(app.getId());
                userAnswer.setScoringStrategy(getScoringStrategy().getValue());
                userAnswer.setAppType(getAppType().getValue());
                userAnswer.setChoices(choicesStr);

                // 如果缓存中不存在，则添加到缓存中
                String jsonStr = JSONUtil.toJsonStr(userAnswer);
                answerCacheMap.put(buildCacheKey(app.getId(), choicesStr), jsonStr);

                return userAnswer;
            }
            return null;
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取分布式锁失败");
        } finally {
            if (lock != null && lock.isLocked()) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    @Override
    public AppTypeEnum getAppType() {
        return AppTypeEnum.TEST;
    }

    @Override
    public AppScoringStrategyEnum getScoringStrategy() {
        return AppScoringStrategyEnum.AI;
    }

    private String buildCacheKey(Long appId, String choices) {
        return DigestUtil.md5Hex(appId + ":" + choices);
    }
}
