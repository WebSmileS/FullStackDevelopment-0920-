<template>
    <div class="scoped">
        <ul class="breadcrumb">
            <li><span class="turquoise">询价单</span></li>
            <li>/</li>
            <li>新增</li>
        </ul>
        <div class="tab-table-wrap no_left" style="top: 20px; position: relative">
            <div class="search-box" style="margin: 0px">
                <div class="search-item">
                    <span class="label" style="min-width: 130px">厂商/经销商</span>
                    <div class="input_box" style="margin-right: 30px">
                        <a-input
                            type="text"
                            readonly="readonly"
                            v-model="orgName"
                            @click="openChooseTb()"
                            placeholder=""
                        />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label" style="min-width: 130px">别名</span>
                    <div class="input_box input" style="margin-right: 30px">
                        <a-input type="text" v-model="formData.contacts" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label">统一社会信用代码</span>
                    <div class="input_box" style="margin-right: 10px">
                        <a-input type="text" v-model="formData.phone" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label">机构类型</span>
                    <div class="input_box" style="margin-right: 10px">
                        <a-input type="text" v-model="formData.phone" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label">联系人</span>
                    <div class="input_box" style="margin-right: 10px">
                        <a-input type="text" v-model="formData.phone" placeholder="" />
                    </div>
                </div>
                <div class="search-item">
                    <span class="label">电话</span>
                    <div class="input_box" style="margin-right: 10px">
                        <a-input type="text" v-model="formData.phone" placeholder="" />
                    </div>
                </div>
                <div @dblclick="openAddPro">
                    <vxe-grid
                        ref="xTable"
                        size="small"
                        :columns="columns"
                        min-height="100"
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
                    <div style="width: 100%; height: 20px">
                        <a-tooltip placement="topLeft" title="新增产品">
                            <a-button @click="openAddPro" style="height: 24px; padding: 0 4px; float: right"
                                >添加产品</a-button
                            >
                        </a-tooltip>
                    </div>
                </div>
                <div class="search-item digest">
                    <span class="label" style="position: absolute; left: 0">摘要</span>
                    <div class="input_box" style="position: absolute; left: 30px">
                        <a-input style="width: 700px" type="text" v-model="formData.description" placeholder="" />
                    </div>
                </div>
                <div class="button_cont">
                    <a-button @click="printFn()" type="primary" style="float: left; left: 24px; top: 13px"
                        >打印</a-button
                    >
                    <a-button @click="exportExcel()" type="primary" style="float: left; left: 36px; top: 13px"
                        >导出Excel</a-button
                    >
                    <a-button @click="inquiryAddFn(1)" type="primary" style="float: right; right: 22px; top: 13px"
                        >询价</a-button
                    >
                    <!-- <a-button @click="inquiryAddFn(0)" type="primary" style="float: right;right: 34px;top:13px">存草稿</a-button> -->
                    <a-button @click="cancelFn()" style="float: right; right: 44px; top: 13px">取消</a-button>
                </div>
            </div>
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
                class="aaaaaaaa"
                ref="chooseProMd"
                @returnData="getProReturn"
                :partB="part_b_obj"
                :metaTitle="addProductMd.title"
            ></add-product>
        </a-modal>
    </div>
</template>

<script>
import { platformUnitTree, inquiryAddAPI } from '@/service/pageAjax';

import chooseOrgTab from '@/components/chooseOrgTab';
import addProduct from '@/components/addProduct';
import { BillsType } from '@/config/constants';

export default {
    name: 'createInquiry',
    components: {
        chooseOrgTab,
        addProduct
    },
    data() {
        return {
            createMd: {
                title: `询价 - 新增`,
                alert: false,
                loading: false
            },
            chooseOrgMd: {
                title: `询价 - 新增 - 选择机构`,
                alert: false,
                loading: false
            },
            addProductMd: {
                title: `询价 - 新增 - 添加产品`,
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
            tableLoading: false,
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
                                    let newData = this.proTableData;
                                    this.proTableData = [];
                                    this.proTableData = newData;
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
                                <a-select v-model={record.rate} style="width:68px; margin-right: 4px" onChange={change}>
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
                    minWidth: 60,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            let delFn = () => {
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
        refreshPage() {
            // 初始化
            const oid = parseInt(this.$cookie.get('userbelong'));
            this.formData = {
                partA: oid, // 询价机构ID
                partB: '', // 被询价机构ID
                ilps_sn: '', // 询价单编号
                contacts: '', // 联系人
                phone: '', // 电话
                detailList: '',
                description: '' // 摘要
            };
            this.part_b_obj.systemType = '';
            this.part_b_obj.modalType = 6;
            this.part_b_obj.id = this.formData.partB;
            this.orgName = '';
            this.orgSystemType = '';
            this.proTableData = [];
        },
        countFn() {
            // 自动计算
            let data = JSON.parse(JSON.stringify(this.proTableData));
            data.forEach((item) => {
                // item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price) // 金额
                // item.discount_unit = parseFloat(item.unit_price) * parseFloat(item.discount) // 折后单价
                // item.discount_price = parseFloat(item.discount_unit) * parseFloat(item.quantity) // 折后金额
                // item.rate_unit = parseFloat(item.discount_unit) * (parseFloat(item.rate) / 100) + parseFloat(item.discount_unit)
                // item.rate_price = parseFloat(item.discount_price) * (parseFloat(item.rate) / 100) + parseFloat(item.discount_price)
                // item.rate_num = parseFloat(item.discount_price) * (parseFloat(item.rate) / 100)

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
                    }
                });
            } else {
                _this.chooseOrgFn(org);
            }
        },
        async chooseOrgFn(org) {
            // 获取选中机构信息
            let systemType = org.systemType;
            this.part_b_obj.systemType = systemType;
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
            this.formData.partB = org.org_id;
            this.part_b_obj.id = org.org_id;
            this.part_b_obj.name = org.org_name;
        },
        openAddPro(e) {
            // 打开选择产品
            const filterDom = ['span', 'svg', 'path', 'input', 'i', 'li', 'ul'],
                flag = filterDom.some((item) => item === e.target.tagName.toLowerCase()),
                childrenFlag = filterDom.some(
                    (item) => item === e.target.children && e.target.children.tagName.toLowerCase()
                ),
                childrenFlagRemove = e.target.getElementsByClassName('action-btn-list-wrap').length;
            if (flag || childrenFlag || childrenFlagRemove) return;

            if (this.part_b_obj.id === '') {
                this.$warning({
                    title: '操作提示',
                    content: '请先选择单位名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn('add_product');
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
            //
            console.log(ven);
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
                item.discount = 1;
                // item.rate = 0
                // item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price) // 金额
                // item.discount_unit = parseFloat(item.unit_price) * parseFloat(item.discount) // 折后单价
                // item.discount_price = parseFloat(item.discount_unit) * parseFloat(item.quantity) // 折后金额
                // item.rate_unit = parseFloat(item.discount_unit) * (parseFloat(item.rate) / 100) + parseFloat(item.discount_unit)
                // item.rate_price = parseFloat(item.discount_price) * (parseFloat(item.rate) / 100) + parseFloat(item.discount_price)
                // item.rate_num = parseFloat(item.discount_price) * (parseFloat(item.rate) / 100)
                item.rate = 0;
                item.rate_unit = item.unit_price; // 含税单价 = 单价 【默认值】
                item.unit_price = parseFloat(item.rate_unit) / (1 + parseFloat(item.rate) / 100); // 单价 = 含税单价 / (1 + 税率 / 100)
                // item.discount_price = parseFloat(item.discount_unit) * parseFloat(item.quantity) // 折后金额 =
                item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额 = 数量 * 单价
                // item.discount_unit = parseFloat(item.unit_price) * parseFloat(item.discount) // 折后单价 = 单价 * 折扣
                item.rate_price = parseFloat(item.quantity) * parseFloat(item.rate_unit); // 含税金额 = 数量 * 含税单价
                item.rate_num = parseFloat(item.rate_price) - parseFloat(item.sum); // 税额 = 含税金额 - 金额
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
            console.log(printContent);
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
            console.log(this.columns);
            this.$Utils.exportExcel(
                this.columns,
                this.proTableData.map((item) => ({
                    ...item,
                    rate: `${item.rate}%`,
                    discount_price: item.discount_price ? item.discount_price.toFixed(2) : '0',
                    discount_unit: item.discount_unit ? item.discount_unit.toFixed(2) : '0',
                    unit_price: item.unit_price ? item.unit_price.toFixed(2) : '0',
                    rate_price: item.rate_price ? item.rate_price.toFixed(2) : '0',
                    rate_unit: item.rate_unit ? item.rate_unit.toFixed(2) : '0',
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
                }
            });
        },
        async inquiryAddFn(type) {
            // 新增询价单据 type 0:草稿 1:询价
            console.log('FORMDATA', this.formData);
            console.log('PROTABLEDATA', this.proTableData);
            let dataObj = this.formData;
            // dataObj.ilps_sn = this.$Utils.trim(dataObj.ilps_sn)
            // if (dataObj.ilps_sn === '' || !dataObj.ilps_sn) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '请输入单据编号',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: true
            //     })
            //     return false
            // }
            if (dataObj.partB === '' || !dataObj.partB) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择单位名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            // 添加合同产品验证
            dataObj.detailList = JSON.parse(JSON.stringify(this.proTableData));
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
                item.sum = item.sum.toFixed(2);
                item.rate_num = item.rate_num.toFixed(2);
            });
            console.log(dataObj);
            await inquiryAddAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let obj = res.info;
                    obj.isActive = '';
                    obj.key = obj.billsType + '' + obj.ilps_inner_sn + '' + obj.partA + '' + obj.partB;
                    obj.sys_type = partB;
                    console.log('adfadfafd', obj);
                    this.$emit('done', obj);
                }
            });
        }
    },
    created() {
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
    width: 100%;
    bottom: 25px;
}
.search-item {
    width: 50%;
    margin-top: 5px;
    margin-bottom: 5px;
    display: inline-block;
    vertical-align: top;
    &.num {
        left: 0px;
        top: 76px;
    }
    &.digest {
        text-align: unset;
        left: 24px;
        bottom: 70px;
    }
    .ant-cascader-input.ant-input {
        height: 24px !important;
    }
}
.label {
    display: inline-block;
    min-width: 112px;
    text-align: right;
    margin-right: 10px;
    text-align: center;
    .em {
        line-height: 14px;
        font-size: 21px;
        vertical-align: middle;
        padding-right: 4px;
    }
}
.input_box {
    display: inline-block;
    width: 70%;
    .ant-input {
        line-height: 24px;
        height: 24px;
    }
}
.search-box {
    margin-top: 15px;
    margin-bottom: 15px;
}
.turquoise {
    color: #1890ff;
}

.breadcrumb {
    list-style: none;
    padding: 0;
    display: flex;
    margin: 0;
}

.breadcrumb li {
    margin: 0 0.2em;
}
.ant-input-number.input {
    width: 100%;
}
</style>
