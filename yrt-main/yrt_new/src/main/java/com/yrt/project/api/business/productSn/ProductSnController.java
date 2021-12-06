package com.yrt.project.api.business.productSn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.BusinessException;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.project.api.business.productSn.service.ProductSnService;
import com.yrt.project.api.business.productSn.vo.EditProductSnForm;
import com.yrt.project.api.business.productSn.vo.ProductSnForm;
import com.yrt.project.api.business.productSn.vo.ProductSnIDForm;
import com.yrt.project.modular.product.domain.ProductSn;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/business/product/sn")
@Api(tags = "业务-机构产品医保编号和阳光流水号管理接口")
public class ProductSnController extends BaseController {

	@Autowired private ProductSnService service;
	
	@Log(title = "business.product.sn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取产品编号列表(医保编号或者阳光流水号)", notes = "获取产品编号列表(医保编号或者阳光流水号)")
	public ResultList<ProductSn> getProductSnList(
			@Validated ProductSnIDForm form){
		SystemType type = ServletUtils.getCurUserSystemType();
		if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
			Long oid = ServletUtils.getCurUserOrgID();
			return ResultList.success(service.selectProductSnList(oid, form.getVendor_inner_sn(),
					form.getProduct_inner_sn(), form.getSpecification_inner_sn(), form.getType()));
		} else {
			throw new BusinessException("当前机构类型不支持!");
		}
	}
	
	@Log(title = "business.product.sn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增产品编号(医保编号或者阳光流水号)", notes = "新增产品编号(医保编号或者阳光流水号)")
	public AjaxResult addProductSn(
			@RequestBody @Validated ProductSnForm form) {
		SystemType type = ServletUtils.getCurUserSystemType();
		if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
			Long oid = ServletUtils.getCurUserOrgID();
			service.addOrEditProductSn(oid, form);
			return success();
		} else {
			throw new BusinessException("当前机构类型不支持!");
		}
	}
	
	@Log(title = "business.product.sn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ApiOperation(value = "编辑产品编号(医保编号或者阳光流水号)", notes = "编辑产品编号(医保编号或者阳光流水号)")
	public AjaxResult editProductSn(
			@RequestBody @Validated EditProductSnForm form) {
		SystemType type = ServletUtils.getCurUserSystemType();
		if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
			Long oid = ServletUtils.getCurUserOrgID();
			service.updateProductSn(oid, form);
			return success();
		} else {
			throw new BusinessException("当前机构类型不支持!");
		}
	}
	
	@Log(title = "business.product.sn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除产品编号(医保编号或者阳光流水号)", notes = "删除产品编号(医保编号或者阳光流水号)")
	public AjaxResult deleteProductSn(
			@RequestBody @Validated ProductSnIDForm form) {
		SystemType type = ServletUtils.getCurUserSystemType();
		if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
			Long oid = ServletUtils.getCurUserOrgID();
			service.deleteProductSn(oid, form.getVendor_inner_sn(), form.getProduct_inner_sn(),
					form.getSpecification_inner_sn(), form.getSn_inner_sn(), form.getType());
			return success();
		} else {
			throw new BusinessException("当前机构类型不支持!");
		}
	}
}
