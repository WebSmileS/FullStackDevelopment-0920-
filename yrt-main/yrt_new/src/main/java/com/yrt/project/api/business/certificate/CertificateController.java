package com.yrt.project.api.business.certificate;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.certificate.service.ICertificateService;
import com.yrt.project.api.business.certificate.vo.CertSearchOrgForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/cert")
@Api(tags = "业务-资证管理接口")
public class CertificateController extends BaseController {

	@Autowired
	private ICertificateService service;
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.cert", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/vendor/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取已签约厂商列表(分页)", notes = "获取已签约厂商列表(分页)")
	public TableDataInfo<Map<String,Object>> getContractVendorList(
			@Validated CertSearchOrgForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		startPage();
		return getDataTable(service.selectContractVendor(systemType, form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.cert", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/hospital/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取已签约医院列表(分页)", notes = "获取已签约医院列表(分页)")
	public TableDataInfo<Map<String,Object>> getContractHospitalList(
			@Validated CertSearchOrgForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		startPage();
		return getDataTable(service.selectContractHospital(systemType, form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.cert", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/dealer/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取已签约经销商列表", notes = "获取已签约经销商列表")
	public TableDataInfo<Map<String,Object>> getContractDealerList(
			@Validated CertSearchOrgForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		startPage();
		return getDataTable(service.selectContractDealer(systemType, form));
	}
	
	@Log(title = "business.cert", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/applicationforCertification/{oid}", method = RequestMethod.PUT)
	@ApiOperation(value = "修改机构状态为用户申请认证", notes = "修改机构状态为用户申请认证")
	public AjaxResult updateCertifieStatus1(
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		return toAjax(service.updateCertstatus(systemType, oid));
	}
}
