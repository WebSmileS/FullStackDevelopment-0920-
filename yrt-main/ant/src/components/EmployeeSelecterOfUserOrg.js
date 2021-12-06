/**
 * 当前登录用户所属机构下的人员选择下拉列表
 * 人员数据使用store中的employeesOfUserOrganization
 * 已绑定a-select所支持的其它属性
 * 默认值：
 *      size: 'small'
 *      labelInValue: true
 *      dropdownStyle: { maxHeight: '400px', overflow: 'auto' }
 *      size: 'small'
 *      showSearch: true
 * 使用示例:
 *     <employee-selecter-of-user-org
 *         allowClear
 *         v-model={value}
 *     ></employee-selecter-of-user-org>
 * or
 *     <employee-selecter-of-user-org
 *         allowClear
 *         :value={value}
 *         @change="(value) => {}"
 *     ></employee-selecter-of-user-org>
 */

/**
 * @typedef Employee - props.value
 * @type {object}
 * @property {string} id - 人员id
 * @property {string} name - 人员名字
 */
export default {
    name: 'EmployeeSelecterOfUserOrg',
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
        employees() {
            return this.$store.state.employeesOfUserOrganization;
        }
    },
    created() {
        // 若employees仍为null，说明store还未加载人员数据
        if (this.employees === null) {
            this.dataLoaded = this.loadData();
        } else {
            this.dataLoaded = Promise.resolve();
        }
    },
    methods: {
        /**
         * @param {Employee} value - 选择的用户
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
         * 选中登录人
         */
        handleSelectCurrentUser() {
            try {
                const loginData = JSON.parse(this.$cookie.get('loginDatas'));
                this.$emit('change', {
                    id: loginData.EmployeeInfo.Employee_inner_sn,
                    name: loginData.EmployeeInfo.Name
                });
            } catch (err) {
                this.$message.warning('自动选择人员失败，请手动选择');
            }
        },
        /**
         * 选中第一个登录人同机构可用的人员
         */
        handleSelectFirstUseableEmployee() {
            if (this.dataLoaded) {
                this.dataLoaded.then(() => {
                    if (this.employees && this.employees[0]) {
                        this.$emit('change', { id: this.employees[0].key, name: this.employees[0].label });
                    }
                });
            }
        },
        /**
         * 通知store加载数据
         */
        async loadData() {
            await this.$store.dispatch('loadEmployeesOfUserOrganization');
        }
    },
    render(h) {
        const { employees, value, onSelectChange, filterOption, $attrs } = this;
        const restProps = {
            dropdownStyle: { maxHeight: '400px', overflow: 'auto' },
            size: 'small',
            showSearch: true,
            filterOption,
            ...$attrs,
            value,
            labelInValue: $attrs.labelInValue !== false ? true : false,
            options: employees
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
