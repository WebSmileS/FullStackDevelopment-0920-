package com.yrt.project.api.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.*;
import com.yrt.project.modular.category.domain.Code68;
import com.yrt.project.modular.category.service.ICode68Service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/comm/code68")
@Api(tags = "公用-医疗器械68分类相关接口")
public class Code68Controller extends BaseController {

	@Autowired
	private ICode68Service code68Service;

	@Log(title = "common.code68", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value="根据id查询分类信息", notes="查询医疗器械68分类信息的详情")
	public ResultInfo<Code68> getCode68Info(@PathVariable Long id) {
		Code68 code = code68Service.selectCode68ById(id);
		return ResultInfo.success(code);
	}

	@Log(title = "common.code68", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list/{id}", method = RequestMethod.GET)
	@ApiOperation(value="根据pid查询分类下所有的下级分类信息", notes="查询医疗器械68分类列表信息的详情")
	public ResultList<Code68> getCode68List(@PathVariable Long id) {
		List<Code68> list = code68Service.selectCode68ChildrenById(id);
		return ResultList.success(list);
	}
	
	@Log(title = "common.code68", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value="查询所有的最上级68分类信息", notes="查询医疗器械68分类列表信息的详情")
	public ResultList<Code68> getCode68List() {
		List<Code68> list =code68Service.selectCode68Mogami();
		return ResultList.success(list);
	}
	
	@Log(title = "common.code68", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	@ApiOperation(value="查询所有68分类信息以树的方式返回", notes="查询所有68分类信息以树的方式返回")
	public AjaxResult getCode68TreeList() {
		List<Code68> list =code68Service.selectAllCode68();
		return success().put("tree",list);
	}
	
	@Log(title = "common.code68", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value="/parents/{id}", method = RequestMethod.GET)
	@ApiOperation(value="根据id查询所有上级信息",notes="根据id查询所有上级信息")
	public ResultInfo<Code68> getParentsCode68ListById(@PathVariable Long id) {
		Code68 code = code68Service.selectCode68ById(id);
		if(code == null) {
			return ResultInfo.error();
		}
		code68Service.selectParent(code, code.getParent_code68_sn());
		
		return ResultInfo.success(code);
	}
}
