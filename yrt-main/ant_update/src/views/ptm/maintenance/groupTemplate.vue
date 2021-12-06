<template>
    <div class="scoped scoped-white">
        <!-- 头部区域开始 -->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="24">
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="popNewTemplate" type="primary" size="small"> 添加模版 </a-button>
                            <a-button
                                :disabled="!selectTemplateFlag"
                                @click="popEditTemplate"
                                type="primary"
                                size="small"
                            >
                                修改
                            </a-button>
                        </li>
                        <li>
                            <a-button :disabled="!selectTemplateFlag" @click="delTemplate" type="danger" size="small">
                                删除
                            </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!-- 头部区域结束 -->
        <!-- 主体区域开始 -->
        <a-row class="tab-table-wrap">
            <vxe-table
                max-height="100%"
                resizable
                show-overflow
                highlight-hover-row
                highlight-current-row
                ref="templateTable"
                type="checkbox"
                @current-change="templateSelectedChange"
                :data="groupTemplates"
            >
                <!-- <vxe-table-column
                    type="seq"
                    width="60"
                ></vxe-table-column> -->
                <vxe-table-column field="id" title="ID"></vxe-table-column>
                <vxe-table-column field="name" title="名称"></vxe-table-column>
                <vxe-table-column field="note" title="备注"></vxe-table-column>
                <vxe-table-column title="树" width="100">
                    <template v-slot="{ row }">
                        <a-button @click="popTerminal(row)" type="link" icon="cluster"></a-button>
                    </template>
                </vxe-table-column>
                <!-- <vxe-table-column
                    field="GroupCount"
                    title="组织"
                    width="100"
                ></vxe-table-column> -->
                <vxe-table-column title="角色模版" width="100">
                    <template v-slot="{ row }">
                        <a-button @click="popRoles(row)" type="link" icon="team"></a-button>
                    </template>
                </vxe-table-column>
            </vxe-table>
        </a-row>
        <!-- 组织table开始 -->
        <!-- 组织table结束 -->
        <!-- 主体区域结束 -->
        <!-- 弹框区域开始 -->
        <!-- 模版信息弹窗开始 -->
        <a-modal
            title
            centered
            v-model="templateInfoPopFlag"
            :width="300"
            :maskClosable="false"
            class="modal-form-input-scoped template-info-modal global-drag"
        >
            <div v-globalDrag="{ el: 'template-info-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    树属性-{{ treePopTitleSuffix }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="templateInfoPopFlag = false">取消</a-button>
                <a-button key="submit" type="primary" @click="saveTemplate">保存</a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item" v-show="isEditTemplateInfoFlag">
                        <span class="label yrt-short-label"> ID </span>
                        <a-input
                            :disabled="isEditTemplateInfoFlag"
                            v-model="templateInfo.id"
                            placeholder
                            class="input"
                        />
                    </li>
                    <li class="item">
                        <span class="label yrt-short-label"> <em class="em red">*</em>名称 </span>
                        <a-input v-model="templateInfo.name" placeholder class="input" />
                    </li>
                    <li class="item">
                        <span class="label yrt-short-label"> 备注 </span>
                        <a-input v-model="templateInfo.note" placeholder class="input" />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 模版信息弹窗结束 -->
        <!-- 端-树弹框开始 -->
        <a-modal
            title
            centered
            v-model="terminalPopFlag"
            :width="800"
            :maskClosable="false"
            class="modal-form-input-scoped terminal-info-modal global-drag"
        >
            <div v-globalDrag="{ el: 'terminal-info-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    端-{{ treePopTitleSuffix }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="terminalPopFlag = false">关闭</a-button>
            </template>
            <div class="modal-form-input">
                <!-- 端Table开始 -->
                <vxe-table
                    max-height="100%"
                    resizable
                    show-overflow
                    highlight-hover-row
                    highlight-current-row
                    :edit-config="{ trigger: 'click', mode: 'row' }"
                    @edit-actived="updateTreeOptions"
                    ref="terminalTable"
                    @current-change="terminalSelectedChange"
                    :data="terminals"
                >
                    <vxe-table-column type="seq" width="60"></vxe-table-column>
                    <vxe-table-column field="key" title="端Key"></vxe-table-column>
                    <vxe-table-column field="name" title="端名称"></vxe-table-column>
                    <vxe-table-column field="treeName" title="树"></vxe-table-column>
                    <vxe-table-column
                        filed="tree"
                        title="选择树"
                        :formatter="formatTreeName"
                        :edit-render="{ name: 'select', options: treeOptions, events: { change: treeOptionChange } }"
                    >
                    </vxe-table-column>
                    <vxe-table-column field="optEdit" title="编辑" width="60">
                        <template v-slot="{ row }">
                            <a-button
                                :disabled="noneTree(row)"
                                @click="popEditSubtree(row)"
                                type="link"
                                icon="setting"
                            ></a-button>
                        </template>
                    </vxe-table-column>
                </vxe-table>
                <!-- 端Table结束 -->
            </div>
        </a-modal>
        <!-- 端-树弹框结束 -->
        <!-- 树选择弹框开始 -->
        <a-modal
            title
            centered
            v-model="subtreePopFlag"
            @cancel="cancelTreeSet"
            :width="1000"
            :maskClosable="false"
            class="modal-form-input-scoped tree-modal global-drag"
        >
            <div v-globalDrag="{ el: 'tree-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    端-{{ treePopTitleSuffix }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="cancelTreeSet">取消</a-button>
                <a-button key="submit" type="primary" @click="submitSetTemplatePermission">保存</a-button>
            </template>
            <div class="modal-form-input">
                <vxe-table
                    border="none"
                    height="600px"
                    resizable
                    show-overflow
                    ref="treeTable"
                    row-id="ID"
                    :checkbox-config="{ highlight: true }"
                    :tree-config="treeConfig"
                    :data="motherTree"
                    @checkbox-change="treeSelectChange"
                    @select-all="treeSelectChange"
                >
                    <vxe-table-column type="checkbox" tree-node></vxe-table-column>
                    <vxe-table-column field="name" title="Name"> </vxe-table-column>
                    <vxe-table-column field="flag" title="Flag">
                        <template v-slot="{ row }">
                            <i v-if="customizeIcon(row.flag)" class="yrt" :class="row.flag" />
                            <a-icon v-else :type="row.flag" />{{ row.flag }}
                        </template>
                    </vxe-table-column>
                    <vxe-table-column field="content" title="Content"></vxe-table-column>
                    <vxe-table-column field="note" title="Note"></vxe-table-column>
                </vxe-table>
            </div>
        </a-modal>
        <!-- 树选择弹框结束 -->
        <!-- 角色弹框开始 -->
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
                    端-{{ treePopTitleSuffix }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="closeRoles">关闭</a-button>
            </template>
            <div class="modal-form-input">
                <a-row class="tab-table-wrap">
                    <a-col :span="6" style="min-height: 500px">
                        <roleList
                            ref="rolesTable"
                            :roles="templateRoles"
                            :editAble="true"
                            :loading="rolesLoadingFlag"
                            :rowChangeAble="roleChangeAbleFlag"
                            :rowChangeWarn="roleChangeWarnMsg"
                            @deleteRole="delRole"
                            @addRole="addRole"
                            @updateRole="updateRole"
                            @selectChange="selectedRoleChange"
                        ></roleList>
                    </a-col>
                    <a-col :span="18" style="max-height: 500px">
                        <roleTree
                            ref="rolesTrees"
                            :tableHeight="440"
                            :trees="templateTree"
                            :checkAble="roleTreeEditAbleFlag"
                            :editAble="true"
                            @changed="onRoleSubtreeChanged"
                            @savePermission="onSaveRolePermission"
                        ></roleTree>
                    </a-col>
                </a-row>
            </div>
        </a-modal>
        <!-- 角色弹框结束 -->
        <!-- 弹框区域结束 -->
    </div>
</template>
<script>
import {
    getAllGroupTemplateAPI,
    addGroupTemplateAPI,
    updateGroupTemplateAPI,
    delGroupTemplateAPI,
    getAllTerminalTreeAPI,
    setTemplatePermissionAPI,
    delTreeFromTemplateAPI,
    getTemplateAllTreeInfoAPI,
    getTemplateAllTreeAPI,
    getTemplateAllRolesAPI,
    addTemplateRoleAPI,
    updateTemplateRoleNameAPI,
    delTemplateRoleAPI,
    setTemplateRolePermissionAPI,
    getRolePermissionsAPI,
    getTemplatePermissionAPI
} from '@/service/pageAjax';
import roleList from '../common/roleList';
import roleTree from '../common/roleTree';
export default {
    components: {
        roleList,
        roleTree
    },
    data() {
        return {
            treeConfig: {
                children: 'children',
                iconOpen: 'yrt-table-tree-icon yrt-minus',
                iconClose: 'yrt-table-tree-icon yrt-jia2',
                expandAll: true
            },
            templateInfoPopFlag: false, // 模版信息弹窗标志
            terminalPopFlag: false, // 端table弹框标志
            subtreePopFlag: false, // 树选择弹框标志
            rolesPopFlag: false, // 角色弹出框标志
            isEditTemplateInfoFlag: false, // 是否编辑模版标志
            isEditSubteeFlag: false, // 是否设定已有树
            selectTemplateFlag: false, // 是否选中模版标志
            selectTerminalFlag: false, // 是否选中端-树标志
            rolesLoadingFlag: false, // 角色模版弹出框加载标志
            templateInfo: {}, // 当前操作模版
            selectedTemplateInfo: {}, // 当前选中模版
            selectedTerminal: {}, // 当前选中的端-树
            groupTemplates: [], // 组织模板列表
            terminals: [], // 所有端
            treeOptions: [], // 端下所有树，用于table中options select
            motherTree: [], // 当前操作母树
            checkedNodes: [], // 当前选中的节点列表
            // checkedSubtree: {}, // 当前操作checked子树，用于设定/更新组织模版树
            currentTemplatePermission: {}, // 当前操作的模板权限
            oldTerminalTree: '', // 选中端-树，临时存储树值，用于取消时回退
            templateRoles: [], // 端所有角色列表
            templateTree: [], // 端下所有树(信息和实体)
            roleChangeAbleFlag: true, // 角色列表是否允许切换，用于角色树有改变并未保存
            roleChangeWarnMsg: '当前权限树已经修改，请先保存或撤销修改', // 不允许角色列表切换时显示的警告
            selectedRole: {}, // 当前操作的角色
            roleTreeEditAbleFlag: false // 权限树是否能够修改，默认没选角色之前不能修改
        };
    },

    methods: {
        // genCheckedTree (children) {
        //     let checkedNode = []
        //     children.forEach(child => {
        //         if (child.children && child.children.length > 0) {
        //             let checkedChildren = this.genCheckedTree(child.children)
        //             if (checkedChildren.length > 0) {
        //                 checkedNode.push({
        //                     ID: child.ID,
        //                     Children: checkedChildren
        //                 })
        //             }
        //         } else if (this.$refs.treeTable.isCheckedByRow(child)) {
        //             checkedNode.push({
        //                 ID: child.ID
        //             })
        //         }
        //     })
        //     return checkedNode
        // },
        popNewTemplate() {
            this.templateInfo = {
                Key: '',
                Name: '',
                Desc: '',
                TreeCount: 0,
                GroupCount: 0
            };
            this.isEditTemplateInfoFlag = false;
            this.popTemplate();
        },
        popEditTemplate() {
            this.templateInfo = {
                Key: this.selectedTemplateInfo.Key,
                Name: this.selectedTemplateInfo.Name,
                Desc: this.selectedTemplateInfo.Desc
            };
            this.isEditTemplateInfoFlag = true;
            this.popTemplate();
        },
        popTemplate() {
            this.templateInfoPopFlag = true;
            this.$Utils.globalDragCenterFn('template-info-modal');
        },
        saveTemplate() {
            if (this.isEditTemplateInfoFlag) {
                this.updateTemplate();
            } else {
                this.addTemplate();
            }
        },
        templateSelectedChange(selected) {
            this.selectTemplateFlag = true;
            this.selectedTemplateInfo = selected.row;
        },
        terminalSelectedChange(selected) {
            this.oldTerminalTree = selected.row.tree;
            this.selectTerminalFlag = true;
            this.selectedTerminal = selected.row;
        },
        popTerminal(row) {
            this.getTemplateAllTreeInfo(row.id);
            this.terminalPopFlag = true;
            this.$Utils.globalDragCenterFn('terminal-info-modal');
        },
        treeOptionChange({ row }, evnt) {
            let treeID = parseInt(evnt.target.value);
            const terminal = row.key;
            let trees = row.trees;
            if (treeID === -1) {
                // 弹框确认是否删除
                const _this = this;
                _this.$confirm({
                    title: '删除',
                    content: '确认从模版中删除该树？',
                    centered: true,
                    maskClosable: false,
                    onOk() {
                        // 设定删除
                        _this
                            .submitDelTree(_this.selectedTemplateInfo.Key, _this.selectedTerminal.key)
                            .then((isDeleted) => {
                                // 删除不成功不能删除
                                if (!isDeleted) {
                                    _this.revertTerminalTree();
                                }
                            });
                    },
                    onCancel() {
                        _this.revertTerminalTree();
                    }
                });
            }
            this.popNewSubtree(terminal, trees, treeID);
        },
        cancelTreeSet() {
            if (!this.isEditSubteeFlag) {
                this.revertTerminalTree();
            }
            this.closeSubtree();
        },
        revertTerminalTree() {
            this.selectedTerminal.tree = this.oldTerminalTree;
        },
        popRoles(row) {
            this.getTemplateRoles(row.id);
            this.getTemplateAllTree(row.id).then(() => {
                this.rolesPopFlag = true;
                this.$Utils.globalDragCenterFn('roles-modal');
            });
        },
        closeRoles() {
            this.roleTreeEditAbleFlag = false;
            this.$refs.rolesTable.init();
            this.$refs.rolesTrees.init();
            this.rolesPopFlag = false;
        },
        updateTreeOptions({ row }) {
            this.treeOptions = row.treeOptions;
        },
        popNewSubtree(terminal, trees, treeID) {
            this.isEditSubteeFlag = false;
            this.currentTemplatePermission = {
                template: this.selectedTemplateInfo.id,
                terminal: terminal,
                tree: treeID,
                nodes: []
            };

            this.motherTree = [];
            trees.forEach((tree) => {
                if (tree.id === treeID) {
                    this.motherTree.push(...tree.rootNode.children);
                }
            });
            this.popSubtree();
        },
        popEditSubtree(row) {
            this.isEditSubteeFlag = true;
            if (!row.tree) {
                return;
            }
            this.popSubtree();
            let terminal = row.key;
            // let tree = row.tree
            this.currentTemplatePermission = {
                template: this.selectedTemplateInfo.id,
                terminal: terminal,
                tree: row.tree,
                nodes: []
            };
            // this.checkedSubtree = {
            //     GroupTemplateKey: this.selectedTemplateInfo.Key,
            //     TerminalKey: terminal,
            //     Subtree: {
            //         Mother: row.tree,
            //         Terminal: terminal,
            //         Tree: []
            //     }
            // }
            // 选择mother tree
            this.motherTree = [];
            row.trees.forEach((tree) => {
                if (tree.id === row.tree) {
                    this.motherTree.push(...tree.rootNode.children);
                }
            });
            // 设置已勾选的节点
            // let permissions = new Set()
            getTemplatePermissionAPI(this.selectedTemplateInfo.id, terminal)
                .then((res) => {
                    if (res.code === 0) {
                        let permissions = new Set(res.data.nodes);
                        this.setCheckNodes(this.$refs.treeTable, this.motherTree, permissions);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        setCheckNodes(table, children, permissions) {
            children.forEach((mNode) => {
                if (mNode.children && mNode.children.length > 0) {
                    this.setCheckNodes(table, mNode.children, permissions);
                } else {
                    if (permissions.has(mNode.id)) {
                        table.setCheckboxRow(mNode, true);
                    }
                }
            });
        },
        popSubtree() {
            this.subtreePopFlag = true;
            this.$Utils.globalDragCenterFn('tree-modal');
        },
        closeSubtree() {
            this.currentTemplatePermission = {};
            this.motherTree = [];
            this.checkedNodes = [];
            this.subtreePopFlag = false;
        },
        customizeIcon(icon) {
            if (!icon) {
                return false;
            }
            if (icon.indexOf('yrt-') === -1) {
                return false;
            }
            return true;
        },
        addRole(role) {
            this.addTemplateRole(role.name).then((id) => {
                if (id && id.length > 0) {
                    this.$refs.rolesTable.confirmAdd(id);
                    this.roleTreeEditAbleFlag = true;
                } else {
                    this.$refs.rolesTable.cancelAdd();
                }
            });
        },
        delRole(id) {
            this.delTemplateRole(id).then((ok) => {
                if (ok) {
                    this.$refs.rolesTable.confirmDel(id);
                    // 删除了角色，恢复无选中状态
                    this.roleTreeEditAbleFlag = false;
                    this.$refs.rolesTrees.clearAllChecked();
                } else {
                    this.$refs.rolesTable.cancelDel();
                }
            });
        },
        updateRole(row) {
            this.updateTemplateRole(row.id, row.name).then((ok) => {
                if (ok) {
                    this.$refs.rolesTable.confirmUpdate(row);
                } else {
                    this.$refs.rolesTable.cancelUpdate(row);
                }
            });
        },
        delTemplate() {
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '删除后无法恢复，确认删除该模板？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.submitDelTemplate();
                },
                onCancel() {
                    _this.revertTerminalTree();
                }
            });
        },
        onRoleSubtreeChanged(changed) {
            this.roleChangeAbleFlag = !changed; // 角色树有修改，在保存或取消之前不能切换角色
        },
        async onSaveRolePermission(rolePermission) {
            if (!rolePermission || rolePermission === null) {
                return;
            }
            rolePermission.role = this.selectedRole.id;

            await setTemplateRolePermissionAPI(rolePermission)
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

            // this.setTemplateRolePermission(this.selectedRole.id, subtree).then(ok => {
            //     if (ok) {
            //         let subtrees = []
            //         subtrees.push(subtree)
            //         this.$refs.rolesTrees.updateCheckedNodes(subtrees)
            //     }
            // })
        },
        selectedRoleChange(selected) {
            this.selectedRole = selected;
            // 先清空选择
            this.$refs.rolesTrees.clearAllChecked();
            // 新建的角色，不读取权限树
            if (!selected || selected.id.length === 0) {
                this.roleTreeEditAbleFlag = false;
                return;
            }
            //
            this.getRolePermissions(selected.id).then((ok) => {
                if (ok) {
                    this.roleTreeEditAbleFlag = true;
                }
            });
        },
        async getAllGroupTemplate() {
            await getAllGroupTemplateAPI()
                .then((res) => {
                    if (res.code === 0) {
                        this.groupTemplates.push(...res.data);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        async addTemplate() {
            await addGroupTemplateAPI(this.templateInfo)
                .then((res) => {
                    if (res.code === 0) {
                        this.groupTemplates.push(this.templateInfo);
                        this.$refs.templateTable.refreshData();
                        this.templateInfoPopFlag = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        async updateTemplate() {
            await updateGroupTemplateAPI(this.templateInfo)
                .then((res) => {
                    if (res.Code || res.code === 0) {
                        this.selectedTemplateInfo.Name = this.templateInfo.Name;
                        this.selectedTemplateInfo.Desc = this.templateInfo.Desc;
                        this.$refs.templateTable.refreshData();
                        this.templateInfoPopFlag = false;
                    } else {
                        this.$message.error(res.Msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        async submitDelTemplate() {
            await delGroupTemplateAPI(this.selectedTemplateInfo.id)
                .then((res) => {
                    if (res.code === 0) {
                        this.groupTemplates.splice(this.selectedTemplateInfo.rowIndex, 1);
                        this.selectedTemplateInfo = {};
                        this.selectTemplateFlag = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch((e) => {
                    this.$message.error(e);
                });
        },
        async getAllTerminalTree() {
            await getAllTerminalTreeAPI()
                .then((res) => {
                    if (res.code === 0) {
                        res.data.forEach((terminal) => {
                            let t = {
                                key: terminal.key,
                                name: terminal.name,
                                trees: terminal.trees,
                                tree: -1,
                                treeName: '',
                                treeOptions: [
                                    {
                                        label: '无',
                                        value: -1,
                                        disabled: false
                                    }
                                ]
                            };
                            terminal.trees.forEach((tree) => {
                                t.treeOptions.push({
                                    label: tree.name,
                                    value: tree.id,
                                    disabled: false
                                });
                            });
                            this.terminals.push(t);
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('网络错误');
                });
        },
        // async getMotherTree (terminal, tree) {
        //     this.motherTree = []
        //     let success = false
        //     await getMotherTreeAPI(terminal, tree).then(res => {
        //         if (res.Code || res.code === 0) {
        //             this.motherTree.push(...res.data.Root.children)
        //             success = true
        //         } else {
        //             this.$message.error(res.Msg)
        //         }
        //     }).catch(() => {
        //         this.$message.error('意外错误')
        //     })
        //     return success
        // },
        async submitSetTemplatePermission() {
            // 获得更新的
            const selected = this.$refs.treeTable.selection;
            selected.forEach((node) => {
                this.currentTemplatePermission.nodes.push(node.id);
            });
            // let post = {
            //     template: this.selectedTemplateInfo.id,
            //     terminal: this.selectedTerminal.key,
            //     tree: 0,
            //     nodes: nodes
            // }

            await setTemplatePermissionAPI(this.currentTemplatePermission)
                .then((res) => {
                    if (res.code === 0) {
                        this.closeSubtree();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        async submitDelTree(template, terminal) {
            let isDeleted = false;
            await delTreeFromTemplateAPI(template, terminal)
                .then((res) => {
                    if (res.Code || res.code === 0) {
                        isDeleted = true;
                    } else {
                        // 不成功，要做恢复处理
                        this.$message.error(res.Msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
            return isDeleted;
        },
        // async getTemplateTreeIndex (template, terminal) {
        //     let success = false
        //     await getTemplateTreeIndexAPI(template, terminal).then(res => {
        //         if (res.Code || res.code === 0) {
        //             this.checkedSubtree.Subtree.Tree = res.data.Tree
        //             success = true
        //         } else {
        //             this.$message.error(res.Msg)
        //         }
        //     }).catch(() => {
        //         this.$message.error('意外错误')
        //     })
        //     return success
        // },
        formatTreeName({ row }) {
            let name = row.treeName;
            return name;
        },
        async getTemplateAllTreeInfo(template) {
            await getTemplateAllTreeInfoAPI(template)
                .then((res) => {
                    if (res.code === 0) {
                        // 设定options值
                        if (!res.data || !this.terminals) {
                            return;
                        }

                        let trees = res.data;
                        for (let i = 0; i < this.terminals.length; i++) {
                            this.terminals[i].tree = -1;
                            this.terminals[i].treeName = '无';
                            for (let j = 0; j < trees.length; j++) {
                                if (this.terminals[i].key === trees[j].terminal) {
                                    this.terminals[i].tree = trees[j].tree;
                                    for (let t = 0; t < this.terminals[i].trees.length; t++) {
                                        if (this.terminals[i].trees[t].id === trees[j].tree) {
                                            this.terminals[i].treeName = this.terminals[i].trees[t].name;
                                            continue;
                                        }
                                    }
                                    continue;
                                }
                            }
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        async getTemplateAllTree(id) {
            this.templateTree = [];
            await getTemplateAllTreeAPI(id)
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

                                this.templateTree.push(tree);
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
        async getTemplateRoles(template) {
            this.templateRoles = [];
            await getTemplateAllRolesAPI(template)
                .then((res) => {
                    if (res.code === 0) {
                        let roles = res.data;
                        roles.forEach((role) => {
                            let r = {
                                id: role.id,
                                name: role.name,
                                editAble: !role.internal
                            };
                            this.templateRoles.push(r);
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch((e) => {
                    this.$message.error(e);
                });
        },
        async addTemplateRole(name) {
            let role = {
                GroupTemplateKey: this.selectedTemplateInfo.Key,
                RoleName: name
            };
            let id = '';
            await addTemplateRoleAPI(role)
                .then((res) => {
                    if (res.Code || res.code === 0) {
                        id = res.data;
                    } else {
                        this.$message.error(res.Msg);
                    }
                })
                .catch((e) => {
                    this.$message.error(e);
                });
            return id;
        },
        async updateTemplateRole(id, name) {
            let success = false;
            let role = {
                GroupTemplateKey: this.selectedTemplateInfo.Key,
                RoleID: id,
                RoleName: name
            };
            await updateTemplateRoleNameAPI(role)
                .then((res) => {
                    if (res.Code || res.code === 0) {
                        success = true;
                    } else {
                        success = false;
                        this.$message.error(res.Msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
            return success;
        },
        async delTemplateRole(id) {
            let success = false;
            await delTemplateRoleAPI(this.selectedTemplateInfo.Key, id)
                .then((res) => {
                    if (res.Code || res.code === 0) {
                        success = true;
                    } else {
                        success = false;
                        this.$message.error(res.Msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
            return success;
        },
        // async setTemplateRolePermission (roleID, subtree) {
        //     let success = false
        //     let post = {
        //         RoleID: roleID,
        //         Tree: subtree
        //     }
        //     await setTemplateRolePermissionAPI(post).then(res => {
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
        noneTree(row) {
            if (!row || !row.tree || row.tree === '' || row.tree === -1) {
                return true;
            }
            return false;
        }
    },
    mounted() {
        this.getAllGroupTemplate();
        this.getAllTerminalTree();
    },
    computed: {}
};
</script>
<style lang="less">
.yrt-table-tree-icon {
    font-family: 'yrt' !important;
    font-style: normal;
    color: #778ca2;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
}
.modal-form-input-scoped .modal-form-input .item-children .yrt-short-label {
    width: 60px;
}
.modal-form-input-scoped .modal-form-input .yrt-short-label {
    width: 60px;
}
</style>
