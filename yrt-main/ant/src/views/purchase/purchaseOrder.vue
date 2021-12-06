<template>
    <my-order :myOrderPropObj="myOrderPropObj"></my-order>
</template>

<script>
//Need to import from the default: import myOrder from './module/mainOrder';
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
        myOrder
    },
    created() {}
};
</script>

<style lang="less" scoped></style>
