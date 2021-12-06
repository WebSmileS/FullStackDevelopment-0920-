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
                                :disabled="deleteButtonDisabled"
                                type="danger"
                                size="small"
                                @click="groupBtnFn('del')"
                            >
                                删除
                            </a-button>
                        </li>
                        <li>
                            <a-button
                                :disabled="aduitButtonDisabled"
                                type="primary"
                                size="small"
                                @click="groupBtnFn('audit')"
                            >
                                送审
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
                        size="small"
                        v-model="searchTables.invoice_no"
                    />
                </a-col>
                <a-col :span="5" class="form-wrap">
                    <span class="form-label">状态</span>
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
                <a-tab-pane :tab="tabPane(0)" style="padding: 8px" :key="tabPane(0, 'value')">
                    <table-list
                        :isRefresh="$data[`is${tabPane(0, 'value')}Refresh`]"
                        :searchData="searchData"
                        :ref="tabPane(0, 'value')"
                        :refName="tabPane(0, 'value')"
                        :scrollYNumber="scrollYNumber"
                        :btnName="btnName"
                        :tableDetailCallbackData="tableDetailCallbackData"
                        @getRowDataFn="getRowDataFn"
                        @lookDetailFn="lookDetailFn"
                    ></table-list>
                </a-tab-pane>
                <a-tab-pane :tab="tabPane(1)" style="padding: 8px" :key="tabPane(1, 'value')">
                    <table-list
                        :isRefresh="$data[`is${tabPane(1, 'value')}Refresh`]"
                        :searchData="searchData"
                        :ref="tabPane(1, 'value')"
                        :refName="tabPane(1, 'value')"
                        :scrollYNumber="scrollYNumber"
                        :btnName="btnName"
                        @getRowDataFn="getRowDataFn"
                        @lookDetailFn="lookDetailFn"
                    ></table-list>
                </a-tab-pane>
            </a-tabs>
        </div>

        <!-- 详情区域 -->
        <table-detail
            :btnName="btnName"
            :isOpen="isDetailModalOpen"
            :rowData="rowData"
            @closeDetailModal="changeDetailModalIsOpenFn(false)"
            @saveDetailModal="saveDetailModalFn"
        ></table-detail>

        <!-- 相关机构区域 -->
        <choose-org-tab ref="organization" @getOrgData="getOrgDataFn"></choose-org-tab>
    </div>
</template>

<script>
import { invoiceState, constantAll, systemAdvancePaymentTab } from '@/views/commonPage/invoiceManage/config/constants';
import chooseOrgTab from '@/views/commonPage/invoiceManage/components/organization';

import { tabPane } from '../mixins/tabPane.js';
import tableList from './tableList';
import tableDetail from './tableDetail';

export default {
    name: 'advancePayment-index', // 预付款 = 财务人员使用
    components: {
        tableList,
        tableDetail,
        chooseOrgTab
    },
    data() {
        return {
            [`is${systemAdvancePaymentTab[0].value}Refresh`]: false,
            [`is${systemAdvancePaymentTab[1].value}Refresh`]: false,
            rangePickerVal: '',
            searchTables: {
                begin_date: '',
                end_date: '',
                invoice_no: '',
                invoice_status: Object.keys(invoiceState)[0],
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
            isRefresh: false,
            btnName: '',
            isDetailModalOpen: false
        };
    },
    methods: {
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
                    this.tabsActiveKey = this.tabPane(0, 'value');
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
                case 'audit':
                    this.$refs[this.tabsActiveKey].groupBtnFn(action);
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
        }
    },
    mixins: [tabPane],
    computed: {
        contentHeight() {
            return this.$store.getters.contentHeight;
        },
        scrollYNumber() {
            let topHeight = this.hightSearchObj.isArrow ? 178 : 118;
            let pager = 52;
            return this.contentHeight - topHeight - pager;
        },
        invoiceStatusData() {
            let datas = [];
            for (let key in invoiceState) {
                datas.push({
                    title: invoiceState[key],
                    key: key,
                    value: key
                });
            }
            return datas;
        },
        deleteButtonDisabled() {
            return this.tabsActiveKey !== systemAdvancePaymentTab[0].value;
        },
        aduitButtonDisabled() {
            return this.tabsActiveKey !== systemAdvancePaymentTab[0].value;
        }
    }
};
</script>

<style lang="less" scoped></style>
