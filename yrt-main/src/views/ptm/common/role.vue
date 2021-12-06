<template>
    <div class="scoped scoped-white table-select-center">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="24">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li v-if="pageType === 0">
                            <a-button @click="addRole" type="primary" size="small"> 新增角色 </a-button>
                        </li>
                        <li v-if="pageType === 0">
                            <a-button @click="editRole" type="primary" size="small" :disabled="!hasSelected">
                                修改
                            </a-button>
                            <a-button @click="removeRole" type="danger" size="small" :disabled="!hasSelected">
                                删除
                            </a-button>
                        </li>
                        <li>
                            <a-button
                                @click="submitRoleTree"
                                type="primary"
                                size="small"
                                :disabled="!hasUpdate && !hasSelected"
                                :loading="saving"
                            >
                                保存
                            </a-button>
                            <!-- <a-button
                                type="danger"
                                size="small"
                            >
                                重置
                            </a-button> -->
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->
        <div class="tab-table-wrap tab-table-overflowy">
            <ul>
                <!-- <div class="leftBox">
                    <a-table
                        style=""
                        :columns="roleColumns"
                        :dataSource="roles"
                        :pagination="false"
                        :loading="orgMenuTreeLoading"
                        :customRow="customRowFn"
                        :rowClassName="rowClassFn"
                        :scroll="{ y: scrollYNumber }"                      
                    >
                    </a-table>
                </div> -->

                <!-- <a-table
                        :columns="menuColumns"
                        :dataSource="orgMenuTree"
                        :pagination="false"
                        :loading="orgMenuTreeLoading" 
                        :rowSelection = "rowSelection"
                        :scroll="{ y: scrollYNumber }"
                    >
                    </a-table> -->
                <!-- <div class="rightBox">
                    <a-tree 
                        checkable
                        @expand="onExpand"
                        :expandedKeys="expandedKeys"
                        :autoExpandParent="true"
                        v-model="checkedKeys"
                        :treeData="orgMenuTree"
                    />
                </div> -->
            </ul>
        </div>
        <a-row class="tab-table-wrap">
            <!-- 角色列表区开始 -->
            <a-col :span="6" style="height: 100%">
                <div class="leftBox tab-table-wrap tab-table-overflowy" style="top: -4px">
                    <a-table
                        style=""
                        :columns="roleColumns"
                        :dataSource="roles"
                        :pagination="false"
                        :loading="orgMenuTreeLoading"
                        :customRow="customRowFn"
                        :rowClassName="rowClassFn"
                        :scroll="{ y: scrollYNumber }"
                    >
                    </a-table>
                </div>
                <!-- margin-top: 4px; margin-right: 6px; -->
            </a-col>
            <!-- 角色列表区结束 -->
            <!-- 权限表开始 -->
            <a-col :span="18" style="height: 100%">
                <div class="rightBox">
                    <a-tree
                        checkable
                        @check="onCheck"
                        @expand="onExpand"
                        :expandedKeys="expandedKeys"
                        :autoExpandParent="true"
                        v-model="checkedKeys"
                        :treeData="orgMenuTree"
                    />
                </div>
                <!-- style="margin-top: 4px;" -->
            </a-col>
            <!-- 权限表结束 -->
        </a-row>
        <!-- 角色弹框开始 -->
        <a-modal
            title=""
            centered
            v-model="popRoleFlag"
            :width="350"
            :maskClosable="false"
            class="modal-form-input-scoped role-modal global-drag"
        >
            <div v-globalDrag="{ el: 'role-modal' }" class="modal-header-wrap">
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
                        <span class="label"><em class="em red">*</em>角色名称</span>
                        <a-input v-model="hotRole.name" placeholder="" class="input" />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 角色弹框结束 -->
    </div>
</template>

<script>
import {
    roleGetRoleListAPI,
    roleAddRoleAPI,
    roleUpdateRoleAPI,
    roleDeleteRoleAPI,
    menuOrgMenuAPI,
    menuSpecifyOrgMenuAPI,
    roleUpdateRoleTree,
    roleUpdateSystemRoleTree,
    roleGetRoleTreeIDs,
    roleGetSystemRoleTreeIDs,
    menuGetSystemRoleListAPI
} from '@/service/pageAjax';

export default {
    name: 'role',
    props: {
        // pageType: { // pageStatus.type = 0 // 页面类型判断依据： 0-普通角色管理   1-系统固定角色管理
        //     type: Number,
        //     defaults: 0
        // }
        pageType: Number
    },
    data() {
        return {
            saving: false,
            roleListLoading: true,
            treeLoading: true,
            roleColumns: [
                {
                    title: '角色',
                    dataIndex: 'name',
                    key: 'name',
                    aligin: 'left'
                }
            ],
            roles: [],
            // 表格的配置
            orgMenuTreeLoading: false,
            popRoleFlag: false,
            popMenuFlag: false,
            isEdit: false,
            menuColumns: [
                {
                    title: '名称',
                    dataIndex: 'Name',
                    key: 'Name',
                    width: '100%',
                    align: 'left'
                }
            ],
            selectedRow: {},
            selectedRowIndex: -1,
            orgMenuTree: [],
            hotRole: {},
            newRole: {},
            checkedKeys: [],
            expandedKeys: [],
            checkedTree: []
        };
    },
    methods: {
        // 读取角色列表
        async getRoles() {
            this.roleListLoading = true;
            if (this.pageType === 0) {
                await roleGetRoleListAPI()
                    .then((res) => {
                        if (parseFloat(res.Code || res.code) === 0) {
                            res.data.forEach((r) => {
                                let role = {
                                    key: r.Role_inner_sn,
                                    name: r.Name,
                                    description: r.Description
                                };
                                this.roles.push(role);
                            });
                        } else {
                            this.$message.error(res.Msg);
                        }
                        this.roleListLoading = false;
                    })
                    .catch(() => {
                        this.roleListLoading = false;
                    });
            } else {
                await menuGetSystemRoleListAPI()
                    .then((res) => {
                        if (parseFloat(res.Code || res.code) === 0) {
                            res.data.forEach((r) => {
                                let role = {
                                    key: r.Role_inner_sn,
                                    name: r.Name,
                                    description: r.Description,
                                    systemType: r.System_type
                                };
                                this.roles.push(role);
                            });
                        } else {
                            this.$message.error(res.Msg);
                        }
                        this.roleListLoading = false;
                    })
                    .catch(() => {
                        this.roleListLoading = false;
                    });
            }
        },
        async updateOrgMenuTree() {
            this.orgMenuTreeLoading = true;
            // 普通角色管理情况下，不用每次都读取菜单树
            if (this.pageType === 0 && this.orgMenuTree.length === 0) {
                await menuOrgMenuAPI()
                    .then((res) => {
                        if (res.Code || res.code === 0) {
                            this.orgMenuTree = this.mockTree(res.data);
                        } else {
                            this.$message.error(res.Msg);
                        }
                        this.orgMenuTreeLoading = false;
                    })
                    .catch(() => {
                        this.orgMenuTreeLoading = false;
                    });
                // 设置系统固定角色的情况下，每次都读取菜单树
            } else if (this.pageType === 1) {
                let systemType = 1;
                if (this.selectedRow.systemType) {
                    systemType = this.selectedRow.systemType;
                }
                await menuSpecifyOrgMenuAPI(systemType)
                    .then((res) => {
                        if (res.Code || res.code === 0) {
                            this.orgMenuTree = this.mockTree(res.data);
                        } else {
                            this.$message.error(res.Msg);
                        }
                        this.orgMenuTreeLoading = false;
                    })
                    .catch(() => {
                        this.orgMenuTreeLoading = false;
                    });
            }

            this.orgMenuTreeLoading = false;
        },
        async submitDeleteRole() {
            this.roleListLoading = true;
            await roleDeleteRoleAPI(this.selectedRow.key)
                .then((res) => {
                    if (res.Code || res.code === 0) {
                        this.roles.splice(this.selectedRowIndex, 1);
                        this.selectedRowIndex = -1;
                        this.selectedRow = {};
                        this.checkedKeys = [];
                        this.checkedTree = [];
                    } else {
                        this.$message.error(res.Msg);
                    }
                    this.roleListLoading = false;
                })
                .catch(() => {
                    this.roleListLoading = false;
                });

            this.roleListLoading = false;
        },
        async submitAddRole() {
            let param = {
                name: this.hotRole.name,
                description: ''
            };
            this.roleListLoading = true;
            await roleAddRoleAPI(param)
                .then((res) => {
                    if (parseFloat(res.Code || res.code) === 0) {
                        let role = {
                            key: res.data.Role_inner_sn,
                            name: res.data.Name,
                            description: res.data.Description
                        };
                        this.roles.push(role);
                        this.selectedRow = role;
                        this.selectedRowIndex = this.roles.length - 1;
                        this.checkedKeys = [];
                        this.roleListLoading = false;
                        return true;
                    } else {
                        this.$message.error(res.Msg);
                        return false;
                    }
                })
                .catch(() => {
                    this.roleListLoading = false;
                    return false;
                });
        },
        async submitEditRole() {
            let param = {
                Role_inner_sn: this.hotRole.key,
                name: this.hotRole.name,
                description: ''
            };
            this.roleListLoading = true;
            await roleUpdateRoleAPI(param)
                .then((res) => {
                    this.roleListLoading = false;
                    if (parseFloat(res.Code || res.code) === 0) {
                        let role = {
                            key: this.hotRole.key,
                            name: this.hotRole.name,
                            description: ''
                        };
                        this.roles.splice(this.selectedRowIndex, 1, role);
                        this.selectedRow = role;
                        return true;
                    } else {
                        this.$message.error(res.Msg);
                        return false;
                    }
                })
                .catch(() => {
                    this.roleListLoading = false;
                    return false;
                });
        },
        async submitRoleTree() {
            this.saving = true;
            if (!this.selectedRow || this.selectedRowIndex < 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择要保存的角色',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                this.saving = false;
                return true;
            }

            let param = {
                RID: this.selectedRow.key,
                Tree: this.checkedTree
            };
            if (this.pageType === 0) {
                await roleUpdateRoleTree(param)
                    .then((res) => {
                        // eslint-disable-next-line no-empty
                        if (res.Code || res.code === 0) {
                        } else {
                            this.$message.error(res.Msg);
                        }
                    })
                    .catch(() => {});
            } else {
                await roleUpdateSystemRoleTree(param)
                    .then((res) => {
                        // eslint-disable-next-line no-empty
                        if (res.Code || res.code === 0) {
                        } else {
                            this.$message.error(res.Msg);
                        }
                    })
                    .catch(() => {});
            }

            this.saving = false;
        },
        async getRoleTreeIDs() {
            this.roleListLoading = true;
            this.checkedKeys = [];
            this.checkedTree = [];
            if (this.pageType === 0) {
                await roleGetRoleTreeIDs(this.selectedRow.key)
                    .then((res) => {
                        if (res.Code || res.code === 0) {
                            this.fillChecked(res.data);
                        } else {
                            this.$message.error(res.Msg);
                        }
                    })
                    .catch(() => {});
            } else {
                await roleGetSystemRoleTreeIDs(this.selectedRow.key)
                    .then((res) => {
                        if (res.Code || res.code === 0) {
                            this.fillChecked(res.data);
                        } else {
                            this.$message.error(res.Msg);
                        }
                    })
                    .catch(() => {});
            }
            this.roleListLoading = false;
        },
        mockTree(root) {
            // 准备copy实体
            let nodes = [];
            // 循环加入key值和检查children
            root.forEach((n) => {
                let t = '';
                if (n.Type === 'cat') {
                    t = '分类';
                } else {
                    t = '菜单';
                }
                // 加入key值
                let node = {
                    key: n.ID,
                    Type: t,
                    title: n.Name,
                    Icon: n.Icon,
                    URI: n.URI,
                    Index: n.Index
                };
                // 检查children
                if (n.children && n.children.length > 0) {
                    // 递归
                    node.children = this.mockTree(n.children);
                }
                nodes.push(node);
            });
            // 返回
            return nodes;
        },
        // 填充角色的权限值
        fillChecked(root) {
            root.forEach((n) => {
                // 检查children
                if (n.ChildrenIDs && n.ChildrenIDs.length > 0) {
                    // 递归
                    this.fillChecked(n.ChildrenIDs);
                } else {
                    this.checkedKeys.push(n.ID);
                }
            });
        },

        // 增加新角色
        addRole() {
            this.hotRole = {
                name: '',
                id: ''
            };
            this.isEdit = false;
            this.popRoleFlag = true;
            this.$Utils.globalDragCenterFn('role-modal');
        },
        // 修改角色
        editRole() {
            // 修改，判断是否有选中的
            if (!this.selectedRow || this.selectedRowIndex < 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择要修改的角色',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return true;
            }
            this.hotRole = {
                key: this.selectedRow.key,
                name: this.selectedRow.name,
                description: this.selectedRow.description
            };

            this.isEdit = true;
            this.popRoleFlag = true;
            this.$Utils.globalDragCenterFn('role-modal');
        },
        // 保存角色
        saveRole() {
            // 检查角色是否重复
            let found = this.roles.some((r) => {
                if (r.name === this.hotRole.name) {
                    this.$warning({
                        title: '操作提示',
                        content: '已有同名角色',
                        okText: '知道了',
                        maskClosable: false,
                        centered: true
                    });
                    return true;
                }
            });
            if (found) {
                return false;
            }
            // 提交后端
            let suc = false;
            if (this.isEdit) {
                suc = this.submitEditRole();
            } else {
                suc = this.submitAddRole();
            }
            // 关闭弹窗
            this.popRoleFlag = !suc;
        },
        // 删除角色
        removeRole() {
            // 修改，判断是否有选中的
            if (!this.selectedRow || this.selectedRowIndex < 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择要删除的角色',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return true;
            }
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.submitDeleteRole();
                    // _this.roles.splice(_this.selectedRowIndex, 1)
                }
            });
        },
        onCheck(keys, e) {
            let tree = JSON.parse(JSON.stringify(this.orgMenuTree));
            e.checkedNodes.forEach((cn) => {
                let node = cn.data;
                this.markChecked(tree, node.props.Index, node.props.dataRef.key);
            });
            this.checkedTree = this.genCheckedTree(tree);
        },
        markChecked(tree, indexStr, key) {
            let strs = indexStr.split('.');
            let current = tree[parseInt(strs[1])];
            for (let i = 2; i < strs.length; i++) {
                let index = parseInt(strs[i]);
                current = current.children[index];
            }

            if (current.key === key) {
                current.checked = true;
            }
        },
        genCheckedTree(children) {
            let checkedNode = [];

            children.forEach((child) => {
                if (child.children) {
                    let checkedChildren = this.genCheckedTree(child.children);
                    if (checkedChildren.length > 0) {
                        checkedNode.push({
                            Index: child.Index,
                            Name: child.title,
                            ID: child.key,
                            ChildrenIDs: checkedChildren
                        });
                    }
                } else if (child.checked) {
                    checkedNode.push({
                        Index: child.Index,
                        Name: child.title,
                        ID: child.key
                    });
                }
            });
            return checkedNode;
        },
        onExpand(expandedKeys) {
            this.expandedKeys = expandedKeys;
        },
        customRowFn(record, index) {
            return {
                on: {
                    click: () => {
                        if (this.selectedRowIndex === index) {
                            return;
                        }
                        this.checkedKeys = [];
                        this.selectedRow = record;
                        this.selectedRowIndex = index;
                        this.updateOrgMenuTree().then(() => {
                            // 必须先读取对应的组织菜单，才能在设定选定的节点
                            this.getRoleTreeIDs();
                        });
                    }
                }
            };
        },
        // 表格的高亮样式显示
        rowClassFn(record) {
            if (record.name === this.selectedRow.name) {
                return 'active-tr';
            }
            return '';
        }
    },
    mounted() {
        this.updateOrgMenuTree().then(() => {
            this.getRoles();
        });
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 155;
            return clientHeight - topHeight;
        },
        hasUpdate() {
            if (this.selectedRowIndex >= 0) {
                return true;
            }
            return false;
        },
        hasSelected() {
            if (this.selectedRowIndex >= 0) {
                return true;
            }
            return false;
        }
    }
};
</script>

<style lang="less" scoped>
.modal-form-input-scoped {
    .modal-form-input {
        .label {
            width: 80px;
        }
    }
}
.ant-table-wrapper {
    width: 100%;
    height: 100%;
    background: #fff;
}
// 表格外层
.tab-table-wrap {
    .leftBox,
    .rightBox {
        display: flex;
        background: #fff;
        height: 100%;
    }
    .leftBox {
        margin-right: 4px;
        ul {
            display: flex;
            flex-direction: row;
            height: 100%;
            padding-left: 0;
            margin-bottom: 0;
            border: 1px solid transparent;
        }
    }
    .rightBox {
        flex: 1;
    }
}
</style>
