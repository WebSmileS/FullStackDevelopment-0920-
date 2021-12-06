<template>
    <div class="scoped" :style="'margin: -24px;height: ' + (520 - ipadHeight) + 'px;'">
        <div class="search-box">
            <div class="search-item" style="padding-right: 16px">
                <span class="label">产品名称</span>
                <div class="input_box">
                    <a-input type="text" v-model="searchDataObj.productName" placeholder="" />
                </div>
            </div>
            <div class="search-item" style="padding-right: 16px">
                <span class="label">厂商/经销商</span>
                <div class="input_box">
                    <a-input type="text" v-model="searchDataObj.productVendorName" placeholder="" />
                </div>
            </div>
            <div class="search-item" style="padding-right: 16px">
                <span class="label">规格型号</span>
                <div class="input_box">
                    <a-input type="text" v-model="searchDataObj.specification" placeholder="" />
                </div>
            </div>
            <div class="search-item" style="min-width: 7%">
                <a-button class="ant-btn-sm" @click="searchDataFn" style="width: 65px" type="primary">搜索</a-button>
            </div>
        </div>
        <vxe-grid
            ref="productSearchContractTable"
            size="small"
            :columns="productSearchContractColumns"
            :height="scrollYProductSearchContractNumber - ipadHeight"
            style="margin: 0 22px"
            resizable
            auto-resize
            border
            show-overflow
            show-header-overflow
            showHeader
            highlight-current-row
            align="center"
            :data="tableData"
            :loading="tablePage.loading"
            :start-index="(tablePage.current - 1) * tablePage.pageSize"
        >
            <vxe-table-column type="seq" fixed="left" width="50" title="NO."></vxe-table-column>
            <vxe-table-column field="contract_sn" fixed="left" minWidth="10%" title="合同编号"></vxe-table-column>
            <vxe-table-column field="contract_name" minWidth="10%" title="合同名称"></vxe-table-column>
            <vxe-table-column field="partA_name" minWidth="12%" title="甲方"></vxe-table-column>
            <vxe-table-column field="partB_name" minWidth="12%" title="乙方"></vxe-table-column>
            <vxe-table-column field="status_text" minWidth="10%" title="合同状态"></vxe-table-column>
            <vxe-table-column field="product_name" minWidth="10%" title="产品名称"></vxe-table-column>
            <vxe-table-column field="specification" minWidth="10%" title="规格型号"></vxe-table-column>
            <vxe-table-column field="product_vendor_name" minWidth="10%" title="厂商/经销商"></vxe-table-column>
            <vxe-table-column field="unit" minWidth="6%" title="单位"></vxe-table-column>
            <vxe-table-column field="price_text" minWidth="10%" title="含税单价(元)"></vxe-table-column>
        </vxe-grid>
        <vxe-pager
            style="margin: 0 22px"
            :loading="tablePage.loading"
            :current-page="tablePage.current"
            :page-size="tablePage.pageSize"
            :page-sizes="tablePage.pageSizes"
            :total="tablePage.total"
            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="changeProductSearchContractPage"
        >
        </vxe-pager>
    </div>
</template>

<script>
import { searchContractDetailAPI } from '@/service/pageAjax';

export default {
    name: 'productSearchContractDetail',
    data() {
        return {
            contractDetailStatus: [
                '草稿',
                '审批进行中',
                '审批未通过',
                '审批通过',
                '有效履行中',
                '自然终止',
                '提前终止',
                '超期履行'
            ], // 合同状态(status): 0-草稿 1-审批进行中 2-审批未通过 3-审批通过 4-有效履行中 5-自然终止 6-提前终止 7-超期履行
            searchDataObj: {
                productName: '',
                productVendorName: '',
                specification: ''
            },
            tableData: [],
            tablePage: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            scrollYProductSearchContractNumber: 390,
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 57 : 0
        };
    },
    methods: {
        async searchContractDetailAPIFn() {
            // 查询合同产品(分页)
            this.tablePage.loading = true;
            await searchContractDetailAPI(this.tablePage.current, this.tablePage.pageSize, {
                'product_name': this.searchDataObj.productName,
                'product_vendor_name': this.searchDataObj.productVendorName,
                'specification': this.searchDataObj.specification
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.tableData = rows.map((item) => {
                            return {
                                ...item,
                                price_text: this.$XEUtils.commafy(item.price, { fixed: 4 }),
                                // price_text: item.price.toFixed(4),
                                status_text: this.contractDetailStatus[parseInt(item.status)]
                            };
                        });
                        this.tablePage.total = res.total;
                        this.$refs.productSearchContractTable.refreshColumn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.tablePage.loading = false;
                });
        },
        changeProductSearchContractPage({ currentPage, pageSize }) {
            this.tablePage.current = currentPage;
            this.tablePage.pageSize = pageSize;
            this.tablePage.loading = true;
            this.searchContractDetailAPIFn();
        },
        searchDataFn() {
            this.tablePage.loading = true;
            this.tablePage.current = 1;
            this.tablePage.pageSize = 10;
            this.tablePage.pageNum = 1;
            this.searchContractDetailAPIFn();
        }
    },
    mounted() {
        this.searchDataFn();
    }
};
</script>

<style scoped lang="less">
.search-box {
    display: flex;
    flex-direction: row;
    padding: 21px;

    .search-item {
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
        height: 24px;

        .label {
            padding-right: 10px;
        }

        .ant-input {
            height: 24px;
        }
    }
}
</style>
