<template>
    <div>
        <div class="modal-form-input" style="padding-top: 0">
            <ul class="top-list" style="width: 100%">
                <li class="item">
                    <span class="label">单据日期</span>
                    <div class="input">
                        <a-range-picker
                            v-model="rangePickerVal"
                            :placeholder="['', '']"
                            @change="changeRangePickerFn"
                            size="small"
                        >
                        </a-range-picker>
                    </div>
                </li>
                <li class="item">
                    <span class="label">产品名称</span>
                    <div class="input">
                        <a-input placeholder="产品" size="small" v-model="searchName" />
                    </div>
                </li>
                <li class="item">
                    <span class="label">厂商</span>
                    <div class="input">
                        <a-input
                            type="text"
                            size="small"
                            :readOnly="true"
                            v-model="vendorObj.name"
                            @click.stop="openChooseOrg"
                            placeholder=""
                        />
                        <span v-show="vendorObj.name" @click="clearSearchDataVenFn" class="close">X</span>
                    </div>
                </li>
            </ul>
            <ul class="top-list" style="width: 100%">
                <li class="item">
                    <span class="label">单据编号</span>
                    <div class="input">
                        <a-input placeholder="单据编号" size="small" v-model="searchSn" />
                    </div>
                </li>
                <li class="item">
                    <span class="label">规格型号</span>
                    <div class="input">
                        <a-input placeholder="规格型号" size="small" v-model="searchSpecification" />
                    </div>
                </li>
                <li class="item"></li>
                <!-- <li class="item">
                    <span class="label">已选仓库</span>
                    <div class="input">
                        {{ searchWarehouseName }}
                        <a-tree-select
                            treeNodeFilterProp="title"
                            :showSearch="true"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            :treeData="warehouseList"
                            placeholder=""
                            treeDefaultExpandAll
                            allowClear
                            class="form-input"
                            size="small"
                            v-model="searchWarehouseInnerSn"
                        >
                        </a-tree-select>
                    </div>
                </li> -->
                <li style="width: auto">
                    <a-button @click="handleSearch" size="small" type="primary"> 搜索 </a-button>
                </li>
            </ul>
        </div>

        <a-modal
            v-model="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            @cancel="chooseOrgMd.alert = false"
            :zIndex="3"
            class="scoped modal-form-input-scoped global-drag choose_org_md_add"
        >
            <div v-globalDrag="{ el: 'choose_org_md_add' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <div style="margin-top: 56px">
                <choose-org-tab
                    ref="chooseOrgMd"
                    @chooseOrg="chooseOrgFn"
                    :pageTypeName="pageTypeName"
                ></choose-org-tab>
            </div>
        </a-modal>
    </div>
</template>

<script>
import moment from 'moment';
import chooseOrgTab from '@/components/chooseOrgTab';

export default {
    name: 'returnSearch',
    props: {
        /// 是否是入库单
        is_GRN: {
            type: Boolean,
            default: true
        },
        /// 搜索值
        searchData: {
            type: Object,
            required: true
        },
        // 表单info数据
        formData: {
            type: Object,
            retuired: true
        }
    },
    data() {
        return {
            rangePickerVal: moment[''], // 单据日期
            searchName: '',
            searchProductVendorInnerSn: '',
            searchSn: '',
            searchSpecification: '',
            searchBeginDate: '',
            searchEndDate: '',
            searchWarehouseName: '',
            searchWarehouseInnerSn: '', // warehouse_inner_sn
            vendorObj: {
                name: '', // product_vendor_name
                id: '' // product_vendor_inner_sn
            },
            chooseOrgMd: {
                title: `${this.$route.meta.title} - 选择机构`,
                alert: false,
                loading: false
            }
        };
    },
    methods: {
        openChooseOrg() {
            // 打开选择机构
            this.chooseOrgMd.alert = true;
            // this.$nextTick(() => {
            //     if (this.$refs.chooseOrgMd) {
            //         this.$refs.chooseOrgMd.pageDataInit()
            //     }
            // })
            this.$Utils.globalDragCenterFn('choose_org_md_add');
        },
        chooseOrgFn(res) {
            this.chooseOrgMd.alert = false;
            this.vendorObj.id = res.org_id;
            this.vendorObj.name = res.org_name;
        },
        changeRangePickerFn(value, dateString) {
            const [beginTime, endTime] = dateString;
            this.searchBeginDate = beginTime;
            this.searchEndDate = endTime;
        },
        handleSearch() {
            this.$emit('searchData', {
                begin_date: this.searchBeginDate,
                end_date: this.searchEndDate,
                name: this.searchName,
                // product_vendor_inner_sn: this.vendorObj.id,
                product_vendor_name: this.vendorObj.name,
                sn: this.searchSn,
                specification: this.searchSpecification
            });
        },
        // 清除厂商名称
        clearSearchDataVenFn() {
            this.vendorObj.name = '';
            this.vendorObj.id = '';
        }
    },
    watch: {
        searchData: {
            handler(newVal) {
                if (newVal) {
                    this.searchWarehouseName = newVal.warehouse_name;
                    this.searchWarehouseInnerSn = newVal.warehouse_inner_sn;
                }

                if (!this.searchData.warehouse_name) {
                    this.searchWarehouseName = this.formData.warehouse_name;
                    this.searchWarehouseInnerSn = this.formData.warehouse_inner_sn;
                }
            },
            deep: true,
            immediate: true
        }
    },
    computed: {
        pageTypeName() {
            return this.is_GRN ? 0 : 2;
        }
    },
    components: {
        chooseOrgTab
    }
};
</script>

<style lang="less" scoped>
.input {
    position: relative;

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
