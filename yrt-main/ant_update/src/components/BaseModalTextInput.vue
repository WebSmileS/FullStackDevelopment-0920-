<template>
    <base-modal
        :destroyOnClose="$attrs.destroyOnClose"
        :title="title"
        :dragKey="dragKey"
        :visible="visible"
        :width="width"
        @close="handleClose"
    >
        <template slot="content">
            <a-textarea
                style="margin-top: 12px"
                :placeholder="placeholder"
                :auto-size="{ minRows: 5, maxRows: 8 }"
                v-model="text"
                @blur="onInputBlur"
            />
        </template>
        <template slot="footer">
            <a-button type="default" @click="handleClose">{{ cancelText }}</a-button>
            <a-button type="primary" @click="handleSubmit" :loading="submitLoading">{{ okText }}</a-button>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal';

/**
 * 带有一个文本输入框的模态框
 * close事件：关闭事件
 * submit事件：用户点击确定按钮时提交数据事件
 */
export default {
    name: 'BaseModalTextInput',
    components: {
        BaseModal
    },
    props: {
        /**
         * 模态框显隐
         */
        visible: {
            type: Boolean,
            default: false
        },
        /**
         * 宽
         */
        width: {
            type: Number,
            default: 600
        },
        /**
         * 标题
         */
        title: {
            type: String,
            default: '文本输入'
        },
        /**
         * 输入框placeholder
         */
        placeholder: {
            type: String,
            default: '请输入描述'
        },
        /**
         * 拖拽功能使用的唯一标识，
         * 同一时间显示到界面上的拖拽元素标识应该各不相同
         */
        dragKey: {
            type: String,
            default: 'base_modal_text_input_md'
        },
        /**
         * 关闭按钮的文本
         */
        cancelText: {
            type: String,
            default: '取消'
        },
        /**
         * 确认按钮的文本
         */
        okText: {
            type: String,
            default: '确认'
        }
    },
    data() {
        return {
            submitLoading: false,
            text: ''
        };
    },
    watch: {
        visible(newValue) {
            // 重置状态
            if (!newValue && this.$attrs.destroyOnClose !== undefined && this.$attrs.destroyOnClose !== false) {
                this.submitLoading = false;
                this.text = '';
            }
        }
    },
    methods: {
        onInputBlur() {
            // 去除字符串两端的空格
            this.text = this.$XEUtils.trim(this.text);
        },

        /**
         * 关闭
         */
        handleClose() {
            this.$emit('close');
        },

        /**
         * 提交
         */
        handleSubmit() {
            this.submitLoading = true;
            this.$emit('submit', this.text, (success) => {
                if (success) {
                    this.$emit('close');
                }
                this.submitLoading = false;
            });
        }
    }
};
</script>
