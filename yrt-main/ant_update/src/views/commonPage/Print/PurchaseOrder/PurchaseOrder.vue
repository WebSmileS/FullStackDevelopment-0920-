<template>
    <div id="purchaseOrderForPrint" :style="wrapperStyle">
        <h2
            :style="{
                fontSize: `${baseConfig.titleFontSize}${baseConfig.unit}`,
                fontWeight: baseConfig.titleFontBold ? 'bold' : 'normal',
                lineHeight: `${baseConfig.titleLineHeight}${baseConfig.unit}`
            }"
            >{{ employeeInfo.OrgName }} 采购订单</h2
        >
        <div class="table-header" width="100%">
            <span class="text-left">采购单号：{{ contentData.infoData.po_sn }}</span>
            <span class="text-right">日期：{{ contentData.infoData.create_time }}</span>
        </div>
        <table border="1" cellpadding="1" width="100%">
            <tr>
                <th align="center" rowspan="3" :colspan="colSpan.top[0]">供货单位</th>
                <td align="left" :colspan="colSpan.top[1]">名称：{{ contentData.infoData.partB_name }}</td>
                <td align="left" :colspan="colSpan.top[2]">联系人：{{ contentData.infoData.b_principal }}</td>
            </tr>
            <tr align="left">
                <td :colspan="colSpan.top[1]">开户银行：{{ contentData.infoData.b_bank_name }}</td>
                <td :colspan="colSpan.top[2]">电话：{{ contentData.infoData.b_phone }}</td>
            </tr>
            <tr align="left">
                <td :colspan="colSpan.top[1]">银行账号：{{ contentData.infoData.b_bank_account }}</td>
                <td :colspan="colSpan.top[2]"></td>
            </tr>
            <tr>
                <th
                    v-for="column in enabledColumns"
                    :key="column.dataIndex"
                    :style="{ minWidth: column.width === undefined ? 'auto' : `${column.width}${baseConfig.unit}` }"
                >
                    {{ column.alias || column.title }}
                </th>
            </tr>
            <tr v-for="(record, index) in contentData.tableData" :key="index">
                <td v-for="column in enabledColumns" :key="column.dataIndex" :align="column.align">
                    {{ getColumnContent(record, column) }}
                </td>
            </tr>
            <tr align="left" class="summation">
                <td :colspan="colSpan.bottom1[0]">总价大写：{{ summationCapital }}</td>
                <td :colspan="colSpan.bottom1[1]">总价：{{ formattedSummation }}</td>
            </tr>
            <tr align="left">
                <td :colspan="colSpan.bottom2[0]">
                    <span class="block-selection-title">付款方式：</span>
                    <span class="block-selection">现金</span>
                    <span class="block-selection">支票</span>
                    <span class="block-selection">网银</span>
                    <span class="block-selection">电汇</span>
                </td>
                <td :colspan="colSpan.bottom2[1]">
                    <span class="block-selection-title">发票：</span>
                    <span class="block-selection">增票</span>
                    <span class="block-selection">普票</span>
                    <span class="block-selection">无票</span>
                </td>
            </tr>
            <tr align="left">
                <td :colspan="colSpan.bottom3[0]">
                    <span class="block-selection-title">付款说明：</span>
                    <span class="block-selection">全额付款</span>
                    <span class="sub-block-selection">
                        <span class="block-selection">分批付款</span>
                        <span>、</span>
                        <span class="block-selection">1</span>
                        <span>/</span>
                        <span class="block-selection">2</span>
                        <span>/</span>
                        <span class="block-selection">3</span>
                        <span class="sub-block-selection-title">分批付款金额：</span>
                    </span>
                </td>
            </tr>
        </table>
        <div class="table-footer" width="100%">
            <div class="text-left"
                >制单人：<span class="underline">{{ contentData.infoData.creator_name }}</span></div
            >
            <div class="text-middle"
                >审核人：<span class="underline">{{ contentData.infoData.auditor_name }}</span></div
            >
            <div class="text-right">财务：<span class="underline"></span></div>
        </div>
    </div>
</template>

<script>
import Big from 'big.js';
import { paperTypeMap, paperSizeMap } from '../type';
import columns from './columns';

export default {
    name: 'PurchaseOrder',
    inject: ['printEditor', 'baseConfig', 'tableFormatter', 'contentData'],
    computed: {
        /**
         * 启用的列
         */
        enabledColumns() {
            return this.tableFormatter.columns.filter((column) => column.enabled !== false);
        },

        wrapperStyle() {
            const { unit, paperWidth, paperHeight, paddingTop, paddingRight, paddingBottom, paddingLeft } =
                this.baseConfig;
            return {
                width: `${paperWidth}${unit}`,
                height: `${paperHeight}${unit}`,
                paddingTop: `${paddingTop}${unit}`,
                paddingRight: `${paddingRight}${unit}`,
                paddingBottom: `${paddingBottom}${unit}`,
                paddingLeft: `${paddingLeft}${unit}`
            };
        },

        // 单元格跨列
        colSpan() {
            let countColumnsEnabled = 0;
            this.tableFormatter.columns.forEach((column) => {
                if (column.enabled) {
                    countColumnsEnabled += 1;
                }
            });
            countColumnsEnabled = Math.max(countColumnsEnabled, 1);
            const top = [
                1,
                Math.round(((countColumnsEnabled - 1) / 9) * 5),
                Math.round(((countColumnsEnabled - 1) / 7) * 4)
            ];
            return {
                // 供货单位行
                top,
                // 总价合计行
                bottom1: [top[0] + top[1], top[2]],
                // 付款方式行
                bottom2: [top[0] + top[1], top[2]],
                // 付款说明行
                bottom3: [countColumnsEnabled]
            };
        },

        // 总合计
        summation() {
            let summation = new Big(0);
            this.contentData.tableData.forEach((product) => {
                summation = summation.plus(new Big(product.quantity).times(product['tax_unit_price']));
            });

            return summation.toNumber();
        },

        // 格式化过的合计
        formattedSummation() {
            let value = new Big(this.summation).round(this.baseConfig.precision).toNumber();
            if (this.baseConfig.thousandSeparator) {
                return this.$Utils.commafy(value);
            }
            return value;
        },

        // 总合计大写
        summationCapital() {
            return this.$Utils.moneyToCapital(new Big(this.summation).round(this.baseConfig.precision).toNumber());
        }
    },
    created() {
        // 用户信息
        this.employeeInfo = JSON.parse(this.$cookie.get('EmployeeInfo')) || {};

        const sourcePrintData = JSON.parse(localStorage.getItem('printContent'));
        this.printEditor.handleInitialize({
            moduleKey: 'PurchaseOrder',
            moduleConfigVersion: '1.0',
            moduleConfigPrevVersion: '0.0',
            baseConfig: {
                paperType: paperTypeMap.A4.value,
                paperWidth: paperSizeMap.A4.width,
                paperHeight: paperSizeMap.A4.height,
                unit: paperSizeMap.A4.unit,
                safeMarginTop: 0,
                safeMarginBottom: 0,
                safeMarginLeft: 0,
                safeMarginRight: 0,
                titleFontSize: 9,
                titleFontBold: true
            },
            tableFormatter: {
                columns,
                pageSplit: false
            },
            contentData: {
                infoData: sourcePrintData.printForm,
                tableData: sourcePrintData.printData.map((product, index) => {
                    let taxAmount = product['tax_amount'];
                    let taxUnitPrice = product['tax_unit_price'];
                    if (typeof taxAmount === 'string') {
                        taxAmount = unCommafy(product['tax_amount']);
                    }
                    if (typeof taxUnitPrice === 'string') {
                        taxUnitPrice = unCommafy(product['tax_unit_price']);
                    }
                    return {
                        ...product,
                        index: index + 1,
                        'tax_unit_price': taxUnitPrice,
                        'tax_amount': taxAmount,
                        'vendor_name': product['vendor_name'] || product['product_vendor_name']
                    };
                })
            }
        });
    },
    methods: {
        /**
         * 计算单元格内容
         * @param {array} record 行信息
         * @param {array} column 列信息
         */
        getColumnContent(record, column) {
            const { baseConfig } = this;
            let precision = 0;
            let value;
            switch (column.dataIndex) {
                case 'tax_unit_price':
                case 'tax_amount':
                    if (typeof baseConfig.precision === 'number' && !isNaN(baseConfig.precision)) {
                        precision = baseConfig.precision;
                    }
                    if (typeof column.precision === 'number' && !isNaN(column.precision)) {
                        precision = column.precision;
                    }
                    value = new Big(record[column.dataIndex]).round(precision).toNumber();
                    if (baseConfig.thousandSeparator) {
                        value = this.$Utils.commafy(value);
                    }
                    return value;
                default:
                    return record[column.dataIndex];
            }
        }
    }
};

/**
 * 反处理经过XEUtils.commafy处理过的字符串为number
 * @param {string} numberString
 * @returns {number}
 */
function unCommafy(numberString) {
    const charArray = [];
    for (let i = 0, l = numberString.length; i < l; i += 1) {
        const code = numberString.charCodeAt(i);
        // 仅保留数字和小数点
        if ((code > 47 && code < 58) || code === 46) {
            charArray.push(numberString[i]);
        }
    }

    return Number(charArray.join(''));
}
</script>

<style lang="less">
/* 采购订单的打印样式 */
#purchaseOrderForPrint {
    @printColor: #000000;
    background-color: #ffffff;
    font-size: 10px;
    color: @printColor;
    table {
        border-color: @printColor;
    }
    th,
    td {
        padding-left: 0.3em;
    }
    .underline {
        border-bottom: 1px solid @printColor;
    }
    .table-header {
        margin-bottom: 12px;
        height: 12px;
        span.text-left {
            float: left;
        }
        span.text-right {
            float: right;
        }
    }
    .table-footer {
        margin-top: 12px;
        height: 12px;
        > div {
            width: 33%;
            text-align: left;
            .underline {
                display: inline-block;
                width: 40%;
            }
        }
        .text-left {
            float: left;
            .underline {
                text-align: center;
            }
        }
        .text-middle {
            float: left;
            text-align: center;
        }
        .text-right {
            float: right;
            text-align: right;
            .underline {
                text-align: center;
            }
        }
    }
    tr.summation {
        font-weight: 900;
        td {
            padding-top: 0.5em;
            padding-bottom: 0.5em;
            padding-left: 0.3em;
        }
    }
    span.block-selection-title {
        font-weight: 900;
    }
    span.block-selection {
        display: inline-block;
        margin-right: 1.1em;
        &::after {
            content: '';
            display: inline-block;
            position: relative;
            top: 0.1em;
            width: 1.2em;
            height: 1.2em;
            margin-left: 0.1em;
            border: 1px solid @printColor;
        }
    }
    span.sub-block-selection {
        span.block-selection {
            margin-right: 0.2em;
        }
    }
}
.ant-table-footer {
    padding: 0;
}
@media print {
    #purchaseOrderForPrint {
        margin: 0;
    }
}
</style>
