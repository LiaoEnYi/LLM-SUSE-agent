package com.guang.llmagent.manus;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @Author L.
 * @Date 2025/12/9 17:50
 * @Description TODO
 * @Version 1.0
 */
@SpringBootTest
class SUSEManusTest {
    @Autowired
    private SUSEManus suseManus;

    @Test
    void run() {
        String userPrompt = """  
                我现在在成都，我想去四川轻化工大学宜宾校区，请为我规划路线，  
                并结合一些学校的网络图片，制定一份详细的出行计划，  
                并以 PDF 格式输出""";
        String answer = suseManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}