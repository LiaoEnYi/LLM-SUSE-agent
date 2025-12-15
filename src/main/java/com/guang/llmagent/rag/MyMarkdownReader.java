package com.guang.llmagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.guang.llmagent.constant.RedisVectorPrefix.EMBEDDING_PREFIX;

/**
 * @Author L.
 * @Date 2025/11/30 21:15
 * @Description markdown 内容获取器
 * @Version 1.0
 */
@Component
@Slf4j
public class MyMarkdownReader {
    private final ResourcePatternResolver resourcePatternResolver;
    private final StringRedisTemplate stringRedisTemplate;

    public MyMarkdownReader(ResourcePatternResolver resourcePatternResolver, StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    List<Document> loadMarkdown() {
        // 如果向量数据库中已经有文件了就无需再次添加了
        if (!stringRedisTemplate.keys(EMBEDDING_PREFIX + "*").isEmpty()) {
            return List.of();
        }
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                String[] split = filename.split("-");
                String status = split[1].substring(0, split[1].length() - 3);
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        .withAdditionalMetadata("status", status)
                        .build();
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(markdownDocumentReader.get());
            }
        } catch (IOException e) {
            log.error("Markdown 加载失败");
        }
        return allDocuments;
    }
}
