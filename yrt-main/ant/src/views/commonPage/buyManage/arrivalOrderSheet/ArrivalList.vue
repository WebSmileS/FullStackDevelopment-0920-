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
            <template v-if="listStatuType === arrivalOrderTabMap.HISTORY.value">
                <vxe-table-column title="是否作废" v-bind="tableColumnConfig.yesOrNo">
                    <template v-slot:default="{ row }">
                        {{ row.is_cancel | arrivalOrderCancelTextFilter }}
                    </template>
                </vxe-table-column>
                <vxe-table-column title="作废理由" field="cancel_reason" v-bind="tableColumnConfig.longText">
                    <template v-slot:default="{ row }">
                        {{ row.cancel_reason }}
                    </template>
                </vxe-table-column>
            </template>
            <vxe-table-column title="状态" v-bind="tableColumnConfig.status">
                <template v-slot:default="{ row }">
                    {{ row.arrival_status | arrivalOrderStatusTextFilter }}
                </template>
            </vxe-table-column>
            <vxe-table-column v-bind="tableColumnConfig.description"></vxe-table-column>
        </template>
    </list-page-list>
</template>

<script>
import tableColumnConfig from '@/config/tableColumnConfig';
import { arrivalOrderListAPI } from '@/service/pageAjax';
import ListPageList from '@/components/ListPageList.vue';
import paginationMixin from '@/mixins/pagination';
import { arrivalOrderStatusTextFilter, arrivalOrderCancelTextFilter } from './filters';
import arrivalListColumns from './arrivalListColumns';
import { arrivalOrderTabMap } from './type';

export default {
    name: 'ArrivalList',
    components: {
        ListPageList
    },
    mixins: [paginationMixin],
    filters: { arrivalOrderStatusTextFilter, arrivalOrderCancelTextFilter },
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
            default: arrivalOrderTabMap.EXECUTING.value
        }
    },
    computed: {
        tableColumnConfig() {
            return tableColumnConfig;
        },
        columns() {
            return arrivalListColumns;
        },
        arrivalOrderTabMap() {
            return arrivalOrderTabMap;
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
            const { sn, dateRange = [], billsType, organizationInnerSn, deliveryMenName } = searchValues;
            const [beginDate, endDate] = dateRange;
            let loaderData = {
                history: this.listStatuType === arrivalOrderTabMap.HISTORY.value,
                sn,
                'begin_date': beginDate ? beginDate.format('YYYY-MM-DD') : undefined,
                'end_date': endDate ? endDate.format('YYYY-MM-DD') : undefined,
                bills_type: billsType,
                organization_inner_sn: organizationInnerSn,
                deliverymen_name: deliveryMenName,
                // 质管复核页面，执行中tab页显示的为已收货
                qualityControl: this.managerMode
            };
            if (
                organizationInnerSn === undefined ||
                billsType === undefined ||
                organizationInnerSn === null ||
                billsType === null
            ) {
                loaderData.bills_type = undefined;
                loaderData.organization_inner_sn = undefined;
            }
            return arrivalOrderListAPI(page, pageSize, loaderData);
        }
    }
};
</script>
