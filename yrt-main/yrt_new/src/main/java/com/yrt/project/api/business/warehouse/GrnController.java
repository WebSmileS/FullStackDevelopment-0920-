package com.yrt.project.api.business.warehouse;

import java.util.ArrayList;
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

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.interceptor.annotation.RepeatSubmit;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.order.vo.purchasingRecord.SearchPurchasingRecordProductForm;
import com.yrt.project.api.business.warehouse.service.IGrnService;
import com.yrt.project.api.business.warehouse.service.IGrnTypeService;
import com.yrt.project.api.business.warehouse.vo.ScanBarcodeForm;
import com.yrt.project.api.business.warehouse.vo.UsableProductInfo;
import com.yrt.project.api.business.warehouse.vo.grn.AddGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.AddGrnTypeForm;
import com.yrt.project.api.business.warehouse.vo.grn.AppGrnSearchForm;
import com.yrt.project.api.business.warehouse.vo.grn.EditGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.EditGrnTypeForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIDForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIds;
import com.yrt.project.api.business.warehouse.vo.grn.SearchGrnForm;
import com.yrt.project.api.common.service.CommService;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.api.common.vo.CommSearchProductForm;
import com.yrt.project.api.platform.service.SysParameterService;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.order.domain.PurchasingRecordProduct;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.Grn;
import com.yrt.project.modular.warehouse.domain.GrnType;
import com.yrt.project.modular.warehouse.domain.TransferVoucher;
import com.yrt.project.modular.warehouse.domain.TransferVoucherDetail;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/grn")
@Api(tags = "业务-入库单管理接口")
public class GrnController extends BaseController {

    @Autowired
    private IGrnService service;
    @Autowired
    private IGrnTypeService typeService;
    @Autowired
    private SysParameterService paramService;
    @Autowired
    private CommService commService;
    @Autowired
    VoucherNumberService numberUtils;

    //手机功能
    @SuppressWarnings("unchecked")
    @Log(title = "business.plan", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/app/list", method = RequestMethod.GET)
    @ApiOperation(value = "搜索入库单列表(分页,手机专用)", notes = "搜索入库单列表(分页,手机专用)")
    public TableDataInfo<Grn> searchGrnList4Phone(
            @Validated AppGrnSearchForm form) {
        startPage();
        return getDataTable(service.searchGrnList4Phone(form));
    }

    //入库单类型接口
    @Log(title = "business.grntype", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/type/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增入库单类型", notes = "新增入库单类型")
    public ResultInfo<GrnType> insertGrnType(
            @Validated AddGrnTypeForm form) {
        return ResultInfo.success(typeService.insertGrnType(form));
    }

    @Log(title = "business.grntype", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/type/edit", method = RequestMethod.PUT)
    @ApiOperation(value = "编辑入库单类型", notes = "编辑入库单类型")
    public AjaxResult updateGrnType(
            @Validated EditGrnTypeForm form) {
        return toAjax(typeService.updateGrnType(form));
    }

    @Log(title = "business.grntype", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/type/del/{oid}/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除入库单类型", notes = "删除入库单类型")
    public AjaxResult deleteGrnType(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "id", value = "入库单类型ID", required = true) @PathVariable Long id) {
        return toAjax(typeService.deleteGrnType(oid, id));
    }

    @Log(title = "business.grntype", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/type/list/{oid}", method = RequestMethod.GET)
    @ApiOperation(value = "查询入库单类型列表", notes = "查询入库单类型列表")
    public ResultList<GrnType> getGrnTypeList(
            @ApiParam(name = "oid", value = "当前机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "status", value = "入库单状态不传查询全部，传1则只查询可用的", required = false) Integer status) {
        return ResultList.success(typeService.selectGrnTypeList(oid, status));
    }

    //入库单接口
    @Log(title = "business.grn", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增入库单", notes = "新增入库单")
    public ResultInfo<Grn> insertGrn(
            @RequestBody @Validated AddGrnForm form) {
        String sn = numberUtils.getMaxSn(VoucherType.PUT_IN, ServletUtils.getCurUserSystemType(), form.getOrg_id());
        OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
        return ResultInfo.success(service.insertGrn(form, sn, curUser));
    }

    @Log(title = "business.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @ApiOperation(value = "编辑入库单", notes = "编辑入库单")
    public ResultInfo<Grn> updateGrn(
            @RequestBody @Validated EditGrnForm form) {
        OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
        return ResultInfo.success(service.updateGrn(form, curUser));
    }

    @Log(title = "business.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/del", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除入库单", notes = "删除入库单")
    public AjaxResult deleteGrn(@Validated GrnIDForm form) {
        OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
        return toAjax(service.deleteGrn(form, curUser));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "搜索入库单列表(分页)", notes = "搜索入库单列表(分页)")
    public TableDataInfo<Grn> searchGrnList(
            @Validated SearchGrnForm form) {
        List<Grn> res = null;
        //加入仓库限制
        List<Long> warehouseIds = getCurUserWarehouseIds();
        startPage();
        if (warehouseIds.size() == 0) {
            res = new ArrayList<>();
        } else {
            res = service.searchGrnList(form, warehouseIds);
        }
        startPage();
        return getDataTable(res);
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/global/list", method = RequestMethod.GET)
    @ApiOperation(value = "搜索入库单列表(分页)", notes = "搜索入库单列表(分页)，类似管理员财务全局搜索，不受部门仓库限制")
    public TableDataInfo<Grn> searchGrnListGlobal(
            @Validated SearchGrnForm form) {
        startPage();
        return getDataTable(service.searchGrnListGlobal(form));
    }

    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "获取入库单详情", notes = "获取入库单详情")
    public ResultInfo<Grn> getGrnInfo(
            @Validated GrnIDForm form) {
        return ResultInfo.success(service.selectGrnInfo(form));
    }

    /* 取消储位功能，故取消条码相关功能
     * @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType
     * = OperatorType.BUSINESS)
     *
     * @RequestMapping(value = "/barcode/info", method = RequestMethod.GET)
     *
     * @ApiOperation(value = "获取产品条码信息", notes = "获取产品条码信息") public
     * ResultInfo<List<ProductBarcode>> getBarcode(
     *
     * @Validated SearchBarcodeForm form) { return
     * ResultInfo.success(service.getBarcode(form)); }
     *
     * @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType
     * = OperatorType.BUSINESS)
     *
     * @RequestMapping(value = "/barcode/create", method = RequestMethod.GET)
     *
     * @ApiOperation(value = "生成条码", notes = "生成条码") public AjaxResult
     * createBarcode(
     *
     * @Validated CreateBarcodeForm form) { return success().put("list",
     * service.createBarcode(form)); }
     *
     * @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType
     * = OperatorType.BUSINESS)
     *
     * @RequestMapping(value = "/barcode/save", method = RequestMethod.POST)
     *
     * @ApiOperation(value = "保存条码(json)", notes = "保存条码(json)") public AjaxResult
     * saveBarcode(
     *
     * @RequestBody @Validated SaveBarcodeForm form) { SystemType systemType =
     * ServletUtils.getCurUserSystemType(); return
     * toAjax(service.saveBarcode(systemType, form)); }
     *
     * @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType
     * = OperatorType.BUSINESS)
     *
     * @RequestMapping(value = "/barcode/scan", method = RequestMethod.POST)
     *
     * @ApiOperation(value = "扫描条码获取产品信息", notes = "扫描条码获取产品信息") public
     * ResultInfo<ProductBarcode> scanBarcode(
     *
     * @Validated ScanBarcodeForm form) { return
     * ResultInfo.success(service.scanBarcode(form)); }
     */

    @RepeatSubmit
    @Log(title = "business.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/putin", method = RequestMethod.PUT)
    @ApiOperation(value = "入库", notes = "入库")
    public AjaxResult grnPutIn(
            @RequestBody @Valid GrnIds form) {
        clearPage();
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        Map<String, Object> result = service.putin(form, curUser, false);
        if (result == null) {
            return error("数据库没有返回值!");
        } else {
            return success().put("info", result);
        }
    }

    @Log(title = "business.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/red", method = RequestMethod.PUT)
    @ApiOperation(value = "红冲", notes = "红冲")
    public ResultInfo<Grn> grnRed(@RequestBody @Valid GrnIDForm form) {
        clearPage();
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        return ResultInfo.success(service.grnRed(form, curUser, false));
    }

    @Log(title = "business.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/unreview", method = RequestMethod.PUT)
    @ApiOperation(value = "反审核", notes = "反审核")
    public AjaxResult unReview(@RequestBody @Valid GrnIDForm form) {
        clearPage();
        service.unReview(form);
        return success();
    }

    /* 取消储位功能，故取消条码相关功能
     * @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType
     * = OperatorType.BUSINESS)
     *
     * @RequestMapping(value =
     * "/storage/{oid}/{wid}/{begin_inventory}/{end_inventory}", method =
     * RequestMethod.GET)
     *
     * @ApiOperation(value = "获取储位分配单信息", notes = "获取储位分配单信息") public
     * ResultInfo<List<InventoryProduct>> getStorageLocationInfo(
     *
     * @ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
     *
     * @ApiParam(name="wid",value="入库单仓库ID",required=true) @PathVariable Long wid,
     *
     * @ApiParam(name="begin_inventory",value="开始值",required=true) @PathVariable
     * Long begin_inventory,
     *
     * @ApiParam(name="end_inventory",value="结束值",required=true) @PathVariable Long
     * end_inventory) { return
     * ResultInfo.success(service.getStorageLocationInfo(oid, wid, begin_inventory,
     * end_inventory)); }
     */

    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/we/list/{oid}", method = RequestMethod.GET)
    @ApiOperation(value = "获取仓库人员", notes = "获取仓库人员")
    public AjaxResult getWarehouseEmployeeList(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "wid", value = "仓库ID") Long wid) {
        SystemType systemType = ServletUtils.getCurUserSystemType();
        return success().put("info", commService.getWarehouseEmployeeList(systemType, oid, wid, null, true));
    }

    //获取合同相关的机构列表(只包含机构ID，机构名称和机构类型字段)
    @SuppressWarnings("unchecked")
    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/org/list/{systemType}/{oid}", method = RequestMethod.GET)
    @ApiOperation(value = "相关机构列表(分页)", notes = "相关机构列表(分页)")
    public TableDataInfo<Map<String, Object>> selectRelatedOrg4DropDownList(
            @ApiParam(name = "systemType", value = "相关机构类型 1-医院 、 2-经销商、 3-厂商", required = true) @PathVariable int systemType,
            @ApiParam(name = "oid", value = "当前机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "name", value = "搜索机构名称", required = false) String name,
            @ApiParam(name = "all", value = "合同机构,默认true", required = true) @RequestParam(defaultValue = "true") Boolean all) {
        startPage();
        return getDataTable(commService.selectRelatedOrg4DropDownList(convertSystemType(systemType), oid, name, all, false, false));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/vendor/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取厂商产品列表(分页)", notes = "获取厂商产品列表(分页)")
    public TableDataInfo<ResponseProductInfo> getVendorProductList(
            @Validated CommSearchProductForm form) {
        startPage();
        return getDataTable(commService.selectVendorProductList(form));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/product/list", method = RequestMethod.POST)
    @ApiOperation(value = "获取可用的采购记录产品列表(分页)", notes = "获取可用的采购记录产品列表(分页)")
    public TableDataInfo<PurchasingRecordProduct> getUsablePurchasingRecordProductList(
            @RequestBody @Validated SearchPurchasingRecordProductForm form) {
        List<PurchasingRecordProduct> res;
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        //加入仓库限制
        List<Long> warehouseIds = getCurUserWarehouseIds();
        if (warehouseIds.size() == 0) {
            res = new ArrayList<>();
        } else {
            res = service.getUsablePurchasingRecordProductList(form, curUser, VoucherType.PUT_IN, warehouseIds);
        }
        return getDataTable(res);
    }

    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/scan", method = RequestMethod.POST)
    @ApiOperation(value = "扫码获取产品信息", notes = "获取可用的采购记录产品列表(分页)")
    public ResultInfo<UsableProductInfo> scanProductByBarcode(
            @RequestBody @Validated ScanBarcodeForm form) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        //加入仓库限制
        UsableProductInfo res = new UsableProductInfo();
        List<Long> warehouseIds = getCurUserWarehouseIds();
        if (warehouseIds.size() != 0) {
            res = service.scanProductByBarcode(form, curUser, warehouseIds);
        }
        return ResultInfo.success(res);
    }

    /*
     * @SuppressWarnings("unchecked")
     *
     * @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType
     * = OperatorType.BUSINESS)
     *
     * @RequestMapping(value = "/dealer/list", method = RequestMethod.GET)
     *
     * @ApiOperation(value = "获取经销商自建产品列表(分页)", notes = "获取经销商自建产品列表(分页)") public
     * TableDataInfo<ResponseProductInfo> getDealerProductList(
     *
     * @Validated CommSearchProductForm form) { startPage(); return
     * getDataTable(commService.selectDealerProductList(form, false)); }
     */

    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/sheet/list", method = RequestMethod.GET)
    @ApiOperation(value = "入库单可调单类型列表", notes = "入库单可调单类型列表")
    public ResultList<RefVoucherType> getOrderSheetList() {
        List<RefVoucherType> list = new ArrayList<>();
        ContextUserInfo info = ServletUtils.getCurUserInfo();
        switch (info.getSystemType()) {
            case HOSPITAL:
            case DEALER:
                if (paramService.isSingle(info.getSystemType(), info.getOrgId())) {
//				list.add(RefVoucherType.ORDER_PO);
                } else {
                    list.add(RefVoucherType.ODO);
//			list.add(RefVoucherType.RECEIPT);
                }
                list.add(RefVoucherType.ALLOT);
                break;
            case VENDOR:
                throw new DBException("当前机构不支持该功能!");
            default:
        }
        return ResultList.success(list);
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/transfer/record/list", method = RequestMethod.GET)
    @ApiOperation(value = "可用做入库调单的单据列表", notes = "可用做入库调单的单据列表")
    public TableDataInfo<TransferVoucher> getTransferOdoList(
            @ApiParam(name = "voucherType", value = "调单类型", required = true) @RequestParam(required = true) RefVoucherType voucherType,
            @ApiParam(name = "sn", value = "搜索销售单单号", required = false) String sn,
            @ApiParam(name = "organizationName", value = "搜索相关机构名称", required = false) String organizationName,
            @ApiParam(name = "warehouse_name", value = "仓库名称", required = false) String warehouse_name,
            @ApiParam(name = "key", value = "搜索产品名称", required = false) String key) {
        ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
        startPage();
        return getDataTable(service.getTransferVoucherList(ctxUser.getSystemType(), ctxUser.getOrgId(), sn, organizationName, warehouse_name, voucherType, key));
    }

    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/transfer/record/detail", method = RequestMethod.GET)
    @ApiOperation(value = "被调单的单据明细列表", notes = "被调单的单据明细列表")
    public ResultList<TransferVoucherDetail> getTransferSaleRecordDetailList(
            @ApiParam(name = "voucherType", value = "调单类型", required = true) @RequestParam(required = true) RefVoucherType voucherType,
            @ApiParam(name = "vId", value = "调单单据内部编号(voucher_inner_sn)", required = true) @RequestParam(required = true) Long vId,
            @ApiParam(name = "organization_inner_sn", value = "相关机构内部编号(organization_inner_sn)", required = true) @RequestParam(required = true) Long organization_inner_sn,
            @ApiParam(name = "billsType", value = "单据类型", required = true) @RequestParam(required = true) BillsType billsType) {
        ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
        return ResultList.success(service.getTransferVoucherDetailList(ctxUser.getOrgId(), vId, organization_inner_sn, billsType, voucherType));
    }
}
