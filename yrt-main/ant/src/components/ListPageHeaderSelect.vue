<template>
    <a-col class="search-form-item" :span="span">
        <span class="form-label">{{ label }}</span>
        <a-select
            size="small"
            v-bind="$attrs"
            :options="options"
            @change="(value, option) => $emit('change', value, option)"
        ></a-select>
    </a-col>
</template>

<script>
/**
 * 列表页顶部选择器，配合ListPageHeader组件使用，
 * change事件：用户选择时触发此事件，携带所选值，及所选项
 */
export default {
    name: 'ListPageHeaderSelect',
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
        },
        /**
         * 选项，参见a-select的options属性
         */
        options: {
            type: Array,
            default: () => []
        }
    }
};
</script>

<style lang="less" scoped>
.search-form-item {
    /deep/ .ant-select {
        width: 70%;
        height: 24px;
        flex: auto;
        flex-grow: 0;
    }
}
</style>
