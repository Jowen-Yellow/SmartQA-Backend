package com.jowen.smartqa.model.dto.question;

import lombok.Data;

@Data
public class QuestionAnswerDTO {
    private Long questionId;
    private String title;
    private String answer;
}
