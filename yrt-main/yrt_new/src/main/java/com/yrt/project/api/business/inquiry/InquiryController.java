package com.yrt.project.api.business.inquiry;

import java.util.ArrayList;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.inquiry.service.IInquiryService;
import com.yrt.project.api.business.inquiry.vo.AddInquiryForm;
import com.yrt.project.api.business.inquiry.vo.EditInquiryForm;
import com.yrt.project.api.business.inquiry.vo.InquiryDetailIDForm;
import com.yrt.project.api.business.inquiry.vo.InquiryIDForm;
import com.yrt.project.api.business.inquiry.vo.InquiryPriceSearchForm;
import com.yrt.project.api.business.inquiry.vo.InquirySearchForm;
import com.yrt.project.modular.inquiry.domain.Inquiry;
import com.yrt.project.modular.inquiry.domain.InquirySortPrice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/inquiry")
@Api(tags = "业务-询价单接口")
public class InquiryController extends BaseController {
	
	@Autowired
	private IInquiryService service;

	@Log(title = "business.inquiry", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增询价单", notes = "新增询价单")
	public ResultInfo<Inquiry> addInquiry(
			@RequestBody @Valid AddInquiryForm form) {
		return ResultInfo.success(service.insertInquiry(form));
	}
	
	@Log(title = "business.inquiry", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑询价单", notes = "编辑询价单")
	public ResultInfo<Inquiry> editInquiry(
			@RequestBody @Valid EditInquiryForm form) {
		return ResultInfo.success(service.updateInquiry(form));
	}
	
	@Log(title = "business.inquiry", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除询价单", notes = "删除询价单")
	public AjaxResult deleteInquiry(
			@Validated InquiryIDForm form) {
		return toAjax(service.deleteInquiry(form));
	}
	
	@Log(title = "business.inquiry", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "询价单详情", notes = "询价单详情")
	public ResultInfo<Inquiry> getInquiryInfo(
			@Validated InquiryIDForm form) {
		return ResultInfo.success(service.selectInquiryInfo(form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.inquiry", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/unreply/list/{oid}/{all}", method = RequestMethod.GET)
	@ApiOperation(value = "查询待回复列表(分页)", notes = "查询待回复列表(分页)")
	public TableDataInfo<Inquiry> getUnReplyList(
			@ApiParam(name="oid",value="当前机构ID",required=true) @PathVariable Long oid,
			@ApiParam(name="all",value="是否显示已回复单据 0-不显示 1-显示",required=true) @PathVariable int all,
			@ApiParam(name="key",value="搜索关键字(询价机构/询价单号)",required=false) String key) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		if(systemType.equals(SystemType.HOSPITAL)) return getDataTable(new ArrayList<Inquiry>());//医院没有待回复功能
		InquirySearchForm form = new InquirySearchForm();
		form.setPartB(oid);
		form.setKey(key);
		if(all == 0) {
			form.setStatus("1,2");
		}else {
			form.setStatus("1,2,3");
		}
		startPage();
		return getDataTable(service.getUnReplyList(systemType, form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.inquiry", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/dealer/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "查询对经销商的询价列表(分页)", notes = "查询对经销商的询价列表(分页)")
	public TableDataInfo<Inquiry> getToDealerList(
			@ApiParam(name="oid",value="当前机构ID",required=true) @PathVariable Long oid,
			@ApiParam(name="key",value="搜索关键字(询价机构/询价单号)",required=false) String key) {
		startPage();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		if(systemType.equals(SystemType.VENDOR)) return getDataTable(new ArrayList<Inquiry>());//厂商没有查询对经销商询价的功能
		InquirySearchForm form = new InquirySearchForm();
		form.setPartA(oid);
		form.setKey(key);
		form.setStatus("0,1,2,3");
		return getDataTable(service.getToDealerList(systemType, form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.inquiry", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/vendor/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "查询对厂商的询价列表(分页)", notes = "查询对厂商的询价列表(分页)")
	public TableDataInfo<Inquiry> getToVendorList(
			@ApiParam(name="oid",value="当前机构ID",required=true) @PathVariable Long oid,
			@ApiParam(name="key",value="搜索关键字(询价机构/询价单号)",required=false) String key) {
		startPage();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		if(systemType.equals(SystemType.VENDOR)) return getDataTable(new ArrayList<Inquiry>());//厂商没有查询对经销商询价的功能
		InquirySearchForm form = new InquirySearchForm();
		form.setPartA(oid);
		form.setKey(key);
		form.setStatus("0,1,2,3");
		return getDataTable(service.getToVendorList(systemType, form));
	}
	
	@Log(title = "business.inquiry", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除询价单明细", notes = "删除询价单明细")
	public AjaxResult deleteInquiryDetail(
			@Validated InquiryDetailIDForm form) {
		return toAjax(service.deleteInquiryDetail(form));
	}
	
	
	@Log(title = "business.inquiry", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/price/list", method = RequestMethod.GET)
	@ApiOperation(value = "询价单价格查询", notes = "询价单价格查询")
	public ResultList<InquirySortPrice> getPriceProductList(
			@Validated InquiryPriceSearchForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		return ResultList.success(service.getPriceProductList(systemType, form));
	}
	
	//获取相关的机构列表(只包含机构ID，机构名称和BillsType,社会统一代码，是否认证)
	@SuppressWarnings("unchecked")
	@Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/org/list/{systemType}", method = RequestMethod.GET)
	@ApiOperation(value = "相关机构列表(分页)", notes = "相关机构列表(分页)")
	public TableDataInfo<Map<String, Object>> selectRelatedOrg4DropDownList(
			@ApiParam(name = "systemType", value = "相关机构类型", required = true) @PathVariable int systemType,
			@ApiParam(name = "name", value = "搜索机构名称", required = false) String name,
			@ApiParam(name = "is_certifie", value = "是否只查询认证通过的机构，默认全部机构", required = false) @RequestParam(defaultValue="true") Boolean is_certifie) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		switch (convertSystemType(systemType)) {
		case HOSPITAL:
			return getDataTable(service.selectRelatedHospitalList(curUser.getSystemType(), name, is_certifie));
		case DEALER:
			return getDataTable(service.selectRelatedDealerList(curUser.getSystemType(), name, is_certifie));
		case VENDOR:
			return getDataTable(service.selectRelatedVendorList(curUser.getSystemType(), name, is_certifie));
		default:
			break;
		}
		throw new DBException("当前机构不支持这个功能!");
	}
	 
}
