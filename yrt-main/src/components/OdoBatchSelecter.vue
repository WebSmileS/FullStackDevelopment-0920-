<template>
    <base-modal
        v-bind="$attrs"
        v-on="$listeners"
        dragKey="odo_batch_selecter"
        title="出库批号选择"
        :visible="visible"
        :width="600"
        :footer="null"
    >
        <template slot="content">
            <a-spin :spinning="loading">
                <div style="padding-top: 24px">
                    <vxe-table
                        ref="batchXTable"
                        :keyboard-config="{ isArrow: true }"
                        size="small"
                        height="420"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        align="center"
                        :data="list"
                        @cell-click="onBatchClick"
                    >
                        <vxe-table-column
                            v-for="column in columns"
                            v-bind="column"
                            :key="column.field"
                        ></vxe-table-column>
                    </vxe-table>
                </div>
            </a-spin>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal.vue';
import { odoBatchListAPI } from '@/service/pageAjax';
import { grnAndOdoOrderType } from '@/config/constants';

const columns = [
    {
        title: '批号',
        field: 'batch_number',
        width: '20%'
    },
    {
        title: '生产日期',
        field: 'produce_date',
        width: '16%'
    },
    {
        title: '失效日期',
        field: 'overdue_date',
        width: '16%'
    },
    {
        title: '可用库存（最小单位）',
        field: 'quantity',
        width: '24%'
    },
    {
        title: '成本单价（最小单位）',
        field: 'average_price',
        width: '24%'
    }
];

export default {
    name: 'OdoBatchSelecter',
    components: {
        BaseModal
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        /**
         * 出库单类型
         */
        orderType: {
            type: String,
            validator: (type) =>
                !type ||
                [
                    grnAndOdoOrderType.SALEODO.value,
                    grnAndOdoOrderType.SALERETURN.value,
                    grnAndOdoOrderType.GIVEODO.value,
                    grnAndOdoOrderType.ALLOTODO.value,
                    grnAndOdoOrderType.GRANTODO.value
                ].indexOf(type) >= 0
        },
        /**
         * 出库单相关信息,
         * 包含odo_inner_sn, warehouse_inner_sn, billsType
         */
        orderInfo: {
            type: Object
        },
        /**
         * 当前单据已选的产品列表
         */
        orderProducts: {
            type: Array
        },
        /**
         * 选择批号的目标产品
         */
        targetProduct: {
            type: Object
        }
    },
    data() {
        return {
            loading: false,
            list: []
        };
    },
    computed: {
        columns() {
            return columns;
        }
    },
    watch: {
        visible(newValue) {
            if (newValue) {
                this.list = [];
                this.loadBatchList();
            }
        }
    },
    methods: {
        onBatchClick({ rowIndex }) {
            this.$emit('select', this.list[rowIndex], this.targetProduct);
            this.$emit('close');
        },
        async loadBatchList() {
            const { orderType, orderInfo, orderProducts, targetProduct } = this;
            this.loading = true;
            try {
                const { code, list, msg } = await odoBatchListAPI({
                    org_id: this.$cookie.get('userbelong'),
                    warehouse_inner_sn: orderInfo.warehouse_inner_sn,
                    product_vendor_inner_sn: targetProduct.vendor_inner_sn || targetProduct.product_vendor_inner_sn,
                    product_inner_sn: targetProduct.product_inner_sn,
                    specification_inner_sn: targetProduct.specification_inner_sn,
                    product_type: targetProduct.product_type,
                    billsType: orderInfo.billsType,
                    odoType: orderType,
                    billId: orderInfo.odo_inner_sn || null,
                    selectedBatchNumberDtos: orderProducts
                        .filter((product) => product._XID !== targetProduct._XID)
                        .map((product) => {
                            const result = {
                                productId: product.product_inner_sn,
                                productVendorId: product.vendor_inner_sn || product.product_vendor_inner_sn,
                                specificationId: product.specification_inner_sn,
                                unitId: product.unit_inner_sn,
                                batchNumber: product.batch_number,
                                produceDate: product.produce_date,
                                overdueDate: product.overdue_date,
                                buyingPrice: product.buying_price === undefined ? null : product.buying_price,
                                quantity: product.quantity === undefined ? null : product.quantity
                            };
                            if (product.unit_inner_sn) {
                                if (
                                    typeof product.unit_inner_sn === 'object' &&
                                    product.unit_inner_sn.constructor === Array
                                ) {
                                    result.unitId = product.unit_inner_sn[product.unit_inner_sn.length - 1];
                                }
                            }
                            if (product.produce_date && typeof product.produce_date === 'object') {
                                result.produceDate = product.produce_date.format('YYYY-MM-DD');
                            }
                            if (product.overdue_date && typeof product.overdue_date === 'object') {
                                result.overdueDate = product.overdue_date.format('YYYY-MM-DD');
                            }
                            return result;
                        })
                });
                this.list = list;
                if (code !== 0) {
                    this.$message.error(msg);
                }
            } catch (err) {
                this.$message.error('批号查询失败');
                console.error(err);
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>

<style lang="less">
.odo-batch-selecter {
    .ant-modal-body {
        padding-left: 0;
        padding-right: 0;
    }
}
</style>
