<template>
    <base-modal
        dragKey="inspection_order_editor_md"
        fullscreenUseable
        :destroyOnClose="true"
        :title="`${isAddOrder ? '新增' : ''}验货单`"
        :width="1100"
        :visible="visible"
        @close="handleClose"
    >
        <template v-slot:content="{ fullscreen }">
            <div style="position: relative">
                <span v-show="!isAddOrder" style="position: absolute; right: 0">单据编号：{{ dataEditing.sn }}</span>
                <h6 style="margin: 24px 0; text-align: center; font-size: 24px; font-weight: 500">
                    <span v-if="organizationNameLogined">{{ organizationNameLogined }}&nbsp;&nbsp;</span>
                    <span>验货单</span>
                </h6>
            </div>
            <div class="editor-form-wrapper">
                <div class="editor-form-item">
                    <span class="label"><span v-if="!isReadonly" class="red">*</span>供货单位：</span>
                    <span v-if="isReadonly">{{ dataEditing.organizationName }}</span>
                    <base-input-with-clear-button
                        v-else
                        size="small"
                        :value="dataEditing.organizationName"
                        @click="handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                        @clear="onOrganizationClear"
                    ></base-input-with-clear-button>
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
                </div>
                <div class="editor-form-item"></div>
            </div>
            <div @dblclick="handleSelectProducts">
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
                    :edit-rules="{
                        'arrival_quantity': [{ min: 0.000001, type: 'number', message: '收货数量必须大于0' }],
                        'qualified_quantity': [{ min: 0, type: 'number', message: '合格数量不能为负' }],
                        'unqualified_quantity': [{ min: 0, type: 'number', message: '不合格数量不能为负' }]
                    }"
                    :span-method="rowSpanMethod"
                    :footer-method="() => [[]]"
                    @cell-dblclick="({ $event }) => $event.stopPropagation()"
                    @edit-actived="onEditActived"
                    @barcodeInputFinish="
                        (barcode, params) =>
                            _mixinOnBarcodeInputFinish(barcode, params, loadBarcodePIInfo, [
                                ['batch_number', 'batch_number', '批号'],
                                ['produce_date', 'produce_date', '生产日期'],
                                ['overdue_date', 'overdue_date', '失效日期']
                            ])
                    "
                >
                    <template slot="empty">
                        <div>双击空白区域添加调单验货产品</div>
                    </template>
                    <vxe-table-column v-bind="tableColumnConfig.seq" field="index">
                        <template v-slot:default="{ row, rowIndex }">
                            <template v-if="row.rowMark === 'inputerRow'">
                                <barcode-inputer-inspection
                                    ref="inputerRow"
                                    :billsType="dataEditing.billsType"
                                    :organizationInnerSn="dataEditing.organizationInnerSn"
                                    :warehouseInnerSn="dataEditing.warehouseInnerSn"
                                    @submit="onBarcodeInputerSubmit"
                                ></barcode-inputer-inspection>
                            </template>
                            <template v-else>
                                <span>{{ rowIndex + 1 }}</span>
                            </template>
                        </template>
                    </vxe-table-column>
                    <vxe-table-column
                        v-for="column in productColumns"
                        v-bind="column"
                        :key="column.field"
                    ></vxe-table-column>
                    <vxe-table-column v-if="!isReadonly" v-bind="tableColumnConfig.operation" key="operation">
                        <template v-slot:default="{ rowIndex }">
                            <table-row-operator
                                copy
                                del
                                :payload="rowIndex"
                                @copy="handleCopyProduct"
                                @del="handleRemoveProduct"
                            ></table-row-operator>
                        </template>
                        <template v-slot:footer>
                            <div style="overflow: hidden">
                                <a-button size="small" type="primary" @click="handleSelectProducts">调单验货</a-button>
                            </div>
                        </template>
                    </vxe-table-column>
                </vxe-table>
            </div>
            <a-row style="margin: 18px 0 12px">
                <template v-if="isReadonly">
                    <a-col :span="24">
                        <span>备注：&nbsp;&nbsp;</span>
                        <span>{{ dataEditing.description }}</span>
                    </a-col>
                </template>
                <template v-else>
                    <a-col :span="2">
                        <span>备注：</span>
                    </a-col>
                    <a-col :span="22">
                        <a-input size="small" v-model="dataEditing.description"></a-input>
                    </a-col>
                </template>
            </a-row>
            <a-row>
                <a-col :span="4">制单人：{{ dataEditing.createdBy | textNullDataFilter }}</a-col>
                <a-col :span="4">生成时间：{{ dataEditing.createdTime | textNullDataFilter }}</a-col>
                <a-col :span="4">验货人：{{ dataEditing.inspectionBy | textNullDataFilter }}</a-col>
                <a-col :span="4">验货时间：{{ dataEditing.inspectionTime | textNullDataFilter }}</a-col>
                <a-col :span="4">复核人：{{ dataEditing.confirmBy | textNullDataFilter }}</a-col>
                <a-col :span="4">复核时间：{{ dataEditing.confirmTime | textNullDataFilter }}</a-col>
                <a-col :span="4" v-if="dataEditing.isCancel"
                    >作废人：{{ dataEditing.cancelBy | textNullDataFilter }}</a-col
                >
                <a-col :span="4" v-if="dataEditing.isCancel"
                    >作废时间：{{ dataEditing.cancelTime | textNullDataFilter }}</a-col
                >
                <a-col :span="16" v-if="dataEditing.isCancel">
                    <text-ellipsis>作废原因：{{ dataEditing.cancelReason | textNullDataFilter }}</text-ellipsis>
                </a-col>
            </a-row>
            <organization-selecter
                title="选择供货单位"
                destroyOnClose
                :visible="modalsVisible[modalsKeyMap.ORGANIZATION_SELECTER]"
                @close="handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                @submit="onOrganizationSelected"
            ></organization-selecter>
            <product-selecter-modal
                :visible="modalsVisible[modalsKeyMap.PRODUCT_SELECTER]"
                :tabs="productSelecterTabs"
                @close="handleCloseModals(modalsKeyMap.PRODUCT_SELECTER)"
            >
                <product-selecter-inspection
                    slot="useable"
                    :billsType="dataEditing.billsType"
                    :organizationInnerSn="dataEditing.organizationInnerSn"
                    :warehouseInnerSn="dataEditing.warehouseInnerSn"
                    @submit="onProductAdd"
                ></product-selecter-inspection>
            </product-selecter-modal>
            <base-modal-text-input
                destroyOnClose
                :visible="modalsVisible[modalsKeyMap.CANCEL_REASON]"
                title="填写作废原因"
                okText="提交"
                @close="handleCloseModals(modalsKeyMap.CANCEL_REASON)"
                @submit="onCancelReasonSubmit"
            ></base-modal-text-input>
            <item-selecter-table-modal
                destroyOnClose
                :visible="modalsVisible[modalsKeyMap.BARCODE_PRODUCT_FILTER]"
                :columns="productSelecterInspectionColumns"
                :data="barcodeProductsToFilter"
                title="请选择调单产品"
                @close="handleCloseModals(modalsKeyMap.BARCODE_PRODUCT_FILTER)"
                @submit="onBarcodeProductFilterSubmit"
            ></item-selecter-table-modal>
        </template>
        <template slot="footer">
            <a-button v-if="isApprovalOrder || !isReadonly" type="default" @click="handleClose">取消</a-button>
            <a-button v-else type="default" @click="handleClose">关闭</a-button>
            <template v-if="isApprovalOrder">
                <a-button type="danger" @click="onCancelButtonClick" :loading="buttonsLoading.cancel">作废</a-button>
                <a-button type="primary" @click="handleApproval" :loading="buttonsLoading.approval">复核</a-button>
            </template>
            <template v-if="!isReadonly">
                <a-button type="primary" @click="onSaveButtonClick" :loading="buttonsLoading.save">保存</a-button>
                <a-button type="primary" @click="handleInspect" :loading="buttonsLoading.save">验货</a-button>
            </template>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal';
import BaseModalTextInput from '@/components/BaseModalTextInput';
import ProductSelecterModal from '@/components/ProductSelecterModal';
import TextEllipsis from '@/components/TextEllipsis';
import TableRowOperator from '@/components/TableRowOperator';
import BaseInputWithClearButton from '@/components/BaseInputWithClearButton';
import ItemSelecterTableModal from '@/components/ItemSelecterTableModal';
import tableColumnConfig from '@/config/tableColumnConfig';
import textNullDataFilter from '@/filters/textNullData';
import {
    inspectionOrderAddAPI,
    inspectionOrderApprovalAPI,
    inspectionOrderCancelAPI,
    inspectionOrderDetailAPI,
    inspectionOrderEditAPI,
    inspectionOrderProductListBarcodeAPI
} from '@/service/pageAjax';
import modalsMixin from '@/mixins/modals';
import productUnitOptionsMixin from '@/mixins/productUnitOptions';
import mdrfOptionsMixin from '@/mixins/mdrfOptions';
import barcodeScanAPIResultParser from '@/mixins/barcodeScanAPIResultParser';
import barcodeInputFinish from '@/mixins/barcodeInputFinish';
import { orderTypeMap } from '@/config/constants';
import ProductSelecterInspection from './ProductSelecterInspection';
import OrganizationSelecter from './OrganizationSelecter';
import BarcodeInputerInspection from './BarcodeInputerInspection';
import productColumns from './inspectionOrderEditorColumns';
import productSelecterInspectionColumns from './productSelecterInspectionColumns.js';
import { inspectionOrderStatusMap } from './type';

const modalsKeyMap = Object.freeze({
    ORGANIZATION_SELECTER: 'o',
    PRODUCT_SELECTER: 'p',
    CANCEL_REASON: 'c',
    BARCODE_PRODUCT_FILTER: 'b'
});

export default {
    name: 'InspectionOrderEditor',
    components: {
        BaseModal,
        BaseModalTextInput,
        ProductSelecterModal,
        ProductSelecterInspection,
        OrganizationSelecter,
        TextEllipsis,
        TableRowOperator,
        BaseInputWithClearButton,
        BarcodeInputerInspection,
        ItemSelecterTableModal
    },
    mixins: [modalsMixin, productUnitOptionsMixin, mdrfOptionsMixin, barcodeScanAPIResultParser, barcodeInputFinish],
    filters: {
        textNullDataFilter
    },
    props: {
        managerMode: {
            type: Boolean,
            default: false
        },
        visible: {
            type: Boolean,
            default: false
        },
        targetOrder: {
            type: Object,
            default: null
        }
    },
    data() {
        return {
            tableColumnConfig,
            productSelecterInspectionColumns,
            modalsKeyMap,
            productColumns: Object.freeze(productColumns),
            dataEditing: {
                isCancel: 0,
                sn: undefined,
                billsType: undefined,
                organizationName: undefined,
                organizationInnerSn: undefined,
                warehouseName: undefined,
                warehouseInnerSn: undefined,
                description: undefined,
                createdBy: undefined,
                createdTime: undefined,
                inspectionBy: undefined,
                inspectionTime: undefined,
                confirmBy: undefined,
                confirmTime: undefined,
                cancelBy: undefined,
                cancelTime: undefined,
                cancelReason: undefined
            },
            dataEditingDetails: [],
            detailLoading: false,
            buttonsLoading: {
                save: false,
                approval: false
            },
            barcodeProductsToFilter: []
        };
    },
    computed: {
        productSelecterTabs() {
            return [{ title: '可用产品', key: 'useable' }];
        },

        /**
         * 全屏时表格高度
         */
        fullscreenTableHeight() {
            return this.$store.state.clientHeight - 348;
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
        },

        /**
         * 是否为新建验货单
         */
        isAddOrder() {
            return this.visible && this.targetOrder === null;
        },

        /**
         * 是否为编辑验货单
         */
        isEditOrder() {
            if (this.targetOrder && this.targetOrder.is_cancel) {
                return false;
            }
            return this.visible && this.targetOrder !== null;
        },

        /**
         * 是否为复核验货单
         */
        isApprovalOrder() {
            if (this.targetOrder && this.targetOrder.is_cancel) {
                return false;
            }
            return (
                this.managerMode &&
                this.targetOrder &&
                this.targetOrder.inspection_status === inspectionOrderStatusMap.INSPECTED.value
            );
        },

        /**
         * 是否只读
         */
        isReadonly() {
            if (this.targetOrder && this.targetOrder.is_cancel) {
                return true;
            }
            if (this.managerMode) {
                return true;
            }
            if (this.targetOrder) {
                if (
                    this.targetOrder.inspection_status === inspectionOrderStatusMap.INSPECTED.value ||
                    this.targetOrder.inspection_status === inspectionOrderStatusMap.REVIEWED.value
                ) {
                    return true;
                }
            }
            return false;
        },

        /**
         * 仓库选项
         */
        warehouseList() {
            return this.$store.state.warehouseList.map((item) => ({
                value: item.value,
                key: item.value,
                label: item.name
            }));
        },

        /**
         * 已选仓库的a-select的value
         */
        warehouseSelecterValue() {
            if (!this.dataEditing.warehouseInnerSn) {
                return undefined;
            }
            return {
                key: String(this.dataEditing.warehouseInnerSn),
                label: this.dataEditing.warehouseName
            };
        }
    },
    watch: {
        visible: {
            handler(newValue) {
                if (newValue) {
                    // 打开时初始化数据
                    if (this.targetOrder) {
                        // 编辑
                        this.loadOrderDetail(this.targetOrder.id);
                    } else {
                        // 新建
                        this.handleInit();
                    }
                    this.handleInit();
                } else {
                    // 关闭时清空数据
                    this.handleInit();
                }
            },
            immediate: true
        }
    },
    methods: {
        rowSpanMethod({ row, column }) {
            if (row.rowMark === 'inputerRow') {
                if (column.property === 'index') {
                    return { rowspan: 1, colspan: 14 };
                } else {
                    return { rowspan: 1, colspan: 0 };
                }
            }
            return { rowspan: 1, colspan: 1 };
        },
        /**
         * 过滤产品数据，删除多余的字段
         * @param {object} data 产品数据
         */
        productDataFilter(data) {
            return this.$XEUtils.pick(data, [
                'arrival_detail_id',
                'arrival_quantity',
                'batch_number',
                'mdrf_inner_sn',
                'mdrf_sn',
                'overdue_date',
                'produce_date',
                'product_inner_sn',
                'product_name',
                'product_type',
                'product_vendor_inner_sn',
                'product_vendor_name',
                'qualified_quantity',
                'specification',
                'specification_inner_sn',
                'unit',
                'unit_inner_sn',
                'unqualified_quantity',
                'unqualified_reason',
                'voucher_inner_sn_c',
                'voucher_type_c',
                'warehouse_inner_sn',
                'warehouse_name'
            ]);
        },

        onCancelButtonClick() {
            if (!this.targetOrder) {
                throw new Error('无目标单据');
            }
            this.$confirm({
                title: '确定要作废单据吗？',
                onOk: () => {
                    this.handleOpenModals(modalsKeyMap.CANCEL_REASON);
                },
                okText: '确定',
                cancelText: '取消'
            });
        },

        onSaveButtonClick() {
            this.handleSave();
        },

        onCancelReasonSubmit(reason, callback) {
            if (reason && reason.length > 0) {
                this.handleCancel(this.targetOrder.id, reason);
                callback(true);
            } else {
                this.$message.warning('作废原因不能为空');
                callback(false);
            }
        },

        /**
         * 表格单元格编辑激活时
         */
        async onEditActived({ row, column }) {
            switch (column.property) {
                case 'mdrf_inner_sn':
                    if (!row.options) {
                        this.$set(row, 'options', {});
                    }
                    if (!row.optionsLoading) {
                        this.$set(row, 'optionsLoading', {});
                    }
                    if (!row.optionsLoaded) {
                        this.$set(row, 'optionsLoaded', {});
                    }
                    if (!row.optionsLoaded.mdrf_inner_sn) {
                        try {
                            this.$set(row.optionsLoading, 'mdrf_inner_sn', true);
                            const options = await this.loadMdrfOptions(
                                row.product_vendor_inner_sn,
                                row.product_inner_sn
                            );
                            this.$set(row.options, 'mdrf_inner_sn', options);
                            this.$set(row.optionsLoaded, 'mdrf_inner_sn', true);
                        } finally {
                            this.$set(row.optionsLoading, 'mdrf_inner_sn', false);
                        }
                    }
                    break;
                default:
            }
        },

        onOrganizationSelected(organizations) {
            if (organizations && organizations.length > 0) {
                this.dataEditing.organizationName = organizations[0].organizationName;
                this.dataEditing.organizationInnerSn = organizations[0].organizationInnerSn;
                this.dataEditing.billsType = organizations[0].billsType;
                // 若更改了供货单位，移除相关的产品，平台产品则会保留
                if (this.dataEditingDetails.length > 0) {
                    let removedCount = 0;
                    const newDetails = this.dataEditingDetails.filter((product) => {
                        if (product.voucher_type_c === orderTypeMap.PO_RECORDS.key) {
                            removedCount += 1;
                            return false;
                        }
                        return true;
                    });
                    if (removedCount > 0) {
                        this.dataEditingDetails = newDetails;
                        this.$message.warning(`由于更改供货单位，相关产品（${removedCount}个）已移除`);
                    }
                }
            }
            this.handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER);
        },

        onOrganizationClear() {
            this.dataEditing.organizationName = undefined;
            this.dataEditing.organizationInnerSn = undefined;
            this.dataEditing.billsType = undefined;
            // 更改了供货单位，移除相关的产品，平台产品则会保留
            if (this.dataEditingDetails.length > 0) {
                let removedCount = 0;
                const newDetails = this.dataEditingDetails.filter((product) => {
                    if (product.voucher_type_c === orderTypeMap.PO_RECORDS.key) {
                        removedCount += 1;
                        return false;
                    }
                    return true;
                });
                if (removedCount > 0) {
                    this.dataEditingDetails = newDetails;
                    this.$message.warning(`由于更改供货单位，相关产品（${removedCount}个）已移除`);
                }
            }
        },

        onWarehouseChange(value = {}) {
            if (this.dataEditing.warehouseName === value.label && this.dataEditing.warehouseInnerSn === value.key) {
                return;
            }
            let removedCount = 0;
            this.dataEditing.warehouseName = value.label;
            this.dataEditing.warehouseInnerSn = value.key;
            // 过滤与当前仓库不符合的单据产品
            const newDetails = this.dataEditingDetails.filter((product) => {
                if (
                    product.voucher_type_c === orderTypeMap.PO_RECORDS.key &&
                    product.warehouse_inner_sn !== this.dataEditing.warehouseInnerSn
                ) {
                    removedCount += 1;
                    return false;
                }
                return true;
            });
            if (removedCount) {
                this.dataEditingDetails = newDetails;
                this.$message.warning(`由于更改仓库，相关产品（${removedCount}个）已移除`);
            }
        },

        onBarcodeInputerSubmit(products) {
            if (!products.length) {
                return;
            }
            if (products.length === 1) {
                const insertIndex = Math.max(0, this.dataEditingDetails.length - 1); // 最后一行为inputRow
                this.onProductAdd({ inspectionOrderProducts: products });
                this.$nextTick(() => {
                    this.$refs.xTable.scrollTo(686);
                    this.$refs.xTable.setActiveCell(this.dataEditingDetails[insertIndex], 'barcode');
                });
                return;
            }
            this.barcodeProductsToFilter = products;
            this.handleOpenModals(modalsKeyMap.BARCODE_PRODUCT_FILTER);
        },

        onBarcodeProductFilterSubmit(products) {
            const insertIndex = Math.max(0, this.dataEditingDetails.length - 1); // 最后一行为inputRow
            this.onProductAdd({ inspectionOrderProducts: products });
            this.handleCloseModals(modalsKeyMap.BARCODE_PRODUCT_FILTER);
            setTimeout(() => {
                this.$refs.xTable.scrollTo(686);
                this.$refs.xTable.setActiveCell(this.dataEditingDetails[insertIndex], 'barcode');
            }, 0);
        },

        /**
         * 产品选择返回值时
         * @param {object} data 产品数据
         * @param {array | undefined} data.inspectionOrderProducts 验货单可用产品
         */
        onProductAdd(data) {
            // 验货单可用产品
            if (data.inspectionOrderProducts) {
                let billsType = this.dataEditing.billsType;
                let organizationName = this.dataEditing.organizationName;
                let organizationInnerSn = this.dataEditing.organizationInnerSn;
                let warehouseName = this.dataEditing.warehouseName;
                let warehouseInnerSn = this.dataEditing.warehouseInnerSn;
                let baseTarget;
                let i = 0;
                let countAdd = 0;
                // 找出一个基准产品，根据该基准产品自动设置供货单位、仓库
                // 基准产品：若已设置供货单位，供货单位相同；若已设置仓库，仓库相同
                while (i < data.inspectionOrderProducts.length) {
                    let t = data.inspectionOrderProducts[i];
                    i += 1;
                    if (billsType && t.bills_type !== billsType) {
                        continue;
                    }
                    if (
                        (organizationInnerSn || organizationInnerSn === 0) &&
                        t.organization_inner_sn !== organizationInnerSn
                    ) {
                        continue;
                    }
                    if ((warehouseInnerSn || warehouseInnerSn === 0) && t.warehouse_inner_sn !== warehouseInnerSn) {
                        continue;
                    }
                    if (!baseTarget) {
                        baseTarget = t;
                        billsType = baseTarget.bills_type;
                        organizationName = baseTarget.organization_name;
                        organizationInnerSn = baseTarget.organization_inner_sn;
                        warehouseName = baseTarget.warehouse_name;
                        warehouseInnerSn = baseTarget.warehouse_inner_sn;
                    }
                    // 将可用的产品添加到列表
                    {
                        const mdrf_sn = t.mdrf_inner_sn ? t.mdrf_sn : undefined;
                        const mdrf_inner_sn = t.mdrf_inner_sn ? t.mdrf_inner_sn : undefined;
                        countAdd += 1;
                        this.dataEditingDetails.splice(Math.max(0, this.dataEditingDetails.length - 1), 0, {
                            ...this.productDataFilter(t),
                            arrival_quantity: t.order_quantity,
                            qualified_quantity: t.order_quantity,
                            unqualified_quantity: 0,
                            voucher_inner_sn_c: t.id,
                            voucher_type_c: orderTypeMap.PO_RECORDS.key,
                            mdrf_sn,
                            mdrf_inner_sn,
                            options: {
                                mdrf_inner_sn: [{ label: mdrf_sn, value: mdrf_inner_sn }]
                            },
                            optionsLoading: {
                                mdrf_inner_sn: false
                            },
                            optionsLoaded: {
                                mdrf_inner_sn: false
                            }
                        });
                    }
                }
                if (countAdd !== data.inspectionOrderProducts.length) {
                    this.$message.warning(
                        `与当前所设置供货单位和仓库不符合的产品（${
                            data.inspectionOrderProducts.length - countAdd
                        }个）已自动过滤`
                    );
                }
                this.dataEditing.billsType = billsType;
                this.dataEditing.organizationName = organizationName;
                this.dataEditing.organizationInnerSn = organizationInnerSn;
                this.dataEditing.warehouseName = warehouseName;
                this.dataEditing.warehouseInnerSn = warehouseInnerSn;
            }
            this.handleCloseModals(modalsKeyMap.PRODUCT_SELECTER);
            this.$nextTick(() => {
                this.$refs.xTable.scrollToRow(this.dataEditingDetails[this.dataEditingDetails.length - 1]);
            });
        },

        /**
         * 初始化编辑器
         * targetOrder为空时，将清空数据
         * @param {object | undefined} targetOrder 编辑的目标单据
         */
        handleInit(targetOrder = {}) {
            this.dataEditing.isCancel = targetOrder.is_cancel;
            this.dataEditing.sn = targetOrder.sn;
            this.dataEditing.billsType = targetOrder.bills_type;
            this.dataEditing.organizationName = targetOrder.organization_name;
            this.dataEditing.organizationInnerSn = targetOrder.organization_inner_sn;
            this.dataEditing.warehouseName = targetOrder.warehouse_name;
            this.dataEditing.warehouseInnerSn = targetOrder.warehouse_inner_sn;
            this.dataEditing.description = targetOrder.description;
            this.dataEditing.createdBy = targetOrder.created_by;
            this.dataEditing.createdTime = targetOrder.created_time;
            this.dataEditing.inspectionBy = targetOrder.inspection_by;
            this.dataEditing.inspectionTime = targetOrder.inspection_time;
            this.dataEditing.confirmBy = targetOrder.confirm_by;
            this.dataEditing.confirmTime = targetOrder.confirm_time;
            this.dataEditing.cancelBy = targetOrder.cancel_by;
            this.dataEditing.cancelTime = targetOrder.cancel_time;
            this.dataEditing.cancelReason = targetOrder.cancel_reason;
            if (targetOrder.details) {
                this.dataEditingDetails = targetOrder.details.map((product) => {
                    const _product = this.productDataFilter(product);
                    _product.vendor_inner_sn = product.product_vendor_inner_sn;
                    _product.vendor_name = product.product_vendor_name;
                    _product.options = {
                        mdrf_inner_sn: [{ label: product.mdrf_sn, value: product.mdrf_inner_sn }]
                    };
                    _product.optionsLoading = {
                        mdrf_inner_sn: false
                    };
                    _product.optionsLoaded = {
                        mdrf_inner_sn: false
                    };
                    return _product;
                });
                if (!this.isReadonly) {
                    this.dataEditingDetails.push({ rowMark: 'inputerRow' });
                }
            } else {
                this.dataEditingDetails = [{ rowMark: 'inputerRow' }];
            }
        },

        /**
         * 关闭编辑器
         */
        handleClose() {
            this.$emit('close');
        },

        handleActiveDefaultCell() {
            this.$refs.inputerRow.focus();
        },

        handleCheckData(data) {
            // 供货单位校验
            try {
                if (!data.organization_inner_sn && data.organization_inner_sn !== 0) {
                    throw new Error('organization_inner_sn');
                }
                if (!data.organization_name) {
                    throw new Error('organization_name');
                }
                if (!data.bills_type) {
                    throw new Error('bills_type');
                }
            } catch (err) {
                this.$message.error('请选择供货单位');
                throw err;
            }
            // 仓库校验
            if ((!data.warehouse_inner_sn && data.warehouse_inner_sn !== 0) || !data.warehouse_name) {
                this.$message.error('请选择仓库');
                throw new Error('warehouse_inner_sn');
            }
            // 产品数据校验
            if (!data.details.length) {
                this.$message.error('产品不能为空');
                throw new Error('details');
            }
            data.details.forEach((product, index) => {
                if (Number(product.arrival_quantity) <= 0) {
                    this.$message.error(`收货数量必须大于0（NO.${index + 1} ${product.product_name}）`);
                    throw new Error('arrival_quantity');
                }
                if (Number(product.qualified_quantity) < 0) {
                    this.$message.error(`合格产品数量不能为负（NO.${index + 1} ${product.product_name}）`);
                    throw new Error('qualified_quantity');
                }
                if (Number(product.unqualified_quantity) < 0) {
                    this.$message.error(`不合格产品数量不能为负（NO.${index + 1} ${product.product_name}）`);
                    throw new Error('unqualified_quantity');
                }
                if (
                    Number(product.qualified_quantity) + Number(product.unqualified_quantity) >
                    Number(product.arrival_quantity)
                ) {
                    this.$message.error(
                        `合格数量与不合格数量的和不能大于收货数量（NO.${index + 1} ${product.product_name}）`
                    );
                    throw new Error('qualified_quantity + unqualified_quantity');
                }
                if (product.overdue_date && new Date(product.overdue_date) <= new Date()) {
                    this.$message.error(`失效日期只能晚于当前时间（NO.${index + 1} ${product.product_name}）`);
                    throw new Error('overdue_date');
                }
            });
        },

        /**
         * 保存
         * @param {boolean} isInspect 是否为验货保存
         */
        async handleSave(isInspect = false) {
            const data = {
                id: undefined,
                inspection_status: undefined,
                bills_type: this.dataEditing.billsType,
                organization_name: this.dataEditing.organizationName,
                organization_inner_sn: this.dataEditing.organizationInnerSn,
                warehouse_name: this.dataEditing.warehouseName,
                warehouse_inner_sn: this.dataEditing.warehouseInnerSn,
                description: this.dataEditing.description,
                // 表格内产品数据
                details: this.dataEditingDetails
                    .filter((product) => product.rowMark !== 'inputerRow')
                    .map(this.productDataFilter)
            };
            this.handleCheckData(data);
            // 新增、新增并验货
            if (this.isAddOrder) {
                data.inspection_status = isInspect
                    ? inspectionOrderStatusMap.INSPECTED.value
                    : inspectionOrderStatusMap.NOT_INSPECTED.value;
                await this.addOrder(data);
                this.$emit('addFinish', data);
            }
            // 编辑、验货
            if (this.isEditOrder) {
                data.id = this.targetOrder.id;
                data.inspection_status = isInspect
                    ? inspectionOrderStatusMap.INSPECTED.value
                    : this.targetOrder.inspection_status;
                await this.editOrder(data);
                this.$emit('editFinish', data);
            }
            this.handleClose();
        },

        /**
         * 验货
         */
        handleInspect() {
            this.handleSave(true);
        },

        /**
         * 复核
         */
        async handleApproval() {
            if (!this.targetOrder) {
                throw new Error('无目标单据');
            }
            await this.approvalOrder(this.targetOrder.id);
            this.$emit('approvalFinish', { ...this.targetOrder });
            this.handleClose();
        },

        /**
         * 作废
         */
        async handleCancel(id, reason) {
            await this.cancelOrder(id, reason);
            this.$emit('cancelFinish', { ...this.targetOrder });
            this.handleClose();
        },

        handleSelectProducts() {
            if (this.isApprovalOrder || this.isReadonly) {
                return;
            }
            this.handleOpenModals(modalsKeyMap.PRODUCT_SELECTER);
        },

        /**
         * 复制表格内的一个产品
         * @param {number} index
         */
        handleCopyProduct(index) {
            const newProduct = { ...this.dataEditingDetails[index] };
            delete newProduct._XID;
            this.dataEditingDetails.splice(index + 1, 0, newProduct);
        },

        /**
         * 移除表格内的一个产品
         * @param {number} index
         */
        handleRemoveProduct(index) {
            this.dataEditingDetails.splice(index, 1);
        },

        /**
         * 检测单元格是否可编辑
         * @param {object} data
         * @param {object} data.row 行数据
         * @param {object} data.column 列信息
         */
        handleCheckCellEditable({ row, column }) {
            if (this.isApprovalOrder || this.isReadonly) {
                return false;
            }
            // 单据可用产品的收货数量不可编辑
            if (column.property === 'arrival_quantity' && row.voucher_type_c === orderTypeMap.PO_RECORDS.key) {
                return false;
            }
            return true;
        },

        /**
         * 加载验货单详情
         * @param {string | number} id 验货单id
         */
        async loadOrderDetail(id) {
            try {
                this.detailLoading = true;
                const { code, msg, info } = await inspectionOrderDetailAPI(id);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.handleInit(info);
            } catch (err) {
                this.$message.error('验货单详情获取失败');
                throw err;
            } finally {
                this.detailLoading = false;
            }
        },

        /**
         * 查询PI码信息
         * @param {string} barcode 条码
         */
        async loadBarcodePIInfo(barcode) {
            try {
                const { code, info, msg } = await inspectionOrderProductListBarcodeAPI({ barcode });
                if (code !== 0) {
                    throw new Error(msg || '条码查询服务错误');
                }
                try {
                    return this._mixinParseBarcodeResult(info, 'PI');
                } catch (err) {
                    this.$message.warning(err.message);
                }
            } catch (err) {
                this.$message.error(err.message);
            }
        },

        /**
         * 创建验货单
         */
        async addOrder(data) {
            try {
                this.buttonsLoading.save = true;
                const { code, msg } = await inspectionOrderAddAPI(data);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('验货单创建成功');
            } catch (err) {
                this.$message.error(err.message || '验货单创建失败');
                throw err;
            } finally {
                this.buttonsLoading.save = false;
            }
        },

        /**
         * 编辑验货单
         */
        async editOrder(data) {
            try {
                this.buttonsLoading.save = true;
                const { code, msg } = await inspectionOrderEditAPI(data);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('验货单保存成功');
            } catch (err) {
                this.$message.error(err.message || '验货单保存失败');
                throw err;
            } finally {
                this.buttonsLoading.save = false;
            }
        },

        /**
         * 复核验货单
         */
        async approvalOrder(id) {
            try {
                this.buttonsLoading.approval = true;
                const { code, msg } = await inspectionOrderApprovalAPI(id);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('验货单复核成功');
            } catch (err) {
                this.$message.error('验货单复核失败');
                throw err;
            } finally {
                this.buttonsLoading.approval = false;
            }
        },

        /**
         * 作废验货单
         * @param {string | number} id 单据id
         * @param {string} reason 作废原因
         */
        async cancelOrder(id, reason) {
            this.buttonsLoading.cancel = true;
            try {
                this.loading = true;
                const { code, msg } = await inspectionOrderCancelAPI(id, reason);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('作废验货单成功');
            } catch (err) {
                if (err.message) {
                    this.$message.error(err.message);
                } else {
                    this.$message.error('作废验货单失败');
                }
                throw err;
            } finally {
                this.buttonsLoading.cancel = false;
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
