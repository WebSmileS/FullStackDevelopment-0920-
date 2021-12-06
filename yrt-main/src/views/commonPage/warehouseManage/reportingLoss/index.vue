<!-- 
* @Author: yangtao  
* @Description: 报损
-->
<template>
    <list-page-layout isTabListPage class="loss-reporting">
        <template slot="header">
            <list-page-header needMixedSearch @search="getSearchData" @expandsChange="handleToggleSearchStatus">
                <template slot="singleSearcher">
                    <list-page-header-single-searcher
                        slot="singleSearcher"
                        placeholder="请输入产品进行查询"
                        v-model="searchValues.product_name"
                        @search="getSearchData"
                    ></list-page-header-single-searcher>
                </template>
                <template slot="searchFormItems">
                    <list-page-header-select
                        label="仓库"
                        allowClear
                        v-model="searchValues.warehouse_inner_sn"
                        :options="warehouseOptions"
                    ></list-page-header-select>
                    <list-page-header-input
                        label="产品"
                        v-model="searchValues.product_name"
                        @keyDownEnter="getSearchData"
                    ></list-page-header-input>
                    <list-page-header-input
                        label="规格型号"
                        v-model="searchValues.specification"
                        @keyDownEnter="getSearchData"
                    ></list-page-header-input>
                    <list-page-header-input
                        label="批号"
                        v-model="searchValues.batch_number"
                        @keyDownEnter="getSearchData"
                    ></list-page-header-input>
                    <list-page-header-modal-input
                        label="相关机构"
                        :value="product_vendor_inner_name"
                        @input="organizationSelecter = true"
                        @clear="onSearchFormOrganizationClear"
                    ></list-page-header-modal-input>
                </template>
            </list-page-header>
        </template>
        <template slot="list">
            <list-page-list
                ref="tableRef"
                :columns="lossReportingColumn"
                :list="actualInventoryDatas"
                :loading="actualInventoryLoading"
                :cellStyle="cellStyle"
                :isPage="false"
            >
                <template slot="statuColumn">
                    <vxe-table-column :width="150" title="操作" field="lossNumber">
                        <template v-slot:default="{ row }">
                            <a-input-search
                                type="number"
                                v-model="row.lossNumber"
                                placeholder="报损数量"
                                size="small"
                                @search="addLoss(row)"
                            >
                                <a-button slot="enterButton">报损</a-button>
                            </a-input-search>
                        </template>
                    </vxe-table-column>
                </template>
            </list-page-list>
        </template>
        <template slot="modals">
            <organization-selecter
                title="选择厂商"
                destroyOnClose
                :visible="organizationSelecter"
                organizationType="ven"
                @close="organizationSelecter = false"
                @submit="onSearchFormOrganizationSelected"
            ></organization-selecter>
        </template>
    </list-page-layout>
</template>

<script>
import Big from 'big.js';
import ListPageLayout from '@/components/ListPageLayout';
import ListPageHeader from '@/components/ListPageHeader';
import ListPageList from '@/components/ListPageList.vue';
import ListPageHeaderSingleSearcher from '@/components/ListPageHeaderSingleSearcher';
import ListPageHeaderInput from '@/components/ListPageHeaderInput';
import ListPageHeaderSelect from '@/components/ListPageHeaderSelect';
import ListPageHeaderModalInput from '@/components/ListPageHeaderModalInput';
import OrganizationSelecter from '@/components/OrganizationSelecter';
import { lossReportingColumn } from './reportingColumns';
import { inventoryListLevelAPI } from '@/service/pageAjax';

export default {
    name: 'reportingLoss',
    components: {
        ListPageLayout,
        ListPageHeader,
        ListPageHeaderSingleSearcher,
        ListPageHeaderInput,
        ListPageHeaderSelect,
        ListPageHeaderModalInput,
        OrganizationSelecter,
        ListPageList
    },
    props: {
        // 管理模式下，部分功能需要过滤处理
        managerMode: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            isExpands: false, //是否显示高级搜索
            searchValues: {
                org_id: this.$cookie.get('userbelong'),
                orderByColumn: '',
                warehouse_inner_sn: '',
                product_name: '',
                specification: '',
                batch_number: '',
                product_vendor_inner_sn: ''
            },
            product_vendor_inner_name: '',
            lossReportingNmber: '',

            level: 3, //查询级别1-4

            organizationSelecter: false, //是否显示选择厂商弹框

            // 实际
            actualInventoryLoading: true, //表格loading
            actualInventoryDatas: [] //表格内容
        };
    },
    computed: {
        warehouseOptions() {
            return this.$store.state.warehouseList.map((warehouse) => ({
                label: warehouse.name,
                value: warehouse.warehouse_model_inner_sn
            }));
        },

        // 表头
        lossReportingColumn() {
            return lossReportingColumn;
        }
    },
    created() {
        this.getSearchData();
        let a = {
            name: "jock",
            age: 20
        }
    },
    methods: {
        // 显示隐藏高级搜索
        handleToggleSearchStatus(value) {
            this.isExpands = value;
        },

        // 查询
        getSearchData() {
            let params = this.searchValues;
            this.actualInventoryLoading = true;
            inventoryListLevelAPI(this.level, { params })
                .then((res) => {
                    this.actualInventoryLoading = false;
                    if (parseInt(res.code) === 0) {
                        this.actualInventoryDatas = res.list.map((item) => {
                            return {
                                ...item,
                                key: Math.random(),
                                activeClass: '',
                                storage_price: this.$Utils.commafy(new Big(item.unit_price).times(item.quantity)),
                                unit_price: this.$Utils.commafy(new Big(item.unit_price).round(4).toString())
                            };
                        });
                        console.log(this.actualInventoryDatas);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.actualInventoryLoading = false;
                });
        },

        // 选择厂商提交
        onSearchFormOrganizationSelected(data) {
            this.searchValues.product_vendor_inner_sn = data[0].organizationInnerSn;
            this.product_vendor_inner_name = data[0].organizationName;
            this.organizationSelecter = false;
        },

        // 相关机构取消
        onSearchFormOrganizationClear() {
            this.searchValues.product_vendor_inner_sn = '';
            this.product_vendor_inner_name = '';
        },

        // 判断是否过期，过期产品用红色字体表示
        cellStyle({ row }) {
            if (row.expire) {
                return {
                    color: '#f00'
                };
            }
        },

        // 添加报损数量
        addLoss(row) {
            console.log(row);
            let that = this;
            if (parseInt(row.lossNumber) > row.quantity) {
                this.$message.info('报损数量不能大于库存数量');
                return;
            }
            // 如果数量为空或者为0 ，不请求
            if (row.lossNumber && row.lossNumber != '' && row.lossNumber != '0') {
                this.$confirm({
                    title: '提示',
                    content: '确定报损吗？',
                    okText: '确认',
                    cancelText: '取消',
                    onOk() {
                        // 确认时更新本条库存数量 不刷新页面
                        that.$set(row, 'quantity', row.quantity - parseInt(row.lossNumber));

                        that.$set(row, 'lossNumber', '');
                    }
                });
            }
        }
    }
};
</script>
<style scoped lang="less">
/deep/ .ant-input {
    height: 26px;
    font-size: 12px;
}
</style>