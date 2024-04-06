package com.atguigu.product.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
/**
 * @Author qiang.long
 * @Date 2024/04/05
 * @Description 节点详情
 **/
@Data
@TableName("node_info")
public class NodeInfo implements Serializable {
    @TableId
    private String nodeIp;
    private String gpuType;
    private String tpuType;
}
