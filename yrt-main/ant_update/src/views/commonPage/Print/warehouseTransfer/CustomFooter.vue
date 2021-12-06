<template>
    <div ref="content">
        <div class="footer1">
            <div><div>签收：</div><div class="underline"></div></div>
            <div
                ><div>制单人：</div><div class="underline">{{ orderInfo.making_people_name }}</div></div
            >
            <div><div>使用科室：</div><div class="underline"></div></div>
            <div><div>发货人：</div><div class="underline"></div></div>
            <div><div>送货人：</div><div class="underline"></div></div>
            <div class="pager"
                ><div>第{{ page }}/{{ pageTotal }}页</div></div
            >
        </div>
        <div v-if="typeof orderInfo.description === 'string' && orderInfo.description !== ''" class="footer2">
            <div class="description">备注：{{ orderInfo.description }}</div>
        </div>
        <div class="footer3" border="0" cellpadding="0" width="100%">
            <div class="phone"><span>咨询电话：0871-64101608</span></div>
            <div><span>白联：存根</span></div>
            <div><span>红联：发票附件</span></div>
            <div><span>黄联：随货同行</span></div>
        </div>
        <div class="footer4">
            <div class="text"
                >注：如发现产品短缺，请当天通知，逾期视为正常。医疗器械属特殊商品，非质量问题，概不退换。</div
            >
        </div>
    </div>
</template>

<script>
import Big from 'big.js';

export default {
    name: 'customFooter',
    props: {
        orderInfo: Object,
        currentPageProducts: [],
        allProducts: [],
        page: Number,
        pageTotal: Number
    },
    data() {
        return {
            summation: 0,
            summationCapital: '',
            pageSummation: 0,
            pageSummationCapital: ''
        };
    },
    created() {
        const { allProducts, currentPageProducts } = this;
        let summation = new Big(0);
        let pageSummation = new Big(0);
        for (let i = 0, l = allProducts.length; i < l; i += 1) {
            summation = summation.plus(allProducts[i].money);
        }
        for (let i = 0, l = currentPageProducts.length; i < l; i += 1) {
            pageSummation = pageSummation.plus(currentPageProducts[i].money);
        }
        this.summation = summation.toNumber();
        this.pageSummation = pageSummation.toNumber;
        this.summationCapital = this.$Utils.moneyToCapital(this.summation);
        this.pageSummationCapital = this.$Utils.moneyToCapital(this.pageSummation);
    }
};
</script>
