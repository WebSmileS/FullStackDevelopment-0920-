<template>
    <div style="height: 100%">
        <div v-if="!isFinance" class="detail-top-bar">
            <a-button v-if="!makeButtonDisabled" size="small" type="primary" @click="onMakeButtonClick"
                >生成出库单</a-button
            >
            <a-tooltip
                v-else
                class="button-tooltip"
                placement="top"
                title="单据已审核，并且选中的产品数量有剩余时才能进行此操作"
            >
                <a-button size="small" type="primary" disabled>生成出库单</a-button>
            </a-tooltip>
            <a-button v-if="!cancelButtonDisabled" size="small" type="danger" @click="onCancelButtonClick"
                >回库</a-button
            >
            <a-tooltip
                v-else
                class="button-tooltip"
                placement="top"
                title="单据已审核，并且选中的产品数量有剩余时才能进行此操作"
            >
                <a-button size="small" type="danger" disabled>回库</a-button>
            </a-tooltip>
        </div>
        <div class="detail-table-wrapper" :class="{ 'full-height': isFinance }">
            <vxe-table
                ref="listTable"
                size="small"
                border
                resizable
                auto-resize
                highlight-current-row
                align="center"
                height="100%"
                show-overflow
                show-header-overflow
                showHeader
                :data="list"
                :loading="loading"
                :checkbox-config="{ checkMethod: detailListCheckMethod }"
                @checkbox-change="onTableSelect"
                @checkbox-all="onTableSelect"
            >
                <vxe-table-column
                    v-if="!isFinance"
                    v-bind="tableColumnConfig.select"
                    type="checkbox"
                ></vxe-table-column>
                <vxe-table-column v-for="column in columns" v-bind="column" :key="column.field"></vxe-table-column>
            </vxe-table>
        </div>
        <advance-sale-order-detail-editor
            :visible="countSelecterVisible"
            :orderData="countSelecterData"
            :submitType="countSelecterType"
            @close="countSelecterVisible = false"
            @finish="onCountSelecterFinish"
        ></advance-sale-order-detail-editor>
    </div>
</template>

<script>
import Big from 'big.js';
import tableColumnConfig from '@/config/tableColumnConfig';
import advanceSaleOrderDetailColumns from './advanceSaleOrderDetailColumns';
import { advanceSaleOrderDetailAPI } from '@/service/advanceSale';
import AdvanceSaleOrderDetailEditor from './AdvanceSaleOrderDetailEditor';
import { advanceSaleOrderAuditStatusMap, countSelecterTypeMap } from './type';

export default {
    name: 'AdvanceSaleOrderDetail',
    components: { AdvanceSaleOrderDetailEditor },
    /**
     * isFinance:
     * 是否为财务人员使用，财务人员使用时只有查看功能，
     * 并且列表内容为已审核单据
     */
    inject: ['isFinance'],
    props: {
        targetOrder: {
            type: Object,
            default: undefined
        }
    },
    data() {
        return {
            loading: false,
            list: [],
            selectedDetail: [],
            countSelecterVisible: false,
            countSelecterData: undefined,
            countSelecterType: undefined
        };
    },
    computed: {
        tableColumnConfig() {
            return tableColumnConfig;
        },
        columns() {
            return advanceSaleOrderDetailColumns;
        },
        makeButtonDisabled() {
            if (
                this.loading ||
                !this.targetOrder ||
                this.targetOrder.auditStatus !== advanceSaleOrderAuditStatusMap.APPROVED ||
                this.selectedDetail.length < 1
            ) {
                return true;
            }
            return false;
        },
        cancelButtonDisabled() {
            if (
                this.loading ||
                !this.targetOrder ||
                this.targetOrder.auditStatus !== advanceSaleOrderAuditStatusMap.APPROVED ||
                this.selectedDetail.length < 1
            ) {
                return true;
            }
            return false;
        }
    },
    watch: {
        targetOrder(newValue, oldValue) {
            if (!newValue) {
                if (oldValue) {
                    this.handleInit();
                }
                return;
            }
            this._debouncedDetailLoader(this.targetOrder.id);
        }
    },
    created() {
        this._debouncedDetailLoader = this.$XEUtils.debounce(this.loadOrderDetail, 100);
    },
    methods: {
        /**
         * 判断条目是否可选中
         */
        detailListCheckMethod({ row }) {
            if (new Big(row.soldQuantity || 0).plus(row.respondQuantity || 0).lt(row.quantity)) {
                return true;
            }
            return false;
        },

        onTableSelect({ records }) {
            this.selectedDetail = records;
        },

        onMakeButtonClick() {
            this.countSelecterVisible = true;
            this.countSelecterData = {
                ...this.targetOrder,
                details: this.selectedDetail.map((product) => ({ ...product }))
            };
            this.countSelecterType = countSelecterTypeMap.ODO;
        },

        onCancelButtonClick() {
            this.countSelecterVisible = true;
            this.countSelecterData = {
                ...this.targetOrder,
                details: this.selectedDetail.map((product) => ({ ...product }))
            };
            this.countSelecterType = countSelecterTypeMap.RETURN;
        },

        onCountSelecterClose() {
            this.countSelecterVisible = false;
            this.countSelecterData = undefined;
        },

        async onCountSelecterFinish() {
            const orderId = this.targetOrder.id;
            this.onCountSelecterClose();
            this.handleInit();
            this.$refs.listTable.clearSelected();
            await this.handleReload();
            // 若产品均已售完或回库，通知父组件，父组件可根据此事件更新单据状态
            if (this.isAllDone()) {
                this.$emit('orderFinish', orderId);
            }
        },

        handleInit() {
            this.loading = false;
            this.list = [];
            this.selectedDetail = [];
        },

        async handleReload() {
            await this.loadOrderDetail(this.targetOrder.id);
        },

        /**
         * 检查当前列表内产品，判断是否均已售完或回库
         */
        isAllDone() {
            if (
                this.list.some((product) => {
                    if (new Big(product.respondQuantity).plus(product.soldQuantity).gte(product.quantity)) {
                        return false;
                    }
                    return true;
                })
            ) {
                return false;
            }
            return true;
        },

        /**
         * 加载预售单明细
         * @param {number} id 预售单id
         */
        async loadOrderDetail(id) {
            try {
                this.loading = true;
                const { code, list, msg } = await advanceSaleOrderDetailAPI(id);
                if (code !== 0) {
                    throw new Error(msg || '单据详情获取失败');
                }
                this.list = list;
                this.selectedDetail = [];
                // 默认选中所有细项
                this.$nextTick(() => {
                    this.list.forEach((product) => {
                        if (this.detailListCheckMethod({ row: product })) {
                            this.$refs.listTable.setCheckboxRow(product, true);
                            this.selectedDetail.push({ ...product });
                        }
                    });
                });
            } catch (err) {
                this.$message.error(err.message);
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>

<style lang="less" scoped>
@topBarHeight: 24px;
.detail-top-bar {
    height: @topBarHeight;
    line-height: @topBarHeight;
    text-align: left;
    /deep/ .ant-btn,
    /deep/ .button-tooltip {
        margin-left: 12px;
        &:first-child {
            margin-left: 0;
        }
    }
}
.detail-table-wrapper {
    height: calc(100% - @topBarHeight - 12px);
    margin-top: 12px;
}
.detail-table-wrapper.full-height {
    height: 100%;
}
</style>
