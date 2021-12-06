<template>
    <div>
        <vxe-table
            ref="table"
            v-bind="$attrs"
            v-on="$listeners"
            :loading="loading"
            :data="data"
            @radio-change="_onRadioChange"
            @checkbox-change="_onCheckboxChange"
            @checkbox-all="_onCheckboxChange"
            @checkbox-range-change="_onCheckboxChange"
            @scroll="_onScroll"
        >
            <slot name="default"></slot>
            <template v-slot:empty>
                <slot name="empty"></slot>
            </template>
        </vxe-table>
        <div v-if="showFooter" class="scroll-load-vxe-table-footer">
            <span>共 {{ total }} 条记录，</span>
            <span>已加载 {{ data.length }} 条，</span>
            <span>已选中 {{ selectedCount }} 条</span>
        </div>
    </div>
</template>

<script>
/**
 * 滚动加载的vxe-table，
 * 已自动绑定vxe-table支持的props属性和事件
 */
export default {
    name: 'ScrollLoadVxeTable',
    props: {
        /**
         * 每次加载的条目数
         */
        countPerLoad: {
            type: Number,
            default: 10
        },
        /**
         * 是否展示底部状态栏
         */
        showFooter: {
            type: Boolean,
            default: true
        }
    },
    data() {
        return {
            loading: false,
            currentPage: 0,
            loadingPage: -1,
            pageSize: this.$props.countPerLoad,
            total: 0,
            data: [],
            selectedCount: 0
        };
    },
    mounted() {
        this._debouncedLoader = this.$XEUtils.debounce(this._loadMoreItem, 500, {
            leading: true,
            trailing: false
        });
    },
    methods: {
        handleReset() {
            this.currentPage = 0;
            this.loadingPage = -1;
            this.pageSize = this.$props.countPerLoad;
            this.total = 0;
            this.data = [];
            this.selectedCount = 0;
        },
        handleReload() {
            this.handleReset();
            this._debouncedLoader();
        },
        _onRadioChange() {
            this.selectedCount = 1;
        },
        _onCheckboxChange({ records }) {
            this.selectedCount = records.length;
        },
        _onScroll(payload) {
            if (
                payload.isY &&
                payload.$event.target.scrollHeight - (payload.scrollTop + payload.$event.target.clientHeight) < 76
            ) {
                this._debouncedLoader();
            }
        },

        async _loadMoreItem() {
            if (!this.$parent || !this.$parent.dataLoaderForScrollLoadVXETable) {
                console.error(new Error(`组件使用错误，父组件缺少数据加载函数`));
                return;
            }
            // 如果将要发出的请求和正在请求的页码相同，取消此次请求，这种判断方式有缺陷，不能更精细的区分两个请求的差异
            // 并且前一个相同页码的请求会阻滞后一个发起的请求
            if (this.loadingPage === this.currentPage + 1) {
                return;
            }
            if (this.total > this.currentPage * this.pageSize || this.currentPage === 0) {
                try {
                    this.loading = true;
                    this.loadingPage = this.currentPage + 1;
                    const { data, total } = await this.$parent.dataLoaderForScrollLoadVXETable(
                        this.currentPage + 1,
                        this.pageSize
                    );
                    this.total = total;
                    this.currentPage = this.currentPage + 1;
                    this.loadingPage = -1;
                    data.forEach((item) => this.data.push(item));
                } finally {
                    this.loading = false;
                    this.loadingPage = -1;
                }
            }
        }
    }
};
</script>

<style lang="less" scoped>
.scroll-load-vxe-table-footer {
    margin-top: 8px;
    text-align: right;
}
</style>
