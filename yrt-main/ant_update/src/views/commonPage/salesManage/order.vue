<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button type="primary" size="small" @click="groupBtnFn('add')"> 新增 </a-button>
                            <a-button type="primary" size="small" @click="groupBtnFn('edit')"> 修改 </a-button
                            ><!-- :disabled="topEditRemoveBtnDisabled" -->
                            <a-button type="danger" size="small" @click="groupBtnFn('remove')"> 删除 </a-button>
                        </li>
                        <li>
                            <a-button
                                :disabled="activeTabsKey === tabTypes[1]"
                                type="primary"
                                size="small"
                                @click="groupBtnFn('submit')"
                            >
                                确认
                            </a-button>
                            <a-button
                                :disabled="activeTabsKey === tabTypes[1]"
                                type="primary"
                                size="small"
                                @click="groupBtnFn('stop')"
                            >
                                终止
                            </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-show="!hightSearchObj.isArrow"
                            v-model="searchTables.order_sn"
                            placeholder="单据编号"
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
                            <span class="label">收货单位</span>
                            <a-input v-model="searchTables.partA_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">单据编号</span>
                            <a-input v-model="searchTables.order_sn" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">状态</span>
                            <a-cascader
                                :options="searchTablesStatus"
                                v-model="searchTables.status"
                                placeholder=""
                                changeOnSelect
                            />
                        </li>
                        <li>
                            <a-button @click="searchBtnFn" key="search" type="primary"> 搜索 </a-button>
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
                <a-tab-pane tab="待处理" :key="tabTypes[0]" forceRender>
                    <div class="box">
                        <div class="boxtop">
                            <!-- 表格头部 scrollYNumber-->
                            <vxe-table
                                ref="xSuppliesClaimTable"
                                size="small"
                                :height="tabTop - ipadHeight"
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
                                    style="display: none"
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
                        <div class="mid">
                            <!-- 下方表格区域 -->
                            <div style="padding-top: 12px" class="search-form-wrap bot-search-form-wrap">
                                <a-row class="hight-search-wrap bot-hight-search-wrap">
                                    <a-col :span="24" class="col-list bot-hight-search-container">
                                        <div class="bot-hight-search-select" v-if="systemType === 2">
                                            <span class="label">仓库</span>
                                            <a-tree-select
                                                ref="idropdo"
                                                v-if="systemType === 2"
                                                style="width: 520px; height: 32px"
                                                treeNodeFilterProp="title"
                                                :showSearch="true"
                                                :treeData="wareHouseFilterData"
                                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                                treeDefaultExpandAll
                                                treeCheckable
                                                allowClear="false"
                                                v-model="searchBotTables.warehouse_inner_sn"
                                                class="input"
                                                @click="dropShow"
                                                @change="selectWarehouseSourceFn"
                                            >
                                                <!--@change="selectWarehouseSourceFn"-->
                                            </a-tree-select>
                                        </div>
                                        <a-checkbox
                                            v-if="systemType === 2 || systemType === 3"
                                            :disabled="onlyUnGrantDisabled"
                                            v-model="searchBotTables.onlyUnGrant"
                                            @change="changeOnlyUnGrantFn"
                                            class="bot-hight-search-checkbox"
                                        >
                                            仅显示待销售的产品细目
                                        </a-checkbox>
                                        <div class="bot-hight-search-button">
                                            <a-button @click="hospitalGrantSendGroupBtnFn('great-send')" type="primary">
                                                生成销售单
                                            </a-button>
                                            <a-button
                                                v-if="systemType === 2"
                                                @click="hospitalGrantSendGroupBtnFn('great-plan')"
                                                type="primary"
                                            >
                                                生成采购计划
                                            </a-button>
                                        </div>
                                    </a-col>
                                </a-row>
                                <!-- 注：下方表格的库存字段如果是厂商端登录去掉，经销商端显示 【详见本代码的198行 和 1174行】 -->
                                <vxe-grid
                                    ref="xSuppliesClaimBotTable"
                                    size="small"
                                    :height="tabDown - 10 - ipadHeight"
                                    border
                                    resizable
                                    auto-resize
                                    show-overflow
                                    show-header-overflow
                                    highlight-current-row
                                    @cell-click="cellClickSuppliesClaimBotFn"
                                    @cell-dblclick="cellDblclickSuppliesClaimBotFn"
                                    align="center"
                                    :data="suppliesClaimBotDatas"
                                    :loading="suppliesClaimBotPagination.loading"
                                    :columns="suppliesColumns"
                                >
                                </vxe-grid>
                                <vxe-table
                                    ref="xSuppliesClaimBotTable.bak"
                                    size="small"
                                    :height="140 - ipadHeight"
                                    border
                                    resizable
                                    auto-resize
                                    highlight-current-row
                                    @cell-click="cellClickSuppliesClaimBotFn"
                                    @cell-dblclick="cellDblclickSuppliesClaimBotFn"
                                    align="center"
                                    :data="suppliesClaimBotDatas"
                                    :loading="suppliesClaimBotPagination.loading"
                                    show-overflow
                                    show-header-overflow
                                    showHeader
                                    style="display: none"
                                >
                                    <vxe-table-column
                                        type="checkbox"
                                        width="5%"
                                        :resizable="false"
                                        fixed="left"
                                    ></vxe-table-column>
                                    <vxe-table-column
                                        field="product_name"
                                        title="产品"
                                        width="16%"
                                        :cell-render="{ name: 'product_name' }"
                                    ></vxe-table-column>
                                    <vxe-table-column
                                        field="specification"
                                        title="规格型号"
                                        width="11%"
                                        :cell-render="{ name: 'specification' }"
                                    ></vxe-table-column>
                                    <vxe-table-column
                                        field="product_vendor_name"
                                        title="厂商"
                                        width="14%"
                                        :cell-render="{ name: 'product_vendor_name' }"
                                    ></vxe-table-column>
                                    <vxe-table-column
                                        field="batch_number"
                                        title="批号"
                                        width="15%"
                                        :cell-render="{ name: 'batch_number' }"
                                    ></vxe-table-column>
                                    <vxe-table-column
                                        field="quantity_new"
                                        title="订购数量"
                                        width="15%"
                                        :cell-render="{ name: 'quantity_new' }"
                                    ></vxe-table-column>
                                    <vxe-table-column
                                        field="sold_quantity"
                                        title="已销售数量"
                                        width="10%"
                                        :cell-render="{ name: 'sold_quantity' }"
                                    ></vxe-table-column>
                                    <vxe-table-column
                                        v-if="systemType === 2"
                                        field="inventory"
                                        title="库存"
                                        width="6%"
                                        :cell-render="{ name: 'inventory' }"
                                    ></vxe-table-column>
                                    <vxe-table-column
                                        field="status_text"
                                        title="状态"
                                        width="10%"
                                        :cell-render="{ name: 'status_text' }"
                                    ></vxe-table-column>
                                    <vxe-table-column
                                        field="unit_price"
                                        title="单价"
                                        width="7%"
                                        :cell-render="{ name: 'unit_price' }"
                                    ></vxe-table-column>
                                    <!-- <vxe-table-column
                                        field="discount"
                                        title="折扣"
                                        width="6%"
                                        :cell-render="{name: 'discount'}"
                                    ></vxe-table-column> -->
                                    <vxe-table-column field="rate" title="税率" width="6%">
                                        <template v-slot="{ row }">
                                            <div class="rate-wrap">
                                                <span class="input">{{ row.rate }}</span>
                                                <span class="tip">%</span>
                                            </div>
                                        </template>
                                    </vxe-table-column>
                                    <vxe-table-column
                                        field="action"
                                        title="操作"
                                        width="80"
                                        :resizable="false"
                                        fixed="right"
                                    >
                                        <template v-slot="{ row }">
                                            <ul class="action-btn-list-wrap" style="position: relative">
                                                <li v-if="row.status === 0 || row.status === 2">
                                                    <a-tooltip
                                                        class="tooltip"
                                                        placement="top"
                                                        title="终止"
                                                        :getPopupContainer="getPopupContainer"
                                                    >
                                                        <a-icon v-if="row.applyStopBtnLoading" type="loading" />
                                                        <i
                                                            v-else
                                                            @click.stop="applyStopBtnFn('apply-stop-wrap-details', row)"
                                                            class="yrt yrt-zhongzhi-gaoduan cancel"
                                                        ></i>
                                                    </a-tooltip>
                                                </li>
                                            </ul>
                                        </template>
                                    </vxe-table-column>
                                </vxe-table>
                            </div>
                        </div>
                    </div>
                    <!-- 下方表格区域 -->
                </a-tab-pane>
                <a-tab-pane tab="已处理" :key="tabTypes[1]" forceRender>
                    <div class="handlebox">
                        <div class="handleBoxtop">
                            <!-- 表格头部 scrollYNumberHistory-->
                            <vxe-table
                                ref="xSuppliesClaimHistoryTable"
                                size="small"
                                :height="tabTopHand - ipadHeight"
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
                                    v-for="suppliesClaimHistoryItem in suppliesClaimColumns"
                                    :key="suppliesClaimHistoryItem.key"
                                    :width="suppliesClaimHistoryItem.width"
                                    :field="suppliesClaimHistoryItem.dataIndex"
                                    :title="suppliesClaimHistoryItem.title"
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
                        <div class="handleResize"></div>
                        <div class="handleBoxdown">
                            <div class="search-form-wrap bot-search-form-wrap" style="margin-top: 8px">
                                <!--之前是209-->
                                <vxe-table
                                    ref="xSuppliesClaimHistoryBotTable"
                                    size="small"
                                    :height="tabDownHand - ipadHeight"
                                    border
                                    resizable
                                    auto-resize
                                    highlight-current-row
                                    @cell-click="cellClickSuppliesClaimHistoryBotFn"
                                    @cell-dblclick="cellDblclickSuppliesClaimHistoryBotFn"
                                    align="center"
                                    :data="suppliesClaimHistoryBotDatas"
                                    :loading="suppliesClaimHistoryBotPagination.loading"
                                    show-overflow
                                    show-header-overflow
                                    showHeader
                                >
                                    <vxe-table-column
                                        v-for="suppliesClaimHistoryBotItem in suppliesClaimHistoryBotColumns"
                                        :key="suppliesClaimHistoryBotItem.key"
                                        :width="suppliesClaimHistoryBotItem.width"
                                        :field="suppliesClaimHistoryBotItem.dataIndex"
                                        :title="suppliesClaimHistoryBotItem.title"
                                    ></vxe-table-column>
                                </vxe-table>
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

        <!-- 销售订单新增/修改区域开始 -->
        <a-modal
            v-model="planModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-add global-drag"
            :width="1200"
            :zIndex="3"
            :afterClose="afterClosePlanmodalFn"
        >
            <div v-globalDrag="{ el: 'modal-add' }" class="modal-header-wrap" @dblclick="modalFullscreen">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ infoModal.title }}
                </h6>
                <span
                    class="modal-fullscreen"
                    v-if="isFullscreen"
                    @click="modalFullscreen"
                    @mouseover="fullscreenMouseOver(false)"
                    @mouseout="fullscreenMouseOver(true)"
                >
                    <i data-v-4a953340="" aria-label="最小化" class="modal-fullscreen-icon anticon anticon-arrows-alt">
                        <svg
                            width="20px"
                            height="20px"
                            viewBox="0 0 256 256"
                            version="1.1"
                            xmlns="http://www.w3.org/2000/svg"
                            xmlns:xlink="http://www.w3.org/1999/xlink"
                        >
                            <title>还原</title>
                            <g id="还原" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                                <g
                                    id="编组"
                                    transform="translate(128.500000, 127.500000) scale(-1, 1) translate(-128.500000, -127.500000) translate(42.000000, 41.000000)"
                                    fill="#ffffff"
                                >
                                    <path
                                        d="M173,47 L173,173 L47,173 L47,47 L173,47 Z M159,62 L62,62 L62,159 L159,159 L159,62 Z"
                                        id="形状结合"
                                    ></path>
                                    <path
                                        d="M126,0 L126,52.672 L107.545,52.672 L107.545455,17.5454545 L17.5454545,17.5454545 L17.5454545,107.545455 L56.803,107.545 L56.803,126 L0,126 L0,0 L126,0 Z"
                                        id="形状结合"
                                    ></path>
                                </g>
                            </g>
                        </svg>
                    </i>
                </span>
                <span
                    class="modal-fullscreen"
                    v-else
                    @click="modalFullscreen"
                    @mouseover="fullscreenMouseOver(false)"
                    @mouseout="fullscreenMouseOver(true)"
                >
                    <i data-v-4a953340="" aria-label="最小化" class="modal-fullscreen-icon anticon anticon-arrows-alt">
                        <svg
                            width="20px"
                            height="20px"
                            viewBox="0 0 256 256"
                            version="1.1"
                            xmlns="http://www.w3.org/2000/svg"
                            xmlns:xlink="http://www.w3.org/1999/xlink"
                        >
                            <title>全屏</title>
                            <g id="全屏" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                                <path
                                    d="M214,41 L214,214 L41,214 L41,41 L214,41 Z M198,57 L57,57 L57,198 L198,198 L198,57 Z"
                                    id="形状结合"
                                    fill="#ffffff"
                                ></path>
                            </g>
                        </svg>
                    </i>
                </span>
            </div>
            <template slot="footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button @click="handlePrint('xTablePlanModalInfo')" key="print"> 打印 </a-button>
                        <a-button @click="exportExcel('xTablePlanModalInfo')" type="primary">导出Excel</a-button>
                        <!-- <a-button :disabled="isAddPlanModalDisabled"
                                  @click="planModalGroupBtnFn('add')"
                                  :loading="planModal.loadingAdd"
                                  key="add">
                            生成销售单
                        </a-button> -->
                    </li>
                    <li>
                        <a-button @click="planModalGroupBtnFn('cancel')" key="cancel">
                            {{ planModalTextComputed }}
                        </a-button>
                        <a-button
                            v-show="planModalForm.status === 0 || btnName === 'add'"
                            :disabled="isEditDisabled"
                            @click="planModalGroupBtnFn('saveDraft')"
                            key="saveDraft"
                            type="primary"
                            :loading="planModal.loadingSaveDraft"
                        >
                            存草稿
                        </a-button>
                        <a-button
                            v-show="
                                planModalForm.status === 0 ||
                                planModalForm.status === 1 ||
                                btnName === 'add' ||
                                isSourceBuyerAudited
                            "
                            :disabled="isSourceBuyerAudited ? false : isEditDisabled"
                            @click="planModalGroupBtnFn('save')"
                            key="submit"
                            type="primary"
                            :loading="planModal.loadingSave"
                        >
                            {{
                                planModalForm.status === 2 && planModalForm.create_organization_type === 0
                                    ? '确认'
                                    : '保存'
                            }}
                        </a-button>
                        <!--    <a-button v-show="planModalForm.status === 2 && planModalForm.create_organization_type !== 1"-->
                        <!--              @click="planModalGroupBtnFn('savePo')"-->
                        <!--              key="submitPo"-->
                        <!--              type="primary"-->
                        <!--              :loading="planModal.loadingSave">-->
                        <!--        保存-->
                        <!--    </a-button>-->
                        <a-button
                            v-show="planModalForm.status === 1"
                            :disabled="isEditDisabled"
                            @click="planModalGroupBtnFn('audit')"
                            key="audit"
                            type="primary"
                            :loading="planModal.loadingAudit"
                        >
                            审核
                        </a-button>
                    </li>
                </ul>
            </template>
            <div class="modal-form-input" style="height: 520px">
                <h6
                    style="
                        position: relative;
                        padding-bottom: 24px;
                        margin-bottom: 0;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    "
                >
                    <span style="margin-right: 10px; font-size: 24px; color: #444">{{ orgName }}</span>
                    <span style="font-size: 24px; color: #444">销售订单</span>
                    <div v-if="btnName !== 'add'" style="position: absolute; right: 0; display: flex">
                        <span class="label" style="font-size: 14px; color: rgba(0, 0, 0, 0.65)"
                            ><em class="em red">*</em>单据编号</span
                        >
                        <a-input disabled v-model="planModalForm.so_sn" placeholder="" class="input" />
                    </div>
                </h6>
                <ul class="top-list" style="'width:60%'">
                    <li class="item">
                        <span class="label"><em class="em red">*</em>收货单位</span>
                        <a-input
                            class="input"
                            type="text"
                            :disabled="btnName === 'edit'"
                            readonly="readonly"
                            v-model="infoAllData.organization_name"
                            @click="openChooseTb"
                            placeholder=""
                        />
                    </li>
                    <li class="item">
                        <span class="label">期望到货日期</span>
                        <a-date-picker
                            :disabled="isEditDisabled"
                            v-model="expectAogDate"
                            format="YYYY-MM-DD"
                            @change="changeExpectAogDateFn"
                            placeholder=""
                            allowClear
                            class="input"
                        />
                    </li>
                    <li class="item"> </li>
                    <li class="item"> </li>
                </ul>
                <div
                    ref="applyTable"
                    class="tab-table-wrap cur-overflow-hidden"
                    style="padding-bottom: 0; margin-top: 0"
                >
                    <!-- <div class="top-add-product">
                        <a-tooltip placement="topLeft" title="添加产品">
                            <a-button
                                :disabled="isEditDisabled"
                                @click="dblclickTableFn"
                                icon="plus"
                                style="height:24px;padding:0 4px;"
                            ></a-button>
                        </a-tooltip>
                    </div> -->
                    <div class="tableFather" style="height: 290px">
                        <vxe-grid
                            ref="xTablePlanModalInfo"
                            class="my-vxe-toolbar"
                            size="small"
                            :toolbar="xTablePlanModalInfoToolbar"
                            :data="planModalDatas"
                            :loading="planModalPagination.loading"
                            height="auto"
                            border
                            resizable
                            auto-resize
                            show-overflow
                            show-header-overflow
                            showHeader
                            highlight-current-row
                            :row-class-name="rowClassNameFn"
                            @cell-click="cellAddInfoClickFn"
                            @cell-dblclick="cellAddInfoDblclickFn"
                            align="center"
                            show-footer
                            :footer-method="footerMethodFn"
                            :footer-cell-class-name="footerCellClassNameFn"
                        >
                            <template v-slot:xtable_plan_modal_info_toolbar>
                                <a-tooltip placement="topLeft" title="添加产品">
                                    <a-button
                                        :disabled="isEditDisabled"
                                        @click="dblclickTableFn"
                                        icon="plus"
                                        style="height: 24px; padding: 0 4px"
                                    ></a-button>
                                </a-tooltip>
                            </template>
                            <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                                <template v-slot="{ row, seq }">
                                    <div>
                                        <i :class="!row.detail_inner_sn ? 'red-dot-add' : 'hide'"></i>
                                        <span>{{ seq }}</span>
                                    </div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="product_name"
                                title="产品"
                                width="15%"
                                :cell-render="{ name: 'product_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="10%"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="partB_name"
                                title="厂商/经销商"
                                width="10%"
                                :cell-render="{ name: 'partB_name' }"
                            ></vxe-table-column>
                            <vxe-table-column field="status" title="状态" width="8%">
                                <template v-slot="{ row }">
                                    <div>{{ row.status | formatStatus('planOrder') }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="quantity" title="数量" width="12%">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :disabled="isEditDisabled"
                                        :min="0.001"
                                        :formatter="
                                            (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')
                                        "
                                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                                        v-model="row.quantity"
                                        class="input"
                                        style="width: 100%"
                                        @change="updateFooterPlanModalEditFn"
                                    />
                                </template>
                            </vxe-table-column>
                            <!--<vxe-table-column-->
                            <!--    field="quantity_delivered"-->
                            <!--    title="已销售数量"-->
                            <!--    width="8%"-->
                            <!--&gt;-->
                            <!--    &lt;!&ndash; <template v-slot="{ row }">-->
                            <!--        - -->
                            <!--    </template> &ndash;&gt;-->
                            <!--    - -->
                            <!--</vxe-table-column>-->
                            <vxe-table-column field="unit_inner_sn" title="包装单位" width="12%">
                                <template v-slot="{ row }">
                                    <a-cascader
                                        :disabled="isEditDisabled"
                                        :allowClear="false"
                                        :options="row.unitList"
                                        v-model="row.unit_inner_sn"
                                        changeOnSelect
                                        :displayRender="displayUnitRender"
                                        @click.stop="clickUnitListFn(row)"
                                        @change="changeUnitListFn"
                                        placeholder=""
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="batch_number" title="批号" width="10%">
                                <template v-slot="{ row }">
                                    <a-input
                                        :disabled="isEditDisabled"
                                        v-model="row.batch_number"
                                        class="input"
                                        style="width: 100%"
                                        @change="updateFooterPlanModalEditBNFn"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_price" title="单价" width="8%" align="right">
                                <template v-slot="{ row }">
                                    <div>{{ unit_price_record(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="money" title="金额" width="12%" align="right">
                                <template v-slot="{ row }">
                                    <div>{{ money_record(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="rate" title="税率" width="12%">
                                <template v-slot="{ row }">
                                    <a-select
                                        :disabled="isSourceBuyerAudited ? false : isEditDisabled"
                                        v-model="row.rate"
                                        :default-value="0"
                                        @change="tableRateChangeFn"
                                    >
                                        <a-select-option :value="0"> 0% </a-select-option>
                                        <a-select-option :value="1"> 1% </a-select-option>
                                        <a-select-option :value="3"> 3% </a-select-option>
                                        <a-select-option :value="6"> 6% </a-select-option>
                                        <a-select-option :value="9"> 9% </a-select-option>
                                        <a-select-option :value="13"> 13% </a-select-option>
                                    </a-select>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax_unit_price" title="含税单价" width="10%" align="right">
                                <template v-slot="{ row }">
                                    <a-dropdown
                                        :disabled="isSourceBuyerAudited ? false : isEditDisabled"
                                        :trigger="['click']"
                                    >
                                        <a-input-number
                                            :min="0"
                                            :formatter="
                                                (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')
                                            "
                                            :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                            v-model="row.tax_unit_price"
                                            class="input"
                                            style="width: 100%"
                                            @change="updateFooterPlanModalFn(row)"
                                        />
                                        <a-menu v-if="row.unitPriceList && row.unitPriceList.length" slot="overlay">
                                            <a-menu-item
                                                v-for="(priceItem, priceIndex) in row.unitPriceList"
                                                :key="priceIndex"
                                                @click="changeUnitPriceFn(row, priceItem)"
                                            >
                                                <a href="javascript:;">{{ priceItem }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax_amount" title="含税金额" width="12%" align="right">
                                <template v-slot="{ row }">
                                    <div>{{ tax_amount_record(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax" title="税额" width="12%" align="right">
                                <template v-slot="{ row }">
                                    <div>{{ tax_record(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                v-if="
                                    !(
                                        planModalDatas.length <= 0 ||
                                        planModalForm.status === 2 ||
                                        planModalForm.status === 4
                                    )
                                "
                                field="action"
                                title="操作"
                                width="120"
                                :resizable="false"
                                fixed="right"
                            >
                                <template v-slot="{ row, seq }">
                                    <ul class="action-btn-list-wrap" style="position: relative">
                                        <li
                                            v-if="
                                                btnName === 'add' ||
                                                planModalForm.status === 0 ||
                                                planModalForm.status === 1
                                            "
                                        >
                                            <a-tooltip class="tooltip" placement="top" title="删除">
                                                <i
                                                    @click.stop="planModalActionBtnFn('remove', row, seq)"
                                                    class="yrt yrt-shanchu-copy"
                                                    :class="isEditDisabled ? 'disabled' : 'cancel'"
                                                ></i>
                                            </a-tooltip>
                                        </li>
                                        <li v-else-if="planModalForm.status === 0 || planModalForm.status === 3">
                                            <a-switch
                                                v-if="parseInt(row.status) !== 1"
                                                :disabled="activeTabsKey === tabTypes[1]"
                                                @click="planModalActionBtnFn('stop', row, seq)"
                                                :loading="row.loading"
                                                :checked="row.switch"
                                                size="small"
                                                checkedChildren="终止"
                                                unCheckedChildren="反终止"
                                                defaultChecked
                                                style="color: #faad14; width: 64px"
                                            />
                                        </li>
                                        <li v-else>-</li>
                                    </ul>
                                </template>
                            </vxe-table-column>
                            <template v-slot:empty>
                                <div v-if="isEditDisabled" class="dblclick-wrap">
                                    <span>暂无数据</span>
                                </div>
                                <div v-else @dblclick.stop="dblclickTableFn" class="dblclick-wrap">
                                    <span>双击添加产品</span>
                                </div>
                            </template>
                        </vxe-grid>
                    </div>
                </div>
                <ul class="order-modal-list">
                    <li>
                        <span class="label" style="width: auto">摘要</span>
                        <a-input
                            :disabled="isEditDisabled"
                            v-model="planModalForm.description"
                            placeholder=""
                            class="input"
                        />
                    </li>
                    <li>
                        <div class="item-wrap">
                            <div class="item">
                                <span class="label" style="width: auto">制单人</span>
                                <span>{{ planModalForm.creator_name || '-' }}</span>
                            </div>
                            <div class="item">
                                <span class="label" style="width: auto">生成时间</span>
                                <span>{{ planModalForm.create_time || '-' }}</span>
                            </div>
                        </div>
                        <div class="item-wrap">
                            <div class="item">
                                <span class="label" style="width: auto">审核人</span>
                                <span>{{ planModalForm.auditor_name || '-' }}</span>
                            </div>
                            <div class="item">
                                <span class="label" style="width: auto">审核时间</span>
                                <span>{{ planModalForm.audit_time || '-' }}</span>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 销售订单新增/修改区域结束 -->

        <!-- 销售单产品详情 -->
        <a-modal
            v-model="infoModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-sales-order-detail global-drag"
            :width="1200"
            :zIndex="3"
            :afterClose="afterClosePlanmodalFn"
        >
            <div
                v-globalDrag="{ el: 'modal-sales-order-detail' }"
                class="modal-header-wrap"
                @dblclick="modalFullscreen"
            >
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ infoModal.title }}
                </h6>
                <span
                    class="modal-fullscreen"
                    v-if="isFullscreen"
                    @click="modalFullscreen"
                    @mouseover="fullscreenMouseOver(false)"
                    @mouseout="fullscreenMouseOver(true)"
                >
                    <i data-v-4a953340="" aria-label="最小化" class="modal-fullscreen-icon anticon anticon-arrows-alt">
                        <svg
                            width="20px"
                            height="20px"
                            viewBox="0 0 256 256"
                            version="1.1"
                            xmlns="http://www.w3.org/2000/svg"
                            xmlns:xlink="http://www.w3.org/1999/xlink"
                        >
                            <title>还原</title>
                            <g id="还原" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                                <g
                                    id="编组"
                                    transform="translate(128.500000, 127.500000) scale(-1, 1) translate(-128.500000, -127.500000) translate(42.000000, 41.000000)"
                                    fill="#ffffff"
                                >
                                    <path
                                        d="M173,47 L173,173 L47,173 L47,47 L173,47 Z M159,62 L62,62 L62,159 L159,159 L159,62 Z"
                                        id="形状结合"
                                    ></path>
                                    <path
                                        d="M126,0 L126,52.672 L107.545,52.672 L107.545455,17.5454545 L17.5454545,17.5454545 L17.5454545,107.545455 L56.803,107.545 L56.803,126 L0,126 L0,0 L126,0 Z"
                                        id="形状结合"
                                    ></path>
                                </g>
                            </g>
                        </svg>
                    </i>
                </span>
                <span
                    class="modal-fullscreen"
                    v-else
                    @click="modalFullscreen"
                    @mouseover="fullscreenMouseOver(false)"
                    @mouseout="fullscreenMouseOver(true)"
                >
                    <i data-v-4a953340="" aria-label="最小化" class="modal-fullscreen-icon anticon anticon-arrows-alt">
                        <svg
                            width="20px"
                            height="20px"
                            viewBox="0 0 256 256"
                            version="1.1"
                            xmlns="http://www.w3.org/2000/svg"
                            xmlns:xlink="http://www.w3.org/1999/xlink"
                        >
                            <title>全屏</title>
                            <g id="全屏" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                                <path
                                    d="M214,41 L214,214 L41,214 L41,41 L214,41 Z M198,57 L57,57 L57,198 L198,198 L198,57 Z"
                                    id="形状结合"
                                    fill="#ffffff"
                                ></path>
                            </g>
                        </svg>
                    </i>
                </span>
            </div>
            <template slot="footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button @click="infoModalGroupBtnSendFn('print')" key="print"> 打印 </a-button>
                        <a-button @click="infoModalGroupBtnSendFn('exportExcel')" key="exportExcel">
                            导出Excel
                        </a-button>
                    </li>
                    <li>
                        <a-button @click="infoModalGroupBtnSendFn('cancel')" key="cancel"> 关闭 </a-button>
                    </li>
                </ul>
            </template>
            <div class="modal-form-input" style="height: 520px">
                <h6
                    style="
                        position: relative;
                        padding-bottom: 24px;
                        margin-bottom: 0;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    "
                >
                    <span style="font-size: 24px; color: #444">销售单</span>
                </h6>
                <ul class="top-list" style="'width:60%'">
                    <li class="item">
                        <span class="label"><em class="em red">*</em>收货单位</span>
                        <a-input
                            class="input"
                            type="text"
                            :disabled="true"
                            readonly="readonly"
                            v-model="infoAllData.organization_name"
                            @click="openChooseTb"
                            placeholder=""
                        />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>销售员</span>
                        <a-tree-select
                            :disabled="true"
                            treeNodeFilterProp="title"
                            searchPlaceholder=""
                            :showSearch="true"
                            :treeData="handlerNameList"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            v-model="infoAllData.seller_inner_sn"
                            @change="changeHandlerNameSnFn"
                            :allowClear="false"
                            class="input"
                            style="min-width: 170px"
                        >
                        </a-tree-select>
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>销售部门</span>
                        <a-tree-select
                            treeNodeFilterProp="title"
                            :showSearch="true"
                            :disabled="true"
                            :treeData="superiorDepartments"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            v-model="infoAllData.department_inner_sn"
                            @change="changeDepartNameSnFn"
                            :allowClear="false"
                            class="input"
                            style="min-width: 170px"
                        >
                        </a-tree-select>
                    </li>
                    <li class="item">
                        <span class="label" v-if="!infoModal.isadd">单据编号</span>
                        <a-input v-model="infoAllData.sl_sn" disabled="true" placeholder="" class="input" />
                    </li>
                </ul>
                <ul class="top-list" style="'width:60%'">
                    <li class="item">
                        <span class="label">采购方联系人</span>
                        <a-input
                            v-model="infoAllData.purchaser_linkman"
                            :disabled="true"
                            placeholder=""
                            class="input"
                        />
                    </li>
                    <li class="item">
                        <span class="label">联系电话</span>
                        <a-input v-model="infoAllData.purchaser_phone" :disabled="true" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label">销售方联系人</span>
                        <a-input v-model="infoAllData.seller_linkman" :disabled="true" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label">联系电话</span>
                        <a-input v-model="infoAllData.seller_phone" :disabled="true" placeholder="" class="input" />
                    </li>
                </ul>
                <div class="tab-table-wrap cur-overflow-hidden" style="padding-bottom: 0; margin-top: 0">
                    <div class="tableFather" style="height: 230px">
                        <vxe-grid
                            ref="xGreatSendSaleDetailTable"
                            size="small"
                            :data="infoData"
                            :loading="planModalPagination.loading"
                            height="auto"
                            border
                            resizable
                            auto-resize
                            show-overflow
                            show-header-overflow
                            highlight-current-row
                            align="center"
                            show-footer
                            :footer-method="footerMethodFnForSalesOrderDetail"
                            :footer-cell-class-name="footerCellClassNameFn"
                        >
                            <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                                <template v-slot="{ seq }">
                                    <div>
                                        <span>{{ seq }}</span>
                                    </div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="product_name"
                                title="产品"
                                width="20%"
                                align="center"
                                :cell-render="{ name: 'product_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="15%"
                                align="center"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="product_vendor_name"
                                title="厂商"
                                width="12%"
                                align="center"
                                :cell-render="{ name: 'product_vendor_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="quantity"
                                title="数量"
                                show-overflow="true"
                                showHeaderOverflow="true"
                                align="center"
                                width="12%"
                            >
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :disabled="true"
                                        :min="0.001"
                                        :formatter="
                                            (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')
                                        "
                                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                                        v-model="row.quantity"
                                        class="input"
                                        style="width: 100%"
                                        @change="updateFooterPlanModalFn"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_name" title="包装单位" width="12%">
                                <template v-slot="{ row }">
                                    <a-cascader
                                        :allowClear="false"
                                        :disabled="true"
                                        :options="row.unitList"
                                        v-model="row.unit_inner_sn"
                                        changeOnSelect
                                        :displayRender="displayUnitRender"
                                        @click.stop="clickUnitListFn(row)"
                                        @change="changeUnitListFn"
                                        placeholder=""
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="batch_number"
                                title="批号"
                                show-overflow="true"
                                showHeaderOverflow="true"
                                align="center"
                                width="12%"
                            >
                                <template v-slot="{ row }">
                                    <a-input
                                        :disabled="true"
                                        v-model="row.batch_number"
                                        class="input"
                                        style="width: 100%"
                                        @change="rushInput"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_price" title="单价" width="10%">
                                <template v-slot="{ row }">
                                    <div>{{ unit_price_record(row) }}</div>
                                </template>
                                <!-- <template v-slot="{ row }">
                                <a-input-number
                                    :min="0"
                                    :formatter="value => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                    :parser="value => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                    :disabled="true"
                                    v-model="row.unit_price"
                                    class="input"
                                    style="width:100%"
                                    @change="updateFooterPlanModalFn"
                                />
                            </template> -->
                            </vxe-table-column>
                            <vxe-table-column field="money" title="金额" align="center" width="12%">
                                <template v-slot="{ row }">
                                    <div>{{ money_record(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="rate" title="税率" align="center" width="12%">
                                <template v-slot="{ row }">
                                    <a-select :disabled="true" v-model="row.rate">
                                        <a-select-option :value="0"> 0% </a-select-option>
                                        <a-select-option :value="1"> 1% </a-select-option>
                                        <a-select-option :value="3"> 3% </a-select-option>
                                        <a-select-option :value="6"> 6% </a-select-option>
                                        <a-select-option :value="9"> 9% </a-select-option>
                                        <a-select-option :value="13"> 13% </a-select-option>
                                    </a-select>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax_unit_price" title="含税单价" align="center" width="10%">
                                <template v-slot="{ row }">
                                    <a-dropdown :disabled="isEditDisabled" :trigger="['click']">
                                        <a-input-number
                                            :min="0"
                                            :formatter="
                                                (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')
                                            "
                                            :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                            v-model="row.tax_unit_price"
                                            class="input"
                                            style="width: 100%"
                                            @change="updateFooterPlanModalFn(row)"
                                        />
                                        <a-menu v-if="row.unitPriceList && row.unitPriceList.length" slot="overlay">
                                            <a-menu-item
                                                v-for="(priceItem, priceIndex) in row.unitPriceList"
                                                :key="priceIndex"
                                                @click="changeUnitPriceFn(row, priceItem)"
                                            >
                                                <a href="javascript:;">{{ priceItem }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax_amount" title="含税金额" align="center" width="12%">
                                <template v-slot="{ row }">
                                    <div>{{ tax_amount_record(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax" title="税额" align="center" width="10%">
                                <template v-slot="{ row }">
                                    <div>{{ tax_record(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="health_care_sn" title="医保编号" align="center" width="150">
                                <template v-slot="{ row, seq }">
                                    <a-dropdown :disabled="isEditDisabled" :trigger="['click']">
                                        <a-input
                                            class="input"
                                            style="width: 100%"
                                            v-model="row.health_care_sn"
                                            @click="getProductSnAPIFn(true, row)"
                                        >
                                        </a-input>
                                        <a-menu
                                            v-if="healthAndSunSnModal.data.length"
                                            slot="overlay"
                                            style="maxheight: 200px"
                                        >
                                            <a-menu-item
                                                v-for="item in healthAndSunSnModal.data"
                                                :key="item.index"
                                                @click="healthCareSnChangeFn(item, seq)"
                                            >
                                                <a href="javascript:;">{{ item.sn }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="sun_sn" title="阳光流水号" align="center" width="150">
                                <template v-slot="{ row, seq }">
                                    <a-dropdown :disabled="isEditDisabled" :trigger="['click']">
                                        <a-input
                                            class="input"
                                            style="width: 100%"
                                            v-model="row.sun_sn"
                                            @click="getProductSnAPIFn(false, row)"
                                        >
                                        </a-input>
                                        <a-menu
                                            v-if="healthAndSunSnModal.data.length"
                                            slot="overlay"
                                            style="maxheight: 200px"
                                        >
                                            <a-menu-item
                                                v-for="item in healthAndSunSnModal.data"
                                                :key="item.index"
                                                @click="sunSnChangeFn(item, seq)"
                                            >
                                                <a href="javascript:;">{{ item.sn }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                v-if="false"
                                field="action"
                                title="操作"
                                width="80"
                                :resizable="false"
                                fixed="right"
                            >
                                <template v-slot="{ row, seq }">
                                    <ul class="action-btn-list-wrap" style="position: relative">
                                        <li>
                                            <a-tooltip class="tooltip" placement="top" title="删除">
                                                <i
                                                    @click.stop="planModalActionBtnSendFn('remove', row, seq)"
                                                    class="yrt yrt-shanchu-copy cancel"
                                                    style="cursor: pointer"
                                                ></i>
                                            </a-tooltip>
                                        </li>
                                    </ul>
                                </template>
                            </vxe-table-column>
                            <template v-slot:empty>
                                <div v-if="isEditDisabled" class="dblclick-wrap">
                                    <span>暂无数据</span>
                                </div>
                            </template>
                        </vxe-grid>
                    </div>
                </div>
                <ul class="order-modal-list">
                    <li>
                        <span class="label" style="width: 66px">摘要</span>
                        <a-input v-model="infoAllData.description" placeholder="" :disabled="true" class="input" />
                    </li>
                    <li>
                        <div class="item-wrap">
                            <div class="item">
                                <span class="label" style="width: auto">制单人</span>
                                <span>{{ infoAllData.making_people_name || '-' }}</span>
                            </div>
                            <div class="item">
                                <span class="label" style="width: auto">生成时间</span>
                                <span>{{ infoAllData.billing_time || '-' }}</span>
                            </div>
                        </div>
                        <div class="item-wrap">
                            <div class="item">
                                <span class="label" style="width: auto">审核人</span>
                                <span>{{ infoAllData.auditor_name || '-' }}</span>
                            </div>
                            <div class="item" style="display: none">
                                <span class="label" style="width: auto">审核时间</span>
                                <span>{{ infoAllData.audit_time || '-' }}</span>
                            </div>
                            <div class="item">
                                <span class="label" style="width: auto">收款日期</span>
                                <span>
                                    <a-date-picker
                                        v-model="infoAllData.collection_date"
                                        class="input"
                                        :disabled="true"
                                        placeholder=""
                                        format="YYYY-MM-DD"
                                    />
                                </span>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 销售单产品详情 -->

        <!-- 待处理tab选项卡下方的 点击生成销售单区域开始 -->
        <a-modal
            v-model="planSendModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-add-send global-drag"
            :width="1200"
            :zIndex="3"
            :afterClose="afterClosePlanmodalFn"
        >
            <div v-globalDrag="{ el: 'modal-add-send' }" class="modal-header-wrap" @dblclick="modalFullscreen">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ infoModal.title }}
                </h6>
                <span
                    class="modal-fullscreen"
                    v-if="isFullscreen"
                    @click="modalFullscreen"
                    @mouseover="fullscreenMouseOver(false)"
                    @mouseout="fullscreenMouseOver(true)"
                >
                    <i data-v-4a953340="" aria-label="最小化" class="modal-fullscreen-icon anticon anticon-arrows-alt">
                        <svg
                            width="20px"
                            height="20px"
                            viewBox="0 0 256 256"
                            version="1.1"
                            xmlns="http://www.w3.org/2000/svg"
                            xmlns:xlink="http://www.w3.org/1999/xlink"
                        >
                            <title>还原</title>
                            <g id="还原" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                                <g
                                    id="编组"
                                    transform="translate(128.500000, 127.500000) scale(-1, 1) translate(-128.500000, -127.500000) translate(42.000000, 41.000000)"
                                    fill="#ffffff"
                                >
                                    <path
                                        d="M173,47 L173,173 L47,173 L47,47 L173,47 Z M159,62 L62,62 L62,159 L159,159 L159,62 Z"
                                        id="形状结合"
                                    ></path>
                                    <path
                                        d="M126,0 L126,52.672 L107.545,52.672 L107.545455,17.5454545 L17.5454545,17.5454545 L17.5454545,107.545455 L56.803,107.545 L56.803,126 L0,126 L0,0 L126,0 Z"
                                        id="形状结合"
                                    ></path>
                                </g>
                            </g>
                        </svg>
                    </i>
                </span>
                <span
                    class="modal-fullscreen"
                    v-else
                    @click="modalFullscreen"
                    @mouseover="fullscreenMouseOver(false)"
                    @mouseout="fullscreenMouseOver(true)"
                >
                    <i data-v-4a953340="" aria-label="最小化" class="modal-fullscreen-icon anticon anticon-arrows-alt">
                        <svg
                            width="20px"
                            height="20px"
                            viewBox="0 0 256 256"
                            version="1.1"
                            xmlns="http://www.w3.org/2000/svg"
                            xmlns:xlink="http://www.w3.org/1999/xlink"
                        >
                            <title>全屏</title>
                            <g id="全屏" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                                <path
                                    d="M214,41 L214,214 L41,214 L41,41 L214,41 Z M198,57 L57,57 L57,198 L198,198 L198,57 Z"
                                    id="形状结合"
                                    fill="#ffffff"
                                ></path>
                            </g>
                        </svg>
                    </i>
                </span>
            </div>
            <template slot="footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button @click="handlePrint('xGreatSendSaleTable')" key="print"> 打印 </a-button>
                        <a-button @click="exportExcel('xGreatSendSaleTable', true)" type="primary">导出Excel</a-button>
                    </li>
                    <li>
                        <a-button @click="planModalGroupBtnSendFn('cancel')" key="cancel">
                            {{ planModalTextComputed }}
                        </a-button>
                        <!--                        <a-button v-show="btnNameSendOrderSale === 'great-send'"-->
                        <!--                                  @click="planModalGroupBtnSendFn('saveDraft')"-->
                        <!--                                  key="saveDraft"-->
                        <!--                                  type="primary"-->
                        <!--                                  :loading="planSendModal.loadingSaveDraft">-->
                        <!--                            存草稿-->
                        <!--                        </a-button>-->
                        <!--                        <a-button v-show="btnNameSendOrderSale === 'great-send'"-->
                        <!--                                  @click="planModalGroupBtnSendFn('save')"-->
                        <!--                                  key="submit"-->
                        <!--                                  type="primary"-->
                        <!--                                  :loading="planSendModal.loadingSave">-->
                        <!--                            保存-->
                        <!--                        </a-button> v-show="btnNameSendOrderSale === 'great-send-audit'"-->
                        <a-button
                            @click="planModalGroupBtnSendFn('savePo')"
                            key="submitPo"
                            type="primary"
                            :loading="planSendModal.loadingSave"
                        >
                            保存
                        </a-button>
                        <a-button
                            @click="planModalGroupBtnSendFn('audit')"
                            key="audit"
                            type="primary"
                            :loading="planSendModal.loadingAudit"
                        >
                            审核
                        </a-button>
                    </li>
                </ul>
            </template>
            <div class="modal-form-input" style="height: 520px">
                <h6
                    style="
                        position: relative;
                        padding-bottom: 24px;
                        margin-bottom: 0;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    "
                >
                    <span style="font-size: 24px; color: #444">销售单</span>
                    <!-- <div v-if="btnName !== 'add'" style="position:absolute;right:0;display:flex;">
                        <span class="label"
                              style="font-size:14px;color:rgba(0, 0, 0, 0.65)"><em class="em red">*</em>单据编号</span>
                        <a-input v-model="planModalForm.so_sn"
                                 placeholder=""
                                 disabled
                                 class="input" />
                    </div> -->
                </h6>
                <ul class="top-list" style="'width:60%'">
                    <li class="item">
                        <span class="label"><em class="em red">*</em>收货单位</span>
                        <a-input
                            class="input"
                            type="text"
                            disabled
                            readonly="readonly"
                            v-model="planModalOrgSn"
                            @click="openChooseTb"
                            placeholder=""
                        />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>销售员</span>
                        <a-tree-select
                            treeNodeFilterProp="title"
                            searchPlaceholder=""
                            :showSearch="true"
                            :treeData="handlerNameList"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            v-model="planModalForm.seller_inner_sn"
                            @change="changeHandlerNameSnFn"
                            :allowClear="false"
                            class="input"
                            style="min-width: 170px"
                        >
                        </a-tree-select>
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>销售部门</span>
                        <a-tree-select
                            treeNodeFilterProp="title"
                            :showSearch="true"
                            :treeData="superiorDepartments"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            v-model="planModalForm.department_id"
                            @change="changeDepartNameSnFn"
                            :allowClear="false"
                            class="input"
                            style="min-width: 170px"
                        >
                        </a-tree-select>
                    </li>
                    <li class="item">
                        <span class="label">单据编号</span>
                        <a-input disabled="true" placeholder="" class="input" />
                        <!-- v-model="planModalForm.so_sn" -->
                    </li>
                </ul>
                <ul class="top-list" style="'width:60%'">
                    <li class="item">
                        <span class="label">采购方联系人</span>
                        <a-input v-model="planModalForm.purchaser_linkman" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label">联系电话</span>
                        <a-input v-model="planModalForm.purchaser_phone" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label">销售方联系人</span>
                        <a-input v-model="planModalForm.seller_linkman" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label">联系电话</span>
                        <a-input v-model="planModalForm.seller_phone" placeholder="" class="input" />
                    </li>
                </ul>
                <div class="tab-table-wrap cur-overflow-hidden" style="padding-bottom: 0; margin-top: 0">
                    <div class="tableFather" style="height: 230px">
                        <vxe-grid
                            ref="xGreatSendSaleTable"
                            size="small"
                            :data="planModalDatas"
                            :loading="planModalPagination.loading"
                            height="auto"
                            border
                            resizable
                            auto-resize
                            show-overflow
                            show-header-overflow
                            highlight-current-row
                            @cell-click="cellGreatSendSaleClickFn"
                            @cell-dblclick="cellGreatSendSaleDblclickFn"
                            align="center"
                            show-footer
                            :footer-method="footerMethodFn"
                            :footer-cell-class-name="footerCellClassNameFn"
                        >
                            <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                                <template v-slot="{ seq }">
                                    <div>
                                        <span>{{ seq }}</span>
                                    </div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="product_name"
                                title="产品"
                                width="20%"
                                :cell-render="{ name: 'product_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="15%"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="product_vendor_name"
                                title="厂商"
                                width="12%"
                                :cell-render="{ name: 'product_vendor_name' }"
                            ></vxe-table-column>
                            <vxe-table-column field="quantity" title="数量" width="12%">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        class="input"
                                        style="width: 100%"
                                        :min="0.001"
                                        :formatter="
                                            (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')
                                        "
                                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                                        v-model="row.quantity"
                                        @change="updateFooterPlanModalFn"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="status" title="状态" width="8%">
                                <template v-slot="{ row }">
                                    <div>{{ row.status | formatStatus('planOrder') }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_inner_sn" title="包装单位" width="12%">
                                <template v-slot="{ row }">
                                    <a-cascader
                                        :allowClear="false"
                                        :options="row.unitList"
                                        v-model="row.unit_inner_sn"
                                        changeOnSelect
                                        :displayRender="displayUnitRender"
                                        @click.stop="clickUnitListFn(row)"
                                        @change="changeUnitListFn"
                                        placeholder=""
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="batch_number" title="批号" width="10%">
                                <template v-slot="{ row }">
                                    <a-input
                                        v-model="row.batch_number"
                                        class="input"
                                        style="width: 100%"
                                        @change="updateFooterPlanModalFn"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_price" title="单价" width="12%">
                                <template v-slot="{ row }">
                                    <div>{{ unit_price_record(row) }}</div>
                                </template>
                                <!-- <template v-slot="{ row }">
                                <a-input-number
                                    :min="0"
                                    :formatter="value => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                    :parser="value => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                    v-model="row.unit_price"
                                    class="input"
                                    style="width:100%"
                                    @change="updateFooterPlanModalFn"
                                />
                            </template> -->
                            </vxe-table-column>
                            <vxe-table-column field="money" title="金额" width="8%">
                                <template v-slot="{ row }">
                                    <div>{{ money_record(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="rate" title="税率" width="12%">
                                <template v-slot="{ row }">
                                    <a-select v-model="row.rate" @change="updateFooterPlanModalFn">
                                        <a-select-option :value="0"> 0% </a-select-option>
                                        <a-select-option :value="1"> 1% </a-select-option>
                                        <a-select-option :value="3"> 3% </a-select-option>
                                        <a-select-option :value="6"> 6% </a-select-option>
                                        <a-select-option :value="9"> 9% </a-select-option>
                                        <a-select-option :value="13"> 13% </a-select-option>
                                    </a-select>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax_unit_price" title="含税单价" width="10%">
                                <template v-slot="{ row }">
                                    <a-dropdown :trigger="['click']">
                                        <a-input-number
                                            :min="0"
                                            :formatter="
                                                (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')
                                            "
                                            :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                            v-model="row.tax_unit_price"
                                            class="input"
                                            style="width: 100%"
                                            @change="updateFooterPlanModalFn(row)"
                                        />
                                        <a-menu v-if="row.unitPriceList && row.unitPriceList.length" slot="overlay">
                                            <a-menu-item
                                                v-for="(priceItem, priceIndex) in row.unitPriceList"
                                                :key="priceIndex"
                                                @click="changeUnitPriceFn(row, priceItem)"
                                            >
                                                <a href="javascript:;">{{ priceItem }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax_amount" title="含税金额" width="10%">
                                <template v-slot="{ row }">
                                    <div>{{ tax_amount_record(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax" title="税额" width="8%">
                                <template v-slot="{ row }">
                                    <div>{{ tax_record(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="health_care_sn" title="医保编号" align="center" width="150">
                                <template v-slot="{ row, seq }">
                                    <a-dropdown :disabled="row.product_type === 0" :trigger="['click']">
                                        <a-input
                                            class="input"
                                            style="width: 100%"
                                            v-model="row.health_care_sn"
                                            @click="getProductSnAPIFn(true, row)"
                                        >
                                        </a-input>
                                        <a-menu
                                            v-if="healthAndSunSnModal.data.length"
                                            slot="overlay"
                                            style="maxheight: 200px"
                                        >
                                            <a-menu-item
                                                v-for="item in healthAndSunSnModal.data"
                                                :key="item.index"
                                                @click="healthCareSnChangeFn(item, seq)"
                                            >
                                                <a href="javascript:;">{{ item.sn }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="sun_sn" title="阳光流水号" align="center" width="150">
                                <template v-slot="{ row, seq }">
                                    <a-dropdown :disabled="row.product_type === 0" :trigger="['click']">
                                        <a-input
                                            class="input"
                                            style="width: 100%"
                                            v-model="row.sun_sn"
                                            @click="getProductSnAPIFn(false, row)"
                                        >
                                        </a-input>
                                        <a-menu
                                            v-if="healthAndSunSnModal.data.length"
                                            slot="overlay"
                                            style="maxheight: 200px"
                                        >
                                            <a-menu-item
                                                v-for="item in healthAndSunSnModal.data"
                                                :key="item.index"
                                                @click="sunSnChangeFn(item, seq)"
                                            >
                                                <a href="javascript:;">{{ item.sn }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                v-if="!(planModalDatas.length <= 0 || planModalForm.status === 2)"
                                field="action"
                                title="操作"
                                width="80"
                                :resizable="false"
                                fixed="right"
                            >
                                <template v-slot="{ row, seq }">
                                    <ul class="action-btn-list-wrap" style="position: relative">
                                        <li>
                                            <a-tooltip class="tooltip" placement="top" title="删除">
                                                <i
                                                    @click.stop="planModalActionBtnSendFn('remove', row, seq)"
                                                    class="yrt yrt-shanchu-copy cancel"
                                                    style="cursor: pointer"
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
                </div>
                <ul class="order-modal-list">
                    <!-- <li>
                        <div style="display:flex;align-items:center;margin-right:16px;display: none">
                            <span class="label"
                                  style="width:auto">送货日期</span>
                            <a-date-picker
                                v-model="planModalForm.delivery_date"
                                class="input"
                                placeholder=""
                                format="YYYY-MM-DD"
                            />
                        </div>
                        <div style="display:flex;align-items:center;flex:1;display: none">
                            <span class="label"
                                  style="width:auto">送货地址</span>
                            <a-input
                                v-model="planModalForm.delivery_address"
                                placeholder=""
                                class="input" />
                        </div>
                    </li> -->
                    <li>
                        <span class="label" style="width: 66px">摘要</span>
                        <a-input v-model="planModalForm.description" placeholder="" class="input" />
                    </li>
                    <li>
                        <div class="item-wrap">
                            <div class="item">
                                <span class="label" style="width: auto">制单人</span>
                                <span>{{ planModalForm.creator_name || '-' }}</span>
                            </div>
                            <div class="item">
                                <span class="label" style="width: auto">生成时间</span>
                                <span>{{ planModalForm.create_time || '-' }}</span>
                            </div>
                        </div>
                        <div class="item-wrap">
                            <div class="item">
                                <span class="label" style="width: auto">收款日期</span>
                                <span>
                                    <a-date-picker
                                        v-model="planModalForm.collection_date"
                                        class="input"
                                        placeholder=""
                                        format="YYYY-MM-DD"
                                    />
                                </span>
                            </div>
                            <!-- <div class="item">
                                <span class="label"
                                      style="width:auto">审核人</span>
                                <span>{{ planModalForm.auditor_name || '-' }}</span>
                            </div>
                            <div class="item">
                                <span class="label"
                                      style="width:auto">审核时间</span>
                                <span>{{ planModalForm.audit_time || '-' }}</span>
                            </div> -->
                        </div>
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 待处理tab选项卡下方的 点击生成销售单区域结束 -->

        <!-- 待处理tab选项卡下方的 点击生成采购计划后的弹框区域开始 -->
        <a-modal
            v-model="greatSendModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-great-send global-drag"
            :width="1100"
            :zIndex="3"
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
                        <a-button key="print" @click="handlePrint('xGreatSendPlanTable')"> 打印 </a-button>
                        <a-button @click="exportExcelForPurchase('xGreatSendPlanTable')" type="primary"
                            >导出Excel</a-button
                        >
                    </li>
                    <li>
                        <div>
                            <a-button key="close" @click="greatSendModalGroupBtnFn('cancel')"> 取消 </a-button>
                            <a-button
                                key="great-send"
                                type="primary"
                                :loading="greatSendModal.loadingSend"
                                @click="greatSendModalGroupBtnFn('great-save-plan')"
                            >
                                保存
                            </a-button>
                        </div>
                    </li>
                </ul>
            </template>
            <div>
                <div class="modal-form-input for-single">
                    <h6 class="title">{{ computedGrantSingleTitle }}</h6>
                    <ul class="top-list">
                        <li>
                            <div class="item">
                                <span class="label"><em class="em red">*</em>部门</span>
                                <a-tree-select
                                    treeNodeFilterProp="title"
                                    :showSearch="true"
                                    :treeData="superiorDepartments"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    treeDefaultExpandAll
                                    v-model="editInfoFormObj.department_inner_sn"
                                    @change="changeDepartNameSnPlanFn"
                                    :allowClear="false"
                                    class="input"
                                    style="min-width: 170px"
                                >
                                </a-tree-select>
                            </div>
                        </li>
                    </ul>
                    <div
                        class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                        style="position: static; height: 289px"
                    >
                        <vxe-table
                            ref="xGreatSendPlanTable"
                            size="small"
                            :height="264 - ipadHeight"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            @cell-click="cellClickGreatSendPlanFn"
                            @cell-dblclick="cellDblclickGreatSendPlanFn"
                            align="center"
                            :data="greatSendDatas"
                            :loading="greatSendPagination.loading"
                            show-overflow
                            show-header-overflow
                            showHeader
                        >
                            <vxe-table-column
                                field="index"
                                type="index"
                                title="NO."
                                width="60"
                                :resizable="false"
                                fixed="left"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="product_name"
                                title="产品"
                                width="18%"
                                :cell-render="{ name: 'product_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="12%"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="product_vendor_name"
                                title="厂商/经销商"
                                width="12%"
                                :cell-render="{ name: 'product_vendor_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="batch_number"
                                title="批号"
                                width="10%"
                                :cell-render="{ name: 'batch_number' }"
                            ></vxe-table-column>
                            <vxe-table-column field="approval_volumes" title="数量" width="12%">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :formatter="
                                            (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')
                                        "
                                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                                        v-model="row.approval_volumes"
                                        @blur="clickUnitPlanListFn(row)"
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit" title="包装单位" width="12%">
                                <template v-slot="{ row }">
                                    <a-cascader
                                        :allowClear="false"
                                        :options="row.unitList"
                                        v-model="row.unit_inner_sn_new"
                                        changeOnSelect
                                        :displayRender="displayUnitRender"
                                        @click.stop="clickUnitPlanListFn(row, 'unit')"
                                        @change="changePlanUnitListFn"
                                        placeholder=""
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="packge_multiple_num" title="采购合计" width="12%">
                                <template v-slot="{ row }">
                                    {{ row.packge_multiple_num }}
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="action" title="操作" width="80" :resizable="false" fixed="right">
                                <template v-slot="{ row, seq }">
                                    <ul class="action-btn-list-wrap" style="position: relative">
                                        <li>
                                            <a-tooltip
                                                class="tooltip"
                                                placement="top"
                                                title="删除"
                                                :getPopupContainer="getPopupContainer"
                                            >
                                                <i
                                                    @click.stop="applyDetailGroupBtnFn('remove', row, seq)"
                                                    class="yrt yrt-shanchu-copy cancel"
                                                ></i>
                                            </a-tooltip>
                                        </li>
                                    </ul>
                                </template>
                            </vxe-table-column>
                        </vxe-table>
                    </div>
                    <ul>
                        <li class="item">
                            <span class="label" style="width: auto">摘要</span>
                            <a-input v-model="editInfoFormObj.description" placeholder="" class="input" />
                        </li>
                        <li class="item">
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
        <!-- 待处理tab选项卡下方的 点击生成采购计划后的弹框区域结束 -->

        <!-- 添加产品区域开始 -->
        <a-modal
            :title="addProductMd.title"
            v-model="addProductMd.alert"
            :maskClosable="false"
            :mask="false"
            :width="1000"
            :centered="true"
            @cancel="addProductMd.alert = false"
            class="modal-form-input-scoped global-drag add_product"
        >
            <div v-globalDrag="{ el: 'add_product' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addProductMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="addProductMd.alert = false">关闭</a-button>
                <a-button :loading="addProductMd.loading" @click="addProductFn" type="primary">添加</a-button>
            </template>
            <add-product ref="chooseProMd" @returnData="getProReturn" :partB="partC"></add-product>
        </a-modal>
        <!-- 添加产品区域结束 -->

        <!-- 选择相关机构区域开始 -->
        <a-modal
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            :zIndex="3"
            @cancel="chooseOrgMd.alert = false"
            class="scoped modal-form-input-scoped global-drag choose_org_md"
        >
            <div v-globalDrag="{ el: 'choose_org_md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <div style="margin-top: 56px">
                <chooseOrgTab
                    ref="chooseOrgMd"
                    @chooseOrg="chooseOrgFn"
                    :partType="1"
                    :pageTypeName="pageTypeName"
                ></chooseOrgTab>
            </div>
        </a-modal>
        <!-- 选择相关机构区域结束 -->
    </div>
</template>

<script>
import moment from 'moment';
import { mapState } from 'vuex';
import addProduct from '@/components/addProduct';
import chooseOrgTab from '@/components/chooseOrgTab';
import {
    orgDeptTopTreeAPI,
    platformUnitMinAPI,
    dealerUnitMinAPI,
    platformUnitTree,
    platformUnitParents,
    dealerUnitTreeAPI,
    dealerUnitParentsAPI,
    orderSaleListAPI,
    orderHistorySaleListAPI,
    orderSaleAddAPI,
    orderSaleRecordListAPI,
    orderSaleEditAPI,
    orderSaleConfirmAPI,
    // orderOrgListAPI,
    orderSaleInfoAPI,
    orderDelAPI,
    orderStopAPI,
    orderConfirmAPI,
    orderSaleBuildAPI,
    orderSaleDetailBuildAPI,
    orderSaleDetailAPI,
    orderDetailDelAPI,
    orderDetailStopAPI,
    orderDetailUnstopAPI,
    orgEmpDropdownListAPI,
    orderSaleCreatplanAPI,
    saleRecordInfoAPI,
    contractGetOrderPriceAPI,
    productSnListAPI
} from '@/service/pageAjax';
import { BillsType } from '@/config/constants';

export default {
    name: 'SuppliesClaim',
    data() {
        return {
            tabTop: 300,
            tabDown: 300,
            tabTopHand: 300,
            tabDownHand: 300,
            systemType: JSON.parse(this.$cookie.get('userSystemType')), // 相关机构类型 0-平台、 1-医院 、 2-经销商、 3-厂商
            employeeInfo: JSON.parse(this.$cookie.get('EmployeeInfo')),
            tabTypes: ['waitApply', 'haveClaimed'], // 待处理, 已处理
            activeTabsKey: 'waitApply',
            isActiveTabsKey: false,
            suppliesClaimColumns: [
                {
                    title: '单据编号',
                    dataIndex: 'so_sn',
                    key: 'so_sn',
                    width: '25%',
                    align: 'center'
                },
                {
                    title: '收货单位',
                    dataIndex: 'partA_name',
                    key: 'partA_name',
                    width: '23%',
                    align: 'center'
                },
                {
                    title: '期望到货日期',
                    dataIndex: 'expect_aog_date',
                    key: 'expect_aog_date',
                    width: '22%',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    width: '10%',
                    align: 'center'
                },
                {
                    title: '单据来源',
                    dataIndex: 'create_organization_type_text',
                    key: 'create_organization_type_text',
                    width: '20%',
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
            suppliesClaimHistoryDatas: [],
            suppliesColumns: [],
            suppliesColumnsBak: [
                { type: 'checkbox', fixed: 'left', width: '5%' },
                {
                    field: 'product_name',
                    title: '产品',
                    width: '16%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'specification',
                    title: '规格型号',
                    width: '11%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'product_vendor_name',
                    title: '厂商',
                    width: '14%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'batch_number',
                    title: '批号',
                    width: '15%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'quantity_new',
                    title: '订购数量',
                    width: '10%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'sold_quantity_new',
                    title: '已售数量',
                    width: '10%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'stay_quantity_new',
                    title: '待售数量',
                    width: '10%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'all_quantity_new',
                    title: '总库存',
                    width: '12%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'unlock_inventory_new',
                    title: '可用库存',
                    width: '12%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'min_unit_lock_inventory_new',
                    title: '锁定库存',
                    width: '12%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'inventory',
                    title: '库存',
                    width: '6%',
                    visible: false,
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'status_text',
                    title: '状态',
                    width: '10%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'unit_price',
                    title: '单价',
                    width: '10%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                // {field: 'discount', title: '折扣', width: '6%', showOverflow: true, showHeaderOverflow: true, align: 'center'},
                {
                    field: 'rate',
                    title: '税率',
                    width: '6%',
                    tooltip: '%',
                    slots: {
                        default: ({ row }) => {
                            return [<span>{row.rate}%</span>];
                        }
                    }
                },
                {
                    field: 'action',
                    title: '操作',
                    width: '80',
                    resizable: false,
                    fixed: 'right',
                    slots: {
                        default: ({ row }) => {
                            const status = row.status;
                            // 已完全处理和已终止的产品不显示终止按钮
                            if (status === 3 || status === 1) {
                                return [];
                            } else {
                                return [
                                    <a-tooltip class="tooltip" placement="top" title="终止" v-if={row.status !== 3}>
                                        <i
                                            class="yrt yrt-zhongzhi-gaoduan cancel"
                                            style="color:#F5222D;margin-left: 14px; font-size:18px"
                                            onClick={() => this.applyStopBtnFn('apply-stop-wrap-details', row)}
                                        >
                                            {' '}
                                        </i>
                                    </a-tooltip>
                                ];
                            }
                        }
                    }
                }
            ],
            suppliesColumnsBakPro: [
                { type: 'checkbox', fixed: 'left', width: '5%' },
                {
                    field: 'product_name',
                    title: '产品',
                    width: '16%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'specification',
                    title: '规格型号',
                    width: '11%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'product_vendor_name',
                    title: '厂商',
                    width: '14%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'batch_number',
                    title: '批号',
                    width: '15%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'quantity_new',
                    title: '订购数量',
                    width: '10%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'sold_quantity_new',
                    title: '已售数量',
                    width: '10%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'stay_quantity_new',
                    title: '待售数量',
                    width: '10%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'inventory',
                    title: '库存',
                    width: '6%',
                    visible: false,
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'status_text',
                    title: '状态',
                    width: '10%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    field: 'unit_price',
                    title: '单价',
                    width: '10%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                // {field: 'discount', title: '折扣', width: '6%', showOverflow: true, showHeaderOverflow: true, align: 'center'},
                {
                    field: 'rate',
                    title: '税率',
                    width: '6%',
                    tooltip: '%',
                    slots: {
                        default: ({ row }) => {
                            return [<span>{row.rate}%</span>];
                        }
                    }
                },
                {
                    field: 'action',
                    title: '操作',
                    width: '80',
                    resizable: false,
                    fixed: 'right',
                    slots: {
                        default: ({ row }) => {
                            if (row.status === 3) {
                                return [];
                            } else {
                                return [
                                    <a-tooltip class="tooltip" placement="top" title="终止" v-if={row.status !== 3}>
                                        <i
                                            class="yrt yrt-zhongzhi-gaoduan cancel"
                                            style="color:#F5222D;margin-left: 14px; font-size:18px"
                                            onClick={() => this.applyStopBtnFn('apply-stop-wrap-details', row)}
                                        >
                                            {' '}
                                        </i>
                                    </a-tooltip>
                                ];
                            }
                        }
                    }
                }
            ],
            createOrganizationType: ['买方', '自建'],
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
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    width: '11%',
                    align: 'center'
                },
                {
                    title: '厂商',
                    dataIndex: 'product_vendor_name',
                    key: 'product_vendor_name',
                    width: '12%',
                    align: 'center'
                },
                {
                    title: '订购数量',
                    dataIndex: 'quantity_new',
                    key: 'quantity_new',
                    width: '12%',
                    align: 'center'
                },
                {
                    title: '已销售数量',
                    dataIndex: 'sold_quantity',
                    key: 'sold_quantity',
                    width: '11%',
                    align: 'center'
                },
                {
                    title: '库存',
                    dataIndex: 'inventory',
                    key: 'inventory',
                    width: '6%',
                    align: 'center'
                },
                // {
                //     title: '包装单位',
                //     dataIndex: 'unit',
                //     key: 'unit',
                //     width: '10%',
                //     align: 'center'
                // },
                {
                    title: '状态',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    width: '8%',
                    align: 'center'
                },
                {
                    title: '含税单价',
                    dataIndex: 'unit_price',
                    key: 'unit_price',
                    width: '7%',
                    align: 'center'
                },
                // {
                //     title: '折扣',
                //     dataIndex: 'discount',
                //     key: 'discount',
                //     width: '6%',
                //     align: 'center'
                // },
                {
                    title: '税率',
                    dataIndex: 'rate',
                    key: 'rate',
                    width: '6%',
                    align: 'center'
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    key: 'action',
                    width: '6%',
                    align: 'center',
                    scopedSlots: { customRender: 'stop' }
                }
            ],
            // 厂商端表格暂时不显示库存字段的对应头部
            suppliesClaimBotColumnsNoInventory: [
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    width: '16%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    width: '11%',
                    align: 'center'
                },
                {
                    title: '厂商',
                    dataIndex: 'product_vendor_name',
                    key: 'product_vendor_name',
                    width: '14%',
                    align: 'center'
                },
                {
                    title: '订购数量',
                    dataIndex: 'quantity_new',
                    key: 'quantity_new',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '已销售数量',
                    dataIndex: 'sold_quantity',
                    key: 'sold_quantity',
                    width: '11%',
                    align: 'center'
                },
                {
                    title: '库存',
                    dataIndex: 'inventory',
                    key: 'inventory',
                    width: '6%',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    width: '8%',
                    align: 'center'
                },
                {
                    title: '含税单价',
                    dataIndex: 'unit_price',
                    key: 'unit_price',
                    width: '7%',
                    align: 'center'
                },
                // {
                //     title: '折扣',
                //     dataIndex: 'discount',
                //     key: 'discount',
                //     width: '6%',
                //     align: 'center'
                // },
                {
                    title: '税率',
                    dataIndex: 'rate',
                    key: 'rate',
                    width: '6%',
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
            sourceDocuments: ['买方', '自建'], // 销售订单外层待处理/已处理列表中的单据来源：0-买方、 1-自建
            suppliesClaimHistoryBotColumns: [
                {
                    title: '销售单编号',
                    dataIndex: 'sl_sn',
                    key: 'sl_sn',
                    width: '25%',
                    align: 'center'
                },
                {
                    title: '销售员',
                    dataIndex: 'seller_name',
                    key: 'seller_name',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '销售部门',
                    dataIndex: 'department_name',
                    key: 'department_name',
                    width: '18%',
                    align: 'center'
                },
                {
                    title: '审核人',
                    dataIndex: 'auditor_name',
                    key: 'auditor_name',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    width: '10%',
                    align: 'center'
                },
                {
                    title: '开单时间',
                    dataIndex: 'billing_time',
                    key: 'billing_time',
                    width: '17%',
                    align: 'center'
                }
            ],
            suppliesClaimBotAllDatas: [],
            suppliesClaimHistoryBotDatas: [],
            saleListStatus: [
                '草稿',
                '正式',
                '已审核',
                '已过账',
                '已生成出库单',
                '已出库',
                '已配送',
                '已送达',
                '已收货'
            ], // 0-草稿 1-正式 2-已审核 3-已过账
            currentTrHistoryBotObj: {},
            suppliesClaimHistoryBotPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            orderSaleDetailStatus: ['处理中', '已完全处理', '已计划采购', '已终止处理'],
            auditStatus: ['草稿', '申领', '部分发放', '全部发放', '申领终止'], // 医院领用单状态(status): 0-草稿 1-申领 2-部分发放 3-全部发放(自然终止) 4-申领终止(人为终止)
            // orderSaleListStatus: ['草稿', '正式', '已审核', '已确认', '已终止', '已完结', '(确认)未通过'],
            orderSaleListStatus: ['草稿', '正式', '已审核', '已确认', '已终止', '已完结'],
            currentTrSalesObj: {},
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            currentTrgreatSendObj: {},
            searchTables: {
                partB_id: '',
                status: [],
                partA_name: '',
                order_sn: ''
            }, // 表格的搜索并向后端请求字段
            searchBotTables: {
                warehouse_inner_sn: '',
                warehouse_inner_snBak: '',
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
            applySendStatus: ['可发放', '已出库', '已领取'],
            greatSendColumns: [
                {
                    title: '',
                    dataIndex: 'index',
                    key: 'index',
                    width: '5%',
                    align: 'center',
                    scopedSlots: { customRender: 'index' }
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    width: '13%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    width: '12%',
                    align: 'center'
                },
                {
                    title: '厂商/经销商',
                    dataIndex: 'product_vendor_name',
                    key: 'product_vendor_name',
                    width: '12%',
                    align: 'center'
                },
                {
                    title: '还需发放量',
                    dataIndex: 'still_need_volumes',
                    key: 'still_need_volumes',
                    width: '10%',
                    align: 'center'
                },
                {
                    title: '库存',
                    dataIndex: 'inventory_new',
                    key: 'inventory_new',
                    width: '8%',
                    align: 'center'
                },
                {
                    title: '批准发放量',
                    dataIndex: 'approval_volumes',
                    key: 'approval_volumes',
                    width: '12%',
                    align: 'center',
                    scopedSlots: { customRender: 'approval_volumes' }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
                    width: '12%',
                    align: 'center',
                    scopedSlots: { customRender: 'unit_name' }
                },
                {
                    title: '',
                    dataIndex: 'packge_multiple_num',
                    key: 'packge_multiple_num',
                    width: '10%',
                    align: 'center'
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    key: 'action',
                    width: '6%',
                    align: 'center',
                    scopedSlots: { customRender: 'action' }
                }
            ],
            greatPlanColumns: [
                {
                    title: 'NO.',
                    dataIndex: 'index',
                    key: 'index',
                    width: '5%',
                    align: 'center',
                    scopedSlots: { customRender: 'index' }
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    width: '22%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    width: '18%',
                    align: 'center'
                },
                {
                    title: '厂商/经销商',
                    dataIndex: 'product_vendor_name',
                    key: 'product_vendor_name',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'approval_volumes',
                    key: 'approval_volumes',
                    width: '12%',
                    align: 'center',
                    scopedSlots: { customRender: 'approval_volumes' }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
                    width: '12%',
                    align: 'center',
                    scopedSlots: { customRender: 'unit_name' }
                },
                {
                    title: '',
                    dataIndex: 'packge_multiple_num',
                    key: 'packge_multiple_num',
                    width: '10%',
                    align: 'center'
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    key: 'action',
                    width: '6%',
                    align: 'center',
                    scopedSlots: { customRender: 'action' }
                }
            ],
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
            // 添加产品
            addProductMd: {
                title: `${this.$route.meta.title} - 新增 - 添加产品`,
                alert: false,
                loading: false
            },
            partC: {
                // 新增产品窗口给子组件传递值
                partA_id: '',
                partB_id: '',
                billsType: '',
                oname: '',
                modalType: 4, // 1 - 合同内的产品  2 - 期初库存的产品列表  3 - 采购计划  4 - 采购/销售订单
                isProcurementSalesFlag: true // true - 销售单  false - 采购单
            },
            unitList: [], // 包装单位
            details: [], // 生成发放单 - 生成采购计划的 details后端所需字段数据
            expectDate: '', // 期望时间
            isEditDisabledFlag: true,
            searchTablesStatus: [
                // 顶部高级搜索的状态
                { key: 1, value: 0, label: '草稿', disabled: false },
                { key: 2, value: 1, label: '正式', disabled: false },
                { key: 3, value: 2, label: '已审核', disabled: false },
                { key: 4, value: 3, label: '已确认', disabled: false },
                { key: 5, value: 4, label: '已终止', disabled: true },
                { key: 6, value: 5, label: '已完结', disabled: true }
                // {key: 7, value: 6, label: '(确认)未通过', disabled: false}
            ],
            recordUnitInnerSn: '',
            applyTableItemStatus: ['申领中', '已完全发放', '已计划采购', '已终止申领'], // 医院申领单产品明细状态(status): `0-申领中 1-已完全发放 2-已计划采购 3-已终止申领`
            isApplyDetailStopFlag: false,
            planModal: {
                title: `${this.$route.meta.title} - 新增`,
                alert: false,
                loadingPrint: false,
                loadingAdd: false,
                loadingSaveDraft: false,
                loadingSave: false,
                loadingAudit: false,
                loadingExportExcel: false
            },
            planSendModal: {
                title: `${this.$route.meta.title} - 生成销售单`,
                alert: false,
                loadingPrint: false,
                loadingAdd: false,
                loadingSaveDraft: false,
                loadingSave: false,
                loadingAudit: false,
                loadingExportExcel: false
            },
            // 已处理 - 销售单详情modal状态
            infoModal: {
                title: `${this.$route.meta.title} - 销售单`,
                alert: false,
                loading: false
            },
            // 已处理 - 销售单详情modal内层数据集
            infoAllData: {
                // 外框总数据
                collection_date: moment[''], // 收款日期
                department_inner_sn: '', // 部门内部编号
                department_name: '', // 部门名称
                details: [], // 销售单明细列表
                billsType: '', // 订单类型 0 DealerDealer 1 DealerVendor 2 hospitalDealer 3 hospitalVendor
                org_id: this.$cookie.get('userbelong'), // 当前机构ID
                organization_inner_sn: 0, // 相关机构ID
                organization_name: '', // 相关机构名称
                purchaser_linkman: '', // 采购方联系人
                purchaser_phone: '', // 采购方联系电话
                seller_inner_sn: '', // 销售员ID
                seller_linkman: '', // 销售方联系人
                seller_name: '', // 销售员名称
                seller_phone: '', // 销售方联系电话
                warehouse_inner_sn: '', // 仓库内部ID
                description: '',
                audit_time: '',
                auditor_name: '',
                making_people_name: '',
                billing_time: ''
            },
            expectAogDate: moment[''],
            planModalHideActionsColumns: [
                {
                    title: 'NO.',
                    dataIndex: 'index',
                    key: 'index',
                    width: 40,
                    align: 'center',
                    scopedSlots: { customRender: 'index' }
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: '1',
                    width: 150,
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: '2',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '厂商',
                    dataIndex: 'partB_name',
                    key: '3',
                    width: 110,
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: '4',
                    width: 110,
                    align: 'center',
                    scopedSlots: { customRender: 'quantity' }
                },
                {
                    title: '已发货数量',
                    dataIndex: 'quantity_delivered',
                    key: '5',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    key: '7',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'status' }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_inner_sn',
                    key: 'unit_inner_sn',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'unit' }
                },
                {
                    title: '单价',
                    dataIndex: 'unit_price',
                    key: '9',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'unit_price' }
                },
                {
                    title: '金额',
                    dataIndex: 'money',
                    key: '10',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'money' }
                },
                // {
                //     title: '折扣',
                //     dataIndex: 'discount',
                //     key: '11',
                //     width: 100,
                //     align: 'center',
                //     scopedSlots: {customRender: 'discount'}
                // },
                // {
                //     title: '折后单价',
                //     dataIndex: 'discount_unit_price',
                //     key: '12',
                //     width: 100,
                //     align: 'center',
                //     scopedSlots: {customRender: 'discount_unit_price'}
                // },
                // {
                //     title: '折后金额',
                //     dataIndex: 'discount_money',
                //     key: '13',
                //     width: 100,
                //     align: 'center',
                //     scopedSlots: {customRender: 'discount_money'}
                // },
                {
                    title: '税率',
                    dataIndex: 'rate',
                    key: '14',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'rate' }
                },
                {
                    title: '含税单价',
                    dataIndex: 'tax_unit_price',
                    key: '15',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax_unit_price' }
                },
                {
                    title: '含税金额',
                    dataIndex: 'tax_amount',
                    key: '16',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax_amount' }
                },
                {
                    title: '税额',
                    dataIndex: 'tax',
                    key: '17',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax' }
                }
            ],
            planModalSendColumns: [
                {
                    title: 'NO.',
                    dataIndex: 'index',
                    key: 'index',
                    width: 40,
                    align: 'center',
                    scopedSlots: { customRender: 'index' }
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: '1',
                    width: 150,
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: '2',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '厂商',
                    dataIndex: 'product_vendor_name',
                    key: '3',
                    width: 110,
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: '4',
                    width: 110,
                    align: 'center',
                    scopedSlots: { customRender: 'quantity' }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_inner_sn',
                    key: 'unit_inner_sn',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'unit' }
                },
                {
                    title: '单价',
                    dataIndex: 'unit_price',
                    key: '9',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'unit_price' }
                },
                {
                    title: '金额',
                    dataIndex: 'money',
                    key: '10',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'money' }
                },
                // {
                //     title: '折扣',
                //     dataIndex: 'discount',
                //     key: '11',
                //     width: 100,
                //     align: 'center',
                //     scopedSlots: {customRender: 'discount'}
                // },
                // {
                //     title: '折后单价',
                //     dataIndex: 'discount_unit_price',
                //     key: '12',
                //     width: 100,
                //     align: 'center',
                //     scopedSlots: {customRender: 'discount_unit_price'}
                // },
                // {
                //     title: '折后金额',
                //     dataIndex: 'discount_money',
                //     key: '13',
                //     width: 100,
                //     align: 'center',
                //     scopedSlots: {customRender: 'discount_money'}
                // },
                {
                    title: '税率',
                    dataIndex: 'rate',
                    key: '14',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'rate' }
                },
                {
                    title: '含税单价',
                    dataIndex: 'tax_unit_price',
                    key: '15',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax_unit_price' }
                },
                {
                    title: '含税金额',
                    dataIndex: 'tax_amount',
                    key: '16',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax_amount' }
                },
                {
                    title: '税额',
                    dataIndex: 'tax',
                    key: '17',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax' }
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    key: '18',
                    width: 100,
                    align: 'center',
                    fixed: 'right',
                    slots: { title: 'actionTitle' },
                    scopedSlots: { customRender: 'action' }
                }
            ],
            planModalSendHideActionsColumns: [
                {
                    title: 'NO.',
                    dataIndex: 'index',
                    key: 'index',
                    width: 40,
                    align: 'center',
                    scopedSlots: { customRender: 'index' }
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: '1',
                    width: 150,
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: '2',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '厂商',
                    dataIndex: 'partB_name',
                    key: '3',
                    width: 110,
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: '4',
                    width: 110,
                    align: 'center',
                    scopedSlots: { customRender: 'quantity' }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_inner_sn',
                    key: 'unit_inner_sn',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'unit' }
                },
                {
                    title: '单价',
                    dataIndex: 'unit_price',
                    key: '9',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'unit_price' }
                },
                {
                    title: '金额',
                    dataIndex: 'money',
                    key: '10',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'money' }
                },
                // {
                //     title: '折扣',
                //     dataIndex: 'discount',
                //     key: '11',
                //     width: 100,
                //     align: 'center',
                //     scopedSlots: {customRender: 'discount'}
                // },
                // {
                //     title: '折后单价',
                //     dataIndex: 'discount_unit_price',
                //     key: '12',
                //     width: 100,
                //     align: 'center',
                //     scopedSlots: {customRender: 'discount_unit_price'}
                // },
                // {
                //     title: '折后金额',
                //     dataIndex: 'discount_money',
                //     key: '13',
                //     width: 100,
                //     align: 'center',
                //     scopedSlots: {customRender: 'discount_money'}
                // },
                {
                    title: '税率',
                    dataIndex: 'rate',
                    key: '14',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'rate' }
                },
                {
                    title: '含税单价',
                    dataIndex: 'tax_unit_price',
                    key: '15',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax_unit_price' }
                },
                {
                    title: '含税金额',
                    dataIndex: 'tax_amount',
                    key: '16',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax_amount' }
                },
                {
                    title: '税额',
                    dataIndex: 'tax',
                    key: '17',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'tax' }
                }
            ],
            planModalOrgSn: [],
            planModalOrgs: [],
            planModalForm: {
                buyer_id: '',
                buyer_id_old: '',
                buyer_name: '',
                department_inner_sn: '',
                department_inner_sn_old: '',
                department_name: '',
                description: '',
                description_old: '',
                details: [],
                expect_aog_date: '',
                expect_aog_date_old: '',
                create_organization_type: '',
                billsType: '',
                partA_id: '',
                partB_id: '',
                po_sn: '',
                po_sn_old: '',
                po_so_inner_sn: '',
                status: '',
                collection_date: '',
                delivery_address: '',
                delivery_date: '',
                department_id: '',
                org_id: '',
                organization_inner_sn: '',
                organization_name: '',
                purchaser_linkman: '',
                purchaser_phone: '',
                seller_inner_sn: '',
                seller_linkman: '',
                seller_name: '',
                seller_phone: '',
                sl_sn: ''
            },
            planModalDatas: [],
            planModalPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            planModalSelectedRowKeys: [],
            currentTrPlanModalObj: {},
            billsType: '', // 采购订单内页中临时记住相关机构的类型
            superiorDepartments: [],
            handlerNameList: [],
            btnNameSendOrderSale: '',
            idropdoNum: 0,
            idropdoOpen: false,
            infoData: [], // 销售单产品详情列表数据
            xTablePlanModalInfoToolbar: {
                slots: {
                    buttons: 'xtable_plan_modal_info_toolbar'
                },
                actions: {
                    isFullScreen: true,
                    fullScreenClass: 'yrt-quanping',
                    fullScreenTip: '全屏',
                    reductionClass: 'yrt-jurassic_Window-min',
                    reductionTip: '还原'
                }
            },
            chooseOrgMd: {
                title: `${this.$route.meta.title} - 选择机构`,
                alert: false,
                loading: false
            },
            healthAndSunSnModal: {
                data: []
            },
            isFullscreen: false,
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 57 : 0
        };
    },
    methods: {
        init() {
            if (this.$route.query.so_sn) {
                this.searchTables.order_sn = this.$route.query.so_sn;
            }
            this.systemType === 2 ? this.warehouseUsableTopListAPIFn() : this.orderSaleListAPIFn();
            this.initTablesPaginationFn();
        },
        // 库存来源改变
        selectWarehouseSourceFn(value) {
            if (!this.idropdoOpen) {
                value && this.suppliesClaimBotDatas.length && this.orderSaleDetailAPIFn(this.currentTrObj, value);
            }
        },
        dropShow() {
            let that = this;
            that.searchBotTables.warehouse_inner_snBak = that.searchBotTables.warehouse_inner_sn;
            that.$refs.idropdo.open = true;
            that.idropdoOpen = true;
            if (that.idropdoNum === 0) {
                setTimeout(() => {
                    let iDropDown = document.getElementsByClassName('ant-select-dropdown');
                    let iDropDownContent = document.getElementsByClassName('ant-select-dropdown-content');
                    iDropDown[0].style.zIndex = '2';
                    iDropDownContent[0].style.margin = '0px 0px 40px 0px';
                    let strParent = document.createElement('div');
                    strParent.className = 'strParent';
                    strParent.style.height = '40px';
                    strParent.style.width = '520px';
                    strParent.style.backgroundColor = 'white';
                    strParent.style.zIndex = '1';
                    strParent.style.position = 'sticky';
                    strParent.style.top = '360px';
                    // strParent.innerHTML = '<button class="ant-btn ant-btn-primary ant-btn-sm" style="float: right; margin: 0px 10px 10px 0px;">确定</button><button class="ant-btn ant-btn-default ant-btn-sm" style="float: right; margin: 0px 10px 10px 0px;">取消</button>'
                    iDropDown[0].appendChild(strParent);

                    let strNode = document.createElement('button');
                    strNode.className = 'ant-btn ant-btn-primary ant-btn-sm';
                    strNode.style.cssFloat = 'right';
                    strNode.style.margin = '0px 10px 10px 0px';
                    strNode.innerHTML = '确定';
                    strNode.onclick = function () {
                        if (that.searchBotTables.warehouse_inner_sn.length > 20) {
                            that.$warning({
                                title: '操作提示',
                                content: `选择上限为20个仓库`,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            return;
                        }
                        that.$refs.idropdo.open = false;
                        that.idropdoOpen = false;
                        if (that.searchBotTables.warehouse_inner_sn) {
                            that.suppliesClaimBotDatas.length &&
                                that.orderSaleDetailAPIFn(that.currentTrObj, that.searchBotTables.warehouse_inner_sn);
                        }
                    };
                    strParent.appendChild(strNode);
                    let strNode2 = document.createElement('button');
                    strNode2.className = 'ant-btn ant-btn-default ant-btn-sm';
                    strNode2.innerHTML = '取消';
                    strNode2.style.cssFloat = 'right';
                    strNode2.style.margin = '0px 10px 10px 0px';
                    strNode2.onclick = function () {
                        that.searchBotTables.warehouse_inner_sn = that.searchBotTables.warehouse_inner_snBak;
                        that.$refs.idropdo.open = false;
                        that.idropdoOpen = false;
                    };
                    strParent.appendChild(strNode2);
                    iDropDown[0].appendChild(iDropDownContent[0]);
                }, 200);
                that.idropdoNum++;
            }
        },
        dragControllerDiv: function () {
            let that = this;
            let resize = document.getElementsByClassName('resize');
            let boxtop = document.getElementsByClassName('boxtop');
            let mid = document.getElementsByClassName('mid');
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
                        that.tabTop = moveLen - 55;
                        that.tabDown = box[i].clientHeight - moveLen - 62;
                        for (let j = 0; j < boxtop.length; j++) {
                            boxtop[j].style.height = moveLen + 'px';
                            mid[j].style.height = box[i].clientHeight - moveLen - 10 + 'px';
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
        dragControllerDivHandle: function () {
            let that = this;
            let resize = document.getElementsByClassName('handleResize');
            let boxtop = document.getElementsByClassName('handleBoxtop');
            let mid = document.getElementsByClassName('handleBoxdown');
            let box = document.getElementsByClassName('handlebox');
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
                        if (moveLen < 200) moveLen = 200;
                        if (moveLen > maxT - 200) moveLen = maxT - 200;

                        resize[i].style.top = moveLen; // 设置左侧区域的宽度
                        that.tabTopHand = moveLen - 55;
                        that.tabDownHand = box[i].clientHeight - moveLen - 22;
                        for (let j = 0; j < boxtop.length; j++) {
                            boxtop[j].style.height = moveLen + 'px';
                            mid[j].style.height = box[i].clientHeight - moveLen - 10 + 'px';
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
        // 【查询机构所有人员】用于下列列表
        async orgEmpDropdownListAPIFn() {
            await orgEmpDropdownListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'allEmpDropdownList');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID查询经销商【所有部门】
        async orgDeptTopTreeAPIFn() {
            await orgDeptTopTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'deptTopTree');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【相关机构列表】
        async orderOrgListAPIFn() {
            // isSale: 是否销售订单 0-不是 1-是
            if (this.btnName === 'add') {
                // 默认赋值
                if (this.planModalOrgs.length) {
                    const curOrg = this.planModalOrgs[0].value,
                        arySplitValue = curOrg.split('-');
                    this.planModalOrgSn = [curOrg];
                    this.planModalForm.partA = arySplitValue[0];
                    this.billsType = arySplitValue[1];
                    this.partC.partA_id = arySplitValue[0];
                    this.partC.billsType = arySplitValue[1];
                    if (this.userSystemType === 2) {
                        // 经销商对经销商 医院对经销商 => 把机构名称读取
                        if (this.billsType === BillsType.DealerDealer || this.billsType === BillsType.HospitalDealer) {
                            this.partC.oname = this.orgName;
                        } else {
                            this.partC.oname = this.planModalOrgs[0].name;
                        }
                    } else if (this.userSystemType === 3) {
                        // 经销商对厂商 医院对厂商 => 把机构名称读取
                        if (this.billsType === BillsType.DealerVendor || this.billsType === BillsType.HospitalVendor) {
                            this.partC.oname = this.orgName;
                        } else {
                            this.partC.oname = this.planModalOrgs[0].name;
                        }
                    }
                } else {
                    this.planModalOrgSn = [];
                    this.planModalForm.partA = '';
                    this.billsType = '';
                    this.partC.oname = '';
                }
                this.planModalForm.billsType = this.partC.billsType;
            }
        },
        // 【生成采购计划】
        async orderSaleCreatplanAPIFn() {
            this.greatSendModal.loadingSend = true;
            let params = {
                department_inner_sn: this.editInfoFormObj.department_inner_sn,
                department_name: this.editInfoFormObj.department_name,
                description: this.editInfoFormObj.description,
                billsType: this.currentTrObj.billsType,
                org_id: this.$cookie.get('userbelong'),
                organization_inner_sn: this.currentTrObj.partA,
                po_sn: '',
                po_so_inner_sn: this.currentTrObj.po_so_inner_sn
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
                    quantity: item.approval_volumes,
                    sort_number: index,
                    product_type: item.product_type,
                    batch_number: item.batch_number || ''
                };
            });

            await orderSaleCreatplanAPI(params)
                .then((res) => {
                    this.greatSendModal.loadingSend = false;
                    if (parseFloat(res.code) === 0) {
                        this.orderSaleListAPIFn(false, this.currentTrObj);
                        // this.orderHistorySaleListAPIFn()
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
                    this.setPackgeMultipleFn(record);
                }
            });
        },
        // 【经销商 产品单位】 根据id查询所有上级产品单位
        async dealerUnitParentsAPIFn(record) {
            await dealerUnitParentsAPI(
                record.dealer_inner_sn ||
                    record.dealer_b_inner_sn ||
                    record.product_vendor_inner_sn ||
                    record.product_dealer_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let { info } = res;
                    record.unit_inner_sn = [];
                    record.isLoadingUnit = true;
                    let treeUnitList = (item) => {
                        record.unit_inner_sn.unshift(item.unit_inner_sn);
                        if (item.parent) {
                            treeUnitList(item.parent);
                        }
                    };
                    treeUnitList(info);
                }
            });
        },
        // 【经销商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async dealerUnitTreeAPIFn(record, btnName) {
            await dealerUnitTreeAPI(
                record.dealer_inner_sn ||
                    record.dealer_b_inner_sn ||
                    record.product_vendor_inner_sn ||
                    record.product_dealer_inner_sn,
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
                        if (btnName === 'add') {
                            // 赋默认值
                            record.unit_inner_sn = [curUnit.value];
                            record.unit_name = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xTablePlanModalInfo.reloadRow(record, record, 'unit');
                        }

                        if (btnName === 'great-plan') {
                            record.unit_inner_sn_new = [curUnit.value];
                            record.unit = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xGreatSendPlanTable.reloadRow(record, record, 'unit');
                            this.setPackgeMultipleFn(record);
                        } else {
                            record.detail_inner_sn && !record.isLoadingUnit && this.dealerUnitParentsAPIFn(record);
                        }
                    }
                }
            });
        },
        // 【厂商 产品单位】 根据id查询所有上级产品单位
        async platformUnitParentsFn(record) {
            await platformUnitParents(
                record.vendor_inner_sn || record.vendor_b_inner_sn || record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let { info } = res;
                    record.unit_inner_sn = [];
                    record.isLoadingUnit = true;
                    let treeUnitList = (item) => {
                        record.unit_inner_sn.unshift(item.unit_inner_sn);
                        if (item.parent) {
                            treeUnitList(item.parent);
                        }
                    };
                    treeUnitList(info);
                }
            });
        },
        // 【厂商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async platformUnitTreeFn(record, btnName) {
            await platformUnitTree(
                record.vendor_inner_sn || record.vendor_b_inner_sn || record.product_vendor_inner_sn,
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
                        if (btnName === 'add') {
                            // 赋默认值
                            record.unit_inner_sn = [curUnit.value];
                            record.unit_name = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xTablePlanModalInfo.reloadRow(record, record, 'unit');
                        }

                        if (btnName === 'great-plan') {
                            record.unit_inner_sn_new = [curUnit.value];
                            record.unit = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xGreatSendPlanTable.reloadRow(record, record, 'unit');
                            this.record = record;
                            this.setPackgeMultipleFn(record);
                        } else {
                            record.detail_inner_sn && !record.isLoadingUnit && this.platformUnitParentsFn(record);
                        }
                    }
                }
            });
        },
        // 【查询所有可用的机构一级仓库】
        async warehouseUsableTopListAPIFn() {
            this.wareHouseFilterData = this.warehouseList.map((item) => {
                return {
                    label: item.name,
                    value: item.warehouse_model_inner_sn
                };
            });
            this.searchBotTables.warehouse_inner_sn = this.wareHouseFilterData.length
                ? [this.wareHouseFilterData[0].value]
                : '';
            this.orderSaleListAPIFn();
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
            //             this.searchBotTables.warehouse_inner_sn = this.wareHouseFilterData.length ? [this.wareHouseFilterData[0].value] : ''
            //             this.orderSaleListAPIFn()
            //         } else {
            //             this.$message.error(res.msg)
            //         }
            //     })
        },
        reNumber(data) {
            let num = data.toString();
            let numA = 0;
            if (num.indexOf('.') !== -1) {
                numA = num.split('.')[1].length;
            }
            if (numA === 3) {
                data += 0.0001;
            }
            if (numA > 10) {
                // toFixed会返回字符串  需要转换为数字
                const n = this.$XEUtils.commafy(data, { fixed: 4 });
                data = parseFloat(n);
            }
            return data;
        },
        reNumberB(data) {
            let num = data.toString();
            let numA = 0;
            if (num.indexOf('.') !== -1) {
                numA = num.split('.')[1].length;
            }
            if (numA > 3) {
                // 小数超过3位小数时显示3位小数 否则只显示有效的小数位数
                data = this.$XEUtils.commafy(data, { fixed: 3 });
            }
            return data;
        },
        // 查看【销售订单明细信息】
        async orderSaleDetailAPIFn(record, warehouseInnerSn) {
            if (this.userSystemType === 3) this.suppliesColumnsBak = this.suppliesColumnsBakPro;
            this.suppliesColumns = [...this.suppliesColumnsBak];

            let arr = [];
            let supInt = 11;
            if (warehouseInnerSn) {
                warehouseInnerSn.forEach((item) => {
                    let obj = {
                        warehouse_inner_sn: item
                    };
                    arr = [...arr, obj];
                    let suppliesColumnsName = '';
                    let circle = (ciritem) => {
                        // 递归遍历
                        ciritem.forEach((circleitem) => {
                            if (circleitem.value === item) {
                                suppliesColumnsName = circleitem.label;
                            } else {
                                if (item.children && item.children.length > 0) {
                                    circle(item.children);
                                }
                            }
                        });
                    };
                    circle(this.wareHouseFilterData);
                    let suppliesColumnsObj = {
                        field: 'min_unit_inventory_' + item,
                        title: suppliesColumnsName,
                        width: '12%',
                        showOverflow: true,
                        showHeaderOverflow: true,
                        align: 'center'
                    };
                    this.suppliesColumns.splice(supInt, 0, suppliesColumnsObj);
                    supInt++;
                });
            }

            // [BUG3778]厂商登录，由于没有仓库，将仓库ID传一个随意值，无任何意义
            if (this.userSystemType === 3) {
                arr = [{ warehouse_inner_sn: 0 }];
            }

            // [BUG3773]有经销商登录，销售订单不选任何仓库时进行提示
            if (this.userSystemType === 2 && arr.length === 0) {
                this.$warning({
                    title: '操作提示',
                    content: '仓库不能为空。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return;
            }

            let params = {
                org_id: record.org_id || this.$cookie.get('userbelong'),
                partA_id: record.partA,
                po_so_inner_sn: record.po_so_inner_sn,
                billsType: record.billsType, // 订单类型 0 DealerDealer 1 DealerVendor 2 hospitalDealer 3 hospitalVendor
                warehouse_ids: arr
            };

            if (this.systemType === 2) {
                if (!this.searchBotTables.warehouse_inner_sn) return;
                // params.warehouse_inner_sn = warehouseInnerSn || this.searchBotTables.warehouse_inner_sn
                this.suppliesClaimBotPagination.loading = true;
            } else {
                // params.warehouse_inner_sn = 0
                this.suppliesClaimBotPagination.loading = true;
            }
            // params: {
            // ...params,
            //         warehouse_ids: JSON.stringify(arr)
            // }
            await orderSaleDetailAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.suppliesClaimBotAllDatas = list.map((item) => {
                            item.rate = this.reNumber(item.rate * 100);
                            item.key = item.detail_inner_sn;
                            item.status = parseFloat(item.status);
                            item.status_text = this.orderSaleDetailStatus[parseFloat(item.status)];
                            item.activeClass = '';
                            item.applyStopBtnLoading = false;
                            item.tax_unit_price = item.unit_price.toFixed(4);
                            // item.tax_unit_price = this.$XEUtils.commafy(item.unit_price, {fixed: 4})
                            item.unit_price = this.unit_price_record(item);
                            // 库存
                            if (typeof item.inventory === 'undefined') {
                                item.inventory = 0;
                            }
                            item.ware_quantity = 0;
                            item.all_quantity = 0;
                            item.all_quantity_new = '';
                            // item.rate = item.rate > 0 ? item.rate * 100 : 0
                            if (warehouseInnerSn) {
                                warehouseInnerSn.forEach((wareitem) => {
                                    item.ware_quantity += item['min_unit_inventory_' + wareitem];
                                    item.all_quantity += item['min_unit_inventory_' + wareitem];
                                    if (parseFloat(item.unit_inner_sn) === parseFloat(item.min_unit_inner_sn)) {
                                        // 申领量 包装单位 === 最小包装单位 不要显示括号的最小包装单位
                                        item['min_unit_inventory_' + wareitem] = `${
                                            item['min_unit_inventory_' + wareitem]
                                        }/${item.unit}`;
                                        item.all_quantity_new = `${item.all_quantity}/${item.unit}`;
                                    } else {
                                        const currentN = this.reNumberB(
                                            item['min_unit_inventory_' + wareitem] / item.min_unit_quantity
                                        ); // 当前遍历仓库的销售包装单位库存
                                        const allN = this.reNumberB(item.all_quantity / item.min_unit_quantity); // 所有选中的仓库的销售包装单位总库存
                                        item['min_unit_inventory_' + wareitem] = `${currentN}/${item.unit}(${
                                            item['min_unit_inventory_' + wareitem]
                                        }${item.min_unit})`;
                                        item.all_quantity_new = `${allN}/${item.unit}(${item.all_quantity}${item.min_unit})`;
                                    }
                                });
                            }
                            item.unlock_inventory = this.reNumber(item.all_quantity - item.min_unit_lock_inventory);
                            if (item.unlock_inventory < 0) item.unlock_inventory = 0;

                            if (parseFloat(item.unit_inner_sn) === parseFloat(item.min_unit_inner_sn)) {
                                // 申领量 包装单位 === 最小包装单位 不要显示括号的最小包装单位
                                item.quantity_new = `${item.quantity}/${item.unit}`;
                                item.stay_quantity = item.quantity - item.sold_quantity;
                                item.stay_quantity_new = `${item.quantity - item.sold_quantity}/${item.unit}`;
                                item.min_unit_for_sale_quantity = item.quantity - item.sold_quantity;
                                item.sold_quantity_new = `${item.sold_quantity}/${item.unit}`;
                                item.min_unit_lock_inventory_new = `${item.min_unit_lock_inventory}/${item.unit}`;
                                item.unlock_inventory_new = `${item.unlock_inventory}/${item.unit}`;
                            } else {
                                item.quantity_new = `${item.quantity}/${item.unit}(${this.reNumberB(
                                    item.quantity * item.min_unit_quantity
                                )}${item.min_unit})`;
                                let allqua = this.reNumberB(item.quantity * item.min_unit_quantity);
                                let stayqua = allqua - item.sold_quantity;
                                if (stayqua < 0) stayqua = 0;
                                item.stay_quantity = this.reNum(stayqua, item.min_unit_quantity);
                                item.stay_quantity_new = `${this.reNum(stayqua, item.min_unit_quantity)}/${
                                    item.unit
                                }(${stayqua}${item.min_unit})`;
                                item.min_unit_for_sale_quantity = stayqua;
                                item.sold_quantity_new = `${this.reNum(item.sold_quantity, item.min_unit_quantity)}/${
                                    item.unit
                                }(${item.sold_quantity}${item.min_unit})`;
                                //  锁定库存
                                item.min_unit_lock_inventory_new = `${this.reNumberB(
                                    item.min_unit_lock_inventory / item.min_unit_quantity
                                )}/${item.unit}(${item.min_unit_lock_inventory}${item.min_unit})`;
                                // 可用库存
                                item.unlock_inventory_new = `${this.reNumberB(
                                    item.unlock_inventory / item.min_unit_quantity
                                )}/${item.unit}(${
                                    item.unlock_inventory !== 0
                                        ? item.unlock_inventory.toFixed(3)
                                        : item.unlock_inventory
                                }${item.min_unit})`;
                            }
                            return item;
                        });
                        if (this.suppliesClaimBotAllDatas.length) {
                            this.filterSearchBotTablesFn();
                        }
                        this.suppliesClaimBotPagination = {
                            ...this.suppliesClaimBotPagination,
                            loading: false
                        };
                        this.$refs.xSuppliesClaimBotTable.updateData();
                    } else {
                        this.$message.error(res.msg);
                        this.suppliesClaimBotPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.suppliesClaimBotPagination.loading = false;
                });
        },
        reNum(NumA, NumB) {
            let nu = NumA / NumB;
            if (nu.toString().indexOf('.') > 0) {
                nu = this.$XEUtils.commafy(nu, { fixed: 1 });
            }
            return nu;
        },
        // 查看【销售订单列表】
        async orderSaleListAPIFn(btnNameFlag, curItem) {
            let params = {
                partB_id: this.$cookie.get('userbelong'),
                pageNum: this.suppliesClaimPagination.current,
                pageSize: this.suppliesClaimPagination.pageSize
            };

            if (this.searchTables.partA_name) {
                params.partA_name = this.searchTables.partA_name;
            }
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                if (this.searchTables.status.length) {
                    params.status = this.searchTables.status[this.searchTables.status.length - 1];
                }
            }

            if (this.searchTables.order_sn) {
                params.order_sn = this.searchTables.order_sn;
            }

            /// 排序
            params.orderByColumn = 'create_time';
            params.isAsc = 'desc';

            await orderSaleListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.suppliesClaimDatas = rows.map((item, index) => {
                            let itemObj = {
                                ...item,
                                index,
                                key: item.partA + '' + item.partB + item.po_so_inner_sn + item.billsType,
                                activeClass: '',
                                audit_status_text: this.orderSaleListStatus[parseFloat(item.status)],
                                create_organization_type_text:
                                    this.createOrganizationType[parseFloat(item.create_organization_type)]
                            };
                            if (!btnNameFlag && curItem && itemObj.key === curItem.key) {
                                this.currentTrObj = itemObj;
                                this.$refs.xSuppliesClaimTable.setCurrentRow(itemObj);
                            }
                            return itemObj;
                        });
                        if (btnNameFlag === 'add') {
                            const curItemAdd = this.suppliesClaimDatas[this.suppliesClaimDatas.length - 1];
                            this.currentTrObj = curItemAdd;
                            this.$refs.xSuppliesClaimTable.setCurrentRow(curItemAdd);
                        } else {
                            if (!curItem) {
                                this.currentTrObj = this.suppliesClaimDatas[0];
                                this.$refs.xSuppliesClaimTable.setCurrentRow(this.suppliesClaimDatas[0]);
                            }
                        }
                        if (this.suppliesClaimDatas.length) {
                            this.orderSaleDetailAPIFn(this.currentTrObj, this.searchBotTables.warehouse_inner_sn);
                        } else {
                            this.suppliesClaimDatas = [];
                            this.currentTrObj = {};
                            this.suppliesClaimBotAllDatas = [];
                            this.suppliesClaimBotDatas = [];
                            this.currentTrBotObj = {};
                            this.$refs.xSuppliesClaimTable.clearData();
                        }
                        this.suppliesClaimPagination = {
                            ...this.suppliesClaimPagination,
                            total,
                            loading: false
                        };
                        this.$refs.xSuppliesClaimTable.updateData();
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.wrapTableCloseLoadingFn();
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 查看【历史销售订单列表】
        async orderHistorySaleListAPIFn(curItem) {
            let params = {
                partB_id: this.$cookie.get('userbelong'),
                pageNum: this.suppliesClaimHistoryPagination.current,
                pageSize: this.suppliesClaimHistoryPagination.pageSize
            };
            // 判断是否是高级搜索
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                if (this.searchTables.order_sn) {
                    params.order_sn = this.searchTables.order_sn;
                }
                if (this.searchTables.status.length) {
                    params.status = this.searchTables.status[this.searchTables.status.length - 1];
                }
            }

            if (this.searchTables.partA_name) {
                params.partA_name = this.searchTables.partA_name;
            }

            if (this.searchTables.order_sn) {
                params.order_sn = this.searchTables.order_sn;
            }

            await orderHistorySaleListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.suppliesClaimHistoryDatas = rows.map((item, index) => {
                            let itemObj = {
                                ...item,
                                index,
                                key: item.partA + '' + item.partB + item.po_so_inner_sn + item.billsType,
                                activeClass: '',
                                audit_status_text: this.orderSaleListStatus[parseFloat(item.status)],
                                create_organization_type_text:
                                    this.createOrganizationType[parseFloat(item.create_organization_type)]
                            };
                            if (curItem && itemObj.key === curItem.key) {
                                this.currentTrHistoryObj = itemObj;
                                this.$refs.xSuppliesClaimHistoryTable.setCurrentRow(itemObj);
                            }
                            return itemObj;
                        });

                        if (!curItem) {
                            const curItemHistory = this.suppliesClaimHistoryDatas.length
                                ? this.suppliesClaimHistoryDatas[0]
                                : {};
                            this.currentTrHistoryObj = curItemHistory;
                            this.$refs.xSuppliesClaimHistoryTable.setCurrentRow(curItemHistory);
                        }
                        if (this.suppliesClaimHistoryDatas.length) {
                            if (Object.keys(this.currentTrHistoryObj).length <= 0) {
                                this.currentTrHistoryObj = this.suppliesClaimHistoryDatas[0];
                                this.$refs.xSuppliesClaimHistoryTable.setCurrentRow(this.suppliesClaimHistoryDatas[0]);
                            }
                            this.orderSaleRecordListAPIFn(this.currentTrHistoryObj);
                        } else {
                            this.suppliesClaimHistoryBotDatas = [];
                            this.currentTrHistoryBotObj = {};
                        }

                        this.suppliesClaimHistoryPagination = {
                            ...this.suppliesClaimHistoryPagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.wrapTableCloseLoadingFn();
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 查看【销售单明细列表】
        async orderSaleRecordListAPIFn(record) {
            this.suppliesClaimHistoryBotPagination.loading = true;
            await orderSaleRecordListAPI({
                params: {
                    partB: record.partB,
                    partA: record.partA,
                    po_so_inner_sn: record.po_so_inner_sn,
                    billsType: record.billsType
                }
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.suppliesClaimHistoryBotDatas = list.map((item) => {
                            let status = parseFloat(item.status);
                            return {
                                ...item,
                                status,
                                audit_status_text: this.saleListStatus[status]
                            };
                        });
                        this.currentTrHistoryBotObj = this.suppliesClaimHistoryBotDatas.length
                            ? this.suppliesClaimHistoryBotDatas[0]
                            : {};
                        this.$refs.xSuppliesClaimHistoryBotTable.setCurrentRow(this.currentTrHistoryBotObj);
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.suppliesClaimHistoryBotPagination.loading = false;
                })
                .catch(() => {
                    this.suppliesClaimHistoryBotPagination.loading = false;
                });
        },
        // 【添加销售订单】
        async orderSaleAddAPIFn(status) {
            let params = {
                description: this.planModalForm.description,
                expect_aog_date: this.planModalForm.expect_aog_date,
                partA_id: this.planModalForm.partA,
                partB_id: this.$cookie.get('userbelong'),
                po_sn: '',
                details: this.detailList,
                billsType: this.billsType,
                status
            };
            params.details = [];
            this.detailList.forEach((item) => {
                item.rate = this.reNumber(item.rate * 0.01); // 折扣保存时除以100
                params.details.push(item);
            });

            await orderSaleAddAPI(params)
                .then((res) => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }

                    if (parseFloat(res.code) === 0) {
                        const { info } = res,
                            infoNew = {
                                ...info,
                                audit_status_text: this.orderSaleListStatus[parseFloat(info.status)],
                                create_organization_type_text:
                                    this.createOrganizationType[parseFloat(info.create_organization_type)]
                            },
                            xSuppliesClaimTable = this.$refs.xSuppliesClaimTable;
                        this.suppliesClaimPagination.total++;

                        if (this.suppliesClaimDatas.length <= 0) {
                            this.$refs.xSuppliesClaimTable.clearData();
                            this.suppliesClaimDatas.push(infoNew);
                            xSuppliesClaimTable.loadData(this.suppliesClaimDatas);
                            xSuppliesClaimTable.setCurrentRow(this.suppliesClaimDatas[0]);
                            this.currentTrObj = this.suppliesClaimDatas[0];
                            this.cellClickSuppliesClaimFn({ row: this.suppliesClaimDatas[0] });
                        } else {
                            const recordDelar = xSuppliesClaimTable.getCurrentRow();
                            xSuppliesClaimTable.insertAt(infoNew, recordDelar).then(({ row }) => {
                                this.suppliesClaimDatas = xSuppliesClaimTable.getTableData().tableData;
                                xSuppliesClaimTable.setCurrentRow(row);
                                this.currentTrObj = row;
                                this.cellClickSuppliesClaimFn({ row });
                                if (this.suppliesClaimDatas.length > this.suppliesClaimPagination.pageSize) {
                                    this.suppliesClaimDatas.splice(this.suppliesClaimDatas.length - 1, 1);
                                }
                            });
                        }

                        this.activeTabsKey = this.tabTypes[0];
                        this.closePlanModalFn();
                        this.resetPlanModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }
                });
        },
        // 【修改】销售订单信息
        async orderSaleEditAPIFn(status) {
            let params = {
                status,
                billsType: this.planModalForm.billsType,
                partA_id: this.planModalForm.partA,
                partB_id: this.planModalForm.partB,
                po_so_inner_sn: this.planModalForm.po_so_inner_sn
            };
            params.details = [];
            this.detailList.forEach((item) => {
                item.rate = this.reNumber(item.rate * 0.01);
                params.details.push(item);
            });

            if (this.planModalForm.description !== this.planModalForm.description_old) {
                params.description = this.planModalForm.description;
                params.description_old = this.planModalForm.description_old;
            }

            if (this.planModalForm.expect_aog_date !== this.planModalForm.expect_aog_date_old) {
                params.expect_aog_date = this.planModalForm.expect_aog_date;
                params.expect_aog_date_old = this.planModalForm.expect_aog_date_old;
            }

            // if (this.planModalForm.so_sn !== this.planModalForm.so_sn_old) {
            //     params.so_sn = this.planModalForm.so_sn
            //     params.so_sn_old = this.planModalForm.so_sn_old
            // }

            await orderSaleEditAPI(params)
                .then((res) => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1 || status === 3) {
                        this.planModal.loadingSave = false;
                    }

                    if (parseFloat(res.code) === 0) {
                        const xSuppliesClaimTable = this.$refs.xSuppliesClaimTable,
                            curObjDelar = xSuppliesClaimTable.getCurrentRow(),
                            infoDelar = {
                                ...curObjDelar,
                                status,
                                audit_status_text: this.orderSaleListStatus[parseFloat(status)],
                                create_organization_type_text:
                                    this.createOrganizationType[parseFloat(curObjDelar.create_organization_type)],
                                department_inner_sn: this.planModalForm.department_inner_sn,
                                department_name: this.planModalForm.department_name,
                                auditor_inner_sn: this.planModalForm.auditor_inner_sn,
                                auditor_name: this.planModalForm.auditor_name,
                                creator_inner_sn: this.planModalForm.creator_inner_sn,
                                creator_name: this.planModalForm.creator_name,
                                buyer_name: this.planModalForm.buyer_name,
                                buyer_id: this.planModalForm.buyer_id,
                                expect_aog_date: this.planModalForm.expect_aog_date
                            };
                        for (let i = 0; i < this.suppliesClaimDatas.length; i++) {
                            const item = this.suppliesClaimDatas[i];
                            if (curObjDelar._XID === item._XID) {
                                this.currentTrObj = infoDelar;
                                this.suppliesClaimDatas.splice(i, 1, infoDelar);
                                xSuppliesClaimTable.refreshData();
                                xSuppliesClaimTable.loadData(this.suppliesClaimDatas);
                                xSuppliesClaimTable.setCurrentRow(infoDelar);
                                break;
                            }
                        }
                        this.closePlanModalFn();
                        this.resetPlanModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1 || status === 3) {
                        this.planModal.loadingSave = false;
                    }
                })
                .finally(() => {
                    this.resetBtnLoadingState();
                    this.orderSaleListAPIFn();
                });
        },
        // 重置dialog中操作按钮的loading状态
        resetBtnLoadingState() {
            this.planModal = {
                ...this.planModal,
                loadingSaveDraft: false,
                loadingSave: false,
                loadingAdd: false,
                loadingAudit: false
            };
        },
        // 【确认销售订单】
        async orderSaleConfirmAPIFn() {
            let params = {
                partA_id: this.planModalForm.partA,
                partB_id: this.planModalForm.partB,
                so_sn: '',
                po_so_inner_sn: this.planModalForm.po_so_inner_sn,
                billsType: this.billsType
            };
            await orderSaleConfirmAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.planModal.loadingSave = false;
                        this.planModal.alert = false;
                        this.orderSaleListAPIFn(false, this.currentTrObj);
                    } else {
                        this.$message.error(res.msg);
                        this.planModal.loadingSave = false;
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                    this.planModal.loadingSave = false;
                });
        },
        // 查看【销售订单信息】
        async orderSaleInfoAPIFn(infoObj) {
            this.planModalPagination.loading = true;
            const params = { ...infoObj };
            await orderSaleInfoAPI({
                params
            })
                .then((res) => {
                    this.planModalPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.planModalForm = {
                            ...info,
                            description_old: info.description,
                            expect_aog_date_old: info.expect_aog_date,
                            so_sn_old: info.so_sn,
                            status: parseFloat(info.status)
                        };
                        this.expectAogDate = info.expect_aog_date ? moment(info.expect_aog_date, 'YYYY-MM-DD') : null;
                        this.editInfoFormDepartmentInnerSn = this.planModalForm.department_inner_sn;
                        this.planModalForm.partA = info.partA;
                        this.planModalOrgSn = [info.partA + '-' + info.billsType];
                        this.partC.partA_id = info.partA;
                        this.billsType = info.billsType;
                        this.partC.billsType = info.billsType;
                        this.planModalForm.billsType = info.billsType;
                        this.infoAllData.organization_name = info.partA_name;

                        if (this.userSystemType === 2) {
                            // 经销商对经销商 医院对经销商 => 把机构名称读取
                            if (
                                this.billsType === BillsType.DealerDealer ||
                                this.billsType === BillsType.HospitalDealer
                            ) {
                                this.partC.oname = this.orgName;
                            } else {
                                this.partC.oname = info.partA_name || '';
                            }
                        } else if (this.userSystemType === 3) {
                            // 经销商对厂商 医院对厂商 => 把机构名称读取
                            if (
                                this.billsType === BillsType.DealerVendor ||
                                this.billsType === BillsType.HospitalVendor
                            ) {
                                this.partC.oname = this.orgName;
                            } else {
                                this.partC.oname = info.partA_name || '';
                            }
                        }

                        // 检测相关机构是否在相关机构列表可以碰撞到值
                        const orgSnFlag = this.planModalOrgs.some((item) => item.value === this.planModalOrgSn);
                        if (!orgSnFlag) {
                            this.planModalOrgs.push({
                                label: info.partA_name || '',
                                name: info.partA_name || '',
                                title: info.partA_name || '',
                                value: this.planModalOrgSn,
                                billsType: info.billsType,
                                org_id: info.partA
                            });
                        }

                        if (info.details && info.details.length) {
                            this.planModalDatas = info.details.map((item, index) => {
                                return {
                                    ...item,
                                    index,
                                    key: Math.random(),
                                    activeClass: '',
                                    unitList: item.unit_inner_sn
                                        ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                        : [],
                                    unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                                    isLoadingUnit: false, // 是否加载过整棵树
                                    status: parseFloat(item.status),
                                    loading: false,
                                    switch:
                                        (this.planModalForm.status === 2 || this.planModalForm.status === 3) &&
                                        (item.status === 0 || item.status === 2),
                                    // rate: item.rate > 0 ? item.rate * 100 : 0,
                                    discount: item.discount,
                                    rate: this.reNumber(item.rate * 100),
                                    unitPriceList: [],
                                    tax_unit_price: item.unit_price
                                };
                            });
                            for (let i = 0; i < this.planModalDatas.length; i++) {
                                let item = this.planModalDatas[i];
                                if (parseInt(this.planModalForm.status) <= 1) {
                                    // 是草稿和正式
                                    this.contractGetOrderPriceAPIFn(item);
                                }
                            }
                        }
                        // this.currentTrPlanModalObj = this.planModalDatas.length ? this.planModalDatas[0] : {}
                        // !this.isEditDisabled && this.dblclickAddProductFn()
                        this.$refs.xTablePlanModalInfo.updateData();
                        this.$refs.xTablePlanModalInfo.setCurrentRow(this.planModalDatas[0]);
                        this.currentTrPlanModalObj = this.planModalDatas[0];
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.planModalPagination.loading = false;
                });
        },
        // 查询价格
        async contractGetOrderPriceAPIFn(record) {
            let tempUnitInnerSn = Array.isArray(record.unit_inner_sn)
                ? record.unit_inner_sn[record.unit_inner_sn.length - 1]
                : record.unit_inner_sn;
            let params = {
                billsType: this.planModalForm.billsType,
                organization_inner_sn: this.planModalForm.partA,
                product_inner_sn: record.product_inner_sn,
                product_type: record.product_type,
                product_vendor_inner_sn:
                    record.product_vendor_inner_sn || record.vendor_inner_sn || record.dealer_inner_sn,
                specification_inner_sn: record.specification_inner_sn,
                unit_inner_sn: typeof tempUnitInnerSn === 'undefined' ? 0 : tempUnitInnerSn,
                saleUse: true
            };

            await contractGetOrderPriceAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    record.unitPriceList = list || [];
                    if (record.unitPriceList.length && typeof record.detail_inner_sn === 'undefined') {
                        record.unit_price = record.unitPriceList[0];
                        record.tax_unit_price = record.unitPriceList[0];
                    }
                    if (this.$refs.xTablePlanModalInfo) {
                        this.$refs.xTablePlanModalInfo.updateData();
                    } else {
                        this.$refs.xGreatSendSaleTable.updateData();
                    }
                    this.updateFooterPlanModalFn();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        changeUnitPriceFn(row, curPrice) {
            row.tax_unit_price = curPrice;
        },
        // 【删除】订单
        async orderDelAPIFn() {
            this.suppliesClaimPagination.loading = true;
            let params = [
                {
                    partA_id: this.currentTrObj.partA,
                    partB_id: this.currentTrObj.partB,
                    po_so_inner_sn: this.currentTrObj.po_so_inner_sn,
                    billsType: this.currentTrObj.billsType
                }
            ];

            await orderDelAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.orderSaleListAPIFn();
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 【终止订单】
        async orderStopAPIFn() {
            let list = this.createServiceListFn();
            await orderStopAPI(list)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        // if (this.suppliesClaimHistoryDatas.length >= this.suppliesClaimHistoryPagination.pageSize) {
                        //     let pageNum = Math.floor(this.suppliesClaimHistoryPagination.total / this.suppliesClaimHistoryPagination.pageSize) + 1
                        //     this.suppliesClaimHistoryPagination.current = pageNum
                        // }
                        this.activeTabsKey = this.tabTypes[1];
                        this.toggleSearchTabsFn();
                        this.orderHistorySaleListAPIFn();
                        this.orderSaleListAPIFn();
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 【确认订单】
        async orderConfirmAPIFn() {
            let list = this.createServiceListFn();
            await orderConfirmAPI(list)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.orderSaleListAPIFn(false, this.currentTrObj);
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 【保存】销售单
        async orderSaleBuildAPIFn(status) {
            let params = {
                po_so_inner_sn: this.planModalForm.po_so_inner_sn,
                warehouse_inner_sn: this.searchBotTables.warehouse_inner_sn[0] || '', // 厂商没有仓库直接给个空字符串 此参数已废弃
                collection_date:
                    this.planModalForm.collection_date && Object.keys(this.planModalForm.collection_date).length
                        ? moment(this.planModalForm.collection_date).format('YYYY-MM-DD')
                        : '',
                // delivery_address: this.planModalForm.delivery_address || '',
                // delivery_date: this.planModalForm.delivery_date && Object.keys(this.planModalForm.delivery_date).length ? moment(this.planModalForm.delivery_date).format('YYYY-MM-DD') : '',
                department_id: this.planModalForm.department_id || '',
                department_name: this.planModalForm.department_name || '',
                billsType: this.planModalForm.billsType,
                org_id: this.$cookie.get('userbelong'),
                organization_inner_sn: this.planModalForm.partA || '',
                organization_name: this.planModalForm.partA_name || '',
                purchaser_linkman: this.planModalForm.purchaser_linkman || '',
                purchaser_phone: this.planModalForm.purchaser_phone || '',
                seller_inner_sn: this.planModalForm.seller_inner_sn || '',
                seller_linkman: this.planModalForm.seller_linkman || '',
                seller_name: this.planModalForm.seller_name || '',
                seller_phone: this.planModalForm.seller_phone || '',
                sl_sn: '',
                description: this.planModalForm.description || '',
                details: this.detailList,
                status
            };
            await orderSaleBuildAPI(params)
                .then((res) => {
                    if (status === 0) {
                        this.planSendModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planSendModal.loadingSave = false;
                    } else if (status === 2) {
                        this.planSendModal.loadingAudit = false;
                    }

                    if (parseFloat(res.code) === 0) {
                        // const {info} = res
                        // info.key = info.partA + '' + info.partB + info.po_so_inner_sn + info.billsType
                        // info.activeClass = ''
                        // this.suppliesClaimPagination.loading = true
                        // if (this.suppliesClaimDatas.length >= this.suppliesClaimPagination.pageSize) {
                        //     let pageNum = Math.floor(this.suppliesClaimPagination.total / this.suppliesClaimPagination.pageSize) + 1
                        //     this.suppliesClaimPagination.current = pageNum
                        // }
                        // this.orderSaleListAPIFn('add', info)
                        this.orderSaleListAPIFn(false, this.currentTrObj);
                        // this.activeTabsKey = this.tabTypes[0]
                        this.closePlanModalFn();
                        this.resetPlanModalFn();
                        this.planSendModal.alert = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    if (status === 0) {
                        this.planSendModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planSendModal.loadingSave = false;
                    } else if (status === 2) {
                        this.planSendModal.loadingAudit = false;
                    }
                });
        },
        // 【勾选计划明细】生成销售单
        async orderSaleDetailBuildAPIFn() {
            this.planModalPagination.loading = true;
            let list = [];
            this.planModalDatas.forEach((item) => {
                this.planModalSelectedRowKeys.forEach((cItem) => {
                    if (parseFloat(item.key) === parseFloat(cItem)) {
                        list.push({
                            partA_id: item.partA,
                            partB_id: item.partB,
                            po_so_inner_sn: item.po_so_inner_sn,
                            detail_inner_sn: item.detail_inner_sn,
                            product_type: item.product_type,
                            billsType: this.planModalForm.billsType
                        });
                    }
                });
            });
            await orderSaleDetailBuildAPI(list)
                .then((res) => {
                    this.planModal.loadingAdd = false;
                    this.planModalPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        this.suppliesClaimPagination.loading = true;
                        if (this.suppliesClaimDatas.length >= this.suppliesClaimPagination.pageSize) {
                            let pageNum =
                                Math.floor(this.suppliesClaimPagination.total / this.suppliesClaimPagination.pageSize) +
                                1;
                            this.suppliesClaimPagination.current = pageNum;
                        }
                        this.orderSaleListAPIFn('add', this.currentTrObj);
                        this.activeTabsKey = this.tabTypes[0];
                        this.closePlanModalFn();
                        this.resetPlanModalFn();
                    } else {
                        this.$message.error(res.msg);
                        this.planModal.loadingAdd = false;
                        this.planModalPagination.loading = false;
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.planModal.loadingAdd = false;
                    this.planModalPagination.loading = false;
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 【删除】订单明细
        async orderDetailDelAPIFn(record) {
            this.planModalPagination.loading = true;
            let params = {
                po_so_inner_sn: record.po_so_inner_sn,
                detail_inner_sn: record.detail_inner_sn,
                product_type: record.product_type,
                billsType: this.planModalForm.billsType,
                partA_id: record.partA,
                partB_id: this.$cookie.get('userbelong')
            };

            await orderDetailDelAPI(params)
                .then((res) => {
                    this.planModalPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
                        xTablePlanModalInfo.remove(record);
                        this.planModalDatas = xTablePlanModalInfo.getTableData().tableData;
                        xTablePlanModalInfo.setCurrentRow(this.planModalDatas[0]);
                        this.dblclickAddProductFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.planModalPagination.loading = false;
                });
        },
        // 【终止订单明细】
        async orderDetailStopAPIFn(record, btnName) {
            const isApplyStopDetailsWrapFlag = btnName === 'apply-stop-wrap-details'; // 是否是外层的终止按钮触发
            let params = {};
            if (isApplyStopDetailsWrapFlag) {
                record.applyStopBtnLoading = true;
                params = {
                    partA_id: this.currentTrObj.partA,
                    partB_id: this.currentTrObj.partB,
                    po_so_inner_sn: this.currentTrObj.po_so_inner_sn,
                    detail_inner_sn: record.detail_inner_sn,
                    product_type: record.product_type,
                    billsType: this.currentTrObj.billsType
                };
            } else {
                params = {
                    partA_id: record.partA,
                    partB_id: record.partB,
                    po_so_inner_sn: record.po_so_inner_sn,
                    detail_inner_sn: record.detail_inner_sn,
                    product_type: record.product_type,
                    billsType: this.planModalForm.billsType
                };
            }

            await orderDetailStopAPI(params)
                .then((res) => {
                    if (isApplyStopDetailsWrapFlag) {
                        record.applyStopBtnLoading = true;
                    } else {
                        record.loading = false;
                    }
                    if (parseFloat(res.code) === 0) {
                        if (btnName === 'apply-stop-wrap-details') {
                            if (this.systemType === 2) {
                                this.searchBotTables.warehouse_inner_sn &&
                                    this.orderSaleDetailAPIFn(
                                        this.currentTrObj,
                                        this.searchBotTables.warehouse_inner_sn
                                    );
                            } else {
                                this.orderSaleDetailAPIFn(this.currentTrObj, this.searchBotTables.warehouse_inner_sn);
                            }
                            this.isApplyDetailStopFlag = true;
                            record.applyStopBtnLoading = false;
                        } else {
                            record.status = 3;
                            record.switch = !record.switch;
                            this.$refs.xTablePlanModalInfo.updateData();
                            this.orderSaleDetailAPIFn(this.currentTrObj, this.searchBotTables.warehouse_inner_sn);
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    if (isApplyStopDetailsWrapFlag) {
                        record.applyStopBtnLoading = true;
                    } else {
                        record.loading = false;
                    }
                });
        },

        // 查看【销售订单明细信息】
        async saleInfoFn() {
            let params = {
                org_id: this.$cookie.get('userbelong'),
                sl_inner_sn: this.currentTrSalesObj.sl_inner_sn,
                billsType: this.currentTrSalesObj.billsType // 订单类型 0 DealerDealer 1 DealerVendor 2 hospitalDealer 3 hospitalVendor
            };
            await saleRecordInfoAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        info.collection_date = info.collection_date ? moment(info.collection_date) : '';
                        this.infoAllData = info;
                        this.infoData = info.details.map((item) => {
                            return {
                                ...item,
                                unit_inner_sn: [item.unit_inner_sn],
                                rate: this.reNumber(item.rate * 100),
                                unitList: item.unit_inner_sn
                                    ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                    : [],
                                action: 2,
                                tax_unit_price: item.unit_price
                            };
                        });
                        this.infoDataBak = this.infoData;
                        this.planModalOrgSn = [info.organization_inner_sn + '-' + info.billsType];
                        this.partC.partA_id = info.organization_inner_sn;
                        this.billsType = info.billsType;
                        this.partC.billsType = info.billsType;
                        this.planModalForm.billsType = info.billsType;
                        // 检测相关机构是否在相关机构列表可以碰撞到值
                        const orgSnOrderType = info.organization_inner_sn + '-' + info.billsType;
                        const orgSnFlag = this.planModalOrgs.some((item) => item.value === orgSnOrderType);
                        if (!orgSnFlag) {
                            this.planModalOrgs.push({
                                label: info.organization_name || '',
                                name: info.organization_name || '',
                                title: info.organization_name || '',
                                // value: info.organization_inner_sn,
                                value: orgSnOrderType,
                                billsType: info.billsType,
                                org_id: info.organization_inner_sn
                            });
                        }

                        if (this.userSystemType === 2) {
                            // 经销商对经销商 医院对经销商 => 把机构名称读取
                            if (
                                info.billsType === BillsType.DealerDealer ||
                                info.billsType === BillsType.HospitalDealer
                            ) {
                                this.partC.oname = this.orgName;
                            } else {
                                this.partC.oname = info.organization_name || '';
                            }
                        } else if (this.userSystemType === 3) {
                            // 经销商对厂商 医院对厂商 => 把机构名称读取
                            if (
                                info.billsType === BillsType.DealerVendor ||
                                info.billsType === BillsType.HospitalVendor
                            ) {
                                this.partC.oname = this.orgName;
                            } else {
                                this.partC.oname = info.organization_name || '';
                            }
                        }
                        // this.$nextTick(() => {
                        //     if (this.infoData.length > 0) this.$refs.xGreatSendSaleTable.setCurrentRow(this.infoData[0])
                        // })
                        this.infoModal = {
                            ...this.infoModal,
                            loading: false
                        };
                        // this.$refs.xGreatSendSaleTable.updateData()
                    } else {
                        this.$message.error(res.msg);
                        this.infoModal.loading = false;
                    }
                })
                .catch(() => {
                    this.infoModal.loading = false;
                });
        },

        // 【反终止订单明细】
        async orderDetailUnstopAPIFn(record) {
            let params = {
                partA_id: record.partA,
                partB_id: record.partB,
                po_so_inner_sn: record.po_so_inner_sn,
                detail_inner_sn: record.detail_inner_sn,
                product_type: record.product_type,
                billsType: this.planModalForm.billsType
            };

            await orderDetailUnstopAPI(params)
                .then((res) => {
                    record.loading = false;
                    if (parseFloat(res.code) === 0) {
                        record.switch = !record.switch;
                        record.status = 0;
                        this.$refs.xTablePlanModalInfo.updateData();
                        this.orderSaleDetailAPIFn(this.currentTrObj, this.searchBotTables.warehouse_inner_sn);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    record.loading = false;
                });
        },

        // 表格重新刷新数据的初始化函数 02 【初始化分页data】
        initTablesPaginationFn() {
            if (this.activeTabsKey === this.tabTypes[0]) {
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
            this.orderSaleListAPIFn();
        },
        changePaginationHistoryFn({ currentPage, pageSize }) {
            this.suppliesClaimHistoryPagination.current = currentPage;
            this.suppliesClaimHistoryPagination.pageSize = pageSize;
            this.suppliesClaimHistoryPagination.loading = true;
            this.orderHistorySaleListAPIFn();
        },
        // 点击待发放的列表显示下方的表格重置
        clickWaitListBotResetFn(record, isClickBtnName) {
            this.currentTrObj = record;
            this.suppliesClaimBotSelectedRowKeys = [];
            this.suppliesClaimBotDatas = [];
            this.currentTrBotObj = {};
            if (isClickBtnName === 'click') {
                // this.systemType === 2 => 经销商端的【仓库id】有，厂商没有仓库一说，故以下接口的【仓库id】应为可选项
                if (this.systemType === 2 && this.searchBotTables.warehouse_inner_sn.length <= 0) {
                    this.$warning({
                        title: '操作提示',
                        content: '仓库不能为空。',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                } else {
                    this.orderSaleDetailAPIFn(this.currentTrObj, this.searchBotTables.warehouse_inner_sn);
                }
            } else if (isClickBtnName === 'dblclick') {
                this.groupBtnFn('edit');
            }
        },
        cellClickSuppliesClaimFn({ row }) {
            this.clickWaitListBotResetFn(row, 'click');
        },
        cellDblclickSuppliesClaimFn({ row }) {
            this.clickWaitListBotResetFn(row, 'dblclick');
        },
        cellClickSuppliesClaimBotFn({ row }) {
            this.currentTrBotObj = row;
        },
        cellDblclickSuppliesClaimBotFn({ row }) {
            this.currentTrBotObj = row;
        },
        cellClickSuppliesClaimHistoryFn({ row }) {
            this.currentTrHistoryObj = row;
            this.orderSaleRecordListAPIFn(this.currentTrHistoryObj);
        },
        cellDblclickSuppliesClaimHistoryFn({ row }) {
            this.currentTrHistoryObj = row;
            this.groupBtnFn('edit');
        },
        cellClickSuppliesClaimHistoryBotFn({ row }) {
            this.currentTrSalesObj = row;
        },
        cellDblclickSuppliesClaimHistoryBotFn({ row }) {
            this.currentTrSalesObj = row;
            this.groupBtnFn('detail');
        },
        customRowgreatSendModalFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentTrgreatSendObj = record;
                    },
                    dblclick: () => {
                        this.currentTrgreatSendObj = record;
                    }
                }
            };
        },
        // 获取期望时间
        getexpectDate(date, dateString) {
            this.editInfoFormObj.expect_date = dateString;
        },
        // 判断申领单的表格有数据没有 并且校验值
        checkApplySingleFn(btnName) {
            let flag = true;
            if (!this.editInfoFormObj.department_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择部门',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            if (!flag) return;
            const len = this.greatSendDatas.length;
            if (len <= 0) {
                if (flag && btnName !== 'cancel') {
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
            for (let i = 0; i < len; i++) {
                const item = this.greatSendDatas[i];
                if (flag && !item.approval_volumes) {
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
            }
            if (flag) {
                flag = this.checkDataUnitInnerSnFn(this.greatSendDatas, 'unit_inner_sn_new');
            }
            return flag;
        },
        // 申领单表格中的操作按钮
        applyDetailGroupBtnFn(btnName, record) {
            switch (btnName) {
                case 'remove': {
                    const xGreatSendPlanTable = this.$refs.xGreatSendPlanTable;
                    xGreatSendPlanTable.remove(record);
                    this.greatSendDatas = xGreatSendPlanTable.getTableData().tableData;
                    xGreatSendPlanTable.setCurrentRow(this.greatSendDatas[0]);
                    this.applyDetailRemoveResetFn();
                    break;
                }
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
        // 打印 - 取消 - 生成销售单 - 生成采购计划 按钮的操作
        greatSendModalGroupBtnFn(btnName) {
            if (btnName === 'great-save-plan' && !this.checkApplySingleFn(btnName)) return;
            this.btnModalName = btnName;
            switch (btnName) {
                case 'print': // 打印
                    break;
                case 'cancel': // 取消
                    this.greatSendDatas = [];
                    this.greatSendModal.alert = false;
                    break;
                case 'great-save-plan': // 生成采购计划
                    this.orderSaleCreatplanAPIFn();
                    break;
            }
        },
        checkDataUnitInnerSnFn(datas) {
            // 产品只能出现唯一
            let isRepeatObj = {},
                flag = true;
            for (let i = 0; i < datas.length; i++) {
                const item = datas[i],
                    key = `${item.product_type}==${item.product_inner_sn}==${item.specification_inner_sn}==${
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
            isRepeatObj = null;
            return flag;
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
        clickUnitListFn(record) {
            if (!record.isLoadingUnit && !this.isEditDisabled) {
                if (!record.detail_inner_sn || this.btnName === 'add') {
                    parseFloat(record.product_type) === 1
                        ? this.platformUnitTreeFn(record, 'add')
                        : this.dealerUnitTreeAPIFn(record, 'add');
                } else if (this.btnName === 'edit') {
                    parseFloat(record.product_type) === 1
                        ? this.platformUnitTreeFn(record)
                        : this.dealerUnitTreeAPIFn(record);
                }
            }
            this.record = record;
        },
        changeUnitListFn(value, selectedOptions) {
            // let name = ''
            // if (selectedOptions.length) {
            //     const selected = selectedOptions[selectedOptions.length - 1]
            //     name = selected.name || selected.label || ''
            // }
            // this.record.unit_name = name
            this.record.unit_name = selectedOptions.length ? selectedOptions[selectedOptions.length - 1].name : '';
            this.contractGetOrderPriceAPIFn(this.record);
        },
        displayUnitRender({ labels }) {
            let label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
        },
        clickUnitPlanListFn(record, clickName) {
            this.record = record;
            if (!clickName) {
                // 触发数量的数字
                this.setPackgeMultipleFn(record);
            }
        },
        // 设置批准发放量 * 包装单位的倍数
        setPackgeMultipleFn(record) {
            record = record || this.record;
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
            // record = record || this.record.unit_inner_sn_new
            // if (!record) return
            // const len = record.length,
            //     recordUnitSn = record[len - 1],
            //     curUnitSn = this.record.min_unit_inner_sn

            // for (let i = 0; i < this.greatSendDatas.length; i++) {
            //     const item = this.greatSendDatas[i]
            //     if (parseFloat(item.key) === parseFloat(this.record.key)) {
            //         if (parseFloat(recordUnitSn) === parseFloat(curUnitSn)) { // 包装单位 === 最小包装单位
            //             item.packge_multiple_num = `${item.approval_volumes}${item.min_unit}`
            //         } else {
            //             item.packge_multiple_num = `${item.approval_volumes * item.packge_multiple}${item.min_unit}`
            //         }
            //         // if (this.record.approval_volumes && len) {
            //         //     if (parseFloat(recordUnitSn) === parseFloat(curUnitSn)) { // 包装单位 === 最小包装单位
            //         //         item.packge_multiple_num = `${item.approval_volumes}${item.min_unit}`
            //         //     } else {
            //         //         item.packge_multiple_num = `${item.approval_volumes * item.packge_multiple}${item.min_unit}`
            //         //     }
            //         // } else {
            //         //     item.packge_multiple_num = ''
            //         // }
            //         break
            //     }
            // }
        },
        changePlanUnitListFn(value, selectedOptions) {
            const len = selectedOptions.length,
                unit = len ? selectedOptions[len - 1].label : '';
            this.greatSendDatas.forEach((item) => {
                if (item.key === this.record.key) {
                    item.unit = unit;
                    item.unit_name = unit;
                    this.$refs.xGreatSendPlanTable.refreshData();
                }
            });
            if (len) {
                const recordUnitSn = value[value.length - 1],
                    curUnitSn = this.record.min_unit_inner_sn;
                if (parseFloat(recordUnitSn) === parseFloat(curUnitSn)) {
                    this.setPackgeMultipleFn(this.record);
                } else {
                    parseFloat(this.record.product_type) === 1
                        ? this.platformUnitMinAPIFn(this.record, recordUnitSn)
                        : this.dealerUnitMinAPIFn(this.record, recordUnitSn);
                }
            }
        },
        changeExpectAogDateFn(date, dateString) {
            this.planModalForm.expect_aog_date = dateString;
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
            let label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
        },
        // 搜索按钮操作
        searchBtnFn(searchBtnName) {
            if (!this.checkSearchInfoFormMsgFn()) return;
            if (searchBtnName === 'general') {
                // 普通搜索
                this.searchTables.status = [];
            }
            this.initTablesPaginationFn();
            this.activeTabsKey === this.tabTypes[0] ? this.orderSaleListAPIFn() : this.orderHistorySaleListAPIFn();
        },
        checkSearchInfoFormMsgFn() {
            if (this.$Utils.checkInputValFn(this.searchTables.partA_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `相关机构${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (this.hightSearchObj.isArrow && this.$Utils.checkInputValFn(this.searchTables.order_sn)) {
                this.$warning({
                    title: '操作提示',
                    content: `单据编号${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 检测相关机构填值了没有
        checkPlanModalOrgSnFn() {
            let flag = false;
            if (this.planModalOrgSn.length <= 0) {
                flag = true;
                this.$warning({
                    title: '操作提示',
                    content: '请选择左上方的【相关机构】，再添加商品。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
            } else {
                this.partC.partB_id = this.$cookie.get('userbelong');
                this.partC.modalType = 4;
                flag = false;
            }
            return flag;
        },
        dblclickAddProductFn() {
            this.$nextTick(() => {
                // 双击
                if (this.$refs.applyTable.getElementsByClassName('ant-table-placeholder').length) {
                    this.$refs.applyTable
                        .getElementsByClassName('ant-table-placeholder')[0]
                        .addEventListener('dblclick', this.dblclickTableFn, false);
                }
            });
        },
        dblclickTableFn(e) {
            const filterDom = ['span', 'svg', 'path', 'input', 'i', 'li', 'ul'],
                flag = filterDom.some((item) => item === e.target.tagName.toLowerCase()),
                childrenFlag = filterDom.some(
                    (item) => item === e.target.children && e.target.children.tagName.toLowerCase()
                ),
                childrenFlagRemove = e.target.getElementsByClassName('action-btn-list-wrap').length;
            if (flag || childrenFlag || childrenFlagRemove) return;
            if (this.checkPlanModalOrgSnFn()) return;
            this.addProductMd.alert = true;
            let timer = setTimeout(() => {
                this.$Utils.globalDragCenterFn('add_product');
                clearTimeout(timer);
                timer = null;
            });
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        addProductFn() {
            this.$refs.chooseProMd.returnProList();
        },
        getProReturn(dea, ven) {
            // 获取回调商品
            this.addProductMd.alert = false;
            this.planModalPagination.loading = true;
            this.clearRowClassNameFn();
            const xTableAddInfo = this.$refs.xTablePlanModalInfo,
                oldAddDatas = [...ven, ...dea].map((item) => {
                    return {
                        ...item,
                        unitPriceList: []
                    };
                }),
                len = this.planModalDatas.length;
            let curObj = xTableAddInfo.getCurrentRow();
            if (!curObj) {
                for (let i = 0; i < len; i++) {
                    const item = this.planModalDatas[i];
                    if (item.temp_add_flag) {
                        if (!curObj) {
                            curObj = item;
                        }
                        delete item.temp_add_flag;
                    }
                }
                xTableAddInfo.reloadData(this.planModalDatas);
            }
            xTableAddInfo.clearCurrentRow();
            if (len) {
                for (let i = 0; i < len; i++) {
                    const item = this.planModalDatas[i];
                    if (item._XID === curObj._XID) {
                        this.planModalDatas.splice(i, 0, ...oldAddDatas);
                        xTableAddInfo.refreshData();
                        xTableAddInfo.loadData(this.planModalDatas);
                        break;
                    }
                }
            } else {
                // 完全新增
                this.planModalDatas.push(...oldAddDatas);
                xTableAddInfo.loadData(this.planModalDatas);
            }
            for (let i = 0; i < this.planModalDatas.length; i++) {
                let item = this.planModalDatas[i];
                if (item.unitPriceList.length <= 0) {
                    // 是草稿和正式
                    this.contractGetOrderPriceAPIFn(item);
                }
            }
            let timer = setTimeout(() => {
                this.planModalPagination.loading = false;
                clearTimeout(timer);
                timer = null;
            }, 200);
        },
        rowClassNameFn({ row }) {
            if (row.row_add_flag) {
                return 'row--current';
            }
        },
        clearRowClassNameFn() {
            // 去除新增的临时背景颜色
            this.planModalDatas.forEach((item) => {
                if (item.row_add_flag) {
                    item.row_add_flag = false;
                }
            });
        },
        // 顶部按钮的事件
        groupBtnFn(btnName) {
            this.btnName = btnName;
            this.resetPlanModalFn();
            if (this.btnName === 'add' || this.btnName === 'edit') {
                // const cookie = this.$cookie
                // this.orderOrgListAPIFn(cookie.get('userSystemType'), cookie.get('userbelong'))
            }
            if (this.btnName !== 'add' && !this.checkSecltedTrFn()) return;
            switch (btnName) {
                case 'add': // 新增
                    this.planModalForm.creator_name = JSON.parse(this.$cookie.get('EmployeeInfo')).Name;
                    this.infoAllData.organization_name = '';
                    this.planModal = {
                        title: `${this.$route.meta.title} - 新增`,
                        alert: true,
                        loading: false
                    };
                    this.$Utils.globalDragCenterFn('modal-add');
                    this.dblclickAddProductFn();
                    break;
                case 'edit': // 修改
                    this.planModal = {
                        title: `${this.$route.meta.title} - 修改`,
                        alert: true,
                        loading: false
                    };
                    this.$Utils.globalDragCenterFn('modal-add');
                    break;
                case 'remove': // 删除
                    this.orderDelAPIFn();
                    break;
                case 'submit': // 确认
                    this.orderConfirmAPIFn();
                    break;
                case 'stop': // 终止
                    this.orderStopAPIFn();
                    break;
                case 'detail': // 销售订单明细
                    this.infoModal = {
                        title: `${this.$route.meta.title} - 查看`,
                        alert: true,
                        loading: false
                    };
                    this.$Utils.globalDragCenterFn('modal-sales-order-detail');
                    this.orgDeptTopTreeAPIFn(); // 销售部门列表
                    this.orgEmpDropdownListAPIFn(); // 销售人员列表
                    this.saleInfoFn();
                    break;
            }
        },
        checkSecltedTrFn() {
            let flag = true;
            if (this.activeTabsKey === this.tabTypes[0] && Object.keys(this.currentTrObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
            } else if (this.activeTabsKey === this.tabTypes[1] && Object.keys(this.currentTrHistoryObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
            }

            if (!flag) return flag;

            const isOrderPage = 1; // 判断是 0-采购订单  1-销售订单 1【自建】
            switch (this.btnName) {
                case 'submit': {
                    let contentSubmit = '只能确认【已审核】的状态。',
                        contentMySelf = '不能确认自己创建的订单。';
                    if (this.activeTabsKey === this.tabTypes[0]) {
                        if (flag) {
                            if (this.currentTrObj.status !== 2) {
                                flag = false;
                                this.$warning({
                                    title: '操作提示',
                                    content: contentSubmit,
                                    okText: '知道了',
                                    centered: true,
                                    maskClosable: false
                                });
                                break;
                            }
                            if (flag && this.currentTrObj.create_organization_type === isOrderPage) {
                                flag = false;
                                this.$warning({
                                    title: '操作提示',
                                    content: contentMySelf,
                                    okText: '知道了',
                                    centered: true,
                                    maskClosable: false
                                });
                                break;
                            }
                        }
                    } else if (this.activeTabsKey === this.tabTypes[1]) {
                        if (flag) {
                            if (this.currentTrHistoryObj.status !== 2) {
                                flag = false;
                                this.$warning({
                                    title: '操作提示',
                                    content: contentSubmit,
                                    okText: '知道了',
                                    centered: true,
                                    maskClosable: false
                                });
                                break;
                            }
                            if (flag && this.currentTrHistoryObj.create_organization_type === isOrderPage) {
                                flag = false;
                                this.$warning({
                                    title: '操作提示',
                                    content: contentMySelf,
                                    okText: '知道了',
                                    centered: true,
                                    maskClosable: false
                                });
                                break;
                            }
                        }
                    }
                    break;
                }
                case 'stop': {
                    let content = '只能终止【已审核/已确认】的状态。';
                    if (this.activeTabsKey === this.tabTypes[0]) {
                        if (flag && !(this.currentTrObj.status === 2 || this.currentTrObj.status === 3)) {
                            flag = false;
                            this.$warning({
                                title: '操作提示',
                                content: content,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            break;
                        }
                    } else {
                        if (flag && !(this.currentTrHistoryObj.status === 2 || this.currentTrHistoryObj.status === 3)) {
                            flag = false;
                            this.$warning({
                                title: '操作提示',
                                content: content,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            break;
                        }
                    }
                    break;
                }
                case 'remove': {
                    let contentRemove = '只能删除【正式/草稿】状态。',
                        contentMySelfRemove = '只能删除自建的订单。';
                    if (this.activeTabsKey === this.tabTypes[0]) {
                        if (flag && parseFloat(this.currentTrObj.create_organization_type) !== isOrderPage) {
                            flag = false;
                            this.$warning({
                                title: '操作提示',
                                content: contentMySelfRemove,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            return;
                        }
                        if (flag && !(this.currentTrObj.status === 0 || this.currentTrObj.status === 1)) {
                            flag = false;
                            this.$warning({
                                title: '操作提示',
                                content: contentRemove,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                        }
                    } else {
                        if (flag && parseFloat(this.currentTrHistoryObj.create_organization_type) !== isOrderPage) {
                            flag = false;
                            this.$warning({
                                title: '操作提示',
                                content: contentMySelfRemove,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            return;
                        }
                        if (flag && !(this.currentTrHistoryObj.status === 0 || this.currentTrHistoryObj.status === 1)) {
                            flag = false;
                            this.$warning({
                                title: '操作提示',
                                content: contentRemove,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                        }
                    }
                    break;
                }
                case 'edit': {
                    if (this.activeTabsKey === this.tabTypes[0]) {
                        this.orderSaleInfoAPIFn({
                            partA_id: this.currentTrObj.partA,
                            partB_id: this.currentTrObj.partB,
                            po_so_inner_sn: this.currentTrObj.po_so_inner_sn,
                            billsType: this.currentTrObj.billsType
                        });
                    } else if (this.activeTabsKey === this.tabTypes[1]) {
                        this.orderSaleInfoAPIFn({
                            partA_id: this.currentTrHistoryObj.partA,
                            partB_id: this.currentTrHistoryObj.partB,
                            po_so_inner_sn: this.currentTrHistoryObj.po_so_inner_sn,
                            billsType: this.currentTrHistoryObj.billsType
                        });
                    }
                }
            }
            return flag;
        },
        // 点击高级搜索的小图标的事件
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
            if (this.hightSearchObj.isArrow && this.architectureAllTrees.length <= 0) {
                this.orgDeptTopTreeAPIFn();
            }
            if (this.hightSearchObj.isArrow) {
                this.tabTop -= 60;
                this.tabTopHand -= 60;
            } else {
                this.tabTop += 60;
                this.tabTopHand += 60;
            }
            try {
                document.getElementsByClassName('boxtop')[0].style.height = this.tabTop + 55 + 'px';
                document.getElementsByClassName('handleBoxtop')[0].style.height = this.tabTopHand + 55 + 'px';
            } catch (e) {
                // ...
            }
        },
        // 右上角刷新按钮
        refreshPageFn() {
            this.searchTables = {
                partB_id: '',
                partA_name: '',
                order_sn: '',
                status: ''
            };
            if (this.activeTabsKey === this.tabTypes[0]) {
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
                this.orderSaleListAPIFn();
            } else if (this.activeTabsKey === this.tabTypes[1]) {
                this.suppliesClaimHistoryPagination = {
                    ...this.suppliesClaimHistoryPagination,
                    loading: true,
                    total: 10,
                    current: 1,
                    // pageSize: 10,
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                };
                this.orderHistorySaleListAPIFn();
            }
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

            if (isAppendDatas) {
                switch (isAppendDatas) {
                    case 'deptTopTree': // 所属部门
                        this.superiorDepartments = datasOriginNew;
                        this.architectureAllTrees = datasOriginNew;
                        // if (this.btnName === 'add' || this.btnName === 'edit' || this.btnName === 'copy') {
                        //     const len = this.architectureAllTrees.length
                        //     this.editInfoFormDepartmentInnerSn = len ? [this.architectureAllTrees[0].department_inner_sn] : []
                        //     this.editInfoFormObj.department_inner_sn = this.editInfoFormDepartmentInnerSn[0]
                        //     this.editInfoFormObj.department_name = len ? this.architectureAllTrees[0].name : ''
                        // }
                        break;
                    case 'allEmpDropdownList': // 经手人
                        this.handlerNameList = datasOriginNew;
                        // if (this.btnName === 'add') { // 默认赋值
                        //     if (this.handlerNameList.length) {
                        //         this.planModalForm.seller_inner_sn = this.handlerNameList[0].value
                        //         this.planModalForm.seller_name = this.handlerNameList[0].label
                        //     } else {
                        //         this.planModalForm.seller_inner_sn = ''
                        //         this.planModalForm.seller_name = ''
                        //     }
                        // }
                        break;
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
        rowClassPlanModalFn(record) {
            if (record.index === this.currentTrPlanModalObj.index) {
                return 'active-tr';
            }
            return '';
        },
        customRowPlanModalFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentTrPlanModalObj = record;
                    },
                    dblclick: (e) => {
                        this.currentTrPlanModalObj = record;
                        if (this.isEditDisabled || !(this.btnName === 'edit' || this.btnName === 'add')) return;
                        this.dblclickTableFn(e);
                    }
                }
            };
        },
        cellAddInfoClickFn({ row }) {
            this.currentTrPlanModalObj = row;
        },
        cellAddInfoDblclickFn({ row }, e) {
            this.currentTrPlanModalObj = row;
            if (this.isEditDisabled || !(this.btnName === 'edit' || this.btnName === 'add')) return;
            this.dblclickTableFn(e);
        },
        cellGreatSendSaleClickFn({ row }) {
            this.currentTrPlanModalObj = row;
        },
        cellGreatSendSaleDblclickFn({ row }, e) {
            this.currentTrPlanModalObj = row;
            if (this.isEditDisabled || !(this.btnName === 'edit' || this.btnName === 'add')) return;
            this.dblclickTableFn(e);
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
            this.toggleSearchTabsFn();
            if (!this.isActiveTabsKey) {
                this.isActiveTabsKey = true;
                this.orderHistorySaleListAPIFn();
            }
            // 切换tab时重置搜索状态
            this.searchTables.status = [];
            this.orderSaleListAPIFn();
        },
        toggleSearchTabsFn() {
            if (this.activeTabsKey === this.tabTypes[0]) {
                // 待申领： 0-1-2-3-6 状态启用  4-5 状态禁用
                this.searchTablesStatus.forEach((item) => {
                    if (
                        item.value === 0 ||
                        item.value === 1 ||
                        item.value === 2 ||
                        item.value === 3 ||
                        item.value === 6
                    ) {
                        item.disabled = false;
                    } else {
                        item.disabled = true;
                    }
                });
            } else {
                // 待申领： 0-1-2 状态禁用  3-4 状态启用
                this.searchTablesStatus.forEach((item) => {
                    if (item.value === 4 || item.value === 5) {
                        item.disabled = false;
                    } else {
                        item.disabled = true;
                    }
                });
            }
        },
        // 底部待发放表格选中项发生变化时的回调
        suppliesClaimBotSelectChangeFn(selectedRowKeys) {
            this.suppliesClaimBotSelectedRowKeys = selectedRowKeys;
        },
        changePlanModalOrgSnFn(value, label) {
            this.partC.partB_id = this.$cookie.get('userbelong');
            const arySplitValue = value.split('-');
            this.partC.partA_id = arySplitValue[0];
            this.planModalForm.partA = arySplitValue[0];
            this.billsType = arySplitValue[1];
            this.partC.billsType = arySplitValue[1];

            if (this.userSystemType === 2) {
                // 经销商对经销商 医院对经销商 => 把机构名称读取
                if (this.billsType === BillsType.DealerDealer || this.billsType === BillsType.HospitalDealer) {
                    this.partC.oname = this.orgName;
                } else {
                    this.partC.oname = label[label.length - 1];
                }
            } else if (this.userSystemType === 3) {
                // 经销商对厂商 医院对厂商 => 把机构名称读取
                if (this.billsType === BillsType.DealerVendor || this.billsType === BillsType.HospitalVendor) {
                    this.partC.oname = this.orgName;
                } else {
                    this.partC.oname = label[label.length - 1];
                }
            }
            // this.planModalForm.partA = ''
            // this.partC.partA_id = ''
            // this.billsType = ''
            // this.partC.billsType = ''
            // this.partC.oname = ''
            this.planModalForm.billsType = this.partC.billsType;
            if (this.btnName === 'add') {
                this.planModalDatas = [];
                this.$refs.xTablePlanModalInfo && this.$refs.xTablePlanModalInfo.remove();
            }
        },
        // 仅显示待发放的产品细目
        changeOnlyUnGrantFn() {
            this.filterSearchBotTablesFn();
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
                this.activeTabsKey = this.tabTypes[0];
                this.orderSaleListAPIFn();
                this.orderHistorySaleListAPIFn(this.currentTrObj);
            }
            this.isApplyDetailStopFlag = false;
        },
        // 只有当前选中父级的已确认状态才能操作生成销售单和生成采购计划 终止明细
        checkParentListStatusFn() {
            let flag = true;
            if (parseFloat(this.currentTrObj.status) !== 3) {
                // 3 - 父级列表的已确认状态
                this.$warning({
                    title: '操作提示',
                    content: '只有【已确认】的状态可以进行该操作。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
            }
            return flag;
        },
        // 申领终止按钮的状态
        applyStopBtnFn(btnName, record) {
            const _this = this;
            if (!_this.checkParentListStatusFn()) return;
            _this.$confirm({
                title: '终止',
                content: '确认要终止吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.orderDetailStopAPIFn(record, btnName);
                },
                onCancel() {}
            });
        },
        // 生成销售单校验库存为0，提醒消息框
        checkoutGreatSendInventoryFn() {
            let isgo = true;
            for (let i = 0; i < this.suppliesClaimBotSelectedRowKeys.length; i++) {
                const item = this.suppliesClaimBotSelectedRowKeys[i];
                //  content: `库存为 ${item.ware_quantity}，确认继续生成销售单吗？`,
                // 新规则 待售数量大于可用库存    //all_quantity
                if (item.stay_quantity > item.unlock_inventory) {
                    // item.inventory <= 0  content: '库存为0 ，确认继续生成销售单吗？',
                    const _this = this;
                    isgo = false;
                    _this.$confirm({
                        title: '提示',
                        content: '库存数量不足，确认继续生成销售单吗？',
                        centered: true,
                        maskClosable: false,
                        onOk() {
                            _this.greatSendInventoryFn();
                        },
                        onCancel() {}
                    });
                    break;
                }
            }
            if (isgo) this.greatSendInventoryFn();
        },
        greatSendInventoryFn() {
            this.planModalDatas = this.details;
            this.record = this.planModalDatas[0];
            this.orgDeptTopTreeAPIFn();
            this.orgEmpDropdownListAPIFn();
            this.greatSendPagination.loading = true;
            if (this.planModalDatas.length) {
                this.planModalDatas.forEach((item) => {
                    // 默认将数量设置为计算过的可售数量
                    item.quantity = isNaN(Number(item.approval_volumes)) ? 0 : Number(item.approval_volumes);
                    // 生成包装单位
                    this.contractGetOrderPriceAPIFn(item);
                    parseFloat(item.product_type) === 1
                        ? this.platformUnitTreeFn(item)
                        : this.dealerUnitTreeAPIFn(item); // 生成销售单 获取产品的包装单位数据
                });
                this.currentTrgreatSendObj = this.planModalDatas[0];
            } else {
                this.currentTrgreatSendObj = {};
            }
            this.greatSendModal.title = `${this.$route.meta.title} - 生成销售单`;
            this.planSendModal.alert = true;
            this.$Utils.globalDragCenterFn('modal-add-send');
            this.greatSendPagination.loading = false;
            this.$nextTick(() => {
                this.$refs.xGreatSendSaleTable.setCurrentRow(this.greatSendDatas[0]);
            });
        },
        // 生成销售单 - 生成采购计划的按钮
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
                this.resetPlanModalFn();
                this.formatGreatSendDatas(btnName);
                this.grantSingleBtnName = btnName;
                switch (btnName) {
                    case 'great-send': // 生成销售单
                        if (this.checkGrandSendPlanFn(btnName)) {
                            if (this.systemType === 2) {
                                this.checkoutGreatSendInventoryFn();
                            } else {
                                this.greatSendInventoryFn();
                            }
                        }
                        break;
                    case 'great-plan': // 生成采购计划单
                        this.greatSendDatas = this.details;
                        this.record = this.greatSendDatas[0];
                        if (!this.checkGrandSendPlanFn(btnName)) return;
                        this.orgDeptTopTreeAPIFn();
                        this.greatSendPagination.loading = true;
                        if (this.greatSendDatas.length) {
                            this.greatSendDatas.forEach((item) => {
                                parseFloat(item.product_type) === 1
                                    ? this.platformUnitTreeFn(item, 'great-plan')
                                    : this.dealerUnitTreeAPIFn(item, 'great-plan');
                            });
                            this.currentTrgreatSendObj = this.greatSendDatas[0];
                        } else {
                            this.currentTrgreatSendObj = {};
                        }
                        this.greatSendModal.title = `${this.$route.meta.title} - 生成采购计划单`;
                        this.greatSendModal.alert = true;
                        this.$Utils.globalDragCenterFn('modal-great-send');
                        this.greatSendPagination.loading = false;
                        this.$nextTick(() => {
                            this.$refs.xGreatSendPlanTable.setCurrentRow(this.greatSendDatas[0]);
                        });
                        break;
                }
            }
        },
        // 是否可发放 - 生成采购计划的按钮状态校验
        checkGrandSendPlanFn(btnName) {
            let flag = true;
            if (!this.checkParentListStatusFn()) return;
            const datas = this.$refs.xSuppliesClaimBotTable.getSelectRecords();
            if (btnName === 'great-send') {
                for (let i = 0; i < datas.length; i++) {
                    const item = datas[i],
                        status = parseFloat(item.status);
                    if (flag && !(status === 0 || status === 2)) {
                        this.$warning({
                            title: '操作提示',
                            content: '只有【处理中/已计划采购】的状态可以生成销售单。',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                }
            } else if (btnName === 'great-plan') {
                for (let i = 0; i < datas.length; i++) {
                    const item = datas[i],
                        status = parseFloat(item.status);
                    if (status !== 0) {
                        // 下方表格的子状态是处理中
                        this.$warning({
                            title: '操作提示',
                            content: '只有【处理中】的状态可以生成采购计划。',
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
        // 生成销售单/生成采购计划 勾选页面中的数据details
        formatGreatSendDatas(btnName) {
            this.btnNameSendOrderSale = btnName;
            this.planModalForm = { ...this.currentTrObj };
            this.planModalOrgs = [{ value: this.currentTrObj.partA, label: this.currentTrObj.partA_name }];
            this.planModalOrgSn = this.currentTrObj.partA_name;
            this.details = [];
            this.suppliesClaimBotDatas.forEach((item, index) => {
                this.suppliesClaimBotSelectedRowKeys.forEach((cItem) => {
                    if (parseFloat(item.detail_inner_sn) === parseFloat(cItem.detail_inner_sn)) {
                        cItem = {
                            ...item,
                            index,
                            key: item.detail_inner_sn,
                            activeClass: '',
                            unit_name: item.unit,
                            unitList: item.unit_inner_sn ? [{ value: item.unit_inner_sn, label: item.unit }] : [],
                            unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                            isLoadingUnit: false, // 是否加载过整棵树
                            loading: false,
                            switch:
                                (this.planModalForm.status === 2 || this.planModalForm.status === 3) &&
                                (item.status === 0 || item.status === 2),
                            // approval_volumes: item.quantity, // 生成采购计划单
                            packge_multiple_num: '', // 生成采购计划单
                            mdrf_sn: item.mdrf_sn ? item.mdrf_sn : '',
                            mdrf_inner_sn: item.mdrf_inner_sn ? item.mdrf_inner_sn : ''
                        };
                        if (parseFloat(item.unit_inner_sn) === parseFloat(item.min_unit_inner_sn)) {
                            // 申领量 包装单位 === 最小包装单位 不要显示括号的最小包装单位
                            const newStillNeedVolumes =
                                item.quantity - item.sold_quantity > 0 ? item.quantity - item.sold_quantity : 0;
                            cItem.approval_volumes = `${newStillNeedVolumes}`; // 点击生成采购计划的数量
                        } else {
                            let appronum = item.quantity * item.min_unit_quantity - item.sold_quantity;
                            if (appronum < 0) appronum = 0;
                            cItem.approval_volumes = appronum; // 点击生成采购计划的数量 待售数量-可用库存
                        }
                        this.details.push(cItem);
                    }
                });
            });
            // this.details.push({
            //     key: Math.random(),
            //     index: this.details.length,
            //     activeClass: ''
            // })
        },
        // 校验销售订单
        checkPlanModalFormFn() {
            let flag = true;
            if (this.planModalDatas.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请双击表格内容部分添加产品',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            if (!this.planModalForm.partA) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择相关机构',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            // if (!this.planModalForm.so_sn) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '请输入单据编号',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     flag = false
            //     return
            // }
            // 判断表格主体的数量 - 包装单位
            const datas = this.planModalDatas;
            if (!datas.every((item) => item.quantity > 0 && item.unit_inner_sn.length)) {
                this.$warning({
                    title: '操作提示',
                    content: '请填写数量/包装单位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }

            if (flag) {
                for (let i = 0; i < datas.length; i++) {
                    const item = datas[i],
                        len = 3, // 小数的有效长度是2位
                        quantityNew = (item.quantity + '').split('.'),
                        quantityLen = quantityNew.length >= 2 ? quantityNew[quantityNew.length - 1].length : 0,
                        unitPriceNew = (item.tax_unit_price + '').split('.'),
                        unitPriceLen = unitPriceNew.length >= 2 ? unitPriceNew[unitPriceNew.length - 1].length : 0,
                        discountNew = (item.discount + '').split('.'),
                        discountLen = discountNew.length >= 2 ? discountNew[discountNew.length - 1].length : 0;
                    // rateNew = (item.rate + '').split('.'),
                    // rateLen = rateNew.length >= 2 ? rateNew[rateNew.length - 1].length : 0
                    if (quantityLen > len) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】数量的小数长度只能是三位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (unitPriceLen > 4) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】含税单价的小数长度只能是四位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (discountLen > len) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】折扣的小数长度只能是两位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (this.$Utils.checkFloatNumFn(item.rate)) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】税率只能是0-100间的整数`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    flag = this.checkDataUnitInnerSnFn(datas, 'unit_inner_sn');
                }
            }

            if (!flag) return;
            if (flag && this.btnName === 'edit' && this.checkEditInfoFormObjFn()) {
                // 校验产品列表有没有改过
                flag = true;
            }
            if (flag) {
                this.detailList = datas.map((item) => {
                    return {
                        detail_inner_sn: item.detail_inner_sn || '',
                        product_vendor_inner_sn:
                            item.product_vendor_inner_sn ||
                            item.vendor_inner_sn ||
                            item.dealer_inner_sn ||
                            item.dealer_b_inner_sn,
                        product_vendor_name: item.product_vendor_name || item.vendor_name || item.dealer_name,
                        product_inner_sn: item.product_inner_sn,
                        product_name: item.product_name,
                        specification_inner_sn: item.specification_inner_sn,
                        specification: item.specification,
                        unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                        unit_name: item.unit_name,
                        product_type: item.product_type,
                        quantity: item.quantity,
                        // unit_price: item.unit_price,
                        discount: item.discount,
                        rate: item.rate,
                        sort_number: item.index,
                        batch_number: item.batch_number,
                        unit_price:
                            typeof item.tax_unit_price === 'undefined' ? 0 : parseFloat(item.tax_unit_price).toFixed(4) // 传递给后端的字段取值变为 【单价 = 含税单价   含税单价 = 单价】
                    };
                });
                flag = true;
            }
            return flag;
        },
        changeHandlerNameSnFn(value, label) {
            this.planModalForm.seller_name = label.length ? label[label.length - 1] : '';
        },
        changeDepartNameSnFn(value, label) {
            this.planModalForm.department_name = label.length ? label[label.length - 1] : '';
        },
        changeDepartNameSnPlanFn(value, label) {
            this.editInfoFormObj.department_name = label.length ? label[label.length - 1] : '';
        },
        // 创建后端所需的list格式数据
        createServiceListFn() {
            let list = [];
            if (Object.keys(this.currentTrObj).length) {
                list.push({
                    partA_id: this.currentTrObj.partA,
                    partB_id: this.currentTrObj.partB,
                    po_so_inner_sn: this.currentTrObj.po_so_inner_sn,
                    billsType: this.currentTrObj.billsType
                });
            }
            return list;
        },
        // 外层表格的loading关闭
        wrapTableCloseLoadingFn() {
            if (this.activeTabsKey === this.tabTypes[0]) {
                this.suppliesClaimPagination.loading = false;
            } else if (this.activeTabsKey === this.tabTypes[1]) {
                this.suppliesClaimHistoryPagination.loading = false;
            }
        },
        // 关闭采购计划单模态框
        closePlanModalFn() {
            this.planModal = {
                title: `${this.$route.meta.title} - 新增`,
                alert: false,
                loadingPrint: false,
                loadingAdd: false,
                loadingSaveDraft: false,
                loadingSave: false,
                loadingAudit: false,
                loadingExportExcel: false
            };
            this.planSendModal = {
                title: `${this.$route.meta.title} - 生成销售单`,
                alert: false,
                loadingPrint: false,
                loadingAdd: false,
                loadingSaveDraft: false,
                loadingSave: false,
                loadingAudit: false,
                loadingExportExcel: false
            };
        },
        // 重置采购计划单模态框
        resetPlanModalFn() {
            this.expectAogDate = '';
            this.planModalOrgSn = [];
            this.planModalDatas = [];
            this.planModalEmployeSn = [];
            this.planModalDepartmentSn = [];
            this.planModalEmployes = [];
            this.planModalDepartmentSns = [];
            this.detailList = [];
            this.editInfoFormNewObj = {};
            this.planModalSelectedRowKeys = [];
            this.currentTrPlanModalObj = {};
            this.superiorDepartments = [];
            this.handlerNameList = [];

            this.planModalForm.buyer_id = '';
            this.planModalForm.buyer_id_old = '';
            this.planModalForm.buyer_name = '';
            this.planModalForm.department_inner_sn = '';
            this.planModalForm.department_inner_sn_old = '';
            this.planModalForm.department_name = '';
            this.planModalForm.description = '';
            this.planModalForm.description_old = '';
            this.planModalForm.details = '';
            this.planModalForm.expect_aog_date = '';
            this.planModalForm.expect_aog_date_old = '';
            this.planModalForm.billsType = '';
            this.planModalForm.partA_id = '';
            this.planModalForm.partB_id = '';
            this.planModalForm.po_sn = '';
            this.planModalForm.po_sn_old = '';
            this.planModalForm.po_so_inner_sn = '';
            this.planModalForm.status = '';
            this.planModalForm.collection_date = '';
            this.planModalForm.delivery_address = '';
            this.planModalForm.delivery_date = '';
            this.planModalForm.department_id = '';
            this.planModalForm.org_id = '';
            this.planModalForm.organization_inner_sn = '';
            this.planModalForm.organization_name = '';
            this.planModalForm.purchaser_linkman = '';
            this.planModalForm.purchaser_phone = '';
            this.planModalForm.seller_inner_sn = '';
            this.planModalForm.seller_linkman = '';
            this.planModalForm.seller_name = '';
            this.planModalForm.seller_phone = '';
            this.planModalForm.sl_s = '';

            this.editInfoFormObj = {};
        },
        // 订单表格的操作
        planModalActionBtnFn(btnName, record) {
            switch (btnName) {
                // 删除
                case 'remove': {
                    if (this.isEditDisabled) return;
                    if (typeof record.detail_inner_sn !== 'undefined' && !this.removeSelfCreateOrganizationTypeFn()) {
                        this.$warning({
                            title: '操作提示',
                            content: '只能删除自建的产品',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        return;
                    }
                    const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
                    xTablePlanModalInfo.remove(record);
                    this.planModalDatas = xTablePlanModalInfo.getTableData().tableData;
                    xTablePlanModalInfo.setCurrentRow(this.planModalDatas[0]);
                    this.dblclickAddProductFn();
                    break;
                }
                case 'stop': {
                    record.loading = true;
                    record.switch ? this.orderDetailStopAPIFn(record) : this.orderDetailUnstopAPIFn(record);
                    break;
                }
            }
        },
        // 只能删除自建的产品
        removeSelfCreateOrganizationTypeFn() {
            return parseFloat(this.planModalForm.create_organization_type) === 1; // 采购单： 0 - 自建   1 - 买家    销售单： 0 - 买家   1 - 自建
        },
        planModalActionBtnSendFn(btnName, record) {
            if (btnName === 'remove') {
                const xGreatSendSaleTable = this.$refs.xGreatSendSaleTable;
                xGreatSendSaleTable.remove(record);
                this.planModalDatas = xGreatSendSaleTable.getTableData().tableData;
                xGreatSendSaleTable.setCurrentRow(this.planModalDatas[0]);
            }
        },
        checkPlanModalFormSendFn() {
            let flag = true;
            if (this.planModalDatas.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请勾选产品，再进行操作。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            // if (!this.planModalForm.so_sn) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '请输入单据编号',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     flag = false
            //     return
            // }
            if (!this.planModalForm.seller_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择销售员',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            if (!this.planModalForm.department_id) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择销售部门',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            // if (this.planModalForm.purchaser_phone && !this.$Utils.regExpFn().mobilePhone.test(this.planModalForm.purchaser_phone)) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '采购方联系电话格式输入有误',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     flag = false
            //     return
            // }
            // if (this.planModalForm.seller_phone && !this.$Utils.regExpFn().mobilePhone.test(this.planModalForm.seller_phone)) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '销售方联系电话格式输入有误',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     flag = false
            //     return
            // }
            // 判断表格主体的数量 - 包装单位
            const datas = this.planModalDatas;
            if (!datas.every((item) => item.quantity && item.unit_inner_sn.length)) {
                this.$warning({
                    title: '操作提示',
                    content: '请填写数量/包装单位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }

            if (flag) {
                for (let i = 0; i < datas.length; i++) {
                    const item = datas[i],
                        len = 3, // 小数的有效长度是2位
                        quantityNew = (item.quantity + '').split('.'),
                        quantityLen = quantityNew.length >= 2 ? quantityNew[quantityNew.length - 1].length : 0,
                        unitPriceNew = (item.tax_unit_price + '').split('.'),
                        unitPriceLen = unitPriceNew.length >= 2 ? unitPriceNew[unitPriceNew.length - 1].length : 0,
                        discountNew = (item.discount + '').split('.'),
                        discountLen = discountNew.length >= 2 ? discountNew[discountNew.length - 1].length : 0,
                        rateNew = (item.rate + '').split('.'),
                        rateLen = rateNew.length >= 2 ? rateNew[rateNew.length - 1].length : 0;
                    if (quantityLen > len) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】数量的小数长度只能是三位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (unitPriceLen > 4) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】含税单价的小数长度只能是四位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (discountLen > len) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】折扣的小数长度只能是两位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (rateLen > len) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】税率的小数长度只能是两位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                this.detailList = datas.map((item) => {
                    return {
                        detail_inner_sn: item.detail_inner_sn || '',
                        product_vendor_inner_sn:
                            item.product_vendor_inner_sn || item.vendor_inner_sn || item.dealer_inner_sn,
                        product_vendor_name: item.product_vendor_name || item.vendor_name || item.dealer_name,
                        product_inner_sn: item.product_inner_sn,
                        product_name: item.product_name,
                        specification_inner_sn: item.specification_inner_sn,
                        specification: item.specification,
                        unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                        unit: item.unit_name,
                        product_type: item.product_type,
                        quantity: item.quantity,
                        // unit_price: item.unit_price,
                        discount: item.discount,
                        rate: item.rate / 100,
                        sort_number: item.index,
                        mdrf_sn: item.mdrf_sn,
                        mdrf_inner_sn: item.mdrf_inner_sn,
                        batch_number: item.batch_number,
                        min_unit_for_sale_quantity: item.min_unit_for_sale_quantity,
                        unit_price:
                            typeof item.tax_unit_price === 'undefined' ? 0 : parseFloat(item.tax_unit_price).toFixed(4), // 传递给后端的字段取值变为 【单价 = 含税单价   含税单价 = 单价】
                        health_care_sn: item.health_care_sn,
                        sun_sn: item.sun_sn
                    };
                });
            }
            return flag;
        },
        planModalGroupBtnSendFn(btnName) {
            if (!(btnName === 'print' || btnName === 'cancel') && !this.checkPlanModalFormSendFn(btnName)) return;
            switch (btnName) {
                case 'print': // 打印
                    break;
                case 'cancel': // 取消
                    this.closePlanModalFn();
                    this.resetPlanModalFn();
                    break;
                case 'saveDraft': // 存草稿
                    this.planSendModal.loadingSaveDraft = true;
                    this.orderSaleBuildAPIFn(0);
                    break;
                case 'save': // 保存
                    this.planSendModal.loadingSave = true;
                    this.orderSaleBuildAPIFn(1);
                    break;
                case 'savePo':
                    // if (!this.planModalForm.so_sn) {
                    //     this.$warning({
                    //         title: '操作提示',
                    //         content: '请填写右上角的单据编号再保存。',
                    //         okText: '知道了',
                    //         centered: true,
                    //         maskClosable: false
                    //     })
                    //     return false
                    // }
                    this.planSendModal.loadingSave = true;
                    this.orderSaleBuildAPIFn(1);
                    break;
                case 'audit': // 审核
                    this.planSendModal.loadingAudit = true;
                    this.orderSaleBuildAPIFn(2);
                    break;
            }
        },
        // 订单表格底部的按钮操作
        planModalGroupBtnFn(btnName) {
            if (
                !(btnName === 'print' || btnName === 'cancel' || btnName === 'savePo') &&
                !this.checkPlanModalFormFn(btnName)
            )
                return;
            switch (btnName) {
                case 'print': // 打印
                    break;
                case 'add': // 生成销售单
                    if (this.planModal.loadingAdd) {
                        return;
                    }
                    this.planModal.loadingAdd = true;
                    this.orderSaleDetailBuildAPIFn();
                    break;
                case 'cancel': // 取消
                    this.closePlanModalFn();
                    this.resetPlanModalFn();
                    break;
                case 'saveDraft': // 存草稿
                    if (this.planModal.loadingSaveDraft) {
                        return;
                    }
                    this.planModal.loadingSaveDraft = true;
                    if (this.btnName === 'add') {
                        this.orderSaleAddAPIFn(0);
                    } else {
                        this.orderSaleEditAPIFn(0);
                    }
                    break;
                case 'save': // 保存
                    if (this.planModal.loadingSave) {
                        return;
                    }
                    this.planModal.loadingSave = true;
                    if (this.btnName === 'add') {
                        this.orderSaleAddAPIFn(1); // 保存正式
                    } else {
                        // 单据来源为买方且状态为已审核的销售订单可以编辑 状态保存为3(确认)
                        const status = this.isSourceBuyerAudited ? 3 : 1;
                        this.orderSaleEditAPIFn(status);
                    }
                    break;
                case 'savePo': // 保存右上角的单据编号
                    // if (!this.planModalForm.so_sn) {
                    //     this.$warning({
                    //         title: '操作提示',
                    //         content: '请填写右上角的单据编号再保存。',
                    //         okText: '知道了',
                    //         centered: true,
                    //         maskClosable: false
                    //     })
                    //     return false
                    // }
                    if (this.planModal.loadingSave) {
                        return;
                    }
                    this.planModal.loadingSave = true;
                    this.orderSaleConfirmAPIFn();
                    break;
                case 'audit': // 审核
                    if (this.planModal.loadingAudit) {
                        return;
                    }
                    this.planModal.loadingAudit = true;
                    this.orderSaleEditAPIFn(2);
                    break;
            }
        },
        // 已处理 - 销售单详情modal按钮组
        infoModalGroupBtnSendFn(btnName) {
            switch (btnName) {
                // 取消
                case 'cancel':
                    this.infoModal = {
                        title: `${this.$route.meta.title} - 销售单`,
                        alert: false,
                        loading: false,
                        saveLoading: false
                    };
                    break;

                // 导出excel
                case 'exportExcel':
                    this.exportExcel('xGreatSendSaleDetailTable', true);
                    break;

                // 打印
                case 'print':
                    this.handlePrint('xGreatSendSaleDetailTable');
                    break;
            }
        },
        // 导出excel => 销售单
        exportExcel(ref, isSend) {
            // isSend 是否为销售单生成 还是销售订单
            const { columns, tableData = [], footerData } = this._genVisibleTableData(ref);
            let formData = {};
            if (isSend) {
                formData = this.getExportFormDataForSendOrder();
            } else {
                formData = this.getExportFormDataForOrder();
            }
            this.$Utils.exportExcel(
                columns,
                tableData.map((i) => ({ ...i, rate: `${i.rate}%` })),
                footerData,
                formData
            );
        },

        // 获取销售订单导出表单信息
        getExportFormDataForOrder() {
            const form = this.planModalForm;
            return {
                title: '销售订单',
                desc: {
                    label: '摘要',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '单据编号',
                            value: form.so_sn
                        },
                        {
                            label: '相关机构',
                            value: form.partA_name
                        },
                        {
                            label: '期望到货日期',
                            value: form.expect_aog_date
                        }
                    ],
                    [
                        {
                            label: '制单人',
                            value: form.creator_name
                        },
                        {
                            label: '生成时间',
                            value: form.create_time
                        },
                        {
                            label: '审核人',
                            value: form.auditor_name
                        },
                        {
                            label: '审核时间',
                            value: form.audit_time
                        }
                    ]
                ]
            };
        },
        // 获取销售单生成导出表单信息
        getExportFormDataForSendOrder() {
            // const form = this.planModalForm
            const form = this.infoAllData;
            return {
                title: '销售单',
                desc: {
                    label: '摘要',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '相关机构',
                            value: form.organization_name
                        },
                        {
                            label: '销售员',
                            value: form.seller_name
                        },
                        {
                            label: '销售部门',
                            value: form.department_name
                        },
                        {
                            label: '单据编号',
                            value: form.sl_sn
                        }
                    ],
                    [
                        {
                            label: '采购方联系人',
                            value: form.purchaser_linkman
                        },
                        {
                            label: '联系电话',
                            value: form.purchaser_phone
                        },
                        {
                            label: '销售方联系人',
                            value: form.seller_linkman
                        },
                        {
                            label: '联系电话',
                            value: form.seller_phone
                        }
                    ],
                    [
                        {
                            label: '收款日期',
                            value:
                                form.collection_date && Object.keys(form.collection_date).length
                                    ? moment(form.collection_date).format('YYYY-MM-DD')
                                    : ''
                        },
                        {
                            label: '制单人',
                            value: form.making_people_name
                        },
                        {
                            label: '生成时间',
                            value: form.billing_time
                        },
                        {
                            label: '审核人',
                            value: form.auditor_name
                        }
                    ]
                ]
            };
        },
        // 导出excel => 采购计划
        exportExcelForPurchase(ref) {
            const table = this.$refs[ref];
            const columns = table.getColumns();
            const tableData = table.getData();
            const formData = {
                title: '采购计划单',
                desc: {
                    label: '摘要',
                    value: this.editInfoFormObj.description
                },
                groups: [
                    [
                        {
                            label: '部门',
                            value: this.editInfoFormObj.department_name
                        },
                        {
                            label: '制单人',
                            value: this.employeeInfo.Name
                        }
                    ]
                ]
            };
            this.$Utils.exportExcel(columns, tableData, [], formData);
        },
        // 打印
        handlePrint(ref) {
            let { columns, tableData } = this._genVisibleTableData(ref);
            columns = columns
                .map((item) => {
                    return {
                        dataIndex: item.property,
                        title: item.title,
                        width: item.width,
                        key: item.property,
                        align: 'center'
                    };
                })
                .filter((item) => item.title !== '操作');

            const printCfg = {
                'xTablePlanModalInfo': {
                    // 销售订单
                    printType: 6, // 1 图片；2、耗材申领；3、出入库单；4、采购询价单；5、采购计划单; 6、采购-订单/销售-订单 8 销售单
                    printForm: this.planModalForm,
                    myOrderPropObj: {
                        pageType: 1 // 页面类型判断依据： 0-采购订单  1-销售订单
                    },
                    totalObj: {
                        moneyTotal: this.$XEUtils.commafy(this.money_total, { fixed: 2 }),
                        taxAmountTotal: this.$XEUtils.commafy(this.tax_amount_total, { fixed: 2 }),
                        taxTotal: this.$XEUtils.commafy(this.tax_total, { fixed: 2 })
                    }
                },
                'xGreatSendSaleTable': {
                    // 生成销售单
                    printType: 8,
                    printForm: {
                        ...this.planModalForm,
                        collection_date:
                            this.planModalForm.collection_date && Object.keys(this.planModalForm.collection_date).length
                                ? moment(this.planModalForm.collection_date).format('YYYY-MM-DD')
                                : '', // 收款时间
                        organization_name: this.planModalForm.partA_name // 相关机构
                    },
                    totalObj: {
                        moneyTotal: this.$XEUtils.commafy(this.money_total, { fixed: 2 }),
                        taxAmountTotal: this.$XEUtils.commafy(this.tax_amount_total, { fixed: 2 }),
                        taxTotal: this.$XEUtils.commafy(this.tax_total, { fixed: 2 })
                    }
                },
                'xGreatSendPlanTable': {
                    // 生成采购计划
                    printType: 5,
                    printForm: {
                        creator_name: this.employeeInfo.Name,
                        description: this.editInfoFormObj.description,
                        department_name: this.editInfoFormObj.department_name
                    }
                },
                'xGreatSendSaleDetailTable': {
                    // 销售单详情
                    printType: 8,
                    printForm: {
                        ...this.infoAllData,
                        collection_date:
                            this.infoAllData.collection_date && Object.keys(this.infoAllData.collection_date).length
                                ? moment(this.infoAllData.collection_date).format('YYYY-MM-DD')
                                : '',
                        so_sn: this.infoAllData.sl_sn, // 单据编号
                        creator_name: this.infoAllData.making_people_name, // 制单人
                        create_time: this.infoAllData.billing_time // 生成时间
                    },
                    totalObj: {
                        moneyTotal: this.$XEUtils.commafy(this.sales_order_detail_money_total, { fixed: 2 }),
                        taxAmountTotal: this.$XEUtils.commafy(this.sales_order_detail_tax_amount_total, { fixed: 2 }),
                        taxTotal: this.$XEUtils.commafy(this.sales_order_detail_tax_total, { fixed: 2 })
                    }
                }
            };

            let printContent = {
                printData: tableData,
                printTable: columns,
                ...printCfg[ref]
            };

            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        // 生成销售单打印和导出table的显示数据（状态文本名称，包装名称文本，金额计算等）
        _genVisibleTableData(ref) {
            const table = this.$refs[ref];
            const columns = table.getColumns();
            const { tableData, footerData } = table.getTableData();
            return {
                columns, // 表头
                footerData, // 表尾数据
                tableData: tableData.map((item, index) => {
                    // 表体数据
                    return {
                        ...item,
                        index: index + 1,
                        status: this.$options.filters['formatStatus'](item.status, 'planOrder'), // 状态名称
                        unit_inner_sn: item.unit_name, // 包装单位名称
                        money: this.money_record(item), // 金额
                        unit_price: this.unit_price_record(item),
                        tax_amount: this.tax_amount_record(item), // 含税金额
                        tax: this.tax_record(item) // 税额
                    };
                })
            };
        },
        updateFooterPlanModalEditBNFn() {
            this.$refs.xTablePlanModalInfo.updateData();
        },
        updateFooterPlanModalEditFn() {
            this.$refs.xTablePlanModalInfo.updateFooter();
        },
        updateFooterPlanModalFn() {
            const xGreatSendSaleTable = this.$refs.xGreatSendSaleTable,
                xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
            if (xGreatSendSaleTable) {
                xGreatSendSaleTable.updateFooter();
                return;
            }
            if (xTablePlanModalInfo) {
                xTablePlanModalInfo.updateFooter();
            }
        },
        footerCellClassNameFn({ column }) {
            const redAry = ['money', 'tax_amount', 'tax'];
            if (redAry.some((item) => item === column.property)) {
                return 'red bold';
            }
            if (column.property === 'index') {
                return 'total-bg bold';
            }
        },
        footerMethodFn({ columns }) {
            const { toFixed } = this.$XEUtils;
            return [
                columns.map((column, columnIndex) => {
                    if (columnIndex === 0) {
                        return '合计';
                    }
                    // 金额
                    if (column.property === 'money') {
                        return toFixed(this.money_total, 4);
                    }
                    if (column.property === 'discount_money') {
                        return toFixed(this.discount_money_total, 4);
                    }
                    // 含税金额
                    if (column.property === 'tax_amount') {
                        return toFixed(this.tax_amount_total, 4);
                    }
                    // 税额
                    if (column.property === 'tax') {
                        return toFixed(this.tax_total, 4);
                    }
                })
            ];
        },
        // 销售单产品详情列表中的footer计算方法
        footerMethodFnForSalesOrderDetail({ columns }) {
            return [
                columns.map((column, columnIndex) => {
                    if (columnIndex === 0) {
                        return '合计';
                    }
                    if (column.property === 'money') {
                        return this.sales_order_detail_money_total;
                    }
                    if (column.property === 'tax_amount') {
                        return this.sales_order_detail_tax_amount_total;
                    }
                    if (column.property === 'tax') {
                        return this.sales_order_detail_tax_total;
                    }
                })
            ];
        },
        afterClosePlanmodalFn() {
            const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
            if (xTablePlanModalInfo) {
                xTablePlanModalInfo.refreshScroll();
                this.planModalDatas = [];
                xTablePlanModalInfo.clearData();
            }
            if (this.isFullscreen) {
                this.modalFullscreen();
            }
        },
        _isStop(status) {
            // 获取订单明细列表 item是否为终止
            const ITEM_STOP_STATUS = 3; // 已终止处理
            return status === ITEM_STOP_STATUS;
        },
        xTablePlanModalInfoToolbarFn() {
            this.$refs.xTablePlanModalInfo.zoom();
            this.xTablePlanModalInfoToolbar.actions.isFullScreen =
                !this.xTablePlanModalInfoToolbar.actions.isFullScreen;
        },
        openChooseTb() {
            // 打开相关机构
            this.pageTypeName = 8;
            this.chooseOrgMd.alert = true;
            this.$Utils.globalDragCenterFn('choose_org_md');
            this.$nextTick(() => {
                if (this.$refs.chooseOrgMd) {
                    this.$refs.chooseOrgMd.setContractOrgCheckedFn();
                    this.$refs.chooseOrgMd.pageDataInit();
                }
            });
        },
        chooseOrgFn(resObj) {
            // 获取选中相关机构信息
            const objValue = resObj.org_id + '-' + resObj.billsType,
                objLabel = resObj.name;
            this.orderOrgListAPIFn(objValue, objLabel);
            this.selectOrganizationFn(objValue, objLabel);
            this.chooseOrgMd.alert = false;
        },
        selectOrganizationFn(value, label) {
            const values = value.split('-');
            this.infoAllData.organization_inner_sn = values[0];
            this.infoAllData.organization_name = label;
            this.infoAllData.billsType = values[1];
            this.planModalForm.partA = values[0];
            this.planModalForm.billsType = values[1];
            this.billsType = values[1];
            this.planModalOrgSn = [value];
            this.partC.oname = this.orgName; // label.length ? label : ''
            this.partC.partB_id = this.$cookie.get('userbelong');
            this.partC.partA_id = values[0];
            this.partC.billsType = values[1];
            if (this.btnName === 'add') {
                this.planModalDatas = [];
                this.$refs.xTablePlanModalInfo && this.$refs.xTablePlanModalInfo.remove();
            }
        },
        // 获取医保编号/阳光流水号下拉
        async getProductSnAPIFn(isHealthCare, row) {
            this.healthAndSunSnModal.data = [];
            let params = {
                vendor_inner_sn: row.product_vendor_inner_sn,
                product_inner_sn: row.product_inner_sn,
                specification_inner_sn: row.specification_inner_sn,
                pageNum: 1,
                pageSize: 10,
                type: isHealthCare ? 'HEALTH_CARE_SN' : 'SUN_SN'
            };
            await productSnListAPI({ params }).then((res) => {
                this.healthAndSunSnModal.data = res.list.map((item, index) => {
                    return {
                        sn: item.sn,
                        sn_inner_sn: item.sn_inner_sn,
                        is_default: item.is_default,
                        index: index,
                        type: item.type,
                        vendor_inner_sn: item.vendor_inner_sn,
                        product_inner_sn: item.product_inner_sn,
                        specification_inner_sn: item.specification_inner_sn
                    };
                });
            });
        },
        // 获取医保编号输入
        healthCareSnChangeFn(item, seq) {
            this.planModalDatas[seq - 1].health_care_sn = item.sn;
            this.$refs.xGreatSendSaleTable.updateData();
        },
        // 获取阳光流水号输入
        sunSnChangeFn(item, seq) {
            this.planModalDatas[seq - 1].sun_sn = item.sn;
            this.$refs.xGreatSendSaleTable.updateData();
        },
        modalFullscreen() {
            const clientHeight = document.body.clientHeight;
            const clientWidth = document.body.clientWidth;
            if (!this.isFullscreen) {
                for (let i = 0; i < document.getElementsByClassName('ant-modal').length; i++) {
                    document.getElementsByClassName('ant-modal')[i].style.height = clientHeight + 24 + 'px';
                    document.getElementsByClassName('ant-modal')[i].style.width = clientWidth + 'px';
                    document.getElementsByClassName('ant-modal')[i].style.top = '0px';
                    document.getElementsByClassName('ant-modal')[i].style.left = '0px';
                    document.getElementsByClassName('ant-modal-content')[i].style.height = clientHeight + 'px';
                    document.getElementsByClassName('ant-modal-content')[i].style.width = clientWidth + 'px';
                    document.getElementsByClassName('ant-modal-body')[i].style.height = clientHeight - 57 + 'px';
                    document.getElementsByClassName('ant-modal-body')[i].style.width = clientWidth + 'px';
                    document.getElementsByClassName('tableFather')[i].style.height = clientHeight - 420 + 'px';
                    this.isFullscreen = true;
                }
            } else {
                for (let i = 0; i < document.getElementsByClassName('ant-modal').length; i++) {
                    document.getElementsByClassName('ant-modal')[i].style.height = '673px';
                    document.getElementsByClassName('ant-modal')[i].style.width = '1200px';
                    document.getElementsByClassName('ant-modal')[i].style.top = '64.5px';
                    document.getElementsByClassName('ant-modal')[i].style.left = '168px';
                    document.getElementsByClassName('ant-modal-content')[i].style.height = '649px';
                    document.getElementsByClassName('ant-modal-content')[i].style.width = '1200px';
                    document.getElementsByClassName('ant-modal-body')[i].style.height = '592px';
                    document.getElementsByClassName('ant-modal-body')[i].style.width = '1200px';
                    document.getElementsByClassName('tableFather')[i].style.height = '230px';
                    this.isFullscreen = false;
                }
            }
        },
        fullscreenMouseOver(isOut) {
            for (let i = 0; i < document.getElementsByClassName('modal-fullscreen').length; i++) {
                document.getElementsByClassName('modal-fullscreen')[i].style.background = isOut ? '' : '#1387e0';
            }
        },
        /** 修改或新增订单时，税率改变触发的方法 */
        tableRateChangeFn(e) {
            const record = this.$refs.xTablePlanModalInfo.getCurrentRecord();
            const index = this.$refs.xTablePlanModalInfo.getRowIndex(record);
            let v = e.target.value ? parseInt(e.target.value) : 0;

            v = v > 100 ? 100 : v < 0 ? 0 : v;
            this.planModalDatas[index].rate = v;
            this.updateFooterPlanModalFn(record);
        }
    },
    filters: {
        formatStatus(value, name) {
            const planListStatus = ['草稿', '正式', '已审核', '已确认', '已终止', '已完结'],
                planOrderStatus = ['处理中', '已完全处理', '已计划采购', '已终止处理'];
            if (name === 'planList') {
                return planListStatus[value];
            } else if (name === 'planOrder') {
                return planOrderStatus[value];
            }
        },
        filtersTwo(value) {
            // 保留两位小数
            if (typeof value === 'number') {
                return this.$XEUtils.commafy(value, { fixed: 2 });
            }
            return '';
        }
    },
    computed: {
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 214 : 154,
                pager = 52;
            // botTable = 400 // 之前是234
            return (clientHeight - topHeight - pager) / 2;
        },
        scrollYNumberHistory() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 214 : 154,
                pager = 52,
                botTable = 400; // 之前是210
            return clientHeight - topHeight - pager - botTable;
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
        planModalTextComputed() {
            // 销售订单右下角的取消文字改变
            if (
                this.planModalForm.status === 0 ||
                this.planModalForm.status === 1 ||
                (this.planModalForm.status === 2 && this.planModalForm.create_organization_type !== 1) ||
                this.btnName === 'add'
            ) {
                return '取消';
            } else {
                return '关闭';
            }
        },
        isEditDisabledPo() {
            // 单据编号【弹框的内容是否可编辑】
            let flag = true;
            if (this.btnName === 'add') {
                flag = false;
            } else if (
                (this.activeTabsKey === this.tabTypes[0] || this.activeTabsKey === this.tabTypes[1]) &&
                this.planModalForm.status <= 2
            ) {
                flag = false;
            }
            return flag;
        },
        isEditDisabled() {
            // 弹框的内容是否可编辑
            let flag = true;
            if (this.btnName === 'add') {
                flag = false;
            } else if (
                this.activeTabsKey === this.tabTypes[0] &&
                this.planModalForm.status <= 1 &&
                this.planModalForm.create_organization_type === 1
            ) {
                flag = false;
            }
            return flag;
        },
        isSourceBuyerAudited() {
            // 单据来源为买方且状态为已审核的销售订单可以编辑单价、折扣、税率 禅道单号（2967）
            const status = this.planModalForm.status; // 状态 0-草稿 1-正式 2-已审批 3-确认 4-已终止 5-已完结
            const source = this.planModalForm.create_organization_type; // 单据生成方 0-采购 1-销售
            return status === 2 && source === 0;
        },
        topEditRemoveBtnDisabled() {
            // 外层顶部的修改/删除按钮状态控制 => 草稿 正式
            let flag = true;
            if (
                this.tabTypes === this.systemType[0] &&
                this.suppliesClaimDatas.length &&
                (this.currentTrObj.status === 0 || this.currentTrObj.status === 1)
            ) {
                flag = false;
            }
            return flag;
        },
        topEditSubmitDisabled() {
            // 外层顶部的确认按钮状态控制 => 已审核
            let flag = true;
            if (
                this.tabTypes === this.systemType[0] &&
                this.suppliesClaimDatas.length &&
                this.currentTrObj.status === 2
            ) {
                flag = false;
            }
            return flag;
        },
        topEditStopDisabled() {
            // 外层顶部的终止按钮状态控制 => 已审核 已确认
            let flag = true;
            if (
                this.tabTypes === this.systemType[0] &&
                this.suppliesClaimDatas.length &&
                (this.currentTrObj.status === 2 || this.currentTrObj.status === 3)
            ) {
                flag = false;
            }
            return flag;
        },
        /**
         * 【金额】合计
         * @abstract 销售订单合计处使用
         */
        money_total() {
            const records = this.planModalDatas;
            if (!records) {
                return 0;
            }
            const { isNumber, add, multiply, divide } = this.$XEUtils;
            return this.planModalDatas.reduce((prevValue, currentItem) => {
                const quantity = isNumber(currentItem.quantity) ? currentItem.quantity : 0;
                const taxUnitPrice = isNumber(currentItem.tax_unit_price) ? currentItem.tax_unit_price : 0;
                if (taxUnitPrice === 0) {
                    return prevValue;
                }
                const currentValue = multiply(divide(taxUnitPrice, add(1, divide(currentItem.rate, 100))), quantity);
                // 不计算已终止的产品
                if (this._isStop(currentItem.status)) {
                    return prevValue;
                }
                return add(prevValue + currentValue);
            }, 0);
        },
        /**
         * 【金额】 = 数量 * 单价
         * @abstract 明细使用
         */
        money_record() {
            // 金额 = 数量 * 单价
            const { isNumber, add, toFixed, multiply, divide } = this.$XEUtils;
            return (record) => {
                const fRate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
                const fTaxUnitPrice = parseFloat(record.tax_unit_price);
                const fQuantity = parseFloat(record.quantity);

                const nextItem = multiply(divide(fTaxUnitPrice, add(1, fRate)), fQuantity);
                if (isNumber(nextItem)) {
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        /**
         * 【单价】 = 含税单价 / (1 + 税率 / 100)
         * @abstract 明细使用
         */
        unit_price_record() {
            return (record) => {
                const rate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
                let nextItem = parseFloat(record.tax_unit_price) / (1 + rate);
                if (!isNaN(nextItem)) {
                    if (nextItem < 0) nextItem = 0;
                    return nextItem.toFixed(4);
                } else {
                    return '';
                }
            };
        },
        /**
         * 【含税金额】合计
         * @abstract 销售订单合计处使用
         */
        tax_amount_total() {
            const { isNumber, add, multiply } = this.$XEUtils;
            return this.planModalDatas.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    taxUnitPrice = next.tax_unit_price || 0,
                    nextItem = multiply(parseFloat(next.quantity), parseFloat(taxUnitPrice));

                // 不计算已终止的产品
                const isStop = this._isStop(next.status);
                if (isStop) nextItem = 0;

                return isNumber(prevItem) && isNumber(nextItem) ? add(prevItem, nextItem) : '';
            }, 0);
        },
        /**
         * 【含税金额】 = 数量 * 含税单价
         * @abstract 明细使用
         */
        tax_amount_record() {
            return (record) => {
                let nextItem = record.quantity * parseFloat(record.tax_unit_price);

                return !isNaN(nextItem) ? nextItem.toFixed(4) : '';
            };
        },
        /**
         * 【税额】合计
         * @abstract 销售订单合计处使用
         */
        tax_total() {
            const records = this.planModalDatas;
            if (!records) {
                return 0;
            }
            const { isNumber, add, subtract, multiply, divide } = this.$XEUtils;
            return records.reduce((prevValue, currentItem) => {
                const rate = isNumber(currentItem.rate) ? divide(currentItem.rate, 100) : 0;
                const taxUnitPrice = isNumber(currentItem.tax_unit_price) ? currentItem.tax_unit_price : 0;
                const quantity = isNumber(currentItem.quantity) ? currentItem.quantity : 0;
                if (taxUnitPrice === 0) {
                    return prevValue;
                }
                const currentValue = multiply(quantity, subtract(taxUnitPrice, divide(taxUnitPrice, add(rate, 1))));
                // 不计算已终止的产品
                if (this._isStop(currentItem.status)) {
                    return prevValue;
                }
                return add(prevValue, currentValue);
            }, 0);
        },
        /**
         * 【税额】= 含税金额 - 金额
         * @abstract 明细使用
         */
        tax_record() {
            return (record) => {
                const rate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
                const unitPrice = (
                    (parseFloat(record.tax_unit_price) / (1 + rate)) *
                    parseFloat(record.quantity)
                ).toFixed(4);
                let nextItem =
                    (parseFloat(record.quantity) * parseFloat(record.tax_unit_price)).toFixed(4) -
                    parseFloat(unitPrice);
                if (!isNaN(nextItem)) {
                    if (nextItem < 0) nextItem = 0;
                    return nextItem.toFixed(4);
                } else {
                    return '';
                }
            };
        },
        /**
         * 销售单产品详情合计金额
         * @unknow
         */
        sales_order_detail_money_total() {
            return this.infoData.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    taxUnitPrice = next.tax_unit_price || 0,
                    nextItem = (parseFloat(taxUnitPrice) / (1 + next.rate / 100)) * parseFloat(next.quantity);
                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return (prevItem + nextItem).toFixed(4);
                } else {
                    return '';
                }
            }, 0);
        },
        /**
         * 销售单产品详情合计含税金额
         * @unknow
         */
        sales_order_detail_tax_amount_total() {
            return this.infoData.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    taxUnitPrice = next.tax_unit_price || 0,
                    nextItem = parseFloat(next.quantity) * parseFloat(taxUnitPrice);

                // nextItem = this.reNumber(nextItem)
                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return (prevItem + nextItem).toFixed(4);
                } else {
                    return '';
                }
            }, 0);
        },
        /**
         * 销售单产品详情合计税额
         * @unknow
         */
        sales_order_detail_tax_total() {
            return this.infoData.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    rate = next.rate < 0 ? 0 : parseFloat(next.rate) / 100,
                    unitPrice = ((parseFloat(next.tax_unit_price) / (1 + rate)) * parseFloat(next.quantity)).toFixed(4),
                    nextItem =
                        (parseFloat(next.quantity) * parseFloat(next.tax_unit_price)).toFixed(4) -
                        parseFloat(unitPrice);

                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return (prevItem + nextItem).toFixed(4);
                } else {
                    return '';
                }
            }, 0);
        },
        rowSelectionPlanModal() {
            // 内层的生成销售订单前面的复选框状态
            const _this = this;
            return {
                onChange: (selectedRowKeys) => {
                    _this.planModalSelectedRowKeys = selectedRowKeys;
                },
                getCheckboxProps: (record) => ({
                    props: {
                        disabled:
                            record.status < 0 ||
                            this.btnName === 'add' ||
                            (this.planModalForm.status > 0 && this.planModalForm.status < 4)
                    }
                })
            };
        },
        orgName() {
            const EmployeeInfo = this.$cookie.get('EmployeeInfo'),
                orgNameNo = '';
            return EmployeeInfo ? JSON.parse(EmployeeInfo).OrgName || orgNameNo : orgNameNo;
        },
        userSystemType() {
            // 0-平台、 1-医院 、 2-经销商、 3-厂商
            return parseFloat(this.$cookie.get('userSystemType'));
        },
        ...mapState(['warehouseList'])
    },
    components: {
        chooseOrgTab,
        addProduct
    },
    watch: {
        '$route'() {
            this.init();
        }
    },
    mounted() {
        this.tabTop = this.scrollYNumber - 32;
        this.tabDown = this.scrollYNumber - 32;
        this.dragControllerDiv();
        this.tabTopHand = this.scrollYNumber - 32;
        this.tabDownHand = this.scrollYNumber + 8;
        this.dragControllerDivHandle();
    },
    created() {
        this.init();
    }
};
</script>

<style lang="less" scoped>
.scoped .bot-search-form-wrap {
    padding: 0;
    max-height: 300px;
    border: none;
    .bot-hight-search-wrap {
        padding: 0 0px 12px;
        .form li:last-child {
            width: auto;
            .ant-btn {
                margin-right: 24px;
            }
        }

        .bot-hight-search-container {
            display: flex;
            align-items: center;

            .bot-hight-search-checkbox {
                margin-left: 10px;
                margin-right: 20px;
            }

            .bot-hight-search-select {
                display: flex;
                align-items: center;
                margin-left: 10px;

                .label {
                    margin-right: 15px;
                }

                /deep/ .ant-select-selection {
                    overflow-y: auto;
                }
            }

            .bot-hight-search-button {
                display: flex;
                .ant-btn {
                    min-width: 76px;
                    height: 24px;
                }
                .ant-btn:first-child {
                    margin-right: 24px;
                }
            }
        }
    }

    .bot-product-item {
        padding: 10px;
        position: absolute;
        left: 575px;
        text-align: left;
    }
}
@w: 24px;
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
        align-items: center;
        padding-top: 24px;

        .item-wrap {
            display: flex;
            align-items: center;
            padding-right: 50px;
            .item {
                padding-bottom: 0;
                padding-right: @w;
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
// 顶部按钮多的样式重新调整左右间距
.scoped .btn-top-wrap .btn-list-wrap li,
.scoped .btn-top-wrap .btn-list-wrap li .ant-btn {
    margin-right: 10px;
}
.order-modal-list li {
    padding-top: 18px;
}
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
.mid {
    width: calc(100% - 2px);
    padding: 0px 8px;
    float: left;
    height: 50%;
}
.handlebox {
    width: 100%;
    top: 40px;
    bottom: 0px;
    overflow: hidden;
    position: absolute;
}

.handleBoxtop {
    width: calc(100% - 2px);
    height: calc(50% - 3px);
    padding: 8px;
    float: left;
}
.handleResize {
    width: 100%;
    height: 5px;
    cursor: w-resize;
    cursor: s-resize;
    float: left;
    border-bottom: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
}
.handleBoxdown {
    width: calc(100% - 2px);
    padding: 0px 8px;
    float: left;
    height: 50%;
}
.dropdownButton {
    float: right;
    margin: 0px 10px 10px 0px;
}

.modal-fullscreen {
    display: inline-block;
    position: absolute;
    right: 56px;
    top: 0px;
    width: 56px;
    font-size: 16px;
    font-style: normal;
    text-align: center;
    text-transform: none;
    text-rendering: auto;
    height: 48px;
    line-height: 48px;
    color: #fff;
}
</style>
