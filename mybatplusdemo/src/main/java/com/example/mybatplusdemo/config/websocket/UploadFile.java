package com.example.mybatplusdemo.config.websocket;


import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.WebResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class UploadFile {

    //远程文件服务器地址
    private static final String FILE_URL="http://xxxxxxxx";



    public static Map<String,Object> doRemoteUpload(MultipartFile File,String fileType){
//        Map<String,Object> map = new HashMap<>();
//        //文件服务器url
//        String path = FILE_URL;
//        //为上传到服务器的文件取名，使用UUID防止文件名重复
//        String type= Objects.requireNonNull(File.getOriginalFilename()).substring(File.getOriginalFilename().lastIndexOf("."));
//        String fileNicKName= UUID.randomUUID() +type;
//        String fileName = File.getOriginalFilename();
//        String fileUrl = path + fileType + fileNicKName;
//        try{
//            //使用Jersey客户端上传文件
//            Client client = Client.create();
//            WebResource webResource = client.resource(path + fileType + URLEncoder.encode(fileNicKName, StandardCharsets.UTF_8));
//            webResource.put(File.getBytes());
//            map.put("status",true);
//            map.put("fileName",fileName);
//            map.put("fileUrl",fileUrl);
//            log.info("文件名：{}  =======> 文件上传路径:  {}",fileName,fileUrl);
//        }catch(Exception e){
//            e.printStackTrace();
//            map.put("status",false);
//            map.put("Msg","上传失败！");
//        }
//        return map;
        return null;
    }

}