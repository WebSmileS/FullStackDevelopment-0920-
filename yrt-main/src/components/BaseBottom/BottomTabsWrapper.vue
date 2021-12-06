<template>
    <div class="bottom-tabs-wrapper">
        <slot></slot>
    </div>
</template>

<script>
export default {
    name: 'BottomTabsWrapper'
};
</script>

<style lang="less" scoped>
.bottom-tabs-wrapper {
    width: 100%;
    height: 100%;
    /deep/ .no-data-box {
        width: 100%;
        padding-top: 40px;
    }
    /deep/ .ant-tabs-card-bar {
        margin-bottom: 0;
    }
    /deep/ .ant-tabs-content {
        height: calc(100% - 40px);
    }
    /deep/ .ant-tabs-tabpane {
        padding-top: 16px;
    }
    /deep/ .bottom-tabs-container,
    /deep/ .ant-tabs-tabpane {
        height: 100%;
    }
    /deep/ .scroll-vertical-wrapper {
        height: 100%;
        overflow-x: hidden;
        overflow-y: auto;
    }
    /deep/ .scroll-vertical-content {
        display: flex;
        flex-wrap: wrap;
        width: 100%;
    }
    /deep/ .bottom-tabs-container {
        position: relative;
        padding: 0 16px 16px;
    }
    /deep/ .bottom-tabs-content-left,
    /deep/ .bottom-tabs-content-right {
        position: absolute;
        top: 0;
    }
    /deep/ .bottom-tabs-content-left {
        left: 20px;
    }
    /deep/ .bottom-tabs-content-right {
        left: 140px;
        width: calc(100% - 154px);
        height: calc(100% - 16px);
    }
    /deep/ .bottom-buttons-vertical-wrapper {
        button {
            display: block;
            width: 100px;
            margin-bottom: 12px;
        }
    }
}
</style>
