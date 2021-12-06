# 通用接口
### [返回README](/README.md)
### 行政区划相关接口
1. 获取全部省份列表 `/v1/comm/area/province`
2. 根据省份ID获取城市列表 `/v1/comm/area/city/{id}`
3. 根据城市ID获取地区列表 `/v1/comm/area/county/{id}`
4. 根据地区ID获取省份、城市和地区列表 `/v1/comm/area/list/{id}`
### 医疗器械68编码相关接口
1. 根据ID获取68编码详情查询  `/v1/comm/code68/{id}`
2. 根据ID查询分类下所有的下级分类 `/v1/comm/code68/list/{id}`
3. 查询所有的最上级68分类信息  `/v1/comm/code68/list`
4. 根据id查询所有上级信息 `/v1/comm/code68/parents/{id}`
5. 查询所有68分类信息以树的方式返回 `/v1/comm/code68/tree`
### 商品自定义分类相关接口
1. 根据id查询分类信息 `/v1/comm/category/{id}`
2. 获取顶级自定义分类 `/v1/comm/category/list`
3. 根据id查询下级分类信息列表 `/v1/comm/category/list/{id}`
4. 增加商品自定义分类 `/v1/comm/category/add`
5. 编辑商品自定义分类 `/v1/comm/category/edit/{id}`
6. 根据id查询所有上级信息 `/v1/comm/category/parents/{id}`
### 其他通用相关接口
1. 获取所有可用的厂商列表(下列组件专用) `/v1/comm/allVendor`
2. 获取所有可用的机构列表(下列组件专用) `/v1/comm/{systemType}/allOrg`
2. 根据机构ID获取所有登录用户账号列表(下列组件专用) `/v1/comm/user/{systemType}/list/{oid}`
### 企业信息操作接口
1. 注册新机构 `/reg/add`
2. 系统登录 `/login`