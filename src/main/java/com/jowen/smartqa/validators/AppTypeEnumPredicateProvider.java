package com.jowen.smartqa.validators;

import com.jowen.smartqa.model.enums.AppTypeEnum;
import com.jowen.smartqa.validation.PredicateProvider;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class AppTypeEnumPredicateProvider implements PredicateProvider<Integer> {
    @Override
    public Predicate<Integer> getPredicate() {
        return x -> x == null || AppTypeEnum.getEnumByValue(x) != null;
    }
}
