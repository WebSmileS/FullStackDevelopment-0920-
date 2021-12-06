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
                        placeholder="单据编号"
                        v-model="searchValues.orderSn"
                        tooltipContent="默认搜索最近30天的记录，其它时间段可以在高级搜索中进行设置"
                        @search="onHeaderSingleSearch"
                    ></list-page-header-single-searcher>
                </template>
                <template slot="searchFormItems">
                    <list-page-header-input label="单据编号" v-model="searchValues.orderSn"></list-page-header-input>
                    <list-page-header-input label="供货单位" v-model="searchValues.partBName"></list-page-header-input>
                    <list-page-header-input
                        label="厂商名称"
                        v-model="searchValues.productVendorName"
                    ></list-page-header-input>
                    <list-page-header-select
                        label="仓库"
                        allowClear
                        :options="warehouseSearchOptions"
                        v-model="searchValues.warehouseInnerSn"
                    ></list-page-header-select>
                    <list-page-header-input
                        label="产品名称"
                        v-model="searchValues.productName"
                    ></list-page-header-input>
                    <list-page-header-select
                        label="状态"
                        allowClear
                        :options="statusSearchOptions"
                        v-model="searchValues.status"
                    ></list-page-header-select>
                    <list-page-header-input label="采购员" v-model="searchValues.buyerName"></list-page-header-input>
                    <list-page-header-date-range
                        label="日期"
                        :allowClear="false"
                        :defaultValue="searchValues.dateRange"
                        v-model="searchValues.dateRange"
                    ></list-page-header-date-range>
                </template>
            </list-page-header>
        </template>
        <template slot="list">
            <purchase-record-list
                ref="list"
                :reloadWhenSearch="false"
                :searchValues="searchValues"
                @selectedChange="onSelectedChange"
                @rowDoubleClick="onRowDoubleClick"
            ></purchase-record-list>
        </template>
    </list-page-layout>
</template>

<script>
import moment from 'moment';
import ListPageLayout from '@/components/ListPageLayout';
import ListPageHeader from '@/components/ListPageHeader';
import ListPageHeaderSingleSearcher from '@/components/ListPageHeaderSingleSearcher';
import ListPageHeaderInput from '@/components/ListPageHeaderInput';
import ListPageHeaderSelect from '@/components/ListPageHeaderSelect';
import ListPageHeaderDateRange from '@/components/ListPageHeaderDateRange';
import { orderPurchaseRecordExportAPI } from '@/service/pageAjax';
import PurchaseRecordList from './PurchaseRecordList';
import { purchaseRecordSearchStatusMap } from './type';

const buttonsMap = {
    export: { key: 'export', text: '导出Excel', type: 'primary' }
};

export default {
    name: 'PurchaseRecord',
    components: {
        ListPageLayout,
        ListPageHeader,
        ListPageHeaderSingleSearcher,
        ListPageHeaderInput,
        ListPageHeaderSelect,
        ListPageHeaderDateRange,
        PurchaseRecordList
    },
    mixins: [],
    data() {
        return {
            loading: false,
            exportButtonLoading: false,
            searchValues: {
                orderSn: undefined,
                partBName: undefined,
                warehouseInnerSn: undefined,
                productName: undefined,
                productVendorName: undefined,
                buyerName: undefined,
                status: undefined,
                dateRange: [moment().subtract(30, 'days'), moment()]
            }
        };
    },
    computed: {
        statusSearchOptions() {
            return Object.keys(purchaseRecordSearchStatusMap).map((key) => {
                return {
                    value: purchaseRecordSearchStatusMap[key].value,
                    label: purchaseRecordSearchStatusMap[key].text
                };
            });
        },
        warehouseSearchOptions() {
            return this.$store.state.warehouseList;
        },
        operationGroups() {
            return [[buttonsMap.export]];
        },
        headerButtonsStatu() {
            return {
                export: { disabled: false, loading: this.exportButtonLoading }
            };
        }
    },
    methods: {
        /**
         * 简单查询
         */
        onHeaderSingleSearch() {
            this.handleSearch({
                orderSn: this.searchValues.orderSn,
                dateRange: this.searchValues.dateRange
            });
        },

        /**
         * 多条件查询
         */
        onHeaderMultipleSearch() {
            this.handleSearch();
        },

        onRowDoubleClick(row) {
            this.orderEditorTarget = row;
        },

        onHeaderButtonClick(key) {
            switch (key) {
                case 'export':
                    this.exportExcel();
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
            if (this.$refs.list) {
                this.$refs.list.handleSearch(values);
            }
        },

        /**
         * 导出excel
         */
        async exportExcel() {
            try {
                this.exportButtonLoading = true;
                const [startDate, endDate] = this.searchValues.dateRange;
                const { data, headers } = await orderPurchaseRecordExportAPI({
                    status: this.searchValues.status,
                    partA: this.$cookie.get('userbelong'),
                    partB_name: this.searchValues.partBName,
                    order_sn: this.searchValues.orderSn,
                    start_date: startDate && startDate.format('YYYY-MM-DD'),
                    end_date: endDate && endDate.format('YYYY-MM-DD'),
                    product_name: this.searchValues.productName,
                    product_vendor_name: this.searchValues.productVendorName,
                    buyer_name: this.searchValues.buyerName,
                    warehouse_inner_sn: this.searchValues.warehouseInnerSn
                });
                const contentDisposition = headers['content-disposition'] || '';
                const matchedFilenames = contentDisposition.match(/fileName=(.*)/);
                const filename =
                    matchedFilenames && matchedFilenames.length > 1
                        ? decodeURIComponent(matchedFilenames[1])
                        : `${moment().format('YYYY-MM-DD')}.xlsx`;
                this.$Utils.download(data, filename);
            } catch (err) {
                this.$message.error('导出失败');
                console.error(err);
            } finally {
                this.exportButtonLoading = false;
            }
        }
    }
};
</script>
