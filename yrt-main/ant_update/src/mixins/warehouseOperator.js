import { grnWeListAPI } from '@/service/pageAjax';

// 仓库人员列表
export default {
    methods: {
        /**
         * @param {string | number} id 仓库id
         */
        async loadWarehouseOperator(id) {
            try {
                const { code, info, msg } = await grnWeListAPI({ params: { wid: id } });
                if (code !== 0) {
                    throw new Error(msg);
                }
                return info;
            } catch (err) {
                this.$message.error('仓库人员加载失败');
                throw err;
            }
        }
    }
};
