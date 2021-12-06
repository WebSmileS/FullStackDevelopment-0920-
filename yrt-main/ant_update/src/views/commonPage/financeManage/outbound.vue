<template>
    <inbound-finance biz-type="outbound" />
</template>

<script>
import InboundFinance from './inbound';

export default {
    name: 'finance-outbound',

    components: {
        InboundFinance
    }
};
</script>
