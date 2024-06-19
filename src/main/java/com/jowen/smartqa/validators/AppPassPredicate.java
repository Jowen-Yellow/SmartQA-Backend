package com.jowen.smartqa.validators;

import com.jowen.smartqa.model.entity.App;
import com.jowen.smartqa.model.enums.ReviewStatusEnum;
import com.jowen.smartqa.service.AppService;
import com.jowen.smartqa.validation.PredicateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class AppPassPredicate implements PredicateProvider<Long> {
    private final AppService appService;

    @Override
    public Predicate<Long> getPredicate() {
        return x -> {
            App app = appService.getById(x);
            return x == null || ReviewStatusEnum.PASS.equals(ReviewStatusEnum.getEnumByValue(app.getReviewStatus()));
        };
    }
}
