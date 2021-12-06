export const STORE_NAME = 'invoiceSearch';
export const SET_GRN_INVOICE_INFO = 'SET_GRN_INVOICE_INFO';
export const SET_ODO_INVOICE_INFO = 'SET_ODO_INVOICE_INFO';
export const CLEAR_GRN_INVOICE_INFO = 'CLEAR_GRN_INVOICE_INFO ';
export const CLEAR_ODO_INVOICE_INFO = 'CLEAR_ODO_INVOICE_INFO ';

export const store = {
    namespaced: true,
    state: () => ({
        grnInvoiceInfo: null,
        odoInvoiceInfo: null
    }),
    mutations: {
        [SET_GRN_INVOICE_INFO](state, info) {
            state.grnInvoiceInfo = {
                ...info,
                products: info.products.map((product) => ({ ...product }))
            };
        },
        [SET_ODO_INVOICE_INFO](state, info) {
            state.odoInvoiceInfo = {
                ...info,
                products: info.products.map((product) => ({ ...product }))
            };
        },
        [CLEAR_GRN_INVOICE_INFO](state) {
            state.grnInvoiceInfo = null;
        },
        [CLEAR_ODO_INVOICE_INFO](state) {
            state.odoInvoiceInfo = null;
        }
    }
};
