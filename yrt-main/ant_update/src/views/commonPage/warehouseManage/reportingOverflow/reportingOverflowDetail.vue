<!-- 
* @Author: yangtao  
* @Description: 新增报溢弹框
-->
<template>
    <base-modal
        dragKey="advance_sale_order_editor_md"
        fullscreenUseable
        :destroyOnClose="true"
        :title="`新增${orderName}`"
        :width="1100"
        :visible="visible"
        @close="handleClose"
    >
        <template v-slot:content="{ fullscreen }">
            <div style="position: relative">
                <h6 style="margin: 24px 0; text-align: center; font-size: 24px; font-weight: 500">
                    <span v-if="organizationNameLogined">{{ organizationNameLogined }}&nbsp;&nbsp;</span>
                    <span>{{ orderName }}</span>
                </h6>
            </div>
            <editor-form-header>
                <editor-form-header-item>
                    <span class="label"><span class="red">*</span>相关机构：</span>
                    <base-input-with-clear-button
                        size="small"
                        :value="dataEditing.buyerName"
                        @click="handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                        @clear="onOrganizationClear"
                    ></base-input-with-clear-button>
                </editor-form-header-item>
                <editor-form-header-item></editor-form-header-item>
            </editor-form-header>
            <div style="margin-top: 24px" @dblclick="handleSelectProducts">
                <vxe-table
                    border
                    show-overflow
                    show-header-overflow
                    show-footer
                    resizable
                    auto-resize
                    ref="xTable"
                    size="small"
                    align="center"
                    :height="fullscreen ? fullscreenTableHeight : 300"
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
                    :edit-config="{ trigger: 'click', mode: 'cell', activeMethod: handleCheckCellEditable }"
                    :footer-method="() => [[]]"
                    @cell-dblclick="({ $event }) => $event.stopPropagation()"
                    @edit-actived="onEditActived"
                >
                    <template slot="empty">
                        <div>双击空白区域添加产品</div>
                    </template>
                    <vxe-table-column v-bind="tableColumnConfig.seq" field="index" fixed="left"></vxe-table-column>
                    <vxe-table-column
                        v-for="column in reportingOverflowEditorColumns"
                        v-bind="column"
                        :key="column.field"
                    ></vxe-table-column>
                    <vxe-table-column v-bind="tableColumnConfig.operation">
                        <template v-slot:default="{ rowIndex }">
                            <table-row-operator del :payload="rowIndex" @del="handleRemoveProduct"></table-row-operator>
                        </template>
                        <template slot="footer">
                            <div style="overflow: hidden">
                                <a-button size="small" type="primary" @click="handleSelectProducts">添加产品</a-button>
                            </div>
                        </template>
                    </vxe-table-column>
                </vxe-table>
            </div>
            <!-- <editor-form-footer-row-item style="margin-top: 24px">
                <span class="label">备注：</span>
                <a-input size="small" v-model="dataEditing.description"></a-input>
            </editor-form-footer-row-item> -->
            <organization-selecter
                title="选择相关机构"
                destroyOnClose
                :visible="modalsVisible[modalsKeyMap.ORGANIZATION_SELECTER]"
                @close="handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER)"
                @submit="onOrganizationSelected"
            ></organization-selecter>
            <product-selecter-modal
                destroyOnClose
                :visible="modalsVisible[modalsKeyMap.PRODUCT_SELECTER]"
                :tabs="productSelecterTabs"
                @close="handleCloseModals(modalsKeyMap.PRODUCT_SELECTER)"
            >
                <!-- 可用产品 -->
                <product-selecter-advance-sale
                    slot="useable"
                    :billsType="dataEditing.billsType"
                    :organizationInnerSn="dataEditing.buyerId"
                    :warehouse="currentWarehouse"
                    @submit="onProductAdd"
                ></product-selecter-advance-sale>
                <!-- 产商产品 -->
                <product-selecter-vendor
                    slot="vendor"
                    :billsType="dataEditing.billsType"
                    :organizationInnerSn="dataEditing.buyerId"
                    @submit="onProductAdd"
                ></product-selecter-vendor>
            </product-selecter-modal>
        </template>
        <template slot="footer">
            <a-button type="default" @click="handleClose">取消</a-button>
            <a-button type="primary" @click="onSaveButtonClick" :loading="buttonsLoading.save">保存</a-button>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal';
import TableRowOperator from '@/components/TableRowOperator';
import BaseInputWithClearButton from '@/components/BaseInputWithClearButton';
import ProductSelecterModal from '@/components/ProductSelecterModal';
import EditorFormHeader from '@/components/EditorFormHeader';
import EditorFormHeaderItem from '@/components/EditorFormHeaderItem';
import tableColumnConfig from '@/config/tableColumnConfig';
import ProductSelecterAdvanceSale from './ProductSelecterAdvanceSale';
import OrganizationSelecter from '@/components/OrganizationSelecter';
import { reportingOverflowEditorColumns } from './columns';
import ProductSelecterVendor from '@/components/ProductSelecterVendor.vue';
import modalsMixin from '@/mixins/modals';

// import { odoBatchListAPI } from '@/service/pageAjax';

const ORDER_NAME = '报溢单';

const modalsKeyMap = Object.freeze({
    ORGANIZATION_SELECTER: 'o',
    PRODUCT_SELECTER: 'p'
});

export default {
    name: 'ReportingOverflowDetail',
    components: {
        BaseModal,
        ProductSelecterModal,
        ProductSelecterAdvanceSale,
        OrganizationSelecter,
        TableRowOperator,
        BaseInputWithClearButton,
        EditorFormHeader,
        EditorFormHeaderItem,
        ProductSelecterVendor
    },
    mixins: [modalsMixin],
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        targetOrder: {
            type: Object,
            default: null
        }
    },
    data() {
        return {
            modalsKeyMap,
            dataEditing: {
                advanceSaleNo: undefined,
                billsType: undefined,
                buyerId: undefined,
                buyerName: undefined,
                agentUserId: undefined,
                saleDepartmentId: undefined,
                saleDepartmentName: undefined,
                warehouseId: undefined,
                warehouseName: undefined,
                warehouseUserId: undefined,
                description: undefined
            },
            dataEditingDetails: [],
            detailLoading: false,
            buttonsLoading: {
                save: false
            },
            needAutoOpenProductSelecter: false
        };
    },
    computed: {
        organizationInnerSn() {
            return this.$cookie.get('userbelong');
        },
        orderName() {
            return ORDER_NAME;
        },

        tableColumnConfig() {
            return tableColumnConfig;
        },

        reportingOverflowEditorColumns() {
            return reportingOverflowEditorColumns;
        },

        productSelecterTabs() {
            return [
                { title: '可用产品', key: 'useable' },
                { title: '厂商产品', key: 'vendor' }
            ];
        },

        /**
         * 全屏时表格高度
         */
        fullscreenTableHeight() {
            return this.$store.state.clientHeight - 348;
        },

        /**
         * 当前登录的机构名
         */
        organizationNameLogined() {
            const info = JSON.parse(this.$cookie.get('EmployeeInfo'));
            if (info && info.OrgName) {
                return info.OrgName;
            }
            return '';
        },

        currentWarehouse() {
            if (!this.dataEditing.warehouseId) {
                return undefined;
            }
            return {
                name: this.dataEditing.warehouseName,
                innerSn: this.dataEditing.warehouseId
            };
        }
    },
    methods: {
        onSaveButtonClick() {
            this.handleSave();
        },

        /**
         * 表格单元格编辑激活时
         */
        async onEditActived({ row, column }) {
            console.log(row);
            switch (column.property) {
                case 'quantity':
                    '报损数量';
                    break;
            }
        },

        onOrganizationSelected(organizations) {
            if (organizations && organizations.length > 0) {
                this.dataEditing.buyerName = organizations[0].organizationName;
                this.dataEditing.buyerId = organizations[0].organizationInnerSn;
                this.dataEditing.billsType = organizations[0].billsType;
            }
            this.handleCloseModals(modalsKeyMap.ORGANIZATION_SELECTER);
            if (this.needAutoOpenProductSelecter) {
                this.needAutoOpenProductSelecter = false;
                this.handleOpenModals(modalsKeyMap.PRODUCT_SELECTER);
            }
        },

        onOrganizationClear() {
            this.dataEditing.buyerId = undefined;
            this.dataEditing.buyerName = undefined;
            this.dataEditing.billsType = undefined;
        },

        /**
         * 产品选择返回值
         * @param {object} data 产品数据
         */
        onProductAdd(data) {
            if (data.advanceSaleOrderProducts) {
                this.dataEditingDetails.push(...data.advanceSaleOrderProducts);
            } else if (data.purchaseGrnOrderProducts) {
                this.dataEditingDetails.push(...data.purchaseGrnOrderProducts);
            }
            this.handleCloseModals(modalsKeyMap.PRODUCT_SELECTER);
        },

        /**
         * 检测单元格是否可编辑
         * @param {object} data
         * @param {object} data.row 行数据
         * @param {object} data.column 列信息
         */
        handleCheckCellEditable(data) {
            if (
                data.column.property === 'unit_inner_sn' &&
                (data.row.voucher_inner_sn_c || data.row.voucher_inner_sn_c === 0)
            ) {
                return false;
            }
            return true;
        },

        /**
         * 关闭编辑器
         */
        handleClose() {
            this.$emit('close');
        },

        /**
         * 保存
         */
        async handleSave() {
            this.$emit('orderFinish', this.dataEditingDetails);
            this.handleClose();
        },

        handleSelectProducts() {
            if (!this.dataEditing.billsType || !this.dataEditing.buyerId) {
                this.$message.warning('请选择相关机构');
                this.handleOpenModals(modalsKeyMap.ORGANIZATION_SELECTER);
                this.needAutoOpenProductSelecter = true;
                return;
            }
            this.handleOpenModals(modalsKeyMap.PRODUCT_SELECTER);
        },

        /**
         * 移除表格内的一个产品
         * @param {number} index
         */
        handleRemoveProduct(index) {
            this.dataEditingDetails.splice(index, 1);
        }
    }
};
</script>
