<template>
    <base-modal
        v-bind="$attrs"
        title="选择产品"
        dragKey="product_selecter_md"
        :width="1100"
        :visible="visible"
        :onClose="handleClose"
    >
        <template slot="content">
            <slot name="searchForm"></slot>
            <a-tabs style="margin-top: 12px" type="card" v-model="activeTabKey">
                <template v-for="tab in tabs">
                    <a-tab-pane :key="tab.key" :tab="tab.title">
                        <slot :name="tab.key"></slot>
                    </a-tab-pane>
                </template>
            </a-tabs>
        </template>
        <template slot="footer">
            <a-button type="default" @click="handleClose">取消</a-button>
            <a-button type="primary" @click="onAddButtonClick">添加</a-button>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal';

/**
 * 产品选择器的容器，提供统一的样式和模态框，
 * 将自行定义的产品选择组件通过插槽置入此容器组件，同时在props中指定相对应的tab,
 * 自行定义的产品选择组件应该实现handleSubmit方法，
 * 用户选择产品时，将调用当前激活tab项所对应的产品选择组件的handleSubmit方法
 * close事件：关闭时触发此事件
 */
export default {
    name: 'ProductSelecterModal',
    components: {
        BaseModal
    },
    props: {
        /**
         * 选择器显隐
         */
        visible: {
            type: Boolean,
            default: false
        },
        /**
         * 选择器tab项
         * @type {object} tab
         * @property {string} key tab项的唯一key，与插槽名保持一致
         * @property {string} title tab项的标题
         */
        tabs: {
            type: Array,
            default: () => []
        }
    },
    data() {
        return {
            activeTabKey: this.$props.tabs[0] ? this.$props.tabs[0].key : undefined
        };
    },
    watch: {
        tabs(newValue) {
            if (newValue) {
                this.activeTabKey = this.$props.tabs[0] ? this.$props.tabs[0].key : undefined;
            }
        }
    },
    methods: {
        onAddButtonClick() {
            const key = this.activeTabKey;
            if (typeof key === 'string') {
                if (
                    this.$slots &&
                    this.$slots[key] &&
                    this.$slots[key][0] &&
                    this.$slots[key][0].componentInstance &&
                    this.$slots[key][0].componentInstance.handleSubmit
                ) {
                    if (this.$slots[key][0].componentInstance.handleSubmit()) {
                        this.$emit('close');
                    }
                    return;
                }
            }
            console.error(new Error(`未正确配置组件插槽:${key}`));
        },

        handleClose() {
            this.$emit('close');
        }
    }
};
</script>
