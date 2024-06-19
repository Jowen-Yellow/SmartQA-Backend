package com.jowen.smartqa.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 审核状态枚举
 */
@Getter
public enum ReviewStatusEnum {
    REVIEWING("审核中", 0),
    PASS("通过", 1),
    REJECT("拒绝", 2);

    private final String text;
    private final int value;

    ReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return 值列表
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(ReviewStatusEnum::getValue).collect(toList());
    }

    /**
     * 根据值获取枚举
     *
     * @param value 值
     * @return 枚举常量
     */
    public static ReviewStatusEnum getEnumByValue(int value) {
        for (ReviewStatusEnum reviewStatusEnum : ReviewStatusEnum.values()) {
            if (reviewStatusEnum.value == value) {
                return reviewStatusEnum;
            }
        }
        return null;
    }
}
