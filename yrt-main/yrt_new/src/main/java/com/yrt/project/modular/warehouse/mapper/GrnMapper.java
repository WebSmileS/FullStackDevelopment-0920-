package com.yrt.project.modular.warehouse.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.order.domain.PurchasingRecordProduct;
import com.yrt.project.modular.warehouse.domain.Grn;
import com.yrt.project.modular.warehouse.domain.Grn4External;
import com.yrt.project.modular.warehouse.domain.GrnDetail;
import com.yrt.project.modular.warehouse.domain.GrnDetail4External;
import com.yrt.project.modular.warehouse.domain.TransferVoucher;
import com.yrt.project.modular.warehouse.domain.TransferVoucherDetail;

@Repository
public interface GrnMapper {

	//入库单
	public void insertGrn(Map<String,Object> params);
	public int updateGrn(Map<String,Object> params);
	public int deleteGrn(Map<String,Object> params);
	public List<Grn> searchGrnList(Map<String,Object> params);
	public Grn selectGrnInfo(Map<String,Object> params);
	public List<Integer> selectGrnRefVoucherType(Map<String,Object> params);
	
	public void putInStorage(Map<String,Object> params);
	
	//入库单产品明细
	public void insertGrnProductDetail(Map<String,Object> params);
	public int updateGrnProductDetail(Map<String,Object> params);
	public int deleteGrnProductDetail(Map<String,Object> params);
	//入库单经销商自建产品明细
	public void insertGrnDealerProductDetail(Map<String,Object> params);
	public int updateGrnDealerProductDetail(Map<String,Object> params);
	public int deleteGrnDealerProductDetail(Map<String,Object> params);
	
	public List<GrnDetail> selectGrnProductDetails(Map<String,Object> params);
	
	//获取仓库人员
	public List<Map<String,Object>> selectWarehouseEmployeeList(Map<String,Object> params);
	
	//获取合同相关的机构列表(只包含机构ID，机构名称和机构类型字段)
	public List<Map<String, Object>> selectRelatedOrg4DropDownList(Map<String,Object> params);
	public List<Map<String, Object>> selectAllOrg4DropDownList(Map<String,Object> params);
	
	//获取厂商产品列表(平台所有发布产品)
//	public List<Map<String,Object>> selectVendorProductList(Map<String,Object> params);
//	public List<Map<String,Object>> selectContractVendorProductList(Map<String,Object> params);
	//获取经销商自建产品列表
//	public List<Map<String,Object>> selectDealerProductList(Map<String,Object> params);
//	public List<Map<String,Object>> selectContractDealerProductList(Map<String,Object> params);
	
	//可用做调单的出库单列表
	public List<TransferVoucher> selectTransferOdoList(Map<String, Object> params);
	public List<TransferVoucherDetail> selectTransferOdoDetailList(Map<String, Object> params);
	//可用做调单的出库单列表
	public List<TransferVoucher> selectTransferPoList(Map<String, Object> params);
	public List<TransferVoucherDetail> selectTransferPoDetailList(Map<String, Object> params);
	
	//专为外部接口
	public Grn4External selectGrnInfo4External(Map<String,Object> params);
	public List<GrnDetail4External> selectGrnProductDetails4External(Map<String,Object> params);
	
	//针对单机模式查询采购订单与入库单的对应关系
	public BigDecimal sumGrnDetailRefPurchaseOrder(Map<String,Object> params);
	
	//查询入库单产品明细列表
	public List<Map<String,Object>> selectGrnProductList4Return(Map<String,Object> params);
	public List<Map<String,Object>> selectGrnProductList4Invoice(Map<String,Object> params);
	
	public boolean isExistGrnRecord(Map<String, Object> params);
	public List<PurchasingRecordProduct> selectControlUsablePurchasingRecordList(Map<String, Object> params);
	public BigDecimal sumGrnDetailByInspection(Long inspection_detail_id);
	public void updatePurchaseGrnDetailInspectionDetailId(Map<String, Object> params);
}
