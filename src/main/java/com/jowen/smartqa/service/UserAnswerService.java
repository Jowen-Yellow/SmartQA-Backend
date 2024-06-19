package com.jowen.smartqa.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jowen.smartqa.model.dto.userAnswer.UserAnswerQueryRequest;
import com.jowen.smartqa.model.entity.UserAnswer;
import com.jowen.smartqa.model.vo.UserAnswerVO;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户答案服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface UserAnswerService extends IService<UserAnswer> {


    /**
     * 创建用户答案校验
     * @param userAnswer
     */
    @Validated(UserAnswer.Create.class)
    void validUserAnswerCreate(@Valid UserAnswer userAnswer);

    /**
     * 更新用户答案校验
     * @param userAnswer
     */
    @Validated(UserAnswer.Update.class)
    void validUserAnswerUpdate(@Valid UserAnswer userAnswer);
    /**
     * 获取查询条件
     *
     * @param userAnswerQueryRequest
     * @return
     */
    QueryWrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequest userAnswerQueryRequest);
    
    /**
     * 获取用户答案封装
     *
     * @param userAnswer
     * @param request
     * @return
     */
    UserAnswerVO getUserAnswerVO(UserAnswer userAnswer, HttpServletRequest request);

    /**
     * 分页获取用户答案封装
     *
     * @param userAnswerPage
     * @param request
     * @return
     */
    Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> userAnswerPage, HttpServletRequest request);
}
