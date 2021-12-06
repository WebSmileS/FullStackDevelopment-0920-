<template>
    <div class="print-editor">
        <slot name="printContent" />
        <a-drawer
            wrapClassName="print-editor-drawer"
            width="50%"
            :visible="printEditor.visible"
            @close="handleCloseEditor"
        >
            <a-tabs type="card">
                <a-tab-pane key="baseConfig" tab="基础设置">
                    <base-config />
                </a-tab-pane>
                <a-tab-pane key="tableConfig" tab="表格设置">
                    <table-formatter />
                </a-tab-pane>
                <a-tab-pane key="customColumn" tab="自定义列">
                    <custom-column></custom-column>
                </a-tab-pane>
                <a-tab-pane key="otherConfig" tab="其他设置">
                    <other-config />
                </a-tab-pane>
            </a-tabs>
            <a-row class="print-editor-footer">
                <a-col class="buttons-wrapper">
                    <a-tooltip
                        title="有新的设置未应用"
                        destroyTooltipOnHide
                        :visible="!allIsSync && printEditor.visible"
                    >
                        <a-button type="primary" :disabled="allIsSync" @click.stop="handleSyncData">
                            <span>应用设置</span>
                            <span v-if="otherConfig.autoSync">（已开启自动应用）</span>
                        </a-button>
                    </a-tooltip>
                    <a-button type="primary" icon="undo" @click.stop="handleResetPrintConfig">重置设置</a-button>
                    <a-button type="primary" icon="printer" @click.stop="handlePrint">打印</a-button>
                </a-col>
            </a-row>
        </a-drawer>
        <div class="print-editor-fixed-buttons" v-if="!printEditor.visible">
            <div>
                <a-button
                    class="print-editor-fixed-button-edit"
                    type="primary"
                    shape="circle"
                    size="large"
                    icon="edit"
                    @click="handleOpenEditor"
                />
            </div>
            <div>
                <a-button
                    class="print-editor-fixed-button-print"
                    type="primary"
                    shape="circle"
                    size="large"
                    icon="printer"
                    @click="handlePrint"
                />
            </div>
        </div>
    </div>
</template>

<script>
import BaseConfig from './BaseConfig.vue';
import TableFormatter from './TableFormatter.vue';
import CustomColumn from './CustomColumn.vue';
import OtherConfig from './OtherConfig.vue';
import PrintEditorProvider from './PrintEditorProvider';

export default {
    name: 'PrintEditor',
    components: {
        BaseConfig,
        TableFormatter,
        CustomColumn,
        OtherConfig
    },
    provide: PrintEditorProvider,
    data() {
        return {
            printEditor: PrintEditorProvider.printEditor,
            baseConfig: PrintEditorProvider.baseConfig,
            baseConfigEditing: PrintEditorProvider.baseConfigEditing,
            tableFormatter: PrintEditorProvider.tableFormatter,
            tableFormatterEditing: PrintEditorProvider.tableFormatterEditing,
            contentData: PrintEditorProvider.contentData,
            contentDataEditing: PrintEditorProvider.contentDataEditing,
            otherConfig: PrintEditorProvider.otherConfig,
            otherConfigEditing: PrintEditorProvider.otherConfigEditing
        };
    },
    computed: {
        allIsSync() {
            return (
                this.printEditor.baseConfigSync &&
                this.printEditor.tableFormatterSync &&
                this.printEditor.otherConfigSync &&
                this.printEditor.contentDataSync
            );
        }
    },
    methods: {
        handleCloseEditor() {
            this.printEditor.visible = false;
        },
        handleOpenEditor() {
            this.printEditor.visible = true;
        },

        /**
         * 将当前编辑态的设置进行同步，同步后打印内容视图将响应新的设置项变化进行渲染
         */
        handleSyncData() {
            // 同步
            this.printEditor.handleSyncData();

            // 保存配置数据
            this.handleSavePrintConfig();
        },

        /**
         * 获取打印配置的key
         * @param {boolean} isOlder
         */
        handleGetPrintConfigKey(isOlder = false) {
            if (isOlder) {
                return `${this.printEditor.moduleKey}-printConfig-${this.printEditor.moduleConfigPrevVersion}`;
            }
            return `${this.printEditor.moduleKey}-printConfig-${this.printEditor.moduleConfigVersion}`;
        },

        /**
         * 保存打印设置数据
         */
        handleSavePrintConfig() {
            window.localStorage.setItem(
                this.handleGetPrintConfigKey(),
                JSON.stringify({
                    baseConfig: this.baseConfig,
                    tableFormatter: this.tableFormatter,
                    otherConfig: this.otherConfig
                })
            );
        },

        /**
         * 移除打印设置数据
         * @param {boolean} isOlder 是否移除上一版本的设置数据
         */
        handleRemovePrintConfig(isOlder = false) {
            window.localStorage.removeItem(this.handleGetPrintConfigKey(isOlder));
        },

        /**
         * 加载已保存的打印设置数据
         */
        handleLoadSavedPrintConfig() {
            const printConfig = window.localStorage.getItem(this.handleGetPrintConfigKey());
            if (printConfig) {
                return JSON.parse(printConfig);
            }
            return false;
        },

        /**
         * 应用已保存的打印设置数据
         */
        handleApplySavedPrintConfig() {
            const savedConfig = this.handleLoadSavedPrintConfig();
            if (typeof savedConfig === 'object') {
                this.printEditor.handleInitialize(savedConfig);
            }
        },

        /**
         * 重置打印设置为默认设置
         */
        handleResetPrintConfig() {
            this.$confirm({
                title: '重置打印设置',
                content: '重置后已保存的设置将被清空，要继续吗？',
                okText: '确认',
                cancelText: '取消',
                onOk: () => {
                    try {
                        this.handleRemovePrintConfig();
                        window.location.reload();
                    } catch (err) {
                        this.$message.error('重置失败，请手动刷新页面。');
                        return true;
                    }
                }
            });
        },

        /**
         * 打印
         */
        handlePrint() {
            if (this.printEditor.baseConfigSync && this.printEditor.tableFormatterSync) {
                window.print();
                return;
            }
            this.$confirm({
                title: '有新的设置未应用，要继续打印吗？',
                okText: '继续打印',
                onOk: () => {
                    window.print();
                },
                cancelText: '取消'
            });
        }
    },
    watch: {
        baseConfigEditing: {
            deep: true,
            handler() {
                if (this.printEditor.baseConfigInitialized) {
                    // 自动同步
                    if (this.otherConfig.autoSync) {
                        this.printEditor.baseConfigSync = false;
                        this.handleSyncData();
                        return;
                    }
                    this.printEditor.baseConfigSync = false;
                }
            }
        },
        tableFormatterEditing: {
            deep: true,
            handler() {
                if (this.printEditor.tableFormatterInitialized) {
                    // 自动同步
                    if (this.otherConfig.autoSync) {
                        this.printEditor.tableFormatterSync = false;
                        this.handleSyncData();
                        return;
                    }
                    this.printEditor.tableFormatterSync = false;
                }
            }
        },
        otherConfigEditing: {
            deep: true,
            handler() {
                if (this.printEditor.otherConfigInitialized) {
                    // 自动同步
                    if (this.otherConfig.autoSync) {
                        this.printEditor.otherConfigSync = false;
                        this.handleSyncData();
                        return;
                    }
                    this.printEditor.otherConfigSync = false;
                }
            }
        },
        contentDataEditing: {
            deep: true,
            handler() {
                if (this.printEditor.contentDataInitialized) {
                    // 自动同步
                    if (this.otherConfig.autoSync) {
                        this.printEditor.contentDataSync = false;
                        this.handleSyncData();
                        return;
                    }
                    this.printEditor.contentDataSync = false;
                }
            }
        }
    },
    mounted() {
        // 自动应用上次保存的设置
        this.handleApplySavedPrintConfig();
        // 删除上一版本的设置数据
        // this.handleRemovePrintConfig(true)
    }
};
</script>

<style lang="less">
.print-editor {
    .print-editor-fixed-buttons {
        position: fixed;
        z-index: 99;
        right: 24px;
        top: 24px;
        & > div {
            margin-bottom: 14px;
        }
    }
}
.print-editor-drawer {
    z-index: 2;
    height: 100%;
    overflow: hidden;
    .ant-drawer-body,
    .ant-tabs {
        height: 100%;
        overflow: hidden;
    }
    .ant-drawer-body {
        padding-bottom: 56px;
    }
    .ant-tabs-content {
        height: calc(100% - 42px);
        overflow: auto;
    }
    .ant-tabs-bar {
        margin-bottom: 0;
    }
    .ant-form-item {
        .ant-select,
        .ant-input-number {
            width: 100%;
        }
        .ant-select-selection {
            margin-top: 7px;
        }
        .ant-input-number-input,
        .ant-input-number {
            height: 24px;
            line-height: 24px;
        }
    }
}
.print-editor-footer {
    position: absolute;
    bottom: 0;
    left: 0;
    width: calc(100% - 48px);
    height: 58px;
    margin-left: 24px;
    border-top: 1px solid #e8e8e8;
    background-color: #ffffff;
}
.buttons-wrapper {
    padding-top: 13px;
    button {
        margin-right: 14px;
    }
}
.print-editor-form-title {
    font-weight: 700;
    margin-top: 24px;
    margin-bottom: 14px;
}
.print-editor-form-wrapper {
    &.ant-row {
        margin-left: 8px !important;
        margin-right: 0 !important;
    }
    .ant-input,
    .ant-input-number-input,
    .ant-input-number {
        height: 24px;
        line-height: 24px;
    }
}
@media print {
    .print-editor,
    .print-editor-drawer {
        padding: 0;
        margin: 0;
    }
    .print-editor-drawer,
    .print-editor-fixed-buttons,
    .ant-tooltip,
    .ant-modal-root,
    .ant-modal-wrap,
    .ant-modal-mask {
        display: none;
    }
}
</style>
