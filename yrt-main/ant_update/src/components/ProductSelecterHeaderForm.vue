<!-- 
* @Author: 
* @Description: 产品选择组件的顶部查询表单；表单值更新时将触发change事件（携带FormValues值），用户点击查询按钮时将触发search事件（携带FormValues值）
* customFormItem插槽示例：
*     <template v-slot:customFormItem="{ value, change }">
*         <span class="label">自定义标签：</span>
*         <your-component :value="value" @change="(value) => change(value)"></your-component>
*     </template>
-->
<template>
    <div>
        <editor-form-header>
            <editor-form-header-item>
                <span class="label">生产厂商：</span>
                <base-input-with-clear-button
                    placeholder="生产厂商"
                    size="small"
                    :value="formValues.productVendorName"
                    @click="organizationSelecterVisible = true"
                    @clear="onOrganizationClear"
                ></base-input-with-clear-button>
            </editor-form-header-item>
            <editor-form-header-item>
                <span class="label">产品名称：</span>
                <a-input placeholder="产品名称" size="small" v-model="formValues.productName"></a-input>
            </editor-form-header-item>
            <editor-form-header-item>
                <span class="label">规格型号：</span>
                <a-input placeholder="规格型号" size="small" v-model="formValues.specification"></a-input>
            </editor-form-header-item>
            <editor-form-header-item>
                <span class="label">产品编码：</span>
                <a-input placeholder="产品编码" size="small" v-model="formValues.specificationVendorSn"></a-input>
            </editor-form-header-item>
        </editor-form-header>
        <editor-form-header style="margin-top: 12px">
            <editor-form-header-item>
                <span class="label">产品分类：</span>
                <category-68-selecter
                    :value="formValues.productType"
                    @change="(value) => (formValues.productType = value)"
                ></category-68-selecter>
            </editor-form-header-item>
            <editor-form-header-item>
                <span class="label">自定义类型：</span>
                <category-custom-selecter
                    :value="formValues.productCustomType"
                    @change="(value) => (formValues.productCustomType = value)"
                ></category-custom-selecter>
            </editor-form-header-item>
            <editor-form-header-item v-if="$scopedSlots.customFormItem">
                <slot
                    name="customFormItem"
                    :value="formValues.customFormItemValue"
                    :change="onCustomFormItemChange"
                ></slot>
            </editor-form-header-item>
            <editor-form-header-item v-else>
                <span class="label">合同内产品：</span>
                <a-checkbox :checked="formValues.isContractProduct" @change="onContractCheckboxChange"></a-checkbox>
            </editor-form-header-item>
            <editor-form-header-item>
                <a-button size="small" type="primary" @click="handleSearch">查询</a-button>
            </editor-form-header-item>
        </editor-form-header>
        <organization-selecter
            title="选择厂商"
            destroyOnClose
            organizationType="ven"
            :visible="organizationSelecterVisible"
            @close="organizationSelecterVisible = false"
            @submit="onOrganizationSelected"
        ></organization-selecter>
    </div>
</template>

<script>
/**
 * @typedef FormValues - 表单值
 * @type {Object}
 * @property {string | undefined}              productName - 产品名称
 * @property {string | undefined}              productVendorName - 厂商名称
 * @property {string | number | undefined}     productVendorInnerSn - 厂商id
 * @property {string | undefined}              specification - 规格型号
 * @property {string | number | undefined}     specificationVendorSn - 规格型号id
 * @property {string[] | number[] | undefined} productType - 产品分类（68分类）
 * @property {string[] | number[] | undefined} productCustomType - 产品自定义分类
 * @property {boolean}                         isContractProduct - 合同内产品
 * @property {any}                             customFormItemValue - 自定义表单项的值；如果使用了自定义表单项插槽（customFormItem），合同内产品的表单项将被取代
 */
import EditorFormHeader from '@/components/EditorFormHeader';
import EditorFormHeaderItem from '@/components/EditorFormHeaderItem';
import BaseInputWithClearButton from '@/components/BaseInputWithClearButton';
import OrganizationSelecter from '@/components/OrganizationSelecter';
import CategoryCustomSelecter from '@/components/CategoryCustomSelecter';
import Category68Selecter from '@/components/Category68Selecter';

/** @type {FormValues} */
const _defaultFormValues = {
    productName: undefined,
    productVendorName: undefined,
    productVendorInnerSn: undefined,
    specification: undefined,
    specificationVendorSn: undefined,
    productType: undefined,
    productCustomType: undefined,
    isContractProduct: false,
    customFormItemValue: undefined
};

export default {
    name: 'ProductSelecterHeaderForm',
    components: {
        EditorFormHeader,
        EditorFormHeaderItem,
        OrganizationSelecter,
        CategoryCustomSelecter,
        Category68Selecter,
        BaseInputWithClearButton
    },
    props: {
        /**
         * 表单初始值
         */
        defaultFormValues: {
            type: Object
        }
    },
    data() {
        /** @type {FormValues} */
        let formValues;
        if (this.$props.defaultFormValues) {
            formValues = { ..._defaultFormValues, ...this.$props.defaultFormValues };
        } else {
            formValues = { ..._defaultFormValues };
        }
        return {
            formValues,
            organizationSelecterVisible: false
        };
    },
    watch: {
        formValues: {
            handler: function (newValue) {
                this.$emit('change', { ...newValue });
            },
            deep: true
        }
    },
    methods: {
        onOrganizationSelected(organizations) {
            if (organizations[0]) {
                this.formValues.productVendorName = organizations[0].organizationName;
                this.formValues.productVendorInnerSn = organizations[0].organizationInnerSn;
            }
            this.organizationSelecterVisible = false;
        },
        onOrganizationClear() {
            this.formValues.productVendorName = undefined;
            this.formValues.productVendorInnerSn = undefined;
        },
        onContractCheckboxChange(event) {
            this.formValues.isContractProduct = event.target.checked;
        },
        onCustomFormItemChange(value) {
            this.formValues.customFormItemValue = value;
        },
        handleSearch() {
            this.$emit('search', { ...this.formValues });
        },
        handleInit() {
            this.formValues = { ..._defaultFormValues, ...this.defaultFormValues };
        }
    }
};
</script>
