package com.jowen.smartqa.controller;

import com.jowen.smartqa.common.BaseResponse;
import com.jowen.smartqa.common.ErrorCode;
import com.jowen.smartqa.common.ResultUtils;
import com.jowen.smartqa.exception.ThrowUtils;
import com.jowen.smartqa.mapper.UserAnswerMapper;
import com.jowen.smartqa.model.dto.statistics.AppAnswerCountDTO;
import com.jowen.smartqa.model.dto.statistics.AppAnswerResultCountDTO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app/statistics")
public class AppStatisticsController {
    @Resource
    private UserAnswerMapper userAnswerMapper;

    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        return ResultUtils.success(userAnswerMapper.getAppAnswerCount());
    }

    @GetMapping("/answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.getAnswerResultCount(appId));
    }
}
