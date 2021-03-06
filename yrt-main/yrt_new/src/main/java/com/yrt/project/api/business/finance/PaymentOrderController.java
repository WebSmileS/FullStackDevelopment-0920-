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
@Api(tags = "??????-???????????????")
public class PaymentOrderController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(PaymentOrderController.class);
	
	@Autowired PaymentOrderService service;
	@Autowired AdvancePaymentService advanceService;
	@Autowired InvoiceService invoiceService;
	
	@Log(title = "business.finance.payment", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "???????????????", notes = "???????????????")
	public ResultInfo<PaymentOrder> insertPayment(
			@RequestBody @Validated PaymentForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.insertPayment(form, curUser));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "???????????????", notes = "???????????????")
	public ResultInfo<PaymentOrder> updatePayment(
			@RequestBody @Validated PaymentForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.updatePayment(form, curUser));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/pay", method = RequestMethod.PUT)
	@ApiOperation(value = "??????", notes = "??????")
	public AjaxResult pay(
			@RequestBody @Validated List<Long> list) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.pay(list, curUser));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/recorded", method = RequestMethod.PUT)
	@ApiOperation(value = "??????", notes = "??????")
	public AjaxResult recorded(
			@RequestBody @Validated List<Long> list) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.recorded(list, curUser));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/cancel/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "???????????????", notes = "???????????????")
	public AjaxResult cancelPayment(
			@ApiParam(name="id",value="?????????ID",required=true) @PathVariable Long id,
			@ApiParam(name="reason",value="????????????",required=true) @RequestParam String reason) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.cancelPayment(id, reason, curUser));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "???????????????", notes = "???????????????")
	public AjaxResult deletePayment(
			@RequestBody List<Long> list) {
		return toAjax(service.deletePayment(list));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "?????????????????????", notes = "?????????????????????")
	public ResultInfo<PaymentOrder> selectPaymentInfo(
			@ApiParam(name="id",value="?????????ID",required=true) @PathVariable Long id) {
		return ResultInfo.success(service.selectPaymentInfo(id));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.finance.payment", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ApiOperation(value = "?????????????????????(??????)", notes = "?????????????????????(??????)")
	public TableDataInfo<PaymentOrder> searchPaymentList(
			@RequestBody  @Validated SearchPaymentForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.searchPaymentList(form, curUser));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.finance.payment", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail/list/{id}", method = RequestMethod.POST)
	@ApiOperation(value = "???????????????????????????", notes = "???????????????????????????")
	public TableDataInfo<PaymentOrderDetail> searchReceiveDetailList(
			@ApiParam(name="id",value="?????????ID",required=true) @PathVariable Long id) {
		return getDataTable(service.searchPaymentDetailList(id));
	}
	
	@Log(title = "business.finance.payment", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
    @ApiOperation(value = "???????????????", notes = "???????????????")
    public void paymentExport(
    		@Validated @RequestBody SearchPaymentForm form, 
    		HttpServletResponse response,
			HttpServletRequest request) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		List<PaymentOrder> list = service.searchPaymentList(form, curUser, true);
		ExcelUtil<PaymentOrder> util = new ExcelUtil<PaymentOrder>(PaymentOrder.class);
		AjaxResult result = util.exportExcel(list, "?????????");
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
			log.error("??????????????????", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.finance.payment", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/invoice/list/{type}", method = RequestMethod.POST)
	@ApiOperation(value = "????????????????????????(??????)", notes = "????????????????????????(??????)")
	public TableDataInfo<PaymentDetailForm> getSystemInvoiceList(
			@ApiParam(name="type",value="?????????????????? 0-???????????? 1-?????????",required=true) @PathVariable int type,
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
