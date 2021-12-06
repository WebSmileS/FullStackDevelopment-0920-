<template>
    <div class="modal-form-input modal-list">
        <div class="cooperation-intention-wrap">
            <ul>
                <div class="item">
                    <span class="label"><em class="em red">*</em>{{ orgType }}</span>
                    <a-input
                        :disabled="disabledType"
                        readonly="readonly"
                        class="input"
                        @click="openChooseTb"
                        v-model="orgName"
                    />
                </div>
                <li class="item">
                    <span class="label"><em class="em red">*</em>开始日期</span>
                    <a-date-picker :value="stDate" placeholder="" format="YYYY-MM-DD" @change="changeStDate" />
                </li>

                <li class="item">
                    <span class="label"><em class="em red">*</em>截止日期</span>
                    <a-date-picker :value="endDate" placeholder="" format="YYYY-MM-DD" @change="changeEndDate" />
                </li>

                <li class="item">
                    <span class="label">手术意向</span>
                    <a-checkbox-group v-model="checkedList" :options="plainOptions" @change="changeStatus" />
                </li>
                <li class="item" style="margin-top: -10px">
                    <span style="margin-right: 14px; padding-right: 0px; margin-top: -32px" class="label">备注</span>
                    <a-textarea
                        v-model="formData.description"
                        style="min-height: 50px; max-height: 100px; resize: none"
                    />
                </li>
            </ul>
            <div class="brand-intention-wrap">
                <vxe-table
                    border
                    show-overflow
                    show-header-overflow
                    showHeader
                    highlight-hover-row
                    highlight-current-row
                    ref="xBrandTable"
                    height="240"
                    align="center"
                    :data="xBrandTable"
                    :loading="brandObj.loading"
                    row-id="brand_inner_sn"
                    :checkbox-config="{ checkRowKeys: checkBrandRowKeys, showHeader: false, highlight: true }"
                >
                    <vxe-table-column type="checkbox" width="88" title="品牌意向"></vxe-table-column>
                    <vxe-table-column field="name" title="品牌名称"></vxe-table-column>
                </vxe-table>
            </div>
        </div>
        <a-modal
            :title="chooseOrgMd.title"
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            @cancel="chooseOrgMd.alert = false"
            class="modal-form-input-scoped global-drag"
            :class="addModalName"
        >
            <div
                v-if="addModalName == 'ct_choose_org'"
                v-globalDrag="{ el: 'ct_choose_org' }"
                class="modal-header-wrap"
            >
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <div v-else v-globalDrag="{ el: 'ed_choose_org' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <choose-org-tab
                ref="chooseOrgMd"
                @chooseOrg="chooseOrgFn"
                :pageTypeName="1"
                :isCertifie="true"
                :partType="partType"
            ></choose-org-tab>
        </a-modal>
    </div>
</template>

<script>
import chooseOrgTab from '@/components/chooseOrgTab';
import moment from 'moment';
import { platformSurgeryBrandListAPI } from '@/service/pageAjax';
export default {
    name: 'opCooperDetail',
    props: ['openType', 'editObj'],
    components: {
        chooseOrgTab
    },
    data() {
        return {
            xBrandTable: [],
            checkBrandRowKeys: [],
            brandIntentionList: [],
            brandObj: {
                loading: false
            },
            partType: 'hos',
            checkedList: [],
            formData: {
                hospital_inner_sn: '',
                dealer_inner_sn: '',
                begin_date: '',
                end_date: '',
                description: '',
                t0_status: 0, // 颅骨状态
                t1_status: 0, // 脊柱状态
                t2_status: 0, // 关节状态
                t3_status: 0 // 创伤状态
            },
            chooseOrgMd: {
                title: `${this.$route.meta.title}  - 选择机构`,
                alert: false,
                loading: false
            },
            plainOptions: [
                {
                    label: '颅骨',
                    value: 0
                },
                {
                    label: '脊柱',
                    value: 1
                },
                {
                    label: '关节',
                    value: 2
                },
                {
                    label: '创伤',
                    value: 3
                }
            ],
            addModalName: '',
            nowOrgType: false,
            disabledType: false,
            orgType: '',
            orgName: '',
            stDate: '',
            endDate: ''
        };
    },
    methods: {
        async platformSurgeryBrandListAPIFn() {
            this.brandObj.loading = true;
            await platformSurgeryBrandListAPI()
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.brandIntentionList = list.filter((item) => parseInt(item.status) > 0); // 0：无效不显示
                        this.loadXBrandTableFn();
                    }
                })
                .finally(() => {
                    this.brandObj.loading = false;
                });
        },
        loadXBrandTableFn(actionNameType) {
            if (actionNameType === 'showLoading') {
                this.brandObj.loading = true;
            }

            if (this.openType === 'edit') {
                const curBrandIntentionList = [...this.brandIntentionList],
                    checkBrandRow = [];
                for (let i = 0; i < this.editObj.rel_brands.length; i++) {
                    const cItem = this.editObj.rel_brands[i];
                    for (let j = 0; j < curBrandIntentionList.length; j++) {
                        const item = curBrandIntentionList[j];
                        if (parseFloat(cItem.brand_inner_sn) === parseFloat(item.brand_inner_sn)) {
                            checkBrandRow.push(cItem);
                            this.checkBrandRowKeys.push(cItem.brand_inner_sn);
                            curBrandIntentionList.splice(j, 1);
                        }
                    }
                }
                // 排序
                this.xBrandTable = [...checkBrandRow, ...curBrandIntentionList];
            } else {
                this.xBrandTable = this.brandIntentionList;
            }
            // 刷新页面表格数据
            const xBrandTable = this.$refs.xBrandTable;
            xBrandTable.reloadData(this.xBrandTable);
            xBrandTable.refreshScroll();

            if (actionNameType === 'showLoading') {
                this.brandObj.loading = false;
            }
        },
        pageRefresh() {
            this.nowOrgType = parseFloat(this.$cookie.get('userSystemType')); // 判断当前机构
            this.checkBrandRow = [];
            this.checkBrandRowKeys = [];
            this.xBrandTable = [];
            if (this.nowOrgType === 1) {
                // 医院
                this.disabledType = true;
            }
            if (this.openType === 'create') {
                // 新增
                this.addModalName = 'ct_choose_org';
                this.formData = {
                    hospital_inner_sn: '',
                    dealer_inner_sn: '',
                    begin_date: '',
                    end_date: '',
                    description: '',
                    t0_status: 0, // 颅骨状态
                    t1_status: 0, // 脊柱状态
                    t2_status: 0, // 关节状态
                    t3_status: 0 // 创伤状态
                };

                if (this.nowOrgType === 1) {
                    // 医院
                    this.formData.hospital_inner_sn = parseInt(this.$cookie.get('userbelong'));
                    this.orgType = '经销商';
                }
                if (this.nowOrgType === 2) {
                    // 经销商
                    this.formData.dealer_inner_sn = parseInt(this.$cookie.get('userbelong'));
                    this.orgType = '医院';
                }
                this.checkedList = [];
                this.orgName = '';
                this.stDate = '';
                this.endDate = '';
            }
            if (this.openType === 'edit') {
                this.disabledType = true;
                this.addModalName = 'ed_choose_org';
                this.formData = {
                    hospital_inner_sn: this.editObj.hospital_inner_sn,
                    dealer_inner_sn: this.editObj.dealer_inner_sn,
                    begin_date: this.editObj.begin_date,
                    end_date: this.editObj.end_date,
                    description: this.editObj.description,
                    t0_status: this.editObj.t0_status, // 颅骨状态
                    t1_status: this.editObj.t1_status, // 脊柱状态
                    t2_status: this.editObj.t2_status, // 关节状态
                    t3_status: this.editObj.t3_status // 创伤状态
                };
                if (this.nowOrgType === 1) {
                    // 医院
                    this.formData.hospital_inner_sn = parseInt(this.$cookie.get('userbelong'));
                    this.orgType = '经销商';
                    this.orgName = this.editObj.dealer_name;
                }
                if (this.nowOrgType === 2) {
                    // 经销商
                    this.formData.dealer_inner_sn = parseInt(this.$cookie.get('userbelong'));
                    this.orgType = '医院';
                    this.orgName = this.editObj.hospital_name;
                }
                this.checkedList = [];
                if (this.editObj.t0_status === 1 || this.editObj.t0_status === 2) {
                    this.checkedList.push(0);
                }
                if (this.editObj.t1_status === 1 || this.editObj.t1_status === 2) {
                    this.checkedList.push(1);
                }
                if (this.editObj.t2_status === 1 || this.editObj.t2_status === 2) {
                    this.checkedList.push(2);
                }
                if (this.editObj.t3_status === 1 || this.editObj.t3_status === 2) {
                    this.checkedList.push(3);
                }
                this.stDate = moment(this.editObj.begin_date);
                this.endDate = moment(this.editObj.end_date);
            }
            this.checkBrandRowKeys = [];
            if (this.brandIntentionList.length) {
                this.loadXBrandTableFn('showLoading');
            } else {
                this.platformSurgeryBrandListAPIFn();
            }
        },
        returnData() {
            if (this.formData.description.length > 150) {
                this.$warning({
                    title: '操作提示',
                    content: '备注输入长度不能超过150个字符。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            if (this.openType === 'create') {
                this.formData.rel_brands = this.$refs.xBrandTable.getSelectRecords().map((item) => {
                    return {
                        ...item,
                        status: 1
                    };
                });
                let st = this.checkData(this.formData);
                if (st !== false) {
                    this.$emit('returnData', this.formData);
                }
            }
            if (this.openType === 'edit') {
                let newObj = this.formData;
                let oldObj = this.editObj;
                if (this.nowOrgType === 1) {
                    // 医院
                    let check = (item) => {
                        if (item === 2) {
                            item = 1;
                        }
                        return item;
                    };
                    newObj.t0_status = check(newObj.t0_status);
                    newObj.t1_status = check(newObj.t1_status);
                    newObj.t2_status = check(newObj.t2_status);
                    newObj.t3_status = check(newObj.t3_status);
                }
                let dataObj = {
                    begin_date: newObj.begin_date,
                    end_date: newObj.end_date,
                    description: newObj.description,
                    t0_status: newObj.t0_status, // 颅骨状态
                    t1_status: newObj.t1_status, // 脊柱状态
                    t2_status: newObj.t2_status, // 关节状态
                    t3_status: newObj.t3_status // 创伤状态
                    // begin_date_old: this.editObj.begin_date,
                    // end_date_old: this.editObj.end_date,
                    // description_old: this.editObj.description,
                    // t0_status_old: this.editObj.t0_status, // 颅骨状态
                    // t1_status_old: this.editObj.t1_status, // 脊柱状态
                    // t2_status_old: this.editObj.t2_status, // 关节状态
                    // t3_status_old: this.editObj.t3_status // 创伤状态
                };
                dataObj.hospital_inner_sn = newObj.hospital_inner_sn;
                dataObj.dealer_inner_sn = newObj.dealer_inner_sn;
                let st = this.checkData(dataObj);
                if (!dataObj.begin_date) {
                    dataObj.begin_date = oldObj.begin_date;
                }
                if (!dataObj.end_date) {
                    dataObj.end_date = oldObj.end_date;
                }
                dataObj.rel_brands = this.$refs.xBrandTable.getSelectRecords().map((item) => {
                    return {
                        ...item,
                        status: 1
                    };
                });
                if (st !== false) {
                    this.$emit('returnData', dataObj);
                }
            }
        },
        checkData(data) {
            if (data.hospital_inner_sn === '') {
                this.$warning({
                    title: '操作提示',
                    content: '请选择医院',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (data.begin_date === '') {
                this.$warning({
                    title: '操作提示',
                    content: '开始日期不能为空',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (data.end_date === '') {
                this.$warning({
                    title: '操作提示',
                    content: '截止日期不能为空',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (data.begin_date && data.end_date) {
                let beginDate = new Date(data.begin_date.replace(/-/g, '/'));
                let endDate = new Date(data.end_date.replace(/-/g, '/'));
                if (endDate < beginDate) {
                    this.$warning({
                        title: '操作提示',
                        content: '截止日期不能小于开始日期',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
            }
        },
        changeStDate(moment, val) {
            // 修改开始日期
            this.stDate = moment;
            this.formData.begin_date = val;
        },
        changeEndDate(moment, val) {
            // 修改结束日期
            this.endDate = moment;
            this.formData.end_date = val;
        },
        changeStatus(val) {
            // 修改合作意向
            this.checkedList = val;
            this.formData.t0_status = 0;
            this.formData.t1_status = 0;
            this.formData.t2_status = 0;
            this.formData.t3_status = 0;
            const systemType = parseInt(this.$cookie.get('userSystemType'));
            this.checkedList.forEach((item) => {
                if (item === 0) {
                    this.formData.t0_status = 2;
                    if (systemType === 1) {
                        this.formData.t0_status = 1;
                    }
                }
                if (item === 1) {
                    this.formData.t1_status = 2;
                    if (systemType === 1) {
                        this.formData.t1_status = 1;
                    }
                }
                if (item === 2) {
                    this.formData.t2_status = 2;
                    if (systemType === 1) {
                        this.formData.t2_status = 1;
                    }
                }
                if (item === 3) {
                    this.formData.t3_status = 2;
                    if (systemType === 1) {
                        this.formData.t3_status = 1;
                    }
                }
            });
        },
        openChooseTb() {
            // 打开选择医院
            this.chooseOrgMd.alert = true;
            this.$Utils.globalDragCenterFn(this.addModalName);
            if (!this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
        },
        async chooseOrgFn(org) {
            // 获取选中机构信息
            this.orgName = org.org_name;
            if (this.nowOrgType === 1) {
                // 医院
                this.formData.dealer_inner_sn = org.org_id;
            }
            if (this.nowOrgType === 2) {
                // 经销商
                this.formData.hospital_inner_sn = org.org_id;
            }
            this.chooseOrgMd.alert = false;
        }
    },
    mounted() {
        this.pageRefresh();
    }
};
</script>

<style scoped lang="less">
.modal-list li.item {
    margin-bottom: 0px !important;
}
.modal-form-input-scoped {
    .modal-form-input {
        padding-top: 0px !important;
        li.item {
            .item-children:first-child {
                width: 50%;
            }
            .item-children:last-child {
                .label {
                    width: 104px;
                }
            }
        }
        .label {
            width: 80px;
        }
        .ant-select {
            width: 173px;
        }
        .ant-calendar-picker-input {
            height: 24px !important;
        }
    }
}

.cooperation-intention-wrap {
    display: flex;
    height: 240px;

    .brand-intention-wrap {
        display: flex;
        flex: 1;
        margin-left: 8px;
        padding-left: 8px;
        border-left: 1px solid #e8eaec;

        .vxe-table {
            width: 100%;
        }
    }
}
</style>
