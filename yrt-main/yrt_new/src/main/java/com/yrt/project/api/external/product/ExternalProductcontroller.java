package com.yrt.project.api.external.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.external.product.vo.ExternalSearchProductForm;
import com.yrt.project.modular.external.domain.ExternalProductInfo;
import com.yrt.project.modular.external.domain.Org4DropDown;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Runner
 *
 */
@RestController
@RequestMapping("/v1/external")
@Api(tags = "外部调用-医院产品查询接口")
public class ExternalProductcontroller extends BaseController {

	@Autowired ExternalProductServiceImpl service;
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.external", businessType = BusinessType.OTHER, operatorType = OperatorType.EXTERNAL_CALL)
	@RequestMapping(value = "/product/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取产品列表(分页)", notes = "获取产品列表(分页)")
	public TableDataInfo<ExternalProductInfo> getVendorProductList(
			@Validated ExternalSearchProductForm form) {
		ContextUserInfo cuser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.getVendorProductList(cuser.getOrgId(), form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.external", businessType = BusinessType.OTHER, operatorType = OperatorType.EXTERNAL_CALL)
	@RequestMapping(value = "/org/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取所有机构列表(分页)", notes = "获取所有机构列表(分页)")
	public TableDataInfo<Org4DropDown> selectRelatedOrg4DropDownList(
			@ApiParam(name = "name", value = "搜索机构名称", required = false) String name) {
		ContextUserInfo cuser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.selectRelatedOrg4DropDownList(cuser.getOrgId(), name));
	}
}
