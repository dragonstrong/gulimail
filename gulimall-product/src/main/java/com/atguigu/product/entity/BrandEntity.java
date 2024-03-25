package com.atguigu.product.entity;
import com.atguigu.product.valid.AddGroup;
import com.atguigu.product.valid.ListValue;
import com.atguigu.product.valid.Update;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author qiang.long
 * @email Long_Q@outlook.com
 * @date 2024-03-17 23:41:04
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@Null(message = "新增不能指定品牌id",groups = {AddGroup.class})
	@NotNull(message = "修改必须指定品牌id",groups = {Update.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空",groups = {AddGroup.class, Update.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	private String logo;
	/**
	 * 介绍
	 */
	@NotBlank(message = "品牌描述不能为空")
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]  @ListValue 自定义注解
	 */
	@ListValue(vals={0,1},message = "必须提交指定的值",groups = {AddGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	private String firstLetter;
	/**
	 * 排序
	 */
	private Integer sort;

}
