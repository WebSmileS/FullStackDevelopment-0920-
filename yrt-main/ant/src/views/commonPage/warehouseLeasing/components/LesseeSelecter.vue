<template>
    <item-selecter-list-modal
        :title="title"
        :visible="visible"
        :width="width"
        :height="height"
        :selectType="selectType"
        :columns="columns"
        :dataLoader="loadDealer"
        :onClose="onClose"
        :onSubmit="onSubmit"
    />
</template>

<script>
import ItemSelecterListModal from '@/components/ItemSelecterListModal.vue';
import { lesseeListAPI } from '@/service/pageAjax';

const columns = [
    {
        title: '机构名称',
        dataIndex: 'dealer_name',
        width: '25%',
        align: 'center'
    },
    {
        title: '别名',
        dataIndex: 'aname',
        width: '25%',
        align: 'center'
    },
    {
        title: '统一社会信用代码',
        dataIndex: 'social_credit_code',
        width: '40%',
        align: 'center'
    }
];

export default {
    name: 'LesseeSelecter',
    components: {
        ItemSelecterListModal
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        width: {
            type: Number,
            default: 600
        },
        height: {
            type: Number,
            default: 400
        },
        title: {
            type: String,
            default: '租户选择'
        },
        selectType: {
            type: String,
            default: 'none'
        },
        columns: {
            type: Array,
            default: () => columns
        },
        loaderParams: {
            type: Object,
            default: () => ({})
        },
        onSubmit: {
            type: Function,
            default: null
        },
        onClose: {
            type: Function,
            default: null
        }
    },
    methods: {
        /**
         * 加载经销商
         * @param {object} params
         */
        async loadDealer(params) {
            let data = new FormData();
            if (params.searchValue) {
                data.append('leaseName', params.searchValue);
            }
            return lesseeListAPI(params.page, params.pageSize, data);
        }
    }
};
</script>
