package com.yrt.project.api.business.applyvoucher.service;

import java.util.List;
import java.util.Map;

import com.yrt.project.api.business.applyvoucher.vo.AddHospitalApplyVoucherForm;
import com.yrt.project.api.business.applyvoucher.vo.ApplyIDForm;
import com.yrt.project.api.business.applyvoucher.vo.EditHospitalApplyVoucherForm;
import com.yrt.project.api.business.applyvoucher.vo.SearchApplyVoucherForm;
import com.yrt.project.api.business.applyvoucher.vo.ViewApplyVoucherForm;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucher;
import com.yrt.project.modular.product.domain.ApplyResponseProductInfo;

public interface IHospitalApplyVoucherService {

	public List<HospitalApplyVoucher> searchHospitalApplyVoucherList(SearchApplyVoucherForm form);
	
	public List<HospitalApplyVoucher> searchHospitalApplyVoucherList4Phone(Long oid, String key, Integer status);
	
	public HospitalApplyVoucher addHospitalApplyVoucher(AddHospitalApplyVoucherForm form);
	
	public int updateAddHospitalApplyVoucher(EditHospitalApplyVoucherForm form);
	
	public int deleteHospitalApplyVoucher(Long oid, Long aid);

	public ViewApplyVoucherForm copyHospitalApplyVoucher(Long oid, Long aid);
	
	public int stopHospitalApplyVoucher(Long oid, Long aid);
	
	public List<ApplyResponseProductInfo> selectContractVendorProductList(Map<String,Object> params);

	public List<ApplyResponseProductInfo> selectHospitalApplyResponseProductInfoByOidAndDid(Long oid, Long did, Integer productType);
	
	public List<ApplyResponseProductInfo> selectContractDealerProductList(Map<String,Object> params);

	public ViewApplyVoucherForm selectHospitalApplyVoucherInfo(Long oid, Long aid);

	public int deleteHospitalApplyVoucherDetail(ApplyIDForm form);

	public int stopHospitalApplyVoucherDetail(ApplyIDForm form);

	public List<String> selectBatchNumberList(Long oid, Long vid, Long pid, Long sid, Integer product_type);
}
