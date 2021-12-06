<template>
    <div style="height: 490px">
        <div class="tabs-product-wrap clear-pt">
            <div class="tabs-product-header mb-20">
                <ul class="tabs-product-nav">
                    <li
                        @click="changeTabsFn('return-product')"
                        :class="activeTab === 'return-product' ? 'active-tab' : ''"
                    >
                        {{ modalTitle || '可退货' }}产品
                    </li>
                    <li
                        @click="changeTabsFn('platform-product')"
                        :class="activeTab === 'platform-product' ? 'active-tab' : ''"
                    >
                        {{ tabNavTitle }}
                    </li>
                </ul>
            </div>
            <ul class="tabs-product-content">
                <!-- 可退货产品 | 出库产品 = 入库产品 -->
                <li v-show="activeTab === 'return-product'">
                    <returnSearch
                        :is_GRN="is_GRN"
                        :formData="formData"
                        :searchData="searchData"
                        @searchData="searchDataFn"
                    ></returnSearch>
                    <div
                        class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                        style="padding-bottom: 0; margin-top: 0"
                    >
                        <vxe-grid
                            ref="orderAddReturnTable"
                            size="small"
                            :height="294 - ipadHeight"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            align="center"
                            :data="searchTableDatas"
                            :loading="searchTablePagination.loading"
                            show-overflow
                            show-header-overflow
                            showHeader
                            :checkbox-config="{ reserve: true, highlight: true, range: false }"
                            @checkbox-change="checkboxChange"
                            @checkbox-all="checkboxAll"
                            @cell-dblclick="onGrnAndOdoProductAdd"
                            :start-index="(searchTablePagination.current - 1) * searchTablePagination.pageSize"
                        >
                            <vxe-table-column fixed="left" type="checkbox"></vxe-table-column>
                            <!-- <vxe-table-column type="seq" title="NO." width="50"></vxe-table-column> -->
                            <vxe-table-column field="sn" title="单据编号" width="140"></vxe-table-column>
                            <vxe-table-column field="organization_name" title="单位名称" width="200"></vxe-table-column>
                            <!-- <vxe-table-column field="warehouse_name" title="仓库" width="120"></vxe-table-column> -->
                            <vxe-table-column field="product_name" title="产品" width="150"></vxe-table-column>
                            <vxe-table-column field="specification" title="规格型号" width="90"></vxe-table-column>
                            <vxe-table-column field="product_vendor_name" title="厂商" width="120"></vxe-table-column>
                            <vxe-table-column field="batch_number" title="批号" width="90"></vxe-table-column>
                            <vxe-table-column field="quantity" title="数量" width="70"></vxe-table-column>
                            <vxe-table-column field="unit" title="单位" width="70"></vxe-table-column>
                            <vxe-table-column field="unit_price" title="含税单价" width="90"></vxe-table-column>
                            <vxe-table-column field="rate_return" title="税率" width="70"></vxe-table-column>
                            <vxe-table-column field="produce_date" title="生产日期" width="90"></vxe-table-column>
                            <vxe-table-column field="overdue_date" title="失效日期" width="90"></vxe-table-column>
                            <vxe-table-column field="mdrf_sn" title="注册证号" width="90"></vxe-table-column>
                            <vxe-table-column field="health_care_sn" title="医保编号" width="90"></vxe-table-column>
                        </vxe-grid>
                        <vxe-pager
                            :loading="searchTablePagination.loading"
                            :current-page="searchTablePagination.current"
                            :page-size="searchTablePagination.pageSize"
                            :page-sizes="searchTablePagination.pageSizes"
                            :total="searchTablePagination.total"
                            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                            style="background: #ffffff"
                            @page-change="searchTablePaginationOnChange"
                        >
                        </vxe-pager>
                    </div>
                </li>
                <!-- 库存产品 | 平台产品： 采购退货调用的是销售出库的产品，销售退货调用的是采购入库产品 -->
                <li v-show="activeTab === 'platform-product'">
                    <add-product
                        ref="chooseProMd"
                        @returnData="onPlatformProductReturn"
                        @returnWarehouseData="getWarehouseReturn"
                        :partB="partB"
                        :contractProductCheckable="contractProductCheckable"
                        :isWarehouseDisabled="true"
                        :orderTypeData="orderTypeData"
                        :scrollYVenNumber="274 - ipadHeight"
                    ></add-product>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
// import addProduct from '@/components/addProduct'
import { grnAndOdoOrderType } from '@/config/constants';
import { grnAndOdoInvoiceOrderType } from '@/views/commonPage/invoiceManage/config/constants';
import {
    purchaseReturnProductListAPI,
    saleReturnVendorListAPI,
    invoiceGrnProductListAPI,
    invoiceOdoProductListAPI
} from '@/service/pageAjax';

export default {
    // 出入库退货单的添加产品列表
    name: 'returnMain',
    props: {
        modalTitle: {
            type: String,
            default: ''
        },

        // 页面标识 [
        // 1. 当前只有出入库单的采购退货和销售退货（出入库单的单据 [采购退货: grnAndOdoOrderType['SALERETURN'].value   销售退货：grnAndOdoOrderType['PURCHASERETURN'].value）
        // 2. 发票管理模块业务人员的出入库发票单grnAndOdoInvoiceOrderType['GRN_INVOICE']、grnAndOdoInvoiceOrderType['ODO_INVOICE']]
        orderTypeData: {
            type: Object,
            retuired: true
        },
        // 表单info数据
        formData: {
            type: Object,
            retuired: true
        },
        /// 是否是入库单
        is_GRN: {
            type: Boolean,
            default: true
        },
        /// 添加产品的入参 [库存产品 | 平台产品]
        partB: Object
    },
    data() {
        return {
            activeTab: 'return-product', // return-product: 可退货产品  platform-product: 平台产品
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 57 : 0,
            searchData: {
                begin_date: '',
                end_date: '',
                name: '',
                product_vendor_name: '',
                sn: '',
                specification: '',
                warehouse_inner_sn: '',
                warehouse_name: ''
            },
            searchTableDatas: [],
            searchTablePagination: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            selectAllCheckboxRecords: {
                0: [],
                1: []
            },
            filterSelectAllCheckboxRecords: [],
            platformAddReturnData: {
                // 库存产品 | 平台产品
                'dea': [],
                'ven': []
            },
            chooseOrgType: 'return-product',
            contractProductCheckable: false,
            
        };
    },
    components: {
        'addProduct': () => import('@/components/addProduct'),
        'returnSearch': () => import('./returnSearch')
    },
    methods: {
        platformWarehouseReturnDataFn() {
            if (this.isPurchaseReturn) {
                this.clearTabCheckCurrentFn();
                this.searchTableDatas = [];

                this.searchDataFn(this.searchData);
            }
        },
        changeTabsFn(key) {
            // 切换tab
            this.activeTab = key;
            this.chooseOrgType = key;
        },
        searchDataFn(searchData) {
            this.searchData = searchData;
            this.searchTablePagination = {
                loading: true,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            };
            this.searchListAPIFn();
        },
        getSearchListFn(params) {
            /// 排序 01
            params.isAsc = 'desc';
            if (this.orderReturnType === 0) {
                if (this.isPurchaseReturn) {
                    /// 排序 02
                    params.orderByColumn = 'grn_sn';

                    return purchaseReturnProductListAPI({ params });
                } else {
                    // this.orderTypeData.value === grnAndOdoOrderType['SALERETURN'].value
                    /// 排序 02
                    params.orderByColumn = 'odo_sn';

                    return saleReturnVendorListAPI({ params });
                }
            } else if (this.orderReturnType === 1) {
                delete params.warehouse_inner_sn;

                if (this.orderTypeDataVal === grnAndOdoInvoiceOrderType['GRN_INVOICE'].value) {
                    /// 排序 02
                    params.orderByColumn = 'in_time';

                    return invoiceGrnProductListAPI({ params });
                } else {
                    /// 排序 02
                    params.orderByColumn = 'out_time';

                    return invoiceOdoProductListAPI({ params });
                }
            } else {
                console.log('未知的单据来源!');
            }
        },
        // 【搜索】出入库单的退货列表
        async searchListAPIFn() {
            const xTable = this.$refs.orderAddReturnTable;

            this.searchTableDatas = [];
            this.searchTablePagination.loading = true;
            let params = {
                org_id: this.formData.org_id || this.$cookie.get('userbelong'),
                organization_inner_sn: this.formData.organization_inner_sn,
                warehouse_inner_sn: this.formData.warehouse_inner_sn || this.searchData.warehouse_inner_sn || '',
                billsType: this.formData.billsType || this.formData.bills_type,
                pageNum: this.searchTablePagination.current,
                pageSize: this.searchTablePagination.pageSize
            };

            if (this.searchData.begin_date) {
                params.begin_date = this.searchData.begin_date;
            }
            if (this.searchData.end_date) {
                params.end_date = this.searchData.end_date;
            }
            if (this.searchData.name) {
                params.name = this.searchData.name;
            }
            if (this.searchData.product_vendor_name) {
                params.product_vendor_name = this.searchData.product_vendor_name;
            }
            if (this.searchData.sn) {
                params.sn = this.searchData.sn;
            }
            if (this.searchData.specification) {
                params.specification = this.searchData.specification;
            }

            await this.getSearchListFn(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.searchTableDatas = rows.map((item) => {
                            item.batch_locked_quantity = 0;
                            item.batchDatas = [];
                            item.tax_unit_price = item.unit_price;
                            item.money = parseFloat(item.quantity)
                                ? (item.quantity * item.tax_unit_price).toFixed(4) / 1
                                : '';
                            item.unitPriceList = [];
                            item.inventoryBannce = 0;
                            item.rate = Math.floor(item.rate * 100);
                            item.max_batch_locked_quantity = 0;
                            if (typeof item.min_unit_info === 'undefined') {
                                item.min_unit_info = 1; // 默认值
                                item.min_measure = 1; // 默认值
                            }
                            item.average_price = 0;
                            item.average_price_compare = 0;

                            return {
                                ...item,
                                organization_name: this.formData.organization_name || '',
                                unit_inner_sn: item.unit ? [item.unit_inner_sn] : [],
                                unit_name: item.unit || '',
                                unitList: item.unit ? [{ label: item.unit, value: item.unit_inner_sn }] : [],
                                name: item.product_name,
                                isActive: '',
                                product_type: typeof item.product_type === 'undefined' ? 1 : item.product_type,
                                vendor_name:
                                    item.vendor_name ||
                                    item.product_vendor_name ||
                                    item.product_dealer_name ||
                                    item.dealer_name,
                                vendor_inner_sn: item.vendor_inner_sn || item.product_vendor_inner_sn,
                                dealer_inner_sn:
                                    item.dealer_inner_sn ||
                                    item.product_vendor_inner_sn ||
                                    item.vendor_inner_sn ||
                                    item.product_dealer_inner_sn,
                                key: Math.random(),
                                type: item.type || '0',
                                // mdrf_inner_sn: item.mdrf_inner_sn || '',
                                mdrfList: item.mdrf_inner_sn
                                    ? [{ value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn }]
                                    : [],
                                isLoadingUnit: false,
                                action: -1,
                                rate_return: `${item.rate} %` // 当前表格中的税率显示
                            };
                        });
                        xTable.refreshColumn();
                        xTable.loadData(this.searchTableDatas);
                        if (this.searchTableDatas.length) {
                            xTable.setCurrentRow(this.searchTableDatas[0]);
                            if (this.filterSelectAllCheckboxRecords.length) {
                                this.filterSelectAllCheckboxRecords.forEach((ftem) => {
                                    this.searchTableDatas.forEach((item) => {
                                        if (item.id === ftem.id) {
                                            xTable.toggleCheckboxRow(item);
                                        }
                                    });
                                });
                            } else {
                                xTable.clearCheckboxRow();
                            }
                        }

                        this.searchTablePagination = {
                            ...this.searchTablePagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.searchTablePagination.loading = false;
                });
        },
        searchTablePaginationOnChange({ currentPage, pageSize }) {
            this.searchTablePagination = {
                ...this.searchTablePagination,
                current: currentPage,
                pageSize: pageSize,
                loading: true
            };
            this.searchTableDatas = [];
            this.searchListAPIFn();
        },
        checkboxChange({ checked, row }) {
            if (checked) {
                this.$refs.orderAddReturnTable.setCurrentRow(row);
            }
            this.clearKeybordTabAddRowFn(checked, row);
            this.setCheckboxReserveRecordsFn([row], checked);
        },
        checkboxAll({ checked, records }) {
            if (!checked) {
                records = this.$refs.orderAddReturnTable.getTableData().tableData;
            }
            this.setCheckboxReserveRecordsFn(records, checked);
        },
        clearKeybordTabAddRowFn() {
            for (let key in this.selectAllCheckboxRecords) {
                let records = this.selectAllCheckboxRecords[key];
                if (typeof records === 'object') {
                    if (records && records.length) {
                        for (let i = 0; i < records.length; i++) {
                            let rtem = records[i],
                                productType = parseInt(rtem.product_type),
                                productTypeName = productType === 0 ? 'dealer' : 'vendor',
                                curKeyFormat = rtem.id + productTypeName;
                            if (rtem.isKeybordTabAdd) {
                                delete this.selectAllCheckboxRecords[curKeyFormat];
                                records.splice(i, 1);
                                i--;
                            }
                        }
                    }
                }
            }
        },
        setCheckboxReserveRecordsFn(records, checked) {
            let firstCheckRecord = checked ? records[0] : {};

            for (let i = 0; i < records.length; i++) {
                let item = records[i],
                    productType = parseInt(item.product_type),
                    productTypeName = productType === 0 ? 'dealer' : 'vendor',
                    curKeyFormat = item.id + productTypeName;

                if (checked) {
                    // 勾选上
                    const compareKeyFormatAry = Object.values(this.selectAllCheckboxRecords).slice(2);
                    if (compareKeyFormatAry.some((cItem) => cItem === curKeyFormat)) continue;
                    item.isLoadingUnit = false;
                    item.row_add_flag = true; // 新增行标识
                    item.temp_add_flag = true; // 新增标识
                    this.selectAllCheckboxRecords[productType].push(item);
                    this.selectAllCheckboxRecords[curKeyFormat] = curKeyFormat;
                } else {
                    for (let k = 0; k < this.selectAllCheckboxRecords[productType].length; k++) {
                        let selectItem = this.selectAllCheckboxRecords[productType][k];
                        if (selectItem.id + productTypeName === curKeyFormat) {
                            this.selectAllCheckboxRecords[productType].splice(k, 1);
                            delete this.selectAllCheckboxRecords[curKeyFormat];
                        }
                    }
                }
            }

            this.filterWarehouseCheckboxRecordsFn(firstCheckRecord);
        },
        filterWarehouseCheckboxRecordsFn(firstCheckRecord) {
            let selectAllCheckboxRecordsVendor = this.selectAllCheckboxRecords[0],
                selectAllCheckboxRecordsDealer = this.selectAllCheckboxRecords[1];

            if (
                (selectAllCheckboxRecordsVendor.length || selectAllCheckboxRecordsDealer.length) &&
                !(this.formData.warehouse_inner_sn || this.searchData.warehouse_inner_sn)
            ) {
                const tempSelectAllCheckboxRecords = [
                    ...selectAllCheckboxRecordsVendor,
                    ...selectAllCheckboxRecordsDealer
                ];

                this.filterSelectAllCheckboxRecords = tempSelectAllCheckboxRecords.filter(
                    (item) => item.warehouse_inner_sn.toString() === firstCheckRecord.warehouse_inner_sn.toString()
                );

                this.searchData.warehouse_inner_sn = firstCheckRecord.warehouse_inner_sn;
                this.searchData.warehouse_name = firstCheckRecord.warehouse_name;
                this.partB.warehouse_inner_sn = firstCheckRecord.warehouse_inner_sn;

                // 是否是出入库单的退货产品来源
                if (this.orderReturnTypeNum === 0) {
                    this.isWarehouseParentDisabled = true;
                    this.searchListAPIFn();
                }
            } else {
                this.filterSelectAllCheckboxRecords = [];
            }
        },
        clearTabCheckCurrentFn() {
            this.selectAllCheckboxRecords = {
                0: [],
                1: []
            };
            this.selectAllCheckboxRecords[0] = [];
            this.selectAllCheckboxRecords[1] = [];
        },
        returnDataFn() {
            const chooseOrgMdRef = this.$refs.chooseProMd;
            let addReturnDatas = [],
                tempCheckboxRecords = [...this.selectAllCheckboxRecords[0], ...this.selectAllCheckboxRecords[1]];

            if (this.filterSelectAllCheckboxRecords.length) {
                addReturnDatas = this.filterSelectAllCheckboxRecords;
            } else if (tempCheckboxRecords.length) {
                addReturnDatas = tempCheckboxRecords;
            } else {
                const row = this.$refs.orderAddReturnTable.getCurrentRecord();
                if (row) {
                    this.searchData.warehouse_inner_sn = row.warehouse_inner_sn;
                    this.searchData.warehouse_name = row.warehouse_name;
                    addReturnDatas = [row];
                } else {
                    addReturnDatas = [];
                }
            }

            // 数据构造完毕将临时容器数据置空
            this.clearTabCheckCurrentFn();
            this.filterSelectAllCheckboxRecords = [];

            // 入（出）库产品选型卡激活时
            if (this.activeTab === 'return-product') {
                this.platformAddReturnData = {
                    'dea': [],
                    'ven': []
                };
                this.$emit('dataAdd', {
                    grnAndOdoProduct: addReturnDatas
                });
            }

            // 平台产品选项卡激活时
            if (this.activeTab === 'platform-product') {
                chooseOrgMdRef.returnProList();
                addReturnDatas = [];
                if (!this.isPurchaseReturn) {
                    this.searchData.warehouse_inner_sn = '';
                    this.searchData.warehouse_name = '';
                }
            }

            // 清除容器数据
            if (
                addReturnDatas.length > 0 &&
                (this.platformAddReturnData.ven.length > 0 || this.platformAddReturnData.dea.length > 0)
            ) {
                chooseOrgMdRef.resetDataFn();
                chooseOrgMdRef.clearCheckboxRowFn();
            }

            return {
                addReturnDatas,
                platformAddReturnData: this.platformAddReturnData,
                searchData: this.searchData
            };
        },
        /**
         * 入库或出库产品添加时（双击表格行）
         * @param {object} payload 事件携带数据
         * @param {object} payload.row 行数据
         */
        onGrnAndOdoProductAdd({ row }) {
            this.$emit('dataAdd', {
                grnAndOdoProduct: [row]
            });
        },
        /**
         * 平台产品返回值时（addProduct组件双击事件）
         * @param {array} dea 经销商产品
         * @param {array} ven 厂商产品
         */
        onPlatformProductReturn(dea, ven) {
            // 获取回调产品
            this.platformAddReturnData = {
                'dea': dea,
                'ven': ven
            };
            this.$emit('dataAdd', {
                platformProduct: { dea, ven }
            });
        },
        getWarehouseReturn(warehouseData) {
            // 设置仓库
            if (warehouseData) {
                this.searchData.warehouse_inner_sn = warehouseData.warehouse_inner_sn;
                this.searchData.warehouse_name = warehouseData.warehouse_name;
                this.platformWarehouseReturnDataFn();
            }
        },
        initAddProductParamsFn() {
            // 退货单屏蔽掉鼠标控制添加产品
            this.partB.isKeyCode = false;

            // 退货单就隐藏合同内产品筛选
            this.contractProductCheckable = false;

            // 退货单的外层仓库如果有值 内层添加产品筛选仓库就只读
            if (this.partB.warehouse_inner_sn) {
                this.isWarehouseParentDisabled = true;
            } else {
                // 仓库没有值就清空默认值
                this.searchData.warehouse_inner_sn = '';
                this.searchData.warehouse_name = '';
            }
        }
    },
    computed: {
        orderTypeDataVal() {
            // 单据的数据来源
            return this.orderTypeData && this.orderTypeData.value;
        },
        // 是否是退货产品来源，还是出入库发票，并且返回数字用以判断
        orderReturnType() {
            if (
                this.orderTypeDataVal === grnAndOdoOrderType['PURCHASERETURN'].value ||
                this.orderTypeDataVal === grnAndOdoOrderType['SALERETURN'].value
            ) {
                // 出入库单
                return 0;
            } else if (
                this.orderTypeDataVal === grnAndOdoInvoiceOrderType['GRN_INVOICE'].value ||
                this.orderTypeDataVal === grnAndOdoInvoiceOrderType['ODO_INVOICE'].value
            ) {
                // 发票管理业务人员的出入库发票
                return 1;
            }
            return -1;
        },
        isPurchaseReturn() {
            // 是否是采购退货产品， true-采购退货， false-销售退货
            return this.orderTypeDataVal === grnAndOdoOrderType['PURCHASERETURN'].value;
        },
        tabNavTitle() {
            return this.isPurchaseReturn ? '库存产品' : '平台产品';
        }
    },
    mounted() {
        this.initAddProductParamsFn();

        this.clearTabCheckCurrentFn();
        this.filterSelectAllCheckboxRecords = [];
        this.platformAddReturnData = {
            'dea': [],
            'ven': []
        };

        this.searchListAPIFn();
    }
};
</script>

<style lang="less" scoped>
.clear-pt {
    padding-top: 0;
}

.mb-20 {
    margin-bottom: 20px;
}
</style>
