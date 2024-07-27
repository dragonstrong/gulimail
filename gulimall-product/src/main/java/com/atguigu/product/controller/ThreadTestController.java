package com.atguigu.product.controller;
import com.atguigu.product.service.ThreadTestService;
import lombok.extern.slf4j.Slf4j;
import com.atguigu.common.utils.R;
import org.springframework.web.bind.annotation.*;

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
