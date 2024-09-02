package com.atguigu.product.app;
import com.atguigu.common.utils.R;
import com.atguigu.product.service.ThreadTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
/**
 * @Author qiang.long
 * @Date 2024/04/17
 * @Description 线程池测试
 **/
@Slf4j
@RestController
public class ThreadTestController {
    @Resource
    private ThreadTestService threadTestService;
    @GetMapping("/product/combineSeveralTask")
    public R combineSeveralTask() throws ExecutionException, InterruptedException {
        return threadTestService.combineSeveralTask();
    }

    @GetMapping("/product/testThreadPool")
    public R testThreadPool(String s) throws ExecutionException, InterruptedException {
        return threadTestService.testThreadPool(s);
    }
}
