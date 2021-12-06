<template>
    <inbound-finance biz-type="stock" />
</template>

<script>
import InboundFinance from './inbound';

export default {
    name: 'finance-stock',

    components: {
        InboundFinance
    }
};
</script>
