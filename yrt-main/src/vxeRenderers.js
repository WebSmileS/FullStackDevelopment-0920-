import Vue from 'vue';

export default [
    // 通过在renderOpts中定义一个转换函数parser(函数应该返回Number或String类型的值)，
    // 渲染单元格内容时调用转换函数后再将结果进行渲染
    // 可选的：如果renderOpts中定义了getClassName函数，单元格将使用通过此函数获取到的类名
    [
        'valueParser',
        {
            renderDefault(h, renderOpts, payload) {
                const { row, column } = payload;
                let className;
                if (renderOpts.getClassName) {
                    className = renderOpts.getClassName(row[column.property], payload);
                }
                return [<div class={className}>{renderOpts.parser(row[column.property], payload)}</div>];
            }
        }
    ],
    // 列头排序
    // vxe-table自带的排序同一时间只能激活一列
    // 此渲染器排序可以在多列同时被激活
    // 排序被点击时，通过sort-change事件返回列对应的排序状态
    // 排序的激活状态通过column的renderOptions传递至渲染器内
    [
        'headerSort',
        {
            renderHeader(h, renderOpts, { $table, column }) {
                let ascClassName = 'vxe-sort--asc-btn vxe-icon--caret-top';
                let descClassName = 'vxe-sort--desc-btn vxe-icon--caret-bottom';
                const onAscClick = () => {
                    $table.$emit('sort-change', {
                        property: column.property,
                        order: renderOpts.order === 'asc' ? null : 'asc'
                    });
                };
                const onDescClick = () => {
                    $table.$emit('sort-change', {
                        property: column.property,
                        order: renderOpts.order === 'desc' ? null : 'desc'
                    });
                };
                if (renderOpts.order === 'asc') {
                    ascClassName += ' sort--active';
                } else if (renderOpts.order === 'desc') {
                    descClassName += ' sort--active';
                }
                return [
                    <span>{column.title}</span>,
                    <span class="vxe-cell--sort">
                        <i title="升序" class={ascClassName} onClick={onAscClick}></i>
                        <i title="降序" class={descClassName} onClick={onDescClick}></i>
                    </span>
                ];
            }
        }
    ],

    // 税率
    // 非编辑状态下，添加百分号显示
    // 当column.params.in 不为'percent'时，自动转为百分比进行显示和编辑
    [
        'rate',
        {
            autofocus: '.vxe-input--inner',
            renderDefault(h, renderOpts, { row, column }) {
                let value = row[column.property];
                if (value === undefined || value === null || isNaN(value)) {
                    return [];
                }
                if (typeof value === 'string') {
                    value = Number(value);
                    if (isNaN(value)) {
                        return [<span>{row[column.property]}</span>];
                    }
                }
                if (column.params && column.params.in === 'percent') {
                    return [<span>{`${value} %`}</span>];
                }
                return [<span>{`${value * 100} %`}</span>];
            },
            renderEdit(h, renderOpts, { row, column }) {
                let value = row[column.property];
                const onChange = (event) => {
                    if (isNaN(Number(event.value))) {
                        row[column.property] = event.value;
                        return;
                    }
                    if (column.params && column.params.in === 'percent') {
                        row[column.property] = Number(event.value);
                        return;
                    }
                    row[column.property] = Number(event.value) / 100;
                };
                if ((!column.params || column.params.in !== 'percent') && value !== '-') {
                    value = value * 100;
                }

                return [<vxe-input type="number" step="1" value={value} onChange={onChange}></vxe-input>];
            },
            renderCell(h, renderOpts, { row, column }) {
                let value = row[column.property];
                if (value === undefined || value === null || isNaN(value)) {
                    return [];
                }
                if (typeof value === 'string') {
                    value = Number(value);
                    if (isNaN(value)) {
                        return [<span>{row[column.property]}</span>];
                    }
                }
                if (column.params && column.params.in === 'percent') {
                    return [<span>{`${value} %`}</span>];
                }
                return [<span>{`${value * 100} %`}</span>];
            }
        }
    ],
    // 单位选择编辑渲染器
    // 显示时自动将value映射为label值
    [
        'unitSelecter',
        {
            autofocus: '.vxe-input--inner',
            renderEdit(h, renderOpts, { row, column }) {
                return [
                    <a-spin size="small" spinning={row.unitLoading}>
                        <vxe-select options={row.unitSelecterOptions || []} v-model={row[column.property]} />
                    </a-spin>
                ];
            },
            renderCell(h, renderOpts, { row, column }) {
                if (!row.unitSelecterOptions) {
                    return [<span>{row[column.property]}</span>];
                }
                let text = '';
                for (let i = 0, l = row.unitSelecterOptions.length; i < l; i += 1) {
                    if (row.unitSelecterOptions[i].value === row[column.property]) {
                        text = row.unitSelecterOptions[i].name;
                        break;
                    }
                }
                return [<span>{text}</span>];
            }
        }
    ],
    // 自定义选择器
    // 显示时自动将value映射为label
    // 选择时更改行数据的value值，若设置了额外的label key，自动根据options中的label值设置行数据的对应值
    [
        'customSelecter',
        {
            autofocus: '.vxe-input--inner',
            renderEdit(h, renderOpts, { row, column }) {
                const { options = {}, optionsLoading } = row;
                const { property, params = {} } = column;
                const { customSelecterChangeMap } = params;
                const columnOptions = options[property] || [];
                const onChange = ({ value }) => {
                    let option;
                    for (let i = 0, l = columnOptions.length; i < l; i += 1) {
                        if (columnOptions[i].value === value) {
                            option = columnOptions[i];
                            break;
                        }
                    }
                    // 若设置了值更新map，根据map设置对应的值
                    if (customSelecterChangeMap) {
                        Object.keys(customSelecterChangeMap).forEach((sourceKey) => {
                            customSelecterChangeMap[sourceKey].forEach((targetKey) => {
                                Vue.set(row, targetKey, option[sourceKey]);
                            });
                        });
                        return;
                    }
                    Vue.set(row, property, option.value);
                };
                return [
                    <a-spin size="small" spinning={optionsLoading && optionsLoading[property]}>
                        <vxe-select options={columnOptions} value={row[property]} onChange={onChange} />
                    </a-spin>
                ];
            },
            renderCell(h, renderOpts, { row, column }) {
                if (!row.options || !row.options[column.property]) {
                    return [<span>{row[column.property]}</span>];
                }
                let text = '';
                for (let i = 0, l = row.options[column.property].length; i < l; i += 1) {
                    if (row.options[column.property][i].value === row[column.property]) {
                        text = row.options[column.property][i].label;
                        break;
                    }
                }
                return [<span>{text}</span>];
            }
        }
    ],
    // 扫码输入框
    // 当扫码输入完成（即触发Enter事件）后，vxe-table会触发barcodeInputFinish事件
    [
        'barcodeInput',
        {
            autofocus: '.vxe-input--inner',
            renderHeader(h, renderOpts, params) {
                return [
                    <a-icon type="scan" style="position: relative; top: -1px; margin-right: 2px" />,
                    <span>{params.column.title}</span>
                ];
            },
            renderEdit(h, renderOpts, params) {
                const onKeydown = ({ $event }) => {
                    if ($event.key === 'Enter') {
                        $event.stopPropagation();
                        params.$table.$emit('barcodeInputFinish', $event.currentTarget.value, params);
                    }
                };
                return [
                    <a-spin size="small" spinning={params.row.barcodeInputLoading || false}>
                        <vxe-input onKeydown={onKeydown}></vxe-input>
                    </a-spin>
                ];
            },
            renderCell(h, renderOpts, { row, column }) {
                return [
                    <a-spin size="small" spinning={row.barcodeInputLoading || false}>
                        <div style="min-height: 1em">{row[column.property]}</div>
                    </a-spin>
                ];
            }
        }
    ]
];
