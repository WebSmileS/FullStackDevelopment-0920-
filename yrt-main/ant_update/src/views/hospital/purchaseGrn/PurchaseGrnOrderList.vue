<template>
    <list-page-list
        ref="list"
        :selectType="selectType"
        :columns="columns"
        :list="list"
        :loading="loading"
        :pagination="pagination"
        v-bind="$attrs"
        @changePagination="handleChangePagination"
        @selectedChange="onSelectedRowsChange"
        @rowDoubleClick="$listeners.rowDoubleClick"
    >
        <template slot="statuColumn">
            <vxe-table-column v-bind="tableColumnConfig.status">
                <template v-slot:default="{ row }">
                    {{ row.status | purchaseGrnOrderStatusTextFilter }}
                </template>
            </vxe-table-column>
            <vxe-table-column v-bind="tableColumnConfig.description"></vxe-table-column>
        </template>
    </list-page-list>
</template>

<script>
import tableColumnConfig from '@/config/tableColumnConfig';
import { purchaseGrnListAPI } from '@/service/pageAjax';
import ListPageList from '@/components/ListPageList.vue';
import paginationMixin from '@/mixins/pagination';
import { purchaseGrnOrderStatusTextFilter } from './filters';
import purchaseGrnOrderListColumns from './purchaseGrnOrderListColumns';

export default {
    name: 'PurchaseGrnOrderList',
    components: {
        ListPageList
    },
    mixins: [paginationMixin],
    filters: { purchaseGrnOrderStatusTextFilter },
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
            return purchaseGrnOrderListColumns;
        }
    },
    created() {
        this.handleInitPaginationMixin();
    },
    methods: {
        onSelectedRowsChange(selected) {
            this.$emit('selectedChange', selected);
        },

        /**
         * 向列表内插入一条记录并选中
         */
        handleInsertOrder(order) {
            this.list.splice(0, 0, order);
            if (this.selectType === 'radio') {
                this.$refs.list.$refs.listTable.setRadioRow(order);
            }
            if (this.selectType === 'checkbox') {
                this.$refs.list.$refs.listTable.setCheckboxRow(order, true);
            }
            this.$emit('selectedChange', [order]);
        },

        /**
         * 更新列表内某条记录的值并选中
         */
        handleUpdateOrder(order) {
            for (let i = 0, l = this.list.length; i < l; i += 1) {
                if (this.list[i].grn_inner_sn === order.grn_inner_sn) {
                    Object.keys(this.list[i]).forEach((key) => {
                        this.list[i][key] = order[key];
                    });
                    this.$emit('selectedChange', [this.list[i]]);
                    break;
                }
            }
        },

        /**
         * 加载列表数据
         */
        listLoader(page, pageSize, searchValues) {
            const {
                dateRange = [],
                grnSn,
                handlerName,
                organizationName,
                productName,
                status,
                warehouseInnerSn
            } = searchValues;
            const [beginDate, endDate] = dateRange;
            const userBelong = this.$cookie.get('userbelong');
            let loaderData = {
                pageSize,
                pageNum: page,
                isAsc: 'desc',
                orderByColumn: 'grn_sn',
                org_id: userBelong,
                begin_date: beginDate ? beginDate.format('YYYY-MM-DD') : undefined,
                end_date: endDate ? endDate.format('YYYY-MM-DD') : undefined,
                grn_sn: grnSn,
                handler_name: handlerName,
                key: organizationName,
                product_name: productName,
                status: status,
                warehouse_model_inner_sn: warehouseInnerSn
            };
            return purchaseGrnListAPI({ params: loaderData });
        }
    }
};
</script>
