<template>
    <base-modal v-bind="$attrs" v-on="$listeners" dragKey="organization_selecter_md" :footer="null">
        <template slot="content">
            <div style="margin: 36px 0 0">
                <choose-org-tab
                    :pageTypeName="pageTypeName"
                    :partType="organizationType"
                    @chooseOrg="onSubmit"
                ></choose-org-tab>
            </div>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal';
import ChooseOrgTab from '@/components/chooseOrgTab';

/**
 * 机构选择器
 * submit事件：用户选择一个机构时，触发此事件，参数携带用户所选机构的描述对象
 */
export default {
    name: 'OrganizationSelecter',
    props: {
        /**
         * 机构类型：医院（hos），经销商（dea），厂商（ven）
         * 当机构类型没有值时，默认查所有机构
         */
        organizationType: {
            type: String,
            validator: (value) => {
                return ['hos', 'dea', 'ven'].findIndex((_) => _ === value) >= 0;
            }
        }
    },
    components: {
        BaseModal,
        ChooseOrgTab
    },
    computed: {
        pageTypeName() {
            if (this.organizationType) {
                return 1;
            }
            return 0;
        }
    },
    methods: {
        onSubmit(resObj) {
            let organizations = [];
            if (resObj && resObj.org_id) {
                organizations.push({
                    organizationInnerSn: resObj.org_id,
                    organizationName: resObj.org_name,
                    billsType: resObj.billsType,
                    socialCreditCode: resObj.social_credit_code
                });
            }
            this.$emit('submit', organizations);
        }
    }
};
</script>
