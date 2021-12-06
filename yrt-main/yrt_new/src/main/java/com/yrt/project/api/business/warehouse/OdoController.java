package com.yrt.project.api.business.warehouse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.constant.Constants;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.PrdType;
import com.yrt.common.dict.ProductSnType;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.DBException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.Utils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.interceptor.annotation.RepeatSubmit;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.warehouse.service.IOdoService;
import com.yrt.project.api.business.warehouse.service.IOdoTypeService;
import com.yrt.project.api.business.warehouse.service.InventoryLockService;
import com.yrt.project.api.business.warehouse.vo.ProductHealthCareSnForm;
import com.yrt.project.api.business.warehouse.vo.odo.AddOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.AddOdoTypeForm;
import com.yrt.project.api.business.warehouse.vo.odo.EditOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.EditOdoTypeForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoListIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchBatchNumForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoProductForm;
import com.yrt.project.api.common.service.CommService;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucherDetail;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.order.domain.SaleRecordDetail;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.product.domain.ProductSn;
import com.yrt.project.modular.warehouse.domain.BatchNumberDetail;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.domain.Odo;
import com.yrt.project.modular.warehouse.domain.OdoType;
import com.yrt.project.modular.warehouse.domain.TransferSaleRecord;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/odo")
@Api(tags = "业务-出库单管理接口")
public class OdoController extends BaseController {

	@Autowired private IOdoService service;
	@Autowired private IOdoTypeService typeService;
	@Autowired private CommService commService;

	// 出库单类型接口
	@Log(title = "business.odotype", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/type/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增出库单类型", notes = "新增出库单类型")
	public ResultInfo<OdoType> insertOdoType(@Validated AddOdoTypeForm form) {
		return ResultInfo.success(typeService.insertOdoType(form));
	}

	@Log(title = "business.odotype", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/type/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑出库单类型", notes = "编辑出库单类型")
	public AjaxResult updateOdoType(@Validated EditOdoTypeForm form) {
		return toAjax(typeService.updateOdoType(form));
	}

	@Log(title = "business.odotype", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/type/del/{oid}/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除出库单类型", notes = "删除出库单类型")
	public AjaxResult deleteOdoType(@ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
			@ApiParam(name = "id", value = "出库单类型ID", required = true) @PathVariable Long id) {
		return toAjax(typeService.deleteOdoType(oid, id));
	}

	@SuppressWarnings("static-access")
	@Log(title = "business.odotype", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/type/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "查询出库单类型列表", notes = "查询出库单类型列表")
	public ResultList<OdoType> getOdoTypeList(@ApiParam(name = "oid", value = "当前机构ID", required = true) @PathVariable Long oid,
			@ApiParam(name = "status", value = "出库单状态不传查询全部，传1则只查询可用的", required = false) Integer status) {
		return new ResultList<OdoType>().success(typeService.selectOdoTypeList(oid, status));
	}

	// 出库单接口
	@Log(title = "business.odo", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增出库单", notes = "新增出库单")
	public ResultInfo<Odo> insertOdo(@RequestBody @Valid AddOdoForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.insertOdo(curUser, ServletUtils.getCurUserSystemType(), form));
	}

	@Log(title = "business.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑出库单", notes = "编辑出库单")
	public ResultInfo<Odo> updateOdo(@RequestBody @Valid EditOdoForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.updateOdo(form, curUser));
	}

	@Log(title = "business.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除出库单", notes = "删除出库单")
	public AjaxResult deleteOdo(@Validated OdoIDForm form) {
		return toAjax(service.deleteOdo(form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "搜索出库单列表(分页)", notes = "搜索出库单列表(分页)")
	public TableDataInfo<Odo> searchOdoList(@Validated SearchOdoForm form) {
		List<Odo> res = null;
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		startPage();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.searchOdoList(form, warehouseIds);
		}
		return getDataTable(res);
	}

	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取出库单详情", notes = "获取出库单详情")
	public ResultInfo<Odo> getOdoInfo(@Validated OdoIDForm form) {
		return ResultInfo.success(service.selectOdoInfo(form));
	}

	@RepeatSubmit
	@Log(title = "business.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/putout", method = RequestMethod.PUT)
	@ApiOperation(value = "出库", notes = "出库")
	public AjaxResult odoPutOut(@RequestBody @Valid OdoListIDForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		String msg = service.putout(form, curUser, false);
		return success(msg);
	}
	
	@Log(title = "business.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/printed", method = RequestMethod.PUT)
	@ApiOperation(value = "打印完成", notes = "打印完成")
	public AjaxResult printed(
			@Validated OdoIDForm form) {
		service.printed(form);
		return success();
	}

	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/we/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "获取仓库人员", notes = "获取仓库人员")
	public AjaxResult getWarehouseEmployeeList(
			@ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
			@ApiParam(name = "wid", value = "仓库ID", required = true) Long wid) {
		return success().put("info", service.getWarehouseEmployeeList(oid, wid));
	}

	// 获取合同相关的机构列表(只包含机构ID，机构名称和机构类型字段)
	@SuppressWarnings("unchecked")
	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/org/list/{systemType}/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "相关机构列表(分页)", notes = "相关机构列表(分页)")
	public TableDataInfo<Map<String, Object>> getDealerProductList(
			@ApiParam(name = "systemType", value = "相关机构类型 1-医院 、 2-经销商、 3-厂商", required = true) @PathVariable int systemType,
			@ApiParam(name = "oid", value = "当前机构ID", required = true) @PathVariable Long oid,
			@ApiParam(name = "name", value = "搜索机构名称", required = false) String name) {
		startPage();
		return getDataTable(commService.selectRelatedOrg4DropDownList(convertSystemType(systemType), oid, name, false, false, true));
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/vendor/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取厂商产品列表(分页)", notes = "获取厂商产品列表(分页)")
	public TableDataInfo<ResponseProductInfo> getVendorProductList(
			@Validated SearchOdoProductForm form) {
		List<ResponseProductInfo> res;
		List<Long> warehouseIds = new ArrayList<>();
		if (Utils.isEmpty(form.getWarehouse_inner_sn())) {
			warehouseIds = getCurUserWarehouseIds();
			if(warehouseIds.isEmpty()) {
				return getDataTable(new ArrayList<>());
			}
		}else {
			warehouseIds.add(form.getWarehouse_inner_sn());
		}
		startPage();
		res = service.selectVendorProductList(form, warehouseIds);
		return getDataTable(res);
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/dealer/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取经销商自建产品列表(分页)", notes = "获取经销商自建产品列表(分页)")
	public TableDataInfo<Map<String, Object>> getDealerProductList(@Validated SearchOdoProductForm form) {
		startPage();
		return getDataTable(service.selectDealerProductList(form));
	}
	
	@Autowired InventoryLockService inventoryLocker;
	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/batch/list", method = RequestMethod.POST)
	@ApiOperation(value = "获取仓库内产品批号列表", notes = "获取仓库内产品批号列表")
	public AjaxResult getProductBatchNumList(@Validated @RequestBody SearchBatchNumForm form) {
		InventoryKey key;
		SystemType systemType = ServletUtils.getCurUserSystemType();
		if (form.getProduct_type().intValue() == 1) {
			key = InventoryKey.of(systemType, PrdType.VENDOR_PRD, form.getOrg_id(), form.getWarehouse_inner_sn(),
					form.getProduct_vendor_inner_sn(), form.getProduct_inner_sn(), form.getSpecification_inner_sn(),
					Constants.PRE_LOCK_BATCH_NUMBER);
		} else {
			key = InventoryKey.of(systemType, PrdType.DELEAR_PRD, form.getOrg_id(), form.getWarehouse_inner_sn(),
					form.getProduct_vendor_inner_sn(), form.getProduct_inner_sn(), form.getSpecification_inner_sn(),
					Constants.PRE_LOCK_BATCH_NUMBER);
		}
		InventoryKey specKey = InventoryKey.of(key);
		//库存余量(库存总量-总锁定量)
		BigDecimal inventoryBannce = inventoryLocker.getInventoryTotalBannce(key);
		//如果是调单情况，需要在库存余量上增加调单余量
		RefVoucherType voucherType = form.getVoucher_type_c();
		if (voucherType != RefVoucherType.NONE) {
			// 如果有关联单说明是调单生成的数据，需要在库存余量上增加调单余量
			BigDecimal banlance = BigDecimal.ZERO;
			if(form.getVoucher_inner_sn_c() != null || form.getVoucher_detail_inner_sn_c() != null) {
				switch (voucherType) {
					case GRANT:
						List<HospitalGrantVoucherDetail> grantDetails = service.getTransferGroupGrantDetailList(systemType,
								form.getOrg_id(), form.getVoucher_inner_sn_c(), specKey);
						if(Utils.allEmpty(grantDetails)) {
							banlance = BigDecimal.ZERO;
						}else {
							HospitalGrantVoucherDetail grantDetail = grantDetails.get(0);
							banlance = service.calculateQuantityByProductMinUnit(grantDetail.getVendor_inner_sn(),
									grantDetail.getProduct_inner_sn(), grantDetail.getSpecification_inner_sn(),
									grantDetail.getUnit_inner_sn(), grantDetail.getProduct_type(), grantDetail.getGrant_quantity());
						}
						break;
					case SALE_RECORD:
						List<SaleRecordDetail> saleDetails = service
								.getTransferSaleRecordDetailList(systemType, form.getOrg_id(), form.getVoucher_inner_sn_c(),
										form.getVoucher_detail_inner_sn_c(), form.getBillsType());
						if(Utils.allEmpty(saleDetails)) {
							banlance = BigDecimal.ZERO;
						}else {
							SaleRecordDetail saleDetail = saleDetails.get(0);
							banlance = service.calculateQuantityByProductMinUnit(saleDetail.getProduct_vendor_inner_sn(),
									saleDetail.getProduct_inner_sn(), saleDetail.getSpecification_inner_sn(),
									saleDetail.getUnit_inner_sn(), saleDetail.getProduct_type(), saleDetail.getToBeOutOfStock_quantity());
						}
						break;
				}
			}
			inventoryBannce = inventoryBannce.add(banlance);
		}
		List<BatchNumberDetail> list = service.selectProductBatchNumList(form, specKey);
		return success().put("inventoryBannce", inventoryBannce).put("list", list);
	}
	
	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sheet/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取出库单可调单类型列表", notes = "获取出库单可调单类型列表")
	public AjaxResult getOrderSheetList() {
		List<RefVoucherType> list = new ArrayList<>();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case HOSPITAL:
			list.add(RefVoucherType.GRANT);
			list.add(RefVoucherType.ALLOT);
			break;
		case DEALER:
			list.add(RefVoucherType.SALE_RECORD);
			list.add(RefVoucherType.ALLOT);
			break;
		case VENDOR:
			list.add(RefVoucherType.SALE_RECORD);
			break;
		default:
		}
		return success().put("list", list);
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/record/list", method = RequestMethod.GET)
	@ApiOperation(value = "可用做出库调单的销售单列表", notes = "可用做出库调单的销售单列表")
	public TableDataInfo<TransferSaleRecord> getTransferSaleRecordList(
			@ApiParam(name="key",value="搜索关键字(销售单单号)",required=false) String key,
			@ApiParam(name="key1",value="搜索关键字(产品名字)",required=false) String key1,
			@ApiParam(name="organizationName",value="搜索相关机构名称",required=false) String organizationName) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.getTransferSaleRecordList(ctxUser.getSystemType(), ctxUser.getOrgId(), key,key1,organizationName));
	}
	
	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/record/detail", method = RequestMethod.GET)
	@ApiOperation(value = "被调单的销售单明细列表", notes = "被调单的销售单明细列表")
	public ResultList<SaleRecordDetail> getTransferSaleRecordDetailList(
			@ApiParam(name="sid",value="销售单内部编号(sl_inner_sn)",required=true) @RequestParam(required=true)Long sid,
			@ApiParam(name="billsType",value="单据类型",required=true) @RequestParam(required=true)BillsType billsType) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		return ResultList.success(service.getTransferSaleRecordDetailList(ctxUser.getSystemType(), ctxUser.getOrgId(), sid, null, billsType));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/grant/list", method = RequestMethod.GET)
	@ApiOperation(value = "可用做出库调单的发放单列表", notes = "可用做出库调单的发放单列表")
	public TableDataInfo<HospitalGrantVoucher> getTransferGrantList(
			@ApiParam(name="key",value="搜索关键字(发放单单号)",required=false) String key,
			@ApiParam(name="key1",value="搜索关键字(产品名字)",required=false) String key1) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.getTransferGrantList(ctxUser.getSystemType(), ctxUser.getOrgId(), key,key1));
	}
	
	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/grant/detail", method = RequestMethod.GET)
	@ApiOperation(value = "被调单的发放单发放单明细列表", notes = "被调单的发放单明细列表")
	public ResultList<HospitalGrantVoucherDetail> getTransferGrantDetailList(
			@ApiParam(name="gvid",value="发放单内部编号(gv_inner_sn)",required=true) @RequestParam(required=true)Long gvid) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		return ResultList.success(service.getTransferGrantDetailList(ctxUser.getSystemType(), ctxUser.getOrgId(), gvid, null));
	}
	
	@Log(title = "business.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/red", method = RequestMethod.PUT)
	@ApiOperation(value = "红冲", notes = "红冲")
	public ResultInfo<Odo> odoRed(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.odoRed(form, curUser));
	}
	
	@Log(title = "business.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/unreview", method = RequestMethod.PUT)
	@ApiOperation(value = "反审核", notes = "反审核")
	public AjaxResult unReview(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		service.unReview(form);
		return success();
	}
	
	@Log(title = "business.product.sn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/healthCareSn/list", method = RequestMethod.POST)
	@ApiOperation(value = "获取产品医保编号列表", notes = "获取产品医保编号列表")
	public ResultList<ProductSn> getProductHealthCareSnList(
			@Validated @RequestBody ProductHealthCareSnForm form){
		SystemType type = ServletUtils.getCurUserSystemType();
		if (type.equals(SystemType.DEALER) || type.equals(SystemType.VENDOR)) {
			Long oid = ServletUtils.getCurUserOrgID();
			return ResultList.success(service.getProductHealthCareSnList(oid, form, ProductSnType.HEALTH_CARE_SN));
		} else {
			throw new BusinessException("当前机构类型不支持!");
		}
	}
}
