import { orgEmpDropdownListAPI } from '@/service/pageAjax';

/**
 * 当前登录人员所属机构下的所有人员列表
 */
export default {
    data() {
        return {
            employeesData: [],
            employeesDataLoaded: false
        };
    },
    methods: {
        /**
         * 初始化
         */
        async handleInitEmployeesMixin() {
            await this.loadEmployeesData();
        },

        /**
         * 获取人员列表
         */
        async loadEmployeesData() {
            try {
                const { code, msg, list } = await orgEmpDropdownListAPI();
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.employeesData = list.map((item) => ({
                    ...item,
                    title: item.name,
                    label: item.name,
                    key: item.employee_inner_sn,
                    value: item.employee_inner_sn
                }));
                this.employeesDataLoaded = true;
            } catch (err) {
                this.$message.error(err.message);
                throw err;
            }
        }
    }
};
