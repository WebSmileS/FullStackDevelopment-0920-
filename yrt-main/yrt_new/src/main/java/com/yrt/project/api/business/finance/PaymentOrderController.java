package com.yrt.project.api.business.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.InvoiceStatus;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemInvoiceType;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.Utils;
import com.yrt.common.utils.file.FileUtils;
import com.yrt.common.utils.poi.ExcelUtil;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.config.YrtConfig;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.finance.vo.PaymentDetailForm;
import com.yrt.project.api.business.finance.vo.PaymentForm;
import com.yrt.project.api.business.finance.vo.SearchBusinessAdvancePaymentForm;
import com.yrt.project.api.business.finance.vo.SearchBusinessInvoiceForm;
import com.yrt.project.api.business.finance.vo.SearchPaymentForm;
import com.yrt.project.api.business.finance.vo.SearchPaymentInvoiceForm;
import com.yrt.project.api.business.invoice.AdvancePaymentService;
import com.yrt.project.api.business.invoice.InvoiceService;
import com.yrt.project.modular.finance.domain.PaymentOrder;
import com.yrt.project.modular.finance.domain.PaymentOrderDetail;
import com.yrt.project.modular.invoice.domain.AdvancePayment;
import com.yrt.project.modular.invoice.domain.SystemInvoice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/finance/payment")
@Api(tags = "财务-付款单接口")
public class PaymentOrderController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(PaymentOrderController.class);
	
	@Autowired PaymentOrderService service;
	@Autowired AdvancePaymentService advanceService;
	@Autowired InvoiceService invoiceService;
	
	@Log(title = "business.finance.payment", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增付款单", notes = "新增付款单")
	public ResultInfo<PaymentOrder> insertPayment(
			@RequestBody @Validated PaymentForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.insertPayment(form, curUser));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑付款单", notes = "编辑付款单")
	public ResultInfo<PaymentOrder> updatePayment(
			@RequestBody @Validated PaymentForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.updatePayment(form, curUser));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/pay", method = RequestMethod.PUT)
	@ApiOperation(value = "付款", notes = "付款")
	public AjaxResult pay(
			@RequestBody @Validated List<Long> list) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.pay(list, curUser));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/recorded", method = RequestMethod.PUT)
	@ApiOperation(value = "入账", notes = "入账")
	public AjaxResult recorded(
			@RequestBody @Validated List<Long> list) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.recorded(list, curUser));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/cancel/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "作废付款单", notes = "作废付款单")
	public AjaxResult cancelPayment(
			@ApiParam(name="id",value="付款单ID",required=true) @PathVariable Long id,
			@ApiParam(name="reason",value="作废原因",required=true) @RequestParam String reason) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.cancelPayment(id, reason, curUser));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除付款单", notes = "删除付款单")
	public AjaxResult deletePayment(
			@RequestBody List<Long> list) {
		return toAjax(service.deletePayment(list));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取付款单详情", notes = "获取付款单详情")
	public ResultInfo<PaymentOrder> selectPaymentInfo(
			@ApiParam(name="id",value="付款单ID",required=true) @PathVariable Long id) {
		return ResultInfo.success(service.selectPaymentInfo(id));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.finance.payment", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ApiOperation(value = "搜索付款单列表(分页)", notes = "搜索付款单列表(分页)")
	public TableDataInfo<PaymentOrder> searchPaymentList(
			@RequestBody  @Validated SearchPaymentForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.searchPaymentList(form, curUser));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.finance.payment", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail/list/{id}", method = RequestMethod.POST)
	@ApiOperation(value = "搜索付款单明细列表", notes = "搜索付款单明细列表")
	public TableDataInfo<PaymentOrderDetail> searchReceiveDetailList(
			@ApiParam(name="id",value="付款单ID",required=true) @PathVariable Long id) {
		return getDataTable(service.searchPaymentDetailList(id));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
    @ApiOperation(value = "导出付款单", notes = "导出付款单")
    public void paymentExport(
    		@Validated @RequestBody SearchPaymentForm form, 
    		HttpServletResponse response,
			HttpServletRequest request) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		List<PaymentOrder> list = service.searchPaymentList(form, curUser, true);
		ExcelUtil<PaymentOrder> util = new ExcelUtil<PaymentOrder>(PaymentOrder.class);
		AjaxResult result = util.exportExcel(list, "付款单");
		try {
			String fileName = String.valueOf(result.get("msg"));
			String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
			String filePath = YrtConfig.getDownloadPath() + fileName;

			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition",
					"attachment;fileName=" + setFileDownloadHeader(request, realFileName));
			FileUtils.writeBytes(filePath, response.getOutputStream());
			FileUtils.deleteFile(filePath);
		} catch (Exception e) {
			log.error("下载文件失败", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.finance.payment", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/invoice/list/{type}", method = RequestMethod.POST)
	@ApiOperation(value = "搜索系统发票列表(分页)", notes = "搜索系统发票列表(分页)")
	public TableDataInfo<PaymentDetailForm> getSystemInvoiceList(
			@ApiParam(name="type",value="系统发票类型 0-入库发票 1-预付款",required=true) @PathVariable int type,
			@RequestBody  @Validated SearchPaymentInvoiceForm form){
		List<PaymentDetailForm> res = new ArrayList<>();
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		
		startPage();
		if(type == 0) {
			SearchBusinessInvoiceForm invoiceForm = Utils.clone(form, SearchBusinessInvoiceForm.class);
			invoiceForm.setInvoice_status(InvoiceStatus.NOT_WRITTEN_OFF);
			invoiceForm.setIsCancel(0);
			invoiceForm.setSystem_invoice_type(SystemInvoiceType.GRN_INVOICE.value());
			List<SystemInvoice> list = invoiceService.searchInvoiceList(invoiceForm, curUser, false);
			TableDataInfo<SystemInvoice> page = getDataTable(list);
			for (SystemInvoice invoice : list) {
				BigDecimal usedPrice = service.getUsedInvoicePrice(curUser.getOrgId(), invoice.getId(), RefVoucherType.GRN_INVOICE);
				PaymentDetailForm detail = new PaymentDetailForm();
				detail.setSn(invoice.getSystem_invoice_sn());
				detail.setInvoice_no(invoice.getInvoice_no());
				detail.setInvoice_price(invoice.getInvoice_price().subtract(usedPrice));
				detail.setVoucher_type_c(RefVoucherType.GRN_INVOICE.value());
				detail.setVoucher_inner_sn_c(invoice.getId());
				res.add(detail);
			}
			TableDataInfo<PaymentDetailForm> rspData = new TableDataInfo<PaymentDetailForm>();
	        rspData.setCode(0);
	        rspData.setRows(res);
	        rspData.setTotal(page.getTotal());
	        
	        return rspData;
		}else {
			SearchBusinessAdvancePaymentForm advanceform = Utils.clone(form, SearchBusinessAdvancePaymentForm.class);
			advanceform.setInvoice_status(InvoiceStatus.NOT_WRITTEN_OFF);
			advanceform.setIsCancel(0);
			List<AdvancePayment> list = advanceService.searchAdvancePaymentList(advanceform, curUser, false);
			TableDataInfo<AdvancePayment> page = getDataTable(list);
			for (AdvancePayment invoice : list) {
				BigDecimal usedPrice = service.getUsedInvoicePrice(curUser.getOrgId(), invoice.getId(), RefVoucherType.ADVANCE_PAYMENT);
				PaymentDetailForm detail = new PaymentDetailForm();
				detail.setSn(invoice.getPayment_sn());
				detail.setInvoice_no("");
				detail.setInvoice_price(invoice.getPayment_price().subtract(usedPrice));
				detail.setVoucher_type_c(RefVoucherType.ADVANCE_PAYMENT.value());
				detail.setVoucher_inner_sn_c(invoice.getId());
				res.add(detail);
			}
			TableDataInfo<PaymentDetailForm> rspData = new TableDataInfo<PaymentDetailForm>();
			rspData.setCode(0);
			rspData.setRows(res);
			rspData.setTotal(page.getTotal());
			
			return rspData;
		}
	}
}
