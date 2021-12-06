<template>
    <div style="margin: 0 24px; padding-bottom: 12px">
        <add-product
            ref="addProduct"
            :partB="partB"
            :contractProductCheckable="false"
            :isWarehouseDisabled="true"
            @returnData="onSubmit"
        ></add-product>
    </div>
</template>

<script>
import AddProduct from '@/components/addProduct';

/**
 * 平台产品选择器
 * @todo 考虑废弃，使用其它业务逻辑更细分的组件
 */
export default {
    name: 'ProductSelecterPlatform',
    components: {
        AddProduct
    },
    props: {
        /**
         * 机构业务类型
         */
        billsType: {
            type: String
        },
        /**
         * 机构id
         */
        organizationInnerSn: {
            type: [Number, String]
        }
    },
    computed: {
        partB() {
            const systemType = this.$cookie.get('userSystemType');
            return {
                systemType,
                modalType: 7, // 注意，“平台产品”使用入库平台产品接口（business/grn/vendor/list）
                org_id: this.organizationInnerSn,
                billsType: this.billsType
            };
        }
    },
    methods: {
        onSubmit(dealerProducts, vendorProducts) {
            this.$emit('submit', vendorProducts);
            this.$emit('close');
        },
        handleSubmit() {
            this.$refs.addProduct.returnProList();
        }
    }
};
</script>
