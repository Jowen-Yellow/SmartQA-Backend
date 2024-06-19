package com.jowen.smartqa.validators;

import com.jowen.smartqa.service.AppService;
import com.jowen.smartqa.validation.PredicateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class AppPresentPredicate implements PredicateProvider<Long> {
    private final AppService appService;

    @Override
    public Predicate<Long> getPredicate() {
        return id -> id == null || appService.getById(id) != null;
    }
}
