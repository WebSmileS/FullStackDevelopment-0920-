export const getCustomColumnKey = (() => {
    let id = 0;
    return () => {
        return `c${id++}`;
    };
})();

export function getEditKey(customKey) {
    return `${customKey}edit`;
}

export function getEditIconKey(customKey) {
    return `${customKey}editIcon`;
}

/**
 * 列编辑器
 * @description 编辑列标题、删除列，
 *              a-table并未提供此功能，所以借用自定义filter来实现此编辑功能
 */
const ColumnEditer = {
    name: 'ColumnEditer',
    props: {
        /**
         * 列配置
         */
        column: {
            type: Object,
            default: () => ({})
        },
        /**
         * 列名检测，阻止用户使用系统内已经存在的列名作为自定义列，进行单据仿造
         */
        columnTitleChecker: {
            type: Function,
            required: true
        }
    },
    data() {
        return {
            editingValue: this.$props.column.title
        };
    },
    methods: {
        onChange(event) {
            this.editingValue = event.currentTarget.value;
        },
        onSubmit() {
            if (this.columnTitleChecker(this.editingValue)) {
                this.$emit('submit', this.column.editKey, this.editingValue);
                this.$emit('close');
            } else {
                this.editingValue = this.column.title;
            }
        },
        onDelete() {
            this.$emit('delete', this.column.editKey);
            this.$emit('close');
        },
        handleFocus() {
            this.$refs.input.focus();
        }
    },
    render(h) {
        const { editingValue, onChange, onSubmit, onDelete } = this;
        return h('div', {}, [
            h('div', [
                h('a-input', {
                    ref: 'input',
                    style: {
                        width: '120px'
                    },
                    props: { size: 'small', placeholder: '输入列名', value: editingValue },
                    on: {
                        change: onChange,
                        pressEnter: onSubmit
                    }
                })
            ]),
            h('div', { style: { marginTop: '4px' } }, [
                h('a-button', { props: { type: 'danger', size: 'small' }, on: { click: onDelete } }, ['删除']),
                h(
                    'a-button',
                    {
                        style: {
                            marginLeft: '4px'
                        },
                        props: { type: 'primary', size: 'small' },
                        on: { click: onSubmit }
                    },
                    ['确定']
                )
            ])
        ]);
    }
};

/**
 * 单元格编辑器
 * @description 编辑单元格内的值
 */
const CellEditer = {
    name: 'CellEditer',
    props: {
        /**
         * 当前单元格的值
         */
        value: {
            type: [String, Number]
        }
    },
    data() {
        return {
            editable: false,
            editingValue: this.$props.value
        };
    },
    methods: {
        onEdit() {
            this.editable = true;
            this.editingValue = this.$props.value;
            this.$nextTick(() => {
                this.$refs.input.focus();
            });
        },
        onChange(event) {
            this.editingValue = event.currentTarget.value;
        },
        onSubmit() {
            this.editable = false;
            this.$emit('submit', this.editingValue);
        }
    },
    render(h) {
        const { value, editable, editingValue, onEdit, onChange, onSubmit } = this;
        if (editable) {
            return h('a-input', {
                ref: 'input',
                props: { value: editingValue },
                on: { change: onChange, pressEnter: onSubmit, blur: onSubmit }
            });
        }
        if (value || value === 0) {
            return h('div', { on: { click: onEdit } }, [value]);
        }
        return h('a-icon', { props: { type: 'edit' }, on: { click: onEdit } });
    }
};

/**
 * 自定义列表格
 */
export const CustomColumnEditableTable = {
    name: 'CustomColumnEditableTable',
    props: {
        /**
         * 所有的表格列
         */
        columns: {
            type: Array,
            default: []
        },
        /**
         * 编辑表格中只显示关键列,
         * 自定义列不受此参数影响
         */
        onlyShowKeyColumn: {
            type: Boolean,
            default: true
        },
        /**
         * 表格内容源数据
         */
        dataSource: {
            type: Array,
            default: []
        }
    },
    computed: {
        filteredColumns() {
            const { columns, onlyShowKeyColumn, onFilterDropdownVisibleChange } = this;
            if (!columns) {
                return [];
            }
            return columns
                .filter((column) => {
                    if (!column.enabled) {
                        return false;
                    }
                    if (column.isCustomColumn) {
                        return true;
                    }
                    if (!onlyShowKeyColumn) {
                        return true;
                    }
                    if (column.isKeyColumn) {
                        return true;
                    }
                    return false;
                })
                .map((column) => {
                    const _column = {
                        ...column,
                        width: Number(column.width) * 7
                    };
                    if (column.isCustomColumn) {
                        _column.onFilterDropdownVisibleChange = (visible) =>
                            onFilterDropdownVisibleChange(getEditKey(column.customKey), visible);
                        _column.editKey = getEditKey(column.customKey);
                        _column.editIconKey = getEditIconKey(column.customKey);
                        _column.scopedSlots = {
                            filterIcon: getEditIconKey(column.customKey),
                            filterDropdown: getEditKey(column.customKey),
                            customRender: column.customKey
                        };
                    }
                    return _column;
                });
        },
        customColumns() {
            return this.filteredColumns.filter((column) => column.isCustomColumn);
        }
    },
    methods: {
        onFilterDropdownVisibleChange(editKey, visible) {
            if (visible) {
                // 列编辑器展开时，自动聚焦
                setTimeout(() => {
                    this.$refs[editKey] && this.$refs[editKey].handleFocus();
                }, 0);
            }
        },
        columnTitleChecker(title) {
            if (!title) {
                return true;
            }
            const { columns } = this;
            const _title = title.replace(/\s*/g, '');
            for (let i = 0, l = columns.length; i < l; i += 1) {
                if (!columns[i].isCustomColumn && _title === columns[i].title) {
                    this.$warning({
                        title: '名称不可用',
                        content: `系统内已经存在此列名称：${columns[i].title}（可从 表格设置 > 表格列格式 中查看到所有的表格列）`,
                        okText: '知道了'
                    });
                    return false;
                }
            }
            return true;
        },
        handleSetColumnTitle(editKey, value) {
            const { columns } = this;
            for (let i = 0, l = columns.length; i < l; i += 1) {
                if (getEditKey(columns[i].customKey) === editKey) {
                    columns[i].title = value;
                    break;
                }
            }
        },
        handleAddColumn() {
            const existKeys = this.columns.filter((column) => column.isCustomColumn).map((column) => column.dataIndex);
            let customKey = getCustomColumnKey();
            while (existKeys.indexOf(customKey) >= 0) {
                customKey = getCustomColumnKey();
            }
            this.columns.push({
                title: '自定义列',
                key: customKey,
                dataIndex: customKey,
                isCustomColumn: true,
                customKey,
                enabled: true,
                width: 14
            });
        },
        handleDeleteColumn(editKey) {
            const { columns } = this;
            for (let i = 0, l = columns.length; i < l; i += 1) {
                if (getEditKey(columns[i].customKey) === editKey) {
                    columns.splice(i, 1);
                    break;
                }
            }
        }
    },
    render(h) {
        const {
            filteredColumns,
            dataSource,
            customColumns,
            columnTitleChecker,
            handleSetColumnTitle,
            handleDeleteColumn
        } = this;
        const scopedSlots = {};
        for (let i = 0, l = customColumns.length; i < l; i += 1) {
            const { customKey, editKey, editIconKey } = customColumns[i];
            // 自定义列头编辑器
            scopedSlots[editKey] = ({ confirm, column }) =>
                h(ColumnEditer, {
                    ref: editKey,
                    props: { column, columnTitleChecker },
                    on: { submit: handleSetColumnTitle, delete: handleDeleteColumn, close: confirm }
                });
            // 自定义列编辑图标
            scopedSlots[editIconKey] = () => {
                return h('a-icon', { props: { type: 'edit' } });
            };
            // 自定义列的单元格编辑器
            scopedSlots[customKey] = (_, record) => {
                return h(CellEditer, {
                    props: {
                        value: record && record[customKey]
                    },
                    on: {
                        submit: (value) => {
                            if (record) {
                                this.$set(record, customKey, value);
                            }
                        }
                    }
                });
            };
        }
        return h(
            'a-table',
            {
                class: {
                    'print-editor-form-wrapper': true
                },
                props: {
                    size: 'small',
                    rowKey: 'index',
                    pagination: false,
                    scroll: { x: filteredColumns.length * 120 },
                    columns: filteredColumns,
                    dataSource
                },
                scopedSlots
            },
            []
        );
    }
};
