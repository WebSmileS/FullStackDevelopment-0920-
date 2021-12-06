<template>
    <div>
        <!-- 双击已发放底部表格列表 - 发放明细查看区域开始 -->
        <a-modal
            :visible="visible"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-apply-send global-drag"
            :width="1100"
            :zIndex="3"
            @cancel="onCancel"
        >
            <div v-globalDrag="{ el: 'modal-apply-send' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ title }}
                </h6>
            </div>
            <template slot="footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button @click="handlePrint('xApplySendModalTable', true)"> 打印 </a-button>
                        <a-button @click="exportExcel('xApplySendModalTable')"> 导出Excel </a-button>
                    </li>
                    <li>
                        <a-button key="close" @click="onCancel"> 关闭 </a-button>
                        <a-button :disabled="!sheet.canCalim" key="confirm" @click="onConfirm" type="primary">
                            全部确认领取
                        </a-button>
                    </li>
                </ul>
            </template>
            <div class="modal-form-input for-single">
                <h6 class="title">
                    <span style="margin-right: 10px; font-size: 24px; color: #444">{{ orgName }}</span>
                    <span style="font-size: 24px; color: #444">发放单</span>
                    <div style="position: absolute; right: 0; display: flex">
                        <span class="label" style="font-size: 14px; color: rgba(0, 0, 0, 0.65)"
                            ><em class="em red">*</em>单据编号</span
                        >
                        <a-input disabled v-model="sheet.gv_sn" placeholder="" class="input" />
                    </div>
                </h6>
                <a-row>
                    <ul class="top-list" :style="'width:60%'">
                        <li class="item">
                            <span class="label"><em class="em red">*</em>部门</span>
                            <a-tree-select
                                disabled
                                :showSearch="false"
                                :treeData="[]"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                treeDefaultExpandAll
                                v-model="sheet.department_name"
                                :allowClear="false"
                                class="input"
                                style="min-width: 150px"
                            >
                            </a-tree-select>
                        </li>
                        <li class="item">
                            <span class="label" style="width: 100px"><em class="em red">*</em>发放仓库</span>
                            <a-select
                                disabled
                                :options="warehouse_list"
                                v-model="sheet.warehouse_inner_sn"
                                placeholder=""
                            />
                        </li>
                    </ul>
                </a-row>
                <div
                    class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                    style="position: static; height: 291px"
                >
                    <vxe-table
                        ref="xApplySendModalTable"
                        size="small"
                        height="266"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        align="center"
                        :data="data"
                        :loading="loading"
                        show-overflow
                        show-header-overflow
                        showHeader
                    >
                        <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                            <template v-slot="{ seq }">
                                {{ seq }}
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            field="product_name"
                            title="产品"
                            width="30%"
                            :cell-render="{ name: 'product_name' }"
                        >
                        </vxe-table-column>
                        <vxe-table-column
                            field="specification"
                            title="规格型号"
                            width="10%"
                            :cell-render="{ name: 'specification' }"
                        >
                        </vxe-table-column>
                        <vxe-table-column
                            field="vendor_name"
                            title="厂商/经销商"
                            width="20%"
                            :cell-render="{ name: 'vendor_name' }"
                        >
                        </vxe-table-column>
                        <vxe-table-column
                            field="batch_number"
                            title="批号"
                            width="10%"
                            :cell-render="{ name: 'batch_number' }"
                        >
                        </vxe-table-column>
                        <vxe-table-column
                            field="grant_quantity"
                            title="批准发放数量"
                            width="10%"
                            :cell-render="{ name: 'grant_quantity' }"
                        >
                        </vxe-table-column>
                        <vxe-table-column field="get_quantity" title="领取数量" width="10%">
                            <template v-slot="{ row }">
                                <a-input-number
                                    :disabled="row.ClaimDone"
                                    :min="0"
                                    v-model="row.get_quantity"
                                    class="input"
                                    style="width: 100%"
                                />
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            field="unit_name"
                            title="包装单位"
                            width="10%"
                            :cell-render="{ name: 'unit_name' }"
                        >
                        </vxe-table-column>
                        <vxe-table-column field="action" title="操作" width="10%" fixed="right">
                            <template v-slot="{ row, seq }">
                                <!-- 只有status = 1才可以领取 -->
                                <ul
                                    v-if="parseInt(row.status) === 1"
                                    class="action-btn-list-wrap"
                                    style="position: relative"
                                >
                                    <li>
                                        <a-tooltip class="tooltip" placement="top" title="领取">
                                            <i
                                                @click.stop="hospitalGrantApplyConfirmAPIFn(row, seq)"
                                                class="yrt yrt-app-shenling"
                                            ></i>
                                        </a-tooltip>
                                    </li>
                                </ul>
                            </template>
                        </vxe-table-column>
                    </vxe-table>
                </div>
                <ul>
                    <li class="item">
                        <span class="label" style="width: auto">摘要</span>
                        <a-input disabled v-model="sheet.description" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <div class="item-children" style="min-width: 120px; width: auto">
                            <span class="label" style="width: auto">发放人</span>
                            <span>{{ sheet.approver_name || '-' }}</span>
                        </div>
                        <div class="item-children" style="min-width: 120px; width: auto">
                            <span class="label" style="width: auto">领取人</span>
                            <span>{{ sheet.claim_name || '-' }}</span>
                        </div>
                        <div class="item-children" style="min-width: 120px; width: auto">
                            <span class="label" style="width: auto">发放时间</span>
                            <span>{{ sheet.grant_time || '-' }}</span>
                        </div>
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 双击已发放底部表格列表 - 发放明细查看区域结束 -->
    </div>
</template>

<script>
import { hospitalGrantApplyConfirmAPI } from '@/service/pageAjax';

export default {
    name: 'applyInfoModal',
    props: {
        visible: Boolean
    },
    data() {
        return {
            title: '',
            loading: false,
            data: [],
            sheet: {
                gv_sn: '',
                department_name: '',
                description: '',
                approver_name: '',
                grant_time: '',
                gv_inner_sn: '',
                claim_name: '',
                warehouse_inner_sn: '',
                canCalim: true
            },
            warehouse_list: []
        };
    },
    computed: {
        // 机构名称
        orgName() {
            const employeeInfo = this.$cookie.get('EmployeeInfo');
            return (employeeInfo ? JSON.parse(employeeInfo).OrgName : '') || '';
        }
    },
    methods: {
        /**
         * 领取按钮接口
         */
        async hospitalGrantApplyConfirmAPIFn(record, index) {
            let ainfo = JSON.parse(this.$cookie.get('EmployeeInfo'));
            let dataList = [
                {
                    'detail_inner_sn': record.detail_inner_sn,
                    'get_quantity': record.get_quantity,
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
                    this.data[index - 1].ClaimDone = true;
                    this.$refs.xApplySendModalTable.updateData(this.data);
                    let flag = true;
                    this.data.forEach((e) => {
                        if (e.ClaimDone === false) {
                            flag = false;
                        }
                    });
                    this.sheet.canCalim = !flag;
                    this.$emit('onClaimSuccess');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /**
         * 领取按钮接口
         */
        async hospitalGrantApplyConfirmAllAPIFn(records) {
            let ainfo = JSON.parse(this.$cookie.get('EmployeeInfo')),
                dataList = [];

            if (records && records.length) {
                for (let i = 0; i < records.length; i++) {
                    let item = records[i];
                    // 只有已生成出库单的才可以领取
                    if (parseInt(item.status) === 1) {
                        dataList.push({
                            detail_inner_sn: item.detail_inner_sn,
                            get_quantity: item.get_quantity,
                            product_type: item.product_type,
                            receiver_inner_sn: ainfo.Employee_inner_sn, // 领取人内部编号
                            receiver_name: ainfo.Name // 领取人姓名
                        });
                    }
                }
            }

            if (dataList.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '没有可以领取的产品！',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return;
            }

            let params = {
                'gid': this.sheet.gv_inner_sn,
                'oid': this.$cookie.get('userbelong'),
                'details': dataList
            };

            await hospitalGrantApplyConfirmAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.$message.success('领取成功');
                    this.onCancel();
                    this.$emit('onClaimSuccess');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        initPage(record) {
            this.sheet.gv_sn = record.gv_sn;
            this.sheet.gv_inner_sn = record.gv_inner_sn;
            this.sheet.department_name = record.department_name;
            this.sheet.hospital_inner_sn = record.hospital_inner_sn;
            this.sheet.description = record.description;
            this.sheet.approver_name = record.approver_name;
            this.sheet.grant_time = record.grant_time;
            this.sheet.claim_name = record.receiver_name;
            this.sheet.warehouse_inner_sn = record.warehouse_inner_sn;
            this.warehouse_list.push({ value: record.warehouse_inner_sn, label: record.warehouse_name });
            this.sheet.canCalim = parseInt(record.status) !== 2;
            this.data = record.details.map((m) => {
                m.ClaimDone = parseInt(m.status) === 2;
                m.get_quantity = m.ClaimDone ? m.get_quantity : m.grant_quantity;
                return m;
            });
        },
        clearPage() {},
        onConfirm() {
            this.hospitalGrantApplyConfirmAllAPIFn(this.data);
        },
        onCancel() {
            this.$emit('cancel');
        }
    }
};
</script>

<style></style>
