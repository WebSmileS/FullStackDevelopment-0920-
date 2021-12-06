import tableColumnConfig from '@/config/tableColumnConfig';
import { purchaseGrnOrderProductTypeMap, rateTypeMap } from './type';

const typeParser = (value) => {
    if (value === purchaseGrnOrderProductTypeMap.NORMAL.value) {
        return purchaseGrnOrderProductTypeMap.NORMAL.text;
    }
    if (value === purchaseGrnOrderProductTypeMap.GIVEAWAY.value) {
        return purchaseGrnOrderProductTypeMap.GIVEAWAY.text;
    }
    return '';
};

const mdrfParser = (_, { row }) => {
    return row.mdrf_sn;
};

// 收货单明细产品列表columns
export default [
    {
        ...tableColumnConfig.vendor,
        title: '厂商/经销商',
        field: 'product_vendor_name'
    },
    {
        ...tableColumnConfig.product
    },
    {
        ...tableColumnConfig.specification
    },
    {
        ...tableColumnConfig.batchNumber,
        editRender: { name: '$input', autoselect: true, props: { type: 'text' } }
    },
    {
        ...tableColumnConfig.count,
        title: '数量',
        field: 'quantity',
        editRender: { name: '$input', props: { type: 'number' } }
    },
    {
        ...tableColumnConfig.unit,
        field: 'unit_name'
    },
    {
        ...tableColumnConfig.price,
        title: '含税单价',
        field: 'unit_price',
        editRender: { name: '$input', props: { type: 'number' } }
    },
    {
        ...tableColumnConfig.price,
        title: '含税金额',
        field: 'money',
        editRender: { name: '$input', props: { type: 'number' } }
    },
    {
        ...tableColumnConfig.rate,
        editRender: {
            name: '$select',
            options: Object.keys(rateTypeMap).map((key) => ({
                value: rateTypeMap[key].value,
                label: `${rateTypeMap[key].text}%`
            }))
        },
        cellRender: { name: 'rate' }
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '生产日期',
        field: 'produce_date',
        editRender: { name: '$input', props: { type: 'date' } }
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '失效日期',
        field: 'overdue_date',
        editRender: { name: '$input', props: { type: 'date' } }
    },
    {
        ...tableColumnConfig.type,
        editRender: {
            name: '$select',
            options: Object.keys(purchaseGrnOrderProductTypeMap).map((key) => ({
                label: purchaseGrnOrderProductTypeMap[key].text,
                value: purchaseGrnOrderProductTypeMap[key].value
            }))
        },
        cellRender: { name: 'valueParser', parser: typeParser }
    },
    {
        ...tableColumnConfig.mdrf,
        field: 'mdrf_inner_sn',
        editRender: { name: 'customSelecter' },
        params: {
            customSelecterChangeMap: {
                value: ['mdrf_inner_sn'],
                label: ['mdrf_sn']
            }
        },
        cellRender: { name: 'valueParser', parser: mdrfParser }
    },
    {
        ...tableColumnConfig.healthCareSn,
        editRender: { name: '$input', props: { type: 'text' } }
    },
    {
        ...tableColumnConfig.barcode,
        title: '扫码（PI）',
        editRender: { name: 'barcodeInput', autoselect: true }
    }
];
