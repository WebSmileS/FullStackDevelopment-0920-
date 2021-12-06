<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="24">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button :disabled="newNodeDisable" @click="handleNewCatalog" type="primary" size="small">
                                新增分类
                            </a-button>
                            <a-button :disabled="newNodeDisable" @click="handleNewMenu" type="primary" size="small">
                                新增菜单
                            </a-button>
                        </li>
                        <li>
                            <a-button @click="handleEdit" type="primary" size="small"> 修改 </a-button>
                            <a-button @click="handleRemove" type="danger" size="small"> 删除 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab切换区域开始 -->
        <div class="tab-table-wrap">
            <!-- 表格头部 -->
            <a-tabs @change="changeTabsFn" type="card">
                <a-tab-pane tab="Web" :key="systemTypes[0]">
                    <a-table
                        :columns="columns"
                        :dataSource="platformTree"
                        :pagination="false"
                        :loading="laodingFlag"
                        :expandedRowKeys="expandedRowKeys"
                        :customRow="customRowFn"
                        :rowClassName="rowClassFn"
                        @expand="expandFn"
                    >
                        <template slot="Name" slot-scope="text, row">
                            <i v-if="customizeIcon(row.Icon)" class="yrt" :class="row.Icon" />
                            <a-icon v-else :type="row.Icon" />{{ row.Name }}
                        </template>
                        <template slot="operation" slot-scope="text, row">
                            <a @click="nodeMoveUp(row)" v-show="upAble(row)"><a-icon type="arrow-up" /></a>
                            <a @click="nodeMoveDown(row)" v-show="downAble(row)"><a-icon type="arrow-down" /></a>
                        </template>
                    </a-table>
                </a-tab-pane>
                <a-tab-pane tab="App" :key="systemTypes[1]">
                    <a-table :columns="columns" :dataSource="datas" :pagination="false" :loading="laodingFlag">
                    </a-table>
                </a-tab-pane>
            </a-tabs>
            <!--表格区域-->
        </div>
        <!-- tab切换区域结束 -->
        <!-- 分类弹框开始 -->
        <a-modal
            title=""
            centered
            v-model="popCatalogFlag"
            :width="400"
            :maskClosable="false"
            class="modal-form-input-scoped global-drag global-drag-catalog"
        >
            <div v-globalDrag="{ el: 'global-drag-catalog' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ $route.meta.title }}{{ popTitleSuffix }}分类
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="popCatalogFlag = false"> 取消 </a-button>
                <a-button key="submit" type="primary" :loading="submitCatalog" @click="saveCatalog"> 保存 </a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>名称</span>
                        <a-input v-model="hotCatalog.Name" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>Icon</span>
                        <a-input v-model="hotCatalog.Icon" placeholder="" class="input" />
                    </li>
                    <li class="item" v-if="isEdit">
                        <span class="label"><em class="em red">*</em>状态</span>
                        <a-switch
                            :checked="hotCatalog.State"
                            checkedChildren="启用"
                            unCheckedChildren="禁用"
                            @change="hotCatalogStatusChange"
                        />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 分类弹框结束 -->
        <!-- 菜单弹框开始 -->
        <a-modal
            centered
            width="500px"
            v-model="popMenuFlag"
            :maskClosable="false"
            class="modal-form-input-scoped global-drag global-drag-menu"
        >
            <div v-globalDrag="{ el: 'global-drag-menu' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ $route.meta.title }}{{ popTitleSuffix }}菜单
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="popMenuFlag = false"> 取消 </a-button>
                <a-button key="submit" type="primary" :loading="submitMenu" @click="saveMenu"> 保存 </a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <div class="item-children">
                            <span class="label"><em class="em red">*</em>名称</span>
                            <a-input v-model="hotNode.Name" placeholder="" class="input" />
                        </div>
                        <div class="item-children">
                            <span class="label"><em class="em red">*</em>Icon</span>
                            <a-input v-model="hotNode.Icon" placeholder="" class="input" />
                        </div>
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>URI</span>
                        <a-input v-model="hotNode.URI" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>状态</span>
                        <a-switch
                            v-model="hotNode.State"
                            checkedChildren="启用"
                            unCheckedChildren="禁用"
                            @change="hotMenuStatusChange"
                        />
                    </li>
                </ul>
            </div>
            <a-tabs v-model="activeKey" type="editable-card" @edit="onEdit" class="tabs-wrap">
                <a-tab-pane
                    v-for="(f, featureIndex) in features"
                    :tab="featureTitle(f.Name, f.Flag)"
                    :key="featureIndex"
                    :closable="f.Closable"
                >
                    <div v-for="(api, apiIndex) in f.APIs" :key="apiIndex" style="margin-bottom: 16px">
                        <a-input v-model="f.APIs[apiIndex]" size="small">
                            <a-icon
                                slot="addonAfter"
                                type="close"
                                class="api-input-delete"
                                style="cursor: pointer"
                                @click="removeApi(featureIndex, apiIndex)"
                            />
                        </a-input>
                    </div>
                    <!-- 增加API开始 -->
                    <div>
                        <a-button type="dashed" size="small" block @click="addEmptyApi(featureIndex)">+</a-button>
                    </div>
                    <!-- 增加API结束 -->
                </a-tab-pane>
            </a-tabs>
        </a-modal>
        <!-- 菜单弹框结束 -->
        <!-- 功能弹框开始 -->
        <a-modal
            title=""
            centered
            v-model="popFeatureFlag"
            :width="400"
            :maskClosable="false"
            class="modal-form-input-scoped global-drag global-drag-feature"
        >
            <div v-globalDrag="{ el: 'global-drag-feature' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ $route.meta.title }}{{ popTitleSuffix }}菜单 - 新增功能
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="popFeatureFlag = false"> 取消 </a-button>
                <a-button key="submit" type="primary" @click="addFeature"> 保存 </a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>名称</span>
                        <a-input v-model="hotFeature.Name" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>Falg</span>
                        <a-input v-model="hotFeature.Flag" placeholder="" class="input" />
                    </li>
                    <!-- <li class="item">
                        <span class="label">备注</span>
                        <a-input
                            v-model="hotFeature.Description"
                            placeholder=""
                            class="input"
                        />
                    </li> -->
                </ul>
            </div>
        </a-modal>
        <!-- 分类弹框结束 -->
    </div>
</template>

<script>
import {
    getPlatformTree,
    getAllTree,
    addNode,
    updateNode,
    deleteNode,
    moveUpNode,
    moveDownNode,
    menuGetNode
} from '@/service/pageAjax';

export default {
    name: 'menuTree',
    data() {
        return {
            // 表格的配置
            laodingFlag: false,
            popCatalogFlag: false,
            popMenuFlag: false,
            popFeatureFlag: false,
            isEdit: false,
            expandedRowKeys: [],
            columns: [
                {
                    title: '名称',
                    dataIndex: 'Name',
                    key: 'Name',
                    width: '30%',
                    align: 'center',
                    scopedSlots: { customRender: 'Name' }
                },
                {
                    title: '类型',
                    dataIndex: 'Type',
                    key: 'Type',
                    width: '10%',
                    align: 'center'
                },
                {
                    title: 'Icon',
                    dataIndex: 'Icon',
                    key: 'Icon',
                    width: '10%',
                    align: 'center'
                },
                {
                    title: 'URI',
                    dataIndex: 'URI',
                    key: 'URI',
                    width: '40%',
                    align: 'center'
                },
                {
                    title: '顺序调整',
                    dataIndex: 'operation',
                    key: 'operation',
                    width: '10%',
                    align: 'center',
                    scopedSlots: { customRender: 'operation' }
                }
            ],
            platformTree: [],
            datas: [],
            activeKey: 0,
            features: [
                { name: '基本', flag: 'base', apis: ['aaaaa/bbbb/cccc', 'sssss/ewwww/adwww'], closable: false },
                { name: '增加', flag: 'add', apis: [], closable: true },
                { name: '删除', flag: 'remove', apis: [], closable: true }
            ],
            systemTypes: ['平台', '医院'],
            hotCatalog: {},
            hotMenu: {},
            hotFeature: {},
            hotNode: {},
            selectedRow: {},
            submitCatalog: false,
            submitMenu: false
        };
    },
    methods: {
        async loadPlatformTree() {
            this.laodingFlag = true;
            await getPlatformTree().then((res) => {
                if (parseFloat(res.Code || res.code) === 0) {
                    let root = {
                        key: res.data.ID,
                        Type: '分类',
                        Name: '平台菜单',
                        Icon: 'root',
                        URI: '/',
                        Index: res.data.Index
                    };
                    // if (res.data.children && res.data.children.length > 0) {
                    root.children = this.mockTree(res.data);
                    // }
                    // 处理level1级菜单，加入key值
                    this.platformTree.push(root);
                    // 展开一级菜单
                    this.expandFn(true, root);
                    this.laodingFlag = false;
                } else {
                    this.$message.error(res.Msg);
                }
            });
        },
        async loadAllTree() {
            this.laodingFlag = true;
            await getAllTree().then((res) => {
                if (parseFloat(res.Code || res.code) === 0) {
                    // 填充数据
                    res.data.forEach((root) => {
                        let rootNode = {
                            key: root.ID,
                            Name: root.Name,
                            Type: '分类',
                            Icon: 'root',
                            Index: root.Index
                        };
                        rootNode.children = this.mockTree(root);
                        this.platformTree.push(rootNode);
                    });

                    // 展开一级菜单
                    this.laodingFlag = false;
                } else {
                    this.$message.error(res.Msg);
                }
            });
        },
        async addCatalog() {
            // 判断当前选中的值是否可以增加子类
            if (this.selectedRow.Type !== '分类') {
                return false;
            }
            // 组装params
            let newNode = {
                Name: this.hotCatalog.Name,
                Parent: this.selectedRow.key,
                Icon: this.hotCatalog.Icon,
                Type: 'cat'
            };
            await addNode(newNode).then((res) => {
                // 检查返回
                if (parseFloat(res.Code || res.code) === 0) {
                    // 成功返回
                    newNode.key = res.data;
                    // 关闭弹窗
                    this.popCatalogFlag = false;
                    // 增加到Tree中
                    newNode.Type = '分类';
                    if (!this.selectedRow.children) {
                        this.selectedRow.children = [];
                    }
                    this.selectedRow.children.push(newNode);
                    // 并展开
                    this.expandFn(true, this.selectedRow);
                } else {
                    this.$message.error(res.Msg);
                }
            });
        },
        async updateCatalog() {
            // 组装params
            let node = {
                ID: this.hotCatalog.key,
                Name: this.hotCatalog.Name,
                Icon: this.hotCatalog.Icon,
                State: this.hotCatalog.State
            };
            await updateNode(node).then((res) => {
                // 检查返回
                if (parseFloat(res.Code || res.code) === 0) {
                    // 成功返回
                    // 关闭弹窗
                    this.popCatalogFlag = false;
                    // 替换Tree中的值
                    this.selectedRow.Name = node.Name;
                    this.selectedRow.Icon = node.Icon;
                    this.selectedRow.State = node.State;
                } else {
                    this.$message.error(res.Msg);
                }
            });
        },
        async addMenu(newNode) {
            // 判断当前选中的值是否可以增加子类
            if (this.selectedRow.Type !== '分类') {
                return false;
            }
            newNode.Parent = this.selectedRow.key;
            newNode.Type = 'leaf';

            await addNode(newNode).then((res) => {
                // 检查返回
                if (parseFloat(res.Code || res.code) === 0) {
                    // 成功返回
                    newNode.key = res.data;
                    newNode.Type = '菜单';
                    // 增加到Tree中
                    if (!this.selectedRow.children) {
                        this.selectedRow.children = [];
                    }
                    this.selectedRow.children.push(newNode);
                    // 并展开
                    this.expandFn(true, this.selectedRow);
                    // 关闭弹窗
                    this.popMenuFlag = false;
                } else {
                    this.$message.error(res.Msg);
                }
            });
        },
        async updateMenu(node) {
            // 组装params
            // let node = {
            //     ID: this.hotMenu.key,
            //     Name: this.hotMenu.Name,
            //     Icon: this.hotMenu.Icon,
            //     URI: this.hotMenu.URI,
            //     State: this.hotMenu.State
            // }
            await updateNode(node).then((res) => {
                // 检查返回
                if (parseFloat(res.Code || res.code) === 0) {
                    // 成功返回
                    // 关闭弹窗
                    this.popMenuFlag = false;
                    // 替换Tree中的值
                    this.selectedRow.Name = node.Name;
                    this.selectedRow.Icon = node.Icon;
                    this.selectedRow.URI = node.URI;
                    this.selectedRow.State = node.State;
                } else {
                    this.$message.error(res.Msg);
                }
            });
        },
        async deleteNode(id) {
            // 组装params
            await deleteNode(id).then((res) => {
                // 检查返回
                if (parseFloat(res.Code || res.code) === 0) {
                    // 成功返回
                    // 从tree中删除
                    let parent = this.findNode(this.platformTree, this.selectedRow.Parent);
                    if (parent) {
                        if (parent.children.length < 2) {
                            parent.children = null;
                            this.expandFn(false, parent);
                            this.selectedRow = {};
                            return true;
                        }
                        parent.children.some((n, index) => {
                            if (n.key === this.selectedRow.key) {
                                parent.children.splice(index, 1);
                                this.selectedRow = {};
                                return true;
                            }
                        });
                    }
                } else {
                    this.$message.error(res.Msg);
                }
            });
        },
        mockTree(root) {
            if (!root.children || root.children.length === 0) {
                return null;
            }
            // 准备copy实体
            let nodes = [];
            // 循环加入key值和检查children
            root.children.forEach((n) => {
                let t = '';
                if (n.Type === 'cat') {
                    t = '分类';
                } else {
                    t = '菜单';
                }
                // 加入key值
                let node = {
                    ID: n.ID,
                    key: n.ID,
                    Type: t,
                    Name: n.Name,
                    Icon: n.Icon,
                    URI: n.URI,
                    Index: n.Index,
                    Parent: root.ID,
                    State: n.State
                };
                // 递归
                node.children = this.mockTree(n);
                nodes.push(node);
            });
            // 返回
            return nodes;
        },
        // 根据key递归寻找对应的节点
        findNode(nodes, key) {
            for (let i = 0; i < nodes.length; i++) {
                let node = nodes[i];
                if (node.key === key) {
                    return node;
                }

                if (node.children && node.children.length > 0) {
                    for (let i = 0; i < node.children.length; i++) {
                        let n = this.findNode(node.children, key);
                        if (n !== false) {
                            return n;
                        }
                    }
                }
            }

            // if (node.children && node.children.length > 0) {
            //     for (let i = 0; i < node.children.length; i++) {
            //         let n = this.findNode(node.children[i], key)
            //         if (n !== false) {
            //             return n
            //         }
            //     }
            // }

            return false;
        },
        // 根据key递归查找parent node
        findParentByKey(node, key) {
            if (!node.children || node.children.length === 0) {
                return false;
            }

            for (let i = 0; i < node.children.length; i++) {
                if (node.children[i].key === key) {
                    return node; // 找到
                }
                return this.findParentByKey(node.children[i], key);
            }
            return false;
        },
        // 保存分类，判断是否修改已有
        saveCatalog() {
            // 检查必填项
            let name = this.checkInputValue(this.hotCatalog.Name, '名称');
            if (!name) {
                return;
            } else {
                this.hotCatalog.Name = name;
            }

            let icon = this.checkInputValue(this.hotCatalog.Icon, '图标名');
            if (!icon) {
                return;
            } else {
                this.hotCatalog.Icon = icon;
            }
            // 设置loading
            this.submitCatalog = true;
            // 修改已有分类
            if (this.isEdit) {
                this.updateCatalog();
            } else {
                // 增加分类
                this.addCatalog();
            }
            this.submitCatalog = false;
        },
        // 保存菜单，判断是否修改
        saveMenu() {
            // 检查必填项
            let name = this.checkInputValue(this.hotNode.Name, '名称');
            if (!name) {
                return;
            } else {
                this.hotNode.Name = name;
            }

            let icon = this.checkInputValue(this.hotNode.Icon, '图标名');
            if (!icon) {
                return;
            } else {
                this.hotNode.Icon = icon;
            }

            let uri = this.checkInputValue(this.hotNode.URI, '路由地址');
            if (!uri) {
                return;
            } else {
                this.hotNode.URI = uri;
            }

            // 组合要提交的node

            // 设置loading
            this.submitMenu = true;
            if (this.isEdit) {
                // 修改菜单
                this.updateMenu(this.fillMenuNode());
            } else {
                // 增加菜单
                this.addMenu(this.fillMenuNode());
            }
            this.submitMenu = false;
        },

        // 填充node（menu）用于新增或更新
        fillMenuNode() {
            let id = '';
            if (this.hotNode.ID.length > 0) {
                id = this.hotNode.ID;
            }
            let node = {
                ID: id,
                Name: this.hotNode.Name,
                Icon: this.hotNode.Icon,
                URI: this.hotNode.URI,
                State: this.hotNode.State ? 1 : 0,
                Features: {
                    Internal: {},
                    Additional: []
                }
            };

            // 填充Features
            // internal feature
            node.Features.Internal = {
                NodeID: id,
                Name: this.features[0].Name,
                APIs: this.features[0].APIs
            };

            // node.Features.Additional = {}
            for (let i = 1; i < this.features.length; i++) {
                let feature = {
                    NodeID: id,
                    Name: this.features[i].Name,
                    Flag: this.features[i].Flag,
                    Desc: this.features[i].Desc,
                    APIs: this.features[i].APIs
                };

                node.Features.Additional.push(feature);
            }
            return node;
        },

        // 分类处理
        handleCatalog() {
            // 弹出框
            // 没选中行的提示
            if (!this.checkCurrentTrDataFn(this.selectedRow)) return;

            if (this.isEdit) {
                // 选中的为平台菜单则不允许修改
                // if (this.selectedRow.key === 'platform_tree') {
                if (this.selectedRow.Index === '0') {
                    this.$warning({
                        title: '操作提示',
                        content: '不能修改根节点',
                        okText: '知道了',
                        maskClosable: false,
                        centered: true
                    });
                    return;
                }
                this.hotCatalog = {
                    key: this.selectedRow.key,
                    Name: this.selectedRow.Name,
                    Icon: this.selectedRow.Icon,
                    Type: this.selectedRow.Type,
                    State: this.selectedRow.State
                };
            } else {
                this.hotCatalog = {};
            }

            this.popCatalogFlag = true;
            this.$Utils.globalDragCenterFn('global-drag-catalog');
        },
        // 分类状态操作
        hotCatalogStatusChange(checked) {
            let checkStatus;
            if (checked) {
                checkStatus = 1;
            } else {
                checkStatus = 0;
            }
            this.hotCatalog.State = checkStatus;
            this.mockTreeChangeStatus(this.platformTree, this.selectedRow.key, checkStatus);
        },
        // 分类更改状态
        mockTreeChangeStatus(root, key, checked) {
            for (let i = 0; i < root.length; i++) {
                if (root[i].children || root[i].children.length > 0) {
                    // 循环加入key值和检查children
                    root[i].children.forEach((n) => {
                        if (n.key === key) {
                            n.State = checked;
                            // 递归
                            this.childrenNodeStatus(n.children, checked);
                        } else {
                            this.mockTreeChangeStatus(n, key, checked);
                        }
                    });
                }
            }
        },
        // 分类确认后子节点状态更改
        childrenNodeStatus(cNode, checked) {
            for (let i = 0; i < cNode.length; i++) {
                if (!cNode[i].children || cNode[i].children.length === 0) {
                    cNode[i].State = checked;
                } else {
                    cNode[i].children.forEach((n) => {
                        n.State = checked;
                        // 递归
                        this.childrenNodeStatus(n.children, checked);
                    });
                }
            }
        },
        // 菜单处理
        async handleMenu() {
            if (!this.checkCurrentTrDataFn(this.selectedRow)) {
                return;
            }
            if (this.isEdit) {
                // 修改菜单
                // 选中的为根节点菜单则不允许修改
                if (this.selectedRow.Index === '0') {
                    this.$warning({
                        title: '操作提示',
                        content: '不能修改根节点',
                        okText: '知道了',
                        maskClosable: false,
                        centered: true
                    });
                    return;
                }
                // 默认选中第一个tab
                this.activeKey = 0;
                await menuGetNode(this.selectedRow.key).then((res) => {
                    // 检查返回
                    if (parseFloat(res.Code || res.code) === 0) {
                        let node = res.data;

                        this.hotNode = {
                            ID: node.ID,
                            Icon: node.Icon,
                            Name: node.Name,
                            State: node.State,
                            URI: node.URI,
                            Type: node.Type
                        };

                        let fs = [];

                        if (node.Features.Internal) {
                            fs.push({
                                ID: node.Features.Internal.ID,
                                Name: '基本',
                                Flag: '',
                                APIs: node.Features.Internal.APIs,
                                Closable: false
                            });
                        } else {
                            fs.push({
                                ID: '',
                                Name: '基本',
                                Flag: '',
                                APIs: [],
                                Closable: false
                            });
                        }

                        if (node.Features.Additional) {
                            node.Features.Additional.forEach((feature) => {
                                fs.push({
                                    ID: feature.ID,
                                    Name: feature.Name,
                                    Flag: feature.Flag,
                                    APIs: feature.APIs,
                                    Closable: true
                                });
                            });
                        }
                        this.features = fs;
                    } else {
                        this.$message.error(res.Msg);
                    }
                });
            } else {
                this.hotNode = {
                    ID: '',
                    Icon: '',
                    Name: '',
                    State: false,
                    URI: '',
                    Type: ''
                };
                this.features = [];
                this.features.push({
                    ID: '',
                    Name: '基本',
                    Flag: '',
                    APIs: [],
                    Closable: false
                });
            }

            this.popMenuFlag = true;
            this.$Utils.globalDragCenterFn('global-drag-menu');
        },
        // 菜单状态操作
        hotMenuStatusChange(checked) {
            let checkStatus;
            if (checked) {
                checkStatus = 1;
            } else {
                checkStatus = 0;
            }
            this.hotMenu.State = checkStatus;
            for (let i = 0; i < this.platformTree.length; i++) {
                this.menuChangeParent(this.platformTree[i], this.selectedRow.key, checkStatus);
            }
        },
        // 菜单向上找根节点
        menuChangeParent(node, key, checked) {
            if (!node.children || node.children.length === 0) {
                return false;
            }
            for (let i = 0; i < node.children.length; i++) {
                if (node.children[i].key === key) {
                    if (!node.State && checked) {
                        node.State = checked;
                    }
                } else {
                    this.menuChangeParent(node.children[i], key, checked);
                }
            }
            return false;
        },
        newFeature() {
            this.popFeatureFlag = true;
            this.$Utils.globalDragCenterFn('global-drag-feature');
        },
        // 增加新的分类
        handleNewCatalog() {
            if (!this.checkCurrentTrDataFn(this.selectedRow)) {
                return;
            }

            this.isEdit = false;
            this.handleCatalog();
        },
        // 增加新的菜单
        handleNewMenu() {
            if (!this.checkCurrentTrDataFn(this.selectedRow)) {
                return;
            }

            this.isEdit = false;
            this.handleMenu();
        },
        // 修改
        handleEdit() {
            if (!this.checkCurrentTrDataFn(this.selectedRow)) {
                return;
            }
            this.isEdit = true;
            if (this.selectedRow.Type === '分类') {
                this.handleCatalog();
            } else {
                this.handleMenu();
            }
        },
        // 检测表格是否有选中的的数据
        checkCurrentTrDataFn(target) {
            if (Object.keys(target).length <= 0) {
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
        // 切换标签
        changeTabsFn() {},
        removeApi(featureIndex, apiListIndex) {
            this.features[featureIndex].APIs.splice(apiListIndex, 1);
        },
        handleRemove() {
            if (!this.checkCurrentTrDataFn(this.selectedRow)) {
                return;
            }
            // 根节点不能删除
            if (this.selectedRow.Index === '0') {
                this.$warning({
                    title: '操作提示',
                    content: '不能删除根节点',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            // 判断是否有children，有则不允许删除
            if (this.selectedRow.children && this.selectedRow.children.length > 0) {
                this.$warning({
                    title: '操作提示',
                    content: '节点下还有子节点，如要删除节点请先删除子节点',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            // 请求后端删除

            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.deleteNode(_this.selectedRow.key);
                    return new Promise((resolve, reject) => {
                        setTimeout(Math.random() > 0.5 ? resolve : reject, 500);
                    }).catch(() => {});
                }
            });
        },
        addFeature() {
            // 检查必填项
            let name = this.checkInputValue(this.hotFeature.Name, '功能名称');
            if (!name) {
                return;
            } else {
                this.hotFeature.Name = name;
            }

            let flag = this.checkInputValue(this.hotFeature.Flag, '功能标识');
            if (!flag) {
                return;
            } else {
                this.hotFeature.Flag = flag;
            }

            let feature = {
                Name: this.hotFeature.Name,
                Flag: this.hotFeature.Flag,
                Closable: true,
                APIs: []
            };
            this.features.push(feature);
            this.popFeatureFlag = false;
        },
        addEmptyApi(featureIndex) {
            // 判断该feature下api是否存在
            if (!this.features[featureIndex].APIs) {
                this.features[featureIndex].APIs = [];
            }
            this.features[featureIndex].APIs.push('');
        },
        onEdit(index, action) {
            switch (action) {
                case 'add':
                    this.newFeature();
                    break;
                case 'remove':
                    this.removeFeature(index);
                    break;
                default:
                    break;
            }
        },
        removeFeature(index) {
            this.features.splice(index, 1);
            this.activeKey = index - 1;
            // for (let i = 0; i < this.features.length; i++) {
            //     if (this.features[i].Flag === flag) {
            //         this.features.splice(i, 1)
            //         return
            //     }
            // }
        },
        // onApiChange (fIndex, aIndex, e) {
        //     console.log(fIndex)
        //     console.log(aIndex)
        //     console.log(e)
        //     this.features[fIndex].apis[aIndex] = e.target.value
        //     console.log(this.features[fIndex].apis[aIndex])
        // },
        // 设置行属性 —— 点击每一行的事件
        customRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.selectedRow = record;
                        // 根据选中的类型，限制按钮是否可用
                        // 分类
                        // 菜单
                        // 修改 & 删除
                    },
                    dblclick: (e) => {
                        if ((e.srcElement || e.target).classList.contains('ant-table-row-expand-icon')) return;
                        this.handleEdit();
                    }
                }
            };
        },
        featureTitle(name, flag) {
            if (flag && flag.length > 0) {
                return name + '(' + flag + ')';
            }
            return name;
        },
        // 表格的高亮样式显示
        rowClassFn(record) {
            if (record.key === this.selectedRow.key) {
                return 'active-tr';
            }
            return '';
        },
        // 手动控制展开行和关闭行
        expandFn(expanded, record) {
            let index = this.expandedRowKeys.indexOf(record.key);
            if (expanded) {
                if (index < 0) {
                    this.expandedRowKeys.push(record.key);
                }
            } else {
                this.expandedRowKeys.splice(index, 1);
            }
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
        notifyEmptyInput(input) {
            this.$warning({
                title: '操作提示',
                content: '请输入' + input,
                okText: '知道了',
                centered: true,
                maskClosable: false
            });
        },
        // 上移Node
        async nodeMoveUp(row) {
            if (!row.Parent) {
                return false;
            }
            let parent = this.findNode(this.platformTree, row.Parent);
            if (!parent) {
                return false;
            }
            let index = 0;
            parent.children.some((n, i) => {
                if (n.key === row.key) {
                    index = i;
                    return true;
                }
            });
            this.laodingFlag = true;
            let ids = {
                ID: row.Parent,
                ChildrenID: row.key
            };
            await moveUpNode(ids).then((res) => {
                if (parseFloat(res.Code || res.code) === 0) {
                    // 成功返回
                    parent.children.splice(
                        index - 1,
                        1,
                        ...parent.children.splice(index, 1, parent.children[index - 1])
                    );
                } else {
                    this.$message.error(res.Msg);
                }
            });
            this.laodingFlag = false;
        },
        // 下移Node
        async nodeMoveDown(row) {
            if (!row.Parent) {
                return false;
            }
            let parent = this.findNode(this.platformTree, row.Parent);
            if (!parent) {
                return false;
            }

            let index = 0;
            parent.children.some((n, i) => {
                if (n.key === row.key) {
                    index = i;
                    return true;
                }
            });
            this.laodingFlag = true;
            let ids = {
                ID: row.Parent,
                ChildrenID: row.key
            };
            await moveDownNode(ids).then((res) => {
                if (parseFloat(res.Code || res.code) === 0) {
                    // 成功返回
                    parent.children.splice(index, 1, ...parent.children.splice(index + 1, 1, parent.children[index]));
                } else {
                    this.$message.error(res.Msg);
                }
            });
            this.laodingFlag = false;
        },
        upAble(row) {
            // 查找父节点
            if (row.Parent) {
                let parent = this.findNode(this.platformTree, row.Parent);
                if (!parent) {
                    return false;
                }
                return parent.children.some((n, index) => {
                    if (n.key === row.key) {
                        if (index > 0) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
            return false;
        },
        downAble(row) {
            // 查找父节点
            if (row.Parent) {
                let parent = this.findNode(this.platformTree, row.Parent);
                if (!parent) {
                    return false;
                }
                return parent.children.some((n, index) => {
                    if (n.key === row.key) {
                        if (index < parent.children.length - 1) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
            return false;
        },
        customizeIcon(icon) {
            if (!icon) {
                return false;
            }

            if (icon.indexOf('yrt-') === -1) {
                return false;
            }
            return true;
        }
    },
    mounted() {
        this.loadAllTree();
        // this.loadPlatformTree()
    },
    computed: {
        newNodeDisable: function () {
            if (this.selectedRow.Type === '分类') {
                return false;
            }
            return true;
        },
        popTitleSuffix: function () {
            if (this.isEdit) {
                return ' - 修改';
            }
            return ' - 新增';
        }
    }
};
</script>

<style lang="less" scoped>
.modal-form-input-scoped {
    .modal-form-input {
        .label {
            width: 52px;
        }
    }
}
.api-input-delete {
    color: #f5222d;
    background-color: #f5f5f5;
    border-color: #d9d9d9;
}
.yrt {
    font-size: 18px;
    padding-right: 6px;
}
</style>
