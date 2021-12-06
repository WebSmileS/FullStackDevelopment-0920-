<template>
    <list-page-horizontal-split>
        <template v-slot:top>
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
            ></list-page-list>
        </template>
        <template v-slot:bottom>
            <advance-sale-order-detail
                ref="detailTable"
                :targetOrder="detailTarget"
                @orderFinish="onOrderFinish"
            ></advance-sale-order-detail>
        </template>
    </list-page-horizontal-split>
</template>

<script>
import tableColumnConfig from '@/config/tableColumnConfig';
import { advanceSaleOrderListAPI } from '@/service/advanceSale';
import ListPageList from '@/components/ListPageList.vue';
import ListPageHorizontalSplit from '@/components/ListPageHorizontalSplit.vue';
import paginationMixin from '@/mixins/pagination';
import AdvanceSaleOrderDetail from './AdvanceSaleOrderDetail';
import advanceSaleOrderListColumns from './advanceSaleOrderListColumns.js';
import { advanceSaleOrderAuditStatusMap, advanceSaleOrderStatusMap, advanceSaleOrderStatusTextMap } from './type';

export default {
    name: 'AdvanceSaleOrderList',
    components: {
        ListPageList,
        ListPageHorizontalSplit,
        AdvanceSaleOrderDetail
    },
    mixins: [paginationMixin],
    /**
     * isFinance:
     * 是否为财务人员使用，财务人员使用时只有查看功能，
     * 并且列表内容为已审核单据
     */
    inject: ['isFinance'],
    props: {
        selectType: {
            type: String,
            default: 'none'
        }
    },
    data() {
        return {
            detailTarget: undefined
        };
    },
    computed: {
        tableColumnConfig() {
            return tableColumnConfig;
        },
        columns() {
            return advanceSaleOrderListColumns;
        },
        advanceSaleOrderStatusTextMap() {
            return advanceSaleOrderStatusTextMap;
        }
    },
    created() {
        this.handleInitPaginationMixin();
    },
    methods: {
        onSelectedRowsChange(selected) {
            this.$emit('selectedChange', selected);
            this.detailTarget = selected[0];
        },

        /**
         * 单据明细产品均已售完或回库
         * @param {number} 单据id
         */
        onOrderFinish(id) {
            const list = this.list;
            for (let i = 0, l = list.length; i < l; i += 1) {
                if (list[i].id === id) {
                    list[i].status = advanceSaleOrderStatusMap.COMPLETED;
                    break;
                }
            }
        },

        /**
         * 加载列表数据
         */
        listLoader(page, pageSize, searchValues) {
            const { advanceSaleNo, dateRange = [], organizationInnerSn, organizationName, auditStatus } = searchValues;
            const [beginDate, endDate] = dateRange;
            let loaderData = {
                advanceSaleNo,
                buyerId: organizationInnerSn,
                buyerName: organizationName,
                auditStatus: auditStatus,
                begin_date: beginDate ? beginDate.format('YYYY-MM-DD') : undefined,
                end_date: endDate ? endDate.format('YYYY-MM-DD') : undefined
            };
            this.detailTarget = undefined;
            // 财务人员只加载已审核的单据
            if (this.isFinance) {
                loaderData.auditStatus = advanceSaleOrderAuditStatusMap.APPROVED;
            }
            return advanceSaleOrderListAPI(page, pageSize, loaderData);
        }
    }
};
</script>
