package com.yrt.project.modular.common.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.common.domain.ResponseProductInfo;

@Repository
public interface CommMapper {

	//获取合同相关的机构列表(只包含机构ID，机构名称和机构类型字段)
	public List<Map<String, Object>> selectRelatedOrg4DropDownList(Map<String,Object> params);
	public List<Map<String, Object>> selectAllOrg4DropDownList(Map<String,Object> params);
		
	//获取厂商产品列表(平台所有发布产品)
	public List<ResponseProductInfo> selectVendorProductList(Map<String,Object> params);
	public List<ResponseProductInfo> selectContractVendorProductList(Map<String,Object> params);
	//获取经销商自建产品列表
	public List<ResponseProductInfo> selectDealerProductList(Map<String,Object> params);
	public List<ResponseProductInfo> selectContractDealerProductList(Map<String,Object> params);
	//获取仓库人员列表
	public List<Map<String, Object>> selectWarehouseEmployeeList(Map<String, Object> params);
	//获取库存的厂商产品
	public List<ResponseProductInfo> selectVendorInventoryProductList(Map<String,Object> params);
}
