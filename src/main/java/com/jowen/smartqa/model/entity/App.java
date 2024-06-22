package com.jowen.smartqa.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.jowen.smartqa.validation.PredicateTrue;
import com.jowen.smartqa.validators.AppScoringStrategyEnumPredicateProvider;
import com.jowen.smartqa.validators.AppTypeEnumPredicateProvider;
import com.jowen.smartqa.validators.ReviewStatusEnumPredicateProvider;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 应用
 * @TableName app
 */
@TableName(value ="app")
@Data
public class App implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message = "id不能为空", groups = {Update.class})
    private Long id;

    /**
     * 应用名
     */
    @NotBlank(message = "应用名不能为空", groups = {Create.class, Update.class})
    @Size(max=60, message = "应用名长度不能超过60", groups = {Create.class, Update.class})
    private String appName;

    /**
     * 应用描述
     */
    @NotBlank(message = "应用描述不能为空", groups = {Create.class, Update.class})
    private String appDesc;

    /**
     * 应用图标
     */
    private String appIcon;

    /**
     * 应用类型（0-得分类，1-测评类）
     */
    @NotNull(message = "应用类型不能为空", groups = {Create.class})
    @PredicateTrue(predicateProvider = AppTypeEnumPredicateProvider.class,
            message = "应用类型不合法", groups = {Create.class, Update.class})
    private Integer appType;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    @NotNull(message = "评分策略不能为空", groups = {Create.class})
    @PredicateTrue(predicateProvider = AppScoringStrategyEnumPredicateProvider.class,
            message = "评分策略不合法", groups = {Create.class, Update.class})
    private Integer scoringStrategy;

    /**
     * 审核状态：0-待审核, 1-通过, 2-拒绝
     */
    @PredicateTrue(predicateProvider = ReviewStatusEnumPredicateProvider.class,
            message = "审核状态不合法", groups = {Create.class, Update.class})
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核人 id
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public static interface Create{}
    public static interface Update{}

}