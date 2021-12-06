<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="24">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="popNewTerminal" type="primary" size="small">新增端</a-button>
                            <a-button
                                :disabled="!selectTerminalFlag"
                                @click="popNewTreeInfo"
                                type="primary"
                                size="small"
                                >新增树</a-button
                            >
                            <a-button
                                :disabled="!selectTerminalFlag"
                                @click="popEditTerminla"
                                type="primary"
                                size="small"
                                >修改端</a-button
                            >
                            <a-button :disabled="!selectTreeFlag" @click="popEditTreeInfo" type="primary" size="small"
                                >修改树</a-button
                            >
                            <a-button :disabled="!treeSelected" @click="exportTree" type="primary" size="small"
                                >导出母树</a-button
                            >
                            <a-button @click="popImportTree" type="primary" size="small">导入母树</a-button>
                            <a-button
                                :disabled="!selectTreeFlag && !selectTerminalFlag"
                                @click="deleteTerminalTree"
                                type="danger"
                                size="small"
                                >删除</a-button
                            >
                        </li>
                        <li>
                            <a-button :disabled="!allowAddChild" @click="popNewNode" type="primary" size="small"
                                >新增节点</a-button
                            >
                            <a-button :disabled="!allowEdit" @click="popEditNode" type="primary" size="small"
                                >修改节点</a-button
                            >

                            <a-button :disabled="!allowDel" @click="delNode" type="danger" size="small"
                                >删除节点</a-button
                            >
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- 主体开始 -->
        <a-row class="tab-table-wrap">
            <!-- 端开始 -->
            <a-col :span="6" style="height: 100%">
                <div class="leftBox">
                    <vxe-table
                        border="none"
                        max-height="100%"
                        resizable
                        show-overflow
                        highlight-hover-row
                        highlight-current-row
                        ref="terminalTree"
                        @current-change="terminalSelectedChange"
                        :tree-config="treeConfig"
                        :data="terminals"
                    >
                        <vxe-table-column field="name" title="端/树" tree-node> </vxe-table-column>
                    </vxe-table>
                </div>
            </a-col>
            <!-- 端结束 -->
            <!-- 树开始 -->
            <a-col :span="18" style="height: 100%">
                <div class="rightBox">
                    <vxe-table
                        border="none"
                        max-height="100%"
                        resizable
                        show-overflow
                        highlight-hover-row
                        highlight-current-row
                        ref="motherTree"
                        @current-change="nodeSelectedChange"
                        :tree-config="treeConfig"
                        :data="rootNode"
                    >
                        <vxe-table-column field="name" title="Name" tree-node> </vxe-table-column>
                        <vxe-table-column field="id" title="ID" width="60"> </vxe-table-column>
                        <vxe-table-column field="runes" title="Runes">
                            <template v-slot="{ row }">
                                <i v-if="customizeIcon(row.runes)" class="yrt" :class="row.runes" />
                                <a-icon v-else :type="row.runes" />{{ row.runes }}
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="content" title="Content"></vxe-table-column>
                        <vxe-table-column field="note" title="Note"></vxe-table-column>
                        <vxe-table-column width="80" title="顺序">
                            <template v-slot="{ row }">
                                <a @click="nodeMoveUp(row)" v-show="upAble(row)"><a-icon type="arrow-up" /></a>
                                <a @click="nodeMoveDown(row)" v-show="downAble(row)"><a-icon type="arrow-down" /></a>
                            </template>
                        </vxe-table-column>
                    </vxe-table>
                </div>
            </a-col>
            <!-- 树结束 -->
        </a-row>
        <!-- 主体结束 -->
        <!-- 弹窗开始 -->
        <!-- 端弹窗开始 -->
        <a-modal
            title
            centered
            v-model="terminalPopFlag"
            :width="300"
            :maskClosable="false"
            class="modal-form-input-scoped terminal-modal global-drag"
        >
            <div v-globalDrag="{ el: 'terminal-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    端-{{ terminalPopTitleSuffix }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="terminalPopFlag = false">取消</a-button>
                <a-button key="submit" type="primary" @click="saveTerminal">保存</a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label yrt-short-label"> <em class="em red">*</em>Key </span>
                        <a-input v-model="terminal.key" placeholder :disabled="isEditTerminalFlag" class="input" />
                    </li>
                    <li class="item">
                        <span class="label yrt-short-label"> <em class="em red">*</em>名称 </span>
                        <a-input v-model="terminal.name" placeholder class="input" />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 端弹窗结束 -->
        <!-- 树信息弹窗开始 -->
        <a-modal
            title
            centered
            v-model="treeInfoPopFlag"
            :width="300"
            :maskClosable="false"
            class="modal-form-input-scoped tree-info-modal global-drag"
        >
            <div v-globalDrag="{ el: 'tree-info-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    树属性-{{ treePopTitleSuffix }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="treeInfoPopFlag = false">取消</a-button>
                <a-button key="submit" type="primary" @click="saveTree">保存</a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label yrt-short-label"> 端: </span>
                        <span> {{ treeInfo.terminalName }}({{ treeInfo.terminal }}) </span>
                    </li>
                    <li class="item" v-show="isEditTreeFlag">
                        <span class="label yrt-short-label"> ID </span>
                        <a-input :disabled="isEditTreeFlag" v-model="treeInfo.id" placeholder class="input" />
                    </li>
                    <li class="item">
                        <span class="label yrt-short-label"> <em class="em red">*</em>名称 </span>
                        <a-input v-model="treeInfo.name" placeholder class="input" />
                    </li>
                    <li class="item">
                        <span class="label yrt-short-label"> 备注 </span>
                        <a-input v-model="treeInfo.note" placeholder class="input" />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 树信息弹窗结束 -->
        <!-- 树节点弹框开始 -->
        <a-modal
            title
            centered
            v-model="nodePopFlag"
            :width="600"
            :maskClosable="false"
            class="modal-form-input-scoped node-modal global-drag"
        >
            <div v-globalDrag="{ el: 'node-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ $route.meta.title }}节点-{{ nodePopTitleSuffix }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="nodePopFlag = false"> 取消 </a-button>
                <a-button key="submit" type="primary" :loading="submitCatalog" @click="saveNode"> 保存 </a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <div class="item-children">
                            <span class="label yrt-short-label"><em class="em red">*</em>Name</span>
                            <a-input v-model="node.name" placeholder="" class="input" />
                        </div>
                        <div class="item-children">
                            <span class="label yrt-short-label"><em class="em red">*</em>Runes</span>
                            <a-input v-model="node.runes" placeholder="" class="input" />
                        </div>
                        <div class="item-children">
                            <span class="label yrt-short-label">Flag</span>
                            <a-input v-model="node.flag" placeholder="" class="input" />
                        </div>
                    </li>
                    <li class="item">
                        <span class="label yrt-short-label">Content</span>
                        <a-input v-model="node.content" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label yrt-short-label">Note</span>
                        <a-input v-model="node.note" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <div class="item-children">
                            <span class="label yrt-short-label"><em class="em red">*</em>类型</span>
                            <a-radio-group :options="nodeTypeOptions" v-model="node.type" />
                        </div>

                        <div class="item-children">
                            <span class="label yrt-short-label"><em class="em red">*</em>状态</span>
                            <a-radio-group :options="nodeStates" v-model="node.state" />
                        </div>
                    </li>
                    <li>
                        <span class="label yrt-short-label">APIs</span>
                        <!-- <div class="item-children"> -->
                        <!-- <div
                                v-for="(api,apiIndex) in node.apis"
                                :key="apiIndex"                                
                            > -->
                        <div
                            style="margin-bottom: 10px; margin-left: 60px"
                            v-for="(api, apiIndex) in node.apis"
                            :key="apiIndex"
                        >
                            <a-input :addon-before="apiIndex + 1" v-model="node.apis[apiIndex]" size="small">
                                <a-icon
                                    slot="addonAfter"
                                    type="close"
                                    class="api-input-delete"
                                    style="cursor: pointer"
                                    @click="removeApi(apiIndex)"
                                />
                            </a-input>
                        </div>
                        <!-- </div> -->

                        <!-- 新API 开始 -->
                        <div style="margin-bottom: 10px; margin-left: 60px">
                            <a-button type="dashed" size="small" block @click="addEmptyApi()">+</a-button>
                        </div>
                        <!-- </div> -->
                        <!-- API列表 结束 -->
                    </li>
                </ul>
            </div>
            <!-- <a-tabs
                v-show="node.boolType"
                v-model="activeFeature"
                type="editable-card"
                @edit="nodeFeatureChange"
                class="tabs-wrap"
            > -->
            <!-- <a-tabs
                v-model="activeFeature"
                type="editable-card"
                @edit="nodeFeatureChange"
                class="tabs-wrap"
            >
                <a-tab-pane
                    v-for="(f, featureIndex) in nodeFeatures"
                    :tab="featureTitle(f.name,f.flag)"
                    :key="featureIndex"
                    :closable="f.closeable"
                > -->
            <!-- API列表 开始 -->
            <!-- <div
                v-for="(api,apiIndex) in node.apis"
                :key="apiIndex"
                style="margin-bottom: 16px"
            >
                <a-input
                    v-model="node.apis[apiIndex]"
                    size="small"
                >
                    <a-icon
                        slot="addonAfter"
                        type="close"
                        class="api-input-delete"
                        style="cursor: pointer"
                        @click="removeApi(featureIndex,apiIndex)"
                    />
                </a-input>
            </div> -->
            <!-- API列表 结束 -->
            <!-- 新API 开始 -->
            <!-- <div>
                <a-button
                    type="dashed"
                    size="small"
                    block
                    @click="addEmptyApi(featureIndex)"
                >+</a-button>
            </div> -->
            <!-- 新API 结束 -->
            <!-- </a-tab-pane>
            </a-tabs> -->
            <!-- 菜单配置结束 -->
        </a-modal>
        <!-- 树节点弹框结束 -->
        <!-- Feature弹窗开始 -->
        <!-- <a-modal
            title=""
            centered
            v-model="featurePopFlag"
            :width="300"
            :maskClosable="false"
            class="modal-form-input-scoped global-drag feature-modal"
        >
            <div
                v-globalDrag="{el: 'feature-modal'}"
                class="modal-header-wrap"
            >
                <h6>
                    <img
                        src="/images/logo/logo-left.svg"
                        alt=""
                        class="modal-logo-left"
                    />
                    {{$route.meta.title}}菜单 - 新增功能
                </h6>
            </div>
            <template slot="footer">
                <a-button
                    key="back"
                    @click="featurePopFlag = false"
                >
                    取消
                </a-button>
                <a-button
                    key="submit"
                    type="primary"
                    @click="addFeature"
                >
                    保存
                </a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label yrt-short-label"><em class="em red">*</em>名称</span>
                        <a-input
                            v-model="feature.name"
                            placeholder=""
                            class="input"
                        />
                    </li>
                    <li class="item">
                        <span class="label yrt-short-label"><em class="em red">*</em>Falg</span>
                        <a-input
                            v-model="feature.flag"
                            placeholder=""
                            class="input"
                        />
                    </li>
                </ul>
            </div>
        </a-modal> -->
        <!-- Feature弹窗结束 -->
        <!-- 导入节点弹框开始 -->
        <a-modal
            title
            centered
            v-model="importNodePopFlag"
            @cancel="cancleImportTree"
            :width="1000"
            :maskClosable="false"
            class="modal-form-input-scoped import-node global-drag"
        >
            <div v-globalDrag="{ el: 'import-node' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    母树菜单-导入母树
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="cancleImportTree">取消</a-button>
                <a-button v-show="importFileLoadedFlag" key="submit" type="primary" @click="submitImportTree"
                    >导入</a-button
                >
            </template>
            <div class="modal-form-input">
                <div v-if="!importFileLoadedFlag" class="importNodeFile" @click="selectFile">
                    <input type="file" ref="nodeFileInput" style="display: none" @change="loadNodeFile" />
                    <div class="drop_zone">点击加载文件，或将文件拖放到此处</div>
                </div>
                <vxe-table
                    v-if="importFileLoadedFlag"
                    border="none"
                    height="600px"
                    resizable
                    show-overflow
                    ref="treeTable"
                    row-id="RowID"
                    :tree-config="treeConfig"
                    :data="importNodes"
                >
                    <vxe-table-column field="name" title="Name" tree-node> </vxe-table-column>
                    <vxe-table-column field="runes" title="Runes">
                        <template v-slot="{ row }">
                            <i v-if="customizeIcon(row.runes)" class="yrt" :class="row.runes" />
                            <a-icon v-else :type="row.runes" />{{ row.runes }}
                        </template>
                    </vxe-table-column>
                    <vxe-table-column field="content" title="Content"></vxe-table-column>
                    <vxe-table-column field="note" title="Note"></vxe-table-column>
                </vxe-table>
            </div>
        </a-modal>
        <!-- 导入节点弹框结束 -->
        <!-- 弹窗结束 -->
    </div>
</template>
<script>
import {
    addTerminalAPI,
    addTreeAPI,
    updateTreeApi,
    delTerminalAPI,
    getAllTerminalTreeAPI,
    updateTerminalAPI,
    delTreeAPI,
    // getMotherTreeAPI,
    addNodeAPI,
    updateNodeAPI,
    delNodeAPI,
    // getMTNodeAPI,
    importTreeAPI,
    nodeSwapSortAPI
} from '@/service/pageAjax';
// import draggable from 'vuedraggable'
export default {
    // components: {
    //     draggable
    // },
    data() {
        return {
            treeConfig: {
                line: true,
                children: 'children',
                iconOpen: 'yrt-table-tree-icon yrt-minus',
                iconClose: 'yrt-table-tree-icon yrt-jia2',
                expandAll: true
            },
            terminalPopFlag: false, // terminal弹框
            treeInfoPopFlag: false, // tree弹框
            nodePopFlag: false, // 树节点弹框
            // featurePopFlag: false, // 功能弹窗
            selectTerminalFlag: false, // 左侧选中terminal
            selectTreeFlag: false, // 左侧选中tree
            selectNodeFlag: false, // 右侧选中Node
            isEditTerminalFlag: false, // 修改标记
            isEditTreeFlag: false,
            isEditNodeFlag: false,
            importNodePopFlag: false, // 导入节点弹框标志
            importFileLoadedFlag: false, // 导入节点文件就绪
            selectedTerminal: {}, // 已选中terminal
            selectedTree: {}, // 已选中tree
            selectedNode: {}, // 已选中node
            terminal: {}, // 当前操作terminal
            treeInfo: {}, // 当前操作tree
            terminals: [], // 左侧表单值
            node: {}, // 当前操作的node
            tree: {}, // 当前操作的tree
            rootNode: [], // 右侧表单值（树）
            // activeFeature: 0, // 当前选中的tab
            // nodeFeatures: [], // 当前操作的功能
            // feature: {}, // 当前操作的功能（临时）
            importNodes: [], // 准备导入的节点
            importTree: {}, // 准备导入的树
            nodeTypeOptions: [
                { label: '菜单', value: 1 },
                { label: '功能', value: 2 }
            ],
            nodeStates: [
                { label: '正式', value: 1 },
                { label: '开发', value: 2 }
            ]
        };
    },
    methods: {
        popNewTerminal() {
            this.terminal = {
                name: '',
                key: ''
            };
            this.isEditTerminalFlag = false;
            this.popTerminal();
        },
        popEditTerminla() {
            this.terminal = {
                name: this.selectedTerminal.name,
                key: this.selectedTerminal.key
            };
            this.isEditTerminalFlag = true;
            this.popTerminal();
        },
        popTerminal() {
            this.terminalPopFlag = true;
            this.$Utils.globalDragCenterFn('terminal-modal');
        },
        popNewTreeInfo() {
            this.treeInfo = {
                terminal: this.selectedTerminal.key,
                terminalName: this.selectedTerminal.name,
                name: '',
                note: ''
            };
            this.isEditTreeFlag = false;
            this.popTreeInfo();
        },
        popEditTreeInfo() {
            this.treeInfo = {
                terminal: this.selectedTree.terminal,
                terminalName: this.selectedTree.terminalName,
                name: this.selectedTree.name,
                id: this.selectedTree.id,
                note: this.selectedTree.note
            };
            this.isEditTreeFlag = true;
            this.popTreeInfo();
        },
        popTreeInfo() {
            this.treeInfoPopFlag = true;
            this.$Utils.globalDragCenterFn('tree-info-modal');
        },
        popNewNode() {
            this.node = {
                flag: '',
                runes: '',
                name: '',
                state: 2,
                type: 1,
                content: '',
                parentID: this.selectedNode.id,
                apis: []
            };
            this.isEditNodeFlag = false;
            this.popNode();
        },
        popEditNode() {
            if (this.node.type === 0) {
                return;
            }
            this.node = this.selectedNode;
            this.isEditNodeFlag = true;
            this.popNode();
        },
        popNode() {
            this.nodePopFlag = true;
            this.$Utils.globalDragCenterFn('node-modal');
        },
        popImportTree() {
            this.importNodePopFlag = true;
            this.$Utils.globalDragCenterFn('import-node');
        },
        cancleImportTree() {
            this.importNodes = [];
            this.importFileLoadedFlag = false;
            this.importNodePopFlag = false;
        },
        saveTerminal() {
            if (this.isEditTerminalFlag) {
                this.submitUpdateTerminal();
            } else {
                this.submitAddTerminal();
            }
        },
        saveTree() {
            if (this.isEditTreeFlag) {
                this.submitUpdateTree();
            } else {
                this.submitAddTree();
            }
        },
        deleteTerminalTree() {
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '一旦删除无法撤销，确认要删除吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    if (_this.selectTerminalFlag) {
                        _this.submitDelTerminal();
                    } else {
                        _this.submitDelTree();
                    }
                }
            });
        },
        terminalSelectedChange(selected) {
            this.rootNode = [];
            this.selectedNode = {};
            if (selected.row.type === 'terminal') {
                this.selectedTerminal = selected.row;
                this.selectedTerminal.rowIndex = selected.$rowIndex;
                this.selectTerminalFlag = true;
                this.selectTreeFlag = false;
            } else {
                this.selectedTree = selected.row;
                this.selectedTree.rowIndex = selected.$rowIndex;
                this.selectTreeFlag = true;
                this.selectTerminalFlag = false;
                this.rootNode.push(selected.row.root);
            }
        },
        nodeSelectedChange(selected) {
            this.selectedNode = selected.row;
        },
        nodeStateChange(state) {
            if (state === 1) {
                this.node.State = 2;
            } else {
                this.node.State = 1;
            }
        },
        removeApi(apiIndex) {
            if (apiIndex === 0) {
                this.node.apis.shift();
            } else {
                this.node.apis.splice(apiIndex, 1);
            }
        },
        addEmptyApi() {
            // 判断该feature下api是否存在
            if (!this.node.apis) {
                this.node.apis = [];
            }
            this.node.apis.push('');
        },
        saveNode() {
            if (this.isEditNodeFlag) {
                this.submitUpdateNode();
            } else {
                this.submitAddNode();
            }
        },
        delNode() {
            // 修改，判断是否有选中的
            if (!this.selectedNode || this.selectedNode.id.length === 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择要删除的节点',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return true;
            }
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认要删除该节点吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.submitDelNode();
                    // _this.roles.splice(_this.selectedRowIndex, 1)
                }
            });
        },
        // 导出节点，存储为json文件
        exportTree() {
            // TODO: 应改为ajax
            window.open('/v1/tree/export_tree/' + this.selectedTree.id);
        },
        loadNodeFile(evt) {
            let files = evt.target.files; // FileList object
            if (evt.target.files.length === 0) {
                this.$message.error('未找到文件');
                return;
            }

            let reader = new FileReader();
            let _this = this;
            reader.onloadend = function () {
                let result = this.result;
                if (result === null) {
                    _this.$message.error('文件为空或读取失败');
                }
                let tree = JSON.parse(this.result);
                if (tree.id && tree.terminal) {
                    // 载入文件
                    _this.importTree = tree;
                    _this.importNodes = [];
                    _this.importNodes.push(tree.rootNode);
                    _this.importFileLoadedFlag = true;
                } else {
                    _this.$message.error('导入的树文件错误，请检查');
                }
            };

            reader.readAsText(files[0]);
        },
        validateNode() {
            let node = {
                id: this.node.id,
                name: this.node.name,
                flag: this.node.flag,
                runes: this.node.runes,
                note: this.node.note,
                content: this.node.content,
                parentID: this.node.parentID,
                type: this.node.type,
                state: this.node.state,
                free: false,
                apis: this.node.apis
            };

            return node;
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
        async submitAddNode() {
            let node = this.validateNode();
            await addNodeAPI(node)
                .then((res) => {
                    if (res.code === 0) {
                        // 加入树
                        // node.id = res.data
                        node = res.data;
                        this.insertNode(node);
                        this.nodePopFlag = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        insertNode(node) {
            node.children = []; // 防止增加新的子节点时出错
            this.selectedNode.children.push(node);

            let xTree = this.$refs.motherTree;
            xTree.setTreeExpansion(this.selectedNode, true);
            xTree.refreshData();
        },
        async submitUpdateNode() {
            let node = this.validateNode();
            await updateNodeAPI(node)
                .then((res) => {
                    if (res.code === 0) {
                        let xTree = this.$refs.motherTree;
                        // let old = this.selectedNode
                        this.selectedNode.flag = node.flag;
                        this.selectedNode.runes = node.runes;
                        this.selectedNode.name = node.name;
                        this.selectedNode.note = node.note;
                        this.selectedNode.type = node.type;
                        this.selectedNode.content = node.content;
                        this.selectedNode.apis = node.apis;
                        this.selectedNode.state = node.state;

                        xTree.reloadRow(this.selectedNode);
                        this.nodePopFlag = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        async submitDelNode() {
            if (this.selectedNode.id.length <= 0) {
                return;
            }
            if (this.selectedNode.children && this.selectedNode.children.length > 0) {
                this.$message.error('为了安全起见，不能删除有子节点的节点，如确定要删除，请先删除子节点');
                return;
            }
            await delNodeAPI(this.selectedNode.id)
                .then((res) => {
                    if (res.code === 0) {
                        this.removeNode();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        removeNode() {
            let { items, index } = this.$XEUtils.findTree(
                this.rootNode,
                (item) => item.id === this.selectedNode.id,
                this.treeConfig
            );
            // 调整node.Index: 所有删除节点之后的节点往上调一位
            // let lastIndex = items[index].Index
            // let tempIndex = ''
            // for (let i = index + 1; i < items.length; i++) {
            //     tempIndex = items[i].Index
            //     items[i].Index = lastIndex
            //     lastIndex = tempIndex
            // }
            // 删除节点
            items.splice(index, 1);
            this.$refs.motherTree.refreshData();
            this.selectedNode = {};
        },
        async submitAddTerminal() {
            // TODO: check terminal
            await addTerminalAPI(this.terminal)
                .then((res) => {
                    if (res.code === 0) {
                        this.terminals.push({
                            type: 'terminal',
                            key: this.terminal.key,
                            name: this.terminal.name
                        });
                        this.terminalPopFlag = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        async submitUpdateTerminal() {
            // TOOD check terminal
            await updateTerminalAPI(this.terminal)
                .then((res) => {
                    if (res.code === 0) {
                        this.selectedTerminal.name = this.terminal.name;
                        this.selectedTerminal.key = this.terminal.key;
                        this.terminalPopFlag = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        async submitDelTerminal() {
            await delTerminalAPI(this.selectedTerminal.key)
                .then((res) => {
                    if (res.code === 0) {
                        this.terminals.splice(this.selectedTerminal.rowIndex, 1);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        async submitAddTree() {
            // TODO check tree
            await addTreeAPI(this.treeInfo)
                .then((res) => {
                    if (res.code === 0) {
                        let mt = res.data;
                        this.selectedTerminal.children.push({
                            type: 'tree',
                            id: mt.id,
                            name: mt.name,
                            note: mt.note,
                            terminal: mt.terminal,
                            terminalName: this.selectedTerminal.name,
                            root: mt.rootNode
                        });
                        this.$refs.terminalTree.refreshData();
                        this.treeInfoPopFlag = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch((e) => {
                    this.$message.error(e);
                });
        },
        async submitUpdateTree() {
            await updateTreeApi(this.treeInfo)
                .then((res) => {
                    if (res.code === 0) {
                        this.selectedTree.name = this.treeInfo.name;
                        this.selectedTree.note = this.treeInfo.note;
                        this.treeInfoPopFlag = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
        },
        async submitDelTree() {
            await delTreeAPI(this.selectedTree.id)
                .then((res) => {
                    if (res.code === 0) {
                        // 找到端
                        this.terminals.some((terminal) => {
                            if (terminal.key === this.selectedTree.terminal) {
                                terminal.children.splice(this.selectedTree.rowIndex, 1);
                                this.$refs.terminalTree.syncData();
                                return true;
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
        // TODO: 准备删除
        // async getMotherTree () {
        //     await getMotherTreeAPI(this.selectedTree.terminal, this.selectedTree.key).then(res => {
        //         if (res.Code || res.code === 0) {
        //             this.rootNode.push(res.data.Root)
        //         } else {
        //             this.$message.error(res.Msg)
        //         }
        //     }).catch(() => {
        //         this.$message.error('意外错误')
        //     })
        // },
        async submitImportTree() {
            if (this.importTree) {
                await importTreeAPI(this.importTree)
                    .then((res) => {
                        if (res.code === 0) {
                            // 插入节点到树上
                            this.$message.success('导入成功，请刷新页面');
                            // 关闭弹框
                            this.cancleImportTree();
                        } else {
                            this.$message.error(res.msg);
                        }
                    })
                    .catch(() => {
                        this.$message.error('意外错误');
                    });
            } else {
                this.$message.error('没有可以导入的树');
            }
        },
        async getAllTerminalTree() {
            await getAllTerminalTreeAPI()
                .then((res) => {
                    if (res.code === 0) {
                        res.data.forEach((terminal) => {
                            let t = {
                                type: 'terminal',
                                key: terminal.key,
                                name: terminal.name,
                                children: []
                            };
                            terminal.trees.forEach((tree) => {
                                t.children.push({
                                    type: 'tree',
                                    id: tree.id,
                                    name: tree.name,
                                    note: tree.note,
                                    terminal: t.key,
                                    terminalName: t.name,
                                    root: tree.rootNode
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
        // 检查必填项
        checkInputValue(value, input) {
            if (value) {
                value = this.$Utils.trim(value);
                if (!value) {
                    this.notifyEmptyInput(input);
                    return false;
                }
            } else {
                this.notifyEmptyInput(input);
                return false;
            }
            return value;
        },
        // featureTitle (name, flag) {
        //     if (flag && flag.length > 0) {
        //         return name + '(' + flag + ')'
        //     }
        //     return name
        // },
        titleSuffix(isEdit) {
            if (isEdit) {
                return '修改';
            }
            return '新建';
        },
        selectFile() {
            this.$refs.nodeFileInput.click();
        },
        upAble(row) {
            // 根节点不能调整
            if (row.parentID > 0) {
                let parent = this.findNodeByID(this.rootNode[0], row.parentID);
                if (parent !== null) {
                    if (parent.children[0].id === row.id) {
                        return false;
                    }
                    return true;
                }
            }
            return false;
        },
        downAble(row) {
            // 根节点不能调整
            if (row.parentID > 0) {
                // 查找父节点
                let parent = this.findNodeByID(this.rootNode[0], row.parentID);
                if (parent !== null) {
                    if (parent.children[parent.children.length - 1].id === row.id) {
                        return false;
                    }
                    return true;
                }
            }
            return false;
        },
        findNodeByID(node, id) {
            if (node.id === id) {
                return node;
            }
            if (node.children && node.children.length > 0) {
                for (let i = 0; i < node.children.length; i++) {
                    let child = this.findNodeByID(node.children[i], id);
                    if (child !== null) {
                        return child;
                    }
                }
            }
            return null;
        },

        async nodeSwap(node1, node2) {
            let success = false;
            let post = {
                ID1: node1.id,
                ID2: node2.id
            };
            await nodeSwapSortAPI(post)
                .then((res) => {
                    if (res.code === 0) {
                        success = true;
                    } else {
                        this.$message.error(res.Msg);
                    }
                })
                .catch(() => {
                    this.$message.error('未知错误');
                });
            return success;
        },

        nodeMoveUp(node) {
            // 找到父节点
            let parent = this.getParentNode(node);
            if (!parent) {
                return;
            }
            for (let i = 0; i < parent.children.length; i++) {
                if (node.id === parent.children[i].id) {
                    if (parent.children[i - 1]) {
                        this.nodeSwap(node, parent.children[i - 1]).then((success) => {
                            if (success) {
                                parent.children.splice(
                                    i - 1,
                                    1,
                                    ...parent.children.splice(i, 1, parent.children[i - 1])
                                );
                            }
                        });
                    }
                }
            }
        },
        nodeMoveDown(node) {
            let parent = this.getParentNode(node);
            if (!parent) {
                return;
            }
            for (let i = 0; i < parent.children.length; i++) {
                if (node.id === parent.children[i].id) {
                    if (parent.children[i + 1]) {
                        this.nodeSwap(node, parent.children[i + 1]).then((success) => {
                            if (success) {
                                parent.children.splice(i, 1, ...parent.children.splice(i + 1, 1, parent.children[i]));
                            }
                        });
                    }
                }
            }

            // // 提交请求
            // await nodeMoveDownAPI(node.ID).then(res => {
            //     if (res.Code || res.code === 0) {
            //         // 获取父节点
            //         let parent = this.getParentNode(node)
            //         // 更改本节点和交换节点的index
            //         let indexArr = node.Index.split('.')
            //         let index = parseInt(indexArr[indexArr.length - 1])
            //         let strIndex = parent.children[index].Index
            //         parent.children[index].Index = parent.children[index + 1].Index
            //         parent.children[index + 1].Index = strIndex
            //         // 调整位置
            //         parent.children.splice(i, 1, ...parent.children.splice(i + 1, 1, parent.children[i]))
            //     } else {
            //         this.$message.error(res.Msg)
            //     }
            // }).catch(() => {
            //     this.$message.error('未知错误')
            // })
        },
        getParentNode(node) {
            let pathArr = node.path.split('/');
            let children = this.rootNode;
            let parent = null;
            for (let level = 1; level < pathArr.length - 1; level++) {
                let id = parseInt(pathArr[level]);
                if (children.length === 0) {
                    return null;
                }

                for (let i = 0; i < children.length; i++) {
                    if (children[i].id === id) {
                        if (level === pathArr.length - 2) {
                            parent = children[i];
                            break;
                        } else {
                            children = children[i].children;
                        }
                    }
                }
            }
            return parent;
        }
    },

    mounted() {
        this.getAllTerminalTree();
    },
    computed: {
        terminalPopTitleSuffix() {
            return this.titleSuffix(this.isEditTerminalFlag);
        },
        treePopTitleSuffix() {
            return this.titleSuffix(this.isEditTreeFlag);
        },
        nodePopTitleSuffix() {
            return this.titleSuffix(this.isEditNodeFlag);
        },
        // 菜单节点（目录无）feature计数，至少有1个（base）
        // nodeFeatureCount () {
        //     return this.nodeFeatures.lenght
        // },
        allowAddChild() {
            if (this.selectedNode.type !== undefined) {
                return true;
            }
            return false;
        },
        allowDel() {
            if (this.selectedNode.type > 0) {
                if (this.selectedNode.children && this.selectedNode.children.length > 0) {
                    return false;
                }
                return true;
            }
            return false;
        },
        treeSelected() {
            if (this.selectedTree && this.selectedTree.id > 0) {
                return true;
            }
            return false;
        },
        allowEdit() {
            if (this.selectedNode.type > 0) {
                return true;
            }
            return false;
        },
        maxHeight() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
            return clientHeight - 110;
        }
    }
};
</script>
<style lang="less">
.modal-form-input-scoped .modal-form-input .item-children .yrt-short-label {
    width: 60px;
}
.modal-form-input-scoped .modal-form-input .yrt-short-label {
    width: 60px;
}
.yrt-table-tree-icon {
    font-family: 'yrt' !important;
    font-style: normal;
    color: #778ca2;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
}

// 表格外层
.tab-table-wrap {
    .leftBox,
    .rightBox {
        background: #fff;
        height: 100%;
    }
    .leftBox {
        margin-right: 4px;
        ul {
            flex-direction: row;
            height: 100%;
            padding-left: 0;
            margin-bottom: 0;
            border: 1px solid transparent;
        }
    }
}
.modal-form-input-scoped .importNodeFile {
    padding: 10px;
    border: 1px solid #ccc;
    .drop_zone {
        border: 2px dashed #bbb;
        border-radius: 5px;
        padding: 25px;
        text-align: center;
        color: #bbb;
    }
}
</style>
