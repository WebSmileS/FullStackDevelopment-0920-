<!--suppress ALL -->
<template>
    <div class="product-page">
        <div class="todo-part">
            <a-row>
                <a-col :span="12" class="btn-count"> </a-col>
                <a-col :span="12" class="align-r">
                    <a-input
                        v-if="!highSearch"
                        placeholder="产品"
                        v-model="searchData.name"
                        size="small"
                        class="input-nom"
                        style="width: 70%"
                    >
                        <a-button
                            @click="initPage()"
                            class="btn-input"
                            icon="search"
                            slot="suffix"
                            type="primary"
                        ></a-button>
                    </a-input>
                    <div class="adv-search mgl-20">
                        <a-tooltip placement="left" title="高级搜索">
                            <a-icon v-if="!highSearch" type="down-circle" @click="highSearch = true" />
                            <a-icon v-else type="up-circle" @click="highSearch = false" />
                        </a-tooltip>
                    </div>
                </a-col>
            </a-row>
            <a-row class="high-search-box pdl-10" :class="{ hide: !highSearch }">
                <a-col :span="24" class="col-list">
                    <!-- 高级搜索 -->
                    <ul class="form">
                        <li>
                            <span class="label">产品</span>
                            <a-input type="text" v-model="searchData.name" placeholder="" />
                        </li>
                        <li style="position: relative" v-if="nowSystemType != 3">
                            <span class="label">厂商</span>
                            <a-input readonly="readonly" class="input" @click="openChooseTb" v-model="vendorObj.name" />
                            <span v-if="vendorObj.name != ''" @click="clearSearchDataVenFn()" class="close">X</span>
                        </li>
                        <li>
                            <span class="label">类型</span>
                            <a-cascader
                                v-model="searchData.type_inner_sn"
                                :options="categoryList"
                                :loadData="loadCategory"
                                placeholder=""
                                :displayRender="displayRender"
                            />
                        </li>
                        <li>
                            <span class="label">产品分类</span>
                            <a-cascader
                                v-model="searchData.code68_sn"
                                :options="op68List"
                                changeOnSelect
                                :loadData="load68data"
                                placeholder=""
                                :displayRender="displayRender"
                            />
                        </li>
                        <li>
                            <a-button class="btn-nom" type="primary" @click="initPage()"> 搜索 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <div class="box">
            <div class="boxtop">
                <div style="margin-top: 0" class="table-part">
                    <vxe-grid
                        ref="xTable"
                        size="small"
                        :columns="columns"
                        :height="boxTopHeight"
                        resizable
                        auto-resize
                        border
                        highlight-current-row
                        @cell-click="cellClickFn"
                        @cell-dblclick="cellDblclickFn"
                        align="center"
                        :data="tableData"
                        :loading="pageInit.loading"
                        :start-index="(pageInit.current - 1) * pageInit.pageSize"
                    >
                    </vxe-grid>
                    <vxe-pager
                        :loading="pageInit.loading"
                        :current-page="pageInit.current"
                        :page-size="pageInit.pageSize"
                        :page-sizes="pageInit.pageSizes"
                        :total="pageInit.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changePaginationFn"
                    >
                    </vxe-pager>
                </div>
            </div>
            <div class="resize"></div>
            <div class="boxdown">
                <div
                    class="bottom-part more"
                    v-bind:style="{ height: boxDownHeight + 8 + 'px' }"
                    style="width: 100%; padding: 0px 4px 4px 4px"
                >
                    <div class="spec-box table-part" style="border: none; padding-top: 0; height: calc(100% - 8px)">
                        <div style="width: 100%">
                            <vxe-grid
                                ref="specTable"
                                size="small"
                                :columns="spec_columns_test"
                                :height="boxDownHeight - 6"
                                border
                                resizable
                                auto-resize
                                highlight-current-row
                                align="center"
                                :data="specData"
                            >
                            </vxe-grid>
                        </div>
                    </div>
                    <div class="resizeHoriz"></div>
                    <div class="tab-table-wrap minimal more2" style="height: calc(100% - 3px); margin-left: 8px">
                        <bottom-tabs-wrapper>
                            <!-- tab切换区域开始 -->
                            <a-tabs class="align-l" style="padding-top: 0; height: calc(100% - 5px)" type="card">
                                <a-tab-pane v-for="item of tabName" :key="item.key" :tab="item.name" forceRender>
                                    <div v-if="!isChooseTr" class="no-data-box">
                                        <div><a-icon type="file-excel" /></div>
                                        请选择商品
                                    </div>
                                    <template v-else>
                                        <!-- 产品图片 -->
                                        <div class="bottom-tabs-container" v-if="item.key == 'goodsPic'">
                                            <div class="scroll-vertical-wrapper">
                                                <div class="scroll-vertical-content">
                                                    <picture-box
                                                        v-for="(item, index) of goodsImgList"
                                                        :key="index"
                                                        :resourceKey="item.url"
                                                        :deleteAble="false"
                                                        @preview="previewImg($Utils.getPictureSrc(item.url))"
                                                    >
                                                        <template
                                                            :slot="`maskButtons${index}`"
                                                            v-if="item.status === 3"
                                                        >
                                                            <span
                                                                class="img_sp"
                                                                @click="imgApproval(item, item.image_inner_sn)"
                                                                >审批</span
                                                            >
                                                        </template>
                                                    </picture-box>
                                                    <div class="no-data-box" v-if="goodsImgList.length <= 0">
                                                        <div><a-icon type="file-excel" /></div>
                                                        暂无图片
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- 注册证登记表 -->
                                        <div class="bottom-tabs-container" v-if="item.key == 'regForm'">
                                            <div class="bottom-table-wrapper">
                                                <vxe-grid
                                                    ref="mdrfTable"
                                                    size="small"
                                                    border
                                                    :columns="mdrf_columns"
                                                    :height="boxDownHeight - 74"
                                                    resizable
                                                    auto-resize
                                                    highlight-current-row
                                                    align="center"
                                                    :data="mdrfData"
                                                >
                                                </vxe-grid>
                                            </div>
                                        </div>
                                    </template>
                                </a-tab-pane>
                            </a-tabs>
                        </bottom-tabs-wrapper>
                    </div>
                </div>
            </div>
        </div>
        <!-- 模态框区域 -->
        <a-modal
            title="图片预览"
            :visible="preImgSt"
            :width="previewWid"
            cancelText="关闭"
            okText="打印"
            @ok="printFn"
            @cancel="closePreview"
            :maskClosable="false"
            :centered="true"
            class="modal-form-input-scoped global-drag imgModal"
        >
            <div v-globalDrag="{ el: 'imgModal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 图片预览
                </h6>
            </div>
            <div ref="printData" class="preview-box">
                <img :src="imgSrc" />
            </div>
        </a-modal>
        <a-modal
            :title="chooseOrgMd.title"
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            @cancel="chooseOrgMd.alert = false"
            class="modal-form-input-scoped"
        >
            <choose-org-tab
                ref="chooseOrgMd"
                @chooseOrg="chooseOrgFn"
                :partType="partType"
                :pageTypeName="1"
            ></choose-org-tab>
        </a-modal>
        <a-modal
            :title="approvalGoodsMd.title"
            :visible="approvalGoodsMd.alert"
            :maskClosable="false"
            :width="900"
            :centered="true"
            :footer="null"
            @cancel="approvalGoodsMd.alert = false"
            class="modal-form-input-scoped global-drag approvalGoodsPg"
        >
            <div v-globalDrag="{ el: 'approvalGoodsPg' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ approvalGoodsMd.title }}
                </h6>
            </div>
            <approval-goods
                ref="approvalGoodsMd"
                @close="approvalGoodsMd.alert = false"
                @done="refreshGoods"
                @getInfo="sendApprovalObj"
            ></approval-goods>
        </a-modal>
    </div>
</template>

<script>
import BottomTabsWrapper from '@/components/BaseBottom/BottomTabsWrapper';
import PictureBox from '@/components/Picture/PictureBox';
import chooseOrgTab from '@/components/chooseOrgTab';
import approvalGoods from './module/approvalGoods';
import clickDebounce from '@/config/clickDebounce';

import {
    getVendorList,
    categoryGetNextListAPI,
    categoryGetParentListAPI,
    code68ParentListAPI,
    code68ChildrenListAPI,
    getUploadTokenAPI,
    productTmpListAPI,
    productTmpOtherInfoAPI,
    platformProductImgGetAPI
} from '@/service/pageAjax';

export default {
    name: 'productAudit',
    components: {
        BottomTabsWrapper,
        PictureBox,
        chooseOrgTab,
        approvalGoods
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            boxTopHeight: 300,
            boxDownHeight: 300,
            leftTitle: `${this.$route.meta.title}`,
            nowSystemType: '',
            nowOid: '',
            tableHeight: 300,
            tabScrollHeight: null,
            vendorList: [],
            categoryList: [],
            op68List: [],
            partType: 'ven',
            approvalObj: {},
            chooseOrgMd: {
                title: `${this.$route.meta.title}  - 选择机构`,
                alert: false,
                loading: false
            },
            approvalGoodsMd: {
                title: `${this.$route.meta.title}  - 审批`,
                alert: false,
                loading: false
            },
            vendorObj: {
                name: ''
            },
            searchData: {
                name: '',
                vendor_inner_sn: '', // 产商ID
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [] // 医疗器械68分类编号
            },
            upToken: '',
            upLoadparams: {},
            highSearch: false,
            preImgSt: false,
            createSt: false,
            editSt: false,
            entrySt: false,
            packUnitSt: false,
            packUnitName: '',
            mdrfIsNew: true,
            editSwitch: true,
            mdrfSwitch: true,
            uploadGicSt: false,
            openType: '',
            specSt: false,
            specTit: '',
            modalData: [], // 模态数据
            editTrObj: {
                // 修改数据
                vid: '',
                pid: '',
                vendor_inner_sn_old: [], // 厂商ID
                type_inner_sn_old: [], // 自定义分类内部编号
                code68_sn_old: [], // 医疗器械68分类编号
                storage_condition_old: 1, // 存储条件
                health_care_type_old: 0, // 医保类型
                name_old: '',
                specifications_old: '',
                health_care_sn_old: '', // 医保编号
                description_old: ''
            }, // 修改回传数据
            gicMdrfObj: {
                address: '',
                application_range: null,
                contraindication: null,
                description: null,
                images: [],
                issue_date: null,
                issue_organization: null,
                new: true,
                performance_structure: null,
                product_name: '',
                production_address: null,
                register_sn: null,
                specifications: '',
                standard: null,
                vendor_name: ''
            },
            newMdrfGicData: {},
            imgSrc: '',
            previewWid: 300, // 图片预览宽度
            pageInit: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageNum
                pageSize: 10, // 对应后端的 pageSize
                pageSizes: [10, 20, 30, 40]
            },
            rowSelection: {
                hideDefaultSelections: false
            },
            chooseTrObj: {}, // 已选择对象
            isChooseTr: false,
            columns: [
                {
                    title: '产品',
                    dataIndex: 'name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    width: '280px',
                    slots: {
                        default: ({ row }) => {
                            return [row.name];
                        }
                    }
                },
                {
                    title: '厂商',
                    dataIndex: 'vendor_name',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '230px',
                    slots: {
                        default: ({ row }) => {
                            return [row.vendor_name];
                        }
                    }
                },
                {
                    title: '类型',
                    dataIndex: 'type_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    width: '150px',
                    slots: {
                        default: ({ row }) => {
                            return [row.type_name];
                        }
                    }
                },
                {
                    id: 4,
                    title: '产品分类',
                    dataIndex: 'text_68',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    width: '200px',
                    slots: {
                        default: ({ row }) => {
                            return [row.text_68];
                        }
                    }
                },
                {
                    title: '来自',
                    dataIndex: 'sourceName',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    width: '100px',
                    slots: {
                        default: ({ row }) => {
                            return [row.sourceName];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    width: '100px',
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            if (parseInt(row.status) === 1) {
                                val = '有效';
                                auditSt = 'color: #16a935';
                            }
                            if (parseInt(row.status) === 0) {
                                auditSt = 'color: red';
                                val = '无效';
                            }
                            if (parseInt(row.status) === 2) {
                                auditSt = 'color: #999';
                                val = '草稿';
                            }
                            if (parseInt(row.status) === 3) {
                                auditSt = 'color: #eb981c';
                                val = '待审核';
                            }
                            if (parseInt(row.status) === 4) {
                                auditSt = 'color: red';
                                val = '不通过';
                            }
                            return [<span style={auditSt}>{val}</span>];
                        }
                    }
                },
                {
                    title: '审核级别',
                    dataIndex: 'audit_level',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    width: '100px',
                    slots: {
                        default: ({ row }) => {
                            return [row.audit_level];
                        }
                    }
                },
                {
                    title: '方式',
                    dataIndex: 'operation',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '80px',
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            let text = row.operation;
                            if (parseInt(text) === 0) {
                                val = '删除';
                                auditSt = 'color: #red';
                            }
                            if (parseInt(text) === 1) {
                                auditSt = 'color: #16a935';
                                val = '新增';
                            }
                            if (parseInt(text) === 2) {
                                auditSt = 'color: #eb981c';
                                val = '修改';
                            }
                            return [<span style={auditSt}>{val}</span>];
                        }
                    }
                },
                {
                    title: '操作',
                    dataIndex: 'status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    width: '100px',
                    slots: {
                        default: ({ row }) => {
                            let text = row.status;
                            let func = () => {
                                this.approvalObj = row;
                                this.openApproval(0);
                            };
                            let html;
                            if (text !== 3) {
                                html = '';
                            } else {
                                html = [
                                    <a-button style="height: 22px" onClick={func} type="primary">
                                        审批
                                    </a-button>
                                ];
                            }
                            return html;
                        }
                    }
                }
            ],
            tableData: [],
            tabName: [
                {
                    name: '产品图片',
                    key: 'goodsPic'
                },
                {
                    name: '医疗器械注册证（登记表）',
                    key: 'regForm'
                }
            ],
            sourceName: ['平台', '医院', '经销商', '厂商'],
            previewVisible: false,
            previewImage: '',
            createDataObj: [],
            newEditDataObj: [],
            goodsImgList: [],
            certImgList: [],
            certObj: {
                begin_date: '',
                end_date: ''
            },
            mdrfImgList: [],
            mdrf_columns: [
                {
                    title: '注册号',
                    dataIndex: 'register_sn',
                    filed: 'register_sn',
                    align: 'center',
                    minWidth: '300',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.register_sn];
                        }
                    }
                },
                {
                    title: '开始日期',
                    dataIndex: 'begin_date',
                    filed: 'begin_date',
                    align: 'center',
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            return [row.begin_date];
                        }
                    }
                },
                {
                    title: '截止日期',
                    dataIndex: 'end_date',
                    filed: 'end_date',
                    align: 'center',
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            return [row.end_date];
                        }
                    }
                },
                {
                    title: '发证部门',
                    dataIndex: 'issue_organization',
                    filed: 'issue_organization',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    minWidth: '150',
                    slots: {
                        default: ({ row }) => {
                            return [row.issue_organization];
                        }
                    }
                },
                {
                    title: '颁发日期',
                    dataIndex: 'issue_date',
                    filed: 'issue_date',
                    align: 'center',
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            return [row.issue_date];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            if (parseInt(row.status) === 1) {
                                val = '有效';
                                auditSt = 'color: #16a935';
                            }
                            if (parseInt(row.status) === 0) {
                                auditSt = 'color: red';
                                val = '无效';
                            }
                            if (parseInt(row.status) === 2) {
                                auditSt = 'color: #999';
                                val = '草稿';
                            }
                            if (parseInt(row.status) === 3) {
                                auditSt = 'color: #eb981c';
                                val = '待审核';
                            }
                            if (parseInt(row.status) === 4) {
                                auditSt = 'color: red';
                                val = '不通过';
                            }
                            return [<span style={auditSt}>{val}</span>];
                        }
                    }
                },
                {
                    title: '操作',
                    dataIndex: 'status',
                    align: 'center',
                    minWidth: '100',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            let text = row.status;
                            let func = () => {
                                this.approvalObj = row;
                                this.approvalObj.mdrf_inner_sn = row.mdrf_inner_sn;
                                this.openApproval(2);
                            };
                            let html;
                            if (text !== 3) {
                                html = '==';
                            } else {
                                html = [
                                    <a-button style="height: 22px" onClick={func} type="primary">
                                        审批
                                    </a-button>
                                ];
                            }
                            return html;
                        }
                    }
                }
            ],
            mdrfData: [],
            chooseTrMdrfObj: '',
            examImgList: [],
            otherImgList: [],
            userSystemType: parseFloat(this.$cookie.get('userSystemType')),
            spec_columns_test: [
                // 平台
                {
                    title: '产品编码',
                    dataIndex: 'specification_vendor_sn',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification_vendor_sn];
                        }
                    }
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '150',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            let text = row.status;
                            if (parseInt(text) === 1) {
                                val = '有效';
                                auditSt = 'color: #16a935';
                            }
                            if (parseInt(text) === 0) {
                                auditSt = 'color: red';
                                val = '无效';
                            }
                            if (parseInt(text) === 2) {
                                auditSt = 'color: #999';
                                val = '草稿';
                            }
                            if (parseInt(text) === 3) {
                                auditSt = 'color: #eb981c';
                                val = '待审核';
                            }
                            if (parseInt(text) === 4) {
                                auditSt = 'color: red';
                                val = '不通过';
                            }
                            return [<span style={auditSt}>{val}</span>];
                        }
                    }
                },
                {
                    title: '审核级别',
                    dataIndex: 'audit_level',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '70',
                    slots: {
                        default: ({ row }) => {
                            return [row.audit_level];
                        }
                    }
                },
                {
                    title: '方式',
                    dataIndex: 'operation',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            let text = row.operation;
                            if (parseInt(text) === 0) {
                                val = '删除';
                                auditSt = 'color: #red';
                            }
                            if (parseInt(text) === 1) {
                                auditSt = 'color: #16a935';
                                val = '新增';
                            }
                            if (parseInt(text) === 2) {
                                auditSt = 'color: #eb981c';
                                val = '修改';
                            }
                            return [<span style={auditSt}>{val}</span>];
                        }
                    }
                },
                {
                    title: '操作',
                    dataIndex: 'status',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            let text = row.status;
                            let func = () => {
                                this.approvalObj = row;
                                this.approvalObj.specification_inner_sn = row.specification_inner_sn;
                                this.openApproval(1);
                            };
                            let html;
                            if (text !== 3) {
                                html = '==';
                            } else {
                                html = [
                                    <a-button style="height: 22px" onClick={func} type="primary">
                                        审批
                                    </a-button>
                                ];
                            }
                            return html;
                        }
                    }
                }
            ],
            spec_columns: [
                // 其它端
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    align: 'center'
                }
            ],
            specData: [],
            chooseTrSpecObj: {},
            newSpec: '',
            editSpec: '',
            newPvd: '',
            editPvd: '',
            isEdit: false,
            packUnitObj: {},
            switchType: true,
            btnDisabled: true,
            isSend: false
        };
    },
    methods: {
        dragControllerDiv: function () {
            let that = this;
            let resize = document.getElementsByClassName('resize');
            let boxtop = document.getElementsByClassName('boxtop');
            let mid = document.getElementsByClassName('boxdown');
            let box = document.getElementsByClassName('box');
            for (let i = 0; i < resize.length; i++) {
                // 鼠标按下事件
                resize[i].onmousedown = function (e) {
                    let startY = e.clientY;
                    resize[i].top = resize[i].offsetTop;
                    // 鼠标拖动事件
                    document.onmousemove = function (e) {
                        let endY = e.clientY;
                        let moveLen = resize[i].top + (endY - startY); // （endx-startx）=移动的距离。resize[i].left+移动的距离=左边区域最后的宽度
                        let maxT = box[i].clientHeight - resize[i].offsetHeight;
                        if (moveLen < 200) moveLen = 200; // 左边区域的最小宽度为200px
                        if (moveLen > maxT - 200) moveLen = maxT - 200; // 右边区域最小宽度为200px
                        resize[i].style.top = moveLen; // 设置左侧区域的宽度
                        that.boxTopHeight = moveLen - 56;
                        that.boxDownHeight = box[i].clientHeight - moveLen - 18;
                        for (let j = 0; j < boxtop.length; j++) {
                            boxtop[j].style.height = moveLen + 'px';
                            mid[j].style.height = box[i].clientHeight - moveLen - 18 + 'px';
                        }
                    };
                    // 鼠标松开事件
                    document.onmouseup = function () {
                        document.onmousemove = null;
                        document.onmouseup = null;
                        resize[i].releaseCapture && resize[i].releaseCapture();
                    };
                    resize[i].setCapture && resize[i].setCapture();
                    return false;
                };
            }
        },
        dragControllerDivHoriz: function () {
            let that = this;
            let res = document.getElementsByClassName('resizeHoriz');
            let boxtop = document.getElementsByClassName('spec-box table-part');
            let mid = document.getElementsByClassName('tab-table-wrap minimal more2');
            let box = document.getElementsByClassName('bottom-part more');
            for (let i = 0; i < res.length; i++) {
                // 鼠标按下事件
                res[i].onmousedown = function (e) {
                    let startX = e.clientX;
                    res[i].left = res[i].offsetLeft;
                    // 鼠标拖动事件
                    document.onmousemove = function (e) {
                        let endX = e.clientX;
                        let moveLen = res[i].left + (endX - startX); // （endx-startx）=移动的距离。resize[i].left+移动的距离=左边区域最后的宽度
                        let maxT = box[i].clientWidth - res[i].offsetWidth;
                        if (moveLen < 380) moveLen = 380; // 左边区域的最小宽度为200px
                        if (moveLen > maxT - 350) moveLen = maxT - 350; // 右边区域最小宽度为200px
                        res[i].style.left = moveLen; // 设置左侧区域的宽度
                        that.boxLeftWidth = moveLen - 56;
                        that.boxRightWidth = box[i].clientWidth - moveLen - 18;
                        for (let j = 0; j < boxtop.length; j++) {
                            boxtop[j].style.width = moveLen + 'px';
                            mid[j].style.width = box[i].clientWidth - moveLen - 18 + 'px';
                        }
                    };
                    // 鼠标松开事件
                    document.onmouseup = function () {
                        document.onmousemove = null;
                        document.onmouseup = null;
                        res[i].releaseCapture && res[i].releaseCapture();
                    };
                    res[i].setCapture && res[i].setCapture();
                    return false;
                };
            }
        },
        openChooseTb() {
            // 打开选择厂商
            this.chooseOrgMd.alert = true;
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
        },
        clearSearchDataVenFn() {
            // 清空选择
            this.vendorObj.name = '';
            this.searchData.vendor_inner_sn = '';
        },
        async chooseOrgFn(org) {
            // 获取选中机构信息
            this.vendorObj.name = org.org_name;
            this.searchData.vendor_inner_sn = org.org_id;
            this.chooseOrgMd.alert = false;
        },
        async getVendorListFn() {
            // 获取厂商
            await getVendorList().then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.vendorList = res.list;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        changeVendor(val) {
            // 选择厂商
            this.searchData.vendor_inner_sn = val;
        },
        async getCategoryListFn() {
            // 获取自定义类型列表
            await categoryGetParentListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    let newArray = [];
                    newArray = rows.map((item) => {
                        let st;
                        if (item.is_leaf === 1) {
                            st = true;
                        } else if (item.is_leaf === 0) {
                            st = false;
                        }
                        item.label = item.type;
                        item.value = item.type_inner_sn;
                        item.isLeaf = st;
                        return item;
                    });
                    this.categoryList = newArray;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async loadCategory(options) {
            // 加载自定义类型列表子集
            const targetOption = options[options.length - 1];
            targetOption.loading = true;
            const id = targetOption.value;
            await categoryGetNextListAPI(id).then((res) => {
                targetOption.loading = false;
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    if (rows.length > 0) {
                        targetOption.children = rows.map((item) => {
                            let st;
                            if (item.is_leaf === 1) {
                                st = true;
                            } else if (item.is_leaf === 0) {
                                st = false;
                            }
                            item.label = item.type;
                            item.value = item.type_inner_sn;
                            item.isLeaf = st;
                            return item;
                        });
                    } else {
                        targetOption.isLeaf = true;
                    }
                    targetOption.loading = false;
                    this.categoryList = [...this.categoryList];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async get68ParentListFn() {
            // 获取68分类
            await code68ParentListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    let newArray = [];
                    rows.forEach((item) => {
                        let st;
                        if (item.is_leaf === 1) {
                            st = true;
                        } else if (item.is_leaf === 0) {
                            st = false;
                        }
                        let obj = {
                            label: '(' + item.code68_sn + ') ' + item.name,
                            value: item.code68_sn,
                            isLeaf: st
                        };
                        newArray.push(obj);
                    });
                    this.op68List = newArray;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async load68data(options) {
            // 加载68分
            const targetOption = options[options.length - 1];
            targetOption.loading = true;
            const id = targetOption.value;
            await code68ChildrenListAPI(id).then((res) => {
                targetOption.loading = false;
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    if (rows.length > 0) {
                        targetOption.children = rows.map((item) => {
                            let st;
                            if (item.is_leaf === 1) {
                                st = true;
                            } else if (item.is_leaf === 0) {
                                st = false;
                            }
                            item.label = '(' + item.code68_sn + ') ' + item.name;
                            item.value = item.code68_sn;
                            item.isLeaf = st;
                            return item;
                        });
                    } else {
                        targetOption.isLeaf = true;
                    }
                    targetOption.loading = false;
                    this.op68List = [...this.op68List];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        displayRender(obj) {
            let len = obj.labels.length;
            const label = obj.labels[len - 1];
            return label;
        },
        async getUploadTokenAPIFn() {
            // 获取上传 token
            await getUploadTokenAPI().then((res) => {
                const { code, data } = res;
                if (parseFloat(code) === 0) {
                    this.upToken = data;
                    this.upLoadparams = {
                        token: this.upToken
                    };
                } else {
                    this.upToken = '';
                    this.$message.error(res.msg);
                }
            });
        },
        getCurrentStyle(current) {
            const style = {};
            if (current.date() === 1) {
                style.border = '1px solid #1890ff';
                style.borderRadius = '50%';
            }
            return style;
        },
        imgApproval(obj, id) {
            this.approvalObj = obj;
            this.approvalObj.image_inner_sn = id;
            this.openApproval(3);
        },
        openApproval(type) {
            // 打开审批
            if (type !== 0) {
                if (this.chooseTrObj.status !== 1 && this.chooseTrObj.operation !== 2) {
                    this.$warning({
                        title: '操作提示',
                        content: '请先审核产品',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
            }
            this.approvalObj.audit_type = type;
            this.approvalGoodsMd.alert = true;
            let cmp = this.$refs.approvalGoodsMd;
            this.$Utils.globalDragCenterFn('create');
            if (cmp) {
                this.$Utils.globalDragCenterFn('approvalGoodsPg');
                cmp.getInfo();
            } else {
                this.$Utils.globalDragCenterFn('approvalGoodsPg', true);
            }
        },
        sendApprovalObj() {
            this.$refs.approvalGoodsMd.initPage(this.approvalObj);
        },
        // 单击行
        cellClickFn: clickDebounce.debounce(function (row) {
            let obj = row.row || row;
            if (this.chooseTrObj === obj) return;
            this.chooseTrEvent(obj);
        }),
        cellDblclickFn() {
            // this.chooseTrEvent(row, true)
            // this.openEdit()
        },
        chooseTrEvent(record) {
            // 选中事件
            this.isChooseTr = true;
            this.chooseTrObj = record;
            if (record.status === 2) {
                this.btnDisabled = false;
            } else {
                this.btnDisabled = true;
            }
            if (record.status === 3) {
                this.isSend = true;
            } else {
                this.isSend = false;
            }
            this.chooseTrSpecObj = {};
            if (record.status === 2 || record.status === 4) {
                this.btnDisabled = false;
            }
            // this.chooseTr(record.vendor_inner_sn, record.product_inner_sn)
            let typeInnerSn = this.chooseTrObj.type_inner_sn;
            if (typeInnerSn && typeInnerSn.length > 0) {
                let index = this.chooseTrObj.type_inner_sn.length - 1;
                this.chooseTrObj.type_inner_sn = this.chooseTrObj.type_inner_sn[index];
            }
            let code68Sn = this.chooseTrObj.code68_sn;
            if (code68Sn && code68Sn.length > 0) {
                let index = this.chooseTrObj.code68_sn.length - 1;
                this.chooseTrObj.code68_sn = this.chooseTrObj.code68_sn[index];
            }
            this.getProductInfo(record);
            setTimeout(() => {
                this.$refs.xTable.setCurrentRow(this.chooseTrObj); // 默认选中
            }, 300);
        },
        chooseTr(vid, pid) {
            // 选中行渲染
            this.isChooseTr = true;
            this.tableData.forEach((item) => {
                if (item.vendor_inner_sn === vid && item.product_inner_sn === pid) {
                    item.isActive = 'active';
                    this.chooseTrObj = item;
                } else {
                    item.isActive = '';
                }
            });
        },
        initPage() {
            this.pageInit.current = 1;
            this.loadTable();
        },
        async loadTable(chooseObj) {
            if (this.$Utils.checkInputValFn(this.searchData.name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                this.pageInit.loading = false;
                return false;
            }
            this.approvalGoodsMd.alert = false;
            this.pageInit.loading = true;
            let params = JSON.parse(JSON.stringify(this.searchData));
            if (params.type_inner_sn.length > 0 && this.highSearch) {
                params.type_inner_sn = params.type_inner_sn[params.type_inner_sn.length - 1];
            } else {
                params.type_inner_sn = '';
            }
            if (params.code68_sn.length > 0 && this.highSearch) {
                params.code68_sn = params.code68_sn[params.code68_sn.length - 1];
            } else {
                params.code68_sn = '';
            }
            if (!this.highSearch) {
                params.vendor_inner_sn = '';
            }
            if (parseInt(this.nowSystemType) === 3) {
                params.vendor_inner_sn = this.nowOid;
            }
            params.pageSize = this.pageInit.pageSize;
            params.pageNum = this.pageInit.current;

            // 排序
            params.orderByColumn = 'product_inner_sn';
            params.isAsc = 'desc';

            await productTmpListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.pageInit.loading = false;
                    if (res.rows.length === 0) {
                        this.isChooseTr = false;
                        this.chooseTrObj = {};
                    }
                    this.tableData = res.rows.map((item) => {
                        item.isActive = '';
                        item.key = item.vendor_inner_sn + '' + item.product_inner_sn + '' + item.source;
                        if (item.code68_name) {
                            item.text_68 = item.code68_name + '(' + item.code68_sn + ')';
                        }
                        item.sourceName = this.sourceName[parseFloat(item.data_source) || parseFloat(item.source)];
                        return item;
                    });
                    let total = res.total;
                    this.pageInit = {
                        ...this.pageInit,
                        total
                    };
                    // this.chooseTrObj = {}
                    if (this.tableData.length > 0) {
                        if (chooseObj) {
                            // 选中新增/编辑的项
                            this.tableData.forEach((item) => {
                                if (item.key === this.chooseTrObj.key) {
                                    this.$refs.xTable.setCurrentRow(item); // 默认选中
                                    this.cellClickFn(item);
                                }
                            });
                        } else {
                            this.$refs.xTable.setCurrentRow(this.tableData[0]); // 默认选中第一
                            this.cellClickFn(this.tableData[0]);
                        }
                    } else {
                        this.specData = [];
                        this.goodsImgList = [];
                        this.certImgList = [];
                        this.mdrfImgList = [];
                        this.examImgList = [];
                        this.mdrfData = [];
                    }
                    this.$refs.xTable.refreshColumn(); // 防止错位
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        changePaginationFn({ currentPage, pageSize }) {
            this.pageInit.current = currentPage;
            this.pageInit.pageSize = pageSize;
            this.pageInit.loading = true;
            this.loadTable();
            this.isChooseTr = false;
        },
        async getProductInfo(obj, isDone) {
            let params = {
                org_id: obj.operation_organization,
                source: obj.source,
                vendor_inner_sn: obj.vendor_inner_sn,
                product_inner_sn: obj.product_inner_sn
            };
            await productTmpOtherInfoAPI({ params }).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.specData = res.info.tmpSpecList;
                    this.goodsImgList = res.info.tmpImageList;
                    this.mdrfData = res.info.tmpMDRFeList;
                    if (isDone) {
                        if (
                            this.specData.length === 0 &&
                            this.goodsImgList.length === 0 &&
                            this.mdrfData.length === 0
                        ) {
                            this.loadTable();
                        }
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        previewImg(src) {
            // 图片预览
            let that = this;
            if (src && src !== '') {
                let img = new Image();
                img.src = src;
                img.onload = function () {
                    that.previewWid = img.width + 50;
                    let orderWidth = document.body.clientWidth; // 最大不能超过屏幕宽度
                    if (that.previewWid > orderWidth) {
                        that.previewWid = orderWidth - 246;
                    }
                    if (that.previewWid < 300) {
                        that.previewWid = 300;
                    }
                    that.preImgSt = true;
                    that.imgSrc = src;
                    that.$Utils.globalDragCenterFn('imgModal');
                };
            }
        },
        closePreview() {
            this.preImgSt = false;
        },
        handleCancel() {
            this.previewVisible = false;
        },
        handlePreview(file) {
            this.previewImage = file.url || file.thumbUrl;
            this.previewVisible = true;
        },
        async getGoodsImg() {
            // 获取商品图片
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductImgGetAPI(vid, pid, 1).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.goodsImgList = res.images;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /** 注册证方法区 **/
        customRowMdrf(record) {
            // 自定义行
            return {
                class: record.isActive,
                on: {
                    // 事件
                    click: () => {
                        // 点击行
                        this.chooseTrMdrfObj = record;
                        // this.chooseTrMdrf(record.key)
                    },
                    dblclick: () => {
                        this.chooseTrMdrfObj = record;
                        this.chooseTrMdrf(record.key);
                    }
                }
            };
        },
        chooseTrMdrf(id) {
            // 选中行渲染
            this.mdrfData.forEach((item) => {
                if (item.key === id) {
                    item.isActive = 'active';
                } else {
                    item.isActive = '';
                }
            });
        },
        /** 注册证方法区end **/
        printFn() {
            // 打印
            let printContent = {
                printData: this.$refs.printData.innerHTML,
                printType: 1 // printType  1、图片；2、耗材申领；3、出入库单; 4、询价单
            };
            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        customRowSpec(record) {
            // 自定义行
            return {
                class: record.isActive,
                on: {
                    // 事件
                    click: () => {
                        // 点击行
                        this.chooseTrSpecObj = record;
                        let vid = this.chooseTrObj.vendor_inner_sn;
                        let pid = this.chooseTrObj.product_inner_sn;
                        let id = this.chooseTrSpecObj.specification_inner_sn;
                        let infoData = {
                            vid: vid,
                            pid: pid,
                            psid: id
                        };
                        this.packUnitObj = infoData;
                        this.chooseTrSpec(record.specification_inner_sn);
                    },
                    dblclick: () => {
                        this.chooseTrSpec(record.specification_inner_sn);
                    }
                }
            };
        },
        chooseTrSpec(id) {
            // 选中行渲染
            this.specData.forEach((item) => {
                if (item.specification_inner_sn === id) {
                    item.isActive = 'active';
                } else {
                    item.isActive = '';
                }
            });
        },
        autoTabHeight() {
            // 自动计算表格高度
            let orderHeight = document.body.clientHeight - 48 - 56 - 256 - 6;
            this.tableHeight = orderHeight;
            this.tabScrollHeight = this.tableHeight - 100;
        },
        refreshGoods(type, auditType) {
            if (type === 2 && auditType === 0) {
                this.loadTable();
            } else {
                this.chooseTrObj.status = 1;
                this.approvalGoodsMd.alert = false;
                this.getProductInfo(this.chooseTrObj, true);
            }
        }
    },
    computed: {
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                pager = 115;
            return (clientHeight - pager) / 2;
        }
    },
    watch: {
        highSearch(val) {
            let box = document.getElementsByClassName('box');
            if (val) {
                this.boxTopHeight -= 38;
                for (let i = 0; i < box.length; i++) {
                    document.getElementsByClassName('box')[i].style.top = '98px';
                    document.getElementsByClassName('boxtop')[i].style.height = this.boxTopHeight + 55 + 'px';
                }
            } else {
                this.boxTopHeight += 38;
                for (let i = 0; i < box.length; i++) {
                    document.getElementsByClassName('box')[i].style.top = '60px';
                    document.getElementsByClassName('boxtop')[i].style.height = this.boxTopHeight + 55 + 'px';
                }
            }
        }
    },
    mounted() {
        this.nowSystemType = this.$cookie.get('userSystemType');
        this.nowOid = this.$cookie.get('userbelong');
        if (parseInt(this.nowSystemType) === 3) {
            this.columns.splice(1, 1);
        }
        this.getUploadTokenAPIFn();
        this.loadTable();
        this.getCategoryListFn();
        this.get68ParentListFn();
        this.autoTabHeight();
        this.boxTopHeight = this.scrollYNumber - 56;
        this.boxDownHeight = this.scrollYNumber - 12;
        this.dragControllerDiv();
        this.dragControllerDivHoriz();
    }
};
</script>

<style scoped lang="less">
@import '../../../../public/style/ptmStyle1';
.upload-count {
    .em {
        font-size: 22px;
        vertical-align: middle;
        margin-right: 3px;
        float: left;
        height: 20px;
        line-height: 27px;
    }
}
.img_sp {
    position: absolute;
    top: 22px;
    right: 12px;
    display: inline-block;
    border: 1px solid #fff;
    border-radius: 5px;
    padding: 0 4px;
    vertical-align: middle;
    margin-top: -10px;
}
</style>
<style scoped>
.box {
    width: 100%;
    top: 60px;
    bottom: 0px;
    overflow: hidden;
    position: absolute;
    background: #ffffff;
}
.boxtop {
    /*width:calc(30% - 10px);*/
    width: 100%;
    padding: 8px;
    height: calc(50% - 3px);
    float: left;
}
.resize {
    width: 100%;
    height: 5px;
    cursor: w-resize;
    cursor: s-resize;
    float: left;
    border-bottom: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
}
.resizeHoriz {
    width: 5px;
    height: 100%;
    margin-left: 8px;
    cursor: n-resize;
    cursor: e-resize;
    float: left;
    border-left: 1px solid #e8e8e8;
    border-right: 1px solid #e8e8e8;
}
.boxdown {
    float: left;
    width: 100%;
    height: 50%;
}
</style>
