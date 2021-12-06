<template>
    <base-modal
        :title="title"
        dragKey="logistics_editor_md"
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
                    <a-select
                        v-model="logisticsMode"
                        :options="logisticsModeOptions"
                        @change="onLogisticsModeChange"
                    ></a-select>
                </div>
                <div class="notice-editor-form-item">
                    <span class="label">
                        <em class="em red" v-if="logisticsMode === logisticsModeMap.THIRD.value">*</em>
                        物流单号
                    </span>
                    <a-input v-if="logisticsMode === logisticsModeMap.THIRD.value" size="small" v-model="logisticsSn" />
                    <a-input v-else size="small" disabled />
                </div>
                <div class="notice-editor-form-item">
                    <span class="label" style="width: 170px">
                        <em class="em red" v-if="logisticsMode === logisticsModeMap.THIRD.value">*</em>
                        第三方物流公司
                    </span>
                    <a-input
                        v-if="logisticsMode === logisticsModeMap.THIRD.value"
                        size="small"
                        v-model="logisticsCompany"
                    />
                    <a-input v-else size="small" disabled />
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
                    <vxe-table-column title="操作" width="120">
                        <template v-slot:default="{ rowIndex }">
                            <a-button size="small" type="default" @click="handleRemoveNotice(rowIndex)">删除</a-button>
                        </template>
                    </vxe-table-column>
                </vxe-table>
            </div>
        </template>
        <template slot="footer">
            <a-button type="default" @click="onClose">取消</a-button>
            <a-button type="primary" @click="handleSave" :loading="loading">保存</a-button>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal.vue';
import columns from './logisticsListColumns';
import { logisticsModeMap } from './logisticsType';

export default {
    name: 'LogisticsEditor',
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
        defaultData: {
            type: Object,
            default: null
        },
        onClose: {
            type: Function,
            default: null
        },
        onSubmit: {
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
        }
    },
    methods: {
        handleInitData() {
            const { defaultData } = this;
            if (!defaultData) {
                this.logisticsMode = '';
                this.logisticsSn = '';
                this.logisticsCompany = '';
                this.logisticsId = undefined;
                this.list = [];
                return;
            }
            this.logisticsMode = defaultData.logisticsMode || this.logisticsModeOptions[0].value;
            this.logisticsSn = defaultData.logisticsSn || '';
            this.logisticsCompany = defaultData.logisticsCompany || '';
            this.list = defaultData.notices;
            this.logisticsId = defaultData.logisticsId;
        },
        handleRemoveNotice(index) {
            const newList = [];
            for (let i = 0, l = this.list.length; i < l; i += 1) {
                if (i === index) continue;
                newList.push(this.list[i]);
            }
            this.list = newList;
        },
        onLogisticsModeChange() {
            // 切换物流方式时，清空历史数据
            this.logisticsSn = '';
            this.logisticsCompany = '';
        },
        async handleSave() {
            if (!this.onSubmit) return;
            try {
                const notices = this.list.map((notice) => notice.id);
                const { trim } = this.$XEUtils;
                let { logisticsMode, logisticsSn, logisticsCompany, logisticsModeMap } = this;

                if (!logisticsMode) throw new Error('请选择物流方式');

                logisticsSn = trim(logisticsSn);
                if (logisticsMode === logisticsModeMap.THIRD.value && !logisticsSn) throw new Error('请填写物流单号');

                logisticsCompany = trim(logisticsCompany);
                if (logisticsMode === logisticsModeMap.THIRD.value && !logisticsCompany)
                    throw new Error('请填写物流公司名称');

                if (notices.length < 1) throw new Error('产品列表不能为空');

                try {
                    this.loading = true;
                    await this.onSubmit({
                        'id': this.logisticsId,
                        'logistics_mode': logisticsMode,
                        'logistics_sn': logisticsMode === logisticsModeMap.THIRD.value ? logisticsSn : undefined,
                        'third_logistics_company':
                            logisticsMode === logisticsModeMap.THIRD.value ? logisticsCompany : undefined,
                        'detail': notices
                    });
                } catch (err) {
                    this.$message.error('保存失败');
                    throw err;
                } finally {
                    this.loading = false;
                }
            } catch (err) {
                this.$message.error(err.message);
            }
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
