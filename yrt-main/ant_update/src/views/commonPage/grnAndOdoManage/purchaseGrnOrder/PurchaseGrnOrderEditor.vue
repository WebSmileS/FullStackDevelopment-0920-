<template>
    <base-modal
        dragKey="arrival_order_editor_md"
        fullscreenUseable
        destroyOnClose
        :title="`${orderName}（${orderStatusText}）`"
        :width="1100"
        :visible="visible"
        @close="handleClose"
    >
        <template v-slot:content="{ fullscreen }">
            <div style="position: relative">
                <span v-show="!isAddOrder" style="position: absolute; right: 0">单据编号：{{ dataEditing.grnSn }}</span>
                <h6 style="margin: 24px 0; text-align: center; font-size: 24px; font-weight: 500">
                    <span v-if="organizationNameLogined">{{ organizationNameLogined }}&nbsp;&nbsp;</span>
                    <span>{{ orderName }}</span>
                    <span v-if="isRedOrder" class="red">（红冲）</span>
                    <span v-if="isCancelOrder">（作废）</span>
                </h6>
            </div>
            <div class="editor-form-header-wrapper" style="margin-top: 24px">
                <div class="editor-form-header-item">
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
                <div class="editor-form-header-item">
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
                <div class="editor-form-header-item">
                    <span class="label"><span v-if="!isReadonly" class="red">*</span>部门：</span>
                    <span v-if="isReadonly">{{ dataEditing.departmentName }}</span>
                    <a-tree-select
                        v-else
                        style="min-width: 120px"
                        showSearch
                        treeDefaultExpandAll
                        labelInValue
                        treeNodeFilterProp="title"
                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                        :treeData="departmentTree"
                        :value="departmentSelecterValue"
                        @change="onDepartmentChange"
                    ></a-tree-select>
                </div>
            </div>
            <div style="margin-top: 24px" @dblclick="handleSelectProducts">
                <vxe-table
                    border
                    show-overflow
                    show-header-overflow
                    show-footer
                    autoResize
                    resizable
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
                    :edit-config="{ trigger: 'click', mode: 'cell' }"
                    :edit-rules="{
                        'quantity': [{ min: 0.0000001, type: 'number', message: '数量必须大于0' }],
                        'unit_price': [{ min: 0, type: 'number', message: '含税单价必须大于等于0' }],
                        'money': [{ min: 0, type: 'number', message: '含税金额必须大于等于0' }]
                    }"
                    :span-method="rowSpanMethod"
                    :footer-cell-class-name="tableFooterCellClassName"
                    :footer-method="tableFooterMethod"
                    @cell-dblclick="({ $event }) => $event.stopPropagation()"
                    @edit-actived="onTableEditActived"
                    @edit-closed="onTableEditClosed"
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
                        <div>双击空白区域添加采购入库调单产品</div>
                    </template>
                    <vxe-table-column v-bind="tableColumnConfig.seq" field="index">
                        <template v-slot:default="{ row, rowIndex }">
                            <template v-if="row.rowMark === 'inputerRow'">
                                <barcode-inputer-purchase-grn
                                    ref="inputerRow"
                                    :billsType="dataEditing.billsType"
                                    :organizationInnerSn="dataEditing.organizationInnerSn"
                                    :warehouseInnerSn="dataEditing.warehouseInnerSn"
                                    @submit="onBarcodeInputerSubmit"
                                ></barcode-inputer-purchase-grn>
                            </template>
                            <template v-else>
                                <span>{{ rowIndex + 1 }}</span>
                            </template>
                        </template>
                    </vxe-table-column>
                    <vxe-table-column v-for="column in productColumns" v-bind="column" :key="column.field" />
                    <vxe-table-column v-if="!isReadonly" v-bind="tableColumnConfig.operation">
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
                                <a-button size="small" type="primary" @click="handleSelectProducts">调单入库</a-button>
                            </div>
                        </template>
                    </vxe-table-column>
                </vxe-table>
            </div>
            <div class="editor-form-footer-wrapper" style="margin-top: 24px">
                <div class="editor-form-footer-item">
                    <span class="label">采购员：</span>
                    <span v-if="isReadonly">{{ dataEditing.buyerName || '--' }}</span>
                    <a-input v-else size="small" v-model="dataEditing.buyerName"></a-input>
                </div>
                <div class="editor-form-footer-item">
                    <span class="label"><span v-if="!isReadonly" class="red">*</span>经手人：</span>
                    <span v-if="isReadonly">{{ dataEditing.handlerName || '--' }}</span>
                    <a-select
                        v-else
                        labelInValue
                        showSearch
                        :options="employeesData"
                        :value="handlerSelecterValue"
                        @change="onHandlerChange"
                    ></a-select>
                </div>
                <div class="editor-form-footer-item">
                    <span class="label">仓库人员：</span>
                    <span v-if="isReadonly">{{ dataEditing.warehouseOperatorName || '--' }}</span>
                    <a-select
                        v-else
                        labelInValue
                        showSearch
                        :options="warehouseOperatorOptions"
                        :value="warehouseOperatorSelecterValue"
                        @change="onWarehouseOperatorChange"
                    ></a-select>
                </div>
                <div class="editor-form-footer-item">
                    <span class="label">制单人：</span>
                    <span>{{ dataEditing.makingPeopleName || '--' }}</span>
                </div>
                <div class="editor-form-footer-item">
                    <span class="label">审核人：</span>
                    <span>{{ dataEditing.auditorName || '--' }}</span>
                </div>
            </div>
            <div class="editor-form-footer-full-wrapper" style="margin-top: 24px">
                <template v-if="isReadonly">
                    <span class="label">备注：</span>
                    <span class="content">{{ dataEditing.description }}</span>
                </template>
                <template v-else>
                    <span class="label">备注：</span>
                    <a-input size="small" v-model="dataEditing.description"></a-input>
                </template>
            </div>
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
                <product-selecter-purchase-grn
                    slot="useable"
                    :billsType="dataEditing.billsType"
                    :organizationInnerSn="dataEditing.organizationInnerSn"
                    :warehouseInnerSn="dataEditing.warehouseInnerSn"
                    @submit="onProductAdd"
                ></product-selecter-purchase-grn>
            </product-selecter-modal>
            <item-selecter-table-modal
                destroyOnClose
                :visible="modalsVisible[modalsKeyMap.BARCODE_PRODUCT_FILTER]"
                :columns="productSelecterPurchaseGrnColumns"
                :data="barcodeProductsToFilter"
                title="请选择调单产品"
                @close="handleCloseModals(modalsKeyMap.BARCODE_PRODUCT_FILTER)"
                @submit="onBarcodeProductFilterSubmit"
            ></item-selecter-table-modal>
        </template>
        <template slot="footer">
            <a-button
                v-if="isPutinOrder || isRedOrder || isCancelOrder"
                style="float: left"
                type="default"
                @click="handlePrint"
                >打印</a-button
            >
            <a-tooltip
                v-else
                style="float: left; margin-right: 8px"
                placement="top"
                title="已入库、红冲和作废状态的单据才可以打印"
            >
                <a-button disabled type="default">打印</a-button>
            </a-tooltip>
            <a-button style="float: left" type="default" @click="handleExportExcel">导出Excel</a-button>
            <a-button v-if="isPutinOrder || isCancelOrder || isRedOrder" type="default" @click="handleClose"
                >关闭</a-button
            >
            <a-button v-else type="default" @click="handleClose">取消</a-button>
            <a-button v-if="!isReadonly" type="primary" @click="onSaveButtonClick" :loading="buttonsLoading.save"
                >保存</a-button
            >
            <a-button
                v-if="isFormalOrder"
                type="primary"
                @click="onApprovalButtonClick"
                :loading="buttonsLoading.approval"
                >审核</a-button
            >
            <a-button v-if="isApprovaledOrder" type="primary" @click="onPutinButtonClick" :loading="buttonsLoading.save"
                >入库</a-button
            >
            <a-button v-if="isPutinOrder" type="danger" @click="onRedButtonClick" :loading="buttonsLoading.red"
                >生成红冲</a-button
            >
        </template>
    </base-modal>
</template>

<script>
import Big from 'big.js';
import BaseModal from '@/components/BaseModal';
import ProductSelecterModal from '@/components/ProductSelecterModal';
import TableRowOperator from '@/components/TableRowOperator';
import textNullDataFilter from '@/filters/textNullData';
import BaseInputWithClearButton from '@/components/BaseInputWithClearButton';
import ItemSelecterTableModal from '@/components/ItemSelecterTableModal';
import {
    grnPutinAPI,
    grnRedDashedAPI,
    purchaseGrnAddAPI,
    purchaseGrnEditAPI,
    purchaseGrnInfoAPI,
    purchaseGrnProductListBarcodeAPI
} from '@/service/pageAjax';
import tableColumnConfig from '@/config/tableColumnConfig';
import modalsMixin from '@/mixins/modals';
import productUnitOptionsMixin from '@/mixins/productUnitOptions';
import mdrfOptionsMixin from '@/mixins/mdrfOptions';
import departmentTreeMixin from '@/mixins/departmentTree';
import employeesMixin from '@/mixins/employees';
import warehouseOperatorOptionsMixin from '@/mixins/warehouseOperatorOptions';
import barcodeScanAPIResultParser from '@/mixins/barcodeScanAPIResultParser';
import barcodeInputFinish from '@/mixins/barcodeInputFinish';
import { orderTypeMap } from '@/config/constants';
import ProductSelecterPurchaseGrn from './ProductSelecterPurchaseGrn';
import BarcodeInputerPurchaseGrn from './BarcodeInputerPurchaseGrn';
import OrganizationSelecter from './OrganizationSelecter';
import productColumns from './purchaseGrnOrderEditorColumns';
import productSelecterPurchaseGrnColumns from './productSelecterPurchaseGrnColumns.js';
import { purchaseGrnOrderDetailActionTypeMap, purchaseGrnOrderProductTypeMap, purchaseGrnOrderStatusMap } from './type';
import { purchaseGrnOrderStatusTextFilter } from './filters';

const ORDER_NAME = '采购入库单';

const modalsKeyMap = Object.freeze({
    ORGANIZATION_SELECTER: 'o',
    PRODUCT_SELECTER: 'p',
    CANCEL_REASON: 'c'
});

export default {
    name: 'PurchaseGrnOrderEditor',
    components: {
        BaseModal,
        ProductSelecterModal,
        ProductSelecterPurchaseGrn,
        OrganizationSelecter,
        TableRowOperator,
        BaseInputWithClearButton,
        BarcodeInputerPurchaseGrn,
        ItemSelecterTableModal
    },
    mixins: [
        modalsMixin,
        productUnitOptionsMixin,
        mdrfOptionsMixin,
        departmentTreeMixin,
        employeesMixin,
        warehouseOperatorOptionsMixin,
        barcodeScanAPIResultParser,
        barcodeInputFinish
    ],
    filters: {
        textNullDataFilter
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        targetOrder: {
            type: Object,
            default: null
        },
        targetOrderIsCopy: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            productSelecterPurchaseGrnColumns,
            tableColumnConfig,
            modalsKeyMap,
            orderName: ORDER_NAME,
            departmentTree: null,
            warehouseOperatorOptions: null,
            dataEditing: {
                auditorInnerSn: undefined,
                auditorName: undefined,
                billsType: undefined,
                buyerName: undefined,
                createTime: undefined,
                departmentInnerSn: undefined,
                departmentName: undefined,
                description: undefined,
                grnInnerSn: undefined,
                grnSn: undefined,
                handlerInnerSn: undefined,
                handlerName: undefined,
                inTime: undefined,
                makingPeopleInnerSn: undefined,
                makingPeopleName: undefined,
                orgId: undefined,
                orgName: undefined,
                organizationInnerSn: undefined,
                organizationName: undefined,
                organizationType: undefined,
                status: undefined,
                typeInnerSn: undefined,
                typeName: undefined,
                voucherInnerSnC: undefined,
                voucherTypeC: undefined,
                warehouseInnerSn: undefined,
                warehouseName: undefined,
                warehouseOperatorInnerSn: undefined,
                warehouseOperatorName: undefined
            },
            dataEditingDetails: [],
            dataRemovedDetails: [],
            dataEditingDetailsMoneySummation: 0,
            detailLoading: false,
            buttonsLoading: {
                save: false,
                approval: false,
                putin: false,
                red: false
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
         * 是否为新建单
         */
        isAddOrder() {
            return this.visible && (this.targetOrder === null || this.targetOrderIsCopy);
        },

        /**
         * 是否为编辑单
         */
        isEditOrder() {
            return this.visible && this.targetOrder !== null && !this.targetOrderIsCopy;
        },

        /**
         * 是否为正式单
         */
        isFormalOrder() {
            return this.targetOrder && this.targetOrder.status === purchaseGrnOrderStatusMap.FORMAL.value;
        },

        /**
         * 是否为已审核单
         */
        isApprovaledOrder() {
            return this.targetOrder && this.targetOrder.status === purchaseGrnOrderStatusMap.APPROVALED.value;
        },

        /**
         * 是否为已入库单
         */
        isPutinOrder() {
            return this.targetOrder && this.targetOrder.status === purchaseGrnOrderStatusMap.GRN.value;
        },

        /**
         * 是否为红冲单
         */
        isRedOrder() {
            return this.targetOrder && this.targetOrder.status === purchaseGrnOrderStatusMap.RED.value;
        },

        /**
         * 是否为作废单
         */
        isCancelOrder() {
            return this.targetOrder && this.targetOrder.status === purchaseGrnOrderStatusMap.CANCEL.value;
        },

        /**
         * 是否只读
         */
        isReadonly() {
            if (this.targetOrder) {
                if (
                    this.targetOrder.status === purchaseGrnOrderStatusMap.APPROVALED.value ||
                    this.targetOrder.status === purchaseGrnOrderStatusMap.GRN.value ||
                    this.targetOrder.status === purchaseGrnOrderStatusMap.RED.value ||
                    this.targetOrder.status === purchaseGrnOrderStatusMap.CANCEL.value
                ) {
                    return true;
                }
            }
            return false;
        },

        /**
         * 单据状态文本
         */
        orderStatusText() {
            if (this.isAddOrder) {
                return '新增';
            }
            return purchaseGrnOrderStatusTextFilter(this.targetOrder && this.targetOrder.status);
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
        },

        departmentSelecterValue() {
            if (!this.dataEditing.departmentInnerSn) {
                return undefined;
            }
            return {
                key: String(this.dataEditing.departmentInnerSn),
                label: this.dataEditing.departmentName
            };
        },

        handlerSelecterValue() {
            if (!this.dataEditing.handlerInnerSn && this.dataEditing.handlerInnerSn !== 0) {
                return undefined;
            }
            return {
                key: String(this.dataEditing.handlerInnerSn),
                label: this.dataEditing.handlerName
            };
        },

        warehouseOperatorSelecterValue() {
            if (!this.dataEditing.warehouseOperatorInnerSn && this.dataEditing.warehouseOperatorInnerSn !== 0) {
                return undefined;
            }
            return {
                key: String(this.dataEditing.warehouseOperatorInnerSn),
                label: this.dataEditing.warehouseOperatorName
            };
        },

        /**
         * 表格列
         */
        productColumns() {
            return productColumns.map((column) => {
                const newColumn = { ...column };
                if (this.isReadonly) {
                    delete newColumn.editRender;
                } else if (newColumn.editRender) {
                    delete newColumn.cellRender;
                }
                return newColumn;
            });
        }
    },
    watch: {
        visible: {
            handler(newValue) {
                if (newValue) {
                    // 打开时初始化数据（请保证数据初始化逻辑处于最优先顺序）
                    if (this.targetOrder) {
                        // 编辑、复制
                        this.loadOrderDetail(this.targetOrder.grn_inner_sn, this.targetOrder.billsType);
                    } else {
                        // 新建
                        this.handleInit();
                        try {
                            const userInfo = JSON.parse(this.$cookie.get('loginDatas'));
                            if (userInfo) {
                                // 新建时默认将经手人设置为当前登录的人
                                if (userInfo.EmployeeInfo) {
                                    this.dataEditing.handlerName = userInfo.EmployeeInfo.Name;
                                    this.dataEditing.handlerInnerSn = userInfo.EmployeeInfo.Employee_inner_sn;
                                }
                                // 新建时默认将部门设置为当前登录人所属的第一个部门
                                if (userInfo.Departments && userInfo.Departments.length) {
                                    this.dataEditing.departmentName = userInfo.Departments[0].Name;
                                    this.dataEditing.departmentInnerSn = userInfo.Departments[0].Department_inner_sn;
                                }
                            }
                        } catch (err) {
                            console.log(err);
                            console.warning('人员相关默认值设置失败');
                        }
                    }

                    // 打开时，若部门列表未加载，加载部门列表
                    if (this.departmentTree === null) {
                        this.loadDepartmentTree();
                    }

                    // 打开时，若人员列表未加载，加载人员列表
                    if (!this.employeesDataLoaded) {
                        this.handleInitEmployeesMixin();
                    }

                    // 打开时，若仓库已选，加载仓库人员
                    if (
                        this.targetOrder &&
                        (this.targetOrder.warehouse_inner_sn || this.targetOrder.warehouse_inner_sn === 0)
                    ) {
                        this.loadWarehouseOperatorOptions(this.targetOrder.warehouse_inner_sn);
                    }
                } else {
                    // 关闭时清空数据
                    this.handleInit();
                }
            },
            immediate: true
        },
        dataEditingDetails() {
            // 重新计算合计值
            this.handleCalculateSummation();
        }
    },
    methods: {
        rowSpanMethod({ row, column }) {
            if (row.rowMark === 'inputerRow') {
                if (column.property === 'index') {
                    return { rowspan: 1, colspan: 15 };
                } else {
                    return { rowspan: 1, colspan: 0 };
                }
            }
            return { rowspan: 1, colspan: 1 };
        },
        tableFooterMethod() {
            let result = [];
            result[0] = '合计';
            result[8] = this.dataEditingDetailsMoneySummation;
            return [result];
        },
        tableFooterCellClassName(payload) {
            if (payload.columnIndex === 0) {
                return 'total-bg';
            }
            return 'red';
        },
        async onSaveButtonClick() {
            this.buttonsLoading.save = true;
            try {
                const data = this.handlePreparePostData();

                // 新增单据时
                if (this.isAddOrder) {
                    data.status = purchaseGrnOrderStatusMap.FORMAL.value;
                    const newData = await this.addOrder(data);
                    this.$emit('orderAdd', newData);
                    this.$emit('close');
                    return;
                }

                // 编辑单据时
                if (this.isEditOrder) {
                    // 设置old标识的字段，供后端使用
                    Object.keys(data).forEach((key) => {
                        switch (key) {
                            case 'details':
                            case 'grn_inner_sn':
                            case 'org_id':
                            case 'status':
                                break;
                            default:
                                data[`${key}_old`] = this.targetOrder[key];
                        }
                    });
                    data.status = purchaseGrnOrderStatusMap.FORMAL.value;
                    data.grn_inner_sn = this.targetOrder.grn_inner_sn;
                    // 设置明细值的action标识
                    data.details.forEach((product) => {
                        if (product.action === undefined || product.action === null) {
                            product.action = purchaseGrnOrderDetailActionTypeMap.ADD;
                        }
                    });
                    data.details = [...this.dataRemovedDetails, ...data.details];
                    const newData = await this.editOrder(data);
                    this.$emit('orderChange', newData);
                    this.$emit('close');
                    this.buttonsLoading.save = false;
                }
            } finally {
                this.buttonsLoading.save = false;
            }
        },

        /**
         * 审核按钮点击
         */
        async onApprovalButtonClick() {
            this.buttonsLoading.approval = true;
            try {
                const data = this.handlePreparePostData();
                // 设置old标识的字段，供后端使用
                Object.keys(data).forEach((key) => {
                    switch (key) {
                        case 'details':
                        case 'grn_inner_sn':
                        case 'org_id':
                        case 'status':
                            break;
                        default:
                            data[`${key}_old`] = this.targetOrder[key];
                    }
                });
                data.status = purchaseGrnOrderStatusMap.APPROVALED.value;
                data.grn_inner_sn = this.targetOrder.grn_inner_sn;
                // 设置明细值的action标识
                data.details.forEach((product) => {
                    product.action = purchaseGrnOrderDetailActionTypeMap.EDIT;
                });
                data.details = [...this.dataRemovedDetails, ...data.details];
                const newData = await this.approvalOrder(data);
                this.$emit('orderChange', newData);
                this.$emit('close');
            } finally {
                this.buttonsLoading.approval = false;
            }
        },

        /**
         * 入库按钮点击
         */
        async onPutinButtonClick() {
            this.$confirm({
                title: '确定要入库吗？',
                onOk: async () => {
                    try {
                        const orgId = this.$cookie.get('userbelong');
                        this.buttonsLoading.putin = true;
                        const { code, msg } = await grnPutinAPI({
                            billsType: this.targetOrder.billsType,
                            org_id: orgId,
                            grn_inner_sn: this.targetOrder.grn_inner_sn,
                            warehouse_inner_sn: this.targetOrder.warehouse_inner_sn,
                            warehouse_name: this.targetOrder.warehouse_name
                        });
                        if (code !== 0) {
                            throw new Error(msg);
                        }
                        this.$message.success('入库成功');
                        this.$emit('orderChange', { ...this.targetOrder, status: purchaseGrnOrderStatusMap.GRN.value });
                        this.$emit('close');
                    } catch (err) {
                        this.$message.error(err.message || '入库失败');
                        console.error(err);
                        return;
                    } finally {
                        this.buttonsLoading.putin = false;
                    }
                }
            });
        },

        /**
         * 红冲按钮点击
         */
        async onRedButtonClick() {
            this.$confirm({
                title: '确定要红冲吗？',
                onOk: async () => {
                    try {
                        const orgId = this.$cookie.get('userbelong');
                        this.buttonsLoading.red = true;
                        const { code, info, msg } = await grnRedDashedAPI({
                            billsType: this.targetOrder.billsType,
                            org_id: orgId,
                            grn_inner_sn: this.targetOrder.grn_inner_sn
                        });
                        if (code !== 0) {
                            throw new Error(msg);
                        }
                        this.$message.success('红冲成功');
                        this.$emit('redFinish', info);
                        this.$emit('close');
                    } catch (err) {
                        this.$message.error(err.message || '红冲失败');
                        console.error(err);
                        return;
                    } finally {
                        this.buttonsLoading.red = false;
                    }
                }
            });
        },

        /**
         * 表格单元格编辑激活时
         */
        async onTableEditActived({ row, column }) {
            if (!row.options) {
                this.$set(row, 'options', {});
            }
            if (!row.optionsLoading) {
                this.$set(row, 'optionsLoading', {});
            }
            if (!row.optionsLoaded) {
                this.$set(row, 'optionsLoaded', {});
            }
            switch (column.property) {
                case 'mdrf_inner_sn':
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
                case 'unit_inner_sn':
                    // 若单位选项未加载则加载单位
                    if (!row.optionsLoaded.unit_inner_sn) {
                        try {
                            this.$set(row.optionsLoading, 'unit_inner_sn', true);
                            const options = await this.loadProductUnitOptions({
                                ...row,
                                vendor_inner_sn: row.vendor_inner_sn || row.product_vendor_inner_sn
                            });
                            this.$set(row.options, 'unit_inner_sn', options);
                            this.$set(row.optionsLoaded, 'unit_inner_sn', true);
                        } finally {
                            this.$set(row.optionsLoading, 'unit_inner_sn', false);
                        }
                    }
                    break;
                default:
            }
        },

        onTableEditClosed({ row, column }) {
            if (row.rowMark === 'inputerRow') {
                return;
            }
            switch (column.property) {
                // 单价或数量更新时，自动计算金额
                case 'unit_price':
                case 'quantity':
                    row.money = new Big(row.quantity).times(row.unit_price).toNumber();
                    this.handleCalculateSummation();
                    break;
                // 金额更新时，自动计算单价
                case 'money':
                    row.unit_price = new Big(row.money).div(row.quantity).toNumber();
                    this.handleCalculateSummation();
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
            // 更新值
            let removedCount = 0;
            this.dataEditing.warehouseName = value.label;
            this.dataEditing.warehouseInnerSn = value.key;

            // 重置仓库人员值，加载仓库人员列表
            if (value.key || value.key === 0) {
                this.loadWarehouseOperatorOptions(value.key);
            } else {
                this.warehouseOperatorOptions = null;
            }
            if (this.dataEditing.warehouseOperatorInnerSn && this.dataEditing.warehouseOperatorName) {
                this.dataEditing.warehouseOperatorInnerSn = undefined;
                this.dataEditing.warehouseOperatorName = undefined;
                this.$message.warning('由于更改仓库，仓库人员值已重置');
            }

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

        onDepartmentChange(value) {
            this.dataEditing.departmentName = value.label;
            this.dataEditing.departmentInnerSn = value.value;
        },

        onHandlerChange(value) {
            this.dataEditing.handlerName = value.label;
            this.dataEditing.handlerInnerSn = value.key;
        },

        onWarehouseOperatorChange(value) {
            this.dataEditing.warehouseOperatorName = value.label;
            this.dataEditing.warehouseOperatorInnerSn = value.key;
        },

        onBarcodeInputerSubmit(products) {
            if (!products.length) {
                return;
            }
            if (products.length === 1) {
                const insertIndex = Math.max(0, this.dataEditingDetails.length - 1); // 最后一行为inputRow
                this.onProductAdd({ purchaseGrnOrderProducts: products });
                this.$nextTick(() => {
                    this.$refs.xTable.scrollTo(826);
                    this.$refs.xTable.setActiveCell(this.dataEditingDetails[insertIndex], 'barcode');
                });
                return;
            }
            this.barcodeProductsToFilter = products;
            this.handleOpenModals(modalsKeyMap.BARCODE_PRODUCT_FILTER);
        },

        onBarcodeProductFilterSubmit(products) {
            const insertIndex = Math.max(0, this.dataEditingDetails.length - 1); // 最后一行为inputRow
            this.onProductAdd({ purchaseGrnOrderProducts: products });
            this.handleCloseModals(modalsKeyMap.BARCODE_PRODUCT_FILTER);
            setTimeout(() => {
                this.$refs.xTable.scrollTo(826);
                this.$refs.xTable.setActiveCell(this.dataEditingDetails[insertIndex], 'barcode');
            }, 0);
        },

        /**
         * 产品选择返回值时
         * @param {object} data 产品数据
         * @param {array | undefined} data.purchaseGrnOrderProducts 收货单可用产品
         */
        onProductAdd(data) {
            // 采购入库单可用产品
            if (data.purchaseGrnOrderProducts) {
                let billsType = this.dataEditing.billsType;
                let organizationName = this.dataEditing.organizationName;
                let organizationInnerSn = this.dataEditing.organizationInnerSn;
                let warehouseName = this.dataEditing.warehouseName;
                let warehouseInnerSn = this.dataEditing.warehouseInnerSn;
                let buyerNameMap = {};
                let baseTarget;
                let i = 0;
                let countAdd = 0;
                // 找出一个基准产品，根据该基准产品自动设置相关单位、仓库、采购员
                // 基准产品：若已设置相关单位，相关单位相同；若已设置仓库，仓库相同
                while (i < data.purchaseGrnOrderProducts.length) {
                    let t = data.purchaseGrnOrderProducts[i];
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
                        let unitOptions;
                        let mdrfOptions;
                        let money = new Big(t.order_quantity).times(t.unit_price).toNumber();
                        if (t.unit_inner_sn || t.unit_inner_sn === 0) {
                            unitOptions = [
                                {
                                    value: t.unit_inner_sn,
                                    label: t.unit
                                }
                            ];
                        }
                        if (t.mdrf_inner_sn || t.mdrf_inner_sn === 0) {
                            mdrfOptions = [
                                {
                                    value: t.mdrf_inner_sn,
                                    label: t.mdrf_sn
                                }
                            ];
                        }
                        buyerNameMap[t.buyer_name] = true;
                        countAdd += 1;
                        this.dataEditingDetails.splice(Math.max(0, this.dataEditingDetails.length - 1), 0, {
                            ...t,
                            money,
                            unit_name: t.unit,
                            quantity: t.order_quantity,
                            quantity_old: t.order_quantity,
                            voucher_detail_inner_sn_c: t.id,
                            voucher_inner_sn_c: t.id,
                            voucher_type_c: orderTypeMap.PO_RECORDS.key,
                            produce_date: t.produce_date,
                            overdue_date: t.overdue_date,
                            type: 0,
                            health_care_sn: undefined,
                            action: purchaseGrnOrderDetailActionTypeMap.ADD,
                            options: {
                                unit_inner_sn: unitOptions,
                                mdrf_inner_sn: mdrfOptions
                            },
                            optionsLoading: { unit_inner_sn: false, mdrf_inner_sn: false },
                            optionsLoaded: { unit_inner_sn: false, mdrf_inner_sn: false }
                        });
                    }
                }
                if (countAdd !== data.purchaseGrnOrderProducts.length) {
                    this.$message.warning(
                        `与当前所设置单位和仓库不符合的产品（${
                            data.purchaseGrnOrderProducts.length - countAdd
                        }个）已自动过滤`
                    );
                }
                this.dataEditing.billsType = billsType;
                this.dataEditing.organizationName = organizationName;
                this.dataEditing.organizationInnerSn = organizationInnerSn;
                // 若仓库发生变更，加载仓库人员列表
                if (this.dataEditing.warehouseInnerSn !== warehouseInnerSn) {
                    this.loadWarehouseOperatorOptions(warehouseInnerSn);
                }
                this.dataEditing.warehouseName = warehouseName;
                this.dataEditing.warehouseInnerSn = warehouseInnerSn;
                // 将多个采购员名称用“,”连接
                if (this.dataEditing.buyerName) {
                    this.dataEditing.buyerName.split(',').forEach((name) => {
                        buyerNameMap[name] = true;
                    });
                }
                this.dataEditing.buyerName = Object.keys(buyerNameMap).join(',');
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
            this.dataEditing.auditorInnerSn = targetOrder.auditor_inner_sn;
            this.dataEditing.auditorName = targetOrder.auditor_name;
            this.dataEditing.billsType = targetOrder.billsType;
            this.dataEditing.buyerName = targetOrder.buyer_name;
            this.dataEditing.createTime = targetOrder.create_time;
            this.dataEditing.departmentInnerSn = targetOrder.department_inner_sn;
            this.dataEditing.departmentName = targetOrder.department_name;
            this.dataEditing.description = targetOrder.description;
            this.dataEditing.grnInnerSn = targetOrder.grn_inner_sn;
            this.dataEditing.grnSn = targetOrder.grn_sn;
            this.dataEditing.handlerInnerSn = targetOrder.handler_inner_sn;
            this.dataEditing.handlerName = targetOrder.handler_name;
            this.dataEditing.inTime = targetOrder.in_time;
            this.dataEditing.makingPeopleInnerSn = targetOrder.making_people_inner_sn;
            this.dataEditing.makingPeopleName = targetOrder.making_people_name;
            this.dataEditing.orgId = targetOrder.org_id;
            this.dataEditing.orgName = targetOrder.org_name;
            this.dataEditing.organizationInnerSn = targetOrder.organization_inner_sn;
            this.dataEditing.organizationName = targetOrder.organization_name;
            this.dataEditing.organizationType = targetOrder.organization_type;
            this.dataEditing.status = targetOrder.status;
            this.dataEditing.typeInnerSn = targetOrder.type_inner_sn;
            this.dataEditing.typeName = targetOrder.type_name;
            this.dataEditing.voucherInnerSnC = targetOrder.voucher_inner_sn_c;
            this.dataEditing.voucherTypeC = targetOrder.voucher_type_c;
            this.dataEditing.warehouseInnerSn = targetOrder.warehouse_inner_sn;
            this.dataEditing.warehouseName = targetOrder.warehouse_name;
            this.dataEditing.warehouseOperatorInnerSn = targetOrder.warehouse_operator_inner_sn;
            this.dataEditing.warehouseOperatorName = targetOrder.warehouse_operator_name;
            if (this.targetOrderIsCopy) {
                this.dataEditing.auditorInnerSn = undefined;
                this.dataEditing.auditorName = undefined;
                this.dataEditing.createTime = undefined;
                this.dataEditing.grnInnerSn = undefined;
                this.dataEditing.grnSn = undefined;
                this.dataEditing.inTime = undefined;
                this.dataEditing.makingPeopleInnerSn = undefined;
                this.dataEditing.makingPeopleName = undefined;
                this.dataEditing.orgId = undefined;
                this.dataEditing.orgName = undefined;
                this.dataEditing.status = undefined;
                this.dataEditing.typeInnerSn = undefined;
                this.dataEditing.typeName = undefined;
                this.dataEditing.voucherInnerSnC = undefined;
                this.dataEditing.voucherTypeC = undefined;
            }
            if (targetOrder.details) {
                this.dataEditingDetails = targetOrder.details.map((product) => {
                    let unitOptions;
                    let mdrfOptions;
                    let money = new Big(product.unit_price).times(product.quantity).toNumber();
                    money = isNaN(money) ? undefined : money;
                    if (product.unit_inner_sn || product.unit_inner_sn === 0) {
                        unitOptions = [{ value: product.unit_inner_sn, label: product.unit_name }];
                    }
                    if (product.mdrf_inner_sn || product.mdrf_inner_sn === 0) {
                        mdrfOptions = [{ value: product.mdrf_inner_sn, label: product.mdrf_sn }];
                    }
                    if (product.product_vendor_inner_sn === undefined) {
                        product.product_vendor_inner_sn = product.vendor_inner_sn;
                        product.product_vendor_name = product.vendor_name;
                    }
                    return {
                        ...product,
                        money,
                        action: this.targetOrderIsCopy
                            ? purchaseGrnOrderDetailActionTypeMap.ADD
                            : purchaseGrnOrderDetailActionTypeMap.EDIT,
                        quantity_old: product.quantity,
                        options: {
                            unit_inner_sn: unitOptions,
                            mdrf_inner_sn: mdrfOptions
                        },
                        optionsLoading: {
                            unit_inner_sn: false,
                            mdrf_inner_sn: false
                        },
                        optionsLoaded: {
                            unit_inner_sn: false,
                            mdrf_inner_sn: false
                        }
                    };
                });
                if (!this.isReadonly) {
                    this.dataEditingDetails.push({ rowMark: 'inputerRow' });
                }
            } else {
                this.dataEditingDetails = [{ rowMark: 'inputerRow' }];
                this.dataRemovedDetails = [];
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

        /**
         * 数据校验
         */
        handleCheckData(data) {
            // 单位名称校验
            try {
                if (!data.organization_inner_sn && data.organization_inner_sn !== 0) {
                    throw new Error('organization_inner_sn');
                }
                if (!data.organization_name) {
                    throw new Error('organization_name');
                }
                if (!data.billsType) {
                    throw new Error('billsType');
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
            // 部门校验
            if ((!data.department_inner_sn && data.department_inner_sn !== 0) || !data.department_name) {
                this.$message.error('请选择部门');
                throw new Error('department_inner_sn');
            }
            // 经手人校验
            if ((!data.handler_inner_sn && data.handler_inner_sn !== 0) || !data.handler_name) {
                this.$message.error('请选择经手人');
                throw new Error('handler_inner_sn');
            }
            // 产品数据校验
            if (!data.details.length) {
                this.$message.error('产品不能为空');
                throw new Error('details');
            }
            data.details.forEach((product) => {
                if ((!product.unit_inner_sn && product.unit_inner_sn !== 0) || !product.unit_name) {
                    this.$message.error(`产品单位不能为空（${product.product_name}）`);
                    throw new Error('unit_inner_sn');
                }
                if (!(Number(product.quantity) > 0)) {
                    this.$message.error(`产品数量必须大于0（${product.product_name}）`);
                    throw new Error('quantity');
                }
                // if (!product.produce_date) {
                //     this.$message.error(`产品失效日期不能为空（${product.product_name}）`);
                //     throw new Error('produce_date');
                // }
                // if (!product.overdue_date) {
                //     this.$message.error(`产品失效日期不能为空（${product.product_name}）`);
                //     throw new Error('overdue_date');
                // }
                if (product.overdue_date && new Date(product.overdue_date) <= new Date()) {
                    this.$message.error(`失效日期只能晚于当前时间（${product.product_name}）`);
                    throw new Error('overdue_date');
                }
                if (
                    product.produce_date &&
                    product.overdue_date &&
                    new Date(product.produce_date) >= new Date(product.overdue_date)
                ) {
                    this.$message.error(`生产日期应该早于失效日期（${product.product_name}）`);
                    throw new Error('produce_date');
                }
            });
        },

        /**
         * 打印
         */
        handlePrint() {
            const data = this.handlePreparePostData(false);
            let targetOrder = this.targetOrder || {};
            let routeUrl = this.$router.resolve({
                path: `/newprint/inboundOrder`
            });
            window.localStorage.setItem(
                'printContent',
                JSON.stringify({
                    printData: data.details.map((item, index) => ({
                        ...item,
                        index: index + 1,
                        vendor_name: item.product_vendor_name,
                        tax_unit_price: item.unit_price,
                        batch_number: item.batch_number.trim()
                    })),
                    printForm: {
                        ...targetOrder,
                        ...data,
                        details: undefined,
                        type_name: `${ORDER_NAME}${this.isRedOrder ? '（红冲）' : ''}${
                            this.isCancelOrder ? '（作废）' : ''
                        }`
                    }
                })
            );
            window.open(routeUrl.href, '_blank');
        },

        /**
         * 导出excel
         */
        handleExportExcel() {
            let data = this.handlePreparePostData(false);
            let targetOrder = this.targetOrder || {};
            data = { ...targetOrder, ...data };
            let typeList = [purchaseGrnOrderProductTypeMap.NORMAL.text, purchaseGrnOrderProductTypeMap.GIVEAWAY.text];
            const tableData = data.details.map((item) => ({
                ...item,
                unit_inner_sn: item.unit_name,
                money: new Big(item.quantity).times(item.unit_price).toNumber(),
                rate: `${new Big(item.rate).times(100).toNumber()}%`,
                mdrf_inner_sn: item.mdrf_sn,
                type: typeList[parseInt(item.type)]
            }));
            const fromData = {
                // 导出表单信息
                title: `${this.organizationNameLogined}${ORDER_NAME}${this.isRedOrder ? '（红冲）' : ''}${
                    this.isCancelOrder ? '（作废）' : ''
                }`,
                desc: {
                    label: '备注',
                    value: data.description
                },
                groups: [
                    [
                        {
                            label: '单据编号',
                            value: data.grn_sn
                        },
                        {
                            label: '单位名称',
                            value: data.organization_name
                        },
                        {
                            label: '仓库',
                            value: data.warehouse_name
                        },
                        {
                            label: '部门',
                            value: data.department_name
                        },
                        {
                            label: '单据类别',
                            value: data.type_name
                        }
                    ],
                    [
                        {
                            label: '采购员',
                            value: data.buyer_name
                        },
                        {
                            label: '经手人',
                            value: data.handler_name
                        },
                        {
                            label: '仓库人员',
                            value: data.warehouse_operator_name
                        },
                        {
                            label: '制单人',
                            value: data.making_people_name
                        },
                        {
                            label: '审核人',
                            value: data.auditor_name
                        }
                    ]
                ]
            };

            this.$Utils.exportExcel(productColumns, tableData, [], fromData);
        },

        /**
         * 生成提交数据
         * @param {boolean} dataCheck 是否需要校验数据
         */
        handlePreparePostData(dataCheck = true) {
            const orgId = this.$cookie.get('userbelong');
            const data = {
                billsType: this.dataEditing.billsType,
                buyer_name: this.dataEditing.buyerName,
                department_inner_sn: this.dataEditing.departmentInnerSn,
                department_name: this.dataEditing.departmentName,
                description: this.dataEditing.description,
                grn_sn: this.dataEditing.grnSn,
                handler_inner_sn: this.dataEditing.handlerInnerSn,
                handler_name: this.dataEditing.handlerName,
                org_id: orgId,
                organization_inner_sn: this.dataEditing.organizationInnerSn,
                organization_name: this.dataEditing.organizationName,
                status: this.dataEditing.status,
                warehouse_inner_sn: this.dataEditing.warehouseInnerSn,
                warehouse_name: this.dataEditing.warehouseName,
                warehouse_operator_inner_sn: this.dataEditing.warehouseOperatorInnerSn,
                warehouse_operator_name: this.dataEditing.warehouseOperatorName,
                details:
                    this.dataEditingDetails &&
                    this.dataEditingDetails
                        .filter((product) => product.rowMark !== 'inputerRow')
                        .map((product, index) => {
                            product.sort_number = index;
                            return this.$XEUtils.pick(product, [
                                'action',
                                'batch_number',
                                'codes',
                                'detail_inner_sn',
                                'grn_inner_sn',
                                'hasBarcode',
                                'health_care_sn',
                                'inspection_detail_id',
                                'mdrf_inner_sn',
                                'mdrf_sn',
                                'org_id',
                                'overdue_date',
                                'produce_date',
                                'product_inner_sn',
                                'product_name',
                                'product_type',
                                'quantity',
                                'quantity_old',
                                'rate',
                                'sort_number',
                                'specification',
                                'specification_inner_sn',
                                'type',
                                'unit_inner_sn',
                                'unit_name',
                                'unit_price',
                                'product_vendor_inner_sn',
                                'product_vendor_name',
                                'voucher_detail_inner_sn_c',
                                'voucher_inner_sn_c',
                                'voucher_type_c'
                            ]);
                        })
            };
            if (dataCheck) {
                this.handleCheckData(data);
            }
            return data;
        },

        handleSelectProducts() {
            if (this.isReadonly) {
                return;
            }
            this.handleOpenModals(modalsKeyMap.PRODUCT_SELECTER);
        },

        /**
         * 复制表格内的一个产品
         * @param {number} index
         */
        handleCopyProduct(index) {
            const newProduct = { ...this.dataEditingDetails[index], action: purchaseGrnOrderDetailActionTypeMap.ADD };
            delete newProduct._XID;
            delete newProduct.detail_inner_sn;
            this.dataEditingDetails.splice(index + 1, 0, newProduct);
        },

        /**
         * 移除表格内的一条记录
         * @param {number} index
         */
        handleRemoveProduct(index) {
            const removed = this.dataEditingDetails.splice(index, 1);
            if (removed[0] && removed[0].action === purchaseGrnOrderDetailActionTypeMap.EDIT) {
                removed[0].action = purchaseGrnOrderDetailActionTypeMap.DELETE;
                this.dataRemovedDetails.push(removed[0]);
            }
        },

        /**
         * 计算合计金额
         */
        handleCalculateSummation() {
            this.dataEditingDetailsMoneySummation = this.dataEditingDetails
                .reduce((prev, current) => {
                    if (current.rowMark === 'inputerRow') {
                        return prev;
                    }
                    return prev.plus(current.money);
                }, new Big(0))
                .toFixed(2);
            // 刷新表格以更新合计栏信息
            if (this.$refs.xTable && this.$refs.xTable.updateFooter) {
                this.$refs.xTable.updateFooter();
            }
        },

        /**
         * 加载采购入库单详情
         * @param {string | number} grnInnerSn 收货单id
         * @param {string} billsType 单据类型
         */
        async loadOrderDetail(grnInnerSn, billsType) {
            const userBelong = this.$cookie.get('userbelong');
            try {
                this.detailLoading = true;
                const { code, msg, info } = await purchaseGrnInfoAPI({
                    params: {
                        billsType,
                        grn_inner_sn: grnInnerSn,
                        org_id: userBelong
                    }
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.handleInit(info);
            } catch (err) {
                this.$message.error(`${ORDER_NAME}详情获取失败`);
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
                const { code, info, msg } = await purchaseGrnProductListBarcodeAPI({ barcode });
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
         * 创建采购入库单
         */
        async addOrder(data) {
            try {
                const { code, info, msg } = await purchaseGrnAddAPI(data);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success(`${ORDER_NAME}创建成功`);
                return info;
            } catch (err) {
                this.$message.error(`${ORDER_NAME}创建失败`);
                throw err;
            }
        },

        /**
         * 编辑采购入库单
         */
        async editOrder(data) {
            try {
                const { code, info, msg } = await purchaseGrnEditAPI(data);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success(`${ORDER_NAME}保存成功`);
                return info;
            } catch (err) {
                this.$message.error(`${ORDER_NAME}保存失败`);
                throw err;
            }
        },

        /**
         * 审核
         */
        async approvalOrder(data) {
            try {
                const { code, info, msg } = await purchaseGrnEditAPI(data);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success(`${ORDER_NAME}审核成功`);
                return info;
            } catch (err) {
                this.$message.error(`${ORDER_NAME}审核失败`);
                throw err;
            }
        }
    }
};
</script>

<style lang="less" scoped>
.editor-form-header-wrapper {
    display: flex;
    flex-wrap: nowrap;
    align-items: center;
    justify-content: space-between;
    align-items: center;
}
.editor-form-header-item {
    display: flex;
    flex: 1;
    flex-wrap: nowrap;
    margin-right: 24px;
    &:last-child {
        margin-right: 0;
    }
    .label {
        white-space: nowrap;
    }
    .atn-input,
    .ant-select {
        width: auto;
    }
}
.editor-form-footer-wrapper {
    height: 24px;
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
    align-items: center;
}
.editor-form-footer-item {
    display: flex;
    flex-wrap: nowrap;
    justify-content: flex-start;
    align-items: center;
    flex-grow: 0;
    margin-right: 32px;
    &:last-child {
        margin-right: 0;
    }
    > .ant-input,
    > .ant-select {
        width: 95px;
    }
}
.editor-form-footer-full-wrapper {
    height: 24px;
    display: flex;
    flex-wrap: nowrap;
    justify-content: flex-start;
    align-items: center;
    > .ant-input {
        width: auto;
        flex-grow: 1;
    }
}
</style>
