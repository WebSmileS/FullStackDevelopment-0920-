package com.yrt.project.modular.inquiry.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.inquiry.domain.Inquiry;
import com.yrt.project.modular.inquiry.domain.InquiryDetail;
import com.yrt.project.modular.inquiry.domain.InquirySortPrice;

public interface InquiryMapper {
	
	public List<Inquiry> searchInquiry(Map<String,Object> params);
	public List<Inquiry> searchUnReplyInquiry(Map<String,Object> params);
	
	public Inquiry selectInquiryInfo(Inquiry inquiry);
	
	public int updateInquiry(Map<String,Object> params);
	
	public int deleteInquiry(Map<String,Object> params);
	
	public List<InquiryDetail> selectInquiryDetailList(InquiryDetail detail);
	
	public int updateInquiryDetail(InquiryDetail detail);
	
	public int deleteInquiryDetail(Map<String,Object> params);
	
	//询价单价格查询
	public List<InquirySortPrice> selectProductListSortNewestPrice(Map<String,Object> params);
	public List<InquirySortPrice> selectProductListSortLowestPrice(Map<String,Object> params);
	
	//获取相关机构列表
	public List<Map<String,Object>> selectRelatedVendorList(Map<String,Object> params);
	public List<Map<String,Object>> selectRelatedDealerList(Map<String,Object> params);
	public List<Map<String,Object>> selectRelatedHospitalList(Map<String,Object> params);
}
