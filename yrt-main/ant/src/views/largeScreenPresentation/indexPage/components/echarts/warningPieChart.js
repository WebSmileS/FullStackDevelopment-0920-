export default function getOptions (productWarnType) {
    const rate = (document.body.offsetWidth / 2560)
    const totalCount = productWarnType.mdrfWarnTotal + productWarnType.overdueWarnTotal + productWarnType.notgoodWarnTotal
    const mdrfRate = parseInt((productWarnType.mdrfWarnTotal / totalCount) * 100)
    const overdueRate = parseInt((productWarnType.overdueWarnTotal / totalCount) * 100)
    const notgoodRate = parseInt((productWarnType.notgoodWarnTotal / totalCount) * 100)
    return {
        backgroundColor: '#0071BC00',
        series: [
            {
                left: 0,
                name: '访问来源',
                type: 'pie',
                radius: ['45%', '70%'],
                label: {
                    position: 'outer',
                    alignTo: 'none',
                    bleedMargin: 1, 
                    fontSize: 20 * rate, 
                    fontStyle: 'normal', 
                    fontWeight: 'bolder'
                },
                labelLine: {
                    length: 15 * rate,
                    length2: 0
                },
                data: [
                    {value: mdrfRate, name: mdrfRate + '%'},
                    {value: overdueRate, name: overdueRate + '%'},
                    {value: notgoodRate, name: notgoodRate + '%'}
                ]
            }
        ],
        color: ['#604AFD', '#F7146F', '#2ADEF7']
    }
}
