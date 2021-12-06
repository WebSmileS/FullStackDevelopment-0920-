<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="groupBtnFn('add')" type="primary" size="small">新增</a-button>
                            <a-button @click="groupBtnFn('edit')" type="primary" size="small">修改</a-button>
                        </li>
                        <li>
                            <a-button @click="groupBtnFn('remove')" type="danger" size="small">删除</a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-show="!hightSearchObj.isArrow"
                            v-model="searchTables.name"
                            placeholder="姓名"
                            size="small"
                            enterButton
                            class="search"
                            @search="searchBtnFn"
                        />
                    </div>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab切换区域开始 -->
        <div
            :class="{
                'cur-overflow-hidden': tabsActiveKey === 'list'
            }"
            class="tab-table-wrap tab-table-overflowy"
        >
            <!-- 表格头部 -->
            <a-tabs @change="changeTabsFn" type="card">
                <a-tab-pane tab="列表" :key="systemTypes[0]" style="padding: 8px">
                    <vxe-table
                        ref="xTable"
                        size="small"
                        :height="scrollYNumber - 4"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        @cell-click="cellClickyFn"
                        @cell-dblclick="cellDblclickFn"
                        align="center"
                        :data="datas"
                        :loading="pagination.loading"
                        show-overflow
                        show-header-overflow
                        showHeader
                        :start-index="(pagination.current - 1) * pagination.pageSize"
                    >
                        <vxe-table-column
                            v-for="columnItem in columns"
                            :key="columnItem.key"
                            :width="columnItem.width"
                            :field="columnItem.field"
                            :title="columnItem.title"
                        ></vxe-table-column>
                    </vxe-table>
                    <vxe-pager
                        :loading="pagination.loading"
                        :current-page="pagination.current"
                        :page-size="pagination.pageSize"
                        :page-sizes="pagination.pageSizes"
                        :total="pagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changePaginationFn"
                    ></vxe-pager>
                </a-tab-pane>
                <a-tab-pane tab="规划图" :key="systemTypes[1]" class="architecture-diagram-wrap" style="padding: 8px">
                    <div class="architecture-diagram-checkbox-group">
                        <a-checkbox-group :options="options" :value="plainOptions" @change="onChangeFn" />
                        <!-- 前面的复选框隐藏掉了，详见App.vue 样式 .ant-checkbox -->
                    </div>
                    <vue2-org-tree
                        :data="humanResourcesAllTrees"
                        :horizontal="horizontal"
                        :render-content="renderContent"
                        @on-node-click="onNodeClick"
                    />
                </a-tab-pane>
                <a-tooltip slot="tabBarExtraContent" placement="left">
                    <template slot="title">
                        <span>刷新</span>
                    </template>
                    <a-button
                        type="primary"
                        shape="circle"
                        icon="reload"
                        size="small"
                        class="refresh-btn"
                        @click="initTablesFn"
                    />
                    <!--刷新按钮区域-->
                </a-tooltip>
            </a-tabs>
            <!--表格区域-->
        </div>
        <!-- tab切换区域结束 -->

        <!-- 信息修改区域开始 -->
        <a-modal
            v-model="humanResourcesInfoModal.alert"
            :centered="true"
            :maskClosable="false"
            class="modal-form-input-scoped human-resources-modal-form global-drag"
        >
            <div v-globalDrag="{ el: 'human-resources-modal-form' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    {{ humanResourcesInfoModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="humanResourcesInfoModalCancelFn">取消</a-button>
                <a-button
                    key="submit"
                    type="primary"
                    :loading="humanResourcesInfoModal.loading"
                    @click="humanResourcesInfoModalSaveFn"
                    >保存</a-button
                >
            </template>
            <div class="modal-form-input">
                <div class="form-top">
                    <div class="left-wrap">
                        <ul>
                            <li class="item">
                                <div class="item-children">
                                    <span class="label"> <em class="em red">*</em>姓名 </span>
                                    <a-input v-model="editInfoFormObj.name" placeholder class="input" />
                                </div>
                                <div class="item-children">
                                    <span class="label">员工编号</span>
                                    <a-input v-model="editInfoFormObj.employee_sn" placeholder class="input" />
                                </div>
                            </li>
                            <li class="item">
                                <div class="item-children">
                                    <span class="label"> <em class="em red">*</em>登录系统 </span>
                                    <a-switch
                                        v-model="login_account_status"
                                        checkedChildren="是"
                                        unCheckedChildren="否"
                                        @change="changeLoginAccountStatusFn"
                                    />
                                </div>
                                <div class="item-children">
                                    <span class="label">状态</span>
                                    <a-tree-select
                                        :showSearch="false"
                                        v-model="editInfoFormObj.status"
                                        placeholder
                                        treeDefaultExpandAll
                                        :allowClear="false"
                                        class="input"
                                    >
                                        <a-tree-select-node value="0" title="试用期" key="试用期"></a-tree-select-node>
                                        <a-tree-select-node value="1" title="在职" key="在职"></a-tree-select-node>
                                        <a-tree-select-node
                                            disabled
                                            value="2"
                                            title="离职"
                                            key="离职"
                                        ></a-tree-select-node>
                                        <a-tree-select-node
                                            value="3"
                                            title="停薪留职"
                                            key="停薪留职"
                                        ></a-tree-select-node>
                                    </a-tree-select>
                                </div>
                            </li>
                            <li class="item">
                                <div class="item-children">
                                    <span
                                        v-show="editInfoFormObj.login_account_status"
                                        class="label"
                                        style="min-width: 94px"
                                    >
                                        <em class="em red">*</em>登录手机号
                                    </span>
                                    <span v-show="!editInfoFormObj.login_account_status" class="label">手机号</span>
                                    <a-input v-model="editInfoFormObj.phone" placeholder class="input" />
                                </div>
                                <div class="item-children">
                                    <span class="label">电子邮件</span>
                                    <a-input v-model="editInfoFormObj.email" placeholder class="input" />
                                </div>
                                <!-- 去除角色设定 -->
                                <!-- <div class="item-children">
                                    <span class="label">
                                        <em v-show="editInfoFormObj.login_account_status"
                                            class="em red">*</em>角色
                                    </span>
                                    <a-tree-select :disabled="!editInfoFormObj.login_account_status"
                                                   :showSearch="false"
                                                   :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                                   :treeData="roleDatas"
                                                   treeDefaultExpandAll
                                                   v-model="editInfoFormObj.role_inner_sn"
                                                   class="input">
                                    </a-tree-select>
                </div>-->
                                <!-- 去除角色设定 -->
                            </li>
                            <li class="item">
                                <div class="item-children">
                                    <span class="label">性别</span>
                                    <a-tree-select
                                        :showSearch="false"
                                        v-model="editInfoFormObj.sex"
                                        placeholder
                                        treeDefaultExpandAll
                                        :allowClear="false"
                                        class="input"
                                    >
                                        <a-tree-select-node value="0" title="男" key="男"></a-tree-select-node>
                                        <a-tree-select-node value="1" title="女" key="女"></a-tree-select-node>
                                    </a-tree-select>
                                </div>
                                <div class="item-children">
                                    <span class="label">民族</span>
                                    <a-input v-model="editInfoFormObj.nation" placeholder class="input" />
                                </div>
                            </li>
                            <li class="item">
                                <div class="item-children">
                                    <span class="label">证件类别</span>
                                    <a-tree-select
                                        :showSearch="false"
                                        v-model="editInfoFormObj.personal_certificate_type"
                                        placeholder
                                        treeDefaultExpandAll
                                        :allowClear="false"
                                        class="input"
                                    >
                                        <a-tree-select-node value="0" title="身份证" key="身份证"></a-tree-select-node>
                                        <a-tree-select-node value="1" title="护照" key="护照"></a-tree-select-node>
                                    </a-tree-select>
                                </div>
                                <div class="item-children">
                                    <span class="label">证件号</span>
                                    <a-input
                                        v-model="editInfoFormObj.personal_certificate_sn"
                                        placeholder
                                        class="input"
                                    />
                                </div>
                            </li>
                            <li class="item">
                                <div class="item-children">
                                    <span class="label">学历</span>
                                    <a-tree-select
                                        :showSearch="false"
                                        v-model="editInfoFormObj.degree"
                                        placeholder
                                        treeDefaultExpandAll
                                        :allowClear="false"
                                        class="input"
                                    >
                                        <a-tree-select-node value="1" title="小学" key="小学"></a-tree-select-node>
                                        <a-tree-select-node value="2" title="初中" key="初中"></a-tree-select-node>
                                        <a-tree-select-node value="3" title="高中" key="高中"></a-tree-select-node>
                                        <a-tree-select-node value="4" title="职高" key="职高"></a-tree-select-node>
                                        <a-tree-select-node value="5" title="大专" key="大专"></a-tree-select-node>
                                        <a-tree-select-node value="6" title="大学" key="大学"></a-tree-select-node>
                                        <a-tree-select-node value="7" title="硕士" key="硕士"></a-tree-select-node>
                                        <a-tree-select-node value="8" title="博士" key="博士"></a-tree-select-node>
                                    </a-tree-select>
                                </div>
                                <div class="item-children">
                                    <span class="label">婚姻状况</span>
                                    <a-tree-select
                                        :showSearch="false"
                                        v-model="editInfoFormObj.marriage_status"
                                        placeholder
                                        treeDefaultExpandAll
                                        :allowClear="false"
                                        class="input"
                                    >
                                        <a-tree-select-node value="0" title="未婚" key="未婚"></a-tree-select-node>
                                        <a-tree-select-node value="1" title="已婚" key="已婚"></a-tree-select-node>
                                        <a-tree-select-node value="2" title="离异" key="离异"></a-tree-select-node>
                                        <a-tree-select-node value="3" title="丧偶" key="丧偶"></a-tree-select-node>
                                    </a-tree-select>
                                </div>
                            </li>
                            <li class="item departments-item">
                                <div class="item-children">
                                    <span class="label">生日</span>
                                    <a-date-picker
                                        v-model="birthday"
                                        @change="changeBirthdayPickerFn"
                                        placeholder
                                        allowClear
                                        class="input"
                                    />
                                </div>
                                <div class="item-children">
                                    <span class="label">职位</span>
                                    <a-input v-model="editInfoFormObj.position" placeholder class="input" />
                                </div>
                            </li>
                            <li class="item departments-item">
                                <div class="item-children">
                                    <span class="label">入职日期</span>
                                    <a-date-picker
                                        :disabledDate="disabledStartDate"
                                        v-model="entry_date"
                                        @change="changeEntryDatePickerFn"
                                        placeholder
                                        allowClear
                                        class="input"
                                    />
                                </div>
                                <div class="item-children">
                                    <span class="label">离职日期</span>
                                    <a-date-picker
                                        :disabledDate="disabledEndDate"
                                        v-model="departure_date"
                                        @change="changeDepartureDatePickerFn"
                                        placeholder
                                        allowClear
                                        class="input"
                                    />
                                </div>
                            </li>
                            <li class="item departments-item">
                                <span class="label"> <em class="em red">*</em>所属部门 </span>
                                <a-tree-select
                                    treeNodeFilterProp="title"
                                    :showSearch="true"
                                    :treeData="architectureAllTrees"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    multiple
                                    allowClear
                                    treeDefaultExpandAll
                                    v-model="editInfoFormDepartmentInnerSn"
                                    @change="setEditInfoFormDepartmentInnerSnFn"
                                    class="input"
                                    style="width: 350px"
                                ></a-tree-select>
                            </li>
                            <li class="item">
                                <div class="item-children">
                                    <span class="label">上级主管</span>
                                    <a-tree-select
                                        treeNodeFilterProp="title"
                                        searchPlaceholder=""
                                        :showSearch="true"
                                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                        :treeData="parentDirectors"
                                        treeDefaultExpandAll
                                        v-model="editInfoFormObj.parent_employee_inner_sn"
                                        allowClear
                                        @change="changeTreeSelect"
                                        @click="clickTreeSelectFn"
                                        class="input"
                                    ></a-tree-select>
                                </div>
                                <!-- 去除角色 -->
                                <!-- <div class="item-children">
                                    <span class="label">电子邮件</span>
                                    <a-input v-model="editInfoFormObj.email"
                                             placeholder=""
                                             class="input" />
                </div>-->
                            </li>
                        </ul>
                    </div>
                    <div class="right-wrap">
                        <div class="user-pic">
                            <div class="right">
                                <base-image v-if="imageUrl" :src="imageUrl" width="100%" height="100%" />
                                <span v-else>暂无图片</span>
                            </div>
                            <a-upload
                                :action="QiniuUrl"
                                listType="picture"
                                :data="{ token: photoUrlData }"
                                :showUploadList="false"
                                :beforeUpload="beforeUploadFn"
                                @change="photoUrlChangeFn"
                                class="upload-wrap"
                            >
                                <a-button style="margin-top: 20px"> <a-icon type="upload" />点击上传图片 </a-button>
                            </a-upload>
                        </div>
                        <div class="form-bottom" style="padding-top: 47px">
                            <ul>
                                <li class="item item-description">
                                    <span class="description-span">备注</span>
                                    <a-textarea
                                        v-model="editInfoFormObj.description"
                                        :autosize="{ minRows: 5, maxRows: 5 }"
                                        class="input"
                                        placeholder
                                    />
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </a-modal>
        <!-- 信息修改区域结束 -->
    </div>
</template>

<script>
import moment from 'moment';
import {
    // userGetRoleListAPI,
    getUploadTokenAPI,
    platformDeptTreeAPI,
    platformEmpDelAPI,
    platformEmpAddAPI,
    platformEmpEditAPI,
    platformEmpInfoAPI,
    platformEmpListAPI,
    platformEmpTreeAPI,
    orgEmpTopTreeAPI,
    orgDeptTopTreeAPI,
    orgEmpAddAPI,
    orgEmpDelAPI,
    orgEmpEditAPI,
    orgEmpInfoAPI,
    orgEmpListAPI,
    orgEmpDropdownListAPI,
    // getGroupRolesAPI,
    delUserRoleAPI
} from '@/service/pageAjax';
import BaseImage from '@/components/Picture/BaseImage';

export default {
    name: 'HumanResources',
    components: {
        BaseImage
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            searchValue: '',
            userSystemTypeFlag: parseFloat(this.$vueCookie.get('userSystemType')) === 0, // 0： 平台 // 1 2 3 其他
            columns: [
                {
                    title: '姓名',
                    field: 'name',
                    key: 'name',
                    minWidth: '20%'
                },
                {
                    title: '员工编号',
                    field: 'employee_sn',
                    key: 'employee_sn',
                    minWidth: '17%'
                },
                // {
                //     title: '角色',
                //     field: 'role_sn',
                //     key: 'role_sn',
                //     width: '12%'
                // },
                {
                    title: '性别',
                    field: 'sex_text',
                    key: 'sex_text',
                    minWidth: '7%'
                },
                {
                    title: '部门',
                    field: 'department_name',
                    key: 'department_name',
                    minWidth: '16%'
                },
                {
                    title: '职位',
                    field: 'position',
                    key: 'position',
                    minWidth: '12%'
                },
                {
                    title: '学历',
                    field: 'degree_text',
                    key: 'degree_text',
                    minWidth: '8%'
                },
                {
                    title: '电话',
                    field: 'phone',
                    key: 'phone',
                    minWidth: '10%'
                },
                {
                    title: '状态',
                    field: 'status_text',
                    key: 'status_text',
                    minWidth: '10%'
                }
            ],
            datas: [],
            // 表格分页
            pagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            systemTypes: ['list', 'architecture'],
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            tabsActiveKey: 'list', // 表格头部的tab选项卡切换的 key 值 【默认值为： 列表】
            searchTables: {
                name: ''
            }, // 表格的搜索并向后端请求字段
            rangePickerVal: moment[''], // 申请时间的默认赋值
            // 修改弹框的配置
            humanResourcesInfoModal: {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            },
            architectureAllTrees: [], // 所属部门的信息列表
            birthday: moment[''],
            entry_date: moment[''],
            departure_date: moment[''],
            editInfoFormObj: {
                // 修改信息的模态框值 - 后端返回回来的
                parent_employee_inner_sn: '',
                sex: '', // 男
                status: '',
                personal_certificate_type: '',
                degree: '',
                department_inner_sn: '',
                department_name: '',
                marriage_status: '',
                employee_inner_sn: '',
                entry_date: '',
                birthday: '',
                departure_date: '',
                name: '',
                employee_sn: '',
                nation: '',
                position: '',
                personal_certificate_sn: '',
                phone: '',
                email: '',
                qq: '',
                description: '',
                photo_url: '',
                login_account_status: 0
                // role_inner_sn: ''
            },
            login_account_status: 0,
            curDepartment_name: '', // 存储部门名称的中文名字
            editInfoFormOldObj: {}, // 修改信息的模态框值 - 后端返回回来的旧值
            editInfoFormNewObj: {}, // 修改信息的模态框值 - 前端根据页面修改的值放进去 【没有修改的不要向后端传递】
            btnName: '', // 记录顶部点击的是哪个按钮
            humanResourcesAllTrees: {
                name: '云南栢田科技有限公司',
                status: 1, // 0：无效  1：有效
                rootFlag: true, // 判断是不是root级
                children: []
            },
            horizontal: false,
            options: [
                { label: '在职人员', value: 0 },
                { label: '停薪留职人员', value: 1 },
                { label: '试用人员', value: 2 },
                { label: '已离职', value: 3 }
            ],
            plainOptions: [],
            sexOptions: ['男', '女'], // 性别
            employeeOptions: ['试用期', '在职', '离职', '停薪留职'], // 员工状态
            degreeOptions: ['文盲', '小学', '初中', '高中', '职高', '大专', '大学', '硕士', '博士', '博士后'], // 学历
            parentDirectors: [], // 上级主管
            editInfoFormDepartmentInnerSn: [], // 所属部门
            photoUrlData: '',
            imageUrl: '',
            key: '',
            roleListObj: {}
        };
    },
    methods: {
        /* 图片上传的接口区域 */
        async getUploadTokenAPIFn() {
            await getUploadTokenAPI().then((res) => {
                const { code, data } = res;
                if (parseFloat(code) === 0) {
                    this.photoUrlData = data;
                } else {
                    this.photoUrlData = '';
                    this.$message.error(res.msg);
                }
            });
        },

        // 取消用户登录时，同时删除用户角色
        async delUserRole() {
            await delUserRoleAPI(this.currentTrObj.user_inner_sn)
                .then(() => {})
                .catch(() => {
                    this.$$message.error('意外错误，请稍后再试');
                });
        },

        // <管理员>获取角色列表
        async userGetRoleListAPIFn() {
            if (this.userSystemTypeFlag) {
                this.platformEmpListAPIFn();
                this.platformEmpTreeAPIFn();
            } else {
                this.orgEmpListAPIFn();
                this.orgEmpTopTreeAPIFn();
            }
            // 人力资源页面不再管理角色
            // await userGetRoleListAPI().then(res => {
            //     const {code, data} = res
            //     if (parseFloat(Code) === 0) {
            //         this.roleDatas = Data.map(item => {
            //             if (item.Role_inner_sn) {
            //                 this.roleListObj[item.Role_inner_sn] = item.Name
            //             }
            //             return {
            //                 ...item,
            //                 value: item.Role_inner_sn,
            //                 label: item.Name
            //             }
            //         })

            //     } else {
            //         this.$message.error(res.msg)
            //     }
            // })
        },

        // 获取角色列表
        // async getGroupRoles () {
        //     this.roles = []
        //     await getGroupRolesAPI().then(res => {
        //         if (res.Code || res.code === 0) {
        //             res.data.forEach(role => {
        //                 let r = {
        //                     id: role.Key,
        //                     name: role.Name,
        //                     editAble: !role.Internal
        //                 }
        //                 this.roles.push(r)
        //             })
        //         } else {
        //             this.$message.error(res.Msg)
        //         }
        //     }).catch((e) => {
        //         this.$message.error('意外错误')
        //     })
        // },

        /* 平台的接口开始 */
        // 【查询平台所有部门】
        async platformDeptTreeAPIFn() {
            await platformDeptTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    const listNew = list.map((item) => {
                        return {
                            ...item
                            // role_sn: item.role_inner_sn ? this.roleListObj[item.role_inner_sn] : ''
                        };
                    });
                    this.treeDatasIndexAryFn(listNew, 'deptTopTree');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【删除】平台人员
        async platformEmpDelAPIFn(id) {
            await platformEmpDelAPI(id)
                .then((res) => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        if (
                            parseFloat(this.$cookie.get('userInnerSn')) === parseFloat(this.currentTrObj.user_inner_sn)
                        ) {
                            // 自己删除自己 退出系统
                            this.$Utils.removeCookieFn();
                            this.$router.replace({ path: '/' });
                        } else {
                            this.platformEmpListAPIFn();
                            this.platformEmpTreeAPIFn();
                            this.humanResourcesInfoModal.alert = false;
                        }
                        // this.$message.success('删除成功')
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                });
        },
        // 【新增】平台人员
        async platformEmpAddAPIFn(editInfoFormObj) {
            let params = {
                name: editInfoFormObj.name,
                sex: editInfoFormObj.sex || '0',
                status: editInfoFormObj.status || '0',
                personal_certificate_type: editInfoFormObj.personal_certificate_type || '0',
                degree: editInfoFormObj.degree || '1',
                marriage_status: editInfoFormObj.marriage_status || '0'
                // role_inner_sn: editInfoFormObj.role_inner_sn
            };

            if (editInfoFormObj.parent_employee_inner_sn) {
                params.parent_employee_inner_sn = editInfoFormObj.parent_employee_inner_sn;
            }

            // if (params.role_inner_sn) {
            //     params.role_inner_sn = editInfoFormObj.role_inner_sn
            // }

            if (editInfoFormObj.birthday) {
                params.birthday = editInfoFormObj.birthday;
            }

            if (editInfoFormObj.entry_date) {
                params.entry_date = editInfoFormObj.entry_date;
            }

            if (editInfoFormObj.departure_date) {
                params.departure_date = editInfoFormObj.departure_date;
            }

            if (editInfoFormObj.employee_sn) {
                params.employee_sn = editInfoFormObj.employee_sn;
            }

            if (editInfoFormObj.nation) {
                params.nation = editInfoFormObj.nation;
            }

            if (editInfoFormObj.position) {
                params.position = editInfoFormObj.position;
            }

            if (editInfoFormObj.personal_certificate_sn) {
                params.personal_certificate_sn = editInfoFormObj.personal_certificate_sn;
            }

            if (editInfoFormObj.phone) {
                params.phone = editInfoFormObj.phone;
            }

            if (editInfoFormObj.email) {
                params.email = editInfoFormObj.email;
            }

            if (editInfoFormObj.qq) {
                params.qq = editInfoFormObj.qq;
            }

            if (editInfoFormObj.description) {
                params.description = editInfoFormObj.description;
            }

            if (editInfoFormObj.photo_url) {
                params.photo_url = editInfoFormObj.photo_url;
            }

            if (editInfoFormObj.department_inner_sn) {
                params.department_inner_sn = editInfoFormObj.department_inner_sn;
            }

            if (editInfoFormObj.login_account_status) {
                params.login_account_status = editInfoFormObj.login_account_status;
            }

            await platformEmpAddAPI(params)
                .then((res) => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const xTable = this.$refs.xTable;
                        let recordItem = xTable.getCurrentRow(),
                            oldSeq = 0,
                            { info } = res;
                        if (recordItem) {
                            oldSeq =
                                recordItem.indexAry && recordItem.indexAry.length > 0
                                    ? recordItem.indexAry[recordItem.indexAry.length - 1]
                                    : 0;
                            info.indexAry = recordItem.indexAry;
                        } else {
                            info.indexAry = [0];
                        }
                        const seq = oldSeq - 1 < 0 ? 0 : oldSeq;
                        info.key = info.employee_inner_sn;
                        info.status_text = this.employeeOptions[parseFloat(info.status)];
                        info.parentDepartmentName = [info.name];
                        info.children = null;
                        info.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                        // info.role_sn = info.role_inner_sn ? this.roleListObj[info.role_inner_sn] : ''
                        this.pagination.total++;
                        xTable.insertAt(info, xTable.getTableData().tableData[seq]).then(({ row }) => {
                            this.treeDatasIndexAryFn(xTable.getTableData().tableData);
                            xTable.setCurrentRow(row);
                            this.currentTrObj = row;
                            this.platformEmpTreeAPIFn(row);
                            if (this.datas.length > this.pagination.pageSize) {
                                this.datas.splice(this.datas.length - 1, 1);
                            }
                        });
                        this.humanResourcesInfoModal.alert = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                });
        },
        // 【编辑】平台人员
        async platformEmpEditAPIFn(editInfoFormNewObj) {
            // 传递给后端的参数，没有的值就不要传递了
            let params = {
                employee_inner_sn: this.editInfoFormObj.employee_inner_sn,
                ...editInfoFormNewObj
            };
            if (!editInfoFormNewObj.photo_url) {
                delete params.photo_url;
                delete params.photo_url_old;
            }
            await platformEmpEditAPI(params)
                .then((res) => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        // department_name
                        for (let i = 0; i < this.datas.length; i++) {
                            const item = this.datas[i];
                            if (parseInt(item.employee_inner_sn) === parseInt(this.currentTrObj.employee_inner_sn)) {
                                const xTable = this.$refs.xTable,
                                    infoItem = {
                                        ...item,
                                        name: this.editInfoFormObj.name,
                                        director_name: this.editInfoFormObj.director_name,
                                        department_name: editInfoFormNewObj.department_inner_sn
                                            ? this.curDepartment_name
                                            : item.department_name,
                                        employee_sn: this.editInfoFormObj.employee_sn,
                                        position: this.editInfoFormObj.position,
                                        phone: this.editInfoFormObj.phone,
                                        status: this.editInfoFormObj.status,
                                        sex_text: this.sexOptions[parseInt(this.editInfoFormObj.sex)],
                                        status_text: this.employeeOptions[parseInt(this.editInfoFormObj.status)],
                                        degree_text: this.degreeOptions[parseInt(this.editInfoFormObj.degree)]
                                        // role_sn: this.editInfoFormObj.role_inner_sn ? this.roleListObj[this.editInfoFormObj.role_inner_sn] : ''
                                    };
                                this.datas.splice(i, 1, infoItem);
                                xTable.refreshData();
                                xTable.loadData(this.datas);
                                xTable.setCurrentRow(infoItem);
                                if (
                                    parseFloat(this.$cookie.get('userInnerSn')) ===
                                    parseFloat(this.currentTrObj.user_inner_sn)
                                ) {
                                    // 说明自己修改了自己
                                    this.$store.commit('setIsNewUserInfoFn', Math.random());
                                }
                                break;
                            }
                        }
                        this.platformEmpTreeAPIFn();
                        this.humanResourcesInfoModal.alert = false;
                        // this.$message.success(`${this.humanResourcesInfoModal.btnTitle}成功`)
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                });
        },
        // 根据平台人员ID【查询平台人员详情】
        async platformEmpInfoAPIFn(id) {
            await platformEmpInfoAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    const infos = {
                        ...info,
                        sex: info.sex + '',
                        parent_employee_inner_sn:
                            info.parent_employee_inner_sn && !isNaN(info.parent_employee_inner_sn)
                                ? info.parent_employee_inner_sn + ''
                                : '',
                        status: info.status + '',
                        personal_certificate_type: info.personal_certificate_type + '',
                        degree: info.degree + '',
                        marriage_status: info.marriage_status + '',
                        personal_certificate_sn: !isNaN(info.personal_certificate_sn)
                            ? info.personal_certificate_sn + ''
                            : '',
                        birthday: info.birthday ? moment(info.birthday, 'YYYY-MM-DD') : null,
                        entry_date: info.entry_date ? moment(info.entry_date, 'YYYY-MM-DD') : null,
                        departure_date: info.departure_date ? moment(info.departure_date, 'YYYY-MM-DD') : null
                    };
                    this.birthday = infos.birthday || moment[''];
                    this.entry_date = infos.entry_date || moment[''];
                    this.departure_date = infos.departure_date || moment[''];

                    this.editInfoFormDepartmentInnerSn = infos.department_inner_sn
                        ? infos.department_inner_sn.split(',')
                        : []; // 所属部门赋值
                    this.editInfoFormObj = {
                        ...info,
                        sex: infos.sex,
                        status: infos.status,
                        personal_certificate_type: infos.personal_certificate_type,
                        degree: infos.degree,
                        marriage_status: infos.marriage_status
                    };
                    this.editInfoFormOldObj = {
                        ...info,
                        sex: infos.sex,
                        status: infos.status,
                        personal_certificate_type: infos.personal_certificate_type,
                        degree: infos.degree,
                        marriage_status: infos.marriage_status
                    }; // 为了比对值的临时存储
                    this.login_account_status = infos.login_account_status;
                    if (infos.photo_url) {
                        this.imageUrl = this.$Utils.getPictureSrc(infos.photo_url);
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID查询机构【所有人员】 = 列表
        async platformEmpListAPIFn(info) {
            this.pagination.loading = true;
            let params = {
                pageNum: this.pagination.current,
                pageSize: this.pagination.pageSize,
                // 排序
                orderByColumn: 'employee_inner_sn',
                isAsc: 'desc'
            };

            if (this.searchTables.name) {
                params.name = this.searchTables.name;
            }

            await platformEmpListAPI({
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res;
                    // 给列表的表格赋值
                    let rowsNew = rows.map((item, index) => {
                        item.key = item.employee_inner_sn;
                        item.activeClass = '';
                        item.status_text = this.employeeOptions[parseFloat(item.status)];
                        // item.sex_text = this.sexOptions[parseInt(item.sex)]
                        item.indexAry = [index];
                        item.parentDepartmentName = [item.name];
                        item.children = null;
                        item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                        // item.role_sn = item.role_inner_sn ? this.roleListObj[item.role_inner_sn] : ''
                        return item;
                    });
                    this.treeDatasIndexAryFn(rowsNew);
                    if (!info) {
                        if (this.datas.length) {
                            this.currentTrObj = this.datas[0];
                        } else {
                            this.datas = [];
                            this.currentTrObj = {};
                        }
                    } else {
                        this.currentTrObj = this.datas.find((item) => item.key === info.employee_inner_sn) || {};
                    }
                    this.$nextTick(() => {
                        this.$refs.xTable.setCurrentRow(this.currentTrObj);
                    });

                    this.pagination = {
                        ...this.pagination,
                        total,
                        loading: false
                    };
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【查询平台人员树】 = 规划图 = 上级主管
        async platformEmpTreeAPIFn() {
            await platformEmpTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    const listNew = list.map((item) => {
                        return {
                            ...item
                            // role_sn: item.role_inner_sn ? this.roleListObj[item.role_inner_sn] : ''
                        };
                    });
                    this.treeDatasIndexAryFn(listNew, 'platformDeptTree');
                    this.treeDatasIndexAryFn(listNew, 'parentDirectors');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /* 平台的接口结束 */

        /* 经销商-医院-厂商的接口开始 */
        // 根据机构ID查询经销商【所有部门】
        async orgDeptTopTreeAPIFn() {
            await orgDeptTopTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'deptTopTree');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【删除】机构人员
        async orgEmpDelAPIFn(eid) {
            await orgEmpDelAPI(eid)
                .then((res) => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        if (
                            parseFloat(this.$cookie.get('userInnerSn')) === parseFloat(this.currentTrObj.user_inner_sn)
                        ) {
                            // 自己删除自己 退出系统
                            this.$Utils.removeCookieFn();
                            this.$router.replace({ path: '/' });
                        } else {
                            this.orgEmpListAPIFn();
                            this.orgEmpTopTreeAPIFn();
                            this.humanResourcesInfoModal.alert = false;
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                });
        },
        // 【新增】机构人员
        async orgEmpAddAPIFn(editInfoFormObj) {
            let params = {
                name: editInfoFormObj.name,
                sex: editInfoFormObj.sex || 0,
                status: editInfoFormObj.status || 0,
                personal_certificate_type: editInfoFormObj.personal_certificate_type || 0,
                degree: editInfoFormObj.degree || 1,
                marriage_status: editInfoFormObj.marriage_status || 0
                // role_inner_sn: editInfoFormObj.role_inner_sn
            };

            if (editInfoFormObj.parent_employee_inner_sn) {
                params.parent_employee_inner_sn = editInfoFormObj.parent_employee_inner_sn;
            }

            if (editInfoFormObj.birthday) {
                params.birthday = editInfoFormObj.birthday;
            }

            if (editInfoFormObj.entry_date) {
                params.entry_date = editInfoFormObj.entry_date;
            }

            if (editInfoFormObj.departure_date) {
                params.departure_date = editInfoFormObj.departure_date;
            }

            if (editInfoFormObj.employee_sn) {
                params.employee_sn = editInfoFormObj.employee_sn;
            }

            if (editInfoFormObj.nation) {
                params.nation = editInfoFormObj.nation;
            }

            if (editInfoFormObj.position) {
                params.position = editInfoFormObj.position;
            }

            if (editInfoFormObj.personal_certificate_sn) {
                params.personal_certificate_sn = editInfoFormObj.personal_certificate_sn;
            }

            if (editInfoFormObj.phone) {
                params.phone = editInfoFormObj.phone;
            }

            if (editInfoFormObj.email) {
                params.email = editInfoFormObj.email;
            }

            if (editInfoFormObj.qq) {
                params.qq = editInfoFormObj.qq;
            }

            if (editInfoFormObj.description) {
                params.description = editInfoFormObj.description;
            }

            if (editInfoFormObj.photo_url) {
                params.photo_url = editInfoFormObj.photo_url;
            }

            if (editInfoFormObj.department_inner_sn) {
                params.department_inner_sn = editInfoFormObj.department_inner_sn;
            }

            if (editInfoFormObj.login_account_status) {
                params.login_account_status = editInfoFormObj.login_account_status;
            }

            await orgEmpAddAPI(params)
                .then((res) => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        this.$nextTick(() => {
                            const xTable = this.$refs.xTable;
                            let recordItem = xTable.getCurrentRow(),
                                oldSeq = 0,
                                { info } = res;
                            if (recordItem) {
                                oldSeq =
                                    recordItem.indexAry && recordItem.indexAry.length > 0
                                        ? recordItem.indexAry[recordItem.indexAry.length - 1]
                                        : 0;
                                info.indexAry = recordItem.indexAry;
                            } else {
                                info.indexAry = [0];
                            }
                            const seq = oldSeq - 1 < 0 ? 0 : oldSeq;
                            info.key = info.employee_inner_sn;
                            info.status_text = this.employeeOptions[parseFloat(info.status)];
                            info.parentDepartmentName = [info.name];
                            info.children = null;
                            info.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                            // info.role_sn = info.role_inner_sn ? this.roleListObj[info.role_inner_sn] : ''
                            this.pagination.total++;
                            xTable.insertAt(info, xTable.getTableData().tableData[seq]).then(({ row }) => {
                                this.treeDatasIndexAryFn(xTable.getTableData().tableData);
                                xTable.setCurrentRow(row);
                                this.currentTrObj = row;
                                this.orgEmpTopTreeAPIFn(row);
                                if (this.datas.length > this.pagination.pageSize) {
                                    this.datas.splice(this.datas.length - 1, 1);
                                }
                            });
                            this.humanResourcesInfoModal.alert = false;
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                });
        },
        // 【编辑】机构人员
        async orgEmpEditAPIFn(editInfoFormNewObj) {
            // 传递给后端的参数，没有的值就不要传递了
            let params = {
                ...editInfoFormNewObj
            };
            if (!editInfoFormNewObj.photo_url) {
                delete params.photo_url;
                delete params.photo_url_old;
            }
            await orgEmpEditAPI(this.editInfoFormObj.employee_inner_sn, params)
                .then((res) => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        for (let i = 0; i < this.datas.length; i++) {
                            const item = this.datas[i];
                            if (parseInt(item.employee_inner_sn) === parseInt(this.currentTrObj.employee_inner_sn)) {
                                const xTable = this.$refs.xTable,
                                    infoItem = {
                                        ...item,
                                        name: this.editInfoFormObj.name,
                                        director_name: this.editInfoFormObj.director_name,
                                        department_name: editInfoFormNewObj.department_inner_sn
                                            ? this.curDepartment_name
                                            : item.department_name,
                                        employee_sn: this.editInfoFormObj.employee_sn,
                                        position: this.editInfoFormObj.position,
                                        phone: this.editInfoFormObj.phone,
                                        status: this.editInfoFormObj.status,
                                        sex_text: this.sexOptions[parseInt(this.editInfoFormObj.sex)],
                                        status_text: this.employeeOptions[parseInt(this.editInfoFormObj.status)],
                                        degree_text: this.degreeOptions[parseInt(this.editInfoFormObj.degree)]
                                        // role_sn: this.editInfoFormObj.role_inner_sn ? this.roleListObj[this.editInfoFormObj.role_inner_sn] : ''
                                    };
                                this.datas.splice(i, 1, infoItem);
                                xTable.refreshData();
                                xTable.loadData(this.datas);
                                xTable.setCurrentRow(infoItem);
                                if (
                                    parseFloat(this.$cookie.get('userInnerSn')) ===
                                    parseFloat(this.currentTrObj.user_inner_sn)
                                ) {
                                    // 说明自己修改了自己
                                    this.$store.commit('setIsNewUserInfoFn', Math.random());
                                }
                                break;
                            }
                        }
                        this.orgEmpTopTreeAPIFn();
                        this.humanResourcesInfoModal.alert = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.humanResourcesInfoModal.loading = false;
                    this.pagination.loading = false;
                });
        },
        // 根据机构ID和人员ID【查询机构人员详情】
        async orgEmpInfoAPIFn(eid) {
            await orgEmpInfoAPI(eid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    const infos = {
                        ...info,
                        sex: info.sex + '',
                        parent_employee_inner_sn:
                            info.parent_employee_inner_sn && !isNaN(info.parent_employee_inner_sn)
                                ? info.parent_employee_inner_sn + ''
                                : '',
                        status: info.status + '',
                        personal_certificate_type: info.personal_certificate_type + '',
                        degree: info.degree + '',
                        marriage_status: info.marriage_status + '',
                        personal_certificate_sn: !isNaN(info.personal_certificate_sn)
                            ? info.personal_certificate_sn + ''
                            : '',
                        birthday: info.birthday ? moment(info.birthday, 'YYYY-MM-DD') : null,
                        entry_date: info.entry_date ? moment(info.entry_date, 'YYYY-MM-DD') : null,
                        departure_date: info.departure_date ? moment(info.departure_date, 'YYYY-MM-DD') : null
                    };
                    this.birthday = infos.birthday || moment[''];
                    this.entry_date = infos.entry_date || moment[''];
                    this.departure_date = infos.departure_date || moment[''];

                    this.editInfoFormDepartmentInnerSn = infos.department_inner_sn
                        ? infos.department_inner_sn.split(',')
                        : []; // 所属部门赋值
                    this.editInfoFormObj = {
                        ...info,
                        sex: infos.sex,
                        status: infos.status,
                        personal_certificate_type: infos.personal_certificate_type,
                        degree: infos.degree,
                        marriage_status: infos.marriage_status
                    };
                    this.editInfoFormOldObj = {
                        ...info,
                        sex: infos.sex,
                        status: infos.status,
                        personal_certificate_type: infos.personal_certificate_type,
                        degree: infos.degree,
                        marriage_status: infos.marriage_status
                    }; // 为了比对值的临时存储
                    this.login_account_status = infos.login_account_status;
                    if (infos.photo_url) {
                        this.imageUrl = this.$Utils.getPictureSrc(infos.photo_url);
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID查询机构【所有人员】 = 列表
        async orgEmpListAPIFn(info) {
            this.pagination.loading = true;
            let params = {
                pageNum: this.pagination.current,
                pageSize: this.pagination.pageSize
            };

            if (this.searchTables.name) {
                params.name = this.searchTables.name;
            }

            await orgEmpListAPI({
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res;
                    // 给列表的表格赋值
                    let rowsNew = rows.map((item, index) => {
                        item.key = item.employee_inner_sn;
                        item.activeClass = '';
                        item.status_text = this.employeeOptions[parseFloat(item.status)];
                        item.indexAry = [index];
                        item.parentDepartmentName = [item.name];
                        item.children = null;
                        item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                        // item.role_sn = item.role_inner_sn ? this.roleListObj[item.role_inner_sn] : ''
                        return item;
                    });
                    this.treeDatasIndexAryFn(rowsNew);
                    if (!info) {
                        if (this.datas.length) {
                            this.currentTrObj = this.datas[0];
                        } else {
                            this.datas = [];
                            this.currentTrObj = {};
                        }
                    } else {
                        this.currentTrObj =
                            (this.datas.length && this.datas.find((item) => item.key === info.employee_inner_sn)) || {};
                    }
                    this.$nextTick(() => {
                        if (Object.keys(this.currentTrObj).length <= 0) {
                            this.currentTrObj = this.datas.length > 0 ? this.datas[0] : {};
                        }
                        if (this.datas.length > 0) {
                            this.$refs.xTable.setCurrentRow(this.currentTrObj);
                        }
                    });
                    this.pagination = {
                        ...this.pagination,
                        total,
                        loading: false
                    };
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID查询机构【所有人员】 = 上级主管
        async orgEmpDropdownListAPIFn() {
            await orgEmpDropdownListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    // 没有分级，直接是一层数据
                    // const {list} = res
                    // this.formatToTreesFn(list, 'parentDirectors')
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 所有【员工规划图】接口 = 规划图 = 上级主管
        async orgEmpTopTreeAPIFn() {
            await orgEmpTopTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    const listNew = list.map((item) => {
                        return {
                            ...item
                            // role_sn: item.role_inner_sn ? this.roleListObj[item.role_inner_sn] : ''
                        };
                    });
                    this.treeDatasIndexAryFn(listNew, 'platformDeptTree');
                    this.treeDatasIndexAryFn(listNew, 'parentDirectors');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /* 经销商-医院-厂商的接口结束 */

        // 清空上级主管的事件
        changeTreeSelect(value, label) {
            if (label.length <= 0) {
                this.editInfoFormObj.parent_employee_inner_sn = '';
            }
        },

        // 上级主管的界定值
        clickTreeSelectFn() {
            // this.currentTrObj.employee_inner_sn
            this.treeDatasIndexAryFn(this.parentDirectors, 'parentDirectors', this.currentTrObj.employee_inner_sn);
        },

        // 设置后端所需要的所属部门格式
        setEditInfoFormDepartmentInnerSnFn(value, label) {
            this.editInfoFormObj.department_inner_sn = value ? value.join(',') : '';
            this.curDepartment_name = label.length ? label.join(',') : '';
        },
        treeSelectSearchFn() {
            this.searchValue = '';
        },
        // 上级部门数据选择完成后的回调
        changeSuperiorDepartmentsFn(value) {
            this.editInfoFormObj.employee_inner_sn = value[value.length - 1];
        },
        getTableDataFn() {
            this.userSystemTypeFlag ? this.platformEmpListAPIFn() : this.orgEmpListAPIFn();
        },
        changePaginationFn({ currentPage, pageSize }) {
            this.pagination.current = currentPage;
            this.pagination.pageSize = pageSize;
            this.pagination.loading = true;
            this.getTableDataFn();
        },
        // tab切换
        changeTabsFn(activeKey) {
            this.tabsActiveKey = activeKey;
        },
        initTablesFn() {
            this.currentTrObj = {};
            this.tabsActiveKey = 'list';
            this.humanResourcesInfoModal = {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            };
            this.editInfoFormObj = {
                // 修改信息的模态框值 - 后端返回回来的
                parent_employee_inner_sn: '',
                sex: '', // 男
                status: '',
                personal_certificate_type: '',
                degree: '',
                marriage_status: '',
                birthday: null,
                entry_date: null,
                departure_date: null,
                name: '',
                employee_sn: '',
                nation: '',
                position: '',
                personal_certificate_sn: '',
                phone: '',
                email: '',
                qq: '',
                description: '',
                photo_url: '',
                login_account_status: 0
            };
            this.login_account_status = 0;
            this.editInfoFormOldObj = {};
            this.editInfoFormNewObj = {};
            this.btnName = '';
            this.value = '';
            this.humanResourcesAllTrees = {
                name: '云南栢田科技有限公司',
                status: 1, // 0：无效  1：有效
                rootFlag: true, // 判断是不是root级
                children: []
            };
            this.horizontal = false;
            this.plainOptions = [];
            this.datas = null;
            this.pagination.current = 1;
            this.pagination.loading = true;
            this.userGetRoleListAPIFn();
        },
        // 表单默认值重置
        humanResourcesInfoModalResetFn() {
            this.editInfoFormObj = {
                parent_employee_inner_sn: '',
                sex: '', // 男
                status: '',
                personal_certificate_type: '',
                degree: '',
                department_inner_sn: '',
                department_name: '',
                marriage_status: '',
                employee_inner_sn: '',
                entry_date: '',
                birthday: '',
                departure_date: '',
                name: '',
                employee_sn: '',
                nation: '',
                position: '',
                personal_certificate_sn: '',
                phone: '',
                email: '',
                qq: '',
                description: '',
                photo_url: '',
                login_account_status: 0
            };
            this.imageUrl = '';
            this.key = '';
            this.photoUrlData = '';
            this.login_account_status = 0;
            this.birthday = moment[''];
            this.entry_date = moment[''];
            this.departure_date = moment[''];
            this.editInfoFormDepartmentInnerSn = [];
        },
        // 检测表格是否有选中的的数据
        checkCurrentTrDataFn() {
            if (Object.keys(this.currentTrObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            return true;
        },
        // 顶部按钮的触发事件 btnName
        groupBtnFn(btnName) {
            this.btnName = btnName;
            this.currentTrObj = this.$refs.xTable.getCurrentRow();
            if (this.btnName !== 'add' && !this.checkCurrentTrDataFn()) return;
            this.humanResourcesInfoModalResetFn();
            if (this.btnName !== 'remove') {
                if (this.userSystemTypeFlag) {
                    this.platformDeptTreeAPIFn();
                } else {
                    this.orgDeptTopTreeAPIFn();
                    this.orgEmpDropdownListAPIFn();
                }
                this.getUploadTokenAPIFn();
            }
            switch (btnName) {
                case 'add': {
                    this.editInfoFormObj.sex = '0';
                    this.editInfoFormObj.status = '0';
                    this.editInfoFormObj.personal_certificate_type = '0';
                    this.editInfoFormObj.degree = '1';
                    this.editInfoFormObj.marriage_status = '0';
                    this.humanResourcesInfoModal = {
                        title: `${this.$route.meta.title} - 新增`,
                        btnTitle: '新增保存',
                        alert: true,
                        loading: false,
                        isShowStatus: false
                    };
                    this.$Utils.globalDragCenterFn('human-resources-modal-form');
                    break;
                }
                case 'edit': {
                    this.humanResourcesInfoModal = {
                        title: `${this.$route.meta.title} - 修改`,
                        btnTitle: '修改保存',
                        alert: true,
                        loading: false,
                        isShowStatus: true
                    };
                    this.$Utils.globalDragCenterFn('human-resources-modal-form');
                    this.userSystemTypeFlag
                        ? this.platformEmpInfoAPIFn(this.currentTrObj.employee_inner_sn)
                        : this.orgEmpInfoAPIFn(this.currentTrObj.employee_inner_sn);
                    break;
                }
                case 'remove': {
                    const _this = this;
                    _this.$confirm({
                        title: '删除',
                        content: '确认要执行该操作吗？',
                        centered: true,
                        maskClosable: false,
                        onOk() {
                            // TODO: 删除角色，再检查
                            if (_this.currentTrObj.user_inner_sn !== null && _this.currentTrObj.user_inner_sn) {
                                _this.delUserRole();
                            }
                            _this.userSystemTypeFlag
                                ? _this.platformEmpDelAPIFn(_this.currentTrObj.employee_inner_sn)
                                : _this.orgEmpDelAPIFn(_this.currentTrObj.employee_inner_sn);
                            return new Promise((resolve, reject) => {
                                setTimeout(Math.random() > 0.5 ? resolve : reject, 1000);
                            }).catch(() => {});
                        }
                    });
                    break;
                }
            }
        },
        renderContent(h, data) {
            const linearGradientAry = [
                    'trial-personnel',
                    'on-personnel',
                    'departure-personnel',
                    'stop-personnel',
                    'department-defaults'
                ], // 试用人员：0  在职人员：1   已离职：2   停薪留职人员：3   默认根颜色：4
                status = data.rootFlag ? linearGradientAry.length - 1 : parseFloat(data.status),
                curClass = data.employee_inner_sn === this.currentTrObj.key ? ' cur' : '';

            return h(
                'div',
                {
                    class: 'architecture-diagram ' + linearGradientAry[status] + curClass
                },
                [
                    h(
                        'div',
                        {
                            class: 'font-size'
                        },
                        data.name
                    ),
                    h(
                        'div',
                        {
                            class: 'font-size'
                        },
                        data.position || '-'
                    ),
                    h(
                        'div',
                        {
                            class: 'font-size'
                        },
                        data.department_name_new || '-'
                    )
                ]
            );
        },
        onNodeClick(e, data) {
            if (!data.employee_inner_sn) return;

            // 去除列表的高亮样式
            for (let i = 0; i < this.datas.length; i++) {
                const item = this.datas[i];
                if (item.employee_inner_sn === data.employee_inner_sn) {
                    this.currentTrObj = item;
                    this.$refs.xTable.setCurrentRow(item);
                    break;
                }
            }

            // 显示架构图的高亮样式
            let architectureDiagramEle = document.getElementsByClassName('architecture-diagram');
            for (let i = 0; i < architectureDiagramEle.length; i++) {
                architectureDiagramEle[i].classList.remove('cur');
            }
            let ev = window.event || e,
                target = ev.srcElement || ev.target;
            // 得到 div
            while (target.classList[0] !== 'architecture-diagram') {
                target = target.parentNode;
            }
            target.classList.add('cur');
        },
        onChangeFn(val) {
            this.plainOptions = val;
        },
        // 检测修改的表单信息校验
        checkEditInfoFormMsgFn() {
            const regExpFn = this.$Utils.regExpFn(),
                mobilePhone = /^[0-9]*$/,
                email = regExpFn.email;

            this.editInfoFormObj.name = this.$Utils.trim(this.editInfoFormObj.name);
            this.editInfoFormObj.email = this.$Utils.trim(this.editInfoFormObj.email);
            this.editInfoFormObj.phone = this.$Utils.trim(this.editInfoFormObj.phone);

            // 姓名
            if (!this.editInfoFormObj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入姓名',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            // 姓名 02
            if (!this.$Utils.trim(this.editInfoFormObj.name)) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入姓名',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                this.editInfoFormObj.name = this.$Utils.trim(this.editInfoFormObj.name);
                return false;
            }
            // 姓名 03
            if (this.$Utils.checkInputValFn(this.editInfoFormObj.name)) {
                this.$warning({
                    title: '操作提示',
                    content: '姓名中请不要输入特殊字符',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return false;
            }
            // 手机号
            if (this.editInfoFormObj.login_account_status && !this.editInfoFormObj.phone) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入手机号',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            // 电话 01
            if (this.editInfoFormObj.phone && this.editInfoFormObj.phone.length > 0) {
                // 电话 02
                if (this.editInfoFormObj.phone.length > 11) {
                    this.$warning({
                        title: '操作提示',
                        content: '电话有效长度是11位',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }

                // 电话 03
                if (!mobilePhone.test(this.editInfoFormObj.phone)) {
                    this.$warning({
                        title: '操作提示',
                        content: '电话格式输入有误',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }
            }

            // 角色
            // if (this.editInfoFormObj.login_account_status && !this.editInfoFormObj.role_inner_sn) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '请选择角色',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     return false
            // }

            // 电子邮件
            if (
                this.editInfoFormObj.email &&
                this.editInfoFormObj.email.length > 0 &&
                !email.test(this.editInfoFormObj.email)
            ) {
                this.$warning({
                    title: '操作提示',
                    content: '电子邮件格式输入有误',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            if (!this.editInfoFormDepartmentInnerSn.length) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择所属部门',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 上传图片的url
            this.editInfoFormObj.photo_url = this.key;
            return true;
        },
        // 检测并设置修改过的修改信息内容字段
        checkEditInfoFormObjFn() {
            let editInfoFlag = true;
            this.editInfoFormNewObj = {};
            for (let server in this.editInfoFormObj) {
                let serverItem = this.editInfoFormObj[server];
                for (let old in this.editInfoFormOldObj) {
                    let oldItem = this.editInfoFormOldObj[old];
                    if (server === old && serverItem !== oldItem && (serverItem || oldItem)) {
                        this.editInfoFormNewObj = {
                            ...this.editInfoFormNewObj,
                            [server]: serverItem, // 新值
                            [server + '_old']: oldItem // 旧值
                        };
                        editInfoFlag = false;
                    }
                }
            }
            return editInfoFlag;
        },
        // 取消按钮
        humanResourcesInfoModalCancelFn() {
            this.humanResourcesInfoModal = {
                title: `${this.$route.meta.title} - 信息`,
                btnTitle: '保存',
                alert: false,
                loading: false,
                isShowStatus: false
            };
            this.editInfoFormNewObj = {};
        },
        // 确认按钮
        humanResourcesInfoModalSaveFn() {
            if (!this.checkEditInfoFormMsgFn()) return; // 前端格式校验
            // TODO: 删除角色，应该判断是否有改变

            if (this.datas.length > 0 && this.btnName === 'edit') {
                if (
                    !this.login_account_status &&
                    this.currentTrObj.user_inner_sn !== null &&
                    this.currentTrObj.user_inner_sn
                ) {
                    this.delUserRole();
                }
                if (this.humanResourcesInfoModal.isShowStatus && this.checkEditInfoFormObjFn()) {
                    // 修改按钮触发的保存要处理后端传值校验
                    this.humanResourcesInfoModalCancelFn();
                    return;
                }
            }
            this.humanResourcesInfoModal.loading = true;
            if (this.btnName === 'edit') {
                // 修改按钮
                this.userSystemTypeFlag
                    ? this.platformEmpEditAPIFn(this.editInfoFormNewObj)
                    : this.orgEmpEditAPIFn(this.editInfoFormNewObj);
            } else {
                this.userSystemTypeFlag
                    ? this.platformEmpAddAPIFn(this.editInfoFormObj)
                    : this.orgEmpAddAPIFn(this.editInfoFormObj);
            }
        },
        // 上传图片按钮操作
        uploadBtnFn() {
            this.$warning({
                title: '操作提示',
                content: '该功能在开发中，敬请期待',
                okText: '知道了',
                centered: true,
                maskClosable: false
            });
        },
        // 搜索按钮操作
        searchBtnFn() {
            if (!this.checkSearchInfoFormMsgFn()) return;
            this.datas = [];
            this.pagination = {
                loading: true,
                total: 10,
                current: 1,
                pageSize: 10
            };
            this.userSystemTypeFlag ? this.platformEmpListAPIFn() : this.orgEmpListAPIFn();
        },
        checkSearchInfoFormMsgFn() {
            // 名称
            if (this.$Utils.checkInputValFn(this.searchTables.name)) {
                this.$warning({
                    title: '操作提示',
                    content: `姓名${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 申请时间的搜索改变事件
        changeRangePickerFn(value, dateString) {
            const [beginTime, endTime] = dateString;
            this.searchTables.begin_time = beginTime;
            this.searchTables.end_time = endTime;
        },
        // 生日改变事件
        changeBirthdayPickerFn(value, dateString) {
            this.editInfoFormObj.birthday = dateString;
        },
        disabledStartDate(startValue) {
            if (!startValue || !this.departure_date) return false;
            return startValue.valueOf() > this.departure_date.valueOf();
        },
        disabledEndDate(endValue) {
            if (!endValue || !this.entry_date) return false;
            return this.entry_date.valueOf() >= endValue.valueOf();
        },
        // 入职日期改变事件
        changeEntryDatePickerFn(value, dateString) {
            this.entry_date = value;
            this.editInfoFormObj.entry_date = dateString;
        },
        // 离职日期改变事件
        changeDepartureDatePickerFn(value, dateString) {
            this.departure_date = value;
            this.editInfoFormObj.departure_date = dateString;
        },
        // 点击高级搜索的小图标的事件
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
            this.searchTables = {
                begin_time: '',
                end_time: '',
                name: '',
                status: ''
            };
            this.rangePickerVal = moment[''];
        },
        // 获取数据的索引值【坐标】 === 增删改
        getDatasLocationFn(record, list, i) {
            // record: 后端数据   list: 要改变的数据源  i: 索引
            let num = this.currentTrObj.indexAry;
            if (num.length <= 1) {
                // 只有一级数据
                switch (this.btnName) {
                    case 'addChild': // 新增下级部门
                        break;
                    case 'addSameLevel': // 新增同级部门
                        break;
                    case 'edit': // 修改
                        this.datas.splice(num[num.length - 1], 1, {
                            ...list,
                            name: this.editInfoFormObj.name,
                            director_name: this.editInfoFormObj.director_name,
                            employee_sn: this.editInfoFormObj.employee_sn,
                            position: this.editInfoFormObj.position,
                            phone: this.editInfoFormObj.phone,
                            status: this.editInfoFormObj.status,
                            sex_text:
                                this.editInfoFormObj.sex && !isNaN(this.editInfoFormObj.sex)
                                    ? this.sexOptions[parseInt(this.editInfoFormObj.sex)]
                                    : '',
                            status_text:
                                this.editInfoFormObj.status && !isNaN(this.editInfoFormObj.status)
                                    ? this.employeeOptions[parseInt(this.editInfoFormObj.status)]
                                    : '',
                            degree_text:
                                this.editInfoFormObj.degree && !isNaN(this.editInfoFormObj.degree)
                                    ? this.degreeOptions[parseInt(this.editInfoFormObj.degree)]
                                    : ''
                        });
                        break;
                    case 'remove':
                        break;
                }
                return;
            }
            ++i; // 多级数据
            list.children.forEach((item, index) => {
                if (index === num[i]) {
                    switch (this.btnName) {
                        case 'addChild': // 新增下级部门
                            if (i === num.length - 1) {
                                // 根据索引的值来判断
                                if (!item.children) {
                                    item.children = [];
                                }
                                item.children.push({
                                    ...record
                                });
                                this.treeDatasIndexAryFn(this.datas);
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas);
                            }
                            break;
                        case 'addSameLevel': // 新增同级部门
                            if (num.length === 2) {
                                this.datas[num[num.length - 2]].children.push({
                                    ...record
                                });
                                this.treeDatasIndexAryFn(this.datas);
                                break;
                            }
                            if (i === num.length - 2) {
                                // 根据索引的值来判断
                                if (!item.children) {
                                    item.children = [];
                                }
                                item.children.push({
                                    ...record
                                });
                                this.treeDatasIndexAryFn(this.datas);
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas);
                            }
                            break;
                        case 'edit': // 修改
                            if (i === num.length - 1) {
                                // 根据索引的值来判断
                                list.children.splice(num[num.length - 1], 1, {
                                    ...item,
                                    name: this.editInfoFormObj.name,
                                    department_sn: this.editInfoFormObj.department_sn,
                                    director_inner_sn: this.editInfoFormObj.director_inner_sn,
                                    director_name: this.editInfoFormObj.director_name,
                                    phone: this.editInfoFormObj.phone,
                                    status: this.editInfoFormObj.status,
                                    sex_text:
                                        this.editInfoFormObj.sex && !isNaN(this.editInfoFormObj.sex)
                                            ? this.sexOptions[parseInt(this.editInfoFormObj.sex)]
                                            : '',
                                    status_text:
                                        this.editInfoFormObj.status && !isNaN(this.editInfoFormObj.status)
                                            ? this.employeeOptions[parseInt(this.editInfoFormObj.status)]
                                            : '',
                                    degree_text:
                                        this.editInfoFormObj.degree && !isNaN(this.editInfoFormObj.degree)
                                            ? this.degreeOptions[parseInt(this.editInfoFormObj.degree)]
                                            : ''
                                });
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas);
                            }
                            break;
                        case 'remove':
                            if (num.length === 2) {
                                this.datas[num[num.length - 2]].children.splice(num[num.length - 1], 1);
                                if (
                                    this.datas[num[num.length - 2]].children &&
                                    this.datas[num[num.length - 2]].children.length <= 0
                                ) {
                                    this.datas[num[num.length - 2]].children = null;
                                }
                            } else if (i === num.length - 2) {
                                // 根据索引的值来判断
                                item.children.splice(num[num.length - 1], 1);
                                if (item.children && item.children.length <= 0) {
                                    item.children = null;
                                }
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas);
                            }
                            this.treeDatasIndexAryFn(this.datas);
                            if (this.datas.length) {
                                this.currentTrObj = this.datas[0];
                            } else {
                                this.currentTrObj = {};
                            }
                            break;
                    }
                }
            });
        },
        // 递归加上索引
        treeDatasIndexAryFn(datasOrigin, isAppendDatas, id) {
            // datasOrigin: 源数据  isAppendDatas: 将数据放在哪里  id: 当前点击的是哪个id标识
            const _this = this;
            // 格式化部门名称
            function formatDepartmentNameFn(strComma) {
                // 研发部门,行政部门,其他
                if (!strComma) return strComma;
                return strComma.replace(/,/g, ' ');
            }
            // 子级递归
            function childrenIndexFn(list, pIndexAry, parentDepartmentNameOld, pItemDisabledFlag) {
                // itemDisabledFlag: 判断子级是否可选
                if (!pIndexAry || !parentDepartmentNameOld) return;
                return list.map((cItem, cIndex) => {
                    let cItemDisabledFlag =
                        _this.btnName === 'edit' &&
                        (pItemDisabledFlag || (isAppendDatas === 'parentDirectors' && id === cItem.employee_inner_sn));
                    if (cItem.children && cItem.children.length > 0) {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn,
                            activeClass: '',
                            sex_text: _this.sexOptions[parseInt(cItem.sex)],
                            status_text: _this.employeeOptions[parseInt(cItem.status)],
                            degree_text: _this.degreeOptions[parseInt(cItem.degree)],
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            department_name_new: formatDepartmentNameFn(cItem.department_name),
                            disabled:
                                cItemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(cItem.status) <= 0),
                            value: (cItem.employee_inner_sn || cItem.department_inner_sn).toString(),
                            label: cItem.name,
                            isLeaf: false,
                            children: childrenIndexFn(
                                cItem.children,
                                [...pIndexAry, cIndex],
                                [...parentDepartmentNameOld, cItem.name],
                                cItemDisabledFlag
                            )
                        };
                        if (obj.key === _this.infoKey) {
                            // 新增下级同级的时候选中新增的那一条
                            _this.currentTrObj = obj;
                        }
                        return obj;
                    } else {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn,
                            activeClass: '',
                            sex_text: _this.sexOptions[parseInt(cItem.sex)],
                            status_text: _this.employeeOptions[parseInt(cItem.status)],
                            degree_text: _this.degreeOptions[parseInt(cItem.degree)],
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            department_name_new: formatDepartmentNameFn(cItem.department_name),
                            disabled:
                                cItemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(cItem.status) <= 0),
                            value: (cItem.employee_inner_sn || cItem.department_inner_sn).toString(),
                            label: cItem.name,
                            children: cItem.children
                        };
                        if (obj.key === _this.infoKey) {
                            // 新增下级同级的时候选中新增的那一条
                            _this.currentTrObj = obj;
                        }
                        return obj;
                    }
                });
            }
            // 父级
            let datasOriginNew = datasOrigin.map((item, index) => {
                let itemDisabledFlag =
                    _this.btnName === 'edit' && isAppendDatas === 'parentDirectors' && id === item.employee_inner_sn;
                if (item.children && item.children.length > 0) {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn,
                        activeClass: '',
                        sex_text: _this.sexOptions[parseInt(item.sex)],
                        status_text: _this.employeeOptions[parseInt(item.status)],
                        degree_text: _this.degreeOptions[parseInt(item.degree)],
                        parentDepartmentName: [item.name],
                        department_name_new: formatDepartmentNameFn(item.department_name),
                        disabled: itemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(item.status) <= 0),
                        value: (item.employee_inner_sn || item.department_inner_sn).toString(),
                        label: item.name,
                        isLeaf: false,
                        children: childrenIndexFn(item.children, [index], [item.name], itemDisabledFlag)
                    };
                    if (obj.key === _this.infoKey) {
                        // 新增下级同级的时候选中新增的那一条
                        _this.currentTrObj = obj;
                    }
                    return obj;
                } else {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn,
                        activeClass: '',
                        sex_text: _this.sexOptions[parseInt(item.sex)],
                        status_text: _this.employeeOptions[parseInt(item.status)],
                        degree_text: _this.degreeOptions[parseInt(item.degree)],
                        parentDepartmentName: [item.name],
                        department_name_new: formatDepartmentNameFn(item.department_name),
                        disabled: itemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(item.status) <= 0),
                        value: (item.employee_inner_sn || item.department_inner_sn).toString(),
                        label: item.name,
                        children: item.children
                    };
                    if (obj.key === _this.infoKey) {
                        // 新增下级同级的时候选中新增的那一条
                        _this.currentTrObj = obj;
                    }
                    return obj;
                }
            });

            if (isAppendDatas) {
                switch (isAppendDatas) {
                    case 'parentDirectors': // 上级主管
                        this.parentDirectors = [...datasOriginNew];
                        break;
                    case 'platformDeptTree': // 规划图
                        this.humanResourcesAllTrees.children = [...datasOriginNew];
                        break;
                    case 'deptTopTree': // 所属部门
                        this.architectureAllTrees = [...datasOriginNew];
                        break;
                }
            } else {
                switch (this.tabsActiveKey) {
                    case 'list':
                        this.datas = datasOriginNew;
                        if (this.datas && this.datas.length > 0) {
                            this.$refs.xTable.reloadData(this.datas);
                        }
                        break;
                    case 'architecture':
                        this.humanResourcesAllTrees.children = datasOriginNew;
                        break;
                }
            }
        },
        cellClickyFn({ row }) {
            this.currentTrObj = row;
        },
        cellDblclickFn({ row }) {
            this.currentTrObj = row;
            this.groupBtnFn('edit');
        },
        // 登录系统的状态
        changeLoginAccountStatusFn(checked) {
            this.editInfoFormObj.login_account_status = checked ? 1 : 0;
        },
        // 上传图片
        beforeUploadFn(file) {
            const isPic = file.type === 'image/jpeg' || file.type === 'image/jpg' || file.type === 'image/png';
            if (!isPic) {
                this.$warning({
                    title: '操作提示',
                    content: '图片格式只能是【.jpeg/.jpg/.png】',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return isPic;
            }
            const isLt1M = file.size / 1024 / 1024 <= 1;
            if (!isLt1M) {
                this.$warning({
                    title: '操作提示',
                    content: '图片大小不能超过1M',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return isLt1M;
            }
            return true;
        },
        photoUrlChangeFn(info) {
            const files = info.file;
            if (files.status === 'done' && parseFloat(files.response.code) === 0) {
                this.key = files.response.data.key;
                this.imageUrl = this.$Utils.getPictureSrc(this.key);
            }
        },
        orgNameFn() {
            const EmployeeInfo = this.$cookie.get('EmployeeInfo'),
                orgNameNo = '医疗耗材供应链管理系统';
            this.humanResourcesAllTrees.name = EmployeeInfo ? JSON.parse(EmployeeInfo).OrgName || orgNameNo : orgNameNo;
        },
        easyTableResizeFn() {
            this.$nextTick(() => {
                // this.$refs.easytable.resize()
            });
        }
    },
    watch: {
        '$store.state.appMenuCollapsed'() {
            // 解决表格宽度不会改变的问题
            this.easyTableResizeFn();
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 214 : 154,
                pager = 52;
            return clientHeight - topHeight - pager;
        }
    },
    created() {
        this.userGetRoleListAPIFn();
        this.orgNameFn();
    }
};
</script>

<style lang="less" scoped>
.modal-form-input-scoped {
    .modal-form-input {
        .label {
            width: 94px;
        }
    }
}

.scoped .btn-top-wrap .input-search-wrap .search {
    // 修复顶部右侧搜索默认的margin-right
    margin-right: 0;
}
</style>
