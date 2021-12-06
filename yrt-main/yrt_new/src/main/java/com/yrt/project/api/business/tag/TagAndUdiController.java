package com.yrt.project.api.business.tag;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.tag.vo.AddUDIRefproductForm;
import com.yrt.project.api.business.tag.vo.SearchProductBaseForm;
import com.yrt.project.api.business.tag.vo.SearchUDIRefProductForm;
import com.yrt.project.api.common.service.UDIService;
import com.yrt.project.modular.product.domain.ProductBaseRefTag;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/tag")
@Api(tags = "业务-标签|UDI管理接口")
public class TagAndUdiController extends BaseController {

	@Autowired UDIService service;
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.tag", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ApiOperation(value = "已关联标签的产品列表(分页)", notes = "用于标签管理主页面列表")
	public TableDataInfo<ProductBaseRefTag> getProductBaseRefTagList(
			@RequestBody SearchProductBaseForm form) {
		startPage();
		return getDataTable(service.getProductBaseRefTagList(form, true));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.tag", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/product/list", method = RequestMethod.POST)
	@ApiOperation(value = "未关联标签的产品列表(分页)", notes = "用于新增关联时选择没有关联的产品")
	public TableDataInfo<ProductBaseRefTag> getUnProductBaseRefTagList(
			@RequestBody SearchProductBaseForm form) {
		startPage();
		return getDataTable(service.getProductBaseRefTagList(form, false));
	}
	
	@Log(title = "business.tag", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "批量添加标签与产品关联", notes = "批量添加标签与产品关联")
	public AjaxResult insertPurchaseGrn(
			@RequestBody @Validated List<AddUDIRefproductForm> list) {
		return toAjax(service.batchSaveUDIRefProduct(list));
	}
	
	@Log(title = "business.tag", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del/{barcode}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除标签与产品关联", notes = "删除标签与产品关联")
	public AjaxResult deleteGrn(
			@ApiParam(name="barcode",value="标签/UDI条码",required=true) @PathVariable String barcode) {
		return toAjax(service.deleteUDIRefProduct(barcode));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.tag", businessType = BusinessType.OTHER, operatorType = OperatorType.OTHER)
	@RequestMapping(value = "/vendor/Product/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取厂商产品列表(分页,平台所有发布产品)", notes = "获取厂商产品列表(分页,平台所有发布产品)")
	public TableDataInfo<Map<String,Object>> getVendorProductList(
			@Validated SearchUDIRefProductForm form) {
		startPage();
		return getDataTable(service.selectVendorProductList(form));
	}
}
