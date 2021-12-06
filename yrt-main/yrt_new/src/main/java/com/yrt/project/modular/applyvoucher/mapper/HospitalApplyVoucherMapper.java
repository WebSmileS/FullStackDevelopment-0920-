package com.yrt.project.modular.applyvoucher.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucherDetail;
import com.yrt.project.modular.product.domain.ApplyResponseProductInfo;

/**
 * 医院领用申请单记录
 * @author Runner
 *
 */
@Repository
public interface HospitalApplyVoucherMapper {

	public void h_add_hospital_apply_voucher(Map<String, Object> params);
	
	public void h_add_hospital_apply_voucher_dealer_product_detail(Map<String, Object> params);
	
	public void h_add_hospital_apply_voucher_product_detail(Map<String, Object> params);
	
	public int updateHospitalApplyVoucher(Map<String, Object> params);
	
	public int trueDeleteHospitalApplyVoucher(Map<String, Object> params);
	
	public int updateHospitalApplyVoucherDealerProduct(Map<String, Object> params);
	
	public int updateHospitalApplyVoucherProduct(Map<String, Object> params);
	
	public int deleteHospitalApplyVoucherDealerProduct(Map<String, Object> params);
	
	public int deleteHospitalApplyVoucherProduct(Map<String, Object> params);
	
	public HospitalApplyVoucher selectHospitalApplyVoucherInfo(Map<String, Object> params);
	
	public List<HospitalApplyVoucher> searchHospitalApplyVoucherList(Map<String, Object> params);
	
	public List<HospitalApplyVoucher> appSearchHospitalApplyVoucherList(Map<String, Object> params);
	
	public List<HospitalApplyVoucherDetail> searchHospitalApplyVoucherDetailList(Map<String, Object> params);

	public List<HospitalApplyVoucherDetail> copyHospitalApplyVoucherDetailList(Map<String, Object> params);
	
	//获取有效合同中厂商产品列表
	public List<ApplyResponseProductInfo> selectContractVendorProductList(Map<String,Object> params);
	
	//获取有效合同中经销商自建产品列表
	public List<ApplyResponseProductInfo> selectContractDealerProductList(Map<String,Object> params);
	
	//获取仓库中存在的产品批号列表
	public List<String> selectVendorBatchNumberList(Map<String,Object> params);
	public List<String> selectDealerBatchNumberList(Map<String,Object> params);

	List<HospitalApplyVoucherDetail> searchHospitalApplyDetailListByOidAndDid(Map<String, Object> params);

	public List<ApplyResponseProductInfo> selectContractVendorProductListByIds(Map<String, Object> params);

	public List<ApplyResponseProductInfo> selectContractDealerProductListByIds(Map<String, Object> params);

}
