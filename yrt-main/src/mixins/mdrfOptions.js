import mdrfMixin from './mdrf';

export default {
    mixins: [mdrfMixin],
    methods: {
        /**
         * 获取产品注册证列表下拉选项
         * @param {string | number} vendorId 厂商id
         * @param {string | number} productId 产品id
         */
        async loadMdrfOptions(vendorId, productId) {
            const list = await this.loadMdrfList(vendorId, productId);
            return list.map((mdrf) => ({
                value: mdrf.mdrf_inner_sn,
                label: mdrf.register_sn
            }));
        }
    }
};
