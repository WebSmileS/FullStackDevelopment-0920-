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
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.finance.vo.ApproveAdvancePaymentForm;
import com.yrt.project.api.business.finance.vo.SearchBusinessAdvancePaymentForm;
import com.yrt.project.api.business.invoice.AdvancePaymentService;
import com.yrt.project.modular.invoice.domain.AdvancePayment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/finance/advance")
@Api(tags = "财务-预付款管理接口")
public class FinanceAdvancePaymentController extends BaseController {
	
	private static final Logger log = LoggerFactory.getLogger(FinanceAdvancePaymentController.class);

	@Autowired AdvancePaymentService service;
	
	@Log(title = "business.advance", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approval", method = RequestMethod.PUT)
	@ApiOperation(value = "审核预付款", notes = "审核预付款")
	public AjaxResult approveAdvancePayment(
			@RequestBody @Validated ApproveAdvancePaymentForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.approveAdvancePayment(form, curUser));
	}
	
	@Log(title = "business.finance.invoice", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/cancel/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "作废预付款", notes = "作废预付款")
	public AjaxResult cancelAdvancePayment(
			@ApiParam(name="id",value="预付款ID",required=true) @PathVariable Long id,
			@ApiParam(name="reason",value="作废原因",required=true) @RequestParam String reason) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.cancelAdvancePayment(id, reason, curUser));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.advance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ApiOperation(value = "搜索预付款列表(分页)", notes = "搜索预付款列表(分页)")
	public TableDataInfo<AdvancePayment> searchAdvancePaymentList(
			@RequestBody  @Validated SearchBusinessAdvancePaymentForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.searchAdvancePaymentList(form, curUser, false));
	}
	
	@Log(title = "business.finance.invoice", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取预付款详情", notes = "获取预付款详情")
	public ResultInfo<AdvancePayment> selectGrnInvoiceInfo(
			@ApiParam(name="id",value="预付款ID",required=true) @PathVariable Long id) {
		return ResultInfo.success(service.selectAdvancePaymentInfo(id));
	}
	
	@Log(title = "business.finance.invoice", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
    @ApiOperation(value = "导出预付款", notes = "导出预付款")
    public void invoiceExport(
    		@Validated @RequestBody SearchBusinessAdvancePaymentForm form, 
    		HttpServletResponse response,
			HttpServletRequest request) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		List<AdvancePayment> list = service.searchAdvancePaymentList(form, curUser, true);
		ExcelUtil<AdvancePayment> util = new ExcelUtil<AdvancePayment>(AdvancePayment.class);
		AjaxResult result = util.exportExcel(list, "预付款");
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
