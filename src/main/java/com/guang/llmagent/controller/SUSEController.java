package com.guang.llmagent.controller;

import com.guang.llmagent.app.SUSEAgentApp;
import com.guang.llmagent.manus.SUSEManus;
import jakarta.annotation.Resource;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @Author L.
 * @Date 2025/12/9 18:24
 * @Description agent
 * @Version 1.0
 */
    @RestController
    @RequestMapping("/suseAgent")
    public class SUSEController {
    @Resource
    private SUSEManus suseManus;
    @Resource
    private SUSEAgentApp suseAgentApp;


    @Resource
    private ToolCallbackProvider toolCallbackProvider;
    @Resource
    private OllamaChatModel ollamaChatModel;

    @GetMapping(path = "/doChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChat(String message, String userId) {
        return suseAgentApp.doChatWithRagAndToolsByStream(message, userId);
    }

    @GetMapping("/doChat/sseEmitter")
    public SseEmitter doChatWithSseEmitter(String message, String userId) {
        SseEmitter sseEmitter = new SseEmitter(18000L);
        suseAgentApp.doChatByStream(message, userId)
                .subscribe(
                        chunk -> {
                            try {
                                sseEmitter.send(chunk);
                            } catch (IOException e) {
                                sseEmitter.completeWithError(e);
                            }
                        },
                        sseEmitter::completeWithError,
                        sseEmitter::complete
                );
        return sseEmitter;
    }

    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        SUSEManus suseManus = new SUSEManus(ollamaChatModel, toolCallbackProvider);
        return suseManus.runStream(message);
    }
}
