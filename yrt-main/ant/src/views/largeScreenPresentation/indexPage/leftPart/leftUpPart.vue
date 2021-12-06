<template>
    <div>
        <div class="header">
            <img class="headerIcon" src="/images/largeScreen/icon/icon@2x.png">
            <span class="headerTitle">产品类型统计</span>
        </div>
        <div class="chartContainer">
            <div class="pieContainer">
                <div class="pieChart" id="chartOne"/>
                <div class="pieTooltip">25%</div>
                <div class="pieName" style="color:#03C591">检验耗材</div>
                <div class="pieQuantity">217,892</div>
            </div>
            <div class="pieContainer">
                <div class="pieChart" id="chartTwo"/>
                <div class="pieTooltip">14%</div>
                <div class="pieName" style="color:#D19198">骨科耗材</div>
                <div class="pieQuantity">128,576</div>
            </div>
            <div class="pieContainer">
                <div class="pieChart" id="chartThree"/>
                <div class="pieTooltip">09%</div>
                <div class="pieName" style="color:#F3AF43">心脏耗材</div>
                <div class="pieQuantity">74,936</div>
            </div>
            <div class="pieContainer">
                <div class="pieChart" id="chartFour"/>
                <div class="pieTooltip">52%</div>
                <div class="pieName" style="color:#2AC1F7">普通耗材</div>
                <div class="pieQuantity">464,380</div>
            </div>
        </div>
        <div class="backgroundContainer"></div>
    </div>
</template>

<script>
import getOptions from '../components/echarts/pieChart'

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
    },
    mounted () {
        this.drawChart()
    },
    methods: {
        drawChart () {
            this.$echarts.use([TooltipComponent, LegendComponent, PieChart, CanvasRenderer])
            // 获取绘制dom
            var chartOneDom = document.getElementById('chartOne')
            var chartTwoDom = document.getElementById('chartTwo')
            var chartThreeDom = document.getElementById('chartThree')
            var chartFourDom = document.getElementById('chartFour')
            // 初始化echart
            var myChartOne = this.$echarts.init(chartOneDom)
            var myChartTwo = this.$echarts.init(chartTwoDom)
            var myChartThree = this.$echarts.init(chartThreeDom)
            var myChartFour = this.$echarts.init(chartFourDom)
            // 获取echarts Options，并指定颜色
            var myChartOneOption = getOptions('#03C591', 25)
            var myChartTwoOption = getOptions('#EC0256', 14)
            var myChartThreeOption = getOptions('#F8BE01', 9)
            var myChartFourOption = getOptions('#2AC1F7', 52)
            // 设置echarts Options
            myChartOne.setOption(myChartOneOption)
            myChartTwo.setOption(myChartTwoOption)
            myChartThree.setOption(myChartThreeOption)
            myChartFour.setOption(myChartFourOption)
            // 调整echarts大小
            const width = (document.body.offsetWidth / 2560) * 109
            myChartOne.resize({ width: width + 'px', height: width + 'px' })
            myChartTwo.resize({ width: width + 'px', height: width + 'px' })
            myChartThree.resize({ width: width + 'px', height: width + 'px' })
            myChartFour.resize({ width: width + 'px', height: width + 'px' })
        }
    }
}
</script>

<style scoped>
.header {
    padding-top: 0.24rem;
    padding-left: 0.32rem;
    display: flex;
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
    color: #facd20;
    line-height: 0.36rem;
}

.circle {
    width: 5.7rem;
    height: 1.36rem;
    margin-top: 0.2rem;
    display: flex;
}

.pieChart {
    display: block;
    width: 1.09rem;
    height: 1.09rem;
}

.pieTooltip {
    position: absolute;
    margin-left: auto;
    margin-right: auto;
    top: 0.3rem;
    left: 0.3rem;
    width: 0.52rem;
    height: 0.33rem;
    font-size: 0.23rem;
    font-family: PingFang SC, PingFang SC-Regular;
    font-weight: 400;
    text-align: center;
    color: #ffffff;
    line-height: 0.54rem;
}

.pieName {
    margin-top: 0.38rem;
    margin-left: auto;
    margin-right: auto;
    width: 0.88rem;
    height: 0.3rem;
    font-size: 0.22rem;
    font-family: PingFang SC, PingFang SC-Regular;
    font-weight: 400;
    text-align: left;
    color: #03c591;
    line-height: 0.4rem;
}

.pieQuantity {
    margin-top: 0.03rem;
    margin-left: auto;
    margin-right: auto;
    width: 1.02rem;
    height: 0.37rem;
    opacity: 1;
    font-size: 0.26rem;
    font-family: PingFang SC, PingFang SC-Regular;
    font-weight: 400;
    text-align: center;
    color: #ffffff;
    line-height: 0.53rem;
}

.pieContainer {
    height: 2.66rem;
    width: 1.09rem;
    display: flex;
    flex-direction: column;
    margin-right: 0.36rem;
    position: relative;

}

.chartContainer {
    margin-top: 0.26rem;
    margin-left: 0.46rem;
    width: 100%;
    display: flex;
    flex-direction: row;
    position: relative;
}

.backgroundContainer {
    position: absolute;
    top: 2.3rem;
    left: 0.32rem;
    background: #0071BC;
    height: 0.94rem;
    width: 5.7rem;
    opacity: 0.2;
}
</style>
