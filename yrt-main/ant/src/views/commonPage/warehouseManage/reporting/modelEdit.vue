<template>
    <base-modal
        dragKey="arrival_order_editor_md"
        fullscreenUseable
        :destroyOnClose="true"
        :title="`${isReadonly ? '编辑' : '新增'}报损单`"
        :width="1246"
        :visible="visible"
        @close="handleClose"
    >
        <template v-slot:content="{ fullscreen }">
            <!-- 获取产品 -->
            <div @dblclick="handleSelectProducts" style="margin-top: 20px">
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
                    :height="fullscreen ? fullscreenTableHeight : 300"
                    :data="dataEditingDetails"
                    :mouse-config="{ selected: true }"
                    :checkbox-config="{ range: true }"
                    :keyboard-config="{
                        isArrow: true,
                        isDel: true,
                        isEnter: true,
                        isTab: true,
                        isEdit: true
                    }"
                    :edit-config="{ trigger: 'click', mode: 'cell', activeMethod: handleCheckCellEditable }"
                    :span-method="rowSpanMethod"
                    :footer-method="() => [[]]"
                    @cell-dblclick="({ $event }) => $event.stopPropagation()"
                    @edit-actived="onEditActived"
                >
                    <template slot="empty">
                        <div>双击空白区域添加产品</div>
                    </template>
                    <vxe-table-column
                        v-for="column in productColumns"
                        v-bind="column"
                        :key="column.field"
                    ></vxe-table-column>
                    <vxe-table-column
                        field="reportNumber"
                        title="报损数量"
                        :width="100"
                        :edit-render="{ name: '$input', props: { type: 'float' } }"
                    ></vxe-table-column>
                    <vxe-table-column v-if="!isReadonly" v-bind="tableColumnConfig.operation">
                        <template v-slot:default="{ rowIndex }">
                            <table-row-operator del :payload="rowIndex" @del="handleRemoveProduct"></table-row-operator>
                        </template>
                        <template slot="footer">
                            <div style="overflow: hidden">
                                <a-button size="small" type="primary" @click="handleSelectProducts">添加产品</a-button>
                            </div>
                        </template>
                    </vxe-table-column>
                </vxe-table>

                <div class="editor-form-wrapper" style="margin-top: 20px">
                    <!-- <div class="editor-form-item">
                        <span class="label"><span v-if="!isReadonly" class="red">*</span>供货单位：</span>
                        <span v-if="isReadonly">{{ dataEditing.organizationName }}</span>
                        <base-input-with-clear-button
                            v-else
                            size="small"
                            :value="dataEditing.organizationName"
                            @click="organizationSelecter = true"
                            @clear="onOrganizationClear"
                        ></base-input-with-clear-button>
                        <organization-selecter
                            title="选择厂商"
                            destroyOnClose
                            :visible="organizationSelecter"
                            organizationType="ven"
                            @close="organizationSelecter = false"
                            @submit="onSearchFormOrganizationSelected"
                        ></organization-selecter>
                    </div>
                    <div class="editor-form-item">
                        <span class="label"><span v-if="!isReadonly" class="red">*</span>仓库：</span>
                        <span v-if="isReadonly">{{ dataEditing.warehouseName }}</span>
                        <a-select
                            v-else
                            allowClear
                            labelInValue
                            :options="warehouseList"
                            :value="warehouseSelecterValue"
                            @change="onWarehouseChange"
                        ></a-select>
                    </div> -->
                    <div class="editor-form-item" style="max-width: 200px">
                        <span class="label">操作员：</span>
                        <span>{{ dataEditing.operationName || '--' }}</span>
                    </div>
                    <div class="editor-form-item">
                        <span class="label">备注：</span>
                        <span v-if="isReadonly">{{ dataEditing.remarks }}</span>
                        <a-input v-else class="form-input" size="small" v-model="dataEditing.remarks" />
                    </div>
                </div>
                <product-selecter-modal
                    :visible="produceSelectModelVisible"
                    :tabs="productSelecterTabs"
                    @close="produceSelectModelVisible = false"
                >
                    <product-selecter-inventory slot="useable" @submit="onProductAdd"></product-selecter-inventory>
                    <!-- <product-selecter-warehouse
                        slot="useable"
                        autoLoadMounted
                        defaultIsContractProduct
                        :billsType="dataEditing.billsType"
                        :organizationInnerSn="dataEditing.organizationInnerSn"
                        :warehouseInnerSn="dataEditing.warehouseInnerSn"
                        @submit="onProductAdd"
                    ></product-selecter-warehouse> -->
                </product-selecter-modal>
            </div>
        </template>
        <template slot="footer"> </template>
    </base-modal>
</template>
<script>
import BaseModal from '@/components/BaseModal';
// import BaseInputWithClearButton from '@/components/BaseInputWithClearButton';
// import OrganizationSelecter from '@/components/OrganizationSelecter';
import ProductSelecterModal from '@/components/ProductSelecterModal';
import ProductSelecterInventory from './ProductSelecterInventory';
import TableRowOperator from '@/components/TableRowOperator';
// import ProductSelecterWarehouse from '@/components/ProductSelecterWarehouse';
import { orderTypeMap } from '@/config/constants';

import { productColumns } from './productColumns';

import tableColumnConfig from '@/config/tableColumnConfig';
import { connect } from 'echarts/core';

const modalsKeyMap = Object.freeze({
    ORGANIZATION_SELECTER: 'o',
    PRODUCT_SELECTER: 'p',
    CANCEL_REASON: 'c',
    BARCODE_PRODUCT_FILTER: 'b'
});
export default {
    components: {
        BaseModal,
        // BaseInputWithClearButton,
        // OrganizationSelecter,
        ProductSelecterModal,
        ProductSelecterInventory,
        // ProductSelecterWarehouse,
        TableRowOperator
    },
    props: {
        // 弹框是否显示
        visible: {
            type: Boolean,
            default: () => false
        },

        // 是否可读
        isReadonly: {
            type: Boolean,
            default: () => false
        }
    },
    data() {
        return {
            tableColumnConfig,
            modalsKeyMap,
            productColumns: Object.freeze(productColumns),
            dataEditing: {
                organizationName: '',
                organizationInnerSn: 2,
                billsType: 'HH',
                warehouseName: undefined,
                warehouseInnerSn: undefined,
                operationName: null,
                remarks: '',
                productBills: ''
            },
            dataEditingDetails: [],

            organizationSelecter: false, //是否显示选择厂商弹框
            produceSelectModelVisible: false //选择产品弹框
        };
    },
    computed: {
        // 仓库选择
        // warehouseList() {
        //     return this.$store.state.warehouseList.map((item) => ({
        //         value: item.value,
        //         key: item.value,
        //         label: item.name
        //     }));
        // },

        // 已选仓库的a-select的value
        // warehouseSelecterValue() {
        //     if (!this.dataEditing.warehouseInnerSn) {
        //         return undefined;
        //     }
        //     return {
        //         key: String(this.dataEditing.warehouseInnerSn),
        //         label: this.dataEditing.warehouseName
        //     };
        // },

        productSelecterTabs() {
            return [{ title: '可用产品', key: 'useable' }];
        }
    },
    methods: {
        // 关闭弹框  关闭弹框时清除弹框的内容
        handleClose() {
            this.dataEditing = {
                organizationName: '',
                organizationInnerSn: undefined,
                billsType: undefined,
                warehouseName: undefined,
                warehouseInnerSn: undefined,
                operationName: '',
                remarks: '',
                productBills: ''
            };
            this.dataEditingDetails = [];
            this.$emit('close');
        },

        // 清除供货单位
        // onOrganizationClear() {
        //     this.dataEditing.organizationName = undefined;
        //     this.dataEditing.organizationInnerSn = undefined;
        //     this.dataEditing.billsType = undefined;
        //     // 更改了供货单位，移除相关的产品，平台产品则会保留
        //     if (this.dataEditingDetails.length > 0) {
        //         let removedCount = 0;
        //         const newDetails = this.dataEditingDetails.filter((product) => {
        //             if (product.voucher_type_c === orderTypeMap.PO_RECORDS.key) {
        //                 removedCount += 1;
        //                 return false;
        //             }
        //             return true;
        //         });
        //         if (removedCount > 0) {
        //             this.dataEditingDetails = newDetails;
        //             this.$message.warning(`由于更改供货单位，相关产品（${removedCount}个）已移除`);
        //         }
        //     }
        // },

        // 仓库下拉选择
        // onWarehouseChange(value = {}) {
        //     if (this.dataEditing.warehouseName === value.label && this.dataEditing.warehouseInnerSn === value.key) {
        //         return;
        //     }
        //     let removedCount = 0;
        //     this.dataEditing.warehouseName = value.label;
        //     this.dataEditing.warehouseInnerSn = value.key;
        //     // 过滤与当前仓库不符合的单据产品
        //     const newDetails = this.dataEditingDetails.filter((product) => {
        //         if (
        //             product.voucher_type_c === orderTypeMap.PO_RECORDS.key &&
        //             product.warehouse_inner_sn !== this.dataEditing.warehouseInnerSn
        //         ) {
        //             removedCount += 1;
        //             return false;
        //         }
        //         return true;
        //     });
        //     if (removedCount) {
        //         this.dataEditingDetails = newDetails;
        //         this.$message.warning(`由于更改仓库，相关产品（${removedCount}个）已移除`);
        //     }
        // },

        // 选择厂商提交
        // onSearchFormOrganizationSelected(data) {
        //     this.dataEditing.organizationName = data[0].organizationName;
        //     this.dataEditing.organizationInnerSn = data[0].organizationInnerSn;
        //     this.organizationSelecter = false;
        // },

        // 调取产品
        handleSelectProducts() {
            if (this.isRewarehouseListadonly) {
                return;
            }
            this.produceSelectModelVisible = true;
            // this.handleOpenModals(modalsKeyMap.PRODUCT_SELECTER);
        },

        /**
         * 产品选择返回值时
         * @param {object} data 产品数据
         */
        onProductAdd(products) {
            console.dir(products);
            if (products.length == 0) {
                return;
            } else {
                this.produceSelectModelVisible = false;
                this.dataEditingDetails = this.dataEditingDetails.concat(products);
            }
        },

        onBarcodeInputerSubmit(products) {
            console.dir(products);
            // if (!products.length) {
            //     return;
            // }
            // if (products.length === 1) {
            //     const insertIndex = Math.max(0, this.dataEditingDetails.length - 1); // 最后一行为inputRow
            //     this.onProductAdd({ arrivalOrderProducts: products });
            //     this.$nextTick(() => {
            //         this.$refs.xTable.scrollTo(124);
            //         this.$refs.xTable.setActiveCell(this.dataEditingDetails[insertIndex], 'barcode');
            //     });
            //     return;
            // }
            // this.barcodeProductsToFilter = products;
            // this.handleOpenModals(modalsKeyMap.BARCODE_PRODUCT_FILTER);
        },

        /**
         * 移除表格内的一个产品
         * @param {number} index
         */
        handleRemoveProduct(index) {
            this.dataEditingDetails.splice(index, 1);
        },

        // handleSelectProducts() {
        // if (this.isApprovalOrder || this.isReadonly) {
        //     return;
        // }
        // this.handleOpenModals(modalsKeyMap.PRODUCT_SELECTER);
        // }

        /**
         * 检测单元格是否可编辑
         * @param {object} data
         * @param {object} data.row 行数据
         * @param {object} data.column 列信息
         */
        handleCheckCellEditable({ row, column }) {
            // if (this.isApprovalOrder || this.isReadonly) {
            //     return false;
            // }
            // if (column.property === 'unit_inner_sn' || column.property === 'order_quantity') {
            //     if (row.voucher_type_c === orderTypeMap.PO_RECORDS.key) {
            //         return false;
            //     }
            // }
            return true;
        },

        rowSpanMethod({ row, column }) {
            if (row.rowMark === 'inputerRow') {
                if (column.property === 'index') {
                    return { rowspan: 1, colspan: 9 };
                } else {
                    return { rowspan: 1, colspan: 0 };
                }
            }
            return { rowspan: 1, colspan: 1 };
        },

        /**
         * 表格单元格编辑激活时
         */
        async onEditActived({ row, column }) {
            switch (column.property) {
                case 'unit_inner_sn':
                    // 若单位选项未加载则加载单位
                    if (!row.unitLoaded) {
                        try {
                            row.unitLoading = true;
                            const unitOptions = await this.loadProductUnitOptions(row);
                            row.unitLoaded = true;
                            row.unitSelecterOptions = unitOptions;
                        } finally {
                            row.unitLoading = false;
                        }
                    }
                    break;
                default:
            }
        }
    }
};
</script>
<style lang="less" scoped>
.editor-form-wrapper {
    margin-bottom: 24px;
    display: flex;
}
.editor-form-item {
    display: flex;
    flex: 1;
    .label {
        margin-right: 12px;
        text-align: right;
        min-width: 80px;
    }
}
</style>
