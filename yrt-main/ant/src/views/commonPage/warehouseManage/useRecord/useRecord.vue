<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :sm="22" :md="22" :lg="18" :xl="18" :xxl="15">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="headerBtnFn('add')" type="primary" size="small"> 新增 </a-button>
                            <a-button @click="headerBtnFn('edit')" type="primary" size="small"> 修改 </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :sm="4" :md="2" :lg="6" :xl="6" :xxl="9">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-model="searchBar.proposer_name"
                            placeholder="经手人"
                            size="small"
                            enterButton
                            class="search"
                            @search="searchBtnFn"
                        />
                        <div
                            @click.stop="searchBar.advSearchActive = !searchBar.advSearchActive"
                            class="hight-search-text"
                        >
                            <!--高级搜索按钮-->
                            <a-tooltip placement="left" title="高级搜索">
                                <a-icon :type="searchBar.advSearchActive ? 'up-circle' : 'down-circle'" />
                            </a-tooltip>
                        </div>
                    </div>
                </a-col>
            </a-row>
            <!-- 高级搜索 -->
            <a-row v-show="searchBar.advSearchActive" class="hight-search-wrap">
                <a-col :span="24" class="col-list">
                    <ul class="form">
                        <li>
                            <span class="label">经手人</span>
                            <a-input v-model="searchBar.proposer_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">单据编号</span>
                            <a-input v-model="searchBar.sn" type="text" placeholder="" />
                        </li>
                        <li class="ant-calendar-picker-wrap">
                            <span class="label">日期</span>
                            <a-range-picker :placeholder="['', '']" @change="searchBarDateChangeFn"> </a-range-picker>
                        </li>
                        <li>
                            <a-button @click="searchBtnFn" type="primary"> 搜索 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab切换区域开始 -->
        <div
            :class="searchBar.advSearchActive ? 'top136' : 'top56'"
            class="tab-table-wrap tab-table-overflowy"
            style="background: '#fff'; margin-top: 0px"
        >
            <div class="refresh" style="height: 30px; text-align: right; background: '#fff'">
                <!-- 刷新按钮  -->
                <a-tooltip slot="tabBarExtraContent" placement="left">
                    <template slot="title">
                        <span>刷新</span>
                    </template>
                    <a-button
                        type="primary"
                        shape="circle"
                        icon="reload"
                        size="small"
                        class="refresh-btn"
                        @click="refreshTableFn()"
                    />
                </a-tooltip>
            </div>
            <div>
                <vxe-grid
                    ref="PendingOutTable"
                    size="small"
                    :height="(searchBar.advSearchActive ? scrollYNumber - 60 : scrollYNumber) + 57"
                    :data="table.Datas"
                    :loading="table.Loading"
                    border
                    resizable
                    auto-resize
                    highlight-current-row
                    @cell-click="tableCellClickFn"
                    @cell-dblclick="tableCellDblclickFn"
                    @checkbox-change="selectChangeEvent"
                    align="center"
                >
                    <vxe-table-column key="index" field="index" title="No." width="50">
                        <template v-slot="{ seq }">
                            <span>{{ seq }}</span>
                        </template>
                    </vxe-table-column>
                    <vxe-table-column
                        v-for="commonTableColumnItem in commonTableColumn"
                        :key="commonTableColumnItem.key"
                        :field="commonTableColumnItem.dataIndex"
                        :title="commonTableColumnItem.title"
                        :width="commonTableColumnItem.width"
                    ></vxe-table-column>
                </vxe-grid>
                <vxe-pager
                    :current-page="pager.pageNum"
                    :page-size="pager.pageSize"
                    :page-sizes="pager.pageSizes"
                    :total="pager.total"
                    :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                    @page-change="ontablePaginationChangeFn"
                >
                </vxe-pager>
            </div>
        </div>
        <!-- 新增/编辑窗口 -->
        <addUseRecord ref="addUseRecord" :visible="addModal.visible" @onCancel="onAddModalCancel" />
    </div>
</template>

<script>
import { useRecordListAPI } from '@/service/pageAjax';

import addUseRecord from './module/addUseRecord';

export default {
    name: 'useRecord',
    components: {
        addUseRecord
    },
    data() {
        return {
            // 顶部搜索栏数据
            searchBar: {
                isSearching: false,
                // 经手人
                proposer_name: '',
                // 编号
                sn: '',
                // 开始日期
                dateBegin: '',
                // 结束日期
                dateEnd: '',
                // 是否打开高级搜索
                advSearchActive: false
            },
            // tab数据
            pager: {
                // 分页页数
                pageNum: 1,
                // 分页数据量
                pageSize: 10,
                // 数据总量
                total: 10,
                // 分页大小
                pageSizes: [10, 20, 30, 40]
            },
            // 表格数据
            table: {
                // 表格的数据
                Datas: [],
                // 是否加载中
                Loading: false,
                // 当前选中表格行数据
                currentSelectRow: {}
            },
            // 数据列设置
            commonTableColumn: [
                {
                    title: '编号',
                    dataIndex: 'rv_sn',
                    key: 'rv_sn',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '仓库',
                    dataIndex: 'warehouse_name',
                    key: 'warehouse_name',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '厂商',
                    dataIndex: 'vd_name',
                    key: 'vd_name',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
                    width: '100',
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: 'quantity',
                    width: '80',
                    align: 'center'
                },
                {
                    title: '日期',
                    dataIndex: 'create_time',
                    key: 'create_time',
                    width: '150',
                    align: 'center'
                },
                {
                    title: '经手人',
                    dataIndex: 'proposer_name',
                    key: 'proposer_name',
                    width: '100',
                    align: 'center'
                },
                {
                    title: '医保编号',
                    dataIndex: 'default_health_care_sn',
                    key: 'default_health_care_sn',
                    width: '100',
                    align: 'center'
                },
                {
                    title: '类型',
                    dataIndex: 'use_type_name',
                    key: 'use_type_name',
                    width: '100',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status_name',
                    key: 'status_name',
                    width: '80',
                    align: 'center'
                }
            ],
            // 新增/编辑窗口
            addModal: {
                visible: false,
                isAdd: true,
                sn: ''
            }
        };
    },
    methods: {
        /** ********************** 接口区域开始 ********************************************/

        /**
         * 获取使用记录数据接口调用
         * @async api:getWarehouseTransferListAPIFnAsync
         */
        async useRecordListAPIFn() {
            let params = {
                // 当前登录机构
                hospital_inner_sn: this.$cookie.get('userbelong'),
                // 分页大小
                pageSize: this.pager.pageSize,
                // 分页页数
                pageNum: this.pager.pageNum
            };

            if (this.searchBar.isSearching) {
                params.rv_sn = this.searchBar.sn;
                params.start_date = this.searchBar.dateBegin;
                params.end_date = this.searchBar.dateEnd;
                params.proposer_name = this.searchBar.proposer_name;
            }

            await useRecordListAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.pager.total = res.total;
                    const data = res.rows.map((item) => {
                        item.status_name =
                            item.status === 'DRAFT' ? '草稿' : item.status === 'CONFIRM' ? '确认' : '已删除';
                        item.use_type_name = parseInt(item.use_type) === 0 ? '病人使用' : '医生使用';
                        return item;
                    });
                    this.table.Datas = data;
                    if (this.searchBar.isSearching) {
                        this.searchBar.isSearching = false;
                    }
                }
            });
        },
        /** ********************** 接口区域结束 ********************************************/

        /**
         * 头部按钮区域对应方法
         * @param btnName 按钮名称('add','edit','delete')
         */
        headerBtnFn(btnName) {
            switch (btnName) {
                case 'add':
                    this.$Utils.globalDragCenterFn('useRecord-modal-add');
                    this.addModal.visible = true;
                    this.addModal.isAdd = true;
                    if (this.$refs.addUseRecord) {
                        this.$refs.addUseRecord.initPage(true);
                    }
                    break;
                case 'edit':
                    if (!this.table.currentSelectRow.product_name) {
                        this.$warning({
                            title: '操作提示',
                            content: '请选中一条数据进行修改',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        return;
                    }
                    this.$Utils.globalDragCenterFn('useRecord-modal-add');
                    this.addModal.visible = true;
                    this.addModal.isAdd = false;
                    this.addModal.sn = this.table.currentSelectRow.rv_inner_sn;
                    if (this.$refs.addUseRecord) {
                        this.$refs.addUseRecord.setSearchSn(this.table.currentSelectRow.rv_inner_sn);
                        this.$refs.addUseRecord.initPage(false);
                    }
                    break;
            }
        },
        /**
         * 搜索按钮对e应方法
         */
        searchBtnFn() {
            this.searchBar.isSearching = true;
            this.useRecordListAPIFn();
        },
        /**
         * 高级搜索中选择日期方法
         * @param value -
         * @param dateString 获取的日期
         */
        searchBarDateChangeFn(value, dateString) {
            // 获取变更数据
            const [beginTime, endTime] = dateString;
            // 赋值到搜索栏数据
            this.searchBar.dateBegin = beginTime;
            this.searchBar.dateEnd = endTime;
        },
        /**
         * 刷新表格的方法
         */
        refreshTableFn() {
            this.useRecordListAPIFn();
        },
        /**
         * 单击表格列触发的方法
         * @param {row} 点击的行信息
         */
        tableCellClickFn({ row }) {
            this.table.currentSelectRow = row;
        },
        /**
         * 双击表格列触发的方法
         * @param {row} 点击的行信息
         */
        tableCellDblclickFn() {
            this.headerBtnFn('edit');
        },
        /**
         * 新增调拨单窗口关闭方法
         */
        onAddModalCancel() {
            this.addModal.visible = false;
            this.refreshTableFn();
        },
        ontablePaginationChangeFn({ currentPage, pageSize }) {
            this.pager.current = currentPage;
            this.pager.pageNum = currentPage;
            this.pager.pageSize = pageSize;

            this.refreshTableFn();
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 253;
            return clientHeight - topHeight;
        }
    },
    mounted() {
        this.useRecordListAPIFn();
    }
};
</script>

<style lang="less" scoped>
@w: 24px;
.scoped.scoped-white .tab-table-wrap {
    background: #fff;
}
.order-modal-list {
    position: absolute;
    left: @w;
    right: @w;
    bottom: 57px;
    display: flex;
    flex-direction: column;
    li {
        display: flex;
        flex-direction: row;
        padding-top: 24px;

        &:nth-of-type(2) {
            .item-wrap {
                display: flex;
                flex: 1;

                &:nth-of-type(2) {
                    padding-left: 50px;
                }

                .item {
                    flex: 1;
                    padding: 0 16px 0 0;

                    &:last-child {
                        padding: 0;
                    }
                }
            }
        }

        &:last-child {
            padding-bottom: 24px;
        }
    }
}
.modal-form-input-scoped .modal-form-input .top-list .label {
    display: flex;
    width: auto;
    max-width: 94px;
}
.dragMain {
    width: 1160px;
    height: 215px;
    margin-top: 40px;
    border: 1px solid #ccc;
    z-index: 1;
    position: absolute;
    filter: alpha(opacity=1);
    opacity: 0.1;
}
.dragMainLi {
    height: 40px;
    line-height: 40px;
    float: left;
    width: 100%;
}
.ant-modal-centered {
    display: none;
}

.ant-modal-footer .ant-btn {
    z-index: 5;
}
</style>
