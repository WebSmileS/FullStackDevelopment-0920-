<template>
    <a-cascader
        v-bind="$attrs"
        placeholder="自定义类型"
        changeOnSelect
        size="small"
        :loading="loading"
        :options="categoryList"
        :loadData="loadCategory"
        :displayRender="displayRender"
        @change="onSelectChange"
    />
</template>

<script>
import { categoryGetParentListAPI, categoryGetNextListAPI } from '@/service/pageAjax';

/**
 * 产品自定义分类选择器
 * 另：所有a-cascader自带的props属性已自动绑定。
 * change事件：用户选择时触发此事件
 */
export default {
    name: 'CategoryCustomSelecter',
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
                const { code, list, msg } = await categoryGetParentListAPI();
                if (code !== 0) {
                    throw new Error(msg || '类型加载失败');
                }
                this.categoryList = list.map((item) => ({
                    value: item['type_inner_sn'],
                    label: item.type,
                    isLeaf: item['is_leaf'] === 1
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
                const { code, list, msg } = await categoryGetNextListAPI(id);
                if (code !== 0) {
                    throw new Error(msg || '类型加载失败');
                }
                if (list.length > 0) {
                    targetOption.children = list.map((item) => {
                        item.label = item.type;
                        item.value = item.type_inner_sn;
                        item.isLeaf = item.is_leaf === 1;
                        return item;
                    });
                } else {
                    targetOption.isLeaf = true;
                }
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
