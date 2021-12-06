<!--suppress ALL -->
<template>
    <div class="product-page">
        <div class="todo-part">
            <a-row>
                <a-col :span="12" class="btn-count">
                    <a-button class="btn-nom mgr-10" type="primary" @click="editGicMdrf">修改</a-button>
                    <a-button class="btn-nom mgr-10" type="primary" @click="openCopyGicMdrf">复制注册证</a-button>
                    <!--<a-button class="btn-nom mgr-10 cut" @click="delGicMdrf" type="danger">删除</a-button>-->
                </a-col>
                <a-col :span="12" class="align-r">
                    <a-input
                        v-if="!highSearch"
                        placeholder="注册证编号"
                        v-model="searchData.register_sn"
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
                            <span class="label">注册证编号</span>
                            <a-input type="text" v-model="searchData.register_sn" placeholder="" />
                        </li>
                        <li>
                            <span class="label">产品名称</span>
                            <a-input type="text" v-model="searchData.product_name" placeholder="" />
                        </li>
                        <li>
                            <span class="label">厂商名称</span>
                            <a-input type="text" v-model="searchData.vendor_name" placeholder="" />
                        </li>
                        <li>
                            <span class="label">是否有注册证图片</span>
                            <a-select type="text" v-model="searchData.hasCertImage" placeholder="">
                                <a-select-option value="0">全部</a-select-option>
                                <a-select-option value="1">有</a-select-option>
                                <a-select-option value="2">无</a-select-option>
                            </a-select>
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
                        <a-tabs class="align-l" style="padding-top: 0; height: calc(100% - 8px)" type="card">
                            <a-tab-pane v-for="item of tabName" :key="item.key" :tab="item.name" forceRender>
                                <div v-if="!isChooseTr" class="no-data-box">
                                    <div><a-icon type="file-excel" /></div>
                                    请选择商品
                                </div>
                                <template v-else>
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
            <choose-org-tab ref="chooseOrgMd" @chooseOrg="chooseOrgFn" :partType="partType"></choose-org-tab>
        </a-modal>
        <a-modal
            :title="addProductMd.title"
            :visible="addProductMd.alert"
            :maskClosable="false"
            :mask="false"
            :width="1000"
            :centered="true"
            @cancel="addProductMd.alert = false"
            class="modal-form-input-scoped global-drag add_pro_cp"
        >
            <div v-globalDrag="{ el: 'add_pro_cp' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addProductMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="addProductMd.alert = false">关闭</a-button>
                <a-button :loading="addProductMd.loading" @click="addProductFn" type="primary">添加</a-button>
            </template>
            <add-product ref="chooseProMd" @returnData="getProReturn" :partB="part_b_obj"></add-product>
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
import addProduct from '@/components/addProduct';
import {
    getVendorList,
    categoryGetNextListAPI,
    categoryGetParentListAPI,
    code68ParentListAPI,
    code68ChildrenListAPI,
    getUploadTokenAPI,
    platformProductAdd,
    platformProductMdrfSearchAPI,
    platformProductDelAPI,
    platformProductEditAPI,
    platformProductMdrfImgAddAPI,
    platformProductMdrfImgAPI,
    platformProductMdrfAdd,
    platformProductMdrfEdit,
    platformProductMdrfDel,
    platformProductMdrfInfo,
    platformProductMdrfImgDelAPI,
    platformProductMdrfCopyAPI
} from '@/service/pageAjax';

export default {
    name: 'productCert',
    components: {
        BottomPictures,
        BottomTabsWrapper,
        createCp,
        editCp,
        entryGic,
        chooseOrgTab,
        addProduct
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
            addProductMd: {
                title: `${this.$route.meta.title} - 复制 - 选择产品`,
                alert: false,
                loading: false
            },
            addProName: 'add_pro_cp',
            part_b_obj: {},
            chooseGoodsArr: [],
            chooseOrgMd: {
                title: `${this.$route.meta.title}  - 选择机构`,
                alert: false,
                loading: false
            },
            vendorObj: {
                name: ''
            },
            searchData: {
                register_sn: '', // 注册证编号
                product_name: '', // 产品名称
                vendor_name: '', // 厂商名称
                hasCertImage: '0' // 是否有注册证  0: 全部 1: 有  2: 无
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
                    title: '产品名称',
                    dataIndex: 'product_name',
                    filed: 'product_name',
                    align: 'center',
                    minWidth: '300',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.product_name];
                        }
                    }
                },
                {
                    title: '厂商名称',
                    dataIndex: 'vendor_name',
                    filed: 'vendor_name',
                    align: 'center',
                    minWidth: '300',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.vendor_name];
                        }
                    }
                },
                {
                    title: '注册证号',
                    dataIndex: 'register_sn',
                    filed: 'register_sn',
                    align: 'center',
                    minWidth: '200',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.register_sn];
                        }
                    }
                },
                {
                    title: '注册证',
                    dataIndex: 'hasCertImage',
                    filed: 'hasCertImage',
                    align: 'center',
                    minWidth: '50',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [
                                row.hasCertImage ? <a-tag color="#87d068">有</a-tag> : <a-tag color="#f50">无</a-tag>
                            ];
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
                    minWidth: '100',
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
                }
            ],
            tableData: [],
            tabName: [
                {
                    name: '注册证图片',
                    key: 'goodsPic'
                }
            ],
            previewVisible: false,
            previewImage: '',
            createDataObj: [],
            newEditDataObj: [],
            goodsImgList: [],
            certObj: {
                begin_date: '',
                end_date: ''
            },
            mdrf_columns: [
                {
                    title: '注册证号',
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
        cellClickFn(row) {
            // 单击行
            let obj = row.row || row;
            this.chooseTrEvent(obj);
        },
        cellDblclickFn({ row }) {
            this.chooseTrEvent(row, true);
        },
        chooseTrEvent(record, dbck) {
            // 选中事件
            this.chooseTrObj = record;
            this.isChooseTr = true;
            this.getGoodsImg();
            setTimeout(() => {
                this.$refs.xTable.setCurrentRow(this.chooseTrObj); // 默认选中
            }, 300);
            if (dbck) {
                this.editGicMdrf();
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
            if (this.$Utils.checkInputValFn(this.searchData.register_sn)) {
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
            const params = JSON.parse(JSON.stringify(this.searchData));
            params.org_id = this.$cookie.get('userbelong');
            if (!this.highSearch) {
                params.product_name = '';
                params.vendor_name = '';
            }
            params.pageSize = this.pageInit.pageSize;
            params.pageNum = this.pageInit.current;
            params.hasCertImage = Number.parseInt(params.hasCertImage, 10);

            // 排序
            params.orderByColumn = 'mdrf_inner_sn';
            params.isAsc = 'desc';

            await platformProductMdrfSearchAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.pageInit.loading = false;
                    if (res.rows.length === 0) {
                        this.isChooseTr = false;
                        this.chooseTrObj = {};
                    }
                    res.rows.forEach((item) => {
                        item.isActive = '';
                        item.key = item.mdrf_inner_sn + '' + item.product_inner_sn + '' + item.vendor_inner_sn;
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
            await platformProductMdrfImgDelAPI(
                this.chooseTrObj.vendor_inner_sn,
                this.chooseTrObj.product_inner_sn,
                this.chooseTrObj.mdrf_inner_sn,
                obj.miid
            ).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.getGoodsImg();
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
                postName !== 'jpeg' &&
                postName !== 'JPEG' &&
                postName !== 'jpg' &&
                postName !== 'JPG' &&
                postName !== 'png' &&
                postName !== 'PNG' &&
                postName !== 'pdf' &&
                postName !== 'PDF'
            ) {
                alert('仅支持png / jpeg / pdf');
                return false;
            }
        },
        async upGoodsImg(info) {
            // 上传注册证图片
            let fileName = this.$Utils.formatFileNameToLowerCase(info.file.name);

            if (info.file.status === 'done') {
                const res = info.file.response;
                if (res.Code || res.code === 0) {
                    let params = {
                        url: res.data.key,
                        fileName,
                        vid: this.chooseTrObj.vendor_inner_sn,
                        pid: this.chooseTrObj.product_inner_sn,
                        mid: this.chooseTrObj.mdrf_inner_sn
                    };
                    await platformProductMdrfImgAddAPI(params).then((innerRes) => {
                        if (parseFloat(innerRes.code) === 0) {
                            this.goodsImgList.push({
                                url: res.data.key, // resourceKey
                                miid: innerRes.info,
                                file_name: fileName
                            });
                        } else {
                            this.$message.error(innerRes.msg);
                        }
                    });
                } else {
                    this.$message.error(res.msg);
                }
            }
        },
        async getGoodsImg() {
            // 获取注册证图片
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let mid = this.chooseTrObj.mdrf_inner_sn;
            await platformProductMdrfImgAPI(vid, pid, mid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.goodsImgList = res.list.map((item) => {
                        item.miid = item.image_inner_sn;
                        return item;
                    });
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /** 注册证方法区 **/
        openCopyGicMdrf() {
            if (!this.chooseTrObj || this.chooseTrObj === '' || !this.chooseTrObj._XID) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择注册证',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            this.part_b_obj.name = this.chooseTrObj.vendor_name;
            this.part_b_obj.systemType = 3;
            this.part_b_obj.modalType = 10;
            this.part_b_obj.id = this.chooseTrObj.vendor_inner_sn;
            this.part_b_obj.isCopy = true;
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn('add_pro_cp', true);
            if (this.$refs.chooseProMd) {
                this.$Utils.globalDragCenterFn('add_pro_cp');
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        addProductFn() {
            // 添加商品
            this.$refs.chooseProMd.returnProList();
        },
        getProReturn(dea, ven) {
            // 获取回调商品
            let arr = ven.concat(dea);
            let newArr = JSON.parse(JSON.stringify(arr));

            this.chooseGoodsArr = [];

            newArr.forEach((item) => {
                this.chooseGoodsArr.push(item.product_inner_sn);
            });
            this.copyCertFn();
        },
        async copyCertFn() {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let mid = this.chooseTrObj.mdrf_inner_sn;
            await platformProductMdrfCopyAPI(vid, pid, mid, this.chooseGoodsArr).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.addProductMd.alert = false;
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

            if (!this.chooseTrObj._XID || !this.chooseTrObj || this.chooseTrObj === '' || this.chooseTrObj === {}) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择注册证',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let mid = this.chooseTrObj.mdrf_inner_sn;
            await platformProductMdrfInfo(vid, pid, mid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.gicMdrfObj = {
                        ...res.info,
                        new: false,
                        vendor_inner_sn: vid
                    };
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
                        content: '注册证号不能为空',
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
                        this.chooseTrObj = res.info;
                        this.loadTable(this.chooseTrObj);
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            } else {
                // 修改
                let oldData = JSON.parse(JSON.stringify(this.oldMdrfObj));
                let newData = JSON.parse(JSON.stringify(this.newMdrfGicData));
                let editInfoFlag = true;
                let imgListNew = newData.images;
                let imgListOld = oldData.images;
                oldData.images = '';
                newData.images = '';
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
                            editInfoFlag = false;
                        }
                    }
                }
                if (imgListNew !== imgListOld) {
                    finalData.images = imgListNew;
                    editInfoFlag = false;
                } else {
                    editInfoFlag = true;
                }

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
                let mid = this.chooseTrObj.mdrf_inner_sn;
                if (!editInfoFlag) {
                    await platformProductMdrfEdit(vid, pid, mid, finalData).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            this.entrySt = false;
                            this.chooseTrObj = res.info;
                            this.chooseTrObj.key =
                                this.chooseTrObj.mdrf_inner_sn +
                                '' +
                                this.chooseTrObj.product_inner_sn +
                                '' +
                                this.chooseTrObj.vendor_inner_sn;
                            this.tableData.forEach((item, idx) => {
                                if (item.key === this.chooseTrObj.key) {
                                    this.tableData.splice(idx, 1, this.chooseTrObj);
                                    this.cellClickFn(this.chooseTrObj);
                                }
                            });
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
            let mid = this.chooseTrObj.mdrf_inner_sn;
            await platformProductMdrfDel(vid, pid, mid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.loadTable();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 注释人：娄潇尘
        // 注释原因：未使用函数
        // mdrfClickFn (row) {
        //     this.chooseTrMdrfObj = row.row || row
        // },
        // mrdfDblclickFn (row) {
        //     this.chooseTrMdrfObj = row.row || row
        //     this.editGicMdrf()
        // },
        //
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
        },
        // 动态更新注册证图片tab数量文本
        goodsImgList(value) {
            this.tabName[0].name = `注册证图片(${value.length})`;
        }
    },
    mounted() {
        this.getUploadTokenAPIFn();
        this.loadTable();
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
.img-pre-ico {
    line-height: 250px !important;
}
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
