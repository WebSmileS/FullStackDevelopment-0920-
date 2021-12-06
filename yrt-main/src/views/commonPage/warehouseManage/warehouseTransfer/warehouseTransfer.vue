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
                            <a-button
                                @click="headerBtnFn('delete')"
                                type="danger"
                                size="small"
                                :disabled="gridData.DiscardDisabled"
                            >
                                作废
                            </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :sm="4" :md="2" :lg="6" :xl="6" :xxl="9">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-model="searchBar.handler_name"
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
                            <a-input v-model="searchBar.handler_name" type="text" placeholder="" />
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
            style="background: #f0f2f5"
        >
            <a-tabs type="card" :activeKey="tabs.activeTabKey" @change="changeTabFn">
                <!-- 待调出区域  -->
                <a-tab-pane :tab="tabs.tabsName[0]" :key="tabs.tabsKey[0]" style="padding: 8px 8px 0px 8px">
                    <vxe-grid
                        ref="PendingOutTable"
                        size="small"
                        :height="(searchBar.advSearchActive ? scrollYNumber - 60 : scrollYNumber) + 43"
                        :data="gridData.PendingOutDatas"
                        :loading="gridData.PendingOutLoading"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        @cell-click="tableCellClickFn"
                        @cell-dblclick="tableCellDblclickFn"
                        @checkbox-change="selectChangeEvent"
                        @checkbox-all="selectChangeEvent"
                        align="center"
                    >
                        <vxe-table-column type="checkbox" title="全选" width="60"> </vxe-table-column>
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
                        <vxe-table-column field="action" title="操作" width="80" fixed="right">
                            <template v-slot="{ row }">
                                <a-button
                                    v-if="row.status === 'DRAFT' || row.status === 'CONFIRM'"
                                    @click="onDiscard(row)"
                                    type="danger"
                                    size="small"
                                >
                                    作废
                                </a-button>
                            </template>
                        </vxe-table-column>
                    </vxe-grid>
                    <vxe-pager
                        :current-page="tabs.pageNum"
                        :page-size="tabs.pageSize"
                        :page-sizes="tabs.pageSizes"
                        :total="tabs.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="onGridDataPaginationChangeFn"
                    >
                    </vxe-pager>
                </a-tab-pane>
                <!-- 待调入区域  -->
                <a-tab-pane :tab="tabs.tabsName[1]" :key="tabs.tabsKey[1]" style="padding: 8px 8px 0px 8px">
                    <vxe-grid
                        ref="PendingInTable"
                        size="small"
                        :height="(searchBar.advSearchActive ? scrollYNumber - 60 : scrollYNumber) + 43"
                        :data="gridData.PendingInDatas"
                        :loading="gridData.PendingInLoading"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        @cell-click="tableCellClickFn"
                        @cell-dblclick="tableCellDblclickFn"
                        @checkbox-change="selectChangeEvent"
                        @checkbox-all="selectChangeEvent"
                        align="center"
                    >
                        <vxe-table-column type="checkbox" title="全选" width="60"> </vxe-table-column>
                        <vxe-table-column key="index" field="index" title="No." width="50" :resizable="false">
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
                        <vxe-table-column field="action" title="操作" width="80" :resizable="false" fixed="right">
                            <template v-slot="{ row }">
                                <a-button
                                    v-if="row.status === 'DRAFT' || row.status === 'CONFIRM'"
                                    @click="onDiscard(row)"
                                    type="danger"
                                    size="small"
                                >
                                    作废
                                </a-button>
                            </template>
                        </vxe-table-column>
                    </vxe-grid>
                    <vxe-pager
                        :current-page="tabs.pageNum"
                        :page-size="tabs.pageSize"
                        :page-sizes="tabs.pageSizes"
                        :total="tabs.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="onGridDataPaginationChangeFn"
                    >
                    </vxe-pager>
                </a-tab-pane>
                <!-- 调出历史区域  -->
                <a-tab-pane :tab="tabs.tabsName[2]" :key="tabs.tabsKey[2]" style="padding: 8px 8px 0px 8px">
                    <vxe-grid
                        ref="HistoryOutTable"
                        size="small"
                        :height="(searchBar.advSearchActive ? scrollYNumber - 60 : scrollYNumber) + 43"
                        :data="gridData.HistoryOutDatas"
                        :loading="gridData.HistoryOutLoading"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        @cell-click="tableCellClickFn"
                        @cell-dblclick="tableCellDblclickFn"
                        @checkbox-change="selectChangeEvent"
                        @checkbox-all="selectChangeEvent"
                        align="center"
                    >
                        <vxe-table-column type="checkbox" title="全选" width="60"> </vxe-table-column>
                        <vxe-table-column key="index" field="index" title="No." width="50" :resizable="false">
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
                        :current-page="tabs.pageNum"
                        :page-size="tabs.pageSize"
                        :page-sizes="tabs.pageSizes"
                        :total="tabs.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="onGridDataPaginationChangeFn"
                    >
                    </vxe-pager>
                </a-tab-pane>
                <!-- 调入历史区域  -->
                <a-tab-pane :tab="tabs.tabsName[3]" :key="tabs.tabsKey[3]" style="padding: 8px 8px 0px 8px">
                    <vxe-grid
                        ref="HistoryInTable"
                        size="small"
                        :height="(searchBar.advSearchActive ? scrollYNumber - 60 : scrollYNumber) + 43"
                        :data="gridData.HistoryInDatas"
                        :loading="gridData.HistoryInLoading"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        @cell-click="tableCellClickFn"
                        @cell-dblclick="tableCellDblclickFn"
                        @checkbox-change="selectChangeEvent"
                        @checkbox-all="selectChangeEvent"
                        align="center"
                    >
                        <vxe-table-column type="checkbox" title="全选" width="60"> </vxe-table-column>
                        <vxe-table-column key="index" field="index" title="No." width="50" :resizable="false">
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
                        :current-page="tabs.pageNum"
                        :page-size="tabs.pageSize"
                        :page-sizes="tabs.pageSizes"
                        :total="tabs.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="onGridDataPaginationChangeFn"
                    >
                    </vxe-pager>
                </a-tab-pane>
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
            </a-tabs>
        </div>

        <!-- 新增/编辑窗口 -->
        <addTransferSheetModal
            ref="addSheetModal"
            :visible="addModal.visible"
            :isTransferOut="tabs.activeTabKey === 0"
            :isTransfer="true"
            @onCancel="onAddModalCancel"
        />
    </div>
</template>

<script>
import {
    getWarehouseTransferListAPI,
    editWarehouseTransferListAPI,
    hospitalGrantApplyConfirmAPI
} from '@/service/pageAjax';

import addTransferSheetModal from './module/addTransferSheetModal';

export default {
    /**
     * =================================
     *         页面字段中英对照说明
     * =================================
     * - 待调出         PendingOut
     * - 待调入         PendingIn
     * - 调出历史       HistoryOut
     * - 调入历史       HistoryIn
     * =================================
     */
    name: 'warehouseTransfer',
    components: {
        addTransferSheetModal
    },
    data() {
        return {
            // 机构类型
            systemType: '',
            // 顶部搜索栏数据
            searchBar: {
                isSearching: false,
                // 经手人
                handler_name: '',
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
            tabs: {
                // tab名称
                tabsName: ['待调出', '待调入', '调出历史', '调入历史'],
                // tab名称
                tabsValue: ['PendingOut', 'PendingIn', 'HistoryOut', 'HistoryIn'],
                // tab key
                tabsKey: [0, 1, 2, 3],
                // 当前tab名称
                activeTabName: '待调出',
                // 当前tab key
                activeTabKey: 0,
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
            gridData: {
                // 待调入表格的数据
                PendingInDatas: [],
                // 待调出表格的数据
                PendingOutDatas: [],
                // 调入历史表格的数据
                HistoryInDatas: [],
                // 调出历史表格的数据
                HistoryOutDatas: [],
                // 待调入是否加载中
                PendingInLoading: false,
                // 待调出是否加载中
                PendingOutLoading: false,
                // 调入历史是否加载中
                HistoryInLoading: false,
                // 调出历史是否加载中
                HistoryOutLoading: false,
                // 当前选中表格行数据
                currentSelectRow: {},
                // 当前选中表格行序号
                currentSelectRecords: [],
                // 作废按钮是否置灰
                DiscardDisabled: false
            },
            // 数据列设置
            commonTableColumn: [
                {
                    title: '编号',
                    dataIndex: 'av_sn',
                    key: 'av_sn',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '源仓库',
                    dataIndex: 'source_warehouse_name',
                    key: 'source_warehouse_name',
                    minWidth: '20',
                    align: 'center'
                },
                {
                    title: '目标仓库',
                    dataIndex: 'target_warehouse_name',
                    key: 'target_warehouse_name',
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
                    dataIndex: 'allot_quantity',
                    key: 'allot_quantity',
                    width: '80',
                    align: 'center'
                },
                {
                    title: '日期',
                    dataIndex: 'expect_date',
                    key: 'expect_date',
                    width: '100',
                    align: 'center'
                },
                {
                    title: '经手人',
                    dataIndex: 'poster_name',
                    key: 'poster_name',
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
         * 获取调拨单数据接口调用
         * @async api:getWarehouseTransferListAPIFnAsync
         * @param tabsKey 当前tabs数据，与this.tabs.tabsKey相同
         */
        async getWarehouseTransferListAPIFnAsync(tabsKey) {
            let params = {
                // 当前tab，参数值为【1，2，3，4】
                tab_id: tabsKey + 1,
                // 当前登录机构
                org_id: this.$cookie.get('userbelong'),
                // 分页大小
                pageSize: this.tabs.pageSize,
                // 分页页数
                pageNum: this.tabs.pageNum
            };

            if (this.searchBar.isSearching) {
                params.av_sn = this.searchBar.sn;
                params.start_date = this.searchBar.dateBegin;
                params.end_date = this.searchBar.dateEnd;
                params.poster_name = this.searchBar.handler_name;
            }

            await getWarehouseTransferListAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.tabs.total = res.total;
                    const data = res.rows.map((item) => {
                        // 将接口返回的状态枚举转为文字
                        const statusCode = item.status;
                        switch (statusCode) {
                            case 'DRAFT':
                                item.status_name = '草稿';
                                break;
                            case 'CONFIRM':
                                item.status_name = '确认';
                                break;
                            case 'OUTPUT':
                                item.status_name = '已出库';
                                break;
                            case 'COMPLETED':
                                item.status_name = '完结';
                                break;
                            case 'DISCARD':
                                item.status_name = '作废';
                                break;
                        }
                        return {
                            ...item
                        };
                    });
                    // 根据tabsKey为表格数据赋值
                    switch (tabsKey) {
                        // PendingOut table data
                        case 0:
                            this.gridData.PendingOutDatas = data;
                            this.gridData.PendingOutLoading = false;
                            break;
                        // PendingIn table data
                        case 1:
                            this.gridData.PendingInDatas = data;
                            this.gridData.PendingInLoading = false;
                            break;
                        // HistoryOut table data
                        case 2:
                            this.gridData.HistoryOutDatas = data;
                            this.gridData.HistoryOutLoading = false;
                            break;
                        // HistoryIn table data
                        case 3:
                            this.gridData.HistoryInDatas = data;
                            this.gridData.HistoryInLoading = false;
                            break;
                    }

                    if (this.searchBar.isSearching) {
                        this.searchBar.isSearching = false;
                    }
                }
            });
        },
        /**
         * 作废调拨单接口
         * @async
         */
        async editWarehouseTransferListAPIFn(isSingleDiscard) {
            let datas = [];
            if (!isSingleDiscard) {
                datas = this.gridData.currentSelectRecords;
                if (datas.length !== 0) {
                    datas.forEach((item) => {
                        item.status = 4;
                    });
                }
            } else {
                datas = [this.gridData.currentSelectRow];
                datas[0].status = 4;
            }
            let params = datas;

            await editWarehouseTransferListAPI(params).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.gridData.currentSelectRecords = [];
                    this.refreshTableFn();
                }
            });
        },
        /**
         * 医院领取调拨单接口
         * @async
         * @param row 当前领取行单据数据
         */
        async hospitalGrantApplyConfirmAPIFn(row) {
            let details = {
                detail_inner_sn: row.detail_inner_sn,
                get_quantity: row.allot_quantity,
                product_type: row.product_type,
                receiver_inner_sn: row.poster_inner_sn,
                receiver_name: row.poster_name
            };
            let params = {
                details: [details],
                gid: row.av_inner_sn,
                oid: row.org_id
            };

            await hospitalGrantApplyConfirmAPI(params).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.gridData.currentSelectRecords = [];
                    this.refreshTableFn();
                } else {
                    this.$warning({
                        title: '提示',
                        content: '该调拨单关联的出库单已经做过入库调单，无法直接领取请调单入库完成!',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
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
                    this.$Utils.globalDragCenterFn('warehouseTransfer-modal-add');
                    this.addModal.visible = true;
                    this.addModal.isAdd = true;
                    if (this.$refs.addSheetModal) {
                        this.$refs.addSheetModal.initPage(true);
                    }
                    break;
                case 'edit':
                    if (!this.gridData.currentSelectRow.product_name) {
                        this.$warning({
                            title: '操作提示',
                            content: '请选中一条数据进行修改',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        return;
                    }
                    this.$Utils.globalDragCenterFn('warehouseTransfer-modal-add');
                    this.addModal.visible = true;
                    this.addModal.isAdd = false;
                    this.addModal.sn = this.gridData.currentSelectRow.av_inner_sn;
                    if (this.$refs.addSheetModal) {
                        this.$refs.addSheetModal.setSearchSn(this.gridData.currentSelectRow.av_inner_sn);
                        this.$refs.addSheetModal.initPage(false);
                    }
                    break;
                case 'delete':
                    if (this.gridData.currentSelectRecords.length === 0) {
                        this.$warning({
                            title: '操作提示',
                            content: '请选中一条数据进行作废',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        return;
                    }

                    this.editWarehouseTransferListAPIFn(false);
                    break;
            }
        },
        /**
         * 搜索按钮对e应方法
         */
        searchBtnFn() {
            this.searchBar.isSearching = true;
            this.getWarehouseTransferListAPIFnAsync(this.tabs.activeTabKey);
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
         * 变更tab时触发的函数
         * @param key 变更后tab的key
         */
        changeTabFn(key) {
            this.tabs.pageNum = 1;
            this.tabs.total = 10;
            // 更新当前tab信息
            this.tabs.activeTabKey = key;
            this.tabs.activeTabName = this.tabs.tabsName[key];

            this.gridData.currentSelectRow = {};
            this.refreshTableFn(this.tabs.tabsValue[key]);
        },
        /**
         * 刷新表格的方法
         * @param tableName 刷新的表格对象('PendingIn', 'PendingOut', 'HistoryIn', 'HistoryOut', 'All')
         */
        refreshTableFn(tableName) {
            // 刷新按钮没有tableName，手动赋值为当前激活tab
            if (!tableName) {
                tableName = this.tabs.tabsValue[this.tabs.activeTabKey];
            }
            switch (tableName) {
                // 重新获取待调入数据
                case 'PendingIn':
                    this.gridData.PendingInLoading = true;
                    this.getWarehouseTransferListAPIFnAsync(this.tabs.activeTabKey);
                    break;
                // 重新获取待调出数据
                case 'PendingOut':
                    this.gridData.PendingOutLoading = true;
                    this.getWarehouseTransferListAPIFnAsync(this.tabs.activeTabKey);
                    break;
                // 重新获取调入历史数据
                case 'HistoryIn':
                    this.gridData.HistoryInLoading = true;
                    this.getWarehouseTransferListAPIFnAsync(this.tabs.activeTabKey);
                    break;
                // 重新获取待调出历史数据
                case 'HistoryOut':
                    this.gridData.HistoryOutLoading = true;
                    this.getWarehouseTransferListAPIFnAsync(this.tabs.activeTabKey);
                    break;
            }
        },
        /**
         * 单击表格列触发的方法
         * @param {row} 点击的行信息
         */
        tableCellClickFn({ row }) {
            // if (row.status === 'DRAFT' || row.status === 'CONFIRM') {
            //     this.gridData.DiscardDisabled = false
            // } else {
            //     this.gridData.DiscardDisabled = true
            // }
            this.gridData.currentSelectRow = row;
        },
        /**
         * 双击表格列触发的方法
         * @param {row} 点击的行信息
         */
        tableCellDblclickFn() {
            this.headerBtnFn('edit');
        },
        /**
         * 初始化页面的方法
         * @abstract 默认打开待调入tab
         */
        initPage() {
            this.changeTabFn(0);
        },
        /**
         * 新增调拨单窗口关闭方法
         */
        onAddModalCancel() {
            this.addModal.visible = false;
            this.refreshTableFn();
        },
        /**
         * 表格列多选绑定方法
         * @param {records} 选中的数据数组
         */
        selectChangeEvent({ records }) {
            if (records.length !== 0) {
                let flagCount = 0;
                records.forEach((v) => {
                    if (v.status !== 'DRAFT' && v.status !== 'CONFIRM') {
                        flagCount++;
                    }
                });
                if (flagCount === 0) {
                    this.gridData.DiscardDisabled = false;
                } else {
                    this.gridData.DiscardDisabled = true;
                }
            }
            this.gridData.currentSelectRecords = records;
        },
        /**
         * 表格列‘作废’按钮绑定方法
         * @param row 当前作废行信息
         * @abstract 标记当前按钮行为选中行，并调用delete方法
         */
        onDiscard(row) {
            this.gridData.currentSelectRow = row;
            this.editWarehouseTransferListAPIFn(true);
        },
        onGridDataPaginationChangeFn({ currentPage, pageSize }) {
            this.tabs.current = currentPage;
            this.tabs.pageNum = currentPage;
            this.tabs.pageSize = pageSize;

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
        this.initPage();
        this.systemType = parseInt(this.$cookie.get('userSystemType'));
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
