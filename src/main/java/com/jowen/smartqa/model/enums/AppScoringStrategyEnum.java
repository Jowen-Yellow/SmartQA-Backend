package com.jowen.smartqa.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 评分策略枚举
 */
@Getter
public enum AppScoringStrategyEnum {
    CUSTOM("自定义", 0),
    AI("AI", 1);

    private final String text;
    private final int value;

    AppScoringStrategyEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return 值列表
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(AppScoringStrategyEnum::getValue).collect(toList());
    }

    /**
     * 根据值获取枚举
     *
     * @param value 值
     * @return 枚举常量
     */
    public static AppScoringStrategyEnum getEnumByValue(int value) {
        for (AppScoringStrategyEnum appTypeEnum : AppScoringStrategyEnum.values()) {
            if (appTypeEnum.value == value) {
                return appTypeEnum;
            }
        }
        return null;
    }

}
