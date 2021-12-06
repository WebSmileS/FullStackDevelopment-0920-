<template>
    <tr
        :style="{
            fontSize: `${tableFormatter.summationFontSize}mm`,
            textAlign: tableFormatter.summationAlign,
            verticalAlign: tableFormatter.summationVerticalAlign
        }"
    >
        <td
            :style="{
                paddingTop: `${tableFormatter.summationPaddingTop}mm`,
                paddingRight: `${tableFormatter.summationPaddingRight}mm`,
                paddingBottom: `${tableFormatter.summationPaddingBottom}mm`,
                paddingLeft: `${tableFormatter.summationPaddingLeft}mm`,
                fontWeight: tableFormatter.summationFontBold ? 'bold' : 'normal'
            }"
            :colspan="summationRowColSpan[0]"
        >
            <span>总合计大写：</span>
            <span>{{ summationCapital }}</span>
            <span>（</span>
            <span>{{ formattedSummation }}</span>
            <span>）</span>
        </td>
        <td
            :style="{
                paddingTop: `${tableFormatter.summationPaddingTop}mm`,
                paddingRight: `${tableFormatter.summationPaddingRight}mm`,
                paddingBottom: `${tableFormatter.summationPaddingBottom}mm`,
                paddingLeft: `${tableFormatter.summationPaddingLeft}mm`,
                fontWeight: tableFormatter.summationFontBold ? 'bold' : 'normal'
            }"
            :colspan="summationRowColSpan[1]"
        >
            <span>本页合计大写：</span>
            <span>{{ pageSummationCapital }}</span>
            <span>（</span>
            <span>{{ formattedPageSummation }}</span>
            <span>）</span>
        </td>
    </tr>
</template>

<script>
import Big from 'big.js';

export default {
    name: 'OrderTableSummationRow',
    inject: ['baseConfig', 'tableFormatter', 'otherConfig'],
    props: {
        records: {
            type: Array,
            default: () => []
        },
        summation: {
            type: Number,
            default: 0
        },
        summationKey: {
            type: String
        }
    },
    computed: {
        // 格式化过的总合计金额
        formattedSummation() {
            let value = new Big(this.summation).abs().toFixed(this.baseConfig.precision);
            if (this.baseConfig.thousandSeparator) {
                value = this.$Utils.commafy(value);
            }
            if (this.summation < 0 && this.otherConfig.keepNegative) {
                value = `-${value}`;
            }
            return value;
        },

        // 总合计大写
        summationCapital() {
            return this.$Utils.moneyToCapital(
                new Big(this.summation).abs().round(this.baseConfig.precision).toNumber()
            );
        },

        // 本页合计
        pageSummation() {
            let summation = new Big(0);
            this.records.forEach((record) => {
                summation = summation.plus(record[this.summationKey]);
            });

            return summation.toNumber();
        },

        // 格式化过的本页合计金额
        formattedPageSummation() {
            let value = new Big(this.pageSummation).abs().round(this.baseConfig.precision).toNumber();
            if (this.baseConfig.thousandSeparator) {
                value = this.$Utils.commafy(value);
            }
            if (this.pageSummation < 0 && this.otherConfig.keepNegative) {
                value = `-${value}`;
            }
            return value;
        },

        // 本页合计大写
        pageSummationCapital() {
            return this.$Utils.moneyToCapital(
                new Big(this.pageSummation).abs().round(this.baseConfig.precision).toNumber()
            );
        },

        summationRowColSpan() {
            let enabledColumnCount = 0;
            let summationSpan = 1;
            let pageSummationSpan = 1;
            this.tableFormatter.columns.forEach((column) => {
                if (column.enabled) {
                    enabledColumnCount += 1;
                }
            });
            if (typeof this.tableFormatter.summationSpans === 'string') {
                const spans = this.tableFormatter.summationSpans.split('/');
                let total = 0;
                spans.forEach((numberStr) => {
                    let span = Number(numberStr);
                    if (!isNaN(span)) {
                        total += span;
                    }
                });
                if (total === enabledColumnCount) {
                    return spans;
                }
            }
            if (enabledColumnCount > 2) {
                pageSummationSpan = Math.floor(enabledColumnCount / 2);
                summationSpan = enabledColumnCount - pageSummationSpan;
            }
            return [summationSpan, pageSummationSpan];
        }
    }
};
</script>
