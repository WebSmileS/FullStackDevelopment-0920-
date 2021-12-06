<template>
    <div class="form-footer">
        <a-row :gutter="24" class="form-row">
            <a-col class="form-col">
                <span class="form-label"><span class="form-text">摘要</span></span>
                <a-input size="small" class="form-input" v-model="formData.description" :disabled="isReadonly" />
            </a-col>
        </a-row>
        <a-row :gutter="24" class="form-row">
            <a-col :span="4" class="form-col" style="padding-right: 0">
                <span class="form-label"><span class="form-text">制单人</span></span>
                <span size="small" class="form-input">{{ formData.created_by }}</span>
            </a-col>
            <a-col :span="4" class="form-col" style="padding-right: 0">
                <span class="form-label"><span class="form-text">生成时间</span></span>
                <span size="small" class="form-input">{{ formData.created_time }}</span>
            </a-col>
            <a-col :span="3" class="form-col" style="padding-right: 0">
                <span class="form-label"><span class="form-text">审核人</span></span>
                <span size="small" class="form-input">{{ formData.confirm_by }}</span>
            </a-col>
            <a-col :span="5" class="form-col" style="padding-right: 0">
                <span class="form-label"><span class="form-text">审核时间</span></span>
                <span size="small" class="form-input">{{ formData.confirm_time }}</span>
            </a-col>
            <a-col :span="4" class="form-col" v-if="isShowPayment" style="padding-right: 0">
                <span class="form-label"
                    ><span class="form-text">{{ paymentLabel }}人</span></span
                >
                <span size="small" class="form-input">{{ formData.payment_by || formData.receive_by }}</span>
            </a-col>
            <a-col :span="4" class="form-col" style="padding-right: 0" v-if="isShowPayment">
                <span class="form-label"
                    ><span class="form-text">{{ paymentLabel }}时间</span></span
                >
                <span size="small" class="form-input">{{ formData.payment_time || formData.receive_time }}</span>
            </a-col>
        </a-row>
        <a-row v-if="isShowCancel" :gutter="24" class="form-row">
            <a-col :span="4" class="form-col">
                <span class="form-label"><span class="form-text">作废人</span></span>
                <span size="small" class="form-input">{{ formData.cancel_by }}</span>
            </a-col>
            <a-col :span="4" class="form-col">
                <span class="form-label"><span class="form-text">作废时间</span></span>
                <span size="small" class="form-input">{{ formData.cancel_time }}</span>
            </a-col>
            <a-col :span="16" class="form-col">
                <span class="form-label"><span class="form-text">作废原因</span></span>
                <span size="small" class="form-input">{{ formData.cancel_reason }}</span>
            </a-col>
        </a-row>
    </div>
</template>

<script>
export default {
    name: 'invoiceFooter', // 入库|出库发票的模态框下半部分表单
    props: {
        formData: {
            // 数据
            type: Object,
            required: true
        },
        isReadonly: {
            // 是否只读
            type: Boolean,
            default: false
        },
        isShowPayment: {
            // 是否显示付款相关字段
            type: Boolean,
            default: false
        },
        paymentLabel: {
            // 付款标题 = '付款' | '收款'，与上方的isShowPayment字段配套展示
            type: String,
            default: ''
        },
        isShowCancel: {
            // 是否显示作废相关字段
            type: Boolean,
            default: false
        }
    }
};
</script>

<style lang="less" scoped></style>
