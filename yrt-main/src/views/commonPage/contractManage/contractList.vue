<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button type="primary" size="small" @click="openLook"> 查看 </a-button>
                            <!--<a-button-->
                            <!--type="primary"-->
                            <!--size="small"-->
                            <!--v-if="currentTrObj.status != 5 && currentTrObj.status != 6"-->
                            <!--&gt;-->
                            <!--变更-->
                            <!--</a-button>-->
                            <a-button type="primary" size="small" @click="openStop" v-if="isShowStopBtn">
                                中止
                            </a-button>
                            <a-button type="primary" size="small" @click="openCreateTip"> 补录 </a-button>
                            <a-button type="primary" size="small" @click="productSearchFn"> 产品搜索 </a-button>
                            <!-- <a-button
                                type="danger"
                                size="small"
                                @click="openDel"
                            >
                                删除
                            </a-button> -->
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            placeholder="名称"
                            v-model="searchData.name"
                            size="small"
                            enterButton
                            class="search"
                            @search="initPage"
                        />
                        <div class="hight-search-text" @click="hightSearchIconFn">
                            <!--<span>高级搜索</span>-->
                            <a-tooltip placement="left" title="高级搜索">
                                <a-icon :type="hightSearchObj.isArrow ? 'up-circle' : 'down-circle'" />
                            </a-tooltip>
                        </div>
                    </div>
                </a-col>
            </a-row>
            <a-row v-show="hightSearchObj.isArrow" class="hight-search-wrap">
                <a-col :span="24" class="col-list">
                    <!-- 高级搜索 -->
                    <ul class="form">
                        <li>
                            <span class="label">合同编号</span>
                            <a-input v-model="searchData.contract_sn" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">名称</span>
                            <a-input v-model="searchData.name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">甲方</span>
                            <a-input v-model="searchData.partA_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">乙方</span>
                            <a-input v-model="searchData.partB_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <a-button @click="loadTable(chooseTrObj)" type="primary"> 搜索 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab切换区域开始 -->
        <div
            :class="hightSearchObj.isArrow ? 'top136' : 'top56'"
            class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
        >
            <!-- 表格头部 -->
            <a-tabs @change="changeTabsFn" type="card">
                <a-tab-pane
                    v-if="nowOrgType !== 1"
                    tab="医院"
                    style="padding: 8px"
                    :forceRender="true"
                    :key="systemTypes[0]"
                >
                    <vxe-grid
                        ref="hosTable"
                        size="small"
                        :columns="columns"
                        :height="(hightSearchObj.isArrow ? scrollYNumber - 60 : scrollYNumber) + 43"
                        resizable
                        auto-resize
                        border
                        highlight-current-row
                        @cell-click="cellClickFn"
                        @cell-dblclick="cellDblclickFn"
                        align="center"
                        :data="hospitalDatas"
                        :loading="hospitalPagination.loading"
                        :start-index="(hospitalPagination.current - 1) * hospitalPagination.pageSize"
                    >
                    </vxe-grid>
                    <vxe-pager
                        :loading="hospitalPagination.loading"
                        :current-page="hospitalPagination.current"
                        :page-size="hospitalPagination.pageSize"
                        :page-sizes="hospitalPagination.pageSizes"
                        :total="hospitalPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changePaginationFn"
                    >
                    </vxe-pager>
                </a-tab-pane>
                <a-tab-pane tab="经销商" :key="systemTypes[1]" style="padding: 8px" :forceRender="true">
                    <vxe-grid
                        ref="deaTable"
                        size="small"
                        :columns="columns"
                        :height="(hightSearchObj.isArrow ? scrollYNumber - 60 : scrollYNumber) + 43"
                        resizable
                        auto-resize
                        border
                        highlight-current-row
                        @cell-click="cellClickFn"
                        @cell-dblclick="cellDblclickFn"
                        align="center"
                        :data="dealersDatas"
                        :loading="dealersPagination.loading"
                        :start-index="(dealersPagination.current - 1) * dealersPagination.pageSize"
                    >
                    </vxe-grid>
                    <vxe-pager
                        :loading="dealersPagination.loading"
                        :current-page="dealersPagination.current"
                        :page-size="dealersPagination.pageSize"
                        :page-sizes="dealersPagination.pageSizes"
                        :total="dealersPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changePaginationFn"
                    >
                    </vxe-pager>
                </a-tab-pane>
                <a-tab-pane
                    tab="厂商"
                    style="padding: 8px"
                    :forceRender="true"
                    v-if="nowOrgType !== 3"
                    :key="systemTypes[2]"
                >
                    <vxe-grid
                        ref="venTable"
                        size="small"
                        :columns="columns"
                        :height="(hightSearchObj.isArrow ? scrollYNumber - 60 : scrollYNumber) + 43"
                        resizable
                        auto-resize
                        border
                        highlight-current-row
                        @cell-click="cellClickFn"
                        @cell-dblclick="cellDblclickFn"
                        align="center"
                        :data="vendorDatas"
                        :loading="vendorPagination.loading"
                        :start-index="(vendorPagination.current - 1) * vendorPagination.pageSize"
                    >
                    </vxe-grid>
                    <vxe-pager
                        :loading="vendorPagination.loading"
                        :current-page="vendorPagination.current"
                        :page-size="vendorPagination.pageSize"
                        :page-sizes="vendorPagination.pageSizes"
                        :total="vendorPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changePaginationFn"
                    >
                    </vxe-pager>
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
                        @click="handleRefreshPage"
                    />
                    <!--刷新按钮区域-->
                </a-tooltip>
            </a-tabs>
            <!--表格区域-->
        </div>
        <!-- tab切换区域结束 -->
        <!--查看-->
        <a-modal
            title="查看"
            :visible="approveMd.alert"
            :width="1200"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="approveMd.alert = false"
            class="modal-form-input-scoped global-drag approve-contract-md"
        >
            <div v-globalDrag="{ el: 'approve-contract-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ approveMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <div style="height: 32px"></div>
            </template>
            <approve-detail
                ref="approveDraftMd"
                @done="saveContractFn"
                @cancel="approveMd.alert = false"
                :partType="approveType"
                :editObj="editObj"
            ></approve-detail>
        </a-modal>
        <!--新增提示-->
        <a-modal
            title=""
            :visible="createTip"
            :width="400"
            :maskClosable="false"
            :centered="true"
            @cancel="createTip = false"
            class="modal-form-input-scoped global-drag create_tip"
        >
            <div v-globalDrag="{ el: 'create_tip' }" class="modal-header-wrap">
                <h6> </h6>
            </div>
            <template slot="footer">
                <a-button type="primary" @click="openCreate">确定</a-button>
            </template>
            <div class="create-tip-box">
                <div class="tip-q">即将创建合同草稿，请问您是甲方，还是乙方？</div>
                <div class="radio-box">
                    <a-radio-group v-model="createTipRadio">
                        <a-radio :value="1">甲方</a-radio>
                        <a-radio :value="2">乙方</a-radio>
                    </a-radio-group>
                </div>
            </div>
        </a-modal>
        <!--补录合同-->
        <a-modal
            title="补录合同"
            :visible="createMd.alert"
            :width="1200"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="createMd.alert = false"
            class="modal-form-input-scoped global-drag create-contract-md"
        >
            <div v-globalDrag="{ el: 'create-contract-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ createMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="createMd.alert = false">取消</a-button>
                <a-button :loading="createMd.loading" type="primary" @click="sendCreateBack">保存</a-button>
            </template>
            <create-draft
                ref="createDraftMd"
                @returnData="saveContractFn"
                :isCloseCreateDraft="createMd.alert"
                :partType="createTipRadio"
            ></create-draft>
        </a-modal>
        <a-modal
            title="产品搜索"
            :visible="productSearchObj.alert"
            :width="1200"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="productSearchObj.alert = false"
            class="modal-form-input-scoped global-drag product-search-contract-md"
        >
            <div v-globalDrag="{ el: 'product-search-contract-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ productSearchObj.title }}
                </h6>
            </div>
            <template slot="footer">
                <div style="height: 32px">
                    <a-button @click="productSearchObj.alert = false">关闭</a-button>
                </div>
            </template>
            <product-search-contract-detail ref="productSearchContract"></product-search-contract-detail>
        </a-modal>
    </div>
</template>

<script>
import { contractListAPI, contractStopAPI, contractMakeupAPI } from '@/service/pageAjax';

import approveDetail from './module/approveDetail';
import createDraft from './module/createDraft';
import productSearchContractDetail from './module/productSearchContractDetail';

export default {
    name: 'contractList',
    components: {
        approveDetail,
        createDraft,
        productSearchContractDetail
    },
    data() {
        return {
            tabsActiveKey: 1,
            currentTrObj: {},
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            searchData: {
                // 合同名称
                name: '',
                // 合同编号
                contract_sn: '',
                // 甲方
                partA_name: '',
                // 乙方
                partB_name: ''
            },
            systemTypes: [1, 2, 3], // 0 平台、 1医院 、 2 经销商、 3 厂商
            columns: [
                {
                    title: '合同编号',
                    dataIndex: 'contract_sn',
                    minWidth: '150',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.contract_sn];
                        }
                    }
                },
                {
                    title: '合同名称',
                    dataIndex: 'name',
                    minWidth: '150',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.name];
                        }
                    }
                },
                {
                    title: '甲方',
                    dataIndex: 'partA_name',
                    minWidth: '150',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.partA_name];
                        }
                    }
                },
                {
                    title: '乙方',
                    dataIndex: 'partB_name',
                    minWidth: '150',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.partB_name];
                        }
                    }
                },
                {
                    title: '开始日期',
                    dataIndex: 'begin_date',
                    minWidth: '100',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.begin_date];
                        }
                    }
                },
                {
                    title: '截止日期',
                    dataIndex: 'end_date',
                    minWidth: '100',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.end_date];
                        }
                    }
                },
                {
                    title: '销售代表',
                    dataIndex: 'employee_name',
                    minWidth: '100',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.employee_name];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    minWidth: '100',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [this.contractStatus[row.status]];
                        }
                    }
                }
            ],
            dealersDatas: [], // 经销商主体数据
            hospitalDatas: [], // 医院主体数据
            vendorDatas: [], // 厂商主体数据
            // 医院 - 表格分页
            hospitalPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            dealersPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            // 厂商 - 表格分页
            vendorPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            contractStatus: [
                '草稿',
                '审批进行中',
                '审批未通过',
                '审批通过',
                '有效履行中',
                '自然终止',
                '提前终止',
                '超期履行'
            ],
            editObj: {},
            approveMd: {
                title: `${this.$route.meta.title} - 详情`,
                alert: false,
                loading: false
            },
            nowOrgType: 2, // 权限 1：医院 2：经销商 3：产商
            createTipRadio: 1,
            createTip: false,
            createMd: {
                title: `${this.$route.meta.title} - 补录`,
                alert: false,
                loading: false
            },
            isShowStopBtn: false, // 是否显示中止按钮
            productSearchObj: {
                title: `${this.$route.meta.title} - 产品搜索`,
                alert: false,
                loading: false
            }
        };
    },
    watch: {
        'currentTrObj.status': function (newStatus) {
            if (newStatus !== 5 && newStatus !== 0 && newStatus !== 6) {
                this.isShowStopBtn = true;
            } else {
                this.isShowStopBtn = false;
            }
        }
    },
    methods: {
        initPage() {
            switch (parseInt(this.tabsActiveKey)) {
                case 0:
                    break;
                case 1:
                    this.hospitalPagination.current = 1;
                    break;
                case 2:
                    this.dealersPagination.current = 1;
                    break;
                case 3:
                    this.vendorPagination.current = 1;
                    break;
            }
            this.loadTable();
        },
        loadTable() {
            if (this.$Utils.checkInputValFn(this.searchData.name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (this.nowOrgType === 1) {
                // 医院
                this.loadTableDel();
                this.loadTableVen();
            } else if (this.nowOrgType === 3) {
                // 厂商
                this.loadTableHos();
                this.loadTableDel();
            } else {
                this.loadTableHos();
                this.loadTableDel();
                this.loadTableVen();
            }
        },
        async loadTableHos() {
            // 加载医院列表
            this.hospitalPagination.loading = true;
            let params = this.searchData;
            params.partA = this.$cookie.get('userbelong');
            params.pageSize = this.hospitalPagination.pageSize;
            params.pageNum = this.hospitalPagination.current;

            // 排序
            params.orderByColumn = 'contract_inner_sn';
            params.isAsc = 'desc';

            await contractListAPI(1, { params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let data = res.rows;
                    this.hospitalPagination.loading = false;
                    let total = res.total;
                    this.hospitalPagination = {
                        ...this.hospitalPagination,
                        total
                    };
                    data.forEach((item) => {
                        item.isActive = '';
                        item.key = item.contract_type + '' + item.contract_inner_sn + '' + item.partA + '' + item.partB;
                    });
                    this.hospitalDatas = data;
                    if (this.hospitalDatas.length > 0) {
                        this.$refs.hosTable.setCurrentRow(data[0]);
                        this.$refs.hosTable.refreshColumn();
                    }
                }
            });
        },
        async loadTableDel() {
            // 加载经销商列表
            this.dealersPagination.loading = true;
            let params = this.searchData;
            params.partA = this.$cookie.get('userbelong');
            params.pageSize = this.dealersPagination.pageSize;
            params.pageNum = this.dealersPagination.current;

            // 排序
            params.orderByColumn = 'contract_inner_sn';
            params.isAsc = 'desc';

            await contractListAPI(2, { params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let data = res.rows;
                    this.dealersPagination.loading = false;
                    let total = res.total;
                    this.dealersPagination = {
                        ...this.dealersPagination,
                        total
                    };
                    data.forEach((item) => {
                        item.isActive = '';
                        item.key = item.contract_type + '' + item.contract_inner_sn + '' + item.partA + '' + item.partB;
                    });
                    this.dealersDatas = data;
                    if (this.dealersDatas.length > 0) {
                        this.$refs.deaTable.setCurrentRow(data[0]);
                        this.$refs.deaTable.refreshColumn();
                    }
                }
            });
        },
        async loadTableVen() {
            // 加载经销商列表
            this.vendorPagination.loading = true;
            let params = this.searchData;
            params.partA = this.$cookie.get('userbelong');
            params.pageSize = this.vendorPagination.pageSize;
            params.pageNum = this.vendorPagination.current;

            // 排序
            params.orderByColumn = 'contract_inner_sn';
            params.isAsc = 'desc';

            await contractListAPI(3, { params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let data = res.rows;
                    this.vendorPagination.loading = false;
                    let total = res.total;
                    this.vendorPagination = {
                        ...this.vendorPagination,
                        total
                    };
                    data.forEach((item) => {
                        item.isActive = '';
                        item.key = item.contract_type + '' + item.contract_inner_sn + '' + item.partA + '' + item.partB;
                    });
                    this.vendorDatas = data;
                    if (this.vendorDatas.length > 0) {
                        this.$refs.venTable.setCurrentRow(data[0]);
                        this.$refs.venTable.refreshColumn();
                    }
                }
            });
        },
        // 设置行属性 —— 点击每一行的事件
        cellClickFn(row) {
            // 单击行
            let obj = row.row || row;
            this.currentTrObj = obj;
            this.editObj = this.currentTrObj;
            this.clearSelect();
        },
        cellDblclickFn({ row }) {
            let obj = row.row || row;
            this.currentTrObj = obj;
            this.editObj = this.currentTrObj;
            this.clearSelect();
            this.openLook();
        },
        changeTabsFn(activeKey) {
            this.tabsActiveKey = parseFloat(activeKey);
        },
        /** 头部按钮方法区 **/
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        clearSelect() {
            const type = this.tabsActiveKey;
            if (type === 1) {
                if (this.$refs.deaTable) {
                    this.$refs.deaTable.clearCurrentRow();
                }
                if (this.$refs.venTable) {
                    this.$refs.venTable.clearCurrentRow();
                }
            }
            if (type === 2) {
                if (this.$refs.hosTable) {
                    this.$refs.hosTable.clearCurrentRow();
                }
                if (this.$refs.venTable) {
                    this.$refs.venTable.clearCurrentRow();
                }
            }
            if (type === 3) {
                if (this.$refs.hosTable) {
                    this.$refs.hosTable.clearCurrentRow();
                }
                if (this.$refs.deaTable) {
                    this.$refs.deaTable.clearCurrentRow();
                }
            }
        },
        // 页码改变的回调函数
        changePaginationFn({ currentPage, pageSize }) {
            switch (parseInt(this.tabsActiveKey)) {
                case 0:
                    break;
                case 1:
                    this.hospitalPagination.current = currentPage;
                    this.hospitalPagination.pageSize = pageSize;
                    this.hospitalPagination.loading = true;
                    this.loadTableHos();
                    break;
                case 2:
                    this.dealersPagination.current = currentPage;
                    this.dealersPagination.pageSize = pageSize;
                    this.dealersPagination.loading = true;
                    this.loadTableDel();
                    break;
                case 3:
                    this.vendorPagination.current = currentPage;
                    this.vendorPagination.pageSize = pageSize;
                    this.vendorPagination.loading = true;
                    this.loadTableVen();
                    break;
            }
        },
        /** 头部按钮 **/
        openLook() {
            // 打开查看
            let target = {};
            switch (this.tabsActiveKey) {
                case 1: // 医院
                    target = this.$refs.hosTable.getCurrentRecord();
                    break;
                case 2: // 经销商
                    target = this.$refs.deaTable.getCurrentRecord();
                    break;
                case 3: // 厂商
                    target = this.$refs.venTable.getCurrentRecord();
                    break;
                default:
            }
            if (!target || !target.key) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            target.c_type = 'look'; // 查看模式
            this.editObj = target;
            this.$Utils.globalDragCenterFn('approve-contract-md');
            this.approveMd.alert = true;
            // 因为手动调用子组件的pageRefresh是脱离正常的数据流的，editObj作为子组件的props，其值更新传递至子组件时，pageRefresh已经被执行了，数据不同步
            // 所以需要将pageRefresh延迟至下一个tick执行。
            // 在不大量改动子组件逻辑的情况下，只能暂时使用此方法实现功能
            // 更好的实现逻辑应该是通过数据流（由上至下）控制子组件的更新（符合vue组件思想）
            // 即使需要通过调用子组件方法的方式触发更新，此时也应该避免同时通过 props传值 和 方法调用 更新子组件状态，如此数据流才是单线、清晰、同步的
            if (this.$refs.approveDraftMd) {
                // 第一次打开模态框时，其还未被创建，对应的节点还未初始化，
                // 在这种情况下，下一个tick调用pageRefresh会与该组件自身的created中调用的pageRefresh形成重复调用
                this.$nextTick(() => {
                    if (this.$refs.approveDraftMd) {
                        // 单纯判断节点是否存在
                        this.$refs.approveDraftMd.pageRefresh('look');
                    }
                });
            }
        },
        openCreateTip() {
            // 打开新增提示
            if (this.nowOrgType === 1) {
                this.createTipRadio = 1;
                this.openCreate();
            }
            if (this.nowOrgType === 2) {
                this.$Utils.globalDragCenterFn('create_tip');
                this.createTip = true;
            }
            if (this.nowOrgType === 3) {
                this.createTipRadio = 2;
                this.openCreate();
            }
        },
        openCreate() {
            // 打开新增
            this.$Utils.globalDragCenterFn('create-contract-md');
            this.createTip = false;
            this.createMd.alert = true;
            if (this.$refs.createDraftMd) {
                this.$refs.createDraftMd.pageRefresh();
            }
        },
        sendCreateBack() {
            // 发送回调请求
            this.$refs.createDraftMd.returnData();
            // this.createMd.alert = false
        },
        getNowDate() {
            let date = new Date();
            let seperator1 = '-';
            let year = date.getFullYear();
            let month = date.getMonth() + 1;
            let strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = '0' + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = '0' + strDate;
            }
            let currentdate = year + seperator1 + month + seperator1 + strDate;
            return currentdate;
        },
        async saveContractFn(dataObj) {
            // 补录合同
            let beginDate = new Date(dataObj.begin_date.replace(/-/g, '/'));
            let endDate = new Date(dataObj.end_date.replace(/-/g, '/'));
            let nowDate = new Date(this.getNowDate().replace(/-/g, '/'));
            if (beginDate > nowDate) {
                this.$warning({
                    title: '操作提示',
                    content: '有效期开始日期不能大于当前日期',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (endDate < nowDate) {
                this.$warning({
                    title: '操作提示',
                    content: '有效期结束日期不能小于当前日期',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            await contractMakeupAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.createMd.alert = false;
                    this.loadTable();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        openStop() {
            // 打开终止
            if (!this.currentTrObj.key) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            const _this = this;
            _this.$confirm({
                title: '中止',
                content: '确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.stopContract();
                }
            });
        },
        async stopContract() {
            // 终止合同
            let params = {
                partA: this.currentTrObj.partA,
                partB: this.currentTrObj.partB,
                contract_inner_sn: this.currentTrObj.contract_inner_sn,
                contract_type: this.currentTrObj.contract_type
            };
            await contractStopAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const key = this.tabsActiveKey;
                    if (parseInt(this.nowOrgType) === 1) {
                        if (key === 1) {
                            this.loadTableDel();
                        }
                        if (key === 2) {
                            this.loadTableVen();
                        }
                    } else {
                        if (key === 1) {
                            this.loadTableHos();
                        }
                        if (key === 2) {
                            this.loadTableDel();
                        }
                        if (key === 3) {
                            this.loadTableVen();
                        }
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        openDel() {
            if (!this.currentTrObj.key) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
        },
        initSetTabsActiveKeyFn() {
            this.nowOrgType = parseFloat(this.$cookie.get('userSystemType'));
            if (this.nowOrgType === 1) {
                this.tabsActiveKey = 2;
            }
        },
        // 刷新页面
        handleRefreshPage() {
            this.initPage();
        },
        productSearchFn() {
            this.$Utils.globalDragCenterFn('product-search-contract-md');
            this.productSearchObj.alert = true;
            this.$nextTick(() => {
                if (this.$refs.productSearchContract) {
                    this.$refs.productSearchContract.searchDataFn();
                }
            });
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 253;
            return clientHeight - topHeight;
        }
    },
    created() {
        this.initSetTabsActiveKeyFn();
        this.loadTable();
    }
};
</script>

<style scoped lang="less">
.create-tip-box {
    height: 108px;
    padding-top: 24px;
    text-align: center;
    .tip-q {
        margin-top: 20px;
    }
    .radio-box {
        margin-top: 20px;
    }
}
</style>
