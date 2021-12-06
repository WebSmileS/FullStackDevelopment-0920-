package com.yrt.project.api.business.applyvoucher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yrt.common.utils.StringUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.applyvoucher.service.IHospitalApplyVoucherService;
import com.yrt.project.api.business.applyvoucher.vo.AddHospitalApplyVoucherForm;
import com.yrt.project.api.business.applyvoucher.vo.AppApplayProductSearchForm;
import com.yrt.project.api.business.applyvoucher.vo.AppApplySearchForm;
import com.yrt.project.api.business.applyvoucher.vo.ApplyIDForm;
import com.yrt.project.api.business.applyvoucher.vo.ApplySearchProductForm;
import com.yrt.project.api.business.applyvoucher.vo.EditHospitalApplyVoucherForm;
import com.yrt.project.api.business.applyvoucher.vo.SearchApplyVoucherForm;
import com.yrt.project.api.business.applyvoucher.vo.ViewApplyVoucherForm;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucher;
import com.yrt.project.modular.product.domain.ApplyResponseProductInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/hospital/apply")
@Api(tags = "业务-医院材料申领接口")
public class HospitalApplyVoucherController extends BaseController {

    @Autowired
    private IHospitalApplyVoucherService service;

    //TODO 临时未云峰医院添加
//	@Autowired private IProductService productService;

//	@Autowired private IDealerProductService dealerProductService;


    static Cache<String, List<ApplyResponseProductInfo>> commonCache = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();


    //手机接口
    @SuppressWarnings("unchecked")
    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/app/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取待申领材料申领列表(手机端专用!status参数不传表示全部。分页,创建时间倒序)", notes = "获取待申领材料申领列表(手机端专用!status参数不传表示全部。分页,创建时间倒序)")
    public TableDataInfo<HospitalApplyVoucher> getHospitalApplyVoucherList4Phone(
            @Validated AppApplySearchForm form) {
        startPage();
        return getDataTable(service.searchHospitalApplyVoucherList4Phone(form.getOid(), form.getKey(), form.getStatus()));
    }

    // TODO 临时未云峰医院添加，合同建好后恢复
    @SuppressWarnings("unchecked")
    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/app/product/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取有效合同内产品列表(分页,手机专用)", notes = "获取有效合同内产品列表(分页,手机专用)")
    public TableDataInfo<ApplyResponseProductInfo> getContractProductList4Phone(
            @Validated AppApplayProductSearchForm form) {
        startPage();
        Map<String, Object> params = new HashMap<>();
        params.put("hospital_inner_sn", form.getOid());
        params.put("key", form.getKey());
        if (form.getProduct_type() == 0) {
            return getDataTable(service.selectContractDealerProductList(params));
        }
        return getDataTable(service.selectContractVendorProductList(params));
    }

    @SuppressWarnings("unchecked")
	@Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/app/product/list/common/{oid}/{did}/{productType}", method = RequestMethod.GET)
    @ApiOperation(value = "根据医院与科室获取常用申领产品", notes = "根据医院与科室获取常用申领产品")
    public TableDataInfo<ApplyResponseProductInfo> selectGrantingVoucherDetailLisHot(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable("oid") Long oid,
            @ApiParam(name = "did", value = "科室ID", required = true) @PathVariable("did") Long did,
            @ApiParam(name = "productType", value = "产品类型", required = true) @PathVariable("productType") Integer productType
    ) throws ExecutionException {

        String key = "product:" + oid + ":" + did + ":" + productType;

		List<ApplyResponseProductInfo> ret = commonCache.get(key, () -> {

            List<ApplyResponseProductInfo> details = service.selectHospitalApplyResponseProductInfoByOidAndDid(oid, did, productType);

            Map<Long, Long> group = details.stream().collect(Collectors.groupingBy(ApplyResponseProductInfo::getProduct_inner_sn, Collectors.counting()));

			List<Map.Entry<Long, Long>> sortedIds = group.entrySet().stream()
					.sorted(Comparator.comparingLong(x -> ((Map.Entry<Long, Long>) x).getValue()).reversed())
					.collect(Collectors.toList());

            List<ApplyResponseProductInfo> list = new ArrayList<>();

            for (Map.Entry<Long, Long> sortedId : sortedIds) {
                ApplyResponseProductInfo item = details.stream().filter(x -> x.getProduct_inner_sn().equals(sortedId.getKey())).findFirst().orElse(null);
                if (null != item) {
                    list.add(item);
                }
            }

            return list;
        });

        return getDataTable(ret);
    }


    //TODO 临时未云峰医院添加，合同建好后删除
    /*
     * @SuppressWarnings("unchecked")
     *
     * @Log(title = "business.apply", businessType = BusinessType.OTHER,
     * operatorType = OperatorType.BUSINESS)
     *
     * @RequestMapping(value = "/app/product/list", method = RequestMethod.GET)
     *
     * @ApiOperation(value = "获取有效合同内产品列表(分页,手机专用)", notes = "获取有效合同内产品列表(分页,手机专用)")
     * public TableDataInfo<ResponseProductInfo> getContractProductList4Phone(
     *
     * @Validated AppApplayProductSearchForm tmp_form) { SearchProductForm form =
     * new SearchProductForm(); form.setKey(tmp_form.getKey());
     * if(tmp_form.getProduct_type() == 0) { form.escapeLikeValue(); return
     * getDataTable(dealerProductService.searchDealerProductList4Contract(form, 2,
     * 1L)); } PageDomain pageDomain = TableSupport.buildPageRequest(); Integer
     * pageNum = pageDomain.getPageNum(); Integer pageSize =
     * pageDomain.getPageSize(); form.escapeLikeValue(); return
     * productService.searchProductList4Contract(form, 2, 1L, pageNum, pageSize); }
     */

    //PC与手机共用接口
    @SuppressWarnings("unchecked")
    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取待申领材料申领列表(分页,创建时间倒序)", notes = "获取待申领材料申领列表(分页,创建时间倒序)")
    public TableDataInfo<HospitalApplyVoucher> getHospitalApplyVoucherList(
            @Validated SearchApplyVoucherForm form) {
        if (StringUtils.isEmpty(form.getStatus())) {
            form.setStatus("0,1");
        }
        return getDataTable(service.searchHospitalApplyVoucherList(form));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/history/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取已申领材料申领列表(分页,创建时间倒序)", notes = "获取已申领材料申领列表(分页,创建时间倒序)")
    public TableDataInfo<HospitalApplyVoucher> getHospitalApplyVoucherHistoryList(
            @Validated SearchApplyVoucherForm form) {
        if (StringUtils.isEmpty(form.getStatus())) {
            form.setStatus("2,3");
        }
        return getDataTable(service.searchHospitalApplyVoucherList(form));
    }

    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/info/{oid}/{aid}", method = RequestMethod.GET)
    @ApiOperation(value = "获取材料申领信息", notes = "获取材料申领信息")
    public ResultInfo<ViewApplyVoucherForm> getHospitalApplyVoucherInfo(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "aid", value = "申领单ID", required = true) @PathVariable Long aid) {
        return ResultInfo.success(service.selectHospitalApplyVoucherInfo(oid, aid));
    }

    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加材料申领信息", notes = "添加材料申领信息")
    public ResultInfo<HospitalApplyVoucher> addHospitalApplyVoucher(@RequestBody @Valid AddHospitalApplyVoucherForm form) {
        return ResultInfo.success(service.addHospitalApplyVoucher(form));
    }

    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @ApiOperation(value = "修改材料申领信息", notes = "修改材料申领信息")
    public AjaxResult updateHospitalApplyVoucher(
            @RequestBody @Valid EditHospitalApplyVoucherForm form) {
        return toAjax(service.updateAddHospitalApplyVoucher(form));
    }

    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/del/{oid}/{aid}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除材料申领信息", notes = "删除材料申领信息")
    public AjaxResult deleteHospitalApplyVoucher(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "aid", value = "申领单ID", required = true) @PathVariable Long aid) {
        return toAjax(service.deleteHospitalApplyVoucher(oid, aid));
    }

    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/detail/del", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除申领材料", notes = "删除申领材料")
    public AjaxResult deleteHospitalApplyVoucherDetail(
            @Validated ApplyIDForm form) {
        return toAjax(service.deleteHospitalApplyVoucherDetail(form));
    }

    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/copy/{oid}/{aid}", method = RequestMethod.POST)
    @ApiOperation(value = "复制材料申领信息", notes = "复制材料申领信息")
    public ResultInfo<ViewApplyVoucherForm> copyHospitalApplyVoucher(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "aid", value = "申领单ID", required = true) @PathVariable Long aid) {
        return ResultInfo.success(service.copyHospitalApplyVoucher(oid, aid));
    }

    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/stop/{oid}/{aid}", method = RequestMethod.PUT)
    @ApiOperation(value = "终止材料申领单", notes = "终止材料申领单")
    public AjaxResult stopHospitalApplyVoucher(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "aid", value = "申领单ID", required = true) @PathVariable Long aid) {
        return toAjax(service.stopHospitalApplyVoucher(oid, aid));
    }

    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/detail/stop", method = RequestMethod.PUT)
    @ApiOperation(value = "终止申领材料", notes = "终止申领材料")
    public AjaxResult stopHospitalApplyVoucherDetail(
            @Validated ApplyIDForm form) {
        return toAjax(service.stopHospitalApplyVoucherDetail(form));
    }

    @SuppressWarnings("unchecked")
    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/product/list/{oid}/{product_type}", method = RequestMethod.GET)
    @ApiOperation(value = "获取有效合同内产品列表(分页)", notes = "获取有效合同内产品列表(分页)")
    public TableDataInfo<ApplyResponseProductInfo> getContractProductList(
            @ApiParam(name = "oid", value = "机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "product_type", value = "产品类型 0:经销商(自建)产品   1:厂商产品", required = true) @PathVariable int product_type,
            @Validated ApplySearchProductForm form) {
        startPage();
        Map<String, Object> params = new HashMap<>();
        params.put("hospital_inner_sn", oid);
        params.put("type_inner_sn", form.getType_inner_sn());
        params.put("code68_sn", form.getCode68_sn());
        params.put("name", form.getName());
        params.put("vendor_inner_sn", form.getVendor_inner_sn());
        params.put("specification_vendor_sn", form.getSpecification_vendor_sn());
        params.put("specification", form.getSpecification());
        if (product_type == 0) {
            return getDataTable(service.selectContractDealerProductList(params));
        }
        return getDataTable(service.selectContractVendorProductList(params));
    }

    @Log(title = "business.apply", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/batch_number/list/{oid}/{vid}/{pid}/{sid}/{product_type}", method = RequestMethod.GET)
    @ApiOperation(value = "获取仓库中存在的产品批号列表", notes = "获取仓库中存在的产品批号列表")
    public ResultList<String> selectBatchNumberList(
            @ApiParam(name = "oid", value = "医院机构ID", required = true) @PathVariable Long oid,
            @ApiParam(name = "vid", value = "厂商ID", required = true) @PathVariable Long vid,
            @ApiParam(name = "pid", value = "产品ID", required = true) @PathVariable Long pid,
            @ApiParam(name = "sid", value = "规格型号ID", required = true) @PathVariable Long sid,
            @ApiParam(name = "product_type", value = "产品类型 0:经销商(自建)产品   1:厂商产品", required = true) @PathVariable Integer product_type) {
        return ResultList.success(service.selectBatchNumberList(oid, vid, pid, sid, product_type));
    }
}
