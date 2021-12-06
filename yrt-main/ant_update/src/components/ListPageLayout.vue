<template>
    <div class="list-page" :style="{ height: `${pageHeight}px` }">
        <div ref="header" class="list-page-header-wrapper">
            <slot name="header"></slot>
        </div>
        <div class="list-page-content-wrapper" :class="{ 'with-tab': isTabListPage }">
            <slot name="list"></slot>
            <slot name="modals"></slot>
        </div>
    </div>
</template>

<script>
/**
 * 列表页布局主框，提供统一的样式，配合ListPageHeader、ListPageList、ListPageListScrollLoad等组件使用
 */
export default {
    name: 'ListPageLayout',
    props: {
        isTabListPage: {
            type: Boolean,
            default: false
        }
    },
    computed: {
        pageHeight() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
            return clientHeight - 53;
        }
    }
};
</script>

<style lang="less" scoped>
.list-page {
    height: 100%;
    display: flex;
    flex-direction: column;
    background-color: transparent;
    .list-page-header-wrapper {
        flex: 1;
        flex-grow: 0;
    }
    .list-page-content-wrapper {
        flex: 1;
        flex-grow: 1;
        overflow: hidden;
    }
}
.list-page-content-wrapper {
    margin-top: 4px;
    margin-bottom: 3px;
    padding: 8px;
    background-color: #fff;
    &.with-tab {
        padding: 4px 0 0;
        background-color: transparent;
        /deep/ .ant-tabs {
            height: 100%;
        }
        /deep/ .ant-tabs-content {
            height: calc(100% - 40px);
        }
        /deep/ .ant-tabs-tabpane {
            height: 100%;
        }
        /deep/ .ant-tabs-bar {
            margin-bottom: 0;
            padding-left: 10px;
            text-align: left;
        }
        /deep/ .ant-tabs-content {
            padding: 8px;
            border-radius: 4px;
            background-color: #fff;
        }
    }
}
</style>
