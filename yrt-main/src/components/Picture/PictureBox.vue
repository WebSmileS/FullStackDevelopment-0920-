<template>
    <div :class="`picture-box ${className}`" :style="styles">
        <base-image
            v-if="fileType === 'image'"
            :width="imageWidth"
            :height="imageHeight"
            :host="host"
            :resourceKey="resourceKey"
            :src="src"
            :isThumbnail="isThumbnail"
        />
        <base-pdf v-if="fileType === 'pdf'" :width="imageWidth" :height="imageHeight" />
        <slot name="default"></slot>
        <div class="picture-box-mask" @click.stop="$emit('maskclick', { ...$props })">
            <a-icon
                v-if="previewAble"
                class="picture-box-mask-preview"
                @click.stop="$emit('preview', { ...$props })"
                type="zoom-in"
            />
            <a-icon
                v-if="deleteAble"
                class="picture-box-mask-delete"
                @click.stop="$emit('delete', { ...$props })"
                type="delete"
            />
            <slot :name="`maskButtons${$vnode.key === undefined ? '' : $vnode.key}`"></slot>
        </div>
    </div>
</template>

<script>
import { pictureAPIHost } from '@/config/constants';
import BaseImage from './BaseImage';
import BasePdf from './BasePdf';

export default {
    name: 'PictureBox',
    components: {
        BaseImage,
        BasePdf
    },
    props: {
        className: {
            type: String,
            default: ''
        },
        styles: {
            type: Object
        },
        imageWidth: {
            type: String
        },
        imageHeight: {
            type: String
        },
        fileType: {
            type: String,
            default: 'image'
        },
        src: {
            type: String
        },
        host: {
            type: String,
            default: pictureAPIHost
        },
        resourceKey: {
            type: String
        },
        isThumbnail: {
            type: Boolean,
            default: true
        },
        previewAble: {
            type: Boolean,
            default: true
        },
        deleteAble: {
            type: Boolean,
            default: true
        }
    }
};
</script>

<style lang="less">
@prefix: ~'picture-box';
.@{prefix} {
    display: inline-block;
    vertical-align: top;
    margin-right: 20px;
    margin-bottom: 20px;
    cursor: pointer;
    position: relative;
    &-mask {
        position: absolute;
        width: 100%;
        height: 100%;
        background: #3d3d3d;
        top: 0;
        opacity: 0.7;
        color: #fff;
        transition: opacity 0.5s;
        -moz-transition: opacity 0.5s;
        -webkit-transition: opacity 0.5s;
        -o-transition: opacity 0.5s;
        opacity: 0;
        text-align: center;
        @iconSize: 26px;
        i {
            text-align: center;
            cursor: pointer;
            &:hover {
                transform: scale(1.1);
            }
        }
        &-preview {
            position: absolute;
            top: 50%;
            left: 50%;
            margin-top: -@iconSize / 2;
            margin-left: -@iconSize / 2;
            font-size: @iconSize + 8px;
        }
        &-delete {
            position: absolute;
            top: 5px;
            right: 5px;
            font-size: @iconSize;
        }
    }
    &:hover {
        .@{prefix}-mask {
            opacity: 0.7;
        }
    }
}
</style>
