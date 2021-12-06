<template>
    <div class="modal-list">
        <div class="img-count">
            <gallery
                :images="
                    entryFormInfo.images &&
                    entryFormInfo.images.filter((_) => Number(_.type) === Number(downTabsActiveKey))
                "
            />
        </div>
        <div class="agency-base-scope">
            <!-- tab切换 -->
            <div class="tab-table-wrap tab-table-overflowy">
                <a-tabs type="card">
                    <a-tab-pane tab="基本信息" key="base">
                        <div class="info-count modal-form-input">
                            <ul>
                                <li class="item-agency">
                                    <span class="label-short"><em class="em red">*</em>企业名称</span>
                                    <a-input v-model="entryFormInfo.name" class="input" />
                                </li>
                                <li class="item-agency" v-if="downTabsActiveKey === '2'">
                                    <template v-if="tabsActiveKey === 'vendor'">
                                        <span class="label-short"><em class="em red">*</em>许可证编号</span>
                                        <a-input v-model="entryFormInfo.license_sn" class="input input-short" />
                                    </template>
                                    <template v-else>
                                        <div class="item-children item-children-agency-approval">
                                            <span class="label-short"><em class="em red">*</em>许可证编号</span>
                                            <a-input v-model="entryFormInfo.license_sn" class="input input-short" />
                                        </div>
                                        <div class="item-children">
                                            <span class="label-short"><em class="em red">*</em>经营方式</span>
                                            <a-select
                                                :defaultValue="entryFormInfo.operation_mode || '0'"
                                                v-model="entryFormInfo.operation_mode"
                                                :style="{ 'width': '164px' }"
                                            >
                                                <a-select-option value="8">其它</a-select-option>
                                                <a-select-option value="1">批发</a-select-option>
                                                <a-select-option value="2">零售</a-select-option>
                                                <a-select-option value="3">制作</a-select-option>
                                                <a-select-option value="4">咨询</a-select-option>
                                                <a-select-option value="5">租赁</a-select-option>
                                                <a-select-option value="6">代理</a-select-option>
                                                <a-select-option value="7">采掘</a-select-option>
                                            </a-select>
                                        </div>
                                    </template>
                                </li>
                                <li class="item-agency" v-if="downTabsActiveKey === '3'">
                                    <div class="item-children item-children-agency-approval">
                                        <span class="label-short"><em class="em red">*</em>备案编号</span>
                                        <a-input v-model="entryFormInfo.record_sn" class="input input-short" />
                                    </div>
                                    <div class="item-children">
                                        <span class="label-short"><em class="em red">*</em>经营方式</span>
                                        <a-select
                                            :defaultValue="entryFormInfo.operation_mode || '0'"
                                            :style="{ 'width': '164px' }"
                                            v-model="entryFormInfo.operation_mode"
                                        >
                                            <a-select-option value="8">其它</a-select-option>
                                            <a-select-option value="1">批发</a-select-option>
                                            <a-select-option value="2">零售</a-select-option>
                                            <a-select-option value="3">制作</a-select-option>
                                            <a-select-option value="4">咨询</a-select-option>
                                            <a-select-option value="5">租赁</a-select-option>
                                            <a-select-option value="6">代理</a-select-option>
                                            <a-select-option value="7">采掘</a-select-option>
                                        </a-select>
                                    </div>
                                </li>
                                <li class="item-agency">
                                    <div class="item-children item-children-agency-approval">
                                        <span class="label-short"><em class="em red">*</em>法定代表人</span>
                                        <a-input
                                            v-model="entryFormInfo.legal_representative"
                                            class="input input-short"
                                        />
                                    </div>
                                    <div
                                        class="item-children"
                                        v-if="downTabsActiveKey === '2' || downTabsActiveKey === '3'"
                                    >
                                        <span class="label-short">企业负责人</span>
                                        <a-input v-model="entryFormInfo.principal" class="input input-short" />
                                    </div>
                                </li>
                                <li class="item-agency">
                                    <span class="label-short">住所</span>
                                    <a-input v-model="entryFormInfo.address" class="input" />
                                </li>
                                <template v-if="tabsActiveKey === 'dealer'">
                                    <li
                                        class="item-agency"
                                        v-if="downTabsActiveKey === '2' || downTabsActiveKey === '3'"
                                    >
                                        <span class="label-short">经营场所</span>
                                        <a-input v-model="entryFormInfo.premises" class="input" />
                                    </li>
                                    <li
                                        class="item-agency"
                                        v-if="downTabsActiveKey === '2' || downTabsActiveKey === '3'"
                                    >
                                        <span class="label-short">库房地址</span>
                                        <a-input v-model="entryFormInfo.warehouse_address" class="input" />
                                    </li>
                                </template>
                                <template v-if="tabsActiveKey === 'vendor'">
                                    <li
                                        class="item-agency"
                                        v-if="downTabsActiveKey === '2' || downTabsActiveKey === '3'"
                                    >
                                        <span class="label-short">生产地址</span>
                                        <a-input v-model="entryFormInfo.production_address" class="input" />
                                    </li>
                                </template>
                                <li class="item-agency" v-if="downTabsActiveKey === '2'">
                                    <div class="item-children item-children-agency-approval" style="width: 272px">
                                        <span class="label-short"><em class="em red">*</em>有效期限</span>
                                        <a-date-picker
                                            @change="entryEndDateFn"
                                            :defaultValue="entryEndDate"
                                            v-model="entryEndDate"
                                            format="YYYY-MM-DD"
                                            size="small"
                                            class="input input-short"
                                            placeholder=""
                                        />
                                    </div>
                                </li>
                                <li class="item-agency" v-if="downTabsActiveKey === '2'">
                                    <div
                                        class="item-children item-children-agency-approval"
                                        v-if="downTabsActiveKey === '1'"
                                    >
                                        <span class="label-short">登记机关</span>
                                        <a-input v-model="entryFormInfo.issue_organization" class="input" />
                                    </div>
                                    <div
                                        class="item-children item-children-agency-approval"
                                        v-if="downTabsActiveKey === '2'"
                                    >
                                        <span class="label-short"><em class="em red"></em>发证部门</span>
                                        <a-input v-model="entryFormInfo.issue_organization" class="input" />
                                    </div>
                                    <div class="item-children">
                                        <span class="label-short"><em class="em red">*</em>发证日期</span>
                                        <a-date-picker
                                            @change="entryIssueDateFn"
                                            :defaultValue="entryIssueDate"
                                            v-model="entryIssueDate"
                                            format="YYYY-MM-DD"
                                            size="small"
                                            class="input input-short"
                                            placeholder=""
                                        />
                                    </div>
                                </li>
                                <li class="item-agency item-children-agency-approval" v-if="downTabsActiveKey === '3'">
                                    <div class="item-children item-children-agency-approval">
                                        <span class="label-short"><em class="em red"></em>备案部门</span>
                                        <a-input v-model="entryFormInfo.issue_organization" class="input" />
                                    </div>
                                    <div class="item-children">
                                        <span class="label-short"><em class="em red">*</em>备案日期</span>
                                        <a-date-picker
                                            @change="entryIssueDateFn"
                                            :defaultValue="entryIssueDate"
                                            v-model="entryIssueDate"
                                            format="YYYY-MM-DD"
                                            size="small"
                                            class="input input-short"
                                            placeholder=""
                                        />
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </a-tab-pane>
                    <a-tab-pane v-if="tabsActiveKey !== 'vendor'" tab="经营范围" class="agency-scope" key="scope">
                        <a-tree
                            checkable
                            @expand="onExpand"
                            :expandedKeys="expandedKeys"
                            :autoExpandParent="autoExpandParent"
                            v-model="checkedKeys"
                            @select="onSelect"
                            @check="onCheck"
                            :selectedKeys="selectedKeys"
                            :treeData="treeData"
                        />
                    </a-tab-pane>
                    <a-tab-pane v-if="tabsActiveKey === 'vendor'" tab="生产范围" class="agency-scope" key="scope">
                        <a-tree
                            checkable
                            @expand="onExpand"
                            :expandedKeys="expandedKeys"
                            :autoExpandParent="autoExpandParent"
                            v-model="checkedKeys"
                            @select="onSelect"
                            @check="onCheck"
                            :selectedKeys="selectedKeys"
                            :treeData="treeData"
                        />
                    </a-tab-pane>
                </a-tabs>
            </div>
        </div>
    </div>
</template>
<script>
import {
    platformAgencyScopeTreeAPI,
    platformAgencyDealerAPI,
    platformAgencyAddDealerAPI,
    platformAgencyEditDealerAPI,
    platformAgencyDealerSecAPI,
    platformAgencyAddDealerSecAPI,
    platformAgencyEditDealerSecAPI,
    platformAgencyVendorAPI,
    platformAgencyAddVendorAPI,
    platformAgencyEditVendorAPI
} from '@/service/pageAjax';
import moment from 'moment';
import Gallery from '@/components/Picture/Gallery';

export default {
    moment,
    name: 'entryLicScope',
    components: {
        Gallery
    },
    props: ['tabsActiveKey', 'downTabsActiveKey', 'currentTrObj', 'entryConfirm'],
    data() {
        return {
            entryEstablishDate: moment[''],
            entryBeginDate: moment[''],
            entryEndDate: moment[''],
            entryIssueDate: moment[''],
            imgList: [],
            expandedKeys: [],
            checkedKeys: [],
            autoExpandParent: true,
            selectedKeys: [],
            entryFormInfo: {
                orgLicense: {},
                orgInfo: {},
                name: '',
                social_credit_code: '',
                enterprise_type: '',
                legal_representative: '',
                registered_capital: '',
                address: '',
                establish_date: '',
                begin_date: '',
                end_date: '',
                issue_date: '',
                issue_organization: '',
                business_scope: '',
                images: [],
                new: true
            },
            entryFormInfoOld: {},
            entryBEDATE: [],
            treeData: [],
            treeFormatData: []
        };
    },
    methods: {
        // 返回机构类型
        ReturnSysType(systemType) {
            let sysType;
            switch (systemType) {
                case 'pt':
                    break;
                case 'hospital':
                    sysType = 1;
                    break;
                case 'dealer':
                    sysType = 2;
                    break;
                case 'vendor':
                    sysType = 3;
                    break;
            }
            return sysType;
        },
        // 检测并设置修改过的修改信息内容字段
        checkEditInfoFormObjFn(oldForm, editForm) {
            let info = {
                editInfoFlag: true,
                newObj: {}
            };
            let newForm = {};
            for (let server in editForm) {
                let serverItem = editForm[server];
                for (let old in oldForm) {
                    let oldItem = oldForm[old];
                    if (server === old && serverItem !== oldItem) {
                        if (serverItem === null && oldItem === undefined) {
                            info.editInfoFlag = true;
                        } else if (serverItem === undefined && oldItem === null) {
                            info.editInfoFlag = true;
                        } else {
                            newForm = {
                                ...newForm,
                                [server]: serverItem, // 新值
                                [server + '_old']: oldItem // 旧值
                            };
                            info.editInfoFlag = false;
                            info.newObj = newForm;
                        }
                    }
                }
            }
            return info;
        },
        // 检测录入的表单信息校验
        checkEntryInfoFormMsgFn() {
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            // const socialCreditCode = /^(\d|[a-zA-Z])+$/
            // 名称 01
            if (!this.entryFormInfo.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                this.$emit('refuseEntry', false);
                return false;
            }
            // 名称 02
            if (!this.$Utils.trim(this.entryFormInfo.name)) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                this.entryFormInfo.name = this.$Utils.trim(this.entryFormInfo.name);
                this.$emit('refuseEntry', false);
                return false;
            }
            // 名称 03
            if (this.$Utils.checkInputValFn(this.entryFormInfo.name)) {
                this.$warning({
                    title: '操作提示',
                    content: '名称中请不要输入特殊字符',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                this.$emit('refuseEntry', false);
                return false;
            }
            // 法定代表人 01
            if (!this.entryFormInfo.legal_representative) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入法定代表人',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                this.$emit('refuseEntry', false);
                return false;
            }
            // 法定代表人 02
            if (!this.$Utils.trim(this.entryFormInfo.legal_representative)) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入法定代表人',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                this.entryFormInfo.legal_representative = this.$Utils.trim(this.entryFormInfo.legal_representative);
                this.$emit('refuseEntry', false);
                return false;
            }
            if (this.downTabsActiveKey === '2') {
                // 有效期限
                if (!this.entryFormInfo.end_date) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择有效期限',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // 发证日期
                if (!this.entryFormInfo.issue_date) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择发证日期',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
                const begin = this.entryFormInfo.issue_date || '';
                const end = this.entryFormInfo.end_date || '';
                // 校验发证日期需小于截止日期
                if (!moment(begin).isBefore(end)) {
                    this.$message.warning('发证日期需小于截止日期');
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // 许可证编号 01
                if (!this.entryFormInfo.license_sn) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入许可证编号',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // 许可证编号 02
                if (!this.$Utils.trim(this.entryFormInfo.license_sn)) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入许可证编号',
                        centered: true,
                        okText: '知道了',
                        maskClosable: false
                    });
                    this.entryFormInfo.license_sn = this.$Utils.trim(this.entryFormInfo.license_sn);
                    this.$emit('refuseEntry', false);
                    return false;
                }
            } else {
                // 备案编号 01
                if (!this.entryFormInfo.record_sn) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入备案编号',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // 备案编号 02
                if (!this.$Utils.trim(this.entryFormInfo.record_sn)) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入备案编号',
                        centered: true,
                        okText: '知道了',
                        maskClosable: false
                    });
                    this.entryFormInfo.record_sn = this.$Utils.trim(this.entryFormInfo.record_sn);
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // 备案日期
                if (!this.entryFormInfo.issue_date) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择备案日期',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
            }

            if (sysType === 2) {
                // 经营方式 01
                if (!this.entryFormInfo.operation_mode) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择经营方式',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // if (this.downTabsActiveKey === '2') {
                // }
            }
            return true;
        },
        // 获取经销商医疗器械经营许可证的所有信息
        async platformAgencyDealerAPIFn(id) {
            await platformAgencyDealerAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let org = res.info;
                    if (org.operation_mode) {
                        org.operation_mode = org.operation_mode.toString();
                    }
                    if (org.new) {
                        // 营业期限 | 有效期限 第一次录入取图片信息里的日期
                        this.initDateInfo(org);

                        this.entryIssueDate = moment[''];
                        this.entryEstablishDate = moment[''];
                    } else {
                        this.entryBEDATE[0] = moment(org.begin_date);
                        this.entryBEDATE[1] = moment(org.end_date);
                        this.entryBeginDate = moment(org.begin_date);
                        this.entryEndDate = moment(org.end_date);
                        this.entryIssueDate = moment(org.issue_date);
                        this.entryEstablishDate = moment(org.establish_date);
                    }
                    if (org.business_scope && org.business_scope.length > 0) {
                        let scopeStr = '[' + org.business_scope + ']';
                        this.checkedKeys = JSON.parse(scopeStr);
                    }
                    this.entryFormInfo = { ...org };
                    this.entryFormInfoOld = { ...org };
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 添加经销商医疗器械经营许可证的所有信息
        async platformAgencyAddDealerAPIFn(id, params) {
            await platformAgencyAddDealerAPI(id, params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.$emit('submitEntrySuccess', true);
                    this.$emit('submitEntryObj', this.entryFormInfo);
                    // this.$message.success(res.msg)
                } else {
                    this.$emit('submitEntrySuccess', false);
                    this.$message.error(res.msg);
                }
            });
        },
        // 编辑经销商医疗器械经营许可证的所有信息
        async platformAgencyEditDealerAPIFn(id, params) {
            await platformAgencyEditDealerAPI(id, params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.$emit('submitEntrySuccess', true);
                    this.$emit('submitEntryObj', this.entryFormInfo);
                    // this.$message.success(res.msg)
                } else {
                    this.$emit('submitEntrySuccess', false);
                    this.$message.error(res.msg);
                }
            });
        },
        // 获取经销商第二类医疗器械经营备案凭证的所有信息
        async platformAgencyDealerSecAPIFn(id) {
            await platformAgencyDealerSecAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let org = res.info;
                    if (org.operation_mode) {
                        org.operation_mode = org.operation_mode.toString();
                    }
                    if (org.new) {
                        this.entryBeginDate = moment[''];
                        this.entryEndDate = moment[''];
                        this.entryIssueDate = moment[''];
                        this.entryEstablishDate = moment[''];
                    } else {
                        this.entryBEDATE[0] = moment(org.begin_date);
                        this.entryBEDATE[1] = moment(org.end_date);
                        this.entryBeginDate = moment(org.begin_date);
                        this.entryEndDate = moment(org.end_date);
                        this.entryIssueDate = moment(org.issue_date);
                        this.entryEstablishDate = moment(org.establish_date);
                    }
                    if (org.business_scope && org.business_scope.length > 0) {
                        let scopeStr = '[' + org.business_scope + ']';
                        this.checkedKeys = JSON.parse(scopeStr);
                    }
                    this.entryFormInfo = { ...org };
                    this.entryFormInfoOld = { ...org };
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 添加经销商第二类医疗器械经营备案凭证的所有信息
        async platformAgencyAddDealerSecAPIFn(id, params) {
            await platformAgencyAddDealerSecAPI(id, params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.$emit('submitEntrySuccess', true);
                    this.$emit('submitEntryObj', this.entryFormInfo);
                    // this.$message.success(res.msg)
                } else {
                    this.$emit('submitEntrySuccess', false);
                    this.$message.error(res.msg);
                }
            });
        },
        // 编辑经销商第二类医疗器械经营备案凭证的所有信息
        async platformAgencyEditDealerSecAPIFn(id, params) {
            await platformAgencyEditDealerSecAPI(id, params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.$emit('submitEntrySuccess', true);
                    this.$emit('submitEntryObj', this.entryFormInfo);
                    // this.$message.success(res.msg)
                } else {
                    this.$emit('submitEntrySuccess', false);
                    this.$message.error(res.msg);
                }
            });
        },
        // 获取厂商医疗器械生产许可证的所有信息
        async platformAgencyVendorAPIFn(id) {
            await platformAgencyVendorAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let org = res.info;
                    if (org.new) {
                        // 营业期限 | 有效期限 第一次录入取图片信息里的日期
                        this.initDateInfo(org);

                        this.entryIssueDate = moment[''];
                        this.entryEstablishDate = moment[''];
                    } else {
                        this.entryBEDATE[0] = moment(org.begin_date);
                        this.entryBEDATE[1] = moment(org.end_date);
                        this.entryBeginDate = moment(org.begin_date);
                        this.entryEndDate = moment(org.end_date);
                        this.entryIssueDate = moment(org.issue_date);
                        this.entryEstablishDate = moment(org.establish_date);
                    }
                    if (org.production_range && org.production_range.length > 0) {
                        let scopeStr = '[' + org.production_range + ']';
                        this.checkedKeys = JSON.parse(scopeStr);
                    }
                    this.entryFormInfo = { ...org };
                    this.entryFormInfoOld = { ...org };
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 添加厂商医疗器械生产许可证的所有信息
        async platformAgencyAddVendorAPIFn(id, params) {
            await platformAgencyAddVendorAPI(id, params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.$emit('submitEntrySuccess', true);
                    this.$emit('submitEntryObj', this.entryFormInfo);
                    // this.$message.success(res.msg)
                } else {
                    this.$emit('submitEntrySuccess', false);
                    this.$message.error(res.msg);
                }
            });
        },
        // 编辑厂商医疗器械生产许可证的所有信息
        async platformAgencyEditVendorAPIFn(id, params) {
            await platformAgencyEditVendorAPI(id, params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.$emit('submitEntrySuccess', true);
                    this.$emit('submitEntryObj', this.entryFormInfo);
                    // this.$message.success(res.msg)
                } else {
                    this.$emit('submitEntrySuccess', false);
                    this.$message.error(res.msg);
                }
            });
        },
        // 获取经营范围树
        async platformAgencyScopeTreeAPIFn() {
            await platformAgencyScopeTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    let resStr = JSON.stringify(res.tree).replace(/"code68_sn"/g, '"key"');
                    let newResStr = resStr.replace(/"name"/g, '"title"');
                    this.treeData = JSON.parse(newResStr);
                    var leafList = function leafList(ListData) {
                        for (let i = 0; i < ListData.length; i++) {
                            ListData[i].title = '(' + ListData[i].key + ') ' + ListData[i].title;
                            if (ListData[i].is_leaf === 1) {
                                leafList(ListData[i].children);
                            }
                        }
                    };
                    leafList(this.treeData);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        onExpand(expandedKeys) {
            // if not set autoExpandParent to false, if children expanded, parent can not collapse.
            // or, you can remove all expanded children keys.
            this.expandedKeys = expandedKeys;
            this.autoExpandParent = true;
        },
        onCheck(checkedKeys) {
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            let scopeStr = JSON.stringify(checkedKeys).replace(/\[|]/g, '');
            if (sysType === 3 && this.downTabsActiveKey === '2') {
                this.entryFormInfo.production_range = scopeStr;
            } else {
                this.entryFormInfo.business_scope = scopeStr;
            }
            this.checkedKeys = checkedKeys;
        },
        onSelect(selectedKeys) {
            this.selectedKeys = selectedKeys;
        },
        // 有效期限
        entryEndDateFn(value, mode) {
            this.entryFormInfo.end_date = mode;
        },
        // 营业期限
        entryHandlePanelChange(value, mode) {
            this.entryBeginDate = value[0];
            this.entryEndDate = value[1];
            this.entryFormInfo.begin_date = mode[0];
            this.entryFormInfo.end_date = mode[1];
        },
        // 成立日期
        entryEstablishDateFn(value, mode) {
            this.entryFormInfo.establish_date = mode;
        },
        // 发证日期、备案日期
        entryIssueDateFn(value, mode) {
            this.entryFormInfo.issue_date = mode;
        },
        // 第一次录入初始化图片数据里面的日期信息 (营业期限 | 有效期限 )
        initDateInfo(data) {
            let beginDate = moment[''];
            let endDate = moment[''];
            if (data.images && data.images.length) {
                if (data.images[0].begin_date) {
                    beginDate = moment(data.images[0].begin_date);
                    this.entryBEDATE[0] = beginDate;
                    data.begin_date = beginDate;
                }
                if (data.images[0].begin_date) {
                    endDate = moment(data.images[0].end_date);
                    this.entryBEDATE[1] = endDate;
                    data.end_date = endDate;
                }
            }
            this.entryBeginDate = beginDate;
            this.entryEndDate = endDate;
        }
    },
    watch: {
        entryConfirm(newVal) {
            if (newVal) {
                if (!this.checkEntryInfoFormMsgFn()) return;
                this.entryFormInfo.begin_date = moment(this.entryBeginDate).format('YYYY-MM-DD');
                this.entryFormInfo.end_date = moment(this.entryEndDate).format('YYYY-MM-DD');
                if (this.entryIssueDate) {
                    this.entryFormInfo.issue_date = moment(this.entryIssueDate).format('YYYY-MM-DD');
                }
                if (this.entryEstablishDate) {
                    this.entryFormInfo.establish_date = moment(this.entryEstablishDate).format('YYYY-MM-DD');
                }
                let sysType = this.ReturnSysType(this.tabsActiveKey);
                let checkRes = this.checkEditInfoFormObjFn(this.entryFormInfoOld, this.entryFormInfo);
                if (checkRes.editInfoFlag) {
                    this.$emit('submitEntryClose', true);
                    // this.$emit('submitEntrySuccess', false)
                    // this.$emit('submitEntrySuccess', true)
                    // this.$message.warn('信息无任何变化')
                } else {
                    if (this.entryFormInfo.new) {
                        switch (this.downTabsActiveKey) {
                            case '2':
                                if (sysType === 2) {
                                    this.platformAgencyAddDealerAPIFn(this.currentTrObj.id, checkRes.newObj);
                                } else if (sysType === 3) {
                                    this.platformAgencyAddVendorAPIFn(this.currentTrObj.id, checkRes.newObj);
                                }
                                break;
                            case '3':
                                if (sysType === 2) {
                                    this.platformAgencyAddDealerSecAPIFn(this.currentTrObj.id, checkRes.newObj);
                                }
                                break;
                        }
                    } else {
                        switch (this.downTabsActiveKey) {
                            case '2':
                                if (sysType === 2) {
                                    this.platformAgencyEditDealerAPIFn(this.currentTrObj.id, checkRes.newObj);
                                } else if (sysType === 3) {
                                    this.platformAgencyEditVendorAPIFn(this.currentTrObj.id, checkRes.newObj);
                                }
                                break;
                            case '3':
                                if (sysType === 2) {
                                    this.platformAgencyEditDealerSecAPIFn(this.currentTrObj.id, checkRes.newObj);
                                }
                                break;
                        }
                    }
                }
            }
        }
    },
    mounted() {
        let sysType = this.ReturnSysType(this.tabsActiveKey);
        this.platformAgencyScopeTreeAPIFn();
        if (sysType === 2 && this.downTabsActiveKey === '2') {
            // 经销商医疗经营许可证
            this.platformAgencyDealerAPIFn(this.currentTrObj.id);
        } else if (sysType === 2 && this.downTabsActiveKey === '3') {
            // 经销商第二类医疗器械经营备案凭证
            this.platformAgencyDealerSecAPIFn(this.currentTrObj.id);
        } else if (sysType === 3 && this.downTabsActiveKey === '2') {
            // 厂商医疗器械生成许可证
            this.platformAgencyVendorAPIFn(this.currentTrObj.id);
        }
    }
};
</script>
<style lang="less" scoped>
.agency-base-scope {
    width: 58%;
    .agency-scope {
        height: 424px;
        overflow-y: scroll;
    }
}
</style>
