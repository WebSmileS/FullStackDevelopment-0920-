package com.yrt.project.modular.organization.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.organization.domain.HospitalPLHMI;

/**
 * 经销商相关数据   数据层
 * @author Runner
 *
 */
@Repository
public interface HospitalMapper extends OrgMapper {
	
	//医院医疗机构执业许可证
	public HospitalPLHMI selectHospitalPLHMIById(Long id);
	
	public int insertHospitalPLHMI(HospitalPLHMI plhmi);
	
	public int updateHospitalPLHMI(HospitalPLHMI plhmi);
	
	public int updateHospitalPLHMI4Map(Map<String, Object> params);
}
