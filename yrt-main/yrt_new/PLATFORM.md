# 平台接口（具体参数请看/doc.html#/）
### [返回README](/README.md)
### 企业注册
1. 根据搜索条件获取待注册审核的机构列表 `/v1/platform/orgReg/list/{systemType}`
2. 查看待注册审核机构信息详情 `/v1/platform/orgReg/info/{id}`
3. 修改待注册审核机构信息 `/v1/platform/orgReg/edit/{id}`
4. 删除待注册审核机构 `/v1/platform/orgReg/delete/{id}`
5. 待注册审核机构审核通过 `/v1/platform/orgReg/pass/{id}`
6. 待注册审核机构审核不通过 `/v1/platform/orgReg/nopass/{id}`

### 机构认证
1. 根据搜索条件获取机构列表 `/v1/platform/org/unAuth/list/{systemType}`
2. 根据机构ID新增机构相关图片 `/v1/platform/org/image/add/{systemType}/{id}/{certType}`
2. 根据机构ID获取机构证件图片列表 `/v1/platform/org/image/list/{systemType}/{id}`
3. 根据机构ID和机构证件类型ID获取证件图片信息 `/v1/platform/org/image/info/{systemType}/{id}/{certType}`
4. 机构认证信息审核通过 `/v1/platform/org/pass/{systemType}/{id}`
5. 机构认证信息审核不通过 `/v1/platform/org/nopass/{systemType}/{id}`
6. 获取机构基本信息 `/v1/platform/org/info/{systemType}/{id}`
7. 修改机构基本信息 `/v1/platform/org/edit/{systemType}/{id}`
8. 删除机构`/v1/platform/org/delete/{systemType}/{id}`
9. 获取与机构营业执照相关的所有信息(经销商、医院、厂商) `/v1/platform/org/info/license/{systemType}/{id}`
10. 添加机构营业执照相关的所有信息(经销商、医院、厂商) `/v1/platform/org/add/license/{systemType}/{id}`
11. 编辑与机构营业执照相关的所有信息(经销商、医院、厂商) `/v1/platform/org/edit/license/{systemType}/{id}`
12. 获取经销商医疗器械经营许可证的所有信息 `/v1/platform/org/info/maaiol/{id}`
13. 添加经销商医疗器械经营许可证的所有信息 `/v1/platform/org/add/maaiol/{id}`
14. 编辑经销商医疗器械经营许可证的所有信息 `/v1/platform/org/edit/maaiol/{id}`
15. 获取经销商第二类医疗器械经营备案凭证的所有信息 `/v1/platform/org/info/stmdbrc/{id}`
16. 添加经销商第二类医疗器械经营备案凭证的所有信息 `/v1/platform/org/add/stmdbrc/{id}`
17. 编辑经销商第二类医疗器械经营备案凭证的所有信息 `/v1/platform/org/edit/stmdbrc/{id}`
18. 获取医院医疗机构执业许可证的所有信息 `/v1/platform/org/info/plhmi/{id}`
19. 添加医院医疗机构执业许可证的所有信息 `/v1/platform/org/add/plhmi/{id}`
20. 编辑医院医疗机构执业许可证的所有信息 `/v1/platform/org/edit/plhmi/{id}`
21. 获取厂商医疗器械生产许可证的所有信息 `/v1/platform/org/info/mdpl/{id}`
22. 添加厂商医疗器械生产许可证的所有信息 `/v1/platform/org/add/mdpl/{id}`
23. 编辑厂商医疗器械生产许可证的所有信息 `/v1/platform/org/edit/mdpl/{id}`

### 机构管理
1. 根据搜索条件获取认证过后的机构列表 `/v1/platform/org/list/{systemType}`
2. 修改认证过后的机构信息 `/v1/platform/org/edit/{systemType}/{id}`
3. 删除认证过后的机构`/v1/platform/org/delete/{systemType}/{id}`
4. 根据机构ID获取机构证件图片列表 `/v1/platform/org/image/list/{systemType}/{id}`
5. 根据机构ID和机构证件类型ID获取证件图片信息 `/v1/platform/org/image/info{systemType}/{id}/{certType}`

### 产品管理
1. 新增商品 `/v1/platform/product/add`
2. 批量导入商品 `暂无`
3. 修改商品 `/v1/platform/product/edit/{vid}/{pid}`
4. 删除商品 `/v1/platform/product/del/{vid}/{pid}`
7. 新增产品相关图 `/v1/platform/product/image/add`
8. 根据产品ID和图片类型获取相关图片 `/v1/platform/product/image/{vid}/{pid}/{id}/{certType}`
9. 按照产品编号查询未删除的正常商品信息 `/v1/platform/product/info/{vid}/{pid}`
10. 按条件搜索未发布的产品列表 `/v1/platform/product/unpublic/list`
11. 根据搜索条件和产品数据来源获取产品(厂商添加的可发布产品和平台添加的可发布产品)列表 `/v1/platform/product/list/{source}`
12. 根据搜索条件获取商品(已发布)列表 `/v1/platform/product/public/list`
13. 根据厂商ID和产品ID获取产品医疗器材注册登记表 `/v1/platform/product/mdrf/info/{vid}/{pid}`
13. 根据厂商ID和产品ID新增医疗器材注册登记表 `/v1/platform/product/mdrf/add/{vid}/{pid}`
14. 根据厂商ID和产品ID修改医疗器材注册登记表 `/v1/platform/product/mdrf/edit/{vid}/{pid}`
14. 获取所有认证通过的厂商列表 `/comm/allVendor/public/{vid}/{pid}`
15. 发布产品 `/v1/platform/product/public/{vid}/{pid}`
16. 根据厂商ID和已**发布产品**ID新增规格型号 `/v1/platform/product/spec/add/{vid}/{pid}/{spec}`
17. 根据厂商ID、已**发布产品**ID和规格型号ID修改规格型号 `/v1/platform/product/spec/del/{vid}/{pid}/{id}`
18. 根据厂商ID和已**发布产品**ID获取产品规格型号列表 `/v1/platform/product/spec/list/{vid}/{pid}`

### 产品单位
1. 新增产品单位 `/v1/platform/unit/add`
2. 修改产品单位 `/v1/platform/unit/edit/{vid}/{pid}/{psid}/{puid}`
3. 根据产品单位ID查询单位信息 `/v1/platform/unit/info/{vid}/{pid}/{psid}/{puid}`
4. 根据产品ID查询所有产品单位 `/v1/platform/unit/list/{vid}/{pid}/{psid}`
5. 根据产品ID和产品单位上级ID查询所有下级产品单位 `/v1/platform/unit/list/{vid}/{pid}/{psid}/{pupid}`
6. 根据产品ID查询所有产品单位以树的方式返回 `/v1/platform/unit/tree/{vid}/{pid}/{psid}`

