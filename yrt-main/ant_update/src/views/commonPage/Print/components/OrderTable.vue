<template>
    <table ref="table" class="product" width="100%" :border="tableFormatter.tableBorder">
        <tr
            :style="{
                textAlign: tableFormatter.headAlign,
                verticalAlign: tableFormatter.headVerticalAlign,
                fontSize: `${tableFormatter.headFontSize}mm`
            }"
        >
            <th
                v-for="column in enabledColumns"
                :key="column.dataIndex"
                :style="{
                    width: column.width === undefined ? 'auto' : `${column.width}${baseConfig.unit}`,
                    paddingTop: `${tableFormatter.headPaddingTop}mm`,
                    paddingRight: `${tableFormatter.headPaddingRight}mm`,
                    paddingBottom: `${tableFormatter.headPaddingBottom}mm`,
                    paddingLeft: `${tableFormatter.headPaddingLeft}mm`,
                    fontWeight: tableFormatter.headFontBold ? 'bold' : 'normal'
                }"
                >{{ column.alias || column.title }}</th
            >
        </tr>
        <tr
            v-for="(record, index) in records"
            :key="index"
            :style="{
                fontSize: `${tableFormatter.columnFontSize}mm`,
                textAlign: tableFormatter.columnAlign,
                verticalAlign: tableFormatter.columnVerticalAlign
            }"
        >
            <td
                v-for="column in enabledColumns"
                :key="column.dataIndex"
                :style="{
                    paddingTop: `${tableFormatter.columnPaddingTop}mm`,
                    paddingRight: `${tableFormatter.columnPaddingRight}mm`,
                    paddingBottom: `${tableFormatter.columnPaddingBottom}mm`,
                    paddingLeft: `${tableFormatter.columnPaddingLeft}mm`,
                    textAlign: column.align,
                    verticalAlign: column.verticalAlign,
                    fontWeight: tableFormatter.columnFontBold ? 'bold' : 'normal'
                }"
            >
                {{ getColumnContent(record, column) }}
            </td>
        </tr>
        <slot name="summation"></slot>
    </table>
</template>

<script>
import Big from 'big.js';

export default {
    name: 'OrderTable',
    inject: ['baseConfig', 'tableFormatter', 'otherConfig'],
    props: {
        records: {
            type: Array,
            default: () => []
        }
    },
    computed: {
        /**
         * 启用的列
         */
        enabledColumns() {
            return this.tableFormatter.columns.filter((column) => column.enabled !== false);
        }
    },
    methods: {
        /**
         * 计算单元格内容
         * @param {array} record 行信息
         * @param {array} column 列信息
         */
        getColumnContent(record, column) {
            const { baseConfig, otherConfig, tableFormatter } = this;
            const { dataIndex } = column;
            let value = record[dataIndex];
            let precision = undefined;
            if (typeof record[dataIndex] !== 'number') {
                return value;
            }
            value = Math.abs(value);
            // 需要数值精度格式化
            if (tableFormatter.controledFields.precision[dataIndex]) {
                if (typeof baseConfig.precision === 'number' && !isNaN(baseConfig.precision)) {
                    precision = baseConfig.precision;
                }
                if (typeof column.precision === 'number' && !isNaN(column.precision)) {
                    precision = column.precision;
                }
                value = new Big(value).toFixed(precision);
            }
            // 需要数值千位分隔符的列
            if (tableFormatter.controledFields.thousandSeparator[dataIndex] && baseConfig.thousandSeparator) {
                if (precision !== undefined) {
                    value = this.$Utils.commafy(value);
                }
            }
            // 判断是否保留负值
            if (
                record[dataIndex] < 0 &&
                otherConfig.keepNegative &&
                tableFormatter.controledFields.negative[dataIndex]
            ) {
                value = `-${value}`;
            }
            return value;
        }
    }
};
</script>
