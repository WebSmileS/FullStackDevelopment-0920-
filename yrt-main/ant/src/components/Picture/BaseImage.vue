<template>
    <div class="base-image" :style="theStyle">
        <a-spin v-show="loading" />
    </div>
</template>

<script>
export default {
    name: 'BaseImage',
    props: {
        src: {
            type: String,
            required: true
        },
        host: {
            type: String
        },
        resourceKey: {
            type: String
        },
        isThumbnail: {
            type: Boolean,
            default: true
        },
        thumbnailSuffix: {
            type: String,
            default: '-200'
        },
        width: {
            type: String,
            default: '172px'
        },
        height: {
            type: String,
            default: '172px'
        }
    },
    data() {
        return {
            loading: true
        };
    },
    computed: {
        realSrc() {
            let src = this.src === undefined ? `${this.host}/${this.resourceKey}` : this.src;
            if (this.isThumbnail) {
                return `${src}${this.thumbnailSuffix}`;
            }
            return src;
        },
        theStyle() {
            const { width, height, realSrc } = this;
            return {
                width,
                height,
                lineHeight: height,
                backgroundImage: `url(${realSrc})`
            };
        }
    },
    created() {
        const img = document.createElement('img');
        img.onload = () => {
            this.loading = false;
        };
        img.src = this.realSrc;
    }
};
</script>

<style lang="less">
.base-image {
    overflow: hidden;
    text-align: center;
    background-position: center;
    background-repeat: no-repeat;
    background-size: cover;
    background-color: #e8eaec;
    img {
        width: auto;
    }
}
</style>
