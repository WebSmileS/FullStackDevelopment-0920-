<template>
    <div class="scoped" style="margin: -24px; height: 569px">
        <div class="search-box">
            <span class="label">名称</span>
            <div class="input_box">
                <a-input type="text" v-model="searchName" placeholder="" @change="searchNameChangeFn" />
            </div>
        </div>
        <div class="tabs-product-wrap" style="padding-top: 0px">
            <div
                style="margin-left: auto; margin-right: 10px; margin-bottom: 5px"
                v-if="
                    pageTypeName === 0 ||
                    pageTypeName === 3 ||
                    pageTypeName === 4 ||
                    pageTypeName === 7 ||
                    pageTypeName === 8
                "
                slot="tabBarExtraContent"
            >
                <a-checkbox :checked="isContractOrgChecked" @change="onContractOrgChangeFn"> 合同机构 </a-checkbox>
            </div>
            <ul class="tabs-product-content">
                <vxe-grid
                    ref="Table"
                    size="small"
                    :keyboard-config="{ isArrow: true }"
                    :columns="tableColumns"
                    :height="scrollYNumber - ipadHeight"
                    show-overflow
                    show-header-overflow
                    showHeader
                    resizable
                    auto-resize
                    border
                    highlight-current-row
                    @keydown="tableKeydownFn"
                    @cell-click="cellClickFn"
                    @scroll="tableScrollFn"
                    align="center"
                    :data="tableData"
                    :loading="tablePage.loading"
                >
                </vxe-grid>
            </ul>
        </div>
    </div>
</template>

<script>
/*************************************************
 *                 README
 *************************************************
 *   机构选择页面原为三个类型的tab，每个tab都有分页，分三个
 * 请求获取数据，现更改为无tab，三个类型的tab合并下拉展示，
 * 由于接口不变，所以使用原有接口。以下需注意：
 *
 * 1.数据加载分为“初次加载”和“滚动加载”，根据名称搜索时也算
 * 作初次加载
 *
 * 2.每次数据加载时都默认发送三个请求(医院，经销商，厂商)，
 * 并通过interfaceCount全局变量作为三个请求是否发送完成的
 * 标准。当请求发送成功后，会在回调中增加interfaceCount的
 * 值，当不需要发送某一个请求时，需手动增加interfaceCount。
 * 每次发送请求前都需要重制该值为0
 *
 * 3.由于几乎所有选择机构都有经销商，所以将经销商的接口调用
 * 放在最后，调用顺序不可更改，否则无法获取准确的interfaceCount
 *
 * 4.pageInitializing作为全局标识，防止外部调用初始化方法
 * 和组件自身初始化方法重复调用导致数据重复获取
 *************************************************
 * LastUpdate 2021/01/28 louxiaochen
 * - 更改每页数据为13挑
 *
 */
import {
    orderOrgListAPI,
    odoOrgListAPI,
    grnOrgListAPI,
    commAllOrgAPI,
    receiptOrgListAPI,
    getInquiryOrgListAPI
} from '@/service/pageAjax';

export default {
    name: 'chooseOrgTab',
    // =========================
    // pageTypeName: 页面类型
    // -------------------------
    // undefined -【合同-草稿/补录】
    // 0 -【仓库管理-入库单】
    // 1 -【添加产品时选择/骨科意向选择】
    // 2 -【仓库管理-出库单】
    // 3 -【采购管理-验货单】
    // 4 -【采购管理-收货单】
    // 6 -【采购管理-询价】
    // 7 -【采购管理-订单】
    // 8 -【销售管理-订单/销售单】
    // 9 -【财务模块】
    // =========================
    // partType:页面相关属性类型
    // -------------------------
    // 合同管理 - 1：甲方
    //           2：乙方
    // 添加产品/骨科意向 - 'hos'医院
    //                  'dea'经销商
    //                  'ven'厂商
    // =========================
    // searchOnrgName: 输入框的值
    // isParentKeyDownEnter: 是否是父组件的回车事件触发，默认是false
    props: ['partType', 'isCertifie', 'pageTypeName', 'searchOrgName', 'isParentKeyDownEnter'],
    data() {
        return {
            /** 机构搜索名称 */
            searchName: '',
            /** 登录用户类型 */
            userSysytemType: '',
            /** Modal高度限制 */
            scrollYNumber: 500,
            /** 表格设置 */
            tableColumns: [
                {
                    title: '名称',
                    dataIndex: 'org_name',
                    width: '30%',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.org_name];
                        }
                    }
                },
                {
                    title: '别名',
                    dataIndex: 'alias',
                    width: '20%',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.alias];
                        }
                    }
                },
                {
                    title: '统一社会信用代码',
                    dataIndex: 'social_credit_code',
                    width: '30%',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.social_credit_code];
                        }
                    }
                },
                {
                    title: '机构类型',
                    dataIndex: 'org_type_name',
                    width: '20%',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.org_type_name];
                        }
                    }
                }
            ],
            /** 表格数据 */
            tableData: [],
            /** api获取临时数据 */
            tableDataTemp: [],
            /** 接口调用计数 */
            interfaceCount: 0,
            // 请求相关数据
            tablePage: {
                loading: false, // 表格是否加载
                pageNum: 1, // 当前页数
                pageSize: 13, // 每页大小
                hosTotal: 0, // 医院数据总量
                deaTotal: 0, // 经销商数据总量
                venTotal: 0 // 厂商数据总量
            },
            // 当前滚动位置
            scrollTopValue: 0,
            // 是否勾选合同机构
            isContractOrgChecked: true,
            // 数据是否加载中
            pageInitializing: false,
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 57 : 0,
            modalWarning: null // 模态框
        };
    },
    methods: {
        // ****************************************************************
        // 接口区域
        // ****************************************************************

        // 【相关机构列表】 【仓库管理-入库单】
        async grnOrgListAPIFn(type, isInitial) {
            let params = {};
            params.pageSize = this.tablePage.pageSize;
            params.pageNum = this.tablePage.pageNum;
            if (this.searchName && this.searchName.length) {
                params.name = this.searchName;
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
                            item.key = item.org_id;
                            item.org_name = item.name;
                            item.org_type_name = this.parseOrgNameFromBillsType(item.billsType);
                            return item;
                        });
                        switch (type) {
                            case 1:
                                this.tablePage.hosTotal = res.total;
                                break;
                            case 2:
                                this.tablePage.deaTotal = res.total;
                                break;
                            case 3:
                                this.tablePage.venTotal = res.total;
                                break;
                        }
                        this.interfaceCount++;
                        this.tableDataTemp = this.tableDataTemp.concat(TempData);
                    }
                })
                .finally(() => {
                    this.recallHandler(isInitial);
                });
        },
        // 【相关机构列表】 【销售/采购管理 - 订单】
        async orderOrgListAPIFn(type, isInitial) {
            let params = {};
            params.pageSize = this.tablePage.pageSize;
            params.pageNum = this.tablePage.pageNum;
            if (this.searchName && this.searchName.length) {
                params.name = this.searchName;
            }
            if (this.isCertifie) {
                params.is_certifie = true;
            }
            if (this.pageTypeName === 7 || this.pageTypeName === 8) {
                params.all = this.isContractOrgChecked;
            }
            if (this.pageTypeName === 8) {
                params.isPurchase = false;
            }
            await orderOrgListAPI(type, this.$cookie.get('userbelong'), { params: params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let TempData = res.rows.map((item) => {
                            item.key = item.org_id;
                            item.org_name = item.name;
                            item.org_type_name = this.parseOrgNameFromBillsType(item.billsType);
                            return item;
                        });
                        switch (type) {
                            case 1:
                                this.tablePage.hosTotal = res.total;
                                break;
                            case 2:
                                this.tablePage.deaTotal = res.total;
                                break;
                            case 3:
                                this.tablePage.venTotal = res.total;
                                break;
                        }
                        this.interfaceCount++;
                        this.tableDataTemp = this.tableDataTemp.concat(TempData);
                    }
                })
                .finally(() => {
                    this.recallHandler(isInitial);
                });
        },
        // 【相关机构列表】 【仓库管理-收货单】
        async receiptOrgListAPIFn(type, isInitial) {
            let params = {};
            params.pageSize = this.tablePage.pageSize;
            params.pageNum = this.tablePage.pageNum;
            if (this.searchName && this.searchName.length) {
                params.name = this.searchName;
            }
            if (this.isCertifie) {
                params.isContract = this.isContractOrgChecked;
            }
            if (this.pageTypeName === 0) {
                // 入库单相关机构
                params.all = this.isContractOrgChecked;
            }
            await receiptOrgListAPI(type, this.$cookie.get('userbelong'), { params: params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let TempData = res.rows.map((item) => {
                            item.key = item.org_id;
                            item.org_name = item.name;
                            item.org_type_name = this.parseOrgNameFromBillsType(item.billsType);
                            return item;
                        });
                        switch (type) {
                            case 1:
                                this.tablePage.hosTotal = res.total;
                                break;
                            case 2:
                                this.tablePage.deaTotal = res.total;
                                break;
                            case 3:
                                this.tablePage.venTotal = res.total;
                                break;
                        }
                        this.interfaceCount++;
                        this.tableDataTemp = this.tableDataTemp.concat(TempData);
                    }
                })
                .finally(() => {
                    this.recallHandler(isInitial);
                });
        },
        // 【相关机构列表】 【仓库管理-出库单】
        async odoOrgListAPIFn(type, isInitial) {
            let params = {};
            if (this.searchName && this.searchName.length) {
                params.name = this.searchName;
            }
            params.pageSize = this.tablePage.pageSize;
            params.pageNum = this.tablePage.pageNum;
            await odoOrgListAPI(type, this.$cookie.get('userbelong'), { params: params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let TempData = res.rows.map((item) => {
                            item.key = item.org_id;
                            item.org_name = item.name;
                            item.org_type_name = this.parseOrgNameFromBillsType(item.billsType);
                            return item;
                        });
                        switch (type) {
                            case 1:
                                this.tablePage.hosTotal = res.total;
                                break;
                            case 2:
                                this.tablePage.deaTotal = res.total;
                                break;
                            case 3:
                                this.tablePage.venTotal = res.total;
                                break;
                        }
                        this.interfaceCount++;
                        this.tableDataTemp = this.tableDataTemp.concat(TempData);
                    }
                })
                .finally(() => {
                    this.recallHandler(isInitial);
                });
        },
        // 获取所有机构公共接口
        async loadTable(type, isInitial) {
            let params = {
                name: this.searchName
            };
            params.pageSize = this.tablePage.pageSize;
            params.pageNum = this.tablePage.pageNum;
            if (this.isCertifie) {
                params.is_certifie = true;
            }
            await commAllOrgAPI(type, { params: params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let TempData = res.rows.map((item) => {
                            item.key = item.org_id;
                            item.systemType = parseInt(type);
                            if (parseInt(item.org_type) === 1) {
                                item.org_type_name = '医院';
                            } else {
                                item.org_type_name = parseInt(item.org_type) === 2 ? '经销商' : '厂商';
                            }
                            return item;
                        });
                        switch (type) {
                            case 1:
                                this.tablePage.hosTotal = res.total;
                                break;
                            case 2:
                                this.tablePage.deaTotal = res.total;
                                break;
                            case 3:
                                this.tablePage.venTotal = res.total;
                                break;
                        }
                        this.interfaceCount++;
                        this.tableDataTemp = this.tableDataTemp.concat(TempData);
                    }
                })
                .finally(() => {
                    this.recallHandler(isInitial);
                });
        },
        // 【采购管理-询价】
        async getInquiryOrgListFn(type, isInitial) {
            let params = {
                name: this.searchName
            };
            params.pageSize = this.tablePage.pageSize;
            params.pageNum = this.tablePage.pageNum;
            if (this.isCertifie) {
                params.is_certifie = true;
            }
            await getInquiryOrgListAPI(type, { params: params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let TempData = res.rows.map((item) => {
                            item.key = item.org_id;
                            item.systemType = parseInt(type);
                            item.org_type_name = this.parseOrgNameFromBillsType(item.billsType);
                            return item;
                        });
                        switch (type) {
                            case 1:
                                this.tablePage.hosTotal = res.total;
                                break;
                            case 2:
                                this.tablePage.deaTotal = res.total;
                                break;
                            case 3:
                                this.tablePage.venTotal = res.total;
                                break;
                        }
                        this.interfaceCount++;
                        this.tableDataTemp = this.tableDataTemp.concat(TempData);
                    }
                })
                .finally(() => {
                    this.recallHandler(isInitial);
                });
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

        /**
         * 滚动条事件绑定方法
         * @abstract 每次滚动触发，用来监听是否滚动到底部
         * @param e 滚动事件
         */
        tableScrollFn(e) {
            if (this.pageInitializing) {
                return;
            }
            // distanceToBottom 的计算值理想情况为0，表示滚动到底部
            // 但是实际操作中，可能为[-1，1】的区间值，故取小于1为判断标准
            // 但是取值此区间可能会触发两次滚动请求，不影响使用
            const distanceToBottom = e.$event.target.scrollHeight - e.$event.target.clientHeight - e.scrollTop;
            if (distanceToBottom <= 1) {
                this.scrollTopValue = e.scrollTop;
                this.tablePage.loading = true;
                this.tablePage.pageNum++;
                const totalDataCount = (this.tablePage.pageNum - 1) * this.tablePage.pageSize;
                // 加载数据，如果数据加载完成，则不调用接口
                this.interfaceCount = 0;
                if (totalDataCount < this.tablePage.hosTotal) {
                    this.runAllApiByType(1, false);
                } else {
                    this.interfaceCount++;
                }
                if (totalDataCount < this.tablePage.venTotal) {
                    this.runAllApiByType(3, false);
                } else {
                    this.interfaceCount++;
                }
                if (totalDataCount < this.tablePage.deaTotal) {
                    this.runAllApiByType(2, false);
                } else {
                    this.interfaceCount++;
                }
                if (this.interfaceCount === 3) {
                    this.tablePage.loading = false;
                }
            }
        },
        /**
         * 综合api调用方法
         * @abstract 根据父组件类型决定调用的api
         * @param i 获取数据的类型：1-医院 2-经销商 3-厂商
         * @param isInitial 是否是初次加载数据
         */
        runAllApiByType(i, isInitial) {
            switch (this.pageTypeName) {
                case 0:
                    /* =====================================
                     *                入库单                *
                     * =====================================
                     *  登陆机构  |医院接口|经销商接口|厂商接口|
                     * =====================================
                     *   医院   |   O   |   O   |   O   |
                     * =====================================
                     *  经销商   |   O   |   O   |   O   |
                     * =====================================
                     *  厂商    |   O   |   O   |   O   |
                     * ===================================== */
                    this.grnOrgListAPIFn(i, isInitial);
                    break;
                case 1:
                    /* =====================================
                     *             添加产品选择              *
                     * =====================================
                     * 此处为只加载一个相关机构类型的方法，如产品选择
                     * 中只需要厂商，或只需要经销商，通过父组件
                     * partType决定                         */
                    if (isInitial && (i === 1 || i === 3)) {
                        this.interfaceCount++;
                        return;
                    }
                    // if (isInitial) {
                    //     this.tablePage.pageSize = 20
                    // }
                    switch (this.partType) {
                        case 'hos':
                            this.loadTable(1, isInitial);
                            break;
                        case 'dea':
                            this.loadTable(2, isInitial);
                            break;
                        case 'ven':
                            this.loadTable(3, isInitial);
                            break;
                    }
                    // if (isInitial) {
                    //     this.tablePage.pageNum++
                    // }
                    // this.tablePage.pageSize = 10
                    break;
                case 2:
                    /* =====================================
                     *                出库单                *
                     * =====================================
                     *  登陆机构  |医院接口|经销商接口|厂商接口|
                     * =====================================
                     *   医院   |   O   |   O   |   O   |
                     * =====================================
                     *  经销商   |   O   |   O   |   O   |
                     * =====================================
                     *  厂商    |   O   |   O   |   X   |
                     * ===================================== */
                    if (this.userSysytemType === 3 && i === 3) {
                        this.interfaceCount++;
                        return;
                    }
                    this.odoOrgListAPIFn(i, isInitial);
                    break;
                case 3:
                    /* =====================================
                     *                验货单                *
                     * =====================================
                     *  登陆机构  |医院接口|经销商接口|厂商接口|
                     * =====================================
                     *   医院   |   O   |   O   |   O   |
                     * =====================================
                     *  经销商   |   O   |   O   |   O   |
                     * =====================================
                     *  厂商    |   O   |   O   |   O   |
                     * ===================================== */

                    // 验货单不需要加载机构类型为医院的列表
                    if (i === 1) {
                        this.interfaceCount++; // 解决loading没有置空问题
                        return;
                    }
                    this.grnOrgListAPIFn(i, isInitial);
                    break;
                case 4:
                    /* =====================================
                     *                收货单                *
                     * =====================================
                     *  登陆机构  |医院接口|经销商接口|厂商接口|
                     * =====================================
                     *   医院   |   O   |   O   |   O   |
                     * =====================================
                     *  经销商   |   O   |   O   |   O   |
                     * =====================================
                     *  厂商    |   O   |   O   |   O   |
                     * ===================================== */
                    this.receiptOrgListAPIFn(i, isInitial);
                    break;
                case 6:
                    /* =====================================
                     *               采购询价               *
                     * =====================================
                     *  登陆机构  |医院接口|经销商接口|厂商接口|
                     * =====================================
                     *   医院   |   X   |   O   |   O   |
                     * =====================================
                     *  经销商   |   X   |   O   |   O   |
                     * =====================================
                     *  厂商    |   X   |   X   |   X   |
                     * ===================================== */
                    if (i === 1) {
                        this.interfaceCount++;
                        return;
                    }
                    this.getInquiryOrgListFn(i, isInitial);
                    break;
                case 7:
                    /* =====================================
                     *               采购订单               *
                     * =====================================
                     *  登陆机构  |医院接口|经销商接口|厂商接口|
                     * =====================================
                     *   医院   |   X   |   O   |   O   |
                     * =====================================
                     *  经销商   |   X   |   O   |   O   |
                     * =====================================
                     *  厂商    |   X   |   X   |   X   |
                     * ===================================== */
                    if (i === 1) {
                        this.interfaceCount++;
                        return;
                    }
                    this.orderOrgListAPIFn(i, isInitial);
                    break;
                case 8:
                    /* =====================================
                     *            销售订单/销售单            *
                     * =====================================
                     *  登陆机构  |医院接口|经销商接口|厂商接口|
                     * =====================================
                     *   医院   |   X   |   X   |   X   |
                     * =====================================
                     *  经销商   |   O   |   O   |   X   |
                     * =====================================
                     *  厂商    |   O   |   O   |   X   |
                     * ===================================== */
                    if (i === 3) {
                        this.interfaceCount++;
                        return;
                    }
                    this.orderOrgListAPIFn(i, isInitial);
                    break;
                case 9:
                    /* =====================================
                     *               财务模块               *
                     * =====================================
                     *  登陆机构  |医院接口|经销商接口|厂商接口|
                     * =====================================
                     *   医院   |   -   |   -   |   -   |
                     * =====================================
                     *  经销商   |   X   |   O   |   O   |
                     * =====================================
                     *  厂商    |   -   |   -   |   -   |
                     * ===================================== */
                    if (this.userSysytemType === 2 && i === 1) {
                        this.interfaceCount++;
                        return;
                    }
                    this.loadTable(i, isInitial);
                    break;
                default:
                    /* =====================================
                     *               合同模块               *
                     * =====================================
                     *  登陆机构  |医院接口|经销商接口|厂商接口|
                     * =====================================
                     *   医院   |   X   |   O   |   O   |
                     * =====================================
                     *  经销商甲方|   X   |   O   |   O   |
                     * =====================================
                     *  经销商乙方|   O   |   O   |   X   |
                     * =====================================
                     *  厂商    |   O   |   O   |   X   |
                     * ===================================== */
                    if (
                        (this.userSysytemType === 2 && parseFloat(this.partType) === 1 && i === 1) ||
                        (this.userSysytemType === 2 && parseFloat(this.partType) === 2 && i === 3) ||
                        (this.userSysytemType === 3 && i === 3) ||
                        (this.userSysytemType === 1 && i === 1)
                    ) {
                        this.interfaceCount++;
                        return;
                    }
                    this.loadTable(i, isInitial);
                    break;
            }
        },
        /**
         * 页面初始化
         * @abstract 共有三处会调用此接口
         *           1.组件外部，打开选择机构窗口时由父组件调用；
         *           2.组件mounted方法调用；
         *           3.searchName改变时调用；
         */
        pageDataInit() {
            // 如果正在初始化，则不调用
            if (this.pageInitializing) {
                return;
            }
            this.pageInitializing = true;
            this.tableData = [];
            this.tableDataTemp = [];
            this.tablePage.pageNum = 1;
            this.tablePage.pageSize = 13;
            this.tablePage.loading = true;
            this.interfaceCount = 0;
            this.tablePage.hosTotal = 0;
            this.tablePage.venTotal = 0;
            this.tablePage.deaTotal = 0;
            this.$refs.Table.clearScroll().then(() => {
                this.$refs.Table.scrollTo(null, 0);
            });
            this.userSysytemType = parseFloat(this.$cookie.get('userSystemType'));

            this.runAllApiByType(1, true);
            this.runAllApiByType(3, true);
            this.runAllApiByType(2, true);
        },
        /**
         * 接口回调的统一处理方法
         * @abstract 所有的api返回的promise中，finally()绑定的方法
         * @param isInitial 由api传递，是否是初次加载数据
         */
        recallHandler(isInitial) {
            if (!isInitial && this.tableDataTemp.length > 0) {
                // 滚动加载数据，拼接数据至tableData
                this.tableData = this.tableData.concat(this.tableDataTemp);
                this.tableDataTemp = [];
            }
            if (isInitial) {
                // 初次加载数据，重新赋值tableData
                this.$refs.Table.focus();
                this.$refs.Table.setCurrentRow(this.tableData[0]);
                this.tableData = this.tableDataTemp;
                this.pageInitializing = false;
            }
            // 当三个机构类型的接口都调用完毕时进行操作
            if (this.interfaceCount === 3) {
                this.tableDataTemp = [];
                // 初始化数据
                this.interfaceCount = 0;
                this.tablePage.loading = false;
                // 更新表格
                this.$refs.Table.loadData(this.tableData);
                this.$refs.Table.clearScroll().then(() => {
                    this.$refs.Table.scrollTo(null, this.scrollTopValue);
                });
            }
        },
        // 修改合同机构状态值
        onContractOrgChangeFn(e) {
            // 初始化数据
            this.isContractOrgChecked = e.target.checked;
            this.pageDataInit();
        },
        // 默认是否勾选上合同内产品
        setContractOrgCheckedFn() {
            if (this.pageTypeName !== 0 && this.pageTypeName !== 4) return;
            this.isContractOrgChecked = true;
        },
        cellClickFn(row) {
            // 单击行
            let obj = row.row || row;
            // 如果要重置searchName，就需要把列表数据（搜索的结果）也进行重置
            // this.searchName = '';
            this.$emit('chooseOrg', obj);
        },
        tableKeydownFn(tableEvent) {
            const { $event, $table } = tableEvent;
            if ($event && $table) {
                const key = $event.key;
                if (key === 'Enter') {
                    const row = $table.getCurrentRow();
                    if (!row) {
                        /// 解决模态框重复出现的问题
                        if (this.modalWarning && this.modalWarning.destroy) {
                            this.modalWarning.destroy();
                        }
                        this.modalWarning = this.$warning({
                            title: '操作提示',
                            content: `${this.$Utils.tips().selectOrgName}`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                    } else {
                        $table.blur();
                        this.cellClickFn(row);
                    }
                }
            }
        },
        searchNameChangeFn({ target }) {
            // isParentKeyDownEnter的值从未被使用？来自上层组件的props，也不应该直接赋值
            // setTimeout(() => {
            //     this.isParentKeyDownEnter = false;
            // }, 300);
            const v = target.value;
            setTimeout(() => {
                if (v === this.searchName) {
                    this.pageDataInit();
                }
            }, 300);
            this.searchName = v;
        }
    },
    watch: {
        searchOrgName: {
            handler(newVal) {
                // isParentKeyDownEnter的值从未被使用？来自上层组件的props，也不应该直接赋值
                // setTimeout(() => {
                //     this.isParentKeyDownEnter = false;
                // }, 300);
                this.searchName = newVal;
            },
            deep: true,
            immediate: true
        }
    },
    mounted() {
        // 经销商默认取消勾选合同机构
        if (this.userSysytemType === 2) {
            this.isContractOrgChecked = false;
        }
        this.pageDataInit();
    }
};
</script>

<style scoped lang="less">
.search-box {
    margin-top: 20px;
    margin-bottom: 6px;
    .label {
        display: inline-block;
        width: 8%;
        text-align: right;
        margin-right: 10px;
    }
    .input_box {
        display: inline-block;
        width: 88%;
        .ant-input {
            line-height: 24px;
            height: 24px;
        }
    }
}
</style>
