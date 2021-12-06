<template>
    <outPutStorageSheet
        :orderTypeData="orderTypeData"
        companyName="供货单位"
        :myListPropObj="{}"
    ></outPutStorageSheet>
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
        'outPutStorageSheet': () => import('@/views/commonPage/warehouseManage/modal/outPutStorageSheet')
    }
};
</script>

<style lang="less" scoped></style>
