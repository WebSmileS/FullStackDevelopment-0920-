<template>
    <div class="base-pdf" :style="{ width, height, fontSize }">
        <a-icon type="file-pdf" />
    </div>
</template>

<script>
export default {
    name: 'BasePdf',
    props: {
        width: {
            type: String,
            default: '172px'
        },
        height: {
            type: String,
            default: '172px'
        }
    },
    computed: {
        fontSize() {
            const widthStrings = this.width.match(/^\d*\.?\d+/g);
            const heightStrings = this.height.match(/^\d*\.?\d+/g);
            const width = widthStrings ? parseFloat(widthStrings[0]) : 12;
            const height = heightStrings ? parseFloat(heightStrings[0]) : 12;
            return `${Math.min(width, height) * 0.6}px`;
        }
    }
};
</script>

<style lang="less">
.base-pdf {
    text-align: center;
    color: #bb5252;
    i {
        font-size: 1em;
        vertical-align: middle;
    }
}
</style>
