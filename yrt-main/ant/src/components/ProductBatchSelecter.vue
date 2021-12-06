<template>
    <base-modal
        dragKey="product_batch_selecter_drag_md"
        v-bind="$attrs"
        v-on="$listeners"
        :visible="visible"
        :footer="null"
    >
        <template slot="content">
            <div style="margin: 36px 0 0">
                <vxe-table
                    size="small"
                    border
                    auto-resize
                    highlight-current-row
                    align="center"
                    :loading="loading"
                    :data="list"
                    :height="tableHeight"
                    @cell-click="onCellClick"
                >
                    <vxe-table-column type="seq" title="NO." />
                    <vxe-table-column v-for="column in columns" v-bind="column" show-overflow :key="column.field" />
                </vxe-table>
            </div>
        </template>
    </base-modal>
</template>

<script>
import BaseModal from '@/components/BaseModal';
import { odoBatchListAPI } from '@/service/pageAjax';

const defaultColumns = [
    {
        title: '批号',
        field: 'batch_number'
    },
    {
        title: '生产日期',
        field: 'produce_date'
    },
    {
        title: '失效日期',
        field: 'overdue_date'
    },
    {
        title: '实际可用库存',
        field: 'quantity'
    },
    {
        title: '平均单价',
        field: 'average_price'
    }
];

/**
 * 产品批号选择模态框
 */
export default {
    name: 'ProductBatchSelecter',
    components: {
        BaseModal
    },
    props: {
        /**
         * 模态框内表格的高度
         */
        tableHeight: {
            type: Number,
            default: 300
        },
        /**
         * 表格列配置，参见vxe-table-column的属性
         * 不传入时使用默认的列配置
         */
        tableColumns: {
            type: Array
        },
        /**
         * 模态框显隐
         */
        visible: {
            type: Boolean,
            default: false
        },
        /**
         * 业务类型
         */
        billsType: {
            type: String
        },
        /**
         * 仓库id
         */
        warehouseInnerSn: {
            type: [Number, String]
        },
        /**
         * 产品id
         */
        productInnerSn: {
            type: [Number, String]
        },
        /**
         * 产品类型
         */
        productType: {
            type: [Number, String]
        },
        /**
         * 产品厂商id
         */
        productVendorInnerSn: {
            type: [Number, String]
        },
        /**
         * 产品规格型号id
         */
        specificationInnerSn: {
            type: [Number, String]
        }
    },
    data() {
        return {
            loading: false,
            list: []
        };
    },
    computed: {
        columns() {
            if (this.tableColumns) {
                return this.tableColumns;
            }
            return defaultColumns;
        }
    },
    watch: {
        visible(newValue) {
            if (newValue) {
                // 打开后初始化状态
                this.handleInit();
                // 打开时加载列表
                this.loadBatchList();
            }
        }
    },
    created() {
        // 数据加载标识，当新的加载请求发出后，手动更新标识
        this.loaderMark = 0;
    },
    methods: {
        onCellClick({ row }) {
            let submitData = { ...row };
            delete submitData._XID;
            this.$emit('submit', submitData);
            this.$emit('close');
        },

        /**
         * 初始化状态
         */
        handleInit() {
            this.loaderMark += 1;
            this.loading = false;
            this.list = [];
        },

        /**
         * 加载批号列表
         * 每次请求返回时都比对loaderMark，防止新发出的请求返回值时的状态更新被先前发出的延迟了的请求覆盖
         */
        async loadBatchList() {
            const currentMark = this.loaderMark + 1;
            this.loaderMark = currentMark;
            try {
                const orgId = this.$cookie.get('userbelong');
                this.loading = true;
                const { code, msg, list } = await odoBatchListAPI({
                    org_id: orgId,
                    billsType: this.billsType,
                    warehouse_inner_sn: this.warehouseInnerSn,
                    product_inner_sn: this.productInnerSn,
                    product_type: this.productType,
                    product_vendor_inner_sn: this.productVendorInnerSn,
                    specification_inner_sn: this.specificationInnerSn
                });
                if (code !== 0) {
                    this.$message.error(msg);
                    return;
                }
                if (currentMark === this.loaderMark) {
                    this.list = list;
                }
            } finally {
                if (currentMark === this.loaderMark) {
                    this.loading = false;
                }
            }
        }
    }
};
</script>
