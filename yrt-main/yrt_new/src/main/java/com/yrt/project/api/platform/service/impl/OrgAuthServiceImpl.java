package com.yrt.project.api.platform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.constant.MessageConstant;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.project.api.platform.service.IOrgAuthService;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.OrgMapper;
import com.yrt.project.modular.organization.mapper.OrganizationMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;



@Service
public class OrgAuthServiceImpl implements IOrgAuthService {

	@Autowired
	private  OrganizationMapper orgMapper;
	
	@Autowired private DealerMapper dMapper;
	@Autowired private HospitalMapper hMapper;
	@Autowired private VendorMapper vMapper;

	private Long getCurUserEmployeeId() {
		OrgEmployee curEmp = orgMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
		Long id = null;
		if(curEmp != null) {
			id = curEmp.getEmployee_inner_sn();
		}
		return id;
	}
	
	private OrgMapper getMapper(int systemType) {
		switch (systemType) {
		case 1:
			return hMapper;
		case 2:
			return dMapper;
		case 3:
			return vMapper;

		default:
			return null;
		}
	}

	@Override
	@Transactional
	public int orgAuthPass(int systemType, Long id) {
		OrgMapper mapper = getMapper(systemType);
		Company c = new Company();
		c.setId(id);
		c.setCertification_status(2);
		c.setCertifie_employee_inner_sn(getCurUserEmployeeId());
		
		return mapper.updateCertifieStatus2(c);
	}

	@Override
	@Transactional
	public int orgAuthNoPass(int systemType, Long id, String reason) {
		OrgMapper mapper = getMapper(systemType);
		Company c = new Company();
		c.setId(id);
		c.setCertification_status(3);
		c.setCertifie_employee_inner_sn(getCurUserEmployeeId());
		Company org = mapper.selectOrgById(id);
		// 认证通过 发送短信
		String phoneMsg="#orgName#=" + org.getName() + "&#reason#=" + reason;
		MessageUtils.sendMessage(org.getPhone(), MessageConstant.TEMP_ADUIT_NO_PASS, phoneMsg);
		return mapper.updateCertifieStatus2(c);
	}
	
}
