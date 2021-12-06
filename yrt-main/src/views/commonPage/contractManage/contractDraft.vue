<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button type="primary" size="small" @click="openCreateTip"> 新增 </a-button>
                            <a-button type="primary" size="small" @click="openEdit"> 修改 </a-button>
                            <a-button type="danger" size="small" @click="openDelContract"> 删除 </a-button>
                        </li>
                        <li>
                            <a-button type="primary" size="small" @click="openApprove"> 发起审批 </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-show="!hightSearchObj.isArrow"
                            v-model="searchData.name"
                            placeholder="名称"
                            size="small"
                            enterButton
                            class="search"
                            @search="loadTable(chooseTrObj)"
                        />
                        <div class="hight-search-text" @click="hightSearchIconFn">
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
                            <span class="label">合同编号</span>
                            <a-input v-model="searchData.contract_sn" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">名称</span>
                            <a-input v-model="searchData.name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">甲方</span>
                            <a-input v-model="searchData.partA_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">乙方</span>
                            <a-input v-model="searchData.partB_name" type="text" placeholder="" />
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
            style="padding: 8px; border: 0"
        >
            <!-- 表格头部 -->
            <vxe-grid
                ref="xTable"
                size="small"
                :columns="columns"
                :height="(hightSearchObj.isArrow ? scrollYNumber - 60 : scrollYNumber) + 124"
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
        <!--新增提示-->
        <a-modal
            title=""
            :visible="createTip"
            :width="400"
            :maskClosable="false"
            :centered="true"
            @cancel="createTip = false"
            class="modal-form-input-scoped global-drag create_tip"
        >
            <div v-globalDrag="{ el: 'create_tip' }" class="modal-header-wrap">
                <h6> </h6>
            </div>
            <template slot="footer">
                <a-button type="primary" @click="openCreate">确定</a-button>
            </template>
            <div class="create-tip-box">
                <div class="tip-q">即将创建合同草稿，请问您是甲方，还是乙方？</div>
                <div class="radio-box">
                    <a-radio-group v-model="createTipRadio">
                        <a-radio :value="1">甲方</a-radio>
                        <a-radio :value="2">乙方</a-radio>
                    </a-radio-group>
                </div>
            </div>
        </a-modal>
        <!--新增合同-->
        <a-modal
            title="新增合同"
            :visible="createMd.alert"
            :width="1200"
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
            <create-draft
                ref="createDraftMd"
                @returnData="saveContractFn"
                :isCloseCreateDraft="createMd.alert"
                :partType="createTipRadio"
                :titType="'create'"
            ></create-draft>
        </a-modal>
        <a-modal
            title="修改合同"
            :visible="editMd.alert"
            :width="1200"
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
            <edit-draft
                ref="editDraftMd"
                @returnData="editContractFn"
                :isCloseCreateDraft="editMd.alert"
                :partType="editPartType"
                :editObj="editObj"
            ></edit-draft>
        </a-modal>
        <a-modal
            title="发起审批"
            :visible="createApproveMd.alert"
            :width="500"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="cancelFn"
            class="modal-form-input-scoped global-drag create-approve-md"
        >
            <div v-globalDrag="{ el: 'create-approve-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ createApproveMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <div class="modal-tip">提示：请先点击“添加审批人”按钮添加审批人。</div>
                <a-button :loading="createApproveMd.loading" type="primary" @click="sendApproveBack">开始审批</a-button>
            </template>
            <create-approve ref="createApproveMd" @returnData="approveAdd" :approveType="approveType"></create-approve>
        </a-modal>
    </div>
</template>

<script>
import {
    contractdraftlistAPI,
    contractAddAPI,
    contractEditAPI,
    contractDelAPI,
    approveAddAPI
} from '@/service/pageAjax';

import createDraft from './module/createDraft';
import editDraft from './module/editDraft';
import createApprove from './module/createApprove';

export default {
    name: 'contractDraft',
    components: {
        createDraft,
        createApprove,
        editDraft
    },
    data() {
        return {
            editPartType: 2,
            editObj: {},
            nowOrgType: 2, // 权限 1：医院 2：经销商 3：产商
            createTip: false,
            isChooseTr: false,
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            createTipRadio: 1,
            searchData: {
                // 合同名称
                name: '',
                // 合同编号
                contract_sn: '',
                // 甲方
                partA_name: '',
                // 乙方
                partB_name: ''
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
                    title: '合同编号',
                    dataIndex: 'contract_sn',
                    minWidth: '200',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.contract_sn];
                        }
                    }
                },
                {
                    title: '合同名称',
                    dataIndex: 'name',
                    minWidth: '200',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.name];
                        }
                    }
                },
                {
                    title: '甲方',
                    dataIndex: 'partA_name',
                    minWidth: '200',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.partA_name];
                        }
                    }
                },
                {
                    title: '乙方',
                    dataIndex: 'partB_name',
                    minWidth: '200',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.partB_name];
                        }
                    }
                },
                {
                    title: '草稿拟定人',
                    dataIndex: 'inputer_name',
                    minWidth: '200',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.inputer_name];
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
            },
            createApproveMd: {
                title: `${this.$route.meta.title} - 发起审批`,
                alert: false,
                loading: false
            },
            approveType: {},
            saveContractLoading: false
        };
    },
    methods: {
        /** 表格方法区 **/
        async loadTable(chooseObj) {
            if (this.$Utils.checkInputValFn(this.searchData.name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                this.pagination.loading = false;
                return false;
            }
            this.pagination.loading = true;
            let params = this.searchData;
            params.partA = this.$cookie.get('userbelong');
            await contractdraftlistAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.tableData = res.rows.map((item) => {
                        item.isActive = '';
                        item.key = item.contract_type + '' + item.contract_inner_sn + '' + item.partA + '' + item.partB;
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
            // this.chooseTr(record.key)
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

        /** 头部按钮方法区 **/
        onChange() {},
        openCreateTip() {
            // 打开新增提示
            if (this.nowOrgType === 1) {
                this.createTipRadio = 1;
                this.openCreate();
            }
            if (this.nowOrgType === 2) {
                this.$Utils.globalDragCenterFn('create_tip');
                this.createTip = true;
            }
            if (this.nowOrgType === 3) {
                this.createTipRadio = 2;
                this.openCreate();
            }
        },
        openCreate() {
            // 打开新增
            this.$Utils.globalDragCenterFn('create-contract-md');
            this.createTip = false;
            this.createMd.alert = true;
            if (this.$refs.createDraftMd) {
                this.$refs.createDraftMd.pageRefresh();
            }
        },
        sendCreateBack() {
            // 发送回调请求
            this.$refs.createDraftMd.returnData();
            // this.createMd.alert = false
        },
        async saveContractFn(dataObj) {
            // 新增合同
            if (this.saveContractLoading) {
                return;
            }

            let endDate = new Date(dataObj.end_date.replace(/-/g, '/'));
            let nowDate = new Date(this.getNowDate().replace(/-/g, '/'));
            if (endDate < nowDate) {
                this.$warning({
                    title: '操作提示',
                    content: '有效期结束日期不能小于当前日期',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            this.saveContractLoading = true;
            dataObj.status = 0; // 草稿
            await contractAddAPI(dataObj)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.createMd.alert = false;
                        let obj = res.info;
                        obj.key = obj.contract_type + '' + obj.contract_inner_sn + '' + obj.partA + '' + obj.partB;
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
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.saveContractLoading = false;
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
            this.editPartType = 2;
            this.editObj = this.$XEUtils.clone(this.chooseTrObj, true);
            this.$Utils.globalDragCenterFn('edit-contract-md');
            this.editMd.alert = true;
            if (this.$refs.editDraftMd) {
                this.$refs.editDraftMd.pageRefresh();
            }
        },
        sendEditBack() {
            // 发送回调请求
            this.$refs.editDraftMd.returnData();
        },
        /*
         *  dataObj 需要提交的数据
         *  changed 是否修改过 未修改不发送请求
         * */
        async editContractFn(dataObj, changed) {
            // 修改合同
            if (!changed) {
                this.editMd.alert = false;
                return;
            }

            if (dataObj.end_date) {
                let endDate = new Date(dataObj.end_date.replace(/-/g, '/'));
                let nowDate = new Date(this.getNowDate().replace(/-/g, '/'));
                if (endDate < nowDate) {
                    this.$warning({
                        title: '操作提示',
                        content: '有效期结束日期不能小于当前日期',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
            }
            await contractEditAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.editMd.alert = false;

                    // 更新当前编辑的行数据
                    const editObjKeys = Object.keys(this.chooseTrObj);
                    Object.assign(this.chooseTrObj, {
                        ...this.$XEUtils.pick(dataObj, editObjKeys)
                    });

                    // this.loadTable(this.chooseTrObj)
                } else {
                    this.$message.error(res.msg);
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
            let params = {
                partA: this.chooseTrObj.partA,
                partB: this.chooseTrObj.partB,
                contract_inner_sn: this.chooseTrObj.contract_inner_sn,
                contract_type: this.chooseTrObj.contract_type
            };
            await contractDelAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.loadTable();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        openApprove() {
            // 打开发起审批
            this.approveType = {
                partA: this.chooseTrObj.partA,
                partB: this.chooseTrObj.partB,
                contract_inner_sn: this.chooseTrObj.contract_inner_sn,
                contract_type: this.chooseTrObj.contract_type
            };
            this.$Utils.globalDragCenterFn('create-approve-md');
            if (this.$refs.createApproveMd) {
                this.$refs.createApproveMd.refreshPage();
            }
            this.createApproveMd.alert = true;
        },
        sendApproveBack() {
            this.$refs.createApproveMd.returnData();
        },
        async approveAdd(data) {
            await approveAddAPI(data).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.createApproveMd.alert = false;
                    this.loadTable();
                    this.cancelFn();
                } else {
                    this.$message.error(res.msg);
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
            return clientHeight - topHeight;
        }
    },
    created() {},
    mounted() {
        this.nowOrgType = parseFloat(this.$cookie.get('userSystemType'));
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
