package com.guang.llmagent.app;

import com.guang.llmagent.rag.ReWriteQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

/**
 * @Author L.
 * @Date 2025/11/26 18:22
 * @Description 支持会话记忆、RAG、ToolCalling、MCP的AI 应用
 * @Version 1.0
 */
@Slf4j
@Component
public class SUSEAgentApp {
    private final ChatClient chatClient;
    /**
     * 最大的历史会话消息
     */
    private static int MAX_MESSAGES = 10;
    /**
     * redis 向量数据库支持
     */
    @jakarta.annotation.Resource
    private VectorStore inRedisVectorStore;
    /**
     * 自定义本地工具
     */
    @jakarta.annotation.Resource
    private ToolCallback[] tools;
    /**
     * MCP
     */
    @jakarta.annotation.Resource
    private ToolCallbackProvider toolCallbackProvider;
    /**
     * 查询重写器
     */
    @jakarta.annotation.Resource
    private ReWriteQuery reWriteQuery;

    public SUSEAgentApp(ChatModel deepSeekChatModel,
                        @Value("classpath:/prompts/systemPrompt.st") Resource systemResource) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(MAX_MESSAGES)
                .build();
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
        String system = systemPromptTemplate.render();
        this.chatClient = ChatClient.builder(deepSeekChatModel)
                .defaultSystem(system)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    /**
     * 使用chatClient进行简单的会话 会话历史保存到本地内存中
     * @param message 用户消息
     * @param chatId conversation id
     * @return 大模型返回的内容
     */
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        Assert.notNull(response, "Response must not be null");
        String text = response.getResult().getOutput().getText();
        log.info("SUSEAgentApp: {}", text);
        return text;
    }

    /**
     * 使用chatClient进行简单的会话 会话历史保存到本地内存中
     * @param message 用户消息
     * @param chatId conversation id
     * @return 大模型返回的内容
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    /**
     *  使用RAG增强大模型的回答
     * @param message 用户消息
     * @param chatId conversation id
     * @return 大模型返回的内容
     */
    public String doChatWithRag(String message, String chatId) {
        // 使用查询重写器重写用户的提问
        message = reWriteQuery.rewrite(message);
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                // 使用redis作为向量存储
                .advisors(QuestionAnswerAdvisor.builder(inRedisVectorStore)
                        .searchRequest(SearchRequest.builder().similarityThreshold(0.7d).topK(3).build())
                        .build())
                .call()
                .chatResponse();
        Assert.notNull(response, "Response must not be null");
        String text = response.getResult().getOutput().getText();
        log.info("SUSEAgentApp: {}", text);
        return text;
    }

    /**
     * 使用工具增强大模型的回答
     * @param message 用户消息
     * @param chatId conversation id
     * @return 大模型返回的内容
     */
    public String doChatWithTools(String message, String chatId) {
        // 使用查询重写器重写用户的提问
        message = reWriteQuery.rewrite(message);
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                // 使用redis作为向量存储
                .advisors(QuestionAnswerAdvisor.builder(inRedisVectorStore)
                        .searchRequest(SearchRequest.builder().similarityThreshold(0.7d).topK(3).build())
                        .build())
                // 工具支持
                .toolCallbacks(tools)
                .call()
                .chatResponse();
        Assert.notNull(response, "Response must not be null");
        String text = response.getResult().getOutput().getText();
        log.info("SUSEAgentApp: {}", text);
        return text;
    }

    /**
     * 使用MCP增强大模型的回答
     * @param message 用户消息
     * @param chatId conversation id
     * @return 大模型返回的内容
     */
    public String doChatWithMCP(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        assert response != null;
        String res = response.getResult().getOutput().getText();
        Assert.notNull(res, "Response must not be null");
        log.info("SUSEAgentApp: {}", res);
        return res;
    }

    /**
     * 使用RAG和工具增强大模型的回答
     * @param message 用户消息
     * @param userId 消息ID
     * @return 流式返回结果
     */
    public Flux<String> doChatWithRagAndToolsByStream(String message, String userId) {
        message = reWriteQuery.rewrite(message);
        return chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, userId))
                .advisors(QuestionAnswerAdvisor.builder(inRedisVectorStore)
                        .searchRequest(SearchRequest.builder().similarityThreshold(0.7d).topK(3).build())
                        .build())
                .toolCallbacks(toolCallbackProvider)
                .stream()
                .content();
    }
}
