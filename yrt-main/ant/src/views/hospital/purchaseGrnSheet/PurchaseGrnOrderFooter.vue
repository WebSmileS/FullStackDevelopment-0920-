<template>
    <div>
        <a-row style="margin-top: 10px">
            <a-col :span="18" style="text-align: left">
                <a-col :span="6" style="padding-right: 20px">
                    <a-input
                        size="small"
                        v-model="dataEditing.buyerName"
                        placeholder="采购员"
                    ></a-input>
                </a-col>
                <a-col :span="6" style="padding-right: 20px">
                    <a-select
                        labelInValue
                        showSearch
                        :options="employeesData"
                        :value="handlerSelecterValue"
                        @change="onHandlerChange"
                        placeholder="经手人 *"
                    ></a-select>
                </a-col>
                <a-col :span="6" style="padding-right: 20px">
                    <a-select
                        labelInValue
                        showSearch
                        :options="warehouseOperatorOptions"
                        :value="warehouseOperatorSelecterValue"
                        @change="onWarehouseOperatorChange"
                        placeholder="仓库人员"
                    ></a-select>
                </a-col>
                <a-col :span="6" style="padding-right: 20px">
                    <a-input
                        size="small"
                        v-model="dataEditing.use_department"
                        placeholder="使用科室"
                    ></a-input>
                </a-col>
            </a-col>
            <a-col :span="6" style="text-align: left">
                <a-col :span="12" style="padding-right: 10px;">
                    <span class="label" style="width: auto; padding-right: 10px">制单人：&nbsp;</span>
                    <span>{{ dataEditing.makingPeopleName || '-' }}</span>
                </a-col>
                <a-col :span="12" style="padding-left: 10px;">
                    <span class="label" style="width: auto; padding-right: 10px">审核人：&nbsp;</span>
                    <span>{{ dataEditing.auditorName || '-' }}</span>
                </a-col>
            </a-col>
        </a-row>
        <a-row style="margin-top: 5px">
            <a-col :span="24">
                <a-input
                    size="small"
                    v-model="dataEditing.description"
                    placeholder="备注"
                ></a-input>
            </a-col>
            <a-col :span="12" style="text-align: left; margin-top: 10px">
                <a-button
                    v-if="isPutinOrder || isRedOrder || isCancelOrder"
                    type="default"
                    @click="handlePrint"
                    >打印</a-button
                >
                <a-tooltip
                    v-else
                    placement="top"
                    title="已入库、红冲和作废状态的单据才可以打印"
                >
                    <a-button disabled type="default">打印</a-button>
                </a-tooltip>
                <a-button
                    style="margin-left: 5px"
                    type="default"
                    @click="handleExportExcel"
                >导出Excel</a-button>
            </a-col>
            <a-col :span="12" style="margin-top: 10px; text-align: right">
                <a-button 
                    v-if="isPutinOrder || isCancelOrder || isRedOrder"
                    type="default"
                    @click="handleClose"
                >关闭</a-button>
                <a-button
                    v-else
                    type="default"
                    @click="handleClose"
                >取消</a-button>
                <a-button
                    v-if="!isReadonly"
                    type="primary"
                    @click="onSaveButtonClick"
                    :loading="buttonsLoading.save"
                    style="margin-left: 5px"
                >保存</a-button>
                <a-button
                    v-if="isFormalOrder"
                    type="primary"
                    @click="onApprovalButtonClick"
                    :loading="buttonsLoading.approval"
                    style="margin-left: 5px"
                    >审核</a-button
                >
                <a-button
                    v-if="isApprovaledOrder"
                    type="primary"
                    @click="onPutinButtonClick"
                    :loading="buttonsLoading.save"
                    style="margin-left: 5px"
                >入库</a-button>
                <a-button
                    v-if="isPutinOrder"
                    type="danger"
                    @click="onRedButtonClick"
                    :loading="buttonsLoading.red"
                    style="margin-left: 5px"
                >生成红冲</a-button>
            </a-col>
        </a-row>
    </div>
</template>

<script>
import Big from 'big.js';
import BaseModal from '@/components/BaseModal';
import ProductSelecterModal from '@/components/ProductSelecterModal';
import TableRowOperator from '@/components/TableRowOperator';
import OrganizationSelecter from '@/components/OrganizationSelecter';
import textNullDataFilter from '@/filters/textNullData';
import BaseInputWithClearButton from '@/components/BaseInputWithClearButton';
import ProductSelecterVendor from '@/components/ProductSelecterVendor';
import {
    grnPutinAPI,
    grnRedDashedAPI,
    purchaseGrnAddAPI,
    purchaseGrnEditAPI,
    purchaseGrnInfoAPI
} from '@/service/pageAjax';
import tableColumnConfig from '@/config/tableColumnConfig';
import modalsMixin from '@/mixins/modals';
import productUnitOptionsMixin from '@/mixins/productUnitOptions';
import mdrfOptionsMixin from '@/mixins/mdrfOptions';
import departmentTreeMixin from '@/mixins/departmentTree';
import employeesMixin from '@/mixins/employees';
import warehouseOperatorOptionsMixin from '@/mixins/warehouseOperatorOptions';
import { orderTypeMap } from '@/config/constants';
import ProductSelecterTransferOrder from './ProductSelecterTransferOrder';
import productColumns from './purchaseGrnOrderEditorColumns';
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
        ProductSelecterVendor,
        ProductSelecterTransferOrder,
        OrganizationSelecter,
        TableRowOperator,
        BaseInputWithClearButton
    },
    mixins: [
        modalsMixin,
        productUnitOptionsMixin,
        mdrfOptionsMixin,
        departmentTreeMixin,
        employeesMixin,
        warehouseOperatorOptionsMixin
    ],
    filters: {
        textNullDataFilter
    },
    props: {
        visible: {
            type: Boolean,
            default: true
        },
        targetOrder: {
            type: Object,
            default: null
        },
        targetOrderIsCopy: {
            type: Boolean,
            default: false
        },
        data: {
            type: Array,
            default: () => []
        },
        dataInfo: {
            type: Object,
            default: () => {}
        }
    },
    data() {
        return {
            tableColumnConfig,
            modalsKeyMap,
            orderName: ORDER_NAME,
            productColumns: Object.freeze(productColumns),
            departmentTree: null,
            warehouseOperatorOptions: null,
            dataEditing: [],
            dataEditingDetails: [],
            dataRemovedDetails: [],
            dataEditingDetailsMoneySummation: 0,
            dataEditingDetailsSalesPriceSummation: 0,
            detailLoading: false,
            buttonsLoading: {
                save: false,
                approval: false,
                putin: false,
                red: false
            },
            needAutoOpenProductSelecter: false,
            currentTransferOrder: undefined
        };
    },
    computed: {
        productSelecterTabs() {
            if (this.isTransferOrder) {
                return [{ title: '调单产品', key: 'transfer' }];
            }
            return [{ title: '厂商产品', key: 'vendor' }];
        },
        purchaseGrnOrderDetailActionTypeMap() {
            return purchaseGrnOrderDetailActionTypeMap;
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
            return (
                this.visible &&
                (this.targetOrder === null ||
                    this.targetOrderIsCopy ||
                    (this.currentTransferOrder && this.currentTransferOrder._from === 'transfer'))
            );
        },

        /**
         * 是否为调单入库
         */
        isTransferOrder() {
            return this.visible && !!this.currentTransferOrder;
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
            if (this.isTransferOrder) {
                return '调单';
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
        }
    },
    mounted() {
        // 打开时初始化数据（请保证数据初始化逻辑处于最优先顺序）
        if (this.targetOrder) {
            // 调单入库
            if (this.targetOrder._from === 'transfer') {
                this.currentTransferOrder = this.targetOrder;
                // this.handleInitTransferOrder(this.currentTransferOrder);
                // this.handleSetDefaultInfo();
            } else {
                // 编辑、复制
                this.loadOrderDetail(this.targetOrder.grn_inner_sn, this.targetOrder.billsType);
            }
        } else {
            // 新建
            // this.handleInit();
            // this.handleSetDefaultInfo();
        }

        // 打开时，若人员列表未加载，加载人员列表
        this.handleInitEmployeesMixin();

        // 打开时，若仓库已选，加载仓库人员
        if (
            this.targetOrder &&
            (this.targetOrder.warehouse_inner_sn || this.targetOrder.warehouse_inner_sn === 0)
        ) {
            this.loadWarehouseOperatorOptions(this.targetOrder.warehouse_inner_sn);
        }
    },
    watch: {
        dataInfo: {
            handler(obj, old){
                if (obj.warehouseInnerSn !== old.warehouseInnerSn) {
                    this.loadWarehouseOperatorOptions(obj.warehouseInnerSn);
                }
                this.dataEditing = obj;
            },
            deep: true
        },
        dataEditingDetails() {
            // 重新计算合计值
            this.handleCalculateSummation();
        }
    },
    methods: {
        tableFooterMethod({ columns }) {
            let result = [];
            result[0] = '合计';
            columns.forEach((column, index) => {
                if (column.property === 'money') {
                    result[index] = this.dataEditingDetailsMoneySummation;
                    return;
                }
                if (column.property === 'total_sales_price') {
                    result[index] = this.dataEditingDetailsSalesPriceSummation;
                }
            });
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

                // 新增单据时、初次调单时
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
            switch (column.property) {
                // 单价或数量更新时，自动计算金额
                case 'unit_price':
                case 'quantity':
                    row.unit_price = new Big(row.unit_price).round(4).toNumber();
                    row.quantity = new Big(row.quantity).round(4).toNumber();
                    if (Number(row.unit_price) === 0) {
                        row.money = 0;
                        row.percent = 0;
                        row.sales_price = 0;
                        row.total_sales_price = 0;
                    } else {
                        row.money = new Big(row.quantity).times(row.unit_price).round(4).toNumber();
                        row.sales_price = new Big(row.percent)
                            .plus(100)
                            .div(100)
                            .times(row.unit_price)
                            .round(4)
                            .toNumber();
                        row.total_sales_price = new Big(row.sales_price).times(row.quantity).round(4).toNumber();
                    }
                    this.handleCalculateSummation();
                    break;
                // 金额更新时，自动计算单价
                case 'money':
                    row.money = new Big(row.money).round(4).toNumber();
                    row.unit_price = new Big(row.money).div(row.quantity).round(4).toNumber();
                    row.sales_price = new Big(row.percent).plus(100).div(100).times(row.unit_price).round(4).toNumber();
                    row.total_sales_price = new Big(row.sales_price).times(row.quantity).round(4).toNumber();
                    this.handleCalculateSummation();
                    break;
                // 溢价比例更新时
                case 'percent':
                    row.percent = new Big(row.percent).round(4).toNumber();
                    if (Number(row.unit_price) === 0) {
                        row.money = 0;
                        row.percent = 0;
                        this.$message.warning('含税单价为零时，不能设置溢价比例');
                    } else {
                        row.sales_price = new Big(row.percent)
                            .plus(100)
                            .div(100)
                            .times(row.unit_price)
                            .round(4)
                            .toNumber();
                        row.total_sales_price = new Big(row.sales_price).times(row.quantity).round(4).toNumber();
                    }
                    this.handleCalculateSummation();
                    break;
                // 售价更新时
                case 'sales_price':
                    row.sales_price = new Big(row.sales_price).round(4).toNumber();
                    row.total_sales_price = new Big(row.sales_price).times(row.quantity).round(4).toNumber();
                    if (Number(row.unit_price) === 0) {
                        row.money = 0;
                        row.percent = 0;
                        this.$message.warning('注意：当前含税单价为零');
                    } else {
                        row.percent = new Big(row.sales_price)
                            .minus(row.unit_price)
                            .div(row.unit_price)
                            .times(100)
                            .round(4)
                            .toNumber();
                    }
                    this.handleCalculateSummation();
                    break;
                // 合计售价更新时
                case 'total_sales_price':
                    row.total_sales_price = new Big(row.total_sales_price).round(4).toNumber();
                    row.sales_price = new Big(row.total_sales_price).div(row.quantity).round(4).toNumber();
                    if (Number(row.unit_price) === 0) {
                        row.money = 0;
                        row.percent = 0;
                        this.$message.warning('注意：当前含税单价为零');
                    } else {
                        row.percent = new Big(row.sales_price)
                            .minus(row.unit_price)
                            .div(row.unit_price)
                            .times(100)
                            .round(4)
                            .toNumber();
                    }
                    this.handleCalculateSummation();
                    break;
                default:
            }
        },

        onOrganizationSelected(organizations) {
            if (
                organizations &&
                organizations.length > 0 &&
                (organizations[0].organizationInnerSn !== this.dataEditing.organizationInnerSn ||
                    organizations[0].billsType !== this.dataEditing.billsType)
            ) {
                this.dataEditing.organizationName = organizations[0].organizationName;
                this.dataEditing.organizationInnerSn = organizations[0].organizationInnerSn;
                this.dataEditing.billsType = organizations[0].billsType;
                // 若更改了相关单位，移除产品
                if (this.dataEditingDetails.length > 0) {
                    this.dataEditingDetails = [];
                    this.$message.warning(`由于更改相关单位，相关产品已移除`);
                }
            }
            this.handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER);
            if (this.needAutoOpenProductSelecter) {
                this.needAutoOpenProductSelecter = false;
                this.handleOpenModals(modalsKeyMap.PRODUCT_SELECTER);
            }
        },

        onOrganizationClear() {
            this.dataEditing.organizationName = undefined;
            this.dataEditing.organizationInnerSn = undefined;
            this.dataEditing.billsType = undefined;
            // 更改了供货单位，移除相关的产品，平台产品则会保留
            if (this.dataEditingDetails.length > 0) {
                this.dataEditingDetails = [];
                this.$message.warning(`由于更改供货单位，相关产品已移除`);
            }
        },

        onWarehouseChange(value = {}) {
            if (this.dataEditing.warehouseName === value.label && this.dataEditing.warehouseInnerSn === value.key) {
                return;
            }
            // 更新值
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

        /**
         * 产品选择返回值时
         * @param {object} data 产品数据
         * @param {array | undefined} data.purchaseGrnOrderProducts 可用产品
         * @param {array | undefined} data.transferOrderProducts 调单产品
         */
        onProductAdd(data) {
            // 采购入库单可用产品
            if (data.purchaseGrnOrderProducts) {
                let i = 0;
                while (i < data.purchaseGrnOrderProducts.length) {
                    let t = data.purchaseGrnOrderProducts[i];
                    i += 1;
                    // 将可用的产品添加到列表
                    let unitOptions;
                    let mdrfOptions;
                    if (t.min_unit_inner_sn) {
                        unitOptions = [
                            {
                                value: t.min_unit_inner_sn,
                                label: t.min_unit
                            }
                        ];
                    }
                    if (t.mdrf_inner_sn) {
                        mdrfOptions = [
                            {
                                value: t.mdrf_inner_sn,
                                label: t.mdrf_sn
                            }
                        ];
                    }
                    this.dataEditingDetails.push({
                        action: purchaseGrnOrderDetailActionTypeMap.ADD,
                        batch_number: undefined,
                        detail_inner_sn: undefined,
                        health_care_sn: undefined,
                        inspection_detail_id: undefined,
                        mdrf_inner_sn: t.mdrf_inner_sn ? t.mdrf_inner_sn : undefined,
                        mdrf_sn: t.mdrf_inner_sn ? t.mdrf_sn : undefined,
                        produce_date: undefined,
                        overdue_date: undefined,
                        product_inner_sn: t.product_inner_sn,
                        product_name: t.product_name,
                        product_type: t.product_type,
                        product_vendor_inner_sn: t.product_vendor_inner_sn || t.vendor_inner_sn,
                        product_vendor_name: t.product_vendor_name || t.vendor_name,
                        quantity: 1,
                        quantity_old: undefined,
                        rate: 0,
                        specification: t.specification,
                        specification_inner_sn: t.specification_inner_sn,
                        type: 0,
                        unit_inner_sn: t.min_unit_inner_sn,
                        unit_name: t.min_unit,
                        unit_price: 0,
                        money: 0,
                        percent: 0,
                        sales_price: 0,
                        total_sales_price: 0,
                        vendor_name: t.vendor_name,
                        vendor_inner_sn: t.vendor_inner_sn,
                        voucher_type_c: orderTypeMap.NONE.key,
                        options: {
                            unit_inner_sn: unitOptions,
                            mdrf_inner_sn: mdrfOptions
                        },
                        optionsLoading: { unit_inner_sn: false, mdrf_inner_sn: false },
                        optionsLoaded: { unit_inner_sn: false, mdrf_inner_sn: false }
                    });
                }
            }
            if (data.transferOrderProducts) {
                this.handleParseTransferOrderProducts(data.transferOrderProducts).forEach((product) => {
                    for (let i = 0, l = this.dataEditingDetails.length; i < l; i += 1) {
                        if (
                            this.dataEditingDetails[i].action === purchaseGrnOrderDetailActionTypeMap.ADD &&
                            this.dataEditingDetails[i].voucher_detail_inner_sn_c === product.voucher_detail_inner_sn_c
                        ) {
                            this.$message.warning(`请勿添加重复的产品（NO.${i + 1} ${product.product_name}）`);
                            return;
                        }
                    }
                    this.dataEditingDetails.push(product);
                });
            }
            this.handleCloseModals(modalsKeyMap.PRODUCT_SELECTER);
            this.$nextTick(() => {
                this.$refs.xTable.scrollToRow(this.dataEditingDetails[this.dataEditingDetails.length - 1]);
            });
        },
        
        handleParseTransferOrderProducts(products) {
            return products.map((product) => {
                let unitOptions;
                let mdrfOptions;
                if (product.unit_inner_sn || product.unit_inner_sn === 0) {
                    unitOptions = [{ value: product.unit_inner_sn, label: product.unit }];
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
                    unit_name: product.unit,
                    money: new Big(product.unit_price).times(product.quantity).toNumber(),
                    total_sales_price: new Big(product.sales_price).times(product.quantity).toNumber(),
                    action: purchaseGrnOrderDetailActionTypeMap.ADD,
                    quantity_old: product.quantity,
                    type: purchaseGrnOrderProductTypeMap.NORMAL.value,
                    voucher_detail_inner_sn_c: product.detail_inner_sn,
                    voucher_inner_sn_c: product.voucher_inner_sn,
                    voucher_type_c: orderTypeMap.ODO.key,
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
        },
        handleInitTransferOrder(targetOrder) {
            this.dataEditing.auditorInnerSn = undefined;
            this.dataEditing.auditorName = undefined;
            this.dataEditing.billsType = targetOrder.billsType;
            this.dataEditing.buyerName = undefined;
            this.dataEditing.createTime = undefined;
            this.dataEditing.departmentInnerSn = undefined;
            this.dataEditing.departmentName = undefined;
            this.dataEditing.description = undefined;
            this.dataEditing.grnInnerSn = undefined;
            this.dataEditing.grnSn = undefined;
            this.dataEditing.handlerInnerSn = undefined;
            this.dataEditing.handlerName = undefined;
            this.dataEditing.inTime = undefined;
            this.dataEditing.makingPeopleInnerSn = undefined;
            this.dataEditing.makingPeopleName = undefined;
            this.dataEditing.orgId = targetOrder.org_id;
            this.dataEditing.orgName = targetOrder.org_name;
            this.dataEditing.organizationInnerSn = targetOrder.organization_inner_sn;
            this.dataEditing.organizationName = targetOrder.organization_name;
            this.dataEditing.use_department = targetOrder.use_department;
            this.dataEditing.organizationType = targetOrder.organization_type;
            this.dataEditing.status = purchaseGrnOrderStatusMap.FORMAL.value;
            this.dataEditing.typeInnerSn = targetOrder.type_inner_sn;
            this.dataEditing.typeName = targetOrder.type_name;
            this.dataEditing.voucherInnerSnC = undefined;
            this.dataEditing.voucherTypeC = undefined;
            this.dataEditing.warehouseInnerSn = undefined;
            this.dataEditing.warehouseName = undefined;
            this.dataEditing.warehouseOperatorInnerSn = undefined;
            this.dataEditing.warehouseOperatorName = undefined;
            this.dataEditingDetails = this.handleParseTransferOrderProducts(targetOrder.details);
        },

        /**
         * 关闭编辑器
         */
        handleClose() {
            this.$emit('close');
        },

        /**
         * 数据校验
         */
        handleCheckData(data) {
            try {
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
                    throw new Error('请选择供货单位');
                }
                // 仓库校验
                if ((!data.warehouse_inner_sn && data.warehouse_inner_sn !== 0) || !data.warehouse_name) {
                    throw new Error('请选择仓库');
                }
                // 部门校验
                if ((!data.department_inner_sn && data.department_inner_sn !== 0) || !data.department_name) {
                    throw new Error('请选择部门');
                }
                // 经手人校验
                if ((!data.handler_inner_sn && data.handler_inner_sn !== 0) || !data.handler_name) {
                    throw new Error('请选择经手人');
                }
                // 产品数据校验
                if (!data.details.length) {
                    throw new Error('产品不能为空');
                }
                data.details.forEach((product, index) => {
                    if ((!product.unit_inner_sn && product.unit_inner_sn !== 0) || !product.unit_name) {
                        throw new Error(`产品单位不能为空（NO.${index + 1} ${product.product_name}）`);
                    }
                    if (!(Number(product.quantity) > 0)) {
                        throw new Error(`产品数量必须大于0（NO.${index + 1} ${product.product_name}）`);
                    }
                    // 调单产品校验最大数量
                    if (product.voucher_detail_inner_sn_c) {
                        if (product.quantity > product.quantity_old) {
                            throw new Error(
                                `产品数量（${product.quantity}）不能大于当前可调单数量（${product.quantity_old}）（NO.${
                                    index + 1
                                } ${product.product_name}）`
                            );
                        }
                    }
                    if (!(Number(product.unit_price) >= 0)) {
                        throw new Error(`含税单价不能小于0（NO.${index + 1} ${product.product_name}）`);
                    }
                    if (!(Number(product.percent) >= 0)) {
                        throw new Error(`溢价比例不能小于0（NO.${index + 1} ${product.product_name}）`);
                    }
                    if (!(Number(product.sales_price) >= 0)) {
                        throw new Error(`售价不能小于0（NO.${index + 1} ${product.product_name}）`);
                    }
                    if (product.overdue_date && new Date(product.overdue_date) <= new Date()) {
                        throw new Error(`失效日期只能晚于当前时间（NO.${index + 1} ${product.product_name}）`);
                    }
                    if (
                        product.produce_date &&
                        product.overdue_date &&
                        new Date(product.produce_date) >= new Date(product.overdue_date)
                    ) {
                        throw new Error(`生产日期应该早于失效日期（NO.${index + 1} ${product.product_name}）`);
                    }
                });
            } catch (err) {
                this.$message.error(err.message);
                throw err;
            }
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

        getStableData() {
            let data = [];
            this.data.forEach((item) => {
                if (item.product_vendor_name !== '') {
                    data.push(item);
                }
            });
            return data;
        },
        /**
         * 生成提交数据
         * @param {boolean} dataCheck 是否需要校验数据
         */
        handlePreparePostData(dataCheck = true) {
            const orgId = this.$cookie.get('userbelong');
            let details = this.getStableData();
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
                use_department: this.dataEditing.use_department,
                status: this.dataEditing.status,
                warehouse_inner_sn: this.dataEditing.warehouseInnerSn,
                warehouse_name: this.dataEditing.warehouseName,
                warehouse_operator_inner_sn: this.dataEditing.warehouseOperatorInnerSn,
                warehouse_operator_name: this.dataEditing.warehouseOperatorName,
                details:
                    details &&
                    details.map((product, index) => {
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
                            'percent',
                            'sales_price',
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

        /**
         * 复制表格内的一个产品
         * @param {number} index
         */
        handleCopyProduct(index) {
            if (this.dataEditingDetails[index].voucher_detail_inner_sn_c) {
                this.$message.warning('调单产品不能复制');
                return;
            }
            const newProduct = { ...this.dataEditingDetails[index], action: purchaseGrnOrderDetailActionTypeMap.ADD };
            delete newProduct.detail_inner_sn;
            delete newProduct._XID;
            this.dataEditingDetails.push(newProduct);
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
         * 检测单元格是否可编辑
         * @param {object} data
         * @param {object} data.row 行数据
         * @param {object} data.column 列信息
         */
        handleCheckCellEditable(data) {
            if (this.isReadonly) {
                return false;
            }
            // 来自单据的可用产品，单位不可编辑
            if (
                data.column.property === 'unit_inner_sn' &&
                (data.row.voucher_inner_sn_c || data.row.voucher_inner_sn_c === 0)
            ) {
                return false;
            }
            return true;
        },

        /**
         * 计算合计金额
         */
        handleCalculateSummation() {
            let moneySummation = new Big(0);
            let sales_priceSummation = new Big(0);
            this.dataEditingDetails.forEach((product) => {
                moneySummation = moneySummation.plus(product.money);
                sales_priceSummation = sales_priceSummation.plus(product.total_sales_price);
            });
            this.dataEditingDetailsMoneySummation = moneySummation.toFixed(2);
            this.dataEditingDetailsSalesPriceSummation = sales_priceSummation.toFixed(2);
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
                // this.handleInit(info);
                // 根据单据详情里的明细推断出当前的采购入库单是否为通过调单创建的
                for (let i = 0, l = info.details.length; i < l; i += 1) {
                    if (info.details[i].voucher_type_c !== orderTypeMap.NONE.key) {
                        this.currentTransferOrder = {
                            billsType: info.billsType,
                            organization_inner_sn: info.organization_inner_sn,
                            organization_name: info.organization_name,
                            voucher_inner_sn_c: info.details[i].voucher_inner_sn_c
                        };
                        break;
                    }
                }
            } catch (err) {
                this.$message.error(`${ORDER_NAME}详情获取失败`);
                throw err;
            } finally {
                this.detailLoading = false;
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

</style>
