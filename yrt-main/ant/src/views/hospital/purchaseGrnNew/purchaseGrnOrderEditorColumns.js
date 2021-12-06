import tableColumnConfig from '@/config/tableColumnConfig';
import { purchaseGrnOrderProductTypeMap, rateTypeMap } from './type';

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
        field: 'unit_inner_sn',
        editRender: { name: 'customSelecter' },
        params: {
            customSelecterChangeMap: {
                value: ['unit_inner_sn'],
                name: ['unit_name']
            }
        }
    },
    {
        ...tableColumnConfig.rate,
        editRender: {
            name: '$select',
            options: Object.keys(rateTypeMap).map((key) => ({
                value: rateTypeMap[key].value,
                label: `${rateTypeMap[key].text}%`
            }))
        }
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
        ...tableColumnConfig.premiumPercent,
        width: tableColumnConfig.premiumPercent.width + 10,
        field: 'percent',
        editRender: { name: 'rate' },
        params: { in: 'percent' }
    },
    {
        ...tableColumnConfig.price,
        title: '售价',
        field: 'salesPrice',
        editRender: { name: '$input', props: { type: 'number' } }
    },
    {
        ...tableColumnConfig.price,
        title: '合计售价',
        field: 'totalSalesPrice',
        editRender: { name: '$input', props: { type: 'number' } }
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
        }
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
        }
    },
    {
        ...tableColumnConfig.healthCareSn,
        editRender: { name: '$input', props: { type: 'text' } }
    }
];
