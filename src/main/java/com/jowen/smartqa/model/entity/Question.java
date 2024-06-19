package com.jowen.smartqa.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jowen.smartqa.validation.PredicateTrue;
import com.jowen.smartqa.validators.AppPresentPredicate;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 题目
 *
 * @TableName question
 */
@TableName(value = "question")
@Data
public class Question implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message = "id不能为空", groups = {Update.class})
    private Long id;

    /**
     * 题目内容（json格式）
     */
    @NotBlank(message = "题目内容不能为空", groups = {Create.class, Update.class})
    private String questionContent;

    /**
     * 应用 id
     */
    @NotNull(message = "应用id不能为空", groups = {Create.class})
    @Min(value = 1, message = "应用id不能小于1", groups = {Create.class})
    @PredicateTrue(predicateProvider = AppPresentPredicate.class, message = "应用不存在", groups = {Create.class, Update.class})
    private Long appId;

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

    public static interface Create {
    }

    public static interface Update {
    }
}