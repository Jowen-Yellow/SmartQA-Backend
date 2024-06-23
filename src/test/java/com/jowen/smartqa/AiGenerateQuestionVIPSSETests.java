package com.jowen.smartqa;

import cn.hutool.core.thread.ThreadUtil;
import com.jowen.smartqa.controller.QuestionController;
import com.jowen.smartqa.model.dto.question.AiGenerateQuestionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

@SpringBootTest
public class AiGenerateQuestionVIPSSETests {
    @Resource
    private QuestionController questionController;

    @Test
    public void test() {
        AiGenerateQuestionRequest request = new AiGenerateQuestionRequest();
        request.setAppId(3L);
        request.setQuestionNumber(10);
        request.setOptionNumber(2);


        ThreadUtil.sleep(1000 * 60 * 60 * 24);
    }
}
