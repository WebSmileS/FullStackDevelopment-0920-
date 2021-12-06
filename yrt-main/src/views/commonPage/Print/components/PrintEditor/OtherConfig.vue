<template>
    <div class="print-editor-other-config">
        <h3 class="print-editor-form-title">编辑器设置</h3>
        <a-row class="print-editor-form-wrapper">
            <a-col span="4">
                <a-form-item label="自动应用设置">
                    <a-switch v-model="otherConfigEditing.autoSync" />
                </a-form-item>
            </a-col>
        </a-row>
        <h3 class="print-editor-form-title">业务设置</h3>
        <a-row class="print-editor-form-wrapper">
            <a-col span="4">
                <form-item-with-helper>
                    <template v-slot:default>
                        <a-switch v-model="otherConfigEditing.keepNegative" />
                    </template>
                    <template v-slot:label>
                        <span>保留负值</span>
                    </template>
                    <template v-slot:helperText>
                        <span>退货类单据的产品数量为负值，如需在打印的时候也显示为负值，则开启此设置项。</span>
                    </template>
                </form-item-with-helper>
            </a-col>
        </a-row>
    </div>
</template>

<script>
import FormItemWithHelper from './FormItemWithHelper.vue';

export default {
    name: 'OtherConfig',
    inject: ['otherConfigEditing'],
    components: {
        FormItemWithHelper
    }
};
</script>
