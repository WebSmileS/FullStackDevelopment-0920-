<template>
    <div class="scoped scoped-white">
        <!-- 头部区域开始 -->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="24">
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="popNewRole" type="primary" size="small"> 添加角色 </a-button>
                            <a-button :disabled="!selectRoleFlag" @click="popEditRole" type="primary" size="small">
                                角色改名
                            </a-button>
                            <a-button :disabled="!selectRoleFlag" @click="delRole" type="danger" size="small">
                                删除角色
                            </a-button>
                        </li>
                        <li>
                            <a-button :disabled="!hasChanged" @click="updateRolePermission" type="primary" size="small">
                                保存修改
                            </a-button>
                            <a-button :disabled="!hasChanged" @click="cancelChange" type="primary" size="small">
                                撤销修改
                            </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!-- 头部区域结束 -->
        <a-row class="tab-table-wrap">
            <!-- 角色列表开始 -->
            <a-col :span="6" style="height: 100%">
                <roleList
                    ref="rolesList"
                    :roles="groupRoles"
                    :editAble="false"
                    :loading="rolesLoadingFlag"
                    :rowChangeAble="!hasChanged"
                    :rowChangeWarn="roleChangeWarnMsg"
                    @deleteRole="mockEvent"
                    @addRole="mockEvent"
                    @updateRole="mockEvent"
                    @selectChange="selectedRoleChange"
                ></roleList>
            </a-col>
            <!-- 角色列表结束 -->

            <!-- 角色树开始 -->
            <a-col :span="18" style="height: 100%">
                <roleTree
                    ref="rolesTrees"
                    :tableHeight="bodyHeight"
                    :trees="trees"
                    :checkAble="roleTreeEditAbleFlag"
                    :editAble="false"
                    @changed="onRoleSubtreeChanged"
                    @update="mockEvent"
                ></roleTree>
            </a-col>
            <!-- 角色树结束 -->
        </a-row>
        <!-- 弹出框区域开始 -->
        <!-- 角色弹出框开始 -->
        <a-modal
            title=""
            centered
            v-model="popRoleFlag"
            :width="350"
            :maskClosable="false"
            class="modal-form-input-scoped roles-role-modal global-drag"
        >
            <div v-globalDrag="{ el: 'roles-role-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    角色
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="popRoleFlag = false"> 取消 </a-button>
                <a-button key="submit" type="primary" @click="saveRole"> 保存 </a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label yrt-short-label"><em class="em red">*</em>角色名称</span>
                        <a-input v-model="role.name" placeholder="" class="input" />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 角色弹出框结束 -->
        <!-- 弹出框区域结束 -->
    </div>
</template>
<script>
import {
    getGroupAllTreeAPI,
    addGroupRoleAPI,
    getGroupRolesAPI,
    getRolePermissionsAPI,
    updateGroupRoleAPI,
    delGroupRoleAPI,
    // onSaveRolePermissionAPI,
    setRolePermissionAPI
} from '@/service/pageAjax';
import roleList from '../common/roleList';
import roleTree from '../common/roleTree';
export default {
    name: 'userRoles',
    components: {
        roleList,
        roleTree
    },
    data() {
        return {
            selectRoleFlag: false, // 选中角色标记
            hasChanged: false, // 角色树有改变标记
            rolesLoadingFlag: false, // 角色列表加载标记
            roleTreeEditAbleFlag: false, // 角色树是否允许编辑标记
            popRoleFlag: false, // 角色弹窗标记
            isEditRole: false, // 弹窗是否修改角色
            trees: [], // 所有端-树
            groupRoles: [], // 角色列表
            role: {}, // 当前操作的role
            selectedRole: {}, // 选中角色
            roleChangeWarnMsg: '当前角色权限已经修改，请先保存修改或撤销修改' // 不允许角色列表切换时显示的警告
        };
    },
    methods: {
        popNewRole() {
            this.role = {
                name: ''
            };
            this.isEditRole = false;
            this.popRole();
        },
        popEditRole() {
            this.isEditRole = true;
            this.role = {
                id: this.selectedRole.id,
                name: this.selectedRole.name
            };
            this.popRole();
        },
        popRole() {
            this.popRoleFlag = true;
            this.$Utils.globalDragCenterFn('roles-role-modal');
        },
        saveRole() {
            if (this.isEditRole) {
                this.updateGroupRole().then(() => {
                    this.popRoleFlag = false;
                });
            } else {
                this.addGroupRole().then((ok) => {
                    if (ok) {
                        this.popRoleFlag = false;
                    }
                });
            }
        },
        delRole() {
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认删除该角色？',
                centered: true,
                maskClosable: false,
                onOk() {
                    // 提交删除
                    _this.delGroupRole(_this.selectedRole.id).then((ok) => {
                        if (ok) {
                            _this.$refs.rolesList.directDel(_this.selectedRole.id);
                            _this.selectedRole = {};
                            _this.selectRoleFlag = false;
                            _this.$refs.rolesTrees.clearAllChecked();
                        }
                    });
                },
                onCancel() {}
            });
        },
        selectedRoleChange(selected) {
            this.selectedRole = selected;
            // this.selectedRoleChange = true
            this.selectRoleFlag = true;
            // 先清空选择
            this.$refs.rolesTrees.clearAllChecked();
            // 新建的角色，不读取权限树
            if (!selected || selected.id.length === 0) {
                this.roleTreeEditAbleFlag = false;
                return;
            }
            this.getRolePermissions(selected.id).then((ok) => {
                if (ok) {
                    this.roleTreeEditAbleFlag = true;
                }
            });
        },
        onRoleSubtreeChanged(changed) {
            this.hasChanged = changed; // 角色树有修改，在保存或取消之前不能切换角色
        },
        cancelRole() {
            this.popRole = false;
        },
        updateRolePermission() {
            let permission = this.$refs.rolesTrees.getCurrentUpdate();
            this.onSaveRolePermission(permission).then((ok) => {
                if (ok) {
                    let permissions = [];
                    permissions.push(permission);
                    this.$refs.rolesTrees.updateCheckedNodes(permissions);
                }
            });
        },
        cancelChange() {
            this.$refs.rolesTrees.cancelCurrentChange();
        },
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
        },
        async addGroupRole() {
            let success = false;
            let role = {
                name: this.role.name
            };
            await addGroupRoleAPI(role)
                .then((res) => {
                    if (res.code === 0) {
                        let r = {
                            id: res.data.id,
                            name: role.name,
                            editAble: true
                        };
                        this.groupRoles.push(r);
                        success = true;
                    } else {
                        this.$message.error(res.msg);
                        success = false;
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
            return success;
        },
        async updateGroupRole() {
            let success = false;
            let role = {
                id: this.role.id,
                name: this.role.name
            };
            await updateGroupRoleAPI(role)
                .then((res) => {
                    if (res.code === 0) {
                        // 更新列表
                        this.$refs.rolesList.directUpdate(role.id, role.name);
                        success = true;
                    } else {
                        this.$message.error(res.msg);
                        success = false;
                    }
                })
                .catch((e) => {
                    console.log(e);
                    this.$message.error('意外错误');
                });
            return success;
        },
        async getGroupRoles() {
            await getGroupRolesAPI()
                .then((res) => {
                    if (res.code === 0) {
                        res.data.forEach((role) => {
                            let r = {
                                id: role.id,
                                name: role.name,
                                editAble: !role.internal
                            };
                            this.groupRoles.push(r);
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
        async delGroupRole(roleID) {
            let success = false;
            await delGroupRoleAPI(roleID)
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
        async onSaveRolePermission(rolePermission) {
            if (!rolePermission || rolePermission === null) {
                return;
            }
            rolePermission.role = this.selectedRole.id;

            await setRolePermissionAPI(rolePermission)
                .then((res) => {
                    if (res.code === 0) {
                        let permissions = [];
                        permissions.push({
                            tree: rolePermission.tree,
                            permission: new Set(rolePermission.nodes)
                        });
                        this.$refs.rolesTrees.updateCheckedNodes(permissions);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误，请稍后再试');
                });
        }
        // async onSaveRolePermission (roleID, subtree) {
        //     let success = false
        //     let post = {
        //         RoleID: roleID,
        //         Tree: subtree
        //     }
        //     await onSaveRolePermissionAPI(post).then(res => {
        //         if (res.Code || res.code === 0) {
        //             success = true
        //         } else {
        //             this.$message.error(res.Msg)
        //         }
        //     }).catch(e => {
        //         this.$message.error('意外错误，请稍后再试')
        //     })
        //     return success
        // },
        // mockClick (something) {
        // },
        // mockEvent () {
        // }
    },
    mounted() {
        this.getGroupAllTree();
        this.getGroupRoles();
    },
    computed: {
        bodyHeight() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 154,
                pager = 0;
            return clientHeight - topHeight - pager;
        }
    }
};
</script>
<style lang="less" scoped>
.modal-form-input-scoped .modal-form-input .item-children .yrt-short-label {
    width: 80px;
}
.modal-form-input-scoped .modal-form-input .yrt-short-label {
    width: 80px;
}
</style>
