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
import com.yrt.project.api.business.dealer.service.IDealerProductUnitService;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductUnitForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.UnitIDForm;
import com.yrt.project.api.platform.vo.product.EditProductUnitForm;
import com.yrt.project.modular.product.domain.DealerProductUnit;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/dealer/unit")
@Api(tags = "业务-经销商自建产品单位管理接口")
public class DealerProductUnitController extends BaseController {

	@Autowired
	private IDealerProductUnitService unitService;
	
	@Log(title = "business.dealer.unit", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增产品单位", notes = "新增产品单位")
	public ResultInfo<DealerProductUnit> addDealerProductUnit(@Validated AddDealerProductUnitForm form) {
		return ResultInfo.success(unitService.insertDealerProductUnit(form));
	}

	@Log(title = "business.dealer.unit", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit/{oid}/{pid}/{psid}/{puid}", method = RequestMethod.PUT)
	@ApiOperation(value = "修改产品单位", notes = "修改产品单位")
	public AjaxResult editProductUnit(@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid,
			@ApiParam(name="puid",value="产品单位ID",required=true) @PathVariable Long puid,
			@Validated EditProductUnitForm form) {
		if (!form.hasUpdate()) {
			throw new BusinessException(MessageUtils.message("update.param.not.null"));
		}
		return toAjax(unitService.updateDealerProductUnit(oid, pid, psid, puid, form));
	}
	
	@Log(title = "business.dealer.unit", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除产品单位", notes = "删除产品单位")
	public AjaxResult delProductUnit(@Validated UnitIDForm form) {
		return toAjax(unitService.delDealerProductUnit(form));
	}
	
	@Log(title = "business.dealer.unit", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{oid}/{pid}/{psid}/{puid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品单位ID查询单位信息", notes = "根据产品单位ID查询单位信息")
	public ResultInfo<DealerProductUnit> selectProductUnitInfo(
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid,
			@ApiParam(name="puid",value="产品单位ID",required=true) @PathVariable Long puid) {
		return ResultInfo.success(unitService.selectDealerProductUnitInfo(oid, pid, psid, puid));
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.dealer.unit", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list/{oid}/{pid}/{psid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品ID查询所有产品单位(分页)", notes = "根据产品ID查询所有产品单位(分页)")
	public TableDataInfo<DealerProductUnit> selectProductUnitList(@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid) {
		startPage();
		return getDataTable(unitService.selectDealerProductUnitList(oid, pid, psid));
	}
	
	@Log(title = "business.dealer.unit", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list/{oid}/{pid}/{psid}/{pupid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品ID和产品单位上级ID查询所有下级产品单位", notes = "根据产品ID和产品单位上级ID查询所有下级产品单位")
	public AjaxResult selectProductUnitListByPid(
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid,
			@ApiParam(name="pupid",value="上级产品单位ID",required=true) @PathVariable Long pupid) {
		List<DealerProductUnit> list = unitService.selectDealerProductUnitListByPid(oid, pid, psid, pupid);
		return success().put("list", list);
	}
	
	@Log(title = "business.dealer.unit", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/tree/{oid}/{pid}/{psid}", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品ID查询所有产品单位以树的方式返回", notes = "根据产品ID查询所有产品单位以树的方式返回")
	public AjaxResult selectAllProductUnitList(@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid) {
		return success().put("tree" ,unitService.selectAllDealerProductUnitList(oid, pid, psid));
	}
	
	@Log(title = "common.category", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value="/parents/{oid}/{pid}/{psid}/{puid}", method = RequestMethod.GET)
	@ApiOperation(value="根据id查询所有上级产品单位",notes="根据id查询所有上级产品单位")
	public ResultInfo<DealerProductUnit> getParentsProductUnitList(
			@ApiParam(name="oid",value="经销商ID",required=true) @PathVariable Long oid,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid,
			@ApiParam(name="puid",value="产品单位ID",required=true) @PathVariable Long puid) {
		
		DealerProductUnit unit = unitService.selectDealerProductUnitInfo(oid, pid, psid, puid);
		unitService.selectParent(unit);
		return ResultInfo.success(unit);
	}
	
	@Log(title = "common.category", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value="/min/{did}/{pid}/{psid}/{puid}", method = RequestMethod.GET)
	@ApiOperation(value="换算产品最小单位",notes="换算产品最小单位")
	public ResultInfo<Object> calculateProductMinUnit(
			@ApiParam(name="did",value="厂商ID",required=true) @PathVariable Long did,
			@ApiParam(name="pid",value="产品ID",required=true) @PathVariable Long pid,
			@ApiParam(name="psid",value="产品规格型号ID",required=true) @PathVariable Long psid,
			@ApiParam(name="puid",value="产品单位ID",required=true) @PathVariable Long puid) {
		
		return ResultInfo.success(unitService.calculateProductMinUnit(did, pid, psid, puid));
	}
}
