package com.jowen.smartqa.validation;

import java.util.function.Predicate;

/**
 * 断言函数提供者接口。
 * @param <T> 断言函数的参数类型
 */
public interface PredicateProvider<T> {
    /**
     * 获取断言函数
     * @return 断言函数
     */
    Predicate<T> getPredicate();
}
