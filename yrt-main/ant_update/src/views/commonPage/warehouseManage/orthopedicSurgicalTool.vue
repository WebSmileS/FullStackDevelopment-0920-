<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12"></a-col>
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
                            <a-input v-model="searchObj.hospitalName" type="text" placeholder />
                        </li>
                        <li>
                            <span class="label">手术名称</span>
                            <a-input v-model="searchObj.q" type="text" placeholder />
                        </li>
                        <!-- <li>
                            <span class="label">类别</span>
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
            </li>-->
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
                <a-tab-pane defaultActiveKey="0" :key="tabTypes[0]" tab="出库" style="padding: 8px 8px 0px 8px">
                    <vxe-table
                        ref="xOutboundTable"
                        size="small"
                        :height="scrollYNumber"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        align="center"
                        :data="outboundDatas"
                        :loading="outboundPagination.loading"
                        show-overflow
                        show-header-overflow
                        showHeader
                        :start-index="(outboundPagination.current - 1) * outboundPagination.pageSize"
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
                        <vxe-table-column field="ois_sn" title="编号" width="13%" show-overflow></vxe-table-column>
                        <vxe-table-column field="name" title="名称" width="12%" show-overflow></vxe-table-column>
                        <vxe-table-column field="type_name" title="类别" width="10%" show-overflow></vxe-table-column>
                        <vxe-table-column field="brand_name" title="品牌" width="6%" show-overflow></vxe-table-column>
                        <vxe-table-column
                            field="operation_time"
                            title="手术时间"
                            width="11%"
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
                        <vxe-table-column field="action" title="操作" width="6%" show-overflow>
                            <template v-slot="{ row, seq }">
                                <div>
                                    <a-button
                                        size="small"
                                        type="primary"
                                        @click.stop="outboundAndBackLibraryFn(row, seq)"
                                        >出库</a-button
                                    >
                                </div>
                            </template>
                        </vxe-table-column>
                    </vxe-table>
                    <vxe-pager
                        :loading="outboundPagination.loading"
                        :current-page="outboundPagination.current"
                        :page-size="outboundPagination.pageSize"
                        :page-sizes="outboundPagination.pageSizes"
                        :total="outboundPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changeOutboundPaginationFn"
                    ></vxe-pager>
                </a-tab-pane>
                <a-tab-pane :key="tabTypes[1]" tab="回库" style="padding: 8px 8px 0px 8px">
                    <vxe-table
                        ref="xBackLibraryTable"
                        size="small"
                        :height="scrollYNumber"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        align="center"
                        :data="backLibraryDatas"
                        :loading="backLibraryPagination.loading"
                        show-overflow
                        show-header-overflow
                        showHeader
                        :start-index="(backLibraryPagination.current - 1) * backLibraryPagination.pageSize"
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
                        <vxe-table-column field="ois_sn" title="编号" width="13%" show-overflow></vxe-table-column>
                        <vxe-table-column field="name" title="名称" width="12%" show-overflow></vxe-table-column>
                        <vxe-table-column field="type_name" title="类别" width="10%" show-overflow></vxe-table-column>
                        <vxe-table-column field="brand_name" title="品牌" width="6%" show-overflow></vxe-table-column>
                        <vxe-table-column
                            field="operation_time"
                            title="手术时间"
                            width="11%"
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
                        <vxe-table-column field="action" title="操作" width="6%" show-overflow>
                            <template v-slot="{ row, seq }">
                                <div>
                                    <a-button
                                        size="small"
                                        type="primary"
                                        @click.stop="outboundAndBackLibraryFn(row, seq)"
                                        >回库</a-button
                                    >
                                </div>
                            </template>
                        </vxe-table-column>
                    </vxe-table>
                    <vxe-pager
                        :loading="backLibraryPagination.loading"
                        :current-page="backLibraryPagination.current"
                        :page-size="backLibraryPagination.pageSize"
                        :page-sizes="backLibraryPagination.pageSizes"
                        :total="backLibraryPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changeBackLibraryPaginationFn"
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
                        @click="initTablesFn(tabChoose)"
                    />
                    <!--刷新按钮区域-->
                </a-tooltip>
            </a-tabs>
        </div>
        <!-- tab切换区域结束 -->

        <!--出库/回库确认手术包区域开始 -->
        <a-modal
            v-model="outboundModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped global-drag modal-outbound"
            :width="1000"
            :zIndex="8"
            style="z-index: 8 !important"
        >
            <div v-globalDrag="{ el: 'modal-outbound' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt class="modal-logo-left" />
                    {{ outboundModal.title }}
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
                    </ul>
                </div>
            </div>
            <a-tabs style="overflow: hidden" type="card" :activeKey="tabDetailChoose" @change="changeTabDetailFn">
                <a-tab-pane defaultActiveKey="0" :key="tabDetailTypes[0]" tab="材料">
                    <vxe-table
                        ref="xPackageMaterialTable"
                        height="323"
                        size="small"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        align="center"
                        :data="packageMaterialDatas"
                        :loading="packageMaterialLoading"
                        show-overflow
                        show-header-overflow
                        showHeader
                    >
                        <vxe-table-column type="seq" title="NO." width="6%" show-overflow></vxe-table-column>
                        <vxe-table-column
                            field="product_name"
                            title="产品"
                            width="15%"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column
                            field="specification_vendor_sn"
                            title="产品编码"
                            :width="tabChoose === 'outbound' ? '13%' : '10%'"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column
                            field="specification"
                            title="规格型号"
                            :width="tabChoose === 'outbound' ? '14%' : '10%'"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column
                            field="product_vendor_name"
                            title="厂商"
                            :width="tabChoose === 'outbound' ? '12%' : '9%'"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column
                            field="unit"
                            title="包装单位"
                            :width="tabChoose === 'outbound' ? '10%' : '9%'"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column
                            field="batch_number"
                            title="批号"
                            :width="tabChoose === 'outbound' ? '12%' : '16%'"
                        >
                            <template v-slot="{ row, seq }">
                                <div
                                    v-if="
                                        tabChoose === 'backLibrary' &&
                                        row.quantity !== row.back_quantity &&
                                        row.back_quantity >= 0
                                    "
                                >
                                    <a-select
                                        size="small"
                                        v-model="row.batch_number"
                                        @focus="batchNumberFocusFn(row, seq)"
                                        @change="batchNumberChangeFn(row, seq)"
                                    >
                                        <a-select-option
                                            v-for="batchNItem in row.batch_number_list"
                                            :key="batchNItem.key"
                                            :value="batchNItem.value"
                                            >{{ batchNItem.value }}</a-select-option
                                        >
                                    </a-select>
                                </div>
                                <div v-else>
                                    <span>{{ row.batch_number || '--' }}</span>
                                </div>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            :visible="tabChoose === 'backLibrary'"
                            field="quantity"
                            title="出库数量"
                            :width="tabChoose === 'backLibrary' ? '9%' : ''"
                            show-overflow
                        >
                            <!-- <template v-slot="{ row }">
                                <div>
                                    <a-checkbox v-model="row.checked"></a-checkbox>
                                </div>
                            </template> -->
                        </vxe-table-column>
                        <!-- :placeholder="tabChoose === 'backLibrary' ? row.quantity : ''" -->
                        <vxe-table-column
                            field="quantity"
                            :title="tabChoose === 'outbound' ? '出库数量' : '回库数量'"
                            :width="tabChoose === 'outbound' ? '10%' : '14%'"
                        >
                            <template v-slot="{ row }">
                                <div v-if="tabChoose === 'outbound'">{{ row.quantity }}</div>
                                <div v-else>
                                    <a-input-number
                                        size="small"
                                        v-model="row.back_quantity"
                                        type="number"
                                        :max="row.quantity"
                                        :min="0"
                                    />
                                </div>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            :visible="tabChoose === 'outbound'"
                            field="checked"
                            type="checkbox"
                            title=""
                            width="8%"
                            show-overflow
                        >
                        </vxe-table-column>
                    </vxe-table>
                </a-tab-pane>
                <a-tab-pane :key="tabDetailTypes[1]" tab="工具">
                    <vxe-table
                        ref="xPackageToolTable"
                        height="323"
                        size="small"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        align="center"
                        :data="packageToolDatas"
                        :loading="packageToolLoading"
                        show-overflow
                        show-header-overflow
                        showHeader
                    >
                        <vxe-table-column type="seq" title="NO." width="6%"></vxe-table-column>
                        <vxe-table-column field="product_name" title="产品" width="15%"></vxe-table-column>
                        <vxe-table-column
                            field="specification_vendor_sn"
                            title="产品编码"
                            :width="tabChoose === 'outbound' ? '13%' : '10%'"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column
                            field="specification"
                            title="规格型号"
                            :width="tabChoose === 'outbound' ? '14%' : '10%'"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column
                            field="product_vendor_name"
                            title="厂商"
                            :width="tabChoose === 'outbound' ? '12%' : '9%'"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column
                            field="unit"
                            title="包装单位"
                            :width="tabChoose === 'outbound' ? '10%' : '9%'"
                            show-overflow
                        ></vxe-table-column>
                        <vxe-table-column
                            field="batch_number"
                            title="批号"
                            :width="tabChoose === 'outbound' ? '12%' : '16%'"
                        >
                            <template v-slot="{ row, seq }">
                                <div
                                    v-if="
                                        tabChoose === 'backLibrary' &&
                                        row.quantity !== row.back_quantity &&
                                        row.back_quantity >= 0
                                    "
                                >
                                    <a-select
                                        size="small"
                                        v-model="row.batch_number"
                                        @focus="batchNumberFocusFn(row, seq)"
                                        @change="batchNumberChangeFn(row, seq)"
                                    >
                                        <a-select-option
                                            v-for="batchNItem in row.batch_number_list"
                                            :key="batchNItem.key"
                                            :value="batchNItem.value"
                                            >{{ batchNItem.value }}</a-select-option
                                        >
                                    </a-select>
                                </div>
                                <div v-else>
                                    <span>{{ row.batch_number || '--' }}</span>
                                </div>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            :visible="tabChoose === 'backLibrary'"
                            field="quantity"
                            title="出库数量"
                            :width="tabChoose === 'backLibrary' ? '9%' : ''"
                            show-overflow
                        >
                            <!-- <template v-slot="{ row }">
                                <div>
                                    <a-checkbox v-model="row.checked"></a-checkbox>
                                </div>
                            </template> -->
                        </vxe-table-column>
                        <vxe-table-column
                            field="quantity"
                            :title="tabChoose === 'outbound' ? '出库数量' : '回库数量'"
                            :width="tabChoose === 'outbound' ? '10%' : '14%'"
                        >
                            <template v-slot="{ row }">
                                <div v-if="tabChoose === 'outbound'">{{ row.quantity }}</div>
                                <div v-else>
                                    <a-input-number
                                        size="small"
                                        v-model="row.back_quantity"
                                        type="number"
                                        :max="row.quantity"
                                        :min="0"
                                    />
                                </div>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            :visible="tabChoose === 'outbound'"
                            field="checked"
                            type="checkbox"
                            title=""
                            width="8%"
                            show-overflow
                        >
                            <!-- <template v-slot="{ row }">
                                <div>
                                    <a-checkbox v-model="row.checked"></a-checkbox>
                                </div>
                            </template> -->
                        </vxe-table-column>
                    </vxe-table>
                </a-tab-pane>
            </a-tabs>
            <div class="operation-item clear-mb">
                <ul>
                    <li>
                        <span>仓库：</span>
                        <span>{{ packagesInfoObj.out_warehouse_name || '--' }}</span>
                    </li>
                </ul>
            </div>
            <template slot="footer">
                <a-button key="cancel" @click.stop="outboundAndBackLibraryModalFn('cancel')">关闭</a-button>
                <a-button
                    :loading="outBoundLoading"
                    key="save"
                    type="primary"
                    @click.stop="outboundAndBackLibraryModalFn('out')"
                    >{{ tabChoose === 'outbound' ? '出库' : '回库' }}</a-button
                >
            </template>
        </a-modal>
        <!-- 出库/回库确认手术包区域结束 -->
    </div>
</template>

<script>
import moment from 'moment';

import {
    surgeryPlanListPackageAPI,
    surgeryCustDictSearchScopeAPI,
    surgeryPlanPackageLockAPI,
    surgeryPlanDictPackageStatusAPI,
    surgeryPlanDictPlanStatusAPI,
    surgeryPlanPackageGetAllAPI,
    surgeryPlanPackageOutAPI,
    surgeryPlanPackageBackAPI,
    odoBatchListAPI,
    platformUnitMinAPI
} from '@/service/pageAjax';

export default {
    name: 'orthopedicSurgicalTool',
    data() {
        return {
            empty: '空',
            format: 'YYYY-MM-DD HH:mm:ss',
            employeeInfo: JSON.parse(this.$cookie.get('EmployeeInfo')),
            userSystemType: this.$cookie.get('userSystemType'),
            outBoundLoading: false,
            lock: {
                title: `${this.$route.meta.title} - 手术包 - 备货`,
                alert: false,
                loading: false
            },
            editObj: {},
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
            tabTypes: ['outbound', 'backLibrary'],
            tabChoose: 'outbound',
            tabDetailTypes: ['material', 'tools'],
            tabDetailChoose: 'material',
            todoDatas: [],
            outboundDatas: [],
            outboundPagination: {
                isLoad: false,
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            backLibraryDatas: [],
            backLibraryPagination: {
                isLoad: false,
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            outboundModal: {
                alert: false,
                title: '骨科手术计划-确认手术包',
                outboundLoading: false
            },
            outboundPackagesList: [],
            packagesInfoObj: {
                name: '',
                ois_sn: '',
                type_name: '',
                brand_name: '',
                id: ''
            },
            packageMaterialLoading: false,
            packageToolLoading: false,
            packageMaterialDatas: [],
            packageToolDatas: [],
            searchScopeDatas: [],
            surgeryPlanPackageStatus: {},
            surgeryPlanStatus: {},
            surgeryPlanStatusDatas: [],
            kits: [],
            operationPlanPackageObj: {
                out_warehouse_name: '',
                out_warehouse_inner_sn: ''
            }
        };
    },
    methods: {
        moment,
        // 获取产品包装单位的最小倍数 【厂商】
        async platformUnitMinAPIFn(record, seq) {
            await platformUnitMinAPI(
                typeof record.product_vendor_inner_sn === 'undefined'
                    ? record.vendor_inner_sn
                    : record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const minUnitInfo = parseInt(res.info);
                    if (this.tabDetailChoose === 'material') {
                        this.packageMaterialDatas[seq].voucher_quantity = minUnitInfo * record.quantity;
                        this.$refs.xPackageMaterialTable.updateData();
                    } else {
                        this.packageToolDatas[seq].voucher_quantity = minUnitInfo * record.quantity;
                        this.$refs.xPackageToolTable.updateData();
                    }
                    record.batchNumerLoading = true;
                    this.odoBatchListAPIFn(record, seq);
                }
            });
        },
        async surgeryPlanListPackageAPIFn(paginationObj, packageStatusesN) {
            paginationObj.loading = true;
            await surgeryPlanListPackageAPI(
                {
                    hospitalName: this.searchObj.hospitalName,
                    operationTimeFrom: this.searchObj.operationTimeFrom
                        ? `${this.searchObj.operationTimeFrom} 00:00:00`
                        : '',
                    operationTimeTo: this.searchObj.operationTimeTo ? `${this.searchObj.operationTimeTo} 23:59:59` : '',
                    packageStatuses: [packageStatusesN],
                    q: this.searchObj.q,
                    statuses: this.searchObj.statuses,
                    types: this.searchObj.types ? [this.searchObj.types] : []
                },
                paginationObj.current,
                paginationObj.pageSize
            )
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res,
                            datas = rows;
                        if (packageStatusesN === 'LOCKED') {
                            this.outboundDatas = datas;
                            this.$refs.xOutboundTable && this.$refs.xOutboundTable.refreshColumn();
                        } else {
                            this.backLibraryDatas = datas;
                            this.$refs.xBackLibraryTable && this.$refs.xBackLibraryTable.refreshColumn();
                        }
                        paginationObj.total = total;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    paginationObj.loading = false;
                });
        },
        async odoBatchListAPIFn(row, seq) {
            await odoBatchListAPI({
                org_id: this.packagesInfoObj.dealer_inner_sn || this.org_id,
                product_inner_sn: row.product_inner_sn,
                product_type: 1, // 手术包不能添加自建产品
                product_vendor_inner_sn: row.product_vendor_inner_sn,
                specification_inner_sn: row.specification_inner_sn,
                warehouse_inner_sn: this.packagesInfoObj.out_warehouse_inner_sn,
                voucher_quantity: row.voucher_quantity,
                billsType: 'DH'
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let { list } = res,
                            resList = list.map((item) => {
                                return {
                                    ...item,
                                    batch_number: item.batch_number || this.empty
                                };
                            });
                        resList = this.formatAttributeFn(resList, 'batch_number', 'batch_number');
                        if (this.tabDetailChoose === 'material') {
                            this.packageMaterialDatas[seq].batch_number_list = resList;
                            this.$refs.xPackageMaterialTable.updateData();
                        } else {
                            this.packageToolDatas[seq].batch_number_list = resList;
                            this.$refs.xPackageToolTable.updateData();
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        async surgeryPlanPackageOutAPIFn() {
            this.outBoundLoading = true;
            await surgeryPlanPackageOutAPI(this.packagesInfoObj.id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.outboundAndBackLibraryModalFn('cancel');
                        this.initTablesFn('outbound');
                        this.initTablesFn('backLibrary');
                        this.packagesInfoObj = {
                            name: '',
                            ois_sn: '',
                            type_name: '',
                            brand_name: '',
                            id: ''
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.outBoundLoading = false;
                });
        },
        async surgeryPlanPackageBackAPIFn(params) {
            this.outBoundLoading = true;
            await surgeryPlanPackageBackAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.outboundAndBackLibraryModalFn('cancel');
                        // this.surgeryPlanListPackageAPIFn(this.outboundPagination, 'LOCKED')
                        this.surgeryPlanListPackageAPIFn(this.backLibraryPagination, 'OUT');
                        this.packagesInfoObj = {
                            name: '',
                            ois_sn: '',
                            type_name: '',
                            brand_name: '',
                            id: ''
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.outBoundLoading = false;
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
        async surgeryPlanPackageGetAllAPIFn(id) {
            this.packageMaterialLoading = true;
            this.packageToolLoading = true;
            await surgeryPlanPackageGetAllAPI(id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let { info } = res,
                            kits = info.kits;
                        this.packageMaterialDatas = info.details.map((item) => {
                            if (this.tabChoose === 'backLibrary') {
                                item.back_quantity = item.back_quantity <= 0 ? '' : item.back_quantity;
                            }

                            return {
                                ...item,
                                batch_number_list: [],
                                overdue_date: '',
                                produce_date: '',
                                checked: false,
                                batchNumerLoading: false
                            };
                        });
                        for (let i = 0; i < kits.length; i++) {
                            let item = kits[i];
                            if (item.details && item.details.length) {
                                for (let j = 0; j < item.details.length; j++) {
                                    let cItem = item.details[j];
                                    if (this.tabChoose === 'backLibrary') {
                                        cItem.back_quantity = cItem.back_quantity <= 0 ? '' : cItem.back_quantity;
                                    }
                                    this.packageToolDatas.push({
                                        ...cItem,
                                        batch_number_list: [],
                                        overdue_date: '',
                                        produce_date: '',
                                        checked: false,
                                        batchNumerLoading: false
                                    });
                                }
                            }
                        }
                        this.$refs.xPackageMaterialTable && this.$refs.xPackageMaterialTable.refreshColumn();
                        this.$refs.xPackageToolTable && this.$refs.xPackageToolTable.refreshColumn();
                        this.packagesInfoObj = {
                            ...info,
                            name: info.name,
                            ois_sn: info.ois_sn,
                            type_name: info.type_name,
                            brand_name: info.brand_name,
                            id: info.id
                        };
                        info = null;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.packageMaterialLoading = false;
                    this.packageToolLoading = false;
                });
        },
        async surgeryPlanPackageLockAPIFn() {
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
                .finally(() => {});
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
        changeOperationTimeFn(value, dateString) {
            const [beginTime, endTime] = dateString;
            this.searchObj = {
                ...this.searchObj,
                operationTimeFrom: beginTime,
                operationTimeTo: endTime
            };
        },
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        changeTabFn(activeKey) {
            this.tabChoose = activeKey;
            if (!this.backLibraryPagination.isLoad) {
                this.backLibraryPagination.isLoad = true;
                this.initTablesFn('backLibrary');
            }
        },
        changeTabDetailFn(activeKey) {
            this.tabDetailChoose = activeKey;
        },
        changeOutboundPaginationFn({ currentPage, pageSize }) {
            this.outboundPagination.current = currentPage;
            this.outboundPagination.pageSize = pageSize;
            this.surgeryPlanListPackageAPIFn(this.outboundPagination, 'LOCKED');
        },
        changeBackLibraryPaginationFn({ currentPage, pageSize }) {
            this.backLibraryPagination.current = currentPage;
            this.backLibraryPagination.pageSize = pageSize;
            this.backLibraryPagination.loading = true;
            this.surgeryPlanListPackageAPIFn(this.backLibraryPagination, 'OUT');
        },
        initTablesFn(actionTypeName) {
            if (actionTypeName === 'outbound') {
                this.outboundDatas = [];
                this.outboundPagination.current = 1;
                this.outboundPagination.pageSize = 10;
                this.outboundPagination.pageNum = 1;
                this.surgeryPlanListPackageAPIFn(this.outboundPagination, 'LOCKED');
            } else {
                // (actionTypeName === 'backLibrary'
                this.backLibraryDatas = [];
                this.backLibraryPagination.current = 1;
                this.backLibraryPagination.pageSize = 10;
                this.backLibraryPagination.pageNum = 1;
                this.surgeryPlanListPackageAPIFn(this.backLibraryPagination, 'OUT');
            }
        },
        outboundAndBackLibraryModalFn(actionTypeName) {
            const surgeryPlanSaveAll = {
                cancel: () => {
                    this.outboundModal.alert = false;
                },
                out: () => {
                    if (this.tabChoose === 'outbound') {
                        this.checkPackageFn() && this.surgeryPlanPackageOutAPIFn();
                    } else {
                        // 回库
                        // 1、除回库数量外的字段全部只读
                        // 2、回库数量默认为空，必须由仓库人员填写，否则不能进行回库操作
                        // 3. 回库数量不等于出库数量，批号必填，反之不用传批号
                        if (this.checkBackLibraryFn()) {
                            let params = this.packagesInfoObj;
                            params.details = this.packageMaterialDatas.map((item, index) => {
                                delete item.batchNumerLoading;
                                delete item.batch_number_list;
                                delete item.checked;
                                delete item._XID;

                                if (item.quantity !== item.back_quantity) {
                                    return {
                                        ...item,
                                        batch_number: item.batch_number === this.empty ? '' : item.batch_number,
                                        sort_number: index
                                    };
                                } else {
                                    delete item.batch_number;
                                    delete item.overdue_date;
                                    delete item.produce_date;

                                    return {
                                        ...item,
                                        sort_number: index
                                    };
                                }
                            });
                            for (let i = 0; i < this.packagesInfoObj.kits.length; i++) {
                                let item = this.packagesInfoObj.kits[i];
                                item.details = [];
                            }
                            for (let i = 0; i < this.packagesInfoObj.kits.length; i++) {
                                let item = this.packagesInfoObj.kits[i];
                                for (let j = 0; j < this.packageToolDatas.length; j++) {
                                    let cItem = this.packageToolDatas[j];
                                    if (item.id === cItem.kit_ins_id) {
                                        delete cItem.batchNumerLoading;
                                        delete cItem.batch_number_list;
                                        delete cItem.checked;
                                        delete item._XID;

                                        if (cItem.quantity !== cItem.back_quantity) {
                                            item.details.push({
                                                ...cItem,
                                                batch_number:
                                                    cItem.batch_number === this.empty ? '' : cItem.batch_number,
                                                sort_number: item.details.length - 1 < 0 ? 0 : item.details.length - 1
                                            });
                                        } else {
                                            delete cItem.batch_number;
                                            delete cItem.overdue_date;
                                            delete cItem.produce_date;

                                            item.details.push({
                                                ...cItem,
                                                sort_number: item.details.length - 1 < 0 ? 0 : item.details.length - 1
                                            });
                                        }
                                    }
                                }
                            }
                            params.kits = this.packagesInfoObj.kits;
                            this.surgeryPlanPackageBackAPIFn(params);
                        }
                    }
                }
            };

            surgeryPlanSaveAll[actionTypeName]();
        },
        checkPackageFn() {
            const xPackageMaterialTable = this.$refs.xPackageMaterialTable,
                xPackageToolTable = this.$refs.xPackageToolTable;
            let isOutBoundChecked = false,
                isBackLibraryChecked = false;

            if (xPackageMaterialTable) {
                isOutBoundChecked =
                    xPackageMaterialTable.getCheckboxRecords().length === this.packageMaterialDatas.length;
            }

            if (xPackageToolTable) {
                isBackLibraryChecked = xPackageToolTable.getCheckboxRecords().length === this.packageToolDatas.length;
            }

            if (!isOutBoundChecked) {
                this.$warning({
                    title: '操作提示',
                    content: '请勾选材料',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            if (this.packageToolDatas.length && !isBackLibraryChecked) {
                this.$warning({
                    title: '操作提示',
                    content: '请勾选工具',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            return true;
        },
        checkBackLibraryFn() {
            let isBackLibraryPass = this.checkBackLibraryItemFn(this.packageMaterialDatas);
            if (isBackLibraryPass) {
                isBackLibraryPass = this.checkBackLibraryItemFn(this.packageToolDatas);
                if (!isBackLibraryPass && this.tabDetailChoose === 'material') {
                    this.tabDetailChoose = 'tools';
                }
            }
            return isBackLibraryPass;
        },
        checkBackLibraryItemFn(datas) {
            let isBackLibraryPass = true;
            for (let i = 0; i < datas.length; i++) {
                let item = datas[i];
                if (item.quantity !== item.back_quantity && !item.batch_number) {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${i + 1} - ${item.product_name}的批号不能为空。`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    isBackLibraryPass = false;
                    break;
                }

                if (item.back_quantity === null || item.back_quantity.length <= 0) {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${i + 1} - ${item.product_name}的回库数量不能为空。`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    isBackLibraryPass = false;
                    break;
                }

                if (item.back_quantity < 0) {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${i + 1} - ${item.product_name}的回库数量无效。`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    isBackLibraryPass = false;
                    break;
                }
            }

            return isBackLibraryPass;
        },
        formatAttributeFn(datas, compaireKey, compaireValue) {
            return datas.map((item) => {
                return {
                    ...item,
                    title: item[compaireValue],
                    key: item[compaireKey].toString(),
                    value: item[compaireKey].toString(),
                    disabled: parseInt(item.status) === 0,
                    children:
                        item.children && item.children.length
                            ? this.formatAttributeFn(item.children, compaireKey, compaireValue)
                            : []
                };
            });
        },
        outboundAndBackLibraryFn(row) {
            this.tabDetailChoose = 'material';
            this.packageMaterialDatas = [];
            this.packageToolDatas = [];
            this.surgeryPlanPackageGetAllAPIFn(row.id);
            this.$Utils.globalDragCenterFn('modal-outbound');
            this.outboundModal.title = `${this.$route.meta.title} - ${
                this.tabChoose === this.tabTypes[0] ? '出库' : '回库'
            }`;
            this.outboundModal.alert = true;
            this.$nextTick(() => {
                if (this.$refs.xPackageMaterialTable) {
                    this.$refs.xPackageMaterialTable.refreshColumn();
                }
                if (this.$refs.xPackageToolTable) {
                    this.$refs.xPackageToolTable.refreshColumn();
                }
            });
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
            } else {
                this.initTablesFn('backLibrary');
            }
        },
        batchNumberFocusFn(row, seq) {
            if (!row.batchNumerLoading) {
                this.platformUnitMinAPIFn(row, seq - 1 > 0 ? seq - 1 : 0);
            }
        },
        batchNumberChangeFn(row, seq) {
            let overdueDate = '',
                produceDate = '';
            for (let i = 0; i < row.batch_number_list.length; i++) {
                let item = row.batch_number_list[i];
                if (item.key === row.batch_number) {
                    overdueDate = item.overdue_date;
                    produceDate = item.produce_date;
                    break;
                }
            }
            if (this.tabDetailChoose === this.tabDetailTypes[0]) {
                this.packageMaterialDatas[seq - 1].overdue_date = overdueDate;
                this.packageMaterialDatas[seq - 1].produce_date = produceDate;
            } else {
                this.packageToolDatas[seq - 1].overdue_date = overdueDate;
                this.packageToolDatas[seq - 1].produce_date = produceDate;
            }
        }
    },
    computed: {
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 218 : 158,
                pager = 52;
            return clientHeight - topHeight - pager;
        }
    },
    created() {
        this.surgeryPlanDictPackageStatusAPIFn();
        this.surgeryPlanDictPlanStatusAPIFn();
        this.initTablesFn('outbound');
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

    &.clear-mb {
        margin-bottom: 0;
    }

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
        height: 400px;
        // margin-bottom: 20px;
        border: 1px solid #e8eaec;
        text-align: center;
        overflow: auto;

        ul {
            display: flex;
            //   justify-content: space-between;
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

.no-data {
    display: block;
    padding: 20px 0;
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
    display: flex;
    flex-direction: column;

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
