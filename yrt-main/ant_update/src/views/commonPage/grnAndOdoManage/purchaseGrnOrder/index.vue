<template>
    <list-page-layout>
        <template slot="header">
            <list-page-header
                needMixedSearch
                :operationGroups="operationGroups"
                :buttonsStatu="headerButtonsStatu"
                @btnClick="onHeaderButtonClick"
                @search="onHeaderMultipleSearch"
            >
                <template slot="singleSearcher">
                    <list-page-header-single-searcher
                        slot="singleSearcher"
                        placeholder="单据编号"
                        v-model="searchValues.grnSn"
                        @search="onHeaderSingleSearch"
                    ></list-page-header-single-searcher>
                </template>
                <template slot="searchFormItems">
                    <list-page-header-input label="单据编号" v-model="searchValues.grnSn"></list-page-header-input>
                    <list-page-header-input
                        label="供货单位"
                        v-model="searchValues.organizationName"
                    ></list-page-header-input>
                    <list-page-header-input
                        label="产品名称"
                        v-model="searchValues.productName"
                    ></list-page-header-input>
                    <list-page-header-select
                        label="仓库"
                        allowClear
                        v-model="searchValues.warehouseInnerSn"
                        :options="warehouseOptions"
                    ></list-page-header-select>
                    <list-page-header-input label="经手人" v-model="searchValues.handlerName"></list-page-header-input>
                    <list-page-header-select
                        label="状态"
                        allowClear
                        v-model="searchValues.status"
                        :options="statusOptions"
                    ></list-page-header-select>
                    <list-page-header-date-range
                        label="日期"
                        v-model="searchValues.dateRange"
                    ></list-page-header-date-range>
                </template>
            </list-page-header>
        </template>
        <template slot="list">
            <purchase-grn-order-list
                selectType="radio"
                ref="listTable"
                :reloadWhenSearch="false"
                :searchValues="searchValues"
                :rowClassName="tableRowClassName"
                @selectedChange="onSelectedChange"
                @rowDoubleClick="onRowDoubleClick"
            ></purchase-grn-order-list>
        </template>
        <template slot="modals">
            <purchase-grn-order-editor
                :visible="modalsVisible[modalsKeyMap.ORDER_EDITOR]"
                :targetOrder="orderEditorTarget"
                :targetOrderIsCopy="orderEditorTargetIsCopy"
                @close="onOrderEditorClose"
                @orderAdd="onOrderAdd"
                @orderChange="onOrderChange"
                @redFinish="onRedFinish"
            ></purchase-grn-order-editor>
        </template>
    </list-page-layout>
</template>

<script>
import ListPageLayout from '@/components/ListPageLayout';
import ListPageHeader from '@/components/ListPageHeader';
import ListPageHeaderSingleSearcher from '@/components/ListPageHeaderSingleSearcher';
import ListPageHeaderInput from '@/components/ListPageHeaderInput';
import ListPageHeaderSelect from '@/components/ListPageHeaderSelect';
import ListPageHeaderDateRange from '@/components/ListPageHeaderDateRange';
import modalsMixin from '@/mixins/modals';
import PurchaseGrnOrderList from './PurchaseGrnOrderList';
import PurchaseGrnOrderEditor from './PurchaseGrnOrderEditor';
import { grnPutinAPI, grnRedDashedAPI, grnUnreviewAPI, purchaseGrnDelAPI } from '@/service/pageAjax';
import { purchaseGrnOrderStatusMap } from './type';

const modalsKeyMap = Object.freeze({
    ORDER_EDITOR: 'o'
});

const buttonsMap = {
    add: { key: 'add', text: '调单入库', type: 'primary' },
    edit: { key: 'edit', text: '修改', type: 'primary' },
    approval: { key: 'approval', text: '审核', type: 'primary' },
    unapproval: { key: 'unapproval', text: '取消审核', type: 'primary' },
    grn: { key: 'grn', text: '入库', type: 'primary' },
    copy: { key: 'copy', text: '复制', type: 'primary' },
    red: { key: 'red', text: '生成红冲', type: 'danger' },
    delete: { key: 'delete', text: '删除', type: 'danger' }
};

export default {
    name: 'PurchaseGrnOrder',
    components: {
        ListPageLayout,
        ListPageHeader,
        ListPageHeaderSingleSearcher,
        ListPageHeaderInput,
        ListPageHeaderSelect,
        ListPageHeaderDateRange,
        PurchaseGrnOrderList,
        PurchaseGrnOrderEditor
    },
    mixins: [modalsMixin],
    data() {
        return {
            modalsKeyMap,
            buttonsLoading: {
                [buttonsMap.grn.key]: false,
                [buttonsMap.red.key]: false,
                [buttonsMap.delete.key]: false,
                [buttonsMap.unapproval.key]: false
            },
            searchValues: {
                grnSn: undefined,
                dateRange: [],
                handlerName: undefined,
                organizationName: undefined,
                productName: undefined,
                status: undefined,
                warehouseInnerSn: undefined
            },
            selectedRecords: [],
            orderEditorTarget: null, // 收货单编辑目标
            orderEditorTargetIsCopy: false
        };
    },
    computed: {
        operationGroups() {
            return [
                [
                    buttonsMap.add,
                    buttonsMap.edit,
                    buttonsMap.approval,
                    buttonsMap.unapproval,
                    buttonsMap.grn,
                    buttonsMap.copy
                ],
                [buttonsMap.red, buttonsMap.delete]
            ];
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false },
                edit: { disabled: false, reason: '' },
                approval: { disabled: false, reason: '' },
                unapproval: { loading: this.buttonsLoading[buttonsMap.unapproval.key], disabled: false, reason: '' },
                grn: { loading: this.buttonsLoading[buttonsMap.grn.key], disabled: false, reason: '' },
                copy: { disabled: false, reason: '' },
                red: { loading: this.buttonsLoading[buttonsMap.red.key], disabled: false, reason: '' },
                delete: { loading: this.buttonsLoading[buttonsMap.delete.key], disabled: false, reason: '' }
            };
            if (this.selectedRecords.length !== 1) {
                result.edit.disabled =
                    result.approval.disabled =
                    result.unapproval.disabled =
                    result.grn.disabled =
                    result.copy.disabled =
                    result.red.disabled =
                    result.delete.disabled =
                        true;
                result.edit.reason =
                    result.approval.reason =
                    result.unapproval.reason =
                    result.grn.reason =
                    result.copy.reason =
                    result.red.reason =
                    result.delete.reason =
                        '选中1条记录才能进行操作';
                return result;
            }
            if (this.selectedRecords[0].status === purchaseGrnOrderStatusMap.FORMAL.value) {
                result.unapproval.disabled = result.grn.disabled = result.copy.disabled = result.red.disabled = true;
                result.unapproval.reason =
                    result.grn.reason =
                    result.copy.reason =
                    result.red.reason =
                        '正式状态的记录不能进行此操作';
            }
            if (this.selectedRecords[0].status === purchaseGrnOrderStatusMap.APPROVALED.value) {
                result.approval.disabled = result.copy.disabled = result.red.disabled = result.delete.disabled = true;
                result.approval.reason =
                    result.copy.reason =
                    result.red.reason =
                    result.delete.reason =
                        '已审核的记录不能进行此操作';
            }

            if (this.selectedRecords[0].status === purchaseGrnOrderStatusMap.GRN.value) {
                result.approval.disabled =
                    result.unapproval.disabled =
                    result.grn.disabled =
                    result.copy.disabled =
                    result.delete.disabled =
                        true;
                result.approval.reason =
                    result.unapproval.reason =
                    result.grn.reason =
                    result.copy.reason =
                    result.delete.reason =
                        '已入库的记录不能进行此操作';
            }

            if (this.selectedRecords[0].status === purchaseGrnOrderStatusMap.RED.value) {
                result.approval.disabled =
                    result.unapproval.disabled =
                    result.grn.disabled =
                    result.copy.disabled =
                    result.red.disabled =
                    result.delete.disabled =
                        true;
                result.edit.reason =
                    result.approval.reason =
                    result.unapproval.reason =
                    result.grn.reason =
                    result.copy.reason =
                    result.red.reason =
                    result.delete.reason =
                        '红冲的记录不能进行此操作';
            }

            if (this.selectedRecords[0].status === purchaseGrnOrderStatusMap.CANCEL.value) {
                result.approval.disabled =
                    result.unapproval.disabled =
                    result.grn.disabled =
                    result.red.disabled =
                    result.delete.disabled =
                        true;
                result.approval.reason =
                    result.unapproval.reason =
                    result.grn.reason =
                    result.red.reason =
                    result.delete.reason =
                        '作废的记录不能进行此操作';
            }

            // todo 单据状态过滤
            return result;
        },
        warehouseOptions() {
            return this.$store.state.warehouseList.map((warehouse) => ({
                label: warehouse.name,
                value: warehouse.warehouse_model_inner_sn
            }));
        },
        statusOptions() {
            return Object.keys(purchaseGrnOrderStatusMap).map((key) => ({
                value: purchaseGrnOrderStatusMap[key].value,
                label: purchaseGrnOrderStatusMap[key].text
            }));
        }
    },
    methods: {
        tableRowClassName({ row }) {
            if (
                row.status === purchaseGrnOrderStatusMap.RED.value ||
                row.status === purchaseGrnOrderStatusMap.CANCEL.value
            ) {
                return 'red-dashed-odo-order';
            }
            return '';
        },
        /**
         * 简单查询
         */
        onHeaderSingleSearch() {
            this.handleSearch({ grnSn: this.searchValues.grnSn });
        },

        /**
         * 多条件查询
         */
        onHeaderMultipleSearch() {
            this.handleSearch();
        },

        onSearchFormOrganizationSelected(organizations) {
            if (organizations && organizations.length > 0) {
                this.searchValues.organizationName = organizations[0].organizationName;
                this.searchValues.organizationInnerSn = organizations[0].organizationInnerSn;
                this.searchValues.billsType = organizations[0].billsType;
            }
            this.handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER);
        },

        onOrderEditorClose() {
            this.handleCloseModals(modalsKeyMap.ORDER_EDITOR);
            this.orderEditorTarget = null;
            this.orderEditorTargetIsCopy = false;
        },

        onOrderAdd(order) {
            this.$refs.listTable.handleInsertOrder(order);
        },

        onOrderChange(order) {
            this.$refs.listTable.handleUpdateOrder(order);
        },

        onRedFinish() {
            this.handleSearch();
            this.selectedRecords = [];
        },

        onRowDoubleClick(row) {
            this.orderEditorTarget = row;
            this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
        },

        onHeaderButtonClick(key) {
            switch (key) {
                case 'add':
                    this.orderEditorTarget = null;
                    this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
                    break;
                case 'edit': // 注意：修改按钮的逻辑非字面意思的修改，应要求，仅仅是打开详情模态框
                case 'approval':
                    if (this.selectedRecords[0]) {
                        this.orderEditorTarget = this.selectedRecords[0];
                        this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
                    }
                    break;
                case 'copy':
                    if (this.selectedRecords[0]) {
                        this.orderEditorTarget = {
                            ...this.selectedRecords[0],
                            details: null,
                            status: undefined
                        };
                        this.orderEditorTargetIsCopy = true;
                        this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
                    }
                    break;
                case 'unapproval':
                    this.$confirm({
                        title: '确定要反审核吗？',
                        onOk: () => {
                            if (!this.selectedRecords[0]) {
                                throw new Error('无目标单据');
                            }
                            this.unapprovalPurchaseGrnOrder(
                                this.selectedRecords[0].grn_inner_sn,
                                this.selectedRecords[0].billsType
                            ).then(() => {
                                this.onOrderChange({
                                    ...this.selectedRecords[0],
                                    status: purchaseGrnOrderStatusMap.FORMAL.value
                                });
                            });
                        },
                        okText: '确定',
                        cancelText: '取消'
                    });
                    break;
                case 'grn':
                    this.$confirm({
                        title: '确定要入库吗？',
                        onOk: () => {
                            if (!this.selectedRecords[0]) {
                                throw new Error('无目标单据');
                            }
                            this.putinPurchaseGrnOrder(
                                this.selectedRecords[0].grn_inner_sn,
                                this.selectedRecords[0].billsType,
                                this.selectedRecords[0].warehouse_inner_sn,
                                this.selectedRecords[0].warehouse_name
                            ).then(() => {
                                this.onOrderChange({
                                    ...this.selectedRecords[0],
                                    status: purchaseGrnOrderStatusMap.GRN.value
                                });
                            });
                        },
                        okText: '确定',
                        cancelText: '取消'
                    });
                    break;
                case 'red':
                    this.$confirm({
                        title: '确定要红冲吗？',
                        onOk: () => {
                            if (!this.selectedRecords[0]) {
                                throw new Error('无目标单据');
                            }
                            this.redPurchaseGrnOrder(
                                this.selectedRecords[0].grn_inner_sn,
                                this.selectedRecords[0].billsType
                            );
                        },
                        okText: '确定',
                        cancelText: '取消'
                    });
                    break;
                case 'delete':
                    this.$confirm({
                        title: '确定要删除单据吗？',
                        onOk: () => {
                            if (!this.selectedRecords[0]) {
                                throw new Error('无目标单据');
                            }
                            this.deletePurchaseGrnOrder(
                                this.selectedRecords[0].grn_inner_sn,
                                this.selectedRecords[0].billsType
                            );
                        },
                        okText: '确定',
                        cancelText: '取消'
                    });
                    break;
            }
        },

        onSelectedChange(selected) {
            this.selectedRecords = selected;
        },

        /**
         * 查询列表
         * @param {object | undefined} values 查询条件，未定义时以当前状态进行查询
         */
        handleSearch(values) {
            if (this.$refs.listTable) {
                this.$refs.listTable.handleSearch(values);
            }
        },

        /**
         * 删除采购入库单
         * @param {string | number} id 单据id
         * @param {string} billsType 单据类型
         */
        async deletePurchaseGrnOrder(id, billsType) {
            const orgId = this.$cookie.get('userbelong');
            try {
                this.buttonsLoading[buttonsMap.delete.key] = true;
                const { code, msg } = await purchaseGrnDelAPI({
                    billsType,
                    grn_inner_sn: id,
                    org_id: orgId
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('删除成功');
                this.selectedRecords = [];
                this.$refs.listTable.handleRemoveRecordByKeyValue('grn_inner_sn', id);
            } catch (err) {
                this.$message.error(err.message || '删除失败');
                throw err;
            } finally {
                this.buttonsLoading[buttonsMap.delete.key] = false;
            }
        },

        /**
         * 入库
         * @param {string | number} id 入库单id
         * @param {string} billsType 单据类型
         * @param {string | number} warehouseInnerSn 仓库id
         * @param {string} warehouseName 仓库名
         */
        async putinPurchaseGrnOrder(id, billsType, warehouseInnerSn, warehouseName) {
            const orgId = this.$cookie.get('userbelong');
            try {
                this.buttonsLoading[buttonsMap.grn.key] = true;
                const { code, msg } = await grnPutinAPI({
                    billsType,
                    org_id: orgId,
                    grn_inner_sn: id,
                    warehouse_inner_sn: warehouseInnerSn,
                    warehouse_name: warehouseName
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('入库成功');
            } catch (err) {
                this.$message.error(err.message || '入库失败');
                throw err;
            } finally {
                this.buttonsLoading[buttonsMap.grn.key] = false;
            }
        },

        /**
         * 红冲
         * @param {string | number} id 入库单id
         * @param {string} billsType 单据类型
         */
        async redPurchaseGrnOrder(id, billsType) {
            const orgId = this.$cookie.get('userbelong');
            try {
                this.buttonsLoading[buttonsMap.red.key] = true;
                const { code, info, msg } = await grnRedDashedAPI({
                    billsType,
                    org_id: orgId,
                    grn_inner_sn: id
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('红冲成功');
                this.onRedFinish(info);
            } catch (err) {
                this.$message.error(err.message || '红冲失败');
                throw err;
            } finally {
                this.buttonsLoading[buttonsMap.red.key] = false;
            }
        },

        /**
         * 反审核
         * @param {string | number} id 入库单id
         * @param {string} billsType 单据类型
         */
        async unapprovalPurchaseGrnOrder(id, billsType) {
            const orgId = this.$cookie.get('userbelong');
            try {
                this.buttonsLoading[buttonsMap.unapproval.key] = true;
                const { code, msg } = await grnUnreviewAPI({
                    billsType,
                    org_id: orgId,
                    grn_inner_sn: id
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('反审核成功');
            } catch (err) {
                this.$message.error(err.message || '反审核失败');
                throw err;
            } finally {
                this.buttonsLoading[buttonsMap.unapproval.key] = false;
            }
        }
    }
};
</script>
