<template>
    <div class="modal-list">
        <a-form>
            <a-form-item v-bind="formItemLayout" label="厂商" class="must-issue" has-feedback>
                <a-input
                    type="text"
                    readonly="readonly"
                    :disabled="disType"
                    v-model="orgName"
                    @click="openChooseTb()"
                    placeholder=""
                />
                <div class="add-pro" @click="addNewBtnFn">新增</div>
                <!--<a-select-->
                <!--@change="changeVendor"-->
                <!--v-model="formData.vendor_inner_sn"-->
                <!--&gt;-->
                <!--<a-select-option v-for="(item, index) of vendorList" :value="item.id" :key="index">-->
                <!--{{item.name}}-->
                <!--</a-select-option>-->
                <!--</a-select>-->
            </a-form-item>
            <div style="margin-left: 70px; margin-bottom: 5px">
                <a-radio-group v-model="formData.type" @change="changeRadio">
                    <a-radio :value="0">医疗器械</a-radio>
                    <a-radio :value="1">非医疗器械</a-radio>
                </a-radio-group>
            </div>
            <a-form-item
                label="名称"
                :label-col="formItemLayout.labelCol"
                class="must-issue"
                :validate-status="isSame ? 'error' : ''"
                :help="isSame ? '平台已有相同产品，请修改。' : ''"
                :wrapper-col="formItemLayout.wrapperCol"
            >
                <a-input v-model="goodsName" @change="changeName" />
            </a-form-item>
            <a-row>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half" label="产品分类">
                        <a-cascader
                            v-model="formData.code68_sn"
                            :options="op68List"
                            changeOnSelect
                            :loadData="load68data"
                            placeholder=""
                            :displayRender="displayRender"
                        />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <div style="margin-left: 40px; margin-bottom: 5px; padding-top: 4px">
                        <a-radio-group v-model="formData.code68_type" @change="change68Type">
                            <a-radio :disabled="isNoMedic" :value="1">Ⅰ类</a-radio>
                            <a-radio :disabled="isNoMedic" :value="2">Ⅱ类</a-radio>
                            <a-radio :disabled="isNoMedic" :value="3">Ⅲ类</a-radio>
                        </a-radio-group>
                    </div>
                </a-col>
            </a-row>
            <a-row>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half" label="类型">
                        <a-cascader
                            v-model="formData.type_inner_sn"
                            :options="categoryList"
                            :loadData="loadCategory"
                            placeholder=""
                            :displayRender="displayRender"
                        />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half_2" label="存储条件">
                        <a-select
                            style="padding-top: 6px"
                            v-decorator="[
                                'select',
                                { rules: [{ required: true, message: 'Please select your country!' }] }
                            ]"
                            v-model="formData.storage_condition"
                        >
                            <a-select-option v-for="item of storageCon" :value="item.value" :key="item.value">
                                {{ item.name }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half" label="医保类型">
                        <a-select
                            style="padding-top: 6px"
                            v-decorator="[
                                'select',
                                { rules: [{ required: true, message: 'Please select your country!' }] }
                            ]"
                            v-model="formData.health_care_type"
                        >
                            <a-select-option v-for="item of healthCare" :value="item.value" :key="item.value">
                                {{ item.name }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                </a-col>
                <!-- <a-col :span="12">
                    <a-form-item
                        label="医保编号"
                        v-bind="formItemLayout_half_2"
                    >
                        <a-input v-model="formData.health_care_sn" />
                    </a-form-item>
                </a-col> -->
            </a-row>
            <a-form-item>
                <div class="tab_box" @dblclick.stop="openCreateSpec" style="padding-left: 6px">
                    <vxe-grid
                        ref="specTable"
                        size="small"
                        :columns="spec_columns"
                        :height="240"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        align="center"
                        :data="specData"
                    >
                    </vxe-grid>
                    <div class="todo_box">
                        <a-button class="btn-nom mgr-10" @click="openCreateSpec" type="primary">+ 新增</a-button>
                    </div>
                </div>
            </a-form-item>
            <a-form-item label="备注" v-bind="formItemLayout">
                <a-textarea v-model="formData.description" :autosize="textAreaSet" />
            </a-form-item>
        </a-form>
        <a-modal
            :title="chooseOrgMd.title"
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            @cancel="chooseOrgMd.alert = false"
            class="modal-form-input-scoped global-drag ct_choose_org"
        >
            <div v-globalDrag="{ el: 'ct_choose_org' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <choose-org-tab
                ref="chooseOrgMd"
                @chooseOrg="chooseOrgFn"
                :isCertifie="false"
                :pageTypeName="1"
                :partType="'ven'"
            ></choose-org-tab>
        </a-modal>
        <!-- 机构注册审核信息修改区域开始 -->
        <a-modal
            v-model="AgencyInfo.alert"
            class="modal-form-input-scoped global-drag agency-edit"
            :centered="true"
            :width="648"
            :maskClosable="false"
        >
            <div v-globalDrag="{ el: 'agency-edit' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ AgencyInfo.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="AgencyInfoCancelFn">取消</a-button>
                <a-button key="submit" type="primary" :loading="AgencyInfo.loading" @click="AgencyInfoOkFn">
                    保存 </a-button
                ><!--:disabled="checkEditInfoFormObjComputed"-->
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>名称</span>
                        <a-input v-model="editInfoFormObj.name" class="input" />
                    </li>
                    <li class="item">
                        <div class="item-children item-children-agency-approval">
                            <span class="label"><em class="em red">*</em>统一社会信用代码</span>
                            <a-input v-model="editInfoFormObj.social_credit_code" class="input input-short" />
                        </div>
                        <div class="item-children">
                            <span class="label" style="width: 80px"><em class="em red">*</em>企业类型</span>
                            <a-select
                                :defaultValue="editInfoFormObj.enterprise_type || 0"
                                v-model="editInfoFormObj.enterprise_type"
                                :style="{ 'width': '160px' }"
                            >
                                <a-select-option value="0">非公司企业法人</a-select-option>
                                <a-select-option value="1">有限责任公司</a-select-option>
                                <a-select-option value="2">股份有限责任公司</a-select-option>
                                <a-select-option value="3">个体工商户</a-select-option>
                                <a-select-option value="4">私营独资企业</a-select-option>
                                <a-select-option value="5">私营合伙企业</a-select-option>
                                <a-select-option value="6">事业单位</a-select-option>
                                <a-select-option value="7">外国厂商</a-select-option>
                            </a-select>
                        </div>
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>行政区划</span>
                        <a-cascader
                            :options="provinces"
                            :loadData="provinceLoads"
                            v-model="provinceDefaults"
                            @change="changeProvincesFn"
                            changeOnSelect
                            class="input"
                            placeholder=""
                        /><!--:defaultValue="provinceDefaults"-->
                    </li>
                    <li class="item">
                        <div class="item-children item-children-agency-approval">
                            <span class="label">成立日期</span>
                            <a-date-picker
                                :defaultValue="editInfoFormObj.establish_date"
                                v-model="editInfoFormObj.establish_date"
                                format="YYYY-MM-DD"
                                size="small"
                                class="input"
                                placeholder=""
                            />
                        </div>
                        <div class="item-children">
                            <span class="label label-1" style="width: 80px">注册资本</span>
                            <a-input v-model="editInfoFormObj.registered_capital" class="input" />
                        </div>
                    </li>
                    <li class="item">
                        <div class="item-children item-children-agency-approval">
                            <span class="label"><em class="em red">*</em>法定代表人</span>
                            <a-input v-model="editInfoFormObj.legal_representative" class="input" />
                        </div>
                        <div class="item-children">
                            <span class="label label-1" style="width: 80px">企业负责人</span>
                            <a-input v-model="editInfoFormObj.principal" class="input" />
                        </div>
                    </li>
                    <li class="item">
                        <span class="label">开户银行</span>
                        <a-input v-model="editInfoFormObj.bank_name" class="input" />
                    </li>
                    <li class="item">
                        <span class="label">银行账号</span>
                        <a-input v-model="editInfoFormObj.bank_account" class="input" />
                    </li>
                    <li class="item">
                        <div class="item-children item-children-agency-approval">
                            <span class="label">电话</span>
                            <a-input v-model="editInfoFormObj.phone" class="input" />
                        </div>
                        <div class="item-children">
                            <span class="label label-1" style="width: 40px">邮箱</span>
                            <a-input v-model="editInfoFormObj.email" class="input" />
                        </div>
                    </li>
                    <li class="item">
                        <span class="label"><em class="em"></em>地址</span>
                        <a-input v-model="editInfoFormObj.address" class="input" />
                    </li>
                    <li class="item">
                        <span class="label memo" style="margin-top: -88px"><em class="em"></em>备注</span>
                        <a-textarea
                            v-model="editInfoFormObj.description"
                            :autosize="{ minRows: 4, maxRows: 4 }"
                            class="input"
                        />
                    </li>
                </ul>
            </div>
        </a-modal>

        <a-modal
            :visible="specSt"
            :maskClosable="false"
            :centered="true"
            :width="540"
            okText="保存"
            @ok="saveSpec"
            @cancel="specSt = false"
            class="modal-form-input-scoped global-drag unit_md_in"
        >
            <div v-globalDrag="{ el: 'unit_md_in' }" class="modal-header-wrap">
                <h6 v-if="!isEdit">
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 规格型号 - 新增
                </h6>
                <h6 v-else>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 规格型号 - 修改
                </h6>
            </div>
            <div class="upload-count">
                <li class="">
                    <span class="label" style="margin-right: 10px"><em class="em red">*</em>规格型号</span>
                    <a-input placeholder="" v-model="newSpec" style="width: 80%" class="input" />
                </li>
                <li style="padding-top: 24px">
                    <span class="label" style="margin-right: 10px">产品编码</span>
                    <a-input placeholder="" v-model="newPvd" style="width: 80%" class="input" />
                </li>
                <li style="padding-top: 24px">
                    <span class="label" style="margin-right: 10px"><em class="em red">*</em>包装单位</span>
                    <a-input placeholder="" v-model="newUnit" style="width: 80%" class="input" />
                </li>
            </div>
        </a-modal>
    </div>
</template>

<script>
import moment from 'moment';
import {
    categoryGetParentListAPI,
    categoryGetNextListAPI,
    code68ParentListAPI,
    code68ChildrenListAPI,
    userUserInfoAPI,
    provinceAreaAPI,
    cityAreaAPI,
    countyAreaAPI,
    listAreaAPI,
    platformAddNewVendorAPI,
    productCheckAPI
} from '@/service/pageAjax';

import chooseOrgTab from '@/components/chooseOrgTab';

export default {
    name: 'createGoods',
    props: ['modalData'],
    components: {
        chooseOrgTab
    },
    data() {
        return {
            leftTitle: `${this.$route.meta.title}`,
            isNoMedic: false,
            isSame: false,
            nowSystemType: '',
            nowOid: '',
            formItemLayout: {
                labelCol: { span: 2 },
                wrapperCol: { span: 22 }
            },
            formItemLayout_half: {
                labelCol: { span: 4 },
                wrapperCol: { span: 20 }
            },
            formItemLayout_half_2: {
                labelCol: { span: 5 },
                wrapperCol: { span: 19 }
            },
            formItemLayout_short: {
                labelCol: { span: 2 },
                wrapperCol: { span: 10 }
            },
            textAreaSet: {
                minRows: 4,
                maxRows: 6
            },
            chooseOrgMd: {
                title: `${this.$route.meta.title} - 新增 - 添加厂商`,
                alert: false,
                loading: false
            },
            radioType: true,
            defaultSel: [],
            goodsName: '',
            formData: {
                vendor_inner_sn: '', // 厂商ID
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [], // 医疗器械68分类编号
                storage_condition: 1, // 存储条件
                health_care_type: 0, // 医保类型
                name: '',
                specifications: '',
                health_care_sn: '', // 医保编号
                description: '',
                type: 0,
                code68_type: '',
                specAndMinUnitList: []
            },
            vendorList: [], // 厂商列表
            categoryList: [], // 自定义类型
            op68List: [],
            storageCon: [
                {
                    value: 0,
                    name: '其它'
                },
                {
                    value: 1,
                    name: '常温'
                },
                {
                    value: 2,
                    name: '保温'
                },
                {
                    value: 3,
                    name: '阴凉'
                },
                {
                    value: 4,
                    name: '冷藏'
                },
                {
                    value: 5,
                    name: '冷冻'
                },
                {
                    value: 6,
                    name: '无菌'
                },
                {
                    value: 7,
                    name: '特储'
                }
            ],
            healthCare: [
                {
                    value: 0,
                    name: 'A型'
                },
                {
                    value: 1,
                    name: 'B型'
                },
                {
                    value: 2,
                    name: 'C型'
                }
            ],
            orgName: '',
            orgSystemType: '',
            disType: false,
            AgencyInfo: {
                title: `${this.$route.meta.title} - 新增厂商`,
                alert: false,
                loading: false
            },
            editInfoFormObj: {
                //
                administrative_division_sn: '',
                name: '',
                alias: '',
                registrant: '',
                registrant_phone: '',
                registrant_email: '',
                address: '',
                description: '',
                audit_status: '',
                not_audit_organization_inner_sn: '',
                register_time: '',
                system_type: ''
            },
            provinces: [],
            provinceDefaults: [],
            provinceSearchDefaults: [],
            provinceDefaultsFlag: true, // 是否加载默认数据的标识
            specSt: false,
            specLoading: false,
            newSpec: '',
            newPvd: '',
            newUnit: '',
            specData: [],
            chooseSpecObj: {},
            spec_columns: [
                {
                    title: '产品编码',
                    dataIndex: 'specification_vendor_sn',
                    align: 'center',
                    minWidth: '200',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification_vendor_sn];
                        }
                    }
                },
                {
                    title: '规格型号',
                    dataIndex: 'specifications',
                    align: 'center',
                    minWidth: '200',
                    slots: {
                        default: ({ row }) => {
                            return [row.specifications];
                        }
                    }
                },
                {
                    title: '包装单位',
                    dataIndex: 'minUnit',
                    align: 'center',
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            return [row.minUnit];
                        }
                    }
                },
                {
                    title: '操作',
                    dataIndex: 'inquirer_name',
                    minWidth: '50',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            let delFn = () => {
                                this.specData.forEach((item, idx) => {
                                    if (item.specification_vendor_sn === row.specification_vendor_sn) {
                                        this.specData.splice(idx, 1);
                                    }
                                });
                            };
                            let editFn = () => {
                                this.chooseSpecObj = row;
                                this.openEditSpec();
                            };
                            return [
                                <div>
                                    <a-tooltip placement="top">
                                        <template slot="title">
                                            <span>编辑</span>
                                        </template>
                                        <a-icon onClick={editFn} type="edit" class="edit_unit" />
                                    </a-tooltip>
                                    <a-tooltip placement="top">
                                        <template slot="title">
                                            <span>删除</span>
                                        </template>
                                        <i onClick={delFn} class="yrt yrt-shanchu-copy del_ico"></i>
                                    </a-tooltip>
                                </div>
                            ];
                        }
                    }
                }
            ]
        };
    },
    methods: {
        initPageData() {
            // 初始化页面
            this.formData = {
                vendor_inner_sn: [], // 厂商ID
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [], // 医疗器械68分类编号
                storage_condition: 1, // 存储条件
                health_care_type: 0, // 医保类型
                name: '',
                specifications: '',
                health_care_sn: '', // 医保编号
                description: '',
                type: 0,
                code68_type: ''
            };
            this.goodsName = '';
            this.isSame = false;
            this.specData = [];
            this.isNoMedic = false;
            this.radioType = true;
            this.orgName = '';
            this.nowSystemType = this.$cookie.get('userSystemType');
            this.nowOid = this.$cookie.get('userbelong');
            this.formData.vendor_inner_sn = '';
            // if (parseInt(this.nowSystemType) === 3) {
            //     this.disType = true
            //     this.getNowObj()
            // }
        },
        changeName() {
            this.formData.name = this.goodsName;
        },
        async checkProName() {
            if (this.formData.vendor_inner_sn !== '' && this.formData.name !== '') {
                let params = {
                    check_name: this.formData.name,
                    vendor_inner_sn: this.formData.vendor_inner_sn
                };
                await productCheckAPI({ params }).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.isSame = false;
                    } else {
                        this.isSame = true;
                    }
                });
            }
        },
        returnPageData() {
            if (this.isSame) {
                this.$warning({
                    title: '操作提示',
                    content: '平台已有相同产品，请修改。',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            if (this.formData.type_inner_sn.length === 0) {
                this.formData.type_inner_sn = '';
            }
            if (this.formData.code68_sn.length === 0) {
                this.formData.code68_sn = '';
            }
            if (this.specData.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请新增规格型号/包装单位',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            this.formData.specAndMinUnitList = JSON.stringify(this.specData);
            this.formData.specAndMinUnitList = this.specData;
            this.$emit('getData', this.formData);
        },
        openChooseTb() {
            // 打开选择厂商
            this.chooseOrgMd.alert = true;
            this.$Utils.globalDragCenterFn('ct_choose_org');
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
        },
        async chooseOrgFn(org) {
            // 获取选中机构信息
            this.orgName = org.org_name;
            this.formData.vendor_inner_sn = org.org_id;
            this.chooseOrgMd.alert = false;
            this.checkProName();
        },
        changeRadio(e) {
            let val = e.target.value;
            if (val === 0) {
                this.isNoMedic = false;
                this.radioType = true;
                this.formData.specifications = '';
            } else {
                this.radioType = false;
                this.isNoMedic = true;
                this.formData.code68_type = '';
            }
        },
        change68Type(e) {
            let val = e.target.value;
            this.formData.code68_type = val;
        },
        changeVendor(val) {
            this.formData.vendor_inner_sn = val;
        },
        async getCategoryListFn() {
            // 获取自定义类型列表
            await categoryGetParentListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    this.categoryList = rows.map((item) => {
                        let st;
                        if (item.is_leaf === 1) {
                            st = true;
                        } else if (item.is_leaf === 0) {
                            st = false;
                        }
                        item.label = item.type;
                        item.value = item.type_inner_sn;
                        item.isLeaf = st;
                        return item;
                    });
                    this.categoryList.forEach((item, idx) => {
                        if (item.status === 0) {
                            this.categoryList.splice(idx, 1);
                        }
                    });
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async loadCategory(options) {
            // 加载自定义类型列表子集
            const targetOption = options[options.length - 1];
            targetOption.loading = true;
            const id = targetOption.value;
            await categoryGetNextListAPI(id).then((res) => {
                targetOption.loading = false;
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    rows.forEach((item, idx) => {
                        if (item.status === 0) {
                            rows.splice(idx, 1);
                        }
                    });
                    if (rows.length > 0) {
                        targetOption.children = rows.map((item) => {
                            let st;
                            if (item.is_leaf === 1) {
                                st = true;
                            } else if (item.is_leaf === 0) {
                                st = false;
                            }
                            item.label = item.type;
                            item.value = item.type_inner_sn;
                            item.isLeaf = st;
                            return item;
                        });
                    } else {
                        targetOption.isLeaf = true;
                    }
                    targetOption.loading = false;
                    this.categoryList = [...this.categoryList];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        changeCategory(val) {
            // let index = val.length - 1
            this.formData.type_inner_sn = val;
        },
        async get68ParentListFn() {
            // 获取68分类
            await code68ParentListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.op68List = [];
                    const rows = res.list;
                    let newArray = [];
                    rows.forEach((item) => {
                        let st;
                        if (item.is_leaf === 0) {
                            st = false;
                        } else if (item.is_leaf === 1) {
                            st = true;
                        }
                        let obj = {
                            label: '(' + item.code68_sn + ') ' + item.name,
                            value: item.code68_sn,
                            isLeaf: st
                        };
                        newArray.push(obj);
                    });
                    this.op68List = newArray;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async load68data(options) {
            // 加载68分
            const targetOption = options[options.length - 1];
            targetOption.loading = true;
            const id = targetOption.value;
            await code68ChildrenListAPI(id).then((res) => {
                targetOption.loading = false;
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    if (rows.length > 0) {
                        targetOption.children = rows.map((item) => {
                            let st;
                            if (item.is_leaf === 0) {
                                st = false;
                            } else if (item.is_leaf === 1) {
                                st = true;
                            }
                            item.label = '(' + item.code68_sn + ') ' + item.name;
                            item.value = item.code68_sn;
                            item.isLeaf = st;
                            return item;
                        });
                    } else {
                        targetOption.isLeaf = true;
                    }
                    targetOption.loading = false;
                    this.op68List = [...this.op68List];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        change68(val) {
            let index = val.length - 1;
            this.formData.code68_sn = val[index];
        },
        displayRender(obj) {
            let len = obj.labels.length;
            const label = obj.labels[len - 1];
            return label;
        },
        async getNowObj() {
            await userUserInfoAPI().then((res) => {
                if (parseInt(res.Code || res.code) === 0) {
                    this.orgName = res.data.EmployeeInfo.OrgName;
                }
            });
        },
        moment,
        // 获取全部省份列表
        async provinceAreaAPIFn() {
            await provinceAreaAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const datas = res.province.map((item) => {
                        item.value = item.administrative_division_sn;
                        item.label = item.name;
                        item.isLeaf = item.is_leaf === 1; // is_leaf 0：  1：没有下一级叶子节点
                        return item;
                    });

                    this.provinces = [...datas];

                    if (this.provinceDefaultsFlag && this.provinceDefaults[0]) {
                        this.provinces.forEach((item, index) => {
                            if (parseFloat(item.value) === parseFloat(this.provinceDefaults[0])) {
                                this.provincesObj = {
                                    pIndex: index,
                                    cIndex: -1
                                };
                            }
                        });
                        this.cityAreaAPIFn(this.provinceDefaults[0]);
                    } else if (this.provinceDefaultsFlag && this.provinceSearchDefaults[0]) {
                        this.provinces.forEach((item, index) => {
                            if (parseFloat(item.value) === parseFloat(this.provinceSearchDefaults[0])) {
                                this.provincesObj = {
                                    pIndex: index,
                                    cIndex: -1
                                };
                            }
                        });
                        this.cityAreaAPIFn(this.provinceSearchDefaults[0]);
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据省份id查询所有的下级城市信息
        async cityAreaAPIFn(targetOption) {
            await cityAreaAPI(targetOption.value || targetOption).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const datas = res.city.map((item) => {
                        item.value = item.administrative_division_sn;
                        item.label = item.name;
                        item.isLeaf = item.is_leaf === 1; // is_leaf 0：没有下一级叶子节点   1：有
                        return item;
                    });

                    if (this.provinceDefaultsFlag && this.provinceDefaults[1]) {
                        this.provinces.splice(this.provincesObj.pIndex, 1, {
                            ...this.provinces[this.provincesObj.pIndex],
                            children: [...datas]
                        });
                        this.provinces[this.provincesObj.pIndex].children.forEach((item, index) => {
                            if (parseFloat(item.value) === parseFloat(this.provinceDefaults[1])) {
                                this.provincesObj = {
                                    ...this.provincesObj,
                                    cIndex: index
                                };
                            }
                        });
                        this.countyAreaAPIFn(this.provinceDefaults[1]);
                    } else if (this.provinceDefaultsFlag && this.provinceSearchDefaults[1]) {
                        this.provinces.splice(this.provincesObj.pIndex, 1, {
                            ...this.provinces[this.provincesObj.pIndex],
                            children: [...datas]
                        });
                        this.provinces[this.provincesObj.pIndex].children.forEach((item, index) => {
                            if (parseFloat(item.value) === parseFloat(this.provinceSearchDefaults[1])) {
                                this.provincesObj = {
                                    ...this.provincesObj,
                                    cIndex: index
                                };
                            }
                        });
                        this.countyAreaAPIFn(this.provinceSearchDefaults[1]);
                    } else {
                        if (typeof targetOption === 'object') {
                            targetOption.loading = false;
                            targetOption.children = [...datas];
                        }
                        this.provinces = [...this.provinces];
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据城市id查询所有的下级区域信息
        async countyAreaAPIFn(targetOption) {
            await countyAreaAPI(targetOption.value || targetOption).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const datas = res.county.map((item) => {
                        item.value = item.administrative_division_sn;
                        item.label = item.name;
                        // item.isLeaf = item.is_leaf === 0 // is_leaf 0：没有下一级叶子节点   1：有
                        return item;
                    });

                    if (this.provinceDefaultsFlag && this.provinceDefaults[2]) {
                        this.provinces[this.provincesObj.pIndex].children.splice(this.provincesObj.cIndex, 1, {
                            ...this.provinces[this.provincesObj.pIndex].children[this.provincesObj.cIndex],
                            children: [...datas]
                        });
                    } else if (this.provinceDefaultsFlag && this.provinceSearchDefaults[2]) {
                        this.provinces[this.provincesObj.pIndex].children.splice(this.provincesObj.cIndex, 1, {
                            ...this.provinces[this.provincesObj.pIndex].children[this.provincesObj.cIndex],
                            children: [...datas]
                        });
                    } else {
                        if (typeof targetOption === 'object') {
                            targetOption.loading = false;
                            targetOption.children = [...datas];
                        }
                        this.provinces = [...this.provinces];
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据城市id查询上级区域信息 【查询所有的上级区域信息】
        async listAreaAPIFn(id) {
            await listAreaAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const areaObj = res.area,
                        countySn = areaObj.administrative_division_sn,
                        citySn = areaObj.parent ? areaObj.parent.administrative_division_sn : null,
                        provinceSn =
                            areaObj.parent && areaObj.parent.parent
                                ? areaObj.parent.parent.administrative_division_sn
                                : null;
                    /* const {
                        area: {
                            administrative_division_sn: countySn,
                            parent: {
                                administrative_division_sn: citySn,
                                parent: {
                                    administrative_division_sn: provinceSn
                                }
                            }
                        }
                    } = res */
                    // 检测省市区有没有值
                    if (provinceSn) {
                        this.provinceDefaults.splice(0, 1, provinceSn);
                    }
                    if (citySn) {
                        this.provinceDefaults.splice(1, 1, citySn);
                    }
                    if (countySn) {
                        this.provinceDefaults.splice(2, 1, countySn);
                    }
                    this.provinceAreaAPIFn();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 异步加载省市区数据
        provinceLoads(selectedOptions) {
            this.provinceDefaultsFlag = false;
            const targetOption = selectedOptions[selectedOptions.length - 1];
            targetOption.loading = true;
            if (!targetOption.parent_administrative_division_sn) {
                this.cityAreaAPIFn(targetOption);
            } else {
                this.countyAreaAPIFn(targetOption);
            }
        },
        // 省市区数据选择完成后的回调
        changeProvincesFn(value) {
            this.editInfoFormObj.administrative_division_sn = value[value.length - 1];
        },
        // 修改取消
        AgencyInfoCancelFn() {
            this.AgencyInfo.alert = false;
            this.editInfoFormNewObj = {};
        },
        AgencyInfoOkFn() {
            if (!this.checkEditInfoFormMsgFn()) return; // 前端格式校验
            this.AgencyInfo.loading = true;
            this.platformAddNewVendorAPIFn(this.editInfoFormObj);
        },
        // 检测修改的表单信息校验
        checkEditInfoFormMsgFn() {
            const socialCreditCode = /^(\d|[a-zA-Z])+$/,
                mobilePhone = /^1\d{10}$/,
                email = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
            // let sysType = this.ReturnSysType(this.tabsActiveKey)
            // 名称
            if (!this.editInfoFormObj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            // else {
            //     this.editInfoFormObj.name = this.$Utils.stripSpecialStr(this.editInfoFormObj.name)
            // }
            // 名称 02
            if (!this.$Utils.trim(this.editInfoFormObj.name)) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                this.editInfoFormObj.name = this.$Utils.trim(this.editInfoFormObj.name);
                return false;
            }
            // 名称 03
            if (this.$Utils.checkInputValFn(this.editInfoFormObj.name)) {
                this.$warning({
                    title: '操作提示',
                    content: '名称中请不要输入特殊字符',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return false;
            }
            // else {
            //     this.editInfoFormObj.name = this.$Utils.stripSpecialStr(this.editInfoFormObj.name)
            // }
            // 统一社会信用代码 01
            if (!this.editInfoFormObj.social_credit_code) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入统一社会信用代码',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }

            // 统一社会信用代码 02
            if (!this.$Utils.trim(this.editInfoFormObj.social_credit_code)) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入统一社会信用代码',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                this.editInfoFormObj.social_credit_code = this.$Utils.trim(this.editInfoFormObj.social_credit_code);
                return false;
            }

            // 统一社会信用代码 03
            if (this.editInfoFormObj.social_credit_code.length !== 18) {
                this.$warning({
                    title: '操作提示',
                    content: '统一社会信用代码有效长度是18位',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }

            // 统一社会信用代码 04
            if (!socialCreditCode.test(this.editInfoFormObj.social_credit_code)) {
                this.$warning({
                    title: '操作提示',
                    content: '统一社会信用代码格式为字母和数字组合的18位',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }

            // 行政区域
            if (!this.editInfoFormObj.administrative_division_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择行政区划',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            // 电话
            if (this.editInfoFormObj.phone && this.editInfoFormObj.phone.length > 0) {
                if (!mobilePhone.test(this.editInfoFormObj.phone)) {
                    this.$warning({
                        title: '操作提示',
                        content: '电话格式输入有误',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }
            }
            // 邮箱
            if (this.editInfoFormObj.email && this.editInfoFormObj.email.length > 0) {
                if (!email.test(this.editInfoFormObj.email)) {
                    this.$warning({
                        title: '操作提示',
                        content: '邮箱格式输入有误',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }
            }
            // 企业类型
            if (!this.editInfoFormObj.enterprise_type) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择企业类型',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            // 法定代表人 01
            if (!this.editInfoFormObj.legal_representative) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入法定代表人',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return false;
            }
            // 法定代表人 02
            if (!this.$Utils.trim(this.editInfoFormObj.legal_representative)) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入法定代表人',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                this.editInfoFormObj.legal_representative = this.$Utils.trim(this.editInfoFormObj.legal_representative);
                return false;
            }
            return true;
        },
        // 新增厂商
        async platformAddNewVendorAPIFn(params) {
            if (params.establish_date) {
                params.establish_date = moment(params.establish_date).format('YYYY-MM-DD');
            }
            await platformAddNewVendorAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.editInfoFormNewObj = {};
                    this.AgencyInfo.alert = false;
                    this.orgName = res.info.name;
                    this.formData.vendor_inner_sn = res.info.id;
                    // this.$message.success('添加成功')
                } else {
                    this.$message.error(res.msg);
                }
                this.AgencyInfo.loading = false;
            });
        },
        addNewBtnFn() {
            this.provinceDefaultsFlag = true;
            this.editInfoFormObj = {
                administrative_division_sn: '',
                name: '',
                alias: '',
                registrant: '',
                registrant_phone: '',
                registrant_email: '',
                address: '',
                description: '',
                audit_status: '',
                not_audit_organization_inner_sn: '',
                establish_date: moment[''],
                register_time: '',
                system_type: ''
            };
            this.provinceDefaults = [];
            this.AgencyInfo = {
                title: `${this.$route.meta.title} - 新增厂商`,
                alert: true,
                loading: false
            };
            this.$Utils.globalDragCenterFn('agency-edit');
        },
        // 打开新增规格型号/单位
        openCreateSpec() {
            this.isEdit = false;
            this.newSpec = '';
            this.newPvd = '';
            this.newUnit = '';
            this.specSt = true;
            this.$Utils.globalDragCenterFn('unit_md_in');
        },
        openEditSpec() {
            this.isEdit = true;
            this.newSpec = this.chooseSpecObj.specifications;
            this.newPvd = this.chooseSpecObj.specification_vendor_sn;
            this.newUnit = this.chooseSpecObj.minUnit;
            this.specSt = true;
            this.$Utils.globalDragCenterFn('unit_md_in');
        },
        // 新增规格型号/单位
        saveSpec() {
            let newKey;
            if (this.isEdit) {
                newKey = this.chooseSpecObj.key;
            } else {
                newKey = Math.random();
            }
            this.newSpec = this.$Utils.trim(this.newSpec);
            this.newUnit = this.$Utils.trim(this.newUnit);
            this.newPvd = this.$Utils.trim(this.newPvd);
            if (!this.$Utils.trim(this.newSpec)) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入规格型号',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }

            if (!this.$Utils.trim(this.newUnit)) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入包装单位',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            for (let i = 0; i < this.specData.length; i++) {
                if (
                    this.specData[i].specification_vendor_sn === this.newPvd &&
                    this.newPvd &&
                    newKey !== this.specData[i].key
                ) {
                    this.$warning({
                        title: '操作提示',
                        content: '产品编码不能重复',
                        okText: '知道了',
                        maskClosable: false,
                        centered: true
                    });
                    return;
                }

                if (this.specData[i].specifications === this.newSpec && newKey !== this.specData[i].key) {
                    this.$warning({
                        title: '操作提示',
                        content: '产品规格型号不能重复',
                        okText: '知道了',
                        maskClosable: false,
                        centered: true
                    });
                    return;
                }
            }
            let newObj = {
                key: newKey,
                audit_level: 0,
                minUnit: this.newUnit,
                operation: 1,
                specifications: this.newSpec,
                specification_vendor_sn: this.newPvd,
                status: 2
            };
            if (this.isEdit) {
                this.specData.forEach((item) => {
                    if (item.key === this.chooseSpecObj.key) {
                        item.minUnit = newObj.minUnit;
                        item.specifications = newObj.specifications;
                        item.specification_vendor_sn = newObj.specification_vendor_sn;
                    }
                });
            } else {
                this.specData.push(newObj);
            }
            this.specSt = false;
        }
    },
    mounted() {
        // this.getVendorListFn()
        this.initPageData();
        this.getCategoryListFn();
        this.get68ParentListFn();
        this.provinceAreaAPIFn();
        this.$watch(
            'goodsName',
            this.$Utils.debounce(() => {
                // 延时监听input
                this.checkProName();
            }, 300)
        );
    }
};
</script>

<style scoped lang="less">
.upload-count {
    margin-top: 44px;
    .em {
        font-size: 22px;
        vertical-align: middle;
        margin-right: 3px;
        float: left;
        height: 20px;
        line-height: 27px;
    }
}
.modal-list {
    .ant-form-item {
        margin-bottom: 8px;
    }
}
.del_ico {
    color: red;
    font-size: 16px;
    padding-left: 14px;
}

.edit_unit {
    font-size: 16px;
    color: #1890ff;
}

.todo_box {
    margin-top: 10px;
    text-align: right;
}
.add-pro {
    position: absolute;
    right: 0px;
    top: -2px;
    height: 22px;
    width: 47px;
    line-height: 22px;
    color: #fff;
    background: #1890ff;
    border-radius: 0 3px 3px 0;
    text-align: center;
    cursor: pointer;
}
textarea {
    resize: none;
}
</style>
