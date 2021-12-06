<template>
    <div class="scoped scoped-white page-stock-area">
        <div class="search-form-wrap">
            <a-form
                class="search-form"
                layout="horizontal"
                :colon="false"
                :form="form"
                :label-col="{ span: 6 }"
                :wrapper-col="{ span: 18 }"
            >
                <a-row class="btn-top-wrap" :gutter="16">
                    <a-col :span="5" class="ant-col">
                        <a-form-item label="租户">
                            <a-tree-select
                                v-decorator="[
                                    'org_id',
                                    {
                                        rules: [
                                            {
                                                required: true,
                                                message: '租户不能为空'
                                            }
                                        ]
                                    }
                                ]"
                                treeNodeFilterProp="title"
                                showSearch
                                :dropdownStyle="{ maxHeight: '400px', maxWidth: '195px', overflow: 'auto' }"
                                :treeData="tenantTreeData"
                                placeholder="请选择"
                                searchPlaceholder="请搜索"
                                treeDefaultExpandAll
                                @change="handleTenantChange"
                            >
                            </a-tree-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="5" class="ant-col">
                        <a-form-item label="仓库">
                            <a-tree-select
                                v-decorator="['warehouse_inner_sn']"
                                treeNodeFilterProp="title"
                                showSearch
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
                        <a-form-item label="产品">
                            <a-input v-decorator="['product_name']" placeholder="请输入" autocomplete="off" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="5" class="ant-col">
                        <a-form-item label="规格型号">
                            <a-input v-decorator="['specification']" placeholder="请输入" autocomplete="off" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4" class="operation-col ant-col">
                        <a-button type="primary" :loading="loading" @click="handlePreSearch" class="btn-search"
                            >搜索</a-button
                        >
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <div style="margin-top: 5px; background: #fff">
            <vxe-table
                size="small"
                :height="scrollYNumber - 1"
                border
                resizable
                auto-resize
                highlight-current-row
                align="center"
                :data="dataSource"
                :loading="pagination.loading"
                show-overflow
                show-header-overflow
                showHeader
                :start-index="(pagination.current - 1) * pagination.pageSize"
            >
                <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                    <template v-slot="{ seq }">
                        <div>
                            {{ seq }}
                        </div>
                    </template>
                </vxe-table-column>
                <vxe-table-column
                    v-for="column in columns"
                    :key="column.key"
                    :field="column.dataIndex"
                    :title="column.title"
                    :width="column.width"
                    show-overflow
                ></vxe-table-column>
            </vxe-table>
            <vxe-pager
                :loading="pagination.loading"
                :current-page="pagination.current"
                :page-size="pagination.pageSize"
                :page-sizes="pagination.pageSizes"
                :total="pagination.total"
                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                @page-change="handlePageChange"
            >
            </vxe-pager>
        </div>
    </div>
</template>

<script>
import { getTenantListAPI, getWarehouseListAPI, getInventoryOfTenantAPI } from '@/service/pageAjax';

export default {
    name: 'stock-area',
    data() {
        return {
            form: this.$form.createForm(this, { name: 'search_form' }), // 查询表单
            dataSource: [],
            columns: [
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    width: '30%'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    width: '20%'
                },
                {
                    title: '厂商名称',
                    dataIndex: 'product_vendor_name',
                    key: 'product_vendor_name',
                    width: '15%'
                },
                {
                    title: '仓库',
                    dataIndex: 'warehouse_name',
                    key: 'warehouse_name',
                    width: '15%'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: 'quantity'
                },
                {
                    title: '单位',
                    dataIndex: 'unit',
                    key: 'unit'
                }
            ],
            tenantTreeData: [],
            warehouseTreeData: [],
            pagination: {
                loading: false, // 记录当前是否在加载中
                total: 0,
                current: 1, // 对应后端的 pageSize
                pageSize: 20, // 对应后端的 pageNum
                pageSizes: [10, 20, 30, 40]
            }
        };
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            return window.innerHeight - 170;
        }
    },
    methods: {
        async init() {
            try {
                const { rows = [] } = await getTenantListAPI();
                const hasDealer = !!(rows && rows.length);
                this.tenantTreeData = rows.map((row) => ({
                    value: row.dealer_inner_sn,
                    title: row.dealer_name,
                    key: row.dealer_inner_sn
                }));
                if (hasDealer) {
                    const defaultDealerInnerSn = rows[0].dealer_inner_sn;
                    this.form.setFieldsValue({
                        org_id: defaultDealerInnerSn
                    });
                    this.handleTenantChange(defaultDealerInnerSn);
                }
                this.handleSearch();
            } catch (error) {
                console.error(error);
            }
        },
        handlePreSearch() {
            this.pagination.current = 1;
            this.handleSearch();
        },
        async handleSearch() {
            if (this.pagination.loading) {
                return;
            }
            try {
                this.pagination.loading = true;
                const formValues = await this.form.validateFields();
                const { pageSize, current } = this.pagination;
                const { rows = [], total } = await getInventoryOfTenantAPI(formValues, current, pageSize);
                this.dataSource = rows;
                this.pagination.total = total;
            } catch (error) {
                console.error(error);
            } finally {
                this.pagination.loading = false;
            }
        },

        // 租户change
        async handleTenantChange(dealerInnerSn) {
            try {
                const { list = [] } = await getWarehouseListAPI(dealerInnerSn);
                this.form.resetFields(['warehouse_inner_sn']);
                this.warehouseTreeData = list.map((item) => ({
                    title: item.name,
                    value: item.warehouse_model_inner_sn,
                    key: item.warehouse_model_inner_sn
                }));
            } catch (error) {
                console.error(error);
            }
        },

        handlePageChange({ currentPage, pageSize }) {
            this.pagination.current = currentPage;
            this.pagination.pageSize = pageSize;
            this.handleSearch();
        }
    },
    created() {
        this.init();
    }
};
</script>

<style lang="less">
.page-stock-area {
    .ant-col {
        height: 36px !important;
    }
    .vxe-table--body {
        .col--ellipsis {
            height: 30px !important;
            padding: 0 !important;
        }
    }

    .search-form {
        .btn-top-wrap {
            .ant-col:nth-child(1),
            .ant-col:nth-child(2) {
                .ant-form-item {
                    margin-top: 2px;
                }
            }
        }
        .operation-col {
            text-align: right !important;
            padding-top: 3px;
        }
        .ant-form-item {
            height: 40px !important;
            .ant-form-item-label {
                line-height: 40px !important;
                width: 25% !important;
            }
            .ant-form-item-control-wrapper {
                width: 75% !important;
                display: inline-block !important;
                height: 39px !important;
                .ant-form-item-control {
                    height: 39px !important;
                }
            }
            .ant-input {
                height: 24px !important;
            }
        }
        .ant-form-item-with-help {
            .ant-form-item-label {
                vertical-align: top;
            }
        }
    }
    .ant-select {
        margin-top: 3px;
    }
    .ant-select-selection {
        box-sizing: border-box;
        height: 24px !important;
    }

    .btn-search {
        margin-right: 5px;
        height: 24px;
        min-width: 76px;
        margin-top: 5px;
    }
}
</style>
