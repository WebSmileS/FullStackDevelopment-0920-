export default function getOptions (colors, value) {
    let percentage = value
    let leftPercentage = 100 - value
    return {
        series: [
            {
                name: '访问来源',
                type: 'pie',
                radius: ['80%', '100%'],
                label: {
                    show: false,
                    position: 'center'
                },
                data: [
                    {value: percentage, name: '搜索引擎'},
                    {value: leftPercentage, name: '直接访问'}
                ]
            }
        ],
        color: [colors, '#4D4D4D']
    }
}
