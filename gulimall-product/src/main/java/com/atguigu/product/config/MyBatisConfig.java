package com.atguigu.product.config;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/**
 * @Author qiang.long
 * @Date 2024/08/28
 * @Description  MyBatis配置：开启事务 分页
 **/
@Configuration
@EnableTransactionManagement
@MapperScan("com.atguigu.product.dao")
public class MyBatisConfig {

    /**
     * 引入分页插件
     **/
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor(){
        PaginationInnerInterceptor paginationInnerInterceptor=new PaginationInnerInterceptor();
        // 设置请求页面大于最大页后操作，true调回到首页，false继续请求，默认false
        paginationInnerInterceptor.setOverflow(true);
        //设置最大单页限制数量，默认500条，-1不受限制
        paginationInnerInterceptor.setMaxLimit((long)100);
        return paginationInnerInterceptor;
    }
}
