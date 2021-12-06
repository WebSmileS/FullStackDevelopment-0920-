<template>
    <and-page-layout class="scoped scoped-white grn-sheets">
        <template slot="breadcrumb">
            <a-breadcrumb style="float: left">
                <a-breadcrumb-item>采购管理</a-breadcrumb-item>
                <a-breadcrumb-item>验货</a-breadcrumb-item>
            </a-breadcrumb>
        </template>
        <template slot="tabs-content">
            <and-tabs-content :panes="panes" :cur="curTab" @changeTabFn="changeTabFn">
                <template slot="atc-header">
                    <a-row class="atc-header" v-if="curTab === 'add'">
                        <a-col :span="24">
                            <h2>{{ organizationNameLogined }}&nbsp;&nbsp;验货单&nbsp;&nbsp;&nbsp;
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
                                <base-input-with-clear-button
                                    size="small"
                                    :value="dataEditing.organizationName"
                                    :disabled="editable.flag && editable.type === 'view'"
                                    @click="handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                                    @clear="onOrganizationClear"
                                ></base-input-with-clear-button>
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
                                    v-model="dataEditing.description"
                                    :disabled="editable.flag && editable.type === 'view'"
                                    placeholder="采购员"
                                    class="input"
                                />
                            </a-col>
                            <a-col :span="24" style="margin-top: 5px;text-align: left">
                                <a-col :span="4">
                                    <span class="label" style="width: auto">制单人:&nbsp;</span>
                                    <span>{{ dataEditing.createdBy | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4">
                                    <span class="label" style="width: auto">生成时间：&nbsp;</span>
                                    <span>{{ dataEditing.createdTime | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4">
                                    <span class="label" style="width: auto">验货人：&nbsp;</span>
                                    <span>{{ dataEditing.inspectionBy | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4">
                                    <span class="label" style="width: auto">验货时间：&nbsp;</span>
                                    <span>{{ dataEditing.inspectionTime | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4">
                                    <span class="label" style="width: auto">复核人：&nbsp;</span>
                                    <span>{{ dataEditing.confirmBy | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4">
                                    <span class="label" style="width: auto">复核时间：&nbsp;</span>
                                    <span>{{ dataEditing.confirmTime | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4" v-if="dataEditing.isCancel">
                                    <span class="label" style="width: auto">作废人：&nbsp;</span>
                                    <span>{{ dataEditing.cancelBy | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="4" v-if="dataEditing.isCancel">
                                    <span class="label" style="width: auto">作废时间：&nbsp;</span>
                                    <span>{{ dataEditing.cancelTime | textNullDataFilter }}</span>
                                </a-col>
                                <a-col :span="16" v-if="dataEditing.isCancel">
                                    <span class="label" style="width: auto">作废原因：&nbsp;</span>
                                    <span>{{ dataEditing.cancelReason | textNullDataFilter }}</span>
                                </a-col>
                            </a-col>
                        </a-row>
                        <a-row>
                            <a-col :span="24" style="text-align: right">
                                <a-button
                                    @click="onSaveButtonClick"
                                    type="primary"
                                    :disabled="editable.flag && editable.type === 'view'"
                                >保存</a-button>
                                <a-button
                                    @click="handleInspect"
                                    type="primary"
                                    style="margin-left: 5px"
                                    :disabled="editable.flag && editable.type === 'view'"
                                >验货</a-button>
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
                @submit="onSearchFormOrganizationSelected"
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
    </and-page-layout>
</template>

<script>
// import newDesign Layount
import AndPageLayout from '@/components/AndLayout/AndPageLayout';
import AndTabsContent from '@/components/AndLayout/AndTabsContent';
// import components
import AndSheetActs from '@/components/purchase/AndSheetActs';
import AndSheet from '@/components/AndRenders/AndSheet';

import BaseModalTextInput from '@/components/BaseModalTextInput';
import ProductSelecterModal from '@/components/ProductSelecterModal';
import BaseInputWithClearButton from '@/components/BaseInputWithClearButton';
import ItemSelecterTableModal from '@/components/ItemSelecterTableModal';
import tableColumnConfig from '@/config/tableColumnConfig';
import textNullDataFilter from '@/filters/textNullData';

import productUnitOptionsMixin from '@/mixins/productUnitOptions';
import mdrfOptionsMixin from '@/mixins/mdrfOptions';
import barcodeScanAPIResultParser from '@/mixins/barcodeScanAPIResultParser';
import barcodeInputFinish from '@/mixins/barcodeInputFinish';
import {
    orderTypeMap
} from '@/config/constants';
import ProductSelecterInspection from './ProductSelecterInspection';
import BarcodeInputerInspection from './BarcodeInputerInspection';
import productColumns from './inspectionOrderEditorColumns';
import productSelecterInspectionColumns from './productSelecterInspectionColumns.js';
import {
    inspectionOrderStatusMap,
    inspectionOrderTabMap
} from './type';
import {
    inspectionOrderStatusTextFilter,
    inspectionOrderCancelTextFilter
} from './filters';


import tabsMixin from '@/mixins/tabs';
import modalsMixin from '@/mixins/modals';
import OrganizationSelecter from './OrganizationSelecter';
import {
    inspectionOrderDeleteAPI,
    inspectionOrderAddAPI,
    inspectionOrderApprovalAPI,
    inspectionOrderCancelAPI,
    inspectionOrderDetailAPI,
    inspectionOrderEditAPI,
    inspectionOrderProductListBarcodeAPI,
    inspectionOrderListAPI,
    inspectionOrderProductListAPI,
    grnOrgListAPI,
    platformUnitTree
} from '@/service/pageAjax';

const modalsKeyMap = Object.freeze({
    ORGANIZATION_SELECTER: 'g',
    ORDER_EDITOR: 'o',
    PRODUCT_SELECTER: 'p',
    CANCEL_REASON: 'c',
    BARCODE_PRODUCT_FILTER: 'b'
});

const buttonsMap = {
    add: { key: 'add', text: '调单验货', type: 'primary' },
    edit: { key: 'edit', text: '修改', type: 'primary' },
    approval: { key: 'approval', text: '复核', type: 'primary' },
    inspect: { key: 'inspect', text: '验货', type: 'primary' },
    cancel: { key: 'cancel', text: '作废', type: 'danger' },
    delete: { key: 'delete', text: '删除', type: 'danger' }
};

export default {
    name: 'InspectionOrder',
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
        ProductSelecterInspection,
        BaseInputWithClearButton,
        BarcodeInputerInspection,
        ItemSelecterTableModal
    },
    mixins: [
        tabsMixin,
        modalsMixin,
        productUnitOptionsMixin,
        mdrfOptionsMixin,
        barcodeScanAPIResultParser,
        barcodeInputFinish
    ],
    filters: {
        textNullDataFilter,
        inspectionOrderStatusTextFilter,
        inspectionOrderCancelTextFilter
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
                    key: 'inspection',
                    title: '验货记录'
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
                    type: 'dropdown',
                    disabled: false,
                    name: '厂商名称'
                },
                {
                    data: 'product_name',
                    type: 'input',
                    disabled: true,
                    name: '产品名称'
                },
                {
                    data: 'specification',
                    type: 'input',
                    disabled: true,
                    name: '规格型号'
                },
                {
                    data: 'unit',
                    type: 'input',
                    disabled: true,
                    name: '包装单位'
                },
                {
                    data: 'arrival_quantity',
                    type: 'number',
                    disabled: true,
                    name: '收货数量'
                },
                {
                    data: 'batch_number',
                    type: 'input',
                    disabled: false,
                    name: '批号'
                },
                {
                    data: 'qualified_quantity',
                    type: 'number',
                    disabled: false,
                    name: '合格数量'
                },
                {
                    data: 'unqualified_quantity',
                    type: 'number',
                    disabled: false,
                    name: '不合格数量'
                },
                {
                    data: 'unqualified_reason',
                    type: 'input',
                    disabled: false,
                    name: '不合格原因及处理意见'
                },
                {
                    data: 'mdrf_inner_sn',
                    type: 'number',
                    disabled: false,
                    name: '注册证'
                },
                {
                    data: 'produce_date',
                    type: 'input',
                    disabled: false,
                    name: '生产日期'
                },
                {
                    data: 'overdue_date',
                    type: 'input',
                    disabled: false,
                    name: '失效日期'
                },
                {
                    data: 'barcode',
                    type: 'input',
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
                    name: '供货单位'
                },
                {
                    data: 'warehouse_name',
                    type: 'input',
                    disabled: true,
                    name: '仓库'
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
                    data: 'inspection_by',
                    type: 'input',
                    disabled: true,
                    name: '验货人'
                },
                {
                    data: 'inspection_time',
                    type: 'input',
                    disabled: true,
                    name: '验货时间'
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
            // 通知列表的状态类型
            listStatuType: {
                type: String,
                default: inspectionOrderTabMap.EXECUTING.value
            },

            modalsKeyMap,
            loading: false,
            searchValues: {
                sn: undefined,
                organizationName: undefined,
                organizationInnerSn: undefined,
                dateRange: []
            },
            activeTabKey: inspectionOrderTabMap.EXECUTING.value,
            selectedRecords: [],
            orderEditorTarget: null, // 验货单编辑目标
            tableColumnConfig,
            productSelecterInspectionColumns,
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
            return this.orderEditorTarget === null;
        },

        /**
         * 是否为编辑验货单
         */
        isEditOrder() {
            if (this.orderEditorTarget && this.orderEditorTarget.is_cancel) {
                return false;
            }
            return this.orderEditorTarget !== null;
        },

        /**
         * 是否为复核验货单
         */
        isApprovalOrder() {
            if (this.orderEditorTarget && this.orderEditorTarget.is_cancel) {
                return false;
            }
            return (
                this.managerMode &&
                this.orderEditorTarget &&
                this.orderEditorTarget.inspection_status === inspectionOrderStatusMap.INSPECTED.value
            );
        },

        /**
         * 是否只读
         */
        isReadonly() {
            if (this.orderEditorTarget && this.orderEditorTarget.is_cancel) {
                return true;
            }
            if (this.managerMode) {
                return true;
            }
            if (this.orderEditorTarget) {
                if (
                    this.orderEditorTarget.inspection_status === inspectionOrderStatusMap.INSPECTED.value ||
                    this.orderEditorTarget.inspection_status === inspectionOrderStatusMap.REVIEWED.value
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
        inspectionOrderTabMap() {
            return inspectionOrderTabMap;
        },
        operationGroups() {
            if (this.managerMode) {
                return [[buttonsMap.approval], [buttonsMap.cancel]];
            }
            return [[buttonsMap.add, buttonsMap.edit, buttonsMap.inspect], [buttonsMap.delete]];
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false },
                edit: { disabled: false, reason: '' },
                inspect: { disabled: false, reason: '' },
                approval: { disabled: false, reason: '' },
                cancel: { loading: this.loading, disabled: false, reason: '' },
                delete: { loading: this.loading, disabled: false, reason: '' }
            };
            if (this.selectedRecords.length !== 1) {
                result.edit.disabled = true;
                result.inspect.disabled = true;
                result.approval.disabled = true;
                result.cancel.disabled = true;
                result.delete.disabled = true;
                result.edit.reason =
                    result.inspect.reason =
                    result.approval.reason =
                    result.cancel.reason =
                    result.delete.reason =
                        '选中1条记录才能进行操作';
            }
            if (this.activeTabKey !== inspectionOrderTabMap.EXECUTING.value) {
                result.edit.disabled = true;
                result.inspect.disabled = true;
                result.approval.disabled = true;
                result.cancel.disabled = true;
                result.delete.disabled = true;
                result.edit.reason =
                    result.inspect.reason =
                    result.approval.reason =
                    result.cancel.reason =
                    result.delete.reason =
                        '执行中的记录才能进行操作';
            }
            return result;
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
                this.partB.oname = obj.name;
                this.chooseOrgFn(obj);
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
                case 'executing':
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
                                key: 'inspection',
                                name: '验货'
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
                case 'inspection':
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
                    unit: '',
                    arrival_quantity: '',
                    batch_number: '',
                    qualified_quantity: '',
                    unqualified_reason: '',
                    mdrf_inner_sn: '',
                    produce_date: '',
                    overdue_date: '',
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
            if ((this.curTab !== 'add' && (obj.key === 'revise' || obj.key === 'inspection')) || (this.curTab === 'inspection' && obj.key === 'view')) {
                this.orderEditorTarget = this.addData[obj.selection.from];
                this.editable = {
                    flag: true,
                    type: (obj.key === 'revise' || obj.key === 'inspection') ? 'revise' : 'view',
                    obj: this.addData[obj.selection.from]
                };
                this.changeTabFn();
            }
            if (this.curTab !== 'add' && obj.key === 'delete') {
                this.$confirm({
                    title: '确定要删除单据吗？',
                    onOk: () => {
                        this.deleteInspectionOrder(this.addData[obj.selection.from].id);
                    },
                    okText: '确定',
                    cancelText: '取消'
                });
            }
            let records = this.getSelectionData(obj.selection.from, obj.selection.to);
            if ((this.curTab === 'add' && obj.key === 'copy') || this.curTab === 'add' && obj.key === 'cut') {
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
                this.inspectionProductListFn(obj.value);
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
                    this.handleCloseModals(modalsKeyMap.PRODUCT_SELECTER);
                } else if (obj.value === 'tips') {
                    return;
                } else {
                    this.selectedVen = Object.assign(
                        {},
                        this.venTableData.find((v) => v.key === obj.value)
                    );
                    this.addData[obj.row] = {
                        idx: obj.row + 1,
                        ...this.selectedVen,
                        arrival_quantity: this.selectedVen.order_quantity,
                        qualified_quantity: this.selectedVen.order_quantity,
                        unqualified_quantity: 0,
                        voucher_inner_sn_c: this.selectedVen.id,
                        voucher_type_c: orderTypeMap.PO_RECORDS.key,
                        mdrf_sn: this.selectedVen.mdrf_sn,
                        mdrf_inner_sn: this.selectedVen.mdrf_inner_sn,
                        inspection_status: ''
                    };
                    // this.venUnitTreeFn(this.selectedVen);
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
                            this.listStatuType = inspectionOrderTabMap.EXECUTING.value;
                            break;
                        case 'inspection':
                            this.listStatuType = inspectionOrderTabMap.HISTORY.value;
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
                    this.actColumns.push({
                        data: 'descripton',
                        type: 'input',
                        disabled: true,
                        name: '备注'
                    });
                    break;
                case 'inspection':
                    this.actColumns = this.executingColumns.map((item) => {
                        return item;
                    });
                    this.actColumns.push({
                        data: 'is_cancel',
                        type: 'header',
                        disabled: true,
                        name: '是否作废'
                    });
                    this.actColumns.push({
                        data: 'cancel_reason',
                        type: 'header',
                        disabled: true,
                        name: '作废理由'
                    });
                    this.actColumns.push({
                        data: 'inspection_status',
                        type: 'input',
                        disabled: true,
                        name: '状态'
                    });
                    this.actColumns.push({
                        data: 'descripton',
                        type: 'input',
                        disabled: true,
                        name: '备注'
                    });
                    break;
                default:
                    //add
                    this.actColumns = this.addColumns;
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
                        this.listStatuType = inspectionOrderTabMap.EXECUTING.value;
                        this.columns = this.executingColumns.map((item) => {
                            return item;
                        });
                        this.columns.push({
                            data: 'arrival_status',
                            type: 'input',
                            disabled: true,
                            name: '状态'
                        });
                        this.columns.push({
                            data: 'descripton',
                            type: 'input',
                            disabled: true,
                            name: '备注'
                        });
                        break;
                    case 'inspection':
                        this.listStatuType = inspectionOrderTabMap.HISTORY.value;
                        this.columns = this.executingColumns.map((item) => {
                            return item;
                        });
                        this.columns.push({
                            data: 'is_cancel',
                            type: 'header',
                            disabled: true,
                            name: '是否作废'
                        });
                        this.columns.push({
                            data: 'cancel_reason',
                            type: 'header',
                            disabled: true,
                            name: '作废理由'
                        });
                        this.columns.push({
                            data: 'inspection_status',
                            type: 'input',
                            disabled: true,
                            name: '状态'
                        });
                        this.columns.push({
                            data: 'descripton',
                            type: 'input',
                            disabled: true,
                            name: '备注'
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
                case 'inspection':
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
                        this.panes[0].title = '调单验货';
                        let data = [];
                        for (let i = 0; i < 10; i ++) {
                            data.push({
                                idx: i + 1,
                                product_vendor_name: '',
                                product_name: '',
                                specification: '',
                                unit: '',
                                arrival_quantity: '',
                                batch_number: '',
                                qualified_quantity: '',
                                unqualified_reason: '',
                                mdrf_inner_sn: '',
                                produce_date: '',
                                overdue_date: '',
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
                    unit: '',
                    arrival_quantity: '',
                    batch_number: '',
                    qualified_quantity: '',
                    unqualified_reason: '',
                    mdrf_inner_sn: '',
                    produce_date: '',
                    overdue_date: '',
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
                        this.listStatuType = inspectionOrderTabMap.EXECUTING.value;
                        break;
                    case 'inspection':
                        this.listStatuType = inspectionOrderTabMap.HISTORY.value;
                        break;
                }
                this.listLoader(obj.name);
            }
        },
        multiSelected(obj) {
            if (this.curTab !== 'add' && this.curTab !== 'inspection') {
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
            let data = [],
                firstColumn = this.columns[1];
            this.addData.forEach((item) => {
                if (item[firstColumn.data] !== '') {
                    data.push(item);
                }
            });
            return data;
        },
        async venUnitTreeFn(record, type = 'add') {
            // 厂商商产品单位
            let oid = record.vendor_inner_sn;
            let pid = record.product_inner_sn;
            let psid = record.specification_inner_sn;
            await platformUnitTree(oid, pid, psid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let circle = (item, parentName) => {
                        // 递归遍历
                        item.forEach((item) => {
                            item.label = `${item.name}(${item.measure}×${parentName})`;
                            item.value = item.unit_inner_sn;
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item.name);
                            } else {
                                item.children = null;
                            }
                        });
                    };
                    res.tree.forEach((item) => {
                        item.label = item.name;
                        item.value = item.unit_inner_sn;
                        if (item.children && item.children.length > 0) {
                            circle(item.children, item.name);
                        } else {
                            item.children = null;
                        }
                    });
                    let unitArr;
                    unitArr = {
                        key: [res.tree[0].value],
                        data: res.tree
                    };
                    this.addData = this.addData.map((item) => {
                        if (item.key === record.key) {
                            item.unit_data = unitArr;
                            if (type === 'add') {
                                item.unit_inner_sn = [unitArr.key[0]];
                                item.unit_name = unitArr.data[0].name;
                            }
                        }
                        return item;
                    });
                }
            });
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
                this.dataEditing.organizationName = organizations[0].organizationName;
                this.dataEditing.organizationInnerSn = organizations[0].organizationInnerSn;
                this.dataEditing.billsType = organizations[0].billsType;
                this.dataEditing.organizationName = organizations[0].organizationName;
                this.inspectionProductListFn();
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
                case 'inspect':
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
                            this.deleteInspectionOrder(this.selectedRecords[0].id);
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
            if (this.$refs[inspectionOrderTabMap[this.activeTabKey].value]) {
                this.$refs[inspectionOrderTabMap[this.activeTabKey].value].handleSearch(values);
            }
        },

        /**
         * 删除验货单
         * @param {string | number} id 单据id
         */
        async deleteInspectionOrder(id) {
            try {
                this.loading = true;
                const { code, msg } = await inspectionOrderDeleteAPI(id);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('删除成功');
                this.selectedRecords = [];
                this.changeTabFn('executing');
            } catch (err) {
                this.$message.error('删除失败');
                throw err;
            } finally {
                this.loading = false;
            }
        },

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
                let data = [];
                this.dataEditingDetails = targetOrder.details.map((product, idx) => {
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
                    _product.idx = idx + 1;
                    data.push(Object.assign({}, _product));
                    return _product;
                });
                if (!this.isReadonly) {
                    this.dataEditingDetails.push({ rowMark: 'inputerRow' });
                }
                this.addData = data;
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
            };
            data.details = this.getStableData();
            this.handleCheckData(data);
            // 新增、新增并验货
            if (!this.editable.flag) {
                data.inspection_status = isInspect
                    ? inspectionOrderStatusMap.INSPECTED.value
                    : inspectionOrderStatusMap.NOT_INSPECTED.value;
                await this.addOrder(data);
            }
            // 编辑、验货
            if (this.editable.flag) {
                data.id = this.editable.obj.id;
                data.inspection_status = isInspect
                    ? inspectionOrderStatusMap.INSPECTED.value
                    : this.editable.obj.inspection_status;
                await this.editOrder(data);
            }
            if (isInspect) {
                this.changeTabFn('inspection');
            } else {
                this.changeTabFn('executing');
            }
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
        },
        /**
         * 加载列表数据
         */
        async listLoader(sn) {
            let loaderData = {
                history: this.listStatuType === inspectionOrderTabMap.HISTORY.value,
                sn,
                // 'begin_date': beginDate ? beginDate.format('YYYY-MM-DD') : undefined,
                // 'end_date': endDate ? endDate.format('YYYY-MM-DD') : undefined,
                // bills_type: billsType,
                // organization_inner_sn: organizationInnerSn,
                // 质管复核页面，执行中tab页显示的为已验货
                // qualityControl: this.managerMode
            };
            await inspectionOrderListAPI(this.sheetPagination.pageNum, this.sheetPagination.pageSize, loaderData)
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
                            inspection_status: inspectionOrderStatusTextFilter(item.inspection_status)
                        };
                    });
                    this.addData.push(...data);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /**
         * 产品列表数据加载器
         */
        async inspectionProductListFn(value) {
            try {
                this.loading = true;
                const { code, msg, rows, total } = await inspectionOrderProductListAPI(1, 10, {
                    bills_type: this.dataEditing.billsType,
                    organization_inner_sn: this.dataEditing.organizationInnerSn,
                    warehouse_inner_sn: this.dataEditing.warehouseInnerSn,
                    product_name: value
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.venTableData = rows.map((i) => {
                    i.key = (Math.random() + 1).toString(36).substring(7);
                    i.name = i.product_vendor_name;
                    return i;
                });
            } catch (err) {
                this.$message.error('产品查询失败');
                throw err;
            }
        }
    },
    mounted() {
        this.changeTabFn();
    }
};
</script>
