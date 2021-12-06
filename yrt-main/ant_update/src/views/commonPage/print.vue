<template>
    <div ref="printText" :class="['print-content', 'print' + printType]">
        <!-- 图片 start -->
        <div v-html="printContent" v-if="printType === 1"></div>
        <!-- 图片 end -->
        <!-- 多张图片 start -->
        <div v-html="printContent" v-if="printType === 11"></div>
        <!-- 多张图片 end -->
        <!-- 耗材申领 start -->
        <div v-if="printType === 2" class="ant-modal scoped modal-form-input-scoped" style="padding: 18px">
            <div class="modal-form-input for-single">
                <h6 class="title">{{ printTitle }}</h6>
                <ul class="top-list">
                    <li style="justify-content: space-between">
                        <div class="item">
                            <span class="label" style="width: 80px">单据编号：</span>
                            {{ editInfoFormObj.voucher_sn || editInfoFormObj.gv_sn }}
                        </div>
                        <div class="item">
                            <span class="label">部门：</span>
                            <span class="input">{{ editInfoFormObj.department_name }}</span>
                        </div>
                        <div class="item">
                            <span class="label">仓库：</span>
                            <span class="input">{{ editInfoFormObj.warehouse_inner_sn_name }}</span>
                        </div>
                        <div class="item" v-if="printCaseType === 1">
                            <span class="label">申领人：</span>
                            <span class="input">{{ editInfoFormObj.proposer_name || '-' }}</span>
                        </div>
                        <div class="item" v-if="printCaseType === 1">
                            <span class="label">期望于</span>
                            {{ editInfoFormObj.expect_date }}
                            <span class="before-issuing">之前发放</span>
                        </div>
                        <div class="item" v-if="printCaseType === 2">
                            <span style="width: 85px" class="label">发放仓库：</span>
                            <span class="input">{{ editInfoFormObj.warehouse_name || '-' }}</span>
                        </div>
                        <div class="item" v-if="printCaseType === 2">
                            <span class="label">发放人：</span>
                            <span class="input">{{ editInfoFormObj.employeeInfoName || '-' }}</span>
                        </div>
                        <div class="item" v-if="printCaseType === 2">
                            <span class="label">领取人：</span>
                            <span class="input">{{ editInfoFormObj.receiverName || '-' }}</span>
                        </div>
                        <div class="item" v-if="printCaseType === 2">
                            <span class="label" style="width: 80px">发放时间：</span>
                            <span class="input">{{ editInfoFormObj.time || '-' }}</span>
                        </div>
                        <div class="item" v-if="printCaseType === 3">
                            <span class="label" style="width: 80px">制单人：</span>
                            <span class="input">{{ editInfoFormObj.employeeInfoName || '-' }}</span>
                        </div>
                        <div class="item" v-if="printCaseType === 3">
                            <span class="label" style="width: 80px">生成时间：</span>
                            <span class="input">{{ editInfoFormObj.time || '-' }}</span>
                        </div>
                        <div class="item" v-if="printCaseType === 3">
                            <span class="label" style="width: 80px">审核人：</span>
                            <span class="input">{{ editInfoFormObj.time || '-' }}</span>
                        </div>
                        <div class="item" v-if="printCaseType === 3">
                            <span class="label" style="width: 80px">审核时间：</span>
                            <span class="input">{{ editInfoFormObj.time || '-' }}</span>
                        </div>
                    </li>
                </ul>
                <div
                    ref="applyTable"
                    class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                    style="position: static"
                >
                    <a-table :columns="addInfoDraftColumns" :dataSource="addInfoDatas" :pagination="false">
                        <!-- 索引 重写 -->
                        <div slot="index" slot-scope="text, record, index">
                            {{ index + 1 }}
                        </div>
                        <!-- 厂商/经销商 重写 -->
                        <div slot="sys_type" slot-scope="text, record">
                            {{ record.vendor_name }}
                        </div>
                        <!-- 申领数量【计划数量】 重写 -->
                        <div slot="plan_quantity" slot-scope="text, record">
                            {{ record.plan_quantity }}
                        </div>
                        <!-- 实发数量 重写 【除了草稿状态显示】 -->
                        <div slot="achieve_quantity" slot-scope="text, record">
                            {{ record.achieve_quantity }}
                        </div>
                    </a-table>
                </div>
                <ul class="bottom-info">
                    <li>
                        <span>摘要:</span>
                        {{ editInfoFormObj.description }}
                    </li>
                    <li v-if="printCaseType === 1">
                        <span>生成时间：</span>
                        {{ editInfoFormObj.create_time || '-' }}
                    </li>
                </ul>
            </div>
        </div>
        <!-- 耗材申领 end -->

        <!-- 询价单 start -->
        <div v-if="printType === 4" class="ant-modal scoped modal-form-input-scoped" style="padding: 18px">
            <div class="modal-form-input">
                <ul class="top-list">
                    <li class="title">
                        <h6 style="text-align: center; padding: 16px; display: block">{{ orderTitle }}</h6>
                    </li>
                </ul>
                <ul class="top-list">
                    <li>
                        <div class="item">
                            <span class="label" style="width: 80px">单据编号：</span>
                            {{ editInfoFormObj.ilps_sn }}
                        </div>
                        <div class="item" style="margin-right: 36px">
                            <span class="label" style="width: 140px">相关机构：</span>
                            {{ orgName }}
                        </div>
                        <div class="item">
                            <span class="label">联系人：</span>
                            {{ editInfoFormObj.contacts }}
                        </div>
                        <div class="item">
                            <span class="label">电话：</span>
                            {{ editInfoFormObj.phone }}
                        </div>
                    </li>
                </ul>
                <div class="tab-table-wrap cur-overflow-hidden tab-table-overflowy">
                    <a-table :columns="enquiryColumns" :dataSource="proTableData" :pagination="false"> </a-table>
                </div>
                <ul>
                    <li class="item item-description" style="display: block; text-align: left; margin-top: 20px">
                        摘要：{{ editInfoFormObj.description }}
                    </li>
                </ul>
            </div>
        </div>
        <!-- 询价单 end -->
        <!-- 采购-计划单 start -->
        <div v-if="printType === 5" class="ant-modal scoped modal-form-input-scoped" style="padding: 18px">
            <div class="modal-form-input" style="height: 580px">
                <h6 style="font-size: 24px; color: #444; padding-bottom: 24px; text-align: center; margin-bottom: 0">
                    采购计划单
                </h6>
                <ul class="top-list" style="justify-content: space-between">
                    <li style="width: 55%">
                        <div class="item">
                            <span class="label" style="width: 80px">单据编号：</span>
                            {{ planModalForm.ppl_sn }}
                        </div>
                        <div class="item">
                            <span class="label">部门：</span>
                            {{ planModalForm.department_name }}
                        </div>
                        <div class="item">
                            <span class="label" style="width: auto">制单人：</span>
                            <span>{{ planModalForm.creator_name || '-' }}</span>
                        </div>
                        <div class="item">
                            <span class="label" style="width: auto">生成时间：</span>
                            <span>{{ planModalForm.create_time || '-' }}</span>
                        </div>
                    </li>
                    <li style="width: 40%">
                        <div class="item">
                            <span class="label" style="width: auto">审核人：</span>
                            <span>{{ planModalForm.auditor_name || '-' }}</span>
                        </div>
                        <div class="item">
                            <span class="label" style="width: auto">审核时间：</span>
                            <span>{{ planModalForm.audit_time || '-' }}</span>
                        </div>
                    </li>
                </ul>
                <div
                    ref="applyTable"
                    class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                    style="padding-bottom: 0"
                >
                    <a-table :columns="planModalStopColumns" :dataSource="planModalDatas" :pagination="false">
                        <!-- NO. 重写 -->
                        <div slot="index" slot-scope="text, record, index">
                            {{ index + 1 }}
                        </div>
                    </a-table>
                </div>
                <ul
                    class="top-list"
                    style="justify-content: space-between; padding-top: 24px; bottom: 78px; left: 0; right: 0"
                >
                    <li style="width: 100%; text-align: left">摘要：{{ planModalForm.description }}</li>
                </ul>
            </div>
        </div>
        <!-- 采购-计划单 end -->

        <!-- 销售订单 start -->
        <div
            v-if="printType === 6 && myOrderPropObj.pageType === 1"
            class="ant-modal scoped modal-form-input-scoped"
            style="padding: 18px"
        >
            <div class="modal-form-input" style="height: 580px">
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
                    <span style="font-size: 24px; color: #444">销售订单</span>
                </h6>
                <ul class="top-list" :style="'width:80%'">
                    <li class="item">
                        <span class="label" style="width: 80px">单据编号:</span>
                        <span> {{ planModalForm.so_sn }}</span>
                    </li>
                    <li class="item">
                        <span class="label" style="width: 80px">相关机构:</span>
                        <span>{{ planModalForm.partA_name }}</span>
                    </li>
                    <li class="item">
                        <span class="label" style="width: 120px">期望到货日期:</span>
                        <span>{{ planModalForm.expect_aog_date }}</span>
                    </li>
                </ul>
                <div
                    ref="applyTable"
                    class="tab-table-wrap cur-overflow-hidden"
                    style="padding-bottom: 0; margin-top: 0"
                >
                    <a-table :columns="orderColumns" :dataSource="planModalDatas" :pagination="false" rowKey="key" />
                </div>
                <p style="display: flex; margin-top: 10px; text-align: left">
                    <span class="label" style="width: 60px">摘要:</span>
                    <span>{{ planModalForm.description }}</span>
                </p>
                <ul class="order-modal-list">
                    <ul class="top-list">
                        <li class="item">
                            <span class="label">制单人</span>
                            <span>{{ planModalForm.creator_name || '-' }}</span>
                        </li>
                        <li class="item">
                            <span class="label">生成时间</span>
                            <span>{{ planModalForm.create_time || '-' }}</span>
                        </li>
                        <li class="item">
                            <span class="label">审核人</span>
                            <span>{{ planModalForm.auditor_name || '-' }}</span>
                        </li>
                        <li class="item">
                            <span class="label">审核时间</span>
                            <span>{{ planModalForm.audit_time || '-' }}</span>
                        </li>
                    </ul>
                </ul>
            </div>
        </div>
        <!-- 销售订单 end -->

        <!-- 验货单 start -->
        <div
            v-if="printType === 7"
            class="ant-modal scoped modal-form-input-scoped"
            style="padding: 18px; overflow: auto"
        >
            <div class="modal-form-input">
                <ul class="top-list">
                    <li class="title">
                        <h6 style="text-align: center; padding: 16px; display: block">{{ orderTitle }}</h6>
                    </li>
                </ul>
                <ul class="top-list">
                    <li>
                        <div class="item">
                            <span class="label" style="width: 80px">单据编号：</span>
                            {{ editInfoFormObj.pp_sn }}
                        </div>
                        <div class="item" style="margin-right: 36px">
                            <span class="label" style="width: 140px">相关机构：</span>
                            {{ editInfoFormObj.organization_name }}
                        </div>
                        <div class="item">
                            <span class="label">部门：</span>
                            {{ editInfoFormObj.department_name }}
                        </div>
                    </li>
                </ul>
                <div class="tab-table-wrap cur-overflow-hidden tab-table-overflowy">
                    <a-table :columns="enquiryColumns" :dataSource="proTableData" :pagination="false"> </a-table>
                </div>
                <ul style="margin: 15px 0">
                    <li class="item item-description" style="display: block; text-align: left">
                        备注：{{ editInfoFormObj.description }}
                    </li>
                </ul>
                <ul class="top-list">
                    <li>
                        <div class="item" style="margin-right: 36px">
                            <span class="label">制单人：</span>
                            {{ editInfoFormObj.making_people_name }}
                        </div>
                        <div class="item" style="margin-right: 36px">
                            <span class="label">验货人：</span>
                            {{ editInfoFormObj.inspector_name }}
                        </div>
                        <div class="item">
                            <span class="label" style="width: 100px">验货时间：</span>
                            {{ editInfoFormObj.inspect_time }}
                        </div>
                        <!--<div class="item">-->
                        <!--    <span class="label">审核人：</span>-->
                        <!--    {{ editInfoFormObj.auditor_name }}-->
                        <!--</div>-->
                    </li>
                </ul>
            </div>
        </div>
        <!-- 验货单 end -->
        <!-- 收货单 start -->
        <div v-if="printType === 10" class="print-container">
            <h6 class="print-container-title">收货单</h6>
            <a-row :gutter="30" class="print-container-form">
                <a-col>
                    <span class="label">单据编号:</span>
                    {{ planModalForm.rn_sn }}
                </a-col>
                <a-col>
                    <span class="label">相关机构:</span>
                    <span>{{ planModalForm.organization_name }}</span>
                </a-col>
                <a-col>
                    <span class="label">部门:</span>
                    <span>{{ planModalForm.department_name }}</span>
                </a-col>
                <a-col>
                    <span class="label">约定交货日期:</span>
                    <span>{{ planModalForm.agreed_delivery_date }}</span>
                </a-col>
                <a-col>
                    <span class="label">送货人:</span>
                    <span>{{ planModalForm.deliverymen_name }}</span>
                </a-col>
            </a-row>
            <a-table
                class="print-container-table"
                :columns="orderColumns"
                :dataSource="planModalDatas"
                :pagination="false"
            >
                <!-- NO. 重写 -->
                <div slot="index" slot-scope="text, record, index">
                    {{ index + 1 }}
                </div>
            </a-table>
            <a-row :gutter="30" class="print-container-form">
                <a-col class="desc" :span="24" style="margin-bottom: 15px">
                    <span class="label">备注:</span>
                    <span>{{ planModalForm.description }}</span>
                </a-col>
                <a-col>
                    <span class="label">制单人:</span>
                    <span>{{ planModalForm.making_people_name || '-' }}</span>
                </a-col>
                <a-col>
                    <span class="label">收货人:</span>
                    <span>{{ planModalForm.consignee_name || '-' }}</span>
                </a-col>
                <a-col>
                    <span class="label">收货时间:</span>
                    <span>{{ planModalForm.receiving_time || '-' }}</span>
                </a-col>
            </a-row>
        </div>
        <!-- 收货单 end -->
        <!-- 销售单 start -->
        <div v-if="printType === 8" class="print-container">
            <h6 class="print-container-title">销售单</h6>
            <a-row class="print-container-form">
                <a-col :span="6">
                    <span class="label">相关机构:</span>
                    {{ planModalForm.organization_name }}
                </a-col>
                <a-col :span="6">
                    <span class="label">销售员:</span>
                    <span>{{ planModalForm.seller_name }}</span>
                </a-col>
                <a-col :span="6">
                    <span class="label">销售部门:</span>
                    <span>{{ planModalForm.department_name }}</span>
                </a-col>
                <a-col :span="6">
                    <span class="label">单据编号:</span>
                    <span>{{ planModalForm.so_sn }}</span>
                </a-col>
            </a-row>
            <a-row class="print-container-form">
                <a-col :span="6">
                    <span class="label">采购方联系人:</span>
                    {{ planModalForm.purchaser_linkman }}
                </a-col>
                <a-col :span="6">
                    <span class="label">联系电话:</span>
                    <span>{{ planModalForm.purchaser_phone }}</span>
                </a-col>
                <a-col :span="6">
                    <span class="label">销售方联系人:</span>
                    <span>{{ planModalForm.seller_linkman }}</span>
                </a-col>
                <a-col :span="6">
                    <span class="label">联系电话:</span>
                    <span>{{ planModalForm.seller_phone }}</span>
                </a-col>
            </a-row>
            <a-table
                class="print-container-table"
                :columns="orderColumns"
                :dataSource="planModalDatas"
                :pagination="false"
            >
                <!-- NO. 重写 -->
                <div slot="index" slot-scope="text, record, index">
                    {{ index + 1 }}
                </div>
            </a-table>
            <a-row class="print-container-form">
                <a-col :span="6">
                    <span class="label">收款日期:</span>
                    {{ planModalForm.collection_date }}
                </a-col>
                <a-col :span="6">
                    <span class="label">制单人:</span>
                    <span>{{ planModalForm.creator_name }}</span>
                </a-col>
                <a-col :span="6">
                    <span class="label">生成时间:</span>
                    <span>{{ planModalForm.create_time }}</span>
                </a-col>
                <a-col :span="6">
                    <span class="label">审核人:</span>
                    <span>{{ planModalForm.auditor_name }}</span>
                </a-col>
                <a-col class="desc" :span="24">
                    <span class="label">摘要:</span>
                    <span>{{ planModalForm.description }}</span>
                </a-col>
            </a-row>
        </div>
        <!-- 销售单 end -->
        <!-- 储位分配单 start -->
        <div v-if="printType === 9" class="print-container">
            <h6 class="print-container-title">储位分配单</h6>
            <a-row :gutter="30" class="print-container-form">
                <a-col>
                    <span class="label">单据编号:</span>
                    {{ planModalForm.sn }}
                </a-col>
                <a-col>
                    <span class="label">仓库:</span>
                    <span>{{ planModalForm.warehouse_name }}</span>
                </a-col>
                <a-col>
                    <span class="label">作业人:</span>
                    <span>{{ planModalForm.warehouse_operator_name }}</span>
                </a-col>
                <a-col>
                    <span class="label">制单人:</span>
                    <span>{{ planModalForm.create_name }}</span>
                </a-col>
                <a-col>
                    <span class="label">制单时间:</span>
                    <span>{{ planModalForm.create_time }}</span>
                </a-col>
            </a-row>
            <a-table
                class="print-container-table"
                :columns="orderColumns"
                :dataSource="planModalDatas"
                :pagination="false"
            >
                <!-- NO. 重写 -->
                <div slot="index" slot-scope="text, record, index">
                    {{ index + 1 }}
                </div>
            </a-table>
            <a-row class="print-container-form">
                <a-col class="desc" :span="24">
                    <span class="label">备注:</span>
                    <span>{{ planModalForm.description }}</span>
                </a-col>
            </a-row>
        </div>
        <!-- 储位分配单 end -->
    </div>
</template>

<script>
// import Vue from 'vue'
export default {
    name: 'print',
    el: '.print-content',
    data() {
        return {
            printContent: '',
            printData: {}, // 来自localStorage的printContent数据，包含了全部打印所需数据
            orderTitle: '',
            editInfoFormObj: {},
            addInfoColumns: {},
            customRowFn: {},
            printType: 1,
            addInfoDatas: {},
            myListPropObj: 0,
            printpageType: 1,
            architectureAllTrees: {},
            addInfoDraftColumns: {},
            orgName: '',
            enquiryColumns: {},
            unitList: [],
            proTableData: {},
            planModalForm: {}, // 采购计划单Form
            planModalDatas: {}, // 采购计划单table数据
            planModalStopColumns: {}, // 采购计划单table表头
            planOrderStatus: [], // 采购计划单状态
            myOrderPropObj: {},
            orderColumns: {}, // 订单头
            printTitle: '',
            printCaseType: 0, // 耗材单据类型
            totalObj: {} // 合计对象
        };
    },
    methods: {
        printFn() {
            window.print();
        },
        formatStatus(value, name) {
            const planListStatus = ['草稿', '正式', '已审核', '已确认', '已终止', '已完结'],
                planOrderStatus = ['处理中', '已完全处理', '已计划采购', '已终止处理'];
            if (name === 'planList') {
                return planListStatus[value];
            } else if (name === 'planOrder') {
                return planOrderStatus[value];
            }
        }
    },
    filters: {
        filtersTwo(value) {
            // 保留两位小数
            if (typeof value === 'number') {
                return this.$XEUtils.commafy(value, { fixed: 2 });
            }
            return '';
        }
    },
    created() {
        let widthOrdList = [];
        let printContent = window.localStorage.getItem('printContent');
        printContent = JSON.parse(printContent);
        // printData 打印所需的全部数据
        this.printData = printContent;
        // printType  1、图片√；2、耗材申领√；3、出入库单√; 4、询价单√；5、计划单√；6、订单√；7、验货单7；11、多张图片
        this.printType = printContent.printType;
        const totalobj = printContent.totalObj || {};
        const { moneyTotal = 0, discountMoneyTotal = 0, taxAmountTotal = 0, taxTotal = 0 } = totalobj;
        switch (printContent.printType) {
            case 1: // 1、图片
                this.printContent = printContent.printData;
                break;
            case 2: // 2、耗材申领
                this.printContent = 'printChange';
                this.addInfoDraftColumns = printContent.printTable;
                this.editInfoFormObj = printContent.printForm;
                this.addInfoDatas = printContent.printData;
                this.printTitle = printContent.printTitle;
                this.printCaseType = printContent.printCaseType;
                break;
            case 3: // 3、出入库单
                console.warn('出入库打印单已迁移至新的打印模块（newprint）');
                break;
            case 4: // 4、询价单
                this.printContent = 'printChange';
                this.orderTitle = printContent.printTitle;
                if (printContent.pageType === 0) {
                    this.enquiryColumns = printContent.printTable
                        .splice(0, printContent.printTable.length - 1)
                        .map((item) => {
                            if (item.fixed) {
                                delete item.fixed;
                            }
                            return item;
                        });
                } else {
                    this.enquiryColumns = printContent.printTable;
                }
                this.editInfoFormObj = printContent.printForm;
                this.proTableData = printContent.printData.map((item) => {
                    return {
                        ...item,
                        rate: `${item.rate}%`,
                        discount_price: item.discount_price ? item.discount_price.toFixed(2) : '',
                        discount_unit: item.discount_unit ? item.discount_unit.toFixed(2) : '',
                        unit_price: item.unit_price ? this.$XEUtils.commafy(item.unit_price, { fixed: 4 }) : '',
                        sum: typeof item.sum !== 'undefined' ? this.$XEUtils.commafy(item.sum, { fixed: 2 }) : '',
                        rate_price: item.rate_price ? this.$XEUtils.commafy(item.rate_price, { fixed: 2 }) : '',
                        rate_num:
                            typeof item.rate_num !== 'undefined'
                                ? this.$XEUtils.commafy(item.rate_num, { fixed: 2 })
                                : ''
                    };
                });
                this.orgName = printContent.orgName;
                this.unitList = printContent.unitList;
                this.enquiryColumns[5].customRender = (text, record) => {
                    // 自定义列
                    if (record.unit_data) {
                        let displayStr = record.unit_name;
                        return <span>{displayStr}</span>;
                    } else {
                        return <span>{record.unit_name}</span>;
                    }
                };
                break;
            case 5: // 5、计划单
                this.printContent = 'printChange';
                this.orderTitle = printContent.printTitle;
                this.planModalStopColumns = printContent.printTable;
                this.planModalForm = printContent.printForm;
                this.planOrderStatus = printContent.planOrderStatus;
                this.planModalDatas = printContent.printData;
                this.deptList = printContent.printDept;
                for (let i = 0; i < this.deptList.length; i++) {
                    if (this.deptList[i].key === parseInt(this.planModalForm.department_inner_sn)) {
                        this.planModalForm.department_name = this.deptList[i].name || this.deptList[i].label;
                    }
                }
                this.planModalStopColumns[7].customRender = (text, record) => {
                    let displayStr = this.planOrderStatus[record.status];
                    return <span>{displayStr}</span>;
                };
                break;
            case 6: // 6、订单
                // 采购订单
                if (printContent.myOrderPropObj.pageType === 0) {
                    console.warn('采购订单打印已迁移至新模块（Print）');
                }
                // 销售订单
                if (printContent.myOrderPropObj.pageType === 1) {
                    widthOrdList = [20, 120, 60, 120, 40, 60, '2%', 40, 50, 50, 50, 40, 50, 50, 50, 50, 50, 50];
                    this.printContent = 'printChange';
                    this.orderColumns = printContent.printTable;
                    this.planModalForm = printContent.printForm;
                    // 将合计行的数据合并到列表数据中，以实现表格列的自然对齐
                    this.planModalDatas = printContent.printData
                        .map((item) => ({ ...item, rate: `${item.rate}%` }))
                        .concat([
                            {
                                index: '合计',
                                money: moneyTotal,
                                discount_money: discountMoneyTotal,
                                tax_amount: taxAmountTotal,
                                tax: taxTotal
                            }
                        ]);
                    this.planOrderStatus = printContent.planOrderStatus;
                    this.myOrderPropObj = printContent.myOrderPropObj;
                    this.totalObj = printContent.totalObj;
                    for (let i = 0; i < this.orderColumns.length; i++) {
                        this.orderColumns[i].width = widthOrdList[i];
                    }
                }
                break;
            case 7: // 7、验货单
                this.printContent = 'printChange';
                this.orderTitle = printContent.printTitle;
                if (printContent.pageType === 0) {
                    this.enquiryColumns = printContent.printTable.splice(0, printContent.printTable.length - 1);
                } else {
                    this.enquiryColumns = printContent.printTable;
                }
                this.editInfoFormObj = printContent.printForm;
                this.proTableData = printContent.printData;
                this.orgName = printContent.orgName;
                this.unitList = printContent.unitList;
                // this.enquiryColumns[5].customRender = (text, record) => { // 自定义列
                //     if (record.unit_data) {
                //         let displayStr = record.unit_name
                //         return (
                //             <span>{ displayStr }</span>
                //     )
                //     }
                // }
                this.enquiryColumns = [
                    {
                        title: 'NO',
                        dataIndex: 'index',
                        key: 'index',
                        align: 'center',
                        width: '5%'
                    },
                    {
                        title: '产品',
                        dataIndex: 'name',
                        key: 'name',
                        width: '20%',
                        align: 'center'
                    },
                    {
                        title: '规格型号',
                        dataIndex: 'specification',
                        key: 'specification',
                        width: '10%',
                        align: 'center'
                    },
                    {
                        title: '厂商/经销商',
                        dataIndex: 'vendor_name',
                        key: 'vendor_name',
                        width: '20%',
                        align: 'center'
                    },
                    {
                        title: '批号',
                        dataIndex: 'batch_number',
                        key: 'batch_number',
                        width: '9%',
                        align: 'center'
                    },
                    {
                        title: '数量',
                        dataIndex: 'quantity',
                        key: 'quantity',
                        width: '8%',
                        align: 'center'
                    },
                    {
                        title: '包装单位',
                        dataIndex: 'unit_name',
                        key: 'unit_name',
                        width: '8%',
                        align: 'center'
                    },
                    {
                        title: '合格数量',
                        dataIndex: 'qualified_quantity',
                        key: 'qualified_quantity',
                        width: '8%',
                        align: 'center'
                    },
                    {
                        title: '不合格数量',
                        dataIndex: 'unqualified_quantity',
                        key: 'unqualified_quantity',
                        width: '10%',
                        align: 'center'
                    },
                    {
                        title: '不合格原因',
                        dataIndex: 'unqualified_reason',
                        key: 'unqualified_reason',
                        width: '14%',
                        align: 'center'
                    },
                    {
                        title: '生产日期',
                        dataIndex: 'produce_date',
                        key: 'produce_date',
                        width: '10%',
                        align: 'center'
                    },
                    {
                        title: '失效日期',
                        dataIndex: 'overdue_date',
                        key: 'overdue_date',
                        width: '10%',
                        align: 'center'
                    },
                    {
                        title: '注册证号',
                        dataIndex: 'mdrf_sn',
                        key: 'mdrf_sn',
                        width: '20%',
                        align: 'center'
                    }
                ];
                break;
            case 8: // 8、销售单
                widthOrdList = [58, 120, 60, 120, 50, 60, 50, 40, 50, 50, 50, 40, 50, 50, 50, 50, 50, 50];
                this.printContent = 'printChange';
                this.orderColumns = printContent.printTable.map((item, idx) => ({ ...item, width: widthOrdList[idx] }));
                this.planModalForm = printContent.printForm;
                // 将合计行的数据合并到列表数据中，以实现表格列的自然对齐
                this.planModalDatas = printContent.printData
                    .map((item) => ({ ...item, rate: `${item.rate}%` }))
                    .concat([
                        {
                            index: '合计',
                            money: moneyTotal,
                            discount_money: discountMoneyTotal,
                            tax_amount: taxAmountTotal,
                            tax: taxTotal
                        }
                    ]);
                // for (let i = 0; i < this.orderColumns.length; i++) {
                //     this.orderColumns[i].width = widthOrdList[i]
                // }
                break;
            case 9: // 9、储位分配单
                this.printContent = 'printChange';
                this.orderColumns = printContent.printTable;
                this.planModalForm = printContent.printForm;
                this.planModalDatas = printContent.printData;
                break;
            case 10: // 10、收货单
                this.printContent = 'printChange';
                this.orderColumns = printContent.printTable;
                this.planModalForm = printContent.printForm;
                this.planModalDatas = printContent.printData;
                break;
            case 11: // 11、多张图片
                this.printContent = printContent.printData
                    .map((item) => `<img src=${item} style="display: block;"/>`)
                    .join('');
                break;
        }
    },
    watch: {
        printContent() {
            this.printFn();
        }
    }
};
</script>

<style scoped lang="less">
.print-content {
    padding-bottom: 0;
}
.print-container {
    padding: 18px;
    .print-container-title {
        font-size: 24px;
        color: #444;
        padding-bottom: 24px;
        text-align: center;
    }
    .print-container-form {
        display: flex;
        /*justify-content: space-between;*/
        flex-wrap: wrap;
        margin-bottom: 15px;
        text-align: left;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.65);
        .label {
            margin-right: 10px;
        }
        .desc {
            margin-top: 15px;
            width: 100%;
        }
    }
    .print-container-table {
        margin-bottom: 15px;
    }
}

.bottom-info {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
}
/*@media (max-width: 768px) {}*/
@media (max-width: 1072px) {
    .print6 {
        // 销售订单
        .top-list {
            .item {
                padding-right: 0;

                &:nth-child(1) {
                    min-width: 250px;
                }
                &:nth-child(2) {
                    min-width: 225px;
                }
            }
        }
    }
    .print3 {
        .top-list {
            flex-wrap: wrap;

            li {
                flex-wrap: wrap;
            }
        }
    }
}

@media print {
    .print5 {
        // 采购计划单
        .top-list {
            flex-wrap: wrap;

            li {
                flex-wrap: wrap;
                width: initial !important;
            }
        }
    }

    .print6 {
        // 销售订单
        @page {
            /*size: landscape; // 横向*/
        }
    }
}
</style>
