<template>
    <div class="scoped" style="margin: -24px">
        <div class="tab-table-wrap no_left">
            <div class="head_box">
                <div class="tit_box">价格查询</div>
            </div>

            <div class="search-box">
                <div class="search-item">
                    <span class="label">厂商</span>
                    <div class="input_box">
                        <a-input
                            allow-clear
                            type="text"
                            readonly="readonly"
                            v-model="orgName"
                            size="small"
                            @click="openChooseTb"
                            @change="onChangeOrg"
                            placeholder=""
                        />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label"><em class="em red">*</em>产品</span>
                    <div class="input_box">
                        <a-input allow-clear size="small" type="text" v-model="formData.product_name" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label">规格型号</span>
                    <div class="input_box">
                        <a-input type="text" v-model="formData.specification" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <div class="input_box isNew">
                        <a-radio-group v-model="formData.isNew" @change="handleRadioChange">
                            <a-radio :value="true"> 最新报价 </a-radio>
                            <a-radio :value="false"> 最低报价 </a-radio>
                        </a-radio-group>
                    </div>
                </div>
                <div class="search-item">
                    <a-button @click="handleSearch" size="small" type="primary">查询</a-button>
                </div>
            </div>

            <div style="padding: 0 22px; margin-bottom: 20px">
                <vxe-grid
                    ref="xTable"
                    size="small"
                    :columns="columns"
                    height="440"
                    resizable
                    auto-resize
                    border
                    show-overflow
                    show-header-overflow
                    highlight-current-row
                    align="center"
                    :data="proTableData"
                    :loading="tableLoading"
                />
            </div>
        </div>
        <div class="button_cont">
            <a-button @click="$emit('cancel')" style="float: right; right: 22px; top: 13px">关闭</a-button>
        </div>
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
            <choose-org-tab ref="chooseOrgMd" @chooseOrg="chooseOrgFn" partType="ven" :isCertifie="true" />
        </a-modal>
    </div>
</template>

<script>
import { inquiryPriceListAPI } from '@/service/pageAjax';

import chooseOrgTab from '@/components/chooseOrgTab';

export default {
    name: 'createInquiry',
    components: {
        chooseOrgTab
    },
    data() {
        return {
            chooseOrgMd: {
                title: `厂商选择`,
                alert: false
            },
            orgName: '',
            formData: {
                specification: '', // 规格型号
                product_name: '', // 产品名称
                product_vendor_inner_sn: '', // 厂商ID
                isNew: true // 规格型号
            },
            columns: [
                {
                    title: 'NO.',
                    type: 'seq',
                    width: '60',
                    fixed: 'left'
                },
                {
                    title: '产品.',
                    field: 'product_name',
                    width: '250'
                },
                {
                    title: '规格型号',
                    field: 'specification',
                    width: '150'
                },
                {
                    title: '厂商',
                    field: 'product_vendor_name',
                    width: '150'
                },
                {
                    title: '包装单位',
                    field: 'unit_name',
                    width: '100'
                },
                // {
                //     title: '单价',
                //     field: 'unit_price',
                //     width: '100'
                // },
                // {
                //     title: '折扣',
                //     field: 'discount',
                //     width: '100'
                // },
                // {
                //     title: '折后单价',
                //     field: 'discounted_price',
                //     width: '100'
                // },
                {
                    title: '税率',
                    field: 'rate',
                    width: '100',
                    slots: {
                        default: ({ row }) => {
                            const value = parseFloat(row.rate) * 100 + '%';
                            return [value];
                        }
                    }
                },
                {
                    title: '含税单价',
                    field: 'rate_price',
                    width: '100'
                },
                {
                    title: '报价机构',
                    field: 'organization_name',
                    width: '200'
                },
                {
                    title: '联系人',
                    field: 'contacts',
                    width: '100'
                },
                {
                    title: '电话',
                    field: 'phone',
                    width: '150'
                },
                {
                    title: '回复时间',
                    field: 'ask_time',
                    width: '200'
                },
                {
                    title: '询价单号',
                    field: 'ilps_sn',
                    width: '200'
                }
            ],
            proTableData: [],
            tableLoading: false
        };
    },
    methods: {
        handleSearch() {
            if (!this.formData.product_name) {
                this.$message.warning('请输入产品名称');
                return;
            }

            this.fetchTableData();
        },
        fetchTableData() {
            this.tableLoading = true;
            const params = {
                ...this.formData,
                org_id: this.$cookie.get('userbelong')
            };
            inquiryPriceListAPI({ params })
                .then((res) => {
                    if (res.code === 0) {
                        const { list } = res;
                        this.proTableData = list.map((item) => {
                            item.rate_price = item.unit_price.toFixed(4);
                            // item.rate_price = this.$XEUtils.commafy(item.unit_price, {fixed: 4})
                            return {
                                ...item,
                                rate_price: item.rate_price,
                                unit_price: (parseFloat(item.rate_price) / (1 + parseFloat(item.rate))).toFixed(4) // 单价 = 含税单价 / (1 + 税率 / 100)
                                // unit_price: this.$XEUtils.commafy(parseFloat(item.rate_price) / (1 + parseFloat(item.rate)), {fixed: 4}) // 单价 = 含税单价 / (1 + 税率 / 100)
                            };
                        });
                    }
                })
                .finally(() => {
                    this.tableLoading = false;
                });
        },
        handleRadioChange(e) {
            this.formData.isNew = e.target.value;
        },
        openChooseTb() {
            // 选择厂商
            this.chooseOrgMd.alert = true;
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
        },
        chooseOrgFn(org) {
            // 获取选中厂商信息
            this.orgName = org.org_name;
            this.formData.product_vendor_inner_sn = org.org_id;
            this.chooseOrgMd.alert = false;
        },
        onChangeOrg(e) {
            if (!e.target.value) this.formData.product_vendor_inner_sn = '';
        }
    }
};
</script>

<style scoped lang="less">
.del_ico {
    color: red;
    font-size: 16px;
}

.head_box {
    .tit_box {
        text-align: center;
        font-size: 30px;
        margin-top: 9px;
    }
}

.button_cont {
    position: absolute;
    width: 100%;
    bottom: 25px;
}

.search-item {
    display: inline-block;
    vertical-align: top;

    &.num {
        position: absolute;
        right: 0px;
        top: 76px;
    }

    &.digest {
        position: absolute;
        left: 24px;
        bottom: 70px;
    }

    .ant-cascader-input.ant-input {
        height: 24px !important;
    }
}

.label {
    display: inline-block;
    min-width: 40px;
    text-align: right;
    margin-right: 10px;

    .em {
        line-height: 14px;
        font-size: 21px;
        vertical-align: middle;
        padding-right: 4px;
    }
}

.input_box {
    display: inline-block;
    width: 175px;
    margin-right: 30px;

    &.isNew {
        width: 200px;
    }

    .ant-input {
        line-height: 24px;
        height: 24px;
    }
}

.search-box {
    margin-top: 15px;
    margin-bottom: 15px;
    padding: 0 22px;
}
</style>
