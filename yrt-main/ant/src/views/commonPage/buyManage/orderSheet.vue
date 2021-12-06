<template>
    <and-page-layout class="scoped scoped-white order-sheets">
        <template slot="breadcrumb">
            <a-breadcrumb style="float: left">
                <a-breadcrumb-item>采购管理</a-breadcrumb-item>
                <a-breadcrumb-item>订单</a-breadcrumb-item>
            </a-breadcrumb>
        </template>
        <template slot="tabs-content">
            <and-tabs-content :panes="panes" :cur="curTab" @changeTabFn="changeTabFn">
                <template slot="atc-header">
                    <a-row class="atc-header" v-if="curTab === 'add'">
                        <a-col :span="24">
                            <h2>{{ orgName }}&nbsp;&nbsp;采购订单</h2>
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
                                    <a-spin v-if="fetching" slot="notFoundContent" size="small" />
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
                        <a-col :span="8" v-if="myOrderPropObj.pageType === 0">
                            <a-col :span="6" style="margin-top: 1px">
                                <span><em class="em red">*</em>&nbsp;采购员:</span>
                            </a-col>
                            <a-col :span="15">
                                <a-tree-select
                                    :disabled="isEditDisabled || (editable.flag && editable.type === 'view')"
                                    treeNodeFilterProp="title"
                                    searchPlaceholder=""
                                    :showSearch="true"
                                    :treeData="planModalEmployes"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    treeDefaultExpandAll
                                    allowClear
                                    :value="planModalEmployeSn | numberToStringFilter"
                                    @change="changePlanModalEmployeSnFn"
                                    class="input"
                                    style="min-width: 120px"
                                >
                                </a-tree-select>
                            </a-col>
                        </a-col>
                        <a-col :span="8" v-if="myOrderPropObj.pageType === 0">
                            <a-col :span="6" style="margin-top: 1px">
                                <span><em class="em red">*</em>&nbsp;采购部门:</span>
                            </a-col>
                            <a-col :span="15">
                                <a-tree-select
                                    treeNodeFilterProp="title"
                                    :showSearch="true"
                                    :disabled="isEditDisabled || (editable.flag && editable.type === 'view')"
                                    :treeData="planModalDepartmentSns"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    treeDefaultExpandAll
                                    :allowClear="false"
                                    :value="planModalDepartmentSn | numberToStringFilter"
                                    @change="changePlanModalDepartmentSnFn"
                                    class="input"
                                    style="min-width: 120px"
                                >
                                </a-tree-select>
                            </a-col>
                        </a-col>
                        <a-col :span="7" :offset="5" v-if="myOrderPropObj.pageType === 0" style="margin-top: 15px">
                            <a-col :span="8" style="margin-top: 1px">
                                <span>期望到货日期:</span>
                            </a-col>
                            <a-col :span="12">
                                <a-date-picker
                                    :disabled="isEditDisabled || (editable.flag && editable.type === 'view')"
                                    v-model="expectAogDate"
                                    format="YYYY-MM-DD"
                                    @change="changeExpectAogDateFn"
                                    placeholder=""
                                    allowClear
                                    class="input"
                                />
                            </a-col>
                        </a-col>
                        <a-col :span="7" v-if="myOrderPropObj.pageType === 0" style="margin-top: 15px">
                            <a-col :span="6" style="margin-top: 1px">
                                <span><em class="em red">*</em>&nbsp;仓库:</span>
                            </a-col>
                            <a-col :span="15">
                                <a-tree-select
                                    treeNodeFilterProp="title"
                                    showSearch
                                    :disabled="isWarehouseEditDisabled  || (editable.flag && editable.type === 'view')"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    :treeData="planModalWarehouseData"
                                    placeholder=""
                                    treeDefaultExpandAll
                                    allowClear
                                    v-on:click="warehouseUsableTopListAPIFn"
                                    :value="planModalWarehouseSn | numberToStringFilter"
                                    @change="changewareHouseFn"
                                >
                                </a-tree-select>
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
                        <a-row style="margin: 8px 0;height: 24px;overflow: hidden">
                            <a-col :span="15" style="text-align: left">
                                <a-col :span="12" style="padding-right: 20px">
                                    <a-input
                                        v-model="planModalLocation"
                                        :disabled="isEditDisabled || (editable.flag && editable.type === 'view')"
                                        placeholder="送货地址"
                                        class="input"
                                        @click="clickDescriptionFn"
                                        @change="changeDeliveryAddressFn"
                                    />
                                </a-col>
                                <a-col :span="12" style="padding-right: 20px">
                                    <a-input
                                        :disabled="isEditDisabled || (editable.flag && editable.type === 'view')"
                                        v-model="planModalForm.description"
                                        placeholder="摘要"
                                        class="input"
                                        @click="clickDescriptionFn"
                                    />
                                </a-col>
                            </a-col>
                            <a-col :span="9" style="text-align: left">
                                <a-col :span="12">
                                    <a-col :span="12">
                                        <span class="label" style="width: auto; padding-right: 10px">制单人</span>
                                        <span>{{ planModalForm.creator_name || '-' }}</span>
                                    </a-col>
                                    <a-col :span="12">
                                        <span class="label" style="width: auto; padding-right: 10px">生成时间</span>
                                        <span>{{ planModalForm.create_time || '-' }}</span>
                                    </a-col>
                                </a-col>
                                <a-col :span="12">
                                    <a-col :span="12">
                                        <span class="label" style="width: auto; padding-right: 10px">审核人</span>
                                        <span>{{ planModalForm.auditor_name || '-' }}</span>
                                    </a-col>
                                    <a-col :span="12">
                                        <span class="label" style="width: auto; padding-right: 10px">审核时间</span>
                                        <span>{{ planModalForm.audit_time || '-' }}</span>
                                    </a-col>
                                </a-col>
                            </a-col>
                        </a-row>
                        <a-row>
                            <a-col :span="6" style="text-align: left">
                                <a-button @click="planModalGroupBtnFn('print')" key="print" :loading="planModal.loadingPrint">
                                    打印
                                </a-button>
                                <a-button
                                    @click="planModalGroupBtnFn('exportExcel')"
                                    key="exportExcel"
                                    :loading="planModal.loadingExportExcel"
                                    style="margin-left: 5px"
                                >
                                    导出Excel
                                </a-button>
                                <a-button
                                    v-if="myOrderPropObj.pageType === 1"
                                    :disabled="isAddPlanModalDisabled || (editable.flag && editable.type === 'view')"
                                    @click="planModalGroupBtnFn('add')"
                                    :loading="planModal.loadingAdd"
                                    key="add"
                                    style="margin-left: 5px"
                                >
                                    生成销售单
                                </a-button>
                            </a-col>
                            <a-col :offset="12" :span="6" style="text-align: right">
                                <!-- <a-button @click="planModalGroupBtnFn('cancel')" key="cancel">
                                    {{ planModalTextComputed }}
                                </a-button> -->
                                <a-button
                                    v-show="planModalForm.status === 0 || btnName === 'add'"
                                    :disabled="isEditDisabled || (editable.flag && editable.type === 'view')"
                                    @click="planModalGroupBtnFn('saveDraft')"
                                    key="saveDraft"
                                    type="primary"
                                    :loading="planModal.loadingSaveDraft"
                                    style="margin-left: 5px"
                                >
                                    存草稿
                                </a-button>
                                <a-button
                                    v-show="
                                        planModalForm.status === 0 ||
                                        planModalForm.status === 1 ||
                                        btnName === 'add' ||
                                        isSourceBuyerAudited
                                    "
                                    :disabled="(isSourceBuyerAudited ? false : isEditDisabled) || (editable.flag && editable.type === 'view')"
                                    @click="planModalGroupBtnFn('save')"
                                    key="submit"
                                    type="primary"
                                    :loading="planModal.loadingSave"
                                    style="margin-left: 5px"
                                >
                                    {{
                                        planModalForm.status === 2 && planModalForm.create_organization_type === 1
                                            ? '确认'
                                            : '保存'
                                    }}
                                </a-button>
                                <a-button
                                    v-show="planModalForm.status === 1"
                                    :disabled="isEditDisabled || (editable.flag && editable.type === 'view')"
                                    @click="planModalGroupBtnFn('audit')"
                                    key="audit"
                                    type="primary"
                                    :loading="planModal.loadingAudit"
                                    style="margin-left: 5px"
                                >
                                    审核
                                </a-button>
                            </a-col>
                        </a-row>
                    </a-row>
                </template>
            </and-tabs-content>
        </template>
        <template slot="modals">
            <!-- 选择采购单位区域开始 -->
            <a-modal
                destroyOnClose
                :visible="chooseOrgMd.alert"
                :maskClosable="false"
                :width="600"
                :centered="true"
                :footer="null"
                :zIndex="3"
                @cancel="chooseOrgMd.alert = false"
                class="scoped modal-form-input-scoped global-drag choose_org_md"
            >
                <div v-globalDrag="{ el: 'choose_org_md' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ chooseOrgMd.title }}
                    </h6>
                </div>
                <div style="margin-top: 56px">
                    <chooseOrgTab
                        ref="chooseOrgMd"
                        @chooseOrg="chooseOrgFn"
                        :searchOrgName="searchOrgName"
                        :isParentKeyDownEnter="isParentKeyDownEnter"
                        :partType="1"
                        :pageTypeName="pageTypeName"
                    ></chooseOrgTab>
                </div>
            </a-modal>
            <!-- 选择采购单位区域结束 -->

            <!-- 添加产品区域开始 -->
            <a-modal
                :title="addProductMd.title"
                v-model="addProductMd.alert"
                :maskClosable="false"
                :mask="false"
                :width="1000"
                :centered="true"
                @cancel="closeAddProductFn"
                class="modal-form-input-scoped global-drag add_product"
            >
                <div v-globalDrag="{ el: 'add_product' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ addProductMd.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <a-button @click="closeAddProductFn">关闭</a-button>
                    <a-button :loading="addProductMd.loading" @click="addProductFn" type="primary">添加</a-button>
                </template>
                <add-product ref="chooseProMd" @returnData="getProReturn" :partB="partB"></add-product>
            </a-modal>
            <!-- 添加产品区域结束 -->
        </template>
    </and-page-layout>
</template>

<script>
import moment from 'moment';
import { mapState } from 'vuex';

// import newDesign Layount
import AndPageLayout from '@/components/AndLayout/AndPageLayout';
import AndTabsContent from '@/components/AndLayout/AndTabsContent';
// import components
import AndSheetActs from '@/components/purchase/AndSheetActs';
import AndSheet from '@/components/AndRenders/AndSheet';

import chooseOrgTab from '@/components/chooseOrgTab';
import addProduct from '@/components/addProduct';

import {
    orderPurchaseConfirmAPI,
    orderSaleConfirmAPI,
    orderDetailStopAPI,
    orderDetailUnstopAPI,
    orderSaleListAPI,
    orderHistorySaleListAPI,
    orderSaleInfoAPI,
    orderSaleAddAPI,
    orderSaleEditAPI,
    orderConfirmAPI,
    orderStopAPI,
    orderSaleBuildAPI,
    orderSaleDetailBuildAPI,
    platformUnitTree,
    platformUnitParents,
    dealerUnitTreeAPI,
    dealerUnitParentsAPI,
    orderPurchaseEditAPI,
    orderDelAPI,
    orderDetailDelAPI,
    orderOrgListAPI,
    orgDeptTopTreeAPI,
    orgEmpDropdownListAPI,
    orderPurchaseListAPI,
    orderHistoryPurchaseListAPI,
    orderPurchaseInfoAPI,
    orderPurchaseAddAPI,
    contractGetOrderPriceAPI,
    grnOrgListAPI,
    orderVendorListAPI
} from '@/service/pageAjax';
import { BillsType } from '@/config/constants';

// 产品的状态文本map
const productStatusTextMap = ['处理中', '已完全处理', '已计划采购', '已终止处理'];

export default {
    name: 'orderSheet',
    props: {
        myOrderPropObj: {
            type: Object,
            default: function () {
                return {
                    pageType: 0
                };
            }
        }
    },
    components: {
        // custom layounts
        AndPageLayout,
        AndTabsContent,
        // custom components
        AndSheet,
        AndSheetActs,
        // modal
        chooseOrgTab,
        addProduct
    },
    data() {
        return {
            // init panes
            panes: [
                {
                    key: 'add',
                    title: '新增'
                },
                {
                    key: 'perform',
                    title: '经销商/厂商'
                },
                // {
                //     key: 'past',
                //     title: '厂商'
                // },
                {
                    key: 'history',
                    title: '历史'
                }
            ],
            curTab: 'add',
            actColumns: [],
            addData: [],
            editable: {
                flag: false,
                type: '',
                obj: {}
            },
            totalFormat: {
                on: true,
                searchable: false,
                footer: [
                    'money',
                    'tax_amount',
                    'tax'
                ]
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
                    data: 'product_name',
                    type: 'dropdown',
                    disabled: false,
                    name: '产品'
                },
                {
                    data: 'specification',
                    type: 'input',
                    disabled: true,
                    name: '规格型号'
                },
                {
                    data: 'product_vendor_name',
                    type: 'input',
                    disabled: true,
                    name: '厂商/经销商'
                },
                {
                    data: 'status_string',
                    type: 'input',
                    disabled: true,
                    name: '状态'
                },
                {
                    data: 'quantity',
                    type: 'number',
                    disabled: false,
                    name: '数量'
                },
                {
                    data: 'unit_name',
                    type: 'cascader',
                    obj: {
                        vModel: 'unit_inner_sn',
                        options: 'unit_data'
                    },
                    disabled: false,
                    name: '包装单位'
                },
                {
                    data: 'batch_number',
                    type: 'input',
                    disabled: true,
                    name: '批号'
                },
                {
                    data: 'unit_price_text',
                    type: 'number',
                    disabled: true,
                    name: '单价',
                    formula: '"unit_price" / (1 + "rate_string" / 100)'
                },
                {
                    data: 'money',
                    type: 'number',
                    disabled: true,
                    name: '金额',
                    formula: '"unit_price_text" * "quantity"'
                },
                {
                    data: 'rate_string',
                    type: 'simpleDropdown',
                    subffix: '%',
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
                    data: 'tax_unit_price',
                    type: 'number',
                    disabled: false,
                    name: '含税单价',
                    formula: '"unit_price"'
                },
                {
                    data: 'tax_amount',
                    type: 'number',
                    disabled: true,
                    name: '含税金额',
                    formula: '"quantity" * "unit_price"'
                },
                {
                    data: 'tax',
                    type: 'input',
                    disabled: true,
                    name: '税额',
                    formula: '"tax_amount" - "money"'
                }
            ],
            dealerColumns: [
                {
                    data: 'idx',
                    type: 'header',
                    disabled: true,
                    name: ''
                },
                {
                    data: 'partB_name',
                    type: 'input',
                    disabled: true,
                    name: ''
                },
                {
                    data: 'po_sn',
                    type: 'input',
                    disabled: true,
                    name: '单据编号'
                },
                {
                    data: 'buyer_name',
                    type: 'input',
                    disabled: true,
                    name: '采购员'
                },
                {
                    data: 'department_name',
                    type: 'input',
                    disabled: true,
                    name: '采购部门'
                },
                {
                    data: 'expect_aog_date',
                    type: 'input',
                    disabled: true,
                    name: '期望到货日期'
                },
                {
                    data: 'create_time',
                    type: 'input',
                    disabled: true,
                    name: '单据生成时间'
                },
                {
                    data: 'status_string',
                    type: 'input',
                    disabled: true,
                    name: '状态'
                },
                {
                    data: 'create_organization_type_string',
                    type: 'input',
                    disabled: true,
                    name: '来源'
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
                total: 15,
                current: 1, // 对应后端的 pageSize
                pageSize: 15, // 对应后端的 pageNum
                pageNum: 1,
                available: true
            },
            sheetPagination2: {
                loading: false,
                total: 15,
                current: 1, // 对应后端的 pageSize
                pageSize: 15, // 对应后端的 pageNum
                pageNum: 1,
                available: true
            },
            pageName: 'order',

            pageTypeName: undefined,
            addProductEnter: false,
            relevantInstitutionsFocus: true,
            format: 'YYYY-MM-DD HH:mm:ss',
            topSearchEmpDropdownList: [], // 人员
            topSearchEmpDropdownSn: [],
            topSearchAllTreeList: [], // 部门
            topSearchAllTreeListSn: [],
            // 部门
            editInfoFormDepartmentInnerSn: [], // 所属部门
            architectureAllTrees: [], // 所属部门的信息列表
            cookieInfo: this.$cookie,
            searchFormObj: {
                partB_name: '',
                order_sn: '',
                buyer_id: '',
                department_id: '',
                createTimeRange: [] // 订单生成时间区间（空数组或[moment, moment]）
            },
            billsType: '', // 采购订单内页中临时记住采购单位的类型
            tabTexts: ['经销商', '厂商', '医院', '经销商', '历史'], // 顶部表格的tab切换
            showSexTip01: false,
            performColumns: [
                // 采购订单的头部
                // {
                //     title: '',
                //     dataIndex: 'partB_name',
                //     align: 'center',
                //     width: '19%',
                //     slots: { title: 'nameTitle' }
                // },
                {
                    title: '单据编号',
                    dataIndex: 'po_sn',
                    align: 'center',
                    width: '160px'
                },
                {
                    title: '采购员',
                    dataIndex: 'buyer_name',
                    align: 'center',
                    width: '80px'
                },
                {
                    title: '采购部门',
                    dataIndex: 'department_name',
                    align: 'center',
                    width: '100px'
                },
                {
                    title: '期望到货日期',
                    dataIndex: 'expect_aog_date',
                    align: 'center',
                    width: '110px'
                },
                {
                    title: '单据生成时间',
                    dataIndex: 'create_time',
                    align: 'center',
                    width: '150px'
                }
            ],
            salesHospitalColumns: [
                // 销售订单的头部 【医院】
                {
                    title: '医院',
                    dataIndex: 'partA_name',
                    align: 'center',
                    width: '20%'
                },
                {
                    title: '医院别名',
                    dataIndex: 'alias',
                    align: 'center',
                    width: '20%'
                },
                {
                    title: '单据编号',
                    dataIndex: 'so_sn',
                    align: 'center',
                    width: '14%'
                },
                {
                    title: '期望到货日期',
                    dataIndex: 'expect_aog_date',
                    align: 'center',
                    width: '16%'
                },
                {
                    title: '单据生成时间',
                    dataIndex: 'create_time',
                    align: 'center',
                    width: '16%'
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    width: '14%',
                    scopedSlots: { customRender: 'status' }
                }
            ],
            salesDealerColumns: [
                // 销售订单的头部 【经销商】
                {
                    title: '经销商',
                    dataIndex: 'partA_name',
                    align: 'center',
                    width: '28%'
                },
                {
                    title: '单据编号',
                    dataIndex: 'so_sn',
                    align: 'center',
                    width: '25%'
                },
                {
                    title: '期望到货日期',
                    dataIndex: 'expect_aog_date',
                    align: 'center',
                    width: '17%'
                },
                {
                    title: '单据生成时间',
                    dataIndex: 'create_time',
                    align: 'center',
                    width: '17%'
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    width: '13%',
                    scopedSlots: { customRender: 'status' }
                }
            ],
            salesHistoryColumns: [
                // 销售订单的头部 【历史】
                {
                    dataIndex: 'partA_name',
                    align: 'center',
                    width: '28%',
                    slots: { title: 'nameTitle' }
                },
                {
                    title: '单据编号',
                    dataIndex: 'so_sn',
                    align: 'center',
                    width: '25%'
                },
                {
                    title: '期望到货日期',
                    dataIndex: 'expect_aog_date',
                    align: 'center',
                    width: '17%'
                },
                {
                    title: '单据生成时间',
                    dataIndex: 'create_time',
                    align: 'center',
                    width: '17%'
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    width: '13%',
                    scopedSlots: { customRender: 'status' }
                }
            ],
            purchaseDelarList: [],
            purchaseDelarPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentPurchaseDelarTrObj: {},
            purchaseVendorList: [],
            purchaseVendorPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentPurchaseVendorTrObj: {},
            purchaseHistoryList: [],
            purchaseHistoryPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentPurchaseHistoryTrObj: {},
            saleDelarList: [],
            saleDelarPagination: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentSaleDelarTrObj: {},
            saleVendorList: [],
            saleVendorPagination: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentSaleVendorTrObj: {},
            saleHistoryList: [],
            saleHistoryPagination: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentSaleHistoryTrObj: {},
            purchaseDelarSelectedRowKeys: [],
            purchaseVendorSelectedRowKeys: [],
            saleDelarSelectedRowKeys: [],
            saleVendorSelectedRowKeys: [],
            systemTypes: ['perform', 'past', 'history'],
            tabsActiveKey: 'perform',
            isTabsActiveVendorKey: false,
            isTabsActiveHistoryKey: false,
            btnName: 'add',
            // 订单弹框
            planModal: {
                title: `${this.$route.meta.title} - 新增`,
                alert: false,
                loadingPrint: false,
                loadingAdd: false,
                loadingSaveDraft: false,
                loadingSave: false,
                loadingAudit: false,
                loadingExportExcel: false
            },
            expectAogDate: null,
            planModalColumns: [
                {
                    title: 'NO.',
                    dataIndex: 'index',
                    key: 'index',
                    width: 40,
                    align: 'center',
                    scopedSlots: { customRender: 'index' }
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: '1',
                    width: 150,
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: '2',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '厂商',
                    dataIndex: 'partB_name',
                    key: '3',
                    width: 110,
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: '4',
                    width: 110,
                    align: 'center',
                    scopedSlots: { customRender: 'quantity' }
                },
                {
                    title: this.myOrderPropObj.pageType === 0 ? '已到货数量' : '已发货数量',
                    dataIndex: 'quantity_delivered',
                    key: '5',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    key: '7',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'status' }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_inner_sn',
                    key: 'unit_inner_sn',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'unit' }
                },
                {
                    title: '单价',
                    dataIndex: 'unit_price_text',
                    key: '9',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'unit_price_text' }
                },
                {
                    title: '金额',
                    dataIndex: 'money',
                    key: '10',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'money' }
                },
                {
                    title: '折扣',
                    dataIndex: 'discount',
                    key: '11',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount' }
                },
                {
                    title: '折后单价',
                    dataIndex: 'discount_unit_price',
                    key: '12',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount_unit_price' }
                },
                {
                    title: '折后金额',
                    dataIndex: 'discount_money',
                    key: '13',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount_money' }
                },
                {
                    title: '税率',
                    dataIndex: 'rate',
                    key: '14',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'rate' }
                },
                {
                    title: '含税单价',
                    dataIndex: 'tax_unit_price',
                    key: '15',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax_unit_price' }
                },
                {
                    title: '含税金额',
                    dataIndex: 'tax_amount',
                    key: '16',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax_amount' }
                },
                {
                    title: '税额',
                    dataIndex: 'tax',
                    key: '17',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax' }
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    key: '18',
                    width: 100,
                    align: 'center',
                    fixed: 'right',
                    slots: { title: 'actionTitle' },
                    scopedSlots: { customRender: 'action' }
                }
            ],
            planModalHideActionsColumns: [
                {
                    title: 'NO.',
                    dataIndex: 'index',
                    key: 'index',
                    width: 40,
                    align: 'center',
                    scopedSlots: { customRender: 'index' }
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: '1',
                    width: 150,
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: '2',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '厂商',
                    dataIndex: 'partB_name',
                    key: '3',
                    width: 110,
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: '4',
                    width: 110,
                    align: 'center',
                    scopedSlots: { customRender: 'quantity' }
                },
                {
                    title: this.myOrderPropObj.pageType === 0 ? '已到货数量' : '已发货数量',
                    dataIndex: 'quantity_delivered',
                    key: '5',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    key: '7',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'status' }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_inner_sn',
                    key: 'unit_inner_sn',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'unit' }
                },
                {
                    title: '单价',
                    dataIndex: 'unit_price_text',
                    key: '9',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'unit_price_text' }
                },
                {
                    title: '金额',
                    dataIndex: 'money',
                    key: '10',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'money' }
                },
                {
                    title: '折扣',
                    dataIndex: 'discount',
                    key: '11',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount' }
                },
                {
                    title: '折后单价',
                    dataIndex: 'discount_unit_price',
                    key: '12',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount_unit_price' }
                },
                {
                    title: '折后金额',
                    dataIndex: 'discount_money',
                    key: '13',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount_money' }
                },
                {
                    title: '税率',
                    dataIndex: 'rate',
                    key: '14',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'rate' }
                },
                {
                    title: '含税单价',
                    dataIndex: 'tax_unit_price',
                    key: '15',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax_unit_price' }
                },
                {
                    title: '含税金额',
                    dataIndex: 'tax_amount',
                    key: '16',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax_amount' }
                },
                {
                    title: '税额',
                    dataIndex: 'tax',
                    key: '17',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax' }
                }
            ],
            planModalOrgSn: [],
            planModalOrgS: [],
            planModalEmployeSn: [],
            planModalDepartmentSn: [],
            planModalEmployes: [],
            planModalDepartmentSns: [],
            planModalLocation: '',
            planModalWarehouseData: [],
            planModalWarehouse: '',
            planModalWarehouseSn: '',
            planModalDatas: [],
            isAddProductModalTableMax: false, // 是否是全屏模式，默认不是
            planModalPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            planModalSelectedRowKeys: [],
            currentTrPlanModalObj: {},
            planModalForm: {
                buyer_id: '',
                buyer_id_old: '',
                buyer_name: '',
                department_inner_sn: '',
                department_inner_sn_old: '',
                department_name: '',
                description: '',
                description_old: '',
                details: [],
                expect_aog_date: '',
                expect_aog_date_old: '',
                billsType: '',
                partA_id: '',
                partB_id: '',
                po_sn: '',
                po_sn_old: '',
                po_so_inner_sn: '',
                warehouse_inner_sn: '',
                warehouse_name: '',
                delivery_address: '',
                status: 0
            },
            editInfoFormNewObj: {},
            detailList: [],
            // 添加产品
            addProductMd: {
                title: `${this.$route.meta.title} - 新增 - 添加产品`,
                alert: false,
                loading: false
            },
            partB: {
                // 新增产品窗口给子组件传递值
                partA_id: '',
                partB_id: '',
                billsType: '',
                oname: '',
                modalType: 4, // 1 - 合同内的产品  2 - 期初库存的产品列表  3 - 采购计划  4 - 采购/销售订单
                isProcurementSalesFlag: false // true - 销售单  false - 采购单
            },
            searchOrgName: '',
            isParentKeyDownEnter: false,
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            chooseOrgMd: {
                title: `${this.$route.meta.title} - 供货单位`,
                alert: false,
                loading: false
            },
            addInfoModalForm: {
                department_inner_sn: '',
                department_name: '',
                description: '',
                details: '',
                grn_sn: '',
                odo_sn: '',
                odo_type: '',
                handler_inner_sn: '',
                handler_name: '',
                org_id: '',
                organization_inner_sn: '',
                organization_name: '',
                status: '',
                type_inner_sn: '',
                type_name: '',
                // 仓库id
                warehouse_inner_sn: '',
                // 仓库名
                warehouse_name: '',
                warehouse_operator_inner_sn: '',
                warehouse_operator_name: '',
                billsType: ''
            },
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 57 : 0,
            tableData: [],
            tableDataTemp: [],
            fetching: false,
            venTableData: [],
            venFetching: false,
            selectedSupply: {},
            supplySearch: '',
            selectedVen: {
                key: ''
            }
        };
    },
    methods: {
        calcPagination(total) {
            this.sheetPagination.total = total;
            this.sheetPagination.available = (this.sheetPagination.pageNum * this.sheetPagination.pageSize) < this.sheetPagination.total ? true : false;
            this.sheetPagination.pageNum = this.sheetPagination.pageNum + 1;
        },
        calcPagination2(total) {
            this.sheetPagination2.total = total;
            this.sheetPagination2.available = (this.sheetPagination2.pageNum * this.sheetPagination2.pageSize) < this.sheetPagination2.total ? true : false;
            this.sheetPagination2.pageNum = this.sheetPagination2.pageNum + 1;
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
                case 'history':
                    this.menu = {
                        visible: false,
                        data: [
                            {
                                disabled: true,
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
                    product_name: '',
                    specification: '',
                    product_vendor_name: '',
                    status_string: '',
                    quantity: '',
                    unit_name: '',
                    batch_number: '',
                    unit_price_text: '',
                    money: '',
                    rate_string: '',
                    tax_unit_price: '',
                    tax_amount: '',
                    tax: ''
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
            if ((this.curTab !== 'add' && obj.key === 'revise') || (this.curTab === 'history' && obj.key === 'view')) {
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
                this.orderVendorListAPIFn(obj.value);
            }
        },
        earnStatus(value, name) {
            const planListStatus = ['草稿', '正式', '已审核', '已确认', '已终止', '已完结'];
            if (name === 'planList') {
                return planListStatus[value];
            } else if (name === 'planOrder') {
                return productStatusTextMap[value];
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
                        rate_string: this.reNumber(this.selectedVen.rate * 100),
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
                total: 15,
                current: 1, // 对应后端的 pageSize
                pageSize: 15, // 对应后端的 pageNum
                pageNum: 1,
                available: true
            };
            this.sheetPagination2 = {
                loading: false,
                total: 15,
                current: 1, // 对应后端的 pageSize
                pageSize: 15, // 对应后端的 pageNum
                pageNum: 1,
                available: true
            };
        },
        resetSheetData() {
            this.addData = [];
            this.widths = [];
            this.columns = [];
            this.resetPaginations();
            this.searchFormObj.partB_name = '';
        },
        sheetScrollToEnd() {
            if (this.curTab !== 'add') {
                if (this.sheetPagination.available) {
                    this.sheetPagination.available = false;
                    switch (this.curTab) {
                        case 'perform':
                        case 'past':
                            this.orderPurchaseListAPIFn('perform');
                            break;
                        case 'history':
                            this.orderHistoryPurchaseListAPIFn();
                            break;
                    }
                }
                if (this.sheetPagination2.available) {
                    this.sheetPagination2.available = false;
                    switch (this.curTab) {
                        case 'perform':
                        case 'past':
                            this.orderPurchaseListAPIFn('past');
                            break;
                        case 'history':
                            this.orderHistoryPurchaseListAPIFn();
                            break;
                    }
                }
            }
        },
        changeTabFn(key = 'add') {
            this.curTab = key;
            // reset Add From
            this.closePlanModalFn();
            this.resetPlanModalFn();
            // reset Sheet
            this.resetSheetData();
            // actions
            switch (key) {
                case 'perform':
                case 'past':
                case 'history':
                    this.totalFormat.on = false;
                    this.dealerColumns[1].name = '经销商/厂商';
                    // if (key === 'perform') {
                    //     this.dealerColumns[1].name = '经销商';
                    // } else if (key === 'past') {
                    //     this.dealerColumns[1].name = '厂商';
                    // } else {
                    //     this.dealerColumns[1].name = '经销商/厂商';
                    // }
                    this.actColumns = this.dealerColumns;
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
                    case 'perform':
                    case 'past':
                    case 'history':
                        this.columns = this.dealerColumns;
                        break;
                }
            }
            if (cookieData !== null && cookieData.widths !== undefined && cookieData.widths.length > 0) {
                this.widths = cookieData.widths;
            }
            // initial menu
            this.setupMenu(key);
            //searchable
            this.totalFormat.searchable = true;
            // actions
            switch (key) {
                case 'perform':
                    // load
                    this.orderPurchaseListAPIFn('perform');
                    this.orderPurchaseListAPIFn('past');
                    break;
                case 'past':
                    this.curTab = 'perform';
                    // load
                    this.orderPurchaseListAPIFn('past');
                    this.orderPurchaseListAPIFn('perform');
                    break;
                case 'history':
                    // load
                    this.orderHistoryPurchaseListAPIFn();
                    break;
                default:
                    //searchable
                    this.totalFormat.searchable = false;
                    this.myOrderPropObj.pageType === 0 && this.orgDeptTopTreeAPIFn();
                    this.myOrderPropObj.pageType === 0 && this.orgEmpDropdownListAPIFn();
                    this.myOrderPropObj.pageType === 0 && this.warehouseUsableTopListAPIFn();
                    this.myOrderPropObj.pageType === 0 && this.loadSupplyUnits();
                    //add
                    if (this.editable.flag) {
                        this.panes[0].title = this.editable.type === 'revise' ? '编辑' : '浏览';
                        this.orderPurchaseInfoAPIFn({
                            partA_id: this.editable.obj.partA,
                            partB_id: this.editable.obj.partB,
                            po_so_inner_sn: this.editable.obj.po_so_inner_sn,
                            billsType: this.editable.obj.billsType
                        });
                    } else {
                        this.panes[0].title = '新增';
                        let data = [];
                        for (let i = 0; i < 10; i ++) {
                            data.push({
                                idx: i + 1,
                                product_name: '',
                                specification: '',
                                product_vendor_name: '',
                                status_string: '',
                                quantity: '',
                                unit_name: '',
                                batch_number: '',
                                unit_price_text: '',
                                money: '',
                                rate_string: '',
                                tax_unit_price: '',
                                tax_amount: '',
                                tax: ''
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
                    product_name: '',
                    specification: '',
                    product_vendor_name: '',
                    status_string: '',
                    quantity: '',
                    unit_name: '',
                    batch_number: '',
                    unit_price_text: '',
                    money: '',
                    rate_string: '',
                    tax_unit_price: '',
                    tax_amount: '',
                    tax: ''
                });
            }
        },
        searchSheetFn(obj) {
            if (this.curTab !== 'add') {
                this.addData = [];
                this.resetPaginations();
                this.searchFormObj.partB_name = obj.name;
                switch (this.curTab) {
                    case 'perform':
                    case 'past':
                        // load
                        this.orderPurchaseListAPIFn('perform');
                        this.orderPurchaseListAPIFn('past');
                        break;
                    case 'history':
                        // load
                        this.orderHistoryPurchaseListAPIFn();
                        break;
                 }
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
                if (this.curTab !== 'history') {
                    data[0].disabled = obj.from === obj.to && parseInt(this.addData[obj.from].status) === 1 ? false : true;
                }
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
                    if (!item.unit_inner_sn.length) {
                        item.unit_inner_sn = [item.unit_inner_sn];
                    }
                    data.push(item);
                }
            });
            return data;
        },
        // 【采购单位列表】
        async orderOrgListAPIFn(systemType, oid) {
            // isSale: 是否销售订单 0-不是 1-是
            await orderOrgListAPI(systemType, oid, this.myOrderPropObj.pageType).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.planModalOrgs = list.map((item) => {
                        return {
                            ...item,
                            value: item.org_id + '-' + item.billsType,
                            label: item.name
                        };
                    });
                    if (this.btnName === 'add') {
                        // 默认赋值
                        if (this.planModalOrgs.length) {
                            const curOrg = this.planModalOrgs[0].value,
                                arySplitValue = curOrg.split('-');
                            this.planModalOrgSn = [curOrg];
                            this.partB.oname = this.planModalOrgs[0].label;
                            if (this.myOrderPropObj.pageType === 0) {
                                this.planModalForm.partB = arySplitValue[0];
                                this.partB.partB_id = arySplitValue[0];
                            } else if (this.myOrderPropObj.pageType === 1) {
                                this.planModalForm.partA = arySplitValue[0];
                                this.partB.partA_id = arySplitValue[0];
                            }
                            this.billsType = arySplitValue[1];
                            this.partB.billsType = arySplitValue[1];
                        } else {
                            this.planModalOrgSn = [];
                            this.partB.oname = '';
                            if (this.myOrderPropObj.pageType === 0) {
                                this.planModalForm.partB = '';
                            } else if (this.myOrderPropObj.pageType === 1) {
                                this.planModalForm.partA = '';
                            }
                            this.billsType = '';
                            this.partB.billsType = '';
                        }
                    }
                    this.planModalForm.billsType = this.partB.billsType;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID查询经销商【所有部门】
        async orgDeptTopTreeAPIFn() {
            await orgDeptTopTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'deptTopTree');
                    this.treeDatasIndexAryFn(list, 'deptTopTreePlan');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【查询机构所有人员】用于下列列表
        async orgEmpDropdownListAPIFn() {
            await orgEmpDropdownListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'allEmpDropdownList');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【经销商 产品单位】 根据id查询所有上级产品单位
        async dealerUnitParentsAPIFn(record) {
            await dealerUnitParentsAPI(
                record.dealer_inner_sn ||
                    record.dealer_b_inner_sn ||
                    record.product_vendor_inner_sn ||
                    record.product_dealer_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let { info } = res;
                    record.unit_inner_sn = [];
                    record.isLoadingUnit = true;
                    let treeUnitList = (item) => {
                        record.unit_inner_sn.unshift(item.unit_inner_sn);
                        if (item.parent) {
                            treeUnitList(item.parent);
                        }
                    };
                    treeUnitList(info);
                }
            });
        },
        // 【经销商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async dealerUnitTreeAPIFn(record, btnName) {
            await dealerUnitTreeAPI(
                record.dealer_inner_sn ||
                    record.dealer_b_inner_sn ||
                    record.product_vendor_inner_sn ||
                    record.product_dealer_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn
            ).then((res) => {
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
                    if (res.tree.length) {
                        record.unitList = res.tree;
                        if (btnName === 'add') {
                            // 赋默认值
                            const curUnit = record.unitList[0];
                            record.unit_inner_sn = [curUnit.value];
                            record.unit_name = curUnit.label;
                            record.isLoadingUnit = true;
                        }
                        record.detail_inner_sn && !record.isLoadingUnit && this.dealerUnitParentsAPIFn(record);
                    }
                }
            });
        },
        // 【厂商 产品单位】 根据id查询所有上级产品单位
        async platformUnitParentsFn(record) {
            await platformUnitParents(
                record.vendor_inner_sn || record.vendor_b_inner_sn || record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let { info } = res;
                    record.unit_inner_sn = [];
                    record.isLoadingUnit = true;
                    let treeUnitList = (item) => {
                        record.unit_inner_sn.unshift(item.unit_inner_sn);
                        if (item.parent) {
                            treeUnitList(item.parent);
                        }
                    };
                    treeUnitList(info);
                }
            });
        },
        // 【厂商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async platformUnitTreeFn(record, btnName) {
            await platformUnitTree(
                record.vendor_inner_sn || record.vendor_b_inner_sn || record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn
            ).then((res) => {
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
                    if (res.tree.length) {
                        record.unitList = res.tree;
                        if (btnName === 'add') {
                            // 赋默认值
                            const curUnit = record.unitList[0];
                            record.unit_inner_sn = [curUnit.value];
                            record.unit_name = curUnit.label;
                            record.isLoadingUnit = true;
                        }
                        record.detail_inner_sn && !record.isLoadingUnit && this.platformUnitParentsFn(record);
                    }
                }
            });
        },
        // 查看【历史采购订单列表】
        async orderHistoryPurchaseListAPIFn(flag) {
            const { createTimeRange } = this.searchFormObj;
            let params = {
                partA_id: this.cookieInfo.get('userbelong'),
                pageNum: this.sheetPagination.pageNum,
                pageSize: this.sheetPagination.pageSize
            };

            if (this.searchFormObj.partB_name) {
                params.partB_name = this.searchFormObj.partB_name;
            }

            if (this.searchFormObj.order_sn) {
                params.order_sn = this.searchFormObj.order_sn;
            }

            if (this.searchFormObj.buyer_id) {
                params.buyer_id = this.searchFormObj.buyer_id;
            }

            if (this.searchFormObj.department_id) {
                params.department_id = this.searchFormObj.department_id;
            }

            // 订单生成时间区间
            if (createTimeRange.length >= 2) {
                params.start_date = createTimeRange[0].format('YYYY-MM-DD');
                params.end_date = createTimeRange[1].format('YYYY-MM-DD');
            }

            await orderHistoryPurchaseListAPI({
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res;
                    let startIdx = this.sheetPagination.pageSize * (this.sheetPagination.pageNum - 1);
                    this.calcPagination(total);
                    let data = rows.map((item, index) => {
                        return {
                            ...item,
                            idx: startIdx + index + 1,
                            key: Math.random(),
                            activeClass: '',
                            product_vendor_name: this.selectedVen.product_vendor_name ? this.selectedVen.product_vendor_name : this.selectedVen.partB_name,
                            status_string: this.earnStatus(item.status, 'planList'),
                            create_organization_type_string: this.earnOrgType(item.create_organization_type)
                        };
                    });

                    this.addData.push(...data);

                    if (this.addData.length <= 0) {
                        this.currentPurchaseHistoryTrObj = {};
                    } else {
                        if (typeof flag === 'boolean') {
                            const curItem = this.addData[this.addData.length - 1];
                            this.currentPurchaseHistoryTrObj = curItem;
                        } else if (typeof flag === 'number') {
                            this.addData.forEach((item) => {
                                if (item.index === flag) {
                                    this.currentPurchaseHistoryTrObj = item;
                                }
                            });
                        } else {
                            this.currentPurchaseHistoryTrObj = this.addData[0];
                        }
                    }
                } else {
                    this.$message.error(res.msg);
                }
            }).catch((e) => {
                console.log(e);
            });
        },
        // 查看【历史销售订单列表】
        async orderHistorySaleListAPIFn(flag) {
            this.saleHistoryPagination.loading = true;
            let params = {
                partB_id: this.cookieInfo.get('userbelong'),
                pageNum: this.saleHistoryPagination.current,
                pageSize: this.saleHistoryPagination.pageSize
            };

            if (this.searchFormObj.partA_name) {
                params.partA_name = this.searchFormObj.partA_name;
            }

            if (this.searchFormObj.order_sn) {
                params.order_sn = this.searchFormObj.order_sn;
            }

            await orderHistorySaleListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.saleHistoryList = rows.map((item, index) => {
                            return {
                                ...item,
                                index,
                                key: Math.random(),
                                activeClass: '',
                                status_string: this.earnOrgType(item.status)
                            };
                        });

                        if (this.saleHistoryList.length <= 0) {
                            this.currentSaleHistoryTrObj = {};
                        } else {
                            if (typeof flag === 'boolean') {
                                this.currentSaleHistoryTrObj = this.saleHistoryList[this.saleHistoryList.length - 1];
                            } else if (typeof flag === 'number') {
                                this.saleHistoryList.forEach((item) => {
                                    if (item.index === flag) {
                                        this.currentSaleHistoryTrObj = item;
                                    }
                                });
                            } else {
                                this.currentSaleHistoryTrObj = this.saleHistoryList[0];
                            }
                        }

                        this.saleHistoryPagination = {
                            ...this.saleHistoryPagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                        this.saleHistoryPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.saleHistoryPagination.loading = false;
                });
        },
        // 查看【采购订单列表】
        async orderPurchaseListAPIFn(tabName, flag, poSoInnerSn) {
            // tabName - 经销商/厂商/历史
            const { createTimeRange } = this.searchFormObj;
            if (tabName === this.systemTypes[0]) {
                this.purchaseDelarPagination.loading = true;
            } else if (tabName === this.systemTypes[1]) {
                this.purchaseVendorPagination.loading = true;
            }
            let params = {
                partA_id: this.cookieInfo.get('userbelong')
            };

            if (this.searchFormObj.partB_name) {
                params.partB_name = this.searchFormObj.partB_name;
            }

            params.pageNum = this.sheetPagination.pageNum;
            params.pageSize = this.sheetPagination.pageSize;

            // 1-医院 、 2-经销商、 3-厂商
            switch (parseFloat(this.cookieInfo.get('userSystemType'))) {
                case 1:
                    if (tabName === this.systemTypes[0]) {
                        // 医院 - 经销商
                        params.billsType = BillsType.HospitalDealer;
                    } else if (tabName === this.systemTypes[1]) {
                        // 医院 - 厂商
                        params.billsType = BillsType.HospitalVendor;
                    }
                    break;
                case 2:
                    if (tabName === this.systemTypes[0]) {
                        // 经销商 - 经销商
                        params.billsType = BillsType.DealerDealer;
                    } else if (tabName === this.systemTypes[1]) {
                        // 经销商 - 厂商
                        params.billsType = BillsType.DealerVendor;
                    }
                    break;
            }

            if (this.searchFormObj.partB_name) {
                params.partB_name = this.searchFormObj.partB_name;
            }

            if (this.searchFormObj.order_sn) {
                params.order_sn = this.searchFormObj.order_sn;
            }

            if (this.searchFormObj.buyer_id) {
                params.buyer_id = this.searchFormObj.buyer_id;
            }

            if (this.searchFormObj.department_id) {
                params.department_id = this.searchFormObj.department_id;
            }

            // 排序
            params.orderByColumn = 'create_time';
            params.isAsc = 'desc';

            // 订单生成时间区间
            if (createTimeRange.length >= 2) {
                params.start_date = createTimeRange[0].format('YYYY-MM-DD');
                params.end_date = createTimeRange[1].format('YYYY-MM-DD');
            }

            await orderPurchaseListAPI({
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res;
                    let startIdx = this.addData.length;
                    tabName === 'perform' && this.calcPagination(total);
                    tabName === 'past' && this.calcPagination2(total);
                    let data = rows.map((item, index) => {
                        let itemObj = {
                            ...item,
                            idx: startIdx + index + 1,
                            key: (Math.random() + 1).toString(36).substring(7),
                            activeClass: '',
                            product_vendor_name: this.selectedVen.product_vendor_name ? this.selectedVen.product_vendor_name : this.selectedVen.partB_name,
                            status_string: this.earnStatus(parseFloat(item.status), 'planList'),
                            create_organization_type_string: this.earnOrgType(item.create_organization_type)
                        };
                        if (itemObj.key === poSoInnerSn) {
                            this.currentPurchaseDelarTrObj = itemObj;
                        }
                        return itemObj;
                    });
                    this.addData.push(...data);

                    if (this.addData.length <= 0) {
                        this.currentPurchaseDelarTrObj = {};
                    } else {
                        if (typeof flag === 'number') {
                            this.addData.forEach((item, index) => {
                                if (index === flag) {
                                    this.currentPurchaseDelarTrObj = item;
                                }
                            });
                        } else if (!flag) {
                            this.currentPurchaseDelarTrObj = this.addData[0];
                        }
                    }
                } else {
                    this.$message.error(res.msg);
                }
            }).catch((e) => {
                console.log(e);
            });
        },
        // 查看【销售订单列表】
        async orderSaleListAPIFn(tabName, flag, poSoInnerSn) {
            // tabName - 医院/经销商/历史   poSoInnerSn - 新增订单id
            if (tabName === this.systemTypes[0]) {
                this.saleDelarPagination.loading = true;
            } else if (tabName === this.systemTypes[1]) {
                this.saleVendorPagination.loading = true;
            }
            let params = {
                partB_id: this.cookieInfo.get('userbelong')
            };

            if (tabName === this.systemTypes[0]) {
                params.pageNum = this.saleDelarPagination.current;
                params.pageSize = this.saleDelarPagination.pageSize;
            } else if (tabName === this.systemTypes[1]) {
                params.pageNum = this.saleVendorPagination.current;
                params.pageSize = this.saleVendorPagination.pageSize;
            }

            // 1-医院 、 2-经销商、 3-厂商
            switch (parseFloat(this.cookieInfo.get('userSystemType'))) {
                case 2:
                    if (tabName === this.systemTypes[0]) {
                        // 经销商 - 医院
                        params.billsType = BillsType.DealerHospital;
                    } else if (tabName === this.systemTypes[1]) {
                        // 经销商 - 经销商
                        params.billsType = BillsType.DealerDealer;
                    }
                    break;
                case 3:
                    if (tabName === this.systemTypes[0]) {
                        // 厂商 - 医院
                        params.billsType = BillsType.HospitalVendor;
                    } else if (tabName === this.systemTypes[1]) {
                        // 厂商 - 经销商
                        params.billsType = BillsType.DealerVendor;
                    }
                    break;
            }

            if (this.searchFormObj.partA_name) {
                params.partA_name = this.searchFormObj.partA_name;
            }

            if (this.searchFormObj.order_sn) {
                params.order_sn = this.searchFormObj.order_sn;
            }

            await orderSaleListAPI({
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res;
                    if (tabName === this.systemTypes[0]) {
                        this.saleDelarList = rows.map((item, index) => {
                            let itemObj = {
                                ...item,
                                index,
                                key: item.partA + '' + item.partB + item.po_so_inner_sn + item.billsType,
                                // key: Math.random(),
                                activeClass: '',
                                status: this.earnStatus(parseFloat(item.status), 'planList')
                            };
                            if (itemObj.key === poSoInnerSn) {
                                this.currentSaleDelarTrObj = itemObj;
                            }
                            return itemObj;
                        });

                        if (this.saleDelarList.length <= 0) {
                            this.currentSaleDelarTrObj = {};
                        } else {
                            if (typeof flag === 'number') {
                                this.saleDelarList.forEach((item) => {
                                    if (item.index === flag) {
                                        this.currentSaleDelarTrObj = item;
                                    }
                                });
                            } else if (!flag) {
                                this.currentSaleDelarTrObj = this.saleDelarList[0];
                            }
                        }

                        this.saleDelarPagination = {
                            ...this.saleDelarPagination,
                            total,
                            loading: false
                        };
                    } else if (tabName === this.systemTypes[1]) {
                        this.saleVendorList = rows.map((item, index) => {
                            let itemObj = {
                                ...item,
                                index,
                                key: item.partA + '' + item.partB + item.po_so_inner_sn + item.billsType,
                                // key: Math.random(),
                                activeClass: '',
                                status: parseFloat(item.status)
                            };
                            if (itemObj.key === poSoInnerSn) {
                                this.currentSaleVendorTrObj = itemObj;
                            }
                            return itemObj;
                        });

                        if (this.saleVendorList.length <= 0) {
                            this.currentsaleVendorTrObj = {};
                        } else {
                            if (typeof flag === 'number') {
                                this.saleVendorList.forEach((item) => {
                                    if (item.index === flag) {
                                        this.currentSaleVendorTrObj = item;
                                    }
                                });
                            } else if (!flag) {
                                this.currentSaleVendorTrObj = this.saleVendorList[0];
                            }
                        }

                        this.saleVendorPagination = {
                            ...this.saleVendorPagination,
                            total,
                            loading: false
                        };
                    }
                } else {
                    this.$message.error(res.msg);
                    if (tabName === this.systemTypes[0]) {
                        this.saleDelarPagination.loading = false;
                    } else if (tabName === this.systemTypes[1]) {
                        this.saleVendorPagination.loading = false;
                    }
                }
            })
            .catch(() => {
                if (tabName === this.systemTypes[0]) {
                    this.saleDelarPagination.loading = false;
                } else if (tabName === this.systemTypes[1]) {
                    this.saleVendorPagination.loading = false;
                }
            });
        },
        // 【生成】销售单
        async orderSaleBuildAPIFn(records) {
            let list = this.createServiceListFn(records);
            await orderSaleBuildAPI(list)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.changeTabFn('history');
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 【确认销售订单】
        async orderSaleConfirmAPIFn() {
            let params = {
                partA_id: this.planModalForm.partA,
                partB_id: this.planModalForm.partB,
                so_sn: '',
                po_so_inner_sn: this.planModalForm.po_so_inner_sn,
                billsType: this.billsType
            };
            await orderSaleConfirmAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.changeTabFn('perform');
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                        this.planModal.loadingSave = false;
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                    this.planModal.loadingSave = false;
                });
        },
        // 【确认采购订单】
        async orderPurchaseConfirmAPIFn() {
            let params = {
                partA_id: this.planModalForm.partA,
                partB_id: this.planModalForm.partB,
                so_sn: '',
                po_so_inner_sn: this.planModalForm.po_so_inner_sn,
                billsType: this.billsType
            };
            await orderPurchaseConfirmAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.planModal.loadingSave = false;
                        this.planModal.alert = false;
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                this.orderPurchaseListAPIFn(this.tabsActiveKey, this.currentPurchaseDelarTrObj.index);
                                this.wrapTableCloseLoadingFn();
                                this.purchaseDelarSelectedRowKeys = [];
                                break;
                            case this.systemTypes[1]:
                                this.orderPurchaseListAPIFn(this.tabsActiveKey, this.currentPurchaseVendorTrObj.index);
                                this.wrapTableCloseLoadingFn();
                                this.purchaseVendorSelectedRowKeys = [];
                                break;
                        }
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                        this.planModal.loadingSave = false;
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                    this.planModal.loadingSave = false;
                });
        },
        // 【确认订单】
        async orderConfirmAPIFn(records) {
            let list = this.createServiceListFn(records);
            await orderConfirmAPI(list)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.changeTabFn('history');
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 【勾选计划明细】生成采购订单
        async orderSaleDetailBuildAPIFn() {
            this.planModalPagination.loading = true;
            let list = [];
            this.planModalDatas.forEach((item) => {
                this.planModalSelectedRowKeys.forEach((cItem) => {
                    if (parseFloat(item.key) === parseFloat(cItem)) {
                        list.push({
                            partA_id: item.partA,
                            partB_id: item.partB,
                            po_so_inner_sn: item.po_so_inner_sn,
                            detail_inner_sn: item.detail_inner_sn,
                            product_type: item.product_type,
                            billsType: this.planModalForm.billsType
                        });
                    }
                });
            });
            await orderSaleDetailBuildAPI(list)
                .then((res) => {
                    this.planModal.loadingAdd = false;
                    this.planModalPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        switch (this.myOrderPropObj.pageType) {
                            case 1:
                                switch (this.tabsActiveKey) {
                                    case this.systemType[0]:
                                        this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleDelarTrObj.index);
                                        this.wrapTableCloseLoadingFn();
                                        break;
                                    case this.systemType[1]:
                                        this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleVendorTrObj.index);
                                        this.wrapTableCloseLoadingFn();
                                        break;
                                }
                                break;
                        }
                    } else {
                        this.$message.error(res.msg);
                        this.planModal.loadingAdd = false;
                        this.planModalPagination.loading = false;
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.planModal.loadingAdd = false;
                    this.planModalPagination.loading = false;
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 【终止订单】
        async orderStopAPIFn(records) {
            let list = this.createServiceListFn(records);
            await orderStopAPI(list)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.changeTabFn('history');
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 【添加采购订单】
        async orderPurchaseAddAPIFn(status) {
            if ((!this.planModalWarehouseSn && this.planModalWarehouseSn !== 0) || !this.planModalWarehouse) {
                this.$message.error('请选择仓库');
                this.planModal.loadingSave = false;
                this.planModal.loadingSaveDraft = false;
                throw new Error('warehouse_inner_sn');
            }
            let params = {
                buyer_id: this.planModalForm.buyer_id,
                department_id: this.planModalForm.department_inner_sn,
                description: this.planModalForm.description,
                expect_aog_date: this.planModalForm.expect_aog_date,
                partA_id: this.cookieInfo.get('userbelong'),
                partB_id: this.planModalForm.partB,
                warehouse_inner_sn: this.planModalWarehouseSn, // 收货仓库ID
                warehouse_name: this.planModalWarehouse, // 收货仓库名称
                delivery_address: this.planModalLocation,
                po_sn: '',
                billsType: this.billsType,
                status
            };
            params.details = [];
            this.detailList.forEach((item) => {
                item.rate = this.reNumber(item.rate / 100); // 税率和折扣保存时除以100
                params.details.push(item);
            });

            await orderPurchaseAddAPI(params)
                .then((res) => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }

                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.planModalForm = {
                            ...this.planModalForm,
                            ...info
                        };
                        this.editable.type = 'just';
                        this.printOfficalPurchaseOrderFn(status);
                        // const key = info.partA + '' + info.partB + info.po_so_inner_sn + info.billsType
                        switch (info.billsType) {
                            case 'HD':
                            case 'DD':
                                this.changeTabFn('perform');
                                break;
                            case 'HV':
                            case 'DV':
                                this.changeTabFn('past');
                                break;
                        }

                        this.closePlanModalFn();
                        this.resetPlanModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }
                });
        },
        // 【添加销售订单】
        async orderSaleAddAPIFn(status) {
            let params = {
                description: this.planModalForm.description,
                details: this.detailList,
                expect_aog_date: this.planModalForm.expect_aog_date,
                partA_id: this.planModalForm.partA,
                partB_id: this.cookieInfo.get('userbelong'),
                po_sn: '',
                billsType: this.billsType,
                status
            };

            await orderSaleAddAPI(params)
                .then((res) => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }

                    if (parseFloat(res.code) === 0) {
                        const { info } = res,
                            key = info.partA + '' + info.partB + info.po_so_inner_sn + info.billsType;
                        switch (parseFloat(this.billsType)) {
                            case 0:
                            case 2:
                                if (this.saleDelarList.length >= this.saleDelarPagination.pageSize) {
                                    let pageNum =
                                        Math.floor(this.saleDelarPagination.total / this.saleDelarPagination.pageSize) +
                                        1;
                                    this.saleDelarPagination.current = pageNum;
                                }
                                this.orderSaleListAPIFn('perform', true, key);
                                this.tabsActiveKey = this.systemTypes[0];
                                break;
                            case 1:
                            case 3:
                                if (this.saleVendorList.length >= this.saleVendorPagination.pageSize) {
                                    let pageNum =
                                        Math.floor(
                                            this.saleVendorPagination.total / this.saleVendorPagination.pageSize
                                        ) + 1;
                                    this.saleVendorPagination.current = pageNum;
                                }
                                this.orderSaleListAPIFn('past', true, key);
                                this.tabsActiveKey = this.systemTypes[1];
                                break;
                        }
                        this.closePlanModalFn();
                        this.resetPlanModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }
                });
        },
        // 【终止订单明细】
        async orderDetailStopAPIFn(record) {
            let params = {
                partA_id: record.partA,
                partB_id: record.partB,
                po_so_inner_sn: record.po_so_inner_sn,
                detail_inner_sn: record.detail_inner_sn,
                product_type: record.product_type,
                billsType: this.planModalForm.billsType
            };

            await orderDetailStopAPI(params)
                .then((res) => {
                    record.loading = false;
                    if (parseFloat(res.code) === 0) {
                        record.switch = !record.switch;
                        record.status = 3;
                        this.$refs.xTablePlanModalInfo.updateData();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    record.loading = false;
                });
        },
        // 【反终止订单明细】
        async orderDetailUnstopAPIFn(record) {
            let params = {
                partA_id: record.partA,
                partB_id: record.partB,
                po_so_inner_sn: record.po_so_inner_sn,
                detail_inner_sn: record.detail_inner_sn,
                product_type: record.product_type,
                billsType: this.planModalForm.billsType
            };

            await orderDetailUnstopAPI(params)
                .then((res) => {
                    record.loading = false;
                    if (parseFloat(res.code) === 0) {
                        record.switch = !record.switch;
                        record.status = 0;
                        this.$refs.xTablePlanModalInfo.updateData();
                        // this.closePlanModalFn()
                        // this.resetPlanModalFn()
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    record.loading = false;
                });
        },
        // 【修改】采购订单信息
        async orderPurchaseEditAPIFn(status) {
            if ((!this.planModalWarehouseSn && this.planModalWarehouseSn !== 0) || !this.planModalWarehouse) {
                this.$message.error('请选择仓库');
                this.planModal.loadingSave = false;
                this.planModal.loadingSaveDraft = false;
                throw new Error('warehouse_inner_sn');
            }
            let params = {
                status,
                billsType: this.planModalForm.billsType,
                partA_id: this.planModalForm.partA,
                partB_id: this.planModalForm.partB,
                po_so_inner_sn: this.planModalForm.po_so_inner_sn,
                warehouse_inner_sn: this.planModalWarehouseSn, // 收货仓库ID
                warehouse_name: this.planModalWarehouse, // 收货仓库名称
                delivery_address: this.planModalLocation
            };

            params.details = [];
            this.detailList.forEach((item) => {
                item.rate = this.reNumber(item.rate / 100); // 税率保存时除以100
                params.details.push(item);
            });

            if (this.planModalForm.buyer_id !== this.planModalForm.buyer_id_old) {
                params.buyer_id = this.planModalForm.buyer_id;
                params.buyer_id_old = this.planModalForm.buyer_id_old;
                params.buyer_name = this.planModalForm.buyer_name;
            }

            if (this.planModalForm.department_inner_sn !== this.planModalForm.department_inner_sn_old) {
                params.department_inner_sn = this.planModalForm.department_inner_sn;
                params.department_inner_sn_old = this.planModalForm.department_inner_sn_old;
                params.department_name = this.planModalForm.department_name;
            }

            if (this.planModalForm.description !== this.planModalForm.description_old) {
                params.description = this.planModalForm.description;
                params.description_old = this.planModalForm.description_old;
            }

            if (this.planModalForm.expect_aog_date !== this.planModalForm.expect_aog_date_old) {
                params.expect_aog_date = this.planModalForm.expect_aog_date;
                params.expect_aog_date_old = this.planModalForm.expect_aog_date_old;
            }

            // if (this.planModalForm.po_sn !== this.planModalForm.po_sn_old) {
            //     params.po_sn = this.planModalForm.po_sn
            //     params.po_sn_old = this.planModalForm.po_sn_old
            // }

            await orderPurchaseEditAPI(params)
                .then((res) => {
                    this.planModal.loadingAudit = false;
                    const { code, info } = res;
                    this.planModalForm = {
                        ...this.planModalForm,
                        ...info
                    };
                    if (parseFloat(code) === 0) {
                        this.printOfficalPurchaseOrderFn(status);
                        switch (info.billsType) {
                            case 'HD':
                            case 'DD':
                                this.changeTabFn('perform');
                                break;
                            case 'HV':
                            case 'DV':
                                this.changeTabFn('past');
                                break;
                        }
                        // this.closePlanModalFn();
                        this.resetPlanModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.planModal.loadingAudit = false;
                });
        },
        /// 保存编辑 —— [正式状态] 打印
        printOfficalPurchaseOrderFn(status) {
            if (parseInt(status) === 1) {
                this.handlePrint();
            }
        },
        // 【修改】销售订单信息
        async orderSaleEditAPIFn(status) {
            let params = {
                details: this.detailList,
                status,
                billsType: this.planModalForm.billsType,
                partA_id: this.planModalForm.partA,
                partB_id: this.planModalForm.partB,
                po_so_inner_sn: this.planModalForm.po_so_inner_sn
            };

            if (this.planModalForm.description !== this.planModalForm.description_old) {
                params.description = this.planModalForm.description;
                params.description_old = this.planModalForm.description_old;
            }

            if (this.planModalForm.expect_aog_date !== this.planModalForm.expect_aog_date_old) {
                params.expect_aog_date = this.planModalForm.expect_aog_date;
                params.expect_aog_date_old = this.planModalForm.expect_aog_date_old;
            }

            // if (this.planModalForm.so_sn !== this.planModalForm.so_sn_old) {
            //     params.so_sn = this.planModalForm.so_sn
            //     params.so_sn_old = this.planModalForm.so_sn_old
            // }

            await orderSaleEditAPI(params)
                .then((res) => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }

                    if (parseFloat(res.code) === 0) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleDelarTrObj.index);
                                break;
                            case this.systemTypes[1]:
                                this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleVendorTrObj.index);
                                break;
                        }
                        this.closePlanModalFn();
                        this.resetPlanModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }
                });
        },
        // 【删除】订单
        async orderDelAPIFn(checkRecordRows) {
            const params = checkRecordRows.map((item) => {
                return {
                    'billsType': item.billsType,
                    'partA_id': item.partA,
                    'partB_id': item.partB,
                    'po_so_inner_sn': item.po_so_inner_sn
                };
            });

            // switch (this.myOrderPropObj.pageType) {
            // case 0:
            //     switch (this.tabsActiveKey) {
            //     case this.systemTypes[0]:
            //         this.purchaseDelarPagination.loading = true
            //         params = {
            //             partA_id: this.currentPurchaseDelarTrObj.partA,
            //             partB_id: this.currentPurchaseDelarTrObj.partB,
            //             po_so_inner_sn: this.currentPurchaseDelarTrObj.po_so_inner_sn,
            //             billsType: this.currentPurchaseDelarTrObj.billsType
            //         }
            //         break
            //     case this.systemTypes[1]:
            //         this.purchaseVendorPagination.loading = true
            //         params = {
            //             partA_id: this.currentPurchaseVendorTrObj.partA,
            //             partB_id: this.currentPurchaseVendorTrObj.partB,
            //             po_so_inner_sn: this.currentPurchaseVendorTrObj.po_so_inner_sn,
            //             billsType: this.currentPurchaseVendorTrObj.billsType
            //         }
            //         break
            //     case this.systemTypes[2]:
            //         this.purchaseHistoryPagination.loading = true
            //         params = {
            //             partA_id: this.currentPurchaseHistoryTrObj.partA,
            //             partB_id: this.currentPurchaseHistoryTrObj.partB,
            //             po_so_inner_sn: this.currentPurchaseHistoryTrObj.po_so_inner_sn,
            //             billsType: this.currentPurchaseHistoryTrObj.billsType
            //         }
            //         break
            //     }
            //     break
            // case 1:
            //     switch (this.tabsActiveKey) {
            //     case this.systemTypes[0]:
            //         this.saleDelarPagination.loading = true
            //         params = {
            //             partA_id: this.currentSaleDelarTrObj.partA,
            //             partB_id: this.currentSaleDelarTrObj.partB,
            //             po_so_inner_sn: this.currentSaleDelarTrObj.po_so_inner_sn,
            //             billsType: this.currentSaleDelarTrObj.billsType
            //         }
            //         break
            //     case this.systemTypes[1]:
            //         this.saleVendorPagination.loading = true
            //         params = {
            //             partA_id: this.currentSaleVendorTrObj.partA,
            //             partB_id: this.currentSaleVendorTrObj.partB,
            //             po_so_inner_sn: this.currentSaleVendorTrObj.po_so_inner_sn,
            //             billsType: this.currentSaleVendorTrObj.billsType
            //         }
            //         break
            //     case this.systemTypes[2]:
            //         this.saleHistoryPagination.loading = true
            //         params = {
            //             partA_id: this.currentSaleHistoryTrObj.partA,
            //             partB_id: this.currentSaleHistoryTrObj.partB,
            //             po_so_inner_sn: this.currentSaleHistoryTrObj.po_so_inner_sn,
            //             billsType: this.currentSaleHistoryTrObj.billsType
            //         }
            //         break
            //     }
            //     break
            // }

            await orderDelAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.changeTabFn(this.curTab);
                } else {
                    this.$message.error(res.msg);
                    this.wrapTableCloseLoadingFn();
                }
            });
        },
        reNumber(data) {
            let num = data.toString();
            let numA = 0;
            if (num.indexOf('.') !== -1) {
                numA = num.split('.')[1].length;
            }
            if (numA === 3) {
                data += 0.0001;
            }
            if (numA > 10) {
                // toFixed会返回字符串  需要转换为数字
                const n = data.toFixed(4);
                data = parseFloat(n);
            }
            return data;
        },
        // 查看【采购订单信息】
        async orderPurchaseInfoAPIFn(infoObj) {
            const params = { ...infoObj };
            await orderPurchaseInfoAPI({
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    this.planModalForm = {
                        ...info,
                        department_inner_sn: info.department_inner_sn
                            ? info.department_inner_sn
                            : JSON.parse(localStorage.getItem('userInfo')).Departments[0].Department_inner_sn,
                        department_name: info.department_name
                            ? info.department_name
                            : JSON.parse(localStorage.getItem('userInfo')).Departments[0].Name,
                        buyer_id: info.buyer_inner_sn
                            ? info.buyer_inner_sn
                            : JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn,
                        buyer_name: info.buyer_name
                            ? info.buyer_name
                            : JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
                        buyer_id_old: info.buyer_inner_sn,
                        department_inner_sn_old: info.department_inner_sn,
                        description_old: info.description,
                        expect_aog_date_old: info.expect_aog_date,
                        po_sn_old: info.po_sn,
                        status: parseFloat(info.status)
                    };
                    const currentWarehouse = this.warehouseList.find(
                        (item) => item.warehouse_model_inner_sn === info.warehouse_inner_sn
                    );
                    if (currentWarehouse) {
                        this.planModalForm.warehouse_name = currentWarehouse.name;
                        this.planModalWarehouse = currentWarehouse.name;
                    }
                    this.expectAogDate = info.expect_aog_date ? moment(info.expect_aog_date, 'YYYY-MM-DD') : null;
                    this.editInfoFormDepartmentInnerSn = this.planModalForm.department_inner_sn;
                    this.planModalOrgSn = info.partB + '-' + info.billsType;
                    this.planModalEmployeSn = info.buyer_inner_sn
                        ? info.buyer_inner_sn
                        : JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn;
                    this.planModalDepartmentSn = info.department_inner_sn
                        ? info.department_inner_sn
                        : JSON.parse(localStorage.getItem('userInfo')).Departments[0].Department_inner_sn;
                    this.planModalLocation = info.delivery_address;
                    // 检索当前账户仓库列表，若单据中的仓库不在列表中，则将其置空
                    if (this.warehouseList.some((warehouse) => warehouse.key === info.warehouse_inner_sn)) {
                        this.planModalWarehouseSn = info.warehouse_inner_sn;
                    } else {
                        this.planModalWarehouseSn = undefined;
                    }
                    this.partB.partB_id = info.partB;
                    this.billsType = info.billsType;
                    this.partB.billsType = info.billsType;
                    this.partB.oname = info.partB_name || '';
                    this.supplySearch = this.partB.oname;
                    // 检测采购单位是否在采购单位列表可以碰撞到值
                    if (info.details && info.details.length) {

                        this.addData = info.details.map((item, index) => {
                            // ******** 注意 ********
                            // item.unit_price 来自后端数据，代表含税单价，含税单价在前端对应tax_unit_price字段
                            const { quantity, unit_price: unitPrice, rate } = item;
                            const { multiply, divide, add, round } = this.$XEUtils;
                            return {
                                ...item,
                                idx: index + 1,
                                key: (Math.random() + 1).toString(36).substring(7),
                                activeClass: '',
                                unitList: item.unit_inner_sn
                                    ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                    : [],
                                unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                                isLoadingUnit: false, // 是否加载过整棵树
                                status: parseFloat(item.status),
                                status_string: this.earnStatus(item.status, 'planOrder'),
                                loading: false,
                                switch:
                                    (this.planModalForm.status === 2 || this.planModalForm.status === 3) &&
                                    (item.status === 0 || item.status === 2),
                                unitPriceList: [],
                                rate: this.reNumber(rate * 100), // 税率显示时乘以100
                                rate_string: this.reNumber(rate * 100),
                                unit_price_text: divide(unitPrice, add(rate, 1)), // 不含税单价
                                tax_unit_price: unitPrice, // 含税单价
                                tax_amount: round(multiply(quantity, unitPrice), 4), // 含税金额
                                vendor_inner_sn: item.product_vendor_inner_sn,
                                focusDatas: [
                                    false,
                                    index === 0 && parseFloat(info.status) <= 1,
                                    false,
                                    false,
                                    false,
                                    false
                                ]
                            };
                        });
                        for (let i = 0; i < this.addData.length; i++) {
                            let item = this.addData[i];
                            if (parseInt(this.planModalForm.status) <= 1) {
                                // 是草稿和正式
                                this.contractGetOrderPriceAPIFn(item);
                            }
                            this.venUnitTreeFn(item);
                        }
                    }
                } else {
                    this.$message.error(res.msg);
                }
            })
            .catch(() => {
                this.planModalPagination.loading = false;
            });
        },
        resetFormFocusFn() {
            this.relevantInstitutionsFocus = false;
            this.isParentKeyDownEnter = false;
        },
        clickDescriptionFn() {
            this.resetFocusFn();
            this.resetFormFocusFn();
        },
        // 查看【销售订单信息】
        async orderSaleInfoAPIFn(infoObj) {
            this.planModalPagination.loading = true;
            const params = { ...infoObj };
            await orderSaleInfoAPI({
                params
            })
                .then((res) => {
                    this.planModalPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.planModalForm = {
                            ...info,
                            description_old: info.description,
                            expect_aog_date_old: info.expect_aog_date,
                            so_sn_old: info.so_sn,
                            status: parseFloat(info.status)
                        };
                        this.expectAogDate = info.expect_aog_date ? moment(info.expect_aog_date, 'YYYY-MM-DD') : null;
                        this.editInfoFormDepartmentInnerSn = this.planModalForm.department_inner_sn;
                        this.planModalOrgSn = [info.partA + '-' + info.billsType];
                        this.billsType = info.billsType;
                        this.partB.partA_id = info.partA;
                        this.billsType = info.billsType;
                        this.partB.billsType = info.billsType;
                        this.partB.oname = info.partA_name || '';

                        if (info.details && info.details.length) {
                            // info.details.push({
                            //     key: Math.random(),
                            //     activeClass: ''
                            // })
                            this.planModalDatas = info.details.map((item, index) => {
                                let items = {};
                                if (index === info.details.length - 1) {
                                    items = { ...item };
                                } else {
                                    items = {
                                        ...item,
                                        index,
                                        key: Math.random(),
                                        activeClass: '',
                                        unitList: item.unit_inner_sn
                                            ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                            : [],
                                        unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                                        isLoadingUnit: false, // 是否加载过整棵树
                                        status: parseFloat(item.status),
                                        loading: false,
                                        switch:
                                            (this.planModalForm.status === 2 || this.planModalForm.status === 3) &&
                                            (item.status === 0 || item.status === 2)
                                    };
                                }
                                return items;
                            });
                        }
                        this.currentTrPlanModalObj = this.planModalDatas.length ? this.planModalDatas[0] : {};
                        !this.isEditDisabled && this.dblclickAddProductFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.planModalPagination.loading = false;
                });
        },
        // 查询价格
        async contractGetOrderPriceAPIFn(record) {
            let tempUnitInnerSn = Array.isArray(record.unit_inner_sn)
                ? record.unit_inner_sn[record.unit_inner_sn.length - 1]
                : record.unit_inner_sn;
            let params = {
                billsType: this.planModalForm.billsType,
                organization_inner_sn: this.planModalForm.partB,
                product_inner_sn: record.product_inner_sn,
                product_type: record.product_type,
                product_vendor_inner_sn:
                    record.product_vendor_inner_sn || record.vendor_inner_sn || record.dealer_inner_sn,
                specification_inner_sn: record.specification_inner_sn,
                unit_inner_sn: typeof tempUnitInnerSn === 'undefined' ? 0 : tempUnitInnerSn
            };
            await contractGetOrderPriceAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    record.unitPriceList = list || [];
                    // if (record.unitPriceList.length && typeof record.detail_inner_sn === 'undefined') {
                    //     // 动态更新含税单价
                    //     this.updateProductTaxUnitPrice(list[0], this.planModalDatas.indexOf(record));
                    // }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【删除】订/单明细
        async orderDetailDelAPIFn(record) {
            this.planModalPagination.loading = true;
            let params = {
                po_so_inner_sn: record.po_so_inner_sn,
                detail_inner_sn: record.detail_inner_sn,
                product_type: record.product_type,
                billsType: this.planModalForm.billsType
            };

            if (this.myOrderPropObj.pageType === 0) {
                params.partA_id = this.$cookie.get('userbelong');
                params.partB_id = record.partB;
            } else if (this.myOrderPropObj.pageType === 1) {
                params.partA_id = record.partA;
                params.partB_id = this.$cookie.get('userbelong');
            }

            await orderDetailDelAPI(params)
                .then((res) => {
                    this.planModalPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
                        xTablePlanModalInfo.remove(record);
                        this.planModalDatas = xTablePlanModalInfo.getTableData().tableData;
                        xTablePlanModalInfo.setCurrentRow(this.planModalDatas[0]);
                        this.dblclickAddProductFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.planModalPagination.loading = false;
                });
        },
        // 设置后端所需要的格式
        changeTopSearchAllTreeListFn(value) {
            this.searchFormObj.department_id = value && value.length ? value[0] : '';
        },
        changeTopSearchEmpDropdownFn(value) {
            // this.searchFormObj.buyer_id = value && value.length ? value[0] : ''
            this.searchFormObj.buyer_id = value;
        },
        // 更新顶部搜索条件：单据生成时间
        changeTopSearchCreateTimeRange(momentArray) {
            this.searchFormObj.createTimeRange = momentArray;
        },
        changePlanModalOrgSnFn(value, label) {
            if (this.myOrderPropObj.pageType === 0) {
                if (typeof value === 'string' && value.length) {
                    const arySplitValue = value.split('-');
                    this.planModalForm.partB = arySplitValue[0];
                    this.partB.partB_id = arySplitValue[0];
                    this.partB.billsType = arySplitValue[1];
                    this.billsType = arySplitValue[1];
                } else {
                    this.planModalForm.partB = '';
                    this.partB.billsType = '';
                    this.billsType = '';
                }
            } else if (this.myOrderPropObj.pageType === 1) {
                if (typeof value === 'string' && value.length) {
                    const arySplitValue = value.split('-');
                    this.planModalForm.partA = arySplitValue[0];
                    this.partB.partA_id = arySplitValue[0];
                    this.billsType = arySplitValue[1];
                    this.partB.billsType = arySplitValue[1];
                } else {
                    this.planModalForm.partA = '';
                    this.partB.billsType = '';
                    this.billsType = '';
                }
            }
            this.partB.oname = label.length ? label : '';
            this.planModalForm.billsType = this.partB.billsType;
            this.planModalOrgSn = value;
            if (this.btnName === 'add') {
                this.planModalDatas = [];
            }
        },
        changePlanModalEmployeSnFn(value, label) {
            this.planModalEmployeSn = value;
            this.planModalForm.buyer_id = value || '';
            this.planModalForm.buyer_name = Array.isArray(label) ? label[0] : label;
        },
        changePlanModalDepartmentSnFn(value, label) {
            this.planModalDepartmentSn = value;
            this.planModalForm.department_inner_sn = value || '';
            this.planModalForm.department_name = Array.isArray(label) ? label[0] : label;
        },
        // value = warehouse_model_inner_sn
        changewareHouseFn(value, label) {
            this.planModalForm.warehouse_name = label[0];
            this.planModalForm.warehouse_inner_sn = value;
            this.planModalWarehouse = label[0];
            this.planModalWarehouseSn = value;
        },
        changeDeliveryAddressFn(e) {
            let { value } = e.target;
            this.planModalForm.delivery_address = value;
        },
        // 顶部搜索
        topSearchFn() {
            if (!this.checkSearchInfoFormMsgFn()) return;
            this.refreshPageFn();
        },
        checkSearchInfoFormMsgFn() {
            if (this.myOrderPropObj.pageType === 0 && this.$Utils.checkInputValFn(this.searchFormObj.partB_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.placeholderComputed}${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (this.myOrderPropObj.pageType === 1 && this.$Utils.checkInputValFn(this.searchFormObj.partA_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.placeholderComputed}${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (this.hightSearchObj.isArrow && this.$Utils.checkInputValFn(this.searchFormObj.order_sn)) {
                this.$warning({
                    title: '操作提示',
                    content: `单据编号${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 顶部按钮的事件
        groupBtnFn(btnName) {
            this.btnName = btnName;
            this.resetPlanModalFn();
            if (this.btnName === 'add' || this.btnName === 'edit') {
                // this.orderOrgListAPIFn(this.cookieInfo.get('userSystemType'), this.cookieInfo.get('userbelong'))
                this.myOrderPropObj.pageType === 0 && this.orgDeptTopTreeAPIFn();
                this.myOrderPropObj.pageType === 0 && this.orgEmpDropdownListAPIFn();
                this.myOrderPropObj.pageType === 0 && this.warehouseUsableTopListAPIFn();
                if (this.$refs.xTablePlanModalInfo) this.$refs.xTablePlanModalInfo.updateData();
            }
            if (!this.checkSecltedTrFn()) return;
            switch (btnName) {
                case 'add': // 新增
                    this.planModal = {
                        title: `${this.$route.meta.title} - 新增`,
                        alert: true,
                        loading: false
                    };
                    this.relevantInstitutionsFocus = true;
                    this.$Utils.globalDragCenterFn('modal-add');
                    this.dblclickAddProductFn();
                    break;
                case 'edit': // 修改
                    this.planModal = {
                        title: `${this.$route.meta.title} - 修改`,
                        alert: true,
                        loading: false
                    };
                    this.$Utils.globalDragCenterFn('modal-add');
                    break;
                case 'remove': {
                    // 删除
                    let checkRecordRows = [];
                    if (this.tabsActiveKey === this.systemTypes[0]) {
                        checkRecordRows = this.$refs.xPurchaseDelarTable.getSelectRecords();
                    } else if (this.tabsActiveKey === this.systemTypes[1]) {
                        checkRecordRows = this.$refs.xPurchaseVendorTable.getSelectRecords();
                    }
                    if (checkRecordRows.length <= 0) {
                        this.$warning({
                            title: '操作提示',
                            content: this.$Utils.tips().noCheckbox,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        return;
                    }
                    if (!this.removeSelfCreateOrganizationTypeFn(checkRecordRows)) {
                        this.$warning({
                            title: '操作提示',
                            content: '只能删除自建的产品',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        return;
                    }
                    if (!checkRecordRows.every((item) => parseInt(item.status) === 0 || parseInt(item.status) === 1)) {
                        this.$warning({
                            title: '操作提示',
                            content: '只能删除【草稿和正式】状态的订单。',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        return;
                    }
                    this.orderDelAPIFn(checkRecordRows);
                    break;
                }
                case 'submit': // 确认
                    if (this.myOrderPropObj.pageType === 0) {
                        if (this.tabsActiveKey === this.systemTypes[0]) {
                            const checkboxRecords = this.$refs.xPurchaseDelarTable.getSelectRecords();
                            this.checkCreateOrganizationTypeListFn(checkboxRecords) && this.orderConfirmAPIFn();
                        } else if (this.tabsActiveKey === this.systemTypes[1]) {
                            const checkboxRecords = this.$refs.xPurchaseVendorTable.getSelectRecords();
                            this.checkCreateOrganizationTypeListFn(checkboxRecords) && this.orderConfirmAPIFn();
                        }
                    } else {
                        this.orderConfirmAPIFn();
                    }
                    break;
                case 'build': // 生成
                    this.orderSaleBuildAPIFn();
                    break;
                case 'stop': // 终止
                    this.checkOrderStopFn();
                    break;
            }
        },
        checkOrderStopFn(records) {
            if (!records.every((item) => parseInt(item.status) === 2 || parseInt(item.status) === 3)) {
                this.$warning({
                    title: '操作提示',
                    content: '只能终止【已审核和已确认】状态的订单。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        hightSearchIconFn() {
            if (this.myOrderPropObj.pageType === 0) {
                this.topSearchEmpDropdownList.length <= 0 && this.orgEmpDropdownListAPIFn();
                this.topSearchAllTreeList.length <= 0 && this.orgDeptTopTreeAPIFn();
            }
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        // 开发临时函数
        developmentFn() {
            this.$warning({
                title: '操作提示',
                content: '该功能在紧张开发中，敬请期待',
                okText: '知道了',
                centered: true,
                maskClosable: false
            });
        },
        // 创建后端所需的list格式数据
        createServiceListFn(records) {
            let list = [];
            records.forEach((item) => {
                list.push({
                    partA_id: item.partA,
                    partB_id: item.partB,
                    po_so_inner_sn: item.po_so_inner_sn,
                    billsType: item.billsType
                });
            });                    
            return list;
        },
        // 外层表格的loading关闭
        wrapTableCloseLoadingFn() {
            this.changeTabFn(this.curTab);
        },
        checkSecltedTrFn() {
            let flag = true;
            switch (this.myOrderPropObj.pageType) {
                case 0:
                    if (
                        this.tabsActiveKey === this.systemTypes[0] &&
                        Object.keys(this.currentPurchaseDelarTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    } else if (
                        this.tabsActiveKey === this.systemTypes[1] &&
                        Object.keys(this.currentPurchaseVendorTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    } else if (
                        this.tabsActiveKey === this.systemTypes[2] &&
                        Object.keys(this.currentPurchaseHistoryTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    }
                    break;
                case 1:
                    if (
                        this.tabsActiveKey === this.systemTypes[0] &&
                        Object.keys(this.currentSaleDelarTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    } else if (
                        this.tabsActiveKey === this.systemTypes[1] &&
                        Object.keys(this.currentSaleVendorTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    } else if (
                        this.tabsActiveKey === this.systemTypes[2] &&
                        Object.keys(this.currentSaleHistoryTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    }
                    break;
            }

            if (!flag) return flag;

            const xPurchaseDelarTable = this.$refs.xPurchaseDelarTable,
                xPurchaseVendorTable = this.$refs.xPurchaseVendorTable;
            this.purchaseDelarSelectedRowKeys = xPurchaseDelarTable && xPurchaseDelarTable.getSelectRecords();
            this.purchaseVendorSelectedRowKeys = xPurchaseVendorTable && xPurchaseVendorTable.getSelectRecords();

            const contentNoData = '请勾选表格中的复选框，再进行相关操作。';
            switch (this.btnName) {
                case 'submit': {
                    let contentSubmit = '只能确认【已审核】的状态。',
                        contentMySelf = '不能确认自己创建的订单。';
                    const isOrderPage = this.myOrderPropObj.pageType; // 判断是0-采购订单  1-销售订单
                    if (isOrderPage === 0) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                if (this.purchaseDelarSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.purchaseDelarList.length; i++) {
                                        let item = this.purchaseDelarList[i];
                                        for (let j = 0; j < this.purchaseDelarSelectedRowKeys.length; j++) {
                                            let cItem = this.purchaseDelarSelectedRowKeys[j];
                                            if (flag && parseFloat(item.key) === parseFloat(cItem.key)) {
                                                if (!(item.status === 2)) {
                                                    flag = false;
                                                    this.$warning({
                                                        title: '操作提示',
                                                        content: contentSubmit,
                                                        okText: '知道了',
                                                        centered: true,
                                                        maskClosable: false
                                                    });
                                                    break;
                                                }
                                                if (flag && item.create_organization_type === isOrderPage) {
                                                    flag = false;
                                                    this.$warning({
                                                        title: '操作提示',
                                                        content: contentMySelf,
                                                        okText: '知道了',
                                                        centered: true,
                                                        maskClosable: false
                                                    });
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            case this.systemTypes[1]:
                                if (this.purchaseVendorSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.purchaseVendorList.length; i++) {
                                        let item = this.purchaseVendorList[i];
                                        for (let j = 0; j < this.purchaseVendorSelectedRowKeys.length; j++) {
                                            let cItem = this.purchaseVendorSelectedRowKeys[j];
                                            if (flag && parseFloat(item.key) === parseFloat(cItem.key)) {
                                                if (!(item.status === 2)) {
                                                    flag = false;
                                                    this.$warning({
                                                        title: '操作提示',
                                                        content: contentSubmit,
                                                        okText: '知道了',
                                                        centered: true,
                                                        maskClosable: false
                                                    });
                                                    break;
                                                }
                                                if (flag && item.create_organization_type === isOrderPage) {
                                                    flag = false;
                                                    this.$warning({
                                                        title: '操作提示',
                                                        content: contentMySelf,
                                                        okText: '知道了',
                                                        centered: true,
                                                        maskClosable: false
                                                    });
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    } else if (this.myOrderPropObj.pageType === 1) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                if (this.saleDelarSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleDelarList.length; i++) {
                                        let item = this.saleDelarList[i];
                                        for (let j = 0; j < this.saleDelarSelectedRowKeys.length; j++) {
                                            let cItem = this.saleDelarSelectedRowKeys[j];
                                            if (flag && parseFloat(item.key) === parseFloat(cItem)) {
                                                if (!(item.status === 2)) {
                                                    flag = false;
                                                    this.$warning({
                                                        title: '操作提示',
                                                        content: contentSubmit,
                                                        okText: '知道了',
                                                        centered: true,
                                                        maskClosable: false
                                                    });
                                                    break;
                                                }
                                                if (flag && item.create_organization_type === isOrderPage) {
                                                    flag = false;
                                                    this.$warning({
                                                        title: '操作提示',
                                                        content: contentMySelf,
                                                        okText: '知道了',
                                                        centered: true,
                                                        maskClosable: false
                                                    });
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            case this.systemTypes[1]:
                                if (this.saleVendorSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleVendorList.length; i++) {
                                        let item = this.saleVendorList[i];
                                        for (let j = 0; j < this.saleVendorSelectedRowKeys.length; j++) {
                                            let cItem = this.saleVendorSelectedRowKeys[j];
                                            if (flag && parseFloat(item.key) === parseFloat(cItem)) {
                                                if (!(item.status === 2)) {
                                                    flag = false;
                                                    this.$warning({
                                                        title: '操作提示',
                                                        content: contentSubmit,
                                                        okText: '知道了',
                                                        centered: true,
                                                        maskClosable: false
                                                    });
                                                    break;
                                                }
                                                if (flag && item.create_organization_type === isOrderPage) {
                                                    flag = false;
                                                    this.$warning({
                                                        title: '操作提示',
                                                        content: contentMySelf,
                                                        okText: '知道了',
                                                        centered: true,
                                                        maskClosable: false
                                                    });
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }
                    break;
                }
                case 'build': {
                    let contentBuild = '只能生成【已审核/已确认】的销售状态。';
                    if (this.myOrderPropObj.pageType === 1) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                if (this.saleDelarSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleDelarList.length; i++) {
                                        let item = this.saleDelarList[i];
                                        for (let j = 0; j < this.saleDelarSelectedRowKeys.length; j++) {
                                            let cItem = this.saleDelarSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem) &&
                                                !(item.status === 2 || item.status === 3)
                                            ) {
                                                flag = false;
                                                this.$warning({
                                                    title: '操作提示',
                                                    content: contentBuild,
                                                    okText: '知道了',
                                                    centered: true,
                                                    maskClosable: false
                                                });
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            case this.systemTypes[1]:
                                if (this.saleVendorSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleVendorList.length; i++) {
                                        let item = this.saleVendorList[i];
                                        for (let j = 0; j < this.saleVendorSelectedRowKeys.length; j++) {
                                            let cItem = this.saleVendorSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem) &&
                                                !(item.status === 2 || item.status === 3)
                                            ) {
                                                flag = false;
                                                this.$warning({
                                                    title: '操作提示',
                                                    content: contentBuild,
                                                    okText: '知道了',
                                                    centered: true,
                                                    maskClosable: false
                                                });
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }
                    break;
                }
                case 'stop': {
                    let content = '只能终止【已审核和已确认】状态的订单。';
                    if (this.myOrderPropObj.pageType === 0) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                if (this.purchaseDelarSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                    break;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.purchaseDelarList.length; i++) {
                                        let item = this.purchaseDelarList[i];
                                        for (let j = 0; j < this.purchaseDelarSelectedRowKeys.length; j++) {
                                            let cItem = this.purchaseDelarSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem.key) &&
                                                !(item.status === 1 || item.status === 2 || item.status === 3)
                                            ) {
                                                flag = false;
                                                this.$warning({
                                                    title: '操作提示',
                                                    content: content,
                                                    okText: '知道了',
                                                    centered: true,
                                                    maskClosable: false
                                                });
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            case this.systemTypes[1]:
                                if (this.purchaseVendorSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                    break;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.purchaseVendorList.length; i++) {
                                        let item = this.purchaseVendorList[i];
                                        for (let j = 0; j < this.purchaseVendorSelectedRowKeys.length; j++) {
                                            let cItem = this.purchaseVendorSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem.key) &&
                                                !(item.status === 1 || item.status === 2 || item.status === 3)
                                            ) {
                                                flag = false;
                                                this.$warning({
                                                    title: '操作提示',
                                                    content: content,
                                                    okText: '知道了',
                                                    centered: true,
                                                    maskClosable: false
                                                });
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    } else if (this.myOrderPropObj.pageType === 1) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                if (this.saleDelarSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                    break;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleDelarList.length; i++) {
                                        let item = this.saleDelarList[i];
                                        for (let j = 0; j < this.saleDelarSelectedRowKeys.length; j++) {
                                            let cItem = this.saleDelarSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem) &&
                                                !(item.status === 1 || item.status === 2 || item.status === 3)
                                            ) {
                                                flag = false;
                                                this.$warning({
                                                    title: '操作提示',
                                                    content: content,
                                                    okText: '知道了',
                                                    centered: true,
                                                    maskClosable: false
                                                });
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            case this.systemTypes[1]:
                                if (this.saleVendorSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                    break;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleVendorList.length; i++) {
                                        let item = this.saleVendorList[i];
                                        for (let j = 0; j < this.saleVendorSelectedRowKeys.length; j++) {
                                            let cItem = this.saleVendorSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem) &&
                                                !(item.status === 1 || item.status === 2 || item.status === 3)
                                            ) {
                                                flag = false;
                                                this.$warning({
                                                    title: '操作提示',
                                                    content: content,
                                                    okText: '知道了',
                                                    centered: true,
                                                    maskClosable: false
                                                });
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }
                    break;
                }
                case 'remove':
                    break;
                case 'edit':
                    if (this.myOrderPropObj.pageType === 0) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                this.orderPurchaseInfoAPIFn({
                                    partA_id: this.currentPurchaseDelarTrObj.partA,
                                    partB_id: this.currentPurchaseDelarTrObj.partB,
                                    po_so_inner_sn: this.currentPurchaseDelarTrObj.po_so_inner_sn,
                                    billsType: this.currentPurchaseDelarTrObj.billsType
                                });
                                break;
                            case this.systemTypes[1]:
                                this.orderPurchaseInfoAPIFn({
                                    partA_id: this.currentPurchaseVendorTrObj.partA,
                                    partB_id: this.currentPurchaseVendorTrObj.partB,
                                    po_so_inner_sn: this.currentPurchaseVendorTrObj.po_so_inner_sn,
                                    billsType: this.currentPurchaseVendorTrObj.billsType
                                });
                                break;
                            case this.systemTypes[2]:
                                this.orderPurchaseInfoAPIFn({
                                    partA_id: this.currentPurchaseHistoryTrObj.partA,
                                    partB_id: this.currentPurchaseHistoryTrObj.partB,
                                    po_so_inner_sn: this.currentPurchaseHistoryTrObj.po_so_inner_sn,
                                    billsType: this.currentPurchaseHistoryTrObj.billsType
                                });
                                break;
                        }
                    } else if (this.myOrderPropObj.pageType === 1) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                this.orderSaleInfoAPIFn({
                                    partA_id: this.currentSaleDelarTrObj.partA,
                                    partB_id: this.currentSaleDelarTrObj.partB,
                                    po_so_inner_sn: this.currentSaleDelarTrObj.po_so_inner_sn,
                                    billsType: this.currentSaleDelarTrObj.billsType
                                });
                                break;
                            case this.systemTypes[1]:
                                this.orderSaleInfoAPIFn({
                                    partA_id: this.currentSaleVendorTrObj.partA,
                                    partB_id: this.currentSaleVendorTrObj.partB,
                                    po_so_inner_sn: this.currentSaleVendorTrObj.po_so_inner_sn,
                                    billsType: this.currentSaleVendorTrObj.billsType
                                });
                                break;
                            case this.systemTypes[2]:
                                this.orderSaleInfoAPIFn({
                                    partA_id: this.currentSaleHistoryTrObj.partA,
                                    partB_id: this.currentSaleHistoryTrObj.partB,
                                    po_so_inner_sn: this.currentSaleHistoryTrObj.po_so_inner_sn,
                                    billsType: this.currentSaleHistoryTrObj.billsType
                                });
                                break;
                        }
                    }
                    break;
            }
            return flag;
        },
        // tabs切换选项卡
        changeActiveFn(activeKey) {
            if (!this.checkSearchInfoFormMsgFn()) return;
            this.tabsActiveKey = activeKey;
            if (this.tabsActiveKey === this.systemTypes[1] && !this.isTabsActiveVendorKey) {
                this.isTabsActiveVendorKey = true;
                this.orderPurchaseListAPIFn('past');
            }
            if (this.tabsActiveKey === this.systemTypes[2] && !this.isTabsActiveHistoryKey) {
                this.isTabsActiveHistoryKey = true;
                this.orderHistoryPurchaseListAPIFn();
            }
        },
        // 选中项发生变化时的回调
        purchaseDelarSelectChangeFn(selectedRowKeys) {
            this.purchaseDelarSelectedRowKeys = selectedRowKeys;
        },
        purchaseVendorSelectChangeFn(selectedRowKeys) {
            this.purchaseVendorSelectedRowKeys = selectedRowKeys;
        },
        saleDelarSelectChangeFn(selectedRowKeys) {
            this.saleDelarSelectedRowKeys = selectedRowKeys;
        },
        saleVendorSelectChangeFn(selectedRowKeys) {
            this.saleVendorSelectedRowKeys = selectedRowKeys;
        },
        // 表格的高亮样式显示
        rowPurchaseDelarClassFn(record) {
            if (record.index === this.currentPurchaseDelarTrObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowPurchaseVendorClassFn(record) {
            if (record.index === this.currentPurchaseVendorTrObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowPurchaseHistoryClassFn(record) {
            if (record.index === this.currentPurchaseHistoryTrObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowSaleDelarClassFn(record) {
            if (record.index === this.currentSaleDelarTrObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowSaleVendorClassFn(record) {
            if (record.index === this.currentSaleVendorTrObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowSaleHistoryClassFn(record) {
            if (record.index === this.currentSaleHistoryTrObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowClassPlanModalFn(record) {
            if (record.index === this.currentTrPlanModalObj.index) {
                return 'active-tr';
            }
            return '';
        },
        cellClickPurchaseDelarFn({ row }) {
            this.currentPurchaseDelarTrObj = row;
        },
        cellDblclickPurchaseDelarFn({ row }) {
            this.currentPurchaseDelarTrObj = row;
            this.groupBtnFn('edit');
        },
        selectChangePurchaseDelarFn({ row }) {
            this.$refs.xPurchaseDelarTable.setCurrentRow(row);
        },
        cellClickPurchaseVendorFn({ row }) {
            this.currentPurchaseVendorTrObj = row;
        },
        cellDblclickPurchaseVendorFn({ row }) {
            this.currentPurchaseVendorTrObj = row;
            this.groupBtnFn('edit');
        },
        selectChangePurchaseVendorFn({ row }) {
            this.$refs.xPurchaseVendorTable.setCurrentRow(row);
        },
        cellClickPurchaseHistoryFn({ row }) {
            this.currentPurchaseHistoryTrObj = row;
        },
        cellDblclickPurchaseHistoryFn({ row }) {
            this.currentPurchaseHistoryTrObj = row;
            this.groupBtnFn('edit');
        },
        customSaleDelarRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentSaleDelarTrObj = record;
                    },
                    dblclick: () => {
                        this.currentSaleDelarTrObj = record;
                        this.groupBtnFn('edit');
                    }
                }
            };
        },
        customSaleVendorRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentSaleVendorTrObj = record;
                    },
                    dblclick: () => {
                        this.currentSaleVendorTrObj = record;
                        this.groupBtnFn('edit');
                    }
                }
            };
        },
        customSaleHistoryRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentSaleHistoryTrObj = record;
                    },
                    dblclick: () => {
                        this.currentSaleHistoryTrObj = record;
                        this.groupBtnFn('edit');
                    }
                }
            };
        },
        cellAddInfoClickFn({ row }) {
            this.currentTrPlanModalObj = row;
            this.resetFocusFn();
        },
        cellAddInfoDblclickFn({ row }, e) {
            this.currentTrPlanModalObj = row;
            this.resetFocusFn();
            if (this.isEditDisabled) return;
            this.dblclickTableFn(e);
        },
        // 右上角刷新按钮
        refreshPageFn() {
            switch (this.myOrderPropObj.pageType) {
                case 0:
                    if (this.tabsActiveKey === this.systemTypes[0]) {
                        this.purchaseDelarSelectedRowKeys = [];
                        this.saleVendorSelectedRowKeys = [];
                        this.purchaseDelarPagination = {
                            ...this.purchaseDelarPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1,
                            pageSizes: [10, 20, 30, 40]
                        };
                        this.orderPurchaseListAPIFn('perform');
                    } else if (this.tabsActiveKey === this.systemTypes[1]) {
                        this.saleDelarSelectedRowKeys = [];
                        this.purchaseVendorSelectedRowKeys = [];
                        this.purchaseVendorPagination = {
                            ...this.purchaseVendorPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1,
                            pageSizes: [10, 20, 30, 40]
                        };
                        this.orderPurchaseListAPIFn('past');
                    } else if (this.tabsActiveKey === this.systemTypes[2]) {
                        this.purchaseHistoryPagination = {
                            ...this.purchaseHistoryPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1
                        };
                        this.orderHistoryPurchaseListAPIFn();
                    }
                    break;
                case 1:
                    if (this.tabsActiveKey === this.systemTypes[0]) {
                        this.saleDelarPagination = {
                            ...this.saleDelarPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1
                        };
                        this.orderSaleListAPIFn('perform');
                    } else if (this.tabsActiveKey === this.systemTypes[1]) {
                        this.saleVendorPagination = {
                            ...this.saleVendorPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1
                        };
                        this.orderSaleListAPIFn('past');
                    } else if (this.tabsActiveKey === this.systemTypes[2]) {
                        this.saleHistoryPagination = {
                            ...this.saleHistoryPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1
                        };
                        this.orderHistorySaleListAPIFn();
                    }
                    break;
            }
        },
        // 页码改变的回调函数
        changePurchaseDelarPaginationFn({ currentPage, pageSize }) {
            this.purchaseDelarPagination.current = currentPage;
            this.purchaseDelarPagination.pageSize = pageSize;
            this.purchaseDelarPagination.loading = true;
            this.orderPurchaseListAPIFn('perform');
        },
        changePurchaseVendorPaginationFn({ currentPage, pageSize }) {
            this.purchaseVendorPagination.current = currentPage;
            this.purchaseVendorPagination.pageSize = pageSize;
            this.purchaseVendorPagination.loading = true;
            this.orderPurchaseListAPIFn('past');
        },
        changePurchaseHistoryPaginationFn({ currentPage, pageSize }) {
            this.purchaseHistoryPagination.current = currentPage;
            this.purchaseHistoryPagination.pageSize = pageSize;
            this.purchaseHistoryPagination.loading = true;
            this.orderHistoryPurchaseListAPIFn();
        },
        changeSaleDelarPaginationFn({ currentPage, pageSize }) {
            this.saleDelarPagination.current = currentPage;
            this.saleDelarPagination.pageSize = pageSize;
            this.saleDelarPagination.loading = true;
            this.orderSaleListAPIFn('perform');
        },
        changeSaleVendorPaginationFn({ currentPage, pageSize }) {
            this.saleVendorPagination.current = currentPage;
            this.saleVendorPagination.pageSize = pageSize;
            this.saleVendorPagination.loading = true;
            this.orderSaleListAPIFn('past');
        },
        changeSaleHistoryPaginationFn({ currentPage, pageSize }) {
            this.saleHistoryPagination.current = currentPage;
            this.saleHistoryPagination.pageSize = pageSize;
            this.saleHistoryPagination.loading = true;
            this.orderHistorySaleListAPIFn();
        },
        // 双击此处添加产品的方法
        // 检测采购单位填值了没有
        checkPlanModalOrgSnFn() {
            let flag = false;
            if (this.planModalOrgSn.length <= 0) {
                flag = true;
                this.$warning({
                    title: '操作提示',
                    content: '请选择左上方的【供货单位】，再添加商品。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
            } else {
                if (this.myOrderPropObj.pageType === 0) {
                    this.partB.partA_id = this.cookieInfo.get('userbelong');
                    // this.partB.partB_id = this.planModalForm.partB
                    // this.partB.billsType = this.billsType
                    this.partB.modalType = 4;
                    // this.partB.oname = this.planModalForm.partB_name
                } else if (this.myOrderPropObj.pageType === 1) {
                    // this.partB.partA_id = this.planModalForm.partA
                    this.partB.partB_id = this.cookieInfo.get('userbelong');
                    // this.partB.billsType = this.billsType
                    this.partB.modalType = 4;
                    // this.partB.oname = this.planModalForm.partA_name
                }
                flag = false;
            }
            return flag;
        },
        dblclickAddProductFn() {
            this.$nextTick(() => {
                // 双击
                if (this.$refs.applyTable.getElementsByClassName('ant-table-placeholder').length) {
                    this.$refs.applyTable
                        .getElementsByClassName('ant-table-placeholder')[0]
                        .addEventListener('dblclick', this.dblclickTableFn, false);
                }
            });
        },
        dblclickTableFn(e) {
            if (e) {
                const filterDom = ['span', 'svg', 'path', 'input', 'i', 'li', 'ul'],
                    flag = filterDom.some((item) => item === e.target.tagName.toLowerCase()),
                    childrenFlag = filterDom.some(
                        (item) => item === e.target.children && e.target.children.tagName.toLowerCase()
                    ),
                    childrenFlagRemove = e.target.getElementsByClassName('action-btn-list-wrap').length;
                if (flag || childrenFlag || childrenFlagRemove) return;
            }
            if (this.checkPlanModalOrgSnFn()) return;
            this.popupAddProduct();
        },
        addProductFn() {
            this.$refs.chooseProMd.returnProList();
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
        async getProReturn(dea, ven) {
            // 获取回调商品
            this.addProductEnter = false;
            this.addProductMd.alert = false;
            this.planModalPagination.loading = true;
            let records = [...ven, ...dea];
            this.addData.splice(this.editCellRow, 1);
            records.forEach((item, idx) => {
                item.unit_price = item.unit_price ? item.unit_price : 1;
                item.status_string = this.earnStatus(item.status, 'planOrder');
                item.rate_string = this.reNumber(item.rate * 100),
                this.addData.splice(this.editCellRow + idx, 0, item);
                this.venUnitTreeFn(item);
            });
            this.addData = this.addData.map((item, idx) => {
                return {
                    ...item,
                    idx: idx + 1
                };
            });
        },
        rowClassNameFn({ row }) {
            if (row.row_add_flag) {
                return 'row--current';
            }
        },
        clearRowClassNameFn() {
            // 去除新增的临时背景颜色
            this.planModalDatas.forEach((item) => {
                if (item.row_add_flag) {
                    item.row_add_flag = false;
                }
            });
        },
        updateFooterPlanModalFnBN() {
            this.$refs.xTablePlanModalInfo.updateData();
        },
        updateFooterPlanModalFn() {
            const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
            xTablePlanModalInfo.updateFooter();
            xTablePlanModalInfo.updateData();
            this.planModalDatas = xTablePlanModalInfo.getTableData().tableData;
        },
        // 订单表格的操作
        planModalActionBtnFn(e, btnName, record) {
            switch (btnName) {
                case 'remove': {
                    // 删除
                    if (this.isEditDisabled) return;
                    if (
                        typeof record.detail_inner_sn !== 'undefined' &&
                        !this.removeSelfCreateOrganizationTypeFn([record])
                    ) {
                        this.$warning({
                            title: '操作提示',
                            content: '只能删除自建的产品',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        return;
                    }
                    const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
                    xTablePlanModalInfo.remove(record);
                    this.planModalDatas = xTablePlanModalInfo.getTableData().tableData;
                    xTablePlanModalInfo.setCurrentRow(this.planModalDatas[0]);
                    this.dblclickAddProductFn();
                    this.$refs.xTablePlanModalInfo.updateData();
                    break;
                }
                case 'stop':
                    record.loading = true;
                    record.switch ? this.orderDetailStopAPIFn(record) : this.orderDetailUnstopAPIFn(record);
                    break;
            }
        },
        // 只能删除自建的产品
        removeSelfCreateOrganizationTypeFn(checkRecordRows) {
            const isOrderPageType = this.myOrderPropObj.pageType; // 0 - 采购单  1 - 销售单
            let flag = true,
                createOrganizationType = isOrderPageType === 0 ? 1 : 0; // 采购单： 0 - 自建   1 - 买家    销售单： 0 - 买家   1 - 自建
            for (let i = 0; i < checkRecordRows.length; i++) {
                let item = checkRecordRows[i];
                if (parseInt(item.create_organization_type) === createOrganizationType) {
                    flag = false;
                    break;
                }
            }
            return flag;
        },
        // 删除订单的合计
        removeOrderLastFn() {
            if (this.planModalDatas.length <= 1) {
                this.planModalDatas = [];
            } else {
                this.currentTrPlanModalObj = this.planModalDatas.length ? this.planModalDatas[0] : {};
            }
        },
        // 导出excel
        exportExcel() {
            const form = this.planModalForm;
            const formData = {
                title: '采购订单',
                desc: {
                    label: '摘要',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '单据编号',
                            value: form.po_sn
                        },
                        {
                            label: '采购单位',
                            value: form.partB_name
                        },
                        {
                            label: '采购员',
                            value: form.buyer_name
                        },
                        {
                            label: '采购部门',
                            value: form.department_name
                        },
                        {
                            label: '期望到货日期',
                            value: form.expect_aog_date
                        },
                        {
                            label: '仓库',
                            value: form.warehouse_name
                        },
                        {
                            label: '送货地址',
                            value: form.delivery_address
                        }
                    ],
                    [
                        {
                            label: '制单人',
                            value: form.creator_name
                        },
                        {
                            label: '生成时间',
                            value: form.create_time
                        },
                        {
                            label: '审核人',
                            value: form.auditor_name
                        },
                        {
                            label: '审核时间',
                            value: form.audit_time
                        }
                    ],
                    [
                        {
                            label: '送货地址',
                            value: form.delivery_address
                        }
                    ]
                ]
            };
            let tableData = this.getStableData();
            this.$Utils.exportExcel(
                this.planModalColumns,
                tableData.map((i) => ({
                    ...i,
                    unit_price_text: this.unit_price_record(i),
                    rate: `${i.rate}%`
                })),
                [],
                formData
            );
        },
        handlePrint() {
            let tableData = this.getStableData();
            let routeUrl = this.$router.resolve({
                // pageType为0时，打印采购订单，为1时，打印销售订单
                // 销售订单打印还未迁移至新的打印模块
                path: this.myOrderPropObj.pageType === 0 ? `/newprint/purchaseOrder` : 'print'
            });
            window.localStorage.setItem(
                'printContent',
                JSON.stringify({
                    // 状态为终止的产品，不进入打印单
                    printData: tableData.filter(
                        (item) => item.product_inner_sn && item.status !== productStatusTextMap[3]
                    ),
                    printForm: this.planModalForm,
                    printTable: this.planModalColumns,
                    myOrderPropObj: this.myOrderPropObj,
                    printType: 6, // 1 图片；2、耗材申领；3、出入库单；4、采购询价单；5、采购计划单; 6、采购-订单/销售-订单
                    planOrderStatus: ['待处理', '已终止', '已生成采购订单'],
                    printPageType: this.myOrderPropObj
                })
            );
            window.open(routeUrl.href, '_blank');
            this.clickDescriptionFn();
        },
        // 订单表格底部的按钮操作
        planModalGroupBtnFn(btnName) {
            if (
                !(btnName === 'print' || btnName === 'exportExcel' || btnName === 'cancel' || btnName === 'savePo') &&
                !this.checkPlanModalFormFn(btnName)
            )
                return;
            let title = '';
            switch (btnName) {
                case 'print':
                    title = '您确定要打印吗?';
                    break;
                case 'exportExcel':
                    title = '您确定要导出Excel吗?';
                    break;
                case 'cancel':
                    title = '您确定要取消吗?';
                    break;
                case 'saveDraft':
                    title = '您确定要存为草稿吗?';
                    break;
                case 'save':
                    title = '您确定要保存吗?';
                    break;
                case 'audit':
                    title = '您确定要保存吗?';
                    break;
            }
            const _this = this;
            this.$confirm({
                title: title,
                onOk() {
                    switch (btnName) {
                        case 'print': // 打印
                            _this.handlePrint();
                            break;
                        case 'exportExcel':
                            _this.exportExcel();
                            break;
                        case 'add': // 生成采购订单
                            _this.planModal.loadingAdd = true;
                            _this.orderSaleDetailBuildAPIFn();
                            break;
                        case 'cancel': // 取消
                            _this.changeTabFn();
                            break;
                        case 'saveDraft': // 存草稿
                            _this.planModal.loadingSaveDraft = true;
                            if (_this.myOrderPropObj.pageType === 0) {
                                if (_this.btnName === 'add') {
                                    _this.orderPurchaseAddAPIFn(0);
                                } else {
                                    _this.orderPurchaseEditAPIFn(0);
                                }
                            } else if (this.myOrderPropObj.pageType === 1) {
                                if (_this.btnName === 'add') {
                                    _this.orderSaleAddAPIFn(0);
                                } else {
                                    _this.orderSaleEditAPIFn(0);
                                }
                            }
                            break;
                        case 'save': // 保存
                            _this.planModal.loadingSave = true;
                            if (_this.myOrderPropObj.pageType === 0) {
                                if (_this.btnName === 'add') {
                                    _this.orderPurchaseAddAPIFn(1); // 保存正式
                                } else {
                                    // 单据来源为销售方方且状态为已审核的销售订单可以编辑 状态保存为3(确认)
                                    const status = _this.isSourceBuyerAudited ? 3 : 1;
                                    _this.orderPurchaseEditAPIFn(status);
                                }
                            } else if (this.myOrderPropObj.pageType === 1) {
                                if (_this.btnName === 'add') {
                                    _this.orderSaleAddAPIFn(1); // 保存正式
                                } else {
                                    _this.orderSaleEditAPIFn(1);
                                }
                            }
                            break;
                        case 'savePo': {
                            let flag = _this.checkCreateOrganizationTypeFn();
                            if (!flag) return;
                            _this.planModal.loadingSave = true;
                            if (_this.myOrderPropObj.pageType === 0) {
                                _this.orderPurchaseConfirmAPIFn();
                            } else if (_this.myOrderPropObj.pageType === 1) {
                                _this.orderSaleConfirmAPIFn();
                            }
                            break;
                        }
                        case 'audit': // 审核
                            _this.planModal.loadingAudit = true;
                            if (_this.myOrderPropObj.pageType === 0) {
                                _this.orderPurchaseEditAPIFn(2);
                            } else if (_this.myOrderPropObj.pageType === 1) {
                                _this.orderSaleEditAPIFn(2);
                            }
                            break;
                    }
                },
                onCancel() {
                    console.log('Cancel');
                }
            });
        },
        // 检测是否是自己创建的订单 【details】
        checkCreateOrganizationTypeFn() {
            let contentMySelf = '不能确认自己创建的订单。',
                flag = true;
            const isOrderPage = 0,
                createOrganizationType = parseInt(this.planModalForm.create_organization_type); // 判断是0-采购订单  1-销售订单
            if (this.tabsActiveKey === this.systemTypes[0]) {
                if (flag && createOrganizationType === isOrderPage) {
                    flag = false;
                    this.$warning({
                        title: '操作提示',
                        content: contentMySelf,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                }
            } else if (this.tabsActiveKey === this.systemTypes[1]) {
                if (flag && createOrganizationType === isOrderPage) {
                    flag = false;
                    this.$warning({
                        title: '操作提示',
                        content: contentMySelf,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                }
            }
            return flag;
        },
        // 检测是否是自己创建的订单 【list】
        checkCreateOrganizationTypeListFn(checkboxRecords) {
            const createOrganizationType = this.myOrderPropObj.pageType === 0 ? 0 : 1, // 判断是0-采购订单  1-销售订单\
                isStatus = checkboxRecords.every(
                    (item) => parseFloat(item.status) !== 2
                ),
                isConfirm = checkboxRecords.every(
                    (item) => parseFloat(item.create_organization_type) === createOrganizationType
                );
            if (isStatus) {
                this.$warning({
                    title: '操作提示',
                    content: '只能确认【已审核】的状态。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (isConfirm) {
                this.$warning({
                    title: '操作提示',
                    content: '不能确认自己创建的订单。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 设置修改过的修改信息内容字段
        checkEditInfoFormObjFn() {
            let editInfoFlag = true;
            this.editInfoFormNewObj = {};
            for (let server in this.planModalForm) {
                if (
                    typeof [server + '_old'] !== 'undefined' &&
                    this.planModalForm[server] !== this.planModalForm[server + '_old']
                ) {
                    this.editInfoFormNewObj = {
                        ...this.editInfoFormNewObj,
                        [server]: this.planModalForm[server], // 新值
                        [server + '_old']: this.planModalForm[server + '_old'] // 旧值
                    };
                    editInfoFlag = false;
                }
            }
            return editInfoFlag;
        },
        // 校验采购计划单
        checkPlanModalFormFn() {
            let flag = true;
            let datas = this.getStableData();
            if (datas.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请双击表格内容部分添加产品',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            if (
                (this.myOrderPropObj.pageType === 0 && !this.planModalForm.partB) ||
                (this.myOrderPropObj.pageType === 1 && !this.planModalForm.partA)
            ) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择供货单位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            if (!flag) return;
            if (this.myOrderPropObj.pageType === 0 && !this.planModalForm.buyer_id && !this.isSourceBuyerAudited) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择采购员',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            if (
                this.myOrderPropObj.pageType === 0 &&
                !this.planModalForm.department_inner_sn &&
                !this.isSourceBuyerAudited
            ) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择采购部门',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            // 判断表格主体的数量 - 包装单位
            if (datas.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请添加产品',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }

            if (!datas.every((item) => item.quantity && item.unit_inner_sn.length)) {
                this.$warning({
                    title: '操作提示',
                    content: '请填写数量/包装单位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }

            if (flag) {
                let isRepeatObj = {};
                for (let i = 0; i < datas.length; i++) {
                    const item = datas[i],
                        unitPriceNew = (item.tax_unit_price + '').split('.'),
                        len = 2, // 小数的有效长度是2位
                        unitPriceLen = unitPriceNew.length >= 2 ? unitPriceNew[unitPriceNew.length - 1].length : 0,
                        discountNew = (item.discount + '').split('.'),
                        discountLen = discountNew.length >= 2 ? discountNew[discountNew.length - 1].length : 0;
                    // rateNew = (item.rate + '').split('.'),
                    // rateLen = rateNew.length >= 2 ? rateNew[rateNew.length - 1].length : 0
                    if (unitPriceLen > 4) {
                        // 单价是4位
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】含税单价的小数长度只能是四位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (discountLen > len) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】折扣的小数长度只能是两位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (this.$Utils.checkFloatNumFn((item.rate * 10000) / 10000)) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】税率只能是0-100间的整数`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (flag) {
                        // 产品只能出现唯一
                        const key = `${item.product_type}==${item.product_inner_sn}==${item.specification_inner_sn}==${
                            item.product_vendor_inner_sn || item.vendor_inner_sn || item.dealer_inner_sn
                        }`;
                        if (!isRepeatObj[key]) {
                            isRepeatObj[key] = `NO.${i + 1}`;
                        } else {
                            this.$warning({
                                title: '操作提示',
                                content: `${isRepeatObj[key]}和NO.${i + 1}产品相同，不能重复添加。`,
                                okText: '知道了',
                                centered: true,
                                maskClosable: true
                            });
                            flag = false;
                            break;
                        }
                    }
                }
                isRepeatObj = null;
            }

            if (!flag) return;
            if (flag && this.btnName === 'edit' && this.checkEditInfoFormObjFn()) {
                // 校验产品列表有没有改过
                flag = true;
            }
            if (flag) {
                this.detailList = datas.map((item, index) => {
                    return {
                        detail_inner_sn: item.detail_inner_sn || '',
                        product_vendor_inner_sn:
                            item.product_vendor_inner_sn ||
                            item.vendor_inner_sn ||
                            item.dealer_b_inner_sn ||
                            item.dealer_inner_sn, // 之前是 dealer_inner_sn
                        product_vendor_name: item.product_vendor_name || item.vendor_name || item.dealer_name,
                        product_inner_sn: item.product_inner_sn,
                        product_name: item.product_name,
                        specification_inner_sn: item.specification_inner_sn,
                        specification: item.specification,
                        unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                        unit_name: item.unit_name,
                        product_type: item.product_type,
                        quantity: item.quantity,
                        // unit_price: item.unit_price,
                        discount: item.discount,
                        rate: item.rate,
                        sort_number: index,
                        batch_number: item.batch_number,
                        unit_price_text:
                            typeof item.tax_unit_price === 'undefined' ? 0 : parseFloat(item.tax_unit_price).toFixed(4) // 传递给后端的字段取值变为 【单价 = 含税单价   含税单价 = 单价】
                    };
                });
                flag = true;
            }
            return flag;
        },
        // 关闭采购计划单模态框
        closePlanModalFn() {
            this.planModal = {
                title: `${this.$route.meta.title} - 新增`,
                alert: false,
                loadingPrint: false,
                loadingAdd: false,
                loadingSaveDraft: false,
                loadingSave: false,
                loadingAudit: false,
                loadingExportExcel: false
            };
        },
        // 重置采购计划单模态框
        resetPlanModalFn() {
            this.expectAogDate = null;
            this.planModalOrgSn = [];
            this.planModalDatas = [];
            this.planModalEmployeSn = [];
            this.planModalDepartmentSn = [];
            this.planModalEmployes = [];
            this.planModalDepartmentSns = [];
            this.planModalLocation = '';
            this.planModalWarehouse = '';
            this.planModalWarehouseSn = '';
            this.detailList = [];
            this.editInfoFormNewObj = {};
            this.planModalSelectedRowKeys = [];
            this.currentTrPlanModalObj = {};
            this.supplySearch = '';
            this.planModalForm = {
                creator_name: JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
                buyer_id: '',
                buyer_id_old: '',
                buyer_name: '',
                department_inner_sn: '',
                department_inner_sn_old: '',
                department_name: '',
                description: '',
                description_old: '',
                details: [],
                expect_aog_date: '',
                expect_aog_date_old: '',
                billsType: '',
                partA_id: '',
                partB_id: '',
                po_sn: '',
                po_sn_old: '',
                po_so_inner_sn: '',
                status: ''
            };
        },
        // 递归加上索引
        treeDatasIndexAryFn(datasOrigin, isAppendDatas, id) {
            // datasOrigin: 源数据  isAppendDatas: 将数据放在哪里  id: 当前点击的是哪个id标识
            const _this = this;
            // 格式化部门名称
            function formatDepartmentNameFn(strComma) {
                // 研发部门,行政部门,其他
                if (!strComma) return strComma;
                return strComma.replace(/,/g, ' ');
            }
            // 子级递归
            function childrenIndexFn(list, pIndexAry, parentDepartmentNameOld, pItemDisabledFlag) {
                if (!pIndexAry || !parentDepartmentNameOld) return;
                return list.map((cItem, cIndex) => {
                    let cItemDisabledFlag =
                        _this.btnName === 'edit' &&
                        (pItemDisabledFlag || (isAppendDatas === 'parentDirectors' && id === cItem.employee_inner_sn));
                    if (cItem.children && cItem.children.length > 0) {
                        let value = '';
                        if (!isNaN(cItem.employee_inner_sn)) {
                            value += cItem.employee_inner_sn;
                        } else if (!isNaN(cItem.department_inner_sn)) {
                            value += cItem.department_inner_sn;
                        } else if (!isNaN(cItem.org_id)) {
                            value += cItem.org_id + '' + cItem.billsType;
                        }
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn,
                            activeClass: '',
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            department_name_new: formatDepartmentNameFn(cItem.department_name),
                            disabled:
                                cItemDisabledFlag ||
                                (isAppendDatas === 'deptTopTreePlan' && parseInt(cItem.status) <= 0),
                            value,
                            label: cItem.name,
                            isLeaf: false,
                            children: childrenIndexFn(
                                cItem.children,
                                [...pIndexAry, cIndex],
                                [...parentDepartmentNameOld, cItem.name],
                                cItemDisabledFlag
                            )
                        };
                        if (obj.key === _this.infoKey) {
                            // 新增下级同级的时候选中新增的那一条
                            _this.currentTrObj = obj;
                        }
                        return obj;
                    } else {
                        let value = '';
                        if (!isNaN(cItem.employee_inner_sn)) {
                            value += cItem.employee_inner_sn;
                        } else if (!isNaN(cItem.department_inner_sn)) {
                            value += cItem.department_inner_sn;
                        } else if (!isNaN(cItem.org_id)) {
                            value += cItem.org_id + '' + cItem.billsType;
                        }
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn,
                            activeClass: '',
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            department_name_new: formatDepartmentNameFn(cItem.department_name),
                            disabled:
                                cItemDisabledFlag ||
                                (isAppendDatas === 'deptTopTreePlan' && parseInt(cItem.status) <= 0),
                            value,
                            label: cItem.name,
                            children: cItem.children
                        };
                        if (obj.key === _this.infoKey) {
                            // 新增下级同级的时候选中新增的那一条
                            _this.currentTrObj = obj;
                        }
                        return obj;
                    }
                });
            }
            // 父级
            let datasOriginNew = datasOrigin.map((item, index) => {
                let itemDisabledFlag =
                    _this.btnName === 'edit' && isAppendDatas === 'parentDirectors' && id === item.employee_inner_sn;
                if (item.children && item.children.length > 0) {
                    let value = '';
                    if (!isNaN(item.employee_inner_sn)) {
                        value += item.employee_inner_sn;
                    } else if (!isNaN(item.department_inner_sn)) {
                        value += item.department_inner_sn;
                    } else if (!isNaN(item.org_id)) {
                        value += item.org_id + '' + item.billsType;
                    }
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn,
                        activeClass: '',
                        parentDepartmentName: [item.name],
                        department_name_new: formatDepartmentNameFn(item.department_name),
                        disabled:
                            itemDisabledFlag || (isAppendDatas === 'deptTopTreePlan' && parseInt(item.status) <= 0),
                        value,
                        label: item.name,
                        isLeaf: false,
                        children: childrenIndexFn(item.children, [index], [item.name], itemDisabledFlag)
                    };
                    if (obj.key === _this.infoKey) {
                        // 新增下级同级的时候选中新增的那一条
                        _this.currentTrObj = obj;
                    }
                    return obj;
                } else {
                    let value = '';
                    if (!isNaN(item.employee_inner_sn)) {
                        value += item.employee_inner_sn;
                    } else if (!isNaN(item.department_inner_sn)) {
                        value += item.department_inner_sn;
                    } else if (!isNaN(item.org_id)) {
                        value += item.org_id + '' + item.billsType;
                    }
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn,
                        activeClass: '',
                        parentDepartmentName: [item.name],
                        department_name_new: formatDepartmentNameFn(item.department_name),
                        disabled:
                            itemDisabledFlag || (isAppendDatas === 'deptTopTreePlan' && parseInt(item.status) <= 0),
                        value,
                        label: item.name,
                        children: item.children
                    };
                    if (obj.key === _this.infoKey) {
                        // 新增下级同级的时候选中新增的那一条
                        _this.currentTrObj = obj;
                    }
                    return obj;
                }
            });

            switch (isAppendDatas) {
                case 'deptTopTree': // 部门
                    this.topSearchAllTreeList = datasOriginNew;
                    break;
                case 'deptTopTreePlan': // 采购部门
                    this.planModalDepartmentSns = datasOriginNew;
                    // 设置默认部门值为当前登陆人的第一个部门
                    if (this.btnName === 'add') {
                        let intersection = [];
                        let userBelong = JSON.parse(localStorage.getItem('userInfo')).Departments;
                        let departmentData = this.$Utils.flatArrayWithChildren(datasOriginNew);
                        userBelong.filter((a) => {
                            departmentData.filter((b) => {
                                if (a.Department_inner_sn === b.department_inner_sn) {
                                    intersection.push(b);
                                }
                            });
                        });
                        this.planModalDepartmentSn = intersection[0].department_inner_sn;
                        this.changePlanModalDepartmentSnFn(
                            intersection[0].department_inner_sn,
                            intersection[0].department_name
                        );
                    }
                    break;
                case 'allEmpDropdownList': // 采购员
                    this.topSearchEmpDropdownList = datasOriginNew;
                    this.planModalEmployes = datasOriginNew;
                    if (this.btnName === 'add') {
                        this.planModalEmployeSn = JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn;
                        this.changePlanModalEmployeSnFn(
                            JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn,
                            JSON.parse(this.$cookie.get('EmployeeInfo')).Name
                        );
                    }
                    break;
            }
        },
        // 包装单位区域
        clickUnitListFn(record, seq) {
            if (!record.isLoadingUnit) {
                if (!record.detail_inner_sn || this.btnName === 'add') {
                    parseFloat(record.product_type) === 1
                        ? this.platformUnitTreeFn(record, 'add')
                        : this.dealerUnitTreeAPIFn(record, 'add');
                } else if (this.btnName === 'edit') {
                    parseFloat(record.product_type) === 1
                        ? this.platformUnitTreeFn(record)
                        : this.dealerUnitTreeAPIFn(record);
                }
            }
            if (typeof seq !== 'undefined' && seq - 1 > -1) {
                this.resetFocusFn('unit_inner_sn', seq - 1);
            }
            this.record = record;
        },
        changeUnitListFn(value, selectedOptions) {
            this.record.unit_name = selectedOptions.length ? selectedOptions[selectedOptions.length - 1].name : '';
            this.$refs.xTablePlanModalInfo.focus();
            this.contractGetOrderPriceAPIFn(this.record);
        },
        displayUnitRender({ labels }) {
            let label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
        },
        changeExpectAogDateFn(date, dateString) {
            console.log(date);
            this.planModalForm.expect_aog_date = dateString;
        },
        footerCellClassNameFn({ column }) {
            const redAry = ['money', 'discount_money', 'tax_amount', 'tax'];
            if (redAry.some((item) => item === column.property)) {
                return 'red bold';
            }
            if (column.property === 'index') {
                return 'total-bg bold';
            }
        },
        footerMethodFn({ columns }) {
            const { toFixed } = this.$XEUtils;
            return [
                columns.map((column, columnIndex) => {
                    if (columnIndex === 0) {
                        return '合计';
                    }
                    // 金额
                    if (column.property === 'money') {
                        return toFixed(this.money_total, 4);
                    }
                    if (column.property === 'discount_money') {
                        return toFixed(this.discount_money_total, 4);
                    }
                    // 含税金额
                    if (column.property === 'tax_amount') {
                        return toFixed(this.tax_amount_total, 4);
                    }
                    // 税额
                    if (column.property === 'tax') {
                        return toFixed(this.tax_total, 4);
                    }
                })
            ];
        },
        afterClosePlanmodalFn() {
            this.partB.oname = '';
            this.planModalOrgs = '';
            this.isAddProductModalTableMax = false;
            const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
            if (xTablePlanModalInfo) {
                xTablePlanModalInfo.revert();
                xTablePlanModalInfo.refreshScroll();
                this.planModalDatas = [];
                xTablePlanModalInfo.clearData();
            }
        },
        _isStop(status) {
            // 获取订单明细列表 item是否为终止
            const ITEM_STOP_STATUS = 3; // 已终止处理
            return status === ITEM_STOP_STATUS;
        },
        // 生成打印和导出 转换处理过的数据（状态文本名称，包装名称文本，金额计算等）
        _genVisibleTableData() {
            const { footerData } = this.$refs['xTablePlanModalInfo'].getTableData();
            const columns = this.$refs['xTablePlanModalInfo'].getColumns();
            const tempDatas = this.planModalDatas.map((item, index) => {
                // 表体数据
                return {
                    ...item,
                    index: index + 1,
                    status: this.$options.filters['formatStatus'](item.status, 'planOrder'), // 状态名称
                    unit_inner_sn: item.unit_name, // 包装单位名称
                    money: this.money_record(item), // 金额
                    discount_unit_price: this.discount_unit_price_record(item), // 折后单价
                    discount_money: this.discount_money_record(item), // 折后金额
                    // tax_unit_price: this.tax_unit_price_record(item), // 含税单价
                    unit_price_text: this.$XEUtils.commafy(item.unit_price, { fixed: 4 }),
                    tax_amount: this.tax_amount_record(item), // 含税金额
                    tax: this.tax_record(item), // 税额
                    newest_register_sn: item.newest_register_sn || item.mdrf_sn,
                    product_vendor_name: item.product_vendor_name || item.vendor_name || item.dealer_name
                };
            });
            return {
                columns, // 表头
                footerData, // 表尾数据
                tableData: tempDatas
            };
        },
        openChooseTb(value) {
            // 打开采购单位
            if (this.$refs.xTablePlanModalInfo) {
                this.$refs.xTablePlanModalInfo.blur();
            }
            this.pageTypeName = 7;
            this.relevantInstitutionsFocus = true;
            this.searchOrgName = typeof value === 'string' ? value : this.partB.oname || '';
            this.resetFocusFn();
            this.isParentKeyDownEnter = true;
            this.chooseOrgMd.alert = true;
            this.$Utils.globalDragCenterFn('choose_org_md');
            this.$nextTick(() => {
                if (this.$refs.chooseOrgMd) {
                    this.$refs.chooseOrgMd.setContractOrgCheckedFn();
                    this.$refs.chooseOrgMd.pageDataInit();
                }
            });
        },
        chooseOrgFn(resObj) {
            // 获取选中采购单位信息
            const objValue = resObj.org_id + '-' + resObj.billsType,
                objLabel = resObj.name;
            this.changePlanModalOrgSnFn(objValue, objLabel);
            this.planModalOrgs = objValue;
            this.chooseOrgMd.alert = false;
            this.isParentKeyDownEnter = false;
            this.selectedSupply = resObj;
            this.supplySearch = resObj.name;
            this.orderVendorListAPIFn();
            // if (this.relevantInstitutionsFocus) {
            //     // this.addProductZoomFn()
            //     this.insertTempProductDetailFn();
            // }
        },
        // 创建临时的产品明细
        insertTempProductDetailFn() {
            const xTableAddInfo = this.$refs.xTablePlanModalInfo;

            xTableAddInfo.focus();
            let tempProduct = {
                // 前面的数字key依次对应着要聚焦的输入框，后面的value对应着当前要聚焦的那个输入框
                'focusDatas': [true, false, false, false, false, false],
                'common_use_unit': '',
                'vendor_inner_sn': '',
                'specification_inner_sn': '',
                'product_type': '',
                'specification_vendor_sn': '',
                'vendor_name': '',
                'specification': '',
                'id': '',
                'product_inner_sn': '',
                'product_name': '',
                'mdrf_sn': '',
                'common_use_unit_inner_sn': '',
                'activeClass': '',
                'key': '',
                'keyFormat': '',
                'partB_name': '',
                'status': 0,
                'quantity_delivered': '',
                'unit_price_text': 0,
                'money': 0,
                'discount': 0,
                'discount_unit_price': 0,
                'discount_money': 0,
                'rate': 0,
                'tax_unit_price': 0,
                'tax_amount': 0,
                'tax': 0,
                'sys_type': '',
                'unit_inner_sn': [],
                'unit_name': '',
                'unitList': [],
                'quantity': 1,
                'isLoadingUnit': false,
                'row_add_flag': true,
                'temp_add_flag': true,
                'batch_number': '',
                'unitPriceList': []
            };
            xTableAddInfo.clearCurrentRow();
            let spliceIndex = this.planModalDatas.findIndex((item) => !item.product_inner_sn);
            if (spliceIndex > -1) {
                return;
            }
            xTableAddInfo.insertAt(tempProduct, -1);
            this.relevantInstitutionsFocus = false;
            const tableData = xTableAddInfo.getTableData().tableData,
                curData = tableData[tableData.length - 1];
            // this.planModalDatas = tableData;
            xTableAddInfo.setCurrentRow(curData);
            this.scrollTableArrowDownFn();
        },
        // 表格详情滚动到底部
        scrollTableArrowDownFn() {
            const xTableAddInfo = this.$refs.xTablePlanModalInfo;
            // rowHeight = 38
            // xTableAddInfo.refreshScroll()
            xTableAddInfo.clearScroll().then(() => {
                xTableAddInfo.scrollToColumn(xTableAddInfo.getColumns(0));
                xTableAddInfo.scrollToRow(xTableAddInfo.getData(this.planModalDatas.length - 1));
                // xTableAddInfo.scrollTo(0, this.planModalDatas.length * rowHeight)
            });
        },
        // 表格的键盘事件
        buyTableKeydownFn(tableEvent) {
            const { $event } = tableEvent,
                keyCode = parseInt($event.keyCode);

            if (keyCode === 27) {
                // Esc
                // this.addProductZoomFn()
                return;
            }

            if (
                (Object.keys(this.planModalForm).length && this.planModalForm.status > 1) ||
                (this.addProductEnter && this.addProductMd.alert)
            )
                return;
            // const {$event, $grid, $table} = tableEvent,

            if (keyCode === 13) {
                // 回车键
                this.enterKeydownFn();
            } else {
                if (this.addProductMd.alert) return;
                switch (keyCode) {
                    case 38: // ↑
                        this.toogleVerticalFocusAndActiveRowFn(keyCode);
                        break;
                    case 40: // ↓
                        this.toogleVerticalFocusAndActiveRowFn(keyCode);
                        break;
                    case 37: // ←
                        this.toogleHorizontalFocusRowFn(keyCode);
                        break;
                    case 39: // →
                        this.toogleHorizontalFocusRowFn(keyCode);
                        break;
                }
            }
        },
        // 表格的回车事件
        enterKeydownFn() {
            this.resetFormFocusFn();
            const xTableAddInfo = this.$refs.xTablePlanModalInfo,
                tableData = xTableAddInfo.getTableData().tableData,
                existProductEmptyIndex = tableData.findIndex((item) => !item.product_inner_sn);

            xTableAddInfo.clearCurrentRow();

            xTableAddInfo.focus();
            if (existProductEmptyIndex <= -1) {
                // 需要创建空数据
                this.resetFocusFn();
                this.insertTempProductDetailFn();
            } else {
                // 说明有需要添加的空数据，直接调出产品添加页面
                if (!this.addProductMd.alert) {
                    this.partB['isKeyCode'] = true;
                    this.partB['keyCodeVal'] = tableData[existProductEmptyIndex].product_name || '';
                    this.dblclickTableFn();
                    setTimeout(() => {
                        this.addProductEnter = true;
                    }, 800);
                } else {
                    if (this.addProductMd.alert && this.addProductEnter) {
                        // this.addProductFn()
                    }
                }
            }
        },
        addProductDetailFn(e) {
            if (e.key === 'Enter' && this.addProductMd.alert && this.addProductEnter) {
                this.addProductFn();
            }
        },
        addEventProductDocumentFn() {
            document.addEventListener('keyup', this.addProductDetailFn, false);
        },
        removeEventProductDocumentFn() {
            document.removeEventListener('keyup', this.addProductDetailFn, false);
        },
        // 所有光标重置
        resetFocusFn(property, rowIndex) {
            const xTableAddInfo = this.$refs.xTablePlanModalInfo;
            for (let i = 0; i < this.planModalDatas.length; i++) {
                let item = this.planModalDatas[i];
                if (rowIndex === i) {
                    item.focusDatas = [
                        property === 'product_name',
                        property === 'quantity',
                        property === 'unit_inner_sn',
                        property === 'batch_number',
                        property === 'rate',
                        property === 'tax_unit_price'
                    ];
                } else {
                    item.focusDatas = [false, false, false, false, false, false];
                }
            }
            // xTableAddInfo.loadData(this.planModalDatas);
        },
        // 水平方向切换焦点事件
        toogleHorizontalFocusRowFn(keyCode) {
            const xTableAddInfo = this.$refs.xTablePlanModalInfo,
                currowData = xTableAddInfo.getCurrentRecord(),
                focusDatas = currowData.focusDatas,
                rowHeight = 38;

            let currowIndex = xTableAddInfo.getRowIndex(currowData);

            if (!currowData.product_inner_sn) return;
            let curFocusIndex = focusDatas.findIndex((item) => !!item),
                isBoundary = false; // 是否是边界值，默认不是边界

            if (keyCode === 39) {
                if (curFocusIndex + 1 <= focusDatas.length - 1) {
                    curFocusIndex += 1;
                } else {
                    isBoundary = true;
                }
            } else {
                if (curFocusIndex - 1 >= 1) {
                    curFocusIndex -= 1;
                } else {
                    isBoundary = true;
                }
            }

            if (isBoundary) return;

            for (let f = 0; f < focusDatas.length; f++) {
                if (f === curFocusIndex) {
                    focusDatas[f] = true;
                } else {
                    focusDatas[f] = false;
                }
            }
            xTableAddInfo.reloadRow(
                {
                    ...currowData,
                    focusDatas
                },
                null,
                'focusDatas'
            );

            xTableAddInfo.syncData();

            this.planModalDatas = xTableAddInfo.getTableData().tableData;

            currowIndex = curFocusIndex === 0 ? currowIndex : currowIndex + 1;
            // xTableAddInfo.clearScroll().then(() => {
            //     xTableAddInfo.scrollToColumn(xTableAddInfo.getColumns(currowIndex <= 2 ? 0 : 12))
            //     xTableAddInfo.scrollToRow(xTableAddInfo.getData(currowIndex))
            // })
            // 产品、 数量处理 [滚动条滚动到最左侧]
            if ((curFocusIndex === 0 || curFocusIndex === 1) && focusDatas[curFocusIndex]) {
                xTableAddInfo.clearScroll().then(() => {
                    // xTableAddInfo.scrollTo(0, currowIndex * rowHeight)
                    xTableAddInfo.scrollToColumn(xTableAddInfo.getColumns(0));
                    xTableAddInfo.scrollToRow(xTableAddInfo.getData(currowIndex));
                });
            }
            // 税率处理 [滚动条滚动到最右侧]
            if (curFocusIndex === 3 && focusDatas[curFocusIndex]) {
                xTableAddInfo.clearScroll().then(() => {
                    xTableAddInfo.scrollTo(900, currowIndex * rowHeight);
                });
            }

            // 包装单位额外处理
            if (curFocusIndex === 2 && focusDatas[curFocusIndex]) {
                this.clickUnitListFn(currowData);
            }
        },
        closeAddProductFn() {
            this.addProductEnter = false;
            this.addProductMd.alert = false;
        },
        addProductZoomFn() {
            this.isAddProductModalTableMax = !this.isAddProductModalTableMax;
            const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
            if (xTablePlanModalInfo) {
                xTablePlanModalInfo.focus();
            }
            // if (this.isAddProductModalTableMax) {
            //     xTablePlanModalInfo.revert()
            // } else {
            //     xTablePlanModalInfo.maximize()
            // }
            // this.isAddProductModalTableMax = !this.isAddProductModalTableMax
            // xTablePlanModalInfo.zoom().then((type, $event) => {
            //     if (this.isAddProductModalTableMax == type && type) {
            //         xTablePlanModalInfo
            //     }
            //     console.log(type)
            //     this.isAddProductModalTableMax = type
            // })
        },
        // 垂直方向切换焦点事件
        toogleVerticalFocusAndActiveRowFn(keyCode) {
            const xTableAddInfo = this.$refs.xTablePlanModalInfo,
                tableData = xTableAddInfo.getTableData().tableData,
                rowHeight = 38;

            let currowData = xTableAddInfo.getCurrentRow(),
                currowIndex = xTableAddInfo.getRowIndex(currowData),
                isBoundary = false; // 是否是边界值，默认不是边界

            if (keyCode === 40) {
                if (currowIndex + 1 <= tableData.length - 1) {
                    currowIndex += 1;
                } else {
                    isBoundary = true;
                }
            } else {
                if (currowIndex - 1 >= 0) {
                    currowIndex -= 1;
                } else {
                    isBoundary = true;
                }
            }

            if (isBoundary) return;

            this.resetFocusFn();

            currowData = tableData[currowIndex];
            if (!currowData.product_inner_sn) {
                currowData.focusDatas.splice(0, 1, true);
            } else {
                currowData.focusDatas.splice(1, 1, true);
            }

            xTableAddInfo.reloadRow(currowData, null, 'focusDatas');

            xTableAddInfo.syncData();
            xTableAddInfo.clearCurrentRow();
            xTableAddInfo.setCurrentRow(currowData);

            this.planModalDatas = xTableAddInfo.getTableData().tableData;

            xTableAddInfo.clearScroll().then(() => {
                if (currowIndex <= 1) {
                    currowIndex = 0;
                }
                if (currowIndex >= tableData.length - 1) {
                    currowIndex = tableData.length;
                }
                xTableAddInfo.scrollTo(null, currowIndex * rowHeight);
            });
        },
        warehouseUsableTopListAPIFn() {
            this.planModalWarehouseData = this.$store.state.warehouseList;
            this.isWareHouseFilterLoading = false;
            // await warehouseUsableTopListAPI()
            // .then(res => {
            //     if (parseFloat(res.code) === 0) {
            //         const {list} = res
            //         let newList = list.map((item, index) => {
            //             return {
            //                 ...item,
            //                 title: item.name,
            //                 value: item.warehouse_model_inner_sn,
            //                 key: (Math.random() + 1).toString(36).substring(7)
            //             }
            //         })
            //         this.planModalWarehouseData = newList
            //         this.isWareHouseFilterLoading = false
            //     } else {
            //         this.$message.error(res.msg)
            //     }
            // })
        },
        relevantInstitutionsKeyDownFn(e) {
            if (this.relevantInstitutionsFocus) {
                this.isParentKeyDownEnter = true;
                this.openChooseTb(e.target.value);
            }
        },
        /**
         * 更新订单中某一条产品记录的税率值
         */
        updateProductRate(value, index) {
            /// 先将焦点重置
            this.resetFocusFn();

            let v = value;
            if (value === null) {
                v = 0;
            }
            this.planModalDatas[index].rate = v;
            this.updateFooterPlanModalFn();
        },
        /**
         * 更新订单中某一条产品记录的数量值
         */
        updateProductQuantity(value, index) {
            /// 先将焦点重置
            this.resetFocusFn();

            const record = this.planModalDatas[index];
            const { multiply, round } = this.$XEUtils;
            let v = value;
            if (value === null) {
                v = 1;
            }
            record.quantity = v;
            record.tax_amount = round(multiply(v, record.tax_unit_price), 3);
            this.updateFooterPlanModalFn();
        },
        /**
         * 更新订单中某一条产品记录的含税单价值
         * 此时动态地计算含税金额
         * 产品含税单价对应后端接口中的unit_price字段、前端的tax_unit_price字段，而前端的unit_price字段代表的是不含税的产品单价
         */
        updateProductTaxUnitPrice(value, index) {
            /// 先将焦点重置
            this.resetFocusFn();

            const { multiply, toFixed } = this.$XEUtils;
            const record = this.planModalDatas[index];
            let v = value;
            if (value === null) {
                v = 0;
            }
            record.tax_unit_price = v;
            record.tax_amount = parseFloat(toFixed(multiply(v, record.quantity), 4));
            this.updateFooterPlanModalFn();
        },
        /**
         * 更新订单中某一条产品记录的含税金额值（总记）
         * 此时动态地计算含税单价（总是假定产品数量不变，以用户输入为准）
         * 含税单价保持小数后4位的精度
         */
        updateProductTaxAmountTotal(value, index) {
            /// 先将焦点重置
            this.resetFocusFn();

            const { divide, toFixed } = this.$XEUtils;
            const record = this.planModalDatas[index];
            let v = value;
            if (value === null) {
                v = 0;
            }
            record.tax_amount = v;
            record.tax_unit_price = parseFloat(toFixed(divide(v, record.quantity), 4));
            this.updateFooterPlanModalFn();
        },
        /// 如果模态框打开，按Esc关闭之
        handlerAddInfoModalFn(e) {
            const { keyCode } = e;
            if (keyCode === 27 && this.planModal.alert) {
                // Esc
                this.planModal.alert = false;
                this.afterClosePlanmodalFn();
            }
        },
        handlerAddInfoModalAddEventFn() {
            document.addEventListener('keydown', this.handlerAddInfoModalFn, false);
        },
        handlerAddInfoModalRemoveEventFn() {
            document.removeEventListener('keydown', this.handlerAddInfoModalFn, false);
        },
        // ****************************************************************
        // 接口区域结束
        // ****************************************************************
        /**
         * 从billsType中获取相关机构类型
         * @param billsType
         */
        parseOrgNameFromBillsType(billsType) {
            const orgTypeStr = billsType.substr(billsType.length - 1, 1);
            let orgTypeName = '';
            switch (orgTypeStr) {
                case 'H':
                    orgTypeName = '医院';
                    break;
                case 'V':
                    orgTypeName = '厂商';
                    break;
                case 'D':
                    orgTypeName = '经销商';
                    break;
            }
            return orgTypeName;
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
                            item.org_name = item.name;
                            item.org_type_name = this.parseOrgNameFromBillsType(item.billsType);
                            return item;
                        });
                        this.tableDataTemp = this.tableDataTemp.concat(TempData);
                    }
                })
                .finally(() => {
                    this.tableData = this.tableDataTemp;
                });
        },
        changeSupplyUnit(value, label) {
            if (value === 'search-more') {
                this.openChooseTb(this.supplySearch);
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
        loadSupplyUnits(search = '') {
            this.supplySearch = search;
            this.tableDataTemp = [];
            this.tableData = [];
            this.grnOrgListAPIFn(2, search);
            this.grnOrgListAPIFn(3, search);
        },
        // 【厂商】产品列表
        async orderVendorListAPIFn(value = '') {
            let params = {
                org_id: this.$cookie.get('userbelong'),
                organization_inner_sn: this.selectedSupply.org_id,
                isPurchase: true,
                name: value, // this.searchDataVen.name
                specification: '', //this.searchDataVen.specification
                vendor_inner_sn: '', //this.searchDataVen.vendor_inner_sn
                specification_vendor_sn: '', //this.searchDataVen.specification_vendor_sn,
                type_inner_sn: '', //this.searchDataVen.type_inner_sn.length
                //? this.searchDataVen.type_inner_sn[this.searchDataVen.type_inner_sn.length - 1]
                //: '',
                code68_sn: '', // this.searchDataVen.code68_sn.length
                // ? this.searchDataVen.code68_sn[this.searchDataVen.code68_sn.length - 1]
                //: '',
                billsType: this.partB.billsType,
                onlyContrctProduct: false,
                pageSize: 10,
                pageNum: 1
            };

            await orderVendorListAPI({
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let { rows } = res;
                    this.venTableData = [];
                    this.venTableData = rows.map((item) => {
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
                            name: item.product_name,
                            key: (Math.random() + 1).toString(36).substring(7),
                            keyFormat: !isNaN(item.vendor_inner_sn)
                                ? item.vendor_inner_sn +
                                    '' +
                                    item.product_inner_sn +
                                    1 +
                                    item.specification_inner_sn
                                : item.vendor_b_inner_sn + '' + item.product_inner_sn + 1,
                            product_name: item.product_name,
                            partB_name: item.vendor_name || item.dealer_name,
                            mdrf_sn: item.mdrf_sn,
                            product_type: item.product_type,
                            status: 0,
                            quantity_delivered: '',
                            unit_price_text: 1,
                            money: 1,
                            discount: 1,
                            discount_unit_price: 1,
                            discount_money: 1,
                            rate: 0,
                            tax_unit_price: 1,
                            tax_amount: 1,
                            tax: 1,
                            sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                            quantity: 1
                        };
                    });
                    this.venFetching = true;
                } else {
                    this.$message.error(res.msg);
                }
            })
            .catch((e) => {
                console.log(e);
            });
        },
        popupAddProduct() {
            this.addProductMd.alert = true;
            let timer = setTimeout(() => {
                this.$Utils.globalDragCenterFn('add_product');
                clearTimeout(timer);
                timer = null;
            });
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
            }
        }
    },
    filters: {
        numberToStringFilter(value) {
            if (typeof value !== 'number') {
                return value;
            }
            if (isNaN(value)) {
                return undefined;
            }
            return String(value);
        },
        formatStatus(value, name) {
            const planListStatus = ['草稿', '正式', '已审核', '已确认', '已终止', '已完结'];
            if (name === 'planList') {
                return planListStatus[value];
            } else if (name === 'planOrder') {
                return productStatusTextMap[value];
            }
        },
        formatOrgType(value) {
            const planListStatus = ['自建', '卖方', '已审核', '已确认', '已终止', '已完结'];
            return planListStatus[value];
        },
        filtersTwo(value) {
            // 保留两位小数
            if (typeof value === 'number') {
                return value.toFixed(2);
            }
            return '';
        }
    },
    computed: {
        scrollYPurchaseDelarNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 214 : 154,
                pager = 52;
            return clientHeight - topHeight - pager;
        },
        isEditDisabled() {
            // 弹框的内容是否可编辑
            let flag = true;
            if (this.btnName === 'add') {
                flag = false;
            } else if (
                (this.tabsActiveKey === this.systemTypes[0] || this.tabsActiveKey === this.systemTypes[1]) &&
                this.planModalForm.status <= 1 &&
                this.planModalForm.create_organization_type === 0
            ) {
                // 采购单：create_organization_type 0-自建  1-买家
                flag = false;
            }
            return flag;
        },
        /**
         * 仓库是否可编辑
         */
        isWarehouseEditDisabled() {
            if (this.isEditDisabled) {
                // 当单据的来源为销售方，进行单据确认步骤（此时单据状态为已审批）时可以修改仓库
                if (this.planModalForm.status === 2 && this.planModalForm.create_organization_type === 1) {
                    return false;
                }
            }
            return this.isEditDisabled;
        },
        isSourceBuyerAudited() {
            // 单据来源为销售方且状态为已审核的销售订单可以编辑数量和单价 禅道单号（2967）
            const status = this.planModalForm.status; // 状态 0-草稿 1-正式 2-已审批 3-确认 4-已终止 5-已完结
            const source = this.planModalForm.create_organization_type; // 单据生成方 0-采购/采购自建/买方 1-销售/销售自建/卖方
            return status === 2 && source === 1;
        },
        // 当前用于判断含税单价是否置灰，后期可修改为全局判断标准
        isPriceDisabled() {
            // 将来源为自建的已审核和已确认状态，以及来源为卖方的已确认状态的采购订单中的含税单价置灰。 禅道单号（3696）
            const pageType = this.myOrderPropObj.pageType; // 页面属性 0-采购订单 1-销售订单
            const status = this.planModalForm.status; // 状态 0-草稿 1-正式 2-已审批 3-确认 4-已终止 5-已完结
            const source = this.planModalForm.create_organization_type; // 单据生成方 0-采购/采购自建/买方 1-销售/销售自建/卖方
            const flag =
                (pageType === 0 && status === 2 && source === 0) || // 来源为采购自建的已审核状态
                (pageType === 0 && status === 3 && source === 0) || // 来源为采购自建的已确认状态
                (pageType === 0 && status === 3 && source === 1); // 来源为卖方的已确认状态
            return flag;
        },
        isEditDisabledPo() {
            // 单据编号【弹框的内容是否可编辑】
            let flag = true;
            if (this.btnName === 'add') {
                flag = false;
            } else if (
                (this.tabsActiveKey === this.systemTypes[0] || this.tabsActiveKey === this.systemTypes[1]) &&
                this.planModalForm.status <= 2
            ) {
                flag = false;
            }
            return flag;
        },
        isAddPlanModalDisabled() {
            // 内层的生成采购订单【采购计划单按钮】
            let flag = true;
            if (this.planModalDatas.length) {
                flag =
                    this.planModalSelectedRowKeys.length <= 0 ||
                    this.btnName === 'add' ||
                    (this.planModalForm.status > 0 && this.planModalForm.status < 4);
            }
            return flag;
        },
        rowSelectionPlanModal() {
            // 内层的生成采购订单前面的复选框状态
            const _this = this;
            return {
                onChange: (selectedRowKeys) => {
                    _this.planModalSelectedRowKeys = selectedRowKeys;
                },
                getCheckboxProps: (record) => ({
                    props: {
                        disabled:
                            record.status < 0 ||
                            this.btnName === 'add' ||
                            (this.planModalForm.status > 0 && this.planModalForm.status < 4)
                    }
                })
            };
        },
        planModalTextComputed() {
            // 采购计划单右下角的取消文字改变
            if (
                this.planModalForm.status === 0 ||
                this.planModalForm.status === 1 ||
                (this.planModalForm.status === 2 && this.planModalForm.create_organization_type !== 0) ||
                this.btnName === 'add'
            ) {
                return '取消';
            } else {
                return '关闭';
            }
        },
        displayRenderUnitFn({ labels }) {
            // 包装单位
            let label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
        },
        tabsTextComputed() {
            // 顶部头部的tabs切换标题
            return (num) => {
                // num: 第几个tabs切换的参数 - @required
                let title = '';
                switch (num) {
                    case 0:
                        switch (this.myOrderPropObj.pageType) {
                            case 0:
                                title = this.tabTexts[0];
                                break;
                            case 1:
                                title = this.tabTexts[2];
                                break;
                        }
                        break;
                    case 1:
                        switch (this.myOrderPropObj.pageType) {
                            case 0:
                                title = this.tabTexts[1];
                                break;
                            case 1:
                                title = this.tabTexts[3];
                                break;
                        }
                        break;
                    case 2:
                        switch (this.myOrderPropObj.pageType) {
                            case 0:
                                title = this.tabTexts[4];
                                break;
                            case 1:
                                title = this.tabTexts[4];
                                break;
                        }
                        break;
                }
                return title;
            };
        },
        placeholderComputed() {
            // 顶部搜索框文字
            let placeholder = '';
            if (this.tabsActiveKey === this.systemTypes[0]) {
                placeholder = this.myOrderPropObj.pageType === 0 ? this.tabTexts[0] : this.tabTexts[2];
            } else if (this.tabsActiveKey === this.systemTypes[1]) {
                placeholder = this.myOrderPropObj.pageType === 0 ? this.tabTexts[1] : this.tabTexts[3];
            } else if (this.tabsActiveKey === this.systemTypes[2]) {
                placeholder = '采购单位';
            }
            return placeholder;
        },
        historyColumnsComputed() {
            // 顶部外层表格的tab切换卡中的历史头部文字
            let title = '';
            if (this.myOrderPropObj.pageType === 0) {
                title = this.tabTexts[0] + '/' + this.tabTexts[1];
            } else if (this.myOrderPropObj.pageType === 1) {
                title = this.tabTexts[2] + '/' + this.tabTexts[3];
            }
            return title;
        },
        modalTopTitle() {
            // 模态框顶部的文字
            let title = '';
            switch (this.myOrderPropObj.pageType) {
                case 0:
                    title = '采购';
                    break;
                case 1:
                    title = '销售';
                    break;
            }
            return title;
        },
        topEditBtnDisabled() {
            // 外层顶部的修改按钮状态控制
            let flag = true;
            switch (this.myOrderPropObj.pageType) {
                case 0: {
                    let purchase1 = parseFloat(this.currentPurchaseDelarTrObj.status),
                        purchase2 = parseFloat(this.currentPurchaseVendorTrObj.status);
                    if (this.tabsActiveKey === this.systemTypes[0]) {
                        flag = !(purchase1 === 0 || purchase1 === 1);
                    } else if (this.tabsActiveKey === this.systemTypes[1]) {
                        flag = !(purchase2 === 0 || purchase2 === 1);
                    }
                    break;
                }
                case 1: {
                    let sale1 = parseFloat(this.currentSaleDelarTrObj.status),
                        sale2 = parseFloat(this.currentSaleVendorTrObj.status);
                    flag = !(sale1 === 0 || sale1 === 1);
                    if (this.tabsActiveKey === this.systemTypes[0]) {
                        flag = !(sale1 === 0 || sale1 === 1);
                    } else if (this.tabsActiveKey === this.systemTypes[1]) {
                        flag = !(sale2 === 0 || sale2 === 1);
                    }
                    break;
                }
            }
            return flag;
        },
        // 单价 = 含税单价 / (1 + 税率 / 100)
        unit_price_record() {
            const { isNumber, toFixed, add, divide } = this.$XEUtils;
            return (record) => {
                // parse float to all
                const fRate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
                const fTaxUnitPrice = parseFloat(record.tax_unit_price);

                let nextItem = divide(fTaxUnitPrice, add(1, fRate));
                if (isNumber(nextItem)) {
                    if (nextItem < 0) nextItem = 0;
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        // 金额 = 数量 * 单价
        money_record() {
            const { isNumber, toFixed, multiply, add, divide } = this.$XEUtils;
            return (record) => {
                // parse float to all
                const fRate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
                const fTaxUnitPrice = parseFloat(record.tax_unit_price);
                const fQuantity = parseFloat(record.quantity);

                const nextItem = multiply(divide(fTaxUnitPrice, add(1, fRate)), fQuantity);
                if (isNumber(nextItem)) {
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        // 折后单价
        discount_unit_price_record() {
            const { isNumber, toFixed, multiply } = this.$XEUtils;
            return (record) => {
                // parse float to all
                const fDiscount = parseFloat(record.discount);
                const fUnitPrice = parseFloat(record.unit_price_text);

                const nextItem = multiply(fUnitPrice, fDiscount);
                if (isNumber(nextItem)) {
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        // 合计：折后金额
        discount_money_total() {
            const { isNumber, multiply, add } = this.$XEUtils;
            return this.planModalDatas.reduce((prev, next) => {
                // parse float to all
                const fNextUnitPrice = parseFloat(next.unit_price);
                const fNextDiscount = parseFloat(next.discount);
                const fNextQuantity = parseFloat(next.quantity);

                let prevItem = parseFloat(prev),
                    nextItem = multiply(multiply(fNextUnitPrice, fNextQuantity), fNextDiscount);

                // 不计算已终止的产品
                const isStop = this._isStop(next.status);
                if (isStop) nextItem = 0;

                if (isNumber(prevItem) && isNumber(nextItem)) {
                    return add(prevItem, nextItem);
                } else {
                    return '';
                }
            }, 0);
        },
        // 折后金额
        discount_money_record() {
            const { isNumber, toFixed, multiply } = this.$XEUtils;
            return (record) => {
                // parse float to all
                const fDiscount = parseFloat(record.discount);
                const fQuantity = parseFloat(record.quantity);
                const fUnitPrice = parseFloat(record.unit_price_text);

                const nextItem = multiply(multiply(fUnitPrice, fQuantity), fDiscount);
                if (isNumber(nextItem)) {
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        // 含税单价
        tax_unit_price_record() {
            const { isNumber, toFixed, add, multiply, toNumber } = this.$XEUtils;
            return (record) => {
                // parse float to all
                const fRate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
                const fUnitPrice = parseFloat(record.unit_price_text);

                let nextItem = multiply(multiply(fUnitPrice, parseFloat(record.discount)), add(1, fRate));
                if (isNumber(nextItem)) {
                    return toFixed(toNumber(nextItem), 4);
                } else {
                    return '';
                }
            };
        },
        // 含税金额 = 数量 * 含税单价
        tax_amount_record() {
            const { isNumber, toFixed, multiply } = this.$XEUtils;
            return (record) => {
                const fTaxUnitPrice = parseFloat(record.tax_unit_price);

                let nextItem = multiply(record.quantity, fTaxUnitPrice);
                if (isNumber(nextItem)) {
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        // 税额 = 含税金额 - 金额
        tax_record() {
            const { isNumber, toFixed, add, subtract, multiply, divide } = this.$XEUtils;
            return (record) => {
                // parse float to all
                const fRate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
                const fTaxUnitPrice = parseFloat(record.tax_unit_price);
                const fQuantity = parseFloat(record.quantity);
                const fMoney = multiply(divide(fTaxUnitPrice, add(1, fRate)), fQuantity);

                let nextItem = subtract(multiply(fQuantity, fTaxUnitPrice), fMoney);
                if (isNumber(nextItem)) {
                    if (nextItem < 0) nextItem = 0;
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        // 合计：金额
        money_total() {
            const records = this.planModalDatas;
            if (!records) {
                return 0;
            }
            const { isNumber, add, multiply, divide } = this.$XEUtils;
            return this.planModalDatas.reduce((prevValue, currentItem) => {
                const quantity = isNumber(currentItem.quantity) ? currentItem.quantity : 0;
                const taxUnitPrice = isNumber(currentItem.tax_unit_price) ? currentItem.tax_unit_price : 0;
                if (taxUnitPrice === 0) {
                    return prevValue;
                }
                const currentValue = multiply(divide(taxUnitPrice, add(1, divide(currentItem.rate, 100))), quantity);
                // 不计算已终止的产品
                if (this._isStop(currentItem.status)) {
                    return prevValue;
                }
                return add(prevValue + currentValue);
            }, 0);
        },
        // 合计：含税金额
        tax_amount_total() {
            const records = this.planModalDatas;
            if (!records) {
                return 0;
            }
            const { isNumber, add } = this.$XEUtils;
            return records.reduce((prevValue, currentItem) => {
                const currentValue = isNumber(currentItem.tax_amount) ? currentItem.tax_amount : 0;
                // 不计算已终止的产品
                if (this._isStop(currentItem.status)) {
                    return prevValue;
                }
                return add(prevValue, currentValue);
            }, 0);
        },
        // 合计：税额
        tax_total() {
            const records = this.planModalDatas;
            if (!records) {
                return 0;
            }
            const { isNumber, add, subtract, multiply, divide } = this.$XEUtils;
            return records.reduce((prevValue, currentItem) => {
                const rate = isNumber(currentItem.rate) ? divide(currentItem.rate, 100) : 0;
                const taxUnitPrice = isNumber(currentItem.tax_unit_price) ? currentItem.tax_unit_price : 0;
                const quantity = isNumber(currentItem.quantity) ? currentItem.quantity : 0;
                if (taxUnitPrice === 0) {
                    return prevValue;
                }
                const currentValue = multiply(quantity, subtract(taxUnitPrice, divide(taxUnitPrice, add(rate, 1))));
                // 不计算已终止的产品
                if (this._isStop(currentItem.status)) {
                    return prevValue;
                }
                return add(prevValue, currentValue);
            }, 0);
        },
        scrollYAddProductNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 180,
                bottomHeight = 116;
            return clientHeight - topHeight - bottomHeight;
        },
        orgName() {
            const employeeInfo = this.$cookie.get('EmployeeInfo');
            return (employeeInfo ? JSON.parse(employeeInfo).OrgName : '') || '';
        },
        ...mapState(['warehouseList'])
    },
    mounted() {
        this.resetPlanModalFn();
        this.changeTabFn();
        // focus
        let aSelectDom = document.getElementsByClassName('focus-a-select');
        aSelectDom[0].getElementsByClassName('ant-select-search__field')[0].click();
    },
    watch: {
        addProductEnter: {
            handler(val) {
                if (val) {
                    this.addEventProductDocumentFn();
                } else {
                    this.removeEventProductDocumentFn();
                }
            },
            deep: true
        },
        'planModal.alert': {
            handler(val) {
                if (val) {
                    this.handlerAddInfoModalAddEventFn();
                } else {
                    this.handlerAddInfoModalRemoveEventFn();
                }
            },
            deep: true
        }
    },
    beforeDestroy() {
        this.removeEventProductDocumentFn();
        this.handlerAddInfoModalRemoveEventFn();
    }
};
</script>

<style lang="less" scoped>
@w: 24px;
.order-modal-list {
    //   position: absolute;
    //   left: @w;
    //   right: @w;
    //   bottom: 57px;
    display: flex;
    flex-direction: row;

    li {
        display: flex;
        flex-direction: row;
        padding: 24px 0;

        &:nth-of-type(1),
        &:nth-of-type(2) {
            flex: 1;

            .item-wrap {
                flex: 1;
            }
        }

        .item-wrap {
            display: flex;
            padding-right: 16px;

            .item {
                padding-bottom: 0;
                padding-right: @w;
            }
        }

        &:last-child {
            .item-wrap {
                &:last-child {
                    padding-right: 0;
                }
            }
        }
    }
}
.modal-form-input-scoped .modal-form-input .top-list .label {
    display: flex;
    width: auto;
    max-width: 94px;
}
// 顶部按钮多的样式重新调整左右间距
.scoped .btn-top-wrap .btn-list-wrap li,
.scoped .btn-top-wrap .btn-list-wrap li .ant-btn {
    margin-right: 10px;
}
// 高级搜索栏
ul#searchForm {
    li.search-form-item {
        width: auto;
        min-width: 280px;
        &.search-button {
            min-width: auto;
        }
        span.label,
        input.ant-input {
            display: inline-block;
        }
        span.label {
            min-width: 72px;
        }
    }
}
.send-plan-order-preview {
    .ant-modal-body {
        padding: 0;
    }

    .modal-form-input ul {
        &.build-plan-parent {
            margin-bottom: 0;
            padding: 15px;

            .build-plan-parent-item {
                padding-bottom: 24px;
                margin-bottom: 24px;
                border-bottom: 4px dashed #ddd;
            }
        }
    }
}
</style>
