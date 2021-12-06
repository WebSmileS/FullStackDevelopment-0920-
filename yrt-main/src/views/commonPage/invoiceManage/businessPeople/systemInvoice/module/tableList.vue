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
            :loading="this[`pagination${refName}`].loading"
            :start-index="(this[`pagination${refName}`].current - 1) * this[`pagination${refName}`].pageSize"
        >
            <vxe-table-column type="checkbox" width="50"></vxe-table-column>
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
import { systemInvoiceTab, format } from '@/views/commonPage/invoiceManage/config/constants';
import {
    invoiceGrnListAPI,
    invoiceOdoListAPI,
    invoiceGrnAndOdoDetailListAPI,
    invoiceGrnAndOdoDelAPI,
    invoiceGrnSubmitAPI,
    invoiceOdoSubmitAPI
} from '@/service/pageAjax';

import { filterFormat } from '@/views/commonPage/invoiceManage/config/filters.js';

import moment from 'moment';

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
            default: null
        }
    },
    data() {
        return {
            scrollYNumberBot: 300,
            // 未作废发票 - 表格数据
            datasUNVOIDED_INVOICES: [],
            // 未作废发票 - 表格分页器数据
            paginationUNVOIDED_INVOICES: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            // 作废发票 - 表格数据
            datasVOIDED_INVOICES: [],
            // 作废发票 - 表格分页器数据
            paginationVOIDED_INVOICES: {
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
        async getTableDatasAPIFn(service, params) {
            this[`pagination${this.refName}`].loading = true;
            const searchData = { ...this.searchData };

            if (!searchData.organization_name) {
                delete searchData.organization_inner_sn;
                delete searchData.billsType;
            }
            delete searchData.organization_name;

            await service(
                {
                    ...searchData,
                    isCancel: this.refName === systemInvoiceTab['0'].value ? 0 : 1 // 是否作废 默认0，0-未作废 1-作废
                },
                params.current,
                params.pageSize
            )
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

            await invoiceGrnAndOdoDetailListAPI(id)
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
        async invoiceGrnAndOdoDelAPIFn(selectedIds) {
            this[`pagination${this.refName}`].loading = true;
            await invoiceGrnAndOdoDelAPI(selectedIds)
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
        async invoiceGrnAndOdoSubmitAPIFn(selectedIds) {
            this[`pagination${this.refName}`].loading = true;
            const service = this.isInbound ? invoiceGrnSubmitAPI : invoiceOdoSubmitAPI;
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
                this.getTableDatasAPIFn(invoiceGrnListAPI, this[`pagination${this.refName}`]);
            } else {
                this.getTableDatasAPIFn(invoiceOdoListAPI, this[`pagination${this.refName}`]);
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
            // 删除发票
            let deleteAble = true;
            // 审核
            let auditAble = true;

            // 未勾选数据
            if (rowsCount <= 0) {
                this.$message.warning('请至少勾选一张发票再进行此操作');
                return;
            }

            for (let i = 0; i < rowsCount; i += 1) {
                // 入库发票
                if (this.isInbound) {
                    switch (selectedRows[i].invoice_status) {
                        // 草稿：可删除、可审核
                        case 'DRAFT':
                            break;
                        // 待审核：可删除
                        case 'PENDING':
                            auditAble = false;
                            break;
                        case 'NOT_WRITTEN_OFF':
                            deleteAble = false;
                            auditAble = false;
                            break;
                        case 'PARTIAL_WRITE_OFF':
                            deleteAble = false;
                            auditAble = false;
                            break;
                        case 'WRITTEN_OFF':
                            deleteAble = false;
                            auditAble = false;
                            break;
                    }
                } else {
                    // 出库发票
                    switch (selectedRows[i].invoice_status) {
                        // 草稿：可删除、可审核
                        case 'DRAFT':
                            break;
                        // 待审核：可删除
                        case 'PENDING':
                            auditAble = false;
                            break;
                        case 'NOT_WRITTEN_OFF':
                            deleteAble = false;
                            auditAble = false;
                            break;
                        case 'PARTIAL_WRITE_OFF':
                            deleteAble = false;
                            auditAble = false;
                            break;
                        case 'WRITTEN_OFF':
                            deleteAble = false;
                            auditAble = false;
                            break;
                    }
                }
                selectedIds.push(selectedRows[i].id);
            }

            if (action === 'del') {
                if (!deleteAble) {
                    this.$message.warning('草稿和待审核状态的单据才可进行此操作');
                    return;
                }
                this.$confirm({
                    title: '删除',
                    content: '您确定要执行该操作吗？',
                    centered: true,
                    maskClosable: false,
                    onOk: () => {
                        this.invoiceGrnAndOdoDelAPIFn(selectedIds);
                    }
                });
            } else if (action === 'audit') {
                if (!auditAble) {
                    this.$message.warning('草稿状态的单据才可进行此操作');
                    return;
                }
                this.invoiceGrnAndOdoSubmitAPIFn(selectedIds);
            }
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
                                    invoice_date: newVal.invoice_date
                                        ? moment(newVal.invoice_date).format(format)
                                        : null
                                });
                                refDom.reloadData(datas);
                                refDom.setCurrentRow(datas[0]);

                                this.getTableDataDetailAPIFn(newVal.id);
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
