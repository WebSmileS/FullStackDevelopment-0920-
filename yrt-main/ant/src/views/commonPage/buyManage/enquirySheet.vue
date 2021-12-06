<template>
    <and-page-layout class="scoped scoped-white enquiry-sheets">
        <template slot="breadcrumb">
            <a-breadcrumb style="float: left">
                <a-breadcrumb-item>采购管理</a-breadcrumb-item>
                <a-breadcrumb-item>询价</a-breadcrumb-item>
            </a-breadcrumb>
        </template>
        <template slot="tabs-content">
            <and-tabs-content :panes="doPanes" :cur="curTab" @changeTabFn="changeTabFn">
                <template slot="atc-header">
                    <a-row class="atc-header"
                        v-if="curTab === 'add'"
                        style="margin: 0"
                    >
                        <enquiry-add-header
                            ref="inquiryAddHeader"
                            @openChooseTb="openChooseTb"
                            @chooseOrgFn="chooseOrgFn"
                            @changeFormData="changeFormDataFn"
                            :searchOrgName="searchOrgName"
                            :formData="formData"
                            :editable="editable"
                            v-if="curTab === 'add'"
                        ></enquiry-add-header>
                        <enquiry-dealer-header
                            v-if="curTab !== 'add'"
                        >
                        </enquiry-dealer-header>
                    </a-row>
                </template>
                <template slot="atc-pane">
                    <a-row class="atc-pane">
                        <and-sheet-acts
                            :data="actColumns"
                            :pageName="pageName"
                            :curTab="curTab"
                            :widths="widths"
                            @renderPage="changeTableSettings"
                        >
                        </and-sheet-acts>
                        <and-sheet
                            ref="andSheetElem"
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
                    <enquiry-add-footer
                        v-if="curTab === 'add'"
                        :formData="formData"
                        :proData="addData"
                        :orgSystemType="selectedOrg.systemType"
                        :editable="editable"
                        @refresh="refresh"
                        @changeFormData="changeFormDataFn"
                        @changeTabFn="changeTabFn"
                    >
                    </enquiry-add-footer>
                </template>
            </and-tabs-content>
        </template>
        <template slot="modals">
            <!-- choose org modal -->
            <a-modal
                :title="chooseOrgMd.title"
                :visible="chooseOrgMd.alert"
                :maskClosable="false"
                :width="600"
                :centered="true"
                @cancel="chooseOrgMd.alert = false"
                class="scoped modal-form-input-scoped global-drag choose_org_md"
            >
                <div v-globalDrag="{ el: 'chooseOrgMd' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ chooseOrgMd.title }}
                    </h6>
                </div>
                <div style="margin-top: 56px">
                    <choose-org-tab
                        ref="chooseOrgMd"
                        @chooseOrg="chooseOrgFn"
                        :pageTypeName="6"
                        :partType="1"
                        :isCertifie="true"
                    ></choose-org-tab>
                </div>
            </a-modal>
            <!-- add product modal -->
            <a-modal
                :title="addProductMd.title"
                :visible="addProductMd.alert"
                :maskClosable="false"
                :mask="false"
                :width="1000"
                :centered="true"
                @cancel="addProductMd.alert = false"
                class="modal-form-input-scoped global-drag add_product"
            >
                <div v-globalDrag="{ el: 'add_product' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ addProductMd.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <a-button @click="addProductMd.alert = false">关闭</a-button>
                    <a-button :loading="addProductMd.loading" @click="addProductFn" type="primary">添加</a-button>
                </template>
                <add-product
                    ref="chooseProMd"
                    @returnData="getProReturn"
                    :partB="part_b_obj"
                    :metaTitle="addProductMd.title"
                ></add-product>
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

        </template>
    </and-page-layout>
</template>

<script>
import {
    inquiryDealerList,
    inquiryVendorList,
    inquiryUnreplyList,
    inquiryDelAPI,
    getVendorProductListAPI,
    platformUnitTree,
    inquiryInfoAPI,
    userPageSettings,
    storePageSettings
} from '@/service/pageAjax';

// import newDesign Layount
import AndPageLayout from '@/components/AndLayout/AndPageLayout';
import AndTabsContent from '@/components/AndLayout/AndTabsContent';
// import components
import AndSheetActs from '@/components/purchase/AndSheetActs';
import AndSheet from '@/components/AndRenders/AndSheet';
import EnquiryAddHeader from '@/components/purchase/EnquiryAddHeader';
import EnquiryAddFooter from '@/components/purchase/EnquiryAddFooter';

import replyInquiry from './module/replyInquiry';
import price from './module/priceInquiry';

// import modals
import chooseOrgTab from '@/components/chooseOrgTab';
import addProduct from '@/components/addProduct';


export default {
    name: 'enquiryList',
    components: {
        // custom layounts
        AndPageLayout,
        AndTabsContent,
        // custom components
        AndSheet,
        AndSheetActs,
        EnquiryAddHeader,
        EnquiryAddFooter,
        
        chooseOrgTab,
        addProduct,
        replyInquiry,
        price
    },
    data() {
        return {
            // init panes
            panes: [
                {
                    key: 'add',
                    title: '新增',
                    permissions: [1, 2, 3]
                },
                {
                    key: 'unreply',
                    title: '询价回复',
                    permissions: [2, 3]
                },
                {
                    key: 'dealer',
                    title: '经销商/厂商',
                    permissions: [1, 2]
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
            totalFormat: {
                on: false,
                searchable: false
            },
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
                    name: '经销商'
                },
                {
                    data: 'ilps_sn',
                    type: 'input',
                    disabled: true,
                    name: '单据编号'
                },
                {
                    data: 'contacts',
                    type: 'input',
                    disabled: true,
                    name: '联系人'
                },
                {
                    data: 'phone',
                    type: 'input',
                    disabled: true,
                    name: '电话'
                },
                {
                    data: 'ask_time',
                    type: 'input',
                    disabled: true,
                    name: '询价时间'
                },
                {
                    data: 'answer_time',
                    type: 'input',
                    disabled: true,
                    name: '回复时间'
                },
                {
                    data: 'status',
                    type: 'simpleDropdown',
                    list: [
                        {
                            key: '询价草稿',
                            name: '询价草稿'
                        },
                        {
                            key: '询价',
                            name: '询价'
                        },
                        {
                            key: '回复草稿',
                            name: '回复草稿'
                        },
                        {
                            key: '已回复',
                            name: '已回复'
                        }
                    ],
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
                    data: 'name',
                    type: 'dropdown',
                    customNext: true,
                    disabled: false,
                    name: '产品'
                },
                {
                    data: 'vendor_name',
                    type: 'input',
                    disabled: true,
                    name: '厂商'
                },
                {
                    data: 'quantity',
                    type: 'number',
                    disabled: false,
                    min: 0,
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
                    data: 'unit_price_text',
                    type: 'number',
                    disabled: true,
                    name: '单价',
                    formula: '"unit_price" / (1 + "rate_string" / 100)'
                },
                {
                    data: 'sum',
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
                    data: 'rate_unit',
                    type: 'number',
                    disabled: false,
                    name: '含税单价'
                },
                {
                    data: 'rate_price',
                    type: 'number',
                    disabled: true,
                    name: '含税金额',
                    formula: '"quantity" * "rate_unit"'
                },
                {
                    data: 'rate_num',
                    type: 'number',
                    disabled: true,
                    name: '税额',
                    formula: '"rate_price" - "sum"'
                }
            ],
            copied: {
                flag: false,
                objs: []
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
            pageName: 'enquiryList',
            venTableData: [],
            dataEditing: {},
            // others
            searchOrgName: '',
            formData: {
                partA: '', // 询价机构ID
                partB: '', // 被询价机构ID
                ilps_sn: '', // 询价单编号
                contacts: '', // 联系人
                phone: '', // 电话
                detailList: '',
                description: '', // 摘要
                status: false
            },
            selectedOrg: {},
            chooseOrgMd: {
                title: `询价 - 新增 - 选择机构`,
                alert: false,
                loading: false
            },
            addProductMd: {
                title: `询价 - 新增 - 添加产品`,
                alert: false,
                loading: false
            },
            chooseProFlag: false,
            part_b_obj: {},


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
                total: 30,
                current: 1, // 对应后端的 pageSize
                pageSize: 30, // 对应后端的 pageNum
                pageNum: 1,
                available: true,
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
        openChooseTb() {
            // 打开选择甲乙方
            this.chooseOrgMd.alert = true;
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
        },
        chooseOrgFn(org) {
            // setup port_b_obj
            this.part_b_obj.systemType = org.systemType;
            this.part_b_obj.modalType = 6;
            this.part_b_obj.id = org.org_id;
            this.part_b_obj.name = org.name;
            // change state
            this.selectedOrg = org;
            this.searchOrgName = org.org_name;
            this.formData.orgName = org.org_name;
            this.formData.partB = org.org_id;
            // 获取选中机构信息
            let systemType = org.systemType,
                id = org.org_id;
            let nowId = parseInt(this.cookieInfo.get('userbelong')),
                nowType = parseInt(this.cookieInfo.get('userSystemType'));
            if (nowId === id && systemType === nowType) {
                this.$warning({
                    title: '操作提示',
                    content: '自己不能和自己询价',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            this.chooseOrgMd.alert = false;
            this.$refs.inquiryAddHeader.changeFocus('form-contacts');
            this.loadVendorPros();
        },
        openAddPro(e) {
            // 打开选择产品
            if (e) {
                const filterDom = ['span', 'svg', 'path', 'input', 'i', 'li', 'ul'],
                    flag = filterDom.some((item) => item === e.target.tagName.toLowerCase()),
                    childrenFlag = filterDom.some(
                        (item) => item === e.target.children && e.target.children.tagName.toLowerCase()
                    ),
                    childrenFlagRemove = e.target.getElementsByClassName('action-btn-list-wrap').length;
                if (flag || childrenFlag || childrenFlagRemove) return;
            }
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn('add_product_edit');
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        async loadVendorPros(value = '') {
            this.venTableData = [];
            // 所有产品
            let params = {};
            params.org_id = parseInt(this.cookieInfo.get('userbelong'));
            params.name = value;
            params.pageSize = 10;
            params.pageNum = 1;
            await getVendorProductListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.venTableData = res.rows.map((item) => {
                        item.unit_name = item.min_unit;
                        item.unit_inner_sn = item.min_unit_inner_sn;
                        item.unitList =
                            item.unit_name && item.unit_inner_sn
                                ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                : [];
                        item.unit_inner_sn = [item.unit_inner_sn];

                        return {
                            ...item,
                            isActive: '',
                            activeClass: '',
                            key: (Math.random() + 1).toString(36).substring(7),
                            keyFormat: item.vendor_inner_sn + '' + item.product_inner_sn + item.specification_inner_sn,
                            // product_name: item.name || item.product_name,
                            name: item.product_name,
                            product_type: 1, // 0 - '经销商'  1 - '厂商'
                            sys_type: '厂商',
                            batch_number: '',
                            quantity: 1,
                            unit_price: 1,
                            money: 2,
                            produce_date: '',
                            overdue_date: '',
                            type: '0',
                            register_inner_sn: item.mdrf_inner_sn,
                            register_sn: item.mdrf_sn,
                            mdrf_inner_sn: item.mdrf_inner_sn,
                            mdrf_sn: item.mdrf_sn,
                            mdrfList: item.mdrf_sn
                                ? [{ value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn }]
                                : [],
                            search_product_name: value,
                            status: 0
                        };
                    });
                }
            });
        },
        refresh() {
            this.formData = {
                partA: this.cookieInfo.get('userbelong'), // 询价机构ID
                partB: '', // 被询价机构ID
                ilps_sn: '', // 询价单编号
                contacts: '', // 联系人
                phone: '', // 电话
                detailList: '',
                description: '', // 摘要
                status: false
            };
            this.selectedOrg = {};
            this.searchOrgName = '';
            this.editPro = false;
            this.changeTabFn();
        },
        addProductFn() {
            // 添加商品
            this.$refs.chooseProMd.returnProList();
        },
        async editablEnquiryInfo() {
            // 初始化
            let params = {
                partA: this.editable.obj.partA,
                ilps_inner_sn: this.editable.obj.ilps_inner_sn,
                billsType: this.editable.obj.billsType
            };
            let type = parseFloat(this.editable.obj.status);
            // if (type === 1 || type === 2 || type === 3) {
            if (type === 3) {
                this.preview = true;
            } else {
                this.preview = false;
            }
            await inquiryInfoAPI({ params }).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.formData = res.info;
                    if (this.editable.type === 'reply') {
                        this.formData.status = 3;
                    }
                    // 如果是询价回复，partA_name，其它partB_name
                    this.orgName =
                        this.editable.obj && this.editable.obj.systemType === 1 ? res.info.partA_name : res.info.partB_name;
                    this.part_b_obj.id = res.info.partB;
                    this.part_b_obj.name = res.info.partB_name;
                    this.part_b_obj.systemType = this.editable.obj.systemType;
                    this.searchOrgName = res.info.partB_name;
                    this.part_b_obj.modalType = 6;
                    this.part_b_obj.billsType = res.info.billsType;
                    let data = [];
                    const _this = this;
                    res.info.detailList.forEach((item, idx) => {
                        item.isActive = '';
                        item.key = (Math.random() + 1).toString(36).substring(7);
                        item.idx = idx + 1;
                        item.name = item.product_name;
                        item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额
                        item.rate = item.rate * 100;
                        item.rate_string = item.rate;
                        item.rate_unit = item.unit_price; // 含税单价 = 单价 【默认值】
                        item.unit_price = parseFloat(item.rate_unit) / (1 + parseFloat(item.rate) / 100); // 单价 = 含税单价 / (1 + 税率 / 100)
                        item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额 = 数量 * 单价
                        item.rate_price = parseFloat(item.quantity) * parseFloat(item.rate_unit); // 含税金额 = 数量 * 含税单价
                        item.rate_num = parseFloat(item.rate_price) - parseFloat(item.sum); // 税额 = 含税金额 - 金额
                        item.isEdit = true;
                        item.unit_data = {
                            key: [item.unit_inner_sn],
                            data: [
                                {
                                    value: item.unit_inner_sn,
                                    label: item.unit_name
                                }
                            ]
                        };
                        data.push(item);
                        _this.venUnitTreeFn(item);
                    });
                    this.addData = data;
                }
            });
        },
        getProReturn(dea, ven) {
            this.addProductMd.alert = false;
            let arr = ven.concat(dea);
            arr.forEach((item, idx) => {
                if (item.product_type === 0) {
                    item.sys_type = item.dealer_name;
                } else if (item.product_type === 1) {
                    item.sys_type = item.vendor_name;
                }
                item.key = (Math.random() + 1).toString(36).substring(7);
                item.isActive = '';
                item.unit_inner_sn = '';
                item.quantity = 1;
                item.discount = 1;
                item.rate = 0;
                item.rate_unit = item.unit_price; // 含税单价 = 单价 【默认值】
                item.unit_price_text = parseFloat(item.rate_unit) / (1 + parseFloat(item.rate) / 100); // 单价 = 含税单价 / (1 + 税率 / 100)
                item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price_text); // 金额 = 数量 * 单价
                item.rate_price = parseFloat(item.quantity) * parseFloat(item.rate_unit); // 含税金额 = 数量 * 含税单价
                item.rate_num = parseFloat(item.rate_price) - parseFloat(item.sum); // 税额 = 含税金额 - 金额
                item.idx = idx + 1;
                item.isNew = true;
                this.addData.splice(this.editCellRow + idx, 0, item);
                this.venUnitTreeFn(item);
            });
            this.addData = this.addData.map((item, idx) => {
                return {
                    ...item,
                    idx: idx + 1
                };
            });
            this.$refs.andSheetElem.changeFocus(this.editCellRow, 3); // focus to quantity
        },

        changeFormDataFn(obj) {
            this.formData[obj.key] = obj.value
        },
        calcPowPagination(pagiObj, total) {
            let obj = {
                loading: false,
                total: total,
                current: 1, // 对应后端的 pageSize
                pageSize: 30, // 对应后端的 pageNum
                pageNum: pagiObj.pageNum + 1,
                available: (pagiObj.pageNum * pagiObj.pageSize) < pagiObj.total ? true : false,
                pageSizes: [10, 20, 30, 40]
            };
            return obj;
        },
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
        sheetScrollToEnd() {
            if (this.curTab !== 'add') {
                if (this.sheetPagination.available) {
                    this.sheetPagination.available = false;
                    switch (this.curTab) {
                        case 'dealer':
                            this.loadTableDel();
                            break;
                        case 'vendor':
                            this.loadTableVen();
                            break;
                    }
                }
                if (this.sheetPagination2.available) {
                    this.sheetPagination2.available = false;
                    switch (this.curTab) {
                        case 'dealer':
                            this.loadTableVen();
                            break;
                        case 'vendor':
                            this.loadTableVen();
                            break;
                    }
                }
                if (this.hospitalPagination.available) {
                    this.hospitalPagination.available = false;
                    switch (this.curTab) {
                        case 'unreply':
                            this.loadTableHos();
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
                case 'unreply':
                    this.menu = {
                        visible: false,
                        data: [
                            {
                                disabled: false,
                                key: 'unreply',
                                name: '回复'
                            }
                        ],
                        position: {
                            top: undefined,
                            left: undefined
                        }
                    };
                    break;
                case 'dealer':
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
                            }
                        ],
                        position: {
                            top: undefined,
                            left: undefined
                        }
                    };
                    break;
                case 'vendor':
                    this.menu = {
                        visible: false,
                        data: [
                            {
                                disabled: true,
                                key: 'revise',
                                name: '修改'
                            },
                            {
                                disabled: true,
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
            if (tab !== 'add') {
                if (this.nowOrgType !== 3) {
                    this.menu.data.push({
                        disabled: false,
                        key: 'check_price',
                        name: '价格查询'
                    });
                }
            }
        },
        cookieStoreSettings(data) {
            this.cookieInfo.set(data.page_name + '-' + data.page_type, data.settings);
            this.changeTableSettings();
        },
        async userPageSettingsFn() {
            await userPageSettings(this.pageName, this.curTab).then((res) => {
                if (res.code === 0 && res.user_setting !== null) {
                    this.cookieStoreSettings(res.data);
                }
            });
        },
        async storePageSettingsFn(params) {
            if (params.settings) {
                await storePageSettings(params).then((res) => {
                    console.log(res);
                });
            }
        },
        postStoreSettings() {
            let cookieData = JSON.parse(this.cookieInfo.get(this.pageName + '-' + this.curTab));
            let data = {};
            if (cookieData !== null && cookieData.headers !== undefined && cookieData.headers.length > 0) {
                data['headers'] = cookieData.headers;
            }
            if (cookieData !== null && cookieData.widths !== undefined && cookieData.widths.length > 0) {
                data['widths'] = cookieData.widths;
            }
            this.storePageSettingsFn({
                page_name: this.pageName,
                page_type: this.curTab,
                settings: JSON.stringify(data)
            });
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
            this.postStoreSettings();
        },
        deleteAddData(obj) {
            for (let i = obj.selection.from; i <= obj.selection.to; i ++) {
                this.addData[i] = {
                    ...this.addData[i],
                    name: '',
                    vendor_name: '',
                    quantity: '',
                    unit_name: '',
                    unit_price_text: '',
                    sum: '',
                    rate_string: '',
                    rate_unit: '',
                    rate_price: '',
                    rate_num: ''
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
            if (this.curTab !== 'add' && obj.key === 'revise') {
                if (this.menu.data[0].disabled === false) {
                    this.editable = {
                        flag: true,
                        type: 'edit',
                        obj: this.addData[obj.selection.from]
                    };
                    this.changeTabFn();
                }
            }
            if (this.curTab !== 'add' && obj.key === 'unreply') {
                if (this.menu.data[0].disabled === false) {
                    this.editable = {
                        flag: true,
                        type: 'reply',
                        obj: this.addData[obj.selection.from]
                    };
                    this.changeTabFn();
                }
            }
            if (this.curTab !== 'add' && obj.key === 'delete') {
                this.currentTrObj = Object.assign({}, this.addData[obj.selection.from]);
                this.openDel();
            }
            if (this.curTab !== 'add' && obj.key === 'check_price') {
                this.handleOpenPriceModal();
            }
            let records = this.getSelectionData(obj.selection.from, obj.selection.to);
            if ((this.curTab === 'add' && obj.key === 'copy') || this.curTab === 'add' && obj.key === 'cut') {
                this.copied.flag = true;
                let objs = [];
                records.forEach((r) => {
                    objs.push(Object.assign({}, r));
                });
                this.copied.objs = objs;
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
                this.loadVendorPros(obj.value);
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
                    this.openAddPro(null);
                } else if (obj.value === 'tips') {
                    return;
                } else {
                    this.selectedVen = Object.assign(
                        {},
                        this.venTableData.find((v) => v.key === obj.value)
                    );
                    if (this.selectedVen.product_type === 0) {
                        this.selectedVen.sys_type = this.selectedVen.dealer_name;
                    } else if (this.selectedVen.product_type === 1) {
                        this.selectedVen.sys_type = this.selectedVen.vendor_name;
                    }
                    this.selectedVen.isActive = '';
                    this.selectedVen.unit_inner_sn = '';
                    this.selectedVen.quantity = 1;
                    this.selectedVen.discount = 1;
                    this.selectedVen.rate_string = '0';
                    this.selectedVen.rate = 0;
                    this.selectedVen.rate_unit = this.selectedVen.unit_price; // 含税单价 = 单价 【默认值】
                    this.selectedVen.unit_price_text = parseFloat(this.selectedVen.rate_unit) / (1 + parseFloat(this.selectedVen.rate) / 100); // 单价 = 含税单价 / (1 + 税率 / 100)
                    this.selectedVen.sum = parseFloat(this.selectedVen.quantity) * parseFloat(this.selectedVen.unit_price_text); // 金额 = 数量 * 单价
                    this.selectedVen.rate_price = parseFloat(this.selectedVen.quantity) * parseFloat(this.selectedVen.rate_unit); // 含税金额 = 数量 * 含税单价
                    this.selectedVen.rate_num = parseFloat(this.selectedVen.rate_price) - parseFloat(this.selectedVen.sum); // 税额 = 含税金额 - 金额
                    this.selectedVen.idx = obj.row + 1;
                    this.selectedVen.isNew = true;
                    this.addData[obj.row] = Object.assign({}, this.selectedVen);
                    this.venUnitTreeFn(this.selectedVen, 'add');
                    this.$refs.andSheetElem.changeFocus(obj.row, 3); // focus to quantity
                }
            }
        },
        async venUnitTreeFn(record, type = 'add') {
            // 厂商商产品单位
            let oid = record.vendor_inner_sn;
            let pid = record.product_inner_sn;
            let psid = record.specification_inner_sn;
            await platformUnitTree(oid, pid, psid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // if (type === 'add') {
                    //     this.hotFullData.push(record);
                    // }
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
                    console.log(this.addData);
                }
            });
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
            this.hospitalPagination = {
                loading: false,
                total: 30,
                current: 1, // 对应后端的 pageSize
                pageSize: 30, // 对应后端的 pageNum
                pageNum: 1,
                available: true
            }
        },
        resetSheetData() {
            this.addData = [];
            this.widths = [];
            this.columns = [];
            this.resetPaginations();
        },
        // save table setting - keep data
        changeTableSettings() {
            this.widths = [];
            this.columns = [];
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
                switch (this.curTab) {
                    case 'add':
                        this.columns = this.addColumns;
                        break;
                    case 'dealer':
                    case 'vendor':
                        this.columns = this.dealerColumns;
                        if (this.curTab === 'vendor') {
                            this.columns[1].name = '经销商/厂商';
                        }
                        break;
                }
            }
            if (cookieData !== null && cookieData.widths !== undefined && cookieData.widths.length > 0) {
                this.widths = cookieData.widths;
            }
            // this.postStoreSettings();
        },
        changeTabFn(key = 'add') {
            this.curTab = key;
            // reset Sheet
            this.resetSheetData();
            // checking reply
            this.formData.status = key === 'unreply' ? 3 : 1;
            // actions
            switch (key) {
                case 'unreply':
                    this.actColumns = this.dealerColumns;
                    if (key === 'vendor') {
                        this.actColumns[1].name = '询价机构';
                    }
                    break;
                case 'dealer':
                case 'vendor':
                    this.actColumns = this.dealerColumns;
                    if (key === 'vendor') {
                        this.actColumns[1].name = '经销商/厂商';
                    }
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
                        this.columns = this.addColumns;
                        break;
                    case 'unreply':
                        this.columns = this.dealerColumns;
                        if (key === 'vendor') {
                            this.columns[1].name = '询价机构';
                        }
                        break;
                    case 'dealer':
                    case 'vendor':
                        this.columns = this.dealerColumns;
                        if (key === 'vendor') {
                            this.columns[1].name = '经销商/厂商';
                        }
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
                case 'unreply':
                    // searchable
                    this.totalFormat.searchable = true;
                    this.loadTableHos();
                    break;
                case 'dealer':
                case 'vendor':
                    this.curTab = 'dealer';
                    // searchable
                    this.totalFormat.searchable = true;
                    if (key === 'dealer') {
                        this.loadTableDel();
                        this.loadTableVen();
                    } else {
                        this.loadTableVen();
                        this.loadTableDel();
                    }
                    break;
                default:
                    // searchable
                    this.totalFormat.searchable = false;
                    //add
                    if (this.editable.flag) {
                        this.panes[0].title = '编辑';
                        this.editablEnquiryInfo();
                    } else {
                        this.panes[0].title = '新增';
                        let data = [];
                        for (let i = 0; i < 10; i++) {
                            data.push({
                                idx: i + 1,
                                name: '',
                                vendor_name: '',
                                quantity: '',
                                unit_name: '',
                                unit_price_text: '',
                                sum: '',
                                rate_string: '',
                                rate_unit: '',
                                rate_price: '',
                                rate_num: ''
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
                    name: '',
                    vendor_name: '',
                    quantity: '',
                    unit_name: '',
                    unit_price: '',
                    sum: '',
                    rate_string: '',
                    rate_unit: '',
                    rate_price: '',
                    rate_num: ''
                });
            }
        },
        searchSheetFn(obj) {
            if (this.curTab !== 'add') {
                this.addData = [];
                this.resetPaginations();
                switch (this.curTab) {
                    case 'unreply':
                        this.loadTableHos(obj.name);
                        break;
                    default:
                        this.loadTableDel(obj.name);
                        his.loadTableVen(obj.name);
                        break;
                }
            }
        },
        multiSelected(obj) {
            if (this.curTab !== 'add') {
                let data = [];
                this.menu.data.forEach((item) => {
                    item.disabled = obj.from !== obj.to ? true: false;
                    data.push(item);
                });
                this.menu.data = data;
            }
            if (this.curTab === 'add') {
                this.menu.data[2].disabled = !this.copied.flag;
            }
        },
        onUpdateSheet(records) {
            if (records) {
                records.forEach((item, idx) => {
                    item.rate_string = item.rate;
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
        async loadTableHos(value = '') {
            // 加载回复列表
            this.hospitalPagination.loading = true;
            let params = {};
            let all = this.showInquiry;
            params.key = value;
            params.pageSize = this.hospitalPagination.pageSize;
            params.pageNum = this.hospitalPagination.pageNum;
            await inquiryUnreplyList(all, { params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res;
                    let startIdx = this.addData.length;
                    this.hospitalPagination = this.calcPowPagination(this.hospitalPagination, total);
                    let data = rows.map((item, index) => {
                        let text = item.status;
                        let itemObj = {
                            ...item,
                            idx: startIdx + index + 1,
                            key: (Math.random() + 1).toString(36).substring(7),
                            isActive: '',
                            status: parseInt(text) === 2 ? this.contractStatus[parseInt(text) - 1] : this.contractStatus[text]
                        };
                        return itemObj;
                    });
                    this.addData.push(...data);
                }
            });
        },
        async loadTableDel(value = '') {
            // 加载经销商列表
            let params = {};
            params.key = value;
            params.pageSize = this.sheetPagination.pageSize;
            params.pageNum = this.sheetPagination.pageNum;
            await inquiryDealerList({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res;
                    let startIdx = this.addData.length;
                    this.calcPagination(total);
                    let data = rows.map((item, index) => {
                        let text = item.status;
                        let itemObj = {
                            ...item,
                            idx: startIdx + index + 1,
                            key: (Math.random() + 1).toString(36).substring(7),
                            isActive: '',
                            status: parseInt(text) === 2 ? this.contractStatus[parseInt(text) - 1] : this.contractStatus[text]
                        };
                        return itemObj;
                    });
                    this.addData.push(...data);
                }
            });
        },
        async loadTableVen(value = '') {
            // 加载厂商列表
            let params = {};
            params.key = value;
            params.pageSize = this.sheetPagination2.pageSize;
            params.pageNum = this.sheetPagination2.pageNum;

            // 排序
            params.orderByColumn = 'ilps_inner_sn';
            params.isAsc = 'desc';

            await inquiryVendorList({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res;
                    let startIdx = this.addData.length;
                    this.calcPagination2(total);
                    let data = rows.map((item, index) => {
                        let text = item.status;
                        let itemObj = {
                            ...item,
                            idx: startIdx + index + 1,
                            key: (Math.random() + 1).toString(36).substring(7),
                            isActive: '',
                            status: parseInt(text) === 2 ? this.contractStatus[parseInt(text) - 1] : this.contractStatus[text]
                        };
                        return itemObj;
                    });
                    this.addData.push(...data);
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
        openDel() {
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.delFn();
                }
            });
        },
        async delFn() {
            // 删除
            let params = {
                partA: this.currentTrObj.partA,
                ilps_inner_sn: this.currentTrObj.ilps_inner_sn,
                billsType: this.currentTrObj.billsType
            };
            const _this = this;
            await inquiryDelAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    _this.changeTabFn(_this.curTab);
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
        },
        doPanes() {
            let sysType = this.nowOrgType,
                panes = [];
            this.panes.forEach((i) => {
                if (i.permissions.includes(sysType)) {
                    panes.push(i);
                }
            });
            return panes;
        }
    },
    created() {
        this.userPageSettingsFn();
        this.nowOrgType = parseFloat(this.$cookie.get('userSystemType'));
        if (this.nowOrgType === 1) {
            // 医院
            this.tabsActiveKey = 2;
        }
        if (this.tabsActiveKey === 1) {
            this.isReply = true;
        }
        this.changeTabFn();
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
<style lang="less">
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
.add_product {
    left: calc(50% - 500px);
    top: 30px !important;
}
</style>
