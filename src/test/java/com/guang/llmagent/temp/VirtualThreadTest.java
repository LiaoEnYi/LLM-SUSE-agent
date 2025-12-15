package com.guang.llmagent.temp;

import org.junit.jupiter.api.Test;

/**
 * @Author L.
 * @Date 2025/12/8 18:15
 * @Description TODO
 * @Version 1.0
 */
public class VirtualThreadTest {
    @Test
    public void test() {
        Thread.ofVirtual().start(() -> {
            System.out.println("hello world");
        });
    }
}
