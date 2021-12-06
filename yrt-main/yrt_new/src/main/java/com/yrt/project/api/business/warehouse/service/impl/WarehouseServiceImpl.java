package com.yrt.project.api.business.warehouse.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.PinyinUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.warehouse.service.IWarehouseService;
import com.yrt.project.api.business.warehouse.vo.AddWareHouseForm;
import com.yrt.project.api.business.warehouse.vo.AddWarehouseTypeForm;
import com.yrt.project.api.business.warehouse.vo.EditWareHouseForm;
import com.yrt.project.api.business.warehouse.vo.EditWarehouseTypeForm;
import com.yrt.project.api.business.warehouse.vo.WareHouseRelEmp;
import com.yrt.project.api.business.warehouse.vo.WarehouseCheckForm;
import com.yrt.project.api.business.warehouse.vo.WarehouseScanForm;
import com.yrt.project.api.common.service.MaxSnService;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;
import com.yrt.project.modular.warehouse.domain.WarehouseRelDept;
import com.yrt.project.modular.warehouse.domain.WarehouseRelEmployee;
import com.yrt.project.modular.warehouse.domain.WarehouseType;
import com.yrt.project.modular.warehouse.mapper.DealerWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.VendorWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.WareHouseMapper;

@Service
public class WarehouseServiceImpl implements IWarehouseService {

	@Autowired DealerWarehouseMapper dwMapper;
	@Autowired HospitalWarehouseMapper hwMapper;
	@Autowired VendorWarehouseMapper vwMapper;
	
	@Autowired MaxSnService snService;
	
	private WareHouseMapper getWarehouseMapper() {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		return getWarehouseMapper(systemType);
	}
	
	private WareHouseMapper getWarehouseMapper(SystemType systemType) {
		switch (systemType) {
		case HOSPITAL:
			return hwMapper;
		case DEALER:
			return dwMapper;
		case VENDOR:
			return vwMapper;
		default:
		}
		return null;
	}
	
	private long getSnMax(Long oid) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		String tableName = null;
		String keyName = null;
		switch (systemType) {
		case HOSPITAL:
			tableName = "hospital_warehouse_model_inner_sn_max";
			keyName = "hospital_inner_sn";
			break;
		case DEALER:
			tableName = "dealer_warehouse_model_inner_sn_max";
			keyName = "dealer_inner_sn";
			break;
		case VENDOR:
			tableName = "vendor_warehouse_model_inner_sn_max";
			keyName = "vendor_inner_sn";
			break;
		default:
		}
		Map<String, Object> keys = new HashMap<>();
		keys.put(keyName, oid);

		return snService.getMaxSn(tableName, keys);
	}
	
	@Override
	@Transactional
	public void initwarehouseMode(Long oid, SystemType type) {
		//机构初始化仓库，一个默认仓库
		WareHouseMapper mapper = getWarehouseMapper(type);
		Map<String, Object> params = new HashMap<>();
		params.put("org_id_i", oid);
		params.put("id", IWarehouseService.DEAFULT_WAREHOUSE_ID);
		params.put("parent_warehouse_model_inner_sn_i", null);
		params.put("warehouse_type_inner_sn_i", null);
		params.put("administrative_division_sn_i", null);
		params.put("name_i", "默认仓库");
		params.put("warehouse_model_sn_i", 1);
		params.put("type_i", IWarehouseService.WARE_HOUSE_TYPE_STORE);
		params.put("proportion_i", 0);
		params.put("aisle_sn_i", "");
		params.put("phone_i", "");
		params.put("barcode_i", "");
		params.put("address_i", "");
		params.put("description_i", "");
		mapper.insertWarehouseModel(params);
	}
	
	@Override
	@Transactional
	public WarehouseModel insertWarehouseMode(Long oid, AddWareHouseForm form) {
		Long wid = getSnMax(oid);
		WareHouseMapper mapper = getWarehouseMapper();
		Map<String, Object> params = new HashMap<>();
		params.put("org_id_i", oid);
		params.put("id", wid);
		params.put("parent_warehouse_model_inner_sn_i", form.getParent_warehouse_model_inner_sn_i());
		params.put("warehouse_type_inner_sn_i", form.getWarehouse_type_inner_sn_i());
		params.put("administrative_division_sn_i", form.getAdministrative_division_sn_i());
		String name = form.getName_i();
		params.put("name_i", name);
		String sn = form.getWarehouse_model_sn_i();
		if(StringUtils.isEmpty(sn)) {
			sn = PinyinUtils.getPy(name);
			Map<String,Object> params1 = new HashMap<>();
			params1.put("org_id", oid);
			params1.put("parent_warehouse_model_inner_sn", form.getParent_warehouse_model_inner_sn_i());
			params1.put("warehouse_model_sn", sn);
			int index = mapper.SelectWarehouseSnIndex(params1);
			sn += calculateWarehouseSn(index + 1);
		}
		params.put("warehouse_model_sn_i", sn);
		params.put("type_i", form.getType_i());
		if(form.getProportion_i() == null) {
			form.setProportion_i(new BigDecimal(0));
		}
		params.put("proportion_i", form.getProportion_i());
		params.put("aisle_sn_i", form.getAisle_sn_i());
		params.put("phone_i", form.getPhone_i());
		params.put("barcode_i", form.getBarcode_i());
		params.put("address_i", form.getAddress_i());
		params.put("description_i", form.getDescription_i());
		if(StringUtils.isNotEmpty(form.getWarehouse_model_sn_i())
				&& existWarehouseSn(oid, form.getParent_warehouse_model_inner_sn_i(), null, form.getWarehouse_model_sn_i())) {
			throw new DuplicateException(MessageUtils.message("db.update.fail","仓库建模",form.getName_i() + "的编号" + form.getWarehouse_model_sn_i() + "已经存在!"));
		}
		mapper.insertWarehouseModel(params);
		WarehouseModel model = new WarehouseModel();
		String departments = form.getDepartments();
		if(StringUtils.isNotEmpty(departments)) {
			String[] depts = departments.split(",");
			for (String dept : depts) {
				WarehouseRelDept relDept = new WarehouseRelDept();
				relDept.setOrg_id(oid);
				relDept.setWarehouse_model_inner_sn(wid);
				relDept.setDepartment_inner_sn(Long.valueOf(dept));
				mapper.insertWarehouseRelDept(relDept);
			}
		}
		List<WareHouseRelEmp> employees = form.getEmployees();
		checkWarehouseManager(form.getType_i().intValue(), employees);
		if(StringUtils.isNotEmpty(employees)) {
			for (WareHouseRelEmp emp : employees) {
				WarehouseRelEmployee relEmp = new WarehouseRelEmployee();
				relEmp.setOrg_id(oid);
				relEmp.setWarehouse_model_inner_sn(wid);
				relEmp.setEmployee_inner_sn(emp.getEmployee_inner_sn());
				relEmp.setType(emp.getType());
				mapper.insertWarehouseRelEmployee(relEmp);
			}
		}
		
		model.setOrg_id(oid);
		model.setWarehouse_model_inner_sn(wid);
		WarehouseModel info = mapper.selectWarehouseModelInfo(model);
		if(info != null) {
			return info;
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","仓库"));
		}
	}

	private String calculateWarehouseSn(int index) {
		DecimalFormat df = new DecimalFormat("000");//默认三位数,补零
		return df.format(index);
	}

	private void checkWarehouseManager(int warehouseType, List<WareHouseRelEmp> employees) {
		//仓库、楼层、区域至少必须有一个仓库管理员 2021-01-28
		if(warehouseType == 0 || warehouseType == 1 || warehouseType == 2) {
			boolean noHasManager = true;
			for (WareHouseRelEmp emp : employees) {
				int type = emp.getType().intValue();
				if(type == 0) {
					noHasManager = false;
					break;
				}
			}
			if(noHasManager) {
				throw new DBException("仓库管理员不能为空!");
			}
		}
	}
	
	@Override
	@Transactional
	public int updateWarehouseMode(Long oid, Long wid, EditWareHouseForm form) {
		WareHouseMapper mapper = getWarehouseMapper();
		int result = 1;
		try {
			WarehouseModel model = new WarehouseModel();
			model.setOrg_id(oid);
			model.setWarehouse_model_inner_sn(wid);
			WarehouseModel tmp = selectWarehouseInfoById(oid, wid);
			if(form.hasInfoUpdate()) {
				Long pid = form.getParent_warehouse_model_inner_sn();
				Long oldPid = form.getParent_warehouse_model_inner_sn_old();
				String sn = StringUtils.isNotEmpty(form.getWarehouse_model_sn())?form.getWarehouse_model_sn():tmp.getWarehouse_model_sn();
				if(StringUtils.isNull(pid) && StringUtils.isNull(oldPid)) {//没有修改父节点
					pid = tmp.getParent_warehouse_model_inner_sn();
				}else {//修改了父节点
					pid = Utils.equals(form.getParent_warehouse_model_inner_sn(), -1L)?null:new Long(form.getParent_warehouse_model_inner_sn().longValue());
				}
				String name = StringUtils.isEmpty(form.getName())?tmp.getName():form.getName();
				if(StringUtils.isEmpty(sn)) {
					sn = PinyinUtils.getPy(name);
					Map<String,Object> params1 = new HashMap<>();
					params1.put("org_id", oid);
					params1.put("parent_warehouse_model_inner_sn", pid);
					params1.put("warehouse_model_inner_sn", wid);
					params1.put("warehouse_model_sn", sn);
					int index = mapper.SelectWarehouseSnIndex(params1);
					sn += calculateWarehouseSn(index + 1);
				}
				if(existWarehouseSn(oid, pid, wid, sn)) {
					throw new RuntimeException(name + "的编号" + sn + "已经存在!");
				}
				BeanUtils.copyProperties(form, model);
				model.setWarehouse_model_sn(sn);
				result = mapper.updateWarehouseModel(model);
			}
			if(form.hasDeptUpdate()) {
				mapper.deleteWarehouseRelDept(model);
				String departments = form.getDepartments();
				if(StringUtils.isNotEmpty(departments)) {
					String[] depts = departments.split(",");
					for (String dept : depts) {
						WarehouseRelDept relDept = new WarehouseRelDept();
						relDept.setOrg_id(oid);
						relDept.setWarehouse_model_inner_sn(wid);
						relDept.setDepartment_inner_sn(Long.valueOf(dept));
						result = result & mapper.insertWarehouseRelDept(relDept);
					}
				}
			}
			if(form.hasEmpUpdate()) {
				mapper.deleteWarehouseRelEmployee(model);
				List<WareHouseRelEmp> employees = form.getEmployees();
				checkWarehouseManager(StringUtils.isNull(form.getType())?tmp.getType().intValue():form.getType().intValue(), employees);
				if(StringUtils.isNotEmpty(employees)) {
					for (WareHouseRelEmp emp : employees) {
						WarehouseRelEmployee relEmp = new WarehouseRelEmployee();
						relEmp.setOrg_id(oid);
						relEmp.setWarehouse_model_inner_sn(wid);
						relEmp.setEmployee_inner_sn(emp.getEmployee_inner_sn());
						relEmp.setType(emp.getType());
						result = result & mapper.insertWarehouseRelEmployee(relEmp);
					}
				}
			}
		} catch(Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException(MessageUtils.message("db.update.fail","仓库建模","仓库名称或者编号重复"));
			}else {
				throw new DBException(MessageUtils.message("db.update.fail","仓库建模",e.getMessage()));
			}
		}
		return result;
	}

	@Override
	@Transactional
	public int deleteWarehouseMode(Long oid, Long wid) {
		WareHouseMapper mapper = getWarehouseMapper();
		try {
			WarehouseModel model = new WarehouseModel();
			model.setOrg_id(oid);
			model.setWarehouse_model_inner_sn(wid);
			Long warehouseID = getTopParentId(mapper, oid, wid);
			Map<String, Object> params = new HashMap<>();
			params.put("org_id", oid);
			params.put("warehouse_inner_sn", warehouseID);
			BigDecimal total = mapper.checkWarehouseQuantity(params);
			if(StringUtils.isNotNull(total) && total.compareTo(BigDecimal.ZERO) == 1) {
				throw new ValidateException("仓库中还有存货，不能删除！");
			}
			mapper.deleteWarehouseRelDept(model);
			return mapper.trueDeleteWarehouseModel(model);
		}catch (DataIntegrityViolationException e) {
			throw new ValidateException("仓库已经被使用，不能删除！");
		}
	}

	@Override
	public List<WarehouseModel> selectTopLevelWarehouseList(Long oid, Long status) {
		WareHouseMapper mapper = getWarehouseMapper();
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("status", status);
		List<WarehouseModel> list = mapper.selectTopLevelWarehouseList(params);
		setWarehouseRelEmpList(mapper, list);
		return list;
	}
	
	@Override
	public List<WarehouseModel> selectTopLevelWarehouseList(Long oid, SystemType systemType, Long status) {
		WareHouseMapper mapper = getWarehouseMapper(systemType);
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("status", status);
		List<WarehouseModel> list = mapper.selectTopLevelWarehouseList(params);
		setWarehouseRelEmpList(mapper, list);
		return list;
	}

	@Override
	public List<WarehouseModel> selectWarehouseListByPid(Long oid, Long wpid) {
		WareHouseMapper mapper = getWarehouseMapper();
		WarehouseModel model = new WarehouseModel();
		model.setOrg_id(oid);
		model.setParent_warehouse_model_inner_sn(wpid);
		List<WarehouseModel> list = mapper.selectWarehouseListByPid(model);
		setWarehouseRelEmpList(mapper, list);
		return list;
	}

	@Override
	public WarehouseModel selectWarehouseInfoById(Long oid, Long wid) {
		WareHouseMapper mapper = getWarehouseMapper();
		WarehouseModel model = new WarehouseModel();
		model.setOrg_id(oid);
		model.setWarehouse_model_inner_sn(wid);
		WarehouseModel info = mapper.selectWarehouseModelInfo(model);
		if(StringUtils.isNull(info)) {
			return null;
		}
		setParents(mapper, info);
		setChildren(mapper, info);
		return info;
	}

	@Override
	public List<WarehouseModel> selectAllWarehouseTree(Long oid, Long status) {
		WareHouseMapper mapper = getWarehouseMapper();
		List<WarehouseModel> list = selectTopLevelWarehouseList(oid, status);
		for (WarehouseModel warehouseModel : list) {
			setChildren(mapper, warehouseModel);
		}
		return list;
	}
	
	private void setChildren(WareHouseMapper mapper, WarehouseModel model) {
		if(StringUtils.isNull(model)) {
			return;
		}
		List<WarehouseModel> children = selectWarehouseListByPid(model.getOrg_id(), model.getWarehouse_model_inner_sn());
		for (WarehouseModel child : children) {
			setChildren(mapper, child);
		}
		setWarehouseRelEmpList(mapper, model);
		model.setChildren(children);
	}
	
	private void setParents(WareHouseMapper mapper, WarehouseModel model) {
		if(StringUtils.isNull(model)) {
			return;
		}
		setWarehouseRelEmpList(mapper, model);
		if(model.getParent_warehouse_model_inner_sn() != null) {
			WarehouseModel params = new WarehouseModel();
			params.setOrg_id(model.getOrg_id());
			params.setWarehouse_model_inner_sn(model.getParent_warehouse_model_inner_sn());
			WarehouseModel parent = mapper.selectWarehouseModelInfo(params);
			setParents(mapper,parent);
			model.setParents(parent);
		}
	}
	
	private Long getTopParentId(WareHouseMapper mapper, Long oid, Long wid) {
		WarehouseModel params = new WarehouseModel();
		params.setOrg_id(oid);
		params.setWarehouse_model_inner_sn(wid);
		WarehouseModel model = mapper.selectWarehouseModelInfo(params);
		if(StringUtils.isNull(model.getParent_warehouse_model_inner_sn())){
			return wid;
		}else {
			return getTopParentId(mapper, oid, model.getParent_warehouse_model_inner_sn());
		}
	}
	
	private void setWarehouseRelEmpList(WareHouseMapper mapper, List<WarehouseModel> list) {
		if(StringUtils.isEmpty(list)) {
			return;
		}
		for (WarehouseModel model : list) {
			setWarehouseRelEmpList(mapper, model);
		}
	}
	
	private void setWarehouseRelEmpList(WareHouseMapper mapper, WarehouseModel model) {
		if(StringUtils.isNull(model)) {
			return;
		}
		WarehouseRelEmployee emp = new WarehouseRelEmployee();
		emp.setOrg_id(model.getOrg_id());
		emp.setWarehouse_model_inner_sn(model.getWarehouse_model_inner_sn());
		List<WarehouseRelEmployee> emps = mapper.selectWarehouseRelEmpList(emp);
		if(StringUtils.isEmpty(emps)) {
			model.setEmployees(new ArrayList<WarehouseRelEmployee>());
		}else {
			model.setEmployees(emps);
		}
	}

	@Override
	public List<WarehouseType> selectWarehouseTypeList(Long oid, Long status) {
		WareHouseMapper mapper = getWarehouseMapper();
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("status", status);
		return mapper.selectWarehouseTypeList(params);
	}

	@Override
	@Transactional
	public WarehouseType insertWarehouseType(AddWarehouseTypeForm form) {
		WareHouseMapper mapper = getWarehouseMapper();
		Map<String, Object> params = new HashMap<>();
		params.put("org_id_i", form.getOrg_id());
		params.put("name_i", form.getName());
		mapper.insertWarehouseType(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long wtid = Long.valueOf(params.get("type_inner_sn_o").toString());
			WarehouseType tmp = new WarehouseType();
			tmp.setOrg_id(form.getOrg_id());
			tmp.setType_inner_sn(wtid);
			WarehouseType info = mapper.selectWarehouseTypeInfo(tmp);
			if(info != null) {
				return info;
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","仓库类型"));
			}
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","仓库类型",params.get("message_string_o")));
		}
	}

	@Override
	@Transactional
	public int updateWarehouseType(EditWarehouseTypeForm form) {
		WareHouseMapper mapper = getWarehouseMapper();
		WarehouseType type = new WarehouseType();
		type.setOrg_id(form.getOrg_id());
		type.setType_inner_sn(form.getType_inner_sn());
		type.setName(form.getName());
		
		return mapper.updateWarehouseType(type);
	}

	@Override
	@Transactional
	public int deleteWarehouseType(Long oid, Long wtid) {
		WareHouseMapper mapper = getWarehouseMapper();
		WarehouseType type = new WarehouseType();
		type.setOrg_id(oid);
		type.setType_inner_sn(wtid);
		mapper.deleteWarehouseType(type);
		return 1;
	}

	@Override
	public int checkWarehouseSn(Long oid, WarehouseCheckForm form) {
		if(existWarehouseSn(oid, form.getParent_warehouse_model_inner_sn(), form.getWarehouse_model_inner_sn(), form.getWarehouse_model_sn())) {
			return 0;
		}
		return 1;
	}
	
	private boolean existWarehouseSn(Long oid, Long pid, Long mid, String sn) {
		WareHouseMapper mapper = getWarehouseMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("parent_warehouse_model_inner_sn", pid);
		params.put("warehouse_model_inner_sn", mid);
		params.put("warehouse_model_sn", sn);
		if(StringUtils.isNotEmpty(mapper.checkWarehouseSn(params))) {
			return true;
		}
		return false;
	}

	@Override
	public WarehouseModel scan(Long oid, WarehouseScanForm form) {
		WareHouseMapper mapper = getWarehouseMapper();
		if(mapper == null) {
			throw new DBException("机构不支持该功能!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("barcode", form.getBarcode());
		WarehouseModel model = mapper.selectWarehouseModeInfoByBarcode(params);
		if(model == null) {
			throw new DBException("错误的储位条码!");
		}
		setParents(mapper,model);
		if(checkWarehouse(model, form.getWarehouse_model_inner_sn())) {
			return model;
		}else {
			throw new DBException("储位不在当前仓库中!");
		}
	}
	
	//检查pid这个仓库中是否有model这个储位
	private boolean checkWarehouse(WarehouseModel model, Long pid) {
		if(pid == null) return true;
		
		if(model.getParent_warehouse_model_inner_sn() != null) {
			return checkWarehouse(model.getParents(), pid);
		}
		return model.getWarehouse_model_inner_sn() == pid;
	}
}
