<template>
    <order-new :myOrderPropObj="myOrderPropObj"></order-new>
</template>

<script>
import orderNew from './module/orderNew';
export default {
    name: 'purchaseOrder',
    data() {
        return {
            myOrderPropObj: {
                pageType: 0 // 页面类型判断依据： 0-采购订单  1-销售订单
            }
        };
    },
    methods: {},
    components: {
        orderNew
    },
    created() {}
};
</script>

<style lang="less">
.ant-layout-content {
    overflow-y: auto !important;
}
.vxe-grid--toolbar-wrapper {
    display: none;
}
.ant-select {
    .ant-select-selection {
        width: 100%;
    }
    .ant-select-selection__rendered {
        width: 100%;
    }
}
</style>
