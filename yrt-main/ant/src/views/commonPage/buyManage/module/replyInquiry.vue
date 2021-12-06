<template>
    <div class="scoped" style="margin: -24px; min-height: 600px">
        <div class="tab-table-wrap no_left">
            <div class="head_box">
                <div class="tit_box">{{ title }}</div>
            </div>
            <div class="search-box">
                <div class="search-item num">
                    <span class="label">单据编号</span>
                    <div class="input_box" style="margin-right: 24px">
                        <a-input type="text" disabled="true" v-model="formData.ilps_sn" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label"><em class="em red">*</em>询价机构</span>
                    <div class="input_box" style="margin-right: 30px">
                        <a-input
                            type="text"
                            disabled="true"
                            readonly="readonly"
                            v-model="orgName"
                            @click="openChooseTb()"
                            placeholder=""
                        />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label">联系人</span>
                    <div class="input_box input" style="margin-right: 30px">
                        <a-input type="text" disabled="true" v-model="formData.contacts" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label">电话</span>
                    <div class="input_box" style="margin-right: 10px">
                        <a-input type="text" disabled="true" v-model="formData.phone" placeholder="" />
                    </div>
                </div>
                <div class="search-item digest">
                    <span class="label">摘要</span>
                    <div class="input_box">
                        <a-input
                            disabled="true"
                            style="width: 1042px"
                            type="text"
                            v-model="formData.description"
                            placeholder=""
                        />
                    </div>
                </div>
            </div>
            <div style="padding: 0 22px">
                <vxe-grid
                    ref="xTable"
                    size="small"
                    :columns="columns"
                    height="440"
                    resizable
                    auto-resize
                    border
                    highlight-current-row
                    align="center"
                    :data="proTableData"
                    :loading="tableLoading"
                >
                </vxe-grid>
            </div>
        </div>
        <div class="button_cont">
            <a-button @click="printFn()" type="primary" style="float: left; left: 24px; top: 13px">打印</a-button>
            <a-button @click="exportExcel()" type="primary" style="float: left; left: 36px; top: 13px"
                >导出Excel</a-button
            >
            <a-button
                v-if="!isShow"
                :disabled="isShow"
                @click="inquiryAddFn(3)"
                type="primary"
                style="float: right; right: 22px; top: 13px"
                >回复</a-button
            >
            <a-button
                v-if="!isShow"
                @click="inquiryAddFn(2)"
                type="primary"
                style="float: right; right: 34px; top: 13px"
                >存草稿</a-button
            >
            <a-button @click="cancelFn()" style="float: right; right: 44px; top: 13px"
                ><span v-if="isShow">关闭</span><span v-else>取消</span></a-button
            >
        </div>
        <a-modal
            :title="chooseOrgMd.title"
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            @cancel="chooseOrgMd.alert = false"
            class="modal-form-input-scoped"
        >
            <choose-org-tab ref="chooseOrgMd" @chooseOrg="chooseOrgFn" :partType="1"></choose-org-tab>
        </a-modal>
        <a-modal
            :title="addProductMd.title"
            :visible="addProductMd.alert"
            :maskClosable="false"
            :mask="false"
            :width="1000"
            :centered="true"
            @cancel="addProductMd.alert = false"
            class="modal-form-input-scoped global-drag add_product"
        >
            <div v-globalDrag="{ el: 'add_product' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addProductMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="addProductMd.alert = false">关闭</a-button>
                <a-button :loading="addProductMd.loading" @click="addProductFn" type="primary">添加</a-button>
            </template>
            <add-product ref="chooseProMd" @returnData="getProReturn" :partB="part_b_obj"></add-product>
        </a-modal>
    </div>
</template>

<script>
import { platformUnitTree, inquiryEditAPI, inquiryInfoAPI } from '@/service/pageAjax';

import chooseOrgTab from '@/components/chooseOrgTab';
import addProduct from '@/components/addProduct';
import { BillsType } from '@/config/constants';

export default {
    name: 'replyInquiry',
    props: ['editObj'],
    components: {
        chooseOrgTab,
        addProduct
    },
    data() {
        return {
            userSystemType: parseInt(this.$cookie.get('userSystemType')),
            // 列表中的输入框选择框等是否禁止编辑
            recordDisabled: false,
            title: '询价回复单',
            isShow: false,
            createMd: {
                title: `${this.$route.meta.title} - 新增询价单`,
                alert: false,
                loading: false
            },
            chooseOrgMd: {
                title: `${this.$route.meta.title} - 新增询价单 - 选择机构`,
                alert: false,
                loading: false
            },
            addProductMd: {
                title: `${this.$route.meta.title} - 新增询价单 - 相关产品 - 添加`,
                alert: false,
                loading: false
            },
            part_b_obj: {},
            orgName: '',
            formData: {
                partA: '', // 询价机构ID
                partB: '', // 被询价机构ID
                ilps_sn: '', // 询价单编号
                contacts: '', // 联系人
                phone: '', // 电话
                detailList: '',
                description: '' // 摘要
            },
            oldFormData: {},
            tableLoading: false,
            columns: [
                {
                    title: 'NO.',
                    dataIndex: 'idx',
                    minWidth: 60,
                    type: 'index',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    fixed: 'left'
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    minWidth: 250,
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.product_name];
                        }
                    }
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
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
                    minWidth: 300,
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
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.quantity];
                        }
                    }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.unit_name];
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
                            return [this.$XEUtils.commafy(row.unit_price, { fixed: 4 })];
                        }
                        // default: ({ row }) => {
                        //     let record = row
                        //     let change = (val) => {
                        //         if (!val) {
                        //             record.unit_price = 0
                        //         }
                        //         this.countFn()
                        //     }
                        //     let dom = [<a-input-number
                        //         v-model = {record.unit_price}
                        //         disabled={this.recordDisabled}
                        //         class='input'
                        //         style='width:80px'
                        //         min = {0}
                        //         formatter={value => `${value}`.replace(/^(\\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')}
                        //         parser={value => value.replace(/^(\\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')}
                        //         onChange = {change}
                        //     />]
                        //     return dom
                        // }
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
                            return [this.$XEUtils.commafy(row.sum, { fixed: 2 })];
                        }
                    }
                },
                // {
                //     title: '折扣',
                //     dataIndex: 'discount',
                //     showOverflow: false,
                //     showHeaderOverflow: true,
                //     minWidth: 100,
                //     align: 'center',
                //     slots: {
                //         default: ({ row }) => {
                //             let record = row
                //             let change = (val) => {
                //                 if (!val) {
                //                     record.discount = 0
                //                 }
                //                 this.countFn()
                //             }
                //             let dom = [<a-input-number
                //                 v-model = {record.discount}
                //                 disabled={this.recordDisabled}
                //                 class='input'
                //                 style='width:83px'
                //                 min = {0.01}
                //                 step={0.01}
                //                 onChange = {change}
                //             />]
                //             return dom
                //         }
                //     }
                // },
                // {
                //     title: '折后单价',
                //     dataIndex: 'discount_unit',
                //     showOverflow: true,
                //     showHeaderOverflow: true,
                //     minWidth: 100,
                //     align: 'center',
                //     slots: {
                //         default: ({ row }) => {
                //             return [row.discount_unit.toFixed(2)]
                //         }
                //     }
                // },
                // {
                //     title: '折后金额',
                //     dataIndex: 'discount_price',
                //     minWidth: 100,
                //     align: 'center',
                //     slots: {
                //         default: ({ row }) => {
                //             return [!isNaN(row.discount_price) ? Number(row.discount_price).toFixed(2) : '']
                //         }
                //     }
                // },
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
                            let dom = [
                                <a-select
                                    v-else
                                    disabled={this.recordDisabled}
                                    v-model={row.rate}
                                    style="width:68px; margin-right: 4px"
                                    onChange={change}
                                >
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
                    minWidth: 120,
                    align: 'center',
                    slots: {
                        // default: ({ row }) => {
                        //     return [row.rate_unit.toFixed(2)]
                        // }
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
                            return [this.$XEUtils.commafy(row.rate_price, { fixed: 2 })];
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
                            return [this.$XEUtils.commafy(row.rate_num, { fixed: 2 })];
                        }
                    }
                }
            ],
            proTableData: [],
            currentTrObj: {},
            unitList: [],
            scrollYNumber: 400
        };
    },

    methods: {
        async refreshReplyPage(isShow) {
            // 初始化
            const { partA, status, billsType, ilps_inner_sn: ilpsInnerSn } = this.editObj;
            this.title = status === '3' ? '询价单' : '询价回复单';
            this.isShow = status === '3' ? true : isShow;
            // 已回复状态的不允许编辑
            this.recordDisabled = status === '3';
            const params = {
                partA,
                billsType,
                ilps_inner_sn: ilpsInnerSn
            };

            await inquiryInfoAPI({ params }).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.oldFormData = JSON.parse(JSON.stringify(res.info));
                    this.formData = res.info;
                    // 如果是询价回复，partA_name，其它partB_name
                    this.orgName =
                        this.editObj && this.editObj.systemType === 1 ? res.info.partA_name : res.info.partB_name;
                    this.part_b_obj.id = res.info.partB;
                    this.proTableData = res.info.detailList;
                    this.proTableData.forEach((item, idx) => {
                        item.idx = idx + 1;
                        item.key = item.product_inner_sn + '' + item.specification_inner_sn;
                        item.rate = item.rate * 100;
                        item.rate_unit = item.unit_price; // 含税单价 = 单价 【默认值】
                        item.unit_price = parseFloat(item.rate_unit) / (1 + parseFloat(item.rate) / 100); // 单价 = 含税单价 / (1 + 税率 / 100)
                        // item.discount_price = parseFloat(item.discount_unit) * parseFloat(item.quantity) // 折后金额 =
                        item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额 = 数量 * 单价
                        // item.discount_unit = parseFloat(item.unit_price) * parseFloat(item.discount) // 折后单价 = 单价 * 折扣
                        item.rate_price = parseFloat(item.quantity) * parseFloat(item.rate_unit); // 含税金额 = 数量 * 含税单价
                        item.rate_num = parseFloat(item.rate_price) - parseFloat(item.sum); // 税额 = 含税金额 - 金额
                        this.venUnitTreeFn(item);
                    });
                    this.$refs.xTable.refreshColumn();
                }
            });
        },
        countFn() {
            // 自动计算
            let data = JSON.parse(JSON.stringify(this.proTableData));
            data.forEach((item) => {
                item.unit_price = parseFloat(item.rate_unit) / (1 + parseFloat(item.rate) / 100); // 单价
                // item.discount_price = parseFloat(item.discount_unit) * parseFloat(item.quantity) // 折后金额
                item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额
                // item.discount_unit = parseFloat(item.unit_price) * parseFloat(item.discount) // 折后单价
                item.rate_price = parseFloat(item.quantity) * parseFloat(item.rate_unit); // 含税金额
                item.rate_num = parseFloat(item.rate_price) - parseFloat(item.sum); // 税额
            });
            this.proTableData = data;
        },
        openChooseTb() {
            // 打开选择甲乙方
            this.chooseOrgMd.alert = true;
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
        },
        async chooseOrgFn(org) {
            // 获取选中机构信息
            this.chooseOrgMd.alert = false;
            this.orgName = org.org_name;
            this.formData.partB = org.org_id;
        },
        openAddPro() {
            // 打开选择产品
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn('add_product');
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        addProductFn() {
            // 添加商品
            this.$refs.chooseProMd.returnProList();
            this.addProductMd.alert = false;
        },
        getProReturn(dea, ven) {
            // 获取回调商品
            let arr = ven.concat(dea);
            arr.forEach((item, idx) => {
                if (item.product_type === 0) {
                    item.sys_type = item.dealer_name;
                } else if (item.product_type === 1) {
                    item.sys_type = item.vendor_name;
                }
                item.isActive = '';
                item.unit_inner_sn = '';
                item.quantity = '';
                item.idx = idx + 1;
                this.proTableData.push(item);
            });
        },
        customRowFn(record) {
            // 设置行属性 —— 点击每一行的事件
            return {
                class: record.isActive,
                on: {
                    click: () => {
                        this.currentTrObj = record;
                        this.chooseTrEvent(record);
                    },
                    dblclick: () => {
                        this.currentTrObj = record;
                        this.chooseTrEvent(record);
                    }
                }
            };
        },
        chooseTrEvent(record) {
            // 选中事件
            this.chooseTr(record.key);
        },
        chooseTr(id) {
            // 选中行渲染
            this.isChooseTr = true;
            this.proTableData.forEach((item) => {
                if (item.key === id) {
                    item.isActive = 'active-tr';
                } else {
                    item.isActive = '';
                }
            });
        },
        cancelFn() {
            this.$emit('cancel');
        },
        async venUnitTreeFn(record) {
            // 厂商商产品单位
            let oid = record.vendor_inner_sn;
            let pid = record.product_inner_sn;
            let psid = record.specification_inner_sn;
            await platformUnitTree(oid, pid, psid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let circle = (item) => {
                        // 递归遍历
                        item.forEach((item) => {
                            item.label = item.name;
                            item.value = item.unit_inner_sn;
                            if (item.children && item.children.length > 0) {
                                circle(item.children);
                            } else {
                                item.children = null;
                            }
                        });
                    };
                    res.tree.forEach((item) => {
                        item.label = item.name;
                        item.value = item.unit_inner_sn;
                        if (item.children && item.children.length > 0) {
                            circle(item.children);
                        } else {
                            item.children = null;
                        }
                    });
                    let unitArr = {
                        key: record.key,
                        data: res.tree
                    };
                    let addSt = true;
                    this.unitList.forEach((item) => {
                        if (item.key === unitArr.key) {
                            addSt = false;
                        }
                    });
                    if (addSt) {
                        this.unitList.push(unitArr);
                    }
                    if (this.unitList.length <= 0) {
                        this.unitList.push(unitArr);
                    }
                }
            });
        },
        async inquiryAddFn(type) {
            // 新增询价单据 type 2:草稿 3:询价
            let newObj = this.formData;
            let oldObj = this.oldFormData;
            let dataObj = {};

            // 排除产品列表比较
            let baseInfoChanged = false;
            const newObjTemp = this.$XEUtils.omit(newObj, ['detailList']);
            for (let server in newObjTemp) {
                let serverItem = newObjTemp[server];
                for (let old in oldObj) {
                    let oldItem = oldObj[old];
                    if (server === old && serverItem !== oldItem) {
                        dataObj = {
                            ...dataObj,
                            [server]: serverItem, // 新值
                            [server + '_old']: oldItem // 旧值
                        };
                        baseInfoChanged = true;
                    }
                }
            }

            if (dataObj.ilps_sn) {
                dataObj.ilps_sn = this.$Utils.trim(dataObj.ilps_sn);
                if (dataObj.ilps_sn === '' || !dataObj.ilps_sn) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入单据编号',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
            }
            if (dataObj.partB) {
                if (dataObj.partB === '' || !dataObj.partB) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择经销商/厂商',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
            }
            if (dataObj.contacts) {
                dataObj.contacts = this.$Utils.trim(dataObj.contacts);
                if (dataObj.contacts === '' || !dataObj.contacts) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入联系人',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
            }
            if (dataObj.phone) {
                dataObj.phone = this.$Utils.trim(dataObj.phone);
                if (dataObj.phone === '' || !dataObj.phone) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入电话',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
            }
            // 添加合同产品验证
            dataObj.detailList = this.proTableData;
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
            dataObj.detailList = JSON.parse(JSON.stringify(this.proTableData));
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
                if (item.rate_unit === '' || !item.rate_unit) {
                    this.$warning({
                        title: '操作提示',
                        content: '添加的产品含税单价不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    proSuc = false;
                    throw new Error('结束循环');
                }
                let nowItem = item;
                dataObj.detailList.forEach((item) => {
                    if (
                        item.key !== nowItem.key &&
                        item.specification === nowItem.specification &&
                        item.vendor_name === nowItem.vendor_name &&
                        item.product_name === nowItem.product_name
                    ) {
                        if (item.unit_inner_sn === nowItem.unit_inner_sn) {
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
            });
            if (!proSuc) {
                return false;
            }
            dataObj.status = type;
            // 根据不同发起机构判断
            dataObj.partA = newObj.partA;
            dataObj.partB = newObj.partB;
            dataObj.ilps_inner_sn = oldObj.ilps_inner_sn;
            let partB = parseInt(dataObj.partB);
            if (this.userSystemType === 1) {
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
            if (this.userSystemType === 2) {
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
            dataObj.billsType_old = oldObj.billsType;
            dataObj.billsType = oldObj.billsType;
            dataObj.detailList.forEach((item) => {
                item.rate = item.rate / 100;
                item.unit = item.unit_name; // 包装单位名称
                /* 传递给后端的字段取值变为 【单价 = 含税单价   含税单价 = 单价】 */
                // let unitPrice = item.unit_price.toFixed(4)
                item.unit_price = item.rate_unit;
                // item.rate_unit = unitPrice
                item.sum = item.sum.toFixed(2);
                item.rate_num = item.rate_num.toFixed(2);
            });

            // 检查产品列表是否修改过  未修改过不发送请求
            const listChanged = this.$Utils.checkListChanged(dataObj.detailList, oldObj.detailList);
            const isReply = type === 3; // 是否为回复操作
            const changed = baseInfoChanged || listChanged || isReply;
            // if (!changed) {
            //     this.$emit('done', changed)
            //     return
            // }
            await inquiryEditAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.$emit('done', changed);
                }
            });
        },
        // 打印
        printFn() {
            let printContent = {
                printData: this.proTableData,
                printForm: this.formData,
                printTable: this.columns.slice(0).map((item) => {
                    return {
                        dataIndex: item.dataIndex,
                        title: item.title,
                        align: 'center',
                        slots: item.slots
                    };
                }),
                printType: 4, // 1、图片；2、耗材申领；3、出入库单; 4、询价单
                pageType: 1, // 0 询价 1回复
                orgName: this.orgName,
                // printTitle: '询价回复单',
                printTitle: this.title,
                unitList: this.unitList
            };
            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        // 导出excel
        exportExcel() {
            const form = this.formData;
            const formData = {
                // title: '询价回复单',
                title: this.title,
                desc: {
                    label: '摘要',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '单据编号',
                            value: form.ilps_sn
                        },
                        {
                            label: '询价机构',
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
            this.$Utils.exportExcel(
                this.columns,
                this.proTableData.map((item) => {
                    return {
                        ...item,
                        rate: `${item.rate}%`,
                        discount_price: item.discount_price ? item.discount_price.toFixed(2) : '0',
                        discount_unit: item.discount_unit ? item.discount_unit.toFixed(2) : '0',
                        unit_price: item.unit_price.toFixed(4),
                        sum: item.sum.toFixed(2),
                        rate_price: item.rate_price.toFixed(2),
                        rate_num: item.rate_num.toFixed(2)
                    };
                }),
                [],
                formData
            );
        }
    }
};
</script>

<style scoped lang="less">
.del_ico {
    color: red;
    font-size: 16px;
}
.head_box {
    .tit_box {
        text-align: center;
        font-size: 30px;
        margin-top: 9px;
    }
}
.button_cont {
    position: absolute;
    width: 100%;
    bottom: 25px;
}
.search-box {
    margin-top: 15px;
    margin-bottom: 15px;
    padding: 0 22px;
    .search-item {
        display: inline-block;
        vertical-align: top;
        &.num {
            position: absolute;
            right: 0px;
            top: 20px;
        }
        &.digest {
            position: absolute;
            left: 24px;
            bottom: -40px;
        }
        .ant-cascader-input.ant-input {
            height: 24px !important;
        }
    }
    .label {
        display: inline-block;
        min-width: 40px;
        text-align: right;
        margin-right: 10px;
        .em {
            line-height: 14px;
            font-size: 21px;
            vertical-align: middle;
            padding-right: 4px;
        }
    }
    .input_box {
        display: inline-block;
        width: 220px;
        .ant-input {
            line-height: 24px;
            height: 24px;
        }
    }
}
</style>
