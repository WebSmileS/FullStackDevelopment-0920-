<template>
    <div class="list-page-list" :class="{ 'list-no-page-list': isPage == false }">
        <div class="list-page-table-wrapper">
            <vxe-toolbar ref="xToolbar1" custom></vxe-toolbar>
            <vxe-table
                :id="id"
                :custom-config="{ storage: true }"
                ref="listTable"
                size="small"
                border
                resizable
                auto-resize
                highlight-current-row
                align="center"
                height="auto"
                :data="list"
                :loading="loading"
                show-overflow
                show-header-overflow
                showHeader
                v-on="$listeners"
                :row-class-name="$attrs.rowClassName"
                :cell-style="cellStyle"
                @cell-click="onCellClick"
                @cell-dblclick="onCellDoubleClick"
                @radio-change="onTableSelect"
                @checkbox-change="onTableSelect"
                @checkbox-all="onTableSelect"
                :show-footer="showFooter"
                :footer-method="omFooterMethod"
            >
                <vxe-table-column
                    v-if="selectType !== 'none'"
                    v-bind="tableColumnConfig.select"
                    :type="selectType"
                ></vxe-table-column>
                <vxe-table-column
                    v-for="column in columns"
                    v-bind="column"
                    :key="column.dataIndex || column.field"
                    :field="column.dataIndex || column.field"
                    :align="column.align"
                    show-overflow
                ></vxe-table-column>
                <slot name="statuColumn"></slot>
                <slot name="operationColumn"></slot>
            </vxe-table>
        </div>
        <div v-if="isPage" class="list-page-pagination-wrapper">
            <vxe-pager
                :loading="loading"
                :current-page="pagination.currentPage"
                :page-size="pagination.pageSize"
                :total="pagination.total"
                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                @page-change="handleChangePage"
            >
            </vxe-pager>
        </div>
    </div>
</template>

<script>
import Big from 'big.js';
import tableColumnConfig from '@/config/tableColumnConfig';

/**
 * 列表页常规列表，提供统一的样式，配合ListPageLayout组件使用
 * changePagination事件：分页导航发生改变时触发
 * selectedChange事件：列表项选择发生变化时触发
 */
export default {
    name: 'ListPageList',
    props: {
        /**
         * 列表项的选择类型,
         * none：无,
         * checkbox: 多选,
         * radio: 单选
         */
        selectType: {
            type: String,
            default: 'none'
        },
        /**
         * 列表的列配置
         * @type {object} column
         * @property {string} title 列标题
         * @property {string} field 列标识
         * @property {any} others 其它vxe-table-column支持的属性
         */
        columns: {
            type: Array,
            default: () => []
        },
        /**
         * 列表加载状态
         */
        loading: {
            type: Boolean,
            default: false
        },
        /**
         * 列表数据
         */
        list: {
            type: Array,
            default: () => []
        },
        /**
         * 列表导航器状态值
         */
        pagination: {
            type: Object,
            default: () => ({
                currentPage: 1,
                pageSize: 10,
                total: 0
            })
        },

        // 是否显示分页
        isPage: {
            type: Boolean,
            default: () => true
        },

        // 底部合计传入需要合计的key值，只支持Number类型, 如果key的值不为Number，返回0
        footerMethodColumn: {
            type: Array,
            default: () => []
        },

        // 单元格样式
        cellStyle: Function
    },
    data() {
        return {
            id: '',
            tableColumnConfig,
            // 是否开启底部合计栏，如果 footerMethodColumn 有值，则显示，没有值不显示
            showFooter: this.footerMethodColumn.length > 0 ? true : false
        };
    },
    created() {
        // 表格唯一id获取路由name
        this.id = this.$route.name;
    },
    methods: {
        handleChangePage({ currentPage, pageSize }) {
            this.$emit('changePagination', { currentPage, pageSize });
        },
        handleDeleteRecord(record) {
            this.$refs.listTable.remove(record);
            switch (this.selectType) {
                case 'none':
                    break;
                case 'checkbox': {
                    let selected = this.$refs.listTable.getCheckboxRecords();
                    let newSelected = selected.filter((_) => _ !== record);
                    if (newSelected.length < selected.length) {
                        this.$emit('selectedChange', newSelected);
                    }
                    break;
                }
                case 'radio': {
                    let selected = this.$refs.listTable.getRadioRecord();
                    if (selected && selected === record) {
                        this.$emit('selectedChange', []);
                    }
                    break;
                }
            }
        },
        onCellClick(target) {
            let selected;
            switch (this.selectType) {
                case 'none':
                    selected = target.row;
                    break;
                case 'checkbox':
                    this.$refs.listTable.toggleCheckboxRow(target.row);
                    selected = this.$refs.listTable.getCheckboxRecords();
                    break;
                case 'radio':
                    this.$refs.listTable.setRadioRow(target.row);
                    selected = [this.$refs.listTable.getRadioRecord()];
            }
            this.$emit('selectedChange', selected);
        },
        onTableSelect({ row, records, $event }) {
            $event.stopPropagation();
            let selected;
            if (records) {
                selected = records;
            } else {
                selected = [row];
            }
            this.$emit('selectedChange', selected);
        },
        onCellDoubleClick({ row, rowIndex }) {
            this.$emit('rowDoubleClick', row, rowIndex);
        },

        // 尾部合计
        omFooterMethod({ columns }) {
            const footerData = [
                columns.map((column, columnIndex) => {
                    if (columnIndex === 0) {
                        return '合计';
                    }
                    let sum;
                    sum = '/';
                    this.footerMethodColumn.map((item) => {
                        if ([item].includes(column.property)) {
                            sum = new Big(0);
                            for (var i = 0; i < this.list.length; i++) {
                                if (typeof this.list[i][item] == 'number') {
                                    sum = sum.plus(this.list[i][item]);
                                } else {
                                    // 统计的数据如果经过处理 将数据转成数值类型
                                    sum = sum.plus(parseFloat(this.list[i][item].replace(/,/g, '')));
                                }
                            }
                            sum = this.$Utils.commafy(new Big(sum));
                        }
                        return sum;
                    });
                    return sum;
                })
            ];
            return footerData;
        }
    }
};
</script>

<style lang="less" scoped>
@paginationHeight: 48px;
.list-page-list {
    height: 100%;
    position: relative;
    .vxe-toolbar {
        font-size: 14px;
        height: 36px;
        position: absolute;
        top: 0;
        right: 1px;
        z-index: 999;
        /deep/ .vxe-button.type--button.is--circle {
            border-radius: 0;
            border: none;
            background-color: #f8f8f9;
        }
    }
    .list-page-table-wrapper {
        height: calc(100% - @paginationHeight);
    }
    .list-page-pagination-wrapper {
        height: @paginationHeight;
        height: 48px;
    }
    /deep/ th.col--center,
    /deep/ td.col--center {
        div.c--tooltip {
            margin: 0 auto;
        }
    }
}
.list-no-page-list {
    .list-page-table-wrapper {
        height: 100%;
    }
}
</style>
