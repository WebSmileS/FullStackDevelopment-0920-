<!-- 
* @Author: 
* @Description: 厂商产品选择组件，不包含模态框部分，请搭配ProductSelecterModal组件使用
-->
<template>
    <div>
        <product-selecter-header-form
            ref="headerForm"
            :defaultFormValues="defaultHeaderFormValues"
            @change="onHeaderChange"
            @search="onHeaderSearch"
        ></product-selecter-header-form>
        <vxe-table
            style="margin-top: 18px"
            border
            show-overflow
            show-header-overflow
            resizable
            auto-resize
            ref="xTable"
            height="320"
            size="small"
            align="center"
            :loading="loading"
            :data="list"
            :checkbox-config="{ range: true }"
            @cell-dblclick="onCellDoubleClick"
        >
            <vxe-table-column v-bind="tableColumnConfig.select" type="checkbox"></vxe-table-column>
            <vxe-table-column v-for="column in productColumns" v-bind="column" :key="column.field" />
        </vxe-table>
        <vxe-pager
            style="margin: 0 22px"
            :loading="loading"
            :current-page="pagination.currentPage"
            :page-size="pagination.pageSize"
            :total="pagination.total"
            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="handleChangePagination"
        >
        </vxe-pager>
    </div>
</template>

<script>
import ProductSelecterHeaderForm from '@/components/ProductSelecterHeaderForm';
import { getAllVendorListAPI } from '@/service/pageAjax';
import tableColumnConfig from '@/config/tableColumnConfig';
import paginationMixin from '@/mixins/pagination';

const defaultColumns = [
    {
        ...tableColumnConfig.vendor
    },
    {
        ...tableColumnConfig.product
    },
    {
        ...tableColumnConfig.specification
    },
    {
        ...tableColumnConfig.sn,
        title: '产品编码',
        field: 'specification_vendor_sn'
    },
    {
        ...tableColumnConfig.mdrf,
        field: 'mdrf_sn'
    }
];

export default {
    name: 'ProductSelecterVendor',
    components: {
        ProductSelecterHeaderForm
    },
    props: {
        /**
         * 表格列配置，参见vxe-table-column
         * 默认将使用defaultColumns
         */
        tableColumns: {
            type: Array
        },
        /**
         * 业务类型
         */
        billsType: {
            type: String,
            required: true
        },
        /**
         * 相关机构id
         */
        organizationInnerSn: {
            type: Number,
            required: true
        },
        autoLoadMounted: {
            type: Boolean,
            default: false
        },
        /**
         * 合同产品勾选项是否默认勾选
         */
        defaultIsContractProduct: {
            type: Boolean,
            default: true
        }
    },
    mixins: [paginationMixin],
    data() {
        return {
            tableColumnConfig,
            defaultHeaderFormValues: {
                isContractProduct: this.$props.defaultIsContractProduct
            },
            headerFormValues: {
                isContractProduct: this.$props.defaultIsContractProduct
            },
            organizationSelecterVisible: false
        };
    },
    computed: {
        productColumns() {
            if (this.tableColumns) {
                return this.tableColumns;
            }
            return defaultColumns;
        }
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
        }
    },
    mounted() {
        if (this.autoLoadMounted) {
            this.handleSearch();
        }
    },
    methods: {
        onCellDoubleClick({ row }) {
            this.handleSubmit([row]);
        },

        onHeaderChange(values) {
            this.headerFormValues = values;
        },

        onHeaderSearch() {
            this.handleSearch();
        },

        handleInit() {
            this.loading = false;
            this.list = [];
            this.pagination.total = 0;
            this.pagination.currentPage = 1;
            this.pagination.pageSize = 10;
            this.$refs.xTable.reloadData([]);
            this.$ref.headerForm.handleInit();
            this.headerFormValues = {
                isContractProduct: this.$props.defaultIsContractProduct
            };
        },

        handleSubmit(products) {
            const _products = products || this.$refs.xTable.getCheckboxRecords(true);
            if (_products && _products.length > 0) {
                this.$emit('submit', { purchaseGrnOrderProducts: _products });
                this.$refs.xTable.removeCheckboxRow();
            } else {
                this.$message.warning('未选择产品');
            }
        },

        /**
         * 产品列表数据加载器
         */
        async listLoader(currentPage, pageSize) {
            const { headerFormValues } = this;
            return getAllVendorListAPI({
                params: {
                    pageSize,
                    org_id: this.$cookie.get('userbelong'),
                    pageNum: currentPage,
                    billsType: this.billsType,
                    organization_inner_sn: this.organizationInnerSn,
                    name: headerFormValues.productName,
                    vendor_inner_sn: headerFormValues.productVendorInnerSn,
                    specification: headerFormValues.specification,
                    specification_vendor_sn: headerFormValues.specificationVendorSn,
                    code68_sn:
                        headerFormValues.productType &&
                        headerFormValues.productType[headerFormValues.productType.length - 1],
                    type_inner_sn:
                        headerFormValues.productCustomType &&
                        headerFormValues.productCustomType[headerFormValues.productCustomType.length - 1],
                    onlyContrctProduct: headerFormValues.isContractProduct
                }
            });
        }
    }
};
</script>
