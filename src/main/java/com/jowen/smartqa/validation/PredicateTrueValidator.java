package com.jowen.smartqa.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.function.Predicate;

/**
 * 断言验证器。仅提供给PredicateTrue注解使用。
 *
 * @author Jowen Yellow
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
class PredicateTrueValidator implements ConstraintValidator<PredicateTrue, Object> {
    private Predicate<Object> predicate;
    private final ApplicationContext applicationContext;

    /**
     * 注解初始化，获得Predicate。
     * <p>
     * 方法中的强制转换不会有类型安全问题，因为PredicateProvider的getPredicate
     * 方法返回的Predicate是泛型，且客户端无法获取Predicate中的泛型实例。
     * </p>
     *
     * @param constraintAnnotation 注解
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initialize(PredicateTrue constraintAnnotation) {
        try {
            predicate = (Predicate<Object>) applicationContext.getBean(constraintAnnotation.predicateProvider()).getPredicate();
        } catch (Exception e) {
            throw new RuntimeException("创建PredicateProvider实例失败", e);
        }
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return predicate.test(value);
    }
}
