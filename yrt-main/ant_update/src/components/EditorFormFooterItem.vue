<template>
    <div class="editor-form-footer-item">
        <slot></slot>
    </div>
</template>

<script>
/**
 * 编辑模态框底部form项，提供统一样式，配合EditorFormFooter使用
 */
export default {
    name: 'EditorFormFooterItem'
};
</script>

<style lang="less" scoped>
.editor-form-footer-item {
    display: flex;
    flex-wrap: nowrap;
    justify-content: flex-start;
    align-items: center;
    flex-grow: 0;
    margin-right: 32px;
    &:last-child {
        margin-right: 0;
    }
    > .ant-input,
    > .ant-select {
        width: 95px;
    }
}
</style>
