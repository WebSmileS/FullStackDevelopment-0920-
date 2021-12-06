package com.yrt.project.api.business.warehouse.vo;

import java.util.List;

import com.yrt.project.api.common.vo.UDIDInfo;
import com.yrt.project.modular.order.domain.PurchasingRecordProduct;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UsableProductInfo {

	@ApiModelProperty(value="平台产品基本信息(包含标签信息,如批号效期)")
	private UDIDInfo productBaseInfo;
	
	@ApiModelProperty(value="可用产品信息列表")
	private List<PurchasingRecordProduct> usableProductList;
	
	@ApiModelProperty(value="是否只有包含批号、效期等其他信息,如果为true，则只需要查看productBaseInfo")
	private boolean onlyOtherInfo = false;
	
	@ApiModelProperty(value="是否存在可用产品. true,则查看usableProductList; false,则只需要查看productBaseInfo")
	private boolean existUsabelProduct = true;
	
	@ApiModelProperty(value="UDI码是否存在，false则需要获取msg信息")
	private boolean existUDI = true;
	
	@ApiModelProperty(value = "提示信息")
	private String msg;
}
