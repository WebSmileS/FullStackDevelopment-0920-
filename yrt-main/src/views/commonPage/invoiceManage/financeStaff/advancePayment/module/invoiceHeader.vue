<template>
    <!-- 相关机构 -->
    <div class="form-header">
        <a-row :gutter="24" class="form-row">
            <a-col :span="8" class="form-col">
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
                    :disabled="isReadonly || isFinanceInvoiceOdoReadonly"
                />
            </a-col>
            <a-col :span="8" class="form-col">
                <span class="form-label"><span class="form-text">经手人</span></span>
                <a-tree-select
                    treeNodeFilterProp="title"
                    :showSearch="true"
                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                    placeholder=""
                    treeDefaultExpandAll
                    allowClear
                    class="form-input"
                    size="small"
                    :treeData="employeesData"
                    v-model="formData.handler_inner_sn"
                    @change="changeHandlerNameFn"
                    :disabled="isReadonly || isFinanceInvoiceOdoReadonly"
                >
                </a-tree-select>
            </a-col>
            <a-col :span="8" class="form-col">
                <span class="form-label"><span class="form-text">部门</span></span>
                <a-tree-select
                    treeNodeFilterProp="title"
                    :showSearch="true"
                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                    placeholder=""
                    treeDefaultExpandAll
                    allowClear
                    class="form-input"
                    size="small"
                    :treeData="departmentsData"
                    v-model="formData.department_inner_sn"
                    @change="changeDepartmentNameFn"
                    :disabled="isReadonly || isFinanceInvoiceOdoReadonly"
                >
                </a-tree-select>
            </a-col>
        </a-row>
        <a-row :gutter="24" class="form-row">
            <a-col :span="8" class="form-col">
                <span class="form-label"
                    ><span class="red asterisk">*</span><span class="form-text">付款金额</span></span
                >
                <a-input-number
                    size="small"
                    :min="0"
                    class="form-input"
                    v-model="formData.invoice_price"
                    :formatter="(value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3')"
                    :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3')"
                    :disabled="isReadonly"
                />
            </a-col>
            <a-col :span="8" class="form-col">
                <span class="form-label"
                    ><span class="red asterisk">*</span><span class="form-text">付款方式</span></span
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
            <a-col :span="8" class="form-col">
                <span class="form-label"
                    ><span class="red asterisk">*</span><span class="form-text">付款日期</span></span
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
import employeesMixin from '@/mixins/employees';
import chooseOrgTab from '@/views/commonPage/invoiceManage/components/organization';
import { paymentMethod } from '@/views/commonPage/invoiceManage/config/constants';
import deaprtmentMixins from '@/views/commonPage/invoiceManage/mixins/department';

export default {
    name: 'invoiceHeader', // 预付款的模态框上部分表单
    props: {
        formData: {
            type: Object,
            required: true
        },
        isReadonly: {
            // 是否只读
            type: Boolean,
            default: false
        },
        isFinanceInvoiceOdoReadonly: {
            // 是否是财务人员使用的预付款，如果是将相关机构、经手人和部门和产品明细设置为 只读
            type: Boolean,
            default: false
        }
    },
    components: {
        chooseOrgTab
    },
    mixins: [deaprtmentMixins, employeesMixin],
    methods: {
        changeHandlerNameFn(_, label) {
            if (label && label.length) {
                this.formData.handler_name = label[0];
            } else {
                this.formData.handler_name = '';
            }
        },
        changeDepartmentNameFn(_, label) {
            if (label && label.length) {
                this.formData.department_name = label[0];
            } else {
                this.formData.department_name = '';
            }
        },
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
        }
    },
    created() {
        this.handleInitEmployeesMixin();
    }
};
</script>

<style lang="less" scoped></style>
