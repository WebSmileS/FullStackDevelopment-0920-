<template>
    <div class="form-caption">
        <h3>{{ captionData.caption }}</h3>
        <a-row v-if="captionData.value" :gutter="24" class="form-row">
            <a-col class="form-col">
                <span class="form-label" style="width: auto; padding-right: 10px"
                    ><span class="form-text">{{ captionData.label }}</span></span
                >
                <span class="form-input">
                    {{ captionData.value }}
                </span>
            </a-col>
        </a-row>
    </div>
</template>

<script>
export default {
    name: 'captionText', // 模态框的标题文字区域
    props: {
        captionData: {
            type: Object, // 标题文字右侧的数据 [标题 + 数据]
            required: true
        }
    }
};
</script>

<style lang="less" scoped></style>
