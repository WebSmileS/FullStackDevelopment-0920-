<template>
    <div>
        <!-- 采购订单【包含旧版的销售订单】新增/修改区域开始 -->
        <a-modal
            :visible="visible"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-add global-drag"
            :class="isAddProductModalTableMax ? 'my-modal-zoom' : ''"
            :width="1200"
            :zIndex="3"
            :keyboard="false"
            :afterClose="afterClosePlanmodalFn"
        >
            <div v-globalDrag="{ el: 'modal-add' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ planModal.title }}
                </h6>
                <a-button
                    @click="addProductZoomFn"
                    class="modal-header-zoom"
                    type="primary"
                    :icon="isAddProductModalTableMax ? 'fullscreen-exit' : 'fullscreen'"
                    size="large"
                />
            </div>
            <template slot="footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button @click="planModalGroupBtnFn('print')" key="print" :loading="planModal.loadingPrint">
                            打印
                        </a-button>
                        <a-button
                            @click="planModalGroupBtnFn('exportExcel')"
                            key="exportExcel"
                            :loading="planModal.loadingExportExcel"
                        >
                            导出Excel
                        </a-button>
                    </li>
                    <li>
                        <a-button @click="planModalGroupBtnFn('cancel')" key="cancel">
                            {{ planModalTextComputed }}
                        </a-button>
                        <a-button
                            v-show="sheetData.status === 0 || btnName === 'add'"
                            :disabled="isEditDisabled"
                            @click="planModalGroupBtnFn('saveDraft')"
                            key="saveDraft"
                            type="primary"
                            :loading="planModal.loadingSaveDraft"
                        >
                            存草稿
                        </a-button>
                        <a-button
                            v-show="sheetData.status === 1"
                            :disabled="isEditDisabled"
                            @click="planModalGroupBtnFn('audit')"
                            key="audit"
                            type="primary"
                            :loading="planModal.loadingAudit"
                        >
                            审核
                        </a-button>
                    </li>
                </ul>
            </template>
            <div class="modal-form-input" style="height: 520px">
                <h6
                    style="
                        position: relative;
                        padding-bottom: 24px;
                        margin-bottom: 0;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    "
                >
                    <span style="margin-right: 10px; font-size: 24px; color: #444">{{ orgName }}</span>
                    <span style="font-size: 24px; color: #444">采购订单</span>
                    <div v-if="btnName !== 'add'" style="position: absolute; right: 0; display: flex">
                        <span class="label" style="font-size: 14px; color: rgba(0, 0, 0, 0.65)"
                            ><em class="em red">*</em>单据编号</span
                        >
                        <a-input disabled v-model="sheetData.po_sn" placeholder="" class="input" />
                    </div>
                </h6>
                <ul class="top-list" style="width: 100%">
                    <li class="item">
                        <span class="label"><em class="em red">*</em>采购单位</span>
                        <a-input
                            class="input"
                            type="text"
                            :disabled="btnName === 'edit'"
                            v-model="sheetData.oname"
                            @click="openChooseTb"
                            placeholder=""
                        />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>采购员</span>
                        <a-tree-select
                            :disabled="isEditDisabled"
                            treeNodeFilterProp="title"
                            searchPlaceholder=""
                            :showSearch="true"
                            :treeData="planModalEmployes"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            v-model="sheetData.EmployeSn"
                            allowClear
                            @click="clickDescriptionFn"
                            @change="changePlanModalEmployeSnFn"
                            class="input"
                            style="min-width: 120px"
                        >
                        </a-tree-select>
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>采购部门</span>
                        <a-tree-select
                            treeNodeFilterProp="title"
                            :showSearch="true"
                            :disabled="isEditDisabled"
                            :treeData="planModalDepartmentSns"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            v-model="sheetData.DepartmentSn"
                            :allowClear="false"
                            @click="clickDescriptionFn"
                            @change="changePlanModalDepartmentSnFn"
                            class="input"
                            style="min-width: 120px"
                        >
                        </a-tree-select>
                    </li>
                    <li class="item">
                        <span class="label">期望到货日期</span>
                        <a-date-picker
                            :disabled="isEditDisabled"
                            v-model="sheetData.expectAogDate"
                            format="YYYY-MM-DD"
                            @click="clickDescriptionFn"
                            @change="changeExpectAogDateFn"
                            placeholder=""
                            allowClear
                            class="input"
                        />
                    </li>
                    <li class="item">
                        <span class="label">仓库</span>
                        <a-tree-select
                            treeNodeFilterProp="title"
                            :showSearch="true"
                            :disabled="isEditDisabled"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            :treeData="planModalWarehouseData"
                            v-model="sheetData.WarehouseSn"
                            placeholder=""
                            treeDefaultExpandAll
                            allowClear
                            @click="clickDescriptionFn"
                            @change="changewareHouseFn"
                        >
                        </a-tree-select>
                    </li>
                </ul>
                <div
                    ref="applyTable"
                    class="tab-table-wrap cur-overflow-hidden"
                    style="padding-bottom: 0; margin-top: 0"
                >
                    <div class="tableFather" style="height: 230px" @dblclick="TableDblclickFn">
                        <vxe-grid
                            ref="dataTable"
                            size="small"
                            :data="planModal.datas"
                            :loading="planModal.loading"
                            height="auto"
                            border
                            resizable
                            auto-resize
                            show-overflow
                            show-header-overflow
                            showHeader
                            highlight-current-row
                            @cell-click="TableClickFn"
                            @cell-dblclick="TableDblclickFn"
                            @cancel="handleCancel"
                            align="center"
                        >
                            <vxe-table-column field="index" title="NO." width="50" :resizable="false" fixed="left">
                                <template v-slot="{ row, seq }">
                                    <div>
                                        <i :class="!row.detail_inner_sn ? 'red-dot-add' : 'hide'"></i>
                                        <span>{{ seq }}</span>
                                    </div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="product_name" title="产品" width="144px">
                                <template v-slot="{ row }">
                                    <span v-if="!!row.product_inner_sn">{{ row.product_name }}</span>
                                    <a-input v-else v-model="row.product_name" class="input" style="width: 100%" />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="specification" title="规格型号" width="100px"></vxe-table-column>
                            <vxe-table-column field="product_vendor_name" title="厂商/经销商" width="120px">
                                <template v-slot="{ row }">
                                    {{ row.product_vendor_name || row.partB_name }}
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="status" title="状态" width="95px">
                                <template v-slot="{ row }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <span v-else>{{ row.status_name }}</span>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="quantity" title="数量" width="90px">
                                <template v-slot="{ row, rowIndex }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-tooltip v-else :trigger="['focus']">
                                        <span slot="title">数量：{{ row.quantity }}</span>
                                        <a-input-number
                                            :disabled="isSourceBuyerAudited ? false : isEditDisabled"
                                            :min="0.0001"
                                            :value="row.quantity"
                                            @change="(value) => updateProductQuantity(value, rowIndex)"
                                            class="input"
                                            style="width: 100%"
                                        />
                                    </a-tooltip>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_inner_sn" title="包装单位" width="100px">
                                <template v-slot="{ row, seq }">
                                    <a-cascader
                                        :disabled="!isEditable"
                                        :allowClear="false"
                                        :options="row.unitList"
                                        v-model="row.unit_inner_sn"
                                        changeOnSelect
                                        :displayRender="unitRenderFn"
                                        :expandTrigger="'hover'"
                                        placeholder=""
                                        style="width: 100%"
                                        @click="unitClickFn(row, seq)"
                                        @change="unitChangeFn"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="batch_number" title="批号" width="90px">
                                <template v-slot="{ row }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-input
                                        v-else
                                        :disabled="isEditDisabled"
                                        v-model="row.batch_number"
                                        class="input"
                                        style="width: 100%"
                                        @change="updateFooterPlanModalFnBN"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_price" title="单价" width="100px">
                                <template v-slot="{ row }">
                                    <span>{{ row.unit_price.toFixed(4) }}</span>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="money" title="金额" width="100px">
                                <template v-slot="{ row }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <span v-else>{{ (row.unit_price * row.quantity).toFixed(2) }}</span>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="rate" title="税率" width="90px">
                                <template v-slot="{ row, rowIndex }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-select
                                        v-else
                                        :disabled="isEditDisabled"
                                        v-model="row.rate"
                                        style="width: 100%"
                                        @change="(value) => updateProductRate(value, rowIndex)"
                                    >
                                        <a-select-option :value="0"> 0% </a-select-option>
                                        <a-select-option :value="1"> 1% </a-select-option>
                                        <a-select-option :value="3"> 3% </a-select-option>
                                        <a-select-option :value="6"> 6% </a-select-option>
                                        <a-select-option :value="9"> 9% </a-select-option>
                                        <a-select-option :value="13"> 13% </a-select-option>
                                    </a-select>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax_unit_price" title="含税单价" width="80px">
                                <template v-slot="{ row, rowIndex }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-dropdown v-else :trigger="['click']" :disabled="isPriceDisabled">
                                        <a-tooltip :trigger="['focus']">
                                            <span slot="title"
                                                >含税单价：{{
                                                    $XEUtils.commafy(row.tax_unit_price, { fixed: 4 })
                                                }}</span
                                            >
                                            <a-input-number
                                                :disabled="isPriceDisabled"
                                                :min="0"
                                                :value="row.tax_unit_price"
                                                @change="(value) => updateProductTaxUnitPrice(value, rowIndex)"
                                                class="input"
                                                style="width: 100%"
                                            />
                                        </a-tooltip>
                                        <a-menu v-if="row.unitPriceList && row.unitPriceList.length" slot="overlay">
                                            <a-menu-item
                                                v-for="(priceItem, priceIndex) in row.unitPriceList"
                                                :key="priceIndex"
                                                @click="updateProductTaxUnitPrice(priceItem, rowIndex)"
                                            >
                                                <a href="javascript:;">{{ priceItem }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax_amount" title="含税金额" width="100px">
                                <template v-slot="{ row, rowIndex }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-tooltip v-else :trigger="['focus']">
                                        <span slot="title"
                                            >含税金额：{{ $XEUtils.commafy(row.tax_amount, { fixed: 3 }) }}</span
                                        >
                                        <a-input-number
                                            class="input"
                                            style="width: 100%"
                                            :disabled="isPriceDisabled"
                                            :min="0"
                                            :value="row.tax_amount"
                                            @change="(value) => updateProductTaxAmountTotal(value, rowIndex)"
                                        />
                                    </a-tooltip>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax" title="税额" width="80px">
                                <template v-slot="{ row }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <div v-else>{{ record_tax_total(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                v-if="!(planModal.datas.length <= 0 || sheetData.status === 2)"
                                field="action"
                                title="操作"
                                width="90"
                                :resizable="false"
                                fixed="right"
                            >
                                <template v-slot="{ row, seq }">
                                    <ul
                                        v-if="row.product_inner_sn"
                                        class="action-btn-list-wrap"
                                        style="position: relative"
                                    >
                                        <li
                                            v-if="btnName === 'add' || sheetData.status === 0 || sheetData.status === 1"
                                        >
                                            <a-tooltip class="tooltip" placement="top" title="删除">
                                                <i
                                                    @click.stop="planModalActionBtnFn($event, 'remove', row, seq)"
                                                    class="yrt yrt-shanchu-copy"
                                                    :class="isEditDisabled ? 'disabled' : 'cancel'"
                                                ></i>
                                            </a-tooltip>
                                        </li>
                                        <li v-else-if="sheetData.status === 0 || sheetData.status === 3">
                                            <a-switch
                                                v-if="row.status === 0 || row.status === 2"
                                                @click="planModalActionBtnFn($event, 'stop', row, seq)"
                                                :loading="row.loading"
                                                :checked="row.switch"
                                                size="small"
                                                checkedChildren="终止"
                                                unCheckedChildren="反终止"
                                                defaultChecked
                                                style="color: #faad14; width: 64px"
                                            />
                                            <a-switch
                                                v-else-if="row.status === 3"
                                                @click="planModalActionBtnFn($event, 'stop', row, seq)"
                                                :loading="row.loading"
                                                :checked="row.switch"
                                                size="small"
                                                checkedChildren="终止"
                                                unCheckedChildren="反终止"
                                                defaultChecked
                                                style="color: #faad14; width: 64px"
                                            />
                                        </li>
                                        <li v-else>-</li>
                                    </ul>
                                </template>
                            </vxe-table-column>
                        </vxe-grid>
                    </div>
                    <ul class="order-modal-list">
                        <li>
                            <div class="item-wrap">
                                <span class="label" style="width: auto">送货地址</span>
                                <a-input
                                    v-model="sheetData.Location"
                                    :disabled="isEditDisabled"
                                    placeholder=""
                                    class="input"
                                    @click="clickDescriptionFn"
                                    @change="changeDeliveryAddressFn"
                                />
                            </div>
                        </li>
                        <li>
                            <div class="item-wrap">
                                <span class="label" style="width: auto">摘要</span>
                                <a-input
                                    :disabled="isEditDisabled"
                                    v-model="sheetData.description"
                                    placeholder=""
                                    class="input"
                                    @click="clickDescriptionFn"
                                />
                            </div>
                        </li>
                        <li>
                            <div class="item-wrap">
                                <div class="item">
                                    <span class="label" style="width: auto">制单人</span>
                                    <span>{{ sheetData.creator_name || '-' }}</span>
                                </div>
                                <div class="item">
                                    <span class="label" style="width: auto">生成时间</span>
                                    <span>{{ sheetData.create_time || '-' }}</span>
                                </div>
                            </div>
                            <div class="item-wrap">
                                <div class="item">
                                    <span class="label" style="width: auto">审核人</span>
                                    <span>{{ sheetData.auditor_name || '-' }}</span>
                                </div>
                                <div class="item">
                                    <span class="label" style="width: auto">审核时间</span>
                                    <span>{{ sheetData.audit_time || '-' }}</span>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </a-modal>
        <!-- 采购订单【包含旧版的销售订单】新增/修改区域结束 -->

        <!-- 选择采购单位区域开始 -->
        <a-modal
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            :zIndex="3"
            @cancel="chooseOrgMd.alert = false"
            class="scoped modal-form-input-scoped global-drag choose_org_md"
        >
            <div v-globalDrag="{ el: 'choose_org_md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <div style="margin-top: 56px">
                <chooseOrgTab
                    ref="chooseOrgMd"
                    @chooseOrg="chooseOrgFn"
                    :searchOrgName="searchOrgName"
                    :isParentKeyDownEnter="isParentKeyDownEnter"
                    :partType="1"
                    :pageTypeName="pageTypeName"
                ></chooseOrgTab>
            </div>
        </a-modal>
        <!-- 选择采购单位区域结束 -->

        <!-- 添加产品区域开始 -->
        <a-modal
            :title="addProductMd.title"
            :visible="addProductMd.alert"
            :maskClosable="false"
            :mask="false"
            :width="1000"
            :centered="true"
            @cancel="onAddProClose"
            class="modal-form-input-scoped global-drag add_product"
        >
            <div v-globalDrag="{ el: 'add_product' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addProductMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="onAddProClose">关闭</a-button>
                <a-button :loading="addProductMd.loading" @click="addProductFn" type="primary">添加</a-button>
            </template>
            <add-product ref="chooseProMd" @returnData="getProReturn" :partB="addProductMd.part_b_obj"></add-product>
        </a-modal>
        <!-- 添加产品区域结束 -->
    </div>
</template>

<script>
import { mapState } from 'vuex';
import addProduct from '@/components/addProduct';
import {
    // orderPurchaseConfirmAPI,
    // orderDetailStopAPI,
    // orderDetailUnstopAPI,
    // orderConfirmAPI,
    // orderStopAPI,
    // platformUnitParents,
    // dealerUnitParentsAPI,
    // orderPurchaseEditAPI,
    // orderDelAPI,
    // orderDetailDelAPI,
    // orderOrgListAPI,
    // orgDeptTopTreeAPI,
    // orgEmpDropdownListAPI,
    warehouseUsableTopListAPI,
    dealerUnitTreeAPI,
    platformUnitTree,
    orderPurchaseInfoAPI
    // contractGetContractPriceAPI
} from '@/service/pageAjax';
import chooseOrgTab from '@/components/chooseOrgTab';

// 产品的状态文本map
const productDetailStatusMap = ['待处理', '已终止', '已完结'];

export default {
    name: 'mainOrder',
    components: {
        chooseOrgTab,
        addProduct
    },
    props: {
        visible: Boolean
    },
    data() {
        return {
            data: [],
            fatherRecord: {},
            sheetData: {
                po_sn: '',
                oname: '',
                EmployeSn: '',
                DepartmentSn: '',
                expectAogDate: '',
                WarehouseSn: '',
                Location: '',
                description: '',
                creator_name: '',
                create_time: '',
                auditor_name: '',
                audit_time: '',
                status: -1
            },
            planModal: {
                datas: [],
                loading: false,
                loadingPrint: false,
                loadingExportExcel: false,
                loadingSaveDraft: false,
                loadingAudit: false
            },
            // 添加产品窗口
            addProductMd: {
                part_b_obj: {
                    systemType: '',
                    id: ''
                },
                title: '采购记录-添加产品',
                loading: false,
                alert: false
            },
            chooseOrgMd: {
                title: '',
                alert: false
            },
            isAddProductModalTableMax: false, // 是否是全屏模式，默认不是
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 57 : 0
        };
    },
    methods: {
        async orderPurchaseInfoAPIFn() {
            this.planModal.datas = [];
            let params = {
                partA_id: this.fatherRecord.partA,
                partB_id: this.fatherRecord.partB,
                po_so_inner_sn: this.fatherRecord.po_so_inner_sn,
                billsType: this.fatherRecord.billsType
            };
            await orderPurchaseInfoAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    // 表格内容赋值
                    this.sheetData.po_sn = info.po_sn;
                    this.sheetData.oname = info.partA_name;
                    this.sheetData.EmployeSn = info.buyer_inner_sn;
                    this.sheetData.DepartmentSn = info.department_inner_sn;
                    this.sheetData.expectAogDate = info.expect_aog_date;
                    this.sheetData.WarehouseSn = info.warehouse_inner_sn;
                    this.sheetData.Location = info.delivery_address;
                    this.sheetData.description = info.description;
                    this.sheetData.creator_name = info.creator_name;
                    this.sheetData.create_time = info.create_time;
                    this.sheetData.auditor_name = info.auditor_name;
                    this.sheetData.audit_time = info.audit_time;
                    // 明细内容赋值
                    this.planModal.datas = info.details.map((m) => {
                        m.status_name = productDetailStatusMap[m.status];
                        m.unitList = [{ value: m.unit_inner_sn, label: m.unit_name }];
                        return { ...m };
                    });
                    console.log(this.planModal.datas);
                    this.$refs.dataTable.loadData(this.planModal.datas);
                    console.log(this.$refs.dataTable.getTableData());
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        openChooseTb(value) {
            // 打开采购单位
            if (this.$refs.xTablePlanModalInfo) {
                this.$refs.xTablePlanModalInfo.blur();
            }
            this.pageTypeName = 7;
            this.relevantInstitutionsFocus = true;
            this.searchOrgName = typeof value === 'string' ? value : this.partB.oname || '';
            this.resetFocusFn();
            this.isParentKeyDownEnter = true;
            this.chooseOrgMd.alert = true;
            this.$Utils.globalDragCenterFn('choose_org_md');
            this.$nextTick(() => {
                if (this.$refs.chooseOrgMd) {
                    this.$refs.chooseOrgMd.setContractOrgCheckedFn();
                    this.$refs.chooseOrgMd.pageDataInit();
                }
            });
        },
        initPage(record) {
            this.fatherRecord = record;
            this.sheetData.status = record.p_status;
            this.orderPurchaseInfoAPIFn();
        },
        handleCancel() {
            this.$emit('onCancel');
        },
        TableClickFn() {},
        TableDblclickFn() {
            this.addProductMd.alert = true;
        },
        footerCellClassNameFn({ column }) {
            const redAry = ['money', 'discount_money', 'tax_amount', 'tax'];
            if (redAry.some((item) => item === column.property)) {
                return 'red bold';
            }
            if (column.property === 'index') {
                return 'total-bg bold';
            }
        },
        footerMethodFn({ columns }) {
            const { commafy } = this.$XEUtils;
            return [
                columns.map((column, columnIndex) => {
                    if (columnIndex === 0) {
                        return '合计';
                    }
                    // 金额
                    if (column.property === 'money') {
                        return commafy(this.money_total, { fixed: 2 });
                    }
                    if (column.property === 'discount_money') {
                        return commafy(this.discount_money_total, { fixed: 2 });
                    }
                    // 含税金额
                    if (column.property === 'tax_amount') {
                        return commafy(this.tax_amount_total, { fixed: 2 });
                    }
                    // 税额
                    if (column.property === 'tax') {
                        return commafy(this.tax_total, { fixed: 2 });
                    }
                })
            ];
        },
        /**************************************************************/
        /*                       添加产品弹窗区域                        */
        /**************************************************************/

        /**
         * 打开添加产品弹窗
         */
        openAddPro() {
            // 为添加产品窗口的PartB准备传递值
            const userBelong = parseInt(this.$cookie.get('userSystemType'));
            const billsType = userBelong === 1 ? 'HH' : userBelong === 2 ? 'DD' : 'VV';

            this.addProductMd.part_b_obj.systemType = this.$cookie.get('userSystemType');
            this.addProductMd.part_b_obj.otype = billsType;
            this.addProductMd.part_b_obj.org_id = this.$cookie.get('userbelong');
            this.addProductMd.part_b_obj.modalType = 8;
            // 打开窗口
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn('add_product');
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        addProductFn() {
            this.$refs.chooseProMd.returnProList();
        },
        /**
         * 获取回调商品
         */
        getProReturn(dea, ven) {
            this.onAddProClose();
            let arr = ven.concat(dea);
            let data = arr.map((item) => {
                return {
                    product_name: item.product_name,
                    product_inner_sn: item.product_inner_sn,
                    product_type: item.product_type,
                    specification: item.specification,
                    specification_inner_sn: item.specification_inner_sn,
                    vd_name: item.vendor_name || item.product_vendor_name,
                    vd_inner_sn: item.vendor_inner_sn || item.product_vendor_inner_sn,
                    batch_number: item.batch_number ? item.batch_number : '',
                    quantity: item.plan_quantity ? item.plan_quantity : '',
                    unit_inner_sn: item.unit_inner_sn ? item.unit_inner_sn : '',
                    unit_name: item.unit ? item.unit : '',
                    unitList: item.unit ? [{ value: item.unit_inner_sn }, { label: item.unit }] : [],
                    unit_price: '',
                    status: '0',
                    status_name: '草稿'
                };
            });
            this.table.tableData = this.table.tableData.concat(data);
        },
        /**
         * 添加产品弹窗取消按钮绑定方法
         */
        onAddProClose() {
            this.addProductMd.alert = false;
        },
        /**
         * 产品单位的渲染方式绑定方法
         * @param {Array} labels 选中的值名称，树形结构扁平化显示
         */
        unitRenderFn({ labels }) {
            // 只显示选择的值，不显示树形值

            return labels[labels.length - 1];
        },
        /**
         * 点击产品单位绑定的方法
         * @param {Object} record 当前选中行数据
         */
        unitClickFn(record, seq) {
            this.table.currentSelectedRow = record;
            this.table.currentSelectedSeq = seq;
            // 根据产品类型调用不同的方法
            parseFloat(record.product_type) === 1
                ? this.platformUnitTreeFn(record) // 厂商
                : this.dealerUnitTreeAPIFn(record); // 经销商
        },
        /**
         * 产品单位选中时绑定的方法
         * @param {Array} value 选中的值，树形结构扁平化显示
         */
        unitChangeFn(value, title) {
            const index = this.table.currentSelectedSeq - 1;
            this.table.tableData[index].unit_name = title[title.length - 1].label;
        },
        /**
         * 获取所有可用仓库，并且为仓库列表赋值
         */
        async getAllWarehouse() {
            const { code, list = [] } = await warehouseUsableTopListAPI();
            if (code === 0 && list.length !== 0) {
                this.allWarehouseList = list.map((item) => {
                    return {
                        value: item.warehouse_model_inner_sn,
                        title: item.name
                    };
                });
            }
        },
        /**
         * 【经销商】根据产品ID查询所有产品单位以树的方式返回
         * @param {Object} record 由调用者赋值，当前数据行
         */
        async dealerUnitTreeAPIFn(record) {
            await dealerUnitTreeAPI(record.vd_inner_sn, record.product_inner_sn, record.specification_inner_sn).then(
                (res) => {
                    if (parseFloat(res.code) === 0) {
                        let circle = (item, prevMeasure, parentName) => {
                            // 递归遍历
                            item.forEach((item) => {
                                item.label = `${item.name}(${item.measure}×${parentName})`;
                                item.value = item.unit_inner_sn;
                                item.min_measure = item.measure * prevMeasure;
                                if (item.children && item.children.length > 0) {
                                    circle(item.children, item.min_measure, item.name);
                                } else {
                                    item.children = null;
                                }
                            });
                        };
                        res.tree.forEach((item) => {
                            item.label = item.name;
                            item.value = item.unit_inner_sn;
                            item.min_measure = item.measure * 1;
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item.min_measure, item.name);
                            } else {
                                item.children = null;
                            }
                        });
                        if (res.tree.length) {
                            record.unitList = res.tree;
                        }
                    }
                }
            );
        },
        /**
         * 【厂商】 根据产品ID查询所有产品单位以树的方式返回
         * @param {Object} record 由调用者赋值，当前数据行
         */
        async platformUnitTreeFn(record) {
            await platformUnitTree(record.vd_inner_sn, record.product_inner_sn, record.specification_inner_sn).then(
                (res) => {
                    if (parseFloat(res.code) === 0) {
                        let circle = (item, prevMeasure, parentName) => {
                            // 递归遍历
                            item.forEach((item) => {
                                item.label = `${item.name}(${item.measure}×${parentName})`;
                                item.value = item.unit_inner_sn;
                                item.min_measure = item.measure * prevMeasure;
                                if (item.children && item.children.length > 0) {
                                    circle(item.children, item.min_measure, item.name);
                                } else {
                                    item.children = null;
                                }
                            });
                        };
                        res.tree.forEach((item) => {
                            item.label = item.name;
                            item.value = item.unit_inner_sn;
                            item.min_measure = item.measure * 1;
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item.min_measure, item.name);
                            } else {
                                item.children = null;
                            }
                        });
                        if (res.tree.length) {
                            record.unitList = res.tree;
                        }

                        // 为当前产品单位列表赋值
                        // const data = this.$Utils.TreeToCascader(res.tree, 'unit_inner_sn', 'name')
                        // const index = this.table.currentSelectedSeq - 1
                        // this.table.tableData[index].unitList = data
                        // console.log(data)
                    }
                }
            );
        }
    },
    mounted() {},
    computed: {
        displayRenderUnitFn({ labels }) {
            // 包装单位
            let label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
        },
        record_unit_price() {
            // 单价 = 含税单价 / (1 + 税率 / 100)
            return (record) => {
                const rate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
                let nextItem = parseFloat(record.tax_unit_price) / (1 + rate);
                if (!isNaN(nextItem)) {
                    if (nextItem < 0) nextItem = 0;
                    return nextItem.toFixed(4);
                    // return this.$XEUtils.commafy(nextItem, {fixed: 4})
                } else {
                    return '';
                }
            };
        },
        record_money_total() {
            // 金额 = 数量 * 单价
            return (record) => {
                const rate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100,
                    nextItem = (parseFloat(record.tax_unit_price) / (1 + rate)) * parseFloat(record.quantity);
                if (!isNaN(nextItem)) {
                    return nextItem.toFixed(2);
                    // return this.$XEUtils.commafy(nextItem, {fixed: 2})
                } else {
                    return '';
                }
            };
        },
        discount_unit_price_total() {
            return this.planModal.datas.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    nextItem = parseFloat(next.unit_price) * parseFloat(next.discount);
                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return prevItem + nextItem;
                } else {
                    return '';
                }
            }, 0);
        },
        record_discount_unit_price_total() {
            return (record) => {
                const nextItem = parseFloat(record.unit_price) * parseFloat(record.discount);
                if (!isNaN(nextItem)) {
                    return this.$XEUtils.commafy(nextItem, { fixed: 2 });
                } else {
                    return '';
                }
            };
        },
        discount_money_total() {
            return this.planModal.datas.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    nextItem = parseFloat(next.unit_price) * parseFloat(next.quantity) * parseFloat(next.discount);

                // 不计算已终止的产品
                const isStop = this._isStop(next.status);
                if (isStop) nextItem = 0;

                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return prevItem + nextItem;
                } else {
                    return '';
                }
            }, 0);
        },
        record_discount_money_total() {
            return (record) => {
                const nextItem =
                    parseFloat(record.unit_price) * parseFloat(record.quantity) * parseFloat(record.discount);
                if (!isNaN(nextItem)) {
                    return this.$XEUtils.commafy(nextItem, { fixed: 2 });
                } else {
                    return '';
                }
            };
        },
        tax_unit_price_total() {
            return this.planModal.datas.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    nextItem = parseFloat(next.unit_price) * parseFloat(next.discount) * (1 + parseFloat(next.rate));
                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return (prevItem + nextItem).toFixed(2);
                } else {
                    return '';
                }
            }, 0);
        },
        record_tax_amount_total() {
            // 含税金额 = 数量 * 含税单价
            return (record) => {
                let nextItem = this.$XEUtils.multiply(record.quantity, parseFloat(record.tax_unit_price));
                if (!isNaN(nextItem)) {
                    // nextItem = this.reNumber(nextItem)
                    return this.$XEUtils.commafy(nextItem, { fixed: 3 });
                } else {
                    return '';
                }
            };
        },
        record_tax_total() {
            // 税额 = 含税金额 - 金额
            return (record) => {
                const rate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100,
                    unitPrice = (
                        (parseFloat(record.tax_unit_price) / (1 + rate)) *
                        parseFloat(record.quantity)
                    ).toFixed(2);
                let nextItem =
                    (parseFloat(record.quantity) * parseFloat(record.tax_unit_price)).toFixed(2) -
                    parseFloat(unitPrice);
                if (!isNaN(nextItem)) {
                    // nextItem = this.reNumber(nextItem)
                    if (nextItem < 0) nextItem = 0;
                    return nextItem.toFixed(2);
                    // return this.$XEUtils.commafy(nextItem, {fixed: 2})
                } else {
                    return '';
                }
            };
        },
        // 合计：金额
        money_total() {
            const records = this.planModal.datas;
            if (!records) {
                return 0;
            }
            const { isNumber, round, add, multiply, divide } = this.$XEUtils;
            return round(
                this.planModal.datas.reduce((prevValue, currentItem) => {
                    const quantity = isNumber(currentItem.quantity) ? currentItem.quantity : 0;
                    const taxUnitPrice = isNumber(currentItem.tax_unit_price) ? currentItem.tax_unit_price : 0;
                    if (taxUnitPrice === 0) {
                        return prevValue;
                    }
                    const currentValue = multiply(
                        divide(taxUnitPrice, add(1, divide(currentItem.rate, 100))),
                        quantity
                    );
                    // 不计算已终止的产品
                    if (this._isStop(currentItem.status)) {
                        return prevValue;
                    }
                    return add(prevValue + currentValue);
                }, 0),
                2
            );
        },
        // 合计：含税金额
        tax_amount_total() {
            const records = this.planModal.datas;
            if (!records) {
                return 0;
            }
            const { isNumber, round, add } = this.$XEUtils;
            return round(
                records.reduce((prevValue, currentItem) => {
                    const currentValue = isNumber(currentItem.tax_amount) ? currentItem.tax_amount : 0;
                    // 不计算已终止的产品
                    if (this._isStop(currentItem.status)) {
                        return prevValue;
                    }
                    return add(prevValue, currentValue);
                }, 0),
                2
            );
        },
        // 合计：税额
        tax_total() {
            const records = this.planModal.datas;
            if (!records) {
                return 0;
            }
            const { isNumber, round, add, subtract, multiply, divide } = this.$XEUtils;
            return round(
                records.reduce((prevValue, currentItem) => {
                    const rate = isNumber(currentItem.rate) ? divide(currentItem.rate, 100) : 0;
                    const taxUnitPrice = isNumber(currentItem.tax_unit_price) ? currentItem.tax_unit_price : 0;
                    const quantity = isNumber(currentItem.quantity) ? currentItem.quantity : 0;
                    if (taxUnitPrice === 0) {
                        return prevValue;
                    }
                    const currentValue = multiply(quantity, subtract(taxUnitPrice, divide(taxUnitPrice, add(rate, 1))));
                    // 不计算已终止的产品
                    if (this._isStop(currentItem.status)) {
                        return prevValue;
                    }
                    return add(prevValue, currentValue);
                }, 0),
                2
            );
        },
        scrollYAddProductNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 110,
                bottomHeight = 116;
            return clientHeight - topHeight - bottomHeight;
        },
        orgName() {
            const employeeInfo = this.$cookie.get('EmployeeInfo');
            return (employeeInfo ? JSON.parse(employeeInfo).OrgName : '') || '';
        },
        ...mapState(['warehouseList'])
    }
};
</script>
<style lang="less" scoped>
@w: 24px;
.order-modal-list {
    //   position: absolute;
    //   left: @w;
    //   right: @w;
    //   bottom: 57px;
    display: flex;
    flex-direction: row;

    li {
        display: flex;
        flex-direction: row;
        padding: 24px 0;

        &:nth-of-type(1),
        &:nth-of-type(2) {
            flex: 1;

            .item-wrap {
                flex: 1;
            }
        }

        .item-wrap {
            display: flex;
            padding-right: 16px;

            .item {
                padding-bottom: 0;
                padding-right: @w;
            }
        }

        &:last-child {
            .item-wrap {
                &:last-child {
                    padding-right: 0;
                }
            }
        }
    }
}
.modal-form-input-scoped .modal-form-input .top-list .label {
    display: flex;
    width: auto;
    max-width: 94px;
}
// 顶部按钮多的样式重新调整左右间距
.scoped .btn-top-wrap .btn-list-wrap li,
.scoped .btn-top-wrap .btn-list-wrap li .ant-btn {
    margin-right: 10px;
}
// 高级搜索栏
ul#searchForm {
    li.search-form-item {
        width: auto;
        min-width: 280px;
        &.search-button {
            min-width: auto;
        }
        span.label,
        input.ant-input {
            display: inline-block;
        }
        span.label {
            min-width: 72px;
        }
    }
}
</style>
