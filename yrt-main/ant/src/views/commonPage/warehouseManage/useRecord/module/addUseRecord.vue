<template>
    <div>
        <a-modal
            :visible="visible"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td useRecord-modal-add global-drag"
            :width="1200"
            :zIndex="3"
            :closable="true"
            @cancel="handleCancel"
        >
            <div v-globalDrag="{ el: 'useRecord-modal-add' }" class="modal-header-wrap">
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
                    <span style="font-size: 24px; color: #444; margin-right: 5px">{{ org_title }}</span>
                    <span style="font-size: 24px; color: #444">使用记录</span>
                    <div v-if="btnName !== 'add'" style="position: absolute; right: 0; display: flex">
                        <span class="label" style="font-size: 14px; color: rgba(0, 0, 0, 0.65)">单据编号</span>
                        <a-input disabled v-model="sheetData.rv_sn" placeholder="" class="input" />
                    </div>
                </h6>
                <a-row>
                    <ul class="top-list">
                        <li class="item">
                            <span class="label">使用人</span>
                            <a-input :disabled="!isEditable" v-model="sheetData.user" placeholder="" class="input" />
                        </li>
                        <li class="item">
                            <span class="label">使用人病历号</span>
                            <a-input
                                :disabled="!isEditable"
                                v-model="sheetData.medical_no"
                                placeholder=""
                                class="input"
                            />
                        </li>
                        <li class="item">
                            <span class="label">使用人医保号</span>
                            <a-input
                                :disabled="!isEditable"
                                v-model="sheetData.health_no"
                                placeholder=""
                                class="input"
                            />
                        </li>
                        <li class="item">
                            <span class="label"><em class="em red">*</em>使用类型</span>
                            <a-select :disabled="!isEditable" v-model="sheetData.use_type" placeholder="">
                                <a-select-option :value="0"> 病人使用 </a-select-option>
                                <a-select-option :value="1"> 医生使用 </a-select-option>
                            </a-select>
                        </li>
                    </ul>
                </a-row>

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
                            height="330px"
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
                            <vxe-table-column
                                field="product_name"
                                title="产品"
                                width="15%"
                                :cell-render="{ name: 'product_name' }"
                            ></vxe-table-column>
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
                                        :disabled="!isEditable"
                                        v-model="row.batch_number"
                                        class="input"
                                        style="width: 100%"
                                        @click="batchNumberClickFn(row, seq)"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="quantity" title="数量" width="12%">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :disabled="!isEditable"
                                        :min="1"
                                        v-model="row.quantity"
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_price" title="价格" width="12%">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :disabled="!isEditable"
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
                            <vxe-table-column field="unit_inner_sn" title="包装单位" width="12%">
                                <template v-slot="{ row, seq }">
                                    <a-cascader
                                        :disabled="!isEditable"
                                        :allowClear="false"
                                        :options="row.unitList"
                                        v-model="row.unit_inner_sn"
                                        changeOnSelect
                                        :displayRender="unitRenderFn"
                                        placeholder=""
                                        style="width: 100%"
                                        @click="unitClickFn(row, seq)"
                                        @change="unitChangeFn"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="status_name" title="状态" width="8%">
                                <template v-slot="{ row }">
                                    <div>{{ row.status_name }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="default_health_care_sn" title="医保编号" width="8%">
                                <template v-slot="{ row }">
                                    <a-input
                                        :disabled="!isEditable"
                                        v-model="row.default_health_care_sn"
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="action" title="操作" width="120" :resizable="false" fixed="right">
                                <template v-slot="{ row, seq }">
                                    <ul class="action-btn-list-wrap" style="position: relative">
                                        <li v-if="parseInt(row.status) === 0 || row.status === 'DRAFT'">
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
                    <!-- <li>
                        <span class="label"
                              style="width:auto">摘要</span>
                        <a-input :disabled="!isEditable"
                                 v-model="sheetData.description"
                                 placeholder=""
                                 class="input" />
                    </li> -->
                    <li>
                        <div class="item-wrap">
                            <div class="item">
                                <span class="label" style="width: auto">经手人</span>
                                <span>{{ sheetData.proposer_name || '-' }}</span>
                            </div>
                            <!-- <div class="item">
                                <span class="label"
                                      style="width:auto">确认人</span>
                                <span>{{ sheetData.comfirm_name || '-' }}</span>
                            </div> -->
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
        <a-modal
            v-model="batchInfoModal.alert"
            :centered="true"
            :maskClosable="false"
            class="modal-form-input-scoped batch-modal global-drag"
        >
            <div v-globalDrag="{ el: 'batch-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ batchInfoModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="close" @click="batchInfoModal.alert = false"> 关闭 </a-button>
            </template>
            <div class="modal-form-input scoped">
                <div class="tab-table-wrap tab-table-overflowy">
                    <vxe-table
                        ref="batchXTable"
                        :keyboard-config="{ isArrow: true }"
                        size="small"
                        height="420"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        @cell-click="BatchTableClickFn"
                        align="center"
                        :data="batchInfoModal.batchDatas"
                        :loading="batchInfoModal.loading"
                    >
                        <vxe-table-column
                            field="batch_number"
                            title="批号"
                            width="15%"
                            :cell-render="{ name: 'batch_number' }"
                        ></vxe-table-column>
                        <vxe-table-column
                            field="produce_date"
                            title="生产日期"
                            width="10%"
                            :cell-render="{ name: 'produce_date' }"
                        >
                        </vxe-table-column>
                        <vxe-table-column
                            field="overdue_date"
                            title="失效日期"
                            width="10%"
                            :cell-render="{ name: 'overdue_date' }"
                        >
                        </vxe-table-column>
                        <vxe-table-column field="quantity" title="数量" width="10%" :cell-render="{ name: 'quantity' }">
                        </vxe-table-column>
                    </vxe-table>
                </div>
            </div>
        </a-modal>
    </div>
</template>

<script>
// import moment from 'moment'
import addProduct from '@/components/addProduct';

import {
    /** 获取使用记录信息 */
    useRecordListAPI,
    /** 添加使用记录信息 */
    useRecordAddAPI,
    /** 修改使用记录信息 */
    useRecordEditAPI,
    /** 获取厂商产品单位信息 */
    platformUnitTree,
    /** 获取经销商产品单位信息 */
    dealerUnitTreeAPI,
    /** 获取所有可用仓库信息 */
    warehouseUsableTopListAPI,
    /** 获取批号信息 */
    useRecordBatchListAPI
} from '@/service/pageAjax';

import { mapState } from 'vuex';

export default {
    name: 'addTransferSheetModal',
    props: {
        visible: {
            type: Boolean,
            default: false
        }
    },
    components: {
        addProduct
    },
    data() {
        return {
            // 模态框名称
            title: '',
            org_title: '',
            // 调拨单是否可以编辑
            isEditable: true,
            // 修改时加载的调拨单编号
            SearchSn: '',
            sheetData: {
                // 使用记录单据内部编号
                rv_inner_sn: '',
                // 使用记录单据编号
                rv_sn: '',
                // 使用人医保号
                health_no: '',
                // 使用人病历号
                medical_no: '',
                // 摘要
                description: '',
                // 机构ID
                org_id: '',
                // 仓库id
                warehouse_inner_sn: '',
                // 使用类型id
                use_type_inner_sn: '',
                // 使用类型名称
                use_type: '',
                // 使用人姓名
                user: '',
                // 经手人ID
                poster_inner_sn: '',
                // 经手人姓名
                poster_name: '',
                // 确认人id
                affirmant_inner_sn: '',
                // 确认人名称
                affirmant_name: '',
                // 表单生成时间
                make_time: ''
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
                    id: ''
                },
                title: '使用记录-添加产品',
                loading: false,
                alert: false
            },
            // 批号选择窗口
            batchInfoModal: {
                alert: false,
                title: '添加批号',
                batchDatas: [],
                loading: false
            },
            // 所有可用仓库数据
            allWarehouseList: [],
            // 有权限的仓库数据
            authWarehouseList: []
        };
    },
    methods: {
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
                    unit_inner_sn: item.unit_inner_sn.length ? item.unit_inner_sn[item.unit_inner_sn.length - 1] : '',
                    money: utils.commafy(utils.multiply(item.quantity, item.unit_price), { fixed: 2 }), // 金额(含税)
                    batch_number: item.batch_number.trim()
                };
            });

            let sheetInfo = {
                // Header所需数据
                org_name: JSON.parse(this.$cookie.get('EmployeeInfo')).OrgName,
                type_name: '使用记录',
                warehouse_name: this.allWarehouseList.find((f) => f.value === this.sheetData.warehouse_inner_sn).title,
                out_time: this.sheetData.make_time,
                av_sn: this.sheetData.rv_sn,
                // Footer所需数据
                making_people_name: this.sheetData.proposer_name,
                health_no: this.sheetData.health_no,
                medical_no: this.sheetData.medical_no,
                use_type: this.sheetData.use_type === 0 ? '病人使用' : '医生使用',
                user: this.sheetData.user
            };

            let routeUrl = this.$router.resolve({
                path: `/newprint/useRecord`
            });
            // 将打印所需的数据JSON序列化后放入localStorage
            window.localStorage.setItem(
                'printContent',
                JSON.stringify({
                    printData: prDatas,
                    printForm: sheetInfo,
                    printTitle: `使用记录`
                })
            );
            window.open(routeUrl.href, '_blank');
        },
        /**
         * 单据功能按钮- 取消
         */
        handleCancel() {
            this.$emit('onCancel');
        },
        /**
         * 单据功能按钮- 保存
         */
        handleSave() {
            if (this.isAdd) {
                this.useRecordAddAPIFn(false);
            } else {
                this.useRecordEditAPIFn(false);
            }
        },
        /**
         * 单据功能按钮- 保存并确认
         */
        handleSaveAndConfirm() {
            if (this.isAdd) {
                this.useRecordAddAPIFn(true);
            } else {
                this.useRecordEditAPIFn(true);
            }
        },

        /**************************************************************/
        /*                       接口区域                              */
        /**************************************************************/

        /**
         * 根据rv_inner_sn获取使用记录详情，并为sheetData和tableData赋值
         */
        async useRecordListAPIFn() {
            this.table.loading = true;
            let params = {
                // 当前登录机构
                hospital_inner_sn: this.$cookie.get('userbelong'),
                // 分页大小
                pageSize: 100,
                // 分页页数
                pageNum: 1,
                // 使用记录单号
                rv_inner_sn: this.SearchSn
            };
            await useRecordListAPI(params).then((res) => {
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

                    const data = res.rows;

                    if (data.length !== 0) {
                        this.sheetData.rv_inner_sn = data[0].rv_inner_sn;
                        this.sheetData.rv_sn = data[0].rv_sn;
                        this.sheetData.make_time = data[0].create_time;
                        this.sheetData.org_id = data[0].org_id;
                        this.sheetData.use_type = data[0].use_type;
                        this.sheetData.proposer_inner_sn = data[0].proposer_inner_sn;
                        this.sheetData.proposer_name = data[0].proposer_name;
                        this.sheetData.user = data[0].use_man;
                        this.sheetData.warehouse_inner_sn = this.authWarehouseList[0].value;
                        this.sheetData.warehouse_name = this.authWarehouseList[0].title;
                        this.sheetData.medical_no = data[0].medical_no;
                        this.sheetData.health_no = data[0].health_no;

                        this.table.tableData = data.map((item) => {
                            const statusCode = item.status;
                            switch (statusCode) {
                                case 'DRAFT':
                                    item.status_name = '草稿';
                                    break;
                                case 'CONFIRM':
                                    item.status_name = '确认';
                                    break;
                                case 'DISCARD':
                                    item.status_name = '已删除';
                                    break;
                            }
                            if (statusCode === 'DRAFT') {
                                this.isEditable = true;
                            } else {
                                this.isEditable = false;
                            }
                            item.unitList = item.unit_inner_sn
                                ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                : [];
                            item.unit_inner_sn = item.unit_inner_sn ? [item.unit_inner_sn] : [];
                            return {
                                ...item
                            };
                        });
                        this.table.loading = false;
                    }
                }
            });
        },
        /**
         * 新增使用记录数据
         */
        async useRecordAddAPIFn(isConfirm) {
            if (!this.validateSheetData()) {
                return;
            }
            let params = {
                description: this.sheetData.description,
                proposer_inner_sn: JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn,
                proposer_name: JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
                use_man: this.sheetData.user,
                use_type: this.sheetData.use_type,
                medical_no: this.sheetData.medical_no,
                health_no: this.sheetData.health_no,
                warehouse_inner_sn: this.authWarehouseList[0].value,
                warehouse_name: this.authWarehouseList[0].title,
                // create_time: this.$Utils.getNowFormatDate(),
                hospital_inner_sn: this.$cookie.get('userbelong'),
                details: this.table.tableData.map((item) => {
                    return {
                        ...item,
                        status: isConfirm ? 1 : item.status,
                        status_name: isConfirm ? '确认' : item.status_name,
                        unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                        unit_name: this.$Utils.UnitBracketsTrim(item.unit_name)
                    };
                })
            };
            if (isConfirm) {
                params.affirmant_name = JSON.parse(this.$cookie.get('EmployeeInfo')).Name;
                params.affirmant_inner_sn = JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn;
            }

            await useRecordAddAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.handleCancel();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /**
         * 修改使用记录信息
         */
        async useRecordEditAPIFn(isConfirm) {
            if (!this.validateSheetData()) {
                return;
            }
            let params = {
                rv_inner_sn: this.sheetData.rv_inner_sn,
                org_id: this.$cookie.get('userbelong'),
                description: this.sheetData.description,
                proposer_inner_sn: JSON.parse(this.$cookie.get('EmployeeInfo')).Employee_inner_sn,
                proposer_name: JSON.parse(this.$cookie.get('EmployeeInfo')).Name,
                use_man: this.sheetData.user,
                use_type: this.sheetData.use_type,
                medical_no: this.sheetData.medical_no,
                health_no: this.sheetData.health_no,
                warehouse_inner_sn: this.authWarehouseList[0].value,
                warehouse_name: this.authWarehouseList[0].title,
                // create_time: this.$Utils.getNowFormatDate(),
                hospital_inner_sn: this.$cookie.get('userbelong'),
                details: this.table.tableData.map((item) => {
                    return {
                        quantity: item.quantity,
                        batch_number: item.batch_number,
                        detail_inner_sn: item.detail_inner_sn,
                        default_health_care_sn: item.default_health_care_sn,
                        hospital_inner_sn: item.hospital_inner_sn,
                        rv_inner_sn: item.rv_inner_sn,
                        product_inner_sn: item.product_inner_sn,
                        product_name: item.product_name,
                        product_type: item.product_type,
                        specification: item.specification,
                        specification_inner_sn: item.specification_inner_sn,
                        status: isConfirm ? 1 : 0,
                        status_name: isConfirm ? '确认' : '草稿',
                        unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                        unit_name: this.$Utils.UnitBracketsTrim(item.unit_name),
                        unit_price: item.unit_price,
                        vd_inner_sn: item.vd_inner_sn,
                        vd_name: item.vd_name,
                        health_care_inner_sn: item.health_care_inner_sn
                    };
                })
            };

            await useRecordEditAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.handleCancel();
                } else {
                    this.$message.error(res.msg);
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
        /**
         * 获取批号【出库】
         */
        async batchRequestFn() {
            this.batchInfoModal.loading = true;
            // 由于调单没有单据类型，所以双方机构都为自身，生成单据类型
            const userBelong = parseInt(this.$cookie.get('userSystemType'));
            const billsType = userBelong === 1 ? 'HH' : userBelong === 2 ? 'DD' : 'VV';

            let params = {
                org_id: this.$cookie.get('userbelong'),
                warehouse_inner_sn: this.authWarehouseList[0].value,
                product_inner_sn: this.table.currentSelectedRow.product_inner_sn,
                product_type: this.table.currentSelectedRow.product_type,
                vd_inner_sn: this.table.currentSelectedRow.vd_inner_sn,
                specification_inner_sn: this.table.currentSelectedRow.specification_inner_sn,
                billsType: billsType
            };

            await useRecordBatchListAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // 为批号选择窗口赋值
                    this.batchInfoModal.batchDatas = res.list;
                    this.batchInfoModal.loading = false;
                } else {
                    this.$message.error(res.msg);
                }
            });
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
            if (
                this.table.tableData.length !== 0 &&
                this.table.currentSelectedRow &&
                this.table.currentSelectedRow.status !== 'DRAFT' &&
                parseInt(this.table.currentSelectedRow.status) !== 0
            ) {
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
            // 调用获取批号接口
            this.batchRequestFn();
            // 打开批号选择窗口
            this.batchInfoModal.alert = true;
            this.$Utils.globalDragCenterFn('batch-modal');
        },
        /**
         * 点击产品单位绑定的方法
         * @param {Object} record 当前选中行数据
         */
        unitClickFn(record, seq) {
            if (!this.isEditable) {
                return;
            }
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
        /**
         * 批号窗口列表点击绑定的方法
         * @param {Object} row 当前选中的行数据
         */
        BatchTableClickFn({ row }) {
            if (parseInt(row.quantity) === 0) {
                this.$warning({
                    title: '操作提示',
                    content: '此批号无库存，请重新选择',
                    okText: '关闭',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            // 防止批号重复
            let duplicatedFlag = false;
            this.table.tableData.forEach((v, index) => {
                if (
                    v.overdue_date !== '' &&
                    v.batch_number === row.batch_number &&
                    index + 1 !== this.table.currentSelectedSeq &&
                    v.product_inner_sn === this.table.currentSelectedRow.product_inner_sn &&
                    v.specification_inner_sn === this.table.currentSelectedRow.specification_inner_sn
                ) {
                    this.$warning({
                        title: '操作提示',
                        content: '产品批号不能重复，请重新选择',
                        okText: '关闭',
                        centered: true,
                        maskClosable: true
                    });
                    duplicatedFlag = true;
                }
            });
            if (duplicatedFlag) {
                return;
            }
            // 为当前产品批号赋值
            const index = this.table.currentSelectedSeq - 1;
            this.table.tableData[index].batch_number = row.batch_number;
            this.table.tableData[index].produce_date = row.produce_date;
            this.table.tableData[index].overdue_date = row.overdue_date;

            // 选中批号数据后关闭批号选择窗口
            this.batchInfoModal.alert = false;
            this.batchInfoModal.batchDatas = [];
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
                    ...item,
                    product_name: item.product_name,
                    product_inner_sn: item.product_inner_sn,
                    product_type: item.product_type,
                    specification: item.specification,
                    specification_inner_sn: item.specification_inner_sn,
                    vd_name: item.vendor_name || item.product_vendor_name,
                    vd_inner_sn: item.vendor_inner_sn || item.product_vendor_inner_sn,
                    batch_number: item.batch_number ? item.batch_number : '',
                    quantity: item.plan_quantity ? item.plan_quantity : '',
                    // unit_inner_sn: item.common_use_unit_inner_sn ? [item.common_use_unit_inner_sn] : '',
                    // unit_name: item.common_use_unit ? item.common_use_unit : '',
                    // unitList: item.common_use_unit ? [{value: item.common_use_unit_inner_sn, label: item.common_use_unit}] : [],
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

        /**************************************************************/
        /*                        单据区域                              */
        /**************************************************************/

        /**
         * 验证单据信息
         */
        validateSheetData() {
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
                    if (!v.quantity || v.quantity === 0) {
                        this.$warning({
                            title: '操作提示',
                            content: `请填写【${v.product_name}】的【数量】`,
                            okText: '关闭',
                            centered: true,
                            maskClosable: true
                        });
                        flag = false;
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
            this.sheetData.rv_inner_sn = '';
            this.sheetData.rv_sn = '';
            this.sheetData.health_no = '';
            this.sheetData.medical_no = '';
            this.sheetData.description = '';
            this.sheetData.org_id = '';
            this.sheetData.warehouse_inner_sn = '';
            this.sheetData.use_type_inner_sn = '';
            this.sheetData.use_type = '';
            this.sheetData.user = '';
            this.sheetData.poster_inner_sn = '';
            this.sheetData.poster_name = '';
            this.sheetData.affirmant_inner_sn = '';
            this.sheetData.affirmant_name = '';
            this.sheetData.make_time = '';
            this.table.tableData = [];
        },
        /**
         * 初始化页面
         * @param add 由父组件赋值，是否为新增
         */
        initPage(add) {
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
            const orgName = JSON.parse(this.$cookie.get('loginDatas')).EmployeeInfo.OrgName;
            const depName = JSON.parse(this.$cookie.get('loginDatas')).Departments[0].Name;
            this.org_title = orgName + ' ' + depName + ' ';

            if (!this.isAdd) {
                // 编辑调拨单
                this.useRecordListAPIFn();
                // 设定模态框标题
                this.title = '修改 - 使用记录';
            } else {
                // 新增调拨单
                // 自动填充经手人为当前操作人
                this.sheetData.proposer_name = JSON.parse(this.$cookie.get('EmployeeInfo')).Name;
                this.sheetData.proposer_inner_sn = this.$cookie.get('userInnerSn');
                this.sheetData.use_type = 0;
                // 设定模态框标题
                this.title = '新增 - 使用记录';
                this.isEditable = true;
            }
        }
    },
    computed: {
        ...mapState(['warehouseList'])
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
