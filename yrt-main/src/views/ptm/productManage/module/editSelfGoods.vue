<template>
    <div class="modal-list">
        <a-form>
            <a-form-item v-bind="formItemLayout" label="经销商" class="must-issue" has-feedback>
                <a-input type="text" disabled="true" v-model="formData.dealer_name" placeholder="" />
            </a-form-item>
            <a-form-item
                label="名称"
                :label-col="formItemLayout.labelCol"
                class="must-issue"
                :wrapper-col="formItemLayout.wrapperCol"
            >
                <a-input v-model="formData.name" />
            </a-form-item>
            <a-row>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half" class="must-issue" label="类型">
                        <a-cascader
                            v-model="formData.type_inner_sn"
                            :options="categoryList"
                            placeholder=""
                            :displayRender="displayRender"
                        />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half_2" label="产品分类">
                        <a-cascader
                            v-model="formData.code68_sn"
                            :options="op68List"
                            changeOnSelect
                            placeholder=""
                            :displayRender="displayRender"
                        />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half" label="存储条件">
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
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half_2" label="状态">
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
            <a-row>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half" label="医保类型">
                        <a-select
                            style="margin-top: 6px"
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
            <!--<div style="margin-left: 70px;margin-bottom: 5px">-->
            <!--<a-radio-group v-model="formData.type" @change="changeRadio">-->
            <!--<a-radio :value="0">医疗器械</a-radio>-->
            <!--<a-radio :value="1">非医疗器械</a-radio>-->
            <!--</a-radio-group>-->
            <!--</div>-->
            <a-form-item label="备注" v-bind="formItemLayout">
                <a-textarea v-model="formData.description" :autosize="textAreaSet" />
            </a-form-item>
        </a-form>
    </div>
</template>

<script>
import {
    getVendorList,
    dealerTypeParentsAPI,
    getAllDealerTypeAPI,
    categoryGetNextListAPI,
    platformAgencyScopeTreeAPI,
    code68ChildrenListAPI,
    getCode68ParentsAPI
} from '@/service/pageAjax';

export default {
    name: 'editSelfGoods',
    props: ['modalData'],
    data() {
        return {
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
            formData: {
                dealer_inner_sn: [], // 厂商ID
                type_inner_sn: '', // 自定义分类内部编号
                code68_sn: '', // 医疗器械68分类编号
                storage_condition: 1, // 存储条件
                health_care_type: 0, // 医保类型
                name: '',
                health_care_sn: '', // 医保编号
                description: '',
                type: '',
                status: ''
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
            ]
        };
    },
    methods: {
        initPageData() {
            // 初始化页面
            this.formData = {
                dealer_inner_sn: [], // 厂商ID
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [], // 医疗器械68分类编号
                storage_condition: 1, // 存储条件
                health_care_type: 0, // 医保类型
                name: '',
                health_care_sn: '', // 医保编号
                description: '',
                type: '',
                status: ''
            };
        },
        returnPageData() {
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
        changeRadio(e) {
            let val = e.target.value;
            if (val === 0) {
                this.radioType = true;
                this.formData.specifications = '';
            } else {
                this.radioType = false;
            }
        },
        changeVendor(val) {
            this.formData.dealer_inner_sn = val;
        },
        async getCategoryListFn() {
            // 获取自定义类型列表
            let oid = this.$cookie.get('userbelong');
            await getAllDealerTypeAPI(oid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const tree = res.list;
                    const circle = (arr) => {
                        arr.forEach((item) => {
                            item.label = item.type;
                            item.value = item.type_inner_sn;
                            let st;
                            if (item.is_leaf === 0) {
                                st = false;
                            } else if (item.is_leaf === 1) {
                                st = true;
                            }
                            if (item.status === 0) {
                                item.disabled = true;
                            }
                            item.isLeaf = st;
                            if (item.children && item.children.length > 0) {
                                circle(item.children);
                            } else {
                                item.children = null;
                            }
                        });
                    };
                    tree.forEach((item) => {
                        item.label = item.type;
                        item.value = item.type_inner_sn;
                        let st;
                        if (item.is_leaf === 0) {
                            st = false;
                        } else if (item.is_leaf === 1) {
                            st = true;
                        }
                        item.isLeaf = st;
                        if (item.status === 0) {
                            item.disabled = true;
                        }
                        if (item.children && item.children.length > 0) {
                            circle(item.children);
                        } else {
                            item.children = null;
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
                    if (rows.length > 0) {
                        targetOption.children = rows.map((item) => {
                            let st;
                            if (item.is_leaf === 0) {
                                st = false;
                            } else if (item.is_leaf === 1) {
                                st = true;
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
                                        st = false;
                                    } else if (poc.is_leaf === 1) {
                                        st = true;
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
                                        st = false;
                                    } else if (poc.is_leaf === 1) {
                                        st = true;
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
            let oid = this.$cookie.get('userbelong');
            if (id) {
                await dealerTypeParentsAPI(oid, id).then((res) => {
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
        initEditData() {
            this.cateSt = 0;
            this.i68St = 0;
            this.getCategoryListFn(); // 类型赋值
            this.get68ParentListFn(); // 68赋值
            // 其他赋值
            this.formData.dealer_name = this.modalData.dealer_name;
            this.formData.dealer_name = this.modalData.dealer_name;
            this.formData.dealer_inner_sn = this.modalData.dealer_inner_sn_old;
            this.formData.storage_condition = this.modalData.storage_condition_old;
            this.formData.health_care_type = this.modalData.health_care_type_old;
            this.formData.name = this.modalData.name_old;
            this.formData.health_care_sn = this.modalData.health_care_sn_old;
            this.formData.description = this.modalData.description_old;
            this.formData.type = this.modalData.type_old;
            this.formData.status = this.modalData.status_old;
            if (this.formData.type === 1) {
                this.radioType = false;
            } else {
                this.radioType = true;
            }
        }
    },
    mounted() {
        // this.getVendorListFn()
        this.getEditInfo();
    }
};
</script>

<style scoped lang="less">
.modal-list {
    .ant-form-item {
        margin-bottom: 8px;
    }
}
textarea {
    resize: none;
}
</style>
