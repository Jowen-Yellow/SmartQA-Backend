package com.jowen.smartqa.validators;

import com.jowen.smartqa.model.enums.ReviewStatusEnum;
import com.jowen.smartqa.validation.PredicateProvider;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class ReviewStatusEnumPredicateProvider implements PredicateProvider<Integer> {
    @Override
    public Predicate<Integer> getPredicate() {
        return x -> x == null || ReviewStatusEnum.getEnumByValue(x) != null;
    }
}
