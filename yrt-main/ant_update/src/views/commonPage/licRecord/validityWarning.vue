<template>
    <div class="scoped agency-approval-container">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="vCheckNew()" type="primary" size="small"> 效期检测 </a-button>
                            <a-button @click="sendWarning()" type="primary" size="small" :disabled="sendSatus">
                                发送过期警告
                            </a-button>
                        </li>
                        <li>
                            <a-button @click="vSetConfig()" type="primary" size="small"> 预警设置 </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12"> </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->
        <!-- 上半部tab切换区域开始 -->
        <div class="tab-table-wrap cur-overflow-hidden tab-table-overflowy" style="padding: 8px; background: #fff">
            <a-table
                :rowSelection="rowSelection"
                :columns="columns"
                :dataSource="tableData"
                bordered
                :scroll="{ y: tabScrollHeight }"
                :pagination="false"
                :rowClassName="rowClassName"
            >
                <span slot="tags" slot-scope="tags">
                    <div class="blue">{{ tags }}</div>
                </span>
            </a-table>
        </div>
        <!-- 上半部tab切换区域结束 -->
        <!-- 模态框区域 -->
        <a-modal
            v-model="saveConfigStatus"
            :width="600"
            :centered="true"
            :maskClosable="false"
            okText="保存"
            @ok="saveConfig"
            @cancel="closeConfig"
            class="modal-form-input-scoped global-drag validity-warning"
        >
            <div v-globalDrag="{ el: 'validity-warning' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ configTitle }}
                </h6>
            </div>
            <div class="modal-form-input">
                <ul>
                    <li class="item" style="width: 200px">
                        <span class="label" style="width: 60px"><em class="em red">*</em>提前</span>
                        <a-input-number
                            :min="0"
                            :max="120"
                            :defaultValue="orgInfo.cew_day"
                            size="small"
                            v-model="orgInfo.cew_day"
                            @change="cewDayChange"
                            class="input"
                        />
                        <span class="label" style="width: 60px; margin-left: 8px">天&nbsp;预警</span>
                    </li>
                    <li>
                        <a-tabs type="card" defaultActiveKey="1">
                            <a-tab-pane tab="邮件模板" key="1">
                                <div class="validity-chose">
                                    <a-checkbox v-model="orgInfo.cew_mail_template_default">采用默认模板</a-checkbox>
                                </div>
                                <div>
                                    <a-textarea
                                        maxlength="500"
                                        :disabled="orgInfo.cew_mail_template_default"
                                        placeholder="请输入邮件模板"
                                        v-model="orgInfo.cew_mail_template"
                                        :autosize="{ minRows: 6, maxRows: 8 }"
                                    />
                                </div>
                            </a-tab-pane>
                            <a-tab-pane tab="微信模板" disabled key="2"></a-tab-pane>
                        </a-tabs>
                    </li>
                </ul>
            </div>
        </a-modal>
    </div>
</template>

<script>
import moment from 'moment';
import { warningInfoAPI, cewlistAPI, waringAddAPI, sendWaringAPI } from '@/service/pageAjax';
export default {
    name: 'validityWarning',
    data() {
        return {
            systemType: 0, // 机构类型
            orgInfo: {
                cew_day: 0,
                cew_mail_template: '',
                cew_mail_template_default: false,
                cew_note_template: '',
                cew_note_template_default: false,
                cew_wechat_template: '',
                cew_wechat_template_default: false,
                org_id: 0
            },
            isWarningInfo: true,
            saveConfigStatus: false,
            columns: [
                {
                    title: '资证名称',
                    dataIndex: 'certification_name',
                    align: 'center',
                    width: '20%',
                    scopedSlots: { customRender: 'tags' }
                },
                {
                    title: '过期日期',
                    dataIndex: 'expiration_date',
                    align: 'center',
                    width: '20%'
                },
                {
                    title: '统一社会信用代码',
                    dataIndex: 'social_credit_code',
                    align: 'center',
                    width: '25%'
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    align: 'center',
                    width: '15%'
                },
                {
                    title: '邮箱',
                    dataIndex: 'email',
                    align: 'center',
                    width: '20%'
                }
            ],
            rowSelection: {
                onSelect: (record, selected, selectedRows) => {
                    let cewStr = '';
                    for (let i = 0; i < selectedRows.length; i++) {
                        if (i < selectedRows.length - 1) {
                            cewStr += selectedRows[i].certification_name + ',';
                        } else {
                            cewStr += selectedRows[i].certification_name;
                        }
                    }
                    this.warningConfigSet.certification_name = cewStr;
                    this.warningConfigSet.email = record.email;
                    this.warningConfigSet.number = selectedRows.length;
                },
                onSelectAll: (selected, selectedRows) => {
                    let cewStr = '';
                    let cewEmail = '';
                    for (let i = 0; i < selectedRows.length; i++) {
                        if (i < selectedRows.length - 1) {
                            cewStr += selectedRows[i].certification_name + ',';
                        } else {
                            cewStr += selectedRows[i].certification_name;
                            cewEmail = selectedRows[i].email;
                        }
                    }
                    this.warningConfigSet.certification_name = cewStr;
                    this.warningConfigSet.email = cewEmail;
                    this.warningConfigSet.number = selectedRows.length;
                }
            },
            isFirstGetList: true, // 是否第一次进入页面
            editInfoFormObj: {
                cew_day: 0,
                email: ''
            },
            configTitle: '预警设置',
            warningConfigSet: {
                certification_name: '',
                email: '',
                number: 0
            },
            tableData: [],
            sendSatus: false,
            format: 'YYYY-MM-DD'
        };
    },
    methods: {
        moment,
        // 获取预警设置信息
        async warningInfoFn() {
            await warningInfoAPI(this.orgInfo.org_id).then((res) => {
                if (res.code === 0) {
                    this.isWarningInfo = false;
                    const { info } = res;
                    this.orgInfo = { ...info };
                } else {
                    this.$message.error(res.Msg);
                }
                // this.cewlistFn()
            });
        },
        // 获取预警列表
        async cewlistFn() {
            // let params = {
            //     org_id: this.orgInfo.org_id,
            //     cew_day: this.orgInfo.cew_day
            // }
            await cewlistAPI().then((res) => {
                if (res.code === 0) {
                    let list = res.rows,
                        nowDate = moment(new Date()).format(this.format).replace(/-/g, '');
                    list = list.map((item) => {
                        item.key = item.certification_name + '-' + item.org_id;
                        item.expiry_date =
                            nowDate - moment(item.expiration_date).format(this.format).replace(/-/g, '') > 0;
                        return item;
                    });
                    this.tableData = list;
                    if (!this.isFirstGetList) {
                        if (res.total > 0) {
                            this.$warning({
                                title: '效期检测',
                                content: '系统检测到有' + res.total + '个资证即将过期或已过期，请尽快进行更新。',
                                centered: true,
                                okText: '关闭',
                                maskClosable: false
                            });
                        } else {
                            this.$warning({
                                title: '效期检测',
                                content: '系统没有检测到即将过期的资证。',
                                centered: true,
                                okText: '关闭',
                                maskClosable: false
                            });
                        }
                    }
                } else {
                    // this.$message.error(res.Msg)
                }
            });
        },
        // 预警设置
        async waringAddFn() {
            await waringAddAPI({
                ...this.orgInfo,
                org_id: this.orgInfo.org_id || parseInt(this.$cookie.get('userbelong')),
                cew_mail_template_default: this.orgInfo.cew_mail_template_default || false
            }).then((res) => {
                if (res.code === 0) {
                    //
                } else {
                    // this.$message.error(res.Msg)
                }
                this.saveConfigStatus = false;
            });
        },
        // 发送过期警告
        async sendWaringFn() {
            if (!this.warningConfigSet.certification_name && !this.warningConfigSet.email) {
                this.$warning({
                    title: '操作提示',
                    content: '请先勾选表格中的复选框，再进行相关操作。',
                    centered: true,
                    okText: '关闭',
                    maskClosable: false
                });
                return;
            }
            await sendWaringAPI(this.warningConfigSet).then((res) => {
                if (res.code === 0) {
                    this.$warning({
                        title: '发送过期警告',
                        content: '系统已向邮箱发送过期警告，请及时处理。',
                        centered: true,
                        okText: '关闭',
                        maskClosable: false
                    });
                } else {
                    // this.$message.error(res.Msg)
                }
            });
        },
        /* 按钮区域的操作 */
        getCurrentStyle(current) {
            const style = {};
            if (current.date() === 1) {
                style.border = '1px solid #1890ff';
                style.borderRadius = '50%';
            }
            return style;
        },
        // 效期检测
        vCheckNew() {
            this.isFirstGetList = false;
            this.cewlistFn();
        },
        // 发送过期警告
        sendWarning() {
            this.sendWaringFn();
        },
        // 预警设置
        vSetConfig() {
            if (this.isWarningInfo) {
                this.warningInfoFn();
            }
            this.saveConfigStatus = true;
            this.$Utils.globalDragCenterFn('validity-warning');
        },
        // 预警天数变化
        cewDayChange(val) {
            // 天数变化
            if (!val) {
                this.orgInfo.cew_day = 0;
            }
        },
        // 保存预警设置
        saveConfig() {
            this.waringAddFn();
        },
        // 关闭预警设置
        closeConfig() {
            this.saveConfigStatus = false;
        },
        rowClassName(record) {
            // 背景颜色在App.vue设置
            if (record.expiry_date) return 'current-bg';
            return '';
        }
    },
    created() {
        this.orgInfo.org_id = parseInt(this.$cookie.get('userbelong'));
        this.systemType = parseInt(this.$cookie.get('userSystemType'));
        this.isFirstGetList = true;
        this.cewlistFn();
    }
};
</script>

<style lang="less" scoped>
@h: 24px;
@w: 16px;
.ant-tabs-tab {
    padding: 0 10px;
}
.scoped {
    .ant-tabs-card-content {
        height: 100%;
    }
}
.validity-chose {
    margin-bottom: 12px;
}
.agency-approval-container {
    /* 水平垂直居中 */
    height: 100%;
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    .center {
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }
    .tab-table-wrap {
        height: calc(100% - 58px);
        .ant-tabs-card-content {
            height: 100%;
        }
    }
}
.modal-form-input-scoped {
    .modal-form-input {
        .ant-input-number {
            width: 120px;
        }
        .label-1 {
            width: 80px;
        }
    }
}
</style>
