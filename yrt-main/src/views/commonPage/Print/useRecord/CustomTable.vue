<template>
    <table ref="table" class="product" border="1" width="100%">
        <tr>
            <th v-for="(column, index) in tableColumns" :key="index" :class="column.class">{{ column.title }}</th>
        </tr>
        <tr v-for="(record, index) in records" :key="index">
            <td v-for="column in tableColumns" :key="column.dataIndex" :align="column.align">
                {{ getColumnContent(record, column) }}
            </td>
        </tr>
    </table>
</template>

<script>
import Big from 'big.js';

export default {
    name: 'useRecordTable',
    props: {
        records: Array
    },
    data() {
        return {
            tableColumns: [
                { title: '行号', dataIndex: 'index', class: 'index' },
                { title: '产品名称', dataIndex: 'product_name', class: 'product', align: 'left' },
                { title: '规格型号', dataIndex: 'specification', class: 'specification', align: 'left' },
                { title: '生产厂商', dataIndex: 'vendor_name', class: 'vendor', align: 'left' },
                { title: '批号', dataIndex: 'batch_number', class: 'batch' },
                { title: '数量', dataIndex: 'quantity', class: 'count' },
                { title: '单价', dataIndex: 'unit_price', class: 'price', align: 'right' },
                { title: '单位', dataIndex: 'unit_name', class: 'unit' },
                { title: '状态', dataIndex: 'status_name', class: 'status_name', align: 'right' }
            ]
        };
    },
    methods: {
        /**
         * 计算单元格内容
         * @param {array} record 行信息
         * @param {array} column 列信息
         */
        getColumnContent(record, column) {
            switch (column.dataIndex) {
                case 'specification':
                    if (!record['specification_vendor_sn']) {
                        return record[column.dataIndex];
                    }
                    return `${record[column.dataIndex]} (${record['specification_vendor_sn']})`;
                case 'unit_price':
                    return this.$Utils.commafy(new Big(record['unit_price']).round(2).toNumber());
                default:
                    return record[column.dataIndex];
            }
        }
    }
};
</script>
