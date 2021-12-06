<template>
    <div class="editor-form-header-item">
        <slot></slot>
    </div>
</template>

<script>
/**
 * 编辑模态框顶部form项，提供统一样式，配合EditorFormHeader使用
 */
export default {
    name: 'EditorFormHeaderItem'
};
</script>

<style lang="less" scoped>
.editor-form-header-item {
    display: flex;
    flex: 1;
    flex-wrap: nowrap;
    margin-right: 24px;
    &:last-child {
        margin-right: 0;
    }
    .label {
        white-space: nowrap;
    }
    .ant-input,
    .ant-select,
    .ant-calendar-picker,
    .ant-cascader-picker {
        flex-grow: 1;
    }
    .ant-btn {
        margin-left: calc(100% - 48px);
    }
}
</style>
