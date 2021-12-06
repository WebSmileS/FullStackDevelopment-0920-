package com.yrt.project.api.business.organization.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.organization.service.IOrgService;
import com.yrt.project.api.business.organization.vo.AddOrgDeptForm;
import com.yrt.project.api.business.organization.vo.AddOrgEmployeeForm;
import com.yrt.project.api.business.organization.vo.EditOrgDeptForm;
import com.yrt.project.api.business.organization.vo.EditOrgEmployeeForm;
import com.yrt.project.api.common.vo.SearchForm;
import com.yrt.project.modular.organization.domain.OrgDept;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.domain.OrgEmployeeRelDept;
import com.yrt.project.modular.organization.domain.User;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.OrgMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.organization.service.IUserService;

@Service
public class OrgServiceImpl implements IOrgService {

	@Autowired
	private HospitalMapper hMapper;
	@Autowired
	private VendorMapper vMapper;
	@Autowired
	private DealerMapper dMapper;
	@Autowired
	IUserService userService;
//	@Autowired
//	UserRoleService userRoleService;
	
	private OrgMapper getMapper(SystemType curSytemType) {
		switch (curSytemType) {
		case DEALER:
			return dMapper;
		case HOSPITAL:
			return hMapper;
		case VENDOR:
			return vMapper;
		default:
			return null;
		}
	}
	
	//机构部门管理
	@Override
	@Transactional
	public OrgDept insertOrgDept(SystemType systemType, Long id, AddOrgDeptForm form) {
		OrgMapper mapper = getMapper(systemType);
		Map<String, Object> params = new HashMap<>();
		params.put("parent_department_inner_sn_i", form.getParent_department_inner_sn());
		params.put("director_inner_sn_i", form.getDirector_inner_sn());
		params.put("name_i", form.getName());
		params.put("department_sn_i", form.getDepartment_sn());
		params.put("phone_i", form.getPhone());
		params.put("description_i", form.getDescription());
		params.put("org_id_i", id);
		mapper.insertOrgDepartment(params);
		
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			OrgDept dept = new OrgDept();
			dept.setId(id);
			dept.setDepartment_inner_sn(Long.valueOf(params.get("department_inner_sn_o").toString()));
			return mapper.selectOrgDeptInfoById(dept);
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","部门",params.get("message_string_o")));
		}
	}

	@Override
	@Transactional
	public int updateOrgDept(SystemType systemType, Long id, Long did, EditOrgDeptForm form) {
		OrgMapper mapper = getMapper(systemType);
		OrgDept dept = new OrgDept();
		if(form.hasUpdate()) {
			Map<String, Object> params = form.getDepts();
			try {
				org.apache.commons.beanutils.BeanUtils.populate(dept, params);
				dept.setId(id);
				dept.setDepartment_inner_sn(did);
				return mapper.updateOrgDept(dept);
			} catch (IllegalAccessException e) {
				throw new BusinessException(e.getMessage());
			} catch (InvocationTargetException e) {
				throw new BusinessException(e.getMessage());
			}
		}
		return 1;
	}

	@Override
	@Transactional
	public int deleteOrgDept(SystemType systemType, Long id, Long did) {
		try {
			OrgMapper mapper = getMapper(systemType);
			if(existLowerLevelDept(mapper, systemType, id, did)) {
				throw new ValidateException("当前部门还有下级部门，不能被删除!");
			}
			if(existWarehouse(mapper, id, did)) {
				throw new ValidateException("当前部门被仓库模型使用，不能被删除!");
			}
			if(existEmployee(mapper, id, did)) {
				throw new ValidateException("当前部门中还存在员工，不能被删除!");
			}
			if(existBusiness(mapper, id, did)) {
				throw new ValidateException("部门已经被业务单据使用，不能删除！");
			}
			OrgDept dept = new OrgDept();
			dept.setId(id);
			dept.setDepartment_inner_sn(did);
			return mapper.trueDeleteOrgDept(dept);
		}catch (DataIntegrityViolationException e) {
			throw new ValidateException("部门已经被使用，不能删除！");
		}
	}
	
	private boolean existWarehouse(OrgMapper mapper, Long id, Long did) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("department_inner_sn", did);
		int count = mapper.existWarehouseByDept(params);
		if(count >0) {
			return true;
		}
		return false;
	}
	
	private boolean existBusiness(OrgMapper mapper, Long id, Long did) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("department_inner_sn", did);
		int count = mapper.existBusinessByDept(params);
		if(count >0) {
			return true;
		}
		return false;
	}

	private boolean existEmployee(OrgMapper mapper, Long id, Long did) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("department_inner_sn", did);
		int count = mapper.existEmployeeInDept(params);
		if(count >0) {
			return true;
		}
		return false;
	}

	private boolean existLowerLevelDept(OrgMapper mapper, SystemType systemType, Long id, Long did) {
		OrgDept params = new OrgDept();
		params.setId(id);
		params.setDepartment_inner_sn(did);
		OrgDept dept = mapper.selectOrgDeptInfoById(params);
		if(dept.getIs_leaf() == 1) {
			return false;
		} else {
			getDeptChildren(dept, systemType, id);
			if(dept.getChildren() != null && dept.getChildren().size() == 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public OrgDept selectOrgDeptInfoById(SystemType systemType, Long id, Long did) {
		OrgMapper mapper = getMapper(systemType);
		OrgDept params = new OrgDept();
		params.setId(id);
		params.setDepartment_inner_sn(did);
		return mapper.selectOrgDeptInfoById(params);
	}

	@Override
	public List<OrgDept> selectTopLevelOrgDeptListByOrgId(SystemType systemType, Long id) {
		OrgMapper mapper = getMapper(systemType);
		OrgDept dept = new OrgDept();
		dept.setId(id);
		return mapper.selectTopLevelOrgDeptListByOrgId(dept);
	}

	@Override
	public List<OrgDept> selectOrgDeptListByPid(SystemType systemType, Long id, Long pid) {
		OrgMapper mapper = getMapper(systemType);
		OrgDept dept = new OrgDept();
		dept.setId(id);
		dept.setParent_department_inner_sn(pid);
		return mapper.selectOrgDeptListByPid(dept);
	}

	@Override
	public List<OrgDept> selectTreeOrgDeptListByOrgId(SystemType systemType, Long id) {
		List<OrgDept> depts = selectTopLevelOrgDeptListByOrgId(systemType, id);
		for (OrgDept orgDept : depts) {
			getDeptChildren(orgDept, systemType, id);
		}
		return depts;
	}
	
	private void getDeptChildren(OrgDept dept, SystemType systemType, Long oid) {
		Long pid = dept.getDepartment_inner_sn();
		List<OrgDept> children = selectOrgDeptListByPid(systemType, oid, pid);
		for (OrgDept child : children) {
			getDeptChildren(child, systemType, oid);
		}
		dept.setChildren(children);
	}

	@Override
	public void selectOrgDeptParentsById(OrgDept dept, SystemType systemType, Long id, Long pid) {
		if(pid != null) {
			OrgDept parent = selectOrgDeptInfoById(systemType, id, pid);
			dept.setParent(parent);
			selectOrgDeptParentsById(parent, systemType, id, parent.getParent_department_inner_sn());
		}
	}
	
	@Override
	public List<Map<String,Object>> selectRelDeptList(SystemType systemType, Long id, Long curUserId) {
		OrgMapper mapper = getMapper(systemType);
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", id);
		params.put("uid", curUserId);
		return mapper.selectRelDeptList(params);
	}

	//机构人员管理
	@Override
	@Transactional
	public OrgEmployee insertOrgEmployee(SystemType systemType, Long id, AddOrgEmployeeForm form) {
		OrgMapper mapper = getMapper(systemType);
		Map<String, Object> params = new HashMap<>();
		params.put("parent_employee_inner_sn_i", form.getParent_employee_inner_sn());
		params.put("sex_i", form.getSex());
		params.put("status_i", form.getStatus());
		params.put("personal_certificate_type_i", form.getPersonal_certificate_type());
		params.put("degree_i", form.getDegree());
		params.put("marriage_status_i", form.getMarriage_status());
		params.put("birthday_i", form.getBirthday());
		params.put("entry_date_i", form.getEntry_date());
		params.put("departure_date_i", form.getDeparture_date());
		params.put("name_i", form.getName());
		params.put("employee_sn_i", form.getEmployee_sn());
		params.put("nation_i", form.getNation());
		params.put("position_i", form.getPosition());
		params.put("department_inner_sns_i", form.getDepartment_inner_sn());
		params.put("personal_certificate_sn_i", form.getPersonal_certificate_sn());
		params.put("phone_i", form.getPhone());
		params.put("email_i", form.getEmail());
		params.put("qq_i", form.getQq());
		params.put("description_i", form.getDescription());
		params.put("photo_url_i", form.getPhoto_url());
		params.put("login_account_status_i", 0);
		
		if (StringUtils.isNotNull(form.getLogin_account_status()) &&
				 form.getLogin_account_status() > 0) {
			long user_inner_sn = userService.CreateUser(form.getName(), form.getPhone(), systemType, id);
			params.put("user_inner_sn_i", user_inner_sn);
			params.put("login_account_status_i", 1); // 客户已经生成登录账号
		}else {
			params.put("user_inner_sn_i", null);
		}
		
		params.put("org_id_i", id);
		mapper.insertOrgEmployee(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			return selectOrgEmployeeInfoById(systemType, id, Long.valueOf(params.get("employee_inner_sn_o").toString()));
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","人员",params.get("message_string_o")));
		}
	}

	@Override
	@Transactional
	public int updateOrgEmployee(SystemType systemType, Long id, Long eid, EditOrgEmployeeForm form) {
		OrgMapper mapper = getMapper(systemType);
		int result = 1;
		boolean isUpdate = false;
		Map<String, Object> emps = form.getEmployees();
		//Map<String, Object> deptsMap = form.getDepts();
		//用于验证是否还进行角色删除操作
//		boolean	userRoleBool = false;
		OrgEmployee oldEmp = selectOrgEmployeeInfoById(systemType, id, eid);
		String employeeName = StringUtils.isEmpty(form.getName())?oldEmp.getName():form.getName();
		if (oldEmp == null) {
			throw new ValidateException(MessageUtils.message("user.not.found", "用户不存在"));
		}
		
		String phone = "";
		if (StringUtils.isNotNull(form.getPhone())) {
			phone = form.getPhone();
		}
		
//			没有改动是否登录，但是需要考虑是否变更了电话号码和角色
		if (StringUtils.isNull(form.getLogin_account_status()) && oldEmp.getLogin_account_status() == 1) {
//				有电话更新, 与原号码校验，是否有变化
			if (phone.length() > 0 && !phone.equals(oldEmp.getPhone())) {
//				PatternUtils.validatePhone(phone, "user.mobile.phone.number.not.valid");
//					更新user的电话
//					检验电话是否存在
				int res = userService.UpdateUserLoginName(oldEmp.getUser_inner_sn(), phone);
				if (res == 0) {
					throw new ValidateException(MessageUtils.message("db.update.fail", "系统用户"));
				}
			}
		}
		
//			打开登录系统
		if (StringUtils.isNotNull(form.getLogin_account_status()) && form.getLogin_account_status() == 1) {
//				新建用户可以直接增加user login账号	，但需要区分是否已有
//				检查电话不能为空，需要用电话作为用户的登录名				
			if (phone.length() <= 0) {
//					电话为空，检查是否原有电话
				if (oldEmp.getPhone().length() <= 0) {
					throw new ValidateException(MessageUtils.message("param.not.null", "手机号码"));
				}
				phone = oldEmp.getPhone();
			}
			//先看是否存在电话 避免直接抛出异常
			User user = userService.selectUserByLoginName(phone);
			Map<String, Object> UserRoleparams = new HashMap<String, Object>();	
			if(user == null) {
				Long user_inner_sn = userService.CreateUser(employeeName, phone, systemType, id);
				form.setUser_inner_sn(user_inner_sn);
				emps.put("user_inner_sn", user_inner_sn);
				UserRoleparams.put("user_inner_sn", user_inner_sn);
			}else {
//				form.setUser_inner_sn(user.getId());
//				emps.put("user_inner_sn", user.getId());
//				UserRoleparams.put("user_inner_sn", user.getId());
				throw new ValidateException(MessageUtils.message("user.mobile.phone.exists", "请先注销该号码"));
			}
			//查看userId有没有值 有就是改
			/*
			 * UserRole userRole =
			 * userRoleService.selectUserRoleById((long)UserRoleparams.get("user_inner_sn"))
			 * ; int resultRole = 0; if(userRole != null) {
			 * UserRoleparams.put("role_inner_sn", form.getRole_inner_sn()); resultRole =
			 * resultRole & userRoleService.updateUserRole(UserRoleparams); userRoleBool =
			 * true; }else { UserRoleparams.put("role_inner_sn", form.getRole_inner_sn());
			 * resultRole = resultRole & userRoleService.insertUserRole(UserRoleparams);
			 * userRoleBool = true; }
			 */
		}
		
//			关闭登录系统
		Long user_inner_sn = Long.valueOf(-1);
		if (StringUtils.isNotNull(oldEmp.getUser_inner_sn())) {
			user_inner_sn = oldEmp.getUser_inner_sn();
		}
		boolean flagDeleteUser = false;
		if (StringUtils.isNotNull(form.getLogin_account_status()) && form.getLogin_account_status() == 0) {
//				调用系统账户处理服务
			if (user_inner_sn != -1) {
				User u = userService.selectUserById(user_inner_sn);
//					关闭登录账户
				if (u != null) {
					if (u.getSystem_type().equals(systemType) && u.getOrg_id() == id) {
//							确实为对应机构用户
						flagDeleteUser = true;
						user_inner_sn = u.getId();
						
						//关闭登录系统 则删除对应roleId
						/*
						 * Map<String, Object> UserRoleparams = new HashMap<String, Object>();
						 * UserRoleparams.put("user_inner_sn", user_inner_sn); int resultRole = 0;
						 * resultRole = resultRole & userRoleService.deleteUserRole(UserRoleparams);
						 * userRoleBool = true;
						 */
					}
				}
			}
		}
		
		if(form.isUpdate()) {
			emps.put("id", id);
			emps.put("employee_inner_sn", eid);
			result = result & mapper.updateOrgEmployee(emps);
		}
		if(result > 0) {
			/*
			 * if(!userRoleBool && form.isUpdateRole()) {
			 * if(StringUtils.isNotNull(form.getLogin_account_status()) &&
			 * form.getLogin_account_status() == 0) { //关闭登录系统 则删除对应roleId Map<String,
			 * Object> UserRoleparams = new HashMap<String, Object>();
			 * UserRoleparams.put("user_inner_sn", user_inner_sn); int resultRole = 0;
			 * resultRole = resultRole & userRoleService.deleteUserRole(UserRoleparams);
			 * userRoleBool = true; }else { //查看userId有没有值 有就是改 Map<String, Object>
			 * UserRoleparams = new HashMap<String, Object>();
			 * UserRoleparams.put("user_inner_sn", oldEmp.getUser_inner_sn()); UserRole
			 * userRole =
			 * userRoleService.selectUserRoleById((long)UserRoleparams.get("user_inner_sn"))
			 * ; int resultRole = 0; if(userRole != null) {
			 * UserRoleparams.put("role_inner_sn", form.getRole_inner_sn()); resultRole =
			 * resultRole & userRoleService.updateUserRole(UserRoleparams);
			 * 
			 * }else { UserRoleparams.put("role_inner_sn", form.getRole_inner_sn());
			 * resultRole = resultRole & userRoleService.insertUserRole(UserRoleparams); } }
			 * }
			 */
//				删除用户
			if (result > 0 && flagDeleteUser) {
				userService.deleteUserUnscoped(user_inner_sn);
			}
				
			if(form.isUpdateDept()) {
				OrgEmployee emp = new OrgEmployee();
				emp.setId(id);
				emp.setEmployee_inner_sn(eid);
				emp.setDepartment_inner_sn(form.getDepartment_inner_sn().trim());
				result = mapper.deleteOrgEmployeeRelDept(emp);
				String deptString = emp.getDepartment_inner_sn();
				if(StringUtils.isNotEmpty(deptString)) {
					String[] depts = deptString.split(",");
					for (String dept : depts) {
						OrgEmployeeRelDept empRelDept = new OrgEmployeeRelDept();
						empRelDept.setDepartment_inner_sn(Long.valueOf(dept));
						empRelDept.setId(id);
						empRelDept.setEmployee_inner_sn(eid);
						switch (systemType) {
						case HOSPITAL:
							result = hMapper.insertOrgEmployeeRelDept(empRelDept);
							break;
						case DEALER:
							result = dMapper.insertOrgEmployeeRelDept(empRelDept);
							break;
						case VENDOR:
							result = vMapper.insertOrgEmployeeRelDept(empRelDept);
							break;
						default:
						}
					}
				}
			}
			isUpdate = true;
		}
		return isUpdate ? 1 : 0;
	}

	@Override
	@Transactional
	public int deleteOrgEmployee(SystemType systemType, Long id, Long eid) {
		OrgMapper mapper = getMapper(systemType);
		OrgEmployee oldEmp = new OrgEmployee();
		OrgEmployee emp = new OrgEmployee();
		emp.setId(id);
		emp.setEmployee_inner_sn(eid);
		int result = 0;
		oldEmp = selectOrgEmployeeInfoById(systemType, id, eid);
		result = mapper.deleteOrgEmployee(emp);
		
		if (result > 0
			&& oldEmp != null
			&& oldEmp.getLogin_account_status() == 1 
			&& StringUtils.isNotNull(oldEmp.getUser_inner_sn())) {
			//删除对应角色信息
			/*
			 * Map<String, Object> params = new HashMap<String, Object>();
			 * params.put("user_inner_sn", oldEmp.getUser_inner_sn()); int resultRole = 0;
			 * resultRole = resultRole & userRoleService.deleteUserRole(params);
			 */
			
			result = userService.deleteUserUnscoped(oldEmp.getUser_inner_sn());
		}
		return result;
	}

	//详情
	@Override
	public OrgEmployee selectOrgEmployeeInfoById(SystemType systemType, Long id, Long eid) {
		OrgMapper mapper = getMapper(systemType);
		Map<String, Object> emp = new HashMap<>();
		emp.put("id", id);
		emp.put("employee_inner_sn", eid);
		OrgEmployee orgEmployee = null;
//		UserRole userRole = null;
		orgEmployee = mapper.selectOrgEmployeeInfoById(emp);
//		if(userRole !=null)
//			orgEmployee.setRole_inner_sn(userRole.getRole_inner_sn());
		return orgEmployee;
	}

	//查List
	@Override
	public List<OrgEmployee> selectOrgEmployeeListByOrgId(SystemType systemType, Long id) {
		OrgMapper mapper = getMapper(systemType);
		OrgEmployee emp = new OrgEmployee();
		emp.setId(id);
		List<OrgEmployee> orgEmployeeList = new ArrayList<OrgEmployee>();
//		UserRole userRole = null;
		orgEmployeeList = mapper.selectOrgEmployeeListById(emp);
		//保证list中也能获取到角色
		/*
		 * for(OrgEmployee o : orgEmployeeList) { userRole =
		 * userRoleService.selectUserRoleById(o.getUser_inner_sn()); if(userRole !=null)
		 * { o.setRole_inner_sn(userRole.getRole_inner_sn()); }
		 * 
		 * }
		 */
		
		return orgEmployeeList;
	}
	
	@Override
	public List<OrgEmployee> selectOrgEmployeeListByOrgId(SystemType systemType, Long id, SearchForm form) {
		OrgMapper mapper = getMapper(systemType);
		OrgEmployee emp = new OrgEmployee();
		emp.setId(id);
		emp.setName(form.getName());
		List<OrgEmployee> orgEmployeeList = new ArrayList<OrgEmployee>();
//		UserRole userRole = null;
		orgEmployeeList =  mapper.selectOrgEmployeeListById(emp);
		//保证list中也能获取到角色
		/*
		 * for(OrgEmployee o : orgEmployeeList) { userRole =
		 * userRoleService.selectUserRoleById(o.getUser_inner_sn()); if(userRole !=null)
		 * { o.setRole_inner_sn(userRole.getRole_inner_sn()); }
		 * 
		 * }
		 */
		return orgEmployeeList;
	}

	@Override
	public List<OrgEmployee> selectTreeOrgEmpListByOrgId(SystemType systemType, Long id) {
		List<OrgEmployee> emps = selectTopLevelOrgEmpListByOrgId(systemType, id);
		for (OrgEmployee orgEmp : emps) {
			getEmpChildren(orgEmp, systemType, id);
		}
		return emps;
	}

	private void getEmpChildren(OrgEmployee emp, SystemType systemType, Long oid) {
		Long pid = emp.getEmployee_inner_sn();
		List<OrgEmployee> children = selectOrgEmployeeListByPid(systemType, oid, pid);
		for (OrgEmployee child : children) {
			getEmpChildren(child, systemType, oid);
		}
		emp.setChildren(children);
	}
	
	private List<OrgEmployee> selectOrgEmployeeListByPid(SystemType systemType, Long id, Long pid) {
		OrgEmployee emp = new OrgEmployee();
		emp.setId(id);
		emp.setParent_employee_inner_sn(pid);
		switch (systemType) {
		case HOSPITAL:
			return hMapper.selectOrgEmployeeListByPid(emp);
		case DEALER:
			return dMapper.selectOrgEmployeeListByPid(emp);
		case VENDOR:
			return vMapper.selectOrgEmployeeListByPid(emp);
		default:
		}
		return new ArrayList<OrgEmployee>();
	}
	
	private List<OrgEmployee> selectTopLevelOrgEmpListByOrgId(SystemType systemType, Long id) {
		OrgMapper mapper = getMapper(systemType);
		OrgEmployee emp = new OrgEmployee();
		emp.setId(id);
		return mapper.selectTopLevelOrgEmpListByOrgId(emp);
	}

	@Override
	public OrgEmployee getEmployeeInfoByUser(SystemType systemType, long userId) {
		OrgMapper mapper = getMapper(systemType);
		return mapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
	}

}
