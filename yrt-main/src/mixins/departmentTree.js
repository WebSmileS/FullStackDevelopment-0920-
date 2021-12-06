import departmentMixin from './department';

/**
 * 当前登录人所属机构的所有部门树
 */
export default {
    mixins: [departmentMixin],
    methods: {
        /**
         * 从departmentTree中找到第一个可用的部门
         * @param {boolean} leafFirst 是否优先找子部门，默认false
         * @returns {object} 第一个可用的部门
         */
        handleGetFirstUseableDepartment(leafFirst = false) {
            if (!this.departmentTree) {
                return undefined;
            }
            const finder = (nodes) => {
                let result;
                for (let i = 0, l = nodes.length; i < l; i += 1) {
                    result = nodes[i];
                    if (!leafFirst && !result.disabled) {
                        return result;
                    }
                    if (nodes[i].children) {
                        result = finder(nodes[i].children);
                    }
                    if (result && !result.disabled) {
                        return result;
                    }
                }
                return result;
            };
            return finder(this.departmentTree);
        },

        /**
         * 加载部门树，若data中有departmentTree字段，更新值
         * @returns {array} 部门树数组
         */
        async loadDepartmentTree() {
            const list = await this.loadDepartmentList();
            const data = [];
            const treeParse = (list, resultArray) => {
                list.forEach((item) => {
                    const result = {
                        title: item.name,
                        key: item.department_inner_sn,
                        value: item.department_inner_sn,
                        disabled: item.status !== 1,
                        children: undefined
                    };
                    if (item.children && item.children.length) {
                        result.children = [];
                        treeParse(item.children, result.children);
                    }
                    resultArray.push(result);
                });
            };
            treeParse(list, data);
            if (Object.prototype.hasOwnProperty.call(this.$data, 'departmentTree')) {
                this.departmentTree = data;
            }
            return data;
        }
    }
};
