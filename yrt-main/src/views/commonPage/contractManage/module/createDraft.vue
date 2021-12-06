<template>
    <div class="modal-tabs-pg">
        <a-tabs :activeKey="activeTab" @change="changeTabsFn" type="card">
            <a-tab-pane v-for="item of tabName" :key="item.key" :tab="item.name">
                <div v-show="item.key == 'basic'" class="modal-form-input modal-list">
                    <ul>
                        <li class="item">
                            <span class="label"><em class="em red">*</em>合同名称</span>
                            <a-input class="input" v-model="formData.name" />
                        </li>
                        <li class="item">
                            <span class="label"><em class="em"></em>合同编号</span>
                            <a-input class="input" v-model="formData.contract_sn" />
                        </li>
                        <li class="item">
                            <div class="item-children">
                                <span class="label"><em class="em red">*</em>甲方</span>
                                <a-input
                                    :disabled="isPartA"
                                    readonly="readonly"
                                    class="input"
                                    @click="openChooseTb"
                                    v-model="part_a_obj.name"
                                />
                            </div>
                            <div class="item-children">
                                <span class="label">电话</span>
                                <a-input disabled="true" class="input" v-model="part_a_obj.phone" />
                            </div>
                        </li>
                        <li class="item">
                            <span class="label">地址</span>
                            <a-input disabled="true" class="input" v-model="part_a_obj.address" />
                        </li>
                        <li class="item">
                            <div class="item-children">
                                <span class="label"><em class="em red">*</em>乙方</span>
                                <a-input
                                    :disabled="isPartB"
                                    readonly="readonly"
                                    class="input"
                                    @click="openChooseTb"
                                    v-model="part_b_obj.name"
                                />
                            </div>
                            <div class="item-children">
                                <span class="label">电话</span>
                                <a-input disabled="true" class="input" v-model="part_b_obj.phone" />
                            </div>
                        </li>
                        <li class="item">
                            <span class="label">地址</span>
                            <a-input disabled="true" class="input" v-model="part_b_obj.address" />
                        </li>
                        <li class="item">
                            <div class="item-children" style="width: 35%">
                                <span class="label"><em class="em red">*</em>回款周期</span>
                                <a-input-number
                                    :min="0"
                                    style="width: 170px"
                                    class="input"
                                    v-model="formData.payment_period"
                                />
                                <span class="tail">天</span>
                            </div>
                            <div class="item-children" style="width: 65%">
                                <span class="label"><em class="em red">*</em>有效期</span>
                                <a-range-picker
                                    :value="validDate"
                                    class="input"
                                    :placeholder="['开始日期', '结束日期']"
                                    format="YYYY-MM-DD"
                                    @change="changeStDate"
                                />
                            </div>
                        </li>
                        <li class="item">
                            <div class="item-children" style="width: 33%">
                                <span class="label"><em class="em red">*</em>销售代表</span>
                                <a-input :disabled="isEditDisabled" class="input" v-model="formData.employee_name" />
                                <!-- <a-tree-select
                                    :disabled="isEditDisabled"
                                    treeNodeFilterProp="title"
                                    searchPlaceholder=""
                                    :showSearch="true"
                                    :treeData="empList"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    treeDefaultExpandAll
                                    v-model="formData.employee_inner_sn"
                                    @change="changeSale"
                                    allowClear
                                    class="input"
                                    style="min-width:170px"
                                > -->
                                <!-- </a-tree-select> -->
                                <!-- <a-select
                                    @change="changeSale"
                                    v-model="formData.employee_inner_sn"
                                >
                                    <a-select-option  class="input" v-for="(item, index) of empList" :value="item.employee_inner_sn" :key="index">
                                        {{item.name}}
                                    </a-select-option>
                                </a-select> -->
                            </div>
                            <div class="item-children" style="width: 65%"> </div>
                        </li>
                        <li class="item" v-if="saleArea">
                            <span class="label">销售区域</span>
                            <a-tree-select
                                treeNodeFilterProp="title"
                                :showSearch="true"
                                style="min-height: 32px"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                :treeData="depList"
                                v-model="departments"
                                multiple
                                treeDefaultExpandAll
                                @change="changeDepartmentsFn"
                                class="input"
                            >
                            </a-tree-select>
                        </li>
                        <li class="item">
                            <span class="label">签订日期</span>
                            <a-date-picker
                                style="width: 182px"
                                :value="signDate"
                                placeholder=""
                                format="YYYY-MM-DD"
                                @change="changeSignDate"
                            />
                        </li>
                    </ul>
                </div>
                <div v-show="item.key == 'main'">
                    <div class="main_box">
                        <a-textarea
                            v-model="formData.content"
                            style="min-height: 462px; max-height: 462px; resize: none"
                        />
                    </div>
                </div>
                <div v-show="item.key == 'product'" class="product_box scoped">
                    <!-- <div class="btn_count"> -->
                    <!-- <a-button class="ant-btn-sm" @click="openAddPro" style="margin-right: 10px" type="primary">添加</a-button> -->
                    <!--<a-button class="ant-btn-sm" @click="delProList" style="margin-right: 10px" type="danger">删除</a-button>-->
                    <!--<a-button class="ant-btn-sm" type="primary">销售冲突分析</a-button>-->
                    <!-- </div> -->
                    <div class="tab-table-wrap">
                        <vxe-grid
                            ref="relatedProductXTable"
                            size="small"
                            :columns="proColumns"
                            height="460"
                            resizable
                            auto-resize
                            border
                            highlight-current-row
                            @cell-click="cellClickFn"
                            @cell-dblclick="cellDblclickFn"
                            align="center"
                            :data="proTableData"
                            :loading="productPage.loading"
                        >
                            <!-- @current-change="currentChangeFn" -->
                        </vxe-grid>
                    </div>
                </div>
                <div v-show="item.key == 'accessory'" class="accessory_box scoped">
                    <div class="upload_box">
                        <a-upload-dragger
                            name="file"
                            :fileList="defaultFileList"
                            :multiple="true"
                            :action="QiniuUrl"
                            :data="upLoadparams"
                            @change="uploadChange"
                        >
                            <p class="ant-upload-drag-icon" style="margin-bottom: 5px">
                                <a-icon type="inbox" />
                            </p>
                            <p class="ant-upload-text">单击或拖动文件到此区域上传</p>
                            <p class="ant-upload-hint">请上传小于40M的附件</p>
                        </a-upload-dragger>
                    </div>
                </div>
            </a-tab-pane>
        </a-tabs>
        <a-button v-if="activeTab == 'main'" type="primary" style="float: left; left: 24px; top: 13px"
            >合同范本</a-button
        >
        <a-modal
            :title="chooseOrgMd.title"
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            @cancel="chooseOrgMd.alert = false"
            class="modal-form-input-scoped global-drag choose_org"
        >
            <div v-globalDrag="{ el: 'choose_org' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <choose-org-tab
                ref="chooseOrgMd"
                @chooseOrg="checkOrgFn"
                :partType="partType"
                :isCertifie="true"
            ></choose-org-tab>
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
        <a-modal
            title="录入"
            :visible="entryStVisible"
            :width="1150"
            :maskClosable="false"
            :centered="true"
            @cancel="entryStVisible = false"
            class="modal-form-input-scoped global-drag entryCreateModal"
        >
            <div v-globalDrag="{ el: 'entryCreateModal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    医疗器械注册证（登记表） - 查看
                </h6>
            </div>
            <entry-gic
                ref="mdrfGicPage"
                :gicMdrfObj="gicMdrfObj"
                @returnOtherCertImgs="getOtherCertImgsForPrint"
            ></entry-gic>
            <template slot="footer">
                <a-button @click="handlePrint">打印</a-button>
                <a-button key="back" @click="entryStVisible = false">
                    <span>关闭</span>
                </a-button>
            </template>
        </a-modal>
    </div>
</template>

<script>
import {
    platformAgenecyBaseInfoAPI,
    orgAllEmpDropdownListAPI,
    orgDeptAllTreeAPI,
    getUploadFileTokenAPI,
    dealerUnitTreeAPI,
    platformUnitTree,
    platformProductMdrfNewestListAPI
} from '@/service/pageAjax';

import chooseOrgTab from '@/components/chooseOrgTab';
import addProduct from '@/components/addProduct';
import entryGic from '@/views/ptm/productManage/module/entryGic';

export default {
    name: 'createDraft',
    props: ['isCloseCreateDraft', 'partType', 'titType'],
    components: {
        chooseOrgTab,
        addProduct,
        entryGic
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            entryStVisible: false,
            upToken: '',
            defaultFileList: [],
            upLoadparams: {},
            userSystemType: '', // 当前机构
            chooseOrgMd: {
                title: `${this.$route.meta.title}  - 选择机构`,
                alert: false,
                loading: false
            },
            addProductMd: {
                title: `${this.$route.meta.title} - ${this.titName} - 相关产品 - 添加`,
                alert: false,
                loading: false
            },
            titName: '',
            departList: [],
            textAreaSet: {
                minRows: 4,
                maxRows: 6
            },
            activeTab: 'basic',
            tabName: [
                {
                    name: '基本信息',
                    key: 'basic'
                },
                {
                    name: '内容',
                    key: 'main'
                },
                {
                    name: '相关产品',
                    key: 'product'
                },
                {
                    name: '附件',
                    key: 'accessory'
                }
            ],
            empList: [],
            departments: [],
            depList: [],
            saleArea: false,
            formData: {
                name: '', // 合同名称
                contract_sn: '', // 合同编号
                partA: '', // 甲方id
                partB: '', // 乙方ID
                payment_period: '', // 回款周期
                begin_date: '',
                end_date: '',
                // employee_inner_sn: '', // 销售代表内部编号
                employee_name: '', // 销售代表名称
                hospital_department_inner_sn: '', // 销售区域
                sign_date: null, // 签订日期
                content: '' // 内容
            },
            validDate: null,
            signDate: null,
            isPartA: true,
            isPartB: false,
            part_a_obj: {},
            part_b_obj: {},
            department_inner_sn: '',
            proColumns: [
                {
                    title: 'NO.',
                    dataIndex: 'sort_number',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '5%',
                    align: 'center',
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
                    title: '名称',
                    dataIndex: 'name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'name',
                    minWidth: '19%',
                    align: 'center',
                    slots: {
                        // default: ({ row }) => {
                        //     return [row.name]
                        // }
                        default: ({ row }) => {
                            let html = [
                                <div>
                                    <a-input
                                        class="input input-name-focus"
                                        disabled={!!row.product_inner_sn}
                                        ref={row._XID + 'name'}
                                        onClick={() => this.cellClickFn(row)}
                                        v-model={row.name}
                                    />
                                </div>
                            ];
                            return html;
                        }
                    }
                },
                {
                    title: '别名',
                    dataIndex: 'a',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'a',
                    minWidth: '7%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'specification',
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification];
                        }
                    }
                },
                {
                    title: '厂商/经销商',
                    dataIndex: 'sys_type',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'sys_type',
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.sys_type];
                        }
                    }
                },
                {
                    title: '单位',
                    dataIndex: 'unit_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'unit_name',
                    minWidth: '8%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.unit_name];
                        }
                        // default: ({ row }) => {
                        //     let record = row
                        //     if (record.unit_data) {
                        //         let val = record.unit_data.key
                        //         let tree = record.unit_data.data
                        //         let displayRender = (rd) => {
                        //             let len = rd.labels.length
                        //             const label = rd.labels[len - 1]
                        //             return label
                        //         }
                        //         let chooseTree = () => {
                        //             if (!record.isLoad) {
                        //                 if (record.product_type === 0) {
                        //                     this.dearUnitTreeFn(record, 'add')
                        //                 } else if (record.product_type === 1) {
                        //                     this.venUnitTreeFn(record, 'add')
                        //                 }
                        //                 record.isLoad = true
                        //             }
                        //         }
                        //         let change = (value, arr) => {
                        //             record.unit_data.key = value
                        //             record.unit_inner_sn = value[value.length - 1]
                        //             record.unit_name = arr[arr.length - 1].name
                        //             record.isNew = false
                        //             let newData = this.proTableData
                        //             this.proTableData = []
                        //             this.proTableData = newData
                        //         }
                        //         return [
                        //             <a-cascader
                        //                 onClick = {chooseTree}
                        //                 allowClear = {false}
                        //                 options = {tree}
                        //                 value = {val}
                        //                 displayRender = {displayRender}
                        //                 expandTrigger = 'hover'
                        //                 placeholder=""
                        //                 onChange = {change}
                        //                 changeOnSelect
                        //             />
                        //         ]
                        //     }
                        // }
                    }
                },
                {
                    title: '含税单价（元）',
                    dataIndex: 'price',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'price',
                    minWidth: '10%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let obj = row;
                            let changeInput = (e) => {
                                if (e.keyCode === 38 || e.keyCode === 40) {
                                    e.preventDefault();
                                }
                                let val = e.target.value;
                                obj.price = val.replace(/[^\d.]/g, ''); // 清除“数字”和“.”以外的字符
                                obj.price = obj.price.replace(/^(\\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3');
                                let newData = this.proTableData;
                                this.proTableData = [];
                                this.proTableData = newData;
                            };
                            let dom;
                            dom = [
                                <a-input
                                    onChange={changeInput}
                                    disabled={!row.product_inner_sn}
                                    v-model={obj.price}
                                    ref={row._XID + 'price'}
                                    min={0}
                                    formatter={(value) => `${value}`.replace(/^(\\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')}
                                    parser={(value) => value.replace(/^(\\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')}
                                    class="input input-price-focus"
                                />
                            ];
                            return dom;
                        }
                    }
                },
                {
                    title: '注册证',
                    dataIndex: 'hasCertImage',
                    filed: 'hasCertImage',
                    align: 'center',
                    minWidth: '8%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [
                                row.hasCertImage || row.mdrf_sn ? (
                                    <a-tag color="#87d068" onClick={() => this.showRegistrationCertificate(row)}>
                                        有
                                    </a-tag>
                                ) : (
                                    <a-tag
                                        color="#f50"
                                        onClick={() => {
                                            this.$warning({
                                                title: '操作提示',
                                                content: '该产品无注册证',
                                                okText: '知道了',
                                                centered: true,
                                                maskClosable: true
                                            });
                                        }}
                                    >
                                        无
                                    </a-tag>
                                )
                            ];
                        }
                    }
                },
                {
                    title: '操作',
                    dataIndex: 'inquirer_name',
                    minWidth: '8%',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row, seq }) => {
                            this.chooseTrObj = row;
                            const delFn = () => {
                                    this.proTableData.splice(seq - 1 < 0 ? 0 : seq - 1, 1);
                                    if (this.proTableData && this.proTableData.length <= 0) {
                                        this.insertFocusRowFn(0);
                                    }
                                },
                                dom = [
                                    <div>
                                        {row.product_type === 1 ? (
                                            <a-tooltip placement="top" title="注册证">
                                                <i
                                                    onClick={() => this.showRegistrationCertificate(row)}
                                                    class="yrt yrt-zhucezheng zhucezheng_ico"
                                                ></i>
                                            </a-tooltip>
                                        ) : (
                                            <i style="display: inline-block; width: 16px; height: 16px;" />
                                        )}
                                        <a-tooltip placement="top" title="删除">
                                            <i onClick={delFn} class="yrt yrt-shanchu-copy cancel del_ico"></i>
                                        </a-tooltip>
                                    </div>
                                ];

                            return !row.product_inner_sn ? [] : dom;
                        }
                    }
                }
            ],
            chooseTrObj: {},
            isChooseTr: false,
            proTableData: [],
            unitList: [],
            productPage: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            uploadList: [], // 附件数据
            gicMdrfObj: {
                // 注册证相关信息
                address: '',
                application_range: null,
                contraindication: null,
                description: null,
                images: [],
                issue_date: null,
                issue_organization: null,
                new: false,
                performance_structure: null,
                product_name: '',
                production_address: null,
                register_sn: null,
                specifications: '',
                standard: null,
                vendor_name: '',
                isOnlyView: true // 是否只查看注册证信息
            },
            otherCertImgs: [],
            checkConstantKey: 'name'
        };
    },
    methods: {
        pageRefresh() {
            // 页面初始化
            this.activeTab = 'basic';
            this.formData = {
                name: '', // 合同名称
                contract_sn: '', // 合同编号
                partA: '', // 甲方id
                partB: '', // 乙方ID
                payment_period: '', // 回款周期
                begin_date: '',
                end_date: '',
                // employee_inner_sn: '', // 销售代表内部编号
                employee_name: '', // 销售代表名称
                hospital_department_inner_sn: '', // 销售区域
                sign_date: null // 签订日期
            };
            this.department_inner_sn = '';
            if (this.titType === 'create') {
                this.addProductMd.title = this.$route.meta.title + ' - 新增 - 相关产品 - 添加';
            } else {
                this.addProductMd.title = this.$route.meta.title + ' - 补录 - 相关产品 - 添加';
            }
            this.saleArea = false;
            this.empList = [];
            this.part_a_obj = {};
            this.part_b_obj = {};
            this.validDate = null;
            this.signDate = null;
            this.departments = [];
            this.proTableData = [];
            this.uploadList = [];
            this.defaultFileList = [];
            this.getSelfOrgInfo();
        },
        returnData() {
            // 保存验证 回调
            let dataObj = this.formData;
            dataObj.name = this.$Utils.trim(dataObj.name);
            if (dataObj.name === '' || !dataObj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入合同名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            dataObj.contract_sn = this.$Utils.trim(dataObj.contract_sn);
            // if (dataObj.contract_sn === '' || !dataObj.contract_sn) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '请输入合同编号',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: true
            //     })
            //     return false
            // }
            if (this.part_a_obj.name === '' || !this.part_a_obj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择甲方',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            dataObj.partA = this.part_a_obj.id;
            if (this.part_b_obj.name === '' || !this.part_b_obj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择乙方',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            dataObj.partB = this.part_b_obj.id;
            if (!dataObj.payment_period && dataObj.payment_period !== 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入回款周期',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (!this.validDate || this.validDate.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择有效期',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (!dataObj.employee_name && dataObj.employee_name !== 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择销售代表',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            // this.empList.forEach(item => {
            //     if (item.employee_name === dataObj.employee_name) {
            //         dataObj.employee_name = item.name
            //     }
            // })

            // if (this.department_inner_sn === '' || !this.department_inner_sn) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '请选择销售区域',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: true
            //     })
            //     return false
            // }
            dataObj.hospital_department_inner_sn = this.department_inner_sn;
            // 根据甲乙双方返回不同的Obj并请求不同接口
            const TypeA = this.part_a_obj.systemType;
            const TypeB = this.part_b_obj.systemType;
            if (TypeA === 1) {
                // 如果医院是甲方
                if (TypeB === 2) {
                    // 医院与经销商
                    dataObj.contract_type = 0;
                }
                if (TypeB === 3) {
                    // 医院与厂商
                    dataObj.contract_type = 1;
                }
            }
            if (TypeA === 2) {
                // 如果经销商是甲方
                if (TypeB === 2) {
                    // 经销商与经销商
                    dataObj.contract_type = 3;
                }
                if (TypeB === 3) {
                    // 经销商与厂商
                    dataObj.contract_type = 2;
                }
            }

            // 添加合同产品验证
            dataObj.productList = JSON.parse(JSON.stringify(this.proTableData));
            /// 过滤掉空项
            dataObj.productList = dataObj.productList.filter((item) => item.product_inner_sn);

            if (!dataObj.productList.length) {
                this.$warning({
                    title: '操作提示',
                    content: `相关产品不能为空`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }

            let compareProductObj = {};
            for (let i = 0; i < dataObj.productList.length; i++) {
                let item = dataObj.productList[i],
                    seq = i + 1,
                    tip = `产品名称：${item.product_name}】`; // —规格型号：${item.specification}

                // if (!item.product_inner_sn) {
                //     dataObj.productList.splice(i, 1)
                //     i--
                //     continue
                // }
                /// 过滤掉空项
                // if (!item.product_inner_sn) continue

                if (!item.unit_inner_sn) {
                    this.$warning({
                        title: '操作提示',
                        content: `【NO.${seq}—${tip}添加的产品单位不能为空`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    compareProductObj = {};
                    break;
                }
                if (!item.price || item.price <= 0) {
                    this.$warning({
                        title: '操作提示',
                        content: `【NO.${seq}—${tip}添加的产品含税单价不能为空或≤0`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    compareProductObj = {};
                    break;
                }
                let key = `${item.product_type}==${item.product_inner_sn}==${item.specification_inner_sn}==${item.vendor_inner_sn}==${item.unit_inner_sn}`;

                if (!compareProductObj[key]) {
                    compareProductObj[key] = seq;
                } else {
                    this.$warning({
                        title: '操作提示',
                        content: `【NO.${compareProductObj[key]}和NO.${seq}—${tip}添加的【产品包装单位：${item.unit_name}】选择不能相同`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    compareProductObj = {};
                    break;
                }
            }
            if (Object.keys(compareProductObj).length <= 0) return;
            // 附件
            if (this.uploadList.length > 0) {
                dataObj.attachmentList = this.uploadList;
            }

            this.$emit('returnData', dataObj);
        },
        changeTabsFn(val) {
            // tab切换
            if (val === 'product') {
                if (this.part_b_obj.name === '' || !this.part_b_obj.name) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择乙方',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
            } else {
                this.clearAllElementFocusFn();
            }
            this.activeTab = val;
            this.relatedProductedFn();
        },
        openChooseTb() {
            // 打开选择甲乙方
            this.chooseOrgMd.alert = true;
            this.$nextTick(() => {
                if (this.$refs.chooseOrgMd) {
                    this.$refs.chooseOrgMd.pageDataInit();
                }
                this.$Utils.globalDragCenterFn('choose_org');
            });
        },
        /** 基本信息**/
        changeDepartmentsFn(value) {
            // 销售区域
            this.department_inner_sn = value.join(',');
        },
        changeStDate(date, dateString) {
            // 有效期
            this.validDate = date;
            this.formData.begin_date = dateString[0];
            this.formData.end_date = dateString[1];
        },
        changeSignDate(date, dateString) {
            // 签订日期
            this.signDate = date;
            this.formData.sign_date = dateString;
        },
        displayRender(obj) {
            let len = obj.labels.length;
            const label = obj.labels[len - (len - 1)];
            return label ? label.split('(')[0] : '';
        },
        async getSelfOrgInfo() {
            // 获取当前机构信息
            let systemType = this.$cookie.get('userSystemType');
            this.userSystemType = systemType;
            let id = this.$cookie.get('userbelong');
            await platformAgenecyBaseInfoAPI(systemType, id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (res.info) {
                        if (this.partType === 1) {
                            this.part_a_obj = res.info;
                            this.part_a_obj.systemType = parseFloat(systemType);
                            this.isPartA = true;
                            this.isPartB = false;
                            this.orgDeptAllTreeAPIFn();
                            if (this.part_a_obj.systemType === 1) {
                                this.saleArea = true;
                            }
                        } else if (this.partType === 2) {
                            this.part_b_obj = res.info;
                            this.part_b_obj.systemType = parseFloat(systemType);
                            this.isPartA = false;
                            this.isPartB = true;
                            this.getSelEmp(this.part_b_obj.systemType, this.part_b_obj.id);
                            // console.log(this.part_b_obj)
                        }
                    }
                }
            });
        },
        checkOrgFn(org) {
            let systemType = org.systemType;
            let id = org.org_id;
            let nowId = parseInt(this.$cookie.get('userbelong'));
            let nowType = parseInt(this.$cookie.get('userSystemType'));
            if (nowId === id && systemType === nowType) {
                this.$warning({
                    title: '操作提示',
                    content: '甲乙双方不能为相同机构',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            const _this = this;
            if (this.part_b_obj.id && org.org_id !== this.part_b_obj.id && this.partType !== 2) {
                _this.$confirm({
                    title: '操作提示',
                    content: '当前选择和已选择乙方不一致，如确认要改变乙方，已选销售代表和相关产品会被清空。',
                    okText: '确认',
                    cancelText: '取消',
                    centered: true,
                    maskClosable: false,
                    mask: false,
                    onOk: () => {
                        _this.chooseOrgFn(org);
                        this.proTableData = [];
                        // this.formData.employee_inner_sn = ''
                        this.formData.employee_name = '';
                    }
                });
            } else {
                _this.chooseOrgFn(org);
            }
        },
        async chooseOrgFn(org) {
            // 获取选中机构信息
            let systemType = org.systemType;
            let id = org.org_id;
            await platformAgenecyBaseInfoAPI(systemType, id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (res.info) {
                        if (this.partType === 1) {
                            this.part_b_obj = res.info;
                            this.part_b_obj.systemType = parseFloat(systemType);
                            this.getSelEmp(this.part_b_obj.systemType, this.part_b_obj.id);
                        } else if (this.partType === 2) {
                            this.part_a_obj = res.info;
                            this.part_a_obj.systemType = parseFloat(systemType);
                            this.orgDeptAllTreeAPIFn();
                            if (this.part_a_obj.systemType === 1) {
                                this.saleArea = true;
                            } else {
                                this.saleArea = false;
                            }
                        }
                        this.chooseOrgMd.alert = false;
                    }
                }
            });
        },
        async getSelEmp(systemType, oid) {
            // 获取当前机构员工
            await orgAllEmpDropdownListAPI(systemType, oid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let empData = res.list.map((item) => {
                        return {
                            ...item,
                            key: item.employee_inner_sn,
                            value: item.employee_inner_sn,
                            label: item.name
                        };
                    });
                    this.empList = empData;
                }
            });
        },
        // 根据机构ID查询【所有部门】
        async orgDeptAllTreeAPIFn() {
            let systemType = this.part_a_obj.systemType;
            let id = this.part_a_obj.id;
            if (id && systemType) {
                await orgDeptAllTreeAPI(systemType, id).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        let circle = (arr) => {
                            // 递归
                            arr.forEach((item) => {
                                item.key = item.department_inner_sn;
                                item.value = item.department_inner_sn;
                                item.title = item.name;
                                if (item.status === 0) {
                                    item.disabled = true;
                                }
                                if (item.children && item.children.length > 0) {
                                    circle(item.children);
                                }
                            });
                        };
                        list.forEach((item) => {
                            item.key = item.department_inner_sn;
                            item.value = item.department_inner_sn;
                            item.title = item.name;
                            if (item.status === 0) {
                                item.disabled = true;
                            }
                            if (item.children && item.children.length > 0) {
                                circle(item.children);
                            }
                        });
                        this.depList = list;
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            }
        },
        /** 基本信息结束**/

        /** 相关产品**/
        cellClickFn(row) {
            // 单击行
            let obj = row.row || row;
            this.isChooseTr = true;
            this.chooseTrObj = obj;
            const xTable = this.$refs.relatedProductXTable;
            if (xTable && xTable[1]) {
                xTable[1].focus();
                xTable[1].clearCurrentRow();
                xTable[1].setCurrentRow(obj);
            }
        },
        cellDblclickFn(row) {
            // 双击行
            let obj = row.row || row;
            if (row.columnIndex === 6) return;
            if (!obj.product_inner_sn) return;
            // 只有厂商产品能双击查看注册证
            if (obj.product_type === 1) {
                this.showRegistrationCertificate(obj);
            } else {
                this.$warning({
                    title: '操作提示',
                    content: '只有厂商产品能够查看注册证',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
            }
        },
        async dearUnitTreeFn(record, type) {
            // 经销商产品单位
            let oid = record.vendor_inner_sn;
            let pid = record.product_inner_sn;
            let psid = record.specification_inner_sn;
            await dealerUnitTreeAPI(oid, pid, psid).then((res) => {
                if (parseFloat(res.code) === 0) {
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
                    const xTable = this.$refs.relatedProductXTable;
                    if (xTable && xTable[1]) {
                        xTable[1].refreshColumn();
                    }
                    let newData = this.proTableData;
                    this.proTableData = [];
                    this.proTableData = newData;
                }
            });
        },
        async venUnitTreeFn(record, type) {
            // 厂商商产品单位
            let oid = record.vendor_inner_sn;
            let pid = record.product_inner_sn;
            let psid = record.specification_inner_sn;
            await platformUnitTree(oid, pid, psid).then((res) => {
                if (parseFloat(res.code) === 0) {
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
                    let newData = this.proTableData;
                    this.proTableData = [];
                    this.proTableData = newData;
                }
            });
        },
        openAddPro() {
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn('add_product');
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
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
            arr.forEach((item) => {
                item.isActive = '';
                item.unit_inner_sn = '';
                item.price = 0;
                item.key = item.key + '' + Math.random();
                item.isNew = true;
                item.isLoad = false;
                item.product_vendor_name = item.vendor_name;
                item.product_vendor_inner_sn = item.vendor_inner_sn;
                item.product_name = item.name;
                item.unit_data = {
                    key: [item.common_use_unit_inner_sn],
                    data: [{ value: item.common_use_unit_inner_sn, label: item.common_use_unit }]
                };
                item.unit_inner_sn = item.common_use_unit_inner_sn;
                item.unit_name = item.common_use_unit;
                if (item.product_type === 0) {
                    item.sys_type = item.vendor_name;
                    // this.dearUnitTreeFn(item, 'add')
                } else if (item.product_type === 1) {
                    item.sys_type = item.vendor_name;
                    // this.venUnitTreeFn(item, 'add')
                }

                // this.removeFocusRowFn()
                let spliceIndex = this.removeFocusRowFn();
                this.proTableData.splice(spliceIndex, 0, item);
                const xTable = this.$refs.relatedProductXTable;
                if (xTable && xTable[1]) {
                    xTable[1].refreshColumn().then(() => {
                        this.$nextTick(() => {
                            const curRow = this.proTableData[spliceIndex];
                            // this.$refs.relatedProductXTable[1].setCurrentRow(curRow)
                            // console.log(this.$refs.relatedProductXTable[1].getCurrentRecord(), this.$refs.relatedProductXTable[1].getTableData())
                            this.setScrollTopFn();
                            xTable[1].clearCurrentRow();
                            this.elementFocusFn(curRow, 'price');
                        });
                    });
                }
            });
        },
        getOtherCertImgsForPrint(imgs) {
            this.otherCertImgs = imgs;
        },
        delProList() {
            // 删除商品
            let key = this.chooseTrObj.key;
            if (!key) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择商品',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            this.proTableData.forEach((item, index) => {
                if (item.key === key) {
                    this.proTableData.splice(index, 1);
                }
            });
        },
        /** 相关产品结束**/

        /** 附件上传**/
        async getUploadTokenAPIFn() {
            // 获取上传 token
            await getUploadFileTokenAPI().then((res) => {
                const { code, data } = res;
                if (parseFloat(code) === 0) {
                    this.upToken = data;
                    this.upLoadparams = {
                        token: this.upToken
                    };
                } else {
                    this.upToken = '';
                    this.$message.error(res.msg);
                }
            });
        },
        uploadChange(info) {
            let size = info.file.size;
            if (size > 41943040) {
                this.$warning({
                    title: '操作提示',
                    content: '请上传小于40M的文件',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            let fileList = [...info.fileList];
            fileList = fileList.slice(-2);
            fileList = fileList.map((file) => {
                file.name = this.$Utils.formatFileNameToLowerCase(file.name);

                if (file.response) {
                    file.url = file.response.url;
                }
                return file;
            });
            this.defaultFileList = fileList;
            if (info.file.status === 'done') {
                // 添加
                const res = info.file.response;
                let key = res.data.key;

                const { file } = info;
                file.name = this.$Utils.formatFileNameToLowerCase(file.name);

                let item = {
                    attachment_name: file.name,
                    attachment_url: key
                };
                this.uploadList.push(item);
            } else if (info.file.status === 'removed') {
                // 删除
                const res = info.file.response;
                let key = res.data.key;
                this.uploadList.forEach((item, index) => {
                    if (item.attachment_url === key) {
                        this.uploadList.splice(index, 1);
                    }
                });
            }
        },
        async showRegistrationCertificate(row) {
            if (!row.product_inner_sn) return;
            const { vendor_inner_sn: vid, product_inner_sn: pid } = row;
            await platformProductMdrfNewestListAPI(vid, pid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (res.info) {
                        this.gicMdrfObj = {
                            ...res.info,
                            vendor_inner_sn: vid,
                            isOnlyView: true
                        };
                        this.$Utils.globalDragCenterFn('entryCreateModal');
                        this.entryStVisible = true;
                        this.$nextTick(() => {
                            if (this.$refs.mdrfGicPage) {
                                this.$refs.mdrfGicPage.refreshPage();
                            }
                        });
                    } else {
                        this.$warning({
                            title: '操作提示',
                            content: '该产品无注册证',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        handlePrint() {
            const otherCertImgs = this.otherCertImgs;
            if (!this.gicMdrfObj.images.length && !otherCertImgs.length) {
                this.$warning({
                    title: '操作提示',
                    content: '暂无注册证可打印',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            const printContent = JSON.stringify({
                printData: this.gicMdrfObj.images.concat(otherCertImgs).map((i) => this.$Utils.getPictureSrc(i.url)),
                printType: 11 // printType  11、多张图片
            });
            window.localStorage.printContent = printContent;
            const routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        relatedProductedFn(key) {
            this.$nextTick(() => {
                if (this.activeTab === 'product') {
                    // this.removeFocusRowFn()
                    if (this.proTableData && this.proTableData.length) {
                        let index = this.proTableData.findIndex((item) => !item.product_inner_sn);
                        if (index <= -1) {
                            // this.insertFocusRowFn()
                        } else {
                            let row = this.proTableData[index];
                            this.elementFocusFn(row, key);
                        }
                    } else {
                        this.insertFocusRowFn();
                    }
                }
            });
        },
        keybordDirectionProductedFn(key = 'name', keyCode) {
            if (this.addProductMd.alert) return;
            if (this.activeTab === 'product') {
                if (this.proTableData && this.proTableData.length) {
                    const xTable = this.$refs.relatedProductXTable;
                    if (xTable && xTable[1]) {
                        let tempRow = xTable[1].getCurrentRecord();
                        if (keyCode === 39 && (!tempRow || !tempRow.product_inner_sn)) return;
                        let index = xTable[1].getRowIndex(tempRow);
                        if (keyCode === 38) {
                            if (index <= 0) return;
                            index -= 1;
                        } else if (keyCode === 40) {
                            if (index < this.proTableData.length - 1) {
                                index += 1;
                            }
                        } else if (keyCode === 13) {
                            if (this.proTableData.findIndex((item) => !item.product_inner_sn) <= -1) {
                                this.insertFocusRowFn(index, keyCode);
                            }
                        }

                        let row = this.proTableData[index];
                        if (row) {
                            if (keyCode === 38 || keyCode === 40) {
                                key = 'price';
                            }
                            if (!row.product_inner_sn) {
                                key = 'name';
                            }
                            this.elementFocusFn(row, key);
                        }
                    }
                } else {
                    this.insertFocusRowFn(0, keyCode);
                }
            }
        },
        removeFocusRowFn() {
            let spliceIndex = this.proTableData.findIndex((ftem) => !ftem.product_inner_sn);
            if (spliceIndex > -1) {
                this.proTableData.splice(spliceIndex, 1);
                return spliceIndex;
            }
            return 0;
        },
        setScrollTopFn() {
            const xTable = this.$refs.relatedProductXTable;
            if (xTable && xTable[1]) {
                const row = xTable[1].getCurrentRecord(),
                    rowHeight = 38;
                xTable[1].scrollTo(0, xTable[1].getRowIndex(row) * rowHeight);
            }
        },
        insertFocusRowFn(index = 0, keyCode) {
            const tempFocusRowData = {
                'id': null,
                'vendor_inner_sn': '',
                'vendor_name': '',
                'product_inner_sn': '',
                'product_name': '',
                'mdrf_sn': '',
                'specification_inner_sn': '',
                'specification': '',
                'specification_vendor_sn': '',
                'common_use_unit_inner_sn': '',
                'common_use_unit': '',
                'product_type': '',
                'unit_inner_sn': '',
                'unit_name': '',
                'unitList': [
                    // {
                    //     'label': '盒',
                    //     'value': 1
                    // }
                ],
                'name': '',
                'isActive': '',
                'key': '',
                'keyFormat': '',
                'isLoadingUnit': false,
                'row_add_flag': true,
                'temp_add_flag': true,
                'batch_number': '',
                'price': 0,
                'isNew': true,
                'isLoad': false,
                'product_vendor_name': '',
                'product_vendor_inner_sn': 1,
                'unit_data': {
                    'key': [],
                    'data': [
                        // {
                        //     'value': 1,
                        //     'label: '盒
                        // }
                    ]
                },
                'sys_type': ''
            };

            this.proTableData.splice(index, 0, tempFocusRowData);
            const xTable = this.$refs.relatedProductXTable;
            if (xTable && xTable[1]) {
                xTable[1].refreshColumn();
                this.setScrollTopFn();
                xTable[1].setCurrentRow(this.proTableData[index]).then(() => {
                    this.keybordDirectionProductedFn('name', keyCode);
                });
            }
        },
        clearAllElementFocusFn() {
            const antModalBody = document.querySelector('.ant-modal-body');
            if (antModalBody) {
                antModalBody.scroll(0, 0);
            } else {
                window.scroll(0, 0);
            }
            if (this.$refs && Object.keys(this.$refs).length) {
                for (let key in this.$refs) {
                    const refKey = this.$refs[key];
                    if (refKey && refKey.blur) {
                        refKey.blur();
                        refKey.autoFocus = false;
                    }
                }
            }
        },
        currentChangeFn({ row }) {
            this.elementFocusFn(row, !row.product_inner_sn ? 'name' : 'price');
        },
        elementFocusFn(row, key = 'name') {
            // ref => row._XID + 'name' || row._XID + 'price'
            let ref = row._XID + key;
            this.checkConstantKey = key;
            this.clearAllElementFocusFn();
            if (this.$refs[ref]) {
                this.cellClickFn(row);
                this.$refs[ref].focus();
                // if (key === 'name') {
                //     document.querySelector('.input-name-focus').focus()
                // } else {
                //     document.querySelector('.input-price-focus').focus()
                // }
                this.$refs[ref].autoFocus = true;
                // console.log(key, ref, this.$refs[ref], row)
            }
        },
        eventListenerFn(e) {
            switch (e.keyCode) {
                // case 9: // Tab
                //     break
                // Enter
                case 13: {
                    let findIndex = this.proTableData.findIndex((item) => !item.product_inner_sn);
                    if (findIndex <= -1) {
                        this.keybordDirectionProductedFn('name', e.keyCode, e);
                    } else {
                        if (!this.addProductMd.alert) {
                            this.part_b_obj['isKeyCode'] = true;
                            this.part_b_obj['keyCodeVal'] =
                                findIndex <= -1 ? e.target.value : this.proTableData[findIndex].name;
                            // this.part_b_obj['keyCodeVal'] = e.target.value
                            this.openAddPro();
                        } else {
                            this.addProductFn();
                        }
                    }
                    break;
                }
                case 38: // ↑
                case 40: // ↓
                    this.keybordDirectionProductedFn('name', e.keyCode, e);
                    break;
                case 37: // ←
                case 39: // →
                    // this.keybordDirectionProductedFn(this.checkConstantKey === 'price' ? 'name' : 'price', e.keyCode, e)
                    this.keybordDirectionProductedFn('price', e.keyCode, e);
                    break;
            }
        },
        documentAddEventListenerFn() {
            document.addEventListener('keyup', this.eventListenerFn);
        },
        documentRemoveEventListenerFn() {
            document.removeEventListener('keyup', this.eventListenerFn);
        }
    },
    created() {
        this.getUploadTokenAPIFn();
        this.getSelfOrgInfo();
        this.pageRefresh();
    },
    mounted() {
        if (this.isCloseCreateDraft) {
            this.documentAddEventListenerFn();
        }
    },
    watch: {
        isCloseCreateDraft: {
            handler(val) {
                if (!val) {
                    this.documentRemoveEventListenerFn();
                } else {
                    this.documentAddEventListenerFn();
                }
            },
            deep: true
        }
    },
    beforeDestroy() {
        this.documentRemoveEventListenerFn();
    }
};
</script>

<style scoped lang="less">
.product_box {
    height: 480px;
}

.del_ico {
    color: red;
    font-size: 16px;
    padding-left: 14px;
}

.zhucezheng_ico {
    padding-right: 0 !important;
    font-size: 16px !important;
}

.accessory_box {
    height: 480px;
    padding: 0 16px;
    .upload_box {
        height: 155px;
    }
}
.btn_count {
    height: 40px;
    padding-left: 12px;
    .ant-btn {
        min-width: 76px;
    }
}
.tail {
    margin-left: 4px;
}
.modal-list li.item {
    margin-bottom: 0px !important;
}
.main_box {
    height: 480px;
    padding: 0px 24px;
}
.modal-form-input-scoped {
    .modal-form-input {
        padding-top: 0px !important;
        padding-right: 24px;
        padding-bottom: 24px;
        li.item {
            .item-children:first-child {
                width: 70%;
            }
            .item-children:last-child {
                .label {
                    width: 80px;
                }
            }
        }
        .label {
            width: 100px;
        }
        .ant-select {
            width: 173px;
        }
        .ant-calendar-picker-input {
            height: 24px !important;
        }
    }
}
.modal-tabs-pg {
    margin: -24px;
    .info-count {
        padding: 0px 24px 14px 24px;
    }
}
</style>
