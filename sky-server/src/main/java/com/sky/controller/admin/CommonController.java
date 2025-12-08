package com.sky.controller.admin;

import cn.hutool.core.lang.UUID;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {
    @PostMapping("/upload")
    public Result upload(MultipartFile file){
        try {
            String fileName=file.getOriginalFilename();
            String suffix=fileName.substring(fileName.lastIndexOf("."));
            String randomFileName= UUID.randomUUID()+suffix;
            String url= AliOssUtil.upload(randomFileName,file.getInputStream());
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
