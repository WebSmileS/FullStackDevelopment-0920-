package com.yrt.project.api.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.yrt.common.dict.ApplyDetailStatus;
import com.yrt.common.dict.ApplyRecordStatus;
import com.yrt.common.dict.ArrivalStatus;
import com.yrt.common.dict.BaseIntEnum;
import com.yrt.common.dict.BaseIntEnum.EnumItemDesc;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.ContractAuditStatus;
import com.yrt.common.dict.ContractStatus;
import com.yrt.common.dict.DealerCertType;
import com.yrt.common.dict.EducationDegree;
import com.yrt.common.dict.EmployeeStatus;
import com.yrt.common.dict.EnterpriseType;
import com.yrt.common.dict.GrantRecordStatus;
import com.yrt.common.dict.GrnBarcodeType;
import com.yrt.common.dict.GrnOdoDetailType;
import com.yrt.common.dict.GrnStatus;
import com.yrt.common.dict.HospitalCertType;
import com.yrt.common.dict.InquiryRecordStatus;
import com.yrt.common.dict.InspectionStatus;
import com.yrt.common.dict.InventoryPrdType;
import com.yrt.common.dict.InventoryStatus;
import com.yrt.common.dict.MarriageStatus;
import com.yrt.common.dict.OdoStatus;
import com.yrt.common.dict.OperationMode;
import com.yrt.common.dict.OrgAuditStatus;
import com.yrt.common.dict.OrgSource;
import com.yrt.common.dict.OrgStatus;
import com.yrt.common.dict.OrthopaedicIntentionStatus;
import com.yrt.common.dict.OrthopaedicOpPackageType;
import com.yrt.common.dict.OrthopaedicOperationType;
import com.yrt.common.dict.PersonCertificateType;
import com.yrt.common.dict.PrdCategory;
import com.yrt.common.dict.PrdCertType;
import com.yrt.common.dict.PrdEditAuditLevel;
import com.yrt.common.dict.PrdEditOperation;
import com.yrt.common.dict.PrdEditStatus;
import com.yrt.common.dict.PrdHealthCareType;
import com.yrt.common.dict.PrdPublishStatus;
import com.yrt.common.dict.PrdStorageCondition;
import com.yrt.common.dict.PrdType;
import com.yrt.common.dict.PrdUsageType;
import com.yrt.common.dict.PurcharseOrderStatus;
import com.yrt.common.dict.PurchaseOrderDetailStatus;
import com.yrt.common.dict.PurchasePlanDetailStatus;
import com.yrt.common.dict.PurchasePlanStatus;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SalesOrderDetailStatus;
import com.yrt.common.dict.SasDetailStatus;
import com.yrt.common.dict.SasStatus;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.TransferVoucherStatus;
import com.yrt.common.dict.UserType;
import com.yrt.common.dict.VendorCertType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.dict.WarehouseType;
import com.yrt.common.dict.WharehouseEmployeeType;
import com.yrt.framework.web.domain.ResultList;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/dict") // gateway 
@Api(tags = "common-dict 枚举常量")
public class DictController {

	// 1. org/user///////////////////////////////////////////////////////////////////
	@GetMapping("systemType")
	@ApiOperation("1.systemType(系统 类型)")
	@ApiOperationSupport(order=100)
	public ResultList<EnumItemDesc> systemType() {
		return ResultList.success(BaseIntEnum.listElements(SystemType.class));
	}

	@GetMapping("orgType")
	@ApiOperation("1.1.orgType(机构 类型, alias: systemType)")
	@ApiOperationSupport(order=110)
	public ResultList<EnumItemDesc> orgType() {
		return ResultList.success(BaseIntEnum.listElements(SystemType.class));
	}

	@GetMapping("orgStatus")
	@ApiOperation("1.2.orgStatus(机构 状态)")
	@ApiOperationSupport(order=120)
	public ResultList<EnumItemDesc> orgStatus() {
		return ResultList.success(BaseIntEnum.listElements(OrgStatus.class));
	}

	@GetMapping("orgAuditStatus")
	@ApiOperation("1.3.orgAuditStatus(机构 审核 状态)")
	@ApiOperationSupport(order=130)
	public ResultList<EnumItemDesc> orgAuditStatus() {
		return ResultList.success(BaseIntEnum.listElements(OrgAuditStatus.class));
	}

	@GetMapping("userType")
	@ApiOperation("1.4.userType(用户类型)")
	@ApiOperationSupport(order=140)
	public ResultList<EnumItemDesc> userType() {
		return ResultList.success(BaseIntEnum.listElements(UserType.class));
	}

	@GetMapping("educationDegree")
	@ApiOperation("1.4.1.educationDegree(员工学历)")
	@ApiOperationSupport(order=141)
	public ResultList<EnumItemDesc> educationDegree() {
		return ResultList.success(BaseIntEnum.listElements(EducationDegree.class));
	}

	@GetMapping("marriageStatus")
	@ApiOperation("1.4.2.marriageStatus(婚姻状况)")
	@ApiOperationSupport(order=142)
	public ResultList<EnumItemDesc> marriageStatus() {
		return ResultList.success(BaseIntEnum.listElements(MarriageStatus.class));
	}

	@GetMapping("employeeStatus")
	@ApiOperation("1.4.3.employeeStatus(员工状态)")
	@ApiOperationSupport(order=143)
	public ResultList<EnumItemDesc> employeeStatus() {
		return ResultList.success(BaseIntEnum.listElements(EmployeeStatus.class));
	}

	@GetMapping("personCertificateType")
	@ApiOperation("1.4.4.personCertificateType(员工证件类别)")
	@ApiOperationSupport(order=144)
	public ResultList<EnumItemDesc> personCertificateType() {
		return ResultList.success(BaseIntEnum.listElements(PersonCertificateType.class));
	}

	@GetMapping("enterpriseType")
	@ApiOperation("1.5.enterpriseType(机构类型)")
	@ApiOperationSupport(order=150)
	public ResultList<EnumItemDesc> enterpriseType() {
		return ResultList.success(BaseIntEnum.listElements(EnterpriseType.class));
	}

	@GetMapping("dealerCertType")
	@ApiOperation("1.5.1.dealerCertType(经销商图片类型)")
	@ApiOperationSupport(order=151)
	public ResultList<EnumItemDesc> dealerCertType() {
		return ResultList.success(BaseIntEnum.listElements(DealerCertType.class));
	}

	@GetMapping("hospitalCertType")
	@ApiOperation("1.5.2.hospitalCertType(医院图片类型)")
	@ApiOperationSupport(order=152)
	public ResultList<EnumItemDesc> hospitalCertType() {
		return ResultList.success(BaseIntEnum.listElements(HospitalCertType.class));
	}

	@GetMapping("vendorCertType")
	@ApiOperation("1.5.3.vendorCertType(厂商证书类型)")
	@ApiOperationSupport(order=153)
	public ResultList<EnumItemDesc> vendorCertType() {
		return ResultList.success(BaseIntEnum.listElements(VendorCertType.class));
	}
	
	@GetMapping("operationMode")
	@ApiOperation("1.5.4.operationMode(经营方式)")
	@ApiOperationSupport(order=154)
	public ResultList<EnumItemDesc> operationMode() {
		return ResultList.success(BaseIntEnum.listElements(OperationMode.class));
	}

	@GetMapping("orgSource")
	@ApiOperation("1.5.5.orgSource(机构信息来源)")
	@ApiOperationSupport(order=155)
	public ResultList<EnumItemDesc> orgSource() {
		return ResultList.success(BaseIntEnum.listElements(OrgSource.class));
	}
	
	@GetMapping("billsType")
	@ApiOperation("1.6.billsType(单据类型)")
	@ApiOperationSupport(order=160)
	public ResultList<EnumItemDesc> billsType() {
		return ResultList.success(BaseIntEnum.listElements(BillsType.class));
	}

	// 2.product////////////////////////////////////////////////////////////////////////
	@GetMapping("prdType")
	@ApiOperation("2.prdType(产品类型)")
	@ApiOperationSupport(order=200)
	public ResultList<EnumItemDesc> prdType() {
		return ResultList.success(BaseIntEnum.listElements(PrdType.class));
	}

	@GetMapping("prdCategory")
	@ApiOperation("2.1.prdCategory(医疗器械分类目录类别)")
	@ApiOperationSupport(order=210)
	public ResultList<EnumItemDesc> prdCategory() {
		return ResultList.success(BaseIntEnum.listElements(PrdCategory.class));
	}

	@GetMapping("prdCertType")
	@ApiOperation("2.2.prdCertType(产品图片类型)")
	@ApiOperationSupport(order=220)
	public ResultList<EnumItemDesc> prdCertType() {
		return ResultList.success(BaseIntEnum.listElements(PrdCertType.class));
	}

	@GetMapping("prdHealthCareType")
	@ApiOperation("2.3.prdHealthCareType(产品医保类型)")
	@ApiOperationSupport(order=230)
	public ResultList<EnumItemDesc> prdHealthCareType() {
		return ResultList.success(BaseIntEnum.listElements(PrdHealthCareType.class));
	}

	@GetMapping("prdStorageCondition")
	@ApiOperation("2.4.prdStorageCondition(产品存储条件)")
	@ApiOperationSupport(order=240)
	public ResultList<EnumItemDesc> prdStorageCondition() {
		return ResultList.success(BaseIntEnum.listElements(PrdStorageCondition.class));
	}

	@GetMapping("prdUsageType")
	@ApiOperation("2.5.prdUsageType(产品使用方式)")
	@ApiOperationSupport(order=250)
	public ResultList<EnumItemDesc> prdUsageType() {
		return ResultList.success(BaseIntEnum.listElements(PrdUsageType.class));
	}

	@GetMapping("prdPublishStatus")
	@ApiOperation("2.6.prdPublishStatus(产品发布状态)")
	@ApiOperationSupport(order=260)
	public ResultList<EnumItemDesc> prdPublishStatus() {
		return ResultList.success(BaseIntEnum.listElements(PrdPublishStatus.class));
	}

	@GetMapping("prdEditAuditLevel")
	@ApiOperation("2.7.prdEditAuditLevel(经销商编辑产品操作审核级别)")
	@ApiOperationSupport(order=270)
	public ResultList<EnumItemDesc> prdEditAuditLevel() {
		return ResultList.success(BaseIntEnum.listElements(PrdEditAuditLevel.class));
	}

	@GetMapping("prdEditOperation")
	@ApiOperation("2.9.1.prdEditOperation(经销商编辑产品操作类型)")
	@ApiOperationSupport(order=291)
	public ResultList<EnumItemDesc> PrdEditOperation() {
		return ResultList.success(BaseIntEnum.listElements(PrdEditOperation.class));
	}

	@GetMapping("prdEditStatus")
	@ApiOperation("2.9.2.prdEditStatus(经销商编辑产品状态)")
	@ApiOperationSupport(order=292)
	public ResultList<EnumItemDesc> prdEditStatus() {
		return ResultList.success(BaseIntEnum.listElements(PrdEditStatus.class));
	}

	// 3.warehouse///////////////////////////////////////////////////////////////////////
	@GetMapping("warehouseType")
	@ApiOperation("3.1.warehouseType(仓库 类型)")
	@ApiOperationSupport(order=310)
	public ResultList<EnumItemDesc> warehouseType() {
		return ResultList.success(BaseIntEnum.listElements(WarehouseType.class));
	}

	@GetMapping("inventoryPrdType")
	@ApiOperation("3.2.inventoryPrdType(库存产品列表type)")
	@ApiOperationSupport(order=320)
	public ResultList<EnumItemDesc> inventoryPrdType() {
		return ResultList.success(BaseIntEnum.listElements(InventoryPrdType.class));
	}

	@GetMapping("inventoryStatus")
	@ApiOperation("3.3.inventoryStatus(库存产品状态)")
	@ApiOperationSupport(order=330)
	public ResultList<EnumItemDesc> inventoryStatus() {
		return ResultList.success(BaseIntEnum.listElements(InventoryStatus.class));
	}
	

	@GetMapping("wharehouseEmployeeType")
	@ApiOperation("3.4.wharehouseEmployeeType(仓库人员 类型)")
	@ApiOperationSupport(order=340)
	public ResultList<EnumItemDesc> wharehouseEmployeeType() {
		return ResultList.success(BaseIntEnum.listElements(WharehouseEmployeeType.class));
	}

	// 4.contract/////////////////////////////////////////////////////////////////////////

	@GetMapping("contractStatus")
	@ApiOperation("4.1.contractStatus(合同状态)")
	@ApiOperationSupport(order=410)
	public ResultList<EnumItemDesc> contractStatus() {
		return ResultList.success(BaseIntEnum.listElements(ContractStatus.class));
	}

	@GetMapping("contractAuditStatus")
	@ApiOperation("4.1.1.contractAuditStatus(合同审批状态)")
	@ApiOperationSupport(order=411)
	public ResultList<EnumItemDesc> contractAuditStatus() {
		return ResultList.success(BaseIntEnum.listElements(ContractAuditStatus.class));
	}

	@GetMapping("inquiryRecordStatus")
	@ApiOperation("4.2.inquiryRecordStatus(询价状态)")
	@ApiOperationSupport(order=420)
	public ResultList<EnumItemDesc> inquiryRecordStatus() {
		return ResultList.success(BaseIntEnum.listElements(InquiryRecordStatus.class));
	}

	@GetMapping("orthopaedicIntentionStatus")
	@ApiOperation("4.3.orthopaedicIntentionStatus(骨科合作意向状态)")
	@ApiOperationSupport(order=430)
	public ResultList<EnumItemDesc> orthopaedicIntentionStatus() {
		return ResultList.success(BaseIntEnum.listElements(OrthopaedicIntentionStatus.class));
	}

	@GetMapping("orthopaedicOperationType")
	@ApiOperation("4.4.orthopaedicOperationType(骨科手术类别)")
	@ApiOperationSupport(order=440)
	public ResultList<EnumItemDesc> orthopaedicOperationType() {
		return ResultList.success(BaseIntEnum.listElements(OrthopaedicOperationType.class));
	}

	@GetMapping("orthopaedicOpPackageType")
	@ApiOperation("4.5.orthopaedicOpPackageType(骨科手术包产品明细类型)")
	@ApiOperationSupport(order=450)
	public ResultList<EnumItemDesc> orthopaedicOpPackageType() {
		return ResultList.success(BaseIntEnum.listElements(OrthopaedicOpPackageType.class));
	}

	// 5.vouch/////////////////////////////////////////////////////////////////////////////////
	@GetMapping("voucherType")
	@ApiOperation("5. voucherType(单据类型)")
	@ApiOperationSupport(order=500)
	public ResultList<EnumItemDesc> voucherType() {
		//using voucher prefix as document.
		return ResultList.success(VoucherType.descs());
	}
	

	@GetMapping("grnStatus")
	@ApiOperation("5.1.grnStatus(入库单状态)")
	@ApiOperationSupport(order=510)
	public ResultList<EnumItemDesc> grnStatus() {
		return ResultList.success(BaseIntEnum.listElements(GrnStatus.class));
	}

	@GetMapping("grnDetailType")
	@ApiOperation("5.1.1.grnDetailType(入库单明细类型)")
	@ApiOperationSupport(order=511)
	public ResultList<EnumItemDesc> grnDetailType() {
		return ResultList.success(BaseIntEnum.listElements(GrnOdoDetailType.class));
	}
	
	@GetMapping("grnBarcodeType")
	@ApiOperation("5.1.2.grnBarcodeType(入库条码类型)")
	@ApiOperationSupport(order=512)
	public ResultList<EnumItemDesc> grnBarcodeType() {
		return ResultList.success(BaseIntEnum.listElements(GrnBarcodeType.class));
	}
	
	@GetMapping("grantRecordStatus")
	@ApiOperation("5.1.3.grantRecordStatus(医院发放单状态)")
	@ApiOperationSupport(order=513)
	public ResultList<EnumItemDesc> grantRecordStatus() {
		return ResultList.success(BaseIntEnum.listElements(GrantRecordStatus.class));
	}

	@GetMapping("odoStatus")
	@ApiOperation("5.2.odoStatus(出库单状态)")
	@ApiOperationSupport(order=520)
	public ResultList<EnumItemDesc> odoStatus() {
		return ResultList.success(BaseIntEnum.listElements(OdoStatus.class));
	}
	
	@GetMapping("applyRecordStatus")
	@ApiOperation("5.3.applyRecordStatus(医院申领单状态)")
	@ApiOperationSupport(order=530)
	public ResultList<EnumItemDesc> applyRecordStatus() {
		return ResultList.success(BaseIntEnum.listElements(ApplyRecordStatus.class));
	}

	@GetMapping("applyDetailStatus")
	@ApiOperation("5.3.1.applyDetailStatus(医院申领单产品明细状态)")
	@ApiOperationSupport(order=531)
	public ResultList<EnumItemDesc> applyDetailStatus() {
		return ResultList.success(BaseIntEnum.listElements(ApplyDetailStatus.class));
	}
	
	@GetMapping("inspectionRecordStatus")
	@ApiOperation("5.4.inspectionRecordStatus(验货单状态)")
	@ApiOperationSupport(order=540)
	public ResultList<EnumItemDesc> inspectionRecordStatus() {
		return ResultList.success(BaseIntEnum.listElements(InspectionStatus.class));
	}

	
	@GetMapping("salesOrderStatus")
	@ApiOperation("5.5.salesOrderStatus(销售订单状态)")
	@ApiOperationSupport(order=550)
	public ResultList<EnumItemDesc> SalesOrderStatus() {
		return ResultList.success(BaseIntEnum.listElements(SalesOrderDetailStatus.class));
	}

	@GetMapping("salesOrderDetailStatus")
	@ApiOperation("5.5.1.salesOrderDetailStatus(销售订单明细状态)")
	@ApiOperationSupport(order=551)
	public ResultList<EnumItemDesc> salesOrderDetailStatus() {
		return ResultList.success(BaseIntEnum.listElements(SalesOrderDetailStatus.class));
	}

	@GetMapping("purchasePlanStatus")
	@ApiOperation("5.6.purchasePlanStatus(采购计划状态)")
	@ApiOperationSupport(order=560)
	public ResultList<EnumItemDesc> purchasePlanStatus() {
		return ResultList.success(BaseIntEnum.listElements(PurchasePlanStatus.class));
	}

	@GetMapping("purchasePlanDetailStatus")
	@ApiOperation("5.6.1.purchasePlanDetailStatus(采购计划明细状态)")
	@ApiOperationSupport(order=561)
	public ResultList<EnumItemDesc> purchasePlanDetailStatus() {
		return ResultList.success(BaseIntEnum.listElements(PurchasePlanDetailStatus.class));
	}

	@GetMapping("purcharseOrderStatus")
	@ApiOperation("5.6.2.purcharseOrderStatus(采购订单状态)")
	@ApiOperationSupport(order=562)
	public ResultList<EnumItemDesc> purcharseOrderStatus() {
		return ResultList.success(BaseIntEnum.listElements(PurcharseOrderStatus.class));
	}

	@GetMapping("purchaseOrderDetailStatus")
	@ApiOperation("5.6.3.purchaseOrderDetailStatus(采购订单明细状态)")
	@ApiOperationSupport(order=563)
	public ResultList<EnumItemDesc> purchaseOrderDetailStatus() {
		return ResultList.success(BaseIntEnum.listElements(PurchaseOrderDetailStatus.class));
	}
	
	@GetMapping("sasStatus")
	@ApiOperation("5.7.sasStatus(储位分配单状态)")
	@ApiOperationSupport(order=570)
	public ResultList<EnumItemDesc> sasStatus() {
		return ResultList.success(BaseIntEnum.listElements(SasStatus.class));
	}

	@GetMapping("sasDetailStatus")
	@ApiOperation("5.7.1.sasDetailStatus(储位分配单明细状态)")
	@ApiOperationSupport(order=571)
	public ResultList<EnumItemDesc> sasDetailStatus() {
		return ResultList.success(BaseIntEnum.listElements(SasDetailStatus.class));
	}

	@GetMapping("arrivalStatus")
	@ApiOperation("5.8.arrivalStatus(收货单状态)")
	@ApiOperationSupport(order=572)
	public ResultList<EnumItemDesc> arrivalStatus() {
		return ResultList.success(BaseIntEnum.listElements(ArrivalStatus.class));
	}
	
	@GetMapping("refVoucherType")
	@ApiOperation("6.refVoucherType(关联单据类型)")
	@ApiOperationSupport(order=600)
	public ResultList<EnumItemDesc> refVoucherType() {
		return ResultList.success(BaseIntEnum.listElements(RefVoucherType.class));
	}
	
	@GetMapping("transferVoucherStatus")
	@ApiOperation("6.1.transferVoucherStatus(关联单据状态)")
	@ApiOperationSupport(order=601)
	public ResultList<EnumItemDesc> transferVoucherStatus() {
		return ResultList.success(BaseIntEnum.listElements(TransferVoucherStatus.class));
	}
}
