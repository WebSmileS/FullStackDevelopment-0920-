import * as echarts from 'echarts/core'

export default function getBarOptions (orgWarnTypes) {
    return {
        backgroundColor: '#0071BC00',
        // tooltip: {
        //     trigger: 'axis'
        // },
        // legend: {
        //     data: ['蒸发量', '降水量']
        // },
        grid: {
            top: '10%',
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'value'
            }
        ],
        yAxis: [
            {
                type: 'category',
                data: ['生产商', '经销商', '回收商', '医院', '科室']
            }
        ],
        series: [
            {
                name: 'one',
                type: 'bar',
                data: [
                    orgWarnTypes.venWarnTotal,
                    orgWarnTypes.deaWarnTotal,
                    orgWarnTypes.recWarnTotal,
                    orgWarnTypes.hosWarnTotal,
                    orgWarnTypes.celWarnTotal
                ],
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(
                        0, 0, 0, 1,
                        [
                            {offset: 0, color: '#00FFFF'},
                            {offset: 1, color: '#4894F2'}
                        ]
                    )
                },
                label: {
                    show: true,
                    position: 'right',
                    valueAnimation: true
                },
                barWidth: '8'
            }
        ]
    }
}
