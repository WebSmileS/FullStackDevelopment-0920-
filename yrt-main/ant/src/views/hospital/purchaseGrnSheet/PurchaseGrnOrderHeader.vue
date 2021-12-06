<template>
    <div>
        <a-col :span="24">
            <h3>
                <span v-if="organizationNameLogined">{{ organizationNameLogined }}&nbsp;&nbsp;</span>
                {{ orderName }}
                <span v-if="isRedOrder" class="red">（红冲）</span>
                <span v-if="isCancelOrder">（作废）</span>
            </h3>
        </a-col>
        <a-col :span="8">
            <a-col :span="6" style="margin-top: 1px">
                <span><em class="em red">*</em>&nbsp;供货单位:</span>
            </a-col>
            <a-col :span="15">
                <!-- <base-input-with-clear-button
                    size="small"
                    :disabled="isFormalOrder || isTransferOrder"
                    :value="dataEditing.organizationName"
                    @click="handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                    @clear="onOrganizationClear"
                ></base-input-with-clear-button> -->
                <a-select
                    mode="default"
                    show-search
                    style="width: 100%"
                    class="focus-a-select"
                    :value="dataEditing.organizationName"
                    :filter-option="false"
                    :show-arrow="false"
                    :not-found-content="null"
                    :default-active-first-option="false"
                    @search="searchGrnOrgList"
                    @change="changeGrnOrg"
                >
                    <a-select-option v-for="d in grnOrgData" :key="d.key" :value="d.key">
                        {{ d.name }}
                    </a-select-option>
                    <a-select-option value="search-more">
                        <b style="color: #1890ff">搜索更多...</b>
                    </a-select-option>
                    <a-select-option value="tips">
                        <i>提示：输入文字搜索</i>
                    </a-select-option>
                </a-select>
            </a-col>
        </a-col>
        <a-col :span="8">
            <a-col :span="6" style="margin-top: 1px">
                <span><em class="em red">*</em>&nbsp;仓库:</span>
            </a-col>
            <a-col :span="15">
                <a-select
                    allowClear
                    labelInValue
                    :disabled="isFormalOrder && !isAddOrder"
                    :options="warehouseList"
                    :value="warehouseSelecterValue"
                    @change="onWarehouseChange"
                ></a-select>
            </a-col>
        </a-col>
        <a-col :span="8">
            <a-col :span="6" style="margin-top: 1px">
                <span><em class="em red">*</em>&nbsp;部门:</span>
            </a-col>
            <a-col :span="15">
                <a-tree-select
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
            </a-col>
        </a-col>
        <div>
            <organization-selecter
                title="选择相关单位"
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
                <product-selecter-vendor
                    slot="vendor"
                    autoLoadMounted
                    defaultIsContractProduct
                    :billsType="dataEditing.billsType"
                    :organizationInnerSn="dataEditing.organizationInnerSn"
                    :warehouseInnerSn="dataEditing.warehouseInnerSn"
                    @submit="onProductAdd"
                ></product-selecter-vendor>
                <product-selecter-transfer-order
                    slot="transfer"
                    :order="currentTransferOrder"
                    @submit="onProductAdd"
                ></product-selecter-transfer-order>
            </product-selecter-modal>
        </div>
    </div>
</template>

<script>
import Big from 'big.js';
import ProductSelecterModal from '@/components/ProductSelecterModal';
import OrganizationSelecter from '@/components/OrganizationSelecter';
import textNullDataFilter from '@/filters/textNullData';
import ProductSelecterVendor from '@/components/ProductSelecterVendor';
import {
    purchaseGrnInfoAPI,
    grnOrgListAPI
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
    name: 'PurchaseGrnOrderHeader',
    components: {
        ProductSelecterModal,
        ProductSelecterVendor,
        ProductSelecterTransferOrder,
        OrganizationSelecter
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
            tableColumnConfig,
            modalsKeyMap,
            orderName: ORDER_NAME,
            productColumns: Object.freeze(productColumns),
            departmentTree: null,
            warehouseOperatorOptions: null,
            grnOrgData: [],
            dataEditing: {
                auditorInnerSn: undefined,
                auditorName: undefined,
                use_department: undefined,
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
                this.handleInitTransferOrder(this.currentTransferOrder);
                this.handleSetDefaultInfo();
            }
        } else {
            // 新建
            this.handleInit();
            this.handleSetDefaultInfo();
        }

        // 打开时，若部门列表未加载，加载部门列表
        if (this.departmentTree === null) {
            this.loadDepartmentTree();
        }
        
        this.searchGrnOrgList();

        // focus
        let aSelectDom = document.getElementsByClassName('focus-a-select');
        aSelectDom[0].getElementsByClassName('ant-select-search__field')[0].click();
    },
    methods: {
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
                this.$emit('updateOrgnization', {
                    flag: true,
                    dataEditing: this.dataEditing
                });
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
            this.$emit('updateOrgnization', {
                flag: true,
                dataEditing: this.dataEditing
            });
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
            this.$emit('updateOrgnization', {
                flag: false,
                dataEditing: this.dataEditing
            });
        },

        handleSelectProducts() {
            if (this.isReadonly) {
                return;
            }
            if (!this.dataEditing.billsType || !this.dataEditing.organizationInnerSn) {
                this.$message.warning('请选择相关单位');
                this.handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER);
                this.needAutoOpenProductSelecter = true;
                return;
            }
            this.handleOpenModals(modalsKeyMap.PRODUCT_SELECTER);
        },
        changeGrnOrg(value) {
            if (value === 'search-more') {
                this.handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER)
                return;
            } else if (value === 'tips') {
                return;
            } else {
                let obj = Object.assign(
                    {},
                    this.grnOrgData.find((v) => v.key === value)
                );
                this.dataEditing.organizationName = obj.name;
                this.dataEditing.organizationInnerSn = obj.org_id;
                this.dataEditing.billsType = obj.billsType;
                this.$emit('updateOrgnization', {
                    flag: true,
                    dataEditing: this.dataEditing
                });
            }
        },
        searchGrnOrgList(value) {
            this.grnOrgData = [];
            this.dataEditing.organizationName = value;
            this.grnOrgListAPIFn(1, true);
            this.grnOrgListAPIFn(3, true);
            this.grnOrgListAPIFn(2, true);
        },
        // 【相关机构列表】 【仓库管理-入库单】
        async grnOrgListAPIFn(type, isInitial) {
            let params = {};
            params.pageSize = 5;
            params.pageNum = 1;
            if (this.dataEditing.organizationName && this.dataEditing.organizationName.length) {
                params.name = this.dataEditing.organizationName;
            }
            if (this.isCertifie) {
                params.is_certifie = true;
            }
            if (
                this.pageTypeName === 0 ||
                this.pageTypeName === 3 ||
                this.pageTypeName === 7 ||
                this.pageTypeName === 8
            ) {
                // 入库单相关机构
                params.all = this.isContractOrgChecked;
            }
            await grnOrgListAPI(type, this.$cookie.get('userbelong'), { params: params })
            .then((res) => {
                if (parseFloat(res.code) === 0) {
                    let TempData = res.rows.map((item) => {
                        item.key = (Math.random() + 1).toString(36).substring(7);
                        return item;
                    });
                    this.grnOrgData = this.grnOrgData.concat(TempData);
                }
            });
        },

        onDepartmentChange(value) {
            this.dataEditing.departmentName = value.label;
            this.dataEditing.departmentInnerSn = value.value;
            this.$emit('updateOrgnization', {
                flag: false,
                dataEditing: this.dataEditing
            });
        },

        onHandlerChange(value) {
            this.dataEditing.handlerName = value.label;
            this.dataEditing.handlerInnerSn = value.key;
            this.$emit('updateOrgnization', {
                flag: false,
                dataEditing: this.dataEditing
            });
        },

        onWarehouseOperatorChange(value) {
            this.dataEditing.warehouseOperatorName = value.label;
            this.dataEditing.warehouseOperatorInnerSn = value.key;
            this.$emit('updateOrgnization', {
                flag: false,
                dataEditing: this.dataEditing
            });
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
                let i = 0,
                    records = [];
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
                    let obj = {
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
                        salesPrice: 0,
                        totalSalesPrice: 0,
                        vendor_name: t.vendor_name,
                        vendor_inner_sn: t.vendor_inner_sn,
                        voucher_type_c: orderTypeMap.NONE.key,
                        options: {
                            unit_inner_sn: unitOptions,
                            mdrf_inner_sn: mdrfOptions
                        },
                        optionsLoading: { unit_inner_sn: false, mdrf_inner_sn: false },
                        optionsLoaded: { unit_inner_sn: false, mdrf_inner_sn: false }
                    };
                    records.push(obj);
                    this.dataEditingDetails.push(obj);
                }
                this.$emit('updateSheet', records);
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
        },

        /**
         * 初始化编辑器
         * targetOrder为空时，将清空数据
         * @param {object | undefined} targetOrder 编辑的目标单据
         */
        handleInit(targetOrder = {}) {
            this.dataEditing.auditorInnerSn = targetOrder.auditor_inner_sn;
            this.dataEditing.use_department = targetOrder.use_department;

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
                this.dataEditing.use_department = undefined;
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
                    let totalSalesPrice = new Big(product.salesPrice).times(product.quantity).toNumber();
                    money = isNaN(money) ? undefined : money;
                    totalSalesPrice = isNaN(totalSalesPrice) ? undefined : totalSalesPrice;
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
                        totalSalesPrice,
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
            } else {
                this.dataEditingDetails = [];
                this.dataRemovedDetails = [];
                this.currentTransferOrder = undefined;
            }
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
                    totalSalesPrice: new Big(product.salesPrice).times(product.quantity).toNumber(),
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

        handleSetDefaultInfo() {
            try {
                const userInfo = JSON.parse(this.$cookie.get('loginDatas'));
                if (userInfo) {
                    // 将经手人设置为当前登录的人
                    if (userInfo.EmployeeInfo) {
                        this.dataEditing.handlerName = userInfo.EmployeeInfo.Name;
                        this.dataEditing.handlerInnerSn = userInfo.EmployeeInfo.Employee_inner_sn;
                    }
                    // 将部门设置为当前登录人所属的第一个部门
                    if (userInfo.Departments && userInfo.Departments.length) {
                        this.dataEditing.departmentName = userInfo.Departments[0].Name;
                        this.dataEditing.departmentInnerSn = userInfo.Departments[0].Department_inner_sn;
                    }
                    this.$emit('updateOrgnization', {
                        flag: false,
                        dataEditing: this.dataEditing
                    });
                }
            } catch (err) {
                console.warning('人员相关默认值设置失败');
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
                    if (!(Number(product.salesPrice) >= 0)) {
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
    }
};
</script>
