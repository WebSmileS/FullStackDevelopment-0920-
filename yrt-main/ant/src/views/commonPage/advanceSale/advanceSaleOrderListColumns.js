import tableColumnConfig from '@/config/tableColumnConfig';
import {
    advanceSaleOrderAuditStatusColor,
    advanceSaleOrderAuditStatusTextMap,
    advanceSaleOrderStatusColor,
    advanceSaleOrderStatusTextMap
} from './type';

function statusTextParser(value) {
    return advanceSaleOrderStatusTextMap[value];
}

function getStatusClassName(value) {
    return advanceSaleOrderStatusColor[value];
}

function auditStatusTextParser(value) {
    return advanceSaleOrderAuditStatusTextMap[value];
}

function getAuditStatusClassName(value) {
    return advanceSaleOrderAuditStatusColor[value];
}

export default [
    {
        ...tableColumnConfig.sn,
        field: 'advanceSaleNo'
    },
    {
        ...tableColumnConfig.organization,
        title: '采购方名称',
        field: 'buyerName'
    },
    {
        ...tableColumnConfig.department,
        title: '开单部门',
        field: 'saleDepartmentName'
    },
    {
        ...tableColumnConfig.warehouse,
        field: 'warehouseName'
    },
    {
        ...tableColumnConfig.personName,
        title: '仓库人员',
        field: 'warehouseUserName'
    },
    {
        ...tableColumnConfig.dateYMDHMS,
        title: '期望到货日期',
        field: 'expectAogDate'
    },
    {
        ...tableColumnConfig.personName,
        title: '创建人',
        field: 'createUserName'
    },
    {
        ...tableColumnConfig.personName,
        title: '经手人',
        field: 'agentUserName'
    },
    {
        ...tableColumnConfig.status,
        cellRender: { name: 'valueParser', parser: statusTextParser, getClassName: getStatusClassName }
    },
    {
        ...tableColumnConfig.status,
        title: '审核状态',
        field: 'auditStatus',
        cellRender: { name: 'valueParser', parser: auditStatusTextParser, getClassName: getAuditStatusClassName }
    },
    {
        ...tableColumnConfig.personName,
        title: '审核人',
        field: 'auditUserName'
    },
    {
        ...tableColumnConfig.dateYMDHMS,
        title: '审核时间',
        field: 'auditTime'
    },
    {
        ...tableColumnConfig.description,
        title: '描述'
    }
];
