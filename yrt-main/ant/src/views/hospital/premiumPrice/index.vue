<template>
    <list-page-layout>
        <template slot="list">
            <div class="toolbar-wrapper">
                <a-button size="small" type="primary" :loading="loading" @click="onRefreshClick">刷新</a-button>
            </div>
            <div class="table-wrapper">
                <vxe-table
                    border
                    show-overflow
                    show-header-overflow
                    show-footer
                    autoResize
                    resizable
                    ref="xTable"
                    size="small"
                    align="center"
                    height="auto"
                    :loading="loading"
                    :data="rules"
                    :mouse-config="{ selected: true }"
                    :checkbox-config="{ range: true }"
                    :keyboard-config="{
                        isArrow: false,
                        isDel: false,
                        isEnter: false,
                        isTab: true,
                        isEdit: true
                    }"
                    :edit-config="{ trigger: 'click', mode: 'row' }"
                    :edit-rules="{
                        'minPrice': [{ min: 0, type: 'number', message: '最小值不能小于0' }],
                        'maxPrice': [{ min: 0, type: 'number', message: '最大值不能小于0' }],
                        'percent': [{ min: 0, type: 'number', message: '溢价比例不能小于0' }]
                    }"
                    @cell-dblclick="({ $event }) => $event.stopPropagation()"
                    @edit-actived="onTableEditActived"
                    @edit-closed="onTableEditClosed"
                >
                    <vxe-table-column v-bind="tableColumnConfig.seq" field="index">
                        <template v-slot:default="{ row, rowIndex }">
                            <span v-if="row.rowMark !== 'addRow'">{{ rowIndex + 1 }}</span>
                        </template>
                    </vxe-table-column>
                    <vxe-table-column v-for="column in columns" v-bind="column" :key="column.field" />
                    <vxe-table-column v-bind="tableColumnConfig.operation">
                        <template v-slot:default="{ row }">
                            <a-button v-if="row.rowMark === 'addRow'" size="small" type="primary" @click="handleAddRule"
                                >新增规则</a-button
                            >
                            <template v-else-if="$refs.xTable.isActiveByRow(row)">
                                <table-row-operator
                                    save
                                    cancel
                                    :payload="row"
                                    @save="handleSaveRule"
                                    @cancel="handleCancelEdit"
                                ></table-row-operator>
                            </template>
                            <template v-else>
                                <table-row-operator
                                    edit
                                    del
                                    :payload="row"
                                    @edit="handleEditRule"
                                    @del="handleDeleteRule"
                                ></table-row-operator>
                            </template>
                        </template>
                    </vxe-table-column>
                </vxe-table>
            </div>
            <div class="pager-wrapper">
                <vxe-pager
                    :loading="loading"
                    :current-page="page"
                    :page-size="pageSize"
                    :total="total"
                    :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                    @page-change="onPagerChange"
                ></vxe-pager>
            </div>
        </template>
    </list-page-layout>
</template>

<script>
import Big from 'big.js';
import ListPageLayout from '@/components/ListPageLayout';
import TableRowOperator from '@/components/TableRowOperator';
import tableColumnConfig from '@/config/tableColumnConfig';
import {
    premiumPriceRuleAddAPI,
    premiumPriceRuleDeleteAPI,
    premiumPriceRuleEditAPI,
    premiumPriceRuleListAPI
} from '@/service/premiumPrice';
import columns from './columns';

export default {
    name: 'PremiumPrice',
    components: {
        ListPageLayout,
        TableRowOperator
    },
    data() {
        return {
            searchValues: {
                grnSn: undefined,
                dateRange: [],
                handlerName: undefined,
                organizationName: undefined,
                productName: undefined,
                status: undefined,
                warehouseInnerSn: undefined
            },
            loading: false,
            page: 1,
            pageSize: 50,
            total: 0,
            rules: [{ rowMark: 'addRow' }],
            ruleEditBackup: undefined,
            ruleSaveLoading: false,
            selectedRecords: []
        };
    },
    computed: {
        tableColumnConfig() {
            return tableColumnConfig;
        },
        columns() {
            return columns;
        }
    },
    mounted() {
        this.loadRuleList(1, this.pageSize);
    },
    methods: {
        onRefreshClick() {
            this.loadRuleList(this.page, this.pageSize);
        },
        onTableEditActived({ row }) {
            this.ruleEditBackup = { ...row };
        },

        onTableEditClosed({ row, rowIndex }) {
            // 表格编辑结束时，若ruleEditBackup存在，根据ruleEditBackup判断是"取消新增"还是"取消编辑"
            if (this.ruleEditBackup) {
                if (this.ruleEditBackup.isNew) {
                    this.rules.splice(rowIndex, 1);
                } else {
                    Object.keys(this.ruleEditBackup).forEach((key) => {
                        if (key === '_XID') {
                            return;
                        }
                        if (Object.prototype.hasOwnProperty.call(this.ruleEditBackup, key)) {
                            row[key] = this.ruleEditBackup[key];
                        }
                    });
                }
                this.ruleEditBackup = undefined;
            }
        },

        onSelectedChange(selected) {
            this.selectedRecords = selected;
        },

        onPagerChange(pagination) {
            this.page = pagination.currentPage;
            this.pageSize = pagination.pageSize;
            this.loadRuleList(this.page, this.pageSize);
        },

        handleAddRule() {
            const length = this.rules.length;
            const preRow = this.rules[length - 2];
            const newRule = { minPrice: undefined, maxPrice: undefined, percent: 0, remake: undefined, isNew: true };
            if (length <= 1) {
                newRule.minPrice = 0.0001;
                this.rules.splice(length - 1, 0, newRule);
            } else {
                if (typeof preRow.maxPrice === 'number' || typeof preRow.maxPrice === 'string') {
                    newRule.minPrice = new Big(preRow.maxPrice).plus(0.0001).toNumber();
                }
                this.rules.splice(length - 1, 0, newRule);
            }
            this.$nextTick(() => {
                this.$refs.xTable.setActiveRow(this.rules[length - 1]);
            });
        },

        handleEditRule(row) {
            this.$refs.xTable.setActiveRow(row);
        },

        handleDeleteRule(row) {
            this.$confirm({
                title: '确定要删除吗？',
                onOk: async () => {
                    await this.deleteRule(row.id);
                    for (let i = 0, l = this.rules.length; i < l; i += 1) {
                        if (this.rules[i].id === row.id) {
                            this.rules.splice(i, 1);
                            this.total -= 1;
                            break;
                        }
                    }
                }
            });
        },

        /**
         * 保存编辑
         */
        async handleSaveRule(row) {
            let data = { ...row };
            if (!this.handleCheckRuleData(data)) {
                return;
            }
            delete data._XID;
            if (data.isNew) {
                delete data.isNew;
                await this.addRule(data);
                delete row.isNew;
            } else {
                await this.editRule(data.id, data);
            }
            this.ruleEditBackup = undefined;
            this.$refs.xTable.clearActived();
        },

        /**
         * 取消编辑
         */
        handleCancelEdit() {
            this.$refs.xTable.clearActived();
        },

        /**
         * 数据校验
         */
        handleCheckRuleData(data) {
            try {
                // 最低价
                if (data.minPrice === null || data.minPrice === undefined || data.minPrice === '') {
                    throw new Error('请填写最低价');
                }
                if (new Big(data.minPrice).lt(0)) {
                    throw new Error('最低价不能为负值');
                }
                // 最高价
                if (data.maxPrice === null || data.maxPrice === undefined || data.maxPrice === '') {
                    throw new Error('请填写最高价');
                }
                if (new Big(data.maxPrice).lt(0)) {
                    throw new Error('最高价不能为负值');
                }
                if (new Big(data.maxPrice).lt(data.minPrice)) {
                    throw new Error('最高价不能低于最低价');
                }
                // 溢价比例
                if (data.percent === null || data.percent === undefined || data.percent === '') {
                    throw new Error('请填写溢价比例');
                }
                if (new Big(data.percent).lt(0)) {
                    throw new Error('溢价比例不能为负值');
                }
            } catch (err) {
                this.$message.error(err.message);
                return false;
            }
            return true;
        },

        async addRule(data) {
            try {
                this.loading = true;
                const { code, msg } = await premiumPriceRuleAddAPI(data);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('新增成功');
                this.loadRuleList(this.page, this.pageSize);
            } catch (err) {
                this.$message.error(err.message || '新增失败');
                throw err;
            } finally {
                this.loading = false;
            }
        },

        async editRule(id, data) {
            try {
                this.loading = true;
                const { code, msg } = await premiumPriceRuleEditAPI(id, data);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('修改成功');
            } catch (err) {
                this.$message.error(err.message || '修改失败');
                throw err;
            } finally {
                this.loading = false;
            }
        },

        /**
         * 删除规则
         * @param {string | number} id 规则id
         */
        async deleteRule(id) {
            try {
                this.loading = true;
                const { code, msg } = await premiumPriceRuleDeleteAPI(id);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('删除成功');
            } catch (err) {
                this.$message.error(err.message || '删除失败');
                throw err;
            } finally {
                this.loading = false;
            }
        },

        async loadRuleList(page, pageSize, searchValues = {}) {
            try {
                this.loading = true;
                const { records, total } = await premiumPriceRuleListAPI(page, pageSize, searchValues);
                if (!records) {
                    this.$message.error('规则加载失败');
                    return;
                }
                this.rules = [...records, this.rules[this.rules.length - 1]];
                this.total = total;
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>

<style lang="less" scoped>
@toolbarHeight: 32px;
@paginationHeight: 48px;
.toolbar-wrapper {
    height: @toolbarHeight;
    text-align: left;
}
.table-wrapper {
    height: calc(100% - @toolbarHeight - @paginationHeight);
}
.pager-wrapper {
    height: @paginationHeight;
}
</style>
