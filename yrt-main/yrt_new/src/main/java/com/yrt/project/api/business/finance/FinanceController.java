package com.yrt.project.api.business.finance;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.SystemType;
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
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.finance.vo.SearchInventoryForm;
import com.yrt.project.api.business.finance.vo.SearchRecordForm;
import com.yrt.project.api.common.service.CommService;
import com.yrt.project.modular.finance.domain.GrnRecord;
import com.yrt.project.modular.finance.domain.InventoryRecord;
import com.yrt.project.modular.finance.domain.OdoRecord;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/finance")
@Api(tags = "财务-财务接口")
public class FinanceController extends BaseController {
	
	private static final Logger log = LoggerFactory.getLogger(FinanceController.class);

	@Autowired FinanceService service;
	@Autowired private CommService commService;
	
	@Log(title = "business.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/we/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "获取仓库人员", notes = "获取仓库人员")
	public AjaxResult getWarehouseEmployeeList(
			@ApiParam(name="oid",value="机构ID",required=true) @PathVariable Long oid,
			@ApiParam(name="wid",value="仓库ID") Long wid) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		return success().put("info", commService.getWarehouseEmployeeList(systemType, oid, wid, 0, false));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.finance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/grn/list", method = RequestMethod.POST)
	@ApiOperation(value = "入库记录查询(分页)", notes = "入库记录查询(分页)")
	public TableDataInfo<GrnRecord> getGrnRecordList(
			@Validated @RequestBody SearchRecordForm form) {
		startPage();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		return getDataTable(service.getGrnRecordList(systemType, form));
	}
	
	@Log(title = "business.finance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/grn/sum", method = RequestMethod.POST)
	@ApiOperation(value = "入库记录金额合计", notes = "入库记录金额合计")
	public AjaxResult sumGrnRecordList(
			@Validated @RequestBody SearchRecordForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		BigDecimal sum = service.sumGrnRecordList(systemType, form);//.divide(new BigDecimal(1), 4, BigDecimal.ROUND_UP);
		return success().put("sum", Utils.isEmpty(sum)?BigDecimal.ZERO:sum);
	}
	
	@Log(title = "business.finance", businessType = BusinessType.EXPORT)
	@PostMapping("/grn/export")
    @ApiOperation(value = "导出入库记录", notes = "导出入库记录")
    public void grnExport(
    		@Validated @RequestBody SearchRecordForm form, 
    		HttpServletResponse response,
			HttpServletRequest request) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		List<GrnRecord> list = service.getGrnRecordList(systemType, form);
		ExcelUtil<GrnRecord> util = new ExcelUtil<GrnRecord>(GrnRecord.class);
		AjaxResult result = util.exportExcel(list, "入库记录");
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
	@Log(title = "business.finance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/odo/list", method = RequestMethod.POST)
	@ApiOperation(value = "出库记录查询(分页)", notes = "出库记录查询(分页)")
	public TableDataInfo<OdoRecord> getOdoRecordList(
			@Validated @RequestBody SearchRecordForm form) {
		startPage();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		return getDataTable(service.getOdoRecordList(systemType, form));
	}
	
	@Log(title = "business.finance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/odo/sum", method = RequestMethod.POST)
	@ApiOperation(value = "出库记录金额合计", notes = "出库记录金额合计")
	public AjaxResult sumOdoRecordList(
			@Validated @RequestBody SearchRecordForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		BigDecimal sum = service.sumOdoRecordList(systemType, form);//.divide(new BigDecimal(1), 4, BigDecimal.ROUND_UP);
		return success().put("sum", Utils.isEmpty(sum)?BigDecimal.ZERO:sum);
	}
	
	@Log(title = "business.finance", businessType = BusinessType.EXPORT)
	@PostMapping("/odo/export")
    @ApiOperation(value = "导出出库记录", notes = "导出出库记录")
    public void odoExport(
    		@Validated @RequestBody SearchRecordForm form,
    		HttpServletResponse response,
			HttpServletRequest request) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		List<OdoRecord> list = service.getOdoRecordList(systemType, form);
		ExcelUtil<OdoRecord> util = new ExcelUtil<OdoRecord>(OdoRecord.class);
		AjaxResult result = util.exportExcel(list, "出库记录");
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
	@Log(title = "business.finance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/inventory/list", method = RequestMethod.POST)
	@ApiOperation(value = "库存记录查询(分页)", notes = "库存记录查询(分页)")
	public TableDataInfo<InventoryRecord> getInventoryRecordList(
			@Validated @RequestBody SearchInventoryForm form) {
		startPage();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		return getDataTable(service.getInventoryRecordList(systemType, form));
	}
	
	@Log(title = "business.finance", businessType = BusinessType.EXPORT)
	@PostMapping("/inventory/export")
    @ApiOperation(value = "导出库存记录", notes = "导出库存记录")
    public void inventoryRecordExport(
    		@Validated @RequestBody SearchInventoryForm form,
    		HttpServletResponse response,
			HttpServletRequest request) {
		SystemType systemType = ServletUtils.getCurUserSystemType();

		// TODO 为什么这里导出只会导出仓库人员有值的数据？
		List<InventoryRecord> list = service.getInventoryRecordList(systemType, form);
		ExcelUtil<InventoryRecord> util = new ExcelUtil<InventoryRecord>(InventoryRecord.class);
		AjaxResult result = util.exportExcel(list, "库存记录");
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
