package com.guang.llmagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @Author L.
 * @Date 2025/11/23 11:24
 * @Description TODO
 * @Version 1.0
 */
public class WebScrapingTool {
    @Tool(description = "Crawl a web page")
    public String webScraping(@ToolParam(description = "The web page address to be crawled") String URL) {
        try {
            Document doc = Jsoup.connect(URL).get();
            return doc.html();
        } catch (Exception e) {
            return "Failed to crawl the web page" + e.getMessage();
        }


    }
}
