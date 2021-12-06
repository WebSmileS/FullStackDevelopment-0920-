package com.yrt.project.api.business.applyvoucher.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.project.api.business.applyvoucher.vo.AppGrantConfirmForm;
import com.yrt.project.api.business.applyvoucher.vo.GrantSendForm;
import com.yrt.project.api.business.applyvoucher.vo.SearchApplyDetailForm;
import com.yrt.project.api.business.applyvoucher.vo.SearchApplyVoucherForm;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucherDetail;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucherDetail;
import com.yrt.project.modular.organization.domain.OrgEmployee;

public interface IHospitalGrantService {
	
	/**
	 * 获取待发放或者已发放申领单列表
	 * @param form
	 * @return
	 */
	public List<HospitalApplyVoucher> searchHospitalApplyVoucherList(SearchApplyVoucherForm form, OrgEmployee makingPeople);
	
	/**
	 * 获取待发放或者已发放申领单列表(手机专用)
	 * @param form
	 * @return
	 */
	public List<HospitalApplyVoucher> searchHospitalApplyVoucherList(Long oid, String key, OrgEmployee makingPeople);
	
	/**
	 * 获取申领单明细列表
	 * @param form
	 * @return
	 */
	public List<HospitalApplyVoucherDetail> selectGrantingVoucherDetailListByWarehouse(SearchApplyDetailForm form);


		/**
         * 获取发放单列表(发放页面使用)
         * @param oid 医院ID
         * @param aid 申领单ID
         * @return
         */
	public List<Map<String, Object>> selectGrantList(Long oid, Long aid, Long wid);
	
	/**
	 * 获取发放单列表(申领页面使用)
	 * @param oid 医院ID
	 * @param aid 申领单ID
	 * @return
	 */
	public List<HospitalGrantVoucher> selectGrantVoucherlList(Long oid, Long aid, Boolean isAll, String key);
	
	/**
	 * 获取发放单列表(手机使用)
	 * @param oid 机构ID
	 * @param pid 申领人ID
	 * @return
	 */
	public List<HospitalGrantVoucher> getHospitalGrandVoucherList4Phone(Long oid, String key, Integer status, OrgEmployee makingPeople);
	
	/**
	 * 根据发放单ID获取发放单明细列表
	 * @param oid 医院ID
	 * @param gid 发放单ID
	 * @return
	 */
	public List<HospitalGrantVoucherDetail> selectGrantDetailList(Long oid, Long gid, String status);
	
	public List<HospitalGrantVoucherDetail> selectGrantDetailList4Phone(Long oid, Long gid, Integer all);

	/**
	 * 发放
	 * @param form
	 * @return
	 */
	public int voucherSend(GrantSendForm form, OrgEmployee makingPeople);
	
	/**
	 * 生成采购计划
	 * @param form
	 * @return
	 */
	public int createPlan(GrantSendForm form, OrgEmployee makingPeople);
	
	/**
	 * 终止申领单
	 * @param oid 医院ID
	 * @param aid 申领单ID
	 * @return
	 */
	public int stopHospitalApplyVoucher(Long oid, Long aid);

	/**
	 * 确认发放
	 * @param oid 医院ID
	 * @param gid 发放单ID
	 * @return
	 */
	public int confirmSendVoucher(Long oid, Long gid, ContextUserInfo curUser);
	
	public int confirmSendVoucher4Phone(AppGrantConfirmForm form, ContextUserInfo curUser);

	/**
	 * 回滚单据状态
	 * @param oid
	 * @param gid
	 * @param detailId
	 */
	public void rollbackVoucherStatus(Long oid, Long gid, Long detailId, int product_type, int status);
	
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
