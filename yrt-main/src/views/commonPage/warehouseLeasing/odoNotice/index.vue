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
                <a-tab-pane key="waiting" tab="待发货">
                    <notice-list
                        listType="odo"
                        listStatuType="waiting"
                        selectType="checkbox"
                        :searchValues="searchValues"
                        :reloadWhenSearch="activeTabKey === 'waiting'"
                        @selectedChange="onSelectedChange"
                    >
                        <template slot="statuColumn">
                            <vxe-table-column width="7%" align="center" title="状态">
                                <template v-slot:default="{ row }">
                                    <span :class="statusTypeMap[row['status']].color">{{
                                        statusTypeMap[row['status']].text
                                    }}</span>
                                </template>
                            </vxe-table-column>
                        </template>
                    </notice-list>
                </a-tab-pane>
                <a-tab-pane key="history" tab="历史">
                    <notice-list
                        listType="odo"
                        listStatuType="history"
                        selectType="checkbox"
                        :searchValues="searchValues"
                        :reloadWhenSearch="activeTabKey === 'history'"
                        @selectedChange="onSelectedChange"
                    >
                        <template slot="statuColumn">
                            <vxe-table-column width="7%" align="center" title="状态">
                                <template v-slot:default="{ row }">
                                    <span :class="statusTypeMap[row['status']].color">{{
                                        statusTypeMap[row['status']].text
                                    }}</span>
                                </template>
                            </vxe-table-column>
                        </template>
                    </notice-list>
                </a-tab-pane>
            </a-tabs>
        </template>
        <template slot="modals">
            <notice-editor
                title="出库通知"
                submit2Text="发货"
                noticeType="odo"
                :visible="modalsVisible.noticeEditor"
                :defaultData="defaultNoticeEditorData"
                :productAddAble="noticeEditorMark !== 'edit'"
                :onClose="() => handleCloseModals('noticeEditor')"
                :onSubmit="onNoticeEditorSubmit"
            />
            <logistics-editor
                title="物流详情"
                :visible="modalsVisible.logisticsEditor"
                :defaultData="defaultLogisticsEditorData"
                :onClose="() => handleCloseModals('logisticsEditor')"
                :onSubmit="onLogisticsEditorSubmit"
            />
        </template>
    </list-page-layout>
</template>

<script>
import { addLogisticsAPI, addOdoNotice, editOdoNotice, updateOdoNoticeStatus } from '@/service/pageAjax';
import ListPageLayout from '@/components/ListPageLayout.vue';
import ListPageHeader from '@/components/ListPageHeader.vue';
import tabsMixin from '@/mixins/tabs';
import modalsMixin from '@/mixins/modals';
import NoticeList from '../components/NoticeList.vue';
import NoticeEditor from '../components/NoticeEditor.vue';
import LogisticsEditor from '../components/LogisticsEditor.vue';

const statusTypeMap = {
    'TO_BE_DELIVER': { value: 'TO_BE_DELIVER', text: '待发货', color: 'orange' },
    'DELIVER': { value: 'DELIVER', text: '已发货', color: 'green' },
    'CANCEL': { value: 'CANCEL', text: '已作废', color: 'gray' },
    'FINISH': { value: 'FINISH', text: '已完成', color: 'green' }
};

export default {
    name: 'OdoNotice',
    components: {
        ListPageLayout,
        ListPageHeader,
        NoticeList,
        NoticeEditor,
        LogisticsEditor
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
            defaultLogisticsEditorData: null,
            statusTypeMap
        };
    },
    computed: {
        operationGroups() {
            const result = [
                [
                    { key: 'add', text: '新增', type: 'primary' },
                    // 备注：目前修改功能后端未实现
                    // { key: 'edit', text: '修改', type: 'primary' }
                    { key: 'confirm', text: '发货', type: 'primary' },
                    { key: 'nullify', text: '作废', type: 'danger' }
                ],
                [{ key: 'addLogistics', text: '新增物流', type: 'primary' }]
            ];
            return result;
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false },
                // 备注：目前修改功能后端未实现
                // edit: { disabled: false, reason: '' },
                confirm: { loading: this.loading, disabled: false, reason: '' },
                nullify: { loading: this.loading, disabled: false, reason: '' },
                addLogistics: { loading: this.loading, disabled: false, reason: '' }
            };
            if (this.selectedRecords.length < 1) {
                result.confirm.disabled = true;
                result.nullify.disabled = true;
                result.addLogistics.disabled = true;
                result.confirm.reason =
                    result.nullify.reason =
                    result.addLogistics.reason =
                        '至少选中1条记录才能进行操作';
            }
            if (this.activeTabKey !== 'waiting') {
                result.confirm.disabled = result.nullify.disabled = true;
                result.confirm.reason = result.nullify.reason = '待处理记录才能进行操作';
            }
            if (this.activeTabKey !== 'history') {
                result.addLogistics.disabled = true;
                result.addLogistics.reason = '历史记录才能进行操作';
            }
            return result;
        },
        searchFormItems() {
            return [
                { type: 'text', key: 'lesseeName', span: 6, label: '租户名称', placeholder: '租户名称' },
                { type: 'text', key: 'companyName', span: 6, label: '采购公司' },
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
            const { pick } = this.$XEUtils;
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
                        product: pick(record, [
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
                        const targets = this.selectedRecords;
                        await this.updateNoticeStatus(targets.map((_) => ({ id: _.id, status: 1 })));
                        this.selectedRecords = [];
                        this.handleSearch();
                        this.$confirm({
                            title: '打开物流记录编辑',
                            content: '使用刚才操作的通知单进行物流记录编辑吗？',
                            onOk: () => {
                                this.handleUpdateDefaultLogisticsEditorData(targets);
                                this.handleOpenModals('logisticsEditor');
                            }
                        });
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
                case 'addLogistics': {
                    const logistics = this.selectedRecords.filter(
                        (_) => _.status !== statusTypeMap.CANCEL.value && _.status !== statusTypeMap.FINISH.value
                    );
                    if (logistics.length > 0) {
                        if (logistics.length !== this.selectedRecords.length) {
                            this.$message.warning('已自动忽略已完成和已作废的记录');
                        }
                        this.handleUpdateDefaultLogisticsEditorData(logistics);
                        this.handleOpenModals('logisticsEditor');
                        break;
                    }
                    if (this.selectedRecords.length > 0) {
                        this.$message.error('已作废和已完成的记录不能再生成物流信息');
                    }
                    break;
                }
            }
        },
        onSelectedChange(selected) {
            this.selectedRecords = selected;
        },
        handleUpdateDefaultLogisticsEditorData(sourceData) {
            this.defaultLogisticsEditorData = {
                notices: sourceData.map((notice) => {
                    return this.$XEUtils.pick(notice, [
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
        },
        /**
         * 通知编辑提交时
         * 备注：目前修改功能后端未实现
         */
        async onNoticeEditorSubmit(noticeData, submitType) {
            switch (this.noticeEditorMark) {
                case 'add': {
                    const result = await this.addNotice(noticeData, submitType);
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
                        if (this.activeTabKey !== 'history') {
                            this.activeTabKey = 'history';
                        } else {
                            this.handleSearch();
                        }
                        this.$message.success('发货成功');
                        this.$confirm({
                            title: '打开物流记录编辑',
                            content: '使用刚才操作的通知单进行物流记录编辑吗？',
                            onOk: () => {
                                this.handleUpdateDefaultLogisticsEditorData(result);
                                this.handleOpenModals('logisticsEditor');
                            }
                        });
                    }
                    break;
                }
                case 'edit': {
                    await this.editNotice(noticeData, this.defaultNoticeEditorData.id);
                    this.$message.success('修改成功');
                    this.handleCloseModals('noticeEditor');
                    this.defaultNoticeEditorData = null;
                    this.noticeEditorMark = '';
                    this.handleSearch();
                    break;
                }
            }
        },
        /**
         * 物流编辑提交时
         */
        async onLogisticsEditorSubmit(logisticsData) {
            try {
                const { code, msg } = await addLogisticsAPI(logisticsData);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('物流信息添加成功');
                this.handleCloseModals('logisticsEditor');
                this.defaultLogisticsEditorData = null;
            } catch (err) {
                this.$message.error(err.message);
                throw err;
            }
        },
        /**
         * 新增出库通知
         */
        async addNotice(noticeData, submitType) {
            try {
                const { code, msg, list } = await addOdoNotice({
                    ...noticeData,
                    status: submitType - 1
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                return list;
            } catch (err) {
                this.$message.error(err.message || '添加失败');
                throw err;
            }
        },
        async editNotice(noticeData, noticeId) {
            try {
                const { code, msg } = await editOdoNotice({
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
         * 批量修改出库通知的状态
         * @param {object[]} statusInfo
         * @param {string} statusInfo[].id 通知的id
         * @param {number} statusInfo[].status 通知的状态1:已收货，2:作废
         */
        async updateNoticeStatus(statusInfo) {
            try {
                const { code, msg } = await updateOdoNoticeStatus(statusInfo);
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
