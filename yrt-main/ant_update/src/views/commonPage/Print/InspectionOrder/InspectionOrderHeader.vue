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
            >{{ orderInfo.title }}</h1
        >
        <div class="table-header">
            <div class="left">供货单位：{{ orderInfo.organization_name }}</div>
            <div class="middle">验货日期：{{ orderInfo.inspect_time }}</div>
            <div class="right">单据编号：{{ orderInfo.pp_sn }}</div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'InspectionOrderHeader',
    inject: ['baseConfig', 'tableFormatter'],
    props: {
        orderInfo: {
            type: Object,
            default: () => ({})
        }
    }
};
</script>
