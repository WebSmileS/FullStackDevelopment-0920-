package com.yrt.project.modular.category.service;

import java.util.List;

import com.yrt.project.modular.category.domain.Code68;

/**
 * 医疗器械68分类服务
 * @author Runner
 *
 */
public interface ICode68Service {

	/**
	 * 查询医疗器械68分类信息
	 * @param code68_sn
	 */
	public Code68 selectCode68ById(Long id);
	
	/**
	 * 查询医疗器械68分类下级分类列表信息
	 * @param id
	 * @return List<Code68>
	 */
	public List<Code68> selectCode68ChildrenById(Long id);
	
	/**
	 * 查询最上级医疗器械68分类信息
	 * @return
	 */
	public List<Code68> selectCode68Mogami();
	
	
	/**
	 * 获取整个68分类树
	 * @return
	 */
	public List<Code68> selectAllCode68();

	/**
	 * 根据id查询所有上级信息
	 * @param code
	 * @param parent_code68_sn
	 */
	public void selectParent(Code68 code, Long parent_code68_sn);
}
