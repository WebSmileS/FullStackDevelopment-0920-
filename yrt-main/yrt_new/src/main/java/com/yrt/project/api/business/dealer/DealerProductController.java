package com.yrt.project.api.business.dealer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.exception.BusinessException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.*;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.dealer.service.IDealerProductService;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductImageForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductSpecForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.EditDealerProductForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.EditDealerProductSpecForm;
import com.yrt.project.api.platform.vo.product.SearchProductForm;
import com.yrt.project.modular.product.domain.DealerProduct;
import com.yrt.project.modular.product.domain.DealerProductRelImage;
import com.yrt.project.modular.product.domain.DealerProductSpecification;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/dealer/product")
@Api(tags = "业务-经销商自建产品管理接口")
public class DealerProductController extends BaseController {

	@Autowired
	private IDealerProductService productService;
	
	@Log(title = "business.dealer.product", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增产品", notes = "新增产品")
	public ResultInfo<DealerProduct> addDealerProduct(
			@Validated AddDealerProductForm product) {
		return ResultInfo.success(productService.insertDealerProduct(product));
	}

	@Log(title = "business.dealer.product", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit/{oid}/{pid}", method = RequestMethod.PUT)
	@ApiOperation(value = "修改产品信息", notes = "修改产品信息")
	public AjaxResult editProduct(@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@Validated EditDealerProductForm product) {
		if (!product.hasUpdate()) {
			throw new BusinessException(MessageUtils.message("update.param.not.null"));
		}
		return toAjax(productService.updateDealerProduct(pid, oid, product));
	}

	@Log(title = "business.dealer.product", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del/{oid}/{pid}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除产品信息", notes = "删除产品信息")
	public AjaxResult delProduct(@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid) {
		return toAjax(productService.deleteDealerProduct(pid, oid));
	}

	@Log(title = "business.dealer.product", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/image/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增产品相关图片", notes = "新增产品相关图片")
	public ResultInfo<DealerProductRelImage> addGoods(@Validated AddDealerProductImageForm form) {
		return ResultInfo.success(productService.insertDealerProductRelImage(form));
	}

	@Log(title = "platform.product", businessType = BusinessType.DELETE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/image/del/{did}/{pid}/{certType}/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除产品相关图片", notes = "删除产品相关图片")
	public AjaxResult delProductImage(@ApiParam(name="did",value="经销商ID",required=true) @PathVariable Long did,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="certType",value="图片类型",required=true) @PathVariable int certType,
			@ApiParam(name="id",value="图片ID",required=true) @PathVariable int id) {
		return toAjax(productService.delProductImage(did, pid, certType, id));
	}
	
	@Log(title = "business.dealer.product", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/image/{oid}/{pid}/{certType}", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品ID和经销商图片类型获取相关图片", notes = "根据产品ID和经销商图片类型获取相关图片")
	public AjaxResult getCertImage(
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="certType",value="图片类型",required=true) @PathVariable int certType) {
		List<DealerProductRelImage> list = productService.selectCertImageList(oid, pid, certType);
		return success().put("images", list);
	}

	@Log(title = "business.dealer.product", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{oid}/{pid}", method = RequestMethod.GET)
	@ApiOperation(value = "按照产品编号查询未删除的正常商品信息", notes = "按照产品编号查询未删除的正常商品信息")
	public ResultInfo<DealerProduct> getProductInfo(@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid) {
		return ResultInfo.success(productService.selectDealerProductInfo(pid, oid));
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.dealer.product", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "按条件搜索经销商自建产品列表(分页)", notes = "按条件搜索经销商自建产品列表(分页)")
	public TableDataInfo<DealerProduct> searchPublicedProductList(SearchProductForm form) {
		startPage();
		return getDataTable(productService.searchDealerProductList(form));
	}

	@Log(title = "business.dealer.product", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/spec/add", method = RequestMethod.POST)
	@ApiOperation(value = "根据经销商ID和产品ID新增规格型号", notes = "根据经销商ID和已发布产品ID新增规格型号")
	public ResultInfo<DealerProductSpecification> addProductSpec(@Validated AddDealerProductSpecForm form) {
		return ResultInfo.success(productService.insertDealerProductSpec(form));
	}

	@Log(title = "business.dealer.product", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/spec/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "根据经销商ID、产品ID和规格型号ID修改规格型号", notes = "根据经销商ID、已发布产品ID和规格型号ID修改规格型号")
	public AjaxResult editProductSpec(@Validated EditDealerProductSpecForm form) {
		return toAjax(productService.updateDealerProductSpec(form));
	}

	@Log(title = "business.dealer.product", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/spec/del/{oid}/{pid}/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "根据经销商ID和产品ID删除规格型号", notes = "根据经销商ID和已发布产品ID删除规格型号")
	public AjaxResult delProductSpec(@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="id",value="规格型号ID",required=true) @PathVariable Long id) {
		return toAjax(productService.deleteDealerProductSpec(oid, pid, id));
	}

	@Log(title = "business.dealer.product", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/spec/list/{oid}/{pid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据经销商ID和产品ID获取产品规格型号列表", notes = "根据经销商ID和已发布产品ID获取产品规格型号列表")
	public AjaxResult searchProductListBySource(@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid) {
		return success().put("list", productService.selectDealerProductSpecListById(oid, pid));
	}
}
