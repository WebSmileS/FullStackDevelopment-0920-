package com.yrt.project.api.business.order.service;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;
import com.yrt.project.api.business.order.vo.order.BuildSaleOrderForm;
import com.yrt.project.api.business.order.vo.record.AddSaleRecordForm;
import com.yrt.project.api.business.order.vo.record.AppSaleRecordSearchForm;
import com.yrt.project.api.business.order.vo.record.EditSaleRecordForm;
import com.yrt.project.api.business.order.vo.record.SaleRecordIDForm;
import com.yrt.project.api.business.order.vo.record.SearchSaleRecordDetailForm;
import com.yrt.project.api.business.order.vo.record.SearchSaleRecordForm;
import com.yrt.project.api.business.order.vo.record.SearchSaleRecordForm2;
import com.yrt.project.modular.order.domain.SaleRecord;
import com.yrt.project.modular.order.domain.SaleRecordDetail;
import com.yrt.project.modular.order.domain.Sl;
import com.yrt.project.modular.organization.domain.OrgEmployee;

public interface ISaleRecordService {

	//销售单查询
	public List<SaleRecord> selectSaleRecordList(SearchSaleRecordForm form);
	public List<Sl> searchSaleRecordList(SearchSaleRecordForm2 form);
	public List<Sl> searchSaleRecordList4Phone(AppSaleRecordSearchForm form);
	public Sl selectSaleRecordInfo(SaleRecordIDForm form);
	//销售单明细查询
	public List<SaleRecordDetail> selectSaleRecordDetailList(SearchSaleRecordDetailForm form);
	//销售单增删改
	public Sl addSaleRecord(OrgEmployee makingPeople, SystemType systemType, @Valid AddSaleRecordForm form);
	public Long buildSaleOrder(BuildSaleOrderForm form, SystemType systemType);
	public int editSalerecord(@Valid EditSaleRecordForm form);
	public int deleteSaleRecord(SaleRecordIDForm form);
	//调单生成出库单
//	public Odo createOdo(OrgEmployee makingPeople, Integer systemType, Long oid, Long sid, @Valid SaleRecordSaveOdoForm form);
	
	/**
	 * 回滚单据状态
	 * @param oid
	 * @param sid
	 * @param detailId
	 */
	public void rollbackVoucherStatus(Long oid, Long sid, Long detailId, BillsType billsType, int product_type, int status);
	
	/**
	 * 设置出库单生成状态
	 * @param oid
	 * @param sid
	 * @param detailId
	 * @param billsType
	 * @param product_type
	 * @param status
	 */
//	public void stock(Long oid, Long sid, Long detailId, BillsType billsType, int product_type);
	
	/**
	 * 解除库存锁定
	 * @param oid
	 * @param gid
	 * @param detailId
	 */
//	public void unLockInventory(OdoMapper mapper, Long oid, Long sid, Long detailId, BillsType billsType);
	
	/**
	 * 
	 * @param curSystemType 当前机构类型
	 * @param curOrgId 当前机构ID
	 * @param voucher_inner_sn 单据ID
	 * @param voucher_details_inner_sn 单据明细ID
	 * @param lockedQuantity 已锁定数量
	 * @param billsType 单据类型
	 */
	public BigDecimal updateTransferDetail(SystemType curSystemType, Long curOrgId, Long voucher_inner_sn, Long voucher_details_inner_sn,
			BigDecimal lockedQuantity, BillsType billsType, boolean isDelete);
}
