<template>
    <div class="scoped scoped-white table-select-center">
        <a-tabs @change="tabChange" :activeKey="activeTab" ref="treesTab" style="margin-left: 4px">
            <a-tab-pane v-for="(t, mtIndex) in trees" :key="mtIndex" :forceRender="true">
                <span slot="tab">
                    {{ t.terminalName }}
                    <span style="display: none">
                        <a-icon
                            type="check-circle"
                            @click="commitSubtree(mtIndex)"
                            style="color: Lime; margin-left: 5px"
                        />
                        <a-icon type="stop" @click="cancelChange(mtIndex)" style="color: Crimson" />
                    </span>
                </span>

                <vxe-table
                    border="none"
                    :height="tableHeight"
                    show-overflow
                    ref="treeTable"
                    row-id="ID"
                    :checkbox-config="{ highlight: true, checkAll: false, showHeader: checkAble, checkMethod }"
                    :tree-config="treeConfig"
                    :data="t.node"
                    @select-change="selectChange"
                    @select-all="selectChange"
                >
                    <vxe-table-column type="checkbox" width="150" tree-node></vxe-table-column>
                    <vxe-table-column field="Name" title="名称"> </vxe-table-column>
                    <vxe-table-column title="子权限" width="60">
                        <template v-slot="{ row }">
                            <ul>
                                kkkkkk
                                <!-- <li>
                                    <span>ID：</span>
                                    <span>{{ row.id }}</span>
                                </li>
                                <li>
                                    <span>Name：</span>
                                    <span>{{ row.name }}</span>
                                </li>
                                <li>
                                    <span>Date</span>
                                    <span>{{ row.date }}</span>
                                </li> -->
                            </ul>
                        </template>
                    </vxe-table-column>

                    <vxe-table-column field="Desc" title="备注"></vxe-table-column>
                </vxe-table>
            </a-tab-pane>
        </a-tabs>
    </div>
</template>
<script>
export default {
    name: 'roleTree',
    props: {
        tableHeight: Number, // 表格高度
        checkAble: Boolean, // 是否允许勾选，用于无角色选中时, 同时也控制是否显示全选框
        editAble: Boolean, // 是否允许内部编辑
        trees: [] // 母树列表
    },
    data() {
        return {
            treeConfig: {
                line: true,
                children: 'children',
                iconOpen: 'yrt-table-tree-icon yrt-minus',
                iconClose: 'yrt-table-tree-icon yrt-jia2',
                expandAll: true
            },
            activeTab: 0, // 当前tab index
            currentChanged: false // 当前树有修改
        };
    },
    methods: {
        init() {
            this.editAble = true;
            this.checkAble = false;
            this.currentChanged = false;
            this.trees = [];
            this.activeTab = 0;
        },
        // 组件初始化完毕，不一定Tree数据加载完毕
        // 在数据加载完毕之前设定checked都不成功
        isReady() {
            if (this.$refs.treeTable) {
                return true;
            }
            return false;
        },
        checkMethod({ row }) {
            return this.checkAble;
        },
        selectChange({ selection }) {
            // 不能check状态下不做操作
            if (!this.checkAble) {
                return;
            }

            let tree = this.trees[this.activeTab];
            // 空树不会有任何改变
            if (!tree.node || tree.node.length === 0) {
                return;
            }
            tree.changed = true;
            this.currentChanged = true;
            this.editStatusVisible(this.activeTab, true);
        },
        tabChange(index) {
            if (this.trees[this.activeTab].changed) {
                this.$message.warning('当前权限树已经修改，请先保存或撤销修改');
                return;
            }
            this.activeTab = index;
            this.refresh(index);
        },
        getUpdateSubtree(index) {
            let tree = this.trees[index];
            let updateSubtree = {
                mother: tree.treeKey,
                terminal: tree.terminalKey,
                terminalName: tree.terminalName,
                tree: []
            };
            updateSubtree.tree = this.genCheckedTree(index, tree.node);
            return updateSubtree;
        },
        commitSubtree(index) {
            this.$emit('update', this.getUpdateSubtree(index));
        },
        getCurrentUpdate() {
            return this.getUpdateSubtree(this.activeTab);
        },
        updateCheckedNodes(subtrees) {
            subtrees.forEach((subtree) => {
                // 找到对应的tree和table
                let index = 0;
                this.trees.some((tree) => {
                    if (tree.treeKey === subtree.mother && tree.terminalKey === subtree.terminal) {
                        // 更新checkedNodes
                        this.trees[index].checkedTree = subtree.tree;
                        // this.trees[index].checkedNodes = this.getCheckedArr(subtree.tree)
                        this.refresh(index);
                        // 更改状态
                        this.editStatusVisible(index, false);
                        return true;
                    }
                    index++;
                });
            });
        },
        refresh(index) {
            let table = this.$refs.treeTable[index];
            let tree = this.trees[index];
            if (!table) {
                return;
            }
            table.clearCheckboxRow().then(() => {
                this.setCheckNodes(table, tree.node, tree.checkedTree);
            });
        },
        cancelCurrentChange() {
            this.cancelChange(this.activeTab);
        },
        cancelChange(index) {
            // 恢复原值
            this.refresh(index);
            // 设置值
            this.trees[index].changed = false;
            // 取消显示
            this.editStatusVisible(index, false);
        },
        genCheckedTree(index, children) {
            let checkedNode = [];
            children.forEach((child) => {
                if (child.children && child.children.length > 0) {
                    let checkedChildren = this.genCheckedTree(index, child.children);
                    if (checkedChildren.length > 0) {
                        checkedNode.push({
                            ID: child.ID,
                            children: checkedChildren
                        });
                    }
                } else if (this.$refs.treeTable[index].isCheckedByRow(child)) {
                    checkedNode.push({
                        ID: child.ID
                    });
                }
            });
            return checkedNode;
        },
        setCheckNodes(table, tree, checkedSubtree) {
            checkedSubtree.forEach((node) => {
                tree.forEach((mNode) => {
                    if (mNode.ID === node.ID) {
                        if (node.children && node.children.length > 0) {
                            this.setCheckNodes(table, mNode.children, node.children);
                        } else {
                            table.setCheckboxRow(mNode, true);
                        }
                    }
                });
            });
        },
        getCheckedArr(checkedSubtree) {
            let checkedNodes = [];
            checkedSubtree.forEach((node) => {
                if (node.children && node.children.length > 0) {
                    this.getCheckedArr(node.children);
                } else {
                    checkedNodes.push(node.ID);
                }
            });
            return checkedNodes;
        },
        editStatusVisible(index, visible) {
            let status = 'none';
            let className = '';
            if (visible) {
                status = 'inline';
                className = 'red-point';
            }
            if (this.editAble) {
                this.$refs.treesTab.$children[0].children[
                    index
                ].componentOptions.children[0].children[1].elm.style.display = status;
            } else {
                this.$refs.treesTab.$children[0].children[this.activeTab].componentOptions.children[0].elm.className =
                    className;
            }
            // this.$refs.treeTable[this.activeTab].refreshData()
            this.trees[index].changed = visible; // 对应的树值
            this.currentChanged = visible; // 当前有修改
            this.$emit('changed', visible); // 上报是否有修改
        },
        clearAllChecked() {
            for (let i = 0; i < this.trees.length; i++) {
                this.editStatusVisible(i, false);
                this.trees[i].checkedTree = [];
                this.$refs.treeTable[i].clearCheckboxRow();
                this.$refs.treeTable[i].setAllCheckboxRow(false);
            }
        },
        hasFeatures({ row }) {
            console.log(row);
            // if (row.Feature && row.Feature > 0) {
            //     return true
            // }
            return false;
        }
    },
    mounted() {
        this.editAble = true;
        this.checkAble = false;
        this.clearAllChecked();
    }
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
.red-point {
    position: relative;
}

.red-point::before {
    content: ' ';
    border: 4px solid red; /*设置红色*/
    border-radius: 4px; /*设置圆角*/
    position: absolute;
    right: 0%;
    margin-right: -10px;
    margin-top: -3px;
}
</style>
