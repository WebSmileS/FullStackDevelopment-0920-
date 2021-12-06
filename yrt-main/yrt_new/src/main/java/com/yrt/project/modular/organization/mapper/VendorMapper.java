package com.yrt.project.modular.organization.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.organization.domain.BusinessScopeRelCode68;
import com.yrt.project.modular.organization.domain.SearchForm;
import com.yrt.project.modular.organization.domain.VendorMDPL;

/**
 * 厂商相关数据   数据层
 * @author Runner
 *
 */
@Repository
public interface VendorMapper extends OrgMapper {
	
	public List<Map<String,Object>> selectOrgList4Dropdown1(SearchForm form);
	
	//厂商医疗器械生产许可证
	public VendorMDPL selectVendorMDPLById(Long id);
	
	public int insertVendorMDPL(VendorMDPL mdpl);
	
	public int updateVendorMDPL(VendorMDPL mdpl);
	
	public int updateVendorMDPL4Map(Map<String, Object> params);
	
	public int insertMDPLScopeRelCode68(BusinessScopeRelCode68 code);
	
	public int deleteMDPLScopeRelCode68(Long org_id);
	
	//删除厂商  定时任务导入相关
	public int deleteVendorTask(Long id);
}
