<template>
    <list-page-list
        ref="listTable"
        :selectType="selectType"
        :columns="columns"
        :list="list"
        :loading="loading"
        :pagination="pagination"
        @changePagination="handleChangePagination"
        @selectedChange="onSelectedRowsChange"
        @rowDoubleClick="$listeners.rowDoubleClick"
    >
        <template slot="statuColumn">
            <template v-if="listStatuType === inspectionOrderTabMap.HISTORY.value">
                <vxe-table-column v-bind="tableColumnConfig.yesOrNo" title="是否作废" field="is_cancel">
                    <template v-slot:default="{ row }">
                        {{ row.is_cancel | inspectionOrderCancelTextFilter }}
                    </template>
                </vxe-table-column>
                <vxe-table-column v-bind="tableColumnConfig.longText" title="作废理由" field="cancel_reason">
                    <template v-slot:default="{ row }">
                        {{ row.cancel_reason }}
                    </template>
                </vxe-table-column>
            </template>
            <vxe-table-column v-bind="tableColumnConfig.status">
                <template v-slot:default="{ row }">
                    {{ row.inspection_status | inspectionOrderStatusTextFilter }}
                </template>
            </vxe-table-column>
            <vxe-table-column v-bind="tableColumnConfig.description"></vxe-table-column>
        </template>
    </list-page-list>
</template>

<script>
import tableColumnConfig from '@/config/tableColumnConfig';
import { inspectionOrderListAPI } from '@/service/pageAjax';
import ListPageList from '@/components/ListPageList.vue';
import paginationMixin from '@/mixins/pagination';
import { inspectionOrderStatusTextFilter, inspectionOrderCancelTextFilter } from './filters';
import inspectionOrderListColumns from './inspectionOrderListColumns';
import { inspectionOrderTabMap } from './type';

export default {
    name: 'InspectionOrderList',
    components: {
        ListPageList
    },
    mixins: [paginationMixin],
    filters: { inspectionOrderStatusTextFilter, inspectionOrderCancelTextFilter },
    props: {
        selectType: {
            type: String,
            default: 'none'
        },
        managerMode: {
            type: Boolean,
            default: false
        },
        // 通知列表的状态类型
        listStatuType: {
            type: String,
            default: inspectionOrderTabMap.EXECUTING.value
        }
    },
    computed: {
        tableColumnConfig() {
            return tableColumnConfig;
        },
        columns() {
            return inspectionOrderListColumns;
        },
        inspectionOrderTabMap() {
            return inspectionOrderTabMap;
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
         * 加载列表数据
         */
        listLoader(page, pageSize, searchValues) {
            const { sn, dateRange = [], billsType, organizationInnerSn } = searchValues;
            const [beginDate, endDate] = dateRange;
            let loaderData = {
                history: this.listStatuType === inspectionOrderTabMap.HISTORY.value,
                sn,
                'begin_date': beginDate ? beginDate.format('YYYY-MM-DD') : undefined,
                'end_date': endDate ? endDate.format('YYYY-MM-DD') : undefined,
                bills_type: billsType,
                organization_inner_sn: organizationInnerSn,
                // 质管复核页面，执行中tab页显示的为已验货
                qualityControl: this.managerMode
            };
            if (
                billsType === undefined ||
                organizationInnerSn === undefined ||
                billsType === null ||
                organizationInnerSn === null
            ) {
                loaderData.bills_type = undefined;
                loaderData.organization_inner_sn = undefined;
            }
            return inspectionOrderListAPI(page, pageSize, loaderData);
        }
    }
};
</script>
