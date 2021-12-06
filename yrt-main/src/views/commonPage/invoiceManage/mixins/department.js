import { orgDeptAllTreeAPI } from '@/service/pageAjax';

/**
 * 当前登录人员所属机构下的所有部门
 */
export default {
    data() {
        return {
            departmentsData: []
        };
    },
    methods: {
        /**
         * 获取部门列表
         */
        async loadDepartmentsData() {
            try {
                const systemType = this.$cookie.get('userSystemType');
                const userBelong = this.$cookie.get('userbelong');
                const { code, msg, list } = await orgDeptAllTreeAPI(systemType, userBelong);
                if (code !== 0) {
                    throw new Error(msg);
                }
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
                this.departmentsData = [];
                treeParse(list, this.departmentsData);
            } catch (err) {
                this.$message.error(err.message);
                throw err;
            }
        }
    },
    created() {
        this.loadDepartmentsData();
    }
};
