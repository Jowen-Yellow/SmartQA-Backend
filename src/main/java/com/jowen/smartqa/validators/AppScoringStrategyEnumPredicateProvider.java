package com.jowen.smartqa.validators;

import com.jowen.smartqa.model.enums.AppScoringStrategyEnum;
import com.jowen.smartqa.validation.PredicateProvider;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class AppScoringStrategyEnumPredicateProvider implements PredicateProvider<Integer> {
    @Override
    public Predicate<Integer> getPredicate() {
        return x -> x == null || AppScoringStrategyEnum.getEnumByValue(x) != null;
    }
}
