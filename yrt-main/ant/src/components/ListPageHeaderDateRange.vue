<template>
    <a-col class="search-form-item list-page-header-date-range" :span="span">
        <span class="form-label">{{ label }}</span>
        <a-range-picker
            size="small"
            :placeholder="placeholders"
            :allowClear="$attrs.allowClear"
            :defaultValue="$attrs.defaultValue"
            @change="onDateChange"
        ></a-range-picker>
    </a-col>
</template>

<script>
/**
 * 列表页顶部日期区间选择器，配合ListPageHeader组件使用,
 * change事件：日期选择变化时触发此事件
 */
export default {
    name: 'ListPageHeaderDateRange',
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
         * 日期区间值，[startDate, endDate]
         */
        value: {
            type: Array,
            default: () => []
        },
        /**
         * 输入框里显示的placeholder
         */
        placeholders: {
            type: Array,
            default: () => ['开始', '结束']
        }
    },
    methods: {
        onDateChange(date) {
            this.$emit('change', date);
        }
    }
};
</script>

<style lang="less" scoped>
.list-page-header-date-range {
    span.form-label {
        flex: 1;
        flex-grow: 1;
    }
    /deep/ .ant-calendar-picker {
        width: 70%;
        height: 24px;
        flex: auto;
        flex-grow: 0;
    }
}
</style>
