<template>
    <base-modal dragKey="barcode_editor_md" title="新增标签" :width="1100" :visible="visible" @close="handleClose">
        <template v-slot:content>
            <div style="margin-top: 24px" @dblclick="handleSelectProducts">
                <vxe-table
                    border
                    show-overflow
                    show-header-overflow
                    show-footer
                    resizable
                    ref="xTable"
                    size="small"
                    height="400"
                    :data="dataEditingDetails"
                    :mouse-config="{ selected: true }"
                    :checkbox-config="{ range: true }"
                    :keyboard-config="{
                        isArrow: true,
                        isDel: true,
                        isEnter: true,
                        isTab: true,
                        isEdit: true
                    }"
                    :edit-config="{ trigger: 'click', mode: 'cell' }"
                    :edit-rules="{
                        'udi_barcode': [{ type: 'string', message: '标签不能为空' }]
                    }"
                    :footer-method="() => [[]]"
                    @cell-dblclick="({ $event }) => $event.stopPropagation()"
                >
                    <template slot="empty">
                        <div>双击空白区域添加产品</div>
                    </template>
                    <vxe-table-column v-bind="tableColumnConfig.seq"></vxe-table-column>
                    <vxe-table-column
                        v-for="column in productColumns"
                        v-bind="column"
                        :key="column.field"
                    ></vxe-table-column>
                    <vxe-table-column v-bind="tableColumnConfig.status">
                        <template v-slot="{ row }">
                            <span class="orange" v-if="row.status === 0">已禁用</span>
                            <span class="green" v-if="row.status === 1">可用</span>
                        </template>
                    </vxe-table-column>
                    <vxe-table-column
                        v-bind="tableColumnConfig.tag"
                        field="udi_barcode"
                        :edit-render="{ autofocus: '.ant-input', autoselect: true }"
                    >
                        <template v-slot:edit="params">
                            <a-input
                                size="small"
                                :defaultValue="params.row[params.column.property]"
                                @pressEnter="(e) => onBarcodeInputFinish(e, params)"
                                @blur="(e) => onBarcodeInputBlur(e, params)"
                            ></a-input>
                        </template>
                    </vxe-table-column>
                    <vxe-table-column v-bind="tableColumnConfig.operation">
                        <template v-slot:default="{ rowIndex }">
                            <table-row-operator
                                copy
                                del
                                :payload="rowIndex"
                                @copy="handleCopyProduct"
                                @del="handleRemoveProduct"
                            ></table-row-operator>
                        </template>
                        <template slot="footer">
                            <div style="overflow: hidden">
                                <a-button size="small" type="primary" @click="handleSelectProducts">添加产品</a-button>
                            </div>
                        </template>
                    </vxe-table-column>
                </vxe-table>
            </div>
            <product-selecter-modal
                destroyOnClose
                :visible="productSelecterVisible"
                :tabs="productSelecterTabs"
                @close="handleCloseProductSelecter"
            >
                <product-selecter
                    slot="vendorProduct"
                    @submit="onProductAdd"
                    @close="handleCloseProductSelecter"
                ></product-selecter>
            </product-selecter-modal>
        </template>
        <template slot="footer">
            <a-button type="default" @click="handleClose">取消</a-button>
            <a-button type="primary" @click="onAddButtonClick" :loading="buttonsLoading.add">批量添加</a-button>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal';
import ProductSelecterModal from '@/components/ProductSelecterModal';
import TableRowOperator from '@/components/TableRowOperator';
import tableColumnConfig from '@/config/tableColumnConfig.js';
import { barcodesAddAPI } from '@/service/pageAjax';
import ProductSelecter from './ProductSelecter';
import productColumns from './barcodeEditorColumns.js';

const productProperties = [
    'product_name',
    'product_inner_sn',
    'vendor_name',
    'vendor_inner_sn',
    'specification',
    'specification_inner_sn',
    'udi_barcode',
    'status'
];

export default {
    name: 'BarcodeEditor',
    components: {
        BaseModal,
        ProductSelecterModal,
        ProductSelecter,
        TableRowOperator
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            tableColumnConfig,
            productSelecterVisible: false,
            productColumns: Object.freeze(productColumns),
            dataEditingDetails: [],
            buttonsLoading: {
                add: false
            }
        };
    },
    computed: {
        productSelecterTabs() {
            return [{ title: '厂商产品', key: 'vendorProduct' }];
        },
        organizationInnerSn() {
            return this.$cookie.get('userbelong');
        }
    },
    watch: {
        visible(newValue) {
            if (newValue === false) {
                this.handleInit();
            }
        }
    },
    methods: {
        onBarcodeInputFinish(event, { $table, row, rowIndex, column }) {
            event.stopPropagation();
            this.$set(row, column.property, event.currentTarget.value);
            if (this.dataEditingDetails[rowIndex + 1]) {
                $table.setActiveCell(this.dataEditingDetails[rowIndex + 1], column.property);
                return;
            }
            $table.clearActived();
            $table.setSelectCell(this.dataEditingDetails[rowIndex], column.property);
        },
        onBarcodeInputBlur(event, { row, column }) {
            if (row[column.property] !== event.currentTarget.value) {
                this.$set(row, column.property, event.currentTarget.value);
            }
        },
        async onAddButtonClick() {
            this.buttonsLoading.add = true;
            try {
                const data = this.handlePreparePostData();
                const { code, msg } = await barcodesAddAPI(data);
                if (code !== 0) {
                    throw new Error(msg || '添加失败');
                }
                this.$message.success('添加成功');
                this.$emit('addFinish', data);
            } catch (err) {
                this.$message.error(err.message);
            } finally {
                this.buttonsLoading.add = false;
            }
        },

        /**
         * 产品选择返回值时
         * @param {array} products 产品数据
         */
        onProductAdd(products) {
            // 平台产品 - 厂商产品
            if (products.length) {
                products.forEach((product) => {
                    this.dataEditingDetails.push(this.$XEUtils.pick(product, productProperties));
                });
            }
            this.handleCloseProductSelecter();
        },

        /**
         * 初始化编辑器
         * targetOrder为空时，将清空数据
         * @param {object | undefined} targetOrder 编辑的目标单据
         */
        handleInit() {
            this.dataEditingDetails = [];
            this.buttonsLoading.add = false;
        },

        /**
         * 关闭编辑器
         */
        handleClose() {
            this.$emit('close');
        },

        /**
         * 数据校验
         */
        handleCheckData(data) {
            // 产品数据校验
            if (!data.length) {
                this.$message.error('产品不能为空');
                throw new Error('details');
            }
            data.forEach((product) => {
                if (!product.udi_barcode) {
                    this.$message.error(`标签不能为空（${product.product_name}）`);
                    throw new Error('udi_barcode');
                }
            });
        },

        /**
         * 生成提交数据
         * @param {boolean} dataCheck 是否需要校验数据
         */
        handlePreparePostData(dataCheck = true) {
            const data = this.dataEditingDetails.map((product) => this.$XEUtils.pick(product, productProperties));
            if (dataCheck) {
                this.handleCheckData(data);
            }
            return data;
        },

        handleSelectProducts() {
            this.productSelecterVisible = true;
        },

        handleCloseProductSelecter() {
            this.productSelecterVisible = false;
        },

        /**
         * 复制表格内的一个产品
         * @param {number} index
         */
        handleCopyProduct(index) {
            const newProduct = { ...this.dataEditingDetails[index] };
            delete newProduct._XID;
            this.dataEditingDetails.splice(index + 1, 0, newProduct);
        },

        /**
         * 移除表格内的一条记录
         * @param {number} index
         */
        handleRemoveProduct(index) {
            this.dataEditingDetails.splice(index, 1);
        }
    }
};
</script>
