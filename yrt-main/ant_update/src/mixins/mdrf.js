import { platformProductMdrfListAPI } from '@/service/pageAjax';

export default {
    methods: {
        /**
         * 获取产品注册证列表
         * @param {string | number} vendorId 厂商id
         * @param {string | number} productId 产品id
         */
        async loadMdrfList(vendorId, productId) {
            try {
                const { code, msg, list } = await platformProductMdrfListAPI(vendorId, productId);
                if (code !== 0) {
                    throw new Error(msg);
                }
                return list;
            } catch (err) {
                this.$message.error('产品注册证查询失败');
                throw err;
            }
        }
    }
};
