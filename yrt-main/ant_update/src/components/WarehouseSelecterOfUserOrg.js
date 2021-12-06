/**
 * 当前登录用户可用的仓库列表下拉选择框
 * 仓库数据使用store中的warehouseList
 * 已绑定a-select所支持的其它属性
 * 默认值：
 *      size: 'small'
 *      labelInValue: true
 * 使用示例:
 *     <warehouse-selecter-of-user-org
 *         allowClear
 *         v-model={value}
 *     ></warehouse-selecter-of-user-org>
 * or
 *     <warehouse-selecter-of-user-org
 *         allowClear
 *         :value={value}
 *         @change="(value) => {}"
 *     ></warehouse-selecter-of-user-org>
 */

/**
 * @typedef Warehouse - props.value
 * @type {object}
 * @property {string} id - 仓库id
 * @property {string} name - 仓库名
 */
export default {
    name: 'WarehouseSelecterOfUserOrg',
    model: {
        prop: 'value',
        event: 'change'
    },
    props: {
        /**
         * 当前选择值
         */
        value: {
            type: [Number, String, Object]
        }
    },
    computed: {
        warehouseList() {
            return this.$store.state.warehouseList.map((warehouse) => ({
                key: warehouse.value,
                label: warehouse.name
            }));
        }
    },
    methods: {
        /**
         * @param {Warehouse} value - 选择的仓库
         */
        onSelectChange(value) {
            if (this.$attrs.labelInValue !== false && value) {
                this.$emit('change', { id: value.key, name: value.label });
                return;
            }
            this.$emit('change', value);
        }
    },
    render(h) {
        const { warehouseList, value, onSelectChange, $attrs } = this;
        const restProps = {
            ...$attrs,
            value,
            labelInValue: $attrs.labelInValue !== false ? true : false,
            size: $attrs.size || 'small',
            options: warehouseList
        };
        if (restProps.labelInValue && value) {
            // a-select的value类型为Number时会抛出prop类型警告，所以转为String
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
