<template>
    <div>
        <vxe-table
            :ref="refName"
            size="small"
            :height="scrollYNumber"
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
            <!-- <vxe-table-column type="checkbox" width="50"></vxe-table-column> -->
            <vxe-table-column field="payment_sn" title="付款编号" width="120" show-overflow></vxe-table-column>
            <vxe-table-column field="organization_name" title="相关机构" width="100" show-overflow></vxe-table-column>
            <vxe-table-column field="handler_name" title="经手人" width="80" show-overflow></vxe-table-column>
            <vxe-table-column field="department_name" title="部门" width="80" show-overflow></vxe-table-column>
            <vxe-table-column field="payment_price" title="付款金额" width="100" show-overflow></vxe-table-column>
            <vxe-table-column field="payment_method" title="付款方式" width="100" show-overflow>
                <template v-slot="{ row }">
                    {{ row.payment_method | paymentAdvanceMethodFilter }}
                </template>
            </vxe-table-column>
            <vxe-table-column field="payment_date" title="付款日期" width="100" show-overflow></vxe-table-column>
            <vxe-table-column field="confirm_by" title="付款人" width="80" show-overflow></vxe-table-column>
            <vxe-table-column field="written_off_price" title="核销金额" width="90" show-overflow></vxe-table-column>
            <vxe-table-column field="payment_status" title="状态" width="90" show-overflow>
                <template v-slot="{ row }">
                    {{ row.payment_status | invoiceStateFilter }}
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
    </div>
</template>

<script>
import { systemInvoiceTabFinance } from '@/views/commonPage/invoiceManage/config/constants';
import { filterFormat } from '@/views/commonPage/invoiceManage/config/filters.js';

import { financeAdvanceListAPI } from '@/service/pageAjax';

export default {
    props: {
        searchData: {
            type: Object, // 搜索过滤的条件
            required: true
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
            // 待审核 - 表格数据
            datasPENDING: [],
            // 待审核 - 表格分页器数据
            paginationPENDING: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
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
                pageSizes: [10, 20, 30, 40]
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
                pageSizes: [10, 20, 30, 40]
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

            await financeAdvanceListAPI(
                {
                    ...searchData,
                    ...params
                },
                this[`pagination${this.refName}`].current,
                this[`pagination${this.refName}`].pageSize
            )
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res,
                            refDom = this.$refs[this.refName];

                        this[`datas${this.refName}`].push(...rows);
                        this[`pagination${this.refName}`].total = total;

                        refDom.loadData(rows);
                        if (rows && rows.length) {
                            const row = rows[0];
                            refDom.setCurrentRow(row);

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
        initTable() {
            this.datas = [];
            this[`datas${this.refName}`] = [];
            this[`pagination${this.refName}`] = {
                loading: false,
                total: 0,
                current: 1,
                pageSize: this.isRefresh ? this[`pagination${this.refName}`].pageSize : 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
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
        },
        cellDblclickFn({ row }) {
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
