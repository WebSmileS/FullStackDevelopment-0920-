import { orgDeptAllTreeAPI } from '@/service/pageAjax';

/**
 * 当前登录人员所属机构下的所有部门
 */
export default {
    methods: {
        /**
         * 获取部门列表
         */
        async loadDepartmentList() {
            try {
                const systemType = this.$cookie.get('userSystemType');
                const userBelong = this.$cookie.get('userbelong');
                const { code, msg, list } = await orgDeptAllTreeAPI(systemType, userBelong);
                if (code !== 0) {
                    throw new Error(msg);
                }
                return list;
            } catch (err) {
                this.$message.error(err.message);
                throw err;
            }
        }
    }
};
