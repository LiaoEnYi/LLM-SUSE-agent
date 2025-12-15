package com.guang.llmagent.manus;

import com.guang.llmagent.exception.AgentStateException;
import com.guang.llmagent.manus.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Author L.
 * @Date 2025/12/9 15:32
 * @Description 基础类
 * @Version 1.0
 */
@Data
@Slf4j
public abstract class BaseAgent {
    private String name;

    private String systemPrompt;
    private String nextStepPrompt;

    private AgentState state = AgentState.IDLE;

    private int maxStep = 10;
    private int currentStep = 0;

    private ChatClient chatClient;
    /**
     * 手动维护消息记录
     */
    private List<Message> messages = new ArrayList<>();

    public String run(String userPrompt) {
        if (this.state != AgentState.IDLE) {
            throw new AgentStateException("this state not allow to run");
        }
        if (userPrompt.isBlank()) {
            throw new AgentStateException("user prompt can not be blank");
        }
        this.state = AgentState.RUNNING;
        messages.add(new UserMessage(userPrompt));
        List<String> res = new ArrayList<>();
        try {
            for (int i = 0; i < maxStep && this.state != AgentState.FINISHED; i++) {
                // 执行步骤
                this.currentStep = i + 1;
                log.info("Executing step" + this.currentStep + "/" + this.maxStep);
                String stepRes = step();
                String result = "Step " + this.currentStep + ": " + stepRes;
                res.add(result);
            }
            if (this.currentStep >= this.maxStep) {
                this.state = AgentState.FINISHED;
                res.add("Terminated step: reached max step (" + this.maxStep + ")");
            }
            return String.join(",", res);
        } catch (Exception e) {
            this.state = AgentState.ERROR;
            log.error("Execute Error : " + e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 具体的执行步骤延迟给子类去实现
     * @return LLM返回结果
     */
    public abstract String step();

    public SseEmitter runStream(String userPrompt) {
        SseEmitter emitter = new SseEmitter(18000L);
        CompletableFuture.runAsync(() -> {
            try {
                if (this.state != AgentState.IDLE) {
                    emitter.send("error: 无法从这个状态代理 - " + this.state);
                    emitter.complete();
                    return;
                }
                if (userPrompt.isBlank()) {
                    emitter.send("error: 用户提示词不能为空");
                    emitter.complete();
                    return;
                }
                this.state = AgentState.RUNNING;
                messages.add(new UserMessage(userPrompt));
                try {
                    for (int i = 0; i < maxStep && state != AgentState.FINISHED; i++) {
                        int stepNumber = i + 1;
                        currentStep = stepNumber;
                        log.info("Executing step " + stepNumber + "/" + maxStep);
                        String stepResult = step();
                        String result = "Step " + stepNumber + ": " + stepResult;
                        emitter.send(result);
                    }
                    if (currentStep >= maxStep) {
                        this.state = AgentState.FINISHED;
                        emitter.send("Terminated step: reached max step (" + maxStep + ")");
                    }
                    emitter.complete();
                } catch (Exception e) {
                    this.state = AgentState.ERROR;
                    log.error("执行智能体失败: " + e);
                    try {
                        emitter.send("error: " + e.getMessage());
                        emitter.complete();
                    } catch (IOException ex) {
                        emitter.completeWithError(e);
                    }
                }

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        emitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            log.warn("connection time out");
        });
        emitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            log.info("connection complete");
        });
        return emitter;
    }
}
