package com.jowen.smartqa;

import com.jowen.smartqa.model.entity.UserAnswer;
import com.jowen.smartqa.service.UserAnswerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ShardingSphereTests {
    @Autowired
    private UserAnswerService userAnswerService;

    @Test
    public void test() {
        UserAnswer userAnswer1 = new UserAnswer();
        userAnswer1.setAppId(1L);
        userAnswer1.setUserId(1L);
        userAnswer1.setResultName("test1");
        userAnswerService.save(userAnswer1);

        UserAnswer userAnswer2 = new UserAnswer();
        userAnswer2.setAppId(2L);
        userAnswer2.setUserId(1L);
        userAnswer2.setResultName("test2");
        userAnswerService.save(userAnswer2);

        System.out.println(userAnswerService.getById(userAnswer1.getId()));
        System.out.println(userAnswerService.getById(userAnswer2.getId()));
    }
}
