<!-- 
* @Author: yangtao  
* @Description: 可用产品弹框列表
-->
<template>
    <div>
        <editor-form-header>
            <editor-form-header-item>
                <span class="label">生产厂商：</span>
                <base-input-with-clear-button
                    placeholder="生产厂商"
                    size="small"
                    :value="searchValues.productVendorName"
                    @clear="onOrganizationClear"
                    @click="organizationSelecterVisible = true"
                ></base-input-with-clear-button>
            </editor-form-header-item>
            <editor-form-header-item>
                <span class="label">产品名称：</span>
                <a-input placeholder="产品名称" size="small" v-model="searchValues.productName" />
            </editor-form-header-item>
            <editor-form-header-item>
                <span class="label">规格型号：</span>
                <a-input placeholder="规格型号" size="small" v-model="searchValues.specification" />
            </editor-form-header-item>
        </editor-form-header>
        <editor-form-header style="margin-top: 24px">
            <editor-form-header-item>
                <span class="label">仓<em v-html="'\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0'"></em>库：</span>
                <a-input v-if="!!warehouse" placeholder="仓库" size="small" disabled :value="warehouse.name" />
                <warehouse-selecter-of-user-org
                    allowClear
                    :value="
                        searchValues.warehouseInnerSn && {
                            id: searchValues.warehouseInnerSn,
                            name: searchValues.warehouseName
                        }
                    "
                    @change="onWarehouseChange"
                ></warehouse-selecter-of-user-org>
            </editor-form-header-item>
            <editor-form-header-item></editor-form-header-item>
            <editor-form-header-item>
                <a-button @click="handleSearch" size="small" type="primary">搜索</a-button>
            </editor-form-header-item>
        </editor-form-header>
        <vxe-table
            style="margin-top: 24px"
            border
            show-overflow
            show-header-overflow
            resizable
            auto-resize
            ref="xTable"
            height="320"
            size="small"
            align="center"
            :data="products"
            :checkbox-config="{ range: true }"
            @cell-dblclick="onCellDoubleClick"
        >
            <vxe-table-column v-bind="tableColumnConfig.select" type="checkbox"></vxe-table-column>
            <vxe-table-column v-for="column in productSelecterAdvanceSaleColumns" v-bind="column" :key="column.field" />
        </vxe-table>
        <vxe-pager
            style="margin: 0 22px"
            :loading="loading"
            :current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="handleChangePagination"
        >
        </vxe-pager>
        <organization-selecter
            :visible="organizationSelecterVisible"
            @close="organizationSelecterVisible = false"
            @submit="onOrganizationSelected"
        ></organization-selecter>
    </div>
</template>

<script>
import EditorFormHeader from '@/components/EditorFormHeader';
import EditorFormHeaderItem from '@/components/EditorFormHeaderItem';
import BaseInputWithClearButton from '@/components/BaseInputWithClearButton';
import WarehouseSelecterOfUserOrg from '@/components/WarehouseSelecterOfUserOrg';
import tableColumnConfig from '@/config/tableColumnConfig';
import { odoVendorListAPI } from '@/service/pageAjax';
import OrganizationSelecter from '@/components/OrganizationSelecter';
import { productSelecterAdvanceSaleColumns } from './columns';

export default {
    name: 'ProductSelecterAdvanceSale',
    components: {
        BaseInputWithClearButton,
        EditorFormHeader,
        EditorFormHeaderItem,
        OrganizationSelecter,
        WarehouseSelecterOfUserOrg
    },
    props: {
        // 业务类型
        billsType: {
            type: String,
            required: false
        },
        // 相关机构id
        organizationInnerSn: {
            type: Number,
            required: false
        },
        // 仓库
        warehouse: {
            type: Object,
            default: undefined
        }
    },
    data() {
        return {
            tableColumnConfig,
            productSelecterAdvanceSaleColumns,
            loading: false,
            searchValues: {
                productName: undefined,
                productVendorName: undefined,
                productVendorInnerSn: undefined,
                warehouseName: undefined,
                warehouseInnerSn: undefined,
                specification: undefined
            },
            total: 0,
            products: [],
            currentPage: 1,
            pageSize: 10,
            organizationSelecterVisible: false
        };
    },
    watch: {
        // 业务类型（此值与相关机构相关联）发生改变时重置数据
        billsType(newValue, oldValue) {
            if (newValue !== oldValue) {
                this.handleInit();
            }
        },
        // 相关机构发生改变时重置数据
        organizationInnerSn(newValue, oldValue) {
            if (newValue !== oldValue) {
                this.handleInit();
            }
        },
        // 仓库发生改变时重置数据
        warehouseInnerSn(newValue, oldValue) {
            if (newValue !== oldValue) {
                this.handleInit();
            }
        }
    },
    mounted() {
        this.handleSearch();
    },
    methods: {
        onOrganizationSelected(organizations) {
            this.searchValues.productVendorName = organizations[0].organizationName;
            this.searchValues.productVendorInnerSn = organizations[0].organizationInnerSn;
            this.organizationSelecterVisible = false;
        },

        onOrganizationClear() {
            this.searchValues.productVendorName = undefined;
            this.searchValues.productVendorInnerSn = undefined;
        },

        onWarehouseChange(value = {}) {
            this.searchValues.warehouseName = value.name;
            this.searchValues.warehouseInnerSn = value.id;
        },

        onCellDoubleClick({ row }) {
            this.handleSubmit([row]);
        },

        handleInit() {
            this.loading = false;
            this.searchValues.productName = undefined;
            this.searchValues.productVendorName = undefined;
            this.searchValues.productVendorInnerSn = undefined;
            this.searchValues.specification = undefined;
            this.total = 0;
            this.products = [];
            this.currentPage = 1;
            this.pageSize = 10;
            this.$refs.xTable.reloadData([]);
        },

        handleSearch() {
            this.listLoader(this.currentPage, this.pageSize, { ...this.searchValues });
        },

        handleSubmit(products) {
            const _products = products || this.$refs.xTable.getCheckboxRecords(true);
            if (_products && _products.length > 0) {
                this.$emit('submit', { advanceSaleOrderProducts: _products });
                this.$refs.xTable.removeCheckboxRow();
            } else {
                this.$message.warning('未选择产品');
            }
        },

        /**
         * 翻页
         */
        handleChangePagination(pagination) {
            this.currentPage = pagination.currentPage;
            this.pageSize = pagination.pageSize;
            this.handleSearch();
        },

        /**
         * 产品列表数据加载器
         */
        async listLoader(currentPage, pageSize, searchValues) {
            try {
                this.loading = true;
                const { code, msg, rows, total } = await odoVendorListAPI({
                    params: {
                        pageSize,
                        pageNum: currentPage,
                        level: 1,
                        org_id: this.$cookie.get('userbelong'),
                        billsType: this.billsType,
                        organization_inner_sn: this.organizationInnerSn,
                        warehouse_inner_sn: this.warehouse
                            ? this.warehouse.innerSn
                            : this.searchValues.warehouseInnerSn,
                        product_name: searchValues.productName,
                        vendor_inner_sn: searchValues.productVendorInnerSn,
                        specification: searchValues.specification
                    }
                });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.products = rows;
                this.total = total;
            } catch (err) {
                this.$message.error('产品查询失败');
                throw err;
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>
