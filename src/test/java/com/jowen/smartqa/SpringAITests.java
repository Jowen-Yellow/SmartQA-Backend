package com.jowen.smartqa;

import cn.hutool.core.thread.ThreadUtil;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

@SpringBootTest
public class SpringAITests {
    @Autowired
    private ChatClient chatClient;
    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = """
            你是一位严谨的判题专家，我会给你如下信息：
            ```
            应用名称，
            【【【应用描述】】】，
            题目和用户回答的列表：格式为 [{"title": "题目","answer": "用户回答"}]
            ```

            请你根据上述信息，按照以下步骤来对用户进行评价：
            1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）
            2. 严格按照下面的 json 格式输出评价名称和评价描述
            ```
            {"resultName": "评价名称", "resultDesc": "评价描述"}
            ```
            3. 返回格式必须为 JSON 对象""";
    private static final String USER_MESSAGE = """
            美食测试
            【【【看看你能不能猜对这些美食吧】】】
            [{"title":"以下哪种属于辣味美食？","answer":"炒面"},{"title":"哪种食物通常搭配番茄酱？","answer":"沙拉"},{"title":"夜市中常见的小吃是哪个？","answer":"蛋糕"},{"title":"哪一种食物适合早餐食用？","answer":"炸鸡"},{"title":"下列哪种食物是甜的？","answer":"烤鸭"},{"title":"哪种食物以酸味为主？","answer":"水果沙拉"},{"title":"西餐厅常见的食物是哪个？","answer":"水煮鱼"},{"title":"以下哪种属于街头小吃？","answer":"章鱼小丸子"},{"title":"哪一种食物常用于庆祝生日？","answer":"馄饨"},{"title":"早晨提神饮料通常是哪个？","answer":"橙汁"}]""";


    @Test
    public void openaiTest() {
        List<Message> messages = List.of(
                new SystemMessage(AI_TEST_SCORING_SYSTEM_MESSAGE),
                new UserMessage(USER_MESSAGE)
        );
        Flux<String> content = chatClient
                .prompt(new Prompt(messages))
                .stream()
                .content();

        content.subscribe(System.out::println);

        ThreadUtil.sleep(500000);
    }
}
