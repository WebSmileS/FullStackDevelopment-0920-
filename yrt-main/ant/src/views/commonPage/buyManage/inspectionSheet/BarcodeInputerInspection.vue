<template>
    <barcode-inputer ref="inputer" :loading="searchLoading" @search="onSearch"></barcode-inputer>
</template>

<script>
import BarcodeInputer from '@/components/BarcodeInputer';
import barcodeScanAPIResultParser from '@/mixins/barcodeScanAPIResultParser.js';
import { inspectionOrderProductListBarcodeAPI } from '@/service/pageAjax';

export default {
    name: 'BarcodeInputerInspection',
    components: {
        BarcodeInputer
    },
    mixins: [barcodeScanAPIResultParser],
    props: {
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
    data() {
        return {
            searchLoading: false,
            searchResult: []
        };
    },
    methods: {
        focus() {
            this.$refs.inputer.$refs.barcodeInput.focus();
        },
        onSearch(barcode) {
            this.loadProduct(barcode);
        },
        handleSubmit() {
            this.$emit('submit', [...this.searchResult]);
        },
        async loadProduct(barcode) {
            try {
                this.searchLoading = true;
                const { billsType, organizationInnerSn, warehouseInnerSn } = this;
                const {
                    code,
                    msg,
                    info = {}
                } = await inspectionOrderProductListBarcodeAPI({
                    barcode,
                    bills_type: billsType ? billsType : undefined,
                    organization_inner_sn: organizationInnerSn ? organizationInnerSn : undefined,
                    warehouse_inner_sn: warehouseInnerSn ? warehouseInnerSn : undefined
                });
                if (code !== 0) {
                    throw new Error(msg || '产品信息获取失败');
                }
                try {
                    const products = this._mixinParseBarcodeResult(info);
                    this.searchResult = products;
                    if (products.length) {
                        this.handleSubmit();
                    }
                } catch (err) {
                    this.$message.warning(err.message);
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
