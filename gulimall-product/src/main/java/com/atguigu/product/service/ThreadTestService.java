package com.atguigu.product.service;
import com.atguigu.common.utils.R;

import java.util.concurrent.ExecutionException;
/**
 * @Author qiang.long
 * @Date 2024/04/17
 * @Description
 **/
public interface ThreadTestService {
    R combineSeveralTask() throws ExecutionException, InterruptedException;

    R testThreadPool(String s) throws ExecutionException, InterruptedException;

}
