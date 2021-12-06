import { dealerUnitTreeAPI, platformUnitTree } from '@/service/pageAjax';

export default {
    methods: {
        /**
         * 加载产品单位
         * @param {object} productInfo 产品信息
         * @param {string | number} productInfo.product_inner_sn 产品id
         * @param {string | number} productInfo.specification_inner_sn 规格型号id
         * @param {string | number} productInfo.vendor_inner_sn 生产厂商id，产品为厂商产品时
         * @param {string | number} productInfo.dealer_inner_sn 经销商id，产品为经销商产品时
         * @typedef Option
         * @type {object}
         * @property {string} label
         * @property {string} name
         * @property {string | number} value
         * @returns {Promise<Option[]>}
         */
        async loadProductUnitOptions(productInfo) {
            try {
                let response;
                if (productInfo['vendor_inner_sn'] !== undefined && productInfo['vendor_inner_sn'] !== null) {
                    response = await platformUnitTree(
                        productInfo['vendor_inner_sn'],
                        productInfo['product_inner_sn'],
                        productInfo['specification_inner_sn']
                    );
                }
                if (productInfo['dealer_inner_sn'] !== undefined && productInfo['dealer_inner_sn'] !== null) {
                    response = await dealerUnitTreeAPI(
                        productInfo['dealer_inner_sn'],
                        productInfo['product_inner_sn'],
                        productInfo['specification_inner_sn']
                    );
                }
                if (response) {
                    const { code, tree, msg } = response;
                    if (code !== 0) {
                        throw new Error(msg);
                    }
                    return this.$Utils.parseUnitTreeToArray(tree);
                }
                return [];
            } catch (err) {
                this.$message.error('产品单位获取失败');
                throw err;
            }
        }
    }
};
