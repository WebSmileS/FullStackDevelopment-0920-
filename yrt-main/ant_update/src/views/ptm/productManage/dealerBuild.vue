<!--suppress ALL -->
<template>
    <div style="margin-top: -16px">
        <div v-bind:style="{ height: tableHeight + 'px' }" class="table-part">
            <vxe-grid
                ref="xTable"
                size="small"
                :columns="columns"
                :height="tabScrollHeight + 40"
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
        <div class="bottom-part more">
            <div class="spec-box more table-part" style="height: 226px">
                <div style="width: 100%">
                    <vxe-grid
                        ref="mdrfTable"
                        size="small"
                        :columns="spec_columns"
                        height="240"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        @cell-click="specClickFn"
                        @cell-dblclick="specDblclickFn"
                        align="center"
                        :data="specData"
                    >
                    </vxe-grid>
                </div>
            </div>
            <div class="tab-table-wrap minimal more2" style="height: 226px">
                <!-- tab切换区域开始 -->
                <bottom-tabs-wrapper>
                    <a-tabs class="align-l" type="card">
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
                                    <div class="bottom-tabs-content-right bottom-pictures-wrapper">
                                        <bottom-pictures
                                            :list="goodsImgList"
                                            :deleteAble="false"
                                            @previewImg="previewImg"
                                        />
                                    </div>
                                </div>
                                <!-- 质量检测报告 -->
                                <div class="bottom-tabs-container" v-if="item.key == 'test'">
                                    <bottom-pictures :list="examImgList" :deleteAble="false" @previewImg="previewImg" />
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
                                            :deleteAble="false"
                                            @previewImg="previewImg"
                                        />
                                    </div>
                                </div>
                            </template>
                        </a-tab-pane>
                    </a-tabs>
                </bottom-tabs-wrapper>
            </div>
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
            class="modal-form-input-scoped global-drag img_dealer"
        >
            <div v-globalDrag="{ el: 'img_dealer' }" class="modal-header-wrap">
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
            class="modal-form-input-scoped global-drag edit_dealer"
        >
            <div v-globalDrag="{ el: 'edit_dealer' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 修改
                </h6>
            </div>
            <edit-cp ref="editPage" :modalData="editTrObj" @getData="getEditData" @getEditInfo="sendEditInfo"></edit-cp>
            <!--:modalData="editTrObj"-->
        </a-modal>
        <a-modal
            title="上传注册证"
            :maskClosable="false"
            :centered="true"
            :visible="uploadGicSt"
            :width="500"
            okText="保存"
            @ok="uploadGicFn"
            @cancel="uploadGicSt = false"
            class="modal-form-input-scoped global-drag gic_dealer"
        >
            <div v-globalDrag="{ el: 'gic_dealer' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 医疗器械注册证 - 上传注册证
                </h6>
            </div>
            <upload-gic ref="upGicPage" @getData="getUpGicData" :chooseTrObj="chooseTrObj"></upload-gic>
        </a-modal>
        <a-modal
            title="录入"
            :maskClosable="false"
            :centered="true"
            :visible="entrySt"
            :width="960"
            okText="保存"
            @ok="entryGicMdrf"
            @cancel="entrySt = false"
            class="modal-form-input-scoped global-drag entry_dealer"
        >
            <div v-globalDrag="{ el: 'entry_dealer' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 医疗器械注册登记表 - 录入
                </h6>
            </div>
            <entry-gic
                ref="mdrfGicPage"
                @getData="getMdrfGicData"
                :imgList="mdrfImgList"
                :gicMdrfObj="gicMdrfObj"
            ></entry-gic>
        </a-modal>
    </div>
</template>

<script>
import BottomTabsWrapper from '@/components/BaseBottom/BottomTabsWrapper';
import BottomPictures from '@/components/BaseBottom/BottomPictures';
import createCp from './module/createGoods';
import editCp from './module/editGoods';
import entryGic from './module/entryGic';
import uploadGic from './module/uploadGic';
import moment from 'moment';
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
    platformProductSpecList,
    platformProductPublic,
    platformProductPublicBatch
} from '@/service/pageAjax';

export default {
    name: 'productReleased',
    props: ['searchData', 'highSearch'],
    components: {
        BottomPictures,
        BottomTabsWrapper,
        createCp,
        editCp,
        entryGic,
        uploadGic
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            leftTitle: `${this.$route.meta.title}`,
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
            specSt: false,
            specTit: '',
            modalData: [], // 模态数据
            editTrObj: {
                // 修改数据
                vid: '',
                pid: '',
                dealer_inner_sn_old: [], // 厂商ID
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
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '300',
                    slots: {
                        default: ({ row }) => {
                            return [row.name];
                        }
                    }
                },
                {
                    title: '经销商',
                    dataIndex: 'dealer_name',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '300',
                    slots: {
                        default: ({ row }) => {
                            return [row.dealer_name];
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
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    minWidth: '200',
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
            examImgList: [],
            spec_columns: [
                {
                    title: '规格型号',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    dataIndex: 'specification',
                    align: 'center'
                }
            ],
            specData: [],
            chooseTrSpecObj: {},
            newSpec: '',
            editSpec: '',
            isEdit: false,
            otherImgList: []
        };
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
        getCurrentStyle(current, today) {
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
            this.isChooseTr = true;
            this.chooseTrObj = record;
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
            // this.getCertImg()
            // this.getGicMdrf()
            // this.getExamImg()
            this.getOtherImg();
            this.loadSpecFn();
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
        async saveGoodsFn(e) {
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

            if (dataObj.dealer_inner_sn.length === 0 || dataObj.dealer_inner_sn === undefined) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择厂商',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
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
            if (newData.dealer_inner_sn.length === 0 || newData.dealer_inner_sn === undefined) {
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
            let vid = this.chooseTrObj.dealer_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductEditAPI(vid, pid, data).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.editSt = false;
                    data.dealer_inner_sn = this.chooseTrObj.dealer_inner_sn;
                    data.product_inner_sn = this.chooseTrObj.product_inner_sn;
                    this.loadTable(data);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 删除
        deleteBtn() {
            if (!this.chooseTrObj.dealer_inner_sn || !this.chooseTrObj.product_inner_sn) {
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
            let vid = this.chooseTrObj.dealer_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductDelAPI(vid, pid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.tableData.forEach((item, index) => {
                        if (item.dealer_inner_sn === vid && item.product_inner_sn === pid) {
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
                params.dealer_inner_sn = '';
            }
            params.source = 2; // 经销商
            params.pageSize = this.pageInit.pageSize;
            params.pageNum = this.pageInit.current;

            // 排序
            params.orderByColumn = 'product_inner_sn';
            params.isAsc = 'desc';

            await platformProductListAPI(2, { params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.pageInit.loading = false;
                    if (res.rows.length === 0) {
                        this.isChooseTr = false;
                        this.chooseTrObj = {};
                    }
                    this.tableData = res.rows.map((item) => {
                        item.isActive = '';
                        item.key = item.dealer_inner_sn + '' + item.product_inner_sn;
                        item.keyFormat = item.dealer_inner_sn + '-' + item.product_inner_sn + '-' + item.cert_no;
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

                    this.chooseTrObj = {};
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
                    this.$Utils.globalDragCenterFn('edit_dealer');
                } else {
                    this.$Utils.globalDragCenterFn('edit_dealer', true);
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
            this.editTrObj.vid = this.chooseTrObj.dealer_inner_sn;
            this.editTrObj.pid = this.chooseTrObj.product_inner_sn;
            this.editTrObj.vendor_name = this.chooseTrObj.vendor_name;
            this.editTrObj.dealer_inner_sn_old = this.chooseTrObj.dealer_inner_sn;
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
                    that.$Utils.globalDragCenterFn('img_dealer');
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
        checkFile(file, fileList) {
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
                        dealer_inner_sn: this.chooseTrObj.dealer_inner_sn,
                        product_inner_sn: this.chooseTrObj.product_inner_sn,
                        type: 1,
                        url: url
                    };
                    await platformProductImgAddAPI(params).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            this.goodsImgList.push({
                                url: this.$Utils.getPictureSrc(url)
                            });
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
                            this.otherImgList.push({
                                url: this.$Utils.getPictureSrc(url)
                            });
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
            let vid = this.chooseTrObj.dealer_inner_sn;
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
            let vid = this.chooseTrObj.dealer_inner_sn;
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
            let vid = this.chooseTrObj.dealer_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductImgGetAPI(vid, pid, 0).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.otherImgList = res.images;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        openUploadGic() {
            // 打开上传注册证
            this.uploadGicSt = true;
            let cmp = this.$refs.upGicPage;
            if (cmp) {
                cmp.initUploadGic();
                this.$Utils.globalDragCenterFn('gic_dealer');
            } else {
                this.$Utils.globalDragCenterFn('gic_dealer', true);
            }
        },
        async uploadGicFn() {
            // 上传注册证
            this.$refs.upGicPage.returnPageData();
            if (!this.uploadGicData.url || this.uploadGicData.url === '') {
                this.$warning({
                    title: '操作提示',
                    content: '请上传注册证图片',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            let params = {
                dealer_inner_sn: this.chooseTrObj.dealer_inner_sn,
                product_inner_sn: this.chooseTrObj.product_inner_sn,
                type: 2,
                url: this.uploadGicData.url,
                begin_date: this.uploadGicData.begin_date,
                end_date: this.uploadGicData.end_date
            };
            await platformProductImgAddAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let item = {
                        url: this.uploadGicData.url
                    };
                    this.certImgList.push(item);
                    this.uploadGicSt = false;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async getCertImg() {
            // 获取注册证图片
            let vid = this.chooseTrObj.dealer_inner_sn;
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
            let vid = this.chooseTrObj.dealer_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductMdrfGet(vid, pid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.oldMdrfObj = {};
                    this.gicMdrfObj = {
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
                        register_sn: '',
                        specifications: '',
                        standard: null,
                        vendor_name: ''
                    };
                    this.mdrfIsNew = res.info.new;
                    if (this.mdrfIsNew) {
                        this.gicMdrfObj.vendor_name = res.info.vendor_name;
                        this.gicMdrfObj.address = res.info.address;
                        this.gicMdrfObj.product_name = res.info.product_name;
                        this.gicMdrfObj.specifications = res.info.specifications;
                    } else {
                        this.gicMdrfObj = {
                            ...res.info,
                            vendor_inner_sn: vid
                        };
                        this.gicMdrfObj.issue_date = moment(this.gicMdrfObj.issue_date);
                        this.oldMdrfObj = JSON.parse(JSON.stringify(res.info));
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async getMdrfImg() {
            // 获取注册证登记表图片
            let vid = this.chooseTrObj.dealer_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductImgGetAPI(vid, pid, 3).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.mdrfImgList = res.images;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async upMdrfImg(info) {
            // 上传注册证登记表图片
            if (info.file.status === 'done') {
                const res = info.file.response;
                if (res.Code || res.code === 0) {
                    let url = res.data.key;
                    let params = {
                        dealer_inner_sn: this.chooseTrObj.dealer_inner_sn,
                        product_inner_sn: this.chooseTrObj.product_inner_sn,
                        type: 3,
                        url: url
                    };
                    await platformProductImgAddAPI(params).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            let item = {
                                url: this.$Utils.getPictureSrc(url)
                            };
                            this.mdrfImgList.push(item);
                        } else {
                            this.$message.error(res.msg);
                        }
                    });
                } else {
                    this.$message.error(res.msg);
                }
            }
        },
        openGicMdrf() {
            // 打开注册证登记表录入
            this.getGicMdrf();
            this.entrySt = true;
            if (this.$refs.mdrfGicPage) {
                this.$Utils.globalDragCenterFn('entry_dealer');
            } else {
                this.$Utils.globalDragCenterFn('entry_dealer', true);
            }
        },
        async entryGicMdrf() {
            // 录入注册证登记表
            this.$refs.mdrfGicPage.returnPageData();
            let vid = this.chooseTrObj.dealer_inner_sn;
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
                data.product_name_old = this.gicMdrfObj.product_name;
                data.specifications_old = this.gicMdrfObj.specifications;
                await platformProductMdrfAdd(vid, pid, data).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.entrySt = false;
                        this.mdrfIsNew = false;
                        this.getGicMdrf();
                        this.loadTable(this.chooseTrObj);
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            } else {
                // 修改
                let oldData = JSON.parse(JSON.stringify(this.oldMdrfObj));
                oldData.issue_date = moment[oldData.issue_date];
                let newData = JSON.parse(JSON.stringify(this.newMdrfGicData));
                oldData.images = '';
                newData.images = '';
                let finalData = {};
                let editInfoFlag = true;
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
                if (!editInfoFlag) {
                    await platformProductMdrfEdit(vid, pid, finalData).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            this.entrySt = false;
                            this.getGicMdrf();
                            this.loadTable(this.chooseTrObj);
                        } else {
                            this.$message.error(res.msg);
                        }
                    });
                }
            }
        },
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
        async loadSpecFn() {
            let vid = this.chooseTrObj.dealer_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductSpecList(vid, pid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.specData = res.list.map((item) => {
                        item.isActive = '';
                        item.key = item.specification_inner_sn;
                        return item;
                    });
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async issueFn() {
            // 发布
            try {
                const { dealer_inner_sn: vid, product_inner_sn: pid } = this.chooseTrObj;
                const selectedRecords = this.$refs.xTable.getCheckboxRecords().map((i) => ({
                    vendor_inner_sn: i.dealer_inner_sn,
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
        }
    },
    watch: {
        highSearch(val) {
            if (val) {
                this.tabScrollHeight = this.tabScrollHeight - 38;
            } else {
                this.tabScrollHeight = this.tabScrollHeight + 38;
            }
        }
    },
    mounted() {
        this.getUploadTokenAPIFn();
        this.loadTable();
        this.autoTabHeight();
    }
};
</script>

<style scoped lang="less">
@import '../../../../public/style/ptmStyle1';
</style>
