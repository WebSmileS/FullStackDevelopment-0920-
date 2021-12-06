<template>
    <div>
        <!-- 左边部分 -->
        <div class="leftPart">
            <left-part-main ref="leftPart"/>
        </div>
        <!-- 中间部分 -->
        <div class="middlePart">
            <middle-part-main ref="middlePart"/>
        </div>
        <!-- 右边部分 -->
        <div class="rightPart">
            <right-part-main ref="rightPart"/>
        </div>
    </div>
</template>

<script>
import LeftPartMain from '../leftPart/leftPartMain.vue'
import MiddlePartMain from '../middlePart/middlePartMain.vue'
import RightPartMain from '../rightPart/rightPartMain.vue'

import {
    screenIndexAPI,
    screenOrgsIndexAPI
} from '@/service/pageAjax'

export default {
    name: 'largeScreenIndex',
    components: {
        LeftPartMain,
        MiddlePartMain,
        RightPartMain
    },
    data () {
        return {
            // 机构统计，中间
            middlePartData: {},
            // 机构预警，右上
            rightUpPartData: {},
            // 产品预警，右下
            rightDownPartData: {}
            // 产品类型统计，左上 (暂无)
            // leftUpPartData: [],
            // 交易统计，左下 (暂无)
            // leftDownPartData: [],
        }
    },
    methods: {
        async getScreenIndexAPIFn () {
            let params = {
                pageNum: 0,
                pageSize: 10
            }
            await screenIndexAPI({params})
                .then(res => {
                    // 机构统计，中间
                    this.middlePartData = res.orgData
                    // 机构预警，右上
                    this.rightUpPartData = res.orgWarnData
                    // 产品预警，右下
                    this.rightDownPartData = res.productWarnData
                    // 产品类型统计，左上 (暂无)
                    // const leftUpPartData = res.productTypeQuantites
                    // 交易统计，左下 (暂无)
                    // const leftDownPartData = res.tradeData

                    // this.$refs.LeftPartMain.init(this.LeftPartData)
                    this.$refs.middlePart.init(res.orgData)
                    this.$refs.rightPart.init(res.orgWarnData, res.productWarnData)
                })
        },
        async getScreenOrgsIndexAPIFn () {
            await screenOrgsIndexAPI()
                .then(res => {
                    console.log(res)
                })
        },
        backToPlatform () {
            this.$router.replace({path: '/ptm/largeScreenEntry'})
        }
    },
    created () {
        const screenWidth = document.body.offsetWidth
        const defaultWidth = 2560

        const widthRate = screenWidth / defaultWidth

        document.getElementsByTagName('html')[0].style.fontSize = 100 * widthRate + 'px'
        document.getElementById('app').style.backgroundColor = '#000'
    },
    mounted () {
        this.getScreenIndexAPIFn()
        this.getScreenOrgsIndexAPIFn()
    }
}
</script>

<style>

</style>
