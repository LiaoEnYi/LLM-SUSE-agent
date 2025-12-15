package com.guang.llmagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * @Author L.
 * @Date 2025/12/1 12:37
 * @Description 使用内存中的vectorStore
 * @Version 1.0
 */
@Configuration
public class SUSEIssueRAGAdvisorConfig {
    @Resource
    private MyMarkdownReader myMarkdownReader;

    @Resource
    private VectorStore redisVectorStore;

    @Bean
    public VectorStore inRedisVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        List<Document> documents = myMarkdownReader.loadMarkdown();
        if (!documents.isEmpty()) {
            // 文档太大需要分批插入
            int partitionStep = Math.ceilDiv(documents.size(), 10);
            for (int i = 0; i < partitionStep; i++) {
                List<Document> res = documents.stream().skip(i * 10L).limit(10).toList();
                redisVectorStore.add(res);
            }
        }
        return redisVectorStore;
    }

}
