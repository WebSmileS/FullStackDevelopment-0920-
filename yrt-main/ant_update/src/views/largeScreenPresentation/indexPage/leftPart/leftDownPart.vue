<template>
    <div>
        <div class="header">
            <div>
                <img class="headerIcon" src="/images/largeScreen/icon/icon33_green.png">
                <span class="headerTitle">产品交易统计</span>
            </div>
            <a-icon type="plus" style="margin-right: 0.3rem;" @click="clickMore"/>
        </div>  
        <div class="chartContainer">
            <div class="floatChartContainer">
                <div class="chartHeader">
                    <span class="chartHeaderTitle">统计时间段</span>
                    <span class="chartHeaderTime">
                        2021.03.26-2021.03.31
                    </span>
                </div>
                <div id="floatChart"></div>
            </div>
            <div class="stripChartContainer" style="margin-top:0.34rem">
                <div class="chartHeader">
                    <span class="chartHeaderTitle">统计时间段</span>
                    <span class="chartHeaderTime">
                        2020.10-2021.03
                    </span>
                </div>
                <div id="barChart"></div>
            </div>
        </div>
        <div class="tableContainer">
            <productDealTable/>
        </div>
    </div>
</template>

<script>
import {
    TitleComponent,
    ToolboxComponent,
    TooltipComponent,
    GridComponent,
    LegendComponent
} from 'echarts/components'
import {
    LineChart,
    BarChart
} from 'echarts/charts'
import {
    CanvasRenderer
} from 'echarts/renderers'

import getOptions from '../components/echarts/floatChart'
import getBarOptions from '../components/echarts/stripChart'
import productDealTable from '../components/tables/productDealTable'

export default {
    mounted () {
        this.$echarts.use([TitleComponent, ToolboxComponent, TooltipComponent, GridComponent, LegendComponent, LineChart, BarChart, CanvasRenderer])
        this.drawFloatChart()
        this.drawBarChart()
    },
    components: {
        productDealTable
    },
    methods: {
        drawFloatChart () {
            var chartDom = document.getElementById('floatChart')
            var myChart = this.$echarts.init(chartDom, 'dark')
            var option = getOptions()
            const rate = (document.body.offsetWidth / 2560)
            myChart.setOption(option)
            myChart.resize({ width: 552 * rate + 'px', height: 199 * rate + 'px' })
        },
        drawBarChart () {
            var barChartDom = document.getElementById('barChart')
            var myBarChart = this.$echarts.init(barChartDom, 'dark')
            var barOption = getBarOptions()
            const rate = (document.body.offsetWidth / 2560)
            myBarChart.setOption(barOption)
            myBarChart.resize({ width: 552 * rate + 'px', height: 253 * rate + 'px' })
        },
        clickMore () {
            this.$router.replace({path: '/largeScreen/productContractInfo'})
        }
    }
}
</script>

<style scoped>
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
    margin-left: 0.24rem;
    width: 1.8rem;
    height: 0.42rem;
    font-size: 0.3rem;
    font-family: PingFang SC, PingFang SC-Regular;
    font-weight: 400;
    text-align: left;
    color: #0FE18A;
    line-height: 0.36rem;
}

.chartHeader {
  display: flex;
  flex-direction: column;
  margin-right: 0.32rem;
  margin-bottom: 0.1rem;
}

.chartHeaderTitle {
  /* this auto keeps the title on 
     the right of the flexBox */
  margin-left: auto;
  width: 1rem;
  height: 0.28rem;
  font-size: 0.2rem;
  font-family: PingFang SC, PingFang SC-Regular;
  font-weight: 400;
  text-align: right;
  color: #9ad5ef;
  line-height: 0.2rem;
}

.chartHeaderTime {
  /* this auto keeps the title on 
     the right of the flexBox */
  margin-left: auto;
  width: 1.81rem;
  height: 0.22rem;
  font-size: 0.16rem;
  font-family: PingFang SC, PingFang SC-Regular;
  font-weight: 400;
  text-align: right;
  color: #ffffff;
  line-height: 0.2rem;
}

#floatChart {
  margin-left: auto;
  margin-right: 0.32rem;
  width: 5.58rem;
  height: 2.02rem;
}

#barChart {
  margin-left: auto;
  margin-right: 0.32rem;
  width: 5.52rem;
  height: 2.53rem;
}

.tableContainer {
  margin-left: 0.4rem;
  margin-top: 0.3rem ;
  width: 5.7rem;
  height: 2.01rem;
  background-color: #0071bc33;
}
</style>
