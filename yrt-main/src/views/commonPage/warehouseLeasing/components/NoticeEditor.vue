<template>
    <base-modal
        :title="title"
        dragKey="notice_editor_md"
        destroyOnClose
        :visible="visible"
        :width="width"
        :height="height"
        :onClose="onClose"
    >
        <template slot="content">
            <h6 style="margin-top: 24px; margin: 24px 0; text-align: center; font-size: 24px; font-weight: 500">{{
                title
            }}</h6>
            <div class="notice-editor-form-wrapper">
                <div class="notice-editor-form-item">
                    <span class="label"><em class="em red">*</em>租户</span>
                    <a-input size="small" :value="lessee['dealer_name']" @click="handleOpenModals('lesseeSelecter')" />
                </div>
                <div class="notice-editor-form-item">
                    <span class="label"
                        ><em class="em red">*</em>{{ noticeType === 'grn' ? '销售公司' : '采购公司' }}</span
                    >
                    <a-input size="small" v-model="companyName" />
                </div>
                <div class="notice-editor-form-item">
                    <span class="label"><em class="em red">*</em>仓库</span>
                    <a-input size="small" v-model="warehouseName" />
                </div>
            </div>
            <div
                class="notice-editor-table-wrapper"
                :class="{ 'handler-active': list.length > 8 }"
                @dblclick="onAddProduct"
            >
                <div class="notice-editor-table-add-handler" v-if="list.length > 8" @click.stop="onAddProduct"
                    >点击添加产品</div
                >
                <vxe-table
                    border
                    show-overflow
                    show-header-overflow
                    ref="xTable"
                    height="400"
                    size="small"
                    align="center"
                    :data="list"
                    :mouse-config="{ selected: true }"
                    :checkbox-config="{ range: true }"
                    :keyboard-config="{
                        isArrow: true,
                        isDel: true,
                        isEnter: true,
                        isTab: true,
                        isEdit: true,
                        editMethod
                    }"
                    :edit-config="{ trigger: 'click', mode: 'cell' }"
                    :edit-rules="{
                        'quantity': [{ min: 0.001, type: 'number', message: '数量必须大于0' }],
                        'unit_price': [{ min: 0.0001, type: 'number', message: '单价必须大于0' }]
                    }"
                    @cell-dblclick="({ $event }) => $event.stopPropagation()"
                    @edit-actived="onEditActived"
                >
                    <template slot="empty">
                        <div>双击空白区域添加产品</div>
                    </template>
                    <vxe-table-column type="seq" title="NO." width="60"></vxe-table-column>
                    <vxe-table-column
                        v-for="(column, index) in productListColumns"
                        :key="index"
                        :title="column.title"
                        :field="column.dataIndex"
                        :editRender="column.editRender"
                    />
                    <vxe-table-column title="操作" width="120">
                        <template v-slot:default="{ rowIndex }">
                            <a-button size="small" type="default" @click="handleRemoveProduct(rowIndex)">删除</a-button>
                        </template>
                    </vxe-table-column>
                </vxe-table>
            </div>
            <lessee-selecter
                title="选择租户"
                :visible="modalsVisible.lesseeSelecter"
                :onClose="() => handleCloseModals('lesseeSelecter')"
                :onSubmit="onLesseeSelecterSubmit"
            />
            <product-selecter
                :visible="modalsVisible.productSelecter"
                :noticeType="noticeType"
                :dealerInnerSn="lessee['dealer_inner_sn']"
                :onClose="() => handleCloseModals('productSelecter')"
                :onSubmit="handleAddProduct"
            />
        </template>
        <template slot="footer">
            <a-button type="default" @click="handlePrint" style="float: left">打印</a-button>
            <a-button type="default" @click="onClose">取消</a-button>
            <a-button type="primary" @click="handleSave(1)" :loading="loading">{{ submit1Text }}</a-button>
            <a-button type="primary" @click="handleSave(2)" :loading="loading">{{ submit2Text }}</a-button>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal.vue';
import modalsMixin from '@/mixins/modals';
import productUnitOptionsMixin from '@/mixins/productUnitOptions';
import ProductSelecter from './ProductSelecter.vue';
import LesseeSelecter from './LesseeSelecter.vue';
import productListColumns from './productListColumns';

export default {
    name: 'NoticeEditor',
    components: {
        BaseModal,
        ProductSelecter,
        LesseeSelecter
    },
    mixins: [modalsMixin, productUnitOptionsMixin],
    props: {
        title: {
            type: String,
            default: '通知'
        },
        visible: {
            type: Boolean,
            default: false
        },
        height: {
            type: Number,
            default: 600
        },
        width: {
            type: Number,
            default: 1200
        },
        submit1Text: {
            type: String,
            default: '保存'
        },
        submit2Text: {
            type: String,
            default: '确认'
        },
        noticeType: {
            type: String,
            required: true,
            validator(value) {
                return ['grn', 'odo'].indexOf(value) !== -1;
            }
        },
        defaultData: {
            type: Object,
            default: null
        },
        productAddAble: {
            type: Boolean,
            default: true
        },
        onClose: {
            type: Function,
            default: null
        },
        onSubmit: {
            type: Function,
            default: null
        }
    },
    data() {
        return {
            loading: false,
            lessee: {},
            companyName: '',
            warehouseName: '',
            list: []
        };
    },
    computed: {
        productListColumns() {
            return productListColumns;
        }
    },
    methods: {
        handleInitData() {
            const { defaultData } = this;
            if (!defaultData) {
                this.lessee = {};
                this.companyName = '';
                this.warehouseName = '';
                this.list = [];
                return;
            }
            this.lessee = defaultData.lessee;
            this.companyName = defaultData.companyName;
            this.warehouseName = defaultData.warehouseName;
            this.list = [defaultData.product];
        },
        handleAddProduct(products) {
            const newProducts = products.map((_) => {
                const result = {
                    ..._,
                    'product_vendor_inner_sn': _['vendor_inner_sn'],
                    'product_vendor_name': _['vendor_name'],
                    'batch_number': '',
                    'quantity': 1,
                    'unit_price': 1,
                    'product_date': '',
                    'overdue_date': '',
                    'rate': 0,
                    unitLoading: false,
                    unitLoaded: false,
                    unitSelecterOptions: []
                };
                if (_.unitList && _.unitList[0]) {
                    result['unit'] = _.unitList[0].label;
                    result['unit_inner_sn'] = _.unitList[0].value;
                    result.unitSelecterOptions = [
                        {
                            label: _.unitList[0].label,
                            name: _.unitList[0].label,
                            value: _.unitList[0].value
                        }
                    ];
                }
                return result;
            });
            newProducts.forEach((product) => this.list.push(product));
            this.handleCloseModals('productSelecter');
        },
        handleRemoveProduct(index) {
            const newList = [];
            for (let i = 0, l = this.list.length; i < l; i += 1) {
                if (i === index) continue;
                newList.push(this.list[i]);
            }
            this.list = newList;
        },
        async handleSave(saveType) {
            if (!this.onSubmit) return;
            try {
                const { find, pick, trim } = this.$XEUtils;
                let { lessee, companyName, warehouseName, list } = this;
                if (!lessee['dealer_inner_sn']) throw new Error('请选择租户');

                companyName = trim(companyName);
                if (!companyName) throw new Error('请填写公司名称');

                warehouseName = trim(warehouseName);
                if (!warehouseName) throw new Error('请填写仓库');

                if (list.length < 1) throw new Error('产品列表不能为空');

                const products = list.map((product) => {
                    if (product['quantity'] <= 0) {
                        this.$refs.xTable.setActiveCell(product, 'quantity');
                        throw new Error('产品数量必须大于0');
                    }
                    if (product['unit_price'] <= 0) {
                        this.$refs.xTable.setActiveCell(product, 'unit_price');
                        throw new Error('产品单价必须大于0');
                    }
                    if (
                        product['overdue_date'] &&
                        product['produce_date'] &&
                        new Date(product['overdue_date']) <= new Date(product['produce_date'])
                    ) {
                        this.$refs.xTable.setActiveCell(product, 'produce_date');
                        throw new Error('产品的失效日期应该晚于生产日期');
                    }
                    const unitOption = find(
                        product.unitSelecterOptions,
                        (item) => item.value === product['unit_inner_sn']
                    );
                    const result = pick(product, [
                        'batch_number',
                        'mdrf_inner_sn',
                        'mdrf_sn',
                        'overdue_date',
                        'produce_date',
                        'product_inner_sn',
                        'product_name',
                        'product_type',
                        'product_vendor_inner_sn',
                        'product_vendor_name',
                        'quantity',
                        'rate',
                        'specification',
                        'specification_inner_sn',
                        'unit',
                        'unit_inner_sn',
                        'unit_price'
                    ]);
                    result['quantity'] = Math.max(parseFloat(result['quantity']), 0.001);
                    result['unit_price'] = Math.max(parseFloat(result['unit_price']), 0.0001);
                    if (unitOption) {
                        result.unit = unitOption.name;
                        result['unit_inner_sn'] = unitOption.value;
                    } else {
                        result.unit = '';
                        result['unit_inner_sn'] = 1;
                    }
                    return result;
                });
                try {
                    this.loading = true;
                    await this.onSubmit(
                        {
                            'dealer_inner_sn': lessee['dealer_inner_sn'],
                            'dealer_name': lessee['dealer_name'],
                            'company_name': companyName,
                            'warehouse_name': warehouseName,
                            'details': products
                        },
                        saveType
                    );
                } catch (err) {
                    this.$message.error('保存失败');
                    throw err;
                } finally {
                    this.loading = false;
                }
            } catch (err) {
                this.$message.error(err.message);
            }
        },
        handlePrint() {
            this.$confirm({
                title: '提示',
                content: '当前无打印模板，功能暂未开放'
            });
        },
        onLesseeSelecterSubmit(selected) {
            this.lessee = selected[0];
            this.handleCloseModals('lesseeSelecter');
        },
        onAddProduct() {
            if (!this.productAddAble) {
                return;
            }
            if (!this.lessee || !this.lessee['dealer_inner_sn']) {
                this.$message.warning('请先选择租户!');
                this.handleOpenModals('lesseeSelecter');
                return;
            }
            this.handleOpenModals('productSelecter');
        },
        editMethod(params) {
            console.log(params);
        },
        async onEditActived({ row, column }) {
            switch (column.property) {
                case 'unit_inner_sn':
                    if (!row.unitLoaded) {
                        try {
                            row.unitLoading = true;
                            const unitOptions = await this.loadProductUnitOptions(row);
                            row.unitLoaded = true;
                            row.unitSelecterOptions = unitOptions;
                        } finally {
                            row.unitLoading = false;
                        }
                    }
                    break;
                default:
            }
        }
    },
    watch: {
        visible(newValue, oldValue) {
            if (newValue !== oldValue && newValue === true) {
                if (this.handleInitData) {
                    this.handleInitData();
                }
            }
        }
    }
};
</script>

<style lang="less" scoped>
.notice-editor-form-wrapper {
    margin-bottom: 24px;
    display: flex;
}
.notice-editor-form-item {
    display: flex;
    flex: 1;
    .label {
        margin-right: 12px;
        text-align: right;
        min-width: 72px;
    }
}
.notice-editor-table-wrapper {
    position: relative;
    &.handler-active {
        /deep/ .vxe-table--body-wrapper {
            padding-bottom: 36px;
        }
    }
}
.notice-editor-table-add-handler {
    position: absolute;
    bottom: 0;
    z-index: 2;
    width: 100%;
    height: 36px;
    line-height: 36px;
    color: #888888;
    background-color: #f9f9f9;
    text-align: center;
    cursor: pointer;
    &:hover {
        background-color: #f0faff;
    }
}
</style>
