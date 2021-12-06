import * as echarts from 'echarts/core'
export default function getOptions () {
    return {
        backgroundColor: '#0071BC00',
        color: ['#03C591', '#D19198', '#F8BE01', '#2AC1F7'],
        legend: {
            data: ['检验耗材', '骨科耗材', '心脏耗材', '普通耗材'],
            align: 'left',
            right: '0%',
            icon: 'rect'
        },
        grid: {
            top: '10%',
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [{
            type: 'category',
            data: ['03.26', '03.27', '03.28', '03.29', '03.30', '03.31'],
            boundaryGap: false
        }],
        yAxis: [{
            type: 'value'
        }],
        series: [{
            name: '检验耗材',
            type: 'line',
            smooth: true,
            lineStyle: {
                width: 1
            },
            showSymbol: true,
            areaStyle: {
                opacity: 0.3,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: '#03C591'
                }, {
                    offset: 1,
                    color: '#0071BC00'
                }])
            },
            emphasis: {
                focus: 'series'
            },
            data: [900, 730, 501, 664, 790, 740, 650]
        },
        {
            name: '骨科耗材',
            type: 'line',
            smooth: true,
            lineStyle: {
                width: 1
            },
            showSymbol: true,
            areaStyle: {
                opacity: 0.3,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: '#D19198'
                }, {
                    offset: 1,
                    color: '#0071BC00'
                }])
            },
            emphasis: {
                focus: 'series'
            },
            data: [520, 582, 411, 634, 720, 640, 410]
        },
        {
            name: '心脏耗材',
            type: 'line',
            smooth: true,
            lineStyle: {
                width: 1
            },
            showSymbol: true,
            areaStyle: {
                opacity: 0.3,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: '#F8BE01'
                }, {
                    offset: 1,
                    color: '#00000000'
                }])
            },
            emphasis: {
                focus: 'series'
            },
            data: [320, 432, 501, 534, 490, 330, 220]
        },
        {
            name: '普通耗材',
            type: 'line',
            smooth: true,
            lineStyle: {
                width: 1
            },
            showSymbol: true,
            areaStyle: {
                opacity: 0.3,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: '#2AC1F7'
                }, {
                    offset: 1,
                    color: '#00000000'
                }])
            },
            emphasis: {
                focus: 'series'
            },
            data: [220, 402, 231, 334, 490, 530, 420]
        }
        ]
    }
}
