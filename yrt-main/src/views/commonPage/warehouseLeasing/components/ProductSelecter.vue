<template>
    <base-modal title="选择产品" dragKey="product_selecter_md" :width="1100" :visible="visible" :onClose="onClose">
        <template slot="content">
            <div class="product-selecter-content-wrapper">
                <add-product
                    ref="addProduct"
                    :partB="componentAddProductPartB"
                    metaTitle="产品选择"
                    :contractProductCheckable="false"
                    :warehouseLeasingNoticeType="noticeType"
                    :warehouseLeasingDealerInnerSn="dealerInnerSn"
                    @returnData="onProductsReturn"
                />
            </div>
        </template>
        <template slot="footer">
            <a-button type="default" @click="onClose">取消</a-button>
            <a-button type="primary" @click="onAddButtonClick">添加</a-button>
        </template>
    </base-modal>
</template>

<script>
import AddProduct from '@/components/addProduct.vue';
import BaseModal from '@/components/BaseModal.vue';

export default {
    name: 'ProductSelecter',
    components: {
        AddProduct,
        BaseModal
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        onSubmit: {
            type: Function,
            default: null
        },
        noticeType: {
            type: String,
            required: true,
            validator(value) {
                return ['grn', 'odo'].indexOf(value) !== -1;
            }
        },
        dealerInnerSn: {
            validator(value) {
                return typeof value === 'string' || typeof value === 'number';
            }
        },
        onClose: Function
    },
    methods: {
        onAddButtonClick() {
            if (this.$refs.addProduct && this.$refs.addProduct.returnProList) {
                this.$refs.addProduct.returnProList();
            }
        },
        onProductsReturn(_, vendorProducts) {
            if (this.onSubmit) {
                this.onSubmit([..._, ...vendorProducts]);
            }
        }
    },
    computed: {
        componentAddProductPartB() {
            return {
                modalType: 12,
                billsType: 'DH',
                'org_id': this.$cookie.get('userbelong')
            };
        }
    }
};
</script>

<style lang="less" scoped>
.product-selecter-content-wrapper {
    margin-top: 24px;
    /deep/ .tabs-product-header {
        margin: 0 24px;
    }
}
</style>
