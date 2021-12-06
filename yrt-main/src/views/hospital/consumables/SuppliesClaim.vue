<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="groupBtnFn('add')" type="primary" size="small"> 新增 </a-button>
                            <a-button @click="groupBtnFn('edit')" type="primary" size="small"> 修改 </a-button>
                            <a-button @click="groupBtnFn('remove')" type="danger" size="small"> 删除 </a-button>
                        </li>
                        <li>
                            <a-button @click="groupBtnFn('copy')" type="primary" size="small"> 复制 </a-button>
                            <a-button @click="groupBtnFn('stop')" type="primary" size="small"> 申领终止 </a-button>
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
                            <a-input v-model="searchTables.voucher_sn" type="text" placeholder="输入申领单编号" />
                        </li>
                        <li style="width: 20%">
                            <span class="label">申领状态</span>
                            <a-select size="small" v-model="searchTables.status" placeholder="申领单状态">
                                <a-select-option :disabled="activeTabsKey === systemTypes[1]" value="0"
                                    >草稿</a-select-option
                                >
                                <a-select-option :disabled="activeTabsKey === systemTypes[1]" value="1"
                                    >申领</a-select-option
                                >
                                <a-select-option :disabled="activeTabsKey === systemTypes[0]" value="2"
                                    >全部发放</a-select-option
                                >
                                <a-select-option :disabled="activeTabsKey === systemTypes[0]" value="3"
                                    >申领终止</a-select-option
                                >
                            </a-select>
                        </li>
                        <li class="search-buttons-group">
                            <a-button @click="searchBtnFn" type="primary"> 搜索 </a-button>
                            <a-button @click="resetSearchResult" type="primary"> 重置 </a-button>
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
                <a-tab-pane tab="待申领" :key="systemTypes[0]" :forceRender="true">
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
                            <!--底部表格区域-->
                            <allotTable
                                :boxDownHeight="boxDownHeight"
                                :isDispatch="false"
                                orderStatus="waiting"
                                ref="allotTable"
                            />
                        </div>
                    </div>
                </a-tab-pane>
                <a-tab-pane tab="已申领" :key="systemTypes[1]" :forceRender="true">
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
                                    v-for="suppliesClaimItem in suppliesClaimColumns"
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
                            <allotTable :boxDownHeight="boxDownHeight" :isDispatch="false" ref="allotTableHistory" />
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
        <!-- 新增区域开始 -->
        <a-modal
            v-model="addInfoModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-add global-drag"
            :width="1300"
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
                        <a-button key="print" @click="addInfoModalGroupBtnFn('print')"> 打印 </a-button>
                        <a-button key="exportExcel" @click="exportExcel('xAddInfoModalTable')"> 导出Excel </a-button>
                        <a-button
                            key="copy"
                            :disabled="
                                btnName === 'add' ||
                                btnName === 'copy' ||
                                (!isEditDisabledFlag && btnModalName === 'copy')
                            "
                            @click="addInfoModalGroupBtnFn('copy')"
                        >
                            复制
                        </a-button>
                    </li>
                    <li>
                        <div v-if="!isEditDisabled">
                            <a-button key="close" @click="addInfoModalGroupBtnFn('cancel')"> 取消 </a-button>
                            <a-button
                                key="saveDraft"
                                type="primary"
                                :disabled="isEditDisabled"
                                :loading="addInfoModal.loadingSaveDraft"
                                @click="addInfoModalGroupBtnFn('saveDraft')"
                            >
                                存草稿
                            </a-button>
                            <a-button
                                key="submit"
                                type="primary"
                                :disabled="isEditDisabled"
                                :loading="addInfoModal.loadingApply"
                                @click="addInfoModalGroupBtnFn('apply')"
                            >
                                申领
                            </a-button>
                        </div>
                        <div v-else>
                            <a-button key="close" @click="addInfoModalGroupBtnFn('cancel')"> 关闭 </a-button>
                        </div>
                    </li>
                </ul>
            </template>
            <div ref="printData">
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
                                <span class="label"><em class="em red">*</em>申领科室</span>
                                <a-tree-select
                                    :disabled="isEditDisabled"
                                    treeNodeFilterProp="title"
                                    :showSearch="true"
                                    :treeData="architectureAllTrees"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    treeDefaultExpandAll
                                    v-model="editInfoFormDepartmentInnerSn"
                                    :allowClear="false"
                                    @change="setEditInfoFormDepartmentInnerSnFn"
                                    class="input"
                                    style="min-width: 150px"
                                >
                                </a-tree-select>
                            </div>
                            <div class="item">
                                <span class="label">仓库</span>
                                <a-select
                                    :disabled="isEditDisabled"
                                    :options="allWarehouseList"
                                    v-model="editInfoFormObj.warehouse_inner_sn"
                                    @change="editInfoFormWarehouseChangeFn"
                                    placeholder=""
                                    :allowClear="true"
                                    style="min-width: 150px"
                                />
                            </div>
                            <div class="item">
                                <span class="label">申领人</span>
                                <span class="input">{{ editInfoFormObj.proposer_name || userName }}</span>
                            </div>
                            <div class="item">
                                <span class="label">期望于</span>
                                <a-date-picker
                                    :disabled="isEditDisabled"
                                    allowClear
                                    v-model="expectDate"
                                    :format="datePickerFormat"
                                    @change="getexpectDate"
                                    class="input ant-calendar-picker-input"
                                />
                                <span class="before-issuing">之前发放</span>
                            </div>
                        </li>
                    </ul>
                    <div
                        ref="applyTable"
                        class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                        style="height: 336px"
                    >
                        <div class="top-add-product">
                            <a-tooltip placement="topLeft" title="添加产品">
                                <a-button
                                    :disabled="isEditDisabled"
                                    @click="dblclickTableFn"
                                    icon="plus"
                                    style="height: 24px; padding: 0 4px"
                                ></a-button>
                            </a-tooltip>
                        </div>
                        <vxe-grid
                            ref="xAddInfoModalTable"
                            size="small"
                            :data="addInfoDatas"
                            :loading="addInfoPagination.loading"
                            height="310"
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
                            show-footer
                        >
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
                                width="200"
                                :cell-render="{ name: 'product_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="specification_name"
                                title="规格型号"
                                width="150"
                                :cell-render="{ name: 'specification_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="vendor_name"
                                title="厂商/经销商"
                                :cell-render="{ name: 'vendor_name' }"
                            ></vxe-table-column>
                            <vxe-table-column field="batch_number" title="批号">
                                <template v-slot="{ row, seq }">
                                    <a-select
                                        :disabled="isEditDisabled"
                                        v-model="row.batch_number"
                                        type="text"
                                        allowClear
                                        :options="row.batchNumberList"
                                        placeholder=""
                                        @change="batchNumberChange(row, seq)"
                                    >
                                    </a-select>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="plan_quantity" title="申领数量">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :disabled="isEditDisabled"
                                        :min="1"
                                        v-model="row.plan_quantity"
                                        :formatter="
                                            (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')
                                        "
                                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <!--                            <vxe-table-column-->
                            <!--                                v-if="((isEditDisabledFlag && !(btnName === 'add' || btnName === 'copy')) || currentTrObj.status > 0) && !addInfoModal.isNewOrder && currentTrObj.status!==0"-->
                            <!--                                field="achieve_quantity"-->
                            <!--                                title="已发数量"-->
                            <!--                            >-->
                            <!--                            </vxe-table-column>-->
                            <vxe-table-column field="unit_name" title="包装单位">
                                <template v-slot="{ row }">
                                    <a-cascader
                                        :allowClear="false"
                                        :disabled="isEditDisabled"
                                        :options="row.unitList"
                                        :displayRender="displayRender"
                                        expand-trigger="hover"
                                        v-model="row.unit_inner_sn"
                                        @click.stop="clickUnitInnerSnFn(row)"
                                        @change="unitInnerSnFn"
                                        changeOnSelect
                                        placeholder=""
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                v-if="!addInfoModal.isNewOrder && currentTrObj.status !== 0"
                                field="status"
                                title="状态"
                            >
                                <template v-slot="{ row }">
                                    {{ row.status | filterApplyStatus }}
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                v-if="btnName === 'copy' || btnName === 'add' || currentTrObj.status !== 2"
                                field="action"
                                title="操作"
                                width="80"
                                :resizable="false"
                                fixed="right"
                            >
                                <template v-slot="{ row, seq }">
                                    <ul class="action-btn-list-wrap" style="position: relative">
                                        <li v-if="isEditDisabled">
                                            <div v-if="row.status === 0 || row.status === 2">
                                                <i
                                                    v-if="
                                                        activeTabsKey === systemTypes[1] ||
                                                        !(row.status === 0 || row.status === 2)
                                                    "
                                                    class="yrt yrt-zhongzhi-gaoduan disabled"
                                                >
                                                </i>
                                                <a-tooltip v-else class="tooltip" placement="top" title="终止">
                                                    <i
                                                        @click.stop="applyDetailGroupBtnFn('apply-stop', row, seq)"
                                                        class="yrt yrt-zhongzhi-gaoduan cancel"
                                                    ></i>
                                                </a-tooltip>
                                            </div>
                                        </li>
                                        <li v-else>
                                            <a-tooltip class="tooltip" placement="top" title="删除">
                                                <i
                                                    @click.stop="applyDetailGroupBtnFn('remove', row, seq)"
                                                    class="yrt yrt-shanchu-copy cancel"
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
                                <div v-else @dblclick.stop="dblclickTableFn" class="dblclick-wrap">
                                    <span>双击添加产品</span>
                                </div>
                            </template>
                        </vxe-grid>
                    </div>
                    <ul>
                        <li class="item" style="justify-content: space-between">
                            <div class="item-children">
                                <span class="label" style="width: auto">摘要</span>
                                <a-input
                                    :disabled="isEditDisabled"
                                    v-model="editInfoFormObj.description"
                                    placeholder=""
                                    class="input"
                                />
                            </div>
                            <div class="item-children" style="width: 28%">
                                <span class="label" style="width: auto">生成时间</span>
                                <span class="input">{{ editInfoFormObj.create_time || '-' }}</span>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </a-modal>
        <!-- 新增区域结束 -->
        <!-- 新增产品区域开始 -->
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
        <!-- 新增产品区域结束 -->
        <!-- 确认发放明细查看区域开始 -->
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
                        <a-button key="print" @click="applySendModalBtnFn('print')"> 打印 </a-button>
                        <a-button key="exportExcel" @click="exportExcel('xApplySendModalTable')"> 导出Excel </a-button>
                    </li>
                    <li>
                        <a-button key="close" @click="applySendModalBtnFn('cancel')">
                            {{ applySendInfoFormObj.status === 2 ? '关闭' : '取消' }}
                        </a-button>
                        <a-button
                            v-if="applySendInfoFormObj.status !== 2"
                            key="submit"
                            type="primary"
                            :loading="addInfoModal.loadingApply"
                            @click="hospitalGrantApplyConfirmAllAPIFn()"
                        >
                            <!--@click="applySendModalBtnFn('submit')">-->
                            全部确认领取
                        </a-button>
                    </li>
                </ul>
            </template>
            <div ref="printData">
                <div class="modal-form-input for-single">
                    <h6 class="title">
                        <span style="margin-right: 10px; font-size: 24px; color: #444">{{ orgName }}</span>
                        <span style="font-size: 24px; color: #444">发放单</span>
                    </h6>
                    <ul class="top-list">
                        <li>
                            <div
                                v-if="btnName !== 'add'"
                                class="item"
                                style="position: absolute; right: 10px; margin-top: -30px"
                            >
                                <span class="label">单据编号</span>
                                <a-input disabled v-model="applySendInfoFormObj.gv_sn" placeholder="" class="input" />
                            </div>
                            <div class="item">
                                <span class="label"><em class="em red">*</em>科室</span>
                                <a-tree-select
                                    disabled
                                    treeNodeFilterProp="title"
                                    :showSearch="true"
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
                            <div class="item">
                                <span class="label" style="width: 80px"><em class="em red">*</em>发放仓库</span>
                                <a-tree-select
                                    disabled
                                    treeNodeFilterProp="title"
                                    :showSearch="true"
                                    :treeData="warehouseAllTrees"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    treeDefaultExpandAll
                                    v-model="applySendInfoFormObj.warehouse_inner_sn"
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
                            @cell-click="cellClickApplySendModalFn"
                            @cell-dblclick="cellDblclickApplySendModalFn"
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
                                field="product_name"
                                title="产品"
                                width="240"
                                :cell-render="{ name: 'product_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="150"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="vendor_name"
                                title="厂商/经销商"
                                width="200"
                                :cell-render="{ name: 'vendor_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="batch_number"
                                title="批号"
                                width="200"
                                :cell-render="{ name: 'batch_number' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="grant_quantity"
                                title="批准发放数量"
                                width="120"
                                :cell-render="{ name: 'grant_quantity' }"
                            ></vxe-table-column>
                            <!--                            <vxe-table-column-->
                            <!--                                v-show="this.applySendInfoFormObj.status === 1"-->
                            <!--                                field="get_quantity"-->
                            <!--                                title="领取数量"-->
                            <!--                                width="100"-->
                            <!--                                :cell-render="{name: 'get_quantity'}"-->
                            <!--                            ></vxe-table-column>-->
                            <vxe-table-column field="get_quantity" title="领取数量" width="100">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        v-show="applySendInfoFormObj.status != 2"
                                        :min="0"
                                        v-model="row.get_quantity"
                                        class="input"
                                        style="width: 100%"
                                    />
                                    <span v-show="applySendInfoFormObj.status == 2">{{ row.get_quantity }}</span>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="unit_name"
                                title="包装单位"
                                width="80"
                                :cell-render="{ name: 'unit_name' }"
                            ></vxe-table-column>
                            <vxe-table-column field="action" title="操作" width="100">
                                <template v-slot="{ row }">
                                    <a-tooltip
                                        v-show="row.status !== 2"
                                        placement="top"
                                        @click="hospitalGrantApplyConfirmAPIFn(row)"
                                        title="确认领取"
                                    >
                                        <i
                                            class="yrt yrt-app-shenling cancel"
                                            style="margin-left: 14px; font-size: 18px"
                                        ></i>
                                    </a-tooltip>
                                    <!--                                <a-button-->
                                    <!--                                    v-show="row.status === 0 && applySendInfoFormObj.status != 2"-->
                                    <!--                                    type="primary"-->
                                    <!--                                    size="small"-->
                                    <!--                                    @click="hospitalGrantApplyConfirmAPIFn(row)">-->
                                    <!--                                    确认领取-->
                                    <!--                                </a-button>-->
                                </template>
                            </vxe-table-column>
                            <!--                            <vxe-table-column-->
                            <!--                                v-for="applySendModalItem in applySendModalColumns"-->
                            <!--                                :key="applySendModalItem.key"-->
                            <!--                                :width="applySendModalItem.width"-->
                            <!--                                :field="applySendModalItem.dataIndex"-->
                            <!--                                :title="applySendModalItem.title"></vxe-table-column>-->
                        </vxe-table>
                    </div>
                    <ul>
                        <li class="item">
                            <span class="label" style="width: auto">摘要</span>
                            <a-input
                                :disabled="isEditDisabled"
                                v-model="applySendInfoFormObj.description"
                                placeholder=""
                                class="input"
                            />
                        </li>
                        <li class="item">
                            <div class="item-children" style="min-width: 120px; width: auto">
                                <span class="label" style="width: auto">发放人</span>
                                <span>{{ applySendInfoFormObj.approver_name || '-' }}</span>
                            </div>
                            <div class="item-children" style="min-width: 120px; width: 260px">
                                <span class="label" style="width: auto">发放时间</span>
                                <span>{{ applySendInfoFormObj.grant_time || '-' }}</span>
                            </div>
                            <div class="item-children" style="min-width: 120px; width: auto">
                                <span class="label" style="width: auto">领取人</span>
                                <span>{{ receiverName || '-' }}</span>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </a-modal>
        <!-- 确认发放明细查看区域结束 -->
    </div>
</template>

<script>
import moment from 'moment';
import addProduct from '@/components/addProduct';
import allotTable from './Module/SuppliesClaimAllotTable';
import { mapState } from 'vuex';
import {
    hospitalApplyDetailStopAPI,
    hospitalApplyDetailDelAPI,
    platformUnitTree,
    platformUnitParents,
    dealerUnitTreeAPI,
    dealerUnitParentsAPI,
    orgDeptRelAPI,
    hospitalApplyInfoAPI,
    hospitalApplyEditAPI,
    hospitalApplyStopAPI,
    hospitalApplyDelAPI,
    hospitalApplyCopyAPI,
    hospitalApplyAddAPI,
    hospitalApplyListAPI,
    hospitalApplyHistoryListAPI,
    hospitalGrantIssuedDetailListAPI,
    hospitalGrantDetailListAPI,
    hospitalGrantConfirmAPI,
    hospitalGrantApplyConfirmAPI,
    hospitalApplyBatchNumberListAPI
} from '@/service/pageAjax';

export default {
    name: 'SuppliesClaim',
    data() {
        return {
            userName: JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
            boxTopHeight: 300,
            boxDownHeight: 300,
            systemTypes: ['waitApply', 'haveClaimed'], // 待申领, 已申领
            activeTabsKey: 'waitApply',
            isActiveTabsKey: false,
            receiverName: '', // 领取人
            suppliesClaimColumns: [
                {
                    title: '申领单编号',
                    dataIndex: 'voucher_sn',
                    key: 'voucher_sn',
                    width: '28%',
                    align: 'center'
                },
                {
                    title: '申领科室',
                    dataIndex: 'department_name',
                    key: 'department_name',
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
            suppliesClaimBotColumns: [
                {
                    title: '发放单编号',
                    dataIndex: 'gv_sn',
                    key: 'gv_sn',
                    width: '35%',
                    align: 'center'
                },
                {
                    title: '发放人',
                    dataIndex: 'approver_name',
                    key: 'approver_name',
                    width: '25%',
                    align: 'center'
                },
                {
                    title: '发放时间',
                    dataIndex: 'grant_time',
                    key: 'grant_time',
                    width: '25%',
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
            suppliesClaimBotDatas: [],
            currentTrBotObj: {},
            suppliesClaimBotPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            suppliesClaimHistoryBotColumns: [
                // 已出库状态 => 发放确认按钮
                {
                    title: '发放单编号',
                    dataIndex: 'gv_sn',
                    key: 'gv_sn',
                    width: '35%',
                    align: 'center'
                },
                {
                    title: '发放人',
                    dataIndex: 'approver_name',
                    key: 'approver_name',
                    width: '25%',
                    align: 'center'
                },
                {
                    title: '发放时间',
                    dataIndex: 'grant_time',
                    key: 'grant_time',
                    width: '25%',
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
            suppliesClaimHistoryBotDatas: [],
            currentTrHistoryBotObj: {},
            suppliesClaimHistoryBotPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            issuingStatus: ['可发放', '已出库', '已领取'], // 0-可发放 1-已出库 2-已领取
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
            auditStatus: ['草稿', '申领中', '全部发放', '申领终止'], // 医院领用单状态(status): 0-草稿 1-申领 2-部分发放 3-全部发放(自然终止) 4-申领终止(人为终止)
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            currentTrAddInfoObj: {},
            searchTables: {
                hospital_inner_sn: '',
                status: undefined,
                voucher_sn: '',
                create_time: '',
                expect_date: ''
            }, // 表格的搜索并向后端请求字段
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
            // 新增弹框的配置
            addInfoModal: {
                title: `${this.$route.meta.title} - 新增`,
                alert: false,
                loadingSaveDraft: false,
                loadingApply: false,
                isNewOrder: false
            },
            // 新增弹框的表格配置 【除了草稿状态才显示已发】
            addInfoColumns: [
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
                    width: '20%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification_name',
                    key: 'specification_name',
                    width: '16%',
                    align: 'center'
                },
                {
                    title: '厂商/经销商',
                    dataIndex: 'sys_type',
                    key: 'sys_type',
                    width: '12%',
                    align: 'center',
                    scopedSlots: { customRender: 'sys_type' }
                },
                {
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    align: 'center',
                    scopedSlots: { customRender: 'batch_number' }
                },
                {
                    title: '申领数量',
                    dataIndex: 'plan_quantity',
                    key: 'plan_quantity',
                    width: '12%',
                    align: 'center',
                    scopedSlots: { customRender: 'plan_quantity' }
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
                    title: '状态',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    width: '10%',
                    align: 'center'
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    key: 'action',
                    width: '10%',
                    align: 'center',
                    scopedSlots: { customRender: 'action' }
                }
            ],
            // 除了草稿状态才显示已发
            addInfoDraftStatusColumns: [
                {
                    title: 'No.',
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
                    dataIndex: 'specification_name',
                    key: 'specification_name',
                    width: '17%',
                    align: 'center'
                },
                {
                    title: '厂商/经销商',
                    dataIndex: 'vendor_name',
                    key: 'vendor_name',
                    width: '17%',
                    align: 'center'
                },
                {
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    align: 'center',
                    scopedSlots: { customRender: 'batch_number' }
                },
                {
                    title: '申领数量',
                    dataIndex: 'plan_quantity',
                    key: 'plan_quantity',
                    width: '14%',
                    align: 'center',
                    scopedSlots: { customRender: 'plan_quantity' }
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
                    title: '操作',
                    dataIndex: 'action',
                    key: 'action',
                    width: '10%',
                    align: 'center',
                    scopedSlots: { customRender: 'action' }
                }
            ],
            addInfoDraftColumns: [
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
                    width: '20%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification_name',
                    key: 'specification_name',
                    width: '13%',
                    align: 'center'
                },
                {
                    title: '厂商/经销商',
                    dataIndex: 'vendor_name',
                    key: 'vendor_name',
                    width: '12%',
                    align: 'center'
                },
                {
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    align: 'center',
                    scopedSlots: { customRender: 'batch_number' }
                },
                {
                    title: '申领数量',
                    dataIndex: 'plan_quantity',
                    key: 'plan_quantity',
                    width: '11%',
                    align: 'center',
                    scopedSlots: { customRender: 'plan_quantity' }
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
                    title: '状态',
                    dataIndex: 'status',
                    key: 'status',
                    width: '10%',
                    align: 'center'
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
            addInfoDatas: [], // 实际后端数据
            addInfoPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            applySendModalColumns: [
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    width: '200',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    width: '200',
                    align: 'center'
                },
                {
                    title: '厂商/经销商',
                    dataIndex: 'vendor_name',
                    key: 'vendor_name',
                    width: '150',
                    align: 'center'
                },
                {
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    width: '100',
                    align: 'center'
                },
                {
                    title: '批准发放数量',
                    dataIndex: 'grant_quantity',
                    key: 'grant_quantity',
                    align: 'center'
                },
                {
                    title: '领取数量',
                    dataIndex: 'get_quantity',
                    key: 'get_quantity',
                    align: 'center'
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
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
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            applySendStatus: ['可发放', '已出库', '已领取'],
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
            editInfoFormObj: {
                // 修改信息的模态框值 - 后端返回回来的
                create_time: '',
                department_inner_sn: '',
                department_name: '',
                warehouse_inner_sn: '',
                warehouse_inner_sn_name: '',
                description: '',
                expect_date: '',
                hospital_inner_sn: '',
                hospital_name: '',
                proposer_inner_sn: '',
                proposer_name: '',
                status: '',
                voucher_inner_sn: '',
                voucher_sn: ''
            },
            editInfoFormOldObj: {}, // 修改信息的模态框值 - 后端返回回来的旧值
            editInfoFormNewObj: {}, // 修改信息的模态框值 - 前端根据页面修改的值放进去 【没有修改的不要向后端传递】
            rangePickerVal: moment[''], // 申请时间的默认赋值
            editInfoFormDepartmentInnerSn: [], // 所属部门
            architectureAllTrees: [], // 所属部门的信息列表
            warehouseAllTrees: [], // 发放仓库的信息列表
            btnName: '', // 当前点击的是哪个顶部按钮
            btnModalName: '',
            datePickerFormat: 'YYYY-MM-DD', // 期望时间的格式
            addProductMd: {
                title: `${this.$route.meta.title} - 新增 - 添加产品`,
                alert: false,
                loading: false
            },
            partB: {
                // 新增产品窗口给子组件传递值
                id: this.$cookie.get('userbelong'),
                modalType: 1 // 1 - 合同内的产品
            },
            unitList: [], // 包装单位
            details: [], // 产品列表 【传给后端的】
            expectDate: '', // 期望时间
            isEditDisabledFlag: true,
            recordUnitInnerSn: '',
            allWarehouseList: []
        };
    },
    methods: {
        init() {
            if (this.$route.query.voucher_sn) {
                this.searchTables.voucher_sn = this.$route.query.voucher_sn;
            }
            this.initTablesPaginationFn();
            this.hospitalApplyListAPIFn();
        },
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
        batchNumberChange() {
            if (this.$refs.xAddInfoModalTable) {
                this.$refs.xAddInfoModalTable.updateData();
            }
        },
        async hospitalApplyBatchNumberListAPIFn(record, index) {
            let aid = '';
            if (record.vendor_inner_sn !== null) {
                aid = record.vendor_inner_sn;
            } else {
                aid = record.dealer_inner_sn;
            }
            await hospitalApplyBatchNumberListAPI(
                record.hospital_inner_sn,
                aid,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.product_type
            )
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        list.forEach((item) => {
                            let label = item;
                            if (item === '') {
                                label = '空批号';
                            }
                            this.addInfoDatas[index].batchNumberList.push({ 'label': label, 'value': item });
                        });
                        if (this.$refs.xAddInfoModalTable) {
                            this.$refs.xAddInfoModalTable.updateData();
                        }
                    }
                })
                .catch(() => {});
        },
        async hospitalGrantApplyConfirmAPIFn(row) {
            let ainfo = JSON.parse(this.$cookie.get('EmployeeInfo'));
            let dataList = [
                {
                    'detail_inner_sn': row.detail_inner_sn,
                    'get_quantity': row.get_quantity,
                    'product_type': row.product_type,
                    'receiver_inner_sn': ainfo.Employee_inner_sn, // 领取人内部编号
                    'receiver_name': ainfo.Name // 领取人姓名
                }
            ];
            let params = {
                'gid': row.gv_inner_sn,
                'oid': this.$cookie.get('userbelong'),
                'details': dataList
            };

            await hospitalGrantApplyConfirmAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        // const {rows, total} = res
                        // hospitalGrantApplyConfirmAPI
                        this.$warning({
                            title: '操作提示',
                            content: `操作成功`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        this.applySendModalDatas.forEach((item) => {
                            if (item._XID === row._XID) {
                                item.status = 2;
                            }
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {});
        },
        async hospitalGrantApplyConfirmAllAPIFn() {
            let ainfo = JSON.parse(this.$cookie.get('EmployeeInfo'));
            let dataList = [];
            this.applySendModalDatas.forEach((item) => {
                let obj = {
                    'detail_inner_sn': item.detail_inner_sn,
                    'get_quantity': item.get_quantity,
                    'product_type': item.product_type,
                    'receiver_inner_sn': ainfo.Employee_inner_sn,
                    'receiver_name': ainfo.Name
                };
                dataList.push(obj);
            });
            let params = {
                'gid': this.applySendInfoFormObj.gv_inner_sn,
                'oid': this.$cookie.get('userbelong'),
                'details': dataList
            };
            await hospitalGrantApplyConfirmAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.applySendModal.alert = false;
                        if (this.activeTabsKey === this.systemTypes[0]) {
                            this.suppliesClaimBotDatas.forEach((item, index) => {
                                if (item._XID === this.currentTrBotObj._XID) {
                                    this.suppliesClaimBotDatas.splice(index, 1);
                                }
                            });
                        } else {
                            this.suppliesClaimHistoryBotDatas.forEach((item, index) => {
                                if (item._XID === this.currentTrHistoryBotObj._XID) {
                                    this.suppliesClaimHistoryBotDatas.splice(index, 1);
                                }
                            });
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {});
        },
        async orgDeptRelAPIFn() {
            await orgDeptRelAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'deptTopTree');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【获取待申领材料】申领列表(分页,创建时间倒序)
        async hospitalApplyListAPIFn(btnNameFlag, curItem) {
            // 判断参数有没有， 没有直接不要传给后端
            let params = {
                hospital_inner_sn: this.$cookie.get('userbelong'),
                pageNum: this.suppliesClaimPagination.current,
                pageSize: this.suppliesClaimPagination.pageSize
            };
            // 判断是否是高级搜索
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                const status = this.searchTables.status;
                if (status !== '' && status !== undefined) {
                    params.status = parseInt(this.searchTables.status);
                }

                if (this.searchTables.create_time) {
                    params.create_time = this.searchTables.create_time;
                }

                if (this.searchTables.expect_date) {
                    params.expect_date = this.searchTables.expect_date;
                }
            }

            if (this.searchTables.voucher_sn) {
                params.voucher_sn = this.searchTables.voucher_sn;
            }

            await hospitalApplyListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.suppliesClaimDatas = rows.map((item) => {
                            item.key = item.hospital_inner_sn + '' + item.proposer_inner_sn + item.voucher_inner_sn;
                            item.audit_status_text = this.auditStatus[parseFloat(item.status)];
                            item.activeClass = '';
                            item.status = parseFloat(item.status);
                            if (curItem && item.key === curItem.key) {
                                this.currentTrObj = item;
                                this.$refs.xSuppliesClaimTable.setCurrentRow(item);
                            }
                            return item;
                        });
                        if (btnNameFlag === 'add') {
                            this.currentTrObj = this.suppliesClaimDatas[this.suppliesClaimDatas.length - 1];
                            this.$refs.xSuppliesClaimTable.setCurrentRow(this.currentTrObj);
                        } else {
                            if (!curItem) {
                                this.currentTrObj = this.suppliesClaimDatas.length ? this.suppliesClaimDatas[0] : {};
                                this.$refs.xSuppliesClaimTable.setCurrentRow(this.currentTrObj);
                            }
                        }

                        if (this.suppliesClaimDatas.length) {
                            this.hospitalGrantIssuedDetailListAPIFn(this.systemTypes[0], this.currentTrObj);
                        } else {
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
        // 【获取已申领材料】申领列表(分页,创建时间倒序)
        async hospitalApplyHistoryListAPIFn(btnNameFlag, curItem) {
            // 判断参数有没有， 没有直接不要传给后端
            let params = {
                hospital_inner_sn: this.$cookie.get('userbelong'),
                pageNum: this.suppliesClaimHistoryPagination.current,
                pageSize: this.suppliesClaimHistoryPagination.pageSize
            };
            // 判断是否是高级搜索
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                if (this.searchTables.status !== '') {
                    params.status = this.searchTables.status;
                }

                if (this.searchTables.create_time) {
                    params.registrant = this.searchTables.create_time;
                }

                if (this.searchTables.expect_date) {
                    params.registrant = this.searchTables.expect_date;
                }
            }

            if (this.searchTables.voucher_sn) {
                params.voucher_sn = this.searchTables.voucher_sn;
            }

            await hospitalApplyHistoryListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.suppliesClaimHistoryDatas = rows.map((item) => {
                            item.key = item.hospital_inner_sn + '' + item.voucher_inner_sn;
                            item.audit_status_text = this.auditStatus[parseFloat(item.status)];
                            item.activeClass = '';
                            item.status = parseFloat(item.status);
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
                            this.hospitalGrantIssuedDetailListAPIFn(this.systemTypes[1], this.currentTrHistoryObj);
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
                        this.suppliesClaimHistoryPagination.loading = false;
                    }
                })
                .catch(() => {
                    this.suppliesClaimHistoryPagination.loading = false;
                });
        },
        // 【获取】获取发放单列表 【耗材申领的待申领明细和耗材发放的已发放明细】
        async hospitalGrantIssuedDetailListAPIFn(tabActiveName, record) {
            if (tabActiveName === this.systemTypes[0]) {
                this.suppliesClaimBotPagination.loading = true;
                this.$refs.allotTable.initPage(record);
                return;
            } else if (tabActiveName === this.systemTypes[1]) {
                this.suppliesClaimHistoryBotPagination.loading = true;
                this.$refs.allotTableHistory.initPage(record);
                return;
            }
            this.receiverName = '';
            this.suppliesClaimBotDatas = [];
            this.suppliesClaimHistoryBotDatas = [];
            await hospitalGrantIssuedDetailListAPI(record.hospital_inner_sn, record.voucher_inner_sn)
                .then((res) => {
                    if (tabActiveName === this.systemTypes[0]) {
                        this.suppliesClaimBotPagination.loading = false;
                    } else if (tabActiveName === this.systemTypes[1]) {
                        this.suppliesClaimHistoryBotPagination.loading = false;
                    }
                    if (parseFloat(res.code) === 0) {
                        const { list } = res,
                            newList = list.map((item) => {
                                item.key = item.hospital_inner_sn + '' + item.gv_inner_sn;
                                item.audit_status_text = this.applySendStatus[parseFloat(item.status)];
                                item.activeClass = '';
                                return item;
                            });
                        if (list.length >= 0) {
                            this.receiverName = list[0].receiver_name;
                        }
                        if (tabActiveName === this.systemTypes[0]) {
                            this.suppliesClaimBotDatas = newList;
                            if (this.suppliesClaimBotDatas.length) {
                                this.currentTrBotObj = this.suppliesClaimBotDatas[0];
                            } else {
                                this.currentTrBotObj = {};
                            }
                            this.$refs.xSuppliesClaimBotTable.setCurrentRow(this.currentTrBotObj);
                        } else if (tabActiveName === this.systemTypes[1]) {
                            this.suppliesClaimHistoryBotDatas = newList;
                            if (this.suppliesClaimHistoryBotDatas.length) {
                                this.currentTrHistoryBotObj = this.suppliesClaimHistoryBotDatas[0];
                            } else {
                                this.currentTrHistoryBotObj = {};
                            }
                            this.$refs.xSuppliesClaimHistoryBotTable.setCurrentRow(this.currentTrHistoryBotObj);
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    if (tabActiveName === this.systemTypes[0]) {
                        this.suppliesClaimBotPagination.loading = false;
                    } else if (tabActiveName === this.systemTypes[1]) {
                        this.suppliesClaimHistoryBotPagination.loading = false;
                    }
                });
        },
        // 【获取】根据发放单ID获取发放单明细列表
        async hospitalGrantDetailListAPIFn(record) {
            this.applySendModalPagination.loading = true;
            await hospitalGrantDetailListAPI(record.hospital_inner_sn, record.gv_inner_sn)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.applySendModalDatas = list.map((item) => {
                            if (this.applySendInfoFormObj.status !== 2) {
                                item.get_quantity = item.grant_quantity;
                            }
                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random()
                            };
                        });
                        this.warehouseAllTrees = [];
                        if (list.length >= 0) {
                            let obj = { 'label': list[0].warehouse_name, 'value': list[0].warehouse_inner_sn };
                            this.warehouseAllTrees.push(obj);
                        }
                        if (this.applySendModalDatas.length) {
                            this.currentApplySendModalTrObj = this.applySendModalDatas[0];
                            this.$refs.xApplySendModalTable.setCurrentRow(this.currentApplySendModalTrObj);
                        } else {
                            this.currentApplySendModalTrObj = {};
                        }
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
        // 【复制】材料申领信息
        async hospitalApplyCopyAPIFn(aid) {
            await hospitalApplyCopyAPI(aid)
                .then((res) => {
                    this.addInfoPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        this.isEditDisabledFlag = false;
                        const { info } = res,
                            details = info.details ? info.details : [];
                        info.create_time = ''; // 解决复制的时候，去除生成时间
                        this.addInfoDatas = details.map((item, index) => {
                            return {
                                ...item,
                                index,
                                key: Math.random(),
                                activeClass: '',
                                specification_name: item.specification || item.specification_name,
                                vendor_name: item.vendor_name || item.dealer_name,
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                                unitList: item.unit_name
                                    ? [{ value: item.unit_inner_sn, label: item.unit_name, title: item.unit_name }]
                                    : [],
                                unit_name: item.unit_name || '',
                                status: parseFloat(item.status),
                                batchNumberList: []
                            };
                        });
                        this.currentTrAddInfoObj = this.addInfoDatas.length ? this.addInfoDatas[0] : {};
                        this.editInfoFormObj = { ...info };
                        this.editInfoFormOldObj = { ...info };
                        this.expectDate = this.editInfoFormObj.expect_date
                            ? moment(this.editInfoFormObj.expect_date)
                            : '';
                        this.editInfoFormDepartmentInnerSn = this.editInfoFormObj.department_inner_sn
                            ? [this.editInfoFormObj.department_inner_sn]
                            : [];
                        this.$refs.xAddInfoModalTable.setCurrentRow(this.currentTrAddInfoObj);
                        this.dblclickAddProductFn();
                        this.btnName = 'add';
                        this.addInfoDatas.forEach((item, index) => {
                            if (item.batchNumberList.length === 0) {
                                this.hospitalApplyBatchNumberListAPIFn(item, index);
                            }
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addInfoPagination.loading = false;
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
                                status: parseFloat(item.status),
                                batchNumberList: []
                            };
                        });
                        this.addInfoDatas.forEach((item, index) => {
                            this.hospitalApplyBatchNumberListAPIFn(item, index);
                        });
                        this.currentTrAddInfoObj = this.addInfoDatas.length ? this.addInfoDatas[0] : {};
                        this.editInfoFormObj = { ...info };
                        this.editInfoFormOldObj = { ...info };
                        this.expectDate = this.editInfoFormObj.expect_date
                            ? moment(this.editInfoFormObj.expect_date)
                            : '';
                        let isMatch = false;
                        JSON.parse(localStorage.getItem('userInfo')).Departments.forEach((v) => {
                            if (v.Department_inner_sn === this.editInfoFormObj.department_inner_sn) {
                                isMatch = true;
                            }
                        });
                        this.editInfoFormDepartmentInnerSn = isMatch
                            ? this.editInfoFormObj.department_inner_sn
                            : this.isEditDisabled
                            ? this.editInfoFormObj.department_name
                            : '';
                        // this.editInfoFormDepartmentInnerSn = this.editInfoFormObj.department_inner_sn ? [this.editInfoFormObj.department_inner_sn] : []
                        this.$refs.xAddInfoModalTable.setCurrentRow(this.currentTrAddInfoObj);
                        // 重载表格配置，以适应内容的更改
                        this.$refs['xAddInfoModalTable'].refreshColumn();
                        this.dblclickAddProductFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addInfoPagination.loading = false;
                });
        },
        // 【添加】材料申领信息
        async hospitalApplyAddAPIFn(status) {
            // 状态: 0-草稿 1-申领  医院申领单产品明细状态(status): `0-申领中 1-已完全发放 2-已计划采购 3-已终止申领`
            let params = {
                department_inner_sn: this.editInfoFormObj.department_inner_sn || '',
                department_name: this.editInfoFormObj.department_name || '',
                warehouse_inner_sn: this.editInfoFormObj.warehouse_inner_sn || '',
                warehouse_inner_sn_name: this.editInfoFormObj.warehouse_inner_sn_name || '',
                details: this.details,
                hospital_inner_sn: this.$cookie.get('userbelong'),
                status,
                voucher_sn: ''
            };

            if (this.editInfoFormObj.description) {
                params.description = this.$Utils.trim(this.editInfoFormObj.description);
            }

            if (this.editInfoFormObj.expect_date) {
                params.expect_date = this.editInfoFormObj.expect_date;
            }

            await hospitalApplyAddAPI(params)
                .then((res) => {
                    status === 0
                        ? (this.addInfoModal.loadingSaveDraft = false)
                        : (this.addInfoModal.loadingApply = false);
                    if (parseFloat(res.code) === 0) {
                        this.suppliesClaimPagination.current = 1;
                        const { info } = res;
                        info.audit_status_text = this.auditStatus[parseFloat(info.status)];
                        if (this.activeTabsKey === this.systemTypes[1]) {
                            this.changeTabsFn(this.systemTypes[0]);
                        }
                        this.suppliesClaimPagination.total++;
                        const xSuppliesClaimTable = this.$refs.xSuppliesClaimTable,
                            recordItem = xSuppliesClaimTable.getCurrentRow();

                        let insertAtRow = !recordItem || !Object.keys(recordItem).length ? null : recordItem;

                        xSuppliesClaimTable.insertAt(info, insertAtRow).then(({ row }) => {
                            this.suppliesClaimDatas = xSuppliesClaimTable.getTableData().tableData;
                            xSuppliesClaimTable.setCurrentRow(row);
                            if (this.suppliesClaimDatas.length > this.suppliesClaimPagination.pageSize) {
                                this.suppliesClaimDatas.splice(this.suppliesClaimDatas.length - 1, 1);
                            }
                        });
                        this.addInfoModal.alert = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    status === 0
                        ? (this.addInfoModal.loadingSaveDraft = false)
                        : (this.addInfoModal.loadingApply = false);
                });
        },
        // 【终止】申领材料-明细
        async hospitalApplyDetailStopAPIFn(record) {
            this.addInfoPagination.loading = true;
            let params = {
                hospital_inner_sn: record.hospital_inner_sn,
                product_type: record.product_type
            };
            if (this.editInfoFormObj.voucher_inner_sn) {
                params.order_id = this.editInfoFormObj.voucher_inner_sn;
            }
            if (record.detail_inner_sn) {
                params.detail_id = record.detail_inner_sn;
            }
            await hospitalApplyDetailStopAPI(params)
                .then((res) => {
                    this.addInfoPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const xAddInfoModalTable = this.$refs.xAddInfoModalTable;
                        for (let i = 0; i < this.addInfoDatas.length; i++) {
                            const item = this.addInfoDatas[i];
                            if (record._XID === item._XID) {
                                item.status = 3;
                                this.addInfoDatas.splice(i, 1, item);
                                xAddInfoModalTable.refreshData();
                                xAddInfoModalTable.loadData(this.addInfoDatas);
                                xAddInfoModalTable.setCurrentRow(item);
                                if (this.addInfoDatas.every((item) => item.status === 3)) {
                                    // 明细的状态都为已终止申领， 外层刷新所有列表
                                    this.hospitalApplyListAPIFn();
                                    this.hospitalApplyHistoryListAPIFn();
                                }
                                break;
                            }
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addInfoPagination.loading = false;
                });
        },
        // 【删除】申领材料-明细
        async hospitalApplyDetailDelAPIFn(record, index) {
            this.addInfoPagination.loading = true;
            let params = {
                hospital_inner_sn: record.hospital_inner_sn,
                product_type: record.product_type
            };
            if (this.editInfoFormObj.voucher_inner_sn) {
                params.order_id = this.editInfoFormObj.voucher_inner_sn;
            }
            if (record.detail_inner_sn) {
                params.detail_id = record.detail_inner_sn;
            }
            await hospitalApplyDetailDelAPI(params)
                .then((res) => {
                    this.addInfoPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const xAddInfoModalTable = this.$refs.xAddInfoModalTable;
                        xAddInfoModalTable.remove(record);
                        this.addInfoDatas = xAddInfoModalTable.getTableData().tableData;
                        xAddInfoModalTable.setCurrentRow(this.addInfoDatas[0]);
                        this.applyDetailRemoveResetFn(index);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addInfoPagination.loading = false;
                });
        },
        // 【确认发放】
        async hospitalGrantConfirmAPIFn(record, index) {
            this.suppliesClaimBotPagination.loading = true;
            await hospitalGrantConfirmAPI(record.hospital_inner_sn, record.voucher_inner_sn)
                .then((res) => {
                    this.suppliesClaimBotPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const curItem = this.suppliesClaimBotDatas[index],
                            receivedStatus = 2; // 已领取状态 - 2
                        this.suppliesClaimBotDatas.splice(index, 1, {
                            ...curItem,
                            status: receivedStatus,
                            audit_status_text: this.auditStatus[receivedStatus]
                        });
                        this.$Utils.globalDragCenterFn('modal-apply-send');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.suppliesClaimBotPagination.loading = false;
                });
        },
        // 【删除】材料申领信息
        async hospitalApplyDelAPIFn() {
            await hospitalApplyDelAPI(this.currentTrObj.voucher_inner_sn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.hospitalApplyListAPIFn();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【终止】材料申领信息
        async hospitalApplyStopAPIFn() {
            await hospitalApplyStopAPI(this.currentTrObj.hospital_inner_sn, this.currentTrObj.voucher_inner_sn).then(
                (res) => {
                    if (parseFloat(res.code) === 0) {
                        this.hospitalApplyListAPIFn();
                        this.hospitalApplyHistoryListAPIFn();
                        if (this.activeTabsKey === this.systemTypes[0]) {
                            this.changeTabsFn(this.systemTypes[1]);
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                }
            );
        },
        // 【修改】材料申领信息
        async hospitalApplyEditAPIFn(status) {
            // 状态 0-草稿 1-申领
            let params = {
                // 申领人ID现在没有： proposer_inner_sn - 申领人
                ...this.editInfoFormNewObj,
                details: this.details,
                hospital_inner_sn: this.editInfoFormObj.hospital_inner_sn || this.$cookie.get('userbelong'),
                voucher_inner_sn: this.editInfoFormObj.voucher_inner_sn
            };
            if (parseFloat(this.editInfoFormObj.status) === 0 && status === 1) {
                params.status = status;
                params.status_old = this.editInfoFormObj.status;
            } else if (parseFloat(this.editInfoFormObj.status) === 1 && status === 0) {
                params.status = status;
                params.status_old = this.editInfoFormObj.status;
            }
            await hospitalApplyEditAPI(params)
                .then((res) => {
                    status === 0
                        ? (this.addInfoModal.loadingSaveDraft = false)
                        : (this.addInfoModal.loadingApply = false);
                    if (parseFloat(res.code) === 0) {
                        const xSuppliesClaimTable = this.$refs.xSuppliesClaimTable,
                            curObjItem = xSuppliesClaimTable.getCurrentRow(),
                            infoItem = {
                                ...curObjItem,
                                status,
                                audit_status_text: this.auditStatus[parseFloat(status)],
                                department_inner_sn: this.editInfoFormObj.department_inner_sn,
                                department_name: this.editInfoFormObj.department_name,
                                proposer_inner_sn: this.editInfoFormObj.proposer_inner_sn,
                                proposer_name: this.editInfoFormObj.proposer_name,
                                create_time: this.editInfoFormObj.create_time,
                                expect_date: this.editInfoFormObj.expect_date
                            };
                        for (let i = 0; i < this.suppliesClaimDatas.length; i++) {
                            const item = this.suppliesClaimDatas[i];
                            if (curObjItem._XID === item._XID) {
                                this.suppliesClaimDatas.splice(i, 1, infoItem);
                                xSuppliesClaimTable.refreshData();
                                xSuppliesClaimTable.loadData(this.suppliesClaimDatas);
                                xSuppliesClaimTable.setCurrentRow(infoItem);
                                break;
                            }
                        }
                        this.details = [];
                        this.addInfoDatas = [];
                        this.editInfoFormNewObj = {};
                        this.addInfoModal.alert = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    status === 0
                        ? (this.addInfoModal.loadingSaveDraft = false)
                        : (this.addInfoModal.loadingApply = false);
                });
        },
        // 【经销商 产品单位】 根据id查询所有上级产品单位
        async dealerUnitParentsAPIFn(record) {
            await dealerUnitParentsAPI(
                record.dealer_inner_sn,
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
                record.dealer_inner_sn || record.vendor_inner_sn,
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
                        item.label = `${item.name}`;
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
                            this.$refs.xAddInfoModalTable.reloadRow(record, record, 'unit');
                        }
                        record.detail_inner_sn && !record.isLoadingUnit && this.dealerUnitParentsAPIFn(record);
                    }
                }
            });
        },
        // 【厂商 产品单位】 根据id查询所有上级产品单位
        async platformUnitParentsFn(record) {
            await platformUnitParents(
                record.vendor_inner_sn,
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
            await platformUnitTree(record.vendor_inner_sn, record.product_inner_sn, record.specification_inner_sn).then(
                (res) => {
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
                            item.label = `${item.name}`;
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
                                this.$refs.xAddInfoModalTable.reloadRow(record, record, 'unit');
                            }
                            record.detail_inner_sn && !record.isLoadingUnit && this.platformUnitParentsFn(record);
                        }
                    }
                }
            );
        },
        /**
         * 获取有权限仓库，并且为仓库列表赋值
         */
        getAllWarehouse() {
            const warehouseListStore = this.$store.state.warehouseList;
            if (warehouseListStore.length !== 0) {
                this.allWarehouseList = warehouseListStore.map((v) => {
                    return {
                        value: v.warehouse_model_inner_sn,
                        title: v.title
                    };
                });
                // this.editInfoFormObj.warehouse_inner_sn = this.allWarehouseList[0].value
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
        // 右上角刷新按钮
        refreshPageFn() {
            this.searchTables = {
                hospital_inner_sn: '',
                status: '',
                voucher_sn: '',
                create_time: '',
                expect_date: ''
            };
            if (this.activeTabsKey === this.systemTypes[0]) {
                this.suppliesClaimPagination = {
                    ...this.suppliesClaimPagination,
                    loading: true,
                    total: 10,
                    current: 1,
                    // pageSize: 10,
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                };
                this.hospitalApplyListAPIFn();
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
                this.hospitalApplyHistoryListAPIFn();
            }
        },
        // 页码改变的回调函数
        changePaginationFn({ currentPage, pageSize }) {
            this.suppliesClaimPagination.current = currentPage;
            this.suppliesClaimPagination.pageSize = pageSize;
            this.suppliesClaimPagination.loading = true;
            this.hospitalApplyListAPIFn();
        },
        changePaginationHistoryFn({ currentPage, pageSize }) {
            this.suppliesClaimHistoryPagination.current = currentPage;
            this.suppliesClaimHistoryPagination.pageSize = pageSize;
            this.suppliesClaimHistoryPagination.loading = true;
            this.hospitalApplyHistoryListAPIFn();
        },
        cellClickSuppliesClaimFn({ row }) {
            this.currentTrObj = row;
            this.hospitalGrantIssuedDetailListAPIFn(this.systemTypes[0], row);
        },
        cellDblclickSuppliesClaimFn({ row }) {
            this.currentTrObj = row;
            this.groupBtnFn('edit');
        },
        cellClickSuppliesClaimHistoryFn({ row }) {
            this.currentTrHistoryObj = row;
            this.hospitalGrantIssuedDetailListAPIFn(this.systemTypes[1], row);
        },
        cellDblclickSuppliesClaimHistoryFn({ row }) {
            this.currentTrHistoryObj = row;
            this.groupBtnFn('edit');
        },
        cellClickSuppliesBotClaimFn({ row }) {
            this.currentTrBotObj = row;
        },
        cellDblclickSuppliesBotClaimFn({ row }) {
            this.currentTrBotObj = row;
            this.showApplySendModalFn(row);
        },
        cellClickApplySendModalFn({ row }) {
            this.currentApplySendModalTrObj = row;
        },
        cellDblclickApplySendModalFn({ row }) {
            this.currentApplySendModalTrObj = row;
        },
        showApplySendModalFn(record) {
            this.applySendInfoFormObj = record;
            this.hospitalGrantDetailListAPIFn(record);
            this.applySendModal.alert = true;
            this.$Utils.globalDragCenterFn('modal-apply-send');
        },
        applySendModalBtnFn(btnName) {
            switch (btnName) {
                case 'print':
                    this.applySendModalPrint();
                    break;
                case 'cancel':
                    this.resetApplySendFn();
                    break;
                case 'submit':
                    if (this.tabActiveName === this.systemTypes[0]) {
                        for (let i = 0; i < this.suppliesClaimBotDatas.length; i++) {
                            if (this.suppliesClaimBotDatas[i].key === this.currentTrBotObj.key) {
                                this.applySendSubmitFn(this.currentTrBotObj, i);
                                break;
                            }
                        }
                    } else if (this.tabActiveName === this.systemTypes[1]) {
                        for (let i = 0; i < this.suppliesClaimHistoryBotDatas.length; i++) {
                            if (this.suppliesClaimHistoryBotDatas[i].key === this.currentTrHistoryBotObj.key) {
                                this.applySendSubmitFn(this.currentTrHistoryBotObj, i);
                                break;
                            }
                        }
                    }
                    break;
            }
        },
        applySendModalPrint() {
            // 发放单打印
            const table = this.$refs['xApplySendModalTable'];
            const tableData = table.getData();
            const tableColumns = table.getColumns();
            const columns = tableColumns.map((item) => {
                return {
                    dataIndex: item.property,
                    title: item.title,
                    width: item.width,
                    key: item.property,
                    align: 'center'
                };
            });

            let printContent = {
                printData: tableData,
                printForm: {
                    ...this.applySendInfoFormObj,
                    employeeInfoName: this.applySendInfoFormObj.approver_name, // 发放人
                    receiverName: this.receiverName, // 领取人
                    time: this.applySendInfoFormObj.grant_time // 发放时间
                },
                printTable: columns,
                printType: 2, // 1 图片；2、耗材申领；3、出入库单
                printCaseType: 2, // 发放
                printTitle: '发放单'
            };
            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        resetApplySendFn() {
            this.applySendModal.alert = false;
            this.applySendModal.loadingApply = false;
            this.applySendInfoFormObj = {
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
            };
        },
        applySendSubmitFn(record, index) {
            const _this = this;
            _this.$confirm({
                title: '确认发放',
                content: '确认发放吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.hospitalGrantConfirmAPIFn(record, index);
                },
                onCancel() {
                    _this.resetApplySendFn();
                }
            });
        },
        cellClickSuppliesHistoryBotClaimFn({ row }) {
            this.currentTrHistoryBotObj = row;
        },
        cellDblclickSuppliesHistoryBotClaimFn({ row }) {
            this.currentTrHistoryBotObj = row;
            this.showApplySendModalFn(this.currentTrHistoryBotObj);
        },
        cellAddInfoClickFn({ row }) {
            this.currentTrAddInfoObj = row;
        },
        cellAddInfoDblclickFn({ row }, e) {
            this.currentTrAddInfoObj = row;
            if (this.isEditDisabled) return;
            this.dblclickTableFn(e);
        },
        // 获取期望时间
        getexpectDate(date, dateString) {
            this.editInfoFormObj.expect_date = dateString;
        },
        // 判断申领单的表格有数据没有 并且校验值
        checkApplySingleFn(btnName) {
            let flag = true;
            if (this.addInfoDatas.length <= 0) {
                if (btnName !== 'cancel') {
                    this.$warning({
                        title: '操作提示',
                        content: '请先双击表格的空白部分完成添加产品，再进行相关操作。',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                }
                flag = false;
            } else {
                if (btnName === 'print') {
                    const status = this.currentTrObj.status;
                    let columns = this.addInfoDraftStatusColumns;
                    let tableData = this.addInfoDatas;
                    if (status !== 0 || this.btnName === 'edit') {
                        columns = this.addInfoDraftColumns;
                        tableData = tableData.map((item) => {
                            return {
                                ...item,
                                status: this.$options.filters['filterApplyStatus'](item.status) // 状态名称
                            };
                        });
                    }
                    let printContent = {
                        printData: tableData,
                        printForm: this.editInfoFormObj,
                        printTable: columns.filter((item) => item.title !== '操作'),
                        printInfoStatus: status,
                        printType: 2, // 1 图片；2、耗材申领；3、出入库单
                        printCaseType: 1, // 申领
                        printTitle: '申领单'
                    };
                    printContent = JSON.stringify(printContent);
                    window.localStorage.printContent = printContent;
                    let routeUrl = this.$router.resolve({
                        path: 'print',
                        name: 'print'
                    });
                    window.open(routeUrl.href, '_blank');
                    return;
                }

                // 判断部门是否为空
                if (this.editInfoFormDepartmentInnerSn.length <= 0) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择科室',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    flag = false;
                    return;
                }
                // 判断表格主体的申领 - 包装单位
                if (this.addInfoDatas.some((item) => item.plan_quantity <= 0 || item.unit_inner_sn.length <= 0)) {
                    this.$warning({
                        title: '操作提示',
                        content: '请填写申领/包装单位',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    flag = false;
                    return;
                } else {
                    // 产品只能出现唯一
                    let isRepeatObj = {};
                    for (let i = 0; i < this.addInfoDatas.length; i++) {
                        const item = this.addInfoDatas[i],
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
                    // if (this.btnName === 'add' || this.btnName === 'edit') { // 增加同一个产品的包装单位不能一样做校验
                    //     let curIdUnitNames = []
                    //     let batchNumbers = []
                    //     for (let i = 0; i < this.addInfoDatas.length; i++) {
                    //         let item = this.addInfoDatas[i],
                    //             idUnitName = '',
                    //             batchNumber = ''
                    //         if (parseFloat(item.product_type) === 0) { // 经销商
                    //             idUnitName = item.dealer_inner_sn + '' + item.product_type + item.product_name + item.product_inner_sn + item.specification_inner_sn + item.specification_name + item.unit_inner_sn[item.unit_inner_sn.length - 1] + item.unit_name
                    //             batchNumber = item.dealer_inner_sn + '' + item.product_type + item.product_name + item.product_inner_sn + item.specification_inner_sn + item.specification_name + item.batch_number
                    //         } else if (parseFloat(item.product_type) === 1) { // 厂商
                    //             idUnitName = item.vendor_inner_sn + '' + item.product_type + item.product_name + item.product_inner_sn + item.specification_inner_sn + item.specification_name + item.unit_inner_sn[item.unit_inner_sn.length - 1] + item.unit_name
                    //             batchNumber = item.vendor_inner_sn + '' + item.product_type + item.product_name + item.product_inner_sn + item.specification_inner_sn + item.specification_name + item.batch_number
                    //         }
                    //         if (curIdUnitNames.includes(idUnitName)) { // 说明有相同的产品和包装单位
                    //             this.$warning({
                    //                 title: '操作提示',
                    //                 content: '添加相同产品的包装单位不能一样',
                    //                 okText: '知道了',
                    //                 centered: true,
                    //                 maskClosable: false
                    //             })
                    //             flag = false
                    //             break
                    //         } else {
                    //             curIdUnitNames.push(idUnitName)
                    //         }
                    //         if (batchNumbers.includes(batchNumber)) { // 说明有相同的产品和包装单位
                    //             this.$warning({
                    //                 title: '操作提示',
                    //                 content: '添加相同产品的批号不能一样',
                    //                 okText: '知道了',
                    //                 centered: true,
                    //                 maskClosable: false
                    //             })
                    //             flag = false
                    //             break
                    //         } else {
                    //             batchNumbers.push(batchNumber)
                    //         }
                    //     }
                    //     curIdUnitNames = null // 释放内存
                    // }
                    if (flag && this.btnName === 'edit') {
                        // 校验产品列表有没有改过
                        let curIdUnitName = '',
                            oldIdUnitName = '';
                        this.addInfoDatas.forEach((item) => {
                            let idUnitName = '';
                            if (parseFloat(item.product_type) === 0) {
                                // 经销商
                                idUnitName =
                                    item.dealer_inner_sn +
                                    '' +
                                    item.product_type +
                                    item.product_name +
                                    item.product_inner_sn +
                                    item.specification_inner_sn +
                                    item.specification_name +
                                    item.unit_inner_sn[item.unit_inner_sn.length - 1] +
                                    item.unit_name;
                            } else if (parseFloat(item.product_type) === 1) {
                                // 厂商
                                idUnitName =
                                    item.vendor_inner_sn +
                                    '' +
                                    item.product_type +
                                    item.product_name +
                                    item.product_inner_sn +
                                    item.specification_inner_sn +
                                    item.specification_name +
                                    item.unit_inner_sn[item.unit_inner_sn.length - 1] +
                                    item.unit_name;
                            }
                            curIdUnitName += idUnitName;
                        });
                        this.editInfoFormOldObj.details.forEach((item) => {
                            let idUnitName = '';
                            if (parseFloat(item.product_type) === 0) {
                                // 经销商
                                idUnitName =
                                    item.dealer_inner_sn +
                                    '' +
                                    item.product_type +
                                    item.product_name +
                                    item.product_inner_sn +
                                    item.specification_inner_sn +
                                    item.specification_name +
                                    item.unit_inner_sn[item.unit_inner_sn.length - 1] +
                                    item.unit_name;
                            } else if (parseFloat(item.product_type) === 1) {
                                // 厂商
                                idUnitName =
                                    item.vendor_inner_sn +
                                    '' +
                                    item.product_type +
                                    item.product_name +
                                    item.product_inner_sn +
                                    item.specification_inner_sn +
                                    item.specification_name +
                                    item.unit_inner_sn[item.unit_inner_sn.length - 1] +
                                    item.unit_name;
                            }
                            oldIdUnitName += idUnitName;
                        });
                        if (curIdUnitName === oldIdUnitName) {
                            // 说明没有改，直接关闭弹窗
                            this.addInfoModal.alert = false;
                            this.addInfoModal.loadingSaveDraft = false;
                            this.addInfoModal.loadingApply = false;
                            flag = false;
                        } else {
                            this.checkEditInfoFormObjFn();
                            flag = true;
                        }
                    }
                    if (flag) {
                        this.details = this.addInfoDatas.map((item, index) => {
                            let status = typeof parseFloat(item.status) === 'number' ? item.status : 0;
                            if (parseFloat(item.product_type) === 0) {
                                // 经销商
                                return {
                                    // // 后台接口发生变化，获得的数据变了
                                    dealer_inner_sn: item.dealer_inner_sn || item.vendor_inner_sn,
                                    dealer_name: item.dealer_name || item.vendor_name,
                                    product_inner_sn: item.product_inner_sn,
                                    product_name: item.product_name,
                                    specification_inner_sn: item.specification_inner_sn,
                                    specification: item.specification_name,
                                    unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                                    unit_name: item.unit_name,
                                    sort_number: index,
                                    plan_quantity: item.plan_quantity,
                                    achieve_quantity: item.achieve_quantity || 0,
                                    product_type: item.product_type,
                                    batch_number: item.batch_number,
                                    status
                                };
                            } else if (parseFloat(item.product_type) === 1) {
                                // 厂商
                                return {
                                    vendor_inner_sn: item.vendor_inner_sn,
                                    vendor_name: item.vendor_name,
                                    product_inner_sn: item.product_inner_sn,
                                    product_name: item.product_name,
                                    specification_inner_sn: item.specification_inner_sn,
                                    specification: item.specification_name,
                                    unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                                    unit_name: item.unit_name,
                                    sort_number: index,
                                    plan_quantity: item.plan_quantity,
                                    achieve_quantity: item.achieve_quantity || 0,
                                    product_type: item.product_type,
                                    batch_number: item.batch_number,
                                    status
                                };
                            }
                        });
                        flag = true;
                    }
                }
            }
            return flag;
        },
        // 申领单表格中的操作按钮
        applyDetailGroupBtnFn(btnName, record, index) {
            switch (btnName) {
                case 'remove':
                    if (
                        this.btnName === 'add' ||
                        this.btnName === 'copy' ||
                        this.btnModalName === 'copy' ||
                        !record.detail_inner_sn
                    ) {
                        const xAddInfoModalTable = this.$refs.xAddInfoModalTable;
                        xAddInfoModalTable.remove(record);
                        this.addInfoDatas = xAddInfoModalTable.getTableData().tableData;
                        xAddInfoModalTable.setCurrentRow(this.addInfoDatas[0]);
                        this.applyDetailRemoveResetFn(index);
                    } else {
                        this.hospitalApplyDetailDelAPIFn(record, index);
                    }
                    break;
                case 'apply-stop': // 申领终止
                    this.hospitalApplyDetailStopAPIFn(record, index);
                    break;
            }
        },
        // 前端删除明细的操作重置
        applyDetailRemoveResetFn() {
            if (this.addInfoDatas.length <= 0) {
                this.currentTrAddInfoObj = {};
                this.dblclickAddProductFn();
            } else {
                this.currentTrAddInfoObj = this.addInfoDatas[0];
            }
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
        // 打印 - 复制 - 存草稿 - 申领按钮的操作
        addInfoModalGroupBtnFn(btnName) {
            if (!(btnName === 'cancel' || btnName === 'copy') && !this.checkApplySingleFn(btnName)) return;
            this.btnModalName = btnName;
            const _this = this;
            switch (btnName) {
                case 'print': // 打印
                    break;
                case 'copy': // 复制
                    _this.$confirm({
                        title: '操作提示',
                        content: '即将复制当前“申领单”，复制完成后当前单据会被关闭，您将看到新的申领单，是否继续？',
                        okText: '确认',
                        cancelText: '取消',
                        centered: true,
                        maskClosable: false,
                        onOk() {
                            const voucherInnerSn =
                                _this.activeTabsKey === _this.systemTypes[0]
                                    ? _this.currentTrObj.voucher_inner_sn
                                    : _this.currentTrHistoryObj.voucher_inner_sn;
                            _this.getAllWarehouse();
                            _this.editInfoFormObj = {
                                warehouse_inner_sn: _this.warehouseList[0].value,
                                warehouse_inner_sn_name: _this.warehouseList[0].title,
                                create_time: '',
                                department_inner_sn: '',
                                department_name: '',
                                description: '',
                                expect_date: '',
                                hospital_inner_sn: '',
                                hospital_name: '',
                                proposer_inner_sn: '',
                                proposer_name: '',
                                status: '',
                                voucher_inner_sn: '',
                                voucher_sn: ''
                            };
                            _this.expectDate = moment[''];
                            _this.addInfoPagination.loading = false;
                            _this.addInfoModal.loadingSaveDraft = false;
                            _this.addInfoModal.loadingApply = false;
                            _this.addInfoDatas = [];
                            _this.isEditDisabledFlag = true;
                            _this.orgDeptRelAPIFn();
                            _this.hospitalApplyCopyAPIFn(voucherInnerSn);
                            _this.addInfoModal.title = `${_this.$route.meta.title} - 复制`;
                            _this.addInfoModal.alert = true;
                            _this.$Utils.globalDragCenterFn('modal-add');
                        }
                    });
                    break;
                case 'cancel': // 取消
                    this.addInfoModal.alert = false;
                    break;
                case 'saveDraft': // 草稿
                    this.addInfoModal.loadingSaveDraft = true;
                    if (this.btnName === 'add' || !this.isEditDisabledFlag) {
                        this.hospitalApplyAddAPIFn(0);
                    } else if (this.btnName === 'edit') {
                        if (Object.keys(this.editInfoFormNewObj).length || this.details.length) {
                            this.hospitalApplyEditAPIFn(0);
                        } else {
                            this.addInfoModal.alert = false;
                            this.addInfoModal.loadingSaveDraft = false;
                        }
                    }
                    break;
                case 'apply': // 申领
                    this.addInfoModal.loadingApply = true;
                    if (this.btnName === 'add' || !this.isEditDisabledFlag) {
                        this.hospitalApplyAddAPIFn(1);
                    } else if (this.btnName === 'edit') {
                        if (Object.keys(this.editInfoFormNewObj).length || this.details.length) {
                            this.hospitalApplyEditAPIFn(1);
                        } else {
                            this.addInfoModal.alert = false;
                            this.addInfoModal.loadingApply = false;
                        }
                    }
                    break;
            }
        },
        exportExcel(refName) {
            // 导出Excel
            const table = this.$refs[refName];
            const isSend = refName === 'xApplySendModalTable'; // 是否为发放单
            if (!table) return;
            let formData = {};
            if (isSend) {
                formData = this.getExportFormDataForSend(); // 获取发放单表单信息
            } else {
                formData = this.getExportFormDataForApply(); // 获取申领单表单信息
            }
            const tableData = table.getData().map((item) => {
                return {
                    ...item,
                    status: isSend ? item.status : this.$options.filters['filterApplyStatus'](item.status) // 状态文本
                };
            });
            const tableColumns = table.getColumns();
            this.$Utils.exportExcel(tableColumns, tableData, [], formData);
        },
        // 获取申领单表单信息
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
                            label: '科室',
                            value: form.department_name
                        },
                        {
                            label: '仓库',
                            value: form.warehouse_inner_sn_name
                        },
                        {
                            label: '申领人',
                            value: form.proposer_name
                        },
                        {
                            label: '期望于',
                            value: form.expect_date ? form.expect_date + ' 之前发放' : '-之前发放'
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
        // 获取发放单表单信息
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
                            label: '单据编号',
                            value: form.gv_sn
                        },
                        {
                            label: '科室',
                            value: form.department_name
                        },
                        {
                            label: '发放仓库',
                            value: form.warehouse_name
                        }
                    ],
                    [
                        {
                            label: '发放人',
                            value: form.approver_name
                        },
                        {
                            label: '领取人',
                            value: this.receiverName
                        },
                        {
                            label: '发放时间',
                            value: form.grant_time
                        }
                    ]
                ]
            };
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
                this.editInfoFormObj.department_inner_sn = value;
                this.editInfoFormObj.department_name = Array.isArray(label) ? label[0] : label;
            } else {
                this.editInfoFormObj.department_inner_sn = '';
                this.editInfoFormObj.department_name = '';
            }
        },
        // 仓库
        editInfoFormWarehouseChangeFn(value, d) {
            if (!value || !d) {
                this.editInfoFormObj.warehouse_inner_sn = '';
                this.editInfoFormObj.warehouse_inner_sn_name = '';
            } else {
                const selected = d.data.props;
                this.editInfoFormObj.warehouse_inner_sn = selected.value;
                this.editInfoFormObj.warehouse_inner_sn_name = selected.title;
            }
        },
        // 设置包装名称
        unitInnerSnFn(value, selectedOptions) {
            this.record.unit_name = selectedOptions.length ? selectedOptions[selectedOptions.length - 1].name : '';
        },
        clickUnitInnerSnFn(record) {
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
        checkCurrentTrDataFn(btnName) {
            // 检测表格是否有选中的的数据\
            let flag = true;
            if (this.activeTabsKey === this.systemTypes[0]) {
                if (Object.keys(this.currentTrObj).length <= 0) {
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
            if (this.activeTabsKey === this.systemTypes[1] && btnName === 'copy') {
                if (Object.keys(this.currentTrHistoryObj).length <= 0) {
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
            return flag;
        },
        groupBtnFn(btnName) {
            if (btnName !== 'add' && !this.checkCurrentTrDataFn(btnName)) return;
            const _this = this,
                _isActiveTabsFlag = _this.activeTabsKey === _this.systemTypes[0]; // 是否是第一个tabs选项卡
            let voucherInnerSn = '';
            this.btnName = btnName;
            // 默认不是新建订单弹窗
            this.addInfoModal.isNewOrder = false;
            if (btnName === 'add' || btnName === 'edit' || btnName === 'copy') {
                this.getAllWarehouse();
                this.editInfoFormObj = {
                    warehouse_inner_sn: this.warehouseList[0].value,
                    warehouse_inner_sn_name: this.warehouseList[0].title,
                    create_time: '',
                    department_inner_sn: '',
                    department_name: '',
                    description: '',
                    expect_date: '',
                    hospital_inner_sn: '',
                    hospital_name: '',
                    proposer_inner_sn: '',
                    proposer_name: '',
                    status: '',
                    voucher_inner_sn: '',
                    voucher_sn: ''
                };
                this.expectDate = moment[''];
                this.addInfoPagination.loading = false;
                this.addInfoModal.loadingSaveDraft = false;
                this.addInfoModal.loadingApply = false;
                this.addInfoDatas = [];
                this.isEditDisabledFlag = true;
                this.orgDeptRelAPIFn();
            }
            if (btnName !== 'add') {
                voucherInnerSn = _isActiveTabsFlag
                    ? this.currentTrObj.voucher_inner_sn
                    : this.currentTrHistoryObj.voucher_inner_sn;
            }
            switch (btnName) {
                case 'add':
                    this.addInfoModal.title = `${this.$route.meta.title} - 新增`;
                    this.addInfoModal.alert = true;
                    this.addInfoModal.isNewOrder = true;
                    this.$Utils.globalDragCenterFn('modal-add');
                    this.dblclickAddProductFn();
                    break;
                case 'edit':
                    this.addInfoPagination.loading = true;
                    this.addInfoModal.title = `${this.$route.meta.title} - 修改`;
                    this.addInfoModal.alert = true;
                    this.hospitalApplyInfoAPIFn(voucherInnerSn);
                    this.$Utils.globalDragCenterFn('modal-add');
                    break;
                case 'remove':
                    if (_isActiveTabsFlag && parseFloat(_this.currentTrObj.status) === 0) {
                        _this.$confirm({
                            title: '删除',
                            content: '确认要执行该操作吗？',
                            maskClosable: false,
                            centered: true,
                            onOk() {
                                _this.hospitalApplyDelAPIFn();
                                return new Promise((resolve, reject) => {
                                    setTimeout(Math.random() > 0.5 ? resolve : reject, 800);
                                }).catch(() => {});
                            }
                        });
                    } else {
                        _this.$warning({
                            title: '删除',
                            content: '只有【草稿】状态的才可以删除',
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                    }
                    break;
                case 'copy':
                    this.addInfoPagination.loading = true;
                    this.hospitalApplyCopyAPIFn(voucherInnerSn);
                    this.addInfoModal.title = `${this.$route.meta.title} - 复制`;
                    this.addInfoModal.alert = true;
                    this.$Utils.globalDragCenterFn('modal-add');
                    break;
                case 'stop': // 申领终止
                    if (
                        _isActiveTabsFlag &&
                        (parseFloat(_this.currentTrObj.status) === 1 || parseFloat(_this.currentTrObj.status) === 2)
                    ) {
                        _this.$confirm({
                            title: '申领终止',
                            content: '申领终止后，耗材不再继续发放，您确认要执行该操作吗？',
                            maskClosable: false,
                            centered: true,
                            onOk() {
                                _this.hospitalApplyStopAPIFn();
                                return new Promise((resolve, reject) => {
                                    setTimeout(Math.random() > 0.5 ? resolve : reject, 800);
                                }).catch(() => {});
                            }
                        });
                    } else {
                        _this.$warning({
                            title: '申领终止',
                            content: '状态是【申领】，才可以执行该操作。',
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                    }
                    break;
            }
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
            this.$Utils.globalDragCenterFn('add_product');
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        addProductFn() {
            // 添加商品
            this.$refs.chooseProMd.returnProList();
        },
        getProReturn(dea, ven) {
            // 获取回调商品
            dea.forEach((item) => {
                item.specification_name = item.specification;
                item.batchNumberList = [];
                item.batch_number = '';
            });
            ven.forEach((item) => {
                item.specification_name = item.specification;
                item.batchNumberList = [];
                item.batch_number = '';
            });
            this.addProductMd.alert = false;
            this.addInfoPagination.loading = true;
            this.clearRowClassNameFn();
            const xTableAddInfo = this.$refs.xAddInfoModalTable,
                oldAddDatas = [...ven, ...dea],
                len = this.addInfoDatas.length;
            let curObj = xTableAddInfo.getCurrentRow();
            if (!curObj) {
                for (let i = 0; i < len; i++) {
                    const item = this.addInfoDatas[i];
                    if (item.temp_add_flag) {
                        if (!curObj) {
                            curObj = item;
                        }
                        delete item.temp_add_flag;
                    }
                }
                xTableAddInfo.reloadData(this.addInfoDatas);
            }
            if (len) {
                for (let i = 0; i < len; i++) {
                    const item = this.addInfoDatas[i];
                    if (item._XID === curObj._XID) {
                        this.addInfoDatas.splice(i, 0, ...oldAddDatas);
                        xTableAddInfo.clearCurrentRow();
                        xTableAddInfo.refreshData();
                        xTableAddInfo.loadData(this.addInfoDatas);
                        break;
                    }
                }
            } else {
                // 完全新增
                this.addInfoDatas.push(...oldAddDatas);
                xTableAddInfo.loadData(this.addInfoDatas);
            }
            let timer = setTimeout(() => {
                this.addInfoPagination.loading = false;
                clearTimeout(timer);
                timer = null;
            }, 200);
            this.addInfoDatas.forEach((item, index) => {
                if (item.batchNumberList.length === 0) {
                    this.hospitalApplyBatchNumberListAPIFn(item, index);
                }
            });
        },
        rowClassNameFn({ row }) {
            if (row.row_add_flag) {
                return 'row--current';
            }
        },
        clearRowClassNameFn() {
            // 去除新增的临时背景颜色
            this.addInfoDatas.forEach((item) => {
                if (item.row_add_flag) {
                    item.row_add_flag = false;
                }
            });
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
                ? this.hospitalApplyListAPIFn()
                : this.hospitalApplyHistoryListAPIFn();
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
            return true;
        },
        // 点击高级搜索的小图标的事件
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
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

                if (this.btnName === 'add') {
                    // 设置默认部门值为当前登陆人的第一个部门
                    let intersection = [];
                    let userBelong = JSON.parse(localStorage.getItem('userInfo')).Departments;
                    userBelong.filter((a) => {
                        datasOriginNew.filter((b) => {
                            if (a.Department_inner_sn === b.department_inner_sn) {
                                intersection.push(b);
                            }
                        });
                    });
                    this.editInfoFormDepartmentInnerSn = intersection[0].department_inner_sn;
                    this.setEditInfoFormDepartmentInnerSnFn(intersection[0].department_inner_sn, intersection[0].label);
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
        rowClassBotFn(record) {
            if (record.key === this.currentTrBotObj.key) {
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
        rowClassHistoryBotFn(record) {
            if (record.key === this.currentTrHistoryBotObj.key) {
                return 'active-tr';
            }
            return '';
        },
        rowClassAddInfoModalFn(record) {
            if (record.index === this.currentTrAddInfoObj.index) {
                return 'active-tr';
            }
            return '';
        },
        rowClassApplySendModalFn(record) {
            if (record.key === this.currentApplySendModalTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        changeTabsFn(activeKey) {
            // 改变tabs的key值
            if (!this.checkSearchInfoFormMsgFn()) return;
            this.activeTabsKey = activeKey;
            this.searchTables.status = '';
            if (!this.isActiveTabsKey) {
                this.isActiveTabsKey = true;
                this.hospitalApplyHistoryListAPIFn();
            }
        },
        /**
         * 重置申领单搜索结果
         */
        resetSearchResult() {
            this.refreshPageFn();
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
        }
    },
    filters: {
        filterApplyStatus(value) {
            const planListStatus = ['申领中', '已完全发放', '已计划采购', '已终止申领'];
            return planListStatus[value];
        }
    },
    watch: {
        '$route'() {
            this.init();
        }
    },
    computed: {
        ...mapState(['warehouseList']),
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
            return Math.floor(this.getContentHeight() * 0.418);
        },
        scrollYDblclickNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 307;
            return clientHeight - topHeight;
        },
        isEditDisabled() {
            // 弹框的内容是否可编辑 【只有新增、草稿、赋值状态的才可以触发存草稿、申领按钮】
            let flag = true;
            if (
                this.btnName === 'copy' ||
                this.btnName === 'add' ||
                (this.btnModalName === 'copy' && !this.isEditDisabledFlag)
            ) {
                flag = false;
            } else if (this.activeTabsKey === this.systemTypes[0] && this.currentTrObj.status === 0) {
                flag = false;
            }
            return flag;
        },
        // 机构名称
        orgName() {
            const employeeInfo = this.$cookie.get('EmployeeInfo');
            return (employeeInfo ? JSON.parse(employeeInfo).OrgName : '') || '';
        }
    },
    components: {
        addProduct,
        allotTable
    },
    mounted() {
        this.boxTopHeight = this.scrollYNumber - 78;
        this.boxDownHeight = this.scrollYNumber;
        this.dragControllerDiv();
        // console.log(localStorage.getItem('userInfo').Departments)
        // console.log(localStorage.getItem('userInfo'))
    },
    created() {
        this.init();
    }
};
</script>

<style lang="less" scoped>
.scoped .search-form-wrap .hight-search-wrap .form li.search-buttons-group {
    display: flex;
    width: 172px;
    justify-content: space-between;
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
