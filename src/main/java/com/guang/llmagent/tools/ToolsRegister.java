package com.guang.llmagent.tools;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author L.
 * @Date 2025/12/7 16:45
 * @Description 工厂模式，统一注册所有工具
 * @Version 1.0
 */
@Configuration
public class ToolsRegister {
    @Value("${api-keys.search-api}")
    private String apiKey;

    @Bean
    public ToolCallback[] tools() {
        WebSearchTool webSearchTool = new WebSearchTool(apiKey);
        FileOperationTool fileOperationTool = new FileOperationTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        TerminateTool terminateTool = new TerminateTool();
        return ToolCallbacks.from(
                webSearchTool,
                fileOperationTool,
                resourceDownloadTool,
                webScrapingTool,
                terminalOperationTool,
                pdfGenerationTool,
                terminateTool
        );
    }
}
