package com.atguigu.product.dto.rabbit;
import com.atguigu.product.valid.AddGroup;
import com.atguigu.product.valid.ListValue;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
/**
 * @Author qiang.long
 * @Date 2024/07/18
 * @Description 创建交换器请求体
 **/
@Data
public class ExchangeDTO {
    /**
     * 交换机类型
     **/
    @ListValue(vals={1,2,3},message = "指定的交换器类型错误")
    private int type;
    /**
     * 交换机名字
     **/
    private String name;
    /**
     * 是否持久化
     **/
    private boolean durable;
    /**
     * 是否自动删除
     **/
    private boolean autoDelete;

}
