<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button v-if="nowOrgType != 1" type="primary" size="small" @click="openCreate">
                                新增
                            </a-button>
                            <a-button type="primary" size="small" @click="openEdit"> 修改 </a-button>
                            <a-button type="danger" size="small" @click="openDelContract"> 删除 </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-show="!hightSearchObj.isArrow"
                            v-model="searchData.search_name"
                            placeholder="名称"
                            size="small"
                            @search="loadTable(chooseTrObj)"
                            enterButton
                            class="search"
                        />
                        <div @click="hightSearchIconFn" class="hight-search-text">
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
                            <span class="label">
                                <span v-if="nowOrgType === 1"> 经销商 </span>
                                <span v-else>医院名称</span>
                            </span>
                            <a-input v-model="searchData.search_name" type="text" placeholder="" />
                        </li>
                        <li v-if="nowOrgType !== 1">
                            <span class="label">医院别名</span>
                            <a-input v-model="searchData.search_alias" type="text" placeholder="" />
                        </li>
                        <li>
                            <a-button @click="loadTable(chooseTrObj)" type="primary"> 搜索 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->
        <div
            :class="hightSearchObj.isArrow ? 'top136' : 'top56'"
            class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
            style="padding: 8px; border: 0px"
        >
            <!-- 表格头部 -->
            <vxe-grid
                ref="xTable"
                size="small"
                :columns="columns"
                :height="scrollYNumber + 124"
                style="margin-top: 8px"
                resizable
                auto-resize
                border
                highlight-current-row
                @cell-click="cellClickFn"
                @cell-dblclick="cellDblclickFn"
                align="center"
                :data="tableData"
                :loading="pagination.loading"
            >
            </vxe-grid>
            <!--表格区域-->
        </div>
        <!-- modal区 -->
        <!--新增合同-->
        <a-modal
            title="新增"
            :visible="createMd.alert"
            :width="620"
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
            <create-op-cooper ref="createOpMd" @returnData="saveFn" :openType="openTypeCreate"></create-op-cooper>
        </a-modal>
        <a-modal
            title="修改"
            :visible="editMd.alert"
            :width="620"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="editMd.alert = false"
            class="modal-form-input-scoped global-drag edit-contract-md"
        >
            <div v-globalDrag="{ el: 'edit-contract-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ editMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="editMd.alert = false">取消</a-button>
                <a-button :loading="editMd.loading" type="primary" @click="sendEditBack">保存</a-button>
            </template>
            <create-op-cooper
                ref="editOpMd"
                @returnData="editContractFn"
                :openType="openTypeEdit"
                :editObj="editObj"
            ></create-op-cooper>
        </a-modal>
    </div>
</template>

<script>
import {
    intentionList,
    intentionAddAPI,
    intentionEditAPI,
    intentionInfoAPI,
    intentionDelAPI
} from '@/service/pageAjax';

import createOpCooper from './module/opCooperDetail';

export default {
    name: 'opCooperationDea',
    components: {
        createOpCooper
    },
    data() {
        return {
            editObj: {},
            nowOrgType: 2, // 权限 1：医院 2：经销商 3：产商
            openTypeCreate: 'create',
            openTypeEdit: 'edit',
            isChooseTr: false,
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            searchData: {
                search_name: '',
                search_alias: ''
            },
            pagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            columns: [
                {
                    title: '医院名称',
                    dataIndex: 'hospital_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '200',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.hospital_name];
                        }
                    }
                },
                {
                    title: '医院别名',
                    dataIndex: 'hospital_alias',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '200',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.hospital_alias];
                        }
                    }
                },
                {
                    title: '开始日期',
                    dataIndex: 'begin_date',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
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
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.end_date];
                        }
                    }
                },
                {
                    title: '颅骨',
                    dataIndex: 't0_status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let val = '';
                            let color = '';
                            let text = row.t0_status;
                            if (parseInt(text) === 0) {
                                val = '无意向';
                                color = 'red_ft';
                            }
                            if (parseInt(text) === 1) {
                                val = '有意向';
                                color = 'green_ft';
                            }
                            if (parseInt(text) === 2) {
                                val = '意向待确认';
                                color = 'yellow_ft';
                            }
                            return [<span class={color}>{val}</span>];
                        }
                    }
                },
                {
                    title: '脊柱',
                    dataIndex: 't1_status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let val = '';
                            let color = '';
                            let text = row.t1_status;
                            if (parseInt(text) === 0) {
                                val = '无意向';
                                color = 'red_ft';
                            }
                            if (parseInt(text) === 1) {
                                val = '有意向';
                                color = 'green_ft';
                            }
                            if (parseInt(text) === 2) {
                                val = '意向待确认';
                                color = 'yellow_ft';
                            }
                            return [<span class={color}>{val}</span>];
                        }
                    }
                },
                {
                    title: '关节',
                    dataIndex: 't2_status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let val = '';
                            let color = '';
                            let text = row.t2_status;
                            if (parseInt(text) === 0) {
                                val = '无意向';
                                color = 'red_ft';
                            }
                            if (parseInt(text) === 1) {
                                val = '有意向';
                                color = 'green_ft';
                            }
                            if (parseInt(text) === 2) {
                                val = '意向待确认';
                                color = 'yellow_ft';
                            }
                            return [<span class={color}>{val}</span>];
                        }
                    }
                },
                {
                    title: '创伤',
                    dataIndex: 't3_status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let val = '';
                            let color = '';
                            let text = row.t3_status;
                            if (parseInt(text) === 0) {
                                val = '无意向';
                                color = 'red_ft';
                            }
                            if (parseInt(text) === 1) {
                                val = '有意向';
                                color = 'green_ft';
                            }
                            if (parseInt(text) === 2) {
                                val = '意向待确认';
                                color = 'yellow_ft';
                            }
                            return [<span class={color}>{val}</span>];
                        }
                    }
                }
            ],
            partStatus: {
                partA: '',
                partB: ''
            },
            chooseTrObj: {},
            tableData: [],
            createMd: {
                title: `${this.$route.meta.title} - 新增`,
                alert: false,
                loading: false
            },
            editMd: {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            }
        };
    },
    methods: {
        /** 表格方法区 **/
        async loadTable(chooseObj) {
            if (
                this.$Utils.checkInputValFn(this.searchData.search_name) ||
                this.$Utils.checkInputValFn(this.searchData.search_alias)
            ) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            this.pagination.loading = true;
            let params = this.searchData;
            params.org_id = this.$cookie.get('userbelong');
            await intentionList({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.tableData = res.list.map((item) => {
                        item.isActive = '';
                        item.key = item.hospital_inner_sn + '' + item.dealer_inner_sn;
                        return item;
                    });
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
                    }
                    this.pagination.loading = false;
                    this.$refs.xTable.refreshColumn(); // 防止错位
                }
                this.pagination.loading = false;
            });
        },
        changePaginationFn(page) {
            // 翻页
            this.pagination = {
                ...page,
                loading: true
            };
            this.loadTable();
        },
        cellClickFn(row) {
            // 单击行
            let obj = row.row || row;
            this.chooseTrEvent(obj);
        },
        cellDblclickFn({ row }) {
            this.chooseTrEvent(row);
            this.openEdit();
        },
        chooseTrEvent(record) {
            // 选中事件
            this.isChooseTr = true;
            this.chooseTrObj = record;
            this.editObj = this.$XEUtils.clone(this.chooseTrObj, true);
            setTimeout(() => {
                this.$refs.xTable.setCurrentRow(this.chooseTrObj); // 默认选中
            }, 300);
        },
        /** 表格方法区结束 **/

        /** 头部按钮方法区 **/
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        onChange() {},
        openCreate() {
            // 打开新增
            this.$Utils.globalDragCenterFn('create-contract-md');
            this.createMd.alert = true;
            if (this.$refs.createOpMd) {
                this.$refs.createOpMd.pageRefresh();
            }
        },
        sendCreateBack() {
            // 发送回调请求
            this.$refs.createOpMd.returnData();
            // this.createMd.alert = false
        },
        async saveFn(dataObj) {
            // 新增
            await intentionAddAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.createMd.alert = false;
                    let obj = res.info;
                    obj.key = obj.hospital_inner_sn + '' + obj.dealer_inner_sn;
                    obj.isActive = '';
                    this.chooseTrObj = obj;
                    // 添加行
                    const table = this.$refs.xTable;
                    table &&
                        table.insert(obj).then(({ row }) => {
                            table.setCurrentRow(row);
                        });
                    // this.loadTable(this.chooseTrObj)
                } else {
                    this.$message.error(`${res.msg}`);
                }
            });
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
        openEdit() {
            // 打开修改
            if (!this.chooseTrObj.key) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            this.$Utils.globalDragCenterFn('edit-contract-md');
            this.editMd.alert = true;
            if (this.$refs.editOpMd) {
                this.$refs.editOpMd.pageRefresh();
            }
        },
        async getEditInfo() {
            // 获取修改详情
            let did = this.chooseTrObj.dealer_inner_sn;
            let hid = this.chooseTrObj.hospital_inner_sn;
            await intentionInfoAPI(hid, did).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let obj = res.info;
                    obj.key = obj.hospital_inner_sn + '' + obj.dealer_inner_sn;
                    obj.isActive = '';
                    this.chooseTrObj = obj;
                    this.loadTable(this.chooseTrObj);
                }
            });
        },
        sendEditBack() {
            // 发送回调请求
            this.$refs.editOpMd.returnData();
        },
        async editContractFn(dataObj) {
            // 修改合同
            // 对比原始数据是否有过修改操作  未修改过不发送请求
            const oldData = this.chooseTrObj;
            const changed = Object.keys(dataObj).some((key) => {
                return Object.hasOwnProperty.call(oldData, key) && oldData[key] !== dataObj[key];
            });

            if (!changed) {
                this.editMd.alert = false;
                return;
            }

            await intentionEditAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res,
                        refsXTable = this.$refs.xTable;
                    this.editMd.alert = false;
                    for (let i = 0; i < this.tableData.length; i++) {
                        const item = this.tableData[i];
                        if (
                            parseInt(item.dealer_inner_sn) === parseInt(info.dealer_inner_sn) &&
                            parseInt(item.hospital_inner_sn) === parseInt(info.hospital_inner_sn)
                        ) {
                            info.isActive = '';
                            info.key = item.hospital_inner_sn + '' + item.dealer_inner_sn;
                            this.tableData.splice(i, 1, info);
                            refsXTable.reloadData(this.tableData);
                            refsXTable.setCurrentRow(info);
                            this.chooseTrEvent(refsXTable.getCurrentRow());
                            break;
                        }
                    }
                    // this.getEditInfo()
                }
            });
        },
        openDelContract() {
            if (!this.chooseTrObj.key) {
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
                title: '删除',
                content: '确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.delContractFn();
                }
            });
        },
        async delContractFn() {
            // 删除
            let did = this.chooseTrObj.dealer_inner_sn;
            let hid = this.chooseTrObj.hospital_inner_sn;
            await intentionDelAPI(hid, did).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.loadTable();
                }
            });
        },
        cancelFn() {
            this.$refs.createApproveMd.cancelFn();
            this.createApproveMd.alert = false;
        }
        /** 头部按钮方法区结束 **/
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 253;

            if (this.hightSearchObj.isArrow) {
                topHeight = 314;
            } else {
                topHeight = 253;
            }
            return clientHeight - topHeight;
        }
    },
    created() {},
    mounted() {
        this.nowOrgType = parseFloat(this.$cookie.get('userSystemType'));
        if (this.nowOrgType === 1) {
            // 医院
            this.columns.splice(0, 1);
            this.columns.splice(0, 1);
            let obj = {
                title: '经销商',
                dataIndex: 'dealer_name',
                minWidth: '400',
                align: 'center',
                slots: {
                    default: ({ row }) => {
                        return [row.dealer_name];
                    }
                }
            };
            this.columns.unshift(obj);
        }
        this.loadTable();
    }
};
</script>

<style lang="less" scoped>
.tab-table-wrap {
    background: #fff;
    padding-top: 0px !important;
    border: 1px solid rgba(0, 0, 0, 0.09);
}
.red_ft {
    color: red;
}
.green_ft {
    color: #27c795;
}
.yellow_ft {
    color: #f3a400;
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
