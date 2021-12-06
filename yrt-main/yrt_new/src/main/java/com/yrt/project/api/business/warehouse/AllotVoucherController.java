package com.yrt.project.api.business.warehouse;

import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.warehouse.service.AllotVoucherService;
import com.yrt.project.api.business.warehouse.vo.allot.AddAllotVoucherForm;
import com.yrt.project.api.business.warehouse.vo.allot.EditAllotVoucherForm;
import com.yrt.project.api.business.warehouse.vo.allot.SearchAllotVoucherForm;
import com.yrt.project.modular.warehouse.domain.AllotVoucher;
import com.yrt.project.modular.warehouse.domain.AllotVoucherDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/business/allot")
@Api(tags = "业务-医院经销商厂商调拨单接口")
public class AllotVoucherController extends BaseController {
    @Autowired
    AllotVoucherService service;

    @Log(title = "business.allot", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation(value = "添加调拨单信息", notes = "添加调拨单信息")
    public ResultInfo<AllotVoucher> addAllotVoucher(@RequestBody @Valid AddAllotVoucherForm form) {
        return ResultInfo.success(service.addAllotVoucher(form));
    }

    @Log(title = "business.allot", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/{av_inner_sn}", method = RequestMethod.GET)
    @ApiOperation(value = "获取单条调拨单详情", notes = "获取单条调拨单详情，用于修改表单回显，返回结果包含一条调拨单据信息多个产品明细信息")
    public ResultInfo<AllotVoucher> getAllotVoucherDetails(@PathVariable("av_inner_sn") Long avid) {
        return ResultInfo.success(service.searchAllotVoucherDetails(avid));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.allot", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "获取调拨单列表(分页,创建时间倒序)", notes = "获取调拨单列表(分页,创建时间倒序)，包含通过明细id检索单条明细")
    public TableDataInfo<AllotVoucherDetail> getAllotVoucherList(
            @Validated SearchAllotVoucherForm form) {
        return getDataTable(service.searchAllotVoucherList(form,true));
    }

    @Log(title = "business.allot", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/voucher/edit", method = RequestMethod.PUT)
    @ApiOperation(value = "修改调拨单信息", notes = "修改调拨单信息，同时修改一条调拨单和多条明细")
    public AjaxResult updateAllotVoucher(
            @RequestBody EditAllotVoucherForm form) {
        return toAjax(service.updateAllotVoucher(form));
    }

    @Log(title = "business.allot", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/detail/edit", method = RequestMethod.PUT)
    @ApiOperation(value = "修改调拨单具体某条明细信息", notes = "修改调拨单具体某条明细信息，如对具体明细的按钮操作")
    public AjaxResult updateAllotVoucherDetail(
            @RequestBody List<AllotVoucherDetail> allotVoucherDetails) {
        return toAjax(service.updateAllotVoucherDetails(allotVoucherDetails));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.allot", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/transfer/voucher", method = RequestMethod.GET)
    @ApiOperation(value = "可用做出库调单的调拨单列表", notes = "可用做调单的调拨单列表，出入库调单时的查询接口")
    public TableDataInfo<AllotVoucher> getTransferAllotVoucherList(
            @ApiParam(name="key",value="搜索关键字(调拨单外部单据编号)") String key,
            @ApiParam(name="key1",value="搜索关键字(产品名字)") String key1,
            @ApiParam(name="status",value="调拨明细单状态，仅入库调单时需要传入status=2其他时候忽略传参")Integer status) {
        ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
        return getDataTable(service.getTransferAllotVoucherList(ctxUser.getOrgId(), key,key1,status));
    }

    @Log(title = "business.allot", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/transfer/detail", method = RequestMethod.GET)
    @ApiOperation(value = "被调单的调拨单明细列表", notes = "被调单的调拨单明细列表，调单时的查询接口")
    public ResultList<AllotVoucherDetail> getTransferAllotVoucherDetailList(
            @ApiParam(name="avid",value="调拨单内部编号(av_inner_sn)",required=true) @RequestParam String avid,
            @ApiParam(name="status",value="调拨明细单状态，仅入库调单时需要传入status=2其他时候忽略传参")Integer status) {
        ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
        return ResultList.success(service.getTransferAllotVoucherDetailList(ctxUser.getOrgId(), Long.valueOf(avid),status));
    }
}
