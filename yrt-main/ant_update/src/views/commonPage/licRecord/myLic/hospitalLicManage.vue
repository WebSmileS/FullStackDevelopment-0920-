<template>
    <div class="scoped mylic-manage-container">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button
                                :disabled="downTabsActiveKey !== '1' && downTabsActiveKey !== '2'"
                                @click="openEntry()"
                                type="primary"
                                size="small"
                            >
                                编辑
                            </a-button>
                            <!-- <a-button
                                @click="openEntry()"
                                type="primary"
                                size="small"
                                v-if="updateLicInfo"
                                :disabled="downTabsActiveKey !== '1' && downTabsActiveKey !== '2'"
                            >
                                录入
                            </a-button>
                            <a-button
                                @click="openEntry()"
                                type="primary"
                                size="small"
                                v-else
                                :disabled="downTabsActiveKey !== '1' && downTabsActiveKey !== '2'"
                            >
                                查看详细
                            </a-button> -->
                            <template v-if="uploadType">
                                <a-button @click="openUploadGic" type="primary" size="small"> 上传图片 </a-button>
                            </template>
                            <template v-else>
                                <a-upload
                                    :action="QiniuUrl"
                                    :show-upload-list="false"
                                    :multiple="true"
                                    :beforeUpload="checkFile"
                                    :supportServerRender="true"
                                    :data="upLoadparams"
                                    :disabled="isUploadImage"
                                    @change="upGoodsImg"
                                >
                                    <div>
                                        <a-button type="primary" size="small"> 上传图片 </a-button>
                                    </div>
                                </a-upload>
                            </template>
                        </li>
                        <li>
                            <a-button @click="applyApproval" type="primary" size="small" :disabled="updateLicInfo">
                                申请认证
                            </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->
        <!-- 上半部tab切换区域开始 -->
        <div class="tab-table-wrap tab-table-overflowy">
            <!-- 表格头部 -->
            <bottom-tabs-wrapper>
                <a-tabs @change="changeDownTabsFn" :activeKey="downTabsActiveKey" class="tab-table-wrap" type="card">
                    <a-tab-pane v-for="tabItem in downTabSet" :tab="tabItem.tab" :key="tabItem.key">
                        <div class="bottom-tabs-container bottom-pictures-wrapper">
                            <bottom-pictures
                                :list="imgList.filter((_) => _.type === Number(tabItem.key))"
                                deleteAble
                                @previewImg="previewImg"
                                @delete="deleteImg"
                            />
                        </div>
                    </a-tab-pane>
                </a-tabs>
            </bottom-tabs-wrapper>
        </div>
        <!-- 上半部tab切换区域结束 -->
        <!-- 模态框区域 -->
        <a-modal
            :visible="preImgSt"
            :width="previewWid"
            :centered="true"
            :maskClosable="false"
            okText="打印"
            cancelText="关闭"
            @ok="printFn"
            @cancel="closePreview"
            class="modal-form-input-scoped global-drag agency-imgpreview"
        >
            <div v-globalDrag="{ el: 'agency-imgpreview' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ imgPreviewTitle }}
                </h6>
            </div>
            <div ref="printData" class="preview-box">
                <img :src="imgSrc" :style="{ 'max-width': clientWidth - 294 + 'px' }" />
            </div>
        </a-modal>
        <!-- 录入操作 -->
        <a-modal
            v-model="entrySt"
            :maskClosable="false"
            :width="1000"
            :okText="'保存'"
            :centered="true"
            class="agency-entry modal-form-input-scoped global-drag"
            @ok="entryConfirmFn"
            @cancel="entryCancelFn"
        >
            <div v-globalDrag="{ el: 'agency-entry' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ entryTitle }}
                </h6>
            </div>
            <template>
                <!-- v-if="updateLicInfo" -->
                <entry-lic
                    :tabsActiveKey="tabsActiveKey"
                    :downTabsActiveKey="downTabsActiveKey"
                    :oid="oid"
                    :entryConfirm="entryConfirm"
                    v-if="entryModal"
                    @refuseEntry="refuseEntryFn"
                    @submitEntryObj="submitEntryObj"
                    @submitEntrySuccess="submitEntrySuccessFn"
                >
                </entry-lic>
                <entry-lic-scope
                    :tabsActiveKey="tabsActiveKey"
                    :downTabsActiveKey="downTabsActiveKey"
                    :oid="oid"
                    :entryConfirm="entryConfirm"
                    v-if="entryScopeModal"
                    @refuseEntry="refuseEntryFn"
                    @submitEntryObj="submitEntryObj"
                    @submitEntrySuccess="submitEntrySuccessFn"
                >
                </entry-lic-scope>
            </template>
            <!-- <template v-else>
                <view-entry-lic
                    :tabsActiveKey='tabsActiveKey'
                    :downTabsActiveKey="downTabsActiveKey"
                    :oid="oid"
                    :entryConfirm="entryConfirm"
                    v-if="entryModal"
                    @refuseEntry="refuseEntryFn"
                    @submitEntryObj="submitEntryObj"
                    @submitEntrySuccess='submitEntrySuccessFn'>
                </view-entry-lic>
                <view-entry-lic-scope
                    :tabsActiveKey='tabsActiveKey'
                    :downTabsActiveKey="downTabsActiveKey"
                    :oid="oid"
                    :entryConfirm="entryConfirm"
                    v-if="entryScopeModal"
                    @refuseEntry="refuseEntryFn"
                    @submitEntryObj="submitEntryObj"
                    @submitEntrySuccess='submitEntrySuccessFn'>
                </view-entry-lic-scope>
            </template> -->
            <template slot="footer">
                <!-- v-if="updateLicInfo" -->
                <a-button key="back" @click="entryCancelFn">关闭</a-button>
                <a-button type="primary" @click="entryConfirmFn">保存</a-button>
            </template>
            <!-- <template slot="footer" v-else>
                <a-button key="back" @click="entryCancelFn">关闭</a-button>
            </template> -->
        </a-modal>
        <!-- 录入操作结束 -->
        <!-- 上传图片 -->
        <a-modal
            :maskClosable="false"
            :centered="true"
            :visible="uploadGicSt"
            :width="600"
            okText="保存"
            @ok="uploadGicFn"
            @cancel="uploadGicSt = false"
            class="agency-upload modal-form-input-scoped global-drag"
        >
            <div v-globalDrag="{ el: 'agency-upload' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ uploadTitle }}
                </h6>
            </div>
            <upload-gic
                ref="upGicPage"
                @getData="getUpGicData"
                :chooseTrObj="currentTrObj"
                :tabsKey="tabsActiveKey"
                :downTabsKey="downTabsActiveKey"
                :uploadDateInfo="uploadDateInfo"
            ></upload-gic>
        </a-modal>
    </div>
</template>

<script>
import moment from 'moment';
import BottomTabsWrapper from '@/components/BaseBottom/BottomTabsWrapper';
import BottomPictures from '@/components/BaseBottom/BottomPictures';
import uploadGic from './modal/uploadGic';
import entryLic from './modal/entryLic';
import entryLicScope from './modal/entryLicScope';
// import viewEntryLic from './modal/viewEntryLic'
// import viewEntryLicScope from './modal/viewEntryLicScope'
import {
    provinceAreaAPI,
    cityAreaAPI,
    countyAreaAPI,
    listAreaAPI,
    platformAgenecyBaseInfoAPI,
    platformAgencyImgListAPI,
    platformAgencyAddPicAPI,
    licDeleteImg,
    getUploadTokenAPI,
    licApplyApproval,
    platformAgencyLicAPI,
    platformAgencyHospitalAPI,
    platformAgencyEditLicAPI,
    platformAgencyEditHospitalAPI
} from '@/service/pageAjax';

export default {
    name: 'hospitalLicManage',
    components: {
        BottomPictures,
        BottomTabsWrapper,
        uploadGic,
        entryLic,
        entryLicScope
        // viewEntryLic,
        // viewEntryLicScope
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            uploadDateInfo: {}, // 上传图片日期信息
            searchStatus: false,
            selectedKey: '',
            systemTypes: ['pt', 'hospital', 'dealer', 'vendor'],
            auditStatus: ['禁用', '正常'], // 机构状态(status)：0-禁用 1-正常
            dataSource: ['来自平台', '来自医院', '来自经销商', '来自厂商'], // 0-来自平台 1-来自医院 2-来自经销商 3-来自厂商
            rangePickerVal: moment[''], // 申请时间的默认赋值
            downTabSet: [
                {
                    tab: '营业执照',
                    key: '1',
                    btn: true
                },
                {
                    tab: '医疗机构执业许可证',
                    key: '2',
                    btn: true
                },
                {
                    tab: '开户许可证',
                    key: '3',
                    btn: false
                },
                {
                    tab: '其它',
                    key: '0',
                    btn: false
                }
            ],
            oid: '',
            updateLicInfo: true,
            uploadType: true,
            entryEstablishDate: moment[''],
            entryBeginDate: moment[''],
            entryEndDate: moment[''],
            entryIssueDate: moment[''],
            preImgSt: false,
            imgSrc: '',
            entryModal: false,
            entryScopeModal: false,
            uploadGicSt: false,
            previewImage: '',
            previewWid: 300,
            provinces: [],
            addNewVendor: false,
            entryTitle: '',
            uploadDisable: false,
            uploadTitle: '',
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            entrySt: false,
            clientHeight: 0,
            clientWidth: 0,
            imageFlag: [false, false, false, false, false, false, false, false, false],
            // 表格选中的当前行数据是否记住旧值
            recordCurrentTrObj: {
                hospitals: {},
                dealers: {},
                vendors: {}
            },
            imgPreviewTitle: '',
            entryConfirm: false,
            provinceDefaultsFlag: true, // 是否加载默认数据的标识
            provinceDefaults: [], // 北京：[110000, 110100, 110101]
            menuItemData: {
                class: true,
                arrow: false
            },
            searchTables: {
                begin_time: '',
                end_time: '',
                name: '',
                status: ''
            }, // 表格的搜索并向后端请求字段
            uploadGicData: {},
            upLoadparams: {},
            imgList: [],
            tabsActiveKey: 'hospital',
            downTabsActiveKey: '1',
            tanActKeyInNum: {
                'pt': 0,
                'hospital': 1,
                'dealer': 2,
                'vendor': 3
            }
        };
    },
    methods: {
        moment,
        // 获取全部省份列表
        async provinceAreaAPIFn() {
            await provinceAreaAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const datas = res.province.map((item) => {
                        item.value = item.administrative_division_sn;
                        item.label = item.name;
                        item.isLeaf = item.is_leaf === 0; // is_leaf 0：没有下一级叶子节点   1：有
                        return item;
                    });

                    this.provinces = [...datas];

                    if (this.provinceDefaultsFlag && this.provinceDefaults[0]) {
                        this.provinces.forEach((item, index) => {
                            if (parseFloat(item.value) === parseFloat(this.provinceDefaults[0])) {
                                this.provincesObj = {
                                    pIndex: index,
                                    cIndex: -1
                                };
                            }
                        });
                        this.cityAreaAPIFn(this.provinceDefaults[0]);
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据省份id查询所有的下级城市信息
        async cityAreaAPIFn(targetOption) {
            await cityAreaAPI(targetOption.value || targetOption).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const datas = res.city.map((item) => {
                        item.value = item.administrative_division_sn;
                        item.label = item.name;
                        item.isLeaf = item.is_leaf === 0; // is_leaf 0：没有下一级叶子节点   1：有
                        return item;
                    });

                    if (this.provinceDefaultsFlag && this.provinceDefaults[1]) {
                        this.provinces.splice(this.provincesObj.pIndex, 1, {
                            ...this.provinces[this.provincesObj.pIndex],
                            children: [...datas]
                        });
                        this.provinces[this.provincesObj.pIndex].children.forEach((item, index) => {
                            if (parseFloat(item.value) === parseFloat(this.provinceDefaults[1])) {
                                this.provincesObj = {
                                    ...this.provincesObj,
                                    cIndex: index
                                };
                            }
                        });
                        this.countyAreaAPIFn(this.provinceDefaults[1]);
                    } else {
                        if (typeof targetOption === 'object') {
                            targetOption.loading = false;
                            targetOption.children = [...datas];
                        }
                        this.provinces = [...this.provinces];
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据城市id查询所有的下级区域信息
        async countyAreaAPIFn(targetOption) {
            await countyAreaAPI(targetOption.value || targetOption).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const datas = res.county.map((item) => {
                        item.value = item.administrative_division_sn;
                        item.label = item.name;
                        // item.isLeaf = item.is_leaf === 0 // is_leaf 0：没有下一级叶子节点   1：有
                        return item;
                    });

                    if (this.provinceDefaultsFlag && this.provinceDefaults[2]) {
                        this.provinces[this.provincesObj.pIndex].children.splice(this.provincesObj.cIndex, 1, {
                            ...this.provinces[this.provincesObj.pIndex].children[this.provincesObj.cIndex],
                            children: [...datas]
                        });
                    } else {
                        if (typeof targetOption === 'object') {
                            targetOption.loading = false;
                            targetOption.children = [...datas];
                        }
                        this.provinces = [...this.provinces];
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据城市id查询上级区域信息 【查询所有的上级区域信息】
        async listAreaAPIFn(id) {
            await listAreaAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const areaObj = res.area,
                        countySn = areaObj.administrative_division_sn,
                        citySn = areaObj.parent ? areaObj.parent.administrative_division_sn : null,
                        provinceSn =
                            areaObj.parent && areaObj.parent.parent
                                ? areaObj.parent.parent.administrative_division_sn
                                : null;
                    /* const {
                        area: {
                            administrative_division_sn: countySn,
                            parent: {
                                administrative_division_sn: citySn,
                                parent: {
                                    administrative_division_sn: provinceSn
                                }
                            }
                        }
                    } = res */
                    // 检测省市区有没有值
                    if (provinceSn) {
                        this.provinceDefaults.splice(0, 1, provinceSn);
                    }
                    if (citySn) {
                        this.provinceDefaults.splice(1, 1, citySn);
                    }
                    if (countySn) {
                        this.provinceDefaults.splice(2, 1, countySn);
                    }
                    this.provinceAreaAPIFn();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 异步加载省市区数据
        provinceLoads(selectedOptions) {
            this.provinceDefaultsFlag = false;
            const targetOption = selectedOptions[selectedOptions.length - 1];
            targetOption.loading = true;
            if (!targetOption.parent_administrative_division_sn) {
                this.cityAreaAPIFn(targetOption);
            } else {
                this.countyAreaAPIFn(targetOption);
            }
        },
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
        // 根据注册机构id【查询】机构信息
        async AgenecyBaseInfoAPIFn(systemType, id) {
            let sysType = this.ReturnSysType(systemType);
            await platformAgenecyBaseInfoAPI(sysType, id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    this.listAreaAPIFn(info.administrative_division_sn);
                    if (info.certification_status === 1 || info.certification_status === 2) {
                        this.updateLicInfo = true;
                    } else {
                        this.updateLicInfo = false;
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID获取图片列表
        async platformAgencyImgListAPIFn(systemType, id) {
            let sysType = this.ReturnSysType(systemType);
            await platformAgencyImgListAPI(sysType, id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // let imgFlag = false
                    this.imgList = res.images;
                    // for (let i = 0; i < this.imgList.length; i++) {
                    //     if (this.imgList[i].type === 1) {
                    //         this.uploadDisable = true
                    //     }
                    // }
                    this.imageFlag = [false, false, false, false, false, false, false, false, false];
                    for (let k = 0; k < this.downTabSet[sysType - 1].length; k++) {
                        for (let i = 0; i < res.images.length; i++) {
                            if (k === res.images[i].type) {
                                this.imageFlag[k] = true;
                            }
                        }
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 表格的tab选项卡切换状态
        changeTabsFn(activeKey) {
            this.tabsActiveKey = activeKey;
            this.changeDownTabsFn('1');
            this.currentTrObj = {};
            this.imgList = [];
            this.imageFlag = [false, false, false, false, false, false, false, false, false];
        },
        changeDownTabsFn(activeKey) {
            this.downTabsActiveKey = activeKey;
            if (parseInt(this.downTabsActiveKey) === 1 || parseInt(this.downTabsActiveKey) === 2) {
                this.uploadType = true;
            } else {
                this.uploadType = false;
            }
        },
        // 申请时间的搜索改变事件
        changeRangePickerFn(value, dateString) {
            const [beginTime, endTime] = dateString;
            this.searchTables = {
                ...this.searchTables,
                begin_time: beginTime,
                end_time: endTime
            };
        },
        // 检测修改的表单信息校验
        checkEditInfoFormMsgFn() {
            const socialCreditCode = /^(\d|[a-zA-Z])+$/,
                mobilePhone = /^1\d{10}$/,
                email = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            // 名称
            if (!this.editInfoFormObj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            // else {
            //     this.editInfoFormObj.name = this.$Utils.stripSpecialStr(this.editInfoFormObj.name)
            // }
            // 名称 02
            if (!this.$Utils.trim(this.editInfoFormObj.name)) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                this.editInfoFormObj.name = this.$Utils.trim(this.editInfoFormObj.name);
                return false;
            }
            // 名称 03
            if (this.$Utils.checkInputValFn(this.editInfoFormObj.name)) {
                this.$warning({
                    title: '操作提示',
                    content: '名称中请不要输入特殊字符',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return false;
            }
            // else {
            //     this.editInfoFormObj.name = this.$Utils.stripSpecialStr(this.editInfoFormObj.name)
            // }
            // 统一社会信用代码 01
            if (!this.editInfoFormObj.social_credit_code) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入统一社会信用代码',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }

            // 统一社会信用代码 02
            if (!this.$Utils.trim(this.editInfoFormObj.social_credit_code)) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入统一社会信用代码',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                this.editInfoFormObj.social_credit_code = this.$Utils.trim(this.editInfoFormObj.social_credit_code);
                return false;
            }

            // 统一社会信用代码 03
            if (this.editInfoFormObj.social_credit_code.length !== 18) {
                this.$warning({
                    title: '操作提示',
                    content: '统一社会信用代码有效长度是18位',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }

            // 统一社会信用代码 04
            if (!socialCreditCode.test(this.editInfoFormObj.social_credit_code)) {
                this.$warning({
                    title: '操作提示',
                    content: '统一社会信用代码格式为字母和数字组合的18位',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }

            // 行政区域
            if (!this.editInfoFormObj.administrative_division_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择行政区划',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            // 电话
            if (this.editInfoFormObj.phone && this.editInfoFormObj.phone.length > 0) {
                if (!mobilePhone.test(this.editInfoFormObj.phone)) {
                    this.$warning({
                        title: '操作提示',
                        content: '电话格式输入有误',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }
            }
            // 邮箱
            if (this.editInfoFormObj.email && this.editInfoFormObj.email.length > 0) {
                if (!email.test(this.editInfoFormObj.email)) {
                    this.$warning({
                        title: '操作提示',
                        content: '邮箱格式输入有误',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }
            }
            // 企业类型
            if (!this.editInfoFormObj.enterprise_type) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择企业类型',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (sysType !== 1) {
                // 法定代表人 01
                if (!this.editInfoFormObj.legal_representative) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入法定代表人',
                        centered: true,
                        okText: '知道了',
                        maskClosable: false
                    });
                    return false;
                }
                // 法定代表人 02
                if (!this.$Utils.trim(this.editInfoFormObj.legal_representative)) {
                    this.$warning({
                        title: '操作提示',
                        content: '请输入法定代表人',
                        centered: true,
                        okText: '知道了',
                        maskClosable: false
                    });
                    this.editInfoFormObj.legal_representative = this.$Utils.trim(
                        this.editInfoFormObj.legal_representative
                    );
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
        // 省市区数据选择完成后的回调
        changeProvincesFn(value) {
            this.editInfoFormObj.administrative_division_sn = value[value.length - 1];
        },
        // 搜索框省市区数据选择完成后的回调
        changeProvincesSearchFn(value) {
            this.searchTables.administrative_division_sn = value[value.length - 1];
        },
        // 每次进来的初始化请求表格数据
        initTablesFn() {
            // this.systemTypes.forEach(item => {
            //     if (item === 'pt') return
            //     // this.platformAgencyAuthListAPIFn(item)
            // })
        },
        // 图片预览
        previewImg(src) {
            let that = this;
            let orderWidth = document.body.clientWidth; // 最大不能超过屏幕宽度
            if (src && src !== '') {
                const img = new Image();
                img.src = src;
                this.imgSrc = src;
                img.onload = function () {
                    that.previewWid = img.width + 50;
                    if (that.previewWid > orderWidth) {
                        that.previewWid = orderWidth - 246;
                    }
                    if (that.previewWid < 300) {
                        that.previewWid = 300;
                    }
                    that.preImgSt = true;
                    if (that.downTabsActiveKey === '0') {
                        that.imgPreviewTitle =
                            '我的资证 - ' + that.downTabSet[that.downTabSet.length - 1].tab + ' - 图片预览';
                    } else {
                        that.imgPreviewTitle =
                            '我的资证 - ' + that.downTabSet[that.downTabsActiveKey - 1].tab + ' - 图片预览';
                    }
                    that.$Utils.globalDragCenterFn('agency-imgpreview');
                };
            }
        },
        // 录入按钮
        openEntry() {
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            this.entrySt = true;
            // if (this.updateLicInfo === true) {
            //     this.entryTitle = '我的资证 - ' + this.downTabSet[this.downTabsActiveKey - 1].tab + ' - 录入'
            // } else {
            //     this.entryTitle = '我的资证 - ' + this.downTabSet[this.downTabsActiveKey - 1].tab + ' - 查看详细'
            // }
            this.entryTitle = '我的资证 - ' + this.downTabSet[this.downTabsActiveKey - 1].tab + ' - 编辑';
            if (this.downTabsActiveKey === '1') {
                // 营业执照
                this.entryModal = true;
            } else if (sysType === 1 && this.downTabsActiveKey === '2') {
                // 医院医疗执业许可证
                this.entryModal = true;
            } else if (sysType === 2 && this.downTabsActiveKey === '2') {
                // 经销商医疗经营西许可证
                this.entryScopeModal = true;
            } else if (sysType === 2 && this.downTabsActiveKey === '3') {
                // 经销商第二类医疗器械经营备案凭证
                this.entryScopeModal = true;
            } else if (sysType === 3 && this.downTabsActiveKey === '2') {
                // 厂商医疗器械生成许可证
                this.entryScopeModal = true;
            }
            this.$Utils.globalDragCenterFn('agency-entry');
        },
        // 录入取消按钮
        entryCancelFn() {
            this.entrySt = false;
            this.entryModal = false;
            this.entryScopeModal = false;
            this.entryConfirm = false;
        },
        refuseEntryFn() {
            this.entryConfirm = false;
        },
        // 录入确认
        entryConfirmFn() {
            this.entryConfirm = true;
        },
        // 录入结束返回
        submitEntrySuccessFn() {
            this.entryConfirm = false;
            this.entrySt = false;
            this.entryModal = false;
            this.entryScopeModal = false;
        },
        // 录入后更新列表数据
        submitEntryObj(obj) {
            this.currentTrObj.name = obj.name;
            this.currentTrObj.social_credit_code = obj.social_credit_code;
        },
        // 关闭图片预览
        closePreview() {
            this.preImgSt = false;
        },
        printFn() {
            let printContent = {
                printData: this.$refs.printData.innerHTML,
                printType: 1 // printType  1、图片；2、耗材申领；3、出入库单; 4、询价单
            };
            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        entryHandlePanelChange(value, mode) {
            this.entryBeginDate = value[0];
            this.entryEndDate = value[1];
            this.entryFormInfo.begin_date = mode[0];
            this.entryFormInfo.end_date = mode[1];
        },
        checkFile(file) {
            let fileName = file.name;
            let index1 = fileName.lastIndexOf('.');
            let index2 = fileName.length;
            let postName = fileName.substring(index1 + 1, index2); // 后缀名

            this.uploadGicData.filename = fileName;

            if (
                postName !== 'jpeg' &&
                postName !== 'JPEG' &&
                postName !== 'jpg' &&
                postName !== 'JPG' &&
                postName !== 'png' &&
                postName !== 'PNG' &&
                postName !== 'pdf' &&
                postName !== 'PDF'
            ) {
                this.$message.error('仅支持png / jpeg 格式图片或PDF文件');
                return false;
            }
        },
        // 打开上传图片
        openUploadGic() {
            this.uploadGicSt = true;
            if (this.downTabsActiveKey === '0') {
                this.uploadTitle = '我的资证 - ' + this.downTabSet[this.downTabSet.length - 1].tab + ' - 上传图片';
            } else {
                this.uploadTitle = '我的资证 - ' + this.downTabSet[this.downTabsActiveKey - 1].tab + ' - 上传图片';

                // 获取相关录入信息的日期数据
                let sysType = this.ReturnSysType(this.tabsActiveKey);
                switch (this.downTabsActiveKey) {
                    case '1': // 营业执照
                        platformAgencyLicAPI(sysType, this.oid).then((res) => {
                            this.setUploadDateInfo(res);
                        });
                        break;
                    case '2': // 医疗机构执业许可证
                        platformAgencyHospitalAPI(this.oid).then((res) => {
                            this.setUploadDateInfo(res);
                        });
                        break;
                }
            }
            this.uploadGicData = {
                url: '',
                begin_date: moment[''],
                end_date: moment['']
            };
            let cmp = this.$refs.upGicPage;
            if (cmp) {
                cmp.initUploadGic();
            }
            this.$Utils.globalDragCenterFn('agency-upload');
        },
        // 设置上传图片日期信息
        setUploadDateInfo(res) {
            if (parseFloat(res.code) === 0) {
                this.uploadDateInfo = {
                    beginDate: res.info.begin_date || '',
                    endDate: res.info.end_date || ''
                };
            }
        },
        // 上传图片 - 有时间单图
        async uploadGicFn() {
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            this.$refs.upGicPage.returnPageData();
            let params = {
                url: this.uploadGicData.url,
                begin_date: this.uploadGicData.begin_date,
                end_date: this.uploadGicData.end_date,
                file_name: this.$Utils.formatFileNameToLowerCase(this.uploadGicData.filename)
            };

            if (!params.url) {
                this.$message.warning('请上传图片再进行保存');
                return;
            }

            const begin = params.begin_date || '';
            const end = params.end_date || '';
            // 如果选择了日期  校验开始日期需小于截止日期
            if ((begin || end) && !moment(begin).isBefore(end)) {
                this.$message.warning('开始日期需小于截止日期');
                return;
            }

            await platformAgencyAddPicAPI(sysType, this.oid, this.downTabsActiveKey, params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // 更新相关录入信息的日期数据
                    switch (this.downTabsActiveKey) {
                        case '1': // 营业执照
                            platformAgencyEditLicAPI(sysType, this.oid, {
                                begin_date: params.begin_date,
                                end_date: params.end_date
                            });
                            break;
                        case '2': // 医疗机构执业许可证
                            platformAgencyEditHospitalAPI(this.oid, {
                                begin_date: params.begin_date,
                                end_date: params.end_date
                            });
                            break;
                    }

                    this.uploadGicData.begin_date = moment[''];
                    this.uploadGicData.end_date = moment[''];
                    this.uploadGicData.url = '';
                    this.imgList.push(res.info);
                    this.imageFlag[this.downTabsActiveKey] = true;
                    this.uploadGicSt = false;
                    this.platformAgencyImgListAPIFn(this.tabsActiveKey, this.oid);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 获取上传 token
        async getUploadTokenAPIFn() {
            await getUploadTokenAPI().then((res) => {
                const { code, data } = res;
                if (parseFloat(code) === 0) {
                    this.upToken = data;
                    this.upLoadparams = {
                        token: this.upToken
                    };
                } else {
                    this.upToken = '';
                    this.$message.error(res.msg);
                }
            });
        },
        // 上传图 - 无时间多图
        async upGoodsImg(info) {
            let sysType = this.ReturnSysType(this.tabsActiveKey);

            if (info.file.status === 'done') {
                const res = info.file.response;
                if (res.code === 0) {
                    let url = res.data.key;
                    let params = {
                        url: url,
                        file_name: this.$Utils.formatFileNameToLowerCase(info.file.name)
                    };
                    await platformAgencyAddPicAPI(sysType, this.oid, this.downTabsActiveKey, params).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            this.imgList.push(res.info);
                            this.imageFlag[this.downTabsActiveKey] = true;
                            this.uploadGicSt = false;
                            this.platformAgencyImgListAPIFn(this.tabsActiveKey, this.oid);
                        } else {
                            this.$message.error(res.msg);
                        }
                    });
                } else {
                    this.$message.error(res.msg);
                }
            }
        },
        getUpGicData(data) {
            this.uploadGicData = data;
        },
        // 删除图片
        async licDeleteImgFn(item) {
            let ImgItem = item;
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            await licDeleteImg(sysType, this.oid, ImgItem.type, ImgItem.org_related_image_inner_sn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    for (let i = 0; i < this.imgList.length; i++) {
                        if (
                            this.imgList[i].org_related_image_inner_sn === ImgItem.org_related_image_inner_sn &&
                            ImgItem.type === this.imgList[i].type
                        ) {
                            this.imgList.splice(i, 1);
                            i--;
                        }
                    }
                    // this.platformAgencyImgListAPIFn(this.tabsActiveKey, this.oid)
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        deleteImg(item) {
            let that = this;
            this.$confirm({
                title: '删除图片',
                content: '确认要执行该操作吗？',
                maskClosable: false,
                centered: true,
                onOk() {
                    that.licDeleteImgFn(item);
                }
            });
        },
        // 申请认证
        async licApplyApprovalFn() {
            await licApplyApproval(this.oid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.updateLicInfo = true;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        applyApproval() {
            let that = this;
            this.$confirm({
                title: '申请认证',
                content: '确认资证上传完毕并申请认证？',
                maskClosable: false,
                centered: true,
                onOk() {
                    that.licApplyApprovalFn();
                }
            });
        }
    },
    computed: {
        isUploadImage() {
            if (this.imgList.leng <= 0) return false;
            let downTabsActiveKey = parseInt(this.downTabsActiveKey),
                isVal = this.imgList.some(
                    (item) => item.type === downTabsActiveKey && (downTabsActiveKey === 1 || downTabsActiveKey === 2)
                );
            return isVal;
        }
    },
    created() {
        this.oid = this.$cookie.get('userbelong');
        this.clientWidth = document.body.clientWidth;
        this.clientHeight = document.body.clientHeight;
        this.AgenecyBaseInfoAPIFn(this.tabsActiveKey, this.oid);
        this.getUploadTokenAPIFn();
        // this.provinceAreaAPIFn()
        this.platformAgencyImgListAPIFn(this.tabsActiveKey, this.oid);
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

.mylic-manage-container {
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
    /deep/ .bottom-tabs-wrapper {
        .tab-table-wrap {
            height: 100%;
        }
    }
    .tab-table-wrap {
        margin-top: 8px;
        height: calc(100% - 70px);
        &.minimal {
            width: 55%;
            display: inline-block;
            vertical-align: top;
            margin-left: 1%;
        }
        .ant-tabs-card-content {
            height: 100%;
        }
        .ant-tabs-nav-scroll {
            display: flex;
            .ant-tabs-nav {
                padding-left: 20px;
            }
        }
        .ant-tabs-bar {
            margin-bottom: 0;
        }
        .ant-tabs-card-content {
            border: 1px solid #e8e8e8;
            border-top: none;
            background: #fff;
            padding: 8px;
            height: calc(100% - 40px);
            overflow-y: scroll;
        }
        .add-box {
            padding-left: 20px;
            padding-top: 10px;
        }
    }
}
.preview-box {
    text-align: center;
    text-align: center;
    display: block;
    margin-top: 48px;
    max-height: 78vh;
    overflow-y: auto;
    img {
        width: 100%;
        border: 1px solid #e7e7e7;
    }
}

.modal-form-input-scoped {
    .modal-form-input {
        .label-1 {
            width: 80px;
        }
    }
}
</style>
