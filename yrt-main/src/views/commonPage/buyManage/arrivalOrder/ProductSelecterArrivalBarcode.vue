<template>
    <product-selecter-barcode
        ref="selecter"
        :columns="columns"
        :searchResult="searchResult"
        :searchLoading="searchLoading"
        @search="onSearch"
        @searchResultChange="onSearchResultChange"
        @submit="onSubmit"
    ></product-selecter-barcode>
</template>

<script>
import ProductSelecterBarcode from '@/components/ProductSelecterBarcode';
import { arrivalOrderProductListBarcodeAPI } from '@/service/pageAjax';
import columns from './productSelecterArrivalBarcodeColumns.js';

export default {
    name: 'ProductSelecterArrivalBarcode',
    props: {
        active: {
            type: Boolean
        },
        billsType: {
            type: String
        },
        organizationInnerSn: {
            type: [Number, String]
        },
        warehouseInnerSn: {
            type: [Number, String]
        }
    },
    components: {
        ProductSelecterBarcode
    },
    data() {
        return {
            columns,
            searchLoading: false,
            searchResult: []
        };
    },
    watch: {
        active(newValue) {
            if (newValue) {
                if (this.$refs.barcodeInput) {
                    this.$refs.barcodeInput.focus();
                }
            }
        }
    },
    methods: {
        onSearch(barcode) {
            this.loadProduct(barcode);
        },
        onSearchResultChange(searchResult) {
            this.searchResult = searchResult;
        },
        onSubmit(data) {
            this.$emit('submit', data);
        },
        handleSubmit() {
            this.$refs.selecter.handleSubmit();
        },
        async loadProduct(barcode) {
            try {
                this.searchLoading = true;
                const { billsType, organizationInnerSn, warehouseInnerSn } = this;
                const {
                    code,
                    msg,
                    info = {}
                } = await arrivalOrderProductListBarcodeAPI({
                    barcode,
                    bills_type: billsType ? billsType : undefined,
                    organization_inner_sn: organizationInnerSn ? organizationInnerSn : undefined,
                    warehouse_inner_sn: warehouseInnerSn ? warehouseInnerSn : undefined
                });
                const { existUDI, usableProductList, msg: msgInner } = info;
                if (code !== 0) {
                    throw new Error(msg || '产品信息获取失败');
                }
                if (!existUDI) {
                    this.$message.warning(msgInner);
                }
                if (usableProductList && usableProductList.length) {
                    this.searchResult = usableProductList;
                } else {
                    this.searchResult = [];
                }
            } catch (err) {
                this.$message.error(err.message);
                throw err;
            } finally {
                this.searchLoading = false;
            }
        }
    }
};
</script>

<style lang="less" scoped>
.barcode-input-wrapper {
    display: flex;
    /deep/ .ant-input {
        flex-grow: 1;
    }
    /deep/ .ant-btn {
        flex-grow: 0;
        width: 80px;
        margin-left: 14px;
    }
}
</style>
