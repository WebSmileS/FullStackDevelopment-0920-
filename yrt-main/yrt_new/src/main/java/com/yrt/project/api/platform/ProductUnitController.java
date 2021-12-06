package com.yrt.project.api.platform;

import java.math.BigDecimal;
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
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.project.api.business.dealer.vo.dealerproduct.UnitIDForm;
import com.yrt.project.api.platform.service.IProductUnitService;
import com.yrt.project.api.platform.vo.product.AddProductUnitForm;
import com.yrt.project.api.platform.vo.product.EditProductUnitForm;
import com.yrt.project.modular.product.domain.ProductUnit;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/platform/unit")
@Api(tags = "平台-产品单位管理相关接口")
public class ProductUnitController extends BaseController {

	@Autowired
	private IProductUnitService unitService;
	
	@Log(title = "platform.unit", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增产品单位", notes = "新增产品单位")
	public ResultInfo<ProductUnit> addProductUnit(@Validated AddProductUnitForm form) {
		ProductUnit unit = unitService.insertProductUnit(form);
		if(unit != null) {
			return ResultInfo.success(unit);
		}
		return ResultInfo.error();
	}

	@Log(title = "platform.unit", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/edit/{vid}/{pid}/{psid}/{puid}", method = RequestMethod.PUT)
	@ApiOperation(value = "修改产品单位", notes = "修改产品单位")
	public AjaxResult editProductUnit(@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid,
			@ApiParam(name="puid",value="产品单位ID",required=true) @PathVariable Long puid,
			@Validated EditProductUnitForm form) {
		if (!form.hasUpdate()) {
			throw new BusinessException(MessageUtils.message("update.param.not.null"));
		}
		return toAjax(unitService.updateProductUnit(vid, pid, psid, puid, form));
	}
	
	@Log(title = "business.unit", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除产品单位", notes = "删除产品单位")
	public AjaxResult delProductUnit(@Validated UnitIDForm form) {
		return toAjax(unitService.delProductUnit(form));
	}
	
	@Log(title = "platform.unit", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/info/{vid}/{pid}/{psid}/{puid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品单位ID查询单位信息", notes = "根据产品单位ID查询单位信息")
	public ResultInfo<ProductUnit> selectProductUnitInfo(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid,
			@ApiParam(name="puid",value="产品单位ID",required=true) @PathVariable Long puid) {
		return ResultInfo.success(unitService.selectProductUnitInfo(vid, pid, psid, puid));
	}

	@Log(title = "platform.unit", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/list/{vid}/{pid}/{psid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品ID查询所有产品单位", notes = "根据产品ID查询所有产品单位")
	public AjaxResult selectProductUnitList(@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid) {
		return success().put("list", unitService.selectProductUnitList(vid, pid, psid));
	}
	
	@Log(title = "platform.unit", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/list/{vid}/{pid}/{psid}/{pupid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品ID和产品单位上级ID查询所有下级产品单位", notes = "根据产品ID和产品单位上级ID查询所有下级产品单位")
	public AjaxResult selectProductUnitListByPid(@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid,
			@ApiParam(name="pupid",value="上级产品单位ID",required=true) @PathVariable Long pupid) {
		List<ProductUnit> list = unitService.selectProductUnitListByPid(vid, pid, psid, pupid);
		return success().put("list", list);
	}
	
	@Log(title = "platform.unit", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/tree/{vid}/{pid}/{psid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品ID查询所有产品单位以树的方式返回", notes = "根据产品ID查询所有产品单位以树的方式返回")
	public AjaxResult selectAllProductUnitList(@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid) {
		return success().put("tree" ,unitService.selectAllProductUnitList(vid, pid, psid));
	}
	
	@Log(title = "common.category", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value="/parents/{vid}/{pid}/{psid}/{puid}", method = RequestMethod.GET)
	@ApiOperation(value="根据id查询所有上级产品单位",notes="根据id查询所有上级产品单位")
	public ResultInfo<ProductUnit> getParentsProductUnitList(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid,
			@ApiParam(name="puid",value="产品单位ID",required=true) @PathVariable Long puid) {
		
		ProductUnit unit = unitService.selectProductUnitInfo(vid, pid, psid, puid);
		unitService.selectParent(unit);
		return ResultInfo.success(unit);
	}
	
	@Log(title = "common.category", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value="/min/{vid}/{pid}/{psid}/{puid}", method = RequestMethod.GET)
	@ApiOperation(value="换算产品最小单位",notes="换算产品最小单位")
	public ResultInfo<BigDecimal> calculateProductMinUnit(
			@ApiParam(name="vid",value="厂商ID",required=true) @PathVariable Long vid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid,
			@ApiParam(name="puid",value="产品单位ID",required=true) @PathVariable Long puid) {
		
		return ResultInfo.success(unitService.calculateProductMinUnit(vid, pid, psid, puid));
	}
}
