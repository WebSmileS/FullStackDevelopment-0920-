<template>
    <home :pageNameType="pageNameType"></home>
</template>

<script>
import { pagePaymentAttachmentType } from '@/views/commonPage/invoiceManage/config/constants';
import Home from './module/index.vue';

export default {
    name: 'receiveOrderFinance', // 收款单 = 财务人员使用
    components: {
        Home
    },
    data() {
        return {
            pageNameType: pagePaymentAttachmentType['RECEIVEORDER'].value
        };
    }
};
</script>
