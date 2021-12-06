<template>
    <!-- 相关机构 -->
    <div class="form-header">
        <a-row :gutter="24" class="form-row">
            <a-col :span="6" class="form-col">
                <span class="form-label">
                    <span v-if="requiredItemMap.organization" class="red asterisk">*</span>
                    <span class="form-text">相关机构</span>
                </span>
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
            <a-col :span="6" class="form-col">
                <span class="form-label">
                    <span v-if="requiredItemMap.handler" class="red asterisk">*</span>
                    <span class="form-text">经手人</span>
                </span>
                <!-- a-tree-select组件要求value值类型不能是number，所以转为string -->
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
                    :value="String(formData.handler_inner_sn)"
                    @change="changeHandlerNameFn"
                    :disabled="isReadonly || isFinanceInvoiceOdoReadonly"
                >
                </a-tree-select>
            </a-col>
            <a-col :span="6" class="form-col">
                <span class="form-label">
                    <span v-if="requiredItemMap.department" class="red asterisk">*</span>
                    <span class="form-text">部门</span>
                </span>
                <!-- a-tree-select组件要求value值类型不能是number，所以转为string -->
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
                    :value="String(formData.department_inner_sn)"
                    @change="changeDepartmentNameFn"
                    :disabled="isReadonly || isFinanceInvoiceOdoReadonly"
                >
                </a-tree-select>
            </a-col>
            <a-col :span="6" class="form-col">
                <span class="form-label">
                    <span v-if="requiredItemMap.invoiceType" class="red asterisk">*</span>
                    <span class="form-text">发票类型</span>
                </span>
                <a-tree-select
                    treeNodeFilterProp="title"
                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                    placeholder=""
                    treeDefaultExpandAll
                    class="form-input"
                    size="small"
                    :treeData="invoiceTypeData"
                    v-model="formData.invoice_type"
                    :disabled="isReadonly && !editableItemMap.invoiceType"
                >
                </a-tree-select>
            </a-col>
        </a-row>
        <a-row :gutter="24" class="form-row">
            <a-col :span="6" class="form-col">
                <span class="form-label">
                    <span v-if="requiredItemMap.invoicePrice" class="red asterisk">*</span>
                    <span class="form-text">开票金额</span>
                </span>
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
            <a-col :span="6" class="form-col">
                <span class="form-label">
                    <span v-if="requiredItemMap.invoiceNo" class="red asterisk">*</span>
                    <span class="form-text">发票号</span>
                </span>
                <a-input
                    size="small"
                    class="form-input"
                    v-model="formData.invoice_no"
                    :disabled="isReadonly && !editableItemMap.invoiceNo"
                />
            </a-col>
            <a-col :span="6" class="form-col">
                <span class="form-label">
                    <span v-if="requiredItemMap.invoiceDate" class="red asterisk">*</span>
                    <span class="form-text">开票日期</span>
                </span>
                <a-date-picker
                    :placeholder="''"
                    size="small"
                    class="form-input"
                    :allowClear="false"
                    v-model="formData.invoice_date"
                    :disabled="isReadonly && !editableItemMap.invoiceDate"
                >
                </a-date-picker>
            </a-col>
            <a-col :span="6" class="form-col">
                <span class="form-label">
                    <span v-if="requiredItemMap.rate" class="red asterisk">*</span>
                    <span class="form-text">发票税率</span>
                </span>
                <a-tree-select
                    treeNodeFilterProp="title"
                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                    placeholder=""
                    treeDefaultExpandAll
                    class="form-input"
                    size="small"
                    :treeData="rateData"
                    v-model="formData.rate"
                    :disabled="isReadonly && !editableItemMap.rate"
                >
                    <span slot="suffixIcon">%</span>
                </a-tree-select>
            </a-col>
        </a-row>

        <!-- 相关机构区域 -->
        <choose-org-tab ref="organization" :isInbound="isInbound" @getOrgData="getOrgDataFn"></choose-org-tab>
    </div>
</template>

<script>
import employeesMixin from '@/mixins/employees';
import { invoiceTypeMap, rateData } from '@/views/commonPage/invoiceManage/config/constants';
import chooseOrgTab from '@/views/commonPage/invoiceManage/components/organization';
import deaprtmentMixins from '@/views/commonPage/invoiceManage/mixins/department';

export default {
    name: 'invoiceHeader', // 入库|出库发票的模态框上部分表单
    props: {
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
        },
        isFinanceInvoiceOdoReadonly: {
            // 是否是财务人员使用的出库发票，如果是将相关机构、经手人和部门和产品明细设置为 只读
            type: Boolean,
            default: false
        },

        /* 只读时（isReadonly = true），额外的可以编辑的项 */
        // example:
        // {
        //     invoiceType: false,
        //     invoiceNo: false,
        //     invoiceDate: false,
        //     rate: false
        // }
        editableItemMap: {
            type: Object,
            default: () => ({})
        },

        /* 必填的项 */
        // example:
        // {
        //     organization: false,
        //     handler: false,
        //     department: false,
        //     invoiceType: false,
        //     invoicePrice: false,
        //     invoiceNo: false,
        //     invoiceDate: false,
        //     rate: false
        // }
        requiredItemMap: {
            type: Object,
            default: () => ({})
        }
    },
    components: {
        chooseOrgTab
    },
    mixins: [deaprtmentMixins, employeesMixin],
    data() {
        return {
            rateData
        };
    },
    methods: {
        changeHandlerNameFn(value, label) {
            this.formData.handler_inner_sn = value;
            if (label && label.length) {
                this.formData.handler_name = label[0];
            } else {
                this.formData.handler_name = '';
            }
        },
        changeDepartmentNameFn(value, label) {
            this.formData.department_inner_sn = value;
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
        // 发票类型
        invoiceTypeData() {
            let invoiceTypeTempData = [];
            Object.keys(invoiceTypeMap).forEach((key) => {
                invoiceTypeTempData.push({
                    title: invoiceTypeMap[key].text,
                    key: invoiceTypeMap[key].value,
                    value: invoiceTypeMap[key].value
                });
            });
            return invoiceTypeTempData;
        }
    },
    created() {
        this.handleInitEmployeesMixin();
    }
};
</script>

<style lang="less" scoped></style>
