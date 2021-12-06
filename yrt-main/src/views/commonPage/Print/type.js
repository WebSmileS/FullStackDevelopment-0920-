export const unitTypeMap = {
    mm: { value: 'mm', text: '毫米（mm）' },
    cm: { value: 'cm', text: '厘米（cm）' }
};

export const paperTypeMap = {
    entire: { value: 'entire', text: '一等分纸' },
    bisection: { value: 'bisection', text: '二等分纸' },
    trisection: { value: 'trisection', text: '三等分纸' },
    A4: { value: 'A4', text: 'A4' },
    custom: { value: 'custom', text: '自定义' }
};

export const paperSizeMap = {
    entire: { width: 241, height: 279, unit: unitTypeMap.mm.value },
    bisection: { width: 241, height: 139.5, unit: unitTypeMap.mm.value },
    trisection: { width: 241, height: 93, unit: unitTypeMap.mm.value },
    A4: { width: 210, height: 297, unit: unitTypeMap.mm.value },
    custom: { width: 241, height: 279, unit: unitTypeMap.mm.value }
};

export const alignTypeMap = {
    left: { value: 'left', text: '左端' },
    center: { value: 'center', text: '居中' },
    right: { value: 'right', text: '右端' }
};

export const verticalAlignTypeMap = {
    top: { value: 'top', text: '顶端' },
    middle: { value: 'middle', text: '居中' },
    bottom: { value: 'bottom', text: '底端' }
};

export const dataTypeMap = {
    string: { value: 'string', text: '字符串' },
    number: { value: 'number', text: '数值' }
};
