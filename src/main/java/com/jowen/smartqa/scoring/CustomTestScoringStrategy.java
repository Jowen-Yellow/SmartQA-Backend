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
import com.jowen.smartqa.model.vo.ScoringResultVO;
import com.jowen.smartqa.service.QuestionService;
import com.jowen.smartqa.service.ScoringResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义测评类应用策略
 *
 * @author Jowen Yellow
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class CustomTestScoringStrategy implements ScoringStrategy {
    private final QuestionService questionService;
    private final ScoringResultService scoringResultService;

    @Override
    public UserAnswer scoring(List<String> choices, App app) {
        // 1.根据id查询题目和题目结果信息
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, app.getId())
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, app.getId())
        );

        // 2. 统计用户每个选择对应的属性个数，如I=10,E=5
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContentList = questionVO.getQuestionContent();

        Map<String, Integer> optionCount = new HashMap<>();

        for (QuestionContentDTO questionContentDTO : questionContentList) {
            for (String choice : choices) {
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    if (option.getKey().equals(choice)) {
                        String result = option.getResult();
                        optionCount.put(result, optionCount.getOrDefault(result, 0) + 1);
                    }
                }
            }
        }

        // 3. 遍历每种评分结果，计算哪个结果的得分更高
        ScoringResult maxScoringResult = scoringResultList.get(0);
        int maxScore = 0;

        for (ScoringResult scoringResult : scoringResultList) {
            ScoringResultVO scoringResultVO = ScoringResultVO.objToVo(scoringResult);
            List<String> resultProp = scoringResultVO.getResultProp();
            int score = resultProp.stream()
                    .mapToInt(prop -> optionCount.getOrDefault(prop, 0))
                    .sum();
            if (score > maxScore) {
                maxScore = score;
                maxScoringResult = scoringResult;
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

        return userAnswer;
    }

    @Override
    public AppTypeEnum getAppType() {
        return AppTypeEnum.TEST;
    }

    @Override
    public AppScoringStrategyEnum getScoringStrategy() {
        return AppScoringStrategyEnum.CUSTOM;
    }
}
