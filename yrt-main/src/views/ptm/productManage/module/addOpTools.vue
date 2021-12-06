<template>
    <div class="scoped" style="margin: -24px; height: 590px">
        <div class="tab-table-wrap">
            <div class="search-box">
                <div class="search-item">
                    <span class="label">名称</span>
                    <div class="input_box">
                        <a-input type="text" v-model="searchData.search_name" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label">编号</span>
                    <div class="input_box">
                        <a-input type="text" v-model="searchData.kit_sn" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label">类别</span>
                    <div class="input_box input" style="margin-right: 10px">
                        <a-select allowClear="true" style="width: 170px" @change="changeSel">
                            <a-select-option value="0">颅骨类</a-select-option>
                            <a-select-option value="1">脊柱类</a-select-option>
                            <a-select-option value="2">关节类</a-select-option>
                            <a-select-option value="3">创伤类</a-select-option>
                        </a-select>
                    </div>
                </div>
                <div class="search-item">
                    <span class="label">品牌</span>
                    <div class="input_box" style="margin-right: 10px">
                        <a-select
                            v-model="searchData.brand_inner_sn"
                            allowClear="true"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            style="width: 180px"
                            @change="changeBrand"
                        >
                            <a-select-option v-for="(item, key) of brandList" :key="key" :value="item.brand_inner_sn">{{
                                item.name
                            }}</a-select-option>
                        </a-select>
                    </div>
                </div>
                <div class="search-item" style="min-width: 8%; margin-left: 26px">
                    <a-button class="ant-btn-sm" @click="searchPageFn" style="width: 66px" type="primary"
                        >搜索</a-button
                    >
                </div>
            </div>
            <a-table
                :loading="tabLoading"
                :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChangeVen }"
                :columns="columns"
                :dataSource="tableData"
                :pagination="false"
                :scroll="{ y: 480 }"
                :customRow="customRowFn"
            >
            </a-table>
        </div>
    </div>
</template>

<script>
import { surgeryKitListAPI, surgeryCustDealerKitListNoPaginationAPI, surgeryBrandListAPI } from '@/service/pageAjax';

export default {
    name: 'addOpTools',
    props: ['isAddOpTools'], // 为true是从新增页面进来的
    data() {
        return {
            searchData: {
                search_name: '',
                type_inner_sn: '',
                brand_inner_sn: '',
                kit_sn: ''
            },
            brandList: [],
            chooseTrObj: {},
            tabLoading: false,
            selectedRowKeys: [],
            selectedRows: [],
            columns: [
                {
                    title: '编号',
                    dataIndex: 'kit_sn',
                    width: '230',
                    align: 'center'
                },
                {
                    title: '名称',
                    dataIndex: 'name',
                    width: '220',
                    align: 'center'
                },
                {
                    title: '类别',
                    dataIndex: 'type_name',
                    width: '200',
                    align: 'center'
                },
                {
                    title: '品牌',
                    dataIndex: 'brand_name',
                    width: '200',
                    align: 'center'
                }
            ],
            tableData: []
        };
    },
    methods: {
        pageDataInit() {
            this.searchData = {
                search_name: '',
                type_inner_sn: '',
                brand_inner_sn: '',
                kit_sn: ''
            };
            this.searchPageFn();
            this.getBrandList();
        },
        searchPageFn() {
            this.userSystemType === 0 || this.isAddOpTools
                ? this.loadTable()
                : this.surgeryCustDealerKitListNoPaginationAPIFn();
        },
        returnProList() {
            if (this.selectedRows.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择工具包',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            const returnDataRows = this.selectedRows.map((item) => {
                return {
                    ...item,
                    temp_add_flag: true // 判断是否是新增的
                };
            });
            this.$emit('returnData', returnDataRows);
            this.selectedRows = [];
            this.selectedRowKeys = [];
        },
        async loadTable() {
            if (this.$Utils.checkInputValFn(this.searchData.search_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                // this.pageInit.loading = false
                return false;
            }
            let params = {
                ...this.searchData,

                // 排序
                orderByColumn: 'kit_inner_sn',
                isAsc: 'desc'
            };
            this.tabLoading = true;
            this.tableData = [];
            await surgeryKitListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    res.rows.forEach((item) => {
                        item.isActive = '';
                        item.key = item.kit_inner_sn;
                        if (item.status === 1) {
                            this.tableData.push(item);
                        }
                    });
                }
                this.tabLoading = false;
            });
        },
        async surgeryCustDealerKitListNoPaginationAPIFn() {
            if (this.$Utils.checkInputValFn(this.searchData.search_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            this.tabLoading = true;
            let params = {};
            if (this.searchData.search_name) {
                params.q = this.searchData.search_name;
            }
            if (this.searchData.brand_inner_sn) {
                params.brands = [this.searchData.brand_inner_sn];
            }
            if (this.searchData.kit_sn) {
                params.kit_sn = this.searchData.kit_sn;
            }
            if (this.searchData.type_inner_sn) {
                params.types = [this.searchData.type_inner_sn];
            }
            if (this.userSystemType === 2) {
                // 经销商登录
                params.searchScope = 'PRIVATE';
            }
            await surgeryCustDealerKitListNoPaginationAPI(this.$cookie.get('userbelong'), params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.tableData = rows.map((item) => {
                            return {
                                ...item,
                                isActive: '',
                                key: item.id + '' + item.platform_kit_inner_sn
                            };
                        });
                    }
                })
                .finally(() => {
                    this.tabLoading = false;
                });
        },
        changeBrand(val) {
            // 切换类型
            this.searchData.brand_inner_sn = val;
        },
        changeSel(val) {
            // 切换类型
            this.searchData.type_inner_sn = val;
        },
        customRowFn(record) {
            // 设置行属性 —— 点击每一行的事件
            return {
                class: record.isActive,
                on: {
                    click: () => {
                        this.chooseTrEvent(record);
                    }
                }
            };
        },
        chooseTrEvent(record) {
            // 选中事件
            this.chooseTr(record.key);
            this.chooseTrObj = record;
        },
        chooseTr(id) {
            // 选中行渲染
            this.isChooseTr = true;
            this.tableData.forEach((item) => {
                if (item.key === id) {
                    item.isActive = 'active-tr';
                } else {
                    item.isActive = '';
                }
            });
        },
        async getBrandList() {
            // 获取品牌列表
            await surgeryBrandListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    let newList = [];
                    res.list.forEach((item) => {
                        item.key = item.brand_inner_sn;
                        if (parseInt(item.status) === 1) {
                            item.status_text = '有效';
                            newList.push(item);
                        } else {
                            item.status_text = '无效';
                        }
                    });
                    this.brandList = newList;
                }
            });
        },
        onSelectChangeVen(selectedRowKeys, selectedRows) {
            this.selectedRowKeys = selectedRowKeys;
            this.selectedRows = selectedRows;
        }
    },
    computed: {
        userSystemType() {
            return parseFloat(this.$cookie.get('userSystemType'));
        }
    },
    mounted() {
        this.pageDataInit();
    }
};
</script>

<style scoped lang="less">
.search-box {
    margin: 20px 0;
    .search-item {
        min-width: 20%;
        display: inline-block;
        vertical-align: top;
        .ant-cascader-input.ant-input {
            height: 24px !important;
        }
    }
    .label {
        display: inline-block;
        min-width: 40px;
        text-align: right;
        margin-right: 10px;
    }
    .input_box {
        display: inline-block;
        width: 160px;
        .ant-input {
            line-height: 24px;
            height: 24px;
        }
    }
}

// 厂商/经销商
.input_box {
    .close {
        position: absolute;
        right: 6px;
        transform: scale(0.9) translateY(-50%);
        top: 50%;
        z-index: 2;
        width: 15px;
        height: 15px;
        border-radius: 50%;
        background: #b9b9b9;
        font-size: 6px;
        color: #fff;
        text-align: center;
        line-height: 15px;
        display: none;
        cursor: pointer;
    }
    &:hover .close {
        display: block;
    }
}
</style>
