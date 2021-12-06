<template>
    <a-modal
        destroyOnClose
        :visible="visible"
        :width="616"
        :maskClosable="false"
        :centered="true"
        :footer="null"
        @cancel="handleCancel"
        class="modal-form-input-scoped global-drag finance-choose-org-modal"
    >
        <div v-globalDrag="{ el: 'finance-choose-org-modal' }" class="modal-header-wrap">
            <h6>选择机构</h6>
        </div>
        <div style="margin-top: 74px">
            <choose-org-tab @chooseOrg="handleChooseOrg" ref="chooseOrgTabModal" :pageTypeName="pageType" />
        </div>
    </a-modal>
</template>

<script>
import chooseOrgTab from '@/components/chooseOrgTab';
export default {
    name: 'ChooseOrgModal',

    props: {
        visible: {
            type: Boolean,
            default: false
        },
        pageType: Number
    },

    components: {
        chooseOrgTab
    },

    methods: {
        handleCancel() {
            this.$emit('oncancel');
        },

        handleChooseOrg(resObj) {
            this.$emit('onsubmit', {
                id: resObj.org_id,
                name: resObj.org_name,
                type: resObj.systemType
            });
        }
    }
};
</script>
