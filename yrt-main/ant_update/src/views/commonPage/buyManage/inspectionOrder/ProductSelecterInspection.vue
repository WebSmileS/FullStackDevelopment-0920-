<template>
    <div>
        <div class="modal-form-input" style="padding-top: 0">
            <ul class="top-list" style="width: 100%">
                <li class="item">
                    <span class="label">单据编号</span>
                    <div class="input">
                        <a-input placeholder="单据编号" size="small" v-model="searchValues.sn" />
                    </div>
                </li>
                <li class="item">
                    <span class="label">产品名称</span>
                    <div class="input">
                        <a-input placeholder="产品" size="small" v-model="searchValues.productName" />
                    </div>
                </li>
                <li class="item">
                    <span class="label">单据日期</span>
                    <div class="input">
                        <a-range-picker :placeholder="['开始', '结束']" @change="onSearchDateChange" size="small">
                        </a-range-picker>
                    </div>
                </li>
            </ul>
            <ul class="top-list" style="width: 100%">
                <li class="item">
                    <span class="label">厂商名称</span>
                    <div class="input">
                        <a-input placeholder="产品" size="small" v-model="searchValues.productVendorName" />
                    </div>
                </li>
                <li class="item">
                    <span class="label">规格型号</span>
                    <div class="input">
                        <a-input placeholder="规格型号" size="small" v-model="searchValues.specification" />
                    </div>
                </li>
                <li class="item"></li>
                <li style="width: auto">
                    <a-button @click="handleSearch" size="small" type="primary"> 搜索 </a-button>
                </li>
            </ul>
        </div>
        <vxe-table
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
            <vxe-table-column v-for="column in productColumns" v-bind="column" :key="column.field" />
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
    </div>
</template>

<script>
import tableColumnConfig from '@/config/tableColumnConfig';
import { inspectionOrderProductListAPI } from '@/service/pageAjax';
import productColumns from './productSelecterInspectionColumns';

export default {
    name: 'ProductSelecterInspection',
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
        // 仓库id
        warehouseInnerSn: {
            type: Number,
            required: false
        }
    },
    data() {
        return {
            tableColumnConfig,
            productColumns,
            loading: false,
            searchValues: {
                sn: undefined,
                beginDate: null,
                endDate: null,
                productName: undefined,
                productVendorName: undefined,
                specification: undefined
            },
            total: 0,
            products: [],
            currentPage: 1,
            pageSize: 10
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
    methods: {
        onSearchDateChange(date) {
            this.searchValues.beginDate = date[0];
            this.searchValues.endDate = date[1];
        },

        onCellDoubleClick({ row }) {
            this.handleSubmit([row]);
        },

        handleInit() {
            this.loading = false;
            this.searchValues.sn = undefined;
            this.searchValues.beginDate = null;
            this.searchValues.endDate = null;
            this.searchValues.productName = undefined;
            this.searchValues.productVendorName = undefined;
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
                this.$emit('submit', { inspectionOrderProducts: _products });
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
                const { code, msg, rows, total } = await inspectionOrderProductListAPI(currentPage, pageSize, {
                    bills_type: this.billsType,
                    organization_inner_sn: this.organizationInnerSn,
                    warehouse_inner_sn: this.warehouseInnerSn,
                    begin_date: searchValues.beginDate
                        ? `${searchValues.beginDate.format('YYYY-MM-DD')} 00:00:00`
                        : undefined,
                    end_date: searchValues.endDate
                        ? `${searchValues.endDate.format('YYYY-MM-DD')} 00:00:00`
                        : undefined,
                    product_name: searchValues.productName,
                    product_vendor_name: searchValues.productVendorName,
                    sn: searchValues.sn,
                    specification: searchValues.specification
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
