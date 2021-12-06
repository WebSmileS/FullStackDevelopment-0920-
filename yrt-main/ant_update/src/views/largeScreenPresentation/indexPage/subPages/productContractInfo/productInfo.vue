<template>
    <div class="common-screen-wrapper">
        <div>
            <commonHeader :headerName="'productContractDetail'" :isGrid="false" :url="'productContractInfo'"/>
        </div>
        <!-- <a-carousel :autoplay="true" :autoplaySpeed="8000" style="height:1000px;width:1000px">
            <div>
                <img style="height:100%;width:100%" src="/images/largeScreen/img/info1.png">
            </div> -->
            <div>
                <img style="height:100%;width:100%" src="/images/largeScreen/img/info1.png">
            </div>
        <!-- </a-carousel> -->
        <!-- <div class="product-info-container">
            <div class="product-info-grid">
                <table>
                    <tbody>
                        <tr>
                            <th>产品名称</th>
                            <td colspan="5">医用固定带（肋骨）</td>
                            <th rowspan="4">产品图片</th>
                            <td rowspan="4"> <img style="height:200px;width:200px" src="/images/largeScreen/img/yiyonggudingdai.jpg"></td>
                        </tr>
                        <tr>
                            <th>生产企业</th>
                            <td>衡水鑫美医疗器械有限公司</td>
                            <th>规格型号</th>
                            <td colspan="3">A-22型</td>
                        </tr>
                        <tr>
                            <th>销售企业</th>
                            <td>北京朝阳医疗器械有限公司</td>
                            <th>批号</th>
                            <td>{{ batch_number }}</td>
                            <th>配送时间</th>
                            <td>{{ deliver_time }}</td>
                        </tr>
                        <tr>
                            <th>申请科室</th>
                            <td>宣威云峰医院急救科</td>
                            <th>数量</th>
                            <td>{{ quantity }}</td>
                            <th>入库时间</th>
                            <td>{{ grn_time }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="product-info-detail-group">
                <infoCard :CardType="'money'"/>
                <infoCard :CardType="'batch'"/>
                <infoCard :CardType="'dealer'"/>
                <infoCard :CardType="'cel'"/>
            </div>
            <div class="product-info-tips">
                点击按钮查看产品相关信息
            </div>
        </div> -->
    </div>
</template>

<script>
    import commonHeader from '../header/commonHeader.vue'
    // import infoCard from './module/productInfoCards.vue'
        import {
        screenProductInfoAPI
    } from '@/service/pageAjax'

    export default {
        components: {
            commonHeader,
            // infoCard
        },
        data () {
            return {
                product_name: '某某医用口罩',
                specification: 'WNK2型',
                produce_company: '某某医疗器械生产有限公司',
                batch_number: '202004071242',
                quantity: '1250箱',
                sales_company: '云南某某医疗器械销售有限公司',
                apply_cel: '某某医院某某科室',
                deliver_time: '2021-07-25 15:30',
                grn_time: '2021-07-26 17:25'
            }
        },
        methods: {
            async screenProductInfoAPIFn () {
                let params = {
                     product_inner_sn: 5,
                 }
                await screenProductInfoAPI({ params  })
                     
            },
            initPageData () {
                const formatedObj = this.$route.params.formatedObj
                this.product_name       = formatedObj.product_name
                this.specification      = formatedObj.specification
                this.produce_company    = formatedObj.produce_company
                this.batch_number       = formatedObj.batch_number
                this.quantity           = formatedObj.quantity
                this.sales_company      = formatedObj.sales_company
                this.apply_cel          = formatedObj.apply_cel
                this.deliver_time       = formatedObj.deliver_time
                this.grn_time           = formatedObj.grn_time
            }
        },
        mounted () {
            // this.screenProductInfoAPIFn()
            // this.initPageData()
        }
    }
</script>

<style>
    .common-screen-wrapper {
        position: absolute;
        top: 1.03rem;
        left: 0.68rem;
    }

    table {
        border-left: 0.01rem solid #2c96d1;
        border-bottom: 0.01rem solid #2c96d1;
    }

    th {
        height: 0.32rem;
        padding: 0px;
        opacity: 1;
        font-size: 0.24rem;
        font-family: PingFang SC, PingFang SC-Medium;
        font-weight: 400;
        text-align: center;
        color: #ffffff;
        border-top: 0.01rem solid #2c96d1;
        border-right: 0.01rem solid #2c96d1;
        background-color: #2c97d12e;
    }

    td {
        height: 0.37rem;
        padding: 0px;
        opacity: 1;
        font-size: 0.24rem;
        font-family: PingFang SC, PingFang SC-Regular;
        font-weight: 400;
        text-align: left;
        padding-left: 0.1rem;
        color: #ffffff;
        border-top: 0.01rem solid #2c96d1;
        border-right: 0.01rem solid #2c96d1;
    }

    .product-info-container {
        height: 11.72rem;
        width: 24.24rem;
        background: transparent;
        border: 1px solid #2c96d1;
    }

    .product-info-grid table {
        width: 100%;
        height: 2.3rem;
    }

    .product-info-detail-group {
        display: flex;
        justify-content: space-evenly;
        align-items: center;
        align-content: center;
        flex-direction: row;
        margin-top: 1.3rem;
    }

    .product-info-tips {
        font-family: "PingFang SC";
        font-weight: normal;
        font-size: 0.26rem;
        line-height: 0.45rem;
        text-align: left;
        color: #68d3e7;
        display: flex;
        justify-content: center;
        margin-top: 0.6rem;
    }
</style>