<template>
    <home :pageNameType="pageNameType"></home>
</template>

<script>
import { pageType } from '@/views/commonPage/invoiceManage/config/constants';
import Home from './module/index.vue';

export default {
    name: 'inboundSystemInvoice', // 入库发票 = 业务人员使用
    components: {
        Home
    },
    data() {
        return {
            pageNameType: pageType['INBOUND'].value
        };
    }
};
</script>
