package com.guang.llmagent.manus.model;

/**
 * @Author L.
 * @Date 2025/12/9 15:34
 * @Description 执行状态
 * @Version 1.0
 */
public enum AgentState {
    /**
     * 空闲状态
     */
    IDLE,
    /**
     * 执行中
     */
    RUNNING,
    /**
     * 完成
     */
    FINISHED,
    /**
     * 错误
     */
    ERROR
}
