<template>
    <a-col class="search-form-item list-page-header-modal-input" :span="span">
        <span class="form-label">{{ label }}</span>
        <base-input-with-clear-button
            :value="value"
            @input="$listeners.input"
            @clear="$listeners.clear"
        ></base-input-with-clear-button>
    </a-col>
</template>

<script>
import BaseInputWithClearButton from '@/components/BaseInputWithClearButton';

/**
 * 列表页顶部模态框输入触发器，配合ListPageHeader组件使用，
 * 此组件只提供统一的样式和行为，开始输入时触发input事件，清除时触发clear事件，
 * 具体的值输入逻辑需要自行处理
 */
export default {
    name: 'ListPageHeaderModalInput',
    components: {
        BaseInputWithClearButton
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
         * 输入框内需要展示的值
         */
        value: {
            type: String,
            default: undefined
        }
    },
    methods: {
        onInput(event) {
            event.stopPropagation();
            this.$emit('input');
        }
    }
};
</script>

<style lang="less" scoped>
.list-page-header-modal-input {
    span.form-label {
        flex: 1;
        flex-grow: 1;
    }
    /deep/ .base-input-with-clear-button {
        width: 70%;
        height: 24px;
        flex: auto;
        flex-grow: 0;
    }
}
</style>
