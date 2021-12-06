<template>
    <a-col class="search-form-item list-page-header-input" :span="span">
        <span class="form-label">{{ label }}</span>
        <a-input
            type="text"
            v-bind="$attrs"
            @change="$emit('change', $event.target.value)"
            @keydown.enter="$emit('keyDownEnter')"
        />
    </a-col>
</template>

<script>
/**
 * 列表页顶部文本输入，配合ListPageHeader组件使用
 * change事件：输入值发生变化时触发此事件
 */
export default {
    name: 'ListPageHeaderInput',
    model: {
        prop: 'value',
        event: 'change'
    },
    props: {
        /**
         * 所占栅格数
         */
        span: {
            type: Number,
            default: 6
        },
        /**
         * 标签
         */
        label: {
            type: String,
            default: ''
        }
    }
};
</script>

<style lang="less" scoped>
.list-page-header-input {
    /deep/ .ant-input {
        width: 70%;
        height: 24px;
        flex: auto;
        flex-grow: 0;
    }
}
</style>
