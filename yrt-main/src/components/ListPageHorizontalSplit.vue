<template>
    <div class="list-page-horizontal-split-wrapper">
        <div class="list-page-horizontal-split-top">
            <slot name="top"></slot>
        </div>
        <div class="list-page-horizontal-split-line"></div>
        <div class="list-page-horizontal-split-bottom">
            <slot name="bottom"></slot>
        </div>
    </div>
</template>

<script>
/**
 * 列表页水平分割，将列表页的主体拆分为上下两个部分，提供统一的样式
 * @todo 拖动分割线时，动态调整上下两个部分的高度比例
 */
export default {
    name: 'ListPageHorizontalSplit'
};
</script>

<style lang="less" scoped>
.list-page-horizontal-split-wrapper {
    height: 100%;
}
.list-page-horizontal-split-top {
    height: 50%;
}
.list-page-horizontal-split-bottom {
    height: calc(50% - 20px);
}
.list-page-horizontal-split-line {
    height: 6px;
    margin-left: -8px;
    margin-right: -8px;
    margin-bottom: 14px;
    border-top: 1px solid #eee;
    border-bottom: 1px solid #eee;
}
</style>
