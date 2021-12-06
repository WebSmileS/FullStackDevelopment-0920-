<template>
    <grnOdoManage :orderTypeData="orderTypeData"></grnOdoManage>
</template>

<script>
import { grnAndOdoOrderType } from '@/config/constants';
export default {
    name: 'purchaseGrn',
    data() {
        return {
            orderTypeData: grnAndOdoOrderType['PURCHASEGRN']
            // orderTypeData: grnAndOdoOrderType['PURCHASERETURN']
            // orderTypeData: grnAndOdoOrderType['ALLOTGRN']
            // orderTypeData: grnAndOdoOrderType['SALEODO']
            // orderTypeData: grnAndOdoOrderType['SALERETURN']
            // orderTypeData: grnAndOdoOrderType['ALLOTODO']
            // orderTypeData: grnAndOdoOrderType['GRANTODO']
        };
    },
    components: {
        'grnOdoManage': () => import('@/views/commonPage/warehouseManage/modal/outPutStorageMange')
    }
};
</script>

<style lang="less" scoped></style>
