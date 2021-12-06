package com.yrt.project.api.platform;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.exception.BusinessException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.PatternUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.platform.service.IOrgRegisterAuditService;
import com.yrt.project.api.platform.vo.organization.MatchResult;
import com.yrt.project.api.platform.vo.organization.NoAuditOrgForm;
import com.yrt.project.api.platform.vo.organization.SearchOrgForm;
import com.yrt.project.modular.organization.domain.CheckCompany;
import com.yrt.project.modular.organization.domain.NotAuditOrganization;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/platform/orgReg")
@Api(tags = "平台-审核机构注册信息相关接口")
public class OrgRegisterAuditController extends BaseController {

	@Autowired
	private IOrgRegisterAuditService orgregisterauditService;
	
	@Log(title = "platform.org.Reg", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
	@ApiOperation(value="根据注册机构id查询机构信息", notes="根据注册机构id查询机构信息")
	public ResultInfo<NotAuditOrganization> getOrgInfo(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		NotAuditOrganization org = orgregisterauditService.selectNoAuditOrgInfo(id);
		return ResultInfo.success(org);
	}
		
	@SuppressWarnings("unchecked")
	@Log(title = "platform.org.Reg", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/list/{systemType}", method = RequestMethod.GET)
	@ApiOperation(value="根据注册机构类型获取机构列表(分页)", notes="根据注册机构类型获取机构列表(分页)")
	public TableDataInfo<NotAuditOrganization> getNoAuditOrgList(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) 
			@PathVariable int systemType, SearchOrgForm form) {
		startPage();
		if(StringUtils.isNull(systemType)) {
			throw new BusinessException(MessageUtils.message("param.not.null", "系统类型"));
		}
		form.escapeLikeValue();
		List<NotAuditOrganization> list = orgregisterauditService.selectAllNoAuditOrgList(convertSystemType(systemType),form);
		return getDataTable(list);
	}
	
	@Log(title = "platform.orgReg", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="修改注册机构基本信息", notes="修改注册机构基本信息")
	public AjaxResult updateNoAuditOrg(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id, 
			NoAuditOrgForm form) {
		//TODO:此处应该加入邮箱和电话的正则表达是验证
		if(StringUtils.isNotEmpty(form.getRegistrant_email())) {
			PatternUtils.validateEmail(form.getRegistrant_email(), "user.email.not.valid");
		}
		//2021-04-30 采购人员要求除了手机号外还需要支持带区号的座机号码
//		if(StringUtils.isNotEmpty(form.getRegistrant_phone())) {
//			PatternUtils.validatePhone(form.getRegistrant_phone(), "user.mobile.phone.number.not.valid");
//		}
		return toAjax(orgregisterauditService.updateNoAuditOrg(id, form));
	}
	
	@Log(title = "platform.orgReg", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value="删除注册机构", notes="删除注册机构")
	public AjaxResult delNoAuditOrg(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		return toAjax(orgregisterauditService.deleteNoAuditOrg(id));
	}
	
	@Log(title = "platform.orgReg", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/pass/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="注册机构审核", notes="注册机构审核,没有问题直接通过,有问题返回可能冲突的数据")
	public AjaxResult passNoAuditOrg(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		MatchResult result = orgregisterauditService.passAduitOrg(id);
		if(result.isEmpty()) {
			return success();
		}
		return error().put("info", result);
	}
	
	@Log(title = "platform.orgReg", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/match/{id}", method = RequestMethod.POST)
	@ApiOperation(value="解决审核冲突数据", notes="解决审核冲突数据")
	public AjaxResult passOrg(
			@ApiParam(name="id",value="注册机构ID",required=true) @PathVariable Long id,
			@RequestBody @Validated CheckCompany company) {
		return toAjax(orgregisterauditService.match(company, id));
	}
	
	@Log(title = "platform.orgReg", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/nopass/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="机构注册审核不通过", notes="机构注册审核不通过")
	public AjaxResult nopassNoAuditOrg(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@RequestParam(required=false, defaultValue="") String reason) {
		return toAjax(orgregisterauditService.noPassAduitOrg(id, reason));
	}
}
