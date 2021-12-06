<template>
    <grnOdoManage :orderTypeData="orderTypeData" companyName="供货单位"></grnOdoManage>
</template>

<script>
import { grnAndOdoOrderType } from '@/config/constants';
export default {
    name: 'purchaseReturn',
    data() {
        return {
            orderTypeData: grnAndOdoOrderType['PURCHASERETURN']
        };
    },
    components: {
        'grnOdoManage': () => import('@/views/commonPage/warehouseManage/modal/outPutStorageMange')
    }
};
</script>

<style lang="less" scoped></style>
