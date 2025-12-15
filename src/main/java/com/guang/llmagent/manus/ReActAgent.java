package com.guang.llmagent.manus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author L.
 * @Date 2025/12/9 15:57
 * @Description TODO
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ReActAgent extends BaseAgent {
    public abstract boolean think();

    public abstract String act();

    @Override
    public String step() {
        try {
            boolean think = think();
            if (!think) {
                return "思考完成 无需行动";
            }
            return act();
        } catch (Exception e) {
            return "执行步骤失败：" + e.getMessage();
        }
    }
}
