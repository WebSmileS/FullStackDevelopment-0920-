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
                        placeholder="产品名称"
                        v-model="searchValues.productName"
                        @search="onHeaderSingleSearch"
                    ></list-page-header-single-searcher>
                </template>
                <template slot="searchFormItems">
                    <list-page-header-input
                        label="产品名称"
                        v-model="searchValues.productName"
                    ></list-page-header-input>
                    <list-page-header-input
                        label="厂商名称"
                        v-model="searchValues.productVendorName"
                    ></list-page-header-input>
                    <list-page-header-input
                        label="规格型号"
                        v-model="searchValues.specification"
                    ></list-page-header-input>
                    <list-page-header-input label="标签" v-model="searchValues.barcode"></list-page-header-input>
                </template>
            </list-page-header>
        </template>
        <template slot="list">
            <barcode-manage-list
                ref="list"
                selectType="radio"
                :reloadWhenSearch="false"
                :searchValues="searchValues"
                @selectedChange="onSelectedChange"
            ></barcode-manage-list>
        </template>
        <template slot="modals">
            <barcode-editor
                :visible="barcodeEditorVisible"
                @close="barcodeEditorVisible = false"
                @addFinish="onBarcodesAdd"
            ></barcode-editor>
        </template>
    </list-page-layout>
</template>

<script>
import ListPageLayout from '@/components/ListPageLayout';
import ListPageHeader from '@/components/ListPageHeader';
import ListPageHeaderSingleSearcher from '@/components/ListPageHeaderSingleSearcher';
import ListPageHeaderInput from '@/components/ListPageHeaderInput';
import { barcodeDeleteAPI } from '@/service/pageAjax';
import BarcodeManageList from './BarcodeManageList';
import BarcodeEditor from './BarcodeEditor';

const buttonsMap = {
    add: { key: 'add', text: '新增', type: 'primary' },
    delete: { key: 'delete', text: '删除', type: 'danger' }
};

export default {
    name: 'BarcodeManage',
    components: {
        ListPageLayout,
        ListPageHeader,
        ListPageHeaderSingleSearcher,
        ListPageHeaderInput,
        BarcodeManageList,
        BarcodeEditor
    },
    data() {
        return {
            loading: false,
            deleteButtonLoading: false,
            searchValues: {
                productName: undefined,
                productVendorName: undefined,
                specification: undefined,
                barcode: undefined
            },
            selectedRecords: [],
            barcodeEditorVisible: false
        };
    },
    computed: {
        operationGroups() {
            return [[buttonsMap.add], [buttonsMap.delete]];
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false },
                delete: { disabled: false, loading: this.deleteButtonLoading }
            };
            if (!this.selectedRecords[0]) {
                result.delete.disabled = true;
                result.delete.reason = '至少选中一条记录才能进行此操作';
            }
            return result;
        }
    },
    methods: {
        /**
         * 简单查询
         */
        onHeaderSingleSearch() {
            this.handleSearch({
                productName: this.searchValues.productName
            });
        },

        /**
         * 多条件查询
         */
        onHeaderMultipleSearch() {
            this.handleSearch();
        },

        onHeaderButtonClick(key) {
            switch (key) {
                case 'add':
                    this.barcodeEditorVisible = true;
                    break;
                case 'delete':
                    if (this.selectedRecords[0]) {
                        this.$confirm({
                            title: '确定要删除吗？',
                            onOk: async () => {
                                try {
                                    await this.deleteBarcode(this.selectedRecords[0].udi_barcode);
                                    this.$refs.list.handleDeleteRecord(this.selectedRecords[0]);
                                } catch (err) {
                                    return true;
                                }
                            }
                        });
                    }
                    break;
            }
        },

        onSelectedChange(selected) {
            this.selectedRecords = selected;
        },

        onBarcodesAdd(barcodes) {
            this.$refs.list.handleAddRecords(barcodes);
            this.barcodeEditorVisible = false;
        },

        /**
         * 查询列表
         * @param {object | undefined} values 查询条件，未定义时以当前状态进行查询
         */
        handleSearch(values) {
            if (this.$refs.list) {
                this.$refs.list.handleSearch(values);
            }
        },

        /**
         * 删除条码
         */
        async deleteBarcode(barcode) {
            try {
                this.deleteButtonLoading = true;
                const { code, msg } = await barcodeDeleteAPI(barcode);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('删除成功');
            } catch (err) {
                this.$message.error('删除失败');
                console.error(err);
            } finally {
                this.deleteButtonLoading = false;
            }
        }
    }
};
</script>
