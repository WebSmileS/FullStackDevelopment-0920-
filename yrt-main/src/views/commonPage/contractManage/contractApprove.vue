<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button type="primary" size="small" @click="openCreate"> 审批 </a-button>
                            <!-- <a-button
                                type="danger"
                                size="small"
                            >
                                删除
                            </a-button> -->
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
                            <!-- <span>高级搜索</span> -->
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
                :height="(hightSearchObj.isArrow ? scrollYNumber - 60 : scrollYNumber) + 60"
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
                :start-index="(pagination.current - 1) * pagination.pageSize"
            >
            </vxe-grid>
            <!-- <vxe-pager
                :loading="pagination.loading"
                :current-page="pagination.current"
                :page-size="pagination.pageSize"
                :page-sizes="pagination.pageSizes"
                :total="pagination.total"
                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                @page-change="changePaginationFn">
            </vxe-pager> -->
            <!--表格区域-->
        </div>
        <!-- modal区 -->
        <!--审批-->
        <a-modal
            title="审批"
            :visible="approveMd.alert"
            :width="1200"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="approveMd.alert = false"
            class="modal-form-input-scoped global-drag approve-contract-md"
        >
            <div v-globalDrag="{ el: 'approve-contract-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ approveMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <div style="height: 32px"></div>
                <!--<a-button-->
                <!--@click="approveMd.alert = false"-->
                <!--&gt;取消</a-button>-->
                <!--<a-button-->
                <!--:loading="approveMd.loading"-->
                <!--type="primary"-->
                <!--@click="sendCreateBack"-->
                <!--&gt;保存</a-button>-->
            </template>
            <approve-detail
                ref="approveDraftMd"
                @done="saveContractFn"
                @cancel="approveMd.alert = false"
                :partType="approveType"
                :editObj="editObj"
            ></approve-detail>
        </a-modal>
    </div>
</template>

<script>
import { contractApprovelistAPI } from '@/service/pageAjax';

import approveDetail from './module/approveDetail';

export default {
    name: 'contractApprove',
    components: {
        approveDetail
    },
    data() {
        return {
            approveType: 1,
            limitType: 2, // 权限 1：医院 2：经销商 3：产商
            createTip: false,
            isChooseTr: false,
            chooseTrObj: {},
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
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
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
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
                    title: '审批状态',
                    dataIndex: 'status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let st = row.status;
                            let text = this.contractStatus[st];
                            let cls = '';
                            if (st === 2) {
                                cls = 'red';
                            }
                            return [<div class={cls}>{text}</div>];
                        }
                    }
                },
                {
                    title: '当前审批人',
                    dataIndex: 'approver_name',
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.approver_name];
                        }
                    }
                }
            ],
            contractStatus: [
                '草稿',
                '审批进行中',
                '审批未通过',
                '审批通过',
                '有效履行中',
                '自然终止',
                '提前终止',
                '超期履行'
            ],
            editObj: {},
            partStatus: {
                partA: '',
                partB: ''
            },
            tableData: [],
            approveMd: {
                title: `${this.$route.meta.title} - 审批`,
                alert: false,
                loading: false
            }
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
            const params = {
                ...this.searchData,
                partA: this.$cookie.get('userbelong')
                // pageSize: this.pagination.pageSize,
                // pageNum: this.pagination.current
            };
            await contractApprovelistAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.tableData = res.list.map((item) => {
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
                            this.$nextTick(() => {
                                this.$refs.xTable.setCurrentRow(this.tableData[0]); // 默认选中第一
                                this.cellClickFn(this.tableData[0]);
                            });
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
            this.openCreate();
        },
        chooseTrEvent(record) {
            // 选中事件
            this.chooseTrObj = record;
            this.editObj = this.chooseTrObj;
            this.isChooseTr = true;
            this.$nextTick(() => {
                setTimeout(() => {
                    this.$refs.xTable.setCurrentRow(this.chooseTrObj); // 默认选中
                }, 300);
            });
        },
        /** 表格方法区结束 **/

        /** 头部按钮方法区 **/
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },

        /** 头部按钮方法区 **/
        onChange() {},
        openCreate() {
            // 打开审批
            if (!this.isChooseTr) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            this.editObj = this.chooseTrObj;
            this.$Utils.globalDragCenterFn('approve-contract-md');
            this.createTip = false;
            this.approveMd.alert = true;
            if (this.$refs.approveDraftMd) {
                this.$refs.approveDraftMd.pageRefresh();
            }
        },
        sendCreateBack() {
            // 发送回调请求
            this.$refs.approveDraftMd.returnData();
            // this.approveMd.alert = false
        },
        async saveContractFn() {
            // 审批完成
            this.approveMd.alert = false;
            this.loadTable(this.chooseTrObj);
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
