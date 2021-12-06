package com.yrt.project.api.app.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.utils.StringUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.app.product.vo.AppProductSearchForm;
import com.yrt.project.api.business.applyvoucher.service.IHospitalApplyVoucherService;
import com.yrt.project.api.business.applyvoucher.vo.AppApplayProductSearchForm;
import com.yrt.project.api.business.plan.service.IPlanService;
import com.yrt.project.api.business.warehouse.service.IOdoService;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.product.domain.ApplyResponseProductInfo;
import com.yrt.project.modular.product.domain.ProductBasic;
import com.yrt.project.modular.product.mapper.ProductMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/app/product")
@Api(tags = "手机-产品接口")
public class ProductAppController extends BaseController {

	@Autowired ProductMapper mapper;

	@Autowired IHospitalApplyVoucherService applyService;
	@Autowired IPlanService planService;
	@Autowired IOdoService odoService;

	@Log(title = "business.app", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@ApiOperation(value = "所有厂商产品列表(分页,带图片)", notes = "所有厂商产品列表(分页,带图片)")
	public TableDataInfo<ProductBasic> getPublishVendorProductList(
			@ApiParam(name = "key", value = "搜索关键字(厂商名称/产品名称/产品编码)", required = false) @RequestParam(required = false, defaultValue = "") String key) {
		PageDomain pageDomain = TableSupport.buildPageRequest();
		Integer pageNum = pageDomain.getPageNum();
		Integer pageSize = pageDomain.getPageSize();
		Map<String, Object> params = new HashMap<>();
		params.put("key_i", StringUtils.isEmpty(key)?null:key);
		params.put("page_i", pageNum == null ? 1 : pageNum);
		params.put("row_i", pageSize == null ? 10 : pageSize);
		List<ProductBasic> list = mapper.searchProductList4App(params);
		long total = Long.valueOf(params.get("total_row_o").toString());
		TableDataInfo<ProductBasic> rspData = new TableDataInfo<ProductBasic>();
		rspData.setCode(0);
		rspData.setRows(list);
		rspData.setTotal(total);
		return rspData;
	}

	@Log(title = "business.app", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "所有已发布厂商产品列表", notes = "所有已发布厂商产品列表")
	public TableDataInfo<ResponseProductInfo> getPublishVendorProductListNoImage(AppProductSearchForm form) {
		PageDomain pageDomain = TableSupport.buildPageRequest();
		Integer pageNum = pageDomain.getPageNum();
		Integer pageSize = pageDomain.getPageSize();
		Map<String, Object> params = new HashMap<>();
		params.put("type_inner_sn_i", null);
		params.put("code68_sn_i", null);
		params.put("product_name_i", form.getKey());
		params.put("vendor_inner_sn_i", null);
		params.put("page_i", pageNum == null ? 1 : pageNum);
		params.put("row_i", pageSize == null ? 10 : pageSize);
		params.put("include_null_unit_i", true);
		List<ResponseProductInfo> list = mapper.selectVendorProductList(params);
		long total = Long.valueOf(params.get("total_row_o").toString());
		TableDataInfo<ResponseProductInfo> rspData = new TableDataInfo<ResponseProductInfo>();
		rspData.setCode(0);
		rspData.setRows(list);
		rspData.setTotal(total);
		return rspData;
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.app", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/plan/list", method = RequestMethod.GET)
	@ApiOperation(value = "所有采购计划产品列表", notes = "所有采购计划产品列表")
	public TableDataInfo<Map<String, Object>> getPlanProductList(@Validated AppApplayProductSearchForm form) {
		startPage();
		if (form.getProduct_type() == 0) {
			return getDataTable(planService.selectDealerProductList4Phone(form));
		}
		return getDataTable(planService.selectVendorProductList4Phone(form));
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.app", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/contract/list", method = RequestMethod.GET)
	@ApiOperation(value = "所有合同双方产品列表", notes = "所有合同双方产品列表")
	public TableDataInfo<Map<String, Object>> getContractProductList(@Validated AppApplayProductSearchForm form) {
		startPage();
		if (form.getProduct_type() == 0) {
			return getDataTable(planService.selectContractDealerProductList4Phone(form));
		}
		return getDataTable(planService.selectContractVendorProductList4Phone(form));
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.app", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/apply/list", method = RequestMethod.GET)
	@ApiOperation(value = "所有合同产品与仓库产品列表", notes = "所有合同产品与仓库产品列表")
	public TableDataInfo<ApplyResponseProductInfo> getContractAndInventoryProductList(
			@Validated AppApplayProductSearchForm form) {
		startPage();
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOid());
		params.put("key", form.getKey());
		if (form.getProduct_type() == 0) {
			return getDataTable(applyService.selectContractDealerProductList(params));
		}
		return getDataTable(applyService.selectContractVendorProductList(params));
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.app", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/inventory/list", method = RequestMethod.GET)
	@ApiOperation(value = "所有库存产品列表", notes = "所有库存产品列表")
	public TableDataInfo<ApplyResponseProductInfo> getInventoryProductList(@Validated AppApplayProductSearchForm form) {
		startPage();
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOid());
		params.put("key", form.getKey());
		if (form.getProduct_type() == 0) {
			return getDataTable(odoService.selectDealerProductList4Phone(params));
		}
		return getDataTable(odoService.selectVendorProductList4Phone(params));
	}

}
