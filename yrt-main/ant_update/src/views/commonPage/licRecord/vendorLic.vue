<template>
    <div class="scoped agency-approval-container">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="modifyBtnFn" type="primary" size="small"> 详细信息 </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-model="searchTables.name"
                            placeholder="名称"
                            size="small"
                            enterButton
                            class="search"
                            @search="searchBtnFn"
                            v-if="!searchStatus"
                        />
                        <div class="hight-search-text" @click="AdvancedSearch">
                            <!--<span>高级搜索</span>-->
                            <a-tooltip placement="left" title="高级搜索">
                                <a-icon :type="searchStatus ? 'up-circle' : 'down-circle'" />
                                <!--circle-->
                            </a-tooltip>
                        </div>
                    </div>
                </a-col>
            </a-row>
            <a-row class="hight-search-wrap" v-if="searchStatus">
                <a-col :span="24" class="col-list">
                    <!-- 高级搜索 -->
                    <ul class="form">
                        <li>
                            <span class="label">名称</span>
                            <a-input v-model="searchTables.name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">统一社会信用代码</span>
                            <a-input v-model="searchTables.social_credit_code" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">行政区划</span>
                            <a-cascader
                                :options="provinces"
                                :loadData="provinceLoads"
                                v-model="provinceSearch"
                                @change="changeProvincesSearchFn"
                                changeOnSelect
                                placeholder=""
                                class="input"
                            />
                        </li>
                        <li>
                            <a-button @click="searchBtnFn" type="primary"> 搜索 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->
        <!-- 上半部tab切换区域开始 -->
        <div class="tab-table-wrap tab-table-overflowy">
            <!-- 表格头部 -->
            <a-tabs @change="changeTabsFn" type="card">
                <a-tab-pane v-for="tabItem in agencyTab" :tab="tabItem.key" :key="tabItem.systemTypes">
                    <div class="box">
                        <div class="boxtop">
                            <vxe-table
                                ref="xTable"
                                size="small"
                                :height="boxTopHeight"
                                border
                                resizable
                                auto-resize
                                highlight-current-row
                                @cell-click="cellClickFn"
                                @cell-dblclick="cellDblclickFn"
                                align="center"
                                :data="tabItem.Datas"
                                :loading="tabItem.Pagination.loading"
                                show-overflow
                                show-header-overflow
                                showHeader
                                :start-index="(tabItem.Pagination.current - 1) * tabItem.Pagination.pageSize"
                            >
                                <vxe-table-column
                                    v-for="aryItem in tableSet[tabItem.sTypeNum - 1]"
                                    :key="aryItem.key"
                                    :width="aryItem.width"
                                    :field="aryItem.dataIndex"
                                    :title="aryItem.title"
                                ></vxe-table-column>
                            </vxe-table>
                            <vxe-pager
                                :loading="tabItem.Pagination.loading"
                                :current-page="tabItem.Pagination.current"
                                :page-size="tabItem.Pagination.pageSize"
                                :page-sizes="tabItem.Pagination.pageSizes"
                                :total="tabItem.Pagination.total"
                                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                                @page-change="changePaginationFn"
                            >
                            </vxe-pager>
                        </div>
                        <div class="resize"></div>
                        <!--表格区域-->
                        <div class="boxdown">
                            <div
                                class="agency-down-tabs"
                                v-bind:style="{ height: boxDownHeight - 4 + 'px' }"
                                style="border-bottom: 1px solid #e8e8e8"
                            >
                                <!-- 表格头部 -->
                                <!-- 下半部tab切换区域开始 -->
                                <bottom-tabs-wrapper>
                                    <a-tabs
                                        @change="changeDownTabsFn"
                                        :activeKey="downTabsActiveKey"
                                        class="tab-table-wrap"
                                        type="card"
                                    >
                                        <a-tab-pane
                                            v-for="tabItem in downTabSet[tanActKeyInNum[tabsActiveKey] - 1]"
                                            :tab="tabItem.tab"
                                            :key="tabItem.key"
                                        >
                                            <div v-if="!currentTrFlag"></div>
                                            <template v-else>
                                                <div
                                                    class="bottom-tabs-container bottom-pictures-wrapper"
                                                    v-if="!tabItem.btn"
                                                >
                                                    <bottom-pictures
                                                        :list="imgList.filter((_) => _.type === Number(tabItem.key))"
                                                        :deleteAble="false"
                                                        @previewImg="previewImg"
                                                    />
                                                </div>
                                                <div class="bottom-tabs-container" v-else>
                                                    <div
                                                        class="bottom-tabs-content-left bottom-buttons-vertical-wrapper"
                                                    >
                                                        <ul class="btn-fixed-wrap" style="padding-left: 0">
                                                            <li @click="openEntry(tabItem.key)">
                                                                <a-button size="small" type="primary"
                                                                    >详细信息</a-button
                                                                >
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <div class="bottom-tabs-content-right bottom-pictures-wrapper">
                                                        <bottom-pictures
                                                            :list="
                                                                imgList.filter((_) => _.type === Number(tabItem.key))
                                                            "
                                                            :deleteAble="false"
                                                            @previewImg="previewImg"
                                                        />
                                                    </div>
                                                </div>
                                            </template>
                                        </a-tab-pane>
                                    </a-tabs>
                                </bottom-tabs-wrapper>
                                <!--表格区域-->
                            </div>
                        </div>
                        <!-- 下半部tab切换区域结束 -->
                    </div>
                </a-tab-pane>
                <a-tooltip slot="tabBarExtraContent" placement="left">
                    <template slot="title">
                        <span>刷新</span>
                    </template>
                    <a-button
                        type="primary"
                        shape="circle"
                        icon="reload"
                        size="small"
                        class="refresh-btn"
                        @click="initRfreshTablesFn"
                    />
                    <!--刷新按钮区域-->
                </a-tooltip>
            </a-tabs>
        </div>
        <!-- 上半部tab切换区域结束 -->
        <!-- 模态框区域 -->
        <a-modal
            :visible="preImgSt"
            :width="previewWid"
            :centered="true"
            :maskClosable="false"
            cancelText="关闭"
            okText="打印"
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
        <!-- 机构注册审核信息修改区域开始 -->
        <a-modal
            v-model="AgencyInfo.alert"
            class="modal-form-input-scoped global-drag agency-edit"
            :centered="true"
            :width="648"
            :maskClosable="false"
        >
            <div v-globalDrag="{ el: 'agency-edit' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ AgencyInfo.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="AgencyInfoCancelFn">关闭</a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>名称</span>
                        <a-input v-model="editInfoFormObj.name" class="input" disabled="true" />
                    </li>
                    <li class="item" v-if="tabsActiveKey === 'hospital'">
                        <span class="label"><em class="em red"></em>别名</span>
                        <a-input v-model="editInfoFormObj.alias" class="input" disabled="true" />
                    </li>
                    <li class="item">
                        <div class="item-children item-children-agency-approval">
                            <span class="label"><em class="em red">*</em>统一社会信用代码</span>
                            <a-input
                                v-model="editInfoFormObj.social_credit_code"
                                class="input input-short"
                                disabled="true"
                            />
                        </div>
                        <div class="item-children">
                            <span class="label" style="width: 80px"><em class="em red">*</em>企业类型</span>
                            <a-select
                                :defaultValue="editInfoFormObj.enterprise_type || 0"
                                v-model="editInfoFormObj.enterprise_type"
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
                    <li class="item">
                        <span class="label"><em class="em red">*</em>行政区划</span>
                        <a-cascader
                            :options="provinces"
                            :loadData="provinceLoads"
                            v-model="provinceDefaults"
                            @change="changeProvincesFn"
                            changeOnSelect
                            class="input"
                            placeholder=""
                            disabled="true"
                        /><!--:defaultValue="provinceDefaults"-->
                    </li>
                    <li class="item">
                        <div class="item-children item-children-agency-approval">
                            <span class="label">成立日期</span>
                            <a-date-picker
                                :defaultValue="editInfoFormObj.establish_date"
                                v-model="editInfoFormObj.establish_date"
                                format="YYYY-MM-DD"
                                size="small"
                                class="input"
                                placeholder=""
                                disabled="true"
                            />
                        </div>
                        <div class="item-children">
                            <span class="label label-1">注册资本</span>
                            <a-input v-model="editInfoFormObj.registered_capital" class="input" disabled="true" />
                        </div>
                    </li>
                    <li class="item">
                        <div class="item-children item-children-agency-approval">
                            <span class="label"
                                ><em class="em red" v-if="tabsActiveKey !== 'hospital'">*</em>法定代表人</span
                            >
                            <a-input v-model="editInfoFormObj.legal_representative" class="input" disabled="true" />
                        </div>
                        <div class="item-children">
                            <span class="label label-1">企业负责人</span>
                            <a-input v-model="editInfoFormObj.principal" class="input" disabled="true" />
                        </div>
                    </li>
                    <li class="item">
                        <span class="label">开户银行</span>
                        <a-input v-model="editInfoFormObj.bank_name" class="input" disabled="true" />
                    </li>
                    <li class="item">
                        <span class="label">银行账号</span>
                        <a-input v-model="editInfoFormObj.bank_account" class="input" disabled="true" />
                    </li>
                    <li class="item">
                        <div class="item-children item-children-agency-approval">
                            <span class="label">电话</span>
                            <a-input v-model="editInfoFormObj.phone" class="input" disabled="true" />
                        </div>
                        <div class="item-children">
                            <span class="label label-1" style="width: 40px">邮箱</span>
                            <a-input v-model="editInfoFormObj.email" class="input" disabled="true" />
                        </div>
                    </li>
                    <li class="item">
                        <span class="label"><em class="em"></em>地址</span>
                        <a-input v-model="editInfoFormObj.address" class="input" disabled="true" />
                    </li>
                    <li class="item">
                        <span class="label memo" style="margin-top: -88px"><em class="em"></em>备注</span>
                        <a-textarea
                            v-model="editInfoFormObj.description"
                            :autosize="{ minRows: 4, maxRows: 4 }"
                            class="input"
                            disabled="true"
                        />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 机构注册审核信息修改区域结束 -->
        <!-- 录入操作 -->
        <a-modal
            v-model="entrySt"
            :maskClosable="false"
            :width="1000"
            :centered="true"
            class="agency-entry modal-form-input-scoped global-drag"
            @cancel="entryCancelFn"
        >
            <div v-globalDrag="{ el: 'agency-entry' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ entryTitle }}
                </h6>
            </div>
            <entry-lic
                :tabsActiveKey="tabsActiveKey"
                :downTabsActiveKey="downTabsActiveKey"
                :currentTrObj="currentTrObj"
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
                :currentTrObj="currentTrObj"
                :entryConfirm="entryConfirm"
                v-if="entryScopeModal"
                @refuseEntry="refuseEntryFn"
                @submitEntryObj="submitEntryObj"
                @submitEntrySuccess="submitEntrySuccessFn"
            >
            </entry-lic-scope>
            <template slot="footer">
                <a-button key="back" @click="entryCancelFn">关闭</a-button>
            </template>
        </a-modal>
        <!-- 录入操作结束 -->
    </div>
</template>

<script>
import moment from 'moment';
import TabTypeCfg from '@/config/tabTypeCfg';
import BottomTabsWrapper from '@/components/BaseBottom/BottomTabsWrapper';
import BottomPictures from '@/components/BaseBottom/BottomPictures';
import entryLic from './modal/entryLic';
import entryLicScope from './modal/entryLicScope';
import {
    provinceAreaAPI,
    cityAreaAPI,
    countyAreaAPI,
    listAreaAPI,
    licManageVendor,
    platformAgenecyBaseInfoAPI,
    platformAgencyImgListAPI
} from '@/service/pageAjax';

export default {
    name: 'vendorLic',
    components: {
        BottomPictures,
        BottomTabsWrapper,
        entryLic,
        entryLicScope
    },
    data() {
        return {
            boxTopHeight: 300,
            boxDownHeight: 300,
            searchStatus: false,
            selectedKey: '',
            systemTypes: ['pt', 'hospital', 'dealer', 'vendor'],
            auditStatus: ['禁用', '正常'], // 机构状态(audit_status): 0-禁用 1-正常
            enterpriseType: [
                '非公司企业法人',
                '有限责任公司',
                '股份有限责任公司',
                '个体工商户',
                '私营独资企业',
                '私营合伙企业',
                '事业单位',
                '外国厂商'
            ],
            rangePickerVal: moment[''], // 申请时间的默认赋值
            agencyTab: TabTypeCfg.licVendorTabSet(),
            tableSet: TabTypeCfg.licVendorTableTAb(),
            downTabSet: TabTypeCfg.AgencyDownTab(),
            pageSizeOptions: ['10', '20', '30', '40'],
            entryEstablishDate: moment[''],
            entryBeginDate: moment[''],
            entryEndDate: moment[''],
            entryIssueDate: moment[''],
            preImgSt: false,
            preImgWid: 0,
            imgSrc: '',
            entryModal: false,
            entryScopeModal: false,
            uploadGicSt: false,
            previewImage: '',
            previewWid: 300,
            provinces: [],
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            entrySt: false,
            clientHeight: 0,
            clientWidth: 0,
            upLoadStatus: false,
            imageFlag: [false, false, false, false, false, false, false, false, false],
            // // 表格选中的当前行数据是否记住旧值
            // recordCurrentTrObj: {
            //     hospitals: {},
            //     dealers: {},
            //     vendors: {}
            // },
            // 修改弹框的配置
            AgencyInfo: {
                title: `厂商 - 详细信息`,
                alert: false,
                loading: false
            },
            oid: '',
            currentTrFlag: false,
            entryTitle: '',
            uploadTitle: '',
            imgPreviewTitle: '',
            editInfoFormObj: {
                // 修改信息的模态框值 - 后端返回回来的
                administrative_division_sn: '',
                name: '',
                alias: '',
                registrant: '',
                registrant_phone: '',
                registrant_email: '',
                address: '',
                description: '',
                audit_status: '',
                not_audit_organization_inner_sn: '',
                register_time: '',
                system_type: ''
            },
            entryConfirm: false,
            provinceDefaultsFlag: true, // 是否加载默认数据的标识
            provinceDefaults: [], // 北京：[110000, 110100, 110101]
            provinceSearch: [],
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
            // 修改弹框的配置
            imgList: [],
            tabsActiveKey: 'vendor',
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
                        item.isLeaf = false;
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
                        item.isLeaf = false;
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
        // 根据注册机构类型【获取机构列表】
        async licManageVendorFn(systemType = this.tabsActiveKey) {
            if (this.tabsActiveKey === 'vendor') {
                this.agencyTab[0].Pagination.loading = true;
            }
            let sysType = 0;
            // 判断参数有没有， 没有直接不要传给后端
            let params = {};
            if (this.searchTables.name) {
                params.name = this.searchTables.name;
            }
            if (this.searchStatus) {
                if (this.searchTables.social_credit_code) {
                    params.social_credit_code = this.searchTables.social_credit_code;
                }
                if (this.searchTables.administrative_division_sn) {
                    params.administrative_division_sn = this.searchTables.administrative_division_sn;
                }
            }
            switch (systemType) {
                case 'pt':
                    break;
                case 'vendor':
                    sysType = 3;
                    params.pageNum = this.agencyTab[0].Pagination.current;
                    params.pageSize = this.agencyTab[0].Pagination.pageSize;
                    break;
            }
            if (sysType === 0) {
                return;
            }
            await licManageVendor({
                params: {
                    oid: this.oid,
                    ...params
                }
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const xTable = this.$refs.xTable,
                        { rows, total } = res;
                    let rowsNew = [];
                    if (rows.length !== 0) {
                        rowsNew = rows.map((item) => {
                            item.key = this.tabsActiveKey + '-' + item.id; // id
                            item.audit_status_text = this.auditStatus[parseFloat(item.status)];
                            item.enterpriseType_text = this.enterpriseType[parseFloat(item.enterprise_type)];
                            item.activeClass = '';
                            return item;
                        });
                    }

                    switch (systemType) {
                        case 'pt':
                            break;
                        case 'vendor':
                            this.agencyTab[0].Datas = rowsNew;
                            this.currentTrObj = rowsNew.length ? rowsNew[0] : {};

                            this.$nextTick(() => {
                                if (xTable[0] && xTable[0].getData().length) {
                                    xTable[0].setCurrentRow(this.currentTrObj);
                                    this.cellClickFn({ row: this.currentTrObj });
                                }
                            });

                            this.agencyTab[0].Pagination = {
                                ...this.agencyTab[0].Pagination,
                                total,
                                loading: false
                            };
                            break;
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
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
                    let org = res.info;
                    org.enterprise_type = org.enterprise_type.toString();
                    this.listAreaAPIFn(org.administrative_division_sn);
                    this.editInfoFormObj = { ...org };
                    this.editInfoFormOldObj = { ...org }; // 为了比对值的临时存储
                    if (this.editInfoFormObj.establish_date) {
                        this.editInfoFormObj.establish_date = moment(this.editInfoFormObj.establish_date);
                    } else {
                        this.editInfoFormObj.establish_date = moment[''];
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
            this.downTabsActiveKey = '1';
            this.currentTrObj = {};
            this.currentTrFlag = false;
            this.imgList = [];
            this.imageFlag = [false, false, false, false, false, false, false, false, false];
        },
        changeDownTabsFn(activeKey) {
            this.downTabsActiveKey = activeKey;
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
        // 查看操作
        modifyBtnFn() {
            if (!this.checkCurrentTrDataFn()) return;
            this.AgenecyBaseInfoAPIFn(this.tabsActiveKey, this.currentTrObj.id);
            this.editInfoFormObj.administrative_division_sn = this.currentTrObj.administrative_division_sn;
            this.provinceDefaultsFlag = true;
            this.provinceDefaults = [];
            this.AgencyInfo.alert = true;
            this.$Utils.globalDragCenterFn('agency-edit');
        },
        // 搜索操作
        searchBtnFn() {
            if (!this.checkSearchInfoFormMsgFn()) return;
            this.initRfreshTablesFn();
        },
        checkSearchInfoFormMsgFn() {
            if (this.$Utils.checkInputValFn(this.searchTables.name)) {
                this.$warning({
                    title: '操作提示',
                    content: `名称${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (this.searchStatus && this.$Utils.checkInputValFn(this.searchTables.social_credit_code)) {
                this.$warning({
                    title: '操作提示',
                    content: `统一社会信用代码${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 修改模态框操作
        AgencyInfoOkFn() {
            if (!this.checkEditInfoFormMsgFn()) return; // 前端格式校验
            let checkRes = this.checkEditInfoFormObjFn(this.editInfoFormOldObj, this.editInfoFormObj);
            if (checkRes.editInfoFlag) {
                // 后端传值校验
                this.AgencyInfoCancelFn();
                return;
            }
            this.AgencyInfo.loading = true;
            this.platformAgencyEditAPIFn(this.tabsActiveKey, this.currentTrObj.id, checkRes.newObj);
        },
        // 修改取消
        AgencyInfoCancelFn() {
            this.AgencyInfo.alert = false;
            this.editInfoFormNewObj = {};
        },
        // 省市区数据选择完成后的回调
        changeProvincesFn(value) {
            this.editInfoFormObj.administrative_division_sn = value[value.length - 1];
        },
        // 搜索框省市区数据选择完成后的回调
        changeProvincesSearchFn(value) {
            this.searchTables.administrative_division_sn = value[value.length - 1];
        },
        /* 按钮区域的操作 */
        checkCurrentTrDataFn() {
            // 检测表格是否有选中的的数据
            if (Object.keys(this.currentTrObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        getCurrentStyle(current) {
            const style = {};
            if (current.date() === 1) {
                style.border = '1px solid #1890ff';
                style.borderRadius = '50%';
            }
            return style;
        },
        AdvancedSearch() {
            this.searchStatus = !this.searchStatus;
            this.provinceDefaults = [];
            let box = document.getElementsByClassName('box');
            if (this.searchStatus) {
                for (let i = 0; i < box.length; i++) {
                    box[i].style.height = box[i].clientHeight - 60;
                }
                this.boxTopHeight -= 60;
            } else {
                for (let i = 0; i < box.length; i++) {
                    box[i].style.height = box[i].clientHeight + 60;
                }
                this.boxTopHeight += 60;
            }
            try {
                for (let i = 0; i < box.length; i++) {
                    document.getElementsByClassName('boxtop')[i].style.height = this.boxTopHeight + 55 + 'px';
                }
            } catch (e) {
                console.log(e);
            }
        },
        cellClickFn({ row }) {
            this.platformAgencyImgListAPIFn(this.tabsActiveKey, row.id);
            this.setDatasClassFn(row.key);
            this.currentTrObj = row;
            this.currentTrFlag = true;
            this.changeDownTabsFn('1');
        },
        cellDblclickFn({ row }) {
            this.currentTrFlag = true;
            this.currentTrObj = row;
            this.changeDownTabsFn('1');
            this.modifyBtnFn();
        },
        // 表格的高亮样式显示
        setDatasClassFn(key) {
            switch (this.tabsActiveKey) {
                case 'vendor':
                    this.agencyTab[0].Datas.forEach((item) => {
                        if (item.key === key) {
                            item.activeClass = 'active-tr';
                        } else {
                            item.activeClass = '';
                        }
                    });
                    break;
            }
        },
        // 待审核 / 审核未通过的菜单显示状态改变时调用，参数为 visible 【经销商】
        visibleChangeFn(val) {
            this.menuItemData.arrow = val;
        },
        // 页码改变的回调函数
        changePaginationFn({ currentPage, pageSize }) {
            if (this.tabsActiveKey === 'vendor') {
                this.agencyTab[0].Pagination.current = currentPage;
                this.agencyTab[0].Pagination.pageSize = pageSize;
                this.agencyTab[0].Pagination.loading = true;
            }
            this.licManageVendorFn(this.tabsActiveKey);
        },
        // 表格重新刷新数据的初始化函数 01
        initRfreshTablesFn() {
            // this.initTablesPaginationFn()
            this.licManageVendorFn(this.tabsActiveKey);
        },
        // 表格重新刷新数据的初始化函数 02 【初始化分页data】
        initTablesPaginationFn() {
            this.currentTrObj = {};
            switch (this.tabsActiveKey) {
                case 'pt':
                    break;
                case 'vendor':
                    this.agencyTab[0].Pagination = {
                        loading: true,
                        total: 10,
                        current: 1,
                        pageSize: 10,
                        pageSizes: [10, 20, 30, 40]
                    };
                    break;
            }
        },
        // 每次进来的初始化请求表格数据
        initTablesFn() {
            this.initTablesPaginationFn();
            this.systemTypes.forEach((item) => {
                if (item === 'pt') return;
                this.licManageVendorFn(item);
            });
        },
        // 录入按钮
        openEntry() {
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            this.entrySt = true;
            this.entryTitle =
                '厂商 - ' +
                this.downTabSet[this.tanActKeyInNum[this.tabsActiveKey] - 1][this.downTabsActiveKey - 1].tab +
                ' - 详细信息';
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
                    let downTabNow = that.downTabSet[that.tanActKeyInNum[that.tabsActiveKey] - 1];
                    const current = downTabNow.find((item) => item.key === that.downTabsActiveKey) || {};
                    that.imgPreviewTitle = '厂商 - ' + current.tab + ' - 图片预览';
                    that.$Utils.globalDragCenterFn('agency-imgpreview');
                };
            }
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
            alert(postName);
            if (
                postName !== 'png' &&
                postName !== 'jpeg' &&
                postName !== 'jpg' &&
                postName !== 'PNG' &&
                postName !== 'JPEG' &&
                postName !== 'JPG'
            ) {
                alert('仅支持png / jpeg');
                return false;
            }
        },
        rowClassFn(record) {
            if (record.key === this.currentTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        dragControllerDiv: function () {
            let that = this;
            let resize = document.getElementsByClassName('resize');
            let boxtop = document.getElementsByClassName('boxtop');
            let mid = document.getElementsByClassName('boxdown');
            let box = document.getElementsByClassName('box');
            for (let i = 0; i < resize.length; i++) {
                // 鼠标按下事件
                resize[i].onmousedown = function (e) {
                    let startY = e.clientY;
                    resize[i].top = resize[i].offsetTop;
                    // 鼠标拖动事件
                    document.onmousemove = function (e) {
                        let endY = e.clientY;
                        let moveLen = resize[i].top + (endY - startY); // （endx-startx）=移动的距离。resize[i].left+移动的距离=左边区域最后的宽度
                        let boxAllHeight = box[i].clientHeight - 4;
                        if (that.searchStatus) boxAllHeight = box[i].clientHeight - 4 - 60;
                        let maxT = boxAllHeight - resize[i].offsetHeight;
                        if (moveLen < 200) moveLen = 200; // 左边区域的最小宽度为200px
                        if (moveLen > maxT - 200) moveLen = maxT - 200; // 右边区域最小宽度为200px
                        // resize[i].style.top = moveLen // 设置左侧区域的宽度
                        that.boxTopHeight = moveLen - 56;
                        that.boxDownHeight = boxAllHeight - moveLen - 12;
                        for (let j = 0; j < boxtop.length; j++) {
                            boxtop[j].style.height = moveLen + 'px';
                            mid[j].style.height = boxAllHeight - moveLen - 12 + 'px';
                        }
                    };
                    // 鼠标松开事件
                    document.onmouseup = function () {
                        document.onmousemove = null;
                        document.onmouseup = null;
                        resize[i].releaseCapture && resize[i].releaseCapture();
                    };
                    resize[i].setCapture && resize[i].setCapture();
                    return false;
                };
            }
        }
    },
    mounted() {
        this.boxTopHeight = this.scrollYNumber - 52;
        this.boxDownHeight = this.scrollYNumber - 7;
        this.dragControllerDiv();
    },
    computed: {
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.searchStatus ? 214 : 154,
                pager = 8;
            return (clientHeight - topHeight - pager) / 2;
        }
    },
    created() {
        // this.platformRegistListAPIFn()
        this.oid = this.$cookie.get('userbelong');
        this.clientHeight = document.body.clientHeight;
        this.clientWidth = document.body.clientWidth;
        this.initTablesFn();
        this.provinceAreaAPIFn();
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
    .agency-down-tabs {
        /*height: 256px;*/
        /*position: absolute;*/
        /*bottom: 0;*/
        /*left: 5px;*/
        /*margin-bottom: 5px;*/
        /*width: calc(100% - 10px);*/
        float: left;
        width: 100%;
        // .ant-tabs-content {
        //     height: 203px !important;
        // }
        .tab-table-wrap {
            margin-top: 8px;
            height: calc(100% - 8px);
            &.minimal {
                width: 55%;
                display: inline-block;
                vertical-align: top;
                margin-left: 1%;
            }
            .form-box {
                height: 100%;
                .head-box {
                    height: 48px;
                    line-height: 26px;
                    text-align: left;
                    position: absolute;
                    top: 52px;
                    left: 8px;
                    z-index: 999;
                    // padding: 10px;
                    // background: #F8FAFC;
                    // border: 1px solid rgba(0,0,0,0.09);
                    // border-radius: 4px;
                }
                .enter-in-btn {
                    width: 53px;
                    height: 24px;
                    padding: 0;
                }
            }
            .no-data-box {
                padding-top: 56px;
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
            .info-count {
                height: 100%;
                background: #fff;
                padding: 0 8px;
            }
            .add-box {
                padding-left: 20px;
                padding-top: 10px;
            }
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
<style scoped>
.box {
    width: 100%;
    top: 40px;
    bottom: 0px;

    overflow: hidden;
    position: absolute;
}
.boxtop {
    width: calc(100% - 2px);
    height: calc(50% - 3px);
    padding: 8px;
    float: left;
}
.resize {
    width: 100%;
    height: 5px;
    cursor: w-resize;
    cursor: s-resize;
    float: left;
    border-bottom: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
}
.boxdown {
    width: calc(100% - 2px);
    padding: 0px 8px;
    float: left;
    height: 50%;
}
</style>
