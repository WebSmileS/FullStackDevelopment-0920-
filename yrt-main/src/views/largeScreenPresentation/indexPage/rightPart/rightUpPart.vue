<template>
    <div>
        <div class="header">
            <div>
                <img class="headerIcon" src="/images/largeScreen/icon/icon22_red.png">
                <span class="headerTitle" style="color:#F4406B">机构预警信息</span>
            </div>
            <a-icon type="plus" style="margin-right: 0.3rem;" @click="clickMore"/>
        </div>
        <div class="warningContainer">
            <div class="warningDesc">
                <div class="warningTitle">预警总数</div>
                <img class="warningImg" src="/images/largeScreen/img/数字@2x.png">
            </div>
            <div id="warningBarChart"></div>
        </div>
        <div class="warningTableContainer">
            <depWarningTable ref="orgWarningTable"/>
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
    BarChart
} from 'echarts/charts'
import {
    CanvasRenderer
} from 'echarts/renderers'

import getBarOptions from '../components/echarts/warningBarChart'
import depWarningTable from '../components/tables/depWarningTable'

export default {
    components: {
        depWarningTable
    },
    methods: {
        /**
         * drawBarChart
         * @abstract 绘制echart表格
         * @param {Array} oegWarnType 机构预警分类统计数据
         */
        drawBarChart (orgWarnType) {
            var barChartDom = document.getElementById('warningBarChart')
            var myBarChart = this.$echarts.init(barChartDom, 'dark')
            var barOption = getBarOptions(orgWarnType)
            myBarChart.setOption(barOption)
            const rate = (document.body.offsetWidth / 2560)
            myBarChart.resize({ width: 383 * rate + 'px', height: 152 * rate + 'px' })
        },
        /**
         * init
         * @abstract 初始化方法，由父组件调用，获取数据并绘制图表和表格
         * @param {Object} 机构预警信息 { rows: [...], total: ...}
         * @param {Array} 机构预警分类统计数据 { type: '...', count: ...}
         */
        init (orgWarnInfo, orgWarnType) {
            // 绘制表格
            this.$refs.orgWarningTable.init(orgWarnInfo.orgWarnInfo)

            // const orgWarnTotal = orgWarnInfo.total
            
            // 绘制柱状图
            this.$echarts.use([TitleComponent, ToolboxComponent, TooltipComponent, GridComponent, LegendComponent, BarChart, CanvasRenderer])
            this.drawBarChart(orgWarnType)
        },
        clickMore () {
            this.$router.replace({path: '/largeScreen/departmentInfo'})
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
    color: #F4406B;
    line-height: 0.36rem;
}

.warningContainer {
    margin-top: 0.24rem;
    height: 1.6rem;
    display: flex;
    flex-direction: row;
}

.warningDesc {
    position: relative;
    margin-left: 0.32rem;
    width: 1.89rem;
    height: 1.52rem;
}

.warningTitle {
    position: absolute;
    left: 0.28rem;
    top: 0.2rem;
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

.warningImg {
    position: absolute;
    left: 0.1rem;
    top: 0.75rem;
    width: 1.48rem;
    height: 0.52rem;
    opacity: 1;
}

#warningBarChart {
    height: 1.52rem;
    width: 3.83rem;
}

.warningTableContainer {
    margin-top: 0.24rem;
    margin-left: 0.32rem;
    height: 2.74rem;
    width: 5.7rem;
    background-color: #0071bc33;
}
</style>
