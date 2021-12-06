package com.yrt.project.api.business.inquiry.service;

import java.util.List;
import java.util.Map;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.business.inquiry.vo.AddInquiryForm;
import com.yrt.project.api.business.inquiry.vo.InquiryDetailIDForm;
import com.yrt.project.api.business.inquiry.vo.EditInquiryForm;
import com.yrt.project.api.business.inquiry.vo.InquiryIDForm;
import com.yrt.project.api.business.inquiry.vo.InquiryPriceSearchForm;
import com.yrt.project.api.business.inquiry.vo.InquirySearchForm;
import com.yrt.project.modular.inquiry.domain.Inquiry;
import com.yrt.project.modular.inquiry.domain.InquirySortPrice;

public interface IInquiryService {

	public List<Inquiry> getUnReplyList(SystemType systemType, InquirySearchForm form);
	
	public List<Inquiry> getToDealerList(SystemType systemType, InquirySearchForm form);
	
	public List<Inquiry> getToVendorList(SystemType systemType, InquirySearchForm form);
	
	public Inquiry selectInquiryInfo(InquiryIDForm form);
	
	public Inquiry insertInquiry(AddInquiryForm form);
	
	public Inquiry updateInquiry(EditInquiryForm form);
	
	public int deleteInquiry(InquiryIDForm form);
	
	public int deleteInquiryDetail(InquiryDetailIDForm form);
	
	//询价单价格查询
	public List<InquirySortPrice> getPriceProductList(SystemType systemType, InquiryPriceSearchForm form);

	public List<Map<String, Object>> selectRelatedHospitalList(SystemType curOrgSystemType, String name, Boolean is_certifie);
	public List<Map<String, Object>> selectRelatedVendorList(SystemType curOrgSystemType, String name, Boolean is_certifie);
	public List<Map<String, Object>> selectRelatedDealerList(SystemType curOrgSystemType, String name, Boolean is_certifie);
}
