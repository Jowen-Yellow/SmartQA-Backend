package com.jowen.smartqa.model.dto.question;

import lombok.Data;

/**
 * AI生成问题请求
 *
 * @author Jowen Yellow
 * @since 1.0
 */
@Data
public class AiGenerateQuestionRequest {
    Long appId;
    Integer questionNumber;
    Integer optionNumber;
}
