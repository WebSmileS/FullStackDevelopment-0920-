<!--suppress ALL -->
<template>
    <div class="box">
        <div class="boxtop">
            <div class="table-part">
                <!-- v-bind:style="{height : tableHeight + 'px'}" -->
                <!-- :height="tabScrollHeight + 40" -->
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
                    row-id="keyFormat"
                    :data="tableData"
                    :loading="pageInit.loading"
                    :start-index="(pageInit.current - 1) * pageInit.pageSize"
                    :checkbox-config="{ reserve: true, highlight: true, range: false }"
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
                class="tab-table-wrap more"
                :style="{ height: boxDownHeight + 8 + 'px' }"
                style="width: 100%; padding: 4px 8px 0px 8px"
            >
                <bottom-tabs-wrapper>
                    <!-- tab切换区域开始 -->
                    <a-tabs class="align-l" style="padding-top: 0; height: calc(100% - 8px)" type="card">
                        <a-tab-pane v-for="item of tabName" :key="item.key" :tab="item.name">
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
                                    <div class="bottom-tabs-content-right bottom-picutres-wrapper">
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
                                    <div class="bottom-tabs-content-right bottom-table-wrapper" style="margin-top: 0px">
                                        <vxe-grid
                                            ref="mdrfTable"
                                            size="small"
                                            :columns="mdrf_columns"
                                            :height="boxDownHeight - 70"
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
                                <!-- 检测报告 -->
                                <div class="bottom-tabs-container" v-if="item.key == 'test'">
                                    <bottom-pictures
                                        :list="examImgList"
                                        @previewImg="previewImage"
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
                                            @previewImg="previewImage"
                                            @delete="deleteImg"
                                        />
                                    </div>
                                </div>
                            </template>
                        </a-tab-pane>
                    </a-tabs>
                </bottom-tabs-wrapper>
            </div>
            <!-- 模态框区域 -->
            <a-modal
                title=" - 图片预览"
                :maskClosable="false"
                :centered="true"
                :visible="preImgSt"
                :width="previewWid"
                cancelText="关闭"
                okText="打印"
                @ok="printFn"
                @cancel="closePreview"
                class="modal-form-input-scoped global-drag img_vender"
            >
                <div v-globalDrag="{ el: 'img_vender' }" class="modal-header-wrap">
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
                title="新增"
                :maskClosable="false"
                :centered="true"
                :visible="createSt"
                :width="900"
                okText="保存"
                @cancel="createSt = false"
                @ok="saveGoodsFn"
                class="modal-form-input-scoped"
            >
                <create-cp ref="createPage" @getData="getCpmData"></create-cp>
            </a-modal>
            <a-modal
                title="修改"
                :maskClosable="false"
                :centered="true"
                :visible="editSt"
                :width="900"
                okText="保存"
                @cancel="editSt = false"
                @ok="editGoodsFn"
                class="modal-form-input-scoped global-drag edit_vender"
            >
                <div v-globalDrag="{ el: 'edit_vender' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ leftTitle }} - 修改
                    </h6>
                </div>
                <edit-cp
                    ref="editPage"
                    :modalData="editTrObj"
                    @getData="getEditData"
                    @getEditInfo="sendEditInfo"
                ></edit-cp>
                <!--:modalData="editTrObj"-->
            </a-modal>
            <a-modal
                title="录入"
                :maskClosable="false"
                :centered="true"
                :visible="entrySt"
                :width="1150"
                @cancel="entrySt = false"
                class="modal-form-input-scoped global-drag entry_vender"
            >
                <div v-globalDrag="{ el: 'entry_vender' }" class="modal-header-wrap">
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
                    :imgList="mdrfImgList"
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
        </div>
    </div>
</template>

<script>
import BottomTabsWrapper from '@/components/BaseBottom/BottomTabsWrapper';
import BottomPictures from '@/components/BaseBottom/BottomPictures';
import createCp from './module/createGoods';
import editCp from './module/editGoods';
import entryGic from './module/entryGic';
import clickDebounce from '@/config/clickDebounce';

import {
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
    platformProductPublic,
    platformProductImgDelAPI,
    platformProductPublicBatch
} from '@/service/pageAjax';

export default {
    name: 'productBuild',
    props: ['searchData', 'highSearch'],
    components: {
        BottomPictures,
        BottomTabsWrapper,
        createCp,
        editCp,
        entryGic
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            leftTitle: `${this.$route.meta.title}`,
            boxTopHeight: 300,
            boxDownHeight: 300,
            tableHeight: 300,
            tabScrollHeight: null,
            upToken: '',
            upLoadparams: {},
            preImgSt: false,
            createSt: false,
            editSt: false,
            entrySt: false,
            mdrfIsNew: true,
            editSwitch: true,
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
                    title: '',
                    dataIndex: '',
                    minWidth: '50',
                    type: 'checkbox',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    title: '产品',
                    dataIndex: 'name',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '250',
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
                    minWidth: '250',
                    slots: {
                        default: ({ row }) => {
                            return [row.vendor_name];
                        }
                    }
                },
                {
                    title: '规格型号',
                    dataIndex: 'specifications',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '200',
                    slots: {
                        default: ({ row }) => {
                            return [row.specifications];
                        }
                    }
                },
                {
                    title: '类型',
                    dataIndex: 'type_name',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '150',
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
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '150',
                    slots: {
                        default: ({ row }) => {
                            return [row.text_68];
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
                    width: '20%',
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
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
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
                pager = 150;
            return (clientHeight - pager) / 2;
        }
    },
    methods: {
        initPageNum() {
            this.pageInit = {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageNum
                pageSize: 10 // 对应后端的 pageSize
            };
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
            this.chooseTrEvent(row);
            this.openEdit();
        },
        chooseTrEvent(record) {
            // 选中事件
            this.chooseTrObj = record;
            this.isChooseTr = true;
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
            setTimeout(() => {
                this.$refs.xTable.setCurrentRow(this.chooseTrObj); // 默认选中
            }, 300);
            this.getGoodsImg();
            this.getCertImg();
            this.getGicMdrf();
            // this.getMdrfImg()
            this.getExamImg();
            this.getOtherImg();
        },
        getCpmData(data) {
            this.createDataObj = data;
        },
        getEditData(data) {
            this.newEditDataObj = JSON.parse(JSON.stringify(data));
        },
        getUpGicData(data) {
            this.uploadGicData = data;
        },
        getMdrfGicData(data) {
            this.newMdrfGicData = JSON.parse(JSON.stringify(data));
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

            if (dataObj.vendor_inner_sn.length === 0 || dataObj.vendor_inner_sn === undefined) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择厂商',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
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
                    this.createSt = false;
                    this.loadTable(res.info);
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
                    this.loadTable(data);
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
            params.source = 3; // 产商
            params.pageSize = this.pageInit.pageSize;
            params.pageNum = this.pageInit.current;

            // 排序
            params.orderByColumn = 'product_inner_sn';
            params.isAsc = 'desc';

            await platformProductListAPI(3, { params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.pageInit.loading = false;
                    if (res.rows.length === 0) {
                        this.isChooseTr = false;
                        this.chooseTrObj = {};
                    }
                    this.tableData = res.rows.map((item) => {
                        item.isActive = '';
                        item.key = item.vendor_inner_sn + '' + item.product_inner_sn;
                        item.keyFormat = item.vendor_inner_sn + '-' + item.product_inner_sn + '-' + item.cert_no;
                        if (item.code68_name) {
                            item.text_68 = item.code68_name + '(' + item.code68_sn + ')';
                        }
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
        changePaginationFn(page) {
            this.pageInit = {
                ...this.pageInit,
                current: page.currentPage,
                pageSize: page.pageSize,
                loading: true
            };
            this.loadTable();
            this.isChooseTr = false;
        },
        openCreate() {
            this.createSt = true;
            this.openType = 'create';
            let cmp = this.$refs.createPage;
            if (cmp) {
                cmp.initPageData();
            }
            this.modalData = [];
        },
        openEdit() {
            // 打开修改
            if (this.chooseTrObj.product_inner_sn) {
                // 赋值
                this.editSt = true;
                if (this.$refs.editPage) {
                    this.sendEditInfo();
                    this.$Utils.globalDragCenterFn('edit_vender');
                } else {
                    this.$Utils.globalDragCenterFn('edit_vender', true);
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
            this.editTrObj.code68_type_old = this.chooseTrObj.code68_type;
            this.editTrObj.status_old = this.chooseTrObj.status;

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
                    that.$Utils.globalDragCenterFn('img_vender');
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
            this.gicMdrfObj.vendor_inner_sn = this.chooseTrObj.vendor_inner_s;
            this.mdrfIsNew = true;
            this.entrySt = true;
            if (this.$refs.mdrfGicPage) {
                this.$Utils.globalDragCenterFn('entry_vender');
                this.$refs.mdrfGicPage.refreshPage();
            } else {
                this.$Utils.globalDragCenterFn('entry_vender', true);
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

            const { vendor_inner_sn: vid, product_inner_sn: pid, mdrf_inner_sn: mid } = this.chooseTrMdrfObj;
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
                this.$Utils.globalDragCenterFn('entry_vender');
                this.$refs.mdrfGicPage.refreshPage();
            } else {
                this.$Utils.globalDragCenterFn('entry_vender', true);
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
                await platformProductMdrfAdd(vid, pid, data).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.entrySt = false;
                        this.chooseTrMdrfObj = res.info;
                        // this.getGicMdrf()
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
                            baseInfoChanged = true;
                        }
                    }
                }

                // 比较图片数组是否有修改操作  没有改动就不做修改提交
                let imgListChanged = false;
                if (!this.$XEUtils.isEqual(imgListOld, imgListNew)) {
                    finalData.images = imgListNew;
                    imgListChanged = true;
                } else {
                    imgListChanged = false;
                }

                const changed = baseInfoChanged || imgListChanged;

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
        async issueFn() {
            // 发布
            try {
                const { vendor_inner_sn: vid, product_inner_sn: pid } = this.chooseTrObj;
                const selectedRecords = this.$refs.xTable.getCheckboxRecords().map((i) => ({
                    vendor_inner_sn: i.vendor_inner_sn,
                    product_inner_sn: i.product_inner_sn
                }));

                if (!vid || !pid) {
                    this.$warning({
                        title: '操作提示',
                        content: this.$Utils.tips().noSelected,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                const res = selectedRecords.length
                    ? await platformProductPublicBatch(selectedRecords)
                    : await platformProductPublic(vid, pid);

                if (parseFloat(res.code) === 0) {
                    const message = selectedRecords.length ? '批量发布成功' : '发布成功';
                    this.$message.success(message);
                    this.loadTable();
                    // 清空表格选中及checkbox选中状态
                    this.$refs.xTable.clearAll();
                } else {
                    this.$message.error(res.msg);
                }
            } catch (error) {
                console.error(error);
            }
        },
        autoTabHeight() {
            // 自动计算表格高度
            let orderHeight = document.body.clientHeight - 48 - 56 - 256 - 10;
            this.tableHeight = orderHeight;
            this.tabScrollHeight = this.tableHeight - 117;
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
            if (val) {
                this.boxTopHeight -= 38;
                this.tabScrollHeight = this.tabScrollHeight - 38;
            } else {
                this.boxTopHeight += 38;
                this.tabScrollHeight = this.tabScrollHeight + 38;
            }
        }
    },
    mounted() {
        this.getUploadTokenAPIFn();
        this.loadTable();
        this.autoTabHeight();
        this.boxTopHeight = this.scrollYNumber - 62;
        this.boxDownHeight = this.scrollYNumber - 12;
    }
};
</script>

<style scoped lang="less">
@import '../../../../public/style/ptmStyle1';
</style>
<style scoped>
.box {
    width: 100%;
    top: 40px;
    bottom: 20px;
    overflow: hidden;
    position: absolute;
    background: #fff;
}
.boxtop {
    /*width:calc(30% - 10px);*/
    width: 100%;
    padding: 8px;
    margin-top: -4px;
    height: calc(50% - 3px);
    float: left;
}
.boxdown {
    float: left;
    width: 100%;
    height: 50%;
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
</style>
