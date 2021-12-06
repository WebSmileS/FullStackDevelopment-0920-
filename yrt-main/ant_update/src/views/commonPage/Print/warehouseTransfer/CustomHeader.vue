<template>
    <div ref="content">
        <h1 class="title">{{ orderInfo.org_name }}{{ orderInfo.type_name }}</h1>
        <p class="sub-title">源仓库：{{ orderInfo.source_warehouse_name }}</p>
        <p class="sub-title">目标仓库：{{ orderInfo.target_warehouse_name }}</p>
        <div class="table-header">
            <div class="middle">录单日期：{{ orderInfo.out_time }}</div>
            <div class="right">单据编号：{{ orderInfo.av_sn }}</div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'customHeader',
    props: {
        orderInfo: Object
    },
    mounted() {}
};
</script>
