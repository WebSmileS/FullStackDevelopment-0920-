/**
 * 
 */
package com.yrt.project.api.external.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.BillsType;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.warehouse.service.IOdoService;
import com.yrt.project.modular.warehouse.domain.TransferVoucher;
import com.yrt.project.modular.warehouse.domain.TransferVoucherDetail;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Runner
 *
 */
@RestController
@RequestMapping("/v1/external/odo")
@Api(tags = "外部调用-医院调单入库接口")
public class ExternalOdoController extends BaseController {

	@Autowired ExternalOdoServiceImpl externalService;
	@Autowired IOdoService service;
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.receipt", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/list", method = RequestMethod.GET)
	@ApiOperation(value = "可用做入库调单的单据列表", notes = "可用做入库调单的单据列表")
	public TableDataInfo<TransferVoucher> getTransferOdoList(
			@ApiParam(name="key",value="搜索关键字(单据号)",required=false) String key) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(externalService.getTransferOdoList(ctxUser.getSystemType(), ctxUser.getOrgId(), key));
	}
	
	@Log(title = "business.receipt", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/detail", method = RequestMethod.GET)
	@ApiOperation(value = "被调单的单据明细列表", notes = "被调单的单据明细列表")
	public ResultList<TransferVoucherDetail> getTransferOdoDetailList(
			@ApiParam(name="odoId",value="单据内部编号(odo_inner_sn)",required=true) @RequestParam(required=true)Long odoId,
			@ApiParam(name="billsType",value="单据类型",required=true) @RequestParam(required=true)BillsType billsType) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		return ResultList.success(externalService.getTransferOdoDetailList(ctxUser.getOrgId(), odoId, billsType));
	}
	
	/*
	 * @Log(title = "business.external.record", businessType = BusinessType.OTHER,
	 * operatorType = OperatorType.EXTERNAL_CALL)
	 * 
	 * @RequestMapping(value = "/info", method = RequestMethod.GET)
	 * 
	 * @ApiOperation(value = "查看出库单详情", notes = "查看出库单详情") public ResultInfo<Odo>
	 * selectSaleRecordInfo(
	 * 
	 * @Validated OdoIDForm form) { return
	 * ResultInfo.success(service.selectOdoInfo(form)); }
	 */
}
