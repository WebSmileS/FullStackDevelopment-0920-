import { expiryDateWarning } from '@/config/constants';

// tab选项卡的minxin
export const tabPane = {
    data() {
        return {
            tabsActiveKey: expiryDateWarning[0].value
        };
    },
    computed: {
        tabPane() {
            // num:    标识是哪个tab选项卡[索引]           - @required
            // action: 取标识tab选项卡的label 还是value值  - @required
            return (num, action = 'label') => expiryDateWarning[num][action];
        }
    }
};
