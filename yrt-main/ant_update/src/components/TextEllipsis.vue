<template>
    <a-tooltip>
        <span slot="title">
            <slot></slot>
        </span>
        <span class="text-ellipsis">
            <slot></slot>
        </span>
    </a-tooltip>
</template>

<script>
export default {
    name: 'TextEllipsis'
};
</script>

<style lang="less" scoped>
.text-ellipsis {
    display: inline-block;
    max-width: 100%;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
</style>
