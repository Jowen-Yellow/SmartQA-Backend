package com.jowen.smartqa.manager;

import com.zhipu.oapi.ClientV4;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 通用AI能力
 *
 * @author Jowen Yellow
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class AiManager {
    private final ChatClient chatClient;

    /**
     * 通用请求
     *
     * @param systemMessage 系统预设信息
     * @param userMessage   用户发送信息
     * @return
     */
    public String doRequest(String systemMessage, String userMessage) {
        List<Message> messages = List.of(
                new SystemMessage(systemMessage),
                new UserMessage(userMessage)
        );
        return chatClient.prompt(new Prompt(messages)).call().content();
    }

    /**
     * 流式请求
     *
     * @param systemMessage 系统预设信息
     * @param userMessage   用户发送信息
     * @return
     */
    public Flux<String> doStreamRequest(String systemMessage, String userMessage) {
        List<Message> messages = List.of(
                new SystemMessage(systemMessage),
                new UserMessage(userMessage)
        );
        return chatClient.prompt(new Prompt(messages)).stream().content();
    }
}
