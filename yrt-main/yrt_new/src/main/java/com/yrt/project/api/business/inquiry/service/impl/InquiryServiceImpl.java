package com.yrt.project.api.business.inquiry.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.project.api.business.inquiry.service.IInquiryService;
import com.yrt.project.api.business.inquiry.vo.AddInquiryDetailForm;
import com.yrt.project.api.business.inquiry.vo.AddInquiryForm;
import com.yrt.project.api.business.inquiry.vo.EditInquiryForm;
import com.yrt.project.api.business.inquiry.vo.InquiryDetailIDForm;
import com.yrt.project.api.business.inquiry.vo.InquiryIDForm;
import com.yrt.project.api.business.inquiry.vo.InquiryPriceSearchForm;
import com.yrt.project.api.business.inquiry.vo.InquirySearchForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.inquiry.domain.Inquiry;
import com.yrt.project.modular.inquiry.domain.InquiryDetail;
import com.yrt.project.modular.inquiry.domain.InquirySortPrice;
import com.yrt.project.modular.inquiry.mapper.DealerDealerInquiryMapper;
import com.yrt.project.modular.inquiry.mapper.DealerVendorInquiryMapper;
import com.yrt.project.modular.inquiry.mapper.HospitalDealerInquiryMapper;
import com.yrt.project.modular.inquiry.mapper.HospitalVendorInquiryMapper;

@Service
public class InquiryServiceImpl implements IInquiryService {

	@Autowired
	private DealerDealerInquiryMapper ddMapper;
	@Autowired
	private DealerVendorInquiryMapper dvMapper;
	@Autowired
	private HospitalDealerInquiryMapper hdMapper;
	@Autowired
	private HospitalVendorInquiryMapper hvMapper;
	
	@Autowired private VoucherNumberService numberUtils;
	
	@Override
	public List<Inquiry> getUnReplyList(SystemType systemType, InquirySearchForm form) {
		List<Inquiry> list = new ArrayList<>();
		Map<String,Object> params = new HashMap<>();
		params.put("partB", form.getPartB());
		params.put("key", form.getKey());
		params.put("status", form.getStatus());
		switch(systemType) {
		case DEALER:
			list = ddMapper.searchUnReplyInquiry(params);
			break;
		case VENDOR:
			list = dvMapper.searchUnReplyInquiry(params);
			break;
		default:
		}
		return list;
	}
	
	@Override
	public List<Inquiry> getToDealerList(SystemType systemType, InquirySearchForm form) {
		List<Inquiry> list = new ArrayList<>();
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA());
		params.put("key", form.getKey());
		params.put("status", form.getStatus());
		switch(systemType) {
		case HOSPITAL:
			list = hdMapper.searchInquiry(params);
			break;
		case DEALER:
			list = ddMapper.searchInquiry(params);
			break;
		default:
		}
		return list;
	}
	
	@Override
	public List<Inquiry> getToVendorList(SystemType systemType, InquirySearchForm form) {
		List<Inquiry> list = new ArrayList<>();
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA());
		params.put("key", form.getKey());
		params.put("status", form.getStatus());
		switch(systemType) {
		case HOSPITAL:
			list = hvMapper.searchInquiry(params);
			break;
		case DEALER:
			list = dvMapper.searchInquiry(params);
			break;
		default:
		}
		return list;
	}

	@Override
	public Inquiry selectInquiryInfo(InquiryIDForm form) {
		BillsType type = form.getBillsType();
		Inquiry inquiry = new Inquiry();
		InquiryDetail detail = new InquiryDetail();
		BeanUtils.copyProperties(form, detail);
		BeanUtils.copyProperties(form, inquiry);
		switch (type) {
		case DD:
			inquiry = ddMapper.selectInquiryInfo(inquiry);
			List<InquiryDetail> list = ddMapper.selectInquiryDetailList(detail);
			inquiry.setDetailList(list);
			return inquiry;
		case DV:
			inquiry = dvMapper.selectInquiryInfo(inquiry);
			List<InquiryDetail> list1 = dvMapper.selectInquiryDetailList(detail);
			inquiry.setDetailList(list1);
			return inquiry;
		case HD:
			inquiry = hdMapper.selectInquiryInfo(inquiry);
			List<InquiryDetail> list2 = hdMapper.selectInquiryDetailList(detail);
			inquiry.setDetailList(list2);
			return inquiry;
		case HV:
			inquiry = hvMapper.selectInquiryInfo(inquiry);
			List<InquiryDetail> list3 = hvMapper.selectInquiryDetailList(detail);
			inquiry.setDetailList(list3);
			return inquiry;
		default:
			break;
		}
		return null;
	}

	@Override
	@Transactional
	public Inquiry insertInquiry(AddInquiryForm form) {
		BillsType type = form.getBillsType();
		Map<String,Object> params = new HashMap<>();
		params.put("status_i", form.getStatus());
		params.put("ask_time_i", new Date());
		params.put("ilps_sn_i", numberUtils.getMaxSn(VoucherType.INQUIRY_PRICE, ServletUtils.getCurUserSystemType(), form.getPartA()));//.getInquiryMaxSn(ServletUtils.getCurUserSystemType(), form.getPartA()));
		params.put("contacts_i", StringUtils.isEmpty(form.getContacts())?"":form.getContacts());
		params.put("phone_i", StringUtils.isEmpty(form.getPhone())?"":form.getPhone());
		params.put("description_i", StringUtils.isEmpty(form.getDescription())?"":form.getDescription());
		switch (type) {
		case DD:
			params.put("dealer_a_inner_sn_i", form.getPartA());
			params.put("dealer_b_inner_sn_i", form.getPartB());
			ddMapper.d_add_dealer_dealer_il(params);
			break;
		case DV:
			params.put("dealer_inner_sn_i", form.getPartA());
			params.put("vendor_inner_sn_i", form.getPartB());
			dvMapper.d_add_dealer_vendor_il(params);
			break;
		case HD:
			params.put("hospital_inner_sn_i", form.getPartA());
			params.put("dealer_inner_sn_i", form.getPartB());
			hdMapper.h_add_hospital_dealer_il(params);
			break;
		case HV:
			params.put("hospital_inner_sn_i", form.getPartA());
			params.put("vendor_inner_sn_i", form.getPartB());
			hvMapper.h_add_hospital_vendor_il(params);
			break;
		default:
			break;
		}
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long iid = Long.valueOf(params.get("ilps_inner_sn_o").toString());
			List<AddInquiryDetailForm> details = form.getDetailList();
			for (AddInquiryDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("ilps_inner_sn_i", iid);
				params1.put("product_vendor_inner_sn_i", detail.getVendor_inner_sn());
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("unit_price_i", detail.getUnit_price());
				params1.put("discount_i", detail.getDiscount());
				params1.put("rate_i", detail.getRate());
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("product_vendor_name_i", detail.getVendor_name());
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit());
				switch (type) {
				case DD:
					params1.put("dealer_inner_sn_i", form.getPartA());
					ddMapper.d_add_dealer_dealer_il_product_detail(params1);
					break;
				case DV:
					params1.put("dealer_inner_sn_i", form.getPartA());
					dvMapper.d_add_dealer_vendor_il_product_detail(params1);
					break;
				case HD:
					params1.put("hospital_inner_sn_i", form.getPartA());
					hdMapper.h_add_hospital_dealer_il_product_detail(params1);
					break;
				case HV:
					params1.put("hospital_inner_sn_i", form.getPartA());
					hvMapper.h_add_hospital_vendor_il_product_detail(params1);
					break;
				default:
					break;
				}
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.add.fail","询价单明细",params1.get("message_string_o")));
				}
			}
			InquiryIDForm form1 = new InquiryIDForm();
			form1.setBillsType(type);
			form1.setPartA(form.getPartA());
			form1.setIlps_inner_sn(iid);
			return selectInquiryInfo(form1);
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","询价单",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","询价单",params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public Inquiry updateInquiry(EditInquiryForm form) {
		Inquiry newInquiry = new Inquiry();
		int result = 1;
		BillsType type = form.getBillsType();
		//查看是否更改了机构类别Il_type
		if(form.getBillsType() != null && form.getBillsType_old() != null) {
			//没有更改Il_type则继续修改
			if(form.getBillsType().equals(form.getBillsType_old())) {
				Map<String,Object> params = form.getInfo();
				params.put("partA",form.getPartA());
				params.put("partB", form.getPartB());
				params.put("ilps_inner_sn", form.getIlps_inner_sn());
				params.put("status", form.getStatus());
				try {
					switch (type) {
					case DD:
						result = result & ddMapper.updateInquiry(params);
						break;
					case DV:
						result = result & dvMapper.updateInquiry(params);
						break;
					case HD:
						result = result & hdMapper.updateInquiry(params);
						break;
					case HV:
						result = result & hvMapper.updateInquiry(params);
						break;
					default:
						break;
					}
				}catch (Exception e) {
					if(e instanceof DuplicateKeyException) {
						throw new DuplicateException(MessageUtils.message("db.update.fail","询价单","询价单编码重复"));
					}else {
						throw new DBException(MessageUtils.message("db.update.fail","询价单",e.getMessage()));
					}
				}
				if(result > 0) {
					Map<String,Object> tmp = new HashMap<>();
					tmp.put("partA", form.getPartA());
					tmp.put("ilps_inner_sn", form.getIlps_inner_sn());
					switch (type) {
					case DD:
						ddMapper.deleteInquiryDetail(tmp);
						break;
					case DV:
						dvMapper.deleteInquiryDetail(tmp);
						break;
					case HD:
						hdMapper.deleteInquiryDetail(tmp);
						break;
					case HV:
						hvMapper.deleteInquiryDetail(tmp);
						break;
					default:
						break;
					}
					List<AddInquiryDetailForm> details = form.getDetailList();
					for (AddInquiryDetailForm detail : details) {
						Map<String,Object> params1 = new HashMap<>();
						params1.put("ilps_inner_sn_i", form.getIlps_inner_sn());
						params1.put("product_vendor_inner_sn_i", detail.getVendor_inner_sn());
						params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
						params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
						params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
						params1.put("quantity_i", detail.getQuantity());
						params1.put("unit_price_i", detail.getUnit_price());
						params1.put("discount_i", detail.getDiscount());
						params1.put("rate_i", detail.getRate());
						params1.put("sort_number_i", detail.getSort_number());
						params1.put("product_vendor_name_i", detail.getVendor_name());
						params1.put("product_name_i", detail.getProduct_name());
						params1.put("specification_i", detail.getSpecification());
						params1.put("unit_i", detail.getUnit());
						switch (type) {
						case DD:
							params1.put("dealer_inner_sn_i", form.getPartA());
							ddMapper.d_add_dealer_dealer_il_product_detail(params1);
							break;
						case DV:
							params1.put("dealer_inner_sn_i", form.getPartA());
							dvMapper.d_add_dealer_vendor_il_product_detail(params1);
							break;
						case HD:
							params1.put("hospital_inner_sn_i", form.getPartA());
							hdMapper.h_add_hospital_dealer_il_product_detail(params1);
							break;
						case HV:
							params1.put("hospital_inner_sn_i", form.getPartA());
							hvMapper.h_add_hospital_vendor_il_product_detail(params1);
							break;
						default:
							break;
						}
						if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
							throw new DBException(MessageUtils.message("db.update.fail","询价单明细",params1.get("message_string_o")));
						}
					}
					InquiryIDForm idForm = new InquiryIDForm(form.getPartA(), form.getIlps_inner_sn(), form.getBillsType());
					return selectInquiryInfo(idForm);
				}else {
					throw new DBException(MessageUtils.message("db.update.fail","询价单"));
				}
				//更改Il_type了则新增
			}else {
				//删除form
				result = result & deleteInquiry(new InquiryIDForm(form.getPartA(), form.getIlps_inner_sn(), form.getBillsType_old()));
				//新增form
				AddInquiryForm addForm = new AddInquiryForm();
				com.yrt.common.utils.bean.BeanUtils.copyBeanProp(addForm, form);
				newInquiry = insertInquiry(addForm);
			}
		}
		return newInquiry;
	}

	@Override
	@Transactional
	public int deleteInquiry(InquiryIDForm form) {
		BillsType type = form.getBillsType();
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA());
		params.put("ilps_inner_sn", form.getIlps_inner_sn());
		switch (type) {
		case DD:
			ddMapper.deleteInquiryDetail(params);
			return ddMapper.deleteInquiry(params);
		case DV:
			dvMapper.deleteInquiryDetail(params);
			return dvMapper.deleteInquiry(params);
		case HD:
			hdMapper.deleteInquiryDetail(params);
			return hdMapper.deleteInquiry(params);
		case HV:
			hvMapper.deleteInquiryDetail(params);
			return hvMapper.deleteInquiry(params);
		default:
			break;
		}
		return 0;
	}

	@Override
	@Transactional
	public int deleteInquiryDetail(InquiryDetailIDForm form) {
		BillsType type = form.getBillsType();
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA());
		params.put("ilps_inner_sn", form.getIlps_inner_sn());
		params.put("detail_inner_sn", form.getDetail_inner_sn());
		switch (type) {
		case DD:
			return ddMapper.deleteInquiryDetail(params);
		case DV:
			return dvMapper.deleteInquiryDetail(params);
		case HD:
			return hdMapper.deleteInquiryDetail(params);
		case HV:
			return hvMapper.deleteInquiryDetail(params);
		default:
			break;
		}
		return 0;
	}

	@Override
	public List<InquirySortPrice> getPriceProductList(SystemType systemType, InquiryPriceSearchForm form) {
		if(systemType.equals(SystemType.VENDOR)) {
			throw new DBException("当前机构不支持该功能!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("product_vendor_inner_sn", form.getProduct_vendor_inner_sn());
		params.put("product_name", form.getProduct_name());
		params.put("specification", form.getSpecification());
		if(form.getIsNew()) {
			switch (systemType) {
			case HOSPITAL:
				return hdMapper.selectProductListSortNewestPrice(params);
			case DEALER:
				return ddMapper.selectProductListSortNewestPrice(params);
			default:
			}
		}else {
			switch (systemType) {
			case HOSPITAL:
				return hdMapper.selectProductListSortLowestPrice(params);
			case DEALER:
				return ddMapper.selectProductListSortLowestPrice(params);
			default:
			}
		}
		throw new DBException("错误的机构类型!");
	}

	@Override
	public List<Map<String, Object>> selectRelatedVendorList(SystemType curOrgSystemType, String name, Boolean is_certifie) {
		Map<String,Object> params = new HashMap<>();
		params.put("name", name);
		params.put("is_certifie", is_certifie);
		switch (curOrgSystemType) {
		case HOSPITAL:
			return hdMapper.selectRelatedVendorList(params);
		case DEALER:
			return ddMapper.selectRelatedVendorList(params);
		default:
		}
		return null;
	}
	
	@Override
	public List<Map<String, Object>> selectRelatedDealerList(SystemType curOrgSystemType, String name, Boolean is_certifie) {
		Map<String,Object> params = new HashMap<>();
		params.put("name", name);
		params.put("is_certifie", is_certifie);
		switch (curOrgSystemType) {
		case HOSPITAL:
			return hdMapper.selectRelatedDealerList(params);
		case DEALER:
			return ddMapper.selectRelatedDealerList(params);
		case VENDOR:
			return dvMapper.selectRelatedDealerList(params);
		default:
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> selectRelatedHospitalList(SystemType curOrgSystemType, String name,
			Boolean is_certifie) {
		Map<String,Object> params = new HashMap<>();
		params.put("name", name);
		params.put("is_certifie", is_certifie);
		switch (curOrgSystemType) {
		case VENDOR:
			return hvMapper.selectRelatedHospitalList(params);
		case DEALER:
			return dvMapper.selectRelatedHospitalList(params);
		default:
		}
		return null;
	}

}
