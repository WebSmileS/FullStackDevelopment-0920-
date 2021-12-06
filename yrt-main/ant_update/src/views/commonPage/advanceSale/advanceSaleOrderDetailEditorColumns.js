import tableColumnConfig from '@/config/tableColumnConfig';
import baseColumns from './advanceSaleProductColumns';
import { rateTypeMap, advanceSaleOrderProductTypeMap } from './type';

/**
 * @param {string} value 产品类型值
 * @returns {string}
 */
function productTypeParser(value) {
    return advanceSaleOrderProductTypeMap[value].text;
}

const selecterColumns = [];

baseColumns.forEach((column) => {
    switch (column.field) {
        case 'quantity':
            selecterColumns.push({
                ...column,
                title: '总数量',
                field: 'quantity'
            });
            selecterColumns.push({
                ...column,
                title: '成本价',
                field: 'buyingPriceConverted'
            });
            selecterColumns.push({
                ...column,
                title: '数量',
                field: 'editingQuantity',
                editRender: { name: '$input', props: { type: 'number' } }
            });
            selecterColumns.push({
                ...tableColumnConfig.rate,
                editRender: {
                    name: '$select',
                    options: Object.keys(rateTypeMap).map((key) => ({
                        value: rateTypeMap[key].value,
                        label: `${rateTypeMap[key].text}%`
                    }))
                }
            });
            selecterColumns.push({
                ...tableColumnConfig.price,
                field: 'salePrice',
                title: '含税单价',
                editRender: { name: '$input', props: { type: 'number' } }
            });
            selecterColumns.push({
                ...tableColumnConfig.price,
                field: 'money',
                title: '含税金额',
                editRender: { name: '$input', props: { type: 'number' } }
            });
            selecterColumns.push({
                ...column,
                title: '已生成出库单数量',
                field: 'soldQuantity',
                width: column.width + 30
            });
            selecterColumns.push({
                ...column,
                title: '已回库数量',
                field: 'respondQuantity'
            });
            selecterColumns.push({
                ...column,
                title: '可用数量',
                field: 'useableQuantity'
            });
            return;
        case 'type':
            selecterColumns.push({
                ...column,
                cellRender: {
                    name: 'valueParser',
                    parser: productTypeParser
                }
            });
            return;
        case 'unitId':
            selecterColumns.push({
                ...column,
                field: 'unitName'
            });
            return;
        case 'mdrfInnerSn':
            selecterColumns.push({
                ...column,
                field: 'mdrfSn'
            });
            return;
    }
    selecterColumns.push(column);
});

export default selecterColumns;
