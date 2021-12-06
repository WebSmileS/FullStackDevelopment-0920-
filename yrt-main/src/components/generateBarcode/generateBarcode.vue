<template>
    <div class="barcode">
        <div class="send-again-barcode-group">
            <a-radio-group
                :disabled="barcodeChildObj.hasBarcode"
                name="radioGroup"
                v-model="barcodeType"
                @change="barcodeTypeChangeFn"
            >
                <a-radio :value="0">单一条码</a-radio>
                <a-radio :value="1">多条码</a-radio>
            </a-radio-group>
            <a-input-number
                placeholder=""
                :disabled="barcodeTypeDisabled"
                allowClear
                v-model="barcodeNumber"
                size="small"
                class="input"
            />
            <!-- 已经生成过的 disabled -->
            <a-button
                v-if="barcodeChildObj.hasBarcode"
                :disabled="barcodeChildObj.hasBarcode"
                class="btn-default"
                key="generate"
                type="primary"
                @click="generateBarcodeTopFn('send-again-barcode')"
            >
                重新生成
            </a-button>
            <a-button
                v-else
                class="btn-default"
                key="generate"
                type="primary"
                :disabled="generateLoading"
                @click="generateBarcodeTopFn('send-again-barcode')"
            >
                {{ barcodeComputedText }}
            </a-button>
        </div>
        <vxe-table
            ref="xBarcodeTable"
            size="small"
            height="382"
            border
            resizable
            auto-resize
            highlight-current-row
            @select-change="selectBarcodeChangeFn"
            align="center"
            :loading="barcodePagination.loading"
            show-overflow
            show-header-overflow
            showHeader
            class="barcode-table"
            :start-index="(barcodePagination.current - 1) * barcodePagination.pageSize"
        >
            <!-- :data="barcodeDatas" -->
            <vxe-table-column type="checkbox" width="60" :resizable="false" fixed="left"></vxe-table-column>
            <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                <template v-slot="{ row, seq }">
                    <div>
                        {{ seq }}
                    </div>
                </template>
            </vxe-table-column>
            <vxe-table-column field="newBarcode" title="条码">
                <template v-slot="{ row, seq }">
                    <div class="barcode-img">
                        <img v-show="row.newBarcode" :id="'jsBarcodeImg' + seq" src="" alt="" />
                        <span v-if="row.newBarcode.length <= 0" class="red">生成条码异常</span>
                    </div>
                </template>
            </vxe-table-column>
        </vxe-table>
        <vxe-pager
            :loading="barcodePagination.loading"
            :current-page="barcodePagination.current"
            :page-size="barcodePagination.pageSize"
            :page-sizes="barcodePagination.pageSizes"
            :total="barcodePagination.total"
            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="changePaginationFn"
        >
        </vxe-pager>
    </div>
</template>

<script>
/* 条形码所用依赖包开始 */
import JsBarcode from 'jsbarcode';
/* 条形码所用依赖包结束 */
import moment from 'moment';

import {
    grnBarcodeInfoAPI,
    grnBarcodeCreateAPI,
    grnBarcodeSaveAPI,
    insBarcodeSaveAPI,
    insBarcodeInfoAPI
} from '@/service/pageAjax';
export default {
    name: 'generateBarcode',
    props: {
        pageNameType: {
            // 页面标识名称
            type: String,
            defaults: ''
        },
        barcodeParentObj: {
            // 父级参数
            type: Object,
            defaults: {}
        },
        barcodeChildObj: {
            // 自己参数
            type: Object,
            defaults: {}
        }
    },
    data() {
        return {
            readBarcodeInput: '',
            readBarcodeList: [],
            barcodeNumber: 10,
            barcodeTypeDisabled: true,
            format: 'YYYY-MM-DD',
            generateLoading: false,
            barcodeType: 0, // 默认选中 '单一条码' - 0   '多条码' - 1
            currentTrBarcodeObj: {},
            barcodeDatas: [],
            barcodePagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            }
        };
    },
    methods: {
        // 入库单 - 【获取产品条码信息】
        async grnBarcodeInfoAPIFn() {
            this.barcodePagination.loading = true;
            const params = {
                billsType: this.barcodeParentObj.billsType,
                org_id: this.barcodeParentObj.org_id || this.$cookie.get('userbelong'),
                grn_inner_sn: this.barcodeParentObj.grn_inner_sn,
                grn_detail_inner_sn: this.barcodeChildObj.detail_inner_sn,
                grn_type: this.barcodeParentObj.grn_type
            };
            await grnBarcodeInfoAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.barcodeType = Array.isArray(info) && info.length > 0 ? info[0].barcode_type : 0;
                        this.barcodeTypeDisabled = true;
                        this.setBarcodeNumMoreFn(info.length);
                        this.renderTableFn(info);
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.barcodePagination.loading = false;
                })
                .catch(() => {
                    this.barcodePagination.loading = false;
                });
        },
        async insBarcodeInfoAPIFn() {
            this.barcodePagination.loading = true;
            const params = {
                // 当前机构ID
                org_id: this.barcodeParentObj.org_id || this.$cookie.get('userbelong'),
                // 验货单ID
                pp_inner_sn: this.barcodeChildObj.pp_inner_sn,
                // 验货单类型 0-DealerDealer 1-DealerVendor
                inspection_type: this.barcodeParentObj.inspection_type,
                // 验货单明细ID
                pp_detail_inner_sn: this.barcodeChildObj.detail_inner_sn,
                // 单据类型
                billsType: this.barcodeParentObj.billsType
            };
            await insBarcodeInfoAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.barcodeType = Array.isArray(info) && info.length > 0 ? info[0].barcode_type : 0;
                        this.barcodeTypeDisabled = true;
                        this.setBarcodeNumMoreFn(info.length);
                        this.renderTableFn(info);
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.barcodePagination.loading = false;
                })
                .catch(() => {
                    this.barcodePagination.loading = false;
                });
        },
        // 【生成条码】
        async grnBarcodeCreateAPIFn(record) {
            this.generateLoading = true;
            this.barcodePagination.loading = true;
            const params = {
                org_id: record.org_id || this.$cookie.get('userbelong'),
                barcode_type: this.barcodeType,
                barcode_number: this.barcodeType === 0 ? 1 : this.barcodeNumber
            };
            await grnBarcodeCreateAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.renderTableFn(list);
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.generateLoading = false;
                    this.barcodePagination.loading = false;
                })
                .catch(() => {
                    this.generateLoading = false;
                    this.barcodePagination.loading = false;
                });
        },
        // 入库单 - 【保存条码】
        async grnBarcodeSaveAPIFn() {
            if (this.checkTableDataFn()) return;
            const params = {
                billsType: this.barcodeParentObj.billsType,
                barcode_type: this.barcodeType,
                grn_type: this.barcodeParentObj.grn_type,
                warehouse_inner_sn: this.barcodeParentObj.warehouse_inner_sn,
                warehouse_name: this.barcodeParentObj.warehouse_name,
                organization_inner_sn: this.barcodeParentObj.organization_inner_sn,
                organization_name: this.barcodeParentObj.organization_name,
                batch_number: this.barcodeChildObj.batch_number,
                grn_detail_inner_sn: this.barcodeChildObj.detail_inner_sn,
                grn_inner_sn: this.barcodeChildObj.grn_inner_sn,
                mdrf_inner_sn: this.barcodeChildObj.mdrf_inner_sn,
                mdrf_sn: this.barcodeChildObj.mdrf_sn,
                org_id: this.barcodeChildObj.org_id,
                overdue_date: moment(this.barcodeChildObj.overdue_date).format(this.format),
                produce_date:
                    this.barcodeChildObj.produce_date === ''
                        ? ''
                        : moment(this.barcodeChildObj.produce_date).format(this.format),
                product_inner_sn: this.barcodeChildObj.product_inner_sn,
                product_name: this.barcodeChildObj.product_name,
                product_number: this.barcodeChildObj.quantity,
                product_type: this.barcodeChildObj.product_type,
                product_vendor_inner_sn: this.barcodeChildObj.vendor_inner_sn,
                product_vendor_name: this.barcodeChildObj.vendor_name,
                specification: this.barcodeChildObj.specification,
                specification_inner_sn: this.barcodeChildObj.specification_inner_sn,
                unit_inner_sn: this.barcodeChildObj.unit_inner_sn[0],
                unit_name: this.barcodeChildObj.unit_name
            };
            params.codes = this.barcodeDatas.map((item) => {
                return {
                    code: item.code
                };
            });

            await grnBarcodeSaveAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.$emit('saveBarcodeEmitFn', true);
                    } else {
                        this.$warning({
                            title: '操作提示',
                            content: res.msg || '保存条码异常，请稍后再试。',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        this.$emit('saveBarcodeEmitFn', false);
                    }
                })
                .catch(() => {
                    this.$emit('saveBarcodeEmitFn', false);
                });
        },
        // 【验货保存条码】
        async insBarcodeSaveAPIFn() {
            if (this.checkTableDataFn()) return;
            const params = {
                barcode_type: this.barcodeType, // 条码类别 0-单一条码 1-多条码
                // grn_type: this.barcodeParentObj.grn_type, // 无
                // warehouse_inner_sn: this.barcodeParentObj.warehouse_inner_sn, // 无
                // warehouse_name: this.barcodeParentObj.warehouse_name, // 无
                organization_inner_sn: this.barcodeParentObj.organization_inner_sn, // 相关机构ID
                organization_name: this.barcodeParentObj.organization_name, // 相关机构名称
                batch_number: this.barcodeChildObj.batch_number, // 产品批号
                // grn_detail_inner_sn: this.barcodeChildObj.detail_inner_sn,
                // grn_inner_sn: this.barcodeChildObj.grn_inner_sn,
                mdrf_inner_sn: this.barcodeChildObj.mdrf_inner_sn, // 登记表内部编号ID
                mdrf_sn: this.barcodeChildObj.mdrf_sn, // 医疗器械注册证号
                org_id: this.barcodeChildObj.org_id, // 当前机构ID
                overdue_date: moment(this.barcodeChildObj.overdue_date).format(this.format), // 失效日期
                produce_date: moment(this.barcodeChildObj.produce_date).format(this.format), // 失效日期
                product_inner_sn: this.barcodeChildObj.product_inner_sn, // 产品ID
                product_name: this.barcodeChildObj.product_name, // 产品名称
                product_number: this.barcodeChildObj.qualified_quantity, // 产品数 这里修复过
                product_type: this.barcodeChildObj.product_type, // 产品类型 0:经销商(自建)产品 1:厂商产品
                product_vendor_inner_sn: this.barcodeChildObj.product_vendor_inner_sn, // 产品厂商ID     ?
                product_vendor_name: this.barcodeChildObj.product_vendor_name, // 产品厂商名称 ?
                specification: this.barcodeChildObj.specification, // 型号规格
                specification_inner_sn: this.barcodeChildObj.specification_inner_sn, // 型号规格ID
                unit_inner_sn: this.barcodeChildObj.unit_inner_sn[0], // 包装单位ID
                unit_name: this.barcodeChildObj.unit, // 单位  ?
                // 增
                inspection_type: this.barcodeParentObj.inspection_type,
                pp_inner_sn: this.barcodeChildObj.pp_inner_sn,
                pp_detail_inner_sn: this.barcodeChildObj.detail_inner_sn,
                billsType: this.barcodeParentObj.billsType // 单据类型
            };
            // 唯一编码
            params.codes = this.barcodeDatas.map((item) => {
                return {
                    code: item.code
                };
            });
            await insBarcodeSaveAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.$emit('saveBarcodeEmitFn', true);
                    } else {
                        this.$warning({
                            title: '操作提示',
                            content: res.msg || '保存条码异常，请稍后再试。',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        this.$emit('saveBarcodeEmitFn', false);
                    }
                })
                .catch(() => {
                    this.$emit('saveBarcodeEmitFn', false);
                });
        },
        // 后端不能提供分页，前端自己分页渲染 01
        renderTableFn(list) {
            this.barcodeDatas = list.map((item) => {
                return {
                    ...item,
                    newBarcode: `${item.orgType}${item.orgId}${item.code}`
                };
            });
            const xBarcodeTable = this.$refs.xBarcodeTable,
                newList = this.barcodeDatas.slice(0, this.barcodePagination.pageSize);
            xBarcodeTable.reloadData(newList).then(() => {
                newList.forEach((item, index) => {
                    this.showJsBarcodeFn(item.newBarcode, ++index);
                });
            });
            this.barcodePagination = {
                ...this.barcodePagination,
                total: this.barcodeDatas.length,
                loading: false
            };
        },
        // 后端不能提供分页，前端自己分页渲染 02
        changePaginationFn({ currentPage, pageSize }) {
            this.barcodePagination.current = currentPage;
            this.barcodePagination.pageSize = pageSize;
            this.barcodePagination.loading = true;
            const xBarcodeTable = this.$refs.xBarcodeTable,
                endNum = currentPage * pageSize,
                startNum = endNum - pageSize,
                newList = this.barcodeDatas.slice(startNum, endNum);
            xBarcodeTable.reloadData(newList).then(() => {
                newList.forEach((item, index) => {
                    this.showJsBarcodeFn(item.newBarcode, ++index);
                });
                this.barcodePagination.loading = false;
            });
        },
        checkTableDataFn() {
            if (this.barcodeDatas.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请生成条码后再保存。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                this.$emit('saveBarcodeEmitFn', false);
                return true;
            }
            return false;
        },
        selectBarcodeChangeFn({ row }) {
            this.$refs.xBarcodeTable.setCurrentRow(row);
        },
        // 要展示条码的方法
        showJsBarcodeFn(barCode, index) {
            JsBarcode(`#jsBarcodeImg${index}`, barCode, {
                format: 'CODE128',
                lineColor: '#000',
                height: 74,
                margin: 0,
                fontSize: 18,
                displayValue: true
            });
        },
        generateBarcodeTopFn(btnName) {
            const btnActionName = {
                'send-again-barcode': () => {
                    if (!this.barcodeChildObj.hasBarcode) {
                        // 还未生成
                        if (this.checkBarcodeTypeFn()) return;
                        this.barcodePagination.current = 1;
                        this.barcodePagination.pageSize = 10;
                        this.barcodePagination.pageNum = 10;
                        if (this.pageNameType === 'godown-entry') {
                            const pageNameTypeFn = {
                                'godown-entry': () => {
                                    // 入库单
                                    this.grnBarcodeCreateAPIFn(this.barcodeChildObj);
                                }
                            };
                            pageNameTypeFn[this.pageNameType]();
                        } else if (this.pageNameType === 'ins-entry') {
                            const pageNameTypeFn = {
                                'ins-entry': () => {
                                    // 验货单
                                    const that = this;
                                    if (that.barcodeChildObj.qualified_quantity === 0) {
                                        that.$confirm({
                                            title: '确认',
                                            content: '合格数量为0，是否确认生成条码？',
                                            centered: true,
                                            maskClosable: false,
                                            onOk() {
                                                that.grnBarcodeCreateAPIFn(that.barcodeChildObj);
                                            }
                                        });
                                    } else {
                                        that.grnBarcodeCreateAPIFn(that.barcodeChildObj);
                                    }
                                }
                            };
                            pageNameTypeFn[this.pageNameType]();
                        }
                    }
                }
            };
            btnActionName[btnName]();
        },
        setBarcodeNumMoreFn(quantity) {
            if (typeof quantity !== 'undefined') {
                this.barcodeNumber = quantity;
            } else {
                this.barcodeNumber = this.$XEUtils.isInteger(this.barcodeChildObj.quantity)
                    ? this.barcodeChildObj.quantity
                    : 1;
            }
        },
        barcodeTypeChangeFn() {
            this.barcodeTypeDisabled = parseFloat(this.barcodeType) === 0;
        },
        checkBarcodeTypeFn() {
            if (parseFloat(this.barcodeType) === 1) {
                if (typeof this.barcodeNumber === 'undefined') {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入要生成的多条码数量',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return true;
                }
                if (!this.$XEUtils.isInteger(this.barcodeNumber)) {
                    this.$warning({
                        title: '操作提示',
                        content: '只能输入正整数',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return true;
                }
                if (parseInt(this.barcodeNumber) <= 0) {
                    this.$warning({
                        title: '操作提示',
                        content: '最小可生成条码数量为1',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return true;
                }
                if (parseInt(this.barcodeNumber) > 5000) {
                    this.$warning({
                        title: '操作提示',
                        content: '最大可生成条码数量为5000',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return true;
                }
            }
            return false;
        },
        openQrcodeFn() {
            let code = '';
            document.onkeydown = (e) => {
                let nextTime = new Date().getTime(),
                    lastTime = this.lastTime,
                    arrCode = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
                if (arrCode.includes(e.key)) {
                    nextTime = new Date().getTime();
                    if (nextTime && lastTime && nextTime - lastTime > 1000) {
                        code = e.key;
                    } else {
                        code = code + e.key;
                    }
                }

                this.lastTime = nextTime;
                if ((e.key === 'Enter' && code.length) || code.length >= 31) {
                    code = code.replace(/\s*/g, '');
                    console.log(`当前扫到的码是：${code}`);
                    code = '';
                }
            };
        },
        reSet() {
            switch (this.pageNameType) {
                case 'ins-entry':
                    if (String(this.barcodeChildObj.qualified_quantity).indexOf('.') === -1) {
                        this.barcodeNumber = this.barcodeChildObj.qualified_quantity;
                    } else {
                        this.barcodeNumber = 1;
                    }
                    break;
            }
        }
    },
    watch: {
        barcodeChildObj: {
            handler(val) {
                if (val && val.hasBarcode) {
                    if (this.pageNameType === 'godown-entry') {
                        const pageNameTypeFn = {
                            'godown-entry': () => {
                                // 入库单
                                this.grnBarcodeInfoAPIFn();
                            }
                        };
                        pageNameTypeFn[this.pageNameType]();
                    } else if (this.pageNameType === 'ins-entry') {
                        const pageNameTypeFn = {
                            'ins-entry': () => {
                                // 入库单
                                this.insBarcodeInfoAPIFn();
                            }
                        };
                        pageNameTypeFn[this.pageNameType]();
                        this.setBarcodeNumMoreFn();
                    }
                }
                this.pageNameType === 'godown-entry' && this.setBarcodeNumMoreFn();
            },
            immediate: true
        }
    },
    computed: {
        barcodeComputedText() {
            return this.barcodeDatas.length ? '重新生成' : '生成';
        }
    },
    mounted() {
        this.openQrcodeFn();
        this.reSet();
    }
};
</script>

<style scoped lang="less">
.send-again-barcode-group {
    display: flex;
    align-items: center;
    padding-bottom: 14px;

    .btn-default {
        height: 24px;
    }

    .input {
        display: flex;
        width: 150px;
        height: 24px;
        margin-right: 16px;
    }
}
</style>
