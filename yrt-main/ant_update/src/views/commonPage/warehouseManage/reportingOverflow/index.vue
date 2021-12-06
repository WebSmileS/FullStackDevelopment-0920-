<!-- 
* @Author: yangtao  
* @Description: 报溢
-->
<template>
    <list-page-layout isTabListPage class="invertory-management">
        <template slot="header">
            <list-page-header
                needMixedSearch
                :operationGroups="operationGroups"
                :buttonsStatu="headerButtonsStatu"
                @btnClick="onHeaderButtonClick"
                @search="getSearchData"
                @expandsChange="handleToggleSearchStatus"
            >
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
                :columns="columnsLevelComputed"
                :list="actualInventoryDatas"
                :loading="actualInventoryLoading"
                :cellStyle="cellStyle"
                :isPage="false"
            >
                <template slot="statuColumn">
                    <vxe-table-column :width="150" title="操作" field="overflowNumber">
                        <template v-slot:default="{ row }">
                            <a-input-search
                                type="number"
                                v-model="row.overflowNumber"
                                placeholder="报溢数量"
                                size="small"
                                @search="addOverflow(row)"
                            >
                                <a-button slot="enterButton">报溢</a-button>
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
            <!-- 新增报溢弹框 -->
            <reporting-overflow-detail
                ref="detailTable"
                :visible="modelEditVisible"
                :targetOrder="detailTarget"
                @close="modelEditVisible = false"
                @orderFinish="onOrderFinish"
            ></reporting-overflow-detail>
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
import reportingOverflowDetail from './reportingOverflowDetail';
import { lossReportingColumn } from '../reportingLoss/reportingColumns';
import { inventoryListLevelAPI } from '@/service/pageAjax';
export default {
    name: 'reportingOverflow',
    components: {
        ListPageLayout,
        ListPageHeader,
        ListPageHeaderSingleSearcher,
        ListPageHeaderInput,
        ListPageHeaderSelect,
        ListPageHeaderModalInput,
        OrganizationSelecter,
        reportingOverflowDetail,
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
            // 顶部左侧按钮
            operationGroups: [[{ key: 'add', text: '新增', type: 'primary' }]],
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

            level: 3, //查询级别1-4

            organizationSelecter: false, //是否显示选择厂商弹框

            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },

            actualInventoryLoading: true, //表格loading
            actualInventoryDatas: [], //表格内容

            // 新增弹框
            modelEditVisible: false,

            detailTarget: undefined
        };
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.isExpands ? 214 : 154;
            return clientHeight - topHeight;
        },
        headerButtonsStatu() {
            const result = {
                add: { disabled: false }
            };
            return result;
        },
        warehouseOptions() {
            return this.$store.state.warehouseList.map((warehouse) => ({
                label: warehouse.name,
                value: warehouse.warehouse_model_inner_sn
            }));
        },

        // 计算表头
        columnsLevelComputed() {
            return lossReportingColumn;
        }
    },
    created() {
        this.getSearchData();
    },
    methods: {
        // 顶部按钮点击
        onHeaderButtonClick(key) {
            switch (key) {
                case 'add': //新增
                    this.modelEditVisible = true;
                    console.log('新增');
                    break;
            }
        },

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

        // 新增弹框关闭
        modelEditClose() {
            this.modelEditVisible = false;
        },

        // 判断是否过期，过期产品用红色字体表示
        cellStyle({ row }) {
            if (row.expire) {
                return {
                    color: '#f00'
                };
            }
        },

        // 添加报溢数量
        addOverflow(row) {
            console.log(row);
            let that = this;
            // 如果数量为空或者为0 ，不请求
            if (row.overflowNumber && row.overflowNumber != '' && row.overflowNumber != '0') {
                this.$confirm({
                    title: '提示',
                    content: '确定报溢吗？',
                    okText: '确认',
                    cancelText: '取消',
                    onOk() {
                        // 确认时更新本条库存数量 不刷新页面
                        that.$set(row, 'quantity', row.quantity + parseInt(row.overflowNumber));

                        // 保存之后清除输入框数据
                        that.$set(row, 'overflowNumber', '');
                    }
                });
            }
        },

        // 新增报溢弹框保存
        onOrderFinish(data) {
            console.log(data);
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
