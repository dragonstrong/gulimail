package com.atguigu.ware.vo;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/01
 * @Description 完成采购单请求体
 **/
@Data
public class PurchaseFinishVo {
    /**
     * 采购单id
     **/
    @NotNull
    private  Long id;
    /**
     * 采购项列表
     **/
    private List<PurchaseItemFinishVo> items;

}
