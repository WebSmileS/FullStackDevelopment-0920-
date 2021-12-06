<template>
    <div>
        <a-modal
            :visible="visible"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td warehouseTransfer-modal-add global-drag"
            :width="1200"
            :zIndex="3"
            :closable="true"
            @cancel="handleCancel"
        >
            <div v-globalDrag="{ el: 'warehouseTransfer-modal-add' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ title }}
                </h6>
            </div>
            <template slot="footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button @click="handlePrint">打印</a-button>
                    </li>
                    <li>
                        <a-button @click="handleCancel">取消</a-button>
                        <a-button @click="handleSave" :disabled="!isEditable">保存</a-button>
                        <a-button @click="handleSaveAndConfirm" :disabled="!isEditable">保存并确认</a-button>
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
                    <span style="font-size: 24px; color: #444">调拨单</span>
                    <div style="position: absolute; right: 0; display: flex">
                        <span class="label" style="font-size: 14px; color: rgba(0, 0, 0, 0.65)"
                            ><em class="em red">*</em>单据编号</span
                        >
                        <a-input disabled v-model="sheetData.av_sn" placeholder="" class="input" />
                    </div>
                </h6>
                <ul class="top-list" style="'width:60%'">
                    <li class="item">
                        <span class="label"><em class="em red">*</em>源仓库</span>
                        <a-select
                            :disabled="!isEditable || isFromDispatch"
                            :options="source_warehouse_list"
                            v-model="sheetData.source_warehouse_inner_sn"
                            placeholder=""
                        />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>目标仓库</span>
                        <a-select
                            :disabled="!isEditable || isFromDispatch"
                            :options="target_warehouse_list"
                            v-model="sheetData.target_warehouse_inner_sn"
                            placeholder=""
                        />
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
                            :data="table.tableData"
                            :loading="table.loading"
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
                            align="center"
                        >
                            <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                                <template v-slot="{ row, seq }">
                                    <div>
                                        <span>{{ seq }}</span>
                                    </div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="product_name" title="产品" width="15%">
                                <template v-slot="{ row }">
                                    <span v-if="row.product_inner_sn">
                                        {{ row.product_name }}
                                    </span>
                                    <a-input-number
                                        v-else
                                        ref="productName"
                                        v-model="row.product_name"
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="10%"
                                :cell-render="{ name: 'specification' }"
                            >
                            </vxe-table-column>
                            <vxe-table-column
                                field="vd_name"
                                title="厂商/经销商"
                                width="10%"
                                :cell-render="{ name: 'vd_name' }"
                            >
                            </vxe-table-column>
                            <vxe-table-column field="batch_number" title="批号" width="10%">
                                <template v-slot="{ row, seq }">
                                    <a-input
                                        :disabled="!row.isEditable"
                                        v-model="row.batch_number"
                                        class="input"
                                        style="width: 100%"
                                        @click="batchNumberClickFn(row, seq)"
                                        readonly="readonly"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="allot_quantity" title="数量" width="12%">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :disabled="!row.isEditable"
                                        :min="0"
                                        v-model="row.allot_quantity"
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_inner_sn" title="包装单位" width="12%">
                                <template v-slot="{ row, seq }">
                                    <a-cascader
                                        :disabled="!row.isEditable"
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
                            <vxe-table-column field="unit_price" title="价格" width="12%">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :disabled="!row.isEditable"
                                        :min="0"
                                        :formatter="
                                            (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')
                                        "
                                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                        v-model="row.unit_price"
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="produce_date" title="生产日期" width="12%">
                                <template v-slot="{ row }">
                                    <a-date-picker
                                        disabled
                                        v-model="row.produce_date"
                                        :format="format"
                                        placeholder=""
                                        allowClear
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="overdue_date" title="失效日期" width="12%">
                                <template v-slot="{ row }">
                                    <a-date-picker
                                        disabled
                                        v-model="row.overdue_date"
                                        :format="format"
                                        placeholder=""
                                        allowClear
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column disabled field="status_name" title="状态" width="8%">
                                <template v-slot="{ row }">
                                    <div>{{ row.status_name }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="action" title="操作" width="120" :resizable="false" fixed="right">
                                <template v-slot="{ row, seq }">
                                    <ul class="action-btn-list-wrap" style="position: relative">
                                        <li v-if="row.status === 'DRAFT'">
                                            <a-tooltip class="tooltip" placement="top" title="删除">
                                                <i
                                                    @click="onTableDelete(seq)"
                                                    class="yrt yrt-shanchu-copy cancel del_ico"
                                                ></i>
                                            </a-tooltip>
                                        </li>
                                    </ul>
                                </template>
                            </vxe-table-column>
                            <template v-slot:empty>
                                <div @dblclick="TableDblclickFn" class="dblclick-wrap">
                                    <span>双击添加产品</span>
                                </div>
                            </template>
                        </vxe-grid>
                    </div>
                </div>
                <ul class="order-modal-list">
                    <li>
                        <span class="label" style="width: auto">摘要</span>
                        <a-input :disabled="!isEditable" v-model="sheetData.description" placeholder="" class="input" />
                    </li>
                    <li>
                        <div class="item-wrap">
                            <div class="item">
                                <span class="label" style="width: auto">经手人</span>
                                <span>{{ sheetData.poster_name || '-' }}</span>
                            </div>
                            <div class="item">
                                <span class="label" style="width: auto">确认人</span>
                                <span>{{ sheetData.comfirm_name || '-' }}</span>
                            </div>
                        </div>
                        <div class="item-wrap">
                            <div class="item">
                                <span class="label" style="width: auto">生成时间</span>
                                <span>{{ sheetData.make_time || '-' }}</span>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 添加产品弹窗 -->
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
        <odo-batch-selecter
            :visible="batchSelecterVisible"
            :orderInfo="orderInfo"
            :orderProducts="orderProducts"
            :orderType="grnAndOdoOrderType.ALLOTODO.value"
            :targetProduct="batchSelecterTargetProduct"
            @select="onBatchSelect"
            @close="batchSelecterVisible = false"
        ></odo-batch-selecter>
    </div>
</template>

<script>
import { grnAndOdoOrderType } from '@/config/constants';
// import moment from 'moment'
import addProduct from '@/components/addProduct';
import { mapState } from 'vuex';
import moment from 'moment';
import OdoBatchSelecter from '@/components/OdoBatchSelecter.vue';

import {
    /** 获取调拨单信息 */
    getWarehouseTransferSheetAPI,
    /** 添加调拨单信息 */
    addWarehouseTransferSheetAPI,
    /** 修改调拨单信息 */
    editWarehouseTransferSheetAPI,
    /** 获取仓库信息 */
    warehouseUsableTopListAPI,
    /** 获取厂商产品单位信息 */
    platformUnitTree,
    /** 获取经销商产品单位信息 */
    dealerUnitTreeAPI
} from '@/service/pageAjax';

export default {
    name: 'addTransferSheetModal',
    components: {
        addProduct,
        OdoBatchSelecter
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        isTransferOut: {
            type: Boolean,
            default: true
        },
        /**
         * 是否为调拨
         */
        isTransfer: {
            type: Boolean
        }
    },
    data() {
        return {
            // 模态框名称
            title: '',
            // 调拨单是否可以编辑
            isEditable: true,
            // 是否是发放单生成
            isFromDispatch: false,
            // 是否已开启键盘监听器
            isKeyboardEventListenerActive: false,
            // 修改时加载的调拨单编号
            SearchSn: '',
            format: 'YYYY-MM-DD',
            sheetData: {
                // 调拨单单据内部编号
                av_inner_sn: '',
                // 调拨单单据编号
                av_sn: '',
                // 摘要
                description: '',
                // 期望完成调拨日期
                expect_date: '',
                // 机构ID
                org_id: '',
                // 调拨人ID
                poster_inner_sn: '',
                // 调拨人姓名
                poster_name: '',
                // 源仓库id
                source_warehouse_inner_sn: '',
                // 目标仓库id
                target_warehouse_inner_sn: '',
                // 确认人名称
                comfirm_name: '',
                // 表单生成时间
                make_time: '',
                // 发放单生成调拨单时，传递的申领单编号
                voucher_inner_sn: ''
            },
            table: {
                // 表格数据
                tableData: [],
                // 表格加载中
                loading: false,
                // 当前选中行
                currentSelectedRow: {},
                // 当前选中行序列
                currentSelectedSeq: 0
            },
            // 添加产品窗口
            addProductMd: {
                part_b_obj: {
                    systemType: '',
                    id: '',
                    warehouseDatas: [] // 仓库列表
                },
                title: '调拨单-添加产品',
                loading: false,
                alert: false
            },
            // 源仓库的仓库数据
            source_warehouse_list: [],
            // 目标仓库的仓库数据
            target_warehouse_list: [],
            // 所有可用仓库数据
            allWarehouseList: [],
            // 有权限的仓库数据
            authWarehouseList: [],
            batchSelecterVisible: false,
            batchSelecterTargetProduct: null,
            orderInfo: null,
            orderProducts: []
        };
    },
    computed: {
        ...mapState(['warehouseList']),
        grnAndOdoOrderType() {
            return grnAndOdoOrderType;
        }
    },
    methods: {
        onBatchSelect(batch) {
            const listRef = this.table.tableData;
            let i = 0;
            while (listRef[i]) {
                if (this.batchSelecterTargetProduct._XID === listRef[i]._XID) {
                    listRef[i].batch_number = batch.batch_number;
                    listRef[i].produce_date = batch.produce_date ? moment(batch.produce_date) : null;
                    listRef[i].overdue_date = batch.overdue_date ? moment(batch.overdue_date) : null;
                    listRef[i].buying_price = batch.average_price;
                    listRef[i].quantityMax = batch.quantity;
                    break;
                }
                i++;
            }
        },
        /**************************************************************/
        /*                       表格功能按钮区域                        */
        /**************************************************************/

        /**
         * 单据功能按钮- 打印
         */
        handlePrint() {
            const utils = this.$XEUtils;
            let prDatas = this.table.tableData.map((item, index) => {
                return {
                    ...item,
                    index: index + 1,
                    vendor_name: item.vd_name,
                    quantity: item.allot_quantity,
                    unit_inner_sn: item.unit_inner_sn.length ? item.unit_inner_sn[item.unit_inner_sn.length - 1] : '',
                    money: utils.commafy(utils.multiply(item.quantity, item.unit_price), { fixed: 2 }), // 金额(含税)
                    produce_date: item.produce_date ? moment(item.produce_date).format(this.format) : '',
                    overdue_date: item.overdue_date ? moment(item.overdue_date).format(this.format) : '',
                    batch_number: item.batch_number.trim()
                };
            });

            let sheetInfo = {
                // Header所需数据
                org_name: JSON.parse(this.$cookie.get('EmployeeInfo')).OrgName,
                type_name: '调拨单',
                source_warehouse_name: this.allWarehouseList.find(
                    (f) => f.value === this.sheetData.source_warehouse_inner_sn
                ).title,
                target_warehouse_name: this.allWarehouseList.find(
                    (f) => f.value === this.sheetData.target_warehouse_inner_sn
                ).title,
                out_time: this.sheetData.make_time,
                av_sn: this.sheetData.av_sn,
                // Footer所需数据
                making_people_name: this.sheetData.poster_name,
                description: this.sheetData.description
            };

            let routeUrl = this.$router.resolve({
                path: `/newprint/warehouseTransfer`
            });
            // 将打印所需的数据JSON序列化后放入localStorage
            window.localStorage.setItem(
                'printContent',
                JSON.stringify({
                    printData: prDatas,
                    printForm: sheetInfo,
                    printTitle: `调拨单`
                })
            );
            window.open(routeUrl.href, '_blank');
        },
        /**
         * 单据功能按钮- 取消
         */
        handleCancel() {
            this.$emit('onCancel');
            this.removeEventProductDocumentFn();
        },
        /**
         * 单据功能按钮- 保存
         */
        handleSave() {
            if (this.isAdd) {
                this.addWarehouseTransferSheetAPIFn(false);
            } else {
                this.editWarehouseTransferSheetAPIFn(false);
            }
        },
        /**
         * 单据功能按钮- 保存并确认
         */
        handleSaveAndConfirm() {
            if (this.isAdd) {
                this.addWarehouseTransferSheetAPIFn(true);
            } else {
                this.editWarehouseTransferSheetAPIFn(true);
            }
        },
        /**
         * 单据功能按钮- 发放
         * @abstract 只在医院发放时使用
         * @deprecated
         */
        handleDispatch() {
            this.addWarehouseTransferSheetAPIFn(true);
        },
        /**
         * 新增成功后的回调函数
         */
        onAddFinished() {
            this.$emit('onAddFinished');
        },

        /**************************************************************/
        /*                       接口区域                              */
        /**************************************************************/

        /**
         * 根据av_inner_sn获取调拨单详情，并为sheetData和tableData赋值
         */
        async getWarehouseTransferSheetAPIFn() {
            this.table.loading = true;
            await getWarehouseTransferSheetAPI(this.SearchSn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (res.info === null) {
                        this.$warning({
                            title: '操作提示',
                            content: '您没有此单据的权限',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        this.handleCancel();
                    }
                    this.table.loading = false;

                    this.sheetData.av_inner_sn = res.info.av_inner_sn;
                    this.sheetData.av_sn = res.info.av_sn;
                    this.sheetData.make_time = res.info.create_time;
                    this.sheetData.description = res.info.description;
                    this.sheetData.expect_date = res.info.expect_date;
                    this.sheetData.org_id = res.info.org_id;
                    this.sheetData.poster_inner_sn = res.info.poster_inner_sn;
                    this.sheetData.poster_name = res.info.poster_name;
                    this.sheetData.comfirm_inner_sn = res.info.affirmant_inner_sn;
                    this.sheetData.comfirm_name = res.info.affirmant_name;
                    this.sheetData.source_warehouse_inner_sn = res.info.source_warehouse_inner_sn;
                    this.sheetData.target_warehouse_inner_sn = res.info.target_warehouse_inner_sn;
                    if (!this.authWarehouseList.find((f) => f.value === res.info.source_warehouse_inner_sn)) {
                        this.authWarehouseList.push({
                            title: res.info.source_warehouse_name,
                            value: res.info.source_warehouse_inner_sn
                        });
                    }
                    // this.sheetData.comfirm_name = res.info.av_inner_sn
                    this.isFromDispatch = res.info.apply_voucher_inner_sn !== null;
                    this.isEditable = res.info.waiting_confirm;
                    this.table.tableData = res.info.details.map((item) => {
                        const statusCode = item.status;
                        switch (statusCode) {
                            case 'DRAFT':
                                item.status_name = '草稿';
                                break;
                            case 'CONFIRM':
                                item.status_name = '确认';
                                break;
                            case 'OUTPUT':
                                item.status_name = '已出库';
                                break;
                            case 'COMPLETED':
                                item.status_name = '完结';
                                break;
                            case 'DISCARD':
                                item.status_name = '作废';
                                break;
                        }
                        if (statusCode === 'DRAFT') {
                            item.isEditable = true;
                        } else {
                            item.isEditable = false;
                        }
                        item.unitList = item.unit_inner_sn
                            ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                            : [];
                        item.unit_inner_sn = item.unit_inner_sn ? [item.unit_inner_sn] : [];
                        item.produce_date = item.produce_date && moment(item.produce_date);
                        item.overdue_date = item.overdue_date && moment(item.overdue_date);
                        return {
                            ...item
                        };
                    });
                    this.table.loading = false;
                }
            });
        },
        /**
         * 新增调拨单数据
         */
        async addWarehouseTransferSheetAPIFn(isConfirm) {
            if (!this.validateSheetData()) {
                return;
            }
            let params = {
                description: this.sheetData.description,
                org_id: this.$cookie.get('userbelong'),
                poster_inner_sn: JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn,
                poster_name: JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
                source_warehouse_inner_sn: this.sheetData.source_warehouse_inner_sn,
                target_warehouse_inner_sn: this.sheetData.target_warehouse_inner_sn,
                expect_date: this.$Utils.getNowFormatDate(),
                details: this.table.tableData.map((item) => {
                    item.unit_inner_sn = Array.isArray(item.unit_inner_sn)
                        ? item.unit_inner_sn[item.unit_inner_sn.length - 1]
                        : item.unit_inner_sn;
                    item.produce_date = item.produce_date && moment(item.produce_date).format(this.format);
                    item.overdue_date = item.overdue_date && moment(item.overdue_date).format(this.format);
                    item.unit_name = this.$Utils.UnitBracketsTrim(item.unit_name);
                    if (isConfirm) {
                        item.status = 'CONFIRM';
                        item.status_name = '确认';
                    }

                    return {
                        ...item
                    };
                })
            };
            if (isConfirm) {
                params.affirmant_name = JSON.parse(this.$cookie.get('EmployeeInfo')).Name;
                params.affirmant_inner_sn = JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn;
            }

            if (this.isFromDispatch) {
                params.apply_voucher_inner_sn = this.sheetData.voucher_inner_sn;
            }

            await addWarehouseTransferSheetAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.handleCancel();
                    this.onAddFinished();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /**
         * 修改调拨单信息
         */
        async editWarehouseTransferSheetAPIFn(isConfirm) {
            if (!this.validateSheetData() && this.isEditable) {
                return;
            }
            let params = {
                av_inner_sn: this.sheetData.av_inner_sn,
                description: this.sheetData.description,
                org_id: this.$cookie.get('userbelong'),
                poster_inner_sn: JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn,
                poster_name: JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
                source_warehouse_inner_sn: this.sheetData.source_warehouse_inner_sn,
                target_warehouse_inner_sn: this.sheetData.target_warehouse_inner_sn,
                expect_date: this.$Utils.getNowFormatDate(),
                details: this.table.tableData.map((item) => {
                    if (item.status !== 'DISCARD') {
                        item.status = isConfirm ? 'CONFIRM' : 'DRAFT';
                        item.status_name = isConfirm ? '确认' : '草稿';
                    }
                    return {
                        allot_quantity: item.allot_quantity,
                        batch_number: item.batch_number,
                        detail_inner_sn: item.detail_inner_sn,
                        product_inner_sn: item.product_inner_sn,
                        product_name: item.product_name,
                        product_type: item.product_type,
                        sort_number: item.sort_number,
                        specification: item.specification,
                        specification_inner_sn: item.specification_inner_sn,
                        status: item.status,
                        status_name: item.status_name,
                        comfirm_name: JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
                        comfirm_inner_sn: JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn,
                        unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                        unit_name: this.$Utils.UnitBracketsTrim(item.unit_name),
                        unit_price: item.unit_price,
                        buying_price: item.buying_price,
                        vd_inner_sn: item.vd_inner_sn,
                        vd_name: item.vd_name,
                        produce_date: item.produce_date && moment(item.produce_date).format(this.format),
                        overdue_date: item.overdue_date && moment(item.overdue_date).format(this.format)
                    };
                })
            };

            if (isConfirm) {
                params.affirmant_name = JSON.parse(this.$cookie.get('EmployeeInfo')).Name;
                params.affirmant_inner_sn = JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn;
            }

            await editWarehouseTransferSheetAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (isConfirm) {
                        this.$emit('onConfirmFinished');
                    }
                    this.handleCancel();
                }
            });
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
                /* ************ 仓库列表规则 **************
                 * ——————————————————————————————————————
                 *       |     源仓库    |    目标仓库    |
                 * ——————————————————————————————————————
                 * 待调出 |   有权限仓库   |    所有仓库    |
                 * ——————————————————————————————————————
                 * 待调入 |    所有仓库    |   有权限仓库   |
                 * ——————————————————————————————————————
                 */
                this.source_warehouse_list = this.isTransferOut ? this.authWarehouseList : this.allWarehouseList;
                this.target_warehouse_list = this.isTransferOut ? this.allWarehouseList : this.authWarehouseList;
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
                        let sn = [];
                        let circle = (item, prevItem) => {
                            // 递归遍历
                            item.forEach((item) => {
                                item.label = `${item.name}(${item.measure}×${prevItem.name})`;
                                item.value = item.unit_inner_sn;
                                item.min_measure = item.measure * prevItem.min_measure;
                                item.format_unit_inner_sn = [].concat(prevItem.format_unit_inner_sn);
                                item.format_unit_inner_sn.push(item.unit_inner_sn);
                                if (record.unit_inner_sn[0] === item.unit_inner_sn) {
                                    sn = item.format_unit_inner_sn;
                                }
                                if (item.children && item.children.length > 0) {
                                    circle(item.children, item);
                                } else {
                                    item.children = null;
                                }
                            });
                        };
                        res.tree.forEach((item) => {
                            item.label = item.name;
                            item.value = item.unit_inner_sn;
                            item.min_measure = item.measure * 1;
                            item.format_unit_inner_sn = [item.unit_inner_sn];
                            if (record.unit_inner_sn[0] === item.unit_inner_sn) {
                                sn = item.format_unit_inner_sn;
                            }
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item);
                            } else {
                                item.children = null;
                            }
                        });
                        if (res.tree.length) {
                            record.unitList = res.tree;
                            if (this.isAdd) {
                                // 赋默认值
                                const curUnit = record.unitList[0];
                                record.unit_inner_sn = [curUnit.value];
                                record.unit_name = curUnit.label;
                                record.min_measure = curUnit.min_measure;
                                record.isLoadingUnit = true;
                                this.$refs.dataTable.reloadRow(record, record, 'unit');
                            } else {
                                record.unit_inner_sn = sn;
                            }
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
                        let sn = [];
                        let circle = (item, prevItem) => {
                            // 递归遍历
                            item.forEach((item) => {
                                item.label = `${item.name}(${item.measure}×${prevItem.name})`;
                                item.value = item.unit_inner_sn;
                                item.min_measure = item.measure * prevItem.min_measure;
                                item.format_unit_inner_sn = [].concat(prevItem.format_unit_inner_sn);
                                item.format_unit_inner_sn.push(item.unit_inner_sn);
                                if (record.unit_inner_sn[0] === item.unit_inner_sn) {
                                    sn = item.format_unit_inner_sn;
                                }
                                if (item.children && item.children.length > 0) {
                                    circle(item.children, item);
                                } else {
                                    item.children = null;
                                }
                            });
                        };
                        res.tree.forEach((item) => {
                            item.label = item.name;
                            item.value = item.unit_inner_sn;
                            item.min_measure = item.measure * 1;
                            item.format_unit_inner_sn = [item.unit_inner_sn];
                            if (record.unit_inner_sn[0] === item.unit_inner_sn) {
                                sn = item.format_unit_inner_sn;
                            }
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item);
                            } else {
                                item.children = null;
                            }
                        });
                        if (res.tree.length) {
                            record.unitList = res.tree;
                            if (this.isAdd) {
                                // 赋默认值
                                const curUnit = record.unitList[0];
                                record.unit_inner_sn = [curUnit.value];
                                record.unit_name = curUnit.label;
                                record.min_measure = curUnit.min_measure;
                                record.isLoadingUnit = true;
                                this.$refs.dataTable.reloadRow(record, record, 'unit');
                            } else {
                                console.log(sn);
                                record.unit_inner_sn = sn;
                            }
                        }

                        // 为当前产品单位列表赋值
                        // const data = this.$Utils.TreeToCascader(res.tree, 'unit_inner_sn', 'name')
                        // const index = this.table.currentSelectedSeq - 1
                        // this.table.tableData[index].unitList = data
                        // console.log(data)
                    }
                }
            );
        },
        /**************************************************************/
        /*                       表格区域                              */
        /**************************************************************/

        /**
         * 单击表格列触发的方法
         */
        TableClickFn({ row, seq }) {
            this.table.currentSelectedRow = row;
            this.table.currentSelectedSeq = seq;
        },
        /**
         * 双击表格列触发的方法
         */
        TableDblclickFn() {
            if (!this.isEditable || this.isFromDispatch) {
                return;
            }
            this.openAddPro();
        },
        /**
         * 表格点击删除图标绑定方法
         * @param seq 点击按钮所处的行数，等于数据中的index+1
         */
        onTableDelete(seq) {
            this.table.tableData.splice(seq - 1, 1);
        },
        /**
         * 点击产品批号框绑定的方法
         */
        batchNumberClickFn(record) {
            this.table.currentSelectedRow = record;
            // 获取批号前需要选择源仓库
            if (!this.sheetData.source_warehouse_inner_sn) {
                this.$warning({
                    title: '提示',
                    content: '请先选择源仓库',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            const orgType = this.$cookie.get('userSystemType');
            this.batchSelecterVisible = true;
            this.batchSelecterTargetProduct = {
                ...record,
                vendor_inner_sn: record.vd_inner_sn
            };
            this.orderInfo = {
                odo_inner_sn: this.sheetData.av_inner_sn,
                billsType: orgType === 1 ? 'HH' : orgType === '2' ? 'DD' : 'VV',
                warehouse_inner_sn: this.sheetData.source_warehouse_inner_sn
            };
            this.orderProducts = this.table.tableData.map((_) => ({
                ..._,
                quantity: _.allot_quantity,
                vendor_inner_sn: _.vd_inner_sn
            }));
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
         * 产品单位的渲染方式绑定方法
         * @param {Array} labels 选中的值名称，树形结构扁平化显示
         */
        unitRenderFn({ labels }) {
            // 只显示选择的值，不显示树形值

            return labels[labels.length - 1];
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
            this.addProductMd.part_b_obj.warehouseDatas = this.isTransferOut
                ? this.authWarehouseList
                : this.allWarehouseList;
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
                    vd_name: item.vendor_name,
                    vd_inner_sn: item.vendor_inner_sn,
                    batch_number: item.batch_number ? item.batch_number : null,
                    allot_quantity: '',
                    unit_inner_sn: item.unit_inner_sn,
                    unit_name: item.unit_name,
                    unitList: item.unitList,
                    unit_price: '',
                    produce_date: null,
                    overdue_date: null,
                    status: 'DRAFT',
                    status_name: '草稿',
                    isEditable: true
                };
            });
            this.table.tableData = this.table.tableData.concat(data);
        },
        /**
         * 获取由发放单生成的数据
         * dispatchInfo
         *  - source_warehouse_inner_sn
         *  - target_warehouse_inner_sn
         */
        dispatchInit(dispatchInfo, DataInfo) {
            this.addEventProductDocumentFn();
            this.sheetData.source_warehouse_inner_sn = dispatchInfo.source_warehouse_inner_sn;
            this.sheetData.target_warehouse_inner_sn = dispatchInfo.target_warehouse_inner_sn;
            this.sheetData.voucher_inner_sn = dispatchInfo.voucher_inner_sn;

            this.isFromDispatch = true;

            const arr = DataInfo;
            let data = arr.map((item) => {
                if (parseFloat(item.unit_inner_sn) !== parseFloat(item.min_unit_inner_sn)) {
                    // 申领量 包装单位 === 最小包装单位 不要显示括号的最小包装单位
                    item.ready_quantity_compare = item.ready_quantity_compare / item.min_unit_quantity;
                }
                return {
                    product_name: item.product_name,
                    product_inner_sn: item.product_inner_sn,
                    product_type: item.product_type,
                    specification: item.specification,
                    specification_inner_sn: item.specification_inner_sn,
                    vd_name: item.product_vendor_name,
                    vd_inner_sn: item.product_vendor_inner_sn,
                    allot_quantity: item.ready_quantity_compare,
                    batch_number: item.batch_number,
                    unit_inner_sn: item.unit_inner_sn ? item.unit_inner_sn : '',
                    unit_name: item.unit ? item.unit : '',
                    unitList: item.unit ? [{ value: item.unit_inner_sn }, { label: item.unit }] : [],
                    unit_price: '',
                    produce_date: '',
                    overdue_date: '',
                    status: 'DRAFT',
                    status_name: '草稿',
                    isEditable: true
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

        /**************************************************************/
        /*                        单据区域                              */
        /**************************************************************/

        /**
         * 验证单据信息
         */
        validateSheetData() {
            if (!this.sheetData.source_warehouse_inner_sn || this.sheetData.source_warehouse_inner_sn === '') {
                this.$warning({
                    title: '操作提示',
                    content: '请填写源仓库',
                    okText: '关闭',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (!this.sheetData.target_warehouse_inner_sn || this.sheetData.target_warehouse_inner_sn === '') {
                this.$warning({
                    title: '操作提示',
                    content: '请填写目标仓库',
                    okText: '关闭',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (this.table.tableData.length === 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请添加产品',
                    okText: '关闭',
                    centered: true,
                    maskClosable: true
                });
                return false;
            } else {
                let flag = true;
                this.table.tableData.every((v) => {
                    if (!v.allot_quantity || v.allot_quantity === 0) {
                        this.$warning({
                            title: '操作提示',
                            content: `请填写【${v.product_name}】的【数量】`,
                            okText: '关闭',
                            centered: true,
                            maskClosable: true
                        });
                        flag = false;
                        return;
                    }
                    if (v.quantityMax !== undefined && v.quantityMax !== null && v.allot_quantity > v.quantityMax) {
                        this.$warning({
                            title: '操作提示',
                            content: `【${v.product_name}】的数量（${v.allot_quantity}）不能超过最大可用数量（${v.quantityMax}）`,
                            okText: '关闭',
                            centered: true,
                            maskClosable: true
                        });
                        flag = false;
                        return;
                    }
                    if (!v.unit_inner_sn || v.unit_inner_sn === '') {
                        this.$warning({
                            title: '操作提示',
                            content: `请填写【${v.product_name}】的【包装单位】`,
                            okText: '关闭',
                            centered: true,
                            maskClosable: true
                        });
                        flag = false;
                        return;
                    }
                    if (v.buying_price === null || v.buying_price === undefined) {
                        this.$warning({
                            title: '操作提示',
                            content: `请选择【${v.product_name}】的【批号】`,
                            okText: '关闭',
                            centered: true,
                            maskClosable: true
                        });
                        flag = false;
                        return;
                    }
                    if (v.unit_price === undefined || v.unit_price === null || !(Number(v.unit_price) >= 0)) {
                        this.$warning({
                            title: '操作提示',
                            content: `请填写【${v.product_name}】的【价格】`,
                            okText: '关闭',
                            centered: true,
                            maskClosable: true
                        });
                        flag = false;
                    }
                });
                if (flag) {
                    return true;
                } else {
                    return false;
                }
            }
        },

        /**
         * 设定当前查询调拨单编号
         * @abstract 由父组件调用，仅编辑时调用
         */
        setSearchSn(sn) {
            this.SearchSn = sn;
        },
        /**
         * 初始化所有单据数据
         */
        clearPage() {
            this.sheetData.av_inner_sn = '';
            this.sheetData.av_sn = '';
            this.sheetData.description = '';
            this.sheetData.expect_date = '';
            this.sheetData.org_id = '';
            this.sheetData.poster_inner_sn = '';
            this.sheetData.poster_name = '';
            this.sheetData.source_warehouse_inner_sn = '';
            this.sheetData.target_warehouse_inner_sn = '';
            this.sheetData.comfirm_name = '';
            this.sheetData.make_time = '';
            this.table.tableData = [];
            this.isFromDispatch = false;
        },
        /**
         * 初始化页面
         * @param add 由父组件赋值，是否为新增
         */
        initPage(add) {
            this.addEventProductDocumentFn();
            // 由父组件赋值，是否为新增
            this.isAdd = add;
            // 清空页面数据
            this.clearPage();
            // 获取所有可用仓库
            this.getAllWarehouse();
            // 获取当前有权限的仓库
            const warehouseListStore = this.$store.state.warehouseList;
            if (warehouseListStore.length !== 0) {
                this.authWarehouseList = warehouseListStore.map((v) => {
                    return {
                        value: v.warehouse_model_inner_sn,
                        title: v.title
                    };
                });
            }

            if (!this.isAdd) {
                // 编辑调拨单
                this.getWarehouseTransferSheetAPIFn();
                // 设定模态框标题
                this.title = '修改 - 调拨单';
            } else {
                // 新增调拨单
                // 自动填充经手人为当前操作人
                this.sheetData.poster_name = JSON.parse(this.$cookie.get('userSystemType')).UserName;
                this.sheetData.poster_inner_sn = this.$cookie.get('userInnerSn');
                // 设定模态框标题
                this.title = '新增 - 调拨单';
                this.isEditable = true;
            }
        },

        /**************************************************************/
        /*                      键盘操作区域                            */
        /**************************************************************/

        addEventProductDocumentFn() {
            if (this.isKeyboardEventListenerActive) {
                return;
            }
            document.addEventListener('keydown', this.openAddProduct, false);
            this.isKeyboardEventListenerActive = true;
        },
        removeEventProductDocumentFn() {
            if (!this.isKeyboardEventListenerActive) {
                return;
            }
            document.removeEventListener('keydown', this.openAddProduct, false);
            this.isKeyboardEventListenerActive = false;
        },
        async openAddProduct(e) {
            const keyCode = e.keyCode;

            // Esc 键
            // if (keyCode === 27) { // Esc
            //     this.addProductZoomFn()
            //     return
            // }

            if (!this.isEditable) return; // 草稿和正式状态才能编辑

            if (keyCode === 13) {
                // 回车键
                const dataTable = this.$refs.dataTable;

                if (!dataTable) return;

                const tableData = dataTable.getTableData().tableData;
                const existProductEmptyIndex = tableData.findIndex((f) => !f.product_inner_sn);

                dataTable.focus();
                if (existProductEmptyIndex <= -1) {
                    // 需要创建空数据
                    const record = { product_name: '' };
                    const { row: newRow } = await dataTable.insertAt(record, -1);
                    await dataTable.setCurrentRow(newRow);
                    this.$refs.productName.focus();
                } else {
                    // 说明有需要添加的空数据，直接调出产品添加页面
                    if (!this.addProductMd.alert) {
                        this.addProductMd.part_b_obj.isKeyCode = true;
                        this.addProductMd.part_b_obj.keyCodeVal = tableData[existProductEmptyIndex].product_name || '';
                        this.TableDblclickFn();
                    } else {
                        this.addProductFn();
                    }
                }
            }
        }
    }
};
</script>

<style lang="less" scoped>
.scoped .bot-search-form-wrap {
    padding: 0;
    max-height: 300px;
    border: none;
    .bot-hight-search-wrap {
        padding: 0 0px 12px;
        .form li:last-child {
            width: auto;
            .ant-btn {
                margin-right: 24px;
            }
        }

        .bot-hight-search-container {
            display: flex;
            align-items: center;

            .bot-hight-search-checkbox {
                margin-left: 10px;
                margin-right: 20px;
            }

            .bot-hight-search-select {
                display: flex;
                align-items: center;
                margin-left: 10px;

                .label {
                    margin-right: 15px;
                }

                /deep/ .ant-select-selection {
                    overflow-y: auto;
                }
            }

            .bot-hight-search-button {
                display: flex;
                .ant-btn {
                    min-width: 76px;
                    height: 24px;
                }
                .ant-btn:first-child {
                    margin-right: 24px;
                }
            }
        }
    }

    .bot-product-item {
        padding: 10px;
        position: absolute;
        left: 575px;
        text-align: left;
    }
}
@w: 24px;
.order-modal-list {
    position: absolute;
    left: @w;
    right: @w;
    bottom: 57px;
    display: flex;
    flex-direction: column;
    li {
        display: flex;
        flex-direction: row;
        align-items: center;
        padding-top: 24px;

        .item-wrap {
            display: flex;
            align-items: center;
            padding-right: 50px;
            .item {
                padding-bottom: 0;
                padding-right: @w;
            }
        }

        &:last-child {
            padding-bottom: 24px;
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
.order-modal-list li {
    padding-top: 18px;
}
.box {
    width: 100%;
    top: 40px;
    bottom: 0px;
    overflow: hidden;
    position: absolute;
}
.boxtop {
    width: calc(100% - 2px);
    height: calc(50% - 3px);
    padding: 8px;
    float: left;
}
.resize {
    width: 100%;
    height: 5px;
    cursor: w-resize;
    cursor: s-resize;
    float: left;
    border-bottom: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
}
.mid {
    width: calc(100% - 2px);
    padding: 0px 8px;
    float: left;
    height: 50%;
}
.handlebox {
    width: 100%;
    top: 40px;
    bottom: 0px;
    overflow: hidden;
    position: absolute;
}

.handleBoxtop {
    width: calc(100% - 2px);
    height: calc(50% - 3px);
    padding: 8px;
    float: left;
}
.handleResize {
    width: 100%;
    height: 5px;
    cursor: w-resize;
    cursor: s-resize;
    float: left;
    border-bottom: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
}
.handleBoxdown {
    width: calc(100% - 2px);
    padding: 0px 8px;
    float: left;
    height: 50%;
}
.dropdownButton {
    float: right;
    margin: 0px 10px 10px 0px;
}

.modal-fullscreen {
    display: inline-block;
    position: absolute;
    right: 56px;
    top: 0px;
    width: 56px;
    font-size: 16px;
    font-style: normal;
    text-align: center;
    text-transform: none;
    text-rendering: auto;
    height: 48px;
    line-height: 48px;
    color: #fff;
}
</style>
