<template>
    <div class="scoped agency-approval-container">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="approvedStatusBtnFn(true)" type="primary" size="small">
                                认证通过
                            </a-button>
                            <a-button @click="approvedStatusBtnFn(false)" type="primary" size="small">
                                认证未通过
                            </a-button>
                            <a-button @click="popSetTemplate" type="primary" size="small"> 设定模板 </a-button>
                        </li>
                        <li>
                            <a-button @click="modifyBtnFn" type="primary" size="small"> 修改 </a-button>
                            <a-button @click="removeBtnFn" type="danger" size="small"> 删除 </a-button>
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
                                v-model="provinceSearchDefaults"
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
                <a-tab-pane
                    v-for="tabItem in agencyTab"
                    v-bind:style="{ height: scrollYNumber * 2 + 9 + 'px' }"
                    :tab="tabItem.key"
                    forceRender
                    :key="tabItem.systemTypes"
                >
                    <div class="box">
                        <div class="boxtop">
                            <vxe-grid
                                ref="vxeTable"
                                :height="boxTopHeight"
                                :key="tabItem.systemTypes + '_table'"
                                :columns="tableSet[tabItem.sTypeNum - 1]"
                                :data="tabItem.Datas"
                                size="small"
                                show-overflow
                                show-header-overflow
                                resizable
                                border
                                highlight-current-row
                                :loading="tabItem.Pagination.loading"
                                @cell-dblclick="handleDbClick"
                                @cell-click="handleClick"
                            ></vxe-grid>
                            <vxe-pager
                                :current-page="tabItem.Pagination.current"
                                :page-size="tabItem.Pagination.pageSize"
                                :total="tabItem.Pagination.total"
                                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                                :page-sizes="[10, 20, 30, 40]"
                                @page-change="handlePageChange"
                            >
                            </vxe-pager>
                            <!--表格区域-->
                        </div>
                        <div class="resize"></div>
                        <div class="boxdown">
                            <div class="agency-down-tabs" v-bind:style="{ height: boxDownHeight + 'px' }">
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
                                            v-for="tabItem in downTabSet[tabActKeyInNum[tabsActiveKey] - 1]"
                                            :tab="tabItem.tab"
                                            :key="tabItem.key"
                                        >
                                            <div v-if="!currentTrFlag" class="no-data-box">
                                                <div><a-icon type="file-excel" /></div>
                                                请选择一个机构
                                            </div>
                                            <template v-else>
                                                <div class="bottom-tabs-container">
                                                    <div
                                                        class="bottom-tabs-content-left bottom-buttons-vertical-wrapper"
                                                    >
                                                        <ul class="btn-fixed-wrap" style="padding-left: 0">
                                                            <li @click="openEntry(tabItem.key)" v-if="tabItem.btn">
                                                                <a-button size="small" type="primary">录入</a-button>
                                                            </li>

                                                            <!-- 模态框上传 -->
                                                            <li v-if="canOpenUploadModel">
                                                                <a-button
                                                                    @click="openUploadGic"
                                                                    size="small"
                                                                    type="primary"
                                                                    >上传图片</a-button
                                                                >
                                                            </li>
                                                            <!-- 直接上传 -->
                                                            <li v-else>
                                                                <a-upload
                                                                    :action="QiniuUrl"
                                                                    :show-upload-list="false"
                                                                    :multiple="true"
                                                                    :beforeUpload="checkFile"
                                                                    :supportServerRender="true"
                                                                    :data="uploadParams"
                                                                    @change="uploadImg"
                                                                >
                                                                    <div>
                                                                        <a-button type="primary" size="small">
                                                                            上传图片
                                                                        </a-button>
                                                                    </div>
                                                                </a-upload>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                    <div class="bottom-tabs-content-right bottom-pictures-wrapper">
                                                        <bottom-pictures
                                                            :list="
                                                                imgList.filter((_) => _.type === Number(tabItem.key))
                                                            "
                                                            @previewImg="previewImg"
                                                            @delete="deleteImg"
                                                        />
                                                    </div>
                                                </div>
                                            </template>
                                        </a-tab-pane>
                                    </a-tabs>
                                </bottom-tabs-wrapper>
                                <!--表格区域-->
                            </div>
                            <!-- 下半部tab切换区域结束 -->
                        </div>
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
                        @click="initTablesFn"
                    />
                    <!--刷新按钮区域-->
                </a-tooltip>
            </a-tabs>
        </div>
        <!-- 上半部tab切换区域结束 -->
        <!-- 认证未通过开始 -->
        <a-modal
            v-model="certificationNotPassModalVisible"
            :centered="true"
            :maskClosable="false"
            okText="确定"
            cancelText="取消"
            @ok="certificationNotPassModalFn('ok')"
            @cancel="certificationNotPassModalFn('cancel')"
            class="modal-form-input-scoped global-drag certification-not-pass"
        >
            <div v-globalDrag="{ el: 'certification-not-pass' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    认证未通过
                </h6>
            </div>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label" style="width: 60px; justify-content: flex-start"
                            ><em class="em red">*</em>原因</span
                        >
                        <a-input
                            v-model="certificationNotPassReason"
                            placeholder=""
                            class="input"
                            type="textarea"
                            autosize
                            style="height: 80px"
                        />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 认证未通过结束 -->
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
                <img :src="imgSrc" />
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
                <a-button key="back" @click="AgencyInfoCancelFn">取消</a-button>
                <a-button key="submit" type="primary" :loading="AgencyInfo.loading" @click="AgencyInfoOkFn">
                    保存 </a-button
                ><!--:disabled="checkEditInfoFormObjComputed"-->
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>名称</span>
                        <a-input v-model="editInfoFormObj.name" class="input" />
                    </li>
                    <li class="item" v-if="tabsActiveKey === 'hospital'">
                        <span class="label"><em class="em red"></em>别名</span>
                        <a-input v-model="editInfoFormObj.alias" class="input" />
                    </li>
                    <li class="item">
                        <div class="item-children item-children-agency-approval">
                            <span class="label"><em class="em red">*</em>统一社会信用代码</span>
                            <a-input v-model="editInfoFormObj.social_credit_code" class="input input-short" />
                        </div>
                        <div class="item-children">
                            <span class="label" style="width: 80px"><em class="em red">*</em>企业类型</span>
                            <a-select
                                :defaultValue="editInfoFormObj.enterprise_type || 0"
                                v-model="editInfoFormObj.enterprise_type"
                                :style="{ 'width': '160px' }"
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
                            />
                        </div>
                        <div class="item-children">
                            <span class="label label-1">注册资本</span>
                            <a-input v-model="editInfoFormObj.registered_capital" class="input" />
                        </div>
                    </li>
                    <li class="item">
                        <div class="item-children item-children-agency-approval">
                            <span class="label"><em class="em red">*</em>法定代表人</span>
                            <a-input v-model="editInfoFormObj.legal_representative" class="input" />
                        </div>
                        <div class="item-children">
                            <span class="label label-1">企业负责人</span>
                            <a-input v-model="editInfoFormObj.principal" class="input" />
                        </div>
                    </li>
                    <li class="item">
                        <span class="label">开户银行</span>
                        <a-input v-model="editInfoFormObj.bank_name" class="input" />
                    </li>
                    <li class="item">
                        <span class="label">银行账号</span>
                        <a-input v-model="editInfoFormObj.bank_account" class="input" />
                    </li>
                    <li class="item">
                        <div class="item-children item-children-agency-approval">
                            <span class="label">电话</span>
                            <a-input v-model="editInfoFormObj.phone" class="input" />
                        </div>
                        <div class="item-children">
                            <span class="label label-1" style="width: 40px">邮箱</span>
                            <a-input v-model="editInfoFormObj.email" class="input" />
                        </div>
                    </li>
                    <li class="item">
                        <span class="label"><em class="em"></em>地址</span>
                        <a-input v-model="editInfoFormObj.address" class="input" />
                    </li>
                    <li class="item">
                        <span class="label memo" style="margin-top: -40px"><em class="em"></em>备注</span>
                        <a-textarea
                            v-model="editInfoFormObj.description"
                            :autosize="{ minRows: 2, maxRows: 2 }"
                            class="input"
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
            <entry-lic
                :tabsActiveKey="tabsActiveKey"
                :downTabsActiveKey="downTabsActiveKey"
                :currentTrObj="currentTrObj"
                :entryConfirm="entryConfirm"
                v-if="entryModal"
                @refuseEntry="refuseEntryFn"
                @submitEntryObj="submitEntryObj"
                @submitEntrySuccess="submitEntrySuccessFn"
                @submitEntryClose="submitEntryClose"
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
                @submitEntryClose="submitEntryClose"
            >
            </entry-lic-scope>
        </a-modal>
        <!-- 录入操作结束 -->
        <!-- 上传图片 -->
        <a-modal
            :maskClosable="false"
            :centered="true"
            v-model="uploadGicSt"
            :width="600"
            okText="保存"
            @ok="uploadGicFn"
            @cancel="uploadGicSt = false"
            :upLoadStatus="upLoadStatus"
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
                :hideBeginDate="hideBeginDate"
                :uploadDateInfo="uploadDateInfo"
            ></upload-gic>
        </a-modal>
        <!-- 上传图片结束 -->
        <!-- 设定企业模板弹框开始 -->
        <a-modal
            v-model="popSetTemplateFlag"
            :maskClosable="false"
            :width="1000"
            :okText="'保存'"
            :centered="true"
            class="agency-set-template modal-form-input-scoped global-drag"
            @ok="entryConfirmFn"
            @cancel="popSetTemplateFlag = false"
        >
            <div v-globalDrag="{ el: 'agency-set-template' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    机构 - 设定模板
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="popSetTemplateFlag = false">取消</a-button>
                <a-dropdown :disabled="!hasChangeTemplateFlag">
                    <a-menu slot="overlay" @click="saveTemplate">
                        <a-menu-item key="include">包含角色</a-menu-item>
                        <a-menu-item key="exclude">不包含角色</a-menu-item>
                    </a-menu>
                    <a-button key="save" type="primary"
                        >设定模板
                        <a-icon type="down" />
                    </a-button>
                </a-dropdown>
            </template>
            <div class="modal-form-input">
                <vxe-table
                    height="440px"
                    resizable
                    show-overflow
                    highlight-hover-row
                    highlight-current-row
                    ref="templateTable"
                    row-id="id"
                    :radio-config="{ checkRowKey: currentTemplateID, labelField: 'id', trigger: 'row' }"
                    @radio-change="changeTemplate"
                    :data="templates"
                >
                    <vxe-table-column type="radio" field="id" title="ID"></vxe-table-column>
                    <vxe-table-column field="name" title="名称"></vxe-table-column>
                    <vxe-table-column field="note" title="说明"></vxe-table-column>
                </vxe-table>
            </div>
        </a-modal>
        <!-- 设定企业模板弹框结束 -->
    </div>
</template>

<script>
import moment from 'moment';
import TabTypeCfg from '@/config/tabTypeCfg';
import BottomTabsWrapper from '@/components/BaseBottom/BottomTabsWrapper';
import BottomPictures from '@/components/BaseBottom/BottomPictures';
import uploadGic from './modal/uploadGic';
import entryLic from './modal/entryLic';
import entryLicScope from './modal/entryLicScope';
import {
    provinceAreaAPI,
    cityAreaAPI,
    countyAreaAPI,
    listAreaAPI,
    platformAgencyUnAuthListAPI,
    platformAgenecyBaseInfoAPI,
    platformAgenecyEditInfoAPI,
    platformAgencyDelAPI,
    platformAgencyPassAPI,
    platformAgencyNoPassAPI,
    platformAgencyImgListAPI,
    platformAgencyAddPicAPI,
    licDeleteImg,
    getAllGroupTemplateAPI,
    getGroupTemplateAPI,
    setTemplate2GroupAPI,
    getUploadTokenAPI,
    platformAgencyLicAPI,
    platformAgencyEditLicAPI,
    platformAgencyDealerAPI,
    platformAgencyEditDealerAPI,
    platformAgencyHospitalAPI,
    platformAgencyEditHospitalAPI,
    platformAgencyVendorAPI,
    platformAgencyEditVendorAPI
} from '@/service/pageAjax';

export default {
    name: 'AgencyApproval',
    components: {
        BottomPictures,
        BottomTabsWrapper,
        uploadGic,
        entryLic,
        entryLicScope
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            uploadDateInfo: {}, // 上传图片日期信息
            uploadParams: {},
            boxTopHeight: 300,
            boxDownHeight: 300,
            searchStatus: false,
            selectedKey: '',
            systemTypes: ['pt', 'hospital', 'dealer', 'vendor'],
            auditStatus: ['未认证', '用户申请认证', '认证通过', '认证未通过'], // 机构认证状态(audit_status): 0-未认证 1-用户申请认证 2-认证通过 3-认证未通过
            rangePickerVal: moment[''], // 申请时间的默认赋值
            agencyTab: TabTypeCfg.AgencyTabSet(),
            tableSet: [
                [
                    {
                        title: '名称',
                        field: 'name',
                        minWidth: '200',
                        align: 'center'
                    },
                    {
                        title: '别名',
                        field: 'alias',
                        minWidth: '150',
                        align: 'center'
                    },
                    {
                        title: '统一社会信用代码',
                        field: 'social_credit_code',
                        minWidth: '150',
                        align: 'center'
                    },
                    {
                        title: '电话',
                        field: 'phone',
                        width: '150',
                        align: 'center'
                    },
                    {
                        title: '邮箱',
                        field: 'email',
                        minWidth: '100',
                        align: 'center'
                    },
                    {
                        title: '注册时间',
                        field: 'register_time',
                        width: '200',
                        align: 'center'
                    },
                    {
                        title: '认证状态',
                        field: 'audit_status_text',
                        width: '120',
                        align: 'center',
                        slots: {
                            default: ({ row }) => {
                                if (row.certification_status === 3) {
                                    return [<span style="color:red;">{row.audit_status_text}</span>];
                                }
                                return [<span>{row.audit_status_text}</span>];
                            }
                        }
                    }
                ],
                [
                    {
                        title: '名称',
                        field: 'name',
                        minWidth: '250',
                        align: 'center'
                    },
                    {
                        title: '统一社会信用代码',
                        field: 'social_credit_code',
                        minWidth: '200',
                        align: 'center'
                    },
                    {
                        title: '电话',
                        field: 'phone',
                        width: '150',
                        align: 'center'
                    },
                    {
                        title: '邮箱',
                        field: 'email',
                        minWidth: '150',
                        align: 'center'
                    },
                    {
                        title: '注册时间',
                        field: 'register_time',
                        width: '200',
                        align: 'center'
                    },
                    {
                        title: '认证状态',
                        field: 'audit_status_text',
                        width: '100',
                        align: 'center',
                        slots: {
                            default: ({ row }) => {
                                if (row.certification_status === 3) {
                                    return [<span style="color:red;">{row.audit_status_text}</span>];
                                }
                                return [<span>{row.audit_status_text}</span>];
                            }
                        }
                    }
                ],
                [
                    {
                        title: '名称',
                        field: 'name',
                        minWidth: '250',
                        align: 'center'
                    },
                    {
                        title: '统一社会信用代码',
                        field: 'social_credit_code',
                        minWidth: '200',
                        align: 'center'
                    },
                    {
                        title: '电话',
                        field: 'phone',
                        width: '150',
                        align: 'center'
                    },
                    {
                        title: '邮箱',
                        field: 'email',
                        minWidth: '150',
                        align: 'center'
                    },
                    {
                        title: '注册时间',
                        field: 'register_time',
                        width: '200',
                        align: 'center'
                    },
                    {
                        title: '认证状态',
                        field: 'audit_status_text',
                        width: '100',
                        align: 'center',
                        slots: {
                            default: ({ row }) => {
                                if (row.certification_status === 3) {
                                    return [<span style="color:red;">{row.audit_status_text}</span>];
                                }
                                return [<span>{row.audit_status_text}</span>];
                            }
                        }
                    }
                ]
            ],
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
            editInfoFormOldObj: {
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
            entrySt: false,
            clientHeight: 0,
            clientWidth: 0,
            upLoadStatus: false,
            imageFlag: [false, false, false, false, false, false, false, false, false],
            // 表格选中的当前行数据是否记住旧值
            recordCurrentTrObj: {
                hospitals: {},
                dealers: {},
                vendors: {}
            },
            // 修改弹框的配置
            disableUpload: false,
            AgencyInfo: {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            },
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
            provinceSearchDefaults: [],
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
            AgencyInfoModal: {
                title: `${this.$route.meta.title} - 信息修改`,
                alert: false,
                loading: false
            },
            imgList: [],
            tabsActiveKey: 'dealer',
            downTabsActiveKey: '1',
            tabActKeyInNum: {
                'pt': 0,
                'hospital': 1,
                'dealer': 2,
                'vendor': 3
            },
            popSetTemplateFlag: false, // 设置组织模板弹出框标志
            templates: [], // 所有组织模板
            currentGroup: '', // 当前组织的key：<system type> + <oid>
            // currentTemplateID: '', // 当前组织的模板key
            currentTemplateID: 0, // 当前组织的模板ID
            hasChangeTemplateFlag: false, // 当前是否有更改
            newTemplateKey: '',
            certificationNotPassReason: '',
            certificationNotPassModalVisible: false
        };
    },
    computed: {
        hideBeginDate() {
            const topTabActive = this.tabsActiveKey;
            const bottomTabActive = this.downTabsActiveKey;
            return (
                (topTabActive === 'dealer' && bottomTabActive === '2') ||
                (topTabActive === 'vendor' && bottomTabActive === '2')
            );
        },
        canOpenUploadModel() {
            // 图片上传 => 根据不同tab类型是否打开model还是直接上传
            const dealerExcludes = ['5', '6', '7', '8', '0']; // 经销商 上传图片不需要模态框的tab类型
            const hospitalExcludes = ['3', '0']; // // 医院 上传图片不需要模态框的tab类型
            const vendorExcludes = ['4', '5', '6', '7', '0']; // // 厂商 上传图片不需要模态框的tab类型
            if (this.tabsActiveKey === 'hospital' && hospitalExcludes.includes(this.downTabsActiveKey)) {
                return false;
            }
            if (this.tabsActiveKey === 'dealer' && dealerExcludes.includes(this.downTabsActiveKey)) {
                return false;
            }
            if (this.tabsActiveKey === 'vendor' && vendorExcludes.includes(this.downTabsActiveKey)) {
                return false;
            }
            return true;
        },
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.searchStatus ? 214 : 154,
                pager = 8;
            return (clientHeight - topHeight - pager) / 2;
        },
        tableHeight() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
            let topHeight = this.searchStatus ? 281 : 221;
            let bottomHeight = 256;
            return clientHeight - topHeight - bottomHeight;
        }
    },
    mounted() {
        this.boxTopHeight = this.scrollYNumber - 52;
        this.boxDownHeight = this.scrollYNumber - 6;
        this.dragControllerDiv();
        this.getUploadTokenAPIFn();
    },
    methods: {
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
                        if (that.searchStatus) boxAllHeight = box[i].clientHeight - 5;
                        let maxT = boxAllHeight - resize[i].offsetHeight;
                        if (moveLen < 200) moveLen = 200; // 左边区域的最小宽度为200px
                        if (moveLen > maxT - 200) moveLen = maxT - 200; // 右边区域最小宽度为200px
                        that.boxTopHeight = moveLen - 56;
                        that.boxDownHeight = boxAllHeight - moveLen - 10;
                        for (let j = 0; j < boxtop.length; j++) {
                            boxtop[j].style.height = moveLen + 'px';
                            mid[j].style.height = boxAllHeight - moveLen - 10 + 'px';
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
        },
        moment,
        // 获取全部省份列表
        async provinceAreaAPIFn() {
            await provinceAreaAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const datas = res.province.map((item) => {
                        item.value = item.administrative_division_sn;
                        item.label = item.name;
                        item.isLeaf = item.is_leaf === 1; // is_leaf 0：没有下一级叶子节点   1：有
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
                    } else if (this.provinceDefaultsFlag && this.provinceSearchDefaults[0]) {
                        this.provinces.forEach((item, index) => {
                            if (parseFloat(item.value) === parseFloat(this.provinceSearchDefaults[0])) {
                                this.provincesObj = {
                                    pIndex: index,
                                    cIndex: -1
                                };
                            }
                        });
                        this.cityAreaAPIFn(this.provinceSearchDefaults[0]);
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
                        item.isLeaf = item.is_leaf === 1; // is_leaf 0：没有下一级叶子节点   1：有
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
                    } else if (this.provinceDefaultsFlag && this.provinceSearchDefaults[1]) {
                        this.provinces.splice(this.provincesObj.pIndex, 1, {
                            ...this.provinces[this.provincesObj.pIndex],
                            children: [...datas]
                        });
                        this.provinces[this.provincesObj.pIndex].children.forEach((item, index) => {
                            if (parseFloat(item.value) === parseFloat(this.provinceSearchDefaults[1])) {
                                this.provincesObj = {
                                    ...this.provincesObj,
                                    cIndex: index
                                };
                            }
                        });
                        this.countyAreaAPIFn(this.provinceSearchDefaults[1]);
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
                        // item.isLeaf = item.is_leaf === 1 // is_leaf 0：没有下一级叶子节点   1：有
                        return item;
                    });

                    if (this.provinceDefaultsFlag && this.provinceDefaults[2]) {
                        this.provinces[this.provincesObj.pIndex].children.splice(this.provincesObj.cIndex, 1, {
                            ...this.provinces[this.provincesObj.pIndex].children[this.provincesObj.cIndex],
                            children: [...datas]
                        });
                    } else if (this.provinceDefaultsFlag && this.provinceSearchDefaults[2]) {
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
        // 根据注册机构类型【获取机构列表】
        async platformAgencyUnAuthListAPIFn(systemType = this.tabsActiveKey) {
            let sysType = 0;
            // 判断参数有没有， 没有直接不要传给后端
            let params = {
                // 排序
                orderByColumn: 'register_time',
                isAsc: 'desc'
            };
            if (this.searchTables.name) {
                params.name = this.searchTables.name;
            }
            if (this.searchTables.social_credit_code) {
                params.social_credit_code = this.searchTables.social_credit_code;
            }
            if (this.searchTables.administrative_division_sn) {
                params.administrative_division_sn = this.searchTables.administrative_division_sn;
            }
            switch (systemType) {
                case 'pt':
                    break;
                case 'hospital':
                    sysType = 1;
                    params.pageNum = this.agencyTab[1].Pagination.current;
                    params.pageSize = this.agencyTab[1].Pagination.pageSize;
                    break;
                case 'dealer':
                    sysType = 2;
                    params.pageNum = this.agencyTab[0].Pagination.current;
                    params.pageSize = this.agencyTab[0].Pagination.pageSize;
                    break;
                case 'vendor':
                    sysType = 3;
                    params.pageNum = this.agencyTab[2].Pagination.current;
                    params.pageSize = this.agencyTab[2].Pagination.pageSize;
                    break;
            }
            if (sysType === 0) {
                return;
            }
            await platformAgencyUnAuthListAPI(sysType, {
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res;
                    let rowsNew = [];
                    if (res.rows.length !== 0) {
                        rowsNew = rows.map((item) => {
                            item.key = this.tabsActiveKey + '-' + item.id; // id
                            item.audit_status_text = this.auditStatus[parseFloat(item.certification_status)];
                            item.activeClass = '';
                            return item;
                        });
                    }

                    switch (systemType) {
                        case 'pt':
                            break;
                        case 'hospital':
                            this.agencyTab[1].Datas = rowsNew;
                            this.agencyTab[1].Pagination = {
                                ...this.agencyTab[1].Pagination,
                                total,
                                loading: false
                            };
                            break;
                        case 'dealer':
                            this.agencyTab[0].Datas = rowsNew;
                            this.agencyTab[0].Pagination = {
                                ...this.agencyTab[0].Pagination,
                                total,
                                loading: false
                            };
                            break;
                        case 'vendor':
                            this.agencyTab[2].Datas = rowsNew;
                            this.agencyTab[2].Pagination = {
                                ...this.agencyTab[2].Pagination,
                                total,
                                loading: false
                            };
                            break;
                    }
                    this.changeTabsFn(this.tabsActiveKey);
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
                    if (org.administrative_division_sn) {
                        this.listAreaAPIFn(org.administrative_division_sn);
                    } else {
                        this.provinceAreaAPIFn();
                    }
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
                            if (res.images[i].type === 1) {
                                this.disableUpload = true;
                            }
                        }
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 删除机构
        async platformAgencyDelAPIFn(systemType, id) {
            let sysType = this.ReturnSysType(systemType);
            await platformAgencyDelAPI(sysType, id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // this.$message.success(`${this.currentTrObj.name} - 删除成功`)
                    this.initRfreshTablesFn();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 修改注册机构信息
        async platformAgencyEditAPIFn(systemType, id, params) {
            let sysType = this.ReturnSysType(systemType);
            if (params.establish_date) {
                params.establish_date = moment(params.establish_date).format('YYYY-MM-DD');
            }
            await platformAgenecyEditInfoAPI(sysType, id, params).then((res) => {
                this.AgencyInfo.loading = false;
                if (parseFloat(res.code) === 0) {
                    // this.editInfoFormObj.establish_date = moment(this.editInfoFormObj.establish_date).format('YYYY-MM-DD')
                    // this.currentTrObj = {
                    //     ...this.editInfoFormObj
                    // }
                    // this.editInfoFormObj = {}
                    this.AgencyInfo.alert = false;
                    this.submitEntryObj(this.editInfoFormObj);
                    // this.$message.success(`${this.currentTrObj.name} - 修改成功`)
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 机构认证通过
        async platformAgencyPassAPIFn(systemType, id) {
            let sysType = this.ReturnSysType(systemType);
            await platformAgencyPassAPI(sysType, id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // this.$message.success(`${this.currentTrObj.name} - 认证成功`)
                    this.$success({
                        title: `${this.currentTrObj.name} - 认证成功`,
                        content: '请到 机构管理 - 机构 中查看！',
                        centered: true,
                        maskClosable: false
                    });
                    this.initRfreshTablesFn();
                } else {
                    this.$error({
                        title: `${this.currentTrObj.name} - 认证通过失败`,
                        content: res.msg,
                        centered: true,
                        maskClosable: false
                    });
                }
            });
        },
        // 机构认证不通过
        async platformAgencyNoPassAPIFn(systemType, id) {
            let sysType = this.ReturnSysType(systemType);
            await platformAgencyNoPassAPI(sysType, id, this.certificationNotPassReason.trim()).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // this.$message.success(`${this.currentTrObj.name} - 审核不通过操作成功`)
                    this.initRfreshTablesFn();
                    this.certificationNotPassModalFn('cancel');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 认证校验相关信息
        approvedCheck(type) {
            if (!this.checkCurrentTrDataFn()) return;
            const row = this.currentTrObj;
            if (parseInt(row.certification_status) > 1) {
                this.$warning({
                    title: '操作提示',
                    content: `只能操作【${this.auditStatus[0]}、${this.auditStatus[1]}】状态的机构。`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (type && row.legal_representative === '') {
                // 如果是认证通过操作 法定代表人必填
                this.$warning({
                    title: '操作提示',
                    content: '请完善法定代表人信息后再进行相关操作',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 审核操作按钮
        approvedStatusBtnFn(type) {
            if (!this.checkCurrentTrDataFn() || !this.approvedCheck(type)) return;
            const _this = this;
            if (type) {
                _this.$confirm({
                    title: '认证通过',
                    content: '认证即将通过，机构信息是否完整和准确？',
                    centered: true,
                    maskClosable: false,
                    onOk() {
                        _this.platformAgencyPassAPIFn(_this.tabsActiveKey, _this.currentTrObj.id);
                    }
                });
            } else {
                this.certificationNotPassModalVisible = true;
                this.$Utils.globalDragCenterFn('certification-not-pass');
            }
        },
        certificationNotPassModalFn(actionName) {
            if (actionName === 'ok') {
                if (!this.certificationNotPassReason.trim()) {
                    this.$warning({
                        title: '操作提示',
                        content: `未通过原因不能为空`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }
                this.platformAgencyNoPassAPIFn(this.tabsActiveKey, this.currentTrObj.id);
            } else {
                this.certificationNotPassModalVisible = false;
                this.certificationNotPassReason = '';
            }
        },
        // 删除按钮
        removeBtnFn() {
            if (!this.checkCurrentTrDataFn()) return;
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '删除后，机构需要重新注册，您真要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.platformAgencyDelAPIFn(_this.tabsActiveKey, _this.currentTrObj.id);
                    return new Promise((resolve, reject) => {
                        setTimeout(Math.random() > 0.5 ? resolve : reject, 1000);
                    }).catch(() => {});
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
            switch (this.tabsActiveKey) {
                case 'hospital':
                    if (this.agencyTab[1].Datas.length) {
                        this.currentTrObj =
                            Object.keys(this.recordCurrentTrObj.hospitals).length > 0
                                ? this.recordCurrentTrObj.hospitals
                                : this.agencyTab[1].Datas[0];
                        this.platformAgencyImgListAPIFn(this.tabsActiveKey, this.currentTrObj.id);
                        this.currentTrFlag = true;
                        this.changeDownTabsFn('1');
                        setTimeout(() => {
                            this.$refs.vxeTable[1].resetAll();
                            this.$refs.vxeTable[1].setCurrentRow(this.currentTrObj);
                        }, 10);
                    } else {
                        this.currentTrObj = {};
                    }
                    break;
                case 'dealer':
                    if (this.agencyTab[0].Datas.length) {
                        this.currentTrObj =
                            Object.keys(this.recordCurrentTrObj.dealers).length > 0
                                ? this.recordCurrentTrObj.dealers
                                : this.agencyTab[0].Datas[0];
                        this.platformAgencyImgListAPIFn(this.tabsActiveKey, this.currentTrObj.id);
                        this.currentTrFlag = true;
                        this.changeDownTabsFn('1');
                        setTimeout(() => {
                            this.$refs.vxeTable[0].resetAll();
                            this.$refs.vxeTable[0].setCurrentRow(this.currentTrObj);
                        }, 10);
                    } else {
                        this.currentTrObj = {};
                    }
                    break;
                case 'vendor':
                    if (this.agencyTab[2].Datas.length) {
                        this.currentTrObj =
                            Object.keys(this.recordCurrentTrObj.vendors).length > 0
                                ? this.recordCurrentTrObj.vendors
                                : this.agencyTab[2].Datas[0];
                        this.platformAgencyImgListAPIFn(this.tabsActiveKey, this.currentTrObj.id);
                        this.currentTrFlag = true;
                        this.changeDownTabsFn('1');
                        setTimeout(() => {
                            this.$refs.vxeTable[2].resetAll();
                            this.$refs.vxeTable[2].setCurrentRow(this.currentTrObj);
                        }, 10);
                    } else {
                        this.currentTrObj = {};
                    }
                    break;
            }
        },
        // 根据图片列表判断上传按钮是否可用
        uploadBtnStFn() {
            if (this.imgList.length === 0) {
                this.disableUpload = false;
            } else {
                for (let i = 0; i < this.imgList.length; i++) {
                    if (this.tabsActiveKey === 'hospital' || this.tabsActiveKey === 'vendor') {
                        if (this.imgList[i].type === 1 && this.downTabsActiveKey === '1') {
                            this.disableUpload = true;
                            return;
                        } else if (this.imgList[i].type === 2 && this.downTabsActiveKey === '2') {
                            this.disableUpload = true;
                            return;
                        } else {
                            this.disableUpload = false;
                        }
                    } else if (this.tabsActiveKey === 'dealer') {
                        if (this.imgList[i].type === 1 && this.downTabsActiveKey === '1') {
                            this.disableUpload = true;
                            return;
                        } else if (this.imgList[i].type === 2 && this.downTabsActiveKey === '2') {
                            this.disableUpload = true;
                            return;
                        } else if (this.imgList[i].type === 3 && this.downTabsActiveKey === '3') {
                            this.disableUpload = true;
                            return;
                        } else {
                            this.disableUpload = false;
                        }
                    }
                }
            }
        },
        changeDownTabsFn(activeKey) {
            this.downTabsActiveKey = activeKey;
            this.uploadBtnStFn();
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
        // 搜索操作
        searchBtnFn() {
            this.initRfreshTablesFn();
        },
        // 修改模态框操作
        AgencyInfoOkFn() {
            if (!this.checkEditInfoFormMsgFn()) return; // 前端格式校验
            let checkRes = this.checkEditInfoFormObjFn(this.editInfoFormOldObj, this.editInfoFormObj);
            if (Object.keys(checkRes.newObj).length <= 0) {
                // 后端传值校验
                this.AgencyInfoCancelFn();
                return;
            }
            this.AgencyInfo.loading = true;
            this.platformAgencyEditAPIFn(this.tabsActiveKey, this.currentTrObj.id, checkRes.newObj);
        },
        // 检测修改的表单信息校验
        checkEditInfoFormMsgFn() {
            const socialCreditCode = /^(\d|[a-zA-Z])+$/,
                // mobilePhone = /^1\d{10}$/,
                email = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
            // let sysType = this.ReturnSysType(this.tabsActiveKey)
            // 名称 01
            if (!this.editInfoFormObj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
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
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
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
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return false;
            }

            // 统一社会信用代码 04
            if (!socialCreditCode.test(this.editInfoFormObj.social_credit_code)) {
                this.$warning({
                    title: '操作提示',
                    content: '统一社会信用代码格式为字母和数字组合的18位',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return false;
            }

            // 行政区域
            if (!this.editInfoFormObj.administrative_division_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择行政区划',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return false;
            }
            // 电话
            // if (this.editInfoFormObj.phone.length > 0) {
            //     if (!mobilePhone.test(this.editInfoFormObj.phone)) {
            //         this.$warning({
            //             title: '操作提示',
            //             content: '电话格式输入有误',
            //             okText: '知道了',
            //             centered: true,
            //             maskClosable: false
            //         })
            //         return false
            //     }
            // }
            // 邮箱
            if (this.editInfoFormObj.email.length > 0) {
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
                this.editInfoFormObj.legal_representative = this.$Utils.trim(this.editInfoFormObj.legal_representative);
                return false;
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
                            if (server === 'establish_date' && moment(serverItem).format('YYYY-MM-DD') === oldItem) {
                                newForm = {
                                    ...newForm
                                };
                            } else {
                                newForm = {
                                    ...newForm,
                                    [server]: serverItem, // 新值
                                    [server + '_old']: oldItem // 旧值
                                };
                            }
                            info.editInfoFlag = false;
                            info.newObj = newForm;
                        }
                    }
                }
            }
            return info;
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
        // 修改操作
        modifyBtnFn() {
            if (!this.checkCurrentTrDataFn()) return;
            this.AgenecyBaseInfoAPIFn(this.tabsActiveKey, this.currentTrObj.id);
            this.editInfoFormObj.administrative_division_sn = this.currentTrObj.administrative_division_sn;
            this.provinceDefaultsFlag = true;
            this.provinceDefaults = [];
            this.AgencyInfo.alert = true;
            this.$Utils.globalDragCenterFn('agency-edit');
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
            if (this.searchStatus) {
                this.boxTopHeight -= 60;
                try {
                    document.getElementsByClassName('boxtop')[0].style.height = this.boxTopHeight + 53 + 'px';
                } catch (e) {
                    console.log(e);
                }
            } else {
                this.boxTopHeight += 60;
                try {
                    document.getElementsByClassName('boxtop')[0].style.height = this.boxTopHeight + 54 + 'px';
                } catch (e) {
                    console.log(e);
                }
            }
        },
        // 点击表格的每一行时记录点击的哪一个列表哪一个数据
        recordRowsFn(record) {
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            switch (sysType) {
                case 1:
                    this.recordCurrentTrObj.hospitals = record;
                    break;
                case 2:
                    this.recordCurrentTrObj.dealers = record;
                    break;
                case 3:
                    this.recordCurrentTrObj.vendors = record;
                    break;
            }
        },
        // 表格的高亮样式显示
        setDatasClassFn(key) {
            switch (this.tabsActiveKey) {
                case 'hospital':
                    this.agencyTab[1].Datas.forEach((item) => {
                        if (item.key === key) {
                            item.activeClass = 'active-tr';
                        } else {
                            item.activeClass = '';
                        }
                    });
                    break;
                case 'dealer':
                    this.agencyTab[0].Datas.forEach((item) => {
                        if (item.key === key) {
                            item.activeClass = 'active-tr';
                        } else {
                            item.activeClass = '';
                        }
                    });
                    break;
                case 'vendor':
                    this.agencyTab[2].Datas.forEach((item) => {
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
        // 待审核 / 审核未通过的切换 【经销商】
        clickMenuItemData1Fn(val) {
            if (parseInt(val.key) === 1) {
                this.menuItemData = {
                    class: true,
                    arrow: false
                };
            } else {
                this.menuItemData = {
                    class: false,
                    arrow: false
                };
            }
        },
        // 页码改变的回调函数
        changePaginationFn(page) {
            switch (this.tabsActiveKey) {
                case 'pt':
                    break;
                case 'hospital':
                    this.agencyTab[1].Pagination = {
                        ...page,
                        loading: true,
                        pageNum: page.current
                    };
                    break;
                case 'dealer':
                    this.agencyTab[0].Pagination = {
                        ...page,
                        loading: true,
                        pageNum: page.current
                    };
                    break;
                case 'vendor':
                    this.agencyTab[2].Pagination = {
                        ...page,
                        loading: true,
                        pageNum: page.current
                    };
                    break;
            }
            this.platformAgencyUnAuthListAPIFn(this.tabsActiveKey);
        },
        // 表格重新刷新数据的初始化函数 01
        initRfreshTablesFn() {
            if (!this.checkSearchInfoFormMsgFn()) return;
            // this.initTablesPaginationFn()
            this.platformAgencyUnAuthListAPIFn(this.tabsActiveKey);
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

            if (this.searchStatus && this.$Utils.checkInputValFn(this.searchTables.registrant)) {
                this.$warning({
                    title: '操作提示',
                    content: `注册人${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 表格重新刷新数据的初始化函数 02 【初始化分页data】
        initTablesPaginationFn() {
            this.currentTrObj = {};
            switch (this.tabsActiveKey) {
                case 'pt':
                    break;
                case 'hospital':
                    this.agencyTab[1].Pagination.current = 1;
                    this.agencyTab[1].Pagination.loading = true;
                    break;
                case 'dealer':
                    this.agencyTab[0].Pagination.current = 1;
                    this.agencyTab[0].Pagination.loading = true;
                    break;
                case 'vendor':
                    this.agencyTab[2].Pagination.current = 1;
                    this.agencyTab[2].Pagination.loading = true;
                    break;
            }
        },
        // 每次进来的初始化请求表格数据
        initTablesFn() {
            this.initTablesPaginationFn();
            this.systemTypes.forEach((item) => {
                if (item === 'pt') return;
                this.platformAgencyUnAuthListAPIFn(item);
            });
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
                    let downTabNow = that.downTabSet[that.tabActKeyInNum[that.tabsActiveKey] - 1];
                    const current = downTabNow.find((item) => item.key === that.downTabsActiveKey) || {};
                    that.imgPreviewTitle = '机构认证 - ' + current.tab + ' - 图片预览';
                    that.$Utils.globalDragCenterFn('agency-imgpreview');
                };
            }
        },
        // 录入按钮
        openEntry() {
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            this.entrySt = true;
            this.entryTitle =
                '机构认证 - ' +
                this.downTabSet[this.tabActKeyInNum[this.tabsActiveKey] - 1][this.downTabsActiveKey - 1].tab +
                ' - 录入';
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
        // 关闭弹框 新增方法 杨
        submitEntryClose() {
            this.entryConfirm = false;
            this.entrySt = false;
            this.entryModal = false;
            this.entryScopeModal = false;
        },
        // 录入结束返回
        submitEntrySuccessFn(obj) {
            if (obj) {
                this.entryConfirm = false;
                // this.entrySt = false
                // this.entryModal = false
                this.entryScopeModal = false;
            } else {
                this.entryConfirm = false;
                // this.entrySt = false
                // this.entryModal = false
                // this.entryScopeModal = false
            }
        },
        // 录入后更新列表数据
        submitEntryObj(obj) {
            this.entryModal = false;
            this.refuseEntryFn();
            this.currentTrObj.name = obj.name;
            if (obj.social_credit_code !== undefined) {
                this.currentTrObj.social_credit_code = obj.social_credit_code;
            }
            // 邮箱
            if (obj.email !== undefined) {
                this.currentTrObj.email = obj.email;
            }
            // 电话
            if (obj.phone !== undefined) {
                this.currentTrObj.phone = obj.phone;
            }
            // 别名
            if (obj.alias !== undefined) {
                this.currentTrObj.alias = obj.alias;
            }
            this.currentTrObj.legal_representative = obj.legal_representative || '';
            this.entryScopeModal = false;
            this.entrySt = false;
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
            let fileName = this.$Utils.formatFileNameToLowerCase(file.name);
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
                alert('仅支持png / jpeg / pdf');
                return false;
            }
        },
        uploadImg(info) {
            // 上传图片
            let sysType = this.ReturnSysType(this.tabsActiveKey);

            if (info.file.status === 'done') {
                const res = info.file.response;
                if (res.Code || res.code === 0) {
                    let url = res.data.key;
                    let params = {
                        url: url,
                        file_name: this.$Utils.formatFileNameToLowerCase(info.file.name)
                    };
                    platformAgencyAddPicAPI(sysType, this.currentTrObj.id, this.downTabsActiveKey, params).then(
                        (res) => {
                            if (parseFloat(res.code) === 0) {
                                this.imgList.push({ ...res.info });
                                this.imageFlag[this.downTabsActiveKey] = true;
                                this.uploadBtnStFn();
                            } else {
                                this.$message.error(res.msg);
                            }
                        }
                    );
                } else {
                    this.$message.error(res.msg);
                }
            }
        },
        async getUploadTokenAPIFn() {
            // 获取上传 token
            await getUploadTokenAPI().then((res) => {
                const { code, data } = res;
                if (parseFloat(code) === 0) {
                    this.uploadParams = {
                        token: data
                    };
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 打开上传图片
        openUploadGic() {
            this.uploadGicSt = true;

            const tabs = this.downTabSet[this.tabActKeyInNum[this.tabsActiveKey] - 1]; // 机构tabs || 医院tabs || 厂商tabs
            const current = tabs.find((item) => item.key === this.downTabsActiveKey) || {};
            this.uploadTitle = '机构认证 - ' + current.tab + ' - 上传图片';

            // 获取相关录入信息的日期数据
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            switch (this.downTabsActiveKey) {
                // 营业执照
                case '1': {
                    platformAgencyLicAPI(sysType, this.currentTrObj.id).then((res) => {
                        this.setUploadDateInfo(res);
                    });
                    break;
                }
                // 医疗器械经营许可证 | 医疗机构执业许可证 | 医疗器械生产许可证
                case '2': {
                    let apiFlag = {
                        dealer: platformAgencyDealerAPI,
                        hospital: platformAgencyHospitalAPI,
                        vendor: platformAgencyVendorAPI
                    };
                    apiFlag[this.tabsActiveKey](this.currentTrObj.id).then((res) => {
                        this.setUploadDateInfo(res);
                    });
                    break;
                }
            }

            let cmp = this.$refs.upGicPage;
            if (cmp) {
                cmp.initUploadGic();
            }
            this.uploadGicData = {
                url: '',
                begin_date: moment[''],
                end_date: moment['']
            };
            this.upLoadStatus = true;
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
        // 上传图片
        async uploadGicFn() {
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            this.$refs.upGicPage.returnPageData();
            if (this.uploadGicData.url === '') {
                this.$warning({
                    title: '操作提示',
                    content: '请上传图片',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return;
            }
            let params = {
                url: this.uploadGicData.url,
                begin_date: this.uploadGicData.begin_date || '',
                end_date: this.uploadGicData.end_date || '',
                file_name: this.$Utils.formatFileNameToLowerCase(this.uploadGicData.filename)
            };

            const begin = params.begin_date || '';
            const end = params.end_date || '';
            // 如果选择了日期  校验开始日期需小于截止日期
            if (!this.hideBeginDate && (begin || end) && !moment(begin).isBefore(end)) {
                this.$warning({
                    title: '操作提示',
                    content: '开始日期需小于截止日期',
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return;
            }

            await platformAgencyAddPicAPI(sysType, this.currentTrObj.id, this.downTabsActiveKey, params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // 更新相关录入信息的日期数据
                    switch (this.downTabsActiveKey) {
                        // 营业执照
                        case '1': {
                            platformAgencyEditLicAPI(sysType, this.currentTrObj.id, {
                                begin_date: params.begin_date,
                                end_date: params.end_date
                            });
                            break;
                        }
                        // 医疗器械经营许可证 | 医疗机构执业许可证 | 医疗器械生产许可证
                        case '2': {
                            let apiFlag = {
                                dealer: platformAgencyEditDealerAPI,
                                hospital: platformAgencyEditHospitalAPI,
                                vendor: platformAgencyEditVendorAPI
                            };
                            apiFlag[this.tabsActiveKey](this.currentTrObj.id, {
                                begin_date: params.begin_date,
                                end_date: params.end_date
                            });
                            break;
                        }
                    }

                    this.upLoadStatus = false;
                    this.$refs.upGicPage.initUploadGic();
                    this.imgList.push({ ...res.info });
                    this.imageFlag[this.downTabsActiveKey] = true;
                    this.uploadGicSt = false;
                    this.uploadBtnStFn();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 删除图片
        async licDeleteImgFn(item) {
            let ImgItem = item;
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            await licDeleteImg(sysType, this.currentTrObj.id, ImgItem.type, ImgItem.org_related_image_inner_sn).then(
                (res) => {
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
                        this.uploadBtnStFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                }
            );
        },
        // 删除图片
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
        getUpGicData(data) {
            this.uploadGicData = data;
        },
        // 处理双击事件
        handleDbClick(cellData) {
            let record = cellData.row;
            this.currentTrFlag = true;
            this.currentTrObj = record;
            this.changeDownTabsFn('1');
            this.recordRowsFn(record);
            this.modifyBtnFn();
        },
        // 处理单击事件
        handleClick(cellData) {
            let record = cellData.row;
            if (this.currentTrObj === record) return;
            this.platformAgencyImgListAPIFn(this.tabsActiveKey, record.id);
            this.setDatasClassFn(record.key);
            this.recordRowsFn(record);
            this.currentTrObj = record;
            this.currentTrFlag = true;
            this.changeDownTabsFn('1');
        },
        // 处理分页
        handlePageChange({ currentPage, pageSize }) {
            this.changePaginationFn({
                current: currentPage,
                pageSize: pageSize
            });
        },
        // 弹出模板设置
        popSetTemplate() {
            this.getAllGroupTemplate().then(() => {
                this.getGroupTemplate().then(() => {
                    this.newTemplateID = 0;
                    this.hasChangeTemplateFlag = false;

                    this.popSetTemplateFlag = true;
                    this.$Utils.globalDragCenterFn('agency-set-template');
                    this.$nextTick(() => {
                        this.$refs.templateTable && this.$refs.templateTable.refreshColumn();
                    });
                    if (this.currentTemplateID === 0) {
                        return;
                    }
                    this.templates.some((template) => {
                        if (template.id === this.currentTemplateID) {
                            // 防止table组件尚未初始化完毕，延迟执行设置选中
                            // TODO: 实现比较粗糙，应该循环判断，直至设置成功
                            let delayTime = 1;
                            if (!this.$refs.templateTable) {
                                delayTime = 100;
                            }
                            setTimeout(() => {
                                this.$refs.templateTable.setRadioRow(template);
                            }, delayTime);
                            return true;
                        }
                    });
                });
            });
        },
        // 选择template
        changeTemplate({ row }) {
            this.newTemplateID = row.id;
            if (this.newTemplateID === this.currentTemplateID) {
                this.hasChangeTemplateFlag = false;
            } else {
                this.hasChangeTemplateFlag = true;
            }
        },
        // 保存template修改
        saveTemplate({ key }) {
            let includeRole = false;
            if (key === 'include') {
                includeRole = true;
            }
            this.setTmplate2Group(includeRole).then(() => {
                this.popSetTemplateFlag = false;
            });
        },
        //
        async setTmplate2Group(includeRole) {
            let success = false;
            let groupTemplate = {
                SystemType: this.ReturnSysType(this.tabsActiveKey),
                GroupID: this.currentTrObj.id,
                TemplateID: this.newTemplateID,
                IncludeRole: includeRole
            };
            await setTemplate2GroupAPI(groupTemplate)
                .then((res) => {
                    if (res.code === 0) {
                        success = true;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误，请稍后再试');
                });
            return success;
        },
        async getAllGroupTemplate() {
            let success = false;
            this.templates = [];
            await getAllGroupTemplateAPI()
                .then((res) => {
                    this.templates.push(...res.data);
                    success = true;
                })
                .catch(() => {
                    this.$message.error('意外错误');
                });
            return success;
        },
        async getGroupTemplate() {
            let success = false;
            let sysType = this.ReturnSysType(this.tabsActiveKey);
            if (!this.currentTrObj) {
                return false;
            }
            let oid = this.currentTrObj.id;
            // row = this.$refs.templateTable.setCurrentRow()
            // this.currentGroup = sysType.toString() + oid.toString()
            this.currentTemplateID = 0;
            await getGroupTemplateAPI(sysType, oid)
                .then((res) => {
                    if (res.code === 0) {
                        if (res.data && res.data.id) {
                            this.currentTemplateID = res.data.id;
                        }
                        success = true;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.$message.error('意外错误，请稍后再试');
                });
            return success;
        }
    },
    created() {
        // this.platformRegistListAPIFn()
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
        .ant-tabs-card-content {
            height: 100%;
        }
    }
    .agency-down-tabs {
        height: 256px;
        position: relative;
        bottom: 0;
        left: 5px;
        margin-bottom: 5px;
        width: calc(100% - 10px);
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
        }
    }
}
.modal-form-input-scoped {
    .modal-form-input {
        .label-1 {
            width: 80px;
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
    padding: 0px 3px;
    float: left;
    height: 50%;
}
</style>
