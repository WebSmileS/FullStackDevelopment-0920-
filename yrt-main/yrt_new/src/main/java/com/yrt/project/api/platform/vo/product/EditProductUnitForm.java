package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditProductUnitForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 名称* */
	@ApiModelProperty(value="名称",required=true)
	@NotBlank(message = "名称不能为空")
	private String name;
	/** 最小计量/倍数 */
	@ApiModelProperty(value="最小计量/倍数",required=true)
	@Digits(integer=8, fraction=4, message="倍数只能是4位整数和最大4位小数的数字")
	private BigDecimal measure;
	@ApiModelProperty(value="是否常用 0-不常用 1-常用",required=true)
	@NotNull(message = "是否常用不能为空")
	private Integer is_common_use;
	
	public boolean hasUpdate() {
		if(StringUtils.isNotEmpty(name) || StringUtils.isNotNull(measure)){
			return true;
		}
		return false;
	}
}
