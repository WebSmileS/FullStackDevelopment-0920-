<template>
    <div>
        <a-modal
            :title="detailMd.title"
            v-model="isOpen"
            :maskClosable="false"
            :width="1100"
            :centered="true"
            @cancel="closeDetailModalFn"
            class="modal-form-input-scoped global-drag invoice-detail-modal"
        >
            <div v-globalDrag="{ el: 'invoice-detail-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ detailMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="detailFn('CLOSE')">
                    {{ closeButtonText }}
                </a-button>
                <a-button
                    v-if="isShowSaveAndPayBtn"
                    @click="detailFn('SAVE')"
                    :loading="detailMd.saveLoading"
                    type="primary"
                    >保存</a-button
                >
                <a-button
                    v-if="isShowSaveAndPayBtn"
                    @click="detailFn('PAY')"
                    :loading="detailMd.paymentAttachLoading"
                    type="primary"
                    >{{ modalTitle }}</a-button
                >
                <a-button
                    v-if="isShowRecordedAndCancelBtn"
                    @click="detailFn('RECORDED')"
                    :loading="detailMd.recordedLoading"
                    type="danger"
                    >入账</a-button
                >
                <a-button
                    v-if="isShowRecordedAndCancelBtn"
                    @click="detailFn('CANCEL')"
                    :loading="detailMd.cancelLoading"
                    type="danger"
                    >作废</a-button
                >
            </template>
            <div class="form-container">
                <!-- 标题区域 -->
                <caption-text
                    :captionData="{
                        ...captionData,
                        caption: `${modalTitle}单`
                    }"
                ></caption-text>

                <!-- 发票的上半部分 表单区域 -->
                <invoice-header
                    :btnName="btnName"
                    :isInbound="isInbound"
                    :isReadonly="isReadonly"
                    :formData.sync="headerFormData"
                ></invoice-header>

                <!-- 发票的中间部分 表格区域 -->
                <invoice-table
                    :isInbound="isInbound"
                    :isReadonly="isReadonly"
                    :tabsActiveKey="tabsActiveKey"
                    :formData="{
                        ...headerFormData,
                        ...footerFormData
                    }"
                    :modalTitle="modalTitle"
                    :columns="detailColumns"
                    :datas="detailDatas"
                    :pagation="detailPagation"
                    @getTableData="getTableDataFn"
                    @delTableData="delTableDataFn"
                ></invoice-table>

                <!-- 发票的下半部分 表单摘要区域 -->
                <invoice-footer
                    :isInbound="isInbound"
                    :isReadonly="isReadonly"
                    :isShowPayment="true"
                    :paymentLabel="isInbound ? '付款' : '收款'"
                    :isShowCancel="isShowCancel"
                    :formData.sync="footerFormData"
                ></invoice-footer>
            </div>
        </a-modal>

        <!-- 作废原因 -->
        <invalid-reason-md
            :isOpen="invalidReasonIsOpen"
            @saveInvalidReasonData="saveInvalidReasonDataFn"
        ></invalid-reason-md>
    </div>
</template>

<script>
import invalidReasonMd from '@/views/commonPage/invoiceManage/components/invalidReason';
import captionText from '@/views/commonPage/invoiceManage/components/captionText';
import invoiceHeader from './invoiceHeader';
import invoiceFooter from '@/views/commonPage/invoiceManage/components/invoiceFooter';
import invoiceTable from './invoiceTable';
import {
    pagePaymentAttachmentType,
    paymentInvoiceState,
    receiveInvoiceState,
    format,
    grnAndOdoInvoiceOrderType
} from '@/views/commonPage/invoiceManage/config/constants';

import {
    financePaymentAddAPI,
    financeReceiveAddAPI,
    financePaymentEditAPI,
    financeReceiveEditAPI,
    financePaymentInfoAPI,
    financeReceiveInfoAPI,
    financePaymentCancelAPI,
    financeReceiveCancelAPI,
    financePaymentRecordedAPI,
    financeReceiveRecordedAPI
} from '@/service/pageAjax';

import moment from 'moment';

export default {
    name: 'tableDetail', // 入库|出库发票的详情 = 财务人员使用
    props: {
        btnName: {
            // 按钮行为
            type: String,
            required: true
        },
        isInbound: {
            type: Boolean, // 是否是入库发票 === 默认是true
            default: true
        },
        isOpen: {
            // 模态框窗口是否打开标识， true、false
            type: Boolean,
            default: true
        },
        rowData: {
            type: Object // 当前表格双击查看当前行的列表数据
        },
        isShowCancel: {
            // 是否是作废单
            type: Boolean,
            default: false
        }
    },
    components: {
        invalidReasonMd,
        captionText,
        invoiceHeader,
        invoiceFooter,
        invoiceTable
    },
    data() {
        return {
            detailMd: {
                title: `${this.$route.meta.title}`,
                saveLoading: false,
                paymentAttachLoading: false,
                recordedLoading: false,
                cancelLoading: false
            },
            detailDatas: [],
            detailPagation: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            invalidReasonIsOpen: false,
            captionData: {
                caption: '',
                label: '',
                value: ''
            },
            headerFormData: {
                bills_type: '',
                department_inner_sn: '',
                department_name: '',
                handler_inner_sn: '',
                handler_name: '',
                invoice_date: '',
                invoice_time: '',
                invoice_no: '',
                invoice_price: '',
                invoice_status: '',
                invoice_type: '',
                organization_inner_sn: '',
                organization_name: '',
                rate: '',
                system_invoice_sn: '',
                system_invoice_type: '',
                system_type: '',
                id: ''
            },
            footerFormData: {
                description: '',
                created_by: '--',
                created_time: '--',
                confirm_by: '--',
                confirm_time: '--',
                cancel_by: '--',
                cancel_time: '--',
                cancel_reason: '--',
                payment_by: '--',
                payment_time: '--',
                receive_by: '--',
                receive_time: '--'
            },
            detail: [],
            isfreshParentTableList: false, // 是否刷新父级表格数据
            invalidReasonData: '' // 作废原因
        };
    },
    methods: {
        closeDetailModalFn() {
            this.$emit('closeDetailModal', false, this.isfreshParentTableList);
            this.detail = [];
            this.detailDatas = [];
            this.captionData.value = '';
            this.captionData.caption = '';
            this.headerFormData = {
                isCancel: false, // 作废标识
                bills_type: '',
                department_inner_sn: '',
                department_name: '',
                handler_inner_sn: '',
                handler_name: '',
                invoice_date: '',
                invoice_no: '',
                invoice_price: '',
                invoice_status: '',
                invoice_type: '',
                organization_inner_sn: '',
                organization_name: '',
                rate: '',
                system_invoice_sn: '',
                system_invoice_type: '',
                system_type: '',
                id: ''
            };
            this.footerFormData = {
                description: '',
                created_by: '--',
                created_time: '--',
                confirm_by: '--',
                confirm_time: '--',
                cancel_by: '--',
                cancel_time: '--',
                cancel_reason: '--',
                payment_by: '--',
                payment_time: '--',
                receive_by: '--',
                receive_time: '--'
            };
        },
        getTableDataFn(tableData) {
            if (tableData) {
                tableData.forEach((item) => {
                    this.detailDatas.push(item);
                });
            }
        },
        delTableDataFn(rowIndex) {
            this.detailDatas.splice(rowIndex, 1);
        },
        checkFormFn() {
            // 校验表单
            if (!this.headerFormData.organization_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: `请选择相关机构`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (!this.headerFormData.invoice_type) {
                this.$warning({
                    title: '操作提示',
                    content: `请选择${this.paymentReceiveLabel}方式`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (!this.headerFormData.invoice_date) {
                this.$warning({
                    title: '操作提示',
                    content: `请选择${this.paymentReceiveLabel}日期`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            // 校验表格数据
            if (this.detailDatas && this.detailDatas.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: `请双击添加发票`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (!this.headerFormData.invoice_price) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.paymentReceiveLabel}金额不能为空`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (this.headerFormData.invoice_price <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.paymentReceiveLabel}金额设置无效，请修改明细核销金额！`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            let isPass = false,
                reapetSnObj = {};
            for (let i = 0; i < this.detailDatas.length; i++) {
                let item = this.detailDatas[i];
                if (typeof item.written_off_price === 'undefined' || !item.written_off_price.toString()) {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${i + 1} 请填写核销金额`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    isPass = true;
                    break;
                }
                if (!reapetSnObj[item.sn]) {
                    reapetSnObj[item.sn] = {
                        counter: 1, // 计数器
                        seq: i + 1 // 当前所处索引
                    };
                } else {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${reapetSnObj[item.sn].seq} 和 NO.${
                            i + 1
                        } 的发票编号被重复添加，请删除其中一个后再操作！`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    isPass = true;
                    break;
                }
            }

            return isPass;
        },
        formatDetailFn() {
            return JSON.parse(JSON.stringify(this.detailDatas)).map((item) => {
                if (this.btnName === 'add') {
                    delete item.id;
                }
                return {
                    invoice_no: item.invoice_no,
                    invoice_price: item.invoice_price,
                    sn: item.sn,
                    voucher_inner_sn_c: item.voucher_inner_sn_c,
                    voucher_type_c: item.voucher_type_c,
                    written_off_price: item.written_off_price
                };
            });
        },
        async invoiceAddAPIFn(status) {
            const service = this.isInbound ? financePaymentAddAPI : financeReceiveAddAPI;

            delete this.headerFormData.id;

            const params = {
                bills_type: this.headerFormData.bills_type,
                organization_inner_sn: this.headerFormData.organization_inner_sn,
                organization_name: this.headerFormData.organization_name,
                description: this.footerFormData.description,
                details: this.formatDetailFn()
            };

            if (this.isInbound) {
                // 付款单
                params.payment_date = moment(this.headerFormData.invoice_date).format(format);
                params.payment_method = this.headerFormData.invoice_type;
                params.payment_price = this.headerFormData.invoice_price;
                params.payment_status = status;
            } else {
                // 收款单
                params.receive_date = moment(this.headerFormData.invoice_date).format(format);
                params.receive_method = this.headerFormData.invoice_type;
                params.receive_price = this.headerFormData.invoice_price;
                params.receive_status = status;
            }

            await service(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.$emit('saveDetailModal', info);
                        this.closeDetailModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.detailMd.saveLoading = false;
                    this.detailMd.paymentAttachLoading = false;
                });
        },
        async invoiceEditAPIFn(status) {
            const service = this.isInbound ? financePaymentEditAPI : financeReceiveEditAPI;

            const params = {
                id: this.headerFormData.id,
                bills_type: this.headerFormData.bills_type,
                organization_inner_sn: this.headerFormData.organization_inner_sn,
                organization_name: this.headerFormData.organization_name,
                description: this.footerFormData.description,
                details: this.formatDetailFn()
            };

            if (this.isInbound) {
                // 付款单
                params.payment_date = moment(this.headerFormData.invoice_date).format(format);
                params.payment_method = this.headerFormData.invoice_type;
                params.payment_price = this.headerFormData.invoice_price;
                params.payment_status = status || this.headerFormData.invoice_status;
            } else {
                // 收款单
                params.receive_date = moment(this.headerFormData.invoice_date).format(format);
                params.receive_method = this.headerFormData.invoice_type;
                params.receive_price = this.headerFormData.invoice_price;
                params.receive_status = status || this.headerFormData.invoice_status;
            }
            await service(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.$emit('saveDetailModal', info);
                        this.closeDetailModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.detailMd.saveLoading = false;
                    this.detailMd.paymentAttachLoading = false;
                    this.detailMd.recordedLoading = false;
                });
        },
        /**
         * 入账收（付）款单
         * @param {(number | string)[]} idArray id数组
         * @param {boolean} [isPayment=true] 为付款单，否则为收款单
         */
        async recordReceiveOrPayment(idArray, isPayment = true) {
            if (typeof idArray !== 'object' || !idArray.length) {
                return;
            }
            try {
                const service = isPayment ? financePaymentRecordedAPI : financeReceiveRecordedAPI;
                const { code, msg } = await service(idArray);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.$message.success('入账成功');
            } catch (err) {
                this.$message.error(err.message);
                throw err;
            }
        },
        /**
         * 作废收（付）款单
         */
        async financeInvoiceCancelAPIFn() {
            this.detailMd.cancelLoading = true;
            const service = this.isInbound ? financePaymentCancelAPI : financeReceiveCancelAPI;
            await service(this.rowData.id, this.invalidReasonData)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.isfreshParentTableList = true;
                        this.closeDetailModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.detailMd.cancelLoading = false;
                });
        },
        async invoiceDetailAPIFn() {
            const service = this.isInbound ? financePaymentInfoAPI : financeReceiveInfoAPI;
            await service(this.rowData.id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;

                        this.headerFormData = {
                            isCancel: info.is_cancel === 1,
                            bills_type: info.bills_type,
                            department_inner_sn: info.department_inner_sn || '',
                            department_name: info.department_name || '',
                            handler_inner_sn: info.handler_inner_sn || '',
                            handler_name: info.handler_name || '',
                            organization_inner_sn: info.organization_inner_sn,
                            organization_name: info.organization_name,
                            rate: `${parseFloat(info.rate) * 100}`,
                            system_invoice_sn: info.system_invoice_sn,
                            system_invoice_type: info.system_invoice_type,
                            system_type: info.system_type,
                            id: info.id
                        };

                        this.footerFormData = {
                            description: info.description || '',
                            created_by: info.created_by || '--',
                            created_time: info.created_time || '--',
                            confirm_by: info.confirm_by || '--',
                            confirm_time: info.confirm_time || '--',
                            cancel_by: info.cancel_by || '--',
                            cancel_time: info.cancel_time || '--',
                            cancel_reason: info.cancel_reason || '--',
                            payment_by: info.payment_by || '--',
                            payment_date: info.payment_date || '--',
                            payment_time: info.payment_time || '--',
                            receive_by: info.receive_by || '--',
                            receive_time: info.receive_time || '--'
                        };

                        // 付款单
                        if (this.isInbound) {
                            this.headerFormData.invoice_date = info.payment_date
                                ? moment(info.payment_date)
                                : undefined;
                            this.headerFormData.invoice_time = info.payment_time;
                            this.headerFormData.invoice_no = info.payment_sn || '--';
                            this.headerFormData.invoice_price = info.payment_price;
                            this.headerFormData.invoice_status = info.payment_status;
                            this.headerFormData.invoice_type = info.payment_method;
                            this.captionData.label = '系统付款单号';
                            this.captionData.value = info.payment_sn || '--';
                        } else {
                            // 收款单
                            this.headerFormData.invoice_date = info.receive_date
                                ? moment(info.receive_date)
                                : undefined;
                            this.headerFormData.invoice_time = info.receive_time;
                            this.headerFormData.invoice_no = info.receive_sn || '--';
                            this.headerFormData.invoice_price = info.receive_price;
                            this.headerFormData.invoice_status = info.receive_status;
                            this.headerFormData.invoice_type = info.receive_method;
                            this.captionData.label = '系统收款单号';
                            this.captionData.value = info.receive_sn || '--';
                        }

                        this.detailDatas = info.details || [];
                        this.detail = info.details || [];
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        saveInvalidReasonDataFn(invalidReasonData) {
            this.invalidReasonIsOpen = false;
            if (invalidReasonData) {
                this.invalidReasonData = invalidReasonData;
                this.financeInvoiceCancelAPIFn();
            } else {
                this.invalidReasonData = '';
            }
        },
        async detailFn(action) {
            let status = ''; // 操作状态的存储容器

            switch (action) {
                case 'CLOSE': // 关闭 | 取消
                    this.closeDetailModalFn();
                    break;
                case 'SAVE': // 保存
                    if (this.checkFormFn()) return;

                    this.detailMd.saveLoading = true;

                    if (this.btnName === 'add') {
                        status = this.isInbound ? this.paymentInvoiceStateVal[1] : this.receiveInvoiceStateVal[1];
                        this.invoiceAddAPIFn(status);
                    } else {
                        this.invoiceEditAPIFn();
                    }
                    break;
                case 'PAY': // 付款 | 收款
                    if (this.checkFormFn()) return;

                    this.detailMd.paymentAttachLoading = true;

                    status = this.isInbound ? this.paymentInvoiceStateVal[2] : this.receiveInvoiceStateVal[2];
                    if (this.btnName === 'add') {
                        this.invoiceAddAPIFn(status);
                    } else {
                        this.invoiceEditAPIFn(status);
                    }
                    break;
                case 'RECORDED': // 入账
                    this.detailMd.recordedLoading = true;
                    await this.recordReceiveOrPayment([this.rowData.id], this.isInbound);
                    this.detailMd.recordedLoading = false;
                    this.closeDetailModalFn();
                    break;
                case 'CANCEL': // 作废
                    // 填写作废原因
                    this.invalidReasonIsOpen = true;
                    break;
            }
        }
    },
    watch: {
        isOpen: {
            handler(newVal) {
                if (newVal) {
                    this.$Utils.globalDragCenterFn('invoice-detail-modal');

                    // 双击查看详情
                    if (this.btnName === 'edit' && this.rowData && Object.keys(this.rowData).length) {
                        this.invoiceDetailAPIFn();
                    }
                }
            },
            immediate: true
        },
        detailDatas: {
            handler(newVal) {
                if (newVal) {
                    let paymentPriceZeroTotal = 0,
                        paymentPriceOneTypeTotal = 0;
                    // 1. 付款单的付款金额 = type为0的明细的核销金额累加和 - type为1的明细的核销金额累加和
                    // 付款单的付款金额 = 明细中的发票核销金额总和 - 预付款核销金额总和
                    // 2. 收款单的收款金额 = 明细的核销金额累加和
                    for (let i = 0; i < newVal.length; i++) {
                        let item = newVal[i],
                            writtenOffPrice = parseFloat(item.written_off_price) || 0;

                        // 预付款发票
                        if (item['voucher_type_c'] === grnAndOdoInvoiceOrderType.ADVANCE_PAYMENT.code) {
                            paymentPriceOneTypeTotal += writtenOffPrice;
                            continue;
                        }
                        // 入库发票、出库发票
                        paymentPriceZeroTotal += writtenOffPrice;
                    }

                    if (this.isInbound) {
                        // 付款单
                        paymentPriceZeroTotal = paymentPriceZeroTotal - paymentPriceOneTypeTotal;
                    }

                    this.headerFormData.invoice_price = this.$XEUtils.toFixed(paymentPriceZeroTotal, 2);
                }
            },
            deep: true
        },
        headerFormDataNew: {
            handler(newVal, oldVal) {
                /// 相关机构被改变后，清空明细数据
                if (
                    this.isOpen &&
                    oldVal.organization_inner_sn &&
                    oldVal.organization_inner_sn !== newVal.organization_inner_sn &&
                    oldVal.organization_name !== newVal.organization_name
                ) {
                    this.detailDatas = [];
                }
            },
            deep: true
        }
    },
    computed: {
        modalTitle() {
            return `${
                this.isInbound
                    ? pagePaymentAttachmentType['PAYMENTORDER'].label
                    : pagePaymentAttachmentType['RECEIVEORDER'].label
            }`;
        },
        paymentReceiveLabel() {
            return this.isInbound ? '付款' : '收款';
        },
        paymentInvoiceStateVal() {
            return Object.keys(paymentInvoiceState);
        },
        receiveInvoiceStateVal() {
            return Object.keys(receiveInvoiceState);
        },
        detailColumns() {
            return [
                {
                    title: this.isInbound ? '发票编号/预付款编号' : '发票编号',
                    dataIndex: 'sn',
                    key: 'sn',
                    width: '150px',
                    align: 'center'
                },
                {
                    title: '发票号',
                    dataIndex: 'invoice_no',
                    key: 'invoice_no',
                    width: '120px',
                    align: 'center'
                },
                {
                    title: '发票金额',
                    dataIndex: 'invoice_price',
                    key: 'invoice_price',
                    width: '100px',
                    align: 'center'
                }
                // {
                //     title: '核销金额',
                //     dataIndex: 'written_off_price',
                //     key: 'written_off_price',
                //     width: '80px',
                //     align: 'center'
                // }
            ];
        },
        // 是否显示保存按钮，付款或收款按钮 [可以更改页面的表单]
        isShowSaveAndPayBtn() {
            return (
                this.btnName === 'add' ||
                this.headerFormData.invoice_status === this.paymentInvoiceStateVal[1] ||
                this.headerFormData.invoice_status === this.receiveInvoiceStateVal[1]
            );
        },
        // 是否显示入账按钮，作废按钮 [不能更改页面的表单]
        isShowRecordedAndCancelBtn() {
            if (this.headerFormData.isCancel) {
                return false;
            }
            return (
                this.headerFormData.invoice_status === this.paymentInvoiceStateVal[2] ||
                this.headerFormData.invoice_status === this.receiveInvoiceStateVal[2]
            );
        },
        // 是否显示关闭按钮 [不能更改页面的表单]
        isShowCloseBtn() {
            return (
                this.isShowCancel ||
                this.headerFormData.invoice_status === this.paymentInvoiceStateVal[3] ||
                this.headerFormData.invoice_status === this.receiveInvoiceStateVal[3]
            );
        },
        // 是否只读，不能修改表单数据
        isReadonly() {
            if (this.headerFormData.isCancel) {
                return true;
            }
            return this.btnName !== 'add' && (this.isShowRecordedAndCancelBtn || this.isShowCloseBtn);
        },
        closeButtonText() {
            const closeTexts = ['关闭', '取消'];
            if (this.isShowCancel) {
                return closeTexts[0];
            }
            if (
                this.rowData &&
                (this.rowData.payment_status === this.paymentInvoiceStateVal[3] ||
                    this.rowData.receive_status === this.receiveInvoiceStateVal[3])
            ) {
                return closeTexts[0];
            }
            return closeTexts[1];
        },
        headerFormDataNew() {
            return JSON.parse(JSON.stringify(this.headerFormData));
        }
    }
};
</script>

<style lang="less">
.invoice-detail-modal {
    .form-caption {
        position: relative;

        h3 {
            margin-bottom: 14px;
            font-size: 24px;
            color: #444;
            text-align: center;
        }

        .form-row {
            position: absolute;
            top: 0;
            right: 0;
        }
    }

    .form-row {
        &:nth-of-type(n + 2) {
            padding-top: 14px;
        }
    }

    .form-col {
        display: flex;

        .form-label {
            position: relative;
            width: 90px;

            .asterisk {
                top: 3px;
            }

            .form-text {
                padding-left: 10px;
            }
        }
    }

    .form-input {
        display: flex;
        flex: 1;
    }

    .form-table {
        padding: 20px 0;
    }
}
</style>
