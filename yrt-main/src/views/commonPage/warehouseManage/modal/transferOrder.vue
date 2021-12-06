<template>
    <div class="modal-form-input" style="height: 628px">
        <ul class="top-list" style="width: 100%">
            <li class="item" v-if="isShowOrgNameInput">
                <span class="label">单位名称</span>
                <div class="input">
                    <a-input placeholder="单位名称" size="small" v-model="searchOrg" />
                </div>
            </li>
            <li class="item">
                <span class="label">产品名称</span>
                <div class="input">
                    <a-input placeholder="产品" size="small" v-model="searchName" />
                </div>
            </li>
        </ul>
        <ul class="top-list" style="width: 100%">
            <li class="item">
                <span class="label">单据类型</span>
                <div class="input">
                    <a-tree-select
                        :treeData="billLadingVoucherTypeList"
                        v-model="sheelID"
                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                        treeDefaultExpandAll
                        @change="handleSearch"
                        class="input"
                    >
                    </a-tree-select>
                </div>
            </li>
            <li class="item">
                <span class="label">单据编号</span>
                <div class="input">
                    <a-input placeholder="单据编号" size="small" v-model="searchKey" />
                </div>
            </li>
            <li class="item" v-if="sheelID === 'ORDER_PO'">
                <span class="label">仓库</span>
                <div class="input">
                    <a-tree-select
                        treeNodeFilterProp="title"
                        :showSearch="true"
                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                        :treeData="warehouseList"
                        placeholder=""
                        treeDefaultExpandAll
                        allowClear
                        class="form-input"
                        size="small"
                        v-model="searchWarehouseModelInnerSn"
                    >
                    </a-tree-select>
                </div>
            </li>
            <li style="width: auto">
                <a-button @click="handleSearch" type="primary"> 搜索 </a-button>
            </li>
        </ul>
        <div class="tab-table-wrap cur-overflow-hidden tab-table-overflowy" style="padding-bottom: 0; margin-top: 0">
            <vxe-grid
                ref="sheelTop"
                size="small"
                :height="150 - ipadHeight"
                border
                resizable
                auto-resize
                highlight-current-row
                @cell-click="MainTableOnClickFn"
                align="center"
                :data="MainTableDatas"
                :loading="MainTablePagination.loading"
                show-overflow
                show-header-overflow
                showHeader
                :columns="MainTableColumns"
                :start-index="(MainTablePagination.current - 1) * MainTablePagination.pageSize"
            >
            </vxe-grid>
            <vxe-pager
                :loading="MainTablePagination.loading"
                :current-page="MainTablePagination.current"
                :page-size="MainTablePagination.pageSize"
                :page-sizes="MainTablePagination.pageSizes"
                :total="MainTablePagination.total"
                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                style="background: #ffffff"
                :pager-count="4"
                @page-change="MainTablePaginationOnChange"
            >
            </vxe-pager>
            <a-checkbox :checked="sheelType" @change="sheelOnchangeFn" style="margin-bottom: 10px; margin-left: 20px">
                {{ checkText }}
            </a-checkbox>
            <vxe-grid
                ref="sheelTable"
                size="small"
                :height="260 - ipadHeight"
                border
                resizable
                auto-resize
                highlight-current-row
                @cell-click="DetailTableOnClickFn"
                align="center"
                :data="DetailTableDatas"
                :loading="DetailTableLoading"
                show-overflow
                type="checkbox"
                show-header-overflow
                showHeader
                :edit-config="{ trigger: 'click', mode: 'row', showStatus: true }"
                @select-change="DetailTableOnCheck"
                @select-all="DetailTableOnCheckAll"
                :checkbox-config="{ checkMethod: DetailTableCheckConfig }"
                :columns="DetailTableColumns"
            >
            </vxe-grid>
        </div>
    </div>
</template>

<script>
import {
    odoSheetListAPI,
    odoTransferGrantListAPI,
    odoTransferGrantDetailAPI,
    receiptSheetListAPI,
    refVoucherTypeAPI,
    receiptTransferRecordListAPI,
    receiptTransferRecordDetailAPI,
    odoTransferRecordListAPI,
    odoTransferRecordDetailAPI,
    grnSheetListAPI,
    grnTransferRecordListAPI,
    grnTransferRecordDetailAPI,
    allotTransferVoucherAPI,
    AllotTransferDetailAPI,
    /* 出入库单单据拆分区域开始 */
    purchaseGrnSheetListAPI,
    purchaseGrnTransferRecordListAPI,
    purchaseGrnTransferRecordDetailAPI,
    saleOdoSheetListAPI,
    saleOdoTransferRecordListAPI,
    saleOdoTransferRecordDetailAPI,
    grantOdoSheetListAPI,
    grantOdoTransferRecordListAPI,
    grantOdoTransferRecordDetailAPI,
    allotGrnSheetListAPI,
    allotGrnTransferRecordListAPI,
    allotGrnTransferRecordDetailAPI,
    allotOdoSheetListAPI,
    allotOdoTransferRecordListAPI,
    allotOdoTransferRecordDetailAPI
    /* 出入库单单据拆分区域结束 */
} from '@/service/pageAjax';

import {
    MainTableColumnsOdoGrant,
    MainTableColumnsOdoSale,
    MainTableColumnsGrnBuy,
    MainTableColumnsAllot,
    DetailTableColumnsOdoGrant,
    DetailTableColumnsOdoSale,
    MainTableColumnsReceiving,
    DetailTableColumnsGrnCommon,
    DetailTableColumnsGrnAllot,
    DetailTableColumnsOdoAllot
} from './module/transferOrderColumnConfig';

import { grnAndOdoOrderType } from '@/config/constants';

export default {
    name: 'transferOrder',
    /**
     * @parameter
     * - TYPE_ODO 出库调单
     * - TYPE_GRN 入库调单
     * @warehouseList
     *
     * @orderTypeData
     * -  <出入库单据类型，该字段主要是从以前的出入库单拆分而来>，
     * -  具体可详见 [config文件夹下的constant.js文件 grnAndOdoOrderType]
     */
    props: ['parameter', 'warehouseList', 'orderTypeData'], // 1 - 合同内的产品  2 - 期初库存的产品列表  3 - 采购计划  4 - 采购/销售订单  7 - 入库单  8 - 出库单  9 - 验货单
    data() {
        return {
            systemType: parseInt(this.$cookie.get('userSystemType')),
            userBelong: parseInt(this.$cookie.get('userbelong')),
            sheelID: '',
            checkText: '仅显示待出库细目',
            org_id_m: 0,
            odrder_type_m: 0,
            sheelType: true, // 仅显示待出库细目状态
            sheelStatus: ['待生成出库单', '出库单已完全生成', '已领取'],
            organization_name_m: '',
            organization_inner_m: 0,
            MainTableDatas: [],
            billLadingBotDatasAll: [],
            DetailTableDatas: [],
            delInfoDatas: [],
            sheelListData: [], // 记录调单的数据
            sheelListDataSame: [], // 记录调单的数据合并其中相同来源的数据
            billLadingVoucherTypeList: [],
            detailTableSelectedRowKeys: [],
            mainTableCurrentSelectRow: {},
            sl_inner_sn_m: 0,
            MainTablePagination: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            searchKey: '', // 搜索关键字(单据编号)
            searchOrg: '', // 搜索关键字(单位名称)
            searchName: '', // 搜索关键字(产品名称)
            searchWarehouseModelInnerSn: '', // 搜索关键字(仓库)
            DetailTableLoading: false,
            DetailTableCurrentSelectRow: {},
            // 需要传递的父节点内容
            infoModalForm: {
                department_inner_sn: '',
                department_name: '',
                organization_name: '',
                organization_inner_sn: '',
                billsType: '',
                sl_inner_sn: '',
                id: '',
                grn_type: '',
                warehouse_name: '',
                warehouse_inner_sn: '',
                delivery_address: '',
                buyer_name: ''
            },
            // 上方表格列设置
            MainTableColumns: [],
            // 下方表格列设置
            DetailTableColumns: [],
            // 【出库】【发放单】上方表格列
            MainTableColumnsOdoGrant,
            // 【出库】【销售单】上方表格列
            MainTableColumnsOdoSale,
            // 【入库】【采购订单】上方表格列
            MainTableColumnsGrnBuy,
            // 【调拨】上方表格列
            MainTableColumnsAllot,
            // 【出库】【发放单】 下方表格列
            DetailTableColumnsOdoGrant,
            // 【出库】【销售单】 下方表格列
            DetailTableColumnsOdoSale,
            // 【停用】【收货】经销商端-调单上边表格的Columns
            MainTableColumnsReceiving,
            // 【收货】经销商端-调单下边表格的Columns
            // 【入库】【出库单】
            // 【入库】【采购订单】
            DetailTableColumnsGrnCommon,
            // 【入库】【调拨】下方表格列
            DetailTableColumnsGrnAllot,
            // 【出库】【调拨】下方表格列
            DetailTableColumnsOdoAllot,
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 60 : 0
        };
    },
    methods: {
        /* 出入库接口拆分区域开始 */
        grnAndOdoSheetListFn(params) {
            switch (this.orderTypeData.value) {
                case grnAndOdoOrderType['PURCHASEGRN'].value:
                    return purchaseGrnSheetListAPI(params);
                case grnAndOdoOrderType['ALLOTGRN'].value:
                    return allotGrnSheetListAPI(params);
                case grnAndOdoOrderType['SALEODO'].value:
                    return saleOdoSheetListAPI(params);
                case grnAndOdoOrderType['ALLOTODO'].value:
                    return allotOdoSheetListAPI(params);
                case grnAndOdoOrderType['GRANTODO'].value:
                    return grantOdoSheetListAPI(params);
            }
        },
        // 出入库单的可调单类型列表
        async grnAndOdoSheetListAPIFn() {
            this.detailTableSelectedRowKeys = [];
            this.checkText = `仅显示可${this.orderTypeData.label}细目`;
            this.billLadingVoucherTypeList = [];
            const res = await this.grnAndOdoSheetListFn({ params: {} });
            if (parseFloat(res.code) === 0) {
                const { list = [] } = res;
                list.forEach((item) => {
                    this.billLadingVoucherTypeList.push({
                        value: item,
                        title: '',
                        label: '',
                        key: item
                    });
                });
                if (this.billLadingVoucherTypeList.length > 0) {
                    this.sheelID = this.billLadingVoucherTypeList[0].value;
                    this.UpdateColumns();
                }
                this.infoModalForm.id = this.sheelID;

                this.refVoucherTypeAPIFn();
                this.grnAndOdoTransferRecordListAPIFn();
            }
        },
        grnAndOdoTransferRecordListFn(params) {
            switch (this.orderTypeData.value) {
                case grnAndOdoOrderType['PURCHASEGRN'].value:
                    params.voucherType = this.sheelID;
                    params.key = this.searchName; // 产品名称
                    params.sn = this.searchKey; // 销售单单号
                    delete params.key1;
                    return purchaseGrnTransferRecordListAPI({ params });
                case grnAndOdoOrderType['ALLOTGRN'].value:
                    return allotGrnTransferRecordListAPI({ params });
                case grnAndOdoOrderType['SALEODO'].value:
                    return saleOdoTransferRecordListAPI({ params });
                case grnAndOdoOrderType['ALLOTODO'].value:
                    return allotOdoTransferRecordListAPI({ params });
                case grnAndOdoOrderType['GRANTODO'].value:
                    return grantOdoTransferRecordListAPI({ params });
            }
        },
        // 出入库单的可调拨单列表
        async grnAndOdoTransferRecordListAPIFn() {
            this.MainTableDatas = [];
            let params = {
                pageNum: this.MainTablePagination.current,
                pageSize: this.MainTablePagination.pageSize,
                key: this.searchKey, // 搜索关键字(单据编号)
                organizationName: this.searchOrg, // 搜索关键字(单位名称)
                key1: this.searchName // 搜索关键字(产品名称)
            };

            if (this.sheelID === 'ORDER_PO') {
                const warehouse = this.warehouseList.find(
                    (item) => item.warehouse_model_inner_sn === this.searchWarehouseModelInnerSn
                );
                if (warehouse) {
                    params.warehouse_name = warehouse.title;
                }
            }

            await this.grnAndOdoTransferRecordListFn(params)
                .then((res) => {
                    this.MainTablePagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;

                        this.MainTableDatas = rows.map((item) => {
                            item.sn = item.gv_sn || item.sl_sn || item.voucher_sn || item.av_sn;
                            if (item.sl_inner_sn && item.seller_inner_sn) {
                                item.key = item.sl_inner_sn + '' + item.seller_inner_sn;
                            } else {
                                item.key = item.org_id + '' + item.odo_inner_sn;
                            }
                            item.activeClass = '';
                            return item;
                        });
                        if (this.MainTableDatas.length) {
                            this.mainTableCurrentSelectRow = this.MainTableDatas[0];
                            this.$refs.sheelTop.setCurrentRow(this.mainTableCurrentSelectRow);
                            this.$refs.sheelTop.updateData();
                            this.grnAndOdoTransferRecordDetailAPIFn(this.mainTableCurrentSelectRow);
                        } else {
                            this.MainTableDatas = [];
                            this.mainTableCurrentSelectRow = {};
                        }

                        this.MainTablePagination = {
                            ...this.MainTablePagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.MainTablePagination.loading = false;
                });
        },
        grnAndOdoTransferRecordDetailFn(params, record) {
            switch (this.orderTypeData.value) {
                case grnAndOdoOrderType['PURCHASEGRN'].value:
                    params.vId = this.infoModalForm.sl_inner_sn;
                    params.voucherType = this.sheelID;
                    params.organization_inner_sn = record.organization_inner_sn;
                    return purchaseGrnTransferRecordDetailAPI({ params });
                case grnAndOdoOrderType['ALLOTGRN'].value:
                    delete params.billsType;

                    params.avid = record.av_inner_sn;
                    return allotGrnTransferRecordDetailAPI({ params });
                case grnAndOdoOrderType['SALEODO'].value:
                    params.sid = record.sl_inner_sn;
                    return saleOdoTransferRecordDetailAPI({ params });
                case grnAndOdoOrderType['ALLOTODO'].value:
                    delete params.billsType;

                    params.avid = record.av_inner_sn;
                    return allotOdoTransferRecordDetailAPI({ params });
                case grnAndOdoOrderType['GRANTODO'].value:
                    delete params.billsType;

                    params.gvid = record.gv_inner_sn;
                    return grantOdoTransferRecordDetailAPI({ params });
            }
        },
        // 出入库单的可调拨单明细列表
        async grnAndOdoTransferRecordDetailAPIFn(record, mode) {
            /// 调拨出入库单
            if (
                this.orderTypeData.value === grnAndOdoOrderType['ALLOTGRN'].value ||
                this.orderTypeData.value === grnAndOdoOrderType['ALLOTODO'].value
            ) {
                const departments = JSON.parse(this.$cookie.get('loginDatas')).Departments;
                this.infoModalForm.department_inner_sn = departments[0].Department_inner_sn;
                this.infoModalForm.department_name = departments[0].Name;
                this.infoModalForm.organization_inner_sn = record.organization_inner_sn || this.userBelong;
                this.infoModalForm.organization_name = JSON.parse(this.$cookie.get('EmployeeInfo')).OrgName;
                this.infoModalForm.sl_inner_sn = record.av_inner_sn;
                this.infoModalForm.billsType = this.systemType === 1 ? 'HH' : this.systemType === 2 ? 'DD' : 'VV';
                this.infoModalForm.warehouse_name =
                    this.parameter === 'TYPE_ODO' ? record.source_warehouse_name : record.target_warehouse_name;
                this.infoModalForm.warehouse_inner_sn =
                    this.parameter === 'TYPE_ODO' ? record.source_warehouse_inner_sn : record.target_warehouse_inner_sn;
                this.DetailTableLoading = true;
            } else {
                /// 其它类型
                this.infoModalForm.department_inner_sn = record.department_inner_sn;
                this.infoModalForm.department_name = record.department_name;
                this.infoModalForm.organization_name = record.organization_name;
                this.infoModalForm.organization_inner_sn = record.organization_inner_sn || this.userBelong;
                this.infoModalForm.sl_inner_sn =
                    record.sl_inner_sn ||
                    record.voucher_inner_sn_c ||
                    record.odo_inner_sn ||
                    record.av_inner_sn ||
                    record.gv_inner_sn;
                this.infoModalForm.billsType = record.billsType;
                this.infoModalForm.grn_type = record.grn_type;
                this.odrder_type_m = record.billsType;
                this.infoModalForm.warehouse_name = record.warehouse_name;
                this.infoModalForm.warehouse_inner_sn = record.warehouse_inner_sn;
                this.infoModalForm.delivery_address = record.delivery_address;
                if (this.orderTypeData.value === grnAndOdoOrderType['PURCHASEGRN'].value) {
                    this.infoModalForm.buyer_name = record.handler_name ? record.handler_name : '';
                }
            }
            this.infoModalForm.voucher_inner_sn_c = this.infoModalForm.sl_inner_sn;

            if (record.org_id) this.org_id_m = record.org_id;
            this.DetailTableDatas = [];
            this.billLadingBotDatasAll = [];
            this.detailTableSelectedRowKeys = [];
            this.sl_inner_sn_m = record.sl_inner_sn;
            const sheelTable = this.$refs.sheelTable;
            if (sheelTable) {
                sheelTable.clearCheckboxRow();
            }
            this.DetailTableLoading = true;
            let params = {
                billsType: record.billsType,
                pageNum: 1,
                pageSize: 100
            };
            await this.grnAndOdoTransferRecordDetailFn(params, record)
                .then((res) => {
                    this.DetailTableDatas = [];
                    this.billLadingBotDatasAll = [];
                    this.DetailTableLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        if (list.length) {
                            this.billLadingBotDatasAll = list.map((item) => {
                                if (item.status || parseInt(item.status) >= 0) {
                                    item.status_name = this.sheelStatus[item.status];
                                } else {
                                    item.status = item.tvStatus;
                                    item.tvStatus = item.tvStatus === 'AWAITING' ? '待完成' : '已完成';
                                    item.status_name = item.tvStatus;
                                }

                                if (typeof item.grant_quantity === 'undefined') {
                                    item.grant_quantity = item.quantity;
                                }

                                if (typeof item.quantity === 'undefined') {
                                    item.quantity = item.grant_quantity;
                                }

                                return {
                                    ...item,
                                    activeClass: '',
                                    key: item.detail_inner_sn,
                                    // status: item.status || item.tvStatus,
                                    // status_name: this.sheelStatus[item.status],
                                    price: item.quantity * item.unit_price,
                                    source_type: 'copy', // 定义一个参数，凡是添加产品过去的信息，删除后都不留痕迹
                                    product_vendor_name: item.product_vendor_name || item.vd_name || item.vendor_name,
                                    product_vendor_inner_sn:
                                        item.product_vendor_inner_sn || item.vd_inner_sn || item.vendor_inner_sn,
                                    vendor_inner_sn: item.product_vendor_inner_sn || item.vendor_inner_sn,
                                    vendor_name: item.product_vendor_name || item.vendor_name,
                                    unit: item.unit || item.unit_name,
                                    unit_name: item.unit || item.unit_name, // 这个是为了后边和发放单通用数据
                                    produce_date: item.produce_date === '0000-00-00' ? '' : item.produce_date
                                };
                            });
                            if (this.sheelType) {
                                this.billLadingBotDatasAll.forEach((item) => {
                                    if (
                                        item.status === 'AWAITING' ||
                                        parseFloat(item.status) === 0 ||
                                        parseFloat(item.status) === 2 ||
                                        item.tvStatus === '待完成'
                                    ) {
                                        this.DetailTableDatas.push(item);
                                        this.detailTableSelectedRowKeys.push(item.detail_inner_sn);
                                    }
                                });
                            } else {
                                this.DetailTableDatas = this.billLadingBotDatasAll;
                                this.billLadingBotDatasAll.forEach((item) => {
                                    this.detailTableSelectedRowKeys.push(item.detail_inner_sn);
                                });
                            }

                            this.billLadingBotDatasAll.forEach((item) => {
                                let isf = false;
                                this.delInfoDatas.forEach((itemS) => {
                                    if (
                                        item.product_type === itemS.product_type &&
                                        item.product_inner_sn === itemS.product_inner_sn &&
                                        item.vendor_inner_sn === itemS.vendor_inner_sn &&
                                        item.specification === itemS.specification
                                    ) {
                                        isf = true;
                                    }
                                });
                                if (isf) item.status = 'AWAITING';
                            });

                            if (mode !== undefined) {
                                // 当这里为点击点开的时候 可以添加删除过的信息
                                this.delInfoDatas.forEach((itemS) => {
                                    let isf = true;
                                    this.DetailTableDatas.forEach((item) => {
                                        if (
                                            item.product_type === itemS.product_type &&
                                            item.product_inner_sn === itemS.product_inner_sn &&
                                            item.vendor_inner_sn === itemS.vendor_inner_sn &&
                                            item.specification === itemS.specification
                                        ) {
                                            isf = false;
                                        }
                                    });
                                    if (isf) {
                                        itemS.key = this.DetailTableDatas.length + 1;
                                        itemS.product_vendor_inner_sn = itemS.vendor_inner_sn;
                                        itemS.product_vendor_name = itemS.vendor_name;
                                        itemS.produce_date = '';
                                        itemS.overdue_date = '';
                                        this.DetailTableDatas.push(itemS);
                                    }
                                });
                            }
                            if (this.DetailTableDatas.length) {
                                this.DetailTableCurrentSelectRow = this.DetailTableDatas[0];
                            } else {
                                this.DetailTableCurrentSelectRow = {};
                            }
                        } else {
                            this.DetailTableDatas = [];
                            this.billLadingBotDatasAll = [];
                        }
                        if (sheelTable) {
                            sheelTable.updateData();
                            sheelTable.setCheckboxRow(this.DetailTableDatas, true);
                            sheelTable.refreshColumn();
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.DetailTableLoading = false;
                });
        },

        /* 出入库接口拆分区域结束 */

        /* ***************************** 单据类型API区域 ******************************************************/
        /**
         * @入库调单
         * @单据类型
         * @abstract 入库调单可用的单据类型列表查询
         */
        async grnSheetListAPIFn() {
            this.detailTableSelectedRowKeys = [];
            this.checkText = '仅显示待入库细目';
            this.billLadingVoucherTypeList = [];
            const res = await grnSheetListAPI({ params: {} });
            if (parseFloat(res.code) === 0) {
                const { list = [] } = res;
                list.forEach((item) => {
                    this.billLadingVoucherTypeList.push({
                        value: item,
                        title: '',
                        label: '',
                        key: item
                    });
                });
                if (this.billLadingVoucherTypeList.length > 0) {
                    this.sheelID = this.billLadingVoucherTypeList[0].value;
                    this.UpdateColumns();
                }
                this.infoModalForm.id = this.sheelID;

                this.refVoucherTypeAPIFn();
                this.grnTransferRecordListAPIFn();
            }
        },
        /**
         * @出库调单
         * @单据类型
         * @abstract 出库调单可用的单据类型列表查询
         */
        async odoSheetListAPIFn() {
            this.detailTableSelectedRowKeys = [];
            this.checkText = '仅显示待出库细目';
            this.billLadingVoucherTypeList = [];
            const params = {
                pageNum: 1,
                pageSize: 100
            };
            await odoSheetListAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        list.forEach((item) => {
                            this.billLadingVoucherTypeList.push({ value: item, title: '', label: '', key: item });
                        });
                        if (this.billLadingVoucherTypeList.length > 0) {
                            this.sheelID = this.billLadingVoucherTypeList[0].value;
                            this.UpdateColumns();
                        }
                        this.infoModalForm.id = this.sheelID;
                        this.refVoucherTypeAPIFn();
                        if (this.sheelID === 'GRANT') {
                            // 医院 发放单
                            this.odoTransferGrantListAPIFn();
                        } else if (this.sheelID === 'SALE_RECORD') {
                            // 经销商 销售单
                            this.odoTransferRecordListAPIFn();
                        }
                    }
                })
                .catch(() => {});
        },
        /**
         * @收货调单
         * @单据类型
         * @abstract 收货调单可用的单据类型列表查询
         */
        async receiptSheetListAPIFn() {
            this.detailTableSelectedRowKeys = [];
            this.checkText = '仅显示待收货细目';
            this.billLadingVoucherTypeList = [];
            const params = {
                pageNum: 1,
                pageSize: 100
            };
            await receiptSheetListAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        list.forEach((item) => {
                            this.billLadingVoucherTypeList.push({ value: item, title: '', label: '', key: item });
                        });
                        if (this.billLadingVoucherTypeList.length > 0) {
                            this.sheelID = this.billLadingVoucherTypeList[0].value;
                            this.UpdateColumns();
                        }
                        this.infoModalForm.id = this.sheelID;
                        this.refVoucherTypeAPIFn();
                        this.receiptTransferRecordListAPIFn();
                    }
                })
                .catch(() => {});
        },
        /**
         * @调单共用
         * @关联单据类型
         * @abstract 关联单据类型 【入库页面的入库单选项卡 / 出库 / 收货共用API
         */
        async refVoucherTypeAPIFn() {
            await refVoucherTypeAPI()
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        for (let i = 0; i < this.billLadingVoucherTypeList.length; i++) {
                            let item = this.billLadingVoucherTypeList[i];
                            list.forEach((ltem) => {
                                if (item.key === ltem.name) {
                                    this.$set(this.billLadingVoucherTypeList, i, {
                                        ...this.billLadingVoucherTypeList[i],
                                        title: ltem.text,
                                        label: ltem.text
                                    });
                                }
                            });
                        }
                    }
                })
                .catch(() => {});
        },
        /* ***************************** 入库单API区域 ******************************************************/
        /**
         * @入库调单
         * @采购订单
         * @abstract 可用作入库单的采购订单查询
         */
        async grnTransferRecordListAPIFn() {
            this.MainTableDatas = [];
            const params = {
                pageNum: this.MainTablePagination.current,
                pageSize: this.MainTablePagination.pageSize,
                sn: this.searchKey, // 搜索关键字(单据编号)
                organizationName: this.searchOrg, // 搜索关键字(单位名称)
                key: this.searchName, // 搜索关键字(产品名称)
                voucherType: this.sheelID
            };

            if (this.sheelID === 'ORDER_PO') {
                const warehouse = this.warehouseList.find(
                    (item) => item.warehouse_model_inner_sn === this.searchWarehouseModelInnerSn
                );
                if (warehouse) {
                    params.warehouse_name = warehouse.title;
                }
            }

            await grnTransferRecordListAPI({ params })
                .then((res) => {
                    this.MainTablePagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;

                        this.MainTableDatas = rows.map((item) => {
                            item.key = item.org_id + '' + item.odo_inner_sn;
                            item.activeClass = '';
                            item.sn = item.gv_sn || item.sl_sn || item.voucher_sn || item.av_sn;
                            return item;
                        });
                        if (this.MainTableDatas.length) {
                            this.mainTableCurrentSelectRow = this.MainTableDatas[0];
                            this.$refs.sheelTop.setCurrentRow(this.mainTableCurrentSelectRow);
                            this.$refs.sheelTop.updateData();
                            this.grnTransferRecordDetailAPIFn(this.mainTableCurrentSelectRow);
                        } else {
                            this.MainTableDatas = [];
                            this.mainTableCurrentSelectRow = {};
                        }

                        this.MainTablePagination = {
                            ...this.MainTablePagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.MainTablePagination.loading = false;
                });
        },
        /**
         * @入库调单
         * @采购订单明细
         * @abstract 采购订单产品明细查询
         */
        async grnTransferRecordDetailAPIFn(record) {
            const sheelTable = this.$refs.sheelTable;
            this.infoModalForm.department_inner_sn = record.department_inner_sn;
            this.infoModalForm.department_name = record.department_name;
            this.infoModalForm.organization_name = record.organization_name;
            this.infoModalForm.organization_inner_sn = record.organization_inner_sn;
            this.infoModalForm.sl_inner_sn = record.sl_inner_sn || record.voucher_inner_sn_c || record.odo_inner_sn;
            this.infoModalForm.billsType = record.billsType;
            this.infoModalForm.grn_type = record.grn_type;
            this.infoModalForm.warehouse_name = record.warehouse_name;
            this.infoModalForm.warehouse_inner_sn = record.warehouse_inner_sn;
            this.infoModalForm.delivery_address = record.delivery_address;
            this.DetailTableDatas = [];
            this.billLadingBotDatasAll = [];
            this.detailTableSelectedRowKeys = [];
            sheelTable.clearCheckboxRow();
            this.DetailTableLoading = true;
            const params = {
                billsType: record.billsType,
                vId: this.infoModalForm.sl_inner_sn,
                voucherType: this.sheelID,
                organization_inner_sn: record.organization_inner_sn
            };
            await grnTransferRecordDetailAPI({ params })
                .then((res) => {
                    this.DetailTableDatas = [];
                    this.billLadingBotDatasAll = [];
                    this.DetailTableLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.billLadingBotDatasAll = list.map((item) => {
                            return {
                                ...item,
                                activeClass: '',
                                grant_quantity: item.grant_quantity, // 发放单不用写，销售单不知道
                                // quantity: item.grant_quantity,
                                key: item.detail_inner_sn,
                                status_name: this.sheelStatus[item.status],
                                price: item.quantity * item.unit_price,
                                source_type: 'copy' // 定义一个参数，凡是添加产品过去的信息，删除后都不留痕迹
                            };
                        });
                        if (this.sheelType) {
                            this.billLadingBotDatasAll.forEach((item) => {
                                if (item.status === 'AWAITING') {
                                    // AWAITING 为可选， COMPLETION为不可选
                                    this.DetailTableDatas.push(item);
                                    this.detailTableSelectedRowKeys.push(item.detail_inner_sn);
                                }
                            });
                        } else {
                            this.DetailTableDatas = this.billLadingBotDatasAll;
                            this.billLadingBotDatasAll.forEach((item) => {
                                this.detailTableSelectedRowKeys.push(item.detail_inner_sn);
                            });
                        }
                        this.billLadingBotDatasAll.forEach((item) => {
                            let isf = false;
                            this.delInfoDatas.forEach((itemS) => {
                                if (
                                    item.product_type === itemS.product_type &&
                                    item.product_inner_sn === itemS.product_inner_sn &&
                                    item.vendor_inner_sn === itemS.vendor_inner_sn &&
                                    item.specification === itemS.specification
                                ) {
                                    isf = true;
                                }
                            });
                            if (isf) item.status = 'AWAITING';
                        });
                        if (this.DetailTableDatas.length) {
                            this.DetailTableCurrentSelectRow = this.DetailTableDatas[0];
                        } else {
                            this.DetailTableCurrentSelectRow = {};
                        }
                        sheelTable.setCheckboxRow(this.DetailTableDatas, true);
                        sheelTable.refreshColumn();
                        // this.$refs.sheelTable.rowSelection.selectedRowKeys = []
                    } else {
                        this.DetailTableLoading = false;
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.DetailTableLoading = false;
                });
        },
        /* ***************************** 收货单API区域 ******************************************************/
        /**
         * @收货调单
         * @发放单
         * @abstract 可用作收货单的发放单查询
         */
        async receiptTransferRecordListAPIFn() {
            this.MainTableDatas = [];
            const params = {
                pageNum: this.MainTablePagination.current,
                pageSize: this.MainTablePagination.pageSize,
                key: this.searchKey // 搜索关键字(单据编号)
            };
            await receiptTransferRecordListAPI({ params })
                .then((res) => {
                    this.MainTablePagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;

                        this.MainTableDatas = rows.map((item) => {
                            item.key = item.hospital_inner_sn + '' + item.gv_inner_sn;
                            item.activeClass = '';
                            return item;
                        });
                        if (this.MainTableDatas.length) {
                            this.mainTableCurrentSelectRow = this.MainTableDatas[0];
                            this.$refs.sheelTop.setCurrentRow(this.mainTableCurrentSelectRow);
                            this.$refs.sheelTop.updateData();
                            this.receiptTransferRecordDetailAPIFn(this.mainTableCurrentSelectRow);
                        } else {
                            this.MainTableDatas = [];
                            this.mainTableCurrentSelectRow = {};
                        }

                        this.MainTablePagination = {
                            ...this.MainTablePagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.MainTablePagination.loading = false;
                });
        },
        /**
         * @收货调单
         * @发放单明细
         * @abstract 发放单的产品明细查询
         */
        async receiptTransferRecordDetailAPIFn(record) {
            this.infoModalForm.department_inner_sn = record.department_inner_sn;
            this.infoModalForm.department_name = record.department_name;
            this.infoModalForm.organization_name = record.organization_name;
            this.infoModalForm.organization_inner_sn = record.organization_inner_sn;
            this.infoModalForm.sl_inner_sn = record.sl_inner_sn;
            this.infoModalForm.billsType = record.billsType;
            this.DetailTableDatas = [];
            this.billLadingBotDatasAll = [];
            this.DetailTableLoading = true;
            const params = {
                pageNum: 1,
                pageSize: 100,
                billsType: record.billsType,
                sid: record.sl_inner_sn
            };
            await receiptTransferRecordDetailAPI({ params })
                .then((res) => {
                    this.DetailTableDatas = [];
                    this.billLadingBotDatasAll = [];
                    this.DetailTableLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.billLadingBotDatasAll = list.map((item) => {
                            return {
                                ...item,
                                activeClass: '',
                                grant_quantity: item.grant_quantity, // 发放单不用写，销售单不知道
                                // quantity: item.grant_quantity,
                                key: item.detail_inner_sn,
                                status_name: this.sheelStatus[item.status],
                                price: item.quantity * item.unit_price,
                                source_type: 'copy' // 定义一个参数，凡是添加产品过去的信息，删除后都不留痕迹
                            };
                        });
                        if (this.sheelType) {
                            this.billLadingBotDatasAll.forEach((item) => {
                                if (item.status === 'AWAITING') {
                                    // AWAITING 为可选， COMPLETION为不可选
                                    this.DetailTableDatas.push(item);
                                }
                            });
                        } else {
                            this.DetailTableDatas = this.billLadingBotDatasAll;
                        }
                        this.billLadingBotDatasAll.forEach((item) => {
                            let isf = false;
                            this.delInfoDatas.forEach((itemS) => {
                                if (
                                    item.product_type === itemS.product_type &&
                                    item.product_inner_sn === itemS.product_inner_sn &&
                                    item.vendor_inner_sn === itemS.vendor_inner_sn &&
                                    item.specification === itemS.specification
                                ) {
                                    isf = true;
                                }
                            });
                            if (isf) item.status = 'AWAITING';
                        });
                        if (this.DetailTableDatas.length) {
                            this.DetailTableCurrentSelectRow = this.DetailTableDatas[0];
                        } else {
                            this.DetailTableCurrentSelectRow = {};
                        }
                        this.$refs.sheelTable.refreshColumn();
                        // this.$refs.sheelTable.rowSelection.selectedRowKeys = []
                    } else {
                        this.DetailTableLoading = false;
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.DetailTableLoading = false;
                });
        },
        /* ***************************** 出库单API区域 ******************************************************/
        /**
         * @出库调单
         * @发放单
         * @abstract 可用作出库单的发放单查询
         */
        async odoTransferGrantListAPIFn() {
            this.MainTableDatas = [];
            const params = {
                pageNum: this.MainTablePagination.current,
                pageSize: this.MainTablePagination.pageSize,
                // isAll: false,
                key1: this.searchName, // 搜索关键字(产品名称)
                key: this.searchKey // 搜索关键字(单据编号)
            };
            // let oid = this.$cookie.get('userbelong')
            await odoTransferGrantListAPI({ params })
                .then((res) => {
                    this.MainTablePagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.MainTableDatas = rows.map((item) => {
                            item.key = item.hospital_inner_sn + '' + item.gv_inner_sn;
                            item.activeClass = '';
                            item.sn = item.gv_sn || item.sl_sn || item.voucher_sn || item.av_sn;
                            return item;
                        });
                        if (this.MainTableDatas.length) {
                            this.mainTableCurrentSelectRow = this.MainTableDatas[0];
                            this.$refs.sheelTop.setCurrentRow(this.mainTableCurrentSelectRow);
                            this.$refs.sheelTop.updateData();
                            this.odoTransferGrantDetailAPIFn(this.mainTableCurrentSelectRow);
                        } else {
                            this.MainTableDatas = [];
                            this.mainTableCurrentSelectRow = {};
                        }

                        this.MainTablePagination = {
                            ...this.MainTablePagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.MainTablePagination.loading = false;
                });
        },
        /**
         * @出库调单
         * @发放单明细
         * @abstract 发放单产品明细查询
         */
        async odoTransferGrantDetailAPIFn(record, mode) {
            this.infoModalForm.department_inner_sn = record.department_inner_sn;
            this.infoModalForm.department_name = record.department_name;
            this.infoModalForm.organization_name = record.hospital_name;
            this.infoModalForm.organization_inner_sn = record.hospital_inner_sn;
            this.infoModalForm.sl_inner_sn = record.gv_inner_sn;
            this.infoModalForm.warehouse_inner_sn = record.warehouse_inner_sn || '';
            this.infoModalForm.warehouse_name = record.warehouse_name || '';
            this.infoModalForm.billsType = 'HH';
            this.infoModalForm.grn_type = 'HH';
            this.DetailTableDatas = [];
            this.billLadingBotDatasAll = [];
            if (mode === undefined) {
                this.sheelListData = [];
            }
            this.DetailTableLoading = true;
            const params = {
                gvid: record.gv_inner_sn
            };
            await odoTransferGrantDetailAPI({ params })
                .then((res) => {
                    this.DetailTableDatas = [];
                    this.billLadingBotDatasAll = [];
                    this.DetailTableLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.billLadingBotDatasAll = list.map((item) => {
                            return {
                                ...item,
                                activeClass: '',
                                grant_quantity: item.grant_quantity, // 发放单不用写，销售单不知道
                                // quantity: item.grant_quantity,
                                key: item.detail_inner_sn,
                                status_name: this.sheelStatus[item.status],
                                source_type: 'copy' // 定义一个参数，凡是添加产品过去的信息，删除后都不留痕迹
                            };
                        });
                        if (this.sheelType) {
                            this.billLadingBotDatasAll.forEach((item) => {
                                if (parseFloat(item.status) === 0) {
                                    this.DetailTableDatas.push(item);
                                }
                            });
                        } else {
                            this.DetailTableDatas = this.billLadingBotDatasAll;
                        }
                        if (mode !== undefined) {
                            // 当这里为点击点开的时候 可以添加删除过的信息
                            this.billLadingBotDatasAll.forEach((item) => {
                                let isf = false;
                                this.delInfoDatas.forEach((itemS) => {
                                    if (
                                        item.product_type === itemS.product_type &&
                                        item.product_inner_sn === itemS.product_inner_sn &&
                                        item.vendor_inner_sn === itemS.vendor_inner_sn &&
                                        item.specification === itemS.specification
                                    ) {
                                        isf = true;
                                    }
                                });
                                if (isf) item.status = 0;
                            });
                        }
                        if (this.DetailTableDatas.length) {
                            this.DetailTableCurrentSelectRow = this.DetailTableDatas[0];
                        } else {
                            this.DetailTableCurrentSelectRow = {};
                        }
                        this.$refs.sheelTable.refreshColumn();
                    } else {
                        this.DetailTableLoading = false;
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.DetailTableLoading = false;
                });
        },
        /**
         * @出库调单
         * @销售单
         * @abstract 可用作出库单的销售单查询
         */
        async odoTransferRecordListAPIFn() {
            this.MainTableDatas = [];
            const params = {
                pageNum: this.MainTablePagination.current,
                pageSize: this.MainTablePagination.pageSize,
                organizationName: this.searchOrg, // 搜索关键字(单位名称)
                key1: this.searchName, // 搜索关键字(产品名称)
                key: this.searchKey // 搜索关键字(单据编号)
            };
            await odoTransferRecordListAPI({ params })
                .then((res) => {
                    this.MainTablePagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.MainTableDatas = rows.map((item) => {
                            item.key = item.sl_inner_sn + '' + item.seller_inner_sn;
                            item.activeClass = '';
                            item.sn = item.gv_sn || item.sl_sn || item.voucher_sn || item.av_sn;
                            return item;
                        });
                        if (this.MainTableDatas.length) {
                            this.mainTableCurrentSelectRow = this.MainTableDatas[0];
                            this.$refs.sheelTop.setCurrentRow(this.mainTableCurrentSelectRow);
                            this.$refs.sheelTop.updateData();
                            this.odoTransferRecordDetailAPIFn(this.mainTableCurrentSelectRow);
                        } else {
                            this.MainTableDatas = [];
                            this.mainTableCurrentSelectRow = {};
                        }
                        this.MainTablePagination = {
                            ...this.MainTablePagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.MainTablePagination.loading = false;
                });
        },
        /**
         * @出库调单
         * @销售单明细
         * @abstract 销售单产品明细查询
         */
        async odoTransferRecordDetailAPIFn(record, mode) {
            this.infoModalForm.department_inner_sn = record.department_inner_sn;
            this.infoModalForm.department_name = record.department_name;
            this.infoModalForm.organization_name = record.organization_name;
            this.infoModalForm.organization_inner_sn = record.organization_inner_sn;
            this.infoModalForm.sl_inner_sn = record.sl_inner_sn;
            this.infoModalForm.billsType = record.billsType;
            this.odrder_type_m = record.billsType;
            if (record.org_id) this.org_id_m = record.org_id;
            this.sl_inner_sn_m = record.sl_inner_sn;
            this.DetailTableDatas = [];
            this.billLadingBotDatasAll = [];
            this.sl_inner_sn_m = record.sl_inner_sn;
            let params = {
                billsType: record.billsType,
                pageNum: 1,
                pageSize: 100,
                sid: record.sl_inner_sn
            };
            await odoTransferRecordDetailAPI({ params })
                .then((res) => {
                    this.DetailTableDatas = [];
                    this.billLadingBotDatasAll = [];
                    this.DetailTableLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        if (list.length) {
                            this.billLadingBotDatasAll = list.map((item) => {
                                return {
                                    ...item,
                                    activeClass: '',
                                    grant_quantity: item.quantity, // 这个是为了后边和发放单通用数据
                                    unit_name: item.unit, // 这个是为了后边和发放单通用数据
                                    key: item.detail_inner_sn,
                                    vendor_inner_sn: item.product_vendor_inner_sn,
                                    vendor_name: item.product_vendor_name,
                                    produce_date: item.produce_date === '0000-00-00' ? '' : item.produce_date
                                };
                            });
                            if (this.sheelType) {
                                this.billLadingBotDatasAll.forEach((item) => {
                                    if (item.status === 'AWAITING') {
                                        this.DetailTableDatas.push(item);
                                    }
                                });
                            } else {
                                this.DetailTableDatas = this.billLadingBotDatasAll;
                            }
                            if (mode !== undefined) {
                                // 当这里为点击点开的时候 可以添加删除过的信息
                                this.delInfoDatas.forEach((itemS) => {
                                    let isf = true;
                                    this.DetailTableDatas.forEach((item) => {
                                        if (
                                            item.product_type === itemS.product_type &&
                                            item.product_inner_sn === itemS.product_inner_sn &&
                                            item.vendor_inner_sn === itemS.vendor_inner_sn &&
                                            item.specification === itemS.specification
                                        ) {
                                            isf = false;
                                        }
                                    });
                                    if (isf) {
                                        itemS.key = this.DetailTableDatas.length + 1;
                                        itemS.product_vendor_inner_sn = itemS.vendor_inner_sn;
                                        itemS.product_vendor_name = itemS.vendor_name;
                                        itemS.produce_date = '';
                                        itemS.overdue_date = '';
                                        this.DetailTableDatas.push(itemS);
                                    }
                                });
                            }
                            if (this.DetailTableDatas.length) {
                                this.DetailTableCurrentSelectRow = this.DetailTableDatas[0];
                            } else {
                                this.DetailTableCurrentSelectRow = {};
                            }
                        } else {
                            this.DetailTableDatas = [];
                            this.billLadingBotDatasAll = [];
                        }
                        if (this.$refs.sheelTable) this.$refs.sheelTable.updateData();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.DetailTableLoading = false;
                });
        },
        /**
         * @出库调单
         * @调拨单
         * @abstract 可用作出库单的调拨单查询
         */
        async allotTransferVoucherAPIAPIFn() {
            this.MainTableDatas = [];
            const params = {
                pageNum: this.MainTablePagination.current,
                pageSize: this.MainTablePagination.pageSize,
                key: this.searchKey, // 搜索关键字(单据编号)
                key1: this.searchName // 搜索关键字(产品名称)
            };

            if (this.parameter === 'TYPE_GRN') {
                this.checkText = '仅显示待入库细目';
                params.status = 2;
            } else if (this.parameter === 'TYPE_ODO') {
                this.checkText = '仅显示待出库细目';
            }

            await allotTransferVoucherAPI({ params })
                .then((res) => {
                    this.MainTablePagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;

                        this.MainTableDatas = rows.map((item) => {
                            return {
                                ...item,
                                sl_sn: item.av_sn,
                                sn: item.gv_sn || item.sl_sn || item.voucher_sn || item.av_sn,
                                organization_name: '',
                                seller_name: '',
                                department_name: ''
                            };
                        });
                        if (this.MainTableDatas.length) {
                            this.mainTableCurrentSelectRow = this.MainTableDatas[0];
                            this.$refs.sheelTop.setCurrentRow(this.mainTableCurrentSelectRow);
                            this.$refs.sheelTop.updateData();
                            this.AllotTransferDetailAPIFn(this.mainTableCurrentSelectRow);
                        } else {
                            this.MainTableDatas = [];
                            this.mainTableCurrentSelectRow = {};
                        }

                        this.MainTablePagination = {
                            ...this.MainTablePagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.MainTablePagination.loading = false;
                });
        },
        /**
         * @出库调单
         * @调拨单明细
         * @abstract 调拨单产品明细查询
         */
        async AllotTransferDetailAPIFn(record) {
            const departments = JSON.parse(this.$cookie.get('loginDatas')).Departments;
            this.infoModalForm.department_inner_sn = departments[0].Department_inner_sn;
            this.infoModalForm.department_name = departments[0].Name;
            this.infoModalForm.organization_inner_sn = this.userBelong;
            this.infoModalForm.organization_name = JSON.parse(this.$cookie.get('EmployeeInfo')).OrgName;
            this.infoModalForm.sl_inner_sn = record.av_inner_sn;
            this.infoModalForm.billsType = this.systemType === 1 ? 'HH' : this.systemType === 2 ? 'DD' : 'VV';
            this.infoModalForm.warehouse_name =
                this.parameter === 'TYPE_ODO' ? record.source_warehouse_name : record.target_warehouse_name;
            this.infoModalForm.warehouse_inner_sn =
                this.parameter === 'TYPE_ODO' ? record.source_warehouse_inner_sn : record.target_warehouse_inner_sn;
            this.DetailTableLoading = true;
            const params = {
                avid: record.av_inner_sn,
                pageNum: 1,
                pageSize: 10
            };
            if (this.parameter === 'TYPE_GRN') {
                params.status = 2;
            }
            /*
            } else if (this.parameter === 'TYPE_ODO') {
            }
            */
            await AllotTransferDetailAPI({ params })
                .then((res) => {
                    this.DetailTableDatas = [];
                    this.billLadingBotDatasAll = [];
                    this.DetailTableLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.billLadingBotDatasAll = list.map((item) => {
                            item.status = item.tvStatus;
                            item.tvStatus = item.tvStatus === 'AWAITING' ? '待完成' : '已完成';
                            item.status_name = item.tvStatus;
                            return {
                                ...item,
                                unit: item.unit_name,
                                product_vendor_name: item.vd_name,
                                product_vendor_inner_sn: item.vd_inner_sn,
                                activeClass: ''
                            };
                        });

                        this.billLadingBotDatasAll.forEach((v) => {
                            if (v.status === 'AWAITING') {
                                this.DetailTableDatas.push(v);
                            }
                        });
                    } else {
                        this.DetailTableLoading = false;
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.DetailTableLoading = false;
                });
        },

        /* ***************************** 页面方法区域 ******************************************************/

        /**
         * 搜索绑定方法
         */
        handleSearch() {
            this.MainTablePagination.current = 1;
            this.getMainTableData();
        },
        /**
         * 获取上方表格数据
         */
        getMainTableData() {
            this.UpdateColumns();

            if (this.isSeparateBillOrder) {
                // 出入库单拆分
                this.grnAndOdoTransferRecordListAPIFn();
            } else {
                switch (this.parameter) {
                    // 入库调单
                    case 'TYPE_GRN':
                        if (this.sheelID === 'ALLOT') {
                            this.allotTransferVoucherAPIAPIFn();
                        } else {
                            this.grnTransferRecordListAPIFn();
                        }
                        break;
                    // 出库调单
                    case 'TYPE_ODO':
                        switch (this.sheelID) {
                            // 【医院】 发放单
                            case 'GRANT':
                                this.odoTransferGrantListAPIFn();
                                break;
                            // 【经销商】 销售单
                            case 'SALE_RECORD':
                                this.odoTransferRecordListAPIFn();
                                break;
                            // 【医院/经销商】 调拨单
                            case 'ALLOT':
                                this.allotTransferVoucherAPIAPIFn();
                                break;
                        }
                        break;
                    // 收货调单
                    case 'receiving':
                        this.receiptTransferRecordListAPIFn();
                        break;
                }
            }
        },
        /**
         * 页面初始化方法
         */
        init() {
            this.clearDefaultDataFn();
            this.sheelType = true;

            // 出入库单拆分
            if (this.isSeparateBillOrder) {
                this.grnAndOdoSheetListAPIFn();
            } else {
                switch (this.parameter) {
                    // 入库调单
                    case 'TYPE_GRN':
                        this.grnSheetListAPIFn();
                        break;
                    // 出库调单
                    case 'TYPE_ODO':
                        this.odoSheetListAPIFn();
                        break;
                    // 收货调单
                    case 'receiving':
                        this.receiptSheetListAPIFn();
                        break;
                }
            }
        },
        /**
         * 向父组件提交数据的方法
         */
        returnList() {
            this.infoModalForm.id = this.sheelID;

            // 解决一个产品都未勾选，但是产品容器还有值的问题
            let returnDatas = this.detailTableSelectedRowKeys.length <= 0 ? [] : this.DetailTableDatas;

            this.$emit('returnData', this.detailTableSelectedRowKeys, returnDatas, this.infoModalForm);

            if (returnDatas.length) {
                this.clearDefaultDataFn();
            }
        },
        /**
         * 下方表格点击绑定方法
         */
        DetailTableOnClickFn({ record }) {
            this.DetailTableCurrentSelectRow = record;
        },
        /**
         * 初始化数据
         */
        clearDefaultDataFn() {
            // 默认搜索数据清空
            this.searchKey = '';
            this.searchOrg = '';
            this.searchName = '';
            this.sheelID = '';

            this.MainTableDatas = [];
            this.DetailTableDatas = [];
            this.billLadingBotDatasAll = [];
            this.MainTablePagination = {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            };
            this.detailTableSelectedRowKeys = [];
        },
        /**
         * 上方表格列点击绑定方法
         * @param row
         */
        MainTableOnClickFn({ row }) {
            // 初始化数据
            this.DetailTableDatas = [];
            this.detailTableSelectedRowKeys = [];
            this.DetailTableCurrentSelectRow = {};

            this.mainTableCurrentSelectRow = row;

            // 出入库单拆分
            if (this.isSeparateBillOrder) {
                this.grnAndOdoTransferRecordDetailAPIFn(row);
            } else {
                switch (this.parameter) {
                    // 入库调单
                    case 'TYPE_GRN':
                        if (this.sheelID === 'ALLOT') {
                            this.AllotTransferDetailAPIFn(row);
                        } else {
                            this.grnTransferRecordDetailAPIFn(row);
                        }
                        break;
                    // 出库调单
                    case 'TYPE_ODO':
                        switch (this.sheelID) {
                            // 【医院】发放单
                            case 'GRANT':
                                this.odoTransferGrantDetailAPIFn(row);
                                break;
                            // 【经销商】销售单
                            case 'SALE_RECORD':
                                this.odoTransferRecordDetailAPIFn(row);
                                break;
                            // 【医院/经销商】调拨单
                            case 'ALLOT':
                                this.AllotTransferDetailAPIFn(row);
                                break;
                        }
                        break;
                    // 收货调单
                    case 'receiving':
                        this.receiptTransferRecordDetailAPIFn(row);
                        break;
                }
            }
        },
        /**
         * 下方表格筛选条件勾选框绑定方法
         * @param e 勾选事件
         */
        sheelOnchangeFn(e) {
            this.DetailTableLoading = true;
            this.sheelType = e.target.checked;
            this.DetailTableDatas = [];
            if (this.MainTableDatas.length <= 0) {
                this.billLadingBotDatasAll = [];
            }
            if (this.sheelType) {
                this.billLadingBotDatasAll.forEach((item) => {
                    if (
                        item.status === 'AWAITING' ||
                        parseFloat(item.status) === 0 ||
                        parseFloat(item.status) === 2 ||
                        item.tvStatus === '待完成'
                    ) {
                        this.DetailTableDatas.push(item);
                    }
                });
            } else {
                this.DetailTableDatas = this.billLadingBotDatasAll;
            }
            if (this.$refs.sheelTable) this.$refs.sheelTable.updateData();
            this.DetailTableLoading = false;
        },
        /**
         * 下方表格checkbox-config绑定方法
         * @param row 勾选的数据
         */
        DetailTableCheckConfig({ row }) {
            return row.status === 'AWAITING' || row.status === 0;
        },
        /**
         * 下方表格产品勾选绑定事件
         * @param checked
         * @param row
         */
        DetailTableOnCheck({ checked, row }) {
            if (checked) {
                this.detailTableSelectedRowKeys.push(row.detail_inner_sn);
            } else {
                this.detailTableSelectedRowKeys.forEach((item, index) => {
                    if (item === row.detail_inner_sn) {
                        this.detailTableSelectedRowKeys.splice(index, 1);
                    }
                });
            }
        },
        /**
         * 下方表格产品全选绑定事件
         * @param checked
         */
        DetailTableOnCheckAll({ checked }) {
            if (checked) {
                this.detailTableSelectedRowKeys = [];
                this.DetailTableDatas.forEach((item) => {
                    if (item.status === 'AWAITING' || item.status === 0) {
                        this.detailTableSelectedRowKeys.push(item.detail_inner_sn);
                    }
                });
            } else {
                this.detailTableSelectedRowKeys = [];
            }
        },
        /**
         * 上方表格换页时绑定方法
         * @param currentPage
         * @param pageSize
         */
        MainTablePaginationOnChange({ currentPage, pageSize }) {
            this.MainTablePagination.current = currentPage;
            this.MainTablePagination.pageSize = pageSize;
            this.MainTablePagination.loading = true;
            this.DetailTableDatas = [];
            this.billLadingBotDatasAll = [];
            this.detailTableSelectedRowKeys = [];

            this.getMainTableData();
        },
        /**
         * 选择合适的表格列
         */
        UpdateColumns() {
            const type = this.parameter + '_' + this.sheelID;
            switch (type) {
                // 入库调采购订单
                case 'TYPE_GRN_ORDER_PO':
                    this.MainTableColumns = this.MainTableColumnsGrnBuy;
                    this.DetailTableColumns = this.DetailTableColumnsGrnCommon;
                    break;
                // 入库调出库单
                case 'TYPE_GRN_ODO':
                    this.MainTableColumns = [
                        ...this.MainTableColumnsGrnBuy.slice(0, 2),
                        ...this.MainTableColumnsGrnBuy.slice(3, 2)
                    ];
                    this.DetailTableColumns = this.DetailTableColumnsGrnCommon;
                    break;
                // 入库调调拨单
                case 'TYPE_GRN_ALLOT':
                    this.MainTableColumns = this.MainTableColumnsAllot;
                    this.DetailTableColumns = this.DetailTableColumnsGrnAllot;
                    break;
                // 出库库调销售单
                case 'TYPE_ODO_SALE_RECORD':
                    this.MainTableColumns = this.MainTableColumnsOdoSale;
                    this.DetailTableColumns = this.DetailTableColumnsOdoSale;
                    break;
                // 出库库调发放单
                case 'TYPE_ODO_GRANT':
                    this.MainTableColumns = this.MainTableColumnsOdoGrant;
                    this.DetailTableColumns = this.DetailTableColumnsOdoGrant;
                    break;
                // 出库库调调拨单
                case 'TYPE_ODO_ALLOT':
                    this.MainTableColumns = this.MainTableColumnsAllot;
                    this.DetailTableColumns = this.DetailTableColumnsOdoAllot;
                    break;
            }
        }
    },
    computed: {
        // 是否是出入库拆分的七大类单
        isSeparateBillOrder() {
            return this.orderTypeData && this.orderTypeData.value;
        },
        // 是否显示单位名称输入框
        isShowOrgNameInput() {
            if (this.isSeparateBillOrder) {
                return !(
                    this.orderTypeData.value === grnAndOdoOrderType['ALLOTGRN'].value ||
                    this.orderTypeData.value === grnAndOdoOrderType['ALLOTODO'].value ||
                    this.orderTypeData.value === grnAndOdoOrderType['GRANTODO'].value
                );
            } else {
                return this.systemType !== 1 || this.parameter !== 'TYPE_ODO';
            }
        }
    },
    watch: {
        /**
         * 父组件传递的数据，确定是从什么界面进入当前组件
         */
        parameter: {
            handler(newVal) {
                if (newVal) {
                    this.parameter = newVal;
                }
                this.clearDefaultDataFn();
            }
        },
        /**
         * 上方表格数据
         */
        MainTableDatas(newDatas) {
            // 销售单不存在时，底部的产品列表也不要显示数据,这里直接清空
            if (newDatas && newDatas.length === 0) {
                this.DetailTableDatas = [];
            }
        },
        /**
         * 所选调单的单据类型
         * @val ORDER_PO 采购订单
         *      SALE_RECORD 销售单
         *      ALLOT 调拨单
         */
        sheelID() {}
    },
    mounted() {
        this.init();
    }
};
</script>
