<template>
    <list-page-list-scroll-load
        ref="listTable"
        selectType="checkbox"
        :columns="columns"
        :listLoader="_listLoader"
        @selectedChange="onSelectedRowsChange"
    >
        <template v-slot:footerLeft>
            <div class="invoice-search-list-footer">
                <span class="price-lable">已选金额合计：</span>
                <span class="price-number red">{{ commafiedSelectedPrice }}</span>
                <span class="price-lable">未开票金额合计: </span>
                <a-spin v-if="totalPriceLoading" class="price-number" size="small" :spinning="totalPriceLoading">
                </a-spin>
                <template v-else>
                    <span v-if="totalPriceUseable" class="price-number red">{{ commafiedTotalPrice }}</span>
                    <span v-else class="price-number red">获取失败</span>
                </template>
            </div>
        </template>
    </list-page-list-scroll-load>
</template>

<script>
import Big from 'big.js';
import tableColumnConfig from '@/config/tableColumnConfig';
import {
    invoiceGrnProductListAPI,
    invoiceOdoProductListAPI,
    invoiceGrnTotalPriceAPI,
    invoiceOdoTotalPriceAPI
} from '@/service/pageAjax';
import ListPageListScrollLoad from '@/components/ListPageListScrollLoad.vue';
import invoiceSearchListColumns from './invoiceSearchListColumns';
import { invoiceSearchListMap } from './type';

export default {
    name: 'InvoiceSearchList',
    components: {
        ListPageListScrollLoad
    },
    props: {
        // 通知列表的状态类型
        listType: {
            type: String,
            required: true
        },
        searchValues: {
            type: Object
        }
    },
    data() {
        return {
            nextSearchValues: undefined,
            totalPrice: 0,
            totalPriceLoading: false,
            totalPriceUseable: true,
            selected: [],
            selectedOrganization: undefined
        };
    },
    computed: {
        tableColumnConfig() {
            return tableColumnConfig;
        },
        columns() {
            // 出库开票列表不需要采购员列
            if (this.listType === invoiceSearchListMap.ODO.value) {
                return invoiceSearchListColumns.filter((column) => column.field !== 'buyer_name');
            }
            return invoiceSearchListColumns;
        },
        commafiedTotalPrice() {
            return this.$Utils.commafy(this.totalPrice);
        },
        commafiedSelectedPrice() {
            let total = new Big(0);
            this.selected.forEach((product) => {
                total = total.plus(new Big(product.quantity).times(product.unit_price));
            });
            return this.$Utils.commafy(total.toNumber(4));
        }
    },
    created() {
        this.$nextTick(() => {
            this.handleSearch();
        });
    },
    methods: {
        onSelectedRowsChange(selected) {
            if (selected.length === 0) {
                this.selectedOrganization = undefined;
            }
            if (!this.selectedOrganization && selected.length > 0) {
                this.selectedOrganization = {
                    billsType: selected[0].billsType,
                    organizationInnerSn: selected[0].organization_inner_sn,
                    organizationName: selected[0].organization_name
                };
            }
            if (this.isSameOrganization(selected)) {
                this.selected = selected;
                this.$emit(
                    'selectedChange',
                    selected.map((_) => ({ ..._ }))
                );
                return;
            }
            selected.forEach((product) => {
                if (
                    product.billsType !== this.selectedOrganization.billsType ||
                    product.organization_inner_sn !== this.selectedOrganization.organizationInnerSn
                ) {
                    this.$refs.listTable.$refs.listTable.setCheckboxRow(product, false);
                }
            });
        },

        handleSearch(searchValues) {
            this.nextSearchValues = searchValues;
            this.$refs.listTable.handleReload();
        },

        /**
         * 检测产品是否均为同一个相关机构
         * @param {array} products
         */
        isSameOrganization(products) {
            if (products.length <= 1) {
                return true;
            }
            return !products.some((product) => {
                if (
                    product.billsType !== this.selectedOrganization.billsType ||
                    product.organization_inner_sn !== this.selectedOrganization.organizationInnerSn
                ) {
                    this.$message.warning('不能同时选择多个“相关机构”的记录');
                    return true;
                }
                return false;
            });
        },

        /**
         * 加载列表数据
         */
        async _listLoader(page, pageSize) {
            const {
                sn,
                dateRange = [],
                billsType,
                organizationInnerSn,
                productName,
                productVendorName,
                specification
            } = this.nextSearchValues || this.searchValues;
            const [beginDate, endDate] = dateRange;
            let loaderData = {
                sn,
                billsType,
                specification,
                org_id: this.$cookie.get('userbelong'),
                organization_inner_sn: organizationInnerSn,
                name: productName,
                product_vendor_name: productVendorName,
                begin_date: beginDate ? beginDate.format('YYYY-MM-DD') : undefined,
                end_date: endDate ? endDate.format('YYYY-MM-DD') : undefined,
                pageNum: page,
                pageSize
            };
            let loader;
            this.nextSearchValues = undefined;
            if (
                organizationInnerSn === undefined ||
                billsType === undefined ||
                organizationInnerSn === null ||
                billsType === null
            ) {
                loaderData.billsType = undefined;
                loaderData.organization_inner_sn = undefined;
            }
            if (this.listType === invoiceSearchListMap.GRN.value) {
                loader = invoiceGrnProductListAPI;
            }
            if (this.listType === invoiceSearchListMap.ODO.value) {
                loader = invoiceOdoProductListAPI;
            }
            if (page === 1) {
                this.loadTotalPrice(loaderData);
            }
            try {
                const { code, rows, total, msg } = await loader({ params: loaderData });
                if (code !== 0) {
                    throw new Error(msg || '列表加载失败');
                }
                return { total, data: rows };
            } catch (err) {
                this.$message.error(err.message);
                throw err;
            }
        },

        async loadTotalPrice(loaderData) {
            let loader;
            if (this.listType === invoiceSearchListMap.GRN.value) {
                loader = invoiceGrnTotalPriceAPI;
            }
            if (this.listType === invoiceSearchListMap.ODO.value) {
                loader = invoiceOdoTotalPriceAPI;
            }
            try {
                this.totalPriceLoading = true;
                const { code, info, msg } = await loader({ ...loaderData, pageNum: undefined, pageSize: undefined });
                if (code !== 0) {
                    throw new Error(msg || '合计金额获取失败');
                }
                this.totalPrice = info;
                this.totalPriceUseable = true;
            } catch (err) {
                this.$message.error(err.message);
                this.totalPriceUseable = false;
            } finally {
                this.totalPriceLoading = false;
            }
        }
    }
};
</script>

<style lang="less" scoped>
.invoice-search-list-footer {
    @height: 48px;
    display: flex;
    line-height: @height;
    font-size: 16px;
    text-align: left;
    .price-lable {
        margin-left: 40px;
        &:first-child {
            margin-left: 0px;
        }
    }
    .price-number {
        margin-left: 12px;
        line-height: @height;
        /deep/ .ant-spin-dot-spin {
            position: relative;
            top: 2px;
        }
    }
}
</style>
