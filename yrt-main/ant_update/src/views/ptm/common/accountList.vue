<template>
    <div class="scoped scoped-white" style="background: #fff">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="hight-search-wrap" style="padding-top: 0px">
                <a-col :span="23" class="col-list">
                    <ul class="form">
                        <li>
                            <span class="label">帐号/电话</span>
                            <a-input v-model="searchBar.account" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">帐号名称</span>
                            <a-input v-model="searchBar.organization_name" type="text" placeholder="" />
                        </li>
                        <li class="ant-calendar-picker-wrap">
                            <span class="label">日期</span>
                            <a-range-picker :placeholder="['', '']" @change="searchBarDateChangeFn"> </a-range-picker>
                        </li>
                        <li>
                            <span class="label">类型</span>
                            <a-select v-model="searchBar.type" type="text" placeholder="">
                                <a-select-option :value="0"> 机构管理员密码 </a-select-option>
                                <a-select-option :value="1"> 员工密码 </a-select-option>
                            </a-select>
                        </li>
                        <li>
                            <a-button @click="searchBtnFn" type="primary"> 搜索 </a-button>
                        </li>
                    </ul>
                </a-col>
                <div class="refresh" style="text-align: right; background: '#fff'; position: absolute; right: 0px">
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
            </a-row>
        </div>
        <!--头部区域结束-->

        <div class="tab-table-wrap tab-table-overflowy" style="background: '#fff'; margin-top: 0px">
            <div>
                <vxe-grid
                    ref="PendingOutTable"
                    size="small"
                    :height="scrollYNumber - 1"
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
    </div>
</template>

<script>
import { accountListAPI } from '@/service/pageAjax';

export default {
    name: 'useRecord',
    data() {
        return {
            // 顶部搜索栏数据
            searchBar: {
                isSearching: false,
                // 经手人
                account: '',
                // 编号
                begin_date: '',
                // 开始日期
                end_date: '',
                // 结束日期
                organization_name: '',
                type: ''
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
                Loading: false
            },
            // 数据列设置
            commonTableColumn: [
                {
                    title: '帐号/电话',
                    dataIndex: 'account',
                    key: 'account',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '操作人',
                    dataIndex: 'operator',
                    key: 'operator',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '帐号名称',
                    dataIndex: 'organization_name',
                    key: 'organization_name',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '初始密码',
                    dataIndex: 'password',
                    key: 'password',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '注册时间',
                    dataIndex: 'register_time',
                    key: 'register_time',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '类型',
                    dataIndex: 'type_name',
                    key: 'type_name',
                    minWidth: '20',
                    align: 'center'
                }
            ]
        };
    },
    methods: {
        /** ********************** 接口区域开始 ********************************************/

        /**
         * 获取使用记录数据接口调用
         * @async api:getWarehouseTransferListAPIFnAsync
         */
        async accountListAPIFn() {
            let params = {
                account: this.searchBar.account,
                begin_date: this.searchBar.begin_date,
                end_date: this.searchBar.end_date,
                organization_name: this.searchBar.organization_name,
                type: this.searchBar.type
            };

            await accountListAPI(params, this.pager.pageNum, this.pager.pageSize).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.pager.total = res.total;
                    if (res.rows.length !== 0) {
                        this.table.Datas = res.rows.map((m) => {
                            m.type_name = m.type === 1 ? '员工密码' : '机构管理员密码';
                            return m;
                        });
                    }
                    this.table.Loading = false;
                }
            });
        },
        /** ********************** 接口区域结束 ********************************************/

        /**
         * 搜索按钮对e应方法
         */
        searchBtnFn() {
            this.table.Datas = [];
            this.table.Loading = true;
            this.pager.pageNum = 1;
            this.pager.pageSize = 10;
            this.accountListAPIFn();
        },
        /**
         * 搜索中选择日期方法
         * @param value -
         * @param dateString 获取的日期
         */
        searchBarDateChangeFn(value, dateString) {
            // 获取变更数据
            const [beginTime, endTime] = dateString;
            // 赋值到搜索栏数据
            this.searchBar.begin_date = beginTime;
            this.searchBar.end_date = endTime;
        },
        /**
         * 刷新表格的方法
         */
        refreshTableFn() {
            this.table.Loading = true;
            this.accountListAPIFn();
        },
        /**
         * 新增调拨单窗口关闭方法
         */
        ontablePaginationChangeFn({ currentPage, pageSize }) {
            this.pager.pageNum = currentPage;
            this.pager.pageSize = pageSize;

            this.refreshTableFn();
        },
        initPage() {
            this.table.Datas = [];
            this.table.Loading = false;
            this.pager.pageNum = 1;
            this.pager.pageSize = 10;
            this.searchBar.type = 1;
            this.searchBar.account = '';
            this.searchBar.begin_date = '';
            this.searchBar.end_date = '';
            this.searchBar.organization_name = '';
            this.table.Loading = true;
            this.accountListAPIFn();
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
            return clientHeight - 166;
        }
    },
    mounted() {
        this.initPage();
    }
};
</script>

<style scoped></style>
