export default {
    props: {
        // 列表查询参数
        searchValues: {
            type: Object,
            default: () => ({})
        },
        // 列表查询参数发生变化时，是否自动加载列表
        reloadWhenSearch: {
            type: Boolean,
            default: true
        }
    },
    data() {
        return {
            loading: false,
            list: [],
            pagination: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            }
        };
    },
    watch: {
        searchValues() {
            if (!this.reloadWhenSearch) return;
            this.loadList();
        }
    },
    methods: {
        /**
         * 初始化
         */
        handleInitPaginationMixin() {
            this.loadList();
        },
        /**
         * 分页导航
         */
        handleChangePagination(pagination) {
            this.pagination = { ...this.pagination, ...pagination };
            this.loadList();
        },

        /**
         * 列表查询，主要来自父组件调用
         */
        handleSearch(searchValues) {
            this.pagination.currentPage = 1;
            this.loadList(searchValues);
        },

        /**
         * 移除一条记录
         * @param {string} key 用来检测是否匹配的该条记录的键
         * @param {string | number} value 匹配的值
         */
        handleRemoveRecordByKeyValue(key, value) {
            let i, l;
            for (i = 0, l = this.list.length; i < l; i += 1) {
                if (this.list[i][key] === value) {
                    break;
                }
            }
            if (i < l) {
                this.list.splice(i, 1);
            }
        },

        /**
         * 加载列表数据
         */
        async loadList(searchValues) {
            const _searchValues = searchValues || this.searchValues;
            try {
                this.loading = true;
                const { currentPage, pageSize } = this.pagination;
                const { code, rows, total } = await this.listLoader(currentPage, pageSize, _searchValues);
                if (code === 0) {
                    this.list = rows;
                    this.pagination.total = total;
                    return;
                }
                throw new Error();
            } catch (err) {
                this.$message.error(err.message || '列表加载失败');
                throw err;
            } finally {
                this.loading = false;
            }
        }
    }
};
