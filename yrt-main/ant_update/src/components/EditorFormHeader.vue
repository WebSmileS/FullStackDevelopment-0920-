<template>
    <div class="editor-form-header-wrapper">
        <slot></slot>
    </div>
</template>

<script>
/**
 * 编辑模态框顶部form容器，提供统一样式，配合EditorFormHeaderItem使用
 */
export default {
    name: 'EditorFormHeader'
};
</script>

<style lang="less" scoped>
.editor-form-header-wrapper {
    display: flex;
    flex-wrap: nowrap;
    align-items: center;
    justify-content: space-between;
    align-items: center;
}
</style>
