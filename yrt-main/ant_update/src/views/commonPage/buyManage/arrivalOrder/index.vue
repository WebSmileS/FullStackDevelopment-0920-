<template>
    <list-page-layout isTabListPage>
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
                        v-model="searchValues.sn"
                        @search="onHeaderSingleSearch"
                    ></list-page-header-single-searcher>
                </template>
                <template slot="searchFormItems">
                    <list-page-header-input label="单据编号" v-model="searchValues.sn"></list-page-header-input>
                    <list-page-header-modal-input
                        label="供货单位"
                        :value="searchValues.organizationName"
                        @input="handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                        @clear="onSearchFormOrganizationClear"
                    ></list-page-header-modal-input>
                    <list-page-header-input
                        label="送货员"
                        v-model="searchValues.deliveryMenName"
                    ></list-page-header-input>
                    <list-page-header-date-range
                        label="收货日期"
                        v-model="searchValues.dateRange"
                    ></list-page-header-date-range>
                </template>
            </list-page-header>
        </template>
        <template slot="list">
            <a-tabs type="card" v-model="activeTabKey">
                <a-tab-pane :key="arrivalOrderTabMap.EXECUTING.value" tab="执行中">
                    <arrival-list
                        selectType="radio"
                        :ref="arrivalOrderTabMap.EXECUTING.value"
                        :reloadWhenSearch="false"
                        :listStatuType="arrivalOrderTabMap.EXECUTING.value"
                        :managerMode="managerMode"
                        :searchValues="searchValues"
                        @selectedChange="onSelectedChange"
                        @rowDoubleClick="onRowDoubleClick"
                    ></arrival-list>
                </a-tab-pane>
                <a-tab-pane :key="arrivalOrderTabMap.HISTORY.value" tab="收货记录">
                    <arrival-list
                        selectType="radio"
                        :ref="arrivalOrderTabMap.HISTORY.value"
                        :reloadWhenSearch="false"
                        :listStatuType="arrivalOrderTabMap.HISTORY.value"
                        :managerMode="managerMode"
                        :searchValues="searchValues"
                        @selectedChange="onSelectedChange"
                        @rowDoubleClick="onRowDoubleClick"
                    ></arrival-list>
                </a-tab-pane>
            </a-tabs>
        </template>
        <template slot="modals">
            <organization-selecter
                title="选择供货单位"
                destroyOnClose
                :visible="modalsVisible[modalsKeyMap.ORGANIZATION_SELECTER]"
                @close="handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                @submit="onSearchFormOrganizationSelected"
            ></organization-selecter>
            <arrival-order-editor
                :visible="modalsVisible[modalsKeyMap.ORDER_EDITOR]"
                :targetOrder="orderEditorTarget"
                :managerMode="managerMode"
                @close="handleCloseModals(modalsKeyMap.ORDER_EDITOR)"
                @addFinish="onOrderAddFinish"
                @editFinish="onOrderEditFinish"
                @approvalFinish="onOrderApprovalFinish"
                @cancelFinish="onOrderCancelFinish"
            ></arrival-order-editor>
        </template>
    </list-page-layout>
</template>

<script>
import ListPageLayout from '@/components/ListPageLayout';
import ListPageHeader from '@/components/ListPageHeader';
import ListPageHeaderSingleSearcher from '@/components/ListPageHeaderSingleSearcher';
import ListPageHeaderInput from '@/components/ListPageHeaderInput';
import ListPageHeaderDateRange from '@/components/ListPageHeaderDateRange';
import ListPageHeaderModalInput from '@/components/ListPageHeaderModalInput';
import tabsMixin from '@/mixins/tabs';
import modalsMixin from '@/mixins/modals';
import ArrivalList from './ArrivalList';
import OrganizationSelecter from './OrganizationSelecter';
import ArrivalOrderEditor from './ArrivalOrderEditor';
import { arrivalOrderDeleteAPI } from '@/service/pageAjax';
import { arrivalOrderTabMap } from './type';

const modalsKeyMap = Object.freeze({
    ORGANIZATION_SELECTER: 'g',
    ORDER_EDITOR: 'o'
});

const buttonsMap = {
    add: { key: 'add', text: '调单收货', type: 'primary' },
    edit: { key: 'edit', text: '修改', type: 'primary' },
    approval: { key: 'approval', text: '复核', type: 'primary' },
    receive: { key: 'receive', text: '收货', type: 'primary' },
    cancel: { key: 'cancel', text: '作废', type: 'danger' },
    delete: { key: 'delete', text: '删除', type: 'danger' }
};

export default {
    name: 'ArrivalOrder',
    components: {
        ListPageLayout,
        ListPageHeader,
        ListPageHeaderSingleSearcher,
        ListPageHeaderInput,
        ListPageHeaderDateRange,
        ListPageHeaderModalInput,
        ArrivalList,
        OrganizationSelecter,
        ArrivalOrderEditor
    },
    mixins: [tabsMixin, modalsMixin],
    props: {
        // 管理模式下，部分功能需要过滤处理
        managerMode: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            modalsKeyMap,
            loading: false,
            searchValues: {
                sn: undefined,
                organizationName: undefined,
                organizationInnerSn: undefined,
                deliveryMenName: undefined,
                dateRange: []
            },
            activeTabKey: arrivalOrderTabMap.EXECUTING.value,
            selectedRecords: [],
            orderEditorTarget: null // 收货单编辑目标
        };
    },
    computed: {
        arrivalOrderTabMap() {
            return arrivalOrderTabMap;
        },
        operationGroups() {
            if (this.managerMode) {
                return [[buttonsMap.approval], [buttonsMap.cancel]];
            }
            return [[buttonsMap.add, buttonsMap.edit, buttonsMap.receive], [buttonsMap.delete]];
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false },
                edit: { disabled: false, reason: '' },
                receive: { disabled: false, reason: '' },
                approval: { disabled: false, reason: '' },
                cancel: { loading: this.loading, disabled: false, reason: '' },
                delete: { loading: this.loading, disabled: false, reason: '' }
            };
            if (this.selectedRecords.length !== 1) {
                result.edit.disabled = true;
                result.receive.disabled = true;
                result.approval.disabled = true;
                result.cancel.disabled = true;
                result.delete.disabled = true;
                result.edit.reason =
                    result.receive.reason =
                    result.approval.reason =
                    result.cancel.reason =
                    result.delete.reason =
                        '选中1条记录才能进行操作';
            }
            if (this.activeTabKey !== arrivalOrderTabMap.EXECUTING.value) {
                result.edit.disabled = true;
                result.receive.disabled = true;
                result.approval.disabled = true;
                result.cancel.disabled = true;
                result.delete.disabled = true;
                result.edit.reason =
                    result.receive.reason =
                    result.approval.reason =
                    result.cancel.reason =
                    result.delete.reason =
                        '执行中的记录才能进行操作';
            }
            return result;
        }
    },
    watch: {
        activeTabKey(newValue, oldValue) {
            // 切换tab并且已有选择项时，清空选择项
            if (oldValue && this.selectedRecords && this.selectedRecords.length) {
                this.selectedRecords = [];
                this.$refs[oldValue].$refs.listTable.$refs.listTable.clearRadioRow();
            }
        }
    },
    methods: {
        /**
         * 简单查询
         */
        onHeaderSingleSearch() {
            this.handleSearch({ sn: this.searchValues.sn });
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

        onOrderAddFinish() {
            this.selectedRecords = [];
            this.handleSearch();
        },

        onOrderEditFinish() {
            this.selectedRecords = [];
            this.handleSearch();
        },

        onOrderApprovalFinish() {
            this.selectedRecords = [];
            this.handleSearch();
        },

        onOrderCancelFinish() {
            this.selectedRecords = [];
            this.handleSearch();
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
                case 'edit':
                case 'receive':
                case 'approval':
                case 'cancel':
                    if (this.selectedRecords[0]) {
                        this.orderEditorTarget = this.selectedRecords[0];
                        this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
                    }
                    break;
                case 'delete':
                    this.$confirm({
                        title: '确定要删除单据吗？',
                        onOk: () => {
                            if (!this.selectedRecords[0]) {
                                throw new Error('无目标单据');
                            }
                            this.deleteArrivalOrder(this.selectedRecords[0].id);
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
            if (this.$refs[arrivalOrderTabMap[this.activeTabKey].value]) {
                this.$refs[arrivalOrderTabMap[this.activeTabKey].value].handleSearch(values);
            }
        },

        /**
         * 删除到货单
         * @param {string | number} id 单据id
         */
        async deleteArrivalOrder(id) {
            try {
                this.loading = true;
                const { code, msg } = await arrivalOrderDeleteAPI(id);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('删除成功');
                this.selectedRecords = [];
                this.$refs[arrivalOrderTabMap[this.activeTabKey].value].handleRemoveRecordByKeyValue('id', id);
            } catch (err) {
                this.$message.error('删除失败');
                throw err;
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>
