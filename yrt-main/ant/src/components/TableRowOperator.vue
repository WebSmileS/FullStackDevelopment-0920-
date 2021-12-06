<template>
    <ul class="table-row-operator">
        <li v-if="save" class="save">
            <a-tooltip class="tooltip" placement="top" title="保存">
                <a-icon class="green" type="check" @click.stop="$emit('save', payload)"></a-icon>
            </a-tooltip>
        </li>
        <li v-if="save" class="cancel">
            <a-tooltip class="tooltip" placement="top" title="取消">
                <a-icon class="red" type="close" @click.stop="$emit('cancel', payload)"></a-icon>
            </a-tooltip>
        </li>
        <li v-if="edit" class="edit">
            <a-tooltip class="tooltip" placement="top" title="编辑">
                <a-icon class="blue" type="edit" @click.stop="$emit('edit', payload)"></a-icon>
            </a-tooltip>
        </li>
        <li v-if="copy" class="copy">
            <a-tooltip class="tooltip" placement="top" title="复制">
                <i @click.stop="$emit('copy', payload)" class="yrt yrt-fuzhi"></i>
            </a-tooltip>
        </li>
        <li v-if="del" class="del">
            <a-tooltip class="tooltip" placement="top" title="删除">
                <i @click.stop="$emit('del', payload)" class="yrt yrt-shanchu-copy cancel"></i>
            </a-tooltip>
        </li>
    </ul>
</template>

<script>
/**
 * 表格中的操作栏，提供统一的样式
 * 事件：按钮点击时触发对应的事件，事件名与props中的该操作的属性名相同
 */
export default {
    name: 'TableRowOperator',
    props: {
        /**
         * 是否显示保存按钮
         */
        save: {
            type: Boolean,
            default: false
        },
        /**
         * 是否显示取消按钮
         */
        cancel: {
            type: Boolean,
            default: false
        },
        /**
         * 是否显示编辑按钮
         */
        edit: {
            type: Boolean,
            default: false
        },
        /**
         * 是否显示复制按钮
         */
        copy: {
            type: Boolean,
            default: false
        },
        /**
         * 是否显示删除按钮
         */
        del: {
            type: Boolean,
            default: false
        },
        /**
         * 触发事件时，事件所携带的数据
         */
        payload: {
            type: [Number, String, Object]
        }
    }
};
</script>

<style lang="less" scoped>
@import '~@/styles/global.less';
.table-row-operator {
    display: flex;
    flex-wrap: nowrap;
    align-content: center;
    justify-content: center;
    align-items: center;
    margin: 0;
    padding: 0;
    > li {
        width: 20px;
        min-height: 20px;
        flex: 1;
        flex-grow: 0;
        margin-left: 14px;
        list-style: none;
        &:first-child {
            margin-left: 0;
        }
        .yrt {
            padding: 0;
        }
        i {
            font-size: 16px;
        }
    }
}
.copy {
    &:hover {
        i {
            color: @primary-color;
        }
    }
}
.del {
    // 禅道#4594 删除常显红色
    // &:hover {
    i {
        color: @error-color;
    }
    // }
}
</style>
