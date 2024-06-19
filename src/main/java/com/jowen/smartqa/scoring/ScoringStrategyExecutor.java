package com.jowen.smartqa.scoring;

import com.jowen.smartqa.common.ErrorCode;
import com.jowen.smartqa.exception.BusinessException;
import com.jowen.smartqa.model.entity.App;
import com.jowen.smartqa.model.entity.UserAnswer;
import com.jowen.smartqa.model.enums.AppScoringStrategyEnum;
import com.jowen.smartqa.model.enums.AppTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScoringStrategyExecutor {
    private final List<ScoringStrategy> scoringStrategyList;

    public UserAnswer scoring(List<String> choices, App app) {
        AppTypeEnum appTypeEnum = AppTypeEnum.getEnumByValue(app.getAppType());
        AppScoringStrategyEnum scoringStrategyEnum = AppScoringStrategyEnum.getEnumByValue(app.getScoringStrategy());
        if (appTypeEnum == null || scoringStrategyEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用类型或评分策略不合法");
        }
        return scoringStrategyList.stream()
                .filter(scoringStrategy -> scoringStrategyEnum.equals(scoringStrategy.getScoringStrategy())
                        && appTypeEnum.equals(scoringStrategy.getAppType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到对应的评分策略"))
                .scoring(choices, app);
    }
}
