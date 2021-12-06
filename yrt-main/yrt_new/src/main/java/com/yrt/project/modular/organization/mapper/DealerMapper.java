package com.yrt.project.modular.organization.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.organization.domain.BusinessScopeRelCode68;
import com.yrt.project.modular.organization.domain.DealerMAAIOL;
import com.yrt.project.modular.organization.domain.DealerSTMDBRC;

/**
 * 经销商相关数据 数据层
 * @author Administrator
 *
 */
@Repository
public interface DealerMapper extends OrgMapper{
	
	//经销商医疗器械经营许可证(就是第三类)
	public DealerMAAIOL selectMAAIOLById(Long id);
	
	public int insertMAAIOL(DealerMAAIOL maaiol);
	
	public int updateMAAIOL(DealerMAAIOL maaiol);
	
	public int updateMAAIOL4Map(Map<String, Object> params);
	
	public int insertMAAIOLScopeRelCode68(BusinessScopeRelCode68 code);
	
	public int deleteMAAIOLScopeRelCode68(Long org_id);
	
	//经销商第二类医疗器械经营备案凭证
	public DealerSTMDBRC selectSTMDBRCById(Long id);
	
	public int insertSTMDBRC(DealerSTMDBRC stmdbrc);
	
	public int updateSTMDBRC(DealerSTMDBRC stmdbrc);
	
	public int updateSTMDBRC4Map(Map<String, Object> params);
	
	public int insertSTMDBRCScopeRelCode68(BusinessScopeRelCode68 code);
	
	public int deleteSTMDBRCScopeRelCode68(Long org_id);
	
}
