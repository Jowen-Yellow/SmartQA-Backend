package com.jowen.smartqa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jowen.smartqa.model.dto.statistics.AppAnswerCountDTO;
import com.jowen.smartqa.model.dto.statistics.AppAnswerResultCountDTO;
import com.jowen.smartqa.model.entity.UserAnswer;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author JoelLaptop
 * @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
 * @createDate 2024-05-25 19:25:45
 * @Entity com.jowen.smartqa.model.entity.UserAnswer
 */
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {

    @Select("""
            select appId, count(*) as answerCount
            from user_answer
            where isDelete != 1
            group by appId
            order by answerCount desc
            limit 5
            """)
    List<AppAnswerCountDTO> getAppAnswerCount();

    @Select("""
            select resultName, count(resultName) as resultCount
            from user_answer
            where appId=#{appId} and isDelete != 1
            group by resultName
            order by resultCount desc
            """)
    List<AppAnswerResultCountDTO> getAnswerResultCount(Long appId);
}




