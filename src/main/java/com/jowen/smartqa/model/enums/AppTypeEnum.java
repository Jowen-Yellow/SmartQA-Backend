package com.jowen.smartqa.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * 应用类型枚举
 */
@Getter
public enum AppTypeEnum {
    SCORING("得分类", 0),
    TEST("测评类", 1);

    private final String text;
    private final int value;

    AppTypeEnum(String text, int value){
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     * @return 值列表
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(AppTypeEnum::getValue).collect(toList());
    }

    /**
     * 根据值获取枚举
     * @param value 值
     * @return 枚举常量
     */
    public static AppTypeEnum getEnumByValue(int value) {
        for (AppTypeEnum appTypeEnum : AppTypeEnum.values()) {
            if (appTypeEnum.value == value) {
                return appTypeEnum;
            }
        }
        return null;
    }

}
