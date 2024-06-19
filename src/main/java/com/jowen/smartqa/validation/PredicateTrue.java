package com.jowen.smartqa.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 断言校验器。通过实现 {@link PredicateProvider} 接口，提供断言逻辑。
 *
 * @since 1.0
 * @author Jowen Yellow
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(PredicateTrue.List.class)
@Constraint(validatedBy = PredicateTrueValidator.class)
public @interface PredicateTrue {
    Class<? extends PredicateProvider<?>> predicateProvider();

    String message() default "校验未通过";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    @Target({ FIELD })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        PredicateTrue[] value();
    }
}