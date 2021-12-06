<template>
    <list-page-list
        :selectType="selectType"
        :columns="columns"
        :list="list"
        :loading="loading"
        :pagination="pagination"
        @changePagination="handleChangePagination"
        @selectedChange="onSelectedRowsChange"
    >
        <template slot="statuColumn">
            <slot name="statuColumn"></slot>
        </template>
    </list-page-list>
</template>

<script>
import { grnNoticeListAPI, odoNoticeListAPI } from '@/service/pageAjax';
import ListPageList from '@/components/ListPageList.vue';
import paginationMixin from '@/mixins/pagination';
import noticeListColumns from './noticeListColumns';

export default {
    name: 'NoticeList',
    components: {
        ListPageList
    },
    mixins: [paginationMixin],
    props: {
        selectType: {
            type: String,
            default: 'none'
        },
        // 通知列表的类型，入库通知：grn，出库通知：odo
        listType: {
            type: String,
            default: 'grn'
        },
        // 通知列表的状态类型，待处理列表：waiting，历史列表：history
        listStatuType: {
            type: String,
            default: 'waiting'
        }
    },
    data() {
        let columns;
        if (this.listType === 'grn') {
            columns = noticeListColumns.filter((_) => _.key !== 'purchasing_company_name');
        }
        if (this.listType === 'odo') {
            columns = noticeListColumns.filter((_) => _.key !== 'sales_company_name');
        }
        return {
            columns
        };
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
            const { lesseeName, companyName, productName, beginDate, endDate } = searchValues;
            let loaderData = {
                isHistory: this.listStatuType === 'history',
                'begin_date': beginDate ? beginDate.format('YYYY-MM-DD hh:mm:ss') : undefined,
                'dealer_name': lesseeName || undefined,
                'company_name': companyName || undefined,
                'end_date': endDate ? endDate.format('YYYY-MM-DD hh:mm:ss') : undefined,
                'product_name': productName || undefined
            };
            switch (this.listType) {
                case 'grn':
                    return grnNoticeListAPI(page, pageSize, loaderData);
                case 'odo':
                    return odoNoticeListAPI(page, pageSize, loaderData);
            }
        }
    }
};
</script>
