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
        <div class="info-count modal-form-input">
            <ul>
                <li class="item-agency">
                    <span class="label" v-if="downTabsActiveKey === '1'"><em class="em red">*</em>名称</span>
                    <span class="label" v-if="downTabsActiveKey === '2'"><em class="em red">*</em>机构名称</span>
                    <a-input v-model="entryFormInfo.name" class="input" disabled="true" />
                </li>
                <li class="item-agency" v-if="downTabsActiveKey === '2'">
                    <span class="label"><em class="em red">*</em>登记号</span>
                    <a-input v-model="entryFormInfo.registered_number" class="input input-short" disabled="true" />
                </li>
                <li class="item-agency" v-if="downTabsActiveKey === '1'">
                    <div class="item-children item-children-agency-approval">
                        <span class="label"><em class="em red">*</em>统一社会信用代码</span>
                        <a-input v-model="entryFormInfo.social_credit_code" class="input input-short" disabled="true" />
                    </div>
                    <div class="item-children">
                        <span class="label label-1" style="width: 74px"><em class="em red">*</em>类型</span>
                        <a-select
                            :defaultValue="entryFormInfo.enterprise_type || '0'"
                            v-model="entryFormInfo.enterprise_type"
                            :style="{ 'width': '160px' }"
                            disabled="true"
                        >
                            <a-select-option value="0">非公司企业法人</a-select-option>
                            <a-select-option value="1">有限责任公司</a-select-option>
                            <a-select-option value="2">股份有限责任公司</a-select-option>
                            <a-select-option value="3">个体工商户</a-select-option>
                            <a-select-option value="4">私营独资企业</a-select-option>
                            <a-select-option value="5">私营合伙企业</a-select-option>
                            <a-select-option value="6">事业单位</a-select-option>
                            <a-select-option value="7">外国厂商</a-select-option>
                        </a-select>
                    </div>
                </li>
                <li class="item-agency">
                    <div class="item-children item-children-agency-approval">
                        <span class="label">法定代表人</span>
                        <a-input
                            v-model="entryFormInfo.legal_representative"
                            class="input input-short"
                            disabled="true"
                        />
                    </div>
                    <div class="item-children" v-if="downTabsActiveKey === '1'">
                        <span class="label label-1" style="width: 74px">注册资本</span>
                        <a-input v-model="entryFormInfo.registered_capital" class="input input-short" disabled="true" />
                    </div>
                    <div class="item-children" v-if="downTabsActiveKey === '2' || downTabsActiveKey === 3">
                        <span class="label" style="width: 80px">主要负责人</span>
                        <a-input v-model="entryFormInfo.principal" class="input" disabled="true" />
                    </div>
                </li>
                <li class="item-agency">
                    <span class="label" v-if="downTabsActiveKey === '1'">住所</span>
                    <span class="label" v-if="downTabsActiveKey === '2'">地址</span>
                    <a-input v-model="entryFormInfo.address" class="input" disabled="true" />
                </li>
                <li class="item-agency" v-if="downTabsActiveKey === '1'">
                    <span class="label">成立日期</span>
                    <a-date-picker
                        @change="entryEstablishDateFn"
                        :defaultValue="entryEstablishDate"
                        v-model="entryEstablishDate"
                        format="YYYY-MM-DD"
                        size="small"
                        class="input"
                        placeholder=""
                        disabled="true"
                    />
                </li>
                <li class="item-agency">
                    <span class="label" v-if="downTabsActiveKey === '1'"><em class="em red">*</em>营业期限</span>
                    <span class="label" v-if="downTabsActiveKey === '2'"><em class="em red">*</em>有效期限</span>
                    <a-range-picker
                        @change="entryHandlePanelChange"
                        v-model="entryBEDATE"
                        :defaultValue="[entryBeginDate, entryEndDate]"
                        format="YYYY-MM-DD"
                        class="input"
                        placeholder=""
                        disabled="true"
                    />
                </li>
                <li class="item-agency" style="align-items: flex-start" v-if="downTabsActiveKey === '1'">
                    <span class="label" style="position: relative; top: 3px"><em class="em"></em>经营范围</span>
                    <a-textarea
                        v-model="entryFormInfo.business_scope"
                        :autosize="{ minRows: 4, maxRows: 4 }"
                        style="margin: 0 !important; line-height: 1"
                        class="input"
                        disabled="true"
                    />
                </li>
                <li class="item-agency" style="align-items: flex-start" v-if="downTabsActiveKey === '2'">
                    <span class="label" style="position: relative; top: 3px"><em class="em"></em>诊疗科目</span>
                    <a-textarea
                        v-model="entryFormInfo.diagnosis_subject"
                        :autosize="{ minRows: 4, maxRows: 6 }"
                        style="margin: 0 !important; line-height: 1"
                        class="input"
                        disabled="true"
                    />
                </li>
                <li class="item-agency" v-if="downTabsActiveKey === '1' || downTabsActiveKey === '2'">
                    <div class="item-children item-children-agency-approval" v-if="downTabsActiveKey === '1'">
                        <span class="label">登记机关</span>
                        <a-input v-model="entryFormInfo.issue_organization" class="input" disabled="true" />
                    </div>
                    <div class="item-children item-children-agency-approval" v-if="downTabsActiveKey === '2'">
                        <span class="label label-1"><em class="em red"></em>发证机关</span>
                        <a-input v-model="entryFormInfo.issue_organization" class="input input-short" disabled="true" />
                    </div>
                    <div class="item-children">
                        <span class="label label-1" style="width: 80px"><em class="em red">*</em>发证日期</span>
                        <a-date-picker
                            @change="entryIssueDateFn"
                            :defaultValue="entryIssueDate"
                            v-model="entryIssueDate"
                            format="YYYY-MM-DD"
                            size="small"
                            class="input"
                            placeholder=""
                            disabled="true"
                        />
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>
<script>
import {
    platformAgencyHospitalAPI,
    platformAgencyAddHospitalAPI,
    platformAgencyEditHospitalAPI,
    platformAgencyLicAPI,
    platformAgencyAddLicAPI,
    platformAgencyEditLicAPI
} from '@/service/pageAjax';
import moment from 'moment';
import Gallery from '@/components/Picture/Gallery';

export default {
    moment,
    name: 'entryLic',
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
            entryFormInfo: {
                orgLicense: {},
                orgInfo: {},
                name: '',
                social_credit_code: '',
                enterprise_type: '',
                legal_representative: '',
                registered_capital: '',
                address: '',
                establish_date: moment[''],
                begin_date: '',
                end_date: '',
                issue_date: moment[''],
                issue_organization: '',
                business_scope: '',
                images: [],
                new: true
            },
            entryBEDATE: [],
            entryFormInfoOld: {}
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
        // 检测录入的表单信息校验
        checkEntryInfoFormMsgFn() {
            const socialCreditCode = /^(\d|[a-zA-Z])+$/;
            // 名称
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
                    content: '名称不能为空',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                this.entryFormInfo.name = this.$Utils.trim(this.entryFormInfo.name);
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
            if (this.downTabsActiveKey === '1') {
                // 类型
                if (!this.entryFormInfo.enterprise_type) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择类型',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // 营业期限
                if (!this.entryFormInfo.end_date || !this.entryFormInfo.begin_date) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择完整营业期限',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // 统一社会信用代码 01
                if (!this.entryFormInfo.social_credit_code) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入统一社会信用代码',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // 统一社会信用代码 04
                if (!this.$Utils.trim(this.entryFormInfo.social_credit_code)) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入统一社会信用代码',
                        centered: true,
                        okText: '知道了',
                        maskClosable: false
                    });
                    this.entryFormInfo.social_credit_code = this.$Utils.trim(this.entryFormInfo.social_credit_code);
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // 统一社会信用代码 02
                if (this.entryFormInfo.social_credit_code.length !== 18) {
                    this.$warning({
                        title: '操作提示',
                        content: '统一社会信用代码有效长度是18位',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }

                // 统一社会信用代码 03
                if (!socialCreditCode.test(this.entryFormInfo.social_credit_code)) {
                    this.$warning({
                        title: '操作提示',
                        content: '统一社会信用代码格式为字母和数字组合的18位',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
            } else {
                // 登记号 01
                if (!this.entryFormInfo.registered_number) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入登记号',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // 登记号 02
                if (!this.$Utils.trim(this.entryFormInfo.registered_number)) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入登记号',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.entryFormInfo.registered_number = this.$Utils.trim(this.entryFormInfo.registered_number);
                    this.$emit('refuseEntry', false);
                    return false;
                }
                // 有效期限
                if (!this.entryFormInfo.begin_date && !this.entryFormInfo.end_date) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择完整有效期限',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    this.$emit('refuseEntry', false);
                    return false;
                }
            }
            return true;
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
        // 根据机构ID获取营业执照相关信息
        async platformAgencyLicAPIFn(systemType, id) {
            let sysType = this.ReturnSysType(systemType);
            await platformAgencyLicAPI(sysType, id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let org = res.info;
                    org.enterprise_type = org.enterprise_type.toString();
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
                        if (org.issue_date) {
                            this.entryIssueDate = moment(org.issue_date);
                        } else {
                            this.entryIssueDate = moment[''];
                        }
                        if (org.establish_date) {
                            this.entryEstablishDate = moment(org.establish_date);
                        } else {
                            this.entryEstablishDate = moment[''];
                        }
                    }
                    this.entrySt = true;
                    this.entryFormInfo = { ...org };
                    this.entryFormInfoOld = { ...org };
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID添加营业执照相关信息
        async platformAgencyAddLicAPIFn(systemType, id, params) {
            let sysType = this.ReturnSysType(systemType);
            await platformAgencyAddLicAPI(sysType, id, params).then((res) => {
                this.$emit('submitEntrySuccess', true);
                if (parseFloat(res.code) === 0) {
                    this.$emit('submitEntryObj', this.entryFormInfo);
                    // this.$message.success(res.msg)
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID编辑营业执照相关信息
        async platformAgencyEditLicAPIFn(systemType, id, params) {
            let sysType = this.ReturnSysType(systemType);
            await platformAgencyEditLicAPI(sysType, id, params).then((res) => {
                this.$emit('submitEntrySuccess', true);
                if (parseFloat(res.code) === 0) {
                    this.$emit('submitEntryObj', this.entryFormInfo);
                    // this.$message.success(res.msg)
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 获取医疗机构执业许可证的所有信息
        async platformAgencyHospitalAPIFn(id) {
            await platformAgencyHospitalAPI(id).then((res) => {
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
                        if (org.issue_date) {
                            this.entryIssueDate = moment(org.issue_date);
                        } else {
                            this.entryIssueDate = moment[''];
                        }
                        if (org.establish_date) {
                            this.entryEstablishDate = moment(org.establish_date);
                        } else {
                            this.entryEstablishDate = moment[''];
                        }
                    }
                    this.entryFormInfo = { ...res.info };
                    this.entryFormInfoOld = { ...res.info };
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 添加医疗机构执业许可证的所有信息
        async platformAgencyAddHospitalAPIFn(id, params) {
            await platformAgencyAddHospitalAPI(id, params).then((res) => {
                this.$emit('submitEntrySuccess', true);
                if (parseFloat(res.code) === 0) {
                    this.$emit('submitEntryObj', this.entryFormInfo);
                    // this.$message.success(res.msg)
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 编辑医疗机构执业许可证的所有信息
        async platformAgencyEditHospitalAPIFn(id, params) {
            await platformAgencyEditHospitalAPI(id, params).then((res) => {
                this.$emit('submitEntrySuccess', true);
                if (parseFloat(res.code) === 0) {
                    // this.$message.success(res.msg)
                    this.$emit('submitEntryObj', this.entryFormInfo);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        entryHandlePanelChange(value, mode) {
            this.entryBeginDate = value[0];
            this.entryEndDate = value[1];
            this.entryFormInfo.begin_date = mode[0];
            this.entryFormInfo.end_date = mode[1];
        },
        entryEstablishDateFn(value, mode) {
            this.entryFormInfo.establish_date = mode;
        },
        entryIssueDateFn(value, mode) {
            this.entryFormInfo.issue_date = mode;
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
                let checkRes = this.checkEditInfoFormObjFn(this.entryFormInfoOld, this.entryFormInfo);
                if (checkRes.editInfoFlag) {
                    this.$emit('submitEntrySuccess', true);
                } else {
                    if (this.entryFormInfo.new) {
                        switch (this.downTabsActiveKey) {
                            case '1':
                                this.platformAgencyAddLicAPIFn(
                                    this.tabsActiveKey,
                                    this.currentTrObj.id,
                                    checkRes.newObj
                                );
                                break;
                            case '2':
                                this.platformAgencyAddHospitalAPIFn(this.currentTrObj.id, checkRes.newObj);
                                break;
                        }
                    } else {
                        switch (this.downTabsActiveKey) {
                            case '1':
                                this.platformAgencyEditLicAPIFn(
                                    this.tabsActiveKey,
                                    this.currentTrObj.id,
                                    checkRes.newObj
                                );
                                break;
                            case '2':
                                this.platformAgencyEditHospitalAPIFn(this.currentTrObj.id, checkRes.newObj);
                                break;
                        }
                    }
                }
            }
        }
    },
    mounted() {
        let sysType = this.ReturnSysType(this.tabsActiveKey);
        if (this.downTabsActiveKey === '1') {
            // 营业执照
            this.platformAgencyLicAPIFn(this.tabsActiveKey, this.currentTrObj.id);
        } else if (sysType === 1 && this.downTabsActiveKey === '2') {
            // 医院医疗执业许可证
            this.platformAgencyHospitalAPIFn(this.currentTrObj.id);
        }
    }
};
</script>
