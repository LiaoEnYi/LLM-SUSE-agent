package com.guang.llmagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author L.
 * @Date 2025/11/26 19:02
 * @Description TODO
 * @Version 1.0
 */
@SpringBootTest
class SUSEAgentAppTest {
    @Resource
    SUSEAgentApp app;

    @Test
    void doChat() {
        String res = app.doChat("你是谁，我叫光头强", "1");
        Assertions.assertNotNull(res, "res should not be null");
        String res2 = app.doChat("我是谁？", "1");
        Assertions.assertNotNull(res2, "res should not be null");
    }

    @Test
    void doChatWithRag() {
        String res = app.doChatWithRag("你是谁，我叫光头强", "1");
        Assertions.assertNotNull(res, "res should not be null");
        String res2 = app.doChatWithRag("大一新生在哪里读？", "1");
        Assertions.assertNotNull(res2, "res should not be null");
    }

    @Test
    void doChatWithTools() {
        String res = app.doChatWithTools("你是谁，我叫光头强", "1");
        Assertions.assertNotNull(res, "res should not be null");
        String res2 = app.doChatWithTools("轻化工宜宾校区附近有什么好玩的推荐", "1");
        Assertions.assertNotNull(res2, "res should not be null");
    }

    @Test
    void doChatWithMCP() {
        String res = app.doChatWithMCP("你是谁，我叫光头强", "1");
        Assertions.assertNotNull(res, "res should not be null");
        String res2 = app.doChatWithMCP("轻化工宜宾校区附近有什么好玩的推荐", "1");
        Assertions.assertNotNull(res2, "res should not be null");
    }
}