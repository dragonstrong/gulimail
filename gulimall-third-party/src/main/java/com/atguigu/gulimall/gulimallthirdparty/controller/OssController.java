package com.atguigu.gulimall.gulimallthirdparty.controller;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.gulimallthirdparty.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @Author qiang.long
 * @Date 2024/08/27
 * @Description 阿里云OSS文件存储接口
 **/
@RestController
public class OssController {
    @Autowired
    OssService ossService;
    @GetMapping("/oss/policy")
    public R policy(){
        return ossService.policy();
    }
}
