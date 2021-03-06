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
@Api(tags = "??????-?????????????????????")
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

    //????????????
    @SuppressWarnings("unchecked")
    @Log(title = "business.plan", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/app/list", method = RequestMethod.GET)
    @ApiOperation(value = "?????????????????????(??????,????????????)", notes = "?????????????????????(??????,????????????)")
    public TableDataInfo<Grn> searchGrnList4Phone(
            @Validated AppGrnSearchForm form) {
        startPage();
        return getDataTable(service.searchGrnList4Phone(form));
    }

    //?????????????????????
    @Log(title = "business.grntype", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/type/add", method = RequestMethod.POST)
    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    public ResultInfo<GrnType> insertGrnType(
            @Validated AddGrnTypeForm form) {
        return ResultInfo.success(typeService.insertGrnType(form));
    }

    @Log(title = "business.grntype", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/type/edit", method = RequestMethod.PUT)
    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    public AjaxResult updateGrnType(
            @Validated EditGrnTypeForm form) {
        return toAjax(typeService.updateGrnType(form));
    }

    @Log(title = "business.grntype", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/type/del/{oid}/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    public AjaxResult deleteGrnType(
            @ApiParam(name = "oid", value = "??????ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "id", value = "???????????????ID", required = true) @PathVariable Long id) {
        return toAjax(typeService.deleteGrnType(oid, id));
    }

    @Log(title = "business.grntype", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/type/list/{oid}", method = RequestMethod.GET)
    @ApiOperation(value = "???????????????????????????", notes = "???????????????????????????")
    public ResultList<GrnType> getGrnTypeList(
            @ApiParam(name = "oid", value = "????????????ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "status", value = "???????????????????????????????????????1?????????????????????", required = false) Integer status) {
        return ResultList.success(typeService.selectGrnTypeList(oid, status));
    }

    //???????????????
    @Log(title = "business.grn", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "???????????????", notes = "???????????????")
    public ResultInfo<Grn> insertGrn(
            @RequestBody @Validated AddGrnForm form) {
        String sn = numberUtils.getMaxSn(VoucherType.PUT_IN, ServletUtils.getCurUserSystemType(), form.getOrg_id());
        OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
        return ResultInfo.success(service.insertGrn(form, sn, curUser));
    }

    @Log(title = "business.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @ApiOperation(value = "???????????????", notes = "???????????????")
    public ResultInfo<Grn> updateGrn(
            @RequestBody @Validated EditGrnForm form) {
        OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
        return ResultInfo.success(service.updateGrn(form, curUser));
    }

    @Log(title = "business.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/del", method = RequestMethod.DELETE)
    @ApiOperation(value = "???????????????", notes = "???????????????")
    public AjaxResult deleteGrn(@Validated GrnIDForm form) {
        OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
        return toAjax(service.deleteGrn(form, curUser));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "?????????????????????(??????)", notes = "?????????????????????(??????)")
    public TableDataInfo<Grn> searchGrnList(
            @Validated SearchGrnForm form) {
        List<Grn> res = null;
        //??????????????????
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
    @ApiOperation(value = "?????????????????????(??????)", notes = "?????????????????????(??????)???????????????????????????????????????????????????????????????")
    public TableDataInfo<Grn> searchGrnListGlobal(
            @Validated SearchGrnForm form) {
        startPage();
        return getDataTable(service.searchGrnListGlobal(form));
    }

    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    public ResultInfo<Grn> getGrnInfo(
            @Validated GrnIDForm form) {
        return ResultInfo.success(service.selectGrnInfo(form));
    }

    /* ????????????????????????????????????????????????
     * @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType
     * = OperatorType.BUSINESS)
     *
     * @RequestMapping(value = "/barcode/info", method = RequestMethod.GET)
     *
     * @ApiOperation(value = "????????????????????????", notes = "????????????????????????") public
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
     * @ApiOperation(value = "????????????", notes = "????????????") public AjaxResult
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
     * @ApiOperation(value = "????????????(json)", notes = "????????????(json)") public AjaxResult
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
     * @ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????") public
     * ResultInfo<ProductBarcode> scanBarcode(
     *
     * @Validated ScanBarcodeForm form) { return
     * ResultInfo.success(service.scanBarcode(form)); }
     */

    @RepeatSubmit
    @Log(title = "business.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/putin", method = RequestMethod.PUT)
    @ApiOperation(value = "??????", notes = "??????")
    public AjaxResult grnPutIn(
            @RequestBody @Valid GrnIds form) {
        clearPage();
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        Map<String, Object> result = service.putin(form, curUser, false);
        if (result == null) {
            return error("????????????????????????!");
        } else {
            return success().put("info", result);
        }
    }

    @Log(title = "business.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/red", method = RequestMethod.PUT)
    @ApiOperation(value = "??????", notes = "??????")
    public ResultInfo<Grn> grnRed(@RequestBody @Valid GrnIDForm form) {
        clearPage();
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        return ResultInfo.success(service.grnRed(form, curUser, false));
    }

    @Log(title = "business.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/unreview", method = RequestMethod.PUT)
    @ApiOperation(value = "?????????", notes = "?????????")
    public AjaxResult unReview(@RequestBody @Valid GrnIDForm form) {
        clearPage();
        service.unReview(form);
        return success();
    }

    /* ????????????????????????????????????????????????
     * @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType
     * = OperatorType.BUSINESS)
     *
     * @RequestMapping(value =
     * "/storage/{oid}/{wid}/{begin_inventory}/{end_inventory}", method =
     * RequestMethod.GET)
     *
     * @ApiOperation(value = "???????????????????????????", notes = "???????????????????????????") public
     * ResultInfo<List<InventoryProduct>> getStorageLocationInfo(
     *
     * @ApiParam(name="oid",value="??????ID",required=true) @PathVariable Long oid,
     *
     * @ApiParam(name="wid",value="???????????????ID",required=true) @PathVariable Long wid,
     *
     * @ApiParam(name="begin_inventory",value="?????????",required=true) @PathVariable
     * Long begin_inventory,
     *
     * @ApiParam(name="end_inventory",value="?????????",required=true) @PathVariable Long
     * end_inventory) { return
     * ResultInfo.success(service.getStorageLocationInfo(oid, wid, begin_inventory,
     * end_inventory)); }
     */

    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/we/list/{oid}", method = RequestMethod.GET)
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public AjaxResult getWarehouseEmployeeList(
            @ApiParam(name = "oid", value = "??????ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "wid", value = "??????ID") Long wid) {
        SystemType systemType = ServletUtils.getCurUserSystemType();
        return success().put("info", commService.getWarehouseEmployeeList(systemType, oid, wid, null, true));
    }

    //?????????????????????????????????(???????????????ID????????????????????????????????????)
    @SuppressWarnings("unchecked")
    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/org/list/{systemType}/{oid}", method = RequestMethod.GET)
    @ApiOperation(value = "??????????????????(??????)", notes = "??????????????????(??????)")
    public TableDataInfo<Map<String, Object>> selectRelatedOrg4DropDownList(
            @ApiParam(name = "systemType", value = "?????????????????? 1-?????? ??? 2-???????????? 3-??????", required = true) @PathVariable int systemType,
            @ApiParam(name = "oid", value = "????????????ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "name", value = "??????????????????", required = false) String name,
            @ApiParam(name = "all", value = "????????????,??????true", required = true) @RequestParam(defaultValue = "true") Boolean all) {
        startPage();
        return getDataTable(commService.selectRelatedOrg4DropDownList(convertSystemType(systemType), oid, name, all, false, false));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/vendor/list", method = RequestMethod.GET)
    @ApiOperation(value = "????????????????????????(??????)", notes = "????????????????????????(??????)")
    public TableDataInfo<ResponseProductInfo> getVendorProductList(
            @Validated CommSearchProductForm form) {
        startPage();
        return getDataTable(commService.selectVendorProductList(form));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/product/list", method = RequestMethod.POST)
    @ApiOperation(value = "???????????????????????????????????????(??????)", notes = "???????????????????????????????????????(??????)")
    public TableDataInfo<PurchasingRecordProduct> getUsablePurchasingRecordProductList(
            @RequestBody @Validated SearchPurchasingRecordProductForm form) {
        List<PurchasingRecordProduct> res;
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        //??????????????????
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
    @ApiOperation(value = "????????????????????????", notes = "???????????????????????????????????????(??????)")
    public ResultInfo<UsableProductInfo> scanProductByBarcode(
            @RequestBody @Validated ScanBarcodeForm form) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        //??????????????????
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
     * @ApiOperation(value = "?????????????????????????????????(??????)", notes = "?????????????????????????????????(??????)") public
     * TableDataInfo<ResponseProductInfo> getDealerProductList(
     *
     * @Validated CommSearchProductForm form) { startPage(); return
     * getDataTable(commService.selectDealerProductList(form, false)); }
     */

    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/sheet/list", method = RequestMethod.GET)
    @ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????")
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
                throw new DBException("??????????????????????????????!");
            default:
        }
        return ResultList.success(list);
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/transfer/record/list", method = RequestMethod.GET)
    @ApiOperation(value = "????????????????????????????????????", notes = "????????????????????????????????????")
    public TableDataInfo<TransferVoucher> getTransferOdoList(
            @ApiParam(name = "voucherType", value = "????????????", required = true) @RequestParam(required = true) RefVoucherType voucherType,
            @ApiParam(name = "sn", value = "?????????????????????", required = false) String sn,
            @ApiParam(name = "organizationName", value = "????????????????????????", required = false) String organizationName,
            @ApiParam(name = "warehouse_name", value = "????????????", required = false) String warehouse_name,
            @ApiParam(name = "key", value = "??????????????????", required = false) String key) {
        ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
        startPage();
        return getDataTable(service.getTransferVoucherList(ctxUser.getSystemType(), ctxUser.getOrgId(), sn, organizationName, warehouse_name, voucherType, key));
    }

    @Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/transfer/record/detail", method = RequestMethod.GET)
    @ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????")
    public ResultList<TransferVoucherDetail> getTransferSaleRecordDetailList(
            @ApiParam(name = "voucherType", value = "????????????", required = true) @RequestParam(required = true) RefVoucherType voucherType,
            @ApiParam(name = "vId", value = "????????????????????????(voucher_inner_sn)", required = true) @RequestParam(required = true) Long vId,
            @ApiParam(name = "organization_inner_sn", value = "????????????????????????(organization_inner_sn)", required = true) @RequestParam(required = true) Long organization_inner_sn,
            @ApiParam(name = "billsType", value = "????????????", required = true) @RequestParam(required = true) BillsType billsType) {
        ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
        return ResultList.success(service.getTransferVoucherDetailList(ctxUser.getOrgId(), vId, organization_inner_sn, billsType, voucherType));
    }
}
