import {
    pagePaymentAttachmentType,
    paymentTabFinance,
    receiptTabFinance
} from '@/views/commonPage/invoiceManage/config/constants';

// tab选项卡的minxin
export const tabPane = {
    computed: {
        tabPane() {
            // pageNameType:    标识是哪个页面进入[付款单还是收款单]            - @required
            // num:             标识是哪个tab选项卡[索引]                    - @required
            // action:          取标识tab选项卡的label 还是value值           - @required
            return (pageNameType, num, action = 'label') =>
                pageNameType === pagePaymentAttachmentType['PAYMENTORDER']['value']
                    ? paymentTabFinance[num][action]
                    : receiptTabFinance[num][action];
        }
    }
};
