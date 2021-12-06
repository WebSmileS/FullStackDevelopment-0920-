package com.yrt.project.modular.external.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.external.domain.ExternalProductInfo;
import com.yrt.project.modular.external.domain.Org4DropDown;

@Repository
public interface ExternalProductMapper {

	//获取所有机构列表(只是包括经销商和厂商)
	public List<Org4DropDown> selectRelatedOrg4DropDownList(Map<String,Object> params);
	
	//获取厂商产品列表(平台所有发布产品)
	public List<ExternalProductInfo> selectVendorProductList(Map<String,Object> params);
	public List<ExternalProductInfo> selectContractVendorProductList(Map<String,Object> params);
	//获取经销商自建产品列表
//	public List<Map<String,Object>> selectDealerProductList(Map<String,Object> params);
//	public List<Map<String,Object>> selectContractDealerProductList(Map<String,Object> params);
}
