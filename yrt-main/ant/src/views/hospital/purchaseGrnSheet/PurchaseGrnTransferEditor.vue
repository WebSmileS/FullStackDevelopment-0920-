<template>
    <base-modal
        dragKey="purchase_grn_transfer_editor_md"
        title="调单入库"
        destroyOnClose
        :width="1100"
        :visible="visible"
        @close="handleClose"
    >
        <template v-slot:content>
            <editor-form-header style="margin-top: 24px">
                <editor-form-header-item>
                    <label class="label">单据编号：</label>
                    <a-input size="small" v-model="searchValues.sn"></a-input>
                </editor-form-header-item>
                <editor-form-header-item>
                    <label class="label">单位名称：</label>
                    <a-input size="small" v-model="searchValues.organizationName"></a-input>
                </editor-form-header-item>
                <editor-form-header-item>
                    <label class="label">产品名称：</label>
                    <a-input size="small" v-model="searchValues.productName"></a-input>
                </editor-form-header-item>
                <editor-form-header-item>
                    <a-button size="small" type="primary" @click="handleSearch">搜索</a-button>
                </editor-form-header-item>
            </editor-form-header>
            <div style="margin-top: 18px">可调单单据：</div>
            <vxe-table
                style="margin-top: 12px"
                border
                show-overflow
                show-header-overflow
                resizable
                auto-resize
                ref="orderList"
                height="200"
                size="small"
                align="center"
                :data="orderList"
                :loading="orderListLoading"
                @cell-click="onOrderCellClick"
            >
                <vxe-table-column v-bind="tableColumnConfig.select" type="radio"></vxe-table-column>
                <template v-for="column in orderColumns">
                    <vxe-table-column v-bind="column" :key="column.field"></vxe-table-column>
                </template>
            </vxe-table>
            <vxe-pager
                :loading="orderListLoading"
                :current-page="orderListCurrentPage"
                :page-size="orderListPageSize"
                :total="orderListTotal"
                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                @page-change="onOrderListPaginationChange"
            >
            </vxe-pager>
            <div style="margin-top: 12px">单据明细：</div>
            <vxe-table
                style="margin-top: 12px"
                border
                show-overflow
                show-header-overflow
                resizable
                auto-resize
                ref="detailList"
                height="200"
                size="small"
                align="center"
                :data="detailList"
                :loading="detailListLoading"
                :checkbox-config="{ checkMethod: detailListCheckMethod }"
                @cell-click="onDetailCellClick"
            >
                <vxe-table-column v-bind="tableColumnConfig.select" type="checkbox" fixed="left"></vxe-table-column>
                <template v-for="column in detailColumns">
                    <vxe-table-column v-bind="column" :key="column.field"></vxe-table-column>
                </template>
                <vxe-table-column v-bind="tableColumnConfig.status" fixed="right">
                    <template v-slot:default="{ row }">
                        <span v-if="row.status === 'AWAITING'" class="orange">待入库</span>
                        <span v-else class="green">已入库</span>
                    </template>
                </vxe-table-column>
            </vxe-table>
        </template>
        <template slot="footer">
            <a-button type="default" @click="handleClose">取消</a-button>
            <a-button type="primary" @click="onMakeButtonClick">生成入库单</a-button>
        </template>
    </base-modal>
</template>

<script>
import Big from 'big.js';
import BaseModal from '@/components/BaseModal';
import EditorFormHeader from '@/components/EditorFormHeader';
import EditorFormHeaderItem from '@/components/EditorFormHeaderItem';
import tableColumnConfig from '@/config/tableColumnConfig';
import orderColumns from './purchaseGrnTransferOrderColumns';
import detailColumns from './purchaseGrnTransferDetailColumns';
import { purchaseGrnTransferRecordDetailAPI, purchaseGrnTransferRecordListAPI } from '@/service/pageAjax';

export default {
    name: 'PurchaseGrnTransferEditor',
    components: {
        BaseModal,
        EditorFormHeader,
        EditorFormHeaderItem
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            searchValues: {
                sn: undefined,
                organizationName: undefined,
                productName: undefined
            },
            orderList: [],
            orderListLoading: false,
            orderListCurrentPage: 1,
            orderListTotal: 0,
            orderListPageSize: 10,
            detailList: [],
            detailListLoading: false
        };
    },
    computed: {
        tableColumnConfig() {
            return tableColumnConfig;
        },
        orderColumns() {
            return orderColumns;
        },
        detailColumns() {
            return detailColumns;
        },
        /**
         * 当前登录的机构名
         */
        organizationNameLogined() {
            const info = JSON.parse(this.$cookie.get('EmployeeInfo'));
            if (info && info.OrgName) {
                return info.OrgName;
            }
            return undefined;
        }
    },
    watch: {
        visible(newValue) {
            if (newValue) {
                this.handleInit();
                this.handleSearch();
            }
        }
    },
    methods: {
        detailListCheckMethod({ row }) {
            if (row.status === 'AWAITING') {
                return true;
            }
            return false;
        },
        onMakeButtonClick() {
            let order = { ...this.$refs.orderList.getRadioRecord() };
            const details = this.$refs.detailList.getCheckboxRecords();
            if (!details.length) {
                this.$message.warning('至少选中一条单据明细才可以生成入库单');
                return;
            }
            order.details = details.map((product) => {
                let newProduct = { ...product };
                delete newProduct._XID;
                return newProduct;
            });
            delete order._XID;
            this.$emit('submit', order);
        },
        onOrderListPaginationChange({ currentPage, pageSize }) {
            this.orderListCurrentPage = currentPage;
            this.orderListPageSize = pageSize;
            this.handleSearch();
        },
        onOrderCellClick({ row }) {
            this.$refs.orderList.setRadioRow(row);
            this.loadDetail(row);
        },
        onDetailCellClick({ row, column }) {
            if (column.property === tableColumnConfig.select.field) {
                return;
            }
            this.$refs.detailList.toggleCheckboxRow(row);
        },
        handleInit() {
            this.orderList = [];
            this.orderListLoading = false;
            this.orderListCurrentPage = 1;
            this.orderListTotal = 0;
            this.detailList = [];
            this.detailListLoading = false;
        },
        handleClose() {
            this.$emit('close');
        },
        handleSearch() {
            this.loadOrderList();
        },
        async loadOrderList() {
            try {
                this.orderListLoading = true;
                const { code, rows, total, msg } = await purchaseGrnTransferRecordListAPI({
                    params: {
                        pageNum: this.orderListCurrentPage,
                        pageSize: this.orderListPageSize,
                        voucherType: 'ODO',
                        sn: this.searchValues.sn,
                        organizationName: this.searchValues.organizationName,
                        key: this.searchValues.productName
                    }
                });
                if (code !== 0) {
                    this.$message.error(msg);
                    return;
                }
                this.orderList = rows;
                this.orderListTotal = total;
                this.detailList = [];
                this.detailListLoading = false;
            } finally {
                this.orderListLoading = false;
            }
        },
        async loadDetail(order) {
            try {
                this.detailListLoading = true;
                const { code, list, msg } = await purchaseGrnTransferRecordDetailAPI({
                    params: {
                        pageNum: 1,
                        pageSize: 100,
                        voucherType: 'ODO',
                        vId: order.voucher_inner_sn_c,
                        billsType: order.billsType,
                        organization_inner_sn: order.organization_inner_sn
                    }
                });
                if (code !== 0) {
                    this.$message(msg);
                    return;
                }
                this.detailList = list.map((product) => {
                    return {
                        ...product,
                        money: new Big(product.unit_price).times(product.quantity).toNumber(),
                        totalSalesPrice: new Big(product.salesPrice).times(product.quantity).toNumber()
                    };
                });
                this.$nextTick(() => {
                    this.detailList.forEach((product) => {
                        if (product.status === 'AWAITING') {
                            this.$refs.detailList.setCheckboxRow(product, true);
                        }
                    });
                });
            } finally {
                this.detailListLoading = false;
            }
        }
    }
};
</script>
