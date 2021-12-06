package com.yrt.project.api.common.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.project.modular.common.mapper.MaxSnMapper;

/**
 * 最大编号, for tables: XXX_max_sn & voucher_sn.
 */
@Service
public class MaxSnService {
	
	private static final int ONE = 1;
	
	@Autowired
	private MaxSnMapper dao;
	
	/**
	 * 分配一个 sn
	 * @param tableName 
	 * @param keys primary key columns/values
	 * @return
	 */
	//no-block each-other(maybe produce in-continues sn)
	@Transactional//(propagation=Propagation.REQUIRES_NEW)
	public long getMaxSn(String tableName, Map<String, Object> keys) {
		return dao.getMaxSn(tableName, keys, ONE);
	}
	
	/**
	 * 一次 分配size个 sn
	 * @param tableName
	 * @param keys primary key columns/values
	 * @param size
	 * @return list of SNs
	 */
	@Transactional//(propagation=Propagation.REQUIRES_NEW)
	public List<Long> getMaxSn(String tableName, Map<String, Object> keys, int size) {
		long last = dao.getMaxSn(tableName, keys, size);
		return LongStream.rangeClosed(last-size+1, last).boxed().collect(Collectors.toList());
	}
	
	
}
