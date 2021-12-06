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
                                :disabled="tabChoose !== 'todo'"
                                @click="groupBtnFn('add')"
                                type="primary"
                                size="small"
                                >新增</a-button
                            >
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-show="!hightSearchObj.isArrow"
                            v-model="searchObj.hospitalName"
                            placeholder="医院名称"
                            size="small"
                            enterButton
                            class="search"
                            @search="searchBtnFn"
                        />
                        <div @click.stop="hightSearchIconFn" class="hight-search-text">
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
                    <ul class="form form-operation">
                        <li>
                            <span class="label">医院名称</span>
                            <a-input v-model="searchObj.hospitalName" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">手术名称</span>
                            <a-input v-model="searchObj.q" type="text" placeholder="" />
                        </li>
                        <!-- <li>
                            <span class="label">手术类别</span>
                            <a-select
                                size="small"
                                v-model="searchObj.types"
                            >
                                <a-select-option value>全部</a-select-option>
                                <a-select-option value="0">颅骨类</a-select-option>
                                <a-select-option value="1">脊柱类</a-select-option>
                                <a-select-option value="2">关节类</a-select-option>
                                <a-select-option value="3">创伤类</a-select-option>
                            </a-select>
                        </li> -->
                        <li class="ant-calendar-picker-wrap">
                            <span class="label">手术时间</span>
                            <a-range-picker
                                v-model="searchObj.operationTime"
                                :placeholder="['', '']"
                                @change="changeOperationTimeFn"
                            ></a-range-picker>
                        </li>
                        <li>
                            <a-button @click="searchBtnFn('hight')" type="primary">搜索</a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab切换区域开始 -->
        <div
            :class="hightSearchObj.isArrow ? 'top136' : 'top56'"
            class="tab-table-wrap tab-table-overflowy"
            style="background: #f0f2f5"
        >
            <a-tabs style="overflow: hidden" type="card" :activeKey="tabChoose" @change="changeTabFn">
                <a-tab-pane defaultActiveKey="0" :key="tabTypes[0]" tab="待办">
                    <ul
                        v-if="todoDatas.length"
                        class="operation-plan-list-wrap"
                        :style="'height:' + todoHeight + 'px;overflow:auto'"
                    >
                        <li v-for="(todo, todoIndex) in todoDatas" :key="todo.id" class="operation-plan-list">
                            <div class="operation-title">
                                <ul class="caption-title">
                                    <li>
                                        <span class="bold">{{ todo.hospital_name }}</span>
                                        <span class="bold">{{ todo.name }}</span>
                                    </li>
                                    <li>
                                        <span class="bold">手术时间：</span>
                                        <span>{{ todo.operation_time }}</span>
                                    </li>
                                    <li>
                                        <span class="bold">状态：</span>
                                        <span>{{ surgeryPlanStatus[todo.status] }}</span>
                                    </li>
                                </ul>
                                <div class="btn-group-wrap">
                                    <div>
                                        <a-tooltip
                                            class="tooltip"
                                            placement="top"
                                            :title="
                                                todo.status === 'DRAFT' || todo.status === 'CONFIRMED' ? '修改' : '查看'
                                            "
                                        >
                                            <i
                                                @click="groupBtnFn('edit', todo, todoIndex)"
                                                class="yrt yrt-wendangxiugai edit"
                                                :class="
                                                    todo.status === 'DRAFT' || todo.status === 'CONFIRMED'
                                                        ? ''
                                                        : 'yrt-yanjing'
                                                "
                                                style="cursor: pointer"
                                            ></i>
                                        </a-tooltip>
                                    </div>
                                    <div>
                                        <a-tooltip class="tooltip" placement="top" title="删除">
                                            <i
                                                @click="groupBtnFn('del', todo, todoIndex)"
                                                class="yrt yrt-shanchu2 cancel"
                                                :class="
                                                    todo.status === 'DRAFT' || todo.status === 'CONFIRMED'
                                                        ? ''
                                                        : 'disabled'
                                                "
                                                style="cursor: pointer; font-size: 16px"
                                            ></i>
                                        </a-tooltip>
                                    </div>
                                    <div>
                                        <a-button
                                            :disabled="todo.status !== 'CONFIRMED'"
                                            @click="groupBtnFn('publish', todo, todoIndex)"
                                            size="small"
                                            type="primary"
                                            >发布</a-button
                                        >
                                    </div>
                                </div>
                            </div>
                            <vxe-grid
                                v-if="todo.details.length"
                                ref="xTodoTable"
                                size="small"
                                border
                                resizable
                                auto-resize
                                highlight-current-row
                                align="center"
                                :data="todo.details"
                                :columns="todoColumns"
                                show-overflow
                                show-header-overflow
                                showHeader
                            ></vxe-grid>
                        </li>
                    </ul>
                    <div v-else class="no-data-wrap" :style="'height:' + todoHeight + 'px;overflow:auto'">
                        <div class="no-data">
                            <a-spin v-if="todoPagination.loading" tip="加载中" />
                            <div v-else> 暂无数据 </div>
                        </div>
                    </div>
                    <vxe-pager
                        :loading="todoPagination.loading"
                        :current-page="todoPagination.current"
                        :page-size="todoPagination.pageSize"
                        :page-sizes="todoPagination.pageSizes"
                        :total="todoPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changeTodoPaginationFn"
                    ></vxe-pager>
                </a-tab-pane>
                <a-tab-pane :key="tabTypes[1]" tab="待销售" style="padding: 8px 8px 0px 8px">
                    <vxe-table
                        ref="xToSalesTable"
                        size="small"
                        :height="scrollYNumber"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        align="center"
                        :data="toSalesDatas"
                        :loading="toSalesPagination.loading"
                        show-overflow
                        show-header-overflow
                        showHeader
                        :start-index="(toSalesPagination.current - 1) * toSalesPagination.pageSize"
                    >
                        <vxe-table-column type="seq" title="NO." width="6%" show-overflow></vxe-table-column>
                        <vxe-table-column
                            field="hospital_name"
                            title="医院"
                            width="10%"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column
                            field="plan_name"
                            title="手术名称"
                            width="10%"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column field="ois_sn" title="编号" width="9%" show-overflow></vxe-table-column>
                        <vxe-table-column field="name" title="名称" width="10%" show-overflow></vxe-table-column>
                        <vxe-table-column field="type_name" title="类别" width="8%" show-overflow></vxe-table-column>
                        <vxe-table-column field="brand_name" title="品牌" width="6%" show-overflow></vxe-table-column>
                        <vxe-table-column
                            field="operation_time"
                            title="手术时间"
                            width="13%"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column field="status" title="状态" width="8%" show-overflow>
                            <template v-slot="{ row }">
                                <div>{{ surgeryPlanPackageStatus[row.status] }}</div>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            field="assistant_employee_name"
                            title="助理"
                            width="8%"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column field="action" title="操作" width="12%" show-overflow>
                            <template v-slot="{ row, seq }">
                                <div>
                                    <a-button
                                        v-if="row.status === 'BACK'"
                                        size="small"
                                        type="primary"
                                        @click.stop="identifySalesAndLossesFn(row, seq)"
                                        >填报销售和损耗</a-button
                                    >
                                    <a-button
                                        v-else-if="row.status === 'SALE_PENDING'"
                                        size="small"
                                        type="primary"
                                        @click.stop="identifySalesAndLossesFn(row, seq)"
                                        >查看损耗</a-button
                                    >
                                    <a-button
                                        v-else
                                        size="small"
                                        type="primary"
                                        @click.stop="identifySalesAndLossesFn(row, seq)"
                                        >确认出单</a-button
                                    >
                                </div>
                            </template>
                        </vxe-table-column>
                    </vxe-table>
                    <vxe-pager
                        :loading="toSalesPagination.loading"
                        :current-page="toSalesPagination.current"
                        :page-size="toSalesPagination.pageSize"
                        :page-sizes="toSalesPagination.pageSizes"
                        :total="toSalesPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changeToSalesPaginationFn"
                    ></vxe-pager>
                </a-tab-pane>
                <a-tab-pane :key="tabTypes[2]" tab="历史">
                    <ul
                        v-if="historyDatas.length"
                        class="operation-plan-list-wrap"
                        :style="'height:' + todoHeight + 'px;overflow:auto'"
                    >
                        <li v-for="todo in historyDatas" :key="todo.id" class="operation-plan-list">
                            <div class="operation-title">
                                <ul class="caption-title">
                                    <li>
                                        <span class="bold">{{ todo.hospital_name }}</span>
                                        <span class="bold">{{ todo.name }}</span>
                                    </li>
                                    <li>
                                        <span class="bold">手术时间：</span>
                                        <span>{{ todo.operation_time }}</span>
                                    </li>
                                    <li>
                                        <span class="bold">状态：</span>
                                        <span>{{ surgeryPlanStatus[todo.status] }}</span>
                                    </li>
                                </ul>
                                <div class="btn-group-wrap">
                                    <div>
                                        <a-tooltip class="tooltip" placement="top" title="查看">
                                            <i
                                                @click="groupBtnFn('edit', todo)"
                                                class="yrt yrt-wendangxiugai edit yrt-yanjing"
                                                style="cursor: pointer"
                                            ></i>
                                        </a-tooltip>
                                    </div>
                                </div>
                            </div>
                            <vxe-grid
                                v-if="todo.details.length"
                                ref="xTodoTable"
                                size="small"
                                border
                                resizable
                                auto-resize
                                highlight-current-row
                                align="center"
                                :data="todo.details"
                                :columns="historyColumns"
                                show-overflow
                                show-header-overflow
                                showHeader
                            ></vxe-grid>
                        </li>
                    </ul>
                    <div v-else class="no-data-wrap" :style="'height:' + todoHeight + 'px;overflow:auto'">
                        <div class="no-data">
                            <a-spin v-if="historyPagination.loading" tip="加载中" />
                            <div v-else> 暂无数据 </div>
                        </div>
                    </div>
                    <vxe-pager
                        :loading="historyPagination.loading"
                        :current-page="historyPagination.current"
                        :page-size="historyPagination.pageSize"
                        :page-sizes="historyPagination.pageSizes"
                        :total="historyPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changeHistoryPaginationFn"
                    ></vxe-pager>
                </a-tab-pane>
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
                        @click="initTablesFn"
                    />
                    <!--刷新按钮区域-->
                </a-tooltip>
            </a-tabs>
        </div>
        <!-- tab切换区域结束 -->

        <!--新增/编辑手术计划区域开始 -->
        <a-modal
            v-model="operationPlanModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped global-drag modal-operation-plan"
            :width="900"
            style="z-index: 8 !important"
            @cancel="operationPlanModalFn('cancel')"
        >
            <div v-globalDrag="{ el: 'modal-operation-plan' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    {{ operationPlanModal.title }}
                </h6>
            </div>
            <div class="operation-modal-body">
                <div class="hospital-name">
                    <template v-if="btnName === 'add'">
                        <span style="font-size: 14px; color: #7b8ba0">医院名称：</span>
                        <div style="display: inline-block; vertical-align: middle; width: 45%">
                            <a-select
                                ref="hospitalNameSelectorRef"
                                size="small"
                                v-model="operationPlanModalObj.hospital_name"
                                @change="hospitalNameChangeFn"
                            >
                                <a-select-option
                                    v-for="item in hospitalNameDatas"
                                    :key="item.key"
                                    :value="item.value"
                                    >{{ item.value }}</a-select-option
                                >
                            </a-select>
                        </div>
                    </template>
                    <h6 v-else class="hospital-name">{{ operationPlanModalObj.hospital_name }}</h6>
                </div>
                <div class="operation-item">
                    <a-row>
                        <a-col :span="6" class="row">
                            <a-row>
                                <a-col :span="9">
                                    <span>计划名称：</span>
                                </a-col>
                                <a-col :span="15">
                                    <a-input
                                        :disabled="isAddEditFlag"
                                        size="small"
                                        v-model="operationPlanModalObj.name"
                                    />
                                </a-col>
                            </a-row>
                        </a-col>
                        <a-col :span="6" class="row">
                            <a-row>
                                <a-col :span="9">
                                    <span>被手术人：</span>
                                </a-col>
                                <a-col :span="15">
                                    <a-input
                                        :disabled="isAddEditFlag"
                                        size="small"
                                        v-model="operationPlanModalObj.patient_name"
                                    />
                                </a-col>
                            </a-row>
                        </a-col>
                        <a-col :span="6" class="row">
                            <a-row>
                                <a-col :span="9">
                                    <span>科室名称：</span>
                                </a-col>
                                <a-col :span="15">
                                    <a-tree-select
                                        :disabled="isAddEditFlag"
                                        :showSearch="false"
                                        :treeData="deptDatas"
                                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                        treeDefaultExpandAll
                                        v-model="operationPlanModalObj.hospital_department_name"
                                        @click="deptClickFn"
                                        @change="deptChangeFn"
                                        class="input"
                                        style="min-width: 130px"
                                    ></a-tree-select>
                                </a-col>
                            </a-row>
                        </a-col>
                        <a-col :span="6" class="row">
                            <a-row>
                                <a-col :span="8">
                                    <span>手术时间：</span>
                                </a-col>
                                <a-col :span="16">
                                    <a-date-picker
                                        :disabled="isAddEditFlag"
                                        size="small"
                                        :allowClear="false"
                                        v-model="operationPlanModalObj.operation_time"
                                        :format="formatOperationPlan"
                                    />
                                </a-col>
                            </a-row>
                        </a-col>
                    </a-row>
                </div>
                <a-button
                    :disabled="isAddEditFlag"
                    @click="addOperationFn"
                    icon="plus"
                    style="height: 24px; padding: 0 4px"
                    >添加手术包</a-button
                >
            </div>
            <vxe-grid
                ref="xTableOperationPlanModalInfo"
                size="small"
                height="232"
                border
                resizable
                auto-resize
                show-overflow
                show-header-overflow
                showHeader
                highlight-current-row
                align="center"
                :data="operationPlanModalDatas"
            >
                <vxe-table-column field="index" title="NO." width="6%" :resizable="false" fixed="left">
                    <template v-slot="{ row, seq }">
                        <div>
                            <i :class="!row.plan_id ? 'red-dot-add' : 'hide'"></i>
                            <span>{{ seq }}</span>
                        </div>
                    </template>
                </vxe-table-column>
                <vxe-table-column
                    field="ois_sn"
                    width="24%"
                    title="编号"
                    :cell-render="{ name: 'ois_sn' }"
                ></vxe-table-column>
                <vxe-table-column
                    field="name"
                    width="15%"
                    title="名称"
                    :cell-render="{ name: 'name' }"
                ></vxe-table-column>
                <vxe-table-column
                    width="10%"
                    field="type_name"
                    title="类别"
                    :cell-render="{ name: 'type_name' }"
                ></vxe-table-column>
                <vxe-table-column
                    field="brand_name"
                    width="15%"
                    title="品牌"
                    :cell-render="{ name: 'brand_name' }"
                ></vxe-table-column>
                <vxe-table-column
                    width="12%"
                    field="out_warehouse_name"
                    title="仓库"
                    :cell-render="{ name: 'out_warehouse_name' }"
                ></vxe-table-column>
                <vxe-table-column field="status" title="状态" width="10%">
                    <template v-slot="{ row }">
                        <div>{{ surgeryPlanPackageStatus[row.status] }}</div>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="action" title="操作" width="8%" :resizable="false" fixed="right">
                    <template v-slot="{ row, seq }">
                        <ul class="action-btn-list-wrap" style="position: relative">
                            <li>
                                <a-tooltip class="tooltip" placement="top" title="查看">
                                    <i
                                        @click="planModalActionBtnFn($event, 'look', row, seq)"
                                        class="yrt yrt-wendangxiugai edit yrt-yanjing"
                                        style="cursor: pointer"
                                    ></i>
                                </a-tooltip>
                                <a-tooltip v-if="!isAddEditFlag" class="tooltip" placement="top" title="删除">
                                    <i
                                        @click="planModalActionBtnFn($event, 'del', row, seq)"
                                        class="yrt yrt-shanchu2 cancel"
                                        style="cursor: pointer"
                                    ></i>
                                </a-tooltip>
                            </li>
                        </ul>
                    </template>
                </vxe-table-column>
                <!-- <template v-slot:empty>
                    <div
                         @dblclick.stop="dblclickTableFn"
                        class="dblclick-wrap">
                        <span>双击添加产品</span>
                    </div>
        </template>-->
            </vxe-grid>
            <div class="operation-item">
                <a-row>
                    <a-col :span="24" style="margin-bottom: 24px">
                        <a-row>
                            <a-col :span="2">
                                <span>备注：</span>
                            </a-col>
                            <a-col :span="22">
                                <a-input
                                    :disabled="isAddEditFlag"
                                    size="small"
                                    v-model="operationPlanModalObj.description"
                                />
                            </a-col>
                        </a-row>
                    </a-col>
                    <a-col :span="6" class="row">
                        <a-row>
                            <a-col :span="9">
                                <span>申请人：</span>
                            </a-col>
                            <a-col :span="15">
                                <span>{{ operationPlanModalObj.creator_user_name }}</span>
                            </a-col>
                        </a-row>
                    </a-col>
                    <a-col :span="6" class="row">
                        <a-row>
                            <a-col :span="9">
                                <span>申请时间：</span>
                            </a-col>
                            <a-col :span="15">
                                <span>{{ operationPlanModalObj.created_time }}</span>
                            </a-col>
                        </a-row>
                    </a-col>
                    <a-col :span="6" class="row"></a-col>
                    <a-col :span="6" class="row">
                        <a-row>
                            <a-col :span="8">
                                <span>手术人：</span>
                            </a-col>
                            <a-col :span="16">
                                <a-tree-select
                                    treeNodeFilterProp="title"
                                    searchPlaceholder=""
                                    :showSearch="true"
                                    :disabled="isAddEditFlag"
                                    v-model="operationPlanModalObj.doctor_name"
                                    style="width: 100%"
                                    :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                                    :tree-data="empDatas"
                                    placeholder="请选择手术人"
                                    tree-default-expand-all
                                    size="small"
                                    @click="empClickFn"
                                    @change="empChangeFn"
                                >
                                </a-tree-select>
                            </a-col>
                        </a-row>
                    </a-col>
                </a-row>
            </div>
            <template slot="footer">
                <a-button key="cancel" @click.stop="operationPlanModalFn('cancel')">{{
                    this.btnName === 'add' ||
                    this.operationPlanModalObj.status === 'DRAFT' ||
                    this.operationPlanModalObj.status === 'CONFIRMED'
                        ? '取消'
                        : '关闭'
                }}</a-button>
                <!-- <a-button
                    v-if="this.operationPlanModalObj.status === 'CONFIRMED'"
                    :loading="operationPlanModal.publishLoading"
                    key="publish"
                    type="primary"
                    @click.stop="operationPlanModalFn('publish')"
        >发布</a-button>-->
                <a-button
                    v-if="
                        this.btnName === 'add' ||
                        this.operationPlanModalObj.status === 'DRAFT' ||
                        this.operationPlanModalObj.status === 'CONFIRMED'
                    "
                    :loading="operationPlanModal.saveLoading"
                    key="save"
                    type="primary"
                    @click.stop="operationPlanModalFn('save')"
                    >保存</a-button
                >
            </template>
        </a-modal>
        <!-- 新增/编辑手术计划区域结束 -->

        <!--添加手术包区域开始 -->
        <a-modal
            v-model="addOperationModal.alert"
            :centered="true"
            :maskClosable="false"
            :mask="false"
            class="scoped modal-form-input-scoped global-drag modal-operation-add"
            :width="900"
            :zIndex="8"
            style="z-index: 8 !important"
        >
            <div v-globalDrag="{ el: 'modal-operation-add' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    {{ addOperationModal.title }}
                </h6>
            </div>
            <div class="operation-modal-body">
                <div class="operation-item">
                    <a-row>
                        <a-col :span="7" class="row">
                            <a-row>
                                <a-col :span="9">
                                    <span>手术包名称：</span>
                                </a-col>
                                <a-col :span="15">
                                    <a-input size="small" v-model="addOperationModalObj.q" />
                                </a-col>
                            </a-row>
                        </a-col>
                        <a-col :span="5" class="row">
                            <a-row>
                                <a-col :span="9">
                                    <span>类别：</span>
                                </a-col>
                                <a-col :span="15">
                                    <a-select size="small" v-model="addOperationModalObj.types" @change="typesChangeFn">
                                        <a-select-option value>全部</a-select-option>
                                        <a-select-option value="0">颅骨类</a-select-option>
                                        <a-select-option value="1">脊柱类</a-select-option>
                                        <a-select-option value="2">关节类</a-select-option>
                                        <a-select-option value="3">创伤类</a-select-option>
                                    </a-select>
                                </a-col>
                            </a-row>
                        </a-col>
                        <a-col :span="5" class="row">
                            <a-row>
                                <a-col :span="9">
                                    <span>品牌：</span>
                                </a-col>
                                <a-col :span="15">
                                    <a-select
                                        :allowClear="true"
                                        size="small"
                                        v-model="addOperationModalObj.brandsText"
                                        @focus="brandsFocusFn"
                                        @change="brandsChangeFn"
                                    >
                                        <a-select-option
                                            v-for="item in brandDatas"
                                            :key="item.key"
                                            :value="item.value"
                                            >{{ item.value }}</a-select-option
                                        >
                                    </a-select>
                                </a-col>
                            </a-row>
                        </a-col>
                        <a-col :span="5" class="row">
                            <a-row>
                                <a-col :span="8">
                                    <span>范围：</span>
                                </a-col>
                                <a-col :span="16">
                                    <a-select
                                        size="small"
                                        v-model="addOperationModalObj.searchScopeText"
                                        @focus="searchScopeFocusFn"
                                        @change="searchScopeChangeFn"
                                    >
                                        <a-select-option
                                            v-for="item in searchScopeDatas"
                                            :key="item.key"
                                            :value="item.value"
                                            >{{ item.value }}</a-select-option
                                        >
                                    </a-select>
                                </a-col>
                            </a-row>
                        </a-col>
                        <a-col :span="2" class="row">
                            <a-button @click="surgeryPlanSupportPackageListAPIFn('search')" type="primary" size="small"
                                >搜索</a-button
                            >
                        </a-col>
                    </a-row>
                </div>
            </div>

            <div class="operation-add-wrap">
                <div class="operation-add-list">
                    <ul v-if="addOperationDatas.length">
                        <li
                            v-for="(addOperationItem, seq) in addOperationDatas"
                            :key="addOperationItem.platform_ois_inner_sn"
                            :class="addOperationItem.is_cust ? 'op_is_cust' : ''"
                            @click="toggleAddOperationFn(seq)"
                        >
                            <div>
                                <p class="brand-name">{{ addOperationItem.brand_name }}</p>
                                <div v-show="addOperationItem.isActive" class="ico_active">
                                    <a-icon type="check-circle" />
                                </div>
                            </div>
                            <p class="name">{{ addOperationItem.name }}</p>
                            <p class="type-name">{{ addOperationItem.type_name }}</p>
                            <p class="ois-sn">{{ addOperationItem.ois_sn }}</p>
                        </li>
                    </ul>
                    <span v-else class="no-data">
                        <a-spin v-if="addOperationPagination.loading" />
                        <span v-else>暂无数据</span>
                    </span>
                </div>
                <vxe-pager
                    size="small"
                    :loading="addOperationPagination.loading"
                    :current-page="addOperationPagination.current"
                    :page-size="addOperationPagination.pageSize"
                    :page-sizes="addOperationPagination.pageSizes"
                    :total="addOperationPagination.total"
                    :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                    @page-change="changeAddOperationPaginationFn"
                ></vxe-pager>
            </div>

            <template slot="footer">
                <a-button key="cancel" @click.stop="addOperationModalFn('cancel')">取消</a-button>
                <a-button
                    :loading="addOperationModal.loading"
                    key="save"
                    type="primary"
                    @click.stop="addOperationModalFn('add')"
                    >添加</a-button
                >
            </template>
        </a-modal>
        <!-- 添加手术包区域结束 -->

        <!-- 编辑手术包区域开始 -->
        <a-modal
            title="修改"
            :visible="editMd.alert"
            :width="1120"
            okText="保存"
            :maskClosable="false"
            :mask="false"
            :centered="true"
            @cancel="editMd.alert = false"
            class="modal-form-input-scoped global-drag edit-contract-md"
        >
            <div v-globalDrag="{ el: 'edit-contract-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    {{ editMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="editMd.alert = false">取消</a-button>
                <a-button :loading="editMd.loading" type="primary" @click="sendEditBack">保存</a-button>
            </template>
            <edits-op-kits
                ref="editOpMd"
                @returnData="editOpFn"
                :partType="editPartType"
                :editObj="editObj"
            ></edits-op-kits>
        </a-modal>
        <!-- 编辑手术包区域结束 -->

        <!-- 查看手术包区域开始-->
        <a-modal
            title="查看"
            :visible="customMd.alert"
            :width="1120"
            okText="保存"
            :maskClosable="false"
            :mask="false"
            :centered="true"
            @cancel="customMd.alert = false"
            class="modal-form-input-scoped global-drag custom-md"
        >
            <div v-globalDrag="{ el: 'custom-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    {{ customMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="customMd.alert = false">关闭</a-button>
            </template>
            <look-op-kits ref="customMd" :editObj="lookOpKitsPlanObj"></look-op-kits>
        </a-modal>
        <!-- 查看手术包区域结束-->

        <!-- 备货 - 修改仓库名称区域开始 -->
        <a-modal
            title="修改"
            :visible="lock.alert"
            :width="400"
            okText="备货"
            :maskClosable="false"
            :centered="true"
            @cancel="lock.alert = false"
            class="modal-form-input-scoped global-drag edit-lock"
        >
            <div v-globalDrag="{ el: 'edit-lock' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    {{ lock.title }}
                </h6>
            </div>
            <div>
                <a-row style="margin-bottom: 16px">
                    <a-col :span="8">
                        <span>仓库：</span>
                    </a-col>
                    <a-col :span="16">
                        <a-tree-select
                            treeNodeFilterProp="title"
                            :showSearch="true"
                            v-model="operationPlanPackageObj.out_warehouse_inner_sn"
                            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                            :tree-data="warehouseUsableTreeDatas"
                            placeholder="请选择仓库"
                            tree-default-expand-all
                            size="small"
                            @change="warehouseTreeChangeFn"
                        ></a-tree-select>
                    </a-col>
                </a-row>
                <a-row>
                    <a-col :span="8">
                        <span>骨科助理：</span>
                    </a-col>
                    <a-col :span="16">
                        <span>{{ this.employeeInfo.Name || '--' }}</span>
                    </a-col>
                </a-row>
            </div>
            <template slot="footer">
                <a-button @click="lock.alert = false">取消</a-button>
                <a-button :loading="lock.loading" type="primary" @click="lockFn">备货</a-button>
            </template>
        </a-modal>
        <!-- 备货 - 修改仓库名称区域结束 -->

        <!--点击 【填报销售和损耗 / 查看损耗 / 确认出单】 按钮弹框区域开始 -->
        <a-modal
            v-model="identifySalesAndLossesModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped global-drag modal-identify-sales-losses"
            :width="1100"
            :zIndex="8"
            style="z-index: 8 !important"
        >
            <div v-globalDrag="{ el: 'modal-identify-sales-losses' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    {{ identifySalesAndLossesModal.title }}
                </h6>
            </div>
            <div class="operation-modal-body">
                <div class="operation-item">
                    <ul>
                        <li>
                            <span>名称：</span>
                            <span>{{ packagesInfoObj.name || '--' }}</span>
                        </li>
                        <li>
                            <span>编号：</span>
                            <span>{{ packagesInfoObj.ois_sn || '--' }}</span>
                        </li>
                        <li>
                            <span>类别：</span>
                            <span>{{ packagesInfoObj.type_name || '--' }}</span>
                        </li>
                        <li>
                            <span>品牌：</span>
                            <span>{{ packagesInfoObj.brand_name || '--' }}</span>
                        </li>
                        <li>
                            <span>仓库：</span>
                            <span>{{ packagesInfoObj.out_warehouse_name || '--' }}</span>
                        </li>
                    </ul>
                </div>
            </div>
            <vxe-table
                ref="xIdentifySalesAndLossesTable"
                height="368"
                size="small"
                border
                resizable
                auto-resize
                highlight-current-row
                align="center"
                :data="identifySalesAndLossesDatas"
                :loading="identifySalesAndLossesModal.tableLoading"
                show-overflow
                show-header-overflow
                showHeader
            >
                <vxe-table-column type="seq" title="NO." width="6%" show-overflow></vxe-table-column>
                <vxe-table-column
                    field="product_name"
                    title="产品"
                    :width="packagesInfoObj.status !== 'BACK' ? '16%' : '12%'"
                    show-overflow
                ></vxe-table-column>
                <vxe-table-column
                    field="specification_vendor_sn"
                    title="产品编码"
                    :width="packagesInfoObj.status !== 'BACK' ? '11%' : '9%'"
                    show-overflow
                ></vxe-table-column>
                <vxe-table-column
                    field="specification"
                    title="规格型号"
                    :width="packagesInfoObj.status !== 'BACK' ? '12%' : '8%'"
                    show-overflow
                ></vxe-table-column>
                <vxe-table-column
                    field="product_vendor_name"
                    title="厂商"
                    :width="packagesInfoObj.status !== 'BACK' ? '12%' : '10%'"
                    show-overflow
                ></vxe-table-column>
                <vxe-table-column field="quantity" title="出库数量" width="8%" show-overflow></vxe-table-column>
                <vxe-table-column field="back_quantity" title="回库数量" width="8%" show-overflow></vxe-table-column>
                <vxe-table-column
                    field="sale_quantity"
                    title="销售数量"
                    :width="packagesInfoObj.status !== 'BACK' ? '8%' : '12%'"
                    show-overflow
                >
                    <template v-slot="{ row }">
                        <div v-if="packagesInfoObj.status === 'BACK'">
                            <a-input-number
                                size="small"
                                v-model="row.sale_quantity"
                                type="number"
                                :min="0"
                                placeholder=""
                            />
                        </div>
                        <div v-else>
                            {{ row.sale_quantity }}
                        </div>
                    </template>
                </vxe-table-column>
                <vxe-table-column
                    field="loss_quantity"
                    title="损耗数量"
                    :width="packagesInfoObj.status !== 'BACK' ? '8%' : '12%'"
                    show-overflow
                >
                    <template v-slot="{ row }">
                        <div v-if="packagesInfoObj.status === 'BACK'">
                            <a-input-number
                                size="small"
                                v-model="row.loss_quantity"
                                type="number"
                                :min="0"
                                placeholder=""
                            />
                        </div>
                        <div v-else>
                            {{ row.loss_quantity }}
                        </div>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="unit" title="包装单位" width="8%" show-overflow></vxe-table-column>
                <vxe-table-column
                    field="unit_price"
                    title="单价"
                    :width="packagesInfoObj.status !== 'BACK' ? '8%' : '12%'"
                >
                    <template v-slot="{ row }">
                        <div v-if="packagesInfoObj.status === 'BACK'">
                            <a-input-number
                                size="small"
                                v-model="row.unit_price"
                                type="number"
                                :min="0"
                                :formatter="(value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                placeholder=""
                            />
                        </div>
                        <div v-else>
                            {{ row.unit_price }}
                        </div>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="loss_reason" title="损耗原因" width="11%" show-overflow>
                    <template v-slot="{ row }">
                        <div v-if="packagesInfoObj.status === 'BACK'">
                            <a-input
                                size="small"
                                v-model="row.loss_reason"
                                type="text"
                                :max="100"
                                :min="0"
                                placeholder=""
                            />
                        </div>
                        <div v-else>
                            {{ row.loss_reason || '--' }}
                        </div>
                    </template>
                </vxe-table-column>
            </vxe-table>
            <template slot="footer">
                <a-button key="cancel" @click.stop="identifySalesAndLossesModalFn('CANCEL')">{{
                    packagesInfoObj.status === 'SALE_PENDING' ? '关闭' : '取消'
                }}</a-button>
                <a-button
                    v-if="packagesInfoObj.status === 'BACK'"
                    :loading="identifySalesAndLossesModal.confirmLoading"
                    key="save"
                    type="primary"
                    @click.stop="identifySalesAndLossesModalFn('BACK')"
                    >确认</a-button
                >
                <a-button
                    v-else-if="packagesInfoObj.status === 'SALE_CONFIRMED'"
                    :loading="identifySalesAndLossesModal.confirmLoading"
                    key="save"
                    type="primary"
                    @click.stop="identifySalesAndLossesModalFn('SALE_CONFIRMED')"
                    >确认</a-button
                >
            </template>
        </a-modal>
        <!-- 点击 【填报销售和损耗 / 查看损耗 / 确认出单】 按钮弹框区域结束 -->
    </div>
</template>

<script>
import moment from 'moment';

import {
    surgeryPlanListAllAPI,
    surgeryPlanListPackageAPI,
    platformIntentionAvailableHospitalAPI,
    orgEmpTopTreeAPI,
    orgDeptAllTreeAPI,
    surgeryPlanSupportPackageListAPI,
    surgeryCustDictSearchScopeAPI,
    platformSurgeryBrandListAPI,
    surgeryCustDealerPackageGetAPI,
    surgeryCustDealerPackageGetPlatformPackageAPI,
    surgeryPlanSaveAllAPI,
    surgeryPlanDictPackageStatusAPI,
    surgeryPlanDictPlanStatusAPI,
    surgeryPlanPublishAPI,
    surgeryPlanDelAPI,
    surgeryCustDealePackageGetPlatformKitAPI,
    surgeryCustDealerKitGetAPI,
    surgeryPlanPackageLockAPI,
    surgeryPlanPackageDelAPI,
    warehouseUsableTopListAPI,
    surgeryPlanPackageGetAllAPI,
    surgeryPlanPackagePendingSaleAPI,
    surgeryPlanPackageFinishAPI
} from '@/service/pageAjax';

import editsOpKits from './module/editOpkits';
import lookOpKits from './module/lookOpKits';

export default {
    name: 'operationPlan',
    data() {
        return {
            format: 'YYYY-MM-DD HH:mm:ss',
            formatOperationPlan: 'YYYY-MM-DD',
            oid: this.$cookie.get('userbelong'),
            userInfo: JSON.parse(this.$cookie.get('UserInfo')),
            employeeInfo: JSON.parse(this.$cookie.get('EmployeeInfo')),
            userSystemType: parseInt(this.$cookie.get('userSystemType')),
            editMd: {
                title: `${this.$route.meta.title} - 手术包 - 修改`,
                alert: false,
                loading: false
            },
            customMd: {
                title: `${this.$route.meta.title} - 手术包 - 查看`,
                alert: false,
                loading: false
            },
            lock: {
                title: `${this.$route.meta.title} - 手术包 - 备货`,
                alert: false,
                loading: false
            },
            editObj: {},
            lookOpKitsPlanObj: {},
            searchObj: {
                hospitalName: '',
                operationTimeFrom: '',
                operationTimeTo: '',
                packageStatuses: [],
                q: '',
                statuses: [],
                types: '',
                operationTime: ''
            },
            hightSearchObj: {
                isArrow: false
            },
            typeDatas: [],
            brandDatas: [],
            tabTypes: ['todo', 'toSales', 'history'],
            tabChoose: 'todo',
            todoColumns: [
                {
                    title: 'NO.',
                    type: 'seq',
                    align: 'center',
                    width: '6%'
                },
                {
                    title: '编号',
                    field: 'ois_sn',
                    align: 'center',
                    width: '24%'
                },
                {
                    title: '名称',
                    field: 'name',
                    align: 'center',
                    width: '20%'
                },
                {
                    title: '类别',
                    field: 'type_name',
                    align: 'center',
                    width: '15%'
                },
                {
                    title: '品牌',
                    field: 'brand_name',
                    align: 'center',
                    width: '12%'
                },
                {
                    title: '状态',
                    field: 'status',
                    align: 'center',
                    width: '8%',
                    slots: {
                        default: ({ row }) => {
                            return [<span>{this.surgeryPlanPackageStatus[row.status]}</span>];
                        }
                    }
                },
                {
                    title: '操作',
                    field: 'action',
                    align: 'center',
                    width: '15%',
                    slots: {
                        default: (row) => {
                            const _row = row.row;
                            return [
                                <a-button
                                    disabled={!(_row.parentStatus === 'DRAFT' || _row.parentStatus === 'CONFIRMED')}
                                    style="margin: 0 4px"
                                    size="small"
                                    onClick={() => this.todoRowBtnFn('del', row)}
                                    type="danger"
                                >
                                    删除
                                </a-button>,
                                <a-button
                                    disabled={!(_row.parentStatus === 'PUBLISHED' && _row.status === 'NORMAL')}
                                    style="margin: 0 4px"
                                    size="small"
                                    onClick={() => this.todoRowBtnFn('package-lock', row)}
                                    type="primary"
                                >
                                    备货
                                </a-button>
                            ];
                        }
                    }
                }
            ],
            todoDatas: [],
            todoPagination: {
                isLoad: false,
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            toSalesDatas: [],
            toSalesPagination: {
                isLoad: false,
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            historyColumns: [
                {
                    title: 'NO.',
                    type: 'seq',
                    align: 'center',
                    width: '6%'
                },
                {
                    title: '编号',
                    field: 'ois_sn',
                    align: 'center',
                    width: '30%'
                },
                {
                    title: '名称',
                    field: 'name',
                    align: 'center',
                    width: '25%'
                },
                {
                    title: '类别',
                    field: 'type_name',
                    align: 'center',
                    width: '18%'
                },
                {
                    title: '品牌',
                    field: 'brand_name',
                    align: 'center',
                    width: '13%'
                },
                {
                    title: '状态',
                    field: 'status',
                    align: 'center',
                    width: '8%',
                    slots: {
                        default: ({ row }) => {
                            return [<span>{this.surgeryPlanPackageStatus[row.status]}</span>];
                        }
                    }
                }
            ],
            historyDatas: [],
            historyPagination: {
                isLoad: false,
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            addOperationDatas: [],
            addOperationPagination: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            operationPlanModalDatas: [],
            operationPlanModal: {
                alert: false,
                title: '手术计划-新增',
                saveLoading: false,
                publishLoading: false
            },
            identifySalesAndLossesModal: {
                alert: false,
                title: '手术计划-填报销售和损耗',
                confirmLoading: false,
                tableLoading: false
            },
            identifySalesAndLossesDatas: [],
            packagesInfoObj: {
                name: '',
                ois_sn: '',
                type_name: '',
                brand_name: ''
            },
            materialPackageDetails: [],
            toolPackageDetails: [],
            addOperationModal: {
                alert: false,
                title: '手术计划-添加手术包',
                loading: false
            },
            addOperationModalObj: {
                brandsText: '',
                brands: '',
                // 'ois_sn': '',
                // 'op_inner_sn': 0,
                q: '',
                searchScopeText: '全部',
                searchScope: 'ALL',
                types: ''
            },
            operationPlanModalObj: {
                created_time: '',
                creator_org_inner_sn: '',
                creator_org_type: '',
                creator_user_inner_sn: '',
                creator_user_name: '',
                description: '',
                doctor_employee_inner_sn: '',
                doctor_name: '',
                hospital_department_inner_sn: '',
                hospital_department_name: '',
                hospital_inner_sn: '',
                hospital_name: '',
                // id: '',
                // is_delete: '',
                // keywords: '',
                // last_modified_time: '',
                // name: '',
                operation_time: '',
                patient_name: ''
            },
            hospitalNameDatas: [],
            deptDatas: [],
            empDatas: [],
            searchScopeDatas: [],
            surgeryPlanPackageStatus: {},
            surgeryPlanStatus: {},
            surgeryPlanStatusDatas: [],
            btnName: 'add',
            kits: [],
            warehouseUsableTreeDatas: [],
            operationPlanPackageObj: {
                out_warehouse_name: '',
                out_warehouse_inner_sn: ''
            },
            currentTrObj: {}
        };
    },
    methods: {
        moment,
        async surgeryPlanListAllAPIFn(paginationObj) {
            if (this.tabChoose === this.tabTypes[0]) {
                this.todoDatas = [];
            } else {
                this.historyDatas = [];
            }
            paginationObj.loading = true;
            await surgeryPlanListAllAPI(
                {
                    hospitalName: this.searchObj.hospitalName,
                    q: this.searchObj.q,
                    operationTimeFrom: this.searchObj.operationTimeFrom
                        ? `${this.searchObj.operationTimeFrom} 00:00:00`
                        : '',
                    operationTimeTo: this.searchObj.operationTimeTo ? `${this.searchObj.operationTimeTo} 23:59:59` : '',
                    types: this.searchObj.types ? [this.searchObj.types] : [],
                    statuses:
                        this.tabChoose === this.tabTypes[0]
                            ? ['DRAFT', 'CONFIRMED', 'PUBLISHED']
                            : ['CLOSED', 'PUBLISHED'],
                    packageStatuses: this.tabChoose === this.tabTypes[0] ? [] : ['CLOSED']
                },
                paginationObj.current,
                paginationObj.pageSize
            )
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res,
                            datas = rows.map((item) => {
                                return {
                                    ...item,
                                    key: item.id,
                                    details: item.details.map((cItem) => {
                                        cItem.parentStatus = item.status;
                                        return cItem;
                                    })
                                };
                            });
                        if (this.tabChoose === this.tabTypes[0]) {
                            this.todoDatas = datas;
                            paginationObj.total = total;
                        } else {
                            this.historyDatas = datas;
                            paginationObj.isLoad = true;
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    paginationObj.loading = false;
                });
        },
        async surgeryPlanListPackageAPIFn() {
            this.toSalesPagination.loading = true;
            await surgeryPlanListPackageAPI(
                {
                    hospitalName: this.searchObj.hospitalName,
                    q: this.searchObj.q,
                    operationTimeFrom: this.searchObj.operationTimeFrom
                        ? `${this.searchObj.operationTimeFrom} 00:00:00`
                        : '',
                    operationTimeTo: this.searchObj.operationTimeTo ? `${this.searchObj.operationTimeTo} 23:59:59` : '',
                    types: this.searchObj.types ? [this.searchObj.types] : [],
                    statuses: [],
                    packageStatuses: ['BACK', 'SALE_PENDING', 'SALE_CONFIRMED']
                },
                this.toSalesPagination.current,
                this.toSalesPagination.pageSize
            )
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.toSalesDatas = rows;
                        this.toSalesPagination.total = total;
                        this.$refs.xToSalesTable && this.$refs.xToSalesTable.refreshColumn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.toSalesPagination.loading = false;
                    this.toSalesPagination.isLoad = true;
                });
        },
        async platformIntentionAvailableHospitalAPIFn() {
            await platformIntentionAvailableHospitalAPI(this.oid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.hospitalNameDatas = res.list;
                    this.operationPlanModalObj.hospital_inner_sn = '';
                    this.operationPlanModalObj.hospital_name = '';
                    // if (this.hospitalNameDatas.length) {
                    //     this.operationPlanModalObj.hospital_inner_sn = this.hospitalNameDatas[0].key
                    //     this.operationPlanModalObj.hospital_name = this.hospitalNameDatas[0].value
                    //     this.orgEmpTopTreeAPIFn()
                    //     this.orgDeptAllTreeAPIFn()
                    // } else {
                    //     this.operationPlanModalObj.hospital_inner_sn = ''
                    //     this.operationPlanModalObj.hospital_name = ''
                    // }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async orgEmpTopTreeAPIFn() {
            await orgEmpTopTreeAPI(1, this.operationPlanModalObj.hospital_inner_sn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    if (list.length) {
                        this.empDatas = this.formatAttributeFn(list, 'employee_inner_sn', 'name');
                        this.operationPlanModalObj.doctor_name = this.empDatas[0].title;
                        this.operationPlanModalObj.doctor_employee_inner_sn = this.empDatas[0].key;
                    } else {
                        this.empDatas = [];
                        this.operationPlanModalObj.doctor_name = '';
                        this.operationPlanModalObj.doctor_employee_inner_sn = '';
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async orgDeptAllTreeAPIFn() {
            await orgDeptAllTreeAPI(1, this.operationPlanModalObj.hospital_inner_sn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    if (list.length) {
                        this.deptDatas = this.formatAttributeFn(list, 'department_inner_sn', 'name');
                        this.operationPlanModalObj.hospital_department_inner_sn = this.deptDatas[0].key;
                        this.operationPlanModalObj.hospital_department_name = this.deptDatas[0].title;
                    } else {
                        this.operationPlanModalObj.hospital_department_inner_sn = '';
                        this.operationPlanModalObj.hospital_department_name = '';
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async surgeryPlanSupportPackageListAPIFn(actionTypeName) {
            this.addOperationDatas = [];
            this.addOperationPagination.loading = true;
            if (actionTypeName === 'search') {
                this.addOperationPagination.current = 1;
            }
            await surgeryPlanSupportPackageListAPI(
                this.operationPlanModalObj.hospital_inner_sn,
                this.oid,
                {
                    brands: this.addOperationModalObj.brands ? [this.addOperationModalObj.brands] : [],
                    q: this.addOperationModalObj.q,
                    searchScope: this.addOperationModalObj.searchScope,
                    types: this.addOperationModalObj.types ? [this.addOperationModalObj.types] : []
                },
                this.addOperationPagination.current,
                this.addOperationPagination.pageSize
            )
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.addOperationDatas = rows.map((item) => {
                            return {
                                ...item,
                                isActive: false
                            };
                        });
                        this.refreshColumnEditModalFn();
                        this.addOperationPagination.total = total;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.addOperationPagination.loading = false;
                });
        },
        async surgeryCustDictSearchScopeAPIFn() {
            await surgeryCustDictSearchScopeAPI()
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.searchScopeDatas = this.formatAttributeFn(res.list, 'text', 'name');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        async platformSurgeryBrandListAPIFn() {
            await platformSurgeryBrandListAPI()
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.brandDatas = this.formatAttributeFn(res.list, 'name', 'brand_inner_sn');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        async surgeryCustDealerPackageGetAPIFn(id) {
            await surgeryCustDealerPackageGetAPI(id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.getOperationPlanModalDataFn(res.info);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        async surgeryCustDealerPackageGetPlatformPackageAPIFn(platformOisId) {
            await surgeryCustDealerPackageGetPlatformPackageAPI(platformOisId)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.getOperationPlanModalDataFn(res.info);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        async surgeryCustDealerKitGetAPIFn(id, kits) {
            await surgeryCustDealerKitGetAPI(id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.getOperationKitDetailDataFn(res.info, kits);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        async surgeryCustDealePackageGetPlatformKitAPIFn(platformOisId, kits) {
            await surgeryCustDealePackageGetPlatformKitAPI(platformOisId)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.getOperationKitDetailDataFn(res.info, kits);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        async surgeryPlanSaveAllAPIFn() {
            this.operationPlanModal.saveLoading = true;
            let params = {
                ...this.operationPlanModalObj,
                operation_time: moment(this.operationPlanModalObj.operation_time).format(this.format),
                details: this.operationPlanModalDatas.map((item) => {
                    delete item.owner_dealer_inner_sn;
                    delete item.owner_dealer_name;
                    delete item._XID;
                    if (this.userSystemType === 2) {
                        // 机构类型(system_type)：0-平台、 1-医院 、 2-经销商、 3-厂商 经销商创建手术计划
                        item.dealer_inner_sn = this.oid;
                        item.dealer_name = this.employeeInfo.OrgName;
                    }
                    return item;
                })
            };

            await surgeryPlanSaveAllAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;

                        info.details = info.details.map((item) => {
                            return {
                                ...item,
                                parentStatus: this.currentTrObj.status
                            };
                        });
                        info.key = info.id || this.currentTrObj.id;

                        if (this.btnName === 'edit') {
                            const parentSeq = this.currentTrObj.seq,
                                seq = this.editObj.seq - 1;
                            this.todoDatas[parentSeq] = { ...info };
                            this.todoDatas[parentSeq].hospital_name = info.hospital_name;
                            this.todoDatas[parentSeq].name = info.name;
                            this.todoDatas[parentSeq].operation_time = info.operation_time;
                            this.todoDatas[parentSeq].status = this.currentTrObj.status;
                            this.todoDatas[parentSeq].details = info.details;
                            this.todoDatas[parentSeq].details[seq].details = info.details[seq].details;
                            this.todoDatas[parentSeq].details[seq].kits = info.details[seq].kits;
                        } else {
                            // 新增 this.surgeryPlanListAllAPIFn(this.todoPagination)
                            this.todoDatas.unshift(info);
                            this.todoPagination.total += 1;
                            if (this.todoDatas.length > this.todoPagination.pageSize) {
                                this.todoDatas.splice(this.todoDatas.length - 1, 1);
                            }
                        }
                        this.editObj.details_b = [];
                        this.editObj.kits_b = [];
                        this.editObj.details = [];
                        this.editObj.kits = [];
                        this.operationPlanModal.alert = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.operationPlanModal.saveLoading = false;
                });
        },
        async warehouseUsableTopListAPIFn() {
            await warehouseUsableTopListAPI()
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.warehouseUsableTreeDatas = this.formatAttributeFn(
                            list,
                            'warehouse_model_inner_sn',
                            'name'
                        );
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        async surgeryPlanPackageDelAPIFn(todoIndex, row) {
            this.todoPagination.loading = true;
            await surgeryPlanPackageDelAPI(row.row.id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.todoDatas[todoIndex].details.splice(row.seq - 1, 1);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.todoPagination.loading = false;
                });
        },
        async surgeryPlanPackageLockAPIFn() {
            this.todoPagination.loading = true;
            let todoIndex = this.operationPlanPackageObj.todoIndex,
                row = this.operationPlanPackageObj.row,
                params = {
                    ...row.row,
                    assistant_employee_inner_sn: this.employeeInfo.Employee_inner_sn,
                    assistant_employee_name: this.employeeInfo.Name,
                    out_warehouse_inner_sn: this.operationPlanPackageObj.out_warehouse_inner_sn,
                    out_warehouse_name: this.operationPlanPackageObj.out_warehouse_name
                };
            delete params.details;
            delete params.kits;
            delete params._XID;
            await surgeryPlanPackageLockAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.lock.alert = false;
                        this.todoDatas[todoIndex].details[row.seq - 1].status = 'LOCKED';
                        this.todoDatas[todoIndex].details[row.seq - 1].out_warehouse_inner_sn =
                            this.operationPlanPackageObj.out_warehouse_inner_sn;
                        this.todoDatas[todoIndex].details[row.seq - 1].out_warehouse_name =
                            this.operationPlanPackageObj.out_warehouse_name;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.todoPagination.loading = false;
                });
        },
        async surgeryPlanPublishAPIFn(id, todoIndex) {
            this.todoPagination.loading = true;
            await surgeryPlanPublishAPI(id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.todoDatas[todoIndex].status = 'PUBLISHED';
                        this.todoDatas[todoIndex].parentStatus = 'PUBLISHED';
                        this.todoDatas[todoIndex].details.forEach((item) => {
                            item.parentStatus = 'PUBLISHED';
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.todoPagination.loading = false;
                });
        },
        async surgeryPlanDelAPIFn(id) {
            this.todoPagination.loading = true;
            await surgeryPlanDelAPI(id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.surgeryPlanListAllAPIFn(this.todoPagination);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.todoPagination.loading = false;
                });
        },
        async surgeryPlanDictPackageStatusAPIFn() {
            await surgeryPlanDictPackageStatusAPI()
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        for (let i = 0; i < list.length; i++) {
                            let item = list[i];
                            this.surgeryPlanPackageStatus[item.name] = item.text;
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        async surgeryPlanDictPlanStatusAPIFn() {
            await surgeryPlanDictPlanStatusAPI()
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.surgeryPlanStatusDatas = list.map((item) => {
                            this.surgeryPlanStatus[item.name] = item.text;
                            return {
                                ...item,
                                key: item.name,
                                value: item.name,
                                title: item.text
                            };
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        async surgeryPlanPackageGetAllAPIFn(id, seq) {
            this.identifySalesAndLossesModal.tableLoading = true;
            await surgeryPlanPackageGetAllAPI(id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let { info } = res,
                            kits = info.kits;
                        this.materialPackageDetails = [];
                        this.toolPackageDetails = [];
                        for (let i = 0; i < info.details.length; i++) {
                            let item = info.details[i];
                            if (item.quantity === item.back_quantity) {
                                // 出库数量等于回库数量前端不显示该条记录，传给后端的值用0代替
                                this.materialPackageDetails.push(item);
                                continue;
                            }
                            this.identifySalesAndLossesDatas.push(item);
                        }
                        for (let i = 0; i < kits.length; i++) {
                            let item = kits[i];
                            if (item.details && item.details.length) {
                                for (let j = 0; j < item.details.length; j++) {
                                    let cItem = item.details[j];
                                    if (cItem.quantity === cItem.back_quantity) {
                                        // 出库数量等于回库数量前端不显示该条记录，传给后端的值用0代替
                                        this.toolPackageDetails.push(cItem);
                                        continue;
                                    }
                                    this.identifySalesAndLossesDatas.push(cItem);
                                }
                            }
                        }
                        this.$refs.xIdentifySalesAndLossesTable &&
                            this.$refs.xIdentifySalesAndLossesTable.refreshColumn();
                        this.packagesInfoObj = {
                            ...info,
                            name: info.name,
                            ois_sn: info.ois_sn,
                            type_name: info.type_name,
                            brand_name: info.brand_name,
                            seq
                        };
                        info = null;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.identifySalesAndLossesModal.tableLoading = false;
                });
        },
        async surgeryPlanPackagePendingSaleAPIFn(params) {
            this.identifySalesAndLossesModal.confirmLoading = true;
            await surgeryPlanPackagePendingSaleAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.identifySalesAndLossesModalFn('CANCEL');
                        this.toSalesDatas[this.packagesInfoObj.seq - 1].status = 'SALE_PENDING';
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.identifySalesAndLossesModal.confirmLoading = false;
                });
        },
        async surgeryPlanPackageFinishAPIFn() {
            this.identifySalesAndLossesModal.confirmLoading = true;
            await surgeryPlanPackageFinishAPI(this.packagesInfoObj.id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.identifySalesAndLossesModalFn('CANCEL');
                        this.toSalesDatas.splice(this.packagesInfoObj.seq - 1, 1);
                        this.surgeryPlanListAllAPIFn(this.historyPagination);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.identifySalesAndLossesModal.confirmLoading = false;
                });
        },
        changeOperationTimeFn(value, dateString) {
            const [beginTime, endTime] = dateString;
            this.searchObj = {
                ...this.searchObj,
                operationTimeFrom: beginTime,
                operationTimeTo: endTime
            };
        },
        refreshColumnEditModalFn() {
            this.$nextTick(() => {
                this.$refs.xTableOperationPlanModalInfo.refreshColumn();
            });
        },
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        changeTabFn(activeKey) {
            this.tabChoose = activeKey;
            if (this.tabChoose === this.tabTypes[1] && !this.toSalesPagination.isLoad) {
                this.surgeryPlanListPackageAPIFn();
                return;
            }
            if (this.tabChoose === this.tabTypes[2] && !this.historyPagination.isLoad) {
                this.surgeryPlanListAllAPIFn(this.historyPagination);
            }
        },
        groupBtnFn(actionTypeName, todo, todoIndex) {
            this.btnName = actionTypeName;
            this.currentTrObj = {
                ...todo,
                seq: todoIndex
            };
            this.operationPlanModalDatas = [];
            const _this = this,
                actionTypeFn = {
                    add: () => {
                        const date = Date();
                        this.operationPlanModalObj = {
                            created_time: moment(date).format(this.format),
                            creator_org_inner_sn: this.oid,
                            creator_org_type: this.userSystemType,
                            creator_user_inner_sn: this.userInfo.User_inner_sn,
                            creator_user_name: this.employeeInfo.Name,
                            description: '',
                            doctor_employee_inner_sn: '',
                            doctor_name: '',
                            hospital_department_inner_sn: '',
                            hospital_department_name: '',
                            hospital_inner_sn: '',
                            hospital_name: '',
                            operation_time: '',
                            patient_name: ''
                        };
                        this.operationPlanModalObj.operation_time = moment(date);
                        this.platformIntentionAvailableHospitalAPIFn();
                        this.operationPlanModal.title = `${this.$route.meta.title} - 新增`;
                        this.operationPlanModal.alert = true;
                        this.$Utils.globalDragCenterFn('modal-operation-plan');
                        // this.hackShowSelectorPlaceholder()
                    },
                    edit: () => {
                        this.operationPlanModal.title = `${this.$route.meta.title} - 修改`;
                        this.operationPlanModalObj = {
                            ...todo
                        };
                        this.operationPlanModalObj.status = todo.status;
                        this.operationPlanModalObj.operation_time = moment(todo.operation_time.split(' ')[0]);
                        this.operationPlanModalDatas = [...todo.details];
                        this.operationPlanModal.alert = true;
                        this.$Utils.globalDragCenterFn('modal-operation-plan');
                    },
                    del: () => {
                        if (todo.status === 'DRAFT' || todo.status === 'CONFIRMED') {
                            _this.$confirm({
                                title: '操作提示',
                                content: '您确定要删除吗？',
                                okText: '删除',
                                cancelText: '取消',
                                centered: true,
                                maskClosable: false,
                                onOk: () => {
                                    _this.surgeryPlanDelAPIFn(todo.id);
                                }
                            });
                        }
                    },
                    publish: () => {
                        _this.$confirm({
                            title: '操作提示',
                            content: '您确定要发布吗？',
                            okText: '发布',
                            cancelText: '取消',
                            centered: true,
                            maskClosable: false,
                            onOk: () => {
                                _this.surgeryPlanPublishAPIFn(todo.id, todoIndex);
                            }
                        });
                    }
                };

            actionTypeFn[actionTypeName]();
        },
        changeTodoPaginationFn({ currentPage, pageSize }) {
            this.todoPagination.current = currentPage;
            this.todoPagination.pageSize = pageSize;
            this.surgeryPlanListAllAPIFn(this.todoPagination);
        },
        changeToSalesPaginationFn({ currentPage, pageSize }) {
            this.toSalesPagination.current = currentPage;
            this.toSalesPagination.pageSize = pageSize;
            this.surgeryPlanListPackageAPIFn();
        },
        changeHistoryPaginationFn({ currentPage, pageSize }) {
            this.historyPagination.current = currentPage;
            this.historyPagination.pageSize = pageSize;
            this.surgeryPlanListAllAPIFn(this.historyPagination);
        },
        changeAddOperationPaginationFn({ currentPage, pageSize }) {
            this.addOperationPagination.current = currentPage;
            this.addOperationPagination.pageSize = pageSize;
            this.addOperationPagination.loading = true;
            this.surgeryPlanSupportPackageListAPIFn();
        },
        initTablesFn() {
            const loadSurgeryPlanList = {
                todo: () => {
                    this.todoDatas = [];
                    this.todoPagination.current = 1;
                    this.todoPagination.pageSize = 10;
                    this.todoPagination.pageNum = 1;
                    this.todoPagination.isLoad = true;
                    this.surgeryPlanListAllAPIFn(this.todoPagination);
                },
                toSales: () => {
                    this.toSalesDatas = [];
                    this.searchObj.statuses = ['DRAFT', 'CONFIRMED', 'PUBLISHED'];
                    this.toSalesPagination.current = 1;
                    this.toSalesPagination.pageSize = 10;
                    this.toSalesPagination.pageNum = 1;
                    this.surgeryPlanListPackageAPIFn();
                },
                history: () => {
                    this.historyDatas = [];
                    this.historyPagination.current = 1;
                    this.historyPagination.pageSize = 10;
                    this.historyPagination.pageNum = 1;
                    this.surgeryPlanListAllAPIFn(this.historyPagination);
                }
            };
            loadSurgeryPlanList[this.tabChoose]();
        },
        operationPlanModalFn(actionTypeName) {
            const surgeryPlanSaveAll = {
                cancel: () => {
                    this.operationPlanModal.alert = false;

                    this.operationPlanModalDatas = [];
                    this.empDatas = [];
                    this.operationPlanModalObj = {
                        created_time: '',
                        creator_org_inner_sn: '',
                        creator_org_type: '',
                        creator_user_inner_sn: '',
                        creator_user_name: '',
                        description: '',
                        doctor_employee_inner_sn: '',
                        doctor_name: '',
                        hospital_department_inner_sn: '',
                        hospital_department_name: '',
                        hospital_inner_sn: '',
                        hospital_name: '',
                        operation_time: '',
                        patient_name: ''
                    };

                    if (this.editObj.details_b && (this.editObj.details_b.length || this.editObj.kits_b.length)) {
                        this.editObj.details_b = [];
                        this.editObj.kits_b = [];
                        const parentSeq = this.currentTrObj.seq,
                            seq = this.editObj.seq - 1;
                        if (typeof parentSeq !== 'undefined') {
                            const parentItem = this.todoDatas[parentSeq].details[seq];
                            for (let i = 0; i < parentItem.details.length; i++) {
                                if (parentItem.details[i].temp_add_flag) {
                                    parentItem.details.splice(i, 1);
                                    i--;
                                }
                            }
                            for (let i = 0; i < parentItem.kits.length; i++) {
                                if (parentItem.kits[i].temp_add_flag) {
                                    parentItem.kits.splice(i, 1);
                                    i--;
                                }
                            }
                        }
                    }
                },
                // 'publish': () => {
                //     this.checkSurgeryPlanModal() && this.surgeryPlanSaveAllAPIFn()
                // },
                save: () => {
                    this.checkSurgeryPlanModal() && this.surgeryPlanSaveAllAPIFn();
                }
            };
            surgeryPlanSaveAll[actionTypeName]();
        },
        checkSurgeryPlanModal() {
            if (!this.operationPlanModalObj.hospital_name) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择医院',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            if (!this.operationPlanModalObj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入计划名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            if (!this.operationPlanModalObj.hospital_department_name) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择科室名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            if (!this.operationPlanModalObj.operation_time) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择手术时间',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            if (!this.operationPlanModalObj.doctor_name) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择手术人',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            if (!this.operationPlanModalDatas.length) {
                this.$warning({
                    title: '操作提示',
                    content: '请添加手术包',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            return true;
        },
        addOperationModalFn(actionTypeName) {
            if (actionTypeName === 'add') {
                // const _datas = this.addOperationDatas.filter(item => item.isActive)
                // this.operationPlanModalDatas.splice(0, 0, ..._datas)
                for (let i = 0; i < this.addOperationDatas.length; i++) {
                    let item = this.addOperationDatas[i];
                    if (item.isActive) {
                        item.is_cust
                            ? this.surgeryCustDealerPackageGetAPIFn(item.id)
                            : this.surgeryCustDealerPackageGetPlatformPackageAPIFn(item.platform_ois_inner_sn);
                    }
                }
            }
            this.addOperationModal.alert = false;
        },
        hospitalNameChangeFn(curItem, curValue) {
            if (
                !this.operationPlanModalObj.hospital_inner_sn ||
                parseInt(curValue.key) !== parseInt(this.operationPlanModalObj.hospital_inner_sn)
            ) {
                this.operationPlanModalDatas = [];
                this.editObj = {};
                this.operationPlanModalObj.hospital_inner_sn = curValue.key;
                this.orgEmpTopTreeAPIFn();
                this.orgDeptAllTreeAPIFn();
            } else {
                this.operationPlanModalObj.hospital_inner_sn = curValue.key;
            }
            // this.operationPlanModalObj.hospital_name = curItem
        },
        deptClickFn() {
            if (this.deptDatas.length <= 0 && this.operationPlanModalObj.hospital_name) {
                this.orgDeptAllTreeAPIFn();
            }
        },
        deptChangeFn(value, label) {
            this.operationPlanModalObj.hospital_department_inner_sn = value && value.length ? value[0] : '';
            this.operationPlanModalObj.hospital_department_name = label && label.length ? label[0] : '';
        },
        warehouseTreeChangeFn(value, label) {
            this.operationPlanPackageObj.out_warehouse_name = label[label.length - 1];
            this.operationPlanPackageObj.out_warehouse_inner_sn = value;
        },
        empChangeFn(value, label) {
            this.operationPlanModalObj.doctor_name = label[label.length - 1];
            this.operationPlanModalObj.doctor_employee_inner_sn = value;
        },
        empClickFn() {
            if (this.empDatas.length <= 0 && this.operationPlanModalObj.hospital_inner_sn) {
                this.orgEmpTopTreeAPIFn();
            }
        },
        operationTimeChangeFn(date, dateString) {
            this.operationPlanModalObj.operation_time = dateString || '';
        },
        formatAttributeFn(datas, compaireKey, compaireValue) {
            return datas.map((item) => {
                return {
                    ...item,
                    title: item[compaireValue],
                    key: item[compaireKey].toString(),
                    value: item[compaireKey].toString(),
                    disabled: parseInt(item.status) !== 1,
                    children:
                        item.children && item.children.length
                            ? this.formatAttributeFn(item.children, compaireKey, compaireValue)
                            : []
                };
            });
        },
        planModalActionBtnFn(e, actionTypeName, row, seq) {
            e.stopPropagation();
            if (actionTypeName === 'del') {
                if (this.operationPlanModalDatas.length <= 1) {
                    this.$warning({
                        title: '操作提示',
                        content: '必须保留一个手术包',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                } else {
                    this.operationPlanModalDatas.splice(seq - 1, 1);
                }
            } else {
                this.editPartType = 2;
                this.editObj = {
                    ...this.editObj,
                    ...row,
                    details: row.details,
                    kits: row.kits,
                    seq,
                    actionTypeName: 'operation-plan'
                };

                this.editObj.details_b = row.details;
                this.editObj.kits_b = row.kits;

                // 查看手术包的明细
                this.lookOpKitsPlanObj = { ...this.editObj };
                this.$Utils.globalDragCenterFn('custom-md');
                this.customMd.alert = true;
                this.$nextTick(() => {
                    if (this.$refs.customMd) {
                        this.$refs.customMd.initPageFn();
                    }
                });
            }
        },
        getOperationPlanModalDataFn(info) {
            info['status'] = Object.keys(this.surgeryPlanPackageStatus)[0]; // 设置默认值为 'NORMAL'
            info['cust_ois_id'] = info['id'];
            delete info['id'];

            if (info.is_cust) {
                info['dealer_inner_sn'] = '';
                info['dealer_name'] = '';
            } else {
                info['dealer_inner_sn'] = this.oid;
                info['dealer_name'] = this.employeeInfo.OrgName;
            }

            this.operationPlanModalDatas.splice(0, 0, info || {});
            this.refreshColumnEditModalFn();
        },
        getOperationKitDetailDataFn(info, kits) {
            this.kits.push({
                ...kits,
                details: info.details.map((item, index) => {
                    return {
                        ...item,
                        id: null,
                        kit_ins_id: null,
                        plan_id: null,
                        sort_number: index
                    };
                })
            });
        },
        addOperationFn() {
            if (!this.operationPlanModalObj.hospital_name) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择医院',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            this.surgeryPlanSupportPackageListAPIFn();
            this.addOperationModal.title = `${this.$route.meta.title} - 添加手术包`;
            this.addOperationModal.alert = true;
            this.$Utils.globalDragCenterFn('modal-operation-add');
        },
        toggleAddOperationFn(seq) {
            this.addOperationDatas[seq].isActive = !this.addOperationDatas[seq].isActive;
        },
        searchScopeFocusFn() {
            this.searchScopeDatas.length <= 0 && this.surgeryCustDictSearchScopeAPIFn();
        },
        searchScopeChangeFn(value) {
            for (let i = 0; i < this.searchScopeDatas.length; i++) {
                let item = this.searchScopeDatas[i];
                if (item.key === value) {
                    this.addOperationModalObj.searchScope = item.name;
                    this.addOperationModalObj.searchScopeText = item.text;
                    break;
                }
            }
        },
        brandsFocusFn() {
            this.brandDatas.length <= 0 && this.platformSurgeryBrandListAPIFn();
        },
        brandsChangeFn(value) {
            if (value) {
                for (let i = 0; i < this.brandDatas.length; i++) {
                    let item = this.brandDatas[i];
                    if (item.key === value) {
                        this.addOperationModalObj.brands = item.title;
                        this.addOperationModalObj.brandsText = item.name;
                        break;
                    }
                }
            } else {
                this.addOperationModalObj.brands = '';
                this.addOperationModalObj.brandsText = '';
            }
        },
        todoRowBtnFn(actionTypeName, row) {
            const _this = this;
            for (let i = 0; i < this.todoDatas.length; i++) {
                let item = this.todoDatas[i];
                if (item.id === row.row.plan_id) {
                    if (actionTypeName === 'del') {
                        // 删掉手术包
                        if (this.todoDatas[i].details.length <= 1) {
                            this.$warning({
                                title: '操作提示',
                                content: '必须保留一个手术包',
                                okText: '知道了',
                                centered: true,
                                maskClosable: true
                            });
                        } else {
                            _this.$confirm({
                                title: '操作提示',
                                content: '您确定要删除吗？',
                                okText: '删除',
                                cancelText: '取消',
                                centered: true,
                                maskClosable: false,
                                onOk: () => {
                                    _this.surgeryPlanPackageDelAPIFn(i, row);
                                }
                            });
                        }
                    } else {
                        this.$Utils.globalDragCenterFn('edit-lock');
                        if (this.warehouseUsableTreeDatas.length <= 0) {
                            this.warehouseUsableTopListAPIFn();
                        }
                        this.lock.alert = true;
                        this.lock.title = `${this.$route.meta.title} - 备货`;
                        this.operationPlanPackageObj.out_warehouse_inner_sn = '';
                        this.operationPlanPackageObj.out_warehouse_name = '';
                        this.operationPlanPackageObj.todoIndex = i;
                        this.operationPlanPackageObj.row = row;
                    }
                    break;
                }
            }
        },
        lockFn() {
            if (!this.operationPlanPackageObj.out_warehouse_name) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择仓库',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false,
                    mask: false
                });
                return;
            }
            this.surgeryPlanPackageLockAPIFn();
        },
        editOpFn(dataObj) {
            // 修改
            this.kits = [];
            let kitsObj = {}; // kitsObj = {'899': {'kits': {..., 'details': []}}}
            for (let i = 0; i < dataObj.kits.length; i++) {
                let item = dataObj.kits[i],
                    id = (item.platform_kit_inner_sn || item.id).toString();
                kitsObj[id] = {};
                if (!kitsObj[id]['kits']) {
                    kitsObj[id]['kits'] = [];
                }
                if (!item.temp_add_flag) {
                    // 已经存在过的
                    kitsObj[id]['kits'] = {
                        brand_inner_sn: item.brand_inner_sn,
                        cust_kit_id: item.cust_kit_id,
                        description: item.description,
                        id: item.id,
                        keywords: item.keywords,
                        kit_sn: item.kit_sn,
                        name: item.name,
                        ois_ins_id: item.ois_ins_id,
                        plan_id: item.plan_id,
                        platform_kit_inner_sn: item.platform_kit_inner_sn,
                        type_inner_sn: item.type_inner_sn
                    };
                    if (!kitsObj[id]['kits']['details']) {
                        kitsObj[id]['kits']['details'] = [];
                    }
                    for (let j = 0; j < item.details.length; j++) {
                        kitsObj[id]['kits']['details'].push({
                            ...item.details[j],
                            sort_number: j
                        });
                    }
                    this.kits.push({
                        ...kitsObj[id]['kits'],
                        details: kitsObj[id]['kits']['details']
                    });
                } else {
                    kitsObj[id]['kits'] = {
                        brand_inner_sn: item.brand_inner_sn,
                        cust_kit_id: item.id,
                        description: item.description,
                        id: null,
                        keywords: item.keywords,
                        kit_sn: item.kit_sn,
                        name: item.name,
                        ois_ins_id: null,
                        plan_id: null,
                        platform_kit_inner_sn: item.platform_kit_inner_sn,
                        type_inner_sn: item.type_inner_sn,
                        temp_add_flag: item.temp_add_flag
                    };
                    item.is_cust
                        ? this.surgeryCustDealerKitGetAPIFn(item.id, kitsObj[id]['kits'])
                        : this.surgeryCustDealePackageGetPlatformKitAPIFn(
                              item.platform_kit_inner_sn,
                              kitsObj[id]['kits']
                          );
                }
            }
            this.editMd.alert = false;
            const seq = this.editObj.seq - 1 > 0 ? this.editObj.seq - 1 : 0;
            this.operationPlanModalDatas[seq].details = dataObj.details.map((item) => {
                item.unit = item.unit_name || item.unit || '';
                return item;
            });
            this.operationPlanModalDatas[seq].kits = this.kits;
        },
        sendEditBack() {
            this.$refs.editOpMd.returnData();
        },
        searchBtnFn(actionTypeName) {
            if (actionTypeName !== 'hight') {
                this.searchObj = {
                    hospitalName: this.searchObj.hospitalName,
                    operationTimeFrom: '',
                    operationTimeTo: '',
                    packageStatuses: [],
                    q: '',
                    statuses: [],
                    types: '',
                    operationTime: ''
                };
            }

            if (this.tabChoose === this.tabTypes[0]) {
                this.initTablesFn('outbound');
            } else if (this.tabChoose === this.tabTypes[1]) {
                this.initTablesFn('toSales');
            } else {
                this.initTablesFn('history');
            }
        },
        identifySalesAndLossesFn(row, seq) {
            this.packagesInfoObj = {
                name: '',
                ois_sn: '',
                type_name: '',
                brand_name: ''
            };
            this.identifySalesAndLossesDatas = [];
            this.surgeryPlanPackageGetAllAPIFn(row.id, seq);
            if (row.status === 'BACK') {
                this.identifySalesAndLossesModal.title = `${this.$route.meta.title} - 填报销售和损耗`;
            } else if (row.status === 'SALE_PENDING') {
                this.identifySalesAndLossesModal.title = `${this.$route.meta.title} - 查看损耗`;
            } else {
                this.identifySalesAndLossesModal.title = `${this.$route.meta.title} - 确认出单`;
            }
            this.$Utils.globalDragCenterFn('modal-identify-sales-losses');
            this.identifySalesAndLossesModal.alert = true;
        },
        identifySalesAndLossesModalFn(actionTypeName) {
            if (actionTypeName === 'CANCEL') {
                this.identifySalesAndLossesModal.alert = false;
                return;
            }
            if (actionTypeName === 'BACK') {
                if (this.checkPackagesBackPassFn()) {
                    /* 构造后端所需的参数开始 */
                    let params = this.packagesInfoObj;
                    params.details = this.materialPackageDetails;
                    for (let i = 0; i < params.kits.length; i++) {
                        params.kits[i].details = [];
                    }
                    for (let i = 0; i < this.identifySalesAndLossesDatas.length; i++) {
                        let item = this.identifySalesAndLossesDatas[i];
                        if (item.ois_ins_id) {
                            // 材料包
                            params.details.push({
                                ...item,
                                sort_number: i
                            });
                        } else {
                            // 工具包
                            for (let j = 0; j < params.kits.length; j++) {
                                let kItem = params.kits[j];
                                if (item.kit_ins_id === kItem.id) {
                                    kItem.details.push({
                                        ...item,
                                        sort_number: kItem.details.length - 1 < 0 ? 0 : kItem.details.length - 1
                                    });
                                    break;
                                }
                            }
                        }
                    }
                    // 插入出库数量等于回库数量前端不显示该条记录，传给后端的值用0代替
                    for (let m = 0; m < this.toolPackageDetails.length; m++) {
                        let mItem = this.toolPackageDetails[m];
                        for (let z = 0; z < params.kits.length; z++) {
                            let zItem = params.kits[z];
                            if (mItem.kit_ins_id === zItem.id) {
                                zItem.details.push({
                                    ...mItem,
                                    sort_number: zItem.details.length - 1 < 0 ? 0 : zItem.details.length - 1
                                });
                                break;
                            }
                        }
                    }
                    /* 构造后端所需的参数结束 */
                    this.surgeryPlanPackagePendingSaleAPIFn(params);
                }
                return;
            }
            // SALE_CONFIRMED
            this.surgeryPlanPackageFinishAPIFn();
        },
        checkPackagesBackPassFn() {
            let isPackagesBackPass = true;
            for (let i = 0; i < this.identifySalesAndLossesDatas.length; i++) {
                let item = this.identifySalesAndLossesDatas[i];
                if (item.unit_price <= 0) {
                    this.$warning({
                        title: '操作提示',
                        content: `请输入NO.${i + 1} - ${item.product_name}单价。`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    isPackagesBackPass = false;
                    break;
                }
                if (item.sale_quantity === null) {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${i + 1} - ${item.product_name}的销售数量不能为空。`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    isPackagesBackPass = false;
                    break;
                }
                if (item.loss_quantity === null) {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${i + 1} - ${item.product_name}的损耗数量不能为空。`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    isPackagesBackPass = false;
                    break;
                }
                if (
                    parseFloat(item.quantity) !==
                    parseFloat(item.back_quantity) + parseFloat(item.sale_quantity) + parseFloat(item.loss_quantity)
                ) {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${i + 1} - ${item.product_name}【出库数量 = 回库数量 + 销售数量 + 损耗数量】。`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    isPackagesBackPass = false;
                    break;
                }
            }
            return isPackagesBackPass;
        }

        // /**
        //  * hack一下selector组件，placeholder不显示的问题，初始值赋予undefined的方案已经试过
        //  * 如版本升级，可依情况将该函数删除
        //  */
        // hackShowSelectorPlaceholder () {
        //     const timer = setTimeout(() => {
        //         const renderedDom = document.querySelector('.hospital-name .ant-select-selection__rendered')
        //         const placeholderDom = document.querySelector('.hospital-name .ant-select-selection__placeholder')
        //         clearTimeout(timer)
        //         if (renderedDom && placeholderDom) {
        //             renderedDom.style.width = '100%'
        //             placeholderDom.style.display = 'block'
        //         }
        //     }, 0)
        // }
    },
    computed: {
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 218 : 158,
                pager = 52;
            return clientHeight - topHeight - pager;
        },
        todoHeight() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 138 : 78,
                topPlanHeight = 72,
                pager = 52;
            return clientHeight - topHeight - topPlanHeight - pager;
        },
        scrollYNumberTodo() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 177 : 116,
                topPlanHeight = 72,
                pager = 52,
                tableHeight =
                    this.todoDatas.length > 1
                        ? clientHeight - topHeight - topPlanHeight - pager - (this.hightSearchObj.isArrow ? 360 : 410)
                        : clientHeight - topHeight - topPlanHeight - pager;
            return tableHeight;
        },
        isAddEditFlag() {
            // 只有新增、草稿和确认（手术包）状态可修改
            if (this.btnName === 'add' || this.btnName === 'del') return false;
            if (this.operationPlanModalObj.status === 'DRAFT') return false;
            if (this.operationPlanModalObj.status === 'CONFIRMED') return false;
            return true;
        }
    },
    components: {
        editsOpKits,
        lookOpKits
    },
    created() {
        this.surgeryPlanDictPackageStatusAPIFn();
        this.surgeryPlanDictPlanStatusAPIFn();
        this.initTablesFn();
    }
};
</script>

<style lang="less" scoped>
.scoped.scoped-white .tab-table-wrap {
    background: #fff;
}

.scoped .search-form-wrap .hight-search-wrap .form li {
    padding-right: 1%;
}

.operation-modal-body {
    margin-top: 48px;
}

.operation-item {
    margin: 24px 0;

    ul {
        display: flex;
        justify-content: space-between;
        padding-left: 0;
        margin-bottom: 0;

        li {
            margin: 0 4px;

            &:last-child {
                margin-right: 0;
            }
        }
    }

    .row {
        .ant-row {
            margin-right: 8px;
        }

        &:last-child {
            .ant-row {
                margin-right: 0;
            }
        }
    }

    span {
        font-size: 14px;
        color: #7b8ba0;
    }
}

.operation-add-wrap {
    height: 424px;

    .operation-add-list {
        position: relative;
        height: 400px;
        border: 1px solid #e8eaec;
        text-align: center;
        overflow: auto;

        ul {
            display: flex;
            flex-wrap: wrap;
            padding: 15px 20px;
            margin-bottom: 20px;
        }

        li {
            position: relative;
            display: flex;
            flex-direction: column;
            width: 240px;
            height: 127px;
            margin: 0 0 18px 36px;
            padding: 0 16px 0 18px;
            background-repeat: no-repeat;
            background-size: cover;
            background-image: url('../../../../public/images/pic/operation-kits-platform.png');
            cursor: pointer;

            &:nth-of-type(1) {
                font-size: 14px;
                color: #565656;
            }

            &:nth-of-type(3n + 1) {
                margin-left: 0;
            }

            &.op_is_cust {
                background-image: url('../../../../public/images/pic/operation-kits-private.png');
            }

            .brand-name {
                width: 150px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                font-weight: 600;
                margin-top: 10px;
                padding-left: 8px;
            }

            .name {
                width: 72%;
                height: 45px;
                text-align: center;
                overflow: hidden;
                text-overflow: ellipsis;
                -webkit-box-orient: vertical;
                -webkit-line-clamp: 2;
                font-size: 16px;
                font-weight: 700;
                color: #565656;
                margin: 0 auto;
            }

            .type-name,
            .ois-sn {
                position: absolute;
                display: flex;
                width: 81%;
                height: 21px;
                justify-content: flex-end;
                align-items: center;
                font-size: 14px;
                color: #565656;
                text-overflow: ellipsis;
                white-space: nowrap;
                overflow: hidden;
            }

            .type-name {
                right: 10px;
                bottom: 3px;
            }

            .ois-sn {
                right: 24px;
                bottom: -12px;
            }
        }

        .ico_active {
            position: absolute;
            right: 10px;
            top: 10px;
            color: #06c506;
        }
    }
}

.no-data-wrap {
    position: relative;
}

.no-data {
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    border-bottom: 1px solid #e8e8e8;
    padding: 80px 0;
    color: #999;

    &.no-data-position {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.2);

        .spin-wrap {
            position: absolute;
            top: 50%;
            transform: translate(0, -50%);
        }
    }
}

.operation-plan-list-wrap {
    display: block;

    ul,
    & {
        padding-left: 0;
        margin-bottom: 0;
    }

    .operation-plan-list {
        display: flex;
        flex-direction: column;
        margin: 8px;
        // border: 1px solid #dfe0df;

        // &:nth-of-type(even) {
        //   background: #f00;
        // }

        // &:nth-of-type(odd) {
        //   background: #0f0;
        // }
    }

    .caption-title {
        li {
            margin-right: 34px;

            span:nth-of-type(2) {
                margin-left: 8px;
            }

            span {
                font-size: 14px;
                color: #323332;

                &.bold {
                    font-weight: bold;
                }
            }
        }
    }

    .operation-title,
    .caption-title,
    .btn-group-wrap {
        display: flex;
        justify-content: space-between;
    }

    .operation-title {
        height: 53px;
        line-height: 53px;
        padding: 0 18px;
        background: #e8edf2;
        border: 1px solid #dfe0df;
        border-bottom: none;
        border-radius: 4px 4px 0 0;
    }

    .btn-group-wrap {
        height: 24px;

        div {
            margin-right: 8px;
            i {
                width: 30px;
                height: 30px;
                font-size: 18px;
                padding-right: 0;
                margin: 0 4px;
                cursor: pointer;

                &.edit {
                    color: #1890ff;
                }

                &.save {
                    color: #52c41a;
                }

                &.cancel {
                    color: #f5222d;
                }

                &.disabled {
                    color: #bfbcbc;
                    cursor: not-allowed;
                }
            }

            &:last-child {
                position: relative;
                margin-right: 0;
                padding-left: 8px;
            }
        }
    }
}

.hospital-name {
    font-size: 24px;
    color: rgb(68, 68, 68);
    text-align: center;
}
</style>
