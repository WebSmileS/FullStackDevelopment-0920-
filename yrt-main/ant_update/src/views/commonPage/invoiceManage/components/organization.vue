<template>
    <div>
        <!-- 选择单位名称区域开始 -->
        <a-modal
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            :zIndex="3"
            @cancel="chooseOrgMd.alert = false"
            class="scoped modal-form-input-scoped global-drag choose_org_md"
        >
            <div v-globalDrag="{ el: 'choose_org_md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <div style="margin-top: 56px">
                <choose-org-tab
                    ref="chooseOrgMd"
                    @chooseOrg="chooseOrgFn"
                    :partType="1"
                    :pageTypeName="pageTypeName"
                ></choose-org-tab>
            </div>
        </a-modal>
        <!-- 选择单位名称区域结束 -->
    </div>
</template>

<script>
import chooseOrgTab from '@/components/chooseOrgTab';

export default {
    name: 'captionText', // 模态框的标题文字区域
    props: {
        isInbound: {
            type: Boolean,
            retuired: true
        }
    },
    components: {
        chooseOrgTab
    },
    data() {
        return {
            pageTypeName: undefined,
            chooseOrgMd: {
                title: `${this.$route.meta.title} - 选择机构`,
                alert: false,
                loading: false
            },
            orgData: {
                organization_inner_sn: '',
                organization_name: '',
                billsType: '',
                social_credit_code: ''
            }
        };
    },
    methods: {
        initPageFn() {
            if (this.isInbound) {
                // 单位名称页面的标识： 0-【仓库管理-入库单】  2【仓库管理-出库单】
                this.pageTypeName = 0;
            } else {
                this.pageTypeName = 2;
            }

            this.chooseOrgMd.alert = true;
            this.$Utils.globalDragCenterFn('choose_org_md');

            this.$nextTick(() => {
                if (this.$refs.chooseOrgMd) {
                    this.$refs.chooseOrgMd.setContractOrgCheckedFn();
                    this.$refs.chooseOrgMd.pageDataInit();
                }
            });
        },
        chooseOrgFn(resObj) {
            if (resObj && resObj.org_id) {
                this.orgData.organization_inner_sn = resObj.org_id;
                this.orgData.organization_name = resObj.org_name;
                this.orgData.billsType = resObj.billsType;
                this.orgData.social_credit_code = resObj.social_credit_code;
            } else {
                this.orgData.organization_inner_sn = '';
                this.orgData.organization_name = '';
                this.orgData.billsType = '';
                this.orgData.social_credit_code = '';
            }
            this.chooseOrgMd.alert = false;
            this.$emit('getOrgData', this.orgData);
        }
    }
};
</script>

<style lang="less" scoped></style>
