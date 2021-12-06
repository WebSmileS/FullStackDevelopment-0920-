export default {
    methods: {
        /**
         * 解析服务端扫码查询接口的返回值
         * @param {object} info 解析的数据
         * @param {boolean} info.existUDI 是否存在UDI码
         * @param {boolean} info.existUsabelProduct 是否存在匹配的业务产品
         * @param {array | null} info.usableProductList 匹配的业务产品列表
         * @param {boolean} info.onlyOtherInfo 条码是否只包含PI信息
         * @param {object} info.productBaseInfo 条码包含的PI信息
         * @param {string | null} info.msg 来自服务端的消息
         * @param {'DI' | 'PI'} target 解析的目标为DI码还是PI码
         * @returns {array | object}
         */
        _mixinParseBarcodeResult(info, target = 'DI') {
            const {
                existUDI,
                existUsabelProduct,
                usableProductList,
                onlyOtherInfo,
                productBaseInfo,
                msg: msgInner
            } = info;
            if (!existUDI) {
                throw new Error(msgInner || '未找到匹配的条码信息');
            }
            // PI信息识别
            if (target === 'PI') {
                return productBaseInfo;
            }
            // DI信息识别
            if (existUsabelProduct && usableProductList && usableProductList.length) {
                return usableProductList;
            }
            if (onlyOtherInfo) {
                throw new Error('无效的DI码');
            }
            throw new Error('未找到对应的产品');
        }
    }
};
