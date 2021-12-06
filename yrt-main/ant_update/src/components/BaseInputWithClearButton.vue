<template>
    <a-input
        class="base-input-with-clear-button"
        type="text"
        v-bind="$attrs"
        v-on="$listeners"
        v-on:click.enter="onInput"
        v-on:click.space="onInput"
        :value="value"
        @click="onInput"
        @change.stop="() => {}"
        @input.stop="() => {}"
    >
        <a-icon
            v-if="value !== undefined && value !== null && value !== '' && !$attrs.disabled"
            slot="suffix"
            type="close-circle"
            @click.stop="$emit('clear')"
        ></a-icon>
    </a-input>
</template>

<script>
/**
 * ant-design-vue的input组件有bug，alowClear属性不生效，所以单独写了此组件。
 * 若ant-design-vue版本升级后已解决此bug，可考虑弃用此组件。
 * 另：所有a-input自带的prop属性和事件已自动绑定。
 * input事件: 输入框值发生变化时触发此事件
 */
export default {
    name: 'BaseInputWithClearButton',
    model: {
        prop: 'value',
        event: 'change'
    },
    props: {
        /**
         * input框value值
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
.base-input-with-clear-button {
    /deep/ .ant-input {
        width: 100%;
        height: 24px;
        flex: auto;
        flex-grow: 0;
    }
    /deep/ .ant-input-suffix {
        display: none;
        cursor: pointer;
        opacity: 0.5;
    }
    &:hover {
        /deep/ .ant-input-suffix {
            display: block;
            cursor: pointer;
            opacity: 0.5;
        }
    }
}
</style>
