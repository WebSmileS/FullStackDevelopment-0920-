<template>
    <and-page-layout class="scoped scoped-white grn-sheets">
        <template slot="breadcrumb">
            <a-breadcrumb style="float: left">
                <a-breadcrumb-item>采购管理</a-breadcrumb-item>
                <a-breadcrumb-item>采购入库</a-breadcrumb-item>
            </a-breadcrumb>
        </template>
        <template slot="tabs-content">
            <and-tabs-content :panes="panes" :cur="curTab" @changeTabFn="changeTabFn">
                <template slot="atc-header">
                    <a-row class="atc-header" v-if="curTab === 'add'">
                        <purchase-grn-order-header
                            ref="grnOrderHeader"
                            :targetOrder="orderEditorTarget"
                            :targetOrderIsCopy="orderEditorTargetIsCopy"
                            @close="onOrderEditorClose"
                            @redFinish="onRedFinish"
                            @updateSheet="onUpdateSheet"
                            @updateOrgnization="onUpdateOrg"
                        >
                        </purchase-grn-order-header>
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
                    <purchase-grn-order-footer
                        v-if="curTab === 'add'"
                        ref="grnOrderFooter"
                        :targetOrder="orderEditorTarget"
                        :targetOrderIsCopy="orderEditorTargetIsCopy"
                        :data="addData"
                        :dataInfo="dataEditing"
                        @close="onOrderEditorClose"
                        @orderAdd="onOrderAdd"
                        @orderChange="onOrderChange"
                        @redFinish="onRedFinish"
                        @updateOrgnization="onUpdateOrg"
                    >
                    </purchase-grn-order-footer>
                </template>
            </and-tabs-content>
        </template>
        <template slot="modals">
            <purchase-grn-transfer-editor
                :visible="modalsVisible[modalsKeyMap.TRANSFER_EDITOR]"
                @close="handleCloseModals(modalsKeyMap.TRANSFER_EDITOR)"
                @submit="onTransferEditorSubmit"
            >
            </purchase-grn-transfer-editor>
        </template>
    </and-page-layout>
</template>

<script>
import Big from 'big.js';
// import newDesign Layount
import AndPageLayout from '@/components/AndLayout/AndPageLayout';
import AndTabsContent from '@/components/AndLayout/AndTabsContent';
// import components
import AndSheetActs from '@/components/purchase/AndSheetActs';
import AndSheet from '@/components/AndRenders/AndSheet';

import modalsMixin from '@/mixins/modals';
import PurchaseGrnTransferEditor from './PurchaseGrnTransferEditor';
import PurchaseGrnOrderHeader from './PurchaseGrnOrderHeader';
import PurchaseGrnOrderFooter from './PurchaseGrnOrderFooter';
import {
    grnPutinAPI,
    grnRedDashedAPI,
    grnUnreviewAPI,
    purchaseGrnDelAPI,
    getAllVendorListAPI,
    purchaseGrnListAPI,
    purchaseGrnInfoAPI,
    platformUnitTree
} from '@/service/pageAjax';
import {
    purchaseGrnOrderStatusMap,
    purchaseGrnOrderDetailActionTypeMap
} from './type';
import {
    orderTypeMap
} from '@/config/constants';
import {
    purchaseGrnOrderStatusTextFilter
} from './filters';

const modalsKeyMap = Object.freeze({
    ORDER_EDITOR: 'o',
    PRODUCT_SELECTER: 'p',
    TRANSFER_EDITOR: 't'
});

const buttonsMap = {
    add: { key: 'add', text: '新增', type: 'primary' },
    transfer: { key: 'transfer', text: '调单', type: 'primary' },
    edit: { key: 'edit', text: '修改', type: 'primary' },
    approval: { key: 'approval', text: '审核', type: 'primary' },
    unapproval: { key: 'unapproval', text: '取消审核', type: 'primary' },
    grn: { key: 'grn', text: '入库', type: 'primary' },
    copy: { key: 'copy', text: '复制', type: 'primary' },
    red: { key: 'red', text: '生成红冲', type: 'danger' },
    delete: { key: 'delete', text: '删除', type: 'danger' }
};

export default {
    name: 'PurchaseGrn',
    components: {
        // custom layounts
        AndPageLayout,
        AndTabsContent,
        // custom components
        AndSheet,
        AndSheetActs,
        // list components
        PurchaseGrnTransferEditor,
        PurchaseGrnOrderHeader,
        PurchaseGrnOrderFooter
    },
    mixins: [modalsMixin],
    data() {
        return {
            // init panes
            panes: [
                {
                    key: 'add',
                    title: '新增'
                },
                {
                    key: 'history',
                    title: '采购入库'
                }
            ],
            curTab: 'add',
            cookieInfo: this.$cookie,
            actColumns: [],
            addData: [],
            editable: {
                flag: false,
                obj: {}
            },
            historyColumns: [
                {
                    data: 'idx',
                    type: 'header',
                    disabled: true,
                    name: ''
                },
                {
                    data: 'grn_sn',
                    type: 'input',
                    disabled: true,
                    name: '单据编号'
                },
                {
                    data: 'organization_name',
                    type: 'input',
                    disabled: true,
                    name: '单位名称'
                },
                {
                    data: 'warehouse_name',
                    type: 'input',
                    disabled: true,
                    name: '仓库'
                },
                {
                    data: 'handler_name',
                    type: 'input',
                    disabled: true,
                    name: '经手人'
                },
                {
                    data: 'buyer_name',
                    type: 'input',
                    disabled: true,
                    name: '采购员'
                },
                {
                    data: 'warehouse_operator_name',
                    type: 'input',
                    disabled: true,
                    name: '仓库人员'
                },
                {
                    data: 'making_people_name',
                    type: 'input',
                    disabled: true,
                    name: '制单人'
                },
                {
                    data: 'in_time',
                    type: 'input',
                    disabled: true,
                    name: '入库时间'
                },
                {
                    data: 'status_string',
                    type: 'input',
                    disabled: true,
                    name: '状态'
                }
            ],
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
                    name: '厂商/经销商'
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
                    data: 'batch_number',
                    type: 'input',
                    disabled: false,
                    name: '批号'
                },
                {
                    data: 'quantity',
                    type: 'number',
                    disabled: false,
                    name: '数量'
                },
                {
                    data: 'unit_inner_sn',
                    type: 'cascader',
                    disabled: false,
                    name: '包装单位'
                },
                {
                    data: 'rate_string',
                    type: 'simpleDropdown',
                    list: [
                        {
                            key: 0,
                            name: '0%'
                        },
                        {
                            key: 1,
                            name: '1%'
                        },
                        {
                            key: 3,
                            name: '3%'
                        },
                        {
                            key: 6,
                            name: '6%'
                        },
                        {
                            key: 9,
                            name: '9%'
                        },
                        {
                            key: 13,
                            name: '13%'
                        }
                    ],
                    disabled: false,
                    name: '税率'
                },
                {
                    data: 'unit_price_text',
                    type: 'number',
                    disabled: false,
                    name: '含税单价',
                    formula: '"unit_price" / (1 + "rate_string" / 100)'
                },
                {
                    data: 'money',
                    type: 'number',
                    disabled: false,
                    name: '含税金额',
                    formula: '"unit_price_text" * "quantity"'
                },
                {
                    data: 'percent_string',
                    type: 'input',
                    disabled: false,
                    name: '溢价比例'
                },
                {
                    data: 'sales_price',
                    type: 'number',
                    disabled: false,
                    name: '售价',
                    formula: '"unit_price" * ((100 + "percent_string") / 100)'
                },
                {
                    data: 'total_sales_price',
                    type: 'number',
                    disabled: false,
                    name: '合计售价',
                    formula: '"sales_price" * "quantity"'
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
                    data: 'type',
                    type: 'input',
                    disabled: false,
                    name: '注册证'
                },
                {
                    data: 'mdrf_inner_sn',
                    type: 'input',
                    disabled: false,
                    name: '生产日期'
                },
                {
                    data: 'health_care_sn',
                    type: 'input',
                    disabled: false,
                    name: '医保编号'
                }
            ],
            totalFormat: {
                on: true,
                searchable: false,
                footer: [
                    'money',
                    'total_sales_price'
                ]
            },
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
            pageName: 'grn',
            venTableData: [],
            dataEditing: {},

            modalsKeyMap,
            buttonsLoading: {
                [buttonsMap.grn.key]: false,
                [buttonsMap.red.key]: false,
                [buttonsMap.delete.key]: false,
                [buttonsMap.unapproval.key]: false
            },
            searchValues: {
                grnSn: '',
                dateRange: [],
                handlerName: undefined,
                organizationName: undefined,
                productName: undefined,
                status: undefined,
                warehouseInnerSn: undefined
            },
            selectedRecords: [],
            orderEditorTarget: null, // 收货单编辑目标
            orderEditorTargetIsCopy: false
        };
    },
    computed: {
        operationGroups() {
            return [
                [
                    buttonsMap.add,
                    buttonsMap.transfer,
                    buttonsMap.edit,
                    buttonsMap.approval,
                    buttonsMap.unapproval,
                    buttonsMap.grn,
                    buttonsMap.copy
                ],
                [buttonsMap.red, buttonsMap.delete]
            ];
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false },
                transfer: { disabled: false },
                edit: { disabled: false, reason: '' },
                approval: { disabled: false, reason: '' },
                unapproval: { loading: this.buttonsLoading[buttonsMap.unapproval.key], disabled: false, reason: '' },
                grn: { loading: this.buttonsLoading[buttonsMap.grn.key], disabled: false, reason: '' },
                copy: { disabled: false, reason: '' },
                red: { loading: this.buttonsLoading[buttonsMap.red.key], disabled: false, reason: '' },
                delete: { loading: this.buttonsLoading[buttonsMap.delete.key], disabled: false, reason: '' }
            };
            if (this.selectedRecords.length !== 1) {
                result.edit.disabled =
                    result.approval.disabled =
                    result.unapproval.disabled =
                    result.grn.disabled =
                    result.copy.disabled =
                    result.red.disabled =
                    result.delete.disabled =
                        true;
                result.edit.reason =
                    result.approval.reason =
                    result.unapproval.reason =
                    result.grn.reason =
                    result.copy.reason =
                    result.red.reason =
                    result.delete.reason =
                        '选中1条记录才能进行操作';
                return result;
            }
            if (this.selectedRecords[0].status === purchaseGrnOrderStatusMap.FORMAL.value) {
                result.unapproval.disabled = result.grn.disabled = result.copy.disabled = result.red.disabled = true;
                result.unapproval.reason =
                    result.grn.reason =
                    result.copy.reason =
                    result.red.reason =
                        '正式状态的记录不能进行此操作';
            }
            if (this.selectedRecords[0].status === purchaseGrnOrderStatusMap.APPROVALED.value) {
                result.approval.disabled = result.copy.disabled = result.red.disabled = result.delete.disabled = true;
                result.approval.reason =
                    result.copy.reason =
                    result.red.reason =
                    result.delete.reason =
                        '已审核的记录不能进行此操作';
            }

            if (this.selectedRecords[0].status === purchaseGrnOrderStatusMap.GRN.value) {
                result.approval.disabled =
                    result.unapproval.disabled =
                    result.grn.disabled =
                    result.copy.disabled =
                    result.delete.disabled =
                        true;
                result.approval.reason =
                    result.unapproval.reason =
                    result.grn.reason =
                    result.copy.reason =
                    result.delete.reason =
                        '已入库的记录不能进行此操作';
            }

            if (this.selectedRecords[0].status === purchaseGrnOrderStatusMap.RED.value) {
                result.approval.disabled =
                    result.unapproval.disabled =
                    result.grn.disabled =
                    result.copy.disabled =
                    result.red.disabled =
                    result.delete.disabled =
                        true;
                result.edit.reason =
                    result.approval.reason =
                    result.unapproval.reason =
                    result.grn.reason =
                    result.copy.reason =
                    result.red.reason =
                    result.delete.reason =
                        '红冲的记录不能进行此操作';
            }

            if (this.selectedRecords[0].status === purchaseGrnOrderStatusMap.CANCEL.value) {
                result.approval.disabled =
                    result.unapproval.disabled =
                    result.grn.disabled =
                    result.red.disabled =
                    result.delete.disabled =
                        true;
                result.approval.reason =
                    result.unapproval.reason =
                    result.grn.reason =
                    result.red.reason =
                    result.delete.reason =
                        '作废的记录不能进行此操作';
            }

            // todo 单据状态过滤
            return result;
        },
        warehouseOptions() {
            return this.$store.state.warehouseList.map((warehouse) => ({
                label: warehouse.name,
                value: warehouse.warehouse_model_inner_sn
            }));
        },
        statusOptions() {
            return Object.keys(purchaseGrnOrderStatusMap).map((key) => ({
                value: purchaseGrnOrderStatusMap[key].value,
                label: purchaseGrnOrderStatusMap[key].text
            }));
        }
    },
    methods: {
        calcPagination(total) {
            this.sheetPagination.total = total;
            this.sheetPagination.available = (this.sheetPagination.pageNum * this.sheetPagination.pageSize) < this.sheetPagination.total ? true : false;
            this.sheetPagination.pageNum = this.sheetPagination.pageNum + 1;
        },
        sheetScrollToEnd() {
            if (this.curTab !== 'add') {
                if (this.sheetPagination.available) {
                    this.sheetPagination.available = false;
                    switch (this.curTab) {
                        case 'history':
                            this.listGrnHistoryFn();
                            break;
                    }
                }
            }
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
                            // {
                            //     disabled: false,
                            //     key: 'delete',
                            //     name: '删除'
                            // },
                            // {
                            //     disabled: true,
                            //     key: 'copy',
                            //     name: '复制'
                            // },
                            // {
                            //     disabled: true,
                            //     key: 'cut',
                            //     name: '剪切'
                            // }
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
                case 'history':
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
                                key: 'transfer',
                                name: '调单'
                            },
                            {
                                disabled: false,
                                key: 'approval',
                                name: '审核'
                            },
                            {
                                disabled: false,
                                key: 'unapproval',
                                name: '取消审核'
                            },
                            {
                                disabled: false,
                                key: 'grn',
                                name: '入库'
                            },
                            {
                                disabled: false,
                                key: 'copy',
                                name: '复制'
                            },
                            {
                                disabled: false,
                                key: 'red',
                                name: '生成红冲'
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
            }
        },
        changeWidths(widths) {
            this.widths = widths;
        },
        clickedMenu(obj) {
            if (this.curTab === 'add' && obj.key === 'delete') {
                for (let i = obj.selection.from; i <= obj.selection.to; i++) {
                    this.addData[i] = {
                        ...this.addData[i],
                        product_vendor_name: '',
                        product_name: '',
                        specification: '',
                        batch_number: '',
                        quantity: '',
                        unit_inner_sn: '',
                        rate_string: '',
                        unit_price_text: '',
                        money: '',
                        percent_string: '',
                        sales_price: '',
                        total_sales_price: '',
                        produce_date: '',
                        overdue_date: '',
                        type: '',
                        mdrf_inner_sn: '',
                        health_care_sn: ''
                    };
                }
            }
            if (this.curTab !== 'add' && obj.key === 'revise') {
                if (this.menu.data[0].disabled === false) {
                    this.editable = {
                        flag: true,
                        obj: this.addData[obj.selection.from]
                    };
                    this.orderEditorTarget = this.addData[obj.selection.from];
                    this.changeTabFn();
                }
            }
        },
        searchComData(obj) {
            if (obj.type === 'dropdown') {
                this.listLoader(obj.value);
            }
        },
        earnOrgType(value) {
            const planListStatus = ['自建', '卖方', '已审核', '已确认', '已终止', '已完结'];
            return planListStatus[value];
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
                                item.unit_inner_sn = unitArr.key[0];
                                item.unit_name = unitArr.data[0].name;
                            }
                        }
                        return item;
                    });
                }
            });
        },
        chooseComData(obj) {
            if (obj.type === 'dropdown') {
                if (obj.value === 'search-more') {
                    this.editCellRow = obj.row;
                    this.$refs.grnOrderHeader.handleSelectProducts();
                } else if (obj.value === 'tips') {
                    return;
                } else {
                    this.selectedVen = Object.assign(
                        {},
                        this.venTableData.find((v) => v.key === obj.value)
                    );
                    let unitOptions;
                    let mdrfOptions;
                    if (this.selectedVen.min_unit_inner_sn) {
                        unitOptions = [
                            {
                                value: this.selectedVen.min_unit_inner_sn,
                                label: this.selectedVen.min_unit
                            }
                        ];
                    }
                    if (this.selectedVen.mdrf_inner_sn) {
                        mdrfOptions = [
                            {
                                value: this.selectedVen.mdrf_inner_sn,
                                label: this.selectedVen.mdrf_sn
                            }
                        ];
                    }
                    let tp = {
                        action: purchaseGrnOrderDetailActionTypeMap.ADD,
                        batch_number: undefined,
                        detail_inner_sn: undefined,
                        health_care_sn: undefined,
                        inspection_detail_id: undefined,
                        mdrf_inner_sn: this.selectedVen.mdrf_inner_sn ? this.selectedVen.mdrf_inner_sn : undefined,
                        mdrf_sn: this.selectedVen.mdrf_inner_sn ? this.selectedVen.mdrf_sn : undefined,
                        produce_date: undefined,
                        overdue_date: undefined,
                        product_inner_sn: this.selectedVen.product_inner_sn,
                        product_name: this.selectedVen.product_name,
                        product_type: this.selectedVen.product_type,
                        product_vendor_inner_sn: this.selectedVen.product_vendor_inner_sn || this.selectedVen.vendor_inner_sn,
                        product_vendor_name: this.selectedVen.product_vendor_name || this.selectedVen.vendor_name,
                        quantity: 1,
                        quantity_old: undefined,
                        rate: 0,
                        specification: this.selectedVen.specification,
                        specification_inner_sn: this.selectedVen.specification_inner_sn,
                        type: 0,
                        unit_inner_sn: this.selectedVen.min_unit_inner_sn,
                        unit_name: this.selectedVen.min_unit,
                        unit_price: 1,
                        unit_price_text: 1,
                        money: 1,
                        percent: 0,
                        sales_price: 0,
                        total_sales_price: 0,
                        vendor_name: this.selectedVen.vendor_name,
                        vendor_inner_sn: this.selectedVen.vendor_inner_sn,
                        voucher_type_c: orderTypeMap.NONE.key,
                        options: {
                            unit_inner_sn: unitOptions,
                            mdrf_inner_sn: mdrfOptions
                        },
                        optionsLoading: { unit_inner_sn: false, mdrf_inner_sn: false },
                        optionsLoaded: { unit_inner_sn: false, mdrf_inner_sn: false }
                    };
                    this.addData[obj.row] = {
                        ...tp,
                        idx: obj.row + 1,
                        rate_string: tp.rate + '%',
                        percent_string: tp.percent + '%'
                    };
                    this.venUnitTreeFn(tp);
                }
            }
        },
        getSelectionData(from, to) {
            let records = [];
            for (let i = from; i <= to; i++) {
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
        searchSheetFn(obj) {
            if (this.curTab !== 'add') {
                this.addData = [];
                this.resetPaginations();
                this.searchValues.grnSn = obj.name;
                this.listGrnHistoryFn();
            }
        },
        resetSheetData() {
            this.addData = [];
            this.widths = [];
            this.columns = [];
            this.resetPaginations();
            this.searchValues.grnSn = '';
        },
        changeTabFn(key = 'add') {
            this.curTab = key;
            // reset Sheet
            this.resetSheetData();
            // actions
            switch (key) {
                case 'history':
                    this.totalFormat.on = false;
                    this.actColumns = this.historyColumns;
                    break;
                default:
                    this.totalFormat.on = true;
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
                        this.columns = this.addColumns;
                        break;
                    case 'history':
                        this.columns = this.historyColumns;
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
                case 'history':
                    //searchable
                    this.totalFormat.searchable = true;
                    //load
                    this.listGrnHistoryFn();
                    break;
                default:
                    //searchable
                    this.totalFormat.searchable = false;
                    //add
                    if (this.editable.flag) {
                        this.panes[0].title = '编辑';
                        this.loadOrderDetail(this.orderEditorTarget.grn_inner_sn, this.orderEditorTarget.billsType);
                    } else {
                        this.panes[0].title = '新增';
                        let data = [];
                        for (let i = 0; i < 10; i++) {
                            data.push({
                                idx: i + 1,
                                key: (Math.random() + 1).toString(36).substring(7),
                                product_vendor_name: '',
                                product_name: '',
                                specification: '',
                                batch_number: '',
                                quantity: '',
                                unit_inner_sn: '',
                                rate_string: '',
                                unit_price_text: '',
                                percent_string: '',
                                money: '',
                                percent: '',
                                sales_price: '',
                                total_sales_price: '',
                                produce_date: '',
                                overdue_date: '',
                                type: '',
                                mdrf_inner_sn: '',
                                health_care_sn: ''
                            });
                        }
                        this.addData = data;
                    }
                    break;
            }
        },
        addNewSheetRow() {
            if (this.curTab === 'add') {
                this.addData.push({
                    idx: this.addData.length + 1,
                    key: (Math.random() + 1).toString(36).substring(7),
                    product_vendor_name: '',
                    product_name: '',
                    specification: '',
                    batch_number: '',
                    quantity: '',
                    unit_inner_sn: '',
                    rate_string: '',
                    unit_price_text: '',
                    percent_string: '',
                    money: '',
                    percent: '',
                    sales_price: '',
                    total_sales_price: '',
                    produce_date: '',
                    overdue_date: '',
                    type: '',
                    mdrf_inner_sn: '',
                    health_care_sn: ''
                });
            }
        },
        multiSelected(obj) {
            if (this.curTab !== 'add') {
                if (obj.from !== obj.to) {
                    let data = [];
                    this.menu.data.forEach((item) => {
                        item.disabled = true;
                        data.push(item);
                    });
                    this.menu.data = data;
                } else {
                    let disabledKeys = [];
                    if (this.addData[obj.from].status === purchaseGrnOrderStatusMap.FORMAL.value) {
                        disabledKeys = ['unapproval', 'grn', 'copy', 'red'];
                    }
                    if (this.addData[obj.from].status === purchaseGrnOrderStatusMap.APPROVALED.value) {
                        disabledKeys = ['approval', 'copy', 'red', 'delete'];
                    }
                    if (this.addData[obj.from].status === purchaseGrnOrderStatusMap.GRN.value) {
                        disabledKeys = ['unapproval', 'grn', 'copy', 'delete'];
                    }
                    if (this.addData[obj.from].status === purchaseGrnOrderStatusMap.RED.value || this.addData[obj.from].status === purchaseGrnOrderStatusMap.CANCEL.value) {
                        disabledKeys = ['approval', 'unapproval', 'copy', 'red', 'delete'];
                    }
                    let data = [];
                    this.menu.data.forEach((item) => {
                        item.disabled = disabledKeys.includes(item.key);
                        data.push(item);
                    });
                    this.menu.data = data;
                }
            }
        },
        onUpdateSheet(records) {
            if (records) {
                records.forEach((item, idx) => {
                    item.rate_string = item.rate + '%';
                    item.percent_string = item.percent + '%';
                    this.addData.splice(this.editCellRow + idx, 0, item);
                });
                this.addData = this.addData.map((item, idx) => {
                    return {
                        ...item,
                        idx: idx + 1
                    };
                });
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
         * 初始化编辑器
         * targetOrder为空时，将清空数据
         * @param {object | undefined} targetOrder 编辑的目标单据
         */
        handleEditInit(targetOrder) {
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
            this.addData = targetOrder.details.map((product, idx) => {
                let unitOptions;
                let mdrfOptions;
                let money = new Big(product.unit_price_text).times(product.quantity).toNumber();
                let total_sales_price = new Big(product.sales_price).times(product.quantity).toNumber();
                money = isNaN(money) ? undefined : money;
                total_sales_price = isNaN(total_sales_price) ? undefined : total_sales_price;
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
                    idx: idx + 1,
                    money,
                    total_sales_price,
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
                    },
                    rate_string: product.rate + '%',
                    percent_string: product.percent_string + '%'
                };
            });
        },
        /**
         * 加载采购入库单详情
         * @param {string | number} grnInnerSn 收货单id
         * @param {string} billsType 单据类型
         */
        async loadOrderDetail(grnInnerSn, billsType) {
            const userBelong = this.$cookie.get('userbelong');
            try {
                const { code, msg, info } = await purchaseGrnInfoAPI({
                    params: {
                        billsType,
                        grn_inner_sn: grnInnerSn,
                        org_id: userBelong
                    }
                });
                console.log(code);
                if (code !== 0) {
                    console.log('******');
                    throw new Error(msg);
                }
                console.log(msg);
                this.handleEditInit(info);
                // 根据单据详情里的明细推断出当前的采购入库单是否为通过调单创建的
                for (let i = 0, l = info.details.length; i < l; i++) {
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
                this.$message.error(`采购入库单详情获取失败`);
                throw err;
            }
        },
        async listGrnHistoryFn() {
            const userBelong = this.$cookie.get('userbelong');
            let loaderData = {
                pageSize: this.sheetPagination.pageSize,
                pageNum: this.sheetPagination.pageNum,
                grn_sn: this.searchValues.grnSn,
                isAsc: 'desc',
                orderByColumn: 'grn_sn',
                org_id: userBelong
            };
            await purchaseGrnListAPI({
                params: loaderData
            }).then((res) => {
                if (parseInt(res.code) === 0) {
                    const { rows, total } = res;
                    let startIdx = this.sheetPagination.pageSize * (this.sheetPagination.pageNum - 1);
                    this.calcPagination(total);
                    let data = [];
                    rows.forEach((item, idx) => {
                        item.idx = startIdx + idx + 1;
                        item.status_string = purchaseGrnOrderStatusTextFilter(item.status);
                        if (item.status === purchaseGrnOrderStatusMap.CANCEL.value || item.status === purchaseGrnOrderStatusMap.RED.value) {
                            item.style = {
                                color: '#ea6e70'
                            };
                        }
                        data.push(item);
                    });
                    this.addData.push(...data);
                }
            });
        },
        onUpdateOrg(obj) {
            this.dataEditing = {
                ...this.dataEditing,
                ...obj.dataEditing
            };
            if (obj.flag) {
                let data = [];
                for (let i = 0; i < 10; i++) {
                    data.push({
                        idx: i + 1,
                        key: (Math.random() + 1).toString(36).substring(7),
                        product_vendor_name: '',
                        product_name: '',
                        specification: '',
                        batch_number: '',
                        quantity: '',
                        unit_inner_sn: '',
                        rate_string: '',
                        unit_price_text: '',
                        money: '',
                        percent: '',
                        sales_price: '',
                        total_sales_price: '',
                        produce_date: '',
                        overdue_date: '',
                        type: '',
                        mdrf_inner_sn: '',
                        health_care_sn: ''
                    });
                }
                this.addData = data;
                this.listLoader();
            }
        },
        /**
         * 产品列表数据加载器
         */
        async listLoader(value = '') {
            this.venTableData = [];
            await getAllVendorListAPI({
                params: {
                    pageSize: 10,
                    org_id: this.cookieInfo.get('userbelong'),
                    pageNum: 1,
                    billsType: this.dataEditing.billsType,
                    organization_inner_sn: this.dataEditing.organizationInnerSn,
                    name: value,
                    onlyContrctProduct: true
                }
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.venTableData = res.rows.map((item) => {
                        item.name = item.product_name;
                        item.unit_name = item.min_unit;
                        item.unit_inner_sn = item.min_unit_inner_sn;
                        item.unitList =
                            item.unit_name && item.unit_inner_sn
                                ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                : [];
                        item.unit_inner_sn = [item.unit_inner_sn];

                        return {
                            ...item,
                            activeClass: '',
                            key: (Math.random() + 1).toString(36).substring(7),
                            keyFormat:
                                item.vendor_inner_sn +
                                '' +
                                item.product_inner_sn +
                                item.product_type +
                                '' +
                                item.specification_inner_sn +
                                item.vendor_name,
                            mdrf_sn: item.mdrf_sn || item.register_sn,
                            mdrf_inner_sn: item.mdrf_inner_sn || item.register_inner_sn,
                            product_name: item.name || item.product_name,
                            product_type: 1,
                            quantity: 1,
                            status: 0
                        };
                    });
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        tableRowClassName({ row }) {
            if (
                row.status === purchaseGrnOrderStatusMap.RED.value ||
                row.status === purchaseGrnOrderStatusMap.CANCEL.value
            ) {
                return 'red-dashed-odo-order';
            }
            return '';
        },
        /**
         * 简单查询
         */
        onHeaderSingleSearch() {
            this.handleSearch({ grnSn: this.searchValues.grnSn });
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

        onOrderEditorClose() {
            this.handleCloseModals(modalsKeyMap.ORDER_EDITOR);
            this.orderEditorTarget = null;
            this.orderEditorTargetIsCopy = false;
        },

        onOrderAdd(order) {
            this.$refs.listTable.handleInsertOrder(order);
        },

        onOrderChange(order) {
            this.$refs.listTable.handleUpdateOrder(order);
        },

        onRedFinish() {
            this.handleSearch();
            this.selectedRecords = [];
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
                case 'transfer':
                    this.handleOpenModals(modalsKeyMap.TRANSFER_EDITOR);
                    break;
                case 'edit': // 注意：修改按钮的逻辑非字面意思的修改，应要求，仅仅是打开详情模态框
                case 'approval':
                    if (this.selectedRecords[0]) {
                        this.orderEditorTarget = this.selectedRecords[0];
                        this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
                    }
                    break;
                case 'copy':
                    if (this.selectedRecords[0]) {
                        this.orderEditorTarget = {
                            ...this.selectedRecords[0],
                            details: null,
                            status: undefined
                        };
                        this.orderEditorTargetIsCopy = true;
                        this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
                    }
                    break;
                case 'unapproval':
                    this.$confirm({
                        title: '确定要反审核吗？',
                        onOk: () => {
                            if (!this.selectedRecords[0]) {
                                throw new Error('无目标单据');
                            }
                            this.unapprovalPurchaseGrnOrder(
                                this.selectedRecords[0].grn_inner_sn,
                                this.selectedRecords[0].billsType
                            ).then(() => {
                                this.onOrderChange({
                                    ...this.selectedRecords[0],
                                    status: purchaseGrnOrderStatusMap.FORMAL.value
                                });
                            });
                        },
                        okText: '确定',
                        cancelText: '取消'
                    });
                    break;
                case 'grn':
                    this.$confirm({
                        title: '确定要入库吗？',
                        onOk: () => {
                            if (!this.selectedRecords[0]) {
                                throw new Error('无目标单据');
                            }
                            this.putinPurchaseGrnOrder(
                                this.selectedRecords[0].grn_inner_sn,
                                this.selectedRecords[0].billsType,
                                this.selectedRecords[0].warehouse_inner_sn,
                                this.selectedRecords[0].warehouse_name
                            ).then(() => {
                                this.onOrderChange({
                                    ...this.selectedRecords[0],
                                    status: purchaseGrnOrderStatusMap.GRN.value
                                });
                            });
                        },
                        okText: '确定',
                        cancelText: '取消'
                    });
                    break;
                case 'red':
                    this.$confirm({
                        title: '确定要红冲吗？',
                        onOk: () => {
                            if (!this.selectedRecords[0]) {
                                throw new Error('无目标单据');
                            }
                            this.redPurchaseGrnOrder(
                                this.selectedRecords[0].grn_inner_sn,
                                this.selectedRecords[0].billsType
                            );
                        },
                        okText: '确定',
                        cancelText: '取消'
                    });
                    break;
                case 'delete':
                    this.$confirm({
                        title: '确定要删除单据吗？',
                        onOk: () => {
                            if (!this.selectedRecords[0]) {
                                throw new Error('无目标单据');
                            }
                            this.deletePurchaseGrnOrder(
                                this.selectedRecords[0].grn_inner_sn,
                                this.selectedRecords[0].billsType
                            );
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

        onTransferEditorSubmit(order) {
            this.handleCloseModals(modalsKeyMap.TRANSFER_EDITOR);
            this.orderEditorTarget = order;
            this.orderEditorTarget._from = 'transfer';
            this.handleOpenModals(modalsKeyMap.ORDER_EDITOR);
        },

        /**
         * 查询列表
         * @param {object | undefined} values 查询条件，未定义时以当前状态进行查询
         */
        handleSearch(values) {
            if (this.$refs.listTable) {
                this.$refs.listTable.handleSearch(values);
            }
        },

        /**
         * 删除采购入库单
         * @param {string | number} id 单据id
         * @param {string} billsType 单据类型
         */
        async deletePurchaseGrnOrder(id, billsType) {
            const orgId = this.$cookie.get('userbelong');
            try {
                this.buttonsLoading[buttonsMap.delete.key] = true;
                const { code, msg } = await purchaseGrnDelAPI({
                    billsType,
                    grn_inner_sn: id,
                    org_id: orgId
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('删除成功');
                this.selectedRecords = [];
                this.$refs.listTable.handleRemoveRecordByKeyValue('grn_inner_sn', id);
            } catch (err) {
                this.$message.error(err.message || '删除失败');
                throw err;
            } finally {
                this.buttonsLoading[buttonsMap.delete.key] = false;
            }
        },

        /**
         * 入库
         * @param {string | number} id 入库单id
         * @param {string} billsType 单据类型
         * @param {string | number} warehouseInnerSn 仓库id
         * @param {string} warehouseName 仓库名
         */
        async putinPurchaseGrnOrder(id, billsType, warehouseInnerSn, warehouseName) {
            const orgId = this.$cookie.get('userbelong');
            try {
                this.buttonsLoading[buttonsMap.grn.key] = true;
                const { code, msg } = await grnPutinAPI({
                    billsType,
                    org_id: orgId,
                    grn_inner_sn: id,
                    warehouse_inner_sn: warehouseInnerSn,
                    warehouse_name: warehouseName
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('入库成功');
            } catch (err) {
                this.$message.error(err.message || '入库失败');
                throw err;
            } finally {
                this.buttonsLoading[buttonsMap.grn.key] = false;
            }
        },

        /**
         * 红冲
         * @param {string | number} id 入库单id
         * @param {string} billsType 单据类型
         */
        async redPurchaseGrnOrder(id, billsType) {
            const orgId = this.$cookie.get('userbelong');
            try {
                this.buttonsLoading[buttonsMap.red.key] = true;
                const { code, info, msg } = await grnRedDashedAPI({
                    billsType,
                    org_id: orgId,
                    grn_inner_sn: id
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('红冲成功');
                this.onRedFinish(info);
            } catch (err) {
                this.$message.error(err.message || '红冲失败');
                throw err;
            } finally {
                this.buttonsLoading[buttonsMap.red.key] = false;
            }
        },

        /**
         * 反审核
         * @param {string | number} id 入库单id
         * @param {string} billsType 单据类型
         */
        async unapprovalPurchaseGrnOrder(id, billsType) {
            const orgId = this.$cookie.get('userbelong');
            try {
                this.buttonsLoading[buttonsMap.unapproval.key] = true;
                const { code, msg } = await grnUnreviewAPI({
                    billsType,
                    org_id: orgId,
                    grn_inner_sn: id
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('反审核成功');
            } catch (err) {
                this.$message.error(err.message || '反审核失败');
                throw err;
            } finally {
                this.buttonsLoading[buttonsMap.unapproval.key] = false;
            }
        }
    },
    mounted() {
        this.changeTabFn();
    }
};
</script>
