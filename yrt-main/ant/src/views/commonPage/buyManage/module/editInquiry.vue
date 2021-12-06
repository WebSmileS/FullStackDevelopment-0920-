<template>
    <div class="scoped" style="margin: -24px; min-height: 580px">
        <div class="tab-table-wrap no_left">
            <div class="head_box">
                <div class="tit_box">询价单</div>
            </div>
            <div class="search-box">
                <div class="search-item num">
                    <span class="label">单据编号</span>
                    <div class="input_box" style="margin-right: 24px">
                        <a-input type="text" v-model="formData.ilps_sn" disabled="true" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label"><em class="em red">*</em>单位名称</span>
                    <div class="input_box" style="margin-right: 30px">
                        <a-input
                            :disabled="preview"
                            type="text"
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
                        <a-input :disabled="preview" type="text" v-model="formData.contacts" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label">电话</span>
                    <div class="input_box" style="margin-right: 10px">
                        <a-input :disabled="preview" type="text" v-model="formData.phone" placeholder="" />
                    </div>
                </div>
            </div>
            <div @dblclick="openAddPro" style="padding: 0 22px">
                <a-tooltip placement="topLeft" title="新增产品">
                    <a-button @click="openAddPro" icon="plus" style="height: 24px; padding: 0 4px"></a-button>
                </a-tooltip>
                <vxe-grid
                    ref="xTable"
                    size="small"
                    :columns="columns"
                    height="400"
                    resizable
                    auto-resize
                    border
                    highlight-current-row
                    align="center"
                    @cell-dblclick="dblClickCell"
                    :data="proTableData"
                    :loading="tableLoading"
                >
                    <template v-slot:empty>
                        <div class="dblclick-wrap">
                            <span>双击添加产品</span>
                        </div>
                    </template>
                </vxe-grid>
            </div>
        </div>
        <div class="search-item digest">
            <span class="label">摘要</span>
            <div class="input_box">
                <a-input
                    :disabled="preview"
                    style="width: 1042px"
                    type="text"
                    v-model="formData.description"
                    placeholder=""
                />
            </div>
        </div>
        <div class="button_cont">
            <a-button @click="printFn()" type="primary" style="float: left; left: 24px; top: 13px">打印</a-button>
            <a-button @click="exportExcel()" type="primary" style="float: left; left: 36px; top: 13px"
                >导出Excel</a-button
            >
            <a-button
                :disabled="preview"
                @click="inquiryAddFn(1)"
                type="primary"
                style="float: right; right: 22px; top: 13px"
                >询价</a-button
            >
            <a-button
                :disabled="preview"
                v-if="editObj.status !== '1' && editObj.status !== '2'"
                @click="inquiryAddFn(0)"
                type="primary"
                style="float: right; right: 34px; top: 13px"
                >存草稿</a-button
            >
            <a-button @click="cancelFn()" style="float: right; right: 44px; top: 13px">
                <span v-if="preview">关闭</span>
                <span v-else>取消</span>
            </a-button>
        </div>
        <a-modal
            :title="chooseOrgMd.title"
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            :zIndex="3"
            @cancel="chooseOrgMd.alert = false"
            class="scoped modal-form-input-scoped global-drag chooseOrgMd"
        >
            <div v-globalDrag="{ el: 'chooseOrgMd' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <div style="margin-top: 56px">
                <choose-org-tab
                    ref="chooseOrgMd"
                    @chooseOrg="chooseOrgTips"
                    :pageTypeName="6"
                    :partType="1"
                    :isCertifie="true"
                ></choose-org-tab>
            </div>
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
            <add-product
                ref="chooseProMd"
                @returnData="getProReturn"
                :partB="part_b_obj"
                :metaTitle="addProductMd.title"
            ></add-product>
        </a-modal>
    </div>
</template>

<script>
import { platformUnitTree, inquiryEditAPI, inquiryInfoAPI, platformUnitParents } from '@/service/pageAjax';

import chooseOrgTab from '@/components/chooseOrgTab';
import addProduct from '@/components/addProduct';

export default {
    name: 'editInquiry',
    props: ['editObj'],
    components: {
        chooseOrgTab,
        addProduct
    },
    data() {
        return {
            preview: false,
            createMd: {
                title: `询价 - 新增`,
                alert: false,
                loading: false
            },
            chooseOrgMd: {
                title: `询价 - 修改 - 选择机构`,
                alert: false,
                loading: false
            },
            addProductMd: {
                title: `询价 - 修改 - 添加产品`,
                alert: false,
                loading: false
            },
            part_b_obj: {},
            orgName: '',
            orgSystemType: '',
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
                    minWidth: '60',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    type: 'index',
                    align: 'center',
                    fixed: 'left',
                    slots: {
                        default: ({ row, seq }) => {
                            let cls = '';
                            if (!row.detail_inner_sn) {
                                cls = 'red-dot-add';
                            }
                            let html = [
                                <div>
                                    <i class={cls}></i>
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
                    minWidth: '250',
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
                    minWidth: '150',
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
                    minWidth: '200',
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
                    minWidth: '130',
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
                                    disabled={this.preview}
                                    min={0}
                                    v-model={record.quantity}
                                    class="input"
                                    onChange={change}
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
                                    return label ? label.split('(')[0] : '';
                                };
                                let change = (value, arr) => {
                                    record.unit_data.key = value;
                                    record.unit_inner_sn = value[value.length - 1];
                                    record.unit_name = arr[arr.length - 1].name;
                                    record.isNew = false;
                                    let newData = this.proTableData;
                                    this.proTableData = [];
                                    this.proTableData = newData;
                                };
                                let clickFn = () => {
                                    if (record.isEdit) {
                                        this.venUnitTreeFn(record, 'edit');
                                        record.isEdit = false;
                                    }
                                };
                                return [
                                    <a-cascader
                                        disabled={this.preview}
                                        onClick={clickFn}
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
                    minWidth: 110,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.unit_price.toFixed(4)];
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
                        //         disabled={this.preview}
                        //         v-model = {record.unit_price}
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
                            const value = !isNaN(row.sum) ? row.sum.toFixed(2) : '';
                            return [value];
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
                //                 disabled={this.preview}
                //                 v-model = {record.discount}
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
                //             const value = !isNaN(row.discount_unit) ? row.discount_unit.toFixed(2) : ''
                //             return [value]
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
                //             const value = !isNaN(row.discount_price) ? Number(row.discount_price).toFixed(2) : ''
                //             return [value]
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
                                    disabled={this.preview}
                                    v-model={record.rate}
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
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        // default: ({ row }) => {
                        //     const value = !isNaN(row.rate_unit) ? row.rate_unit.toFixed(2) : ''
                        //     return [value]
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
                    minWidth: '60',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let delFn = () => {
                                let record = row;
                                this.proTableData.forEach((item, index) => {
                                    if (item.key === record.key) {
                                        this.proTableData.splice(index, 1);
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
            proTableData: [],
            currentTrObj: {},
            unitList: [],
            scrollYNumber: 400
        };
    },
    methods: {
        async refreshPage() {
            // 初始化
            let params = {
                partA: this.editObj.partA,
                ilps_inner_sn: this.editObj.ilps_inner_sn,
                billsType: this.editObj.billsType
            };
            let type = parseFloat(this.editObj.status);
            // if (type === 1 || type === 2 || type === 3) {
            if (type === 3) {
                this.preview = true;
            } else {
                this.preview = false;
            }
            await inquiryInfoAPI({ params }).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.oldFormData = JSON.parse(JSON.stringify(res.info));
                    this.formData = res.info;
                    // 如果是询价回复，partA_name，其它partB_name
                    this.orgName =
                        this.editObj && this.editObj.systemType === 1 ? res.info.partA_name : res.info.partB_name;
                    this.part_b_obj.id = res.info.partB;
                    this.part_b_obj.name = res.info.partB_name;
                    this.part_b_obj.systemType = this.editObj.systemType;
                    this.part_b_obj.modalType = 6;
                    this.part_b_obj.billsType = res.info.billsType;
                    this.proTableData = [];
                    res.info.detailList.forEach((item, idx) => {
                        item.isActive = '';
                        item.key = Math.random();
                        item.idx = idx + 1;
                        item.name = item.product_name;
                        item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额
                        item.rate = parseInt(item.rate * 100); // 税率
                        item.rate_unit = item.unit_price; // 含税单价 = 单价 【默认值】
                        item.unit_price = parseFloat(item.rate_unit) / (1 + parseFloat(item.rate) / 100); // 单价 = 含税单价 / (1 + 税率 / 100)
                        // item.discount_price = parseFloat(item.discount_unit) * parseFloat(item.quantity) // 折后金额 =
                        item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额 = 数量 * 单价
                        // item.discount_unit = parseFloat(item.unit_price) * parseFloat(item.discount) // 折后单价 = 单价 * 折扣
                        item.rate_price = parseFloat(item.quantity) * parseFloat(item.rate_unit); // 含税金额 = 数量 * 含税单价
                        item.rate_num = parseFloat(item.rate_price) - parseFloat(item.sum); // 税额 = 含税金额 - 金额
                        item.isEdit = true;
                        item.unit_data = {
                            key: [item.unit_inner_sn],
                            data: [
                                {
                                    value: item.unit_inner_sn,
                                    label: item.unit_name
                                }
                            ]
                        };
                        this.proTableData.push(item);
                    });
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
        chooseOrgTips(org) {
            const _this = this;
            if (this.formData.partB && org.org_id !== this.formData.partB) {
                _this.$confirm({
                    title: '操作提示',
                    content:
                        '当前选择和已选择单位名称不一致，如确认要改变已选择单位名称，已选择单位名称的所有信息会被清空。',
                    okText: '确认',
                    cancelText: '取消',
                    centered: true,
                    maskClosable: false,
                    mask: false,
                    onOk: () => {
                        _this.chooseOrgFn(org);
                        this.proTableData = [];
                        this.formData.contacts = '';
                        this.formData.phone = '';
                        this.formData.description = '';
                    }
                });
            } else {
                _this.chooseOrgFn(org);
            }
        },
        chooseOrgFn(org) {
            // 获取选中机构信息
            let systemType = org.systemType;
            let id = org.org_id;
            let nowId = parseInt(this.$cookie.get('userbelong'));
            let nowType = parseInt(this.$cookie.get('userSystemType'));
            if (nowId === id && systemType === nowType) {
                this.$warning({
                    title: '操作提示',
                    content: '自己不能和自己询价',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            this.chooseOrgMd.alert = false;
            this.orgName = org.org_name;
            this.orgSystemType = org.systemType;
            this.formData = {
                ...this.formData,
                ...org,
                partB: org.org_id,
                partB_name: org.org_name
            };
            this.part_b_obj.id = org.org_id;
            this.part_b_obj.name = org.org_name;
            this.part_b_obj.modalType = 6;
            this.part_b_obj.billsType = org.billsType;
        },
        openAddPro(e) {
            // 打开选择产品
            if (this.preview) return;
            const filterDom = ['span', 'svg', 'path', 'input', 'i', 'li', 'ul'],
                flag = filterDom.some((item) => item === e.target.tagName.toLowerCase()),
                childrenFlag = filterDom.some(
                    (item) => item === e.target.children && e.target.children.tagName.toLowerCase()
                ),
                childrenFlagRemove = e.target.getElementsByClassName('action-btn-list-wrap').length;
            if (flag || childrenFlag || childrenFlagRemove) return;
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn('add_product_edit');
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        dblClickCell(row, e) {
            // 阻止冒泡打开弹窗
            if (row.columnIndex === 4 || row.columnIndex === 5) {
                e.stopPropagation();
            }
        },
        addProductFn() {
            // 添加商品
            this.$refs.chooseProMd.returnProList();
        },
        getProReturn(dea, ven) {
            // 获取回调商品
            this.addProductMd.alert = false;
            let arr = ven.concat(dea);
            arr.forEach((item, idx) => {
                if (item.product_type === 0) {
                    item.sys_type = item.dealer_name;
                } else if (item.product_type === 1) {
                    item.sys_type = item.vendor_name;
                }
                item.isActive = '';
                item.unit_inner_sn = '';
                item.quantity = 1;
                item.key = Math.random();
                item.discount = 1;
                item.rate = 0;
                item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额
                item.discount_unit = parseFloat(item.unit_price) * parseFloat(item.discount); // 折后单价
                item.discount_price = parseFloat(item.discount_unit) * parseFloat(item.quantity); // 折后金额
                item.rate_unit =
                    parseFloat(item.discount_unit) * (parseFloat(item.rate) / 100) + parseFloat(item.discount_unit);
                item.rate_price =
                    parseFloat(item.discount_price) * (parseFloat(item.rate) / 100) + parseFloat(item.discount_price);
                item.rate_num = parseFloat(item.discount_price) * (parseFloat(item.rate) / 100);
                item.idx = idx + 1;
                item.isNew = true;
                this.venUnitTreeFn(item, 'add');
            });
        },
        chooseTrEvent(record) {
            // 选中事件
            this.chooseTr(record.key);
        },
        chooseTr(id) {
            // 选中行渲染
            this.isChooseTr = true;

            let data = [];
            this.proTableData.forEach((item) => {
                if (item.key === id) {
                    item.isActive = 'active-tr';
                } else {
                    item.isActive = '';
                }
                data.push(item);
            });
            this.proTableData = data;
        },
        cancelFn() {
            this.$emit('cancel');
        },
        printFn() {
            let printContent = {
                printData: this.proTableData,
                printForm: this.formData,
                printTable: this.columns,
                printType: 4, // 1、图片；2、耗材申领；3、出入库单; 4、询价单
                pageType: 0, // 0 询价 1回复
                orgName: this.orgName,
                printTitle: '询价单',
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
                title: '询价单',
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
                            label: '单位名称',
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
                this.proTableData.map((item) => ({
                    ...item,
                    rate: `${item.rate}%`,
                    discount_price: item.discount_price ? item.discount_price.toFixed(2) : '0',
                    discount_unit: item.discount_unit ? item.discount_unit.toFixed(2) : '0',
                    unit_price: item.unit_price ? item.unit_price.toFixed(4) : '0',
                    rate_price: item.rate_price ? item.rate_price.toFixed(2) : '0',
                    rate_unit: item.rate_unit ? item.rate_unit : '0',
                    sum: item.sum ? item.sum.toFixed(2) : '0',
                    rate_num: item.rate_num ? item.rate_num.toFixed(2) : '0'
                })),
                [],
                formData
            );
        },
        async venUnitTreeFn(record, type) {
            // 厂商商产品单位
            let oid = record.vendor_inner_sn;
            let pid = record.product_inner_sn;
            let psid = record.specification_inner_sn;
            await platformUnitTree(oid, pid, psid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (type === 'add') {
                        this.proTableData.push(record);
                        this.$refs['xTable'] && this.$refs['xTable'].refreshColumn();
                    }
                    let circle = (item, parentName) => {
                        // 递归遍历
                        item.forEach((item) => {
                            item.label = `${item.name}(${item.measure}×${parentName})`;
                            item.value = item.unit_inner_sn;
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item.name);
                            } else {
                                item.children = null;
                            }
                        });
                    };
                    res.tree.forEach((item) => {
                        item.label = item.name;
                        item.value = item.unit_inner_sn;
                        if (item.children && item.children.length > 0) {
                            circle(item.children, item.name);
                        } else {
                            item.children = null;
                        }
                    });
                    let unitArr;
                    unitArr = {
                        key: [res.tree[0].value],
                        data: res.tree
                    };
                    this.proTableData.forEach((item) => {
                        if (item.key === record.key) {
                            item.unit_data = unitArr;
                            if (type === 'add') {
                                item.unit_inner_sn = unitArr.key[0];
                                item.unit_name = unitArr.data[0].name;
                            }
                        }
                    });
                    if (type === 'edit') {
                        this.getUnitParent(record);
                    }
                }
            });
        },
        getUnitParent(unitObj) {
            let oid = unitObj.vendor_inner_sn;
            let pid = unitObj.product_inner_sn;
            let psid = unitObj.specification_inner_sn;
            let pupid = unitObj.unit_inner_sn;
            platformUnitParents(oid, pid, psid, pupid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let def = [];
                    let circle = (item) => {
                        // 递归遍历
                        def.unshift(item.unit_inner_sn);
                        if (item.parent) {
                            circle(item.parent);
                        }
                    };
                    def.unshift(res.info.unit_inner_sn);
                    if (res.info.parent) {
                        circle(res.info.parent);
                    }
                    let tabData = JSON.parse(JSON.stringify(this.proTableData));
                    tabData.forEach((item) => {
                        if (item.key === unitObj.key) {
                            item.unit_data.key = def;
                        }
                    });
                    this.proTableData = tabData;
                }
            });
        },
        async inquiryAddFn(type) {
            // 新增询价单据 type 0:草稿 1:询价
            let newObj = this.formData;
            let oldObj = this.oldFormData;
            let dataObj = {};
            for (let server in newObj) {
                let serverItem = newObj[server];
                for (let old in oldObj) {
                    let oldItem = oldObj[old];
                    if (server === old && serverItem !== oldItem) {
                        dataObj = {
                            ...dataObj,
                            [server]: serverItem, // 新值
                            [server + '_old']: oldItem // 旧值
                        };
                    }
                }
            }
            // if (dataObj.ilps_sn) {
            //     dataObj.ilps_sn = this.$Utils.trim(dataObj.ilps_sn)
            //     if (dataObj.ilps_sn === '' || !dataObj.ilps_sn) {
            //         this.$warning({
            //             title: '操作提示',
            //             content: '请输入单据编号',
            //             okText: '知道了',
            //             centered: true,
            //             maskClosable: true
            //         })
            //         return false
            //     }
            // }
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
                    if (item.specification === nowItem.specification && item.key !== nowItem.key) {
                        if (
                            item.unit_inner_sn === nowItem.unit_inner_sn &&
                            item.name === nowItem.name &&
                            item.vendor_name === nowItem.vendor_name
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
            });
            if (!proSuc) {
                return false;
            }
            dataObj.status = type;
            // 根据不同发起机构判断
            dataObj.partA = newObj.partA;
            dataObj.partB = newObj.partB;
            dataObj.ilps_inner_sn = oldObj.ilps_inner_sn;
            dataObj.detail_inner_sn = this.$cookie.get('userbelong');
            dataObj.billsType = newObj.billsType || oldObj.billsType;
            dataObj.billsType_old = oldObj.billsType;
            dataObj.detailList.forEach((item) => {
                item.rate = item.rate / 100;
                item.product_name = item.name; // 产品名称
                item.unit = item.unit_name; // 包装单位名称
                /* 传递给后端的字段取值变为 【单价 = 含税单价   含税单价 = 单价】 */
                // let unitPrice = item.unit_price.toFixed(4)
                item.unit_price = item.rate_unit;
                // item.rate_unit = unitPrice
                item.sum = item.sum.toFixed(2);
                item.rate_num = item.rate_num.toFixed(2);
            });

            await inquiryEditAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let data = res.info || {};
                    this.$emit('done', data);
                } else {
                    this.$message.error(res.msg);
                }
            });
        }
    },
    mounted() {
        this.refreshPage();
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
        bottom: 70px;
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
.search-box {
    margin-top: 15px;
    margin-bottom: 15px;
    padding: 0 22px;
}
</style>
