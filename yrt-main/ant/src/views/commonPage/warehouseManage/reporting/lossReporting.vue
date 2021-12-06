<template>
    <list-page-layout isTabListPage class="invertory-management">
        <template slot="header">
            <list-page-header
                needMixedSearch
                :operationGroups="operationGroups"
                :buttonsStatu="headerButtonsStatu"
                @btnClick="onHeaderButtonClick"
                @search="getSearchData"
                @expandsChange="handleToggleSearchStatus"
            >
                <template slot="singleSearcher">
                    <list-page-header-single-searcher
                        slot="singleSearcher"
                        placeholder="请输入产品进行查询"
                        v-model="searchValues.product_name"
                        @search="getSearchData"
                    ></list-page-header-single-searcher>
                </template>
                <template slot="searchFormItems">
                    <list-page-header-select
                        label="仓库"
                        allowClear
                        v-model="searchValues.warehouse_inner_sn"
                        :options="warehouseOptions"
                    ></list-page-header-select>
                    <list-page-header-input
                        label="产品"
                        v-model="searchValues.product_name"
                        @keyDownEnter="getSearchData"
                    ></list-page-header-input>
                    <list-page-header-input
                        label="规格型号"
                        v-model="searchValues.specification"
                        @keyDownEnter="getSearchData"
                    ></list-page-header-input>
                    <list-page-header-input
                        label="批号"
                        v-model="searchValues.batch_number"
                        @keyDownEnter="getSearchData"
                    ></list-page-header-input>
                    <list-page-header-modal-input
                        label="相关机构"
                        :value="product_vendor_inner_name"
                        @input="organizationSelecter = true"
                        @clear="onSearchFormOrganizationClear"
                    ></list-page-header-modal-input>
                </template>
            </list-page-header>
        </template>
        <template slot="list">
            <list-page-list
                :columns="columnsLevelComputed"
                :list="actualInventoryDatas"
                :loading="actualInventoryLoading"
            ></list-page-list>
        </template>
        <template slot="modals">
            <organization-selecter
                title="选择厂商"
                destroyOnClose
                :visible="organizationSelecter"
                organizationType="ven"
                @close="organizationSelecter = false"
                @submit="onSearchFormOrganizationSelected"
            ></organization-selecter>
            <!-- 新增编辑弹框 -->
            <model-edit :visible="modelEditVisible" @close="modelEditClose"></model-edit>
        </template>
    </list-page-layout>
</template>

<script>
import Big from 'big.js';
import ListPageLayout from '@/components/ListPageLayout';
import ListPageHeader from '@/components/ListPageHeader';
import ListPageList from '@/components/ListPageList.vue';
import ListPageHeaderSingleSearcher from '@/components/ListPageHeaderSingleSearcher';
import ListPageHeaderInput from '@/components/ListPageHeaderInput';
import ListPageHeaderSelect from '@/components/ListPageHeaderSelect';
import ListPageHeaderModalInput from '@/components/ListPageHeaderModalInput';
import OrganizationSelecter from '@/components/OrganizationSelecter';
import moment from 'moment';
import modelEdit from './modelEdit.vue';
import { inventoryListLevelAPI, exportFinanceStockListAPI } from '@/service/pageAjax';
import { level0Columns, level1Columns, level2Columns, modelingColumns } from '../inventoryColumns';

export default {
    name: 'inventory',
    components: {
        ListPageLayout,
        ListPageHeader,
        ListPageHeaderSingleSearcher,
        ListPageHeaderInput,
        ListPageHeaderSelect,
        ListPageHeaderModalInput,
        OrganizationSelecter,
        ListPageList,
        modelEdit
    },
    props: {
        // 管理模式下，部分功能需要过滤处理
        managerMode: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            // 顶部左侧按钮
            operationGroups: [
                [
                    { key: 'add', text: '新增', type: 'primary' },
                    { key: 'edit', text: '修改', type: 'primary' },
                    { key: 'delete', text: '删除', type: 'danger' }
                ]
            ],
            isExpands: false, //是否显示高级搜索
            searchValues: {
                org_id: this.$cookie.get('userbelong'),
                orderByColumn: '',
                warehouse_inner_sn: '',
                product_name: '',
                specification: '',
                batch_number: '',
                product_vendor_inner_sn: ''
            },
            product_vendor_inner_name: '',

            level: 3, //查询级别1-4

            organizationSelecter: false, //是否显示选择厂商弹框

            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            activeTabKey: 0,

            // 实际
            currentTrObj: {},
            actualInventoryLoading: true, //表格loading
            actualInventoryDatas: [], //表格内容
            tableSortStatus: {},
            actualInventoryDatasArr: '',

            // 新增弹框
            modelEditVisible: false
        };
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.isExpands ? 214 : 154;
            return clientHeight - topHeight;
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false },
                edit: { disabled: false, reason: '' },
                delete: { disabled: false, reason: '' }
            };
            return result;
        },
        warehouseOptions() {
            return this.$store.state.warehouseList.map((warehouse) => ({
                label: warehouse.name,
                value: warehouse.warehouse_model_inner_sn
            }));
        },

        // 计算表头
        columnsLevelComputed() {
            let columns = [];
            switch (parseFloat(this.level)) {
                case 1:
                    columns = level0Columns;
                    break;
                case 2:
                    columns = level1Columns;
                    break;
                case 3:
                    columns = level2Columns;
                    break;
            }
            return columns.map((column) => {
                if (column.cellRender) {
                    switch (column.field) {
                        case 'product_vendor_name':
                            return {
                                ...column,
                                cellRender: { ...column.cellRender, order: this.tableSortStatus[column.field] }
                            };
                        case 'product_name':
                            return {
                                ...column,
                                cellRender: { ...column.cellRender, order: this.tableSortStatus[column.field] }
                            };
                        case 'specification':
                            return {
                                ...column,
                                cellRender: { ...column.cellRender, order: this.tableSortStatus[column.field] }
                            };
                    }
                }
                return column;
            });
        }
    },
    created() {
        this.getSearchData();
    },
    methods: {
        // 此功能正在开发中提示
        tips() {
            return this.$warning({
                title: '操作提示',
                content: '该功能在紧张开发中，敬请期待',
                okText: '知道了',
                centered: true,
                maskClosable: false
            });
        },

        // 顶部按钮点击
        onHeaderButtonClick(key) {
            switch (key) {
                case 'add': //新增
                    this.modelEditVisible = true;
                    break;
                case 'edit': //修改
                    this.tips();
                    break;
                case 'delete': //删除
                    this.tips();
                    break;
            }
        },

        // 导出Excel
        async downloadExcel() {
            const hideMessage = this.$message.loading('导出中...');
            try {
                const formData = {
                    org_id: this.$cookie.get('userbelong'),
                    orderByColumn: Object.keys(this.tableSortStatus)
                        .filter((key) => !!this.tableSortStatus[key])
                        .map((key) => `${key}+${this.tableSortStatus[key]}`)
                        .join(','),
                    ...this.searchTables
                };
                const { data, headers } = await exportFinanceStockListAPI(formData);
                const contentDisposition = headers['content-disposition'] || '';
                const matchedFilenames = contentDisposition.match(/fileName=(.*)/);
                const filename =
                    matchedFilenames.length > 1
                        ? decodeURIComponent(matchedFilenames[1])
                        : `${moment().format('YYYY-MM-DD')}.xlsx`;
                this.$Utils.download(data, filename);
            } catch (error) {
                console.error(error);
            } finally {
                hideMessage();
            }
        },

        // 显示隐藏高级搜索
        handleToggleSearchStatus(value) {
            this.isExpands = value;
        },

        // 查询
        getSearchData() {
            let params = this.searchValues;
            this.actualInventoryLoading = true;
            inventoryListLevelAPI(this.level, { params })
                .then((res) => {
                    this.actualInventoryLoading = false;
                    if (parseInt(res.code) === 0) {
                        this.actualInventoryDatasArr = res.list;
                        this.actualInventoryDatas = res.list;

                        this.actualInventoryDatasArr.forEach((item) => {
                            item.storage_price = new Big(item.unit_price).times(item.quantity).toNumber(4);
                        });

                        this.actualInventoryDatas.map((item) => {
                            return {
                                ...item,
                                key: Math.random(),
                                activeClass: '',
                                storage_price: this.$Utils.commafy(new Big(item.unit_price).times(item.quantity)),
                                unit_price: this.$Utils.commafy(new Big(item.unit_price).round(4).toString()),
                                status_text: parseFloat(item.status) <= 0 ? '无效' : '有效',
                                type_text: parseFloat(item.type) <= 0 ? '常规' : '赠品'
                            };
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.actualInventoryLoading = false;
                });
        },

        // 选择厂商提交
        onSearchFormOrganizationSelected(data) {
            this.searchValues.product_vendor_inner_sn = data[0].organizationInnerSn;
            this.product_vendor_inner_name = data[0].organizationName;
            this.organizationSelecter = false;
        },

        // 相关机构取消
        onSearchFormOrganizationClear() {
            this.searchValues.product_vendor_inner_s = '';
            this.product_vendor_inner_name = '';
        },

        // 表格部 ///////
        cellStyle({ row }) {
            // 判断是否过期，过期产品用红色字体表示
            if (row.expire) {
                return {
                    color: '#f00'
                };
            }
        },

        // 表格单击和双击
        cellClickFn({ row }) {
            this.currentTrObj = row;
        },
        cellDblclickFn({ row }) {
            this.currentTrObj = row;
            // this.level === 4 && this.previewLevel4Fn()
        },
        onTableSortChange({ property, order }) {
            this.$set(this.tableSortStatus, property, order);
            this.initTablesFn();
        },

        // 初始化数据【搜索按钮】
        initTablesFn() {
            this.getSearchData();
        },

        // 新增弹框关闭
        modelEditClose() {
            this.modelEditVisible = false;
        }
    }
};
</script>
