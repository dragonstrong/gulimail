package com.atguigu.gulimall.gulimallthirdparty;
import com.aliyun.oss.OSS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
@SpringBootTest
class GulimallThirdPartyApplicationTests {
    @Autowired
    OSS oss;
    @Value("${alibaba.oss.bucket}")
    private String bucket;
    /**
     * @description: OSS存储文件上传
     **/
    @Test
    public void upLoadOSS() throws FileNotFoundException {
        /*
        // Endpoint以上海为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-shanghai.aliyuncs.com";
        // 运行本代码示例之前，请确保已设置OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET
        String accessKeyId="LTAI5tFTTUWqBbq8F9zzvhVb";
        String accessKeySecret="9EvG2ZhCor8ZutCWrccFzJLDtaxDtU";
        // 创建OSSClient实例
        OSS ossClient=new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        */

        // 上传文件流
        InputStream inputStream = new FileInputStream("C:\\Users\\dragon\\Desktop\\1.jpg");
        // 要上传的文件名
        String objectName = "5.jpg";
        oss.putObject(bucket,objectName,inputStream);
        // 关闭OSSClient
        oss.shutdown();
    }

}
