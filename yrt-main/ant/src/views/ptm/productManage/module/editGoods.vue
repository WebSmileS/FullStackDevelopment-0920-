<template>
    <div class="modal-list">
        <a-form>
            <a-form-item v-bind="formItemLayout" label="厂商" class="must-issue" has-feedback>
                <a-select
                    style="margin-top: 5px"
                    @change="changeVendor"
                    v-model="formData.vendor_name"
                    :disabled="true"
                >
                    <a-select-option v-for="(item, index) of vendorList" :value="item.id" :key="index">
                        {{ item.name }}
                    </a-select-option>
                </a-select>
            </a-form-item>
            <div style="margin-left: 70px; margin-bottom: 5px">
                <a-radio-group v-model="formData.type" @change="changeRadio">
                    <a-radio :value="0">医疗器械</a-radio>
                    <a-radio :value="1">非医疗器械</a-radio>
                </a-radio-group>
            </div>
            <a-form-item
                v-if="goodsType !== 'deaCt'"
                label="名称"
                :label-col="formItemLayout.labelCol"
                class="must-issue"
                :wrapper-col="formItemLayout.wrapperCol"
            >
                <a-input v-model="formData.name" />
            </a-form-item>
            <a-form-item
                v-else
                label="名称"
                :validate-status="isSame ? 'error' : ''"
                :help="isSame ? '平台已有相同产品，请修改。' : ''"
                :label-col="formItemLayout.labelCol"
                class="must-issue"
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
                            placeholder=""
                            changeOnSelect
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
                            placeholder=""
                            :displayRender="displayRender"
                        />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half_2" label="存储条件">
                        <a-select
                            style="margin-top: 5px"
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
                            style="margin-top: 5px"
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
            <a-row>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half" label="状态" v-if="pageType != 'proCheck'">
                        <a-select
                            style="margin-top: 5px"
                            v-decorator="['select', { rules: [{ required: true, message: '' }] }]"
                            v-model="formData.status"
                        >
                            <a-select-option v-for="item of statusList" :value="item.value" :key="item.value">
                                {{ item.name }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                </a-col>
            </a-row>
            <a-form-item label="规格型号" v-bind="formItemLayout" v-if="pageType != 'proCheck'">
                <a-textarea
                    :disabled="radioType"
                    placeholder="请输入规格型号，多型号请用 , 分隔"
                    v-model="formData.specifications"
                    :autosize="textAreaSet"
                />
            </a-form-item>
            <a-form-item v-if="goodsType == 'deaCt'">
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
            :visible="specSt"
            :maskClosable="false"
            :centered="true"
            :width="540"
            okText="保存"
            @ok="saveSpec"
            @cancel="specSt = false"
            class="modal-form-input-scoped global-drag unit_md_ed"
        >
            <div v-globalDrag="{ el: 'unit_md_ed' }" class="modal-header-wrap">
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
import {
    tmpSpecListAPI,
    getVendorList,
    getCategoryParentsAPI,
    getAllCategoryAPI,
    categoryGetNextListAPI,
    platformAgencyScopeTreeAPI,
    code68ChildrenListAPI,
    getCode68ParentsAPI,
    productCheckAPI
} from '@/service/pageAjax';
import AFormItem from 'ant-design-vue/es/form/FormItem';

export default {
    name: 'editGoods',
    components: { AFormItem },
    props: ['modalData'],
    data() {
        return {
            isNoMedic: false,
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
            radioType: true,
            defaultSel: [],
            oldFormData: {},
            goodsName: '',
            isSame: false,
            formData: {
                vendor_inner_sn: [], // 厂商ID
                type_inner_sn: '', // 自定义分类内部编号
                code68_sn: '', // 医疗器械68分类编号
                storage_condition: 1, // 存储条件
                health_care_type: 0, // 医保类型
                name: '',
                specifications: '',
                health_care_sn: '', // 医保编号
                description: '',
                type: '',
                status: '',
                code68_type: ''
            },
            vendorList: [], // 厂商列表
            categoryList: [], // 自定义类型
            cateSt: 0,
            i68St: 0,
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
            statusList: [
                {
                    value: 0,
                    name: '无效'
                },
                {
                    value: 1,
                    name: '有效'
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
            specSt: false,
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
                    minWidth: '30%',
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
                    minWidth: '40%',
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
                    minWidth: '20%',
                    slots: {
                        default: ({ row }) => {
                            return [row.minUnit];
                        }
                    }
                },
                {
                    title: '操作',
                    dataIndex: 'inquirer_name',
                    minWidth: '10%',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            let delFn = () => {
                                this.specData.forEach((item, index) => {
                                    if (item.specification_inner_sn === row.specification_inner_sn) {
                                        this.specData.splice(index, 1);
                                    }
                                });
                            };
                            let editFn = () => {
                                this.chooseSpecObj = row;
                                this.openEditSpec();
                            };
                            return [
                                <div>
                                    <a-tooltip placement="top" title="编辑">
                                        <a-icon onClick={editFn} type="edit" class="edit_unit" />
                                    </a-tooltip>
                                    <a-tooltip placement="top" title="删除">
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
            if (this.goodsType === 'deaCt') {
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
            }
            this.$emit('getData', this.formData);
        },
        async getVendorListFn() {
            await getVendorList().then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.vendorList = res.list;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        change68Type(e) {
            let val = e.target.value;
            this.formData.code68_type = val;
        },
        changeRadio(e) {
            let val = e.target.value;
            if (val === 0) {
                this.isNoMedic = false;
                this.radioType = true;
                this.formData.specifications = '';
            } else {
                this.isNoMedic = true;
                this.radioType = false;
                this.formData.code68_type = '';
            }
        },
        changeVendor(val) {
            this.formData.vendor_inner_sn = val;
        },
        async getCategoryListFn() {
            // 获取自定义类型列表
            let id = this.modalData.type_inner_sn_old;
            await getAllCategoryAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const tree = res.info;
                    const circle = (arr) => {
                        arr.forEach((item) => {
                            item.label = item.type;
                            item.value = item.type_inner_sn;
                            let st;
                            if (item.is_leaf === 1) {
                                st = true;
                            } else if (item.is_leaf === 0) {
                                st = false;
                            }
                            item.isLeaf = st;
                            if (item.status === 0) {
                                item.disabled = true;
                            }
                            if (item.children && item.children.length > 0) {
                                circle(item.children);
                            }
                        });
                    };
                    tree.forEach((item) => {
                        item.label = item.type;
                        item.value = item.type_inner_sn;
                        let st;
                        if (item.is_leaf === 1) {
                            st = true;
                        } else if (item.is_leaf === 0) {
                            st = false;
                        }
                        item.isLeaf = st;
                        if (item.children && item.children.length > 0) {
                            circle(item.children);
                        }
                        if (item.status === 0) {
                            item.disabled = true;
                        }
                    });
                    let typeInner = this.modalData.type_inner_sn_old;
                    this.categoryList = tree;
                    this.getCategoryParentsFn(typeInner);
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
                            if (item.is_leaf === 0) {
                                st = true;
                            } else if (item.is_leaf === 1) {
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
        async getCateChild() {
            // 反向递归自定义类型树
            let idArr = this.modalData.type_inner_sn_old;
            if (this.cateSt < idArr.length) {
                let id = idArr[this.cateSt];
                await categoryGetNextListAPI(id).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const rows = res.list;
                        this.categoryList.forEach((item) => {
                            if (item.type_inner_sn === id) {
                                let arr = rows.map((poc) => {
                                    let st;
                                    if (poc.is_leaf === 0) {
                                        st = true;
                                    } else if (poc.is_leaf === 1) {
                                        st = false;
                                    }
                                    poc.label = poc.type;
                                    poc.value = poc.type_inner_sn;
                                    poc.isLeaf = st;
                                    return poc;
                                });
                                item.children = arr;
                            }
                        });
                        this.cateSt++;
                        this.getCateChild();
                        this.formData.type_inner_sn = this.modalData.type_inner_sn_old;
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            }
        },
        changeCategory(val) {
            let index = val.length - 1;
            this.formData.type_inner_sn = val[index];
        },
        async get68ParentListFn() {
            // 获取68分类
            await platformAgencyScopeTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const tree = res.tree;
                    const circle = (arr) => {
                        arr.forEach((item) => {
                            item.label = '(' + item.code68_sn + ') ' + item.name;
                            item.value = item.code68_sn;
                            let st;
                            if (item.is_leaf === 0) {
                                st = false;
                            } else if (item.is_leaf === 1) {
                                st = true;
                            }
                            item.isLeaf = st;
                            if (item.children && item.children.length > 0) {
                                circle(item.children);
                            }
                        });
                    };
                    tree.forEach((item) => {
                        item.label = '(' + item.code68_sn + ') ' + item.name;
                        item.value = item.code68_sn;
                        let st;
                        if (item.is_leaf === 0) {
                            st = false;
                        } else if (item.is_leaf === 1) {
                            st = true;
                        }
                        item.isLeaf = st;
                        if (item.children && item.children.length > 0) {
                            circle(item.children);
                        }
                    });
                    this.op68List = tree;
                    let typeInner = this.modalData.code68_sn_old;
                    this.getCode68ParentsFn(typeInner);
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
                                st = true;
                            } else if (item.is_leaf === 1) {
                                st = false;
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
        async get68Child() {
            // 反向递归68类型树
            let idArr = this.modalData.code68_sn_old;
            if (this.i68St < idArr.length) {
                let id = idArr[this.i68St];
                await code68ChildrenListAPI(id).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const rows = res.list;
                        this.op68List.forEach((item) => {
                            if (item.code68_sn === id) {
                                let arr = rows.map((poc) => {
                                    let st;
                                    if (poc.is_leaf === 0) {
                                        st = true;
                                    } else if (poc.is_leaf === 1) {
                                        st = false;
                                    }
                                    poc.label = '(' + poc.code68_sn + ') ' + poc.name;
                                    poc.value = poc.code68_sn;
                                    poc.isLeaf = st;
                                    return poc;
                                });
                                item.children = arr;
                            }
                        });
                        this.i68St++;
                        this.get68Child();
                        this.formData.code68_sn = this.modalData.code68_sn_old;
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            }
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
        getEditInfo() {
            this.$emit('getEditInfo', true);
        },
        async getCategoryParentsFn(id) {
            if (id) {
                await getCategoryParentsAPI(id).then((res) => {
                    let dataArray = [];
                    let getParent = (item) => {
                        // 递归查询
                        if (item.parent) {
                            dataArray.unshift(item.parent.type_inner_sn);
                            getParent(item.parent);
                        }
                    };
                    if (parseFloat(res.code) === 0) {
                        const info = res.info;
                        dataArray.unshift(info.type_inner_sn);
                        getParent(info);
                        this.modalData.type_inner_sn_old = dataArray;
                        let typeInner = this.modalData.type_inner_sn_old;
                        if (typeInner && typeInner.length > 0) {
                            // this.getCateChild()
                            this.formData.type_inner_sn = typeInner;
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            }
        },
        async getCode68ParentsFn(id) {
            if (id) {
                await getCode68ParentsAPI(id).then((res) => {
                    let dataArray = [];
                    let getParent = (item) => {
                        // 递归查询
                        if (item.parent) {
                            dataArray.unshift(item.parent.code68_sn);
                            getParent(item.parent);
                        }
                    };
                    if (parseFloat(res.code) === 0) {
                        const info = res.info;
                        dataArray.unshift(info.code68_sn);
                        getParent(info);
                        this.modalData.code68_sn_old = dataArray;
                        let typeInner = this.modalData.code68_sn_old;
                        if (typeInner && typeInner.length > 0) {
                            // this.get68Child()
                            this.formData.code68_sn = typeInner;
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            }
        },
        // 打开新增规格型号/单位
        openCreateSpec() {
            this.isEdit = false;
            this.newSpec = '';
            this.newPvd = '';
            this.newUnit = '';
            this.specSt = true;
            this.$Utils.globalDragCenterFn('unit_md_ed');
        },
        openEditSpec() {
            this.isEdit = true;
            this.newSpec = this.chooseSpecObj.specifications;
            this.newPvd = this.chooseSpecObj.specification_vendor_sn;
            this.newUnit = this.chooseSpecObj.minUnit;
            this.specSt = true;
            this.$Utils.globalDragCenterFn('unit_md_ed');
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
                specification_vendor_sn: this.newPvd
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
        },
        async loadSpecFn(vid, pid) {
            this.specData = [];
            let params = {
                org_id: this.$cookie.get('userbelong'),
                source: parseInt(this.$cookie.get('userSystemType')),
                vendor_inner_sn: vid,
                product_inner_sn: pid
            };
            await tmpSpecListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.specData = res.list.map((item) => {
                        return {
                            key: Math.random(),
                            audit_level: 0,
                            minUnit: item.unit_name,
                            operation: 1,
                            specifications: item.specification,
                            specification_vendor_sn: item.specification_vendor_sn,
                            specification_inner_sn: item.specification_inner_sn
                        };
                    });
                }
            });
        },
        initEditData() {
            this.isSame = false;
            this.formData.type_inner_sn = [];
            this.formData.code68_sn = [];
            this.pageType = this.$cookie.get('pageType');
            this.goodsType = this.$cookie.get('goodsType');
            this.cateSt = 0;
            this.i68St = 0;
            this.getCategoryListFn(); // 类型赋值
            this.get68ParentListFn(); // 68赋值
            // 其他赋值
            console.log(this.modalData);
            this.formData.vendor_name = this.modalData.vendor_name;
            this.formData.vendor_inner_sn = this.modalData.vendor_inner_sn_old;
            this.formData.storage_condition = this.modalData.storage_condition_old;
            this.formData.health_care_type = this.modalData.health_care_type_old;
            this.formData.name = this.modalData.name_old;
            this.goodsName = this.modalData.name_old;
            this.formData.specifications = this.modalData.specifications_old;
            this.formData.health_care_sn = this.modalData.health_care_sn_old;
            this.formData.description = this.modalData.description_old;
            this.formData.type = this.modalData.type_old;
            this.formData.status = this.modalData.status_old;
            this.formData.code68_type = this.modalData.code68_type_old;
            this.loadSpecFn(this.modalData.vendor_inner_sn_old, this.modalData.pid);
            // this.specData = []
            // this.modalData.specData && this.modalData.specData.forEach(item => {
            //     let newObj = {
            //         key: Math.random(),
            //         audit_level: 0,
            //         minUnit: item.unit_name,
            //         operation: 1,
            //         specifications: item.specification,
            //         specification_vendor_sn: item.specification_vendor_sn,
            //         specification_inner_sn: item.specification_inner_sn
            //     }
            //     this.specData.push(newObj)
            // })
            if (this.formData.type === 1) {
                this.radioType = false;
            } else {
                this.radioType = true;
                this.formData.specifications = '';
            }
            if (parseInt(this.formData.type) === 0) {
                this.isNoMedic = false;
            } else {
                this.isNoMedic = true;
            }
        }
    },
    mounted() {
        // this.getVendorListFn()
        this.getEditInfo();
        this.$watch(
            'goodsName',
            this.$Utils.debounce(() => {
                // 延时监听input
                if (this.goodsType === 'deaCt') {
                    this.checkProName();
                }
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
textarea {
    resize: none;
}
</style>
