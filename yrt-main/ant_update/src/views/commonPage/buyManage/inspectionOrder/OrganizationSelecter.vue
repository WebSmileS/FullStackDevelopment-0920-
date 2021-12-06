<template>
    <base-modal v-bind="$attrs" v-on="$listeners" dragKey="organization_selecter_md" :footer="null">
        <template slot="content">
            <div style="margin: 36px 0 0">
                <choose-org-tab :partType="1" :pageTypeName="0" @chooseOrg="onSubmit"></choose-org-tab>
            </div>
        </template>
    </base-modal>
</template>

<script>
import ChooseOrgTab from '@/components/chooseOrgTab';
import BaseModal from '@/components/BaseModal';

export default {
    name: 'OrganizationSelecter',
    components: {
        BaseModal,
        ChooseOrgTab
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
