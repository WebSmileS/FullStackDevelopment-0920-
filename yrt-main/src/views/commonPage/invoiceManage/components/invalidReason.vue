<template>
    <div>
        <a-modal
            :title="invalidReasonMd.title"
            v-model="isOpen"
            :maskClosable="false"
            :width="400"
            :centered="true"
            :mask="false"
            @cancel="invalidReasonFn('CLOSE')"
            class="modal-form-input-scoped global-drag finnance-invoice-cancel-reason-modal"
        >
            <div v-globalDrag="{ el: 'finnance-invoice-cancel-reason-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ invalidReasonMd.title }} - 作废原因
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="invalidReasonFn('CLOSE')">取消</a-button>
                <a-button type="primary" @click="invalidReasonFn('SAVE')">确定</a-button>
            </template>
            <div class="form-container">
                <a-textarea
                    placeholder="请输入作废原因"
                    :auto-size="{ minRows: 5, maxRows: 8 }"
                    v-model="reasonData"
                    @blur="onInputBlur"
                />
            </div>
        </a-modal>
    </div>
</template>

<script>
export default {
    name: 'invalidReasonMd', // 发票作废原因 = 财务人员使用
    props: {
        isOpen: {
            // 模态框窗口是否打开标识， true、false
            type: Boolean,
            default: true
        }
    },
    data() {
        return {
            invalidReasonMd: {
                title: `${this.$route.meta.title}`,
                auditLoading: false,
                cancelLoading: false
            },
            reasonData: ''
        };
    },
    methods: {
        onInputBlur() {
            // 去除字符串两端的空格
            this.reasonData = this.$XEUtils.trim(this.reasonData);
        },
        invalidReasonFn(action) {
            if (action === 'CLOSE') {
                this.$emit('saveInvalidReasonData');
            } else if (action === 'SAVE') {
                if (!this.reasonData) {
                    this.$warning({
                        title: '操作提示',
                        content: `请填写作废原因`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                } else {
                    this.$emit('saveInvalidReasonData', this.reasonData);
                }
            }

            this.reasonData = '';
        }
    },
    watch: {
        isOpen: {
            handler(newVal) {
                if (newVal) {
                    this.$Utils.globalDragCenterFn('finnance-invoice-cancel-reason-modal');
                }
            },
            immediate: true
        }
    }
};
</script>

<style lang="less" scoped></style>
