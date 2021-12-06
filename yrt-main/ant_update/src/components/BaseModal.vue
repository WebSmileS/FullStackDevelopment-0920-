<template>
    <a-modal
        :destroyOnClose="destroyOnClose"
        class="base-modal scoped modal-form-input-scoped global-drag"
        :class="{ [dragKey]: true, 'my-modal-zoom': fullscreen }"
        :width="fullscreen ? '100%' : width"
        :height="fullscreen ? '100%' : 'auto'"
        :visible="visible"
        :maskClosable="false"
        :centered="true"
        :zIndex="3"
        :footer="$attrs.footer"
        @cancel="handleClose"
    >
        <a-button
            v-if="fullscreenUseable"
            class="fullscreen-button"
            type="primary"
            size="large"
            :icon="fullscreen ? 'fullscreen' : 'fullscreen-exit'"
            @click="onFullscreenClick"
        />
        <div v-globalDrag="{ el: dragKey }" class="modal-header-wrap">
            <h6>
                <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                {{ title }}
            </h6>
        </div>
        <slot name="content" :fullscreen="fullscreen"></slot>
        <template slot="footer">
            <slot name="footer" :fullscreen="fullscreen"></slot>
        </template>
        <!-- 防止模态框在关闭触发后，关闭动画仍未结束前，界面元素仍可触发点击事件 -->
        <div v-show="!visible" class="click-forbidden-if-invisible" @click.stop="() => {}"></div>
    </a-modal>
</template>

<script>
/**
 * 基础模态框，带有项目样式
 * close事件：模态框关闭事件
 */
export default {
    name: 'baseModal',
    props: {
        /**
         * 模态框显隐
         */
        visible: {
            type: Boolean,
            default: false
        },
        /**
         * 宽
         */
        width: {
            type: [Number, String],
            default: 600
        },
        /**
         * 标题
         */
        title: {
            type: String,
            default: '基础弹窗'
        },
        /**
         * 关闭后是否清除子元素
         */
        destroyOnClose: {
            type: Boolean,
            default: false
        },
        /**
         * 拖拽功能使用的唯一标识，
         * 同一时间显示到界面上的拖拽元素标识应该各不相同
         */
        dragKey: {
            type: String,
            default: 'base_md',
            require: true
        },
        /**
         * 是否显示全屏按钮
         */
        fullscreenUseable: {
            type: Boolean,
            default: false
        },
        /**
         * 关闭时回调函数
         */
        onClose: {
            type: Function,
            default: null
        }
    },
    data() {
        return {
            fullscreen: false
        };
    },
    watch: {
        visible: {
            handler(newVal) {
                // 窗口打开时，自动居中
                if (newVal) {
                    this.$Utils.globalDragCenterFn(this.dragKey);
                }
            },
            immediate: true
        }
    },
    methods: {
        onFullscreenClick() {
            if (this.fullscreen) {
                this.fullscreen = false;
                this.$Utils.globalDragCenterFn(this.dragKey);
            } else {
                this.fullscreen = true;
            }
        },
        handleClose() {
            if (this.onClose) {
                this.onClose();
            }
            this.$emit('close');
            this.$nextTick(() => {
                this.fullscreen = false;
            });
        }
    }
};
</script>

<style lang="less" scoped>
@import '~@/styles/global.less';
.fullscreen-button {
    position: absolute;
    z-index: 10;
    top: 0;
    right: 56px;
    height: 48px;
    cursor: pointer;
    background-color: @custom-component-bg;
    &:hover {
        background-color: darken(@custom-component-bg, 5%);
        border-color: darken(@custom-component-bg, 5%);
    }
    &:focus {
        border-color: @custom-component-bg;
    }
}
.click-forbidden-if-invisible {
    position: absolute;
    z-index: 10;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}
</style>
