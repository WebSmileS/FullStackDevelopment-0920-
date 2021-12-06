<template>
    <div class="print-editor-base-config">
        <h3 class="print-editor-form-title">纸型设置</h3>
        <a-row class="print-editor-form-wrapper" :gutter="24">
            <a-col span="4">
                <a-form-item label="打印纸类型">
                    <a-select
                        :options="paperTypeOptions"
                        v-model="configEditing.paperType"
                        @change="handleAutoSetPaperSize"
                    />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="度量单位">
                    <a-select :options="unitOptions" v-model="configEditing.unit" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="打印纸宽度">
                    <a-input-number v-model="configEditing.paperWidth" :min="0" @change="handleAutoSetPaperType" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="打印纸高度">
                    <a-input-number v-model="configEditing.paperHeight" :min="0" @change="handleAutoSetPaperType" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="实际打印宽度">
                    <a-input-number
                        disabled
                        :value="configEditing.paperWidth - configEditing.safeMarginLeft - configEditing.safeMarginRight"
                    />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="实际打印高度">
                    <a-input-number
                        disabled
                        :value="
                            configEditing.paperHeight - configEditing.safeMarginTop - configEditing.safeMarginBottom
                        "
                    />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="上安全距离">
                    <a-input-number v-model="configEditing.safeMarginTop" :min="0" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="下安全距离">
                    <a-input-number v-model="configEditing.safeMarginBottom" :min="0" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="左安全距离">
                    <a-input-number v-model="configEditing.safeMarginLeft" :min="0" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="右安全距离">
                    <a-input-number v-model="configEditing.safeMarginRight" :min="0" />
                </a-form-item>
            </a-col>
        </a-row>
        <h3 class="print-editor-form-title">版面设置</h3>
        <a-row class="print-editor-form-wrapper" :gutter="24">
            <a-col span="4">
                <a-form-item label="上填充">
                    <a-input-number v-model="configEditing.paddingTop" :min="0" :max="100" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="下填充">
                    <a-input-number v-model="configEditing.paddingBottom" :min="0" :max="100" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="左填充">
                    <a-input-number v-model="configEditing.paddingLeft" :min="0" :max="100" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="右填充">
                    <a-input-number v-model="configEditing.paddingRight" :min="0" :max="100" />
                </a-form-item>
            </a-col>
        </a-row>
        <h3 class="print-editor-form-title">标题设置</h3>
        <a-row class="print-editor-form-wrapper" :gutter="24">
            <a-col span="4">
                <a-form-item label="标题字体大小">
                    <a-input-number v-model="configEditing.titleFontSize" :min="0" :step="0.1" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="标题字体行高">
                    <a-input-number v-model="configEditing.titleLineHeight" :min="0" :step="0.1" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="标题字体间距">
                    <a-input-number v-model="configEditing.titleLetterSpacing" :min="0" :step="0.1" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="标题字体加粗">
                    <a-switch v-model="configEditing.titleFontBold" />
                </a-form-item>
            </a-col>
        </a-row>
        <h3 class="print-editor-form-title">正文设置</h3>
        <a-row class="print-editor-form-wrapper" :gutter="24">
            <a-col span="4">
                <a-form-item label="正文字体大小">
                    <a-input-number v-model="configEditing.bodyFontSize" :min="0" :step="0.1" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="正文字体行高">
                    <a-input-number v-model="configEditing.bodyLineHeight" :min="0" :step="0.1" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="正文字体间距">
                    <a-input-number v-model="configEditing.bodyLetterSpacing" :min="0" :step="0.1" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="正文字体加粗">
                    <a-switch v-model="configEditing.bodyFontBold" />
                </a-form-item>
            </a-col>
        </a-row>
        <h3 class="print-editor-form-title">数值设置</h3>
        <a-row class="print-editor-form-wrapper" :gutter="24">
            <a-col span="4">
                <a-form-item label="数值精度">
                    <a-input-number :min="0" :max="4" :step="1" :precision="0" v-model="configEditing.precision" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="千位分隔符">
                    <a-switch v-model="configEditing.thousandSeparator" />
                </a-form-item>
            </a-col>
        </a-row>
    </div>
</template>

<script>
import { unitTypeMap, paperTypeMap, paperSizeMap } from '../../type';

export default {
    name: 'BaseConfig',
    inject: {
        configEditing: {
            from: 'baseConfigEditing'
        }
    },
    computed: {
        unitOptions() {
            const options = [];
            Object.keys(unitTypeMap).forEach((key) =>
                options.push({ label: unitTypeMap[key].text, value: unitTypeMap[key].value })
            );
            return options;
        },
        paperTypeOptions() {
            const options = [];
            Object.keys(paperTypeMap).forEach((key) =>
                options.push({ label: paperTypeMap[key].text, value: paperTypeMap[key].value })
            );
            return options;
        }
    },
    methods: {
        /**
         * 用户手动更改纸型时，自动将宽、高和单位设置为该纸型对应的值
         */
        handleAutoSetPaperSize() {
            const { paperType } = this.configEditing;
            if (paperType !== paperTypeMap.custom.value) {
                const { width, height, unit } = paperSizeMap[paperType];
                this.configEditing.paperWidth = width;
                this.configEditing.paperHeight = height;
                this.configEditing.unit = unit;
            }
        },
        /**
         * 用户手动更改宽、高和单位时，自动变更纸型为custom
         */
        handleAutoSetPaperType() {
            this.configEditing.paperType = paperTypeMap.custom.value;
        }
    }
};
</script>
