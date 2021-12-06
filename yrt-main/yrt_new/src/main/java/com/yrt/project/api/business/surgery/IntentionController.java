package com.yrt.project.api.business.surgery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.lang.KeyValue;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.project.api.business.surgery.service.IIntentionService;
import com.yrt.project.api.business.surgery.vo.SearchIntentionForm;
import com.yrt.project.modular.surgery.domain.OrthopaedicIntention;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/platform/intention")
@Api(tags = "平台-骨科合作意向接口")
public class IntentionController extends BaseController {

	@Autowired
	private IIntentionService service;
	
	//骨科合作意向
	@Log(title = "business.surgery", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增骨科合作意向", notes = "新增骨科合作意向")
	public ResultInfo<OrthopaedicIntention> addOrthopaedicIntention(@RequestBody @Validated OrthopaedicIntention form) {
		OrthopaedicIntention intention = service.addOrthopaedicIntention(form);
		if(intention != null) {
			return ResultInfo.success(intention);
		}
		return ResultInfo.error();
	}
	
	@Log(title = "business.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改骨科合作意向", notes = "修改骨科合作意向")
	public ResultInfo<OrthopaedicIntention> editOrthopaedicIntention(
			@RequestBody @Validated OrthopaedicIntention form) {
		OrthopaedicIntention intention = service.editOrthopaedicIntention(form);
		if(intention != null) {
			return ResultInfo.success(intention);
		}
		return ResultInfo.error();
	}
	
	@Log(title = "business.surgery", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del/{hid}/{did}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除骨科合作意向", notes = "删除骨科合作意向")
	public AjaxResult delOrthopaedicIntention(
			@ApiParam(name="hid",value="医院ID",required=true) @PathVariable Long hid,
			@ApiParam(name="did",value="经销商ID",required=true) @PathVariable Long did) {
		return toAjax(service.delOrthopaedicIntention(hid, did));
	}
	
	@Log(title = "business.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "搜索合作意向列表", notes = "搜索合作意向列表")
	public ResultList<OrthopaedicIntention> searchOrthopaedicIntentionList(@Validated SearchIntentionForm form) {
		return ResultList.success(service.searchOrthopaedicIntentionList(form));
	}
	
	@Log(title = "business.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{hid}/{did}", method = RequestMethod.GET)
	@ApiOperation(value = "获取合作意向信息", notes = "获取合作意向信息")
	public ResultInfo<OrthopaedicIntention> searchOrthopaedicIntention(
			@ApiParam(name="hid",value="医院ID",required=true) @PathVariable Long hid,
			@ApiParam(name="did",value="经销商ID",required=true) @PathVariable Long did) {
		return ResultInfo.success(service.selectOrthopaedicIntentionInfo(hid, did));
	}
	
	@Log(title = "business.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/available/dealer/{hospitalId}/{typeId}/{brandId}", method = RequestMethod.GET)
	@ApiOperation(value = "意向内的可选dealer(特定医院、手术、品牌): key=dealerId, value=dealerName", notes = "手术包 可选dealer")
	public ResultList<KeyValue<Long, String>> getAvailableDealer(
			@ApiParam(name="hospitalId",value="医院ID",required=true) @PathVariable Long hospitalId,
			@ApiParam(name="typeId",value="手术类型ID",required=true) @PathVariable Long typeId,
			@ApiParam(name="brandId",value="品牌ID",required=true) @PathVariable Long brandId) {
		return ResultList.success(service.getAvailableDealer(hospitalId, typeId, brandId));
	}
	
	@Log(title = "business.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/available/hospital/{dealerId}", method = RequestMethod.GET)
	@ApiOperation(value = "意向内的可选hospital(特定经销商对应的): key=hospitalId, value=hospitalName", notes = "手术计划(经销商制定) 可选hospital")
	public ResultList<KeyValue<Long, String>> getAvailableHospital(
			@ApiParam(name="dealerId",value="销销商ID",required=true) @PathVariable Long dealerId) {
		return ResultList.success(service.getAvailableHospital(dealerId));
	}
}
