
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
        <template slot="modals">
            <organization-selecter
                title="选择厂商"
                destroyOnClose
                :visible="organizationSelecter"
                organizationType="ven"
                @close="organizationSelecter = false"
                @submit="onSearchFormOrganizationSelected"
            ></organization-selecter>
        </template>
        <template slot="list">
            <a-tabs type="card" v-model="activeTabKey">
                <a-tab-pane :key="0" tab="实际">
                    <list-page-list
                        :columns="inventoryColumns"
                        :list="actualInventoryDatas"
                        :loading="actualInventoryLoading"
                        :is-page="false"
                        :footer-method-column="['unit_price', 'storage_price', 'quantity']"
                        :sort-config="{ remote: true }"
                        :rowClassName="tableRowClassName"
                        :cellStyle="cellStyle"
                        @sort-change="onTableSortChange"
                        @selectedChange="selectedChange"
                    ></list-page-list>
                </a-tab-pane>
                <a-tab-pane :key="1" tab="在途" disabled>
                    <list-page-list
                        :columns="modelingColumns"
                        :list="modelingData"
                        :loading="warehouseModelingLoading"
                        :is-page="false"
                        :footer-method-column="['unit_price', 'storage_price', 'quantity']"
                        :sort-config="{ remote: true }"
                        :rowClassName="tableRowClassName"
                        :cellStyle="cellStyle"
                        @sort-change="onTableSortChange"
                        @selectedChange="selectedChange"
                    ></list-page-list>
                </a-tab-pane>
            </a-tabs>
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
import { inventoryListLevelAPI, exportFinanceStockListAPI } from '@/service/pageAjax';
import { inventoryColumns, modelingColumns } from './inventoryColumns';

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
        ListPageList
    },
    data() {
        return {
            // 顶部左侧按钮
            operationGroups: [
                [
                    { key: 'library', text: '移库', type: 'primary' },
                    { key: 'position', text: '移位', type: 'primary' },
                    { key: 'sell', text: '拆零', type: 'primary' },
                    { key: 'hatSales', text: '停售', type: 'danger' },
                    { key: 'relieve', text: '停售解除', type: 'danger' },
                    { key: 'excel', text: '导出Excel', type: 'primary' }
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
            actualInventoryDatasArr: [],

            // 在途
            modelingData: [],
            warehouseModelingLoading: true
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
                library: { disabled: false },
                position: { disabled: false, reason: '' },
                sell: { disabled: false, reason: '' },
                hatSales: { disabled: false, reason: '' },
                relieve: { disabled: false, reason: '' },
                excel: { disabled: false, reason: '' }
            };
            return result;
        },
        warehouseOptions() {
            return this.$store.state.warehouseList.map((warehouse) => ({
                label: warehouse.name,
                value: warehouse.warehouse_model_inner_sn
            }));
        },

        // 实际tbale columns
        inventoryColumns() {
            return inventoryColumns;
        },

        // 在途table colums
        modelingColumns() {
            return modelingColumns;
        }
    },
    created() {
        this.getSearchData();
    },
    methods: {
        tableRowClassName({ row }) {
            if (row.expire) {
                return 'red';
            }
            return undefined;
        },
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
                case 'library': //移库
                    this.tips();
                    break;
                case 'position': //移位
                    this.tips();
                    break;
                case 'sell': //拆零
                    this.tips();
                    break;
                case 'hatSales': //停售
                    this.tips();
                    break;
                case 'relieve': //移位
                    this.tips();
                    break;
                case 'excel': //导出Excel
                    this.downloadExcel();
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
                        .map((key) => `${key} ${this.tableSortStatus[key]}`)
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
            params.orderByColumn = Object.keys(this.tableSortStatus)
                .filter((key) => !!this.tableSortStatus[key])
                .map((key) => `${key} ${this.tableSortStatus[key]}`)
                .join(',');
            inventoryListLevelAPI(this.level, { params })
                .then((res) => {
                    this.actualInventoryLoading = false;
                    if (parseInt(res.code) === 0) {
                        this.actualInventoryDatasArr = res.list;
                        this.actualInventoryDatas = res.list;

                        this.actualInventoryDatasArr.forEach((item) => {
                            item.storage_price = new Big(item.unit_price).times(item.quantity).toNumber(4);
                        });

                        this.actualInventoryDatas = this.actualInventoryDatas.map((item) => {
                            return {
                                ...item,
                                key: Math.random(),
                                activeClass: '',
                                storage_price: this.$Utils.commafy(
                                    new Big(item.unit_price).times(item.quantity).round(4).toString()
                                ),
                                unit_price: this.$Utils.commafy(new Big(item.unit_price).round(4).toString())
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

        // 关闭厂商弹框
        handleCloseModals() {
            this.organizationSelecter = false;
        },

        // 选择厂商提交
        onSearchFormOrganizationSelected(data) {
            this.searchValues.product_vendor_inner_sn = data[0].organizationInnerSn;
            this.product_vendor_inner_name = data[0].organizationName;
            this.organizationSelecter = false;
        },

        // 相关机构取消
        onSearchFormOrganizationClear() {
            this.searchValues.product_vendor_inner_sn = '';
            this.product_vendor_inner_name = '';
        },

        // 判断是否过期，过期产品用红色字体表示
        cellStyle({ row }) {
            if (row.expire) {
                return {
                    color: '#f00'
                };
            }
        },
        cellDblclickFn({ row }) {
            this.currentTrObj = row;
            // this.level === 4 && this.previewLevel4Fn()
        },
        onTableSortChange({ property, order }) {
            this.$set(this.tableSortStatus, property, order);
            this.getSearchData();
        },

        // 单击表格
        selectedChange(row) {
            console.log(row);
        },

        // ---------------------------在途 ----------------------------
        // 表格的高亮样式显示
        rowClassFn(rowData) {
            if (rowData.key === this.currentTrObj.key) {
                return 'easytable-active-tr';
            }
        }
    }
};
</script>
