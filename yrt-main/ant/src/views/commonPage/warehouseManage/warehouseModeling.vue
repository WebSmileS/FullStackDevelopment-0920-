<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="24">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="groupBtnFn('addChild', 'architectureBtn')" type="primary" size="small">
                                新增下级模型
                            </a-button>
                            <a-button
                                @click="groupBtnFn('addSameLevel', 'architectureBtn')"
                                type="primary"
                                size="small"
                            >
                                新增同级模型
                            </a-button>
                        </li>
                        <li>
                            <a-button @click="groupBtnFn('edit', 'architectureBtn')" type="primary" size="small">
                                修改
                            </a-button>
                            <a-button @click="groupBtnFn('remove', 'architectureBtn')" type="danger" size="small">
                                删除
                            </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab切换区域开始 -->
        <div
            class="tab-table-wrap tab-table-overflowy"
            :class="tabsActiveKey === 'list' ? 'cur-overflow-hidden' : 'cur-overflow-y'"
        >
            <!-- 表格头部 -->
            <a-tabs @change="changeTabsFn" type="card">
                <a-tab-pane tab="列表" :key="systemTypes[0]" style="padding: 8px">
                    <a-table
                        :columns="columns"
                        :dataSource="datas"
                        :pagination="false"
                        bordered
                        :loading="warehouseModelingLoading"
                        :customRow="customRowFn"
                        :expandedRowKeys="expandedRows"
                        :scroll="{ y: scrollYNumber }"
                        :rowClassName="rowClassFn"
                        :defaultExpandAllRows="true"
                        @expand="expandFn"
                    >
                        <!--状态重写-->
                        <div
                            slot="status_text"
                            slot-scope="text, record"
                            :class="{
                                'red': record.status !== 1
                            }"
                            >{{ text }}</div
                        >
                    </a-table>
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

        <!-- 信息修改区域开始 -->
        <a-modal
            v-model="architectureInfoModal.alert"
            :centered="true"
            :maskClosable="false"
            class="modal-form-input-scoped modal-form-input-scoped-8 global-drag architecture-category-modal"
        >
            <div v-globalDrag="{ el: 'architecture-category-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ architectureInfoModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="architectureInfoModalCancelFn('architectureCategoryModal')">
                    取消
                </a-button>
                <a-button
                    key="submit"
                    type="primary"
                    :loading="architectureInfoModal.loading"
                    @click="architectureInfoModalSaveFn('architectureCategoryModal')"
                >
                    保存
                </a-button>
            </template>
            <div class="modal-form-input">
                <a-tabs v-model="tabsArchitectureInfoModalKey" type="card">
                    <a-tab-pane tab="基本信息" :key="tabsArchitectureList[0]">
                        <ul>
                            <li v-if="isShowSuperiorModel || isAddChildModel" class="item">
                                <span class="label">上级模型</span>
                                <a-cascader
                                    :disabled="typeIsCur === '0'"
                                    :options="parentDepartments"
                                    :allowClear="false"
                                    v-model="parentDepartmentDefaults"
                                    :loadData="loadParentDepartments"
                                    changeOnSelect
                                    @change="changeParentDepartmentsFn"
                                    placeholder=""
                                    class="input"
                                    style="z-index: 3 !important"
                                />
                            </li>
                            <li class="item">
                                <div class="item-children">
                                    <span class="label"><em class="em red">*</em>模型类别</span>
                                    <a-tree-select
                                        :showSearch="false"
                                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                        :treeData="typeIs.filter((item) => item.key === 0 || item.key === 6)"
                                        treeDefaultExpandAll
                                        v-model="typeIsCur"
                                        @change="getTypeIsFn"
                                        class="input"
                                    >
                                    </a-tree-select>
                                </div>
                                <div v-show="btnName === 'edit'" class="item-children">
                                    <span class="label" style="width: auto"><em class="em"></em>有效</span>
                                    <a-checkbox-group v-model="optionStatusCur" @change="getStatusFn">
                                        <a-checkbox
                                            v-for="optionStatusItem in optionStatus"
                                            :key="optionStatusItem.key"
                                            :value="optionStatusItem.value"
                                        ></a-checkbox>
                                    </a-checkbox-group>
                                </div>
                            </li>
                            <li class="item">
                                <div class="item-children">
                                    <span class="label"><em class="em red">*</em>名称</span>
                                    <a-input v-model="editInfoFormObj.name" placeholder="" class="input" />
                                </div>
                                <div class="item-children">
                                    <span class="label" style="width: auto"><em class="em"></em>编号</span>
                                    <a-input
                                        :disabled="!!editInfoFormObj.barcode"
                                        v-model="editInfoFormObj.warehouse_model_sn"
                                        @blur="blurCheckRepeatFn"
                                        placeholder=""
                                        class="input"
                                    />
                                </div>
                            </li>
                            <li v-show="typeIsCur === '0' || typeIsCur === '7'" class="item">
                                <span class="label">专属部门</span>
                                <a-tree-select
                                    treeNodeFilterProp="title"
                                    :showSearch="true"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    :treeData="superiorDepartments"
                                    :allowClear="false"
                                    treeDefaultExpandAll
                                    multiple
                                    v-model="departments"
                                    @change="changeSuperiorDepartmentsFn"
                                    style="width: 86%"
                                >
                                </a-tree-select>
                            </li>
                            <li v-show="typeIsCur === '0'" class="item">
                                <div class="item-children">
                                    <span class="label">仓库类别</span>
                                    <div class="warehouse-category">
                                        <a-tree-select
                                            :showSearch="false"
                                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                            :treeData="warehouseCategory"
                                            treeDefaultExpandAll
                                            v-model="editInfoFormObj.warehouse_type_inner_sn"
                                            @change="getWarehouseCategoryFn"
                                            allowClear
                                            class="input"
                                            style="width: 92px"
                                        >
                                        </a-tree-select>
                                        <a-tooltip class="tooltip" placement="right" title="新增仓库类别">
                                            <a-button icon="edit" @click="warehouseCategoryBtnFn"></a-button>
                                        </a-tooltip>
                                    </div>
                                </div>
                                <div class="item-children">
                                    <span class="label" style="width: auto"><em class="em"></em>电话</span>
                                    <a-input v-model="editInfoFormObj.phone" placeholder="" class="input" />
                                </div>
                            </li>
                            <li v-show="typeIsCur === '0'" class="item">
                                <span class="label">行政区划</span>
                                <a-cascader
                                    :options="provinces"
                                    :loadData="provinceLoads"
                                    v-model="provinceDefaults"
                                    @change="changeProvincesFn"
                                    @click="clickProvincesFn"
                                    changeOnSelect
                                    placeholder=""
                                    allowClear
                                    class="input"
                                />
                            </li>
                            <li v-show="typeIsCur === '0'" class="item">
                                <span class="label">地址</span>
                                <a-input v-model="editInfoFormObj.address" placeholder="" class="input" />
                            </li>
                            <li v-show="typeIsCur === '2'" class="item">
                                <div class="item-children">
                                    <span class="label">面积</span>
                                    <a-input v-model="editInfoFormObj.proportion" placeholder="" class="input" />
                                    <span class="unit" style="right: -35px">平方米</span>
                                </div>
                            </li>
                            <li v-show="typeIsCur === '5'" class="item">
                                <span class="label">通道号</span>
                                <a-input v-model="editInfoFormObj.aisle_sn" placeholder="" class="input" />
                            </li>
                            <li class="item item-description">
                                <span class="label">备注</span>
                                <a-textarea
                                    v-model="editInfoFormObj.description"
                                    :autosize="{ minRows: 2, maxRows: 2 }"
                                    class="input"
                                    placeholder=""
                                />
                            </li>
                            <li class="item barcode-img-wrap"
                                ><!--v-show="typeIsCur === '3' || typeIsCur === '6' || typeIsCur === '8'"-->
                                <span class="label"></span>
                                <div class="input">
                                    <div class="barcode-img">
                                        <img v-show="editInfoFormObj.barcode" id="jsBarcodeImg" src="" alt="" />
                                        <img v-show="!editInfoFormObj.barcode" class="no-barcode-img" src="" alt="" />
                                    </div>
                                    <div
                                        v-show="editInfoFormObj.barcode"
                                        @click="editInfoFormObj.barcode = ''"
                                        class="btn-wrap"
                                    >
                                        <i class="yrt yrt-shanchu-copy"></i>
                                        <span>删除条码</span>
                                    </div>
                                    <div v-show="!editInfoFormObj.barcode" @click="createJsBarcodeFn" class="btn-wrap">
                                        <i class="yrt yrt-shengchengtiaoma"></i>
                                        <span>生成条码</span>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </a-tab-pane>
                    <a-tab-pane
                        v-if="typeIsCur === '0' || typeIsCur === '1' || typeIsCur === '2'"
                        tab="员工"
                        :key="tabsArchitectureList[1]"
                    >
                        <!--表格区域开始-->
                        <div class="scoped scoped-white" style="position: static; display: flex; flex: 1">
                            <div class="tab-table-wrap tab-table-overflowy" style="position: static">
                                <a-table
                                    :columns="parentDirectorsColumns"
                                    :dataSource="parentDirectorsDatas"
                                    :pagination="false"
                                    :scroll="{ y: scrollYParentDirectorsNumber }"
                                    :customRow="parentDirectorsCustomRowFn"
                                    :rowClassName="parentDirectorsRowClassFn"
                                >
                                    <!-- 姓名 重写 -->
                                    <div slot="nameTitle">
                                        <span>姓名</span>
                                        <em class="asterisk red">*</em>
                                    </div>
                                    <div slot="employee_inner_sn" slot-scope="text, record">
                                        <a-tree-select
                                            treeNodeFilterProp="title"
                                            searchPlaceholder=""
                                            :showSearch="true"
                                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                            :treeData="parentDirectors"
                                            :allowClear="false"
                                            treeDefaultExpandAll
                                            v-model="record.employee_inner_sn"
                                            class="input"
                                        >
                                        </a-tree-select>
                                    </div>
                                    <!-- 仓库人员类型 - 职务 重写 -->
                                    <div slot="typeTitle">
                                        <span>职务</span>
                                        <em class="asterisk red">*</em>
                                    </div>
                                    <div slot="type" slot-scope="text, record">
                                        <a-tree-select
                                            :showSearch="false"
                                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                            :treeData="employeesTypes"
                                            :allowClear="false"
                                            treeDefaultExpandAll
                                            v-model="record.type"
                                            class="input"
                                        >
                                        </a-tree-select>
                                    </div>
                                    <!-- 操作 重写 -->
                                    <div slot="status" slot-scope="text, record">
                                        <ul class="action-btn-list-wrap" style="position: relative">
                                            <li>
                                                <a-tooltip class="tooltip" placement="top" title="删除">
                                                    <i
                                                        @click.stop="parentDirectorPositionFn(record)"
                                                        class="yrt yrt-shanchu-copy cancel"
                                                        style="cursor: pointer"
                                                    ></i>
                                                </a-tooltip>
                                            </li>
                                        </ul>
                                    </div>
                                </a-table>
                                <!--底部加号重写-->
                                <div class="footer-add-wrap">
                                    <a-tooltip class="tooltip" placement="right" title="新增员工">
                                        <i @click="parentDirectorAddFn" class="yrt yrt-tianjiayuangong"></i>
                                    </a-tooltip>
                                </div>
                            </div>
                        </div>
                        <!--表格区域结束-->
                    </a-tab-pane>
                </a-tabs>
            </div>
        </a-modal>
        <!-- 信息修改区域结束 -->

        <!-- 仓库类别区域开始 -->
        <a-modal
            v-model="warehouseCategoryModal.alert"
            :centered="true"
            :maskClosable="false"
            :mask="false"
            @cancel="warehouseCategoryTypeResetFn('reset')"
            class="modal-form-input-scoped modal-form-input-scoped-8 global-drag warehouse-category-modal"
        >
            <div v-globalDrag="{ el: 'warehouse-category-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ warehouseCategoryModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="submit" type="primary" @click="closeWarehouseCategoryModalFn"> 关闭 </a-button>
            </template>
            <div class="modal-form-input" style="height: 645px">
                <div class="scoped scoped-white" style="top: -15px">
                    <!--头部区域开始-->
                    <!--<a-row class="btn-top-wrap">
                        <a-col :span="24">
                            &lt;!&ndash; 按钮区域 &ndash;&gt;
                            <ul class="btn-list-wrap">
                                <li>
                                    <a-button
                                        @click="groupBtnFn('add', 'warehouseCategoryModal')"
                                        type="primary"
                                        size="small"
                                    >
                                        新增
                                    </a-button>
                                    <a-button
                                        @click="groupBtnFn('edit', 'warehouseCategoryModal')"
                                        type="primary"
                                        size="small"
                                    >
                                        修改
                                    </a-button>
                                </li>
                                <li>
                                    <a-button
                                        @click="groupBtnFn('remove', 'warehouseCategoryModal')"
                                        type="danger"
                                        size="small"
                                    >
                                        删除
                                    </a-button>
                                </li>
                            </ul>
                        </a-col>
                    </a-row>-->
                    <!--头部区域结束-->
                    <!--表格区域开始-->
                    <div class="tab-table-wrap tab-table-overflowy" style="bottom: 57px">
                        <a-table
                            :loading="warehouseCategoryLoading"
                            :columns="warehouseCategoryColumns"
                            :dataSource="warehouseCategory"
                            :pagination="false"
                            :scroll="{ y: scrollYWarehouseCategoryNumber }"
                            :customRow="warehouseCategoryCustomRowFn"
                            :rowClassName="warehouseCategoryRowClassFn"
                        >
                            <!-- 姓名 重写 -->
                            <div slot="name" slot-scope="text, record">
                                <span v-show="record.editFlag">{{ text }}</span>
                                <a-input
                                    v-show="!record.editFlag"
                                    v-model="record.name_edit"
                                    placeholder=""
                                    @click.stop="return false;"
                                    @change.stop="changeEditFn(record)"
                                    class="input input-auto-focus"
                                />
                            </div>
                            <!-- 状态 重写 -->
                            <div
                                slot="status_text"
                                slot-scope="text, record"
                                :class="{
                                    'red': record.status !== 1
                                }"
                                >{{ text }}</div
                            >
                            <!-- 操作 重写 -->
                            <div slot="action" slot-scope="text, record">
                                <ul class="action-btn-list-wrap" style="position: relative">
                                    <li v-show="!record.editFlag">
                                        <a-tooltip
                                            class="tooltip"
                                            placement="right"
                                            title="保存"
                                            :getPopupContainer="getPopupContainer"
                                        >
                                            <a-icon
                                                @click="warehouseCategoryTypeFn('save', record)"
                                                type="check"
                                                class="save"
                                            />
                                        </a-tooltip>
                                        <a-tooltip
                                            class="tooltip"
                                            placement="right"
                                            title="取消"
                                            :getPopupContainer="getPopupContainer"
                                        >
                                            <a-icon
                                                @click.stop="warehouseCategoryTypeFn('cancel', record)"
                                                type="close"
                                                class="cancel"
                                            />
                                        </a-tooltip>
                                    </li>
                                    <li v-show="record.editFlag">
                                        <a-tooltip
                                            class="tooltip"
                                            placement="right"
                                            title="编辑"
                                            :getPopupContainer="getPopupContainer"
                                        >
                                            <a-icon
                                                @click="warehouseCategoryTypeFn('edit', record)"
                                                type="edit"
                                                class="edit"
                                            />
                                        </a-tooltip>
                                        <a-tooltip
                                            class="tooltip"
                                            placement="right"
                                            title="删除"
                                            :getPopupContainer="getPopupContainer"
                                        >
                                            <i
                                                @click="warehouseCategoryTypeFn('remove', record)"
                                                class="yrt yrt-shanchu-copy cancel"
                                            ></i>
                                        </a-tooltip>
                                    </li>
                                </ul>
                            </div>
                        </a-table>
                        <!--底部加号重写-->
                        <div class="footer-add-wrap">
                            <a-tooltip class="tooltip" placement="right" title="新增仓库类别">
                                <i @click="warehouseCategoryTypeFn('add')" class="yrt yrt-xinzengcangku"></i>
                            </a-tooltip>
                        </div>
                    </div>
                    <!--表格区域结束-->
                </div>
            </div>
        </a-modal>
        <!-- 仓库类别区域结束 -->

        <!-- 仓库类别信息修改区域开始 -->
        <a-modal
            v-model="warehouseCategoryInfoModal.alert"
            :centered="true"
            :maskClosable="false"
            class="modal-form-input-scoped global-drag warehouse-category-info-modal"
        >
            <div v-globalDrag="{ el: 'warehouse-category-info-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ warehouseCategoryInfoModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="architectureInfoModalCancelFn('warehouseCategoryInfoModal')">
                    取消
                </a-button>
                <a-button
                    key="submit"
                    type="primary"
                    :loading="warehouseCategoryInfoModal.loading"
                    @click="architectureInfoModalSaveFn('warehouseCategoryInfoModal')"
                >
                    保存
                </a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label" style="width: auto"><em class="em red">*</em>名称</span>
                        <a-input v-model="warehouseCategoryEditInfoFormObj.name" placeholder="" class="input" />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 仓库类别信息修改区域结束 -->
    </div>
</template>

<script>
/* 条形码所用依赖包开始 */
import JsBarcode from 'jsbarcode';
/* 条形码所用依赖包结束 */
import {
    warehouseTypeEditAPI,
    warehouseTypeDelAPI,
    warehouseTypeAddAPI,
    warehouseTypeUsableListAPI,
    provinceAreaAPI,
    cityAreaAPI,
    countyAreaAPI,
    listAreaAPI,
    orgEmpDropdownListAPI,
    orgEmpListAPI,
    orgDeptTopTreeAPI,
    warehouseParentTopListAPI,
    warehouseChildrenListAPI,
    warehouseInfoAPI,
    warehouseAddAPI,
    warehouseDelAPI,
    warehouseEditAPI,
    warehouseCheckAPI
} from '@/service/pageAjax';

export default {
    name: 'warehouseModeling',
    data() {
        return {
            userSystemTypeFlag: parseFloat(this.$vueCookie.get('userSystemType')) === 0, // 0： 平台 // 1 2 3 其他
            // 表格的配置
            warehouseModelingLoading: false,
            columns: [
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    width: '50em',
                    align: 'center'
                },
                {
                    title: '编号',
                    dataIndex: 'warehouse_model_sn',
                    key: 'warehouse_model_sn',
                    width: '15em',
                    align: 'center'
                },
                {
                    title: '类别',
                    dataIndex: 'type_text',
                    key: 'type_text',
                    width: '10em',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    align: 'center',
                    scopedSlots: { customRender: 'status_text' }
                }
            ],
            datas: [],
            systemTypes: ['list', 'architecture'],
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            currentTrObjLoad: {}, // 上级模型当前选中的数据 / 标识
            tabsActiveKey: 'list', // 表格头部的tab选项卡切换的 key 值 【默认值为： 列表】
            tabsArchitectureInfoModalKey: 'basicInfo',
            tabsArchitectureList: ['basicInfo', 'employees'],
            statusAry: ['无效', '有效'], // 状态: 0-无效  1-有效
            // 修改弹框的配置
            architectureInfoModal: {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            },
            // 仓库类别弹框的配置
            warehouseCategoryModal: {
                title: `${this.$route.meta.title} - 仓库类别`,
                alert: false
            },
            warehouseCategoryInfoModal: {
                title: `${this.$route.meta.title} - 仓库类别 - 修改`,
                alert: false,
                loading: false
            },
            editInfoFormObj: {
                // 修改信息的模态框值 - 后端返回回来的
                address: '',
                aisle_sn: '',
                children: null,
                department_names: '',
                departments: '',
                description: '',
                name: '',
                org_id: '',
                org_name: '',
                parent_warehouse_model_inner_sn: '',
                parents: '',
                phone: '',
                proportion: '',
                status: 1,
                type: '0',
                warehouse_model_inner_sn: '',
                warehouse_model_sn: '',
                barcode: '',
                administrative_division_sn: '',
                warehouse_type_inner_sn: ''
            },
            editInfoFormOldObj: {}, // 修改信息的模态框值 - 后端返回回来的旧值
            editInfoFormNewObj: {}, // 修改信息的模态框值 - 前端根据页面修改的值放进去 【没有修改的不要向后端传递】
            btnName: '', // 记录顶部点击的是哪个按钮
            expandedRows: [], // 表格是否发过请求标识的容器
            architectureAllTrees: {
                name: '云南栢田科技有限公司',
                status: 1, // 0：无效  1：有效
                rootFlag: true, // 判断是不是root级
                children: []
            },
            horizontal: false,
            optionStatus: [
                { title: '有效', value: '1', key: 1 }
                // { title: '无效', value: '0', key: 0 }
            ],
            optionStatusCur: [], // 有效状态的默认值
            plainOptions: [],
            // 上级模型
            parentDepartments: [],
            parentDepartmentsLen: 0,
            curBarcode: [],
            parentDepartmentDefaults: [], // 类似于行政区划的北京市：[110000, 110100, 110101]
            parentDirectors: [], // 主管
            parentDirectorsColumns: [
                {
                    // title: '姓名 *',
                    dataIndex: 'name',
                    key: 'name',
                    width: '40%',
                    align: 'center',
                    slots: { title: 'nameTitle' },
                    scopedSlots: { customRender: 'employee_inner_sn' }
                },
                {
                    // title: '职务 *',
                    dataIndex: 'type',
                    key: 'type',
                    width: '40%',
                    align: 'center',
                    slots: { title: 'typeTitle' },
                    scopedSlots: { customRender: 'type' }
                },
                {
                    title: '操作',
                    dataIndex: 'status',
                    key: 'status',
                    width: '20%',
                    align: 'center',
                    scopedSlots: { customRender: 'status' }
                }
            ],
            warehouseCategoryLoading: false,
            parentDirectorsDatas: [], // 员工新值
            parentDirectorsCurrentTrObj: {},
            employees_old: [], // 员工老值
            employees: [], // 员工新值 【给后端传递的值】
            employeesTypes: [
                // 仓库人员类型
                { title: '仓库主管', value: '0', key: 0 },
                { title: '拣货员', value: '1', key: 1 }
            ],
            editEmployeesInfoFlag: true, // 说明没有改
            infoKey: '', // 新增下级同级的key
            typeIs: [
                // 模型类别(type): 0-仓库 1-楼层 2-区域 3-货架组 4-货架 5-货位 6-储物柜 7-储物格
                { title: '仓库', value: '0', key: 0 },
                { title: '楼层', value: '1', key: 1 },
                { title: '区域', value: '2', key: 2 },
                { title: '托盘', value: '3', key: 3 },
                { title: '货架组', value: '4', key: 4 },
                { title: '货架', value: '5', key: 5 },
                { title: '货位', value: '6', key: 6 },
                { title: '储物柜', value: '7', key: 7 },
                { title: '储物格', value: '8', key: 8 }
            ],
            typeIsCur: '0',
            // 仓库类别
            warehouseCategory: [],
            warehouseCategoryCurrentTrObj: {},
            warehouseCategoryEditInfoFormObj: {
                name: ''
            },
            // 仓库类别的表格设置
            warehouseCategoryBtnName: '',
            warehouseCategoryColumns: [
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    width: '50%',
                    align: 'center',
                    scopedSlots: { customRender: 'name' }
                },
                {
                    title: '状态',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    width: '15%',
                    align: 'center',
                    scopedSlots: { customRender: 'status_text' }
                },
                {
                    title: '操作',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    width: '35%',
                    align: 'center',
                    scopedSlots: { customRender: 'action' }
                }
            ],
            // 专属部门的多层递归
            superiorDepartments: [],
            departments: [], // 专属部门的赋值
            deparentsChange: '', // 临时比对专属部门的赋值
            directors: [], // 员工的赋值
            // 省 - 市 - 区
            provinces: [],
            provinceDefaultsFlag: true, // 是否加载默认数据的标识
            provinceDefaults: [], // 北京：[110000, 110100, 110101]
            provincesObj: {
                pIndex: -1,
                cIndex: -1
            },
            barcodeRepeatFlag: false, // 编号是否有重复的判断
            isAddChildModel: false // 顶级仓库点击新增下级模型时要显示上级模型
        };
    },
    methods: {
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
                    if (this.provinceDefaults.length) {
                        this.provinceDefaultsFlag = true;
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
            this.editInfoFormObj.administrative_division_sn = value && value.length ? value[value.length - 1] : '';
        },
        // 省市区数据新增的时候没有数据初始化数据
        clickProvincesFn() {
            if (this.provinces.length <= 0) {
                this.provinceAreaAPIFn();
            }
        },

        /* 经销商-医院-厂商的接口开始 */
        // 【检测】仓库编码是否有重复
        async warehouseCheckAPIFn() {
            let params = {
                warehouse_model_sn: this.editInfoFormObj.warehouse_model_sn,
                warehouse_model_inner_sn: this.editInfoFormObj.warehouse_model_inner_sn
            };

            if (this.btnName === 'edit') {
                if (this.editInfoFormObj.parent_warehouse_model_inner_sn) {
                    params.parent_warehouse_model_inner_sn = this.editInfoFormObj.parent_warehouse_model_inner_sn;
                }
            } else {
                if (Object.keys(this.currentTrObjLoad).length > 0) {
                    if (this.btnName === 'addChild') {
                        // addChild addSameLevel
                        params.parent_warehouse_model_inner_sn = this.currentTrObjLoad.warehouse_model_inner_sn;
                    } else {
                        if (this.currentTrObjLoad.parent_warehouse_model_inner_sn) {
                            params.parent_warehouse_model_inner_sn =
                                this.currentTrObjLoad.parent_warehouse_model_inner_sn;
                        }
                    }
                } else {
                    if (this.btnName === 'addChild') {
                        // addChild addSameLevel
                        params.parent_warehouse_model_inner_sn = this.currentTrObj.warehouse_model_inner_sn;
                    } else {
                        if (this.currentTrObj.parent_warehouse_model_inner_sn) {
                            params.parent_warehouse_model_inner_sn = this.currentTrObj.parent_warehouse_model_inner_sn;
                        }
                    }
                }
            }

            await warehouseCheckAPI({
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.barcodeRepeatFlag = false;
                } else {
                    this.barcodeRepeatFlag = true;
                }
                this.saveBtnFlag = true;
            });
        },
        // 【删除】机构仓库仓库
        async warehouseTypeDelAPIFn(wid) {
            await warehouseTypeDelAPI(wid)
                .then((res) => {
                    this.warehouseCategoryLoading = false;
                    if (parseFloat(res.code) === 0) {
                        this.warehouseTypeUsableListAPIFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.warehouseCategoryLoading = false;
                });
        },
        // 【新增】机构仓库仓库
        async warehouseTypeAddAPIFn() {
            let params = {
                org_id: this.$vueCookie.get('userbelong'),
                name: this.warehouseCategoryEditInfoFormObj.name
            };

            await warehouseTypeAddAPI(params)
                .then((res) => {
                    this.warehouseCategoryLoading = false;
                    if (parseFloat(res.code) === 0) {
                        this.warehouseTypeUsableListAPIFn(res.info.type_inner_sn);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.warehouseCategoryLoading = false;
                });
        },
        // 【编辑】机构仓库仓库
        async warehouseTypeEditAPIFn() {
            let params = {
                org_id: this.warehouseCategoryEditInfoFormObj.org_id,
                type_inner_sn: this.warehouseCategoryEditInfoFormObj.type_inner_sn,
                name: this.warehouseCategoryEditInfoFormObj.name
            };
            await warehouseTypeEditAPI(params)
                .then((res) => {
                    this.warehouseCategoryLoading = false;
                    if (parseFloat(res.code) === 0) {
                        this.warehouseTypeUsableListAPIFn(this.warehouseCategoryEditInfoFormObj.type_inner_sn);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.warehouseCategoryLoading = false;
                });
        },
        // 【查询】机构仓库类型
        async warehouseTypeUsableListAPIFn(insertAddId) {
            await warehouseTypeUsableListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.warehouseCategory = list.map((item) => {
                        return {
                            ...item,
                            title: item.name,
                            value: item.type_inner_sn.toString(),
                            activeClass: '',
                            key: item.type_inner_sn,
                            status_text: this.statusAry[item.status],
                            name_edit: item.name, // 编辑
                            editFlag: true // 是否可编辑的标识
                        };
                    });
                    if (typeof insertAddId !== 'undefined') {
                        this.warehouseCategoryCurrentTrObj = this.warehouseCategory.filter(
                            (item) => parseFloat(item.type_inner_sn) === parseFloat(insertAddId)
                        )[0];
                        this.editInfoFormObj.warehouse_type_inner_sn = insertAddId;
                    } else {
                        if (this.warehouseCategory.length) {
                            this.warehouseCategoryCurrentTrObj = this.warehouseCategory[0];
                            this.editInfoFormObj.warehouse_type_inner_sn =
                                this.warehouseCategoryCurrentTrObj.type_inner_sn;
                        } else {
                            this.warehouseCategoryCurrentTrObj = {};
                            this.editInfoFormObj.warehouse_type_inner_sn = '';
                        }
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },

        // 【查询经销商人员树】
        async orgEmpListAPIFn() {
            await orgEmpListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { rows } = res;
                    this.treeDatasIndexAryFn(rows, 'parentDirectors'); // 上级主管
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【查询机构所有人员】用于下列列表
        async orgEmpDropdownListAPIFn() {
            await orgEmpDropdownListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const listNew = res.list.map((item) => {
                        item.children = null;
                        return item;
                    });
                    this.treeDatasIndexAryFn(listNew, 'parentDirectors'); // 上级主管
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID查询经销商【所有部门】
        async orgDeptTopTreeAPIFn() {
            await orgDeptTopTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'superiorDepartments');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【删除】经销商部门
        async warehouseDelAPIFn(wid) {
            await warehouseDelAPI(wid)
                .then((res) => {
                    this.architectureInfoModal.loading = false;
                    this.warehouseModelingLoading = false;
                    if (parseFloat(res.code) === 0) {
                        if (!this.currentTrObj.parent_warehouse_model_inner_sn) {
                            this.datas.splice(this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1], 1);
                            if (this.datas.length) {
                                this.currentTrObj = this.datas[0];
                                this.treeDatasIndexAryFn(this.datas);
                            } else {
                                this.datas = [];
                                this.currentTrObj = {};
                            }
                        } else {
                            this.getDatasLocationFn(this.currentTrObj, this.datas[this.currentTrObj.indexAry[0]], 0);
                        }
                        this.architectureInfoModal.alert = false;
                        // this.$message.success('删除成功')
                        this.$store.commit('refreshUserWarehouseListFn');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.architectureInfoModal.loading = false;
                    this.warehouseModelingLoading = false;
                });
        },
        // 【新增】经销商部门
        async warehouseAddAPIFn() {
            let params = {
                name_i: this.editInfoFormObj.name,
                type_i: this.editInfoFormObj.type,
                warehouse_model_sn_i: this.editInfoFormObj.warehouse_model_sn
            };

            // 判断是新增子分类还是新增同级分类的按钮 [ 自定义分类父ID ]
            if (Object.keys(this.currentTrObjLoad).length > 0) {
                if (this.btnName === 'addChild') {
                    // addChild addSameLevel
                    params.parent_warehouse_model_inner_sn_i = this.currentTrObjLoad.warehouse_model_inner_sn;
                } else {
                    if (this.currentTrObjLoad.parent_warehouse_model_inner_sn) {
                        params.parent_warehouse_model_inner_sn_i =
                            this.currentTrObjLoad.parent_warehouse_model_inner_sn;
                    }
                }
            } else {
                if (this.btnName === 'addChild') {
                    // addChild addSameLevel
                    params.parent_warehouse_model_inner_sn_i = this.currentTrObj.warehouse_model_inner_sn;
                } else {
                    if (this.currentTrObj.parent_warehouse_model_inner_sn) {
                        params.parent_warehouse_model_inner_sn_i = this.currentTrObj.parent_warehouse_model_inner_sn;
                    }
                }
            }

            if (this.typeIsCur === '2' && this.editInfoFormObj.proportion) {
                params.proportion_i = this.editInfoFormObj.proportion;
            }

            if (this.typeIsCur === '5' && this.editInfoFormObj.aisle_sn) {
                params.aisle_sn_i = this.editInfoFormObj.aisle_sn;
            }

            if (this.typeIsCur === '0' && this.editInfoFormObj.phone) {
                params.phone_i = this.editInfoFormObj.phone;
            }

            if (this.typeIsCur === '0' && this.editInfoFormObj.address) {
                params.address_i = this.editInfoFormObj.address;
            }

            if (this.editInfoFormObj.description) {
                params.description_i = this.editInfoFormObj.description;
            }

            if ((this.typeIsCur === '0' || this.typeIsCur === '7') && this.editInfoFormObj.departments) {
                params.departments = this.editInfoFormObj.departments;
            }

            if (
                (this.typeIsCur === '0' || this.typeIsCur === '1' || this.typeIsCur === '2') &&
                !this.editEmployeesInfoFlag
            ) {
                params.employees = this.employees;
            }

            if (this.editInfoFormObj.barcode) {
                params.barcode_i = this.editInfoFormObj.barcode;
            }

            if (this.typeIsCur === '0' && this.editInfoFormObj.administrative_division_sn) {
                params.administrative_division_sn_i = this.editInfoFormObj.administrative_division_sn;
            }

            if (this.typeIsCur === '0' && this.editInfoFormObj.warehouse_type_inner_sn) {
                params.warehouse_type_inner_sn_i = this.editInfoFormObj.warehouse_type_inner_sn;
            }

            await warehouseAddAPI(params)
                .then((res) => {
                    this.architectureInfoModal.loading = false;
                    this.warehouseModelingLoading = false;
                    if (parseFloat(res.code) === 0) {
                        let { info } = res;
                        info['key'] = info.warehouse_model_inner_sn;
                        info['status_text'] = this.statusAry[parseFloat(info.status)];
                        info['type_text'] = this.formatTypeIsFn(info.type);
                        info['children'] = parseFloat(info.is_leaf) === 0 ? [] : null;
                        info['loadingChildrenFlag'] = false;

                        this.infoKey = info['key']; // 选中当前新增的那一条数据的key
                        if (Object.keys(this.currentTrObjLoad).length > 0) {
                            // 说明是修改了上级模型
                            if (!this.currentTrObjLoad.parent_warehouse_model_inner_sn) {
                                if (this.btnName === 'addChild') {
                                    // 下级插入
                                    if (
                                        !this.datas[
                                            this.currentTrObjLoad.indexAry[this.currentTrObjLoad.indexAry.length - 1]
                                        ].children ||
                                        this.datas[
                                            this.currentTrObjLoad.indexAry[this.currentTrObj.indexAry.length - 1]
                                        ].children.length <= 0
                                    ) {
                                        this.datas[
                                            this.currentTrObjLoad.indexAry[this.currentTrObjLoad.indexAry.length - 1]
                                        ].children = [];
                                    }
                                    this.datas[
                                        this.currentTrObjLoad.indexAry[this.currentTrObjLoad.indexAry.length - 1]
                                    ].children.push(info);
                                } else if (this.btnName === 'addSameLevel') {
                                    // 同级插入
                                    this.datas.push(info);
                                }
                                this.treeDatasIndexAryFn(this.datas, null, info.warehouse_model_inner_sn);
                            } else {
                                this.getDatasLocationFn(info, this.datas[this.currentTrObjLoad.indexAry[0]], 0);
                            }
                        } else {
                            if (!this.currentTrObj.parent_warehouse_model_inner_sn) {
                                if (this.btnName === 'addChild') {
                                    // 下级插入
                                    if (
                                        !this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]]
                                            .children ||
                                        this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]]
                                            .children.length <= 0
                                    ) {
                                        this.datas[
                                            this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]
                                        ].children = [];
                                    }
                                    this.datas[
                                        this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]
                                    ].children.push(info);
                                } else if (this.btnName === 'addSameLevel') {
                                    // 同级插入
                                    this.datas.push(info);
                                }
                                this.treeDatasIndexAryFn(this.datas, null, info.warehouse_model_inner_sn);
                            } else {
                                this.getDatasLocationFn(info, this.datas[this.currentTrObj.indexAry[0]], 0);
                            }
                        }

                        this.architectureInfoModal.alert = false;
                        this.isAddChildModel = false;
                        this.warehouseParentTopListAPIFn();
                        this.initTablesFn();

                        this.$store.commit('refreshUserWarehouseListFn');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.architectureInfoModal.loading = false;
                    this.warehouseModelingLoading = false;
                });
        },
        // 【编辑】经销商部门
        async warehouseEditAPIFn(editInfoFormNewObj) {
            // 传递给后端的参数，没有的值就不要传递了
            // if (parseFloat(editInfoFormNewObj.proportion) === 0) {
            //     delete editInfoFormNewObj.proportion
            //     delete editInfoFormNewObj.proportion_old
            // }
            let params = {
                ...editInfoFormNewObj
            };

            if (
                (this.typeIsCur === '0' || this.typeIsCur === '1' || this.typeIsCur === '2') &&
                !this.editEmployeesInfoFlag
            ) {
                params.employees = this.employees;
                params.employees_old = this.employees_old;
            }

            await warehouseEditAPI(this.currentTrObj.warehouse_model_inner_sn, params)
                .then((res) => {
                    this.architectureInfoModal.loading = false;
                    this.warehouseModelingLoading = false;
                    if (parseFloat(res.code) === 0) {
                        this.architectureInfoModal.alert = false;
                        if (Object.keys(this.currentTrObjLoad).length > 0) {
                            this.removeParentModalsFn();
                        } else {
                            this.getDatasLocationFn(this.currentTrObj, this.datas[this.currentTrObj.indexAry[0]], 0);
                        }
                        this.initTablesFn();
                        // this.$message.success(`${this.architectureInfoModal.btnTitle}成功`)
                        this.$store.commit('refreshUserWarehouseListFn');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.architectureInfoModal.loading = false;
                    this.warehouseModelingLoading = false;
                });
        },
        // 根据机构ID和部门ID查询经销商【部门详情】
        async warehouseInfoAPIFn(wid) {
            await warehouseInfoAPI(wid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res,
                        infos = {
                            ...info,
                            type: info.type ? info.type + '' : this.typeIs[0].value,
                            director_inner_sn: info.director_inner_sn ? info.director_inner_sn + '' : '',
                            parent_warehouse_model_inner_sn: info.parent_warehouse_model_inner_sn
                                ? info.parent_warehouse_model_inner_sn + ''
                                : '',
                            warehouse_type_inner_sn: info.warehouse_type_inner_sn
                                ? info.warehouse_type_inner_sn + ''
                                : '',
                            administrative_division_sn: info.administrative_division_sn || ''
                        };
                    // 多选框赋值 1:有效赋值
                    this.optionStatusCur = parseFloat(infos.status) === 1 ? [this.optionStatus[0].value] : [];
                    this.editInfoFormObj = { ...infos };
                    this.editInfoFormOldObj = { ...infos }; // 为了比对值的临时存储
                    // 行政区划赋值
                    if (infos.administrative_division_sn) {
                        this.listAreaAPIFn(infos.administrative_division_sn);
                    } else {
                        this.provinceAreaAPIFn();
                    }

                    if (
                        typeof this.warehouseCategory.find((item) => item.value === infos.warehouse_type_inner_sn) ===
                        'undefined'
                    ) {
                        this.editInfoFormObj.warehouse_type_inner_sn = '';
                    } else {
                        this.editInfoFormObj.warehouse_type_inner_sn = infos.warehouse_type_inner_sn;
                    }

                    // 上级模型的源头赋值
                    if (info.parents) {
                        this.flatOutParentDepartmentsFn(info.parents);
                        if (this.parentDepartmentDefaults.length) {
                            this.parentDepartmentsLen = 0;
                            this.warehouseParentTopListAPIFn(true);
                        }
                    }

                    this.typeIsCur = infos.type; // 给条形码赋值
                    infos.employees.forEach((item) => {
                        this.parentDirectorsDatas.push({
                            ...item,
                            key: item.employee_inner_sn,
                            activeClass: '',
                            name: item.employee_name, // 姓名
                            status: 1,
                            type: item.type.toString()
                        }); // 给员工赋值
                        this.parentDirectorsCurrentTrObj = this.parentDirectorsDatas.length
                            ? this.parentDirectorsDatas[0]
                            : {};
                        this.employees_old.push({
                            employee_inner_sn: item.employee_inner_sn,
                            type: item.type.toString()
                        }); // 给员工老值赋值，以便做统计看是否有改变过
                    });
                    this.departments = infos.departments ? infos.departments.split(',') : []; // 给专属部门赋值
                    this.editInfoFormObj.barcode &&
                        this.editInfoFormObj.warehouse_model_sn &&
                        this.showJsBarcodeFn(this.editInfoFormObj.barcode);
                    this.$Utils.globalDragCenterFn('architecture-category-modal');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID和上级部门ID查询经销商【所有下级部门】
        async warehouseChildrenListAPIFn(record, isAddFlag, parentModalFlag) {
            // 每一次的item, isAddFlag, 上级模型
            if (!record) return;
            await warehouseChildrenListAPI(record.warehouse_model_inner_sn)
                .then((res) => {
                    if (!parentModalFlag) {
                        this.warehouseModelingLoading = false;
                    } else {
                        parentModalFlag === 'parentModalFirst' && this.parentDepartmentsLen++;
                    }

                    const { code, list } = res;
                    if (parseFloat(code) === 0) {
                        // 给列表的表格赋值
                        const rowsNew = list.map((item, index) => {
                            item.key = item.warehouse_model_inner_sn;
                            item.activeClass = '';
                            item.status_text = this.statusAry[parseFloat(item.status)];
                            item.indexAry = [...record.indexAry, index];
                            item.parentDepartmentName = [...record.parentDepartmentName, item.name];
                            item.parentWarehouseModelInnerSn = [
                                ...record.parentWarehouseModelInnerSn,
                                item.warehouse_model_inner_sn
                            ];
                            if (record.curBarcode && record.curBarcode.length) {
                                if (item.barcode) {
                                    item.curBarcode = [...record.curBarcode, item.warehouse_model_sn];
                                } else {
                                    item.curBarcode = [...record.curBarcode];
                                }
                            } else {
                                if (item.barcode) {
                                    item.curBarcode = [item.barcode, item.warehouse_model_sn];
                                } else {
                                    item.curBarcode = [item.warehouse_model_sn];
                                }
                            }
                            item.type_text = this.formatTypeIsFn(item.type);
                            item.is_leaf = parseFloat(item.is_leaf);
                            if (!parentModalFlag) {
                                if (item.is_leaf === 0) {
                                    item.children = [];
                                } else {
                                    delete item.children;
                                }
                            } else {
                                delete item.children;
                            }
                            item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】

                            /* 上级模型的值构建 */
                            item.value = item.warehouse_model_inner_sn.toString();
                            item.label = item.name;
                            item.isLeaf = item.is_leaf === 1;

                            return item;
                        });

                        if (parentModalFlag) {
                            record.loading = false;
                            if (parentModalFlag === 'parentModalLoad') {
                                // 上级模型赋值每次点击的时候加载
                                if (rowsNew.length > 0) {
                                    delete rowsNew.children;
                                    delete rowsNew.isLeaf;
                                    if (!record.children) {
                                        record.children = [];
                                    }
                                    record.children = [...rowsNew];
                                } else {
                                    delete record.children;
                                    delete record.isLeaf;
                                }
                                this.parentDepartments = [...this.parentDepartments];
                            } else if (parentModalFlag === 'parentModalFirst') {
                                // 上级模型赋值第一次加载
                                if (rowsNew.length > 0) {
                                    delete rowsNew.children;
                                    delete rowsNew.isLeaf;
                                    if (!record.children) {
                                        record.children = [];
                                    }
                                    record.children = [...rowsNew];
                                } else {
                                    delete record.children;
                                    delete record.isLeaf;
                                }
                                this.parentDepartments = [...this.parentDepartments];
                                if (
                                    this.parentDepartmentsLen <= this.parentDepartmentDefaults.length &&
                                    rowsNew.length > 0
                                ) {
                                    this.warehouseChildrenListAPIFn(
                                        record.children[this.currentTrObj.indexAry[this.parentDepartmentsLen]],
                                        false,
                                        'parentModalFirst'
                                    );
                                }
                            }
                        } else {
                            if (!record.children) {
                                record.children = [];
                            }
                            record.children = rowsNew.length > 0 ? [...rowsNew] : null;
                            record.loadingChildrenFlag = true;
                            if (isAddFlag) {
                                this.warehouseAddAPIFn(record, this.datas);
                            } else {
                                this.treeDatasIndexAryFn(this.datas);
                            }
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    if (!parentModalFlag) {
                        this.warehouseModelingLoading = false;
                    }
                });
        },
        // 根据机构ID【查询机构所有一级仓库】
        async warehouseParentTopListAPIFn(parentModalFlag) {
            await warehouseParentTopListAPI()
                .then((res) => {
                    if (!parentModalFlag) {
                        this.warehouseModelingLoading = false;
                    }

                    const { code, list } = res;
                    if (parseFloat(code) === 0) {
                        // 给列表的表格赋值
                        let newList = list.map((item, index) => {
                            if (item.warehouse_model_sn) {
                                item.curBarcode = [item.warehouse_model_sn];
                            } else {
                                item.curBarcode = [];
                            }
                            item.key = item.warehouse_model_inner_sn;
                            item.activeClass = '';
                            item.status_text = this.statusAry[parseFloat(item.status)];
                            item.indexAry = [index];
                            item.parentDepartmentName = [item.name];
                            item.parentWarehouseModelInnerSn = [item.warehouse_model_inner_sn];
                            item.type_text = this.formatTypeIsFn(item.type);
                            item.is_leaf = parseFloat(item.is_leaf);
                            if (!parentModalFlag) {
                                if (item.is_leaf === 0) {
                                    item.children = [];
                                } else {
                                    delete item.children;
                                }
                            } else {
                                delete item.children;
                            }
                            item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】

                            /* 上级模型的值构建 */
                            item.value = item.warehouse_model_inner_sn.toString();
                            item.label = item.name;
                            item.isLeaf = item.is_leaf === 1;

                            /* 给第一级仓库模型自定义属性 */
                            item.isFirstLevelWarehouseModel = true;

                            return item;
                        });

                        if (!parentModalFlag) {
                            this.datas = newList;
                            if (this.datas.length) {
                                this.currentTrObj = this.datas[0];
                            }
                        } else {
                            this.parentDepartments = [...newList]; // 给上级模型赋值父级
                            const pIndex = this.currentTrObj.indexAry[this.parentDepartmentsLen];
                            this.parentDepartmentDefaults.length > 1 &&
                                this.warehouseChildrenListAPIFn(
                                    this.parentDepartments[pIndex],
                                    false,
                                    'parentModalFirst'
                                );
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    if (!parentModalFlag) {
                        this.warehouseModelingLoading = false;
                    }
                });
        },
        /* 经销商-医院-厂商的接口结束 */

        // 专属部门
        changeSuperiorDepartmentsFn(value) {
            this.deparentsChange = value.join(',');

            /* 以下代码是为了专属部门是否修改了向后端传值的比对 */
            if (this.editInfoFormOldObj.departments) {
                let departmentsMap = new Set(value);
                this.editInfoFormOldObj.departments.split(',').forEach((_) => departmentsMap.add(_));
                if (value.length !== departmentsMap.size) {
                    this.editInfoFormObj.departments = this.deparentsChange;
                } else {
                    this.editInfoFormObj.departments = this.editInfoFormOldObj.departments;
                }
                departmentsMap = null; // 释放对象
            } else {
                this.editInfoFormObj.departments = this.deparentsChange;
            }
        },
        // tab切换
        changeTabsFn(activeKey) {
            this.tabsActiveKey = activeKey;
        },
        parentDirectorsCustomRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.parentDirectorsCurrentTrObj = record;
                    },
                    dblclick: () => {
                        this.parentDirectorsCurrentTrObj = record;
                    }
                }
            };
        },
        warehouseCategoryRowFn(record) {
            if (this.warehouseCategoryBtnName !== 'add' && this.checkWarehouseCategoryAddContentNoFn()) {
                this.$warning({
                    title: '操作提示',
                    content: '请填写仓库类别的名称，再进行操作',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            if (this.warehouseCategoryBtnName !== 'save' && this.checkWarehouseCategoryAddContentYesFn()) {
                this.$warning({
                    title: '操作提示',
                    content: '请保存仓库类别，再进行操作',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            if (
                !this.warehouseCategoryBtnName &&
                this.warehouseCategory.every((item) => item.name_edit.length > 0 && item.name.length > 0)
            ) {
                this.warehouseCategoryTypeResetFn();
            }
            this.warehouseCategoryCurrentTrObj = record;
            this.editInfoFormObj.warehouse_type_inner_sn = record.type_inner_sn;
            this.warehouseCategoryBtnName = '';
        },
        warehouseCategoryCustomRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.warehouseCategoryRowFn(record, 'click');
                    },
                    dblclick: () => {
                        this.warehouseCategoryRowFn(record, 'dblclick');
                        this.warehouseCategoryTypeFn('edit', record);
                        // this.groupBtnFn('edit', 'warehouseCategoryModal')
                    }
                }
            };
        },
        // 设置行属性 —— 点击每一行的事件
        customRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentTrObj = record;
                    },
                    dblclick: (e) => {
                        if ((e.srcElement || e.target).classList.contains('ant-table-row-expand-icon')) return;
                        this.currentTrObj = record;
                        this.groupBtnFn('edit', 'architectureBtn');
                    }
                }
            };
        },
        // 获取数据的索引值【坐标】
        getDatasLocationFn(record, list, i, datas, editParentModalFlag) {
            // record: 后端数据   list: 要改变的数据源  i: 索引  editParentModalFlag: 是否修改过上级模型
            let num = this.currentTrObj.indexAry;
            if (this.btnName === 'addChild' || this.btnName === 'addSameLevel') {
                if (Object.keys(this.currentTrObjLoad).length > 0) {
                    num = this.currentTrObjLoad.indexAry;
                }
            }
            if (num.length <= 1) {
                // 只有一级数据
                switch (this.btnName) {
                    case 'addChild': // 新增下级模型
                        break;
                    case 'addSameLevel': // 新增同级模型
                        break;
                    case 'edit': // 修改
                        this.datas.splice(num[num.length - 1], 1, {
                            ...list,
                            name: this.editInfoFormObj.name,
                            warehouse_model_sn: this.editInfoFormObj.warehouse_model_sn,
                            status: this.editInfoFormObj.status,
                            status_text: this.statusAry[parseFloat(this.editInfoFormObj.status)],
                            type_text: this.formatTypeIsFn(this.editInfoFormObj.type)
                        });
                        break;
                }
                return;
            }
            ++i; // 多级数据
            list.children.forEach((item, index) => {
                if (index === num[i]) {
                    switch (this.btnName) {
                        case 'addChild': // 新增下级模型
                            if (i === num.length - 1) {
                                // 根据索引的值来判断
                                if (!item.children) {
                                    item.children = [];
                                }
                                item.children.push({
                                    ...record
                                });
                                this.treeDatasIndexAryFn(this.datas, null, record.warehouse_model_inner_sn);
                                this.currentTrObjLoad = {};
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas, editParentModalFlag);
                            }
                            break;
                        case 'addSameLevel': // 新增同级模型
                            if (num.length === 2) {
                                this.datas[num[num.length - 2]].children.push(record);
                                this.treeDatasIndexAryFn(this.datas, null, record.warehouse_model_inner_sn);
                                this.currentTrObjLoad = {};
                                break;
                            }
                            if (i === num.length - 2) {
                                // 根据索引的值来判断
                                if (!item.children) {
                                    item.children = [];
                                }
                                item.children.push(record);
                                this.treeDatasIndexAryFn(this.datas, null, record.warehouse_model_inner_sn);
                                this.currentTrObjLoad = {};
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas);
                            }
                            break;
                        case 'edit': // 修改
                            if (i === num.length - 1) {
                                // 根据索引的值来判断
                                list.children.splice(num[num.length - 1], 1, {
                                    ...item,
                                    name: this.editInfoFormObj.name,
                                    warehouse_model_sn: this.editInfoFormObj.warehouse_model_sn,
                                    status: this.editInfoFormObj.status,
                                    status_text: this.statusAry[parseFloat(this.editInfoFormObj.status)],
                                    type_text: this.formatTypeIsFn(this.editInfoFormObj.type)
                                });
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas);
                            }
                            break;
                        case 'remove':
                            if (num.length === 2) {
                                let removeItem = this.datas[num[num.length - 2]].children.splice(
                                    num[num.length - 1],
                                    1
                                );
                                if (
                                    this.datas[num[num.length - 2]].children &&
                                    this.datas[num[num.length - 2]].children.length <= 0
                                ) {
                                    this.datas[num[num.length - 2]].children = null;
                                }
                                if (editParentModalFlag && editParentModalFlag === 'editParentModalFlag') {
                                    this.addParentModalsFn(removeItem[0]);
                                } else {
                                    if (this.datas.length <= 0) {
                                        this.currentTrObj = {};
                                    }
                                    this.treeDatasIndexAryFn(this.datas);
                                }
                            } else if (i === num.length - 2) {
                                // 根据索引的值来判断
                                let removeItem = item.children.splice(num[num.length - 1], 1);
                                if (item.children && item.children.length <= 0) {
                                    item.children = null;
                                }
                                if (editParentModalFlag && editParentModalFlag === 'editParentModalFlag') {
                                    this.addParentModalsFn(removeItem[0]);
                                } else {
                                    if (this.datas.length <= 0) {
                                        this.currentTrObj = {};
                                    }
                                    this.treeDatasIndexAryFn(this.datas);
                                }
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas, editParentModalFlag);
                            }
                            break;
                    }
                }
            });
        },
        // 修改上级模型 【删除】
        removeParentModalsFn() {
            if (!this.currentTrObj.parent_warehouse_model_inner_sn) {
                let removeItem = this.datas.splice(
                    this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1],
                    1
                );
                if (this.datas.length <= 0) {
                    this.datas = [];
                    this.currentTrObj = {};
                }
                this.addParentModalsFn(removeItem[0]);
            } else {
                this.btnName = 'remove';
                this.getDatasLocationFn(
                    this.currentTrObj,
                    this.datas[this.currentTrObj.indexAry[0]],
                    0,
                    false,
                    'editParentModalFlag'
                );
            }
        },
        // 循环插入元素 【查询】
        insertAryFn(flagName, pidName = 'parent_warehouse_model_inner_sn', newItem, datas, parentId) {
            if (datas) {
                let id = parentId || newItem[pidName];
                for (let i = 0; i < datas.length; i++) {
                    if (datas[i].warehouse_model_inner_sn === id) {
                        if (flagName === 'addChild') {
                            // parent_warehouse_model_inner_sn
                            if (!datas[i].children) {
                                datas[i].children = [];
                            }
                            datas[i].children.push({
                                ...newItem,
                                parent_warehouse_model_inner_sn: datas[i].warehouse_model_inner_sn
                            });
                            this.treeDatasIndexAryFn(
                                this.datas,
                                null,
                                datas[i].children[datas[i].children.length - 1].warehouse_model_inner_sn
                            );
                            this.currentTrObjLoad = {};
                        } else if (flagName === 'requestChild') {
                            // warehouse_model_inner_sn
                            this.expandFn(true, datas[i]);
                        }
                        break;
                    } else if (datas[i].children) {
                        this.insertAryFn(flagName, pidName, newItem, datas[i].children, id);
                    } else if (datas[i + 1]) {
                        if (datas[i + 1].warehouse_model_inner_sn === id) {
                            if (flagName === 'addChild') {
                                if (!datas[i + 1].children) {
                                    datas[i + 1].children = [];
                                }
                                datas[i + 1].children.push({
                                    ...newItem,
                                    parent_warehouse_model_inner_sn: datas[i + 1].warehouse_model_inner_sn
                                });
                                this.treeDatasIndexAryFn(
                                    this.datas,
                                    null,
                                    datas[i + 1].children[datas[i + 1].children.length - 1].warehouse_model_inner_sn
                                );
                                this.currentTrObjLoad = {};
                            } else if (flagName === 'requestChild') {
                                this.expandFn(true, datas[i + 1]);
                            }
                            break;
                        } else if (datas[i + 1].children) {
                            this.insertAryFn(flagName, pidName, newItem, datas[i + 1].children, id);
                        }
                    }
                }
            }
        },

        // 修改上级模型 【插入】
        addParentModalsFn(curItem) {
            if (!this.editInfoFormObj.parent_warehouse_model_inner_sn) {
                this.datas.push({
                    ...curItem,
                    parent_warehouse_model_inner_sn: ''
                });
                this.treeDatasIndexAryFn(this.datas, null, curItem.warehouse_model_inner_sn);
                this.currentTrObjLoad = {};
            } else {
                this.insertAryFn(
                    'addChild',
                    'parent_warehouse_model_inner_sn',
                    curItem,
                    this.datas,
                    this.editInfoFormObj.parent_warehouse_model_inner_sn
                );
            }
        },
        // 初始化数据【搜索按钮】
        initTablesFn() {
            this.currentTrObj = {};
            this.tabsActiveKey = 'list';
            this.architectureInfoModal = {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            };
            this.editInfoFormObj = {
                address: '',
                aisle_sn: '',
                children: null,
                department_names: '',
                departments: '',
                description: '',
                name: '',
                org_id: '',
                org_name: '',
                parent_warehouse_model_inner_sn: '',
                parents: '',
                phone: '',
                proportion: '',
                status: 1,
                type: '0',
                warehouse_model_inner_sn: '',
                warehouse_model_sn: '',
                barcode: '',
                administrative_division_sn: '',
                warehouse_type_inner_sn: ''
            };
            this.currentTrObjLoad = {};
            this.infoKey = '';
            this.optionStatusCur = [];
            this.typeIsCur = '';
            this.editInfoFormOldObj = {};
            this.editInfoFormNewObj = {};
            this.btnName = '';
            this.expandedRows = [];
            this.architectureAllTrees = {
                name: '云南栢田科技有限公司',
                status: 1, // 0：无效  1：有效
                rootFlag: true, // 判断是不是root级
                children: []
            };
            this.horizontal = false;
            this.plainOptions = [];
            this.superiorDepartments = [];
            this.parentDepartmentDefaults = [];
            this.curBarcodeDefaults = [];
            this.warehouseModelingLoading = true;
            this.datas = null;
            this.infoKey = '';
            this.parentDirectors = [];
            this.directors = [];
            this.departments = [];
            this.warehouseCategoryCurrentTrObj = {};
            this.parentDirectorsCurrentTrObj = {};
            this.warehouseParentTopListAPIFn();
        },
        // 点击展开图标时触发
        expandFn(expanded, record, isAddFlag) {
            if (expanded) {
                this.expandedRows.push(record.warehouse_model_inner_sn);
            } else {
                this.expandedRows = this.expandedRows.filter((item) => item !== record.warehouse_model_inner_sn);
            }
            if (!record.loadingChildrenFlag) {
                this.warehouseModelingLoading = true;
                this.warehouseChildrenListAPIFn(record, isAddFlag);
            }
        },
        // 表单默认值重置
        architectureInfoModalResetFn() {
            this.editInfoFormObj = {
                address: '',
                aisle_sn: '',
                children: null,
                department_names: '',
                departments: '',
                description: '',
                name: '',
                org_id: '',
                org_name: '',
                parent_warehouse_model_inner_sn: '',
                parents: '',
                phone: '',
                proportion: '',
                status: 1,
                type: '0',
                warehouse_model_inner_sn: '',
                warehouse_model_sn: '',
                barcode: '',
                administrative_division_sn: '',
                warehouse_type_inner_sn: ''
            };
            this.parentDepartmentDefaults = [];
            this.curBarcodeDefaults = [];
            this.parentDirectorsCurrentTrObj = {};
            this.infoKey = '';
            this.optionStatusCur = [];
            this.typeIsCur = '';
            this.departments = [];
            this.directors = [];
            this.currentTrObjLoad = {};
            this.employees_old = [];
            this.parentDirectorsDatas = [];
            this.employees = [];
            this.provinceDefaults = [];
            this.provincesObj = {
                pIndex: -1,
                cIndex: -1
            };
            this.saveBtnFlag = true;
            this.barcodeRepeatFlag = false;
            this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
            this.warehouseCategoryCurrentTrObj = {};

            if (this.btnName === 'addChild' || this.btnName === 'addSameLevel') {
                this.typeIsCur = '0';
                this.editInfoFormObj.type = '0';
                this.optionStatusCur = ['1'];
                this.editInfoFormObj.status = 1;
            }
        },
        // 获取有效状态的值
        getStatusFn(checkedStatus) {
            this.editInfoFormObj.status = parseFloat(checkedStatus[0]) || 0;
        },
        // 平铺展开上级模型的多层嵌套数据
        flatOutParentDepartmentsFn(obj) {
            obj['warehouse_model_sn'] && this.curBarcodeDefaults.unshift(obj['warehouse_model_sn'].toString());
            this.parentDepartmentDefaults.unshift(obj['warehouse_model_inner_sn'].toString());
            if (!obj['parents']) return;
            this.flatOutParentDepartmentsFn(obj['parents']);
        },
        // 上级模型加载数据
        loadParentDepartments(selectedOptions) {
            const targetOption = selectedOptions[selectedOptions.length - 1];
            targetOption.loading = true;
            this.warehouseChildrenListAPIFn(targetOption, false, 'parentModalLoad');

            // 加载表格数据
            this.currentTrObjLoad = targetOption;
            if (this.expandedRows.indexOf(targetOption.warehouse_model_inner_sn) <= -1) {
                this.insertAryFn('requestChild', 'warehouse_model_inner_sn', this.currentTrObjLoad, this.datas);
            }
        },
        // 上级模型的选择完成后的回调
        changeParentDepartmentsFn(value, selectedOptions) {
            if (selectedOptions) {
                if (
                    typeof selectedOptions.find(
                        (item) => item.warehouse_model_inner_sn === this.currentTrObj.warehouse_model_inner_sn
                    ) !== 'undefined'
                ) {
                    document.getElementsByClassName(
                        'ant-cascader-menus ant-cascader-menus-placement-bottomLeft'
                    )[0].style.zIndex = 3;
                    this.$warning({
                        title: '操作提示',
                        content: '上级模型不能选择当前仓库或当前仓库的下级模型',
                        okText: '知道了',
                        maskClosable: false,
                        centered: true
                    });
                    this.parentDepartmentDefaults = '';
                    return;
                }
                this.currentTrObjLoad = selectedOptions[selectedOptions.length - 1];
                this.curBarcodeDefaults = [];
                // 实时改变父级的父仓库ID
                if (this.btnName === 'addChild' || this.btnName === 'edit') {
                    this.editInfoFormObj.parent_warehouse_model_inner_sn =
                        this.currentTrObjLoad.warehouse_model_inner_sn;
                } else {
                    this.editInfoFormObj.parent_warehouse_model_inner_sn =
                        this.currentTrObjLoad.parent_warehouse_model_inner_sn || '';
                }
            } else {
                // 修改为根节点
                this.editInfoFormObj.parent_warehouse_model_inner_sn = '';
            }
        },
        // 获取模型类别的值
        getTypeIsFn(checkedTypeIs) {
            this.editInfoFormObj.type = parseFloat(checkedTypeIs);
            this.showModalParentDeparentNameFn();
        },
        // 获取仓库类别的值
        getWarehouseCategoryFn(value, label) {
            this.editInfoFormObj.warehouse_type_inner_sn = label.length > 0 ? value : '';
        },
        // 控制仓库类别的弹窗
        warehouseCategoryBtnFn() {
            // 让内项的表格单元行跟随外面选中
            if (this.warehouseCategory.length) {
                if (!isNaN(parseFloat(this.editInfoFormObj.warehouse_type_inner_sn))) {
                    this.warehouseCategoryCurrentTrObj = this.warehouseCategory.filter(
                        (item) =>
                            parseFloat(item.type_inner_sn) === parseFloat(this.editInfoFormObj.warehouse_type_inner_sn)
                    )[0];
                } else {
                    this.warehouseCategoryCurrentTrObj = this.warehouseCategory[0];
                }
            } else {
                this.warehouseCategoryCurrentTrObj = {};
            }

            this.warehouseCategoryModal.alert = true;
            this.$Utils.globalDragCenterFn('warehouse-category-modal');
        },
        // 检测表格是否有选中的的数据
        checkCurrentTrDataFn(isModalName) {
            switch (isModalName) {
                case 'architectureBtn': // 最外层的仓库建模的新增按钮
                    if (Object.keys(this.currentTrObj).length <= 0) {
                        this.$warning({
                            title: '操作提示',
                            content: this.$Utils.tips().noSelected,
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                        return false;
                    }
                    return true;
                case 'warehouseCategoryModal': // 最外层的仓库建模的新增按钮
                    if (Object.keys(this.warehouseCategoryCurrentTrObj).length <= 0) {
                        this.$warning({
                            title: '操作提示',
                            content: this.$Utils.tips().noSelected,
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                        return false;
                    }
                    return true;
            }
        },
        // 显示弹框的上级模型名称
        showModalParentDeparentNameFn() {
            /* 根据id显示中文默认值 */
            let aryDefaults = [];
            if (this.btnName === 'addChild') {
                if (this.currentTrObj.parent_warehouse_model_inner_sn) {
                    aryDefaults = this.currentTrObj.parentWarehouseModelInnerSn.map((item) => item + '');
                } else {
                    aryDefaults = [this.currentTrObj.parentWarehouseModelInnerSn + ''];
                }
            } else {
                if (
                    this.currentTrObj.parentWarehouseModelInnerSn &&
                    this.currentTrObj.parentWarehouseModelInnerSn.length
                ) {
                    aryDefaults = this.currentTrObj.parentWarehouseModelInnerSn.slice(0, -1).map((item) => item + '');
                }
            }
            this.parentDepartmentDefaults = [...aryDefaults];
        },
        // 顶部按钮的触发事件 btnName
        groupBtnFn(btnName, isModalName) {
            switch (isModalName) {
                case 'architectureBtn': // 最外层的仓库建模的新增按钮
                    this.btnName = btnName;
                    if (this.btnName !== 'addSameLevel' && !this.checkCurrentTrDataFn(isModalName)) return; // 除了新增同级分类按钮不需要选中表格中的某一行数据， 其它按钮都需要选中某一行才能进行下步操作
                    this.architectureInfoModalResetFn();
                    if (this.btnName !== 'remove') {
                        this.orgEmpDropdownListAPIFn();
                        this.orgDeptTopTreeAPIFn();
                        this.warehouseTypeUsableListAPIFn();

                        this.parentDepartmentsLen = 0; // 发送上级模型的API取值
                        this.warehouseParentTopListAPIFn(true);
                    }
                    if (this.btnName === 'addChild' || this.btnName === 'addSameLevel') {
                        this.showModalParentDeparentNameFn();
                    }

                    switch (btnName) {
                        case 'addChild':
                            this.architectureInfoModal = {
                                title: `${this.$route.meta.title} - 新增下级模型`,
                                btnTitle: '新增下级模型保存',
                                alert: true,
                                loading: false,
                                isShowStatus: false
                            };
                            this.isAddChildModel = true;
                            this.$Utils.globalDragCenterFn('architecture-category-modal');
                            break;
                        case 'addSameLevel':
                            this.architectureInfoModal = {
                                title: `${this.$route.meta.title} - 新增同级模型`,
                                btnTitle: '新增同级模型保存',
                                alert: true,
                                loading: false,
                                isShowStatus: false
                            };
                            this.isAddChildModel = false;
                            this.$Utils.globalDragCenterFn('architecture-category-modal');
                            break;
                        case 'edit':
                            this.warehouseInfoAPIFn(this.currentTrObj.warehouse_model_inner_sn);
                            this.architectureInfoModal = {
                                title: `${this.$route.meta.title} - 修改`,
                                btnTitle: '修改保存',
                                alert: true,
                                loading: false,
                                isShowStatus: true
                            };
                            this.$Utils.globalDragCenterFn('architecture-category-modal');
                            break;
                        case 'remove': {
                            const _this = this;
                            _this.$confirm({
                                title: '删除',
                                content: '确认要执行该操作吗？',
                                centered: true,
                                maskClosable: false,
                                onOk() {
                                    _this.warehouseDelAPIFn(_this.currentTrObj.warehouse_model_inner_sn, _this.datas);
                                    return new Promise((resolve, reject) => {
                                        setTimeout(Math.random() > 0.5 ? resolve : reject, 1000);
                                    }).catch(() => {});
                                }
                            });
                            break;
                        }
                    }
                    break;
                case 'warehouseCategoryModal':
                    this.warehouseCategoryBtnName = btnName;
                    if (this.warehouseCategoryBtnName !== 'add' && !this.checkCurrentTrDataFn(isModalName)) return;
                    switch (this.warehouseCategoryBtnName) {
                        case 'add':
                            this.warehouseCategoryEditInfoFormObj = {
                                name: ''
                            };
                            this.warehouseCategoryInfoModal = {
                                title: `${this.$route.meta.title} - 仓库类别 - 新增`,
                                alert: true,
                                loading: false
                            };
                            this.$Utils.globalDragCenterFn('warehouse-category-info-modal');
                            break;
                        case 'edit':
                            this.warehouseCategoryEditInfoFormObj = { ...this.warehouseCategoryCurrentTrObj };
                            this.warehouseCategoryInfoModal = {
                                title: `${this.$route.meta.title} - 仓库类别 - 修改`,
                                alert: true,
                                loading: false
                            };
                            this.$Utils.globalDragCenterFn('warehouse-category-info-modal');
                            break;
                        case 'remove': {
                            const _this = this;
                            _this.$confirm({
                                title: '删除',
                                content: '确认要执行该操作吗？',
                                centered: true,
                                maskClosable: false,
                                onOk() {
                                    _this.warehouseTypeDelAPIFn(_this.warehouseCategoryCurrentTrObj.type_inner_sn);
                                    return new Promise((resolve, reject) => {
                                        setTimeout(Math.random() > 0.5 ? resolve : reject, 1000);
                                    }).catch(() => {});
                                }
                            });
                            break;
                        }
                    }
                    break;
            }
        },
        onChangeFn(val) {
            this.plainOptions = val;
        },
        // 检测修改的表单信息校验
        checkEditInfoFormMsgFn(isModalName) {
            switch (isModalName) {
                case 'warehouseCategoryInfoModal': // 仓库类别新增的模态框
                    this.warehouseCategoryEditInfoFormObj.name = this.$Utils.trim(
                        this.warehouseCategoryEditInfoFormObj.name + ''
                    );
                    // 名称
                    if (!this.warehouseCategoryEditInfoFormObj.name) {
                        this.$warning({
                            title: '操作提示',
                            content: '请输入名称',
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                        return false;
                    }
                    // 名称 02
                    if (!this.$Utils.trim(this.warehouseCategoryEditInfoFormObj.name)) {
                        this.$warning({
                            title: '操作提示',
                            content: '请输入名称',
                            centered: true,
                            okText: '知道了',
                            maskClosable: false
                        });
                        this.warehouseCategoryEditInfoFormObj.name = this.$Utils.trim(
                            this.warehouseCategoryEditInfoFormObj.name
                        );
                        return false;
                    }
                    // 名称 03
                    if (this.$Utils.checkInputValFn(this.warehouseCategoryEditInfoFormObj.name)) {
                        this.$warning({
                            title: '操作提示',
                            content: '名称中请不要输入特殊字符',
                            centered: true,
                            okText: '知道了',
                            maskClosable: false
                        });
                        return false;
                    }
                    return true;
                // 仓库建模新增的模态框
                case 'architectureCategoryModal': {
                    // const mobilePhone = /^1\d{10}$/ // mobilePhone = /^1\d{10}$/

                    this.editInfoFormObj.name = this.$Utils.trim(this.editInfoFormObj.name);

                    // 类别为仓库的时候，不能新建子级仓库
                    if (
                        this.currentTrObj.parent_warehouse_model_inner_sn &&
                        parseInt(this.editInfoFormObj.type) === 0
                    ) {
                        this.$warning({
                            title: '操作提示',
                            content: '【仓库】是顶级模型，不能建立成子模型',
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                        this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
                        return false;
                    }

                    // 类别为仓库/储物柜的时候，不能新建同级仓库 【上级模型没有值肯定是新建顶级的上级模型】
                    if (
                        this.parentDepartmentDefaults.length <= 0 &&
                        !(parseInt(this.editInfoFormObj.type) === 0 || parseInt(this.editInfoFormObj.type) === 7)
                    ) {
                        this.$warning({
                            title: '操作提示',
                            content: '顶级模型只能是【仓库】',
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                        this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
                        return false;
                    }

                    // 下级模型不能新建类别为仓库
                    if (parseInt(this.editInfoFormObj.type) === 0 && this.btnName === 'addChild') {
                        this.$warning({
                            title: '操作提示',
                            content: '【仓库】是顶级模型，不能建立成子模型',
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                        this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
                        return false;
                    }

                    // 类别
                    if (isNaN(this.typeIsCur)) {
                        this.$warning({
                            title: '操作提示',
                            content: '请选择类别',
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                        this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
                        return false;
                    }

                    // 名称
                    if (!this.editInfoFormObj.name) {
                        this.$warning({
                            title: '操作提示',
                            content: '请输入名称',
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                        this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
                        return false;
                    }

                    // 编号校验
                    // if (!this.checkWarehouseModelFormMsgFn()) return false

                    // 面积有效取值的区间 【0 - 99999】
                    const areaMinNum = 0,
                        areaMaxNum = 99999,
                        areaRegNum = /^\d+(\.\d{1,2})?$/; // 两位小数

                    this.editInfoFormObj.proportion = this.$Utils.trim(this.editInfoFormObj.proportion + '');
                    if (
                        this.typeIsCur === '2' &&
                        this.editInfoFormObj.proportion &&
                        this.editInfoFormObj.proportion.length
                    ) {
                        if (isNaN(this.editInfoFormObj.proportion)) {
                            this.$warning({
                                title: '操作提示',
                                content: '面积只能是小数和正整数',
                                okText: '知道了',
                                maskClosable: false,
                                centered: true
                            });
                            this.editInfoFormObj.proportion = '';
                            this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
                            return false;
                        }
                        if (parseFloat(this.editInfoFormObj.proportion) < areaMinNum) {
                            this.$warning({
                                title: '操作提示',
                                content: '输入面积的值无效',
                                okText: '知道了',
                                maskClosable: false,
                                centered: true
                            });
                            this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
                            return false;
                        }
                        if (parseFloat(this.editInfoFormObj.proportion) > areaMaxNum) {
                            this.$warning({
                                title: '操作提示',
                                content: `面积的最大有效值不能大于${areaMaxNum}`,
                                okText: '知道了',
                                maskClosable: false,
                                centered: true
                            });
                            this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
                            return false;
                        }
                        if (!areaRegNum.test(this.editInfoFormObj.proportion)) {
                            this.$warning({
                                title: '操作提示',
                                content: '面积的小数范围为两位',
                                okText: '知道了',
                                maskClosable: false,
                                centered: true
                            });
                            this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
                            return false;
                        }
                    }

                    // 员工
                    let isAddFlag = this.parentDirectorsDatas.some(
                            (item) => isNaN(parseFloat(item.employee_inner_sn)) || isNaN(parseFloat(item.type))
                        ),
                        lenFlag = this.parentDirectorsDatas.length;
                    if (
                        (this.typeIsCur === '0' || this.typeIsCur === '1' || this.typeIsCur === '2') &&
                        lenFlag &&
                        isAddFlag
                    ) {
                        this.$warning({
                            title: '操作提示',
                            content: '请填写姓名和职务',
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                        this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[1];
                        return false;
                    }
                    if (
                        (this.typeIsCur === '0' || this.typeIsCur === '1' || this.typeIsCur === '2') &&
                        this.repeatEmployeeFlagFn()
                    ) {
                        this.$warning({
                            title: '操作提示',
                            content: '姓名不能重复',
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                        this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[1];
                        return false;
                    }
                    return true;
                }
            }
        },
        // 校验员工姓名是否有重复的
        repeatEmployeeFlagFn() {
            let hasRepeatObj = {},
                flag = false;
            for (let i = 0; i < this.parentDirectorsDatas.length; i++) {
                let employeeInnerSn = this.parentDirectorsDatas[i].employee_inner_sn;
                if (!hasRepeatObj[employeeInnerSn]) {
                    hasRepeatObj[employeeInnerSn] = employeeInnerSn;
                } else {
                    hasRepeatObj[employeeInnerSn] = employeeInnerSn;
                    flag = true;
                    break;
                }
            }
            return flag;
        },
        // 检测编号的信息校验
        checkWarehouseModelFormMsgFn() {
            this.editInfoFormObj.warehouse_model_sn = this.$Utils.trim(this.editInfoFormObj.warehouse_model_sn);

            // 编号
            if (!this.editInfoFormObj.warehouse_model_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入编号',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
                return false;
            }

            // 编号有效ASCII的区间 【0 - 127】
            let ASCIIMinNum = 0,
                ASCIIMaxNum = 127,
                flag = false;
            for (let i = 0; i < this.editInfoFormObj.warehouse_model_sn.length; i++) {
                if (
                    !flag &&
                    (this.editInfoFormObj.warehouse_model_sn.charCodeAt(i) < ASCIIMinNum ||
                        this.editInfoFormObj.warehouse_model_sn.charCodeAt(i) > ASCIIMaxNum)
                ) {
                    this.$warning({
                        title: '操作提示',
                        content: '编号不能输入特殊字符',
                        okText: '知道了',
                        maskClosable: false,
                        centered: true
                    });
                    flag = true;
                    this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
                    break;
                }
            }

            if (!flag && !this.saveBtnFlag) {
                this.$warning({
                    title: '操作提示',
                    content: '正在校验同一模型下的同级别【模型编号】是否有重复',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            if (!flag && this.barcodeRepeatFlag) {
                this.$warning({
                    title: '操作提示',
                    content: '同一模型下的同级别【模型编号】不允许重复',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                flag = true;
                this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[0];
            }
            return !flag;
        },
        // 检测并设置修改过的修改信息内容字段
        checkEditInfoFormObjFn() {
            let editInfoFlag = true;
            this.editInfoFormNewObj = {};
            for (let server in this.editInfoFormObj) {
                let serverItem = this.editInfoFormObj[server];
                for (let old in this.editInfoFormOldObj) {
                    let oldItem = this.editInfoFormOldObj[old];
                    if (server === old && serverItem + '' !== oldItem + '') {
                        this.editInfoFormNewObj = {
                            ...this.editInfoFormNewObj,
                            [server]: serverItem, // 新值
                            [server + '_old']: oldItem // 旧值
                        };
                        editInfoFlag = false;
                    }
                }
            }

            if (this.checkEmployeesFn()) {
                // 没有改
                return editInfoFlag;
            } else {
                return this.editEmployeesInfoFlag;
            }
        },
        // 判断仓库人员改过了没有
        checkEmployeesFn() {
            this.editEmployeesInfoFlag = true;
            // 判断仓库人员是否有改过
            if (this.typeIsCur === '0' || this.typeIsCur === '1' || this.typeIsCur === '2') {
                switch (this.btnName) {
                    case 'addChild':
                    case 'addSameLevel':
                        if (this.parentDirectorsDatas.length <= 0) {
                            this.employees = [];
                            return;
                        }
                        this.formatEmployeesFn();
                        this.editEmployeesInfoFlag = false;
                        break;
                    case 'edit':
                        if (this.parentDirectorsDatas.length !== this.employees_old.length) {
                            this.formatEmployeesFn();
                            this.editEmployeesInfoFlag = false;
                        } else {
                            this.parentDirectorsDatas.forEach((item) => {
                                this.employees_old.forEach((cItem) => {
                                    if (
                                        item.employee_inner_sn.toString() !== cItem.employee_inner_sn.toString() ||
                                        item.type.toString() !== cItem.type.toString()
                                    ) {
                                        this.formatEmployeesFn();
                                        this.editEmployeesInfoFlag = false;
                                    }
                                });
                            });
                        }
                        break;
                }
            }
            return this.editEmployeesInfoFlag;
        },
        // 给仓库人员格式化成后端要的list
        formatEmployeesFn() {
            this.employees = this.parentDirectorsDatas.map((item) => {
                return {
                    employee_inner_sn: item.employee_inner_sn,
                    type: item.type
                };
            });
        },
        // 取消按钮
        architectureInfoModalCancelFn(isModalName) {
            switch (isModalName) {
                case 'warehouseCategoryInfoModal': // 仓库类别新增的模态框
                    this.warehouseCategoryInfoModal = {
                        title: `${this.$route.meta.title} - 仓库类别 - 修改`,
                        alert: false,
                        loading: false
                    };
                    break;
                case 'architectureCategoryModal': // 仓库建模新增的模态框
                    this.architectureInfoModal = {
                        title: `${this.$route.meta.title} - 信息`,
                        btnTitle: '保存',
                        alert: false,
                        loading: false,
                        isShowStatus: false
                    };
                    this.editInfoFormNewObj = {};
                    this.isAddChildModel = false;
                    break;
            }
        },
        // 确认按钮
        architectureInfoModalSaveFn(isModalName) {
            switch (isModalName) {
                case 'warehouseCategoryInfoModal': // 仓库类别新增的模态框
                    if (!this.checkEditInfoFormMsgFn(isModalName)) return; // 前端格式校验
                    if (this.warehouseCategoryEditInfoFormObj.name === this.warehouseCategoryCurrentTrObj.name) {
                        this.architectureInfoModalCancelFn('warehouseCategoryInfoModal');
                        return;
                    } // 说明没有改
                    this.warehouseCategoryInfoModal.loading = true;
                    switch (this.warehouseCategoryBtnName) {
                        case 'add':
                            this.warehouseTypeAddAPIFn();
                            break;
                        case 'edit':
                            this.warehouseTypeEditAPIFn();
                            break;
                    }
                    break;
                case 'architectureCategoryModal': // 仓库建模新增的模态框
                    if (!this.checkEditInfoFormMsgFn(isModalName)) return; // 前端格式校验
                    if (this.architectureInfoModal.isShowStatus && this.checkEditInfoFormObjFn()) {
                        // 修改按钮触发的保存要处理后端传值校验
                        this.architectureInfoModalCancelFn('architectureCategoryModal');
                        return;
                    }
                    if (this.btnName === 'addChild' || this.btnName === 'addSameLevel') {
                        this.checkEmployeesFn();
                    }

                    this.architectureInfoModal.loading = true;
                    if (this.architectureInfoModal.isShowStatus) {
                        // 修改按钮
                        this.provinceDefaultsFlag = true;
                        this.warehouseEditAPIFn(this.editInfoFormNewObj);
                    } else {
                        if (this.tabsActiveKey === 'list') {
                            if (Object.keys(this.currentTrObjLoad).length > 0) {
                                // 说明修改过上级模型
                                // 判断是否有加载过子级菜单
                                if (this.currentTrObjLoad.loadingChildrenFlag) {
                                    // 加载过
                                    this.warehouseAddAPIFn();
                                } else {
                                    // 没有加载过
                                    if (this.btnName === 'addSameLevel') {
                                        this.warehouseAddAPIFn();
                                        return;
                                    }
                                    this.expandFn(true, this.currentTrObjLoad, true);
                                }
                            } else {
                                // 判断是否有加载过子级菜单
                                if (this.currentTrObj.loadingChildrenFlag) {
                                    // 加载过
                                    this.warehouseAddAPIFn();
                                } else {
                                    // 没有加载过
                                    if (this.btnName === 'addSameLevel') {
                                        this.warehouseAddAPIFn();
                                        return;
                                    }
                                    this.expandFn(true, this.currentTrObj, true);
                                }
                            }
                        } else {
                            this.warehouseAddAPIFn();
                        }
                    }
                    break;
            }
        },
        // 递归加上索引
        treeDatasIndexAryFn(datasOrigin, isAppendDatas, id) {
            // datasOrigin: 源数据  isAppendDatas: 将数据放在哪里  id: 当前item的id【用来设置thi.currentTrObj】
            const _this = this;
            // 子级递归
            function childrenIndexFn(
                list,
                pIndexAry,
                parentDepartmentNameOld,
                parentWarehouseModelInnerSnOld,
                pItemDisabledFlag,
                curBarcode
            ) {
                if (!pIndexAry || !parentDepartmentNameOld) return;
                return list.map((cItem, cIndex) => {
                    let cItemDisabledFlag =
                        _this.btnName === 'edit' &&
                        (pItemDisabledFlag || (isAppendDatas === 'parentDirectors' && id === cItem.employee_inner_sn));
                    if (curBarcode.length) {
                        if (cItem.barcode) {
                            cItem.curBarcode = [...curBarcode, cItem.warehouse_model_sn];
                        } else {
                            cItem.curBarcode = [...curBarcode];
                        }
                    } else {
                        cItem.curBarcode = [];
                    }
                    if (cItem.children && cItem.children.length > 0) {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn || cItem.warehouse_model_inner_sn,
                            activeClass: '',
                            status_text: _this.statusAry[parseFloat(cItem.status)],
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            parentWarehouseModelInnerSn: [
                                ...parentWarehouseModelInnerSnOld,
                                cItem.warehouse_model_inner_sn
                            ],
                            disabled:
                                cItemDisabledFlag ||
                                (isAppendDatas === 'parentDirectors' && parseInt(cItem.status) <= 0),
                            value: (
                                cItem.employee_inner_sn ||
                                cItem.department_inner_sn ||
                                cItem.warehouse_model_inner_sn
                            ).toString(),
                            label: cItem.name,
                            isLeaf: false,
                            children: childrenIndexFn(
                                cItem.children,
                                [...pIndexAry, cIndex],
                                [...parentDepartmentNameOld, cItem.name],
                                [...parentWarehouseModelInnerSnOld, cItem.warehouse_model_inner_sn],
                                cItemDisabledFlag,
                                cItem.curBarcode
                            )
                        };
                        if (obj.key === _this.infoKey || parseFloat(id) === parseFloat(obj.warehouse_model_inner_sn)) {
                            // 新增下级同级的时候选中新增的那一条
                            _this.currentTrObj = obj;
                        }
                        return obj;
                    } else {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn || cItem.warehouse_model_inner_sn,
                            activeClass: '',
                            status_text: _this.statusAry[parseFloat(cItem.status)],
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            parentWarehouseModelInnerSn: [
                                ...parentWarehouseModelInnerSnOld,
                                cItem.warehouse_model_inner_sn
                            ],
                            disabled:
                                cItemDisabledFlag ||
                                (isAppendDatas === 'parentDirectors' && parseInt(cItem.status) <= 0),
                            value: (
                                cItem.employee_inner_sn ||
                                cItem.department_inner_sn ||
                                cItem.warehouse_model_inner_sn
                            ).toString(),
                            label: cItem.name,
                            children: cItem.children
                        };
                        if (obj.key === _this.infoKey || parseFloat(id) === parseFloat(obj.warehouse_model_inner_sn)) {
                            // 新增下级同级的时候选中新增的那一条
                            _this.currentTrObj = obj;
                        }
                        return obj;
                    }
                });
            }
            // 父级
            let datasOriginNew = datasOrigin.map((item, index) => {
                let itemDisabledFlag =
                    _this.btnName === 'edit' && isAppendDatas === 'parentDirectors' && id === item.employee_inner_sn;
                if (item.warehouse_model_sn) {
                    item.curBarcode = [item.warehouse_model_sn];
                } else {
                    item.curBarcode = [];
                }
                if (item.children && item.children.length > 0) {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn || item.warehouse_model_inner_sn,
                        activeClass: '',
                        status_text: _this.statusAry[parseFloat(item.status)],
                        parentDepartmentName: [item.name],
                        parentWarehouseModelInnerSn: [item.warehouse_model_inner_sn],
                        disabled:
                            itemDisabledFlag || (isAppendDatas === 'superiorDepartments' && parseInt(item.status) <= 0),
                        value: (
                            item.employee_inner_sn ||
                            item.department_inner_sn ||
                            item.warehouse_model_inner_sn
                        ).toString(),
                        label: item.name,
                        isLeaf: false,
                        children: childrenIndexFn(
                            item.children,
                            [index],
                            [item.name],
                            [item.warehouse_model_inner_sn],
                            itemDisabledFlag,
                            item.curBarcode
                        )
                    };
                    if (obj.key === _this.infoKey || parseFloat(id) === parseFloat(obj.warehouse_model_inner_sn)) {
                        // 新增下级同级的时候选中新增的那一条
                        _this.currentTrObj = obj;
                    }
                    return obj;
                } else {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn || item.warehouse_model_inner_sn,
                        activeClass: '',
                        status_text: _this.statusAry[parseFloat(item.status)],
                        parentDepartmentName: [item.name],
                        parentWarehouseModelInnerSn: [item.warehouse_model_inner_sn],
                        disabled:
                            itemDisabledFlag || (isAppendDatas === 'superiorDepartments' && parseInt(item.status) <= 0),
                        value: (
                            item.employee_inner_sn ||
                            item.department_inner_sn ||
                            item.warehouse_model_inner_sn
                        ).toString(),
                        label: item.name,
                        children: item.children
                    };
                    if (obj.key === _this.infoKey || parseFloat(id) === parseFloat(obj.warehouse_model_inner_sn)) {
                        // 新增下级同级的时候选中新增的那一条
                        _this.currentTrObj = obj;
                    }
                    return obj;
                }
            });

            if (isAppendDatas) {
                switch (isAppendDatas) {
                    case 'parentDirectors': // 上级主管
                        this.parentDirectors = [...datasOriginNew];
                        break;
                    // case 'platformDeptTree': // 架构图
                    //     this.architectureAllTrees.children = [...datasOriginNew]
                    //     break
                    case 'superiorDepartments': // 专属部门
                        this.superiorDepartments = [...datasOriginNew];
                }
            } else {
                switch (this.tabsActiveKey) {
                    case 'list':
                        this.datas = datasOriginNew;
                        break;
                }
            }
        },
        // 表格的高亮样式显示
        rowClassFn(record) {
            if (record.key === this.currentTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        warehouseCategoryRowClassFn(record) {
            if (record.key === this.warehouseCategoryCurrentTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        parentDirectorsRowClassFn(record) {
            if (record.key === this.parentDirectorsCurrentTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        // 生成条码的方法
        createJsBarcodeFn() {
            if (!this.checkWarehouseModelFormMsgFn()) return false;
            this.showJsBarcodeFn();
        },
        // 要展示条码的方法
        showJsBarcodeFn(barcodeOld) {
            // 条码规则：父级仓库warehouse_model_sn + 当前级的warehouse_model_sn【编号】
            let barCode = '',
                barCodeMaxLen = 50; // 组合后的编号最大长度
            if (barcodeOld || this.curBarcodeDefaults.length) {
                // 后端给的默认值
                barCode = this.curBarcodeDefaults.join('') + this.editInfoFormObj.warehouse_model_sn;
            } else {
                if (this.currentTrObjLoad.curBarcode && this.currentTrObjLoad.curBarcode.length) {
                    barCode = this.currentTrObjLoad.curBarcode.join('') + this.editInfoFormObj.warehouse_model_sn;
                } else {
                    if (this.btnName === 'addChild') {
                        barCode = this.currentTrObj.curBarcode.join('') + '' + this.editInfoFormObj.warehouse_model_sn;
                    } else if (this.btnName === 'addSameLevel') {
                        let length =
                            Object.keys(this.currentTrObj).length > 0 ? this.currentTrObj.curBarcode.length : 0;
                        if (length > 1) {
                            barCode =
                                this.currentTrObj.curBarcode.slice(0, length - 1).join('') +
                                this.editInfoFormObj.warehouse_model_sn;
                        } else {
                            barCode = this.editInfoFormObj.warehouse_model_sn;
                        }
                    } else {
                        barCode = this.editInfoFormObj.warehouse_model_sn;
                    }
                }
            }

            if (barCode && barCode.length > barCodeMaxLen) {
                this.$warning({
                    title: '操作提示',
                    content: `上级模型和编号组合后的长度超出了${barCodeMaxLen}位字符`,
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }

            JsBarcode('#jsBarcodeImg', barCode, {
                format: 'CODE128',
                lineColor: '#000',
                height: 74,
                marginBottom: 5,
                fontSize: 26,
                displayValue: true
            });
            if (!barcodeOld) {
                this.editInfoFormObj.barcode = barCode;
            }
        },
        // 修改员工 - 职务 - 删除 - 新增操作
        parentDirectorPositionFn(record) {
            this.parentDirectorsDatas = this.parentDirectorsDatas.filter((item) => item.key !== record.key);
            this.parentDirectorsCurrentTrObj = this.parentDirectorsDatas.length
                ? this.parentDirectorsDatas[this.parentDirectorsDatas.length - 1]
                : {};
        },
        parentDirectorAddFn() {
            if (
                (this.typeIsCur === '0' || this.typeIsCur === '1' || this.typeIsCur === '2') &&
                this.repeatEmployeeFlagFn()
            ) {
                this.$warning({
                    title: '操作提示',
                    content: '姓名不能重复',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                this.tabsArchitectureInfoModalKey = this.tabsArchitectureList[1];
                return false;
            }

            let isAddFlag = this.parentDirectorsDatas.every(
                    (item) => !isNaN(parseFloat(item.employee_inner_sn)) && !isNaN(parseFloat(item.type))
                ),
                lenFlag = this.parentDirectorsDatas.length <= 0;
            if (lenFlag || isAddFlag) {
                let curItem = {
                    key: new Date().getTime(),
                    activeClass: '',
                    employee_inner_sn: '',
                    name: '', // 姓名
                    status: 1,
                    type: '' // 职务
                };
                this.parentDirectorsDatas.push(curItem);
                this.parentDirectorsCurrentTrObj = curItem;
                return;
            }
            this.$warning({
                title: '操作提示',
                content: '请填写姓名和职务',
                okText: '知道了',
                maskClosable: false,
                centered: true
            });
        },
        // 仓库类别 - 新增 - 保存 - 取消 - 编辑操作
        // 判断是否是可编辑的状态 【新增的弹框没有内容】
        checkWarehouseCategoryAddContentNoFn() {
            return this.warehouseCategory.some(
                (item) => !this.$Utils.trim(item.name_edit) && !this.$Utils.trim(item.name) && !item.editFlag
            );
        },
        // 判断是否是可编辑的状态 【新增的弹框有内容，但是没有保存】
        checkWarehouseCategoryAddContentYesFn() {
            return this.warehouseCategory.some((item) => !item.type_inner_sn && !item.editFlag);
        },
        changeEditFn(record) {
            record.name = record.name_edit;
        },
        // 数据的状态重置
        warehouseCategoryTypeResetFn(flag) {
            this.warehouseCategory.forEach((item, index) => {
                item.name_edit = item.name;
                item.editFlag = true;
                if (flag && !item.type_inner_sn) {
                    // 清除空数据
                    this.warehouseCategory.splice(index, 1);
                }
            });
        },
        // 关闭弹窗
        closeWarehouseCategoryModalFn() {
            this.warehouseCategoryModal.alert = false;
            this.warehouseCategoryTypeResetFn('reset');
        },
        warehouseCategoryTypeFn(btnName, record) {
            this.warehouseCategoryBtnName = btnName;
            if (btnName === 'save' && this.checkWarehouseCategoryAddContentNoFn()) return;
            if (
                (btnName === 'edit' || btnName === 'remove') &&
                (this.checkWarehouseCategoryAddContentNoFn() || this.checkWarehouseCategoryAddContentYesFn())
            )
                return;
            switch (btnName) {
                case 'save':
                    if (!isNaN(parseFloat(record.type_inner_sn))) {
                        if (record.name === record.name_edit) {
                            // 没有改
                            this.warehouseCategoryTypeResetFn();
                        } else {
                            // 修改过
                            this.warehouseCategoryLoading = true;
                            this.warehouseCategoryEditInfoFormObj.org_id = record.org_id;
                            this.warehouseCategoryEditInfoFormObj.type_inner_sn = record.type_inner_sn;
                            this.warehouseCategoryEditInfoFormObj.name = record.name_edit;
                            this.warehouseTypeEditAPIFn();
                        }
                    } else {
                        // 新增
                        this.warehouseCategoryLoading = true;
                        this.warehouseCategoryEditInfoFormObj.name = record.name_edit;
                        this.warehouseTypeAddAPIFn();
                    }
                    break;
                case 'remove':
                    if (!isNaN(parseFloat(record.type_inner_sn))) {
                        this.warehouseCategoryTypeResetFn();
                        this.editInfoFormObj.warehouse_type_inner_sn = '';
                    }
                    this.groupBtnFn(btnName, 'warehouseCategoryModal');
                    break;
                case 'edit':
                    this.warehouseCategory = this.warehouseCategory.map((item, index) => {
                        if (item.key === record.key) {
                            item.editFlag = false;
                            this.inputAutoFocusFn(index);
                        } else {
                            item.editFlag = true;
                        }
                        return item;
                    });
                    break;
                case 'cancel':
                    if (!isNaN(parseFloat(record.type_inner_sn))) {
                        this.warehouseCategoryTypeResetFn();
                    } else {
                        this.warehouseCategory = this.warehouseCategory.filter((item) => item.key !== record.key);
                        if (record.key === this.warehouseCategoryCurrentTrObj.key) {
                            this.warehouseCategoryCurrentTrObj = this.warehouseCategory.length
                                ? this.warehouseCategory[this.warehouseCategory.length - 1]
                                : {};
                        }
                    }
                    break;
                case 'add': {
                    if (this.warehouseCategory.some((item) => !item.type_inner_sn)) {
                        this.$warning({
                            title: '操作提示',
                            content: '请保存仓库类别，再进行操作',
                            okText: '知道了',
                            maskClosable: false,
                            centered: true
                        });
                        break;
                    }
                    if (this.warehouseCategory.every((item) => item.name.length > 0)) {
                        this.warehouseCategoryTypeResetFn();
                    }
                    let isAddFlag = this.warehouseCategory.every((item) => item.name.length > 0),
                        lenFlag = this.warehouseCategory.length <= 0;
                    if (lenFlag || isAddFlag) {
                        let curItem = {
                            key: new Date().getTime(),
                            title: '',
                            value: '',
                            activeClass: '',
                            status_text: this.statusAry[1],
                            org_id: '',
                            status: 1,
                            type_inner_sn: '',
                            name: '',
                            name_edit: '', // 编辑
                            editFlag: false // 是否可编辑的标识 - 新增的直接进入编辑状态
                        };
                        this.warehouseCategory.push(curItem);
                        this.warehouseCategoryCurrentTrObj = curItem;
                        this.warehouseCategoryBtnName = '';
                        this.inputAutoFocusFn(this.warehouseCategory.length - 1);
                        return;
                    }
                    this.$warning({
                        title: '操作提示',
                        content: '请填写仓库类别的名称',
                        okText: '知道了',
                        maskClosable: false,
                        centered: true
                    });
                    break;
                }
            }
        },
        // input自动获取焦点
        inputAutoFocusFn(index) {
            this.$nextTick(() => {
                document.getElementsByClassName('input-auto-focus')[index].focus();
            });
        },
        // toolTips位置纠正 - 配合【父级的 position：relative】
        getPopupContainer(trigger) {
            return trigger.parentElement;
        },
        // 失去焦点检测编号是否有重复的值
        blurCheckRepeatFn() {
            if (this.editInfoFormObj.warehouse_model_sn) {
                this.saveBtnFlag = false;
                this.barcodeRepeatFlag = false;
                this.warehouseCheckAPIFn();
            }
        },
        // 格式化仓库值
        formatTypeIsFn(type) {
            let title = '';
            if (typeof type !== 'undefined') {
                for (let i = 0; i < this.typeIs.length; i++) {
                    let item = this.typeIs[i];
                    if (item.key === parseInt(type)) {
                        title = item.title;
                    }
                }
            } else {
                title = type;
            }
            return title;
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 196;
            return clientHeight - topHeight;
        },
        scrollYWarehouseCategoryNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 432;
            return clientHeight - topHeight;
        },
        scrollYParentDirectorsNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 494;
            return clientHeight - topHeight;
        },
        isShowSuperiorModel() {
            // 是【一级/顶级】模型隐藏掉上级模型
            return Object.keys(this.currentTrObj).length && !this.currentTrObj.isFirstLevelWarehouseModel;
        }
    },
    created() {
        this.warehouseParentTopListAPIFn();
    }
};
</script>

<style lang="less" scoped>
.modal-form-input-scoped {
    .modal-form-input {
        .label {
            width: 80px;
        }

        .warehouse-category {
            display: flex;
            flex-direction: row;
            flex: 1;

            .input {
                display: flex;
                flex: 1;
            }

            .ant-btn {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 24px;
                height: 24px;
                padding: 0;
                margin-left: 6px;
                font-weight: normal;
            }
        }
    }
}

// 条码图片
.center {
    display: flex;
    align-items: center;
    justify-content: center;
}

.barcode-img-wrap {
    .input {
        &:extend(.center);
        height: 74px;
    }
    .barcode-img {
        display: flex;
        flex: 1;
        min-height: 74px;

        img {
            max-width: 100%;
        }

        .no-barcode-img {
            position: relative;
            display: block;
            min-width: 100%;
            min-height: 74px;
            background: #f5f5f5;

            &:before {
                content: '条码生成区';
                &:extend(.center);
                height: 100%;
                font-size: 16px;
                color: #d9d9d9;
            }
        }
    }

    .btn-wrap {
        &:extend(.center);
        flex-direction: column;
        color: #7b8ba0;
        padding-left: 10px;
        cursor: pointer;

        &:hover {
            i {
                color: #1890ff;
            }
        }

        i {
            font-size: 24px;
            padding-right: 0;
        }

        span {
            font-size: 12px;
        }
    }
}
</style>
