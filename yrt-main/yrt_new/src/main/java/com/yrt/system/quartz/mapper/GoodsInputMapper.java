package com.yrt.system.quartz.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.system.quartz.domain.GoodsInput;

@Repository
public interface GoodsInputMapper {
	
	/**
	 * 查询全部商品
	 * @return
	 */
	public List<GoodsInput> selectAllGoods();
	
	/**
	 * 修改转态和成功msg
	 */
	public void updateMsgAndStatus(Long id,String msg);
	
	/**
	 * 修改msg和失败转态
	 */
	public void updateMsgAndNostatus(Long id,Integer status, String msg);
	
	/**
	 * 修改msg和失败状态
	 */
	public void updateErrorMsg(Long id, Integer status, String msg);
	
	public void updateMDRFErrorMsg(Long id, Integer status, String msg);
	
	public void updatePublishErrorMsg(Long id, Integer status, String msg);
	
	
	//************以下为ImportProductTask3版本**********
	/**
	 * 查询全部商品
	 * @return
	 */
	public List<GoodsInput> selectAllGoods2();
	/**
	 * 查询某一商品
	 * @return
	 */
	public List<GoodsInput> selectGoodsById2(GoodsInput input);
	/**
	 * 修改转态和成功msg
	 */
	default void updateMsgAndStatus2(Long id,String msg) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("msg", msg);
		updateMsgAndStatus2(params);
	}
	
	public void updateMsgAndStatus2(Map<String, Object> params);
	
	/**
	 * 修改msg和失败转态
	 */
	public void updateMsgAndNostatus2(Long id,Integer status, String msg);
	
	/**
	 * 修改msg和失败状态
	 */
	public void updateErrorMsg2(Long id, Integer status, String msg);
	
	public void updateMDRFErrorMsg2(Long id, Integer status, String msg);
	
	public void updatePublishErrorMsg2(Long id, Integer status, String msg);
	
	public void updateSPecErrorMsg2(Long id, Integer status, String msg);
	
	public void updateUnitErrorMsg2(Long id, Integer status, String msg);
}
