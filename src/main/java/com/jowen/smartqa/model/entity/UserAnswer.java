package com.jowen.smartqa.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jowen.smartqa.validation.PredicateTrue;
import com.jowen.smartqa.validators.AppPassPredicate;
import com.jowen.smartqa.validators.AppPresentPredicate;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户答题记录
 *
 * @TableName user_answer
 */
@TableName(value = "user_answer")
@Data
public class UserAnswer implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message = "id不能为空", groups = {Create.class, Update.class})
    private Long id;

    /**
     * 应用 id
     */
    @NotNull(message = "应用id不能为空", groups = {Create.class})
    @Min(value = 1L, message = "应用id不能小于1", groups = {Create.class})
    @PredicateTrue(predicateProvider = AppPresentPredicate.class,
            message = "应用不存在", groups = {Create.class, Update.class})
    @PredicateTrue(predicateProvider = AppPassPredicate.class,
            message = "应用未通过审核", groups = {Create.class, Update.class})
    private Long appId;

    /**
     * 应用类型（0-得分类，1-角色测评类）
     */
    private Integer appType;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    private Integer scoringStrategy;

    /**
     * 用户答案（JSON 数组）
     */
    private String choices;

    /**
     * 评分结果 id
     */
    private Long resultId;

    /**
     * 结果名称，如物流师
     */
    private String resultName;

    /**
     * 结果描述
     */
    private String resultDesc;

    /**
     * 结果图标
     */
    private String resultPicture;

    /**
     * 得分
     */
    private Integer resultScore;

    /**
     * 用户 id
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

    public static interface Create {
    }

    public static interface Update {
    }
}