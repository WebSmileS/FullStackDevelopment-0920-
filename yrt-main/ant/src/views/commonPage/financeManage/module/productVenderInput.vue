<template>
    <div class="product-vender-input-wrapper">
        <input
            :value="value"
            readonly
            class="ant-input product-vender-input"
            :placeholder="placeholder"
            @click="handleClick"
            autocomplete="off"
        />
        <span class="ant-input-suffix" v-if="value && allowClear">
            <a-icon
                @click="handleClear"
                type="close-circle"
                class="anticon anticon-close-circle ant-input-clear-icon"
                theme="filled"
            />
        </span>
    </div>
</template>

<script>
/**
 * 自定义表单组件
 * 选择厂商/经销商时，希望组件以input的展现形式并且能进行选择，而不允许用户自己编辑input
 * 所以该自定义组件只支持选择
 * antdv的a-input同样支持readOnly属性，但加了该属性之后，就不能使用清空操作
 */
export default {
    name: 'ProductVenderInput',

    props: {
        value: {
            type: [String, Number]
        },
        allowClear: {
            type: Boolean,
            default: false
        },
        placeholder: {
            type: String,
            default: ''
        }
    },

    methods: {
        handleClick() {
            this.$emit('click');
        },

        handleClear() {
            this.$emit('clear');
        }
    }
};
</script>

<style lang="less">
.product-vender-input-wrapper {
    position: relative;
    .ant-input-suffix {
        position: absolute;
        right: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 12px;
        height: 12px;
        line-height: 12px;
        border-radius: 100%;
        overflow: hidden;
        .ant-input-clear-icon {
            font-size: 12px;
        }
    }
}
</style>
