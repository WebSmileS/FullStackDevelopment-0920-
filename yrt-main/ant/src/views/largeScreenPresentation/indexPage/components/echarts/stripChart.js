import * as echarts from 'echarts/core'

export default function getBarOptions () {
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
                type: 'category',
                data: ['10月', '11月', '12月', '1月', '2月', '3月']
            }
        ],
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: [
            {
                name: 'one',
                type: 'bar',
                data: [78, 67, 72, 70, 57, 68],
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(
                        0, 0, 0, 1,
                        [
                            {offset: 0, color: '#00FFFF'},
                            {offset: 1, color: '#4894F2'}
                        ]
                    )
                },
                barWidth: '8'
            },
            {
                name: 'two',
                type: 'bar',
                data: [40, 38, 49, 61, 38, 42],
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(
                        0, 0, 0, 1,
                        [
                            {offset: 0, color: '#D19198'},
                            {offset: 1, color: '#4894F2'}
                        ]
                    )
                },
                barWidth: '8'
            },
            {
                name: 'three',
                type: 'bar',
                data: [92, 67, 73, 80, 49, 32],
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(
                        0, 0, 0, 1,
                        [
                            {offset: 0, color: '#9799E3'},
                            {offset: 1, color: '#4894F2'}
                        ]
                    )
                },
                barWidth: '8'
            },
            {
                name: 'four',
                type: 'bar',
                data: [77, 83, 80, 90, 78, 77],
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(
                        0, 0, 0, 1,
                        [
                            {offset: 0, color: '#FBB03B'},
                            {offset: 1, color: '#4894F2'}
                        ]
                    )
                },
                barWidth: '8',
                barGap: '0.5'
            }
        ]
    }
}
