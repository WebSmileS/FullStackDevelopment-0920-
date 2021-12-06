<template>
    <div class="modal-list">
        <a-form>
            <a-form-item v-bind="formItemLayout" label="经销商" class="must-issue" has-feedback>
                <a-input type="text" disabled="true" v-model="orgName" placeholder="" />
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
                            :loadData="loadCategory"
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
                            :loadData="load68data"
                            changeOnSelect
                            placeholder=""
                            :displayRender="displayRender"
                        />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-form-item v-bind="formItemLayout_short" label="存储条件">
                <a-select
                    v-decorator="['select', { rules: [{ required: true, message: 'Please select your country!' }] }]"
                    style="margin-top: 4px"
                    v-model="formData.storage_condition"
                >
                    <a-select-option v-for="item of storageCon" :value="item.value" :key="item.value">
                        {{ item.name }}
                    </a-select-option>
                </a-select>
            </a-form-item>
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
            <!--</a-form-item>-->
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
                :isCertifie="true"
                :pageTypeName="1"
            ></choose-org-tab>
        </a-modal>
    </div>
</template>

<script>
import {
    dealerGetParentListAPI,
    dealerGetNextListAPI,
    code68ParentListAPI,
    code68ChildrenListAPI,
    userUserInfoAPI
} from '@/service/pageAjax';

import chooseOrgTab from '@/components/chooseOrgTab';

export default {
    name: 'createSelfGoods',
    props: ['modalData'],
    components: {
        chooseOrgTab
    },
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
            chooseOrgMd: {
                title: `${this.$route.meta.title} - 新增 - 添加厂商`,
                alert: false,
                loading: false
            },
            radioType: true,
            defaultSel: [],
            formData: {
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [], // 医疗器械68分类编号
                storage_condition: 1, // 存储条件
                health_care_type: 0, // 医保类型
                name: '',
                health_care_sn: '', // 医保编号
                description: ''
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
            orgSystemType: ''
        };
    },
    methods: {
        async initPageData() {
            // 初始化页面
            this.formData = {
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [], // 医疗器械68分类编号
                storage_condition: 1, // 存储条件
                health_care_type: 0, // 医保类型
                name: '',
                health_care_sn: '', // 医保编号
                description: ''
            };
            await userUserInfoAPI().then((res) => {
                if (parseInt(res.Code || res.code) === 0) {
                    this.orgName = res.data.EmployeeInfo.OrgName;
                }
            });
        },
        returnPageData() {
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
            this.formData.vendor_inner_sn = val;
        },
        async getCategoryListFn() {
            // 获取自定义类型列表
            let oid = this.$cookie.get('userbelong');
            await dealerGetParentListAPI(oid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    this.categoryList = rows.map((item) => {
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
            let oid = this.$cookie.get('userbelong');
            await dealerGetNextListAPI(oid, id).then((res) => {
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
        }
    },
    mounted() {
        this.initPageData();
        this.getCategoryListFn();
        this.get68ParentListFn();
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
