package com.yrt.project.api.business.order;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicResponseParameters;
import com.yrt.common.dict.SystemType;
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
import com.yrt.project.api.business.order.service.IOrderService;
import com.yrt.project.api.business.order.service.ISaleRecordService;
import com.yrt.project.api.business.order.vo.order.AddPurchaseOrderForm;
import com.yrt.project.api.business.order.vo.order.AddSaleOrderForm;
import com.yrt.project.api.business.order.vo.order.AppOrderSearchForm;
import com.yrt.project.api.business.order.vo.order.BuildSaleOrderForm;
import com.yrt.project.api.business.order.vo.order.ConfirmIDForm;
import com.yrt.project.api.business.order.vo.order.DetailCIDForm;
import com.yrt.project.api.business.order.vo.order.EditPurchaseOrderForm;
import com.yrt.project.api.business.order.vo.order.EditSaleOrderForm;
import com.yrt.project.api.business.order.vo.order.OrderIDForm;
import com.yrt.project.api.business.order.vo.order.OrderSendForm;
import com.yrt.project.api.business.order.vo.order.PurchaseHistorySearchForm;
import com.yrt.project.api.business.order.vo.order.PurchaseSearchForm;
import com.yrt.project.api.business.order.vo.order.SaleDetailSearchForm;
import com.yrt.project.api.business.order.vo.order.SaleHistorySearchForm;
import com.yrt.project.api.business.order.vo.order.SaleSearchForm;
import com.yrt.project.api.business.order.vo.order.SearchOrderPriceForm;
import com.yrt.project.api.business.order.vo.order.SearchPurchaseRecordForm;
import com.yrt.project.api.common.service.CommService;
import com.yrt.project.api.common.vo.CommSearchProductForm;
import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.order.domain.PurchasingRecord;
import com.yrt.project.modular.order.domain.SaleOrder;
import com.yrt.project.modular.organization.domain.OrgEmployee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/order")
@Api(tags = "业务-订单管理接口")
public class OrderController extends BaseController {
	
	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired private IOrderService service;
	@Autowired private ISaleRecordService saleRecordservice;
	@Autowired private CommService commService;
//	@Autowired private SysParameterService paramService;
	
	//手机接口
	@SuppressWarnings("unchecked")
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/app/purchase/list", method = RequestMethod.GET)
	@ApiOperation(value = "查看采购订单列表(分页，手机端专用!)", notes = "查看采购订单列表(分页，手机端专用!)")
	public TableDataInfo<PurchaseOrder> selectPurchaseOrderList(
			@Validated AppOrderSearchForm form) {
		OrgEmployee emp = getCurEmployee();
		startPage();
		List<PurchaseOrder> list = service.selectPurchaseOrderList4Phone(form.getOid(), form.getKey(), form.getStatus(), emp.getEmployee_inner_sn());
		return getDataTable(list);
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/app/sale/list", method = RequestMethod.GET)
	@ApiOperation(value = "查看销售订单列表(分页，手机端专用!)", notes = "查看销售订单列表(分页，手机端专用!)")
	public TableDataInfo<SaleOrder> selectSaleOrderList(
			@Validated AppOrderSearchForm form) {
		OrgEmployee emp = getCurEmployee();
		startPage();
		List<SaleOrder> list = service.selectSaleOrderList4Phone(form.getOid(), form.getKey(), form.getStatus(), emp.getEmployee_inner_sn());
		return getDataTable(list);
	}
	
	// 通用编辑
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除订单", notes = "删除订单")
	public AjaxResult deleteOrder(
			@RequestBody @Validated List<OrderIDForm> ids) {
		return toAjax(service.deleteOrder(ids));
	}
	
	/*
	 * @Log(title = "business.order", businessType = BusinessType.OTHER,
	 * operatorType = OperatorType.BUSINESS)
	 * 
	 * @RequestMapping(value = "/detail/del", method = RequestMethod.DELETE)
	 * 
	 * @ApiOperation(value = "删除订单明细", notes = "删除订单明细") public AjaxResult
	 * deleteOrderDetail(@Validated DetailCIDForm form) { return
	 * toAjax(service.deleteDetailOrder(form)); }
	 */
	
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail/stop", method = RequestMethod.PUT)
	@ApiOperation(value = "终止订单明细", notes = "终止订单明细")
	public AjaxResult stopOrderDetail(@Validated DetailCIDForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.stopOrderDetail(form, curUser));
	}
	
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail/unstop", method = RequestMethod.PUT)
	@ApiOperation(value = "反终止订单明细", notes = "反终止订单明细")
	public AjaxResult unStopOrderDetail(@Validated DetailCIDForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.unStopOrderDetail(form, curUser));
	}
	
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/stop", method = RequestMethod.PUT)
	@ApiOperation(value = "终止订单", notes = "终止订单")
	public AjaxResult stopPurchaseOrder(@RequestBody @Valid List<OrderIDForm> list) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.stopOrder(list, curUser));
	}
	
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/confirm", method = RequestMethod.PUT)
	@ApiOperation(value = "外部批量确认订单", notes = "外部批量确认订单")
	public AjaxResult confirmOrder(@RequestBody @Valid List<OrderIDForm> list) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.confirmOrder(list, curUser));
	}
	
	// 采购订单编辑
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/purchase/add", method = RequestMethod.POST)
	@ApiOperation(value = "添加采购订单", notes = "添加采购订单")
	public ResultInfo<PurchaseOrder> addPurchaseOrder(@RequestBody @Valid AddPurchaseOrderForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.addPurchaseOrder(form, curUser));
	}
	
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/purchase/confirm", method = RequestMethod.PUT)
	@ApiOperation(value = "确认采购订单", notes = "确认采购订单")
	public AjaxResult confirmPurchaseOrder(@Validated ConfirmIDForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.confirmPurchaseOrder(form, curUser));
	}
	
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/purchase/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改采购订单信息", notes = "修改采购订单信息")
	public ResultInfo<PurchaseOrder> editPurchaseOrder(@RequestBody @Valid EditPurchaseOrderForm form) {
		ContextUserInfo info = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.editPurchaseOrder(form, info));
	}
	
	// 销售订单编辑
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sale/add", method = RequestMethod.POST)
	@ApiOperation(value = "添加销售订单", notes = "添加销售订单")
	public ResultInfo<SaleOrder> addSaleOrder(@RequestBody @Valid AddSaleOrderForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.addSaleOrder(form, curUser));
	}
	
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sale/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改销售订单信息", notes = "修改销售订单信息")
	public ResultInfo<SaleOrder> editSaleOrder(@RequestBody @Valid EditSaleOrderForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.editSaleOrder(form, curUser));
	}
	
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sale/confirm", method = RequestMethod.PUT)
	@ApiOperation(value = "确认销售订单", notes = "确认销售订单")
	public AjaxResult confirmSaleOrder(@Validated ConfirmIDForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.confirmSaleOrder(form, curUser));
	}
	
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sale/build", method = RequestMethod.PUT)
	@ApiOperation(value = "保存销售单", notes = "保存销售单")
	public AjaxResult buildSaleOrder(@RequestBody @Valid BuildSaleOrderForm form) {
		return toAjax(saleRecordservice.buildSaleOrder(form,ServletUtils.getCurUserSystemType())!=null?1:0);
	}
	
	@Log(title = "business.plan", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sale/creatplan", method = RequestMethod.POST)
	@ApiOperation(value = "生成采购计划", notes = "生成采购计划")
	public AjaxResult detailBuildSaleOrder(
			@RequestBody @Valid OrderSendForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case DEALER:
			OrgEmployee emp = getCurEmployee();
			return toAjax(service.createPlan(form, emp));
		default:
		}
		return error("当前机构不支持这个功能!");
	}
	 
	
	// 查询
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sale/info", method = RequestMethod.GET)
	@ApiOperation(value = "查看销售订单信息", notes = "查看销售订单信息")
	public ResultInfo<SaleOrder> selectSaleOrderInfo(@Validated OrderIDForm form) {
		return ResultInfo.success(service.selectSaleOrderInfo(form));
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sale/list", method = RequestMethod.GET)
	@ApiOperation(value = "查看销售订单列表(分页)", notes = "查看销售订单列表(分页)")
	public TableDataInfo<SaleOrder> selectSaleOrderList(@Validated SaleSearchForm form) {
		OrgEmployee emp = getCurEmployee();
		startPage();
		List<SaleOrder> list = service.selectSaleOrderList(form, emp.getEmployee_inner_sn());
		return getDataTable(list);
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/history/sale/list", method = RequestMethod.GET)
	@ApiOperation(value = "查看历史销售订单列表(分页)", notes = "查看历史销售订单列表(分页)")
	public TableDataInfo<SaleOrder> selectHistorySaleOrderList(@Validated SaleHistorySearchForm form) {
		startPage();
		List<SaleOrder> list = service.selectHistorySaleOrderList(form);
		return getDataTable(list);
	}
	@ApiOperationSupport(
            responses = @DynamicResponseParameters(properties = {
                    @DynamicParameter(value = "自定义状态码 0-成功 1-失败 和其他http状态码",name = "code"),
                    @DynamicParameter(value = "提示信息",name = "msg"),
                    @DynamicParameter(value = "销售订单明细列表",name = "list", dataTypeClass = List.class, 
                    	example="[\r\n" + 
                    		"    {\r\n" + 
                    		"        \"min_unit_inventory_7\":\"仓库内部编码为7的最小包装单位库存量\",\r\n" + 
                    		"        \"min_unit_inner_sn\":\"最小包装单位内部编码\",\r\n" + 
                    		"        \"quantity\":\"销售数量\",\r\n" + 
                    		"        \"batch_number\":\"批号\",\r\n" + 
                    		"        \"min_unit\":\"最小包装单位名称\",\r\n" + 
                    		"        \"detail_inner_sn\":\"明细内部编码\",\r\n" + 
                    		"        \"discount\":\"折扣率\",\r\n" + 
                    		"        \"specification\":\"规格型号名称\",\r\n" + 
                    		"        \"product_vendor_inner_sn\":\"厂商内部编码\",\r\n" + 
                    		"        \"unit_inner_sn\":\"包装单位内部编码\",\r\n" + 
                    		"        \"unit_price\":\"单价\",\r\n" + 
                    		"        \"min_unit_lock_inventory\":\"锁定最小包装单位库存量\",\r\n" + 
                    		"        \"product_name\":\"产品名称\",\r\n" + 
                    		"        \"sold_quantity\":\"已销售数量\",\r\n" + 
                    		"        \"min_unit_quantity\":\"销售包装单位与最小包装单位的倍数\",\r\n" + 
                    		"        \"specification_inner_sn\":\"规格型号内部编码\",\r\n" + 
                    		"        \"unit\":\"销售包装单位名称\",\r\n" + 
                    		"        \"product_type\":\"产品类型 0:经销商(自建)产品 1:厂商产品\",\r\n" + 
                    		"        \"sort_number\":\"排序号\",\r\n" + 
                    		"        \"product_vendor_name\":\"厂商名称\",\r\n" + 
                    		"        \"rate\":\"税率\",\r\n" + 
                    		"        \"product_inner_sn\":\"产品内部编码\",\r\n" + 
                    		"        \"status\":\"状态\"\r\n" + 
                    		"    }\r\n" + 
                    		"]")
            })
    )
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sale/detail", method = RequestMethod.POST)
	@ApiOperation(value = "查看销售订单明细信息", notes = "查看销售订单明细信息")
	public AjaxResult selectSaleOrderDetailList(
			@RequestBody @Validated SaleDetailSearchForm form) {
		clearPage();
		return success().put("list", service.selectSaleOrderDetailList(form));
	}
	
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/purchase/info", method = RequestMethod.GET)
	@ApiOperation(value = "查看采购订单信息", notes = "查看采购订单信息")
	public ResultInfo<PurchaseOrder> selectPurchaseOrderInfo(@Validated OrderIDForm form) {
		OrgEmployee emp = getCurEmployee();
		return ResultInfo.success(service.selectPurchaseOrderInfo(form, emp, true));
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/purchase/list", method = RequestMethod.GET)
	@ApiOperation(value = "查看采购订单列表(分页)", notes = "查看采购订单列表(分页)")
	public TableDataInfo<PurchaseOrder> selectPurchaseOrderList(@Validated PurchaseSearchForm form) {
		OrgEmployee emp = getCurEmployee();
		startPage();
		List<PurchaseOrder> list = service.selectPurchaseOrderList(form, emp.getEmployee_inner_sn());
		return getDataTable(list);
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/history/purchase/list", method = RequestMethod.GET)
	@ApiOperation(value = "查看历史采购订单列表(分页)", notes = "查看历史采购订单列表(分页)")
	public TableDataInfo<PurchaseOrder> selectHistoryPurchaseOrderList(@Validated PurchaseHistorySearchForm form) {
		startPage();
		List<PurchaseOrder> list = service.selectHistoryPurchaseOrderList(form);
		return getDataTable(list);
	}
	
	//产品选择列表
	@SuppressWarnings("unchecked")
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/vendor/list", method = RequestMethod.GET)
	@ApiOperation(value = "厂商产品列表(分页)", notes = "厂商产品列表(分页)")
	public TableDataInfo<Map<String, Object>> getVendorProductList(@Validated CommSearchProductForm form) {
		startPage();
		return getDataTable(commService.selectVendorProductList(form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/dealer/list", method = RequestMethod.GET)
	@ApiOperation(value = "经销商自建产品列表(分页)", notes = "经销商自建产品列表(分页)")
	public TableDataInfo<Map<String, Object>> getDealerProductList(@Validated CommSearchProductForm form) {
		startPage();
		return getDataTable(commService.selectDealerProductList(form, true));
	}
	
	//获取合同相关的机构列表(只包含机构ID，机构名称和机构类型字段)
	@SuppressWarnings("unchecked")
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/org/list/{systemType}/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "相关机构列表", notes = "相关机构列表")
	public TableDataInfo<Map<String, Object>> getDealerProductList(
			@ApiParam(name = "systemType", value = "相关机构类型 1-医院 、 2-经销商、 3-厂商", required = true) @PathVariable int systemType,
			@ApiParam(name = "oid", value = "当前机构ID", required = true) @PathVariable Long oid,
			@ApiParam(name = "name", value = "搜索机构名称", required = false) String name,
			@ApiParam(name = "all", value = "合同机构,默认true", required = true) @RequestParam(defaultValue="true") Boolean all,
			@ApiParam(name = "isPurchase", value = "是否采购,默认true", required = true) @RequestParam(defaultValue="true") Boolean isPurchase) {
		startPage();
		return getDataTable(commService.selectRelatedOrg4DropDownList(convertSystemType(systemType), oid, name, all, isPurchase, true));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/purchase/record/list", method = RequestMethod.POST)
	@ApiOperation(value = "查询采购记录列表(分页)", notes = "查询采购记录列表(分页)")
	public TableDataInfo<PurchasingRecord> selectPurchaseRecordList(
			@Validated @RequestBody SearchPurchaseRecordForm form) {
		SystemType type = ServletUtils.getCurUserSystemType();
//		boolean isSingle = paramService.isSingle(type, form.getPartA());
		startPage();
		List<PurchasingRecord> list = service.selectPurchaseRecordList(form, type);
		return getDataTable(list);
	}
	
	@Log(title = "business.order", businessType = BusinessType.EXPORT)
	@PostMapping("/purchase/record/export")
    @ApiOperation(value = "导出采购记录", notes = "导出采购记录")
    public void PurchaseOrderRecordExport(
    		@Validated @RequestBody SearchPurchaseRecordForm form,
    		HttpServletResponse response,
			HttpServletRequest request) {
		SystemType type = ServletUtils.getCurUserSystemType();
//		boolean isSingle = paramService.isSingle(type, form.getPartA());
		List<PurchasingRecord> list = service.selectPurchaseRecordList(form, type);
		ExcelUtil<PurchasingRecord> util = new ExcelUtil<PurchasingRecord>(PurchasingRecord.class);
		AjaxResult result = util.exportExcel(list, "采购记录");
		try {
			String fileName = String.valueOf(result.get("msg"));
			String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
			String filePath = YrtConfig.getDownloadPath() + fileName;

			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			;
			response.setHeader("Content-Disposition",
					"attachment;fileName=" + setFileDownloadHeader(request, realFileName));
			FileUtils.writeBytes(filePath, response.getOutputStream());
			FileUtils.deleteFile(filePath);
		} catch (Exception e) {
			log.error("下载文件失败", e);
		}
	}
	
	@Log(title = "business.order", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/getOrderPrice", method = RequestMethod.POST)
	@ApiOperation(value = "查询产品采购价格", notes = "查询产品采购价格")
	public AjaxResult getOrderPrice(
			@RequestBody @Valid SearchOrderPriceForm form) {
		Long orgId = ServletUtils.getCurUserOrgID();
		return success().put("list", service.getOrderPrice(form, orgId));
	}
}