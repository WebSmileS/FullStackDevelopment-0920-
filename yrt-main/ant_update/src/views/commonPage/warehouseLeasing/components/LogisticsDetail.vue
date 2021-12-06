<template>
    <base-modal
        :title="title"
        dragKey="logistics_detail_md"
        destroyOnClose
        :visible="visible"
        :width="width"
        :height="height"
        :onClose="onClose"
    >
        <template slot="content">
            <h6 style="margin-top: 24px; margin: 24px 0; text-align: center; font-size: 24px; font-weight: 500">{{
                title
            }}</h6>
            <div class="notice-editor-form-wrapper">
                <div class="notice-editor-form-item">
                    <span class="label"><em class="em red">*</em>物流方式</span>
                    <a-select :value="logisticsMode" :options="logisticsModeOptions"></a-select>
                </div>
                <div class="notice-editor-form-item">
                    <span class="label">
                        <em class="em red" v-if="logisticsMode === logisticsModeMap.THIRD.value">*</em>
                        物流单号
                    </span>
                    <a-input size="small" :value="logisticsSn" />
                </div>
                <div class="notice-editor-form-item">
                    <span class="label" style="width: 170px">
                        <em class="em red" v-if="logisticsMode === logisticsModeMap.THIRD.value">*</em>
                        第三方物流公司
                    </span>
                    <a-input size="small" :value="logisticsCompany" />
                </div>
            </div>
            <div>
                <vxe-table
                    border
                    show-overflow
                    show-header-overflow
                    ref="xTable"
                    height="400"
                    size="small"
                    align="center"
                    :data="list"
                    :scroll-y="{ enabled: true }"
                    :scroll-x="{ enabled: true }"
                    @cell-dblclick="({ $event }) => $event.stopPropagation()"
                >
                    <vxe-table-column type="seq" title="序号" width="60"></vxe-table-column>
                    <template v-for="(column, index) in columns">
                        <vxe-table-column
                            :key="index"
                            :title="column.title"
                            :field="column.dataIndex"
                            :width="column.width"
                        />
                    </template>
                </vxe-table>
            </div>
        </template>
        <template slot="footer">
            <a-button v-if="logisticsEditable" type="primary" @click="onEdit">编辑</a-button>
            <a-button type="default" @click="onClose">关闭</a-button>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal.vue';
import columns from './logisticsListColumns';
import { logisticsModeMap, logisticsStatusMap } from './logisticsType';

export default {
    name: 'LogisticsDetail',
    components: {
        BaseModal
    },
    props: {
        title: {
            type: String,
            default: '物流详情'
        },
        visible: {
            type: Boolean,
            default: false
        },
        height: {
            type: Number,
            default: 600
        },
        width: {
            type: Number,
            default: 1200
        },
        logisticsData: {
            type: Object,
            default: null
        },
        onClose: {
            type: Function,
            default: null
        },
        onEdit: {
            type: Function,
            default: null
        }
    },
    data() {
        return {
            columns,
            loading: false,
            logisticsId: undefined,
            logisticsMode: logisticsModeMap.SELF.value,
            logisticsSn: '',
            logisticsCompany: '',
            list: []
        };
    },
    computed: {
        logisticsModeMap() {
            return logisticsModeMap;
        },
        logisticsModeOptions() {
            return Object.keys(logisticsModeMap).map((key) => ({
                value: logisticsModeMap[key].value,
                label: logisticsModeMap[key].text
            }));
        },
        logisticsEditable() {
            if (this.logisticsData) {
                return this.logisticsData.logisticsStatus === logisticsStatusMap.DRAFTD.value;
            }
            return false;
        }
    },
    methods: {
        handleInitData() {
            const { logisticsData } = this;
            if (!logisticsData) {
                this.logisticsMode = '';
                this.logisticsSn = '';
                this.logisticsCompany = '';
                this.logisticsId = undefined;
                this.list = [];
                return;
            }
            this.logisticsMode = logisticsData.logisticsMode || this.logisticsModeOptions[0].value;
            this.logisticsSn = logisticsData.logisticsSn || '';
            this.logisticsCompany = logisticsData.logisticsCompany || '';
            this.list = logisticsData.notices;
            this.logisticsId = logisticsData.logisticsId;
        }
    },
    watch: {
        visible(newValue, oldValue) {
            if (newValue !== oldValue && newValue === true) {
                if (this.handleInitData) {
                    this.handleInitData();
                }
            }
        }
    }
};
</script>

<style lang="less" scoped>
.notice-editor-form-wrapper {
    margin-bottom: 24px;
    display: flex;
}
.notice-editor-form-item {
    display: flex;
    flex: 1;
    .label {
        margin-right: 12px;
        text-align: right;
        min-width: 72px;
    }
}
</style>
