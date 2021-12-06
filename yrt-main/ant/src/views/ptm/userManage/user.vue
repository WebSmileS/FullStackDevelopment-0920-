<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button type="primary" size="small" @click="groupBtnFn('resetPwd')"> 重置密码 </a-button>
                            <a-button type="primary" size="small" @click="groupBtnFn('editPhone')"
                                ><!--editModal.alert = true-->
                                修改登录手机
                            </a-button>
                            <a-button type="primary" size="small" @click="groupBtnFn('setRole')"> 设定角色 </a-button>
                        </li>
                        <li>
                            <!-- <a-button
                                type="warning"
                                size="small"
                            >
                                停用
                            </a-button> -->
                            <a-button type="danger" size="small" @click="groupBtnFn('remove')"> 删除 </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-model="keyword"
                            placeholder="姓名/用户名/电话"
                            size="small"
                            enterButton
                            class="search"
                            @search="userGetUserListAPIFn"
                        />
                    </div>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab切换区域开始 -->
        <div class="tab-table-wrap tab-table-overflowy" style="padding: 8px">
            <vxe-table
                ref="xTable"
                size="small"
                :height="scrollYNumber - 14"
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
                @page-change="changetPaginationFn"
            ></vxe-pager>
        </div>
        <!-- tab切换区域结束 -->

        <!-- 修改登录手机区域开始 -->
        <a-modal
            v-model="editModal.alert"
            :width="400"
            :centered="true"
            :maskClosable="false"
            class="modal-form-input-scoped global-drag modal-edit-mobile"
        >
            <div v-globalDrag="{ el: 'modal-edit-mobile' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ editModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="editModalFn('cancel')"> 取消 </a-button>
                <a-button key="submit" type="primary" :loading="editModal.loading" @click="editModalFn('save')">
                    保存
                </a-button>
            </template>
            <div class="modal-form-input">
                <div class="form-top">
                    <div class="left-wrap">
                        <ul>
                            <li class="item">
                                <span class="label" style="width: 80px"><em class="em red">*</em>手机号</span>
                                <a-input placeholder="" class="input" v-model="userListFormObj.name" />
                            </li>
                            <li class="item">
                                <span class="label" style="width: 80px"><em class="em red">*</em>再次输入</span>
                                <a-input placeholder="" class="input" v-model="userListFormObj.againName" />
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </a-modal>
        <!-- 修改登录手机区域结束 -->

        <!-- 设定角色弹窗 v2 开始-->
        <a-modal
            title
            centered
            v-model="rolesPopFlag"
            @cancel="closeRoles"
            :width="1024"
            :height="800"
            :maskClosable="false"
            class="modal-form-input-scoped roles-modal global-drag"
        >
            <div v-globalDrag="{ el: 'roles-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    {{ roleModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="closeRoles">取消</a-button>
                <a-button key="submit" type="primary" @click="saveRole"> 保存 </a-button>
            </template>
            <div class="modal-form-input">
                <a-row class="tab-table-wrap">
                    <a-col :span="6" style="min-height: 500px">
                        <roleList
                            ref="rolesTable"
                            :roles="roles"
                            :editAble="false"
                            :loading="rolesLoadingFlag"
                            :rowChangeAble="true"
                            :rowChangeWarn="roleChangeWarnMsg"
                            @selectChange="selectedRoleChange"
                        ></roleList>
                    </a-col>
                    <a-col :span="18" style="max-height: 500px">
                        <roleTree
                            ref="rolesTrees"
                            :tableHeight="440"
                            :showHeader="false"
                            :trees="trees"
                            :checkAble="false"
                            :editAble="false"
                        ></roleTree>
                    </a-col>
                </a-row>
            </div>
        </a-modal>
        <!-- 设定角色弹窗 v2 结束 -->
    </div>
</template>

<script>
import roleList from '../common/roleList';
import roleTree from '../common/roleTree';
import {
    userGetUserListAPI,
    userResetPasswordAPI,
    userDeleteUserAPI,
    userChangeUsernameAPI,
    getRolePermissionsAPI,
    getGroupRolesAPI,
    getGroupAllTreeAPI,
    setUserRoleAPI
} from '@/service/pageAjax';
export default {
    name: 'user',
    components: {
        roleList,
        roleTree
    },
    data() {
        return {
            // 表格的配置
            columns: [
                {
                    title: '姓名',
                    field: 'Name',
                    key: 'Name',
                    minWidth: '20%'
                },
                {
                    title: '用户名',
                    field: 'User_name',
                    key: 'User_name',
                    minWidth: '20%'
                },
                {
                    title: '职位',
                    field: 'Position',
                    key: 'Position',
                    minWidth: '20%'
                },
                {
                    title: '角色',
                    field: 'Role',
                    key: 'Role',
                    minWidth: '20%'
                },
                {
                    title: '部门',
                    field: 'Department_name',
                    key: 'Department_name',
                    minWidth: '20%'
                }
            ],
            datas: [],
            currentTrObj: {},
            // 表格分页
            pagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            // 修改登录手机
            editModal: {
                name: 0, // 0: 修改用户名  1: 修改密码
                alert: false, // 控制模态框是否显示
                loading: false, // 控制点击保存按钮loading加载动画的标识
                title: `${this.$route.meta.title} - 修改登录手机`
            },
            userListFormObj: {
                name: '',
                againName: ''
            },
            roleModal: {
                alert: false, // 同上
                loading: false, // 同上
                title: `${this.$route.meta.title} - 设定角色`
            },
            roleColumns: [
                {
                    title: '角色',
                    dataIndex: 'name',
                    key: 'name',
                    aligin: 'left'
                }
            ],
            // 表格的配置
            orgTreeLoading: false,
            popRoleFlag: false,
            popMenuFlag: false,
            isEdit: false,
            menuColumns: [
                {
                    title: '权限',
                    dataIndex: 'Name',
                    key: 'Name',
                    width: '100%',
                    align: 'left'
                }
            ],
            selectedRow: {},
            selectedRowIndex: -1,
            orgTree: [],
            roles: [],
            rolesPopFlag: false, // 角色设定弹窗标志
            rolesLoadingFlag: false, // 角色列表加载标志
            roleChangeWarnMsg: '', // 角色变化警告词
            trees: [], // 角色菜单树
            selectedRole: {}, // 当前选中的角色
            keyword: '' // 查询关键字
        };
    },
    methods: {
        // 修改登录电话
        async userChangeUsernameAPIFn() {
            let params = {
                // login_account_status: 0,
                phone: this.userListFormObj.name,
                user_inner_sn: this.currentTrObj.User_inner_sn
            };
            await userChangeUsernameAPI(params)
                .then((res) => {
                    this.pagination.loading = false;
                    if (parseFloat(res.Code || res.code) === 0) {
                        this.resetUserModalFn();
                        this.editModal.alert = false;
                        this.userGetUserListAPIFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.pagination.loading = false;
                });
        },
        // 删除系统账号
        async userDeleteUserAPIFn(id) {
            userDeleteUserAPI(id)
                .then((res) => {
                    this.pagination.loading = false;
                    if (parseFloat(res.Code || res.code) === 0) {
                        this.userGetUserListAPIFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.pagination.loading = false;
                    this.$message.error('意外错误，请稍后再试');
                });

            // await delUserRoleAPI(id).then(res => {
            //     if (res.Code || res.code === 0) {
            //         // 角色删除成功，才删除用户
            //         userDeleteUserAPI(id).then(res => {
            //             this.pagination.loading = false
            //             if (parseFloat(res.Code || res.code) === 0) {
            //                 this.userGetUserListAPIFn()
            //             } else {
            //                 this.$message.error(res.Msg)
            //             }
            //         }).catch(() => {
            //             this.pagination.loading = false
            //         })
            //     } else {
            //         this.$message.error(res.Msg)
            //     }
            // }).catch((e) => {
            //     this.$message.error('意外错误，请稍后再试')
            // })
        },
        // 重置密码
        async userResetPasswordAPIFn(id) {
            await userResetPasswordAPI(id)
                .then((res) => {
                    this.pagination.loading = false;
                    if (parseFloat(res.Code || res.code) === 0) {
                        // this.Data.splice(num, 1);
                        this.userGetUserListAPIFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.pagination.loading = false;
                });
        },
        // 获取用户列表
        async userGetUserListAPIFn(num) {
            let params = {
                pageNum: this.pagination.current,
                pageSize: this.pagination.pageSize,
                keyword: this.keyword,
                // 排序
                orderByColumn: 'User_inner_sn',
                isAsc: 'desc'
            };

            await userGetUserListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const {
                            data: { Total: total, UserList }
                        } = res;
                        let rowsNew = UserList.map((item) => {
                            return {
                                ...item,
                                key: item.User_inner_sn,
                                Role: this.roleName(item.RoleID),
                                activeClass: ''
                            };
                        });
                        if (rowsNew.length > 0) {
                            if (typeof num === 'number') {
                                this.currentTrObj = rowsNew[num];
                            } else {
                                this.currentTrObj = rowsNew[0];
                            }
                            this.$refs.xTable.setCurrentRow(this.currentTrObj);
                        } else {
                            this.currentTrObj = {};
                        }
                        this.datas = rowsNew;
                        this.pagination = {
                            ...this.pagination,
                            total,
                            loading: false
                        };
                        this.$refs.xTable.refreshData();
                    } else {
                        this.$message.error(res.msg);
                        this.pagination.loading = false;
                    }
                })
                .catch(() => {
                    this.pagination.loading = false;
                });
        },
        cellClickyFn({ row }) {
            this.currentTrObj = row;
        },
        cellDblclickFn({ row }) {
            this.currentTrObj = row;
            this.groupBtnFn('edit');
        },
        // 判断表格里面是否有选中的数据
        checkedTrFlagFn() {
            if (Object.keys(this.currentTrObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                })``;
                return true;
            }
            return false;
        },
        // 判断提交表单的正确性
        checkedUserListTrFormFlagFn() {
            if (this.btnName === 'editPhone') {
                const regExpFn = this.$Utils.regExpFn(),
                    mobilePhone = regExpFn.mobilePhone;

                if (!this.userListFormObj.name) {
                    this.$warning({
                        title: '操作提示',
                        content: '登录手机不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return true;
                }

                if (this.userListFormObj.name.length > 11) {
                    this.$warning({
                        title: '操作提示',
                        content: '登录手机有效长度是11位',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return true;
                }

                if (!mobilePhone.test(this.userListFormObj.name)) {
                    this.$warning({
                        title: '操作提示',
                        content: '登录手机格式输入有误',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return true;
                }
                if (!this.userListFormObj.againName) {
                    this.$warning({
                        title: '操作提示',
                        content: '再次输入登录手机不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return true;
                }
                if (
                    this.$Utils.trim(this.userListFormObj.name + '') !==
                    this.$Utils.trim(this.userListFormObj.againName + '')
                ) {
                    this.$warning({
                        title: '操作提示',
                        content: '两次输入不一致',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return true;
                }
            }
            return false;
        },
        // 表单重置
        resetUserModalFn() {
            this.userListFormObj = {
                name: '',
                againName: ''
            };
        },
        // 顶部按钮操作
        groupBtnFn(btnName) {
            this.btnName = btnName;
            if (this.checkedTrFlagFn()) return;
            this.resetUserModalFn();
            const tips = '您确定要执行该操作吗？',
                _this = this;
            switch (btnName) {
                case 'resetPwd':
                    _this.$confirm({
                        title: '操作提示',
                        content: tips,
                        okText: '确认',
                        centered: true,
                        maskClosable: false,
                        onOk() {
                            _this.userResetPasswordAPIFn(_this.currentTrObj.User_inner_sn);
                        }
                    });
                    break;
                case 'editPhone':
                    _this.editModal = {
                        name: 0,
                        alert: true,
                        loading: false,
                        title: `${_this.$route.meta.title} - 修改登录手机`
                    };
                    _this.$Utils.globalDragCenterFn('modal-edit-mobile');
                    break;
                case 'setRole':
                    this.selectedRow = this.findRole(this.currentTrObj.RoleID);
                    this.popRoles();
                    break;
                case 'remove':
                    _this.$confirm({
                        title: '操作提示',
                        content: '删除用户登录账号后该用户将不能登录系统（不影响人员信息），确定要执行该操作吗？',
                        okText: '确认',
                        centered: true,
                        maskClosable: false,
                        onOk() {
                            _this.userDeleteUserAPIFn(_this.currentTrObj.User_inner_sn);
                        }
                    });
                    break;
            }
        },
        // 改变用户列表的分页
        changetPaginationFn({ currentPage, pageSize }) {
            this.pagination.current = currentPage;
            this.pagination.pageSize = pageSize;
            this.pagination.loading = true;
            this.userGetUserListAPIFn();
        },
        editModalFn(btnName) {
            // 表单操作
            if (btnName === 'cancel') {
                this.editModal.alert = false;
                this.editModal.loading = false;
                this.resetUserModalFn();
            } else if (btnName === 'save') {
                if (this.checkedUserListTrFormFlagFn()) return;
                this.btnName === 'editPhone' && this.userChangeUsernameAPIFn();
            }
        },
        customRowFn(record, index) {
            return {
                on: {
                    click: () => {
                        this.selectedRow = record;
                        this.selectedRowIndex = index;
                        this.orgTreeLoading = true;
                        this.getRoleTree();
                    }
                }
            };
        },
        customRowUserListFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentTrObj = record;
                    }
                }
            };
        },
        roleName(rid) {
            let role = this.roles.find((role) => {
                if (role.id === rid) {
                    return role.name;
                }
            });
            if (role) {
                return role.name;
            }
            return '';
        },
        findRole(rid) {
            return this.roles.find((role) => {
                if (role.id === rid) {
                    return role;
                }
            });
        },
        // 打开角色弹窗
        popRoles() {
            if (!this.trees || this.trees.length <= 0) {
                this.getGroupAllTree().then(() => {
                    this.rolesPopFlag = true;
                    this.$Utils.globalDragCenterFn('roles-modal');
                    if (this.currentTrObj && this.currentTrObj.RoleID && this.currentTrObj.RoleID > 0) {
                        this.selectedRole = this.findRole(this.currentTrObj.RoleID);
                        setTimeout(() => {
                            this.$refs.rolesTable.setSelectedRole(this.selectedRole);
                        }, 400);
                    }
                });
            } else {
                this.rolesPopFlag = true;
                this.$Utils.globalDragCenterFn('roles-modal');
                if (this.currentTrObj && this.currentTrObj.RoleID && this.currentTrObj.RoleID > 0) {
                    this.selectedRole = this.findRole(this.currentTrObj.RoleID);
                    setTimeout(() => {
                        this.$refs.rolesTable.setSelectedRole(this.selectedRole);
                    }, 400);
                }
            }
        },
        // 设置已有的角色
        // 废弃（仍然没办法很好的检测table是否初始化完毕），只有第一次比较准确
        setSelectedRole() {
            if (this.$refs.rolesTrees && this.$refs.rolesTrees.isReady()) {
                this.$refs.rolesTable.setSelectedRole(this.selectedRole);
            } else {
                setTimeout(() => {
                    this.setSelectedRole();
                }, 10);
            }
        },
        // 关闭角色设定弹窗
        closeRoles() {
            this.$refs.rolesTable.clearSelected();
            this.selectedRole = null;
            this.rolesPopFlag = false;
        },
        // 保存为用户选定的角色
        saveRole() {
            if (
                this.currentTrObj.User_inner_sn &&
                this.currentTrObj.User_inner_sn > 0 &&
                this.selectedRole &&
                this.selectedRole.id &&
                this.selectedRole.id > 0
            ) {
                // 保存
                this.setUserRole(this.currentTrObj.User_inner_sn, this.selectedRole.id).then((ok) => {
                    if (ok) {
                        // 关闭，更新数据
                        this.currentTrObj.RoleID = this.selectedRole.id;
                        this.currentTrObj.Role = this.roleName(this.currentTrObj.RoleID);
                        this.closeRoles();
                    }
                });
            }
        },
        // 弹窗中选择角色
        selectedRoleChange(selected) {
            this.selectedRole = selected;
            // this.selectedRoleChange = true
            this.selectRoleFlag = true;
            // 先清空选择
            this.$refs.rolesTrees.clearAllChecked();

            this.getRolePermissions(selected.id).then((ok) => {
                if (ok) {
                    this.roleTreeEditAbleFlag = true;
                }
            });
        },
        // 获取角色列表
        async getGroupRoles() {
            this.roles = [];
            await getGroupRolesAPI()
                .then((res) => {
                    if (res.code === 0) {
                        res.data.forEach((role) => {
                            let r = {
                                id: role.id,
                                name: role.name,
                                editAble: !role.internal
                            };
                            this.roles.push(r);
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        async getRolePermissions(roleID) {
            let success = false;
            await getRolePermissionsAPI(roleID)
                .then((res) => {
                    if (res.code === 0) {
                        let permissions = [];
                        if (res.data) {
                            res.data.forEach((p) => {
                                let permission = new Set(p.nodes);
                                let treePermission = {
                                    tree: p.tree,
                                    terminal: p.terminal,
                                    permission: permission
                                };
                                permissions.push(treePermission);
                            });
                        }
                        this.$refs.rolesTrees.updateCheckedNodes(permissions);
                        success = true;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误，请稍后再试');
                });
            return success;
        },

        // 获取角色对应的子树
        // async getRoleTreesIndex (roleID) {
        //     let success = false
        //     await getRoleTreesIndexAPI(roleID).then(res => {
        //         if (res.Code || res.code === 0) {
        //             let subtrees = []
        //             if (res.data) {
        //                 res.data.forEach(tree => {
        //                     let subtree = {
        //                         mother: tree.Mother,
        //                         terminal: tree.Terminal,
        //                         tree: tree.Tree
        //                     }
        //                     subtrees.push(subtree)
        //                 })
        //             }
        //             this.$refs.rolesTrees.updateCheckedNodes(subtrees)
        //             success = true
        //         } else {
        //             this.$message.error(res.Msg)
        //         }
        //     }).catch(e => {
        //         this.$message.error('意外错误，请稍后再试')
        //     })
        //     return success
        // },
        // 设定角色
        async setUserRole(userID, roleID) {
            let success = false;
            let ur = {
                UserID: userID,
                RoleID: roleID
            };
            await setUserRoleAPI(ur)
                .then((res) => {
                    if (res.code === 0) {
                        success = true;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误，请稍后再试');
                });
            return success;
        },
        // 组织所有母树
        async getGroupAllTree() {
            this.trees = [];
            await getGroupAllTreeAPI()
                .then((res) => {
                    if (res.code === 0) {
                        // 装载
                        res.data.forEach((t) => {
                            // 只有有内容的树才显示
                            if (t.rootNode.children && t.rootNode.children.length > 0) {
                                let tree = {
                                    id: t.id,
                                    terminal: t.terminal,
                                    terminalName: t.terminalName,
                                    nodes: t.rootNode.children,
                                    checkedNodes: [],
                                    checkedTree: []
                                };

                                this.trees.push(tree);
                            }
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 155;
            return clientHeight - topHeight;
        }
    },
    created() {
        this.getGroupRoles();
        // this.roleGetRoleListAPIFn()
        this.userGetUserListAPIFn();
        // this.getRoleTree()
    }
};
</script>

<style lang="less" scoped>
/* 顶部的停用按钮 - warning */
.ant-btn-warning {
    color: #faad14;
    background-color: #f5f5f5;
    border-color: #d9d9d9;

    &:hover {
        color: #fff;
        background-color: #faad14;
        border-color: #faad14;
    }
}

// 表格外层
.tab-table-wrap {
    background: #fff;

    ul {
        display: flex;
        flex-direction: row;
        height: 100%;
        padding-left: 0;
        margin-bottom: 0;
        border: 1px solid transparent;
    }
    .leftBox,
    .rightBox {
        display: flex;
        background: #fff;
    }
    .leftBox {
        width: 30%;
        margin-right: 4px;
    }
    .rightBox {
        flex: 1;
    }
}

.scoped .btn-top-wrap .input-search-wrap .search {
    margin-right: 0;
}
</style>
