<template>
    <list-page-layout isTabListPage>
        <template slot="header">
            <list-page-header
                needMixedSearch
                :operationGroups="operationGroups"
                :buttonsStatu="headerButtonsStatu"
                :searchFormItems="searchFormItems"
                @btnClick="onHeaderButtonClick"
                @search="handleSearch"
            />
        </template>
        <template slot="list">
            <a-tabs type="card" v-model="activeTabKey">
                <a-tab-pane key="waiting" tab="待收货">
                    <notice-list
                        listType="grn"
                        listStatuType="waiting"
                        selectType="checkbox"
                        :searchValues="searchValues"
                        :reloadWhenSearch="activeTabKey === 'waiting'"
                        @selectedChange="onSelectedChange"
                    >
                        <template slot="statuColumn">
                            <vxe-table-column width="7%" align="center" title="状态">
                                <template v-slot:default="{ row }">
                                    <span :class="statusMap[row['status']][1]">{{ statusMap[row['status']][0] }}</span>
                                </template>
                            </vxe-table-column>
                        </template>
                    </notice-list>
                </a-tab-pane>
                <a-tab-pane key="history" tab="历史">
                    <notice-list
                        listType="grn"
                        listStatuType="history"
                        selectType="none"
                        :searchValues="searchValues"
                        :reloadWhenSearch="activeTabKey === 'history'"
                    >
                        <template slot="statuColumn">
                            <vxe-table-column width="5%" align="center" title="状态">
                                <template v-slot:default="{ row }">
                                    <span :class="statusMap[row['status']][1]">{{ statusMap[row['status']][0] }}</span>
                                </template>
                            </vxe-table-column>
                        </template>
                    </notice-list>
                </a-tab-pane>
            </a-tabs>
        </template>
        <template slot="modals">
            <notice-editor
                title="入库通知"
                submit2Text="收货"
                noticeType="grn"
                :visible="modalsVisible.noticeEditor"
                :defaultData="defaultNoticeEditorData"
                :productAddAble="noticeEditorMark !== 'edit'"
                :onClose="() => handleCloseModals('noticeEditor')"
                :onSubmit="onNoticeEditorSubmit"
            />
        </template>
    </list-page-layout>
</template>

<script>
import { addGrnNotice, editGrnNotice, updateGrnNoticeStatus } from '@/service/pageAjax';
import ListPageLayout from '@/components/ListPageLayout.vue';
import ListPageHeader from '@/components/ListPageHeader.vue';
import tabsMixin from '@/mixins/tabs';
import modalsMixin from '@/mixins/modals';
import NoticeList from '../components/NoticeList.vue';
import NoticeEditor from '../components/NoticeEditor.vue';

const statusMap = {
    'TO_BE_RECEIVED': ['待收货', 'orange'],
    'RECEIVED': ['已收货', 'green'],
    'CANCEL': ['已作废', 'gray']
};

export default {
    name: 'GrnNotice',
    components: {
        ListPageLayout,
        ListPageHeader,
        NoticeList,
        NoticeEditor
    },
    mixins: [tabsMixin, modalsMixin],
    data() {
        return {
            loading: false,
            searchValues: {},
            activeTabKey: 'waiting',
            selectedRecords: [],
            // 通知编辑模态框标识: 'add', 'edit'
            noticeEditorMark: '',
            defaultNoticeEditorData: null,
            statusMap
        };
    },
    computed: {
        operationGroups() {
            const result = [
                [
                    { key: 'add', text: '新增', type: 'primary' },
                    // 备注：目前修改功能后端未实现
                    // { key: 'edit', text: '修改', type: 'primary' }
                    { key: 'confirm', text: '确认', type: 'primary' },
                    { key: 'nullify', text: '作废', type: 'danger' }
                ]
            ];
            return result;
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false },
                // 备注：目前修改功能后端未实现
                // edit: { disabled: false, reason: '' },
                confirm: { loading: this.loading, disabled: false, reason: '' },
                nullify: { loading: this.loading, disabled: false, reason: '' }
            };
            if (this.selectedRecords.length < 1) {
                result.confirm.disabled = true;
                result.nullify.disabled = true;
                result.confirm.reason = result.nullify.reason = '至少选中1条记录才能进行操作';
            }
            if (this.activeTabKey !== 'waiting') {
                result.confirm.disabled = true;
                result.nullify.disabled = true;
                result.confirm.reason = result.nullify.reason = '待收货记录才能进行操作';
            }
            return result;
        },
        searchFormItems() {
            return [
                { type: 'text', key: 'lesseeName', span: 6, label: '租户名称', placeholder: '租户名称' },
                { type: 'text', key: 'companyName', span: 6, label: '销售公司' },
                { type: 'text', key: 'productName', span: 6, label: '产品名称' },
                { type: 'daterange', keys: ['beginDate', 'endDate'], span: 6, label: '日期' }
            ];
        }
    },
    methods: {
        /**
         * 按条件查询
         * 更新查询条件，列表组件(NoticeList)会自动响应(watch)，并刷新列表
         * @param {object} values 查询条件
         */
        handleSearch(values = {}) {
            this.searchValues = { ...values };
        },
        async onHeaderButtonClick(type) {
            switch (type) {
                case 'add': {
                    this.noticeEditorMark = 'add';
                    this.handleOpenModals('noticeEditor');
                    break;
                }
                case 'edit': {
                    this.noticeEditorMark = 'edit';
                    const record = this.selectedRecords[0];
                    const defaultNoticeEditorData = {
                        noticeId: record['notice_id'],
                        companyName: record['sales_company_name'],
                        warehouseName: record['warehouse_name'],
                        lessee: {
                            'dealer_name': record['dealer_name'],
                            'dealer_inner_sn': record['dealer_inner_sn']
                        },
                        product: this.$XEUtils.pick(record, [
                            'batch_number',
                            'mdrf_inner_sn',
                            'mdrf_sn',
                            'overdue_date',
                            'produce_date',
                            'product_inner_sn',
                            'product_name',
                            'product_type',
                            'product_vendor_inner_sn',
                            'product_vendor_name',
                            'quantity',
                            'rate',
                            'specification',
                            'specification_inner_sn',
                            'unit',
                            'unit_inner_sn',
                            'unit_price'
                        ])
                    };
                    defaultNoticeEditorData.product.unitList = [{ label: record.unit, value: record['unit_inner_sn'] }];
                    this.defaultNoticeEditorData = defaultNoticeEditorData;
                    this.handleOpenModals('noticeEditor');
                    break;
                }
                case 'confirm': {
                    try {
                        this.loading = true;
                        await this.updateNoticeStatus(this.selectedRecords.map((_) => ({ id: _.id, status: 1 })));
                        this.selectedRecords = [];
                        this.handleSearch();
                    } finally {
                        this.loading = false;
                    }
                    break;
                }
                case 'nullify': {
                    try {
                        this.loading = true;
                        await this.updateNoticeStatus(this.selectedRecords.map((_) => ({ id: _.id, status: 2 })));
                        this.selectedRecords = [];
                        this.handleSearch();
                    } finally {
                        this.loading = false;
                    }
                    break;
                }
            }
        },
        onSelectedChange(selected) {
            this.selectedRecords = selected;
        },
        /**
         * 通知编辑提交时
         * 备注：目前修改功能后端未实现
         */
        async onNoticeEditorSubmit(noticeData, submitType) {
            switch (this.noticeEditorMark) {
                case 'add':
                    await this.addNotice(noticeData, submitType);
                    this.handleCloseModals('noticeEditor');
                    if (submitType === 1) {
                        this.$message.success('新增成功');
                        if (this.activeTabKey !== 'waiting') {
                            this.activeTabKey = 'waiting';
                        } else {
                            this.handleSearch();
                        }
                    }
                    if (submitType === 2) {
                        this.$message.success('收货成功');
                        if (this.activeTabKey !== 'history') {
                            this.activeTabKey = 'history';
                        } else {
                            this.handleSearch();
                        }
                    }
                    break;
                case 'edit':
                    await this.editNotice(noticeData, this.defaultNoticeEditorData.id);
                    this.$message.success('修改成功');
                    this.handleCloseModals('noticeEditor');
                    this.defaultNoticeEditorData = null;
                    this.noticeEditorMark = '';
                    this.handleSearch();
                    break;
                default:
            }
        },
        /**
         * 新增入库通知
         */
        async addNotice(noticeData, submitType) {
            try {
                const { code, msg } = await addGrnNotice({
                    ...noticeData,
                    status: submitType - 1
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
            } catch (err) {
                this.$message.error(err.message || '添加失败');
                throw err;
            }
        },
        async editNotice(noticeData, noticeId) {
            try {
                const { code, msg } = await editGrnNotice({
                    ...noticeData,
                    status: 0,
                    sn: noticeId
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
            } catch (err) {
                this.$message.error(err.message || '修改失败');
                throw err;
            }
        },
        /**
         * 批量修改入库通知的状态
         * @param {object[]} statusInfo
         * @param {string} statusInfo[].id 通知的id
         * @param {number} statusInfo[].status 通知的状态1:已收货，2:作废
         */
        async updateNoticeStatus(statusInfo) {
            try {
                const { code, msg } = await updateGrnNoticeStatus(statusInfo);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('操作成功');
            } catch (err) {
                this.$message.error(err.message || '更新失败');
                throw err;
            }
        }
    },
    watch: {
        activeTabKey() {
            this.handleSearch();
            this.selectedRecords = [];
        }
    }
};
</script>
