package com.yrt.project.api.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.project.modular.area.domain.AdministrativeDivision;
import com.yrt.project.modular.area.service.IAdministrativeDivisionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/comm/area")
@Api(tags = "公用-行政区划相关接口")
public class AdministrativeDivisionController extends BaseController {

	@Autowired
	private IAdministrativeDivisionService administrativeDivisionService;
	
//	@Log(title = "common.area", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/province", method = RequestMethod.GET)
	@ApiOperation(value="获取全部省份列表", notes="获取全部省份列表")
	public AjaxResult getProvinceList() {
		List<AdministrativeDivision> list = administrativeDivisionService.selectProvinceList();
		return success().put("province", list);
	}
	
//	@Log(title = "common.area", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/city/{id}", method = RequestMethod.GET)
	@ApiOperation(value="根据省份id查询所有的下级城市信息", notes="查询所有的下级城市信息")
	public AjaxResult getCityList(@PathVariable Long id) {
		List<AdministrativeDivision> list = administrativeDivisionService.selectChildrenList(id);
		return success().put("city", list);
	}
	
//	@Log(title = "common.area", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/county/{id}", method = RequestMethod.GET)
	@ApiOperation(value="根据城市id查询所有的下级区域信息", notes="查询所有的下级区域信息")
	public AjaxResult getCountyList(@PathVariable Long id) {
		List<AdministrativeDivision> list = administrativeDivisionService.selectChildrenList(id);
		return success().put("county", list);
	}
	
	
//	@Log(title = "common.area", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value="/list/{id}", method = RequestMethod.GET)
	@ApiOperation(value="根据id查询上级信息",notes="查询所有的上级区域信息")
	public AjaxResult getProvince(@PathVariable Long id) {
		AdministrativeDivision cur = administrativeDivisionService.selectDivisionById(id);
		administrativeDivisionService.selectParent(cur, cur.getParent_administrative_division_sn());
		
		return success().put("area",cur);
	}
}
