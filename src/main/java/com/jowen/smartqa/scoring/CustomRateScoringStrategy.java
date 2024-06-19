package com.jowen.smartqa.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jowen.smartqa.model.dto.question.QuestionContentDTO;
import com.jowen.smartqa.model.entity.App;
import com.jowen.smartqa.model.entity.Question;
import com.jowen.smartqa.model.entity.ScoringResult;
import com.jowen.smartqa.model.entity.UserAnswer;
import com.jowen.smartqa.model.enums.AppScoringStrategyEnum;
import com.jowen.smartqa.model.enums.AppTypeEnum;
import com.jowen.smartqa.model.vo.QuestionVO;
import com.jowen.smartqa.service.QuestionService;
import com.jowen.smartqa.service.ScoringResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomRateScoringStrategy implements ScoringStrategy {
    private final QuestionService questionService;
    private final ScoringResultService scoringResultService;

    @Override
    public UserAnswer scoring(List<String> choices, App app) {
        // 1.根据id查询题目和题目结果信息（按分数降序排序）
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, app.getId())
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, app.getId())
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );

        // 2. 统计用户总分
        int totalScore = 0;
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContentList = questionVO.getQuestionContent();

        for (QuestionContentDTO questionContentDTO : questionContentList) {
            for (String choice : choices) {
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    if (option.getKey().equals(choice)) {
                        totalScore += option.getScore();
                    }
                }
            }
        }


        // 3. 遍历得分范围，找出最大得分
        ScoringResult maxScoringResult = scoringResultList.get(0);

        for (ScoringResult scoringResult : scoringResultList) {
            Integer resultScoreRange = scoringResult.getResultScoreRange();
            if (totalScore >= resultScoreRange) {
                maxScoringResult = scoringResult;
                break;
            }
        }

        // 4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(app.getId());
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);

        return userAnswer;
    }

    @Override
    public AppTypeEnum getAppType() {
        return AppTypeEnum.SCORING;
    }

    @Override
    public AppScoringStrategyEnum getScoringStrategy() {
        return AppScoringStrategyEnum.CUSTOM;
    }
}
