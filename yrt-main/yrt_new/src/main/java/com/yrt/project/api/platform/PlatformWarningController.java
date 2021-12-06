package com.yrt.project.api.platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.common.service.WarningService;
import com.yrt.project.api.common.vo.SearchWarningForm;
import com.yrt.project.api.platform.service.SysParameterService;
import com.yrt.project.modular.warning.domain.CertWarning;
import com.yrt.project.modular.warning.domain.ProductMdrfWarning;
import com.yrt.project.modular.warning.domain.WarningSetupValue;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/platform/warning")
@Api(tags = "平台-效期预警接口")
public class PlatformWarningController  extends BaseController {
	
	@Autowired WarningService service;
	@Autowired SysParameterService paramService;
	
	@Log(title = "business.warning", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/setup/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取预警设置", notes = "获取预警设置")
	public ResultInfo<WarningSetupValue> getWarningSetup(){
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		String value = null;
		if(SystemType.PLATFORM.equals(curUser.getSystemType())) {
			value = paramService.getDefaultWarningSetup();
		}else {
			value = paramService.getWarningSetup(curUser.getSystemType(), curUser.getOrgId());
		}
		WarningSetupValue res = JSONObject.parseObject(value, WarningSetupValue.class);
		return ResultInfo.success(res);
	}
	
	@Log(title = "business.warning", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/setup/save", method = RequestMethod.POST)
	@ApiOperation(value = "保存预警设置", notes = "保存预警设置")
	public AjaxResult saveWarningSetup(
			@RequestBody WarningSetupValue value) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		String setupValue = JSONObject.toJSONString(value);
		paramService.saveParameter(curUser.getSystemType(), 0L, SysParameterService.WARNING, "预警设置", setupValue);
		return success();
	}
	
	//机构资证预警对应方法
	@SuppressWarnings("unchecked")
	@Log(title = "business.warning", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/cert/list", method = RequestMethod.GET)
	@ApiOperation(value = "机构资证效期预警(分页)", notes = "机构资证效期预警(分页)")
	public TableDataInfo<CertWarning> getCertWarningList(
			@Validated SearchWarningForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.getCertWarningList(form, curUser));
	}
	
	//机构资证预警对应方法
	@SuppressWarnings("unchecked")
	@Log(title = "business.warning", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/mdrf/list", method = RequestMethod.GET)
	@ApiOperation(value = "产品注册证效期预警(分页)", notes = "产品注册证效期预警(分页)")
	public TableDataInfo<ProductMdrfWarning> getProductMdrfWarningList(
			@Validated SearchWarningForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.getProductMdrfWarningList(form, curUser));
	}
}
