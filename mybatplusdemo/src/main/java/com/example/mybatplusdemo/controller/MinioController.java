package com.example.mybatplusdemo.controller;

import com.example.mybatplusdemo.minio.MinioConfig;
import com.example.mybatplusdemo.minio.MinioUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @description: minio控制器
 **/
@Api(tags = {"文件上传、下载、预览接口"})
@RequestMapping("/minio")
@RestController
@Slf4j
public class MinioController {

    @Autowired
    private MinioUtil minioUtils;

    @Autowired
    private MinioConfig minioConfig;

    /**
     * 文件上传
     *
     * @param file
     */
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            //文件名
            String fileName = file.getOriginalFilename();
            String newFileName = System.currentTimeMillis() + "." + StringUtils.substringAfterLast(fileName, ".");
            //类型
            String contentType = file.getContentType();
            // 存储的文件
            String objectName = new SimpleDateFormat("yyyy/MM/dd/").format(new Date()) + UUID.randomUUID().toString().replaceAll("-", "")
                    + fileName.substring(Objects.requireNonNull(fileName).lastIndexOf("."));

            log.info("newFileName:{}",newFileName);
            minioUtils.uploadFile(minioConfig.getBucketName(), file, objectName, contentType);

            return objectName;
        } catch (Exception e) {
            log.error("上传失败",e.getMessage());
            return "上传失败";
        }
    }

    /**
     * 删除
     *
     * @param fileName
     */
    @ApiOperation("删除文件")
    @DeleteMapping("/")
    public void delete(@RequestParam("fileName") String fileName) {
        minioUtils.removeFile(minioConfig.getBucketName(), fileName);
    }

    /**
     * 文件下载
     *
     * @param fileName
     * @param response
     */
    @ApiOperation("文件下载")
    @GetMapping("/download")
    public void download(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        try {
            InputStream fileInputStream = minioUtils.getObject(minioConfig.getBucketName(), fileName);
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/force-download");
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(fileInputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载失败");
        }
    }

    /**
     * 预览文件
     * @param fileName
     * @return
     */
//    @ApiOperation("预览文件")
//    @GetMapping("/preview")
//    public Result preview(String fileName) {
//        String result = minioUtils.getPreviewUrl(fileName);
//        log.info("预览：{}",result);
//        return success(result);
//    }

}

