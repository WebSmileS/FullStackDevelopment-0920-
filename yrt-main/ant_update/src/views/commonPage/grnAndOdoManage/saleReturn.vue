<template>
    <grnOdoManage :orderTypeData="orderTypeData"></grnOdoManage>
</template>

<script>
import { grnAndOdoOrderType } from '@/config/constants';
export default {
    name: 'saleReturn',
    data() {
        return {
            orderTypeData: grnAndOdoOrderType['SALERETURN']
        };
    },
    components: {
        'grnOdoManage': () => import('@/views/commonPage/warehouseManage/modal/outPutStorageMange')
    }
};
</script>

<style lang="less" scoped></style>
