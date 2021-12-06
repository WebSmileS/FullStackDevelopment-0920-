<template>
    <div>
        <vxe-table
            style="margin-top: 12px"
            border
            show-overflow
            show-header-overflow
            resizable
            auto-resize
            ref="table"
            height="400"
            size="small"
            align="center"
            :data="products"
            :loading="loading"
            :checkbox-config="{ checkMethod: detailListCheckMethod }"
            @cell-click="onDetailCellClick"
        >
            <vxe-table-column v-bind="tableColumnConfig.select" type="checkbox" fixed="left"></vxe-table-column>
            <template v-for="column in columns">
                <vxe-table-column v-bind="column" :key="column.field"></vxe-table-column>
            </template>
            <vxe-table-column v-bind="tableColumnConfig.status" fixed="right">
                <template v-slot:default="{ row }">
                    <span v-if="row.status === 'AWAITING'" class="orange">待入库</span>
                    <span v-else class="green">已入库</span>
                </template>
            </vxe-table-column>
        </vxe-table>
    </div>
</template>

<script>
import Big from 'big.js';
import tableColumnConfig from '@/config/tableColumnConfig';
import { purchaseGrnTransferRecordDetailAPI } from '@/service/pageAjax';
import columns from './purchaseGrnTransferDetailColumns';

export default {
    name: 'ProductSelecterTransferOrder',
    props: {
        order: {
            type: Object
        }
    },
    data() {
        return {
            loading: false,
            products: []
        };
    },
    computed: {
        tableColumnConfig() {
            return tableColumnConfig;
        },
        columns() {
            return columns;
        }
    },
    watch: {
        order(newValue) {
            if (newValue) {
                this.loadDetail(this.order);
            }
        }
    },
    mounted() {
        if (this.order) {
            this.loadDetail(this.order);
        }
    },
    methods: {
        onDetailCellClick({ row }) {
            this.$refs.table.toggleCheckboxRow(row);
        },
        detailListCheckMethod({ row }) {
            if (row.status === 'AWAITING') {
                return true;
            }
            return false;
        },
        handleSubmit() {
            const details = this.$refs.table.getCheckboxRecords();
            if (!details.length) {
                this.$message.warning('至少选中一条单据明细');
                return;
            }
            this.$emit('submit', {
                transferOrderProducts: details.map((product) => {
                    let newProduct = { ...product };
                    delete newProduct._XID;
                    return newProduct;
                })
            });
        },
        async loadDetail(order) {
            try {
                this.loading = true;
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
                this.products = list.map((product) => {
                    return {
                        ...product,
                        money: new Big(product.unit_price).times(product.quantity).toNumber(),
                        totalSalesPrice: new Big(product.salesPrice).times(product.quantity).toNumber()
                    };
                });
                this.$nextTick(() => {
                    this.products.forEach((product) => {
                        if (product.status === 'AWAITING') {
                            this.$refs.table.setCheckboxRow(product, true);
                        }
                    });
                });
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>
