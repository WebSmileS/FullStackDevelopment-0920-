<template>
    <advance-payment-index></advance-payment-index>
</template>

<script>
import advancePaymentIndex from './module/index';

export default {
    name: 'advancePayment', // 预付款 = 业务人员使用
    components: {
        advancePaymentIndex
    },
    data() {
        return {};
    }
};
</script>
