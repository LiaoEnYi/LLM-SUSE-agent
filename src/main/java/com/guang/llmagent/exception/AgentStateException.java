package com.guang.llmagent.exception;

/**
 * @Author L.
 * @Date 2025/12/9 15:40
 * @Description manus 执行状态异常
 * @Version 1.0
 */
public class AgentStateException extends RuntimeException {

    public AgentStateException(String message) {
        super(message);
    }
}
