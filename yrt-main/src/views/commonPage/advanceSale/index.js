/**
 * 业务人员使用的预售单
 */
import AdvanceSale from './AdvanceSale.vue';

export default {
    name: 'AdvanceSaleBusiness',
    provide: {
        isFinance: false
    },
    render(h) {
        return h(AdvanceSale);
    }
};
