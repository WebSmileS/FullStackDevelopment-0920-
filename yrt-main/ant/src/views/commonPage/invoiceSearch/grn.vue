<template>
    <list-page-layout>
        <template slot="header">
            <list-page-header
                needMixedSearch
                :operationGroups="operationGroups"
                :buttonsStatu="headerButtonsStatu"
                @btnClick="onHeaderButtonClick"
                @search="onHeaderMultipleSearch"
            >
                <template slot="singleSearcher">
                    <list-page-header-single-searcher
                        slot="singleSearcher"
                        placeholder="单据编号"
                        v-model="searchValues.sn"
                        @search="onHeaderSingleSearch"
                    ></list-page-header-single-searcher>
                </template>
                <template slot="searchFormItems">
                    <list-page-header-input label="单据编号" v-model="searchValues.sn"></list-page-header-input>
                    <list-page-header-modal-input
                        label="单位名称"
                        :value="searchValues.organizationName"
                        @input="handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                        @clear="onSearchFormOrganizationClear"
                    ></list-page-header-modal-input>
                    <list-page-header-input
                        label="厂商名称"
                        v-model="searchValues.productVendorName"
                    ></list-page-header-input>
                    <list-page-header-input
                        label="产品名称"
                        v-model="searchValues.productName"
                    ></list-page-header-input>
                    <list-page-header-input
                        label="规格型号"
                        v-model="searchValues.specification"
                    ></list-page-header-input>
                    <list-page-header-date-range
                        label="收货日期"
                        v-model="searchValues.dateRange"
                    ></list-page-header-date-range>
                </template>
            </list-page-header>
        </template>
        <template slot="list">
            <invoice-search-list
                ref="list"
                :listType="invoiceSearchListMap.GRN.value"
                :searchValues="searchValues"
                @selectedChange="onSelectedChange"
            ></invoice-search-list>
        </template>
        <template slot="modals">
            <organization-selecter
                title="选择供货单位"
                destroyOnClose
                :visible="modalsVisible[modalsKeyMap.ORGANIZATION_SELECTER]"
                @close="handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                @submit="onSearchFormOrganizationSelected"
            ></organization-selecter>
        </template>
    </list-page-layout>
</template>

<script>
import ListPageLayout from '@/components/ListPageLayout';
import ListPageHeader from '@/components/ListPageHeader';
import ListPageHeaderSingleSearcher from '@/components/ListPageHeaderSingleSearcher';
import ListPageHeaderInput from '@/components/ListPageHeaderInput';
import ListPageHeaderDateRange from '@/components/ListPageHeaderDateRange';
import ListPageHeaderModalInput from '@/components/ListPageHeaderModalInput';
import modalsMixin from '@/mixins/modals';
import { store as invoiceSearchStore, STORE_NAME as invoiceSearchStoreName, SET_GRN_INVOICE_INFO } from './store';
import InvoiceSearchList from './InvoiceSearchList';
import OrganizationSelecter from './OrganizationSelecter';
import { invoiceSearchListMap } from './type';

const modalsKeyMap = Object.freeze({
    ORGANIZATION_SELECTER: 'g'
});

const buttonsMap = {
    add: { key: 'add', text: '开票', type: 'primary' }
};

export default {
    name: 'InvoiceSearch',
    components: {
        ListPageLayout,
        ListPageHeader,
        ListPageHeaderSingleSearcher,
        ListPageHeaderInput,
        ListPageHeaderDateRange,
        ListPageHeaderModalInput,
        OrganizationSelecter,
        InvoiceSearchList
    },
    mixins: [modalsMixin],
    data() {
        return {
            modalsKeyMap,
            loading: false,
            searchValues: {
                sn: undefined,
                billsType: undefined,
                organizationName: undefined,
                organizationInnerSn: undefined,
                productName: undefined,
                productVendorName: undefined,
                specification: undefined,
                dateRange: []
            },
            selected: [],
            targetOrganization: []
        };
    },
    computed: {
        invoiceSearchListMap() {
            return invoiceSearchListMap;
        },
        operationGroups() {
            return [[buttonsMap.add]];
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false, reason: '' }
            };
            if (!this.selected || this.selected.length < 1) {
                result.add.disabled = true;
                result.add.reason = '至少选中1条记录才能进行此操作';
            }
            return result;
        }
    },
    created() {
        if (!this.$store.hasModule(invoiceSearchStoreName)) {
            this.$store.registerModule(invoiceSearchStoreName, invoiceSearchStore);
        }
    },
    methods: {
        /**
         * 简单查询
         */
        onHeaderSingleSearch() {
            this.handleSearch({ sn: this.searchValues.sn });
        },

        /**
         * 多条件查询
         */
        onHeaderMultipleSearch() {
            this.handleSearch();
        },

        onSearchFormOrganizationSelected(organizations) {
            if (organizations && organizations.length > 0) {
                this.searchValues.organizationName = organizations[0].organizationName;
                this.searchValues.organizationInnerSn = organizations[0].organizationInnerSn;
                this.searchValues.billsType = organizations[0].billsType;
            }
            this.handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER);
        },

        onSearchFormOrganizationClear() {
            this.searchValues.organizationName = undefined;
            this.searchValues.organizationInnerSn = undefined;
            this.searchValues.billsType = undefined;
        },

        onHeaderButtonClick(key) {
            switch (key) {
                case 'add':
                    this.handleCreateInvoice();
                    break;
            }
        },

        onSelectedChange(selected) {
            this.selected = selected;
            if (!selected[0]) {
                this.targetOrganization = undefined;
                return;
            }
            this.targetOrganization = {
                billsType: selected[0].billsType,
                organizationName: selected[0].organization_name,
                organizationInnerSn: selected[0].organization_inner_sn
            };
        },

        /**
         * 查询列表
         * @param {object | undefined} values 查询条件，未定义时以当前状态进行查询
         */
        handleSearch(values) {
            this.$refs.list.handleSearch(values);
        },

        /**
         * 根据当前已选的记录筛选出开票产品进行开票
         */
        handleCreateInvoice() {
            const productsFiltered = [];
            const products = this.selected.filter((product) => {
                if (
                    product.billsType === this.targetOrganization.billsType &&
                    product.organization_inner_sn === this.targetOrganization.organizationInnerSn
                ) {
                    return true;
                }
                productsFiltered.push(product);
            });
            if (productsFiltered.length) {
                this.$confirm({
                    title: '不能同时开具多个“单位名称”的入库发票',
                    content: `已选择的产品（共${this.selected.length}个）中包含不属于机构“${this.targetOrganization.organizationName}”的入库产品（共${productsFiltered.length}个），将会被自动过滤，确定继续吗？`,
                    onOk: () => {
                        this.handleCreateGrnInvoice(this.targetOrganization, products);
                    }
                });
            } else {
                this.handleCreateGrnInvoice(this.targetOrganization, products);
            }
        },

        /**
         * 开入库发票
         * 将相关机构数据和产品数据放入store，并跳转到开票页面
         * 开票页面应该接收store中的相关机构数据和产品数据，自动打开开票窗口
         * @param {object} organization 开票的机构
         * @param {array} products 开票的产品
         */
        handleCreateGrnInvoice(organization, products) {
            this.$store.commit(`${invoiceSearchStoreName}/${SET_GRN_INVOICE_INFO}`, {
                products,
                billsType: organization.billsType,
                organizationInnerSn: organization.organizationInnerSn,
                organizationName: organization.organizationName
            });
            this.$router.push({ name: 'inboundSystemInvoice' });
        }
    }
};
</script>
