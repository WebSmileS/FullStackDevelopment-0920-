package com.yrt.project.api.platform.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.common.vo.SearchForm;
import com.yrt.project.api.platform.service.IPlatformDeptService;
import com.yrt.project.api.platform.vo.organization.AddPlatformDeptForm;
import com.yrt.project.api.platform.vo.organization.AddPlatformEmployeeForm;
import com.yrt.project.api.platform.vo.organization.EditPlatformDeptForm;
import com.yrt.project.api.platform.vo.organization.EditPlatformEmployeeForm;
import com.yrt.project.modular.organization.domain.PlatformDept;
import com.yrt.project.modular.organization.domain.PlatformEmployee;
import com.yrt.project.modular.organization.domain.User;
import com.yrt.project.modular.organization.mapper.PlatformDeptMapper;
import com.yrt.project.modular.organization.service.IUserService;

@Service
public class PlatformDeptServiceImpl implements IPlatformDeptService {

	@Autowired
	PlatformDeptMapper mapper;
	@Autowired
	IUserService userService;
//	@Autowired
//	UserRoleService userRoleService;

	@Override
	public PlatformDept selectPlatformDeptInfo(Long id) {
		PlatformDept platformDept = mapper.selectPlatformDeptInfo(id);
		this.setPlatformDeptParent(platformDept);
		return platformDept;
	}

	@Override
	public List<PlatformDept> selectPlatformDeptChildrenList(long id) {
		
		return mapper.selectPlatformDeptChildrenList(id);
	}

	@Override
	@Transactional
	public long insertPlatformDept(AddPlatformDeptForm platformDept) {
		long dept_id = 0;
		if (mapper.insertPlatformDept(platformDept) > 0) {
			dept_id = platformDept.getDepartment_inner_sn();
		}
		return dept_id;
	}

	@Override
	@Transactional
	public int updatePlatformDept(EditPlatformDeptForm platformDept) {
		boolean isUpdate = false;
		if (mapper.updatePlatformDept(platformDept) > 0) {
			isUpdate = true;
		}
		return isUpdate ? 1 : 0;
	}

	@Override
	@Transactional
	public int deletePlatformDept(Long id) {
		try {
			PlatformDept dept = mapper.selectPlatformDeptInfo(id);
			if(dept.getIs_leaf() == 1) {
				return mapper.trueDeletePlatformDept(id);
			}else {
				setPlatformDeptChild(dept);
				if(dept.getChildren() != null && dept.getChildren().size() == 0) {
					return mapper.trueDeletePlatformDept(id);
				}
			}
			throw new ValidateException("????????????????????????????????????????????????!");
		}catch (DataIntegrityViolationException e) {
			throw new ValidateException("???????????????????????????????????????");
		}
	}

	@Override
	public List<PlatformDept> selectTopLevelPlatformDept() {

		return mapper.selectTopLevelPlatformDeptlist();
	}

	@Override
	public List<PlatformDept> selectTreePlatformDeptList() {
		List<PlatformDept> list = selectTopLevelPlatformDept();
		for (PlatformDept dept : list) {
			setPlatformDeptChild(dept);
		}
		return list;
	}

	private void setPlatformDeptChild(PlatformDept platformDept) {
		long id = platformDept.getDepartment_inner_sn();
		List<PlatformDept> childrenList = this.selectPlatformDeptChildrenList(id);
		platformDept.setChildren(childrenList);
		for (PlatformDept children : childrenList) {
			setPlatformDeptChild(children);
		}

	}

	private void setPlatformDeptParent(PlatformDept platformDept) {
		if (platformDept.getParent_department_inner_sn() != null) {
			platformDept.setParent(mapper.selectPlatformDeptInfo(platformDept.getParent_department_inner_sn()));
			setPlatformDeptParent(platformDept.getParent());
		}

	}

	@Override
	public PlatformEmployee selectPlatformEmployeeInfoById(long id) {
		PlatformEmployee platformEmployee = mapper.selectPlatformEmployeeInfoById(id);
		//---------?????????????????????????????????
		/*
		 * UserRole userRole = null; userRole =
		 * userRoleService.selectUserRoleById(platformEmployee.getUser_inner_sn());
		 * if(userRole !=null)
		 * platformEmployee.setRole_inner_sn(userRole.getRole_inner_sn());
		 */
		return platformEmployee;
	}

	@Override
	public long insertPlatformEmployee(AddPlatformEmployeeForm form) {
		String depts = form.getDepartment_inner_sn().trim();
		Map<String, Object> params = new HashMap<>();
		params.put("parent_employee_inner_sn", form.getParent_employee_inner_sn());
		params.put("sex", form.getSex());
		params.put("status", form.getStatus());
		params.put("personal_certificate_type", form.getPersonal_certificate_type());
		params.put("degree", form.getDegree());
		params.put("marriage_status", form.getMarriage_status());
		params.put("birthday", form.getBirthday());
		params.put("entry_date", form.getEntry_date());
		params.put("departure_date", form.getDeparture_date());
		params.put("name", form.getName());
		params.put("employee_sn", form.getEmployee_sn());
		params.put("nation", form.getNation());
		params.put("position", form.getPosition());
		params.put("personal_certificate_sn", form.getPersonal_certificate_sn());
		params.put("phone", form.getPhone());
		params.put("email", form.getEmail());
		params.put("qq", form.getQq());
		params.put("description", form.getDescription());
		params.put("photo_url", form.getPhoto_url());
		params.put("login_account_status", 0);
		if (StringUtils.isNotNull(form.getLogin_account_status()) &&
				 form.getLogin_account_status() > 0) {
			long user_inner_sn = userService.CreateUser(form.getName(), form.getPhone(), SystemType.PLATFORM, 0);
			params.put("user_inner_sn", user_inner_sn);
			params.put("login_account_status", 1); // ??????????????????????????????
		}else {
			params.put("user_inner_sn", null);
		}
		/*
		 * if (StringUtils.isNotNull(form.getLogin_account_status()) &&
		 * form.getLogin_account_status() > 0) { Map<String, Object> UserRoleparams =
		 * new HashMap<String, Object>(); long user_inner_sn =
		 * userService.CreateUser(form.getPhone(), 0, 0); params.put("user_inner_sn",
		 * user_inner_sn); params.put("login_account_status", 1); // ??????????????????????????????
		 * UserRoleparams.put("user_inner_sn", user_inner_sn); //??????userId???????????? ????????????
		 * UserRole userRole =
		 * userRoleService.selectUserRoleById((long)UserRoleparams.get("user_inner_sn"))
		 * ; int result = 0; //????????? if(userRole != null) {
		 * UserRoleparams.put("role_inner_sn", form.getRole_inner_sn()); result = result
		 * & userRoleService.updateUserRole(UserRoleparams); //????????? }else {
		 * UserRoleparams.put("role_inner_sn", form.getRole_inner_sn()); result = result
		 * & userRoleService.insertUserRole(UserRoleparams); }
		 * 
		 * } else { } params.put("user_inner_sn", null);
		 */
		long emp_id = 0;
		if (mapper.insertPlatformEmployee(params) > 0) {
			emp_id = Long.valueOf(params.get("employee_inner_sn").toString());
			if (StringUtils.isNotEmpty(depts)) {
				String[] dept_sn = depts.split(",");
				for (String dept_id : dept_sn) {
					Map<String, Object> params1 = new HashMap<>();
					params1.put("employee_innser_sn", emp_id);
					params1.put("department_innser_sn", Long.valueOf(dept_id));
					if (mapper.insertPlatformDeptRelEmp(params1) == 0) {
						return 0;
					}
					;
				}
			}
		}
		return emp_id;
	}

	@Override
	public int updatePlatformEmployee(EditPlatformEmployeeForm form) {
		boolean isUpdate = false;
		Long emp_id = form.getEmployee_inner_sn();
		String depts = "";
//		EditPlatformEmployeeForm emp = new EditPlatformEmployeeForm();
		Map<String, Object> emps = form.getEmployee();
		boolean flagDeleteUser = false;
//		org.apache.commons.beanutils.BeanUtils.populate(emp, emps);
		PlatformEmployee employee = selectPlatformEmployeeInfoById(form.getEmployee_inner_sn());
		String employeeName = StringUtils.isEmpty(form.getName())?employee.getName():form.getName();
		//?????????????????????????????????????????????
//		boolean	userRoleBool = false; 
		
		if (employee == null) {
			throw new ValidateException(MessageUtils.message("user.not.found", "???????????????"));
		}
		
		String phone = "";
		if (StringUtils.isNotNull(form.getPhone())) {
			phone = form.getPhone();
		}
		
//			?????????????????????????????????????????????????????????????????????????????????
		if (StringUtils.isNull(form.getLogin_account_status()) && employee.getLogin_account_status() == 1) {
//				???????????????, ????????????????????????????????????
			if (phone.length() > 0 && !phone.equals(employee.getPhone())) {
//				PatternUtils.validatePhone(phone, "user.mobile.phone.number.not.valid");
//					??????user?????????
//					????????????????????????
				int result = userService.UpdateUserLoginName(employee.getUser_inner_sn(), phone);
				if (result == 0) {
					throw new ValidateException(MessageUtils.message("db.add.fail", "????????????"));
				}
			}
		}
		
//			??????????????????
		if (StringUtils.isNotNull(form.getLogin_account_status()) && form.getLogin_account_status() == 1) {
//				??????????????????????????????user login??????	??????????????????????????????
//				??????????????????????????????????????????????????????????????????				
			if (phone.length() <= 0) {
//					???????????????????????????????????????
				if (employee.getPhone().length() <= 0) {
					throw new ValidateException(MessageUtils.message("param.not.null", "????????????"));
				}
				phone = employee.getPhone();
			}
			//???????????????????????? ????????????????????????
			User user = userService.selectUserByLoginName(phone);
			Map<String, Object> UserRoleparams = new HashMap<String, Object>();	
			if(user == null) {
				Long user_inner_sn = userService.CreateUser(employeeName, phone, SystemType.PLATFORM, 0);
				form.setUser_inner_sn(user_inner_sn);
				emps.put("user_inner_sn", user_inner_sn);
				UserRoleparams.put("user_inner_sn", user_inner_sn);
			}else {
//				form.setUser_inner_sn(user.getId());
//				emps.put("user_inner_sn", user.getId());
//				UserRoleparams.put("user_inner_sn", user.getId());
				throw new ValidateException(MessageUtils.message("user.mobile.phone.exists", "?????????????????????"));
			}
			
			//??????userId???????????? ????????????
			/*
			 * UserRole userRole =
			 * userRoleService.selectUserRoleById((long)UserRoleparams.get("user_inner_sn"))
			 * ; int result = 0; if(userRole != null) { UserRoleparams.put("role_inner_sn",
			 * form.getRole_inner_sn()); result = result &
			 * userRoleService.updateUserRole(UserRoleparams); userRoleBool = true; }else {
			 * UserRoleparams.put("role_inner_sn", form.getRole_inner_sn()); result = result
			 * & userRoleService.insertUserRole(UserRoleparams); userRoleBool = true; }
			 */
			
			
		}
		
//			??????????????????
		Long user_inner_sn = Long.valueOf(-1);
		if (StringUtils.isNotNull(employee.getUser_inner_sn())) {
			user_inner_sn = employee.getUser_inner_sn();
		}
		if (StringUtils.isNotNull(form.getLogin_account_status()) && form.getLogin_account_status() == 0) {
//				??????????????????????????????
			if (user_inner_sn != -1) {
				User u = userService.selectUserById(user_inner_sn);
//					??????????????????
				if (u != null) {
					if (u.getOrg_id() == 0) {
//							?????????????????????
						flagDeleteUser = true;
						user_inner_sn = u.getId();
						
						//?????????????????? ???????????????roleId
						/*
						 * Map<String, Object> UserRoleparams = new HashMap<String, Object>();
						 * UserRoleparams.put("user_inner_sn", user_inner_sn); int result = 0; result =
						 * result & userRoleService.deleteUserRole(UserRoleparams); userRoleBool = true;
						 */
					}
				}
			}
		}
		int updateResult = 1;
		if(form.isUpdate()) {
			emps.put("employee_inner_sn", form.getEmployee_inner_sn());
			updateResult = updateResult & mapper.updatePlatformEmployee(emps);
			
		}
		if (updateResult > 0) {
			/*
			 * if(!userRoleBool && form.isUpdateRole()) {
			 * if(StringUtils.isNotNull(form.getLogin_account_status()) &&
			 * form.getLogin_account_status() == 0) { //?????????????????? ???????????????roleId Map<String,
			 * Object> UserRoleparams = new HashMap<String, Object>();
			 * UserRoleparams.put("user_inner_sn", user_inner_sn); int result = 0; result =
			 * result & userRoleService.deleteUserRole(UserRoleparams); userRoleBool = true;
			 * }else { //??????userId???????????? ???????????? Map<String, Object> UserRoleparams = new
			 * HashMap<String, Object>(); UserRoleparams.put("user_inner_sn",
			 * employee.getUser_inner_sn()); UserRole userRole =
			 * userRoleService.selectUserRoleById((long)UserRoleparams.get("user_inner_sn"))
			 * ; int resultRole = 0; if(userRole != null) {
			 * UserRoleparams.put("role_inner_sn", form.getRole_inner_sn()); resultRole =
			 * resultRole & userRoleService.updateUserRole(UserRoleparams);
			 * 
			 * }else { UserRoleparams.put("role_inner_sn", form.getRole_inner_sn());
			 * resultRole = resultRole & userRoleService.insertUserRole(UserRoleparams); } }
			 * 
			 * }
			 */
			
			
//			?????????????????????????????????????????????????????????
			if (flagDeleteUser && user_inner_sn != -1) {
				userService.deleteUserUnscoped(user_inner_sn);
			}
			if(form.isUpdateDept()) {
				depts = form.getDepartment_inner_sn().trim();
				mapper.deletePlatformDeptRelEmp(emp_id);
				if (StringUtils.isNotEmpty(depts)) {
					String[] dept_sn = depts.split(",");
					for (String dept_id : dept_sn) {
						Map<String, Object> params = new HashMap<>();
						params.put("employee_innser_sn", emp_id);
						params.put("department_innser_sn", Long.valueOf(dept_id));
						if (mapper.insertPlatformDeptRelEmp(params) == 0) {
							return 0;
						}
					}
				}
			}
			isUpdate = true;
		}
		
		return isUpdate ? 1 : 0;
	}

	@Override
	public int deletePlatformEmployee(long id) {
		mapper.deletePlatformDeptRelEmp(id);
//		????????????
//		??????????????????????????????
		PlatformEmployee employee = mapper.selectPlatformEmployeeInfoById(id);
		if (employee == null) {
			throw new ValidateException(MessageUtils.message("user.not.found", "???????????????"));
		}

		boolean flagDeleteUser = false;
		if (StringUtils.isNotNull(employee.getUser_inner_sn())) {
			User u = userService.selectUserById(employee.getUser_inner_sn());
//			??????????????????
			if (u != null) {
				if (u.getOrg_id() == 0) {
//					?????????????????????
					flagDeleteUser = true;
					//????????????????????????
					/*
					 * Map<String, Object> params = new HashMap<String, Object>();
					 * params.put("user_inner_sn", employee.getUser_inner_sn()); int result = 0;
					 * result = result & userRoleService.deleteUserRole(params);
					 */
				}
			}
		}
		int result = mapper.deletePlatformEmployee(id);
		if (result > 0 && flagDeleteUser) {
			result = userService.deleteUserUnscoped(employee.getUser_inner_sn());
		}
		return result;
	}

	@Override
	public List<PlatformEmployee> selectTreePlatformEmployeeList() {
		List<PlatformEmployee> list = mapper.selectTopPlatformEmployeeList();
		for (PlatformEmployee emp : list) {
			this.setPlatformEmpChild(emp);
		}
		return list;
	}

	private void setPlatformEmpChild(PlatformEmployee platformEmployee) {
		List<PlatformEmployee> list = mapper
				.selectPlatformEmployeeChildrenList(platformEmployee.getEmployee_inner_sn());
		platformEmployee.setChildren(list);
		for (PlatformEmployee emp : list) {
			setPlatformEmpChild(emp);
		}
	}

	@Override
	public List<PlatformEmployee> selectPlatformEmployeeList(SearchForm form) {
		List<PlatformEmployee> list = mapper.selectPlatformEmployeeList(form);
		//??????list????????????????????????
		/*
		 * UserRole userRole = null; for(PlatformEmployee o : list) { userRole =
		 * userRoleService.selectUserRoleById(o.getUser_inner_sn()); if(userRole !=null)
		 * { o.setRole_inner_sn(userRole.getRole_inner_sn()); }
		 * 
		 * }
		 */
		
		return list;
	}

	@Override
	public List<PlatformEmployee> selectPlatformEmployeeDropdownList() {

		return mapper.selectPlatformEmployeeList(null);
	}
	//????????????
	/*
	 * public UserRole getUserRole(Long id) { //??????userId???????????? PlatformEmployee
	 * platformEmployee = selectPlatformEmployeeInfoById(id); UserRole useRole =
	 * null; useRole =
	 * userRoleService.selectUserRoleById(platformEmployee.getUser_inner_sn());
	 * return useRole; }
	 */
}
