import baseColumns from './advanceSaleProductColumns';
import { advanceSaleOrderProductTypeMap } from './type';

/**
 * @param {string} value 产品类型值
 * @returns {string}
 */
function productTypeParser(value) {
    return advanceSaleOrderProductTypeMap[value].text;
}

const detailColumns = [];

baseColumns.forEach((column) => {
    switch (column.field) {
        case 'type':
            detailColumns.push({
                ...column,
                cellRender: {
                    name: 'valueParser',
                    parser: productTypeParser
                }
            });
            return;
        case 'quantity':
            detailColumns.push({
                ...column,
                title: '总数量'
            });
            detailColumns.push({
                ...column,
                title: '已生成出库单数量',
                field: 'soldQuantity',
                width: column.width + 30
            });
            detailColumns.push({
                ...column,
                title: '已回库数量',
                field: 'respondQuantity'
            });
            return;
        case 'unitId':
            detailColumns.push({
                ...column,
                field: 'unitName'
            });
            return;
        case 'mdrfInnerSn':
            detailColumns.push({
                ...column,
                field: 'mdrfSn'
            });
            return;
    }
    detailColumns.push(column);
});

export default detailColumns;
