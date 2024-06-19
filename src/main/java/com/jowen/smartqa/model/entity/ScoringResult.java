package com.jowen.smartqa.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.jowen.smartqa.validation.PredicateTrue;
import com.jowen.smartqa.validators.AppPresentPredicate;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 评分结果
 * @TableName scoring_result
 */
@TableName(value ="scoring_result")
@Data
public class ScoringResult implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message = "id不能为空", groups = {Update.class})
    private Long id;

    /**
     * 结果名称，如物流师
     */
    @NotBlank(message = "结果名称不能为空", groups = {Create.class})
    @Size(max=128, message = "结果名称长度不能超过128", groups = {Create.class, Update.class})
    private String resultName;

    /**
     * 结果描述
     */
    private String resultDesc;

    /**
     * 结果图片
     */
    private String resultPicture;

    /**
     * 结果属性集合 JSON，如 [I,S,T,J]
     */
    private String resultProp;

    /**
     * 结果得分范围，如 80，表示 80及以上的分数命中此结果
     */
    private Integer resultScoreRange;

    /**
     * 应用 id
     */
    @NotNull(message = "应用id不能为空", groups = {Create.class})
    @PredicateTrue(predicateProvider = AppPresentPredicate.class, message = "应用id不能为空", groups = {Create.class})
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

    public static interface Create{}
    public static interface Update{}
}