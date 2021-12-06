<template>
    <order-for-print>
        <!-- slotProps为OrderForPrint组件计算后的数据项，比如分页相关的数据 -->
        <template v-slot:header="slotProps">
            <custom-header :orderInfo="contentData.infoData" :slotProps="slotProps"></custom-header>
        </template>
        <!-- slotProps为OrderForPrint组件计算后的数据项，比如分页相关的数据 -->
        <template v-slot:table="slotProps">
            <order-table :records="slotProps.records">
                <template slot="summation">
                    <order-table-summation-row
                        :records="slotProps.records"
                        :summation="summation"
                        summationKey="money"
                    ></order-table-summation-row>
                </template>
            </order-table>
        </template>
        <!-- slotProps为OrderForPrint组件计算后的数据项，比如分页相关的数据 -->
        <template v-slot:footer="slotProps">
            <custom-footer
                :orderInfo="contentData.infoData"
                :allProducts="contentData.tableData"
                :currentPageProducts="slotProps.records"
                :page="slotProps.page"
                :pageTotal="slotProps.pageTotal"
            ></custom-footer>
        </template>
    </order-for-print>
</template>

<script>
import Big from 'big.js';
import {
    odoAllotPrintCountAPI,
    odoGivePrintCountAPI,
    odoGrantPrintCountAPI,
    odoPrintCountAPI,
    odoPurchasePrintCountAPI,
    odoSalePrintCountAPI,
    odoSaleReturnPrintCountAPI
} from '@/service/pageAjax';
import OrderForPrint from '../components/OrderForPrint';
import CustomHeader from './CustomHeader';
import CustomFooter from './CustomFooter';
import OrderTable from '../components/OrderTable';
import OrderTableSummationRow from '../components/OrderTableSummationRow';
import { paperTypeMap, paperSizeMap } from '../type';
import defaultTableColumns from '../components/inAndOutboundOrderTableColumns';
import { grnAndOdoOrderType } from '@/config/constants';

export default {
    name: 'OutboundOrderForPrint',
    components: {
        OrderForPrint,
        CustomHeader,
        CustomFooter,
        OrderTable,
        OrderTableSummationRow
    },
    inject: ['printEditor', 'baseConfig', 'tableFormatter', 'contentData'],
    computed: {
        // 总合计金额
        summation() {
            let summation = new Big(0);
            if (this.contentData.tableData) {
                this.contentData.tableData.forEach((product) => {
                    summation = summation.plus(product.money);
                });
            }
            return summation.toNumber();
        }
    },
    created() {
        // 设置打印数据
        const sourcePrintData = JSON.parse(localStorage.getItem('printContent'));
        const tableColumns = [
            ...defaultTableColumns,
            {
                title: '许可证号',
                dataIndex: 'c0',
                isCustomColumn: true,
                customKey: 'c0',
                enabled: false,
                width: 16
            },
            {
                title: '储存',
                dataIndex: 'c1',
                isCustomColumn: true,
                customKey: 'c1',
                enabled: false,
                width: 8
            }
        ];
        const options = {
            moduleKey: 'OutboundOrder',
            moduleConfigVersion: '1.1',
            moduleConfigPrevVersion: '1.0',
            baseConfig: {
                paperType: paperTypeMap.bisection.value,
                paperWidth: paperSizeMap.bisection.width,
                paperHeight: paperSizeMap.bisection.height,
                unit: paperSizeMap.bisection.unit,
                safeMarginTop: 0,
                safeMarginBottom: 0,
                safeMarginLeft: 15.5,
                safeMarginRight: 15.5
            },
            tableFormatter: {
                columns: tableColumns,
                pageSplit: true,
                maxRecordCountPerPage: 5,
                summationSpans: '5/8',
                controledFields: {
                    // 需要控制精度的列
                    precision: { 'tax_unit_price': true, money: true },
                    // 需要控制千位分隔符的列
                    thousandSeparator: { quantity: true, 'tax_unit_price': true, money: true },
                    // 需要控制正负的列
                    negative: { quantity: true, 'tax_unit_price': true, money: true }
                }
            },
            otherConfig: {
                autoSync: true
            },
            contentData: {
                infoData: {
                    ...sourcePrintData.printForm,
                    details: undefined, // 覆盖掉冗余的数据
                    type_key: !sourcePrintData.orderTypeData ? 'ODO' : sourcePrintData.orderTypeData.value
                },
                tableData: sourcePrintData.printData.map((record, index) => {
                    return {
                        ...record,
                        money: new Big(record.quantity).times(record['tax_unit_price']).toNumber(),
                        index: index + 1
                    };
                })
            }
        };
        // 赠送单
        if (sourcePrintData.orderTypeData && sourcePrintData.orderTypeData.value === grnAndOdoOrderType.GIVEODO.value) {
            // 使用单独的模块key保存打印设置
            options.moduleKey = 'OutboundOrderGive';
            // 不显示单价和金额列
            for (let i = 0, l = options.tableFormatter.columns.length; i < l; i += 1) {
                if (
                    options.tableFormatter.columns[i].dataIndex === 'tax_unit_price' ||
                    options.tableFormatter.columns[i].dataIndex === 'money'
                ) {
                    options.tableFormatter.columns[i] = {
                        ...options.tableFormatter.columns[i],
                        enabled: false
                    };
                }
            }
        }
        // 初始化编辑器
        this.printEditor.handleInitialize(options);
    },
    mounted() {
        // 添加打印计数事件
        window.addEventListener('afterprint', this.handleUpdatePrintCount, false);
    },
    beforeDestroy() {
        // 移除打印计数事件
        window.removeEventListener('afterprint', this.handleUpdatePrintCount, false);
    },
    methods: {
        /**
         * 更新当前出库单打印次数
         */
        async handleUpdatePrintCount() {
            const orderTypeKey = this.contentData.infoData.type_key;
            const requestParams = {
                billsType: this.contentData.infoData.billsType,
                'odo_inner_sn': this.contentData.infoData['odo_inner_sn'],
                'org_id': this.contentData.infoData['org_id']
            };
            let response = {};
            // 出库单
            if (orderTypeKey === 'ODO') {
                response = await odoPrintCountAPI(requestParams);
            }
            // 调拨出库单
            if (orderTypeKey === grnAndOdoOrderType.ALLOTODO.value) {
                response = await odoAllotPrintCountAPI(requestParams);
            }
            // 采购退货出库单
            if (orderTypeKey === grnAndOdoOrderType.PURCHASERETURN.value) {
                response = await odoPurchasePrintCountAPI(requestParams);
            }
            // 销售出库单
            if (orderTypeKey === grnAndOdoOrderType.SALEODO.value) {
                response = await odoSalePrintCountAPI(requestParams);
            }
            // 销售退货单
            if (orderTypeKey === grnAndOdoOrderType.SALERETURN.value) {
                response = await odoSaleReturnPrintCountAPI(requestParams);
            }
            // 赠送出库单
            if (orderTypeKey === grnAndOdoOrderType.GIVEODO.value) {
                response = await odoGivePrintCountAPI(requestParams);
            }
            // 领用出库单
            if (orderTypeKey === grnAndOdoOrderType.GRANTODO.value) {
                response = await odoGrantPrintCountAPI(requestParams);
            }
            const { code, msg } = response;
            if (code !== 0) {
                throw new Error(msg);
            }
        }
    }
};
</script>
