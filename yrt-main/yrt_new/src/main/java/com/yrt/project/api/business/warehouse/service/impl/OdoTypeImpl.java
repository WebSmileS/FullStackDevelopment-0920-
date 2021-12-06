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
import com.yrt.project.api.business.warehouse.service.IOdoTypeService;
import com.yrt.project.api.business.warehouse.vo.odo.AddOdoTypeForm;
import com.yrt.project.api.business.warehouse.vo.odo.EditOdoTypeForm;
import com.yrt.project.modular.warehouse.domain.OdoType;
import com.yrt.project.modular.warehouse.mapper.DealerOdoTypeMapper;
import com.yrt.project.modular.warehouse.mapper.OdoTypeMapper;
import com.yrt.project.modular.warehouse.mapper.VendorOdoTypeMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalOdoTypeMapper;

@Service
public class OdoTypeImpl implements IOdoTypeService {

	@Autowired DealerOdoTypeMapper dMapper;
	@Autowired HospitalOdoTypeMapper hMapper;
	@Autowired VendorOdoTypeMapper vMapper;
	
	private OdoTypeMapper getMapper() {
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
	public OdoType insertOdoType(AddOdoTypeForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("status_i", form.getStatus());
		params.put("is_delete_i", 0);
		params.put("name_i", form.getName());
		getMapper().insertOdoType(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long tid = Long.valueOf(params.get("type_inner_sn_o").toString());
			Map<String,Object> params1 = new HashMap<>();
			params1.put("org_id", form.getOrg_id());
			params1.put("type_inner_sn", tid);
			return getMapper().selectOdoTypeInfo(params1);
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","入库单类型",params.get("message_string_o")));
		}
	}

	@Override
	@Transactional
	public int updateOdoType(EditOdoTypeForm form) {
		try {
			Map<String,Object> params = new HashMap<>();
			params.put("org_id", form.getOrg_id());
			params.put("type_inner_sn", form.getType_inner_sn());
			params.put("name", form.getName());
			params.put("status", form.getStatus());
			return getMapper().updateOdoType(params);
		} catch (Exception e) {
			if (e instanceof DuplicateKeyException) {
				throw new DuplicateException("已有相同出库单类型存在！");
			}else {
				throw new DBException(MessageUtils.message("db.update.fail", "出库单类型", e.getMessage()));
			}
		}
	}

	@Override
	@Transactional
	public int deleteOdoType(Long oid, Long id) {
		OdoTypeMapper mapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("type_inner_sn", id);
		if(StringUtils.isNotEmpty(mapper.checkUseList(params))){
			throw new ValidateException("单据类型已经被使用，不能删除！");
		}
		return mapper.deleteOdoType(params);
	}

	@Override
	public List<OdoType> selectOdoTypeList(Long oid, Integer status) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("status", status);
		return getMapper().selectOdoTypeList(params);
	}

}
