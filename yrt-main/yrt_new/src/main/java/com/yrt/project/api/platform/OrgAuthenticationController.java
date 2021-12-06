package com.yrt.project.api.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.PatternUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.platform.service.IDealerOrgService;
import com.yrt.project.api.platform.service.IHospitalOrgService;
import com.yrt.project.api.platform.service.IOrgAuthService;
import com.yrt.project.api.platform.service.IVendorOrgService;
import com.yrt.project.api.platform.vo.organization.AddDealerMAAIOLForm;
import com.yrt.project.api.platform.vo.organization.AddDealerSTMDBRCForm;
import com.yrt.project.api.platform.vo.organization.AddHospitalPLHMIForm;
import com.yrt.project.api.platform.vo.organization.AddOrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.AddOrgRelImageForm;
import com.yrt.project.api.platform.vo.organization.AddVendorForm;
import com.yrt.project.api.platform.vo.organization.AddVendorMDPLForm;
import com.yrt.project.api.platform.vo.organization.DealerMAAIOLForm;
import com.yrt.project.api.platform.vo.organization.DealerSTMDBRCForm;
import com.yrt.project.api.platform.vo.organization.EditOrgInfo;
import com.yrt.project.api.platform.vo.organization.HospitalPLHMIForm;
import com.yrt.project.api.platform.vo.organization.OrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.SearchOrgForm;
import com.yrt.project.api.platform.vo.organization.VendorMDPLForm;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;
import com.yrt.project.modular.organization.domain.Vendor;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/platform/org")
@Api(tags = "平台-认证机构信息管理相关接口")
public class OrgAuthenticationController extends BaseController {

	@Autowired
	private IOrgAuthService orgAuthService;
	@Autowired
	private IDealerOrgService dealerOrgService;
	@Autowired
	private IHospitalOrgService hospitalOrgService;
	@Autowired
	private IVendorOrgService venderOrgService;
	
	@SuppressWarnings("unchecked")
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/unAuth/list/{systemType}", method = RequestMethod.GET)
	@ApiOperation(value="根据机构类型获取待审批机构列表(分页)", notes="根据机构类型获取待审批机构列表(分页)")
	public TableDataInfo<Map<String,Object>> searchUnAuthOrgList(@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			SearchOrgForm form){
		startPage();
		form.escapeLikeValue();
		SearchForm f = new SearchForm();
		BeanUtils.copyProperties(form, f);
		f.setCertification_status("0,1,3");
		List<?> list;
		switch (systemType) {
		case 1:
			list = hospitalOrgService.searchOrgList(f);
			break;
		case 2:
			list = dealerOrgService.searchOrgList(f);
			break;
		case 3:
			list = venderOrgService.searchOrgList(f);
			break;
		default:
			list = new ArrayList<Object>();
		}
		return getDataTable(list);
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/list/{systemType}", method = RequestMethod.GET)
	@ApiOperation(value="根据机构类型获取已审批机构列表(分页)", notes="根据机构类型获取已审批机构列表(分页)")
	public TableDataInfo<Map<String,Object>> searchAuthOrgList(@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			SearchOrgForm form){
		startPage();
		form.escapeLikeValue();
		SearchForm f = new SearchForm();
		BeanUtils.copyProperties(form, f);
		f.setCertification_status("2");
		List<?> list;
		switch (systemType) {
		case 1:
			list = hospitalOrgService.searchOrgList(f);
			break;
		case 2:
			list = dealerOrgService.searchOrgList(f);
			break;
		case 3:
			list = venderOrgService.searchOrgList(f);
			break;
		default:
			list = new ArrayList<Object>();
		}
		return getDataTable(list);
	}
	@Log(title = "platform.product", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/image/add/{systemType}/{id}/{certType}", method = RequestMethod.POST)
	@ApiOperation(value = "新增机构相关图片", notes = "新增机构相关图片")
	public ResultInfo<OrgRelImage> addOrgImage(
			@ApiParam(name = "systemType", value = "机构类型0-平台、 1-医院 、 2-经销商、 3-厂商", required = true) @PathVariable int systemType,
			@ApiParam(name = "id", value = "机构ID", required = true) @PathVariable Long id,
			@ApiParam(name = "certType", value = "图片类型(机构不同类型不同)", required = true) @PathVariable int certType,
			@Validated AddOrgRelImageForm form) {
		OrgRelImage img = null;
		switch (systemType) {
		case 1:
			img = hospitalOrgService.insertOrgRelImage(id, certType, form);
			break;
		case 2:
			img = dealerOrgService.insertOrgRelImage(id, certType, form);
			break;
		case 3:
			img = venderOrgService.insertOrgRelImage(id, certType, form);
			break;
		default:
			return ResultInfo.error();
		}
		if (img != null) {
			return ResultInfo.success(img);
		} else {
			return ResultInfo.error();
		}

		/*
		 * switch (systemType) { case 1: return
		 * toAjax(hospitalOrgService.insertOrgRelImage(id, certType, form)); case 2:
		 * return toAjax(dealerOrgService.insertOrgRelImage(id, certType, form)); case
		 * 3: return toAjax(venderOrgService.insertOrgRelImage(id, certType, form));
		 * 
		 * default: return toAjax(0); }
		 */
	}
	
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/image/info/{systemType}/{id}/{certType}", method = RequestMethod.GET)
	@ApiOperation(value="获取机构指定证件类型的图片", notes="获取机构指定证件类型的图片")
	public ResultList<OrgRelImage> getCertImage(@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType, 
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id, 
			@ApiParam(name="certType",value="图片类型",required=true) @PathVariable int certType) {
		List<OrgRelImage> list;
		switch (systemType) {
		case 1:
			list = hospitalOrgService.selectCertImageList2(id, certType);
			break;
		case 2:
			list = dealerOrgService.selectCertImageList2(id, certType);
			break;
		case 3:
			list = venderOrgService.selectCertImageList2(id, certType);
			break;
		default:
			list = new ArrayList<OrgRelImage>();
		}
		return ResultList.success(list);
	}
	
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/image/del/{systemType}/{id}/{certType}/{iid}", method = RequestMethod.DELETE)
	@ApiOperation(value="删除机构指定证件类型的图片", notes="删除机构指定证件类型的图片")
	public AjaxResult deleteCertImage(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType, 
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id, 
			@ApiParam(name="certType",value="图片类型",required=true) @PathVariable int certType,
			@ApiParam(name="iid",value="图片ID",required=true) @PathVariable int iid) {
		switch (systemType) {
		case 1:
			return toAjax(hospitalOrgService.deleteCertImage(id, certType, iid));
		case 2:
			return toAjax(dealerOrgService.deleteCertImage(id, certType, iid));
		case 3:
			return toAjax(venderOrgService.deleteCertImage(id, certType, iid));
		default:
			return toAjax(0);
		}
	}
	
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/image/list/{systemType}/{id}", method = RequestMethod.GET)
	@ApiOperation(value="根据机构ID获取证件图片列表", notes="根据机构ID获取证件图片列表")
	public AjaxResult getCertImageListByOrgId(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		List<OrgRelImage> list;
		switch (systemType) {
		case 1:
			list = hospitalOrgService.getImageListByOrgId(id);
			break;
		case 2:
			list = dealerOrgService.getImageListByOrgId(id);
			break;
		case 3:
			list = venderOrgService.getImageListByOrgId(id);
			break;

		default:
			list = new ArrayList<OrgRelImage>();
		}
		return success().put("images", list);
	}
	
	@Log(title = "platform.org", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/pass/{systemType}/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="机构认证信息审核通过", notes="机构认证信息审核通过")
	public AjaxResult pass(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		return toAjax(orgAuthService.orgAuthPass(systemType, id));
	}
	
	@Log(title = "platform.org", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/nopass/{systemType}/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="机构认证信息审核不通过", notes="机构认证信息审核不通过")
	public AjaxResult noPass(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@RequestParam String reason) {
		return toAjax(orgAuthService.orgAuthNoPass(systemType, id, reason));
	}
	
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/info/{systemType}/{id}", method = RequestMethod.GET)
	@ApiOperation(value="获取机构基本信息", notes="获取机构基本信息")
	public ResultInfo<Company> getOrgInfo(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		switch (systemType) {
		case 1:
			return ResultInfo.success(hospitalOrgService.selectNoAuditOrgInfo(id));
		case 2:
			return ResultInfo.success(dealerOrgService.selectNoAuditOrgInfo(id));
		case 3:
			return ResultInfo.success(venderOrgService.selectNoAuditOrgInfo(id));

		default:
			return ResultInfo.error("系统类型不对");
		}
	}
	
	@Log(title = "platform.org", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/edit/{systemType}/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="修改机构基本信息", notes="修改机构基本信息")
	public AjaxResult editOrgInfo(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@Validated EditOrgInfo org) {
		//TODO:此处应该加入邮箱和电话的正则表达是验证
		if(StringUtils.isNotEmpty(org.getEmail())) {
			PatternUtils.validateEmail(org.getEmail(), "user.email.not.valid");
		}
		//2021-04-30 采购人员要求除了手机号外还需要支持带区号的座机号码
//		if(StringUtils.isNotEmpty(org.getPhone())) {
//			PatternUtils.validatePhone(org.getPhone(), "user.mobile.phone.number.not.valid");
//		}
		SystemType curUserSystemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case 1:
			return toAjax(hospitalOrgService.updateNoAuditOrgInfo(id, org, curUserSystemType));
		case 2:
			return toAjax(dealerOrgService.updateNoAuditOrgInfo(id, org, curUserSystemType));
		case 3:
			return toAjax(venderOrgService.updateNoAuditOrgInfo(id, org, curUserSystemType));

		default:
			return toAjax(0);
		}
	}
	
	@Log(title = "platform.org", businessType = BusinessType.DELETE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/delete/{systemType}/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value="删除机构", notes="删除机构")
	public AjaxResult delOrg(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		switch (systemType) {
		case 1:
			return toAjax(hospitalOrgService.deleteNoAuditOrgInfo(id));
		case 2:
			return toAjax(dealerOrgService.deleteNoAuditOrgInfo(id));
		case 3:
			return toAjax(venderOrgService.deleteNoAuditOrgInfo(id));

		default:
			return toAjax(0);
		}
	}
	
	//营业执照
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/info/license/{systemType}/{id}", method = RequestMethod.GET)
	@ApiOperation(value="获取与机构营业执照相关的所有信息", notes="获取与机构营业执照相关的所有信息")
	public AjaxResult getOrgLicenseInfoById(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		OrgLicenseForm orgLicenseForm = new OrgLicenseForm();
		switch (systemType) {
		case 1:
			orgLicenseForm = hospitalOrgService.selectOrgLicenseInfoById(id);
			break;
		case 2:
			orgLicenseForm = dealerOrgService.selectOrgLicenseInfoById(id);
			break;
		case 3:
			orgLicenseForm = venderOrgService.selectOrgLicenseInfoById(id);
			break;
		}
		return success().put("info", orgLicenseForm);
	}
	
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/add/license/{systemType}/{id}", method = RequestMethod.POST)
	@ApiOperation(value="添加机构营业执照相关的所有信息", notes="添加机构营业执照相关的所有信息")
	public AjaxResult addOrgLicenseInfoById(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@Validated AddOrgLicenseForm form) {
		switch (systemType) {
		case 1:
			return toAjax(hospitalOrgService.insertOrgLicenseInfo(id, form));
		case 2:
			return toAjax(dealerOrgService.insertOrgLicenseInfo(id, form));
		case 3:
			return toAjax(venderOrgService.insertOrgLicenseInfo(id, form));
			default:
				return toAjax(0);
			}
	}
	
	@Log(title = "platform.org", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/edit/license/{systemType}/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="修改与机构营业执照相关的所有信息", notes="修改与机构营业执照相关的所有信息")
	public AjaxResult editOrgLicenseInfo(
			@ApiParam(name="systemType",value="机构类型0-平台、 1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			OrgLicenseForm form) {
		SystemType curUserSystemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case 1:
			return toAjax(hospitalOrgService.updateOrgLicenseInfo(id,form, curUserSystemType));
		case 2:
			return toAjax(dealerOrgService.updateOrgLicenseInfo(id,form, curUserSystemType));
		case 3:
			return toAjax(venderOrgService.updateOrgLicenseInfo(id,form, curUserSystemType));

		default:
			return toAjax(0);
		}
	}
	
	//医院医疗机构执业许可证
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/info/plhmi/{id}", method = RequestMethod.GET)
	@ApiOperation(value="获取医疗机构执业许可证的所有信息 ", notes="获取医疗机构执业许可证的所有信息 ")
	public AjaxResult getHospitalPLHMIById(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		return success().put("info", hospitalOrgService.selectHospitalPLHMI(id));
	}
	
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/add/plhmi/{id}", method = RequestMethod.POST)
	@ApiOperation(value="添加医疗机构执业许可证的所有信息 ", notes="添加医疗机构执业许可证的所有信息 ")
	public AjaxResult addHospitalPLHMIById(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@Validated AddHospitalPLHMIForm form) {
		return toAjax(hospitalOrgService.insertHospitalPLHMI(id, form));
	}
	
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/edit/plhmi/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="编辑医疗机构执业许可证的所有信息 ", notes="编辑医疗机构执业许可证的所有信息 ")
	public AjaxResult editHospitalPLHMIById(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			HospitalPLHMIForm form) {
		SystemType curUserSystemType = ServletUtils.getCurUserSystemType();
		return toAjax(hospitalOrgService.updateHospitalPLHMI(id, form, curUserSystemType));
	}
	
	//经销商医疗器械经营许可证(就是第三类)
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/info/maaiol/{id}", method = RequestMethod.GET)
	@ApiOperation(value="获取经销商医疗器械经营许可证的所有信息 ", notes="获取经销商医疗器械经营许可证的所有信息 ")
	public AjaxResult getDealerMAAIOLById(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		return success().put("info", dealerOrgService.selectDealerMAAIOL(id));
	}
	
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/add/maaiol/{id}", method = RequestMethod.POST)
	@ApiOperation(value="添加经销商医疗器械经营许可证的所有信息 ", notes="添加经销商医疗器械经营许可证的所有信息 ")
	public AjaxResult addDealerMAAIOLById(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@Validated AddDealerMAAIOLForm form) {
		return toAjax(dealerOrgService.insertDealerMAAIOL(id, form));
	}
	
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/edit/maaiol/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="编辑经销商医疗器械经营许可证的所有信息 ", notes="编辑经销商医疗器械经营许可证的所有信息 ")
	public AjaxResult editDealerMAAIOLById(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			DealerMAAIOLForm form) {
		SystemType curUserSystemType = ServletUtils.getCurUserSystemType();
		return toAjax(dealerOrgService.updateDealerMAAIOL(id, form, curUserSystemType));
	}
	
	//经销商第二类医疗器械经营备案凭证
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/info/stmdbrc/{id}", method = RequestMethod.GET)
	@ApiOperation(value="获取经销商第二类医疗器械经营备案凭证的所有信息 ", notes="获取经销商第二类医疗器械经营备案凭证的所有信息 ")
	public AjaxResult getDealerSTMDBRCById(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		return success().put("info", dealerOrgService.selectDealerSTMDBRC(id));
	}
	
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/add/stmdbrc/{id}", method = RequestMethod.POST)
	@ApiOperation(value="添加经销商第二类医疗器械经营备案凭证的所有信息 ", notes="添加经销商第二类医疗器械经营备案凭证的所有信息 ")
	public AjaxResult addDealerSTMDBRCById(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@Validated AddDealerSTMDBRCForm form) {
		return toAjax(dealerOrgService.insertDealerSTMDBRC(id, form));
	}
	
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/edit/stmdbrc/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="编辑经销商第二类医疗器械经营备案凭证的所有信息 ", notes="编辑经销商第二类医疗器械经营备案凭证的所有信息 ")
	public AjaxResult editDealerSTMDBRCById(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			DealerSTMDBRCForm form) {
		SystemType curUserSystemType = ServletUtils.getCurUserSystemType();
		return toAjax(dealerOrgService.updateDealerSTMDBRC(id, form, curUserSystemType));
	}
	
	//厂商医疗器械生产许可证
	@Log(title = "platform.org", businessType = BusinessType.OTHER, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/info/mdpl/{id}", method = RequestMethod.GET)
	@ApiOperation(value="获取厂商医疗器械生产许可证的所有信息", notes="获取医疗器械生产许可证的所有信息")
	public AjaxResult getVendorMDPL(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id) {
		VendorMDPLForm vMDPLForm = 	venderOrgService.selectVendorMDPLById(id);
		  return success().put("info", vMDPLForm);
	}
	
	@Log(title = "platform.org", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/add/mdpl/{id}", method = RequestMethod.POST)
	@ApiOperation(value="添加厂商医疗器械生产许可证的所有信息", notes="添加医疗器械生产许可证的所有信息")
	public AjaxResult addVenderMDPL(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			@Validated AddVendorMDPLForm vmdplfl) {
		return toAjax(venderOrgService.insertVendorMDPL(id, vmdplfl));
	}

	@Log(title = "platform.org", businessType = BusinessType.UPDATE, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/edit/mdpl/{id}", method = RequestMethod.PUT)
	@ApiOperation(value="编辑厂商医疗器械生产许可证的所有信息", notes="编辑医疗器械生产许可证的所有信息")
	public AjaxResult updateVenderMDPL(@ApiParam(name="id",value="机构ID",required=true) @PathVariable Long id,
			VendorMDPLForm vmdplfl) {
		SystemType curUserSystemType = ServletUtils.getCurUserSystemType();
		return toAjax(venderOrgService.updateVendorMDPL(id, vmdplfl, curUserSystemType));
	}
	
	//新增厂商
	@Log(title = "platform.org", businessType = BusinessType.INSERT, operatorType = OperatorType.PLATFORM)
	@RequestMapping(value = "/vendor/add", method = RequestMethod.POST)
	@ApiOperation(value="添加厂商基本信息", notes="添加厂商基本信息")
	public ResultInfo<Vendor> addVender(
			@Validated AddVendorForm form) {
		Vendor v = venderOrgService.insertVendor(form);
		if(v != null) {
			return ResultInfo.success(v);
		}
		return ResultInfo.error();
	}
}
