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
                            <a-button type="primary" size="small" @click="groupBtnFn('edit')"> 修改 </a-button>
                            <a-button
                                :disabled="tabsActiveKey === systemTypes[1]"
                                type="danger"
                                size="small"
                                @click="groupBtnFn('remove')"
                            >
                                删除
                            </a-button>
                        </li>
                        <li>
                            <a-button
                                :disabled="tabsActiveKey === systemTypes[1]"
                                type="primary"
                                size="small"
                                @click="groupBtnFn('stop')"
                            >
                                终止
                            </a-button>
                        </li>
                        <li>
                            <a-button
                                :disabled="tabsActiveKey === systemTypes[1]"
                                type="primary"
                                size="small"
                                @click="sendPlanOrderActionFn('list')"
                            >
                                生成采购订单
                            </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-model="searchTables.ppl_sn"
                            v-show="!hightSearchObj.isArrow"
                            @search="developmentFn"
                            placeholder=""
                            size="small"
                            enterButton
                            class="search"
                        />
                        <div class="hight-search-text"
                            ><!--@click="hightSearchIconFn"-->
                            <a-tooltip placement="left" title="高级搜索">
                                <a-icon
                                    @click="hightSearchIconFn"
                                    :type="hightSearchObj.isArrow ? 'up-circle' : 'down-circle'"
                                />
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
                            <span class="label">单据编号</span>
                            <a-input v-model="searchTables.ppl_sn" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">制单人</span>
                            <a-input v-model="searchTables.creator_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">部门</span>
                            <a-tree-select
                                treeNodeFilterProp="title"
                                :showSearch="true"
                                :treeData="architectureAllTrees"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                treeDefaultExpandAll
                                v-model="searchTables.department_inner_sn"
                                allowClear
                                @change="changeTopSearchAllTreeListFn"
                                class="input"
                                style="min-width: 120px"
                            >
                            </a-tree-select>
                        </li>
                        <li>
                            <span class="label">状态</span>
                            <a-select
                                v-show="this.tabsActiveKey === 'perform'"
                                v-model="searchTables.status"
                                type="text"
                                allowClear
                                :options="pendingStatusOptions"
                                placeholder=""
                            >
                            </a-select>
                            <a-select
                                v-show="this.tabsActiveKey === 'past'"
                                v-model="searchTables.status"
                                type="text"
                                allowClear
                                :options="historyStatusOptions"
                                placeholder=""
                            >
                            </a-select>
                        </li>
                        <li>
                            <a-button @click="developmentFn" type="primary"> 搜索 </a-button>
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
            <a-tabs type="card" @change="changeActiveFn">
                <a-tab-pane tab="待处理" style="padding: 8px" :key="systemTypes[0]">
                    <vxe-table
                        ref="xPerformTable"
                        size="small"
                        :height="scrollYPerformNumber - 4"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        @cell-click="cellClickFn"
                        @cell-dblclick="cellDblclickFn"
                        @select-change="selectChangeFn"
                        align="center"
                        :data="performDatas"
                        :loading="performPagination.loading"
                        show-overflow
                        show-header-overflow
                        showHeader
                        :start-index="(performPagination.current - 1) * performPagination.pageSize"
                    >
                        <vxe-table-column type="checkbox" width="5%" :resizable="false"></vxe-table-column>
                        <vxe-table-column minWidth="15%" field="ppl_sn" title="单据编号"></vxe-table-column>
                        <vxe-table-column minWidth="5%" field="creator_name" title="制单人"></vxe-table-column>
                        <vxe-table-column minWidth="8%" field="auditor_name" title="审核人"></vxe-table-column>
                        <vxe-table-column minWidth="14%" field="department_name" title="部门"></vxe-table-column>
                        <vxe-table-column minWidth="15%" field="create_time" title="计划生成时间"></vxe-table-column>
                        <vxe-table-column minWidth="15%" field="audit_time" title="计划审核时间"></vxe-table-column>
                        <vxe-table-column minWidth="13%" field="status" title="状态">
                            <template v-slot="{ row }">
                                <span>{{ row.status | formatStatus('planList') }}</span>
                            </template>
                        </vxe-table-column>
                    </vxe-table>
                    <vxe-pager
                        :loading="performPagination.loading"
                        :current-page="performPagination.current"
                        :page-size="performPagination.pageSize"
                        :page-sizes="performPagination.pageSizes"
                        :total="performPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changePerformPaginationFn"
                    >
                    </vxe-pager>
                </a-tab-pane>
                <a-tab-pane tab="历史" style="padding: 8px" :key="systemTypes[1]">
                    <vxe-table
                        ref="xPastTable"
                        size="small"
                        :height="scrollYPerformNumber - 4"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        @cell-click="cellClickPastFn"
                        @cell-dblclick="cellDblclickPastFn"
                        align="center"
                        :data="pastDatas"
                        :loading="pastPagination.loading"
                        show-overflow
                        show-header-overflow
                        showHeader
                        :start-index="(pastPagination.current - 1) * pastPagination.pageSize"
                    >
                        <vxe-table-column minWidth="15%" field="ppl_sn" title="单据编号"></vxe-table-column>
                        <vxe-table-column minWidth="5%" field="creator_name" title="制单人"></vxe-table-column>
                        <vxe-table-column minWidth="8%" field="auditor_name" title="审核人"></vxe-table-column>
                        <vxe-table-column minWidth="14%" field="department_name" title="部门"></vxe-table-column>
                        <vxe-table-column minWidth="15%" field="create_time" title="计划生成时间"></vxe-table-column>
                        <vxe-table-column minWidth="15%" field="audit_time" title="计划审核时间"></vxe-table-column>
                        <vxe-table-column minWidth="13%" field="status" title="状态">
                            <template v-slot="{ row }">
                                <span>{{ row.status | formatStatus('planList') }}</span>
                            </template>
                        </vxe-table-column>
                    </vxe-table>
                    <vxe-pager
                        :loading="pastPagination.loading"
                        :current-page="pastPagination.current"
                        :page-size="pastPagination.pageSize"
                        :page-sizes="pastPagination.pageSizes"
                        :total="pastPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changePastPaginationFn"
                    >
                    </vxe-pager>
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

        <!-- 采购计划单区域开始 -->
        <a-modal
            v-model="planModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-add global-drag"
            :width="1100"
            :zIndex="3"
        >
            <div v-globalDrag="{ el: 'modal-add' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ planModal.title }}
                </h6>
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
                            :disabled="computedSendPlanOrderAction"
                            @click="sendPlanOrderActionFn('detail')"
                            :loading="planModal.loadingAdd"
                            key="add"
                        >
                            生成采购订单
                        </a-button>
                    </li>
                    <li>
                        <a-button @click="planModalGroupBtnFn('cancel')" key="cancel">
                            {{ planModalTextComputed }}
                        </a-button>
                        <a-button
                            v-show="planModalForm.status === 0 || btnName === 'add'"
                            @click="planModalGroupBtnFn('saveDraft')"
                            key="saveDraft"
                            type="primary"
                            :loading="planModal.loadingSaveDraft"
                        >
                            存草稿
                        </a-button>
                        <a-button
                            v-show="planModalForm.status === 0 || planModalForm.status === 1 || btnName === 'add'"
                            @click="planModalGroupBtnFn('save')"
                            key="submit"
                            type="primary"
                            :loading="planModal.loadingSave"
                        >
                            保存
                        </a-button>
                        <a-button
                            v-show="planModalForm.status === 1"
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
            <div class="modal-form-input" style="height: 520px; position: relative">
                <h6 style="font-size: 24px; color: #444; padding-bottom: 24px; text-align: center; margin-bottom: 0">
                    <span style="margin-right: 10px; font-size: 24px; color: #444">{{ orgName }}</span>
                    <span style="font-size: 24px; color: #444">采购计划单</span>
                </h6>
                <ul class="top-list" style="justify-content: space-between">
                    <li style="width: 55%">
                        <div v-if="btnName !== 'add'" class="item">
                            <span class="label">单据编号</span>
                            <a-input disabled v-model="planModalForm.ppl_sn" placeholder="" class="input" />
                        </div>
                        <div class="item">
                            <span class="label">部门</span>
                            <a-tree-select
                                :disabled="isEditDisabled"
                                treeNodeFilterProp="title"
                                :showSearch="true"
                                :treeData="architectureAllTrees"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                treeDefaultExpandAll
                                v-model="planModalForm.department_inner_sn"
                                allowClear
                                @change="setEditInfoFormDepartmentInnerSnFn"
                                class="input"
                                style="min-width: 130px"
                            >
                            </a-tree-select>
                        </div>
                    </li>
                </ul>
                <div
                    ref="applyTable"
                    class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                    style="padding-bottom: 0; margin-top: 0"
                >
                    <div class="top-add-product">
                        <a-tooltip placement="topLeft" title="新增产品">
                            <a-button
                                :disabled="isEditDisabled"
                                @click="dblclickTableFn"
                                icon="plus"
                                style="height: 24px; padding: 0 4px"
                            ></a-button>
                        </a-tooltip>
                    </div>
                    <vxe-grid
                        ref="xTablePlanModalInfo"
                        size="small"
                        :data="planModalDatas"
                        :loading="planModalPagination.loading"
                        :height="270 - ipadHeight"
                        border
                        resizable
                        auto-resize
                        show-overflow
                        show-header-overflow
                        highlight-current-row
                        :row-class-name="rowClassNameFn"
                        @cell-click="cellAddInfoClickFn"
                        @cell-dblclick="cellAddInfoDblclickFn"
                        align="center"
                    >
                        <vxe-table-column type="checkbox" width="60" fixed="left" :resizable="false"></vxe-table-column>
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
                            :cell-render="{ name: 'product_name' }"
                        ></vxe-table-column>
                        <vxe-table-column
                            field="specification"
                            title="规格型号"
                            :cell-render="{ name: 'specification' }"
                        ></vxe-table-column>
                        <vxe-table-column
                            field="product_vendor_name"
                            title="厂商/经销商"
                            :cell-render="{ name: 'product_vendor_name' }"
                        ></vxe-table-column>
                        <vxe-table-column
                            field="batch_number"
                            title="批号"
                            width="100"
                            :cell-render="{ name: 'batch_number' }"
                        >
                        </vxe-table-column>
                        <vxe-table-column field="quantity" title="数量" width="90">
                            <template v-slot="{ row }">
                                <a-input-number
                                    :disabled="isEditDisabled"
                                    :min="0.001"
                                    v-model="row.quantity"
                                    class="input"
                                    style="width: 100%"
                                />
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="unit_inner_sn" title="包装单位" width="90">
                            <template v-slot="{ row }">
                                <a-cascader
                                    :disabled="isEditDisabled"
                                    :allowClear="false"
                                    :options="row.unitList"
                                    v-model="row.unit_inner_sn"
                                    changeOnSelect
                                    :displayRender="displayUnitRender"
                                    @click.stop="clickUnitListFn(row)"
                                    @change="changeUnitFn"
                                    placeholder=""
                                />
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="status" title="状态" width="150">
                            <template v-slot="{ row }">
                                <div>{{ row.status | formatStatus('planOrder') }}</div>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            v-if="!(planModalForm.status === 2 || planModalForm.status === 3)"
                            field="action"
                            title="操作"
                            width="130"
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
                                                @click="planModalActionBtnFn($event, 'remove', row, seq)"
                                                class="yrt yrt-shanchu-copy cancel"
                                                style="cursor: pointer"
                                            ></i>
                                        </a-tooltip>
                                    </li>
                                    <li v-else-if="planModalForm.status === 2">
                                        <a-switch
                                            v-if="row.status === 0"
                                            @click="planModalActionBtnFn($event, 'stop', row, seq)"
                                            :loading="row.loading"
                                            :checked="row.switch"
                                            size="small"
                                            checkedChildren="待处理"
                                            unCheckedChildren="已终止"
                                            defaultChecked
                                            style="color: #faad14"
                                        />
                                        <a-switch
                                            v-else-if="row.status === 1"
                                            @click="planModalActionBtnFn($event, 'stop', row, seq)"
                                            :loading="row.loading"
                                            :checked="row.switch"
                                            size="small"
                                            checkedChildren="待处理"
                                            unCheckedChildren="已终止"
                                            defaultChecked
                                            style="color: #faad14"
                                        />
                                    </li>
                                    <li v-else-if="planModalForm.status === 3 || planModalForm.status === 4"> </li>
                                </ul>
                            </template>
                        </vxe-table-column>
                        <template v-slot:empty>
                            <div @dblclick.stop="dblclickTableFn" class="dblclick-wrap">
                                <span>双击添加产品</span>
                            </div>
                        </template>
                    </vxe-grid>
                </div>
                <div
                    class="top-list"
                    style="flex-direction: column; padding-top: 24px; position: absolute; bottom: 0; left: 0; right: 0"
                >
                    <div style="display: flex">
                        <span class="label" style="width: auto">摘要</span>
                        <a-input
                            :disabled="isEditDisabled"
                            v-model="planModalForm.description"
                            placeholder=""
                            class="input"
                        />
                    </div>
                    <ul style="display: flex; padding-top: 24px">
                        <li>
                            <div class="item" style="min-width: 140px; padding-bottom: 0">
                                <span class="label" style="width: auto">制单人</span>
                                <span>{{ planModalForm.creator_name || '-' }}</span>
                            </div>
                            <div class="item" style="min-width: 194px; padding-bottom: 0">
                                <span class="label" style="width: auto">生成时间</span>
                                <span>{{ planModalForm.create_time || '-' }}</span>
                            </div>
                        </li>
                        <li>
                            <div class="item" style="min-width: 140px; padding-bottom: 0">
                                <span class="label" style="width: auto">审核人</span>
                                <span>{{ planModalForm.auditor_name || '-' }}</span>
                            </div>
                            <div class="item" style="min-width: 194px; padding-bottom: 0">
                                <span class="label" style="width: auto">审核时间</span>
                                <span>{{ planModalForm.audit_time || '-' }}</span>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </a-modal>
        <!-- 采购计划单区域结束 -->

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
            <add-product ref="chooseProMd" @returnData="getProReturn" :partB="partB"></add-product>
        </a-modal>
        <!-- 添加产品区域结束 -->

        <!-- 生成采购订单区域开始 -->
        <a-modal
            :title="sendPlanOrderModal.title"
            v-model="sendPlanOrderModal.alert"
            :maskClosable="false"
            :width="1000"
            :centered="true"
            @cancel="sendPlanOrderModal.alert = false"
            class="modal-form-input-scoped global-drag send-plan-order"
            :zIndex="3"
        >
            <div v-globalDrag="{ el: 'send-plan-order' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ sendPlanOrderModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="sendPlanOrderModal.alert = false">取消</a-button>
                <a-button
                    :loading="sendPlanOrderModal.loading"
                    :disabled="sendPlanOrderModalDatas.length <= 0"
                    @click="sendPlanOrderListModalFn"
                    type="primary"
                    >生成</a-button
                >
            </template>
            <div class="scoped">
                <div class="modal-form-input" style="height: 472px; position: relative; padding-top: 0">
                    <div
                        class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                        style="padding-bottom: 0; margin-top: 0"
                    >
                        <vxe-grid
                            ref="xTableSendPlanOrderModalInfo"
                            size="small"
                            :data="sendPlanOrderModalDatas"
                            :loading="sendPlanOrderModalPagination.loading"
                            :height="470 - ipadHeight"
                            border
                            resizable
                            auto-resize
                            show-overflow
                            show-header-overflow
                            highlight-current-row
                            @cell-click="cellSendPlanOrderClickFn"
                            @cell-dblclick="cellSendPlanOrderDblclickFn"
                            align="center"
                        >
                            <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                                <template v-slot="{ seq }">
                                    <div>
                                        <!-- <i :class="!row.detail_inner_sn ? 'red-dot-add' : 'hide'"></i> -->
                                        <span>{{ seq }}</span>
                                    </div>
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
                                width="14%"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column field="unit_price" title="含税单价" width="12%">
                                <template v-slot="{ row }">
                                    {{ $XEUtils.commafy(row.unit_price, { fixed: 4 }) }}
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="quantity" title="数量" width="12%">
                                <template v-slot="{ row }">
                                    <a-input-number :min="1" v-model="row.quantity_new" class="input" />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_inner_sn" title="包装单位" width="12%">
                                <template v-slot="{ row }">
                                    <a-cascader
                                        :allowClear="false"
                                        :options="row.unitList"
                                        v-model="row.unit_inner_sn"
                                        readonly
                                        disabled
                                        placeholder=""
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="batch_number"
                                title="批号"
                                width="10%"
                                :cell-render="{ name: 'batch_number' }"
                            ></vxe-table-column>
                            <vxe-table-column field="supplierList" title="供应商" width="16%">
                                <template v-slot="{ row }">
                                    <a-tree-select
                                        treeNodeFilterProp="title"
                                        :showSearch="true"
                                        :treeData="row.supplierList"
                                        v-model="row.supplier_name"
                                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                        treeDefaultExpandAll
                                        @click="clickSelectSupplierListFn(row)"
                                        @select="selectSelectSupplierListFn"
                                        class="input"
                                    >
                                    </a-tree-select>
                                </template>
                            </vxe-table-column>
                        </vxe-grid>
                    </div>
                </div>
            </div>
        </a-modal>
        <!-- 生成采购订单区域结束 -->

        <!-- 预览 - 生成采购订单区域开始 -->
        <a-modal
            :title="sendPlanOrderPreviewModal.title"
            v-model="sendPlanOrderPreviewModal.alert"
            :maskClosable="false"
            :width="1200"
            :centered="true"
            @cancel="sendPlanOrderPreviewModal.alert = false"
            :zIndex="3"
            class="modal-form-input-scoped global-drag send-plan-order-preview"
        >
            <div v-globalDrag="{ el: 'send-plan-order-preview' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ sendPlanOrderPreviewModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="sendPlanOrderPreviewModal.alert = false">关闭</a-button>
            </template>
            <div class="scoped">
                <div
                    class="modal-form-input"
                    style="height: 520px; overflow-y: auto; position: relative; padding-top: 0"
                >
                    <div
                        v-if="sendPlanOrderPreviewBuildSpin && sendPlanOrderPreviewBuildDatas.length <= 0"
                        style="text-align: center; margin-top: 230px"
                    >
                        <a-spin :spinning="sendPlanOrderPreviewBuildSpin"> </a-spin>
                    </div>
                    <ul v-else class="build-plan-parent">
                        <li
                            v-for="(buildPlanPItem, buildPlanPIndex) in sendPlanOrderPreviewBuildDatas"
                            :key="buildPlanPItem.key"
                            :class="
                                buildPlanPIndex === sendPlanOrderPreviewBuildDatas.length - 1
                                    ? ''
                                    : 'build-plan-parent-item'
                            "
                        >
                            <h6
                                style="
                                    display: flex;
                                    justify-content: center;
                                    align-items: center;
                                    font-size: 24px;
                                    color: #444;
                                    padding-bottom: 24px;
                                    margin-bottom: 0;
                                "
                            >
                                <span style="font-size: 24px; color: #444">采购订单</span>
                                <div
                                    v-if="btnName !== 'add'"
                                    style="position: absolute; right: 0; display: flex; padding-right: 36px"
                                >
                                    <span class="label" style="font-size: 14px; color: rgba(0, 0, 0, 0.65)"
                                        >单据编号</span
                                    >
                                    <a-input disabled v-model="buildPlanPItem.po_sn" placeholder="" class="input" />
                                </div>
                            </h6>
                            <ul class="top-list" style="justify-content: space-between">
                                <li>
                                    <div class="item">
                                        <span class="label" style="width: auto; max-width: 96px"
                                            ><em class="em red">*</em>相关机构</span
                                        >
                                        <a-input
                                            disabled
                                            v-model="buildPlanPItem.partB_name"
                                            placeholder=""
                                            class="input"
                                        />
                                    </div>
                                </li>
                                <li>
                                    <div class="item">
                                        <span class="label" style="width: auto; max-width: 96px"
                                            ><em class="em red">*</em>采购员</span
                                        >
                                        <a-input
                                            disabled
                                            v-model="buildPlanPItem.buyer_name"
                                            placeholder=""
                                            class="input"
                                        />
                                    </div>
                                </li>
                                <li>
                                    <div class="item">
                                        <span class="label" style="width: auto; max-width: 96px"
                                            ><em class="em red">*</em>采购部门</span
                                        >
                                        <a-input
                                            disabled
                                            v-model="buildPlanPItem.department_name"
                                            placeholder=""
                                            class="input"
                                        />
                                    </div>
                                </li>
                                <li>
                                    <div class="item">
                                        <span class="label" style="width: auto; max-width: 96px">期望到货日期</span>
                                        <a-input
                                            disabled
                                            v-model="buildPlanPItem.expect_aog_date"
                                            placeholder=""
                                            class="input"
                                        />
                                    </div>
                                </li>
                            </ul>
                            <div
                                v-if="buildPlanPItem.details && buildPlanPItem.details.length > 0"
                                class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                                style="padding-bottom: 0; margin-top: 0"
                            >
                                <vxe-grid
                                    size="small"
                                    :columns="planBuildModalStopColumns"
                                    :data="buildPlanPItem.details"
                                    :height="270 - ipadHeight"
                                    border
                                    resizable
                                    auto-resize
                                    show-overflow
                                    show-header-overflow
                                    highlight-current-row
                                    align="center"
                                >
                                </vxe-grid>
                            </div>
                            <div class="top-list" style="flex-direction: column; padding-top: 24px">
                                <div style="display: flex">
                                    <span class="label" style="width: auto">摘要</span>
                                    <a-input
                                        disabled
                                        v-model="buildPlanPItem.description"
                                        placeholder=""
                                        class="input"
                                    />
                                </div>
                                <ul style="display: flex; padding-top: 24px">
                                    <li>
                                        <div class="item" style="min-width: 140px; padding-bottom: 0">
                                            <span class="label" style="width: auto">制单人</span>
                                            <span>{{ buildPlanPItem.creator_name || '-' }}</span>
                                        </div>
                                        <div class="item" style="min-width: 194px; padding-bottom: 0">
                                            <span class="label" style="width: auto">生成时间</span>
                                            <span>{{ buildPlanPItem.create_time || '-' }}</span>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="item" style="min-width: 140px; padding-bottom: 0">
                                            <span class="label" style="width: auto">审核人</span>
                                            <span>{{ buildPlanPItem.auditor_name || '-' }}</span>
                                        </div>
                                        <div class="item" style="min-width: 194px; padding-bottom: 0">
                                            <span class="label" style="width: auto">审核时间</span>
                                            <span>{{ buildPlanPItem.audit_time || '-' }}</span>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </a-modal>
        <!-- 预览 - 生成采购订单区域结束 -->
    </div>
</template>

<script>
import addProduct from '@/components/addProduct';
import {
    orgDeptTopTreeAPI,
    platformUnitTree,
    platformUnitParents,
    dealerUnitTreeAPI,
    dealerUnitParentsAPI,
    planUnexecutedListAPI,
    planHistoryListAPI,
    planBuildAPI,
    planStopAPI,
    planDelAPI,
    planAddAPI,
    planEditAPI,
    planInfoAPI,
    planDetailDelAPI,
    planDetailStopAPI,
    planDetailUnstopAPI,
    planCreateAPI,
    planDetailCreateAPI
} from '@/service/pageAjax';

import moment from 'moment';

export default {
    name: 'plan',
    data() {
        return {
            employeeInfo: JSON.parse(this.$cookie.get('EmployeeInfo')),
            format: 'YYYY-MM-DD HH:mm:ss',
            topSearchAllTreeList: [], // 部门
            planModalDepartmentSns: [],
            searchTables: {
                ppl_sn: '',
                status: undefined,
                department_inner_sn: '',
                creator_name: ''
            }, // 表格的搜索并向后端请求字段
            performColumns: [
                {
                    title: '单据编号',
                    dataIndex: 'ppl_sn',
                    align: 'center',
                    minWidth: '20%'
                },
                {
                    title: '制单人',
                    dataIndex: 'creator_name',
                    align: 'center',
                    minWidth: '10%'
                },
                {
                    title: '审核人',
                    dataIndex: 'auditor_name',
                    align: 'center',
                    minWidth: '8%'
                },
                {
                    title: '部门',
                    dataIndex: 'department_name',
                    align: 'center',
                    minWidth: '14%'
                },
                {
                    title: '计划生成时间',
                    dataIndex: 'create_time',
                    align: 'center',
                    minWidth: '15%'
                },
                {
                    title: '计划审核时间',
                    dataIndex: 'audit_time',
                    align: 'center',
                    minWidth: '15%'
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    minWidth: '13%',
                    scopedSlots: { customRender: 'status' }
                }
            ],
            performDatas: [],
            performPagination: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentPerformTrObj: {},
            pastDatas: [],
            pastPagination: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentPastTrObj: {},
            systemTypes: ['perform', 'past'], // 草稿、正式、已审核3个状态在“待执行”卡片页中显示  已终止、已生成采购订单2个状态在“过往”卡片页中显示
            tabsActiveKey: 'perform',
            isTabsActiveKey: false,
            btnName: '',
            // 修改弹框的配置
            editInfoModal: {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            },
            // 采购计划单弹框
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
            planBuildModalStopColumns: [
                {
                    type: 'index',
                    title: 'NO.',
                    field: 'index',
                    key: '1',
                    width: 60,
                    align: 'center'
                },
                {
                    title: '产品',
                    field: 'product_name',
                    key: '1',
                    width: '20%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    field: 'specification',
                    key: '2',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '厂商',
                    field: 'partB_name',
                    key: '3',
                    width: '12%',
                    align: 'center'
                },
                {
                    title: '数量',
                    field: 'quantity',
                    key: '4',
                    width: '8%',
                    align: 'center'
                },
                {
                    title: '已到货数量',
                    field: 'quantity_delivered',
                    key: '5',
                    width: '8%',
                    align: 'center'
                },
                {
                    title: '状态',
                    field: 'status_text',
                    key: '7',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '包装单位',
                    field: 'unit_name',
                    key: 'unit_name',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '批号',
                    field: 'batch_number',
                    key: 'batch_number',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '单价',
                    field: 'unit_price',
                    key: '9',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '金额',
                    field: 'money',
                    key: '10',
                    width: 100,
                    align: 'center'
                },
                // {
                //     title: '折扣',
                //     field: 'discount',
                //     key: '11',
                //     width: 100,
                //     align: 'center'
                // },
                // {
                //     title: '折后单价',
                //     field: 'discount_unit_price',
                //     key: '12',
                //     width: 100,
                //     align: 'center'
                // },
                // {
                //     title: '折后金额',
                //     field: 'discount_money',
                //     key: '13',
                //     width: 100,
                //     align: 'center'
                // },
                {
                    title: '税率',
                    field: 'rate',
                    key: '14',
                    width: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            const value = parseFloat(row.rate) * 100 + '%';
                            return [value];
                        }
                    }
                },
                {
                    title: '含税单价',
                    field: 'tax_unit_price',
                    key: '15',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '含税金额',
                    field: 'tax_amount',
                    key: '16',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '税额',
                    field: 'tax',
                    key: '17',
                    width: 100,
                    align: 'center'
                }
            ],
            planModalColumns: [
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
                    width: '16%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '经销商/厂商',
                    dataIndex: 'vendor_name',
                    key: 'vendor_name',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: 'quantity',
                    width: '13%',
                    align: 'center',
                    scopedSlots: { customRender: 'quantity' }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_inner_sn',
                    key: 'unit_inner_sn',
                    width: '14%',
                    align: 'center',
                    scopedSlots: { customRender: 'unit' }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    key: 'status',
                    width: '14%',
                    align: 'center',
                    scopedSlots: { customRender: 'status' }
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    key: 'action',
                    width: '8%',
                    align: 'center',
                    scopedSlots: { customRender: 'action' }
                }
            ],
            planModalStopColumns: [
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
                    width: '16%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '经销商/厂商',
                    dataIndex: 'vendor_name',
                    key: 'vendor_name',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: 'quantity',
                    width: '13%',
                    align: 'center',
                    scopedSlots: { customRender: 'quantity' }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
                    width: '14%',
                    align: 'center',
                    scopedSlots: { customRender: 'unit_name' }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    key: 'status',
                    width: '14%',
                    align: 'center',
                    scopedSlots: { customRender: 'status' }
                }
            ],
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
            planModalForm: {
                audit_time: '',
                auditor_inner_sn: '',
                auditor_name: '',
                create_time: '',
                creator_inner_sn: '',
                creator_name: '',
                department_inner_sn: '',
                department_inner_sn_old: '',
                department_name: '',
                department_name_old: '',
                description: '',
                description_old: '',
                detailList: [],
                ppl_inner_sn: '',
                ppl_sn: '',
                ppl_sn_old: '',
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
            sendPlanOrderModal: {
                title: `${this.$route.meta.title} - 生成采购订单`,
                alert: false,
                loading: false
            },
            sendPlanOrderPreviewModal: {
                title: `${this.$route.meta.title} - 已生成采购订单 - 预览`,
                alert: false,
                loading: false
            },
            sendPlanOrderPreviewBuildSpin: false,
            sendPlanOrderModalPagination: {
                loading: false
            },
            sendPlanOrderPreviewBuildDatas: [],
            sendPlanOrderModalStopColumns: [
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
                    width: '25%',
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
                    title: '单价',
                    dataIndex: 'unit_price',
                    key: 'unit_price',
                    width: '12%',
                    align: 'center',
                    scopedSlots: { customRender: 'unit_price' }
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: 'quantity',
                    width: '12%',
                    align: 'center',
                    scopedSlots: { customRender: 'quantity' }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_inner_sn',
                    key: 'unit_inner_sn',
                    width: '12%',
                    align: 'center',
                    scopedSlots: { customRender: 'unit' }
                },
                {
                    title: '供应商',
                    dataIndex: 'supplierList',
                    key: 'supplierList',
                    width: '16%',
                    align: 'center',
                    scopedSlots: { customRender: 'supplierList' }
                }
            ],
            recordSupplierList: {},
            currentTrSendPlanOrderModalObj: {},
            sendPlanOrderModalDatas: [],
            detailSendPlan: [],
            partB: {
                // 新增产品窗口给子组件传递值
                id: this.$cookie.get('userbelong'),
                modalType: 3 // 1 - 合同内的产品  2 - 期初库存的产品列表  3 - 采购计划
            },
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            // 部门
            editInfoFormDepartmentInnerSn: [], // 所属部门
            architectureAllTrees: [], // 所属部门的信息列表
            // 待处理状态options
            pendingStatusOptions: [
                { label: '草稿', value: 0 },
                { label: '正式', value: 1 },
                { label: '已审核', value: 2 }
            ],
            // 历史状态options
            historyStatusOptions: [
                { label: '已终止', value: 3 },
                { label: '订单已生成', value: 4 }
            ],
            chooseSatus: 0,
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 57 : 0
        };
    },
    methods: {
        hightSearchIconFn() {
            if (this.architectureAllTrees.length <= 0) {
                this.orgDeptTopTreeAPIFn();
            }
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        // 设置后端所需要的格式
        changeTopSearchAllTreeListFn(value, label) {
            this.searchTables.department_inner_sn = value && value.length ? value[0] : '';
            this.planModalForm.department_inner_sn = value && value.length ? value[0] : '';
            this.planModalForm.department_name = label;
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
        // 【经销商 产品单位】 根据id查询所有上级产品单位
        async dealerUnitParentsAPIFn(record) {
            await dealerUnitParentsAPI(
                record.product_vendor_inner_sn,
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
                record.product_vendor_inner_sn,
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
        // 【未执行】采购计划列表
        async planUnexecutedListAPIFn(curObj) {
            this.performPagination.loading = true;
            let params = {
                pageNum: this.performPagination.current,
                pageSize: this.performPagination.pageSize,
                // 排序
                orderByColumn: 'ppl_sn',
                isAsc: 'desc'
            };
            if (this.searchTables.ppl_sn.length) {
                params.ppl_sn = this.searchTables.ppl_sn;
            }
            if (this.hightSearchObj.isArrow) {
                if (this.searchTables.status !== undefined && this.tabsActiveKey === 'perform') {
                    // params.status = this.searchTables.status[this.searchTables.status.length - 1]
                    params.status = this.searchTables.status;
                }
                if (this.searchTables.department_inner_sn.length) {
                    params.department_inner_sn =
                        this.searchTables.department_inner_sn[this.searchTables.department_inner_sn.length - 1];
                }
                if (this.searchTables.creator_name.length) {
                    params.creator_name = this.searchTables.creator_name;
                }
            }
            await planUnexecutedListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.performDatas = rows.map((item) => {
                            return {
                                ...item,
                                key: item.ppl_inner_sn,
                                activeClass: '',
                                status: parseFloat(item.status)
                            };
                        });

                        if (this.performDatas.length <= 0) {
                            this.currentPerformTrObj = {};
                        } else {
                            let isAdd = false;
                            if (curObj) {
                                const ary = this.performDatas.filter(
                                    (item) => item.ppl_sn + item.ppl_inner_sn === curObj.ppl_sn + curObj.ppl_inner_sn
                                );
                                if (ary.length) {
                                    isAdd = true;
                                    this.currentPerformTrObj = ary[0];
                                    this.$refs.xPerformTable.setCurrentRow(ary[0]);
                                }
                            }
                            if (!isAdd) {
                                this.currentPerformTrObj = this.performDatas[0];
                                this.$refs.xPerformTable.setCurrentRow(this.performDatas[0]);
                            }
                        }
                        this.performPagination = {
                            ...this.performPagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                        this.performPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.performPagination.loading = false;
                });
        },
        // 【历史】采购计划列表
        async planHistoryListAPIFn(curObj) {
            const params = {
                pageNum: this.pastPagination.current,
                pageSize: this.pastPagination.pageSize,
                orderByColumn: 'ppl_sn',
                isAsc: 'desc'
            };
            const {
                ppl_sn: pplSn,
                status,
                creator_name: creatorName,
                department_inner_sn: departmentInnerSn
            } = this.searchTables;
            const isArrow = this.hightSearchObj.isArrow;

            this.pastPagination.loading = true;
            if (pplSn.length) {
                params.ppl_sn = pplSn;
            }
            if (isArrow) {
                if (status !== undefined && this.tabsActiveKey === 'past') {
                    params.status = status;
                }
                if (departmentInnerSn.length) {
                    params.department_inner_sn = departmentInnerSn[departmentInnerSn.length - 1];
                }
                if (creatorName.length) {
                    params.creator_name = creatorName;
                }
            }
            await planHistoryListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.pastDatas = rows.map((item) => {
                            return {
                                ...item,
                                key: item.ppl_inner_sn,
                                activeClass: '',
                                status: parseFloat(item.status)
                            };
                        });

                        if (this.pastDatas.length <= 0) {
                            this.currentPastTrObj = {};
                        } else {
                            let isAdd = false;
                            if (curObj) {
                                const ary = this.pastDatas.filter(
                                    (item) => item.ppl_sn + item.ppl_inner_sn === curObj.ppl_sn + curObj.ppl_inner_sn
                                );
                                if (ary.length) {
                                    isAdd = true;
                                    this.currentPastTrObj = ary[0];
                                    this.$refs.xPastTable.setCurrentRow(ary[0]);
                                }
                            }
                            if (!isAdd) {
                                this.currentPastTrObj = this.pastDatas[0];
                                this.$refs.xPastTable.setCurrentRow(this.pastDatas[0]);
                            }
                        }
                        this.pastPagination = {
                            ...this.pastPagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                        this.pastPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.pastPagination.loading = false;
                });
        },
        // 【计算】采购订单数据
        async planCreateAPIFn(details) {
            await planCreateAPI(this.$cookie.get('userbelong'), details)
                .then((res) => {
                    this.sendPlanOrderModalPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const {
                            info: { buildData, detail }
                        } = res;
                        this.sendPlanOrderModalDatas = buildData.map((item, index) => {
                            if (item.supplierList && item.supplierList.length) {
                                item.supplierList = item.supplierList.map((sItemap, sIndex) => {
                                    return {
                                        ...sItemap,
                                        value:
                                            sItemap.customID +
                                            '-' +
                                            sItemap.supplier_type +
                                            '-' +
                                            sItemap.supplier_id +
                                            '-' +
                                            sIndex,
                                        label: sItemap.supplier_name
                                    };
                                });
                                if (item.supplierList.length === 1) {
                                    // 供应商列表只有一项值就默认选中对应的值，否则让用户自己选择
                                    const sItemapFirst = item.supplierList[0];
                                    item.supplier_id = sItemapFirst.value;
                                    item.supplier_name = sItemapFirst.supplier_name;
                                    item.unitList = [
                                        { value: sItemapFirst.unit_inner_sn, label: sItemapFirst.unit_name }
                                    ];
                                    item.unit_inner_sn = [sItemapFirst.unit_inner_sn];
                                    item.unit_name = sItemapFirst.unit_name;
                                    item.unit_price = sItemapFirst.unit_price;
                                    const curQuantity = item.quantity / sItemapFirst.measure;
                                    item.quantity_new = this.$Utils.checkFloatNumFn(curQuantity)
                                        ? parseInt(curQuantity) + 1
                                        : curQuantity;
                                } else {
                                    item.supplier_id = '';
                                    item.supplier_name = '';
                                    item.unitList = [];
                                    item.unit_inner_sn = '';
                                    item.unit_name = '';
                                    item.unit_price = '';
                                    item.quantity_new = '';
                                }
                            }
                            return {
                                ...item,
                                index,
                                activeClass: '',
                                key: item.product_vendor_inner_sn + '' + item.product_type
                            };
                        });
                        this.$refs.xPerformTable.clearSelection();
                        this.detailSendPlan = detail;
                        this.$Utils.globalDragCenterFn('send-plan-order');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.sendPlanOrderModalPagination.loading = false;
                });
        },
        // 【勾选计划明细计算】采购订单数据
        async planDetailCreateAPIFn(details) {
            await planDetailCreateAPI(this.$cookie.get('userbelong'), details)
                .then((res) => {
                    this.sendPlanOrderModalPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const {
                            info: { buildData, detail }
                        } = res;
                        this.sendPlanOrderModalDatas = buildData.map((item, index) => {
                            if (item.supplierList && item.supplierList.length) {
                                item.supplierList = item.supplierList.map((sItemap, sIndex) => {
                                    return {
                                        ...sItemap,
                                        value:
                                            sItemap.customID +
                                            '-' +
                                            sItemap.supplier_type +
                                            '-' +
                                            sItemap.supplier_id +
                                            '-' +
                                            sIndex,
                                        label: sItemap.supplier_name
                                    };
                                });
                                if (item.supplierList.length === 1) {
                                    // 供应商列表只有一项值就默认选中对应的值，否则让用户自己选择
                                    const sItemapFirst = item.supplierList[0];
                                    item.supplier_id = sItemapFirst.value;
                                    item.supplier_name = sItemapFirst.supplier_name;
                                    item.unitList = [
                                        { value: sItemapFirst.unit_inner_sn, label: sItemapFirst.unit_name }
                                    ];
                                    item.unit_inner_sn = [sItemapFirst.unit_inner_sn];
                                    item.unit_name = sItemapFirst.unit_name;
                                    item.unit_price = sItemapFirst.unit_price;
                                    const curQuantity = item.quantity / sItemapFirst.measure;
                                    item.quantity_new = this.$Utils.checkFloatNumFn(curQuantity)
                                        ? parseInt(curQuantity) + 1
                                        : curQuantity;
                                } else {
                                    item.supplier_id = '';
                                    item.supplier_name = '';
                                    item.unitList = [];
                                    item.unit_inner_sn = '';
                                    item.unit_name = '';
                                    item.unit_price = '';
                                    item.quantity_new = '';
                                }
                            }
                            return {
                                ...item,
                                index,
                                activeClass: '',
                                key: item.product_vendor_inner_sn + '' + item.product_type
                            };
                        });
                        this.$refs.xPerformTable.clearSelection();
                        this.detailSendPlan = detail;
                        this.$Utils.globalDragCenterFn('send-plan-order');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.sendPlanOrderModalPagination.loading = false;
                });
        },
        // 【生成】采购订单
        async planBuildAPIFn(buildData) {
            let params = {
                org_id: this.$cookie.get('userbelong'),
                buildData,
                detail: this.detailSendPlan
            };
            await planBuildAPI(params)
                .then((res) => {
                    this.sendPlanOrderPreviewBuildSpin = false;
                    this.sendPlanOrderModal.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res,
                            orderStatus = ['处理中', '已完全处理', '已计划采购', '已终止处理']; // 采购订单明细状态
                        this.sendPlanOrderPreviewBuildDatas = list.map((item) => {
                            if (item.details.length) {
                                item.details = item.details.map((cItem, cIndex) => {
                                    let money = cItem.quantity * cItem.unit_price;
                                    cItem.rate = cItem.rate < 0 ? 0 : parseFloat(cItem.rate) / 100;
                                    cItem.tax_unit_price = this.$XEUtils.commafy(cItem.unit_price, { fixed: 4 }); // 含税单价 = 单价 【默认值】
                                    cItem.unit_price = this.$XEUtils.commafy(cItem.unit_price / (1 + cItem.rate), {
                                        fixed: 4
                                    }); // 单价
                                    cItem.money = this.$XEUtils.commafy(money, { fixed: 2 }); // 金额 = 数量 * 单价
                                    cItem.tax_amount = this.$XEUtils.commafy(money, { fixed: 2 }); // 含税金额 = 数量 * 含税单价
                                    cItem.tax = this.$XEUtils.commafy(money - money, { fixed: 2 }); // 税额 = 含税金额 - 金额

                                    return {
                                        ...cItem,
                                        index: cIndex + 1,
                                        key: cItem.detail_inner_sn,
                                        activeClass: '',
                                        status_text: orderStatus[cItem.status],
                                        discount_unit_price: this.$XEUtils.commafy(cItem.unit_price * cItem.discount, {
                                            fixed: 2
                                        }),
                                        discount_money: this.$XEUtils.commafy(
                                            cItem.unit_price * cItem.quantity * cItem.discount,
                                            { fixed: 2 }
                                        ),
                                        quantity_delivered: '-'
                                    };
                                });
                            }
                            return {
                                ...item,
                                buyer_inner_sn: JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn,
                                buyer_name: JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
                                department_inner_sn: JSON.parse(localStorage.getItem('userInfo')).Departments[0]
                                    .Department_inner_sn,
                                department_name: JSON.parse(localStorage.getItem('userInfo')).Departments[0].Name,
                                key: Math.random(),
                                activeClass: ''
                            };
                        });
                        this.$Utils.globalDragCenterFn('send-plan-order-preview');
                        if (this.sendPlanOrderPreviewBuildDatas.length) {
                            this.planUnexecutedListAPIFn(this.$refs.xPerformTable.getCurrentRow());
                            this.planHistoryListAPIFn();
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.sendPlanOrderPreviewBuildSpin = false;
                    this.sendPlanOrderModal.loading = false;
                });
        },
        // 【终止计划】
        async planStopAPIFn() {
            this.performPagination.loading = true;
            const xPerformTable = this.$refs.xPerformTable,
                list = xPerformTable.getSelectRecords().map((item) => {
                    return {
                        org_id: this.$cookie.get('userbelong'),
                        ppl_inner_sn: item.ppl_inner_sn
                    };
                });
            await planStopAPI(list)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        if (
                            this.tabsActiveKey === this.systemTypes[1] &&
                            this.pastDatas.length >= this.pastPagination.pageSize
                        ) {
                            let pageNum = Math.floor(this.pastPagination.total / this.pastPagination.pageSize) + 1;
                            this.pastPagination.current = pageNum;
                        }
                        xPerformTable.clearSelection();
                        this.planUnexecutedListAPIFn();
                        this.planHistoryListAPIFn();
                        this.performPagination.loading = false;
                    } else {
                        this.$message.error(res.msg);
                        this.performPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.performPagination.loading = false;
                });
        },
        // 【新增】采购计划
        async planAddAPIFn(status) {
            let params = {
                department_inner_sn: this.planModalForm.department_inner_sn,
                department_name: this.planModalForm.department_name,
                description: this.planModalForm.description,
                detailList: this.detailList,
                org_id: this.$cookie.get('userbelong'),
                ppl_sn: this.planModalForm.ppl_sn,
                status
            };

            await planAddAPI(params)
                .then((res) => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }

                    if (parseFloat(res.code) === 0) {
                        this.performPagination.total++;
                        const { info } = res,
                            xTable = this.$refs.xPerformTable,
                            record = xTable.getCurrentRow();
                        xTable.insertAt(info, record).then(({ row }) => {
                            this.performDatas = xTable.getTableData().tableData;
                            xTable.setCurrentRow(row);
                            if (this.performDatas.length > this.performPagination.pageSize) {
                                this.performDatas.splice(this.performDatas.length - 1, 1);
                            }
                            this.closePlanModalFn();
                            this.resetPlanModalFn();
                        });
                        this.tabsActiveKey = this.systemTypes[0];
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
        // 【编辑】采购计划
        async planEditAPIFn(status) {
            const curObj = this.$refs.xPerformTable.getCurrentRow(),
                params = {
                    detailList: this.detailList,
                    org_id: this.$cookie.get('userbelong'),
                    ppl_inner_sn: this.planModalForm.ppl_inner_sn,
                    status
                };
            if (this.planModalForm.department_inner_sn !== this.planModalForm.department_inner_sn_old) {
                params.department_inner_sn = this.planModalForm.department_inner_sn;
                params.department_inner_sn_old = this.planModalForm.department_inner_sn_old;
                params.department_name = this.planModalForm.department_name;
            }

            if (this.planModalForm.description !== this.planModalForm.description_old) {
                params.description = this.planModalForm.description;
                params.description_old = this.planModalForm.description_old;
            }

            if (this.planModalForm.ppl_sn !== this.planModalForm.ppl_sn_old) {
                params.ppl_sn = this.planModalForm.ppl_sn;
                params.ppl_sn_old = this.planModalForm.ppl_sn_old;
            }

            await planEditAPI(params)
                .then((res) => {
                    if (status === 0) {
                        this.planModal.loadingSaveDraft = false;
                    } else if (status === 1) {
                        this.planModal.loadingSave = false;
                    }

                    if (parseFloat(res.code) === 0) {
                        const xTable = this.$refs.xPerformTable,
                            info = {
                                ...curObj,
                                status,
                                create_time: status === 1 ? moment(Date()).format(this.format) : curObj.create_time, // 草稿 -> 正式状态 显示计划生成时间
                                audit_time: status === 2 ? moment(Date()).format(this.format) : curObj.audit_time, // 正式 -> 已审核状态 显示计划审核时间
                                department_inner_sn: this.planModalForm.department_inner_sn,
                                department_name: this.planModalForm.department_name,
                                auditor_inner_sn: this.planModalForm.auditor_inner_sn,
                                auditor_name: status === 2 ? this.employeeInfo.Name : this.planModalForm.auditor_name,
                                creator_inner_sn: this.planModalForm.creator_inner_sn,
                                creator_name: this.planModalForm.creator_name
                            };
                        for (let i = 0; i < this.performDatas.length; i++) {
                            const item = this.performDatas[i];
                            if (curObj._XID === item._XID) {
                                this.performDatas.splice(i, 1, info);
                                xTable.refreshData();
                                xTable.loadData(this.performDatas);
                                xTable.setCurrentRow(info);
                                this.closePlanModalFn();
                                this.resetPlanModalFn();
                                break;
                            }
                        }
                        this.tabsActiveKey = this.systemTypes[0];
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
        // 【删除】采购计划
        async planDelAPIFn() {
            let params = this.$refs.xPerformTable.getSelectRecords().map((item) => {
                return {
                    org_id: item.org_id || this.$cookie.get('userbelong'),
                    ppl_inner_sn: item.ppl_inner_sn
                };
            });

            await planDelAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.planUnexecutedListAPIFn();
                    this.planHistoryListAPIFn();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 采购计划【详情】
        async planInfoAPIFn(pplInnerSn) {
            this.planModalPagination.loading = true;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                ppl_inner_sn: pplInnerSn
            };
            await planInfoAPI({
                params
            })
                .then((res) => {
                    this.planModalPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.planModalForm = {
                            ...info,
                            creator_name: info.creator_name || '-',
                            ppl_sn_old: info.ppl_sn,
                            department_inner_sn_old: info.department_inner_sn,
                            department_name_old: info.department_name,
                            description_old: info.description,
                            status: parseFloat(info.status)
                        };

                        this.editInfoFormDepartmentInnerSn = this.planModalForm.department_inner_sn;
                        this.planModalDatas = info.detailList.map((item, index) => {
                            return {
                                ...item,
                                // key: item.vendor_inner_sn + '' + item.ppl_inner_sn + item.product_inner_sn + item.product_type + item.specification_inner_sn,
                                index,
                                key: Math.random(),
                                activeClass: '',
                                vendor_name: item.product_vendor_name || item.vendor_name,
                                unitList: item.unit_inner_sn
                                    ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                    : [],
                                unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                                isLoadingUnit: false, // 是否加载过整棵树
                                loading: false,
                                switch: this.planModalForm.status === 2 && item.status === 0,
                                status: parseFloat(item.status)
                            };
                        });
                        this.$refs.xTablePlanModalInfo.refreshColumn();
                        // this.currentTrPlanModalObj = this.planModalDatas.length ? this.planModalDatas[0] : {}
                        // !this.isEditDisabled && this.dblclickAddProductFn()
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
        // 【删除】计划明细
        async planDetailDelAPIFn(record) {
            this.planModalPagination.loading = true;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                product_type: record.product_type,
                ppl_inner_sn: record.ppl_inner_sn,
                detail_inner_sn: record.detail_inner_sn
            };
            await planDetailDelAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
                        xTablePlanModalInfo.remove(record);
                        this.planModalDatas = xTablePlanModalInfo.getTableData().tableData;
                        xTablePlanModalInfo.setCurrentRow(this.planModalDatas[0]);
                        this.dblclickAddProductFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.planModalPagination.loading = false;
                })
                .catch(() => {
                    this.planModalPagination.loading = false;
                });
        },
        // 【终止】计划明细
        async planDetailStopAPIFn(record) {
            let params = {
                org_id: this.$cookie.get('userbelong'),
                product_type: record.product_type,
                ppl_inner_sn: record.ppl_inner_sn,
                detail_inner_sn: record.detail_inner_sn
            };

            await planDetailStopAPI(params)
                .then((res) => {
                    record.loading = false;
                    if (parseFloat(res.code) === 0) {
                        record.switch = !record.switch;
                        record.status = 1;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    record.loading = false;
                });
        },
        // 【反终止】计划明细
        async planDetailUnstopAPIFn(record) {
            let params = {
                org_id: this.$cookie.get('userbelong'),
                product_type: record.product_type,
                ppl_inner_sn: record.ppl_inner_sn,
                detail_inner_sn: record.detail_inner_sn
            };

            await planDetailUnstopAPI(params)
                .then((res) => {
                    record.loading = false;
                    if (parseFloat(res.code) === 0) {
                        record.switch = !record.switch;
                        record.status = 0;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    record.loading = false;
                });
        },
        // 设置后端所需要的所属部门格式
        setEditInfoFormDepartmentInnerSnFn(value, label) {
            this.planModalForm.department_inner_sn = value && value.length ? value[0] : '';
            this.planModalForm.department_name = label && label.length ? label[0] : '';
        },
        // 顶部按钮的事件
        groupBtnFn(btnName) {
            this.btnName = btnName;
            if (!this.checkSecltedTrFn()) return;
            switch (btnName) {
                case 'add': // 新增
                    this.planModal = {
                        title: `${this.$route.meta.title} - 新增`,
                        alert: true,
                        loading: false
                    };
                    this.planModalForm.department_inner_sn = JSON.parse(
                        localStorage.getItem('userInfo')
                    ).Departments[0].Department_inner_sn;
                    this.planModalForm.department_name = JSON.parse(
                        localStorage.getItem('userInfo')
                    ).Departments[0].Name;
                    this.currentPerformTrObj = {};
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
                    this.checkPlanListFn();
                    break;
                case 'stop': // 终止
                    this.planStopAPIFn();
                    break;
            }
        },
        checkPlanListFn() {
            const selectRecords = this.$refs.xPerformTable.getSelectRecords();
            if (selectRecords.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noCheckbox,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (selectRecords.every((item) => parseInt(item.status) > 1)) {
                this.$warning({
                    title: '操作提示',
                    content: '只能删除【草稿和正式】状态的采购计划。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            this.planDelAPIFn();
        },
        // 开发临时函数
        developmentFn() {
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
        checkSecltedTrFn() {
            let flag = true;
            this.resetPlanModalFn();

            if (this.btnName === 'add' || this.btnName === 'edit') {
                this.orgDeptTopTreeAPIFn();
            }

            if (this.tabsActiveKey === this.systemTypes[0] && Object.keys(this.currentPerformTrObj).length <= 0) {
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
            } else if (this.tabsActiveKey === this.systemTypes[1] && Object.keys(this.currentPastTrObj).length <= 0) {
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

            if (!flag) return flag;

            switch (this.btnName) {
                case 'build':
                case 'stop':
                    flag = this.checkSelectedRowKeysFn('list');
                    break;
                case 'remove': {
                    const refs = this.$refs.xPerformTable,
                        removeDtatas = refs.getSelectRecords();
                    for (let i = 0; i < removeDtatas.length; i++) {
                        const items = removeDtatas[i];
                        if (items.status > 1) {
                            this.$warning({
                                title: '操作提示',
                                content: '只能删除【草稿和正式】状态的采购计划。',
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            flag = false;
                            break;
                        }
                    }
                    break;
                }
                case 'edit':
                    if (this.tabsActiveKey === this.systemTypes[0]) {
                        this.planInfoAPIFn(this.currentPerformTrObj.ppl_inner_sn);
                    } else if (this.tabsActiveKey === this.systemTypes[1]) {
                        this.planInfoAPIFn(this.currentPastTrObj.ppl_inner_sn);
                    }
                    break;
                case 'add': // 如果是过往栏，在新增完成后直接切回到待执行栏
                    break;
            }
            return flag;
        },
        // 判断列表中表单是否有勾选的列表
        checkSelectedRowKeysFn(btnName) {
            let flag = true;
            if (btnName === 'list') {
                if (this.$refs.xPerformTable.getSelectRecords().length <= 0) {
                    this.$warning({
                        title: '操作提示',
                        content: this.$Utils.tips().noCheckbox,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    flag = false;
                }
            } else if (btnName === 'detail') {
                if (this.$refs.xTablePlanModalInfo.getSelectRecords().length <= 0) {
                    this.$warning({
                        title: '操作提示',
                        content: this.$Utils.tips().noCheckbox,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    flag = false;
                }
            }

            return flag;
        },
        // tabs切换选项卡
        changeActiveFn(activeKey) {
            this.tabsActiveKey = activeKey;
            this.searchTables.status = undefined;
            if (!this.isTabsActiveKey) {
                this.isTabsActiveKey = true; // 解决tab切换不刷新的问题
                this.planHistoryListAPIFn();
            }
        },
        // 表格的高亮样式显示
        rowPerformClassFn(record) {
            if (record.key === this.currentPerformTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        rowPastClassFn(record) {
            if (record.key === this.currentPastTrObj.key) {
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
        rowClassSendPlanOrderModalFn(record) {
            if (record.index === this.currentTrSendPlanOrderModalObj.index) {
                return 'active-tr';
            }
            return '';
        },
        cellClickFn({ row }) {
            this.currentPerformTrObj = row;
        },
        cellDblclickFn({ row }) {
            this.currentPerformTrObj = row;
            this.groupBtnFn('edit');
        },
        cellClickPastFn({ row }) {
            this.currentPastTrObj = row;
        },
        cellDblclickPastFn({ row }) {
            this.currentPastTrObj = row;
            this.groupBtnFn('edit');
        },
        selectChangeFn({ row }) {
            this.$refs.xPerformTable.setCurrentRow(row);
        },
        customRowSendPlanOrderModalFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentTrSendPlanOrderModalObj = record;
                    },
                    dblclick: () => {
                        this.currentTrSendPlanOrderModalObj = record;
                    }
                }
            };
        },
        customPastRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentPastTrObj = record;
                    },
                    dblclick: () => {
                        this.currentPastTrObj = record;
                        this.groupBtnFn('edit');
                    }
                }
            };
        },
        cellSendPlanOrderClickFn({ row }) {
            this.currentTrSendPlanOrderModalObj = row;
        },
        cellSendPlanOrderDblclickFn({ row }) {
            this.currentTrSendPlanOrderModalObj = row;
        },
        cellAddInfoClickFn({ row }) {
            this.currentTrPlanModalObj = row;
        },
        cellAddInfoDblclickFn({ row }, e) {
            this.currentTrPlanModalObj = row;
            if (this.isEditDisabled) return;
            this.dblclickTableFn(e);
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
        // 右上角刷新按钮
        refreshPageFn() {
            if (this.tabsActiveKey === this.systemTypes[0]) {
                this.$refs.xPerformTable.clearSelection();
                this.performPagination = {
                    ...this.performPagination,
                    loading: true,
                    total: 10,
                    current: 1,
                    // pageSize: 10,
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                };
                this.planUnexecutedListAPIFn();
            } else if (this.tabsActiveKey === this.systemTypes[1]) {
                this.pastPagination = {
                    ...this.pastPagination,
                    loading: true,
                    total: 10,
                    current: 1,
                    // pageSize: 10,
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                };
                this.planHistoryListAPIFn();
            }
        },
        // 页码改变的回调函数
        changePerformPaginationFn({ currentPage, pageSize }) {
            this.performPagination.current = currentPage;
            this.performPagination.pageSize = pageSize;
            this.performPagination.loading = true;
            this.planUnexecutedListAPIFn();
        },
        // 页码改变的回调函数
        changePastPaginationFn({ currentPage, pageSize }) {
            this.pastPagination.current = currentPage;
            this.pastPagination.pageSize = pageSize;
            this.pastPagination.loading = true;
            this.planHistoryListAPIFn();
        },
        // 双击此处添加产品的方法
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
                oldAddDatas = [...ven, ...dea],
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
            let timer = setTimeout(() => {
                this.planModalPagination.loading = false;
                clearTimeout(timer);
                timer = null;
            }, 200);
        },
        // 采购计划单表格的操作
        planModalActionBtnFn(e, btnName, record) {
            switch (btnName) {
                // 删除
                case 'remove': {
                    const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo;
                    xTablePlanModalInfo.remove(record);
                    this.planModalDatas = xTablePlanModalInfo.getTableData().tableData;
                    xTablePlanModalInfo.setCurrentRow(this.planModalDatas[0]);
                    this.dblclickAddProductFn();
                    this.planModalPagination.loading = false;
                    // this.planModalPagination.loading = true
                    // if (typeof record.sort_number !== 'undefined') { // 说明存在
                    //     this.planDetailDelAPIFn(record, index)
                    // } else { // 临时添加
                    //     const xTablePlanModalInfo = this.$refs.xTablePlanModalInfo
                    //     xTablePlanModalInfo.remove(record)
                    //     this.planModalDatas = xTablePlanModalInfo.getTableData().tableData
                    //     xTablePlanModalInfo.setCurrentRow(this.planModalDatas[0])
                    //     this.dblclickAddProductFn()
                    //     this.planModalPagination.loading = false
                    // }
                    break;
                }
                case 'stop': // 终止
                    record.loading = true;
                    record.switch ? this.planDetailStopAPIFn(record) : this.planDetailUnstopAPIFn(record);
                    break;
            }
            e.stopPropagation();
        },
        // 采购计划单表格底部的按钮操作
        planModalGroupBtnFn(btnName) {
            if (
                !(btnName === 'print' || btnName === 'exportExcel' || btnName === 'cancel') &&
                !this.checkPlanModalFormFn(btnName)
            )
                return;
            switch (btnName) {
                // 打印
                case 'print': {
                    // this.planModal.loadingPrint = true
                    let printContent = {
                        printDept: this.architectureAllTrees,
                        printData: this.planModalDatas,
                        printForm: this.planModalForm,
                        printTable: this.planModalStopColumns,
                        printType: 5, // 1 图片；2、耗材申领；3、出入库单；4、采购询价单；5、采购计划单
                        planOrderStatus: ['待处理', '已终止', '已生成采购订单'],
                        printTitle: '采购计划单'
                    };
                    printContent = JSON.stringify(printContent);
                    window.localStorage.printContent = printContent;
                    let routeUrl = this.$router.resolve({
                        path: 'print',
                        name: 'print'
                    });
                    window.open(routeUrl.href, '_blank');
                    break;
                }
                case 'exportExcel':
                    this.exportExcel();
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
                        this.planAddAPIFn(0); // 草稿
                    } else {
                        this.planEditAPIFn(0);
                    }
                    break;
                case 'save': // 保存
                    if (this.planModal.loadingSave) {
                        return;
                    }
                    this.planModal.loadingSave = true;
                    if (this.btnName === 'add') {
                        this.planAddAPIFn(1); // 保存正式
                    } else {
                        this.planEditAPIFn(1);
                    }
                    break;
                case 'audit': // 审核
                    this.planModal.loadingAudit = true;
                    this.planEditAPIFn(2);
                    break;
            }
        },
        // 导出
        exportExcel() {
            const form = this.planModalForm;
            const formData = {
                title: '采购计划单',
                desc: {
                    label: '摘要',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '单据编号',
                            value: form.ppl_sn
                        },
                        {
                            label: '部门',
                            value: form.department_name
                        },
                        {
                            label: '制单人',
                            value: form.creator_name
                        }
                    ],
                    [
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
            const data = this.planModalDatas.map((item) => {
                return {
                    ...item,
                    status: this.$options.filters['formatStatus'](item.status, 'planOrder'), // 状态名称
                    unit_inner_sn: item.unit_name // 包装单位名称
                };
            });
            this.$Utils.exportExcel(this.planModalStopColumns, data, [], formData);
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
            // 判断表格主体的数量 - 包装单位
            if (!this.planModalDatas.every((item) => item.quantity > 0 && item.unit_inner_sn.length)) {
                this.$warning({
                    title: '操作提示',
                    content: '请填写数量和包装单位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            } else {
                if (flag && this.btnName === 'edit' && this.checkEditInfoFormObjFn()) {
                    // 校验产品列表有没有改过
                    flag = true;
                }
                if (flag) {
                    // 产品只能出现唯一
                    let isRepeatObj = {};
                    for (let i = 0; i < this.planModalDatas.length; i++) {
                        const item = this.planModalDatas[i],
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
                }
                if (flag) {
                    this.detailList = this.planModalDatas.map((item, index) => {
                        return {
                            vendor_inner_sn: item.product_vendor_inner_sn || item.vendor_inner_sn,
                            vendor_name: item.product_vendor_name || item.vendor_name,
                            product_inner_sn: item.product_inner_sn,
                            product_name: item.product_name,
                            specification_inner_sn: item.specification_inner_sn,
                            specification: item.specification,
                            unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                            unit_name: item.unit_name,
                            sort_number: index,
                            quantity: item.quantity,
                            product_type: item.product_type,
                            batch_number: item.batch_number || ''
                        };
                    });
                    flag = true;
                }
            }
            return flag;
        },
        // 生成采购订单的按钮操作 => 最终操作【右下角生成按钮】
        sendPlanOrderListModalFn() {
            // 1. 前端格式校验
            let flag = true,
                datas = this.sendPlanOrderModalDatas,
                len = datas.length;

            for (let i = 0; i < len; i++) {
                const item = datas[i];
                if (item.supplierList.length) {
                    if (item.supplier_id.length <= 0) {
                        this.$warning({
                            title: '操作提示',
                            content: `请选择【NO.${i + 1} - ${item.product_name}】供应商`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                } else {
                    this.$warning({
                        title: '操作提示',
                        content: `请选择【NO.${i + 1} - ${item.product_name}】供应商`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    flag = false;
                    break;
                }
            }

            if (flag) {
                for (let i = 0; i < len; i++) {
                    const item = datas[i];
                    for (let j = 0; j < item.supplierList.length; j++) {
                        const cItem = item.supplierList[j];
                        if (item.supplier_id === cItem.value) {
                            const curQuantity = item.quantity / cItem.measure,
                                quantityNew = this.$Utils.checkFloatNumFn(curQuantity)
                                    ? parseInt(curQuantity) + 1
                                    : curQuantity;
                            if (typeof item.quantity_new === 'undefined' && flag) {
                                this.$warning({
                                    title: '操作提示',
                                    content: `【NO.${i + 1} - ${item.product_name}】的数量不能为空`,
                                    okText: '知道了',
                                    centered: true,
                                    maskClosable: false
                                });
                                flag = false;
                                break;
                            }
                            if (item.quantity_new <= 0 && flag) {
                                this.$warning({
                                    title: '操作提示',
                                    content: `【NO.${i + 1} - ${item.product_name}】的数量不能小于1`,
                                    okText: '知道了',
                                    centered: true,
                                    maskClosable: false
                                });
                                flag = false;
                                break;
                            }
                            if (item.quantity_new < quantityNew && flag) {
                                this.$warning({
                                    title: '操作提示',
                                    content: `【NO.${i + 1} - ${item.product_name}】的数量不能小于【${quantityNew}】`,
                                    okText: '知道了',
                                    centered: true,
                                    maskClosable: false
                                });
                                flag = false;
                                break;
                            }
                        }
                    }
                }
            }

            // 2. 重构后端所需数据
            if (flag) {
                this.sendPlanOrderModal.loading = true;
                let buildData = [];
                for (let i = 0; i < len; i++) {
                    const item = datas[i],
                        value = item.supplier_id.split('-');
                    buildData.push({
                        product_vendor_inner_sn: item.product_vendor_inner_sn,
                        product_vendor_name: item.product_vendor_name,
                        product_inner_sn: item.product_inner_sn,
                        product_name: item.product_name,
                        specification_inner_sn: item.specification_inner_sn,
                        specification: item.specification,
                        unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                        unit_name: item.unit_name,
                        quantity: item.quantity_new,
                        unit_price: item.unit_price,
                        supplierID: value[2],
                        supplierType: value[1],
                        customID: value[0],
                        product_type: item.product_type,
                        batch_number: item.batch_number || ''
                    });
                }
                this.sendPlanOrderModal.alert = false;
                this.sendPlanOrderPreviewModal.alert = true;
                this.sendPlanOrderPreviewBuildSpin = true;
                this.sendPlanOrderPreviewBuildDatas = [];
                this.planBuildAPIFn(buildData);
                this.$Utils.globalDragCenterFn('send-plan-order-preview');
            }
        },
        // 生成采购订单的按钮操作
        sendPlanOrderActionFn(btnName) {
            const isListFlag = btnName === 'list', // 'list' - 列表   'detail' - 明细
                result = isListFlag
                    ? this.checkReturnSelectedRowKeysFn(this.performDatas, btnName)
                    : this.checkReturnSelectedRowKeysFn(this.planModalDatas, btnName);
            if (result && result.flag) {
                !isListFlag && this.planModalGroupBtnFn('cancel');
                this.sendPlanOrderModal.alert = true;
                this.sendPlanOrderModalPagination.loading = true;
                this.$Utils.globalDragCenterFn('send-plan-order');
                isListFlag ? this.planCreateAPIFn(result.details) : this.planDetailCreateAPIFn(result.details);
            }
        },
        checkReturnSelectedRowKeysFn(list, btnName) {
            // 校验并返回所需要的后端格式
            if (!this.checkSelectedRowKeysFn(btnName)) return;
            let flag = true,
                details = [],
                getSelectRecords =
                    btnName === 'list'
                        ? this.$refs.xPerformTable.getSelectRecords()
                        : this.$refs.xTablePlanModalInfo.getSelectRecords();
            for (let i = 0; i < list.length; i++) {
                const item = list[i];
                for (let j = 0; j < getSelectRecords.length; j++) {
                    const cItem = getSelectRecords[j];
                    if (btnName === 'list' && item.key === cItem.key && parseFloat(item.status) !== 2 && flag) {
                        this.$warning({
                            title: '操作提示',
                            content: '只能【已审核】的状态才能生成采购订单',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (btnName === 'detail' && item.key === cItem.key && parseFloat(item.status) !== 0 && flag) {
                        this.$warning({
                            title: '操作提示',
                            content: '只能【已审核 - 待处理】的状态才能生成采购订单',
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
                for (let i = 0; i < list.length; i++) {
                    const item = list[i];
                    for (let j = 0; j < getSelectRecords.length; j++) {
                        const cItem = getSelectRecords[j];
                        if (btnName === 'list' && item.key === cItem.key) {
                            details.push({
                                org_id: item.org_id,
                                ppl_inner_sn: item.ppl_inner_sn
                            });
                        }
                        if (btnName === 'detail' && item.key === cItem.key) {
                            details.push({
                                org_id: item.org_id,
                                ppl_inner_sn: item.ppl_inner_sn,
                                detail_inner_sn: item.detail_inner_sn,
                                product_type: item.product_type
                            });
                        }
                    }
                }
            }
            return {
                flag,
                details
            };
        },
        // 供应商列表选择事件
        clickSelectSupplierListFn(record) {
            this.recordSupplierList = record;
        },
        selectSelectSupplierListFn(value, node) {
            for (let i = 0; i < this.recordSupplierList.supplierList.length; i++) {
                const item = this.recordSupplierList.supplierList[i];
                if (item.value === value) {
                    this.recordSupplierList.supplier_id = value;
                    this.recordSupplierList.supplier_name = node.label;
                    const sItemapFirst = item;
                    this.recordSupplierList.unitList = [
                        { value: sItemapFirst.unit_inner_sn, label: sItemapFirst.unit_name }
                    ];
                    this.recordSupplierList.unit_inner_sn = [sItemapFirst.unit_inner_sn];
                    this.recordSupplierList.unit_name = sItemapFirst.unit_name;
                    this.recordSupplierList.unit_price = sItemapFirst.unit_price;
                    const curQuantity = this.recordSupplierList.quantity / sItemapFirst.measure;
                    this.recordSupplierList.quantity_new = this.$Utils.checkFloatNumFn(curQuantity)
                        ? parseInt(curQuantity) + 1
                        : curQuantity;
                    break;
                }
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
                loadingAudit: false
            };
        },
        // 重置采购计划单模态框
        resetPlanModalFn() {
            this.planModalDatas = [];
            this.detailList = [];
            this.editInfoFormNewObj = {};
            this.planModalSelectedRowKeys = [];
            this.currentTrPlanModalObj = {};
            this.editInfoFormDepartmentInnerSn = [];
            this.planModalForm = {
                audit_time: '',
                auditor_inner_sn: '',
                auditor_name: '',
                create_time: '',
                creator_inner_sn: JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn,
                creator_name: JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
                department_inner_sn: '',
                department_inner_sn_old: '',
                department_name: '',
                department_name_old: '',
                description: '',
                description_old: '',
                detailList: [],
                ppl_inner_sn: '',
                ppl_sn: '',
                ppl_sn_old: '',
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
                // 设置默认部门值为当前登陆人的第一个部门
                if (this.btnName === 'add' && parseFloat(this.$cookie.get('userSystemType')) === 1) {
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
                    this.planModalForm.department_inner_sn = intersection[0].department_inner_sn;
                    this.planModalForm.department_name = intersection[0].label;
                }
            }
        },
        // 包装单位区域
        clickUnitListFn(record) {
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
            this.record = record;
        },
        changeUnitFn(value, selectedOptions) {
            this.record.unit_name = selectedOptions.length ? selectedOptions[selectedOptions.length - 1].name : '';
        },
        displayUnitRender({ labels }) {
            let label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
        }
    },
    filters: {
        formatStatus(value, name) {
            const planListStatus = ['草稿', '正式', '已审核', '已终止', '订单已生成'],
                planOrderStatus = ['待处理', '已终止', '已生成采购订单'];
            if (name === 'planList') {
                return planListStatus[value];
            } else if (name === 'planOrder') {
                return planOrderStatus[value];
            }
        }
    },
    computed: {
        scrollYPerformNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 214 : 154,
                pager = 52;
            return clientHeight - topHeight - pager;
        },
        scrollYPlanModalNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 400;
            return clientHeight - topHeight;
        },
        isEditDisabled() {
            // 弹框的内容是否可编辑
            let flag = true;
            if (this.btnName === 'add') {
                flag = false;
            } else if (
                (this.tabsActiveKey === this.systemTypes[0] && parseFloat(this.currentPerformTrObj.status) <= 1) ||
                parseFloat(this.planModalForm.status) <= 1
            ) {
                flag = false;
            }
            return flag;
        },
        isAddPlanModalDisabled() {
            // 内层的生成采购订单【采购计划单按钮】
            let flag = true;
            if (this.planModalDatas.length) {
                flag = this.planModalSelectedRowKeys.length <= 0;
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
                        disabled: record.status !== 0
                    }
                })
            };
        },
        planModalTextComputed() {
            // 采购计划单右下角的取消文字改变
            if (this.planModalForm.status === 0 || this.planModalForm.status === 1 || this.btnName === 'add') {
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
        computedSendPlanOrderAction() {
            let flag = true,
                curTrObj = Object.keys(this.currentPerformTrObj).length > 0;
            if (
                this.tabsActiveKey === this.systemTypes[0] &&
                curTrObj &&
                parseFloat(this.currentPerformTrObj.status) === 2
            ) {
                flag = false;
            }
            return flag;
        },
        orgName() {
            const employeeInfo = this.$cookie.get('EmployeeInfo');
            return (employeeInfo ? JSON.parse(employeeInfo).OrgName : '') || '';
        }
    },
    components: {
        addProduct
    },
    created() {
        this.planUnexecutedListAPIFn();
    }
};
</script>

<style lang="less">
.send-plan-order-preview {
    .ant-modal-body {
        padding: 0;
    }

    .modal-form-input ul {
        &.build-plan-parent {
            margin-bottom: 0;
            padding: 15px;

            .build-plan-parent-item {
                padding-bottom: 24px;
                margin-bottom: 24px;
                border-bottom: 4px dashed #ddd;
            }
        }
    }
}
</style>
