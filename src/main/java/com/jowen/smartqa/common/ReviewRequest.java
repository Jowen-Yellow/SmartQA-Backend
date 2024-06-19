package com.jowen.smartqa.common;

import lombok.Data;

/**
 * 审核请求
 */
@Data
public class ReviewRequest {
    private Long id;
    private int reviewStatus;
    private String reviewReason;
}
