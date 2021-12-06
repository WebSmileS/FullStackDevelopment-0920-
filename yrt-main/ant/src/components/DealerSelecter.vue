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
        v-on="$listeners"
    />
</template>

<script>
import ItemSelecterListModal from '@/components/ItemSelecterListModal.vue';
import columns from '@/components/dealerSelecterColumns';
import { getVendorList } from '@/service/pageAjax';

/**
 * 经销商选择器，
 * 将被弃用，请使用OrganizationSelecter
 * @todo 从项目移除
 */
export default {
    name: 'DealerSelecter',
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
            default: 600
        },
        title: {
            type: String,
            default: '经销商选择'
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
            let newParams = {
                ...this.loaderParams,
                pageSize: params.pageSize,
                pageNum: params.page,
                'is_certifie': true
            };
            if (params.searchValue) {
                newParams.name = params.searchValue;
            }
            return getVendorList(2, { params: newParams });
        }
    }
};
</script>
