<template>
    <order-for-print :records="contentData.tableData">
        <!-- slotProps为OrderForPrint组件计算后的数据项，比如分页相关的数据 -->
        <template v-slot:header="slotProps">
            <inspection-order-header :orderInfo="contentData.infoData" :slotProps="slotProps"></inspection-order-header>
        </template>
        <!-- slotProps为OrderForPrint组件计算后的数据项，比如分页相关的数据 -->
        <template v-slot:table="slotProps">
            <order-table :records="slotProps.records"></order-table>
        </template>
        <!-- slotProps为OrderForPrint组件计算后的数据项，比如分页相关的数据 -->
        <template v-slot:footer="slotProps">
            <inspection-order-footer
                :orderInfo="contentData.infoData"
                :allProducts="contentData.tableData"
                :currentPageProducts="slotProps.records"
                :page="slotProps.page"
                :pageTotal="slotProps.pageTotal"
            ></inspection-order-footer>
        </template>
    </order-for-print>
</template>

<script>
import OrderForPrint from '../components/OrderForPrint/OrderForPrint.vue';
import InspectionOrderHeader from './InspectionOrderHeader.vue';
import InspectionOrderFooter from './InspectionOrderFooter.vue';
import OrderTable from '../components/OrderTable.vue';
import { paperTypeMap, paperSizeMap } from '../type';
import inspectionOrderColumns from './inspectionOrderColumns';

export default {
    name: 'InspectionOrder',
    components: {
        OrderForPrint,
        InspectionOrderHeader,
        InspectionOrderFooter,
        OrderTable
    },
    inject: ['printEditor', 'baseConfig', 'tableFormatter', 'contentData'],
    created() {
        const sourcePrintData = JSON.parse(localStorage.getItem('printContent'));
        // 初始化编辑器
        this.printEditor.handleInitialize({
            moduleKey: 'InspectionOrder',
            moduleConfigVersion: '1.0',
            moduleConfigPrevVersion: '0.0',
            baseConfig: {
                paperType: paperTypeMap.bisection.value,
                paperWidth: paperSizeMap.bisection.width,
                paperHeight: paperSizeMap.bisection.height,
                unit: paperSizeMap.bisection.unit,
                safeMarginTop: 0,
                safeMarginBottom: 0,
                safeMarginLeft: 15.5,
                safeMarginRight: 15.5,
                titleLineHeight: 14
            },
            tableFormatter: {
                columns: inspectionOrderColumns,
                pageSplit: true,
                maxRecordCountPerPage: 5,
                summationSpans: '5/7'
            },
            contentData: {
                infoData: sourcePrintData.printForm,
                tableData: sourcePrintData.printData.map((record, index) => ({ ...record, index: index + 1 }))
            }
        });
    }
};
</script>
