<template>
    <a-cascader
        class="and-cascader"
        :allowClear="false"
        :options="data === undefined || data === null || Object.keys(data).length === 0 ? [] : data.data"
        :value="data === undefined || data === null || Object.keys(data).length === 0  ? [] : data.key"
        expand-trigger="hover"
        @change="onChange"
        :style="'display:' + obj.display + ';left:' + obj.left + 'px;top:' + (obj.top - 26) + 'px;width:' + obj.width + 'px'"
    >
        <template slot="displayRender" slot-scope="{ labels, selectedOptions }">
            <span v-for="(label, index) in labels" :key="selectedOptions[index].value">
                <span v-if="index === labels.length - 1">
                    {{ label }}
                </span>
                <span v-else> {{ label }} / </span>
            </span>
        </template>
    </a-cascader>
</template>

<script>

export default {
    name: 'AndCascader',
    props: {
        obj: {
            type: Object,
            default: () => {}
        },
        data: {
            type: Object,
            default: () => {}
        }
    },
    data() {
        return {
        }
    },
    methods: {
        onChange(value, arr) {
            this.$emit('chooseUnitFn', value, arr);
        }
    },
    mounted() {
    },
    watch: {
        obj: {
            handler(newV, oldV) {
                if (newV.display === 'block') {
                    setTimeout(() => {document.getElementsByClassName('ant-cascader-picker')[0].click()}, 100);
                }
            },
            deep: true
        }
    }
};
</script>