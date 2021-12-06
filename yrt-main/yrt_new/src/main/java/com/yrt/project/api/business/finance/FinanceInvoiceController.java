package com.yrt.project.api.business.finance;

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

import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.file.FileUtils;
import com.yrt.common.utils.poi.ExcelUtil;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.config.YrtConfig;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.finance.vo.ApproveOdoInvoiceForm;
import com.yrt.project.api.business.finance.vo.SearchBusinessInvoiceForm;
import com.yrt.project.api.business.invoice.InvoiceService;
import com.yrt.project.modular.invoice.domain.SystemInvoice;
import com.yrt.project.modular.invoice.domain.SystemInvoiceDetail;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/finance/invoice")
@Api(tags = "财务-发票管理接口")
public class FinanceInvoiceController extends BaseController {
	
	private static final Logger log = LoggerFactory.getLogger(FinanceInvoiceController.class);

	@Autowired InvoiceService service;
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.finance.invoice", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ApiOperation(value = "搜索发票列表(分页)", notes = "搜索发票列表(分页)")
	public TableDataInfo<SystemInvoice> searchOdoInvoiceList(
			@RequestBody @Validated SearchBusinessInvoiceForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.searchInvoiceList(form, curUser, false));
	}
	
	@Log(title = "business.finance.invoice", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取出入库发票详情", notes = "获取出入库发票详情")
	public ResultInfo<SystemInvoice> selectGrnInvoiceInfo(
			@ApiParam(name="id",value="发票ID",required=true) @PathVariable Long id) {
		return ResultInfo.success(service.selectInvoiceInfo(id));
	}
	
	@Log(title = "business.finance.invoice", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail/list/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "搜索发票明细列表", notes = "搜索发票明细列表")
	public ResultList<SystemInvoiceDetail> getInvoiceDetailList(
			@ApiParam(name="id",value="发票ID",required=true) @PathVariable Long id) {
		return ResultList.success(service.getInvoiceDetailList(id));
	}
	
	@Log(title = "business.finance.invoice", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/grn/approval/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "审批入库发票", notes = "审批入库发票")
	public AjaxResult approvalInvoice(
			@ApiParam(name="id",value="发票ID",required=true) @PathVariable Long id) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.approvalGrnInvoice(id, curUser));
	}
	
	@Log(title = "business.invoice", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/odo/approval", method = RequestMethod.PUT)
	@ApiOperation(value = "审批出库发票", notes = "审批出库发票")
	public AjaxResult updateOdoInvoice(
			@RequestBody @Validated ApproveOdoInvoiceForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.approvalOdoInvoice(form, curUser));
	}
	
	@Log(title = "business.finance.invoice", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/cancel/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "作废发票", notes = "作废发票")
	public AjaxResult cancelInvoice(
			@ApiParam(name="id",value="发票ID",required=true) @PathVariable Long id,
			@ApiParam(name="reason",value="作废原因",required=true) @RequestParam String reason) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.cancelInvoice(id, reason, curUser));
	}
	
	@Log(title = "business.finance.invoice", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
    @ApiOperation(value = "导出出入库发票", notes = "导出出入库发票")
    public void invoiceExport(
    		@Validated @RequestBody SearchBusinessInvoiceForm form, 
    		HttpServletResponse response,
			HttpServletRequest request) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		List<SystemInvoice> list = service.searchInvoiceList(form, curUser, true);
		ExcelUtil<SystemInvoice> util = new ExcelUtil<SystemInvoice>(SystemInvoice.class);
		AjaxResult result = util.exportExcel(list, "出入库发票");
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
}
