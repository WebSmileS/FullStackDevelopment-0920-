<template>
    <div class="common-header">
        <div class="common-header-left-title">
            <img class="headerIcon" :src="headerIconPath">
            <span class="headerTitle" :style="`color:${headerTitleColor}`">{{ headerTitle }}</span>
        </div>
        <div class="common-header-options">
            <div class="common-header-area-dropdown" v-if="isShowArea">
                <select name="provinceChoose">
                    <option value="">请选择省份</option>
                    <option value="dog">Dog</option>
                </select>
                <select name="areaChoose">
                    <option value="">请选择市/县/地区</option>
                    <option value="dog">Dog</option>
                </select>
            </div>
            <div class="common-header-product-warning" v-if="isShowProductWarning"> 
                <button>产品资证预警</button>
                <button>不良产品预警</button>
                <button>产品失效预警</button>
            </div>
            <div class="common-header-common-search">
                <input placeholder="请输入需要查询的内容" size="small">
                <button>
                    查询
                </button>
            </div>
            <div class="common-header-button-group">
                <a-icon type="left" @click="onReturn" style="margin-left: 0.2rem"/>
                <a-icon type="close" @click="onClose" style="margin-left: 0.2rem"/>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        props: {
            // productContractIndex     -> [name, area, search, action],
            // productContractDetail    -> [name, search, action],
            // departmentWarningindex   -> [name, area, search, action],
            // departmentWarningDetail  -> [name, search, action],
            // departmentWarningProcess -> [name, area, search, action],
            // productWarningIndex      -> [name, area, button, search, action],
            // productWarningDetail     -> [name, search, action],
            headerName: String,
            isGrid: Boolean,
            url: String
        },
        data () {
            return {
                headerType: '',
                headerTitle: '',
                headerTitleColor: '',
                headerIconPath: '',
                isShowArea: false,
                isShowProductWarning: false,
            }
        },
        methods: {
                        /**
             * =====================================
             *               表格类型
             * -------------------------------------
             *  - ProContract    ->  产品交易统计
             *  - ProMoney       ->  产品金额
             *  - ProBatch       ->  产品批次
             *  - ProDealer      ->  产品经销商
             *  - ProCel         ->  产品科室
             *  - DepWarning     ->  机构预警信息
             *  - ProWarning     ->  产品预警信息
             *  - ProWarningBad  ->  产品预警信息-不良
             * =====================================
             */
            chooseGridType () {
                switch (this.headerName) {
                    // 产品交易统计
                    case 'ProContract': {
                        return 'productContractIndex'
                    }
                    // 产品金额
                    case 'ProMoney': {
                        return 'productContractDetail'
                    }
                    // 产品批次
                    case 'ProBatch': {
                        return 'productContractDetail'
                    }
                    // 产品经销商
                    case 'ProDealer': {
                        return 'productContractDetail'
                    }
                    // 产品科室
                    case 'ProCel': {
                        return 'productContractDetail'
                    }
                    // 机构预警信息
                    case 'DepWarning': {
                        return 'departmentWarningindex'
                    }
                    // 产品预警信息
                    case 'ProWarning': {
                        return 'productWarningIndex'
                    }
                    // 产品预警信息-不良
                    case 'ProWarningBad': {
                        return 'productWarningIndex'
                    }
                }
            },
            pageInit () {
                if (this.isGrid) {
                    this.headerType = this.chooseGridType()
                } else {
                    this.headerType = this.headerName
                }
                console.log(this.headerType)
                switch (this.headerType) {
                    case 'productContractIndex': {
                        this.headerTitle = '产品交易统计'
                        this.headerTitleColor = '#0FE18A'
                        this.headerIconPath = '/images/largeScreen/icon/icon33_green.png'
                        this.isShowArea = true
                        this.isShowProductWarning = false
                        break
                    }
                    case 'productContractDetail': {
                        this.headerTitle = '产品交易统计'
                        this.headerTitleColor = '#0FE18A'
                        this.headerIconPath = '/images/largeScreen/icon/icon33_green.png'
                        this.isShowArea = false
                        this.isShowProductWarning = false
                        break
                    }
                    case 'departmentWarningindex': {
                        this.headerTitle = '机构信息'
                        this.headerTitleColor = '#F4406B'
                        this.headerIconPath = '/images/largeScreen/icon/icon22_red.png'
                        this.isShowArea = true
                        this.isShowProductWarning = false
                        break
                    }
                    case 'departmentWarningDetail': {
                        this.headerTitle = '机构预警信息'
                        this.headerTitleColor = '#F4406B'
                        this.headerIconPath = '/images/largeScreen/icon/icon22_red.png'
                        this.isShowArea = false
                        this.isShowProductWarning = false
                        break
                    }
                    case 'departmentWarningProcess': {
                        this.headerTitle = '机构预警信息'
                        this.headerTitleColor = '#F4406B'
                        this.headerIconPath = '/images/largeScreen/icon/icon22_red.png'
                        this.isShowArea = true
                        this.isShowProductWarning = false
                        break
                    }
                    case 'productWarningIndex': {
                        this.headerTitle = '产品预警信息'
                        this.headerTitleColor = '#604AFD'
                        this.headerIconPath = '/images/largeScreen/icon/icon11_purple.png'
                        this.isShowArea = true
                        this.isShowProductWarning = true
                        break
                    }
                    case 'productWarningDetail': {
                        this.headerTitle = '产品预警信息'
                        this.headerTitleColor = '#604AFD'
                        this.headerIconPath = '/images/largeScreen/icon/icon11_purple.png'
                        this.isShowArea = false
                        this.isShowProductWarning = false
                        break
                    }
                   
                }
            },
            onReturn () {
                this.$router.replace({path: `/largeScreen/${this.url}`})
            },
            onClose () {
                this.$router.replace({path: '/largeScreen/index'})
            },
        },
        mounted () {
            this.pageInit()
        }
    }
</script>

<style scoped>
    .header {
        padding-top: 0.24rem;
        padding-left: 0.32rem;
        display: flex;
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
        color: #8954EF;
        line-height: 0.36rem;
    }

    .common-header {
        display: flex;
        flex-direction: row;
        align-content: center;
        align-items: center;
        justify-content: space-between;
        margin-top: 0.2rem;
        height: 0.9rem;
    }

    .common-header-options {
        display: flex;
        flex-direction: row;
        align-content: center;
        align-items: center;
        justify-content: flex-end;
    }

    .common-header-area-dropdown select {
        height: 0.42rem;
        width: 2.77rem;
        background: transparent;
        border: 1px solid #2c96d1bf;
        color: white;
        margin-right: 0.2rem;
        font-family: "PingFang SC";
        font-weight: normal;
        font-size: 0.2rem;
        line-height: 0.5287rem;
        text-align: left;
        color: #fff;
    }

    .common-header-area-dropdown option {
        background-color: #000;
        border: none;
    }

    .common-header-product-warning button {
        height: 0.42rem;
        width: 1.55rem;
        border-radius: 0.08rem;
        background: transparent;
        border: 0.01rem solid #2c96d1bf;
        font-family: "PingFang SC";
        font-weight: normal;
        font-size: 0.2rem;
        line-height: 0.5287rem;
        text-align: left;
        color: #fff;
        margin-right: 0.2rem;
        display: inline-flex;
        align-items: center;
        justify-content: space-around;
    }

    .common-header-product-warning button:focus {
        background: linear-gradient(#fba993 0%, #ef77a8 100%);
        border: none;
    }

    .common-header-common-search {
        height: 0.42rem;
        width: 3.57rem;
        display: flex;
        justify-content: space-between;
        background: transparent;
        border: 0.01rem solid #2c96d1bf;
    }

    .common-header-common-search input {
        background: #0071bd26;
        border: none;
        font-family: "PingFang SC";
        font-weight: normal;
        font-size: 0.18rem;
        line-height: 0.5287rem;
        text-align: left;
        color: #9ad5ef;
        opacity: 0.7;
    }

    .common-header-common-search button {
        height: 0.42rem;
        display: flex;
        align-items: center;
        background: #0071bd59;
        border: none;
        font-family: "PingFang SC";
        font-weight: normal;
        font-size: 0.18rem;
        line-height: 0.5287rem;
        text-align: left;
        color: #fff;
    }
</style>