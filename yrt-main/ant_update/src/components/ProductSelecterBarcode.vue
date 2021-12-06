<template>
    <div>
        <div class="barcode-input-wrapper">
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
            <a-button size="small" type="primary" @click="onBarcodeInputFinish">手动查询</a-button>
        </div>
        <div style="margin-top: 14px">
            <p>
                <span>扫码结果：</span>
                <span>{{ barcode }}</span>
            </p>
            <vxe-table
                border
                show-overflow
                show-header-overflow
                resizable
                auto-resize
                ref="searchList"
                height="200"
                size="small"
                align="center"
                :loading="searchLoading"
                :data="searchResult"
                :checkbox-config="{ range: true }"
                @cell-click="onSearchResultClick"
            >
                <template v-for="column in columns">
                    <vxe-table-column v-bind="column" :key="column.field"></vxe-table-column>
                </template>
                <template slot="empty">无匹配的扫码结果</template>
            </vxe-table>
        </div>
        <div style="margin-top: 24px">
            <p>待添加产品：</p>
            <vxe-table
                border
                show-overflow
                show-header-overflow
                resizable
                auto-resize
                ref="addList"
                height="200"
                size="small"
                align="center"
                :data="addList"
                :checkbox-config="{ range: true }"
            >
                <vxe-table-column v-bind="tableColumnConfig.select" type="checkbox"></vxe-table-column>
                <template v-for="column in columns">
                    <vxe-table-column v-bind="column" :key="column.dataIndex"></vxe-table-column>
                </template>
                <template slot="empty">请选择扫码结果中的产品</template>
            </vxe-table>
        </div>
    </div>
</template>

<script>
import tableColumnConfig from '@/config/tableColumnConfig';

/**
 * 扫码产品选择器
 */
export default {
    name: 'ProductSelecterBarcode',
    props: {
        /**
         * 列配置，参考vxe-table-column的属性
         */
        columns: {
            type: Array,
            default: () => []
        },
        /**
         * 条码查询的loading状态
         */
        searchLoading: {
            type: Boolean,
            default: false
        },
        /**
         * 当前条码查询的结果
         */
        searchResult: {
            type: Array,
            default: () => []
        }
    },
    data() {
        return {
            tableColumnConfig,
            barcodeInputing: undefined,
            barcode: '',
            addList: []
        };
    },
    watch: {
        searchLoading(newValue) {
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
        onSearchResultClick({ row, rowIndex }) {
            const newProduct = { ...row, _XID: undefined };
            this.addList.push(newProduct);
            this.$emit(
                'searchResultChange',
                this.searchResult.filter((_, index) => index !== rowIndex)
            );
            this.$nextTick(() => {
                this.$refs.addList.setCheckboxRow(this.addList[this.addList.length - 1], true);
            });
        },
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
            this.barcode = this.barcodeInputing && this.barcodeInputing.split(' ').join(''); // 去除所有空格
            this.barcodeInputing = undefined;
            this.$refs.barcodeInput.blur();
            this.handleSearch();
        },
        handleInit() {
            this.barcode = undefined;
            this.barcodeInputing = undefined;
            this.addList = [];
            if (!this.searchResult || this.searchResult.length) {
                this.$emit('searchResultChange', []);
            }
        },
        handleSearch() {
            if (typeof this.barcode !== 'string' || this.barcode.length <= 0) {
                this.$message.warning('条码不能为空');
                return;
            }
            this.$emit('search', this.barcode);
        },
        handleSubmit() {
            const selected = this.$refs.addList.getCheckboxRecords();
            if (!selected.length) {
                this.$message.warning('请选择待添加产品');
                return;
            }
            this.$emit('submit', { barcodeProducts: selected });
        }
    }
};
</script>

<style lang="less" scoped>
.barcode-input-wrapper {
    display: flex;
    /deep/ .ant-input {
        flex-grow: 1;
    }
    /deep/ .ant-btn {
        flex-grow: 0;
        width: 80px;
        margin-left: 14px;
    }
}
</style>
