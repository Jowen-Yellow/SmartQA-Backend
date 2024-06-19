package com.jowen.smartqa.scoring;

import com.jowen.smartqa.model.entity.App;
import com.jowen.smartqa.model.entity.UserAnswer;
import com.jowen.smartqa.model.enums.AppScoringStrategyEnum;
import com.jowen.smartqa.model.enums.AppTypeEnum;

import java.util.List;

/**
 * 评分策略接口。其中scoring方法是核心方法，根据题目的选项和应用类型进行评分。
 * getAppType和getScoringStrategy方法用于获取应用类型和评分策略。
 *
 * @since 1.0
 * @author Jowen Yellow
 */
public interface ScoringStrategy {
    UserAnswer scoring(List<String> choices, App app);
    AppTypeEnum getAppType();
    AppScoringStrategyEnum getScoringStrategy();
}
