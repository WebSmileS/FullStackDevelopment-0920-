<template>
    <div class="editor-form-footer-wrapper">
        <slot></slot>
    </div>
</template>

<script>
/**
 * 编辑模态框底部form容器，提供统一样式，配合EditorFormFooterItem使用
 */
export default {
    name: 'EditorFormFooter'
};
</script>

<style lang="less" scoped>
.editor-form-footer-wrapper {
    height: 24px;
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
    align-items: center;
}
</style>
