<template>
    <div class="list-page-header">
        <a-row class="list-page-header-buttons">
            <a-col class="operations-wrapper" :sm="20" :md="22" :lg="18" :xl="18" :xxl="18">
                <!-- 按钮区域 -->
                <div class="operations-group" v-for="(operationGroup, index) in useableOperationGroups" :key="index">
                    <template v-for="operation in operationGroup">
                        <a-button
                            v-if="!buttonsStatu[operation.key].disabled"
                            size="small"
                            :key="operation.key"
                            :type="operation.type"
                            :loading="buttonsStatu[operation.key].loading"
                            @click="onBtnClick(operation.key, $event)"
                        >
                            {{ operation.text }}
                        </a-button>
                        <a-tooltip
                            v-else
                            placement="top"
                            :key="operation.key"
                            :title="buttonsStatu[operation.key].reason"
                        >
                            <a-button size="small" disabled :key="operation.key" :type="operation.type">
                                {{ operation.text }}
                            </a-button>
                        </a-tooltip>
                    </template>
                </div>
            </a-col>
            <a-col class="search-wrapper" :sm="4" :md="2" :lg="6" :xl="6" :xxl="6">
                <!-- 普通搜索 -->
                <template v-if="!expanded">
                    <slot name="singleSearcher"></slot>
                    <div v-if="!$slots.singleSearcher && searchFormItems.length > 0" class="single-search">
                        <a-input-search
                            v-model="searchFormValues[searchFormItems[0].key]"
                            size="small"
                            enterButton
                            class="search"
                            :placeholder="searchFormItems[0].placeholder"
                            @search="handleSearch"
                        />
                    </div>
                </template>
                <div v-if="needMixedSearch" @click.stop="handleToggleSearchStatus" class="search-expands-button">
                    <!--<span>高级搜索</span>-->
                    <a-tooltip placement="top" title="高级搜索">
                        <a-icon :type="expanded ? 'up-circle' : 'down-circle'" />
                    </a-tooltip>
                </div>
            </a-col>
        </a-row>
        <a-row :gutter="16" v-if="needMixedSearch" v-show="expanded" class="search-multiple-wrapper">
            <slot name="searchFormItems"></slot>
            <template v-if="!$slots.searchFormItems">
                <template v-for="(formItem, index) in searchFormItems">
                    <a-col class="search-form-item" v-if="formItem.type === 'text'" :key="index" :span="formItem.span">
                        <span class="form-label">{{ formItem.label }}</span>
                        <a-input
                            v-model="searchFormValues[formItem.key]"
                            type="text"
                            placeholder=""
                            style="width: 70%; height: 24px"
                        />
                    </a-col>
                    <a-col
                        class="search-form-item"
                        v-if="formItem.type === 'select'"
                        :key="index"
                        :span="formItem.span"
                    >
                        <span class="form-label">{{ formItem.label }}</span>
                        <a-select
                            style="width: 70%; height: 24px; flex: auto"
                            v-model="searchFormValues[formItem.key]"
                            size="small"
                            allowClear
                        >
                            <a-select-option
                                v-for="(option, index) in formItem.options"
                                :key="index"
                                :value="option.value"
                            >
                                {{ option.label }}
                            </a-select-option>
                        </a-select>
                    </a-col>
                    <a-col
                        class="search-form-item"
                        v-if="formItem.type === 'daterange'"
                        :key="index"
                        :span="formItem.span"
                    >
                        <span class="form-label">{{ formItem.label }}</span>
                        <a-range-picker
                            size="small"
                            :placeholder="['开始', '结束']"
                            @change="(dates, dateStrings) => onDateChange(formItem, dates, dateStrings)"
                        ></a-range-picker>
                    </a-col>
                </template>
            </template>
            <a-col class="search-form-button" :span="6" style="float: right; text-align: right">
                <a-button @click="handleSearch" size="small" type="primary"> 查询 </a-button>
            </a-col>
        </a-row>
    </div>
</template>

<script>
/**
 * 列表页顶部，提供统一的样式，配合ListPageLayout使用
 * search事件：用户点击搜索时触发此事件，事件携带当前searchValues
 * btnClick事件： 按钮点击事件，事件携带操作按钮的唯一标识key
 * expandsChange事件： 高级搜索栏展开状态发生改变时触发此事件
 */
export default {
    name: 'ListPageHeader',
    props: {
        /**
         * 操作按钮配置项,
         * 数组的第一维代表按钮分组，
         * 数组的第二维代表按钮，
         * 每个按钮项带有key（唯一标识）、text（文本）、type（ant按钮类型）三项属性
         */
        operationGroups: {
            type: Array,
            default: () => [
                [
                    { key: 'add', text: '新增', type: 'primary' },
                    { key: 'edit', text: '修改', type: 'primary' },
                    { key: 'confirm', text: '确认', type: 'primary' },
                    { key: 'nullify', text: '作废', type: 'danger' }
                ],
                [{ key: 'delete', text: '删除', type: 'danger' }]
            ]
        },
        /**
         * 按钮状态描述对象,
         * 将按钮的“唯一标识”（operationGroups中的key）作为键，
         * 每个按钮的状态包含disabled（是否禁用）、reason（禁用时的提示文本）、loading（加载状态）三项属性
         */
        buttonsStatu: {
            type: Object,
            default: () => ({
                add: { disabled: true, reason: undefined },
                edit: { disabled: true, reason: undefined },
                confirm: { disabled: true },
                delete: { disabled: true },
                nullify: { disabled: true }
            })
        },
        /**
         * 禁用状态的按钮是否显示在界面上
         */
        disabledButtonVisible: {
            type: Boolean,
            defualt: false
        },
        /**
         * 是否开启高级搜索功能
         */
        needMixedSearch: {
            type: Boolean,
            default: false
        },
        /**
         * 搜索表单项描述数组
         * @todo 将弃用，统一使用singleSearcher插槽和searchFormItems插槽
         */
        searchFormItems: {
            type: Array,
            default: () => []
        }
    },
    data() {
        return {
            expanded: false,
            searchFormValues: {}
        };
    },
    computed: {
        /**
         * 过滤操作按钮，被禁用状态的按钮将被过滤
         */
        useableOperationGroups() {
            if (this.disabledButtonVisible) {
                return this.operationGroups;
            }
            let result = [];
            this.operationGroups.forEach((group) => {
                let newGroup = [];
                group.forEach((operation) => {
                    if (this.buttonsStatu[operation.key] && !this.buttonsStatu[operation.key].disabled) {
                        newGroup.push(operation);
                    }
                });
                if (newGroup.length) {
                    result.push(newGroup);
                }
            });
            return result;
        }
    },
    methods: {
        handleToggleSearchStatus() {
            this.expanded = !this.expanded;
            this.$emit('expandsChange', this.expanded);
        },
        handleSearch() {
            const { needMixedSearch, expanded, searchFormValues, searchFormItems } = this;
            // 需要复杂搜索并且复杂搜索条展开时才传递所有的搜索参数，否则只通过租户名搜索
            let searchValues = {};
            if (needMixedSearch && expanded) {
                searchValues = { ...searchFormValues };
            } else {
                searchValues[searchFormItems[0].key] = searchFormValues[searchFormItems[0].key];
            }
            this.$emit('search', searchValues, expanded);
        },
        /**
         * @param {string} type 点击事件对应的按钮类型
         * @param {object} event 事件对象
         */
        onBtnClick(type, event) {
            this.$emit('btnClick', type, event);
        },

        onDateChange(formItem, dates) {
            this.searchFormValues[formItem.keys[0]] = dates[0];
            this.searchFormValues[formItem.keys[1]] = dates[1];
        }
    }
};
</script>

<style lang="less" scoped>
.list-page-header {
    min-height: 48px;
    padding: 17px;
    border-radius: 4px;
    background-color: #fff;
}
.operations-wrapper {
    display: flex;
    justify-content: flex-start;
    .operations-group {
        padding-left: 16px;
        text-align: left;
        border-left: 1px solid #dfe0df;
        button {
            min-width: 76px;
            height: 24px;
            margin-right: 16px;
        }
    }
    .operations-group:first-child {
        padding-left: 0;
        border-left: none;
    }
}
.search-wrapper {
    display: flex;
    justify-content: flex-end;
    align-items: right;
    /deep/ .single-search {
        flex-grow: 1;
    }
    .search-expands-button {
        flex-grow: 0;
        margin-left: 16px;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.65);
        opacity: 0.44;
        .anticon {
            font-size: 24px;
        }
        &:hover {
            color: #1890ff;
            opacity: 1;
            cursor: pointer;
        }
    }
}
.search-multiple-wrapper {
    margin-top: 24px;
    /deep/ .search-form-item {
        display: flex;
        justify-content: space-between;
        margin-bottom: 14px;
    }
    /deep/ span.form-label {
        flex-grow: 1;
        text-align: right;
        margin-right: 20px;
        white-space: nowrap;
    }
    /deep/ input {
        height: 24px;
    }
}
</style>
