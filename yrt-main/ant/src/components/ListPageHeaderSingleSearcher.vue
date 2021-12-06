<template>
    <div class="single-search">
        <a-tooltip v-if="typeof tooltipContent === 'string'" :title="tooltipContent" placement="bottom">
            <a-input-search
                class="search"
                size="small"
                v-bind="$attrs"
                enterButton
                :placeholder="placeholder"
                @search="$listeners.search"
                @change="$emit('change', $event.target.value)"
            />
        </a-tooltip>
        <a-input-search
            v-else
            class="search"
            size="small"
            v-bind="$attrs"
            enterButton
            :placeholder="placeholder"
            @search="$listeners.search"
            @change="$emit('change', $event.target.value)"
        />
    </div>
</template>

<script>
/**
 * 列表页顶部单条件查询器，提供统一的样式，配合ListPageHeader组件使用，
 * change事件：输入值发生变化时触发此事件
 */
export default {
    name: 'ListPageHeaderSingleSearcher',
    model: {
        prop: 'value',
        event: 'change'
    },
    props: {
        /**
         * 输入框placeholder
         */
        placeholder: {
            type: String,
            default: '关键词'
        },
        /**
         * 输入聚焦时的提示信息
         */
        tooltipContent: {
            type: String,
            default: undefined
        }
    }
};
</script>
