<template>
    <a-row class="atc-header">
        <a-col :span="24">
            <h3>询价单</h3>
        </a-col>
        <a-col :span="7">
            <a-col :span="6" style="margin-top: 1px">
                <span><em class="em red">*</em>&nbsp;相关机构:</span>
            </a-col>
            <a-col :span="15">
                <a-select
                    :disabled="parseInt(formData.status) === 3"
                    mode="default"
                    show-search
                    style="width: 100%"
                    :value="stateOrgName"
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
                <a-input :disabled="parseInt(formData.status) === 3" @change="changeContacts" :value="formData.contacts" style="width: 80px"/>
            </a-col>
        </a-col>
        <a-col :span="4">
            <a-col :span="6" style="margin-top: 1px">
                <span>电话:</span>
            </a-col>
            <a-col :span="13">
                <a-input :disabled="parseInt(formData.status) === 3" @change="changeTelephone" :value="formData.phone" style="width: 110px"/>
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
        }
    },
    data() {
        return {
            // others
            fetchingOrg: false,
            orgData: [],
            orgTpData: [],
            selectedOrg: {},
            stateOrgName: this.searchOrgName
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
         * 从billsType中获取相关机构类型
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
        }
    },
    mounted() {
        // inital load Org
        this.getInquiryOrgListFn(2);
        this.getInquiryOrgListFn(3);
    },
    watch: { 
      	searchOrgName: function(newV, oldV) { // watch it
            this.stateOrgName = newV;
        }
    }
};
</script>