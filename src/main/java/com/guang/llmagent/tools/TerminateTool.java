package com.guang.llmagent.tools;

import org.springframework.ai.tool.annotation.Tool;

/**
 * @Author L.
 * @Date 2025/12/9 16:46
 * @Description 告诉大模型可以终止操作
 * @Version 1.0
 */
public class TerminateTool {
    @Tool(description = """  
            Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.  
            "When you have finished all the tasks, call this tool to end the work.  
            """)
    public String terminated() {
        return "任务结束";
    }
}
