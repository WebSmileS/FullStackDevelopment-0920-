<!--suppress ALL -->
<template>
    <div class="product-page">
        <div class="todo-part">
            <a-row>
                <a-col :span="12" class="btn-count">
                    <a-button class="btn-nom mgr-10" @click="issueBtn" type="primary">发布</a-button>
                    <a-button class="btn-nom mgr-10" @click="changeBtn" type="primary">修改</a-button>
                </a-col>
                <a-col :span="12" class="align-r">
                    <a-input
                        v-if="!highSearch"
                        placeholder="产品"
                        v-model="searchData.name"
                        size="small"
                        class="input-nom"
                        style="width: 70%"
                    >
                        <a-button
                            @click="searchFn"
                            class="btn-input"
                            icon="search"
                            slot="suffix"
                            type="primary"
                        ></a-button>
                    </a-input>
                    <div class="adv-search mgl-20">
                        <a-tooltip placement="left" title="高级搜索">
                            <a-icon v-if="!highSearch" type="down-circle" @click="highSearch = true" />
                            <a-icon v-else type="up-circle" @click="highSearch = false" />
                        </a-tooltip>
                    </div>
                </a-col>
            </a-row>
            <a-row class="high-search-box pdl-10" :class="{ hide: !highSearch }">
                <a-col :span="24" class="col-list">
                    <!-- 高级搜索 -->
                    <ul class="form">
                        <li>
                            <span class="label">产品</span>
                            <a-input type="text" v-model="searchData.name" placeholder="" />
                        </li>
                        <li style="position: relative">
                            <span class="label">厂商</span>
                            <a-input readonly="readonly" class="input" @click="openChooseTb" v-model="vendorObj.name" />
                            <span v-if="vendorObj.name != ''" @click="clearSearchDataVenFn()" class="close">X</span>
                        </li>
                        <li>
                            <span class="label">类型</span>
                            <a-cascader
                                v-model="searchData.type_inner_sn"
                                :options="categoryList"
                                :loadData="loadCategory"
                                placeholder=""
                                :displayRender="displayRender"
                            />
                        </li>
                        <li>
                            <span class="label" style="white-space: nowrap">产品分类</span>
                            <a-cascader
                                v-model="searchData.code68_sn"
                                :options="op68List"
                                changeOnSelect
                                :loadData="load68data"
                                placeholder=""
                                :displayRender="displayRender"
                            />
                        </li>
                        <li>
                            <a-button class="btn-nom" type="primary" @click="searchFn"> 搜索 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <a-tabs
            class="align-l"
            style="margin-top: 3px"
            @change="changeTabsFn"
            v-bind:style="{ height: tableHeight + 'px' }"
            type="card"
        >
            <a-tab-pane :key="tabTypeName[0].key" :tab="tabTypeName[0].name">
                <ptm-build :searchData="searchData" ref="ptmBuildPg" :highSearch="highSearch"></ptm-build>
            </a-tab-pane>
            <a-tab-pane :key="tabTypeName[1].key" :tab="tabTypeName[1].name">
                <vender-build :searchData="searchData" ref="venderBuildPg" :highSearch="highSearch"></vender-build>
            </a-tab-pane>
            <!-- <a-tab-pane
                :key="tabTypeName[2].key"
                :tab="tabTypeName[2].name"
            >
                <dealer-build :searchData="searchData" ref="dealerBuildPg" :highSearch="highSearch"></dealer-build>
            </a-tab-pane> -->
            <a-tooltip slot="tabBarExtraContent" placement="left">
                <template slot="title">
                    <span>刷新</span>
                </template>
                <a-button
                    type="primary"
                    shape="circle"
                    icon="reload"
                    size="small"
                    class="refresh-btn"
                    @click="initTablesFn"
                />
                <!--刷新按钮区域-->
            </a-tooltip>
        </a-tabs>
        <!-- 模态框区域 -->
        <a-modal
            :title="chooseOrgMd.title"
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            @cancel="chooseOrgMd.alert = false"
            class="modal-form-input-scoped"
        >
            <choose-org-tab
                ref="chooseOrgMd"
                @chooseOrg="chooseOrgFn"
                :partType="partType"
                :pageTypeName="1"
            ></choose-org-tab>
        </a-modal>
    </div>
</template>

<script>
import ptmBuild from './ptmBuild';
import venderBuild from './venderBuild';
// import dealerBuild from './dealerBuild'
import chooseOrgTab from '@/components/chooseOrgTab';

import {
    getVendorList,
    categoryGetNextListAPI,
    categoryGetParentListAPI,
    code68ParentListAPI,
    code68ChildrenListAPI
} from '@/service/pageAjax';

export default {
    name: 'productIssue',
    components: {
        ptmBuild,
        venderBuild,
        // dealerBuild,
        chooseOrgTab
    },
    data() {
        return {
            tableHeight: 300,
            vendorList: [],
            categoryList: [],
            op68List: [],
            isSearched: false, // 判断是否搜索过
            partType: 'ven',
            chooseOrgMd: {
                title: `${this.$route.meta.title}  - 选择机构`,
                alert: false,
                loading: false
            },
            vendorObj: {
                name: ''
            },
            searchData: {
                name: '',
                vendor_inner_sn: '', // 产商ID
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [] // 医疗器械68分类编号
            },
            highSearch: false,
            tabTypeName: [
                {
                    key: 1,
                    name: '平台初建产品'
                },
                {
                    key: 2,
                    name: '厂商产品'
                },
                {
                    key: 3,
                    name: '经销商自建产品'
                }
            ],
            tabActive: 1,
            tabName: [
                {
                    name: '产品图片',
                    key: 'goodsPic'
                },
                {
                    name: '医疗器械注册证',
                    key: 'cert'
                },
                {
                    name: '医疗器械注册登记表',
                    key: 'regForm'
                },
                {
                    name: '质量检测报告'
                }
            ]
        };
    },
    methods: {
        openChooseTb() {
            // 打开选择厂商
            this.chooseOrgMd.alert = true;
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
        },
        clearSearchDataVenFn() {
            // 清空选择
            this.vendorObj.name = '';
            this.searchData.vendor_inner_sn = '';
        },
        async chooseOrgFn(org) {
            // 获取选中机构信息
            this.vendorObj.name = org.org_name;
            this.searchData.vendor_inner_sn = org.org_id;
            this.chooseOrgMd.alert = false;
        },
        async getVendorListFn() {
            // 获取厂商
            await getVendorList().then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.vendorList = res.list;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        changeVendor(val) {
            // 选择厂商
            this.searchData.vendor_inner_sn = val;
        },
        async getCategoryListFn() {
            // 获取自定义类型列表
            await categoryGetParentListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    this.categoryList = rows.map((item) => {
                        let st;
                        if (item.is_leaf === 1) {
                            st = true;
                        } else if (item.is_leaf === 0) {
                            st = false;
                        }
                        item.label = item.type;
                        item.value = item.type_inner_sn;
                        item.isLeaf = st;
                        return item;
                    });
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async loadCategory(options) {
            // 加载自定义类型列表子集
            const targetOption = options[options.length - 1];
            targetOption.loading = true;
            const id = targetOption.value;
            await categoryGetNextListAPI(id).then((res) => {
                targetOption.loading = false;
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    if (rows.length > 0) {
                        targetOption.children = rows.map((item) => {
                            let st;
                            if (item.is_leaf === 1) {
                                st = true;
                            } else if (item.is_leaf === 0) {
                                st = false;
                            }
                            item.label = item.type;
                            item.value = item.type_inner_sn;
                            item.isLeaf = st;
                            return item;
                        });
                    } else {
                        targetOption.isLeaf = true;
                    }
                    targetOption.loading = false;
                    this.categoryList = [...this.categoryList];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async get68ParentListFn() {
            // 获取68分类
            await code68ParentListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    let newArray = [];
                    rows.forEach((item) => {
                        let st;
                        if (item.is_leaf === 1) {
                            st = true;
                        } else if (item.is_leaf === 0) {
                            st = false;
                        }
                        let obj = {
                            label: '(' + item.code68_sn + ') ' + item.name,
                            value: item.code68_sn,
                            isLeaf: st
                        };
                        newArray.push(obj);
                    });
                    this.op68List = newArray;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async load68data(options) {
            // 加载68分
            const targetOption = options[options.length - 1];
            targetOption.loading = true;
            const id = targetOption.value;
            await code68ChildrenListAPI(id).then((res) => {
                targetOption.loading = false;
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    if (rows.length > 0) {
                        targetOption.children = rows.map((item) => {
                            let st;
                            if (item.is_leaf === 1) {
                                st = true;
                            } else if (item.is_leaf === 0) {
                                st = false;
                            }
                            item.label = '(' + item.code68_sn + ') ' + item.name;
                            item.value = item.code68_sn;
                            item.isLeaf = st;
                            return item;
                        });
                    } else {
                        targetOption.isLeaf = true;
                    }
                    targetOption.loading = false;
                    this.op68List = [...this.op68List];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        displayRender(obj) {
            let len = obj.labels.length;
            const label = obj.labels[len - 1];
            return label;
        },
        changeTabsFn(activeKey) {
            // 切换tab
            this.tabActive = parseFloat(activeKey);
            if (!this.isSearched) {
                // 如果没有搜索过，初始化搜索条件
                this.searchData = {
                    name: '',
                    vendor_inner_sn: '', // 产商ID
                    type_inner_sn: [], // 自定义分类内部编号
                    code68_sn: [] // 医疗器械68分类编号
                };
            }
            // 平台 1 - 厂商 2 - 经销商 3
            if (this.tabActive === 1) {
                let module = this.$refs.ptmBuildPg;
                if (module) {
                    module.loadTable();
                }
            } else if (this.tabActive === 2) {
                let module = this.$refs.venderBuildPg;
                if (module) {
                    module.loadTable();
                }
            } else if (this.tabActive === 3) {
                let module = this.$refs.dealerBuildPg;
                if (module) {
                    module.loadTable();
                }
            }
        },
        issueBtn() {
            // 发布
            // 平台 1 - 厂商 2 - 经销商 3
            if (this.tabActive === 1) {
                this.$refs.ptmBuildPg.issueFn();
            } else if (this.tabActive === 2) {
                this.$refs.venderBuildPg.issueFn();
            } else if (this.tabActive === 3) {
                this.$refs.dealerBuildPg.issueFn();
            }
        },
        changeBtn() {
            // 编辑
            // 平台 1 - 厂商 2 - 经销商 3
            if (this.tabActive === 1) {
                this.$refs.ptmBuildPg.openEdit();
            } else if (this.tabActive === 2) {
                this.$refs.venderBuildPg.openEdit();
            } else if (this.tabActive === 3) {
                this.$refs.dealerBuildPg.openEdit();
            }
        },
        searchFn() {
            // 搜索
            if (this.$Utils.checkInputValFn(this.searchData.name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            // 平台 1 - 厂商 2 - 经销商 3
            this.isSearched = true;
            if (this.tabActive === 1) {
                this.$refs.ptmBuildPg.initPage();
            } else if (this.tabActive === 2) {
                this.$refs.venderBuildPg.initPage();
            } else if (this.tabActive === 3) {
                this.$refs.dealerBuildPg.initPage();
            }
        },
        initTablesFn() {
            this.searchData = {
                name: '',
                vendor_inner_sn: '', // 产商ID
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [] // 医疗器械68分类编号
            };
            this.isSearched = false;
            if (this.tabActive === 1) {
                // this.$refs.ptmBuildPg.initPageNum()
                this.$refs.ptmBuildPg.loadTable();
            } else if (this.tabActive === 2) {
                // this.$refs.venderBuildPg.initPageNum()
                this.$refs.venderBuildPg.loadTable();
            } else if (this.tabActive === 3) {
                // this.$refs.dealerBuildPg.initPageNum()
                this.$refs.dealerBuildPg.loadTable();
            }
        },
        autoTabHeight() {
            // 自动计算表格高度
            let orderHeight = document.body.clientHeight - 48 - 56 - 10;
            this.tableHeight = orderHeight + 20 + 4;
        }
    },
    watch: {
        highSearch(val) {
            if (val) {
                this.tableHeight = this.tableHeight - 38;
            } else {
                this.tableHeight = this.tableHeight + 38;
            }
        }
    },
    mounted() {
        this.getCategoryListFn();
        this.get68ParentListFn();
        this.autoTabHeight();
    }
};
</script>

<style scoped lang="less">
@import '../../../../public/style/ptmStyle1';
</style>
