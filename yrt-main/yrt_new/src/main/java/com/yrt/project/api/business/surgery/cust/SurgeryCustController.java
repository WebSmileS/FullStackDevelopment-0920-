package com.yrt.project.api.business.surgery.cust;

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
import org.springframework.web.bind.annotation.RestController;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.yrt.common.dict.BaseIntEnum;
import com.yrt.common.dict.BaseIntEnum.EnumItemDesc;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.surgery.cust.service.SurgeryCustService;
import com.yrt.project.api.business.surgery.cust.vo.CustKitCondition;
import com.yrt.project.api.business.surgery.cust.vo.CustKitInfo;
import com.yrt.project.api.business.surgery.cust.vo.CustPackageCondition;
import com.yrt.project.api.business.surgery.cust.vo.CustPackageInfo;
import com.yrt.project.api.business.surgery.cust.vo.CustPackageInit;
import com.yrt.project.api.business.surgery.cust.vo.CustPackageRecord;
import com.yrt.project.api.business.surgery.cust.vo.SearchScope;
import com.yrt.project.modular.surgery.OisKitStatus;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustKit;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOis;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/surgery/cust")
@Api(tags = "??????-???????????????-cust")
public class SurgeryCustController extends BaseController {
	
	
	@Autowired
	private SurgeryCustService service;
	
	@GetMapping("/dict/packageStatus")
	@ApiOperation("01 ?????????/????????? ?????? enum")
	@ApiOperationSupport(order=1)
	public ResultList<EnumItemDesc> packageStatusDict() {
		return ResultList.success(BaseIntEnum.listElements(OisKitStatus.class));
	}
	
	@GetMapping("/dict/searchScope")
	@ApiOperation("02 ????????????enum")
	@ApiOperationSupport(order=2)
	public ResultList<EnumItemDesc> searchScopeDict() {
		return ResultList.success(BaseIntEnum.listElements(SearchScope.class));
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	// getPlatformPacakge + savePackage
	@Log(title = "platform.surgery.cust", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@PostMapping("/dealer/package/init")
	@ApiOperation("10 init?????????(?????????????????????src, ????????????????????????--????????????)")
	@ApiOperationSupport(order=10)
	public ResultInfo<CustPackageInfo> initPackage(
			@Validated @RequestBody CustPackageInit initPackage
			) {
		return ResultInfo.success(service.initPackageFromPlatform(initPackage));
	}
	
	@Log(title = "platform.surgery.cust", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@PutMapping("/dealer/package/save")
	@ApiOperation("11 ?????????????????????(owner_dealer_inner_sn/owner_dealer_name/platform_ois_inner_sn)")
	@ApiOperationSupport(order=11)
	public ResultInfo<CustPackageInfo> savePackage(@Validated @RequestBody CustPackageRecord record) {
		return ResultInfo.success(service.savePackage(record));
	}
	
	@Log(title = "platform.surgery.cust", businessType = BusinessType.DELETE, operatorType = OperatorType.PLATFORM)
	@DeleteMapping("/dealer/package/del/{id}")
	@ApiOperation("12 ?????????????????????")
	@ApiOperationSupport(order=12)
	public ResultInfo<Boolean> deletePackage(
			@ApiParam(value="?????????id", required=true)@PathVariable("id")Long id) {
		return ResultInfo.success(service.deletePackage(id));
	}
	
	@GetMapping("/dealer/package/get/{id}")
	@ApiOperation("13 ?????????????????????(?????????)")
	@ApiOperationSupport(order=13)
	public ResultInfo<CustPackageInfo> getPacakge(
			@ApiParam(value="???????????????id", required=true)@PathVariable("id")Long id
			) {
		return ResultInfo.success(service.getPackage(id));
	}
	
	// one platform-ois may has multi-custois(for same dealer)
//	@GetMapping("/dealer/package/get/{org_id}/{platform_ois_id}")
//	@ApiOperation("13.2 ?????????????????????id, ?????????????????????(?????????, ??????????????????????????????is_cust=true)")
//	@ApiOperationSupport(order=13)
//	public ResultInfo<CustPackageInfo> getPacakgeByPlatformId(
//			@ApiParam(value="?????????id", required=true)@PathVariable("org_id")Long org_id,
//			@ApiParam(value="???????????????id", required=true)@PathVariable("platform_ois_id")Long platform_ois_id
//			) {
//		return ResultInfo.success(service.getPackageByPlatformId(org_id, platform_ois_id));
//	}
	
	@GetMapping("/dealer/package/getPlatformPackage/{platform_ois_id}")
	@ApiOperation("13.2 ????????????platform?????????(?????????, ???????????????????????????is_cust=false)")
	@ApiOperationSupport(order=13)
	public ResultInfo<CustPackageInfo> getPlatformPacakge(
			@ApiParam(value="???????????????id", required=true)@PathVariable("platform_ois_id")Long platformId
			) {
		return ResultInfo.success(service.getPlatformPackage(platformId));
	}
	
	//@Log(title = "platform.surgery", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@SuppressWarnings("unchecked")
	@PostMapping("/dealer/package/list/{owner_dealer_inner_sn}")
	@ApiOperation("14 list?????????????????????(??????)")
	@ApiOperationSupport(order=14)
	public TableDataInfo<OrthopaedicCustOis> listPackage(
			@ApiParam(value="?????????id", required=true)@PathVariable("owner_dealer_inner_sn")Long owner_dealer_inner_sn,
			@RequestBody @Validated CustPackageCondition condition) {
		//startPage();
		return getDataTable(service.listPackageForDealer(owner_dealer_inner_sn, condition, TableSupport.buildValidPageRequest()));
	}
	
	//move to OperationPlanSupportController
//	@SuppressWarnings("unchecked")
//	@PostMapping({"/package/listForPlan/{hospital_inner_sn}", "/package/listForPlan/{hospital_inner_sn}/{dealer_inner_sn}"})
//	@ApiOperation("15.(hospital/[dealer]) list?????????????????????(??????)")
//	@ApiOperationSupport(order=15)
//	public TableDataInfo<OrthopaedicCustOis> listPackageForPlan(
//			@ApiParam(value="??????id", required=true)@PathVariable("hospital_inner_sn")Long hospital_inner_sn,
//			@ApiParam(value="?????????id", required=false)@PathVariable(name="dealer_inner_sn", required=false)Long dealer_inner_sn,
//			@RequestBody @Validated CustPackageCondition condition) {
//		//startPage();
//		return getDataTable(service.listPackageForPlan(hospital_inner_sn, dealer_inner_sn, condition, TableSupport.buildValidPageRequest()));
//	}

	@Log(title = "platform.surgery.cust", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@PostMapping("/dealer/package/addToPath/{op_inner_sn}")
	@ApiOperation("16 ?????????????????????????????????")
	@ApiOperationSupport(order=16)
	public ResultInfo<Integer> addPackageToPath(
			@ApiParam(value="??????id", required=true)@PathVariable("op_inner_sn")Long pathId,
			@RequestBody List<Long> custOisIds) {
		return ResultInfo.success(service.addPackageIntoPath(pathId, custOisIds));
	}
	
	@Log(title = "platform.surgery.cust", businessType = BusinessType.DELETE, operatorType = OperatorType.PLATFORM)
	@DeleteMapping("/dealer/package/removeFromPath/{op_inner_sn}")
	@ApiOperation("17 ???????????? ?????????????????????")
	@ApiOperationSupport(order=17)
	public ResultInfo<Integer> removePackageFromPath(
			@ApiParam(value="??????id", required=true)@PathVariable("op_inner_sn")Long pathId,
			@RequestBody List<Long> custOisIds) {
		return ResultInfo.success(service.removePackageFromPath(pathId, custOisIds));
	}
	
	//////////////////////////////////////////
	@Log(title = "platform.surgery.cust", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@PostMapping("/dealer/kit/init")
	@ApiOperation("20 init?????????(?????????????????????src, ??????????????????????????????)")
	@ApiOperationSupport(order=20)
	public ResultInfo<CustKitInfo> initKit(
			@Validated @RequestBody CustPackageInit initKit
			) {
		return ResultInfo.success(service.initKitFromPlatform(initKit));
	}
	
	@Log(title = "platform.surgery.cust", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@PutMapping("/dealer/kit/save")
	@ApiOperation("21 ?????????????????????(owner_dealer_inner_sn/owner_dealer_name/platform_kit_inner_sn)")
	@ApiOperationSupport(order=21)
	public ResultInfo<CustKitInfo> saveKit(
			@RequestBody @Validated CustKitInfo kit) {
		return ResultInfo.success(service.saveKit(kit));
	}
	
	@Log(title = "platform.surgery.cust", businessType = BusinessType.DELETE, operatorType = OperatorType.PLATFORM)
	@DeleteMapping("/dealer/kit/del/{id}")
	@ApiOperation("22 ?????????????????????")
	@ApiOperationSupport(order=22)
	public ResultInfo<Boolean> deleteKit(
			@ApiParam(value="?????????id", required=true)@PathVariable("id")Long id) {
		return ResultInfo.success(service.deleteKit(id));
	}
	
	
	@GetMapping("/dealer/kit/get/{id}")
	@ApiOperation("23 ?????????????????????(?????????)")
	@ApiOperationSupport(order=23)
	public ResultInfo<CustKitInfo> getKit(
			@ApiParam(value="?????????id", required=true)@PathVariable("id")Long id
			) {
		return ResultInfo.success(service.getKit(id));
	}
	
	@GetMapping("/dealer/kit/getPlatofrmKit/{platformKitId}")
	@ApiOperation("23.2 ????????????platform?????????(?????????, ???????????????????????????is_cust=false)")
	@ApiOperationSupport(order=23)
	public ResultInfo<CustKitInfo> getPlatformKit(
			@ApiParam(value="???????????????id", required=true)@PathVariable("platformKitId")Long platformKitId
			) {
		return ResultInfo.success(service.getPlatformKit(platformKitId));
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/dealer/kit/list/{owner_dealer_inner_sn}")
	@ApiOperation("24 list?????????????????????(??????)")
	@ApiOperationSupport(order=24)
	public TableDataInfo<OrthopaedicCustKit> listKit(
			@ApiParam(value="?????????id", required=true)@PathVariable("owner_dealer_inner_sn")Long owner_dealer_inner_sn,
			@RequestBody @Validated CustKitCondition condition) {
		startPage();
		return getDataTable(service.listKitForDealer(owner_dealer_inner_sn, condition));
	}
	
///////////////////////////////////////////////////
//	@DeleteMapping("/dealer/package/delReal/{id}")
//	@ApiOperation("50 ?????????????????????????????????")
//	@ApiOperationSupport(order=50)
//	public ResultInfo<Boolean> realDeletePackage(
//			@PathVariable("id")Long id) {
//		return ResultInfo.success(service.deletePackage(id));
//	}
//	
//	@DeleteMapping("/dealer/kit/delReal/{id}")
//	@ApiOperation("51 ?????????????????????????????????")
//	@ApiOperationSupport(order=51)
//	public ResultInfo<Boolean> realDeleteKit(
//			@PathVariable("id")Long id) {
//		return ResultInfo.success(service.deleteKit(id));
//	}
	
	
}
