package com.yrt.project.api.platform.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.client.PushService;
import com.yrt.common.constant.Constants;
import com.yrt.common.constant.MessageConstant;
import com.yrt.common.dict.BaseIntEnum;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.utils.Md5Utils;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.RandomPasswordsUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.warehouse.service.IWarehouseService;
import com.yrt.project.api.common.vo.AddNoAuditOrgForm;
import com.yrt.project.api.platform.service.IOrgRegisterAuditService;
import com.yrt.project.api.platform.vo.organization.MatchBean;
import com.yrt.project.api.platform.vo.organization.MatchResult;
import com.yrt.project.api.platform.vo.organization.NoAuditOrgForm;
import com.yrt.project.api.platform.vo.organization.SearchOrgForm;
import com.yrt.project.modular.organization.domain.CheckCompany;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.Dealer;
import com.yrt.project.modular.organization.domain.Hospital;
import com.yrt.project.modular.organization.domain.NotAuditOrganization;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.domain.User;
import com.yrt.project.modular.organization.domain.Vendor;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.OrganizationMapper;
import com.yrt.project.modular.organization.mapper.UserMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.organization.service.IUserService;
import com.yrt.project.modular.platform.mapper.ProceduresMapper;

@Service
public class OrgRegisterAuditServiceImpl implements IOrgRegisterAuditService{
 
	@Autowired private  OrganizationMapper orgMapper;
	@Autowired private HospitalMapper hMapper;
	@Autowired private DealerMapper dMapper;
	@Autowired private VendorMapper vMapper;
	@Autowired private UserMapper uMapper;
	
	@Autowired private ProceduresMapper mapper;
	@Autowired private PushService pushService;
	@Autowired private IUserService service;
	@Autowired private IWarehouseService wService;
	
	private Long getCurUserEmployeeId() {
		OrgEmployee curEmp = orgMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
		Long id = null;
		if(curEmp != null) {
			id = curEmp.getEmployee_inner_sn();
		}
		return id;
	}
	
	@Override
	public NotAuditOrganization selectNoAuditOrgInfo(Long id) {
		return orgMapper.selectNoAuditOrgInfo(id);
	}
	
	@Override
	public List<NotAuditOrganization> selectAllNoAuditOrgList(SystemType systemType,SearchOrgForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("system_type", systemType);
		params.put("name", form.getName());
		params.put("social_credit_code", form.getSocial_credit_code());
		params.put("registrant", form.getRegistrant());
		return orgMapper.selectAllNoAuditOrgList(params);
	}

	@Override
	@Transactional
	public int insertNoAuditOrg(AddNoAuditOrgForm form) {
		NotAuditOrganization org = new NotAuditOrganization();
		BeanUtils.copyProperties(form, org);
		org.setSystem_type(BaseIntEnum.valueOf(SystemType.class, form.getSystem_type()));
		if(StringUtils.isEmpty(form.getSocial_credit_code())) {
			org.setSocial_credit_code(RandomPasswordsUtils.randomPasswords(18));
		}
		org.setAudit_status(Constants.ORG_REG_AUDIT_STATUS_PEDDING);
		Map<String,Object> params = new HashMap<>();
		params.put("system_type", org.getSystem_type());
		params.put("name1", org.getName());
		List<NotAuditOrganization> tmp = orgMapper.selectAllNoAuditOrgList(params);
		if(StringUtils.isEmpty(tmp)) {
			orgMapper.insertNoAuditOrg(org);
		}else {
			throw new DBException("'" + org.getName() + "'已经注册过了!");
		}
		//发送短信：感谢注册，后台审核
		MessageUtils.sendMessage(form.getRegistrant_phone(), MessageConstant.TEMP_WAITING_AUDIT, "");
		pushService.sendRegOrgMessage(form.getSystem_type(), form.getName());
		return 1;
	}
	
	@Override
	@Transactional
	public int updateNoAuditOrg(Long id, NoAuditOrgForm form) {
		NotAuditOrganization org = new NotAuditOrganization();
		BeanUtils.copyProperties(form, org);
		org.setNot_audit_organization_inner_sn(id);
		return orgMapper.updateNoAuditOrg(org);
	}

	@Override
	@Transactional
	public int deleteNoAuditOrg(Long id) {
		return orgMapper.deleteNoAuditOrg(id);
	}

	@Override
	@Transactional
	public int noPassAduitOrg(Long id, String reason) {
		NotAuditOrganization org = new NotAuditOrganization();
		org.setNot_audit_organization_inner_sn(id);
		org.setAudit_status(Constants.ORG_REG_AUDIT_STATUS_NOPASS);
		int result = orgMapper.updateStatus(org);
		//发送短信，审核未通过
		NotAuditOrganization orgNew = selectNoAuditOrgInfo(id);
		String phoneMsg="#orgName#=" + orgNew.getName() + "&#reason#=" + reason;
		MessageUtils.sendMessage(orgNew.getRegistrant_phone(), MessageConstant.TEMP_ADUIT_NO_PASS, phoneMsg);
		return result;
	}

	@Override
	@Transactional
	public MatchResult passAduitOrg(Long id) {
		MatchResult match = new MatchResult();
		List<MatchBean> resultList = new ArrayList<>();
		String password = RandomPasswordsUtils.randomPasswords(8);
		int result = 0;
		NotAuditOrganization org = selectNoAuditOrgInfo(id);
		SystemType type = org.getSystem_type();
		Company c = new Company();
		c.setAdministrative_division_sn(org.getAdministrative_division_sn());
		c.setStatus(Constants.ORG_STATUS_NORMAL);
//		c.setIs_delete(Constants.ORG_NOT_DELETE);
		c.setData_source(Constants.SYSTEM_TYPE_PLATFORM);//经过审核的肯定是平台建立
		c.setName(org.getName());
		c.setAlias(org.getAlias());
		c.setAddress(org.getAddress());
		c.setPhone(org.getRegistrant_phone());
		c.setEmail(org.getRegistrant_email());
		c.setDescription(org.getDescription());
		c.setRegister_time(org.getRegister_time());
		c.setSocial_credit_code(org.getSocial_credit_code());
		c.setLegal_representative("");
		c.setCertifie_employee_inner_sn(getCurUserEmployeeId());
		//建立默认管理员
		User u = new User();
		String uname = Constants.DEFAULT_USER_NAME_PREFIX+org.getSystem_type().value();
		//u.setPassword(Md5Utils.hash(Constants.DEFAULT_USER_PASSWORD));
		u.setPassword(Md5Utils.hash(password));
		u.setSystem_type(org.getSystem_type());
		u.setPassword_cycle(0);
		u.setPhone(org.getRegistrant_phone());
		u.setType(Constants.USER_TYPE_ADMIN);
		
		try {
			Map<String, Object> params = new HashMap<>();
			CheckCompany company = null;
			switch (type) {
			case HOSPITAL://医院
				params.put("name", c.getName());
				params.put("social_credit_code", c.getSocial_credit_code());
				company = hMapper.checkExistOrg(params);
				if(StringUtils.isNotNull(company)) {//检查是否存在对应机构
					//是否存在机构管理员账号
					uname = uname + company.getId();
					u = uMapper.selectUserByLoginName(uname);
					if(StringUtils.isNotNull(u)) {
						resultList = setMatchData(c, company);
						match.setAccount(uname);
					}else {
						resultList = setMatchData(c, company);
					}
					match.setList(resultList);
					match.setOrgId(company.getId());
					return match;
				}else {
					Hospital h = new Hospital();
					BeanUtils.copyProperties(c, h);
					h.setEnterprise_type(0);
					result = hMapper.insertOrg(h);
					u.setOrg_id(h.getId());
					u.setLongin_name(uname + h.getId());
				}
				break;
			case DEALER://经销商
				params.put("name", c.getName());
				params.put("social_credit_code", c.getSocial_credit_code());
				company = dMapper.checkExistOrg(params);
				if(StringUtils.isNotNull(company)) {//检查是否存在对应机构
					//是否存在机构管理员账号
					uname = uname + company.getId();
					u = uMapper.selectUserByLoginName(uname);
					if(StringUtils.isNotNull(u)) {
						resultList = setMatchData(c, company);
						match.setAccount(uname);
					}else {
						resultList = setMatchData(c, company);
					}
					match.setList(resultList);
					match.setOrgId(company.getId());
					return match;
				}else {
					Dealer d = new Dealer();
					BeanUtils.copyProperties(c, d);
					d.setEnterprise_type(0);
					result = dMapper.insertOrg(d);
					u.setOrg_id(d.getId());
					u.setLongin_name(uname + d.getId());
				}
				break;
			case VENDOR://厂商
				params.put("name", c.getName());
				params.put("social_credit_code", c.getSocial_credit_code());
				company = vMapper.checkExistOrg(params);
				if(StringUtils.isNotNull(company)) {//检查是否存在对应机构
					//是否存在机构管理员账号
					uname = uname + company.getId();
					u = uMapper.selectUserByLoginName(uname);
					if(StringUtils.isNotNull(u)) {
						resultList = setMatchData(c, company);
						match.setAccount(uname);
					}else {
						resultList = setMatchData(c, company);
					}
					match.setList(resultList);
					match.setOrgId(company.getId());
					return match;
				}else {
					Vendor v = new Vendor();
					BeanUtils.copyProperties(c, v);
					v.setEnterprise_type(0);
					result = vMapper.insertOrg(v);
					u.setOrg_id(v.getId());
					u.setLongin_name(uname + v.getId());
				}
				break;
			default:
			}
			
			if (result > 0) {
				result = uMapper.insertUser(u);
				if(result > 0) {
					result = orgMapper.deleteNoAuditOrg(id);
					//初始化默认仓库
					wService.initwarehouseMode(u.getOrg_id(), type);
					// 审核通过 发送短信
					String phoneMsg="#useraccount#="+u.getLongin_name()+"&#password#="+password;
					MessageUtils.sendMessage(org.getRegistrant_phone(), MessageConstant.TEMP_ADUIT_PASS, phoneMsg);
					// TODO: 记录注册机构管理员账号密码
					User curUser = service.selectUserById(ServletUtils.getCurUserID());
					String curName = "";
		            if(curUser != null) {
		            	curName = curUser.getLongin_name();
		            }
					Map<String, Object> params2 = new HashMap<>();
					params2.put("organization_name", c.getName());
					params2.put("account", u.getLongin_name());
					params2.put("password", password);
					params2.put("register_time", c.getRegister_time());
					params2.put("operator", curName);
					params2.put("type", 0);//保存机构管理员密码
					mapper.insertSMSTemp(params2);
				}
			}
		}catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException("机构审核失败!原因:机构名称或者机构社会信用代码重复");
			}else {
				throw new DBException("机构审核失败!原因:"+e.getMessage());
			}
		}
		return match;
	}

	@Override
	@Transactional
	public int match(CheckCompany c, Long id) {
		NotAuditOrganization org = selectNoAuditOrgInfo(id);
		SystemType type = org.getSystem_type();
		int result = 0;
		Map<String, Object> params = new HashMap<>();
		params.put("id", c.getId());
		params.put("name", c.getName());
		params.put("alias", c.getAlias());
		params.put("social_credit_code", c.getSocial_credit_code());
		params.put("administrative_division_sn", StringUtils.isNull(c.getAdministrative_division_sn())?null:c.getAdministrative_division_sn());
		params.put("register_time", c.getRegister_time());
		params.put("address", c.getAddress());
		params.put("phone", c.getPhone());
		params.put("email", c.getEmail());
		params.put("description", c.getDescription());
		params.put("is_delete", Constants.ORG_NOT_DELETE);
		try {
			switch (type) {
			case HOSPITAL://医院
				hMapper.updateOrg4Map(params);
				break;
			case DEALER://经销商
				dMapper.updateOrg4Map(params);
				break;
			case VENDOR://厂商
				vMapper.updateOrg4Map(params);
				break;
			default:
			}
			//建立默认管理员
			User u = null;
			String uname = String.format("%s%d%d", Constants.DEFAULT_USER_NAME_PREFIX, org.getSystem_type().value(), c.getId());
			u = uMapper.selectUserByLoginName(uname);
			String password = RandomPasswordsUtils.randomPasswords(8);
			
			if(StringUtils.isNotNull(u)) {
				u.setPassword(Md5Utils.hash(password));
				u.setPhone(c.getPhone());
				result = uMapper.updateUser(u);
			}else {
				u = new User();
				u.setOrg_id(c.getId());
				u.setLongin_name(uname);
				u.setPassword(Md5Utils.hash(password));
				u.setSystem_type(org.getSystem_type());
				u.setPassword_cycle(0);
				u.setPhone(org.getRegistrant_phone());
				u.setType(Constants.USER_TYPE_ADMIN);
				result = uMapper.insertUser(u);
			}
			if(result > 0) {
				result = orgMapper.deleteNoAuditOrg(id);
				// 审核通过 发送短信
				String phoneMsg="#useraccount#="+u.getLongin_name()+"&#password#="+password;
				MessageUtils.sendMessage(c.getPhone(), MessageConstant.TEMP_ADUIT_PASS, phoneMsg);
				// TODO: 记录注册机构管理员账号密码
				User curUser = service.selectUserById(ServletUtils.getCurUserID());
				String curName = "";
	            if(curUser != null) {
	            	curName = curUser.getLongin_name();
	            }
				Map<String, Object> params2 = new HashMap<>();
				params2.put("organization_name", c.getName());
				params2.put("account", u.getLongin_name());
				params2.put("password", password);
				params2.put("register_time", c.getRegister_time());
				params2.put("operator", curName);
				params2.put("type", 0);//保存机构管理员密码
				mapper.insertSMSTemp(params2);
			}
		}catch(Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException("机构审核失败!原因:机构名称或者机构社会信用代码重复");
			}else {
				throw new DBException("机构审核失败!原因:"+e.getMessage());
			}
		}
		
		return 1;
	}
	
	private List<MatchBean> setMatchData(Company new_company, CheckCompany old_compay){
		List<MatchBean> resultList = new ArrayList<>();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MatchBean bean1 = new MatchBean("name", "机构名称", new_company.getName(), old_compay.getName());
		MatchBean bean2 = new MatchBean("alias", "机构别名", new_company.getAlias(), old_compay.getAlias());
		MatchBean bean3 = new MatchBean("social_credit_code", "统一社会信用代码", new_company.getSocial_credit_code(), old_compay.getSocial_credit_code());
		MatchBean bean4 = new MatchBean("administrative_division_sn", "行政区划", 
				StringUtils.isNull(new_company.getAdministrative_division_sn())?"":new_company.getAdministrative_division_sn().toString(), 
				StringUtils.isNull(old_compay.getAdministrative_division_sn())?"":old_compay.getAdministrative_division_sn().toString());
		MatchBean bean5 = new MatchBean("phone", "注册人电话", new_company.getPhone(), old_compay.getPhone());
		MatchBean bean6 = new MatchBean("email", "注册人邮箱", new_company.getEmail(), old_compay.getEmail());
		MatchBean bean7 = new MatchBean("address", "地址", new_company.getAddress(), old_compay.getAddress());
		MatchBean bean8 = new MatchBean("description", "备注", new_company.getDescription(), old_compay.getDescription());
		MatchBean bean9 = new MatchBean("register_time", "注册时间", 
				StringUtils.isNull(new_company.getRegister_time())?null:f.format(new_company.getRegister_time()),
				StringUtils.isNull(old_compay.getRegister_time())?null:f.format(old_compay.getRegister_time()));
		resultList.add(bean1);
		resultList.add(bean2);
		resultList.add(bean3);
		resultList.add(bean4);
		resultList.add(bean5);
		resultList.add(bean6);
		resultList.add(bean7);
		resultList.add(bean8);
		resultList.add(bean9);
		return resultList;
	}
}
