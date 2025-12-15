package com.guang.llmagent.manus;

import cn.hutool.core.collection.CollUtil;
import com.guang.llmagent.manus.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author L.
 * @Date 2025/12/9 16:01
 * @Description 工具调用超级智能体
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    private final ToolCallback[] tools;

    private ChatResponse chatResponse;

    private final ToolCallingManager toolCallingManager;
    /**
     * 用于手动接管Spring AI 的工具调用
     */
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] tools) {
        super();
        this.tools = tools;
        toolCallingManager = ToolCallingManager.builder()
                .build();
        this.chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(tools)
                .internalToolExecutionEnabled(false)
                .build();
    }

    @Override
    public boolean think() {
        if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessages().add(userMessage);
        }
        List<Message> messages = getMessages();
        Prompt prompt = new Prompt(messages, chatOptions);
        // 调用大模型
        try {
            this.chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(tools)
                    .call()
                    .chatResponse();
            assert chatResponse != null;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            String res = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            log.info(getName() + "的思考结果：" + res);
            log.info(getName() + "选择了 " + toolCallList.size() + " 个工具使用");
            String toolCallInfo = toolCallList.stream()
                    .map(tool -> String.format("使用工具: %s，参数: %s", tool.name(), tool.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            if (toolCallList.isEmpty()) {
                getMessages().add(assistantMessage);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error(getName() + " 思考过程中遇到问题：", e.getMessage());
            getMessages().add(new AssistantMessage("处理时遇到了问题：" + e.getMessage()));
            return false;
        }
    }

    @Override
    public String act() {
        if (!chatResponse.hasToolCalls()) {
            return "没有工具调用";
        }
        Prompt prompt = new Prompt(getMessages(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);
        setMessages(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        String res = toolResponseMessage.getResponses().stream()
                .map(v -> "工具：" + v.name() + "，结果：" + v.responseData())
                .collect(Collectors.joining("\n"));
        boolean terminated = toolResponseMessage.getResponses().stream()
                .anyMatch(v -> "terminated".equals(v.name()));
        if (terminated) {
            setState(AgentState.FINISHED);
        }
        log.info(res);
        return res;
    }
}
