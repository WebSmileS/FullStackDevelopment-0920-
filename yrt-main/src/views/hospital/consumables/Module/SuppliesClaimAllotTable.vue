<template>
    <div>
        <vxe-grid
            ref="PendingOutTable"
            size="small"
            :height="boxDownHeight - 38"
            :data="gridData.Datas"
            :loading="gridData.Loading"
            border
            resizable
            auto-resize
            highlight-current-row
            @cell-click="tableCellClickFn"
            @cell-dblclick="tableCellDblclickFn"
            @checkbox-change="selectChangeEvent"
            style="margin-top: 8px"
            align="center"
            show-overflow
            show-header-overflow
            showHeader
        >
            <vxe-table-column type="checkbox" title="全选" width="60"> </vxe-table-column>
            <vxe-table-column key="index" field="index" title="No." minWidth="5" :resizable="false">
                <template v-slot="{ seq }">
                    <span>{{ seq }}</span>
                </template>
            </vxe-table-column>
            <vxe-table-column
                v-for="commonTableColumnItem in gridColumn"
                :key="commonTableColumnItem.key"
                :field="commonTableColumnItem.dataIndex"
                :title="commonTableColumnItem.title"
            ></vxe-table-column>
            <vxe-table-column field="action" title="操作" minWidth="10" fixed="right">
                <template v-slot="{ row, seq }">
                    <ul class="action-btn-list-wrap" style="position: relative">
                        <li v-if="row.sheet_type_is_allot && (row.status === 'CONFIRM' || row.status === 'DRAFT')">
                            <a-tooltip class="tooltip" placement="top" title="作废">
                                <i @click.stop="handleDiscard(row, seq)" class="yrt yrt-zhongzhi-gaoduan cancel"></i>
                            </a-tooltip>
                        </li>
                        <li v-if="row.sheet_type_is_allot && row.status === 'OUTPUT'">
                            <a-tooltip class="tooltip" placement="top" title="领取">
                                <i @click.stop="handleClaim(row, seq)" class="yrt yrt-app-shenling"></i>
                            </a-tooltip>
                        </li>
                        <!-- 只有已生成出库单的才可以领取 -->
                        <li v-if="!row.sheet_type_is_allot && row.status === 1">
                            <a-tooltip class="tooltip" placement="top" title="确认领取">
                                <i
                                    @click.stop="handleClaim(row, seq)"
                                    class="yrt yrt-app-shenling"
                                    style="margin-left: 14px; font-size: 18px"
                                ></i>
                            </a-tooltip>
                        </li>
                    </ul>
                </template>
            </vxe-table-column>
        </vxe-grid>
        <applyInfoModal
            ref="applyInfoModal"
            :visible="applyInfoModal.visible"
            @cancel="onApplyInfoModalCancel"
            @onClaimSuccess="initPage(currentRecord)"
        />
        <addTransferSheetModal
            ref="addTransferSheetModal"
            :visible="addTransferSheetModal.visible"
            @onCancel="onAddTransferSheetModalCancel"
            @onConfirmFinished="initPage(currentRecord)"
        />
    </div>
</template>

<script>
import {
    hospitalGrantGaListAPI,
    hospitalApplyDetailDelAPI,
    hospitalApplyDetailStopAPI,
    hospitalGrantApplyConfirmAPI,
    editWarehouseTransferListAPI
} from '@/service/pageAjax';

import addTransferSheetModal from '../../../commonPage/warehouseManage/warehouseTransfer/module/addTransferSheetModal';
import applyInfoModal from './applyInfoModal';

export default {
    name: 'warehouseTransfer',
    props: {
        boxDownHeight: Number,
        isDispatch: Boolean,
        orderStatus: {
            type: String
        }
    },
    components: {
        addTransferSheetModal,
        applyInfoModal
    },
    data() {
        return {
            // 表格数据
            gridData: {
                // 表格的数据
                Datas: [],
                // 是否加载中
                Loading: false,
                // 当前选中表格行数据
                currentSelectRow: {},
                // 当前选中表格行序号
                currentSelectRecords: []
            },
            currentRecord: {},
            gridColumn: [],
            rawData: {},
            addTransferSheetModal: {
                visible: false
            },
            applyInfoModal: {
                visible: false
            },
            // 数据列设置
            hasTargetWarehouse: [
                {
                    title: '编号',
                    dataIndex: 'av_sn',
                    key: 'av_sn',
                    align: 'center'
                },
                {
                    title: '发出仓库',
                    dataIndex: 'source_warehouse_name',
                    key: 'source_warehouse_name',
                    align: 'center'
                },
                {
                    title: '目标仓库',
                    dataIndex: 'target_warehouse_name',
                    key: 'target_warehouse_name',
                    align: 'center'
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    align: 'center'
                },
                {
                    title: '厂商',
                    dataIndex: 'vd_name',
                    key: 'vd_name',
                    align: 'center'
                },
                {
                    title: '单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: 'quantity',
                    align: 'center'
                },
                {
                    title: '售价',
                    dataIndex: 'salesPrice',
                    key: 'salesPrice',
                    align: 'center'
                },
                {
                    title: '日期',
                    dataIndex: 'expect_date',
                    key: 'expect_date',
                    align: 'center'
                },
                {
                    title: '经手人',
                    dataIndex: 'poster_name',
                    key: 'poster_name',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status_name',
                    key: 'status_name',
                    align: 'center'
                }
            ].filter((column) => {
                if (column.dataIndex !== 'salesPrice') {
                    return true;
                }
                // 发放单
                if (this.isDispatch) {
                    if (this.orderStatus !== 'waiting') {
                        return true;
                    }
                }
                // 申领单
                if (!this.isDispatch) {
                    if (this.orderStatus !== 'waiting') {
                        return true;
                    }
                }
                return false;
            }),
            noTargetWarehouse: [
                {
                    title: '编号',
                    dataIndex: 'av_sn',
                    key: 'av_sn',
                    align: 'center'
                },
                {
                    title: '发出仓库',
                    dataIndex: 'source_warehouse_name',
                    key: 'source_warehouse_name',
                    align: 'center'
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    align: 'center'
                },
                {
                    title: '厂商',
                    dataIndex: 'vd_name',
                    key: 'vd_name',
                    align: 'center'
                },
                {
                    title: '单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
                    align: 'center'
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    key: 'quantity',
                    align: 'center'
                },
                {
                    title: '售价',
                    dataIndex: 'salesPrice',
                    key: 'salesPrice',
                    align: 'center'
                },
                {
                    title: '日期',
                    dataIndex: 'expect_date',
                    key: 'expect_date',
                    align: 'center'
                },
                {
                    title: '经手人',
                    dataIndex: 'poster_name',
                    key: 'poster_name',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status_name',
                    key: 'status_name',
                    align: 'center'
                }
            ].filter((column) => {
                if (column.dataIndex !== 'salesPrice') {
                    return true;
                }
                // 待发放、待申领
                if (this.orderStatus !== 'waiting') {
                    return true;
                }
                // 已发放、已申领
                if (this.orderStatus === 'waiting') {
                    return false;
                }
                return false;
            })
        };
    },
    methods: {
        /** ********************** 接口区域开始 ********************************************/

        /**
         * 获取调拨单数据接口调用
         * @async api:getWarehouseTransferListAPIFnAsync
         * @param tabsKey 当前tabs数据，与this.tabs.tabsKey相同
         * @param avSn 可空，具体单据编号
         */
        async hospitalGrantGaListAPIFnAsync(record) {
            // 如果当前申领单没有仓库，则返回的是发放单，否则就是调拨单
            const isAllot = record.warehouse_inner_sn !== null;
            await hospitalGrantGaListAPI(record.hospital_inner_sn, record.voucher_inner_sn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    if (list.length === 0) {
                        this.gridData.Loading = false;
                        return;
                    }
                    this.rawData = list;
                    // 如果返回的是发放单
                    if (!isAllot) {
                        list.map((item) => {
                            if (!item.details) {
                                return;
                            }
                            item.details.map((m) => {
                                // 将接口返回的状态枚举转为文字
                                const statusCode = m.status;
                                switch (statusCode) {
                                    case 0:
                                        m.status_name = '待生成出库单';
                                        break;
                                    case 1:
                                        m.status_name = '出库单已完全生成';
                                        break;
                                    case 2:
                                        m.status_name = '已领取';
                                        break;
                                }
                                let target = {
                                    av_sn: item.gv_sn,
                                    sheet_type_is_allot: false,
                                    source_warehouse_name: m.warehouse_name,
                                    target_warehouse_name: '',
                                    product_name: m.product_name,
                                    specification: m.specification,
                                    vd_name: m.vendor_name,
                                    unit_name: m.unit_name,
                                    quantity: m.grant_quantity,
                                    expect_date: item.grant_time,
                                    poster_name: item.approver_name,
                                    status_name: m.status_name,
                                    ...m
                                };
                                this.gridData.Datas.push(target);
                            });
                        });
                    } else {
                        list.map((m) => {
                            // 将接口返回的状态枚举转为文字
                            const statusCode = m.status;
                            switch (statusCode) {
                                case 'DRAFT':
                                    m.status_name = '草稿';
                                    break;
                                case 'CONFIRM':
                                    m.status_name = '确认';
                                    break;
                                case 'OUTPUT':
                                    m.status_name = '已出库';
                                    break;
                                case 'COMPLETED':
                                    m.status_name = '完结';
                                    break;
                                case 'DISCARD':
                                    m.status_name = '作废';
                                    break;
                            }
                            let target = {
                                av_sn: m.av_sn,
                                sheet_type_is_allot: true,
                                source_warehouse_name: m.source_warehouse_name,
                                target_warehouse_name: m.target_warehouse_name,
                                product_name: m.product_name,
                                specification: m.specification,
                                vd_name: m.vd_name,
                                unit_name: m.unit_name,
                                quantity: m.allot_quantity,
                                expect_date: m.expect_date,
                                poster_name: m.poster_name,
                                status_name: m.status_name,
                                ...m
                            };
                            this.gridData.Datas.push(target);
                        });
                    }

                    this.$refs.PendingOutTable.loadData(this.gridData.Datas);
                    this.gridData.Loading = false;
                }
            });
        },
        // 【终止】申领材料-明细
        async hospitalApplyDetailStopAPIFn(record) {
            this.gridData.loading = true;
            let params = {
                hospital_inner_sn: record.hospital_inner_sn || record.org_id,
                product_type: record.product_type
            };
            if (record.detail_inner_sn) {
                params.detail_id = record.detail_inner_sn;
            }
            await hospitalApplyDetailStopAPI(params)
                .then((res) => {
                    this.gridData.loading = false;
                    if (parseFloat(res.code) === 0) {
                        this.initPage(this.currentRecord);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.gridData.loading = false;
                });
        },
        // 【删除】申领材料-明细
        async hospitalApplyDetailDelAPIFn(record) {
            this.gridData.loading = true;
            let params = {
                hospital_inner_sn: record.hospital_inner_sn,
                product_type: record.product_type
            };
            if (record.detail_inner_sn) {
                params.detail_id = record.detail_inner_sn;
            }
            await hospitalApplyDetailDelAPI(params)
                .then((res) => {
                    this.gridData.loading = false;
                    if (parseFloat(res.code) === 0) {
                        this.initPage(this.currentRecord);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.gridData.loading = false;
                });
        },
        /**
         * 领取按钮接口
         */
        async hospitalGrantApplyConfirmAPIFn(record) {
            let ainfo = JSON.parse(this.$cookie.get('EmployeeInfo'));
            let dataList = [
                {
                    'detail_inner_sn': record.detail_inner_sn,
                    'get_quantity': record.allot_quantity || record.grant_quantity,
                    'product_type': record.product_type,
                    'receiver_inner_sn': ainfo.Employee_inner_sn, // 领取人内部编号
                    'receiver_name': ainfo.Name // 领取人姓名
                }
            ];
            let params = {
                'gid': record.gv_inner_sn || record.av_inner_sn,
                'oid': this.$cookie.get('userbelong'),
                'details': dataList
            };

            await hospitalGrantApplyConfirmAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.$message.success('领取成功');
                    this.initPage(this.currentRecord);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /**
         * 作废调拨单接口
         * @async
         */
        async discardWarehouseTransferListAPIFn(record) {
            let datas = [];
            record.status = 4;
            datas = [record];
            let params = datas;

            await editWarehouseTransferListAPI(params).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.$message.success('作废成功');
                    this.initPage(this.currentRecord);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /** ********************** 接口区域结束 ********************************************/

        /**
         * 单击表格列触发的方法
         * @param {row} 点击的行信息
         */
        tableCellClickFn({ row }) {
            this.gridData.currentSelectRow = row;
        },
        /**
         * 双击表格列触发的方法
         * @param {row} 点击的行信息
         */
        tableCellDblclickFn({ row }) {
            const sn = row.av_sn || row.gv_sn;
            if (sn.startsWith('DB')) {
                this.$Utils.globalDragCenterFn('warehouseTransfer-modal-add');
                this.addTransferSheetModal.visible = true;
                this.addTransferSheetModal.isAdd = false;
                if (this.$refs.addTransferSheetModal) {
                    this.$refs.addTransferSheetModal.setSearchSn(this.gridData.currentSelectRow.av_inner_sn);
                    this.$refs.addTransferSheetModal.initPage(false);
                    this.$refs.addTransferSheetModal.isFromDispatch = true;
                }
            } else {
                this.$Utils.globalDragCenterFn('modal-apply-send');
                this.applyInfoModal.visible = true;
                if (this.$refs.applyInfoModal) {
                    let d = {};
                    this.rawData.map((m) => {
                        if (m.gv_inner_sn === row.gv_inner_sn) {
                            d = m;
                        }
                    });
                    this.$refs.applyInfoModal.initPage(d);
                }
            }
        },
        /**
         * 初始化页面的方法
         */
        initPage(record) {
            this.currentRecord = record;
            this.gridColumn =
                this.currentRecord.warehouse_inner_sn === null ? this.noTargetWarehouse : this.hasTargetWarehouse;
            this.gridData.Datas = [];
            this.gridData.Loading = true;
            this.hospitalGrantGaListAPIFnAsync(record);
        },
        /**
         * 表格列多选绑定方法
         * @param {records} 选中的数据数组
         */
        selectChangeEvent({ records }) {
            this.gridData.currentSelectRecords = records;
        },
        /**
         * 表格列‘作废’按钮绑定方法
         * @param row 当前作废行信息
         * @abstract 标记当前按钮行为选中行，并调用delete方法
         */
        onDiscard(row) {
            this.gridData.currentSelectRow = row;
            this.headerBtnFn('delete');
        },
        /**
         * 中止绑定的方法
         */
        handleStop(record, index) {
            this.hospitalApplyDetailStopAPIFn(record, index);
        },
        /**
         * 删除绑定的方法
         */
        handleRemove(record, index) {
            this.hospitalApplyDetailDelAPIFn(record, index);
        },
        /**
         * 申领绑定的方法
         */
        handleClaim(record, index) {
            this.hospitalGrantApplyConfirmAPIFn(record, index);
        },
        /**
         * 作废绑定的方法
         */
        handleDiscard(record, index) {
            this.discardWarehouseTransferListAPIFn(record, index);
        },
        /**
         * 查看发放单窗口取消按钮绑定方法
         */
        onApplyInfoModalCancel() {
            this.applyInfoModal.visible = false;
        },
        /**
         * 查看发放单窗口取消按钮绑定方法
         */
        onAddTransferSheetModalCancel() {
            this.addTransferSheetModal.visible = false;
        }
    },
    mounted() {
        this.gridColumn = this.noTargetWarehouse;
    }
};
</script>

<style lang="less" scoped></style>
