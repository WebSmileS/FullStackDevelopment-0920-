<template>
    <div>
        <div class="search-form-wrapper">
            <div class="form-item">
                <label>产品厂商</label>
                <base-input-with-clear-button
                    class="form-item-content"
                    size="small"
                    :value="searchFormValues.productVendorName"
                    @click="organizationSelecterVisible = true"
                    @clear="
                        (searchFormValues.productVendorName = undefined),
                            (searchFormValues.productVendorInnerSn = undefined)
                    "
                ></base-input-with-clear-button>
            </div>
            <div class="form-item">
                <label>产品名称</label>
                <a-input class="form-item-content" size="small" v-model="searchFormValues.productName"></a-input>
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
            show-footer
            resizable
            auto-resize
            ref="table"
            height="300"
            size="small"
            :checkbox-config="{ range: true }"
            @cell-dblclick="onCellDoubleClick"
        >
            <vxe-table-column type="checkbox" v-bind="tableColumnConfig.select"></vxe-table-column>
            <vxe-table-column v-bind="tableColumnConfig.seq"></vxe-table-column>
            <template v-for="column in columns">
                <vxe-table-column v-bind="column" :key="column.field"></vxe-table-column>
            </template>
            <vxe-table-column v-bind="tableColumnConfig.status">
                <template v-slot="{ row }">
                    <span class="orange" v-if="row.status === 0">已禁用</span>
                    <span class="green" v-if="row.status === 1">可用</span>
                </template>
            </vxe-table-column>
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
import tableColumnConfig from '@/config/tableColumnConfig';
import { barcodeVendorProductList } from '@/service/pageAjax';
import ScrollLoadVxeTable from '@/components/ScrollLoadVxeTable';
import BaseInputWithClearButton from '@/components/BaseInputWithClearButton.vue';
import OrganizationSelecter from './OrganizationSelecter';
import columns from './productSelecterColumns.js';

export default {
    name: 'ProductSelecter',
    components: {
        ScrollLoadVxeTable,
        OrganizationSelecter,
        BaseInputWithClearButton
    },
    data() {
        return {
            tableColumnConfig,
            columns,
            searchFormValues: {
                productName: undefined,
                productVendorName: undefined,
                productVendorInnerSn: undefined,
                specification: undefined
            },
            organizationSelecterVisible: false
        };
    },
    created() {
        this.$nextTick(() => {
            this.onSearch();
        });
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
                this.searchFormValues.productVendorInnerSn = organizations[0].organizationInnerSn;
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
                const { productName, productVendorInnerSn, specification } = this.searchFormValues;
                const { code, msg, rows, total } = await barcodeVendorProductList(page, pageSize, {
                    name: productName ? productName : undefined,
                    vendor_inner_sn: productVendorInnerSn ? productVendorInnerSn : undefined,
                    specification: specification ? specification : undefined
                });
                if (code !== 0) {
                    throw new Error(msg || '产品信息获取失败');
                }
                return {
                    total,
                    data: rows
                };
            } catch (err) {
                this.$message.error(err.message);
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
