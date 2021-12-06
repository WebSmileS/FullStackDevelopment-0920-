<template>
    <div>
        <vxe-grid
            ref="table"
            size="small"
            :height="table.height"
            :data="datas"
            :loading="table.Loading"
            border
            resizable
            auto-resize
            highlight-current-row
            @cell-click="tableCellClickFn"
            @cell-dblclick="tableCellDblclickFn"
            @checkbox-change="selectChangeEvent"
            align="center"
        />
        <vxe-pager
            :current-page="pager.pageNum"
            :page-size="pager.pageSize"
            :page-sizes="pager.pageSizes"
            :total="pager.total"
            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="ontablePaginationChangeFn">
        </vxe-pager>
    </div>
</template>

<script>
export default {
    props: {
        /** 调用来源 */
        origins: Number,
        /** 表格设置 */
        columns: Array,
        /** 表格内容 */
        datas: Object
    },
    data () {
        return {
            /** 分页设置 */
            pager: {
                pageNum: 0,
                pageSize: 0,
                pageSizes: 0,
                total: 0
            },
            /** 表格设置 */
            table: {
                // 表格高度
                height: 0,
                // 表格加载中
                loading: false
            }
        }
    },
    methods: {
        /**
         * 表格单击绑定事件
         */
        tableCellClickFn () {
            // select item
        },
        /**
         * 表格双击绑定事件
         */
        tableCellDblclickFn () {
            // another route
        },
        /**
         * 表格分页绑定事件
         */
        ontablePaginationChangeFn () {

        }
    }
}
</script>

<style>

</style>
