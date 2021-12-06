<template>
    <a-form-item :colon="false">
        <template v-slot:default>
            <slot name="default"></slot>
        </template>
        <template v-slot:label>
            <slot name="label"></slot>
            <span>:</span>
            <a-tooltip>
                <template v-slot:default>
                    <span class="helper">?</span>
                </template>
                <template v-slot:title>
                    <slot name="helperText"></slot>
                </template>
            </a-tooltip>
        </template>
    </a-form-item>
</template>

<script>
export default {
    name: 'FormItemWithHelper'
};
</script>

<style lang="less" scoped>
.helper {
    @size: 24px;
    display: inline-block;
    min-width: @size;
    border-radius: @size;
    text-align: center;
    color: #fff;
    background-color: #ffbb66;
    cursor: pointer;
    transform: scale(0.6);
}
</style>
