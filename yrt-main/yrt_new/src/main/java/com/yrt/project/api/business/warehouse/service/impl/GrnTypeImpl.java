package com.yrt.project.api.business.warehouse.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.warehouse.service.IGrnTypeService;
import com.yrt.project.api.business.warehouse.vo.grn.AddGrnTypeForm;
import com.yrt.project.api.business.warehouse.vo.grn.EditGrnTypeForm;
import com.yrt.project.modular.warehouse.domain.GrnType;
import com.yrt.project.modular.warehouse.mapper.DealerGrnTypeMapper;
import com.yrt.project.modular.warehouse.mapper.GrnTypeMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalGrnTypeMapper;
import com.yrt.project.modular.warehouse.mapper.VendorGrnTypeMapper;

@Service
public class GrnTypeImpl implements IGrnTypeService {

	@Autowired DealerGrnTypeMapper dMapper;
	@Autowired HospitalGrnTypeMapper hMapper;
	@Autowired VendorGrnTypeMapper vMapper;
	
	private GrnTypeMapper getMapper() {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case HOSPITAL:
			return hMapper;
		case DEALER:
			return dMapper;
		case VENDOR:
			return vMapper;
		default:
		}
		return null;
	}
	
	@Override
	@Transactional
	public GrnType insertGrnType(AddGrnTypeForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("status_i", form.getStatus());
		params.put("is_delete_i", 0);
		params.put("name_i", form.getName());
		getMapper().insertGrnType(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long tid = Long.valueOf(params.get("type_inner_sn_o").toString());
			Map<String,Object> params1 = new HashMap<>();
			params1.put("org_id", form.getOrg_id());
			params1.put("type_inner_sn", tid);
			return getMapper().selectGrnTypeInfo(params1);
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","入库单类型",params.get("message_string_o")));
		}
	}

	@Override
	@Transactional
	public int updateGrnType(EditGrnTypeForm form) {
		try {
			Map<String,Object> params = new HashMap<>();
			params.put("org_id", form.getOrg_id());
			params.put("type_inner_sn", form.getType_inner_sn());
			params.put("name", form.getName());
			params.put("status", form.getStatus());
			return getMapper().updateGrnType(params);
		} catch (Exception e) {
			if (e instanceof DuplicateKeyException) {
				throw new DuplicateException("已有相同入库单类型存在！");
			}else {
				throw new DBException(MessageUtils.message("db.update.fail", "入库单类型", e.getMessage()));
			}
		}
	}

	@Override
	@Transactional
	public int deleteGrnType(Long oid, Long id) {
		GrnTypeMapper mapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("type_inner_sn", id);
		if(StringUtils.isNotEmpty(mapper.checkUseList(params))){
			throw new ValidateException("单据类型已经被使用，不能删除！");
		}
		return mapper.deleteGrnType(params);
	}

	@Override
	public List<GrnType> selectGrnTypeList(Long oid, Integer status) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("status", status);
		return getMapper().selectGrnTypeList(params);
	}

}
