package com.guang.llmagent.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static com.guang.llmagent.constant.FilePath.FILE_DIR_PATH;

/**
 * @Author L.
 * @Date 2025/11/21 16:07
 * @Description 文件操作工具
 * @Version 1.0
 */
public class FileOperationTool {
    private final String FILE_PATH = FILE_DIR_PATH + "/file";

    @Tool(description = "Write the file locally")
    public String WriteFile(@ToolParam(description = "Write to the local file name") String fileName,
                            @ToolParam(description = "The content to be written") String content) {
        String path = FILE_PATH + "/" + fileName;
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        try (
                FileWriter fw = new FileWriter(path, StandardCharsets.UTF_8, true);
                BufferedWriter bw = new BufferedWriter(fw);
        ) {
            bw.write(content);
        } catch (Exception e) {
            return "Write failed" + e.getMessage();
        }
        return "write successful";
    }

    @Tool(description = "Read the file locally")
    public String ReadFile(@ToolParam(description = "The file name to be read") String fileName) {
        String path = FILE_PATH + "/" + fileName;
        StringBuilder sb = new StringBuilder();
        try (
                FileReader fr = new FileReader(path, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(fr);
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            return "Read failed" + e.getMessage();
        }
        return sb.toString();
    }
}
