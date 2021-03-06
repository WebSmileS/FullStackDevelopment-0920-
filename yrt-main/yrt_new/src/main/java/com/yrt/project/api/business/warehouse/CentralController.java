package com.yrt.project.api.business.warehouse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.warehouse.service.CentralService;
import com.yrt.project.api.business.warehouse.vo.central.AddNoticeForm;
import com.yrt.project.api.business.warehouse.vo.central.LogisticsForm;
import com.yrt.project.api.business.warehouse.vo.central.SearchLogisticsForm;
import com.yrt.project.api.business.warehouse.vo.central.SearchNoticeForm;
import com.yrt.project.api.business.warehouse.vo.central.SearchNoticeProductForm;
import com.yrt.project.api.business.warehouse.vo.central.SearchWarehouseProductForm;
import com.yrt.project.api.business.warehouse.vo.central.SelectNoticeForm;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.warehouse.domain.GrnNotice;
import com.yrt.project.modular.warehouse.domain.InventoryProduct;
import com.yrt.project.modular.warehouse.domain.Lease;
import com.yrt.project.modular.warehouse.domain.OdoLogisticsRecord;
import com.yrt.project.modular.warehouse.domain.OdoNotice;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/central")
@Api(tags = "??????-??????????????????")
public class CentralController extends BaseController {

	@Autowired CentralService service;
	
	//??????????????????
	@Log(title = "business.central", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/lease/add/{dealerId}", method = RequestMethod.POST)
	@ApiOperation(value = "????????????", notes = "????????????")
	public ResultInfo<Lease> insertLease(
			@ApiParam(name="dealerId",value="??????ID",required=true) @PathVariable Long dealerId) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.addLease(curUser.getOrgId(), dealerId));
	}
	
	@Log(title = "business.central", businessType = BusinessType.DELETE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/lease/del", method = RequestMethod.POST)
	@ApiOperation(value = "????????????", notes = "????????????")
	public AjaxResult delLease(
			@ApiParam(name="ids",value="??????ID??????",required=true) @RequestBody List<Long> ids) {
		service.deleteLease(ids);
		return success();
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.central", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/lease/list", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????(??????)", notes = "??????????????????(??????)")
	public TableDataInfo<Lease> getLeaseList(
			@ApiParam(name="leaseName",value="????????????") String leaseName) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.getLeaseList(curUser.getOrgId(), leaseName));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.central", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/inventory/list", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public TableDataInfo<InventoryProduct> getTopLevelWarehouseList(
			@Validated @RequestBody SearchWarehouseProductForm form) {
		startPage();
		return getDataTable(service.selectInventoryByLevel(2, form));
	}
	
	@Log(title = "business.central", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/warehouse/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "????????????ID????????????????????????", notes = "????????????ID????????????????????????")
	public ResultList<WarehouseModel> getUsableTopLevelWarehouseList(
			@ApiParam(name="oid",value="??????ID",required=true) @PathVariable Long oid) {
		return ResultList.success(service.selectTopLevelWarehouseList(oid, 1L));
	}
	
	//??????????????????
	@SuppressWarnings("unchecked")
	@Log(title = "business.central", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/notice/grn/list", method = RequestMethod.POST)
	@ApiOperation(value = "????????????????????????(??????)", notes = "????????????????????????(??????)")
	public TableDataInfo<GrnNotice> getGrnNoticeList(
			@RequestBody SearchNoticeForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.searchGrnNoticeList(curUser.getOrgId(), form, form.getIsHistory()));
	}
	
	@Log(title = "business.central", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/notice/grn/add", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public ResultList<GrnNotice> addGrnNotice(
			@RequestBody @Validated AddNoticeForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultList.success(service.insertGrnNotice(form, curUser));
	}
	
	@Log(title = "business.central", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/notice/grn/edit", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public AjaxResult updateGrnNotice(
			@Validated @RequestBody List<SelectNoticeForm> list) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		for (SelectNoticeForm form : list) {
			service.updateGrnNotice(form.getId(), form.getStatus(), curUser);
		}
		return success();
	}
	
	//??????????????????
	@SuppressWarnings("unchecked")
	@Log(title = "business.central", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/notice/odo/list", method = RequestMethod.POST)
	@ApiOperation(value = "????????????????????????(??????)", notes = "????????????????????????(??????)")
	public TableDataInfo<OdoNotice> getOdoNoticeList(
			@RequestBody SearchNoticeForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.searchOdoNoticeList(curUser.getOrgId(), form, form.getIsHistory()));
	}
	
	@Log(title = "business.central", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/notice/odo/add", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public ResultList<OdoNotice> addOdoNotice(
			@RequestBody @Validated AddNoticeForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultList.success(service.insertOdoNotice(form, curUser));
	}
	
	@Log(title = "business.central", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/notice/odo/edit", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public AjaxResult updateOdoNotice(
			@Validated @RequestBody List<SelectNoticeForm> list) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		for (SelectNoticeForm form : list) {
			service.updateOdoNotice(form.getId(), form.getStatus(), curUser);
		}
		return success();
	}
	
	//??????????????????
	@SuppressWarnings("unchecked")
	@Log(title = "business.central", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/logistics/list", method = RequestMethod.POST)
	@ApiOperation(value = "????????????????????????(??????)", notes = "????????????????????????(??????)")
	public TableDataInfo<OdoLogisticsRecord> getLogisticsRecordList(
			@RequestBody SearchLogisticsForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.getLogisticsRecordList(curUser.getOrgId(), form, form.getIsHistory()));
	}
	
	@Log(title = "business.central", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/logistics/add", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public ResultInfo<OdoLogisticsRecord> addLogisticsRecord(
			@RequestBody @Validated LogisticsForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.insertOdoLogisticsRecord(form, curUser));
	}
	
	@Log(title = "business.central", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/logistics/edit", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public AjaxResult updateLogisticsRecord(
			@RequestBody @Validated LogisticsForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		service.updateOdoLogisticsRecord(form, curUser);
		return success();
	}
	
	@Log(title = "business.central", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/logistics/confirm/{id}", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public AjaxResult confirmLogisticsRecord(
			@ApiParam(name="id",value="????????????",required=true) @PathVariable Long id) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		service.confirmLogistics(id, curUser);
		return success();
	}
	
	@Log(title = "business.central", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/logistics/info/{id}", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public ResultInfo<OdoLogisticsRecord> getLogisticsRecordInfo(
			@ApiParam(name="id",value="????????????",required=true) @PathVariable Long id) {
		return ResultInfo.success(service.getLogisticsRecordInfo(id));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.central", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/notice/grn/vendor/list", method = RequestMethod.POST)
	@ApiOperation(value = "???????????????????????????????????????(??????)", notes = "???????????????????????????????????????(??????)")
	public TableDataInfo<ResponseProductInfo> getGrnNoticeVendorProductList(
			@Validated @RequestBody SearchNoticeProductForm form) {
		startPage();
		return getDataTable(service.getGrnNoticeVendorProductList(form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.central", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/notice/odo/vendor/list", method = RequestMethod.POST)
	@ApiOperation(value = "???????????????????????????????????????(??????)", notes = "???????????????????????????????????????(??????)")
	public TableDataInfo<ResponseProductInfo> getOdoNoticeVendorProductList(
			@Validated @RequestBody SearchNoticeProductForm form) {
		startPage();
		return getDataTable(service.getOdoNoticeVendorProductList(form));
	}
}
