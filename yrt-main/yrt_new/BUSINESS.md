# 通用接口
### [返回README](/README.md)

### 机构部门与人员管理
1. 新增机构部门 `/v1/business/org/{systemType}/dept/add/{id}`
2. 修改机构部门 `/v1/business/org/{systemType}/dept/edit/{id}/{did}`
3. 删除机构部门 `/v1/business/org/{systemType}/dept/del/{id}/{did}`
4. 根据机构ID和机构类型查询部门列表 `/v1/business/org/{systemType}/dept/info/{id}/{did}`
5. 根据机构ID查询机构所有一级部门 `/v1/business/org/{systemType}/dept/toplist/{id}`
6. 根据机构ID和上级部门ID查询机构所有下级部门 `/v1/business/org/{systemType}/dept/toplist/{id}/{pid}`
7. 根据机构ID查询机构所有部门 `/v1/business/org/{systemType}/dept/topTree/{id}`
8. 新增机构人员 `/v1/business/org/{systemType}/emp/add/{id}`
9. 编辑机构人员 `/v1/business/org/{systemType}/emp/edit/{id}/{eid}`
10. 删除机构人员 `/v1/business/org/{systemType}/emp/del/{id}/{eid}`
11. 根据机构ID和人员ID查询机构人员详情 `/v1/business/org/{systemType}/emp/info/{id}/{eid}`
12. 根据机构ID查询机构所有人员 `/v1/business/org/{systemType}/emp/list/{id}`
### 经销商自建产品管理
1. 新增产品 `/v1/business/dealer/product/add`
2. 修改产品信息 `/v1/business/dealer/product/edit/{did}/{pid}`
3. 删除产品信息 `/v1/business/dealer/product/del/{did}/{pid}`
4. 新增产品相关图片 `/v1/business/dealer/product/image/add`
5. 根据产品ID和经销商图片类型获取相关图片 `/v1/business/dealer/product/image/{did}/{pid}/{certType}`
6. 按照产品编号查询未删除的正常商品信息 `/v1/business/dealer/product/info/{did}/{pid}`
7. 根据经销商ID和产品ID新增规格型号 `/v1/business/dealer/product/spec/add/{did}/{pid}/{spec}`
8. 根据经销商ID、产品ID和规格型号ID修改规格型号 `/v1/business/dealer/product/spec/edit/{did}/{pid}/{id}/{spec}`
9. 根据经销商ID和产品ID删除规格型号 `/v1/business/dealer/product/spec/del/{did}/{pid}/{id}`
10. 根据经销商ID和产品ID获取产品规格型号列表 `/v1/business/dealer/product/spec/list/{did}/{pid}`
### 经销商自建产品单位
1. 新增产品单位 `/v1/business/dealer/unit/add`
2. 修改产品单位 `/v1/business/dealer/unit/edit/{vid}/{pid}/{psid}/{puid}`
3. 根据产品单位ID查询单位信息 `/v1/business/dealer/unit/info/{vid}/{pid}/{psid}/{puid}`
4. 根据产品ID查询所有产品单位 `/v1/business/dealer/unit/list/{vid}/{pid}/{psid}`
5. 根据产品ID和产品单位上级ID查询所有下级产品单位 `/v1/business/dealer/unit/list/{vid}/{pid}/{psid}/{pupid}`
### 仓库管理接口
1. 新增机构仓库 `/v1/business/warehouse/{systemType}/add/{oid}`
2. 编辑机构仓库 `/v1/business/warehouse/{systemType}/edit/{oid}/{wid}`
3. 删除机构仓库 `/v1/business/warehouse/{systemType}/del/{oid}/{wid}`
4. 根据机构ID和仓库ID查询仓库信息 `/v1/business/warehouse/{systemType}/info/{oid}/{wid}`
5. 根据机构ID和仓库ID查询所有下级仓库 `/v1/business/warehouse/{systemType}/list/{oid}/{wid}`
6. 根据机构ID查询机构所有一级仓库 `/v1/business/warehouse/{systemType}/toplist/{oid}`
7. 根据机构ID查询所有仓库用树状显示 `/v1/business/warehouse/{systemType}/tree/{oid}`