<template>
    <base-modal title="选择产品" dragKey="product_selecter_md" :width="1100" :visible="visible" :onClose="handleClose">
        <template slot="content">
            <a-tabs style="margin-top: 12px" type="card" v-model="activeTabKey">
                <a-tab-pane :key="tabsKeyMap.CUSTOM" tab="可用产品">
                    <slot name="customProductSelecter"></slot>
                </a-tab-pane>
                <template v-if="platformProductUseable">
                    <a-tab-pane :key="tabsKeyMap.PLATFORM" tab="平台产品">
                        <div style="margin: 0 24px; padding-bottom: 12px">
                            <add-product
                                metaTitle="产品选择"
                                ref="platformProduct"
                                :partB="componentAddProductPartB"
                                :orderTypeData="orderTypeData"
                                :contractProductCheckable="false"
                                @returnData="onPlatformProductsReturn"
                            />
                        </div>
                    </a-tab-pane>
                </template>
            </a-tabs>
        </template>
        <template slot="footer">
            <a-button type="default" @click="handleClose">取消</a-button>
            <a-button type="primary" @click="onAddButtonClick">添加</a-button>
        </template>
    </base-modal>
</template>

<script>
import AddProduct from '@/components/addProduct.vue';
import BaseModal from '@/components/BaseModal.vue';
import tabsMixin from '@/mixins/tabs';
import { grnAndOdoOrderType } from '@/config/constants';

const tabsKeyMap = Object.freeze({
    CUSTOM: 'c',
    PLATFORM: 'p'
});

/**
 * 产品选择器
 * @todo 将被弃用
 */
export default {
    name: 'ProductSelecter',
    components: {
        AddProduct,
        BaseModal
    },
    mixins: [tabsMixin],
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        // 平台产品是否可用，可用时，平台产品选项卡才会被显示
        platformProductUseable: {
            type: Boolean,
            default: true
        }
    },
    data() {
        return {
            tabsKeyMap,
            activeTabKey: tabsKeyMap.CUSTOM
        };
    },
    computed: {
        /**
         * 用于addProduct组件
         */
        componentAddProductPartB() {
            return {
                modalType: 7,
                billsType: 'DH',
                'org_id': this.$cookie.get('userbelong')
            };
        },
        /**
         * 用于addProduct组件
         */
        orderTypeData() {
            return { ...grnAndOdoOrderType.PURCHASEGRN };
        }
    },
    methods: {
        /**
         * 点击添加按钮时
         */
        onAddButtonClick() {
            if (this.activeTabKey === tabsKeyMap.PLATFORM) {
                this.$refs.platformProduct.returnProList && this.$refs.platformProduct.returnProList();
            }
            if (this.activeTabKey === tabsKeyMap.CUSTOM) {
                if (
                    this.$slots.customProductSelecter &&
                    this.$slots.customProductSelecter[0] &&
                    this.$slots.customProductSelecter[0].componentInstance &&
                    this.$slots.customProductSelecter[0].componentInstance.handleSubmit
                ) {
                    this.$slots.customProductSelecter[0].componentInstance.handleSubmit();
                }
            }
        },

        /**
         * 平台产品返回值时
         */
        onPlatformProductsReturn(dealerProducts, vendorProducts) {
            this.$emit('submit', {
                platformProducts: {
                    dealer: dealerProducts.map((product) => ({
                        ...product,
                        product_vendor_name: product.vendor_name ? product.vendor_name : product.product_vendor_name,
                        product_vendor_inner_sn: product.vendor_name
                            ? product.vendor_inner_sn
                            : product.product_vendor_inner_sn
                    })),
                    vendor: vendorProducts.map((product) => ({
                        ...product,
                        product_vendor_name: product.vendor_name ? product.vendor_name : product.product_vendor_name,
                        product_vendor_inner_sn: product.vendor_name
                            ? product.vendor_inner_sn
                            : product.product_vendor_inner_sn
                    }))
                }
            });
        },

        /**
         * 收货单可用产品返回值时
         */
        onCustomProductsReturn(products) {
            this.$emit('submit', {
                customProducts: products.map((product) => ({
                    ...product,
                    vendor_name: product.product_vendor_name,
                    vendor_inner_sn: product.product_vendor_inner_sn
                }))
            });
        },

        handleClose() {
            this.$emit('close');
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
