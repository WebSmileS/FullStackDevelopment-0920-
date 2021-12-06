<template>
    <list-page-layout isTabListPage>
        <template slot="header">
            <list-page-header
                needMixedSearch
                :operationGroups="operationGroups"
                :buttonsStatu="buttonsStatu"
                :searchFormItems="searchFormItems"
                @btnClick="onHeaderButtonClick"
                @search="handleSearch"
            />
        </template>
        <template slot="list">
            <a-tabs type="card" v-model="activeTabKey">
                <a-tab-pane key="waiting" tab="待处理">
                    <logistics-list
                        listStatuType="waiting"
                        selectType="radio"
                        :searchValues="searchValues"
                        :reloadWhenSearch="activeTabKey === 'waiting'"
                        @selectedChange="onSelectedChange"
                    >
                        <template slot="statuColumn">
                            <vxe-table-column title="状态" width="17%">
                                <template v-slot:default="{ row }">
                                    <span :class="logisticsStatusMap[row['status']].color">{{
                                        logisticsStatusMap[row['status']].text
                                    }}</span>
                                </template>
                            </vxe-table-column>
                        </template>
                    </logistics-list>
                </a-tab-pane>
                <a-tab-pane key="history" tab="历史">
                    <logistics-list
                        listStatuType="history"
                        selectType="radio"
                        :searchValues="searchValues"
                        :reloadWhenSearch="activeTabKey === 'history'"
                        @selectedChange="onSelectedChange"
                    >
                        <template slot="statuColumn">
                            <vxe-table-column title="状态" width="17%">
                                <template v-slot:default="{ row }">
                                    <span :class="logisticsStatusMap[row['status']].color">{{
                                        logisticsStatusMap[row['status']].text
                                    }}</span>
                                </template>
                            </vxe-table-column>
                        </template>
                    </logistics-list>
                </a-tab-pane>
            </a-tabs>
        </template>
        <template slot="modals">
            <logistics-editor
                title="物流详情"
                :visible="modalsVisible.logisticsEditor"
                :defaultData="defaultLogisticsEditorData"
                :onClose="() => handleCloseModals('logisticsEditor')"
                :onSubmit="onLogisticsEditorSubmit"
            />
            <logistics-detail
                title="物流详情"
                :visible="modalsVisible.logisticsDetail"
                :logisticsData="logisticsDetailData"
                :onClose="() => handleCloseModals('logisticsDetail')"
                :onEdit="handleEditLogisticsByDetail"
            />
        </template>
    </list-page-layout>
</template>

<script>
import { confirmLogisticsAPI, editLogisticsAPI, getLogisticsAPI } from '@/service/pageAjax';
import ListPageLayout from '@/components/ListPageLayout.vue';
import ListPageHeader from '@/components/ListPageHeader.vue';
import tabsMixin from '@/mixins/tabs';
import LogisticsEditor from '../components/LogisticsEditor.vue';
import LogisticsDetail from '../components/LogisticsDetail.vue';
import modalsMixin from '@/mixins/modals';
import LogisticsList from './LogisticsList.vue';
import columns from './columns';
import { logisticsModeMap, logisticsStatusMap } from '../components/logisticsType';

const logisticsModeOptions = Object.keys(logisticsModeMap).map((key) => ({
    value: logisticsModeMap[key].value,
    label: logisticsModeMap[key].text
}));

export default {
    name: 'LogisticsManage',
    components: {
        ListPageLayout,
        ListPageHeader,
        LogisticsList,
        LogisticsEditor,
        LogisticsDetail
    },
    mixins: [modalsMixin, tabsMixin],
    data() {
        return {
            columns,
            logisticsStatusMap,
            loading: false,
            searchValues: {},
            activeTabKey: 'waiting',
            selected: [],
            defaultLogisticsEditorData: null,
            logisticsDetailData: null
        };
    },
    computed: {
        operationGroups() {
            return [
                [
                    { key: 'scan', text: '查看', type: 'primary' },
                    { key: 'edit', text: '编辑', type: 'primary' },
                    { key: 'confirm', text: '确认', type: 'primary' }
                ]
            ];
        },
        buttonsStatu() {
            let result = {
                scan: { loading: this.loading, disabled: false },
                edit: { loading: this.loading, disabled: false },
                confirm: { loading: this.loading, disabled: false }
            };
            if (this.selected.length !== 1) {
                result.scan.disabled = true;
                result.edit.disabled = true;
                result.confirm.disabled = true;
                result.scan.reason = result.edit.reason = result.confirm.reason = '选中1条物流记录才可以进行操作';
            }
            if (this.selected.length > 1) {
                result.scan.disabled = true;
                result.scan.reason = '一次只能查看一条记录';
            }
            if (this.activeTabKey !== 'waiting') {
                result.edit.disabled = true;
                result.confirm.disabled = true;
                result.edit.reason = result.confirm.reason = '待处理的物流记录才可以进行操作';
            }
            return result;
        },
        searchFormItems() {
            return [
                { type: 'text', key: 'logisticsSn', span: 6, label: '物流单号', placeholder: '物流单号' },
                {
                    type: 'select',
                    key: 'logisticsMode',
                    span: 6,
                    label: '物流类型',
                    options: logisticsModeOptions
                },
                { type: 'daterange', keys: ['beginDate', 'endDate'], span: 6, label: '创建日期' }
            ];
        }
    },
    methods: {
        handleSearch(values = {}) {
            this.searchValues = { ...this.searchValues, ...values };
        },
        handleEditLogisticsByDetail() {
            this.defaultLogisticsEditorData = this.logisticsDetailData;
            this.handleCloseModals('logisticsDetail');
            this.handleOpenModals('logisticsEditor');
        },
        onSelectedChange(selected) {
            this.selected = selected;
        },
        async onHeaderButtonClick(type) {
            const { pick } = this.$XEUtils;
            switch (type) {
                case 'scan':
                    try {
                        const record = this.selected[0];
                        this.loading = true;
                        const logisticsInfo = await this.loadLogistics(record.id);
                        this.logisticsDetailData = {
                            logisticsId: record.id,
                            logisticsMode: logisticsInfo['logistics_mode'],
                            logisticsSn: logisticsInfo['logistics_sn'],
                            logisticsCompany: logisticsInfo['third_logistics_company'],
                            logisticsStatus: logisticsInfo['status'],
                            notices: logisticsInfo.details.map((notice) => {
                                return pick(notice, [
                                    'id',
                                    'dealer_name',
                                    'purchasing_company_name',
                                    'warehouse_name',
                                    'batch_number',
                                    'product_name',
                                    'product_vendor_name',
                                    'quantity',
                                    'rate',
                                    'specification',
                                    'unit',
                                    'unit_price'
                                ]);
                            })
                        };
                        this.handleOpenModals('logisticsDetail');
                    } finally {
                        this.loading = false;
                    }
                    break;
                case 'edit':
                    try {
                        const record = this.selected[0];
                        this.loading = true;
                        const logisticsInfo = await this.loadLogistics(record.id);
                        this.defaultLogisticsEditorData = {
                            logisticsId: record.id,
                            logisticsMode: logisticsInfo['logistics_mode'],
                            logisticsSn: logisticsInfo['logistics_sn'],
                            logisticsCompany: logisticsInfo['third_logistics_company'],
                            notices: logisticsInfo.details.map((notice) => {
                                return pick(notice, [
                                    'id',
                                    'dealer_name',
                                    'purchasing_company_name',
                                    'warehouse_name',
                                    'batch_number',
                                    'product_name',
                                    'product_vendor_name',
                                    'quantity',
                                    'rate',
                                    'specification',
                                    'unit',
                                    'unit_price'
                                ]);
                            })
                        };
                        this.handleOpenModals('logisticsEditor');
                    } finally {
                        this.loading = false;
                    }
                    break;
                case 'confirm':
                    try {
                        this.loading = true;
                        await this.confirmLogistics(this.selected[0].id);
                        this.handleSearch();
                    } finally {
                        this.loading = false;
                    }
                    break;
                default:
            }
        },
        async onLogisticsEditorSubmit(logisticsData) {
            try {
                const { code, msg } = await editLogisticsAPI(logisticsData);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('物流信息保存成功');
                this.defaultLogisticsEditorData = null;
                this.handleCloseModals('logisticsEditor');
                this.handleSearch();
            } catch (err) {
                this.$message.error('物流信息保存失败');
                throw err;
            }
        },
        async confirmLogistics(logisticsId) {
            try {
                const { code, msg } = await confirmLogisticsAPI(logisticsId);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('确认成功');
            } catch (err) {
                this.$message.error('确认失败');
                throw err;
            }
        },
        async loadLogistics(logisticsId) {
            try {
                const { code, info, msg } = await getLogisticsAPI(logisticsId);
                if (code !== 0) {
                    throw new Error(msg);
                }
                return info;
            } catch (err) {
                this.$message.error('获取物流信息失败');
                throw err;
            }
        },
        async editLogistics(logisticsData) {
            try {
                const { code, msg } = await editLogisticsAPI(logisticsData);
                if (code !== 0) {
                    throw new Error(msg);
                }
            } catch (err) {
                this.$message.error('编辑物流信息失败');
                throw err;
            }
        }
    },
    watch: {
        activeTabKey() {
            this.handleSearch();
            this.selected = [];
        }
    }
};
</script>
