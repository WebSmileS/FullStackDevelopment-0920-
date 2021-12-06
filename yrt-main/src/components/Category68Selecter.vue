<template>
    <a-cascader
        v-bind="$attrs"
        size="small"
        placeholder="产品分类"
        changeOnSelect
        :options="categoryList"
        :loadData="loadCategory"
        :displayRender="displayRender"
        @change="onSelectChange"
    />
</template>

<script>
import { code68ChildrenListAPI, code68ParentListAPI } from '@/service/pageAjax';

/**
 * 产品68分类选择器
 * 另：所有a-cascader自带的props属性已自动绑定。
 * change事件：用户选择时触发此事件
 */
export default {
    name: 'Category68Selecter',
    props: {
        /**
         * 选择器当前值
         */
        value: {
            type: Array
        }
    },
    data() {
        return {
            loading: false,
            categoryList: []
        };
    },
    mounted() {
        this.loadCategoryList();
    },
    methods: {
        onSelectChange(value) {
            this.$emit('change', value);
        },
        displayRender(obj) {
            let len = obj.labels.length;
            const label = obj.labels[len - 1];
            return label;
        },
        // 获取自定义类型列表
        async loadCategoryList() {
            this.loading = true;
            try {
                const { code, list, msg } = await code68ParentListAPI();
                if (code !== 0) {
                    throw new Error(msg || '类型加载失败');
                }
                this.categoryList = list.map((item) => ({
                    label: '(' + item.code68_sn + ') ' + item.name,
                    value: item.code68_sn,
                    isLeaf: item.is_leaf === 1
                }));
            } catch (err) {
                this.$message.error(err.message);
            } finally {
                this.loading = false;
            }
        },
        // 加载自定义类型列表子集
        async loadCategory(options) {
            const targetOption = options[options.length - 1];
            try {
                targetOption.loading = true;
                const id = targetOption.value;
                const { code, list, msg } = await code68ChildrenListAPI(id);
                if (code !== 0) {
                    throw new Error(msg || '类型加载失败');
                }
                if (list.length > 0) {
                    targetOption.children = list.map((item) => {
                        item.label = '(' + item.code68_sn + ') ' + item.name;
                        item.value = item.code68_sn;
                        item.isLeaf = item.is_leaf === 1;
                        return item;
                    });
                } else {
                    targetOption.isLeaf = true;
                }
                targetOption.loading = false;
                this.categoryList = [...this.categoryList];
            } catch (err) {
                this.$message.error(err.message);
            } finally {
                targetOption.loading = false;
            }
        }
    }
};
</script>
