<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="approvedStatusBtnFn(1)" type="primary" size="small"> 审核通过 </a-button>
                            <a-button @click="approvedStatusBtnFn(-1)" type="primary" size="small">
                                审核未通过
                            </a-button>
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
                            v-show="!hightSearchObj.isArrow"
                            v-model="searchTables.name"
                            placeholder="名称"
                            size="small"
                            enterButton
                            class="search"
                            @search="searchBtnFn"
                        />
                        <div @click="hightSearchIconFn" class="hight-search-text">
                            <!--<span>高级搜索</span>-->
                            <a-tooltip placement="left" title="高级搜索">
                                <a-icon :type="hightSearchObj.isArrow ? 'up-circle' : 'down-circle'" />
                            </a-tooltip>
                        </div>
                    </div>
                </a-col>
            </a-row>
            <a-row v-show="hightSearchObj.isArrow" class="hight-search-wrap">
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
                            <span class="label">注册人</span>
                            <a-input v-model="searchTables.registrant" type="text" placeholder="" />
                        </li>
                        <li>
                            <a-button @click="searchBtnFn" type="primary"> 搜索 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab切换区域开始 -->
        <div
            :class="hightSearchObj.isArrow ? 'top136' : 'top56'"
            class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
        >
            <!-- 表格头部 -->
            <a-tabs :activeKey="tabsActiveKey" @change="changeTabsFn" type="card">
                <a-tab-pane tab="经销商" style="padding: 8px" :key="systemTypes[2]">
                    <vxe-table
                        :data="dealersDatas"
                        ref="dealerTable"
                        size="small"
                        align="center"
                        :height="scrollYNumber"
                        border
                        highlight-current-row
                        resizable
                        show-header-overflow
                        show-overflow
                        @cell-dblclick="handleDbClick"
                        @cell-click="handleClick"
                        :loading="dealersPagination.loading"
                    >
                        <vxe-table-column field="name" title="名称" min-width="250"></vxe-table-column>
                        <vxe-table-column
                            field="social_credit_code"
                            title="统一社会信用代码"
                            min-width="200"
                        ></vxe-table-column>
                        <vxe-table-column field="registrant" title="注册人" min-width="100"></vxe-table-column>
                        <vxe-table-column
                            field="registrant_phone"
                            title="注册人电话"
                            min-width="150"
                        ></vxe-table-column>
                        <vxe-table-column field="register_time" title="注册时间" min-width="200"></vxe-table-column>
                        <vxe-table-column field="audit_status_text" title="审核状态" width="100">
                            <template v-slot="{ row }">
                                <span :class="{ 'red': row.audit_status === 2 }">{{ row.audit_status_text }}</span>
                            </template>
                        </vxe-table-column>
                    </vxe-table>
                    <vxe-pager
                        :loading="dealersPagination.loading"
                        :current-page="dealersPagination.current"
                        :page-size="dealersPagination.pageSize"
                        :total="dealersPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        :page-sizes="[10, 20, 30, 40]"
                        @page-change="handlePageChange"
                    >
                    </vxe-pager>
                </a-tab-pane>

                <a-tab-pane tab="医院" style="padding: 8px" :key="systemTypes[1]">
                    <vxe-table
                        :data="hospitalDatas"
                        ref="hospitalTable"
                        size="small"
                        align="center"
                        :height="scrollYNumber"
                        border
                        highlight-current-row
                        resizable
                        show-header-overflow
                        show-overflow
                        :radio-config="{ trigger: 'row' }"
                        @cell-dblclick="handleDbClick"
                        @cell-click="handleClick"
                        :loading="hospitalPagination.loading"
                    >
                        <vxe-table-column field="name" title="名称" min-width="250"></vxe-table-column>
                        <vxe-table-column field="alias" title="别名" min-width="200"></vxe-table-column>
                        <vxe-table-column
                            field="social_credit_code"
                            title="统一社会信用代码"
                            min-width="200"
                        ></vxe-table-column>
                        <vxe-table-column field="registrant" title="注册人" min-width="200"></vxe-table-column>
                        <vxe-table-column
                            field="registrant_phone"
                            title="注册人电话"
                            min-width="150"
                            align="center"
                        ></vxe-table-column>
                        <vxe-table-column field="register_time" title="注册时间" min-width="200"></vxe-table-column>
                        <vxe-table-column field="audit_status_text" title="审核状态" width="100">
                            <template v-slot="{ row }">
                                <span :class="{ 'red': row.audit_status === 2 }">{{ row.audit_status_text }}</span>
                            </template>
                        </vxe-table-column>
                    </vxe-table>
                    <vxe-pager
                        :loading="hospitalPagination.loading"
                        :current-page="hospitalPagination.current"
                        :page-size="hospitalPagination.pageSize"
                        :total="hospitalPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        :page-sizes="[10, 20, 30, 40]"
                        @page-change="handlePageChange"
                    >
                    </vxe-pager>
                </a-tab-pane>

                <a-tab-pane tab="厂商" style="padding: 8px" :key="systemTypes[3]">
                    <vxe-table
                        :data="vendorDatas"
                        ref="vendorTable"
                        size="small"
                        align="center"
                        :height="scrollYNumber"
                        border
                        highlight-current-row
                        resizable
                        show-header-overflow
                        show-overflow
                        :radio-config="{ trigger: 'row' }"
                        @cell-dblclick="handleDbClick"
                        @current-change="handleClick"
                        @cell-click="handleClick"
                        :loading="vendorPagination.loading"
                    >
                        <vxe-table-column field="name" title="名称" min-width="250"></vxe-table-column>
                        <vxe-table-column
                            field="social_credit_code"
                            min-width="200"
                            title="统一社会信用代码"
                            width="300"
                        ></vxe-table-column>
                        <vxe-table-column field="registrant" title="注册人" min-width="100"></vxe-table-column>
                        <vxe-table-column
                            field="registrant_phone"
                            title="注册人电话"
                            min-width="150"
                        ></vxe-table-column>
                        <vxe-table-column field="register_time" title="注册时间" min-width="200"></vxe-table-column>
                        <vxe-table-column field="audit_status_text" title="审核状态" width="100">
                            <template v-slot="{ row }">
                                <span :class="{ 'red': row.audit_status === 2 }">{{ row.audit_status_text }}</span>
                            </template>
                        </vxe-table-column>
                    </vxe-table>
                    <vxe-pager
                        :loading="vendorPagination.loading"
                        :current-page="vendorPagination.current"
                        :page-size="vendorPagination.pageSize"
                        :total="vendorPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        :page-sizes="[10, 20, 30, 40]"
                        @page-change="handlePageChange"
                    >
                    </vxe-pager>
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
            <!--表格区域-->
        </div>
        <!-- tab切换区域结束 -->

        <!-- 审核未通过开始 -->
        <a-modal
            v-model="auditFailedModalVisible"
            :centered="true"
            :maskClosable="false"
            okText="确定"
            cancelText="取消"
            @ok="handleSubmitAuditFailed"
            @cancel="handleCancelAuditFailed"
            class="modal-form-input-scoped audit-failed global-drag"
        >
            <div v-globalDrag="{ el: 'audit-failed' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    审核未通过
                </h6>
            </div>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label" style="width: 60px; justify-content: flex-start"
                            ><em class="em red">*</em>原因</span
                        >
                        <a-input
                            v-model="auditFailedReason"
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
        <!-- 审核未通过结束 -->

        <!-- 机构注册审核信息修改区域开始 -->
        <a-modal
            v-model="registAuditInfoModal.alert"
            :centered="true"
            :maskClosable="false"
            class="modal-form-input-scoped regist-audit global-drag"
        >
            <div v-globalDrag="{ el: 'regist-audit' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ registAuditInfoModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="registAuditInfoModalCancelFn"> 取消 </a-button>
                <a-button
                    key="submit"
                    type="primary"
                    :loading="registAuditInfoModal.loading"
                    @click="registAuditInfoModalOkFn"
                >
                    保存 </a-button
                ><!--:disabled="checkEditInfoFormObjComputed"-->
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>名称</span>
                        <a-input v-model="editInfoFormObj.name" placeholder="" class="input" />
                    </li>
                    <li v-show="tabsActiveKey === 1" class="item">
                        <!-- 只有是医院的tabs才显示别名 -->
                        <span class="label"><em class="em"></em>别名</span>
                        <a-input v-model="editInfoFormObj.alias" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>统一社会信用代码</span>
                        <a-input v-model="editInfoFormObj.social_credit_code" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>行政区划</span>
                        <a-cascader
                            :options="provinces"
                            :loadData="provinceLoads"
                            v-model="provinceDefaults"
                            @change="changeProvincesFn"
                            changeOnSelect
                            placeholder=""
                            :allowClear="false"
                            class="input"
                        /><!--:defaultValue="provinceDefaults"-->
                    </li>
                    <li class="item">
                        <div class="item-children">
                            <span class="label"><em class="em red">*</em>注册人</span>
                            <a-input v-model="editInfoFormObj.registrant" placeholder="" class="input" />
                        </div>
                        <div class="item-children">
                            <span class="label" style="width: auto"><em class="em red">*</em>注册人电话</span>
                            <a-input v-model="editInfoFormObj.registrant_phone" placeholder="" class="input" />
                        </div>
                    </li>
                    <li class="item">
                        <span class="label"><em class="em"></em>注册人邮箱</span>
                        <a-input v-model="editInfoFormObj.registrant_email" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em"></em>地址</span>
                        <a-input v-model="editInfoFormObj.address" placeholder="" class="input" />
                    </li>
                    <li class="item item-description">
                        <span class="label"><em class="em"></em>备注</span>
                        <a-textarea
                            v-model="editInfoFormObj.description"
                            :autosize="{ minRows: 4, maxRows: 6 }"
                            class="input"
                            placeholder=""
                        />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 机构注册审核信息修改区域结束 -->

        <!-- 机构注册审核解决冲突结束  -->
        <a-modal
            title="解决审核冲突数据"
            v-model="conflictModel.alert"
            :centered="true"
            :maskClosable="false"
            :width="900"
            class="modal-form-input-scoped register-conflict-resolve global-drag"
        >
            <div v-globalDrag="{ el: 'register-conflict-resolve' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ conflictModel.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="conflictModel.alert = false"> 取消 </a-button>
                <a-button key="submit" type="primary" :loading="conflictModel.loading" @click="conflictFormSubmit">
                    保存 </a-button
                ><!--:disabled="checkEditInfoFormObjComputed"-->
            </template>
            <div style="margin: -24px">
                <vxe-table
                    ref="xTable"
                    size="medium"
                    show-overflow
                    min-height
                    resizable
                    auto-resize
                    border
                    highlight-current-row
                    header-align="center"
                    :data="conflictTableData"
                >
                    <vxe-table-column
                        align="center"
                        field="property_desc"
                        title="属性名称"
                        width="40%"
                    ></vxe-table-column>
                    <vxe-table-column title="当前审核机构" width="30%">
                        <template v-slot="{ row: { property_name, new_value, newAreaName } }">
                            <label class="conflict-form-label">
                                <input
                                    :key="conflictModel.key"
                                    :name="property_name"
                                    v-model="conflictTableForm[property_name]"
                                    type="radio"
                                    :value="1"
                                />
                                <template v-if="property_name === 'administrative_division_sn'">
                                    <!-- 行政区划 -->
                                    {{ newAreaName }}
                                </template>
                                <template v-else>
                                    {{ new_value }}
                                </template>
                            </label>
                        </template>
                    </vxe-table-column>
                    <vxe-table-column title="系统已存在机构" width="30%">
                        <template v-slot="{ row: { property_name, old_value, oldAreaName } }">
                            <label class="conflict-form-label">
                                <input
                                    :key="conflictModel.key"
                                    :name="property_name"
                                    v-model="conflictTableForm[property_name]"
                                    type="radio"
                                    :value="0"
                                />
                                <template v-if="property_name === 'administrative_division_sn'">
                                    <!-- 行政区划 -->
                                    {{ oldAreaName }}
                                </template>
                                <template v-else>
                                    {{ old_value }}
                                </template>
                            </label>
                        </template>
                    </vxe-table-column>
                </vxe-table>
                <p class="conflict-form-info" v-if="this.conflictTableForm.account">
                    <span>管理员账号：</span>{{ this.conflictTableForm.account }}
                </p>
            </div>
        </a-modal>
        <!-- 机构注册审核解决冲突结束 -->
    </div>
</template>

<script>
import {
    provinceAreaAPI,
    cityAreaAPI,
    countyAreaAPI,
    listAreaAPI,
    platformRegistListAPI,
    platformRegistDeleteAPI,
    platformRegistEditAPI,
    platformRegistInfoAPI,
    platformRegistPassAPI,
    platformRegistNopassAPI,
    platformRegisterConflictAPI
} from '@/service/pageAjax';

export default {
    name: 'RegistAudit',
    data() {
        return {
            dealersDatas: [], // 经销商主体数据
            hospitalDatas: [], // 医院主体数据
            vendorDatas: [], // 厂商主体数据
            // 经销商 - 表格分页
            dealersPagination: {
                loading: true,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            // 医院 - 表格分页
            hospitalPagination: {
                loading: true,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            // 厂商 - 表格分页
            vendorPagination: {
                loading: true,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            systemTypes: [0, 1, 2, 3], // 0 平台、 1医院 、 2 经销商、 3 厂商
            auditStatus: ['未审核', '通过', '未通过'], // 表格的审核状态： 0-未审核 1-通过 2-未通过
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            recordCurrentTrObj: {
                hospitals: {},
                dealers: {},
                vendors: {}
            }, // 表格选中的当前行数据是否记住旧值
            tabsActiveKey: 2, // 表格头部的tab选项卡切换的 key 值 【默认值为： 2】
            searchTables: {
                registrant: '',
                social_credit_code: '',
                name: ''
            }, // 表格的搜索并向后端请求字段
            // 审核状态的下拉颜色高亮显示、箭头的朝向改变
            menuItemData: {
                class: true,
                arrow: false
            },
            // 修改弹框的配置
            registAuditInfoModal: {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            },
            // 省 - 市 - 区
            provinces: [],
            provinceDefaultsFlag: true, // 是否加载默认数据的标识
            provinceDefaults: [], // 北京：[110000, 110100, 110101]
            provincesObj: {
                pIndex: -1,
                cIndex: -1
            },
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
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
            editInfoFormOldObj: {}, // 修改信息的模态框值 - 后端返回回来的旧值
            editInfoFormNewObj: {}, // 修改信息的模态框值 - 前端根据页面修改的值放进去 【没有修改的不要向后端传递】
            activatedTable: 'dealerTable', // 当前激活使用的表格
            confirmTableLoadedTimer: 0, // 用于检查表格是否激活的定时器
            conflictModel: {
                // 解决机构审核冲突弹框配置
                title: `${this.$route.meta.title} - 解决机构注册数据冲突`,
                alert: false,
                loading: false,
                key: 0 // 防止解决机构审核冲突table表单中单选框input被复用 选中状态不能被更新
            },
            conflictTableData: [], // 解决机构审核冲突table
            conflictTableForm: {}, // 解决机构审核冲突table表单
            auditFailedModalVisible: false, // 审核未通过modal显隐标志
            auditFailedReason: '' // 审核未通过原因
        };
    },
    methods: {
        init() {
            const { system_type: systemType, name } = this.$route.query;
            if (systemType && name) {
                this.tabsActiveKey = parseInt(systemType);
                this.searchTables.name = this.$route.query.name;
                this.changeTabsFn(this.tabsActiveKey);
            }
            this.systemTypes.forEach((item) => {
                if (parseFloat(item) === 0) return;
                this.platformRegistListAPIFn(item);
            });
        },
        setReferredTable() {
            this.confirmTableLoadedTimer = setInterval(() => {
                if (this.$refs[this.activatedTable]) {
                    clearInterval(this.confirmTableLoadedTimer);
                    let useTable = this.$refs[this.activatedTable];
                    useTable.resetAll();
                    useTable.setCurrentRow(this.currentTrObj);
                }
            }, 1);
        },
        // 获取全部省份列表
        async provinceAreaAPIFn() {
            await provinceAreaAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const datas = res.province.map((item) => {
                        item.value = item.administrative_division_sn;
                        item.label = item.name;
                        item.isLeaf = item.is_leaf === 1; // is_leaf 1：没有下一级叶子节点   0：有
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
                        item.isLeaf = item.is_leaf === 1; // is_leaf 1：没有下一级叶子节点   0：有
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
                        // item.isLeaf = item.is_leaf === 1 // is_leaf 1：没有下一级叶子节点   0：有
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
        // 省市区数据选择完成后的回调
        changeProvincesFn(value) {
            this.editInfoFormObj.administrative_division_sn = value[value.length - 1];
        },
        // 根据注册机构类型【获取机构列表】
        async platformRegistListAPIFn(systemType = this.tabsActiveKey, initFlag) {
            // 判断参数有没有， 没有直接不要传给后端
            let params = {
                // 排序
                orderByColumn: 'register_time',
                isAsc: 'desc'
            };

            // 判断是否是高级搜索
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                if (this.searchTables.social_credit_code) {
                    params.social_credit_code = this.searchTables.social_credit_code;
                }

                if (this.searchTables.registrant) {
                    params.registrant = this.searchTables.registrant;
                }
            }

            if (this.searchTables.name) {
                params.name = this.searchTables.name;
            }

            switch (parseFloat(systemType)) {
                case 0:
                    break;
                case 1:
                    params.pageNum = this.hospitalPagination.current;
                    params.pageSize = this.hospitalPagination.pageSize;
                    break;
                case 2:
                    params.pageNum = this.dealersPagination.current;
                    params.pageSize = this.dealersPagination.pageSize;
                    break;
                case 3:
                    params.pageNum = this.vendorPagination.current;
                    params.pageSize = this.vendorPagination.pageSize;
                    break;
            }
            await platformRegistListAPI(systemType, {
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res,
                        rowsNew = rows.map((item) => {
                            item.key = item.not_audit_organization_inner_sn; // id
                            item.audit_status_text = this.auditStatus[parseFloat(item.audit_status)];
                            item.activeClass = '';
                            return item;
                        });

                    switch (parseFloat(systemType)) {
                        case 0:
                            break;
                        case 1:
                            this.hospitalDatas = rowsNew;
                            this.hospitalPagination = {
                                ...this.hospitalPagination,
                                total,
                                loading: false
                            };
                            this.$refs['hospitalTable'] && this.$refs['hospitalTable'].refreshColumn(0);
                            break;
                        case 2:
                            this.dealersDatas = rowsNew;
                            if (this.dealersDatas.length) {
                                this.currentTrObj = this.dealersDatas[0];
                                this.activatedTable = 'dealerTable';
                                this.setReferredTable();
                            }
                            this.dealersPagination = {
                                ...this.dealersPagination,
                                total,
                                loading: false
                            };
                            break;
                        case 3:
                            this.vendorDatas = rowsNew;
                            this.vendorPagination = {
                                ...this.vendorPagination,
                                total,
                                loading: false
                            };
                            break;
                    }
                    if (initFlag && initFlag === 'initFlag') {
                        // 刷新按钮触发
                        this.changeTabsFn(systemType);
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据注册机构id【删除】注册机构
        async platformRegistDeleteAPIFn(id) {
            await platformRegistDeleteAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // this.$message.success(`${this.currentTrObj.name} - 删除成功`)
                    this.operationsOneTablesTrFn('remove');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据注册机构id【修改】注册机构基本信息
        async platformRegistEditAPIFn(id, params) {
            await platformRegistEditAPI(id, params).then((res) => {
                this.registAuditInfoModal.loading = false;
                if (parseFloat(res.code) === 0) {
                    this.editInfoFormNewObj = {};
                    this.registAuditInfoModal.alert = false;
                    // this.$message.success(`${this.currentTrObj.name} - 修改成功`)
                    this.operationsOneTablesTrFn('edit');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据注册机构id【查询】机构信息
        async platformRegistInfoAPIFn(id) {
            await platformRegistInfoAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    if (info.administrative_division_sn) {
                        this.listAreaAPIFn(info.administrative_division_sn);
                    } else {
                        this.provinceAreaAPIFn();
                    }
                    this.editInfoFormObj = { ...info };
                    this.editInfoFormOldObj = { ...info }; // 为了比对值的临时存储
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据注册机构id【审核通过】机构信息
        async platformRegistPassAPIFn(id) {
            await platformRegistPassAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // this.$message.success(`${this.currentTrObj.name} - 审核通过`)
                    this.operationsOneTablesTrFn('remove');
                } else {
                    if (res.info) {
                        // 注册机构审核,有问题返回可能冲突的数据
                        this.$nextTick(() => {
                            this.$message.warning('系统已存在当前审核机构信息，请解决冲突后提交');
                            this.conflictModel.alert = true;
                            this.$Utils.globalDragCenterFn('register-conflict-resolve');
                            this._setConflictTableData(res.info);
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                }
            });
        },
        // 根据注册机构id【审核不通过】机构信息
        async platformRegistNopassAPIFn(id) {
            await platformRegistNopassAPI(id, { reason: this.auditFailedReason }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // this.$message.success(`${this.currentTrObj.name} - 审核未通过`)
                    this.operationsOneTablesTrFn('nopass');
                    this.auditFailedModalVisible = false;
                    this.auditFailedReason = '';
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 前端修改或删除表格的任意一条数据函数
        operationsOneTablesTrFn(flag) {
            // flag: 'remove' 删除/审核通过   'edit' 修改   'add' 增加  'nopass' 审核未通过
            switch (this.tabsActiveKey) {
                case 0:
                    break;
                case 1:
                    for (let i = 0; i < this.hospitalDatas.length; i++) {
                        const item = this.hospitalDatas[i],
                            noPassStatus = 2; // 未通过
                        if (
                            item.not_audit_organization_inner_sn === this.currentTrObj.not_audit_organization_inner_sn
                        ) {
                            switch (flag) {
                                case 'remove':
                                    this.hospitalDatas.splice(i, 1);
                                    if (this.hospitalDatas.length) {
                                        this.currentTrObj = this.hospitalDatas[0];
                                    } else {
                                        this.currentTrObj = {};
                                    }
                                    this.platformRegistListAPIFn(this.tabsActiveKey);
                                    break;
                                case 'edit':
                                    this.hospitalDatas.splice(i, 1, {
                                        ...this.editInfoFormObj,
                                        key: this.editInfoFormObj.not_audit_organization_inner_sn,
                                        audit_status_text:
                                            this.auditStatus[parseFloat(this.editInfoFormObj.audit_status)]
                                    });
                                    this.currentTrObj = this.hospitalDatas[i];
                                    this.recordRowsFn(this.hospitalDatas[i]);
                                    this.setReferredTable();
                                    break;
                                case 'nopass':
                                    this.hospitalDatas.splice(i, 1, {
                                        ...item,
                                        key: item.not_audit_organization_inner_sn,
                                        audit_status: noPassStatus,
                                        audit_status_text: this.auditStatus[noPassStatus]
                                    });
                                    break;
                                case 'add':
                                    break;
                            }
                            break;
                        }
                    }
                    break;
                case 2:
                    for (let i = 0; i < this.dealersDatas.length; i++) {
                        const item = this.dealersDatas[i],
                            noPassStatus = 2; // 未通过
                        if (
                            item.not_audit_organization_inner_sn === this.currentTrObj.not_audit_organization_inner_sn
                        ) {
                            switch (flag) {
                                case 'remove':
                                    this.dealersDatas.splice(i, 1);
                                    if (this.dealersDatas.length) {
                                        this.currentTrObj = this.dealersDatas[0];
                                    } else {
                                        this.currentTrObj = {};
                                    }
                                    this.platformRegistListAPIFn(this.tabsActiveKey);
                                    break;
                                case 'edit':
                                    this.dealersDatas.splice(i, 1, {
                                        ...this.editInfoFormObj,
                                        key: this.editInfoFormObj.not_audit_organization_inner_sn,
                                        audit_status_text:
                                            this.auditStatus[parseFloat(this.editInfoFormObj.audit_status)]
                                    });
                                    this.currentTrObj = this.dealersDatas[i];
                                    this.recordRowsFn(this.dealersDatas[i]);
                                    this.setReferredTable();
                                    break;
                                case 'nopass':
                                    this.dealersDatas.splice(i, 1, {
                                        ...item,
                                        key: item.not_audit_organization_inner_sn,
                                        audit_status: noPassStatus,
                                        audit_status_text: this.auditStatus[noPassStatus]
                                    });
                                    break;
                                case 'add':
                                    break;
                            }
                            break;
                        }
                    }
                    break;
                case 3:
                    for (let i = 0; i < this.vendorDatas.length; i++) {
                        const item = this.vendorDatas[i],
                            noPassStatus = 2; // 未通过
                        if (
                            item.not_audit_organization_inner_sn === this.currentTrObj.not_audit_organization_inner_sn
                        ) {
                            switch (flag) {
                                case 'remove':
                                    this.vendorDatas.splice(i, 1);
                                    if (this.vendorDatas.length) {
                                        this.currentTrObj = this.vendorDatas[0];
                                    } else {
                                        this.currentTrObj = {};
                                    }
                                    this.platformRegistListAPIFn(this.tabsActiveKey);
                                    break;
                                case 'edit':
                                    this.vendorDatas.splice(i, 1, {
                                        ...this.editInfoFormObj,
                                        key: this.editInfoFormObj.not_audit_organization_inner_sn,
                                        audit_status_text:
                                            this.auditStatus[parseFloat(this.editInfoFormObj.audit_status)]
                                    });
                                    this.currentTrObj = this.vendorDatas[i];
                                    this.recordRowsFn(this.vendorDatas[i]);
                                    this.setReferredTable();
                                    break;
                                case 'nopass':
                                    this.vendorDatas.splice(i, 1, {
                                        ...item,
                                        key: item.not_audit_organization_inner_sn,
                                        audit_status: noPassStatus,
                                        audit_status_text: this.auditStatus[noPassStatus]
                                    });
                                    break;
                                case 'add':
                                    break;
                            }
                            break;
                        }
                    }
                    break;
            }
        },
        // 表格重新刷新数据的初始化函数 01
        initRfreshTablesFn() {
            if (!this.checkSearchInfoFormMsgFn()) return;
            this.initTablesPaginationFn();
            this.platformRegistListAPIFn(this.tabsActiveKey);
        },
        // 表格重新刷新数据的初始化函数 02 【初始化分页data】
        initTablesPaginationFn() {
            this.currentTrObj = {};
            switch (parseInt(this.tabsActiveKey)) {
                case 0:
                    break;
                case 1:
                    this.hospitalPagination.current = 1;
                    this.hospitalPagination.loading = true;
                    break;
                case 2:
                    this.dealersPagination.current = 1;
                    this.dealersPagination.loading = true;
                    break;
                case 3:
                    this.vendorPagination.current = 1;
                    this.vendorPagination.loading = true;
                    break;
            }
        },
        // 页码改变的回调函数
        changePaginationFn(page) {
            switch (parseInt(this.tabsActiveKey)) {
                case 0:
                    break;
                case 1:
                    this.hospitalPagination = {
                        ...page,
                        loading: true,
                        pageNum: page.current
                    };
                    break;
                case 2:
                    this.dealersPagination = {
                        ...page,
                        loading: true,
                        pageNum: page.current
                    };
                    break;
                case 3:
                    this.vendorPagination = {
                        ...page,
                        loading: true,
                        pageNum: page.current
                    };
                    break;
            }
            this.platformRegistListAPIFn(this.tabsActiveKey, 'initFlag');
        },
        getCurrentStyle(current) {
            const style = {};
            if (current.date() === 1) {
                style.border = '1px solid #1890ff';
                style.borderRadius = '50%';
            }
            return style;
        },

        // 点击表格的每一行时记录点击的哪一个列表哪一个数据
        recordRowsFn(record) {
            switch (parseFloat(this.tabsActiveKey)) {
                case 0:
                    break;
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
        // 待审核 / 审核未通过的菜单显示状态改变时调用，参数为 visible 【经销商】
        visibleChangeFn(val) {
            this.menuItemData.arrow = val;
        },
        // 表格的tab选项卡切换状态
        changeTabsFn(activeKey) {
            this.tabsActiveKey = parseFloat(activeKey);
            switch (this.tabsActiveKey) {
                case 0:
                    break;
                case 1:
                    if (this.hospitalDatas.length) {
                        this.currentTrObj =
                            Object.keys(this.recordCurrentTrObj.hospitals).length > 0
                                ? this.recordCurrentTrObj.hospitals
                                : this.hospitalDatas[0];
                        this.activatedTable = 'hospitalTable';
                        this.setReferredTable();
                    } else {
                        this.currentTrObj = {};
                    }
                    break;
                case 2:
                    if (this.dealersDatas.length) {
                        this.currentTrObj =
                            Object.keys(this.recordCurrentTrObj.dealers).length > 0
                                ? this.recordCurrentTrObj.dealers
                                : this.dealersDatas[0];
                        this.activatedTable = 'dealerTable';
                        this.setReferredTable();
                    } else {
                        this.currentTrObj = {};
                    }
                    break;
                case 3:
                    if (this.vendorDatas.length) {
                        this.currentTrObj =
                            Object.keys(this.recordCurrentTrObj.vendors).length > 0
                                ? this.recordCurrentTrObj.vendors
                                : this.vendorDatas[0];
                        this.activatedTable = 'vendorTable';
                        this.setReferredTable();
                    } else {
                        this.currentTrObj = {};
                    }
                    break;
            }
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

            if (this.hightSearchObj.isArrow && this.$Utils.checkInputValFn(this.searchTables.social_credit_code)) {
                this.$warning({
                    title: '操作提示',
                    content: `统一社会信用代码${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            if (this.hightSearchObj.isArrow && this.$Utils.checkInputValFn(this.searchTables.registrant)) {
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
        // 检测修改的表单信息校验
        checkEditInfoFormMsgFn() {
            const regExpFn = this.$Utils.regExpFn(),
                // mobilePhone = regExpFn.mobilePhone,
                email = regExpFn.email,
                socialCreditCode = regExpFn.socialCreditCode;

            this.editInfoFormObj.name = this.$Utils.trim(this.editInfoFormObj.name);
            this.editInfoFormObj.social_credit_code = this.$Utils.trim(this.editInfoFormObj.social_credit_code);
            this.editInfoFormObj.registrant = this.$Utils.trim(this.editInfoFormObj.registrant);
            this.editInfoFormObj.registrant_phone = this.$Utils.trim(this.editInfoFormObj.registrant_phone);
            this.editInfoFormObj.registrant_email = this.$Utils.trim(this.editInfoFormObj.registrant_email);

            // 名称
            if (!this.editInfoFormObj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
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
            // 统一社会信用代码 01
            if (!this.editInfoFormObj.social_credit_code) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入统一社会信用代码',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 统一社会信用代码 02
            if (this.editInfoFormObj.social_credit_code.length !== 18) {
                this.$warning({
                    title: '操作提示',
                    content: '统一社会信用代码有效长度是18位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 统一社会信用代码 03
            if (!socialCreditCode.test(this.editInfoFormObj.social_credit_code)) {
                this.$warning({
                    title: '操作提示',
                    content: '统一社会信用代码格式为字母和数字组合的18位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 行政区域
            if (!this.editInfoFormObj.administrative_division_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择行政区域',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 注册人
            if (!this.editInfoFormObj.registrant) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入注册人',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 注册人电话 01
            // if (!this.editInfoFormObj.registrant_phone) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '请输入注册人电话',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     return false
            // }

            // // 注册人电话 02
            // if (this.editInfoFormObj.registrant_phone.length > 11) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '注册人电话有效长度是11位',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     return false
            // }

            // 注册人电话 03
            // if (!mobilePhone.test(this.editInfoFormObj.registrant_phone)) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '注册人电话格式输入有误',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     return false
            // }

            // 注册人邮箱 01
            if (this.editInfoFormObj.registrant_email && !email.test(this.editInfoFormObj.registrant_email)) {
                this.$warning({
                    title: '操作提示',
                    content: '注册人邮箱格式输入有误',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 检测并设置修改过的修改信息内容字段
        checkEditInfoFormObjFn() {
            let editInfoFlag = true;
            this.editInfoFormNewObj = {};
            for (let server in this.editInfoFormObj) {
                let serverItem = this.editInfoFormObj[server];
                for (let old in this.editInfoFormOldObj) {
                    let oldItem = this.editInfoFormOldObj[old];
                    if (server === old && serverItem !== oldItem) {
                        this.editInfoFormNewObj = {
                            ...this.editInfoFormNewObj,
                            [server]: serverItem, // 新值
                            [server + '_old']: oldItem // 旧值
                        };
                        editInfoFlag = false;
                    }
                }
            }
            return editInfoFlag;
        },
        // 模态框操作
        registAuditInfoModalOkFn() {
            if (!this.checkEditInfoFormMsgFn()) return; // 前端格式校验

            if (this.checkEditInfoFormObjFn()) {
                // 后端传值校验
                this.registAuditInfoModalCancelFn();
                return;
            }

            this.registAuditInfoModal.loading = true;
            this.platformRegistEditAPIFn(this.currentTrObj.not_audit_organization_inner_sn, this.editInfoFormNewObj);
        },
        registAuditInfoModalCancelFn() {
            this.registAuditInfoModal.alert = false;
            this.editInfoFormNewObj = {};
        },
        /* 按钮区域的操作 */
        checkCurrentTrDataFn() {
            // 检测表格是否有选中的的数据
            if (Object.keys(this.currentTrObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        checkAuditStatusFn() {
            // 检测是否是未审核状态的单子的机构
            if (parseInt(this.currentTrObj.audit_status) !== 0) {
                this.$warning({
                    title: '操作提示',
                    content: `只能操作【${this.auditStatus[0]}】状态的机构。`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        approvedStatusBtnFn(flag) {
            if (!this.checkCurrentTrDataFn()) return;
            if (!this.checkAuditStatusFn()) return;
            const _this = this;
            if (flag > 0) {
                _this.$confirm({
                    title: '审核通过',
                    content: '确认要执行该操作吗？',
                    centered: true,
                    maskClosable: false,
                    onOk() {
                        _this.platformRegistPassAPIFn(_this.currentTrObj.not_audit_organization_inner_sn);
                        // return new Promise((resolve, reject) => {
                        //     setTimeout(Math.random() > 0.5 ? resolve : reject, 1000)
                        // }).catch(() => {})
                    }
                });
            } else {
                this.auditFailedModalVisible = true;
                this.$Utils.globalDragCenterFn('audit-failed');
                // _this.$confirm({
                //     title: '审核未通过',
                //     content: '确认要执行该操作吗？',
                //     maskClosable: false,
                //     centered: true,
                //     onOk () {
                //         _this.platformRegistNopassAPIFn(_this.currentTrObj.not_audit_organization_inner_sn)
                //         return new Promise((resolve, reject) => {
                //             setTimeout(Math.random() > 0.5 ? resolve : reject, 1000)
                //         }).catch(() => {})
                //     }
                // })
            }
        },
        handleSubmitAuditFailed() {
            if (!this.auditFailedReason.trim()) {
                this.$warning({
                    title: '操作提示',
                    content: `未通过原因不能为空`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            this.platformRegistNopassAPIFn(this.currentTrObj.not_audit_organization_inner_sn);
        },
        handleCancelAuditFailed() {
            this.auditFailedModalVisible = false;
            this.auditFailedReason = '';
        },
        modifyBtnFn() {
            if (!this.checkCurrentTrDataFn()) return;
            this.platformRegistInfoAPIFn(this.currentTrObj.not_audit_organization_inner_sn);
            this.editInfoFormObj.administrative_division_sn = this.currentTrObj.administrative_division_sn;
            this.provinceDefaultsFlag = true;
            this.provinceDefaults = [];
            this.registAuditInfoModal.alert = true;
            this.$Utils.globalDragCenterFn('regist-audit');
        },
        removeBtnFn() {
            if (!this.checkCurrentTrDataFn()) return;
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认要执行该操作吗？',
                maskClosable: false,
                centered: true,
                onOk() {
                    _this.platformRegistDeleteAPIFn(_this.currentTrObj.not_audit_organization_inner_sn);
                    return new Promise((resolve, reject) => {
                        setTimeout(Math.random() > 0.5 ? resolve : reject, 1000);
                    }).catch(() => {});
                }
            });
        },
        // 搜索按钮操作
        searchBtnFn() {
            this.initRfreshTablesFn();
        },
        // 点击高级搜索的小图标的事件
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        // 刷新按钮
        initTablesFn() {
            this.tabsActiveKey = parseFloat(this.tabsActiveKey);
            this.initTablesPaginationFn();
            switch (this.tabsActiveKey) {
                case 0:
                    break;
                case 1:
                    this.recordCurrentTrObj.hospitals = {};
                    break;
                case 2:
                    this.recordCurrentTrObj.dealers = {};
                    break;
                case 3:
                    this.recordCurrentTrObj.vendors = {};
                    break;
            }
            this.platformRegistListAPIFn(this.tabsActiveKey, 'initFlag');
        },
        // 表格的高亮样式显示
        rowClassFn(record) {
            if (record.key === this.currentTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        // 处理双击事件
        handleDbClick(cellData) {
            let record = cellData.row;
            this.currentTrObj = record;
            this.recordRowsFn(record);
            this.modifyBtnFn();
        },
        // 处理单击事件
        handleClick(cellData) {
            let record = cellData.row;
            this.currentTrObj = record;
            this.recordRowsFn(record);
        },
        // 处理分页
        handlePageChange({ currentPage, pageSize }) {
            this.dealersPagination.current = currentPage;
            this.dealersPagination.pageSize = pageSize;
            this.changePaginationFn(this.dealersPagination);
        },
        // 设置机构审核冲突解决表格表单数据
        _setConflictTableData(data = {}) {
            this._resetConflictForm();
            const { list, orgId, account } = data;
            let tempArr = [];
            list.forEach(async (item) => {
                let newVal = item.new_value,
                    oldVal = item.old_value,
                    name = item.property_name;
                if (newVal || oldVal) {
                    if (name === 'administrative_division_sn') {
                        let newAreaName = newVal ? await this._getAreaName(newVal) : '';
                        let oldAreaName = oldVal ? await this._getAreaName(oldVal) : '';
                        item.newAreaName = newAreaName;
                        item.oldAreaName = oldAreaName;
                    }
                    this.conflictTableForm[item.property_name] = 0; // 默认选中系统已存在机构
                    tempArr.push(item);
                }
            });
            this.conflictTableData = tempArr;
            this.conflictTableForm.orgId = orgId;
            this.conflictTableForm.account = account; // 管理员账号
        },
        // 设置机构审核冲突解决提交
        async conflictFormSubmit() {
            this.conflictModel.loading = true;
            let params = {};
            this.conflictTableData.forEach((item) => {
                const name = item.property_name;
                if (this.conflictTableForm[name]) {
                    params[name] = item.new_value;
                } else {
                    params[name] = item.old_value;
                }
            });
            params.id = this.conflictTableForm.orgId;
            const path = this.currentTrObj.not_audit_organization_inner_sn;
            await platformRegisterConflictAPI(path, params).then((res) => {
                this.conflictModel.loading = false;
                if (parseFloat(res.code) === 0) {
                    this.conflictModel.alert = false;
                    this.operationsOneTablesTrFn('remove');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据行政区划ID获取行政区划名称
        _getAreaName(id) {
            return new Promise((resolve) => {
                listAreaAPI(id).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const areaObj = res.area,
                            countyName = areaObj.name,
                            cityName = areaObj.parent ? areaObj.parent.name : '',
                            provinceName = areaObj.parent && areaObj.parent.parent ? areaObj.parent.parent.name : '';
                        resolve(`${provinceName}/${cityName}/${countyName}`);
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            });
        },
        // 重置table表单
        _resetConflictForm() {
            this.conflictModel.key += 1;
            this.conflictTableForm = {};
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
            let topHeight = this.hightSearchObj.isArrow ? 281 : 221;
            return clientHeight - topHeight + 11;
        }
    },
    watch: {
        '$route'() {
            this.init();
        }
    },
    created() {
        this.init();
    }
};
</script>

<style lang="less" scoped>
.conflict-form-label {
    display: flex;
    align-items: center;
    cursor: pointer;

    input {
        margin-right: 8px;
    }
}
.conflict-form-info {
    padding: 20px 20px 0;

    span {
        font-weight: 600;
    }
}
</style>
