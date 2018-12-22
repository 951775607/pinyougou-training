package com.pinyougou.shop.controller;

import com.pinyougou.common.util.FastDFSClient;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/upload")
@RestController
public class PicUploadController {

    /**
     * 图片上传到fastdfs
     * @param file 图片文件
     * @return 操作结果
     */
    @PostMapping
    public Result upload(MultipartFile file){
        Result result = Result.fail("上传图片失败");
        try {
            //上传
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastdfs/tracker.conf");

            //文件的扩展名
            String file_ext_name = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            String url = fastDFSClient.uploadFile(file.getBytes(), file_ext_name);

            result = Result.ok(url);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //返回结果
        return result;
    }
}
