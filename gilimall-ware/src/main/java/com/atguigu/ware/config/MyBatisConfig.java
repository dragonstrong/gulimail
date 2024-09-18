package com.atguigu.ware.config;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
/**
 * @Author qiang.long
 * @Date 2024/09/01
 * @Description 数据库配置
 **/
@EnableTransactionManagement
@MapperScan("com.atguigu.ware.dao")
@Configuration
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

    @Autowired
    DataSourceProperties dataSourceProperties;
    /**
     * seata分布式事务数据源配置
     **/
    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties){
        HikariDataSource dataSource=dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        if(StringUtils.hasText(dataSourceProperties.getName())){
            dataSource.setPoolName(dataSourceProperties.getName());
        }
        return new DataSourceProxy(dataSource);
    }
}

