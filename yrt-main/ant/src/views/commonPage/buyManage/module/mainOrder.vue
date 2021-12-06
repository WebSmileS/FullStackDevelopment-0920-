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
                            <a-button
                                :disabled="topEditBtnDisabled"
                                type="primary"
                                size="small"
                                @click="groupBtnFn('edit')"
                            >
                                修改
                            </a-button>
                            <a-button
                                :disabled="tabsActiveKey === systemTypes[2]"
                                type="danger"
                                size="small"
                                @click="groupBtnFn('remove')"
                            >
                                删除
                            </a-button>
                        </li>
                        <li>
                            <a-button
                                :disabled="tabsActiveKey === systemTypes[2]"
                                type="primary"
                                size="small"
                                @click="groupBtnFn('submit')"
                            >
                                确认
                            </a-button>
                            <a-button
                                v-if="myOrderPropObj.pageType === 1"
                                :disabled="tabsActiveKey === systemTypes[2]"
                                type="primary"
                                size="small"
                                @click="groupBtnFn('build')"
                            >
                                生成销售单
                            </a-button>
                            <a-button
                                :disabled="tabsActiveKey === systemTypes[2]"
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
                            v-if="myOrderPropObj.pageType === 0 && !hightSearchObj.isArrow"
                            v-model="searchFormObj.order_sn"
                            placeholder="单据编号"
                            size="small"
                            enterButton
                            @search="topSearchFn"
                            class="search"
                        />
                        <a-input-search
                            v-else-if="myOrderPropObj.pageType === 1 && !hightSearchObj.isArrow"
                            v-model="searchFormObj.partA_name"
                            :placeholder="placeholderComputed"
                            size="small"
                            enterButton
                            @search="topSearchFn"
                            class="search"
                        />
                        <div @click="hightSearchIconFn" class="hight-search-text">
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
                    <ul id="searchForm" class="form">
                        <li class="search-form-item">
                            <span class="label">{{ placeholderComputed }}</span>
                            <a-input
                                type="text"
                                v-if="myOrderPropObj.pageType === 0"
                                v-model="searchFormObj.partB_name"
                                placeholder=""
                            />
                            <a-input
                                type="text"
                                v-else-if="myOrderPropObj.pageType === 1"
                                v-model="searchFormObj.partA_name"
                                placeholder=""
                            />
                        </li>
                        <li class="search-form-item">
                            <span class="label">单据编号</span>
                            <a-input type="text" v-model="searchFormObj.order_sn" placeholder="" />
                        </li>
                        <li class="search-form-item" v-if="myOrderPropObj.pageType === 0">
                            <span class="label">采购员</span>
                            <a-tree-select
                                treeNodeFilterProp="title"
                                searchPlaceholder=""
                                :showSearch="true"
                                :treeData="topSearchEmpDropdownList"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                treeDefaultExpandAll
                                v-model="topSearchEmpDropdownSn"
                                allowClear
                                @change="changeTopSearchEmpDropdownFn"
                                class="input"
                                style="min-width: 120px"
                            >
                            </a-tree-select>
                        </li>
                        <li class="search-form-item" v-if="myOrderPropObj.pageType === 0">
                            <span class="label">采购部门</span>
                            <a-tree-select
                                treeNodeFilterProp="title"
                                :showSearch="true"
                                :treeData="topSearchAllTreeList"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                treeDefaultExpandAll
                                v-model="topSearchAllTreeListSn"
                                allowClear
                                @change="changeTopSearchAllTreeListFn"
                                class="input"
                                style="min-width: 120px"
                            >
                            </a-tree-select>
                        </li>
                        <li class="search-form-item">
                            <span class="label" style="width: 116px">单据生成时间</span>
                            <a-range-picker
                                :value="searchFormObj.createTimeRange"
                                @change="changeTopSearchCreateTimeRange"
                            />
                        </li>
                        <li class="search-form-item search-button">
                            <a-button type="primary" @click="topSearchFn"> 搜索 </a-button>
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
            <!-- 表格头部 -->
            <a-tabs type="card" v-model="tabsActiveKey" @change="changeActiveFn">
                <a-tab-pane :tab="tabsTextComputed(0)" style="padding: 8px" :key="systemTypes[0]">
                    <div v-if="myOrderPropObj.pageType === 0">
                        <vxe-table
                            ref="xPurchaseDelarTable"
                            size="small"
                            :height="scrollYPurchaseDelarNumber - 4"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            @cell-click="cellClickPurchaseDelarFn"
                            @cell-dblclick="cellDblclickPurchaseDelarFn"
                            @select-change="selectChangePurchaseDelarFn"
                            align="center"
                            :data="purchaseDelarList"
                            :loading="purchaseDelarPagination.loading"
                            show-overflow
                            show-header-overflow
                            showHeader
                            :start-index="(purchaseDelarPagination.current - 1) * purchaseDelarPagination.pageSize"
                        >
                            <vxe-table-column type="checkbox" width="5%" :resizable="false"></vxe-table-column>
                            <vxe-table-column width="150px" filed="partB_name" :title="placeholderComputed">
                                <template v-slot="{ row }">
                                    <span>{{ row.partB_name }}</span>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                v-for="performItem in performColumns"
                                :key="performItem.key"
                                :width="performItem.width"
                                :field="performItem.dataIndex"
                                :title="performItem.title"
                            ></vxe-table-column>
                            <vxe-table-column width="80px" field="status" title="状态">
                                <template v-slot="{ row }">
                                    <span>{{ row.status | formatStatus('planList') }}</span>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column width="60px" field="status" title="来源">
                                <template v-slot="{ row }">
                                    <span>{{ row.create_organization_type | formatOrgType() }}</span>
                                </template>
                            </vxe-table-column>
                        </vxe-table>
                        <vxe-pager
                            :loading="purchaseDelarPagination.loading"
                            :current-page="purchaseDelarPagination.current"
                            :page-size="purchaseDelarPagination.pageSize"
                            :page-sizes="purchaseDelarPagination.pageSizes"
                            :total="purchaseDelarPagination.total"
                            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                            @page-change="changePurchaseDelarPaginationFn"
                        >
                        </vxe-pager>
                    </div>
                    <a-table
                        v-else-if="myOrderPropObj.pageType === 1"
                        :rowSelection="{ selectedRowKeys: saleDelarSelectedRowKeys, onChange: saleDelarSelectChangeFn }"
                        :columns="salesHospitalColumns"
                        :dataSource="saleDelarList"
                        :loading="saleDelarPagination.loading"
                        :pagination="{
                            size: 'small',
                            current: saleDelarPagination.current,
                            total: saleDelarPagination.total,
                            pageSize: saleDelarPagination.pageSize,
                            showSizeChanger: true,
                            showQuickJumper: true
                        }"
                        :rowClassName="rowSaleDelarClassFn"
                        :scroll="{ y: scrollYPerformNumber }"
                        :customRow="customSaleDelarRowFn"
                        @change="changeSaleDelarPaginationFn"
                    >
                        <div slot="status" slot-scope="text, record">
                            {{ record.status | formatStatus('planList') }}
                        </div>
                    </a-table>
                </a-tab-pane>
                <a-tab-pane :tab="tabsTextComputed(1)" style="padding: 8px" :key="systemTypes[1]">
                    <div v-if="myOrderPropObj.pageType === 0">
                        <vxe-table
                            ref="xPurchaseVendorTable"
                            size="small"
                            :height="scrollYPurchaseDelarNumber - 4"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            @cell-click="cellClickPurchaseVendorFn"
                            @cell-dblclick="cellDblclickPurchaseVendorFn"
                            @select-change="selectChangePurchaseVendorFn"
                            align="center"
                            :data="purchaseVendorList"
                            :loading="purchaseVendorPagination.loading"
                            show-overflow
                            show-header-overflow
                            showHeader
                            :start-index="(purchaseVendorPagination.current - 1) * purchaseVendorPagination.pageSize"
                        >
                            <vxe-table-column type="checkbox" width="5%" :resizable="false"></vxe-table-column>
                            <vxe-table-column width="150px" filed="partB_name" :title="placeholderComputed">
                                <template v-slot="{ row }">
                                    <span>{{ row.partB_name }} </span>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                v-for="performItem in performColumns"
                                :key="performItem.key"
                                :width="performItem.width"
                                :field="performItem.dataIndex"
                                :title="performItem.title"
                            ></vxe-table-column>
                            <vxe-table-column width="80px" field="status" title="状态">
                                <template v-slot="{ row }">
                                    <span>{{ row.status | formatStatus('planList') }}</span>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column width="60px" field="status" title="来源">
                                <template v-slot="{ row }">
                                    <span>{{ row.create_organization_type | formatOrgType() }}</span>
                                </template>
                            </vxe-table-column>
                        </vxe-table>
                        <vxe-pager
                            :loading="purchaseVendorPagination.loading"
                            :current-page="purchaseVendorPagination.current"
                            :page-size="purchaseVendorPagination.pageSize"
                            :page-sizes="purchaseVendorPagination.pageSizes"
                            :total="purchaseVendorPagination.total"
                            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                            @page-change="changePurchaseVendorPaginationFn"
                        >
                        </vxe-pager>
                    </div>
                    <!-- <a-table v-if="myOrderPropObj.pageType === 0"
                             :rowSelection="{selectedRowKeys: purchaseVendorSelectedRowKeys, onChange: purchaseVendorSelectChangeFn}"
                             :columns="performColumns"
                             :dataSource="purchaseVendorList"
                             :loading="purchaseVendorPagination.loading"
                             :pagination="{
                                 size: 'small',
                                 current: purchaseVendorPagination.current,
                                 total: purchaseVendorPagination.total,
                                 pageSize: purchaseVendorPagination.pageSize,
                                 showSizeChanger: true,
                                 showQuickJumper: true
                             }"
                             :rowClassName="rowPurchaseVendorClassFn"
                             :scroll="{ y: scrollYPerformNumber }"
                             :customRow="customPurchaseVendorRowFn"
                             @change="changePurchaseVendorPaginationFn">
                        <span slot='nameTitle'>{{ placeholderComputed }}</span>

                        <div slot="status"
                             slot-scope="text, record">
                            {{ record.status | formatStatus('planList') }}
                        </div>
                    </a-table> -->
                    <a-table
                        v-else-if="myOrderPropObj.pageType === 1"
                        :rowSelection="{
                            selectedRowKeys: saleVendorSelectedRowKeys,
                            onChange: saleVendorSelectChangeFn
                        }"
                        :columns="salesDealerColumns"
                        :dataSource="saleVendorList"
                        :loading="saleVendorPagination.loading"
                        :pagination="{
                            size: 'small',
                            current: saleVendorPagination.current,
                            total: saleVendorPagination.total,
                            pageSize: saleVendorPagination.pageSize,
                            showSizeChanger: true,
                            showQuickJumper: true
                        }"
                        :rowClassName="rowSaleVendorClassFn"
                        :scroll="{ y: scrollYPerformNumber }"
                        :customRow="customSaleVendorRowFn"
                        @change="changeSaleVendorPaginationFn"
                    >
                        <div slot="status" slot-scope="text, record">
                            {{ record.status | formatStatus('planList') }}
                        </div>
                    </a-table>
                </a-tab-pane>
                <a-tab-pane :tab="tabsTextComputed(2)" style="padding: 8px" :key="systemTypes[2]">
                    <div v-if="myOrderPropObj.pageType === 0">
                        <vxe-table
                            ref="xPurchaseHistoryTable"
                            size="small"
                            :height="scrollYPurchaseDelarNumber - 4"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            @cell-click="cellClickPurchaseHistoryFn"
                            @cell-dblclick="cellDblclickPurchaseHistoryFn"
                            align="center"
                            :data="purchaseHistoryList"
                            :loading="purchaseHistoryPagination.loading"
                            show-overflow
                            show-header-overflow
                            showHeader
                            :start-index="(purchaseHistoryPagination.current - 1) * purchaseHistoryPagination.pageSize"
                        >
                            <vxe-table-column width="150px" filed="partB_name" :title="historyColumnsComputed">
                                <template v-slot="{ row }">
                                    <span>{{ row.partB_name }} </span>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                v-for="performItem in performColumns"
                                :key="performItem.key"
                                :width="performItem.width"
                                :field="performItem.dataIndex"
                                :title="performItem.title"
                            ></vxe-table-column>
                            <vxe-table-column width="80px" field="status" title="状态">
                                <template v-slot="{ row }">
                                    <span>{{ row.status | formatStatus('planList') }}</span>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column width="60px" field="status" title="来源">
                                <template v-slot="{ row }">
                                    <span>{{ row.create_organization_type | formatOrgType() }}</span>
                                </template>
                            </vxe-table-column>
                        </vxe-table>
                        <vxe-pager
                            :loading="purchaseHistoryPagination.loading"
                            :current-page="purchaseHistoryPagination.current"
                            :page-size="purchaseHistoryPagination.pageSize"
                            :page-sizes="purchaseHistoryPagination.pageSizes"
                            :total="purchaseHistoryPagination.total"
                            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                            @page-change="changePurchaseHistoryPaginationFn"
                        >
                        </vxe-pager>
                    </div>
                    <!-- <a-table v-if="myOrderPropObj.pageType === 0"
                             :columns="performColumns"
                             :dataSource="purchaseHistoryList"
                             :loading="purchaseHistoryPagination.loading"
                             :pagination="{
                                 size: 'small',
                                 current: purchaseHistoryPagination.current,
                                 total: purchaseHistoryPagination.total,
                                 pageSize: purchaseHistoryPagination.pageSize,
                                 showSizeChanger: true,
                                 showQuickJumper: true
                             }"
                             :rowClassName="rowPurchaseHistoryClassFn"
                             :scroll="{ y: scrollYPerformNumber }"
                             :customRow="customPurchaseHistoryRowFn"
                             @change="changePurchaseHistoryPaginationFn">
                        <span slot='nameTitle'>{{ historyColumnsComputed }}</span>

                        <div slot="status"
                             slot-scope="text, record">
                            {{ record.status | formatStatus('planList') }}
                        </div>
                    </a-table> -->
                    <a-table
                        v-else-if="myOrderPropObj.pageType === 1"
                        :columns="salesHistoryColumns"
                        :dataSource="saleHistoryList"
                        :loading="saleHistoryPagination.loading"
                        :pagination="{
                            size: 'small',
                            current: saleHistoryPagination.current,
                            total: saleHistoryPagination.total,
                            pageSize: saleHistoryPagination.pageSize,
                            showSizeChanger: true,
                            showQuickJumper: true
                        }"
                        :rowClassName="rowSaleHistoryClassFn"
                        :scroll="{ y: scrollYPerformNumber }"
                        :customRow="customSaleHistoryRowFn"
                        @change="changeSaleHistoryPaginationFn"
                    >
                        <span slot="nameTitle">{{ historyColumnsComputed }}</span>

                        <div slot="status" slot-scope="text, record">
                            {{ record.status | formatStatus('planList') }}
                        </div>
                    </a-table>
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
            <!--表格区域-->
        </div>
        <!-- tab切换区域结束 -->

        <!-- 采购订单【包含旧版的销售订单】新增/修改区域开始 -->
        <a-modal
            v-model="planModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-add global-drag"
            :class="isAddProductModalTableMax ? 'my-modal-zoom' : ''"
            :width="1200"
            :zIndex="3"
            :keyboard="false"
            :afterClose="afterClosePlanmodalFn"
        >
            <div v-globalDrag="{ el: 'modal-add' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ planModal.title }}
                </h6>
                <a-button
                    @click="addProductZoomFn"
                    class="modal-header-zoom"
                    type="primary"
                    :icon="isAddProductModalTableMax ? 'fullscreen-exit' : 'fullscreen'"
                    size="large"
                />
                <!-- <a-button
                    v-show="!isAddProductModalTableMax"
                    @click="addProductZoomFn"
                    class="modal-header-zoom"
                    type="primary" 
                    icon="switcher" 
                    size="large" /> -->
            </div>
            <template slot="footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button @click="planModalGroupBtnFn('print')" key="print" :loading="planModal.loadingPrint">
                            打印
                        </a-button>
                        <a-button
                            @click="planModalGroupBtnFn('exportExcel')"
                            key="exportExcel"
                            :loading="planModal.loadingExportExcel"
                        >
                            导出Excel
                        </a-button>
                        <a-button
                            v-if="myOrderPropObj.pageType === 1"
                            :disabled="isAddPlanModalDisabled"
                            @click="planModalGroupBtnFn('add')"
                            :loading="planModal.loadingAdd"
                            key="add"
                        >
                            生成销售单
                        </a-button>
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
                                planModalForm.status === 2 && planModalForm.create_organization_type === 1
                                    ? '确认'
                                    : '保存'
                            }}
                        </a-button>
                        <!--<a-button v-show="planModalForm.status === 2 && planModalForm.create_organization_type !== 0"-->
                        <!--          @click="planModalGroupBtnFn('savePo')"-->
                        <!--          key="submitPo"-->
                        <!--          type="primary"-->
                        <!--          :loading="planModal.loadingSave">-->
                        <!--    保存-->
                        <!--</a-button>-->
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
                    <span style="font-size: 24px; color: #444">{{ modalTopTitle }}订单</span>
                    <div v-if="btnName !== 'add'" style="position: absolute; right: 0; display: flex">
                        <span class="label" style="font-size: 14px; color: rgba(0, 0, 0, 0.65)"
                            ><em class="em red">*</em>单据编号</span
                        >
                        <a-input
                            v-if="myOrderPropObj.pageType === 0"
                            disabled
                            v-model="planModalForm.po_sn"
                            placeholder=""
                            class="input"
                        />
                        <a-input
                            v-else-if="myOrderPropObj.pageType === 1"
                            disabled
                            v-model="planModalForm.so_sn"
                            placeholder=""
                            class="input"
                        />
                    </div>
                </h6>
                <ul class="top-list" :style="myOrderPropObj.pageType === 1 ? 'width:60%' : 'width: 100%'">
                    <li class="item">
                        <span class="label"><em class="em red">*</em>供货单位</span>
                        <a-input
                            class="input"
                            type="text"
                            :disabled="btnName === 'edit'"
                            v-model="partB.oname"
                            @click="openChooseTb"
                            @keydown.enter="relevantInstitutionsKeyDownFn"
                            v-focus="relevantInstitutionsFocus"
                            placeholder=""
                        />
                    </li>
                    <li v-if="myOrderPropObj.pageType === 0" class="item">
                        <span class="label"><em class="em red">*</em>采购员</span>
                        <a-tree-select
                            :disabled="isEditDisabled"
                            treeNodeFilterProp="title"
                            searchPlaceholder=""
                            :showSearch="true"
                            :treeData="planModalEmployes"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            allowClear
                            :value="planModalEmployeSn | numberToStringFilter"
                            @change="changePlanModalEmployeSnFn"
                            class="input"
                            style="min-width: 120px"
                        >
                        </a-tree-select>
                    </li>
                    <li v-if="myOrderPropObj.pageType === 0" class="item">
                        <span class="label"><em class="em red">*</em>采购部门</span>
                        <a-tree-select
                            treeNodeFilterProp="title"
                            :showSearch="true"
                            :disabled="isEditDisabled"
                            :treeData="planModalDepartmentSns"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            :allowClear="false"
                            :value="planModalDepartmentSn | numberToStringFilter"
                            @change="changePlanModalDepartmentSnFn"
                            class="input"
                            style="min-width: 120px"
                        >
                        </a-tree-select>
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
                    <li class="item">
                        <span class="label"><em class="em red">*</em>仓库</span>
                        <a-tree-select
                            treeNodeFilterProp="title"
                            showSearch
                            :disabled="isWarehouseEditDisabled"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            :treeData="planModalWarehouseData"
                            placeholder=""
                            treeDefaultExpandAll
                            allowClear
                            :value="planModalWarehouseSn | numberToStringFilter"
                            @change="changewareHouseFn"
                        >
                        </a-tree-select>
                    </li>
                </ul>
                <div
                    ref="applyTable"
                    class="tab-table-wrap cur-overflow-hidden"
                    style="padding-bottom: 0; margin-top: 0"
                >
                    <!-- <div
                        class="top-add-product">
                        <a-tooltip placement="topLeft" title="添加产品">
                            <a-button
                                :disabled="isEditDisabled"
                                @click="dblclickTableFn"
                                icon="plus"
                                style="height:24px;padding:0 4px;"
                            ></a-button>
                        </a-tooltip>
                    </div> -->
                    <vxe-grid
                        v-if="myOrderPropObj.pageType === 0"
                        ref="xTablePlanModalInfo"
                        size="small"
                        :keyboard-config="{ isArrow: true }"
                        :data="planModalDatas"
                        :loading="planModalPagination.loading"
                        :height="(isAddProductModalTableMax ? scrollYAddProductNumber : 310) - ipadHeight"
                        border
                        resizable
                        auto-resize
                        show-overflow
                        show-header-overflow
                        highlight-current-row
                        :row-class-name="rowClassNameFn"
                        @keydown="buyTableKeydownFn"
                        @cell-click="cellAddInfoClickFn"
                        @cell-dblclick="cellAddInfoDblclickFn"
                        align="center"
                        show-footer
                        :footer-method="footerMethodFn"
                        :footer-cell-class-name="footerCellClassNameFn"
                        :toolbar-config="{ zoom: false }"
                    >
                        <vxe-table-column field="index" title="NO." width="50" :resizable="false" fixed="left">
                            <template v-slot="{ row, seq }">
                                <div>
                                    <i :class="!row.detail_inner_sn ? 'red-dot-add' : 'hide'"></i>
                                    <span>{{ seq }}</span>
                                </div>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="product_name" title="产品" width="144px">
                            <template v-slot="{ row }">
                                <span v-if="!!row.product_inner_sn">{{ row.product_name }}</span>
                                <a-input
                                    v-else
                                    v-model="row.product_name"
                                    v-focus="row.focusDatas[0]"
                                    class="input"
                                    style="width: 100%"
                                />
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="specification" title="规格型号" width="100px"></vxe-table-column>
                        <vxe-table-column field="product_vendor_name" title="厂商/经销商" width="120px">
                            <template v-slot="{ row }">
                                {{ row.product_vendor_name || row.partB_name }}
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="status" title="状态" width="95px">
                            <template v-slot="{ row }">
                                <span v-if="!row.product_inner_sn"></span>
                                <span v-else>{{ row.status | formatStatus('planOrder') }}</span>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="quantity" title="数量" width="90px">
                            <template v-slot="{ row, rowIndex }">
                                <span v-if="!row.product_inner_sn"></span>
                                <a-tooltip v-else :trigger="['focus']">
                                    <span slot="title">数量：{{ row.quantity }}</span>
                                    <a-input-number
                                        :disabled="isSourceBuyerAudited ? false : isEditDisabled"
                                        :min="0.001"
                                        :formatter="
                                            (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')
                                        "
                                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                                        :value="row.quantity"
                                        @change="(value) => updateProductQuantity(value, rowIndex)"
                                        class="input"
                                        style="width: 100%"
                                    />
                                </a-tooltip>
                            </template>
                        </vxe-table-column>
                        <!-- <vxe-table-column
                            field="quantity_delivered"
                            :title="myOrderPropObj.pageType === 0 ? '已到货数量' : '已发货数量'"
                            width="8%"
                        >
                            <template>
                                -
                            </template>
                        </vxe-table-column> -->
                        <vxe-table-column field="unit_inner_sn" title="包装单位" width="100px">
                            <template v-slot="{ row, seq }">
                                <span v-if="!row.product_inner_sn"></span>
                                <a-cascader
                                    v-else
                                    :disabled="isEditDisabled"
                                    :allowClear="false"
                                    :options="row.unitList"
                                    v-model="row.unit_inner_sn"
                                    changeOnSelect
                                    :displayRender="displayUnitRender"
                                    @click.stop="clickUnitListFn(row, seq)"
                                    @change="changeUnitListFn"
                                    placeholder=""
                                    style="width: 100%"
                                />
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="batch_number" title="批号" width="90px">
                            <template v-slot="{ row }">
                                <span v-if="!row.product_inner_sn"></span>
                                <a-input
                                    v-else
                                    :disabled="isEditDisabled"
                                    v-model="row.batch_number"
                                    class="input"
                                    style="width: 100%"
                                    @change="updateFooterPlanModalFnBN"
                                />
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="unit_price" title="单价" width="100px" align="right">
                            <template v-slot="{ row }">
                                <span v-if="!row.product_inner_sn"></span>
                                <span v-else>{{ unit_price_record(row) }}</span>
                            </template>
                            <!-- <template v-slot="{ row }">
                                <a-input-number
                                    :disabled="isSourceBuyerAudited ? false : isEditDisabled"
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
                        <vxe-table-column field="money" title="金额" width="100px" align="right">
                            <template v-slot="{ row }">
                                <span v-if="!row.product_inner_sn"></span>
                                <span v-else>{{ money_record(row) }}</span>
                            </template>
                        </vxe-table-column>
                        <!-- <vxe-table-column
                            field="discount"
                            title="折扣"
                            width="10%"
                        >
                            <template v-slot="{ row }">
                                <a-input-number
                                    :disabled="isEditDisabled"
                                    :min="0.01"
                                    :step="0.01"
                                    v-model="row.discount"
                                    class="input"
                                    style="width:100%"
                                    @change="updateFooterPlanModalFn"
                                />
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            field="discount_unit_price"
                            title="折后单价"
                            width="10%"
                        >
                            <template v-slot="{ row }">
                                <div>{{ discount_unit_price_record(row) }}</div>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            field="discount_money"
                            title="折后金额"
                            width="10%"
                        >
                            <template v-slot="{ row }">
                                <div>{{ discount_money_record(row) }}</div>
                            </template>
                        </vxe-table-column> -->
                        <vxe-table-column field="rate" title="税率" width="90px">
                            <template v-slot="{ row, rowIndex }">
                                <span v-if="!row.product_inner_sn"></span>
                                <a-select
                                    v-else
                                    :disabled="isEditDisabled"
                                    v-model="row.rate"
                                    style="width: 100%"
                                    @change="(value) => updateProductRate(value, rowIndex)"
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
                        <vxe-table-column field="tax_unit_price" title="含税单价" width="80px" align="right">
                            <template v-slot="{ row, rowIndex }">
                                <span v-if="!row.product_inner_sn"></span>
                                <a-dropdown v-else :trigger="['click']" :disabled="isPriceDisabled">
                                    <a-tooltip :trigger="['focus']">
                                        <span slot="title"
                                            >含税单价：{{ $XEUtils.toFixed(row.tax_unit_price, 4) }}</span
                                        >
                                        <a-input-number
                                            :disabled="isPriceDisabled"
                                            :min="0"
                                            :value="row.tax_unit_price"
                                            @change="(value) => updateProductTaxUnitPrice(value, rowIndex)"
                                            class="input"
                                            style="width: 100%"
                                        />
                                    </a-tooltip>
                                    <a-menu v-if="row.unitPriceList && row.unitPriceList.length" slot="overlay">
                                        <a-menu-item
                                            v-for="(priceItem, priceIndex) in row.unitPriceList"
                                            :key="priceIndex"
                                            @click="updateProductTaxUnitPrice(priceItem, rowIndex)"
                                        >
                                            <a href="javascript:;">{{ priceItem }}</a>
                                        </a-menu-item>
                                    </a-menu>
                                </a-dropdown>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="tax_amount" title="含税金额" width="100px" align="right">
                            <template v-slot="{ row, rowIndex }">
                                <span v-if="!row.product_inner_sn"></span>
                                <a-tooltip v-else :trigger="['focus']">
                                    <span slot="title">含税金额：{{ $XEUtils.toFixed(row.tax_amount, 4) }}</span>
                                    <a-input-number
                                        class="input"
                                        style="width: 100%"
                                        :disabled="isPriceDisabled"
                                        :min="0"
                                        :value="row.tax_amount"
                                        @change="(value) => updateProductTaxAmountTotal(value, rowIndex)"
                                    />
                                </a-tooltip>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="tax" title="税额" width="80px" align="right">
                            <template v-slot="{ row }">
                                <span v-if="!row.product_inner_sn"></span>
                                <div v-else>{{ tax_record(row) }}</div>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            v-if="!(planModalDatas.length <= 0 || planModalForm.status === 2)"
                            field="action"
                            title="操作"
                            width="90"
                            :resizable="false"
                            fixed="right"
                        >
                            <template v-slot="{ row, seq }">
                                <ul v-if="row.product_inner_sn" class="action-btn-list-wrap" style="position: relative">
                                    <li
                                        v-if="
                                            btnName === 'add' ||
                                            planModalForm.status === 0 ||
                                            planModalForm.status === 1
                                        "
                                    >
                                        <a-tooltip class="tooltip" placement="top" title="删除">
                                            <i
                                                @click.stop="planModalActionBtnFn($event, 'remove', row, seq)"
                                                class="yrt yrt-shanchu-copy"
                                                :class="isEditDisabled ? 'disabled' : 'cancel'"
                                            ></i>
                                        </a-tooltip>
                                    </li>
                                    <li v-else-if="planModalForm.status === 0 || planModalForm.status === 3">
                                        <a-switch
                                            v-if="row.status === 0 || row.status === 2"
                                            @click="planModalActionBtnFn($event, 'stop', row, seq)"
                                            :loading="row.loading"
                                            :checked="row.switch"
                                            size="small"
                                            checkedChildren="终止"
                                            unCheckedChildren="反终止"
                                            defaultChecked
                                            style="color: #faad14; width: 64px"
                                        />
                                        <a-switch
                                            v-else-if="row.status === 3"
                                            @click="planModalActionBtnFn($event, 'stop', row, seq)"
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
                        <!-- <template v-slot:toolbar_buttons>
                            <a-button-group 
                                v-if="isAddProductModalTableMax"
                                size="small">
                                <a-button
                                    :disabled="isEditDisabled"
                                    @click="dblclickTableFn"
                                    type="primary"
                                    icon="plus"
                                >Enter</a-button>
                                <a-button
                                    @click="addProductZoomFn"
                                    type="primary" 
                                    icon="switcher" 
                                >Esc</a-button>
                            </a-button-group>
                        </template> -->
                    </vxe-grid>
                    <a-table
                        v-else-if="myOrderPropObj.pageType === 1"
                        :rowSelection="rowSelectionPlanModal"
                        :locale="{ emptyText: '双击此处添加产品' }"
                        :loading="planModalPagination.loading"
                        :columns="
                            planModalDatas.length <= 0 || planModalForm.status === 2
                                ? planModalHideActionsColumns
                                : planModalColumns
                        "
                        :dataSource="planModalDatas"
                        :pagination="false"
                        :rowClassName="rowClassPlanModalFn"
                        :scroll="{ x: 2200, y: 290 }"
                        :customRow="customRowPlanModalFn"
                    >
                        <div slot="index" slot-scope="text, record, index">
                            <span
                                v-if="index === planModalDatas.length - 1"
                                style="color: #f00; font-size: 16px; font-weight: 700"
                            >
                            </span>
                            <span v-else>{{ index + 1 }}</span>
                        </div>
                        <div @click.stop="() => {}" slot="quantity" slot-scope="text, record, index">
                            <span
                                v-if="index === planModalDatas.length - 1"
                                style="color: #f00; font-size: 16px; font-weight: 700"
                            >
                            </span>
                            <a-input-number
                                v-else
                                :disabled="isEditDisabled"
                                :min="1"
                                v-model="record.quantity"
                                :formatter="(value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                                :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                                class="input"
                            />
                        </div>
                        <div slot="status" slot-scope="text, record, index">
                            <span v-if="index === planModalDatas.length - 1"> </span>
                            <span v-else>
                                {{ record.status | formatStatus('planOrder') }}
                            </span>
                        </div>
                        <div slot="unit" slot-scope="text, record, index">
                            <span v-if="index === planModalDatas.length - 1"> </span>
                            <a-cascader
                                v-else
                                :disabled="isEditDisabled"
                                :allowClear="false"
                                :options="record.unitList"
                                v-model="record.unit_inner_sn"
                                changeOnSelect
                                :displayRender="displayUnitRender"
                                @click.stop="clickUnitListFn(record)"
                                @change="changeUnitListFn"
                                placeholder=""
                            />
                        </div>
                        <div @click.stop="() => {}" slot="unit_price" slot-scope="text, record, index">
                            <span
                                v-if="index === planModalDatas.length - 1"
                                style="color: #f00; font-size: 16px; font-weight: 700"
                            >
                            </span>
                            <a-input-number
                                v-else
                                :disabled="isEditDisabled"
                                :min="0.01"
                                :step="0.01"
                                v-model="record.unit_price"
                                class="input"
                            />
                        </div>
                        <div @click.stop="() => {}" slot="money" slot-scope="text, record, index">
                            <span
                                v-if="index === planModalDatas.length - 1"
                                style="color: #f00; font-size: 16px; font-weight: 700"
                            >
                                {{ money_total }}
                            </span>
                            <span v-else>{{ money_record(record) }}</span>
                        </div>
                        <div @click.stop="() => {}" slot="discount" slot-scope="text, record, index">
                            <span
                                v-if="index === planModalDatas.length - 1"
                                style="color: #f00; font-size: 16px; font-weight: 700"
                            >
                            </span>
                            <a-input-number
                                v-else
                                :disabled="isEditDisabled"
                                :min="0.01"
                                :step="0.01"
                                v-model="record.discount"
                                class="input"
                            />
                        </div>
                        <div @click.stop="() => {}" slot="discount_unit_price" slot-scope="text, record, index">
                            <span
                                v-if="index === planModalDatas.length - 1"
                                style="color: #f00; font-size: 16px; font-weight: 700"
                            >
                            </span>
                            <span v-else>{{ discount_unit_price_record(record) }}</span>
                        </div>
                        <div @click.stop="() => {}" slot="discount_money" slot-scope="text, record, index">
                            <span
                                v-if="index === planModalDatas.length - 1"
                                style="color: #f00; font-size: 16px; font-weight: 700"
                            >
                                {{ discount_money_total }}
                            </span>
                            <span v-else>{{ discount_money_record(record) }}</span>
                        </div>
                        <div @click.stop="() => {}" slot="rate" slot-scope="text, record, index">
                            <span
                                v-if="index === planModalDatas.length - 1"
                                style="color: #f00; font-size: 16px; font-weight: 700"
                            >
                            </span>
                            <a-input-number
                                v-else
                                :disabled="isEditDisabled"
                                :min="0"
                                :max="100"
                                v-model="record.rate"
                                class="input"
                            />
                        </div>
                        <div @click.stop="() => {}" slot="tax_unit_price" slot-scope="text, record, index">
                            <span
                                v-if="index === planModalDatas.length - 1"
                                style="color: #f00; font-size: 16px; font-weight: 700"
                            >
                            </span>
                            <span v-else>{{ tax_unit_price_record(record) }}</span>
                        </div>
                        <div @click.stop="() => {}" slot="tax_amount" slot-scope="text, record, index">
                            <span
                                v-if="index === planModalDatas.length - 1"
                                style="color: #f00; font-size: 16px; font-weight: 700"
                            >
                                {{ tax_amount_total }}
                            </span>
                            <span v-else>{{ tax_amount_record(record) }}</span>
                        </div>
                        <div @click.stop="() => {}" slot="tax" slot-scope="text, record, index">
                            <span
                                v-if="index === planModalDatas.length - 1"
                                style="color: #f00; font-size: 16px; font-weight: 700"
                            >
                                {{ tax_total }}
                            </span>
                            <span v-else>{{ tax_record(record) }}</span>
                        </div>
                        <div slot="action" slot-scope="text, record, index" @click.stop="() => {}">
                            <span v-if="index === planModalDatas.length - 1"> </span>
                            <ul v-else class="action-btn-list-wrap" style="position: relative">
                                <li
                                    v-if="btnName === 'add' || planModalForm.status === 0 || planModalForm.status === 1"
                                >
                                    <a-tooltip class="tooltip" placement="top" title="删除">
                                        <i
                                            @click.stop="planModalActionBtnFn($event, 'remove', record, index)"
                                            class="yrt yrt-shanchu-copy"
                                            :class="isEditDisabled ? 'disabled' : 'cancel'"
                                        ></i>
                                    </a-tooltip>
                                </li>
                                <li v-else-if="planModalForm.status === 0 || planModalForm.status === 3">
                                    <a-switch
                                        v-if="parseInt(row.status) !== 1"
                                        :disabled="activeTabsKey === tabTypes[2]"
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
                        </div>
                    </a-table>
                </div>
                <ul class="order-modal-list">
                    <li>
                        <div class="item-wrap">
                            <span class="label" style="width: auto">送货地址</span>
                            <a-input
                                v-model="planModalLocation"
                                :disabled="isEditDisabled"
                                placeholder=""
                                class="input"
                                @click="clickDescriptionFn"
                                @change="changeDeliveryAddressFn"
                            />
                        </div>
                    </li>
                    <li>
                        <div class="item-wrap">
                            <span class="label" style="width: auto">摘要</span>
                            <a-input
                                :disabled="isEditDisabled"
                                v-model="planModalForm.description"
                                placeholder=""
                                class="input"
                                @click="clickDescriptionFn"
                            />
                        </div>
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
        <!-- 采购订单【包含旧版的销售订单】新增/修改区域结束 -->

        <!-- 选择采购单位区域开始 -->
        <a-modal
            destroyOnClose
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
                    :searchOrgName="searchOrgName"
                    :isParentKeyDownEnter="isParentKeyDownEnter"
                    :partType="1"
                    :pageTypeName="pageTypeName"
                ></chooseOrgTab>
            </div>
        </a-modal>
        <!-- 选择采购单位区域结束 -->

        <!-- 添加产品区域开始 -->
        <a-modal
            :title="addProductMd.title"
            v-model="addProductMd.alert"
            :maskClosable="false"
            :mask="false"
            :width="1000"
            :centered="true"
            @cancel="closeAddProductFn"
            class="modal-form-input-scoped global-drag add_product"
        >
            <div v-globalDrag="{ el: 'add_product' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addProductMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="closeAddProductFn">关闭</a-button>
                <a-button :loading="addProductMd.loading" @click="addProductFn" type="primary">添加</a-button>
            </template>
            <add-product ref="chooseProMd" @returnData="getProReturn" :partB="partB"></add-product>
        </a-modal>
        <!-- 添加产品区域结束 -->
    </div>
</template>

<script>
import moment from 'moment';
import { mapState } from 'vuex';
import addProduct from '@/components/addProduct';
import {
    orderPurchaseConfirmAPI,
    orderSaleConfirmAPI,
    orderDetailStopAPI,
    orderDetailUnstopAPI,
    orderSaleListAPI,
    orderHistorySaleListAPI,
    orderSaleInfoAPI,
    orderSaleAddAPI,
    orderSaleEditAPI,
    orderConfirmAPI,
    orderStopAPI,
    orderSaleBuildAPI,
    orderSaleDetailBuildAPI,
    platformUnitTree,
    platformUnitParents,
    dealerUnitTreeAPI,
    dealerUnitParentsAPI,
    orderPurchaseEditAPI,
    orderDelAPI,
    orderDetailDelAPI,
    orderOrgListAPI,
    orgDeptTopTreeAPI,
    orgEmpDropdownListAPI,
    orderPurchaseListAPI,
    orderHistoryPurchaseListAPI,
    orderPurchaseInfoAPI,
    orderPurchaseAddAPI,
    contractGetOrderPriceAPI
} from '@/service/pageAjax';
import { BillsType } from '@/config/constants';
import chooseOrgTab from '@/components/chooseOrgTab';

// 产品的状态文本map
const productStatusTextMap = ['处理中', '已完全处理', '已计划采购', '已终止处理'];

export default {
    name: 'mainOrder',
    props: {
        myOrderPropObj: {
            pageType: -1 // 页面类型判断依据： 0-采购订单  1-销售订单
        }
    },
    components: {
        chooseOrgTab,
        addProduct
    },
    data() {
        return {
            pageTypeName: undefined,
            addProductEnter: false,
            relevantInstitutionsFocus: true,
            format: 'YYYY-MM-DD HH:mm:ss',
            topSearchEmpDropdownList: [], // 人员
            topSearchEmpDropdownSn: [],
            topSearchAllTreeList: [], // 部门
            topSearchAllTreeListSn: [],
            // 部门
            editInfoFormDepartmentInnerSn: [], // 所属部门
            architectureAllTrees: [], // 所属部门的信息列表
            cookieInfo: this.$cookie,
            searchFormObj: {
                partB_name: '',
                order_sn: '',
                buyer_id: '',
                department_id: '',
                createTimeRange: [] // 订单生成时间区间（空数组或[moment, moment]）
            },
            billsType: '', // 采购订单内页中临时记住采购单位的类型
            tabTexts: ['经销商', '厂商', '医院', '经销商', '历史'], // 顶部表格的tab切换
            showSexTip01: false,
            performColumns: [
                // 采购订单的头部
                // {
                //     title: '',
                //     dataIndex: 'partB_name',
                //     align: 'center',
                //     width: '19%',
                //     slots: { title: 'nameTitle' }
                // },
                {
                    title: '单据编号',
                    dataIndex: 'po_sn',
                    align: 'center',
                    width: '160px'
                },
                {
                    title: '采购员',
                    dataIndex: 'buyer_name',
                    align: 'center',
                    width: '80px'
                },
                {
                    title: '采购部门',
                    dataIndex: 'department_name',
                    align: 'center',
                    width: '100px'
                },
                {
                    title: '期望到货日期',
                    dataIndex: 'expect_aog_date',
                    align: 'center',
                    width: '110px'
                },
                {
                    title: '单据生成时间',
                    dataIndex: 'create_time',
                    align: 'center',
                    width: '150px'
                }
            ],
            salesHospitalColumns: [
                // 销售订单的头部 【医院】
                {
                    title: '医院',
                    dataIndex: 'partA_name',
                    align: 'center',
                    width: '20%'
                },
                {
                    title: '医院别名',
                    dataIndex: 'alias',
                    align: 'center',
                    width: '20%'
                },
                {
                    title: '单据编号',
                    dataIndex: 'so_sn',
                    align: 'center',
                    width: '14%'
                },
                {
                    title: '期望到货日期',
                    dataIndex: 'expect_aog_date',
                    align: 'center',
                    width: '16%'
                },
                {
                    title: '单据生成时间',
                    dataIndex: 'create_time',
                    align: 'center',
                    width: '16%'
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    width: '14%',
                    scopedSlots: { customRender: 'status' }
                }
            ],
            salesDealerColumns: [
                // 销售订单的头部 【经销商】
                {
                    title: '经销商',
                    dataIndex: 'partA_name',
                    align: 'center',
                    width: '28%'
                },
                {
                    title: '单据编号',
                    dataIndex: 'so_sn',
                    align: 'center',
                    width: '25%'
                },
                {
                    title: '期望到货日期',
                    dataIndex: 'expect_aog_date',
                    align: 'center',
                    width: '17%'
                },
                {
                    title: '单据生成时间',
                    dataIndex: 'create_time',
                    align: 'center',
                    width: '17%'
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    width: '13%',
                    scopedSlots: { customRender: 'status' }
                }
            ],
            salesHistoryColumns: [
                // 销售订单的头部 【历史】
                {
                    dataIndex: 'partA_name',
                    align: 'center',
                    width: '28%',
                    slots: { title: 'nameTitle' }
                },
                {
                    title: '单据编号',
                    dataIndex: 'so_sn',
                    align: 'center',
                    width: '25%'
                },
                {
                    title: '期望到货日期',
                    dataIndex: 'expect_aog_date',
                    align: 'center',
                    width: '17%'
                },
                {
                    title: '单据生成时间',
                    dataIndex: 'create_time',
                    align: 'center',
                    width: '17%'
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    width: '13%',
                    scopedSlots: { customRender: 'status' }
                }
            ],
            purchaseDelarList: [],
            purchaseDelarPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentPurchaseDelarTrObj: {},
            purchaseVendorList: [],
            purchaseVendorPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentPurchaseVendorTrObj: {},
            purchaseHistoryList: [],
            purchaseHistoryPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentPurchaseHistoryTrObj: {},
            saleDelarList: [],
            saleDelarPagination: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentSaleDelarTrObj: {},
            saleVendorList: [],
            saleVendorPagination: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentSaleVendorTrObj: {},
            saleHistoryList: [],
            saleHistoryPagination: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentSaleHistoryTrObj: {},
            purchaseDelarSelectedRowKeys: [],
            purchaseVendorSelectedRowKeys: [],
            saleDelarSelectedRowKeys: [],
            saleVendorSelectedRowKeys: [],
            systemTypes: ['perform', 'past', 'history'],
            tabsActiveKey: 'perform',
            isTabsActiveVendorKey: false,
            isTabsActiveHistoryKey: false,
            btnName: '',
            // 订单弹框
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
            expectAogDate: null,
            planModalColumns: [
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
                    title: this.myOrderPropObj.pageType === 0 ? '已到货数量' : '已发货数量',
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
                {
                    title: '折扣',
                    dataIndex: 'discount',
                    key: '11',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount' }
                },
                {
                    title: '折后单价',
                    dataIndex: 'discount_unit_price',
                    key: '12',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount_unit_price' }
                },
                {
                    title: '折后金额',
                    dataIndex: 'discount_money',
                    key: '13',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount_money' }
                },
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
                    title: this.myOrderPropObj.pageType === 0 ? '已到货数量' : '已发货数量',
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
                {
                    title: '折扣',
                    dataIndex: 'discount',
                    key: '11',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount' }
                },
                {
                    title: '折后单价',
                    dataIndex: 'discount_unit_price',
                    key: '12',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount_unit_price' }
                },
                {
                    title: '折后金额',
                    dataIndex: 'discount_money',
                    key: '13',
                    width: 100,
                    align: 'center',
                    scopedSlots: { customRender: 'discount_money' }
                },
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
            planModalOrgS: [],
            planModalEmployeSn: [],
            planModalDepartmentSn: [],
            planModalEmployes: [],
            planModalDepartmentSns: [],
            planModalLocation: '',
            planModalWarehouseData: [],
            planModalWarehouse: '',
            planModalWarehouseSn: '',
            planModalDatas: [],
            isAddProductModalTableMax: false, // 是否是全屏模式，默认不是
            planModalPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            planModalSelectedRowKeys: [],
            currentTrPlanModalObj: {},
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
                billsType: '',
                partA_id: '',
                partB_id: '',
                po_sn: '',
                po_sn_old: '',
                po_so_inner_sn: '',
                warehouse_inner_sn: '',
                warehouse_name: '',
                delivery_address: '',
                status: ''
            },
            editInfoFormNewObj: {},
            detailList: [],
            // 添加产品
            addProductMd: {
                title: `${this.$route.meta.title} - 新增 - 添加产品`,
                alert: false,
                loading: false
            },
            partB: {
                // 新增产品窗口给子组件传递值
                partA_id: '',
                partB_id: '',
                billsType: '',
                oname: '',
                modalType: 4, // 1 - 合同内的产品  2 - 期初库存的产品列表  3 - 采购计划  4 - 采购/销售订单
                isProcurementSalesFlag: false // true - 销售单  false - 采购单
            },
            searchOrgName: '',
            isParentKeyDownEnter: false,
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            chooseOrgMd: {
                title: `${this.$route.meta.title} - 供货单位`,
                alert: false,
                loading: false
            },
            addInfoModalForm: {
                department_inner_sn: '',
                department_name: '',
                description: '',
                details: '',
                grn_sn: '',
                odo_sn: '',
                odo_type: '',
                handler_inner_sn: '',
                handler_name: '',
                org_id: '',
                organization_inner_sn: '',
                organization_name: '',
                status: '',
                type_inner_sn: '',
                type_name: '',
                // 仓库id
                warehouse_inner_sn: '',
                // 仓库名
                warehouse_name: '',
                warehouse_operator_inner_sn: '',
                warehouse_operator_name: '',
                billsType: ''
            },
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 57 : 0
        };
    },
    methods: {
        // 【采购单位列表】
        async orderOrgListAPIFn(systemType, oid) {
            // isSale: 是否销售订单 0-不是 1-是
            await orderOrgListAPI(systemType, oid, this.myOrderPropObj.pageType).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.planModalOrgs = list.map((item) => {
                        return {
                            ...item,
                            value: item.org_id + '-' + item.billsType,
                            label: item.name
                        };
                    });
                    if (this.btnName === 'add') {
                        // 默认赋值
                        if (this.planModalOrgs.length) {
                            const curOrg = this.planModalOrgs[0].value,
                                arySplitValue = curOrg.split('-');
                            this.planModalOrgSn = [curOrg];
                            this.partB.oname = this.planModalOrgs[0].label;
                            if (this.myOrderPropObj.pageType === 0) {
                                this.planModalForm.partB = arySplitValue[0];
                                this.partB.partB_id = arySplitValue[0];
                            } else if (this.myOrderPropObj.pageType === 1) {
                                this.planModalForm.partA = arySplitValue[0];
                                this.partB.partA_id = arySplitValue[0];
                            }
                            this.billsType = arySplitValue[1];
                            this.partB.billsType = arySplitValue[1];
                        } else {
                            this.planModalOrgSn = [];
                            this.partB.oname = '';
                            if (this.myOrderPropObj.pageType === 0) {
                                this.planModalForm.partB = '';
                            } else if (this.myOrderPropObj.pageType === 1) {
                                this.planModalForm.partA = '';
                            }
                            this.billsType = '';
                            this.partB.billsType = '';
                        }
                    }
                    this.planModalForm.billsType = this.partB.billsType;
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
                    this.treeDatasIndexAryFn(list, 'deptTopTreePlan');
                } else {
                    this.$message.error(res.msg);
                }
            });
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
                        if (btnName === 'add') {
                            // 赋默认值
                            const curUnit = record.unitList[0];
                            record.unit_inner_sn = [curUnit.value];
                            record.unit_name = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xTablePlanModalInfo.reloadRow(record, record, 'unit');
                        }
                        record.detail_inner_sn && !record.isLoadingUnit && this.dealerUnitParentsAPIFn(record);
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
                        if (btnName === 'add') {
                            // 赋默认值
                            const curUnit = record.unitList[0];
                            record.unit_inner_sn = [curUnit.value];
                            record.unit_name = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xTablePlanModalInfo.reloadRow(record, record, 'unit');
                        }
                        record.detail_inner_sn && !record.isLoadingUnit && this.platformUnitParentsFn(record);
                    }
                }
            });
        },
        // 查看【历史采购订单列表】
        async orderHistoryPurchaseListAPIFn(flag) {
            this.purchaseHistoryPagination.loading = true;
            const { createTimeRange } = this.searchFormObj;
            let params = {
                partA_id: this.cookieInfo.get('userbelong'),
                pageNum: this.purchaseHistoryPagination.current,
                pageSize: this.purchaseHistoryPagination.pageSize
            };

            if (this.searchFormObj.partB_name) {
                params.partB_name = this.searchFormObj.partB_name;
            }

            if (this.searchFormObj.order_sn) {
                params.order_sn = this.searchFormObj.order_sn;
            }

            if (this.searchFormObj.buyer_id) {
                params.buyer_id = this.searchFormObj.buyer_id;
            }

            if (this.searchFormObj.department_id) {
                params.department_id = this.searchFormObj.department_id;
            }

            // 订单生成时间区间
            if (createTimeRange.length >= 2) {
                params.start_date = createTimeRange[0].format('YYYY-MM-DD');
                params.end_date = createTimeRange[1].format('YYYY-MM-DD');
            }

            await orderHistoryPurchaseListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.purchaseHistoryList = rows.map((item, index) => {
                            return {
                                ...item,
                                index,
                                key: Math.random(),
                                activeClass: '',
                                status: parseFloat(item.status)
                            };
                        });

                        if (this.purchaseHistoryList.length <= 0) {
                            this.currentPurchaseHistoryTrObj = {};
                        } else {
                            if (typeof flag === 'boolean') {
                                const curItem = this.purchaseHistoryList[this.purchaseHistoryList.length - 1];
                                this.currentPurchaseHistoryTrObj = curItem;
                                this.$refs.xPurchaseHistoryTable.setCurrentRow(curItem);
                            } else if (typeof flag === 'number') {
                                this.purchaseHistoryList.forEach((item) => {
                                    if (item.index === flag) {
                                        this.currentPurchaseHistoryTrObj = item;
                                        this.$refs.xPurchaseHistoryTable.setCurrentRow(item);
                                    }
                                });
                            } else {
                                this.currentPurchaseHistoryTrObj = this.purchaseHistoryList[0];
                                this.$refs.xPurchaseHistoryTable.setCurrentRow(this.purchaseHistoryList[0]);
                            }
                        }

                        this.purchaseHistoryPagination = {
                            ...this.purchaseHistoryPagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                        this.purchaseHistoryPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.purchaseHistoryPagination.loading = false;
                });
        },
        // 查看【历史销售订单列表】
        async orderHistorySaleListAPIFn(flag) {
            this.saleHistoryPagination.loading = true;
            let params = {
                partB_id: this.cookieInfo.get('userbelong'),
                pageNum: this.saleHistoryPagination.current,
                pageSize: this.saleHistoryPagination.pageSize
            };

            if (this.searchFormObj.partA_name) {
                params.partA_name = this.searchFormObj.partA_name;
            }

            if (this.searchFormObj.order_sn) {
                params.order_sn = this.searchFormObj.order_sn;
            }

            await orderHistorySaleListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.saleHistoryList = rows.map((item, index) => {
                            return {
                                ...item,
                                index,
                                key: Math.random(),
                                activeClass: '',
                                status: parseFloat(item.status)
                            };
                        });

                        if (this.saleHistoryList.length <= 0) {
                            this.currentSaleHistoryTrObj = {};
                        } else {
                            if (typeof flag === 'boolean') {
                                this.currentSaleHistoryTrObj = this.saleHistoryList[this.saleHistoryList.length - 1];
                            } else if (typeof flag === 'number') {
                                this.saleHistoryList.forEach((item) => {
                                    if (item.index === flag) {
                                        this.currentSaleHistoryTrObj = item;
                                    }
                                });
                            } else {
                                this.currentSaleHistoryTrObj = this.saleHistoryList[0];
                            }
                        }

                        this.saleHistoryPagination = {
                            ...this.saleHistoryPagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                        this.saleHistoryPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.saleHistoryPagination.loading = false;
                });
        },
        // 查看【采购订单列表】
        async orderPurchaseListAPIFn(tabName, flag, poSoInnerSn) {
            // tabName - 经销商/厂商/历史
            const { createTimeRange } = this.searchFormObj;
            if (tabName === this.systemTypes[0]) {
                this.purchaseDelarPagination.loading = true;
            } else if (tabName === this.systemTypes[1]) {
                this.purchaseVendorPagination.loading = true;
            }
            let params = {
                partA_id: this.cookieInfo.get('userbelong')
            };

            if (tabName === this.systemTypes[0]) {
                params.pageNum = this.purchaseDelarPagination.current;
                params.pageSize = this.purchaseDelarPagination.pageSize;
            } else if (tabName === this.systemTypes[1]) {
                params.pageNum = this.purchaseVendorPagination.current;
                params.pageSize = this.purchaseVendorPagination.pageSize;
            }

            // 1-医院 、 2-经销商、 3-厂商
            switch (parseFloat(this.cookieInfo.get('userSystemType'))) {
                case 1:
                    if (tabName === this.systemTypes[0]) {
                        // 医院 - 经销商
                        params.billsType = BillsType.HospitalDealer;
                    } else if (tabName === this.systemTypes[1]) {
                        // 医院 - 厂商
                        params.billsType = BillsType.HospitalVendor;
                    }
                    break;
                case 2:
                    if (tabName === this.systemTypes[0]) {
                        // 经销商 - 经销商
                        params.billsType = BillsType.DealerDealer;
                    } else if (tabName === this.systemTypes[1]) {
                        // 经销商 - 厂商
                        params.billsType = BillsType.DealerVendor;
                    }
                    break;
            }

            if (this.searchFormObj.partB_name) {
                params.partB_name = this.searchFormObj.partB_name;
            }

            if (this.searchFormObj.order_sn) {
                params.order_sn = this.searchFormObj.order_sn;
            }

            if (this.searchFormObj.buyer_id) {
                params.buyer_id = this.searchFormObj.buyer_id;
            }

            if (this.searchFormObj.department_id) {
                params.department_id = this.searchFormObj.department_id;
            }

            // 排序
            params.orderByColumn = 'create_time';
            params.isAsc = 'desc';

            // 订单生成时间区间
            if (createTimeRange.length >= 2) {
                params.start_date = createTimeRange[0].format('YYYY-MM-DD');
                params.end_date = createTimeRange[1].format('YYYY-MM-DD');
            }

            await orderPurchaseListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        if (tabName === this.systemTypes[0]) {
                            this.purchaseDelarList = rows.map((item, index) => {
                                let itemObj = {
                                    ...item,
                                    index,
                                    key: item.partA + '' + item.partB + item.po_so_inner_sn + item.billsType,
                                    activeClass: '',
                                    status: parseFloat(item.status)
                                };
                                if (itemObj.key === poSoInnerSn) {
                                    this.currentPurchaseDelarTrObj = itemObj;
                                    this.$refs.xPurchaseDelarTable.setCurrentRow(itemObj);
                                }
                                return itemObj;
                            });

                            if (this.purchaseDelarList.length <= 0) {
                                this.currentPurchaseDelarTrObj = {};
                            } else {
                                if (typeof flag === 'number') {
                                    this.purchaseDelarList.forEach((item, index) => {
                                        if (index === flag) {
                                            this.currentPurchaseDelarTrObj = item;
                                            this.$refs.xPurchaseDelarTable.setCurrentRow(item);
                                        }
                                    });
                                } else if (!flag) {
                                    this.currentPurchaseDelarTrObj = this.purchaseDelarList[0];
                                    this.$refs.xPurchaseDelarTable.setCurrentRow(this.purchaseDelarList[0]);
                                }
                            }

                            this.purchaseDelarPagination = {
                                ...this.purchaseDelarPagination,
                                total,
                                loading: false
                            };
                        } else if (tabName === this.systemTypes[1]) {
                            this.purchaseVendorList = rows.map((item, index) => {
                                let itemObj = {
                                    ...item,
                                    index,
                                    key: item.partA + '' + item.partB + item.po_so_inner_sn + item.billsType,
                                    activeClass: '',
                                    status: parseFloat(item.status)
                                };
                                if (itemObj.key === poSoInnerSn) {
                                    this.currentPurchaseVendorTrObj = itemObj;
                                    this.$refs.xPurchaseVendorTable.setCurrentRow(itemObj);
                                }
                                return itemObj;
                            });
                            if (this.purchaseVendorList.length <= 0) {
                                this.currentPurchaseVendorTrObj = {};
                            } else {
                                if (typeof flag === 'number') {
                                    this.purchaseVendorList.forEach((item) => {
                                        if (item.index === flag) {
                                            this.currentPurchaseVendorTrObj = item;
                                            this.$refs.xPurchaseVendorTable.setCurrentRow(item);
                                        }
                                    });
                                } else if (!flag) {
                                    this.currentPurchaseVendorTrObj = this.purchaseVendorList[0];
                                    this.$refs.xPurchaseVendorTable.setCurrentRow(this.purchaseVendorList[0]);
                                }
                            }

                            this.purchaseVendorPagination = {
                                ...this.purchaseVendorPagination,
                                total,
                                loading: false
                            };
                        }
                    } else {
                        this.$message.error(res.msg);
                        if (tabName === this.systemTypes[0]) {
                            this.purchaseDelarPagination.loading = false;
                        } else if (tabName === this.systemTypes[1]) {
                            this.purchaseVendorPagination.loading = false;
                        }
                    }
                })
                .catch(() => {
                    if (tabName === this.systemTypes[0]) {
                        this.purchaseDelarPagination.loading = false;
                    } else if (tabName === this.systemTypes[1]) {
                        this.purchaseVendorPagination.loading = false;
                    }
                });
        },
        // 查看【销售订单列表】
        async orderSaleListAPIFn(tabName, flag, poSoInnerSn) {
            // tabName - 医院/经销商/历史   poSoInnerSn - 新增订单id
            if (tabName === this.systemTypes[0]) {
                this.saleDelarPagination.loading = true;
            } else if (tabName === this.systemTypes[1]) {
                this.saleVendorPagination.loading = true;
            }
            let params = {
                partB_id: this.cookieInfo.get('userbelong')
            };

            if (tabName === this.systemTypes[0]) {
                params.pageNum = this.saleDelarPagination.current;
                params.pageSize = this.saleDelarPagination.pageSize;
            } else if (tabName === this.systemTypes[1]) {
                params.pageNum = this.saleVendorPagination.current;
                params.pageSize = this.saleVendorPagination.pageSize;
            }

            // 1-医院 、 2-经销商、 3-厂商
            switch (parseFloat(this.cookieInfo.get('userSystemType'))) {
                case 2:
                    if (tabName === this.systemTypes[0]) {
                        // 经销商 - 医院
                        params.billsType = BillsType.DealerHospital;
                    } else if (tabName === this.systemTypes[1]) {
                        // 经销商 - 经销商
                        params.billsType = BillsType.DealerDealer;
                    }
                    break;
                case 3:
                    if (tabName === this.systemTypes[0]) {
                        // 厂商 - 医院
                        params.billsType = BillsType.HospitalVendor;
                    } else if (tabName === this.systemTypes[1]) {
                        // 厂商 - 经销商
                        params.billsType = BillsType.DealerVendor;
                    }
                    break;
            }

            if (this.searchFormObj.partA_name) {
                params.partA_name = this.searchFormObj.partA_name;
            }

            if (this.searchFormObj.order_sn) {
                params.order_sn = this.searchFormObj.order_sn;
            }

            await orderSaleListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        if (tabName === this.systemTypes[0]) {
                            this.saleDelarList = rows.map((item, index) => {
                                let itemObj = {
                                    ...item,
                                    index,
                                    key: item.partA + '' + item.partB + item.po_so_inner_sn + item.billsType,
                                    // key: Math.random(),
                                    activeClass: '',
                                    status: parseFloat(item.status)
                                };
                                if (itemObj.key === poSoInnerSn) {
                                    this.currentSaleDelarTrObj = itemObj;
                                }
                                return itemObj;
                            });

                            if (this.saleDelarList.length <= 0) {
                                this.currentSaleDelarTrObj = {};
                            } else {
                                if (typeof flag === 'number') {
                                    this.saleDelarList.forEach((item) => {
                                        if (item.index === flag) {
                                            this.currentSaleDelarTrObj = item;
                                        }
                                    });
                                } else if (!flag) {
                                    this.currentSaleDelarTrObj = this.saleDelarList[0];
                                }
                            }

                            this.saleDelarPagination = {
                                ...this.saleDelarPagination,
                                total,
                                loading: false
                            };
                        } else if (tabName === this.systemTypes[1]) {
                            this.saleVendorList = rows.map((item, index) => {
                                let itemObj = {
                                    ...item,
                                    index,
                                    key: item.partA + '' + item.partB + item.po_so_inner_sn + item.billsType,
                                    // key: Math.random(),
                                    activeClass: '',
                                    status: parseFloat(item.status)
                                };
                                if (itemObj.key === poSoInnerSn) {
                                    this.currentSaleVendorTrObj = itemObj;
                                }
                                return itemObj;
                            });

                            if (this.saleVendorList.length <= 0) {
                                this.currentsaleVendorTrObj = {};
                            } else {
                                if (typeof flag === 'number') {
                                    this.saleVendorList.forEach((item) => {
                                        if (item.index === flag) {
                                            this.currentSaleVendorTrObj = item;
                                        }
                                    });
                                } else if (!flag) {
                                    this.currentSaleVendorTrObj = this.saleVendorList[0];
                                }
                            }

                            this.saleVendorPagination = {
                                ...this.saleVendorPagination,
                                total,
                                loading: false
                            };
                        }
                    } else {
                        this.$message.error(res.msg);
                        if (tabName === this.systemTypes[0]) {
                            this.saleDelarPagination.loading = false;
                        } else if (tabName === this.systemTypes[1]) {
                            this.saleVendorPagination.loading = false;
                        }
                    }
                })
                .catch(() => {
                    if (tabName === this.systemTypes[0]) {
                        this.saleDelarPagination.loading = false;
                    } else if (tabName === this.systemTypes[1]) {
                        this.saleVendorPagination.loading = false;
                    }
                });
        },
        // 【生成】销售单
        async orderSaleBuildAPIFn() {
            let list = this.createServiceListFn();
            await orderSaleBuildAPI(list)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        switch (this.myOrderPropObj.pageType) {
                            case 1:
                                switch (this.tabsActiveKey) {
                                    case this.systemTypes[0]:
                                        this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleDelarTrObj.index);
                                        this.orderHistorySaleListAPIFn(true);
                                        this.wrapTableCloseLoadingFn();
                                        this.saleDelarSelectedRowKeys = [];
                                        break;
                                    case this.systemTypes[1]:
                                        this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleVendorTrObj.index);
                                        this.orderHistorySaleListAPIFn(true);
                                        this.wrapTableCloseLoadingFn();
                                        this.saleVendorSelectedRowKeys = [];
                                        break;
                                }
                                break;
                        }
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                });
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
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleDelarTrObj.index);
                                this.wrapTableCloseLoadingFn();
                                this.saleDelarSelectedRowKeys = [];
                                break;
                            case this.systemTypes[1]:
                                this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleVendorTrObj.index);
                                this.wrapTableCloseLoadingFn();
                                this.saleVendorSelectedRowKeys = [];
                                break;
                        }
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                        this.planModal.loadingSave = false;
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                    this.planModal.loadingSave = false;
                });
        },
        // 【确认采购订单】
        async orderPurchaseConfirmAPIFn() {
            let params = {
                partA_id: this.planModalForm.partA,
                partB_id: this.planModalForm.partB,
                so_sn: '',
                po_so_inner_sn: this.planModalForm.po_so_inner_sn,
                billsType: this.billsType
            };
            await orderPurchaseConfirmAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.planModal.loadingSave = false;
                        this.planModal.alert = false;
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                this.orderPurchaseListAPIFn(this.tabsActiveKey, this.currentPurchaseDelarTrObj.index);
                                this.wrapTableCloseLoadingFn();
                                this.purchaseDelarSelectedRowKeys = [];
                                break;
                            case this.systemTypes[1]:
                                this.orderPurchaseListAPIFn(this.tabsActiveKey, this.currentPurchaseVendorTrObj.index);
                                this.wrapTableCloseLoadingFn();
                                this.purchaseVendorSelectedRowKeys = [];
                                break;
                        }
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                        this.planModal.loadingSave = false;
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                    this.planModal.loadingSave = false;
                });
        },
        // 【确认订单】
        async orderConfirmAPIFn() {
            let list = this.createServiceListFn();
            await orderConfirmAPI(list)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        switch (this.myOrderPropObj.pageType) {
                            case 0:
                                switch (this.tabsActiveKey) {
                                    case this.systemTypes[0]: {
                                        // this.orderPurchaseListAPIFn(this.tabsActiveKey, this.currentPurchaseDelarTrObj.index)
                                        const xPurchaseDelarTable = this.$refs.xPurchaseDelarTable,
                                            curObjDelar = xPurchaseDelarTable.getCurrentRow(),
                                            infoDelar = {
                                                ...curObjDelar,
                                                status: 3
                                            };
                                        for (let i = 0; i < this.purchaseDelarList.length; i++) {
                                            const item = this.purchaseDelarList[i];
                                            if (curObjDelar._XID === item._XID) {
                                                this.purchaseDelarList.splice(i, 1, infoDelar);
                                                xPurchaseDelarTable.refreshData();
                                                xPurchaseDelarTable.loadData(this.purchaseDelarList);
                                                xPurchaseDelarTable.setCurrentRow(infoDelar);
                                                xPurchaseDelarTable.clearSelection();
                                                break;
                                            }
                                        }
                                        this.wrapTableCloseLoadingFn();
                                        this.purchaseDelarSelectedRowKeys = [];
                                        break;
                                    }
                                    case this.systemTypes[1]: {
                                        const xPurchaseVendorTable = this.$refs.xPurchaseVendorTable,
                                            curObjVendor = xPurchaseVendorTable.getCurrentRow(),
                                            infoVendor = {
                                                ...curObjVendor,
                                                status: 3
                                            };
                                        for (let i = 0; i < this.purchaseVendorList.length; i++) {
                                            const item = this.purchaseVendorList[i];
                                            if (curObjVendor._XID === item._XID) {
                                                this.purchaseVendorList.splice(i, 1, infoVendor);
                                                xPurchaseVendorTable.refreshData();
                                                xPurchaseVendorTable.loadData(this.purchaseVendorList);
                                                xPurchaseVendorTable.setCurrentRow(infoVendor);
                                                xPurchaseVendorTable.clearSelection();
                                                break;
                                            }
                                        }
                                        // this.orderPurchaseListAPIFn(this.tabsActiveKey, this.currentPurchaseVendorTrObj.index)
                                        this.wrapTableCloseLoadingFn();
                                        this.purchaseVendorSelectedRowKeys = [];
                                        break;
                                    }
                                }
                                break;
                            case 1:
                                switch (this.tabsActiveKey) {
                                    case this.systemTypes[0]:
                                        this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleDelarTrObj.index);
                                        this.wrapTableCloseLoadingFn();
                                        this.saleDelarSelectedRowKeys = [];
                                        break;
                                    case this.systemTypes[1]:
                                        this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleVendorTrObj.index);
                                        this.wrapTableCloseLoadingFn();
                                        this.saleVendorSelectedRowKeys = [];
                                        break;
                                }
                                break;
                        }
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 【勾选计划明细】生成采购订单
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
                        switch (this.myOrderPropObj.pageType) {
                            case 1:
                                switch (this.tabsActiveKey) {
                                    case this.systemType[0]:
                                        this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleDelarTrObj.index);
                                        this.wrapTableCloseLoadingFn();
                                        break;
                                    case this.systemType[1]:
                                        this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleVendorTrObj.index);
                                        this.wrapTableCloseLoadingFn();
                                        break;
                                }
                                break;
                        }
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
        // 【终止订单】
        async orderStopAPIFn() {
            let list = this.createServiceListFn();
            await orderStopAPI(list)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        switch (this.myOrderPropObj.pageType) {
                            case 0:
                                switch (this.tabsActiveKey) {
                                    case this.systemTypes[0]:
                                    case this.systemTypes[1]:
                                        if (
                                            this.purchaseHistoryList.length >= this.purchaseHistoryPagination.pageSize
                                        ) {
                                            let pageNum =
                                                Math.floor(
                                                    this.purchaseHistoryPagination.total /
                                                        this.purchaseHistoryPagination.pageSize
                                                ) + 1;
                                            this.purchaseHistoryPagination.current = pageNum;
                                        }
                                        this.orderPurchaseListAPIFn(this.tabsActiveKey);
                                        this.orderHistoryPurchaseListAPIFn(true);
                                        this.wrapTableCloseLoadingFn();
                                        this.tabsActiveKey = this.systemTypes[2];
                                        this.purchaseVendorSelectedRowKeys = [];
                                        this.purchaseDelarSelectedRowKeys = [];
                                        break;
                                }
                                break;
                            case 1:
                                switch (this.tabsActiveKey) {
                                    case this.systemTypes[0]:
                                    case this.systemTypes[1]:
                                        if (this.saleHistoryList.length >= this.saleHistoryPagination.pageSize) {
                                            let pageNum =
                                                Math.floor(
                                                    this.saleHistoryPagination.total /
                                                        this.saleHistoryPagination.pageSize
                                                ) + 1;
                                            this.saleHistoryPagination.current = pageNum;
                                        }
                                        this.orderSaleListAPIFn(this.tabsActiveKey);
                                        this.orderHistorySaleListAPIFn(true);
                                        this.wrapTableCloseLoadingFn();
                                        this.tabsActiveKey = this.systemTypes[2];
                                        this.saleDelarSelectedRowKeys = [];
                                        this.saleVendorSelectedRowKeys = [];
                                        break;
                                }
                                break;
                        }
                    } else {
                        this.$message.error(res.msg);
                        this.wrapTableCloseLoadingFn();
                    }
                })
                .catch(() => {
                    this.wrapTableCloseLoadingFn();
                });
        },
        // 【添加采购订单】
        async orderPurchaseAddAPIFn(status) {
            if ((!this.planModalWarehouseSn && this.planModalWarehouseSn !== 0) || !this.planModalWarehouse) {
                this.$message.error('请选择仓库');
                this.planModal.loadingSave = false;
                this.planModal.loadingSaveDraft = false;
                throw new Error('warehouse_inner_sn');
            }
            let params = {
                buyer_id: this.planModalForm.buyer_id,
                department_id: this.planModalForm.department_inner_sn,
                description: this.planModalForm.description,
                expect_aog_date: this.planModalForm.expect_aog_date,
                partA_id: this.cookieInfo.get('userbelong'),
                partB_id: this.planModalForm.partB,
                warehouse_inner_sn: this.planModalWarehouseSn, // 收货仓库ID
                warehouse_name: this.planModalWarehouse, // 收货仓库名称
                delivery_address: this.planModalLocation,
                po_sn: '',
                billsType: this.billsType,
                status
            };
            params.details = [];
            this.detailList.forEach((item) => {
                item.rate = this.reNumber(item.rate / 100); // 税率和折扣保存时除以100
                params.details.push(item);
            });

            await orderPurchaseAddAPI(params)
                .then((res) => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }

                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.planModalForm = {
                            ...this.planModalForm,
                            ...info
                        };
                        // const key = info.partA + '' + info.partB + info.po_so_inner_sn + info.billsType
                        switch (info.billsType) {
                            case 'HD':
                            case 'DD':
                                this.tabsActiveKey = this.systemTypes[0];
                                this.purchaseDelarPagination.total++;
                                this.$nextTick(() => {
                                    const xPurchaseDelarTable = this.$refs.xPurchaseDelarTable,
                                        recordDelar = xPurchaseDelarTable.getCurrentRow();
                                    xPurchaseDelarTable.insertAt(info, recordDelar).then(({ row }) => {
                                        this.purchaseDelarList = xPurchaseDelarTable.getTableData().tableData;
                                        xPurchaseDelarTable.setCurrentRow(row);
                                        this.currentPurchaseDelarTrObj = row;
                                        if (this.purchaseDelarList.length > this.purchaseDelarPagination.pageSize) {
                                            this.purchaseDelarList.splice(this.purchaseDelarList.length - 1, 1);
                                        }
                                    });
                                });
                                break;
                            case 'HV':
                            case 'DV':
                                this.tabsActiveKey = this.systemTypes[1];
                                this.purchaseVendorPagination.total++;
                                this.$nextTick(() => {
                                    const xPurchaseVendorTable = this.$refs.xPurchaseVendorTable,
                                        recordVendor = xPurchaseVendorTable.getCurrentRow();
                                    xPurchaseVendorTable.insertAt(info, recordVendor).then(({ row }) => {
                                        this.purchaseVendorList = xPurchaseVendorTable.getTableData().tableData;
                                        xPurchaseVendorTable.setCurrentRow(row);
                                        this.currentPurchaseVendorTrObj = row;
                                        if (this.purchaseVendorList.length > this.purchaseVendorPagination.pageSize) {
                                            this.purchaseVendorList.splice(this.purchaseVendorList.length - 1, 1);
                                        }
                                    });
                                });
                                break;
                        }

                        const { details } = info;
                        if (details && details.length) {
                            this.$refs.xTablePlanModalInfo.loadData(details);
                        }
                        this.printOfficalPurchaseOrderFn(status);
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
        // 【添加销售订单】
        async orderSaleAddAPIFn(status) {
            let params = {
                description: this.planModalForm.description,
                details: this.detailList,
                expect_aog_date: this.planModalForm.expect_aog_date,
                partA_id: this.planModalForm.partA,
                partB_id: this.cookieInfo.get('userbelong'),
                po_sn: '',
                billsType: this.billsType,
                status
            };

            await orderSaleAddAPI(params)
                .then((res) => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }

                    if (parseFloat(res.code) === 0) {
                        const { info } = res,
                            key = info.partA + '' + info.partB + info.po_so_inner_sn + info.billsType;
                        switch (parseFloat(this.billsType)) {
                            case 0:
                            case 2:
                                if (this.saleDelarList.length >= this.saleDelarPagination.pageSize) {
                                    let pageNum =
                                        Math.floor(this.saleDelarPagination.total / this.saleDelarPagination.pageSize) +
                                        1;
                                    this.saleDelarPagination.current = pageNum;
                                }
                                this.orderSaleListAPIFn('perform', true, key);
                                this.tabsActiveKey = this.systemTypes[0];
                                break;
                            case 1:
                            case 3:
                                if (this.saleVendorList.length >= this.saleVendorPagination.pageSize) {
                                    let pageNum =
                                        Math.floor(
                                            this.saleVendorPagination.total / this.saleVendorPagination.pageSize
                                        ) + 1;
                                    this.saleVendorPagination.current = pageNum;
                                }
                                this.orderSaleListAPIFn('past', true, key);
                                this.tabsActiveKey = this.systemTypes[1];
                                break;
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
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }
                });
        },
        // 【终止订单明细】
        async orderDetailStopAPIFn(record) {
            let params = {
                partA_id: record.partA,
                partB_id: record.partB,
                po_so_inner_sn: record.po_so_inner_sn,
                detail_inner_sn: record.detail_inner_sn,
                product_type: record.product_type,
                billsType: this.planModalForm.billsType
            };

            await orderDetailStopAPI(params)
                .then((res) => {
                    record.loading = false;
                    if (parseFloat(res.code) === 0) {
                        record.switch = !record.switch;
                        record.status = 3;
                        this.$refs.xTablePlanModalInfo.updateData();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    record.loading = false;
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
                        // this.closePlanModalFn()
                        // this.resetPlanModalFn()
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    record.loading = false;
                });
        },
        // 【修改】采购订单信息
        async orderPurchaseEditAPIFn(status) {
            if ((!this.planModalWarehouseSn && this.planModalWarehouseSn !== 0) || !this.planModalWarehouse) {
                this.$message.error('请选择仓库');
                this.planModal.loadingSave = false;
                this.planModal.loadingSaveDraft = false;
                throw new Error('warehouse_inner_sn');
            }
            let params = {
                status,
                billsType: this.planModalForm.billsType,
                partA_id: this.planModalForm.partA,
                partB_id: this.planModalForm.partB,
                po_so_inner_sn: this.planModalForm.po_so_inner_sn,
                warehouse_inner_sn: this.planModalWarehouseSn, // 收货仓库ID
                warehouse_name: this.planModalWarehouse, // 收货仓库名称
                delivery_address: this.planModalLocation
            };

            params.details = [];
            this.detailList.forEach((item) => {
                item.rate = this.reNumber(item.rate / 100); // 税率保存时除以100
                params.details.push(item);
            });

            if (this.planModalForm.buyer_id !== this.planModalForm.buyer_id_old) {
                params.buyer_id = this.planModalForm.buyer_id;
                params.buyer_id_old = this.planModalForm.buyer_id_old;
                params.buyer_name = this.planModalForm.buyer_name;
            }

            if (this.planModalForm.department_inner_sn !== this.planModalForm.department_inner_sn_old) {
                params.department_inner_sn = this.planModalForm.department_inner_sn;
                params.department_inner_sn_old = this.planModalForm.department_inner_sn_old;
                params.department_name = this.planModalForm.department_name;
            }

            if (this.planModalForm.description !== this.planModalForm.description_old) {
                params.description = this.planModalForm.description;
                params.description_old = this.planModalForm.description_old;
            }

            if (this.planModalForm.expect_aog_date !== this.planModalForm.expect_aog_date_old) {
                params.expect_aog_date = this.planModalForm.expect_aog_date;
                params.expect_aog_date_old = this.planModalForm.expect_aog_date_old;
            }

            // if (this.planModalForm.po_sn !== this.planModalForm.po_sn_old) {
            //     params.po_sn = this.planModalForm.po_sn
            //     params.po_sn_old = this.planModalForm.po_sn_old
            // }

            await orderPurchaseEditAPI(params)
                .then((res) => {
                    this.planModal.loadingAudit = false;
                    const { code, info } = res;
                    this.planModalForm = {
                        ...this.planModalForm,
                        ...info
                    };
                    if (parseFloat(code) === 0) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]: {
                                const xPurchaseDelarTable = this.$refs.xPurchaseDelarTable,
                                    curObjDelar = xPurchaseDelarTable.getCurrentRow(),
                                    infoDelar = {
                                        ...curObjDelar,
                                        ...info,
                                        status,
                                        department_inner_sn: this.planModalForm.department_inner_sn,
                                        department_name: this.planModalForm.department_name,
                                        auditor_inner_sn: this.planModalForm.auditor_inner_sn,
                                        auditor_name: this.planModalForm.auditor_name,
                                        creator_inner_sn: this.planModalForm.creator_inner_sn,
                                        creator_name: this.planModalForm.creator_name,
                                        buyer_name: this.planModalForm.buyer_name,
                                        buyer_id: this.planModalForm.buyer_id,
                                        // create_time: status === 1 ? moment(Date()).format(this.format) : curObjDelar.create_time, // 草稿 -> 正式状态 显示单据生成时间
                                        expect_aog_date: this.planModalForm.expect_aog_date
                                    };
                                for (let i = 0; i < this.purchaseDelarList.length; i++) {
                                    const item = this.purchaseDelarList[i];
                                    if (curObjDelar._XID === item._XID) {
                                        this.purchaseDelarList.splice(i, 1, infoDelar);
                                        xPurchaseDelarTable.refreshData();
                                        xPurchaseDelarTable.loadData(this.purchaseDelarList);
                                        xPurchaseDelarTable.setCurrentRow(infoDelar);
                                        break;
                                    }
                                }
                                // this.orderPurchaseListAPIFn(this.tabsActiveKey, this.currentPurchaseDelarTrObj.index)
                                break;
                            }
                            case this.systemTypes[1]: {
                                const xPurchaseVendorTable = this.$refs.xPurchaseVendorTable,
                                    curObjVendor = xPurchaseVendorTable.getCurrentRow(),
                                    infoVendor = {
                                        ...curObjVendor,
                                        status,
                                        department_inner_sn: this.planModalForm.department_inner_sn,
                                        department_name: this.planModalForm.department_name,
                                        auditor_inner_sn: this.planModalForm.auditor_inner_sn,
                                        auditor_name: this.planModalForm.auditor_name,
                                        creator_inner_sn: this.planModalForm.creator_inner_sn,
                                        creator_name: this.planModalForm.creator_name,
                                        buyer_name: this.planModalForm.buyer_name,
                                        buyer_id: this.planModalForm.buyer_id,
                                        create_time:
                                            status === 1
                                                ? moment(Date()).format(this.format)
                                                : curObjVendor.create_time, // 草稿 -> 正式状态 显示单据生成时间
                                        expect_aog_date: this.planModalForm.expect_aog_date
                                    };
                                for (let i = 0; i < this.purchaseVendorList.length; i++) {
                                    const item = this.purchaseVendorList[i];
                                    if (curObjVendor._XID === item._XID) {
                                        this.purchaseVendorList.splice(i, 1, infoVendor);
                                        xPurchaseVendorTable.refreshData();
                                        xPurchaseVendorTable.loadData(this.purchaseVendorList);
                                        xPurchaseVendorTable.setCurrentRow(infoVendor);
                                        break;
                                    }
                                }
                                // this.orderPurchaseListAPIFn(this.tabsActiveKey, this.currentPurchaseVendorTrObj.index)
                                break;
                            }
                        }
                        this.printOfficalPurchaseOrderFn(status);
                        this.closePlanModalFn();
                        this.resetPlanModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.planModal.loadingAudit = false;
                });
        },
        /// 保存编辑 —— [正式状态] 打印
        printOfficalPurchaseOrderFn(status) {
            if (parseInt(status) === 1) {
                this.handlePrint();
            }
        },
        // 【修改】销售订单信息
        async orderSaleEditAPIFn(status) {
            let params = {
                details: this.detailList,
                status,
                billsType: this.planModalForm.billsType,
                partA_id: this.planModalForm.partA,
                partB_id: this.planModalForm.partB,
                po_so_inner_sn: this.planModalForm.po_so_inner_sn
            };

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
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }

                    if (parseFloat(res.code) === 0) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleDelarTrObj.index);
                                break;
                            case this.systemTypes[1]:
                                this.orderSaleListAPIFn(this.tabsActiveKey, this.currentSaleVendorTrObj.index);
                                break;
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
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }
                });
        },
        // 【删除】订单
        async orderDelAPIFn(checkRecordRows) {
            const params = checkRecordRows.map((item) => {
                return {
                    'billsType': item.billsType,
                    'partA_id': item.partA,
                    'partB_id': item.partB,
                    'po_so_inner_sn': item.po_so_inner_sn
                };
            });

            // switch (this.myOrderPropObj.pageType) {
            // case 0:
            //     switch (this.tabsActiveKey) {
            //     case this.systemTypes[0]:
            //         this.purchaseDelarPagination.loading = true
            //         params = {
            //             partA_id: this.currentPurchaseDelarTrObj.partA,
            //             partB_id: this.currentPurchaseDelarTrObj.partB,
            //             po_so_inner_sn: this.currentPurchaseDelarTrObj.po_so_inner_sn,
            //             billsType: this.currentPurchaseDelarTrObj.billsType
            //         }
            //         break
            //     case this.systemTypes[1]:
            //         this.purchaseVendorPagination.loading = true
            //         params = {
            //             partA_id: this.currentPurchaseVendorTrObj.partA,
            //             partB_id: this.currentPurchaseVendorTrObj.partB,
            //             po_so_inner_sn: this.currentPurchaseVendorTrObj.po_so_inner_sn,
            //             billsType: this.currentPurchaseVendorTrObj.billsType
            //         }
            //         break
            //     case this.systemTypes[2]:
            //         this.purchaseHistoryPagination.loading = true
            //         params = {
            //             partA_id: this.currentPurchaseHistoryTrObj.partA,
            //             partB_id: this.currentPurchaseHistoryTrObj.partB,
            //             po_so_inner_sn: this.currentPurchaseHistoryTrObj.po_so_inner_sn,
            //             billsType: this.currentPurchaseHistoryTrObj.billsType
            //         }
            //         break
            //     }
            //     break
            // case 1:
            //     switch (this.tabsActiveKey) {
            //     case this.systemTypes[0]:
            //         this.saleDelarPagination.loading = true
            //         params = {
            //             partA_id: this.currentSaleDelarTrObj.partA,
            //             partB_id: this.currentSaleDelarTrObj.partB,
            //             po_so_inner_sn: this.currentSaleDelarTrObj.po_so_inner_sn,
            //             billsType: this.currentSaleDelarTrObj.billsType
            //         }
            //         break
            //     case this.systemTypes[1]:
            //         this.saleVendorPagination.loading = true
            //         params = {
            //             partA_id: this.currentSaleVendorTrObj.partA,
            //             partB_id: this.currentSaleVendorTrObj.partB,
            //             po_so_inner_sn: this.currentSaleVendorTrObj.po_so_inner_sn,
            //             billsType: this.currentSaleVendorTrObj.billsType
            //         }
            //         break
            //     case this.systemTypes[2]:
            //         this.saleHistoryPagination.loading = true
            //         params = {
            //             partA_id: this.currentSaleHistoryTrObj.partA,
            //             partB_id: this.currentSaleHistoryTrObj.partB,
            //             po_so_inner_sn: this.currentSaleHistoryTrObj.po_so_inner_sn,
            //             billsType: this.currentSaleHistoryTrObj.billsType
            //         }
            //         break
            //     }
            //     break
            // }

            await orderDelAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    switch (this.myOrderPropObj.pageType) {
                        case 0:
                            switch (this.tabsActiveKey) {
                                case this.systemTypes[0]:
                                    this.orderPurchaseListAPIFn(this.tabsActiveKey);
                                    break;
                                case this.systemTypes[1]:
                                    this.orderPurchaseListAPIFn(this.tabsActiveKey);
                                    break;
                                case this.systemTypes[2]:
                                    this.orderHistoryPurchaseListAPIFn();
                                    break;
                            }
                            break;
                        case 1:
                            switch (this.tabsActiveKey) {
                                case this.systemTypes[0]:
                                    this.orderSaleListAPIFn(this.tabsActiveKey);
                                    break;
                                case this.systemTypes[1]:
                                    this.orderSaleListAPIFn(this.tabsActiveKey);
                                    break;
                                case this.systemTypes[2]:
                                    this.orderHistorySaleListAPIFn();
                                    break;
                            }
                            break;
                    }
                } else {
                    this.$message.error(res.msg);
                    this.wrapTableCloseLoadingFn();
                }
            });
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
                const n = data.toFixed(4);
                data = parseFloat(n);
            }
            return data;
        },
        // 查看【采购订单信息】
        async orderPurchaseInfoAPIFn(infoObj) {
            this.planModalPagination.loading = true;
            this.planModalDatas = [];
            const params = { ...infoObj };
            await orderPurchaseInfoAPI({
                params
            })
                .then((res) => {
                    this.planModalPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.planModalForm = {
                            ...info,
                            department_inner_sn: info.department_inner_sn
                                ? info.department_inner_sn
                                : JSON.parse(localStorage.getItem('userInfo')).Departments[0].Department_inner_sn,
                            department_name: info.department_name
                                ? info.department_name
                                : JSON.parse(localStorage.getItem('userInfo')).Departments[0].Name,
                            buyer_id: info.buyer_inner_sn
                                ? info.buyer_inner_sn
                                : JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn,
                            buyer_name: info.buyer_name
                                ? info.buyer_name
                                : JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
                            buyer_id_old: info.buyer_inner_sn,
                            department_inner_sn_old: info.department_inner_sn,
                            description_old: info.description,
                            expect_aog_date_old: info.expect_aog_date,
                            po_sn_old: info.po_sn,
                            status: parseFloat(info.status)
                        };
                        const currentWarehouse = this.warehouseList.find(
                            (item) => item.warehouse_model_inner_sn === info.warehouse_inner_sn
                        );
                        if (currentWarehouse) {
                            this.planModalForm.warehouse_name = currentWarehouse.name;
                            this.planModalWarehouse = currentWarehouse.name;
                        }
                        this.expectAogDate = info.expect_aog_date ? moment(info.expect_aog_date, 'YYYY-MM-DD') : null;
                        this.editInfoFormDepartmentInnerSn = this.planModalForm.department_inner_sn;
                        this.planModalOrgSn = info.partB + '-' + info.billsType;
                        this.planModalEmployeSn = info.buyer_inner_sn
                            ? info.buyer_inner_sn
                            : JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn;
                        this.planModalDepartmentSn = info.department_inner_sn
                            ? info.department_inner_sn
                            : JSON.parse(localStorage.getItem('userInfo')).Departments[0].Department_inner_sn;
                        this.planModalLocation = info.delivery_address;
                        // 检索当前账户仓库列表，若单据中的仓库不在列表中，则将其置空
                        if (this.warehouseList.some((warehouse) => warehouse.key === info.warehouse_inner_sn)) {
                            this.planModalWarehouseSn = info.warehouse_inner_sn;
                        } else {
                            this.planModalWarehouseSn = undefined;
                        }
                        this.partB.partB_id = info.partB;
                        this.billsType = info.billsType;
                        this.partB.billsType = info.billsType;
                        this.partB.oname = info.partB_name || '';
                        // 检测采购单位是否在采购单位列表可以碰撞到值
                        // const orgSnFlag = this.planModalOrgs.some(item => item.value === this.planModalOrgSn)
                        // if (!orgSnFlag) {
                        //     this.planModalOrgs.push({
                        //         label: info.partB_name || '',
                        //         name: info.partB_name || '',
                        //         title: info.partB_name || '',
                        //         value: this.planModalOrgSn,
                        //         billsType: info.billsType,
                        //         org_id: info.partB
                        //     })
                        // }
                        if (info.details && info.details.length) {
                            this.planModalDatas = info.details.map((item, index) => {
                                // ******** 注意 ********
                                // item.unit_price 来自后端数据，代表含税单价，含税单价在前端对应tax_unit_price字段
                                const { quantity, unit_price: unitPrice, rate } = item;
                                const { multiply, divide, add, round } = this.$XEUtils;
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
                                    unitPriceList: [],
                                    rate: this.reNumber(rate * 100), // 税率显示时乘以100
                                    unit_price: divide(unitPrice, add(rate, 1)), // 不含税单价
                                    tax_unit_price: unitPrice, // 含税单价
                                    tax_amount: round(multiply(quantity, unitPrice), 4), // 含税金额
                                    focusDatas: [
                                        false,
                                        index === 0 && parseFloat(info.status) <= 1,
                                        false,
                                        false,
                                        false,
                                        false
                                    ]
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
                        this.$nextTick(() => {
                            const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
                            if (xTablePlanModalInfo) {
                                xTablePlanModalInfo.updateData();
                                this.clickDescriptionFn();
                                xTablePlanModalInfo.focus();
                                xTablePlanModalInfo.refreshScroll();
                                xTablePlanModalInfo.clearCurrentRow();
                                if (this.planModalDatas && this.planModalDatas.length) {
                                    xTablePlanModalInfo.setCurrentRow(this.planModalDatas[0]);
                                    this.currentTrPlanModalObj = this.planModalDatas[0];
                                }
                            }
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.planModalPagination.loading = false;
                });
        },
        resetFormFocusFn() {
            this.relevantInstitutionsFocus = false;
            this.isParentKeyDownEnter = false;
        },
        clickDescriptionFn() {
            this.resetFocusFn();
            this.resetFormFocusFn();
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
                        this.planModalOrgSn = [info.partA + '-' + info.billsType];
                        this.billsType = info.billsType;
                        this.partB.partA_id = info.partA;
                        this.billsType = info.billsType;
                        this.partB.billsType = info.billsType;
                        this.partB.oname = info.partA_name || '';

                        if (info.details && info.details.length) {
                            // info.details.push({
                            //     key: Math.random(),
                            //     activeClass: ''
                            // })
                            this.planModalDatas = info.details.map((item, index) => {
                                let items = {};
                                if (index === info.details.length - 1) {
                                    items = { ...item };
                                } else {
                                    items = {
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
                                            (item.status === 0 || item.status === 2)
                                    };
                                }
                                return items;
                            });
                        }
                        this.currentTrPlanModalObj = this.planModalDatas.length ? this.planModalDatas[0] : {};
                        !this.isEditDisabled && this.dblclickAddProductFn();
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
                organization_inner_sn: this.planModalForm.partB,
                product_inner_sn: record.product_inner_sn,
                product_type: record.product_type,
                product_vendor_inner_sn:
                    record.product_vendor_inner_sn || record.vendor_inner_sn || record.dealer_inner_sn,
                specification_inner_sn: record.specification_inner_sn,
                unit_inner_sn: typeof tempUnitInnerSn === 'undefined' ? 0 : tempUnitInnerSn
            };
            await contractGetOrderPriceAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    record.unitPriceList = list || [];
                    if (record.unitPriceList.length && typeof record.detail_inner_sn === 'undefined') {
                        // 动态更新含税单价
                        this.updateProductTaxUnitPrice(list[0], this.planModalDatas.indexOf(record));
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【删除】订/单明细
        async orderDetailDelAPIFn(record) {
            this.planModalPagination.loading = true;
            let params = {
                po_so_inner_sn: record.po_so_inner_sn,
                detail_inner_sn: record.detail_inner_sn,
                product_type: record.product_type,
                billsType: this.planModalForm.billsType
            };

            if (this.myOrderPropObj.pageType === 0) {
                params.partA_id = this.$cookie.get('userbelong');
                params.partB_id = record.partB;
            } else if (this.myOrderPropObj.pageType === 1) {
                params.partA_id = record.partA;
                params.partB_id = this.$cookie.get('userbelong');
            }

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
        // 设置后端所需要的格式
        changeTopSearchAllTreeListFn(value) {
            this.searchFormObj.department_id = value && value.length ? value[0] : '';
        },
        changeTopSearchEmpDropdownFn(value) {
            // this.searchFormObj.buyer_id = value && value.length ? value[0] : ''
            this.searchFormObj.buyer_id = value;
        },
        // 更新顶部搜索条件：单据生成时间
        changeTopSearchCreateTimeRange(momentArray) {
            this.searchFormObj.createTimeRange = momentArray;
        },
        changePlanModalOrgSnFn(value, label) {
            if (this.myOrderPropObj.pageType === 0) {
                if (typeof value === 'string' && value.length) {
                    const arySplitValue = value.split('-');
                    this.planModalForm.partB = arySplitValue[0];
                    this.partB.partB_id = arySplitValue[0];
                    this.partB.billsType = arySplitValue[1];
                    this.billsType = arySplitValue[1];
                } else {
                    this.planModalForm.partB = '';
                    this.partB.billsType = '';
                    this.billsType = '';
                }
            } else if (this.myOrderPropObj.pageType === 1) {
                if (typeof value === 'string' && value.length) {
                    const arySplitValue = value.split('-');
                    this.planModalForm.partA = arySplitValue[0];
                    this.partB.partA_id = arySplitValue[0];
                    this.billsType = arySplitValue[1];
                    this.partB.billsType = arySplitValue[1];
                } else {
                    this.planModalForm.partA = '';
                    this.partB.billsType = '';
                    this.billsType = '';
                }
            }
            this.partB.oname = label.length ? label : '';
            this.planModalForm.billsType = this.partB.billsType;
            this.planModalOrgSn = value;
            if (this.btnName === 'add') {
                this.planModalDatas = [];
                this.$refs.xTablePlanModalInfo && this.$refs.xTablePlanModalInfo.remove();
            }
        },
        changePlanModalEmployeSnFn(value, label) {
            this.planModalEmployeSn = value;
            this.planModalForm.buyer_id = value || '';
            this.planModalForm.buyer_name = Array.isArray(label) ? label[0] : label;
            this.$refs.xTablePlanModalInfo.focus();
        },
        changePlanModalDepartmentSnFn(value, label) {
            this.planModalDepartmentSn = value;
            this.planModalForm.department_inner_sn = value || '';
            this.planModalForm.department_name = Array.isArray(label) ? label[0] : label;
            this.$refs.xTablePlanModalInfo.focus();
        },
        // value = warehouse_model_inner_sn
        changewareHouseFn(value, label) {
            this.planModalForm.warehouse_name = label[0];
            this.planModalForm.warehouse_inner_sn = value;
            this.planModalWarehouse = label[0];
            this.planModalWarehouseSn = value;
            this.$refs.xTablePlanModalInfo.focus();
        },
        changeDeliveryAddressFn(e) {
            let { value } = e.target;
            this.planModalForm.delivery_address = value;
        },
        // 顶部搜索
        topSearchFn() {
            if (!this.checkSearchInfoFormMsgFn()) return;
            this.refreshPageFn();
        },
        checkSearchInfoFormMsgFn() {
            if (this.myOrderPropObj.pageType === 0 && this.$Utils.checkInputValFn(this.searchFormObj.partB_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.placeholderComputed}${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (this.myOrderPropObj.pageType === 1 && this.$Utils.checkInputValFn(this.searchFormObj.partA_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.placeholderComputed}${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (this.hightSearchObj.isArrow && this.$Utils.checkInputValFn(this.searchFormObj.order_sn)) {
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
        // 顶部按钮的事件
        groupBtnFn(btnName) {
            this.btnName = btnName;
            this.resetPlanModalFn();
            if (this.btnName === 'add' || this.btnName === 'edit') {
                // this.orderOrgListAPIFn(this.cookieInfo.get('userSystemType'), this.cookieInfo.get('userbelong'))
                this.myOrderPropObj.pageType === 0 && this.orgDeptTopTreeAPIFn();
                this.myOrderPropObj.pageType === 0 && this.orgEmpDropdownListAPIFn();
                this.myOrderPropObj.pageType === 0 && this.warehouseUsableTopListAPIFn();
                if (this.$refs.xTablePlanModalInfo) this.$refs.xTablePlanModalInfo.updateData();
            }
            if (!this.checkSecltedTrFn()) return;
            switch (btnName) {
                case 'add': // 新增
                    this.planModal = {
                        title: `${this.$route.meta.title} - 新增`,
                        alert: true,
                        loading: false
                    };
                    this.relevantInstitutionsFocus = true;
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
                case 'remove': {
                    // 删除
                    let checkRecordRows = [];
                    if (this.tabsActiveKey === this.systemTypes[0]) {
                        checkRecordRows = this.$refs.xPurchaseDelarTable.getSelectRecords();
                    } else if (this.tabsActiveKey === this.systemTypes[1]) {
                        checkRecordRows = this.$refs.xPurchaseVendorTable.getSelectRecords();
                    }
                    if (checkRecordRows.length <= 0) {
                        this.$warning({
                            title: '操作提示',
                            content: this.$Utils.tips().noCheckbox,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        return;
                    }
                    if (!this.removeSelfCreateOrganizationTypeFn(checkRecordRows)) {
                        this.$warning({
                            title: '操作提示',
                            content: '只能删除自建的产品',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        return;
                    }
                    if (!checkRecordRows.every((item) => parseInt(item.status) === 0 || parseInt(item.status) === 1)) {
                        this.$warning({
                            title: '操作提示',
                            content: '只能删除【草稿和正式】状态的订单。',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        return;
                    }
                    this.orderDelAPIFn(checkRecordRows);
                    break;
                }
                case 'submit': // 确认
                    if (this.myOrderPropObj.pageType === 0) {
                        if (this.tabsActiveKey === this.systemTypes[0]) {
                            const checkboxRecords = this.$refs.xPurchaseDelarTable.getSelectRecords();
                            this.checkCreateOrganizationTypeListFn(checkboxRecords) && this.orderConfirmAPIFn();
                        } else if (this.tabsActiveKey === this.systemTypes[1]) {
                            const checkboxRecords = this.$refs.xPurchaseVendorTable.getSelectRecords();
                            this.checkCreateOrganizationTypeListFn(checkboxRecords) && this.orderConfirmAPIFn();
                        }
                    } else {
                        this.orderConfirmAPIFn();
                    }
                    break;
                case 'build': // 生成
                    this.orderSaleBuildAPIFn();
                    break;
                case 'stop': // 终止
                    this.checkOrderStopFn();
                    break;
            }
        },
        checkOrderStopFn() {
            const orderSelectRecods = this.$refs.xPurchaseDelarTable.getSelectRecords();
            if (!orderSelectRecods.every((item) => parseInt(item.status) === 2 || parseInt(item.status) === 3)) {
                this.$warning({
                    title: '操作提示',
                    content: '只能终止【已审核和已确认】状态的订单。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            this.orderStopAPIFn();
        },
        hightSearchIconFn() {
            if (this.myOrderPropObj.pageType === 0) {
                this.topSearchEmpDropdownList.length <= 0 && this.orgEmpDropdownListAPIFn();
                this.topSearchAllTreeList.length <= 0 && this.orgDeptTopTreeAPIFn();
            }
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        // 开发临时函数
        developmentFn() {
            this.$warning({
                title: '操作提示',
                content: '该功能在紧张开发中，敬请期待',
                okText: '知道了',
                centered: true,
                maskClosable: false
            });
        },
        // 创建后端所需的list格式数据
        createServiceListFn() {
            let list = [];
            switch (this.myOrderPropObj.pageType) {
                case 0:
                    switch (this.tabsActiveKey) {
                        case this.systemTypes[0]:
                            this.purchaseDelarPagination.loading = true;
                            this.purchaseDelarList.forEach((item) => {
                                this.$refs.xPurchaseDelarTable.getSelectRecords().forEach((cItem) => {
                                    if (item.key === cItem.key) {
                                        list.push({
                                            partA_id: item.partA,
                                            partB_id: item.partB,
                                            po_so_inner_sn: item.po_so_inner_sn,
                                            billsType: item.billsType
                                        });
                                    }
                                });
                            });
                            break;
                        case this.systemTypes[1]:
                            this.purchaseVendorPagination.loading = true;
                            this.purchaseVendorList.forEach((item) => {
                                this.$refs.xPurchaseVendorTable.getSelectRecords().forEach((cItem) => {
                                    if (item.key === cItem.key) {
                                        list.push({
                                            partA_id: item.partA,
                                            partB_id: item.partB,
                                            po_so_inner_sn: item.po_so_inner_sn,
                                            billsType: item.billsType
                                        });
                                    }
                                });
                            });
                            break;
                    }
                    break;
                case 1:
                    switch (this.tabsActiveKey) {
                        case this.systemTypes[0]:
                            this.saleDelarPagination.loading = true;
                            this.saleDelarList.forEach((item) => {
                                this.saleDelarSelectedRowKeys.forEach((cItem) => {
                                    if (item.key === cItem) {
                                        list.push({
                                            partA_id: item.partA,
                                            partB_id: item.partB,
                                            po_so_inner_sn: item.po_so_inner_sn,
                                            billsType: item.billsType
                                        });
                                    }
                                });
                            });
                            break;
                        case this.systemTypes[1]:
                            this.saleVendorPagination.loading = true;
                            this.saleVendorList.forEach((item) => {
                                this.saleVendorSelectedRowKeys.forEach((cItem) => {
                                    if (item.key === cItem) {
                                        list.push({
                                            partA_id: item.partA,
                                            partB_id: item.partB,
                                            po_so_inner_sn: item.po_so_inner_sn,
                                            billsType: item.billsType
                                        });
                                    }
                                });
                            });
                            break;
                    }
                    break;
            }
            return list;
        },
        // 外层表格的loading关闭
        wrapTableCloseLoadingFn() {
            switch (this.myOrderPropObj.pageType) {
                case 0:
                    switch (this.tabsActiveKey) {
                        case this.systemTypes[0]:
                            this.purchaseDelarPagination.loading = false;
                            break;
                        case this.systemTypes[1]:
                            this.purchaseVendorPagination.loading = false;
                            break;
                        case this.systemTypes[2]:
                            this.purchaseHistoryPagination.loading = false;
                            break;
                    }
                    break;
                case 1:
                    switch (this.tabsActiveKey) {
                        case this.systemTypes[0]:
                            this.saleDelarPagination.loading = false;
                            break;
                        case this.systemTypes[1]:
                            this.saleVendorPagination.loading = false;
                            break;
                        case this.systemTypes[2]:
                            this.saleHistoryPagination.loading = false;
                            break;
                    }
                    break;
            }
        },
        checkSecltedTrFn() {
            let flag = true;
            switch (this.myOrderPropObj.pageType) {
                case 0:
                    if (
                        this.tabsActiveKey === this.systemTypes[0] &&
                        Object.keys(this.currentPurchaseDelarTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    } else if (
                        this.tabsActiveKey === this.systemTypes[1] &&
                        Object.keys(this.currentPurchaseVendorTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    } else if (
                        this.tabsActiveKey === this.systemTypes[2] &&
                        Object.keys(this.currentPurchaseHistoryTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    }
                    break;
                case 1:
                    if (
                        this.tabsActiveKey === this.systemTypes[0] &&
                        Object.keys(this.currentSaleDelarTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    } else if (
                        this.tabsActiveKey === this.systemTypes[1] &&
                        Object.keys(this.currentSaleVendorTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    } else if (
                        this.tabsActiveKey === this.systemTypes[2] &&
                        Object.keys(this.currentSaleHistoryTrObj).length <= 0
                    ) {
                        if (this.btnName === 'edit' || this.btnName === 'remove') {
                            this.$warning({
                                title: '操作提示',
                                content: this.$Utils.tips().noSelected,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                        }
                    }
                    break;
            }

            if (!flag) return flag;

            const xPurchaseDelarTable = this.$refs.xPurchaseDelarTable,
                xPurchaseVendorTable = this.$refs.xPurchaseVendorTable;
            this.purchaseDelarSelectedRowKeys = xPurchaseDelarTable && xPurchaseDelarTable.getSelectRecords();
            this.purchaseVendorSelectedRowKeys = xPurchaseVendorTable && xPurchaseVendorTable.getSelectRecords();

            const contentNoData = '请勾选表格中的复选框，再进行相关操作。';
            switch (this.btnName) {
                case 'submit': {
                    let contentSubmit = '只能确认【已审核】的状态。',
                        contentMySelf = '不能确认自己创建的订单。';
                    const isOrderPage = this.myOrderPropObj.pageType; // 判断是0-采购订单  1-销售订单
                    if (isOrderPage === 0) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                if (this.purchaseDelarSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.purchaseDelarList.length; i++) {
                                        let item = this.purchaseDelarList[i];
                                        for (let j = 0; j < this.purchaseDelarSelectedRowKeys.length; j++) {
                                            let cItem = this.purchaseDelarSelectedRowKeys[j];
                                            if (flag && parseFloat(item.key) === parseFloat(cItem.key)) {
                                                if (!(item.status === 2)) {
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
                                                if (flag && item.create_organization_type === isOrderPage) {
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
                                    }
                                }
                                break;
                            case this.systemTypes[1]:
                                if (this.purchaseVendorSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.purchaseVendorList.length; i++) {
                                        let item = this.purchaseVendorList[i];
                                        for (let j = 0; j < this.purchaseVendorSelectedRowKeys.length; j++) {
                                            let cItem = this.purchaseVendorSelectedRowKeys[j];
                                            if (flag && parseFloat(item.key) === parseFloat(cItem.key)) {
                                                if (!(item.status === 2)) {
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
                                                if (flag && item.create_organization_type === isOrderPage) {
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
                                    }
                                }
                                break;
                        }
                    } else if (this.myOrderPropObj.pageType === 1) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                if (this.saleDelarSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleDelarList.length; i++) {
                                        let item = this.saleDelarList[i];
                                        for (let j = 0; j < this.saleDelarSelectedRowKeys.length; j++) {
                                            let cItem = this.saleDelarSelectedRowKeys[j];
                                            if (flag && parseFloat(item.key) === parseFloat(cItem)) {
                                                if (!(item.status === 2)) {
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
                                                if (flag && item.create_organization_type === isOrderPage) {
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
                                    }
                                }
                                break;
                            case this.systemTypes[1]:
                                if (this.saleVendorSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleVendorList.length; i++) {
                                        let item = this.saleVendorList[i];
                                        for (let j = 0; j < this.saleVendorSelectedRowKeys.length; j++) {
                                            let cItem = this.saleVendorSelectedRowKeys[j];
                                            if (flag && parseFloat(item.key) === parseFloat(cItem)) {
                                                if (!(item.status === 2)) {
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
                                                if (flag && item.create_organization_type === isOrderPage) {
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
                                    }
                                }
                                break;
                        }
                    }
                    break;
                }
                case 'build': {
                    let contentBuild = '只能生成【已审核/已确认】的销售状态。';
                    if (this.myOrderPropObj.pageType === 1) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                if (this.saleDelarSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleDelarList.length; i++) {
                                        let item = this.saleDelarList[i];
                                        for (let j = 0; j < this.saleDelarSelectedRowKeys.length; j++) {
                                            let cItem = this.saleDelarSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem) &&
                                                !(item.status === 2 || item.status === 3)
                                            ) {
                                                flag = false;
                                                this.$warning({
                                                    title: '操作提示',
                                                    content: contentBuild,
                                                    okText: '知道了',
                                                    centered: true,
                                                    maskClosable: false
                                                });
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            case this.systemTypes[1]:
                                if (this.saleVendorSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleVendorList.length; i++) {
                                        let item = this.saleVendorList[i];
                                        for (let j = 0; j < this.saleVendorSelectedRowKeys.length; j++) {
                                            let cItem = this.saleVendorSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem) &&
                                                !(item.status === 2 || item.status === 3)
                                            ) {
                                                flag = false;
                                                this.$warning({
                                                    title: '操作提示',
                                                    content: contentBuild,
                                                    okText: '知道了',
                                                    centered: true,
                                                    maskClosable: false
                                                });
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }
                    break;
                }
                case 'stop': {
                    let content = '只能终止【已审核和已确认】状态的订单。';
                    if (this.myOrderPropObj.pageType === 0) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                if (this.purchaseDelarSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                    break;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.purchaseDelarList.length; i++) {
                                        let item = this.purchaseDelarList[i];
                                        for (let j = 0; j < this.purchaseDelarSelectedRowKeys.length; j++) {
                                            let cItem = this.purchaseDelarSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem.key) &&
                                                !(item.status === 1 || item.status === 2 || item.status === 3)
                                            ) {
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
                                    }
                                }
                                break;
                            case this.systemTypes[1]:
                                if (this.purchaseVendorSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                    break;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.purchaseVendorList.length; i++) {
                                        let item = this.purchaseVendorList[i];
                                        for (let j = 0; j < this.purchaseVendorSelectedRowKeys.length; j++) {
                                            let cItem = this.purchaseVendorSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem.key) &&
                                                !(item.status === 1 || item.status === 2 || item.status === 3)
                                            ) {
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
                                    }
                                }
                                break;
                        }
                    } else if (this.myOrderPropObj.pageType === 1) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                if (this.saleDelarSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                    break;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleDelarList.length; i++) {
                                        let item = this.saleDelarList[i];
                                        for (let j = 0; j < this.saleDelarSelectedRowKeys.length; j++) {
                                            let cItem = this.saleDelarSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem) &&
                                                !(item.status === 1 || item.status === 2 || item.status === 3)
                                            ) {
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
                                    }
                                }
                                break;
                            case this.systemTypes[1]:
                                if (this.saleVendorSelectedRowKeys.length <= 0) {
                                    this.$warning({
                                        title: '操作提示',
                                        content: contentNoData,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: false
                                    });
                                    flag = false;
                                    break;
                                }
                                if (flag) {
                                    for (let i = 0; i < this.saleVendorList.length; i++) {
                                        let item = this.saleVendorList[i];
                                        for (let j = 0; j < this.saleVendorSelectedRowKeys.length; j++) {
                                            let cItem = this.saleVendorSelectedRowKeys[j];
                                            if (
                                                flag &&
                                                parseFloat(item.key) === parseFloat(cItem) &&
                                                !(item.status === 1 || item.status === 2 || item.status === 3)
                                            ) {
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
                                    }
                                }
                                break;
                        }
                    }
                    break;
                }
                case 'remove':
                    break;
                case 'edit':
                    if (this.myOrderPropObj.pageType === 0) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                this.orderPurchaseInfoAPIFn({
                                    partA_id: this.currentPurchaseDelarTrObj.partA,
                                    partB_id: this.currentPurchaseDelarTrObj.partB,
                                    po_so_inner_sn: this.currentPurchaseDelarTrObj.po_so_inner_sn,
                                    billsType: this.currentPurchaseDelarTrObj.billsType
                                });
                                break;
                            case this.systemTypes[1]:
                                this.orderPurchaseInfoAPIFn({
                                    partA_id: this.currentPurchaseVendorTrObj.partA,
                                    partB_id: this.currentPurchaseVendorTrObj.partB,
                                    po_so_inner_sn: this.currentPurchaseVendorTrObj.po_so_inner_sn,
                                    billsType: this.currentPurchaseVendorTrObj.billsType
                                });
                                break;
                            case this.systemTypes[2]:
                                this.orderPurchaseInfoAPIFn({
                                    partA_id: this.currentPurchaseHistoryTrObj.partA,
                                    partB_id: this.currentPurchaseHistoryTrObj.partB,
                                    po_so_inner_sn: this.currentPurchaseHistoryTrObj.po_so_inner_sn,
                                    billsType: this.currentPurchaseHistoryTrObj.billsType
                                });
                                break;
                        }
                    } else if (this.myOrderPropObj.pageType === 1) {
                        switch (this.tabsActiveKey) {
                            case this.systemTypes[0]:
                                this.orderSaleInfoAPIFn({
                                    partA_id: this.currentSaleDelarTrObj.partA,
                                    partB_id: this.currentSaleDelarTrObj.partB,
                                    po_so_inner_sn: this.currentSaleDelarTrObj.po_so_inner_sn,
                                    billsType: this.currentSaleDelarTrObj.billsType
                                });
                                break;
                            case this.systemTypes[1]:
                                this.orderSaleInfoAPIFn({
                                    partA_id: this.currentSaleVendorTrObj.partA,
                                    partB_id: this.currentSaleVendorTrObj.partB,
                                    po_so_inner_sn: this.currentSaleVendorTrObj.po_so_inner_sn,
                                    billsType: this.currentSaleVendorTrObj.billsType
                                });
                                break;
                            case this.systemTypes[2]:
                                this.orderSaleInfoAPIFn({
                                    partA_id: this.currentSaleHistoryTrObj.partA,
                                    partB_id: this.currentSaleHistoryTrObj.partB,
                                    po_so_inner_sn: this.currentSaleHistoryTrObj.po_so_inner_sn,
                                    billsType: this.currentSaleHistoryTrObj.billsType
                                });
                                break;
                        }
                    }
                    break;
            }
            return flag;
        },
        // tabs切换选项卡
        changeActiveFn(activeKey) {
            if (!this.checkSearchInfoFormMsgFn()) return;
            this.tabsActiveKey = activeKey;
            if (this.tabsActiveKey === this.systemTypes[1] && !this.isTabsActiveVendorKey) {
                this.isTabsActiveVendorKey = true;
                this.orderPurchaseListAPIFn('past');
            }
            if (this.tabsActiveKey === this.systemTypes[2] && !this.isTabsActiveHistoryKey) {
                this.isTabsActiveHistoryKey = true;
                this.orderHistoryPurchaseListAPIFn();
            }
        },
        // 选中项发生变化时的回调
        purchaseDelarSelectChangeFn(selectedRowKeys) {
            this.purchaseDelarSelectedRowKeys = selectedRowKeys;
        },
        purchaseVendorSelectChangeFn(selectedRowKeys) {
            this.purchaseVendorSelectedRowKeys = selectedRowKeys;
        },
        saleDelarSelectChangeFn(selectedRowKeys) {
            this.saleDelarSelectedRowKeys = selectedRowKeys;
        },
        saleVendorSelectChangeFn(selectedRowKeys) {
            this.saleVendorSelectedRowKeys = selectedRowKeys;
        },
        // 表格的高亮样式显示
        rowPurchaseDelarClassFn(record) {
            if (record.index === this.currentPurchaseDelarTrObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowPurchaseVendorClassFn(record) {
            if (record.index === this.currentPurchaseVendorTrObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowPurchaseHistoryClassFn(record) {
            if (record.index === this.currentPurchaseHistoryTrObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowSaleDelarClassFn(record) {
            if (record.index === this.currentSaleDelarTrObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowSaleVendorClassFn(record) {
            if (record.index === this.currentSaleVendorTrObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowSaleHistoryClassFn(record) {
            if (record.index === this.currentSaleHistoryTrObj.index) {
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
        cellClickPurchaseDelarFn({ row }) {
            this.currentPurchaseDelarTrObj = row;
        },
        cellDblclickPurchaseDelarFn({ row }) {
            this.currentPurchaseDelarTrObj = row;
            this.groupBtnFn('edit');
        },
        selectChangePurchaseDelarFn({ row }) {
            this.$refs.xPurchaseDelarTable.setCurrentRow(row);
        },
        cellClickPurchaseVendorFn({ row }) {
            this.currentPurchaseVendorTrObj = row;
        },
        cellDblclickPurchaseVendorFn({ row }) {
            this.currentPurchaseVendorTrObj = row;
            this.groupBtnFn('edit');
        },
        selectChangePurchaseVendorFn({ row }) {
            this.$refs.xPurchaseVendorTable.setCurrentRow(row);
        },
        cellClickPurchaseHistoryFn({ row }) {
            this.currentPurchaseHistoryTrObj = row;
        },
        cellDblclickPurchaseHistoryFn({ row }) {
            this.currentPurchaseHistoryTrObj = row;
            this.groupBtnFn('edit');
        },
        customSaleDelarRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentSaleDelarTrObj = record;
                    },
                    dblclick: () => {
                        this.currentSaleDelarTrObj = record;
                        this.groupBtnFn('edit');
                    }
                }
            };
        },
        customSaleVendorRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentSaleVendorTrObj = record;
                    },
                    dblclick: () => {
                        this.currentSaleVendorTrObj = record;
                        this.groupBtnFn('edit');
                    }
                }
            };
        },
        customSaleHistoryRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentSaleHistoryTrObj = record;
                    },
                    dblclick: () => {
                        this.currentSaleHistoryTrObj = record;
                        this.groupBtnFn('edit');
                    }
                }
            };
        },
        cellAddInfoClickFn({ row }) {
            this.currentTrPlanModalObj = row;
            this.resetFocusFn();
        },
        cellAddInfoDblclickFn({ row }, e) {
            this.currentTrPlanModalObj = row;
            this.resetFocusFn();
            if (this.isEditDisabled) return;
            this.dblclickTableFn(e);
        },
        // 右上角刷新按钮
        refreshPageFn() {
            switch (this.myOrderPropObj.pageType) {
                case 0:
                    if (this.tabsActiveKey === this.systemTypes[0]) {
                        this.purchaseDelarSelectedRowKeys = [];
                        this.saleVendorSelectedRowKeys = [];
                        this.purchaseDelarPagination = {
                            ...this.purchaseDelarPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1,
                            pageSizes: [10, 20, 30, 40]
                        };
                        this.orderPurchaseListAPIFn('perform');
                    } else if (this.tabsActiveKey === this.systemTypes[1]) {
                        this.saleDelarSelectedRowKeys = [];
                        this.purchaseVendorSelectedRowKeys = [];
                        this.purchaseVendorPagination = {
                            ...this.purchaseVendorPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1,
                            pageSizes: [10, 20, 30, 40]
                        };
                        this.orderPurchaseListAPIFn('past');
                    } else if (this.tabsActiveKey === this.systemTypes[2]) {
                        this.purchaseHistoryPagination = {
                            ...this.purchaseHistoryPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1
                        };
                        this.orderHistoryPurchaseListAPIFn();
                    }
                    break;
                case 1:
                    if (this.tabsActiveKey === this.systemTypes[0]) {
                        this.saleDelarPagination = {
                            ...this.saleDelarPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1
                        };
                        this.orderSaleListAPIFn('perform');
                    } else if (this.tabsActiveKey === this.systemTypes[1]) {
                        this.saleVendorPagination = {
                            ...this.saleVendorPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1
                        };
                        this.orderSaleListAPIFn('past');
                    } else if (this.tabsActiveKey === this.systemTypes[2]) {
                        this.saleHistoryPagination = {
                            ...this.saleHistoryPagination,
                            loading: true,
                            total: 10,
                            current: 1,
                            // pageSize: 10,
                            pageNum: 1
                        };
                        this.orderHistorySaleListAPIFn();
                    }
                    break;
            }
        },
        // 页码改变的回调函数
        changePurchaseDelarPaginationFn({ currentPage, pageSize }) {
            this.purchaseDelarPagination.current = currentPage;
            this.purchaseDelarPagination.pageSize = pageSize;
            this.purchaseDelarPagination.loading = true;
            this.orderPurchaseListAPIFn('perform');
        },
        changePurchaseVendorPaginationFn({ currentPage, pageSize }) {
            this.purchaseVendorPagination.current = currentPage;
            this.purchaseVendorPagination.pageSize = pageSize;
            this.purchaseVendorPagination.loading = true;
            this.orderPurchaseListAPIFn('past');
        },
        changePurchaseHistoryPaginationFn({ currentPage, pageSize }) {
            this.purchaseHistoryPagination.current = currentPage;
            this.purchaseHistoryPagination.pageSize = pageSize;
            this.purchaseHistoryPagination.loading = true;
            this.orderHistoryPurchaseListAPIFn();
        },
        changeSaleDelarPaginationFn({ currentPage, pageSize }) {
            this.saleDelarPagination.current = currentPage;
            this.saleDelarPagination.pageSize = pageSize;
            this.saleDelarPagination.loading = true;
            this.orderSaleListAPIFn('perform');
        },
        changeSaleVendorPaginationFn({ currentPage, pageSize }) {
            this.saleVendorPagination.current = currentPage;
            this.saleVendorPagination.pageSize = pageSize;
            this.saleVendorPagination.loading = true;
            this.orderSaleListAPIFn('past');
        },
        changeSaleHistoryPaginationFn({ currentPage, pageSize }) {
            this.saleHistoryPagination.current = currentPage;
            this.saleHistoryPagination.pageSize = pageSize;
            this.saleHistoryPagination.loading = true;
            this.orderHistorySaleListAPIFn();
        },
        // 双击此处添加产品的方法
        // 检测采购单位填值了没有
        checkPlanModalOrgSnFn() {
            let flag = false;
            if (this.planModalOrgSn.length <= 0) {
                flag = true;
                this.$warning({
                    title: '操作提示',
                    content: '请选择左上方的【供货单位】，再添加商品。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
            } else {
                if (this.myOrderPropObj.pageType === 0) {
                    this.partB.partA_id = this.cookieInfo.get('userbelong');
                    // this.partB.partB_id = this.planModalForm.partB
                    // this.partB.billsType = this.billsType
                    this.partB.modalType = 4;
                    // this.partB.oname = this.planModalForm.partB_name
                } else if (this.myOrderPropObj.pageType === 1) {
                    // this.partB.partA_id = this.planModalForm.partA
                    this.partB.partB_id = this.cookieInfo.get('userbelong');
                    // this.partB.billsType = this.billsType
                    this.partB.modalType = 4;
                    // this.partB.oname = this.planModalForm.partA_name
                }
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
            this.resetFormFocusFn();
            if (e) {
                const filterDom = ['span', 'svg', 'path', 'input', 'i', 'li', 'ul'],
                    flag = filterDom.some((item) => item === e.target.tagName.toLowerCase()),
                    childrenFlag = filterDom.some(
                        (item) => item === e.target.children && e.target.children.tagName.toLowerCase()
                    ),
                    childrenFlagRemove = e.target.getElementsByClassName('action-btn-list-wrap').length;
                if (flag || childrenFlag || childrenFlagRemove) return;
            }
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
        async getProReturn(dea, ven) {
            // 获取回调商品
            this.addProductEnter = false;
            this.addProductMd.alert = false;
            this.planModalPagination.loading = true;
            this.clearRowClassNameFn();
            let xTableAddInfo = this.$refs.xTablePlanModalInfo,
                tempDatas = [...ven, ...dea],
                oldAddDatas = tempDatas.map((item, index) => {
                    return {
                        ...item,
                        focusDatas: [false, index === tempDatas.length - 1, false, false, false, false],
                        unitPriceList: [],
                        rate: 0
                    };
                });
            await xTableAddInfo.insertAt(oldAddDatas, -1);
            let tableData = xTableAddInfo.getTableData().tableData;

            let spliceIndex = tableData.findIndex((item) => !item.product_inner_sn);
            if (spliceIndex > -1) {
                await xTableAddInfo.remove(tableData[spliceIndex]);
            }
            xTableAddInfo.clearCurrentRow();

            this.planModalDatas = xTableAddInfo.getTableData().tableData;
            const curData = this.planModalDatas[this.planModalDatas.length - 1];

            xTableAddInfo.focus();

            xTableAddInfo.setCurrentRow(curData);

            this.scrollTableArrowDownFn();

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
        updateFooterPlanModalFnBN() {
            this.$refs.xTablePlanModalInfo.updateData();
        },
        updateFooterPlanModalFn() {
            const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
            xTablePlanModalInfo.updateFooter();
            xTablePlanModalInfo.updateData();
            this.planModalDatas = xTablePlanModalInfo.getTableData().tableData;
        },
        // 订单表格的操作
        planModalActionBtnFn(e, btnName, record) {
            switch (btnName) {
                case 'remove': {
                    // 删除
                    if (this.isEditDisabled) return;
                    if (
                        typeof record.detail_inner_sn !== 'undefined' &&
                        !this.removeSelfCreateOrganizationTypeFn([record])
                    ) {
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
                    this.$refs.xTablePlanModalInfo.updateData();
                    break;
                }
                case 'stop':
                    record.loading = true;
                    record.switch ? this.orderDetailStopAPIFn(record) : this.orderDetailUnstopAPIFn(record);
                    break;
            }
        },
        // 只能删除自建的产品
        removeSelfCreateOrganizationTypeFn(checkRecordRows) {
            const isOrderPageType = this.myOrderPropObj.pageType; // 0 - 采购单  1 - 销售单
            let flag = true,
                createOrganizationType = isOrderPageType === 0 ? 1 : 0; // 采购单： 0 - 自建   1 - 买家    销售单： 0 - 买家   1 - 自建
            for (let i = 0; i < checkRecordRows.length; i++) {
                let item = checkRecordRows[i];
                if (parseInt(item.create_organization_type) === createOrganizationType) {
                    flag = false;
                    break;
                }
            }
            return flag;
        },
        // 删除订单的合计
        removeOrderLastFn() {
            if (this.planModalDatas.length <= 1) {
                this.planModalDatas = [];
            } else {
                this.currentTrPlanModalObj = this.planModalDatas.length ? this.planModalDatas[0] : {};
            }
        },
        // 导出excel
        exportExcel() {
            const form = this.planModalForm;
            const formData = {
                title: '采购订单',
                desc: {
                    label: '摘要',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '单据编号',
                            value: form.po_sn
                        },
                        {
                            label: '采购单位',
                            value: form.partB_name
                        },
                        {
                            label: '采购员',
                            value: form.buyer_name
                        },
                        {
                            label: '采购部门',
                            value: form.department_name
                        },
                        {
                            label: '期望到货日期',
                            value: form.expect_aog_date
                        },
                        {
                            label: '仓库',
                            value: form.warehouse_name
                        },
                        {
                            label: '送货地址',
                            value: form.delivery_address
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
            const { columns, tableData, footerData } = this._genVisibleTableData();
            this.$Utils.exportExcel(
                columns,
                tableData.map((i) => ({
                    ...i,
                    unit_price: this.unit_price_record(i),
                    rate: `${i.rate}%`
                })),
                footerData,
                formData
            );
        },
        handlePrint() {
            let { columns, tableData } = this._genVisibleTableData();
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
            let routeUrl = this.$router.resolve({
                // pageType为0时，打印采购订单，为1时，打印销售订单
                // 销售订单打印还未迁移至新的打印模块
                path: this.myOrderPropObj.pageType === 0 ? `/newprint/purchaseOrder` : 'print'
            });
            window.localStorage.setItem(
                'printContent',
                JSON.stringify({
                    // 状态为终止的产品，不进入打印单
                    printData: tableData.filter(
                        (item) => item.product_inner_sn && item.status !== productStatusTextMap[3]
                    ),
                    printForm: this.planModalForm,
                    printTable: columns,
                    myOrderPropObj: this.myOrderPropObj,
                    printType: 6, // 1 图片；2、耗材申领；3、出入库单；4、采购询价单；5、采购计划单; 6、采购-订单/销售-订单
                    planOrderStatus: ['待处理', '已终止', '已生成采购订单'],
                    printPageType: this.myOrderPropObj
                })
            );
            window.open(routeUrl.href, '_blank');
            this.clickDescriptionFn();
        },
        // 订单表格底部的按钮操作
        planModalGroupBtnFn(btnName) {
            if (
                !(btnName === 'print' || btnName === 'exportExcel' || btnName === 'cancel' || btnName === 'savePo') &&
                !this.checkPlanModalFormFn(btnName)
            )
                return;
            switch (btnName) {
                case 'print': // 打印
                    this.handlePrint();
                    break;
                case 'exportExcel':
                    this.exportExcel();
                    break;
                case 'add': // 生成采购订单
                    this.planModal.loadingAdd = true;
                    this.orderSaleDetailBuildAPIFn();
                    break;
                case 'cancel': // 取消
                    this.closePlanModalFn();
                    this.resetPlanModalFn();
                    break;
                case 'saveDraft': // 存草稿
                    this.planModal.loadingSaveDraft = true;
                    if (this.myOrderPropObj.pageType === 0) {
                        if (this.btnName === 'add') {
                            this.orderPurchaseAddAPIFn(0);
                        } else {
                            this.orderPurchaseEditAPIFn(0);
                        }
                    } else if (this.myOrderPropObj.pageType === 1) {
                        if (this.btnName === 'add') {
                            this.orderSaleAddAPIFn(0);
                        } else {
                            this.orderSaleEditAPIFn(0);
                        }
                    }
                    break;
                case 'save': // 保存
                    this.planModal.loadingSave = true;
                    if (this.myOrderPropObj.pageType === 0) {
                        if (this.btnName === 'add') {
                            this.orderPurchaseAddAPIFn(1); // 保存正式
                        } else {
                            // 单据来源为销售方方且状态为已审核的销售订单可以编辑 状态保存为3(确认)
                            const status = this.isSourceBuyerAudited ? 3 : 1;
                            this.orderPurchaseEditAPIFn(status);
                        }
                    } else if (this.myOrderPropObj.pageType === 1) {
                        if (this.btnName === 'add') {
                            this.orderSaleAddAPIFn(1); // 保存正式
                        } else {
                            this.orderSaleEditAPIFn(1);
                        }
                    }
                    break;
                case 'savePo': {
                    let flag = this.checkCreateOrganizationTypeFn();
                    if (!flag) return;
                    this.planModal.loadingSave = true;
                    if (this.myOrderPropObj.pageType === 0) {
                        this.orderPurchaseConfirmAPIFn();
                    } else if (this.myOrderPropObj.pageType === 1) {
                        this.orderSaleConfirmAPIFn();
                    }
                    break;
                }
                case 'audit': // 审核
                    this.planModal.loadingAudit = true;
                    if (this.myOrderPropObj.pageType === 0) {
                        this.orderPurchaseEditAPIFn(2);
                    } else if (this.myOrderPropObj.pageType === 1) {
                        this.orderSaleEditAPIFn(2);
                    }
                    break;
            }
        },
        // 检测是否是自己创建的订单 【details】
        checkCreateOrganizationTypeFn() {
            let contentMySelf = '不能确认自己创建的订单。',
                flag = true;
            const isOrderPage = 0,
                createOrganizationType = parseInt(this.planModalForm.create_organization_type); // 判断是0-采购订单  1-销售订单
            if (this.tabsActiveKey === this.systemTypes[0]) {
                if (flag && createOrganizationType === isOrderPage) {
                    flag = false;
                    this.$warning({
                        title: '操作提示',
                        content: contentMySelf,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                }
            } else if (this.tabsActiveKey === this.systemTypes[1]) {
                if (flag && createOrganizationType === isOrderPage) {
                    flag = false;
                    this.$warning({
                        title: '操作提示',
                        content: contentMySelf,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                }
            }
            return flag;
        },
        // 检测是否是自己创建的订单 【list】
        checkCreateOrganizationTypeListFn(checkboxRecords) {
            const createOrganizationType = this.myOrderPropObj.pageType === 0 ? 0 : 1, // 判断是0-采购订单  1-销售订单
                isConfirm = checkboxRecords.every(
                    (item) => parseFloat(item.create_organization_type) === createOrganizationType
                );
            if (isConfirm) {
                this.$warning({
                    title: '操作提示',
                    content: '不能确认自己创建的订单。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 设置修改过的修改信息内容字段
        checkEditInfoFormObjFn() {
            let editInfoFlag = true;
            this.editInfoFormNewObj = {};
            for (let server in this.planModalForm) {
                if (
                    typeof [server + '_old'] !== 'undefined' &&
                    this.planModalForm[server] !== this.planModalForm[server + '_old']
                ) {
                    this.editInfoFormNewObj = {
                        ...this.editInfoFormNewObj,
                        [server]: this.planModalForm[server], // 新值
                        [server + '_old']: this.planModalForm[server + '_old'] // 旧值
                    };
                    editInfoFlag = false;
                }
            }
            return editInfoFlag;
        },
        // 校验采购计划单
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
            if (
                (this.myOrderPropObj.pageType === 0 && !this.planModalForm.partB) ||
                (this.myOrderPropObj.pageType === 1 && !this.planModalForm.partA)
            ) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择供货单位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            if (!flag) return;
            if (this.myOrderPropObj.pageType === 0 && !this.planModalForm.buyer_id && !this.isSourceBuyerAudited) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择采购员',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            if (
                this.myOrderPropObj.pageType === 0 &&
                !this.planModalForm.department_inner_sn &&
                !this.isSourceBuyerAudited
            ) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择采购部门',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            // 判断表格主体的数量 - 包装单位
            let datas = this.$refs.xTablePlanModalInfo.getTableData().tableData.filter((item) => item.product_inner_sn);

            if (datas.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请添加产品',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }

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
                let isRepeatObj = {};
                for (let i = 0; i < datas.length; i++) {
                    const item = datas[i],
                        unitPriceNew = (item.tax_unit_price + '').split('.'),
                        len = 2, // 小数的有效长度是2位
                        unitPriceLen = unitPriceNew.length >= 2 ? unitPriceNew[unitPriceNew.length - 1].length : 0,
                        discountNew = (item.discount + '').split('.'),
                        discountLen = discountNew.length >= 2 ? discountNew[discountNew.length - 1].length : 0;
                    // rateNew = (item.rate + '').split('.'),
                    // rateLen = rateNew.length >= 2 ? rateNew[rateNew.length - 1].length : 0
                    if (unitPriceLen > 4) {
                        // 单价是4位
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
                    if (this.$Utils.checkFloatNumFn((item.rate * 10000) / 10000)) {
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
            }

            if (!flag) return;
            if (flag && this.btnName === 'edit' && this.checkEditInfoFormObjFn()) {
                // 校验产品列表有没有改过
                flag = true;
            }
            if (flag) {
                this.detailList = datas.map((item, index) => {
                    return {
                        detail_inner_sn: item.detail_inner_sn || '',
                        product_vendor_inner_sn:
                            item.product_vendor_inner_sn ||
                            item.vendor_inner_sn ||
                            item.dealer_b_inner_sn ||
                            item.dealer_inner_sn, // 之前是 dealer_inner_sn
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
                        sort_number: index,
                        batch_number: item.batch_number,
                        unit_price:
                            typeof item.tax_unit_price === 'undefined' ? 0 : parseFloat(item.tax_unit_price).toFixed(4) // 传递给后端的字段取值变为 【单价 = 含税单价   含税单价 = 单价】
                    };
                });
                flag = true;
            }
            return flag;
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
        },
        // 重置采购计划单模态框
        resetPlanModalFn() {
            this.expectAogDate = null;
            this.planModalOrgSn = [];
            this.planModalDatas = [];
            this.planModalEmployeSn = [];
            this.planModalDepartmentSn = [];
            this.planModalEmployes = [];
            this.planModalDepartmentSns = [];
            this.planModalLocation = '';
            this.planModalWarehouse = '';
            this.planModalWarehouseSn = '';
            this.detailList = [];
            this.editInfoFormNewObj = {};
            this.planModalSelectedRowKeys = [];
            this.currentTrPlanModalObj = {};
            this.planModalForm = {
                creator_name: JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
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
                billsType: '',
                partA_id: '',
                partB_id: '',
                po_sn: '',
                po_sn_old: '',
                po_so_inner_sn: '',
                status: ''
            };
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
                        let value = '';
                        if (!isNaN(cItem.employee_inner_sn)) {
                            value += cItem.employee_inner_sn;
                        } else if (!isNaN(cItem.department_inner_sn)) {
                            value += cItem.department_inner_sn;
                        } else if (!isNaN(cItem.org_id)) {
                            value += cItem.org_id + '' + cItem.billsType;
                        }
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn,
                            activeClass: '',
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            department_name_new: formatDepartmentNameFn(cItem.department_name),
                            disabled:
                                cItemDisabledFlag ||
                                (isAppendDatas === 'deptTopTreePlan' && parseInt(cItem.status) <= 0),
                            value,
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
                        let value = '';
                        if (!isNaN(cItem.employee_inner_sn)) {
                            value += cItem.employee_inner_sn;
                        } else if (!isNaN(cItem.department_inner_sn)) {
                            value += cItem.department_inner_sn;
                        } else if (!isNaN(cItem.org_id)) {
                            value += cItem.org_id + '' + cItem.billsType;
                        }
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn,
                            activeClass: '',
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            department_name_new: formatDepartmentNameFn(cItem.department_name),
                            disabled:
                                cItemDisabledFlag ||
                                (isAppendDatas === 'deptTopTreePlan' && parseInt(cItem.status) <= 0),
                            value,
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
                    let value = '';
                    if (!isNaN(item.employee_inner_sn)) {
                        value += item.employee_inner_sn;
                    } else if (!isNaN(item.department_inner_sn)) {
                        value += item.department_inner_sn;
                    } else if (!isNaN(item.org_id)) {
                        value += item.org_id + '' + item.billsType;
                    }
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn,
                        activeClass: '',
                        parentDepartmentName: [item.name],
                        department_name_new: formatDepartmentNameFn(item.department_name),
                        disabled:
                            itemDisabledFlag || (isAppendDatas === 'deptTopTreePlan' && parseInt(item.status) <= 0),
                        value,
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
                    let value = '';
                    if (!isNaN(item.employee_inner_sn)) {
                        value += item.employee_inner_sn;
                    } else if (!isNaN(item.department_inner_sn)) {
                        value += item.department_inner_sn;
                    } else if (!isNaN(item.org_id)) {
                        value += item.org_id + '' + item.billsType;
                    }
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn,
                        activeClass: '',
                        parentDepartmentName: [item.name],
                        department_name_new: formatDepartmentNameFn(item.department_name),
                        disabled:
                            itemDisabledFlag || (isAppendDatas === 'deptTopTreePlan' && parseInt(item.status) <= 0),
                        value,
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

            switch (isAppendDatas) {
                case 'deptTopTree': // 部门
                    this.topSearchAllTreeList = datasOriginNew;
                    break;
                case 'deptTopTreePlan': // 采购部门
                    this.planModalDepartmentSns = datasOriginNew;
                    // 设置默认部门值为当前登陆人的第一个部门
                    if (this.btnName === 'add') {
                        let intersection = [];
                        let userBelong = JSON.parse(localStorage.getItem('userInfo')).Departments;
                        let departmentData = this.$Utils.flatArrayWithChildren(datasOriginNew);
                        userBelong.filter((a) => {
                            departmentData.filter((b) => {
                                if (a.Department_inner_sn === b.department_inner_sn) {
                                    intersection.push(b);
                                }
                            });
                        });
                        this.planModalDepartmentSn = intersection[0].department_inner_sn;
                        this.changePlanModalDepartmentSnFn(
                            intersection[0].department_inner_sn,
                            intersection[0].department_name
                        );
                    }
                    break;
                case 'allEmpDropdownList': // 采购员
                    this.topSearchEmpDropdownList = datasOriginNew;
                    this.planModalEmployes = datasOriginNew;
                    if (this.btnName === 'add') {
                        this.planModalEmployeSn = JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn;
                        this.changePlanModalEmployeSnFn(
                            JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn,
                            JSON.parse(this.$cookie.get('EmployeeInfo')).Name
                        );
                    }
                    break;
            }
        },
        // 包装单位区域
        clickUnitListFn(record, seq) {
            if (!record.isLoadingUnit) {
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
            if (typeof seq !== 'undefined' && seq - 1 > -1) {
                this.resetFocusFn('unit_inner_sn', seq - 1);
            }
            this.record = record;
        },
        changeUnitListFn(value, selectedOptions) {
            this.record.unit_name = selectedOptions.length ? selectedOptions[selectedOptions.length - 1].name : '';
            this.$refs.xTablePlanModalInfo.focus();
            this.contractGetOrderPriceAPIFn(this.record);
        },
        displayUnitRender({ labels }) {
            let label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
        },
        changeExpectAogDateFn(date, dateString) {
            this.planModalForm.expect_aog_date = dateString;
            this.$refs.xTablePlanModalInfo.focus();
        },
        footerCellClassNameFn({ column }) {
            const redAry = ['money', 'discount_money', 'tax_amount', 'tax'];
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
        afterClosePlanmodalFn() {
            this.partB.oname = '';
            this.planModalOrgs = '';
            this.isAddProductModalTableMax = false;
            const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
            if (xTablePlanModalInfo) {
                xTablePlanModalInfo.revert();
                xTablePlanModalInfo.refreshScroll();
                this.planModalDatas = [];
                xTablePlanModalInfo.clearData();
            }
        },
        _isStop(status) {
            // 获取订单明细列表 item是否为终止
            const ITEM_STOP_STATUS = 3; // 已终止处理
            return status === ITEM_STOP_STATUS;
        },
        // 生成打印和导出 转换处理过的数据（状态文本名称，包装名称文本，金额计算等）
        _genVisibleTableData() {
            const { footerData } = this.$refs['xTablePlanModalInfo'].getTableData();
            const columns = this.$refs['xTablePlanModalInfo'].getColumns();
            const tempDatas = this.planModalDatas.map((item, index) => {
                // 表体数据
                return {
                    ...item,
                    index: index + 1,
                    status: this.$options.filters['formatStatus'](item.status, 'planOrder'), // 状态名称
                    unit_inner_sn: item.unit_name, // 包装单位名称
                    money: this.money_record(item), // 金额
                    discount_unit_price: this.discount_unit_price_record(item), // 折后单价
                    discount_money: this.discount_money_record(item), // 折后金额
                    // tax_unit_price: this.tax_unit_price_record(item), // 含税单价
                    unit_price: this.$XEUtils.commafy(item.unit_price, { fixed: 4 }),
                    tax_amount: this.tax_amount_record(item), // 含税金额
                    tax: this.tax_record(item), // 税额
                    newest_register_sn: item.newest_register_sn || item.mdrf_sn,
                    product_vendor_name: item.product_vendor_name || item.vendor_name || item.dealer_name
                };
            });
            return {
                columns, // 表头
                footerData, // 表尾数据
                tableData: tempDatas
            };
        },
        openChooseTb(value) {
            // 打开采购单位
            if (this.$refs.xTablePlanModalInfo) {
                this.$refs.xTablePlanModalInfo.blur();
            }
            this.pageTypeName = 7;
            this.relevantInstitutionsFocus = true;
            this.searchOrgName = typeof value === 'string' ? value : this.partB.oname || '';
            this.resetFocusFn();
            this.isParentKeyDownEnter = true;
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
            // 获取选中采购单位信息
            const objValue = resObj.org_id + '-' + resObj.billsType,
                objLabel = resObj.name;
            const xTableAddInfo = this.$refs.xTablePlanModalInfo;
            xTableAddInfo.focus();
            this.changePlanModalOrgSnFn(objValue, objLabel);
            xTableAddInfo.clearScroll().then(() => {
                xTableAddInfo.scrollTo(0, 0);
            });
            this.planModalOrgs = objValue;
            this.chooseOrgMd.alert = false;
            this.isParentKeyDownEnter = false;
            if (this.relevantInstitutionsFocus) {
                // this.addProductZoomFn()
                this.insertTempProductDetailFn();
            }
        },
        // 创建临时的产品明细
        insertTempProductDetailFn() {
            const xTableAddInfo = this.$refs.xTablePlanModalInfo;

            xTableAddInfo.focus();
            let tempProduct = {
                // 前面的数字key依次对应着要聚焦的输入框，后面的value对应着当前要聚焦的那个输入框
                'focusDatas': [true, false, false, false, false, false],
                'common_use_unit': '',
                'vendor_inner_sn': '',
                'specification_inner_sn': '',
                'product_type': '',
                'specification_vendor_sn': '',
                'vendor_name': '',
                'specification': '',
                'id': '',
                'product_inner_sn': '',
                'product_name': '',
                'mdrf_sn': '',
                'common_use_unit_inner_sn': '',
                'activeClass': '',
                'key': '',
                'keyFormat': '',
                'partB_name': '',
                'status': 0,
                'quantity_delivered': '',
                'unit_price': 0,
                'money': 0,
                'discount': 0,
                'discount_unit_price': 0,
                'discount_money': 0,
                'rate': 0,
                'tax_unit_price': 0,
                'tax_amount': 0,
                'tax': 0,
                'sys_type': '',
                'unit_inner_sn': [],
                'unit_name': '',
                'unitList': [],
                'quantity': 1,
                'isLoadingUnit': false,
                'row_add_flag': true,
                'temp_add_flag': true,
                'batch_number': '',
                'unitPriceList': []
            };
            xTableAddInfo.clearCurrentRow();
            let spliceIndex = this.planModalDatas.findIndex((item) => !item.product_inner_sn);
            if (spliceIndex > -1) {
                return;
            }
            xTableAddInfo.insertAt(tempProduct, -1);
            this.relevantInstitutionsFocus = false;
            const tableData = xTableAddInfo.getTableData().tableData,
                curData = tableData[tableData.length - 1];
            this.planModalDatas = tableData;
            xTableAddInfo.setCurrentRow(curData);
            this.scrollTableArrowDownFn();
        },
        // 表格详情滚动到底部
        scrollTableArrowDownFn() {
            const xTableAddInfo = this.$refs.xTablePlanModalInfo;
            // rowHeight = 38
            // xTableAddInfo.refreshScroll()
            xTableAddInfo.clearScroll().then(() => {
                xTableAddInfo.scrollToColumn(xTableAddInfo.getColumns(0));
                xTableAddInfo.scrollToRow(xTableAddInfo.getData(this.planModalDatas.length - 1));
                // xTableAddInfo.scrollTo(0, this.planModalDatas.length * rowHeight)
            });
        },
        // 表格的键盘事件
        buyTableKeydownFn(tableEvent) {
            const { $event } = tableEvent,
                keyCode = parseInt($event.keyCode);

            if (keyCode === 27) {
                // Esc
                // this.addProductZoomFn()
                return;
            }

            if (
                (Object.keys(this.planModalForm).length && this.planModalForm.status > 1) ||
                (this.addProductEnter && this.addProductMd.alert)
            )
                return;
            // const {$event, $grid, $table} = tableEvent,

            if (keyCode === 13) {
                // 回车键
                this.enterKeydownFn();
            } else {
                if (this.addProductMd.alert) return;
                switch (keyCode) {
                    case 38: // ↑
                        this.toogleVerticalFocusAndActiveRowFn(keyCode);
                        break;
                    case 40: // ↓
                        this.toogleVerticalFocusAndActiveRowFn(keyCode);
                        break;
                    case 37: // ←
                        this.toogleHorizontalFocusRowFn(keyCode);
                        break;
                    case 39: // →
                        this.toogleHorizontalFocusRowFn(keyCode);
                        break;
                }
            }
        },
        // 表格的回车事件
        enterKeydownFn() {
            this.resetFormFocusFn();
            const xTableAddInfo = this.$refs.xTablePlanModalInfo,
                tableData = xTableAddInfo.getTableData().tableData,
                existProductEmptyIndex = tableData.findIndex((item) => !item.product_inner_sn);

            xTableAddInfo.clearCurrentRow();

            xTableAddInfo.focus();
            if (existProductEmptyIndex <= -1) {
                // 需要创建空数据
                this.resetFocusFn();
                this.insertTempProductDetailFn();
            } else {
                // 说明有需要添加的空数据，直接调出产品添加页面
                if (!this.addProductMd.alert) {
                    this.partB['isKeyCode'] = true;
                    this.partB['keyCodeVal'] = tableData[existProductEmptyIndex].product_name || '';
                    this.dblclickTableFn();
                    setTimeout(() => {
                        this.addProductEnter = true;
                    }, 800);
                } else {
                    if (this.addProductMd.alert && this.addProductEnter) {
                        // this.addProductFn()
                    }
                }
            }
        },
        addProductDetailFn(e) {
            if (e.key === 'Enter' && this.addProductMd.alert && this.addProductEnter) {
                this.addProductFn();
            }
        },
        addEventProductDocumentFn() {
            document.addEventListener('keyup', this.addProductDetailFn, false);
        },
        removeEventProductDocumentFn() {
            document.removeEventListener('keyup', this.addProductDetailFn, false);
        },
        // 所有光标重置
        resetFocusFn(property, rowIndex) {
            const xTableAddInfo = this.$refs.xTablePlanModalInfo;
            for (let i = 0; i < this.planModalDatas.length; i++) {
                let item = this.planModalDatas[i];
                if (rowIndex === i) {
                    item.focusDatas = [
                        property === 'product_name',
                        property === 'quantity',
                        property === 'unit_inner_sn',
                        property === 'batch_number',
                        property === 'rate',
                        property === 'tax_unit_price'
                    ];
                } else {
                    item.focusDatas = [false, false, false, false, false, false];
                }
            }
            xTableAddInfo.loadData(this.planModalDatas);
        },
        // 水平方向切换焦点事件
        toogleHorizontalFocusRowFn(keyCode) {
            const xTableAddInfo = this.$refs.xTablePlanModalInfo,
                currowData = xTableAddInfo.getCurrentRecord(),
                focusDatas = currowData.focusDatas,
                rowHeight = 38;

            let currowIndex = xTableAddInfo.getRowIndex(currowData);

            if (!currowData.product_inner_sn) return;
            let curFocusIndex = focusDatas.findIndex((item) => !!item),
                isBoundary = false; // 是否是边界值，默认不是边界

            if (keyCode === 39) {
                if (curFocusIndex + 1 <= focusDatas.length - 1) {
                    curFocusIndex += 1;
                } else {
                    isBoundary = true;
                }
            } else {
                if (curFocusIndex - 1 >= 1) {
                    curFocusIndex -= 1;
                } else {
                    isBoundary = true;
                }
            }

            if (isBoundary) return;

            for (let f = 0; f < focusDatas.length; f++) {
                if (f === curFocusIndex) {
                    focusDatas[f] = true;
                } else {
                    focusDatas[f] = false;
                }
            }
            xTableAddInfo.reloadRow(
                {
                    ...currowData,
                    focusDatas
                },
                null,
                'focusDatas'
            );

            xTableAddInfo.syncData();

            this.planModalDatas = xTableAddInfo.getTableData().tableData;

            currowIndex = curFocusIndex === 0 ? currowIndex : currowIndex + 1;
            // xTableAddInfo.clearScroll().then(() => {
            //     xTableAddInfo.scrollToColumn(xTableAddInfo.getColumns(currowIndex <= 2 ? 0 : 12))
            //     xTableAddInfo.scrollToRow(xTableAddInfo.getData(currowIndex))
            // })
            // 产品、 数量处理 [滚动条滚动到最左侧]
            if ((curFocusIndex === 0 || curFocusIndex === 1) && focusDatas[curFocusIndex]) {
                xTableAddInfo.clearScroll().then(() => {
                    // xTableAddInfo.scrollTo(0, currowIndex * rowHeight)
                    xTableAddInfo.scrollToColumn(xTableAddInfo.getColumns(0));
                    xTableAddInfo.scrollToRow(xTableAddInfo.getData(currowIndex));
                });
            }
            // 税率处理 [滚动条滚动到最右侧]
            if (curFocusIndex === 3 && focusDatas[curFocusIndex]) {
                xTableAddInfo.clearScroll().then(() => {
                    xTableAddInfo.scrollTo(900, currowIndex * rowHeight);
                });
            }

            // 包装单位额外处理
            if (curFocusIndex === 2 && focusDatas[curFocusIndex]) {
                this.clickUnitListFn(currowData);
            }
        },
        closeAddProductFn() {
            this.addProductEnter = false;
            this.addProductMd.alert = false;
            const xTableAddInfo = this.$refs.xTablePlanModalInfo,
                tableData = xTableAddInfo.getTableData().tableData;

            xTableAddInfo.focus();
            xTableAddInfo.clearCurrentRow();
            xTableAddInfo.setCurrentRow(tableData[tableData.length - 1]);
        },
        addProductZoomFn() {
            this.isAddProductModalTableMax = !this.isAddProductModalTableMax;
            const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
            if (xTablePlanModalInfo) {
                xTablePlanModalInfo.focus();
            }
            // if (this.isAddProductModalTableMax) {
            //     xTablePlanModalInfo.revert()
            // } else {
            //     xTablePlanModalInfo.maximize()
            // }
            // this.isAddProductModalTableMax = !this.isAddProductModalTableMax
            // xTablePlanModalInfo.zoom().then((type, $event) => {
            //     if (this.isAddProductModalTableMax == type && type) {
            //         xTablePlanModalInfo
            //     }
            //     console.log(type)
            //     this.isAddProductModalTableMax = type
            // })
        },
        // 垂直方向切换焦点事件
        toogleVerticalFocusAndActiveRowFn(keyCode) {
            const xTableAddInfo = this.$refs.xTablePlanModalInfo,
                tableData = xTableAddInfo.getTableData().tableData,
                rowHeight = 38;

            let currowData = xTableAddInfo.getCurrentRow(),
                currowIndex = xTableAddInfo.getRowIndex(currowData),
                isBoundary = false; // 是否是边界值，默认不是边界

            if (keyCode === 40) {
                if (currowIndex + 1 <= tableData.length - 1) {
                    currowIndex += 1;
                } else {
                    isBoundary = true;
                }
            } else {
                if (currowIndex - 1 >= 0) {
                    currowIndex -= 1;
                } else {
                    isBoundary = true;
                }
            }

            if (isBoundary) return;

            this.resetFocusFn();

            currowData = tableData[currowIndex];
            if (!currowData.product_inner_sn) {
                currowData.focusDatas.splice(0, 1, true);
            } else {
                currowData.focusDatas.splice(1, 1, true);
            }

            xTableAddInfo.reloadRow(currowData, null, 'focusDatas');

            xTableAddInfo.syncData();
            xTableAddInfo.clearCurrentRow();
            xTableAddInfo.setCurrentRow(currowData);

            this.planModalDatas = xTableAddInfo.getTableData().tableData;

            xTableAddInfo.clearScroll().then(() => {
                if (currowIndex <= 1) {
                    currowIndex = 0;
                }
                if (currowIndex >= tableData.length - 1) {
                    currowIndex = tableData.length;
                }
                xTableAddInfo.scrollTo(null, currowIndex * rowHeight);
            });
        },
        async warehouseUsableTopListAPIFn() {
            this.planModalWarehouseData = this.warehouseList;
            this.isWareHouseFilterLoading = false;
            // await warehouseUsableTopListAPI()
            //     .then(res => {
            //         if (parseFloat(res.code) === 0) {
            //             const {list} = res
            //             let newList = list.map((item, index) => {
            //                 return {
            //                     ...item,
            //                     title: item.name,
            //                     value: item.warehouse_model_inner_sn,
            //                     key: item.warehouse_model_inner_sn
            //                 }
            //             })
            //             this.planModalWarehouseData = newList
            //             this.isWareHouseFilterLoading = false
            //         } else {
            //             this.$message.error(res.msg)
            //         }
            //     })
        },
        relevantInstitutionsKeyDownFn(e) {
            if (this.relevantInstitutionsFocus) {
                this.isParentKeyDownEnter = true;
                this.openChooseTb(e.target.value);
            }
        },
        /**
         * 更新订单中某一条产品记录的税率值
         */
        updateProductRate(value, index) {
            /// 先将焦点重置
            this.resetFocusFn();

            let v = value;
            if (value === null) {
                v = 0;
            }
            this.planModalDatas[index].rate = v;
            this.updateFooterPlanModalFn();
        },
        /**
         * 更新订单中某一条产品记录的数量值
         */
        updateProductQuantity(value, index) {
            /// 先将焦点重置
            this.resetFocusFn();

            const record = this.planModalDatas[index];
            const { multiply, round } = this.$XEUtils;
            let v = value;
            if (value === null) {
                v = 1;
            }
            record.quantity = v;
            record.tax_amount = round(multiply(v, record.tax_unit_price), 3);
            this.updateFooterPlanModalFn();
        },
        /**
         * 更新订单中某一条产品记录的含税单价值
         * 此时动态地计算含税金额
         * 产品含税单价对应后端接口中的unit_price字段、前端的tax_unit_price字段，而前端的unit_price字段代表的是不含税的产品单价
         */
        updateProductTaxUnitPrice(value, index) {
            /// 先将焦点重置
            this.resetFocusFn();

            const { multiply, toFixed } = this.$XEUtils;
            const record = this.planModalDatas[index];
            let v = value;
            if (value === null) {
                v = 0;
            }
            record.tax_unit_price = v;
            record.tax_amount = parseFloat(toFixed(multiply(v, record.quantity), 4));
            this.updateFooterPlanModalFn();
        },
        /**
         * 更新订单中某一条产品记录的含税金额值（总记）
         * 此时动态地计算含税单价（总是假定产品数量不变，以用户输入为准）
         * 含税单价保持小数后4位的精度
         */
        updateProductTaxAmountTotal(value, index) {
            /// 先将焦点重置
            this.resetFocusFn();

            const { divide, toFixed } = this.$XEUtils;
            const record = this.planModalDatas[index];
            let v = value;
            if (value === null) {
                v = 0;
            }
            record.tax_amount = v;
            record.tax_unit_price = parseFloat(toFixed(divide(v, record.quantity), 4));
            this.updateFooterPlanModalFn();
        },
        /// 如果模态框打开，按Esc关闭之
        handlerAddInfoModalFn(e) {
            const { keyCode } = e;
            if (keyCode === 27 && this.planModal.alert) {
                // Esc
                this.planModal.alert = false;
                this.afterClosePlanmodalFn();
            }
        },
        handlerAddInfoModalAddEventFn() {
            document.addEventListener('keydown', this.handlerAddInfoModalFn, false);
        },
        handlerAddInfoModalRemoveEventFn() {
            document.removeEventListener('keydown', this.handlerAddInfoModalFn, false);
        }
    },
    filters: {
        numberToStringFilter(value) {
            if (typeof value !== 'number') {
                return value;
            }
            if (isNaN(value)) {
                return undefined;
            }
            return String(value);
        },
        formatStatus(value, name) {
            const planListStatus = ['草稿', '正式', '已审核', '已确认', '已终止', '已完结'];
            if (name === 'planList') {
                return planListStatus[value];
            } else if (name === 'planOrder') {
                return productStatusTextMap[value];
            }
        },
        formatOrgType(value) {
            const planListStatus = ['自建', '卖方', '已审核', '已确认', '已终止', '已完结'];
            return planListStatus[value];
        },
        filtersTwo(value) {
            // 保留两位小数
            if (typeof value === 'number') {
                return value.toFixed(2);
            }
            return '';
        }
    },
    computed: {
        scrollYPurchaseDelarNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 214 : 154,
                pager = 52;
            return clientHeight - topHeight - pager;
        },
        isEditDisabled() {
            // 弹框的内容是否可编辑
            let flag = true;
            if (this.btnName === 'add') {
                flag = false;
            } else if (
                (this.tabsActiveKey === this.systemTypes[0] || this.tabsActiveKey === this.systemTypes[1]) &&
                this.planModalForm.status <= 1 &&
                this.planModalForm.create_organization_type === 0
            ) {
                // 采购单：create_organization_type 0-自建  1-买家
                flag = false;
            }
            return flag;
        },
        /**
         * 仓库是否可编辑
         */
        isWarehouseEditDisabled() {
            if (this.isEditDisabled) {
                // 当单据的来源为销售方，进行单据确认步骤（此时单据状态为已审批）时可以修改仓库
                if (this.planModalForm.status === 2 && this.planModalForm.create_organization_type === 1) {
                    return false;
                }
            }
            return this.isEditDisabled;
        },
        isSourceBuyerAudited() {
            // 单据来源为销售方且状态为已审核的销售订单可以编辑数量和单价 禅道单号（2967）
            const status = this.planModalForm.status; // 状态 0-草稿 1-正式 2-已审批 3-确认 4-已终止 5-已完结
            const source = this.planModalForm.create_organization_type; // 单据生成方 0-采购/采购自建/买方 1-销售/销售自建/卖方
            return status === 2 && source === 1;
        },
        // 当前用于判断含税单价是否置灰，后期可修改为全局判断标准
        isPriceDisabled() {
            // 将来源为自建的已审核和已确认状态，以及来源为卖方的已确认状态的采购订单中的含税单价置灰。 禅道单号（3696）
            const pageType = this.myOrderPropObj.pageType; // 页面属性 0-采购订单 1-销售订单
            const status = this.planModalForm.status; // 状态 0-草稿 1-正式 2-已审批 3-确认 4-已终止 5-已完结
            const source = this.planModalForm.create_organization_type; // 单据生成方 0-采购/采购自建/买方 1-销售/销售自建/卖方
            const flag =
                (pageType === 0 && status === 2 && source === 0) || // 来源为采购自建的已审核状态
                (pageType === 0 && status === 3 && source === 0) || // 来源为采购自建的已确认状态
                (pageType === 0 && status === 3 && source === 1); // 来源为卖方的已确认状态
            return flag;
        },
        isEditDisabledPo() {
            // 单据编号【弹框的内容是否可编辑】
            let flag = true;
            if (this.btnName === 'add') {
                flag = false;
            } else if (
                (this.tabsActiveKey === this.systemTypes[0] || this.tabsActiveKey === this.systemTypes[1]) &&
                this.planModalForm.status <= 2
            ) {
                flag = false;
            }
            return flag;
        },
        isAddPlanModalDisabled() {
            // 内层的生成采购订单【采购计划单按钮】
            let flag = true;
            if (this.planModalDatas.length) {
                flag =
                    this.planModalSelectedRowKeys.length <= 0 ||
                    this.btnName === 'add' ||
                    (this.planModalForm.status > 0 && this.planModalForm.status < 4);
            }
            return flag;
        },
        rowSelectionPlanModal() {
            // 内层的生成采购订单前面的复选框状态
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
        planModalTextComputed() {
            // 采购计划单右下角的取消文字改变
            if (
                this.planModalForm.status === 0 ||
                this.planModalForm.status === 1 ||
                (this.planModalForm.status === 2 && this.planModalForm.create_organization_type !== 0) ||
                this.btnName === 'add'
            ) {
                return '取消';
            } else {
                return '关闭';
            }
        },
        displayRenderUnitFn({ labels }) {
            // 包装单位
            let label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
        },
        tabsTextComputed() {
            // 顶部头部的tabs切换标题
            return (num) => {
                // num: 第几个tabs切换的参数 - @required
                let title = '';
                switch (num) {
                    case 0:
                        switch (this.myOrderPropObj.pageType) {
                            case 0:
                                title = this.tabTexts[0];
                                break;
                            case 1:
                                title = this.tabTexts[2];
                                break;
                        }
                        break;
                    case 1:
                        switch (this.myOrderPropObj.pageType) {
                            case 0:
                                title = this.tabTexts[1];
                                break;
                            case 1:
                                title = this.tabTexts[3];
                                break;
                        }
                        break;
                    case 2:
                        switch (this.myOrderPropObj.pageType) {
                            case 0:
                                title = this.tabTexts[4];
                                break;
                            case 1:
                                title = this.tabTexts[4];
                                break;
                        }
                        break;
                }
                return title;
            };
        },
        placeholderComputed() {
            // 顶部搜索框文字
            let placeholder = '';
            if (this.tabsActiveKey === this.systemTypes[0]) {
                placeholder = this.myOrderPropObj.pageType === 0 ? this.tabTexts[0] : this.tabTexts[2];
            } else if (this.tabsActiveKey === this.systemTypes[1]) {
                placeholder = this.myOrderPropObj.pageType === 0 ? this.tabTexts[1] : this.tabTexts[3];
            } else if (this.tabsActiveKey === this.systemTypes[2]) {
                placeholder = '采购单位';
            }
            return placeholder;
        },
        historyColumnsComputed() {
            // 顶部外层表格的tab切换卡中的历史头部文字
            let title = '';
            if (this.myOrderPropObj.pageType === 0) {
                title = this.tabTexts[0] + '/' + this.tabTexts[1];
            } else if (this.myOrderPropObj.pageType === 1) {
                title = this.tabTexts[2] + '/' + this.tabTexts[3];
            }
            return title;
        },
        modalTopTitle() {
            // 模态框顶部的文字
            let title = '';
            switch (this.myOrderPropObj.pageType) {
                case 0:
                    title = '采购';
                    break;
                case 1:
                    title = '销售';
                    break;
            }
            return title;
        },
        topEditBtnDisabled() {
            // 外层顶部的修改按钮状态控制
            let flag = true;
            switch (this.myOrderPropObj.pageType) {
                case 0: {
                    let purchase1 = parseFloat(this.currentPurchaseDelarTrObj.status),
                        purchase2 = parseFloat(this.currentPurchaseVendorTrObj.status);
                    if (this.tabsActiveKey === this.systemTypes[0]) {
                        flag = !(purchase1 === 0 || purchase1 === 1);
                    } else if (this.tabsActiveKey === this.systemTypes[1]) {
                        flag = !(purchase2 === 0 || purchase2 === 1);
                    }
                    break;
                }
                case 1: {
                    let sale1 = parseFloat(this.currentSaleDelarTrObj.status),
                        sale2 = parseFloat(this.currentSaleVendorTrObj.status);
                    flag = !(sale1 === 0 || sale1 === 1);
                    if (this.tabsActiveKey === this.systemTypes[0]) {
                        flag = !(sale1 === 0 || sale1 === 1);
                    } else if (this.tabsActiveKey === this.systemTypes[1]) {
                        flag = !(sale2 === 0 || sale2 === 1);
                    }
                    break;
                }
            }
            return flag;
        },
        // 单价 = 含税单价 / (1 + 税率 / 100)
        unit_price_record() {
            const { isNumber, toFixed, add, divide } = this.$XEUtils;
            return (record) => {
                // parse float to all
                const fRate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
                const fTaxUnitPrice = parseFloat(record.tax_unit_price);

                let nextItem = divide(fTaxUnitPrice, add(1, fRate));
                if (isNumber(nextItem)) {
                    if (nextItem < 0) nextItem = 0;
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        // 金额 = 数量 * 单价
        money_record() {
            const { isNumber, toFixed, multiply, add, divide } = this.$XEUtils;
            return (record) => {
                // parse float to all
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
        // 折后单价
        discount_unit_price_record() {
            const { isNumber, toFixed, multiply } = this.$XEUtils;
            return (record) => {
                // parse float to all
                const fDiscount = parseFloat(record.discount);
                const fUnitPrice = parseFloat(record.unit_price);

                const nextItem = multiply(fUnitPrice, fDiscount);
                if (isNumber(nextItem)) {
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        // 合计：折后金额
        discount_money_total() {
            const { isNumber, multiply, add } = this.$XEUtils;
            return this.planModalDatas.reduce((prev, next) => {
                // parse float to all
                const fNextUnitPrice = parseFloat(next.unit_price);
                const fNextDiscount = parseFloat(next.discount);
                const fNextQuantity = parseFloat(next.quantity);

                let prevItem = parseFloat(prev),
                    nextItem = multiply(multiply(fNextUnitPrice, fNextQuantity), fNextDiscount);

                // 不计算已终止的产品
                const isStop = this._isStop(next.status);
                if (isStop) nextItem = 0;

                if (isNumber(prevItem) && isNumber(nextItem)) {
                    return add(prevItem, nextItem);
                } else {
                    return '';
                }
            }, 0);
        },
        // 折后金额
        discount_money_record() {
            const { isNumber, toFixed, multiply } = this.$XEUtils;
            return (record) => {
                // parse float to all
                const fDiscount = parseFloat(record.discount);
                const fQuantity = parseFloat(record.quantity);
                const fUnitPrice = parseFloat(record.unit_price);

                const nextItem = multiply(multiply(fUnitPrice, fQuantity), fDiscount);
                if (isNumber(nextItem)) {
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        // 含税单价
        tax_unit_price_record() {
            const { isNumber, toFixed, add, multiply, toNumber } = this.$XEUtils;
            return (record) => {
                // parse float to all
                const fRate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
                const fUnitPrice = parseFloat(record.unit_price);

                let nextItem = multiply(multiply(fUnitPrice, parseFloat(record.discount)), add(1, fRate));
                if (isNumber(nextItem)) {
                    return toFixed(toNumber(nextItem), 4);
                } else {
                    return '';
                }
            };
        },
        // 含税金额 = 数量 * 含税单价
        tax_amount_record() {
            const { isNumber, toFixed, multiply } = this.$XEUtils;
            return (record) => {
                const fTaxUnitPrice = parseFloat(record.tax_unit_price);

                let nextItem = multiply(record.quantity, fTaxUnitPrice);
                if (isNumber(nextItem)) {
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        // 税额 = 含税金额 - 金额
        tax_record() {
            const { isNumber, toFixed, add, subtract, multiply, divide } = this.$XEUtils;
            return (record) => {
                // parse float to all
                const fRate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
                const fTaxUnitPrice = parseFloat(record.tax_unit_price);
                const fQuantity = parseFloat(record.quantity);
                const fMoney = multiply(divide(fTaxUnitPrice, add(1, fRate)), fQuantity);

                let nextItem = subtract(multiply(fQuantity, fTaxUnitPrice), fMoney);
                if (isNumber(nextItem)) {
                    if (nextItem < 0) nextItem = 0;
                    return toFixed(nextItem, 4);
                } else {
                    return '';
                }
            };
        },
        // 合计：金额
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
        // 合计：含税金额
        tax_amount_total() {
            const records = this.planModalDatas;
            if (!records) {
                return 0;
            }
            const { isNumber, add } = this.$XEUtils;
            return records.reduce((prevValue, currentItem) => {
                const currentValue = isNumber(currentItem.tax_amount) ? currentItem.tax_amount : 0;
                // 不计算已终止的产品
                if (this._isStop(currentItem.status)) {
                    return prevValue;
                }
                return add(prevValue, currentValue);
            }, 0);
        },
        // 合计：税额
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
        scrollYAddProductNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 180,
                bottomHeight = 116;
            return clientHeight - topHeight - bottomHeight;
        },
        orgName() {
            const employeeInfo = this.$cookie.get('EmployeeInfo');
            return (employeeInfo ? JSON.parse(employeeInfo).OrgName : '') || '';
        },
        ...mapState(['warehouseList'])
    },
    mounted() {
        if (this.myOrderPropObj.pageType === 0) {
            this.orderPurchaseListAPIFn('perform');
            this.orderPurchaseListAPIFn('past');
            this.orderHistoryPurchaseListAPIFn();
        } else if (this.myOrderPropObj.pageType === 1) {
            this.orderSaleListAPIFn('perform');
            this.orderSaleListAPIFn('past');
            this.orderHistorySaleListAPIFn();
        }
    },
    watch: {
        addProductEnter: {
            handler(val) {
                if (val) {
                    this.addEventProductDocumentFn();
                } else {
                    this.removeEventProductDocumentFn();
                }
            },
            deep: true
        },
        'planModal.alert': {
            handler(val) {
                if (val) {
                    this.handlerAddInfoModalAddEventFn();
                } else {
                    this.handlerAddInfoModalRemoveEventFn();
                }
            },
            deep: true
        }
    },
    beforeDestroy() {
        this.removeEventProductDocumentFn();
        this.handlerAddInfoModalRemoveEventFn();
    }
};
</script>

<style lang="less" scoped>
@w: 24px;
.order-modal-list {
    //   position: absolute;
    //   left: @w;
    //   right: @w;
    //   bottom: 57px;
    display: flex;
    flex-direction: row;

    li {
        display: flex;
        flex-direction: row;
        padding: 24px 0;

        &:nth-of-type(1),
        &:nth-of-type(2) {
            flex: 1;

            .item-wrap {
                flex: 1;
            }
        }

        .item-wrap {
            display: flex;
            padding-right: 16px;

            .item {
                padding-bottom: 0;
                padding-right: @w;
            }
        }

        &:last-child {
            .item-wrap {
                &:last-child {
                    padding-right: 0;
                }
            }
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
// 高级搜索栏
ul#searchForm {
    li.search-form-item {
        width: auto;
        min-width: 280px;
        &.search-button {
            min-width: auto;
        }
        span.label,
        input.ant-input {
            display: inline-block;
        }
        span.label {
            min-width: 72px;
        }
    }
}
</style>
