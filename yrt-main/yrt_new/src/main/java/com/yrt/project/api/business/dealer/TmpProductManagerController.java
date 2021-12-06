package com.yrt.project.api.business.dealer;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.*;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.dealer.service.ITmpProductService;
import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpProductComposeForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductImageForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductMDRFForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductSpecForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AuditForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.EditTmpProductForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.EditTmpProductMDRFForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.EditTmpProductSpecForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpIDSForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.ImageIdsForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpSearchProductForm;
import com.yrt.project.modular.product.domain.TmpProduct;
import com.yrt.project.modular.product.domain.TmpProductMDRF;
import com.yrt.project.modular.product.domain.TmpProductRelImage;
import com.yrt.project.modular.product.domain.TmpProductSpecification;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/business/tmp")
@Api(tags = "业务-待审核产品管理相关接口")
public class TmpProductManagerController extends BaseController {

	@Autowired
	private ITmpProductService tmpProductService;
	
	@Log(title = "business.tmp", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value="添加待审核产品", notes="添加待审核产品")
	public ResultInfo<TmpProduct> addTmpProduct(
			@Validated AddTmpProductForm form) {
		return ResultInfo.success(tmpProductService.addTmpProduct(form));
	}
	
	@Log(title = "business.tmp", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/compose/add", method = RequestMethod.POST)
	@ApiOperation(value="添加待审核产品及规格型号", notes="添加待审核产品及规格型号")
	public ResultInfo<TmpProduct> addTmpProductCompose(
			@RequestBody @Validated({TmpProductComposeForm.Add.class}) TmpProductComposeForm form) {
		return ResultInfo.success(tmpProductService.addOrEditTmpProductCompose(form, false));
	}
	
	@Log(title = "business.tmp", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/compose/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改待审核产品信息及规格型号", notes = "修改待审核产品信息及规格型号")
	public ResultInfo<TmpProduct> editTmpProduct(
			@RequestBody @Validated({TmpProductComposeForm.Update.class}) TmpProductComposeForm form) {
		return ResultInfo.success(tmpProductService.addOrEditTmpProductCompose(form, true));
	}
	
	@Log(title = "business.tmp", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改待审核产品信息", notes = "修改待审核产品信息")
	public ResultInfo<TmpProduct> editTmpProduct(
			@Validated EditTmpProductForm form) {
		return ResultInfo.success(tmpProductService.editTmpProduct(form));
	}
	
	@Log(title = "business.tmp", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除待审核产品信息", notes = "删除待审核产品信息")
	public AjaxResult delProduct(
			@Validated TmpIDSForm form) {
		return toAjax(tmpProductService.deleteTmpProudct(form));
	}
	
	@Log(title = "business.tmp", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "搜索待审核产品列表", notes = "搜索待审核产品列表")
	public TableDataInfo<TmpProduct> searchPublicedProductList(
			@Validated TmpSearchProductForm form) {
		PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
		return tmpProductService.searchtTmpProduct(form, pageNum, pageSize);
	}
	
	@Log(title = "business.tmp", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取待审核产品信息", notes = "获取待审核产品信息")
	public ResultInfo<TmpProduct> getTmpProduct(
			@Validated TmpIDSForm form) {
		return ResultInfo.success(tmpProductService.getTmpProductInfo(form));
	}
	
	//待审核规格型号
	@Log(title = "business.tmp", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/spec/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增规格型号", notes = "新增规格型号")
	public ResultInfo<TmpProductSpecification> addProductSpec(
			@Validated AddTmpProductSpecForm form) {
		return ResultInfo.success(tmpProductService.addTmpProductSpec(form));
	}

	@Log(title = "business.tmp", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/spec/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改规格型号", notes = "修改规格型号")
	public AjaxResult editProductSpec(@Validated EditTmpProductSpecForm form) {
		return toAjax(tmpProductService.editTmpProductSpec(form));
	}

	@Log(title = "business.tmp", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/spec/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除规格型号", notes = "删除规格型号")
	public AjaxResult delProductSpec(
			@Validated TmpIDSForm form) {
		return toAjax(tmpProductService.deleteTmpProductSpec(form));
	}

	@Log(title = "business.tmp", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/spec/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取产品规格型号列表", notes = "获取产品规格型号列表")
	public AjaxResult selectTmpProductSpecList(
			@Validated TmpIDSForm form) {
		return success().put("list", tmpProductService.selectTmpProductSpecList(form));
	}
	
	@Log(title = "business.tmp", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/spec/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取产品规格型号信息", notes = "获取产品规格型号信息")
	public AjaxResult selectTmpProductSpecInfo(
			@Validated TmpIDSForm form) {
		return success().put("list", tmpProductService.selectTmpProductSpecInfo(form));
	}
	
	//图片
	@Log(title = "business.tmp", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/image/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增产品图片", notes = "新增产品图片")
	public ResultInfo<TmpProductRelImage> addGoods(@Validated AddTmpProductImageForm form) {
		return ResultInfo.success(tmpProductService.addTmpProductRelImage(form));
	}
	
	@Log(title = "business.tmp", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/image/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除产品图片", notes = "删除产品图片")
	public AjaxResult delProductImage(
			@Validated ImageIdsForm form) {
		return toAjax(tmpProductService.delTmpProductImage(form));
	}

	@Log(title = "business.tmp", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/image/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取产品图片", notes = "获取产品图片")
	public AjaxResult getCertImage(
			@Validated ImageIdsForm form) {
		return success().put("images", tmpProductService.selectCertImageList(form));
	}
	
	//注册证
	@Log(title = "business.tmp", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/mdrf/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增医疗器材注册登记表", notes = "新增医疗器材注册登记表")
	public ResultInfo<TmpProductMDRF> addProductMDRF(
			@RequestBody @Valid AddTmpProductMDRFForm form) {
		return ResultInfo.success(tmpProductService.addTmpProductMDRF(form));
	}

	@Log(title = "business.tmp", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/mdrf/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改医疗器材注册登记表", notes = "修改医疗器材注册登记表")
	public AjaxResult editProductMDRF(
			@RequestBody @Valid EditTmpProductMDRFForm form) {
		return toAjax(tmpProductService.editTmpProductMDRF(form));
	}
	
	@Log(title = "business.tmp", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/mdrf/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除医疗器材注册登记表", notes = "删除医疗器材注册登记表")
	public AjaxResult delProductMDRF(
			@Validated TmpIDSForm form) {
		return toAjax(tmpProductService.delTmpProductMDRF(form));
	}
	
	@Log(title = "business.tmp", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/mdrf/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取产品医疗器材注册登记表列表", notes = "获取产品医疗器材注册登记表列表")
	public AjaxResult getProductMDRFList(
			@Validated TmpIDSForm form) {
		return success().put("list", tmpProductService.getTmpProductMDRFList(form));
	}
	
	@Log(title = "business.tmp", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/mdrf/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取产品医疗器材注册登记表", notes = "获取产品医疗器材注册登记表")
	public ResultInfo<TmpProductMDRF> getProductMDRF(
			@Validated TmpIDSForm form) {
		return ResultInfo.success(tmpProductService.getTmpProductMDRF(form));
	}
	
	//审批
	@Log(title = "business.tmp", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sendAudit", method = RequestMethod.PUT)
	@ApiOperation(value = "送审", notes = "送审")
	public AjaxResult audit(
			@Validated AuditForm form) {
		return toAjax(tmpProductService.sendAudit(form));
	}
	
}
