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
@Api(tags = "??????-??????????????????")
public class InitialInventoryController extends BaseController {
	
	private static final Logger log = LoggerFactory.getLogger(InitialInventoryController.class);
	
	@Autowired private InitialInventoryService inventoryService;
	
	@Log(title = "business.initial", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public ResultInfo<ImportInventoryProduct> save(
			@Validated @RequestBody InventoryProductForm entity) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(inventoryService.save(entity, curUser));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.initial", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ApiOperation(value = "????????????????????????", notes = "????????????????????????")
	public TableDataInfo<ImportInventoryProduct> getAllInventoryProductList(
			@Validated @RequestBody InitialSearchProductForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(inventoryService.getAllInventoryProductList(form, curUser.getSystemType()));
	}
	
	@Log(title = "business.initial", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public AjaxResult delProduct(
			@ApiParam(name="id",value="????????????ID",required=true) @PathVariable Long id) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(inventoryService.deleteInventoryProduct(id, curUser.getSystemType()));
	}
	
	@Log(title = "business.initial", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/clean", method = RequestMethod.DELETE)
	@ApiOperation(value = "????????????", notes = "????????????")
	public AjaxResult clean() {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(inventoryService.clean(curUser.getOrgId(), curUser.getSystemType()));
	}
	
	@Log(title = "business.initial", businessType = BusinessType.IMPORT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/importData", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????????????????", notes = "????????????(??????????????????????????????????????????,????????????????????????????????????Excel??????)????????????importDataResult/taskId?????????")
	@ResponseBody
	public AjaxResult importData(MultipartFile file) {
		ExcelUtil<ImportInventoryProduct> util = new ExcelUtil<ImportInventoryProduct>(ImportInventoryProduct.class);
		List<ImportInventoryProduct> productList = null;
		try {
			productList = util.importExcel("????????????", file.getInputStream());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return error(1, "???????????????????????????");
		}
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		String taskId = inventoryService.asyncImportData(productList, curUser);
		AjaxResult result = AjaxResult.success(taskId);
		result.put("taskId", taskId);
		return result;
	}
	
	@Log(title = "business.initial", businessType = BusinessType.IMPORT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/importDataResult/{taskId}", method = RequestMethod.GET)
	@ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????(????????????????????????,????????????????????????????????????Excel??????)")
	@ResponseBody
	public AjaxResult importDataResult(
			@PathVariable("taskId")String taskId,
			HttpServletResponse response,
			HttpServletRequest request) throws Exception{
		Future<List<ImportInventoryProduct>> future = inventoryService.fetchImportTask(taskId);
		if (future == null) {
			return AjaxResult.error(1, "???????????????!");
		}
		
		if(future.isDone()) {
			List<ImportInventoryProduct> errorProductList = future.get();
			if(StringUtils.isNotEmpty(errorProductList)) {
				ExcelUtil<ImportInventoryProduct> util = new ExcelUtil<ImportInventoryProduct>(ImportInventoryProduct.class);
				AjaxResult result = util.exportExcel(errorProductList, "????????????");
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
				 * (Exception e) { log.error("??????????????????", e); }
				 */
			}
			return success("??????????????????!");
		}
		return AjaxResult.error(2, "??????????????????...");
		
	}
	
	@Log(title = "business.initial", businessType = BusinessType.EXPORT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ApiOperation(value = "????????????????????????", notes = "?????????????????????")
	public Resource export(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
		try {
			ClassPathResource cpr = new ClassPathResource("static/??????????????????.xlsx");
            Resource resource = new UrlResource(cpr.getURI());
            if(resource.exists()) {
            	response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("??????????????????.xlsx", "UTF-8"));
                return resource;
            } else {
                throw new FileException("??????????????????'??????????????????.xlsx'", null);
            }
        } catch (MalformedURLException ex) {
            throw new FileException("??????????????????'??????????????????.xlsx'", null);
        }
	}
	
	@Log(title = "business.initial", businessType = BusinessType.IMPORT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/create/{orgId}", method = RequestMethod.POST)
	@ApiOperation(value = "??????", notes = "??????????????????createResult/taskId?????????")
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
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	@ResponseBody
	public AjaxResult createResult(@PathVariable("taskId")String taskId) throws Exception{
		Future<String> future = inventoryService.fetchCreateTask(taskId);
		if (future == null) {
			return AjaxResult.error(1, "???????????????!");
		}
		
		if(future.isDone()) {
			String msg = future.get();
			return success(msg);
		}
		return AjaxResult.error(2, "??????????????????...");
		
	}
}
