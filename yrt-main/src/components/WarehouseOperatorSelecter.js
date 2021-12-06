/**
 * 仓库相关人员选择下拉列表
 * 已绑定a-select所支持的其它属性
 * 默认值：
 *      size: 'small'
 *      labelInValue: true
 *      dropdownStyle: { maxHeight: '400px', overflow: 'auto' }
 *      size: 'small'
 *      showSearch: true
 * 使用示例:
 *     <warehouse-operator-selecter
 *         allowClear
 *         v-model={value}
 *     ></warehouse-operator-selecter>
 * or
 *     <warehouse-operator-selecter
 *         allowClear
 *         :value={value}
 *         @change="(value) => {}"
 *     ></warehouse-operator-selecter>
 */

/**
 * @typedef Operator - props.value
 * @type {object}
 * @property {string} id - 人员id
 * @property {string} name - 人员名字
 */
import { grnWeListAPI } from '@/service/pageAjax';

export default {
    name: 'WarehouseOperatorSelecter',
    model: {
        prop: 'value',
        event: 'change'
    },
    props: {
        /**
         * 仓库id
         */
        warehouseId: {
            type: [Number, String]
        },
        /**
         * 当前选择值
         */
        value: {
            type: Object
        }
    },
    data() {
        return {
            operatorOptions: null
        };
    },
    watch: {
        warehouseId(newValue) {
            if (newValue || newValue === 0) {
                this.dataLoaded = this.loadData();
                return;
            }
            this.operatorOptions = null;
        }
    },
    methods: {
        /**
         * @param {Operator} value - 选择的用户
         */
        onSelectChange(value) {
            if (this.$attrs.labelInValue !== false && value) {
                this.$emit('change', { id: value.key, name: value.label });
                return;
            }
            this.$emit('change', value);
        },
        /**
         * a-select搜索过滤函数
         */
        filterOption(input, option) {
            return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
        },
        /**
         * 选中第一个可用的人员
         */
        handleSelectFirstOperator() {
            if (this.dataLoaded) {
                this.dataLoaded.then(() => {
                    if (this.operatorOptions && this.operatorOptions[0]) {
                        this.$emit('change', {
                            id: this.operatorOptions[0].value,
                            name: this.operatorOptions[0].label
                        });
                    }
                });
            }
        },
        /**
         * 加载目标仓库的操作人员数据
         */
        async loadData() {
            const { code, info, msg } = await grnWeListAPI({ params: { wid: this.warehouseId } });
            if (code !== 0) {
                this.$message.error(msg);
                throw new Error(msg);
            }
            this.operatorOptions = info.map((operator) => ({
                value: operator.employee_inner_sn,
                label: operator.employee_name
            }));
        }
    },
    render(h) {
        const { operatorOptions, value, onSelectChange, filterOption, $attrs } = this;
        const restProps = {
            dropdownStyle: { maxHeight: '400px', overflow: 'auto' },
            size: 'small',
            showSearch: true,
            filterOption,
            ...$attrs,
            value,
            labelInValue: $attrs.labelInValue !== false ? true : false,
            options: operatorOptions
        };
        if (restProps.labelInValue && value) {
            // a-tree-select的value类型为Number时会抛出prop类型警告，所以转为String
            restProps.value = { value: value.id && String(value.id), label: value.name };
        }
        return h('a-select', {
            props: restProps,
            on: {
                change: onSelectChange
            }
        });
    }
};
