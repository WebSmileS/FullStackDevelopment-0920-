<template>
    <a-modal
        :visible="visible"
        :width="900"
        okText="查询"
        cancelText="重置"
        :maskClosable="false"
        :centered="true"
        @ok="handleSubmit"
        @cancel="handleCancel"
        class="modal-form-input-scoped global-drag finance-bound-modal"
    >
        <div v-globalDrag="{ el: 'finance-bound-modal' }" class="modal-header-wrap">
            <h6 style="text-align: center">请输入需要查询的信息内容</h6>
        </div>
        <a-form
            v-if="bizType === 'stock'"
            class="search-modal-form"
            layout="horizontal"
            :colon="false"
            :form="form"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 18 }"
        >
            <a-row class="btn-top-wrap" :gutter="16">
                <a-col :span="12">
                    <a-form-item label="仓库名称">
                        <a-tree-select
                            v-decorator="['warehouse_model_inner_sn']"
                            treeNodeFilterProp="title"
                            showSearch
                            :style="{ marginTop: '4px' }"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            :treeData="warehouseTreeData"
                            placeholder="请选择"
                            treeDefaultExpandAll
                            allowClear
                            @change="handleWarehouseNameChange"
                        >
                        </a-tree-select>
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item label="仓库人员">
                        <a-tree-select
                            v-decorator="['employee_inner_sn']"
                            treeNodeFilterProp="title"
                            showSearch
                            style="margin-top: 4px"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            :treeData="warehouseOperatorTreeData"
                            placeholder="请选择"
                            treeDefaultExpandAll
                            allowClear
                        >
                        </a-tree-select>
                    </a-form-item>
                </a-col>
                <a-col :span="24">
                    <a-form-item :label-col="{ span: 3 }" :wrapper-col="{ span: 21 }" label="产品名称">
                        <a-input v-decorator="['product_name']" placeholder="请输入" autocomplete="off" />
                    </a-form-item>
                </a-col>
            </a-row>
        </a-form>
        <a-form
            v-else
            class="search-modal-form"
            layout="horizontal"
            :form="form"
            :label-col="{ span: 8 }"
            :wrapper-col="{ span: 16 }"
        >
            <a-row class="btn-top-wrap" :gutter="16">
                <a-col :span="8">
                    <a-form-item :label="bizType === 'inbound' ? '供货单位' : '单位名称'">
                        <a-input v-decorator="['organization_name']" placeholder="请输入" autocomplete="off" />
                    </a-form-item>
                </a-col>
                <a-col :span="8">
                    <a-form-item label="部门">
                        <a-tree-select
                            treeNodeFilterProp="title"
                            v-decorator="['department_inner_sn']"
                            showSearch
                            :treeData="departmentTreeData"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            allowClear
                            class="input"
                            style="margin-top: 4px; min-width: 120px; height: 32px"
                        >
                        </a-tree-select>
                    </a-form-item>
                </a-col>
                <a-col :span="8">
                    <a-form-item label="仓库名称">
                        <a-tree-select
                            v-decorator="['warehouse_model_inner_sn']"
                            treeNodeFilterProp="title"
                            showSearch
                            :style="{ marginTop: '4px' }"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            :treeData="warehouseTreeData"
                            placeholder="请选择"
                            treeDefaultExpandAll
                            allowClear
                        >
                        </a-tree-select>
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row class="btn-top-wrap" :gutter="16">
                <a-col :span="8">
                    <a-form-item label="产品名称">
                        <a-input v-decorator="['product_name']" placeholder="请输入" autocomplete="off" />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item
                        :label-col="{ span: 6 }"
                        :wrapper-col="{ span: 18 }"
                        :label="bizType === 'inbound' ? '入库日期' : '出库日期'"
                    >
                        <a-range-picker style="width: 100%" v-decorator="['range-date']" format="YYYY-MM-DD" />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row class="btn-top-wrap" :gutter="16">
                <a-col :span="8">
                    <a-form-item label="生产厂家">
                        <product-vender-input
                            allowClear
                            placeholder="请选择"
                            v-decorator="['product_vendor_name']"
                            @click="handleClickProductVender"
                            @clear="handleClearProductVender"
                        />
                    </a-form-item>
                    <div style="display: none">
                        <a-form-item label="生产厂家id（hidden）">
                            <a-input v-decorator="['product_vendor_inner_sn']" autocomplete="off" />
                        </a-form-item>
                        <a-form-item label="生产厂家类型（hidden）">
                            <a-input v-decorator="['product_type']" autocomplete="off" />
                        </a-form-item>
                    </div>
                </a-col>
                <a-col :span="8">
                    <a-form-item label="经手人">
                        <a-input v-decorator="['handler_name']" placeholder="请输入" autocomplete="off" />
                    </a-form-item>
                </a-col>
                <a-col :span="8">
                    <a-form-item label="单据编号">
                        <a-input v-decorator="['sn']" placeholder="请输入" autocomplete="off" />
                    </a-form-item>
                </a-col>
            </a-row>
        </a-form>
    </a-modal>
</template>

<script>
import { financeWeListAPI } from '@/service/pageAjax';
import ProductVenderInput from './productVenderInput';
export default {
    name: 'SearchModal',

    props: {
        visible: {
            type: Boolean,
            default: false
        },
        selectedOrg: Object,
        bizType: String,
        warehouseTreeData: Array,
        initialWarehouseOperatorTreeData: Array,
        allWarehouseOperatorTreeData: Array,
        departmentTreeData: Array
    },

    data() {
        return {
            form: this.$form.createForm(this, { name: 'search_modal_form' }),
            warehouseOperatorTreeData: []
        };
    },

    watch: {
        'initialWarehouseOperatorTreeData': function (val) {
            this.warehouseOperatorTreeData = val;
        },
        'selectedOrg': function (val) {
            this.form.setFieldsValue({
                product_vendor_inner_sn: val.id,
                product_vendor_name: val.name,
                product_type: val.type
            });
        }
    },

    methods: {
        handleSubmit() {
            this.$emit('onsubmit', this.form.getFieldsValue(), {
                warehouseOperatorTreeData: this.warehouseOperatorTreeData
            });
        },

        handleCancel() {
            this.form.resetFields();
            this.$emit('oncancel');
        },

        // 仓库名称change
        handleWarehouseNameChange(wId) {
            this.form.resetFields(['employee_inner_sn']);
            this.getWarehouseOperators(wId);
        },

        // 获取仓库人员
        async getWarehouseOperators(wid) {
            // 获取全量仓库人员
            if (!wid) {
                this.warehouseOperatorTreeData = this.allWarehouseOperatorTreeData;
                return;
            }
            try {
                // 获取指定仓库人员
                const { code, info = [] } = await financeWeListAPI({ params: { wid } });
                if (code === 0) {
                    this.warehouseOperatorTreeData = info.map((item) => ({
                        ...item,
                        title: item.employee_name,
                        value: item.employee_inner_sn,
                        key: item.employee_inner_sn
                    }));
                }
            } catch (error) {
                console.error(error);
            }
        },

        handleClickProductVender() {
            this.$emit('onopenchooseorg');
        },

        handleClearProductVender() {
            this.form.resetFields(['product_vendor_inner_sn', 'product_vendor_name', 'product_type']);
        }
    },

    components: {
        ProductVenderInput
    }
};
</script>

<style lang="less">
.finance-bound-modal {
    .search-modal-form {
        margin-top: 50px;
        .ant-form-item {
            .ant-form-item-label {
                line-height: 40px !important;
                width: 25% !important;
            }
            .ant-form-item-control-wrapper {
                width: 75% !important;
                display: inline-block !important;
            }
        }
        .ant-select-selection {
            box-sizing: border-box;
            height: 32px !important;
            padding: 4px 11px;
        }
    }
    .ant-modal-title {
        text-align: center;
        font-size: 18px;
    }
}
</style>
