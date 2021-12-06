<template>
    <div
        ref="content"
        :style="{
            paddingBottom: `${tableFormatter.tableMarginTop}${baseConfig.unit}`,
            fontSize: `${baseConfig.bodyFontSize}${baseConfig.unit}`,
            lineHeight: `${baseConfig.bodyLineHeight}${baseConfig.unit}`,
            letterSpacing: `${baseConfig.bodyLetterSpacing}${baseConfig.unit}`,
            fontWeight: baseConfig.bodyFontBold ? 'bold' : 'normal'
        }"
    >
        <h1
            class="title"
            :style="{
                fontSize: `${baseConfig.titleFontSize}${baseConfig.unit}`,
                lineHeight: `${baseConfig.titleLineHeight}${baseConfig.unit}`,
                letterSpacing: `${baseConfig.titleLetterSpacing}${baseConfig.unit}`,
                fontWeight: baseConfig.titleFontBold ? 'bold' : 'normal'
            }"
            >{{ orderInfo.org_name }}{{ orderInfo.type_name }}</h1
        >
        <p class="sub-title">收货仓库：{{ orderInfo.warehouse_name }}</p>
        <div class="table-header">
            <div class="left">发货单位：{{ orderInfo.organization_name }}</div>
            <div class="middle">录单日期：{{ orderInfo.create_time }}</div>
            <div class="right">单据编号：{{ orderInfo.grn_sn }}</div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'customHeader',
    inject: ['baseConfig', 'tableFormatter'],
    props: {
        orderInfo: {
            type: Object,
            default: () => ({})
        }
    }
};
</script>
