package com.yrt.project.api.business.applyvoucher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicResponseParameters;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.applyvoucher.service.IHospitalGrantService;
import com.yrt.project.api.business.applyvoucher.vo.AppGrantConfirmForm;
import com.yrt.project.api.business.applyvoucher.vo.GrantSendForm;
import com.yrt.project.api.business.applyvoucher.vo.SearchApplyDetailForm;
import com.yrt.project.api.business.applyvoucher.vo.SearchApplyVoucherForm;
import com.yrt.project.api.business.warehouse.service.AllotVoucherService;
import com.yrt.project.api.business.warehouse.vo.allot.SearchAllotVoucherForm;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucherDetail;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucherDetail;
import com.yrt.project.modular.applyvoucher.mapper.HospitalApplyVoucherMapper;
import com.yrt.project.modular.organization.domain.OrgEmployee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@ApiResponses({@ApiResponse(code = 400, message = "缺少必要参数")})
@RequestMapping("/v1/business/hospital/grant")
@Api(tags = "业务-医院材料发放申领接口")
public class HospitalGrantController extends BaseController {

    @Autowired
    IHospitalGrantService service;
    @Autowired
    private HospitalApplyVoucherMapper mapper;
    @Autowired
    private AllotVoucherService allotService;

    //手机接口
    @SuppressWarnings("unchecked")
    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/app/apply/search", method = RequestMethod.GET)
    @ApiOperation(value = "获取待发放申领单列表(手机端专用!分页,提交时间倒序)", notes = "获取待发放申领单列表(手机端专用!分页,提交时间倒序)")
    public TableDataInfo<HospitalApplyVoucher> getHospitalApplyVoucherList4Phone(
            @ApiParam(name = "oid", value = "机构ID", required = true) @RequestParam(required = true) Long oid,
            @ApiParam(name = "key", value = "搜索关键字", required = false) String key) {
    	OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
    	startPage();
        return getDataTable(service.searchHospitalApplyVoucherList(oid, key, curUser));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/app/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取发放单列表(分页，手机端专用!)", notes = "获取发放单列表(分页，手机端专用!)")
    public TableDataInfo<HospitalGrantVoucher> getHospitalGrandVoucherList4Phone(
            @ApiParam(name = "oid", value = "机构ID", required = true) @RequestParam(required = true) Long oid,
            @ApiParam(name = "key", value = "搜索关键字", required = false) String key,
            @ApiParam(name = "status", value = "状态 传值可选: 0-全部 1-可发放 2-已领取", required = true) @RequestParam(required = true) Integer status) {
    	OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
        startPage();
        return getDataTable(service.getHospitalGrandVoucherList4Phone(oid, key, status, curUser));
    }

    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/app/detail/list/{oid}/{gid}/{all}", method = RequestMethod.GET)
    @ApiOperation(value = "根据发放单ID获取发放单明细列表(手机端专用!)", notes = "根据发放单ID获取发放单明细列表(手机端专用!)")
    public ResultList<HospitalGrantVoucherDetail> selectGrantDetailList4Phone(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "gid", value = "发放单ID", required = true) @PathVariable Long gid,
            @ApiParam(name = "all", value = "是否显示全部明细(包含已确认过的明细,0-否 1-是 )", required = true) @PathVariable Integer all) {
        return ResultList.success(service.selectGrantDetailList4Phone(oid, gid, all));
    }

    //PC与手机共用接口
    @SuppressWarnings("unchecked")
    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/apply/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取待发放申领单列表(分页,提交时间倒序)", notes = "获取待发放申领单列表(分页,提交时间倒序)")
    public TableDataInfo<HospitalApplyVoucher> getHospitalApplyVoucherList(
            @Validated SearchApplyVoucherForm form) {
    	OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
        if (StringUtils.isEmpty(form.getStatus())) {
            form.setStatus("1");
        }
        startPage();
        return getDataTable(service.searchHospitalApplyVoucherList(form, curUser));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/apply/history/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取已发放申领单列表(分页,提交时间倒序)", notes = "获取已发放申领单列表(分页,提交时间倒序)")
    public TableDataInfo<HospitalApplyVoucher> getHospitalApplyVoucherHistoryList(
            @Validated SearchApplyVoucherForm form) {
    	OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
        if (StringUtils.isEmpty(form.getStatus())) {
            form.setStatus("2,3");
        }
        startPage();
        return getDataTable(service.searchHospitalApplyVoucherList(form, curUser));
    }

    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/apply/detail/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取申领单明细列表", notes = "获取申领单明细列表")
    public ResultList<HospitalApplyVoucherDetail> selectGrantingVoucherDetailListByWarehouse(
            @Validated SearchApplyDetailForm form) {
        return ResultList.success(service.selectGrantingVoucherDetailListByWarehouse(form));
    }


    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/list/{oid}/{aid}", method = RequestMethod.GET)
    @ApiOperation(value = "获取发放单列表(申领页面使用)", notes = "获取发放单列表(申领页面使用)")
    public ResultList<HospitalGrantVoucher> getHospitalGrandVoucherList(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "aid", value = "申领单ID", required = true) @PathVariable Long aid) {
        return ResultList.success(service.selectGrantVoucherlList(oid, aid, true, ""));
    }

    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/ga/{oid}/{aid}", method = RequestMethod.GET)
    @ApiOperation(value = "获取发放单/调拨单列表(申领页面使用)", notes = "获取发放单/调拨单列表(申领页面使用)")
    public ResultList<?> getHospitalAllotVoucherList(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "aid", value = "申领单ID", required = true) @PathVariable Long aid) {
        //首先查到申领单，判断是否有仓库 无:获取发放单列表	有:获取调拨单列表
        Map<String, Object> params1 = new HashMap<>();
        params1.put("hospital_inner_sn", oid);
        params1.put("av_inner_sn", aid);
        HospitalApplyVoucher ovoucher = mapper.selectHospitalApplyVoucherInfo(params1);
        if (Objects.isNull(ovoucher.getWarehouse_inner_sn()) || StringUtils.isEmpty(ovoucher.getWarehouse_inner_sn_name())) {
            //发放单
            return ResultList.success(service.selectGrantVoucherlList(oid, aid, true, ""));
        }
        //查调拨单
        SearchAllotVoucherForm form = new SearchAllotVoucherForm();
        form.setOrg_id(oid);
        form.setTab_id(-1);
        form.setApply_voucher_inner_sn(ovoucher.getVoucher_inner_sn());
        return ResultList.success(allotService.searchAllotVoucherList(form, false));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/list/{oid}", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有发放单列表(分页)", notes = "获取所有发放单列表(分页)")
    public TableDataInfo<HospitalGrantVoucher> getHospitalAllGrandVoucherList(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "isAll", value = "是否显示全部明细，否则只显示0-可发放 (默认:true)") @RequestParam(defaultValue = "true") Boolean isAll,
            @ApiParam(name = "key", value = "搜索关键字(发放单单号)", required = false) String key) {
        startPage();
        return getDataTable(service.selectGrantVoucherlList(oid, null, isAll, key));
    }

    @ApiOperationSupport(
            responses = @DynamicResponseParameters(properties = {
                    @DynamicParameter(value = "自定义状态码 0-成功 1-失败 和其他http状态码", name = "code"),
                    @DynamicParameter(value = "提示信息", name = "msg"),
                    @DynamicParameter(value = "申领单明细列表", name = "list", dataTypeClass = List.class,
                            example = "[\r\n" +
                                    "    {\r\n" +
                                    "        \"min_unit_inner_sn\":\"最小包装单位内部编码\",\r\n" +
                                    "        \"batch_number\":\"批号\",\r\n" +
                                    "        \"min_unit\":\"最小包装单位名称\",\r\n" +
                                    "        \"detail_inner_sn\":\"明细内部编码\",\r\n" +
                                    "        \"plan_quantity\":\"计划数量\",\r\n" +
                                    "        \"specification\":\"规格型号名称\",\r\n" +
                                    "        \"approved_quantity\":\"已批准发放数量\",\r\n" +
                                    "        \"product_vendor_inner_sn\":\"厂商内部编码\",\r\n" +
                                    "        \"unit_inner_sn\":\"包装单位内部编码\",\r\n" +
                                    "        \"real_quantity\":\"实际发放量\",\r\n" +
                                    "        \"inventory\":\"库存量\",\r\n" +
                                    "        \"min_unit_lock_inventory\":\"锁定最小包装单位库存量\",\r\n" +
                                    "        \"product_name\":\"产品名称\",\r\n" +
                                    "        \"min_unit_quantity\":\"申领包装单位与最小包装单位的倍数\",\r\n" +
                                    "        \"specification_inner_sn\":\"规格型号内部编码\",\r\n" +
                                    "        \"unit\":\"申领包装单位名称\",\r\n" +
                                    "        \"product_type\":\"产品类型 0:经销商(自建)产品 1:厂商产品\",\r\n" +
                                    "        \"sort_number\":\"排序号\",\r\n" +
                                    "        \"product_vendor_name\":\"厂商名称\",\r\n" +
                                    "        \"product_inner_sn\":\"产品内部编码\",\r\n" +
                                    "        \"status\":\"状态\"\r\n" +
                                    "    }\r\n" +
                                    "]")
            })
    )
    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/list/{oid}/{aid}/{wid}", method = RequestMethod.GET)
    @ApiOperation(value = "获取发放单列表(发放页面使用)", notes = "获取发放单列表(发放页面使用)")
    public AjaxResult selectGrantList(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "aid", value = "申领单ID", required = true) @PathVariable Long aid,
            @ApiParam(name = "wid", value = "仓库ID", required = true) @PathVariable Long wid) {
        clearPage();
        return success().put("list", service.selectGrantList(oid, aid, wid));
    }

    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/detail/list/{oid}/{gid}", method = RequestMethod.GET)
    @ApiOperation(value = "根据发放单ID获取发放单明细列表", notes = "根据发放单ID获取发放单明细列表")
    public ResultList<HospitalGrantVoucherDetail> selectGrantDetailList(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "gid", value = "发放单ID", required = true) @PathVariable Long gid) {
        //String status = "0";//仅显示0-可发放
        return ResultList.success(service.selectGrantDetailList(oid, gid, null));
    }

    /*
     * @Log(title = "business.grant", businessType = BusinessType.OTHER,
     * operatorType = OperatorType.BUSINESS)
     *
     * @RequestMapping(value = "/stop/{oid}/{aid}", method = RequestMethod.PUT)
     *
     * @ApiOperation(value = "终止材料申领单", notes = "终止材料申领单") public AjaxResult
     * stopHospitalApplyVoucher(
     *
     * @ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
     *
     * @ApiParam(name="aid",value="申领单ID",required=true) @PathVariable Long aid) {
     * return toAjax(service.stopHospitalApplyVoucher(oid, aid)); }
     */

    /*
     * @Log(title = "business.grant", businessType = BusinessType.OTHER,
     * operatorType = OperatorType.BUSINESS)
     *
     * @RequestMapping(value = "/confirm/{oid}/{gid}", method = RequestMethod.PUT)
     *
     * @ApiOperation(value = "确认发放", notes = "确认发放") public AjaxResult
     * confirmSendVoucher(
     *
     * @ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
     *
     * @ApiParam(name="gid",value="发放单ID",required=true) @PathVariable Long gid) {
     * return toAjax(service.confirmSendVoucher(oid, gid)); }
     */

    @ApiOperationSupport(
            responses = @DynamicResponseParameters(properties = {
                    @DynamicParameter(value = "自定义状态码 0-成功 1-失败 和其他http状态码", name = "code"),
                    @DynamicParameter(value = "提示信息", name = "msg"),
                    @DynamicParameter(value = "发放单状态0-可发放 1-出库单已完全生成 2-已领取", name = "info", dataTypeClass = Integer.class)
            })
    )
    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/confirm", method = RequestMethod.PUT)
    @ApiOperation(value = "确认发放(单个确认发放和全部确认发放)", notes = "确认发放(单个确认发放和全部确认发放)")
    public ResultInfo<Integer> confirmSendVoucher4Phone(
            @RequestBody @Valid AppGrantConfirmForm form) {
    	ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        return ResultInfo.success(service.confirmSendVoucher4Phone(form, curUser));
    }

    @ApiOperationSupport(
            responses = @DynamicResponseParameters(properties = {
                    @DynamicParameter(value = "自定义状态码 0-成功 1-失败 和其他http状态码", name = "code"),
                    @DynamicParameter(value = "提示信息", name = "msg"),
                    @DynamicParameter(value = "申领单状态 0-草稿 1-申领 2-全部发放(自然终止) 3-申领终止(人为终止)", name = "info", dataTypeClass = Integer.class)
            })
    )
    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ApiOperation(value = "发放", notes = "发放")
    public ResultInfo<Integer> voucherSend(
            @RequestBody @Valid GrantSendForm form) {
    	OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
        return ResultInfo.success(service.voucherSend(form, curUser));
    }

    @Log(title = "business.grant", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/creatplan", method = RequestMethod.POST)
    @ApiOperation(value = "生成采购计划", notes = "生成采购计划")
    public AjaxResult createPlan(
            @RequestBody @Valid GrantSendForm form) {
    	OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
        return toAjax(service.createPlan(form, curUser));
    }

}
