<template>
    <a-spin :spinning="spinning">
        <div class="scoped modal_box" style="height: 500px">
            <div class="search-box">
                <span class="label">手术包名称</span>
                <div class="input_box" style="width: 140px">
                    <a-input type="text" v-model="searchData.search_name" placeholder="" />
                </div>
                <span class="label" style="width: 50px">类别</span>
                <a-select
                    allowClear="true"
                    :value="searchData.type_inner_sn"
                    style="width: 150px; margin-right: 20px; display: inline-block; vertical-align: bottom"
                    @change="changeSel"
                >
                    <a-select-option value="">全部</a-select-option>
                    <a-select-option value="0">颅骨类</a-select-option>
                    <a-select-option value="1">脊柱类</a-select-option>
                    <a-select-option value="2">关节类</a-select-option>
                    <a-select-option value="3">创伤类</a-select-option>
                </a-select>
                <span class="label" style="width: 50px"><em class="em red">*</em>品牌</span>
                <div class="input_box" style="width: 150px">
                    <a-select
                        allowClear="true"
                        v-model="searchData.brand_inner_sn"
                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                        style="width: 150px"
                        @change="changeBrand"
                    >
                        <a-select-option v-for="(item, key) of brandList" :key="key" :value="item.brand_inner_sn">{{
                            item.name
                        }}</a-select-option>
                    </a-select>
                </div>
                <a-button style="height: 24px; vertical-align: top; margin-left: 10px" @click="initFn" type="primary">
                    搜索
                </a-button>
            </div>
            <div class="op_kits_list">
                <div
                    v-for="(item, index) of dataList"
                    :key="index"
                    class="op_div"
                    :class="item.is_cust ? 'op_is_cust' : ''"
                    @click="chooseKitsFn(item)"
                >
                    <a-tooltip placement="bottomRight">
                        <template slot="title">
                            <div>备注: {{ item.description ? item.description : '--' }}</div>
                        </template>
                        <div style="height: 100%">
                            <ul class="op_kit">
                                <li>
                                    <div class="brand">{{ item.brand_name }}</div>
                                    <!-- 品牌： -->
                                    <div v-if="item.active" class="ico_active">
                                        <a-icon type="check-circle" />
                                    </div>
                                </li>
                                <li>
                                    <div class="name">{{ item.name }}</div>
                                    <!-- 名称： -->
                                </li>
                                <li>
                                    <div class="position">{{ item.type_name ? item.type_name : '--' }}</div>
                                    <!-- 部位 -->
                                </li>
                                <li>
                                    <div class="serial-number">{{ item.ois_sn ? item.ois_sn : '--' }}</div>
                                    <!-- 编号： -->
                                </li>
                            </ul>
                        </div>
                    </a-tooltip>
                </div>
            </div>
        </div>
    </a-spin>
</template>

<script>
import {
    surgeryPackageListAPI,
    surgeryPathRelpackageAPI,
    surgeryBrandListAPI,
    surgeryCustDealerPackageAddToPathAPI,
    surgeryCustDealerPackageListNoPaginationAPI
} from '@/service/pageAjax';

export default {
    name: 'addOpKits',
    props: ['editPathObj'],
    data() {
        return {
            spinning: false,
            searchData: {
                search_name: '',
                type_inner_sn: '',
                brand_inner_sn: ''
            },
            dataList: [],
            typeName: ['颅骨', '脊柱', '关节', '创伤'],
            brandList: [],
            chooseList: []
        };
    },
    methods: {
        pageDataInit() {
            this.searchData.search_name = '';
            this.searchData.type_inner_sn = '';
            this.searchData.brand_inner_sn = '';
            this.getBrandList();
            this.initFn();
        },
        initFn() {
            if (this.userSystemType === 0) {
                this.loadTable();
            } else {
                this.surgeryCustDealerPackageListNoPaginationAPIFn();
            }
        },
        changeSel(val) {
            this.searchData.type_inner_sn = val;
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
        changeBrand(val) {
            // 切换类型
            this.searchData.brand_inner_sn = val;
        },
        async surgeryCustDealerPackageListNoPaginationAPIFn() {
            // 加载节点
            if (this.$Utils.checkInputValFn(this.searchData.search_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return;
            }
            this.spinning = true;
            let params = {
                searchScope: 'PRIVATE' // 只拿私有的手术包
            };
            if (this.searchData.search_name) {
                params.q = this.searchData.search_name;
            }
            if (this.searchData.brand_inner_sn) {
                params.brands = [this.searchData.brand_inner_sn];
            }
            if (this.searchData.type_inner_sn) {
                params.brands = [this.searchData.type_inner_sn];
            }
            await surgeryCustDealerPackageListNoPaginationAPI(this.$cookie.get('userbelong'), params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        res.rows.forEach((item) => {
                            item.active = false;
                        });
                        this.dataList = res.rows;
                        this.chooseList = [];
                    }
                })
                .finally(() => {
                    this.spinning = false;
                });
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
            this.spinning = true;

            let params = {
                ...this.searchData,

                // 排序
                orderByColumn: 'ois_inner_sn',
                isAsc: 'desc'
            };

            // params.pid = this.editPathObj.op_inner_sn
            await surgeryPackageListAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        res.rows.forEach((item) => {
                            item.active = false;
                        });
                        this.dataList = res.rows;
                        this.chooseList = [];
                    }
                })
                .finally(() => {
                    this.spinning = false;
                });
        },
        async addKitsFn() {
            // 添加手术包
            if (this.checkOperationPathFn()) return;
            let kitsId = this.chooseList.join(',');
            let params = {
                op_inner_sn: this.editPathObj.op_inner_sn,
                packages: kitsId
            };
            await surgeryPathRelpackageAPI(params).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.$emit('done');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async surgeryCustDealerPackageAddToPathAPIFn() {
            if (this.checkOperationPathFn()) return;
            await surgeryCustDealerPackageAddToPathAPI(this.editPathObj.op_inner_sn, this.chooseList).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.$emit('done');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        checkOperationPathFn() {
            if (this.chooseList.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择手术包',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return true;
            }
            return false;
        },
        chooseKitsFn(curItem) {
            // 选中手术包
            if (this.userSystemType === 2 && !curItem.is_cust) return; // 经销商只能添加私有的手术包
            const curId = curItem.ois_inner_sn || curItem.id || curItem.platform_ois_inner_sn;
            this.dataList.forEach((item) => {
                const id = item.ois_inner_sn || item.id || item.platform_ois_inner_sn;
                if (id === curId) {
                    if (!item.active) {
                        item.active = true;
                        this.chooseList.push(id);
                    } else {
                        item.active = false;
                        let idx;
                        this.chooseList.forEach((item, i) => {
                            if (item === id) {
                                idx = i;
                            }
                        });
                        this.chooseList.splice(idx, 1);
                    }
                }
            });
        }
    },
    computed: {
        userSystemType() {
            return parseFloat(this.$cookie.get('userSystemType'));
        }
    },
    created() {
        this.pageDataInit();
    }
};
</script>

<style scoped lang="less">
.modal_box {
    margin: -24px;
    background: #f7f7f7;
}
.op_kits_list {
    display: flex;
    flex-wrap: wrap;
    text-align: left;
    padding: 10px;
    overflow-y: auto;
    max-height: 436px;

    .op_div {
        display: flex;
        flex-direction: column;
        width: 240px;
        height: 127px;
        margin: 0 0 18px 10px;
        background-repeat: no-repeat;
        background-size: cover;
        background-image: url('../../../../../public/images/pic/operation-kits-platform.png');
        cursor: pointer;

        &.op_is_cust {
            background-image: url('../../../../../public/images/pic/operation-kits-private.png');
        }
    }
    .op_kit {
        position: relative;
        display: flex;
        flex-direction: column;
        padding: 11px 0 8px;
        margin-bottom: 0;

        li {
            &:nth-of-type(1) {
                display: flex;
                justify-content: space-between;
                align-items: center;
                font-size: 14px;
                padding: 0 16px 0 18px;
                color: #565656;

                .brand {
                    width: 100px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    font-weight: 600;
                }

                .ico_active {
                    color: #06c506;
                }
            }
            &:nth-of-type(2) {
                display: flex;
                justify-content: center;
                align-items: center;
                font-size: 16px;
                font-weight: 700;
                padding: 11px 0 8px;
                color: #565656;

                .name {
                    width: 160px;
                    height: 45px;
                    text-align: center;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    // display: -webkit-box;
                    -webkit-box-orient: vertical;
                    -webkit-box-orient: vertical;
                    -webkit-line-clamp: 2;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
            }
            &:nth-of-type(3) {
                position: absolute;
                right: 24px;
                bottom: -5px;
                font-weight: 600;
            }
            &:nth-of-type(4) {
                position: absolute;
                right: 24px;
                bottom: -22px;
            }
            &:nth-of-type(3),
            &:nth-of-type(4) {
                display: flex;
                justify-content: flex-end;
                align-items: center;
                font-size: 14px;
                color: #565656;
            }
        }

        .ico_box {
            position: absolute;
            right: 18px;
            top: 35px;
            width: 44px;
            img {
                width: 100%;
            }
        }
        .ico_add {
            display: none;
            position: absolute;
            left: 0%;
            top: 0;
            font-size: 30px;
            width: 100%;
            text-align: center;
            color: #fff;
            background: #000;
            height: 100%;
            line-height: 87px;
            opacity: 0.35;
        }
        .ico_del {
            cursor: pointer;
            display: none;
            position: absolute;
            right: 1px;
            top: 9px;
            font-size: 18px;
            text-align: center;

            .del_ico {
                color: red;
                font-size: 18px;
            }
        }
    }
    .op_kit:hover {
        .ico_del {
            display: inline-block;
        }
    }
}
.search-box {
    padding: 20px;
    background: #fff;
    .em {
        font-size: 20px;
        vertical-align: middle;
        line-height: 12px;
        margin-right: 3px;
    }
    .label {
        display: inline-block;
        width: 70px;
        text-align: right;
        margin-right: 10px;
    }
    .input_box {
        display: inline-block;
        width: 200px;
        .ant-input {
            line-height: 24px;
            height: 24px;
        }
    }
}
</style>
