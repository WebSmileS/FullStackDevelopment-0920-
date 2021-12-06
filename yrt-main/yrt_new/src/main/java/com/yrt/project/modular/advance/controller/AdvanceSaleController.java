package com.yrt.project.modular.advance.controller;

import com.yrt.common.utils.bean.BeanUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.modular.advance.domain.AdvanceSale;
import com.yrt.project.modular.advance.domain.AdvanceSaleDetail;
import com.yrt.project.modular.advance.dto.*;
import com.yrt.project.modular.advance.service.AdvanceSaleDetailService;
import com.yrt.project.modular.advance.service.AdvanceSaleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/advance/sale")
@Api(tags = "业务-预售管理接口")
@RequiredArgsConstructor
public class AdvanceSaleController extends BaseController {

	private final AdvanceSaleService advanceSaleService;
	private final AdvanceSaleDetailService advanceSaleDetailService;

	@Log(title = "business.advance", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/addOrUpdate", method = RequestMethod.POST)
	@ApiOperation(value = "新增预售单/修改预售单", notes = "新增预售单/修改预售单, 如果是修改预售单需要传原预售单ID")
	public ResultInfo<GetAdvanceSaleDtoResp> addOrUpdateAdvanceSale(@RequestBody @Validated AddAdvanceSaleDtoReq advanceSaleDto){
		GetAdvanceSaleDtoResp dto = advanceSaleService.addOrUpdateAdvanceSale(advanceSaleDto);
		return ResultInfo.success(dto);
	}

	@Log(title = "business.advance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ApiOperation(value = "分页查询预售单", notes = "分页查询预售单")
	public TableDataInfo<GetAdvanceSaleDtoResp> getAdvanceSaleList(
			@RequestBody  @Validated GetAdvanceSaleConditionDtoReq conditionDto) {
		startPage();
		return getDataTable(advanceSaleService.getAdvanceSaleList(conditionDto));
	}

	@Log(title = "business.advance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/getDetailsByAdvanceId/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "查询预售单产品明细", notes = "查询预售单产品明细")
	public ResultList<AddAdvanceSaleDetailDtoResp> getDetailsByAdvanceId(@PathVariable("id") Long advanceSaleId){
		List<AddAdvanceSaleDetailDtoResp> details= advanceSaleDetailService.getDetailsByAdvanceSaleId(advanceSaleId).stream().map(e->{
			AddAdvanceSaleDetailDtoResp req = BeanUtils.clone(e, AddAdvanceSaleDetailDtoResp.class);
			req.setStatus(GetAdvanceSaleDtoResp.getSaleStatusByQuantity(req.getQuantity().intValue(),req.getSoldQuantity()));
			return req;
		}).collect(Collectors.toList());
		return ResultList.success(details);
	}

	@Log(title = "business.advance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/getAdvanceById/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "查询预售单,附带返回产品明细", notes = "查询预售单，附带返回产品明细")
	public ResultInfo<GetAdvanceSaleDtoResp> addAdvanceSale(@PathVariable("id") Long advanceSaleId){
		// 查询主单
		AdvanceSale advanceSale = advanceSaleService.getById(advanceSaleId);
		GetAdvanceSaleDtoResp resp = BeanUtils.clone(advanceSale, GetAdvanceSaleDtoResp.class);

		// 查询明细单
		List<AdvanceSaleDetail> details = advanceSaleDetailService.getDetailsByAdvanceSaleId(advanceSaleId);

		resp.setStatus(GetAdvanceSaleDtoResp.getSaleStatusByDetails(details));

		List<AddAdvanceSaleDetailDtoResp> dtoDetails = details.stream().map(e->{
			AddAdvanceSaleDetailDtoResp req = BeanUtils.clone(e, AddAdvanceSaleDetailDtoResp.class);
			req.setStatus(GetAdvanceSaleDtoResp.getSaleStatusByQuantity(req.getQuantity().intValue(),req.getSoldQuantity()));
			return req;
		}).collect(Collectors.toList());
		resp.setDetails(dtoDetails);
		return ResultInfo.success(resp);
	}

	@Log(title = "business.advance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/deleteAdvanceSale/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "删除预售单", notes = "删除预售单")
	public ResultInfo deleteAdvanceSale(@PathVariable("id") Long advanceSaleId){
		advanceSaleService.deleteAdvanceSale(advanceSaleId);
		return ResultInfo.success(null);
	}

	@Log(title = "business.advance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/checkAdvanceSale/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "确认预售单", notes = "确认预售单")
	public ResultInfo checkAdvanceSale(@PathVariable("id") Long advanceSaleId){
		advanceSaleService.checkAdvanceSale(advanceSaleId);
		return ResultInfo.success(null);
	}

	@Log(title = "business.advance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/cancel", method = RequestMethod.PUT)
	@ApiOperation(value = "回库", notes = "回库")
	public ResultInfo doCancelAdvanceSale(@RequestBody List<SalesDetailDtoReq> salesDtoReqs){
		advanceSaleService.transform(salesDtoReqs, 0);
		return ResultInfo.success(null);
	}

	@Log(title = "business.advance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sold", method = RequestMethod.PUT)
	@ApiOperation(value = "生成出库单", notes = "生成出库单")
	public ResultInfo sold(@RequestBody List<SalesDetailDtoReq> salesDtoReqs){
		advanceSaleService.transform(salesDtoReqs, 1);
		return ResultInfo.success(null);
	}

}