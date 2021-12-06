package com.yrt.project.api.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.PatternUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.project.api.common.vo.AddNoAuditOrgForm;
import com.yrt.project.api.common.vo.LoginForm;
import com.yrt.project.api.platform.service.IOrgRegisterAuditService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/")
@Api(tags = "公用-首页、登录、注册机构等相关接口")
public class IndexController extends BaseController {

	@Autowired
	private IOrgRegisterAuditService orgregisterauditService;
	
	// 系统首页
    @GetMapping
    public AjaxResult index()
    {
    	return success().put("文档地址", "/doc.html");
    }
        
//    @Log(title = "common.org", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/v1/reg/add", method = RequestMethod.POST)
	@ApiOperation(value="注册新机构", notes="注册新机构")
	public AjaxResult addNoAuditOrg(
			@RequestBody @Validated AddNoAuditOrgForm form) {
		//TODO:此处应该加入邮箱和电话的正则表达是验证
		if(StringUtils.isNotEmpty(form.getRegistrant_email())) {
			PatternUtils.validateEmail(form.getRegistrant_email(), "user.email.not.valid");
		}
		//2021-04-30 采购人员要求除了手机号外还需要支持带区号的座机号码
//		if(StringUtils.isNotEmpty(form.getRegistrant_phone())) {
//			PatternUtils.validatePhone(form.getRegistrant_phone(), "user.mobile.phone.number.not.valid");
//		}
		return toAjax(orgregisterauditService.insertNoAuditOrg(form));
	}
    
    @RequestMapping(value = "/v1/test", method = RequestMethod.POST)
    public AjaxResult test(@RequestBody LoginForm form) {
    	System.err.println("account:" + form.getAccount() +" password: "+form.getPassword());
    	return success();
    }

	@RequestMapping(value = "/userinfo", method = RequestMethod.GET)
	@ApiOperation(value = "查询登录人信息", notes = "请求头须设置systemType、uid、oid参数")
	public ResultInfo<ContextUserInfo> getWarehouseList() {
		return ResultInfo.success(ServletUtils.getCurUserInfo());
	}
}
