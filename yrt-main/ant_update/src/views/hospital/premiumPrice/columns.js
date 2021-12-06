import tableColumnConfig from '@/config/tableColumnConfig';

export default [
    {
        ...tableColumnConfig.price,
        title: '最低价',
        field: 'minPrice',
        editRender: { name: '$input', autoselect: true, props: { type: 'number' } }
    },
    {
        ...tableColumnConfig.price,
        title: '最高价',
        field: 'maxPrice',
        editRender: { name: '$input', autoselect: true, props: { type: 'number' } }
    },
    {
        ...tableColumnConfig.premiumPercent,
        field: 'percent',
        width: tableColumnConfig.premiumPercent.width + 10,
        editRender: { name: 'rate', autoselect: true },
        params: { in: 'percent' }
    },
    {
        ...tableColumnConfig.description,
        field: 'remake',
        editRender: { name: '$input', autoselect: true, props: { type: 'text' } }
    }
];
