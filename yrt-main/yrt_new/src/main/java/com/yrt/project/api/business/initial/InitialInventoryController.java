package com.yrt.project.api.business.initial;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.file.FileException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.poi.ExcelUtil;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.initial.service.InitialInventoryService;
import com.yrt.project.api.business.initial.vo.InitialSearchProductForm;
import com.yrt.project.api.business.initial.vo.InventoryProductForm;
import com.yrt.project.modular.initial.domain.ImportInventoryProduct;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/initial")
@Api(tags = "业务-期初库存接口")
public class InitialInventoryController extends BaseController {
	
	private static final Logger log = LoggerFactory.getLogger(InitialInventoryController.class);
	
	@Autowired private InitialInventoryService inventoryService;
	
	@Log(title = "business.initial", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ApiOperation(value = "保存期初产品", notes = "保存期初产品")
	public ResultInfo<ImportInventoryProduct> save(
			@Validated @RequestBody InventoryProductForm entity) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(inventoryService.save(entity, curUser));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.initial", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ApiOperation(value = "查询期初产品列表", notes = "查询期初产品列表")
	public TableDataInfo<ImportInventoryProduct> getAllInventoryProductList(
			@Validated @RequestBody InitialSearchProductForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(inventoryService.getAllInventoryProductList(form, curUser.getSystemType()));
	}
	
	@Log(title = "business.initial", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除期初产品", notes = "删除期初产品")
	public AjaxResult delProduct(
			@ApiParam(name="id",value="期初产品ID",required=true) @PathVariable Long id) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(inventoryService.deleteInventoryProduct(id, curUser.getSystemType()));
	}
	
	@Log(title = "business.initial", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/clean", method = RequestMethod.DELETE)
	@ApiOperation(value = "全部删除", notes = "全部删除")
	public AjaxResult clean() {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(inventoryService.clean(curUser.getOrgId(), curUser.getSystemType()));
	}
	
	@Log(title = "business.initial", businessType = BusinessType.IMPORT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/importData", method = RequestMethod.POST)
	@ApiOperation(value = "提交导入期初产品任务", notes = "导入产品(根据期初库存模板导入库存产品,导入失败的数据保存为新的Excel文件)：后续调importDataResult/taskId查结果")
	@ResponseBody
	public AjaxResult importData(MultipartFile file) {
		ExcelUtil<ImportInventoryProduct> util = new ExcelUtil<ImportInventoryProduct>(ImportInventoryProduct.class);
		List<ImportInventoryProduct> productList = null;
		try {
			productList = util.importExcel("导入数据", file.getInputStream());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return error(1, "错误的期初库存模板");
		}
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		String taskId = inventoryService.asyncImportData(productList, curUser);
		AjaxResult result = AjaxResult.success(taskId);
		result.put("taskId", taskId);
		return result;
	}
	
	@Log(title = "business.initial", businessType = BusinessType.IMPORT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/importDataResult/{taskId}", method = RequestMethod.GET)
	@ApiOperation(value = "导入期初产品结果查询", notes = "导入期初产品结果查询(根据模板导入产品,导入失败的数据保存为新的Excel文件)")
	@ResponseBody
	public AjaxResult importDataResult(
			@PathVariable("taskId")String taskId,
			HttpServletResponse response,
			HttpServletRequest request) throws Exception{
		Future<List<ImportInventoryProduct>> future = inventoryService.fetchImportTask(taskId);
		if (future == null) {
			return AjaxResult.error(1, "任务不存在!");
		}
		
		if(future.isDone()) {
			List<ImportInventoryProduct> errorProductList = future.get();
			if(StringUtils.isNotEmpty(errorProductList)) {
				ExcelUtil<ImportInventoryProduct> util = new ExcelUtil<ImportInventoryProduct>(ImportInventoryProduct.class);
				AjaxResult result = util.exportExcel(errorProductList, "错误数据");
				result.put("code", 3);
				return result;
				/*
				 * try { String fileName = String.valueOf(result.get("msg")); String
				 * realFileName = System.currentTimeMillis() +
				 * fileName.substring(fileName.indexOf("_") + 1); String filePath =
				 * YrtConfig.getDownloadPath() + fileName;
				 * 
				 * response.setCharacterEncoding("utf-8");
				 * response.setContentType("multipart/form-data");
				 * response.setHeader("Content-Disposition", "attachment;fileName=" +
				 * setFileDownloadHeader(request, realFileName)); FileUtils.writeBytes(filePath,
				 * response.getOutputStream()); FileUtils.deleteFile(filePath); } catch
				 * (Exception e) { log.error("下载文件失败", e); }
				 */
			}
			return success("数据导入成功!");
		}
		return AjaxResult.error(2, "还在在处理中...");
		
	}
	
	@Log(title = "business.initial", businessType = BusinessType.EXPORT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ApiOperation(value = "下载期初库存模板", notes = "下期初库存模板")
	public Resource export(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
		try {
			ClassPathResource cpr = new ClassPathResource("static/期初库存模板.xlsx");
            Resource resource = new UrlResource(cpr.getURI());
            if(resource.exists()) {
            	response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("期初库存模板.xlsx", "UTF-8"));
                return resource;
            } else {
                throw new FileException("没有找到文件'期初库存模板.xlsx'", null);
            }
        } catch (MalformedURLException ex) {
            throw new FileException("没有找到文件'期初库存模板.xlsx'", null);
        }
	}
	
	@Log(title = "business.initial", businessType = BusinessType.IMPORT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/create/{orgId}", method = RequestMethod.POST)
	@ApiOperation(value = "建账", notes = "建账：后续调createResult/taskId查结果")
	@ResponseBody
	public AjaxResult create(@PathVariable("orgId")Long orgId) {
		SystemType type = ServletUtils.getCurUserSystemType();
		String taskId = inventoryService.create(orgId, type);
		AjaxResult result = AjaxResult.success(taskId);
		result.put("taskId", taskId);
		return result;
	}
	
	@Log(title = "business.initial", businessType = BusinessType.IMPORT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/createResult/{taskId}", method = RequestMethod.GET)
	@ApiOperation(value = "建账结果查询", notes = "建账结果查询")
	@ResponseBody
	public AjaxResult createResult(@PathVariable("taskId")String taskId) throws Exception{
		Future<String> future = inventoryService.fetchCreateTask(taskId);
		if (future == null) {
			return AjaxResult.error(1, "任务不存在!");
		}
		
		if(future.isDone()) {
			String msg = future.get();
			return success(msg);
		}
		return AjaxResult.error(2, "还在在处理中...");
		
	}
}
