<template>
    <div class="scoped scoped-white" style="background: #fff">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="hight-search-wrap" style="padding-top: 0px">
                <a-col :span="22" class="col-list">
                    <ul class="form">
                        <li>
                            <span class="label">单位名称</span>
                            <a-input v-model="searchBar.organization_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">厂商</span>
                            <a-input v-model="searchBar.product_vendor_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">单据编号</span>
                            <a-input v-model="searchBar.purchase_sn" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">产品名称</span>
                            <a-input v-model="searchBar.product_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">采购员</span>
                            <a-input v-model="searchBar.buyer_name" type="text" placeholder="" />
                        </li>
                        <li class="ant-calendar-picker-wrap">
                            <span class="label">日期</span>
                            <a-range-picker
                                :placeholder="['', '']"
                                :default-value="defaultDateRange"
                                @change="searchBarDateChangeFn"
                            >
                            </a-range-picker>
                        </li>
                        <li>
                            <a-button @click="searchBtnFn" type="primary" style="margin-right: 10px"> 搜索 </a-button>
                            <a-button @click="handleExport" class="btn-export" size="default">
                                <span class="btn-text-export">导出</span>
                                <i class="yrt yrt-excel"></i>
                            </a-button>
                        </li>
                    </ul>
                </a-col>
                <div class="refresh" style="text-align: right; background: '#fff'; position: absolute; right: 0px">
                    <!-- 刷新按钮  -->
                    <a-tooltip slot="tabBarExtraContent" placement="left">
                        <template slot="title">
                            <span>刷新</span>
                        </template>
                        <a-button
                            type="primary"
                            shape="circle"
                            icon="reload"
                            size="small"
                            class="refresh-btn"
                            @click="refreshTableFn()"
                        />
                    </a-tooltip>
                </div>
            </a-row>
        </div>
        <!--头部区域结束-->

        <div class="tab-table-wrap tab-table-overflowy" style="background: '#fff'; margin-top: 0px">
            <div>
                <vxe-grid
                    ref="PendingOutTable"
                    size="small"
                    show-overflow
                    show-header-overflow
                    showHeader
                    border
                    resizable
                    auto-resize
                    highlight-current-row
                    :row-class-name="listTableRowClassName"
                    :height="scrollYNumber - 1"
                    :data="table.Datas"
                    :loading="table.Loading"
                    @cell-click="tableCellClickFn"
                    @cell-dblclick="tableCellDblclickFn"
                    align="center"
                >
                    <vxe-table-column key="index" field="index" title="No." width="50">
                        <template v-slot="{ seq }">
                            <span>{{ seq }}</span>
                        </template>
                    </vxe-table-column>
                    <vxe-table-column
                        v-for="column in commonTableColumn"
                        :key="column.key"
                        :field="column.dataIndex"
                        :title="column.title"
                    ></vxe-table-column>
                    <vxe-table-column key="record_status" field="record_status" title="状态">
                        <template v-slot="{ row }">
                            <span>{{ row.record_status | purchaseRecordStatusTextFilter }}</span>
                        </template>
                    </vxe-table-column>
                </vxe-grid>
                <vxe-pager
                    :current-page="pager.pageNum"
                    :page-size="pager.pageSize"
                    :page-sizes="pager.pageSizes"
                    :total="pager.total"
                    :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                    @page-change="ontablePaginationChangeFn"
                >
                </vxe-pager>
            </div>
        </div>
        <!--
        <check-record ref="checkRecordModal" :visible="checkModal.visible" @onCancel="onCheckModalCancel" />
        -->
    </div>
</template>

<script>
import { orderPurchaseRecordListAPI, orderPurchaseRecordExportAPI } from '@/service/pageAjax';
import moment from 'moment';
// import CheckRecord from './checkRecord.vue';
import purchaseRecordColumns from './purchaseRecordColumns';
import { purchaseRecordStatusTextFilter } from './filters';
import { purchaseRecordStatusMap } from './type';

export default {
    name: 'useRecord',
    components: {
        // CheckRecord
    },
    data() {
        return {
            // 顶部搜索栏数据
            searchBar: {
                purchase_sn: '',
                product_name: '',
                product_vendor_name: '',
                buyer_name: '',
                begin_date: '',
                end_date: '',
                organization_name: ''
            },
            // tab数据
            pager: {
                // 分页页数
                pageNum: 1,
                // 分页数据量
                pageSize: 10,
                // 数据总量
                total: 10,
                // 分页大小
                pageSizes: [10, 20, 30, 40]
            },
            // 表格数据
            table: {
                // 表格的数据
                Datas: [],
                // 是否加载中
                Loading: false,
                // 当前选中行
                currentSelectedRow: {}
            },
            checkModal: {
                visible: false
            },
            // 数据列设置
            commonTableColumn: purchaseRecordColumns,
            defaultDateRange: [],
            isExporting: false
        };
    },
    filters: {
        /**
         * 采购记录状态文本
         */
        purchaseRecordStatusTextFilter
    },
    methods: {
        listTableRowClassName({ row }) {
            if (row.record_status === purchaseRecordStatusMap.TO_BE_VERIFY.value) {
                return 'un-certificated';
            }
            if (row.record_status === purchaseRecordStatusMap.EXECUTING.value) {
                return 'in-process';
            }
            return '';
        },
        /** ********************** 接口区域开始 ********************************************/

        /**
         * 获取使用记录数据接口调用
         * @async api:getWarehouseTransferListAPIFnAsync
         */
        async orderPurchaseRecordListAPIFn() {
            let params = {
                partA: this.$cookie.get('userbelong'),
                partB_name: this.searchBar.organization_name,
                product_vendor_name: this.searchBar.product_vendor_name,
                order_sn: this.searchBar.purchase_sn,
                buyer_name: this.searchBar.buyer_name,
                start_date: this.searchBar.begin_date,
                end_date: this.searchBar.end_date,
                product_name: this.searchBar.product_name
            };

            await orderPurchaseRecordListAPI(params, this.pager.pageNum, this.pager.pageSize).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.pager.total = res.total;
                    if (res.rows.length !== 0) {
                        this.table.Datas = res.rows.map((m) => {
                            switch (parseInt(m.status)) {
                                case 0:
                                    m.status_name = '未审核';
                                    break;
                                case 1:
                                    m.status_name = '执行中';
                                    break;
                                case 2:
                                    m.status_name = '完成 ';
                                    break;
                                case 3:
                                    m.status_name = '终止';
                                    break;
                            }
                            return m;
                        });
                    }
                    this.table.Loading = false;
                }
            });
        },
        /**
         * 导出使用记录数据接口调用
         */
        async orderPurchaseRecordExportAPIFn() {
            if (this.exporting) {
                return;
            }
            this.exporting = true;
            const hideMessage = this.$message.loading('导出中...');
            let params = {
                partA: this.$cookie.get('userbelong'),
                partB_name: this.searchBar.organization_name,
                order_sn: this.searchBar.purchase_sn,
                start_date: this.searchBar.begin_date,
                end_date: this.searchBar.end_date,
                product_name: this.searchBar.product_name,
                product_vendor_name: this.searchBar.product_vendor_name
            };

            await orderPurchaseRecordExportAPI(params, this.pager.pageNum, this.pager.pageSize)
                .then(({ data, headers }) => {
                    const contentDisposition = headers['content-disposition'] || '';
                    const matchedFilenames = contentDisposition.match(/fileName=(.*)/);
                    const filename =
                        matchedFilenames.length > 1
                            ? decodeURIComponent(matchedFilenames[1])
                            : `${moment().format('YYYY-MM-DD')}.xlsx`;
                    this.$Utils.download(data, filename);
                })
                .catch((error) => {
                    console.error(error);
                })
                .finally(() => {
                    this.exporting = false;
                    hideMessage();
                });
        },
        /** ********************** 接口区域结束 ********************************************/

        /**
         * 导出按钮对应方法
         */
        handleExport() {
            this.orderPurchaseRecordExportAPIFn();
        },
        /**
         * 搜索按钮对e应方法
         */
        searchBtnFn() {
            if (this.searchBar.begin_date === '' || this.searchBar.end_date === '') {
                this.$warning({
                    title: '提示',
                    content: '请先选择日期再进行搜索',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            this.table.Datas = [];
            this.table.Loading = true;
            this.pager.pageNum = 1;
            this.orderPurchaseRecordListAPIFn();
        },
        /**
         * 搜索中选择日期方法
         * @param value -
         * @param dateString 获取的日期
         */
        searchBarDateChangeFn(value, dateString) {
            // 获取变更数据
            const [beginTime, endTime] = dateString;
            // 赋值到搜索栏数据
            this.searchBar.begin_date = beginTime;
            this.searchBar.end_date = endTime;
        },
        /**
         * 刷新表格的方法
         */
        refreshTableFn() {
            this.table.Loading = true;
            this.orderPurchaseRecordListAPIFn();
        },
        /**
         * 新增调拨单窗口关闭方法
         */
        ontablePaginationChangeFn({ currentPage, pageSize }) {
            this.pager.pageNum = currentPage;
            this.pager.pageSize = pageSize;

            this.refreshTableFn();
        },
        initPage() {
            this.table.Datas = [];
            this.table.Loading = false;
            this.pager.pageNum = 1;
            this.pager.pageSize = 10;
            this.searchBar.purchase_sn = '';
            this.searchBar.product_name = '';
            this.searchBar.begin_date = this.defaultDateRange[0];
            this.searchBar.end_date = this.defaultDateRange[1];
            this.searchBar.organization_name = '';
        },
        /**
         * 单击表格列触发的方法
         * @param {row} 点击的行信息
         */
        tableCellClickFn({ row }) {
            this.table.currentSelectedRow = row;
        },
        /**
         * 双击表格列触发的方法
         * @param {row} 点击的行信息
         */
        tableCellDblclickFn() {
            // if (!this.table.currentSelectedRow) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '请选中一条数据进行修改',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: true
            //     })
            //     return
            // }
            // this.$Utils.globalDragCenterFn('checkRecord')
            // this.checkModal.visible = true
            // if (this.$refs.checkRecordModal) {
            //     this.$refs.checkRecordModal.initPage(this.table.currentSelectedRow)
            // }
        },
        onCheckModalCancel() {
            // this.checkModal.visible = false
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
            return clientHeight - 166;
        }
    },
    created() {
        const dateFormat = 'YYYY/MM/DD';
        const startTime = new Date();
        startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * 30);
        const start = startTime;
        const end = new Date();
        this.defaultDateRange = [moment(start, dateFormat), moment(end, dateFormat)];
    },
    mounted() {
        this.initPage();
    }
};
</script>

<style lang="less" scoped>
.btn-export {
    width: 97px;
    text-align: center;
    color: #1e8702 !important;
    border-color: #1e8702 !important;
    overflow: hidden;
    transition: 0.3s all linear;
    margin-left: 40px;
    .btn-text-export {
        display: inline-block;
        vertical-align: middle;
        font-weight: 500;
        margin-top: -3px;
    }
    .yrt-excel {
        padding-right: 0;
        font-size: 22px;
        vertical-align: middle;
        color: #1e8702;
        margin-top: -3px;
    }
    &:hover {
        background: rgba(30, 135, 2) !important;
        color: #fff !important;
        border-color: rgba(30, 135, 2) !important;
        .yrt-excel {
            color: #fff;
        }
    }
}
</style>
