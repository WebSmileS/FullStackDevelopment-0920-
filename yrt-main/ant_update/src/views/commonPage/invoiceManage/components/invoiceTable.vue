<template>
    <div class="form-table">
        <vxe-table
            ref="detailModalTable"
            size="small"
            :height="226"
            border
            resizable
            auto-resize
            highlight-current-row
            align="center"
            show-overflow
            show-header-overflow
            showHeader
            @cell-dblclick="dblclickAddProductFn"
            :data="datas"
            :loading="pagation.loading"
        >
            <vxe-table-column field="NO." title="NO." width="50px" type="seq" align="center">
                <template v-slot="{ row, seq }">
                    <div>
                        <i v-show="row.isNew" class="red-dot-add"></i>
                        <span>{{ seq }}</span>
                    </div>
                </template>
            </vxe-table-column>
            <vxe-table-column
                field="product_vendor_name"
                title="厂商名称"
                width="150px"
                align="center"
            ></vxe-table-column>
            <vxe-table-column field="product_name" title="产品名称" width="120px" align="center"></vxe-table-column>
            <vxe-table-column field="specification" title="规格型号" width="90px" align="center"></vxe-table-column>
            <vxe-table-column field="unit" title="单位" width="60px" align="center"></vxe-table-column>
            <vxe-table-column field="unit_price" title="单价" width="80px" align="center">
                <template v-slot="{ row }">
                    <span v-if="row.voucher_type_c && row.voucher_inner_sn_c && row.voucher_detail_inner_sn_c">{{
                        row.unit_price
                    }}</span>
                    <div v-else @dblclick.stop="() => {}">
                        <a-input-number
                            :min="0"
                            :formatter="(value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                            :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                            v-model="row.unit_price"
                            class="form-input"
                            size="small"
                            style="width: 100%"
                            :disabled="isReadonly || isFinanceInvoiceOdoReadonly"
                        />
                    </div>
                </template>
            </vxe-table-column>
            <vxe-table-column field="quantity" title="数量" width="60px" align="center">
                <template v-slot="{ row }">
                    <span v-if="row.voucher_type_c && row.voucher_inner_sn_c && row.voucher_detail_inner_sn_c">{{
                        row.quantity
                    }}</span>
                    <div v-else @dblclick.stop="() => {}">
                        <a-input-number
                            :formatter="(value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                            :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                            v-model="row.quantity"
                            class="form-input"
                            size="small"
                            style="width: 100%; z-index: 1"
                            :disabled="isReadonly || isFinanceInvoiceOdoReadonly"
                        />
                    </div>
                </template>
            </vxe-table-column>
            <vxe-table-column title="金额" width="70px" align="center">
                <template v-slot="{ row }">
                    <span>
                        {{ $XEUtils.commafy($XEUtils.multiply(row.quantity, row.unit_price), { fixed: 2 }) }}
                    </span>
                </template>
            </vxe-table-column>
            <vxe-table-column field="rate" title="税率" width="60px" align="center">
                <template v-slot="{ row }">
                    <span v-if="row.voucher_type_c && row.voucher_inner_sn_c && row.voucher_detail_inner_sn_c"
                        >{{ row.rate }}&nbsp;%</span
                    >
                    <a-tree-select
                        v-else
                        treeNodeFilterProp="title"
                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                        placeholder=""
                        treeDefaultExpandAll
                        class="form-input"
                        size="small"
                        :treeData="rateData"
                        v-model="row.rate"
                        @click="() => {}"
                        :disabled="isReadonly || isFinanceInvoiceOdoReadonly"
                    >
                        <span slot="suffixIcon">%</span>
                    </a-tree-select>
                </template>
            </vxe-table-column>
            <vxe-table-column field="invoiced_quantity" title="开票数量" width="80px" align="center">
                <template v-slot="{ row }">
                    <div @dblclick.stop="() => {}">
                        <a-input-number
                            :formatter="(value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                            :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                            v-model="row.invoiced_quantity"
                            class="form-input"
                            size="small"
                            style="width: 100%; z-index: 1"
                            :disabled="isReadonly || isFinanceInvoiceOdoReadonly"
                        />
                    </div>
                </template>
            </vxe-table-column>
            <vxe-table-column title="开票金额" width="70px" align="center">
                <template v-slot="{ row }">
                    <span>
                        {{ $XEUtils.commafy($XEUtils.multiply(row.invoiced_quantity, row.unit_price), { fixed: 2 }) }}
                    </span>
                </template>
            </vxe-table-column>
            <vxe-table-column field="remarks" title="备注" width="120px" align="center">
                <template v-slot="{ row }">
                    <a-input
                        placeholder=""
                        class="form-input"
                        size="small"
                        v-model="row.remarks"
                        @click="() => {}"
                        :disabled="isReadonly || isFinanceInvoiceOdoReadonly"
                    />
                </template>
            </vxe-table-column>
            <vxe-table-column
                v-if="isShowActionColumn && !isReadonly"
                field="action"
                title="操作"
                width="50px"
                align="center"
            >
                <template v-slot="{ row, seq }">
                    <ul class="action-btn-list-wrap" style="position: relative">
                        <li @click.stop="delFn(row, seq - 1)">
                            <a-tooltip class="tooltip" placement="top" title="删除">
                                <i class="yrt yrt-shanchu-copy cancel"></i>
                            </a-tooltip>
                        </li>
                    </ul>
                </template>
            </vxe-table-column>
            <template v-slot:empty>
                <div class="dblclick-wrap" @dblclick="dblclickAddProductFn">
                    <span>双击添加产品</span>
                </div>
            </template>
        </vxe-table>

        <product-selecter-modal
            :visible="productSelecterVisible"
            :tabs="productSelecterTabs"
            @close="productSelecterVisible = false"
        >
            <product-selecter-grn-or-odo
                slot="grn"
                isInvoiceGrn
                :billsType="formData.bills_type"
                :organizationInnerSn="formData.organization_inner_sn"
                :organizationName="formData.organization_name"
                @submit="onGrnOrOdoProductAdd"
                @close="productSelecterVisible = false"
            ></product-selecter-grn-or-odo>
            <product-selecter-grn-or-odo
                slot="odo"
                isInvoiceOdo
                :billsType="formData.bills_type"
                :organizationInnerSn="formData.organization_inner_sn"
                :organizationName="formData.organization_name"
                @submit="onGrnOrOdoProductAdd"
                @close="productSelecterVisible = false"
            ></product-selecter-grn-or-odo>
            <product-selecter-platform
                slot="platform"
                :billsType="formData.bills_type"
                :organizationInnerSn="formData.organization_inner_sn"
                @submit="onPlatformProductAdd"
                @close="productSelecterVisible = false"
            ></product-selecter-platform>
        </product-selecter-modal>
    </div>
</template>

<script>
import Big from 'big.js';
import ProductSelecterModal from '@/components/ProductSelecterModal';
import ProductSelecterPlatform from '@/components/ProductSelecterPlatform';
import {
    rateData,
    getUseableRateType,
    grnAndOdoInvoiceOrderType
} from '@/views/commonPage/invoiceManage/config/constants';
import ProductSelecterGrnOrOdo from './ProductSelecterGrnOrOdo';

export default {
    name: 'invoiceTable', // 入库|出库发票的模态框中间部分表格
    props: {
        isShowActionColumn: {
            type: Boolean, // 是否显示操作栏
            default: true
        },
        modalTitle: {
            type: String, // 当前是入库还是出库的文字
            default: ''
        },
        formData: {
            type: Object, // 相关机构的数据
            required: true
        },
        columns: {
            type: Array, // 表格的头部
            required: true
        },
        datas: {
            type: Array, // 表格的数据
            required: true
        },
        pagation: {
            type: Object, // 表格的分页
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
        // 发票管理模块业务人员的出入库发票单grnAndOdoInvoiceOrderType['GRN_INVOICE']、grnAndOdoInvoiceOrderType['ODO_INVOICE']]
        orderTypeData: {
            type: Object,
            retuired: true
        }
    },
    components: {
        ProductSelecterModal,
        ProductSelecterPlatform,
        ProductSelecterGrnOrOdo
    },
    data() {
        return {
            partB: {
                // 新增产品窗口给子组件传递值
                org_id: this.$cookie.get('userbelong'),
                modalType: -1, // 1 - 合同内的产品  2 - 期初库存的产品列表  3 - 采购计划  4 - 采购/销售订单  7 - 入库单  8 - 出库单
                oname: '', // 厂商/医院/经销商名称
                otype: '', // 是厂商/医院/经销商  0 - 经销商   1 - 厂商   2 - 医院
                oid: '',
                warehouse_inner_sn: ''
            },
            rateData, // 税率数据
            productSelecterVisible: false
        };
    },
    computed: {
        isInvoiceGRN() {
            return this.orderTypeData && this.orderTypeData.value === grnAndOdoInvoiceOrderType.GRN_INVOICE.value;
        },
        isInvoiceODO() {
            return this.orderTypeData && this.orderTypeData.value === grnAndOdoInvoiceOrderType.ODO_INVOICE.value;
        },
        productSelecterTabs() {
            if (this.isInvoiceGRN) {
                return [
                    { title: '入库产品', key: 'grn' },
                    { title: '平台产品', key: 'platform' }
                ];
            }
            if (this.isInvoiceODO) {
                return [
                    { title: '出库产品', key: 'odo' },
                    { title: '平台产品', key: 'platform' }
                ];
            }
            return [];
        }
    },
    methods: {
        delFn(row, index) {
            this.$emit('delTableData', index);
        },
        dblclickAddProductFn() {
            if (this.isReadonly) return;

            if (!this.formData.organization_inner_sn && !this.formData.organization_name) {
                this.$warning({
                    title: '操作提示',
                    content: '请先选择相关机构',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            this.partB.modalType = 7;
            this.partB.otype = this.formData.bills_type;

            this.partB.oname = this.formData.organization_name;
            this.partB.oid = this.formData.organization_inner_sn;

            this.productSelecterVisible = true;
            this.$nextTick(() => {
                this.$Utils.globalDragCenterFn('add-return-list');
            });
        },
        addReturnSearchDataFn(addReturnDatas, platformAddReturnData) {
            // platformAddReturnData: {'ven': [], 'dea': []}
            const hasVendorProducts = platformAddReturnData && platformAddReturnData.ven.length > 0;
            // const hasDealerProducts = platformAddReturnData && platformAddReturnData.dea.length > 0
            const hasOrderProducts = addReturnDatas && addReturnDatas.length > 0;
            const newProducts = [];

            // 平台产品 - 厂商产品
            if (hasVendorProducts) {
                platformAddReturnData.ven.forEach((product) => {
                    newProducts.push({
                        ...product,
                        remarks: '',
                        invoiced_quantity: 1,
                        unit_price: 0,
                        quantity: 1,
                        unit: product.unit_name,
                        rate: getUseableRateType(0).value, // 平台产品税率默认0
                        product_vendor_name: product.product_vendor_name || product.vendor_name,
                        product_vendor_inner_sn: product.product_vendor_inner_sn || product.vendor_inner_sn,
                        id: undefined // 平台产品没有关联的单据id
                    });
                });
            }

            // 平台产品 - 经销商产品
            // if (hasDealerProducts) {
            //     this.datas.push({})
            // }

            // 出入库单据的产品
            if (hasOrderProducts) {
                const filterMap = {};
                // 过滤重复的出入库单据产品
                this.datas.forEach((product) => {
                    filterMap[`${product.voucher_inner_sn_c}&${product.voucher_detail_inner_sn_c}`] = true;
                });
                addReturnDatas.forEach((product) => {
                    if (filterMap[`${product.voucher_inner_sn_c}&${product.voucher_detail_inner_sn_c}`]) {
                        return;
                    }
                    newProducts.push({
                        remarks: '',
                        invoiced_quantity: product.quantity,
                        unit_price: 0,
                        quantity: 1,
                        ...product,
                        rate: getUseableRateType(new Big(product.rate).times(100).toNumber()).value,
                        product_vendor_name: product.product_vendor_name || product.vendor_name,
                        product_vendor_inner_sn: product.product_vendor_inner_sn || product.vendor_inner_sn
                    });
                });
            }

            // 关闭弹窗
            if (hasVendorProducts || hasOrderProducts) {
                this.$emit('datasAdd', newProducts);
                this.productSelecterVisible = false;
            }
        },
        onGrnOrOdoProductAdd(products) {
            this.addReturnSearchDataFn(products);
        },
        onPlatformProductAdd(products) {
            this.addReturnSearchDataFn(undefined, { ven: products });
        }
    },
    watch: {
        datas: {
            handler(newVal) {
                if (newVal) {
                    this.$emit('getTableData', newVal);
                }
            },
            deep: true
        }
    }
};
</script>

<style lang="less" scoped></style>
