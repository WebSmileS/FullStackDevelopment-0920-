<template>
    <and-page-layout class="scoped scoped-white arrival-sheets">
        <template slot="breadcrumb">
            <a-breadcrumb style="float: left">
                <a-breadcrumb-item>采购管理</a-breadcrumb-item>
                <a-breadcrumb-item>收货</a-breadcrumb-item>
            </a-breadcrumb>
        </template>
        <template slot="tabs-content">
            <and-tabs-content :panes="panes" :cur="curTab" @changeTabFn="changeTabFn">
                <template slot="atc-header">
                    <a-row class="atc-header" v-if="curTab === 'add'">
                        <a-col :span="24">
                            <h2>{{ orgName }}&nbsp;&nbsp;收货单&nbsp;&nbsp;&nbsp;
                                <i
                                    v-if="editable.flag && editable.type === 'view'"
                                    style="margin-left: 25px; font-size: 15px"
                                >{{ `单据编号: ${editable.obj.sn}` }}</i>
                            </h2>
                        </a-col>
                        <a-col :span="8">
                            <a-col :span="6" style="margin-top: 1px">
                                <span><em class="em red">*</em>&nbsp;供货单位:</span>
                            </a-col>
                            <a-col :span="15">
                                <a-select
                                    :disabled="editable.flag"
                                    mode="default"
                                    show-search
                                    style="width: 100%"
                                    class="focus-a-select"
                                    :value="supplySearch"
                                    :filter-option="false"
                                    :show-arrow="false"
                                    :not-found-content="null"
                                    :default-active-first-option="false"
                                    @search="loadSupplyUnits"
                                    @change="changeSupplyUnit"
                                >
                                    <a-select-option v-for="d in tableData" :key="d.key" :value="d.key">
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
                                    :options="warehouseList"
                                    :value="warehouseSelecterValue"
                                    :disabled="editable.flag && editable.type === 'view'"
                                    @change="onWarehouseChange"
                                ></a-select>
                            </a-col>
                        </a-col>
                        <a-col :span="8">
                            <a-col :span="6" style="margin-top: 1px">
                                <span><em class="em red">*</em>&nbsp;送货员:</span>
                            </a-col>
                            <a-col :span="15">
                                <a-input
                                    class="form-input"
                                    size="small"
                                    v-model="dataEditing.deliveryMenName"
                                    :disabled="editable.flag && editable.type === 'view'"
                                />
                            </a-col>
                        </a-col>
                    </a-row>
                </template>
                <template slot="atc-pane">
                    <a-row class="atc-pane">
                        <and-sheet-acts
                            :data="actColumns"
                            :pageName="pageName"
                            :curTab="curTab"
                            :widths="widths"
                            @renderPage="changeTabFn"
                        >
                        </and-sheet-acts>
                        <and-sheet
                            :columns="columns"
                            :data="addData"
                            :widths="widths"
                            :menu="menu"
                            :dropdownData="venTableData"
                            :totalFormat="totalFormat"
                            @searchComData="searchComData"
                            @chooseComData="chooseComData"
                            @changeWidths="changeWidths"
                            @clickedMenu="clickedMenu"
                            @multiSelected="multiSelected"
                            @scrollToEnd="sheetScrollToEnd"
                            @searchSheet="searchSheetFn"
                            @addNewRow="addNewSheetRow"
                        >
                        </and-sheet>
                    </a-row>
                </template>
                <template slot="atc-footer">
                    <a-row class="atc-footer" v-if="curTab === 'add'">
                        <a-row style="margin: 8px 0">
                            <a-col :span="24">
                                <a-input
                                    :disabled="editable.flag && editable.type === 'view'"
                                    v-model="dataEditing.description"
                                    placeholder="备注"
                                    class="input"
                                />
                            </a-col>
                            <a-col :span="24" style="margin-top: 5px;text-align: left">
                                <a-col :span="4">
                                    <span class="label" style="width: auto;">制单人:&nbsp;</span>
                                    <span>{{ dataEditing.createdBy | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4">
                                    <span class="label" style="width: auto;">生成时间：&nbsp;</span>
                                    <span>{{ dataEditing.createdTime | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4">
                                    <span class="label" style="width: auto;">收货人：&nbsp;</span>
                                    <span>{{ dataEditing.arrivalBy | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4">
                                    <span class="label" style="width: auto;">收货时间：&nbsp;</span>
                                    <span>{{ dataEditing.arrivalTime | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4">
                                    <span class="label" style="width: auto;">复核人：&nbsp;</span>
                                    <span>{{ dataEditing.confirmBy | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4">
                                    <span class="label" style="width: auto;">复核时间：&nbsp;</span>
                                    <span>{{ dataEditing.confirmTime | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4" v-if="dataEditing.isCancel">
                                    <span class="label" style="width: auto;">作废人：&nbsp;</span>
                                    <span>{{ dataEditing.cancelBy | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4" v-if="dataEditing.isCancel">
                                    <span class="label" style="width: auto;">作废时间：&nbsp;</span>
                                    <span>{{ dataEditing.confirmTime | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="16" v-if="dataEditing.isCancel">
                                    <span class="label" style="width: auto;">作废原因：&nbsp;</span>
                                    <span>{{ dataEditing.confirmTime | textNullDataFilter }}</span>
                                </a-col>
                            </a-col>
                        </a-row>
                        <a-row>
                            <!-- <a-col :span="6" style="text-align: left">
                                <a-button
                                    @click="handleClose"
                                >取消</a-button>
                            </a-col> -->
                            <a-col :offset="18" :span="6" style="text-align: right">
                                <a-button
                                    :disabled="editable.flag && editable.type === 'view'"
                                    @click="onSaveButtonClick"
                                    type="primary"
                                >保存</a-button>
                                <a-button
                                    :disabled="editable.flag && editable.type === 'view'"
                                    @click="handleReceive"
                                    type="primary"
                                    style="margin-left: 5px"
                                >收货</a-button>
                            </a-col>
                        </a-row>
                    </a-row>
                </template>
            </and-tabs-content>
        </template>
        <template slot="modals">
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
                <product-selecter-arrival
                    slot="useable"
                    :billsType="dataEditing.billsType"
                    :organizationInnerSn="dataEditing.organizationInnerSn"
                    :warehouseInnerSn="dataEditing.warehouseInnerSn"
                    @submit="onProductAdd"
                ></product-selecter-arrival>
                <product-selecter-arrival-barcode
                    slot="barcode"
                    :billsType="dataEditing.billsType"
                    :organizationInnerSn="dataEditing.organizationInnerSn"
                    :warehouseInnerSn="dataEditing.warehouseInnerSn"
                    @submit="(data) => onProductAdd({ arrivalOrderProducts: data.barcodeProducts })"
                    @close="handleCloseModals(modalsKeyMap.PRODUCT_SELECTER)"
                ></product-selecter-arrival-barcode>
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
                :columns="productSelecterArrivalColumns"
                :data="barcodeProductsToFilter"
                title="请选择调单产品"
                @close="handleCloseModals(modalsKeyMap.BARCODE_PRODUCT_FILTER)"
                @submit="onBarcodeProductFilterSubmit"
            ></item-selecter-table-modal>
        </template>
    </and-page-layout>
</template>

<script>
// import newDesign Layount
import AndPageLayout from '@/components/AndLayout/AndPageLayout';
import AndTabsContent from '@/components/AndLayout/AndTabsContent';
// import components
import AndSheetActs from '@/components/purchase/AndSheetActs';
import AndSheet from '@/components/AndRenders/AndSheet';

import tabsMixin from '@/mixins/tabs';
import modalsMixin from '@/mixins/modals';
import productUnitOptionsMixin from '@/mixins/productUnitOptions';
import barcodeScanAPIResultParser from '@/mixins/barcodeScanAPIResultParser';
import barcodeInputFinish from '@/mixins/barcodeInputFinish';

import OrganizationSelecter from './OrganizationSelecter';
import textNullDataFilter from '@/filters/textNullData';
import {
    arrivalOrderStatusTextFilter,
    arrivalOrderCancelTextFilter
} from './filters';

import BaseModalTextInput from '@/components/BaseModalTextInput';
import ProductSelecterModal from '@/components/ProductSelecterModal';
import ItemSelecterTableModal from '@/components/ItemSelecterTableModal';

import productSelecterArrivalColumns from './productSelecterArrivalColumns';
import productColumns from './arrivalOrderEditorColumns';

import {
    arrivalOrderDeleteAPI,
    arrivalOrderAddAPI,
    arrivalOrderApprovalAPI,
    arrivalOrderCancelAPI,
    arrivalOrderDetailAPI,
    arrivalOrderEditAPI,
    arrivalOrderProductListBarcodeAPI,
    grnOrgListAPI,
    arrivalOrderListAPI
} from '@/service/pageAjax';
import { orderTypeMap } from '@/config/constants';
import {
    arrivalOrderTabMap,
    arrivalOrderStatusMap
} from './type';

const modalsKeyMap = Object.freeze({
    ORGANIZATION_SELECTER: 'o',
    PRODUCT_SELECTER: 'p',
    CANCEL_REASON: 'c',
    BARCODE_PRODUCT_FILTER: 'b'
});

const buttonsMap = {
    add: { key: 'add', text: '调单收货', type: 'primary' },
    edit: { key: 'edit', text: '修改', type: 'primary' },
    approval: { key: 'approval', text: '复核', type: 'primary' },
    receive: { key: 'receive', text: '收货', type: 'primary' },
    cancel: { key: 'cancel', text: '作废', type: 'danger' },
    delete: { key: 'delete', text: '删除', type: 'danger' }
};

export default {
    name: 'ArrivalOrder',
    components: {
        // custom layounts
        AndPageLayout,
        AndTabsContent,
        // custom components
        AndSheet,
        AndSheetActs,
        OrganizationSelecter,
        BaseModalTextInput,
        ProductSelecterModal,
        ItemSelecterTableModal
    },
    mixins: [
        tabsMixin,
        modalsMixin,
        productUnitOptionsMixin,
        barcodeScanAPIResultParser,
        barcodeInputFinish
    ],
    filters: {
        textNullDataFilter,
        arrivalOrderStatusTextFilter,
        arrivalOrderCancelTextFilter
    },
    props: {
        // 管理模式下，部分功能需要过滤处理
        managerMode: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            // init panes
            panes: [
                {
                    key: 'add',
                    title: '调单收货'
                },
                {
                    key: 'executing',
                    title: '执行中'
                },
                {
                    key: 'receipt',
                    title: '收货记录'
                }
            ],
            curTab: 'add',
            actColumns: [],
            addData: [],
            cookieInfo: this.$cookie,
            editable: {
                flag: false,
                type: '',
                obj: {}
            },
            totalFormat: {
                on: false,
                searchable: false,
                footer: []
            },
            copied: {
                flag: false,
                objs: []
            },
            addColumns: [
                {
                    data: 'idx',
                    type: 'header',
                    disabled: true,
                    name: ''
                },
                {
                    data: 'product_vendor_name',
                    type: 'input',
                    disabled: false,
                    name: '厂商名称'
                },
                {
                    data: 'product_name',
                    type: 'input',
                    disabled: false,
                    name: '产品名称'
                },
                {
                    data: 'specification',
                    type: 'input',
                    disabled: false,
                    name: '规格型号'
                },
                {
                    data: 'unit_inner_sn',
                    type: 'input',
                    disabled: false,
                    name: '包装单位'
                },
                {
                    data: 'order_quantity',
                    type: 'number',
                    disabled: false,
                    name: '订单数量'
                },
                {
                    data: 'batch_number',
                    type: 'input',
                    disabled: false,
                    name: '批号'
                },
                {
                    data: 'arrival_quantity',
                    type: 'input',
                    disabled: false,
                    name: '收货数量'
                },
                {
                    data: 'barcode',
                    type: 'number',
                    disabled: false,
                    name: '扫码（PI）'
                }
            ],
            executingColumns: [
                {
                    data: 'idx',
                    type: 'header',
                    disabled: true,
                    name: ''
                },
                {
                    data: 'sn',
                    type: 'input',
                    disabled: true,
                    name: '单据编号'
                },
                {
                    data: 'organization_name',
                    type: 'input',
                    disabled: true,
                    name: '机构名称'
                },
                {
                    data: 'warehouse_name',
                    type: 'input',
                    disabled: true,
                    name: '仓库'
                },
                {
                    data: 'deliverymen_name',
                    type: 'input',
                    disabled: true,
                    name: '送货员'
                },
                {
                    data: 'created_by',
                    type: 'input',
                    disabled: true,
                    name: '创建人'
                },
                {
                    data: 'created_time',
                    type: 'input',
                    disabled: true,
                    name: '创建时间'
                },
                {
                    data: 'arrival_by',
                    type: 'input',
                    disabled: true,
                    name: '收货人'
                },
                {
                    data: 'arrival_time',
                    type: 'input',
                    disabled: true,
                    name: '收货时间'
                },
                {
                    data: 'confirm_by',
                    type: 'input',
                    disabled: true,
                    name: '复核人'
                },
                {
                    data: 'confirm_time',
                    type: 'input',
                    disabled: true,
                    name: '复核时间'
                }
            ],
            columns: [],
            widths: [],
            menu: {
                visible: false,
                data: [],
                position: {
                    top: undefined,
                    left: undefined
                }
            },
            editCellRow: undefined,
            sheetPagination: {
                loading: false,
                total: 30,
                current: 1, // 对应后端的 pageSize
                pageSize: 30, // 对应后端的 pageNum
                pageNum: 1,
                available: true
            },
            pageName: 'arrival',
            supplySearch: '',
            tableData: [],
            tableDataTemp: [],
            venTableData: [],

            listStatuType: arrivalOrderTabMap.EXECUTING.value,
            modalsKeyMap,
            loading: false,
            searchValues: {
                sn: undefined,
                organizationName: undefined,
                organizationInnerSn: undefined,
                deliveryMenName: undefined,
                dateRange: []
            },
            activeTabKey: arrivalOrderTabMap.EXECUTING.value,
            selectedRecords: [],
            orderEditorTarget: null, // 收货单编辑目标
            productSelecterArrivalColumns,
            productColumns: Object.freeze(productColumns),
            dataEditing: {
                isCancel: 0,
                sn: undefined,
                billsType: undefined,
                organizationName: undefined,
                organizationInnerSn: undefined,
                warehouseName: undefined,
                warehouseInnerSn: undefined,
                deliveryMenName: undefined,
                description: undefined,
                createdBy: undefined,
                createdTime: undefined,
                arrivalBy: undefined,
                arrivalTime: undefined,
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
        arrivalOrderTabMap() {
            return arrivalOrderTabMap;
        },
        operationGroups() {
            if (this.managerMode) {
                return [[buttonsMap.approval], [buttonsMap.cancel]];
            }
            return [[buttonsMap.add, buttonsMap.edit, buttonsMap.receive], [buttonsMap.delete]];
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false },
                edit: { disabled: false, reason: '' },
                receive: { disabled: false, reason: '' },
                approval: { disabled: false, reason: '' },
                cancel: { loading: this.loading, disabled: false, reason: '' },
                delete: { loading: this.loading, disabled: false, reason: '' }
            };
            if (this.selectedRecords.length !== 1) {
                result.edit.disabled = true;
                result.receive.disabled = true;
                result.approval.disabled = true;
                result.cancel.disabled = true;
                result.delete.disabled = true;
                result.edit.reason =
                    result.receive.reason =
                    result.approval.reason =
                    result.cancel.reason =
                    result.delete.reason =
                        '选中1条记录才能进行操作';
            }
            if (this.activeTabKey !== arrivalOrderTabMap.EXECUTING.value) {
                result.edit.disabled = true;
                result.receive.disabled = true;
                result.approval.disabled = true;
                result.cancel.disabled = true;
                result.delete.disabled = true;
                result.edit.reason =
                    result.receive.reason =
                    result.approval.reason =
                    result.cancel.reason =
                    result.delete.reason =
                        '执行中的记录才能进行操作';
            }
            return result;
        },
        productSelecterTabs() {
            return [
                { title: '可用产品', key: 'useable' },
                { title: '扫码产品', key: 'barcode' }
            ];
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
         * 是否为新建收货单
         */
        isAddOrder() {
            return this.visible && this.targetOrder === null;
        },

        /**
         * 是否为编辑收货单
         */
        isEditOrder() {
            if (this.targetOrder && this.targetOrder.is_cancel) {
                return false;
            }
            return this.visible && this.targetOrder !== null;
        },

        /**
         * 是否为复核收货单
         */
        isApprovalOrder() {
            if (this.targetOrder && this.targetOrder.is_cancel) {
                return false;
            }
            return (
                this.managerMode &&
                this.targetOrder &&
                this.targetOrder.arrival_status === arrivalOrderStatusMap.RECEIVED.value
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
                    this.targetOrder.arrival_status === arrivalOrderStatusMap.RECEIVED.value ||
                    this.targetOrder.arrival_status === arrivalOrderStatusMap.REVIEWED.value
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
        },
        orgName() {
            const employeeInfo = this.$cookie.get('EmployeeInfo');
            return (employeeInfo ? JSON.parse(employeeInfo).OrgName : '') || '';
        }
    },
    watch: {
        activeTabKey(newValue, oldValue) {
            // 切换tab并且已有选择项时，清空选择项
            if (oldValue && this.selectedRecords && this.selectedRecords.length) {
                this.selectedRecords = [];
                this.$refs[oldValue].$refs.listTable.$refs.listTable.clearRadioRow();
            }
        }
    },
    methods: {
        loadSupplyUnits(search = '') {
            this.supplySearch = search;
            this.tableDataTemp = [];
            this.tableData = [];
            this.grnOrgListAPIFn(1, search);
            this.grnOrgListAPIFn(3, search);
            this.grnOrgListAPIFn(2, search);
        },
        changeSupplyUnit(value, label) {
            if (value === 'search-more') {
                this.handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER);
                return;
            } else if (value === 'tips') {
                return;
            } else {
                let obj = Object.assign(
                    {},
                    this.tableData.find((v) => v.key === value)
                );
                this.supplySearch = obj.name;
                // this.chooseOrgFn(obj);
            }
        },
        // 【相关机构列表】 【仓库管理-入库单】
        async grnOrgListAPIFn(type, search = '') {
            let params = {};
            params.pageSize = 5;
            params.pageNum = 1;
            if (search && search.length) {
                params.name = search;
            }
            params.is_certifie = true; // this.isCertifie
            params.all = true; // 入库单相关机构 this.isContractOrgChecked
            await grnOrgListAPI(type, this.$cookie.get('userbelong'), { params: params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let TempData = res.rows.map((item) => {
                            item.key = (Math.random() + 1).toString(36).substring(7);
                            return item;
                        });
                        this.tableDataTemp = this.tableDataTemp.concat(TempData);
                    }
                })
                .finally(() => {
                    this.tableData = this.tableDataTemp;
                });
        },
        calcPagination(total) {
            this.sheetPagination.total = total;
            this.sheetPagination.available = (this.sheetPagination.pageNum * this.sheetPagination.pageSize) < this.sheetPagination.total ? true : false;
            this.sheetPagination.pageNum = this.sheetPagination.pageNum + 1;
        },
        setupMenu(tab = 'add') {
            switch (tab) {
                case 'add':
                    this.menu = {
                        visible: false,
                        data: [
                            // {
                            //     disabled: true,
                            //     key: 'insert-above',
                            //     name: '其上插入'
                            // },
                            // {
                            //     disabled: true,
                            //     key: 'insert-below',
                            //     name: '其下插入'
                            // },
                            {
                                disabled: false,
                                key: 'copy',
                                name: '复制'
                            },
                            {
                                disabled: false,
                                key: 'cut',
                                name: '剪切'
                            },
                            {
                                disabled: false,
                                key: 'paste',
                                name: '粘贴'
                            },
                            {
                                disabled: false,
                                key: 'delete',
                                name: '删除'
                            }
                        ],
                        position: {
                            top: undefined,
                            left: undefined
                        }
                    };
                    break;
                case 'perform':
                    this.menu = {
                        visible: false,
                        data: [
                            {
                                disabled: false,
                                key: 'revise',
                                name: '修改'
                            },
                            {
                                disabled: false,
                                key: 'delete',
                                name: '删除'
                            },
                            {
                                disabled: false,
                                key: 'confirm',
                                name: '确认'
                            },
                            {
                                disabled: false,
                                key: 'termination',
                                name: '终止'
                            }
                        ],
                        position: {
                            top: undefined,
                            left: undefined
                        }
                    };
                    break;
                case 'past':
                    this.menu = {
                        visible: false,
                        data: [
                            {
                                disabled: false,
                                key: 'revise',
                                name: '修改'
                            },
                            {
                                disabled: false,
                                key: 'delete',
                                name: '删除'
                            },
                            {
                                disabled: false,
                                key: 'confirm',
                                name: '确认'
                            },
                            {
                                disabled: false,
                                key: 'termination',
                                name: '终止'
                            }
                        ],
                        position: {
                            top: undefined,
                            left: undefined
                        }
                    };
                    break;
                case 'receipt':
                    this.menu = {
                        visible: false,
                        data: [
                            {
                                disabled: false,
                                key: 'view',
                                name: '浏览'
                            }
                        ],
                        position: {
                            top: undefined,
                            left: undefined
                        }
                    };
                    break;
            }
        },
        changeWidths(widths) {
            this.widths = widths;
            let cookieData = JSON.parse(this.cookieInfo.get(this.pageName + '-' + this.curTab));
            let data = {
                widths
            };
            if (cookieData !== null && cookieData.headers !== undefined && cookieData.headers.length > 0) {
                data['headers'] = cookieData.headers;
            }
            this.cookieInfo.set(this.pageName + '-' + this.curTab, JSON.stringify(data));
            // this.postStoreSettings();
        },
        deleteAddData(obj) {
            for (let i = obj.selection.from; i <= obj.selection.to; i ++) {
                this.addData[i] = {
                    ...this.addData[i],
                    product_vendor_name: '',
                    product_name: '',
                    specification: '',
                    unit_inner_sn: '',
                    order_quantity: '',
                    batch_number: '',
                    arrival_quantity: '',
                    barcode: ''
                };
            }
        },
        clickedMenu(obj) {
            if (this.curTab === 'add' && obj.key === 'delete') {
                this.deleteAddData(obj);
                let data = [], index = 0;
                this.addData.forEach((d, idx) => {
                    if (idx < obj.selection.from || idx > obj.selection.to) {
                        d.idx = ++index;
                        data.push(d);
                    }
                });
                this.addData = data;
                return;
            }
            if ((this.curTab !== 'add' && obj.key === 'revise') || (this.curTab === 'receipt' && obj.key === 'view')) {
                if (this.menu.data[0].disabled === false) {
                    this.editable = {
                        flag: true,
                        type: obj.key,
                        obj: this.addData[obj.selection.from]
                    };
                    this.changeTabFn();
                }
            }
            if (this.curTab !== 'add' && obj.key === 'confirm') {
                let records = this.getSelectionData(obj.selection.from, obj.selection.to);
                if (this.myOrderPropObj.pageType === 0) {
                    this.checkCreateOrganizationTypeListFn(records) && this.orderConfirmAPIFn(records);
                } else {
                    this.orderConfirmAPIFn(records);
                }
            }
            if (this.curTab !== 'add' && obj.key === 'termination') {
                let records = this.getSelectionData(obj.selection.from, obj.selection.to);
                this.checkOrderStopFn(records) && this.orderStopAPIFn(records);
            }
            if (this.curTab !== 'add' && obj.key === 'delete') {
                let records = this.getSelectionData(obj.selection.from, obj.selection.to);
                if (records.length <= 0) {
                    this.$warning({
                        title: '操作提示',
                        content: this.$Utils.tips().noCheckbox,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return;
                }
                if (!this.removeSelfCreateOrganizationTypeFn(records)) {
                    this.$warning({
                        title: '操作提示',
                        content: '只能删除自建的产品',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return;
                }
                if (!records.every((item) => parseInt(item.status) === 0 || parseInt(item.status) === 1)) {
                    this.$warning({
                        title: '操作提示',
                        content: '只能删除【草稿和正式】状态的订单。',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return;
                }
                this.orderDelAPIFn(records);
            }
            if ((this.curTab === 'add' && obj.key === 'copy') || this.curTab === 'add' && obj.key === 'cut') {
                let records = this.getSelectionData(obj.selection.from, obj.selection.to);
                this.copied.flag = true;
                let objs = [];
                records.forEach((r) => {
                    objs.push(Object.assign({}, r));
                });
                this.copied.objs = objs;
                console.log(this.copied.objs);
                if (obj.key === 'cut') {
                    this.deleteAddData(obj);
                }
            }
            if (this.curTab === 'add' && obj.key === 'paste') {
                this.copied.objs.forEach((r, idx) => {
                    this.addData[obj.selection.from + idx] = Object.assign({}, r);
                });
                this.addData.map((d, idx) => {
                    d.idx = idx + 1;
                    return d;
                });
            }
        },
        searchComData(obj) {
            if (obj.type === 'dropdown') {
                this.orderVendorListAPIFn(obj.value);
            }
        },
        earnOrgType(value) {
            const planListStatus = ['自建', '卖方', '已审核', '已确认', '已终止', '已完结'];
            return planListStatus[value];
        },
        chooseComData(obj) {
            if (obj.type === 'dropdown') {
                if (obj.value === 'search-more') {
                    this.editCellRow = obj.row;
                    this.dblclickTableFn(null);
                } else if (obj.value === 'tips') {
                    return;
                } else {
                    this.selectedVen = Object.assign(
                        {},
                        this.venTableData.find((v) => v.key === obj.value)
                    );
                    this.venUnitTreeFn(this.selectedVen);
                    this.addData[obj.row] = {
                        idx: obj.row + 1,
                        ...this.selectedVen,
                        unit_price: this.selectedVen.unit_price ? this.selectedVen.unit_price : 1,
                        product_vendor_name: this.selectedVen.product_vendor_name ? this.selectedVen.product_vendor_name : this.selectedVen.partB_name,
                        rate_string: this.reNumber(this.selectedVen.rate * 100) + '%',
                        status_string: this.earnStatus(this.selectedVen.status, 'planOrder')
                    };
                }
            }
        },
        getSelectionData(from, to) {
            let records = [];
            for (let i = from; i <= to; i ++) {
                records.push(this.addData[i]);
            }
            return records;
        },
        resetPaginations() {
            this.sheetPagination = {
                loading: false,
                total: 30,
                current: 1, // 对应后端的 pageSize
                pageSize: 30, // 对应后端的 pageNum
                pageNum: 1,
                available: true
            };
        },
        resetSheetData() {
            this.addData = [];
            this.widths = [];
            this.columns = [];
            this.resetPaginations();
        },
        sheetScrollToEnd() {
            if (this.curTab !== 'add') {
                if (this.sheetPagination.available) {
                    this.sheetPagination.available = false;
                    switch (this.curTab) {
                        case 'executing':
                            this.listStatuType = arrivalOrderTabMap.EXECUTING.value;
                            break;
                        case 'receipt':
                            this.listStatuType = arrivalOrderTabMap.HISTORY.value;
                            break;
                    }
                    this.listLoader();
                }
            }
        },
        changeTabFn(key = 'add') {
            this.curTab = key;
            // reset Sheet
            this.resetSheetData();
            // actions
            switch (key) {
                case 'executing':
                    this.actColumns = this.executingColumns.map((item) => {
                        return item;
                    });
                    this.actColumns.push({
                        data: 'arrival_status',
                        type: 'input',
                        disabled: true,
                        name: '状态'
                    });
                    break;
                case 'receipt':
                    this.actColumns = this.executingColumns.map((item) => {
                        return item;
                    });
                    this.actColumns.push({
                        data: 'is_cancel',
                        type: 'input',
                        disabled: true,
                        name: '是否作废'
                    });
                    this.actColumns.push({
                        data: 'cancel_reason',
                        type: 'input',
                        disabled: true,
                        name: '作废理由'
                    });
                    this.actColumns.push({
                        data: 'arrival_status',
                        type: 'input',
                        disabled: true,
                        name: '状态'
                    });
                    break;
                default:
                    //add
                    if (this.editable.flag && this.editable.type === 'view') {
                        let columns = [];
                        this.addColumns.forEach((c) => {
                            c.disabled = true;
                            columns.push(c);
                        });
                        this.columns = columns;
                    } else {
                        this.columns = this.addColumns;
                    }
                    break;
            }
            // checking headers & widths
            let cookieData = JSON.parse(this.cookieInfo.get(this.pageName + '-' + this.curTab));
            if (cookieData !== null && cookieData.headers !== undefined && cookieData.headers.length > 0) {
                let columns = [];
                this.actColumns.forEach((c) => {
                    if (cookieData.headers.includes(c.data) > 0) {
                        columns.push(c);
                    }
                });
                this.columns = columns;
            } else {
                switch (key) {
                    case 'add':
                        if (this.editable.flag && this.editable.type === 'view') {
                            let columns = [];
                            this.addColumns.forEach((c) => {
                                c.disabled = true;
                                columns.push(c);
                            });
                            this.columns = columns;
                        } else {
                            this.columns = this.addColumns;
                        }
                        break;
                    case 'executing':
                        this.listStatuType = arrivalOrderTabMap.EXECUTING.value;
                        this.columns = this.executingColumns.map((item) => {
                            return item;
                        });
                        this.columns.push({
                            data: 'arrival_status',
                            type: 'header',
                            disabled: false,
                            name: '状态'
                        });
                        break;
                    case 'receipt':
                        this.listStatuType = arrivalOrderTabMap.HISTORY.value;
                        this.columns = this.executingColumns.map((item) => {
                            return item;
                        });
                        this.columns.push({
                            data: 'is_cancel',
                            type: 'header',
                            disabled: false,
                            name: '是否作废'
                        });
                        this.columns.push({
                            data: 'cancel_reason',
                            type: 'header',
                            disabled: false,
                            name: '作废理由'
                        });
                        this.columns.push({
                            data: 'arrival_status',
                            type: 'header',
                            disabled: false,
                            name: '状态'
                        });
                        break;
                }
            }
            if (cookieData !== null && cookieData.widths !== undefined && cookieData.widths.length > 0) {
                this.widths = cookieData.widths;
            }
            // initial menu
            this.setupMenu(key);
            // actions
            switch (key) {
                case 'executing':
                case 'receipt':
                    //searchable
                    this.totalFormat.searchable = true;
                    // load
                    this.listLoader();
                    break;
                default:
                    //searchable
                    this.totalFormat.searchable = false;
                    //add
                    if (this.editable.flag) {
                        this.panes[0].title = this.editable.type === 'revise' ? '编辑' : '浏览';
                        this.loadOrderDetail(this.editable.obj.id);
                    } else {
                        this.panes[0].title = '调单收货';
                        let data = [];
                        for (let i = 0; i < 10; i ++) {
                            data.push({
                                idx: i + 1,
                                product_vendor_name: '',
                                product_name: '',
                                specification: '',
                                unit_inner_sn: '',
                                order_quantity: '',
                                batch_number: '',
                                arrival_quantity: '',
                                barcode: ''
                            });
                            this.addData = data;
                        }
                    }
                    break;
            }
        },
        addNewSheetRow() {
            if (this.curTab === 'add') {
                this.addData.push({
                    idx: this.addData.length + 1,
                    product_vendor_name: '',
                    product_name: '',
                    specification: '',
                    unit_inner_sn: '',
                    order_quantity: '',
                    batch_number: '',
                    arrival_quantity: '',
                    barcode: ''
                });
            }
        },
        searchSheetFn(obj) {
            if (this.curTab !== 'add') {
                this.addData = [];
                this.resetPaginations();
                switch (this.curTab) {
                    case 'executing':
                        this.listStatuType = arrivalOrderTabMap.EXECUTING.value;
                        break;
                    case 'receipt':
                        this.listStatuType = arrivalOrderTabMap.HISTORY.value;
                        break;
                }
                this.listLoader(obj.name);
            }
        },
        multiSelected(obj) {
            if (this.curTab !== 'add') {
                let data = [],
                    disabled = obj.from !== obj.to;
                this.menu.data.forEach((m) => {
                    m.disabled = disabled;
                    data.push(m);
                });
                this.menu.data = data;
            }
            if (this.curTab === 'add') {
                this.menu.data[2].disabled = !this.copied.flag;
            }
        },
        getStableData() {
            let data = [];
            this.addData.forEach((item) => {
                if (item.product_name !== '') {
                    data.push(item);
                }
            });
            return data;
        },
        /**
         * 简单查询
         */
        onHeaderSingleSearch() {
            this.handleSearch({ sn: this.searchValues.sn });
        },

        /**
         * 多条件查询
         */
        onHeaderMultipleSearch() {
            this.handleSearch();
        },

        onSearchFormOrganizationSelected(organizations) {
            if (organizations && organizations.length > 0) {
                this.searchValues.organizationName = organizations[0].organizationName;
                this.searchValues.organizationInnerSn = organizations[0].organizationInnerSn;
                this.searchValues.billsType = organizations[0].billsType;
            }
            this.handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER);
        },

        onSearchFormOrganizationClear() {
            this.searchValues.organizationName = undefined;
            this.searchValues.organizationInnerSn = undefined;
            this.searchValues.billsType = undefined;
        },

        onOrderAddFinish() {
            this.selectedRecords = [];
            this.handleSearch();
        },

        onOrderEditFinish() {
            this.selectedRecords = [];
            this.handleSearch();
        },

        onOrderApprovalFinish() {
            this.selectedRecords = [];
            this.handleSearch();
        },

        onOrderCancelFinish() {
            this.selectedRecords = [];
            this.handleSearch();
        },

        onRowDoubleClick(row) {
            this.orderEditorTarget = row;
            this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
        },

        onHeaderButtonClick(key) {
            switch (key) {
                case 'add':
                    this.orderEditorTarget = null;
                    this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
                    break;
                case 'edit':
                case 'receive':
                case 'approval':
                case 'cancel':
                    if (this.selectedRecords[0]) {
                        this.orderEditorTarget = this.selectedRecords[0];
                        this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
                    }
                    break;
                case 'delete':
                    this.$confirm({
                        title: '确定要删除单据吗？',
                        onOk: () => {
                            if (!this.selectedRecords[0]) {
                                throw new Error('无目标单据');
                            }
                            this.deleteArrivalOrder(this.selectedRecords[0].id);
                        },
                        okText: '确定',
                        cancelText: '取消'
                    });
                    break;
            }
        },

        onSelectedChange(selected) {
            this.selectedRecords = selected;
        },

        /**
         * 查询列表
         * @param {object | undefined} values 查询条件，未定义时以当前状态进行查询
         */
        handleSearch(values) {
            if (this.$refs[arrivalOrderTabMap[this.activeTabKey].value]) {
                this.$refs[arrivalOrderTabMap[this.activeTabKey].value].handleSearch(values);
            }
        },

        /**
         * 删除到货单
         * @param {string | number} id 单据id
         */
        async deleteArrivalOrder(id) {
            try {
                this.loading = true;
                const { code, msg } = await arrivalOrderDeleteAPI(id);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('删除成功');
                this.selectedRecords = [];
                this.$refs[arrivalOrderTabMap[this.activeTabKey].value].handleRemoveRecordByKeyValue('id', id);
            } catch (err) {
                this.$message.error('删除失败');
                throw err;
            } finally {
                this.loading = false;
            }
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
        },

        onOrganizationSelected(organizations) {
            if (organizations && organizations.length > 0) {
                this.supplySearch = organizations[0].organizationName;
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
                this.onProductAdd({ arrivalOrderProducts: products });
                this.$nextTick(() => {
                    this.$refs.xTable.scrollTo(124);
                    this.$refs.xTable.setActiveCell(this.dataEditingDetails[insertIndex], 'barcode');
                });
                return;
            }
            this.barcodeProductsToFilter = products;
            this.handleOpenModals(modalsKeyMap.BARCODE_PRODUCT_FILTER);
        },

        onBarcodeProductFilterSubmit(products) {
            const insertIndex = Math.max(0, this.dataEditingDetails.length - 1); // 最后一行为inputRow
            this.onProductAdd({ arrivalOrderProducts: products });
            this.handleCloseModals(modalsKeyMap.BARCODE_PRODUCT_FILTER);
            setTimeout(() => {
                this.$refs.xTable.scrollTo(124);
                this.$refs.xTable.setActiveCell(this.dataEditingDetails[insertIndex], 'barcode');
            }, 0);
        },

        /**
         * 产品选择返回值时
         * @param {object} data 产品数据
         * @param {array | undefined} data.arrivalOrderProducts 收货单可用产品
         */
        onProductAdd(data) {
            // 收货单可用产品
            if (data.arrivalOrderProducts) {
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
                while (i < data.arrivalOrderProducts.length) {
                    let t = data.arrivalOrderProducts[i];
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
                    countAdd += 1;
                    this.dataEditingDetails.splice(Math.max(this.dataEditingDetails.length - 1, 0), 0, {
                        ...t,
                        arrival_quantity: t.order_quantity,
                        voucher_inner_sn_c: t.id,
                        voucher_type_c: orderTypeMap.PO_RECORDS.key,
                        unitSelecterOptions: [
                            {
                                label: t.unit,
                                name: t.unit,
                                value: t.unit_inner_sn
                            }
                        ]
                    });
                }
                if (countAdd !== data.arrivalOrderProducts.length) {
                    this.$message.warning(
                        `与当前所设置供货单位和仓库不符合的产品（${
                            data.arrivalOrderProducts.length - countAdd
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
            this.supplySearch = targetOrder.organization_name;
            this.dataEditing.isCancel = targetOrder.is_cancel;
            this.dataEditing.sn = targetOrder.sn;
            this.dataEditing.billsType = targetOrder.bills_type;
            this.dataEditing.organizationName = targetOrder.organization_name;
            this.dataEditing.organizationInnerSn = targetOrder.organization_inner_sn;
            this.dataEditing.warehouseName = targetOrder.warehouse_name;
            this.dataEditing.warehouseInnerSn = targetOrder.warehouse_inner_sn;
            this.dataEditing.deliveryMenName = targetOrder.deliverymen_name;
            this.dataEditing.description = targetOrder.description;
            this.dataEditing.createdBy = targetOrder.created_by;
            this.dataEditing.createdTime = targetOrder.created_time;
            this.dataEditing.arrivalBy = targetOrder.arrival_by;
            this.dataEditing.arrivalTime = targetOrder.arrival_time;
            this.dataEditing.confirmBy = targetOrder.confirm_by;
            this.dataEditing.confirmTime = targetOrder.confirm_time;
            this.dataEditing.cancelBy = targetOrder.cancel_by;
            this.dataEditing.cancelTime = targetOrder.cancel_time;
            this.dataEditing.cancelReason = targetOrder.cancel_reason;
            if (targetOrder.details) {
                let data = [];
                this.dataEditingDetails = targetOrder.details.map((product) => {
                    let item = {
                        ...product,
                        vendor_inner_sn: product.product_vendor_inner_sn,
                        vendor_name: product.product_vendor_name,
                        unitSelecterOptions: [
                            {
                                name: product.unit,
                                value: product.unit_inner_sn,
                                label: product.unit
                            }
                        ]
                    };
                    item.unit_inner_sn = item.unit;
                    data.push(item);
                    return item;
                });
                this.addData = data;
                if (!this.isReadonly) {
                    this.dataEditingDetails.push({ rowMark: 'inputerRow' });
                }
            } else {
                this.dataEditingDetails = [{ rowMark: 'inputerRow' }];
            }
        },
        /**
         * 数据校验
         */
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
            data.details.forEach((product) => {
                if (!product.unit || !product.unit_inner_sn) {
                    this.$message.error('错误的产品单位');
                    throw new Error('unit');
                }
                if (!(Number(product.order_quantity) > 0)) {
                    this.$message.error('订单数量必须大于0');
                    throw new Error('order_quantity');
                }
                if (!(Number(product.arrival_quantity) > 0)) {
                    this.$message.error('收货数量必须大于0');
                    throw new Error('arrival_quantity');
                }
            });
        },

        /**
         * 保存
         * @param {boolean} isReceive 是否为收货保存
         */
        async handleSave(isReceive = false) {
            const data = {
                id: undefined,
                arrival_status: undefined,
                bills_type: this.dataEditing.billsType,
                organization_name: this.dataEditing.organizationName,
                organization_inner_sn: this.dataEditing.organizationInnerSn,
                warehouse_name: this.dataEditing.warehouseName,
                warehouse_inner_sn: this.dataEditing.warehouseInnerSn,
                deliverymen_name: this.dataEditing.deliveryMenName,
                deliverymen_inner_sn: this.dataEditing.deliveryMenInnerSn,
                description: this.dataEditing.description,
                details: this.dataEditingDetails
                    .filter((product) => product.rowMark !== 'inputerRow')
                    .map((product) => {
                        let unit = '';
                        if (product.unitSelecterOptions) {
                            for (let i = 0, l = product.unitSelecterOptions.length; i < l; i += 1) {
                                if (product.unitSelecterOptions[i].value === product.unit_inner_sn) {
                                    unit = product.unitSelecterOptions[i].name;
                                }
                            }
                        }
                        return { ...product, unit };
                    })
            };
            this.handleCheckData(data);
            // 新增、新增并收货
            if (this.isAddOrder) {
                data.arrival_status = isReceive
                    ? arrivalOrderStatusMap.RECEIVED.value
                    : arrivalOrderStatusMap.NOT_RECEIVED.value;
                await this.addOrder(data);
                this.$emit('addFinish', data);
            }
            // 编辑、收货
            if (this.isEditOrder) {
                data.id = this.targetOrder.id;
                data.arrival_status = isReceive
                    ? arrivalOrderStatusMap.RECEIVED.value
                    : this.targetOrder.arrival_status;
                await this.editOrder(data);
                this.$emit('editFinish', data);
            }
            this.handleClose();
        },

        /**
         * 收货
         */
        handleReceive() {
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
            if (column.property === 'unit_inner_sn' || column.property === 'order_quantity') {
                if (row.voucher_type_c === orderTypeMap.PO_RECORDS.key) {
                    return false;
                }
            }
            return true;
        },

        /**
         * 加载收货单详情
         * @param {string | number} id 收货单id
         */
        async loadOrderDetail(id) {
            try {
                this.detailLoading = true;
                const { code, msg, info } = await arrivalOrderDetailAPI(id);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.handleInit(info);
            } catch (err) {
                this.$message.error('收货单详情获取失败');
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
                const { code, info, msg } = await arrivalOrderProductListBarcodeAPI({ barcode });
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
         * 创建收货单
         */
        async addOrder(data) {
            try {
                this.buttonsLoading.save = true;
                const { code, msg } = await arrivalOrderAddAPI(data);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('收货单创建成功');
            } catch (err) {
                this.$message.error('收货单创建失败');
                throw err;
            } finally {
                this.buttonsLoading.save = false;
            }
        },

        /**
         * 编辑收货单
         */
        async editOrder(data) {
            try {
                this.buttonsLoading.save = true;
                const { code, msg } = await arrivalOrderEditAPI(data);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('收货单保存成功');
            } catch (err) {
                this.$message.error('收货单保存失败');
                throw err;
            } finally {
                this.buttonsLoading.save = false;
            }
        },

        /**
         * 复核收货单
         */
        async approvalOrder(id) {
            try {
                this.buttonsLoading.approval = true;
                const { code, msg } = await arrivalOrderApprovalAPI(id);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('收货单复核成功');
            } catch (err) {
                this.$message.error('收货单复核失败');
                throw err;
            } finally {
                this.buttonsLoading.approval = false;
            }
        },

        /**
         * 作废收货单
         * @param {string | number} id 单据id
         * @param {string} reason 作废原因
         */
        async cancelOrder(id, reason) {
            this.buttonsLoading.cancel = true;
            try {
                this.loading = true;
                const { code, msg } = await arrivalOrderCancelAPI(id, reason);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('作废收货单成功');
            } catch (err) {
                if (err.message) {
                    this.$message.error(err.message);
                } else {
                    this.$message.error('作废收货单失败');
                }
                throw err;
            } finally {
                this.buttonsLoading.cancel = false;
            }
        },
        /**
         * 关闭编辑器
         */
        handleClose() {
            this.$emit('close');
        },
        /**
         * 加载列表数据
         */
        async listLoader(sn = '') {
            // const { sn, dateRange = [], billsType, organizationInnerSn, deliveryMenName } = searchValues;
            // const [beginDate, endDate] = dateRange;
            let loaderData = {
                history: this.listStatuType === arrivalOrderTabMap.HISTORY.value,
                sn,
                // 'begin_date': beginDate ? beginDate.format('YYYY-MM-DD') : undefined,
                // 'end_date': endDate ? endDate.format('YYYY-MM-DD') : undefined,
                // bills_type: billsType,
                // organization_inner_sn: organizationInnerSn,
                // deliverymen_name: deliveryMenName,
                // 质管复核页面，执行中tab页显示的为已收货
                // qualityControl: this.managerMode
            };
            // if (
            //     organizationInnerSn === undefined ||
            //     billsType === undefined ||
            //     organizationInnerSn === null ||
            //     billsType === null
            // ) {
            //     loaderData.bills_type = undefined;
            //     loaderData.organization_inner_sn = undefined;
            // }
            await arrivalOrderListAPI(this.sheetPagination.pageNum, this.sheetPagination.pageSize, loaderData)
            .then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res;
                    let startIdx = this.sheetPagination.pageSize * (this.sheetPagination.pageNum - 1);
                    this.calcPagination(total);
                    let data = rows.map((item, index) => {
                        return {
                            ...item,
                            idx: startIdx + index + 1,
                            key: Math.random(),
                            is_cancel: arrivalOrderCancelTextFilter(item.is_cancel),
                            arrival_status: arrivalOrderStatusTextFilter(item.arrival_status)
                        };
                    });
                    this.addData.push(...data);
                } else {
                    this.$message.error(res.msg);
                }
            });
        }
    },
    mounted() {
        this.loadSupplyUnits();
        this.changeTabFn();
    }
};
</script>
