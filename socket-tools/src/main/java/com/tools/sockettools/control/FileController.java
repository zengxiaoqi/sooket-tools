package com.tools.sockettools.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件上传测试类
 *
 */
@Slf4j
@RestController
public class FileController {
    /**
     * 上传文件
     * @param file  上传的文件
     * treePath = theme + "-" + fileName
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public String upload(@RequestParam("file") MultipartFile file) throws Exception{
        String fileName = file.getOriginalFilename();
        log.info(fileName);
        File remoteFile =  new File("C:\\Users\\Administrator\\Downloads\\"  + fileName);
        if (!remoteFile.getParentFile().exists()) {
            log.info("文件不存在，创建目录");
            remoteFile.getParentFile().mkdirs();
        }
        if(!remoteFile.exists()){
            remoteFile.createNewFile();
            log.info("文件不存在，创建文件");
        }
        file.transferTo(remoteFile);

        log.info("文件上传成功...");
        return "ok";
    }
}
