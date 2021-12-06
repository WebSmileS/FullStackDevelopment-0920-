<template>
    <base-modal
        fullscreenUseable
        dragKey="advance_sale_count_selecter_md"
        :destroyOnClose="true"
        :title="title"
        :width="1100"
        :visible="visible"
        @close="$emit('close')"
    >
        <template v-slot:content="{ fullscreen }">
            <div class="detail-table-wrapper" style="padding-top: 24px">
                <vxe-table
                    border
                    show-overflow
                    show-header-overflow
                    show-footer
                    resizable
                    auto-resize
                    ref="xTable"
                    size="small"
                    align="center"
                    :height="fullscreen ? fullscreenTableHeight : 400"
                    :data="list"
                    :mouse-config="{ selected: true }"
                    :checkbox-config="{ range: true }"
                    :keyboard-config="{
                        isArrow: true,
                        isDel: true,
                        isEnter: true,
                        isTab: true,
                        isEdit: true
                    }"
                    :edit-config="{ trigger: 'click', mode: 'cell' }"
                    :edit-rules="{
                        editingQuantity: [{ min: 0.00001, type: 'number', message: '数量必须大于0' }],
                        unitPrice: [{ min: 0, type: 'number', message: '含税单价不能为负' }],
                        money: [{ min: 0, type: 'number', message: '含税金额不能为负' }]
                    }"
                    :footer-cell-class-name="tableFooterCellClassName"
                    :footer-method="submitType === 'ODO' ? tableFooterMethod : undefined"
                    @edit-closed="onEditClosed"
                >
                    <vxe-table-column v-bind="tableColumnConfig.seq" fixed="left"></vxe-table-column>
                    <vxe-table-column v-for="column in columns" v-bind="column" :key="column.field"></vxe-table-column>
                </vxe-table>
            </div>
        </template>
        <template slot="footer">
            <a-button type="default" @click="$emit('close')">取消</a-button>
            <a-button type="primary" @click="onSubmitButtonClick" :loading="loading">确认</a-button>
        </template>
    </base-modal>
</template>

<script>
import Big from 'big.js';
import BaseModal from '@/components/BaseModal';
import tableColumnConfig from '@/config/tableColumnConfig';
import advanceSaleOrderDetailEditorColumns from './advanceSaleOrderDetailEditorColumns';
import { advanceSaleOrderCancelAPI, advanceSaleOrderSoldAPI } from '@/service/advanceSale';
import { countSelecterTypeMap } from './type';

const quantityTextMap = {
    [countSelecterTypeMap.ODO]: '出库数量',
    [countSelecterTypeMap.RETURN]: '回库数量'
};

export default {
    name: 'AdvanceSaleOrderDetailEditor',
    components: {
        BaseModal
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        orderData: {
            type: Object,
            default: undefined
        },
        /**
         * 数据提交的方式，出库（ODO）或回库（RETURN)
         */
        submitType: {
            type: String,
            validator: (value) => value && countSelecterTypeMap[value]
        }
    },
    data() {
        return {
            loading: false,
            list: [],
            moneySummation: 0
        };
    },
    computed: {
        tableColumnConfig() {
            return tableColumnConfig;
        },
        columns() {
            const columns = advanceSaleOrderDetailEditorColumns.map((column) => {
                if (column.field === 'editingQuantity') {
                    return {
                        ...column,
                        title: quantityTextMap[this.submitType]
                    };
                }
                return column;
            });
            // 回库时不用设置税率、单价、含税金额
            if (this.submitType === countSelecterTypeMap.RETURN) {
                return columns.filter((column) => ['rate', 'unitPrice', 'money'].indexOf(column.field) < 0);
            }
            return columns;
        },
        title() {
            if (this.submitType === countSelecterTypeMap.ODO) {
                return '生成出库单';
            }
            if (this.submitType === countSelecterTypeMap.RETURN) {
                return '回库';
            }
            return '';
        },
        makeButtonDisabled() {
            if (this.loading || !this.orderData) {
                return true;
            }
            return false;
        },
        cancelButtonDisabled() {
            if (this.loading || !this.orderData) {
                return true;
            }
            return false;
        },
        fullscreenTableHeight() {
            return this.$store.state.clientHeight - 154;
        }
    },
    watch: {
        visible(newValue) {
            if (newValue) {
                this.list = this.orderData.details.map((product) => {
                    const useableQuantity = new Big(product.quantity || 0)
                        .minus(product.soldQuantity || 0)
                        .minus(product.respondQuantity || 0)
                        .toNumber();
                    const result = {
                        ...product,
                        useableQuantity,
                        editingQuantity: useableQuantity
                    };
                    if (this.submitType === countSelecterTypeMap.ODO) {
                        result.rate = 0;
                        result.unitPrice = 0;
                        result.money = 0;
                    }
                    return result;
                });
            }
        }
    },
    created() {
        this._debouncedDetailLoader = this.$XEUtils.debounce(this.loadOrderDetail, 100);
    },
    methods: {
        tableFooterMethod() {
            let result = [];
            result[0] = '合计';
            result[9] = this.moneySummation;
            return [result];
        },
        tableFooterCellClassName(payload) {
            if (payload.columnIndex === 0) {
                return 'total-bg';
            }
            return 'red';
        },
        onSubmitButtonClick() {
            if (!this.handleCheckData()) {
                return;
            }
            const details = this.list.map((product) => {
                const result = {
                    saleDetailId: product.id,
                    quantity: product.editingQuantity
                };
                if (this.submitType === countSelecterTypeMap.ODO) {
                    result.unitPrice = product.unitPrice;
                    result.rate = product.rate;
                }
                return result;
            });
            if (this.submitType === countSelecterTypeMap.ODO) {
                this.makeSaleOrder(this.orderData.id, details);
            }
            if (this.submitType === countSelecterTypeMap.RETURN) {
                this.cancelOrderDetail(this.orderData.id, details);
            }
        },

        onEditClosed({ column, row }) {
            switch (column.property) {
                case 'editingQuantity':
                    row.editingQuantity = new Big(row.editingQuantity || 0).round(4).toNumber();
                    if (this.submitType === countSelecterTypeMap.ODO) {
                        if (row.unitPrice) {
                            row.money = new Big(row.editingQuantity).times(row.unitPrice).round(4).toNumber();
                            break;
                        }
                        if (row.money) {
                            row.unitPrice = new Big(row.money).div(row.editingQuantity).round(4).toNumber();
                        }
                    }
                    break;
                case 'unitPrice':
                    row.unitPrice = new Big(row.unitPrice || 0).round(4).toNumber();
                    if (this.submitType === countSelecterTypeMap.ODO) {
                        if (row.editingQuantity) {
                            row.money = new Big(row.unitPrice).times(row.editingQuantity).round(4).toNumber();
                            break;
                        }
                        if (row.money) {
                            row.editingQuantity = new Big(row.money).div(row.unitPrice).round(4).toNumber();
                            break;
                        }
                    }
                    break;
                case 'money':
                    row.money = new Big(row.money || 0).round(4).toNumber();
                    if (this.submitType === countSelecterTypeMap.ODO) {
                        if (row.editingQuantity) {
                            row.unitPrice = new Big(row.money).div(row.editingQuantity).round(4).toNumber();
                            break;
                        }
                        if (row.unitPrice) {
                            row.editingQuantity = new Big(row.money).div(row.unitPrice).round(4).toNumber();
                            break;
                        }
                    }
                    break;
            }
            this.handleUpdateMoneySummation();
        },

        /**
         * 更新表尾合计金额
         */
        handleUpdateMoneySummation() {
            let summation = new Big(0);
            this.list.forEach((detail) => {
                if (detail.money) {
                    summation = summation.plus(detail.money);
                }
            });
            this.moneySummation = summation.round(4).toNumber();
            this.$refs.xTable.updateFooter();
        },

        handleCheckData() {
            const { list } = this;
            for (let i = 0, l = list.length; i < l; i += 1) {
                // 数量校验
                if (!list[i].editingQuantity || list[i].editingQuantity < 0) {
                    this.$message.warning(
                        `NO.${i + 1}${list[i].productName} ${quantityTextMap[this.submitType]}不能为零`
                    );
                    return false;
                }
                if (new Big(list[i].editingQuantity).gt(list[i].useableQuantity)) {
                    this.$message.warning(
                        `NO.${i + 1}${list[i].productName} ${quantityTextMap[this.submitType]}不能超过可用数量`
                    );
                    return false;
                }
                // 价格校验
                if (this.submitType === countSelecterTypeMap.ODO) {
                    if (isNaN(Number(list[i].unitPrice)) || Number(list[i].unitPrice) < 0) {
                        this.$message.warning(`NO.${i + 1}${list[i].productName} 含税单价必须大于等于0`);
                        return false;
                    }
                }
            }
            return true;
        },

        /**
         * 生成出库单
         * @param {number} orderId 预售单id
         * @param {object[]} detail 预售单明细
         * @param {string} detail[].saleDetailId
         * @param {number | string} detail[].quantity
         * @returns {Promise<{ code: Number, msg: String }>}
         */
        async makeSaleOrder(orderId, detail) {
            try {
                this.loading = true;
                const { code, msg } = await advanceSaleOrderSoldAPI(detail);
                if (code !== 0) {
                    throw new Error(msg || '出库单生成失败');
                }
                this.$message.success('生成出库单成功');
                this.$emit('finish');
            } catch (err) {
                this.$message.error(err.message);
            } finally {
                this.loading = false;
            }
        },

        /**
         * 预售单回库
         * @param {number} orderId 预售单id
         * @param {object[]} details 预售单明细
         * @param {string} details[].saleDetailId
         * @param {number | string} details[].quantity
         * @returns {Promise<{ code: Number, msg: String }>}
         */
        async cancelOrderDetail(orderId, details) {
            try {
                this.loading = true;
                const { code, msg } = await advanceSaleOrderCancelAPI(details);
                if (code !== 0) {
                    throw new Error(msg || '回库失败');
                }
                this.$message.success('回库成功');
                this.$emit('finish');
            } catch (err) {
                this.$message.error(err.message);
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>
