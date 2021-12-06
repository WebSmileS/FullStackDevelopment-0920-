<template>
    <a-row class="atc-header">
        <a-col :span="24">
            <h2>询价单&nbsp;&nbsp;{{ formData.status === 3 ? formData.ilps_sn : '' }}</h2>
        </a-col>
        <a-col :span="7">
            <a-col :span="6" style="margin-top: 1px">
                <span><em class="em red">*</em>&nbsp;单位名称:</span>
            </a-col>
            <a-col :span="15">
                <a-select
                    :disabled="parseInt(formData.status) === 3"
                    class="focus-a-select"
                    mode="default"
                    show-search
                    style="width: 100%"
                    :value="searchOrgName ? searchOrgName : formData.partB_name"
                    :filter-option="false"
                    :show-arrow="false"
                    :not-found-content="null"
                    :default-active-first-option="false"
                    @search="searchOrgFn"
                    @change="changeOrgFn"
                >
                    <a-spin v-if="fetchingOrg" slot="notFoundContent" size="small" />
                    <a-select-option v-for="d in orgData" :key="d.key" :value="d.key">
                        {{ d.org_name }}
                    </a-select-option>
                    <a-select-option value="search-more">
                        <b style="color: #1890ff">搜索更多...</b>
                    </a-select-option>
                    <a-select-option value="tips">
                        <i>提示：输入文字搜索</i>
                    </a-select-option>
                </a-select>
            </a-col>
        </a-col>
        <a-col :span="4">
            <a-col :span="8" style="margin-top: 1px">
                <span>联系人:</span>
            </a-col>
            <a-col :span="10">
                <a-input
                    class="form-contacts"
                    :disabled="parseInt(formData.status) === 3" @change="changeContacts"
                    :value="formData.contacts"
                    @keydown.enter="changeFocus('form-phone')"
                    style="width: 80px"/>
            </a-col>
        </a-col>
        <a-col :span="4">
            <a-col :span="6" style="margin-top: 1px">
                <span>电话:</span>
            </a-col>
            <a-col :span="13">
                <a-input
                    class="form-phone"
                    :disabled="parseInt(formData.status) === 3" @change="changeTelephone"
                    :value="formData.phone"
                    @keydown="inputKeydown"
                    style="width: 120px"/>
            </a-col>
        </a-col>
    </a-row>
</template>

<script>
// import ajax
import {
    getInquiryOrgListAPI
} from '@/service/pageAjax';

export default {
    name: 'EnquiryAddHeader',
    props: {
        searchOrgName: {
            type: String,
            default: () => ''
        },
        formData: {
            type: Object,
            default: () => {}
        },
        editable: {
            type: Object,
            default: () => {}
        }
    },
    data() {
        return {
            // others
            fetchingOrg: false,
            orgData: [],
            orgTpData: [],
            selectedOrg: {}
        };
    },
    methods: {
        searchOrgFn(value = '') {
            this.orgData  = this.orgTpData = [];
            this.getInquiryOrgListFn(2, value);
            this.getInquiryOrgListFn(3, value);
        },
        changeOrgFn(value) {
            if (value === 'search-more') {
                this.$emit('openChooseTb');
                return;
            } else if (value === 'tips') {
                return;
            } else {
                this.selectedOrg = Object.assign(
                    {},
                    this.orgData.find((v) => v.key === value)
                );
                this.stateOrgName = this.selectedOrg.org_name;
                this.$emit('chooseOrgFn', this.selectedOrg);
            }
        },
        // 【采购管理-询价】
        async getInquiryOrgListFn(type, value = '') {
            this.stateOrgName = value;
            let params = {
                name: value,
                pageSize: 5,
                pageNum: 1,
                is_certifie: true
            };
            await getInquiryOrgListAPI(type, { params: params })
            .then((res) => {
                if (parseFloat(res.code) === 0) {
                    let TempData = res.rows.map((item) => {
                        item.key = (Math.random() + 1).toString(36).substring(7);
                        item.systemType = parseInt(type);
                        item.org_type_name = this.parseOrgNameFromBillsType(item.billsType);
                        item.serachOrgName = item.org_name;
                        return item;
                    });
                    this.orgTpData = this.orgTpData.concat(TempData);
                }
            })
            .finally(() => {
                this.orgData = this.orgData.concat(this.orgTpData);
                this.orgTpData = [];
                this.fetchingOrg = true;
            });
        },
        // ****************************************************************
        // 接口区域结束
        // ****************************************************************
        /**
         * 从billsType中获取单位名称类型
         * @param billsType
         */
        parseOrgNameFromBillsType(billsType) {
            const orgTypeStr = billsType.substr(billsType.length - 1, 1);
            let orgTypeName = '';
            switch (orgTypeStr) {
                case 'H':
                    orgTypeName = '医院';
                    break;
                case 'V':
                    orgTypeName = '厂商';
                    break;
                case 'D':
                    orgTypeName = '经销商';
                    break;
            }
            return orgTypeName;
        },
        changeContacts(e) {
            this.$emit('changeFormData', {
                key: 'contacts',
                value: e.target.value
            });
        },
        changeTelephone(e) {
            this.$emit('changeFormData', {
                key: 'phone',
                value: e.target.value
            });
        },
        changeFocus(className) {
            if (className === 'sheet') {
                let sheetFirstCell = document.querySelectorAll('[tabindex="' + 0 + '9' + 1 + '"]')[0];
                sheetFirstCell.click();
                var event = new MouseEvent('dblclick', {
                    'view': window,
                    'bubbles': true,
                    'cancelable': true
                });
                sheetFirstCell.dispatchEvent(event);
            } else {
                document.getElementsByClassName(className)[0].focus();
            }
        },
        inputKeydown(e) {
            e.keyCode === 13 && this.changeFocus('sheet');
            // validate phone number
            if ((e.keyCode > 47 && e.keyCode < 58) || (e.keyCode > 95 && e.keyCode < 106)) {
                this.formData.phone.length === 11 && e.preventDefault();
            } else {
                !(e.keyCode === 37 || e.keyCode === 39 || e.keyCode === 9 || e.keyCode === 8 || e.keyCode === 46 || e.keyCode === 36 || e.keyCode === 35) && e.preventDefault();
            }
        },
        autoDropdownInForm(data) {
            if (data.status !== 3) {
                // focus
                let aSelectDom = document.getElementsByClassName('focus-a-select');
                aSelectDom[0].getElementsByClassName('ant-select-search__field')[0].click();
            }
        }
    },
    mounted() {
        // inital load Org
        this.getInquiryOrgListFn(2);
        this.getInquiryOrgListFn(3);
        // autoDropdown
        if (!this.editable.flag) {
            this.autoDropdownInForm(this.formData);
        }
    }
};
</script>