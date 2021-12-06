<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button type="primary" size="small" @click="openCreate" :disabled="isReply">
                                新增
                            </a-button>
                            <a-button v-if="isReply" type="primary" size="small" @click="openReply"> 回复 </a-button>
                            <a-button v-if="!isReply" type="primary" size="small" @click="openEdit"> 修改 </a-button>
                            <a-button type="danger" size="small" :disabled="isReply" @click="openDel"> 删除 </a-button>

                            <!-- 医院和经销商 -->
                            <a-button v-if="nowOrgType !== 3" type="primary" size="small" @click="handleOpenPriceModal">
                                价格查询
                            </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            placeholder="询价机构/单据编号"
                            size="small"
                            enterButton
                            class="search"
                            v-model="searchKey"
                            @search="handleSearch"
                        />
                    </div>
                </a-col>
            </a-row>
            <a-row v-show="hightSearchObj.isArrow" class="hight-search-wrap">
                <a-col :span="24" class="col-list">
                    <!-- 高级搜索 -->
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
            <a-tabs @change="changeTabsFn" :activeKey="tabsActiveKey" type="card">
                <a-tab-pane tab="询价回复" v-if="nowOrgType != 1" style="padding: 8px" :key="systemTypes[0]">
                    <vxe-grid
                        ref="hosTable"
                        size="small"
                        :columns="columns_hos"
                        :height="scrollYNumber + 43"
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
                    <div class="show_inquiry">
                        <a-checkbox @change="changeInquiry">显示全部</a-checkbox>
                    </div>
                </a-tab-pane>
                <a-tab-pane tab="经销商" style="padding: 8px" :key="systemTypes[1]" v-if="nowOrgType != 3">
                    <vxe-grid
                        ref="deaTable"
                        size="small"
                        :columns="columns_dea"
                        :height="scrollYNumber + 43"
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
                <a-tab-pane tab="厂商" style="padding: 8px" v-if="nowOrgType != 3" :key="systemTypes[2]">
                    <vxe-grid
                        ref="venTable"
                        size="small"
                        :columns="columns_ven"
                        :height="scrollYNumber + 43"
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
                        @click="RefreshTable"
                    />
                    <!--刷新按钮区域-->
                </a-tooltip>
            </a-tabs>
            <!--表格区域-->
        </div>
        <!-- tab切换区域结束 -->

        <a-modal
            title="新增"
            :visible="createMd.alert"
            :width="1140"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="cancelCt"
            class="modal-form-input-scoped global-drag create-inquiry-md"
        >
            <div v-globalDrag="{ el: 'create-inquiry-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ createMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <div style="height: 32px"></div>
            </template>
            <create-inquiry ref="enquiryListMd" @done="createDone" @cancel="createMd.alert = false"></create-inquiry>
        </a-modal>

        <a-modal
            title="修改"
            :visible="editMd.alert"
            :width="1140"
            okText="保存"
            :maskClosable="false"
            :mask="!priceMd.alert"
            :centered="true"
            @cancel="editMd.alert = false"
            class="modal-form-input-scoped global-drag edit-inquiry-md"
            :wrapClassName="priceMd.alert ? 'inquiry-edit-wrap' : ''"
        >
            <div v-globalDrag="{ el: 'edit-inquiry-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ editMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <div style="height: 32px"></div>
            </template>
            <edit-inquiry
                ref="enquiryListEditMd"
                @done="editDone"
                @cancel="editMd.alert = false"
                :editObj="editObj"
            ></edit-inquiry>
        </a-modal>

        <a-modal
            title="回复"
            :visible="replyMd.alert"
            :width="1140"
            okText="保存"
            :maskClosable="false"
            :mask="!priceMd.alert"
            :centered="true"
            @cancel="replyMd.alert = false"
            class="modal-form-input-scoped global-drag reply-inquiry-md"
            :wrapClassName="priceMd.alert ? 'inquiry-reply-wrap' : ''"
        >
            <div v-globalDrag="{ el: 'reply-inquiry-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ replyMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <div style="height: 32px"></div>
            </template>
            <reply-inquiry
                ref="replyListEditMd"
                @done="replyDone"
                @cancel="replyMd.alert = false"
                :editObj="editObj"
            ></reply-inquiry>
        </a-modal>

        <!-- 价格查询modal -->
        <a-modal
            title="价格"
            :visible="priceMd.alert"
            :width="1140"
            okText="保存"
            :centered="true"
            :maskClosable="false"
            @cancel="priceMd.alert = false"
            class="modal-form-input-scoped global-drag inquiry-price-md"
            wrapClassName="inquiry-price-wrap"
        >
            <div v-globalDrag="{ el: 'inquiry-price-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ priceMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <div style="height: 32px"></div>
            </template>
            <price :key="priceMdKey" @cancel="priceMd.alert = false"></price>
        </a-modal>
        <!-- 价格查询modal end -->
    </div>
</template>

<script>
import { inquiryDealerList, inquiryVendorList, inquiryUnreplyList, inquiryDelAPI } from '@/service/pageAjax';

import createInquiry from './module/createInquiry';
import editInquiry from './module/editInquiry';
import replyInquiry from './module/replyInquiry';
import price from './module/priceInquiry';

export default {
    name: 'enquiryList',
    components: {
        createInquiry,
        editInquiry,
        replyInquiry,
        price
    },
    data() {
        return {
            priceMdKey: 0,
            searchKey: '', // 搜索关键字 询价机构/单据编号
            isReply: false,
            isDraft: false,
            tabsActiveKey: 1,
            showInquiry: 0,
            currentTrObj: {},
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            searchTables: {
                registrant: '',
                social_credit_code: '',
                name: ''
            },
            systemTypes: [1, 2, 3], // 待回复 、 2 经销商、 3 厂商
            columns_hos: [
                {
                    title: '询价机构',
                    dataIndex: 'partA_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.partA_name];
                        }
                    }
                },
                {
                    title: '单据编号',
                    dataIndex: 'ilps_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ilps_sn];
                        }
                    }
                },
                {
                    title: '联系人',
                    dataIndex: 'contacts',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.contacts];
                        }
                    }
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.phone];
                        }
                    }
                },
                {
                    title: '询价时间',
                    dataIndex: 'ask_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ask_time];
                        }
                    }
                },
                {
                    title: '回复时间',
                    dataIndex: 'answer_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.answer_time];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '10%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [this.contractStatus[row.status]];
                        }
                    }
                }
            ],
            columns_dea: [
                {
                    title: '经销商',
                    dataIndex: 'partB_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.partB_name];
                        }
                    }
                },
                {
                    title: '单据编号',
                    dataIndex: 'ilps_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ilps_sn];
                        }
                    }
                },
                {
                    title: '联系人',
                    dataIndex: 'contacts',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.contacts];
                        }
                    }
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.phone];
                        }
                    }
                },
                {
                    title: '询价时间',
                    dataIndex: 'ask_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ask_time];
                        }
                    }
                },
                {
                    title: '回复时间',
                    dataIndex: 'answer_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.answer_time];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    minWidth: '10%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let text = row.status;
                            if (parseInt(text) === 2) {
                                return [this.contractStatus[parseInt(text) - 1]];
                            } else {
                                return [this.contractStatus[text]];
                            }
                        }
                    }
                }
            ],
            columns_ven: [
                {
                    title: '厂商',
                    dataIndex: 'partB_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.partB_name];
                        }
                    }
                },
                {
                    title: '单据编号',
                    dataIndex: 'ilps_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ilps_sn];
                        }
                    }
                },
                {
                    title: '联系人',
                    dataIndex: 'contacts',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.contacts];
                        }
                    }
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.phone];
                        }
                    }
                },
                {
                    title: '询价时间',
                    dataIndex: 'ask_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ask_time];
                        }
                    }
                },
                {
                    title: '回复时间',
                    dataIndex: 'answer_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.answer_time];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '10%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let text = row.status;
                            if (parseInt(text) === 2) {
                                return [this.contractStatus[parseInt(text) - 1]];
                            } else {
                                return [this.contractStatus[text]];
                            }
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
            contractStatus: ['询价草稿', '询价', '回复草稿', '已回复'],
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
                title: `询价 - 新增`,
                alert: false,
                loading: false
            },
            editMd: {
                title: `询价 - 修改`,
                alert: false,
                loading: false
            },
            replyMd: {
                title: `询价 - 回复`,
                alert: false,
                loading: false
            },
            priceMd: {
                title: `询价 - 价格`,
                alert: false,
                loading: false
            }
        };
    },
    methods: {
        // 打开价格查询modal
        handleOpenPriceModal() {
            this.priceMdKey = new Date().getTime(); // 刷新组件状态
            this.$Utils.globalDragCenterFn('inquiry-price-md');
            this.priceMd.alert = true;
        },
        // 点击搜索
        handleSearch() {
            this.hospitalPagination = {
                ...this.hospitalPagination,
                pageNum: 1,
                current: 1
            };
            this.dealersPagination = {
                ...this.dealersPagination,
                pageNum: 1,
                current: 1
            };
            this.vendorPagination = {
                ...this.vendorPagination,
                pageNum: 1,
                current: 1
            };
            this.loadTable();
        },
        RefreshTable() {
            this.hospitalPagination.current = 1;
            this.dealersPagination.current = 1;
            this.vendorPagination.current = 1;
            this.loadTable();
        },
        loadTable(data) {
            this.loadTableHos();
            this.loadTableDel(data);
            this.loadTableVen(data);
        },
        async loadTableHos() {
            // 加载回复列表
            this.hospitalPagination.loading = true;
            let params = {};
            let all = this.showInquiry;
            params.key = this.searchKey;
            params.pageSize = this.hospitalPagination.pageSize;
            params.pageNum = this.hospitalPagination.current;
            await inquiryUnreplyList(all, { params }).then((res) => {
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
                        item.key = '1' + item.il_type + '' + item.ilps_inner_sn + '' + item.partA + '' + item.partB;
                    });
                    this.hospitalDatas = data;
                }
            });
        },
        async loadTableDel(chooseObj) {
            console.log(chooseObj);
            // 加载经销商列表
            this.dealersPagination.loading = true;
            let params = {};
            params.key = this.searchKey;
            params.pageSize = this.dealersPagination.pageSize;
            params.pageNum = this.dealersPagination.current;
            if (chooseObj) {
                let pn = parseInt(this.dealersPagination.total / params.pageSize);
                params.pageNum = pn + 1;
                this.dealersPagination.pageNum = pn + 1;
                this.dealersPagination.current = pn + 1;
            }
            await inquiryDealerList({ params }).then((res) => {
                console.log(params);
                console.log(res);
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
                        item.key = item.il_type + '' + item.ilps_inner_sn + '' + item.partA + '' + item.partB;
                    });
                    this.dealersDatas = data;
                    console.log(this.dealersDatas);
                    if (this.dealersDatas.length > 0) {
                        if (chooseObj) {
                            // 选中新增/编辑的项
                            this.currentTrObj = this.dealersDatas[this.dealersDatas.length - 1];
                        } else {
                            this.currentTrObj = this.dealersDatas[0]; // 默认选中第一
                        }
                        this.editObj = this.currentTrObj;
                        console.log(this.editObj);
                    }
                }
            });
        },
        async loadTableVen(chooseObj) {
            // 加载厂商列表
            this.vendorPagination.loading = true;
            let params = {};
            params.key = this.searchKey;
            params.pageSize = this.vendorPagination.pageSize;
            params.pageNum = this.vendorPagination.current;

            // 排序
            params.orderByColumn = 'ilps_inner_sn';
            params.isAsc = 'desc';

            if (chooseObj) {
                let pn = parseInt(this.dealersPagination.total / params.pageSize);
                params.pageNum = pn + 1;
                this.vendorPagination.pageNum = pn + 1;
                this.vendorPagination.current = pn + 1;
            }
            await inquiryVendorList({ params }).then((res) => {
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
                        item.key = item.il_type + '' + item.ilps_inner_sn + '' + item.partA + '' + item.partB;
                    });
                    this.vendorDatas = data;
                    if (this.vendorDatas.length > 0) {
                        if (chooseObj) {
                            // 选中新增/编辑的项
                            this.currentTrObj = this.vendorDatas[this.vendorDatas.length - 1];
                        } else {
                            this.currentTrObj = this.vendorDatas[0]; // 默认选中第一
                        }
                        this.editObj = this.currentTrObj;
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
            this.editObj.systemType = this.tabsActiveKey;
            if (parseInt(obj.status) === 0) {
                this.isDraft = true;
            } else {
                this.isDraft = false;
            }
            this.clearSelect();
        },
        cellDblclickFn({ row }) {
            let obj = row.row || row;
            console.log(obj);
            this.currentTrObj = obj;
            this.editObj = this.currentTrObj;
            this.editObj.systemType = this.tabsActiveKey;
            let isEdit = true;
            if (parseFloat(obj.status) === 3) {
                isEdit = false;
            }
            if (this.tabsActiveKey === 1) {
                this.openReply();
            } else {
                this.openEdit(isEdit);
            }
            this.clearSelect();
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
        changeTabsFn(activeKey) {
            this.tabsActiveKey = parseFloat(activeKey);
            if (this.tabsActiveKey === 1) {
                this.isReply = true;
            } else {
                this.isReply = false;
            }
            this.currentTrObj = {};
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
        changeInquiry(e) {
            if (e.target.checked) {
                this.showInquiry = 1;
            } else {
                this.showInquiry = 0;
            }
            this.loadTableHos();
        },
        cancelCt() {
            this.createMd.alert = false;
            // this.$refs.enquiryListMd.refreshPage()
        },
        /** 头部按钮 **/
        openCreate() {
            // 打开新增
            this.$Utils.globalDragCenterFn('create-inquiry-md');
            this.createMd.alert = true;
            console.log(this.$refs);
            if (this.$refs.enquiryListMd) {
                this.$refs.enquiryListMd.refreshPage();
            }
        },
        createDone(dataObj) {
            this.createMd.alert = false;
            this.tabsActiveKey = dataObj.sys_type; // 指定tab
            // 添加行
            // let table = {}
            // if (dataObj.sys_type === 2) {
            //     table = this.$refs.deaTable // 经销商table
            // } else if (dataObj.sys_type === 3) {
            //     table = this.$refs.venTable // 厂商table
            // }
            // table && table.insert(dataObj).then(({row}) => {
            //     table.setCurrentRow(row)
            // })
            this.loadTable(dataObj);
        },
        openEdit(st) {
            // 打开修改
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
            let type = parseFloat(this.currentTrObj.status);
            this.editObj.systemType = this.tabsActiveKey;
            if (type === 3 && !st) {
                this.$Utils.globalDragCenterFn('reply-inquiry-md');
                this.replyMd.alert = true;
                this.$nextTick(() => {
                    this.$refs.replyListEditMd && this.$refs.replyListEditMd.refreshReplyPage(true);
                });
            } else {
                this.$Utils.globalDragCenterFn('edit-inquiry-md');
                this.editMd.alert = true;
                if (this.$refs.enquiryListEditMd) {
                    this.$refs.enquiryListEditMd.refreshPage();
                }
            }
        },
        editDone(data) {
            this.editMd.alert = false;
            // 更新当前编辑的行数据
            const editObjKeys = Object.keys(this.currentTrObj);
            Object.assign(this.currentTrObj, {
                ...this.$XEUtils.pick(data, editObjKeys)
            });
            // this.loadTable(this.currentTrObj)
        },
        replyDone() {
            this.replyMd.alert = false;
            // 暂时注释这个判断逻辑，改为有没有修改操作都发送请求
            // if (!changed) return // 没有修改操作 不发送请求
            this.loadTable(this.currentTrObj);
        },
        openReply() {
            // 打开回复
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
            this.editObj.systemType = this.tabsActiveKey;
            this.$Utils.globalDragCenterFn('reply-inquiry-md');
            this.replyMd.alert = true;
            this.$nextTick(() => {
                this.$refs.replyListEditMd && this.$refs.replyListEditMd.refreshReplyPage(false);
            });
        },
        openDel() {
            // 打开删除
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
            let key = parseFloat(this.tabsActiveKey);
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.delFn(key);
                }
            });
        },
        async delFn(key) {
            // 删除
            let params = {
                partA: this.currentTrObj.partA,
                ilps_inner_sn: this.currentTrObj.ilps_inner_sn,
                billsType: this.currentTrObj.billsType
            };
            await inquiryDelAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
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
        this.nowOrgType = parseFloat(this.$cookie.get('userSystemType'));
        if (this.nowOrgType === 1) {
            // 医院
            this.tabsActiveKey = 2;
        }
        if (this.tabsActiveKey === 1) {
            this.isReply = true;
        }
        this.loadTable();
    }
};
</script>

<style lang="less">
.inquiry-price-wrap {
    // 价格modal容器
    pointer-events: none;

    .inquiry-price-md {
        pointer-events: auto;
    }
}
.inquiry-reply-wrap {
    // 回复modal容器
    pointer-events: none;

    .reply-inquiry-md {
        pointer-events: auto;
    }
}
.inquiry-edit-wrap {
    // 编辑modal容器
    pointer-events: none;

    .edit-inquiry-md {
        pointer-events: auto;
    }
}
</style>
<style scoped lang="less">
.show_inquiry {
    position: absolute;
    margin-top: -33px;
    padding-left: 20px;
}
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
