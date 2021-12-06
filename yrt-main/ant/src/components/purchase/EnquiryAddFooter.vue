<template>
    <a-row class="atc-footer">
        <a-row style="margin: 8px 0">
            <a-input :disabled="parseInt(formData.status) === 3" placeholder="摘要" @change="changeDesc" :value="formData.description"/>
        </a-row>
        <a-row>
            <a-col :offset="12" :span="12" style="text-align: right">
                <a-button
                    @click="printFn()"
                    type="primary"
                >打印</a-button>
                <a-button
                    @click="exportExcel()"
                    type="primary"
                    style="margin-left: 5px"
                >导出Excel</a-button>
                <!-- <a-button
                    @click="reset()"
                    style="margin-left: 5px"
                >取消</a-button> -->
                <a-button
                    @click="popupReply(3)"
                    type="primary"
                    style="margin-left: 5px"
                    v-if="parseInt(formData.status) === 3"
                >回复</a-button>
                <a-button
                    @click="popupStore(1)"
                    type="primary"
                    style="margin-left: 5px"
                    v-if="parseInt(formData.status) !== 3"
                >询价</a-button>
            </a-col>
        </a-row>
    </a-row>
</template>

<script>
import { BillsType } from '@/config/constants';

import {
    inquiryAddAPI,
    inquiryEditAPI
} from '@/service/pageAjax';

export default {
    name: 'EnquiryAddFooter',
    props: {
        proData: {
            type: Array,
            default: () => []
        },
        formData: {
            type: Object,
            default: () => {}
        },
        orgSystemType: {
            type: Number,
            default: () => 0
        },
        editable: {
            type: Object,
            default: () => {}
        }
    },
    data() {
        return {
            columns: [
                {
                    title: 'NO.',
                    dataIndex: 'idx',
                    minWidth: '40',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    type: 'index',
                    align: 'center',
                    fixed: 'left',
                    slots: {
                        default: ({ seq }) => {
                            let html = [
                                <div>
                                    <i class="red-dot-add"></i>
                                    <span>{seq}</span>
                                </div>
                            ];
                            return html;
                        }
                    }
                },
                {
                    title: '产品',
                    dataIndex: 'name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '120',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.name];
                        }
                    }
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '80',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification];
                        }
                    }
                },
                {
                    title: '厂商',
                    dataIndex: 'vendor_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '180',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.vendor_name];
                        }
                    }
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '60',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            let change = (val) => {
                                if (!val) {
                                    record.quantity = 0;
                                }
                                this.countFn();
                            };
                            let dom;
                            dom = [
                                <a-input-number
                                    min={0}
                                    v-model={record.quantity}
                                    class="input"
                                    onChange={change}
                                    width="30px"
                                />
                            ];
                            return dom;
                        }
                    }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            if (record.unit_data) {
                                let val = record.unit_data.key;
                                let tree = record.unit_data.data;
                                let displayRender = (rd) => {
                                    let len = rd.labels.length;
                                    const label = rd.labels[len - 1];
                                    return label.split('(')[0];
                                };
                                let change = (value, arr) => {
                                    record.unit_data.key = value;
                                    record.unit_inner_sn = value[value.length - 1];
                                    record.unit_name = arr[arr.length - 1].name;
                                    record.isNew = false;
                                    let newData = this.proData;
                                    this.proData = [];
                                    this.proData = newData;
                                };
                                return [
                                    <a-cascader
                                        allowClear={false}
                                        options={tree}
                                        value={val}
                                        displayRender={displayRender}
                                        expandTrigger="hover"
                                        placeholder=""
                                        onChange={change}
                                        changeOnSelect
                                    />
                                ];
                            }
                        }
                    }
                },
                {
                    title: '单价',
                    dataIndex: 'unit_price',
                    showOverflow: false,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.unit_price.toFixed(4)];
                        }
                    }
                },
                {
                    title: '金额',
                    dataIndex: 'sum',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            const value = !isNaN(row.sum) ? row.sum.toFixed(2) : '';
                            return [value];
                        }
                    }
                },
                {
                    title: '税率',
                    dataIndex: 'rate',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            let change = (val) => {
                                if (!val) {
                                    record.rate = 0;
                                }
                                this.countFn();
                            };
                            let valRate = parseInt(record.rate);
                            let dom = [
                                <a-select v-model={valRate} style="width:68px; margin-right: 4px" onChange={change}>
                                    <a-select-option value={0}> 0% </a-select-option>
                                    <a-select-option value={1}> 1% </a-select-option>
                                    <a-select-option value={3}> 3% </a-select-option>
                                    <a-select-option value={6}> 6% </a-select-option>
                                    <a-select-option value={9}> 9% </a-select-option>
                                    <a-select-option value={13}> 13% </a-select-option>
                                </a-select>
                            ];
                            return dom;
                        }
                    }
                },
                {
                    title: '含税单价',
                    dataIndex: 'rate_unit',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            let change = (val) => {
                                if (!val) {
                                    record.rate_unit = 0;
                                }
                                this.countFn();
                            };
                            let dom = [
                                <a-input-number
                                    v-model={record.rate_unit}
                                    disabled={this.recordDisabled}
                                    class="input"
                                    style="width:100%"
                                    min={0}
                                    formatter={(value) => `${value}`.replace(/^(\\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')}
                                    parser={(value) => value.replace(/^(\\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')}
                                    onChange={change}
                                />
                            ];
                            return dom;
                        }
                    }
                },
                {
                    title: '含税金额',
                    dataIndex: 'rate_price',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            const value = !isNaN(row.rate_price) ? row.rate_price.toFixed(2) : '';
                            return [value];
                        }
                    }
                },
                {
                    title: '税额',
                    dataIndex: 'rate_num',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            const value = !isNaN(row.rate_num) ? row.rate_num.toFixed(2) : '';
                            return [value];
                        }
                    }
                },
                {
                    title: '操作',
                    dataIndex: 'inquirer_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 60,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            let delFn = () => {
                                this.proData.forEach((item, index) => {
                                    if (item.key === record.key) {
                                        this.proData.splice(index, 1);
                                    }
                                });
                            };
                            return [
                                <a-tooltip placement="top" title="删除">
                                    <i onClick={delFn} class="yrt yrt-shanchu-copy cancel del_ico"></i>
                                </a-tooltip>
                            ];
                        }
                    }
                }
            ],
            unitList: []
        }
    },
    methods: {
        changeDesc(e) {
            this.$emit('changeFormData', {
                key: 'description',
                value: e.target.value
            });
        },
        getStableData() {
            let data = [];
            this.proData.forEach((item) => {
                if (item.name !== '') {
                    data.push(item);
                }
            });
            return data;
        },
        printFn() {
            const _this = this;
            let data = this.getStableData();
            this.$confirm({
                title: '你确定要打印吗？',
                onOk() {
                    let printContent = {
                        printData: data,
                        printForm: _this.formData,
                        printTable: _this.columns,
                        printType: 4, // 1、图片；2、耗材申领；3、出入库单; 4、询价单
                        pageType: 0, // 0 询价 1回复
                        orgName: _this.formData.orgName,
                        printTitle: '询价单',
                        unitList: _this.unitList
                    };
                    console.log(printContent);
                    printContent = JSON.stringify(printContent);
                    window.localStorage.printContent = printContent;
                    let routeUrl = _this.$router.resolve({
                        path: 'print',
                        name: 'print'
                    });
                    window.open(routeUrl.href, '_blank');
                },
                onCancel() {
                    console.log('Cancel');
                },
                class: 'test',
            });
        },
        // 导出excel
        exportExcel() {
            const form = this.formData;
            const formData = {
                title: '询价单',
                desc: {
                    label: '摘要',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '相关机构',
                            value: this.orgName
                        },
                        {
                            label: '联系人',
                            value: form.contacts
                        },
                        {
                            label: '电话',
                            value: form.phone
                        }
                    ]
                ]
            };
            console.log(this.columns);
            const _this = this;
            let data = this.getStableData();
            this.$confirm({
                title: '您确定要导出为 Excel 吗？',
                onOk() {
                    _this.$Utils.exportExcel(
                        _this.columns,
                        data.map((item) => ({
                            ...item,
                            rate: `${item.rate}%`,
                            discount_price: item.discount_price ? parseFloat(item.discount_price).toFixed(2) : '0',
                            discount_unit: item.discount_unit ? parseFloat(item.discount_unit).toFixed(2) : '0',
                            unit_price: item.unit_price ? parseFloat(item.unit_price).toFixed(2) : '0',
                            rate_price: item.rate_price ? parseFloat(item.rate_price).toFixed(2) : '0',
                            rate_unit: item.rate_unit ? parseFloat(item.rate_unit).toFixed(2) : '0',
                            rate_num: item.rate_num ? parseFloat(item.rate_num).toFixed(2) : '0'
                        })),
                        [],
                        formData
                    );
                },
                onCancel() {
                    console.log('Cancel');
                },
                class: 'test',
            });
        },
        reset() {
            this.$emit('refresh');
        },
        popupReply(type) {
            const _this = this;
            this.$confirm({
                title: '你想保存吗？',
                onOk() {
                    _this.inquiryAddFn(type);
                },
                onCancel() {
                    console.log('Cancel');
                },
                class: 'test',
            });
        },
        async inquiryAddFn(type) {
            this.formData.partA = parseInt(this.$cookie.get('userbelong'));
            let dataObj = Object.assign({}, this.formData);
            dataObj.billsType_old = dataObj.billsType;
            let proTD = this.getStableData();
            // 添加合同产品验证
            dataObj.detailList = JSON.parse(JSON.stringify(proTD));
            if (dataObj.detailList.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请添加产品',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            let proSuc = true;
            dataObj.detailList.forEach((item) => {
                let slen = String(item.rate).indexOf('.') + 1;
                if (slen > 0) {
                    this.$warning({
                        title: '操作提示',
                        content: '添加的产品税率不能为小数',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    proSuc = false;
                    throw new Error('结束循环');
                }
                if (item.unit_inner_sn === '' || !item.unit_inner_sn) {
                    this.$warning({
                        title: '操作提示',
                        content: '添加的产品单位不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    proSuc = false;
                    throw new Error('结束循环');
                }
                if (item.quantity === '' || !item.quantity) {
                    this.$warning({
                        title: '操作提示',
                        content: '添加的产品数量不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    proSuc = false;
                    throw new Error('结束循环');
                }
                let nowItem = item;
                dataObj.detailList.forEach((item) => {
                    if (item.specification === nowItem.specification && item.key !== nowItem.key) {
                        if (
                            item.unit_inner_sn === nowItem.unit_inner_sn &&
                            item.name === nowItem.name &&
                            item.sys_type === nowItem.sys_type
                        ) {
                            this.$warning({
                                title: '操作提示',
                                content: '相同规格型号的产品包装单位不能相同',
                                okText: '知道了',
                                centered: true,
                                maskClosable: true
                            });
                            proSuc = false;
                            throw new Error('结束循环');
                        }
                    }
                });
                console.log(dataObj);
            });
            if (!proSuc) {
                return false;
            }
            dataObj.status = type;
            // 根据不同发起机构判断
            let self = parseInt(this.$cookie.get('userSystemType'));
            let partB = parseInt(this.orgSystemType);
            if (self === 1) {
                // 医院
                if (partB === 2) {
                    // 医院对经销商
                    dataObj.billsType = BillsType.HospitalDealer;
                }
                if (partB === 3) {
                    // 医院对厂商
                    dataObj.billsType = BillsType.HospitalVendor;
                }
            }
            if (self === 2) {
                // 经销商
                if (partB === 2) {
                    // 经销商对经销商
                    dataObj.billsType = BillsType.DealerDealer;
                }
                if (partB === 3) {
                    // 经销商对厂商
                    dataObj.billsType = BillsType.DealerVendor;
                }
            }
            dataObj.detailList.forEach((item) => {
                item.rate = item.rate / 100;
                item.product_name = item.name; // 产品名称
                item.unit = item.unit_name; // 包装单位名称
                /* 传递给后端的字段取值变为 【单价 = 含税单价   含税单价 = 单价】 */
                // let unitPrice = item.unit_price.toFixed(4)
                item.unit_price = item.rate_unit;
                // item.rate_unit = unitPrice
                item.sum = item.sum;
                // item.sum = item.sum.toFixed(2);
                item.rate_num = item.rate_num;
                // item.rate_num = item.rate_num.toFixed(2);
            });
            console.log(dataObj);
            await inquiryEditAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let obj = res.info;
                    obj.sys_type = partB;
                    switch (obj.billsType) {
                        case 'HD':
                        case 'DD':
                            this.$emit('changeTabFn', 'dealer');
                            break;
                        case 'HV':
                        case 'DV':
                            this.$emit('changeTabFn', 'vendor');
                            break;
                    }
                }
            });
        },
        popupStore(type) {
            const _this = this;
            this.$confirm({
                title: '你想保存吗？',
                onOk() {
                    _this.store(type);
                },
                onCancel() {
                    console.log('Cancel');
                },
                class: 'test',
            });
        },
        async store(type) {
            if (this.editable.flag) {
                this.inquiryAddFn(type);
            } else {
                // 新增询价单据 type 0:草稿 1:询价
                console.log('FORMDATA', this.formData);
                console.log('PROTABLEDATA', this.proData);
                this.formData.partA = parseInt(this.$cookie.get('userbelong'));
                let dataObj = this.formData;
                let proTD = this.getStableData();
                if (dataObj.partB === '' || !dataObj.partB) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择相关机构',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                // 添加合同产品验证
                dataObj.detailList = JSON.parse(JSON.stringify(proTD));
                if (dataObj.detailList.length <= 0) {
                    this.$warning({
                        title: '操作提示',
                        content: '请添加产品',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                let proSuc = true;
                dataObj.detailList.forEach((item) => {
                    let slen = String(item.rate).indexOf('.') + 1;
                    if (slen > 0) {
                        this.$warning({
                            title: '操作提示',
                            content: '添加的产品税率不能为小数',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        proSuc = false;
                        throw new Error('结束循环');
                    }
                    if (item.unit_inner_sn === '' || !item.unit_inner_sn) {
                        this.$warning({
                            title: '操作提示',
                            content: '添加的产品单位不能为空',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        proSuc = false;
                        throw new Error('结束循环');
                    }
                    if (item.quantity === '' || !item.quantity) {
                        this.$warning({
                            title: '操作提示',
                            content: '添加的产品数量不能为空',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        proSuc = false;
                        throw new Error('结束循环');
                    }
                    // if (item.rate_unit === '' || !item.rate_unit) {
                    //     this.$warning({
                    //         title: '操作提示',
                    //         content: '添加的产品含税单价不能为空',
                    //         okText: '知道了',
                    //         centered: true,
                    //         maskClosable: true
                    //     });
                    //     proSuc = false;
                    //     throw new Error('结束循环');
                    // }
                    let nowItem = item;
                    dataObj.detailList.forEach((item) => {
                        if (item.specification === nowItem.specification && item.key !== nowItem.key) {
                            if (
                                item.unit_inner_sn === nowItem.unit_inner_sn &&
                                item.name === nowItem.name &&
                                item.sys_type === nowItem.sys_type
                            ) {
                                this.$warning({
                                    title: '操作提示',
                                    content: '相同规格型号的产品包装单位不能相同',
                                    okText: '知道了',
                                    centered: true,
                                    maskClosable: true
                                });
                                proSuc = false;
                                throw new Error('结束循环');
                            }
                        }
                    });
                    console.log(dataObj);
                });
                if (!proSuc) {
                    return false;
                }
                dataObj.status = type;
                // 根据不同发起机构判断
                console.log('SYSTEMTYPE', this.$cookie.get('userSystemType'));
                console.log('ORGTYPE', this.orgSystemType);
                console.log('BILLTYPE', BillsType);
                let self = parseInt(this.$cookie.get('userSystemType'));
                let partB = parseInt(this.orgSystemType);
                if (self === 1) {
                    // 医院
                    if (partB === 2) {
                        // 医院对经销商
                        dataObj.billsType = BillsType.HospitalDealer;
                    }
                    if (partB === 3) {
                        // 医院对厂商
                        dataObj.billsType = BillsType.HospitalVendor;
                    }
                }
                if (self === 2) {
                    // 经销商
                    if (partB === 2) {
                        // 经销商对经销商
                        dataObj.billsType = BillsType.DealerDealer;
                    }
                    if (partB === 3) {
                        // 经销商对厂商
                        dataObj.billsType = BillsType.DealerVendor;
                    }
                }
                dataObj.detailList.forEach((item) => {
                    item.rate = item.rate / 100;
                    item.product_name = item.name; // 产品名称
                    item.unit = item.unit_name; // 包装单位名称
                    /* 传递给后端的字段取值变为 【单价 = 含税单价   含税单价 = 单价】 */
                    // let unitPrice = item.unit_price.toFixed(4)
                    item.unit_price = item.rate_unit;
                    // item.rate_unit = unitPrice
                    item.sum = item.sum;
                    // item.sum = item.sum.toFixed(2);
                    item.rate_num = item.rate_num;
                    // item.rate_num = item.rate_num.toFixed(2);
                });
                console.log(dataObj);
                await inquiryAddAPI(dataObj).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let obj = res.info;
                        obj.isActive = '';
                        obj.key = obj.billsType + '' + obj.ilps_inner_sn + '' + obj.partA + '' + obj.partB;
                        obj.sys_type = partB;
                        switch (obj.billsType) {
                            case 'HD':
                            case 'DD':
                                this.$emit('changeTabFn', 'dealer');
                                break;
                            case 'HV':
                            case 'DV':
                                this.$emit('changeTabFn', 'vendor');
                                break;
                        }
                    }
                });
            }
        }
    },
    mounted() {
    }
};
</script>