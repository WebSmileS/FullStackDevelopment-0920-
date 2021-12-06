<template>
    <div>
        <div class="header">
            <div>
                <img class="headerIcon" src="/images/largeScreen/icon/icon11_purple.png">
                <span class="headerTitle">产品预警信息</span>
            </div>
            <a-icon type="plus" style="margin-right: 0.3rem;" @click="clickMore"/>
        </div>
        <div class="product-warning-desc">
            <div class="warning-info">
                <div class="info-total">
                    <div class="warning-title">预警总数</div>
                    <img class="warning-img" src="/images/largeScreen/img/数字_1@2x.png">
                </div>
                <div class="info-chart-legend">
                    <div class="legend-item">
                        <div class="square" style="background: #f7146f;"></div>
                        <div class="legend-title">产品资证预警</div>
                        <div class="legend-quantity">{{ mdrfWarnTotal }}</div>
                    </div>
                    <div class="legend-item">
                        <div class="square" style="background: #2ADEF7;"></div>
                        <div class="legend-title">不良产品预警</div>
                        <div class="legend-quantity">{{ overdueWarnTotal }}</div>
                    </div>
                    <div class="legend-item">
                        <div class="square" style="background: #604AFD;"></div>
                        <div class="legend-title">产品失效预警</div>
                        <div class="legend-quantity">{{ notgoodWarnTotal }}</div>
                    </div>
                </div>
            </div>
            <div id="warning-circle-chart">
            </div>
        </div>
        <div class="product-warning-table">
            <proWarningTable ref="productWarnTable"/>
        </div>
    </div>
</template>

<script>
import getOptions from '../components/echarts/warningPieChart'
import proWarningTable from '../components/tables/proWarningTable'

import {
    TooltipComponent,
    LegendComponent
} from 'echarts/components'
import {
    PieChart
} from 'echarts/charts'
import {
    CanvasRenderer
} from 'echarts/renderers'

export default {
    components: {
        proWarningTable
    },
    data () {
        return {
            mdrfWarnTotal: 0,
            overdueWarnTotal: 0,
            notgoodWarnTotal: 0
        }
    },
    methods: {
        drawChart (productWarnType) {
            this.$echarts.use([TooltipComponent, LegendComponent, PieChart, CanvasRenderer])
            // 获取绘制dom
            var chartOneDom = document.getElementById('warning-circle-chart')
            // 初始化echart
            var myChartOne = this.$echarts.init(chartOneDom, 'dark')
            // 获取echarts Options，并指定颜色
            var myChartOneOption = getOptions(productWarnType)
            // 设置echarts Options
            myChartOne.setOption(myChartOneOption)
            // 调整echarts大小
            const rate = (document.body.offsetWidth / 2560)
            myChartOne.resize({ width: 302 * rate + 'px', height: 262 * rate + 'px' })
        },
        init (productWarnInfo, productWarnType) {
            const productWarnData = productWarnInfo.productWarnInfo
            // const productWarnTotal = productWarnInfo.productWarnTotal

            this.mdrfWarnTotal = productWarnType.mdrfWarnTotal
            this.overdueWarnTotal = productWarnType.overdueWarnTotal
            this.notgoodWarnTotal = productWarnType.notgoodWarnTotal 

            this.$refs.productWarnTable.init(productWarnData)

            this.drawChart(productWarnType)
        },
        clickMore () {
            // this.$router.replace({path: '/largeScreen/productWarningInfo'})
            // 演示ONLY
            this.$router.replace({path: '/largeScreen/productInfo'})
        }
    }
}
</script>

<style scpoed>
.header {
    padding-top: 0.24rem;
    padding-left: 0.32rem;
    display: flex;
    justify-content: space-between;
}

.headerIcon {
    height: 0.26rem;
}

.headerTitle {
    margin-top: auto;
    margin-bottom: auto;
    margin-left: 0.24rem;
    width: 1.8rem;
    height: 0.42rem;
    font-size: 0.3rem;
    font-family: PingFang SC, PingFang SC-Regular;
    font-weight: 400;
    text-align: left;
    color: #8954EF;
    line-height: 0.36rem;
}

.product-warning-desc {
    margin-top: 0.26rem;
    margin-left: 0.32rem;
    height: 2.62rem;
    width: 5.7rem;
    display: flex;
    flex-direction: row;
}

.product-warning-desc .warning-info{
    height: 2.62rem;
    width: 3.08rem;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.product-warning-desc .warning-circle-chart{
    height: 2.62rem;
    width: 3.02rem;
}

.product-warning-desc .warning-info .info-total{
    height: 1.07rem;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.product-warning-desc .warning-info .info-total .warning-title{
    width: 1.12rem;
    height: 0.40rem;
    opacity: 1;
    font-size: 0.28rem;
    font-family: PingFang SC, PingFang SC-Regular;
    font-weight: 400;
    text-align: left;
    color: #9ad5ef;
    line-height: 0.53rem;
}

.product-warning-desc .warning-info .info-total .warning-img{
    margin-top: 0.15rem;
    width: 1.86rem;
    height: 0.52rem;
    opacity: 1;
}

.product-warning-desc .warning-info .info-chart-legend{
    height: 1.12rem;
    width: 2.1rem;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.info-chart-legend .legend-item{
    margin-top: 0.12rem;
    height: 0.32rem;
    width: 2.5rem;
    display: flex;
    flex-direction: row;
    align-items: center;
}

.info-chart-legend .legend-item > div {
    margin-right: 0.08rem;
}

.legend-item .square{
    height: 0.17rem;
    width: 0.17rem;
}

.legend-item .legend-title{
    width: 1.8rem;
    /* height: 0.28rem; */
    opacity: 1;
    font-size: 0.2rem;
    font-family: PingFang SC, PingFang SC-Regular;
    font-weight: 400;
    text-align: left;
    color: #9ad5ef;
    line-height: 0.46rem;
}

.legend-item .legend-quantity{
    width: 0.57rem;
    /* height: 0.32rem; */
    opacity: 1;
    font-size: 0.24rem;
    font-family: Segoe UI, Segoe UI-Regular;
    font-weight: 400;
    text-align: right;
    color: #ffffff;
    line-height: 0.79rem;
}

.product-warning-desc .warning-circle-chart{
    height: 2.62rem;
    width: 2.62rem;
}

.product-warning-table {
    margin-top: 0.1rem;
    margin-left: 0.32rem;
    height: 3.2rem;
    width: 5.7rem;
    background: #0071bc33;
}
</style>
