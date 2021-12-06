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
            :checkbox-config="{ highlight: true, strict: true, range: true }"
            @cell-click="cellClickFn"
            @cell-dblclick="cellDblclickFn"
            @checkbox-change="selectChangeEvent"
            :data="invoiceDatas"
            :loading="this[`pagination${refName}`].loading"
            :start-index="(this[`pagination${refName}`].current - 1) * this[`pagination${refName}`].pageSize"
        >
            <vxe-table-column type="checkbox" width="50"></vxe-table-column>
            <!-- 付款单编号 -->
            <vxe-table-column
                v-if="this.isInbound"
                field="payment_sn"
                title="付款编号"
                width="120"
                show-overflow
            ></vxe-table-column>
            <!-- 收款单编号 -->
            <vxe-table-column v-else field="receive_sn" title="收款编号" width="120" show-overflow></vxe-table-column>
            <vxe-table-column field="organization_name" title="相关机构" width="100" show-overflow></vxe-table-column>
            <vxe-table-column
                :field="this.isInbound ? 'payment_price' : 'receive_price'"
                :title="`${this.isInbound ? '付款' : '收款'}金额`"
                width="80"
                show-overflow
            ></vxe-table-column>
            <vxe-table-column
                :field="this.isInbound ? 'payment_date' : 'receive_date'"
                :title="`${this.isInbound ? '付款' : '收款'}日期`"
                width="80"
                show-overflow
            ></vxe-table-column>
            <!-- 付款单的方式 -->
            <vxe-table-column v-if="this.isInbound" field="payment_method" title="付款方式" width="80" show-overflow>
                <template v-slot="{ row }">
                    {{ row.payment_method | paymentAdvanceMethodFilter }}
                </template>
            </vxe-table-column>
            <!-- 收款单的方式 -->
            <vxe-table-column v-else field="receive_method" title="收款方式" width="80" show-overflow>
                <template v-slot="{ row }">
                    {{ row.receive_method | paymentAdvanceMethodFilter }}
                </template>
            </vxe-table-column>
            <vxe-table-column field="created_by" title="创建人" width="100" show-overflow></vxe-table-column>
            <vxe-table-column field="created_time" title="创建时间" width="80" show-overflow></vxe-table-column>
            <vxe-table-column
                :field="this.isInbound ? 'payment_by' : 'receive_by'"
                :title="`${this.isInbound ? '付款' : '收款'}人`"
                width="60"
                show-overflow
            ></vxe-table-column>
            <vxe-table-column
                :field="this.isInbound ? 'payment_time' : 'receive_time'"
                :title="`${this.isInbound ? '付款' : '收款'}时间`"
                width="180"
                show-overflow
            ></vxe-table-column>
            <vxe-table-column field="entry_by" title="入账人" width="100" show-overflow></vxe-table-column>
            <vxe-table-column field="entry_time" title="入账时间" width="90" show-overflow></vxe-table-column>
            <!-- 付款单的状态 -->
            <vxe-table-column v-if="this.isInbound" field="payment_status" title="状态" width="80" show-overflow>
                <template v-slot="{ row }">
                    {{ row.payment_status | paymentInvoiceStateFilter }}
                </template>
            </vxe-table-column>
            <!-- 收款单的状态 -->
            <vxe-table-column v-else field="receive_status" title="状态" width="80" show-overflow>
                <template v-slot="{ row }">
                    {{ row.receive_status | receiveInvoiceStateFilter }}
                </template>
            </vxe-table-column>
            <vxe-table-column field="description" title="摘要" width="100" show-overflow></vxe-table-column>
            <vxe-table-column v-if="isShowCancelColumn" field="is_cancel" title="是否作废" width="80" show-overflow>
                <template v-slot="{ row }">
                    {{ row.is_cancel | cancelStateFilter }}
                </template>
            </vxe-table-column>
            <vxe-table-column
                v-if="isShowCancelColumn"
                field="cancel_reason"
                title="作废原因"
                width="100"
                show-overflow
            ></vxe-table-column>
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
            <vxe-table-column
                v-for="invoiceColumnItem in invoiceDetailColumns"
                :key="invoiceColumnItem.key"
                :field="invoiceColumnItem.dataIndex"
                :title="invoiceColumnItem.title"
                :width="invoiceColumnItem.width"
                show-overflow
            ></vxe-table-column>
        </vxe-table>
    </div>
</template>

<script>
import { paymentTabFinance, receiptTabFinance, format } from '@/views/commonPage/invoiceManage/config/constants';
import { filterFormat } from '@/views/commonPage/invoiceManage/config/filters.js';

import {
    financePaymentListAPI,
    financeReceiveListAPI,
    financePaymentInvoiceDetailListAPI,
    financeReceiveInvoiceDetailListAPI,
    financePaymentDelAPI,
    financeReceiveDelAPI,
    financePaymentPayAPI,
    financeReceivePayAPI,
    financePaymentRecordedAPI,
    financeReceiveRecordedAPI
} from '@/service/pageAjax';

import moment from 'moment';

export default {
    props: {
        searchData: {
            type: Object, // 搜索过滤的条件
            required: true
        },
        isInbound: {
            type: Boolean, // 是否是付款单-true  收款单-false
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
            default: null
        }
    },
    data() {
        return {
            scrollYNumberBot: 300,
            // 有效付款单 - 表格数据
            datasVALID_PAYMENT_NOTE: [],
            // 有效付款单 - 表格分页器数据
            paginationVALID_PAYMENT_NOTE: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            // 作废付款单 - 表格数据
            datasCANCEL_A_BILL_OF_PAYMENT: [],
            // 作废付款单 - 表格分页器数据
            paginationCANCEL_A_BILL_OF_PAYMENT: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            // 有效收款单 - 表格数据
            datasVALID_RECEIPT: [],
            // 有效收款单 - 表格分页器数据
            paginationVALID_RECEIPT: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            // 作废收款单 - 表格数据
            datasNULLIFY_RECEIPT: [],
            // 作废收款单 - 表格分页器数据
            paginationNULLIFY_RECEIPT: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            invoiceDetailDatas: [],
            invoiceDetailPagation: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            }
        };
    },
    methods: {
        async getTableDatasAPIFn(service, paganationData) {
            this[`pagination${this.refName}`].loading = true;
            const searchData = { ...this.searchData };

            // 去除无效字段
            if (!searchData.organization_name) {
                delete searchData.billsType;
                delete searchData.organization_inner_sn;
            }
            delete searchData.organization_name;

            // 根据付款和收款单的字段构造数据
            const params = {
                ...searchData
            };
            if (this.isInbound) {
                params.payment_sn = params.invoice_no;
                params.isCancel = this.refName === paymentTabFinance['0'].value ? 0 : 1;
                params.payment_status = params.invoice_status;
            } else {
                params.receive_sn = params.invoice_no;
                params.isCancel = this.refName === receiptTabFinance['0'].value ? 0 : 1; // 是否作废 默认0，0-未作废 1-作废
                params.receive_status = params.invoice_status;
            }
            delete params.invoice_no;
            delete params.invoice_status;

            await service(params, paganationData.current, paganationData.pageSize)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res,
                            datas = this[`datas${this.refName}`],
                            refDom = this.$refs[this.refName];

                        this[`datas${this.refName}`].push(...rows);
                        this[`pagination${this.refName}`].total = total;

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

            const service = this.isInbound ? financePaymentInvoiceDetailListAPI : financeReceiveInvoiceDetailListAPI;
            await service(id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.invoiceDetailDatas = rows;
                        this.$refs.invoiceDetailTable.loadData(this.invoiceDetailDatas);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.invoiceDetailPagation.loading = false;
                });
        },
        async financePaymentAndReceiveDelAPIFn(selectedIds) {
            this[`pagination${this.refName}`].loading = true;
            const service = this.isInbound ? financePaymentDelAPI : financeReceiveDelAPI;
            await service(selectedIds)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.initTableData();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this[`pagination${this.refName}`].loading = false;
                });
        },
        async financePaymentAndReceivePayAPIFn(selectedIds) {
            this[`pagination${this.refName}`].loading = true;
            const service = this.isInbound ? financePaymentPayAPI : financeReceivePayAPI;
            await service(selectedIds)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.initTableData();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this[`pagination${this.refName}`].loading = false;
                });
        },
        async financePaymentAndReceiveRecordedAPIFn(selectedIds) {
            this[`pagination${this.refName}`].loading = true;
            const service = this.isInbound ? financePaymentRecordedAPI : financeReceiveRecordedAPI;
            await service(selectedIds)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.initTableData();
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
        getServiceApiFn() {
            if (this.isInbound) {
                this.getTableDatasAPIFn(financePaymentListAPI, this[`pagination${this.refName}`]);
            } else {
                this.getTableDatasAPIFn(financeReceiveListAPI, this[`pagination${this.refName}`]);
            }
        },
        initTableData() {
            this.initTable();
            this.getServiceApiFn();
        },
        changePaginationFn({ currentPage, pageSize }) {
            this[`datas${this.refName}`] = [];
            this[`pagination${this.refName}`].current = currentPage;
            this[`pagination${this.refName}`].pageSize = pageSize;
            this[`pagination${this.refName}`].loading = true;
            this.getServiceApiFn();
        },
        cellClickFn({ row }) {
            // 缓冲单击操作
            clearTimeout(this.clickTimer);
            this.clickTimer = setTimeout(() => {
                this.$emit('getRowDataFn', row);
                this.getTableDataDetailAPIFn(row.id);
            }, 500);
        },
        cellDblclickFn({ row }) {
            // 双击时取消单击操作
            clearTimeout(this.clickTimer);
            this.getTableDataDetailAPIFn(row.id);
            this.$emit('lookDetailFn', row);
        },
        selectChangeEvent({ records }) {
            this.$refs[this.refName].setCurrentRow(records);
            this.$emit('getRowDataFn', records);
        },
        /**
         * @todo 可以考虑单独提出操作过滤逻辑
         */
        groupBtnFn(action) {
            const selectedIds = [];
            const selectedRows = this.$refs[this.refName].getCheckboxRecords(true);
            const rowsCount = selectedRows.length;
            // 删除单据
            let deleteAble = true;
            // 收/付款
            let payOrReceiveAble = true;
            // 入账
            let recordAble = true;

            // 未勾选数据
            if (rowsCount <= 0) {
                this.$message.warning('请至少勾选一张单据再进行此操作');
                return;
            }

            for (let i = 0; i < rowsCount; i += 1) {
                // 付款单
                if (this.isInbound) {
                    switch (selectedRows[i].payment_status) {
                        case 'TO_BE_PAID':
                            recordAble = false;
                            break;
                        case 'PAID':
                            payOrReceiveAble = false;
                            deleteAble = false;
                            break;
                        case 'RECORDED':
                            payOrReceiveAble = false;
                            recordAble = false;
                            deleteAble = false;
                    }
                } else {
                    // 收款单
                    switch (selectedRows[i].receive_status) {
                        case 'TO_BE_COLLECTED':
                            recordAble = false;
                            break;
                        case 'RECEIVED':
                            payOrReceiveAble = false;
                            deleteAble = false;
                            break;
                        case 'RECORDED':
                            payOrReceiveAble = false;
                            recordAble = false;
                            deleteAble = false;
                    }
                }
                selectedIds.push(selectedRows[i].id);
            }

            switch (action) {
                case 'del':
                    if (!deleteAble) {
                        this.$message.warning('待付款状态的单据才能进行此操作');
                        return;
                    }
                    this.$confirm({
                        title: '删除',
                        content: '您确定要执行该操作吗？',
                        centered: true,
                        maskClosable: false,
                        onOk: () => {
                            this.financePaymentAndReceiveDelAPIFn(selectedIds);
                        }
                    });
                    break;
                case 'pay': // 付款 | 收款
                    if (!payOrReceiveAble) {
                        this.$message.warning(`待${this.isInbound ? '付' : '收'}款状态的单据才能进行此操作`);
                        return;
                    }
                    this.financePaymentAndReceivePayAPIFn(selectedIds);
                    break;
                case 'recorded': // 入账
                    if (!recordAble) {
                        this.$message.warning(`已${this.isInbound ? '付' : '收'}款状态的单据才能进行此操作`);
                        return;
                    }
                    this.financePaymentAndReceiveRecordedAPIFn(selectedIds);
                    break;
            }
        }
    },
    computed: {
        pagination() {
            return this[`pagination${this.refName}`];
        },
        isShowCancelColumn() {
            return this.refName === paymentTabFinance[1].value || this.refName === receiptTabFinance[1].value;
        },
        // 发票明细区域
        invoiceDetailColumns() {
            return [
                {
                    title: this.isInbound ? '发票编号/预付款编号' : '发票编号',
                    dataIndex: 'sn',
                    key: 'sn',
                    width: '150px',
                    align: 'center'
                },
                {
                    title: '发票号',
                    dataIndex: 'invoice_no',
                    key: 'invoice_no',
                    width: '120px',
                    align: 'center'
                },
                {
                    title: '发票金额',
                    dataIndex: 'invoice_price',
                    key: 'invoice_price',
                    width: '100px',
                    align: 'center'
                },
                {
                    title: '核销金额',
                    dataIndex: 'written_off_price',
                    key: 'written_off_price',
                    width: '80px',
                    align: 'center'
                }
            ];
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
                    const datas = this[`datas${this.refName}`],
                        refDom = this.$refs[this.refName];

                    if (this.btnName === 'add') {
                        delete newVal.details;

                        datas.push(newVal);
                        this[`pagination${this.refName}`].total += 1;
                        refDom.reloadData(datas);
                        refDom.setCurrentRow(datas[datas.length - 1]);

                        this.getTableDataDetailAPIFn(newVal.id);
                    } else if (this.btnName === 'edit') {
                        for (let i = 0; i < datas.length; i++) {
                            let item = datas[i];
                            if (item.id === newVal.id) {
                                this.$set(datas, i, {
                                    ...item,
                                    ...newVal,
                                    invoice_date: moment(newVal.invoice_date).format(format)
                                });
                                refDom.reloadData(datas);
                                refDom.setCurrentRow(datas[0]);

                                this.getTableDataDetailAPIFn(newVal.id);
                                // this.invoiceDetailDatas = newVal.details || []
                                // this.$refs.invoiceDetailTable.loadData(this.invoiceDetailDatas)
                                break;
                            }
                        }
                    }
                }
            },
            deep: true
        }
    },
    filters: filterFormat.filters
};
</script>

<style lang="less" scoped></style>
