<template>
    <div
        ref="content"
        :style="{
            paddingTop: `${tableFormatter.tableMarginBottom}${baseConfig.unit}`,
            fontSize: `${baseConfig.bodyFontSize}${baseConfig.unit}`,
            lineHeight: `${baseConfig.bodyLineHeight}${baseConfig.unit}`,
            letterSpacing: `${baseConfig.bodyLetterSpacing}${baseConfig.unit}`,
            fontWeight: baseConfig.bodyFontBold ? 'bold' : 'normal'
        }"
    >
        <div class="footer1">
            <div class="float-wrapper float-wrapper-left left-wrapper">
                <div>
                    <span>签收：</span>
                    <span class="underline underline-null">&nbsp;</span>
                </div>
                <div>
                    <span>制单人：</span>
                    <span v-if="orderInfo.making_people_name" class="underline"
                        >&nbsp;{{ orderInfo.making_people_name }}&nbsp;</span
                    >
                    <span v-else class="underline underline-null">&nbsp;</span>
                </div>
                <div>
                    <span>审核人：</span>
                    <span v-if="orderInfo.auditor_name" class="underline"
                        >&nbsp;{{ orderInfo.auditor_name }}&nbsp;</span
                    >
                    <span v-else class="underline underline-null">&nbsp;</span>
                </div>
                <div>
                    <span>使用科室：</span>
                    <span v-if="orderInfo.use_department" class="underline"
                        >&nbsp;{{ orderInfo.use_department }}&nbsp;</span
                    >
                    <span v-else class="underline underline-null">&nbsp;</span>
                </div>
                <div>
                    <span>发货人：</span>
                    <span class="underline underline-null">&nbsp;</span>
                </div>
                <div>
                    <span>送货人：</span>
                    <span class="underline underline-null">&nbsp;</span>
                </div>
            </div>
            <div class="right-wrapper pager">
                <span>第{{ page }}/{{ pageTotal }}页</span>
            </div>
        </div>
        <div v-if="typeof orderInfo.description === 'string' && orderInfo.description !== ''" class="footer2">
            <div class="description">备注：{{ orderInfo.description }}</div>
        </div>
        <div class="footer3">
            <div class="left-wrapper">
                <span class="phone">咨询电话：0871-64101608</span>
            </div>
            <div class="right-wrapper float-wrapper float-wrapper-right">
                <div><span>黄联：随货同行</span></div>
                <div><span>红联：发票附件</span></div>
                <div><span>白联：存根</span></div>
            </div>
        </div>
        <div class="footer4">
            <div class="text"
                >注：如发现产品短缺，请当天通知，逾期视为正常。医疗器械属特殊商品，非质量问题，概不退换。</div
            >
        </div>
    </div>
</template>

<script>
export default {
    name: 'customFooter',
    inject: ['baseConfig', 'tableFormatter'],
    props: {
        orderInfo: {
            type: Object,
            default: () => ({})
        },
        currentPageProducts: {
            type: Array,
            default: () => []
        },
        allProducts: {
            type: Array,
            default: () => []
        },
        page: {
            type: Number,
            default: 1
        },
        pageTotal: {
            type: Number,
            default: 1
        }
    }
};
</script>
