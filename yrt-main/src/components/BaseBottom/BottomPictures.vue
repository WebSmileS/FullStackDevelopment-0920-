<template>
    <div class="scroll-vertical-wrapper" :style="styles">
        <div class="scroll-vertical-content">
            <template v-for="(item, index) of list">
                <!-- PDF文件 -->
                <picture-box
                    v-if="$Utils.checkPdf(item.file_name)"
                    fileType="pdf"
                    :key="index"
                    :resourceKey="item[resourceProperty]"
                    :deleteAble="deleteAble"
                    @preview="previewPdf($Utils.getPictureSrc(item[resourceProperty]))"
                    @delete="$emit('delete', item)"
                />
                <!-- 非PDF文件 -->
                <picture-box
                    v-else
                    :key="index"
                    :resourceKey="item[resourceProperty]"
                    :deleteAble="deleteAble"
                    @preview="$emit('previewImg', $Utils.getPictureSrc(item[resourceProperty]))"
                    @delete="$emit('delete', item)"
                />
            </template>
            <div class="no-data-box" v-if="!list || list.length <= 0">
                <div><a-icon type="file-excel" /></div>
                暂无图片
            </div>
        </div>
    </div>
</template>

<script>
import PictureBox from '@/components/Picture/PictureBox';

export default {
    name: 'BottomPictures',
    components: {
        PictureBox
    },
    props: {
        styles: {
            type: Object
        },
        list: {
            type: Array
        },
        deleteAble: {
            type: Boolean,
            default: true
        },
        resourceProperty: {
            type: String,
            default: 'url'
        }
    },
    methods: {
        previewPdf(src) {
            window.open(src, '_blank');
        }
    }
};
</script>
