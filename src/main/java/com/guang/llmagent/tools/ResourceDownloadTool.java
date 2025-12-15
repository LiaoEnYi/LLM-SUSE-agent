package com.guang.llmagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.guang.llmagent.constant.FilePath;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

/**
 * @Author L.
 * @Date 2025/11/23 12:09
 * @Description TODO
 * @Version 1.0
 */
public class ResourceDownloadTool {
    @Tool(description = "Download a resource from a given URL")
    public String resourceDownload(@ToolParam(description = "URL of the resource to download") String url,
                                   @ToolParam(description = "Name of the file to save the downloaded resource") String fileName) {
        String fileDir = FilePath.FILE_DIR_PATH + "/download";
        String path = fileDir + "/" + fileDir;
        try {
            FileUtil.mkdir(fileDir);
            HttpUtil.downloadFile(url, new File(path));
            return "Resource downloaded successfully to: " + path;
        } catch (Exception e) {
            return "Error downloading resource: " + e.getMessage();
        }
    }
}
