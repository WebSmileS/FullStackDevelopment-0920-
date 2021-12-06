package com.yrt.project.modular.category.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.project.modular.category.domain.Code68;
import com.yrt.project.modular.category.mapper.CategoryMapper;

@Service
public class Code68ServiceImpl implements ICode68Service {

	@Autowired
	private CategoryMapper mapper;
	
	@Override
	public Code68 selectCode68ById(Long id) {
		return mapper.selectCode68ById(id);
	}

	@Override
	public List<Code68> selectCode68ChildrenById(Long id) {
		return mapper.selectCode68ChildrenById(id);
	}

	@Override
	public List<Code68> selectCode68Mogami() {
		return mapper.selectCode68Mogami();
	}
	
	@Override
	public List<Code68> selectAllCode68(){
		List<Code68> c68s = mapper.selectCode68Mogami();
		for (Code68 code68 : c68s) {
			getCode68Children(code68);
		}
		return c68s;
	}
	
	private void getCode68Children(Code68 c68) {
		Long pid = c68.getCode68_sn();
		List<Code68> children = selectCode68ChildrenById(pid);//mapper.selectCode68ChildrenByPid(pid);
		for (Code68 child : children) {
			if(child.getIs_leaf() == 0) {
				getCode68Children(child);
			}
		}
		c68.setChildren(children);
	}

	@Override
	public void selectParent(Code68 code, Long pid) {
		if(pid != null) {
			Code68 parent = selectCode68ById(pid);
			code.setParent(parent);
			selectParent(parent, parent.getParent_code68_sn());
		}
	}

}
