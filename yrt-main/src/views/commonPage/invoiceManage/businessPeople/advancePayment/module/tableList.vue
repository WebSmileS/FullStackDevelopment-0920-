<template>
    <div style="height: 100%">
        <vxe-table
            ref="paymentTableListRef"
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
            :checkbox-config="{ highlight: true, strict: true, range: true }"
            @cell-click="cellClickFn"
            @cell-dblclick="cellDblclickFn"
            @checkbox-change="selectChangeEvent"
            :data="invoiceDatas"
            :loading="pagination.loading"
            :start-index="(pagination.current - 1) * pagination.pageSize"
        >
            <vxe-table-column type="checkbox" width="50"></vxe-table-column>
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
            :loading="pagination.loading"
            :current-page="pagination.current"
            :page-size="pagination.pageSize"
            :page-sizes="pagination.pageSizes"
            :total="pagination.total"
            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="changePaginationFn"
        >
        </vxe-pager>
    </div>
</template>

<script>
import { format, systemAdvancePaymentTab } from '@/views/commonPage/invoiceManage/config/constants';
import { filterFormat } from '@/views/commonPage/invoiceManage/config/filters.js';
import { invoiceAdvanceListAPI, invoiceAdvanceDelAPI, invoiceAdvanceSubmitAPI } from '@/service/pageAjax';

import moment from 'moment';

export default {
    props: {
        searchData: {
            type: Object, // 搜索过滤的条件
            required: true
        },
        refName: {
            type: String,
            required: true,
            validator(value) {
                return value === systemAdvancePaymentTab[0].value || value === systemAdvancePaymentTab[1].value;
            }
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
            // 待审核 - 表格数据
            datas: [],
            // 待审核 - 表格分页器数据
            pagination: {
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
        async getTableDatasAPIFn() {
            this.pagination.loading = true;
            const searchData = { ...this.searchData };

            if (!searchData.organization_name) {
                delete searchData.billsType;
                delete searchData.organization_inner_sn;
            }
            delete searchData.organization_name;

            await invoiceAdvanceListAPI(
                {
                    ...searchData,
                    isCancel: this.refName === systemAdvancePaymentTab[0].value ? 0 : 1 // 是否作废 默认0，0-未作废 1-作废
                },
                this.pagination.current,
                this.pagination.pageSize
            )
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res,
                            refDom = this.$refs['paymentTableListRef'];

                        this.datas.push(...rows);
                        this.pagination.total = total;

                        refDom.loadData(this.datas);
                        if (this.datas && this.datas.length) {
                            const row = this.datas[0];
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
                    this.pagination.loading = false;
                });
        },
        async invoiceAdvanceDelAPIFn(selectedIds) {
            this.pagination.loading = true;
            await invoiceAdvanceDelAPI(selectedIds)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.initTableData();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.pagination.loading = false;
                });
        },
        async invoiceAdvanceSubmitAPIFn(selectedIds) {
            this.pagination.loading = true;
            await invoiceAdvanceSubmitAPI(selectedIds)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.initTableData();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.pagination.loading = false;
                });
        },
        initTable() {
            this.datas = [];
            this.pagination = {
                loading: false,
                total: 0,
                current: 1,
                pageSize: this.isRefresh ? this.pagination.pageSize : 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            };
        },
        initTableData() {
            this.initTable();
            this.getTableDatasAPIFn();
        },
        changePaginationFn({ currentPage, pageSize }) {
            this.datas = [];
            this.pagination.current = currentPage;
            this.pagination.pageSize = pageSize;
            this.pagination.loading = true;
            this.getTableDatasAPIFn();
        },
        cellClickFn({ row }) {
            this.$emit('getRowDataFn', row);
        },
        cellDblclickFn({ row }) {
            this.$emit('lookDetailFn', row);
        },
        selectChangeEvent({ records }) {
            this.$refs['paymentTableListRef'].setCurrentRow(records);
            this.$emit('getRowDataFn', records);
        },
        /**
         * @todo 可以考虑单独提出操作过滤逻辑
         */
        groupBtnFn(action) {
            const selectedIds = [];
            const selectedRows = this.$refs['paymentTableListRef'].getCheckboxRecords(true);
            const rowsCount = selectedRows.length;
            // 删除单据
            let deleteAble = true;
            // 审核
            let auditAble = true;

            // 未勾选数据
            if (rowsCount <= 0) {
                this.$message.warning('请至少勾选一张单据再进行此操作');
                return;
            }

            for (let i = 0; i < rowsCount; i += 1) {
                switch (selectedRows[i].payment_status) {
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
                        this.invoiceAdvanceDelAPIFn(selectedIds);
                    }
                });
            } else if (action === 'audit') {
                if (!auditAble) {
                    this.$message.warning('草稿状态的单据才可进行此操作');
                    return;
                }
                this.invoiceAdvanceSubmitAPIFn(selectedIds);
            }
        }
    },
    watch: {
        isRefresh(newVal) {
            if (newVal) {
                this.initTableData();
            }
        },
        tableDetailCallbackData: {
            handler(newVal) {
                if (newVal) {
                    const datas = this.datas,
                        refDom = this.$refs['paymentTableListRef'];

                    if (this.btnName === 'add') {
                        delete newVal.details;

                        datas.push(newVal);
                        this.pagination.total += 1;
                        refDom.reloadData(datas);
                        refDom.setCurrentRow(datas[datas.length - 1]);
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
                                break;
                            }
                        }
                    }
                }
            },
            deep: true
        }
    },
    mounted() {
        this.initTableData();
    },
    filters: filterFormat.filters
};
</script>

<style lang="less" scoped></style>
