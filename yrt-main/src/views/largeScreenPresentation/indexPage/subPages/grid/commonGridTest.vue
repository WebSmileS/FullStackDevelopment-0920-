<template>
    <div class="common-grid-wrapper">
        <div>
            <commonHeader :headerName="gridType" :isGrid="true" :url="refUrl"/>
        </div>
        <div class="common-grid-container">
            <table v-if="isShowAppendTable" class="append-grid-table-head">
                <thead>
                    <tr>
                        <th style="width:1.98rem">{{ appendTable.firstTitle }}</th>
                        <td style="width:15.84rem">{{ appendTable.firstValue }}</td>
                        <th style="width:2.5rem">{{ appendTable.secondTitle }}</th>
                        <td >{{ appendTable.secondValue }}</td>
                    </tr>
                </thead>
            </table>
            <table class="common-grid-table">
                <thead>
                    <tr>
                        <th style="width:450px">产品名称</th>
                        <th style="width:120px">规格型号</th>
                        <th style="width:250px">生产企业</th>
                        <th style="width:120px">批号</th>
                        <th style="width:100px">数量</th>
                        <th style="width:250px">销售企业</th>
                        <th style="width:180px">配送时间</th>
                        <th style="width:180px">入库时间</th>
                    </tr>
                </thead>
            </table>
             <vue-seamless-scroll :data="listData" class="warp" ref="seamlessScroll">
                <ul class="item">
                <li v-for="(item, index) in listData" :key="index">
                    <span style="width:450px" class="product_name" v-text="item.product_name"></span>
                    <span style="width:120px" class="specification" v-text="item.specification"></span>
                    <span style="width:250px" class="product_vendor_name" v-text="item.product_vendor_name"></span>
                    <span style="width:120px" class="batch_number" v-text="item.batch_number"></span>
                    <span style="width:100px" class="quantity" v-text="item.quantity"></span>
                    <span style="width:250px" class="organization_name" v-text="item.organization_name"></span>
                    <span style="width:180px" class="at" v-text="item.at"></span>
                    <span style="width:180px" class="in_time" v-text="item.in_time"></span>
                </li>
                </ul>
            </vue-seamless-scroll>
            <!-- <a-pagination show-quick-jumper size="small" :default-current="1" :total="500" disabled show-less-items
                @change="onPaginationChange" /> -->

        </div>
    </div>
</template>

<script>
    import commonHeader from '../header/commonHeader.vue'
    import datas from './test'
    import moment from 'moment';
    import {
        // 产品交易统计 表格列
        ProductContractColumn,
        // 产品金额统计 表格列
        ProductMoneyColumn,
        // 产品批次统计 表格列
        ProductBatchColumn,
        // 产品经销商统计 表格列
        ProductDealerColumn,
        // 产品科室统计 表格列
        ProductCelColumn,
        // 机构预警信息统计 表格列
        DepartmentWarningColumn,
        // 产品预警信息统计 表格列
        ProductWarningColumn
    } from './commonGridColumnsConfig'

    import {
        screenWarnsProductAPI,
        screenWarnsOrgsAPI,
        // screenTradeItemsAPI
    } from '@/service/pageAjax'


    export default {
        components: {
            commonHeader
        },
        props: {
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
            gridType: {
                type: String,
                require: true
            },
            refUrl: {
                type: String,
                require: true
            }
        },
        data() {
            return {
                 listData: [
                ],
                celsNameMock: [
                ],
                /** ------- 表格列设定 ---------- */
                ProductContractColumn,
                ProductMoneyColumn,
                ProductBatchColumn,
                ProductDealerColumn,
                ProductCelColumn,
                DepartmentWarningColumn,
                ProductWarningColumn,
                // 表格数据列
                tableColumns: [],
                appendTable: {
                    firstTitle: '',
                    firstValue: '',
                    secondTitle: '',
                    secondValue: ''
                },
                tableData: [],
                pagination: {
                    pageNum: 26,
                    pageSize: 89
                }
            }
        },
        methods: {
            async screenWarnsProductAPIFn () {
                let params = {
                     pageNum: this.pagination.pageNum,
                     pageSize: this.pagination.pageSize
                 }
                await screenWarnsProductAPI({ params  })
                     .then(res => {
                         const { rows } = res
                         this.tableData = rows.map(m => {
                             var obj = [
                                 { /* product_name */    v: m.productName },
                                 { /* specification */   v: m.specification },
                                 { /* produce_company */ v: m.orgName },
                                 { /* batch_number */    v: m.batch_number },
                                 { /* quantity */        v: '' },
                                 { /* sales_company */   v: '' },
                                 { /* apply_cel */       v: '' },
                                 { /* contract_time */   v: m.expireAt },
                                 { /* warning_type */    v: m.warnType }
                             ]
                             return obj
                         }) 
                    })
            },
            async screenWarnsOrgsAPIFn () {
                let params = {
                     pageNum: this.pagination.pageNum,
                     pageSize: this.pagination.pageSize
                 }
                await screenWarnsOrgsAPI({ params  })
                     .then(res => {
                         const { rows } = res
                         this.tableData = rows.map(m => {
                             var obj = [
                                { /* company_name */                    v: m.orgName },
                                { /* social_number */                   v: m.social_credit_code },
                                { /* address */                         v: m.address },
                                { /* telephone */                       v: m.phone },
                                { /* bussiness_department_quantity */   v: m.friendOrgNum },
                                { /* product_type_quantity */           v: m.friendProductCategoryNum },
                                { /* latest_contract_time */            v: m.lastTradeAt },
                                { /* warning_type */                    v: m.warnType },
                             ]
                             return obj
                         }) 
                    })
            },
            async screenTradeItemsAPIFn () {
                this.listData = datas.map(m => {
                    return {
                        // 'celName': this.celsNameMock[(Math.random()*10).toFixed(0)],
                        'at': moment(m.in_time).add(-1, 'days').add(-3, 's').add(-22, 'ms').format('YYYY-MM-DD h:mm:ss'),
                        ...m
                    }
                });
                // let params = {
                //      pageNum: this.pagination.pageNum,
                //      pageSize: this.pagination.pageSize
                //  }
                // await screenTradeItemsAPI({ params  })
                //      .then(res => {
                //          const { rows } = res
                //          this.listData = rows
                //     })
            },
            getProductContactInfo () {
                this.screenTradeItemsAPIFn()
                this.tableData = [
                          /* no */ 
                        { /* product_name */    v: '某某医用口罩' },
                        { /* specification */   v: 'WNK2型' },
                        { /* produce_company */ v: '某某医疗器械生产有限公司' },
                        { /* batch_number */    v: '202004071242' },
                        { /* quantity */        v: '1250箱' },
                        { /* sales_company */   v: '云南某某医疗器械销售有限公司' },
                        { /* apply_cel */       v: '某某医院某某科室' },
                        { /* deliver_time */    v: '2021-03-08 15:30' },
                        { /* grn_time */        v: '2021-03-08 17:25' }
                ]
            },
            getProductMoneyInfo () {
                this.appendTable.firstTitle = '产品名称'
                this.appendTable.firstValue = '某某医用口罩'
                this.appendTable.secondTitle = '销售总金额'
                this.appendTable.secondValue = '263584元'
                this.tableData = [
                          /* no */ 
                        { /* specification */   v: 'WNK2型' },
                        { /* sales_company */   v: '云南某某医疗器械销售有限公司' },
                        { /* quantity */        v: '1250箱' },
                        { /* deliver_time */    v: '2021-03-08 15:30' },
                        { /* money */           v: '50000.00元' },
                ]
            },
            getProductBatchInfo () {
                this.appendTable.firstTitle = '产品名称'
                this.appendTable.firstValue = '某某医用口罩'
                this.appendTable.secondTitle = '批次总数量'
                this.appendTable.secondValue = '3682'
                this.tableData = [
                          /* no */ 
                        { /* specification */   v: 'SAKS3型' },
                        { /* sales_company */   v: '云南某某医疗器械生产有限公司' },
                        { /* quantity */        v: '1250箱' },
                        { /* deliver_time */    v: '2021-03-08 15:30' },
                        { /* money */           v: '20210316A0251' },
                ]
            },
            getProductDealerInfo () {
                this.appendTable.firstTitle = '产品名称'
                this.appendTable.firstValue = '某某医用口罩'
                this.appendTable.secondTitle = '经销商总数量'
                this.appendTable.secondValue = '512'
                this.tableData = [
                          /* no */ 
                        { /* specification */   v: 'SAKS3型' },
                        { /* sales_company */   v: '云南某某医疗器械生产有限公司' },
                        { /* quantity */        v: '1250箱' },
                        { /* deliver_time */    v: '2021-03-08 15:30' },
                        { /* dealer */          v: '云南某某医疗器械销售有限公司' },
                ]
            },
            getProductCelInfo () {
                this.appendTable.firstTitle = '产品名称'
                this.appendTable.firstValue = '某某医用口罩'
                this.appendTable.secondTitle = '使用科室总数量'
                this.appendTable.secondValue = '4216'
                this.tableData = [
                          /* no */ 
                        { /* specification */   v: 'SAKS3型' },
                        { /* sales_company */   v: '云南某某医疗器械销售有限公司' },
                        { /* quantity */        v: '1250箱' },
                        { /* deliver_time */    v: '2021-03-08 15:30' },
                        { /* dealer */          v: '某某医院某某科室' },
                ]
            },
            getDepWarningInfo () {
                this.screenWarnsOrgsAPIFn()
            },
            getProductWarningInfo () {
                this.screenWarnsProductAPIFn()
            },
            PageInit () {
                switch (this.gridType) {
                    // ---- 产品交易统计 ------------------
                    case 'ProContract': {
                        this.tableColumns = this.ProductContractColumn
                        this.getProductContactInfo()
                        break
                    }
                    // ---- 产品金额 ---------------------
                    case 'ProMoney': {
                        this.tableColumns = this.ProductMoneyColumn
                        this.getProductMoneyInfo()
                        break
                    }
                    // ---- 产品批次 ---------------------
                    case 'ProBatch': {
                        this.tableColumns = this.ProductBatchColumn
                        this.getProductBatchInfo()
                        break
                    }
                    // ---- 产品经销商 -------------------
                    case 'ProDealer': {
                        this.tableColumns = this.ProductDealerColumn
                        this.getProductDealerInfo()
                        break
                    }
                    // ---- 产品科室 ---------------------
                    case 'ProCel': {
                        this.tableColumns = this.ProductCelColumn
                        this.getProductCelInfo()
                        break
                    }
                    // ---- 机构预警信息 ------------------
                    case 'DepWarning': {
                        this.getDepWarningInfo()
                        this.tableColumns = this.DepartmentWarningColumn
                        break
                    }
                    // ---- 产品预警信息 ------------------
                    case 'ProWarning': {
                        this.getProductWarningInfo()
                        this.tableColumns = this.ProductWarningColumn
                        break
                    }
                    // ---- 产品预警信息-不良 ---------------
                    case 'ProWarningBad': {
                        this.getProductWarningInfo()
                        this.tableColumns = this.ProductWarningColumn
                        break
                    }
                        

                }
            },
            convertFormat (obj) {
                let final = new Object()
                obj.forEach(e => {
                    final[e.k] = e.v
                })
                return final
            },
            onPaginationChange (page) {
                this.pagination.pageNum = page - 1
                this.PageInit()
            },
            onTableClick (obj) {
                let formatedObj = this.convertFormat(obj)
                switch (this.gridType) {
                    // ---- 产品交易统计 ------------------
                    case 'ProContract': {
                        this.$router.push({
                            name: 'productInfo',
                            params: {
                                formatedObj: formatedObj
                            }
                        })
                        break
                    }
                    // ---- 产品金额 ---------------------
                    case 'ProMoney': {
                        this.tableColumns = this.ProductMoneyColumn
                        break
                    }
                    // ---- 产品批次 ---------------------
                    case 'ProBatch': {
                        this.tableColumns = this.ProductBatchColumn
                        break
                    }
                    // ---- 产品经销商 -------------------
                    case 'ProDealer': {
                        this.tableColumns = this.ProductDealerColumn
                        break
                    }
                    // ---- 产品科室 ---------------------
                    case 'ProCel': {
                        this.tableColumns = this.ProductCelColumn
                        break
                    }
                    // ---- 机构预警信息 ------------------
                    case 'DepWarning': {
                        this.$router.replace({path: '/largeScreen/departmentInfoDetail'})
                        break
                    }
                    // ---- 产品预警信息 ------------------
                    case 'ProWarning': {
                        this.$router.replace({path: '/largeScreen/productWarningInfoDetail'})
                        break
                    }
                    // ---- 产品预警信息-不良 ---------------
                    case 'ProWarningBad': {
                        this.$router.replace({path: '/largeScreen/productWarningInfoDetail'})
                        break
                    }
                }
            }
        },
        computed: {
            isShowAppendTable () {
                return this.gridType === 'ProMoney' 
                    || this.gridType === 'ProBatch'
                    || this.gridType === 'ProDealer'
                    || this.gridType === 'ProCel'
            },
        },
        mounted () {
            this.PageInit()
        }
    }
</script>

<style scoped>

    .append-grid-table-head {
        width: 24.24rem;
        margin-left: 0.32rem;
        border-bottom: 0;
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
        height: 0.55rem;
        padding: 0px;
        opacity: 1;
        font-size: 0.24rem;
        font-family: PingFang SC, PingFang SC-Regular;
        font-weight: 400;
        text-align: center;
        color: #ffffff;
        border-top: 0.01rem solid #2c96d1;
        border-right: 0.01rem solid #2c96d1;
    }

    tr:hover {
         background-color: #2c97d188;
    }

    .common-grid-wrapper {
        position: absolute;
        top: 1.03rem;
        left: 0.68rem;
    }

    .common-grid-table {
        width: 100%;
        margin-left: 32px;
        margin-bottom: 10px;
    }

</style>

<style>
    /* ----------- AntD分页的样式覆盖 ------------------------------------------------- */

    /* 分页输入框 样式覆盖*/
    .ant-pagination.mini .ant-pagination-options-quick-jumper input {
        background-color: #00000000;
        border-color: #0071BC;
    }

    /* 分页输入框描述 样式覆盖*/
    .ant-pagination.mini .ant-pagination-options-quick-jumper {
        color: #9AD5EF;
    }

    /* 分页页数 样式覆盖*/
    .ant-pagination.mini .ant-pagination-item:not(.ant-pagination-item-active) {
        background-color: #0071bd33;
        margin-right: 0.1rem !important;
        margin-left: 0.1rem !important;
    }

    .ant-pagination-item {
        background-color: #0071bd33;
    }

    .ant-pagination-prev,
    .ant-pagination-next {
        margin-right: 0.1rem !important;
        margin-left: 0.1rem !important;
        background-color: #0071bd33;
    }

    .ant-pagination.mini .ant-pagination-jump-prev,
    .ant-pagination.mini .ant-pagination-jump-next {
        background-color: #0071bd33;

    }

    .ant-pagination-item-active {
        color: #ffffff;
        border-color: #1890ff;
        font-weight: 500;
    }

    .anticon svg {
        color: #9AD5EF;
    }

    .ant-pagination-item-ellipsis {
        color: #9AD5EF;
    }

    /* 分页页数数字 样式覆盖*/
    .ant-pagination-item a {
        color: #9AD5EF;
    }

</style>

<style lang="less" scoped>
  .warp {
    height: 780px;
    width: 100%;
    overflow: hidden;
    width: 100%;
        max-height: 1155px;
        margin-left: 32px;
        margin-bottom: 0.2rem;
    ul {
      list-style: none;
      padding: 0;
      margin: 0 auto;
      li{
        display: block;
        height: 30px;
        line-height: 30px;
        display: flex;
        justify-content: space-between;
      }
      span {
        height: 0.55rem;
        opacity: 1;
        font-size: 0.24rem;
        font-family: PingFang SC, PingFang SC-Regular;
        font-weight: 400;
        text-align: center;
        color: #ffffff;
        border-top: 0.01rem solid #2c96d1;
        border-right: 0.01rem solid #2c96d1;
        overflow: hidden;
      }
    }
  }
</style>
