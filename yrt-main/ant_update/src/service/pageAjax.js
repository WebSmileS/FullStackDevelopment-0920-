import axios from './axios'
import qs from 'qs' // 注意: post, put提交方式需要采用该写法: qs.stringify(params)
import VueCookie from 'vue-cookie'

// 部分接口需延长超时时间
const LongTimeout = 60000

/* =============== 公用 接口区域 ================== */
// 查询登录人所在部门关联的仓库【当前登录用户的仓库】
export const userWarehouseListAPI = () => axios.get(`/v1/business/warehouse/list`)
// 登录成功后获取【用户菜单】
export const menuUserMenuAPI = (terminalName) => axios.get(`/v1/user/get_user_menu/${terminalName}`)
// 查询登录帐号的初始密码(分页)
export const accountListAPI = (params, pageNum, pageSize) => axios.post(`/v1/comm/account/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 获取【全部省份列表】
export const provinceAreaAPI = params => axios.get('/v1/comm/area/province', params)
// 根据【省份id查询所有的下级城市信息】
export const cityAreaAPI = id => axios.get(`/v1/comm/area/city/${id}`)
// 根据【城市id查询所有的下级区域信息】
export const countyAreaAPI = id => axios.get(`/v1/comm/area/county/${id}`)
// 根据城市id查询上级区域信息 【查询所有的上级区域信息】
export const listAreaAPI = id => axios.get(`/v1/comm/area/list/${id}`)
// 查询医疗器械【68分类列表】信息的详情 - 查询所有的最上级68分类信息
export const code68ParentListAPI = () => axios.get(`/v1/comm/code68/list`)
// 根据【pid查询分类下所有的下级分类信息】
export const code68ChildrenListAPI = id => axios.get(`/v1/comm/code68/list/${id}`)
// 【增加】商品自定义分类
export const categoryAddAPI = params => axios.post(`/v1/comm/category/add`, qs.stringify(params))
// 【编辑】商品自定义分类
export const categoryEditAPI = (id, params) => axios.put(`/v1/comm/category/edit/${id}`, qs.stringify(params))
// 【获取所有的最上级/顶级】自定义分类
export const categoryGetParentListAPI = () => axios.get(`/v1/comm/category/list`)
// 根据id【查询下级分类信息列表】
export const categoryGetNextListAPI = id => axios.get(`/v1/comm/category/list/${id}`)
// 根据id【查询分类信息】
export const categoryGetInfoAPI = id => axios.get(`/v1/comm/category/${id}`)
// 注册新机构
export const registAddAPI = params => axios.post(`/v1/reg/add`, params)
// 【查询】获取所有认证通过的厂商列表
export const getVendorList = (systemType, params) => axios.get(`/v1/comm/${systemType}/allOrg`, params)
// 相关机构列表(分页)
export const getInquiryOrgListAPI = (systemType, params) => axios.get(`/v1/business/inquiry/org/list/${systemType}`, params)
// 获得图片上传Token
export const getUploadTokenAPI = () => axios.get(`/v1/image/token`)
// 获得文件上传Token
export const getUploadFileTokenAPI = () => axios.get(`/v1/att/token`)
// 【自定义分类】根据id查询所有上级信息
export const getCategoryParentsAPI = id => axios.get(`/v1/comm/category/parents/${id}`)
// 【自定义分类】根据id查询所有上级信息
export const getCode68ParentsAPI = id => axios.get(`/v1/comm/code68/parents/${id}`)
// 【自定义分类】获取所有tree
export const getAllCategoryAPI = () => axios.get(`/v1/comm/category/all/list`)
// 平台所有产品
export const getAllVendorListAPI = (params) => axios.get(`/v1/business/grn/vendor/list`, params)
// 获取厂商产品列表(平台所有发布产品)
export const getVendorProductListAPI = (params) => axios.get(`/v1/comm/vendor/Product/list`, params)
// 获取厂商产品列表(平台所有发布产品)
export const getVendorProductDropdownAPI = (params) => axios.get(`/v1/comm/vendor/Product/dropdown`, params)

/* =============== 平台运维 接口区域 ================== */
// 获取平台菜单母树
export const getPlatformTree = () => axios.get(`/v1/menu/get_platform_tree`)
// 获取所有菜单母树
export const getAllTree = () => axios.get(`/v1/menu/getall_tree`)
// 增加菜单树节点
export const addNode = params => axios.post(`/v1/menu/add_node`, params)
// 修改菜单树节点
export const updateNode = params => axios.put(`/v1/menu/update_node`, params)
// 删除菜单树节点
export const deleteNode = id => axios.delete(`/v1/menu/delete_node/${id}`)
// 上移节点
export const moveUpNode = params => axios.put('/v1/menu/moveup_node', params)
// 下移节点
export const moveDownNode = params => axios.put('/v1/menu/movedown_node', params)
// 获取指定类型的组织菜单
export const menuSpecifyOrgMenuAPI = type => axios.get(`/v1/menu/org_menu/${type}`)
// 获取系统固定角色列表
export const menuGetSystemRoleListAPI = () => axios.get('/v1/menu/get_system_role_list')
// 获取系统固定角色树IDs
export const roleGetSystemRoleTreeIDs = id => axios.get(`/v1/menu/get_system_role_tree_ids/${id}`)
// 获取系统固定角色树IDs
export const roleUpdateSystemRoleTree = params => axios.put(`/v1/menu/update_system_role_tree`, params)
// 获取菜单节点，带详细信息
export const menuGetNode = id => axios.get(`/v1/menu/get_node/${id}`)
// 新建端
export const addTerminalAPI = params => axios.post('/v1/tree/add_terminal', params)
// 修改端
export const updateTerminalAPI = params => axios.post('/v1/tree/update_terminal', params)
// 删除端
export const delTerminalAPI = key => axios.delete(`/v1/tree/del_terminal/${key}`)
// 获取所有端-树
export const getAllTerminalTreeAPI = () => axios.get('/v1/tree/getall_terminal_tree')
// 新建母树
export const addTreeAPI = params => axios.post('/v1/tree/add_tree', params)
// 修改母树信息
export const updateTreeApi = params => axios.post('/v1/tree/update_tree', params)
// 删除母树
export const delTreeAPI = (id) => axios.delete(`/v1/tree/del_tree/${id}`)
// 获取母树
export const getMotherTreeAPI = (terminal, key) => axios.get(`/v1/tree/get_mother_tree/${terminal}/${key}`)
// 新增母树节点
export const addNodeAPI = params => axios.post('/v1/tree/add_node', params)
// 修改母树节点
export const updateNodeAPI = params => axios.post('/v1/tree/update_node', params)
// 删除母树节点
export const delNodeAPI = id => axios.delete(`/v1/tree/del_node/${id}`)
// 获取所有组织模版
export const getAllGroupTemplateAPI = () => axios.get('/v1/tree/get_all_template')
// 新增组织模版
export const addGroupTemplateAPI = params => axios.post('/v1/tree/add_template', params)
// 修改组织模版
export const updateGroupTemplateAPI = params => axios.post('/v1/tree/update_template', params)
// 删除组织模版
export const delGroupTemplateAPI = key => axios.delete(`/v1/tree/del_template/${key}`)
// 设定组织模板的树
// export const setGroupTemplateTreeAPI = params => axios.post('/v1/tree/set_template_tree', params)
// 设定组织模板的权限（按树设置）
export const setTemplatePermissionAPI = params => axios.post('/v1/tree/set_template_permission', params)

// 删除组织模版的树
export const delTreeFromTemplateAPI = (template, terminal) => axios.delete(`/v1/tree/del_template_tree/${template}/${terminal}`)
// 获取组织模版指定端的树索引
export const getTemplateTreeIndexAPI = (template, terminal) => axios.get(`/v1/tree/get_template_tree_index/${template}/${terminal}`)
// 获取组织模版指定端的树
export const getTemplateTreeAPI = (template, terminal) => axios.get(`/v1/tree/get_template_tree/${template}/${terminal}`)
// 获取指定组织模版下所有树信息，不包含实体
export const getTemplateAllTreeInfoAPI = key => axios.get(`/v1/tree/get_template_all_tree_info/${key}`)
// 获取指定组织模版下所有树
export const getTemplateAllTreeAPI = key => axios.get(`/v1/tree/get_template_all_tree/${key}`)
// 获取指定组织模板下所有角色
export const getTemplateRolesAPI = key => axios.get(`/v1/tree/get_template_roles/${key}`)
// 获取指定组织模板下所有角色，包括系统内置角色
export const getTemplateAllRolesAPI = key => axios.get(`/v1/tree/get_template_all_roles/${key}`)
// 新增指定组织模板角色
export const addTemplateRoleAPI = params => axios.post('/v1/tree/add_template_role', params)
// 修改指定组织模版角色（名称）
export const updateTemplateRoleNameAPI = params => axios.post('/v1/tree/update_template_role_name', params)
// 删除指定组织模版角色
export const delTemplateRoleAPI = (template, role) => axios.delete(`/v1/tree/del_template_role/${template}/${role}`)
// 设定组织模版角色树
// export const setTemplateRoleTreeAPI = params => axios.post('/v1/tree/set_template_role_tree', params)
// 设定模板角色权限
export const setTemplateRolePermissionAPI = rolePermission => axios.post(`/v1/tree/set_template_role_permission`, rolePermission)
// 获取组织模板角色指定端树（索引）
export const getTemplateRoleTreeIndexAPI = (role, terminal) => axios.get(`/v1/tree/get_template_role_tree_index/${role}/${terminal}`)
// 获取角色所有端树（索引）
// export const getRoleTreesIndexAPI = role => axios.get(`/v1/tree/get_role_trees_index/${role}`)
// 获取角色所有权限
export const getRolePermissionsAPI = role => axios.get(`/v1/tree/get_role_permissions/${role}`)
// 获取组织对应的模板
// export const getGroupTemplateKeyAPI = (group) => axios.get(`/v1/tree/get_group_template_key/${group}`)
export const getGroupTemplateAPI = (systemType, groupID) => axios.get(`/v1/tree/get_group_template/${systemType}/${groupID}`)
// 设定组织的模板
export const setTemplate2GroupAPI = (params) => axios.post('/v1/tree/set_template_to_group', params)
// 获取母树节点详细，包括API等
export const getMTNodeAPI = (node) => axios.get(`/v1/tree/get_mtnode/${node}`)
// 导入子节点
export const importTreeAPI = (tree) => axios.post(`/v1/tree/import_tree`, tree)
// // 节点上移
// export const nodeMoveUpAPI = (node) => axios.post(`/v1/tree/node_moveup/${node}`)
// // 节点下移
// export const nodeMoveDownAPI = (node) => axios.post(`/v1/tree/node_movedown/${node}`)
// 节点顺序对调
export const nodeSwapSortAPI = (post) => axios.post('/v1/tree/node_swap', post)
// 获取模板权限，根据terminal
export const getTemplatePermissionAPI = (template, terminal) => axios.get(`/v1/tree/get_template_permission/${template}/${terminal}`)

/* =============== 用户管理 接口区域 ================== */
// 获取用户列表
export const userGetUserListAPI = params => axios.get('/v1/user/get_user_list', params)
// 修改登录电话
export const userChangeUsernameAPI = params => axios.put('/v1/user/change_username', params)
// 删除系统账号
export const userDeleteUserAPI = id => axios.delete(`/v1/user/delete_user/${id}`)
// 重置密码
export const userResetPasswordAPI = id => axios.post(`/v1/user/reset_password/${id}`)
// 获取角色列表
export const roleGetRoleListAPI = () => axios.get('/v1/user/get_role_list')
// 新建角色
export const roleAddRoleAPI = params => axios.post('/v1/user/add_role', params)
// 修改角色
export const roleUpdateRoleAPI = params => axios.put('/v1/user/update_role', params)
// 删除角色
export const roleDeleteRoleAPI = id => axios.delete(`/v1/user/delete_role/${id}`)
// 登录成功后获取【用户所属组织全菜单】
export const menuOrgMenuAPI = () => axios.get('/v1/menu/org_menu')
// 登录成功后获取【用户所属组织全菜单】
export const roleUpdateRoleTree = params => axios.put('/v1/user/update_role_tree', params)
// 获取角色树（仅ID）
export const roleGetRoleTreeIDs = id => axios.get(`/v1/user/get_role_tree_ids/${id}`)
// 获取角色树
export const roleGetRoleTree = id => axios.get(`/v1/user/get_role_tree/${id}`)
// 设定账号角色
export const roleBindRoleAPI = params => axios.put('/v1/user/bind_role', params)
// <管理员>获取角色列表
export const userGetRoleListAPI = () => axios.get(`/v1/user/get_role_list`)
// <当前>获取用户信息
export const userUserInfoAPI = () => axios.get(`/v1/user/user_info`)
// 用户修改自己的登录密码
export const userChangeMyPasswordAPI = params => axios.put('/v1/user/change_my_password', params)
// 找回密码-修改密码
export const userChangePasswordAPI = params => axios.post('/v1/user/retrieve_pwd/change_password', params)
// 找回密码-发送验证码
export const userGetVerifyCodeAPI = phone => axios.get(`/v1/user/retrieve_pwd/code/${phone}`)
// 找回密码-验证短信验证码
export const userVerifyCodeAPI = params => axios.post('/v1/user/retrieve_pwd/verify_code', params)

// <管理员>获取所有端和树
export const getGroupAllTreeAPI = () => axios.get('/v1/user/get_group_all_tree')
// <管理员> 获取所有角色
export const getGroupRolesAPI = () => axios.get('/v1/user/get_group_roles')
// <管理员>增加新角色
export const addGroupRoleAPI = params => axios.post('/v1/user/add_group_role', params)
// <管理员>修改角色名称
export const updateGroupRoleAPI = params => axios.post('/v1/user/update_group_role', params)
// <管理员>删除角色
export const delGroupRoleAPI = id => axios.delete(`/v1/user/del_group_role/${id}`)
// <管理员>设置角色权限树
// export const setGroupRoleTreeAPI = params => axios.post('/v1/user/set_group_role_tree', params)
// <管理员>设置角色权限
export const setRolePermissionAPI = params => axios.post('/v1/user/set_role_permission', params) 
// <管理员>设置用户角色
export const setUserRoleAPI = params => axios.post('/v1/user/set_user_role', params)
// <管理员>删除用户角色
export const delUserRoleAPI = id => axios.delete(`/v1/user/del_user_role/${id}`)

/* =============== 平台管理 接口区域 ================== */
// 根据注册机构id【删除】注册机构
export const platformRegistDeleteAPI = id => axios.delete(`/v1/platform/orgReg/delete/${id}`)
// 根据注册机构id【修改】注册机构基本信息
export const platformRegistEditAPI = (id, params) => axios.put(`/v1/platform/orgReg/edit/${id}`, qs.stringify(params))
// 根据注册机构id【查询】机构信息
export const platformRegistInfoAPI = id => axios.get(`/v1/platform/orgReg/info/${id}`)
// 根据注册机构类型【获取机构列表】 = systemType从页面传入的tab选项卡参数
export const platformRegistListAPI = (systemType, params) => axios.get(`/v1/platform/orgReg/list/${systemType}`, params)
// 根据注册机构id【审核通过】机构信息
export const platformRegistPassAPI = id => axios.put(`/v1/platform/orgReg/pass/${id}`)
// 根据注册机构id【审核不通过】机构信息
export const platformRegistNopassAPI = (id, params) => axios.put(`/v1/platform/orgReg/nopass/${id}`, qs.stringify(params))
// 解决注册机构审核冲突
export const platformRegisterConflictAPI = (id, params) => axios.post(`/v1/platform/orgReg/match/${id}`, params)
// 添加产品
export const platformProductAdd = params => axios.post(`/v1/platform/product/add`, qs.stringify(params))
// 按条件搜索未发布的产品列表
export const platformProductNnpublicAPI = params => axios.get(`/v1/platform/product/unpublic/list`, params)
// 根据搜索条件和产品数据来源获取产品
export const platformProductListAPI = (source, params) => axios.get(`/v1/platform/product/list/${source}`, params)
// 删除产品信息
export const platformProductDelAPI = (vid, pid) => axios.delete(`/v1/platform/product/del/${vid}/${pid}`)
//  修改产品信息
export const platformProductEditAPI = (vid, pid, params) => axios.put(`/v1/platform/product/edit/${vid}/${pid}`, qs.stringify(params))
// 上传产品图片
export const platformProductImgAddAPI = (params) => axios.post(`/v1/platform/product/image/add`, qs.stringify(params))
// 新增医疗器材注册登记表图片
export const platformProductMdrfImgAddAPI = params => axios.post(`/v1/platform/product/mdrf/image/add`, params)
// 删除医疗器材注册登记表图片
export const platformProductMdrfImgDelAPI = (vid, pid, mid, miid) => axios.delete(`/v1/platform/product/mdrf/image/del/${vid}/${pid}/${mid}/${miid}`)
// 获取产品图片
export const platformProductImgGetAPI = (vid, pid, certType) => axios.get(`/v1/platform/product/image/${vid}/${pid}/${certType}`)
// 获取产品医疗器材注册登记表图片列表
export const platformProductMdrfImgAPI = (vid, pid, mid) => axios.get(`/v1/platform/product/mdrf/image/list/${vid}/${pid}/${mid}`)
// 获取产品图片
export const dealerProductImgGetAPI = (vid, pid, certType) => axios.get(`/v1/business/dealer/product/image/${vid}/${pid}/${certType}`)
// 删除产品图片
export const platformProductImgDelAPI = (vid, pid, certType, id) => axios.delete(`/v1/platform/product/image/del/${vid}/${pid}/${certType}/${id}`)
// 删除产品图片【经销商】
export const dealerProductImgDelAPI = (vid, pid, certType, id) => axios.delete(`/v1/business/dealer/product/image/del/${vid}/${pid}/${certType}/${id}`)
// 获取医疗器材注册登记表
export const platformProductMdrfGet = (vid, pid) => axios.get(`/v1/platform/product/mdrf/list/${vid}/${pid}`)
// 搜索产品医疗器材注册登记表列表
export const platformProductMdrfSearchAPI = (params) => axios.get(`/v1/platform/product/mdrf/search`, params)
// 新增医疗器材注册登记表
export const platformProductMdrfAdd = (vid, pid, params) => axios.post(`/v1/platform/product/mdrf/add/${vid}/${pid}`, params)
// 根据厂商ID和产品ID获取产品医疗器材注册登记表
export const platformProductMdrfInfo = (vid, pid, mid) => axios.get(`/v1/platform/product/mdrf/info/${vid}/${pid}/${mid}`)
// 修改医疗器材注册登记表
export const platformProductMdrfEdit = (vid, pid, mid, params) => axios.put(`/v1/platform/product/mdrf/edit/${vid}/${pid}/${mid}`, params)
// 删除医疗器材注册登记表
export const platformProductMdrfDel = (vid, pid, mid, params) => axios.delete(`/v1/platform/product/mdrf/del/${vid}/${pid}/${mid}`, params)
//  发布产品
export const platformProductPublic = (vid, pid) => axios.put(`/v1/platform/product/public/${vid}/${pid}`)
// 批量发布产品
export const platformProductPublicBatch = (params) => axios.post(`/v1/platform/product/public`, params)
// 获取已发布的产品列表
export const platformProductPublicList = params => axios.get(`/v1/platform/product/public/list`, params)
// 获取规格型号列表
export const platformProductSpecList = (vid, pid) => axios.get(`/v1/platform/product/spec/list/${vid}/${pid}`)
// 新增规格型号
export const platformProductSpecAdd = (params) => axios.post(`/v1/platform/product/spec/add`, qs.stringify(params))
// 修改规格型号
export const platformProductSpecEdit = (params) => axios.put(`/v1/platform/product/spec/edit`, qs.stringify(params))
// 删除规格型号
export const platformProductSpecDel = (vid, pid, id) => axios.delete(`/v1/platform/product/spec/del/${vid}/${pid}/${id}`)
// 同步平台产品信息与所有仓库的库存产品信息
export const platformProductSyncAPI = (vid, pid) => axios.get(`/v1/platform/product/sync/${vid}/${pid}`)
// 获取所有产品单位
export const platformUnitList = (vid, pid, psid) => axios.get(`/v1/platform/unit/list/${vid}/${pid}/${psid}`)
// 获取所有产品单位以树的方式返回
export const platformUnitTree = (vid, pid, psid) => axios.get(`/v1/platform/unit/tree/${vid}/${pid}/${psid}`)
// 获取下级产品单位
export const platformUnitNextList = (vid, pid, psid, pupid) => axios.get(`/v1/platform/unit/list/${vid}/${pid}/${psid}/${pupid}`)
// 获取包装单位所有上级
export const platformUnitParents = (vid, pid, psid, pupid) => axios.get(`/v1/platform/unit/parents/${vid}/${pid}/${psid}/${pupid}`)
// 新增产品单位
export const platformUnitAdd = (params) => axios.post(`/v1/platform/unit/add`, qs.stringify(params))
// 修改产品单位
export const platformUnitEdit = (vid, pid, psid, puid, params) => axios.put(`/v1/platform/unit/edit/${vid}/${pid}/${psid}/${puid}`, qs.stringify(params))
// 根据产品单位ID查询单位信息
export const platformUnitInfo = (vid, pid, psid, puid) => axios.get(`/v1/platform/unit/info/${vid}/${pid}/${psid}/${puid}`)
// 登陆
export const loginAPI = params => axios.post(`/v1/login`, params)
// 登出
export const logoutAPI = () => axios.post(`/v1/logout`)
// 【删除】平台部门
export const platformDeptDelAPI = (id) => axios.delete(`/v1/platform/dept/del/${id}`)
// 【新增】平台部门信息
export const platformDeptAddAPI = (params) => axios.post(`/v1/platform/dept/add`, qs.stringify(params))
// 根据平台部门id【查询平台该部门的子部门列表】
export const platformDeptChildrenlistAPI = (id) => axios.get(`/v1/platform/dept/childrenlist/${id}`)
// 【修改】平台部门信息
export const platformDeptEditAPI = (params) => axios.put(`/v1/platform/dept/edit`, qs.stringify(params))
// 根据平台部门id【查询平台部门信息】
export const platformDeptInfoAPI = (id) => axios.get(`/v1/platform/dept/info/${id}`)
// 【查询平台所有顶级部门列表】
export const platformDeptToplevelAPI = () => axios.get(`/v1/platform/dept/toplevel`)
// 【查询平台所有部门】
export const platformDeptTreeAPI = () => axios.get(`/v1/platform/dept/tree`)
// 【查询平台人员树】
export const platformEmpTreeAPI = () => axios.get(`/v1/platform/emp/Tree`)
// 【新增】平台人员
export const platformEmpAddAPI = (params) => axios.post(`/v1/platform/emp/add`, qs.stringify(params))
// 【删除】平台人员
export const platformEmpDelAPI = (id) => axios.delete(`/v1/platform/emp/del/${id}`)
// 【编辑】平台人员
export const platformEmpEditAPI = (params) => axios.put(`/v1/platform/emp/edit`, qs.stringify(params))
// 根据平台人员ID【查询平台人员详情】
export const platformEmpInfoAPI = (id) => axios.get(`/v1/platform/emp/info/${id}`)
// 下拉列表使用【查询平台所有人员】
export const platformEmpDropdownListAPI = () => axios.get(`/v1/platform/emp/dropdownlist`)
// 【查询】平台人员
export const platformEmpListAPI = (params) => axios.get(`/v1/platform/emp/list`, params)
// 根据机构类型获取待审批机构列表
export const platformAgencyUnAuthListAPI = (systemType, params) => axios.get(`/v1/platform/org/unAuth/list/${systemType}`, params)
// 根据机构类型获取已审批机构列表
export const platformAgencyAuthListAPI = (systemType, params) => axios.get(`/v1/platform/org/list/${systemType}`, params)
// 根据机构ID获取证件图片列表
export const platformAgencyImgListAPI = (systemType, id) => axios.get(`/v1/platform/org/image/list/${systemType}/${id}`)
// 获取机构指定证件类型的图片
export const platformOrgImgListAPI = (systemType, id, certType) => axios.get(`/v1/platform/org/image/info/${systemType}/${id}/${certType}`)
// 根据注册机构id【查询】机构信息
export const platformAgenecyBaseInfoAPI = (systemType, id) => axios.get(`/v1/platform/org/info/${systemType}/${id}`)
// 修改注册机构信息
export const platformAgenecyEditInfoAPI = (systemType, id, params) => axios.put(`/v1/platform/org/edit/${systemType}/${id}`, qs.stringify(params))
// 删除机构
export const platformAgencyDelAPI = (systemType, id) => axios.delete(`/v1/platform/org/delete/${systemType}/${id}`)
// 机构认证通过
export const platformAgencyPassAPI = (systemType, id) => axios.put(`/v1/platform/org/pass/${systemType}/${id}`)
// 机构认证不通过
export const platformAgencyNoPassAPI = (systemType, id, reason) => axios.put(`/v1/platform/org/nopass/${systemType}/${id}?reason=${reason}`)
// 根据机构id获取营业执照信息
export const platformAgencyLicAPI = (systemType, id) => axios.get(`/v1/platform/org/info/license/${systemType}/${id}`)
// 根据机构id添加营业执照信息
export const platformAgencyAddLicAPI = (systemType, id, params) => axios.post(`/v1/platform/org/add/license/${systemType}/${id}`, qs.stringify(params))
// 根据机构id修改营业执照信息
export const platformAgencyEditLicAPI = (systemType, id, params) => axios.put(`/v1/platform/org/edit/license/${systemType}/${id}`, qs.stringify(params))
// 获取经销商医疗器械经营许可证的所有信息
export const platformAgencyDealerAPI = (id) => axios.get(`/v1/platform/org/info/maaiol/${id}`)
// 添加经销商医疗器械经营许可证的所有信息
export const platformAgencyAddDealerAPI = (id, params) => axios.post(`/v1/platform/org/add/maaiol/${id}`, qs.stringify(params))
// 编辑经销商医疗器械经营许可证的所有信息
export const platformAgencyEditDealerAPI = (id, params) => axios.put(`/v1/platform/org/edit/maaiol/${id}`, qs.stringify(params))
// 获取经销商第二类医疗器械经营备案凭证的所有信息
export const platformAgencyDealerSecAPI = (id) => axios.get(`/v1/platform/org/info/stmdbrc/${id}`)
// 添加经销商第二类医疗器械经营备案凭证的所有信息
export const platformAgencyAddDealerSecAPI = (id, params) => axios.post(`/v1/platform/org/add/stmdbrc/${id}`, qs.stringify(params))
// 编辑经销商第二类医疗器械经营备案凭证的所有信息
export const platformAgencyEditDealerSecAPI = (id, params) => axios.put(`/v1/platform/org/edit/stmdbrc/${id}`, qs.stringify(params))
// 获取医疗机构执业许可证的所有信息
export const platformAgencyHospitalAPI = (id) => axios.get(`/v1/platform/org/info/plhmi/${id}`)
// 添加医疗机构执业许可证的所有信息
export const platformAgencyAddHospitalAPI = (id, params) => axios.post(`/v1/platform/org/add/plhmi/${id}`, qs.stringify(params))
// 编辑医疗机构执业许可证的所有信息
export const platformAgencyEditHospitalAPI = (id, params) => axios.put(`/v1/platform/org/edit/plhmi/${id}`, qs.stringify(params))
// 获取厂商医疗器械生产许可证的所有信息
export const platformAgencyVendorAPI = (id) => axios.get(`/v1/platform/org/info/mdpl/${id}`)
// 添加厂商医疗器械生产许可证的所有信息
export const platformAgencyAddVendorAPI = (id, params) => axios.post(`/v1/platform/org/add/mdpl/${id}`, qs.stringify(params))
// 编辑厂商医疗器械生产许可证的所有信息
export const platformAgencyEditVendorAPI = (id, params) => axios.put(`/v1/platform/org/edit/mdpl/${id}`, qs.stringify(params))
// 新增机构相关图片
export const platformAgencyAddPicAPI = (systemType, id, certType, params) => axios.post(`/v1/platform/org/image/add/${systemType}/${id}/${certType}`, qs.stringify(params))
// 添加厂商基本信息
export const platformAddNewVendorAPI = (params) => axios.post(`/v1/platform/org/vendor/add`, qs.stringify(params))
// 获取机构经营返回tree
export const platformAgencyScopeTreeAPI = () => axios.get(`/v1/comm/code68/tree`)
// 复制产品医疗器材注册登记表到其他产品
export const platformProductMdrfCopyAPI = (vid, pid, mid, params) => axios.post(`/v1/platform/product/mdrf/copy/${vid}/${pid}/${mid}`, params)

/* =============== 医院 接口区域 ================== */
// 【获取待申领材料】申领列表(分页,创建时间倒序)
export const hospitalApplyListAPI = (params) => axios.get(`/v1/business/hospital/apply/list`, params)
// 【获取已申领材料】申领列表(分页,创建时间倒序)
export const hospitalApplyHistoryListAPI = (params) => axios.get(`/v1/business/hospital/apply/history/list`, params)
// 【获取】有效合同内产品列表(分页)
export const hospitalApplyProductListAPI = (oid, productType, params) => axios.get(`/v1/business/hospital/apply/product/list/${oid}/${productType}`, params)
// 【添加】材料申领信息
export const hospitalApplyAddAPI = (params) => axios.post(`/v1/business/hospital/apply/add`, params)
// 【复制】材料申领信息
export const hospitalApplyCopyAPI = (aid) => axios.post(`/v1/business/hospital/apply/copy/${VueCookie.get('userbelong')}/${aid}`)
// 【删除】材料申领信息
export const hospitalApplyDelAPI = (aid) => axios.delete(`/v1/business/hospital/apply/del/${VueCookie.get('userbelong')}/${aid}`)
// 【终止】材料申领信息
export const hospitalApplyStopAPI = (oid, aid) => axios.put(`/v1/business/hospital/apply/stop/${oid}/${aid}`)
// 【修改】材料申领信息
export const hospitalApplyEditAPI = (params) => axios.put(`/v1/business/hospital/apply/edit`, params)
// 【获取】材料申领信息
export const hospitalApplyInfoAPI = (aid) => axios.get(`/v1/business/hospital/apply/info/${VueCookie.get('userbelong')}/${aid}`)
// 【获取】材料申领信息的部门列表【新增的弹框】
export const orgDeptRelAPI = (oid) => axios.get(`/v1/business/org/dept/rel/${oid || VueCookie.get('userbelong')}`)
// 【删除】申领材料-明细
export const hospitalApplyDetailDelAPI = (params) => axios.delete(`/v1/business/hospital/apply/detail/del`, { params })
// 【终止】申领材料-明细
export const hospitalApplyDetailStopAPI = (params) => axios.put(`/v1/business/hospital/apply/detail/stop`, qs.stringify(params))
// 【获取】待发放申领单列表(分页,提交时间倒序)
export const hospitalGrantApplyListAPI = (params) => axios.get(`/v1/business/hospital/grant/apply/list`, params)
// 【获取】获取已发放申领单列表(分页,提交时间倒序)
export const hospitalGrantApplyHistoryListAPI = (params) => axios.get(`/v1/business/hospital/grant/apply/history/list`, params)
// 【获取】获取发放单列表
export const hospitalGrantListAPI = (oid, aid, wid) => axios.get(`/v1/business/hospital/grant/list/${oid}/${aid}/${wid}`)
// 【获取】所有发放单列表(分页)
export const hospitalGrantAllListAPI = (oid, params) => axios.get(`/v1/business/hospital/grant/list/${oid}`, params)
// 【可用做出库调单的发放单列表】
export const odoTransferGrantListAPI = (params) => axios.get(`/v1/business/odo/transfer/grant/list`, params)
// 【获取】获取发放单列表 【耗材申领的待申领明细和耗材发放的已发放明细】
export const hospitalGrantIssuedDetailListAPI = (oid, aid) => axios.get(`/v1/business/hospital/grant/list/${oid}/${aid}`)
// 【获取】获取发放单/调拨单列表(申领页面使用)
export const hospitalGrantGaListAPI = (oid, aid) => axios.get(`/v1/business/hospital/grant/ga/${oid}/${aid}`)
// 【获取】获取仓库中存在的产品批号列表
export const hospitalApplyBatchNumberListAPI = (oid, aid, pid, sid, productType) => axios.get(`/v1/business/hospital/apply/batch_number/list/${oid}/${aid}/${pid}/${sid}/${productType}`)
// 【获取】根据发放单ID获取发放单明细列表
export const hospitalGrantDetailListAPI = (oid, gid) => axios.get(`/v1/business/hospital/grant/detail/list/${oid}/${gid}`)
// 【被调单的发放单销售单明细列表】
export const odoTransferGrantDetailAPI = (params) => axios.get(`/v1/business/odo/transfer/grant/detail`, params)
// 【发放】
export const hospitalGrantSendAPI = (params) => axios.post(`/v1/business/hospital/grant/send`, params)
// 【确认发放】
export const hospitalGrantConfirmAPI = (oid, gid) => axios.put(`/v1/business/hospital/grant/confirm/${oid}/${gid}`)
// 【单个提交】发放单
export const hospitalGrantApplyConfirmAPI = (params) => axios.put(`/v1/business/hospital/grant/confirm`, params)
// 【生成采购计划】
export const hospitalGrantCreatplanAPI = (params) => axios.post(`/v1/business/hospital/grant/creatplan`, params)
// 销售订单-点击生成采购计划的按钮【生成采购计划】
export const orderSaleCreatplanAPI = (params) => axios.post(`/v1/business/order/sale/creatplan
`, params)
// 获取产品包装单位的最小倍数 【厂商】
export const platformUnitMinAPI = (vid, pid, psid, puid) => axios.get(`/v1/platform/unit/min/${vid}/${pid}/${psid}/${puid}`)
// 获取产品包装单位的最小倍数 【经销商】
export const dealerUnitMinAPI = (vid, pid, psid, puid) => axios.get(`/v1/business/dealer/unit/min/${vid}/${pid}/${psid}/${puid}`)

/* =============== 厂家 接口区域 ================== */

/* =============== 经销商 接口区域 ================== */
// 根据条件查看医院经销商合同信息"列表
export const hdcontractListAPI = (params) => axios.get(`/v1/business/contract/hdcontract/list`, qs.stringify(params))
// 根据产品ID查询所有产品单位以树的方式返回
export const dealerUnitTreeAPI = (oid, pid, psid) => axios.get(`/v1/business/dealer/unit/tree/${oid}/${pid}/${psid}`)
// 根据id查询所有上级产品单位
export const dealerUnitParentsAPI = (vid, pid, psid, puid) => axios.get(`/v1/business/dealer/unit/parents/${vid}/${pid}/${psid}/${puid}`)
// 经销商自建产品列表
export const dealerProductListAPI = (params) => axios.get(`/v1/business/dealer/product/list`, params)
// 新增产品
export const dealerProductAdd = params => axios.post(`/v1/business/dealer/product/add`, qs.stringify(params))
// 【顶级】产品分类
export const dealerGetParentListAPI = (oid) => axios.get(`/v1/business/dealer/type/list/${oid}`)
// 所有上级产品分类
export const dealerTypeParentsAPI = (oid, tid) => axios.get(`/v1/business/dealer/type/parents/${oid}/${tid}`)
// 根据id【查询下级分类信息列表】
export const dealerGetNextListAPI = (oid, tid) => axios.get(`/v1/business/dealer/type/list/${oid}/${tid}`)
// 【自定义分类】获取所有tree
export const getAllDealerTypeAPI = (oid) => axios.get(`/v1/business/dealer/type/all/tree/${oid}`)
// 删除产品信息
export const dealerProductDelAPI = (oid, pid) => axios.delete(`/v1/business/dealer/product/del/${oid}/${pid}`)
//  修改产品信息
export const dealerProductEditAPI = (oid, pid, params) => axios.put(`/v1/business/dealer/product/edit/${oid}/${pid}`, qs.stringify(params))
// 上传产品图片
export const dealerProductImgAddAPI = (params) => axios.post(`/v1/business/dealer/product/image/add`, qs.stringify(params))
// 【新增】产品类型
export const dealerTypeAddAPI = (oid, params) => axios.post(`/v1/business/dealer/type/add/${oid}`, qs.stringify(params))
// 【修改】产品类型
export const dealerTypeEditAPI = (oid, tid, params) => axios.put(`/v1/business/dealer/type/edit/${oid}/${tid}`, qs.stringify(params))
// 【查询】分类信息
export const dealerTypeInfoAPI = (oid, tid) => axios.get(`/v1/business/dealer/type/info/${oid}/${tid}`)
// 【删除】产品类型
export const dealerTypeDelAPI = (oid, tid) => axios.delete(`/v1/business/dealer/type/del/${oid}/${tid}`)
// 根据经销商ID和产品ID获取产品规格型号列表
export const dealerUnitSpecList = (oid, pid) => axios.get(`/v1/business/dealer/product/spec/list/${oid}/${pid}`)
// 根据经销商ID和产品ID新增规格型号
export const dealerUnitSpecAdd = params => axios.post(`/v1/business/dealer/product/spec/add`, qs.stringify(params))
// 根据经销商ID、产品ID和规格型号ID修改规格型号
export const dealerUnitSpecEdit = params => axios.put(`/v1/business/dealer/product/spec/edit`, qs.stringify(params))
// 根据经销商ID和产品ID删除规格型号
export const dealerUnitSpecEditDel = (oid, pid, id) => axios.delete(`/v1/business/dealer/product/spec/del/${oid}/${pid}/${id}`)
// 获取所有产品单位以树的方式返回
export const dealerUnitTree = (oid, pid, psid) => axios.get(`/v1/business/dealer/unit/tree/${oid}/${pid}/${psid}`)
// 新增产品单位
export const dealerUnitAdd = params => axios.post(`/v1/business/dealer/unit/add`, qs.stringify(params))
// 修改产品单位
export const dealerUnitEdit = (oid, pid, psid, puid, params) => axios.put(`/v1/business/dealer/unit/edit/${oid}/${pid}/${psid}/${puid}`, qs.stringify(params))
// 根据产品单位ID查询单位信息
export const dealerUnitInfo = (vid, pid, psid, puid) => axios.get(`/v1/business/dealer/unit/info/${vid}/${pid}/${psid}/${puid}`)
// 搜索待审核产品列表
export const tmpListAPI = (params) => axios.get(`/v1/business/tmp/list`, params)
// 检查产品名称是否存在
export const productCheckAPI = (params) => axios.get(`/v1/platform/product/check`, params)
// 添加待审核产品
export const tmpAddAPI = params => axios.post(`/v1/business/tmp/compose/add`, params)
// 修改待审核产品信息
export const tmpEditAPI = params => axios.put(`/v1/business/tmp/edit`, qs.stringify(params))
// 修改待审核产品信息经销商
export const tmpComposeEditAPI = params => axios.put(`/v1/business/tmp/compose/edit`, params)
// 删除待审核产品信息
export const tmpDelAPI = params => axios.delete(`/v1/business/tmp/del`, params)
// 获取产品规格型号列表
export const tmpSpecListAPI = (params) => axios.get(`/v1/business/tmp/spec/list`, params)
// 新增规格型号
export const tmpSpecAddAPI = params => axios.post(`/v1/business/tmp/spec/add`, qs.stringify(params))
// 修改规格型号
export const tmpSpecEditAPI = params => axios.put(`/v1/business/tmp/spec/edit`, qs.stringify(params))
// 删除规格型号
export const tmpSpecDelAPI = params => axios.delete(`/v1/business/tmp/spec/del`, params)
// 获取产品图片
export const tmpImageListAPI = (params) => axios.get(`/v1/business/tmp/image/list`, params)
// 新增产品图片
export const tmpImageAddAPI = params => axios.post(`/v1/business/tmp/image/add`, qs.stringify(params))
// 删除产品图片
export const tmpImageDelAPI = params => axios.delete(`/v1/business/tmp/image/del`, params)
// 获取产品医疗器材注册登记表列表
export const tmpMdrfListAPI = (params) => axios.get(`/v1/business/tmp/mdrf/list`, params)
// 新增医疗器材注册登记表
export const tmpMdrfAddAPI = params => axios.post(`/v1/business/tmp/mdrf/add`, params)
// 修改医疗器材注册登记表
export const tmpMdrfEditAPI = (params) => axios.put(`/v1/business/tmp/mdrf/edit`, params)
// 获取产品医疗器材注册登记表
export const tmpMdrfInfoAPI = (params) => axios.get(`/v1/business/tmp/mdrf/info`, params)
// 删除医疗器材注册登记表
export const tmpMdrfDelAPI = params => axios.delete(`/v1/business/tmp/mdrf/del`, params)
// 送审
export const tmpSendAuditAPI = params => axios.put(`/v1/business/tmp/sendAudit`, qs.stringify(params))
// 获取待审核产品列表
export const productTmpListAPI = (params) => axios.get(`/v1/platform/product/tmp/list`, params)
// 获取待审核产品关联信息
export const productTmpOtherInfoAPI = (params) => axios.get(`/v1/platform/product/tmp/otherInfo`, params)
// 获取待审核产品详细信息(包含规格型号、图片和登记表信息)
export const productTmpDetailAPI = (params) => axios.get(`/v1/platform/product/tmp/detail`, params)
// 审核产品
export const productAuditAPI = params => axios.post(`/v1/platform/product/audit`, qs.stringify(params))

/* =============== 经销商/厂家/医院 三端公共接口区域 ================== */
// 获取所有可用的机构列表(modal tab组件专用)
export const commAllOrgAPI = (systemType, params) => axios.get(`/v1/comm/${systemType}/allOrg`, params)
// 根据机构ID查询所有机构【所有部门】
export const orgDeptAllTreeAPI = (systemType, id) => axios.get(`/v1/business/org/${systemType}/dept/topTree/${id}`)
// 根据机构ID查询经销商【所有部门】
export const orgDeptTopTreeAPI = () => axios.get(`/v1/business/org/${VueCookie.get('userSystemType')}/dept/topTree/${VueCookie.get('userbelong')}`)
// 根据机构ID查询经销商【所有一级部门】
export const orgDeptTopParentListAPI = () => axios.get(`/v1/business/org/${VueCookie.get('userSystemType')}/dept/toplist/${VueCookie.get('userbelong')}`)
// 根据机构ID和上级部门ID查询经销商【所有下级部门】
export const orgDeptTopNextListAPI = (pid) => axios.get(`/v1/business/org/${VueCookie.get('userSystemType')}/dept/toplist/${VueCookie.get('userbelong')}/${pid}`)
// 根据机构ID和部门ID查询经销商【部门详情】
export const orgDeptInfoAPI = (did) => axios.get(`/v1/business/org/${VueCookie.get('userSystemType')}/dept/info/${VueCookie.get('userbelong')}/${did}`)
// 【新增】经销商部门
export const orgDeptAddAPI = (params) => axios.post(`/v1/business/org/${VueCookie.get('userSystemType')}/dept/add/${VueCookie.get('userbelong')}`, qs.stringify(params))
// 【删除】经销商部门
export const orgDeptDelAPI = (did) => axios.delete(`/v1/business/org/${VueCookie.get('userSystemType')}/dept/del/${VueCookie.get('userbelong')}/${did}`)
// 【编辑】经销商部门
export const orgDeptEditAPI = (did, params) => axios.put(`/v1/business/org/${VueCookie.get('userSystemType')}/dept/edit/${VueCookie.get('userbelong')}/${did}`, qs.stringify(params))
// 【新增】机构人员
export const orgEmpAddAPI = (params) => axios.post(`/v1/business/org/${VueCookie.get('userSystemType')}/emp/add/${VueCookie.get('userbelong')}`, qs.stringify(params))
// 【删除】机构人员
export const orgEmpDelAPI = (eid) => axios.delete(`/v1/business/org/${VueCookie.get('userSystemType')}/emp/del/${VueCookie.get('userbelong')}/${eid}`)
// 【编辑】机构人员
export const orgEmpEditAPI = (eid, params) => axios.put(`/v1/business/org/${VueCookie.get('userSystemType')}/emp/edit/${VueCookie.get('userbelong')}/${eid}`, qs.stringify(params))
// 根据机构ID和人员ID【查询机构人员详情】
export const orgEmpInfoAPI = (eid) => axios.get(`/v1/business/org/${VueCookie.get('userSystemType')}/emp/info/${VueCookie.get('userbelong')}/${eid}`)
// 根据机构ID查询机构【所有人员】 = 列表
export const orgEmpListAPI = (params) => axios.get(`/v1/business/org/${VueCookie.get('userSystemType')}/emp/list/${VueCookie.get('userbelong')}`, params)
// 【查询机构所有人员】用于下列列表
export const orgEmpDropdownListAPI = () => axios.get(`/v1/business/org/${VueCookie.get('userSystemType')}/emp/dropdownlist/${VueCookie.get('userbelong')}`)
// 【查询机构所有人员】用于下列列表
export const orgAllEmpDropdownListAPI = (systemType, oid) => axios.get(`/v1/business/org/${systemType}/emp/dropdownlist/${oid}`)
// 所有【员工规划图】接口 = 规划图
export const orgEmpTopTreeAPI = (userSystemType, userbelong) => axios.get(`/v1/business/org/${userSystemType || VueCookie.get('userSystemType')}/emp/topTree/${userbelong || VueCookie.get('userbelong')}`)
// 根据机构ID【查询机构所有一级仓库】
export const warehouseParentTopListAPI = () => axios.get(`/v1/business/warehouse/${VueCookie.get('userSystemType')}/toplist/${VueCookie.get('userbelong')}`)
// 【查询所有可用的机构一级仓库】
export const warehouseUsableTopListAPI = () => axios.get(`/v1/business/warehouse/usable/toplist/${VueCookie.get('userbelong')}`)
// 根据机构ID和仓库ID【查询所有下级仓库】
export const warehouseChildrenListAPI = (wid) => axios.get(`/v1/business/warehouse/${VueCookie.get('userSystemType')}/list/${VueCookie.get('userbelong')}/${wid}`)
// 根据机构ID和仓库ID【查询仓库信息】【注：仅拿当前仓库的这棵tree】
export const warehouseInfoAPI = (wid) => axios.get(`/v1/business/warehouse/${VueCookie.get('userSystemType')}/info/${VueCookie.get('userbelong')}/${wid}`)
// 【编辑】机构仓库
export const warehouseEditAPI = (wid, params) => axios.put(`/v1/business/warehouse/${VueCookie.get('userSystemType')}/edit/${VueCookie.get('userbelong')}/${wid}`, params)
// 【删除】机构仓库
export const warehouseDelAPI = (wid) => axios.delete(`/v1/business/warehouse/${VueCookie.get('userSystemType')}/del/${VueCookie.get('userbelong')}/${wid}`)
// 【新增】机构仓库
export const warehouseAddAPI = (params) => axios.post(`/v1/business/warehouse/${VueCookie.get('userSystemType')}/add/${VueCookie.get('userbelong')}`, params)
// 【编辑】机构仓库仓库
export const warehouseTypeEditAPI = (params) => axios.put(`/v1/business/warehouse/type/${VueCookie.get('userSystemType')}/edit`, qs.stringify(params))
// 【删除】机构仓库仓库
export const warehouseTypeDelAPI = (wid) => axios.delete(`/v1/business/warehouse/type/${VueCookie.get('userSystemType')}/del/${VueCookie.get('userbelong')}/${wid}`)
// 【新增】机构仓库仓库
export const warehouseTypeAddAPI = (params) => axios.post(`/v1/business/warehouse/type/${VueCookie.get('userSystemType')}/add`, qs.stringify(params))
// 【查询】机构仓库类型
export const warehouseTypeListAPI = () => axios.get(`/v1/business/warehouse/type/${VueCookie.get('userSystemType')}/list/${VueCookie.get('userbelong')}`)
// 【查询】可用机构仓库类型
export const warehouseTypeUsableListAPI = () => axios.get(`/v1/business/warehouse/type/usable/list/${VueCookie.get('userbelong')}`)
// 【树状显示所有可用仓库】
export const warehouseUsableTreeAPI = () => axios.get(`/v1/business/warehouse/usable/tree/${VueCookie.get('userbelong')}`)
// 【检测】仓库编码是否有重复
export const warehouseCheckAPI = (params) => axios.get(`/v1/business/warehouse/${VueCookie.get('userSystemType')}/check/${VueCookie.get('userbelong')}`, params)
// 查看草稿合同列表
export const contractdraftlistAPI = (params) => axios.get(`/v1/business/contract/draf/list`, params)
// 添加合同
export const contractAddAPI = (params) => axios.post(`/v1/business/contract/add`, params)
// 修改合同
export const contractEditAPI = (params) => axios.put(`/v1/business/contract/edit`, params)
// 查看合同信息
export const contractInfoAPI = (params) => axios.get(`/v1/business/contract/info`, params)
// 删除合同信息
export const contractDelAPI = (params) => axios.delete(`/v1/business/contract/del`, params)
// 添加审批
export const approveAddAPI = (params) => axios.post(`/v1/business/contract/approve/add`, params)
// 查看审批合同列表
export const contractApprovelistAPI = (params) => axios.get(`/v1/business/contract/approve/list`, params)
// 查看合同审批备注列表
export const contractApproveCommentlistAPI = (params) => axios.get(`/v1/business/contract/approve/comment/list`, params)
// 根据机构ID获取所有登录用户账号列表(下列组件专用)
export const commUserAPI = (systemType, oid) => axios.get(`/v1/comm/user/${systemType}/list/${oid}`)
// 搜索所有可以添加到合同的厂商产品
export const productListContract = (systemType, oid, source, params) => axios.get(`/v1/platform/product/dealer/list/contract/${systemType}/${oid}`, params)
// 搜索所有可以添加到合同的经销商商自建商品
export const productDealerListContractAPI = (systemType, oid, params) => axios.get(`/v1/platform/product/dealer/list/contract/${systemType}/${oid}`, params)
// 搜索所有可以添加到合同的平台厂商产品
export const productVendorListContractAPI = (systemType, oid, params) => axios.get(`/v1/platform/product/vendor/list/contract/${systemType}/${oid}`, params)
// 根据条件查看合同列表
export const contractListAPI = (systemType, params) => axios.get(`/v1/business/contract/list/${systemType}`, params)
// 审批合同
export const contractApproveAPI = (params) => axios.put(`/v1/business/contract/approve/approve`, qs.stringify(params))
// 添加合同审批备注
export const approveCommentAddAPI = (params) => axios.post(`/v1/business/contract/approve/comment/add`, qs.stringify(params))
// 补录合同
export const contractMakeupAPI = (params) => axios.post(`/v1/business/contract/makeup`, params)
// 中止合同
export const contractStopAPI = (params) => axios.put(`/v1/business/contract/stop`, qs.stringify(params))
// 【获取】经销商自建产品列表
export const initialDealerListAPI = (params) => axios.get(`/v1/business/initial/${VueCookie.get('userSystemType')}/dealer/list`, params)
// 【查询】期初库存
export const initialInfoAPI = (wid) => axios.get(`/v1/business/initial/${VueCookie.get('userSystemType')}/info/${VueCookie.get('userbelong')}/${wid}`)
// 【查询】期初库存产品明细列表
export const initialDetailListAPI = (params) => axios.get(`/v1/business/initial/${VueCookie.get('userSystemType')}/detail/list`, params)
// 【新增】期初库存
export const initialAddAPI = (params) => axios.post(`/v1/business/initial/${VueCookie.get('userSystemType')}/add`, params)
// 【新增】期初库存产品明细
export const initialDetailAddAPI = (params) => axios.post(`/v1/business/initial/${VueCookie.get('userSystemType')}/detail/add`, qs.stringify(params))
// 【删除】期初库存产品明细
export const initialDetailDelAPI = (params) => axios.delete(`/v1/business/initial/${VueCookie.get('userSystemType')}/detail/del`, { params })
// 【删除】期初库存产品规格型号
export const initialDelAPI = (params) => axios.delete(`/v1/business/initial/${VueCookie.get('userSystemType')}/del`, { params })
// 期初库存【过账】
export const initialPostAPI = (wid) => axios.put(`/v1/business/initial/${VueCookie.get('userSystemType')}/post/${VueCookie.get('userbelong')}/${wid}`)
// 【更新】期初库存产品明细
export const initialDetailEditAPI = (params) => axios.put(`/v1/business/initial/${VueCookie.get('userSystemType')}/detail/edit`, qs.stringify(params))
// 查询对经销商的询价列表
export const inquiryDealerList = (params) => axios.get(`/v1/business/inquiry/dealer/list/${VueCookie.get('userbelong')}`, params)
// 查询对厂商的询价列表
export const inquiryVendorList = (params) => axios.get(`/v1/business/inquiry/vendor/list/${VueCookie.get('userbelong')}`, params)
// 查询待回复列表
export const inquiryUnreplyList = (all, params) => axios.get(`/v1/business/inquiry/unreply/list/${VueCookie.get('userbelong')}/${all}`, params)
// 新增询价单
export const inquiryAddAPI = (params) => axios.post(`/v1/business/inquiry/add`, params)
// 编辑询价单
export const inquiryEditAPI = (params) => axios.put(`/v1/business/inquiry/edit`, params)
// 询价单详情
export const inquiryInfoAPI = (params) => axios.get(`/v1/business/inquiry/info`, params)
// 删除询价单
export const inquiryDelAPI = (params) => axios.delete(`/v1/business/inquiry/del`, params)
// 询价单价格查询
export const inquiryPriceListAPI = (params) => axios.get(`/v1/business/inquiry/price/list`, params)
// 获取厂商产品列表【询价】
export const inquiryProductListAPI = (params) => axios.get(`/v1/business/inquiry/product/list`, params)
// 资证管理 - 已签约经销商列表
export const licManageDealer = (params) => axios.get(`/v1/business/cert/dealer/list`, params)
// 资证管理 - 已签约医院列表
export const licManageHospital = (params) => axios.get(`/v1/business/cert/hospital/list`, params)
// 资证管理 - 已签约厂商列表
export const licManageVendor = (params) => axios.get(`/v1/business/cert/vendor/list`, params)
// 资证管理 - 删除图片
export const licDeleteImg = (systemType, id, certType, iid) => axios.delete(`/v1/platform/org/image/del/${systemType}/${id}/${certType}/${iid}`)
// 资证管理 - 申请认证
export const licApplyApproval = (oid) => axios.put(`/v1/business/cert/applicationforCertification/${oid}`)
// 查看【采购订单列表】
export const orderPurchaseListAPI = (params) => axios.get(`/v1/business/order/purchase/list`, params)
// 查看【历史采购订单列表】
export const orderHistoryPurchaseListAPI = (params) => axios.get(`/v1/business/order/history/purchase/list`, params)
// 查看【采购订单信息】
export const orderPurchaseInfoAPI = (params) => axios.get(`/v1/business/order/purchase/info`, params)
// 【相关机构列表】
export const orderOrgListAPI = (systemType, oid, params) => axios.get(`/v1/business/order/org/list/${systemType}/${oid}`, params)
// 【添加采购订单】
export const orderPurchaseAddAPI = (params) => axios.post(`/v1/business/order/purchase/add`, params)
// 【经销商自建产品列表】
export const orderDealerListAPI = (params) => axios.get(`/v1/business/order/dealer/list`, params)
// 【厂商产品列表】
export const orderVendorListAPI = (params) => axios.get(`/v1/business/order/vendor/list`, params)
// 【删除】订单
export const orderDelAPI = (params) => axios.delete(`/v1/business/order/del`, { data: params })
// 【删除】订单明细
export const orderDetailDelAPI = (params) => axios.delete(`/v1/business/order/detail/del`, { params })
// 【修改】采购订单信息
export const orderPurchaseEditAPI = (params) => axios.put(`/v1/business/order/purchase/edit`, params)
// 查看【销售订单列表】
export const orderSaleListAPI = (params) => axios.get(`/v1/business/order/sale/list`, params)
// 查看【历史销售订单列表】
export const orderHistorySaleListAPI = (params) => axios.get(`/v1/business/order/history/sale/list`, params)
// 查看【销售订单信息】
export const orderSaleInfoAPI = (params) => axios.get(`/v1/business/order/sale/info`, params)
// 【添加销售订单】
export const orderSaleAddAPI = (params) => axios.post(`/v1/business/order/sale/add`, params)
// 查看【销售单明细列表】
export const orderSaleRecordListAPI = (params) => axios.get(`/v1/business/order/sale/record/list`, params)
// 【修改】销售订单信息
export const orderSaleEditAPI = (params) => axios.put(`/v1/business/order/sale/edit`, params)
// 【确认订单】
export const orderConfirmAPI = (params) => axios.put(`/v1/business/order/confirm`, params)
// 【确认采购订单】
export const orderPurchaseConfirmAPI = (params) => axios.put(`/v1/business/order/purchase/confirm`, qs.stringify(params))
// 【采购记录查询】
export const orderPurchaseRecordListAPI = (params, pageNum, pageSize) => axios.post(`/v1/business/order/purchase/record/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 【导出采购记录】
export const orderPurchaseRecordExportAPI = (params) => axios.post(`/v1/business/order/purchase/record/export`, params, { timeout: LongTimeout, responseType: 'blob' })
// 【确认销售订单】
export const orderSaleConfirmAPI = (params) => axios.put(`/v1/business/order/sale/confirm`, qs.stringify(params))
// 查看【销售订单明细信息】
export const orderSaleDetailAPI = (params) => axios.post(`/v1/business/order/sale/detail`, params)
// 查看列表【销售管理】【销售单】
export const saleRecordSearchAPI = (params) => axios.get(`/v1/business/order/sale/record/search`, params)
// 查看单条【销售管理】【销售单】
export const saleRecordInfoAPI = (params) => axios.get(`/v1/business/order/sale/record/info`, params)
// 查看单条【销售管理】【销售单】
export const saleRecordDetailAPI = (params) => axios.get(`/v1/business/order/sale/record/detail`, params)
// 删除【销售管理】【销售单】
export const saleRecordDelAPI = (params) => axios.delete(`/v1/business/order/sale/record/del`, params)
// 新增【销售管理】【销售单】
export const saleRecordAddAPI = (params) => axios.post(`/v1/business/order/sale/record/add`, params)
// 编辑【销售管理】【销售单】
export const saleRecordEditAPI = (params) => axios.put(`/v1/business/order/sale/record/edit`, params)
// 【保存销售单】
export const orderSaleBuildAPI = (params) => axios.put(`/v1/business/order/sale/build`, params)
// 【终止订单】
export const orderStopAPI = (params) => axios.put(`/v1/business/order/stop`, params)
// 【勾选计划明细】生成采购订单
export const orderSaleDetailBuildAPI = (params) => axios.put(`/v1/business/order/sale/detail/build`, params)
// 【骨科】搜索合作意向列表
export const intentionList = (params) => axios.get(`/v1/platform/intention/list`, params)
// 【骨科】新增骨科合作意向
export const intentionAddAPI = (params) => axios.post(`/v1/platform/intention/add`, params)
// 【骨科】修改骨科合作意向
export const intentionEditAPI = (params) => axios.put(`/v1/platform/intention/edit`, params)
// 【骨科】删除骨科合作意向
export const intentionDelAPI = (hid, did) => axios.delete(`/v1/platform/intention/del/${hid}/${did}`)
// 【骨科】获取合作意向信息
export const intentionInfoAPI = (hid, did) => axios.get(`/v1/platform/intention/info/${hid}/${did}`)
// 【骨科】获取骨科工具包列表
export const surgeryKitListAPI = (params) => axios.get(`/v1/platform/surgery/kit/list`, params)
// 【骨科】新增骨科工具包
export const surgeryKitAddAPI = (params) => axios.post(`/v1/platform/surgery/kit/add`, params)
// 【骨科】获取骨科工具包信息
export const surgeryKitInfoAPI = (kid) => axios.get(`/v1/platform/surgery/kit/info/${kid}`)
// 【骨科】修改骨科工具包信息
export const surgeryKitEditAPI = (params) => axios.put(`/v1/platform/surgery/kit/edit`, params)
// 【骨科】 删除骨科工具包
export const surgeryKitDelAPI = (kid) => axios.delete(`/v1/platform/surgery/kit/del/${kid}`)
// 【骨科】获取骨科手术包列表
export const surgeryPackageListAPI = (params) => axios.get(`/v1/platform/surgery/package/list`, params)
// 【骨科】新增骨科手术包
export const surgeryPackageAddAPI = (params) => axios.post(`/v1/platform/surgery/package/add`, params)
// 【骨科】获取骨科手术包信息
export const surgeryPackageInfoAPI = (pid) => axios.get(`/v1/platform/surgery/package/info/${pid}`)
// 【骨科】修改骨科手术包信息
export const surgeryPackageEditAPI = (params) => axios.put(`/v1/platform/surgery/package/edit`, params)
// 【骨科】 删除骨科手术包
export const surgeryPackageDelAPI = (pid) => axios.delete(`/v1/platform/surgery/package/del/${pid}`)
// 【骨科】获取骨科手术包品牌
export const surgeryBrandListAPI = () => axios.get(`/v1/platform/surgery/brand/list`)
// 【骨科】 新增骨科品牌
export const surgeryBrandAddAPI = (params) => axios.post(`/v1/platform/surgery/brand/add`, qs.stringify(params))
// 【骨科】修改骨科品牌
export const surgeryBrandEditAPI = (params) => axios.put(`/v1/platform/surgery/brand/edit`, qs.stringify(params))
// 【骨科】 删除骨科品牌
export const surgeryBrandDelAPI = (bid) => axios.delete(`/v1/platform/surgery/brand/del/${bid}`)
// 【骨科】 获取顶级骨科路径
export const surgeryPathTopAPI = () => axios.get(`/v1/platform/surgery/path/top`)
// 【骨科】 新增骨科路径
export const surgeryPathAddAPI = (params) => axios.post(`/v1/platform/surgery/path/add`, qs.stringify(params))
// 【骨科】获取骨科路径信息
export const surgeryPathInfoAPI = (pid, params) => axios.get(`/v1/platform/surgery/path/info/${pid}`, params)
// 【骨科】 修改骨科路径信息
export const surgeryPathEditAPI = (params) => axios.put(`/v1/platform/surgery/path/edit`, qs.stringify(params))
// 【骨科】获取下级骨科路径
export const surgeryPathChildAPI = (pid) => axios.get(`/v1/platform/surgery/path/child/${pid}`)
// 【骨科】删除骨科路径
export const surgeryPathDelAPI = (pid) => axios.delete(`/v1/platform/surgery/path/del/${pid}`)
// 【骨科】关联手术包到骨科路径
export const surgeryPathRelpackageAPI = (params) => axios.post(`/v1/platform/surgery/path/relpackage`, qs.stringify(params))
// 【骨科】删除骨科路径上关联的手术包
export const surgeryPathUnrelpackageAPI = (params) => axios.delete(`/v1/platform/surgery/path/unrelpackage`, { params })
// 【骨科】获取厂商产品列表
export const surgeryVendorListAPI = (params) => axios.get(`/v1/platform/surgery/vendor/list`, params)
// 【终止订单明细】
export const orderDetailStopAPI = (params) => axios.put(`/v1/business/order/detail/stop`, qs.stringify(params))
// 【反终止订单明细】
export const orderDetailUnstopAPI = (params) => axios.put(`/v1/business/order/detail/unstop`, qs.stringify(params))
// 【搜索】入库单列表
export const grnListAPI = (params) => axios.get(`/v1/business/grn/list`, { ...params, timeout: LongTimeout })
// 【搜索】【入库单】【储位分配单】列表
export const stoListAPI = (params) => axios.get(`/v1/business/sas/list`, params)
// 【入库单】【储位分配单】单条详情
export const stoInfoAPI = (params) => axios.get(`/v1/business/sas/info`, params)
// 【入库单】【储位分配单】检测是否存在
export const stoCheckAPI = (params) => axios.get(`/v1/business/sas/check`, params)
// 【入库单】【储位分配单】获取可生成储位分配单明细产品信息
export const stoGetInfoAPI = (params) => axios.get(`/v1/business/sas/product/list`, params)
// 【入库单】【储位分配单】删除
export const stoDelAPI = (params) => axios.delete(`/v1/business/sas/del`, { params })
// 获取【入库单详情】
export const grnInfoAPI = (params) => axios.get(`/v1/business/grn/info`, params)
// 【编辑】入库单
export const grnEditAPI = (params) => axios.put(`/v1/business/grn/edit`, params)
// 【删除】入库单
export const grnDelAPI = (params) => axios.delete(`/v1/business/grn/del`, { params })
// 【新增】入库单
export const grnAddAPI = (params) => axios.post(`/v1/business/grn/add`, params)
// 【反审核入库单】
export const grnUnreviewAPI = (params) => axios.put(`/v1/business/grn/unreview`, params)
// 【红冲入库单】
export const grnRedDashedAPI = (params) => axios.put(`/v1/business/grn/red`, params)
// 【入库】
export const grnPutinAPI = (params) => axios.put(`/v1/business/grn/putin`, params)
// 【入库】采购退货
export const purchaseReturnPutinAPI = (params) => axios.put(`/v1/business/purchase/return/putin`, params)
// 【获取储位分配单信息】
export const grnStorageAPI = (oid, wid, beginInventory, endInventory) => axios.get(`/v1/business/grn/storage/${oid}/${wid}/${beginInventory}/${endInventory}`)
// 【新增】储位分配单
export const sasAddAPI = (params) => axios.post(`/v1/business/sas/add`, params)
// 【编辑】储位分配单
export const sasEditAPI = (params) => axios.put(`/v1/business/sas/edit`, params)
// 入库单 - 【生成条码】
export const grnBarcodeCreateAPI = (params) => axios.get(`/v1/business/grn/barcode/create`, params)
// 入库单 - 【获取产品条码信息】
export const grnBarcodeInfoAPI = (params) => axios.get(`/v1/business/grn/barcode/info`, params)
// 入库单 - 【保存条码】
export const grnBarcodeSaveAPI = (params) => axios.post(`/v1/business/grn/barcode/save`, params)
// 【新增】入库单类型
export const grnTypeAddAPI = (params) => axios.post(`/v1/business/grn/type/add`, qs.stringify(params))
// 【删除】入库单类型
export const grnTypeDelAPI = (oid, id) => axios.delete(`/v1/business/grn/type/del/${oid}/${id}`)
// 【编辑】入库单类型
export const grnTypeEditAPI = (params) => axios.put(`/v1/business/grn/type/edit`, qs.stringify(params))
// 【查询入库单类型列表】
export const grnTypeListAPI = (params) => axios.get(`/v1/business/grn/type/list/${VueCookie.get('userbelong')}`, params)
// 【获取仓库人员】
export const grnWeListAPI = (params) => axios.get(`/v1/business/grn/we/list/${VueCookie.get('userbelong')}`, params)
// 【相关机构列表】
export const grnOrgListAPI = (systemType, oid, params) => axios.get(`/v1/business/grn/org/list/${systemType}/${oid}`, params)
// 【获取厂商产品列表】
export const grnVendorListAPI = (params) => axios.get(`/v1/business/grn/vendor/list`, params)
// 【获取经销商自建产品列表】
export const grnDealerListAPI = (params) => axios.get(`/v1/business/grn/dealer/list`, params)
// 【获取厂商注册证号的下拉列表】
export const platformProductMdrfListAPI = (vid, pid) => axios.get(`/v1/platform/product/mdrf/list/${vid}/${pid}`)
// 【获取产品医疗器材最新注册登记表】
export const platformProductMdrfNewestListAPI = (vid, pid) => axios.get(`/v1/platform/product/mdrf/newest/info/${vid}/${pid}`)
// 【资证备案】效期预警-机构预警设置信息
export const warningInfoAPI = (oid) => axios.get(`/v1/business/warning/info/${oid}`)
// 【资证备案】效期预警-效期检测
export const cewlistAPI = () => axios.put(`/v1/business/warning/cewlist`)
// 【资证备案】效期预警-预警设置
export const waringAddAPI = (params) => axios.post(`/v1/business/warning/add`, qs.stringify(params))
// 【资证备案】效期预警-发送过期警告
export const sendWaringAPI = (params) => axios.post(`/v1/business/warning/sendWarning`, qs.stringify(params))
// 【搜索】出库单列表
export const odoListAPI = (params) => axios.get(`/v1/business/odo/list`, { ...params, timeout: LongTimeout })
// 获取【出库单详情】
export const odoInfoAPI = (params) => axios.get(`/v1/business/odo/info`, params)
// 获取【仓库内产品批号列表】
export const odoBatchListAPI = (params) => axios.post(`/v1/business/odo/batch/list`, params)
// 新获取【仓库内产品批号列表】
export const planBatchListAPI = (params) => axios.post(`/v1/business/plan/batch/list`, params)
// 【编辑】出库单
export const odoEditAPI = (params) => axios.put(`/v1/business/odo/edit`, params)
// 【删除】出库单
export const odoDelAPI = (params) => axios.delete(`/v1/business/odo/del`, { params })
// 【新增】出库单
export const odoAddAPI = (params) => axios.post(`/v1/business/odo/add`, params)
// 【出库】
export const odoPutoutAPI = (params) => axios.put(`/v1/business/odo/putout`, params)
// 【出库】销售退货
export const saleReturnPutoutAPI = (params) => axios.put(`/v1/business/sale/return/putout`, params)
// 【新增】出库单类型
export const odoTypeAddAPI = (params) => axios.post(`/v1/business/odo/type/add`, qs.stringify(params))
// 【删除】出库单类型
export const odoTypeDelAPI = (oid, id) => axios.delete(`/v1/business/odo/type/del/${oid}/${id}`)
// 【编辑】出库单类型
export const odoTypeEditAPI = (params) => axios.put(`/v1/business/odo/type/edit`, qs.stringify(params))
// 【查询出库单类型列表】
export const odoTypeListAPI = (params) => axios.get(`/v1/business/odo/type/list/${VueCookie.get('userbelong')}`, params)
// 【复核出库单】
export const odoReviewAPI = (params) => axios.put(`/v1/business/odo/review`, qs.stringify(params))
// 【反审核出库单】
export const odoUnreviewAPI = (params) => axios.put(`/v1/business/odo/unreview`, params)
// 【红冲出库单】
export const odoRedDashedAPI = (params) => axios.put(`/v1/business/odo/red`, params)
// 【获取出库单可调单类型列表】
export const odoSheetListAPI = (params) => axios.get(`/v1/business/odo/sheet/list`, params)
// 【新增】发放单生成出库单
export const hospitalGrantCreatodoAPI = (oid, sid, params) => axios.post(`/v1/business/hospital/grant/creatodo/${oid}/${sid}`, params)
// 【新增】新增销售单生成出库单
export const saleRecordCreatodoAPI = (oid, sid, params) => axios.post(`/v1/business/order/sale/record/creatodo/${oid}/${sid}`, params)
// 【打印】出库单打印计数
export const odoPrintCountAPI = (params) => axios.put('/v1/business/odo/printed', undefined, {params})
// 【打印】调拨出库单打印计数
export const odoAllotPrintCountAPI = (params) => axios.put('/v1/business/odo/printed', undefined, {params})
// 【打印】采购退货单打印计数
export const odoPurchasePrintCountAPI = (params) => axios.put('/v1/business/purchase/return/printed', undefined, {params})
// 【打印】销售出库单打印计数
export const odoSalePrintCountAPI = (params) => axios.put('/v1/business/sale/odo/printed', undefined, {params})
// 【打印】销售退货单打印计数
export const odoSaleReturnPrintCountAPI = (params) => axios.put('/v1/business/sale/return/printed', undefined, {params})
// 【打印】赠送单打印计数
export const odoGivePrintCountAPI = (params) => axios.put('/v1/business/give/odo/printed', undefined, {params})
// 【打印】领用出库单打印计数
export const odoGrantPrintCountAPI = (params) => axios.put('/v1/business/grant/odo/printed', undefined, {params})
// 【获取仓库人员】
export const odoWeListAPI = (params) => axios.get(`/v1/business/odo/we/list/${VueCookie.get('userbelong')}`, params)
// 【相关机构列表】
export const odoOrgListAPI = (systemType, oid, params) => axios.get(`/v1/business/odo/org/list/${systemType}/${oid}`, params)
// 【获取经销商自建产品列表】
export const odoDealerListAPI = (params) => axios.get(`/v1/business/odo/dealer/list`, params)
// 【获取厂商产品列表】
export const odoVendorListAPI = (params) => axios.get(`/v1/business/odo/vendor/list`, params)
// 【获取产品编号列表(医保编号或者阳光流水号)】
export const platforProductSnListAPI = (params) => axios.get(`/v1/business/product/sn/list`, params)
export const productSnListAPI = (params) => axios.post(`/v1/business/odo/healthCareSn/list`, params)
// 【新增产品编号(医保编号或者阳光流水号)】
export const productSnAddAPI = (params) => axios.post(`/v1/business/product/sn/add`, params)
// 【编辑产品编号(医保编号或者阳光流水号)】
export const productSnEditAPI = (params) => axios.post(`/v1/business/product/sn/edit`, params)
// 【删除产品编号(医保编号或者阳光流水号)】
export const productSnDelAPI = (params) => axios.delete(`/v1/business/product/sn/del`, params)
/* =============== 经销商/医院 两端公共接口区域 ================== */
// 【未执行】采购计划列表
export const planUnexecutedListAPI = (params) => axios.get(`/v1/business/plan/unexecuted/list/${VueCookie.get('userbelong')}`, params)
// 【过往】采购计划列表
export const planHistoryListAPI = (params) => axios.get(`/v1/business/plan/history/list/${VueCookie.get('userbelong')}`, params)
// 【终止计划】
export const planStopAPI = (params) => axios.put(`/v1/business/plan/stop`, params)
// 【新增】采购计划
export const planAddAPI = (params) => axios.post(`/v1/business/plan/add`, params)
// 【删除】采购计划
export const planDelAPI = (params) => axios.delete(`/v1/business/plan/del`, {data: params})
// 【编辑】采购计划
export const planEditAPI = (params) => axios.put(`/v1/business/plan/edit`, params)
// 采购计划【详情】
export const planInfoAPI = (params) => axios.get(`/v1/business/plan/info`, params)
// 【厂商】产品列表
export const planVendorListAPI = (params) => axios.get(`/v1/business/plan/vendor/list/${VueCookie.get('userbelong')}`, params)
// 【经销商】自建产品列表
export const planDealerListAPI = (params) => axios.get(`/v1/business/plan/dealer/list/${VueCookie.get('userbelong')}`, params)
// 【勾选计划明细】生成采购订单
export const planDetailBuildAPI = (params) => axios.put(`/v1/business/plan/detail/build`, params)
// 【删除】计划明细
export const planDetailDelAPI = (params) => axios.delete(`/v1/business/plan/detail/del`, { params })
// 【终止】计划明细
export const planDetailStopAPI = (params) => axios.put(`/v1/business/plan/detail/stop`, qs.stringify(params))
// 【反终止】计划明细
export const planDetailUnstopAPI = (params) => axios.put(`/v1/business/plan/detail/unstop`, qs.stringify(params))
// 【按级别查询库存产品】
export const inventoryListLevelAPI = (level, params) => axios.get(`/v1/business/inventory/list/${level}`, { ...params, timeout: LongTimeout })
// 【计算】采购订单数据
export const planCreateAPI = (oid, params) => axios.post(`/v1/business/plan/create/${oid}`, params)
// 【勾选计划明细计算】采购订单数据
export const planDetailCreateAPI = (oid, params) => axios.post(`/v1/business/plan/detail/create/${oid}`, params)
// 【生成】采购订单
export const planBuildAPI = (params) => axios.post(`/v1/business/plan/build`, params)
// 【验货】单个详情
export const insInfoAPI = (params) => axios.get(`/v1/business/pp/info`, params)
// 【验货-新增】
export const insAddAPI = (params) => axios.post(`/v1/business/pp/add`, params)
// 【验货-删除】
export const insDelAPI = (params) => axios.delete(`/v1/business/pp/del`, { params })
// 【验货-编辑】
export const insEditAPI = (params) => axios.put(`/v1/business/pp/edit`, params)
// 【验货-保存条码】
export const insBarcodeSaveAPI = (params) => axios.post(`/v1/business/pp/barcode/save`, params)
// 【验货-列表】
export const insListAPI = (params) => axios.get(`/v1/business/pp/list`, params)
// 【验货-获取产品条码信息】
export const insBarcodeInfoAPI = (params) => axios.get(`/v1/business/pp/barcode/info`, params)
// 【验货-获取经销商自建产品列表】
export const insDealerListAPI = (params) => axios.get(`/v1/business/pp/dealer/list`, params)
// 【生成】扫描条码获取产品信息
export const barcodeScanAPI = (params) => axios.post(`/v1/business/grn/barcode/scan`, qs.stringify(params))
// 【生成】扫描条码获取产品信息
export const barcodeScanAPIinventory = (params) => axios.post(`/v1/business/inventory/barcode/scan`, qs.stringify(params))
// 【收货-关联单据类型】
export const refVoucherTypeAPI = () => axios.get(`/v1/dict/refVoucherType`)
// 【收货-列表】
export const receiptListAPI = (params) => axios.get(`/v1/business/receipt/list`, params)
// 【收货-列表状态】
export const receiptStatusAPI = () => axios.get(`/v1/dict/receiptStatus`)
// 【收货-新增】
export const receiptAddAPI = (params) => axios.post(`/v1/business/receipt/add`, params)
// 【收货-删除】
export const receiptDelAPI = (params) => axios.delete(`/v1/business/receipt/del`, {data: params})
// 【收货-查看单个】
export const receiptInfoAPI = (params) => axios.get(`/v1/business/receipt/info`, params)
// 【收货-编辑】
export const receiptEditAPI = (params) => axios.put(`/v1/business/receipt/edit`, params)
// 【收货】相关机构列表(分页)
export const receiptOrgListAPI = (systemType, oid, params) => axios.get(`/v1/business/receipt/org/list/${systemType}/${oid}`, params)
// 【收货-获取厂商产品列表(分页)】
export const receiptVendorListAPI = (params) => axios.get(`/v1/business/receipt/vendor/list`, params)
// 【收货-获取经销商自建产品列表(分页)】
export const receiptDealerListAPI = (params) => axios.get(`/v1/business/receipt/dealer/list`, params)
// 【收货-获取出库单可调单类型列表】
export const receiptSheetListAPI = (params) => axios.get(`/v1/business/receipt/sheet/list`, params)
// 【收货-可用做收货调单的销售单列表】
export const receiptTransferRecordListAPI = (params) => axios.get(`/v1/business/receipt/transfer/record/list`, params)
// 【收货-可用做收货调单的销售单详情】
export const receiptTransferRecordDetailAPI = (params) => axios.get(`/v1/business/receipt/transfer/record/detail`, params)
// 【出库-可用做出库调单的销售单列表】
export const odoTransferRecordListAPI = (params) => axios.get(`/v1/business/odo/transfer/record/list`, params)
// 【收货-被调单的销售单明细列表】
export const odoTransferRecordDetailAPI = (params) => axios.get(`/v1/business/odo/transfer/record/detail`, params)
// 【查询范围enum == 所属范围】
export const surgeryCustDictSearchScopeAPI = () => axios.get(`/v1/surgery/cust/dict/searchScope`)
// 【list骨科手术包列表(分页)】
export const surgeryCustDealerPackageListAPI = (ownerDealerInnerSn, params, pageNum, pageSize) => axios.post(`/v1/surgery/cust/dealer/package/list/${ownerDealerInnerSn}?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 【list骨科手术包列表(不需要分页)】
export const surgeryCustDealerPackageListNoPaginationAPI = (ownerDealerInnerSn, params) => axios.post(`/v1/surgery/cust/dealer/package/list/${ownerDealerInnerSn}`, params)
// 【添加定制手术包到路径下】
export const surgeryCustDealerPackageAddToPathAPI = (opInnerSn, params) => axios.post(`/v1/surgery/cust/dealer/package/addToPath/${opInnerSn}`, params)
// 【从路径下 移除定制手术包】
export const surgeryCustDealerPackageRemoveFromPathAPI = (opInnerSn, params) => axios.delete(`/v1/surgery/cust/dealer/package/removeFromPath/${opInnerSn}`, {data: params})
// 【获取骨科手术包】
export const surgeryCustDealePackageGetAPI = (id) => axios.get(`/v1/surgery/cust/dealer/package/get/${id}`)
// 【获取骨科手术包】
export const surgeryCustDealePackageGetPlatformPackageAPI = (platformOisId) => axios.get(`/v1/surgery/cust/dealer/package/getPlatformPackage/${platformOisId}`)
// 【手术包/工具包 状态 enum】
export const surgeryCustDictPackageStatusAPI = () => axios.get(`/v1/surgery/cust/dict/packageStatus`)
// 【删除定制手术包】
export const surgeryCustDealerPackageDelAPI = (id) => axios.delete(`/v1/surgery/cust/dealer/package/del/${id}`)
// 【保存定制手术包(owner_dealer_inner_sn/owner_dealer_name/platform_ois_inner_sn)】
export const surgeryCustDealerPackageSaveAPI = (params) => axios.put(`/v1/surgery/cust/dealer/package/save`, params)
// 【init手术包(以平台手术包为src, 生成经销商定制包--含工具包)】
export const surgeryCustDealerPackageInitAPI = (params) => axios.post(`/v1/surgery/cust/dealer/package/init`, params)
// 【list骨科工具包列表(分页)】
export const surgeryCustDealerKitListAPI = (ownerDealerInnerSn, params, pageNum, pageSize) => axios.post(`/v1/surgery/cust/dealer/kit/list/${ownerDealerInnerSn}?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 【list骨科工具包列表(不需要分页)】
export const surgeryCustDealerKitListNoPaginationAPI = (ownerDealerInnerSn, params) => axios.post(`/v1/surgery/cust/dealer/kit/list/${ownerDealerInnerSn}`, params)
// 【删除定制工具包】
export const surgeryCustDealerKitDelAPI = (id) => axios.delete(`/v1/surgery/cust/dealer/kit/del/${id}`)
// 【init工具包(以平台工具包为src, copy生成经销商定制工具包)】
export const surgeryCustDealerKitInitAPI = (params) => axios.post(`/v1/surgery/cust/dealer/kit/init`, params)
// 【获取骨科工具包】
export const surgeryCustDealerKitGetAPI = (id) => axios.get(`/v1/surgery/cust/dealer/kit/get/${id}`)
// 【获取骨科工具包】
export const surgeryCustDealePackageGetPlatformKitAPI = (platformKitId) => axios.get(`/v1/surgery/cust/dealer/kit/getPlatofrmKit/${platformKitId}`)
// 【保存定制工具包(owner_dealer_inner_sn/owner_dealer_name/platform_kit_inner_sn)】
export const surgeryCustDealerKitSaveAPI = (params) => axios.put(`/v1/surgery/cust/dealer/kit/save`, params)
// 【获取骨科品牌列表】
export const platformSurgeryBrandListAPI = () => axios.get(`/v1/platform/surgery/brand/list`)
// 【list手术plan列表(分页, 不含明细)】
export const surgeryPlanListAPI = (params, pageNum, pageSize) => axios.post(`/v1/surgery/plan/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 【意向内的可选hospital(特定经销商对应的): key=hospitalId, value=hospitalName】
export const platformIntentionAvailableHospitalAPI = (dealerId) => axios.get(`/v1/platform/intention/available/hospital/${dealerId}`)
// 【保存 手术plan(全量, doPublish=true时直接发布)】
export const surgeryPlanSaveAllAPI = (params, doPublish = false) => axios.post(`/v1/surgery/plan/saveAll?doPublish=${doPublish}`, params)
// 【获取骨科手术包(含明细)】
export const surgeryCustDealerPackageGetAPI = (id) => axios.get(`/v1/surgery/cust/dealer/package/get/${id}`)
// 【获取骨科platform手术包(含明细, 按自定义包字段格式is_cust=false)】
export const surgeryCustDealerPackageGetPlatformPackageAPI = (platformOisId) => axios.get(`/v1/surgery/cust/dealer/package/getPlatformPackage/${platformOisId}`)
// 【手术包实例 状态 enum】
export const surgeryPlanDictPackageStatusAPI = () => axios.get(`/v1/surgery/plan/dict/packageStatus`)
// 【计划状态enum】
export const surgeryPlanDictPlanStatusAPI = () => axios.get(`/v1/surgery/plan/dict/planStatus`)
// 【list手术plan(分页, 全量)】
export const surgeryPlanListAllAPI = (params, pageNum, pageSize) => axios.post(`/v1/surgery/plan/listAll?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 【发布手术plan】
export const surgeryPlanPublishAPI = (id) => axios.put(`/v1/surgery/plan/publish/${id}`)
// 【删除手术plan】
export const surgeryPlanDelAPI = (id) => axios.delete(`/v1/surgery/plan/del/${id}`)
// 【备货1个手术包(助理out_warehouse_inner_sn/out_warehouse_name)】
export const surgeryPlanPackageLockAPI = (params) => axios.put(`/v1/surgery/plan/package/lock`, params)
// 【删除1个手术包(单个手术包)】
export const surgeryPlanPackageDelAPI = (packageId) => axios.delete(`/v1/surgery/plan/package/del/${packageId}`)
// 【list手术plan手术包--展开(分页)】
export const surgeryPlanListPackageAPI = (params, pageNum, pageSize) => axios.post(`/v1/surgery/plan/listPackage?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 【获取plan内一个手术包(含明细)】
export const surgeryPlanPackageGetAllAPI = (id) => axios.get(`/v1/surgery/plan/package/getAll/${id}`)
// 【出库1个手术包(仓库)】
export const surgeryPlanPackageOutAPI = (packageId) => axios.put(`/v1/surgery/plan/package/out/${packageId}`)
// 【回库1个手术包(仓库back_quantity/batch_number)】
export const surgeryPlanPackageBackAPI = (params) => axios.put(`/v1/surgery/plan/package/back`, params)
// 【确认 数量(助理填量unit_price/sale_quantity/loss_quantity/loss_reason)】
export const surgeryPlanPackagePendingSaleAPI = (params) => axios.put(`/v1/surgery/plan/package/pendingSale`, params)
// 【完成 计划内1个手术包(助理出单)】
export const surgeryPlanPackageFinishAPI = (packageId) => axios.put(`/v1/surgery/plan/package/finish/${packageId}`)
// 【list计划可选手术包(分页: hospital/[dealer])】
export const surgeryPlanSupportPackageListAPI = (hospitalInnerSn, dealerInnerSn, params, pageNum, pageSize) => axios.post(`/v1/surgery/plan/support/package/list/${hospitalInnerSn}/${dealerInnerSn}?pageNum=${pageNum}&pageSize=${pageSize}`, params)

// 【入库单可调单类型列表】
export const grnSheetListAPI = (params) => axios.get(`/v1/business/grn/sheet/list`, params)
// 【可用做入库调单的销售单列表】
export const grnTransferRecordListAPI = (params) => axios.get(`/v1/business/grn/transfer/record/list`, params)
// 【被调单的销售单明细列表】
export const grnTransferRecordDetailAPI = (params) => axios.get(`/v1/business/grn/transfer/record/detail`, params)
// 【查询产品合同价格】【采购订单、销售订单、销售单】
export const contractGetContractPriceAPI = (params) => axios.post(`/v1/business/contract/getContractPrice`, params)
// 【查询产品合同价格】【采购订单、销售订单、销售单】
export const contractGetOrderPriceAPI = (params) => axios.post(`/v1/business/order/getOrderPrice`, params)
// 查询合同产品(分页)
export const searchContractDetailAPI = (pageNum, pageSize, params) => axios.post(`/v1/business/contract/searchContractDetail?pageNum=${pageNum}&pageSize=${pageSize}`, params)

// 入库记录查询
export const searchFinanceInboundListAPI = (pageNum, pageSize, orderByColumn, isAsc, params) => axios.post(`/v1/business/finance/grn/list?pageNum=${pageNum}&pageSize=${pageSize}&orderByColumn=${orderByColumn}&isAsc=${isAsc}`, params, { timeout: LongTimeout })
// 出库记录查询
export const searchFinanceOutboundListAPI = (pageNum, pageSize, orderByColumn, isAsc, params) => axios.post(`/v1/business/finance/odo/list?pageNum=${pageNum}&pageSize=${pageSize}&orderByColumn=${orderByColumn}&isAsc=${isAsc}`, params, { timeout: LongTimeout })
// 库存记录查询
export const searchFinanceStockListAPI = (pageNum, pageSize, orderByColumn, isAsc, params) => axios.post(`/v1/business/finance/inventory/list?pageNum=${pageNum}&pageSize=${pageSize}&orderByColumn=${orderByColumn}&isAsc=${isAsc}`, params, { timeout: LongTimeout })
// 仓库人员查询
export const financeWeListAPI = (params) => axios.get(`/v1/business/finance/we/list/${VueCookie.get('userbelong')}`, params)
// 导出入库记录
export const exportFinanceInboundListAPI = (params) => axios.post(`/v1/business/finance/grn/export`, params, { timeout: LongTimeout, responseType: 'blob' })
// 导出出库记录
export const exportFinanceOutboundListAPI = (params) => axios.post(`/v1/business/finance/odo/export`, params, { timeout: LongTimeout, responseType: 'blob' })
// 导出库存记录
export const exportFinanceStockListAPI = (params) => axios.post(`/v1/business/finance/inventory/export`, params, { timeout: LongTimeout, responseType: 'blob' })
// 入库记录金额合计
export const getFinanceGrnSumAPI = (params) => axios.post(`/v1/business/finance/grn/sum`, params, { timeout: LongTimeout })
// 出库记录金额合计
export const getFinanceOdoSumAPI = (params) => axios.post(`/v1/business/finance/odo/sum`, params, { timeout: LongTimeout })
// 全量仓库库存记录查询
export const grnGlobalListAPI = (level, params) => axios.get(`/v1/business/inventory/global/list/${level}`, {...params, timeout: LongTimeout})

// 期初建账全部删除
export const delAllInitialInventoryAPI = () => axios.delete('/v1/business/initial/clean')
// 期初建账
export const createInitialInventoryAPI = (orgId) => axios.post(`/v1/business/initial/create/${orgId}`)
// 建账结果查询
export const searchCreateInitInventoryResultAPI = (taskId) => axios.get(`/v1/business/initial/createResult/${taskId}`)
// 删除期初产品
export const delInitialInventoryAPI = (id) => axios.delete(`/v1/business/initial/del/${id}`)
// 导入期初产品结果查询
export const searchImportInitInventoryResultAPI = (taskId) => axios.get(`/v1/business/initial/importDataResult/${taskId}`)
// 查询期初产品列表
export const searchInitInventoryListAPI = (params, pageNum, pageSize) => axios.post(`/v1/business/initial/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 保存期初产品
export const saveInitInventoryAPI = (params) => axios.post(`/v1/business/initial/save`, params)

// 仓库调拨
// 获取调拨单列表(分页,创建时间倒序) 
export const getWarehouseTransferListAPI = (params) => axios.get(`/v1/business/allot/`, {params})
// 修改调拨单信息
export const editWarehouseTransferSheetAPI = (params) => axios.put(`/v1/business/allot/voucher/edit`, params)
// 修改调拨单具体某条明细信息
export const editWarehouseTransferListAPI = (params) => axios.put(`/v1/business/allot/detail/edit`, params)
// 获取单条调拨单详情
export const getWarehouseTransferSheetAPI = (avInnerSn) => axios.get(`/v1/business/allot/${avInnerSn}`)
// 添加调拨单信息
export const addWarehouseTransferSheetAPI = (params) => axios.post(`/v1/business/allot/`, params)
// 可用做出库调单的调拨单列表
export const allotTransferVoucherAPI = (params) => axios.get(`/v1/business/allot/transfer/voucher`, params)
// 被调单的调拨单明细列表
export const AllotTransferDetailAPI = (params) => axios.get(`/v1/business/allot/transfer/detail`, params)

// 使用记录
// 获取库存使用记录列表
export const useRecordListAPI = (params) => axios.get(`/v1/business/hospital/use_record/`, {params})
// 添加库存使用记录
export const useRecordAddAPI = (params) => axios.post(`/v1/business/hospital/use_record/`, params)
// 修改库存使用记录单信息
export const useRecordEditAPI = (params) => axios.put(`/v1/business/hospital/use_record/`, params)
// 获取【使用记录产品批号列表】
export const useRecordBatchListAPI = (params) => axios.get(`/v1/business/hospital/use_record/batch/list`, {params})

/* 效期预警 */
// 【平台】机构资证效期预警(分页)
export const platformWarningCertListAPI = (pageNum, pageSize) => axios.get(`/v1/platform/warning/cert/list?pageNum=${pageNum}&pageSize=${pageSize}`)
// 【平台】产品注册证效期预警(分页)
export const platformWarningMdrfListAPI = (pageNum, pageSize) => axios.get(`/v1/platform/warning/mdrf/list?pageNum=${pageNum}&pageSize=${pageSize}`)
// 【平台】获取预警设置
export const platformWarningSetupInfoAPI = () => axios.get(`/v1/platform/warning/setup/info`)
// 【平台】保存预警设置
export const platformWarningSetupSaveAPI = (params) => axios.post(`/v1/platform/warning/setup/save`, params)
// 【其它机构】当前机构及合作机构资证效期预警(分页)
export const orgWarningCertListAPI = (pageNum, pageSize) => axios.get(`/v1/business/warning/cert/list?pageNum=${pageNum}&pageSize=${pageSize}`)
// 【其它机构】当前机构有效合同内产品注册证效期预警(分页)
export const orgWarningMdrfListAPI = (pageNum, pageSize) => axios.get(`/v1/business/warning/mdrf/list?pageNum=${pageNum}&pageSize=${pageSize}`)
// 【其它机构】当前机构库存产品效期预警(分页)
export const orgWarningValidityListAPI = (pageNum, pageSize) => axios.get(`/v1/business/warning/validity/list?pageNum=${pageNum}&pageSize=${pageSize}`)
// 【其它机构】当前机构获取预警设置
export const orgWarningSetupInfoAPI = () => axios.get(`/v1/business/warning/setup/info`)

/* 代储代配 */
// 【代储代配】获取租户列表
export const lesseeListAPI = (pageNum, pageSize, data) => axios.post('/v1/business/central/lease/list', data, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, params: { pageNum, pageSize } })
// 【代储代配】新增租户
export const addLesseeAPI = (dealerId) => axios.post(`/v1/business/central/lease/add/${dealerId}`)
// 【代储代配】删除租户
export const deleteLesseesAPI = (lesseeIds) => axios.post('/v1/business/central/lease/del', lesseeIds)
// 【代储代配】获取入库通知列表
export const grnNoticeListAPI = (pageNum, pageSize, data) => axios.post('/v1/business/central/notice/grn/list', data, { params: { pageNum, pageSize } })
// 【代储代配】获取入库通知产品列表
export const grnNoticeProductListAPI = (pageNum, pageSize, data) => axios.post('/v1/business/central/notice/grn/vendor/list', data, { params: { pageNum, pageSize } })
// 【代储代配】新增入库通知
export const addGrnNotice = (data) => axios.post('/v1/business/central/notice/grn/add', data)
// 【代储代配】编辑入库通知
export const editGrnNotice = (data) => axios.post('/v1/business/central/notice/grn/add', data)
// 【代储代配】更新入库通知状态
export const updateGrnNoticeStatus = (statusInfo) => axios.post('/v1/business/central/notice/grn/edit', statusInfo)
// 【代储代配】获取出库通知列表
export const odoNoticeListAPI = (pageNum, pageSize, data) => axios.post('/v1/business/central/notice/odo/list', data, { params: { pageNum, pageSize } })
// 【代储代配】获取出库通知产品列表
export const odoNoticeProductListAPI = (pageNum, pageSize, data) => axios.post('/v1/business/central/notice/odo/vendor/list', data, { params: { pageNum, pageSize } })
// 【代储代配】新增出库通知
export const addOdoNotice = (data) => axios.post('/v1/business/central/notice/odo/add', data)
// 【代储代配】编辑出库通知
export const editOdoNotice = (data) => axios.post('/v1/business/central/notice/odo/add', data)
// 【代储代配】更新出库通知状态
export const updateOdoNoticeStatus = (statusInfo) => axios.post('/v1/business/central/notice/odo/edit', statusInfo)
// 【代储代配】获取物流信息列表
export const logisticsListAPI = (pageNum, pageSize, data, params = {}) => axios.post('/v1/business/central/logistics/list', data, { params: { pageNum, pageSize, ...params } })
// 【代储代配】获取物流信息详情
export const getLogisticsAPI = (logisticsId) => axios.post(`/v1/business/central/logistics/info/${logisticsId}`)
// 【代储代配】新增物流信息
export const addLogisticsAPI = (data) => axios.post('/v1/business/central/logistics/add', data)
// 【代储代配】编辑物流信息
export const editLogisticsAPI = (data) => axios.post('/v1/business/central/logistics/edit', data)
// 【代储代配】确认物流信息
export const confirmLogisticsAPI = (logisticsId) => axios.post(`/v1/business/central/logistics/confirm/${logisticsId}`)

/* ========= 出入库单单据拆分区域开始 ========= */
/* 采购入库 */
// 搜索采购入库单列表(分页)
export const purchaseGrnListAPI = (params) => axios.get(`/v1/business/purchase/grn/list`, params)
// 新增采购入库单
export const purchaseGrnAddAPI = (params) => axios.post('/v1/business/purchase/grn/add', params)
// 删除采购入库单
export const purchaseGrnDelAPI = (params) => axios.delete('/v1/business/purchase/grn/del', {params})
// 编辑采购入库单
export const purchaseGrnEditAPI = (params) => axios.put(`/v1/business/purchase/grn/edit`, params)
// 获取采购入库单详情
export const purchaseGrnInfoAPI = (params) => axios.get(`/v1/business/purchase/grn/info`, params)
// 采购入库单红冲
export const purchaseGrnRedAPI = (params) => axios.put(`/v1/business/purchase/grn/red`, params)
// 采购入库单反审核
export const purchaseGrnUnreviewAPI = (params) => axios.put(`/v1/business/purchase/grn/unreview`, params)
// 采购入库单可调单类型列表
export const purchaseGrnSheetListAPI = (params) => axios.get(`/v1/business/purchase/grn/sheet/list`, params)
// 可用做采购入库调单的单据列表
export const purchaseGrnTransferRecordListAPI = (params) => axios.get(`/v1/business/purchase/grn/transfer/record/list`, params)
// 被调单的单据明细列表
export const purchaseGrnTransferRecordDetailAPI = (params) => axios.get(`/v1/business/purchase/grn/transfer/record/detail`, params)
// 采购入库单可用的产品查询接口
export const purchaseGrnOrderProductListAPI = (pageNum, pageSize, data) => axios.post('/v1/business/grn/product/list', data, { params: { pageNum, pageSize, orderByColumn: 'sn', isAsc: 'desc' } })
// 采购入库单可用产品扫码查询
export const purchaseGrnProductListBarcodeAPI = (searchParams) => axios.post('/v1/business/grn/scan', searchParams)

/* 采购退货 */
// 搜索采购退货单列表(分页)
export const purchaseReturnListAPI = (params) => axios.get(`/v1/business/purchase/return/list`, params)
// 新增采购退货单
export const purchaseReturnAddAPI = (params) => axios.post('/v1/business/purchase/return/add', params)
// 删除采购退货单
export const purchaseReturnDelAPI = (params) => axios.delete('/v1/business/purchase/return/del', {params})
// 编辑采购退货单
export const purchaseReturnEditAPI = (params) => axios.put(`/v1/business/purchase/return/edit`, params)
// 获取采购退货单详情
export const purchaseReturnInfoAPI = (params) => axios.get(`/v1/business/purchase/return/info`, params)
// 采购退货单红冲
export const purchaseReturnRedAPI = (params) => axios.put(`/v1/business/purchase/return/red`, params)
// 采购退货单反审核
export const purchaseReturnUnreviewAPI = (params) => axios.put(`/v1/business/purchase/return/unreview`, params)
// 获取可退货产品列表(分页)
export const purchaseReturnProductListAPI = (params) => axios.get(`/v1/business/purchase/return/product/list`, params)

/* 销售出库 */
// 搜索销售出库单列表(分页)
export const saleOdoListAPI = (params) => axios.get(`/v1/business/sale/odo/list`, params)
// 新增销售出库单
export const saleOdoAddAPI = (params) => axios.post('/v1/business/sale/odo/add', params)
// 删除销售出库单
export const saleOdoDelAPI = (params) => axios.delete('/v1/business/sale/odo/del', {params})
// 编辑销售出库单
export const saleOdoEditAPI = (params) => axios.put(`/v1/business/sale/odo/edit`, params)
// 获取销售出库单详情
export const saleOdoInfoAPI = (params) => axios.get(`/v1/business/sale/odo/info`, params)
// 销售出库单红冲
export const saleOdoRedAPI = (params) => axios.put(`/v1/business/sale/odo/red`, params)
// 销售出库单反审核
export const saleOdoUnreviewAPI = (params) => axios.put(`/v1/business/sale/odo/unreview`, params)
// 获取销售出库单可调单类型列表
export const saleOdoSheetListAPI = (params) => axios.get(`/v1/business/sale/odo/sheet/list`, params)
// 可用做销售出库调单的销售单列表
export const saleOdoTransferRecordListAPI = (params) => axios.get(`/v1/business/sale/odo/transfer/record/list`, params)
// 被调单的销售单明细列表
export const saleOdoTransferRecordDetailAPI = (params) => axios.get(`/v1/business/sale/odo/transfer/record/detail`, params)

/* 销售退货 */
// 搜索销售退货单列表(分页)
export const saleReturnListAPI = (params) => axios.get(`/v1/business/sale/return/list`, params)
// 新增销售退货单
export const saleReturnAddAPI = (params) => axios.post('/v1/business/sale/return/add', params)
// 删除销售退货单
export const saleReturnDelAPI = (params) => axios.delete('/v1/business/sale/return/del', {params})
// 编辑销售退货单
export const saleReturnEditAPI = (params) => axios.put(`/v1/business/sale/return/edit`, params)
// 获取销售退货单详情
export const saleReturnInfoAPI = (params) => axios.get(`/v1/business/sale/return/info`, params)
// 销售退货单红冲
export const saleReturnRedAPI = (params) => axios.put(`/v1/business/sale/return/red`, params)
// 销售退货单反审核
export const saleReturnUnreviewAPI = (params) => axios.put(`/v1/business/sale/return/unreview`, params)
// 获取可退货产品列表(分页)
export const saleReturnVendorListAPI = (params) => axios.get(`/v1/business/sale/return/vendor/list`, params)

/* 领用出库 */
// 搜索领用出库单列表(分页)
export const grantOdoListAPI = (params) => axios.get(`/v1/business/grant/odo/list`, params)
// 新增领用出库单
export const grantOdoAddAPI = (params) => axios.post('/v1/business/grant/odo/add', params)
// 删除领用出库单
export const grantOdoDelAPI = (params) => axios.delete('/v1/business/grant/odo/del', {params})
// 编辑领用出库单
export const grantOdoEditAPI = (params) => axios.put(`/v1/business/grant/odo/edit`, params)
// 获取领用出库单详情
export const grantOdoInfoAPI = (params) => axios.get(`/v1/business/grant/odo/info`, params)
// 领用出库单红冲
export const grantOdoRedAPI = (params) => axios.put(`/v1/business/grant/odo/red`, params)
// 领用出库单反审核
export const grantOdoUnreviewAPI = (params) => axios.put(`/v1/business/grant/odo/unreview`, params)
// 获取领用出库单可调单类型列表
export const grantOdoSheetListAPI = (params) => axios.get(`/v1/business/grant/odo/sheet/list`, params)
// 可用做领用出库调单的发放单列表
export const grantOdoTransferRecordListAPI = (params) => axios.get(`/v1/business/grant/odo/transfer/grant/list`, params)
// 被调单的发放单发放单明细列表
export const grantOdoTransferRecordDetailAPI = (params) => axios.get(`/v1/business/grant/odo/transfer/grant/detail`, params)

/* 调拨入库 */
// 搜索调拨入库单列表(分页)
export const allotGrnListAPI = (params) => axios.get(`/v1/business/allot/grn/list`, params)
// 新增调拨入库单
export const allotGrnAddAPI = (params) => axios.post('/v1/business/allot/grn/add', params)
// 删除调拨入库单
export const allotGrnDelAPI = (params) => axios.delete('/v1/business/allot/grn/del', {params})
// 编辑调拨入库单
export const allotGrnEditAPI = (params) => axios.put(`/v1/business/allot/grn/edit`, params)
// 获取调拨入库单详情
export const allotGrnInfoAPI = (params) => axios.get(`/v1/business/allot/grn/info`, params)
// 调拨入库单红冲
export const allotGrnRedAPI = (params) => axios.put(`/v1/business/allot/grn/red`, params)
// 调拨入库单反审核
export const allotGrnUnreviewAPI = (params) => axios.put(`/v1/business/allot/grn/unreview`, params)
// 获取调拨入库可调单类型列表
export const allotGrnSheetListAPI = (params) => axios.get(`/v1/business/allot/grn/sheet/list`, params)
// 可用做调拨入库调单的调拨单列表
export const allotGrnTransferRecordListAPI = (params) => axios.get(`/v1/business/allot/grn/transfer/voucher`, params)
// 被调单的调拨单明细列表
export const allotGrnTransferRecordDetailAPI = (params) => axios.get(`/v1/business/allot/grn/transfer/detail`, params)

/* 调拨出库 */
// 搜索调拨出库单列表(分页)
export const allotOdoListAPI = (params) => axios.get(`/v1/business/allot/odo/list`, params)
// 新增采购退货单
export const allotOdoAddAPI = (params) => axios.post('/v1/business/allot/odo/add', params)
// 删除调拨出库单
export const allotOdoDelAPI = (params) => axios.delete('/v1/business/allot/odo/del', {params})
// 编辑调拨出库单
export const allotOdoEditAPI = (params) => axios.put(`/v1/business/allot/odo/edit`, params)
// 获取调拨出库单详情
export const allotOdoInfoAPI = (params) => axios.get(`/v1/business/allot/odo/info`, params)
// 调拨出库单红冲
export const allotOdoRedAPI = (params) => axios.put(`/v1/business/allot/odo/red`, params)
// 调拨出库单反审核
export const allotOdoUnreviewAPI = (params) => axios.put(`/v1/business/allot/odo/unreview`, params)
// 获取调拨出库可调单类型列表
export const allotOdoSheetListAPI = (params) => axios.get(`/v1/business/allot/odo/sheet/list`, params)
// 可用做调拨出库调单的调拨单列表
export const allotOdoTransferRecordListAPI = (params) => axios.get(`/v1/business/allot/odo/transfer/voucher`, params)
// 被调单的调拨单明细列表
export const allotOdoTransferRecordDetailAPI = (params) => axios.get(`/v1/business/allot/odo/transfer/detail`, params)

/* 赠送单 === 出库单的一种，就是产品明细没有价格和没有调单功能 */
// 搜索赠送单列表(分页)
export const giveOdoListAPI = (params) => axios.get(`/v1/business/give/odo/list`, params)
// 新增赠送单
export const giveOdoAddAPI = (params) => axios.post('/v1/business/give/odo/add', params)
// 删除赠送单
export const giveOdoDelAPI = (params) => axios.delete('/v1/business/give/odo/del', {params})
// 编辑赠送单
export const giveOdoEditAPI = (params) => axios.put(`/v1/business/give/odo/edit`, params)
// 获取赠送单详情
export const giveOdoInfoAPI = (params) => axios.get(`/v1/business/give/odo/info`, params)
// 赠送单红冲
export const giveOdoRedAPI = (params) => axios.put(`/v1/business/give/odo/red`, params)
// 赠送单反审核
export const giveOdoUnreviewAPI = (params) => axios.put(`/v1/business/give/odo/unreview`, params)

/* ========= 出入库单单据拆分区域结束 ========= */

/* 库区管理 */
// 获取租户列表(分页)
export const getTenantListAPI = () => axios.post(`/v1/business/central/lease/list`)
// 获根据机构ID查询顶级仓库列表
export const getWarehouseListAPI = (oid) => axios.get(`/v1/business/central/warehouse/list/${oid}`)
// 查询租户库存
export const getInventoryOfTenantAPI = (params, pageNum, pageSize) => axios.post(`/v1/business/central/inventory/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)

/* ========= 大屏区域开始 ========= */
// 大屏数据首页
export const screenIndexAPI = (params) => axios.get(`/v1/business/screen/`, params)
// 大屏数据首页
export const screenOrgsIndexAPI = (params) => axios.get(`/v1/business/screen/orgs`, params)
// 大屏数据首页
export const screenOrgsInfoAPI = (type) => axios.get(`/v1/business/screen/orgs/info/${type}`)
// 大屏 - 产品信息（分页）
export const screenProductInfoAPI = (params) => axios.get(`/v1/business/screen/products/info`, params)
// 大屏 - 子页面:产品交易统计
export const screenTradeItemsAPI = (params) => axios.get(`/v1/business/screen/trade/items`, params)
// 大屏 - 子页面:产品交易详情
export const screenTradeItemsDetailAPI = (vd_inner_sn, product_inner_sn) => axios.get(`/v1/business/screen/product/info/${vd_inner_sn}/${product_inner_sn}`)
// 大屏 - 产品预警信息（分页）
export const screenWarnsProductAPI = (params) => axios.get(`/v1/business/screen/warns/products`, params)
// 大屏 - 机构预警信息（分页）
export const screenWarnsOrgsAPI = (params) => axios.get(`/v1/business/screen/warns/orgs`, params)
/* ========= 大屏区域结束 ========= */

/* ========= 发票管理 区域开始 ========= */
/* 业务人员的出入库发票 */
// 搜索入库发票列表(分页)
export const invoiceGrnListAPI = (params, pageNum, pageSize) => axios.post(`/v1/business/invoice/grn/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 搜索出库发票列表(分页)
export const invoiceOdoListAPI = (params, pageNum, pageSize) => axios.post(`/v1/business/invoice/odo/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 新增入库发票
export const invoiceGrnAddAPI = (params) => axios.post(`/v1/business/invoice/grn/add`, params)
// 新增出库发票
export const invoiceOdoAddAPI = (params) => axios.post(`/v1/business/invoice/odo/add`, params)
// 删除出入库发票
export const invoiceGrnAndOdoDelAPI = (params) => axios.delete('/v1/business/invoice/del', {data: params})
// 编辑入库发票
export const invoiceGrnEditAPI = (params) => axios.put(`/v1/business/invoice/grn/edit`, params)
// 编辑出库发票
export const invoiceOdoEditAPI = (params) => axios.put(`/v1/business/invoice/odo/edit`, params)
// 送审入库发票
export const invoiceGrnSubmitAPI = (params) => axios.put(`/v1/business/invoice/grn/submit`, params)
// 送审出库发票
export const invoiceOdoSubmitAPI = (params) => axios.put(`/v1/business/invoice/odo/submit`, params)
// 获取出入库发票详情
export const invoiceGrnAndOdoInfoAPI = (id) => axios.get(`/v1/business/invoice/info/${id}`)
// 搜索发票明细列表
export const invoiceGrnAndOdoDetailListAPI = (id) => axios.get(`/v1/business/invoice/detail/list/${id}`)
// 获取入库发票产品列表(分页)
export const invoiceGrnProductListAPI = (params) => axios.get(`/v1/business/invoice/grn/product/list`, params)
// 获取出库发票产品列表(分页)
export const invoiceOdoProductListAPI = (params) => axios.get(`/v1/business/invoice/odo/product/list`, params)
/**
 * 待开票入库金额
 * @param {object} params 
 * @returns {Promise}
 */
export const invoiceGrnTotalPriceAPI = (params) => axios.put(`/v1/business/invoice/grn/product/price/count`, params)
/**
 * 待开票出库金额
 * @param {object} params 
 * @returns {Promise}
 */
export const invoiceOdoTotalPriceAPI = (params) => axios.put(`/v1/business/invoice/odo/product/price/count`, params)

/* 业务人员的预付款 */
// 新增预付款
export const invoiceAdvanceAddAPI = (params) => axios.post(`/v1/business/advance/add`, params)
// 删除预付款
export const invoiceAdvanceDelAPI = (params) => axios.delete(`/v1/business/advance/del`, {data: params})
// 编辑预付款
export const invoiceAdvanceEditAPI = (params) => axios.put(`/v1/business/advance/edit`, params)
// 获取预付款详情
export const invoiceAdvanceInfoAPI = (id) => axios.get(`/v1/business/advance/info/${id}`)
// 搜索预付款列表(分页)
export const invoiceAdvanceListAPI = (params, pageNum, pageSize) => axios.post(`/v1/business/advance/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 送审预付款
export const invoiceAdvanceSubmitAPI = (params) => axios.put(`/v1/business/advance/submit`, params)

/* 财务人员的出入库发票 */
// 作废发票
export const financeInvoiceCancelAPI = (id, reason) => axios.put(`/v1/business/finance/invoice/cancel/${id}?reason=${reason}`)
// 搜索发票明细列表
export const financeInvoiceDetailListAPI = (id) => axios.get(`/v1/business/finance/invoice/detail/list/${id}`)
// 导出出入库发票
export const financeInvoiceExportAPI = (params) => axios.post(`/v1/business/finance/invoice/export`, params, { timeout: LongTimeout, responseType: 'blob' })
// 审批入库发票
export const financeInvoiceGrnApprovalAPI = (id) => axios.put(`/v1/business/finance/invoice/grn/approval/${id}`)
// 审批出库发票
export const financeInvoiceOdoApprovalAPI = (params) => axios.put(`/v1/business/finance/invoice/odo/approval`, params)
// 搜索发票列表(分页)
export const financeInvoiceListAPI = (params, pageNum, pageSize) => axios.post(`/v1/business/finance/invoice/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 获取出入库发票详情
export const financeInvoiceInfoAPI = (id) => axios.get(`/v1/business/finance/invoice/info/${id}`)

/* 财务人员的收付款单 */
// 搜索付款单列表(分页)
export const financePaymentListAPI = (params, pageNum, pageSize) => axios.post(`/v1/business/finance/payment/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 搜索付款单明细列表
export const financePaymentInvoiceDetailListAPI = (id) => axios.post(`/v1/business/finance/payment/detail/list/${id}`)
// 搜索系统发票列表(分页)
export const financePaymentInvoiceListAPI = (params, pageNum, pageSize, type) => axios.post(`/v1/business/finance/payment/invoice/list/${type}?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 新增付款单
export const financePaymentAddAPI = (params) => axios.post(`/v1/business/finance/payment/add`, params)
// 作废付款单
export const financePaymentCancelAPI = (id, reason) => axios.put(`/v1/business/finance/payment/cancel/${id}?reason=${reason}`)
// 删除付款单
export const financePaymentDelAPI = (params) => axios.delete(`/v1/business/finance/payment/del`, {data: params})
// 编辑付款单
export const financePaymentEditAPI = (params) => axios.put(`/v1/business/finance/payment/edit`, params)
// 导出付款单
export const financePaymentExportAPI = (params) => axios.post(`/v1/business/finance/payment/export`, params, { timeout: LongTimeout, responseType: 'blob' })
// 获取付款单详情
export const financePaymentInfoAPI = (id) => axios.get(`/v1/business/finance/payment/info/${id}`)
// 付款
export const financePaymentPayAPI = (params) => axios.put(`/v1/business/finance/payment/pay`, params)
// 入账
export const financePaymentRecordedAPI = (idArray) => axios.put(`/v1/business/finance/payment/recorded`, idArray)

// 搜索收款单列表(分页)
export const financeReceiveListAPI = (params, pageNum, pageSize) => axios.post(`/v1/business/finance/receive/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 搜索收款单明细列表
export const financeReceiveInvoiceDetailListAPI = (id) => axios.post(`/v1/business/finance/receive/detail/list/${id}`)
// 搜索系统发票列表(分页)
export const financeReceiveInvoiceListAPI = (params, pageNum, pageSize) => axios.post(`/v1/business/finance/receive/invoice/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)
// 新增收款单
export const financeReceiveAddAPI = (params) => axios.post(`/v1/business/finance/receive/add`, params)
// 作废收款单
export const financeReceiveCancelAPI = (id, reason) => axios.put(`/v1/business/finance/receive/cancel/${id}?reason=${reason}`)
// 删除收款单
export const financeReceiveDelAPI = (params) => axios.delete(`/v1/business/finance/receive/del`, {data: params})
// 编辑收款单
export const financeReceiveEditAPI = (params) => axios.put(`/v1/business/finance/receive/edit`, params)
// 导出收款单
export const financeReceiveExportAPI = (params) => axios.post(`/v1/business/finance/receive/export`, params, { timeout: LongTimeout, responseType: 'blob' })
// 获取收款单详情
export const financeReceiveInfoAPI = (id) => axios.get(`/v1/business/finance/receive/info/${id}`)
// 收款
export const financeReceivePayAPI = (params) => axios.put(`/v1/business/finance/receive/pay`, params)
// 入账
export const financeReceiveRecordedAPI = (idArray) => axios.put(`/v1/business/finance/receive/recorded`, idArray)

/* 财务人员的预付款 */
// 审核预付款
export const financeAdvanceApprovalAPI = (params) => axios.put(`/v1/business/finance/advance/approval`, params)
// 作废预付款
export const financeAdvanceCancelAPI = (id, reason) => axios.put(`/v1/business/finance/advance/cancel/${id}?reason=${reason}`)
// 导出预付款
export const financeAdvanceExportAPI = (params) => axios.post(`/v1/business/finance/advance/export`, params, { timeout: LongTimeout, responseType: 'blob' })
// 获取预付款详情
export const financeAdvanceInfoAPI = (id) => axios.get(`/v1/business/finance/advance/info/${id}`)
// 搜索预付款列表(分页)
export const financeAdvanceListAPI = (params, pageNum, pageSize) => axios.post(`/v1/business/finance/advance/list?pageNum=${pageNum}&pageSize=${pageSize}`, params)

/* ========= 发票管理 区域结束 ========= */

// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 收货单 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
const arrivalPath = '/v1/business/arrival/'
// 收货单列表
export const arrivalOrderListAPI = (pageNum, pageSize, data) => axios.post(`${arrivalPath}list`, data, { params: { pageNum, pageSize } })
// 新增收货单
export const arrivalOrderAddAPI = (data) => axios.post(`${arrivalPath}add`, data)
// 编辑收货单
export const arrivalOrderEditAPI = (data) => axios.put(`${arrivalPath}edit`, data)
// 复核收货单
export const arrivalOrderApprovalAPI = (id) => axios.put(`${arrivalPath}approval/${id}`)
// 作废收货单
export const arrivalOrderCancelAPI = (id, reason) => axios.put(`${arrivalPath}cancel/${id}`, undefined, { params: { reason } })
// 删除收货单
export const arrivalOrderDeleteAPI = (id) => axios.delete(`${arrivalPath}del/${id}`)
// 收货单详情
export const arrivalOrderDetailAPI = (id) => axios.get(`${arrivalPath}info/${id}`)
// 产品列表
export const arrivalOrderProductListAPI = (pageNum, pageSize, data) => axios.post(`${arrivalPath}product/list`, data, { params: { pageNum, pageSize, orderByColumn: 'sn', isAsc: 'desc' } })
/**
 * 产品列表-扫码
 * @param {object} searchParams
 * @param {string} searchParams.barcode 条码
 * @param {string} searchParams.bills_type 业务类型
 * @param {string} searchParams.organization_inner_sn 机构id
 * @param {string} searchParams.warehouse_inner_sn 仓库id
 * @returns 
 */
export const arrivalOrderProductListBarcodeAPI = (searchParams) =>
    axios.post(`${arrivalPath}scan`, searchParams);
// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 收货单 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 验货单 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
const inspectionOrderPath = '/v1/business/inspection/'
// 收货单列表
export const inspectionOrderListAPI = (pageNum, pageSize, data) => axios.post(`${inspectionOrderPath}list`, data, { params: { pageNum, pageSize } })
// 新增收货单
export const inspectionOrderAddAPI = (data) => axios.post(`${inspectionOrderPath}add`, data)
// 编辑收货单
export const inspectionOrderEditAPI = (data) => axios.put(`${inspectionOrderPath}edit`, data)
// 复核收货单
export const inspectionOrderApprovalAPI = (id) => axios.put(`${inspectionOrderPath}approval/${id}`)
// 作废收货单
export const inspectionOrderCancelAPI = (id, reason) => axios.put(`${inspectionOrderPath}cancel/${id}`, undefined, { params: { reason } })
// 删除收货单
export const inspectionOrderDeleteAPI = (id) => axios.delete(`${inspectionOrderPath}del/${id}`)
// 收货单详情
export const inspectionOrderDetailAPI = (id) => axios.get(`${inspectionOrderPath}info/${id}`)
// 产品列表
export const inspectionOrderProductListAPI = (pageNum, pageSize, data) => axios.post(`${inspectionOrderPath}product/list`, data, { params: { pageNum, pageSize, orderByColumn: 'sn', isAsc: 'desc' } })
/**
 * 产品列表-扫码
 * @param {object} searchParams
 * @param {string} searchParams.barcode 条码
 * @param {string} searchParams.bills_type 业务类型
 * @param {string} searchParams.organization_inner_sn 机构id
 * @param {string} searchParams.warehouse_inner_sn 仓库id
 * @returns 
 */
export const inspectionOrderProductListBarcodeAPI = (searchParams) =>
    axios.post(`${inspectionOrderPath}scan`, searchParams);
// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 验货单 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 产品标签管理 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
const barcodeManagePath = '/v1/business/tag';

/**
 * 已关联产品的标签列表
 * @param {number} page 页码
 * @param {number} pageSize 页尺寸
 * @param {object} searchParams 查询参数
 * @param {string | undefined} searchParams.product_name 产品名称
 * @param {string | undefined} searchParams.product_vendor_name 厂商名称
 * @param {string | undefined} searchParams.specification 规格型号
 * @param {string | undefined} searchParams.udi_barcode udi码
 * @returns {Promise}
 */
export const barcodeListAPI = (page, pageSize, searchParams) =>
    axios.post(`${barcodeManagePath}/list`, searchParams, { params: { pageNum: page, pageSize: pageSize } });

/**
 * 
 * 未关联标签的产品列表
 * @param {number} page 页码
 * @param {number} pageSize 页尺寸
 * @param {object} searchParams 查询参数
 * @param {string | undefined} searchParams.product_name 产品名称
 * @param {string | undefined} searchParams.product_vendor_name 厂商名称
 * @param {string | undefined} searchParams.specification 规格型号
 * @param {string | undefined} searchParams.udi_barcode udi码
 * @returns {Promise}
 */
export const barcodeProductListAPI = (page, pageSize, searchParams) =>
    axios.post(`${barcodeManagePath}/product/list`, searchParams, { params: { pageNum: page, pageSize } });

/**
 * 供标签管理使用的厂商产品列表
 * @param {number} page 页码
 * @param {number} pageSize 页尺寸
 * @param {object} searchParams 查询参数
 * @param {string | undefined} searchParams.code68_sn 68分类编号
 * @param {string | undefined} searchParams.name 产品名称
 * @param {string | undefined} searchParams.specification 规格型号
 * @param {string | undefined} searchParams.specification_vendor_sn 型号规格厂商自定编号
 * @param {string | undefined} searchParams.type_inner_sn 自定义类型编号
 * @param {string | undefined} searchParams.vendor_inner_sn 厂商ID
 * @returns {Promise}
 */
export const barcodeVendorProductList = (page, pageSize, searchParams) =>
    axios.get(`${barcodeManagePath}/vendor/Product/list`, { params: { ...searchParams, pageSize, pageNum: page } })

/**
 * 批量添加标签关联产品
 * @param {array} barcodeList 标签列表
 * @param {object} barcodeList[] 标签详细
 * @returns {Promise}
 */
export const barcodesAddAPI = (barcodeList) => axios.post(`${barcodeManagePath}/add`, barcodeList);

/**
 * 删除标签
 * @param {string} barcode udi码
 * @returns {Promise}
 */
export const barcodeDeleteAPI = (barcode) => axios.delete(`${barcodeManagePath}/del/${barcode}`);

// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 产品标签管理 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
