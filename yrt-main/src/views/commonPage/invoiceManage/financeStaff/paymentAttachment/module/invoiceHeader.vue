<template>
    <!-- 相关机构 -->
    <div class="form-header">
        <a-row :gutter="24" class="form-row">
            <a-col :span="6" class="form-col">
                <span class="form-label"
                    ><span class="red asterisk">*</span><span class="form-text">相关机构</span></span
                >
                <a-input
                    type="text"
                    placeholder=""
                    class="form-input"
                    size="small"
                    @click="openChooseTab"
                    v-model="formData.organization_name"
                    :disabled="isReadonly || btnName === 'edit'"
                />
            </a-col>
            <a-col :span="6" class="form-col">
                <span class="form-label"
                    ><span class="red asterisk">*</span
                    ><span class="form-text">{{ paymentReceiveLabel }}方式</span></span
                >
                <a-tree-select
                    treeNodeFilterProp="title"
                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                    placeholder=""
                    treeDefaultExpandAll
                    class="form-input"
                    size="small"
                    :treeData="paymentMethodData"
                    v-model="formData.invoice_type"
                    :disabled="isReadonly"
                >
                </a-tree-select>
            </a-col>
            <a-col :span="6" class="form-col">
                <span class="form-label"
                    ><span class="form-text">{{ paymentReceiveLabel }}金额</span></span
                >
                <a-input-number size="small" :min="0" class="form-input" v-model="formData.invoice_price" disabled />
            </a-col>
            <a-col :span="6" class="form-col">
                <span class="form-label"
                    ><span class="red asterisk">*</span
                    ><span class="form-text">{{ paymentReceiveLabel }}日期</span></span
                >
                <a-date-picker
                    :placeholder="['']"
                    size="small"
                    class="form-input"
                    :allowClear="false"
                    v-model="formData.invoice_date"
                    :disabled="isReadonly"
                >
                </a-date-picker>
            </a-col>
        </a-row>

        <!-- 相关机构区域 -->
        <choose-org-tab ref="organization" :isInbound="isInbound" @getOrgData="getOrgDataFn"></choose-org-tab>
    </div>
</template>

<script>
import chooseOrgTab from '@/views/commonPage/invoiceManage/components/organization';
import { paymentMethod } from '@/views/commonPage/invoiceManage/config/constants';

export default {
    name: 'invoiceHeader', // 入库|出库发票的模态框上部分表单
    props: {
        btnName: {
            type: String,
            default: ''
        },
        isInbound: {
            type: Boolean,
            default: true
        },
        formData: {
            type: Object,
            required: true
        },
        isReadonly: {
            // 是否只读
            type: Boolean,
            default: false
        }
    },
    components: {
        chooseOrgTab
    },
    methods: {
        // 相关机构
        openChooseTab() {
            this.$nextTick(() => {
                if (this.$refs.organization) {
                    this.$refs.organization.initPageFn();
                }
            });
        },
        getOrgDataFn(orgData) {
            const {
                organization_name: organizationName,
                organization_inner_sn: organizationInnerSn,
                billsType
            } = orgData;
            this.formData.organization_name = organizationName;
            this.formData.organization_inner_sn = organizationInnerSn;
            this.formData.bills_type = billsType;
        }
    },
    computed: {
        // 付款方式 | 收款方式
        paymentMethodData() {
            let paymentMethodTempData = [];
            for (let key in paymentMethod) {
                paymentMethodTempData.push({
                    title: paymentMethod[key],
                    key: key,
                    value: key
                });
            }
            return paymentMethodTempData;
        },
        paymentReceiveLabel() {
            return this.isInbound ? '付款' : '收款';
        }
    }
};
</script>

<style lang="less" scoped></style>
