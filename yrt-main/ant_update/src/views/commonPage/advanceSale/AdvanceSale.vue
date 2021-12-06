<template>
    <list-page-layout>
        <template slot="header">
            <list-page-header
                needMixedSearch
                disabledButtonVisible
                :operationGroups="operationGroups"
                :buttonsStatu="headerButtonsStatu"
                @btnClick="onHeaderButtonClick"
                @search="onHeaderMultipleSearch"
            >
                <template slot="singleSearcher">
                    <list-page-header-single-searcher
                        slot="singleSearcher"
                        placeholder="单据编号"
                        v-model="searchValues.advanceSaleNo"
                        @search="onHeaderSingleSearch"
                    ></list-page-header-single-searcher>
                </template>
                <template slot="searchFormItems">
                    <list-page-header-input
                        label="单据编号"
                        v-model="searchValues.advanceSaleNo"
                    ></list-page-header-input>
                    <list-page-header-modal-input
                        label="收货单位"
                        :value="searchValues.organizationName"
                        @input="handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                        @clear="onSearchFormOrganizationClear"
                    ></list-page-header-modal-input>
                    <list-page-header-select
                        v-if="!isFinance"
                        label="审核状态"
                        allowClear
                        v-model="searchValues.auditStatus"
                        :options="orderAuditStatusOptions"
                    ></list-page-header-select>
                </template>
            </list-page-header>
        </template>
        <template slot="list">
            <advance-sale-order-list
                selectType="radio"
                ref="list"
                :reloadWhenSearch="false"
                :searchValues="searchValues"
                @selectedChange="onSelectedChange"
                @rowDoubleClick="onRowDoubleClick"
            ></advance-sale-order-list>
        </template>
        <template slot="modals">
            <organization-selecter
                title="选择收货单位"
                destroyOnClose
                :visible="modalsVisible[modalsKeyMap.ORGANIZATION_SELECTER]"
                @close="handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                @submit="onSearchFormOrganizationSelected"
            ></organization-selecter>
            <advance-sale-order-editor
                :visible="modalsVisible[modalsKeyMap.ORDER_EDITOR]"
                :targetOrder="orderEditorTarget"
                @close="handleCloseModals(modalsKeyMap.ORDER_EDITOR)"
                @addFinish="onOrderAddFinish"
                @editFinish="onOrderEditFinish"
            ></advance-sale-order-editor>
        </template>
    </list-page-layout>
</template>

<script>
import ListPageLayout from '@/components/ListPageLayout';
import ListPageHeader from '@/components/ListPageHeader';
import ListPageHeaderSingleSearcher from '@/components/ListPageHeaderSingleSearcher';
import ListPageHeaderInput from '@/components/ListPageHeaderInput';
import ListPageHeaderSelect from '@/components/ListPageHeaderSelect';
import ListPageHeaderModalInput from '@/components/ListPageHeaderModalInput';
import OrganizationSelecter from '@/components/OrganizationSelecter';
import modalsMixin from '@/mixins/modals';
import AdvanceSaleOrderList from './AdvanceSaleOrderList';
import AdvanceSaleOrderEditor from './AdvanceSaleOrderEditor';
import { advanceSaleOrderApproveAPI, advanceSaleOrderDeleteAPI } from '@/service/advanceSale';
import { advanceSaleOrderAuditStatusMap, advanceSaleOrderAuditStatusTextMap } from './type';

const modalsKeyMap = Object.freeze({
    ORGANIZATION_SELECTER: 'g',
    ORDER_EDITOR: 'o'
});

const buttonsMap = {
    add: { key: 'add', text: '新增', type: 'primary' },
    detail: { key: 'detail', text: '查看', type: 'primary' },
    edit: { key: 'edit', text: '修改', type: 'primary' },
    approve: { key: 'approve', text: '审核', type: 'primary' },
    delete: { key: 'delete', text: '删除', type: 'danger' }
};

export default {
    name: 'AdvanceSale',
    components: {
        ListPageLayout,
        ListPageHeader,
        ListPageHeaderSingleSearcher,
        ListPageHeaderInput,
        ListPageHeaderSelect,
        ListPageHeaderModalInput,
        AdvanceSaleOrderList,
        OrganizationSelecter,
        AdvanceSaleOrderEditor
    },
    mixins: [modalsMixin],
    /**
     * isFinance:
     * 是否为财务人员使用，财务人员使用时只有查看功能，
     * 并且列表内容为已审核单据
     */
    inject: ['isFinance'],
    data() {
        return {
            modalsKeyMap,
            loading: false,
            searchValues: {
                auditStatus: undefined,
                advanceSaleNo: undefined,
                organizationName: undefined,
                organizationInnerSn: undefined,
                dateRange: []
            },
            selectedRecords: [],
            orderEditorTarget: null, // 收货单编辑目标
            approveLoading: false,
            deleteLoading: false
        };
    },
    computed: {
        operationGroups() {
            // 财务人员只用查看功能
            if (this.isFinance) {
                return [[buttonsMap.detail]];
            }
            return [[buttonsMap.add, buttonsMap.detail, buttonsMap.edit, buttonsMap.approve], [buttonsMap.delete]];
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false },
                detail: { disabled: false },
                edit: { disabled: false, reason: '' },
                approve: { disabled: false, loading: this.approveLoading, reason: '' },
                delete: { disabled: false, loading: this.deleteLoading, reason: '' }
            };
            if (this.selectedRecords.length !== 1) {
                result.detail.disabled = result.edit.disabled = result.approve.disabled = result.delete.disabled = true;
                result.detail.reason =
                    result.edit.reason =
                    result.approve.reason =
                    result.delete.reason =
                        '选中1条记录才能进行操作';
            }
            if (
                this.selectedRecords[0] &&
                this.selectedRecords[0].auditStatus !== advanceSaleOrderAuditStatusMap.UNCHECKED
            ) {
                result.edit.disabled = result.approve.disabled = result.delete.disabled = true;
                result.edit.reason = result.approve.reason = result.delete.reason = '未审核的单据才能进行此操作';
            }
            return result;
        },
        orderAuditStatusOptions() {
            return Object.keys(advanceSaleOrderAuditStatusMap).map((key) => ({
                value: advanceSaleOrderAuditStatusMap[key],
                label: advanceSaleOrderAuditStatusTextMap[key]
            }));
        }
    },
    methods: {
        /**
         * 简单查询
         */
        onHeaderSingleSearch() {
            this.handleSearch({ advanceSaleNo: this.searchValues.advanceSaleNo });
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

        onSearchFormOrganizationClear() {
            this.searchValues.organizationName = undefined;
            this.searchValues.organizationInnerSn = undefined;
            this.searchValues.billsType = undefined;
        },

        /**
         * 单据新增完成后，使用返回的数据更新对应的记录信息
         */
        onOrderAddFinish(order) {
            this.$refs.list.$data.list.unshift(order);
            this.$refs.list.$data.pagination.total += 1;
            this.$nextTick(() => {
                const list = this.$refs.list.$data.list;
                const newRecord = list[0];
                const vxeTable = this.$refs.list.$refs.listTable.$refs.listTable;
                this.selectedRecords = [newRecord];
                vxeTable.setRadioRow(newRecord);
                vxeTable.scrollToRow(newRecord);
                vxeTable.setCurrentRow(newRecord);
                this.$refs.list.$data.detailTarget = newRecord;
            });
        },

        /**
         * 单据编辑完成后，使用返回的数据更新对应的记录信息
         */
        onOrderEditFinish(order) {
            const list = this.$refs.list.$data.list;
            for (let i = 0, l = list.length; i < l; i += 1) {
                const current = list[i];
                if (current.id === order.id) {
                    Object.keys(current).forEach((key) => {
                        if (key !== '_XID') {
                            current[key] = order[key];
                        }
                    });
                    break;
                }
            }
            this.$refs.list.$refs.detailTable.handleReload();
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
                case 'detail':
                case 'edit':
                    if (this.selectedRecords[0]) {
                        this.orderEditorTarget = this.selectedRecords[0];
                        this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
                    }
                    break;
                case 'approve':
                    if (this.selectedRecords[0]) {
                        this.approveAdvanceSaleOrder(this.selectedRecords[0].id, this.selectedRecords[0]);
                    }
                    break;
                case 'delete':
                    this.$confirm({
                        title: '确定要删除单据吗？',
                        onOk: () => {
                            if (this.selectedRecords[0]) {
                                this.deleteAdvanceSaleOrder(this.selectedRecords[0].id, this.selectedRecords[0]);
                            }
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
            this.$refs.list.handleSearch(values);
        },

        /**
         * 更新列表内某条记录的审核状态（仅前端更新）
         * @param {number | string} id 预售单id
         * @param {string} 状态值
         */
        handleChangeRecordAuditStatusLazily(id, status) {
            const list = this.$refs.list.$data.list;
            for (let i = 0, l = list.length; i < l; i += 1) {
                if (list[i].id === id) {
                    list[i].auditStatus = status;
                    break;
                }
            }
        },

        /**
         * 审核预售单
         * @param {number | string} id 预售单id
         * @param {object | undefined} 目标单据记录
         */
        async approveAdvanceSaleOrder(id, record) {
            try {
                this.approveLoading = true;
                const { code, msg } = await advanceSaleOrderApproveAPI(id);
                if (code !== 0) {
                    this.$message.error(msg, 5);
                    throw new Error(msg);
                }
                this.$message.success('审核成功');
                if (record) {
                    this.handleChangeRecordAuditStatusLazily(id, advanceSaleOrderAuditStatusMap.APPROVED);
                }
            } finally {
                this.approveLoading = false;
            }
        },

        /**
         * 删除预售单
         * @param {string | number} id 单据id
         */
        async deleteAdvanceSaleOrder(id) {
            try {
                this.deleteLoading = true;
                const { code, msg } = await advanceSaleOrderDeleteAPI(id);
                if (code !== 0) {
                    this.$message.error(msg);
                    throw new Error(msg);
                }
                this.$message.success('删除成功');
                this.selectedRecords = [];
                this.$refs.list.handleRemoveRecordByKeyValue('id', id);
                this.$refs.list.$data.detailTarget = undefined;
            } finally {
                this.deleteLoading = false;
            }
        }
    }
};
</script>
