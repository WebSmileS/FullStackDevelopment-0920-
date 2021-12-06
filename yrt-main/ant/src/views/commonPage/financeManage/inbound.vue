<template>
    <div class="scoped scoped-white page-finance-inbound">
        <h1 class="page_title">{{ title }}</h1>
        <div class="search-form-wrap">
            <a-form
                v-if="bizType === 'stock'"
                class="search-form"
                layout="horizontal"
                :colon="false"
                :form="form"
                :label-col="{ span: 6 }"
                :wrapper-col="{ span: 18 }"
            >
                <a-row class="btn-top-wrap" :gutter="16">
                    <a-col :span="6" class="ant-col">
                        <a-form-item label="仓库名称">
                            <a-tree-select
                                v-decorator="['warehouse_model_inner_sn']"
                                treeNodeFilterProp="title"
                                showSearch
                                style="margin-top: 4px"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                :treeData="warehouseTreeData"
                                placeholder="请选择"
                                treeDefaultExpandAll
                                allowClear
                                @change="handleWarehouseNameChange"
                            >
                            </a-tree-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="6" class="ant-col">
                        <a-form-item label="仓库人员">
                            <a-tree-select
                                v-decorator="['employee_inner_sn']"
                                treeNodeFilterProp="title"
                                showSearch
                                style="margin-top: 4px"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                :treeData="warehouseOperatorTreeData"
                                placeholder="请选择"
                                treeDefaultExpandAll
                                allowClear
                            >
                            </a-tree-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="6" class="ant-col">
                        <a-form-item label="产品名称">
                            <a-input
                                v-decorator="['product_name']"
                                placeholder="请输入要查询的名称"
                                autocomplete="off"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :span="6" class="operation-col ant-col">
                        <a-button type="primary" ghost @click="handleReset" class="btn-reset" :disabled="loading"
                            >重置</a-button
                        >
                        <a-button type="primary" :loading="loading" @click="handlePreSearch" class="btn-search"
                            >查询</a-button
                        >
                        <a-button @click="handleExport" class="btn-export"
                            ><span class="btn-text-export">导出至</span>
                            <i class="yrt yrt-excel"></i>
                        </a-button>
                    </a-col>
                </a-row>
            </a-form>
            <a-form
                v-else
                class="search-form"
                layout="horizontal"
                :colon="false"
                :form="form"
                :label-col="{ span: 6 }"
                :wrapper-col="{ span: 18 }"
            >
                <a-row class="btn-top-wrap" :gutter="16">
                    <a-col :span="5" class="ant-col">
                        <a-form-item :label="bizType === 'inbound' ? '供货单位' : '单位名称'">
                            <a-input
                                allowClear
                                v-decorator="['organization_name']"
                                placeholder="请输入要查询的名称"
                                autocomplete="off"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :span="5" class="ant-col">
                        <a-form-item label="部门">
                            <a-tree-select
                                treeNodeFilterProp="title"
                                v-decorator="['department_inner_sn']"
                                showSearch
                                :treeData="departmentTreeData"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                treeDefaultExpandAll
                                allowClear
                                class="input"
                                placeholder="请选择"
                                searchPlaceholder="请搜索"
                                style="margin-top: 4px; min-width: 120px; height: 32px"
                            >
                            </a-tree-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="5" class="ant-col">
                        <a-form-item label="仓库">
                            <a-tree-select
                                v-decorator="['warehouse_model_inner_sn']"
                                treeNodeFilterProp="title"
                                showSearch
                                :style="{ marginTop: '4px' }"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                :treeData="warehouseTreeData"
                                placeholder="请选择"
                                searchPlaceholder="请搜索"
                                treeDefaultExpandAll
                                allowClear
                            >
                            </a-tree-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="5" class="ant-col">
                        <a-form-item label="产品名称">
                            <a-input v-decorator="['product_name']" placeholder="请输入" autocomplete="off" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4" class="ant-col">
                        <a-form-item :label="bizType === 'inbound' ? '入库日期' : '出库日期'">
                            <a-range-picker style="width: 100%" v-decorator="['range-date']" format="YYYY-MM-DD" />
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-row class="btn-top-wrap" :gutter="16" style="margin-top: 15px">
                    <a-col :span="5" class="ant-col">
                        <a-form-item label="生产厂家">
                            <product-vender-input
                                allowClear
                                placeholder="请选择"
                                v-decorator="['product_vendor_name']"
                                @click="handleClickProductVender"
                                @clear="handleClearProductVender"
                            />
                        </a-form-item>
                        <div style="display: none">
                            <a-form-item label="生产厂家id（hidden）">
                                <a-input v-decorator="['product_vendor_inner_sn']" autocomplete="off" />
                            </a-form-item>
                            <a-form-item label="生产厂家类型（hidden）">
                                <a-input v-decorator="['product_type']" autocomplete="off" />
                            </a-form-item>
                        </div>
                    </a-col>
                    <a-col :span="5" class="handler_name_col ant-col">
                        <a-form-item label="经手人">
                            <a-input
                                v-decorator="['handler_name']"
                                autocomplete="off"
                                placeholder="请输入"
                                class="handler_name_input"
                                style="width: 77px"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :span="5" class="ant-col">
                        <a-form-item label="单据编号">
                            <a-input v-decorator="['sn']" placeholder="请输入" autocomplete="off" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="9" class="operation-col ant-col">
                        <a-button type="primary" ghost @click="handleReset" class="btn-reset" :disabled="loading"
                            >重置</a-button
                        >
                        <a-button type="primary" :loading="loading" @click="handlePreSearch" class="btn-search"
                            >查询</a-button
                        >
                        <a-button @click="handleExport" class="btn-export" :loading="exporting"
                            ><span class="btn-text-export">导出至</span>
                            <i class="yrt yrt-excel"></i>
                        </a-button>
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <div style="margin-top: 5px; background: #fff">
            <vxe-table
                ref="table"
                size="small"
                :height="scrollYNumber"
                border
                resizable
                auto-resize
                highlight-current-row
                align="center"
                :data="dataSource"
                :loading="loading"
                show-header-overflow
            >
                <template v-if="bizType === 'stock'">
                    <vxe-table-column
                        v-for="column in stockTableColumns"
                        :key="column.key"
                        :field="column.field"
                        :title="column.title"
                        :width="column.width"
                        show-overflow
                    >
                    </vxe-table-column>
                </template>
                <template v-else>
                    <vxe-table-column
                        v-for="column in columns"
                        :key="column.key"
                        :field="column.field"
                        :title="column.title"
                        :width="column.width"
                        show-overflow
                    >
                    </vxe-table-column>
                </template>
            </vxe-table>
            <vxe-pager
                :loading="loading"
                :current-page="pagination.current"
                :page-size="pagination.pageSize"
                :page-sizes="pagination.pageSizes"
                :total="pagination.total"
                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                :pager-count="4"
                @page-change="handlePageChange"
            >
                <template v-slot:left>
                    <div v-if="bizType !== 'stock'" class="pricesum_wrapper">
                        <span class="pricesum_label">总金额：</span>
                        <span class="pricesum_value">¥ {{ priceSum }}</span>
                    </div>
                </template>
            </vxe-pager>
        </div>
        <search-modal
            :bizType="bizType"
            :selectedOrg="selectedOrg"
            :visible="searchModalVisible"
            :warehouseTreeData="warehouseTreeData"
            :departmentTreeData="departmentTreeData"
            :initialWarehouseOperatorTreeData="warehouseOperatorTreeData"
            :allWarehouseOperatorTreeData="allWarehouseOperatorTreeDataCache"
            @onsubmit="handleSearchModalSubmit"
            @oncancel="handleCancelSearchModal"
            @onopenchooseorg="handleOpenChooseOrgModal"
        />
        <choose-org-modal
            :visible="chooseOrgModalVisible"
            :pageType="9"
            @onsubmit="handleChooseOrgModalSubmit"
            @oncancel="chooseOrgModalVisible = false"
        />
    </div>
</template>

<script>
import moment from 'moment';
import {
    orgDeptTopTreeAPI,
    financeWeListAPI,
    warehouseUsableTopListAPI,
    searchFinanceInboundListAPI,
    searchFinanceOutboundListAPI,
    searchFinanceStockListAPI,
    exportFinanceInboundListAPI,
    exportFinanceOutboundListAPI,
    exportFinanceStockListAPI,
    getFinanceGrnSumAPI,
    getFinanceOdoSumAPI
} from '@/service/pageAjax';
import ChooseOrgModal from './module/chooseOrgModal';
import SearchModal from './module/searchModal';
import ProductVenderInput from './module/productVenderInput';

const DateTimeFormat = 'YYYY-MM-DD';

export default {
    name: 'finance-inbound',
    props: {
        // inbound: 入库查询 | outbound: 出库查询 | stock: 库存查询
        bizType: {
            type: String,
            default: 'inbound'
        }
    },
    data() {
        return {
            form: this.$form.createForm(this, { name: 'search_form' }), // 查询表单
            priceSum: 0, // 出入库金额合计
            warehouseTreeData: [], // 仓库列表数据
            warehouseOperatorTreeData: [], // 仓库人员列表数据
            allWarehouseOperatorTreeDataCacheCache: [], // 所有的仓库人员列表数据
            dataSource: [], // 表格数据源
            departmentTreeData: [], // 部门列表
            loading: false, // 表格loading
            exporting: false, // 导出loading状态
            searchModalVisible: true,
            chooseOrgModalVisible: false,
            productVenderInputFocus: true,
            selectedOrg: {
                name: '',
                id: '',
                type: ''
            },
            columns: [
                // 表格列
                {
                    title: '单据编号',
                    field: 'grn_sn',
                    key: 'grn_sn',
                    width: '9%'
                },
                {
                    title: '供货单位',
                    field: 'organization_name',
                    key: 'organization_name',
                    width: '10%'
                },
                {
                    title: '产品',
                    field: 'product_name',
                    key: 'product_name',
                    width: '12%'
                },
                {
                    title: '规格型号',
                    field: 'specification',
                    key: 'specification',
                    width: '9%'
                },
                {
                    title: '单位',
                    field: 'unit_name',
                    key: 'unit_name',
                    width: 50
                },
                {
                    title: '数量',
                    field: 'quantity',
                    key: 'quantity'
                },
                {
                    title: '采购价',
                    field: 'unit_price',
                    key: 'unit_price'
                },
                {
                    title: '金额',
                    field: 'amount_of_money',
                    key: 'amount_of_money'
                },
                {
                    title: '税率',
                    field: 'rate',
                    key: 'rate',
                    width: 66
                },
                {
                    title: '采购员',
                    field: 'buyer_name',
                    key: 'buyer_name'
                },
                {
                    title: '仓库',
                    field: 'warehouse_name',
                    key: 'warehouse_name'
                },
                {
                    title: '部门',
                    field: 'department_name',
                    key: 'department_name',
                    width: 90
                },
                {
                    title: '经手人',
                    field: 'handler_name',
                    key: 'handler_name',
                    width: 60
                },
                {
                    title: '批号',
                    field: 'batch_number',
                    key: 'batch_number'
                },
                {
                    title: '入库时间',
                    field: 'in_time',
                    key: 'in_time'
                },
                {
                    title: '厂商',
                    field: 'product_vendor_name',
                    key: 'product_vendor_name'
                },
                {
                    title: '备注',
                    field: 'description',
                    key: 'description',
                    width: 50
                }
            ],
            outboundColumns: [
                // 表格列
                {
                    title: '单据编号',
                    field: 'odo_sn',
                    key: 'odo_sn',
                    width: '9%'
                },
                {
                    title: '单位名称',
                    field: 'organization_name',
                    key: 'organization_name',
                    width: '10%'
                },
                {
                    title: '产品',
                    field: 'product_name',
                    key: 'product_name',
                    width: '12%'
                },
                {
                    title: '规格型号',
                    field: 'specification',
                    key: 'specification',
                    width: '9%'
                },
                {
                    title: '单位',
                    field: 'unit_name',
                    key: 'unit_name',
                    width: 50
                },
                {
                    title: '成本价',
                    field: 'buying_price',
                    key: 'buying_price'
                },
                {
                    title: '数量',
                    field: 'quantity',
                    key: 'quantity'
                },
                {
                    title: '销售价',
                    field: 'unit_price',
                    key: 'unit_price'
                },
                {
                    title: '金额',
                    field: 'amount_of_money',
                    key: 'amount_of_money'
                },
                {
                    title: '税率',
                    field: 'rate',
                    key: 'rate',
                    width: 66
                },
                {
                    title: '仓库',
                    field: 'warehouse_name',
                    key: 'warehouse_name'
                },
                {
                    title: '部门',
                    field: 'department_name',
                    key: 'department_name',
                    width: 90
                },
                {
                    title: '经手人',
                    field: 'handler_name',
                    key: 'handler_name',
                    width: 60
                },
                {
                    title: '批号',
                    field: 'batch_number',
                    key: 'batch_number'
                },
                {
                    title: '出库时间',
                    field: 'out_time',
                    key: 'out_time'
                },
                {
                    title: '厂商',
                    field: 'product_vendor_name',
                    key: 'product_vendor_name'
                },
                {
                    title: '生产日期',
                    field: 'produce_date',
                    key: 'produce_date',
                    width: 80
                },
                {
                    title: '失效日期',
                    field: 'overdue_date',
                    key: 'overdue_date',
                    width: 80
                },
                {
                    title: '注册证',
                    field: 'mdrf_sn',
                    key: 'mdrf_sn'
                },
                {
                    title: '备注',
                    field: 'description',
                    key: 'description',
                    width: 50
                }
            ],
            stockTableColumns: [
                {
                    title: '产品',
                    field: 'product_name',
                    key: 'product_name'
                },
                {
                    title: '规格型号',
                    field: 'specification',
                    key: 'specification'
                },
                {
                    title: '数量',
                    field: 'quantity',
                    key: 'quantity',
                    width: 120
                },
                {
                    title: '包装单位',
                    field: 'unit',
                    key: 'unit',
                    width: 80
                },
                {
                    title: '成本均价（元）',
                    field: 'unit_price',
                    key: 'unit_price',
                    width: 150
                },
                {
                    title: '库存金额（元）',
                    field: 'amount_of_money',
                    key: 'amount_of_money'
                },
                {
                    title: '厂商/经销商',
                    field: 'product_vendor_name',
                    key: 'product_vendor_name'
                },
                {
                    title: '仓库',
                    field: 'warehouse_name',
                    key: 'warehouse_name',
                    width: 110
                },
                {
                    title: '仓库人员',
                    field: 'employee_name',
                    key: 'employee_name',
                    width: 100
                },
                {
                    title: '批号',
                    field: 'batch_number',
                    key: 'batch_number',
                    width: 110
                },
                {
                    title: '失效日期',
                    field: 'overdue_date',
                    key: 'overdue_date',
                    width: 110
                }
            ],
            pagination: {
                pageSize: 20,
                current: 1,
                total: 0,
                pageSizes: [20]
            }
        };
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            // 281: 199 + 82 - 28
            // 345: 263 + 82 - 28
            return window.innerHeight - (this.bizType === 'stock' ? 253 : 302);
        },

        title() {
            switch (this.bizType) {
                case 'inbound':
                    return '采购入库查询统计表';
                case 'outbound':
                    return '销售出库查询统计表';
                case 'stock':
                    return '库存查询统计表';
                default:
                    return '';
            }
        }
    },

    methods: {
        // 初始化
        init() {
            this.getWarehouseTopList();
            this.getDepartments();
            if (this.bizType === 'stock') {
                this.getWarehouseOperators(undefined, true);
            }
            if (this.bizType === 'outbound') {
                this.columns = this.outboundColumns;
            }
        },
        handlePreSearch() {
            this.pagination.current = 1;
            this.handleSearch();
            this.getPriceSum();
        },
        // 查询
        async handleSearch() {
            if (this.loading) {
                return;
            }
            try {
                this.loading = true;
                const fieldsValue = this.form.getFieldsValue();
                const service = this.getService('search');
                const { current, pageSize } = this.pagination;
                const sortFilterData = this.getSortFilter();
                const formData = this.getFormData(fieldsValue);
                const { rows = [], total } = await service(
                    current,
                    pageSize,
                    sortFilterData.orderByColumn,
                    sortFilterData.isAsc,
                    formData
                );
                this.dataSource = this.getFilterDataSource(rows);
                this.pagination.total = total;
            } catch (error) {
                console.error(error);
            } finally {
                this.loading = false;
            }
        },

        // 重置
        handleReset() {
            this.form.resetFields();
            this.pagination.current = 1;
            if (this.bizType === 'stock') {
                this.getWarehouseOperators();
            }
            this.handleSearch();
            this.getPriceSum();
        },

        // 导出excel
        async handleExport() {
            if (this.exporting) {
                return;
            }
            this.exporting = true;
            const hideMessage = this.$message.loading('导出中...');
            try {
                const service = this.getService('export');
                const formData = this.getFormData(this.form.getFieldsValue());
                const { data, headers } = await service(formData);
                const contentDisposition = headers['content-disposition'] || '';
                const matchedFilenames = contentDisposition.match(/fileName=(.*)/);
                const filename =
                    matchedFilenames.length > 1
                        ? decodeURIComponent(matchedFilenames[1])
                        : `${moment().format('YYYY-MM-DD')}.xlsx`;
                this.$Utils.download(data, filename);
            } catch (error) {
                console.error(error);
            } finally {
                this.exporting = false;
                hideMessage();
            }
        },

        // 页数变化
        handlePageChange({ pageSize, currentPage }) {
            this.pagination.pageSize = pageSize;
            this.pagination.current = currentPage;
            this.handleSearch();
        },

        // 查询Modal提交回调
        handleSearchModalSubmit(fieldsValue, otherResData) {
            // 把查询模态框中的仓库人员列表数据回填到主页面
            if (otherResData.warehouseOperatorTreeData) {
                this.warehouseOperatorTreeData = otherResData.warehouseOperatorTreeData;
            }
            this.form.setFieldsValue(fieldsValue);
            this.searchModalVisible = false;
            this.handleSearch();
            this.getPriceSum();
        },

        handleCancelSearchModal() {
            this.form.resetFields();
            if (this.bizType !== 'stock') {
                this.selectedOrg = {
                    name: '',
                    id: '',
                    type: ''
                };
            }
            this.searchModalVisible = false;
        },

        handleOpenChooseOrgModal() {
            this.$Utils.globalDragCenterFn('finance-choose-org-modal');
            this.chooseOrgModalVisible = true;
        },

        // 选择生产厂家（机构）回调
        handleChooseOrgModalSubmit(selectedOrg) {
            // 来自查询modal框的确定提交
            if (this.searchModalVisible) {
                this.selectedOrg = selectedOrg;
            } else {
                this.form.setFieldsValue({
                    product_vendor_inner_sn: selectedOrg.id,
                    product_vendor_name: selectedOrg.name,
                    product_type: selectedOrg.type
                });
            }
            this.chooseOrgModalVisible = false;
        },

        // 仓库名称change
        handleWarehouseNameChange(wId) {
            this.form.resetFields(['employee_inner_sn']);
            this.getWarehouseOperators(wId);
        },

        handleClickProductVender() {
            this.$Utils.globalDragCenterFn('finance-choose-org-modal');
            this.chooseOrgModalVisible = true;
        },

        handleClearProductVender() {
            this.form.resetFields(['product_vendor_inner_sn', 'product_vendor_name', 'product_type']);
        },

        // 获取一级仓库列表
        async getWarehouseTopList() {
            try {
                const { code, list = [], msg } = await warehouseUsableTopListAPI();
                if (code === 0) {
                    this.warehouseTreeData = list.map((item) => ({
                        ...item,
                        title: item.name,
                        value: item.warehouse_model_inner_sn,
                        key: item.warehouse_model_inner_sn
                    }));
                } else {
                    this.$message.error(msg);
                }
            } catch (error) {
                console.error(error);
            }
        },

        /**
         * 获取仓库人员
         * @param wid
         * @param isInit 是否是第一次进行请求，用于缓存全量仓库人员数据
         */
        async getWarehouseOperators(wid, isInit) {
            if (!wid && !isInit) {
                this.warehouseOperatorTreeData = this.allWarehouseOperatorTreeDataCache;
                return;
            }
            try {
                const { code, info = [], msg } = await financeWeListAPI({ params: { wid } });
                if (code === 0) {
                    this.warehouseOperatorTreeData = info.map((item) => ({
                        ...item,
                        title: item.employee_name,
                        value: item.employee_inner_sn,
                        key: item.employee_inner_sn
                    }));
                    if (isInit) {
                        this.allWarehouseOperatorTreeDataCache = JSON.parse(
                            JSON.stringify(this.warehouseOperatorTreeData)
                        );
                    }
                } else {
                    this.$message.error(msg);
                }
            } catch (error) {
                console.error(error);
            }
        },

        // 获取部门
        async getDepartments() {
            try {
                const { list = [], msg, code } = await orgDeptTopTreeAPI();
                if (code === 0) {
                    this.departmentTreeData = list.map(function walk(item) {
                        const hasChildren = item.children && item.children.length > 0;
                        return {
                            ...item,
                            value: item.department_inner_sn,
                            label: item.name,
                            isLeaf: !hasChildren,
                            children: hasChildren ? item.children.map(walk) : undefined
                        };
                    });
                } else {
                    this.$message.error(msg);
                }
            } catch (error) {
                console.error(error);
            }
        },
        // 排序字段
        getSortFilter() {
            const sortFilter = {
                isAsc: 'desc'
            };
            switch (this.bizType) {
                case 'inbound':
                    sortFilter.orderByColumn = 'in_time';
                    break;
                case 'stock':
                    sortFilter.orderByColumn = 'warehouse_inner_sn';
                    break;
                case 'outbound':
                    sortFilter.orderByColumn = 'out_time';
                    break;
            }
            return sortFilter;
        },
        // 获取查询表单值
        getFormData(fieldsValue) {
            const orgId = this.$cookie.get('userbelong');
            if (this.bizType === 'stock') {
                return {
                    ...fieldsValue,
                    org_id: orgId
                };
            }
            const [beginDateMoment, endDateMoment] = fieldsValue['range-date'] || [];

            return {
                org_id: orgId,
                begin_date: beginDateMoment ? beginDateMoment.format(DateTimeFormat) : undefined,
                end_date: endDateMoment ? endDateMoment.format(DateTimeFormat) : undefined,
                handler_name: fieldsValue['handler_name'],
                organization_name: fieldsValue['organization_name'],
                product_name: fieldsValue['product_name'],
                sn: fieldsValue['sn'],
                warehouse_model_inner_sn: fieldsValue['warehouse_model_inner_sn'],
                department_inner_sn: fieldsValue['department_inner_sn'],
                [fieldsValue['product_type'] === 2 ? 'product_dealer_inner_sn' : 'product_vendor_inner_sn']:
                    fieldsValue['product_vendor_inner_sn']
            };
        },

        /**
         * 根据操作类型获取对应API
         * type: 'search' | 'export'
         */
        getService(type) {
            if (type === 'search') {
                switch (this.bizType) {
                    case 'inbound':
                        return searchFinanceInboundListAPI;
                    case 'outbound':
                        return searchFinanceOutboundListAPI;
                    case 'stock':
                        return searchFinanceStockListAPI;
                    default:
                        return null;
                }
            } else {
                switch (this.bizType) {
                    case 'inbound':
                        return exportFinanceInboundListAPI;
                    case 'outbound':
                        return exportFinanceOutboundListAPI;
                    case 'stock':
                        return exportFinanceStockListAPI;
                    default:
                        return null;
                }
            }
        },

        // 对数字类型相关字段进行过滤处理
        getFilterDataSource(rows = []) {
            const transferNum = (num, digits) => {
                if (typeof num !== 'number') {
                    return null;
                }
                if (num === 0) {
                    return 0;
                }
                return num.toFixed(digits);
            };

            if (this.bizType === 'stock') {
                return rows.map((row) => ({
                    ...row,
                    quantity: transferNum(row.quantity, 3),
                    unit_price: transferNum(row.unit_price, 4),
                    amount_of_money: transferNum(row.amount_of_money, 4)
                }));
            }
            return rows.map((row) => ({
                ...row,
                buying_price: transferNum(row.buying_price, 4),
                unit_price: transferNum(row.unit_price, 4),
                quantity: transferNum(row.quantity, 3),
                amount_of_money: transferNum(row.amount_of_money, 4),
                rate: row.rate === 0 || row.rate === null ? 0 : `${this.$Utils.accMul(row.rate, 100).toFixed(2)}%`,
                produce_date: row.produce_date && moment(row.produce_date).format(DateTimeFormat),
                overdue_date: row.overdue_date && moment(row.overdue_date).format(DateTimeFormat)
            }));
        },

        // 获取出入库查询列表价格合计
        async getPriceSum() {
            if (this.bizType === 'stock') {
                return;
            }
            const service = this.bizType === 'inbound' ? getFinanceGrnSumAPI : getFinanceOdoSumAPI;
            const fieldsValue = this.form.getFieldsValue();
            const formData = this.getFormData(fieldsValue);
            try {
                const { sum = 0 } = await service(formData);
                this.priceSum = sum === 0 ? 0 : typeof sum === 'number' ? sum.toFixed(4) : 0;
            } catch (error) {
                console.error(error);
            }
        }
    },

    created() {
        this.init();
    },

    mounted() {
        this.$Utils.globalDragCenterFn('finance-bound-modal');
    },

    components: {
        SearchModal,
        ChooseOrgModal,
        ProductVenderInput
    }
};
</script>

<style lang="less">
.page-finance-inbound {
    .ant-col {
        height: 36px !important;
    }
    .page_title {
        text-align: center;
        color: #000;
        font-size: 24px;
        font-weight: 600;
        padding-top: 23px;
        padding-bottom: 23px;
        margin-bottom: 0;
        background: #fff;
    }
    .vxe-table--body {
        .col--ellipsis {
            height: 30px !important;
            padding: 0 !important;
        }
    }

    .search-form {
        .operation-col {
            text-align: right !important;
            padding-top: 3px;
        }
        .ant-form-item {
            .ant-form-item-label {
                line-height: 40px !important;
                width: 25% !important;
            }
            .ant-form-item-control-wrapper {
                width: 75% !important;
                display: inline-block !important;
            }
        }
    }
    .handler_name_col {
        text-align: left;
    }
    .ant-select-selection {
        box-sizing: border-box;
        height: 32px !important;
        padding: 4px 11px;
    }
    .vxe-pager--left-wrapper {
        .pricesum_wrapper {
            position: absolute;
            left: 30px;
            font-size: 16px;
            .pricesum_label {
                color: #585a5f;
                font-weight: 500;
            }
            .pricesum_value {
                color: #ff0000;
            }
        }
    }

    .btn-search,
    .btn-reset {
        width: 76px;
        margin-right: 5px;
    }
    .btn-reset {
        margin-right: 14px;
    }
    .btn-export {
        width: 97px;
        text-align: center;
        color: #1e8702 !important;
        border-color: #1e8702 !important;
        overflow: hidden;
        transition: 0.3s all linear;
        margin-left: 40px;
        .btn-text-export {
            display: inline-block;
            vertical-align: middle;
            font-weight: 500;
        }
        .yrt-excel {
            padding-right: 0;
            font-size: 22px;
            vertical-align: middle;
            color: #1e8702;
        }
        &:hover {
            background: rgba(30, 135, 2) !important;
            color: #fff !important;
            border-color: rgba(30, 135, 2) !important;
            .yrt-excel {
                color: #fff;
            }
        }
    }
}
</style>
