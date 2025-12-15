package com.guang.llmagent;

import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class LlmAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlmAgentApplication.class, args);
    }

}
