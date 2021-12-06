<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button
                                :disabled="activeTabsKey === systemTypes[1]"
                                @click="applyStopBtnFn('apply-stop-top')"
                                type="primary"
                                size="small"
                            >
                                申领终止
                            </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-show="!hightSearchObj.isArrow"
                            v-model="searchTables.voucher_sn"
                            placeholder="申领单编号"
                            size="small"
                            enterButton
                            class="search"
                            @search="searchBtnFn('general')"
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
                            <span class="label">申领单编号</span>
                            <a-input v-model="searchTables.voucher_sn" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">申领部门</span>
                            <a-tree-select
                                treeNodeFilterProp="title"
                                :showSearch="true"
                                :treeData="architectureAllTrees"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                treeDefaultExpandAll
                                allowClear
                                v-model="searchTables.department_inner_sn"
                                class="input"
                            >
                            </a-tree-select>
                        </li>
                        <li>
                            <span class="label">申领人</span>
                            <a-input v-model="searchTables.proposer_name" type="text" placeholder="" />
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
            :class="hightSearchObj.isArrow ? 'top136' : 'top56'"
            class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
        >
            <a-tabs v-model="activeTabsKey" @change="changeTabsFn" type="card">
                <a-tab-pane tab="待发放" :key="systemTypes[0]" forceRender>
                    <div class="box">
                        <div class="boxtop">
                            <!-- 表格头部 -->
                            <vxe-table
                                ref="xSuppliesClaimTable"
                                size="small"
                                :height="boxTopHeight"
                                border
                                resizable
                                auto-resize
                                highlight-current-row
                                @cell-click="cellClickSuppliesClaimFn"
                                @cell-dblclick="cellDblclickSuppliesClaimFn"
                                align="center"
                                :data="suppliesClaimDatas"
                                :loading="suppliesClaimPagination.loading"
                                show-overflow
                                show-header-overflow
                                showHeader
                                :start-index="(suppliesClaimPagination.current - 1) * suppliesClaimPagination.pageSize"
                            >
                                <vxe-table-column
                                    v-for="suppliesClaimItem in suppliesClaimColumns"
                                    :key="suppliesClaimItem.key"
                                    :width="suppliesClaimItem.width"
                                    :field="suppliesClaimItem.dataIndex"
                                    :title="suppliesClaimItem.title"
                                ></vxe-table-column>
                            </vxe-table>
                            <vxe-pager
                                :loading="suppliesClaimPagination.loading"
                                :current-page="suppliesClaimPagination.current"
                                :page-size="suppliesClaimPagination.pageSize"
                                :page-sizes="suppliesClaimPagination.pageSizes"
                                :total="suppliesClaimPagination.total"
                                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                                @page-change="changePaginationFn"
                            >
                            </vxe-pager>
                        </div>
                        <div class="resize"></div>
                        <div class="boxdown">
                            <!-- 下方表格区域 -->
                            <div style="margin-top: 8px" class="search-form-wrap bot-search-form-wrap">
                                <a-row class="hight-search-wrap bot-hight-search-wrap">
                                    <a-col :span="24" class="col-list">
                                        <ul class="form">
                                            <li style="width: 20%">
                                                <span class="label">仓库</span>
                                                <a-tree-select
                                                    treeNodeFilterProp="title"
                                                    :showSearch="true"
                                                    :treeData="wareHouseFilterData"
                                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                                    treeDefaultExpandAll
                                                    v-model="searchBotTables.warehouse_inner_sn"
                                                    @select="selectWarehouseSourceFn"
                                                    class="input"
                                                >
                                                </a-tree-select>
                                            </li>
                                            <li id="to-be-issued">
                                                <a-checkbox
                                                    :disabled="onlyUnGrantDisabled"
                                                    v-model="searchBotTables.onlyUnGrant"
                                                    @change="changeOnlyUnGrantFn"
                                                    >仅显示待发放的产品细目</a-checkbox
                                                >
                                            </li>
                                            <li>
                                                <a-button
                                                    @click="hospitalGrantSendGroupBtnFn('great-send')"
                                                    type="primary"
                                                >
                                                    发放
                                                </a-button>
                                                <a-button
                                                    @click="hospitalGrantSendGroupBtnFn('great-plan')"
                                                    type="primary"
                                                >
                                                    生成采购计划
                                                </a-button>
                                            </li>
                                        </ul>
                                    </a-col>
                                </a-row>
                                <vxe-table
                                    ref="xSuppliesClaimBotTable"
                                    size="small"
                                    :height="boxDownHeight - 70"
                                    border
                                    resizable
                                    auto-resize
                                    highlight-current-row
                                    align="center"
                                    :data="suppliesClaimBotDatas"
                                    :loading="suppliesClaimBotPagination.loading"
                                    show-overflow
                                    show-header-overflow
                                    show-header
                                >
                                    <vxe-table-column
                                        type="checkbox"
                                        width="80"
                                        :resizable="false"
                                        fixed="left"
                                    ></vxe-table-column>
                                    <vxe-table-column
                                        v-for="suppliesClaimBotItem in suppliesClaimBotColumns"
                                        :key="suppliesClaimBotItem.key"
                                        :width="suppliesClaimBotItem.width"
                                        :field="suppliesClaimBotItem.dataIndex"
                                        :title="suppliesClaimBotItem.title"
                                    ></vxe-table-column>
                                    <vxe-table-column
                                        field="action"
                                        title="操作"
                                        width="80"
                                        :resizable="false"
                                        fixed="right"
                                    >
                                        <template v-slot="{ row }">
                                            <ul
                                                v-if="row.status === 0 || row.status === 2"
                                                class="action-btn-list-wrap"
                                                style="position: relative"
                                            >
                                                <li>
                                                    <a-tooltip
                                                        class="tooltip"
                                                        placement="top"
                                                        title="申领终止"
                                                        :getPopupContainer="getPopupContainer"
                                                    >
                                                        <a-icon v-if="row.applyStopBtnLoading" type="loading" />
                                                        <i
                                                            v-else
                                                            @click.stop="applyStopBtnFn('apply-stop', row)"
                                                            class="yrt yrt-zhongzhi-gaoduan cancel"
                                                        ></i>
                                                    </a-tooltip>
                                                </li>
                                            </ul>
                                        </template>
                                    </vxe-table-column>
                                </vxe-table>
                            </div>
                            <!-- 下方表格区域 -->
                        </div>
                    </div>
                </a-tab-pane>
                <a-tab-pane tab="已发放" :key="systemTypes[1]" forceRender>
                    <div class="box">
                        <div class="boxtop">
                            <!-- 表格头部 -->
                            <vxe-table
                                ref="xSuppliesClaimHistoryTable"
                                size="small"
                                :height="boxTopHeight"
                                border
                                resizable
                                auto-resize
                                highlight-current-row
                                @cell-click="cellClickSuppliesClaimHistoryFn"
                                @cell-dblclick="cellDblclickSuppliesClaimHistoryFn"
                                align="center"
                                :data="suppliesClaimHistoryDatas"
                                :loading="suppliesClaimHistoryPagination.loading"
                                show-overflow
                                show-header-overflow
                                showHeader
                                :start-index="
                                    (suppliesClaimHistoryPagination.current - 1) *
                                    suppliesClaimHistoryPagination.pageSize
                                "
                            >
                                <vxe-table-column
                                    v-for="suppliesClaimItem in suppliesClaimHistoryColumns"
                                    :key="suppliesClaimItem.key"
                                    :width="suppliesClaimItem.width"
                                    :field="suppliesClaimItem.dataIndex"
                                    :title="suppliesClaimItem.title"
                                ></vxe-table-column>
                            </vxe-table>
                            <vxe-pager
                                :loading="suppliesClaimHistoryPagination.loading"
                                :current-page="suppliesClaimHistoryPagination.current"
                                :page-size="suppliesClaimHistoryPagination.pageSize"
                                :page-sizes="suppliesClaimHistoryPagination.pageSizes"
                                :total="suppliesClaimHistoryPagination.total"
                                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                                @page-change="changePaginationHistoryFn"
                            >
                            </vxe-pager>
                        </div>
                        <div class="resize"></div>
                        <div class="boxdown">
                            <!--底部表格区域-->
                            <div class="search-form-wrap bot-search-form-wrap" style="margin-top: 8px">
                                <allotTable :boxDownHeight="boxDownHeight" :isDispatch="true" ref="allotTable" />
                            </div>
                        </div>
                    </div>
                </a-tab-pane>
                <a-tooltip slot="tabBarExtraContent" placement="left">
                    <template slot="title">
                        <span>刷新</span>
                    </template>
                    <a-button
                        @click="refreshPageFn"
                        type="primary"
                        shape="circle"
                        icon="reload"
                        size="small"
                        class="refresh-btn"
                    />
                    <!--刷新按钮区域-->
                </a-tooltip>
            </a-tabs>
        </div>
        <!-- tab切换区域结束 -->

        <!-- 点击发放 / 生成采购计划 后的弹框区域开始 -->
        <!-- 注意：此处的单据编号不显示是根据发放按钮和生成采购计划按钮已经是新增了，所以直接注释掉单据编号，需要查看请前往申领菜单 【周哥 20190920】 -->
        <a-modal
            v-model="greatSendModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-great-send global-drag"
            :width="1300"
            :zIndex="3"
            @cancel="greatSendModalGroupBtnFn('cancel')"
        >
            <div v-globalDrag="{ el: 'modal-great-send' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ greatSendModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button key="print" @click="greatSendModalGroupBtnFn('print')"> 打印 </a-button>
                        <a-button key="exportExcel" @click="greatSendModalGroupBtnFn('exportExcel')">
                            导出Excel
                        </a-button>
                    </li>
                    <li>
                        <div v-if="!isEditDisabled">
                            <a-button key="close" @click="greatSendModalGroupBtnFn('cancel')"> 取消 </a-button>
                            <a-button
                                v-if="grantSingleBtnName === 'great-send'"
                                key="great-send"
                                type="primary"
                                :loading="greatSendModal.loadingSend"
                                @click="greatSendModalGroupBtnFn('great-send')"
                            >
                                发放
                            </a-button>
                            <a-button
                                v-else
                                key="great-send"
                                type="primary"
                                :loading="greatSendModal.loadingSend"
                                @click="greatSendModalGroupBtnFn('great-save-plan')"
                            >
                                保存
                            </a-button>
                        </div>
                        <div v-else>
                            <a-button key="close" @click="greatSendModalGroupBtnFn('cancel')"> 关闭 </a-button>
                        </div>
                    </li>
                </ul>
            </template>
            <div ref="printData">
                <div class="modal-form-input for-single">
                    <h6 class="title">
                        <span style="margin-right: 10px; font-size: 24px; color: #444">{{ orgName }}</span>
                        <span style="font-size: 24px; color: #444">{{ computedGrantSingleTitle }}</span>
                    </h6>
                    <ul class="top-list grant-claim-info">
                        <li style="width: 20%">
                            <div class="item">
                                <span>部门: </span>
                                <span>{{ currentTrObj.department_name }}</span>
                            </div>
                        </li>
                        <li v-if="needWarehouse" style="width: 20%">
                            <div class="item">
                                <span>仓库: </span>
                                <span>{{ selectedWarehouse.label }}</span>
                            </div>
                        </li>
                    </ul>
                    <div
                        ref="applyTable"
                        class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                        style="position: static; height: 291px"
                    >
                        <!-- 发放单 -->
                        <vxe-grid
                            v-show="grantSingleBtnName === 'great-send'"
                            ref="xGrantSendTable"
                            size="small"
                            :data="greatSendDatas"
                            :loading="greatSendPagination.loading"
                            height="266"
                            border
                            resizable
                            auto-resize
                            show-overflow
                            show-header-overflow
                            highlight-current-row
                            align="center"
                            show-footer
                        >
                            <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                                <template v-slot="{ seq }">
                                    {{ seq }}
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="product_name"
                                title="产品"
                                :cell-render="{ name: 'product_name' }"
                                width="120"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="120"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="product_vendor_name"
                                title="厂商/经销商"
                                width="120"
                                :cell-render="{ name: 'product_vendor_name' }"
                            ></vxe-table-column>
                            <vxe-table-column field="batch_number" title="批号" width="150">
                                <template v-slot="{ row, seq }">
                                    <a-dropdown :trigger="['click']">
                                        <!-- louxiaochen: a-input 中 -->
                                        <!-- @change 调用方法处理输入 -->
                                        <!-- @click 调用api获取下拉 -->
                                        <a-input
                                            class="input"
                                            style="width: 120px"
                                            v-model="row.batch_number"
                                            @change="onBatchNumberInputFn(true, row, e)"
                                            @click="odoBatchListAPIFn(row, seq)"
                                        />
                                        <a-menu
                                            v-if="row.batch_number_list && row.batch_number_list.length"
                                            slot="overlay"
                                        >
                                            <!-- louxiaochen: a-input 中 -->
                                            <!-- @click 调用方法处理选中 -->
                                            <a-menu-item
                                                v-for="(batchNumberItem, batchNumberIndex) in row.batch_number_list"
                                                :key="batchNumberIndex"
                                                @click="onBatchNumberSelectFn(false, row, batchNumberItem.batch_number)"
                                            >
                                                <a href="javascript:;">{{ batchNumberItem.batch_number }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="ready_quantity_new"
                                title="待批准发放数量"
                                width="120"
                                :cell-render="{ name: 'ready_quantity_new' }"
                            >
                            </vxe-table-column>
                            <vxe-table-column
                                field="available_inventory_new"
                                title="可用库存"
                                width="120"
                                :cell-render="{ name: 'available_inventory_new' }"
                            >
                            </vxe-table-column>
                            <vxe-table-column field="approval_volumes" title="批准发放">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :min="0"
                                        v-model="row.approval_volumes"
                                        @click="clickUnitListFn(row)"
                                        @blur="clickUnitListFn(row)"
                                        :formatter="
                                            (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')
                                        "
                                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_name" title="包装单位">
                                <template v-slot="{ row }">
                                    <a-cascader
                                        :allowClear="false"
                                        :options="row.unitList"
                                        v-model="row.unit_inner_sn_new"
                                        changeOnSelect
                                        :displayRender="displayUnitRender"
                                        @click.stop="clickUnitListFn(row, 'unit')"
                                        @change="changeUnitListFn"
                                        placeholder=""
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="packge_multiple_num"
                                title="合计发放"
                                width="130"
                                :cell-render="{ name: 'packge_multiple_num' }"
                            >
                            </vxe-table-column>
                            <vxe-table-column field="action" title="操作" width="60" :resizable="false" fixed="right">
                                <template v-slot="{ row }">
                                    <ul class="action-btn-list-wrap" style="position: relative">
                                        <li>
                                            <a-tooltip
                                                class="tooltip"
                                                placement="top"
                                                title="删除"
                                                :getPopupContainer="getPopupContainer"
                                            >
                                                <i
                                                    @click.stop="applyDetailGroupBtnFn('remove', row, 'great-send')"
                                                    class="yrt yrt-shanchu-copy cancel"
                                                ></i>
                                            </a-tooltip>
                                        </li>
                                    </ul>
                                </template>
                            </vxe-table-column>
                            <template v-slot:empty>
                                <div class="dblclick-wrap">
                                    <span>暂无数据</span>
                                </div>
                            </template>
                        </vxe-grid>

                        <!-- 采购计划单 -->
                        <vxe-grid
                            v-show="grantSingleBtnName === 'great-plan'"
                            ref="xGrantSingleTable"
                            size="small"
                            :data="greatSendDatas"
                            :loading="greatSendPagination.loading"
                            height="266"
                            border
                            resizable
                            auto-resize
                            show-overflow
                            show-header-overflow
                            highlight-current-row
                            align="center"
                            show-footer
                        >
                            <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                                <template v-slot="{ seq }">
                                    {{ seq }}
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="product_name"
                                title="产品"
                                :cell-render="{ name: 'product_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="16%"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="product_vendor_name"
                                title="厂商/经销商"
                                width="15%"
                                :cell-render="{ name: 'product_vendor_name' }"
                            ></vxe-table-column>
                            <vxe-table-column field="batch_number" title="批号" width="150">
                                <template v-slot="{ row, seq }">
                                    <!-- 批号原代码 -->
                                    <!-- <a-select
                                        label-in-value
                                        :default-value="{ key: row.batch_number }"
                                        style="width: 120px"
                                        @focus="batchNumberFocusFn(row, seq)"
                                        @change="batchNumberChangeFn"
                                    >
                                        <a-select-option 
                                            v-for="(batchNumberItem, batchNumberIndex) in row.batch_number_list"
                                            :key="batchNumberIndex"
                                            :value="batchNumberItem.batch_number">
                                            {{ batchNumberItem.batch_number }}
                                        </a-select-option>
                                    </a-select> -->
                                    <!-- 批号原代码 -->
                                    <a-dropdown :trigger="['click']">
                                        <!-- louxiaochen: a-input 中 -->
                                        <!-- @change 调用方法处理输入 -->
                                        <!-- @click 调用api获取下拉 -->
                                        <a-input
                                            class="input"
                                            style="width: 120px"
                                            v-model="row.batch_number"
                                            @change="onBatchNumberInputFn(true, row, e)"
                                            @click="planBatchListAPIFn(row, seq)"
                                        />
                                        <a-menu
                                            v-if="row.batch_number_list && row.batch_number_list.length"
                                            slot="overlay"
                                        >
                                            <!-- louxiaochen: a-input 中 -->
                                            <!-- @click 调用方法处理选中 -->
                                            <a-menu-item
                                                v-for="(batchNumberItem, batchNumberIndex) in row.batch_number_list"
                                                :key="batchNumberIndex"
                                                @click="onBatchNumberSelectFn(true, row, batchNumberItem)"
                                            >
                                                <a href="javascript:;">{{ batchNumberItem }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="approval_volumes" title="数量" width="12%">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :min="0"
                                        v-model="row.approval_volumes"
                                        @click="clickUnitPlanListFn(row)"
                                        @blur="clickUnitPlanListFn(row)"
                                        :formatter="
                                            (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')
                                        "
                                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_name" title="包装单位" width="12%">
                                <template v-slot="{ row }">
                                    <a-cascader
                                        :allowClear="false"
                                        :options="row.unitList"
                                        v-model="row.unit_inner_sn_new"
                                        changeOnSelect
                                        :displayRender="displayUnitRender"
                                        @click.stop="clickUnitPlanListFn(row, 'unit')"
                                        @change="changeUnitListFn"
                                        placeholder=""
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="packge_multiple_num"
                                title="采购合计"
                                width="8%"
                                :cell-render="{ name: 'packge_multiple_num' }"
                            >
                            </vxe-table-column>
                            <vxe-table-column field="action" title="操作" width="80" :resizable="false" fixed="right">
                                <template v-slot="{ row }">
                                    <ul class="action-btn-list-wrap" style="position: relative">
                                        <li>
                                            <a-tooltip
                                                class="tooltip"
                                                placement="top"
                                                title="删除"
                                                :getPopupContainer="getPopupContainer"
                                            >
                                                <i
                                                    @click.stop="applyDetailGroupBtnFn('remove', row, 'great-plan')"
                                                    class="yrt yrt-shanchu-copy cancel"
                                                ></i>
                                            </a-tooltip>
                                        </li>
                                    </ul>
                                </template>
                            </vxe-table-column>
                            <template v-slot:empty>
                                <div class="dblclick-wrap">
                                    <span>暂无数据</span>
                                </div>
                            </template>
                        </vxe-grid>
                    </div>
                    <ul>
                        <li class="item">
                            <span class="label" style="width: auto">摘要</span>
                            <a-input
                                :disabled="isEditDisabled"
                                v-model="editInfoFormObj.description"
                                placeholder=""
                                class="input"
                            />
                        </li>
                        <!-- 发放单 -->
                        <li v-if="grantSingleBtnName === 'great-send'" class="item">
                            <div class="item-children" style="min-width: 120px; width: auto">
                                <span class="label" style="width: auto">发放人</span>
                                <span>{{ employeeInfo.Name || '-' }}</span>
                            </div>
                            <div class="item-children" style="min-width: 120px; width: auto">
                                <span class="label" style="width: auto">发放时间</span>
                                <span>-</span>
                            </div>
                            <!--                            <div class="item-children" style="min-width:120px;width:auto">-->
                            <!--                                <span-->
                            <!--                                    class="label"-->
                            <!--                                    style="width: auto;">申领人</span>-->
                            <!--                                <span>{{ employeeInfo.Name || '-' }}</span>-->
                            <!--                            </div>-->
                        </li>
                        <!-- 采购计划单 -->
                        <li v-else class="item">
                            <div class="item-children" style="min-width: 120px; width: auto">
                                <span class="label" style="width: auto">制单人</span>
                                <span>{{ employeeInfo.Name || '-' }}</span>
                            </div>
                            <div class="item-children" style="min-width: 120px; width: auto">
                                <span class="label" style="width: auto">生成时间</span>
                                <span>-</span>
                            </div>
                            <div class="item-children" style="min-width: 120px; width: auto">
                                <span class="label" style="width: auto">审核人</span>
                                <span>-</span>
                            </div>
                            <div class="item-children" style="min-width: 120px; width: auto">
                                <span class="label" style="width: auto">审核时间</span>
                                <span>-</span>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </a-modal>
        <!-- 点击生成采购计划后的弹框区域结束 -->

        <!-- 双击已发放顶部列表区域开始 -->
        <a-modal
            v-model="addInfoModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-add global-drag"
            :width="1100"
            :zIndex="3"
        >
            <div v-globalDrag="{ el: 'modal-add' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addInfoModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button key="print" @click="handlePrint('xApplySendConfirmModalTable')"> 打印 </a-button>
                        <a-button @click="exportExcel('xApplySendConfirmModalTable')"> 导出Excel </a-button>
                    </li>
                    <li>
                        <div>
                            <a-button key="close" @click="addInfoModal.alert = false"> 关闭 </a-button>
                        </div>
                    </li>
                </ul>
            </template>
            <div class="modal-form-input for-single">
                <h6 class="title">
                    <span style="margin-right: 10px; font-size: 24px; color: #444">{{ orgName }}</span>
                    <span style="font-size: 24px; color: #444">申领单</span>
                </h6>
                <ul class="top-list">
                    <li :style="btnName !== 'add' ? 'justify-content:space-between' : 'justify-content:start'">
                        <div v-if="btnName !== 'add'" class="item">
                            <span class="label">单据编号</span>
                            <a-input disabled v-model="editInfoFormObj.voucher_sn" placeholder="" class="input" />
                        </div>
                        <div class="item">
                            <span class="label"><em class="em red">*</em>部门</span>
                            <a-tree-select
                                disabled
                                :showSearch="false"
                                :treeData="architectureAllTrees"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                treeDefaultExpandAll
                                v-model="editInfoFormObj.department_name"
                                :allowClear="false"
                                class="input"
                                style="min-width: 150px"
                            >
                            </a-tree-select>
                        </div>
                        <div class="item">
                            <span class="label">申领人</span>
                            <span class="input">{{ editInfoFormObj.proposer_name || '-' }}</span>
                        </div>
                        <div class="item">
                            <span class="label">期望于</span>
                            <a-date-picker
                                disabled
                                allowClear
                                v-model="expectDate"
                                :format="datePickerFormat"
                                class="input ant-calendar-picker-input"
                            />
                            <span class="before-issuing">之前发放</span>
                        </div>
                    </li>
                </ul>
                <div
                    class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                    style="position: static; height: 336px"
                >
                    <vxe-table
                        ref="xApplySendConfirmModalTable"
                        size="small"
                        height="310"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        align="center"
                        :data="addInfoDatas"
                        :loading="addInfoPagination.loading"
                        show-overflow
                        show-header-overflow
                        showHeader
                    >
                        <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                            <template v-slot="{ seq }">
                                {{ seq }}
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            v-for="applySendModalItem in addInfoDraftColumns"
                            :key="applySendModalItem.key"
                            :width="applySendModalItem.width"
                            :field="applySendModalItem.dataIndex"
                            :title="applySendModalItem.title"
                        ></vxe-table-column>
                    </vxe-table>
                </div>
                <ul>
                    <li class="item" style="justify-content: space-between">
                        <div class="item-children">
                            <span class="label" style="width: auto">摘要</span>
                            <a-input disabled v-model="editInfoFormObj.description" placeholder="" class="input" />
                        </div>
                        <div class="item-children" style="width: 28%">
                            <span class="label" style="width: auto">生成时间</span>
                            <span class="input">{{ editInfoFormObj.create_time || '-' }}</span>
                        </div>
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 双击已发放顶部列表区域结束 -->

        <!-- 双击已发放底部表格列表 - 发放明细查看区域开始 -->
        <a-modal
            v-model="applySendModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-apply-send global-drag"
            :width="1100"
            :zIndex="3"
        >
            <div v-globalDrag="{ el: 'modal-apply-send' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ applySendModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button @click="handlePrint('xApplySendModalTable', true)"> 打印 </a-button>
                        <a-button @click="exportExcel('xApplySendModalTable')"> 导出Excel </a-button>
                    </li>
                    <li>
                        <a-button key="close" @click="applySendModal.alert = false"> 关闭 </a-button>
                    </li>
                </ul>
            </template>
            <div class="modal-form-input for-single">
                <h6 class="title">
                    <span style="margin-right: 10px; font-size: 24px; color: #444">{{ orgName }}</span>
                    <span style="font-size: 24px; color: #444">发放单</span>
                </h6>
                <ul class="top-list">
                    <li>
                        <div v-if="btnName !== 'add'" class="item">
                            <span class="label">单据编号</span>
                            <a-input disabled v-model="applySendInfoFormObj.gv_sn" placeholder="" class="input" />
                        </div>
                        <div class="item">
                            <span class="label"><em class="em red">*</em>部门</span>
                            <a-tree-select
                                disabled
                                :showSearch="false"
                                :treeData="architectureAllTrees"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                treeDefaultExpandAll
                                v-model="applySendInfoFormObj.department_name"
                                :allowClear="false"
                                class="input"
                                style="min-width: 150px"
                            >
                            </a-tree-select>
                        </div>
                    </li>
                </ul>
                <div
                    class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                    style="position: static; height: 291px"
                >
                    <vxe-table
                        ref="xApplySendModalTable"
                        size="small"
                        height="266"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        align="center"
                        :data="applySendModalDatas"
                        :loading="applySendModalPagination.loading"
                        show-overflow
                        show-header-overflow
                        showHeader
                    >
                        <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                            <template v-slot="{ seq }">
                                {{ seq }}
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            v-for="applySendModalItem in applySendModalColumns"
                            :key="applySendModalItem.key"
                            :width="applySendModalItem.width"
                            :field="applySendModalItem.dataIndex"
                            :title="applySendModalItem.title"
                        ></vxe-table-column>
                    </vxe-table>
                </div>
                <ul>
                    <li class="item">
                        <span class="label" style="width: auto">摘要</span>
                        <a-input disabled v-model="applySendInfoFormObj.description" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <div class="item-children" style="min-width: 120px; width: auto">
                            <span class="label" style="width: auto">发放人</span>
                            <span>{{ applySendInfoFormObj.approver_name || '-' }}</span>
                        </div>
                        <div class="item-children" style="min-width: 120px; width: auto">
                            <span class="label" style="width: auto">发放时间</span>
                            <span>{{ applySendInfoFormObj.grant_time || '-' }}</span>
                        </div>
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 双击已发放底部表格列表 - 发放明细查看区域结束 -->
        <!-- 新增/编辑调拨单窗口 -->
        <addTransferSheetModal
            ref="addAllotSheetModal"
            :visible="addAllotModal.visible"
            @onCancel="addAllotModal.visible = false"
            @onAddFinished="refreshPageFn"
        />
    </div>
</template>

<script>
import moment from 'moment';
import { mapState } from 'vuex';
import {
    hospitalApplyDetailStopAPI,
    platformUnitInfo,
    dealerUnitInfo,
    platformUnitMinAPI,
    dealerUnitMinAPI,
    orgDeptTopTreeAPI,
    platformUnitTree,
    dealerUnitTreeAPI,
    hospitalApplyStopAPI,
    hospitalGrantSendAPI,
    hospitalGrantCreatplanAPI,
    hospitalGrantListAPI,
    hospitalGrantApplyListAPI,
    hospitalGrantApplyHistoryListAPI,
    hospitalGrantDetailListAPI,
    hospitalApplyInfoAPI,
    odoBatchListAPI,
    planBatchListAPI,
    hospitalGrantGaListAPI
} from '@/service/pageAjax';

import allotTable from './Module/SuppliesClaimAllotTable';
import addTransferSheetModal from '../../commonPage/warehouseManage/warehouseTransfer/module/addTransferSheetModal';

export default {
    name: 'SuppliesClaim',
    components: {
        allotTable,
        addTransferSheetModal
    },
    data() {
        return {
            addAllotModal: {
                visible: false
            },
            batchNumberEmpty: '空批号',
            batchNumberCurItem: {},
            boxTopHeight: 300,
            boxDownHeight: 300,
            employeeInfo: JSON.parse(this.$cookie.get('EmployeeInfo')),
            systemTypes: ['waitApply', 'haveClaimed'], // 待发放 已发放
            activeTabsKey: 'waitApply',
            isActiveTabsKey: false,
            suppliesClaimColumns: [
                {
                    title: '申领单编号',
                    dataIndex: 'voucher_sn',
                    key: 'voucher_sn',
                    width: '28%',
                    align: 'center'
                },
                {
                    title: '申领部门',
                    dataIndex: 'department_name',
                    key: 'department_name',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '仓库',
                    dataIndex: 'warehouse_inner_sn_name',
                    key: 'warehouse_inner_sn_name',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '申领人',
                    dataIndex: 'proposer_name',
                    key: 'proposer_name',
                    width: '10%',
                    align: 'center'
                },
                {
                    title: '提交时间',
                    dataIndex: 'create_time',
                    key: 'create_time',
                    width: '17%',
                    align: 'center'
                },
                {
                    title: '期望发放日期(之前)',
                    dataIndex: 'expect_date',
                    key: 'expect_date',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    width: '15%',
                    align: 'center'
                }
            ],
            suppliesClaimDatas: [],
            suppliesClaimPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            suppliesClaimHistoryColumns: [
                {
                    title: '申领单编号',
                    dataIndex: 'voucher_sn',
                    key: 'voucher_sn',
                    align: 'center'
                },
                {
                    title: '申领部门',
                    dataIndex: 'department_name',
                    key: 'department_name',
                    minWidth: 200,
                    align: 'center'
                },
                {
                    title: '仓库',
                    dataIndex: 'warehouse_inner_sn_name',
                    key: 'warehouse_inner_sn_name',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '申领人',
                    dataIndex: 'proposer_name',
                    key: 'proposer_name',
                    width: 150,
                    align: 'center'
                },
                {
                    title: '生成时间',
                    dataIndex: 'create_time',
                    key: 'create_time',
                    minWidth: 200,
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    width: 150,
                    align: 'center'
                }
            ],
            suppliesClaimHistoryDatas: [],
            currentTrHistoryObj: {},
            suppliesClaimHistoryPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            suppliesClaimBotSelectedRowKeys: [],
            suppliesClaimBotColumns: [
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    align: 'center',
                    width: 200
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    align: 'center',
                    width: 200
                },
                {
                    title: '厂商/经销商',
                    dataIndex: 'product_vendor_name',
                    key: 'product_vendor_name',
                    align: 'center',
                    width: 200
                },
                {
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    align: 'center',
                    width: 100
                },
                {
                    title: '申领数量',
                    dataIndex: 'plan_quantity_new',
                    key: 'plan_quantity_new',
                    align: 'center',
                    width: 150
                },
                {
                    title: '已批准发放数量',
                    dataIndex: 'approved_quantity_new',
                    key: 'approved_quantity_new',
                    align: 'center',
                    width: 150
                },
                {
                    title: '待批准发放数量',
                    dataIndex: 'ready_quantity_new',
                    key: 'ready_quantity_new',
                    align: 'center',
                    width: 150
                },
                // {
                //     visible: false,
                //     title: '已领取数量',
                //     dataIndex: 'confirm_quantity_new',
                //     key: 'confirm_quantity_new',
                //     align: 'center',
                //     width: 150
                // },
                {
                    title: '库存',
                    dataIndex: 'inventory_new',
                    key: 'inventory_new',
                    align: 'center',
                    width: 150
                },
                {
                    title: '可用库存',
                    dataIndex: 'available_inventory_new',
                    key: 'available_inventory_new',
                    align: 'center',
                    width: 150
                },
                {
                    title: '锁定库存',
                    dataIndex: 'locked_inventory_new',
                    key: 'locked_inventory_new',
                    align: 'center',
                    width: 150
                },
                {
                    title: '状态',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    width: 150,
                    align: 'center'
                }
            ],
            suppliesClaimBotDatas: [],
            applyStopBtnLoading: false,
            currentTrBotObj: {},
            suppliesClaimBotPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            suppliesClaimHistoryBotColumns: [],
            suppliesClaimHistoryBotColumnsData: [
                {
                    title: '编号',
                    dataIndex: 'av_sn',
                    key: 'av_sn',
                    align: 'center'
                },
                {
                    title: '发出仓库',
                    dataIndex: 'source_warehouse_name',
                    key: 'source_warehouse_name',
                    align: 'center'
                },
                {
                    title: '目标仓库',
                    dataIndex: 'target_warehouse_name',
                    key: 'target_warehouse_name',
                    align: 'center'
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    align: 'center'
                },
                {
                    title: '厂商',
                    dataIndex: 'vd_name',
                    key: 'vd_name',
                    align: 'center'
                },
                {
                    title: '单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: 'quantity',
                    align: 'center'
                },
                {
                    title: '日期',
                    dataIndex: 'expect_date',
                    key: 'expect_date',
                    align: 'center'
                },
                {
                    title: '经手人',
                    dataIndex: 'poster_name',
                    key: 'poster_name',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status_name',
                    key: 'status_name',
                    align: 'center'
                }
            ],
            suppliesClaimBotAllDatas: [],
            suppliesClaimHistoryBotDatas: [],
            currentTrHistoryBotObj: {},
            suppliesClaimHistoryBotPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            hospitalGrantStatus: ['申领中', '已完全发放', '已计划采购', '已终止申领'], // 耗材发放 - 待发放底部列表的状态
            auditStatus: ['草稿', '申领中', '全部发放', '申领终止'], // 医院领用单状态(status): 0-草稿 1-申领 2-全部发放(自然终止) 3-申领终止(人为终止)
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            currentTrgreatSendObj: {},
            searchTables: {
                hospital_inner_sn: '',
                status: '',
                voucher_sn: '',
                proposer_name: '',
                department_inner_sn: ''
            }, // 表格的搜索并向后端请求字段
            searchBotTables: {
                warehouse_name: '',
                warehouse_inner_sn: '',
                onlyUnGrant: true
            },
            // 审核状态的下拉颜色高亮显示、箭头的朝向改变
            menuItemData: {
                class: true,
                arrow: false
            },
            // 修改弹框的配置
            editInfoModal: {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            },
            // 生成发放单的配置
            greatSendModal: {
                title: `${this.$route.meta.title} - 发放单`,
                alert: false,
                loadingSend: false
            },
            wareHouseFilterData: [], // 库存来源
            selectedWarehouse: {}, // 已选中的仓库
            needWarehouse: true, // 功能是否依赖仓库（区分发放/采购计划）
            applySendStatus: ['可发放', '已出库', '已领取'],
            grantSingleBtnName: '',
            greatSendDatas: [], // 实际后端数据
            record: {},
            greatSendPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            editInfoFormObj: {
                // 修改信息的模态框值 - 后端返回回来的
                department_inner_sn: '',
                department_name: '',
                description: '',
                gv_sn: ''
            },
            editInfoFormOldObj: {}, // 修改信息的模态框值 - 后端返回回来的旧值
            editInfoFormNewObj: {}, // 修改信息的模态框值 - 前端根据页面修改的值放进去 【没有修改的不要向后端传递】
            rangePickerVal: moment[''], // 申请时间的默认赋值
            editInfoFormDepartmentInnerSn: [], // 所属部门
            architectureAllTrees: [], // 所属部门的信息列表
            btnName: '', // 当前点击的是哪个顶部按钮
            btnModalName: '',
            datePickerFormat: 'YYYY-MM-DD', // 期望时间的格式
            partB: {
                // 新增产品窗口给子组件传递值
                id: this.$cookie.get('userbelong'),
                modalType: 1 // 1 - 合同内的产品
            },
            unitList: [], // 包装单位
            details: [], // 生成发放单 - 生成采购计划的 details后端所需字段数据
            expectDate: '', // 期望时间
            isEditDisabledFlag: true,
            searchTablesStatus: [
                // 顶部高级搜索的状态
                { value: 0, label: '草稿', disabled: false },
                { value: 1, label: '申领', disabled: false },
                { value: 2, label: '部分发放', disabled: false },
                { value: 3, label: '全部发放', disabled: true },
                { value: 4, label: '申领终止', disabled: true }
            ],
            recordUnitInnerSn: '',
            applyTableItemStatus: ['申领中', '已完全发放', '已计划采购', '已终止申领'], // 医院申领单产品明细状态(status): `0-申领中 1-已完全发放 2-已计划采购 3-已终止申领`
            scrollYNumber02: 350,
            isApplyDetailStopFlag: false,
            applySendModalColumns: [
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    width: 200,
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    width: 150,
                    align: 'center'
                },
                {
                    title: '厂商/经销商',
                    dataIndex: 'vendor_name',
                    key: 'vendor_name',
                    width: 150,
                    align: 'center'
                },
                {
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    align: 'center'
                },
                {
                    title: '申领',
                    dataIndex: 'grant_quantity',
                    key: 'grant_quantity',
                    align: 'center'
                },
                // {
                //     title: '已发',
                //     dataIndex: 'real_quantity',
                //     key: 'real_quantity',
                //     align: 'center'
                // },
                {
                    title: '包装单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
                    align: 'center'
                },
                {
                    title: '售价',
                    dataIndex: 'salesPrice',
                    key: 'salesPrice',
                    align: 'center'
                }
            ],
            applySendModalDatas: [],
            applySendModalPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            currentApplySendModalTrObj: {},
            applySendModal: {
                title: `${this.$route.meta.title} - 发放单`,
                alert: false,
                loadingApply: false
            },
            applySendInfoFormObj: {
                approver_inner_sn: '',
                approver_name: '',
                department_inner_sn: '',
                department_name: '',
                description: '',
                grant_time: '',
                gv_inner_sn: '',
                gv_sn: '',
                hospital_inner_sn: '',
                hospital_name: '',
                is_delete: '',
                status: ''
            },
            addInfoModal: {
                title: `${this.$route.meta.title} - 新增`,
                alert: false,
                loadingSaveDraft: false,
                loadingApply: false
            },
            addInfoDraftColumns: [
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification_name',
                    key: 'specification_name',
                    minWidth: 150,
                    align: 'center'
                },
                {
                    title: '厂商/经销商',
                    dataIndex: 'vendor_name',
                    key: 'vendor_name',
                    width: 150,
                    align: 'center'
                },
                {
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '申领',
                    dataIndex: 'plan_quantity',
                    key: 'plan_quantity',
                    width: 80,
                    align: 'center',
                    scopedSlots: { customRender: 'plan_quantity' }
                },
                // {
                //     title: '已发',
                //     dataIndex: 'achieve_quantity',
                //     key: 'achieve_quantity',
                //     width: 80,
                //     align: 'center',
                //     scopedSlots: {customRender: 'achieve_quantity'}
                // },
                {
                    title: '包装单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
                    width: 80,
                    align: 'center',
                    scopedSlots: { customRender: 'unit_name' }
                },
                {
                    title: '状态',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    width: 100,
                    align: 'center'
                }
            ],
            addInfoDatas: [], // 实际后端数据
            addInfoPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            }
        };
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
                        that.boxDownHeight = box[i].clientHeight - moveLen + 16;
                        for (let j = 0; j < boxtop.length; j++) {
                            boxtop[j].style.height = moveLen + 'px';
                            mid[j].style.height = box[i].clientHeight - moveLen + 16 + 'px';
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
        // 【生成采购计划】
        async hospitalGrantCreatplanAPIFn() {
            this.greatSendModal.loadingSend = true;
            let params = {
                hospital_inner_sn: this.currentTrObj.hospital_inner_sn,
                av_inner_sn: this.currentTrObj.voucher_inner_sn,
                department_inner_sn: this.currentTrObj.department_inner_sn,
                department_name: this.currentTrObj.department_name,
                description: this.editInfoFormObj.description,
                warehouse_inner_sn: this.searchBotTables.warehouse_inner_sn,
                warehouse_name: this.searchBotTables.warehouse_name
                // status: 1 // 正式状态
            };

            params.details = this.greatSendDatas.map((item, index) => {
                return {
                    detail_inner_sn: item.detail_inner_sn || '',
                    product_vendor_inner_sn: item.product_vendor_inner_sn,
                    product_vendor_name: item.product_vendor_name,
                    product_inner_sn: item.product_inner_sn,
                    product_name: item.product_name,
                    specification_inner_sn: item.specification_inner_sn,
                    specification: item.specification,
                    unit_inner_sn: item.unit_inner_sn_new[item.unit_inner_sn_new.length - 1],
                    unit: item.unit,
                    grant_quantity: item.approval_volumes,
                    need_quantity: item['ready_quantity_compare'], // 还需发放量
                    min_unit_grant_quantity: parseFloat(item.packge_multiple_num), // 最小单位转换量
                    sort_number: index,
                    product_type: item.product_type,
                    batch_number: item.batch_number === this.batchNumberEmpty ? '' : item.batch_number
                };
            });

            await hospitalGrantCreatplanAPI(params)
                .then((res) => {
                    this.greatSendModal.loadingSend = false;
                    if (parseFloat(res.code) === 0) {
                        this.hospitalGrantApplyListAPIFn(false, this.currentTrObj);
                        this.hospitalGrantApplyHistoryListAPIFn();
                        this.greatSendModal.alert = false;
                        this.greatSendDatas = [];
                        this.suppliesClaimBotSelectedRowKeys = [];
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.greatSendModal.loadingSend = false;
                });
        },
        // 【经销商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async dealerUnitTreeAPIFn(record) {
            await dealerUnitTreeAPI(
                record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let circle = (item, parentName) => {
                        // 递归遍历
                        item.forEach((item) => {
                            item.label = `${item.name}(${item.measure}×${parentName})`;
                            item.value = item.unit_inner_sn;
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item.name);
                            } else {
                                item.children = null;
                            }
                        });
                    };
                    res.tree.forEach((item) => {
                        item.label = item.name;
                        item.value = item.unit_inner_sn;
                        if (item.children && item.children.length > 0) {
                            circle(item.children, item.name);
                        } else {
                            item.children = null;
                        }
                    });
                    if (res.tree.length) {
                        record.unitList = res.tree;
                        const curUnit = record.unitList[0];
                        record.unit_inner_sn_new = [curUnit.value];
                        record.unit = curUnit.label;
                        record.unit_name = curUnit.label;
                        this.setPackgeMultipleFn(record);
                    }
                }
            });
        },
        // 【厂商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async platformUnitTreeFn(record) {
            await platformUnitTree(
                record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { tree } = res;
                    let circle = (item, parentName) => {
                        // 递归遍历
                        item.forEach((item) => {
                            item.label = `${item.name}(${item.measure}×${parentName})`;
                            item.value = item.unit_inner_sn;
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item.name);
                            } else {
                                item.children = null;
                            }
                        });
                    };
                    tree.forEach((item) => {
                        item.label = item.name;
                        item.value = item.unit_inner_sn;
                        if (item.children && item.children.length > 0) {
                            circle(item.children, item.name);
                        } else {
                            item.children = null;
                        }
                    });
                    if (tree.length) {
                        record.unitList = tree;
                        const curUnit = record.unitList[0];
                        record.unit_inner_sn_new = [curUnit.value];
                        record.unit = curUnit.label;
                        record.unit_name = curUnit.label;
                        this.setPackgeMultipleFn(record);
                    }
                }
            });
        },
        // 【查询所有可用的机构一级仓库】
        async warehouseUsableTopListAPIFn() {
            this.wareHouseFilterData = this.warehouseList.map((item) => ({
                label: item.name,
                value: item.warehouse_model_inner_sn
            }));
            if (this.wareHouseFilterData.length > 0) {
                let curWareHouseData = this.wareHouseFilterData[0];
                this.searchBotTables.warehouse_name = curWareHouseData.label;
                this.searchBotTables.warehouse_inner_sn = curWareHouseData.value;
                this.selectedWarehouse = curWareHouseData;
            } else {
                this.searchBotTables.warehouse_name = '';
                this.searchBotTables.warehouse_inner_sn = '';
                this.selectedWarehouse = {};
            }
            this.hospitalGrantApplyListAPIFn();
            // await warehouseUsableTopListAPI()
            //     .then(res => {
            //         if (parseFloat(res.code) === 0) {
            //             const {list} = res
            //             this.wareHouseFilterData = list.map(item => {
            //                 return {
            //                     label: item.name,
            //                     value: item.warehouse_model_inner_sn
            //                 }
            //             })
            //             if (this.wareHouseFilterData.length > 0) {
            //                 let curWareHouseData = this.wareHouseFilterData[0]
            //                 this.searchBotTables.warehouse_name = curWareHouseData.label
            //                 this.searchBotTables.warehouse_inner_sn = curWareHouseData.value
            //                 this.selectedWarehouse = curWareHouseData
            //             } else {
            //                 this.searchBotTables.warehouse_name = ''
            //                 this.searchBotTables.warehouse_inner_sn = ''
            //                 this.selectedWarehouse = {}
            //             }
            //             this.hospitalGrantApplyListAPIFn()
            //         } else {
            //             this.$message.error(res.msg)
            //         }
            //     })
        },
        // 根据机构ID查询经销商【所有部门】
        async orgDeptTopTreeAPIFn() {
            await orgDeptTopTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    console.log(list);
                    this.treeDatasIndexAryFn(list, 'deptTopTree');
                    console.log(this.architectureAllTrees);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【获取】材料申领信息
        async hospitalApplyInfoAPIFn(aid) {
            await hospitalApplyInfoAPI(aid)
                .then((res) => {
                    this.addInfoPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { info } = res,
                            details = info.details ? info.details : [];
                        this.addInfoDatas = details.map((item, index) => {
                            let status = parseFloat(item.status);
                            return {
                                ...item,
                                index,
                                key: Math.random(),
                                activeClass: '',
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                specification_name: item.specification || item.specification_name,
                                vendor_name: item.vendor_name || item.dealer_name,
                                unitList: item.unit_inner_sn
                                    ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                    : [],
                                unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                                isLoadingUnit: false, // 是否加载过整棵树
                                status_text: this.applyTableItemStatus[status]
                            };
                        });
                        this.$refs.xApplySendConfirmModalTable.setCurrentRow(this.addInfoDatas[0]);
                        this.$refs['xApplySendConfirmModalTable'].refreshColumn();
                        this.editInfoFormObj = { ...info };
                        this.expectDate = this.editInfoFormObj.expect_date
                            ? moment(this.editInfoFormObj.expect_date)
                            : '';
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addInfoPagination.loading = false;
                });
        },
        // 【获取】获取发放单列表 【耗材申领的待申领明细和耗材发放的已发放明细】
        async hospitalGrantIssuedDetailListAPIFn(record) {
            this.suppliesClaimHistoryBotPagination.loading = true;
            if (record.warehouse_inner_sn === null) {
                let t = this.suppliesClaimHistoryBotColumnsData;
                t.splice(1, 1);
                this.suppliesClaimHistoryBotColumns = t;
            } else {
                this.suppliesClaimHistoryBotColumns = this.suppliesClaimHistoryBotColumnsData;
            }
            await hospitalGrantGaListAPI(record.hospital_inner_sn, record.voucher_inner_sn)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.suppliesClaimHistoryBotDatas = list[0].details.map((item) => {
                            const statusCode = item.status;
                            switch (statusCode) {
                                case 0:
                                    item.status_name = '草稿';
                                    break;
                                case 1:
                                    item.status_name = '确认';
                                    break;
                                case 2:
                                    item.status_name = '已出库';
                                    break;
                                case 3:
                                    item.status_name = '完结';
                                    break;
                                case 4:
                                    item.status_name = '作废';
                                    break;
                            }
                            return {
                                av_sn: record.voucher_inner_sn,
                                source_warehouse_name: item.warehouse_name,
                                target_warehouse_name: '',
                                product_name: item.product_name,
                                specification: item.specification,
                                vd_name: item.vendor_name,
                                unit_name: item.unit_name,
                                quantity: item.get_quantity,
                                expect_date: '',
                                poster_name: list[0].poster_name,
                                status_name: item.status_name,
                                ...item
                            };
                        });
                        if (this.suppliesClaimHistoryBotDatas.length) {
                            this.currentTrHistoryBotObj = this.suppliesClaimHistoryBotDatas[0];
                        } else {
                            this.currentTrHistoryBotObj = {};
                        }
                        this.$refs.xSuppliesClaimHistoryBotTable.setCurrentRow(this.currentTrHistoryBotObj);
                        this.suppliesClaimHistoryBotPagination = {
                            ...this.suppliesClaimHistoryBotPagination,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                        this.suppliesClaimHistoryBotPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.suppliesClaimHistoryBotPagination.loading = false;
                });
        },
        // 【获取】发放单列表
        async hospitalGrantListAPIFn(record, warehouseInnerSn) {
            this.suppliesClaimBotPagination.loading = true;
            await hospitalGrantListAPI(
                record.hospital_inner_sn,
                record.voucher_inner_sn,
                warehouseInnerSn || this.searchBotTables.warehouse_inner_sn
            )
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.suppliesClaimBotAllDatas = list.map((item) => {
                            item.key = item.detail_inner_sn;
                            item.status = parseFloat(item.status);
                            item.status_text = this.hospitalGrantStatus[parseFloat(item.status)];
                            item.activeClass = '';
                            item.applyStopBtnLoading = false;
                            if (typeof item.inventory === 'undefined') {
                                item.inventory = 0;
                            }
                            // 待批准发放数量 = 计划数量 * 申领包装单位与最小包装单位的倍数 - 已批准发放数量
                            let readyQuantity = item.plan_quantity * item.min_unit_quantity - item.approved_quantity;
                            if (readyQuantity < 0) readyQuantity = 0;
                            // 可用库存 = 库存量 - 锁定最小包装单位库存量
                            let lockedInventoryInit = this.lockedInventoryNewFn(item),
                                availableInventory = item.inventory - lockedInventoryInit;
                            availableInventory =
                                availableInventory % 1 === 0 ? availableInventory : availableInventory.toFixed(3);
                            if (parseFloat(item.unit_inner_sn) === parseFloat(item.min_unit_inner_sn)) {
                                // 申领量 包装单位 === 最小包装单位 不要显示括号的最小包装单位
                                // 申领数量
                                item.plan_quantity_new = `${item.plan_quantity}${item.unit}`;
                                // 已批准发放量
                                item.approved_quantity_new = `${item.approved_quantity}${item.min_unit}`;
                                // 待批准发放数量
                                item.ready_quantity_new = `${readyQuantity}${item.unit}`;
                                item.ready_quantity_compare = readyQuantity;
                                // 库存
                                item.inventory_new = `${item.inventory}${item.min_unit}`;
                                // 锁定库存
                                item.locked_inventory_new = `${lockedInventoryInit}${item.min_unit}`;
                                // 可用库存
                                item.available_inventory_new = `${availableInventory}${item.min_unit}`;
                                item.available_inventory = availableInventory;
                            } else {
                                // 申领数量
                                item.plan_quantity_new = this.transToUnit(item.plan_quantity, item);
                                // 已批准发放量
                                item.approved_quantity_new = this.transToUnit(
                                    item.approved_quantity / item.min_unit_quantity,
                                    item
                                );
                                // 待发放数量
                                let readyQy = item.plan_quantity - item.approved_quantity / item.min_unit_quantity;
                                if (readyQy < 0) readyQy = 0;
                                item.ready_quantity_new = this.transToUnit(readyQy, item);
                                item.ready_quantity_compare = readyQy * item.min_unit_quantity;
                                // 库存
                                item.inventory_new = this.transToUserUnit(item.inventory, item);
                                // 锁定库存
                                item.locked_inventory_new = this.transToUserUnit(lockedInventoryInit, item);
                                // 可用库存
                                item.available_inventory_new = this.transToUserUnit(availableInventory, item);
                                item.available_inventory = availableInventory;
                            }
                            item.confirm_quantity_new = `${item.real_quantity}${item.min_unit}`; // 确认发放量
                            // item.unitList = item.unit_inner_sn ? [{value: parseFloat(item.unit_inner_sn), label: item.unit}] : []
                            item.unitList = [];
                            item.unit_inner_sn_new = [];
                            return item;
                        });
                        if (this.suppliesClaimBotAllDatas.length) {
                            this.filterSearchBotTablesFn();
                        }
                        this.suppliesClaimBotPagination = {
                            ...this.suppliesClaimBotPagination,
                            loading: false
                        };
                        // 刷新列配置
                        this.$refs['xSuppliesClaimBotTable'].refreshColumn();
                        return Promise.resolve();
                    } else {
                        this.$message.error(res.msg);
                        this.suppliesClaimBotPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.suppliesClaimBotPagination.loading = false;
                });
        },
        // 【获取】待发放申领单列表(分页,提交时间倒序)
        async hospitalGrantApplyListAPIFn(btnNameFlag, curItem) {
            // 判断参数有没有， 没有直接不要传给后端
            let params = {
                hospital_inner_sn: this.$cookie.get('userbelong'),
                pageNum: this.suppliesClaimPagination.current,
                pageSize: this.suppliesClaimPagination.pageSize
            };
            // 判断是否是高级搜索
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                if (typeof this.searchTables.status !== 'string') {
                    params.status = this.searchTables.status[0];
                }

                if (this.searchTables.proposer_name) {
                    params.proposer_name = this.searchTables.proposer_name;
                }

                if (this.searchTables.department_inner_sn) {
                    params.department_inner_sn = this.searchTables.department_inner_sn;
                }
            }

            if (this.searchTables.voucher_sn) {
                params.voucher_sn = this.searchTables.voucher_sn;
            }

            await hospitalGrantApplyListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        let isSetCurrentTrObjFlag = false;
                        this.suppliesClaimDatas = rows.map((item) => {
                            item.key = item.hospital_inner_sn + '' + item.proposer_inner_sn + item.voucher_inner_sn;
                            item.audit_status_text = this.auditStatus[parseFloat(item.status)];
                            item.activeClass = '';
                            if (curItem && item.key === curItem.key) {
                                this.currentTrObj = item;
                                isSetCurrentTrObjFlag = true;
                            }
                            return item;
                        });
                        this.$refs.xSuppliesClaimTable.setCurrentRow(this.currentTrObj);
                        if (btnNameFlag === 'add') {
                            this.currentTrObj = this.suppliesClaimDatas[this.suppliesClaimDatas.length - 1];
                            this.$refs.xSuppliesClaimTable.setCurrentRow(this.currentTrObj);
                        } else {
                            if (!curItem) {
                                this.currentTrObj = this.suppliesClaimDatas.length ? this.suppliesClaimDatas[0] : {};
                            } else if (curItem && !isSetCurrentTrObjFlag) {
                                this.currentTrObj = this.suppliesClaimDatas.length ? this.suppliesClaimDatas[0] : {};
                            }
                            this.$refs.xSuppliesClaimTable.setCurrentRow(this.currentTrObj);
                        }

                        if (this.suppliesClaimDatas.length) {
                            this.searchBotTables.warehouse_inner_sn && this.hospitalGrantListAPIFn(this.currentTrObj);
                        } else {
                            this.suppliesClaimDatas = [];
                            this.currentTrObj = {};
                            this.suppliesClaimBotDatas = [];
                            this.currentTrBotObj = {};
                        }

                        this.suppliesClaimPagination = {
                            ...this.suppliesClaimPagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                        this.suppliesClaimPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.suppliesClaimPagination.loading = false;
                });
        },
        // 【获取】获取已发放申领单列表(分页,提交时间倒序)
        async hospitalGrantApplyHistoryListAPIFn(btnNameFlag, curItem) {
            // 判断参数有没有， 没有直接不要传给后端
            let params = {
                hospital_inner_sn: this.$cookie.get('userbelong'),
                pageNum: this.suppliesClaimHistoryPagination.current,
                pageSize: this.suppliesClaimHistoryPagination.pageSize
            };
            // 判断是否是高级搜索
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                if (typeof this.searchTables.status !== 'string') {
                    params.status = this.searchTables.status[0];
                }

                if (this.searchTables.proposer_name) {
                    params.proposer_name = this.searchTables.proposer_name;
                }

                if (this.searchTables.department_inner_sn) {
                    params.department_inner_sn = this.searchTables.department_inner_sn;
                }
            }

            if (this.searchTables.voucher_sn) {
                params.voucher_sn = this.searchTables.voucher_sn;
            }

            await hospitalGrantApplyHistoryListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.suppliesClaimHistoryDatas = rows.map((item) => {
                            item.key = item.hospital_inner_sn + '' + item.proposer_inner_sn + item.voucher_inner_sn;
                            item.audit_status_text = this.auditStatus[parseFloat(item.status)];
                            item.activeClass = '';
                            if (curItem && item.key === curItem.key) {
                                this.currentTrHistoryObj = item;
                                this.$refs.xSuppliesClaimHistoryTable.setCurrentRow(this.currentTrHistoryObj);
                            }
                            return item;
                        });
                        if (!curItem) {
                            this.currentTrHistoryObj = this.suppliesClaimHistoryDatas.length
                                ? this.suppliesClaimHistoryDatas[0]
                                : {};
                            this.$refs.xSuppliesClaimHistoryTable.setCurrentRow(this.currentTrHistoryObj);
                        }

                        if (this.suppliesClaimHistoryDatas.length) {
                            this.hospitalGrantIssuedDetailListAPIFn(this.currentTrHistoryObj);
                        } else {
                            this.suppliesClaimHistoryBotDatas = [];
                            this.currentTrHistoryBotObj = {};
                        }

                        this.suppliesClaimHistoryPagination = {
                            ...this.suppliesClaimHistoryPagination,
                            total,
                            loading: false
                        };

                        this.$refs.allotTable.initPage(this.currentTrHistoryObj);
                    } else {
                        this.$message.error(res.msg);
                        this.suppliesClaimHistoryPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.suppliesClaimHistoryPagination.loading = false;
                });
        },
        // 【终止】申领材料-明细
        async hospitalApplyDetailStopAPIFn(record) {
            record.applyStopBtnLoading = true;
            let params = {
                hospital_inner_sn: this.currentTrObj.hospital_inner_sn,
                order_id: this.currentTrObj.voucher_inner_sn,
                detail_id: record.detail_inner_sn,
                product_type: record.product_type
            };
            await hospitalApplyDetailStopAPI(params)
                .then((res) => {
                    record.applyStopBtnLoading = false;
                    if (parseFloat(res.code) === 0) {
                        this.searchBotTables.warehouse_inner_sn && this.hospitalGrantListAPIFn(this.currentTrObj);
                        this.isApplyDetailStopFlag = true;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    record.applyStopBtnLoading = false;
                });
        },
        // 【终止】材料申领单
        async hospitalApplyStopAPIFn() {
            await hospitalApplyStopAPI(this.currentTrObj.hospital_inner_sn, this.currentTrObj.voucher_inner_sn).then(
                (res) => {
                    if (parseFloat(res.code) === 0) {
                        this.hospitalGrantApplyListAPIFn(false, this.currentTrObj);
                        this.hospitalGrantApplyHistoryListAPIFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                }
            );
        },
        // 【发放】
        async hospitalGrantSendAPIFn() {
            this.greatSendModal.loadingSend = true;
            let params = {
                hospital_inner_sn: this.currentTrObj.hospital_inner_sn,
                av_inner_sn: this.currentTrObj.voucher_inner_sn, // 申领单ID
                department_inner_sn: this.currentTrObj.department_inner_sn,
                department_name: this.currentTrObj.department_name,
                gv_sn: '',
                description: this.editInfoFormObj.description,
                warehouse_inner_sn: this.searchBotTables.warehouse_inner_sn,
                warehouse_name: this.selectedWarehouse.label
            };
            params.details = this.greatSendDatas.map((item, index) => {
                return {
                    product_vendor_inner_sn: item.product_vendor_inner_sn,
                    product_vendor_name: item.product_vendor_name,
                    product_inner_sn: item.product_inner_sn,
                    product_name: item.product_name,
                    specification_inner_sn: item.specification_inner_sn,
                    specification: item.specification,
                    unit_inner_sn: item.unit_inner_sn_new[item.unit_inner_sn_new.length - 1],
                    unit: item.unit,
                    grant_quantity: item.approval_volumes,
                    sort_number: index,
                    product_type: item.product_type,
                    need_quantity: item['ready_quantity_compare'], // 还需发放量
                    min_unit_grant_quantity: parseFloat(item.packge_multiple_num), // 最小单位转换量
                    detail_inner_sn: item.detail_inner_sn, // 申领单明细ID
                    batch_number: item.batch_number === this.batchNumberEmpty ? '' : item.batch_number
                };
            });

            await hospitalGrantSendAPI(params)
                .then((res) => {
                    this.greatSendModal.loadingSend = false;
                    if (parseFloat(res.code) === 0) {
                        // this.hospitalGrantApplyListAPIFn(false, this.currentTrObj)
                        // this.hospitalGrantApplyHistoryListAPIFn()
                        this.suppliesClaimBotDatas = res.list;
                        this.greatSendModal.alert = false;
                        this.greatSendDatas = [];
                        this.suppliesClaimBotSelectedRowKeys = [];
                        this.hospitalGrantListAPIFn(this.currentTrObj).then(() => {
                            // 筛选当前是否还有待发放的产品
                            const tempArr = this.suppliesClaimBotAllDatas.filter(
                                (item) => parseFloat(item.status) === 0 || parseFloat(item.status) === 2
                            );
                            // 待发放的产品都发放完成后  重新获取待发放申领单列表
                            if (!tempArr.length) {
                                this.hospitalGrantApplyListAPIFn();
                            }
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.greatSendModal.loadingSend = false;
                    this.clickWaitListBotResetFn(this.currentTrObj);
                });
        },
        // 【获取】根据发放单ID获取发放单明细列表
        async hospitalGrantDetailListAPIFn(record) {
            this.applySendModalPagination.loading = true;

            await hospitalGrantDetailListAPI(record.hospital_inner_sn, record.gv_inner_sn)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.applySendModalDatas = list.map((item, index) => {
                            return {
                                ...item,
                                index,
                                activeClass: '',
                                key: Math.random()
                            };
                        });
                        if (this.applySendModalDatas.length) {
                            this.currentApplySendModalTrObj = this.applySendModalDatas[0];
                        } else {
                            this.currentApplySendModalTrObj = {};
                        }
                        this.$refs.xApplySendModalTable.setCurrentRow(this.currentApplySendModalTrObj);
                        this.applySendModalPagination = {
                            ...this.applySendModalPagination,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                        this.applySendModalPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.applySendModalPagination.loading = false;
                });
        },
        // 【经销商 产品单位】
        async dealerUnitInfoFn(record) {
            await dealerUnitInfo(
                record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    this.setUnitListFn(info, record);
                }
            });
        },
        // 【厂商 产品单位】
        async platformUnitInfoFn(record) {
            await platformUnitInfo(
                record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    this.setUnitListFn(info, record);
                }
            });
        },
        // 获取产品包装单位的最小倍数 【厂商】
        async platformUnitMinAPIFn(record, recordUnitSn) {
            await platformUnitMinAPI(
                record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                recordUnitSn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.greatSendDatas.forEach((item) => {
                        if (item.key === this.record.key) {
                            item.packge_multiple = parseFloat(res.info);
                        }
                    });
                    this.setPackgeMultipleFn(record);
                }
            });
        },
        // 获取产品包装单位的最小倍数 【经销商】
        async dealerUnitMinAPIFn(record, recordUnitSn) {
            await dealerUnitMinAPI(
                record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                recordUnitSn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.greatSendDatas.forEach((item) => {
                        if (item.key === this.record.key) {
                            item.packge_multiple = parseFloat(res.info);
                        }
                    });
                    // 原代码，但修改包装单位不应该添加数据
                    // this.greatSendDatas.splice(record.index, 1, {
                    //     ...record,
                    //     packge_multiple: parseFloat(res.info)
                    // })
                    this.setPackgeMultipleFn(record);
                }
            });
        },
        // 发放页 获取【仓库内产品批号列表】
        async odoBatchListAPIFn(record, seq) {
            const params = {
                org_id: record.org_id || this.$cookie.get('userbelong'),
                warehouse_inner_sn: this.selectedWarehouse.value,
                product_vendor_inner_sn: record.product_vendor_inner_sn,
                product_inner_sn: record.product_inner_sn,
                specification_inner_sn: record.specification_inner_sn,
                product_type: record.product_type,
                batch_number: record.batch_number || '',
                billsType: 'DH'
            };

            await odoBatchListAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list, inventoryBannce } = res;
                    this.batchNumberCurItem = {
                        ...record,
                        inventoryBannce,
                        batch_number_loading: !record.batch_number_loading,
                        batch_number_list:
                            list.map((item) => {
                                return {
                                    ...item,
                                    inventoryBannce,
                                    batch_number: item.batch_number ? item.batch_number : this.batchNumberEmpty
                                };
                            }) || [],
                        seq: seq - 1 < 0 ? 0 : seq - 1
                    };
                    this.batchNumberChangeFn();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 生成采购计划-获取【仓库内产品批号列表】
        async planBatchListAPIFn(record, seq) {
            const params = {
                org_id: record.org_id || this.$cookie.get('userbelong'),
                product_inner_sn: record.product_inner_sn,
                product_type: record.product_type,
                product_vendor_inner_sn: record.product_vendor_inner_sn,
                specification_inner_sn: record.specification_inner_sn
            };

            await planBatchListAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.batchNumberCurItem = {
                        ...record,
                        batch_number_loading: !record.batch_number_loading,
                        batch_number_list: list.length === 0 ? [] : list.map((x) => (x === '' ? '空批号' : x)),
                        seq: seq - 1 < 0 ? 0 : seq - 1
                    };
                    this.batchNumberChangeFn();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据产品包装单位的info信息设置包装单位信息
        setUnitListFn(info, record) {
            const parentFlag = info.parent,
                childrenFlag = info.children,
                childrenLen = childrenFlag.length;
            record.isLoadingUnit = true; // 是否加载过整棵树
            record.unitListCur = [];
            if (Object.keys(info).length) {
                if (parentFlag && childrenFlag && childrenLen <= 0) {
                    // parent存在 和 children不存在
                    this.treeGetUnitDatasFn(info, record);
                    this.treeInsertUnitDatasFn(record);
                } else if (!parentFlag && childrenFlag && childrenLen) {
                    // parent不存在 和 children存在
                    info.children && info.children.length && this.treeGetUnitChildrenDatasFn(info, record);
                    record.unitList = [...record.unitListCur];
                    delete record.unitListCur;
                } else if (parentFlag && childrenFlag && childrenLen) {
                    // parent 和 children 都存在
                    this.treeGetUnitDatasFn(info, record);
                    // delete info.parent
                    info.children = [];
                    info.children.push(info.parent);
                    this.treeGetUnitChildrenLinkDatasFn(info, record);
                    record.unitList = record.unitListCur;
                    // this.treeInsertUnitDatasFn(record)
                } else if (!parentFlag && childrenFlag && childrenLen <= 0) {
                    // parent 和 children 都不存在
                    record.unitList = [
                        {
                            label: info.name,
                            value: info.unit_inner_sn
                        }
                    ];
                }
            }
        },
        // 递归查询包装单位数据
        treeGetUnitDatasFn(treeObj, record) {
            record.unitListCur.push({
                value: treeObj.unit_inner_sn,
                label: treeObj.name,
                ...treeObj,
                children: []
            });
            if (treeObj.parent) {
                this.treeGetUnitDatasFn(treeObj.parent, record);
            }
        },
        treeGetUnitChildrenDatasFn(treeObj, record) {
            treeObj.value = treeObj.unit_inner_sn;
            treeObj.label = treeObj.name;
            let treeInfoChildrenFn = (oldDatas) => {
                oldDatas.children.forEach((item) => {
                    item.value = item.unit_inner_sn;
                    item.label = item.name;
                    if (item.children && item.children.length) {
                        treeInfoChildrenFn(item);
                    } else {
                        delete item.children;
                    }
                });
            };
            treeInfoChildrenFn(treeObj);
            record.unitListCur = [treeObj];
        },
        treeGetUnitChildrenLinkDatasFn(treeObj, record) {
            treeObj.children.forEach((item) => {
                item.value = item.unit_inner_sn;
                item.label = item.name;
                if (item.children && item.children.length) {
                    this.treeGetUnitChildrenDatasFn(item, record);
                } else {
                    delete item.children;
                    const childrenList = treeObj.children[0];
                    delete treeObj.children;
                    record.unitListCur.unshift(childrenList);
                }
            });
        },
        // 递归反向插入包装单位数据
        treeInsertUnitDatasFn(record) {
            const len = record.unitListCur.length;
            if (len <= 0) {
                record.unitList = [];
            } else if (len === 1) {
                delete record.unitListCur[0].children;
                record.unitList = record.unitListCur;
            } else {
                for (let i = 0; i <= len; i++) {
                    const cur = record.unitListCur[i],
                        prev = record.unitListCur[i + 1];
                    if (i === 0) {
                        delete cur.children;
                    }
                    prev.children.push(cur);
                    if (!prev.parent) {
                        record.unitList = [prev];
                        delete record.unitListCur;
                        break;
                    }
                }
            }
        },
        // 表格重新刷新数据的初始化函数 02 【初始化分页data】
        initTablesPaginationFn() {
            if (this.activeTabsKey === this.systemTypes[0]) {
                this.currentTrObj = {};
                this.suppliesClaimPagination = {
                    ...this.suppliesClaimPagination,
                    loading: true,
                    current: 1
                };
            } else {
                this.currentTrHistoryObj = {};
                this.suppliesClaimHistoryPagination = {
                    ...this.suppliesClaimHistoryPagination,
                    loading: true,
                    current: 1
                };
            }
        },
        // 页码改变的回调函数
        changePaginationFn({ currentPage, pageSize }) {
            this.suppliesClaimPagination.current = currentPage;
            this.suppliesClaimPagination.pageSize = pageSize;
            this.suppliesClaimPagination.loading = true;
            this.hospitalGrantApplyListAPIFn();
        },
        changePaginationHistoryFn({ currentPage, pageSize }) {
            this.suppliesClaimHistoryPagination.current = currentPage;
            this.suppliesClaimHistoryPagination.pageSize = pageSize;
            this.suppliesClaimHistoryPagination.loading = true;
            this.hospitalGrantApplyHistoryListAPIFn();
        },
        // 点击待发放的列表显示下方的表格重置
        clickWaitListBotResetFn(record) {
            this.currentTrObj = record;
            this.suppliesClaimBotSelectedRowKeys = [];
            this.suppliesClaimBotDatas = [];
            this.currentTrBotObj = {};
            if (this.searchBotTables.warehouse_inner_sn.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '仓库不能为空。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
            } else {
                this.hospitalGrantListAPIFn(this.currentTrObj);
            }
        },
        cellClickSuppliesClaimFn({ row }) {
            this.clickWaitListBotResetFn(row);
        },
        cellDblclickSuppliesClaimFn({ row }) {
            // this.clickWaitListBotResetFn(row)
            this.currentTrHistoryObj = row;
            this.showApplySendTopModalFn(row);
            this.hospitalGrantIssuedDetailListAPIFn(row);
        },
        customRowBotFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentTrBotObj = record;
                    },
                    dblclick: () => {
                        this.currentTrBotObj = record;
                    }
                }
            };
        },
        cellClickSuppliesClaimHistoryFn({ row }) {
            this.currentTrHistoryObj = row;
            this.$refs.allotTable.initPage(row);
            // this.hospitalGrantIssuedDetailListAPIFn(row)
        },
        cellDblclickSuppliesClaimHistoryFn({ row }) {
            this.currentTrHistoryObj = row;
            // 展示申领单详情弹窗
            this.showApplySendTopModalFn(row);
        },
        showApplySendTopModalFn(record) {
            this.addInfoPagination.loading = true;
            this.addInfoModal.title = `${this.$route.meta.title} - 查看`;
            // 获取申领单信息
            this.hospitalApplyInfoAPIFn(record.voucher_inner_sn);
            this.addInfoModal.alert = true;
            this.$Utils.globalDragCenterFn('modal-add');
        },
        cellClickSuppliesHistoryBotClaimFn({ row }) {
            this.currentTrHistoryBotObj = row;
        },
        cellDblclickSuppliesHistoryBotClaimFn({ row }) {
            this.currentTrHistoryBotObj = row;
            this.showApplySendModalFn(row);
        },
        showApplySendModalFn(record) {
            this.applySendInfoFormObj = record;
            this.hospitalGrantDetailListAPIFn(record);
            this.applySendModal.alert = true;
            this.$Utils.globalDragCenterFn('modal-apply-send');
        },
        // 获取期望时间
        getexpectDate(date, dateString) {
            this.editInfoFormObj.expect_date = dateString;
        },
        // 判断申领单的表格有数据没有 并且校验值
        checkApplySingleFn(btnName) {
            const len = this.greatSendDatas.length;
            let flag = true;
            if (len <= 0) {
                if (btnName !== 'cancel') {
                    this.$warning({
                        title: '操作提示',
                        content: '请先关闭弹窗勾选产品，再进行相关操作。',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                }
                flag = false;
                return;
            }
            let isRepeatObj = {};
            for (let i = 0; i < len; i++) {
                const item = this.greatSendDatas[i];
                if (btnName === 'great-send') {
                    // 发放
                    if (!item.approval_volumes) {
                        this.$warning({
                            title: '操作提示',
                            content: `请填写【NO.${i + 1} - ${item.product_name}】批准发放量`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    const approvalVolumes = this.$Utils.trim(item.approval_volumes + '');
                    if (flag && !approvalVolumes) {
                        this.$warning({
                            title: '操作提示',
                            content: `请填写【NO.${i + 1} - ${item.product_name}】批准发放量`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }

                    // louxiaochen：根据要求去除前端判断，由后端同意判断
                    // if (flag && parseFloat(item.packge_multiple_num) > parseFloat(item.available_inventory_new)) {
                    // if (flag && parseFloat(item.packge_multiple_num) > parseFloat(item.available_inventory)) {
                    //     this.$warning({
                    //         title: '操作提示',
                    //         content: `【NO.${i + 1} - ${item.product_name}】批准发放量【${item.packge_multiple_num}】不能大于可用库存量【${item.available_inventory_new}】`,
                    //         okText: '知道了',
                    //         centered: true,
                    //         maskClosable: false
                    //     })
                    //     flag = false
                    //     break
                    // }

                    if (flag && item.unit_inner_sn_new.length <= 0) {
                        this.$warning({
                            title: '操作提示',
                            content: `请选择【NO.${i + 1} - ${item.product_name}】包装单位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                } else if (btnName === 'great-save-plan') {
                    // 生成采购计划
                    if (item.approval_volumes <= 0) {
                        this.$warning({
                            title: '操作提示',
                            content: `请填写【NO.${i + 1} - ${item.product_name}】数量`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (item.unit_inner_sn.length <= 0) {
                        this.$warning({
                            title: '操作提示',
                            content: `请选择【NO.${i + 1} - ${item.product_name}】包装单位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    // 产品只能出现唯一
                    const key = `${item.product_type}==${item.product_inner_sn}==${item.specification_inner_sn}==${
                        item.product_vendor_inner_sn || item.vendor_inner_sn || item.dealer_inner_sn
                    }`;
                    if (!isRepeatObj[key]) {
                        isRepeatObj[key] = `NO.${i + 1}`;
                    } else {
                        this.$warning({
                            title: '操作提示',
                            content: `${isRepeatObj[key]}和NO.${i + 1}产品相同，不能重复添加。`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        flag = false;
                        break;
                    }
                }
            }
            isRepeatObj = null;
            return flag;
        },
        // 申领单表格中的操作按钮
        applyDetailGroupBtnFn(btnName, record, tabName) {
            switch (btnName) {
                case 'remove':
                    if (tabName === 'great-send') {
                        const xGrantSendTable = this.$refs.xGrantSendTable;
                        xGrantSendTable.remove(record);
                        this.greatSendDatas = xGrantSendTable.getTableData().tableData;
                        xGrantSendTable.setCurrentRow(this.greatSendDatas[0]);
                    } else if (tabName === 'great-plan') {
                        const xGrantSingleTable = this.$refs.xGrantSingleTable;
                        xGrantSingleTable.remove(record);
                        this.greatSendDatas = xGrantSingleTable.getTableData().tableData;
                        xGrantSingleTable.setCurrentRow(this.greatSendDatas[0]);
                    }
                    this.applyDetailRemoveResetFn();
                    break;
            }
        },
        // 前端删除明细的操作重置
        applyDetailRemoveResetFn() {
            if (this.greatSendDatas.length <= 0) {
                this.currentTrgreatSendObj = {};
            } else {
                this.currentTrgreatSendObj = this.greatSendDatas[0];
            }
        },
        // 库存来源改变
        selectWarehouseSourceFn(value) {
            value && this.suppliesClaimDatas.length && this.hospitalGrantListAPIFn(this.currentTrObj, value);
            this.wareHouseFilterData.forEach((item) => {
                if (item.value === value) {
                    this.selectedWarehouse = item;
                    this.searchBotTables.warehouse_name = item.label;
                }
            });
        },
        // 打印 - 取消 - 发放 - 生成采购计划 按钮的操作
        greatSendModalGroupBtnFn(btnName) {
            if ((btnName === 'great-send' || btnName === 'great-save-plan') && !this.checkApplySingleFn(btnName))
                return;
            this.btnModalName = btnName;
            switch (btnName) {
                // 导出excel
                case 'exportExcel': {
                    if (this.grantSingleBtnName === 'great-send') {
                        // 发放
                        this.exportExcel('xGrantSendTable');
                    } else if (this.grantSingleBtnName === 'great-plan') {
                        // 生成采购计划
                        this.exportExcel('xGrantSingleTable');
                    }
                    break;
                }
                // 打印
                case 'print': {
                    let printContent = {
                        printData: this.greatSendDatas.map((item, index) => {
                            return {
                                ...item,
                                index: index + 1,
                                still_need_volumes:
                                    item.plan_quantity - item.real_quantity > 0
                                        ? item.plan_quantity - item.real_quantity
                                        : 0 // 还需发放量
                            };
                        }),
                        printForm: this.editInfoFormObj,
                        printTable: [],
                        printInfoStatus: this.currentTrObj.status,
                        printType: 2, // 1 图片；2、耗材申领；3、出入库单
                        printTitle: ''
                    };
                    printContent.printForm.department_name = this.currentTrObj.department_name;
                    printContent.printForm.employeeInfoName = this.employeeInfo.Name;
                    printContent.printForm.voucher_sn = this.editInfoFormObj.gv_sn;
                    if (this.grantSingleBtnName === 'great-send') {
                        printContent.printTable = this.$refs['xGrantSendTable'].getColumns();
                        printContent.printCaseType = 2; // 发放
                        printContent.printTitle = '发放单';
                    } else if (this.grantSingleBtnName === 'great-plan') {
                        printContent.printTable = this.$refs['xGrantSingleTable'].getColumns();
                        printContent.printCaseType = 3; // 生成采购计划
                        printContent.printTitle = '采购计划单';
                    }

                    // 操作列不显示出来
                    printContent.printTable = printContent.printTable
                        .map((item) => {
                            return {
                                dataIndex: item.property,
                                title: item.title,
                                key: item.property,
                                width: item.width,
                                align: 'center'
                            };
                        })
                        .filter((item) => item.title !== '操作');

                    printContent = JSON.stringify(printContent);
                    window.localStorage.printContent = printContent;
                    let routeUrl = this.$router.resolve({
                        path: 'print',
                        name: 'print'
                    });
                    window.open(routeUrl.href, '_blank');
                    break;
                }
                // 取消
                case 'cancel': {
                    this.greatSendDatas = [];
                    this.batchNumberCurItem = {};
                    if (this.grantSingleBtnName === 'great-send') {
                        this.$refs.xGrantSendTable.loadData(this.greatSendDatas);
                    } else {
                        this.$refs.xGrantSingleTable.loadData(this.greatSendDatas);
                    }
                    this.greatSendModal.alert = false;
                    break;
                }
                // 发放
                case 'great-send': {
                    this.hospitalGrantSendAPIFn();
                    break;
                }
                // 生成采购计划
                case 'great-save-plan': {
                    this.hospitalGrantCreatplanAPIFn();
                    break;
                }
            }
        },
        exportExcel(ref) {
            // 导出Excel
            const formDataCfg = {
                // 申领单查看
                'xApplySendConfirmModalTable': this.getExportFormDataForApply(),
                // 发放单查看
                'xApplySendModalTable': this.getExportFormDataForSend(),
                // 生成发放单
                'xGrantSendTable': this.getExportFormDataForGrantSend(),
                // 生成采购计划
                'xGrantSingleTable': this.getExportFormDataForGrantPlan()
            };

            const table = this.$refs[ref];
            const columns = table.getColumns();
            const tableData = table.getData();
            this.$Utils.exportExcel(columns, tableData, [], formDataCfg[ref]);
        },
        // 获取申领单查看表单信息
        getExportFormDataForApply() {
            const form = this.editInfoFormObj;
            return {
                title: '申领单',
                desc: {
                    label: '摘要',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '单据编号',
                            value: form.voucher_sn
                        },
                        {
                            label: '部门',
                            value: form.department_name
                        },
                        {
                            label: '申领人',
                            value: form.proposer_name
                        },
                        {
                            label: '期望于',
                            value: form.expect_date ? form.expect_date + ' 之前发放' : ''
                        }
                    ],
                    [
                        {
                            label: '生成时间',
                            value: form.create_time
                        }
                    ]
                ]
            };
        },
        // 获取发放单查看表单信息
        getExportFormDataForSend() {
            const form = this.applySendInfoFormObj;
            return {
                title: '发放单',
                desc: {
                    label: '摘要',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '部门',
                            value: form.department_name
                        },
                        {
                            label: '发放人',
                            value: form.approver_name
                        },
                        {
                            label: '发放时间',
                            value: form.grant_time
                        }
                    ]
                ]
            };
        },
        // 获取生成发放单表单信息
        getExportFormDataForGrantSend() {
            return {
                title: '发放单',
                desc: {
                    label: '摘要',
                    value: this.editInfoFormObj.description
                },
                groups: [
                    [
                        {
                            label: '部门',
                            value: this.currentTrObj.department_name
                        },
                        {
                            label: '发放人',
                            value: this.employeeInfo.Name
                        }
                    ]
                ]
            };
        },
        // 获取生成采购计划表单信息
        getExportFormDataForGrantPlan() {
            return {
                title: '采购计划单',
                desc: {
                    label: '摘要',
                    value: this.editInfoFormObj.description
                },
                groups: [
                    [
                        {
                            label: '部门',
                            value: this.currentTrObj.department_name
                        },
                        {
                            label: '制单人',
                            value: this.employeeInfo.Name
                        }
                    ]
                ]
            };
        },
        handlePrint(ref, isSend) {
            // 打印
            const table = this.$refs[ref];
            const columns = table.getColumns().map((item) => {
                return {
                    dataIndex: item.property,
                    title: item.title,
                    key: item.property,
                    width: item.width,
                    align: 'center'
                };
            });
            const tableData = table.getData().map((item, index) => ({ ...item, index: index + 1 }));

            let printContent = {
                printData: tableData,
                printForm: this.editInfoFormObj,
                printTable: columns,
                printType: 2, // 1 图片；2、耗材申领；3、出入库单
                printCaseType: 1, // 1申领 2发放
                printTitle: '申领单'
            };

            if (isSend) {
                printContent = {
                    ...printContent,
                    printTitle: '发放单',
                    printCaseType: 2,
                    printForm: {
                        ...this.applySendInfoFormObj,
                        employeeInfoName: this.applySendInfoFormObj.approver_name,
                        time: this.applySendInfoFormObj.grant_time
                    }
                };
            }

            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        // 待审核 / 审核未通过的切换 【经销商】
        clickMenuItemData1Fn(val) {
            if (parseInt(val.key) === 1) {
                this.menuItemData = {
                    class: true,
                    arrow: false
                };
            } else {
                this.menuItemData = {
                    class: false,
                    arrow: false
                };
            }
        },
        // 待审核 / 审核未通过的菜单显示状态改变时调用，参数为 visible 【经销商】
        visibleChangeFn(val) {
            this.menuItemData.arrow = val;
        },
        // 设置后端所需要的所属部门格式
        setEditInfoFormDepartmentInnerSnFn(value, label) {
            if (value) {
                this.editInfoFormObj.department_inner_sn = value[0];
                this.editInfoFormObj.department_name = label[0];
            } else {
                this.editInfoFormObj.department_inner_sn = '';
                this.editInfoFormObj.department_name = '';
            }
        },
        // 包装单位区域
        clickUnitListFn(record, clickName) {
            // if (clickName === 'unit' && !record.isLoadingUnit) {
            //     parseFloat(record.product_type) === 1 ? this.platformUnitInfoFn(record) : this.dealerUnitInfoFn(record)
            // }
            this.record = record;
            if (!clickName) {
                // 触发批准发放量的数字
                this.setPackgeMultipleFn(record);
            }
        },
        // 包装单位change
        changeUnitListFn(value, selectedOptions) {
            const len = selectedOptions.length;
            const unit = len ? selectedOptions[len - 1].label : '';
            this.greatSendDatas.forEach((item) => {
                if (item.key === this.record.key) {
                    item.unit = unit;
                    item.unit_name = unit;
                }
            });
            if (len) {
                const recordUnitSn = value[value.length - 1];
                const curUnitSn = this.record.min_unit_inner_sn;
                if (parseFloat(recordUnitSn) === parseFloat(curUnitSn)) {
                    this.setPackgeMultipleFn(this.record);
                } else {
                    parseFloat(this.record.product_type) === 1
                        ? this.platformUnitMinAPIFn(this.record, recordUnitSn)
                        : this.dealerUnitMinAPIFn(this.record, recordUnitSn);
                }
            }
        },
        displayUnitRender({ labels }) {
            let curLabel = labels[labels.length - 1];
            return curLabel ? curLabel.split('(')[0] : '';
        },
        clickUnitPlanListFn(record, clickName) {
            this.record = record;
            if (!clickName) {
                // 触发数量的数字
                this.setPackgeMultipleFn(record);
            }
        },
        reNumber(data) {
            let num = data.toString();
            let numA = 0;
            if (num.indexOf('.') !== -1) {
                numA = num.split('.')[1].length;
            }
            if (numA > 10) {
                data = data.toFixed(3);
            }
            data = Math.round(data * 1000) / 1000;
            return data;
        },
        // 设置批准发放量 * 包装单位的倍数
        setPackgeMultipleFn(record) {
            // for (let i = 0; i < this.greatSendDatas.length; i++) {
            //     const item = this.greatSendDatas[i]
            //     if (parseFloat(item.key) === parseFloat(this.record.key)) {
            //         const len = this.record.unit_inner_sn_new.length
            //         const recordUnitSn = this.record.unit_inner_sn_new[len - 1]
            //         const curUnitSn = this.record.min_unit_inner_sn
            //         if (this.record.approval_volumes && len) {
            //             if (parseFloat(recordUnitSn) === parseFloat(curUnitSn)) { // 包装单位 === 最小包装单位
            //                 item.packge_multiple_num = `${item.approval_volumes}${item.min_unit}`
            //             } else {
            //                 item.packge_multiple_num = this.reNumber(item.approval_volumes * item.packge_multiple) + item.min_unit
            //             }
            //         } else {
            //             item.packge_multiple_num = ''
            //         }
            //         break
            //     }
            // }
            for (let i = 0; i < this.greatSendDatas.length; i++) {
                const item = this.greatSendDatas[i];
                if (parseFloat(item.key) === parseFloat(record.key)) {
                    const len = record.unit_inner_sn_new.length;
                    const recordUnitSn = record.unit_inner_sn_new[len - 1];
                    const curUnitSn = record.min_unit_inner_sn;
                    // if (this.record.approval_volumes && len) {
                    if (record.approval_volumes) {
                        if (parseFloat(recordUnitSn) === parseFloat(curUnitSn)) {
                            // 包装单位 === 最小包装单位
                            item.packge_multiple_num = `${item.approval_volumes}${item.min_unit}`;
                        } else {
                            item.packge_multiple_num =
                                this.reNumber(item.approval_volumes * item.packge_multiple) + item.min_unit;
                        }
                    } else {
                        item.packge_multiple_num = '';
                    }
                    break;
                }
            }
        },
        // 检测并设置修改过的修改信息内容字段
        checkEditInfoFormObjFn() {
            let editInfoFlag = true;
            this.editInfoFormNewObj = {};
            for (let server in this.editInfoFormObj) {
                let serverItem = this.editInfoFormObj[server];
                for (let old in this.editInfoFormOldObj) {
                    let oldItem = this.editInfoFormOldObj[old];
                    if (server === old && serverItem !== oldItem) {
                        this.editInfoFormNewObj = {
                            ...this.editInfoFormNewObj,
                            [server]: serverItem, // 新值
                            [server + '_old']: oldItem // 旧值
                        };
                        editInfoFlag = false;
                    }
                }
            }
            return editInfoFlag;
        },
        /* 按钮区域的操作 */
        checkCurrentTrDataFn() {
            // 检测表格是否有选中的的数据
            if (Object.keys(this.currentTrObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 包装单位最后一项
        displayRender({ labels }) {
            let curLabel = labels[labels.length - 1];
            return curLabel ? curLabel.split('(')[0] : '';
        },
        // 搜索按钮操作
        searchBtnFn(searchBtnName) {
            if (!this.checkSearchInfoFormMsgFn()) return;
            if (searchBtnName === 'general') {
                // 普通搜索
                this.searchTables.status = '';
            }
            this.initTablesPaginationFn();
            this.activeTabsKey === this.systemTypes[0]
                ? this.hospitalGrantApplyListAPIFn()
                : this.hospitalGrantApplyHistoryListAPIFn();
        },
        checkSearchInfoFormMsgFn() {
            if (this.$Utils.checkInputValFn(this.searchTables.voucher_sn)) {
                this.$warning({
                    title: '操作提示',
                    content: `申领单编号${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (this.hightSearchObj.isArrow && this.$Utils.checkInputValFn(this.searchTables.proposer_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `申领人${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 点击高级搜索的小图标的事件
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
            if (this.hightSearchObj.isArrow && this.architectureAllTrees.length <= 0) {
                this.orgDeptTopTreeAPIFn();
            }
            let box = document.getElementsByClassName('box');
            if (this.hightSearchObj.isArrow) {
                for (let i = 0; i < box.length; i++) {
                    box[i].style.height = box[i].clientHeight - 60;
                }
                this.boxTopHeight -= 60;
            } else {
                for (let i = 0; i < box.length; i++) {
                    box[i].style.height = box[i].clientHeight + 60;
                }
                this.boxTopHeight += 60;
            }
            try {
                for (let i = 0; i < box.length; i++) {
                    document.getElementsByClassName('boxtop')[i].style.height = this.boxTopHeight + 55 + 'px';
                }
            } catch (e) {
                console.log(e);
            }
        },
        // 右上角刷新按钮
        refreshPageFn() {
            this.searchTables = {
                hospital_inner_sn: '',
                status: '',
                voucher_sn: '',
                proposer_name: '',
                department_inner_sn: ''
            };
            if (this.activeTabsKey === this.systemTypes[0]) {
                this.suppliesClaimBotSelectedRowKeys = [];
                this.suppliesClaimBotDatas = [];
                this.suppliesClaimDatas = [];
                this.searchBotTables.onlyUnGrant = true;
                this.suppliesClaimPagination = {
                    ...this.suppliesClaimPagination,
                    loading: true,
                    total: 10,
                    current: 1,
                    // pageSize: 10,
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                };
                this.hospitalGrantApplyListAPIFn();
            } else if (this.activeTabsKey === this.systemTypes[1]) {
                this.suppliesClaimHistoryPagination = {
                    ...this.suppliesClaimHistoryPagination,
                    loading: true,
                    total: 10,
                    current: 1,
                    // pageSize: 10,
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                };
                this.hospitalGrantApplyHistoryListAPIFn();
            }
            this.$refs.xSuppliesClaimBotTable.updateData();
            this.$refs.xSuppliesClaimBotTable.clearScroll();
        },
        // 递归加上索引
        treeDatasIndexAryFn(datasOrigin, isAppendDatas, id) {
            // datasOrigin: 源数据  isAppendDatas: 将数据放在哪里  id: 当前点击的是哪个id标识
            const _this = this;
            // 格式化部门名称
            function formatDepartmentNameFn(strComma) {
                // 研发部门,行政部门,其他
                if (!strComma) return strComma;
                return strComma.replace(/,/g, ' ');
            }
            // 子级递归
            function childrenIndexFn(list, pIndexAry, parentDepartmentNameOld, pItemDisabledFlag) {
                if (!pIndexAry || !parentDepartmentNameOld) return;
                return list.map((cItem, cIndex) => {
                    let cItemDisabledFlag =
                        _this.btnName === 'edit' &&
                        (pItemDisabledFlag || (isAppendDatas === 'parentDirectors' && id === cItem.employee_inner_sn));
                    if (cItem.children && cItem.children.length > 0) {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn,
                            activeClass: '',
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            department_name_new: formatDepartmentNameFn(cItem.department_name),
                            disabled:
                                cItemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(cItem.status) <= 0),
                            value: (cItem.employee_inner_sn || cItem.department_inner_sn).toString(),
                            label: cItem.name,
                            isLeaf: false,
                            children: childrenIndexFn(
                                cItem.children,
                                [...pIndexAry, cIndex],
                                [...parentDepartmentNameOld, cItem.name],
                                cItemDisabledFlag
                            )
                        };
                        if (obj.key === _this.infoKey) {
                            // 新增下级同级的时候选中新增的那一条
                            _this.currentTrObj = obj;
                        }
                        return obj;
                    } else {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn,
                            activeClass: '',
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            department_name_new: formatDepartmentNameFn(cItem.department_name),
                            disabled:
                                cItemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(cItem.status) <= 0),
                            value: (cItem.employee_inner_sn || cItem.department_inner_sn).toString(),
                            label: cItem.name,
                            children: cItem.children
                        };
                        if (obj.key === _this.infoKey) {
                            // 新增下级同级的时候选中新增的那一条
                            _this.currentTrObj = obj;
                        }
                        return obj;
                    }
                });
            }
            // 父级
            let datasOriginNew = datasOrigin.map((item, index) => {
                let itemDisabledFlag =
                    _this.btnName === 'edit' && isAppendDatas === 'parentDirectors' && id === item.employee_inner_sn;
                if (item.children && item.children.length > 0) {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn,
                        activeClass: '',
                        parentDepartmentName: [item.name],
                        department_name_new: formatDepartmentNameFn(item.department_name),
                        disabled: itemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(item.status) <= 0),
                        value: (item.employee_inner_sn || item.department_inner_sn).toString(),
                        label: item.name,
                        isLeaf: false,
                        children: childrenIndexFn(item.children, [index], [item.name], itemDisabledFlag)
                    };
                    if (obj.key === _this.infoKey) {
                        // 新增下级同级的时候选中新增的那一条
                        _this.currentTrObj = obj;
                    }
                    return obj;
                } else {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn,
                        activeClass: '',
                        parentDepartmentName: [item.name],
                        department_name_new: formatDepartmentNameFn(item.department_name),
                        disabled: itemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(item.status) <= 0),
                        value: (item.employee_inner_sn || item.department_inner_sn).toString(),
                        label: item.name,
                        children: item.children
                    };
                    if (obj.key === _this.infoKey) {
                        // 新增下级同级的时候选中新增的那一条
                        _this.currentTrObj = obj;
                    }
                    return obj;
                }
            });

            if (isAppendDatas === 'deptTopTree') {
                // 所属部门
                this.architectureAllTrees = datasOriginNew;
                if (this.btnName === 'add' || this.btnName === 'edit' || this.btnName === 'copy') {
                    const len = this.architectureAllTrees.length;
                    this.editInfoFormDepartmentInnerSn = len ? [this.architectureAllTrees[0].department_inner_sn] : [];
                    this.editInfoFormObj.department_inner_sn = this.editInfoFormDepartmentInnerSn[0];
                    this.editInfoFormObj.department_name = len ? this.architectureAllTrees[0].name : '';
                }
            }
        },
        // 表格的高亮样式显示
        rowClassFn(record) {
            if (record.key === this.currentTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        rowClassHistoryFn(record) {
            if (record.key === this.currentTrHistoryObj.key) {
                return 'active-tr';
            }
            return '';
        },
        rowClassgreatSendModalFn(record) {
            if (record.index === this.currentTrgreatSendObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowClassBotFn(record) {
            if (record.key === this.currentTrBotObj.key) {
                return 'active-tr';
            }
            return '';
        },
        rowClassHistoryBotFn(record) {
            if (record.key === this.currentTrHistoryBotObj.key) {
                return 'active-tr';
            }
            return '';
        },
        changeTabsFn(activeKey) {
            // 改变tabs的key值
            if (!this.checkSearchInfoFormMsgFn()) return;
            this.activeTabsKey = activeKey;
            if (this.activeTabsKey === this.systemTypes[0]) {
                // 待申领： 0-1-2 状态启用  3-4 状态禁用
                this.searchTablesStatus.forEach((item) => {
                    if (item.value === 0 || item.value === 1 || item.value === 2) {
                        item.disabled = false;
                    } else {
                        item.disabled = true;
                    }
                });
            } else {
                // 待申领： 0-1-2 状态禁用  3-4 状态启用
                this.searchTablesStatus.forEach((item) => {
                    if (item.value === 3 || item.value === 4) {
                        item.disabled = false;
                    } else {
                        item.disabled = true;
                    }
                });
            }
            if (!this.isActiveTabsKey) {
                this.isActiveTabsKey = true;
                this.hospitalGrantApplyHistoryListAPIFn();
            }
        },
        // 底部待发放表格选中项发生变化时的回调
        suppliesClaimBotSelectChangeFn(selectedRowKeys) {
            this.suppliesClaimBotSelectedRowKeys = selectedRowKeys;
        },
        // 仅显示待发放的产品细目
        changeOnlyUnGrantFn() {
            // this.searchBotTables.onlyUnGrant = e.target.checked
            this.filterSearchBotTablesFn();
            this.$refs['xSuppliesClaimBotTable'].refreshColumn();
        },
        // 筛选待发放的数据
        filterSearchBotTablesFn() {
            if (this.searchBotTables.onlyUnGrant) {
                // 0 2 仅发放勾选
                this.suppliesClaimBotDatas = this.suppliesClaimBotAllDatas.filter(
                    (item) => parseFloat(item.status) === 0 || parseFloat(item.status) === 2
                );
            } else {
                this.suppliesClaimBotDatas = this.suppliesClaimBotAllDatas;
            }
            if (this.suppliesClaimBotDatas.length) {
                this.currentTrBotObj = this.suppliesClaimBotDatas[0];
            } else {
                this.suppliesClaimBotDatas = [];
                this.currentTrBotObj = {};
            }
            this.$refs.xSuppliesClaimBotTable.setCurrentRow(this.currentTrBotObj);
            if (this.suppliesClaimBotAllDatas.every((item) => item.status === 3) && this.isApplyDetailStopFlag) {
                // 说明已经全部已终止申领
                this.activeTabsKey = this.systemTypes[0];
                this.hospitalGrantApplyListAPIFn();
                this.hospitalGrantApplyHistoryListAPIFn(false, this.currentTrObj);
            }
            this.isApplyDetailStopFlag = false;
        },
        // 申领终止按钮的状态
        applyStopBtnFn(btnName, record) {
            const _this = this;
            if (!this.currentTrObj['_XID']) {
                _this.$confirm({
                    title: '操作提示',
                    content: '请选择申领单再进行操作',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return;
            }
            _this.$confirm({
                title: '申领终止',
                content: '申领终止后，耗材不再继续发放，您确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    btnName === 'apply-stop'
                        ? _this.hospitalApplyDetailStopAPIFn(record)
                        : _this.hospitalApplyStopAPIFn(_this.currentTrObj);
                },
                onCancel() {}
            });
        },
        // 发放 - 生成采购计划的按钮
        hospitalGrantSendGroupBtnFn(btnName) {
            this.suppliesClaimBotSelectedRowKeys = this.$refs.xSuppliesClaimBotTable.getSelectRecords();
            if (this.suppliesClaimBotSelectedRowKeys.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noCheckbox,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
            } else {
                this.formatGreatSendDatas(btnName);
                this.greatSendDatas = this.details;
                this.record = this.greatSendDatas[0];
                if (!this.checkGrandSendPlanFn(btnName)) return;
                this.grantSingleBtnName = btnName;
                switch (btnName) {
                    // 发放
                    case 'great-send':
                        if (this.currentTrObj.warehouse_inner_sn === null) {
                            this.needWarehouse = true;
                            this.greatSendPagination.loading = true;
                            if (this.greatSendDatas.length) {
                                this.greatSendDatas.forEach((item) => {
                                    parseFloat(item.product_type) === 1
                                        ? this.platformUnitTreeFn(item)
                                        : this.dealerUnitTreeAPIFn(item);
                                });
                                this.currentTrgreatSendObj = this.greatSendDatas[0];
                            } else {
                                this.currentTrgreatSendObj = {};
                            }
                            this.editInfoFormObj.gv_sn = '';
                            this.editInfoFormObj.description = '';
                            this.greatSendModal.title = `${this.$route.meta.title} - 发放单`;
                            this.greatSendModal.alert = true;
                            this.$Utils.globalDragCenterFn('modal-great-send');
                            this.greatSendPagination.loading = false;
                            this.$nextTick(() => {
                                this.$refs.xGrantSendTable.loadData(this.greatSendDatas);
                                this.$refs.xGrantSendTable.setCurrentRow(this.currentTrgreatSendObj);
                            });
                        } else {
                            this.$Utils.globalDragCenterFn('warehouseTransfer-modal-add');
                            this.addAllotModal.visible = true;
                            this.addAllotModal.isAdd = true;
                            if (this.$refs.addAllotSheetModal) {
                                this.$refs.addAllotSheetModal.initPage(true);
                                let dispatchInfo = {
                                    hospital_inner_sn: this.currentTrObj.hospital_inner_sn,
                                    voucher_sn: this.currentTrObj.voucher_sn,
                                    department_inner_sn: this.currentTrObj.department_inner_sn,
                                    department_name: this.currentTrObj.department_name,
                                    warehouse_inner_sn: this.searchBotTables.warehouse_inner_sn,
                                    warehouse_name: this.selectedWarehouse.label,
                                    source_warehouse_inner_sn: this.searchBotTables.warehouse_inner_sn,
                                    target_warehouse_inner_sn: this.currentTrObj.warehouse_inner_sn,
                                    voucher_inner_sn: this.currentTrObj.voucher_inner_sn
                                };
                                this.$refs.addAllotSheetModal.dispatchInit(dispatchInfo, this.greatSendDatas);
                            }
                        }
                        break;
                    case 'great-plan': // 生成采购计划
                        this.needWarehouse = false;
                        this.greatSendPagination.loading = true;
                        if (this.greatSendDatas.length) {
                            this.greatSendDatas.forEach((item) => {
                                parseFloat(item.product_type) === 1
                                    ? this.platformUnitTreeFn(item)
                                    : this.dealerUnitTreeAPIFn(item);
                            });
                            this.currentTrgreatSendObj = this.greatSendDatas[0];
                        } else {
                            this.currentTrgreatSendObj = {};
                        }
                        this.editInfoFormObj.gv_sn = '';
                        this.editInfoFormObj.description = '';
                        this.greatSendModal.title = `${this.$route.meta.title} - 采购计划单`;
                        this.greatSendModal.alert = true;
                        this.$Utils.globalDragCenterFn('modal-great-send');
                        this.greatSendPagination.loading = false;
                        this.$nextTick(() => {
                            this.$refs.xGrantSingleTable.loadData(this.greatSendDatas);
                            this.$refs.xGrantSingleTable.setCurrentRow(this.currentTrgreatSendObj);
                        });
                        break;
                }
            }
        },
        // 是否可发放 - 生成采购计划的按钮状态校验
        checkGrandSendPlanFn(btnName) {
            let flag = true;
            if (btnName === 'great-send') {
                for (let i = 0; i < this.greatSendDatas.length; i++) {
                    const item = this.greatSendDatas[i],
                        status = parseFloat(item.status);
                    if (!(status === 0 || status === 2)) {
                        this.$warning({
                            title: '操作提示',
                            content: '只有【申领中/已计划采购】的状态可以发放。',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                }
            } else if (btnName === 'great-plan') {
                for (let i = 0; i < this.greatSendDatas.length; i++) {
                    const item = this.greatSendDatas[i],
                        status = parseFloat(item.status);
                    if (status !== 0) {
                        this.$warning({
                            title: '操作提示',
                            content: '只有【申领中】的状态可以生成采购计划。',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                }
            }
            return flag;
        },
        // 待发放勾选页面中的数据details
        formatGreatSendDatas(btnName) {
            this.details = [];
            this.suppliesClaimBotDatas.forEach((item, index) => {
                this.suppliesClaimBotSelectedRowKeys.forEach((cItem) => {
                    if (parseFloat(item.detail_inner_sn) === parseFloat(cItem.detail_inner_sn)) {
                        if (btnName === 'great-send') {
                            item.approval_volumes = '';
                        }

                        // BUG[3785] 当转换为最小包装单位除不尽时，取三位小数
                        let newApprovalVolumes = item.plan_quantity * item.min_unit_quantity - item.approved_quantity;

                        if (newApprovalVolumes % 1 !== 0) {
                            newApprovalVolumes = newApprovalVolumes.toFixed(3);
                        }

                        this.details.push({
                            ...item,
                            index,
                            batch_number_list: item.batch_number
                                ? [
                                      {
                                          'batch_number': item.batch_number,
                                          'overdue_date': '',
                                          'produce_date': '',
                                          'quantity': 0
                                      }
                                  ]
                                : [], // 批号列表
                            batch_number_loading: true, // 是否加载过批号数据 [默认没有加载过]
                            // approval_volumes: '', // 批准发放量 - 发放 / 数量 - 采购计划 == > 详见1230 / 1234行代码
                            packge_multiple: '', // 最小倍数的包装单位 - 发放
                            packge_multiple_num: '', // 批准发放量 * 最小倍数的包装单位 - 发放
                            // still_need_volumes: item.plan_quantity - item.real_quantity > 0 ?
                            approval_volumes: newApprovalVolumes
                            // approval_volumes: item.plan_quantity // 数量（申领数量） - 采购计划单
                        });
                    }
                });
            });
        },
        /**
         * 得到页面用于展示内容的高度
         * @return {number}
         */
        getContentHeight() {
            // 顶部导航高度(含margin,下同)
            let navHeight = 51;
            // 工具栏高度(跟高级搜索是否展开关联)
            let toolsHeight = this.hightSearchObj.isArrow ? 122 : 62;
            // tabs切换标题栏高度
            let tabsTitleHeight = 40;
            // tab 容器内边距
            let tabBodyPadding = 20;
            // 上方表格页码高度
            let topTablePagerHeight = 48;
            // 页面高度
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
            // 剩余高度用于展示内容
            return clientHeight - navHeight - toolsHeight - tabsTitleHeight - tabBodyPadding - topTablePagerHeight;
        },
        /**
         * 判断数据是否为小数
         * @param {number} num
         * @return {boolean}
         * */
        isFloat(num) {
            if (num % 1 === 0) {
                return false;
            } else {
                return true;
            }
        },
        /**
         * 将单位转换为用户所选单位
         * @param {number} count 带转换数量
         * @param {object} item 带转换的耗材项
         * */
        transToUserUnit(count, item) {
            // 已批准发放量换算为用户所选单位
            let resNum = count / item.min_unit_quantity;
            let resStr = '';
            if (resNum % 1 === 0) {
                // 结果为整数
                resStr = `${resNum}${item.unit}`;
            } else {
                // 结果为小数字
                resStr = `${resNum.toFixed(1)}${item.unit}`;
            }

            if (count % 1 !== 0) {
                count = count.toFixed(3);
            }
            return `${resStr}(${count}${item.min_unit})`;
        },
        transToUnit(count, item) {
            // 已批准发放量换算为用户所选单位
            let resNum = count;
            let resStr = '';
            let countNum = count * item.min_unit_quantity;
            let countStr = '';
            if (resNum % 1 === 0) {
                // 结果为整数
                resStr = `${resNum}${item.unit}`;
            } else {
                // 结果为小数字
                resStr = `${resNum.toFixed(1)}${item.unit}`;
            }
            if (countNum % 1 === 0) {
                countStr = `${count * item.min_unit_quantity}${item.min_unit}`;
            } else {
                countStr = `${(count * item.min_unit_quantity).toFixed(3)}${item.min_unit}`;
            }
            return `${resStr}(${countStr})`;
        },
        //
        // louxiaochen: 因为批号下拉改为可输入，不再使用方法
        // batchNumberFocusFn (row, seq) {
        //     if (row.batch_number_loading) {
        //         this.planBatchListAPIFn(row, seq)
        //     } else {
        //         this.batchNumberCurItem = {
        //             ...row,
        //             seq: seq - 1 < 0 ? 0 : seq - 1
        //         }
        //     }
        // },
        batchNumberChangeFn() {
            // louxiaochen: 取消函数参数
            //              使用外部方法onBatchNumberSelectFn()或onBatchNumberInputFn()进行赋值并调用此方法
            // if (changeVal) {
            //     this.batchNumberCurItem.batch_number = changeVal.key
            // }
            let seq = this.batchNumberCurItem.seq,
                record = this.greatSendDatas[seq],
                quantityTemp =
                    record.batch_number_list && record.batch_number_list.length
                        ? record.batch_number_list.find((f) => f.batch_number === this.batchNumberCurItem.batch_number)
                              .quantity
                        : 0;

            // 批号的库存大于实际可用库存时可用库存显示实际可用库存
            if (typeof this.batchNumberCurItem.inventoryBannce !== 'undefined') {
                quantityTemp =
                    quantityTemp < this.batchNumberCurItem.inventoryBannce
                        ? quantityTemp
                        : this.batchNumberCurItem.inventoryBannce;
            }
            this.greatSendDatas.splice(this.batchNumberCurItem.seq, 1, {
                ...this.batchNumberCurItem,
                available_inventory_new:
                    typeof this.batchNumberCurItem.batch_number === 'undefined'
                        ? this.greatSendDatas[seq].available_inventory_new
                        : `${quantityTemp}${record.min_unit}`,
                available_inventory:
                    typeof this.batchNumberCurItem.batch_number === 'undefined'
                        ? this.greatSendDatas[seq].available_inventory
                        : quantityTemp
            });
            if (this.grantSingleBtnName === 'great-send') {
                this.$refs.xGrantSendTable.loadData(this.greatSendDatas);
            } else {
                this.$refs.xGrantSingleTable.loadData(this.greatSendDatas);
            }
        },
        // 锁定库存计算前端逻辑变更
        lockedInventoryNewFn(item) {
            if (item.inventory_all - item.min_unit_lock_inventory_all < item.inventory - item.min_unit_lock_inventory) {
                return (
                    item.min_unit_lock_inventory +
                    (item.inventory - item.min_unit_lock_inventory) -
                    (item.inventory_all - item.min_unit_lock_inventory_all)
                );
            }
            return item.min_unit_lock_inventory;
        },
        /**
         * 当选中批号下拉时触发
         * @param {bool} isPlan 是否是生成采购计划页面
         * @param {object} 当前选中行对象
         * @param {String} 所选下拉的值
         */
        onBatchNumberSelectFn(isPlan, row, curVal) {
            row.batch_number = curVal;
            this.batchNumberCurItem.batch_number = curVal;
            if (!isPlan) {
                this.batchNumberChangeFn();
            }
        },
        /**
         * 当手动输入批号时触发
         * @param {bool} isPlan 是否是生成采购计划页面
         * @param {object} 当前选中行对象
         * @param {event} 输入事件
         */
        onBatchNumberInputFn(isPlan, row, e) {
            const { value } = e.target;
            row.batch_number = value;
            this.batchNumberCurItem.batch_number = value;
            if (!isPlan) {
                this.batchNumberChangeFn();
            }
        }
    },
    computed: {
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                pager = 115;
            return (clientHeight - pager) / 2;
        },
        /**
         * 计算顶部表格高度
         * @return {number}
         */
        topTableHeight() {
            // 内容展示区高度的60%
            return Math.floor(this.getContentHeight() * 0.582);
        },
        /**
         * 计算底部表格高度
         * @return {number}
         */
        bottomTableHeight() {
            // 内容展示区高度的40%
            // 需要减去下方表格工具栏高度
            return Math.floor(this.getContentHeight() * 0.418);
        },
        scrollYDblclickNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 307;
            return clientHeight - topHeight;
        },
        onlyUnGrantDisabled() {
            return this.suppliesClaimBotAllDatas.length <= 0;
        },
        computedGrantSingleTitle() {
            let title = '';
            if (this.grantSingleBtnName === 'great-send') {
                title = '发放单';
            } else {
                // this.grantSingleBtnName === 'great-plan'
                title = '采购计划单';
            }
            return title;
        },
        // 机构名称
        orgName() {
            const employeeInfo = this.$cookie.get('EmployeeInfo');
            return (employeeInfo ? JSON.parse(employeeInfo).OrgName : '') || '';
        },
        ...mapState(['warehouseList'])
    },
    mounted() {
        this.boxTopHeight = this.scrollYNumber - 78;
        this.boxDownHeight = this.scrollYNumber;
        this.dragControllerDiv();
    },
    created() {
        this.initTablesPaginationFn();
        this.warehouseUsableTopListAPIFn();
    }
};
</script>

<style lang="less" scoped>
.scoped .bot-search-form-wrap {
    padding: 0;
    max-height: 300px;
    border: none;
    .bot-hight-search-wrap {
        padding: 0 0px 8px;
        .form li:last-child {
            width: auto;
            .ant-btn {
                margin-right: 24px;
            }
        }
    }

    .bot-product-item {
        padding: 16px;
        text-align: left;
    }
}
li#to-be-issued {
    width: 15rem;
}
.modal-form-input.for-single .grant-claim-info .item span {
    display: inline-block;
    &:first-child {
        margin-right: 20px;
    }
    &:last-child {
        font-weight: bold;
    }
}
</style>
<style scoped>
.box {
    width: 100%;
    top: 40px;
    bottom: 0px;

    overflow: hidden;
    position: absolute;
}
.boxtop {
    width: calc(100% - 2px);
    height: calc(50% - 3px);
    padding: 8px;
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
    width: calc(100% - 2px);
    padding: 0px 8px;
    float: left;
    height: 50%;
}
</style>
