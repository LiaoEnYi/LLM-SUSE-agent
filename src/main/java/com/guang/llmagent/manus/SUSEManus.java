package com.guang.llmagent.manus;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Author L.
 * @Date 2025/12/9 17:00
 * @Description SUSE超级智能体
 * @Version 1.0
 */
@Component
public class SUSEManus extends ToolCallAgent {

    public SUSEManus(ChatModel ollamaChatModel, ToolCallbackProvider toolCallbackProvider) {
        super(toolCallbackProvider.getToolCallbacks());
        ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
        String SYSTEM_PROMPT = """  
                You are SUSEManus, an all-capable AI assistant, aimed at solving any task presented by the user.  
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
                """;
        String NEXT_STEP_PROMPT = """  
                Based on user needs, proactively select the most appropriate tool or combination of tools.  
                For complex tasks, you can break down the problem and use different tools step by step to solve it.  
                After using each tool, clearly explain the execution results and suggest the next steps.  
                If you want to stop the interaction at any point, use the `terminated` tool/function call.  
                """;
        setSystemPrompt(SYSTEM_PROMPT);
        setNextStepPrompt(NEXT_STEP_PROMPT);
        setName("SUSEManus");
        setMaxStep(10);

        ChatClient chatClient = ChatClient.builder(ollamaChatModel)
                .build();
        setChatClient(chatClient);
    }
}
