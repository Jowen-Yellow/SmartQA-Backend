package com.jowen.smartqa.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jowen.smartqa.model.dto.app.AppQueryRequest;
import com.jowen.smartqa.model.entity.App;
import com.jowen.smartqa.model.vo.AppVO;
import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 应用服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface AppService extends IService<App> {

    /**
     * 校验应用创建参数
     * @param app 应用
     */
    @Validated(App.Create.class)
    void validAppCreate(@Valid App app);
    /**
     * 校验应用更新参数
     * @param app 应用
     */
    @Validated(App.Update.class)
    void validAppUpdate(@Valid App app);

    /**
     * 获取查询条件
     *
     * @param appQueryRequest
     * @return
     */
    QueryWrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest);
    
    /**
     * 获取应用封装
     *
     * @param app
     * @param request
     * @return
     */
    AppVO getAppVO(App app, HttpServletRequest request);

    /**
     * 分页获取应用封装
     *
     * @param appPage
     * @param request
     * @return
     */
    Page<AppVO> getAppVOPage(Page<App> appPage, HttpServletRequest request);
}
