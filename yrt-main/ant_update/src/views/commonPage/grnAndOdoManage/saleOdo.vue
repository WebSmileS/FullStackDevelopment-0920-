<template>
    <grnOdoManage :orderTypeData="orderTypeData"></grnOdoManage>
</template>

<script>
import { grnAndOdoOrderType } from '@/config/constants';
export default {
    name: 'saleOdo',
    data() {
        return {
            orderTypeData: grnAndOdoOrderType['SALEODO']
        };
    },
    components: {
        'grnOdoManage': () => import('@/views/commonPage/warehouseManage/modal/outPutStorageMange')
    }
};
</script>

<style lang="less" scoped></style>
