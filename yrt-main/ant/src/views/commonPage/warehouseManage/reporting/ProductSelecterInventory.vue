<!-- 
* @Author: yangtao  
* @Description: 报损-新增编辑-选择产品组件
-->
<template>
    <div>
        <list-page-list
            style="height: 400px"
            ref="xTable"
            selectType="checkbox"
            :columns="columnsLevelComputed"
            :list="actualInventoryDatas"
            :loading="actualInventoryLoading"
        ></list-page-list>

        <vxe-table
            border
            show-overflow
            show-header-overflow
            show-footer
            resizable
            auto-resize
            ref="xTable"
            size="small"
            align="center"
            :height="fullscreen ? fullscreenTableHeight : 300"
            :data="dataEditingDetails"
            :mouse-config="{ selected: true }"
            :checkbox-config="{ range: true }"
            :keyboard-config="{
                isArrow: true,
                isDel: true,
                isEnter: true,
                isTab: true,
                isEdit: true
            }"
            :edit-config="{ trigger: 'click', mode: 'cell', activeMethod: handleCheckCellEditable }"
            :span-method="rowSpanMethod"
            :footer-method="() => [[]]"
            @cell-dblclick="({ $event }) => $event.stopPropagation()"
            @edit-actived="onEditActived"
            @barcodeInputFinish="
                (barcode, params) =>
                    _mixinOnBarcodeInputFinish(barcode, params, loadBarcodePIInfo, [
                        ['batch_number', 'batch_number', '批号']
                    ])
            "
        >
            <template slot="empty">
                <div>双击空白区域添加产品</div>
            </template>
            <vxe-table-column v-for="column in productColumns" v-bind="column" :key="column.field"></vxe-table-column>
            <vxe-table-column
                field="reportNumber"
                title="报损数量"
                :width="100"
                :edit-render="{ name: '$input', props: { type: 'float' } }"
            ></vxe-table-column>
            <vxe-table-column v-if="!isReadonly" v-bind="tableColumnConfig.operation">
                <template v-slot:default="{ rowIndex }">
                    <table-row-operator del :payload="rowIndex" @del="handleRemoveProduct"></table-row-operator>
                </template>
                <template slot="footer">
                    <div style="overflow: hidden">
                        <a-button size="small" type="primary" @click="handleSelectProducts">添加产品</a-button>
                    </div>
                </template>
            </vxe-table-column>
        </vxe-table>
    </div>
</template>

<script>
import ListPageList from '@/components/ListPageList.vue';
import { inventoryListLevelAPI } from '@/service/pageAjax';
import { productColumns } from './productColumns';

export default {
    name: 'productSelecterInventory',
    components: {
        ListPageList
    },
    props: {},
    data() {
        return {
            searchValues: {
                org_id: this.$cookie.get('userbelong'),
                orderByColumn: '',
                warehouse_inner_sn: '',
                product_name: '',
                specification: '',
                batch_number: '',
                product_vendor_inner_sn: ''
            },
            //查询级别1-4
            level: 3,
            actualInventoryLoading: true,
            actualInventoryDatas: [],
            productSelecter: []
        };
    },
    computed: {
        // 计算表头
        columnsLevelComputed() {
            let columns = productColumns;
            return columns;
        }
    },
    created() {
        this.getSearchData();
    },
    methods: {
        // 查询
        getSearchData() {
            let params = this.searchValues;
            this.actualInventoryLoading = true;
            inventoryListLevelAPI(this.level, { params })
                .then((res) => {
                    this.actualInventoryLoading = false;
                    if (parseInt(res.code) === 0) {
                        this.actualInventoryDatas = res.list;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.actualInventoryLoading = false;
                });
        },

        handleSubmit(products) {
            const _products = products || this.$refs.xTable.$refs.listTable.getCheckboxRecords(true);
            if (_products && _products.length > 0) {
                this.$emit('submit', _products);
                this.$refs.xTable.$refs.listTable.removeCheckboxRow();
            } else {
                this.$message.warning('未选择产品');
            }
        }
    }
};
</script>
