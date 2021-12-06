<template>
    <div class="print-editor-table-formatter">
        <h3 class="print-editor-form-title">表格分页</h3>
        <a-row class="print-editor-form-wrapper" :gutter="24">
            <a-col span="4">
                <a-form-item label="启用分页">
                    <a-switch v-model="tableFormatterEditing.pageSplit" />
                </a-form-item>
            </a-col>
            <a-col span="4">
                <a-form-item label="每页最大记录数">
                    <a-input-number
                        v-model="tableFormatterEditing.maxRecordCountPerPage"
                        :disabled="!tableFormatterEditing.pageSplit"
                        :min="1"
                        :precision="0"
                    />
                </a-form-item>
            </a-col>
        </a-row>
        <h3 class="print-editor-form-title">表格格式</h3>
        <a-row class="print-editor-form-wrapper" :gutter="24">
            <a-col :span="4">
                <a-form-item label="边框">
                    <a-input-number v-model="tableFormatterEditing.tableBorder" :min="0" :step="1" :precision="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="上边距">
                    <a-input-number v-model="tableFormatterEditing.tableMarginTop" :min="0" :step="0.1" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="下边距">
                    <a-input-number v-model="tableFormatterEditing.tableMarginBottom" :min="0" :step="0.1" />
                </a-form-item>
            </a-col>
        </a-row>
        <h3 class="print-editor-form-title">
            <span>表格列格式：</span>
            <a @click="columnTableShow = !columnTableShow">
                <span v-if="columnTableShow">收起</span>
                <span v-else>展开</span>
            </a>
            <a @click="descriptionShow = !descriptionShow">
                <span v-if="!descriptionShow">（查看说明）</span>
                <span v-else>（收起说明）</span>
            </a>
        </h3>
        <div v-if="descriptionShow">
            <p>1. “已勾选”的列才会被打印</p>
            <p>2. “列别名”在打印时会替代列名</p>
            <p>3. “列最小宽度”可控制该列的最小宽度</p>
            <p>4. “精度”决定数值保留至小数后多少位（只对数值类的列生效）</p>
            <p>5. “对齐方式”决定该列单元格的内容对齐方式</p>
            <p>6. “列顺序”决定该列在表格中的前后顺序（从小到大）</p>
        </div>
        <a-table
            v-show="columnTableShow"
            rowKey="title"
            size="small"
            style="padding-left: 20px; padding-right: 12px"
            class="print-editor-form-wrapper"
            :pagination="false"
            :columns="columnTableColumns"
            :dataSource="tableColumnsEditing"
        >
            <div slot="enableTitle">
                <a-checkbox
                    :checked="allColumnsSelected"
                    @change="({ target: { checked } }) => handleChangeAllColumnsSelected(checked)"
                />
            </div>
            <div slot="name" slot-scope="text, record, index">{{ tableColumnsEditing[index].title }}</div>
            <div slot="alias" slot-scope="text, record, index">
                <a-input v-model="tableColumnsEditing[index].alias" />
            </div>
            <div slot="enable" slot-scope="text, record, index">
                <a-checkbox
                    :checked="tableColumnsEditing[index].enabled"
                    @change="({ target: { checked } }) => handleChangeColumnEnable(index, checked)"
                />
            </div>
            <div slot="width" slot-scope="text, record, index">
                <a-input-number v-model="tableColumnsEditing[index].width" :min="0" :max="10000" />
            </div>
            <div slot="precision" slot-scope="text, record, index">
                <a-input-number v-model="tableColumnsEditing[index].precision" :min="0" :max="4" :precision="0" />
            </div>
            <div slot="align" slot-scope="text, record, index">
                <a-select v-model="tableColumnsEditing[index].align" :options="alignOptions" />
            </div>
            <div slot="verticalAlign" slot-scope="text, record, index">
                <a-select v-model="tableColumnsEditing[index].verticalAlign" :options="verticalAlignOptions" />
            </div>
            <div slot="order" slot-scope="text, record, index">
                <a-input-number
                    :min="0"
                    :max="tableColumnsEditing.length"
                    :precision="0"
                    :value="tableColumnsEditing[index].order"
                    @change="(value) => handleChangeColumnOrderValue(index, value)"
                    @blur="handleChangeColumnOrder"
                />
            </div>
        </a-table>
        <h3 class="print-editor-form-title">合计单元格格式：</h3>
        <a-row class="print-editor-form-wrapper" :gutter="24">
            <a-col :span="4">
                <a-form-item label="水平对齐方式">
                    <a-select v-model="tableFormatterEditing.summationAlign" :options="alignOptions" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="垂直对齐方式">
                    <a-select v-model="tableFormatterEditing.summationVerticalAlign" :options="verticalAlignOptions" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="上填充">
                    <a-input-number v-model="tableFormatterEditing.summationPaddingTop" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="下填充">
                    <a-input-number v-model="tableFormatterEditing.summationPaddingBottom" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="左填充">
                    <a-input-number v-model="tableFormatterEditing.summationPaddingLeft" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="右填充">
                    <a-input-number v-model="tableFormatterEditing.summationPaddingRight" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="字体大小">
                    <a-input-number v-model="tableFormatterEditing.summationFontSize" :min="0" :step="0.1" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="加粗">
                    <a-switch v-model="tableFormatterEditing.summationFontBold" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="单元格分割（跨列）">
                    <a-input v-model="tableFormatterEditing.summationSpans" />
                </a-form-item>
            </a-col>
        </a-row>
        <h3 class="print-editor-form-title">表头格式：</h3>
        <a-row class="print-editor-form-wrapper" :gutter="24">
            <a-col :span="4">
                <a-form-item label="水平对齐方式">
                    <a-select v-model="tableFormatterEditing.headAlign" :options="alignOptions" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="垂直对齐方式">
                    <a-select v-model="tableFormatterEditing.headVerticalAlign" :options="verticalAlignOptions" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="上填充">
                    <a-input-number v-model="tableFormatterEditing.headPaddingTop" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="下填充">
                    <a-input-number v-model="tableFormatterEditing.headPaddingBottom" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="左填充">
                    <a-input-number v-model="tableFormatterEditing.headPaddingLeft" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="右填充">
                    <a-input-number v-model="tableFormatterEditing.headPaddingRight" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="字体大小">
                    <a-input-number v-model="tableFormatterEditing.headFontSize" :min="0" :step="0.1" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="加粗">
                    <a-switch v-model="tableFormatterEditing.headFontBold" />
                </a-form-item>
            </a-col>
        </a-row>
        <h3 class="print-editor-form-title">单元格格式：</h3>
        <a-row class="print-editor-form-wrapper" :gutter="24">
            <a-col :span="4">
                <a-form-item label="水平对齐方式">
                    <a-select v-model="tableFormatterEditing.columnAlign" :options="alignOptions" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="垂直对齐方式">
                    <a-select v-model="tableFormatterEditing.columnVerticalAlign" :options="verticalAlignOptions" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="上填充">
                    <a-input-number v-model="tableFormatterEditing.columnPaddingTop" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="下填充">
                    <a-input-number v-model="tableFormatterEditing.columnPaddingBottom" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="左填充">
                    <a-input-number v-model="tableFormatterEditing.columnPaddingLeft" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="右填充">
                    <a-input-number v-model="tableFormatterEditing.columnPaddingRight" :min="0" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="字体大小">
                    <a-input-number v-model="tableFormatterEditing.columnFontSize" :min="0" :step="0.1" />
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="加粗">
                    <a-switch v-model="tableFormatterEditing.columnFontBold" />
                </a-form-item>
            </a-col>
        </a-row>
    </div>
</template>

<script>
import { alignTypeMap, verticalAlignTypeMap } from '../../type';

const columnTableColumns = [
    {
        key: 'enable',
        slots: { title: 'enableTitle' },
        scopedSlots: { customRender: 'enable' }
    },
    {
        title: '列名',
        key: 'name',
        scopedSlots: { customRender: 'name' }
    },
    {
        title: '列别名',
        key: 'alias',
        scopedSlots: { customRender: 'alias' }
    },
    {
        title: '列最小宽度',
        key: 'width',
        scopedSlots: { customRender: 'width' }
    },
    {
        title: '数值精度',
        key: 'precision',
        scopedSlots: { customRender: 'precision' }
    },
    {
        title: '水平对齐方式',
        key: 'align',
        scopedSlots: { customRender: 'align' }
    },
    {
        title: '垂直对齐方式',
        key: 'verticalAlign',
        scopedSlots: { customRender: 'verticalAlign' }
    },
    {
        title: '列顺序',
        key: 'order',
        scopedSlots: { customRender: 'order' }
    }
];

export default {
    name: 'TableFormatter',
    inject: ['tableFormatterEditing'],
    data() {
        let allColumnsSelected = true;
        for (let i = 0, l = this.tableFormatterEditing.columns.length; i < l; i += 1) {
            if (!this.tableFormatterEditing.columns[i].enabled) {
                allColumnsSelected = false;
                break;
            }
        }
        return {
            descriptionShow: false,
            columnTableShow: true,
            columnTableColumns,
            allColumnsSelected,
            tableColumnsEditing: this.tableFormatterEditing.columns,

            // 当排序值发生变化时的标记，避免用户从输入框移出，但并未改变值时，进行不必要的排序
            columnsOrderValueChanged: false
        };
    },
    computed: {
        // 水平对齐方式options
        alignOptions() {
            return Object.keys(alignTypeMap).map((key) => ({
                value: alignTypeMap[key].value,
                label: alignTypeMap[key].text
            }));
        },
        // 垂直对齐方式options
        verticalAlignOptions() {
            return Object.keys(verticalAlignTypeMap).map((key) => ({
                value: verticalAlignTypeMap[key].value,
                label: verticalAlignTypeMap[key].text
            }));
        }
    },
    methods: {
        /**
         * 切换所有的表格列启用状态
         * @param {boolean} checked 表格列启用状态
         */
        handleChangeAllColumnsSelected(checked) {
            for (let i = 0, l = this.tableColumnsEditing.length; i < l; i += 1) {
                this.$set(this.tableColumnsEditing[i], 'enabled', checked);
            }
            this.allColumnsSelected = checked;
        },

        /**
         * 改变表格列的启用状态
         * @param {number} index 表格列的索引值
         * @param {boolean} enabled 表格列的启用状态
         */
        handleChangeColumnEnable(index, enabled) {
            this.$set(this.tableColumnsEditing[index], 'enabled', enabled);
            if (!enabled) {
                this.allColumnsSelected = false;
            }
        },

        /**
         * 改变表格列打印时的排列顺序值
         * @param {number} index 表格列的索引值
         * @param {number} width 表格列顺序值，值越小越靠前
         */
        handleChangeColumnOrderValue(index, order) {
            // 防止输入框a-input-number里没值时，会触发onchange事件
            if (order === null && this.tableColumnsEditing[index].order === undefined) {
                return;
            }
            this.$set(this.tableColumnsEditing[index], 'order', order);
            // 当排序值发生变化时的标记，避免用户从输入框移出，但并未改变值时，进行不必要的排序
            this.columnsOrderValueChanged = true;
        },

        /**
         * 根据列排序值排序表格列
         */
        handleChangeColumnOrder() {
            // 排序值未发生变化时，不进行不必要的排序
            if (!this.columnsOrderValueChanged) {
                return;
            }
            const columns = this.tableColumnsEditing;
            const length = columns.length;
            const newColumns = [];
            const orderMap = {};
            const noOrderColumns = [];
            for (let i = 0; i < length; i += 1) {
                if (typeof columns[i].order !== 'number' || isNaN(columns[i].order)) {
                    noOrderColumns.push(columns[i]);
                    continue;
                }
                if (orderMap[columns[i].order]) {
                    orderMap[columns[i].order].push(columns[i]);
                    continue;
                }
                orderMap[columns[i].order] = [columns[i]];
            }
            Object.keys(orderMap).forEach((key) => {
                newColumns.push(...orderMap[key]);
            });
            this.tableColumnsEditing.splice(0, length, ...newColumns, ...noOrderColumns);
            this.columnsOrderValueChanged = false;
        }
    }
};
</script>
