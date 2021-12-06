package com.yrt.project.api.business.gsp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.project.api.platform.service.SysParameterService;
import com.yrt.project.modular.gsp.domain.GspSetup;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/business/gsp")
@Api(tags = "业务-GSP系统设置接口")
public class GspController extends BaseController {

	@Autowired SysParameterService paramService;
	
	@Log(title = "business.gsp", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/setup/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取GSP系统设置", notes = "当前机构获取GSP系统设置")
	public ResultInfo<GspSetup> getGspSetup(){
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		String setupStr = paramService.getGSPSetup(curUser.getSystemType(), curUser.getOrgId());
		GspSetup gsp = JSONObject.parseObject(setupStr, GspSetup.class);
		return ResultInfo.success(gsp);
	}
	
	@Log(title = "business.gsp", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/setup/save", method = RequestMethod.POST)
	@ApiOperation(value = "保存GSP系统设置", notes = "保存GSP系统设置")
	public AjaxResult save(
			@RequestBody GspSetup gsp) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		String setupValue = JSONObject.toJSONString(gsp);
		paramService.saveParameter(curUser.getSystemType(), curUser.getOrgId(), SysParameterService.GSP_SETUP, "GSP设置", setupValue);
		return success();
	}
}
