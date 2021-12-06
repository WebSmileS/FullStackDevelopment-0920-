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
import { logisticsListAPI } from '@/service/pageAjax';
import ListPageList from '@/components/ListPageList.vue';
import paginationMixin from '@/mixins/pagination';
import columns from './columns';
import { logisticsModeMap } from '../components/logisticsType';

export default {
    name: 'LogisticsList',
    components: {
        ListPageList
    },
    mixins: [paginationMixin],
    props: {
        selectType: {
            type: String,
            default: 'none'
        },
        // 通知列表的状态类型，待处理列表：waiting，历史列表：history
        listStatuType: {
            type: String,
            default: 'waiting'
        }
    },
    data() {
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
        async listLoader(page, pageSize, searchValues) {
            const { logisticsSn, logisticsMode, beginDate, endDate } = searchValues;
            let loaderData = {
                isHistory: this.listStatuType === 'history',
                'begin_date': beginDate ? beginDate.format('YYYY-MM-DD hh:mm:ss') : undefined,
                'end_date': endDate ? endDate.format('YYYY-MM-DD hh:mm:ss') : undefined,
                'logistics_sn': logisticsSn || undefined,
                'logistics_mode': logisticsMode || undefined
            };
            const result = await logisticsListAPI(page, pageSize, loaderData, {
                orderByColumn: 'created_time',
                isAsc: 'desc'
            });
            if (result && result.code === 0) {
                return {
                    ...result,
                    rows: result.rows.map((item) => ({
                        ...item,
                        logisticsModeText: logisticsModeMap[item['logistics_mode']].text
                    }))
                };
            }
            return result;
        }
    }
};
</script>
