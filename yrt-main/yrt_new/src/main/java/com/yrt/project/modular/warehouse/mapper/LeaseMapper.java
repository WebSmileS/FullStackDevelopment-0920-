package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warehouse.domain.Lease;

@Repository
public interface LeaseMapper {

	public void insertLease(Map<String,Object> params);
	
	public void deleteLease(Long id);
	
	public List<Lease> selectLeaseList(Map<String,Object> params);
	
	public Lease selectLeaseById(Long id);
	
	public Lease existLease(Map<String,Object> params);
}
