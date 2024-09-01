package com.atguigu.ware.vo;
import lombok.Data;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/08/31
 * @Description 合并采购单请求
 **/
@Data
public class MergePurchaseVo {
    /**
     * 采购单id
     **/
    private Long purchaseId;
    /**
     * 合并项集合  wms_purchase_detail表的id
     **/
    private List<Long> items;
}
