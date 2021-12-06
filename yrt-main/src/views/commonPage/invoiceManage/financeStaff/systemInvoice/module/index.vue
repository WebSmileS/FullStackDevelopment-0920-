<template>
    <div class="scoped scoped-white">
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :sm="22" :md="22" :lg="18" :xl="18" :xxl="15">
                    <ul class="btn-list-wrap">
                        <li>
                            <!-- 只有待审核选项卡的发票才可以审核 -->
                            <a-button
                                :disabled="auditButtonDisabled"
                                type="primary"
                                size="small"
                                @click="groupBtnFn('audit')"
                            >
                                审核
                            </a-button>
                            <!-- 只有待审核选项卡和未核销选项卡的发票才可以作废 -->
                            <a-button
                                :disabled="cancelButtonDisabled"
                                type="danger"
                                size="small"
                                @click="groupBtnFn('cancel')"
                            >
                                作废
                            </a-button>
                        </li>
                        <li>
                            <!-- 注意：此处导出的内容是除草稿状态以外的所有状态发票清单，且不包含发票明细 -->
                            <a-button type="primary" size="small" @click="financeInvoiceExportAPIFn">
                                导出Excel
                            </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :sm="4" :md="2" :lg="6" :xl="6" :xxl="9">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-group style="width: auto" v-show="!hightSearchObj.isArrow" compact>
                            <a-select style="width: 120px" v-model="searchTables.system_invoice_type">
                                <a-select-option
                                    v-for="option in systemInvoiceTypeData"
                                    :key="option.key"
                                    :value="option.value"
                                >
                                    {{ option.title }}
                                </a-select-option>
                            </a-select>
                            <a-input-search
                                style="width: 240px"
                                v-model="searchTables.invoice_no"
                                @search="searchBtnFn"
                                placeholder="发票号"
                                size="small"
                                enterButton
                                class="search"
                            />
                        </a-input-group>
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
                    <span class="form-label">发票号</span>
                    <a-input
                        type="text"
                        placeholder=""
                        class="form-input"
                        style="height: 24px"
                        v-model="searchTables.invoice_no"
                    />
                </a-col>
                <a-col :span="5" class="form-wrap">
                    <span class="form-label">发票类型</span>
                    <a-tree-select
                        treeNodeFilterProp="title"
                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                        placeholder=""
                        treeDefaultExpandAll
                        class="form-input"
                        size="small"
                        :treeData="systemInvoiceTypeData"
                        v-model="searchTables.system_invoice_type"
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
                <a-tab-pane :tab="tabPane(0)" style="padding: 8px" :key="tabPane(0, 'value')">
                    <table-list
                        v-show="tabsActiveKey === tabPane(0, 'value')"
                        :refName="tabPane(0, 'value')"
                        :isRefresh="isPENDINGRefresh"
                        :scrollYNumber="scrollYNumber"
                        :searchData="searchData"
                        :ref="tabPane(0, 'value')"
                        :isInbound="is_Inbound"
                        :btnName="btnName"
                        :tableDetailCallbackData="tableDetailCallbackData"
                        @getRowDataFn="getRowDataFn"
                        @lookDetailFn="lookDetailFn"
                    ></table-list>
                </a-tab-pane>
                <a-tab-pane :tab="tabPane(1)" style="padding: 8px" :key="tabPane(1, 'value')">
                    <table-list
                        v-show="tabsActiveKey === tabPane(1, 'value')"
                        :refName="tabPane(1, 'value')"
                        :isRefresh="isNOT_WRITTEN_OFFRefresh"
                        :scrollYNumber="scrollYNumber"
                        :searchData="searchData"
                        :ref="tabPane(1, 'value')"
                        :isInbound="is_Inbound"
                        :btnName="btnName"
                        :tableDetailCallbackData="tableDetailCallbackData"
                        @getRowDataFn="getRowDataFn"
                        @lookDetailFn="lookDetailFn"
                    ></table-list>
                </a-tab-pane>
                <a-tab-pane :tab="tabPane(2)" style="padding: 8px" :key="tabPane(2, 'value')">
                    <table-list
                        v-show="tabsActiveKey === tabPane(2, 'value')"
                        :refName="tabPane(2, 'value')"
                        :isRefresh="isWRITTEN_OFFRefresh"
                        :scrollYNumber="scrollYNumber"
                        :searchData="searchData"
                        :ref="tabPane(2, 'value')"
                        :isInbound="is_Inbound"
                        :btnName="btnName"
                        :tableDetailCallbackData="tableDetailCallbackData"
                        @getRowDataFn="getRowDataFn"
                        @lookDetailFn="lookDetailFn"
                    ></table-list>
                </a-tab-pane>
                <a-tab-pane :tab="tabPane(3)" style="padding: 8px" :key="tabPane(3, 'value')">
                    <table-list
                        v-show="tabsActiveKey === tabPane(3, 'value')"
                        :refName="tabPane(3, 'value')"
                        :isRefresh="isHAVE_BEEN_VOIDEDRefresh"
                        :scrollYNumber="scrollYNumber"
                        :searchData="searchData"
                        :ref="tabPane(3, 'value')"
                        :isInbound="is_Inbound"
                        :btnName="btnName"
                        :tableDetailCallbackData="tableDetailCallbackData"
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
            :btnName="btnName"
            :isOpen="isDetailModalOpen"
            :rowData="rowData"
            :isInbound="is_Inbound"
            :tabsActiveKey="tabsActiveKey"
            :isShowCancel="tabsActiveKey === tabPane(3, 'value')"
            @closeDetailModal="changeDetailModalIsOpenFn"
            @saveDetailModal="saveDetailModalFn"
        ></table-detail>

        <!-- 相关机构区域 -->
        <choose-org-tab ref="organization" :isInbound="is_Inbound" @getOrgData="getOrgDataFn"></choose-org-tab>
    </div>
</template>

<script>
import { pageType, systemInvoiceTabFinance } from '@/views/commonPage/invoiceManage/config/constants';

import chooseOrgTab from '@/views/commonPage/invoiceManage/components/organization';

import { tabPane } from '../mixins/tabPane.js';
import tableList from '@/views/commonPage/invoiceManage/financeStaff/systemInvoice/module/tableList';
import tableDetail from '@/views/commonPage/invoiceManage/financeStaff/systemInvoice/module/tableDetail';

import { financeInvoiceExportAPI } from '@/service/pageAjax';

import moment from 'moment';

export default {
    name: 'inboundSystemInvoiceFinance-index', // 入库发票|出库发票 = 财务人员使用
    components: {
        tableList,
        tableDetail,
        chooseOrgTab
    },
    data() {
        return {
            tabsActiveKey: systemInvoiceTabFinance[0].value,
            isPENDINGRefresh: false,
            isNOT_WRITTEN_OFFRefresh: false,
            isWRITTEN_OFFRefresh: false,
            isHAVE_BEEN_VOIDEDRefresh: false,
            rangePickerVal: '',
            searchTables: {
                begin_date: '',
                end_date: '',
                invoice_no: '',
                system_invoice_type: Object.keys(pageType)[0],
                billsType: '',
                organization_inner_sn: '',
                organization_name: ''
            },
            searchData: {},
            organizationData: [],
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            tableDetailCallbackData: [],
            btnName: '',
            isDetailModalOpen: false,
            is_Inbound: true, // 默认是true-入库发票 | false-出库发票
            rowData: {},
            exporting: false // 导出Excel标识
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
                const params = {
                    ...this.searchTables,

                    // 发票类型 默认0-入库发票 1-出库发票
                    system_invoice_type: this.searchTables.system_invoice_type === pageType.INBOUND.value ? 0 : 1
                };
                if (typeof params.organization_name !== 'string' || params.organization_name.length < 1) {
                    delete params.billsType;
                    delete params.organization_inner_sn;
                }
                delete params.organization_name;
                const { data, headers } = await financeInvoiceExportAPI(params);

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

            if (action === 'audit' || action === 'cancel' || action === 'edit') {
                if (this.rowData && Object.keys(this.rowData).length <= 0) {
                    this.$message.warning('请先选中一张发票再进行该操作');
                    return;
                }
                this.isDetailModalOpen = true;
            }

            // this.$refs[this.tabsActiveKey].groupBtnFn(action, this.rowData)
        },
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        searchBtnFn() {
            if (this.searchTables.system_invoice_type === pageType['INBOUND'].value) {
                this.is_Inbound = true;
            } else {
                this.is_Inbound = false;
            }
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
                    invoice_status: this.tabsActiveKey === 'HAVE_BEEN_VOIDED' ? undefined : this.tabsActiveKey,
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
        changeDetailModalIsOpenFn(isOpen, isfreshParentTableList) {
            this.isDetailModalOpen = isOpen;

            // 刷新父级表格数据
            if (isfreshParentTableList) {
                this.searchBtnFn();
            }
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
        }
    },
    mixins: [tabPane],
    computed: {
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 214 : 154,
                pager = 52;
            return clientHeight - topHeight - pager;
        },
        systemInvoiceTypeData() {
            let datas = [];
            for (let key in pageType) {
                let item = pageType[key];
                datas.push({
                    title: `${item.label}发票`,
                    key: item.value,
                    value: item.value
                });
            }
            return datas;
        },
        // 审核按钮禁用状态
        auditButtonDisabled() {
            if (this.tabsActiveKey === this.tabPane(0, 'value')) {
                return false;
            }
            return true;
        },
        // 作废按钮禁用状态
        cancelButtonDisabled() {
            if (this.tabsActiveKey === this.tabPane(2, 'value') || this.tabsActiveKey === this.tabPane(3, 'value')) {
                return true;
            }
            if (this.rowData && this.rowData.invoice_status === 'PARTIAL_WRITE_OFF') {
                return true;
            }
            return false;
        }
    },
    created() {
        this.searchData = { ...this.searchTables };
    }
};
</script>
