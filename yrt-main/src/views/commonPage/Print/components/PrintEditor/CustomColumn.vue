<template>
    <div class="print-editor-custom-column" :class="{ 'fullscreen': fullScreen }">
        <div class="print-editor-form-wrapper buttons-wrapper" style="margin-bottom: 12px">
            <a-button size="small" type="primary" icon="plus" @click="onAddColumnClick">新增列</a-button>
            <a-button size="small" type="danger" icon="delete" @click="onDeleteAllClick">全部删除</a-button>
            <a-button v-if="!fullScreen" size="small" icon="fullscreen" @click="fullScreen = true">全屏</a-button>
            <a-button v-else size="small" icon="fullscreen-exit" @click="fullScreen = false">返回</a-button>
        </div>
        <custom-column-editable-table
            ref="editTable"
            :columns="tableFormatterEditing.columns"
            :onlyShowKeyColumn="!fullScreen"
            :dataSource="contentDataEditing.tableData"
        ></custom-column-editable-table>
    </div>
</template>

<script>
import { CustomColumnEditableTable } from './CustomColumnEditableTable';

export default {
    name: 'CustomColumn',
    components: {
        CustomColumnEditableTable
    },
    inject: ['tableFormatterEditing', 'contentDataEditing'],
    data() {
        return {
            fullScreen: false
        };
    },
    methods: {
        onAddColumnClick() {
            this.$refs.editTable.handleAddColumn();
        },
        onDeleteAllClick() {
            this.$confirm({
                title: '确定要删除所有自定义的列吗？',
                okText: '确认',
                cancelText: '取消',
                onOk: () => {
                    this.tableFormatterEditing.columns = this.tableFormatterEditing.columns.filter(
                        (column) => !column.isCustomColumn
                    );
                }
            });
        }
    }
};
</script>

<style lang="less" scoped>
.buttons-wrapper {
    /deep/ .ant-btn {
        float: right;
        &:first-child {
            margin-right: 0;
        }
    }
    &::after {
        content: '';
        display: block;
        width: 0;
        height: 0;
        clear: both;
    }
}
.fullscreen {
    position: fixed;
    z-index: 11;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    padding: 12px 24px 24px 24px;
    background-color: #fff;
    .print-editor-form-title {
        display: none;
    }
}
</style>
