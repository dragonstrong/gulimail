package com.atguigu.ware.vo;
import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;
/**
 * @Author qiang.long
 * @Date 2024/09/01
 * @Description 采购项
 **/
@Data
public class PurchaseItemFinishVo {
    /**
     * 采购项id
     **/
    private Long itemId;
    /**
     * 状态：是否完成
     **/
    private Integer status;
    /**
     * 未完成原因
     **/
    private String reason;

}
