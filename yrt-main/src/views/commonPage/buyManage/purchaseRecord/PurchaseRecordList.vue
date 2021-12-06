<template>
    <list-page-list
        :selectType="selectType"
        :columns="columns"
        :list="list"
        :loading="loading"
        :pagination="pagination"
        :rowClassName="listTableRowClassName"
        @changePagination="handleChangePagination"
        @selectedChange="onSelectedRowsChange"
        @rowDoubleClick="$listeners.rowDoubleClick"
    >
        <template slot="statuColumn">
            <vxe-table-column v-bind="tableColumnConfig.status">
                <template v-slot:default="{ row }">
                    {{ row.record_status | purchaseRecordStatusTextFilter }}
                </template>
            </vxe-table-column>
        </template>
    </list-page-list>
</template>

<script>
import tableColumnConfig from '@/config/tableColumnConfig';
import { orderPurchaseRecordListAPI } from '@/service/pageAjax';
import ListPageList from '@/components/ListPageList.vue';
import paginationMixin from '@/mixins/pagination';
import { purchaseRecordStatusMap } from './type';
import { purchaseRecordStatusTextFilter } from './filters';
import purchaseRecordListColumns from './purchaseRecordListColumns';

export default {
    name: 'PurchaseRecordList',
    components: {
        ListPageList
    },
    mixins: [paginationMixin],
    filters: { purchaseRecordStatusTextFilter },
    props: {
        selectType: {
            type: String,
            default: 'none'
        }
    },
    computed: {
        tableColumnConfig() {
            return tableColumnConfig;
        },
        columns() {
            return purchaseRecordListColumns;
        }
    },
    created() {
        this.handleInitPaginationMixin();
    },
    methods: {
        onSelectedRowsChange(selected) {
            this.$emit('selectedChange', selected);
        },

        listTableRowClassName({ row }) {
            if (row.record_status === purchaseRecordStatusMap.TO_BE_VERIFY.value) {
                return 'un-certificated';
            }
            if (row.record_status === purchaseRecordStatusMap.EXECUTING.value) {
                return 'in-process';
            }
            return '';
        },

        /**
         * 加载列表数据
         * 列表数据的加载函数，供paginationMixin调用
         * @param {number} page 页
         * @param {number} pageSize 页大小
         * @param {object | undefined} searchValues 查询参数
         */
        listLoader(page, pageSize, searchValues) {
            const {
                orderSn,
                dateRange = [],
                partBName,
                productName,
                productVendorName,
                warehouseInnerSn,
                buyerName,
                status
            } = searchValues;
            const [startDate, endDate] = dateRange;
            return orderPurchaseRecordListAPI(
                {
                    status,
                    order_sn: orderSn,
                    partA: this.$cookie.get('userbelong'),
                    partB_name: partBName,
                    product_name: productName,
                    product_vendor_name: productVendorName,
                    warehouse_inner_sn: warehouseInnerSn,
                    buyer_name: buyerName,
                    start_date: startDate ? startDate.format('YYYY-MM-DD') : undefined,
                    end_date: endDate ? endDate.format('YYYY-MM-DD') : undefined
                },
                page,
                pageSize
            );
        }
    }
};
</script>
