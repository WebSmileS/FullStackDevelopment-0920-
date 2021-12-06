package com.yrt.project.api.business.surgery.plan;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.yrt.common.dict.BaseIntEnum;
import com.yrt.common.dict.BaseIntEnum.EnumItemDesc;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.surgery.plan.service.OperationPlanService;
import com.yrt.project.api.business.surgery.plan.vo.OperationPlanAll;
import com.yrt.project.api.business.surgery.plan.vo.PlanCondition;
import com.yrt.project.api.business.surgery.plan.vo.PlanOisPackageAll;
import com.yrt.project.modular.surgery.OisInstanceStatus;
import com.yrt.project.modular.surgery.OperationPlanStatus;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlan;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanOisInstance;
import com.yrt.project.modular.surgery.plan.PlanOisView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/surgery/plan")
@Api(tags = "业务-骨科手术plan")
public class OperationPlanController extends BaseController {

	@Autowired
	private OperationPlanService service;
	
	
	@GetMapping("/dict/planStatus")
	@ApiOperation("01计划状态enum")
	@ApiOperationSupport(order=1)
	public ResultList<EnumItemDesc> planStatusDict() {
		return ResultList.success(BaseIntEnum.listElements(OperationPlanStatus.class));
	}
	
	@GetMapping("/dict/packageStatus")
	@ApiOperation("02手术包实例 状态 enum")
	@ApiOperationSupport(order=2)
	public ResultList<EnumItemDesc> packageInstanceStatusDict() {
		return ResultList.success(BaseIntEnum.listElements(OisInstanceStatus.class));
	}
	
	/**
	 * @param id
	 * @return
	 */
	@GetMapping("/get/{id}")
	@ApiOperation("10 获取 手术plan(全量, 经销商只加载其供应的手术包(如果有packageStatus: 只获取特定状态的package))")
	@ApiOperationSupport(order = 10)
	public ResultInfo<OperationPlanAll> getPlan(@PathVariable("id") long id,
			@ApiParam("手术包状态") @RequestParam(name = "packageStatus", required = false) List<OisInstanceStatus> packageStatus) {
		return ResultInfo.success(service.getPlan(ServletUtils.getCurUserInfo(), id, packageStatus));
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/list")
	@ApiOperation("11 list手术plan列表(分页, 不含明细)")
	@ApiOperationSupport(order = 11)
	public TableDataInfo<OrthopaedicOperationPlan> listPlan(@RequestBody @Validated PlanCondition condition) {
		super.startPage();
		return getDataTable(service.listPlan(ServletUtils.getCurUserInfo(), condition));
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/listAll")
	@ApiOperation("12 list手术plan(分页, 全量)")
	@ApiOperationSupport(order = 12)
	public TableDataInfo<OperationPlanAll> listPlanAll(@RequestBody @Validated PlanCondition condition) {
		super.startPage();
		return getDataTable(service.listPlanAll(ServletUtils.getCurUserInfo(), condition));
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/listPackage")
	@ApiOperation("13 list手术plan手术包--展开(分页)")
	@ApiOperationSupport(order = 13)
	public TableDataInfo<PlanOisView> listPlanPackage(@RequestParam(name="aggMoney", required=false, defaultValue="false") boolean aggMoney,
			@RequestBody @Validated PlanCondition condition) {
		super.startPage();
		return getDataTable(service.listPlanPackage(ServletUtils.getCurUserInfo(), condition, aggMoney));
	}

	//

	@Log(title = "business.surgery.plan.savePlan", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@PostMapping("/save")
	@ApiOperation("21 保存 手术plan(不含明细)")
	@ApiOperationSupport(order = 21)
	public ResultInfo<OrthopaedicOperationPlan> savePlan(@RequestBody @Validated OrthopaedicOperationPlan plan) {
		return ResultInfo.success(service.savePlan(ServletUtils.getCurUserInfo(), plan));
	}

	@Log(title = "business.surgery.plan.savePlanAll", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@PostMapping("/saveAll")
	@ApiOperation("22 保存 手术plan(全量, doPublish=true时直接发布)")
	@ApiOperationSupport(order = 22)
	public ResultInfo<OperationPlanAll> savePlanAll(
			@RequestParam(name="doPublish", defaultValue="false", required=false)boolean doPublish, 
			@RequestBody @Validated OperationPlanAll plan) {
		return ResultInfo.success(service.savePlanAll(ServletUtils.getCurUserInfo(), plan, doPublish));
	}

	//
	@Log(title = "business.surgery.plan.confirmPlan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@PutMapping("/confirm/{id}")
	@ApiOperation("23 确认手术plan")
	@ApiOperationSupport(order = 23)
	public ResultInfo<OrthopaedicOperationPlan> confirmPlan(@PathVariable("id") long id) {
		return ResultInfo.success(service.confirmPlan(ServletUtils.getCurUserInfo(), id));
	}
	
	@Log(title = "business.surgery.plan.deletePlan", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@DeleteMapping("/del/{id}")
	@ApiOperation("24 删除手术plan")
	@ApiOperationSupport(order = 24)
	public ResultInfo<Boolean> deletePlan(@PathVariable("id") long id) {
		return ResultInfo.success(service.deletePlan(ServletUtils.getCurUserInfo(), id));
	}

	@Log(title = "business.surgery.plan.publishPlan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@PutMapping("/publish/{id}")
	@ApiOperation("25 发布手术plan")
	@ApiOperationSupport(order = 25)
	public ResultInfo<OrthopaedicOperationPlan> publishPlan(@PathVariable("id") long id) {
		return ResultInfo.success(service.publishPlan(ServletUtils.getCurUserInfo(), id));
	}

	@Log(title = "business.surgery.plan.closePlan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@PutMapping("/finish/{id}")
	@ApiOperation("26 完成(关闭)手术plan")
	@ApiOperationSupport(order = 26)
	public ResultInfo<OrthopaedicOperationPlan> finishPlan(@PathVariable("id") long id) {
		return ResultInfo.success(service.closePlan(ServletUtils.getCurUserInfo(), id));
	}

	// 计划内 手术包

	@GetMapping("/package/getAll/{id}")
	@ApiOperation("30 获取plan内一个手术包(含明细)")
	@ApiOperationSupport(order = 30)
	public ResultInfo<PlanOisPackageAll> getPlanPackage(@PathVariable("id")Long id) {
		return ResultInfo.success(service.getPackageAll(ServletUtils.getCurUserInfo(), id));
	}
	
	@Log(title = "business.surgery.plan.savePackage", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@PostMapping("/package/save") // INIT
	@ApiOperation("31 保存1个手术包(单个手术包全量)")
	@ApiOperationSupport(order = 31)
	public ResultInfo<PlanOisPackageAll> savePackage(@RequestBody @Validated PlanOisPackageAll record) {
		return ResultInfo.success(service.savePackageAll(ServletUtils.getCurUserInfo(), record));
	}

	@Log(title = "business.surgery.plan.deletePackage", businessType = BusinessType.DELETE, operatorType = OperatorType.BUSINESS)
	@DeleteMapping("/package/del/{packageId}")
	@ApiOperation("32 删除1个手术包(单个手术包)")
	@ApiOperationSupport(order = 32)
	public ResultInfo<Boolean> deletePackage(@PathVariable("packageId") long packageId) {
		return ResultInfo.success(service.deletePackage(ServletUtils.getCurUserInfo(), packageId));
	}

	@Log(title = "business.surgery.plan.lockPackage", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@PutMapping("/package/lock")
	@ApiOperation("41 备货1个手术包(助理out_warehouse_inner_sn/out_warehouse_name)")
	@ApiOperationSupport(order = 41)
	public ResultInfo<OrthopaedicOperationPlanOisInstance> lockPackage(@RequestBody OrthopaedicOperationPlanOisInstance record) {
		return ResultInfo.success(service.lockPackage(ServletUtils.getCurUserInfo(), record));
	}

	@Log(title = "business.surgery.plan.outPackage", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@PutMapping("/package/out/{packageId}")
	@ApiOperation("42 出库1个手术包(仓库)")
	@ApiOperationSupport(order = 42)
	public ResultInfo<OrthopaedicOperationPlanOisInstance> outPackage(@PathVariable("packageId") long packageId) {
		return ResultInfo.success(service.outPackage(ServletUtils.getCurUserInfo(), packageId));
	}

	@Log(title = "business.surgery.plan.backPackage", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@PutMapping("/package/back")
	@ApiOperation("43 回库1个手术包(仓库back_quantity/batch_number)")
	@ApiOperationSupport(order = 43)
	public ResultInfo<OrthopaedicOperationPlanOisInstance> backPackage(@RequestBody PlanOisPackageAll record) {
		return ResultInfo.success(service.backPackage(ServletUtils.getCurUserInfo(), record));
	}

	@Log(title = "business.surgery.plan.pendingSalePackage", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@PutMapping("/package/pendingSale")
	@ApiOperation("44  确认 数量(助理填量unit_price/sale_quantity/loss_quantity/loss_reason)")
	@ApiOperationSupport(order = 44)
	public ResultInfo<OrthopaedicOperationPlanOisInstance> pendingSalePackage(
			@RequestBody PlanOisPackageAll record) {
		return ResultInfo.success(service.pendingSalePackage(ServletUtils.getCurUserInfo(), record));
	}
	
	// hospital
	@Log(title = "business.surgery.plan.confirmSalePackage", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@PutMapping("/package/confirmSale")
	@ApiOperation("45 确认计划内n个手术包(医院 确认销售)")
	@ApiOperationSupport(order = 45)
	public ResultList<OrthopaedicOperationPlanOisInstance> confirmSalePackage(
			@RequestParam("packageIds") List<Long> packageIds) {
		return ResultList.success(service.confirmSalePackage(ServletUtils.getCurUserInfo(), packageIds));
	}
	// reject and return to BACK status
	
	
	@Log(title = "business.surgery.plan.finishPackage", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@PutMapping("/package/finish/{packageId}")
	@ApiOperation("46 完成 计划内1个手术包(助理出单)")
	@ApiOperationSupport(order = 46)
	public ResultInfo<OrthopaedicOperationPlanOisInstance> finishPackage(
			@PathVariable("packageId") long packageId) {
		return ResultInfo.success(service.finishPackage(ServletUtils.getCurUserInfo(), packageId));
	}

}
