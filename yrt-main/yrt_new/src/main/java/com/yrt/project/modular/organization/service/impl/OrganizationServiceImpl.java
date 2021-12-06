package com.yrt.project.modular.organization.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.ServletUtils;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.NotAuditOrganization;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.domain.SearchForm;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.OrganizationMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.organization.service.IOrganizationService;

@Service
public class OrganizationServiceImpl implements IOrganizationService {

	@Autowired
	private OrganizationMapper orgMapper;
	@Autowired
	private HospitalMapper hMapper;
	@Autowired
	private DealerMapper dMapper;
	@Autowired
	private VendorMapper vMapper;

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
	public List<NotAuditOrganization> selectAllNoAuditOrgList(SystemType systemType,SearchForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("system_type", systemType);
		params.put("name", form.getName());
		return orgMapper.selectAllNoAuditOrgList(params);
	}

	@Override
	@Transactional
	public int insertNoAuditOrg(NotAuditOrganization org) {
		return orgMapper.insertNoAuditOrg(org);
	}

	@Override
	@Transactional
	public int updateNoAuditOrg(NotAuditOrganization org) {
		return orgMapper.updateNoAuditOrg(org);
	}

	@Override
	@Transactional
	public int deleteNoAuditOrg(Long id) {
		return orgMapper.deleteNoAuditOrg(id);
	}

	@Override
	@Transactional
	public int updateStatus(NotAuditOrganization org) {
		return orgMapper.updateStatus(org);
	}

	@Override
	@Transactional
	public int passAduitOrg(Long id) {
		NotAuditOrganization org = selectNoAuditOrgInfo(id);
		int result = orgMapper.deleteNoAuditOrg(id);
		if (result > 0) {
			SystemType type = org.getSystem_type();
			Company c = new Company();
			c.setAdministrative_division_sn(org.getAdministrative_division_sn());
			c.setStatus(1);
//			c.setIs_delete(0);
			c.setData_source(0);//经过审核的肯定是平台建立
			c.setName(org.getName());
			c.setAlias(org.getAlias());
			c.setAddress(org.getAddress());
			c.setPhone(org.getRegistrant_phone());
			c.setEmail(org.getRegistrant_email());
			c.setDescription(org.getDescription());
			c.setRegister_time(org.getRegister_time());
			c.setCertifie_employee_inner_sn(getCurUserEmployeeId());
			switch (type) {
			case HOSPITAL://医院
				result = hMapper.insertOrg(c);
				break;
			case DEALER://经销商
				result = dMapper.insertOrg(c);
				break;
			case VENDOR://厂商
				result = vMapper.insertOrg(c);
				break;

			default:
				break;
			}
		}
		return result;
	}
	
}
