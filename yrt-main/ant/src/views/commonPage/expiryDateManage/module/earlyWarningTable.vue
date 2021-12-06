<template>
    <div>
        <vxe-table
            :ref="refName"
            size="small"
            :height="scrollYNumber - 4"
            border
            resizable
            auto-resize
            highlight-current-row
            :row-class-name="rowClassName"
            @cell-dblclick="cellDblclickFn"
            align="center"
            :data="datas"
            :loading="pagination.loading"
            show-overflow
            show-header-overflow
            showHeader
            :start-index="(pagination.current - 1) * pagination.pageSize"
        >
            <vxe-table-column
                v-for="(column, columnIndex) in columns"
                :key="`${column.field}${columnIndex}`"
                :width="column.width"
                :field="column.field"
                :title="column.title"
            >
            </vxe-table-column>
        </vxe-table>
        <vxe-pager
            :loading="pagination.loading"
            :current-page="pagination.current"
            :page-size="pagination.pageSize"
            :page-sizes="pagination.pageSizes"
            :total="pagination.total"
            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="changePaginationFn"
        >
        </vxe-pager>

        <!-- 注册证区域开始 -->
        <a-modal
            :title="mdrfModalTitle"
            :maskClosable="false"
            :visible="entrySt"
            :centered="true"
            :width="1150"
            @cancel="entrySt = false"
            class="modal-form-input-scoped global-drag entryModal"
        >
            <div v-globalDrag="{ el: 'entryModal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ mdrfModalTitle }}
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
                    <span>关闭</span>
                </a-button>
                <!-- <a-button @click="handlePrint">
                    <span>打印</span>
                </a-button>
                <a-button type="primary" @click="entryGicMdrf">
                    <span>保存</span>
                </a-button> -->
            </template>
        </a-modal>
        <!-- 注册证区域结束 -->
    </div>
</template>

<script>
import {
    platformWarningCertListAPI,
    platformWarningMdrfListAPI,
    orgWarningCertListAPI,
    orgWarningMdrfListAPI,
    orgWarningValidityListAPI,
    platformProductMdrfInfo
    // platformProductMdrfEdit
} from '@/service/pageAjax';
import moment from 'moment';
import { expiryDateWarning } from '@/config/constants';
import entryGic from '@/views/ptm/productManage/module/entryGic';
import { identification } from '../mixins/identification.js';

export default {
    name: 'earlyWarningTable', // 预警 = 外层表格
    props: {
        refName: {
            // 表格的ref，同时也是当前tab选项卡的标识key
            type: String,
            required: true
        },
        scrollYNumber: {
            // 表格的高度所需值
            type: Number,
            default: 0
        },
        isRefresh: {
            // 刷新表格的数据标识
            type: Boolean,
            default: false
        }
    },
    components: {
        entryGic
    },
    data() {
        return {
            columns: [],
            // 资证预警 - 表格指定列
            columnsCERTIFICATIONEARLY: [
                {
                    'width': '140px',
                    'field': 'organization_name',
                    'title': '机构名称'
                },
                {
                    'width': '120px',
                    'field': 'certification_name',
                    'title': '资证名称'
                },
                {
                    'width': '100px',
                    'field': 'expiration_date',
                    'title': '过期日期'
                },
                {
                    'width': '140px',
                    'field': 'social_credit_code',
                    'title': '统一社会信用代码'
                },
                {
                    'width': '100px',
                    'field': 'phone',
                    'title': '电话'
                },
                {
                    'width': '120px',
                    'field': 'email',
                    'title': '邮箱'
                }
            ],
            // 产品注册证预警 - 表格指定列
            columnsPRODUCTREGISTRATION: [
                {
                    'width': '140px',
                    'field': 'vendor_name',
                    'title': '厂商名称'
                },
                {
                    'width': '160px',
                    'field': 'product_name',
                    'title': '产品名称'
                },
                {
                    'width': '140px',
                    'field': 'register_sn',
                    'title': '注册证号'
                },
                {
                    'width': '100px',
                    'field': 'expiration_date',
                    'title': '过期日期'
                },
                {
                    'width': '100px',
                    'field': 'issue_organization',
                    'title': '发证机关'
                }
            ],
            // 产品效期预警 - 表格指定列
            columnsPRODUCTEXPIRY: [
                {
                    'width': '140px',
                    'field': 'vendor_name',
                    'title': '厂商名称'
                },
                {
                    'width': '160px',
                    'field': 'product_name',
                    'title': '产品名称'
                },
                {
                    'width': '140px',
                    'field': 'specification',
                    'title': '规格型号'
                },
                {
                    'width': '80px',
                    'field': 'batch_number',
                    'title': '批号'
                },
                {
                    'width': '100px',
                    'field': 'expiration_date',
                    'title': '过期日期'
                }
            ],
            datas: [],
            // 资证预警 - 表格数据
            datasCERTIFICATIONEARLY: [],
            // 产品注册证预警 - 表格数据
            datasPRODUCTREGISTRATION: [],
            // 产品效期预警 - 表格数据
            datasPRODUCTEXPIRY: [],
            // 资证预警 - 表格分页器数据
            paginationCERTIFICATIONEARLY: {
                loading: false,
                total: 0,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            // 产品注册证预警 - 表格分页器数据
            paginationPRODUCTREGISTRATION: {
                loading: false,
                total: 0,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            // 产品效期预警 - 表格分页器数据
            paginationPRODUCTEXPIRY: {
                loading: false,
                total: 0,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            // 产品注册证区域
            entrySt: false,
            gicMdrfObj: {
                address: '',
                application_range: '',
                contraindication: '',
                description: '',
                images: [],
                issue_date: '',
                issue_organization: '',
                new: false,
                performance_structure: '',
                product_name: '',
                production_address: '',
                register_sn: '',
                specifications: '',
                standard: '',
                vendor_name: '',
                isOnlyView: true // 只读属性
            },
            newMdrfGicData: {},
            otherCertImgs: [] // 针对于打印的，“营业执照”和“医疗器械生产许可证”的图片数组，从entry-gic组件中回传回来
        };
    },
    methods: {
        async getTableDatasAPIFn(service, params) {
            this.pagination.loading = true;
            await service(params.current, params.pageSize)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res,
                            datas = this[`datas${this.refName}`],
                            refDom = this.$refs[this.refName];

                        this[`datas${this.refName}`].push(...rows);
                        this[`pagination${this.refName}`].total = total;

                        refDom.loadData(datas);
                        if (datas && datas.length) {
                            refDom.setCurrentRow(datas[0]);
                        } else {
                            refDom.clearCurrentRow();
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.pagination.loading = false;
                    this[`pagination${this.refName}`].loading = false;
                });
        },
        async platformProductMdrfInfoFn(row) {
            const { vendor_inner_sn: vid, product_inner_sn: pid, mdrf_inner_sn: mid } = row;
            await platformProductMdrfInfo(vid, pid, mid)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        // 重新构造注册证信息
                        const tempGicMdrfData = {
                            ...this.gicMdrfObj,
                            ...row,
                            ...info
                        };
                        this.oldMdrfObj = JSON.parse(JSON.stringify(tempGicMdrfData));
                        this.gicMdrfObj = JSON.parse(JSON.stringify(tempGicMdrfData));
                        this.mdrfIsNew = true;
                        this.entrySt = true;
                        this.$nextTick(() => {
                            if (this.$refs.mdrfGicPage) {
                                this.$Utils.globalDragCenterFn('entryModal');
                                this.$refs.mdrfGicPage.refreshPage();
                            } else {
                                this.$Utils.globalDragCenterFn('entryModal', true);
                            }
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this[`pagination${this.refName}`].loading = false;
                });
        },
        // async entryGicMdrf () {
        //     const xTable = this.$refs[this.refName],
        //         tableData = xTable.getTableData().tableData,
        //         row = tableData && tableData.length ? xTable.getCurrentRow() : null

        //     this.$refs.mdrfGicPage.returnPageData()
        //     let oldData = JSON.parse(JSON.stringify(this.oldMdrfObj))
        //     let newData = JSON.parse(JSON.stringify(this.newMdrfGicData))
        //     let editInfoFlag = true
        //     let imgListNew = newData.images
        //     let imgListOld = oldData.images
        //     oldData.images = ''
        //     newData.images = ''
        //     let finalData = {}
        //     for (let server in newData) {
        //         let serverItem = newData[server]
        //         for (let old in oldData) {
        //             let oldItem = oldData[old]
        //             if (server === old && serverItem !== oldItem) {
        //                 finalData = {
        //                     ...finalData,
        //                     [server]: serverItem, // 新值
        //                     [server + '_old']: oldItem // 旧值
        //                 }
        //                 editInfoFlag = false
        //             }
        //         }
        //     }
        //     if (imgListNew !== imgListOld) {
        //         finalData.images = imgListNew
        //         editInfoFlag = false
        //     } else {
        //         editInfoFlag = true
        //     }
        //     newData.register_sn = this.$Utils.trim(newData.register_sn)
        //     if (!newData.register_sn || newData.register_sn === '') {
        //         this.$warning({
        //             title: '操作提示',
        //             content: '注册号不能为空',
        //             okText: '知道了',
        //             centered: true,
        //             maskClosable: true
        //         })
        //         return false
        //     }
        //     if (!newData.begin_date || newData.begin_date === '') {
        //         this.$warning({
        //             title: '操作提示',
        //             content: '有效期不能为空',
        //             okText: '知道了',
        //             centered: true,
        //             maskClosable: true
        //         })
        //         return false
        //     }
        //     newData.product_name = this.$Utils.trim(newData.product_name)
        //     if (!newData.product_name || newData.product_name === '') {
        //         this.$warning({
        //             title: '操作提示',
        //             content: '产品名称不能为空',
        //             okText: '知道了',
        //             centered: true,
        //             maskClosable: true
        //         })
        //         return
        //     }
        //     if (!newData.issue_date) {
        //         this.$warning({
        //             title: '操作提示',
        //             content: '发证日期不能为空',
        //             okText: '知道了',
        //             centered: true,
        //             maskClosable: true
        //         })
        //         return
        //     }
        //     if (!editInfoFlag) {
        //         const {vendor_inner_sn: vid, product_inner_sn: pid, mdrf_inner_sn: mid} = row
        //         await platformProductMdrfEdit(vid, pid, mid, finalData).then(res => {
        //             if (parseFloat(res.code) === 0) {
        //                 this.entrySt = false
        //                 // this.chooseTrObj = res.info
        //                 // this.chooseTrObj.key = this.chooseTrObj.mdrf_inner_sn + '' + this.chooseTrObj.product_inner_sn + '' + this.chooseTrObj.vendor_inner_sn
        //                 // this.tableData.forEach((item, idx) => {
        //                 //     if (item.key === this.chooseTrObj.key) {
        //                 //         this.tableData.splice(idx, 1, this.chooseTrObj)
        //                 //         this.cellClickFn(this.chooseTrObj)
        //                 //     }
        //                 // })
        //             } else {
        //                 this.$message.error(res.msg)
        //             }
        //         })
        //     } else {
        //         this.entrySt = false
        //     }
        // },
        getOtherCertImgsForPrint(imgs) {
            this.otherCertImgs = imgs;
        },
        getMdrfGicData(data) {
            this.newMdrfGicData = data;
        },
        rowClassName({ row }) {
            if (row.expiration_date && moment(new Date()).isAfter(row.expiration_date)) {
                return 'red-dashed-odo-order';
            }
        },
        initColumns() {
            this.columns = this[`columns${this.refName}`];
        },
        initTable() {
            this.datas = [];
            this[`datas${this.refName}`] = [];
            this[`pagination${this.refName}`] = {
                loading: false,
                total: 0,
                current: 1,
                pageSize: this.isRefresh ? this[`pagination${this.refName}`].pageSize : 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            };
        },
        getServiceApiFn() {
            switch (this.refName) {
                case expiryDateWarning['0'].value:
                    if (this.isPlatform) {
                        this.getTableDatasAPIFn(platformWarningCertListAPI, this.paginationCERTIFICATIONEARLY);
                    } else {
                        this.getTableDatasAPIFn(orgWarningCertListAPI, this.paginationCERTIFICATIONEARLY);
                    }
                    break;
                case expiryDateWarning['1'].value:
                    if (this.isPlatform) {
                        this.getTableDatasAPIFn(platformWarningMdrfListAPI, this.paginationPRODUCTREGISTRATION);
                    } else {
                        this.getTableDatasAPIFn(orgWarningMdrfListAPI, this.paginationPRODUCTREGISTRATION);
                    }
                    break;
                case expiryDateWarning['2'].value:
                    if (!this.isPlatform) {
                        this.getTableDatasAPIFn(orgWarningValidityListAPI, this.paginationPRODUCTEXPIRY);
                    }
                    break;
            }
        },
        initTableData() {
            this.initColumns();
            this.initTable();
            this.getServiceApiFn();
        },
        changePaginationFn({ currentPage, pageSize }) {
            this[`datas${this.refName}`] = [];
            this[`pagination${this.refName}`].current = currentPage;
            this[`pagination${this.refName}`].pageSize = pageSize;
            this[`pagination${this.refName}`].loading = true;
            this.getServiceApiFn();
        },
        cellDblclickFn({ row }) {
            // 暂时只有产品注册证预警可以查看
            if (this.refName === expiryDateWarning['1'].value) {
                this.entrySt = true;
                this.platformProductMdrfInfoFn(row); // 打开注册证登记表录入
            }
        }
    },
    mixins: [identification],
    computed: {
        pagination() {
            return this[`pagination${this.refName}`];
        },
        mdrfModalTitle() {
            return `${this.$route.meta.title} - 医疗器械注册证（登记表） - 查看`;
        }
    },
    watch: {
        refName: {
            handler(newVal, oldVal) {
                if (newVal !== oldVal) {
                    this.initTableData();
                }
            },
            immediate: true
        },
        isRefresh(newVal) {
            if (newVal) {
                this.initTableData();
            }
        }
    }
};
</script>

<style lang="less" scoped></style>
