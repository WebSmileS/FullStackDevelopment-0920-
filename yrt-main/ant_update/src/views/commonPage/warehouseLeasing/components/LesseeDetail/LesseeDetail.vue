<template>
    <base-modal
        dragKey="lessee_detail_md"
        destroyOnClose
        :title="title"
        :width="1100"
        :visible="visible"
        :onClose="onClose"
    >
        <template slot="content">
            <div class="lessee-detail">
                <a-tabs type="card">
                    <a-tab-pane key="baseInfo" tab="基本信息">
                        <base-info :orgId="orgId" />
                    </a-tab-pane>
                    <a-tab-pane key="licenseInfo" tab="营业执照">
                        <license-info :orgId="orgId" />
                    </a-tab-pane>
                    <a-tab-pane key="icpInfo" tab="医疗器械经营许可证">
                        <i-c-p-info :orgId="orgId" />
                    </a-tab-pane>
                    <a-tab-pane key="recordKeepingInfo" tab="第二类医疗器械经营备案凭证">
                        <record-keeping-info :orgId="orgId" />
                    </a-tab-pane>
                </a-tabs>
            </div>
        </template>
        <template slot="footer">
            <a-button type="default" @click="onClose">关闭</a-button>
        </template>
    </base-modal>
</template>
<script>
import moment from 'moment';
import BaseModal from '@/components/BaseModal.vue';
import BaseInfo from './BaseInfo.vue';
import LicenseInfo from './LicenseInfo.vue';
import ICPInfo from './ICPInfo.vue';
import RecordKeepingInfo from './RecordKeepingInfo.vue';

export default {
    moment,
    name: 'LesseeDetail',
    components: {
        BaseModal,
        BaseInfo,
        LicenseInfo,
        ICPInfo,
        RecordKeepingInfo
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        title: {
            type: String,
            default: ''
        },
        orgId: {
            required: true,
            validator(value) {
                return typeof value === 'string' || typeof value === 'number';
            }
        },
        onClose: {
            type: Function,
            default: null
        }
    }
};
</script>

<style lang="less" scoped>
.lessee-detail {
    width: 100%;
    margin-top: 24px;
    /deep/ .ant-form-item {
        display: flex;
    }
    /deep/ .ant-form-item-label {
        min-width: 124px;
    }
    /deep/ .ant-form-item-control-wrapper {
        flex-grow: 1;
    }
    /deep/ .ant-form-item-control {
        line-height: unset;
    }
}
</style>
