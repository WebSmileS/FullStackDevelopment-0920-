<template>
    <list-page-layout>
        <template slot="header">
            <list-page-header
                :operationGroups="operationGroups"
                :buttonsStatu="headerButtonsStatu"
                :searchFormItems="searchFormItems"
                @btnClick="onHeaderButtonClick"
                @search="handleSearch"
            />
        </template>
        <template slot="list">
            <list-page-list
                selectType="checkbox"
                :columns="columns"
                :pagination="pagination"
                :list="lesseeListData"
                :loading="loading"
                @changePagination="handleChangePagination"
                @selectedChange="onSelectedRowsChange"
            >
                <template slot="operationColumn">
                    <vxe-table-column title="操作" width="12%">
                        <template v-slot:default="{ row }">
                            <a-button size="small" type="primary" @click.stop="onCertificateClick(row)">资证</a-button>
                        </template>
                    </vxe-table-column>
                </template>
            </list-page-list>
        </template>
        <template slot="modals">
            <dealer-selecter
                title="新增租户-选择经销商"
                :visible="lesseesSelecterVisible"
                :onSubmit="onLesseesSelecterSubmit"
                :onClose="onLesseesSelecterClose"
            />
            <lessee-detail
                title="租户详情"
                :visible="modalsVisible.lesseeDetail"
                :orgId="idForLesseeDetailScan"
                :onClose="() => handleCloseModals('lesseeDetail')"
            />
        </template>
    </list-page-layout>
</template>

<script>
import { lesseeListAPI, addLesseeAPI, deleteLesseesAPI } from '@/service/pageAjax';
import ListPageLayout from '@/components/ListPageLayout.vue';
import ListPageHeader from '@/components/ListPageHeader.vue';
import ListPageList from '@/components/ListPageList.vue';
import modalsMixin from '@/mixins/modals';
import DealerSelecter from '@/components/DealerSelecter.vue';
import LesseeDetail from '../components/LesseeDetail/LesseeDetail.vue';
import columns from './columns';

export default {
    name: 'LesseeManage',
    components: {
        ListPageLayout,
        ListPageHeader,
        ListPageList,
        DealerSelecter,
        LesseeDetail
    },
    mixins: [modalsMixin],
    data() {
        return {
            /* 租户列表 */
            columns,
            searchValues: {
                lesseeName: '' // 租户名称
            },
            loading: false,
            lesseeListData: [],
            selectedRows: [],
            pagination: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },

            /* 新增租户选择modal */
            lesseesSelecterVisible: false,

            idForLesseeDetailScan: ''
        };
    },
    computed: {
        operationGroups() {
            return [
                [
                    { key: 'add', text: '新增', type: 'primary' },
                    { key: 'delete', text: '删除', type: 'danger' }
                ]
            ];
        },
        headerButtonsStatu() {
            return {
                add: { disabled: false },
                delete: { disabled: this.selectedRows.length < 1, reason: '至少选中1条记录才能进行删除' }
            };
        },
        searchFormItems() {
            return [{ type: 'text', key: 'lesseeName', label: '租户名称', placeholder: '租户名称' }];
        }
    },
    methods: {
        /**
         * 搜索
         */
        handleSearch(values) {
            this.searchValues = { ...this.searchValues, ...values };
            this.loadLessees();
        },

        /**
         * 分页导航
         */
        handleChangePagination(pagination) {
            this.pagination = { ...this.pagination, ...pagination };
            this.loadLessees();
        },

        /**
         * 删除当前选中的租户
         */
        handleDeleteSelectedLessees() {
            const { selectedRows } = this;
            this.$confirm({
                title: '删除确认',
                content: `确定要删除当前已选中的租户吗？（已选中${selectedRows.length}个）`,
                onOk: async () => {
                    const { code } = await deleteLesseesAPI(selectedRows.map((_) => _.id));
                    if (code === 0) {
                        this.$message.success('删除成功');
                        this.loadLessees();
                        this.selectedRows = [];
                    } else {
                        this.$message.error('删除失败');
                    }
                }
            });
        },

        /**
         * 顶部栏的按钮操作发生时
         * @param {string} type 操作类型标识
         * @param {object} event 事件对象
         */
        onHeaderButtonClick(type) {
            switch (type) {
                case 'add':
                    this.lesseesSelecterVisible = true;
                    break;
                case 'delete':
                    this.handleDeleteSelectedLessees();
                    break;
                default:
            }
        },

        /**
         * 资证按钮点击时
         * @param {object} row 操作对应的行数据
         */
        onCertificateClick(row) {
            this.idForLesseeDetailScan = row['dealer_inner_sn'];
            this.handleOpenModals('lesseeDetail');
        },

        /**
         * 列表内记录的选中发生变化时
         */
        onSelectedRowsChange(selected) {
            this.selectedRows = [...selected];
        },

        /**
         * 选择租户模态框关闭时
         */
        onLesseesSelecterClose() {
            this.lesseesSelecterVisible = false;
        },

        /**
         * 选择租户模态框进行提交时
         * @param {array} selected 已选择的租户
         */
        async onLesseesSelecterSubmit(selected) {
            try {
                await this.addLessees(selected);
                this.lesseesSelecterVisible = false;
            } catch (err) {
                this.$message.error('提交失败');
                throw err;
            }
        },

        /**
         * 加载租户列表
         * @param {boolean} isDefault 是否为默认加载
         * 如默认加载：不带查询条件，加载第一页，每页10条
         * 否则：带当前查询条件以及当前分页状态进行加载
         */
        async loadLessees(isDefault = false) {
            let params = {
                page: 1,
                pageSize: 10
            };
            let data = new FormData();
            if (!isDefault) {
                const {
                    searchValues: { lesseeName },
                    pagination: { currentPage, pageSize }
                } = this;
                params = {
                    pageSize,
                    page: currentPage
                };
                if (lesseeName) {
                    data.append('leaseName', lesseeName);
                }
            }
            this.loading = true;
            const { total, rows } = await lesseeListAPI(params.page, params.pageSize, data);
            this.lesseeListData = rows;
            this.pagination = {
                ...this.pagination,
                total,
                currentPage: params.page,
                pageSize: params.pageSize
            };
            this.loading = false;
        },

        async addLessees(lessees) {
            try {
                const { code } = await addLesseeAPI(lessees[0]['org_id']);
                if (code === 0) {
                    this.$message.success('新增成功');
                    this.loadLessees();
                    return;
                }
                throw new Error();
            } catch (err) {
                this.$message.error('新增失败');
                throw err;
            }
        }
    },
    mounted() {
        this.loadLessees(true);
    }
};
</script>
