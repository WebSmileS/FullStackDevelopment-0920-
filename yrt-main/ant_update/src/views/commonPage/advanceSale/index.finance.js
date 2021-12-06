/**
 * 财务人员使用的预售单页面
 */
import AdvanceSale from './AdvanceSale.vue';

export default {
    name: 'AdvanceSaleFinance',
    provide: {
        isFinance: true
    },
    render(h) {
        return h(AdvanceSale);
    }
};
