<template>
    <div class="row bg_box">
        <div v-show="!triggerCollapsed" class="chart_count">
            <div class="box box-primary">
                <div class="img_box">
                    <div class="frontImg" :class="{ hide: !chooseFace }">
                        <img style="width: 100%" src="../../../../public/images/pic/gk_face.jpg" />
                        <div v-for="(item, index) of frontArr" :key="index">
                            <div
                                @click="choosePath(item)"
                                class="dots"
                                :class="'f_' + item.key + ' ' + item.act + ' ' + item.rev"
                                >{{ item.key }}</div
                            >
                        </div>
                    </div>
                    <div class="backImg" :class="{ hide: chooseFace }">
                        <img style="width: 100%" src="../../../../public/images/pic/gk_back.jpg" />
                        <div v-for="(item, index) of backArr" :key="index">
                            <div
                                @click="choosePath(item)"
                                class="dots"
                                :class="'b_' + item.key + ' ' + item.act + ' ' + item.rev"
                                >{{ item.key }}</div
                            >
                        </div>
                    </div>
                    <div class="cs_tab">
                        <div class="ico_box" @click="chooseFace = !chooseFace">
                            <a-icon type="sync" />
                        </div>
                        <a class="frontTab" v-if="chooseFace" herf="javascript:;">正面</a>
                        <a class="backTab" v-if="!chooseFace" herf="javascript:;">背面</a>
                    </div>
                </div>
            </div>
        </div>
        <div class="tree_count">
            <ul class="top-caption">
                <li>
                    <div @click="triggerCollapsed = !triggerCollapsed" class="icon">
                        <a-icon class="trigger" :type="triggerCollapsed ? 'menu-unfold' : 'menu-fold'" />
                    </div>
                    <h6 class="title">部位选择</h6>
                </li>
            </ul>
            <div v-if="treeData.length <= 0" class="cd_box position-placeholder">
                <a-spin tip="部位树加载中" />
            </div>
            <div v-else class="cd_box" style="margin-bottom: 23px; overflow: auto">
                <vxe-table
                    ref="xTree"
                    :cell-class-name="cellClassName"
                    :loading="xTreeLoading"
                    :show-header="false"
                    row-key="op_inner_sn"
                    :tree-config="treeConfig"
                    class="operation-path-tree-table"
                    @cell-click="cellClickFn"
                    @cell-mouseenter="cellMouseenterFn"
                    @cell-mouseleave="cellMouseleaveFn"
                    :data="treeData"
                >
                    <vxe-table-column field="name" title="Name" tree-node>
                        <template v-slot="{ row, seq }">
                            <span class="a-icon-wrap">
                                <template v-if="parseInt(row.is_leaf) === 0">
                                    <a-spin v-if="row.checked && !row.isChildrenClick" />
                                    <a-icon v-if="$refs.xTree.isTreeExpandByRow(row)" type="folder-open" />
                                    <a-icon v-else type="folder" />
                                </template>
                                <template v-else>
                                    <a-icon type="file" />
                                </template>
                                <span class="tree-title">
                                    <span>{{ row.name }}</span>
                                    <span v-show="row.hover && systemType !== 2" class="action">
                                        <a-tooltip placement="top">
                                            <template slot="title">
                                                <span>新增子节点</span>
                                            </template>
                                            <a-icon
                                                @click.stop="treeActionFn(row, seq, 'add', $event)"
                                                style="color: #3b881f"
                                                type="plus-circle"
                                            />
                                        </a-tooltip>
                                        <a-tooltip placement="top">
                                            <template slot="title">
                                                <span>编辑</span>
                                            </template>
                                            <a-icon
                                                @click.stop="treeActionFn(row, seq, 'edit', $event)"
                                                style="color: #6aa2e5"
                                                type="form"
                                            />
                                        </a-tooltip>
                                        <a-tooltip
                                            placement="top"
                                            v-if="row.parent_op_inner_sn && parseInt(row.is_leaf) === 1"
                                        >
                                            <template slot="title">
                                                <span>删除</span>
                                            </template>
                                            <a-icon
                                                @click.stop="treeActionFn(row, seq, 'del', $event)"
                                                style="color: #f00"
                                                type="delete"
                                            />
                                        </a-tooltip>
                                    </span>
                                </span>
                            </span>
                        </template>
                    </vxe-table-column>
                </vxe-table>
            </div>
        </div>
        <div class="list_count">
            <ul class="top-caption option-kit-list">
                <li>
                    <h6 class="title title-clear-pl">
                        手术包列表
                        <span :class="{ hide: !isChoose }">-- {{ chooseName }}</span>
                    </h6>
                </li>
                <li @click="openAddKits">
                    <div class="icon">
                        <i class="yrt yrt-tianjia"></i>
                    </div>
                    <h6 class="title">添加手术包</h6>
                </li>
            </ul>
            <div class="placeholder" :class="{ hide: isChoose }">
                <a-icon style="margin-right: 14px" type="select" />请先选择手术径路
            </div>
            <div class="op_count" :class="{ hide: !isChoose }">
                <div class="todo_box form-list-wrap">
                    <ul class="operation-kits-filters">
                        <li v-if="systemType === 2" class="form-list">
                            <label class="label">所属范围</label>
                            <div class="select-wrap" style="width: 80px">
                                <a-select :value="searchScope" allowClear="false" @change="changeScopeOperationKitFn">
                                    <a-select-option
                                        v-for="scopeOperration in scopeOperationKitItem"
                                        :key="scopeOperration.name"
                                        :value="scopeOperration.name"
                                        >{{ scopeOperration.text }}</a-select-option
                                    >
                                </a-select>
                            </div>
                        </li>
                        <li class="form-list">
                            <label class="label">手术类别</label>
                            <div class="select-wrap" style="width: 160px">
                                <a-select :value="kitsType" allowClear="false" @change="changeSel">
                                    <a-select-option value>全部</a-select-option>
                                    <a-select-option value="0">颅骨类</a-select-option>
                                    <a-select-option value="1">脊柱类</a-select-option>
                                    <a-select-option value="2">关节类</a-select-option>
                                    <a-select-option value="3">创伤类</a-select-option>
                                </a-select>
                            </div>
                        </li>
                    </ul>
                    <div v-if="isSelectedNodePack" class="cd_box position-placeholder">
                        <a-spin tip="手术包加载中" />
                    </div>
                    <div v-if="!isSelectedNodePack && nodePackList.length <= 0" class="cd_box position-placeholder"
                        >暂无手术包</div
                    >
                    <div v-else>
                        <a-spin :spinning="operationKitsSpinning">
                            <div class="op_kits_list">
                                <div
                                    v-for="(item, index) of nodePackList"
                                    @click="openLookKits(item)"
                                    :key="index"
                                    class="op_div"
                                    :class="item.is_cust ? 'op_is_cust' : ''"
                                >
                                    <a-tooltip placement="bottomRight">
                                        <template slot="title">
                                            <div>备注: {{ item.description ? item.description : '--' }}</div>
                                        </template>
                                        <div style="height: 100%">
                                            <ul class="op_kit">
                                                <li>
                                                    <div class="brand">{{ item.brand_name }}</div>
                                                    <!-- 品牌： -->
                                                    <div v-if="systemType === 0" class="ico_del">
                                                        <a-tooltip placement="top" title="删除">
                                                            <i
                                                                @click.stop="delKitsOp(item)"
                                                                class="yrt yrt-shanchu-copy cancel del_ico"
                                                            ></i>
                                                        </a-tooltip>
                                                    </div>
                                                    <div v-else-if="systemType === 2 && item.is_cust" class="ico_del">
                                                        <a-tooltip placement="top" title="删除">
                                                            <i
                                                                @click.stop="delKitsOp(item)"
                                                                class="yrt yrt-shanchu-copy cancel del_ico"
                                                            ></i>
                                                        </a-tooltip>
                                                    </div>
                                                </li>
                                                <li>
                                                    <div class="name">{{ item.name }}</div>
                                                    <!-- 名称： -->
                                                </li>
                                                <li>
                                                    <div class="position">{{
                                                        item.type_name ? item.type_name : '--'
                                                    }}</div>
                                                    <!-- 部位 -->
                                                </li>
                                                <li>
                                                    <div class="serial-number">{{
                                                        item.ois_sn ? item.ois_sn : '--'
                                                    }}</div>
                                                    <!-- 编号： -->
                                                </li>
                                            </ul>
                                        </div>
                                    </a-tooltip>
                                </div>
                            </div>
                        </a-spin>
                    </div>
                </div>
            </div>
        </div>
        <a-modal
            title="手术径路 - 修改节点名称"
            :visible="editBox"
            :maskClosable="false"
            :width="300"
            :centered="true"
            @cancel="editBox = false"
            @ok="saveEdit"
            class="modal-form-input-scoped global-drag edit_md"
        >
            <div v-globalDrag="{ el: 'edit_md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    手术径路 - 修改节点名称
                </h6>
            </div>
            <div class="edit_name_box">
                <div class="label">名称</div>
                <a-input class="val" type="text" v-model="editPathObj.name"></a-input>
            </div>
        </a-modal>
        <a-modal
            title="添加"
            :visible="addMd.alert"
            :width="790"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="addMd.alert = false"
            class="modal-form-input-scoped global-drag add-op-md"
        >
            <div v-globalDrag="{ el: 'add-op-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    {{ addMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="addMd.alert = false">取消</a-button>
                <a-button @click="getAddKits" type="primary">添加</a-button>
            </template>
            <add-opkits :editPathObj="editPathObj" ref="addOpMd" @done="addKitsFn"></add-opkits>
        </a-modal>
        <a-modal
            title="手术包详情"
            :visible="editMd.alert"
            :width="960"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="editMd.alert = false"
            class="modal-form-input-scoped global-drag look-op-md"
        >
            <div v-globalDrag="{ el: 'look-op-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    {{ editMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="editMd.alert = false">关闭</a-button>
            </template>
            <look-op-kits ref="editOpMd" :editObj="editObj"></look-op-kits>
        </a-modal>
    </div>
</template>

<script>
import {
    surgeryPathTopAPI,
    surgeryPathAddAPI,
    surgeryPathEditAPI,
    surgeryPathChildAPI,
    surgeryPathDelAPI,
    surgeryPathUnrelpackageAPI,
    surgeryPathInfoAPI,
    surgeryCustDealerPackageListNoPaginationAPI,
    surgeryCustDictSearchScopeAPI,
    surgeryCustDealerPackageRemoveFromPathAPI
} from '@/service/pageAjax';
import './module/op-point.less';
import './module/op-tree.css';
import addOpkits from './module/addOpKits';
import lookOpKits from './module/lookOpKits';

export default {
    name: 'operationPath',
    components: {
        lookOpKits,
        addOpkits
    },
    data() {
        return {
            operationKitsSpinning: false,
            isSelectedNodePack: true,
            lang: 'zh',
            slider: 0,
            in_sp_name: '',
            selStatus: '',
            listData: [],
            scopeOperationKitItem: [],
            selectItem: [
                {
                    label: '关节手术',
                    value: '1'
                },
                {
                    label: '创伤手术',
                    value: '2'
                },
                {
                    label: '脊椎手术',
                    value: '3'
                }
            ],
            nodePackList: [], // 节点手术包
            chooseFace: true,
            editBox: false,
            opDetail: false,
            editParent: {},
            isChoose: false, // 判断是否选中
            editPathObj: {
                name: '',
                op_inner_sn: '',
                parent_op_inner_sn: ''
            },
            oldEditPathObj: {},
            pathName: '',
            chooseName: '', // 选中名称
            chooseId: '',
            dataObj: {
                otherParam: 'zTreeAsyncTest'
            },
            typeName: ['颅骨', '脊柱', '关节', '创伤'],
            nowNode: {},
            treeData: [],
            addMd: {
                title: `${this.$route.meta.title} - 添加手术包`,
                alert: false,
                loading: false
            },
            editMd: {
                title: `${this.$route.meta.title} - 手术包`,
                alert: false,
                loading: false
            },
            frontArr: [
                { key: 1, act: false },
                { key: 2, act: false },
                { key: 4, act: false },
                { key: 6, act: false },
                { key: 7, act: false },
                { key: 8, act: false },
                { key: 10, act: false },
                { key: 11, act: false },
                { key: 12, act: false },
                { key: 13, act: false },
                { key: 14, act: false },
                { key: 15, act: false },
                { key: 16, act: false },
                { key: 17, act: false },
                { key: 18, act: false },
                { key: 20, act: false },
                { key: 21, act: false },
                { key: 22, act: false },
                { key: 23, act: false },
                { key: 8, act: false, rev: 'rev' },
                { key: 10, act: false, rev: 'rev' },
                { key: 11, act: false, rev: 'rev' },
                { key: 12, act: false, rev: 'rev' },
                { key: 13, act: false, rev: 'rev' },
                { key: 14, act: false, rev: 'rev' },
                { key: 15, act: false, rev: 'rev' },
                { key: 16, act: false, rev: 'rev' },
                { key: 17, act: false, rev: 'rev' },
                { key: 18, act: false, rev: 'rev' },
                { key: 20, act: false, rev: 'rev' },
                { key: 21, act: false, rev: 'rev' },
                { key: 22, act: false, rev: 'rev' },
                { key: 23, act: false, rev: 'rev' }
            ],
            backArr: [
                { key: 1, act: false },
                { key: 2, act: false },
                { key: 3, act: false },
                { key: 4, act: false },
                { key: 5, act: false },
                { key: 6, act: false },
                { key: 9, act: false },
                { key: 10, act: false },
                { key: 11, act: false },
                { key: 12, act: false },
                { key: 13, act: false },
                { key: 14, act: false },
                { key: 15, act: false },
                { key: 16, act: false },
                { key: 17, act: false },
                { key: 19, act: false },
                { key: 20, act: false },
                { key: 21, act: false },
                { key: 22, act: false },
                { key: 5, act: false, rev: 'rev' },
                { key: 9, act: false, rev: 'rev' },
                { key: 10, act: false, rev: 'rev' },
                { key: 11, act: false, rev: 'rev' },
                { key: 12, act: false, rev: 'rev' },
                { key: 13, act: false, rev: 'rev' },
                { key: 14, act: false, rev: 'rev' },
                { key: 15, act: false, rev: 'rev' },
                { key: 16, act: false, rev: 'rev' },
                { key: 17, act: false, rev: 'rev' },
                { key: 19, act: false, rev: 'rev' },
                { key: 20, act: false, rev: 'rev' },
                { key: 21, act: false, rev: 'rev' },
                { key: 22, act: false, rev: 'rev' }
            ],
            searchScope: '',
            kitsType: '',
            triggerCollapsed: false,
            treeConfig: {
                line: true,
                children: 'children',
                trigger: 'cell',
                iconOpen: 'yrt-operation-path-tree-icon yrt-minus',
                iconClose: 'yrt-operation-path-tree-icon yrt-jia2'
            },
            selectRow: {},
            xTreeLoading: false
        };
    },
    methods: {
        cellMouseenterFn({ row }) {
            row.hover = true;
        },
        cellMouseleaveFn({ row }) {
            row.hover = false;
        },
        cellClassName({ row }) {
            if (row === this.selectRow) {
                return 'active-tr';
            }
        },
        cellClickFn(data, action) {
            const row = data.row || data;
            this.chooseName = row.name || '';
            this.loadOperationListFn(row);
            this.selectRow = row;
            this.editPathObj = { ...this.selectRow };
            row.checked = !row.checked;
            if (action !== 'isClickImage') {
                // 点击右侧部位Tree
                this.chooseNodeFn(row);
            }
            if (row.is_leaf === 1) return;
            if (row.checked && !row.isChildrenClick) {
                this.getTreeChildDataFn(row, action);
            }
        },
        setItemTreeDataFn(datas, key, value) {
            for (let i = 0; i < datas.length; i++) {
                const item = datas[i];
                if (item.op_inner_sn === this.selectRow.op_inner_sn) {
                    if (key === 'children') {
                        item[key].push(value);
                    } else {
                        item[key] = value;
                    }
                    break;
                }
                if (item.root_op_inner_sn === this.selectRow.root_op_inner_sn) {
                    this.setItemTreeDataFn(item.children, key, value);
                }
            }
        },
        treeActionFn(row, seq, actionTypeName, e) {
            e.stopPropagation(); // 禁止触发上层事件
            this.selectRow = row;

            const actionCallBackObj = {
                add: () => {
                    const isLeaf = parseInt(row.is_leaf);
                    if (isLeaf === 1 || (isLeaf === 0 && row.isChildrenClick)) {
                        this.addNode(row);
                    } else {
                        this.getTreeChildDataFn(row, 'add', e);
                    }
                },
                edit: () => {
                    this.editNode(row);
                },
                del: () => {
                    const _this = this;
                    _this.$confirm({
                        title: '删除',
                        content: '确认要执行该操作吗？',
                        centered: true,
                        maskClosable: false,
                        onOk() {
                            _this.delNode(row);
                        }
                    });
                }
            };
            actionCallBackObj[actionTypeName]();
        },
        choosePath(obj) {
            // 选中关节图
            let node;
            let index = obj.key;
            this.treeData.forEach((item) => {
                if (item.op_inner_sn === index) {
                    node = item;
                }
            });
            if (node) {
                this.selNode(node);
            }
        },
        chooseNodeFn(obj) {
            const rootOpInnerSn = obj.root_op_inner_sn || obj.op_inner_sn;

            // 选中渲染
            let toBack = false;
            this.frontArr.forEach((item) => {
                if (item.key === rootOpInnerSn) {
                    item.act = 'active';
                    toBack = true;
                } else {
                    item.act = false;
                }
            });
            if (toBack === false) {
                this.chooseFace = false;
            }
            let toFront = false;
            this.backArr.forEach((item) => {
                if (item.key === rootOpInnerSn) {
                    item.act = 'active';
                    toFront = true;
                } else {
                    item.act = false;
                }
            });
            if (toFront === false) {
                this.chooseFace = true;
            }
        },
        async selNode(node, parent) {
            // 异步加载节点
            this.editPathObj.name = node.name;
            this.editPathObj.op_inner_sn = node.op_inner_sn;
            this.editPathObj.parent_op_inner_sn = node.parent_op_inner_sn;
            let lv;
            if (!parent) {
                // 设置层级
                lv = 0;
                this.chooseNodeFn(node);
                this.$set(node, 'lv', lv);
            } else {
                lv = parent.lv + 1;
                this.chooseNodeFn(parent);
            }
            this.cellClickFn(node, 'isClickImage');
            if (!node.isParent) {
                this.isChoose = true;
                this.chooseName = node.name;
                this.chooseId = node.op_inner_sn;
                this.loadOperationListFn(node);
                this.selStatus = node.op_inner_sn;
            }
        },
        loadOperationListFn(row) {
            if (parseInt(row.is_leaf) === 0) {
                this.nodePackList = [];
                return;
            }
            if (Object.keys(this.selectRow).length < 0 || row.op_inner_sn !== this.selectRow.op_inner_sn) {
                this.systemType === 2
                    ? this.surgeryCustDealerPackageListNoPaginationAPIFn(row.op_inner_sn)
                    : this.loadNode(row.op_inner_sn);
            }
        },
        async surgeryCustDictSearchScopeAPIFn() {
            const res = await surgeryCustDictSearchScopeAPI();
            if (res.code === 0) {
                this.scopeOperationKitItem = res.list;
                if (this.scopeOperationKitItem && this.scopeOperationKitItem.length > 0) {
                    this.searchScope = this.scopeOperationKitItem[0]['name'];
                }
            }
        },
        async surgeryCustDealerPackageListNoPaginationAPIFn(pid) {
            // 加载节点
            this.isChoose = true;
            this.isSelectedNodePack = true;
            let params = {
                brands: [],
                ois_sn: '',
                op_inner_sn: pid,
                q: '',
                searchScope: this.searchScope,
                types: this.kitsType ? [this.kitsType] : []
            };
            await surgeryCustDealerPackageListNoPaginationAPI(this.$cookie.get('userbelong'), params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.nodePackList = res.rows;
                        // if (res.info.topParent) {
                        //     this.chooseNodeFn(res.info.topParent)
                        // }
                    }
                })
                .finally(() => {
                    this.isSelectedNodePack = false;
                });
        },
        async loadNode(pid) {
            // 加载节点
            this.isChoose = true;
            this.isSelectedNodePack = true;
            let params = {
                pid: pid
            };
            if (this.kitsType) {
                params.type_inner_sn = this.kitsType;
            }
            await surgeryPathInfoAPI(pid, { params })
                .then((res) => {
                    if (res.code === 0) {
                        this.nodePackList = res.info.packages;
                        if (res.info.topParent) {
                            this.chooseNodeFn(res.info.topParent);
                        }
                    }
                })
                .finally(() => {
                    this.isSelectedNodePack = false;
                });
        },
        async addNode(node) {
            if (node.packages) {
                this.$warning({
                    title: '操作提示',
                    content: '已有手术包的根节点下不能添加子节点',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            this.xTreeLoading = true;
            let lv;
            if (!node.lv) {
                // 设置层级
                lv = 1;
            } else {
                lv = node.lv + 1;
            }

            await surgeryPathAddAPI({
                parent_op_inner_sn: node.op_inner_sn,
                name: '新节点'
            })
                .then((res) => {
                    if (parseInt(res.code) === 0) {
                        node.isParent = true;
                        node.is_leaf = 0;
                        node.isChildrenClick = true;
                        const { info } = res,
                            newNode = {
                                ...info,
                                lv: lv,
                                expanded: false,
                                edit: true,
                                async: true,
                                canAdd: true,
                                checked: false,
                                icon: null,
                                isParent: false,
                                name: '新节点',
                                nocheck: false,
                                title: '新节点',
                                st_id: '',
                                root_op_inner_sn: node.root_op_inner_sn,
                                hover: false,
                                isChildrenClick: false,
                                children: []
                            };
                        this.setItemTreeDataFn(this.treeData, 'children', newNode);
                        const xTree = this.$refs.xTree;
                        if (!xTree.isTreeExpandByRow(node)) {
                            xTree.setTreeExpansion(node, true);
                        }
                        this.cellClickFn(node.children[node.children.length > 0 ? node.children.length - 1 : 0]);
                    }
                })
                .finally(() => {
                    this.xTreeLoading = false;
                });
        },
        delFn(node, parent) {
            // 删除
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.delNode(node, parent);
                }
            });
        },
        async delNode(node) {
            this.xTreeLoading = true;
            await surgeryPathDelAPI(node.op_inner_sn)
                .then((res) => {
                    if (res.code === 0) {
                        let matchObj = this.$XEUtils.findTree(this.treeData, (item) => item === node, this.treeConfig);
                        if (matchObj) {
                            // 从树节点中移除
                            matchObj.items.splice(matchObj.index, 1);
                            if (matchObj.items.length <= 0) {
                                matchObj.parent.is_leaf = 1;
                            }
                            this.nodePackList = [];
                            for (let i = 0; i < this.treeData.length; i++) {
                                const item = this.treeData[i];
                                if (item.op_inner_sn === node.root_op_inner_sn) {
                                    this.cellClickFn(item);
                                }
                            }
                        }
                    }
                })
                .finally(() => {
                    this.xTreeLoading = false;
                });
        },
        editNode(node, parent) {
            this.$Utils.globalDragCenterFn('edit_md');
            this.editBox = true;
            this.editPathObj.name = node.name;
            this.editPathObj.op_inner_sn = node.op_inner_sn;
            this.editPathObj.parent_op_inner_sn = node.parent_op_inner_sn;
            this.editParent = parent || '';
            this.oldEditPathObj = {
                name: node.name,
                op_inner_sn: node.op_inner_sn,
                parent_op_inner_sn: node.parent_op_inner_sn
            };
        },
        async saveEdit() {
            this.xTreeLoading = true;
            let newObj = this.editPathObj;
            let oldObj = this.oldEditPathObj;
            let dataObj = {};
            for (let server in newObj) {
                let serverItem = newObj[server];
                for (let old in oldObj) {
                    let oldItem = oldObj[old];
                    if (server === old && serverItem !== oldItem) {
                        dataObj = {
                            ...dataObj,
                            [server]: serverItem, // 新值
                            [server + '_old']: oldItem // 旧值
                        };
                    }
                }
            }
            if (dataObj.name || dataObj.name === '') {
                dataObj.name = this.$Utils.trim(dataObj.name);
                if (dataObj.name === '' || !dataObj.name) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入名称',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }
            }
            dataObj.op_inner_sn = newObj.op_inner_sn;
            await surgeryPathEditAPI(dataObj)
                .then((res) => {
                    if (res.code === 0) {
                        this.setItemTreeDataFn(this.treeData, 'name', dataObj.name);
                        this.editBox = false;
                    } else {
                        this.$warning({
                            title: '操作提示',
                            content: res.msg.toString(),
                            okText: '知道了',
                            centered: true,
                            mask: false,
                            maskClosable: false
                        });
                    }
                })
                .finally(() => {
                    this.xTreeLoading = false;
                });
        },
        async getTreeData() {
            await surgeryPathTopAPI().then((res) => {
                if (res.code === 0) {
                    res.list.forEach((item) => {
                        item.async = true;
                        item.edit = false;
                        item.canAdd = true;
                        item.checked = false;
                        item.root_op_inner_sn = item.op_inner_sn; // 根级id
                        item.hover = false; // 显示后面的小按钮【新增/修改/删除】
                        item.isChildrenClick = false; // 是否点击过加号【默认没有点击过】
                        if (item.is_leaf === 1) {
                            item.isParent = false;
                            item.children = [];
                        }
                        if (item.is_leaf === 0) {
                            item.isParent = true;
                            item.children = [{}];
                        }
                    });
                    this.treeData = res.list;
                }
            });
        },

        async getTreeChildDataFn(row, action, e) {
            this.xTreeLoading = true;
            await surgeryPathChildAPI(row.op_inner_sn)
                .then((res) => {
                    if (res.code === 0) {
                        let data = res.list;
                        data.forEach((item) => {
                            item.async = true;
                            item.edit = false;
                            item.canAdd = true;
                            item.checked = false;
                            item.root_op_inner_sn = row.root_op_inner_sn; // 根级id
                            item.hover = false; // 显示后面的小按钮【新增/修改/删除】
                            item.isChildrenClick = false; // 是否点击过加号【默认没有点击过】
                            if (item.is_leaf === 1) {
                                item.isParent = false;
                                item.children = [];
                            }
                            if (item.is_leaf === 0) {
                                item.isParent = true;
                                item.children = [{}];
                            }
                        });
                        row.isChildrenClick = true;
                        row.children = data;
                        if (action === 'isClickImage') {
                            // 点击左侧的人体骨骼图
                            this.$refs.xTree.toggleTreeExpansion(row);
                            return;
                        }
                        if (action === 'add') {
                            // 插入节点名称
                            this.treeActionFn(row, null, action, e);
                        }
                    }
                })
                .catch(() => {
                    row.isChildrenClick = false;
                })
                .finally(() => {
                    this.xTreeLoading = false;
                });
        },
        async getTreeChildData(pid) {
            let res = await surgeryPathChildAPI(pid);
            if (res.code === 0) {
                let data = res.list;
                data.forEach((item) => {
                    item.async = true;
                    item.edit = false;
                    item.canAdd = true;
                    item.checked = false;
                    item.children = [];
                    if (item.is_leaf === 1) {
                        item.isParent = false;
                    }
                    if (item.is_leaf === 0) {
                        item.isParent = true;
                    }
                });
                return data;
            }
        },
        changeScopeOperationKitFn(val) {
            this.searchScope = val;
            this.surgeryCustDealerPackageListNoPaginationAPIFn(this.selectRow.op_inner_sn);
        },
        changeSel(val) {
            this.kitsType = val;
            this.systemType === 2
                ? this.surgeryCustDealerPackageListNoPaginationAPIFn(this.selectRow.op_inner_sn)
                : this.loadNode(this.selectRow.op_inner_sn);
        },
        openAddKits() {
            if (!this.selectRow.op_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择手术径路',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return;
            }
            if (parseInt(this.selectRow.is_leaf) === 0) {
                this.$warning({
                    title: '操作提示',
                    content: '该节点存在子节点，不能在该节点下添加手术包',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return;
            }
            this.addMd.alert = true;
            this.$Utils.globalDragCenterFn('add-op-md');
            if (this.$refs.addOpMd) {
                this.$refs.addOpMd.pageDataInit();
            }
        },
        addKitsFn() {
            this.addMd.alert = false;
            this.systemType === 2
                ? this.surgeryCustDealerPackageListNoPaginationAPIFn(this.editPathObj.op_inner_sn)
                : this.loadNode(this.editPathObj.op_inner_sn);
        },
        getAddKits() {
            // 添加手术包
            this.systemType === 2
                ? this.$refs.addOpMd.surgeryCustDealerPackageAddToPathAPIFn()
                : this.$refs.addOpMd.addKitsFn();
        },
        openLookKits(obj) {
            this.editObj = obj;
            this.$Utils.globalDragCenterFn('look-op-md');
            this.editMd.alert = true;
            this.$nextTick(() => {
                if (this.$refs.editOpMd) {
                    this.$refs.editOpMd.initPageFn(this.editObj);
                }
            });
        },
        delKitsOp(item) {
            // 删除关联手术包
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.systemType === 2
                        ? _this.surgeryCustDealerPackageRemoveFromPathAPIFn(item)
                        : _this.delKits(item.ois_inner_sn);
                }
            });
        },
        async surgeryCustDealerPackageRemoveFromPathAPIFn(curItem) {
            this.operationKitsSpinning = true;
            const id = curItem.id;
            await surgeryCustDealerPackageRemoveFromPathAPI(this.editPathObj.op_inner_sn, [id])
                .then((res) => {
                    if (res.code === 0) {
                        for (let i = 0; i < this.nodePackList.length; i++) {
                            if (this.nodePackList[i].id === id) {
                                this.nodePackList.splice(i, 1);
                                break;
                            }
                        }
                    }
                })
                .finally(() => {
                    this.operationKitsSpinning = false;
                });
        },
        async delKits(id) {
            this.operationKitsSpinning = true;
            await surgeryPathUnrelpackageAPI({
                op_inner_sn: this.editPathObj.op_inner_sn,
                packages: id
            })
                .then((res) => {
                    if (res.code === 0) {
                        for (let i = 0; i < this.nodePackList.length; i++) {
                            if (this.nodePackList[i].ois_inner_sn === id) {
                                this.nodePackList.splice(i, 1);
                                break;
                            }
                        }
                    }
                })
                .finally(() => {
                    this.operationKitsSpinning = false;
                });
        },
        cancelFn() {
            this.editBox = false;
            this.pathName = '';
        }
    },
    computed: {
        systemType() {
            // 0-平台、1-医院 、2-经销商、3-厂商
            return parseFloat(this.$cookie.get('userSystemType'));
        }
    },
    created() {
        this.getTreeData();
        this.surgeryCustDictSearchScopeAPIFn();
    }
};
</script>

<style scoped lang="less">
.op_kits_list {
    display: flex;
    min-width: 258px;
    flex-wrap: wrap;
    text-align: left;
    padding: 0 6px 18px;
    cursor: pointer;

    .op_div {
        display: flex;
        flex-direction: column;
        width: 240px;
        height: 127px;
        margin: 0 0 18px 10px;
        background-repeat: no-repeat;
        background-size: cover;
        background-image: url('../../../../public/images/pic/operation-kits-platform.png');

        &.op_is_cust {
            background-image: url('../../../../public/images/pic/operation-kits-private.png');
        }
    }
    .op_kit {
        position: relative;
        display: flex;
        flex-direction: column;
        padding: 11px 0 8px;
        margin-bottom: 0;

        li {
            &:nth-of-type(1) {
                display: flex;
                justify-content: space-between;
                align-items: center;
                font-size: 14px;
                padding: 0 16px 0 18px;
                color: #565656;

                .brand {
                    width: 100px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    font-weight: 600;
                }
            }
            &:nth-of-type(2) {
                display: flex;
                justify-content: center;
                align-items: center;
                font-size: 16px;
                font-weight: 700;
                padding: 11px 0 8px;
                color: #565656;

                .name {
                    width: 160px;
                    height: 45px;
                    text-align: center;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    // display: -webkit-box;
                    -webkit-box-orient: vertical;
                    -webkit-box-orient: vertical;
                    -webkit-line-clamp: 2;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
            }
            &:nth-of-type(3) {
                position: absolute;
                right: 24px;
                bottom: -5px;
                font-weight: 600;
            }
            &:nth-of-type(4) {
                position: absolute;
                right: 24px;
                bottom: -22px;
            }
            &:nth-of-type(3),
            &:nth-of-type(4) {
                display: flex;
                justify-content: flex-end;
                align-items: center;
                font-size: 14px;
                color: #565656;
            }
        }

        .ico_box {
            position: absolute;
            right: 18px;
            top: 35px;
            width: 44px;
            img {
                width: 100%;
            }
        }
        .ico_add {
            display: none;
            position: absolute;
            left: 0%;
            top: 0;
            font-size: 30px;
            width: 100%;
            text-align: center;
            color: #fff;
            background: #000;
            height: 100%;
            line-height: 87px;
            opacity: 0.35;
        }
        .ico_del {
            cursor: pointer;
            display: none;
            position: absolute;
            right: 1px;
            top: 9px;
            font-size: 18px;
            text-align: center;

            .del_ico {
                color: red;
                font-size: 18px;
            }
        }
    }
    .op_kit:hover {
        .ico_del {
            display: inline-block;
        }
    }
}
.edit_name_box {
    .label {
        display: inline-block;
        width: 40px;
    }
    .val {
        display: inline-block;
        width: 212px;
    }
}
.bg_box {
    display: flex;
    flex-direction: row;
    height: 100vh;
    background: #fff;
    padding: 8px 0 7px 6px;
}
.chart_count {
    display: flex;
    width: 307px;
    position: relative;
    margin-right: 8px;
}
.tree_count {
    display: flex;
    flex-direction: column;
    width: 26%;
    margin-right: 8px;
    overflow-x: auto;
}
.cd_box {
    height: 89vh;
}
.cd_box,
.list_count {
    border: 1px solid #eceef0;
    overflow: hidden;
}
.list_count {
    display: flex;
    flex-direction: column;
    flex: 1;
    height: 93.2vh;

    .tit_box {
        text-align: left;
        font-size: 16px;
        padding: 10px;
        border-bottom: 1px solid #d4d8df;
    }
    .placeholder {
        text-align: center;
        padding-left: 20px;
        padding-top: 40px;
        font-size: 38px;
        color: #b8b8b8;
    }
    .op_count {
        height: 100%;
        overflow: auto;

        .todo_box {
            .form-list {
                display: flex;
                align-items: center;
                text-align: left;
                label {
                    margin-right: 12px;
                    font-size: 14px;
                    color: #7b8ba0;
                }
            }
        }
    }
}
.tree_box {
    padding: 10px 0 28px 20px;
    overflow-y: auto;
}
.operation-kits-filters {
    display: flex;
    flex-direction: row;
    padding: 10px 0 17px 16px;
    margin-bottom: 0;

    .form-list {
        display: flex;
        flex-direction: row;
        &:nth-of-type(2n) {
            padding-left: 33px;
        }
    }
}

.position-placeholder {
    padding-top: 30px;
    text-align: center;
    font-size: 14px;
    color: #b8b8b8;
}

.row-wrap {
    display: inline-block;
    min-height: 24px;

    .name {
        display: inline-block;
        font-size: 14px;
        color: #64758b;

        span {
            margin: 0 4px;
        }

        .icon {
            margin-right: 8px;
        }
    }

    .icon-wrap {
        display: none;
        padding-left: 0;
        margin-bottom: 0;

        .ant-btn {
            width: 24px;
            height: 24px;
            margin: 0 2px;
        }
    }

    &:hover .icon-wrap {
        display: inline-block;
    }
}

.tree-node-icon {
    width: 20px;
}

.yrt-table-tree-icon {
    font-family: 'yrt' !important;
    font-style: normal;
    color: #778ca2;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
}

.a-icon-wrap i {
    margin: 0 4px;
}

.tree-title {
    color: #64758b;

    .action {
        margin-left: 15px;
    }
}
</style>
