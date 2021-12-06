package com.yrt.project.api.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.common.service.CommService;
import com.yrt.project.api.common.service.UDIService;
import com.yrt.project.api.common.vo.SearchAccountForm;
import com.yrt.project.api.common.vo.SearchForm;
import com.yrt.project.api.common.vo.UDIDInfo;
import com.yrt.project.api.platform.service.IProductService;
import com.yrt.project.api.platform.vo.product.SearchProductForm;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.common.domain.SmsTemp;
import com.yrt.project.modular.organization.service.IDealerService;
import com.yrt.project.modular.organization.service.IHospitalService;
import com.yrt.project.modular.organization.service.IUserService;
import com.yrt.project.modular.organization.service.IVendorService;
import com.yrt.project.modular.product.domain.ResponseProductInfo4Dropdown;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/comm")
@Api(tags = "公用-其他通用相关接口")
public class OtherController extends BaseController {

	@Autowired private IVendorService venderService;
	@Autowired private IDealerService dealerService;
	@Autowired private IHospitalService hospitalService;
	@Autowired private IProductService productService;
	
	@Autowired private IUserService userService;
	@Autowired private CommService commonService;
	
	@Autowired private UDIService udiService;
	
	@SuppressWarnings("unchecked")
	@Log(title = "common.other", businessType = BusinessType.OTHER, operatorType = OperatorType.OTHER)
	@RequestMapping(value = "/allVendor", method = RequestMethod.GET)
	@ApiOperation(value = "获取所有可用的厂商列表(分页，产品专用)", notes = "获取所有可用的厂商列表(分页，产品专用)")
	public TableDataInfo<Map<String, Object>> getVendorList(SearchForm form) {
		startPage();
		return getDataTable(venderService.selectOrgList4Dropdown1(form.getName()));
	}
	
//	@Log(title = "common.other", businessType = BusinessType.OTHER, operatorType = OperatorType.OTHER)
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{systemType}/allOrg", method = RequestMethod.GET)
	@ApiOperation(value = "获取所有可用的机构列表(分页，下列组件专用)", notes = "获取所有可用的厂商列表(分页，下列组件专用)")
	public TableDataInfo<Map<String, Object>> getAllOrgList(
			@ApiParam(name="systemType",value="机构类型 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			SearchForm form) {
		startPage();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		switch (systemType) {
		case 1:
			list = hospitalService.selectOrgList4Dropdown(form.getName(), form.getIs_certifie());
			break;
		case 2:
			list = dealerService.selectOrgList4Dropdown(form.getName(), form.getIs_certifie());
			break;
		case 3:
			list = venderService.selectOrgList4Dropdown(form.getName(), form.getIs_certifie());
			break;
		}
		return getDataTable(list);
	}
	

	@Log(title = "common.other", businessType = BusinessType.OTHER, operatorType = OperatorType.OTHER)
	@RequestMapping(value = "/user/{systemType}/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据机构ID获取所有登录用户账号列表(下列组件专用)", notes = "根据机构ID获取所有登录用户账号列表(下列组件专用)")
	public AjaxResult getUserList4Dropdown(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid) {
		return success().put("list", userService.selectUserListByOrgId4Dropdown(systemType, oid));
	}
	
	@Log(title = "common.other", businessType = BusinessType.OTHER, operatorType = OperatorType.OTHER)
	@RequestMapping(value = "/vendor/Product/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取厂商产品列表(分页,平台所有发布产品)", notes = "获取厂商产品列表(分页,平台所有发布产品)")
	public TableDataInfo<ResponseProductInfo> getVendorProductList(
			@Validated SearchProductForm form) {
		PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        form.escapeLikeValue();
		return productService.selectVendorProductList(form, pageNum, pageSize);
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "common.other", businessType = BusinessType.OTHER, operatorType = OperatorType.OTHER)
	@RequestMapping(value = "/vendor/Product/dropdown", method = RequestMethod.GET)
	@ApiOperation(value = "获取厂商产品列表(分页,平台所有发布产品的名称)", notes = "获取厂商产品列表(分页,平台所有发布产品的名称)")
	public TableDataInfo<ResponseProductInfo4Dropdown> getVendorProductDropdown(
			@ApiParam(name="vid",value="厂商ID",required=true) @RequestParam(required = true) Long vid, 
			@ApiParam(name="name",value="产品名称",required=false) String name) {
		startPage();
		return getDataTable(productService.getVendorProductDropdown(vid, name));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "common.other", businessType = BusinessType.OTHER, operatorType = OperatorType.OTHER)
	@RequestMapping(value = "/account/list", method = RequestMethod.POST)
	@ApiOperation(value = "查询登录帐号的初始密码(分页)", notes = "查询登录帐号的初始密码(分页)")
	public TableDataInfo<SmsTemp> getInitLoginAccount(
			@RequestBody SearchAccountForm form) {
		if(!SystemType.PLATFORM.equals(ServletUtils.getCurUserSystemType())) {
			throw new ValidateException("非平台人员不能使用!");
		}
		startPage();
		return getDataTable(commonService.getInitLoginAccount(form));
	}
	
	
	@Log(title = "common.other", businessType = BusinessType.OTHER, operatorType = OperatorType.OTHER)

	@RequestMapping(value = "/scan", method = RequestMethod.GET)

	@ApiOperation(value = "测试扫码UDI条码", notes = "测试扫码UDI条码")
	public ResultInfo<UDIDInfo> testScanGs1Barcode(String code) {
		return ResultInfo.success(udiService.parseBarcode(code, true));
	}
	 
}
