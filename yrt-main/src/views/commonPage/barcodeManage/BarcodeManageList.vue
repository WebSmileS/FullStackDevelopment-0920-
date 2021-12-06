<template>
    <list-page-list
        ref="list"
        :selectType="selectType"
        :columns="columns"
        :list="list"
        :loading="loading"
        :pagination="pagination"
        @changePagination="handleChangePagination"
        @selectedChange="onSelectedRowsChange"
    ></list-page-list>
</template>

<script>
import { barcodeListAPI } from '@/service/pageAjax';
import ListPageList from '@/components/ListPageList.vue';
import paginationMixin from '@/mixins/pagination';
import barcodeManageListColumns from './barcodeManageListColumns';

export default {
    name: 'BarcodeManageList',
    components: {
        ListPageList
    },
    mixins: [paginationMixin],
    props: {
        selectType: {
            type: String,
            default: 'none'
        }
    },
    computed: {
        columns() {
            return barcodeManageListColumns;
        }
    },
    created() {
        this.handleInitPaginationMixin();
    },
    methods: {
        onSelectedRowsChange(selected) {
            this.$emit('selectedChange', selected);
        },

        handleAddRecords(records) {
            records.forEach((record) => {
                record.product_vendor_name = record.vendor_name;
                record.product_vendor_inner_sn = record.vendor_inner_sn;
                this.list.push(record);
            });
            this.$refs.list.pagination.total += records.length;
        },

        handleDeleteRecord(record) {
            this.$refs.list.handleDeleteRecord(record);
            this.pagination.total -= 1;
            for (let i = 0, l = this.list.length; i < l; i += 1) {
                if (this.list[i] === record) {
                    this.list.splice(i, 1);
                    break;
                }
            }
        },

        /**
         * 加载列表数据
         * 列表数据的加载函数，供paginationMixin调用
         * @param {number} page 页
         * @param {number} pageSize 页大小
         * @param {object | undefined} searchValues 查询参数
         */
        listLoader(page, pageSize, searchValues) {
            const { productName, productVendorName, specification, barcode } = searchValues;
            return barcodeListAPI(page, pageSize, {
                product_name: productName ? productName : undefined,
                product_vendor_name: productVendorName ? productVendorName : undefined,
                specification: specification ? specification : undefined,
                udi_barcode: barcode ? barcode : undefined
            });
        }
    }
};
</script>
