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
                <a-button @click="detailFn('CLOSE')">{{ isReadonly ? '关闭' : '取消' }}</a-button>
                <a-button v-if="!isReadonly" @click="detailFn('DRAFT')" :loading="detailMd.draftLoading" type="primary"
                    >存草稿</a-button
                >
                <a-button
                    v-if="!isReadonly"
                    @click="detailFn('PENDING')"
                    :loading="detailMd.pendingLoading"
                    type="primary"
                    >送审</a-button
                >
            </template>
            <div class="form-container">
                <!-- 标题区域 -->
                <caption-text :captionData="captionData"></caption-text>

                <!-- 预付款的上半部分 表单区域 -->
                <invoice-header :isReadonly="isReadonly" :formData.sync="headerFormData"></invoice-header>

                <!-- 预付款的中间部分 付款凭证图片区域 -->
                <invoice-photo :invoicePaymentImgDatas.sync="invoicePaymentImgDatas"></invoice-photo>

                <!-- 预付款的下半部分 表单摘要区域 -->
                <invoice-footer
                    :isReadonly="isReadonly"
                    :formData.sync="footerFormData"
                    :isShowCancel="isShowCancel"
                ></invoice-footer>
            </div>
        </a-modal>
    </div>
</template>

<script>
import captionText from '@/views/commonPage/invoiceManage/components/captionText';
import invoiceHeader from './invoiceHeader';
import invoiceFooter from '@/views/commonPage/invoiceManage/components/invoiceFooter';
import invoicePhoto from '@/views/commonPage/invoiceManage/components//invoicePhoto';
import { format } from '@/views/commonPage/invoiceManage/config/constants';

import { invoiceAdvanceAddAPI, invoiceAdvanceEditAPI, invoiceAdvanceInfoAPI } from '@/service/pageAjax';

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
        rowData: {
            type: Object // 当前表格双击查看当前行的列表数据
        }
    },
    components: {
        captionText,
        invoiceHeader,
        invoiceFooter,
        invoicePhoto
    },
    data() {
        return {
            detailMd: {
                title: `${this.$route.meta.title}`,
                draftLoading: false,
                pendingLoading: false
            },
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
                payment_date: '',
                payment_sn: '',
                payment_price: '',
                payment_status: '',
                payment_method: '',
                organization_inner_sn: '',
                organization_name: '',
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
            invoicePaymentImgDatas: [],
            isShowCancel: false
        };
    },
    methods: {
        closeDetailModalFn() {
            this.$emit('closeDetailModal', false);
            this.detail = [];
            this.detailDatas = [];
            this.captionData.value = '';
            this.headerFormData = {
                bills_type: '',
                department_inner_sn: '',
                department_name: '',
                handler_inner_sn: '',
                handler_name: '',
                payment_date: '',
                payment_sn: '',
                payment_price: '',
                payment_status: '',
                payment_method: '',
                organization_inner_sn: '',
                organization_name: '',
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
            this.invoicePaymentImgDatas = [];
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

            if (!this.headerFormData.payment_price) {
                this.$warning({
                    title: '操作提示',
                    content: `请填写付款金额`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (!this.headerFormData.payment_method) {
                this.$warning({
                    title: '操作提示',
                    content: `请选择付款方式`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (!this.headerFormData.payment_date) {
                this.$warning({
                    title: '操作提示',
                    content: `请选择付款日期`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            return false;
        },
        async invoiceAdvanceAddFn(status) {
            this.detailMd.draftLoading = true;
            delete this.headerFormData.id;

            await invoiceAdvanceAddAPI({
                ...this.headerFormData,
                payment_status: status,
                payment_date: moment(this.headerFormData.payment_date).format(format),
                description: this.footerFormData.description
            })
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
                    this.detailMd.draftLoading = false;
                });
        },
        async invoiceAdvanceEditAPIFn(status) {
            this.detailMd.pendingLoading = true;
            await invoiceAdvanceEditAPI({
                ...this.headerFormData,
                payment_status: status,
                payment_date: moment(this.headerFormData.payment_date).format(format),
                description: this.footerFormData.description
            })
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
                    this.detailMd.pendingLoading = false;
                });
        },
        async invoiceAdvanceInfoAPIFn() {
            await invoiceAdvanceInfoAPI(this.rowData.id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;

                        this.captionData.value = info.payment_sn || '--';

                        // 底部是否展示作废相关字段
                        this.isShowCancel = info.is_cancel.toString() === '1';

                        this.headerFormData = {
                            bills_type: info.bills_type,
                            department_inner_sn: info.department_inner_sn || '',
                            department_name: info.department_name || '',
                            handler_inner_sn: info.handler_inner_sn || '',
                            handler_name: info.handler_name || '',
                            payment_date: moment(info.payment_date),
                            payment_sn: info.payment_sn || '',
                            payment_price: info.payment_price,
                            payment_status: info.payment_status,
                            payment_method: info.payment_method,
                            organization_inner_sn: info.organization_inner_sn,
                            organization_name: info.organization_name,
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
        detailFn(status) {
            switch (status) {
                case 'CLOSE':
                    this.closeDetailModalFn();
                    break;
                case 'DRAFT':
                    if (this.checkFormFn()) return;
                    if (this.btnName === 'add') {
                        this.invoiceAdvanceAddFn(status);
                    } else {
                        this.invoiceAdvanceEditAPIFn(status);
                    }
                    break;
                case 'PENDING':
                    if (this.checkFormFn()) return;
                    if (this.btnName === 'add') {
                        this.invoiceAdvanceAddFn(status);
                    } else {
                        this.invoiceAdvanceEditAPIFn(status);
                    }
                    break;
            }
        }
    },
    watch: {
        isOpen: {
            handler(newVal) {
                if (newVal) {
                    this.$Utils.globalDragCenterFn('advance-payment-detail-modal');

                    // 双击查看详情
                    if (this.btnName === 'edit' && this.rowData && Object.keys(this.rowData).length) {
                        this.invoiceAdvanceInfoAPIFn();
                    }
                }
            },
            immediate: true
        }
    },
    computed: {
        isReadonly() {
            return this.btnName !== 'add' && this.headerFormData.payment_status !== 'DRAFT';
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

    .form-input {
        display: flex;
        flex: 1;
    }
}
</style>
