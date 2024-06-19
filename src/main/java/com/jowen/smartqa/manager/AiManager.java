package com.jowen.smartqa.manager;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private final ClientV4 clientV4;
    private final Float STABLE_TEMPERATURE = 0.7f;
    private final Float UNSTABLE_TEMPERATURE = 0.9f;

    /**
     * 通用请求
     *
     * @param systemMessage 系统预设信息
     * @param userMessage   用户发送信息
     * @param stream        是否流式输出
     * @param temperature   随机性
     * @return
     */
    public String doRequest(String systemMessage, String userMessage, Boolean stream, Float temperature) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        messages.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(userChatMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .temperature(temperature)
                .build();
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        return invokeModelApiResp.getData().getChoices().get(0).toString();
    }

    /**
     * 同步请求
     *
     * @param systemMessage 系统预设信息
     * @param userMessage   用户发送信息
     * @param temperature   随机性
     * @return
     */
    public String doSyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, temperature);
    }

    /**
     * 异步请求
     *
     * @param systemMessage 系统预设信息
     * @param userMessage   用户发送信息
     * @param temperature   随机性
     * @return
     */
    public String doAsyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, Boolean.TRUE, temperature);
    }

    /**
     * 稳定请求
     *
     * @param systemMessage 系统预设信息
     * @param userMessage   用户发送信息
     * @return
     */
    public String doSyncStableRequest(String systemMessage, String userMessage) {
        return doSyncRequest(systemMessage, userMessage, STABLE_TEMPERATURE);
    }

    /**
     * 不稳定请求
     *
     * @param systemMessage 系统预设信息
     * @param userMessage   用户发送信息
     * @return
     */
    public String doSyncUnstableRequest(String systemMessage, String userMessage) {
        return doSyncRequest(systemMessage, userMessage, UNSTABLE_TEMPERATURE);
    }

    /**
     * 流式请求
     *
     * @param systemMessage 系统预设信息
     * @param userMessage   用户发送信息
     * @param temperature   随机性
     * @return
     */
    public Flowable<ModelData> doStreamRequest(String systemMessage, String userMessage, Float temperature) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        messages.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(userChatMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .temperature(temperature)
                .build();
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        return invokeModelApiResp.getFlowable();
    }
}
