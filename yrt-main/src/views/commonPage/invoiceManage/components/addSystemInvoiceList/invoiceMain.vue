<template>
    <div style="height: 490px">
        <div class="tabs-product-wrap clear-pt">
            <div class="tabs-product-header mb-20">
                <ul class="tabs-product-nav">
                    <li
                        @click="changeTabsFn('inbound-invoice')"
                        :class="activeTab === 'inbound-invoice' ? 'active-tab' : ''"
                        >{{ isInbound ? '入库' : '出库' }}发票</li
                    >
                    <li
                        v-show="isShowSecondTab"
                        @click="changeTabsFn('advance-payment')"
                        :class="activeTab === 'advance-payment' ? 'active-tab' : ''"
                        >预付款</li
                    >
                </ul>
            </div>

            <invoiceSearch
                :isInbound="isInbound"
                :formData="formData"
                :searchData="searchData"
                :snLabel="activeTab === 'inbound-invoice' ? '发票号' : '付款编号'"
                @searchData="searchDataFn"
            ></invoiceSearch>

            <ul class="tabs-product-content">
                <!-- 入库发票 | 出库发票 -->
                <li v-show="activeTab === 'inbound-invoice'">
                    <div
                        class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                        style="padding-bottom: 0; margin-top: 0"
                    >
                        <vxe-grid
                            ref="invoiceInboundTable"
                            size="small"
                            :height="340 - ipadHeight"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            align="center"
                            :data="inboundInvoiceDatas"
                            :loading="inboundInvoicePagination.loading"
                            show-overflow
                            show-header-overflow
                            showHeader
                            :checkbox-config="{ reserve: true, highlight: true, range: false }"
                            @checkbox-change="checkboxChange"
                            @checkbox-all="checkboxAll"
                            :start-index="(inboundInvoicePagination.current - 1) * inboundInvoicePagination.pageSize"
                        >
                            <vxe-table-column fixed="left" width="50" type="checkbox"></vxe-table-column>
                            <!-- <vxe-table-column type="seq" title="NO." width="50"></vxe-table-column> -->
                            <vxe-table-column field="sn" title="发票编号" width="140"></vxe-table-column>
                            <vxe-table-column field="invoice_no" title="发票号" width="200"></vxe-table-column>
                            <vxe-table-column field="invoice_price" title="发票金额" width="120"></vxe-table-column>
                            <vxe-table-column field="written_off_price" title="核销金额" width="150"></vxe-table-column>
                        </vxe-grid>
                        <vxe-pager
                            :loading="inboundInvoicePagination.loading"
                            :current-page="inboundInvoicePagination.current"
                            :page-size="inboundInvoicePagination.pageSize"
                            :page-sizes="inboundInvoicePagination.pageSizes"
                            :total="inboundInvoicePagination.total"
                            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                            style="background: #ffffff"
                            @page-change="inboundInvoicePaginationOnChange"
                        >
                        </vxe-pager>
                    </div>
                </li>

                <!-- 预付款 -->
                <li v-show="isShowSecondTab ? activeTab === 'advance-payment' : false">
                    <div
                        class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                        style="padding-bottom: 0; margin-top: 0"
                    >
                        <vxe-grid
                            ref="advancePaymentTable"
                            size="small"
                            :height="340 - ipadHeight"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            align="center"
                            :data="advancePaymentDatas"
                            :loading="advancePaymentPagination.loading"
                            show-overflow
                            show-header-overflow
                            showHeader
                            :checkbox-config="{ reserve: true, highlight: true, range: false }"
                            @checkbox-change="checkboxChange"
                            @checkbox-all="checkboxAll"
                            :start-index="(advancePaymentPagination.current - 1) * advancePaymentPagination.pageSize"
                        >
                            <vxe-table-column fixed="left" width="50" type="checkbox"></vxe-table-column>
                            <!-- <vxe-table-column type="seq" title="NO." width="50"></vxe-table-column> -->
                            <vxe-table-column field="sn" title="预付款编号" width="140"></vxe-table-column>
                            <vxe-table-column field="invoice_no" title="发票号" width="200"></vxe-table-column>
                            <vxe-table-column field="invoice_price" title="发票金额" width="120"></vxe-table-column>
                            <vxe-table-column field="written_off_price" title="核销金额" width="150"></vxe-table-column>
                        </vxe-grid>
                        <vxe-pager
                            :loading="advancePaymentPagination.loading"
                            :current-page="advancePaymentPagination.current"
                            :page-size="advancePaymentPagination.pageSize"
                            :page-sizes="advancePaymentPagination.pageSizes"
                            :total="advancePaymentPagination.total"
                            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                            style="background: #ffffff"
                            @page-change="advancePaymentPaginationOnChange"
                        >
                        </vxe-pager>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import { financePaymentInvoiceListAPI, financeReceiveInvoiceListAPI } from '@/service/pageAjax';

export default {
    // 添加发票列表
    name: 'invoiceMain',
    props: {
        modalTitle: {
            type: String,
            default: ''
        },
        // 表单info数据
        formData: {
            type: Object,
            retuired: true
        },
        // 是否显示第二个tab选项卡 [采购退货和销售退货显示  财务人员的付款单显示  财务人员的收款单不显示]
        isShowSecondTab: {
            type: Boolean,
            default: true
        },
        isInbound: {
            type: Boolean,
            default: true
        }
    },
    data() {
        return {
            activeTab: 'inbound-invoice', // inbound-invoice: 入库发票  advance-payment: 预付款
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 57 : 0,
            searchData: {
                begin_date: '',
                end_date: '',
                sn: ''
            },
            inboundInvoiceDatas: [],
            inboundInvoicePagination: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            advancePaymentDatas: [],
            advancePaymentPagination: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            selectAllCheckboxRecords: {} // 存储的数据是 key：item  key的取值是 item.invoice_no + item.sn
        };
    },
    components: {
        'invoiceSearch': () => import('./invoiceSearch')
    },
    methods: {
        changeTabsFn(key) {
            // 切换tab
            if (key !== this.activeTab) {
                this.activeTab = key;
                // tab切换时自动加载列表数据
                this.searchDataFn(this.searchData);
            }
        },
        searchDataFn(searchData) {
            this.searchData = searchData;
            if (this.isInbound) {
                this.inboundInvoicePagination = {
                    loading: true,
                    total: 0,
                    current: 1,
                    pageSize: 10,
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                };
            } else {
                this.advancePaymentPagination = {
                    loading: true,
                    total: 0,
                    current: 1,
                    pageSize: 10,
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                };
            }
            this.searchListAPIFn();
        },
        // 【搜索】搜索系统发票列表
        async searchListAPIFn() {
            let xTable,
                params = {
                    billsType: this.formData.bills_type,
                    organization_inner_sn: this.formData.organization_inner_sn
                },
                type = 0, // 付款单：系统发票类型 0-入库发票 1-预付款  收款单没有type
                pageNum = 1,
                pageSize = 10;

            if (this.activeTab === 'inbound-invoice') {
                xTable = this.$refs.invoiceInboundTable;
                this.inboundInvoiceDatas = [];
                this.inboundInvoicePagination.loading = true;
                this.advancePaymentPagination.loading = false;

                type = 0;

                pageNum = this.inboundInvoicePagination.current;
                pageSize = this.inboundInvoicePagination.pageSize;
            } else {
                xTable = this.$refs.advancePaymentTable;
                this.advancePaymentDatas = [];
                this.advancePaymentPagination.loading = true;
                this.inboundInvoicePagination.loading = false;

                type = 1;

                pageNum = this.advancePaymentPagination.current;
                pageSize = this.advancePaymentPagination.pageSize;
            }

            if (this.searchData.begin_date) {
                params.begin_date = this.searchData.begin_date;
            }
            if (this.searchData.end_date) {
                params.end_date = this.searchData.end_date;
            }
            if (this.searchData.invoice_no) {
                params.invoice_no = this.searchData.invoice_no;
            }

            const service = this.isInbound ? financePaymentInvoiceListAPI : financeReceiveInvoiceListAPI;

            await service(params, pageNum, pageSize, type)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res,
                            rowData = this.isInbound
                                ? rows.map((item) => ({
                                      ...item,
                                      type // 付款单需要区分type
                                  }))
                                : rows; // 收款单不需要type，所以直接返回原值即可

                        if (this.activeTab === 'inbound-invoice') {
                            this.inboundInvoiceDatas = rowData;

                            xTable.refreshColumn();
                            xTable.loadData(this.inboundInvoiceDatas);
                            if (this.inboundInvoiceDatas.length) {
                                xTable.setCurrentRow(this.inboundInvoiceDatas[0]);
                            } else {
                                xTable.clearCheckboxRow();
                            }

                            this.inboundInvoicePagination = {
                                ...this.inboundInvoicePagination,
                                total,
                                loading: false
                            };
                        } else {
                            this.advancePaymentDatas = rowData;

                            xTable.refreshColumn();
                            xTable.loadData(this.advancePaymentDatas);
                            if (this.advancePaymentDatas.length) {
                                xTable.setCurrentRow(this.advancePaymentDatas[0]);
                            } else {
                                xTable.clearCheckboxRow();
                            }

                            this.advancePaymentPagination = {
                                ...this.advancePaymentPagination,
                                total,
                                loading: false
                            };
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    if (this.activeTab === 'inbound-invoice') {
                        this.inboundInvoicePagination.loading = false;
                    } else {
                        this.advancePaymentPagination.loading = false;
                    }
                });
        },
        inboundInvoicePaginationOnChange({ currentPage, pageSize }) {
            this.inboundInvoicePagination = {
                ...this.inboundInvoicePagination,
                current: currentPage,
                pageSize: pageSize,
                loading: true
            };
            this.inboundInvoiceDatas = [];
            this.searchListAPIFn();
        },
        advancePaymentPaginationOnChange({ currentPage, pageSize }) {
            this.advancePaymentPagination = {
                ...this.advancePaymentPagination,
                current: currentPage,
                pageSize: pageSize,
                loading: true
            };
            this.advancePaymentDatas = [];
            this.searchListAPIFn();
        },
        checkboxChange({ checked, row }) {
            if (checked) {
                if (this.activeTab === 'inbound-invoice') {
                    this.$refs.invoiceInboundTable.setCurrentRow(row);
                } else {
                    this.$refs.advancePaymentTable.setCurrentRow(row);
                }
            }
            this.setCheckboxReserveRecordsFn([row], checked);
        },
        checkboxAll({ checked, records }) {
            if (!checked) {
                if (this.activeTab === 'inbound-invoice') {
                    records = this.$refs.invoiceInboundTable.getTableData().tableData;
                } else {
                    records = this.$refs.advancePaymentTable.getTableData().tableData;
                }
            }
            this.setCheckboxReserveRecordsFn(records, checked);
        },
        setCheckboxReserveRecordsFn(records, checked) {
            for (let i = 0; i < records.length; i++) {
                let item = records[i],
                    curKeyFormat = item.invoice_no + item.sn;

                if (checked) {
                    // 勾选上
                    this.selectAllCheckboxRecords[curKeyFormat] = item;
                } else {
                    delete this.selectAllCheckboxRecords[curKeyFormat];
                }
            }
        },
        // 获取勾选上的数据
        getSelectedDataFn() {
            return this.selectAllCheckboxRecords;
        }
    },
    mounted() {
        this.searchListAPIFn();
    }
};
</script>

<style lang="less" scoped>
.clear-pt {
    padding-top: 0;
}

.mb-20 {
    margin-bottom: 20px;
}
</style>
