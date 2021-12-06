<template>
    <div class="editor-form-footer-row-item">
        <slot></slot>
    </div>
</template>

<script>
/**
 * 编辑模态框底部单行form项，提供统一样式，配合EditorFormFooter使用
 */
export default {
    name: 'EditorFormFooterRowItem'
};
</script>

<style lang="less" scoped>
.editor-form-footer-row-item {
    height: 24px;
    display: flex;
    flex-wrap: nowrap;
    justify-content: flex-start;
    align-items: center;
    > .ant-input {
        width: auto;
        flex-grow: 1;
    }
}
</style>
