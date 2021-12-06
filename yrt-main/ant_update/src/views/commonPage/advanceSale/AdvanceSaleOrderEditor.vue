<template>
    <base-modal
        dragKey="advance_sale_order_editor_md"
        fullscreenUseable
        :destroyOnClose="true"
        :title="`${isAddOrder ? '新增' : ''}${orderName}`"
        :width="1100"
        :visible="visible"
        @close="handleClose"
    >
        <template v-slot:content="{ fullscreen }">
            <div style="position: relative">
                <span v-show="!isAddOrder" style="position: absolute; right: 0"
                    >单据编号：{{ dataEditing.advanceSaleNo }}</span
                >
                <h6 style="margin: 24px 0; text-align: center; font-size: 24px; font-weight: 500">
                    <span v-if="organizationNameLogined">{{ organizationNameLogined }}&nbsp;&nbsp;</span>
                    <span>{{ orderName }}</span>
                </h6>
            </div>
            <editor-form-header>
                <editor-form-header-item>
                    <span class="label"><span v-if="!isReadonly" class="red">*</span>收货单位：</span>
                    <span v-if="isReadonly">{{ dataEditing.buyerName }}</span>
                    <base-input-with-clear-button
                        v-else
                        size="small"
                        :value="dataEditing.buyerName"
                        @click="handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                        @clear="onOrganizationClear"
                    ></base-input-with-clear-button>
                </editor-form-header-item>
                <editor-form-header-item>
                    <span class="label"><span v-if="!isReadonly" class="red">*</span>仓库：</span>
                    <span v-if="isReadonly">{{ dataEditing.warehouseName }}</span>
                    <warehouse-selecter-of-user-org
                        v-else
                        allowClear
                        :value="
                            dataEditing.warehouseId && { id: dataEditing.warehouseId, name: dataEditing.warehouseName }
                        "
                        @change="onWarehouseChange"
                    ></warehouse-selecter-of-user-org>
                </editor-form-header-item>
                <editor-form-header-item>
                    <span class="label"><span v-if="!isReadonly" class="red">*</span>部门：</span>
                    <span v-if="isReadonly">{{ dataEditing.saleDepartmentName }}</span>
                    <department-selecter-of-user-org
                        v-else
                        ref="department"
                        :value="
                            dataEditing.saleDepartmentId && {
                                id: dataEditing.saleDepartmentId,
                                name: dataEditing.saleDepartmentName
                            }
                        "
                        @change="onDepartmentChange"
                    ></department-selecter-of-user-org>
                </editor-form-header-item>
                <editor-form-header-item>
                    <span class="label">期望到货日期：</span>
                    <span v-if="isReadonly">
                        {{ readonlyExpectAogDate || '--' }}
                    </span>
                    <a-date-picker
                        v-else
                        size="small"
                        format="YYYY-MM-DD HH:mm:ss"
                        v-model="dataEditing.expectAogDate"
                        :showTime="{ format: 'HH:mm:ss' }"
                    ></a-date-picker>
                </editor-form-header-item>
            </editor-form-header>
            <div style="margin-top: 24px" @dblclick="handleSelectProducts">
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
                        'quantity': [{ min: 0.00001, type: 'number', message: '数量必须大于0' }]
                    }"
                    :footer-method="() => [[]]"
                    @cell-dblclick="({ $event }) => $event.stopPropagation()"
                    @edit-actived="onEditActived"
                >
                    <template slot="empty">
                        <div>双击空白区域添加产品</div>
                    </template>
                    <vxe-table-column v-bind="tableColumnConfig.seq" field="index" fixed="left"></vxe-table-column>
                    <vxe-table-column
                        v-for="column in productColumns"
                        v-bind="column"
                        :key="column.field"
                    ></vxe-table-column>
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
                        <template slot="footer">
                            <div style="overflow: hidden">
                                <a-button size="small" type="primary" @click="handleSelectProducts">添加产品</a-button>
                            </div>
                        </template>
                    </vxe-table-column>
                </vxe-table>
            </div>
            <editor-form-footer style="margin-top: 24px">
                <editor-form-footer-item>
                    <span class="label"><span v-if="!isReadonly" class="red">*</span>经手人：</span>
                    <span v-if="isReadonly">{{ dataEditing.agentUserName || '--' }}</span>
                    <employee-selecter-of-user-org
                        v-else
                        ref="employee"
                        :value="
                            dataEditing.agentUserId && { id: dataEditing.agentUserId, name: dataEditing.agentUserName }
                        "
                        @change="onHandlerChange"
                    ></employee-selecter-of-user-org>
                </editor-form-footer-item>
                <editor-form-footer-item>
                    <span class="label">仓库人员：</span>
                    <span v-if="isReadonly">{{ dataEditing.warehouseUserName || '--' }}</span>
                    <a-select
                        v-else
                        labelInValue
                        showSearch
                        :options="warehouseOperatorOptions"
                        :value="warehouseOperatorSelecterValue"
                        @change="onWarehouseOperatorChange"
                    ></a-select>
                </editor-form-footer-item>
                <editor-form-footer-item>
                    <span class="label">制单人：</span>
                    <span>{{ dataEditing.createUserName || '--' }}</span>
                </editor-form-footer-item>
                <editor-form-footer-item>
                    <span class="label">审核人：</span>
                    <span>{{ dataEditing.auditUserName || '--' }}</span>
                </editor-form-footer-item>
            </editor-form-footer>
            <editor-form-footer-row-item style="margin-top: 24px">
                <span class="label">备注：</span>
                <span v-if="isReadonly">{{ dataEditing.description }}</span>
                <a-input v-else size="small" v-model="dataEditing.description"></a-input>
            </editor-form-footer-row-item>
            <organization-selecter
                title="选择收货单位"
                destroyOnClose
                :visible="modalsVisible[modalsKeyMap.ORGANIZATION_SELECTER]"
                @close="handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                @submit="onOrganizationSelected"
            ></organization-selecter>
            <product-selecter-modal
                destroyOnClose
                :visible="modalsVisible[modalsKeyMap.PRODUCT_SELECTER]"
                :tabs="productSelecterTabs"
                @close="handleCloseModals(modalsKeyMap.PRODUCT_SELECTER)"
            >
                <product-selecter-advance-sale
                    slot="useable"
                    :billsType="dataEditing.billsType"
                    :organizationInnerSn="dataEditing.buyerId"
                    :warehouse="currentWarehouse"
                    @submit="onProductAdd"
                ></product-selecter-advance-sale>
            </product-selecter-modal>
            <product-batch-selecter
                title="选择批号"
                :visible="modalsVisible[modalsKeyMap.BATCH_SELECTER]"
                :billsType="batchSelecterTarget.billsType"
                :warehouseInnerSn="batchSelecterTarget.warehouseInnerSn"
                :productInnerSn="batchSelecterTarget.productInnerSn"
                :productType="batchSelecterTarget.productType"
                :productVendorInnerSn="batchSelecterTarget.productVendorInnerSn"
                :specificationInnerSn="batchSelecterTarget.specificationInnerSn"
                @close="handleCloseModals(modalsKeyMap.BATCH_SELECTER)"
                @submit="onBatchSelecterSubmit"
            ></product-batch-selecter>
        </template>
        <template slot="footer">
            <a-button v-if="isReadonly" type="default" @click="handleClose">关闭</a-button>
            <a-button v-else type="default" @click="handleClose">取消</a-button>
            <a-button v-if="!isReadonly" type="primary" @click="onSaveButtonClick" :loading="buttonsLoading.save"
                >保存</a-button
            >
        </template>
    </base-modal>
</template>

<script>
import Big from 'big.js';
import moment from 'moment';
import BaseModal from '@/components/BaseModal';
import TableRowOperator from '@/components/TableRowOperator';
import BaseInputWithClearButton from '@/components/BaseInputWithClearButton';
import ProductSelecterModal from '@/components/ProductSelecterModal';
import EditorFormHeader from '@/components/EditorFormHeader';
import EditorFormHeaderItem from '@/components/EditorFormHeaderItem';
import EditorFormFooter from '@/components/EditorFormFooter';
import EditorFormFooterItem from '@/components/EditorFormFooterItem';
import EditorFormFooterRowItem from '@/components/EditorFormFooterRowItem';
import ProductBatchSelecter from '@/components/ProductBatchSelecter';
import WarehouseSelecterOfUserOrg from '@/components/WarehouseSelecterOfUserOrg.js';
import DepartmentSelecterOfUserOrg from '@/components/DepartmentSelecterOfUserOrg.js';
import EmployeeSelecterOfUserOrg from '@/components/EmployeeSelecterOfUserOrg.js';
import textNullDataFilter from '@/filters/textNullData';
import { advanceSaleOrderAddAPI, advanceSaleOrderEditAPI, advanceSaleOrderInfoAPI } from '@/service/advanceSale';
import modalsMixin from '@/mixins/modals';
import productUnitOptionsMixin from '@/mixins/productUnitOptions';
import mdrfOptionsMixin from '@/mixins/mdrfOptions';
import warehouseOperatorOptionsMixin from '@/mixins/warehouseOperatorOptions';
import tableColumnConfig from '@/config/tableColumnConfig';
import ProductSelecterAdvanceSale from './ProductSelecterAdvanceSale';
import OrganizationSelecter from '@/components/OrganizationSelecter';
import productColumns from './advanceSaleOrderEditorColumns';
import { advanceSaleOrderAuditStatusMap, advanceSaleOrderProductTypeMap } from './type';

const DATE_TIME_FORMAT = 'YYYY-MM-DD HH:mm:ss';

const ORDER_NAME = '销售草稿';

const modalsKeyMap = Object.freeze({
    ORGANIZATION_SELECTER: 'o',
    PRODUCT_SELECTER: 'p',
    BATCH_SELECTER: 'b'
});

export default {
    name: 'AdvanceSaleOrderEditor',
    components: {
        BaseModal,
        ProductSelecterModal,
        ProductSelecterAdvanceSale,
        OrganizationSelecter,
        TableRowOperator,
        BaseInputWithClearButton,
        EditorFormHeader,
        EditorFormHeaderItem,
        EditorFormFooter,
        EditorFormFooterItem,
        EditorFormFooterRowItem,
        ProductBatchSelecter,
        WarehouseSelecterOfUserOrg,
        DepartmentSelecterOfUserOrg,
        EmployeeSelecterOfUserOrg
    },
    mixins: [modalsMixin, warehouseOperatorOptionsMixin, productUnitOptionsMixin, mdrfOptionsMixin],
    filters: {
        textNullDataFilter
    },
    /**
     * isFinance:
     * 是否为财务人员使用，财务人员使用时只有查看功能，
     * 并且列表内容为已审核单据
     */
    inject: ['isFinance'],
    props: {
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
            modalsKeyMap,
            departmentTree: null,
            warehouseOperatorOptions: null,
            dataEditing: {
                advanceSaleNo: undefined,
                billsType: undefined,
                buyerId: undefined,
                buyerName: undefined,
                agentUserId: undefined,
                agentUserName: undefined,
                saleDepartmentId: undefined,
                saleDepartmentName: undefined,
                warehouseId: undefined,
                warehouseName: undefined,
                warehouseUserId: undefined,
                warehouseUserName: undefined,
                description: undefined,
                expectAogDate: undefined,
                createUserName: undefined,
                auditUserName: undefined
            },
            dataEditingDetails: [],
            detailLoading: false,
            buttonsLoading: {
                save: false
            },
            needAutoOpenProductSelecter: false,
            batchSelecterTarget: {
                _XID: undefined, // 行id，批号选择完成后根据此id进行数据回填
                billsType: undefined,
                warehouseInnerSn: undefined,
                productInnerSn: undefined,
                productType: undefined,
                productVendorInnerSn: undefined,
                specificationInnerSn: undefined
            } // 当前进行批号选择的目标行数据
        };
    },
    computed: {
        orderName() {
            return ORDER_NAME;
        },

        tableColumnConfig() {
            return tableColumnConfig;
        },

        productColumns() {
            if (this.isReadonly) {
                return productColumns
                    .map((column) => ({ ...column, editRender: undefined }))
                    .filter((column) => ['unitId', 'type', 'mdrfInnerSn'].indexOf(column.field) < 0);
            }
            return productColumns.filter((column) => ['unitName', 'typeName', 'mdrfSn'].indexOf(column.field) < 0);
        },

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
         * 是否为新建预售单
         */
        isAddOrder() {
            return this.visible && this.targetOrder === null;
        },

        /**
         * 是否为编辑预售单
         */
        isEditOrder() {
            if (this.targetOrder && this.targetOrder.auditStatus === advanceSaleOrderAuditStatusMap.UNCHECKED) {
                return true;
            }
            return this.visible && this.targetOrder !== null;
        },

        /**
         * 是否只读
         */
        isReadonly() {
            if (this.isFinance) {
                return true;
            }
            if (this.targetOrder && this.targetOrder.auditStatus === advanceSaleOrderAuditStatusMap.APPROVED) {
                return true;
            }
            return false;
        },

        readonlyExpectAogDate() {
            if (!this.dataEditing.expectAogDate) {
                return undefined;
            }
            if (typeof this.dataEditing.expectAogDate === 'object') {
                return this.dataEditing.expectAogDate.format(DATE_TIME_FORMAT);
            }
            return this.dataEditing.expectAogDate;
        },

        currentWarehouse() {
            if (!this.dataEditing.warehouseId) {
                return undefined;
            }
            return {
                name: this.dataEditing.warehouseName,
                innerSn: this.dataEditing.warehouseId
            };
        },

        warehouseOperatorSelecterValue() {
            if (!this.dataEditing.warehouseUserId) {
                return undefined;
            }
            return {
                key: String(this.dataEditing.warehouseUserId),
                label: this.dataEditing.warehouseUserName
            };
        }
    },
    watch: {
        visible: {
            handler(newValue) {
                if (newValue) {
                    // 打开时，若仓库已选，加载仓库人员
                    if (this.targetOrder && this.targetOrder.warehouseId) {
                        this.loadWarehouseOperatorOptions(this.targetOrder.warehouseId);
                    }

                    // 打开时初始化数据
                    if (this.targetOrder) {
                        // 编辑
                        this.handleInit(this.targetOrder);
                        this.loadOrderInfo(this.targetOrder.id);
                    } else {
                        // 新建
                        this.handleInit();
                        // 选中第一个所属部门
                        // 选中登录人作为经手人
                        this.$nextTick(() => {
                            this.$refs.department.handleSelectFirstDepartmentOfUser();
                            this.$refs.employee.handleSelectCurrentUser();
                        });
                    }
                } else {
                    // 关闭时清空数据
                    this.handleInit();
                }
            },
            immediate: true
        }
    },
    methods: {
        onSaveButtonClick() {
            this.handleSave();
        },

        /**
         * 表格单元格编辑激活时
         */
        async onEditActived({ row, column }) {
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
                case 'batchNumber':
                    this.batchSelecterTarget._XID = row._XID;
                    this.batchSelecterTarget.billsType = this.dataEditing.billsType;
                    this.batchSelecterTarget.warehouseInnerSn = this.dataEditing.warehouseId;
                    this.batchSelecterTarget.productInnerSn = row.productId;
                    this.batchSelecterTarget.productType = row.productType;
                    this.batchSelecterTarget.productVendorInnerSn = row.productVendorId;
                    this.batchSelecterTarget.specificationInnerSn = row.specificationId;
                    this.handleOpenModals(modalsKeyMap.BATCH_SELECTER);
                    break;
                case 'mdrfInnerSn':
                    if (!row.optionsLoaded.mdrfInnerSn) {
                        try {
                            this.$set(row.optionsLoading, 'mdrfInnerSn', true);
                            const options = await this.loadMdrfOptions(row.productVendorId, row.productId);
                            this.$set(row.options, 'mdrfInnerSn', options);
                            this.$set(row.optionsLoaded, 'mdrfInnerSn', true);
                        } finally {
                            this.$set(row.optionsLoading, 'mdrfInnerSn', false);
                        }
                    }
                    break;
                case 'unitId':
                    // 若单位选项未加载则加载单位
                    if (!row.optionsLoaded.unitId) {
                        try {
                            this.$set(row.optionsLoading, 'unitId', true);
                            const options = await this.loadProductUnitOptions({
                                ...row,
                                vendor_inner_sn: row.productVendorId,
                                product_inner_sn: row.productId,
                                specification_inner_sn: row.specificationId
                            });
                            this.$set(row.options, 'unitId', options);
                            this.$set(row.optionsLoaded, 'unitId', true);
                        } finally {
                            this.$set(row.optionsLoading, 'unitId', false);
                        }
                    }
                    break;
                default:
            }
        },

        onOrganizationSelected(organizations) {
            if (organizations && organizations.length > 0) {
                this.dataEditing.buyerName = organizations[0].organizationName;
                this.dataEditing.buyerId = organizations[0].organizationInnerSn;
                this.dataEditing.billsType = organizations[0].billsType;
            }
            this.handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER);
            if (this.needAutoOpenProductSelecter) {
                this.needAutoOpenProductSelecter = false;
                this.handleOpenModals(modalsKeyMap.PRODUCT_SELECTER);
            }
        },

        onOrganizationClear() {
            this.dataEditing.buyerId = undefined;
            this.dataEditing.buyerName = undefined;
            this.dataEditing.billsType = undefined;
        },

        onWarehouseChange(value = {}) {
            if (this.dataEditing.warehouseName === value.name && this.dataEditing.warehouseId === value.id) {
                return;
            }
            // 更新值
            let removedCount = 0;
            this.dataEditing.warehouseName = value.name;
            this.dataEditing.warehouseId = value.id;

            // 重置仓库人员值，加载仓库人员列表
            if (value.id) {
                this.loadWarehouseOperatorOptions(value.id);
            } else {
                this.warehouseOperatorOptions = null;
            }
            if (this.dataEditing.warehouseUserId && this.dataEditing.warehouseUserName) {
                this.dataEditing.warehouseUserId = undefined;
                this.dataEditing.warehouseUserName = undefined;
                this.$message.warning('由于更改仓库，仓库人员值已重置');
            }

            // 过滤与当前仓库不符合的单据产品
            const newDetails = this.dataEditingDetails.filter((product) => {
                if (product.warehouseId !== this.dataEditing.warehouseId) {
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

        onDepartmentChange(value = {}) {
            this.dataEditing.saleDepartmentName = value.name;
            this.dataEditing.saleDepartmentId = value.id;
        },

        onHandlerChange(value) {
            this.dataEditing.agentUserName = value.name;
            this.dataEditing.agentUserId = value.id;
        },

        onWarehouseOperatorChange(value) {
            this.dataEditing.warehouseUserName = value.label;
            this.dataEditing.warehouseUserId = value.key;
        },

        /**
         * 产品选择返回值时
         * @param {object} data 产品数据
         * @param {array | undefined} data.advanceSaleOrderProducts 预售单可用产品
         */
        onProductAdd(data) {
            // 采购入库单可用产品
            if (data.advanceSaleOrderProducts) {
                let warehouseName = this.dataEditing.warehouseName;
                let warehouseId = this.dataEditing.warehouseId;
                let baseTarget;
                let i = 0;
                let countAdd = 0;
                // 找出一个基准产品，根据该基准产品自动设置相关单位、仓库、采购员
                // 基准产品：若已设置相关单位，相关单位相同；若已设置仓库，仓库相同
                while (i < data.advanceSaleOrderProducts.length) {
                    let t = data.advanceSaleOrderProducts[i];
                    i += 1;
                    if (warehouseId && t.warehouse_inner_sn !== warehouseId) {
                        continue;
                    }
                    if (!baseTarget) {
                        baseTarget = t;
                        warehouseName = baseTarget.warehouse_name;
                        warehouseId = baseTarget.warehouse_inner_sn;
                    }
                    // 将可用的产品添加到列表
                    {
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
                        if (t.mdrf_inner_sn || t.mdrf_inner_sn === 0) {
                            mdrfOptions = [
                                {
                                    value: t.mdrf_inner_sn,
                                    label: t.mdrf_sn
                                }
                            ];
                        }
                        countAdd += 1;
                        this.dataEditingDetails.splice(Math.max(0, this.dataEditingDetails.length - 1), 0, {
                            productVendorId: t.vendor_inner_sn,
                            productVendorName: t.vendor_name,
                            productName: t.product_name,
                            productId: t.product_inner_sn,
                            productType: t.product_type,
                            specificationName: t.specification,
                            specificationId: t.specification_inner_sn,
                            batchNumber: undefined,
                            quantity: new Big(t.quantity || 0.0001).toNumber(),
                            unitId: t.min_unit_inner_sn,
                            unitName: t.min_unit,
                            produceDate: t.produce_date,
                            overdueDate: t.overdue_date,
                            type: advanceSaleOrderProductTypeMap.NORMAL.value,
                            mdrfInnerSn: t.mdrf_inner_sn,
                            mdrfSn: t.mdrf,
                            healthCareSn: undefined,
                            options: {
                                unitId: unitOptions,
                                mdrfInnerSn: mdrfOptions
                            },
                            optionsLoading: { unitId: false, mdrfInnerSn: false },
                            optionsLoaded: { unitId: false, mdrfInnerSn: false },
                            batchNumberLoaded: false
                        });
                    }
                }
                if (countAdd !== data.advanceSaleOrderProducts.length) {
                    this.$message.warning(
                        `与当前所设置单位和仓库不符合的产品（${
                            data.advanceSaleOrderProducts.length - countAdd
                        }个）已自动过滤`
                    );
                }
                // 若仓库发生变更，加载仓库人员列表
                if (this.dataEditing.warehouseId !== warehouseId) {
                    this.loadWarehouseOperatorOptions(warehouseId);
                }
                this.dataEditing.warehouseName = warehouseName;
                this.dataEditing.warehouseId = warehouseId;
            }
            this.handleCloseModals(modalsKeyMap.PRODUCT_SELECTER);
            this.$nextTick(() => {
                const list = this.dataEditingDetails;
                this.$refs.xTable.scrollToRow(list[list.length - 1]);
            });
        },

        /**
         * 批号选择完成时，更新数据
         */
        onBatchSelecterSubmit(batchInfo) {
            const list = this.dataEditingDetails;
            for (let i = 0, l = list.length; i < l; i += 1) {
                if (list[i]._XID === this.batchSelecterTarget._XID) {
                    list[i].batchNumber = batchInfo.batch_number;
                    list[i].buyingPrice = batchInfo.average_price;
                    list[i].produceDate = batchInfo.produce_date;
                    list[i].overdueDate = batchInfo.overdue_date;
                    break;
                }
            }
        },

        /**
         * 初始化编辑器
         * targetOrder为空时，将清空数据
         * @param {object | undefined} targetOrder 编辑的目标单据
         */
        handleInit(targetOrder = {}) {
            Object.keys(this.dataEditing).forEach((key) => {
                if (Object.prototype.hasOwnProperty.call(this.dataEditing, key)) {
                    if (key === 'expectAogDate') {
                        this.dataEditing[key] = targetOrder[key] && moment(targetOrder[key]);
                        return;
                    }
                    this.dataEditing[key] = targetOrder[key];
                }
            });
            if (targetOrder.details) {
                this.dataEditingDetails = targetOrder.details.map((product) => ({
                    ...product,
                    options: {
                        unitId: [{ value: product.unitId, label: product.unitName }],
                        mdrfInnerSn: product.mdrfSn ? [{ value: product.mdrfInnerSn, label: product.mdrfSn }] : []
                    }
                }));
            } else {
                this.dataEditingDetails = [];
            }
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
                // 收货单位校验
                try {
                    if (!data.buyerId) {
                        throw new Error('buyerId');
                    }
                    if (!data.buyerName) {
                        throw new Error('buyerName');
                    }
                    if (!data.billsType) {
                        throw new Error('billsType');
                    }
                } catch (err) {
                    throw new Error('请选择收货单位');
                }
                // 仓库校验
                if (!data.warehouseId || !data.warehouseName) {
                    throw new Error('请选择仓库');
                }
                // 部门校验
                if (!data.saleDepartmentId || !data.saleDepartmentName) {
                    throw new Error('请选择部门');
                }
                // 经手人
                if (!data.agentUserId || !data.agentUserName) {
                    throw new Error('请选择经手人');
                }
                // 产品数据校验
                if (!data.details.length) {
                    throw new Error('产品不能为空');
                }
                data.details.forEach((product, index) => {
                    if (!product.unitId) {
                        throw new Error(`错误的产品单位（NO.${index + 1} ${product.productName}）`);
                    }
                    if (!(Number(product.quantity) > 0)) {
                        throw new Error(`数量必须大于0（NO.${index + 1} ${product.productName}）`);
                    }
                    if (product.buyingPrice === undefined || product.buyingPrice === null) {
                        throw new Error(`请手动选择批号（NO.${index + 1} ${product.productName}）`);
                    }
                });
            } catch (err) {
                this.$message.error(err.message);
                return false;
            }
            return true;
        },

        /**
         * 保存
         */
        async handleSave() {
            let data = {
                ...this.dataEditing,
                expectAogDate: this.dataEditing.expectAogDate
                    ? this.dataEditing.expectAogDate.format(DATE_TIME_FORMAT)
                    : undefined,
                details: this.dataEditingDetails.map((product, index) => {
                    let result = { sortNumber: index + 1, ...product };
                    delete result.options;
                    delete result.optionsLoading;
                    delete result.optionsLoaded;
                    delete result.money;
                    delete result._XID;
                    return result;
                })
            };
            if (!this.handleCheckData(data)) {
                return;
            }
            // 新增
            if (this.isAddOrder) {
                const orderInfo = await this.addOrder(data);
                this.$emit('addFinish', orderInfo);
            }
            // 编辑
            if (this.isEditOrder) {
                const orderInfo = await this.editOrder(this.targetOrder.id, data);
                this.$emit('editFinish', orderInfo);
            }
            this.handleClose();
        },

        handleSelectProducts() {
            if (this.isReadonly) {
                return;
            }
            if (!this.dataEditing.billsType || !this.dataEditing.buyerId) {
                this.$message.warning('请选择收货单位');
                this.handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER);
                this.needAutoOpenProductSelecter = true;
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
        handleCheckCellEditable() {
            if (this.isReadonly) {
                return false;
            }
            return true;
        },

        /**
         * 加载预售单详情
         * @param {string | number} id 预售单id
         */
        async loadOrderInfo(id) {
            try {
                this.detailLoading = true;
                const { code, msg, info } = await advanceSaleOrderInfoAPI(id);
                if (code !== 0) {
                    this.$message.error(msg || `${this.orderName}详情加载失败`);
                    throw new Error(msg);
                }
                this.handleInit(info);
            } finally {
                this.detailLoading = false;
            }
        },

        /**
         * 创建预售单
         */
        async addOrder(data) {
            try {
                this.buttonsLoading.save = true;
                const { code, msg, info } = await advanceSaleOrderAddAPI(data);
                if (code !== 0) {
                    this.$message.error(msg || `${this.orderName}创建失败`);
                    throw new Error(msg);
                }
                this.$message.success(`${this.orderName}创建成功`);
                return info;
            } finally {
                this.buttonsLoading.save = false;
            }
        },

        /**
         * 编辑预售单
         */
        async editOrder(id, data) {
            try {
                this.buttonsLoading.save = true;
                const { code, msg, info } = await advanceSaleOrderEditAPI(id, data);
                if (code !== 0) {
                    this.$message.error(msg || `${this.orderName}保存失败`);
                    throw new Error(msg);
                }
                this.$message.success(`${this.orderName}保存成功`);
                return info;
            } finally {
                this.buttonsLoading.save = false;
            }
        }
    }
};
</script>
