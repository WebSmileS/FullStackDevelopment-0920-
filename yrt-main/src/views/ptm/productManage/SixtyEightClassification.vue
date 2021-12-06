<template>
    <div class="scoped scoped-white">
        <!-- 表格区域开始 -->
        <div class="tab-table-wrap tab-table-overflowy">
            <!--no-cursor-pointer-->
            <div class="table-pd-bg" style="padding: 8px">
                <a-table
                    ref="xSixtyTable"
                    :columns="columns"
                    :dataSource="datas"
                    :pagination="true"
                    bordered
                    :loading="code68Loading"
                    :scroll="{ y: scrollYNumber }"
                    :customRow="customRowFn"
                    :rowClassName="rowClassFn"
                    @expand="expandFn"
                >
                    <div slot="management_type" slot-scope="text">
                        {{ text | managementTypeFilters }}
                    </div>
                </a-table>
            </div>
        </div>
        <!-- 表格区域结束 -->
    </div>
</template>

<script>
import { code68ParentListAPI, code68ChildrenListAPI } from '@/service/pageAjax';
export default {
    name: 'SixtyEightClassification',
    data() {
        return {
            columns: [
                {
                    title: '分类目录编号',
                    dataIndex: 'code68_sn',
                    key: 'code68_sn',
                    width: '30em',
                    align: 'center'
                },
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    width: '40em',
                    align: 'center'
                },
                {
                    title: '管理类别',
                    dataIndex: 'management_type',
                    key: 'management_type',
                    align: 'center',
                    scopedSlots: { customRender: 'management_type' }
                }
            ],
            datas: [], // 表格数据
            code68Loading: true, // 表格loading动画配置
            expandedRows: [], // 表格是否发过请求标识的容器
            currentTrObj: {} // 表格选中的当前行数据 / 标识
        };
    },
    methods: {
        // 查询医疗器械【68分类列表】信息的详情 - 查询所有的最上级68分类信息
        async code68ParentListAPIFn() {
            await code68ParentListAPI().then((res) => {
                this.code68Loading = false;
                const { code, list } = res;
                if (parseFloat(code) === 0) {
                    this.datas = list.map((item) => {
                        item.key = item.code68_sn;
                        item.children = item.is_leaf === 0 ? [] : null; // is_leaf 1：没有下一级叶子节点   0：有
                        return item;
                    });
                    if (this.datas.length) {
                        this.currentTrObj = this.datas[0];
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据【pid查询分类下所有的下级分类信息】
        async code68ChildrenListAPIFn(record) {
            const { code68_sn: code68Sn } = record;
            await code68ChildrenListAPI(code68Sn).then((res) => {
                this.code68Loading = false;
                const { code, list } = res;
                if (parseFloat(code) === 0) {
                    // 追加children
                    const rowsNew = list.map((item) => {
                        item.key = item.code68_sn;
                        item.children = item.is_leaf === 0 ? [] : null; // is_leaf 1：没有下一级叶子节点   0：有
                        return item;
                    });
                    record.children = rowsNew.length > 0 ? rowsNew : null;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 点击展开图标时触发
        expandFn(expanded, record) {
            if (!expanded) return;
            if (this.expandedRows.some((item) => item === record.code68_sn)) return; // 已经请求过的就不发请求
            this.code68Loading = true;
            this.code68ChildrenListAPIFn(record);
            this.expandedRows.push(record.code68_sn);
        },
        // 设置行属性 —— 点击每一行的事件
        customRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentTrObj = record;
                    },
                    dblclick: () => {
                        // if ((e.srcElement || e.target).classList.contains('ant-table-row-expand-icon')) return
                        this.currentTrObj = record;
                    }
                }
            };
        },
        // 表格的高亮样式显示
        rowClassFn(record) {
            if (record.key === this.currentTrObj.key) {
                return 'active-tr';
            }
            return '';
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 180;
            return clientHeight - topHeight;
        }
    },
    filters: {
        managementTypeFilters(myInput) {
            // 格式化管理类别
            const managementTypes = ['', 'Ⅰ类', 'Ⅱ类', 'Ⅲ类']; // 管理类别 0 1 2 3 依次对应数组的罗马数字 【罗马没有数字0】
            return managementTypes[parseFloat(myInput)];
        }
    },
    created() {
        this.code68ParentListAPIFn();
    }
};
</script>

<style lang="less" scoped>
.scoped-white {
    .tab-table-wrap {
        top: 0;
    }
}
</style>
