<template>
    <a-modal
        style="top: 120px; left: 0; right: 0"
        class="scoped modal-form-input-scoped global-drag item_selecter_list_md"
        :visible="visible"
        :maskClosable="false"
        :width="width"
        :height="height"
        :centered="true"
        :footer="null"
        :zIndex="3"
        @cancel="handleClose"
    >
        <div v-globalDrag="{ el: 'item_selecter_md' }" class="modal-header-wrap">
            <h6>
                <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                {{ title }}
            </h6>
        </div>
        <div style="margin-top: 56px">
            <div class="search-wrapper">
                <a-input-search placeholder="名称" v-model="searchValue" @search="handleSearch" />
            </div>
            <vxe-table
                size="small"
                border
                auto-resize
                highlight-current-row
                align="center"
                :loading="loading"
                :data="list"
                :height="height - 100"
                @scroll="onListScroll"
                @cell-dblclick="onCellDoubleClick"
            >
                <vxe-table-column v-if="selectType !== 'none'" width="10%" :type="selectType" :resizable="false" />
                <vxe-table-column
                    v-for="(column, index) in columns"
                    show-overflow
                    :key="index"
                    :field="column.dataIndex"
                    :title="column.title"
                    :width="column.width"
                    :align="column.align"
                />
            </vxe-table>
        </div>
        <div class="item-selecter-modal-footer">
            <span>共 {{ total }} 项</span>
            <span v-if="selectType === 'checkbox'">, 已选择 {{ selected.length }} 个</span>
        </div>
    </a-modal>
</template>

<script>
const columns = [
    {
        title: '名称',
        dataIndex: 'name',
        width: '25%',
        align: 'center'
    }
];

/**
 * 列表选择模态框，
 * 考虑弃用
 * @todo 从项目移除
 */
export default {
    name: 'ItemSelecterModal',
    props: {
        columns: {
            type: Array,
            default: () => columns
        },
        visible: {
            type: Boolean,
            default: false
        },
        width: {
            type: Number,
            default: 600
        },
        height: {
            type: Number,
            default: 400
        },
        title: {
            type: String,
            default: ''
        },
        selectType: {
            type: String,
            default: 'radio'
        },
        dataLoader: Function,
        onSubmit: {
            type: Function,
            default: null
        },
        onClose: {
            type: Function,
            default: null
        }
    },
    data() {
        return {
            searchValue: '',
            list: [],
            currentPage: 0,
            total: 0,
            selected: [],
            loading: false
        };
    },
    methods: {
        handleClose() {
            this.$emit('close');
            if (this.onClose) {
                this.onClose();
            }
            this.loading = false;
        },
        async handleSubmit() {
            this.$emit('submit', [...this.selected]);
            if (this.onSubmit) {
                try {
                    this.loading = true;
                    await this.onSubmit([...this.selected]);
                } finally {
                    this.loading = false;
                }
            }
        },
        handleSearch() {
            this.loading = true;
            this.selected = [];
            this.loadMoreItem(true);
        },
        handleRefresh() {
            this.searchValue = '';
            this.selected = [];
            this.loading = true;
            this.loadMoreItem(true);
        },
        onCellDoubleClick(rowInfo) {
            this.selected = [rowInfo.row];
            this.handleSubmit();
        },
        onListScroll(info) {
            if (info.$event.target.scrollHeight - (info.scrollTop + info.$event.target.clientHeight) < 76) {
                this.debouncedLoader();
            }
        },

        /**
         * 加载更多
         * @param {boolean} isReload 是否为重新加载，若重新加载，则清空列表，从第一页开始重新加载
         */
        async loadMoreItem(isReload = false) {
            try {
                let params = {
                    pageSize: 20,
                    page: this.currentPage + 1,
                    searchValue: this.searchValue
                };
                if (isReload) {
                    params.page = 1;
                } else {
                    // 若当前已加载的数量已达到总数，不继续加载
                    if (this.list.length >= this.total) return;
                }
                const { code, rows, total } = await this.dataLoader(params);
                if (code === 0) {
                    if (isReload) {
                        this.list = [...rows];
                    } else {
                        this.list = [...this.list, ...rows];
                    }
                    this.total = total;
                    this.currentPage = params.page;
                    return;
                }
                throw new Error();
            } catch (err) {
                this.$message.error('列表加载失败');
                throw err;
            } finally {
                this.loading = false;
            }
        }
    },
    watch: {
        visible(value, oldValue) {
            if (value === true && oldValue === false) {
                this.handleRefresh();
            }
        }
    },
    mounted() {
        this.debouncedLoader = this.$XEUtils.debounce(this.loadMoreItem, 500, {
            leading: true,
            trailing: false
        });
    }
};
</script>

<style lang="less" scoped>
.search-wrapper {
    margin-bottom: 16px;
}
.item-selecter-modal-footer {
    margin-top: 18px;
    text-align: right;
}
</style>
