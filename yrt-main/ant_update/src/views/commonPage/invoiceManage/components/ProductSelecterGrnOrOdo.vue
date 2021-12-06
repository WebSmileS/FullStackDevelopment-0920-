<template>
    <div>
        <div class="search-form-wrapper">
            <div class="form-item">
                <label>单据日期</label>
                <a-range-picker
                    class="form-item-content"
                    size="small"
                    v-model="searchFormValues.dateRange"
                ></a-range-picker>
            </div>
            <div class="form-item">
                <label>产品名称</label>
                <a-input class="form-item-content" size="small" v-model="searchFormValues.productName"></a-input>
            </div>
            <div class="form-item">
                <label>厂商</label>
                <a-input
                    class="form-item-content"
                    size="small"
                    :value="searchFormValues.productVendorName"
                    @click="organizationSelecterVisible = true"
                ></a-input>
            </div>
            <div class="form-item">
                <label>单据编号</label>
                <a-input class="form-item-content" size="small" v-model="searchFormValues.sn"></a-input>
            </div>
            <div class="form-item">
                <label>规格型号</label>
                <a-input class="form-item-content" size="small" v-model="searchFormValues.specification"></a-input>
            </div>
            <div class="form-item" style="justify-content: flex-end">
                <a-button size="small" type="primary" @click="onSearch">搜索</a-button>
            </div>
        </div>
        <scroll-load-vxe-table
            border
            show-overflow
            show-header-overflow
            resizable
            auto-resize
            ref="table"
            height="400"
            size="small"
            align="center"
            :checkbox-config="{ range: true }"
            @cell-dblclick="onCellDoubleClick"
        >
            <vxe-table-column type="checkbox" :resizable="false" width="32" />
            <template v-for="column in columns">
                <vxe-table-column
                    :key="column.dataIndex"
                    :field="column.dataIndex"
                    :min-width="column.minWidth"
                    :title="column.title"
                ></vxe-table-column>
            </template>
        </scroll-load-vxe-table>
        <organization-selecter
            destroyOnClose
            :visible="organizationSelecterVisible"
            @close="organizationSelecterVisible = false"
            @submit="onOrganizationSubmit"
        ></organization-selecter>
    </div>
</template>

<script>
import Big from 'big.js';
import { invoiceGrnProductListAPI, invoiceOdoProductListAPI } from '@/service/pageAjax';
import ScrollLoadVxeTable from '@/components/ScrollLoadVxeTable';
import OrganizationSelecter from './OrganizationSelecter';
import columns from './ProductSelecterGrnOrOdoColumns';

export default {
    name: 'ProductSelecterGrnOrOdo',
    components: {
        ScrollLoadVxeTable,
        OrganizationSelecter
    },
    props: {
        isInvoiceGrn: {
            type: Boolean
        },
        isInvoiceOdo: {
            type: Boolean
        },
        billsType: {
            type: String
        },
        organizationInnerSn: {
            type: [Number, String]
        },
        organizationName: {
            type: String
        }
    },
    data() {
        return {
            columns,
            searchFormValues: {
                productName: undefined,
                productVendorName: undefined,
                sn: undefined,
                specification: undefined,
                dateRange: []
            },
            organizationSelecterVisible: false
        };
    },
    watch: {
        billsType() {
            this.$refs.table.handleReset();
        },
        organizationInnerSn() {
            this.$refs.table.handleReset();
        }
    },
    methods: {
        onCellDoubleClick({ row }) {
            this.$emit('submit', [row]);
            this.$emit('close');
        },
        onSearch() {
            this.$refs.table.handleReload();
        },
        onOrganizationSubmit(organizations) {
            if (organizations.length) {
                this.searchFormValues.productVendorName = organizations[0].organizationName;
                this.organizationSelecterVisible = false;
            }
        },
        handleSubmit() {
            const selected = this.$refs.table.$refs.table.getCheckboxRecords();
            if (!selected.length) {
                return;
            }
            this.$emit('submit', selected);
            this.$emit('close');
        },
        async dataLoaderForScrollLoadVXETable(page, pageSize) {
            try {
                const orgId = this.$cookie.get('userbelong');
                let dataAPI;
                if (this.isInvoiceGrn) {
                    dataAPI = invoiceGrnProductListAPI;
                }
                if (this.isInvoiceOdo) {
                    dataAPI = invoiceOdoProductListAPI;
                }
                const { code, msg, rows, total } = await dataAPI({
                    params: {
                        org_id: orgId,
                        billsType: this.billsType,
                        organization_inner_sn: this.organizationInnerSn,
                        name: this.searchFormValues.productName,
                        product_vendor_name: this.searchFormValues.productVendorName,
                        sn: this.searchFormValues.sn,
                        specification: this.searchFormValues.specification,
                        begin_date: this.searchFormValues.dateRange[0]
                            ? this.searchFormValues.dateRange[0].format('YYYY-MM-DD')
                            : undefined,
                        end_date: this.searchFormValues.dateRange[1]
                            ? this.searchFormValues.dateRange[1].format('YYYY-MM-DD')
                            : undefined,
                        pageNum: page,
                        pageSize
                    }
                });
                if (code !== 0) {
                    throw new Error(msg || '产品信息获取失败');
                }
                return {
                    total,
                    data: rows.map((product) => ({
                        ...product,
                        percentRate: `${new Big(product.rate).times(100).toString()}%`,
                        org_name: this.organizationName
                    }))
                };
            } catch (err) {
                this.$message.error(err.message);
                throw err;
            }
        }
    }
};
</script>

<style lang="less" scoped>
.search-form-wrapper {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    margin-right: -22px;
    .form-item {
        display: flex;
        flex: 1;
        flex-wrap: nowrap;
        margin-right: 22px;
        margin-bottom: 24px;
        label {
            flex-grow: 0;
            padding-right: 8px;
            white-space: nowrap;
        }
        .form-item-content {
            flex-grow: 1;
            width: 272px;
        }
    }
}
</style>
