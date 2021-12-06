import warehouseOperatorMixin from './warehouseOperator';

export default {
    mixins: [warehouseOperatorMixin],
    methods: {
        /**
         * @param {string | number} id 仓库id
         */
        async loadWarehouseOperatorOptions(id) {
            const list = await this.loadWarehouseOperator(id);
            const options = list.map((operator) => ({
                value: operator.employee_inner_sn,
                label: operator.employee_name
            }));
            if (Object.prototype.hasOwnProperty.call(this.$data, 'warehouseOperatorOptions')) {
                this.warehouseOperatorOptions = options;
            }
            return list;
        }
    }
};
