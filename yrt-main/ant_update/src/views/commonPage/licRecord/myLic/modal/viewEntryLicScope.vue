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
                                    <a-input v-model="entryFormInfo.name" class="input" disabled="true" />
                                </li>
                                <li class="item-agency" v-if="downTabsActiveKey === '2'">
                                    <template v-if="tabsActiveKey === 'vendor'">
                                        <span class="label-short"><em class="em red">*</em>许可证编号</span>
                                        <a-input
                                            v-model="entryFormInfo.license_sn"
                                            class="input input-short"
                                            disabled="true"
                                        />
                                    </template>
                                    <template v-else>
                                        <div class="item-children item-children-agency-approval">
                                            <span class="label-short"><em class="em red">*</em>许可证编号</span>
                                            <a-input
                                                v-model="entryFormInfo.license_sn"
                                                class="input input-short"
                                                disabled="true"
                                            />
                                        </div>
                                        <div class="item-children">
                                            <span class="label-short"><em class="em red">*</em>经营方式</span>
                                            <a-select
                                                :defaultValue="entryFormInfo.operation_mode || '0'"
                                                v-model="entryFormInfo.operation_mode"
                                                :style="{ 'width': '164px' }"
                                                disabled="true"
                                            >
                                                <a-select-option value="8">其他</a-select-option>
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
                                        <a-input
                                            v-model="entryFormInfo.record_sn"
                                            class="input input-short"
                                            disabled="true"
                                        />
                                    </div>
                                    <div class="item-children">
                                        <span class="label-short"><em class="em red">*</em>经营方式</span>
                                        <a-select
                                            :defaultValue="entryFormInfo.operation_mode || '0'"
                                            :style="{ 'width': '164px' }"
                                            v-model="entryFormInfo.operation_mode"
                                            disabled="true"
                                        >
                                            <a-select-option value="8">其他</a-select-option>
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
                                        <span class="label-short">法定代表人</span>
                                        <a-input
                                            v-model="entryFormInfo.legal_representative"
                                            class="input input-short"
                                            disabled="true"
                                        />
                                    </div>
                                    <div
                                        class="item-children"
                                        v-if="downTabsActiveKey === '2' || downTabsActiveKey === '3'"
                                    >
                                        <span class="label-short">企业负责人</span>
                                        <a-input
                                            v-model="entryFormInfo.principal"
                                            class="input input-short"
                                            disabled="true"
                                        />
                                    </div>
                                </li>
                                <li class="item-agency">
                                    <span class="label-short">住所</span>
                                    <a-input v-model="entryFormInfo.address" class="input" disabled="true" />
                                </li>
                                <template v-if="tabsActiveKey === 'dealer'">
                                    <li
                                        class="item-agency"
                                        v-if="downTabsActiveKey === '2' || downTabsActiveKey === '3'"
                                    >
                                        <span class="label-short">经营场所</span>
                                        <a-input v-model="entryFormInfo.premises" class="input" disabled="true" />
                                    </li>
                                    <li
                                        class="item-agency"
                                        v-if="downTabsActiveKey === '2' || downTabsActiveKey === '3'"
                                    >
                                        <span class="label-short">库房地址</span>
                                        <a-input
                                            v-model="entryFormInfo.warehouse_address"
                                            class="input"
                                            disabled="true"
                                        />
                                    </li>
                                </template>
                                <template v-if="tabsActiveKey === 'vendor'">
                                    <li
                                        class="item-agency"
                                        v-if="downTabsActiveKey === '2' || downTabsActiveKey === '3'"
                                    >
                                        <span class="label-short">生产地址</span>
                                        <a-input
                                            v-model="entryFormInfo.production_address"
                                            class="input"
                                            disabled="true"
                                        />
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
                                            disabled="true"
                                        />
                                    </div>
                                </li>
                                <li class="item-agency" v-if="downTabsActiveKey === '2'">
                                    <div
                                        class="item-children item-children-agency-approval"
                                        v-if="downTabsActiveKey === '1'"
                                    >
                                        <span class="label-short">登记机关</span>
                                        <a-input
                                            v-model="entryFormInfo.issue_organization"
                                            class="input"
                                            disabled="true"
                                        />
                                    </div>
                                    <div
                                        class="item-children item-children-agency-approval"
                                        v-if="downTabsActiveKey === '2'"
                                    >
                                        <span class="label-short"><em class="em red"></em>发证部门</span>
                                        <a-input
                                            v-model="entryFormInfo.issue_organization"
                                            class="input"
                                            disabled="true"
                                        />
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
                                            disabled="true"
                                        />
                                    </div>
                                </li>
                                <li class="item-agency item-children-agency-approval" v-if="downTabsActiveKey === '3'">
                                    <div class="item-children item-children-agency-approval">
                                        <span class="label-short"><em class="em red"></em>备案部门</span>
                                        <a-input
                                            v-model="entryFormInfo.issue_organization"
                                            class="input"
                                            disabled="true"
                                        />
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
                                            disabled="true"
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
                            disabled="true"
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
                            disabled="true"
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
    platformAgencyDealerSecAPI,
    platformAgencyVendorAPI
} from '@/service/pageAjax';
import moment from 'moment';
import Gallery from '@/components/Picture/Gallery';

export default {
    moment,
    name: 'viewEntryLicScope',
    components: {
        Gallery
    },
    props: ['tabsActiveKey', 'downTabsActiveKey', 'oid', 'entryConfirm'],
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
        // 获取经销商医疗器械经营许可证的所有信息
        async platformAgencyDealerAPIFn(id) {
            await platformAgencyDealerAPI(id).then((res) => {
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
        // 获取厂商医疗器械生产许可证的所有信息
        async platformAgencyVendorAPIFn(id) {
            await platformAgencyVendorAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let org = res.info;
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
            // console.log('onExpand', expandedKeys)
            // if not set autoExpandParent to false, if children expanded, parent can not collapse.
            // or, you can remove all expanded children keys.
            this.expandedKeys = expandedKeys;
            this.autoExpandParent = true;
        },
        onCheck(checkedKeys) {
            // console.log('onCheck', checkedKeys)
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            let scopeStr = JSON.stringify(checkedKeys).replace(/\[|]/g, '');
            if (sysType === 3 && this.downTabsActiveKey === '2') {
                this.entryFormInfo.production_range = scopeStr;
            } else {
                this.entryFormInfo.business_scope = scopeStr;
            }
            this.checkedKeys = checkedKeys;
        },
        onSelect(selectedKeys, info) {
            // console.log('onSelect', info)
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
        }
    },
    watch: {
        entryConfirm(newVal, oldVal) {}
    },
    mounted() {
        let sysType = this.ReturnSysType(this.tabsActiveKey);
        this.platformAgencyScopeTreeAPIFn();
        if (sysType === 2 && this.downTabsActiveKey === '2') {
            // 经销商医疗经营许可证
            this.platformAgencyDealerAPIFn(this.oid);
        } else if (sysType === 2 && this.downTabsActiveKey === '3') {
            // 经销商第二类医疗器械经营备案凭证
            this.platformAgencyDealerSecAPIFn(this.oid);
        } else if (sysType === 3 && this.downTabsActiveKey === '2') {
            // 厂商医疗器械生成许可证
            this.platformAgencyVendorAPIFn(this.oid);
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
