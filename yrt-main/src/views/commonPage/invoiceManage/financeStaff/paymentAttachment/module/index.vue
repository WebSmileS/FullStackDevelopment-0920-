<template>
    <div class="scoped scoped-white">
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :sm="22" :md="22" :lg="18" :xl="18" :xxl="15">
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button type="primary" size="small" @click="groupBtnFn('add')"> 新增 </a-button>
                            <a-button type="primary" size="small" @click="groupBtnFn('edit')"> 编辑 </a-button>
                            <a-button
                                :disabled="tabsActiveKey === tabPane(pageNameType, 1, 'value')"
                                type="danger"
                                size="small"
                                @click="groupBtnFn('del')"
                            >
                                删除
                            </a-button>
                        </li>
                        <li>
                            <a-button
                                :disabled="tabsActiveKey === tabPane(pageNameType, 1, 'value')"
                                type="primary"
                                size="small"
                                @click="groupBtnFn('pay')"
                            >
                                {{ is_PaymentOrder ? '付款' : '收款' }}
                            </a-button>
                            <a-button
                                :disabled="tabsActiveKey === tabPane(pageNameType, 1, 'value')"
                                type="danger"
                                size="small"
                                @click="groupBtnFn('recorded')"
                            >
                                入账
                            </a-button>
                            <a-button type="primary" size="small" @click="financeInvoiceExportAPIFn">
                                导出Excel
                            </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :sm="4" :md="2" :lg="6" :xl="6" :xxl="9">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-show="!hightSearchObj.isArrow"
                            v-model="searchTables.invoice_no"
                            @search="searchBtnFn"
                            placeholder="付款编号"
                            size="small"
                            enterButton
                            class="search"
                        />
                        <div @click.stop="hightSearchIconFn" class="hight-search-text">
                            <a-tooltip placement="left" title="高级搜索">
                                <a-icon :type="hightSearchObj.isArrow ? 'up-circle' : 'down-circle'" />
                            </a-tooltip>
                        </div>
                    </div>
                </a-col>
            </a-row>
            <a-row :gutter="24" v-show="hightSearchObj.isArrow" class="hight-search-wrap">
                <a-col :span="6" class="form-wrap">
                    <span class="form-label">相关机构</span>
                    <a-input
                        type="text"
                        placeholder=""
                        class="form-input"
                        size="small"
                        @click="openChooseTab"
                        allowClear
                        v-model="searchTables.organization_name"
                    />
                </a-col>
                <a-col :span="5" class="form-wrap">
                    <span class="form-label">付款编号</span>
                    <a-input
                        type="text"
                        placeholder=""
                        class="form-input"
                        style="height: 24px"
                        v-model="searchTables.invoice_no"
                    />
                </a-col>
                <a-col :span="5" class="form-wrap">
                    <span class="form-label">发票状态</span>
                    <a-tree-select
                        treeNodeFilterProp="title"
                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                        placeholder=""
                        treeDefaultExpandAll
                        class="form-input"
                        size="small"
                        :treeData="invoiceStatusData"
                        v-model="searchTables.invoice_status"
                    >
                    </a-tree-select>
                </a-col>
                <a-col :span="5" class="form-wrap">
                    <span class="form-label">日期</span>
                    <a-range-picker
                        :placeholder="['', '']"
                        size="small"
                        class="form-input"
                        :style="{ width: '70%' }"
                        v-model="rangePickerVal"
                        @change="changeRangePickerFn"
                    >
                    </a-range-picker>
                </a-col>
                <a-col :span="3">
                    <a-button @click="searchBtnFn" size="small" type="primary"> 查询 </a-button>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab切换区域开始 -->
        <div
            :class="hightSearchObj.isArrow ? 'top136' : 'top56'"
            class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
        >
            <a-tabs type="card" v-model="tabsActiveKey">
                <a-tab-pane
                    :tab="tabPane(pageNameType, 0)"
                    style="padding: 8px"
                    :key="tabPane(pageNameType, 0, 'value')"
                >
                    <table-list
                        v-show="tabsActiveKey === tabPane(pageNameType, 0, 'value')"
                        :refName="tabPane(pageNameType, 0, 'value')"
                        :isRefresh="this[`is${tabPane(pageNameType, 0, 'value')}Refresh`]"
                        :scrollYNumber="scrollYNumber"
                        :searchData="searchData"
                        :ref="tabPane(pageNameType, 0, 'value')"
                        :isInbound="is_PaymentOrder"
                        :btnName="btnName"
                        :tableDetailCallbackData="tableDetailCallbackData"
                        @getRowDataFn="getRowDataFn"
                        @lookDetailFn="lookDetailFn"
                    ></table-list>
                </a-tab-pane>
                <a-tab-pane
                    :tab="tabPane(pageNameType, 1)"
                    style="padding: 8px"
                    :key="tabPane(pageNameType, 1, 'value')"
                >
                    <table-list
                        v-show="tabsActiveKey === tabPane(pageNameType, 1, 'value')"
                        :refName="tabPane(pageNameType, 1, 'value')"
                        :isRefresh="this[`is${tabPane(pageNameType, 1, 'value')}Refresh`]"
                        :scrollYNumber="scrollYNumber"
                        :searchData="searchData"
                        :isInbound="is_PaymentOrder"
                        :btnName="btnName"
                        @getRowDataFn="getRowDataFn"
                        @lookDetailFn="lookDetailFn"
                    ></table-list>
                </a-tab-pane>
                <a-tooltip slot="tabBarExtraContent" placement="left">
                    <template slot="title">
                        <span>刷新</span>
                    </template>
                    <a-button
                        type="primary"
                        shape="circle"
                        icon="reload"
                        size="small"
                        class="refresh-btn"
                        @click="initTableFn"
                    />
                </a-tooltip>
            </a-tabs>
        </div>

        <!-- 详情区域 -->
        <table-detail
            :isOpen="isDetailModalOpen"
            :isInbound="is_PaymentOrder"
            :isShowCancel="tabsActiveKey === tabPane(pageNameType, 1, 'value')"
            :btnName="btnName"
            :rowData="rowData"
            @closeDetailModal="changeDetailModalIsOpenFn(false)"
            @saveDetailModal="saveDetailModalFn"
        ></table-detail>

        <!-- 相关机构区域 -->
        <choose-org-tab ref="organization" :isInbound="is_PaymentOrder" @getOrgData="getOrgDataFn"></choose-org-tab>
    </div>
</template>

<script>
import {
    pagePaymentAttachmentType,
    paymentTabFinance,
    receiptTabFinance,
    paymentInvoiceState,
    receiveInvoiceState,
    constantAll
} from '@/views/commonPage/invoiceManage/config/constants';

import chooseOrgTab from '@/views/commonPage/invoiceManage/components/organization';

import { tabPane } from '../mixins/tabPane.js';
import tableList from './tableList';
import tableDetail from './tableDetail';

import moment from 'moment';

import { financePaymentExportAPI, financeReceiveExportAPI } from '@/service/pageAjax';

export default {
    name: 'inboundSystemInvoiceFinance-index', // 付款单 | 收款单 = 财务人员使用
    props: {
        pageNameType: {
            // 页面标识类型 [PAYMENTORDER | RECEIVEORDER]
            type: String,
            retuired: true
        }
    },
    components: {
        tableList,
        tableDetail,
        chooseOrgTab
    },
    data() {
        return {
            isPAYMENTORDER: false,
            isRECEIVEORDER: false,

            // 表格刷新标识
            isVALID_PAYMENT_NOTERefresh: false,
            isCANCEL_A_BILL_OF_PAYMENTRefresh: false,
            isVALID_RECEIPTRefresh: false,
            isNULLIFY_RECEIPTRefresh: false,

            rangePickerVal: '',
            searchTables: {
                begin_date: '',
                end_date: '',
                invoice_no: '',
                invoice_status: '',
                billsType: '',
                organization_inner_sn: '',
                organization_name: ''
            },
            searchData: {},
            organizationData: [],
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            tableDetailCallbackData: null,
            tabsActiveKey: '',
            btnName: '',
            isDetailModalOpen: false
        };
    },
    methods: {
        async financeInvoiceExportAPIFn() {
            if (this.exporting) {
                return;
            }
            this.exporting = true;
            const hideMessage = this.$message.loading('导出Excel中...');
            try {
                let params = {
                        ...this.searchTables,
                        begin_date: this.searchTables.begin_date,
                        end_date: this.searchTables.end_date,
                        isCancel: 0
                    },
                    service = '';

                if (typeof params.organization_name !== 'string' || params.organization_name.length < 1) {
                    delete params.billsType;
                    delete params.organization_inner_sn;
                }
                delete params.organization_name;

                if (this.tabsActiveKey === this.tabPane(this.pageNameType, 0, 'value')) {
                    params.isCancel = 0;
                } else {
                    params.isCancel = 1;
                }

                if (this.is_PaymentOrder) {
                    params.payment_sn = this.searchTables.invoice_no;
                    params.payment_status = this.searchTables.invoice_status;

                    service = financePaymentExportAPI;
                } else {
                    params.receive_sn = this.searchTables.invoice_no;
                    params.receive_status = this.searchTables.invoice_status;

                    service = financeReceiveExportAPI;
                }
                delete params.invoice_no;
                delete params.invoice_status;

                const { data, headers } = await service(params);
                const contentDisposition = headers['content-disposition'] || '';
                const matchedFilenames = contentDisposition.match(/fileName=(.*)/);
                const filename =
                    matchedFilenames.length > 1
                        ? decodeURIComponent(matchedFilenames[1])
                        : `${moment().format('YYYY-MM-DD')}.xlsx`;
                this.$Utils.download(data, filename);
            } catch (error) {
                console.error(error);
            } finally {
                this.exporting = false;
                hideMessage();
            }
        },

        // 相关机构
        openChooseTab() {
            this.$nextTick(() => {
                if (this.$refs.organization) {
                    this.$refs.organization.initPageFn();
                }
            });
        },
        getOrgDataFn(orgData) {
            const {
                billsType,
                organization_name: organizationName,
                organization_inner_sn: organizationInnerSn
            } = orgData;
            this.searchTables.billsType = billsType;
            this.searchTables.organization_name = organizationName;
            this.searchTables.organization_inner_sn = organizationInnerSn;
        },

        groupBtnFn(action) {
            this.btnName = action;

            switch (action) {
                case 'add':
                    // 点击新增时，自动将tab选项卡切换至第一项，以防止新增完成后，新增的记录添加至错误的组件
                    this.tabsActiveKey = this.tabPane(this.pageNameType, 0, 'value');
                    this.isDetailModalOpen = true;
                    break;
                case 'edit':
                    if (this.rowData && Object.keys(this.rowData).length) {
                        this.isDetailModalOpen = true;
                    } else {
                        this.$warning({
                            title: '操作提示',
                            content: `请先选中某一行再进行该操作`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                    }
                    break;
                case 'del':
                case 'pay':
                case 'recorded':
                case 'export-excel':
                    this.$refs[this.tabPane(this.pageNameType, 0, 'value')].groupBtnFn(action);
                    break;
            }
        },
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        searchBtnFn() {
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                this.searchData = {
                    ...this.searchTables
                };
            } else {
                this.searchData = {
                    ...this.searchTables,
                    begin_date: '',
                    end_date: '',
                    invoice_status: constantAll,
                    billsType: '',
                    organization_name: '',
                    organization_inner_sn: ''
                };
            }
            this.initTableFn();
        },
        initTableFn() {
            this[`is${this.tabsActiveKey}Refresh`] = true;
            let timer = setTimeout(() => {
                clearTimeout(timer);
                this[`is${this.tabsActiveKey}Refresh`] = false;
            }, 500);
        },
        changeRangePickerFn(_, dateStrings) {
            if (dateStrings && dateStrings.length) {
                this.searchTables.begin_date = dateStrings[0];
                this.searchTables.end_date = dateStrings[1];
            }
        },
        changeDetailModalIsOpenFn(isOpen) {
            this.isDetailModalOpen = isOpen;
        },
        saveDetailModalFn(info) {
            // 包含返回的明细
            this.changeDetailModalIsOpenFn(false);
            this.tableDetailCallbackData = info;
        },
        getRowDataFn(row) {
            this.rowData = row;
        },
        lookDetailFn(row) {
            this.groupBtnFn('edit');

            this.rowData = row;
        },
        initDataFn() {
            this.tabsActiveKey = this.is_PaymentOrder ? paymentTabFinance[0].value : receiptTabFinance[0].value;
            this.searchTables.invoice_status = (this.invoiceStatusData && this.invoiceStatusData[0].key) || '';
            this.searchData = {
                ...this.searchTables
            };
        }
    },
    mixins: [tabPane],
    computed: {
        is_PaymentOrder() {
            // 是否是付款单 | 收款单
            return this.pageNameType === pagePaymentAttachmentType['PAYMENTORDER'].value;
        },
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 214 : 154,
                pager = 52;
            return clientHeight - topHeight - pager;
        },
        invoiceStatusData() {
            const invoiceState = this.is_PaymentOrder ? paymentInvoiceState : receiveInvoiceState;
            let datas = [];
            for (let key in invoiceState) {
                datas.push({
                    title: invoiceState[key],
                    key: key,
                    value: key
                });
            }
            return datas;
        }
    },
    mounted() {
        this.initDataFn();
    }
};
</script>
