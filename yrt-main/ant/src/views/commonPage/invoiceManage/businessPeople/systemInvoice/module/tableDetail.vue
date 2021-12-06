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
                <caption-text
                    :captionData="{
                        ...captionData,
                        caption: modalTitle
                    }"
                ></caption-text>

                <!-- 发票的上半部分 表单区域 -->
                <invoice-header
                    :isReadonly="isReadonly"
                    :formData.sync="headerFormData"
                    :requiredItemMap="requiredHeaderItemMap"
                ></invoice-header>

                <!-- 发票的中间部分 表格区域 -->
                <invoice-table
                    ref="invoiceTable"
                    :isReadonly="isReadonly"
                    :formData="{
                        ...headerFormData,
                        ...footerFormData
                    }"
                    :modalTitle="isInbound ? '入库' : '出库'"
                    :orderTypeData="orderTypeData"
                    :columns="detailColumns"
                    :datas="detailDatas"
                    :pagation="detailPagation"
                    pageNameType="add-platform-product"
                    @datasAdd="addTableDataFn"
                    @delTableData="delTableDataFn"
                ></invoice-table>

                <!-- 发票的下半部分 表单摘要区域 -->
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
import Big from 'big.js';
import captionText from '@/views/commonPage/invoiceManage/components/captionText';
import invoiceHeader from '@/views/commonPage/invoiceManage/components/invoiceHeader';
import invoiceFooter from '@/views/commonPage/invoiceManage/components/invoiceFooter';
import invoiceTable from '@/views/commonPage/invoiceManage/components/invoiceTable';
import {
    pageType,
    grnAndOdoInvoiceOrderType,
    format,
    rateTypeMap,
    invoiceTypeMap
} from '@/views/commonPage/invoiceManage/config/constants';

import {
    invoiceGrnAddAPI,
    invoiceOdoAddAPI,
    invoiceGrnEditAPI,
    invoiceOdoEditAPI,
    invoiceGrnAndOdoInfoAPI
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
        captionText,
        invoiceHeader,
        invoiceFooter,
        invoiceTable
    },
    data() {
        return {
            detailMd: {
                title: `${this.$route.meta.title}`,
                draftLoading: false,
                pendingLoading: false
            },
            detailColumns: [
                {
                    title: '厂商名称',
                    dataIndex: 'product_vendor_name',
                    key: 'product_vendor_name',
                    width: '150px',
                    align: 'center'
                },
                {
                    title: '产品名称',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    width: '120px',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    width: '100px',
                    align: 'center'
                },
                {
                    title: '单位',
                    dataIndex: 'unit',
                    key: 'unit',
                    width: '80px',
                    align: 'center'
                },
                {
                    title: '单价',
                    dataIndex: 'unit_price',
                    key: 'unit_price',
                    width: '100px',
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: 'quantity',
                    width: '80px',
                    align: 'center'
                },
                {
                    title: '税率',
                    dataIndex: 'rate',
                    key: 'rate',
                    width: '60px',
                    align: 'center'
                },
                {
                    title: '开票数量',
                    dataIndex: 'invoiced_quantity',
                    key: 'invoiced_quantity',
                    width: '90px',
                    align: 'center'
                },
                {
                    title: '备注',
                    dataIndex: 'remarks',
                    key: 'remarks',
                    width: '120px',
                    align: 'center'
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    key: 'action',
                    width: '100px',
                    align: 'center'
                }
            ],
            detailDatas: [],
            detailDatasIsPrimary: false, // 标识数据是否为来自服务器的初始数据
            detailPagation: {
                loading: false,
                total: 0,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            captionData: {
                'caption': '',
                'label': '系统发票号',
                'value': ''
            },
            headerFormData: {
                bills_type: '',
                department_inner_sn: '',
                department_name: '',
                handler_inner_sn: '',
                handler_name: '',
                invoice_date: null,
                invoice_no: '',
                invoice_price: undefined,
                invoice_status: '',
                invoice_type: invoiceTypeMap.GENERAL_INVOICE.value,
                organization_inner_sn: '',
                organization_name: '',
                rate: rateTypeMap[0].value,
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
                isShowPayment: false // 是否显示付款字段， 默认不显示
            },
            isReadonly: false
        };
    },
    methods: {
        closeDetailModalFn() {
            this.$emit('closeDetailModal', false);
            this.detailDatas = [];
            this.captionData.caption = '';
            this.captionData.value = '';
            this.headerFormData = {
                bills_type: '',
                department_inner_sn: '',
                department_name: '',
                handler_inner_sn: '',
                handler_name: '',
                invoice_date: null,
                invoice_no: '',
                invoice_price: undefined,
                invoice_status: '',
                invoice_type: invoiceTypeMap.GENERAL_INVOICE.value,
                organization_inner_sn: '',
                organization_name: '',
                rate: rateTypeMap[0].value,
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
        addTableDataFn(newProducts = []) {
            newProducts.forEach((product) => {
                // 新增标识
                product.isNew = true;
                this.detailDatas.push(product);
            });
        },
        delTableDataFn(rowIndex) {
            this.detailDatas.splice(rowIndex, 1);
        },
        checkFormFn() {
            // 校验表单
            if (this.requiredHeaderItemMap.organization && !this.headerFormData.organization_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: `请选择相关机构`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (this.requiredHeaderItemMap.invoiceType && !this.headerFormData.invoice_type) {
                this.$warning({
                    title: '操作提示',
                    content: `请选择发票类型`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (
                this.requiredHeaderItemMap.invoicePrice &&
                !this.headerFormData.invoice_price &&
                this.headerFormData.invoice_price !== 0
            ) {
                this.$warning({
                    title: '操作提示',
                    content: `请填写开票金额`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (this.requiredHeaderItemMap.invoiceNo && !this.headerFormData.invoice_no) {
                this.$warning({
                    title: '操作提示',
                    content: `请填写发票号`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (this.requiredHeaderItemMap.invoiceDate && !this.headerFormData.invoice_date) {
                this.$warning({
                    title: '操作提示',
                    content: `请选择开票日期`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            if (this.requiredHeaderItemMap.rate && !this.headerFormData.rate && this.headerFormData.rate !== 0) {
                this.$warning({
                    title: '操作提示',
                    content: `请选择发票税率`,
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
                    content: `请双击添加产品`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }

            let isPass = false;
            for (let i = 0; i < this.detailDatas.length; i++) {
                let item = this.detailDatas[i];
                if (typeof item.unit_price === 'undefined' || !item.unit_price.toString()) {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${i + 1} 请填写单价`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    isPass = true;
                    break;
                }
                if (typeof item.quantity === 'undefined' || !item.quantity.toString()) {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${i + 1} 请填写数量`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    isPass = true;
                    break;
                }
                if (!item.rate.toString()) {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${i + 1} 请选择税率`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    isPass = true;
                    break;
                }
                if (typeof item.invoiced_quantity === 'undefined' || !item.invoiced_quantity.toString()) {
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${i + 1} 请填写开票数量`,
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
                item.unit_inner_sn = Array.isArray(item.unit_inner_sn) ? item.unit_inner_sn[0] : item.unit_inner_sn;
                item.rate = new Big(item.rate).div(100).toNumber();

                delete item.id;
                delete item.activeClass;
                delete item.common_use_unit;
                delete item.common_use_unit_inner_sn;
                delete item.isLoadingUnit;
                delete item.key;
                delete item.keyFormat;
                delete item.mdrfList;
                delete item.mdrf_sn;
                delete item.min_unit;
                delete item.min_unit_inner_sn;
                delete item.money;
                delete item.name;
                delete item.overdue_date;
                delete item.produce_date;
                delete item.register_sn;
                delete item.row_add_flag;
                delete item.status;
                delete item.temp_add_flag;
                delete item.type;
                delete item.unitList;
                delete item.unit_name;
                delete item.vendor_inner_sn;
                delete item.vendor_name;
                delete item.warehouse_inner_sn;
                delete item.warehouse_name;
                delete item._XID;
                delete item.isNew;

                return item;
            });
        },
        async invoiceAddAPIFn(status, isDRAFT) {
            if (isDRAFT) {
                this.detailMd.draftLoading = true;
            } else {
                this.detailMd.pendingLoading = true;
            }
            const service = this.isInbound ? invoiceGrnAddAPI : invoiceOdoAddAPI;
            const dataPost = {
                ...this.headerFormData,
                invoice_status: status,
                rate: new Big(this.headerFormData.rate).div(100).toNumber(),
                description: this.footerFormData.description,
                detail: this.formatDetailFn()
            };

            if (!this.headerFormData.invoice_type) {
                delete dataPost.invoice_type;
            }
            if (this.headerFormData.invoice_date) {
                dataPost.invoice_date = moment(this.headerFormData.invoice_date).format(format);
            } else {
                delete dataPost.invoice_date;
            }

            delete dataPost.id;

            await service(dataPost)
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
                    if (isDRAFT) {
                        this.detailMd.draftLoading = false;
                    } else {
                        this.detailMd.pendingLoading = false;
                    }
                });
        },
        async invoiceEditAPIFn(status, isDRAFT) {
            if (isDRAFT) {
                this.detailMd.draftLoading = true;
            } else {
                this.detailMd.pendingLoading = true;
            }
            const service = this.isInbound ? invoiceGrnEditAPI : invoiceOdoEditAPI;
            const dataPost = {
                ...this.headerFormData,
                invoice_status: status,
                rate: new Big(this.headerFormData.rate).div(100).toNumber(),
                description: this.footerFormData.description,
                detail: this.formatDetailFn()
            };
            if (!this.headerFormData.invoice_type) {
                delete dataPost.invoice_type;
            }
            if (this.headerFormData.invoice_date) {
                dataPost.invoice_date = moment(this.headerFormData.invoice_date).format(format);
            } else {
                delete dataPost.invoice_date;
            }
            await service(dataPost)
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
                    if (isDRAFT) {
                        this.detailMd.draftLoading = false;
                    } else {
                        this.detailMd.pendingLoading = false;
                    }
                });
        },
        async invoiceDetailAPIFn() {
            await invoiceGrnAndOdoInfoAPI(this.rowData.id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;

                        // 只读
                        this.isReadonly = info.invoice_status !== 'DRAFT' && info.invoice_status.length > 0;

                        this.captionData.value = info.system_invoice_sn || '--';

                        this.headerFormData = {
                            bills_type: info.bills_type,
                            department_inner_sn: info.department_inner_sn || '',
                            department_name: info.department_name || '',
                            handler_inner_sn: info.handler_inner_sn || '',
                            handler_name: info.handler_name || '',
                            invoice_date: info.invoice_date ? moment(info.invoice_date) : null,
                            invoice_no: info.invoice_no || '',
                            invoice_price: info.invoice_price,
                            invoice_status: info.invoice_status,
                            invoice_type: info.invoice_type,
                            organization_inner_sn: info.organization_inner_sn,
                            organization_name: info.organization_name,
                            rate: new Big(info.rate).times(100).toString(),
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

                        info.details = info.details.map((item) => ({
                            ...item,
                            rate: new Big(item.rate).times(100).toString()
                        }));

                        this.detailDatas = info.details || [];
                        this.detailDatasIsPrimary = true;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {});
        },
        detailFn(status) {
            let isDRAFT = false; // 是否是存草稿按钮

            switch (status) {
                case 'CLOSE':
                    this.closeDetailModalFn();
                    break;
                case 'DRAFT':
                    if (this.checkFormFn()) return;

                    isDRAFT = true;
                    if (this.btnName === 'add') {
                        this.invoiceAddAPIFn(status, isDRAFT);
                    } else {
                        this.invoiceEditAPIFn(status, isDRAFT);
                    }
                    break;
                case 'PENDING':
                    if (this.checkFormFn()) return;

                    isDRAFT = false;
                    if (this.btnName === 'add') {
                        this.invoiceAddAPIFn(status, isDRAFT);
                    } else {
                        this.invoiceEditAPIFn(status, isDRAFT);
                    }
                    break;
            }
        }
    },
    watch: {
        isOpen: {
            handler(newVal) {
                if (newVal) {
                    this.$Utils.globalDragCenterFn('invoice-detail-modal');

                    if (this.btnName === 'add') {
                        this.isReadonly = false;
                    }

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
                    // 防止从服务器获取到的invoice_price值被自动计算的值覆盖
                    if (this.detailDatasIsPrimary) {
                        this.detailDatasIsPrimary = false;
                        return;
                    }
                    // 出入库发票明细的每行单价 * 开票数量 累加后 = 顶部的开票金额，但是因开票金额可修改，故暂时如果用户修改了以用户修改的开票金额为准
                    let invoicePriceTotal = newVal.reduce((prev, next) => {
                        if (!next.unit_price) {
                            next.unit_price = 0;
                        }
                        if (!next.invoiced_quantity) {
                            next.invoiced_quantity = 0;
                        }
                        return prev + next.unit_price * next.invoiced_quantity;
                    }, 0);

                    this.headerFormData.invoice_price = this.$XEUtils.toFixed(invoicePriceTotal, 2);
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
            return `${this.isInbound ? pageType['INBOUND'].label : pageType['OUTBOUND'].label}发票`;
        },
        orderTypeData() {
            return this.isInbound ? grnAndOdoInvoiceOrderType['GRN_INVOICE'] : grnAndOdoInvoiceOrderType['ODO_INVOICE'];
        },
        headerFormDataNew() {
            return JSON.parse(JSON.stringify(this.headerFormData));
        },
        requiredHeaderItemMap() {
            // 出库发票，发票类型、发票号、开票日期为非必填
            return {
                organization: true,
                handler: false,
                department: false,
                invoiceType: !!this.isInbound,
                invoicePrice: true,
                invoiceNo: !!this.isInbound,
                invoiceDate: !!this.isInbound,
                rate: true
            };
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
