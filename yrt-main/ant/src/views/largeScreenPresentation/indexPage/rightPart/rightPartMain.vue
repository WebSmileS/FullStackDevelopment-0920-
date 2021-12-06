<template>
    <div>
        <div class="rightUpPart">
            <rightUpPart ref="rightUpPart"/>
        </div>
        <div class="rightDownPart">
            <rightDownPart ref="rightDownPart"/>
        </div>
    </div>
</template>

<script>
import rightUpPart from './rightUpPart'
import rightDownPart from './rightDownPart'

export default {
    name: 'rightPart',
    components: {
        rightUpPart,
        rightDownPart
    },
    data () {
        return {
            // 右上方，机构预警信息列表数据
            orgWarnInfo: {
                orgWarnInfo: [],
                orgWarnTotal: 0
            },
            // 右上方，机构预警信息柱状图数据
            orgWarnType: {
                // 医院预警总数
                hosWarnTotal: 0,
                // 经销商预警总数
                deaWarnTotal: 0,
                // 厂商预警总数
                venWarnTotal: 0,
                // 回收商预警总数
                recWarnTotal: 0,
                // 科室预警总数
                celWarnTotal: 0
            },
            // 左上方，产品预警信息列表数据
            productWarnInfo: {
                productWarnInfo: [],
                productWarnTotal: 0
            },
            // 左上方，产品预警信息饼图数据
            productWarnType: {
                // 产品注册证预警
                mdrfWarnTotal: 0,
                // 产品过期预警
                overdueWarnTotal: 0,
                // 不良产品预警
                notgoodWarnTotal: 0
            }
        }
    },
    methods: {
        /**
         * init
         * @abstract 由父组件调用的初始化数据方法，初始化自身后，调用子组件初始化方法
         * @param {Object} rightUpPartData 右上方数据，机构预警信息
         * @param {Object} rightDownPartData 右下方数据，产品预警信息
         */
        init (rightUpPartData, rightDownPartData) {
            /* ---------------------------------------------------------------------------------------------- */
            /*   右上方数据赋值 */
            /* ---------------------------------------------------------------------------------------------- */
            // 为机构预警信息列表数据赋值
            this.orgWarnInfo.orgWarnInfo = rightUpPartData.orgWarnInfoPage.rows.map(m => {
                return {
                    address: m.address,  
                    friendOrgNum: m.friendOrgNum,  
                    friendProductCategoryNum: m.friendProductCategoryNum,  
                    lastTradeAt: m.lastTradeAt,  
                    orgName: m.orgName,
                    phone: m.phone,
                    social_credit_code: m.social_credit_code,
                    type: m.type,
                    warnType: m.warnType
                }
            })
            // 为机构预警信息列表数据总数赋值
            this.orgWarnInfo.orgWarnTotal = rightUpPartData.orgWarnInfoPage.total
            // 为机构预警信息柱状图信息赋值
            this.orgWarnType.hosWarnTotal = rightUpPartData.orgWarns.find(f => parseInt(f.type) === 1).count
            this.orgWarnType.deaWarnTotal = rightUpPartData.orgWarns.find(f => parseInt(f.type) === 2).count
            this.orgWarnType.venWarnTotal = rightUpPartData.orgWarns.find(f => parseInt(f.type) === 3).count
            this.orgWarnType.recWarnTotal = rightUpPartData.orgWarns.find(f => parseInt(f.type) === 4).count
            this.orgWarnType.celWarnTotal = rightUpPartData.orgWarns.find(f => parseInt(f.type) === 5).count

            /* ---------------------------------------------------------------------------------------------- */
            /*   右下方数据赋值 */
            /* ---------------------------------------------------------------------------------------------- */
            // 为产品预警信息列表数据赋值
            this.productWarnInfo.productWarnInfo = rightDownPartData.productWarnInfoPage.rows.map(m => {
                return {
                    batch_number: m.batch_number,
                    expireAt: m.expireAt,
                    orgName: m.orgName,
                    productName: m.productName,
                    product_inner_sn: m.product_inner_sn,
                    specification: m.specification,
                    vendor_inner_sn: m.vendor_inner_sn,
                    vendor_name: m.vendor_name,
                    warnType: m.warnType
                }
            })
            // 为机构预警信息列表数据总数赋值
            this.productWarnInfo.productWarnTotal = rightDownPartData.productWarnInfoPage.total
            // 为机构预警信息柱状图信息赋值
            this.productWarnType.mdrfWarnTotal = rightDownPartData.productWarns.find(f => f.warnType === '产品注册证预警').count
            this.productWarnType.overdueWarnTotal = rightDownPartData.productWarns.find(f => f.warnType === '产品过期预警').count
            this.productWarnType.notgoodWarnTotal = rightDownPartData.productWarns.find(f => f.warnType === '不良产品预警').count
            
            /* ---------------------------------------------------------------------------------------------- */
            /*   为子页面赋值 */
            /* ---------------------------------------------------------------------------------------------- */
            this.$refs.rightUpPart.init(this.orgWarnInfo, this.orgWarnType)
            this.$refs.rightDownPart.init(this.productWarnInfo, this.productWarnType)
        }
    },
    mounted () {
    }
}
</script>

<style>

.rightUpPart {
  background-color: #0071bc2e;
  height: 5.68rem;
  width: 6.34rem;
}

.rightDownPart {
  background-color: #0071bc2e;
  margin-top: 0.24rem;
  height: 7.08rem;
  width: 6.34rem;
}

</style>
