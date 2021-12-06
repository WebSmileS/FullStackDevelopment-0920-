package com.yrt.project.api.external.order;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.user.UserException;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.project.api.external.order.vo.ExternalSaleOrderForm;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/external/order")
@Api(tags = "外部调用-医院产品采购接口")
public class ExternalOrderController extends BaseController {
	
	@Autowired ExternalOrderServiceImpl service;
	
	@Autowired DealerMapper dMapper;
	@Autowired HospitalMapper hMapper;
	@Autowired VendorMapper vMapper;
	
	private String[] getCurEmployeeInfo() {
		SystemType curSystemType = ServletUtils.getCurUserSystemType();
		Long curId = ServletUtils.getCurUserID();
		OrgEmployee curEmp = null;
		switch (curSystemType) {
		case HOSPITAL:
			curEmp = hMapper.selectOrgEmployeeInfoByUserId(curId);
			break;
		case DEALER:
			curEmp = dMapper.selectOrgEmployeeInfoByUserId(curId);
			break;
		case VENDOR:
			curEmp = vMapper.selectOrgEmployeeInfoByUserId(curId);
			break;
		default:
		}
		if(curEmp != null) {
			return new String[] {curEmp.getEmployee_inner_sn().toString(), curEmp.getName()};
		}
		throw new UserException("user.unknown.error",null);
	}
	
	// 采购订单编辑
	@Log(title = "business.external.order", businessType = BusinessType.OTHER, operatorType = OperatorType.EXTERNAL_CALL)
	@RequestMapping(value = "/build", method = RequestMethod.POST)
	@ApiOperation(value = "生成采购订单", notes = "生成采购订单")
	public AjaxResult buildPurchaseOrder(
			@RequestBody @Valid ExternalSaleOrderForm form) {
		String[] user = getCurEmployeeInfo();
		return toAjax(service.buildPurchaseOrder(form,Long.parseLong(user[0]),user[1]));
	}
}
