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
import OrderForPrint from '../components/OrderForPrint';
import CustomHeader from './CustomHeader';
import CustomFooter from './CustomFooter';
import OrderTable from '../components/OrderTable';
import OrderTableSummationRow from '../components/OrderTableSummationRow';
import { paperTypeMap, paperSizeMap } from '../type';
import defaultTableColumns from '../components/inAndOutboundOrderTableColumns';

export default {
    name: 'InboundOrderForPrint',
    components: {
        OrderForPrint,
        CustomHeader,
        CustomFooter,
        OrderTable,
        OrderTableSummationRow
    },
    inject: ['printEditor', 'baseConfig', 'tableFormatter', 'contentData'],
    computed: {
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
        const sourcePrintData = JSON.parse(localStorage.getItem('printContent'));
        // 初始化编辑器
        this.printEditor.handleInitialize({
            moduleKey: 'InboundOrder',
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
                columns: defaultTableColumns.map((column) => {
                    switch (column.dataIndex) {
                        // 入库单不打印医保编号
                        case 'health_care_sn':
                            return { ...column, enabled: false };
                        // 入库单将列名“产品名称”替换为“商品名称”
                        case 'product_name':
                            return { ...column, alias: '商品名称' };
                        // 入库单将列名“生产厂商”替换为“产地”
                        case 'vendor_name':
                            return { ...column, alias: '产地' };
                        default:
                            return { ...column };
                    }
                }),
                pageSplit: true,
                maxRecordCountPerPage: 5,
                summationSpans: '5/7',
                controledFields: {
                    // 需要控制精度的列
                    precision: { 'tax_unit_price': true, money: true },
                    // 需要控制千位分隔符的列
                    thousandSeparator: { quantity: true, 'tax_unit_price': true, money: true },
                    // 需要控制正负的列
                    negative: { quantity: true, 'tax_unit_price': true, money: true }
                }
            },
            contentData: {
                infoData: sourcePrintData.printForm,
                tableData: sourcePrintData.printData.map((record, index) => {
                    return {
                        ...record,
                        specification: !record['specification_vendor_sn']
                            ? record.specification
                            : `${record.specification} (${record['specification_vendor_sn']})`,
                        money: new Big(record.quantity).times(record['tax_unit_price']).toNumber(),
                        index: index + 1
                    };
                })
            }
        });
    }
};
</script>
