<template>
    <div class="form-table">
        <vxe-table
            ref="detailModalTable"
            size="small"
            :height="226"
            border
            resizable
            auto-resize
            highlight-current-row
            align="center"
            show-overflow
            show-header-overflow
            showHeader
            @cell-dblclick="dblclickAddProductFn"
            :data="datas"
            :loading="pagation.loading"
        >
            <vxe-table-column title="NO." width="50" type="seq" show-overflow></vxe-table-column>
            <vxe-table-column
                v-for="detailColumnItem in columns"
                :key="detailColumnItem.key"
                :field="detailColumnItem.dataIndex"
                :title="detailColumnItem.title"
                :width="detailColumnItem.width"
                show-overflow
            ></vxe-table-column>
            <vxe-table-column field="written_off_price" title="核销金额" width="180" show-overflow>
                <template v-slot="{ row }">
                    <a-input-number
                        :min="0"
                        :formatter="(value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                        placeholder=""
                        class="form-input"
                        size="small"
                        v-model="row.written_off_price"
                        :disabled="isReadonly"
                        style="width: 100%"
                    />
                </template>
            </vxe-table-column>
            <vxe-table-column v-if="!isReadonly" field="action" title="操作" width="50px" align="center">
                <template v-slot="{ row, seq }">
                    <ul class="action-btn-list-wrap" style="position: relative">
                        <li @click.stop="delFn(row, seq - 1)">
                            <a-tooltip class="tooltip" placement="top" title="删除">
                                <i class="yrt yrt-shanchu-copy cancel"></i>
                            </a-tooltip>
                        </li>
                    </ul>
                </template>
            </vxe-table-column>
            <template v-slot:empty>
                <div class="dblclick-wrap" @dblclick="dblclickAddProductFn">
                    <span>双击添加发票</span>
                </div>
            </template>
        </vxe-table>

        <!-- 出入库发票的添加产品列表区域开始 -->
        <a-modal
            :title="addSystemInvoiceListModal.title"
            v-model="addSystemInvoiceListModal.alert"
            :maskClosable="false"
            :mask="false"
            :width="800"
            :zIndex="3"
            :centered="true"
            @cancel="addSystemInvoiceListModal.alert = false"
            class="modal-form-input-scoped gotdown-info-modal global-drag add-system-invoice-list"
        >
            <div v-globalDrag="{ el: 'add-system-invoice-list' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addSystemInvoiceListModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="close" @click="addSystemInvoiceListModalFn('close')"> 取消 </a-button>
                <a-button
                    key="save"
                    type="primary"
                    :loading="addSystemInvoiceListModal.loading"
                    @click="addSystemInvoiceListModalFn('save')"
                >
                    添加
                </a-button>
            </template>
            <add-system-invoice-list
                v-if="addSystemInvoiceListModal.alert"
                ref="addSystemInvoiceRef"
                :modalTitle="modalTitle"
                :formData="formData"
                :isInbound="isInbound"
                :isShowSecondTab="isInbound"
            ></add-system-invoice-list>
        </a-modal>
        <!-- 出入库发票的添加产品列表区域结束 -->
    </div>
</template>

<script>
import addSystemInvoiceList from '@/views/commonPage/invoiceManage/components/addSystemInvoiceList/invoiceMain';

export default {
    name: 'invoiceTable', // 入库|出库发票的模态框中间部分表格
    props: {
        isInbound: {
            type: Boolean, // 是否是出入库发票 === 默认是true
            default: true
        },
        isShowActionColumn: {
            type: Boolean, // 是否显示操作栏
            default: true
        },
        modalTitle: {
            type: String, // 当前是入库还是出库的文字
            default: ''
        },
        formData: {
            type: Object, // 相关机构的数据
            required: true
        },
        columns: {
            type: Array, // 表格的头部
            required: true
        },
        datas: {
            type: Array, // 表格的数据
            required: true
        },
        pagation: {
            type: Object, // 表格的分页
            required: true
        },
        isReadonly: {
            // 是否只读
            type: Boolean,
            default: false
        },
        tabsActiveKey: {
            // 模态框窗口tab选项卡当前的key标识，必传
            type: String,
            required: true
        }
    },
    components: {
        addSystemInvoiceList
    },
    data() {
        return {
            addSystemInvoiceListModal: {
                title: `${this.$route.meta.title} - 添加发票`,
                alert: false,
                loading: false
            }
        };
    },
    methods: {
        delFn(_, index) {
            this.$emit('delTableData', index);
        },
        dblclickAddProductFn() {
            if (this.isReadonly) return;

            if (!this.formData.organization_inner_sn && !this.formData.organization_name) {
                this.$warning({
                    title: '操作提示',
                    content: '请先选择相关机构',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            this.addSystemInvoiceListModal.alert = true;
            this.$nextTick(() => {
                this.$Utils.globalDragCenterFn('add-system-invoice-list');
            });
        },
        addSystemInvoiceListModalFn(actionName) {
            if (actionName === 'close') {
                this.addSystemInvoiceListModal.alert = false;
            } else if (actionName === 'save') {
                this.addSystemInvoiceListModal.loading = true;
                const addSystemInvoiceRef = this.$refs.addSystemInvoiceRef,
                    selectedData = addSystemInvoiceRef.getSelectedDataFn();

                if (Object.keys(selectedData).length <= 0) {
                    this.$warning({
                        title: '操作提示',
                        content: '请添加发票',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                } else {
                    /// 构造并回传数据
                    const selectedTempDatas = [];
                    for (let key in selectedData) {
                        selectedTempDatas.push(selectedData[key]);
                    }
                    this.$emit('getTableData', selectedTempDatas);

                    this.addSystemInvoiceListModal.alert = false;
                }
                this.addSystemInvoiceListModal.loading = false;
            }
        }
    }
};
</script>

<style lang="less" scoped></style>
