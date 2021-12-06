import { advanceSaleOrderProductTypeMap } from './type';
import baseColumns from './advanceSaleProductColumns';
import tableColumnConfig from '@/config/tableColumnConfig';

const editorColumns = [];

function typeNameParser(_, { row }) {
    return advanceSaleOrderProductTypeMap[row.type].text;
}

baseColumns.forEach((column) => {
    switch (column.field) {
        case 'batchNumber':
            editorColumns.push({
                ...column,
                editRender: { name: '$input', props: { type: 'text' } }
            });
            editorColumns.push({
                ...tableColumnConfig.dateYMD,
                title: '生产日期',
                field: 'produceDate'
            });
            editorColumns.push({
                ...tableColumnConfig.dateYMD,
                title: '失效日期',
                field: 'overdueDate'
            });
            return;
        case 'quantity':
            editorColumns.push({
                ...column,
                editRender: { name: '$input', props: { type: 'number' } }
            });
            return;
        case 'unitId':
            editorColumns.push({
                ...column,
                editRender: { name: 'customSelecter' },
                params: {
                    customSelecterChangeMap: {
                        value: ['unitId'],
                        name: ['unitName']
                    }
                }
            });
            editorColumns.push({
                ...column,
                field: 'unitName'
            });
            return;
        case 'type':
            editorColumns.push({
                ...column,
                editRender: {
                    name: '$select',
                    options: Object.keys(advanceSaleOrderProductTypeMap).map((key) => ({
                        label: advanceSaleOrderProductTypeMap[key].text,
                        value: advanceSaleOrderProductTypeMap[key].value
                    }))
                }
            });
            editorColumns.push({
                ...column,
                field: 'typeName',
                cellRender: { name: 'valueParser', parser: typeNameParser }
            });
            return;
        case 'mdrfInnerSn':
            editorColumns.push({
                ...column,
                editRender: { name: 'customSelecter' },
                params: {
                    customSelecterChangeMap: {
                        value: ['mdrfInnerSn'],
                        label: ['mdrfSn']
                    }
                }
            });
            editorColumns.push({
                ...column,
                field: 'mdrfSn'
            });
            return;
        case 'healthCareSn':
            editorColumns.push({
                ...column,
                editRender: { name: '$input', props: { type: 'text' } }
            });
            return;
    }
    editorColumns.push(column);
});

export default editorColumns;
