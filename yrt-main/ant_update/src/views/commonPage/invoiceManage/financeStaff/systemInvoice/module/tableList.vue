<template>
    <div>
        <vxe-table
            :ref="refName"
            size="small"
            :height="scrollYNumber - scrollYNumberBot"
            border
            resizable
            auto-resize
            highlight-current-row
            align="center"
            show-overflow
            show-header-overflow
            showHeader
            @cell-click="cellClickFn"
            @cell-dblclick="cellDblclickFn"
            :data="invoiceDatas"
            :loading="this[`pagination${refName}`].loading"
            :start-index="(this[`pagination${refName}`].current - 1) * this[`pagination${refName}`].pageSize"
        >
            <vxe-table-column field="system_invoice_sn" title="系统发票号" width="120" show-overflow></vxe-table-column>
            <vxe-table-column field="organization_name" title="相关机构" width="100" show-overflow></vxe-table-column>
            <vxe-table-column field="handler_name" title="经手人" width="80" show-overflow></vxe-table-column>
            <vxe-table-column field="department_name" title="部门" width="80" show-overflow></vxe-table-column>
            <vxe-table-column field="invoice_no" title="发票号" width="100" show-overflow></vxe-table-column>
            <vxe-table-column field="invoice_date" title="开票日期" width="100" show-overflow></vxe-table-column>
            <vxe-table-column field="invoice_type" title="发票类型" width="100" show-overflow>
                <template v-slot="{ row }">
                    {{ row.invoice_type | invoiceTypeFilter }}
                </template>
            </vxe-table-column>
            <vxe-table-column field="rate" title="税率" width="60" show-overflow>
                <template v-slot="{ row }">
                    {{ row.rate | rateMulFilter }}
                </template>
            </vxe-table-column>
            <vxe-table-column field="invoice_price" title="开票金额" width="90" show-overflow></vxe-table-column>
            <vxe-table-column field="written_off_price" title="核销金额" width="100" show-overflow></vxe-table-column>
            <vxe-table-column field="invoice_status" title="发票状态" width="90" show-overflow>
                <template v-slot="{ row }">
                    {{ row.invoice_status | invoiceStateFilter }}
                </template>
            </vxe-table-column>
            <vxe-table-column field="description" title="摘要" width="100" show-overflow></vxe-table-column>
        </vxe-table>
        <vxe-pager
            :loading="this[`pagination${refName}`].loading"
            :current-page="this[`pagination${refName}`].current"
            :page-size="this[`pagination${refName}`].pageSize"
            :page-sizes="this[`pagination${refName}`].pageSizes"
            :total="this[`pagination${refName}`].total"
            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="changePaginationFn"
        >
        </vxe-pager>

        <!-- 发票明细 -->
        <vxe-table
            ref="invoiceDetailTable"
            size="small"
            :height="scrollYNumberBot"
            border
            resizable
            auto-resize
            highlight-current-row
            align="center"
            show-overflow
            show-header-overflow
            showHeader
            :data="invoiceDetailDatas"
            :loading="invoiceDetailPagation.loading"
        >
            <vxe-table-column field="product_vendor_name" title="厂商名称" width="180" show-overflow></vxe-table-column>
            <vxe-table-column field="product_name" title="产品名称" width="150" show-overflow></vxe-table-column>
            <vxe-table-column field="specification" title="规格型号" width="100" show-overflow></vxe-table-column>
            <vxe-table-column field="unit" title="单位" width="120" show-overflow></vxe-table-column>
            <vxe-table-column field="unit_price" title="单价" width="100" show-overflow></vxe-table-column>
            <vxe-table-column field="quantity" title="数量" width="80" show-overflow></vxe-table-column>
            <vxe-table-column field="rate" title="税率" width="60" show-overflow>
                <template v-slot="{ row }">
                    {{ row.rate | rateMulFilter }}
                </template>
            </vxe-table-column>
            <vxe-table-column field="invoiced_quantity" title="开票数量" width="90" show-overflow></vxe-table-column>
            <vxe-table-column field="remarks" title="备注" width="120" show-overflow></vxe-table-column>
        </vxe-table>
    </div>
</template>

<script>
import { pageType, systemInvoiceTabFinance } from '@/views/commonPage/invoiceManage/config/constants';
import { filterFormat } from '@/views/commonPage/invoiceManage/config/filters.js';

import { financeInvoiceListAPI, financeInvoiceDetailListAPI } from '@/service/pageAjax';

export default {
    props: {
        searchData: {
            type: Object, // 搜索过滤的条件
            required: true
        },
        isInbound: {
            type: Boolean, // 是否是入库发票 === 默认是true
            default: true
        },
        refName: {
            // 表格的ref，同时也是当前tab选项卡的标识key
            type: String,
            required: true
        },
        scrollYNumber: {
            // 表格的高度所需值
            type: Number,
            default: 0
        },
        isRefresh: {
            // 刷新表格的数据标识
            type: Boolean,
            default: false
        },
        btnName: {
            type: String,
            required: true
        },
        tableDetailCallbackData: {
            // 表格详情回传的数据
            type: Object,
            default: function () {
                return {};
            }
        }
    },
    data() {
        return {
            scrollYNumberBot: 300,
            // 待审核 - 表格数据
            datasPENDING: [],
            // 待审核 - 表格分页器数据
            paginationPENDING: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40],
                isRefresh: false
            },
            // 未核销 - 表格数据
            datasNOT_WRITTEN_OFF: [],
            // 未核销 - 表格分页器数据
            paginationNOT_WRITTEN_OFF: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40],
                isRefresh: false
            },
            // 已核销 - 表格数据
            datasWRITTEN_OFF: [],
            // 已核销 - 表格分页器数据
            paginationWRITTEN_OFF: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40],
                isRefresh: false
            },
            // 已作废 - 表格数据
            datasHAVE_BEEN_VOIDED: [],
            // 已作废 - 表格分页器数据
            paginationHAVE_BEEN_VOIDED: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40],
                isRefresh: false
            },
            invoiceDetailDatas: [],
            invoiceDetailPagation: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            rowData: {}
        };
    },
    methods: {
        async getTableDatasAPIFn() {
            this[`pagination${this.refName}`].loading = true;
            const searchData = { ...this.searchData };

            if (!searchData.organization_name) {
                delete searchData.billsType;
                delete searchData.organization_inner_sn;
            }
            delete searchData.organization_name;

            const params = {};
            if (this.refName !== systemInvoiceTabFinance['3'].value) {
                // 不是已作废状态的tab选项卡 [是否作废 默认0，0-未作废 1-作废]
                params.isCancel = 0;
                params.invoice_status = this.refName;
            } else {
                params.isCancel = 1;
                // params.invoice_status = ''
            }

            await financeInvoiceListAPI(
                {
                    ...searchData,
                    ...params,
                    system_invoice_type: this.searchData.system_invoice_type === pageType['INBOUND'].value ? 0 : 1 // 发票类型 默认0-入库发票 1-出库发票
                },
                this[`pagination${this.refName}`].current,
                this[`pagination${this.refName}`].pageSize
            )
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res,
                            datas = this[`datas${this.refName}`],
                            refDom = this.$refs[this.refName];

                        this[`datas${this.refName}`].push(...rows);
                        this[`pagination${this.refName}`].total = total;
                        this[`pagination${this.refName}`].isRefresh = true;

                        refDom.loadData(datas);
                        if (datas && datas.length) {
                            const row = datas[0];
                            refDom.setCurrentRow(row);

                            this.getTableDataDetailAPIFn(row.id);

                            this.$emit('getRowDataFn', row);
                        } else {
                            refDom.clearCurrentRow();
                            this.invoiceDetailDatas = [];
                            this.$emit('getRowDataFn', {});
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this[`pagination${this.refName}`].loading = false;
                });
        },
        async getTableDataDetailAPIFn(id) {
            this.invoiceDetailPagation.loading = true;

            await financeInvoiceDetailListAPI(id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.invoiceDetailDatas = list;
                        this.$refs.invoiceDetailTable.loadData(this.invoiceDetailDatas);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.invoiceDetailPagation.loading = false;
                });
        },
        initTable() {
            this.datas = [];
            this[`datas${this.refName}`] = [];
            this[`pagination${this.refName}`] = {
                loading: false,
                total: 0,
                current: 1,
                pageSize: this.isRefresh ? this[`pagination${this.refName}`].pageSize : 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40],
                isRefresh: false
            };
        },
        initTableData() {
            this.initTable();
            this.getTableDatasAPIFn();
        },
        changePaginationFn({ currentPage, pageSize }) {
            this[`datas${this.refName}`] = [];
            this[`pagination${this.refName}`].current = currentPage;
            this[`pagination${this.refName}`].pageSize = pageSize;
            this[`pagination${this.refName}`].loading = true;
            this.getTableDatasAPIFn();
        },
        cellClickFn({ row }) {
            this.$emit('getRowDataFn', row);

            this.getTableDataDetailAPIFn(row.id);
        },
        cellDblclickFn({ row }) {
            this.getTableDataDetailAPIFn(row.id);

            this.$emit('lookDetailFn', row);
        }
    },
    computed: {
        pagination() {
            return this[`pagination${this.refName}`];
        }
    },
    watch: {
        refName: {
            handler(newVal, oldVal) {
                if (newVal !== oldVal) {
                    this.initTableData();
                }
            },
            immediate: true
        },
        isInbound: {
            handler(newVal, oldVal) {
                if (newVal !== oldVal) {
                    this.paginationPENDING.isRefresh = false;
                    this.paginationNOT_WRITTEN_OFF.isRefresh = false;
                    this.paginationWRITTEN_OFF.isRefresh = false;
                    this.paginationHAVE_BEEN_VOIDED.isRefresh = false;

                    if (!this[`pagination${this.refName}`].isRefresh) {
                        this.initTableData();
                    }
                }
            },
            immediate: true
        },
        isRefresh(newVal) {
            if (newVal) {
                this.initTableData();
            }
        },
        tableDetailCallbackData: {
            handler(newVal) {
                if (newVal) {
                    this.initTableData();
                }
            },
            deep: true
        }
    },
    filters: filterFormat.filters
};
</script>

<style lang="less" scoped></style>
