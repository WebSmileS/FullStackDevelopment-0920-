<template>
    <div class="list-page-list-scroll-load">
        <div class="list-page-table-wrapper" ref="tableContainer">
            <vxe-table
                ref="listTable"
                size="small"
                border
                resizable
                auto-resize
                highlight-current-row
                align="center"
                height="auto"
                :data="data"
                :loading="loading"
                show-overflow
                show-header-overflow
                showHeader
                :row-class-name="$attrs.rowClassName"
                @cell-click="onCellClick"
                @cell-dblclick="onCellDoubleClick"
                @radio-change="onTableSelect"
                @checkbox-change="onTableSelect"
                @checkbox-all="onTableSelect"
                @scroll="_onScroll"
            >
                <vxe-table-column
                    v-if="selectType !== 'none'"
                    v-bind="tableColumnConfig.select"
                    :type="selectType"
                ></vxe-table-column>
                <vxe-table-column
                    v-for="column in columns"
                    v-bind="column"
                    :key="column.field"
                    show-overflow
                ></vxe-table-column>
                <slot name="statuColumn"></slot>
                <slot name="operationColumn"></slot>
            </vxe-table>
        </div>
        <div v-if="showFooter" class="list-page-list-scroll-load-footer">
            <div class="footer-left">
                <slot name="footerLeft"></slot>
            </div>
            <div class="footer-right">
                <span>共 {{ total }} 条记录，</span>
                <span>已加载 {{ data.length }} 条，</span>
                <span>已选中 {{ selectedCount }} 条</span>
                <a-button
                    class="button-load-more"
                    size="small"
                    type="primary"
                    :loading="loading"
                    @click="onLoadMoreButtonClick"
                    >加载更多</a-button
                >
            </div>
        </div>
    </div>
</template>

<script>
import tableColumnConfig from '@/config/tableColumnConfig';

/**
 * 列表页滚动加载列表，提供统一的样式，配合ListPageLayout组件使用
 * rowDoubleClick事件：行双击事件
 * selectedChange事件：列表项选择发生变化时触发
 */
export default {
    name: 'ListPageListScrollLoad',
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
         * 参见vxe-table-column支持的属性
         */
        columns: {
            type: Array,
            default: () => []
        },
        /**
         * 是否显示底部栏
         */
        showFooter: {
            type: Boolean,
            default: true
        },
        /**
         * 每次加载条目数量
         */
        countPerLoad: {
            type: Number,
            default: undefined
        },
        /**
         * 列表的查询函数，触发列表加载时将调用此函数加载数据
         * @todo 考虑变更为其它方式，不在props中传递函数，或此类逻辑需求均使用props向子组件传递函数
         */
        listLoader: {
            type: Function,
            require: true
        }
    },
    data() {
        return {
            loading: false,
            currentPage: 0,
            loadingPage: -1,
            pageSize: this.$props.countPerLoad === undefined ? 10 : this.$props.countPerLoad,
            total: 0,
            data: [],
            selectedCount: 0
        };
    },
    computed: {
        tableColumnConfig() {
            return tableColumnConfig;
        }
    },
    created() {
        this._debouncedLoader = this.$XEUtils.debounce(this._loadMoreItem, 500, {
            leading: true,
            trailing: false
        });
    },
    mounted() {
        this.$nextTick(() => {
            if (this.countPerLoad === undefined) {
                // 粗略计算每页视图区可显示多少条数据
                this.pageSize = Math.round((this.$refs.tableContainer.clientHeight - 36) / 36) + 1;
            }
        });
    },
    methods: {
        onCellClick(target) {
            let selected;
            switch (this.selectType) {
                case 'none':
                    return;
                case 'checkbox':
                    this.$refs.listTable.toggleCheckboxRow(target.row);
                    selected = this.$refs.listTable.getCheckboxRecords();
                    break;
                case 'radio':
                    this.$refs.listTable.setRadioRow(target.row);
                    selected = [this.$refs.listTable.getRadioRecord()];
            }
            this.handleUpdateSelected(selected);
        },
        onTableSelect({ row, records, $event }) {
            $event.stopPropagation();
            let selected;
            if (records) {
                selected = records;
            } else {
                selected = [row];
            }
            this.handleUpdateSelected(selected);
        },

        onCellDoubleClick({ row, rowIndex }) {
            this.$emit('rowDoubleClick', row, rowIndex);
        },

        _onScroll(payload) {
            if (
                payload.isY &&
                payload.$event.target.scrollHeight - (payload.scrollTop + payload.$event.target.clientHeight) < 76
            ) {
                this._debouncedLoader();
            }
        },

        onLoadMoreButtonClick() {
            this._debouncedLoader();
        },

        handleReset() {
            this.currentPage = 0;
            this.loadingPage = -1;
            this.total = 0;
            this.data = [];
            this.selectedCount = 0;
            this.handleUpdateSelected([]);
        },

        handleReload() {
            this.handleReset();
            this._debouncedLoader();
        },

        /**
         * @param {array} selected 已选记录
         */
        handleUpdateSelected(selected) {
            this.$emit('selectedChange', selected);
            this.selectedCount = selected.length;
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
                        this.handleUpdateSelected(newSelected);
                    }
                    break;
                }
                case 'radio': {
                    let selected = this.$refs.listTable.getRadioRecord();
                    if (selected && selected === record) {
                        this.handleUpdateSelected([]);
                    }
                    break;
                }
            }
        },

        async _loadMoreItem() {
            if (!this.listLoader || typeof this.listLoader !== 'function') {
                console.error(new Error(`组件使用错误，缺少数据加载函数`));
                return;
            }
            // 如果将要发出的请求和正在请求的页码相同，取消此次请求，这种判断方式有缺陷，不能更精细的区分两个请求的差异
            // 并且前一个相同页码的请求会阻滞后一个发起的请求
            if (this.loadingPage === this.currentPage + 1) {
                return;
            }
            if (this.total > this.currentPage * this.pageSize || this.currentPage === 0) {
                this.loading = true;
                try {
                    this.loadingPage = this.currentPage + 1;
                    const { data, total } = await this.listLoader(this.currentPage + 1, this.pageSize);
                    this.total = total;
                    this.currentPage = this.currentPage + 1;
                    this.loadingPage = -1;
                    data.forEach((item) => this.data.push(item));
                } finally {
                    this.loadingPage = -1;
                    this.loading = false;
                }
            }
        }
    }
};
</script>

<style lang="less" scoped>
@footerHeight: 48px;
.list-page-list-scroll-load {
    height: 100%;
    .list-page-table-wrapper {
        height: calc(100% - @footerHeight);
    }
    .list-page-list-scroll-load-footer {
        display: flex;
        .footer-left {
            flex-grow: 1;
        }
        .footer-right {
            flex-grow: 0;
            height: @footerHeight;
            line-height: @footerHeight;
            text-align: right;
        }
    }
    .button-load-more {
        margin-left: 24px;
    }
    /deep/ th.col--center,
    /deep/ td.col--center {
        div.c--tooltip {
            margin: 0 auto;
        }
    }
}
</style>
