// 标识判断的minxin
export const identification = {
    computed: {
        // 是否是平台，true-平台
        isPlatform() {
            return parseInt(this.$cookie.get('userSystemType')) === 0;
        }
    }
};
