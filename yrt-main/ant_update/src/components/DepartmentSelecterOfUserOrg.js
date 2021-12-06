/**
 * 当前登录用户所属机构下的部门选择下拉列表
 * 部门数据使用store中的departmentsForUserOrganization
 * 已绑定a-tree-select所支持的其它属性
 * 默认值：
 *      size: 'small'
 *      labelInValue: true
        showSearch: true,
        treeNodeFilterProp: 'title',
        dropdownStyle: { maxHeight: '400px', overflow: 'auto' },
        treeDefaultExpandAll: true
 * 使用示例:
 *     <department-selecter-of-user-org
 *         allowClear
 *         v-model={value}
 *     ></department-selecter-of-user-org>
 * or
 *     <department-selecter-of-user-org
 *         allowClear
 *         :value={value}
 *         @change="(value) => {}"
 *     ></department-selecter-of-user-org>
 */

/**
 * @typedef Department - props.value
 * @type {object}
 * @property {string} id - 部门id
 * @property {string} name - 部门名称
 */
export default {
    name: 'DepartmentSelecterOfUserOrg',
    model: {
        prop: 'value',
        event: 'change'
    },
    props: {
        /**
         * 当前选择值
         */
        value: {
            type: Object
        }
    },
    computed: {
        departments() {
            return this.$store.state.departmentsOfUserOrganization;
        }
    },
    created() {
        // 若departments仍为null，说明store还未加载部门数据
        if (this.departments === null) {
            this.dataLoaded = this.loadData();
        } else {
            this.dataLoaded = Promise.resolve();
        }
    },
    methods: {
        /**
         * @param {Department} value - 选择的部门
         */
        onSelectChange(value) {
            if (this.$attrs.labelInValue !== false && value) {
                this.$emit('change', { id: value.value, name: value.label });
                return;
            }
            this.$emit('change', value);
        },
        /**
         * 选中第一个用户所属的部门
         */
        handleSelectFirstDepartmentOfUser() {
            try {
                const loginData = JSON.parse(this.$cookie.get('loginDatas'));
                this.$emit('change', {
                    id: loginData.Departments[0].Department_inner_sn,
                    name: loginData.Departments[0].Name
                });
            } catch (err) {
                this.$message.warning('自动选择部门失败，请手动选择');
            }
        },
        /**
         * 从departments中找到第一个可用的部门
         * @param {boolean} leafFirst 是否优先找子部门，默认false
         * @returns {object} 第一个可用的部门
         */
        handleGetFirstUseableDepartment(leafFirst = false) {
            if (!this.departments) {
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
            return finder(this.departments);
        },
        /**
         * 选中用户所属机构下第一个可用部门
         */
        handleSelectFirstUseableDepartment() {
            if (this.dataLoaded) {
                this.dataLoaded.then(() => {
                    const department = this.handleGetFirstUseableDepartment();
                    if (department) {
                        this.$emit('change', { id: department.value, name: department.label });
                    }
                });
            }
        },
        /**
         * 通知store加载数据
         */
        async loadData() {
            await this.$store.dispatch('loadDepartmentsOfUserOrganization');
        }
    },
    render(h) {
        const { departments, value, onSelectChange, $attrs } = this;
        const restProps = {
            dropdownStyle: { maxHeight: '400px', overflow: 'auto' },
            size: 'small',
            treeDefaultExpandAll: true,
            showSearch: true,
            treeNodeFilterProp: 'title',
            ...$attrs,
            value,
            labelInValue: $attrs.labelInValue !== false ? true : false,
            treeData: departments
        };
        if (restProps.labelInValue && value) {
            // a-tree-select的value类型为Number时会抛出prop类型警告，所以转为String
            restProps.value = { value: value.id && String(value.id), label: value.name };
        }
        return h('a-tree-select', {
            props: restProps,
            on: {
                change: onSelectChange
            }
        });
    }
};
