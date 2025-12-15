package com.guang.llmagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * @Author L.
 * @Date 2025/11/27 18:16
 * @Description 用户提示词重写器
 * @Version 1.0
 */
@Component
public class ReWriteQuery {
    private final RewriteQueryTransformer rewriteQueryTransformer;

    public ReWriteQuery(ChatModel ollamaChatModel) {
        ChatClient.Builder builder = ChatClient.builder(ollamaChatModel);
        this.rewriteQueryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
    }

    public String rewrite(String message) {
        Query query = new Query(message);
        return rewriteQueryTransformer.transform(query).text();
    }
}
