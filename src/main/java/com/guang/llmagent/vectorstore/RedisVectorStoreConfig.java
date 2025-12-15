package com.guang.llmagent.vectorstore;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

import static com.guang.llmagent.constant.RedisVectorPrefix.EMBEDDING_PREFIX;

/**
 * @Author L.
 * @Date 2025/12/7 14:54
 * @Description 手动配置redis向量数据库
 * @Version 1.0
 */
@Configuration
public class RedisVectorStoreConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled(host, port);
    }

    @Bean
    public VectorStore redisVectorStore(JedisPooled jedisPooled, EmbeddingModel dashscopeEmbeddingModel) {
        return RedisVectorStore.builder(jedisPooled, dashscopeEmbeddingModel)
                .indexName("suse-index")                // Optional: defaults to "spring-ai-index"
                .prefix(EMBEDDING_PREFIX)                  // Optional: defaults to "embedding:"
                .initializeSchema(true)                   // Optional: defaults to false
                .batchingStrategy(new TokenCountBatchingStrategy()) // Optional: defaults to TokenCountBatchingStrategy
                .build();
    }
}
