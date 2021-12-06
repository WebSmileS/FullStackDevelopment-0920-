<template>
    <a-spin :spinning="loading">
        <a-input
            ref="barcodeInput"
            size="small"
            v-mountedFocus
            placeholder="请将输入光标定位到此处后再进行扫码"
            :value="barcodeInputing"
            @change="onBarcodeInputChange"
            @pressEnter="onBarcodeInputFinish"
            @focus="onBarcodeInputFocus"
        ></a-input>
    </a-spin>
</template>

<script>
/**
 * 扫码输入框
 * search事件： 扫码完成时触发此事件
 */
export default {
    name: 'BarcodeInputer',
    props: {
        /**
         * 加载状态
         */
        loading: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            barcodeInputing: undefined,
            barcode: '',
            addList: []
        };
    },
    watch: {
        loading(newValue) {
            if (!newValue) {
                this.$refs.barcodeInput.focus();
            }
        }
    },
    directives: {
        mountedFocus: {
            inserted(el) {
                el.focus();
            }
        }
    },
    methods: {
        onBarcodeInputFocus() {
            this.$nextTick(() => {
                this.$refs.barcodeInput.select();
            });
        },
        onBarcodeInputChange(event) {
            this.barcodeInputing = event.currentTarget.value;
        },
        onBarcodeInputFinish(event) {
            event.stopPropagation();
            if (!this.barcodeInputing) {
                return;
            }
            this.barcode = this.barcodeInputing;
            this.barcodeInputing = undefined;
            this.$refs.barcodeInput.blur();
            this.handleSearch();
        },
        handleInit() {
            this.barcode = undefined;
            this.barcodeInputing = undefined;
        },
        handleSearch() {
            this.$emit('search', this.barcode);
        }
    }
};
</script>
