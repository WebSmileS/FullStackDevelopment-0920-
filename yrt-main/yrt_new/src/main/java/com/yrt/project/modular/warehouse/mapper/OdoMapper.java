package com.yrt.project.modular.warehouse.mapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.common.dict.RefVoucherType;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucherDetail;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.contract.domain.ContractProduct;
import com.yrt.project.modular.order.domain.SaleRecordDetail;
import com.yrt.project.modular.warehouse.domain.BatchNumberDetail;
import com.yrt.project.modular.warehouse.domain.Odo;
import com.yrt.project.modular.warehouse.domain.Odo4External;
import com.yrt.project.modular.warehouse.domain.OdoDetail;
import com.yrt.project.modular.warehouse.domain.OdoDetail4External;
import com.yrt.project.modular.warehouse.domain.TransferSaleRecord;
import com.yrt.project.modular.warehouse.domain.VoucherDetailQuantity;

@Repository
public interface OdoMapper {

	//出库单
	public void insertOdo(Map<String,Object> params);
	public int updateOdo(Map<String,Object> params);
	public int deleteOdo(Map<String,Object> params);
	public List<Odo> searchOdoList(Map<String,Object> params);
	public Odo selectOdoInfo(Map<String,Object> params);
	
	//出库单产品明细
	public void insertOdoProductDetail(Map<String,Object> params);
//	public int updateOdoProductDetail(Map<String,Object> params);
	public int deleteOdoProductDetail(Map<String,Object> params);
	//出库单经销商自建产品明细
	public void insertOdoDealerProductDetail(Map<String,Object> params);
//	public int updateOdoDealerProductDetail(Map<String,Object> params);
	public int deleteOdoDealerProductDetail(Map<String,Object> params);
	
	public List<OdoDetail> selectOdoProductDetails(Map<String,Object> params);
	
	//获取仓库人员
	public List<Map<String,Object>> selectWarehouseEmployeeList(Map<String,Object> params);
	
	//获取合同相关的机构列表(只包含机构ID，机构名称和机构类型字段)
	public List<Map<String, Object>> selectRelatedOrg4DropDownList(Map<String,Object> params);
	
	//获取厂商产品列表(平台所有发布产品)
	public List<ResponseProductInfo> selectVendorProductList(Map<String,Object> params);
	public List<Map<String,Object>> selectVendorProductList4Phone(Map<String,Object> params);
	//获取经销商自建产品列表
	public List<Map<String,Object>> selectDealerProductList(Map<String,Object> params);
	public List<Map<String,Object>> selectDealerProductList4Phone(Map<String,Object> params);
	
	//获取仓库内产品批号列表
	public List<BatchNumberDetail> selectDealerProductBatchNumList(Map<String,Object> params);
	//获取仓库内自建产品批号列表
	public List<BatchNumberDetail> selectProductBatchNumList(Map<String,Object> params);
	
	//出库
//	public void putout(Map<String,Object> params);
	
	//转换自建产品最小包装单位数量
	public BigDecimal calculateQuantityByDealerProductMinUnit(Map<String,Object> params);
	//转换厂商产品最小包装单位数量
	public BigDecimal calculateQuantityByProductMinUnit(Map<String,Object> params);
	
	default List<VoucherDetailQuantity> getVoucherDetailQuantity(Long oid, RefVoucherType voucherType, Long voucherId, Long voucherDetailId){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("voucherType", voucherType.value());
		params.put("voucherId", voucherId);
		params.put("voucherDetailId", voucherDetailId);
		return selectVoucherDetailQuantity(params);
	}
	//获取所有调单明细数量汇总
	public List<VoucherDetailQuantity> selectVoucherDetailQuantity(Map<String,Object> params);
	
	//可用做调单的销售单列表
	public List<TransferSaleRecord> selectTransferSaleRecordList(Map<String, Object> params);
	public List<SaleRecordDetail> selectTransferSaleRecordDetailList(Map<String, Object> params);
	public List<SaleRecordDetail> selectTransferGroupSaleRecordDetailList(Map<String, Object> params);
	//可用做调单的发放单列表
	public List<HospitalGrantVoucher> selectTransferGrantList(Map<String, Object> params);
	public List<HospitalGrantVoucherDetail> selectTransferGrantDetailList(Map<String, Object> params);
	public List<HospitalGrantVoucherDetail> selectTransferGroupGrantDetailList(Map<String, Object> params);
	
	//专为外部接口
	public Odo4External selectOdoInfo4External(Map<String,Object> params);
	public List<OdoDetail4External> selectOdoProductDetails4External(Map<String,Object> params);

	public List<ContractProduct> selectLastPrice(Map<String, Object> params);
	
	//查询出库单产品明细列表
	public List<Map<String, Object>> selectOdoProductList4Return(Map<String, Object> params);
	public List<Map<String, Object>> selectOdoProductList4Invoice(Map<String, Object> params);
	
	//查询最后一次产品医保编号
	public String selectLastProductHealthCareSn(Map<String, Object> params);
}
