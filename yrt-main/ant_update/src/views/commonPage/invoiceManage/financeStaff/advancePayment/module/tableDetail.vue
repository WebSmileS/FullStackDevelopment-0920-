<template>
    <div>
        <a-modal
            :title="detailMd.title"
            v-model="isOpen"
            :maskClosable="false"
            :width="900"
            :centered="true"
            @cancel="closeDetailModalFn"
            class="modal-form-input-scoped global-drag advance-payment-detail-modal"
        >
            <div v-globalDrag="{ el: 'advance-payment-detail-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ detailMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="detailFn('CLOSE')">关闭</a-button>
                <a-button
                    v-if="tabsActiveKey === 'PENDING'"
                    @click="detailFn('AUDIT')"
                    :loading="detailMd.auditLoading"
                    type="primary"
                    >审核</a-button
                >
                <a-button
                    v-if="cancelButtonVisible"
                    @click="detailFn('CANCEL')"
                    :loading="detailMd.cancelLoading"
                    type="danger"
                    >作废</a-button
                >
            </template>
            <div class="form-container">
                <!-- 标题区域 -->
                <caption-text :captionData="captionData"></caption-text>

                <!-- 预付款的上半部分 表单区域 -->
                <invoice-header
                    :isReadonly="isReadonly"
                    :isFinanceInvoiceOdoReadonly="isFinanceInvoiceOdoReadonly"
                    :formData.sync="headerFormData"
                ></invoice-header>

                <!-- 预付款的中间部分 付款凭证图片区域 -->
                <invoice-photo
                    :isUpload="!isReadonly"
                    :invoicePaymentImgDatas="invoicePaymentImgDatas"
                    @getInvoicePaymentImgData="getInvoicePaymentImgDataFn"
                ></invoice-photo>

                <!-- 预付款的下半部分 表单摘要区域 -->
                <invoice-footer
                    :isReadonly="isReadonly"
                    :formData.sync="footerFormData"
                    :isShowCancel="isShowCancel"
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
import invoicePhoto from '@/views/commonPage/invoiceManage/components//invoicePhoto';
import { format } from '@/views/commonPage/invoiceManage/config/constants';
import { tabPane } from '../mixins/tabPane.js';

import { financeAdvanceInfoAPI, financeAdvanceApprovalAPI, financeAdvanceCancelAPI } from '@/service/pageAjax';

import moment from 'moment';

export default {
    name: 'tableDetail', // 入库|出库预付款的详情 = 财务人员使用
    props: {
        btnName: {
            // 按钮行为
            type: String,
            required: true
        },
        isOpen: {
            // 模态框窗口是否打开标识， true、false
            type: Boolean,
            default: true
        },
        tabsActiveKey: {
            // 模态框窗口tab选项卡当前的key标识，必传
            type: String,
            required: true
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
        invoicePhoto
    },
    data() {
        return {
            detailMd: {
                title: `${this.$route.meta.title}`,
                auditLoading: false,
                cancelLoading: false
            },
            invalidReasonIsOpen: false,
            captionData: {
                'caption': '预付款',
                'label': '付款编号',
                'value': ''
            },
            headerFormData: {
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
            },
            footerFormData: {
                description: '',
                created_by: '--',
                created_time: '--',
                confirm_by: '--',
                confirm_time: '--',
                cancel_by: '--',
                cancel_time: '--',
                cancel_reason: '--'
            },
            isFinanceInvoiceOdoReadonly: true, // 是否是财务人员使用的出库发票，如果是将相关机构、经手人和部门和产品明细设置为 只读
            isfreshParentTableList: false, // 是否刷新父级表格数据
            invalidReasonData: '', // 作废原因
            invoicePaymentImgDatas: []
        };
    },
    methods: {
        getInvoicePaymentImgDataFn(imgDatas) {
            this.invoicePaymentImgDatas = imgDatas;
        },

        async financeAdvanceApprovalAPIFn() {
            // 校验表单
            if (this.checkFormFn()) return;

            this.detailMd.auditLoading = true;
            financeAdvanceApprovalAPI({
                attas: this.invoicePaymentImgDatas.length
                    ? this.invoicePaymentImgDatas.map((item) => ({
                          file_name: item.file_name,
                          url: item.url
                      }))
                    : [],
                decription: this.footerFormData.description || '',
                id: this.headerFormData.id || this.rowData.id,
                payment_date: moment(this.headerFormData.invoice_date).format(format),
                payment_method: this.headerFormData.invoice_type,
                payment_price: this.headerFormData.invoice_price
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.isfreshParentTableList = true;
                        this.closeDetailModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.detailMd.auditLoading = false;
                });
        },
        async financeAdvanceCancelAPIFn() {
            this.detailMd.cancelLoading = true;
            await financeAdvanceCancelAPI(this.rowData.id, this.invalidReasonData)
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
        closeDetailModalFn() {
            this.$emit('closeDetailModal', false, this.isfreshParentTableList);

            this.invalidReasonData = '';
            this.detail = [];
            this.detailDatas = [];
            this.headerFormData = {
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
                isShowPayment: false // 是否显示付款字段， 默认不显示
            };
        },
        getTableDataFn(tableData) {
            this.detail = tableData;
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

            if (!this.headerFormData.invoice_price) {
                this.$warning({
                    title: '操作提示',
                    content: `请填写付款金额`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (!this.headerFormData.invoice_type) {
                this.$warning({
                    title: '操作提示',
                    content: `请选择付款方式`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (!this.headerFormData.invoice_date) {
                this.$warning({
                    title: '操作提示',
                    content: `请选择付款日期`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (this.invoicePaymentImgDatas.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: `请上传付款凭证`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            return false;
        },
        async financeAdvanceInfoAPIFn() {
            await financeAdvanceInfoAPI(this.rowData.id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.captionData.value = info.payment_sn || '--';

                        this.headerFormData = {
                            bills_type: info.bills_type,
                            department_inner_sn: info.department_inner_sn || '',
                            department_name: info.department_name || '',
                            handler_inner_sn: info.handler_inner_sn || '',
                            handler_name: info.handler_name || '',
                            invoice_date: moment(info.payment_date),
                            invoice_no: info.payment_sn || '',
                            invoice_price: info.payment_price,
                            invoice_status: info.payment_status,
                            invoice_type: info.payment_method,
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
                            isShowPayment: false // 是否显示付款字段， 默认不显示
                        };

                        this.invoicePaymentImgDatas = info.attas || [];
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
                this.financeAdvanceCancelAPIFn();
            } else {
                this.invalidReasonData = '';
            }
        },
        detailFn(status) {
            switch (status) {
                case 'CLOSE':
                    this.closeDetailModalFn();
                    break;
                case 'AUDIT':
                    this.financeAdvanceApprovalAPIFn();
                    break;
                case 'CANCEL':
                    // // 校验表单
                    // if (this.checkFormFn()) return
                    // 填写作废原因
                    this.invalidReasonIsOpen = true;
                    break;
            }
        }
    },
    mixins: [tabPane],
    watch: {
        isOpen: {
            handler(newVal) {
                if (newVal) {
                    this.$Utils.globalDragCenterFn('advance-payment-detail-modal');

                    // 双击查看详情
                    if (
                        (this.btnName === 'audit' || this.btnName === 'cancel' || this.btnName === 'edit') &&
                        this.rowData &&
                        Object.keys(this.rowData).length
                    ) {
                        this.financeAdvanceInfoAPIFn();
                    }
                }
            },
            immediate: true
        }
    },
    computed: {
        // 是否只读 [不是新增按钮 当前状态不是PENDING]
        isReadonly() {
            return this.btnName !== 'add' && this.tabsActiveKey !== this.tabPane(0, 'value');
        },
        cancelButtonVisible() {
            if (this.rowData && this.rowData.payment_status === 'PARTIAL_WRITE_OFF') {
                return false;
            }
            if (this.tabsActiveKey === 'PENDING' || this.tabsActiveKey === 'NOT_WRITTEN_OFF') {
                return true;
            }
            return false;
        }
    }
};
</script>

<style lang="less">
.advance-payment-detail-modal {
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

    .form-input,
    .form-input div {
        display: flex;
        flex: 1;
    }
}
</style>
