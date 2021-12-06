<template>
    <base-modal
        dragKey="item_selecter_table_md"
        :destroyOnClose="$attrs.destroyOnClose"
        :title="title"
        :width="1100"
        :visible="visible"
        :onClose="handleClose"
    >
        <template slot="content">
            <div style="margin-top: 14px">
                <p>待选（{{ toSelect.length }}项）：</p>
                <vxe-table
                    border
                    show-overflow
                    show-header-overflow
                    resizable
                    auto-resize
                    ref="toSelect"
                    height="200"
                    size="small"
                    align="center"
                    :data="toSelect"
                    @cell-click="onToSelectCellClick"
                >
                    <template v-for="column in columns">
                        <vxe-table-column v-bind="column" :key="column.field"></vxe-table-column>
                    </template>
                    <template slot="empty">待选列表为空</template>
                </vxe-table>
            </div>
            <div style="margin-top: 24px">
                <p>已选（{{ selected.length }}项）：</p>
                <vxe-table
                    border
                    show-overflow
                    show-header-overflow
                    resizable
                    auto-resize
                    ref="selected"
                    height="200"
                    size="small"
                    align="center"
                    :data="selected"
                    :checkbox-config="{ range: true }"
                    @cell-click="onSelectedCellClick"
                >
                    <template v-for="column in columns">
                        <vxe-table-column v-bind="column" :key="column.field"></vxe-table-column>
                    </template>
                    <template slot="empty">请从待选列表中选择</template>
                </vxe-table>
            </div>
        </template>
        <template slot="footer">
            <a-button type="default" @click="handleClose">取消</a-button>
            <a-button type="primary" @click="onOkButtonClick">确认</a-button>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal';

/**
 * 表格选择模态框
 * 考虑弃用
 * @todo 从项目移除
 */
export default {
    name: 'ItemSelecterTableModal',
    components: {
        BaseModal
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        title: {
            type: String,
            default: '请选择'
        },
        columns: {
            type: Array,
            default: () => []
        },
        data: {
            type: Array,
            default: () => []
        },
        // reverse=false，初始时所有项都在待选列表中，reverse=true，初始时所有项都在已选列表中
        reverse: {
            type: Boolean,
            default: false
        }
    },
    data() {
        const defaultData = this.$props.data.map((item, index) => ({ ...item, _itemSelecterIndex: index }));
        return {
            toSelect: this.$props.reverse ? [] : defaultData,
            selected: this.$props.reverse ? defaultData : []
        };
    },
    watch: {
        visible(newValue) {
            if (newValue) {
                this.toSelect = this.$props.data.map((item, index) => ({ ...item, _itemSelecterIndex: index }));
                this.selected = [];
            }
        }
    },
    methods: {
        onOkButtonClick() {
            if (!this.selected.length) {
                this.$message.warning('请至少选择一项');
            }
            this.$emit(
                'submit',
                this.selected.map((item) => this.$props.data[item._itemSelecterIndex])
            );
        },

        onToSelectCellClick({ rowIndex }) {
            this.selected.push(...this.toSelect.splice(rowIndex, 1));
        },

        onSelectedCellClick({ rowIndex }) {
            this.toSelect.push(...this.selected.splice(rowIndex, 1));
        },

        handleClose() {
            this.$emit('close');
        }
    }
};
</script>
