<!--suppress ALL -->
<template>
    <div class="product-page">
        <div class="todo-part">
            <a-row>
                <a-col :span="12" class="btn-count">
                    <a-button class="btn-nom mgr-10" @click="openCreate" type="primary">新增</a-button>
                    <a-button class="btn-nom mgr-10" type="primary" @click="openEdit">修改</a-button>
                    <a-button class="btn-nom mgr-10 cut" @click="deleteBtn" type="danger">删除</a-button>
                    <a-button class="btn-nom" type="primary">导入</a-button>
                </a-col>
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
            <a-row class="high-search-box" :class="{ hide: !highSearch }">
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
            <!-- tab切换区域结束 -->
            <div class="boxdown">
                <div
                    class="tab-table-wrap more"
                    v-bind:style="{ height: boxDownHeight + 8 + 'px' }"
                    style="width: 100%; padding: 4px 8px 0px 8px"
                >
                    <bottom-tabs-wrapper>
                        <!-- tab切换区域开始 -->
                        <a-tabs class="align-l" style="padding-top: 0; height: calc(100% - 10px)" type="card">
                            <a-tab-pane v-for="item of tabName" :key="item.key" :tab="item.name" forceRender>
                                <div v-if="!isChooseTr" class="no-data-box">
                                    <div><a-icon type="file-excel" /></div>
                                    请选择商品
                                </div>
                                <template v-else>
                                    <!-- 产品图片 -->
                                    <div class="bottom-tabs-container" v-if="item.key == 'goodsPic'">
                                        <div class="bottom-tabs-content-left bottom-buttons-vertical-wrapper">
                                            <a-upload
                                                :action="QiniuUrl"
                                                :show-upload-list="false"
                                                :multiple="true"
                                                :beforeUpload="checkFile"
                                                :supportServerRender="true"
                                                :data="upLoadparams"
                                                @change="upGoodsImg"
                                            >
                                                <div>
                                                    <a-button class="btn-nom mgr-10" type="primary">上传图片</a-button>
                                                </div>
                                            </a-upload>
                                        </div>
                                        <div class="bottom-tabs-content-right bottom-pictures-wrapper">
                                            <bottom-pictures
                                                :list="goodsImgList"
                                                @previewImg="previewImg"
                                                @delete="deleteImg"
                                            />
                                        </div>
                                    </div>
                                    <!-- 注册证登记表 -->
                                    <div class="bottom-tabs-container" v-if="item.key == 'regForm'">
                                        <div class="bottom-tabs-content-left bottom-buttons-vertical-wrapper">
                                            <a-button @click="openGicMdrf" class="btn-nom mgr-10" type="primary">
                                                新增
                                            </a-button>
                                            <a-button @click="editGicMdrf" class="btn-nom mgr-10" type="primary">
                                                修改
                                            </a-button>
                                            <a-button @click="delGicMdrf" class="btn-nom mgr-10" type="danger">
                                                删除
                                            </a-button>
                                        </div>
                                        <div
                                            class="bottom-tabs-content-right bottom-table-wrapper"
                                            style="margin-top: 0px"
                                        >
                                            <vxe-grid
                                                ref="mdrfTable"
                                                size="small"
                                                :columns="mdrf_columns"
                                                :height="boxDownHeight - 78"
                                                resizable
                                                border
                                                auto-resize
                                                highlight-current-row
                                                @cell-click="mdrfClickFn"
                                                @cell-dblclick="mrdfDblclickFn"
                                                align="center"
                                                :data="mdrfData"
                                            >
                                            </vxe-grid>
                                        </div>
                                    </div>
                                    <!-- 质量检测报告 -->
                                    <div class="bottom-tabs-container" v-if="item.key === 'test'">
                                        <bottom-pictures
                                            :list="examImgList"
                                            @previewImg="previewImg"
                                            @delete="deleteImg"
                                        />
                                    </div>
                                    <!-- 其他 -->
                                    <div class="bottom-tabs-container" v-if="item.key == 'other'">
                                        <div class="bottom-tabs-content-left bottom-buttons-vertical-wrapper">
                                            <a-upload
                                                :action="QiniuUrl"
                                                :show-upload-list="false"
                                                :multiple="true"
                                                :beforeUpload="checkFile"
                                                :supportServerRender="true"
                                                :data="upLoadparams"
                                                @change="upOtherImg"
                                            >
                                                <div>
                                                    <a-button class="btn-nom mgr-10" type="primary">上传图片</a-button>
                                                </div>
                                            </a-upload>
                                        </div>
                                        <div class="bottom-tabs-content-right bottom-pictures-wrapper">
                                            <bottom-pictures
                                                :list="otherImgList"
                                                @previewImg="previewImg"
                                                @delete="deleteImg"
                                            />
                                        </div>
                                    </div>
                                </template>
                            </a-tab-pane>
                        </a-tabs>
                    </bottom-tabs-wrapper>
                </div>
            </div>
        </div>
        <!-- 模态框区域 -->
        <a-modal
            title="产品初建 - 图片预览"
            :visible="preImgSt"
            :width="previewWid"
            :maskClosable="false"
            :centered="true"
            okText="打印"
            cancelText="关闭"
            @ok="printFn"
            @cancel="closePreview"
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
            title="产品初建 - 新增"
            :maskClosable="false"
            :visible="createSt"
            :centered="true"
            :width="900"
            okText="保存"
            @cancel="createSt = false"
            @ok="saveGoodsFn"
            class="modal-form-input-scoped global-drag create"
        >
            <div v-globalDrag="{ el: 'create' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 新增
                </h6>
            </div>
            <create-cp ref="createPage" @getData="getCpmData"></create-cp>
        </a-modal>
        <a-modal
            title="产品初建 - 修改"
            :maskClosable="false"
            :visible="editSt"
            :centered="true"
            :width="900"
            okText="保存"
            @cancel="editSt = false"
            @ok="editGoodsFn"
            class="modal-form-input-scoped global-drag edit"
        >
            <div v-globalDrag="{ el: 'edit' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 修改
                </h6>
            </div>
            <edit-cp ref="editPage" :modalData="editTrObj" @getData="getEditData" @getEditInfo="sendEditInfo"></edit-cp>
            <!--:modalData="editTrObj"-->
        </a-modal>
        <a-modal
            title="录入"
            :maskClosable="false"
            :visible="entrySt"
            :centered="true"
            :width="1150"
            @cancel="entrySt = false"
            class="modal-form-input-scoped global-drag entryModal"
        >
            <div v-globalDrag="{ el: 'entryModal' }" class="modal-header-wrap">
                <h6 v-if="mdrfIsNew">
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 医疗器械注册证（登记表） - 新增
                </h6>
                <h6 v-else>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 医疗器械注册证（登记表） - 修改
                </h6>
            </div>
            <entry-gic
                ref="mdrfGicPage"
                @getData="getMdrfGicData"
                @returnOtherCertImgs="getOtherCertImgsForPrint"
                :gicMdrfObj="gicMdrfObj"
            ></entry-gic>
            <template slot="footer">
                <a-button @click="entrySt = false">
                    <span>取消</span>
                </a-button>
                <a-button @click="handlePrint">
                    <span>打印</span>
                </a-button>
                <a-button type="primary" @click="entryGicMdrf">
                    <span>保存</span>
                </a-button>
            </template>
        </a-modal>
        <a-modal
            :title="chooseOrgMd.title"
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            @cancel="chooseOrgMd.alert = false"
            class="modal-form-input-scoped global-drag ct_choose_org_2"
        >
            <div v-globalDrag="{ el: 'ct_choose_org_2' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <choose-org-tab
                ref="chooseOrgMd"
                @chooseOrg="chooseOrgFn"
                :partType="partType"
                :pageTypeName="1"
            ></choose-org-tab>
        </a-modal>
    </div>
</template>

<script>
import BottomTabsWrapper from '@/components/BaseBottom/BottomTabsWrapper';
import BottomPictures from '@/components/BaseBottom/BottomPictures';
import createCp from './module/createGoods';
import editCp from './module/editGoods';
import entryGic from './module/entryGic';
import chooseOrgTab from '@/components/chooseOrgTab';

import {
    getVendorList,
    categoryGetNextListAPI,
    categoryGetParentListAPI,
    code68ParentListAPI,
    code68ChildrenListAPI,
    getUploadTokenAPI,
    platformProductAdd,
    platformProductListAPI,
    platformProductDelAPI,
    platformProductEditAPI,
    platformProductImgAddAPI,
    platformProductImgGetAPI,
    platformProductMdrfGet,
    platformProductMdrfAdd,
    platformProductMdrfEdit,
    platformProductMdrfDel,
    platformProductMdrfInfo,
    platformProductImgDelAPI
} from '@/service/pageAjax';
import clickDebounce from '@/config/clickDebounce';

export default {
    name: 'productBuild',
    components: {
        BottomPictures,
        BottomTabsWrapper,
        createCp,
        editCp,
        entryGic,
        chooseOrgTab
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            boxTopHeight: 300,
            boxDownHeight: 300,
            leftTitle: `${this.$route.meta.title}`,
            nowSystemType: '',
            tableHeight: 300,
            tabScrollHeight: null,
            vendorList: [],
            categoryList: [],
            op68List: [],
            partType: 'ven',
            chooseOrgMd: {
                title: `${this.$route.meta.title}  - 选择机构`,
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
            mdrfIsNew: true,
            editSwitch_1: false,
            editSwitch_2: false,
            mdrfSwitch: true,
            uploadGicSt: false,
            openType: '',
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
                application_range: '',
                contraindication: '',
                description: '',
                images: [],
                issue_date: '',
                issue_organization: '',
                new: true,
                performance_structure: '',
                product_name: '',
                production_address: '',
                register_sn: '',
                specifications: '',
                standard: '',
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
                    filed: 'name',
                    dataIndex: 'name',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '200',
                    slots: {
                        default: ({ row }) => {
                            return [row.name];
                        }
                    }
                },
                {
                    title: '厂商',
                    filed: 'vendor_name',
                    dataIndex: 'vendor_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    minWidth: '220',
                    slots: {
                        default: ({ row }) => {
                            return [row.vendor_name];
                        }
                    }
                },
                {
                    title: '规格型号',
                    filed: 'specifications',
                    dataIndex: 'specifications',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '220',
                    slots: {
                        default: ({ row }) => {
                            return [row.specifications];
                        }
                    }
                },
                {
                    title: '类型',
                    filed: 'type_name',
                    dataIndex: 'type_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    minWidth: '150',
                    slots: {
                        default: ({ row }) => {
                            return [row.type_name];
                        }
                    }
                },
                {
                    title: '产品分类',
                    filed: 'text_68',
                    dataIndex: 'text_68',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    minWidth: '150',
                    slots: {
                        default: ({ row }) => {
                            return [row.text_68];
                        }
                    }
                },
                {
                    title: '状态',
                    filed: 'status',
                    dataIndex: 'status',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '60',
                    key: 'status',
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
                            return [<span style={auditSt}>{val}</span>];
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
                },
                {
                    name: '质量检测报告',
                    key: 'test'
                },
                {
                    name: '其它',
                    key: 'other'
                }
            ],
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
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '20%',
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
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    width: '20%',
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
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    width: '20%',
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
                    width: '20%',
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
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '20%',
                    slots: {
                        default: ({ row }) => {
                            return [row.issue_date];
                        }
                    }
                }
            ],
            mdrfData: [],
            chooseTrMdrfObj: '',
            examImgList: [],
            otherImgList: [],
            otherCertImgs: [] // 针对于打印的，“营业执照”和“医疗器械生产许可证”的图片数组，从entry-gic组件中回传回来
        };
    },
    computed: {
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                pager = 115;
            return (clientHeight - pager) / 2;
        }
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
                            isLeaf: st,
                            children: st ? [] : null // is_leaf 1：没有下一级叶子节点   0：有
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
        openChooseTb() {
            // 打开选择厂商
            this.chooseOrgMd.alert = true;
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
            this.$Utils.globalDragCenterFn('ct_choose_org_2');
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
        // 单击行
        cellClickFn: clickDebounce.debounce(function (row) {
            let obj = row.row || row;
            if (this.chooseTrObj === obj) return;
            this.chooseTrEvent(obj);
        }),
        // 双击行
        cellDblclickFn({ row }) {
            const timer = clickDebounce.getTimer();
            if (timer) {
                // 清除单击事件 防止和双击事件同时触发
                clickDebounce.clearTimer();
            }
            this.chooseTrEvent(row, true);
        },
        chooseTrEvent(record, dbck) {
            // 选中事件
            this.chooseTrObj = record;
            this.isChooseTr = true;
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
            this.getGoodsImg();
            this.getCertImg();
            this.getGicMdrf();
            this.getExamImg();
            this.getOtherImg();
            setTimeout(() => {
                this.$refs.xTable.setCurrentRow(this.chooseTrObj); // 默认选中
            }, 300);
            if (dbck) {
                this.openEdit();
            }
        },
        getCpmData(data) {
            this.createDataObj = JSON.parse(JSON.stringify(data));
        },
        getEditData(data) {
            this.newEditDataObj = JSON.parse(JSON.stringify(data));
        },
        getMdrfGicData(data) {
            this.newMdrfGicData = data;
        },
        async saveGoodsFn() {
            // 保存
            this.$refs.createPage.returnPageData();
            let dataObj = this.createDataObj;
            let typeInnerSn = dataObj.type_inner_sn;
            if (typeInnerSn.length > 0) {
                let index = dataObj.type_inner_sn.length - 1;
                dataObj.type_inner_sn = dataObj.type_inner_sn[index];
            }
            let code68Sn = dataObj.code68_sn;
            if (code68Sn.length > 0) {
                let index = dataObj.code68_sn.length - 1;
                dataObj.code68_sn = dataObj.code68_sn[index];
            }
            if (parseInt(this.nowSystemType) !== 3) {
                if (dataObj.vendor_inner_sn === '' || !dataObj.vendor_inner_sn) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择厂商',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
            } else {
                dataObj.vendor_inner_sn = this.$cookie.get('userbelong');
            }
            dataObj.name = this.$Utils.trim(dataObj.name);
            if (dataObj.name === '' || !dataObj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            await platformProductAdd(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.pageInit.total = this.pageInit.total + 1;
                    this.createSt = false;
                    if (res.info.code68_name) {
                        res.info.text_68 = res.info.code68_name + '(' + res.info.code68_sn + ')';
                    }
                    res.info.key = res.info.vendor_inner_sn + '' + res.info.product_inner_sn;
                    const curRow = this.$refs.xTable.getCurrentRow();
                    this.tableData.forEach((item, idx) => {
                        if (item.key === curRow.key) {
                            this.tableData.splice(idx, 0, res.info);
                            if (this.tableData.length + 1 > this.pageInit.pageSize) {
                                this.tableData.splice(this.tableData.length - 1, 1);
                            }
                            this.$refs.xTable.setCurrentRow(res.info); // 默认选中
                            this.cellClickFn(res.info);
                            throw new Error('forEach循环结束');
                        } else if (this.tableData.length <= 0) {
                            this.tableData.push(res.info);
                            this.cellClickFn(res.info);
                        }
                    });
                    if (this.tableData.length === 0) {
                        this.tableData.push(res.info);
                        this.cellClickFn(res.info);
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 编辑
        editGoodsFn() {
            this.$refs.editPage.returnPageData();
            let oldData = this.chooseTrObj;
            let newData = this.newEditDataObj;
            let finalData = {};
            let editInfoFlag = true;
            // 获取68 和 type
            let typeInnerSn = newData.type_inner_sn;
            if (typeInnerSn.length > 0) {
                let index = newData.type_inner_sn.length - 1;
                newData.type_inner_sn = newData.type_inner_sn[index];
            } else {
                newData.type_inner_sn = null;
            }
            let code68Sn = newData.code68_sn;
            if (code68Sn.length > 0) {
                let index = newData.code68_sn.length - 1;
                newData.code68_sn = newData.code68_sn[index];
            } else {
                newData.code68_sn = null;
            }
            for (let server in newData) {
                let serverItem = newData[server];
                for (let old in oldData) {
                    let oldItem = oldData[old];
                    if (server === old && serverItem !== oldItem) {
                        finalData = {
                            ...finalData,
                            [server]: serverItem, // 新值
                            [server + '_old']: oldItem // 旧值
                        };
                        editInfoFlag = false;
                    }
                }
            }
            if (newData.vendor_inner_sn.length === 0 || newData.vendor_inner_sn === undefined) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择厂商',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            newData.name = this.$Utils.trim(newData.name);
            if (newData.name === '' || !newData.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (!editInfoFlag) {
                this.editProServe(finalData);
            } else {
                this.editSt = false;
            }
        },
        async editProServe(data) {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductEditAPI(vid, pid, data).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.editSt = false;
                    data.vendor_inner_sn = this.chooseTrObj.vendor_inner_sn;
                    data.product_inner_sn = this.chooseTrObj.product_inner_sn;
                    this.loadTable(this.chooseTrObj);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 删除
        deleteBtn() {
            if (!this.chooseTrObj.vendor_inner_sn || !this.chooseTrObj.product_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '您真要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.deleteProduct();
                }
            });
        },
        async deleteProduct() {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductDelAPI(vid, pid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.tableData.forEach((item, index) => {
                        if (item.vendor_inner_sn === vid && item.product_inner_sn === pid) {
                            this.tableData.splice(index, 1);
                        }
                    });
                    this.chooseTrObj = {};
                    this.isChooseTr = false;
                    this.loadTable();
                } else {
                    this.$message.error(res.msg);
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
            this.pageInit.loading = true;
            let params = JSON.parse(JSON.stringify(this.searchData));
            params.org_id = this.$cookie.get('userbelong');
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
            params.pageSize = this.pageInit.pageSize;
            params.pageNum = this.pageInit.current;

            // 排序
            params.orderByColumn = 'product_inner_sn';
            params.isAsc = 'desc';

            let source = this.$cookie.get('userSystemType');
            await platformProductListAPI(source, { params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.pageInit.loading = false;
                    if (res.rows.length === 0) {
                        this.isChooseTr = false;
                        this.chooseTrObj = {};
                    }
                    res.rows.forEach((item) => {
                        item.isActive = '';
                        item.key = item.vendor_inner_sn + '' + item.product_inner_sn;
                        if (item.code68_name) {
                            item.text_68 = item.code68_name + '(' + item.code68_sn + ')';
                        }
                    });
                    this.tableData = res.rows;

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
                        this.goodsImgList = [];
                        this.certImgList = [];
                        this.mdrfImgList = [];
                        this.examImgList = [];
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
        openCreate() {
            this.createSt = true;
            this.openType = 'create';
            let cmp = this.$refs.createPage;
            if (cmp) {
                cmp.initPageData();
                this.$Utils.globalDragCenterFn('create');
            } else {
                this.$Utils.globalDragCenterFn('create', true);
            }
            this.modalData = [];
        },
        openEdit() {
            // 打开修改
            if (this.chooseTrObj.product_inner_sn) {
                // 赋值
                this.editSt = true;
                if (this.$refs.editPage) {
                    this.$Utils.globalDragCenterFn('edit');
                    this.sendEditInfo();
                } else {
                    this.$Utils.globalDragCenterFn('edit', true);
                }
            } else {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
            }
        },
        sendEditInfo() {
            // 加载编辑
            this.editTrObj.vid = this.chooseTrObj.vendor_inner_sn;
            this.editTrObj.pid = this.chooseTrObj.product_inner_sn;
            this.editTrObj.vendor_name = this.chooseTrObj.vendor_name;
            this.editTrObj.vendor_inner_sn_old = this.chooseTrObj.vendor_inner_sn;
            this.editTrObj.type_inner_sn_old = this.chooseTrObj.type_inner_sn;
            this.editTrObj.code68_sn_old = this.chooseTrObj.code68_sn;
            this.editTrObj.storage_condition_old = this.chooseTrObj.storage_condition;
            this.editTrObj.health_care_type_old = this.chooseTrObj.health_care_type;
            this.editTrObj.name_old = this.chooseTrObj.name;
            this.editTrObj.specifications_old = this.chooseTrObj.specifications;
            this.editTrObj.health_care_sn_old = this.chooseTrObj.health_care_sn;
            this.editTrObj.description_old = this.chooseTrObj.description;
            this.editTrObj.type_old = this.chooseTrObj.type;
            this.editTrObj.status_old = this.chooseTrObj.status;
            this.editTrObj.code68_type_old = this.chooseTrObj.code68_type;
            this.$refs.editPage.initEditData();
        },
        deleteImg(obj) {
            // 删除图片
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '您真要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.delImgFn(obj);
                }
            });
        },
        async delImgFn(obj) {
            // 删除图片
            await platformProductImgDelAPI(
                obj.vendor_inner_sn,
                obj.product_inner_sn,
                obj.type,
                obj.image_inner_sn
            ).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.getGoodsImg();
                    this.getExamImg();
                    this.getOtherImg();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        previewImg(src) {
            // 图片预览
            if (src && src !== '') {
                let img = new Image();
                img.src = src;
                this.previewWid = img.width + 50;
                let orderWidth = document.body.clientWidth; // 最大不能超过屏幕宽度
                if (this.previewWid > orderWidth) {
                    this.previewWid = orderWidth - 246;
                }
                if (this.previewWid < 300) {
                    this.previewWid = 300;
                }
                this.preImgSt = true;
                this.imgSrc = src;
                this.$Utils.globalDragCenterFn('imgModal');
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
        checkFile(file) {
            this.getUploadTokenAPIFn();
            let fileName = this.$Utils.formatFileNameToLowerCase(file.name);
            let index1 = fileName.lastIndexOf('.');
            let index2 = fileName.length;
            let postName = fileName.substring(index1 + 1, index2); // 后缀名
            if (
                postName !== 'png' &&
                postName !== 'jpeg' &&
                postName !== 'jpg' &&
                postName !== 'PNG' &&
                postName !== 'JPEG' &&
                postName !== 'JPG'
            ) {
                this.$message.error('仅支持png / jpeg 格式图片文件');
                return false;
            }
        },
        async upGoodsImg(info) {
            // 上传商品图片
            info.file.name = this.$Utils.formatFileNameToLowerCase(info.file.name);

            if (info.file.status === 'done') {
                const res = info.file.response;
                if (res.Code || res.code === 0) {
                    let url = res.data.key;
                    let params = {
                        vendor_inner_sn: this.chooseTrObj.vendor_inner_sn,
                        product_inner_sn: this.chooseTrObj.product_inner_sn,
                        type: 1,
                        url: url
                    };
                    await platformProductImgAddAPI(params).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            this.goodsImgList.push({ ...res.info });
                        } else {
                            this.$message.error(res.msg);
                        }
                    });
                } else {
                    this.$message.error(res.msg);
                }
            }
        },
        async upOtherImg(info) {
            // 上传其他图片
            info.file.name = this.$Utils.formatFileNameToLowerCase(info.file.name);

            if (info.file.status === 'done') {
                const res = info.file.response;
                if (res.Code || res.code === 0) {
                    let url = res.data.key;
                    let params = {
                        vendor_inner_sn: this.chooseTrObj.vendor_inner_sn,
                        product_inner_sn: this.chooseTrObj.product_inner_sn,
                        type: 0,
                        url: url
                    };
                    await platformProductImgAddAPI(params).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            this.otherImgList.push({ ...res.info });
                        } else {
                            this.$message.error(res.msg);
                        }
                    });
                } else {
                    this.$message.error(res.msg);
                }
            }
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
        async getExamImg() {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductImgGetAPI(vid, pid, 4).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.examImgList = res.images;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async getOtherImg() {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductImgGetAPI(vid, pid, 0).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.otherImgList = res.images;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /** 注册证方法区 **/
        async getCertImg() {
            // 获取注册证图片
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductImgGetAPI(vid, pid, 2).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.certImgList = res.images;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async getGicMdrf() {
            // 获取注册证登记表
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductMdrfGet(vid, pid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (parseFloat(res.code) === 0) {
                        this.mdrfData = res.list.map((item) => {
                            item.key = item.mdrf_inner_sn;
                            item.isActive = '';
                            return item;
                        });
                        this.$refs.mdrfTable[0].refreshColumn();
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.gicMdrfObj.name = res.info.product_name;
                    this.gicMdrfObj.vendor_name = res.info.vendor_name;
                    this.gicMdrfObj.address = res.info.vendor_address;
                    this.gicMdrfObj.specifications = this.chooseTrObj.specifications;

                    if (this.chooseTrMdrfObj !== '') {
                        this.mdrfData.forEach((item) => {
                            if (item.mdrf_inner_sn === this.chooseTrMdrfObj.mdrf_inner_sn) {
                                item.isActive = 'active-tr';
                                this.$refs.mdrfTable[0].setCurrentRow(item);
                            } else {
                                item.isActive = '';
                            }
                        });
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        openGicMdrf() {
            // 打开注册证登记表录入
            this.gicMdrfObj.new = true;
            this.gicMdrfObj.vendor_inner_sn = this.chooseTrObj.vendor_inner_sn;
            this.mdrfIsNew = true;
            this.entrySt = true;
            if (this.$refs.mdrfGicPage) {
                this.$Utils.globalDragCenterFn('entryModal');
                this.$refs.mdrfGicPage.refreshPage();
            } else {
                this.$Utils.globalDragCenterFn('entryModal', true);
            }
        },
        async editGicMdrf() {
            // 修改注册证
            this.mdrfIsNew = false;
            if (!this.chooseTrMdrfObj || this.chooseTrMdrfObj === '') {
                this.$warning({
                    title: '操作提示',
                    content: '请选择注册证',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            let vid = this.chooseTrMdrfObj.vendor_inner_sn;
            let pid = this.chooseTrMdrfObj.product_inner_sn;
            let mid = this.chooseTrMdrfObj.mdrf_inner_sn;
            await platformProductMdrfInfo(vid, pid, mid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.gicMdrfObj = {
                        ...res.info,
                        new: false,
                        vendor_inner_sn: vid
                    };
                    this.gicMdrfObj.new = false;
                    this.oldMdrfObj = JSON.parse(JSON.stringify(res.info));
                } else {
                    this.$message.error(res.msg);
                }
            });
            this.entrySt = true;
            if (this.$refs.mdrfGicPage) {
                this.$Utils.globalDragCenterFn('entryModal');
                this.$refs.mdrfGicPage.refreshPage();
            } else {
                this.$Utils.globalDragCenterFn('entryModal', true);
            }
        },
        async entryGicMdrf() {
            // 录入注册证登记表
            this.$refs.mdrfGicPage.returnPageData();
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            if (this.mdrfIsNew) {
                // 新增
                let data = this.newMdrfGicData;
                data.register_sn = this.$Utils.trim(data.register_sn);
                if (!data.register_sn || data.register_sn === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '注册号不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return;
                }
                if (!data.begin_date || data.begin_date === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '有效期不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                data.product_name = this.$Utils.trim(data.product_name);
                if (!data.product_name || data.product_name === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '产品名称不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return;
                }
                if (!data.issue_date) {
                    this.$warning({
                        title: '操作提示',
                        content: '发证日期不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                data.product_name_old = this.gicMdrfObj.name;
                data.specifications_old = this.gicMdrfObj.specifications;
                // let mdrfTable = this.$refs.mdrfTable[0]
                // let curRow = mdrfTable.getCurrentRow()
                await platformProductMdrfAdd(vid, pid, data).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.entrySt = false;
                        this.chooseTrMdrfObj = res.info;
                        this.loadTable(this.chooseTrObj);
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            } else {
                // 修改
                let oldData = JSON.parse(JSON.stringify(this.oldMdrfObj));
                let newData = JSON.parse(JSON.stringify(this.newMdrfGicData));
                let baseInfoChanged = false;
                // let imgListNew = newData.images
                // let imgListOld = oldData.images
                // oldData.images = ''
                // newData.images = ''
                let finalData = {};
                for (let server in newData) {
                    let serverItem = newData[server];
                    for (let old in oldData) {
                        let oldItem = oldData[old];
                        if (server === old && serverItem !== oldItem) {
                            finalData = {
                                ...finalData,
                                [server]: serverItem, // 新值
                                [server + '_old']: oldItem // 旧值
                            };
                            baseInfoChanged = true;
                        }
                    }
                }

                finalData.images = [...newData.images]; // === 后端约定，不管改没改变注册证图片，都必须传递
                // 比较图片数组是否有修改操作  没有改动就不做修改提交
                // let imgListChanged = false
                // if (!this.$XEUtils.isEqual(imgListOld, imgListNew)) {
                //     finalData.images = imgListNew
                //     imgListChanged = true
                // } else {
                //     imgListChanged = false
                // }

                const changed = baseInfoChanged;

                newData.register_sn = this.$Utils.trim(newData.register_sn);
                if (!newData.register_sn || newData.register_sn === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '注册号不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                if (!newData.begin_date || newData.begin_date === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '有效期不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                newData.product_name = this.$Utils.trim(newData.product_name);
                if (!newData.product_name || newData.product_name === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '产品名称不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return;
                }
                if (!newData.issue_date) {
                    this.$warning({
                        title: '操作提示',
                        content: '发证日期不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return;
                }
                let mid = this.chooseTrMdrfObj.mdrf_inner_sn;
                if (changed) {
                    await platformProductMdrfEdit(vid, pid, mid, finalData).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            this.entrySt = false;
                            // this.getGicMdrf()
                            this.loadTable(this.chooseTrObj);
                        } else {
                            this.$message.error(res.msg);
                        }
                    });
                } else {
                    this.entrySt = false;
                }
            }
        },
        async delGicMdrf() {
            // 删除注册证件
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let mid = this.chooseTrMdrfObj.mdrf_inner_sn;
            await platformProductMdrfDel(vid, pid, mid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.getGicMdrf();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        mdrfClickFn(row) {
            this.chooseTrMdrfObj = row.row || row;
        },
        mrdfDblclickFn(row) {
            this.chooseTrMdrfObj = row.row || row;
            this.editGicMdrf();
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
        autoTabHeight() {
            // 自动计算表格高度
            let orderHeight = document.body.clientHeight - 48 - 56 - 256 - 10;
            this.tableHeight = orderHeight;
            this.tabScrollHeight = this.tableHeight - 100;
        },
        handlePrint() {
            const otherCertImgs = this.otherCertImgs;
            if (!this.gicMdrfObj.images.length && !otherCertImgs.length) {
                this.$warning({
                    title: '操作提示',
                    content: '暂无注册证可打印',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            const printContent = JSON.stringify({
                printData: this.gicMdrfObj.images.concat(otherCertImgs).map((i) => this.$Utils.getPictureSrc(i.url)),
                printType: 11 // printType  11、多张图片
            });
            window.localStorage.printContent = printContent;
            const routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        getOtherCertImgsForPrint(imgs) {
            this.otherCertImgs = imgs;
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
        this.getUploadTokenAPIFn();
        this.loadTable();
        this.getCategoryListFn();
        this.get68ParentListFn();
        this.autoTabHeight();
        this.nowSystemType = this.$cookie.get('userSystemType');
        if (parseInt(this.nowSystemType) === 3) {
            this.columns.splice(1, 1);
            this.columns[0].width = '42%';
        }
        this.boxTopHeight = this.scrollYNumber - 56;
        this.boxDownHeight = this.scrollYNumber - 12;
        this.dragControllerDiv();
    }
};
</script>

<style scoped lang="less">
@import '../../../../public/style/ptmStyle1';
.bd_white {
    height: 100%;
    background: #fff;
}
.vxe-table:after {
    width: 0 !important;
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
.boxdown {
    float: left;
    width: 100%;
    height: 50%;
}
</style>
