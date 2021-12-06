<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="24">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="groupBtnFn('addChild')" type="primary" size="small">
                                新增下级部门
                            </a-button>
                            <a-button @click="groupBtnFn('addSameLevel')" type="primary" size="small">
                                新增同级部门
                            </a-button>
                        </li>
                        <li>
                            <a-button @click="groupBtnFn('edit')" type="primary" size="small"> 修改 </a-button>
                            <a-button @click="groupBtnFn('remove')" type="danger" size="small"> 删除 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab切换区域开始 -->
        <div
            class="tab-table-wrap tab-table-overflowy"
            :class="tabsActiveKey === 'list' ? 'cur-overflow-hidden' : 'cur-overflow-y'"
        >
            <!-- 表格头部 -->
            <a-tabs @change="changeTabsFn" type="card">
                <a-tab-pane tab="列表" :key="systemTypes[0]" style="padding: 8px">
                    <a-table
                        :columns="columns"
                        :dataSource="datas"
                        :pagination="false"
                        bordered
                        :loading="organizationalStructureLoading"
                        :customRow="customRowFn"
                        :expandedRowKeys="expandedRows"
                        :scroll="{ y: scrollYNumber }"
                        :rowClassName="rowClassFn"
                        @expand="expandFn"
                    >
                        <!--审核状态重写-->
                        <div
                            slot="status_text"
                            slot-scope="text, record"
                            :class="{
                                'red': record.status !== 1
                            }"
                            >{{ text }}</div
                        >
                    </a-table>
                </a-tab-pane>
                <a-tab-pane
                    tab="架构图"
                    :key="systemTypes[1]"
                    class="architecture-diagram-wrap architecture-plan-wrap"
                    style="padding: 8px"
                >
                    <div class="architecture-diagram-checkbox-group">
                        <a-checkbox-group :options="options" :value="plainOptions" @change="onChangeFn" />
                        <!-- 前面的复选框隐藏掉了，详见App.vue 样式 .ant-checkbox -->
                    </div>
                    <vue2-org-tree
                        :data="architectureAllTrees"
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
            v-model="architectureInfoModal.alert"
            :centered="true"
            :maskClosable="false"
            :width="600"
            class="modal-form-input-scoped organizational-structure global-drag"
        >
            <div v-globalDrag="{ el: 'organizational-structure' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ architectureInfoModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="architectureInfoModalCancelFn"> 取消 </a-button>
                <a-button
                    key="submit"
                    type="primary"
                    :loading="architectureInfoModal.loading"
                    @click="architectureInfoModalSaveFn"
                >
                    保存
                </a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <div class="item-children">
                            <span class="label"><em class="em red">*</em>名称</span>
                            <a-input v-model="editInfoFormObj.name" placeholder="" class="input" />
                        </div>
                        <div class="item-children">
                            <span class="label">部门编号</span>
                            <a-input v-model="editInfoFormObj.department_sn" placeholder="" class="input" />
                        </div>
                    </li>
                    <li class="item">
                        <span class="label">上级部门</span>
                        <a-input
                            v-model="editInfoFormObj.parent_department_inner_name"
                            :disabled="true"
                            class="input"
                        />
                        <!--<a-cascader-->
                        <!--:options="superiorDepartments"-->
                        <!--v-model="superiorDepartmentDefaults"-->
                        <!--@change="changeSuperiorDepartmentsFn"-->
                        <!--changeOnSelect-->
                        <!--placeholder=""-->
                        <!--class="input"-->
                        <!--/>-->
                    </li>
                    <li class="item">
                        <div class="item-children">
                            <span class="label"><em class="em"></em>主管</span>
                            <a-tree-select
                                treeNodeFilterProp="title"
                                searchPlaceholder=""
                                :showSearch="true"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                :treeData="parentDirectors"
                                treeDefaultExpandAll
                                v-model="editInfoFormObj.director_inner_sn"
                                @change="changeDirectorInnerSnFn"
                                :allowClear="false"
                                class="input"
                            >
                            </a-tree-select>
                        </div>
                        <div class="item-children">
                            <span class="label"><em class="em"></em>电话</span>
                            <a-input v-model="editInfoFormObj.phone" placeholder="" class="input" />
                        </div>
                    </li>
                    <li v-show="btnName === 'edit'" class="item">
                        <div class="item-children">
                            <span class="label"><em class="em"></em>有效</span>
                            <a-checkbox-group
                                :options="checkboxGroupStatus"
                                v-model="checkboxGroupCursVal"
                                @change="checkboxGroupCursValChangeFn"
                                class="input"
                            />
                        </div>
                    </li>
                    <li class="item item-description">
                        <span class="label"><em class="em"></em>备注</span>
                        <a-textarea
                            v-model="editInfoFormObj.description"
                            :autosize="{ minRows: 4, maxRows: 6 }"
                            class="input"
                            placeholder=""
                        />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 信息修改区域结束 -->
    </div>
</template>

<script>
import {
    platformEmpDropdownListAPI,
    platformDeptDelAPI,
    platformDeptAddAPI,
    platformDeptEditAPI,
    platformDeptInfoAPI,
    platformDeptChildrenlistAPI,
    platformDeptToplevelAPI,
    platformDeptTreeAPI,
    orgEmpDropdownListAPI,
    orgEmpListAPI,
    orgDeptTopTreeAPI,
    orgDeptTopParentListAPI,
    orgDeptTopNextListAPI,
    orgDeptInfoAPI,
    orgDeptAddAPI,
    orgDeptDelAPI,
    orgDeptEditAPI
} from '@/service/pageAjax';

export default {
    name: 'OrganizationalStructure',
    data() {
        return {
            userSystemTypeFlag: parseFloat(this.$vueCookie.get('userSystemType')) === 0, // 0： 平台 // 1 2 3 其他
            // 表格的配置
            organizationalStructureLoading: false,
            columns: [
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    width: '40em',
                    align: 'center'
                },
                {
                    title: '部门编号',
                    dataIndex: 'department_sn',
                    key: 'department_sn',
                    width: '17em',
                    align: 'center'
                },
                {
                    title: '主管',
                    dataIndex: 'director_name',
                    key: 'director_name',
                    width: '10em',
                    align: 'center'
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    key: 'phone',
                    width: '12em',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    align: 'center',
                    scopedSlots: { customRender: 'status_text' }
                }
            ],
            datas: [],
            systemTypes: ['list', 'architecture'],
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            tabsActiveKey: 'list', // 表格头部的tab选项卡切换的 key 值 【默认值为： 列表】
            statusAry: ['无效', '有效'], // 状态: 0-失效  1-有效
            // 修改弹框的配置
            architectureInfoModal: {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            },
            editInfoFormObj: {
                // 修改信息的模态框值 - 后端返回回来的
                department_inner_sn: '',
                department_sn: '',
                description: '',
                director_inner_sn: '',
                id: '',
                is_delete: '',
                name: '',
                parent: '',
                parent_department_inner_sn: '',
                parent_department_inner_name: '',
                phone: '',
                status: 1
            },
            editInfoFormOldObj: {}, // 修改信息的模态框值 - 后端返回回来的旧值
            editInfoFormNewObj: {}, // 修改信息的模态框值 - 前端根据页面修改的值放进去 【没有修改的不要向后端传递】
            btnName: '', // 记录顶部点击的是哪个按钮
            expandedRows: [], // 表格是否发过请求标识的容器
            checkboxGroupStatus: [
                // { label: '失效', value: 0 },
                { label: '', value: 1 } // 有效
            ], // 修改状态的模态框的多选框
            checkboxGroupCursVal: [], // 修改状态的模态框的当前选中的多选框值
            value: undefined,
            architectureAllTrees: {
                name: '云南栢田科技有限公司',
                status: 1, // 0：无效  1：有效
                rootFlag: true, // 判断是不是root级
                children: []
            },
            horizontal: false,
            options: [
                { label: '有效', value: 0 },
                { label: '无效', value: 1 }
            ],
            plainOptions: [],
            // 上级部门的多层递归
            superiorDepartments: [],
            superiorDepartmentDefaults: [], // 类似于行政区划的北京市：[110000, 110100, 110101]
            parentDirectors: [], // 主管
            infoKey: '' // 新增下级同级的key
        };
    },
    methods: {
        /* 平台的接口开始 */
        // 下拉列表使用【查询平台所有人员】
        async platformEmpDropdownListAPIFn() {
            await platformEmpDropdownListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const listNew = res.list.map((item) => {
                        item.children = null;
                        return item;
                    });
                    this.treeDatasIndexAryFn(listNew, 'parentDirectors'); // 上级主管
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【删除】平台部门
        async platformDeptDelAPIFn(id) {
            await platformDeptDelAPI(id)
                .then((res) => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                    if (parseFloat(res.code) === 0) {
                        if (!this.currentTrObj.parent_department_inner_sn) {
                            this.datas.splice(this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1], 1);
                            if (this.datas.length) {
                                this.currentTrObj = this.datas[0];
                                this.treeDatasIndexAryFn(this.datas);
                            } else {
                                this.datas = [];
                                this.currentTrObj = {};
                            }
                        } else {
                            this.getDatasLocationFn(this.currentTrObj, this.datas[this.currentTrObj.indexAry[0]], 0);
                        }
                        this.platformDeptTreeAPIFn();
                        this.architectureInfoModal.alert = false;
                        // this.$message.success('删除成功')
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                });
        },
        // 【新增】平台部门信息
        async platformDeptAddAPIFn() {
            let params = {
                name: this.editInfoFormObj.name
            };

            // 判断是新增子分类还是新增同级分类的按钮 [ 自定义分类父ID ]
            if (this.btnName === 'addChild') {
                // addChild addSameLevel
                params.parent_department_inner_sn = this.currentTrObj.department_inner_sn;
            } else {
                if (this.currentTrObj.parent_department_inner_sn) {
                    params.parent_department_inner_sn = this.currentTrObj.parent_department_inner_sn;
                }
            }

            if (this.editInfoFormObj.director_inner_sn) {
                params.director_inner_sn = this.editInfoFormObj.director_inner_sn;
            }

            if (this.editInfoFormObj.department_sn) {
                params.department_sn = this.editInfoFormObj.department_sn;
            }

            if (this.editInfoFormObj.phone) {
                params.phone = this.editInfoFormObj.phone;
            }

            if (this.editInfoFormObj.description) {
                params.description = this.editInfoFormObj.description;
            }

            await platformDeptAddAPI(params)
                .then((res) => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                    if (parseFloat(res.code) === 0) {
                        let { info } = res;
                        info['key'] = info.department_inner_sn;
                        info['status_text'] = this.statusAry[parseFloat(info.status)];
                        info['loadingChildrenFlag'] = false;
                        info['children'] = parseFloat(info.is_leaf) === 0 ? [] : null;

                        this.infoKey = info['key']; // 选中当前新增的那一条数据的key

                        if (!this.currentTrObj.parent_department_inner_sn) {
                            if (this.btnName === 'addChild') {
                                // 下级插入
                                if (
                                    !this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]]
                                        .children ||
                                    this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]]
                                        .children.length <= 0
                                ) {
                                    this.datas[
                                        this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]
                                    ].children = [];
                                }
                                this.datas[
                                    this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]
                                ].children.push(info);
                            } else if (this.btnName === 'addSameLevel') {
                                // 同级插入
                                this.datas.push(info);
                            }
                            this.treeDatasIndexAryFn(this.datas);
                        } else {
                            this.getDatasLocationFn(info, this.datas[this.currentTrObj.indexAry[0]], 0);
                        }
                        this.platformDeptTreeAPIFn();
                        this.architectureInfoModal.alert = false;
                        // this.$message.success(`${this.architectureInfoModal.btnTitle}成功`)
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                });
        },
        // 【修改】平台部门信息
        async platformDeptEditAPIFn(editInfoFormNewObj) {
            // 传递给后端的参数，没有的值就不要传递了
            let params = {
                department_inner_sn: this.editInfoFormObj.department_inner_sn,
                ...editInfoFormNewObj
            };
            await platformDeptEditAPI(params)
                .then((res) => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                    if (parseFloat(res.code) === 0) {
                        this.getDatasLocationFn(this.currentTrObj, this.datas[this.currentTrObj.indexAry[0]], 0);
                        this.platformDeptTreeAPIFn();
                        this.architectureInfoModal.alert = false;
                        // this.$message.success(`${this.architectureInfoModal.btnTitle}成功`)
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                });
        },
        // 根据平台部门id【查询平台部门信息】
        async platformDeptInfoAPIFn(id) {
            await platformDeptInfoAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res,
                        infos = {
                            ...info,
                            director_inner_sn: info.director_inner_sn ? info.director_inner_sn.toString() : null,
                            parent_department_inner_sn: info.parent_department_inner_sn
                                ? info.parent_department_inner_sn.toString()
                                : null
                        };
                    // 多选框赋值 1:有效赋值
                    this.checkboxGroupCursVal =
                        parseFloat(infos.status) === 1 ? [this.checkboxGroupStatus[0].value] : [];
                    this.editInfoFormObj = { ...this.editInfoFormObj, ...infos };
                    this.editInfoFormOldObj = { ...infos }; // 为了比对值的临时存储
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据平台部门id【查询平台该部门的子部门列表】
        async platformDeptChildrenlistAPIFn(record, isAddFlag) {
            await platformDeptChildrenlistAPI(record.department_inner_sn).then((res) => {
                this.organizationalStructureLoading = false;
                const { code, list } = res;
                if (parseFloat(code) === 0) {
                    // 给列表的表格赋值
                    const rowsNew = list.map((item, index) => {
                        item.key = item.department_inner_sn;
                        item.activeClass = '';
                        item.status_text = this.statusAry[parseFloat(item.status)];
                        item.indexAry = [...record.indexAry, index];
                        item.parentDepartmentName = [...record.parentDepartmentName, item.name];
                        item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                        item.is_leaf = parseFloat(item.is_leaf);
                        if (item.is_leaf === 0) {
                            item.children = [];
                        } else {
                            delete item.children;
                        }
                        return item;
                    });
                    record.children = rowsNew.length > 0 ? rowsNew : null;
                    record.loadingChildrenFlag = true;
                    if (isAddFlag) {
                        this.platformDeptAddAPIFn(record, this.datas);
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【查询平台所有顶级部门列表】
        async platformDeptToplevelAPIFn() {
            await platformDeptToplevelAPI()
                .then((res) => {
                    this.organizationalStructureLoading = false;
                    const { code, list } = res;
                    if (parseFloat(code) === 0) {
                        // 给列表的表格赋值
                        this.datas = list.map((item, index) => {
                            item.key = item.department_inner_sn;
                            item.activeClass = '';
                            item.status_text = this.statusAry[parseFloat(item.status)];
                            item.indexAry = [index];
                            item.is_leaf = parseFloat(item.is_leaf);
                            if (item.is_leaf === 0) {
                                item.children = [];
                            } else {
                                delete item.children;
                            }
                            item.parentDepartmentName = [item.name];
                            item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                            return item;
                        });
                        if (this.datas.length) {
                            this.currentTrObj = this.datas[0];
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.organizationalStructureLoading = false;
                });
        },
        // 【查询平台所有部门】
        async platformDeptTreeAPIFn() {
            await platformDeptTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'platformDeptTree');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /* 平台的接口结束 */

        /* 经销商-医院-厂商的接口开始 */
        // 【查询经销商人员树】
        async orgEmpListAPIFn() {
            await orgEmpListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows } = res;
                    this.treeDatasIndexAryFn(rows, 'parentDirectors'); // 上级主管
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【查询机构所有人员】用于下列列表
        async orgEmpDropdownListAPIFn() {
            await orgEmpDropdownListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const listNew = res.list.map((item) => {
                        item.children = null;
                        return item;
                    });
                    this.treeDatasIndexAryFn(listNew, 'parentDirectors'); // 上级主管
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【删除】经销商部门
        async orgDeptDelAPIFn(did) {
            await orgDeptDelAPI(did)
                .then((res) => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                    if (parseFloat(res.code) === 0) {
                        if (!this.currentTrObj.parent_department_inner_sn) {
                            this.datas.splice(this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1], 1);
                            if (this.datas.length) {
                                this.currentTrObj = this.datas[0];
                                this.treeDatasIndexAryFn(this.datas);
                            } else {
                                this.datas = [];
                                this.currentTrObj = {};
                            }
                        } else {
                            this.getDatasLocationFn(this.currentTrObj, this.datas[this.currentTrObj.indexAry[0]], 0);
                        }
                        this.orgDeptTopTreeAPIFn();
                        this.architectureInfoModal.alert = false;
                        // this.$message.success('删除成功')
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                });
        },
        // 【新增】经销商部门
        async orgDeptAddAPIFn() {
            let params = {
                name: this.editInfoFormObj.name
            };

            // 判断是新增子分类还是新增同级分类的按钮 [ 自定义分类父ID ]
            if (this.btnName === 'addChild') {
                // addChild addSameLevel
                params.parent_department_inner_sn = this.currentTrObj.department_inner_sn;
            } else {
                if (this.currentTrObj.parent_department_inner_sn) {
                    params.parent_department_inner_sn = this.currentTrObj.parent_department_inner_sn;
                }
            }

            if (this.editInfoFormObj.director_inner_sn) {
                params.director_inner_sn = this.editInfoFormObj.director_inner_sn;
            }

            if (this.editInfoFormObj.department_sn) {
                params.department_sn = this.editInfoFormObj.department_sn;
            }

            if (this.editInfoFormObj.phone) {
                params.phone = this.editInfoFormObj.phone;
            }

            if (this.editInfoFormObj.description) {
                params.description = this.editInfoFormObj.description;
            }

            await orgDeptAddAPI(params)
                .then((res) => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                    if (parseFloat(res.code) === 0) {
                        let { info } = res;
                        info['key'] = info.department_inner_sn;
                        info['status_text'] = this.statusAry[parseFloat(info.status)];
                        info['loadingChildrenFlag'] = false;
                        info['children'] = parseFloat(info.is_leaf) === 0 ? [] : null;

                        this.infoKey = info['key']; // 选中当前新增的那一条数据的key

                        if (!this.currentTrObj.parent_department_inner_sn) {
                            if (this.btnName === 'addChild') {
                                // 下级插入
                                if (
                                    !this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]]
                                        .children ||
                                    this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]]
                                        .children.length <= 0
                                ) {
                                    this.datas[
                                        this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]
                                    ].children = [];
                                }
                                this.datas[
                                    this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]
                                ].children.push(info);
                            } else if (this.btnName === 'addSameLevel') {
                                // 同级插入
                                this.datas.push(info);
                            }
                            this.treeDatasIndexAryFn(this.datas);
                        } else {
                            this.getDatasLocationFn(info, this.datas[this.currentTrObj.indexAry[0]], 0);
                        }
                        this.orgDeptTopTreeAPIFn();
                        this.architectureInfoModal.alert = false;
                        // this.$message.success(`${this.architectureInfoModal.btnTitle}成功`)
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                });
        },
        // 【编辑】经销商部门
        async orgDeptEditAPIFn(editInfoFormNewObj) {
            // 传递给后端的参数，没有的值就不要传递了
            let params = {
                ...editInfoFormNewObj
            };
            await orgDeptEditAPI(this.currentTrObj.department_inner_sn, params)
                .then((res) => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                    if (parseFloat(res.code) === 0) {
                        this.getDatasLocationFn(this.currentTrObj, this.datas[this.currentTrObj.indexAry[0]], 0);
                        this.orgDeptTopTreeAPIFn();
                        this.architectureInfoModal.alert = false;
                        // this.$message.success(`${this.architectureInfoModal.btnTitle}成功`)
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.architectureInfoModal.loading = false;
                    this.organizationalStructureLoading = false;
                });
        },
        // 根据机构ID和部门ID查询经销商【部门详情】
        async orgDeptInfoAPIFn(did) {
            await orgDeptInfoAPI(did).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res,
                        infos = {
                            ...info,
                            director_inner_sn: info.director_inner_sn ? info.director_inner_sn.toString() : null,
                            parent_department_inner_sn: info.parent_department_inner_sn
                                ? info.parent_department_inner_sn.toString()
                                : null
                        };
                    // 多选框赋值 1:有效赋值
                    this.checkboxGroupCursVal =
                        parseFloat(infos.status) === 1 ? [this.checkboxGroupStatus[0].value] : [];
                    this.editInfoFormObj = { ...this.editInfoFormObj, ...infos };
                    this.editInfoFormOldObj = { ...infos }; // 为了比对值的临时存储
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID和上级部门ID查询经销商【所有下级部门】
        async orgDeptTopNextListAPIFn(record, isAddFlag) {
            // 每一次的item, 递归的值{item, index}
            await orgDeptTopNextListAPI(record && record.department_inner_sn)
                .then((res) => {
                    this.organizationalStructureLoading = false;
                    const { code, info } = res;
                    if (parseFloat(code) === 0) {
                        // 给列表的表格赋值
                        const rowsNew = info.map((item, index) => {
                            item.key = item.department_inner_sn;
                            item.activeClass = '';
                            item.status_text = this.statusAry[parseFloat(item.status)];
                            item.indexAry = [...record.indexAry, index];
                            item.parentDepartmentName = [...record.parentDepartmentName, item.name];
                            item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                            item.is_leaf = parseFloat(item.is_leaf);
                            if (item.is_leaf === 0) {
                                item.children = [];
                            } else {
                                delete item.children;
                            }
                            return item;
                        });
                        record.children = rowsNew.length > 0 ? rowsNew : null;
                        record.loadingChildrenFlag = true;
                        if (isAddFlag) {
                            this.orgDeptAddAPIFn(record, this.datas);
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.organizationalStructureLoading = false;
                });
        },
        // 根据机构ID查询经销商【所有一级部门】
        async orgDeptTopParentListAPIFn() {
            await orgDeptTopParentListAPI().then((res) => {
                this.organizationalStructureLoading = false;
                const { code, info } = res;
                if (parseFloat(code) === 0) {
                    // 给列表的表格赋值
                    this.datas = info.map((item, index) => {
                        item.key = item.department_inner_sn;
                        item.activeClass = '';
                        item.status_text = this.statusAry[parseFloat(item.status)];
                        item.indexAry = [index];
                        item.parentDepartmentName = [item.name];
                        item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                        if (item.is_leaf === 0) {
                            item.children = [];
                        } else {
                            delete item.children;
                        }
                        return item;
                    });
                    if (this.datas.length) {
                        this.currentTrObj = this.datas[0];
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID查询经销商【所有部门】
        async orgDeptTopTreeAPIFn() {
            await orgDeptTopTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'platformDeptTree');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /* 经销商-医院-厂商的接口结束 */

        // 上级部门数据选择完成后的回调
        changeSuperiorDepartmentsFn(value) {
            this.editInfoFormObj.parent_department_inner_sn = value[value.length - 1];
        },
        // tab切换
        changeTabsFn(activeKey) {
            this.tabsActiveKey = activeKey;
        },
        // 设置行属性 —— 点击每一行的事件
        customRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentTrObj = record;
                    },
                    dblclick: (e) => {
                        if ((e.srcElement || e.target).classList.contains('ant-table-row-expand-icon')) return;
                        this.currentTrObj = record;
                        this.groupBtnFn('edit');
                    }
                }
            };
        },
        // 上级主管的中文值
        changeDirectorInnerSnFn(value, label) {
            this.editInfoFormObj.director_name = label[label.length - 1];
        },
        // 获取数据的索引值【坐标】
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
                            department_sn: this.editInfoFormObj.department_sn,
                            director_inner_sn: this.editInfoFormObj.director_inner_sn,
                            director_name: this.editInfoFormObj.director_name,
                            phone: this.editInfoFormObj.phone,
                            status: this.editInfoFormObj.status,
                            status_text: this.statusAry[parseFloat(this.editInfoFormObj.status)]
                        });
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
                                    status_text: this.statusAry[parseFloat(this.editInfoFormObj.status)]
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
        // 初始化数据【搜索按钮】
        initTablesFn() {
            this.currentTrObj = {};
            this.tabsActiveKey = 'list';
            this.architectureInfoModal = {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            };
            this.editInfoFormObj = {
                // 修改信息的模态框值 - 后端返回回来的
                department_inner_sn: '',
                department_sn: '',
                description: '',
                director_inner_sn: '',
                id: '',
                is_delete: '',
                name: '',
                parent: '',
                parent_department_inner_sn: '',
                parent_department_inner_name: '',
                phone: '',
                status: 1
            };
            this.editInfoFormOldObj = {};
            this.editInfoFormNewObj = {};
            this.btnName = '';
            this.expandedRows = [];
            this.checkboxGroupStatus = [{ label: '', value: 1 }];
            this.checkboxGroupCursVal = [];
            this.value = '';
            this.architectureAllTrees = {
                name: '云南栢田科技有限公司',
                status: 1, // 0：无效  1：有效
                rootFlag: true, // 判断是不是root级
                children: []
            };
            this.horizontal = false;
            this.plainOptions = [];
            this.superiorDepartments = [];
            this.superiorDepartmentDefaults = [];
            this.organizationalStructureLoading = true;
            this.datas = null;
            this.infoKey = '';
            this.parentDirectors = [];
            if (this.userSystemTypeFlag) {
                this.platformDeptTreeAPIFn();
                this.platformDeptToplevelAPIFn();
            } else {
                this.orgDeptTopParentListAPIFn();
                this.orgDeptTopTreeAPIFn();
            }
        },
        // 点击展开图标时触发
        expandFn(expanded, record, isAddFlag) {
            if (expanded) {
                this.expandedRows.push(record.department_inner_sn);
            } else {
                this.expandedRows = this.expandedRows.filter((item) => item !== record.department_inner_sn);
            }
            if (!record.loadingChildrenFlag) {
                this.organizationalStructureLoading = true;
                this.userSystemTypeFlag
                    ? this.platformDeptChildrenlistAPIFn(record, isAddFlag)
                    : this.orgDeptTopNextListAPIFn(record, isAddFlag);
            }
        },
        // 表单默认值重置
        architectureInfoModalResetFn() {
            this.editInfoFormObj = {
                department_inner_sn: this.currentTrObj.department_inner_sn,
                department_sn: '',
                description: '',
                director_inner_sn: '',
                id: '',
                is_delete: '',
                name: '',
                parent: '',
                parent_department_inner_sn: '',
                parent_department_inner_name: '',
                phone: '',
                status: 1
            };
            this.superiorDepartmentDefaults = [];
            this.checkboxGroupCursVal = [];
            this.infoKey = '';
        },
        // 判断多选框修改了没有 【没有修改就不要向后端传值】
        checkboxGroupCursValChangeFn(checkedValues) {
            this.editInfoFormObj.status = parseFloat(checkedValues[0]) || 0;
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
        // 显示弹框的上级部门名称
        showModalParentDeparentNameFn() {
            if (this.btnName === 'addChild' && !this.currentTrObj.parent_department_inner_sn) {
                this.editInfoFormObj.parent_department_inner_name = this.currentTrObj.name;
            } else {
                if (!this.currentTrObj.parent_department_inner_sn) {
                    this.editInfoFormObj.parent_department_inner_name = '无';
                } else {
                    this.currentTrObj.parentDepartmentName.forEach((item, index) => {
                        if (index === 0) {
                            this.editInfoFormObj.parent_department_inner_name = item;
                        } else {
                            switch (this.btnName) {
                                case 'addChild':
                                    this.editInfoFormObj.parent_department_inner_name += `/${item}`;
                                    break;
                                case 'addSameLevel':
                                    if (this.currentTrObj.parentDepartmentName.length - 1 === index) return;
                                    this.editInfoFormObj.parent_department_inner_name += `/${item}`;
                                    break;
                                case 'edit':
                                    if (this.currentTrObj.parentDepartmentName.length - 1 === index) return;
                                    this.editInfoFormObj.parent_department_inner_name += `/${item}`;
                                    break;
                            }
                        }
                    });
                }
            }
        },
        // 顶部按钮的触发事件 btnName
        groupBtnFn(btnName) {
            this.btnName = btnName;
            if (this.btnName !== 'addSameLevel' && !this.checkCurrentTrDataFn()) return; // 除了新增同级分类按钮不需要选中表格中的某一行数据， 其它按钮都需要选中某一行才能进行下步操作
            this.architectureInfoModalResetFn();
            if (this.btnName !== 'remove') {
                if (this.userSystemTypeFlag) {
                    this.platformEmpDropdownListAPIFn();
                } else {
                    this.orgEmpDropdownListAPIFn();
                }
                this.showModalParentDeparentNameFn();
            }
            // this.treeDatasIndexAryFn(this.datas)
            // this.treeDatasIndexAryFn(this.architectureAllTrees.children, 'platformDeptTree')

            switch (btnName) {
                case 'addChild': {
                    this.architectureInfoModal = {
                        title: `${this.$route.meta.title} - 新增下级部门`,
                        btnTitle: '新增下级部门保存',
                        alert: true,
                        loading: false,
                        isShowStatus: false
                    };
                    this.$Utils.globalDragCenterFn('organizational-structure');
                    break;
                }
                case 'addSameLevel':
                    this.architectureInfoModal = {
                        title: `${this.$route.meta.title} - 新增同级部门`,
                        btnTitle: '新增同级部门保存',
                        alert: true,
                        loading: false,
                        isShowStatus: false
                    };
                    this.$Utils.globalDragCenterFn('organizational-structure');
                    break;
                case 'edit': {
                    this.architectureInfoModal = {
                        title: `${this.$route.meta.title} - 修改`,
                        btnTitle: '修改保存',
                        alert: true,
                        loading: false,
                        isShowStatus: true
                    };
                    this.$Utils.globalDragCenterFn('organizational-structure');
                    this.userSystemTypeFlag
                        ? this.platformDeptInfoAPIFn(this.currentTrObj.department_inner_sn)
                        : this.orgDeptInfoAPIFn(this.currentTrObj.department_inner_sn);
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
                            _this.userSystemTypeFlag
                                ? _this.platformDeptDelAPIFn(_this.currentTrObj.department_inner_sn, _this.datas)
                                : _this.orgDeptDelAPIFn(_this.currentTrObj.department_inner_sn, _this.datas);

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
            const linearGradientAry = ['department-no', 'department-yes', 'department-defaults'], // 无效：0  有效：1   默认根颜色：2
                status = data.rootFlag ? linearGradientAry.length - 1 : parseFloat(data.status),
                curClass = data.department_inner_sn === this.currentTrObj.key ? ' cur' : '';

            return h(
                'div',
                {
                    'class': 'architecture-diagram ' + linearGradientAry[status] + curClass
                },
                [
                    h(
                        'div',
                        {
                            'class': 'font-size'
                        },
                        data.name
                    ),
                    h(
                        'div',
                        {
                            'class': 'font-size'
                        },
                        data.director_name || '-'
                    ),
                    h(
                        'div',
                        {
                            'class': 'font-size'
                        },
                        data.phone || '-'
                    )
                ]
            );
        },
        onNodeClick(e, data) {
            if (!data.department_inner_sn) return;
            this.currentTrObj = data;

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
            this.editInfoFormObj.name = this.$Utils.trim(this.editInfoFormObj.name);
            this.editInfoFormObj.phone = this.$Utils.trim(this.editInfoFormObj.phone);

            // 名称
            if (!this.editInfoFormObj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
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
                    if (server === old && serverItem !== oldItem) {
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
        architectureInfoModalCancelFn() {
            this.architectureInfoModal = {
                title: `${this.$route.meta.title} - 信息`,
                btnTitle: '保存',
                alert: false,
                loading: false,
                isShowStatus: false
            };
            this.editInfoFormNewObj = {};
        },
        // 确认按钮
        architectureInfoModalSaveFn() {
            if (!this.checkEditInfoFormMsgFn()) return; // 前端格式校验

            if (this.architectureInfoModal.isShowStatus && this.checkEditInfoFormObjFn()) {
                // 修改按钮触发的保存要处理后端传值校验
                this.architectureInfoModalCancelFn();
                return;
            }
            this.architectureInfoModal.loading = true;
            if (this.architectureInfoModal.isShowStatus) {
                // 修改按钮
                this.userSystemTypeFlag
                    ? this.platformDeptEditAPIFn(this.editInfoFormNewObj, this.datas)
                    : this.orgDeptEditAPIFn(this.editInfoFormNewObj, this.datas);
            } else {
                if (this.tabsActiveKey === 'list') {
                    // 判断是否有加载过子级菜单
                    if (this.currentTrObj.loadingChildrenFlag) {
                        // 加载过
                        this.userSystemTypeFlag
                            ? this.platformDeptAddAPIFn(null, this.datas)
                            : this.orgDeptAddAPIFn(null, this.datas);
                    } else {
                        // 没有加载过
                        if (this.btnName === 'addSameLevel') {
                            this.userSystemTypeFlag
                                ? this.platformDeptAddAPIFn(null, this.datas)
                                : this.orgDeptAddAPIFn(null, this.datas);
                            return;
                        }
                        this.expandFn(true, this.currentTrObj, true);
                    }
                } else {
                    this.userSystemTypeFlag
                        ? this.platformDeptAddAPIFn(null, this.datas)
                        : this.orgDeptAddAPIFn(null, this.datas);
                }
            }
        },
        // 递归加上索引
        treeDatasIndexAryFn(datasOrigin, isAppendDatas) {
            // datasOrigin: 源数据  isAppendDatas: 将数据放在哪里
            const _this = this;
            // 子级递归
            function childrenIndexFn(list, pIndexAry, parentDepartmentNameOld) {
                if (!pIndexAry || !parentDepartmentNameOld) return;
                return list.map((cItem, cIndex) => {
                    if (cItem.children && cItem.children.length > 0) {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn,
                            activeClass: '',
                            status_text: _this.statusAry[parseFloat(cItem.status)],
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            value: (cItem.employee_inner_sn || cItem.department_inner_sn).toString(),
                            label: cItem.name,
                            isLeaf: false,
                            children: childrenIndexFn(
                                cItem.children,
                                [...pIndexAry, cIndex],
                                [...parentDepartmentNameOld, cItem.name]
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
                            status_text: _this.statusAry[parseFloat(cItem.status)],
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
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
                if (item.children && item.children.length > 0) {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn,
                        activeClass: '',
                        status_text: _this.statusAry[parseFloat(item.status)],
                        parentDepartmentName: [item.name],
                        value: (item.employee_inner_sn || item.department_inner_sn).toString(),
                        label: item.name,
                        isLeaf: false,
                        children: childrenIndexFn(item.children, [index], [item.name])
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
                        status_text: _this.statusAry[parseFloat(item.status)],
                        parentDepartmentName: [item.name],
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
                    case 'platformDeptTree': // 架构图
                        this.architectureAllTrees.children = [...datasOriginNew];
                        break;
                    default:
                        // 上级部门
                        this.superiorDepartments = [...datasOriginNew];
                }
            } else {
                switch (this.tabsActiveKey) {
                    case 'list':
                        this.datas = datasOriginNew;
                        break;
                    case 'architecture':
                        this.architectureAllTrees.children = datasOriginNew;
                        break;
                }
            }
        },
        // 表格的高亮样式显示
        rowClassFn(record) {
            if (record.key === this.currentTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        orgNameFn() {
            const EmployeeInfo = this.$cookie.get('EmployeeInfo'),
                orgNameNo = '医疗耗材供应链管理系统';
            this.architectureAllTrees.name = EmployeeInfo ? JSON.parse(EmployeeInfo).OrgName || orgNameNo : orgNameNo;
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 196;
            return clientHeight - topHeight;
        }
    },
    created() {
        if (this.userSystemTypeFlag) {
            this.platformDeptToplevelAPIFn();
            this.platformDeptTreeAPIFn();
        } else {
            this.orgDeptTopParentListAPIFn();
            this.orgDeptTopTreeAPIFn();
        }
        this.orgNameFn();
    }
};
</script>

<style lang="less" scoped>
.modal-form-input-scoped {
    .modal-form-input {
        .label {
            width: 66px;
        }
    }
}
</style>
