<template>
    <div class="gallery" :style="styles">
        <a-carousel arrows>
            <div slot="prevArrow" slot-scope="" class="custom-slick-arrow" style="zindex: 1">
                <a-icon type="left" />
            </div>
            <div slot="nextArrow" slot-scope="" class="custom-slick-arrow" style="zindex: 1">
                <a-icon type="right" />
            </div>
            <div v-for="item of images" :key="item.url" class="picture-item">
                <a :href="$Utils.getPictureSrc(item.url)" target="_blank">
                    <pdfPreview
                        v-if="$Utils.checkPdf(item.file_name)"
                        :type="'big'"
                        :url="$Utils.getPictureSrc(item.url)"
                    />
                    <img v-else :src="$Utils.getPictureSrc(item.url)" />
                </a>
            </div>
        </a-carousel>
        <div class="no-data-box" v-if="images.length <= 0">
            <div><a-icon type="file-excel" /></div>
            暂无图片
        </div>
    </div>
</template>

<script>
import pdfPreview from '@/components/pdfPreview';

export default {
    name: 'Gallery',
    components: {
        pdfPreview
    },
    props: {
        styles: {
            type: Object,
            default: () => {}
        },
        images: {
            type: Array,
            default: () => []
        }
    }
};
</script>

<style lang="less" scoped>
// 图片展示栏
div.gallery {
    position: relative;
    background-color: #e0e0e0;
    .picture-item {
        img {
            max-width: 100%;
            max-height: 100%;
        }
    }
    /deep/ .ant-carousel {
        height: 100%;
    }
    /deep/ .slick-slider {
        height: 100%;
        text-align: center;
        overflow: hidden;
    }
    /deep/ .custom-slick-arrow {
        top: 0;
        width: 36px;
        height: 100%;
        margin-top: 0;
        padding-left: 8px;
        padding-right: 8px;
        font-size: 20px;
        &.slick-prev {
            left: 0;
        }
        &.slick-next {
            right: 0;
        }
        &:before {
            content: '';
            display: block;
            width: 36px;
            height: 100%;
            margin-left: -8px;
            background-color: #000;
            opacity: 0;
        }
        &:hover {
            &:before {
                opacity: 0.2;
            }
            i {
                opacity: 0.8;
            }
        }
        i {
            position: absolute;
            top: 50%;
            left: 0;
            right: 0;
            margin-top: -10px;
            color: #fff;
            opacity: 0;
        }
    }
    .no-data-box {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        margin-top: 40%;
        color: #b2b2b2;
        font-size: 26px;
        text-align: center;
        i {
            font-size: 48px;
        }
    }
}
</style>
