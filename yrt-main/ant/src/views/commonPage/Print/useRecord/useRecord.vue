<template>
    <order-for-print :records="productList" needPageSplit :maxCountPerPage="5">
        <!-- slotProps为OrderForPrint组件计算后的数据项，比如分页相关的数据 -->
        <template v-slot:header="slotProps">
            <custom-header :orderInfo="orderInfo" :slotProps="slotProps"></custom-header>
        </template>
        <!-- slotProps为OrderForPrint组件计算后的数据项，比如分页相关的数据 -->
        <template v-slot:table="slotProps">
            <custom-table warehouseTransfer :orderInfo="orderInfo" :records="slotProps.records"></custom-table>
        </template>
        <!-- slotProps为OrderForPrint组件计算后的数据项，比如分页相关的数据 -->
        <template v-slot:footer="slotProps">
            <custom-footer
                :orderInfo="orderInfo"
                :allProducts="productList"
                :currentPageProducts="slotProps.records"
                :page="slotProps.page"
                :pageTotal="slotProps.pageTotal"
            ></custom-footer>
        </template>
    </order-for-print>
</template>

<script>
import OrderForPrint from '../components/OrderForPrint';
import CustomTable from './CustomTable';
import CustomHeader from './CustomHeader';
import CustomFooter from './CustomFooter';

export default {
    name: 'OutboundOrderForPrint',
    components: {
        OrderForPrint,
        CustomHeader,
        CustomFooter,
        CustomTable
    },
    props: {
        printData: {
            type: Object,
            required: true
        }
    },
    data() {
        return {
            orderInfo: this.printData.printForm,
            // 手动添加'跨页的行号'（序号），以避免未来上层模块数据变更而产生的隐性问题(虽然源数据里带有index可用)
            productList: this.printData.printData.map((_, index) => ({ ..._, index: index + 1 }))
        };
    }
};
</script>
