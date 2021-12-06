<template>
    <and-page-layout class="scoped scoped-white return-sheets">
        <template slot="breadcrumb">
            <a-breadcrumb style="float: left">
                <a-breadcrumb-item>采购管理</a-breadcrumb-item>
                <a-breadcrumb-item>采购退货</a-breadcrumb-item>
            </a-breadcrumb>
        </template>
        <template slot="tabs-content">
            <and-tabs-content :panes="panes" :cur="curTab" @changeTabFn="changeTabFn">
                <template slot="atc-header">
                    <a-row class="atc-header" v-if="curTab === 'add'">
                        <a-col :span="24">
                            <h2>{{ orgName }}&nbsp;&nbsp;采购退货单</h2>
                        </a-col>
                        <a-col :span="8">
                            <a-col :span="6" style="margin-top: 1px">
                                <span><em class="em red">*</em>&nbsp;{{ companyName }}</span>
                            </a-col>
                            <a-col :span="15">
                                <!-- <a-input
                                    ref="relevantInstitutions"
                                    class="input"
                                    type="text"
                                    v-model="addInfoModalForm.organization_name"
                                    @keydown.enter="relevantInstitutionsKeyDownFn"
                                    @click="openChooseTb"
                                    placeholder=""
                                /> -->
                                <a-select
                                    :disabled="editable.flag"
                                    mode="default"
                                    show-search
                                    style="width: 100%"
                                    class="focus-a-select"
                                    :value="addInfoModalForm.organization_name"
                                    :filter-option="false"
                                    :show-arrow="false"
                                    :not-found-content="null"
                                    :default-active-first-option="false"
                                    @search="searchGrnOrgList"
                                    @change="changeGrnOrg"
                                >
                                    <a-select-option v-for="d in grnOrgData" :key="d.key" :value="d.key">
                                        {{ d.name }}
                                    </a-select-option>
                                    <a-select-option value="search-more">
                                        <b style="color: #1890ff">搜索更多...</b>
                                    </a-select-option>
                                    <a-select-option value="tips">
                                        <i>提示：输入文字搜索</i>
                                    </a-select-option>
                                </a-select>
                            </a-col>
                        </a-col>
                        <a-col :span="8">
                            <a-col :span="6" style="margin-top: 1px">
                                <span><em class="em red">*</em>&nbsp;仓库:</span>
                            </a-col>
                            <a-col :span="15">
                                <a-tree-select
                                    ref="warehouseFocus"
                                    treeNodeFilterProp="title"
                                    :showSearch="true"
                                    class="input"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    :treeData="wareHouseFilterData"
                                    v-model="addInfoModalForm.warehouse_inner_sn"
                                    treeDefaultExpandAll
                                    @focus="clickAddInfoModalWarehouseFn"
                                    @change="changeAddInfoModalWarehouseFn"
                                    style="min-width: 120px"
                                >
                                </a-tree-select>
                            </a-col>
                        </a-col>
                        <a-col :span="8">
                            <a-col :span="6" style="margin-top: 1px">
                                <span><em class="em red">*</em>&nbsp;部门:</span>
                            </a-col>
                            <a-col :span="15">
                                <a-tree-select
                                    treeNodeFilterProp="title"
                                    :showSearch="true"
                                    class="input"
                                    :value="addInfoModalForm.department_inner_sn"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    :treeData="superiorDepartments"
                                    treeDefaultExpandAll
                                    @focus="clickSuperiorDepartmentsFn"
                                    @change="changeSuperiorDepartmentsFn"
                                    style="min-width: 120px"
                                >
                                </a-tree-select>
                            </a-col>
                        </a-col>
                    </a-row>
                </template>
                <template slot="atc-pane">
                    <a-row class="atc-pane">
                        <and-sheet-acts
                            :data="actColumns"
                            :pageName="pageName"
                            :curTab="curTab"
                            :widths="widths"
                            @renderPage="changeTabFn"
                        >
                        </and-sheet-acts>
                        <and-sheet
                            :columns="columns"
                            :data="addData"
                            :widths="widths"
                            :menu="menu"
                            :dropdownData="venTableData"
                            :totalFormat="totalFormat"
                            @searchComData="searchComData"
                            @chooseComData="chooseComData"
                            @changeWidths="changeWidths"
                            @clickedMenu="clickedMenu"
                            @multiSelected="multiSelected"
                            @scrollToEnd="sheetScrollToEnd"
                            @searchSheet="searchSheetFn"
                            @addNewRow="addNewSheetRow"
                        >
                        </and-sheet>
                    </a-row>
                </template>
                <template slot="atc-footer">
                    <a-row class="atc-footer" v-if="curTab === 'add'">
                        <a-row style="margin: 10px 0">
                            <a-col :span="8">
                                <a-col :span="6" style="margin-top: 1px">
                                    <span><em class="em red">*</em>&nbsp;经手人</span>
                                </a-col>
                                <a-col :span="15">
                                    <a-tree-select
                                        treeNodeFilterProp="title"
                                        show-search
                                        :treeData="handlerNameList"
                                        v-model="addInfoModalForm.handler_inner_sn"
                                        @focus="clickHandlerNameSnFn"
                                        @change="changeHandlerNameSnFn"
                                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                        treeDefaultExpandAll
                                        class="input"
                                    >
                                    </a-tree-select>
                                </a-col>
                            </a-col>
                            <a-col :span="8">
                                <a-col :span="6" style="margin-top: 1px">
                                    <span><em class="em red">*</em>&nbsp;仓库人员</span>
                                </a-col>
                                <a-col :span="15">
                                    <a-tree-select
                                        treeNodeFilterProp="title"
                                        searchPlaceholder=""
                                        show-search
                                        allow-clear
                                        :treeData="warehouseOperatorList"
                                        v-model="addInfoModalForm.warehouse_operator_inner_sn"
                                        :disabled="isEditDisabledAddInfo || !isIncludedInWarehouseList"
                                        @focus="clickWarehouseOperatorFn"
                                        @change="changeWarehouseOperatorNameSnFn"
                                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                        treeDefaultExpandAll
                                        class="input"
                                    >
                                    </a-tree-select>
                                </a-col>
                            </a-col>
                            <a-col :span="8">
                                <a-col :span="12" style="margin-top: 1px">
                                    <span class="label" style="width: auto">制单人</span>
                                    <span>{{ making_people_name || '-' }}</span>
                                </a-col>
                                <a-col :span="12">
                                    <span class="label" style="width: auto">审核人</span>
                                    <span>{{ auditor_name || '-' }}</span>
                                </a-col>
                            </a-col>
                            <a-col :span="24" style="margin-top: 5px">
                                <a-input
                                    placeholder="备注"
                                    v-model="addInfoModalForm.description"
                                    class="input"
                                    @click="handleResetMultipleFocus"
                                />
                            </a-col>
                        </a-row>
                        <a-row style="margin-top: 10px">
                            <a-col :span="6" style="text-align: left">
                                <a-button
                                    key="print"
                                    :disabled="!printButtonEnabled"
                                    @click="addInfoModalBotFn('print')"
                                >打印</a-button>
                                <a-button
                                    key="exportExcel"
                                    @click="exportExcel('xTableAddInfo')"
                                    style="margin-left: 5px"
                                >导出Excel</a-button>
                            </a-col>
                            <a-col
                                v-if="isIncludedInWarehouseList"
                                :offset="12"
                                :span="6"
                                style="text-align: right"
                            >
                                <!-- <a-button
                                    key="close"
                                    @click="addInfoModalBotFn('close')"
                                >{{ addInfoModalTextComputed }}</a-button> -->
                                <a-button
                                    v-show="isClickRedDashedOrder || addInfoModalForm.status === 0 || btnName === 'add'"
                                    key="save"
                                    type="primary"
                                    :loading="addInfoModal.saveLoading"
                                    @click="addInfoModalBotFn('save')"
                                    style="margin-left: 5px"
                                >保存</a-button>
                                <a-button
                                    v-show="isClickRedDashedOrder || addInfoModalForm.status === 0 || btnName === 'add'"
                                    key="audit"
                                    type="primary"
                                    :loading="addInfoModal.auditLoading"
                                    @click="addInfoModalBotFn('audit')"
                                    style="margin-left: 5px"
                                >审核</a-button>
                                <a-button
                                    v-show="addInfoModalForm.status === 1"
                                    key="godownEntry"
                                    type="primary"
                                    :loading="addInfoModal.godownEntryLoading"
                                    @click="addInfoModalBotFn('godownEntry')"
                                    style="margin-left: 5px"
                                >{{ setOutPutButtonTextComputed }}</a-button>
                            </a-col>
                            <a-col
                                v-else
                                :offset="12"
                                :span="6"
                                style="text-align: right"
                            >
                                <a-button
                                    key="close"
                                    @click="addInfoModalBotFn('close')"
                                    style="margin-left: 5px"
                                >关闭</a-button>
                            </a-col>
                        </a-row>
                    </a-row>
                </template>
            </and-tabs-content>
        </template>
        <template slot="modals">
            <!-- 储位分配单添加产品 -->
            <a-modal
                v-model="tableHeadModal.alert"
                :centered="true"
                forceRender
                :style="{ 'pointer-events': 'auto', 'z-index': '11 !important' }"
                :maskStyle="{ backgroundColor: '#ff4f03' }"
                class="scoped modal-form-input-scoped modal-storage-add global-drag"
                :width="800"
                :zIndex="11"
            >
                <div v-globalDrag="{ el: 'modal-storage-add' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ tableHeadModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <ul class="footer-btn-list">
                        <li> </li>
                        <li>
                            <div>
                                <a-button key="close" @click="tableHeadModal.alert = false"> 关闭 </a-button>
                                <a-button key="save" type="primary" @click="stoAddListSave"> 添加 </a-button>
                            </div>
                        </li>
                    </ul>
                </template>
                <div class="modal-form-input" style="height: 320px">
                    <div
                        class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                        style="padding-bottom: 0; margin-top: 0; z-index: 5 !important"
                    >
                        <vxe-grid
                            ref="stoAddListTable"
                            size="small"
                            :data="addListAllDatas"
                            :loading="storageAllocatLoading"
                            :height="280 - ipadHeight"
                            border
                            resizable
                            auto-resize
                            show-overflow
                            show-header-overflow
                            highlight-current-row
                            align="center"
                            :edit-config="{ trigger: 'click', mode: 'row', showStatus: true }"
                            @select-change="selectChangeEvent"
                            @select-all="selectAllEvent"
                            @cell-dblclick="DblClick_event"
                        >
                            <vxe-table-column type="checkbox" title="" width="60" :resizable="false" fixed="left">
                            </vxe-table-column>
                            <vxe-table-column
                                field="product_name"
                                title="产品"
                                width="200"
                                :cell-render="{ name: 'product_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="120"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="product_vendor_name"
                                title="厂商/经销商"
                                width="120"
                                :cell-render="{ name: 'product_vendor_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="batch_number"
                                title="批号"
                                width="72"
                                :cell-render="{ name: 'batch_number' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="quantity"
                                title="应放数量"
                                width="90"
                                :cell-render="{ name: 'quantity' }"
                            >
                            </vxe-table-column>
                            <vxe-table-column
                                field="unit"
                                title="包装单位"
                                width="90"
                                :cell-render="{ name: 'unit' }"
                            ></vxe-table-column>

                            <template v-slot:empty>
                                <div class="dblclick-wrap">
                                    <span>暂无数据</span>
                                </div>
                            </template>
                        </vxe-grid>
                    </div>
                </div>
            </a-modal>
            <!-- 储位分配单添加产品 -->

            <!-- 储位分配单区域开始 为了修复bug将其位置调上 -->
            <a-modal
                v-model="storageAllocatModal.alert"
                :centered="true"
                :maskClosable="false"
                :mask="modalmask"
                :destroyOnClose="true"
                :style="{ 'pointer-events': 'auto', 'z-index': '10 !important' }"
                :maskStyle="{ backgroundColor: '#ff4f03' }"
                class="scoped modal-form-input-scoped modal-storage-allocation-sheet global-drag"
                :width="1210"
                :zIndex="10"
                style="z-index: 10 !important"
                @cancel="dargShow = false"
            >
                <div v-globalDrag="{ el: 'modal-storage-allocation-sheet' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ storageAllocatModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <ul class="footer-btn-list">
                        <li>
                            <a-button key="print" @click="printForStoreAllocation"> 打印 </a-button>
                            <a-button key="export" @click="exportExcelForStoreAllocation"> 导出Excel </a-button>
                        </li>
                        <li>
                            <div>
                                <a-button key="close" @click="storageAllocatModalBotFn('close')"> 取消 </a-button>
                                <a-button
                                    v-if="stoModelStatus != 2"
                                    key="save"
                                    type="primary"
                                    :loading="storageAllocatModal.saveLoading"
                                    @click="storageAllocatModalBotFn('save')"
                                >
                                    保存
                                </a-button>
                            </div>
                        </li>
                    </ul>
                </template>
                <div class="modal-form-input" style="height: 520px">
                    <h6
                        style="
                            position: relative;
                            padding-bottom: 24px;
                            margin-bottom: 0;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                        "
                    >
                        <span style="margin-right: 10px; font-size: 24px; color: #444">{{ orgName }}</span>
                        <span style="font-size: 24px; color: #444">储位分配单</span>
                    </h6>
                    <ul class="top-list">
                        <li v-if="btnName !== 'add'" class="item">
                            <span class="label">单据编号</span>
                            <a-input
                                v-show="c_IsGrn && !modalmask"
                                v-model="addInfoModalForm.sas_sn"
                                disabled
                                placeholder=""
                                class="input"
                            />
                            <!-- 储位分配单 -->
                            <a-input
                                v-show="c_IsGrn && modalmask && stoType === 0"
                                v-model="addInfoModalForm.sas_sn"
                                disabled
                                placeholder=""
                                class="input"
                            />
                            <!-- 入库单 -->
                            <a-input
                                v-show="c_IsGrn && modalmask && stoType === 1"
                                v-model="addInfoModalForm.grn_sn"
                                disabled
                                placeholder=""
                                class="input"
                            />
                            <!-- 入库单 -->
                            <a-input
                                v-show="!c_IsGrn"
                                v-model="addInfoModalForm.odo_sn"
                                disabled
                                placeholder=""
                                class="input"
                            />
                            <!-- 出库单 -->
                        </li>
                        <li class="item">
                            <span class="label"><em class="em red">*</em>仓库</span>
                            <a-tree-select
                                treeNodeFilterProp="title"
                                :showSearch="true"
                                class="input"
                                style="min-width: 120px"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                placeholder=""
                                treeDefaultExpandAll
                                v-model="addInfoModalForm.warehouse_name"
                                disabled
                            >
                            </a-tree-select>
                        </li>
                        <li class="item">
                            <span class="label"><em class="em red">*</em>作业人</span>
                            <a-tree-select
                                treeNodeFilterProp="title"
                                searchPlaceholder=""
                                :showSearch="true"
                                :disabled="stoType === 1"
                                :treeData="warehouseOperatorList"
                                v-model="addInfoModalForm.warehouse_operator_inner_sn"
                                @click="clickWarehouseOperatorFn"
                                @change="changeWarehouseOperatorNameSnFn"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                treeDefaultExpandAll
                                class="input"
                            >
                            </a-tree-select>
                        </li>
                    </ul>
                    <div
                        class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                        style="padding-bottom: 0; margin-top: 0; z-index: 5 !important"
                    >
                        <div class="top-add-product">
                            <a-tooltip placement="topLeft" title="添加产品">
                                <a-button @click="tableHeadFn" icon="plus" style="height: 24px; padding: 0 4px"></a-button>
                            </a-tooltip>
                        </div>
                        <draggable
                            v-show="dargShow"
                            :options="{ put: true }"
                            style="position: absolute; width: 1040px; height: 220px; z-index: 1; top: 40px"
                        >
                        </draggable>
                        <vxe-grid
                            ref="xStorageAllocatTable"
                            size="small"
                            :data="storageAllocatDatas"
                            :loading="storageAllocatLoading"
                            :height="320 - ipadHeight"
                            border
                            resizable
                            auto-resize
                            show-overflow
                            show-header-overflow
                            highlight-current-row
                            @cell-click="cellClickStorageAllocatFn"
                            @cell-dblclick="cellDblclickStorageAllocatFn"
                            align="center"
                        >
                            <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                                <template v-slot="{ seq }">
                                    <div>
                                        <span>{{ seq }}</span>
                                    </div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="product_name"
                                title="产品"
                                width="20%"
                                :cell-render="{ name: 'product_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="15%"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="product_vendor_name"
                                title="厂商/经销商"
                                width="12%"
                                :cell-render="{ name: 'product_vendor_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="batch_number"
                                title="批号"
                                width="8%"
                                :cell-render="{ name: 'batch_number' }"
                            ></vxe-table-column>
                            <vxe-table-column field="quantity" title="应放数量" width="12%">
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :disabled="stoModelStatus === 2"
                                        :min="0"
                                        v-model="row.quantity"
                                        class="input"
                                        style="width: 100%; z-index: 1"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="real_quantity"
                                title="实放数量"
                                width="8%"
                                :cell-render="{ name: 'real_quantity' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="unit"
                                title="包装单位"
                                width="8%"
                                :cell-render="{ name: 'unit' }"
                            ></vxe-table-column>
                            <vxe-table-column field="location_name" title="存储位置" width="12%">
                                <template v-slot="{ row, seq }">
                                    <a-cascader
                                        :disabled="stoModelStatus === 2"
                                        :options="locationList"
                                        :loadData="loadLocationDataFn"
                                        v-model="row.location_inner_sn"
                                        placeholder=""
                                        :allowClear="false"
                                        changeOnSelect
                                        style="z-index: 1"
                                        :displayRender="displayLocationRenderFn"
                                        @click="clickLocationFn(row, seq)"
                                        @change="changeLocationFn"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="location_msg"
                                title="位置描述"
                                width="10%"
                                show-overflow
                                :cell-render="{ name: 'location_msg' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                v-if="stoModelStatus !== 2"
                                field="action"
                                title="操作"
                                width="120"
                                :resizable="false"
                                fixed="right"
                            >
                                <template v-slot="{ row, seq }">
                                    <ul class="action-btn-list-wrap" style="position: relative">
                                        <li>
                                            <a-tooltip class="tooltip" placement="top" title="删除">
                                                <i
                                                    @click.stop="storageAllocatTableFn(row, seq, 'remove')"
                                                    class="yrt yrt-shanchu-copy cancel"
                                                ></i>
                                            </a-tooltip>
                                        </li>
                                        <li>
                                            <a-tooltip class="tooltip" placement="top" title="新储位">
                                                <i
                                                    @click.stop="storageAllocatTableFn(row, seq, 'add-storage-allocat')"
                                                    class="yrt yrt-fuzhi"
                                                ></i>
                                            </a-tooltip>
                                        </li>
                                    </ul>
                                </template>
                            </vxe-table-column>
                            <template v-slot:empty>
                                <div class="dblclick-wrap">
                                    <span>暂无数据</span>
                                </div>
                            </template>
                        </vxe-grid>
                    </div>
                    <ul class="order-modal-list">
                        <li>
                            <div class="item-wrap">
                                <span class="label" style="width: auto">备注</span>
                                <a-input
                                    placeholder=""
                                    :disabled="stoModelStatus === 2"
                                    v-model="addInfoModalForm.description"
                                    class="input"
                                />
                            </div>
                            <div class="item-wrap">
                                <div class="item">
                                    <span class="label" style="width: auto">制单人</span>
                                    <span class="input">{{ making_people_name || '-' }}</span>
                                </div>
                                <div class="item">
                                    <span class="label" style="width: auto">制单时间</span>
                                    <span class="input">{{ create_time || '-' }}</span>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
            </a-modal>
            <!-- 储位分配单区域结束 -->

            <!-- 新增区域开始 -->
            <a-modal
                v-model="addInfoModal.alert"
                :centered="true"
                :tabindex="32"
                :maskClosable="false"
                :destroyOnClose="false"
                class="scoped modal-form-input-scoped modal-add global-drag"
                :class="isAddProductModalTableMax ? 'my-modal-zoom' : ''"
                :width="1210"
                :zIndex="3"
                :keyboard="false"
                @cancel="stoCloseWarn"
                :afterClose="afterCloseFn"
            >
                <div v-globalDrag="{ el: 'modal-add' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ addInfoModal.title }}
                    </h6>
                    <a-button
                        @click="addProductZoomFn"
                        class="modal-header-zoom"
                        type="primary"
                        :icon="isAddProductModalTableMax ? 'fullscreen-exit' : 'fullscreen'"
                        size="large"
                    />
                </div>
                <template slot="footer">
                    <ul class="footer-btn-list">
                        <li>
                            <a-button key="print" :disabled="!printButtonEnabled" @click="addInfoModalBotFn('print')">
                                打印
                            </a-button>
                            <a-button key="exportExcel" @click="exportExcel('xTableAddInfo')"> 导出Excel </a-button>
                            <!-- <a-button
                                v-show="(addInfoModalForm.status === 2 && this.orderTypeData.value === 0) && !isClickRedDashedOrder"
                                key="save"
                                type="primary"
                                @click="stoModalFn">
                                储位分配单
                            </a-button> -->
                            <!-- <a-button
                                v-if="!c_IsGrn && btnName === 'add'"
                                key="bill-lading"
                                @click="addInfoModalBotFn('bill-lading')"
                            >
                                调单
                            </a-button> -->
                        </li>
                        <li v-if="isIncludedInWarehouseList">
                            <!-- <a-button
                                v-if="isRedDashedOrder && !isClickRedDashedOrder"
                                key="redDashed"
                                type="primary"
                                @click="addInfoModalBotFn('redDashed')">
                                生成红冲出库单
                            </a-button> -->
                            <a-button key="close" @click="addInfoModalBotFn('close')">
                                {{ addInfoModalTextComputed }}
                            </a-button>
                            <a-button
                                v-show="isClickRedDashedOrder || addInfoModalForm.status === 0 || btnName === 'add'"
                                key="save"
                                type="primary"
                                :loading="addInfoModal.saveLoading"
                                @click="addInfoModalBotFn('save')"
                            >
                                保存
                            </a-button>
                            <a-button
                                v-show="isClickRedDashedOrder || addInfoModalForm.status === 0 || btnName === 'add'"
                                key="audit"
                                type="primary"
                                :loading="addInfoModal.auditLoading"
                                @click="addInfoModalBotFn('audit')"
                            >
                                审核
                            </a-button>
                            <a-button
                                v-show="addInfoModalForm.status === 1"
                                key="godownEntry"
                                type="primary"
                                :loading="addInfoModal.godownEntryLoading"
                                @click="addInfoModalBotFn('godownEntry')"
                            >
                                {{ setOutPutButtonTextComputed }}
                            </a-button>
                            <!-- <a-button
                                v-show="!c_IsGrn && addInfoModalForm.status === 2"
                                key="review"
                                type="primary"
                                :loading="addInfoModal.reviewLoading"
                                @click="addInfoModalBotFn('review')">
                                复核
                            </a-button> -->
                        </li>
                        <!-- <li v-else>
                            <a-button key="close" @click="addInfoModalBotFn('close')"> 关闭 </a-button>
                        </li> -->
                    </ul>
                </template>
                <div class="modal-form-input" style="height: 560px">
                    <h6
                        style="
                            position: relative;
                            padding-bottom: 24px;
                            margin-bottom: 0;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                        "
                    >
                        <span style="font-size: 24px">
                            <span style="color: #444; margin-right: 10px">{{ orgName }}</span>
                            <span :style="isRedDashedOrder ? 'color:#f00' : 'color:#444'">{{
                                this.addInfoModalTitleComputed
                            }}</span>
                        </span>
                        <div
                            v-if="!(btnName === 'add' || btnName === 'copy')"
                            style="position: absolute; right: 0; display: flex"
                        >
                            <span class="label" style="font-size: 14px; color: rgba(0, 0, 0, 0.65)">单据编号</span>
                            <a-input
                                v-show="c_IsGrn"
                                v-model="addInfoModalForm.grn_sn"
                                disabled
                                placeholder=""
                                class="input"
                            />
                            <!-- 入库单 -->
                            <a-input
                                v-show="!c_IsGrn"
                                v-model="addInfoModalForm.odo_sn"
                                disabled
                                placeholder=""
                                class="input"
                            />
                            <!-- 出库单 -->
                        </div>
                    </h6>
                    <ul class="top-list">
                        <li v-if="!isShowOrgName" class="item">
                            <span class="label"><em class="em red">*</em>{{ companyName }}</span>
                            <a-input
                                ref="relevantInstitutions"
                                v-focus="relevantInstitutionsFocus"
                                class="input"
                                type="text"
                                :disabled="btnName === 'edit' || isEditDisabledAddInfo || isSheel"
                                v-model="addInfoModalForm.organization_name"
                                @keydown.enter="relevantInstitutionsKeyDownFn"
                                @click="openChooseTb"
                                placeholder=""
                            />
                        </li>
                        <li class="item">
                            <span class="label"><em class="em red">*</em>仓库</span>
                            <a-tree-select
                                ref="warehouseFocus"
                                treeNodeFilterProp="title"
                                :showSearch="true"
                                class="input"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                :treeData="wareHouseFilterData"
                                v-model="addInfoModalForm.warehouse_inner_sn"
                                :disabled="
                                    isReturnGoods || btnName === 'edit' || isEditDisabledAddInfo || SheelHasWarehouse
                                "
                                treeDefaultExpandAll
                                @focus="clickAddInfoModalWarehouseFn"
                                @change="changeAddInfoModalWarehouseFn"
                                style="min-width: 120px"
                            >
                            </a-tree-select>
                        </li>
                        <li class="item">
                            <!-- <span class="label"><em 
                                v-if="userSystemType === 1 && addInfoModalForm.billsType === billsTypeObj[3]"
                                class="em red">*</em>部门</span> -->
                            <span class="label"><em class="em red">*</em>部门</span>
                            <a-tree-select
                                treeNodeFilterProp="title"
                                :showSearch="true"
                                class="input"
                                :value="addInfoModalForm.department_inner_sn"
                                :disabled="isEditDisabledAddInfo"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                :treeData="superiorDepartments"
                                treeDefaultExpandAll
                                @focus="clickSuperiorDepartmentsFn"
                                @change="changeSuperiorDepartmentsFn"
                                style="min-width: 120px"
                            >
                            </a-tree-select>
                        </li>
                        <li v-if="!isSeparateBillOrder" class="item">
                            <span class="label"><em class="em red">*</em>单据类别</span>
                            <div class="input" style="max-width: 201px">
                                <a-tree-select
                                    :treeData="grnTypeList"
                                    v-model="addInfoModalForm.type_inner_sn"
                                    :disabled="isEditDisabledAddInfo || (isSheel && sheelID === 'ALLOT')"
                                    @focus="clickGrnTypeListSnFn"
                                    @change="changeGrnTypeListSnFn"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    treeDefaultExpandAll
                                    class="input"
                                >
                                </a-tree-select>
                                <!-- <a-tooltip placement="topLeft" title="新增类别">
                                    <a-button
                                        :disabled="isEditDisabledAddInfo"
                                        @click="addGrnTypeFn"
                                        icon="plus"
                                        style="height:24px;padding:0 4px;margin-left:10px"
                                    ></a-button>
                                </a-tooltip> -->
                            </div>
                        </li>
                        <li v-if="isShowOrgName" class="item"></li>
                    </ul>
                    <div
                        ref="applyTable"
                        :class="isAddProductModalTableMax ? 'table-top-zoom-max' : ''"
                        class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                        style="padding-bottom: 0; margin-top: 0"
                    >
                        <!-- <div class="top-add-product">
                            <a-tooltip placement="topLeft" title="添加产品">
                                <a-button
                                    :disabled="isEditDisabledAddInfo"
                                    @click="dblclickTableFn"
                                    icon="plus"
                                    style="height:24px;padding:0 4px;"
                                ></a-button>
                            </a-tooltip>
                        </div> -->
                        <draggable
                            element="ul"
                            v-model="addInfoDatas"
                            @start="dragStart"
                            @end="dragEnd"
                            :sort="true"
                            :move="dragGetdata"
                            v-show="dargShow"
                            class="dragMain"
                        >
                            <li
                                class="dragMainLi"
                                v-for="(item, index) in addInfoDatas"
                                :key="item.product_name"
                                @click="dragClick(index)"
                            >
                                <!-- {{item.product_name}} {{item.specification}} {{item.vendor_name}}-->
                            </li>
                        </draggable>
                        <vxe-grid
                            ref="xTableAddInfo"
                            size="small"
                            :data="addInfoDatas"
                            :loading="addInfoLoading"
                            :height="(isAddProductModalTableMax ? scrollYAddProductNumber : 310) - ipadHeight"
                            border
                            resizable
                            auto-resize
                            show-overflow
                            show-header-overflow
                            highlight-current-row
                            :row-class-name="rowClassNameFn"
                            @cell-click="cellAddInfoClickFn"
                            @cell-dblclick="cellAddInfoDblclickFn"
                            show-footer
                            :footer-method="footerMethod"
                            :footer-cell-class-name="footerCellClassNameFn"
                            align="center"
                        >
                            <!-- :height="320 - ipadHeight" @keydown="buyTableKeydownFn" -->
                            <vxe-table-column field="index" title="NO." width="50" :resizable="false" fixed="left">
                                <template v-slot="{ row, seq }">
                                    <div>
                                        <i
                                            :class="
                                                row.source_type === 'copy' || !row.detail_inner_sn ? 'red-dot-add' : 'hide'
                                            "
                                        ></i>
                                        <span>{{ seq }}</span>
                                    </div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="product_name" title="产品" width="120">
                                <template v-slot="{ row }">
                                    <span v-if="!!row.product_inner_sn">{{ row.product_name }}</span>
                                    <a-input
                                        v-else
                                        v-model="row.product_name"
                                        v-focus="row.focusDatas[0]"
                                        @blur="resetFormFocusFn"
                                        class="input"
                                        style="width: 100%"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="90"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="vendor_name"
                                title="厂商/经销商"
                                width="100"
                                :cell-render="{ name: 'vendor_name' }"
                            ></vxe-table-column>
                            <vxe-table-column field="batch_number" title="批号" width="80">
                                <template v-slot="{ row, seq }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-input
                                        v-else-if="
                                            (c_IsGrn && !(orderTypeData.value === 'PURCHASERETURN')) ||
                                            orderTypeData.value === 'SALERETURN'
                                        "
                                        :disabled="row.isReadonly || isEditDisabledAddInfo || sheelID === 'ALLOT'"
                                        v-model="row.batch_number"
                                        class="input"
                                        style="width: 100%; z-index: 1"
                                    />
                                    <!-- 入库 -->
                                    <a-input
                                        v-else
                                        :readonly="true"
                                        :disabled="
                                            row.isReadonly ||
                                            isEditDisabledAddInfo ||
                                            !isIncludedInWarehouseList ||
                                            sheelID === 'ALLOT'
                                        "
                                        v-model="row.batch_number"
                                        class="input"
                                        style="width: 100%; z-index: 1"
                                        @click.stop="odoBatchNumberFn(row, seq)"
                                    />
                                    <!-- 出库 -->
                                </template>
                            </vxe-table-column>
                            <!-- 数量 重写 -->
                            <vxe-table-column field="quantity" title="数量" width="70">
                                <template v-slot="{ row, seq }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-input-number
                                        v-else
                                        :disabled="isEditDisabledAddInfo"
                                        v-model="row.quantity"
                                        class="input"
                                        style="width: 100%; z-index: 1"
                                        @click="batchRequestFn(row, seq, true)"
                                        @change="handleTaxUnitPriceChange(row, 'quantity', seq)"
                                    />
                                </template>
                            </vxe-table-column>
                            <!-- 包装单位 重写 -->
                            <vxe-table-column field="unit" title="单位" width="80">
                                <template v-slot="{ row, seq }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <span v-else>
                                        <span
                                            v-if="
                                                isEditDisabledAddInfo ||
                                                row.voucher_inner_sn_c ||
                                                (isSheel && orderTypeData.value === 1)
                                            "
                                        >
                                            {{ row.unit_name }}
                                        </span>
                                        <a-cascader
                                            v-else
                                            :disabled="
                                                row.isReadonly ||
                                                isEditDisabledAddInfo ||
                                                row.voucher_inner_sn_c ||
                                                (isSheel && orderTypeData.value === 1)
                                            "
                                            :allowClear="false"
                                            :options="row.unitList"
                                            v-model="row.unit_inner_sn"
                                            changeOnSelect
                                            :displayRender="displayUnitRender"
                                            @click="clickUnitListFn(row, seq)"
                                            @change="changeUnitListFn"
                                            placeholder=""
                                            style="width: 100%"
                                        />
                                    </span>
                                </template>
                            </vxe-table-column>
                            <!-- 含税单价 重写 -->
                            <vxe-table-column
                                v-if="orderTypeData.value === 'SALERETURN'"
                                field="buying_price"
                                title="成本价"
                                width="80"
                            >
                                <template v-slot="{ row }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-input-number
                                        :min="0"
                                        :formatter="(value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                        v-model="row.buying_price"
                                        :disabled="row.isReadonly || isEditDisabledAddInfo"
                                        class="input"
                                        style="width: 100%"
                                        @click="() => {}"
                                    />
                                </template>
                            </vxe-table-column>
                            <!-- 含税单价 重写 -->
                            <vxe-table-column :visible="isShowGiveOdoColumn" field="unit_price" title="含税单价" width="80">
                                <template v-slot="{ row, seq }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-dropdown
                                        v-else
                                        :disabled="row.isReadonly || isEditDisabledAddInfo"
                                        :trigger="['click']"
                                    >
                                        <a-input-number
                                            :min="0"
                                            v-model="row.tax_unit_price"
                                            :disabled="row.isReadonly || isEditDisabledAddInfo"
                                            class="input"
                                            style="width: 100%"
                                            :style="`color:${
                                                !c_IsGrn &&
                                                row.tax_unit_price &&
                                                row.average_price &&
                                                row.min_unit_info &&
                                                parseFloat(row.tax_unit_price) <
                                                    parseFloat(row.average_price) * parseFloat(row.min_unit_info)
                                                    ? '#f00'
                                                    : '#666'
                                            }`"
                                            @click="() => {}"
                                            @change="handleTaxUnitPriceChange(row, 'tax_unit_price', seq)"
                                        />
                                        <a-menu v-if="row.unitPriceList && row.unitPriceList.length" slot="overlay">
                                            <a-menu-item
                                                v-for="(priceItem, priceIndex) in row.unitPriceList"
                                                :key="priceIndex"
                                                @click="changeUnitPriceFn(row, priceItem)"
                                            >
                                                <a href="javascript:;">{{ priceItem }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                    <!-- <a-input-number
                                        v-else
                                        :disabled="isEditDisabledAddInfo"
                                        :min="0"
                                        :formatter="value => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                        :parser="value => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                        v-model="row.unit_price"
                                        class="input"
                                        style="width:100%"
                                        @change="updataTable(row, 'unit_price_text', seq)"
                                    /> -->
                                </template>
                            </vxe-table-column>
                            <!-- 含税金额 重写 -->
                            <vxe-table-column :visible="isShowGiveOdoColumn" field="money" title="含税金额" width="80">
                                <template v-slot="{ row, seq }">
                                    <a-input-number
                                        :disabled="row.isReadonly || isEditDisabledAddInfo"
                                        v-model="row.money"
                                        class="input"
                                        style="width: 100%"
                                        @change="handleTaxUnitPriceChange(row, 'money', seq)"
                                    />
                                </template>
                            </vxe-table-column>
                            <!-- 税率 重写 -->
                            <vxe-table-column field="rate" title="税率" width="80">
                                <template v-slot="{ row, seq }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <!-- <div 
                                        v-else 
                                        class="rate-wrap"
                                    >
                                        <a-input-number
                                            :disabled="row.isReadonly || isEditDisabledAddInfo"
                                            size="small"
                                            :min="0"
                                            :max="100"
                                            :percision="0"
                                            :formatter="value => value.toString().replace(/^(0+)|[^\d]+/g,'')"
                                            :parser="value => value.replace(/^(0+)|[^\d]+/g,'')"
                                            v-model="row.rate"
                                            class="input"
                                            style="width: 80%;display: inline-block;margin-right: 5px;"
                                            @change="updataTable(row, 'rate', seq)"
                                        />
                                        <span class="tip">%</span>
                                    </div> -->
                                    <a-select
                                        class="input"
                                        v-else
                                        :disabled="row.isReadonly || isEditDisabledAddInfo"
                                        v-model="row.rate"
                                        :default-value="0"
                                        style="width: 80%; display: inline-block; margin-right: 5px; margin-top: 6px"
                                        @change="updataTable(row, 'rate', seq)"
                                    >
                                        <a-select-option :value="0"> 0% </a-select-option>
                                        <a-select-option :value="1"> 1% </a-select-option>
                                        <a-select-option :value="3"> 3% </a-select-option>
                                        <a-select-option :value="6"> 6% </a-select-option>
                                        <a-select-option :value="9"> 9% </a-select-option>
                                        <a-select-option :value="13"> 13% </a-select-option>
                                    </a-select>
                                </template>
                            </vxe-table-column>
                            <!-- 生产日期 重写 -->
                            <vxe-table-column field="produce_date" title="生产日期" width="120">
                                <template v-slot="{ row, seq }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-date-picker
                                        v-else
                                        :disabled="
                                            row.isReadonly ||
                                            (!c_IsGrn && !(orderTypeData.value === 'SALERETURN')) ||
                                            isEditDisabledAddInfo ||
                                            sheelID === 'ALLOT'
                                        "
                                        v-model="row.produce_date"
                                        :format="format"
                                        placeholder=""
                                        allowClear
                                        class="input"
                                        style="width: 100%"
                                        @change="updataTable(row, 'overdue_date', seq)"
                                    />
                                    <!-- :open="row.focusDatas[6]" -->
                                </template>
                            </vxe-table-column>
                            <!-- 失效日期 重写 -->
                            <vxe-table-column field="overdue_date" title="失效日期" width="120">
                                <template v-slot="{ row, seq }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-date-picker
                                        v-else
                                        :disabled="
                                            row.isReadonly ||
                                            (!c_IsGrn && !(orderTypeData.value === 'SALERETURN')) ||
                                            isEditDisabledAddInfo ||
                                            sheelID === 'ALLOT'
                                        "
                                        v-model="row.overdue_date"
                                        :format="format"
                                        placeholder=""
                                        allowClear
                                        class="input"
                                        style="width: 100%"
                                        :disabled-date="disabledDate"
                                        @change="updataTable(row, '', seq)"
                                    />
                                    <!-- :open="row.focusDatas[7]" -->
                                </template>
                            </vxe-table-column>
                            <!-- 类别 重写 -->
                            <vxe-table-column :visible="isShowGiveOdoColumn" field="type" title="类别" width="90">
                                <template v-slot="{ row, seq }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-select
                                        v-else
                                        :disabled="row.isReadonly || isEditDisabledAddInfo"
                                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                        placeholder=""
                                        v-model="row.type"
                                        class="input"
                                        style="width: 100%"
                                        @change="updataTable(row, 'mdrf_inner_sn', seq)"
                                    >
                                        <a-select-option
                                            v-for="typeItem in typeList"
                                            :value="typeItem.value"
                                            :key="typeItem.value"
                                        >
                                            {{ typeItem.label }}
                                        </a-select-option>
                                    </a-select>
                                </template>
                            </vxe-table-column>
                            <!-- 注册证号 重写 -->
                            <vxe-table-column field="mdrf_inner_sn" title="注册证号" width="100">
                                <template v-slot="{ row, seq }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-tree-select
                                        v-else
                                        :disabled="row.isReadonly || isEditDisabledAddInfo || row.product_type === 0"
                                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                        placeholder=""
                                        treeDefaultExpandAll
                                        v-model="row.mdrf_inner_sn"
                                        :treeData="row.mdrfList"
                                        class="input"
                                        style="width: 100%"
                                        @click.stop="getMdrfListFn($event, row, seq)"
                                        @change="changeMdrfListFn"
                                    >
                                    </a-tree-select>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="health_care_sn" title="医保编号" align="center" width="100">
                                <template v-slot="{ row, seq }">
                                    <span v-if="!row.product_inner_sn"></span>
                                    <a-dropdown
                                        v-else
                                        :disabled="row.isReadonly || isEditDisabledAddInfo || row.product_type === 0"
                                        :trigger="['click']"
                                    >
                                        <a-input
                                            class="input"
                                            style="width: 100%"
                                            v-model="row.health_care_sn"
                                            :disabled="row.isReadonly || isEditDisabledAddInfo"
                                            @click="getProductSnAPIFn(row, seq)"
                                            @change="$refs.xTableAddInfo.updateData()"
                                        >
                                        </a-input>
                                        <a-menu
                                            v-if="row.healthSnData && row.healthSnData.length"
                                            slot="overlay"
                                            style="maxheight: 200px"
                                        >
                                            <a-menu-item
                                                v-for="item in row.healthSnData"
                                                :key="item.index"
                                                @click="healthCareSnChangeFn(item, seq)"
                                            >
                                                <a href="javascript:;">{{ item.sn }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <!-- v-if="addInfoModalForm.status <= 1" -->
                            <!-- v-if="addInfoModalForm.status <= 2" -->
                            <vxe-table-column v-if="isShowAddInfoModalActionColumn" field="action" title="操作" width="100">
                                <template v-slot="{ row, seq }">
                                    <!-- 1. 出入库单拆分后： 调拨入库和调拨出库、采购退货和销售退货 -->
                                    <div
                                        v-if="
                                            isSeparateBillOrder &&
                                            (!isNotAllot || !isTransferRecord) &&
                                            row.product_inner_sn &&
                                            (btnName === 'add' || addInfoModalForm.status === 0)
                                        "
                                    >
                                        <ul class="action-btn-list-wrap" style="position: relative">
                                            <li>
                                                <a-tooltip class="tooltip" placement="top" title="删除">
                                                    <i
                                                        @click.stop="actionTablegroupBtnFn($event, 'remove', row, seq)"
                                                        class="yrt yrt-shanchu-copy cancel"
                                                    ></i>
                                                </a-tooltip>
                                            </li>
                                        </ul>
                                    </div>
                                    <!-- 其它情况 -->
                                    <div
                                        v-else-if="
                                            row.product_inner_sn && (btnName === 'add' || addInfoModalForm.status === 0)
                                        "
                                    >
                                        <ul class="action-btn-list-wrap" style="position: relative">
                                            <li>
                                                <!-- 出库调单隐藏 【复制按钮】 v-if="!isSheel || c_IsGrn" -->
                                                <a-tooltip class="tooltip" placement="top" title="复制">
                                                    <i
                                                        @click.stop="actionTablegroupBtnFn($event, 'copy', row, seq)"
                                                        class="yrt yrt-fuzhi"
                                                    ></i>
                                                </a-tooltip>
                                            </li>
                                            <li>
                                                <a-tooltip class="tooltip" placement="top" title="删除">
                                                    <i
                                                        @click.stop="actionTablegroupBtnFn($event, 'remove', row, seq)"
                                                        class="yrt yrt-shanchu-copy cancel"
                                                    ></i>
                                                </a-tooltip>
                                            </li>
                                        </ul>
                                    </div>
                                    <!-- 点击生成内部条码 -->
                                    <!-- <div v-else-if="c_IsGrn && addInfoModalForm.status === 1">
                                        <ul
                                            class="action-btn-list-wrap"
                                            style="position:relative"
                                        >
                                            <li v-if="row.hasBarcode">
                                                <a-tooltip
                                                    class="tooltip"
                                                    placement="top"
                                                    title="查看内部条码"
                                                >
                                                    <i
                                                        @click.stop="actionTablegroupBtnFn($event, 'send-barcode', row, seq)"
                                                        class="yrt yrt-chakantiaoma"></i>
                                                </a-tooltip>
                                            </li>
                                            <li v-else>
                                                <a-tooltip
                                                    class="tooltip"
                                                    placement="top"
                                                    title="生成内部条码"
                                                >
                                                    <i
                                                        @click.stop="actionTablegroupBtnFn($event, 'send-barcode', row, seq)"
                                                        class="yrt yrt-shengchengtiaoma1"></i>
                                                </a-tooltip>
                                            </li>
                                        </ul>
                                    </div> -->
                                    <!-- 已入库状态入库单 -->
                                    <div v-else-if="c_IsGrn && addInfoModalForm.status === 2">
                                        <ul class="action-btn-list-wrap" style="position: relative">
                                            <li v-if="isClickRedDashedOrder">
                                                <a-tooltip class="tooltip" placement="top" title="删除">
                                                    <i
                                                        @click.stop="actionTablegroupBtnFn($event, 'remove', row, seq)"
                                                        class="yrt yrt-shanchu-copy cancel"
                                                    ></i>
                                                </a-tooltip>
                                            </li>
                                            <!-- <li v-else>
                                                <a-tooltip
                                                    class="tooltip"
                                                    placement="top"
                                                    title="查看内部条码"
                                                >
                                                    <i
                                                        @click.stop="actionTablegroupBtnFn($event, 'send-barcode', row, seq)"
                                                        class="yrt yrt-chakantiaoma"></i>
                                                </a-tooltip>
                                            </li> -->
                                        </ul>
                                    </div>
                                </template>
                            </vxe-table-column>
                            <template v-slot:empty>
                                <div v-if="!isClickRedDashedOrder" @dblclick.stop="dblclickTableFn" class="dblclick-wrap">
                                    <span @dblclick.stop="dblclickTableFn">{{
                                        c_IsGrn ? '双击添加产品或扫码录入' : '双击添加产品'
                                    }}</span>
                                </div>
                            </template>
                            <!-- <template v-slot:toolbar_buttons>
                                <a-button-group 
                                    v-if="isAddProductModalTableMax"
                                    size="small">
                                    <a-button
                                        :disabled="isEditDisabledAddInfo"
                                        @click="dblclickTableFn"
                                        type="primary"
                                        icon="plus"
                                    >Enter</a-button>
                                    <a-button
                                        @click="addProductZoomFn"
                                        type="primary" 
                                        icon="switcher" 
                                    >Esc</a-button>
                                </a-button-group>
                            </template> -->
                        </vxe-grid>
                    </div>
                    <ul class="order-modal-list">
                        <li>
                            <div
                                class="item-wrap"
                                v-if="
                                    !c_IsGrn && (addInfoModalForm.billsType === 'DH' || addInfoModalForm.billsType === 'VH')
                                "
                            >
                                <span class="label" style="width: auto">使用科室</span>
                                <a-input
                                    placeholder=""
                                    :disabled="isEditDisabledAddInfo || !isIncludedInWarehouseList"
                                    v-model="addInfoModalForm['use_department']"
                                    class="input"
                                    @click="handleResetMultipleFocus"
                                />
                            </div>
                            <!-- @click="resetFocusFn" -->
                            <div class="item-wrap">
                                <div class="item" v-if="isSeparateBillOrder && orderTypeData.value === 'PURCHASEGRN'">
                                    <span class="label" style="width: auto">采购员</span>
                                    <a-input
                                        placeholder=""
                                        :disabled="
                                            isEditDisabledAddInfo || (isSheel && orderTypeData.value !== 'PURCHASEGRN')
                                        "
                                        v-model="addInfoModalForm['buyer_name']"
                                        class="input"
                                        type="text"
                                        style="width: 50px"
                                    />
                                </div>
                                <div class="item">
                                    <span class="label" style="width: auto"><em class="em red">*</em>经手人</span>
                                    <a-tree-select
                                        treeNodeFilterProp="title"
                                        show-search
                                        :treeData="handlerNameList"
                                        v-model="addInfoModalForm.handler_inner_sn"
                                        :disabled="isEditDisabledAddInfo || !isIncludedInWarehouseList"
                                        @focus="clickHandlerNameSnFn"
                                        @change="changeHandlerNameSnFn"
                                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                        treeDefaultExpandAll
                                        class="input"
                                    >
                                    </a-tree-select>
                                </div>
                                <div class="item">
                                    <span class="label" style="width: auto">仓库人员</span>
                                    <a-tree-select
                                        treeNodeFilterProp="title"
                                        searchPlaceholder=""
                                        show-search
                                        allow-clear
                                        :treeData="warehouseOperatorList"
                                        v-model="addInfoModalForm.warehouse_operator_inner_sn"
                                        @focus="clickWarehouseOperatorFn"
                                        @change="changeWarehouseOperatorNameSnFn"
                                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                        treeDefaultExpandAll
                                        class="input"
                                    >
                                    </a-tree-select>
                                </div>
                            </div>
                            <div class="item-wrap">
                                <div class="item">
                                    <span class="label" style="width: auto">制单人</span>
                                    <span>{{ making_people_name || '-' }}</span>
                                </div>
                                <div class="item">
                                    <span class="label" style="width: auto">审核人</span>
                                    <span>{{ auditor_name || '-' }}</span>
                                </div>
                                <!-- <div class="item" v-show = "!c_IsGrn">
                                    <span class="label"
                                        style="width:auto">复核人</span>
                                    <span>{{ reviewer_name || '-' }}</span>
                                </div> -->
                            </div>
                        </li>
                        <li>
                            <div class="item-wrap">
                                <span class="label" style="width: auto">备注</span>
                                <a-input
                                    placeholder=""
                                    :disabled="isEditDisabledAddInfo || !isIncludedInWarehouseList"
                                    v-model="addInfoModalForm.description"
                                    class="input"
                                    @click="handleResetMultipleFocus"
                                />
                            </div>
                            <!-- @click="resetFocusFn" -->
                        </li>
                    </ul>
                </div>
            </a-modal>
            <!-- 新增区域结束 -->

            <!-- 新增类别区域开始 -->
            <a-modal
                v-model="addGrnTypeModal.alert"
                :centered="true"
                :maskClosable="false"
                class="scoped modal-form-input-scoped modal-grntype global-drag"
                :width="600"
                :zIndex="5"
                style="z-index: 5 !important"
            >
                <div v-globalDrag="{ el: 'modal-grntype' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ addGrnTypeModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <a-button key="close" @click="addGrnTypeModal.alert = false"> 关闭 </a-button>
                </template>
                <div class="modal-form-input">
                    <div class="scoped scoped-white" style="position: static">
                        <!--头部区域开始 根据红冲单要求暂时隐藏单据类别的编辑-->
                        <!-- <div
                            class="search-form-wrap"
                            style="padding:0;border:none;"
                        >
                            <a-row class="btn-top-wrap">
                                <a-col :span="24">
                                    <ul class="btn-list-wrap">
                                        <li>
                                            <a-button
                                                @click="grnTypeAddBtnFn('add')"
                                                type="primary"
                                                size="small"
                                            >
                                                新增
                                            </a-button>
                                            <a-button
                                                @click="grnTypeAddBtnFn('edit')"
                                                type="primary"
                                                size="small"
                                            >
                                                修改
                                            </a-button>
                                            <a-button
                                                @click="grnTypeAddBtnFn('remove')"
                                                type="danger"
                                                size="small"
                                            >
                                                删除
                                            </a-button>
                                        </li>
                                    </ul>
                                </a-col>
                            </a-row>
                        </div> -->
                        <!--头部区域结束-->

                        <!-- tab切换区域开始 -->
                        <div class="tab-table-wrap cur-overflow-hidden tab-table-overflowy" style="position: static">
                            <vxe-grid
                                ref="xTableAddType"
                                size="small"
                                :columns="grnTypeAddColumns"
                                :data="grnTypeAddDatas"
                                :loading="grnTypeAddLoading"
                                :height="420 - ipadHeight"
                                border
                                resizable
                                auto-resize
                                highlight-current-row
                                @cell-click="cellClickAddTypeFn"
                                @cell-dblclick="cellDblclickAddTypeFn"
                                align="center"
                            >
                            </vxe-grid>
                        </div>
                        <!-- tab切换区域结束 -->
                    </div>
                </div>
            </a-modal>
            <!-- 新增类别区域结束 -->

            <!-- 新增类别明细区域开始 -->
            <a-modal
                v-model="addGrnTypeListModal.alert"
                :centered="true"
                :maskClosable="false"
                :mask="false"
                class="scoped modal-form-input-scoped modal-grntype-list global-drag"
                :width="500"
                :zIndex="8"
                style="z-index: 8 !important"
            >
                <div v-globalDrag="{ el: 'modal-grntype-list' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ addGrnTypeListModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <a-button key="cancel" @click.stop="addGrnTypeListModalFn('cancel')"> 取消 </a-button>
                    <a-button
                        :loading="addGrnTypeListModal.loading"
                        key="save"
                        type="primary"
                        @click.stop="addGrnTypeListModalFn('save')"
                    >
                        保存
                    </a-button>
                </template>
                <div class="modal-form-input">
                    <ul class="order-modal-list" style="position: static">
                        <li style="padding-top: 0">
                            <span class="label" style="width: 54px"><em class="em red">*</em>类别</span>
                            <a-input placeholder="" v-model="grnTypeAddForm.name" class="input" />
                        </li>
                        <li style="padding-bottom: 0">
                            <span class="label" style="width: 54px">有效</span>
                            <a-checkbox
                                :checked="grnTypeAddForm.checked"
                                @change.stop="changeGrnTypeAddFormFn"
                            ></a-checkbox>
                        </li>
                    </ul>
                </div>
            </a-modal>
            <!-- 新增类别明细区域结束 -->

            <!-- 选择单位名称区域开始 -->
            <a-modal
                :visible="chooseOrgMd.alert"
                :maskClosable="false"
                :width="600"
                :centered="true"
                :footer="null"
                :zIndex="3"
                @cancel="chooseOrgMd.alert = false"
                class="scoped modal-form-input-scoped global-drag choose_org_md"
            >
                <div v-globalDrag="{ el: 'choose_org_md' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ chooseOrgMd.title }}
                    </h6>
                </div>
                <div style="margin-top: 56px">
                    <choose-org-tab
                        ref="chooseOrgMd"
                        @chooseOrg="chooseOrgFn"
                        :searchOrgName="searchOrgName"
                        :isParentKeyDownEnter="isParentKeyDownEnter"
                        :partType="1"
                        :pageTypeName="pageTypeName"
                    ></choose-org-tab>
                </div>
            </a-modal>
            <!-- 选择单位名称区域结束 -->

            <!-- 添加产品区域开始 -->
            <a-modal
                :title="addProductMd.title"
                v-model="addProductMd.alert"
                :maskClosable="false"
                :mask="false"
                :width="1160"
                :centered="true"
                @cancel="closeAddProductFn"
                class="modal-form-input-scoped global-drag add_product"
            >
                <div v-globalDrag="{ el: 'add_product' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ addProductMd.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <a-button @click="closeAddProductFn">关闭</a-button>
                    <!-- <a-button @click="addProductMd.alert = false">关闭</a-button> -->
                    <a-button :loading="addProductMd.loading" @click="addProductFn" type="primary">添加</a-button>
                </template>
                <add-product ref="chooseProMd" @returnData="getProReturn" :partB="partB"></add-product>
            </a-modal>
            <!-- 添加产品区域结束 -->
            <!-- 调单区域开始 -->
            <a-modal
                v-model="billLadingModal.alert"
                :centered="true"
                :maskClosable="false"
                class="scoped modal-form-input-scoped modal-bill-lading global-drag"
                :width="1210"
                :zIndex="3"
            >
                <div v-globalDrag="{ el: 'modal-bill-lading' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ billLadingModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <ul class="footer-btn-list">
                        <li> </li>
                        <li>
                            <a-button
                                key="save"
                                type="primary"
                                :loading="billLadingModal.saveLoading"
                                @click="transferOrderFn"
                            >
                                <!--                            @click="billLadingModalBotFn('save')"-->
                                生成出库单
                            </a-button>
                        </li>
                    </ul>
                </template>
                <transfer-order
                    ref="transfer"
                    @returnData="getReturn"
                    parameter="TYPE_ODO"
                    :orderTypeData="orderTypeData"
                ></transfer-order>
            </a-modal>
            <!-- 调单区域结束 -->

            <!-- 入库页面的入库选项卡调单区域开始 -->
            <a-modal
                v-model="godownEntryTransferModal.alert"
                :centered="true"
                :maskClosable="false"
                class="scoped modal-form-input-scoped modal-godown-entry-transfer global-drag"
                :width="1210"
                :zIndex="3"
            >
                <div v-globalDrag="{ el: 'modal-godown-entry-transfer' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ godownEntryTransferModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <ul class="footer-btn-list">
                        <li> </li>
                        <li>
                            <a-button
                                key="save"
                                type="primary"
                                :loading="godownEntryTransferModal.saveLoading"
                                @click="transferOrderFn"
                            >
                                生成入库单
                            </a-button>
                        </li>
                    </ul>
                </template>
                <transfer-order
                    ref="godown-entry-transfer"
                    @returnData="getReturn"
                    :warehouse-list="warehouseList"
                    parameter="TYPE_GRN"
                    :orderTypeData="orderTypeData"
                ></transfer-order>
            </a-modal>
            <!-- 入库页面的入库选项卡调单区域结束 -->

            <!-- 入库页面的入库选项卡调单 - 添加产品 -->
            <a-modal
                v-model="gotdownInfoModal.alert"
                :centered="true"
                :maskClosable="false"
                class="modal-form-input-scoped gotdown-info-modal global-drag"
                :width="1190"
                :zIndex="3"
            >
                <div v-globalDrag="{ el: 'gotdown-info-modal' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ gotdownInfoModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <a-button key="close" @click="gotdownInfoModal.alert = false"> 关闭 </a-button>
                    <a-button type="primary" @click="addGotdownInfo"> 添加 </a-button>
                </template>
                <div class="modal-form-input scoped" style="height: 300px">
                    <div class="tab-table-wrap tab-table-overflowy">
                        <vxe-grid
                            ref="gotdownInfoTableAdd"
                            size="small"
                            :height="260 - ipadHeight"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            align="center"
                            :data="gotdownInfoModal.data"
                            :loading="gotdownInfoModal.loading"
                            show-overflow
                            type="checkbox"
                            show-header-overflow
                            showHeader
                            :edit-config="{ trigger: 'click', mode: 'row', showStatus: true }"
                            @select-change="selectChangeGotdown"
                            @select-all="selectAllGotdown"
                            :checkbox-config="{ checkMethod }"
                            :columns="gotdownInfoModal.columns"
                            @cell-dblclick="DblClickGotdown"
                        >
                        </vxe-grid>
                    </div>
                </div>
            </a-modal>
            <!-- 入库页面的入库选项卡调单 - 添加产品 -->

            <!-- 出库单的表格明细调单添加产品区域开始 -->
            <a-modal
                v-model="sheelInfoModal.alert"
                :centered="true"
                :maskClosable="false"
                class="modal-form-input-scoped sheel-modal global-drag"
                :width="1190"
                :zIndex="3"
            >
                <div v-globalDrag="{ el: 'sheel-modal' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ sheelInfoModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <a-button type="primary" @click="addSheelInfo"> 添加 </a-button>
                    <a-button key="close" @click="sheelInfoModal.alert = false"> 关闭 </a-button>
                </template>
                <div class="modal-form-input scoped" style="height: 300px">
                    <div class="tab-table-wrap tab-table-overflowy">
                        <vxe-grid
                            ref="sheelTableAdd"
                            size="small"
                            :height="260 - ipadHeight"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            @cell-click="customRowBillLadingBotFn"
                            align="center"
                            :data="billLadingBotDatas"
                            :loading="billLadingBotLoading"
                            show-overflow
                            type="checkbox"
                            show-header-overflow
                            showHeader
                            :edit-config="{ trigger: 'click', mode: 'row', showStatus: true }"
                            @select-change="selectChangeEvent_sheel"
                            @select-all="selectAllEvent_sheel"
                            :checkbox-config="{ checkMethod }"
                            :columns="billLadingBotColumns"
                            @cell-dblclick="DblClick_sheel"
                        >
                        </vxe-grid>
                    </div>
                </div>
            </a-modal>
            <!-- 出库单的表格明细调单添加产品区域结束 -->

            <!-- 出库单的表格明细选择批号预览区域开始 -->
            <a-modal
                v-model="batchInfoModal.alert"
                :centered="true"
                :maskClosable="false"
                class="modal-form-input-scoped batch-modal global-drag"
            >
                <div v-globalDrag="{ el: 'batch-modal' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ batchInfoModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <a-button key="close" @click="batchInfoModal.alert = false"> 关闭 </a-button>
                </template>
                <div class="modal-form-input scoped">
                    <div class="tab-table-wrap tab-table-overflowy">
                        <vxe-table
                            ref="batchXTable"
                            :keyboard-config="{ isArrow: true }"
                            size="small"
                            height="420"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            @cell-click="cellClickBatchFn"
                            @cell-dblclick="cellDblclickBatchFn"
                            align="center"
                            :data="batchDatas"
                            :loading="godownEntryPagination.loading"
                        >
                            <vxe-table-column
                                v-for="batchColumnItem in batchColumns"
                                :key="batchColumnItem.key"
                                :field="batchColumnItem.dataIndex"
                                :title="batchColumnItem.title"
                                :width="batchColumnItem.width"
                                show-overflow
                                show-header-overflow
                            ></vxe-table-column>
                        </vxe-table>
                    </div>
                </div>
            </a-modal>
            <!-- 出库单的表格明细选择批号预览区域结束 -->

            <!-- 生成内部条码区域开始 -->
            <a-modal
                :title="generateBarcodeModal.title"
                v-model="generateBarcodeModal.alert"
                :maskClosable="false"
                :mask="false"
                :width="1000"
                :zIndex="3"
                :centered="true"
                @cancel="generateBarcodeModal.alert = false"
                class="modal-form-input-scoped global-drag generate-internal-barcode"
            >
                <div v-globalDrag="{ el: 'generate-internal-barcode' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ generateBarcodeModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <ul class="footer-btn-list">
                        <li>
                            <a-button key="print" @click="generateBarcodeModalBotFn('print')"> 打印 </a-button>
                        </li>
                        <li>
                            <div v-if="barcodeChildObj.hasBarcode">
                                <a-button key="close" @click="generateBarcodeModalBotFn('close')"> 关闭 </a-button>
                            </div>
                            <div v-else>
                                <a-button key="close" @click="generateBarcodeModalBotFn('close')"> 取消 </a-button>
                                <a-button
                                    key="save"
                                    type="primary"
                                    :loading="generateBarcodeModal.loading"
                                    @click="generateBarcodeModalBotFn('save')"
                                >
                                    保存
                                </a-button>
                            </div>
                        </li>
                    </ul>
                </template>
                <generate-internal-barcode
                    ref="generateBarcode"
                    v-if="generateBarcodeModal.alert"
                    :pageNameType="pageNameType"
                    :barcodeParentObj="barcodeParentObj"
                    :barcodeChildObj="barcodeChildObj"
                    @saveBarcodeEmitFn="saveBarcodeEmitFn"
                ></generate-internal-barcode>
            </a-modal>
            <!-- 生成内部条码区域结束 -->

            <!-- 超出库存的判断模态框开始 -->
            <a-modal
                :title="beyondInventoryModal.title"
                v-model="beyondInventoryModal.alert"
                :maskClosable="false"
                :width="600"
                :zIndex="3"
                :centered="true"
                @cancel="beyondInventoryModal.alert = false"
                class="modal-form-input-scoped global-drag beyond-inventory"
            >
                <div v-globalDrag="{ el: 'beyond-inventory' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ beyondInventoryModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <a-button key="back" @click="() => (beyondInventoryModal.alert = false)"> 知道了 </a-button>
                </template>
                <div v-if="beyondInventoryModal.datas.length">
                    <p v-for="(beyondItem, beyondIndex) in beyondInventoryModal.datas" :key="beyondIndex">{{
                        beyondItem
                    }}</p>
                </div>
            </a-modal>
            <!-- 超出库存的判断模态框结束 -->

            <!-- 出入库退货单的添加产品列表区域开始 -->
            <a-modal
                :title="addReturnListModal.title"
                v-model="addReturnListModal.alert"
                :maskClosable="false"
                :mask="false"
                :width="1100"
                :zIndex="3"
                :centered="true"
                @cancel="addReturnListModal.alert = false"
                class="modal-form-input-scoped gotdown-info-modal global-drag add-return-list"
            >
                <div v-globalDrag="{ el: 'add-return-list' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ addReturnListModal.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <a-button key="close" @click="addReturnListModalFn('close')"> 取消 </a-button>
                    <a-button
                        key="save"
                        type="primary"
                        :loading="addReturnListModal.loading"
                        @click="addReturnListModalFn('save')"
                    >
                        添加
                    </a-button>
                </template>
                <add-return-list
                    v-if="addReturnListModal.alert"
                    ref="orderAddReturnList"
                    :orderTypeData="orderTypeData"
                    :formData="addInfoModalForm"
                    :is_GRN="c_IsGrn"
                    :partB="partB"
                ></add-return-list>
            </a-modal>
            <!-- 出入库退货单的添加产品列表区域结束 -->
        </template>
    </and-page-layout>
</template>

<script>
import moment from 'moment';
import draggable from 'vuedraggable';
import { mapState } from 'vuex';
import Big from 'big.js';
import {
    warehouseChildrenListAPI,
    warehouseUsableTopListAPI,
    odoWeListAPI,
    odoTypeAddAPI,
    odoTypeDelAPI,
    odoTypeEditAPI,
    odoTypeListAPI,
    odoPutoutAPI,
    saleReturnPutoutAPI,
    odoAddAPI,
    odoDelAPI,
    odoInfoAPI,
    odoEditAPI,
    odoListAPI,
    odoReviewAPI,
    platformProductMdrfListAPI,
    platformUnitTree,
    platformUnitParents,
    dealerUnitTreeAPI,
    dealerUnitParentsAPI,
    orgEmpDropdownListAPI,
    orgDeptTopTreeAPI,
    grnWeListAPI,
    grnTypeAddAPI,
    grnTypeDelAPI,
    grnTypeEditAPI,
    grnTypeListAPI,
    grnPutinAPI,
    purchaseReturnPutinAPI,
    grnStorageAPI,
    grnAddAPI,
    grnDelAPI,
    grnInfoAPI,
    grnEditAPI,
    grnListAPI,
    grnOrgListAPI,
    hospitalGrantDetailListAPI,
    odoBatchListAPI,
    barcodeScanAPI,
    stoListAPI,
    sasAddAPI,
    stoDelAPI,
    stoInfoAPI,
    stoCheckAPI,
    stoGetInfoAPI,
    sasEditAPI,
    warehouseInfoAPI,
    hospitalGrantCreatodoAPI,
    saleRecordCreatodoAPI,
    saleRecordSearchAPI,
    saleRecordDetailAPI,
    odoTransferRecordDetailAPI,
    odoTransferGrantDetailAPI,
    platformUnitMinAPI,
    dealerUnitMinAPI,
    grnTransferRecordDetailAPI,
    AllotTransferDetailAPI,
    productSnListAPI,
    contractGetContractPriceAPI,
    exportFinanceInboundListAPI,
    exportFinanceOutboundListAPI,
    grnUnreviewAPI,
    odoUnreviewAPI,
    grnRedDashedAPI,
    odoRedDashedAPI,
    /* 出入库单单据拆分区域开始 */
    purchaseGrnListAPI,
    purchaseReturnListAPI,
    saleOdoListAPI,
    saleReturnListAPI,
    grantOdoListAPI,
    allotGrnListAPI,
    allotOdoListAPI,
    purchaseGrnAddAPI,
    purchaseReturnAddAPI,
    saleOdoAddAPI,
    saleReturnAddAPI,
    grantOdoAddAPI,
    allotGrnAddAPI,
    allotOdoAddAPI,
    purchaseGrnDelAPI,
    purchaseReturnDelAPI,
    saleOdoDelAPI,
    saleReturnDelAPI,
    grantOdoDelAPI,
    allotGrnDelAPI,
    allotOdoDelAPI,
    purchaseGrnEditAPI,
    purchaseReturnEditAPI,
    allotGrnEditAPI,
    saleOdoEditAPI,
    saleReturnEditAPI,
    allotOdoEditAPI,
    grantOdoEditAPI,
    purchaseGrnInfoAPI,
    purchaseReturnInfoAPI,
    allotGrnInfoAPI,
    saleOdoInfoAPI,
    saleReturnInfoAPI,
    allotOdoInfoAPI,
    grantOdoInfoAPI,
    purchaseGrnRedAPI,
    purchaseReturnRedAPI,
    allotGrnRedAPI,
    saleOdoRedAPI,
    saleReturnRedAPI,
    allotOdoRedAPI,
    grantOdoRedAPI,
    purchaseGrnUnreviewAPI,
    purchaseReturnUnreviewAPI,
    allotGrnUnreviewAPI,
    saleOdoUnreviewAPI,
    saleReturnUnreviewAPI,
    allotOdoUnreviewAPI,
    grantOdoUnreviewAPI,
    purchaseGrnTransferRecordDetailAPI,
    saleOdoTransferRecordDetailAPI,
    grantOdoTransferRecordDetailAPI,
    allotGrnTransferRecordDetailAPI,
    allotOdoTransferRecordDetailAPI,
    giveOdoListAPI,
    giveOdoAddAPI,
    giveOdoDelAPI,
    giveOdoEditAPI,
    giveOdoInfoAPI,
    giveOdoRedAPI,
    giveOdoUnreviewAPI,
    purchaseReturnProductListAPI
    /* 出入库单单据拆分区域结束 */
} from '@/service/pageAjax';

import {
    DetailTableColumnsGrnAllot,
    DetailTableColumnsOdoAllot
} from './module/transferOrderColumnConfig';

import {
    CommonGrnListColumns,
    PurchaseGrnListColumns,
    CommonOdoListColumns
} from './module/GrnAndOdoColumnConfig';
import {
    purchaseGrnOrderStatusMap,
    purchaseGrnOrderDetailActionTypeMap
} from './type';

import {
    orderTypeMap
} from '@/config/constants';

// import newDesign Layount
import AndPageLayout from '@/components/AndLayout/AndPageLayout';
import AndTabsContent from '@/components/AndLayout/AndTabsContent';
// import components
import AndSheetActs from '@/components/purchase/AndSheetActs';
import AndSheet from '@/components/AndRenders/AndSheet';

import chooseOrgTab from '@/components/chooseOrgTab';
import addProduct from '@/components/addProduct';
import transferOrder from './transferOrder';
import addReturnList from '@/components/addReturnList/returnMain';
import generateInternalBarcode from '@/components/generateBarcode/generateBarcode';
import { constants, grnAndOdoOrderType } from '@/config/constants';

export default {
    name: 'godownEntry',
    props: {
        myListPropObj: {
            // myListPropObj.pageType = 0 // 页面类型判断依据： 0-入库单   1-出库单
            type: Object,
            required: true
        },
        orderTypeData: {
            // 出入库单拆分为七个单的判断依据，详见config文件夹下的constant.js的 grnAndOdoOrderType
            type: Object,
            required: true
        },
        companyName: {
            type: String,
            default: () => '收货单位'
        }
    },
    components: {
        // custom layounts
        AndPageLayout,
        AndTabsContent,
        // custom components
        AndSheet,
        AndSheetActs,
        // modals
        chooseOrgTab,
        addProduct,
        transferOrder,
        generateInternalBarcode,
        draggable,
        addReturnList
    },
    data() {
        return {
            // init panes
            panes: [
                {
                    key: 'add',
                    title: '新增'
                },
                {
                    key: 'history',
                    title: '采购退货'
                }
            ],
            curTab: 'add',
            actColumns: [],
            addData: [],
            editable: {
                flag: false,
                obj: {}
            },
            totalFormat: {
                on: true,
                searchable: false,
                footer: [
                    'buying_price',
                    'money'
                ]
            },
            copied: {
                flag: false,
                objs: []
            },
            addColumns: [
                {
                    data: 'idx',
                    type: 'header',
                    disabled: true,
                    name: ''
                },
                {
                    data: 'product_name',
                    type: 'dropdown',
                    disabled: false,
                    name: '产品'
                },
                {
                    data: 'specification',
                    type: 'input',
                    disabled: true,
                    name: '规格型号'
                },
                {
                    data: 'vendor_name',
                    type: 'input',
                    disabled: true,
                    name: '厂商/经销商'
                },
                {
                    data: 'batch_number',
                    type: 'input',
                    disabled: true,
                    name: '批号'
                },
                {
                    data: 'quantity',
                    type: 'minusNumber',
                    disabled: false,
                    name: '数量'
                },
                {
                    data: 'unit',
                    type: 'cascader',
                    disabled: true,
                    name: '单位'
                },
                // {
                //     data: 'buying_price',
                //     type: 'number',
                //     disabled: false,
                //     name: '成本价'
                // },
                {
                    data: 'unit_price_text',
                    type: 'number',
                    disabled: true,
                    name: '含税单价'
                },
                {
                    data: 'money',
                    type: 'number',
                    disabled: true,
                    name: '含税金额',
                    formula: '"unit_price_text" * (0 - "quantity")'
                },
                {
                    data: 'rate_string',
                    type: 'simpleDropdown',
                    subffix: '%',
                    list: [
                        {
                            key: 0,
                            name: '0%'
                        },
                        {
                            key: 1,
                            name: '1%'
                        },
                        {
                            key: 3,
                            name: '3%'
                        },
                        {
                            key: 6,
                            name: '6%'
                        },
                        {
                            key: 9,
                            name: '9%'
                        },
                        {
                            key: 13,
                            name: '13%'
                        }
                    ],
                    disabled: false,
                    name: '税率'
                },
                {
                    data: 'produce_date',
                    type: 'input',
                    disabled: true,
                    name: '生产日期'
                },
                {
                    data: 'overdue_date',
                    type: 'input',
                    disabled: true,
                    name: '失效日期'
                },
                {
                    data: 'type',
                    type: 'input',
                    disabled: true,
                    name: '类别'
                },
                {
                    data: 'mdrf_inner_sn',
                    type: 'input',
                    disabled: true,
                    name: '注册证号'
                },
                {
                    data: 'health_care_sn',
                    type: 'input',
                    disabled: true,
                    name: '医保编号'
                }
            ],
            historyColumns: [
                {
                    data: 'idx',
                    type: 'header',
                    disabled: true,
                    name: ''
                },
                {
                    data: 'grn_sn',
                    type: 'input',
                    disabled: true,
                    name: '单据编号'
                },
                {
                    data: 'organization_name',
                    type: 'input',
                    disabled: true,
                    name: '供货单位'
                },
                {
                    data: 'warehouse_name',
                    type: 'input',
                    disabled: true,
                    name: '仓库'
                },
                {
                    data: 'handler_name',
                    type: 'input',
                    disabled: true,
                    name: '经手人'
                },
                {
                    data: 'warehouse_operator_name',
                    type: 'input',
                    disabled: true,
                    name: '仓库人员'
                },
                {
                    data: 'type_name',
                    type: 'input',
                    disabled: true,
                    name: '类别'
                },
                {
                    data: 'status_string',
                    type: 'input',
                    disabled: true,
                    name: '状态'
                },
                {
                    data: 'making_people_name',
                    type: 'input',
                    disabled: true,
                    name: '制单人'
                },
                {
                    data: 'description',
                    type: 'input',
                    disabled: true,
                    name: '备注'
                },
                {
                    data: 'in_time',
                    type: 'input',
                    disabled: true,
                    name: '入库时间'
                }
            ],
            columns: [],
            widths: [],
            menu: {
                visible: false,
                data: [],
                position: {
                    top: undefined,
                    left: undefined
                }
            },
            editCellRow: undefined,
            sheetPagination: {
                loading: false,
                total: 30,
                current: 1, // 对应后端的 pageSize
                pageSize: 30, // 对应后端的 pageNum
                pageNum: 1,
                available: true
            },
            pageName: 'buyReturn',
            venTableData: [],
            cookieInfo: this.$cookie,

            userBelong: parseInt(this.$cookie.get('userbelong')),
            DetailTableColumnsGrnAllot,
            DetailTableColumnsOdoAllot,
            isClickRedDashedOrder: false, // 是否点击了生成红冲出库单按钮，默认没有
            billsTypeObj: {
                0: 'DD',
                1: 'DV',
                2: 'DH',
                3: 'HH',
                4: 'HD',
                5: 'HV'
            }, // 入库单单据类型
            userSystemType: parseInt(this.$cookie.get('userSystemType')),
            sheelStatus: ['待生成出库单', '出库单已完全生成', '已领取'],
            sl_inner_sn_m: 0,
            org_id_m: 0,
            odrder_type_m: 0,
            voucher_type_c: 0,
            voucher_inner_sn_c: 0,
            allWarehouseList: [],
            sheelID: '',
            sheelType: true,
            sheelListData: [], // 记录调单的数据
            sheelListDataSame: [], // 记录调单的数据合并其中相同来源的数据
            isSheel: false,
            sheelInfoModal: {
                title: `${this.$route.meta.title} - 产品选择`,
                alert: false,
                loading: false
            },
            stoModalFnIsClick: true,
            pageNameType: 'godown-entry', // 入库单
            barcodeParentObj: {},
            barcodeChildObj: {
                batch_number: '',
                detail_inner_sn: '',
                grn_inner_sn: '',
                hasBarcode: '',
                mdrf_inner_sn: '',
                mdrf_sn: '',
                org_id: '',
                overdue_date: '',
                produce_date: '',
                product_inner_sn: '',
                product_name: '',
                product_type: '',
                quantity: '',
                sort_number: '',
                specification: '',
                specification_inner_sn: '',
                type: '',
                unit_inner_sn: '',
                unit_name: '',
                unit_price_text: '',
                vendor_inner_sn: ''
            },
            editGrnTypeFlag: false, // 是否修改过单位名称
            chooseOrgMd: {
                title: `${this.$route.meta.title} - ${this.companyName}`,
                alert: false,
                loading: false
            },
            // 添加产品
            addProductMd: {
                title: `${this.$route.meta.title} - 新增 - 添加产品`,
                alert: false,
                loading: false
            },
            partB: {
                // 新增产品窗口给子组件传递值
                org_id: this.$cookie.get('userbelong'),
                modalType: -1, // 1 - 合同内的产品  2 - 期初库存的产品列表  3 - 采购计划  4 - 采购/销售订单  7 - 入库单  8 - 出库单
                oname: '', // 厂商/医院/经销商名称
                otype: '', // 是厂商/医院/经销商  0 - 经销商   1 - 厂商   2 - 医院
                oid: '',
                warehouse_inner_sn: ''
            },
            generateBarcodeModal: {
                title: `${this.$route.meta.title} - 生成内部条码`,
                alert: false,
                loading: false
            },
            beyondInventoryModal: {
                title: `${this.$route.meta.title} - 实际库存`,
                alert: false,
                datas: []
            },
            addReturnListModal: {
                title: `${this.$route.meta.title} - 添加产品`,
                alert: false,
                loading: false
            },
            isWareHouseFilterLoading: true,
            warehouseNameFocus: false,
            wareHouseSearchData: [],
            wareHouseFilterData: [],
            recordMdrf: {},
            godownEntryColumns: [],
            godownEntryDatas: [], // 医院主体数据
            godownEntryPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            systemTypes: [0, 1, 2, 3], // 0 平台、 1医院 、 2 经销商、 3 厂商
            grnStatus: [],
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            isCopyLoading: false,
            currentGrnTypeListTrObj: {},
            currentTrAddInfoObj: {},
            searchTables: {
                handler_name: '',
                warehouse_model_inner_sn: '',
                grn_odo_sn: '',
                begin_date: '',
                end_date: '',
                status: '',
                key: '',
                product_name: ''
            }, // 表格的搜索并向后端请求字段
            // 审核状态的下拉颜色高亮显示、箭头的朝向改变
            menuItemData: {
                class: true,
                arrow: false
            },
            format: 'YYYY-MM-DD',
            // 修改弹框的配置
            editInfoModal: {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            },
            // 新增弹框的配置
            addInfoModal: {
                title: `${this.$route.meta.title}`,
                alert: false,
                saveLoading: false,
                auditLoading: false,
                godownEntryLoading: false,
                reviewLoading: false
            },
            storageAllocatModal: {
                title: `${this.$route.meta.title} - 储位分配单`,
                alert: false,
                saveLoading: false
            },
            storageAllocatColumns: [
                {
                    title: 'NO.',
                    dataIndex: 'index',
                    key: 'index',
                    width: '5%',
                    align: 'center',
                    scopedSlots: { customRender: 'index' }
                },
                {
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    width: '9%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    width: '8%',
                    align: 'center'
                },
                {
                    title: '厂商/经销商',
                    dataIndex: 'product_vendor_name',
                    key: 'product_vendor_name',
                    width: '11%',
                    align: 'center'
                },
                {
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    width: '7%',
                    align: 'center'
                },
                {
                    title: '应放数量',
                    dataIndex: 'quantity',
                    key: 'quantity',
                    width: '9%',
                    align: 'center',
                    scopedSlots: { customRender: 'quantity' }
                },
                {
                    title: '实放数量',
                    dataIndex: 'real_quantity',
                    key: 'real_quantity',
                    width: '8%',
                    align: 'center'
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit',
                    key: 'unit',
                    width: '9%',
                    align: 'center'
                },
                {
                    title: '存储位置',
                    dataIndex: 'location',
                    key: 'location',
                    width: '10%',
                    align: 'center',
                    scopedSlots: { customRender: 'location' }
                },
                {
                    title: '位置描述',
                    dataIndex: 'location_msg',
                    key: 'location_msg',
                    width: '12%',
                    align: 'center',
                    scopedSlots: { customRender: 'location_msg' }
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    key: 'action',
                    width: '12%',
                    align: 'center',
                    scopedSlots: { customRender: 'action' }
                }
            ],
            storageAllocatDatas: [],
            storageAllocatOldDatas: [], // 用于比对新老值的储位分配明细值
            storageAllocatLoading: false,
            currentTrStorageAllocationObj: {},
            auditStatus: ['草稿', '申领', '部分发放', '全部发放', '申领终止'], // 医院领用单状态(status): 0-草稿 1-申领 2-部分发放 3-全部发放(自然终止) 4-申领终止(人为终止)
            billLadingColumns: [
                {
                    field: 'gv_sn',
                    title: '单据编号',
                    minWidth: '250px'
                },
                {
                    field: 'department_name',
                    title: '部门',
                    dataIndex: 'department_name',
                    key: 'department_name',
                    minWidth: '200px',
                    align: 'center'
                },
                {
                    field: 'warehouse_name',
                    title: '仓库',
                    dataIndex: 'warehouse_name',
                    key: 'warehouse_name',
                    minWidth: '250px',
                    align: 'center'
                },
                {
                    field: 'approver_name',
                    title: '发放人',
                    dataIndex: 'approver_name',
                    key: 'approver_name',
                    minWidth: '200px',
                    align: 'center'
                },
                {
                    field: 'grant_time',
                    title: '发放时间',
                    dataIndex: 'grant_time',
                    key: 'grant_time',
                    minWidth: '',
                    align: 'center'
                }
            ],
            billLadingColumnsSale: [
                {
                    field: 'sl_sn',
                    title: '单据编号',
                    dataIndex: 'sl_sn',
                    key: 'sl_sn',
                    minWidth: '350px',
                    align: 'center'
                },
                {
                    field: 'organization_name',
                    title: '收货单位',
                    dataIndex: 'organization_name',
                    key: 'organization_name',
                    minWidth: '200px',
                    align: 'center'
                },
                {
                    field: 'seller_name',
                    title: '销售员',
                    dataIndex: 'seller_name',
                    key: 'seller_name',
                    minWidth: '200px',
                    align: 'center'
                },
                {
                    field: 'department_name',
                    title: '部门',
                    dataIndex: 'department_name',
                    key: 'department_name',
                    minWidth: '200px',
                    align: 'center'
                }
            ],
            billLadingDatas: [],
            currentTrBillLadingObj: {},
            getSheel: {},
            billLadingPagination: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1
            },
            godownEntryTransferModal: {
                title: `${this.$route.meta.title} - 调单`,
                alert: false,
                saveLoading: false
            },
            billLadingModal: {
                title: `${this.$route.meta.title} - 调单`,
                alert: false,
                saveLoading: false
            },
            billLadingBotColumns: [
                {
                    title: '',
                    dataIndex: '',
                    minWidth: '5%',
                    type: 'checkbox',
                    align: 'center'
                },
                {
                    field: 'product_name',
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    minWidth: '150px',
                    align: 'center'
                },
                {
                    field: 'specification',
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    minWidth: '120px',
                    align: 'center'
                },
                {
                    field: 'vendor_name',
                    title: '厂商/经销商',
                    dataIndex: 'vendor_name',
                    key: 'vendor_name',
                    minWidth: '150px',
                    align: 'center'
                },
                {
                    field: 'batch_number',
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    minWidth: '120px',
                    align: 'center'
                },
                {
                    field: 'grant_quantity',
                    title: '批准发放量',
                    dataIndex: 'grant_quantity',
                    key: 'grant_quantity',
                    minWidth: '120px',
                    align: 'center'
                },
                {
                    field: 'unit_name',
                    title: '包装单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
                    minWidth: '100px',
                    align: 'center'
                },
                {
                    field: 'status_name',
                    title: '状态',
                    dataIndex: 'status_name',
                    key: 'status_name',
                    minWidth: '100px',
                    align: 'center'
                }
            ],
            billLadingBotColumnsSale: [
                {
                    title: '',
                    dataIndex: '',
                    minWidth: '5%',
                    type: 'checkbox',
                    align: 'center'
                },
                {
                    field: 'product_name',
                    title: '产品',
                    dataIndex: 'product_name',
                    key: 'product_name',
                    minWidth: '200px',
                    align: 'center'
                },
                {
                    field: 'specification',
                    title: '规格型号',
                    dataIndex: 'specification',
                    key: 'specification',
                    minWidth: '100px',
                    align: 'center'
                },
                {
                    field: 'product_vendor_name',
                    title: '厂商/经销商',
                    dataIndex: 'product_vendor_name',
                    key: 'product_vendor_name',
                    minWidth: '200px',
                    align: 'center'
                },
                {
                    field: 'batch_number',
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    minWidth: '100px',
                    align: 'center'
                },
                {
                    field: 'quantity',
                    title: '数量',
                    dataIndex: 'quantity',
                    key: 'quantity',
                    minWidth: '100px',
                    align: 'center'
                },
                {
                    field: 'unit_name',
                    title: '包装单位',
                    dataIndex: 'unit_name',
                    key: 'unit_name',
                    minWidth: '100px',
                    align: 'center'
                },
                {
                    field: 'outOfStock_quantity',
                    title: '已出库数量',
                    dataIndex: 'outOfStock_quantity',
                    key: 'outOfStock_quantity',
                    minWidth: '120px',
                    align: 'center'
                },
                {
                    field: 'toBeOutOfStock_quantity',
                    title: '待出库数量',
                    dataIndex: 'toBeOutOfStock_quantity',
                    key: 'toBeOutOfStock_quantity',
                    minWidth: '120px',
                    align: 'center'
                }
            ],
            billLadingBotDatasAll: [],
            billLadingBotDatas: [],
            billLadingBotSelectedRowKeys: [],
            currentTrBillLadingBotObj: {},
            billLadingBotLoading: false,
            billLadingVoucherTypeList: [],
            billLadingForm: {
                voucher_type: 1
            },
            batchInfoModal: {
                title: `${this.$route.meta.title} - 批号选择`,
                alert: false,
                loading: false
            },
            batchColumns: [
                {
                    title: '批号',
                    dataIndex: 'batch_number',
                    key: 'batch_number',
                    width: '24%',
                    align: 'center'
                },
                {
                    title: '生产日期',
                    dataIndex: 'produce_date',
                    key: 'produce_date',
                    width: '18%',
                    align: 'center'
                },
                {
                    title: '失效日期',
                    dataIndex: 'overdue_date',
                    key: 'overdue_date',
                    width: '18%',
                    align: 'center'
                },
                // {
                //     title: '最小包装单位库存',
                //     dataIndex: 'quantity',
                //     key: 'quantity',
                //     width: '20%',
                //     align: 'center'
                // },
                {
                    title: '实际可用库存',
                    dataIndex: 'batch_locked_quantity',
                    key: 'batch_locked_quantity',
                    width: '20%',
                    align: 'center'
                },
                {
                    title: '平均单价',
                    dataIndex: 'average_price',
                    key: 'average_price',
                    width: '20%',
                    align: 'center'
                }
            ],
            batchDatas: [],
            addGrnTypeListModal: {
                title: `${this.$route.meta.title} - 单据类别 - 编辑`,
                alert: false,
                loading: false
            },
            grnTypeAddForm: {
                name: '',
                status: 1,
                checked: true
            },
            gotdownInfoModal: {
                title: `${this.$route.meta.title} - 产品选择`,
                alert: false,
                loading: false,
                data: [],
                chooseData: [],
                columns: [
                    {
                        title: '',
                        dataIndex: '',
                        minWidth: '5%',
                        type: 'checkbox',
                        align: 'center'
                    },
                    {
                        field: 'product_name',
                        title: '产品',
                        dataIndex: 'product_name',
                        key: 'product_name',
                        minWidth: '200px',
                        align: 'center'
                    },
                    {
                        field: 'specification',
                        title: '规格型号',
                        dataIndex: 'specification',
                        key: 'specification',
                        minWidth: '100px',
                        align: 'center'
                    },
                    {
                        field: 'product_vendor_name',
                        title: '厂商/经销商',
                        dataIndex: 'product_vendor_name',
                        key: 'product_vendor_name',
                        minWidth: '200px',
                        align: 'center'
                    },
                    {
                        field: 'batch_number',
                        title: '批号',
                        dataIndex: 'batch_number',
                        key: 'batch_number',
                        minWidth: '100px',
                        align: 'center'
                    },
                    {
                        field: 'quantity',
                        title: '数量',
                        dataIndex: 'quantity',
                        key: 'quantity',
                        minWidth: '80px',
                        align: 'center'
                    },
                    {
                        field: 'unit',
                        title: '包装单位',
                        dataIndex: 'unit_name',
                        key: 'unit',
                        minWidth: '80px',
                        align: 'center'
                    },
                    {
                        field: 'unit_price_text',
                        title: '含税单价',
                        dataIndex: 'unit_price_text',
                        key: 'unit_price_text',
                        minWidth: '80px',
                        align: 'center'
                    },
                    {
                        field: 'price',
                        title: '含税金额',
                        dataIndex: 'price',
                        key: 'price',
                        minWidth: '80px',
                        align: 'center'
                    },
                    {
                        field: 'mdrf_sn',
                        title: '注册证号',
                        dataIndex: 'mdrf_sn',
                        key: 'mdrf_sn',
                        minWidth: '150px',
                        align: 'center'
                    }
                ]
            },
            making_people_name: '',
            auditor_name: '',
            reviewer_name: '',
            addModalEmployes: [],
            superiorDepartments: [],
            isLoadingDepartmentInnerSn: true,
            handlerNameList: [],
            addInfoModalForm: {
                department_inner_sn: '',
                department_name: '',
                use_department: '', // 使用科室
                description: '',
                details: '',
                grn_sn: '',
                odo_sn: '',
                odo_type: '',
                handler_inner_sn: '',
                handler_name: '',
                org_id: '',
                organization_inner_sn: '',
                organization_name: '',
                status: '',
                type_inner_sn: '',
                type_name: '',
                warehouse_inner_sn: '',
                warehouse_name: '',
                warehouse_operator_inner_sn: '',
                warehouse_operator_name: '',
                billsType: ''
            },
            storageAllocatModalOldForm: {
                department_inner_sn: '',
                department_name: '',
                description: '',
                details: '',
                grn_sn: '',
                odo_sn: '',
                odo_type: '',
                handler_inner_sn: '',
                handler_name: '',
                org_id: '',
                organization_inner_sn: '',
                organization_name: '',
                status: '',
                type_inner_sn: '',
                type_name: '',
                warehouse_inner_sn: '',
                warehouse_name: '',
                warehouse_operator_inner_sn: '',
                warehouse_operator_name: '',
                billsType: ''
            }, // 储位分配旧数据
            addGrnTypeModal: {
                title: `${this.$route.meta.title} - 单据类别`,
                alert: false,
                loading: false
            },
            addInfoDatas: [],
            addInfoTempDatas: [],
            isAddInfoTempData: false,
            typeList: [
                {
                    label: '常规',
                    value: '0'
                },
                {
                    label: '赠品',
                    value: '1'
                }
            ],
            batchRemoveTempObj: {},
            batchRepeatKeyObj: {},
            delInfoDatas: [],
            isEditBatchinventoryBannce: false,
            isBatchLadingFlag: false, // 是否触发了调单按钮
            addInfoLoading: false,
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            btnName: '',
            addInfoModalTitleFlag: false,
            btnNameAddGrnTypeList: '',
            grnTypeList: [],
            isGrnTypeLoading: true,
            warehouseOperatorList: [],
            editInfoFormOldObj: {}, // 修改信息的模态框值 - 后端返回回来的旧值
            editInfoFormNewObj: {}, // 修改信息的模态框值 - 前端根据页面修改的值放进去 【没有修改的不要向后端传递】
            editInfoFormObj: {
                // 修改信息的模态框值 - 后端返回回来的
                department_inner_sn: '',
                department_name: '',
                description: '',
                details: '',
                grn_sn: '',
                billsType: '',
                billsType_old: '',
                handler_inner_sn: '',
                handler_name: '',
                org_id: '',
                organization_inner_sn: '',
                organization_name: '',
                status: '',
                type_inner_sn: '',
                type_name: '',
                warehouse_inner_sn: '',
                warehouse_name: '',
                warehouse_operator_inner_sn: '',
                warehouse_operator_name: ''
            },
            produce_date_o: '',
            overdue_date_o: '',
            grnTypeAddLoading: false,
            grnTypeAddColumns: [
                {
                    title: '类别',
                    filed: 'name',
                    key: 'name',
                    width: 350,
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [<span>{row.name}</span>];
                        }
                    }
                },
                {
                    title: '状态',
                    filed: 'status_text',
                    key: 'status_text',
                    width: 200,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [<span class={{ 'red': row.status === 0 }}>{row.status_text}</span>];
                        }
                    }
                }
            ],
            grnTypeAddDatas: [],
            rangePickerVal: moment[''], // 申请时间的默认赋值
            pageTypeName: -1, // 单位名称页面的标识： 0-【仓库管理-入库单】  2【仓库管理-出库单】
            isAddProductModalTableMax: false, // 是否是全屏模式，默认不是
            searchOrgName: '',
            isParentKeyDownEnter: false,
            isChooseOrgMdTip: false,
            addProductEnter: false,
            relevantInstitutionsFocus: true,
            locationList: [],
            recordLocation: {},
            record: {},
            firstScan: true,
            tabChoose: 1,
            create_time: '',
            tabTypes: ['入库单', '储位分配单'],
            tabTypesNum: [1, 2], // 1 入库单、 2储位分配单
            stoEntryDatas: [], // 储位分配单主体数据
            stoType: 0, // 储位分配单   0新增  1编辑
            stoRefresh: true, // 储位分配单是否刷新
            stoEntryPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            currentTrObjSto: {}, // 表格选中的当前行数据 / 标识
            stoTypeAddDatas: [],
            stoModelStatus: 0,
            stoTypeList: [],
            storageAllocatDatasDrag: {}, // 用来储存拖动时的临时变量
            currentStoTypeListTrObj: {},
            stoStatus: ['待放置', '部分放置', '作业结束'],
            modalmask: true, // 模态框状态
            dargShow: false, // 拖动层
            recordRrnInnerSn: 0,
            firstCome: true,
            tableHeadModal: {
                title: `${this.$route.meta.title} - 储位分配单 - 添加产品`,
                alert: false,
                loading: false
            },
            stoQuaryNum: [],
            addListAllDatas: [], // 所有可添加的数据
            addListChooseDatas: [], // 选中添加的数据
            stoEntryColumns: [
                {
                    title: '单据编号',
                    dataIndex: 'sas_sn',
                    key: 'sas_sn',
                    minWidth: '10%',
                    align: 'center'
                },
                {
                    title: '仓库',
                    dataIndex: 'warehouse_name',
                    key: 'warehouse_name',
                    minWidth: '15%',
                    align: 'center'
                },
                {
                    title: '作业人',
                    dataIndex: 'operator_name',
                    key: 'operator_name',
                    minWidth: '15%',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    minWidth: '15%',
                    align: 'center'
                },
                {
                    title: '制单人',
                    dataIndex: 'making_people_name',
                    key: 'making_people_name',
                    minWidth: '15%',
                    align: 'center'
                },
                {
                    title: '制单时间',
                    dataIndex: 'create_time',
                    key: 'create_time',
                    minWidth: '20%',
                    align: 'center'
                }
            ],
            SheelHasWarehouse: false,
            healthAndSunSnModal: {
                data: []
            },
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 57 : 0,
            tax_amount_total: 0,
            exporting: false,
            grnOrgData: []
        };
    },
    methods: {
        calcPagination(total) {
            this.sheetPagination.total = total;
            this.sheetPagination.available = (this.sheetPagination.pageNum * this.sheetPagination.pageSize) < this.sheetPagination.total ? true : false;
            this.sheetPagination.pageNum = this.sheetPagination.pageNum + 1;
        },
        sheetScrollToEnd() {
            if (this.curTab !== 'add') {
                if (this.sheetPagination.available) {
                    this.sheetPagination.available = false;
                    switch (this.curTab) {
                        case 'history':
                            this.godownEntryColumns = CommonGrnListColumns;

                            if (!this.c_IsGrn) {
                                // 出库单新增两个字段
                                this.godownEntryColumns = CommonOdoListColumns;
                            }

                            if (this.isSeparateBillOrder) {
                                if (this.orderTypeData.value === grnAndOdoOrderType['PURCHASEGRN'].value) {
                                    this.godownEntryColumns = PurchaseGrnListColumns;
                                }

                                this.searchListAPIFn();
                            } else {
                                if (this.c_IsGrn) {
                                    this.grnListAPIFn();
                                    // this.stoListAPIFn()
                                } else {
                                    this.odoListAPIFn();
                                }
                            }
                            break;
                    }
                }
            }
        },
        setupMenu(tab = 'add') {
            switch (tab) {
                case 'add':
                    this.menu = {
                        visible: false,
                        data: [
                            // {
                            //     disabled: true,
                            //     key: 'insert-above',
                            //     name: '其上插入'
                            // },
                            // {
                            //     disabled: true,
                            //     key: 'insert-below',
                            //     name: '其下插入'
                            // },
                            // {
                            {
                                disabled: false,
                                key: 'copy',
                                name: '复制'
                            },
                            {
                                disabled: false,
                                key: 'cut',
                                name: '剪切'
                            },
                            {
                                disabled: false,
                                key: 'paste',
                                name: '粘贴'
                            },
                            {
                                disabled: false,
                                key: 'delete',
                                name: '删除'
                            }
                        ],
                        position: {
                            top: undefined,
                            left: undefined
                        }
                    };
                    break;
                case 'history':
                    this.menu = {
                        visible: false,
                        data: [
                            {
                                disabled: false,
                                key: 'revise',
                                name: '修改'
                            },
                            {
                                disabled: true,
                                key: 'transfer',
                                name: '调单'
                            },
                            {
                                disabled: true,
                                key: 'unapproval',
                                name: '取消审核'
                            },
                            {
                                disabled: true,
                                key: 'copy',
                                name: '复制'
                            },
                            {
                                disabled: true,
                                key: 'red',
                                name: '生成红冲'
                            },
                            {
                                disabled: true,
                                key: 'delete',
                                name: '删除'
                            }
                        ],
                        position: {
                            top: undefined,
                            left: undefined
                        }
                    };
                    break;
            }
        },
        changeWidths(widths) {
            this.widths = widths;
            let cookieData = JSON.parse(this.cookieInfo.get(this.pageName + '-' + this.curTab));
            let data = {
                widths
            };
            if (cookieData !== null && cookieData.headers !== undefined && cookieData.headers.length > 0) {
                data['headers'] = cookieData.headers;
            }
            this.cookieInfo.set(this.pageName + '-' + this.curTab, JSON.stringify(data));
            // this.postStoreSettings();
        },
        deleteAddData(obj) {
            for (let i = obj.selection.from; i <= obj.selection.to; i ++) {
                this.addData[i] = {
                    ...this.addData[i],
                    product_name: '',
                    specification: '',
                    vendor_name: '',
                    batch_number: '',
                    quantity: '',
                    unit: '',
                    buying_price: '',
                    unit_price_text: '',
                    money: '',
                    rate_string: '',
                    produce_date: '',
                    overdue_date: '',
                    type: '',
                    mdrf_inner_sn: '',
                    health_care_sn: ''
                };
            }
        },
        clickedMenu(obj) {
            if (this.curTab === 'add' && obj.key === 'delete') {
                this.deleteAddData(obj);
                let data = [], index = 0;
                this.addData.forEach((d, idx) => {
                    if (idx < obj.selection.from || idx > obj.selection.to) {
                        d.idx = ++index;
                        data.push(d);
                    }
                });
                this.addData = data;
                return;
            }
            if (this.curTab !== 'add' && obj.key === 'revise') {
                if (this.menu.data[0].disabled === false) {
                    this.editable = {
                        flag: true,
                        obj: this.addData[obj.selection.from]
                    };
                    this.orderEditorTarget = this.addData[obj.selection.from];
                    this.changeTabFn();
                }
            }
            let records = this.getSelectionData(obj.selection.from, obj.selection.to);
            if ((this.curTab === 'add' && obj.key === 'copy') || this.curTab === 'add' && obj.key === 'cut') {
                this.copied.flag = true;
                let objs = [];
                records.forEach((r) => {
                    objs.push(Object.assign({}, r));
                });
                this.copied.objs = objs;
                console.log(this.copied.objs);
                if (obj.key === 'cut') {
                    this.deleteAddData(obj);
                }
            }
            if (this.curTab === 'add' && obj.key === 'paste') {
                this.copied.objs.forEach((r, idx) => {
                    this.addData[obj.selection.from + idx] = Object.assign({}, r);
                });
                this.addData.map((d, idx) => {
                    d.idx = idx + 1;
                    return d;
                });
            }
        },
        /**
         * 产品列表数据加载器
         */
        async loadVenData(value = '', currentPage = 1, pageSize = 10) {
            this.venTableData = [];
            try {
                let params = {
                    org_id: this.cookieInfo.get('userbelong'),
                    organization_inner_sn: this.addInfoModalForm.organization_inner_sn,
                    billsType: this.addInfoModalForm.billsType,
                    pageNum: 1,
                    pageSize: 10,
                    isAsc: 'desc',
                    name: value,
                    orderByColumn: 'grn_sn'
                };
                const { code, msg, rows } = await purchaseReturnProductListAPI({ params });
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.venTableData = rows.map((item) => {
                    return {
                        ...item,
                        key: (Math.random() + 1).toString(36).substring(7),
                        name: item.product_name
                    }
                });
            } catch (err) {
                this.$message.error('产品查询失败');
                throw err;
            }
        },
        searchComData(obj) {
            if (obj.type === 'dropdown') {
                this.loadVenData(obj.value);
            }
        },
        earnOrgType(value) {
            const planListStatus = ['自建', '卖方', '已审核', '已确认', '已终止', '已完结'];
            return planListStatus[value];
        },
        chooseComData(obj) {
            if (obj.type === 'dropdown') {
                if (obj.value === 'search-more') {
                    this.editCellRow = obj.row;
                    this.cellAddInfoDblclickFn(this.addData[obj.row], null);
                } else if (obj.value === 'tips') {
                    return;
                } else {
                    this.selectedVen = Object.assign(
                        {},
                        this.venTableData.find((v) => v.key === obj.value)
                    );
                    this.addData[obj.row] = {
                        ...this.selectedVen,
                        rate_string: this.selectedVen.rate * 100,
                        type: this.selectedVen.product_type === 1 ? '赠品' : '常规',
                        unit_price_text: this.selectedVen.unit_price,
                        tax_unit_price: this.selectedVen.unit_price,
                        vendor_name: this.selectedVen.vendor_name ? this.selectedVen.vendor_name : this.selectedVen.product_vendor_name,
                        idx: obj.row + 1
                    }
                }
            }
        },
        getSelectionData(from, to) {
            let records = [];
            for (let i = from; i <= to; i++) {
                records.push(this.addData[i]);
            }
            return records;
        },
        resetPaginations() {
            this.sheetPagination = {
                loading: false,
                total: 30,
                current: 1, // 对应后端的 pageSize
                pageSize: 30, // 对应后端的 pageNum
                pageNum: 1,
                available: true
            };
        },
        resetSheetData() {
            this.addData = [];
            this.widths = [];
            this.columns = [];
            this.resetPaginations();
            this.searchTables.key = '';
        },
        clearAddData() {
            let data = [];
            for (let i = 0; i < 10; i++) {
                data.push({
                    idx: i + 1,
                    product_name: '',
                    specification: '',
                    vendor_name: '',
                    batch_number: '',
                    quantity: '',
                    unit: '',
                    buying_price: '',
                    unit_price_text: '',
                    money: '',
                    rate_string: '',
                    produce_date: '',
                    overdue_date: '',
                    type: '',
                    mdrf_inner_sn: '',
                    health_care_sn: ''
                });
            }
            return data;
        },
        searchSheetFn(obj) {
            if (this.curTab !== 'add') {
                this.addData = [];
                this.resetPaginations();
                this.searchTables.key = obj.name;
                this.searchListAPIFn();
            }
        },
        changeTabFn(key = 'add') {
            this.curTab = key;
            // reset Sheet
            this.resetSheetData();
            // actions
            switch (key) {
                case 'history':
                    this.actColumns = this.historyColumns;
                    break;
                default:
                    //add
                    this.actColumns = this.addColumns;
                    break;
            }
            // checking headers & widths
            let cookieData = JSON.parse(this.cookieInfo.get(this.pageName + '-' + this.curTab));
            if (cookieData !== null && cookieData.headers !== undefined && cookieData.headers.length > 0) {
                let columns = [];
                this.actColumns.forEach((c) => {
                    if (cookieData.headers.includes(c.data) > 0) {
                        columns.push(c);
                    }
                });
                this.columns = columns;
            } else {
                switch (key) {
                    case 'add':
                        this.totalFormat.on = true;
                        this.columns = this.addColumns;
                        break;
                    case 'history':
                        this.totalFormat.on = false;
                        this.columns = this.historyColumns;
                        break;
                }
            }
            if (cookieData !== null && cookieData.widths !== undefined && cookieData.widths.length > 0) {
                this.widths = cookieData.widths;
            }
            // initial menu
            this.setupMenu(key);
            // actions
            switch (key) {
                case 'history':
                    //searchable
                    this.totalFormat.searchable = true;
                    //load
                    this.godownEntryColumns = CommonGrnListColumns;

                    if (!this.c_IsGrn) {
                        // 出库单新增两个字段
                        this.godownEntryColumns = CommonOdoListColumns;
                    }

                    if (this.isSeparateBillOrder) {
                        if (this.orderTypeData.value === grnAndOdoOrderType['PURCHASEGRN'].value) {
                            this.godownEntryColumns = PurchaseGrnListColumns;
                        }

                        this.searchListAPIFn();
                    } else {
                        if (this.c_IsGrn) {
                            this.grnListAPIFn();
                            // this.stoListAPIFn()
                        } else {
                            this.odoListAPIFn();
                        }
                    }

                    this.getAllWarehouseList();
                    this.warehouseUsableTopListAPIFn();
                    break;
                default:
                    //searchable
                    this.totalFormat.searchable = false;
                    //add
                    if (this.editable.flag) {
                        this.panes[0].title = '编辑';
                        this.grnAndOdoInfoAPIFn();
                    } else {
                        this.panes[0].title = '新增';
                        this.addData = this.clearAddData();
                    }
                    break;
            }
        },
        addNewSheetRow() {
            if (this.curTab === 'add') {
                this.addData.push({
                    idx: this.addData.length + 1,
                    product_name: '',
                    specification: '',
                    vendor_name: '',
                    batch_number: '',
                    quantity: '',
                    unit: '',
                    buying_price: '',
                    unit_price_text: '',
                    money: '',
                    rate_string: '',
                    produce_date: '',
                    overdue_date: '',
                    type: '',
                    mdrf_inner_sn: '',
                    health_care_sn: ''
                });
            }
        },
        multiSelected(obj) {
            if (this.curTab !== 'add') {
                if (obj.from !== obj.to) {
                    let data = [];
                    this.menu.data.forEach((item) => {
                        item.disabled = true;
                        data.push(item);
                    });
                    this.menu.data = data;
                } else {
                    this.currentTrObj = this.addData[obj.from];
                    // this.menu.data[0].disabled = false;
                    // this.menu.data[1].disabled = this.isShowTransferRecordBtn;
                    // this.menu.data[2].disabled = !this.isTableDatasEmpty && currentTrObj.status === 1;
                    // this.menu.data[3].disabled = !this.isTableDatasEmpty && currentTrObj.status === 4;
                    // this.menu.data[4].disabled = !this.isTableDatasEmpty && currentTrObj.status === 2 && currentTrObj.type_inner_sn !== 2;
                    // this.menu.data[5].disabled = !this.isTableDatasEmpty && currentTrObj.status === 0;
                }
                if (this.curTab === 'add') {
                    this.menu.data[2].disabled = !this.copied.flag;
                }
            }
        },
        onUpdateSheet(records) {
            if (records) {
                records.forEach((item, idx) => {
                    item.rate_string = item.rate * 100;
                    item.percent_string = item.percent + '%';
                    this.addData.splice(this.editCellRow + idx, 0, item);
                });
                this.addData = this.addData.map((item, idx) => {
                    return {
                        ...item,
                        idx: idx + 1
                    };
                });
            }
        },
        getStableData() {
            let data = [],
                firstColumn = this.columns[1];
            this.addData.forEach((item) => {
                if (item[firstColumn.data] !== '') {
                    data.push(item);
                }
            });
            return data;
        },

        /// 出/入库单中[作废-4 | 红冲-5状态的单据]的文字样式
        getRedDashedOrderClassName({ row }) {
            const status = parseInt(row.status);
            if (status === 4 || status === 5) {
                return 'red-dashed-odo-order';
            }
        },
        sheelOnchangeFn(e) {
            this.sheelType = e.target.checked;
            this.billLadingBotDatas = [];
            if (this.sheelType) {
                this.billLadingBotDatasAll.forEach((item) => {
                    if (item.status === 0) {
                        this.billLadingBotDatas.push(item);
                    }
                });
            } else {
                this.billLadingBotDatas = this.billLadingBotDatasAll;
            }
            if (this.$refs.sheelTable) this.$refs.sheelTable.updateData();
        },
        stoCloseWarn() {
            let change = 0;
            this.storageAllocatOldDatas.forEach((item) => {
                if (item.action === 0) change++;
            });
            this.storageAllocatDatas.forEach((item) => {
                if (item.action === 1) {
                    change++;
                    return;
                }
                this.storageAllocatOldDatas.forEach((itemOld) => {
                    if (item.detail_inner_sn === itemOld.detail_inner_sn) {
                        let checkRes = this.checkEditObjFn(itemOld, item);
                        if (!checkRes.editInfoFlag) {
                            change++;
                        }
                    }
                });
            });
            if (this.storageAllocatModal.alert && change > 0) {
                this.$warning({
                    title: '操作提示',
                    content: '当前入库单的“储位分配单”有修改且尚未保存，请先保存储位分配单',
                    okText: '关闭',
                    centered: true,
                    maskClosable: true
                });
            } else {
                this.closeAddInfoModalResetScrollFn();
                this.resetAddInfoModalFn();
                this.resetStorageAllocatModalInfoModalFn();
                this.addInfoModal.saveLoading = false;
                this.addInfoModal.auditLoading = false;
                this.addInfoModal.godownEntryLoading = false;
            }
        },
        // 【反审核入库单】
        async grnUnreviewAPIFn() {
            const params = {
                org_id: this.currentTrObj.org_id,
                grn_inner_sn: this.currentTrObj.grn_inner_sn,
                billsType: this.currentTrObj.billsType
            };
            await grnUnreviewAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.grnAndOdoUnreviewSyncDataFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.godownEntryPagination.loading = false;
                });
        },
        // 【反审核出库单】
        async odoUnreviewAPIFn() {
            const params = {
                org_id: this.currentTrObj.org_id,
                odo_inner_sn: this.currentTrObj.odo_inner_sn,
                billsType: this.currentTrObj.billsType
            };
            await odoUnreviewAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.grnAndOdoUnreviewSyncDataFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.godownEntryPagination.loading = false;
                });
        },
        // 【红冲 = 入库单】
        async grnRedDashedAPIFn() {
            const params = {
                org_id: this.currentTrObj.org_id,
                grn_inner_sn: this.currentTrObj.grn_inner_sn,
                billsType: this.currentTrObj.billsType
            };
            await grnRedDashedAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.grnAndOdoRedDashedFn(info);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.godownEntryPagination.loading = false;
                });
        },
        // 【红冲 = 出库单】
        async odoRedDashedAPIFn() {
            const params = {
                org_id: this.currentTrObj.org_id,
                odo_inner_sn: this.currentTrObj.odo_inner_sn,
                billsType: this.currentTrObj.billsType
            };
            await odoRedDashedAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.grnAndOdoRedDashedFn(info);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.godownEntryPagination.loading = false;
                });
        },
        // 【复核出库单】
        async odoReviewAPIFn() {
            const xTable = this.$refs.xTable,
                curObj = xTable.getCurrentRow(),
                params = {
                    org_id: this.addInfoModalForm.org_id,
                    odo_inner_sn: this.addInfoModalForm.odo_inner_sn,
                    billsType: this.addInfoModalForm.billsType
                };
            await odoReviewAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        // let json = JSON.parse(this.$cookie.get('loginDatas'))
                        for (let i = 0; i < this.godownEntryDatas.length; i++) {
                            const item = this.godownEntryDatas[i];
                            if (item._XID === curObj._XID) {
                                item.status = 3; // 已出库复核
                                item.status_text = this.grnStatus[parseFloat(item.status)];
                                // item.reviewer_name = json.EmployeeInfo.Name // 复核人姓名
                                xTable.updateData();
                                xTable.setCurrentRow(item);
                                break;
                            }
                        }
                        this.closeAddInfoModalResetScrollFn();
                        this.resetAddInfoModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.addInfoModal.reviewLoading = false;
                })
                .catch(() => {
                    this.addInfoModal.reviewLoading = false;
                });
        },
        // 【获取储位分配单-检测是否存在】
        async stoModalFn() {
            if (!this.stoModalFnIsClick) return;
            this.stoModalFnIsClick = false; // 控制用户不狂点此方法
            this.recordRrnInnerSn = this.addInfoModalForm.grn_inner_sn
                ? this.addInfoModalForm.grn_inner_sn
                : this.recordRrnInnerSn;
            const params = {
                org_id: this.addInfoModalForm.org_id,
                warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                grn_inner_sn: this.recordRrnInnerSn,
                billsType: this.addInfoModalForm.billsType
            };
            await stoCheckAPI({ params })
                .then((res) => {
                    this.stoModalFnIsClick = true;
                    if (parseFloat(res.code) === 0) {
                        const { info, isNew } = res;
                        const that = this;
                        if (isNew) {
                            that.stoType = 0;
                        } else {
                            that.stoType = 1;
                        }
                        switch (that.stoType) {
                            case 0: {
                                this.$confirm({
                                    title: '操作提示',
                                    content: '当前入库单没有对应的“储位分配单”，是否需要生成？',
                                    centered: true,
                                    maskClosable: false,
                                    onOk() {
                                        setTimeout(() => {
                                            document.getElementsByClassName(
                                                'ant-modal-wrap ant-modal-centered'
                                            )[1].style.pointerEvents = 'none';
                                        }, 1000);
                                        that.addInfoModalForm.status = 2; // 为了让储位分配单按钮，持续显示为2

                                        that.making_people_name = ''; // 生成新储位分配单时  清除之前的制单人和制单时间
                                        that.create_time = '';

                                        that.addInfoModalForm.warehouse_operator_inner_sn = '';
                                        that.locationList = [];

                                        that.stoGetInfoAPIFn();
                                        that.stoType = 0; // 进修改的查询，类型改为修改
                                        // that.grnStorageAPIFn(that.currentTrObj.org_id, info.begin_inventory, info.end_inventory)
                                        // that.grnStorageAPIFn(that.currentTrObj.org_id, 1, 1)
                                        that.modalmask = false;
                                        that.dargShow = true;
                                        that.storageAllocatModal.alert = true;

                                        that.$Utils.globalDragCenterFn('modal-add', '', 'left');
                                        that.$Utils.globalDragCenterFn('modal-storage-allocation-sheet', '', 'right');
                                        return new Promise((resolve, reject) => {
                                            setTimeout(Math.random() > 0.5 ? resolve : reject, 800);
                                        }).catch(() => {});
                                    },
                                    onCancel() {}
                                });
                                break;
                            }
                            case 1: {
                                that.warehouseInfoListAPIFn(info.warehouse_inner_sn);
                                that.stoSet(info, 1);
                                break;
                            }
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.stoModalFnIsClick = true;
                    this.storageAllocatLoading = false;
                });
        },
        stoSet(info) {
            setTimeout(() => {
                document.getElementsByClassName('ant-modal-wrap ant-modal-centered')[1].style.pointerEvents = 'none';
            }, 1000);
            this.stoType = 1; // 进修改的查询，类型改为修改
            this.modalmask = false;
            this.dargShow = true;
            this.storageAllocatModal.alert = true;
            this.stoModelStatus = info.status;
            this.$Utils.globalDragCenterFn('modal-add', '', 'left');
            this.$Utils.globalDragCenterFn('modal-storage-allocation-sheet', '', 'right');
            this.orgEmpDropdownListAPIFn(); // 经手人
            this.grnWeListAPIFn();
            this.stoQuaryNum = [];
            let isShowLocation = this.locationList && this.locationList.length > 0;
            const infos = info.details.map((item, index) => {
                return {
                    ...item,
                    // location: [], // 存储位置 - 储位分配
                    location_inner_sn: item.location_inner_sn ? [item.location_inner_sn] : [], // 存储位置id - 储位分配
                    location_name: isShowLocation ? item.location : '', // 位置信息 - 储位分配 存储位置
                    location_msg: isShowLocation ? item.location : '', // 位置信息 - 储位分配 位置描述
                    // real_quantity: 0, // 实放数量 - 储位分配
                    key: Math.random(),
                    index,
                    activeClass: '',
                    type: item.type + '',
                    produce_date: item.produce_date && moment(item.produce_date),
                    overdue_date: item.overdue_date && moment(item.overdue_date),
                    // item.name = item.product_name
                    // item.mdrfList = item.mdrf_sn ? [{value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn}] : []
                    unitList: item.unit_inner_sn ? [{ value: item.unit_inner_sn, label: item.unit_name }] : [],
                    unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                    isLoadingUnit: false, // 是否加载过整棵树
                    unit: item.unit_name,
                    quantity: item.expect_quantity
                };
            });
            this.storageAllocatDatas = infos;
            this.storageAllocatOldDatas = infos;
            this.addInfoModalForm = {
                ...this.addInfoModalForm,
                ...info,
                grn_sn: this.addInfoModalForm.grn_sn, // 补充
                organization_name: this.addInfoModalForm.organization_name, // 补充
                type_inner_sn: this.addInfoModalForm.type_inner_sn, // 补充
                department_inner_sn: this.addInfoModalForm.department_inner_sn, // 补充
                handler_inner_sn: this.addInfoModalForm.handler_inner_sn, // 补充
                warehouse_operator_inner_sn: [info.operator_inner_sn],
                warehouse_operator_name: info.operator_name || '', // 作业人名称
                status: 2 // 需要让储位分配单持续为2
            };
            this.getStoQuaryNum();
            this.making_people_name = info.making_people_name;
            this.create_time = info.create_time;
            this.warehouseChildrenListAPIFn(info.warehouse_inner_sn, this.locationList, infos, 'previewsto');
        },
        async getStoQuaryNum() {
            const params = {
                org_id: this.addInfoModalForm.org_id,
                warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                grn_inner_sn: this.addInfoModalForm.grn_inner_sn,
                billsType: this.addInfoModalForm.billsType
            };
            await stoGetInfoAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.stoQuaryNum = list;
                        // this.addListAllDatas = list
                    } else {
                        this.stoQuaryNum = [];
                        // this.addListAllDatas = []
                    }
                })
                .catch(() => {});
        },
        async stoReOneAPIFn(infoMode) {
            const params = {
                org_id: this.addInfoModalForm.org_id,
                warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                grn_inner_sn: this.addInfoModalForm.grn_inner_sn,
                billsType: this.addInfoModalForm.billsType,
                product_vendor_inner_sn: infoMode.vendor_inner_sn,
                product_inner_sn: infoMode.product_inner_sn,
                specification_inner_sn: infoMode.specification_inner_sn
            };
            await stoGetInfoAPI({ params })
                .then((res) => {
                    this.storageAllocatLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        if (list.length === 0) return;
                        this.storageAllocatDatasDrag = {
                            ...list[0],
                            detail_inner_sn: '',
                            action: 1,
                            sort_number: this.storageAllocatDatas.length,
                            location_inner_sn: [],
                            location: '',
                            real_quantity: 0, // 这个字段获取无门，只能先写死
                            unit: list[0].unit_name // 修复字段别名
                        };
                        this.storageAllocatDatas = [...this.storageAllocatDatas, this.storageAllocatDatasDrag];
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {});
        },
        selectAllEvent({ checked }) {
            if (checked) {
                this.addListChooseDatas = this.addListAllDatas;
            } else {
                this.addListChooseDatas = [];
            }
        },
        selectChangeEvent({ checked, row }) {
            if (checked) {
                this.addListChooseDatas = [...this.addListChooseDatas, row];
            } else {
                this.addListChooseDatas.forEach((item, index) => {
                    if (item === row) {
                        this.addListChooseDatas.splice(index, 1);
                    }
                });
            }
        },
        DblClick_event({ row }) {
            if (
                typeof this.addListChooseDatas.find((e) => e.grn_detail_inner_sn === row.grn_detail_inner_sn) ===
                'undefined'
            ) {
                this.addListChooseDatas.push(row);
            }
            this.stoAddListSave();
        },
        selectAllEvent_sheel({ checked }) {
            if (checked) {
                this.billLadingBotSelectedRowKeys = [];
                this.billLadingBotDatas.forEach((item) => {
                    this.billLadingBotSelectedRowKeys.push(item.detail_inner_sn);
                });
            } else {
                this.billLadingBotSelectedRowKeys = [];
            }
        },

        selectChangeEvent_sheel({ checked, row }) {
            if (checked) {
                this.billLadingBotSelectedRowKeys.push(row.detail_inner_sn);
            } else {
                this.billLadingBotSelectedRowKeys.forEach((item, index) => {
                    if (item === row.detail_inner_sn) {
                        this.billLadingBotSelectedRowKeys.splice(index, 1);
                    }
                });
            }
        },
        DblClick_sheel({ row }) {
            if (typeof this.billLadingBotSelectedRowKeys.find((e) => e === row.detail_inner_sn) === 'undefined') {
                this.billLadingBotSelectedRowKeys.push(row.detail_inner_sn);
            }
            this.addSheelInfo();
        },
        async tableHeadFn() {
            this.addListAllDatas = [];
            this.addListChooseDatas = [];
            this.tableHeadModal.alert = true;
            this.tableHeadModal.loading = true;
            this.$Utils.globalDragCenterFn('modal-storage-add');
            const params = {
                org_id: this.addInfoModalForm.org_id,
                warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                grn_inner_sn: this.addInfoModalForm.grn_inner_sn,
                billsType: this.addInfoModalForm.billsType
            };
            // if (params.grn_type === undefined) return
            await stoGetInfoAPI({ params })
                .then((res) => {
                    this.tableHeadModal.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        const infos = list.map((item, index) => {
                            return {
                                ...item,
                                // location: [], // 存储位置 - 储位分配
                                // location_inner_sn: [], // 存储位置id - 储位分配
                                // location_msg: '', // 位置信息 - 储位分配
                                // real_quantity: 0, // 实放数量 - 储位分配
                                key: Math.random(),
                                index,
                                activeClass: '',
                                type: item.type + '',
                                produce_date: item.produce_date && moment(item.produce_date),
                                overdue_date: item.overdue_date && moment(item.overdue_date),
                                // item.name = item.product_name
                                // item.mdrfList = item.mdrf_sn ? [{value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn}] : []
                                unitList: item.unit_inner_sn
                                    ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                    : [],
                                unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                                isLoadingUnit: false, // 是否加载过整棵树
                                unit: item.unit_name
                            };
                        });
                        this.addListAllDatas = infos;
                        this.$refs.stoAddListTable.refreshColumn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.tableHeadModal.loading = false;
                });
        },
        stoAddListSave() {
            this.addListChooseDatas.forEach((item) => {
                item.action = 1;
                item.real_quantity = 0;
                item.location_inner_sn = [];
                item.location = '';
                this.storageAllocatDatas = [...this.storageAllocatDatas, item];
            });

            this.$refs.xStorageAllocatTable.refreshColumn();
            this.tableHeadModal.alert = false;
        },
        dragClick(index) {
            this.$refs.xTableAddInfo.setCurrentRow(this.addInfoDatas[index]);
        },
        dragStart() {
            this.storageAllocatDatasDrag = {};
        },
        dragEnd() {
            // const x = evt.originalEvent.screenX
            // const y = evt.originalEvent.screenY
            if (!this.storageAllocatDatasDrag.org_id) {
                this.storageAllocatDatasDrag = this.addInfoDatas[this.addInfoDatas.length - 1];
            }
            this.stoReOneAPIFn(this.storageAllocatDatasDrag);
        },
        dragGetdata(evt) {
            this.storageAllocatDatasDrag = evt.draggedContext.element;
        },
        async stoGetInfoAPIFn() {
            const params = {
                org_id: this.addInfoModalForm.org_id,
                warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                grn_inner_sn: this.addInfoModalForm.grn_inner_sn,
                billsType: this.addInfoModalForm.billsType
            };
            this.warehouseInfoListAPIFn(this.addInfoModalForm.warehouse_inner_sn);
            await stoGetInfoAPI({ params })
                .then((res) => {
                    this.storageAllocatLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.stoQuaryNum = [];
                        if (list.length === 0) {
                            this.stoModelStatus = 0;
                        } else {
                            this.stoModelStatus = list[0].status;
                        }
                        const infos = list.map((item, index) => {
                            return {
                                ...item,
                                // location: [], // 存储位置 - 储位分配
                                // location_inner_sn: [], // 存储位置id - 储位分配
                                // location_msg: '', // 位置信息 - 储位分配
                                // real_quantity: 0, // 实放数量 - 储位分配
                                key: Math.random(),
                                index,
                                activeClass: '',
                                type: item.type + '',
                                produce_date: item.produce_date && moment(item.produce_date),
                                overdue_date: item.overdue_date && moment(item.overdue_date),
                                // item.name = item.product_name
                                // item.mdrfList = item.mdrf_sn ? [{value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn}] : []
                                unitList: item.unit_inner_sn
                                    ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                    : [],
                                unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                                isLoadingUnit: false, // 是否加载过整棵树
                                location_msg: '', // 位置信息 - 储位分配
                                real_quantity: 0, // 实放数量 - 储位分配
                                unit: item.unit_name
                            };
                        });
                        this.storageAllocatDatas = infos;
                        this.stoQuaryNum = infos;
                        // this.getStoQuaryNum()
                        this.warehouseChildrenListAPIFn(
                            this.addInfoModalForm.warehouse_inner_sn,
                            this.locationList,
                            infos,
                            'preview'
                        );
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.storageAllocatLoading = false;
                });
        },
        cellClickFnSto({ row }) {
            this.currentTrObjSto = row;
        },
        cellDblclickFnSto({ row }) {
            this.currentTrObjSto = row;
            this.groupBtnFn('stoModify');
        },
        // 【删除】入库单-储位
        async stoDelAPIFn() {
            const curObj = this.$refs.xTableSto.getCurrentRow(),
                params = {
                    org_id: curObj.org_id || this.userBelong,
                    sas_inner_sn: curObj.sas_inner_sn,
                    warehouse_inner_sn: curObj.warehouse_inner_sn
                };
            await stoDelAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.stoListAPIFn();
                    this.editGrnTypeFlag = false;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 初始化数据【搜索按钮】
        initTablesFn() {
            switch (this.tabChoose) {
                case 1: {
                    this.godownEntryPagination.current = 1;
                    this.grnListAPIFn();
                    this.$refs.xTable.clearScroll();
                    break;
                }
                case 2: {
                    this.stoEntryPagination.current = 1;
                    this.stoListAPIFn();
                    this.$refs.xTableSto.clearScroll();
                    break;
                }
            }
        },
        // changeTabFn(key) {
        //     if (this.stoRefresh) this.stoListAPIFn();
        //     this.tabChoose = key;
        //     // this.$refs.xTableSto.setCurrentRow(this.stoEntryDatas[0])
        // },
        // 【搜索】入库单列表【储位分配表】
        async stoListAPIFn(grnInnerSn) {
            this.godownEntryPagination.loading = false;
            const typeNameFlag = typeof grnInnerSn === 'object'; // 详见新增
            this.stoEntryPagination.loading = true;
            let params = {
                org_id: this.userBelong,
                pageNum: this.stoEntryPagination.current,
                pageSize: this.stoEntryPagination.pageSize,
                isOnlyEffective: false
            };

            // 判断是否是高级搜索
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                if (this.searchTables.handler_name) {
                    params.operator_name = this.searchTables.handler_name;
                }

                if (this.searchTables.warehouse_model_inner_sn) {
                    params.warehouse_inner_sn = this.searchTables.warehouse_model_inner_sn;
                }

                if (this.searchTables.status) {
                    params.status = this.searchTables.status;
                }

                if (this.searchTables.begin_date) {
                    params.begin_date = this.searchTables.begin_date;
                }

                if (this.searchTables.end_date) {
                    params.end_date = this.searchTables.end_date;
                }
            }

            if (this.searchTables.grn_odo_sn) {
                params.sas_sn = this.searchTables.grn_odo_sn;
            }
            await stoListAPI({ params })
                .then((res) => {
                    this.stoRefresh = false;
                    this.stoEntryPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        let isAdd = false;
                        this.stoEntryDatas = rows.map((item, index) => {
                            let curItem = {
                                ...item,
                                index,
                                key: item.sas_inner_sn + '' + item.billsType,
                                activeClass: '',
                                status_text: this.stoStatus[parseFloat(item.status)]
                            };
                            if (typeNameFlag) {
                                if (grnInnerSn.curItem.key === curItem.key) {
                                    this.currentTrObjSto = curItem;
                                    isAdd = true;
                                }
                            } else {
                                if (grnInnerSn && curItem.key === grnInnerSn) {
                                    this.currentTrObjSto = curItem;
                                }
                            }
                            return curItem;
                        });
                        this.stoEntryPagination = {
                            ...this.stoEntryPagination,
                            total,
                            loading: false
                        };
                        if (typeNameFlag) {
                            if (!isAdd) {
                                this.stoEntryDatas.splice(this.stoEntryDatas.length - 1, 1, grnInnerSn.curItem);
                                this.currentTrObjSto = grnInnerSn.curItem;
                            }
                        } else {
                            if (!grnInnerSn) {
                                this.$refs.xTableSto.setCurrentRow(this.stoEntryDatas[0]);
                            }
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.stoEntryPagination.loading = false;
                });
        },
        // 搜索销售单列表(分页)
        async saleRecordSearchAPIFn() {
            this.billLadingDatas = [];
            const params = {
                pageNum: this.billLadingPagination.current,
                pageSize: this.billLadingPagination.pageSize,
                // 排序
                orderByColumn: 'sl_inner_sn',
                isAsc: 'desc'
            };
            let oid = this.sheelID;
            if (oid === '') return;
            params.org_id = this.userBelong;
            params.status = 2;
            await saleRecordSearchAPI({ params })
                .then((res) => {
                    this.billLadingPagination.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.billLadingDatas = rows.map((item) => {
                            item.key = item.sl_inner_sn + '' + item.seller_inner_sn;
                            item.activeClass = '';
                            return item;
                        });
                        if (this.billLadingDatas.length) {
                            this.currentTrBillLadingObj = this.billLadingDatas[0];
                            this.$refs.sheelTop.setCurrentRow(this.currentTrBillLadingObj);
                            this.$refs.sheelTop.updateData();
                            this.hospitalGrantDetailListAPIFn(this.currentTrBillLadingObj);
                        } else {
                            this.billLadingDatas = [];
                            this.currentTrBillLadingObj = {};
                        }
                        this.billLadingPagination = {
                            ...this.billLadingPagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.billLadingPagination.loading = false;
                });
        },
        changeUnitPriceFn(row, curPrice) {
            row.tax_unit_price_text = curPrice;
            row.money = parseFloat(row.quantity) ? (row.quantity * row.tax_unit_price).toFixed(4) : '';
            const xTableAddInfo = this.$refs.xTableAddInfo;
            xTableAddInfo.focus();
            xTableAddInfo.updateData();
        },
        updataTable(record) {
            /// 先将焦点重置
            this.resetFocusFn();

            if (record.action && record.action === -1) record.action = 2;

            // if (!this.c_IsGrn && actionTypeName === 'quantity') {
            //     const firstKey = `${record.product_type}${record.vendor_inner_sn}${record.product_inner_sn}${record.specification_inner_sn}`,
            //         lastKey = `${record.batch_number || ''}${record.overdue_date && moment(record.overdue_date).format(this.format)}${record.produce_date && moment(record.produce_date).format(this.format)}`,
            //         // fullKey = `${firstKey}||${lastKey}`,
            //         batchData = this.batchRepeatKeyObj[firstKey] ? this.batchRepeatKeyObj[firstKey].batch_data : [],
            //         minQuantity = record.quantity * record.min_unit_info,
            //         findBatchIndex = batchData.findIndex(f => `${f.batch_number || ''}${f.overdue_date}${f.produce_date}` === lastKey)
            //     if (this.batchRepeatKeyObj[firstKey].seq <= -1) return
            //     if (findBatchIndex > -1) {
            //         let batchLockedQuantityObj = batchData[findBatchIndex],
            //             inventoryBannce = batchLockedQuantityObj.inventoryBannce,
            //             batchLockedQuantity = batchLockedQuantityObj.batch_locked_quantity
            //         let timer = setTimeout(() => {
            //             clearTimeout(timer)
            //             timer = null
            //             if (!this.isEditBatchinventoryBannce && record.overdue_date && record.produce_date) {
            //                 if (minQuantity > inventoryBannce && inventoryBannce > 0) {
            //                     this.isEditBatchinventoryBannce = true // 防止修改数量频繁弹出该提示框
            //                     const modal = this.$warning({
            //                         title: '操作提示',
            //                         content: `【NO.${seq} —— ${record.batch_number || '空批号'}】已超出最大实际可用库存 ${inventoryBannce} !`,
            //                         okText: '知道了',
            //                         centered: true,
            //                         maskClosable: false,
            //                         onOk: (e) => {
            //                             modal.destroy()
            //                             this.isEditBatchinventoryBannce = false
            //                         }
            //                     })
            //                     record.quantity = batchLockedQuantity
            //                     return
            //                 }
            //                 if (!this.isEditBatchinventoryBannce && minQuantity > batchLockedQuantity) {
            //                     this.isEditBatchinventoryBannce = true // 防止修改数量频繁弹出该提示框
            //                     const modal = this.$warning({
            //                         title: '操作提示',
            //                         content: `【NO.${seq} —— ${record.batch_number || '空批号'}】已超出实际可用库存 ${batchLockedQuantity} !`,
            //                         okText: '知道了',
            //                         centered: true,
            //                         maskClosable: false,
            //                         onOk: (e) => {
            //                             modal.destroy()
            //                             this.isEditBatchinventoryBannce = false
            //                         }
            //                     })
            //                     record.quantity = batchLockedQuantity
            //                 }
            //             }
            //         }, 1000)
            //     }
            // }
            const xTableAddInfo = this.$refs.xTableAddInfo;
            xTableAddInfo.focus();
            // if (actionTypeName) {
            //     xTableAddInfo.resetFocusFn(actionTypeName, seq > 0 ? seq - 1 : 0)
            // }
            xTableAddInfo.updateData();
        },
        checkBatchinventoryBannceDataFn() {
            let isChcek = false,
                batchRangeCheckObj = {};
            for (let i = 0; i < this.addInfoDatas.length; i++) {
                let item = this.addInfoDatas[i],
                    minQuantity = item.quantity * item.min_unit_info,
                    batchNumber = item.batch_number || '',
                    overdueDate = item.overdue_date && moment(item.overdue_date).format(this.format),
                    produceDate = item.produce_date && moment(item.produce_date).format(this.format),
                    firstKey = `${item.product_type}${item.vendor_inner_sn}${item.product_inner_sn}${item.specification_inner_sn}`,
                    lastKey = `${batchNumber}${overdueDate}${produceDate}`,
                    key = `${firstKey}||${lastKey}`;

                let currentBatchDatas = {},
                    tempbatchRepeatKeyObj = this.batchRepeatKeyObj[firstKey];
                if (tempbatchRepeatKeyObj) {
                    // 注意：在编辑修改这张单子时，切换原来的仓库需要比较仓库id，如果一样忽略它
                    if (tempbatchRepeatKeyObj.batch_data.length <= 0) {
                        isChcek = true;
                        this.$warning({
                            title: '操作提示',
                            content: `仓库更换后请重新选择产品的批号。`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        break;
                    }
                    currentBatchDatas = tempbatchRepeatKeyObj.batch_data.find(
                        (f) =>
                            f.batch_number === batchNumber &&
                            f.overdue_date === overdueDate &&
                            f.produce_date === produceDate
                    );
                    if (!currentBatchDatas) {
                        isChcek = true;
                        this.$warning({
                            title: '操作提示',
                            content: `仓库更换后请重新选择产品的批号。`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        break;
                    }
                } else {
                    isChcek = true;
                    this.$warning({
                        title: '操作提示',
                        content: `仓库更换后请重新选择产品的批号。`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    break;
                }

                if (currentBatchDatas) {
                    let maxBatchinventoryBannce = currentBatchDatas.max_batch_locked_quantity;

                    if (!isChcek && minQuantity > currentBatchDatas.inventoryBannce) {
                        isChcek = true;
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} —— ${batchNumber || '空批号'}】已超出最大的实际可用库存 ${
                                currentBatchDatas.inventoryBannce
                            } !`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        break;
                    }

                    if (batchRangeCheckObj[key]) {
                        batchRangeCheckObj[key].seq.push(`NO.${i + 1}`);
                        batchRangeCheckObj[key].itemMinQuantity += minQuantity;

                        if (!isChcek && minQuantity > maxBatchinventoryBannce) {
                            isChcek = true;
                            this.$warning({
                                title: '操作提示',
                                content: `【NO.${i + 1} —— ${
                                    batchNumber || '空批号'
                                }】已超出当前批号的实际可用库存 ${maxBatchinventoryBannce} !`,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            break;
                        }

                        if (!isChcek && batchRangeCheckObj[key].itemMinQuantity > maxBatchinventoryBannce) {
                            isChcek = true;
                            this.$warning({
                                title: '操作提示',
                                content: `【${batchRangeCheckObj[key].seq.join('，')} —— ${
                                    batchNumber || '空批号'
                                }】已超出累加的实际可用库存 ${maxBatchinventoryBannce} !`,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            break;
                        }

                        if (!isChcek && batchRangeCheckObj[key].itemMinQuantity > currentBatchDatas.inventoryBannce) {
                            isChcek = true;
                            this.$warning({
                                title: '操作提示',
                                content: `【${batchRangeCheckObj[key].seq.join('，')} —— ${
                                    batchNumber || '空批号'
                                }】已超出最大的实际可用库存 ${currentBatchDatas.inventoryBannce} !`,
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            break;
                        }
                    } else {
                        batchRangeCheckObj[key] = {
                            seq: [`NO.${i + 1}`],
                            batch_number: item.batch_number,
                            itemMinQuantity: minQuantity
                        };
                    }
                }

                if (isChcek) break;
            }

            return isChcek;
        },
        // 【获取】根据发放单ID获取发放单明细列表
        async hospitalGrantDetailListAPIFn(record, mode) {
            if (record.department_inner_sn) {
                this.addInfoModalForm.department_inner_sn = record.department_inner_sn;
                this.addInfoModalForm.department_name = record.department_name;
            }
            this.billLadingBotDatas = [];
            this.billLadingBotDatasAll = [];
            if (mode === undefined) {
                this.sheelListData = [];
            }
            if (this.sheelID === 'GRANT') {
                this.billLadingBotLoading = true;
                await hospitalGrantDetailListAPI(record.hospital_inner_sn, record.gv_inner_sn)
                    .then((res) => {
                        this.billLadingBotDatas = [];
                        this.billLadingBotDatasAll = [];
                        this.billLadingBotLoading = false;
                        if (parseFloat(res.code) === 0) {
                            const { list } = res;
                            this.billLadingBotDatasAll = list.map((item) => {
                                return {
                                    ...item,
                                    activeClass: '',
                                    grant_quantity: item.grant_quantity, // 发放单不用写，销售单不知道
                                    // quantity: item.grant_quantity,
                                    key: item.detail_inner_sn,
                                    status_name: this.sheelStatus[item.status],
                                    source_type: 'copy' // 定义一个参数，凡是添加产品过去的信息，删除后都不留痕迹
                                };
                            });
                            if (this.sheelType) {
                                this.billLadingBotDatasAll.forEach((item) => {
                                    if (item.status === 0) {
                                        this.billLadingBotDatas.push(item);
                                    }
                                });
                            } else {
                                this.billLadingBotDatas = this.billLadingBotDatasAll;
                            }

                            if (mode !== undefined) {
                                // 当这里为点击点开的时候 可以添加删除过的信息
                                this.billLadingBotDatasAll.forEach((item) => {
                                    let isf = false;
                                    this.delInfoDatas.forEach((itemS) => {
                                        if (
                                            item.product_type === itemS.product_type &&
                                            item.product_inner_sn === itemS.product_inner_sn &&
                                            item.vendor_inner_sn === itemS.vendor_inner_sn &&
                                            item.specification === itemS.specification
                                        ) {
                                            isf = true;
                                        }
                                    });
                                    if (isf) item.status = 0;
                                });
                                this.billLadingBotDatas = [];
                                this.billLadingBotDatasAll.forEach((item) => {
                                    if (item.status === 0) {
                                        this.billLadingBotDatas.push(item);
                                    }
                                });
                            }
                            if (this.billLadingBotDatas.length) {
                                this.currentTrBillLadingBotObj = this.billLadingBotDatas[0];
                            } else {
                                this.currentTrBillLadingBotObj = {};
                            }
                            if (this.$refs.sheelTableAdd) {
                                this.$refs.sheelTableAdd.refreshColumn();
                            }
                        } else {
                            this.billLadingBotLoading = false;
                            this.$message.error(res.msg);
                        }
                    })
                    .catch(() => {
                        this.billLadingBotLoading = false;
                    });
            } else {
                if (mode === undefined) {
                    this.saleRecordDetailAPIFn(record);
                } else {
                    this.saleRecordDetailAPIFn(record, true);
                }
            }
        },
        // 【获取】根据发放单ID获取发放单明细列表  下边表格 销售单
        async saleRecordDetailAPIFn(record, mode) {
            if (record.billsType) this.odrder_type_m = record.billsType;
            if (record.org_id) this.org_id_m = record.org_id;
            this.sl_inner_sn_m = record.sl_inner_sn;
            this.billLadingBotDatas = [];
            this.billLadingBotDatasAll = [];
            this.sl_inner_sn_m = record.sl_inner_sn;
            let params = {
                billsType: this.odrder_type_m,
                org_id: this.org_id_m,
                pageNum: 1,
                pageSize: 100,
                sl_inner_sn: record.sl_inner_sn
            };
            await saleRecordDetailAPI({ params })
                .then((res) => {
                    this.billLadingBotDatas = [];
                    this.billLadingBotDatasAll = [];
                    this.billLadingBotLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.billLadingBotDatasAll = list.map((item) => {
                            return {
                                ...item,
                                activeClass: '',
                                grant_quantity: item.quantity, // 这个是为了后边和发放单通用数据
                                key: item.detail_inner_sn,
                                vendor_inner_sn: item.product_vendor_inner_sn,
                                vendor_name: item.product_vendor_name
                            };
                        });

                        if (this.sheelType) {
                            this.billLadingBotDatasAll.forEach((item) => {
                                if (item.status === 0) {
                                    this.billLadingBotDatas.push(item);
                                }
                            });
                        } else {
                            this.billLadingBotDatas = this.billLadingBotDatasAll;
                        }
                        if (mode !== undefined) {
                            // 当这里为点击点开的时候 可以添加删除过的信息
                            this.delInfoDatas.forEach((itemS) => {
                                let isf = true;
                                this.billLadingBotDatas.forEach((item) => {
                                    if (
                                        item.product_type === itemS.product_type &&
                                        item.product_inner_sn === itemS.product_inner_sn &&
                                        item.vendor_inner_sn === itemS.vendor_inner_sn &&
                                        item.specification === itemS.specification
                                    ) {
                                        isf = false;
                                    }
                                });
                                if (isf) {
                                    itemS.key = this.billLadingBotDatas.length + 1;
                                    itemS.product_vendor_inner_sn = itemS.vendor_inner_sn;
                                    itemS.product_vendor_name = itemS.vendor_name;
                                    itemS.produce_date = '';
                                    itemS.overdue_date = '';
                                    this.billLadingBotDatas.push(itemS);
                                }
                            });
                        }
                        if (this.billLadingBotDatas.length) {
                            this.currentTrBillLadingBotObj = this.billLadingBotDatas[0];
                        } else {
                            this.currentTrBillLadingBotObj = {};
                        }
                        if (this.$refs.sheelTable) this.$refs.sheelTable.updateData();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.billLadingBotLoading = false;
                });
        },
        // 【新增】储位分配单
        async sasAddAPIFn() {
            const params = {
                    org_id: this.addInfoModalForm.org_id,
                    warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                    warehouse_name: this.addInfoModalForm.warehouse_name,
                    operator_inner_sn: Array.isArray(this.addInfoModalForm.warehouse_operator_inner_sn)
                        ? this.addInfoModalForm.warehouse_operator_inner_sn[
                              this.addInfoModalForm.warehouse_operator_inner_sn.length - 1
                          ]
                        : this.addInfoModalForm.warehouse_operator_inner_sn,
                    operator_name: this.addInfoModalForm.warehouse_operator_name,
                    description: this.addInfoModalForm.description
                },
                details = this.$refs.xStorageAllocatTable.getTableData().tableData;

            params.details = details.map((item, index) => {
                return {
                    detail_inner_sn: item.detail_inner_sn || '',
                    product_vendor_inner_sn: item.product_vendor_inner_sn
                        ? item.product_vendor_inner_sn
                        : item.vendor_inner_sn,
                    product_vendor_name: item.product_vendor_name ? item.product_vendor_name : item.vendor_name,
                    product_inner_sn: item.product_inner_sn,
                    product_name: item.product_name,
                    specification_inner_sn: item.specification_inner_sn,
                    specification: item.specification,
                    unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                    unit: item.unit,
                    inventory_inner_sn: item.inventory_inner_sn,
                    location_inner_sn: item.location_inner_sn[item.location_inner_sn.length - 1],
                    location: item.location_name,
                    expect_quantity: item.quantity,
                    sort_number: index,
                    batch_number: item.batch_number,
                    description: item.description || '',
                    product_type: item.product_type
                };
            });

            await sasAddAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.stoRefresh = true; // 新增完，进入到储位分配单tab刷新
                        if (!this.modalmask) {
                            this.resetStorageAllocatModalInfoModalFn();
                        } else {
                            this.resetStorageAllocatModalInfoModalFn();
                            this.resetAddInfoModalFn();
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.storageAllocatModal.saveLoading = false;
                })
                .catch((err) => {
                    const response = err.response;
                    if (response && response.data) {
                        this.beyondInventoryModalFn(response.data.message);
                    }
                    this.storageAllocatModal.saveLoading = false;
                });
        },
        // 【编辑】储位分配单
        async sasEditAPIFn() {
            const params = {
                org_id: this.addInfoModalForm.org_id,
                warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                sas_inner_sn: this.addInfoModalForm.sas_inner_sn,
                description: this.addInfoModalForm.description
            };
            let det = [];
            this.storageAllocatOldDatas.forEach((item, index) => {
                if (item.action === 0) {
                    const obj = {
                        detail_inner_sn: item.detail_inner_sn || '',
                        product_vendor_inner_sn: item.product_vendor_inner_sn,
                        product_vendor_name: item.product_vendor_name,
                        product_inner_sn: item.product_inner_sn,
                        product_name: item.product_name,
                        specification_inner_sn: item.specification_inner_sn,
                        specification: item.specification,
                        unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                        unit_name: item.unit,
                        inventory_inner_sn: item.inventory_inner_sn,
                        location_inner_sn: item.location_inner_sn[item.location_inner_sn.length - 1],
                        location: item.location,
                        quantity: item.quantity,
                        sort_number: index,
                        batch_number: item.batch_number,
                        product_type: item.product_type,
                        action: item.action
                    };
                    det = [...det, obj];
                }
            });
            this.storageAllocatDatas.forEach((item, index) => {
                if (item.action === 1) {
                    const obj = {
                        detail_inner_sn: item.detail_inner_sn || '',
                        product_vendor_inner_sn: item.product_vendor_inner_sn,
                        product_vendor_name: item.product_vendor_name,
                        product_inner_sn: item.product_inner_sn,
                        product_name: item.product_name,
                        specification_inner_sn: item.specification_inner_sn,
                        specification: item.specification,
                        unit_inner_sn:
                            typeof item.unit_inner_sn === 'object'
                                ? item.unit_inner_sn[item.unit_inner_sn.length - 1]
                                : item.unit_inner_sn,
                        unit_name: item.unit,
                        inventory_inner_sn: item.inventory_inner_sn,
                        location_inner_sn: item.location_inner_sn[item.location_inner_sn.length - 1],
                        location: item.location,
                        quantity: item.quantity,
                        sort_number: index,
                        batch_number: item.batch_number,
                        product_type: item.product_type,
                        action: item.action
                    };
                    det = [...det, obj];
                    return;
                }
                let act = null;
                this.storageAllocatOldDatas.forEach((itemOld) => {
                    if (item.detail_inner_sn === itemOld.detail_inner_sn) {
                        let checkRes = this.checkEditObjFn(itemOld, item);

                        if (!checkRes.editInfoFlag) {
                            act = 2; // 有变化，判断为修改
                        }
                    }
                });
                if (act !== null) {
                    const obj = {
                        detail_inner_sn: item.detail_inner_sn || '',
                        product_vendor_inner_sn: item.product_vendor_inner_sn,
                        product_vendor_name: item.product_vendor_name,
                        product_inner_sn: item.product_inner_sn,
                        product_name: item.product_name,
                        specification_inner_sn: item.specification_inner_sn,
                        specification: item.specification,
                        unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                        // unit_inner_sn: item.unit_inner_sn,
                        unit_name: item.unit,
                        inventory_inner_sn: item.inventory_inner_sn,
                        location_inner_sn: item.location_inner_sn[item.location_inner_sn.length - 1],
                        // location_inner_sn: item.location_inner_sn,
                        location: item.location,
                        quantity: item.quantity,
                        sort_number: index,
                        batch_number: item.batch_number,
                        product_type: item.product_type,
                        action: act
                    };
                    det = [...det, obj];
                }
            });
            params.details = det;
            await sasEditAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        if (!this.modalmask) {
                            this.resetStorageAllocatModalInfoModalFn();
                        } else {
                            this.resetStorageAllocatModalInfoModalFn();
                            this.resetAddInfoModalFn();
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.storageAllocatModal.saveLoading = false;
                })
                .catch((err) => {
                    const response = err.response;
                    if (response && response.data) {
                        this.beyondInventoryModalFn(response.data.message);
                    }
                    this.storageAllocatModal.saveLoading = false;
                });
        },
        checkEditObjFnAdd(array, array2) {
            var arr3 = [];
            array.forEach((item) => {
                let count = 0;
                array2.forEach((item2) => {
                    if (item === item2) {
                        count++;
                    }
                });
                if (count === 0) {
                    // 表示数组1的这个值没有重复的，放到arr3列表中
                    arr3.push(item);
                }
            });
            return arr3;
        },
        checkEditObjFn(oldForm, editForm) {
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
                                    inventory_inner_sn: oldForm.inventory_inner_sn
                                    // [server + '_old']: oldItem // 旧值
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
        // 获取【仓库内产品批号列表】
        async odoBatchListAPIFn(record) {
            this.batchDatas = [];
            record.batchDatas = [];
            this.godownEntryPagination.loading = true;
            let params = {
                org_id: record.org_id || this.userBelong,
                warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                product_vendor_inner_sn: record.vendor_inner_sn || record.dealer_inner_sn || record.vd_inner_sn,
                product_inner_sn: record.product_inner_sn,
                specification_inner_sn: record.specification_inner_sn,
                product_type: record.product_type,
                billsType: this.addInfoModalForm.billsType
            };
            if (this.sheelID === 'SALE_RECORD') {
                params.product_vendor_inner_sn = record.product_vendor_inner_sn;
            }

            if (this.isSheel) {
                // if (record.batch_number_old) {
                //     params.batch_number = record.batch_number_old
                // }

                params.voucher_detail_inner_sn_c = record.voucher_detail_inner_sn_c;
                params.voucher_inner_sn_c = record.voucher_inner_sn_c;
                params.voucher_type_c = record.voucher_type_c;
            }

            // if (this.btnName === 'add') {
            //     params.batch_number = ''
            // }

            if (this.btnName === 'edit') {
                params.batchNumberList = {}; // {'批号': '数量'}
                const firstKey = `${record.product_type}${record.vendor_inner_sn}${record.product_inner_sn}${record.specification_inner_sn}`;
                this.addInfoTempDatas.forEach((f) => {
                    if (
                        firstKey ===
                        `${f.product_type}${f.vendor_inner_sn}${f.product_inner_sn}${f.specification_inner_sn}`
                    ) {
                        if (typeof f.min_unit_info === 'undefined') {
                            f.min_unit_info = 1;
                            f.min_measure = 1; // 默认值
                        }
                        if (params.batchNumberList[f.batch_number]) {
                            params.batchNumberList[f.batch_number] += f.quantity * f.min_unit_info;
                        } else {
                            params.batchNumberList[f.batch_number] = f.quantity * f.min_unit_info;
                        }
                    }
                });
            }

            if (this.orderTypeData.value === grnAndOdoOrderType['PURCHASERETURN'].value) {
                params.isReturn = true;
            }
            await odoBatchListAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let { list, inventoryBannce } = res;
                        list = list.map((f) => {
                            return {
                                ...f,
                                inventoryBannce,
                                inventoryBannceDefault: inventoryBannce,
                                quantity: Math.max(0, f.quantity)
                            };
                        });
                        record.inventoryBannce = inventoryBannce;
                        if (this.orderTypeData.value === grnAndOdoOrderType['PURCHASERETURN'].value) {
                            this.batchDatas = list.map((m) => {
                                return {
                                    ...m,
                                    batch_locked_quantity: m.quantity
                                };
                            });
                        } else {
                            this.batchNumberSetVal(record, 'add', list);
                        }
                        this.$Utils.globalDragCenterFn('batch-modal');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    if (Object.keys(this.record).length > 0 && this.record.asynchronous) {
                        this.addInfoLoading = false;
                        /// 2. 异步请求标记，等待包装单位加载清除
                        this.record.asynchronous = false;
                    }
                    this.godownEntryPagination.loading = false;
                });
        },
        // 【经销商 产品单位】 根据id查询所有上级产品单位
        async dealerUnitParentsAPIFn(record) {
            await dealerUnitParentsAPI(
                record.dealer_inner_sn || record.vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn && record.unit_inner_sn.length
                    ? record.unit_inner_sn[record.unit_inner_sn.length - 1]
                    : ''
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let { info } = res;
                    record.unit_inner_sn = [];
                    record.isLoadingUnit = true;
                    let treeUnitList = (item) => {
                        record.unit_inner_sn.unshift(item.unit_inner_sn);
                        if (item.parent) {
                            treeUnitList(item.parent);
                        }
                    };
                    treeUnitList(info);
                }
            });
        },
        // 【经销商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async dealerUnitTreeAPIFn(record, btnName) {
            await dealerUnitTreeAPI(
                record.dealer_inner_sn || record.vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let circle = (item, prevMeasure, parentName) => {
                        // 递归遍历
                        item.forEach((item) => {
                            item.label = `${item.name}(${item.measure}×${parentName})`;
                            item.value = item.unit_inner_sn;
                            item.min_measure = item.measure * prevMeasure;
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item.min_measure, item.name);
                            } else {
                                item.children = null;
                            }
                        });
                    };
                    res.tree.forEach((item) => {
                        item.label = item.name;
                        item.value = item.unit_inner_sn;
                        item.min_measure = item.measure * 1;
                        if (item.children && item.children.length > 0) {
                            circle(item.children, item.min_measure, item.name);
                        } else {
                            item.children = null;
                        }
                    });
                    if (res.tree.length) {
                        record.unitList = res.tree;
                        if (btnName === 'add') {
                            // 赋默认值
                            const curUnit = record.unitList[0];
                            record.unit_inner_sn = [curUnit.value];
                            record.unit_name = curUnit.label;
                            record.min_measure = curUnit.min_measure;
                            record.isLoadingUnit = true;
                            this.$refs.xTableAddInfo.reloadRow(record, record, 'unit');
                            if (typeof record.min_unit_info === 'undefined') {
                                record.min_unit_info = 1; // 默认值
                            }
                            this.loadUnitMinFn(record, record.seq);
                        }
                        record.detail_inner_sn && !record.isLoadingUnit && this.dealerUnitParentsAPIFn(record);
                    }
                }
            });
        },
        // 【厂商 产品单位】 根据id查询所有上级产品单位
        async platformUnitParentsFn(record) {
            await platformUnitParents(
                record.vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn && record.unit_inner_sn.length
                    ? record.unit_inner_sn[record.unit_inner_sn.length - 1]
                    : ''
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let { info } = res;
                    record.unit_inner_sn = [];
                    record.isLoadingUnit = true;
                    let treeUnitList = (item) => {
                        record.unit_inner_sn.unshift(item.unit_inner_sn);
                        if (item.parent) {
                            treeUnitList(item.parent);
                        }
                    };
                    treeUnitList(info);
                }
            });
        },
        // 【厂商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async platformUnitTreeFn(record, btnName) {
            await platformUnitTree(record.vendor_inner_sn, record.product_inner_sn, record.specification_inner_sn).then(
                (res) => {
                    if (parseFloat(res.code) === 0) {
                        let circle = (item, prevMeasure, parentName) => {
                            // 递归遍历
                            item.forEach((item) => {
                                item.label = `${item.name}(${item.measure}×${parentName})`;
                                item.value = item.unit_inner_sn;
                                item.min_measure = item.measure * prevMeasure;
                                if (item.children && item.children.length > 0) {
                                    circle(item.children, item.min_measure, item.name);
                                } else {
                                    item.children = null;
                                }
                            });
                        };
                        res.tree.forEach((item) => {
                            item.label = item.name;
                            item.value = item.unit_inner_sn;
                            item.min_measure = item.measure * 1;
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item.min_measure, item.name);
                            } else {
                                item.children = null;
                            }
                        });
                        if (res.tree.length) {
                            record.unitList = res.tree;
                            if (btnName === 'add') {
                                // 赋默认值
                                const curUnit = record.unitList[0];
                                record.unit_inner_sn = [curUnit.value];
                                record.unit_name = curUnit.label;
                                record.min_measure = curUnit.min_measure;
                                record.isLoadingUnit = true;
                                this.$refs.xTableAddInfo.reloadRow(record, record, 'unit');
                                if (typeof record.min_unit_info === 'undefined') {
                                    record.min_unit_info = 1; // 默认值
                                }
                                this.loadUnitMinFn(record, record.seq);
                            }
                            record.detail_inner_sn && !record.isLoadingUnit && this.platformUnitParentsFn(record);
                        }
                    }
                }
            );
        },
        async warehouseInfoListAPIFn(warehouseInnerSn) {
            await warehouseInfoAPI(warehouseInnerSn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    // 递归遍历
                    let circle = (datas) => {
                        datas.forEach((item) => {
                            // 为Cascader赋值
                            item.label = item.name;
                            item.value = item.warehouse_model_inner_sn;
                            item.disabled = item.status === 0;
                            // 剔除失效项
                            if (item.disabled) {
                                datas = datas.filter(
                                    (element) => element.warehouse_inner_sn !== item.warehouse_inner_sn
                                );
                                // 递归有效项
                            } else {
                                if (item.children && item.children.length > 0) {
                                    item.children = circle(item.children);
                                } else {
                                    item.children = null;
                                }
                            }
                        });
                        return datas;
                    };

                    this.locationList = circle(info.children);
                }
            });
        },
        async warehouseInfoAPIFn(warehouseInnerSn, index) {
            await warehouseInfoAPI(warehouseInnerSn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    let showValue = [];
                    let showLabel = '';
                    let circle = (datas) => {
                        // 递归遍历
                        showValue.unshift(datas.warehouse_model_inner_sn);
                        if (datas.parents !== null) {
                            showLabel = datas.name + ' ' + showLabel;
                            circle(datas.parents);
                        }
                    };
                    circle(info);
                    showValue.splice(0, 1);
                    if (this.storageAllocatDatas[index]) {
                        // this.storageAllocatDatas[index].location = showLabel
                        this.storageAllocatDatas[index].location_name = this.storageAllocatDatas[index].location;
                        this.storageAllocatDatas[index].location_msg = showLabel;
                        this.storageAllocatDatas[index].location_inner_sn = showValue;
                        this.$refs.xStorageAllocatTable.updateData();
                    }
                }
            });
            // this.$refs.xStorageAllocatTable.updateData()
        },
        warehouseInfoOne() {
            this.storageAllocatDatas.forEach((item, index) => {
                item.location_inner_sn &&
                    item.location_inner_sn.length > 0 &&
                    this.warehouseInfoAPIFn(item.location_inner_sn[item.location_inner_sn.length - 1], index);
            });
        },
        // 根据机构ID和仓库ID【查询所有下级仓库】
        async warehouseChildrenListAPIFn(warehouseInnerSn, record, infos, btnName) {
            // btnName - 加载标识
            await warehouseChildrenListAPI(warehouseInnerSn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    record.loading = false;
                    const { list } = res,
                        listNew = list.map((item) => {
                            item.value = item.warehouse_model_inner_sn;
                            item.label = item.name;
                            item.disabled = parseInt(item.status) === 0;
                            item.isLeaf = parseInt(item.is_leaf) === 1; // is_leaf 1：没有下一级叶子节点   0：有
                            return item;
                        });
                    switch (btnName) {
                        case 'preview': {
                            record.push(...listNew);
                            break;
                        }
                        case 'insert': {
                            if (listNew.length > 0) {
                                if (!record.children) {
                                    record.children = [];
                                }
                                record.children.push(...listNew);
                            } else {
                                delete record.children;
                                delete record.isLeaf;
                            }
                            break;
                        }
                    }
                    this.locationList = [...this.locationList];
                    if (btnName === 'preview') {
                        const xStorageAllocatTable = this.$refs.xStorageAllocatTable;
                        this.storageAllocatDatas = infos.map((item) => {
                            return {
                                ...item,
                                location: [...record],
                                location_inner_sn: [],
                                location_msg: '',
                                location_name: ''
                            };
                        });
                        xStorageAllocatTable.reloadData(this.storageAllocatDatas);
                        this.currentTrStorageAllocationObj = this.storageAllocatDatas[0];
                        this.$nextTick(() => {
                            xStorageAllocatTable.setCurrentRow(this.currentTrStorageAllocationObj);
                        });
                    }
                    if (btnName === 'previewsto') {
                        const xStorageAllocatTable = this.$refs.xStorageAllocatTable;
                        if (this.locationList.length === 0) {
                            this.locationList.push(...listNew);
                        }
                        this.storageAllocatDatas = infos.map((item) => {
                            return {
                                ...item
                                // location: [...record],
                                // location_name: ''
                            };
                        });
                        this.warehouseInfoOne();
                        xStorageAllocatTable.reloadData(this.storageAllocatDatas);
                        this.currentTrStorageAllocationObj = this.storageAllocatDatas[0];
                        this.$nextTick(() => {
                            xStorageAllocatTable.setCurrentRow(this.currentTrStorageAllocationObj);
                        });
                    }
                    // if (this.modalmask) this.$Utils.globalDragCenterFn('modal-storage-allocation-sheet')
                }
            });
        },
        //     let circle = (datas, fatherCode) => { // 递归遍历
        //         datas.forEach((ciritem) => {
        //             var bs = []
        //             if (fatherCode.length === 0) {
        //                 bs.push()
        //             } else {
        //                 bs = fatherCode
        //             }
        //             if (ciritem.value === item.unit_inner_sn) {
        //                 item.unit_inner_sn = fatherCode
        //                 item.unit_inner_sn.push(item.value)
        //             } else {
        //                 if (ciritem.children && ciritem.children.length > 0) {
        //                     bs.push(ciritem.value)
        //                     this.circle(ciritem.children, bs)
        //                 }
        //             }
        //         })
        //     }
        //     circle(this.listdata, [])
        // 获取下级仓库
        loadLocationDataFn(selectedOptions) {
            const targetOption = selectedOptions[selectedOptions.length - 1];
            targetOption.loading = true;
            this.warehouseChildrenListAPIFn(targetOption.value, targetOption, null, 'insert');
        },
        // 【查询所有可用的机构一级仓库】
        async warehouseUsableTopListAPIFn(actionTypeName) {
            if (actionTypeName === 'search') {
                this.wareHouseSearchData = this.warehouseList;
            } else {
                this.isWareHouseFilterLoading = false;
                this.wareHouseFilterData = this.warehouseList;
            }
            // if (this.wareHouseFilterData.length <= 0) { // 预加载
            //     this.addInfoModalForm.warehouse_inner_sn = ''
            //     this.addInfoModalForm.warehouse_name = ''
            // }
            // }
            // await warehouseUsableTopListAPI()
            //     .then(res => {
            //         if (parseFloat(res.code) === 0) {
            //             const {list} = res
            //             let newList = list.map((item, index) => {
            //                 const curItem = {
            //                     ...item,
            //                     title: item.name,
            //                     value: item.warehouse_model_inner_sn,
            //                     key: item.warehouse_model_inner_sn
            //                 }

            //                 // if (!actionTypeName) {
            //                 //     if (this.btnName === 'add' && index === 0) {
            //                 //         this.addInfoModalForm.warehouse_inner_sn = curItem.value
            //                 //         this.addInfoModalForm.warehouse_name = curItem.title
            //                 //     } else {
            //                 //         if (parseFloat(this.addInfoModalForm.warehouse_inner_sn) === parseFloat(curItem.value)) {
            //                 //             this.addInfoModalForm.warehouse_inner_sn = curItem.value
            //                 //             this.addInfoModalForm.warehouse_name = curItem.title
            //                 //         }
            //                 //     }
            //                 // }
            //                 return curItem
            //             })
            //             if (actionTypeName === 'search') {
            //                 this.wareHouseSearchData = newList
            //             } else {
            //                 this.isWareHouseFilterLoading = false
            //                 this.wareHouseFilterData = newList
            //                 // if (this.wareHouseFilterData.length <= 0) { // 预加载
            //                 //     this.addInfoModalForm.warehouse_inner_sn = ''
            //                 //     this.addInfoModalForm.warehouse_name = ''
            //                 // }
            //             }
            //         } else {
            //             this.$message.error(res.msg)
            //         }
            //     })
        },
        async getAllWarehouseList() {
            try {
                const { code, list = [] } = await warehouseUsableTopListAPI();
                if (code === 0) {
                    this.allWarehouseList = list;
                }
            } catch (error) {
                console.error(error);
            }
        },
        // 【获取厂商注册证号的下拉列表】
        async platformProductMdrfListAPIFn(record, seq) {
            await platformProductMdrfListAPI(record.vendor_inner_sn, record.product_inner_sn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    let circle = (item) => {
                        // 递归遍历
                        item.forEach((item) => {
                            item.label = item.register_sn || item.mdrf_sn || item.name;
                            item.value = item.mdrf_inner_sn;
                            if (item.children && item.children.length > 0) {
                                circle(item.children);
                            } else {
                                item.children = null;
                            }
                        });
                    };
                    list.forEach((item) => {
                        item.label = item.register_sn || item.mdrf_sn || item.name;
                        item.value = item.mdrf_inner_sn;
                        if (item.children && item.children.length > 0) {
                            circle(item.children);
                        } else {
                            item.children = null;
                        }
                    });

                    if (list.length) {
                        record.mdrfList = list;
                        if (!record.mdrf_inner_sn) {
                            record.mdrf_inner_sn = [];
                        }
                    }
                    record.productMdrfFlag = true;
                    this.recordMdrf = { ...record, seq: seq - 1 };
                    this.$refs.xTableAddInfo.updateData();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【查询机构所有人员】用于下列列表
        async orgEmpDropdownListAPIFn() {
            await orgEmpDropdownListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'allEmpDropdownList');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据机构ID查询经销商【所有部门】
        async orgDeptTopTreeAPIFn() {
            await orgDeptTopTreeAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.isLoadingDepartmentInnerSn = false;
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'superiorDepartments');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /* 入库单区域主要API开始 */
        // 【获取仓库人员】
        async grnWeListAPIFn() {
            if (!this.addInfoModalForm.warehouse_inner_sn) return;
            let params = {
                wid: this.addInfoModalForm.warehouse_inner_sn
            };
            await grnWeListAPI({
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;

                    this.formatWarehouseOperationListFn(info);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【编辑】入库单类型
        async grnTypeEditAPIFn() {
            this.grnTypeAddLoading = true;
            let status = parseFloat(this.grnTypeAddForm.status),
                params = {
                    org_id: this.currentGrnTypeListTrObj.org_id,
                    type_inner_sn: this.currentGrnTypeListTrObj.type_inner_sn,
                    name: this.grnTypeAddForm.name,
                    status
                };
            await grnTypeEditAPI(params)
                .then((res) => {
                    this.grnTypeAddLoading = false;
                    if (parseFloat(res.code) === 0) {
                        this.grnTypeAddDatas.forEach((item) => {
                            if (
                                parseFloat(item.type_inner_sn) ===
                                parseFloat(this.currentGrnTypeListTrObj.type_inner_sn)
                            ) {
                                item.name = this.grnTypeAddForm.name;
                                item.label = this.grnTypeAddForm.name;
                                item.title = this.grnTypeAddForm.name;
                                item.status = status;
                                item.status_text = status === 0 ? '无效' : '有效';
                            }
                        });
                        status === 0 && this.removeTypeIsAddFn(this.currentGrnTypeListTrObj.type_inner_sn);
                        this.grnTypeList = this.grnTypeAddDatas.filter((item) => parseFloat(item.status) === 1);
                        this.addGrnTypeListModalFn('cancel');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.grnTypeAddLoading = false;
                });
        },
        // 【删除】入库单类型
        async grnTypeDelAPIFn(curObj) {
            await grnTypeDelAPI(curObj.org_id, curObj.type_inner_sn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const xTableAddType = this.$refs.xTableAddType,
                        curObj = xTableAddType.getCurrentRow();
                    xTableAddType.remove(curObj);
                    xTableAddType.refreshData();
                    this.grnTypeAddDatas = xTableAddType.getTableData().tableData;
                    xTableAddType.setCurrentRow(this.grnTypeAddDatas[0]);
                    this.currentGrnTypeListTrObj = this.grnTypeAddDatas.length ? this.grnTypeAddDatas[0] : {};
                    this.removeTypeIsAddFn(this.currentGrnTypeListTrObj.type_inner_sn);
                    this.grnTypeList = this.grnTypeAddDatas.filter((item) => parseFloat(item.status) === 1);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【新增】入库单类型
        async grnTypeAddAPIFn() {
            this.grnTypeAddLoading = true;
            let params = {
                org_id: this.userBelong,
                name: this.grnTypeAddForm.name,
                status: this.grnTypeAddForm.status
            };
            await grnTypeAddAPI(params)
                .then((res) => {
                    this.grnTypeAddLoading = false;
                    this.addGrnTypeListModal.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { info } = res,
                            status = parseFloat(info.status),
                            xTableAddType = this.$refs.xTableAddType;
                        let curIndex = xTableAddType.getCurrentRow();
                        if (!curIndex) {
                            curIndex = 0;
                        }
                        xTableAddType
                            .insertAt(
                                {
                                    ...info,
                                    status,
                                    checked: status === 1,
                                    status_text: status === 1 ? '有效' : '无效',
                                    key: info.type_inner_sn + '',
                                    activeClass: '',
                                    value: info.type_inner_sn + '',
                                    label: info.name,
                                    title: info.title
                                },
                                curIndex
                            )
                            .then(({ row }) => {
                                this.grnTypeAddDatas = xTableAddType.getTableData().tableData;
                                xTableAddType.setCurrentRow(row);
                                this.grnTypeList = this.grnTypeAddDatas.filter((item) => parseFloat(item.status) === 1);
                            });
                        this.addGrnTypeListModalFn('cancel');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addGrnTypeListModal.loading = false;
                    this.grnTypeAddLoading = false;
                });
        },
        // 【查询入库单类型列表】
        async grnTypeListAPIFn(status, typeInnerSn) {
            // status: 0 - 无效   1 - 有效
            let params = {};
            if (typeof status === 'number') {
                params.status = status;
            }
            await grnTypeListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.isGrnTypeLoading = false;
                    const { list } = res;
                    this.grnTypeAddDatas = list.map((item) => {
                        const status = parseFloat(item.status),
                            cur = {
                                ...item,
                                status,
                                checked: status === 1,
                                status_text: status === 1 ? '有效' : '无效',
                                key: item.type_inner_sn + '',
                                activeClass: '',
                                value: item.type_inner_sn + '',
                                label: item.name,
                                title: item.title
                            };
                        if (parseFloat(typeInnerSn) === parseFloat(item.type_inner_sn)) {
                            this.currentGrnTypeListTrObj = cur;
                        }
                        return cur;
                    });

                    list.length && this.$Utils.globalDragCenterFn('modal-grntype');

                    this.grnTypeList = this.grnTypeAddDatas.filter((item) => parseFloat(item.status) === 1);
                    const xTableAddType = this.$refs.xTableAddType;
                    this.grnTypeAddDatas.length &&
                        xTableAddType &&
                        xTableAddType.setCurrentRow(this.grnTypeAddDatas[0]);

                    if (this.btnName === 'add') {
                        if (this.grnTypeList.length) {
                            this.addInfoModalForm.type_inner_sn = this.grnTypeList[0].value;
                            this.addInfoModalForm.type_name = this.grnTypeList[0].label;
                        } else {
                            this.addInfoModalForm.type_inner_sn = '';
                            this.addInfoModalForm.type_name = '';
                        }
                    }
                    if (
                        this.grnTypeList.findIndex(
                            (item) => parseInt(item.type_inner_sn) === parseInt(this.addInfoModalForm.type_inner_sn)
                        ) === -1
                    ) {
                        this.addInfoModalForm.type_inner_sn = '';
                        this.addInfoModalForm.type_name = '';
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 获取【入库单详情】
        async grnInfoAPIFn() {
            this.isLoadingDepartmentInnerSn = true;
            this.isGrnTypeLoading = true;
            this.delInfoDatas = [];
            const curObj = this.$refs.xTable.getCurrentRow();
            const params = {
                org_id: curObj.org_id || this.userBelong,
                grn_inner_sn: curObj.grn_inner_sn,
                billsType: curObj.billsType
            };
            await grnInfoAPI({ params })
                .then((res) => {
                    this.addInfoLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        // if (this.grnOdoOrderIsCopyFn(info.voucher_type_c)) {
                        //     return
                        // }
                        /// 复制当前单据 ===> 将该单据的主状态置为 [0 - '正式']
                        if (this.btnName === 'copy') {
                            info.status = 0;
                            if (info.voucher_type_c !== 'NONE' || !info.voucher_type_c) {
                                this.isSheel = true;
                                this.sheelID = info.voucher_type_c;
                            }
                            this.grnOdoOrderCopyFn();
                        }
                        this.barcodeParentObj = { ...info };
                        info.type_inner_sn = info.type_inner_sn + '';
                        this.making_people_name = info.making_people_name;
                        this.auditor_name = info.auditor_name;
                        this.addInfoModalForm = { ...info };
                        this.addInfoModalForm.organization_name_old = info.organization_name;
                        this.addInfoModalForm.organization_inner_sn_old = info.organization_inner_sn;
                        this.voucher_type_c = info.voucher_type_c;
                        this.voucher_inner_sn_c = info.voucher_inner_sn_c;
                        if (typeof info.type_inner_sn !== 'undefined') {
                            this.grnTypeList = [
                                {
                                    name: info.type_name,
                                    label: info.type_name,
                                    title: info.type_name,
                                    status: info.status,
                                    status_text: '无效',
                                    value: info.type_inner_sn
                                }
                            ];
                        }
                        this.warehouseOperatorList = [
                            {
                                title: info.warehouse_operator_name,
                                value: info.warehouse_operator_inner_sn,
                                key: info.warehouse_operator_inner_sn,
                                isLoading: false // 是否加载过
                            }
                        ];
                        if (typeof info.warehouse_inner_sn !== 'undefined') {
                            this.wareHouseFilterData = [
                                {
                                    name: info.warehouse_name,
                                    label: info.warehouse_name,
                                    title: info.warehouse_name,
                                    key: info.warehouse_inner_sn,
                                    value: info.warehouse_inner_sn
                                }
                            ];
                            this.isWareHouseFilterLoading = true;
                        }

                        this.superiorDepartments = [
                            {
                                title: info.department_name,
                                value: info.department_inner_sn,
                                key: info.department_inner_sn
                            }
                        ];
                        // this.warehouseUsableTopListAPIFn(this.addInfoModalForm.warehouse_inner_sn) // 仓库
                        // this.orgDeptTopTreeAPIFn() // 部门
                        // this.grnTypeListAPIFn() // 单据类别
                        this.orgEmpDropdownListAPIFn(); // 经手人
                        // this.grnWeListAPIFn()
                        for (let key in info) {
                            if (key !== 'details') {
                                this.editInfoFormObj[key + '_old'] = info[key];
                            }
                        }
                        let tempDatas = info.details.map((item, index) => {
                            item.location = []; // 存储位置 - 储位分配
                            item.location_inner_sn = []; // 存储位置id - 储位分配
                            item.location_msg = ''; // 位置信息 - 储位分配
                            item.real_quantity = 0; // 实放数量 - 储位分配
                            item.key = Math.random();
                            item.index = index;
                            item.focusDatas = [
                                false,
                                index === 0 && parseInt(info.status) <= 0,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false
                            ];
                            item.activeClass = '';
                            item.type = item.type + '';
                            item.produce_date = item.produce_date && moment(item.produce_date);
                            item.overdue_date = item.overdue_date && moment(item.overdue_date);
                            item.name = item.product_name;
                            item.mdrfList = item.mdrf_sn
                                ? [{ value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn }]
                                : [];
                            item.unitList = item.unit_inner_sn
                                ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                : [];
                            item.unit_inner_sn_old = item.unit_inner_sn ? [item.unit_inner_sn] : [];
                            item.unit_inner_sn = item.unit_inner_sn ? [item.unit_inner_sn] : [];
                            item.isLoadingUnit = false; // 是否加载过整棵树
                            item.isOld = true;
                            item.batch_number_old = item.batch_number;
                            item.quantity_old = item.quantity;
                            item.unit_inner_sn_old = item.unit_inner_sn;
                            item.unit_price_old = item.unit_price;
                            item.tax_unit_price = item.unit_price;
                            item.money = parseFloat(item.quantity)
                                ? (item.quantity * item.tax_unit_price).toFixed(4)
                                : '';
                            item.unitPriceList = [];
                            item.rate_old = Math.floor(item.rate * 100);
                            item.rate = Math.floor(item.rate * 100);
                            item.produce_date_old = item.produce_date;
                            item.overdue_date_old = item.overdue_date;
                            item.type_old = item.type;
                            item.mdrf_inner_sn_old = item.mdrf_inner_sn;
                            item.voucher_type_c = info.voucher_type_c;
                            item.voucher_inner_sn_c = info.voucher_inner_sn_c;
                            item.health_care_sn_old = item.health_care_sn;
                            return item;
                        });
                        if (info.voucher_type_c !== 'NONE') {
                            this.isSheel = true;
                            this.sheelID = info.voucher_type_c;
                        }
                        this.addInfoDatas = tempDatas;
                        const xTableAddInfo = this.$refs.xTableAddInfo;
                        xTableAddInfo.loadData(tempDatas);
                        this.handleResetMultipleFocus();
                        xTableAddInfo.focus();
                        xTableAddInfo.refreshScroll();
                        if (this.addInfoDatas && this.addInfoDatas.length) {
                            xTableAddInfo.setCurrentRow(this.addInfoDatas[0]);
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addInfoLoading = false;
                })
                .finally(() => {
                    this.isCopyLoading = false;
                });
        },
        // 【编辑】入库单
        async grnEditAPIFn(status) {
            const curObj = this.addData[this.editCellRow],
                params = {
                    grn_inner_sn: curObj.grn_inner_sn,
                    billsType: this.addInfoModalForm.billsType,
                    billsType_old: this.editInfoFormObj.billsType_old,
                    org_id: this.addInfoModalForm.org_id || this.userBelong,
                    warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                    warehouse_inner_sn_old: this.editInfoFormObj.warehouse_inner_sn_old,
                    warehouse_name: this.addInfoModalForm.warehouse_name,
                    warehouse_name_old: this.editInfoFormObj.warehouse_name_old,
                    department_inner_sn: this.addInfoModalForm.department_inner_sn,
                    department_inner_sn_old: this.editInfoFormObj.department_inner_sn_old,
                    department_name: this.addInfoModalForm.department_name,
                    department_name_old: this.editInfoFormObj.department_name_old,
                    description: this.addInfoModalForm.description,
                    description_old: this.editInfoFormObj.description_old,
                    grn_sn: '',
                    grn_sn_old: '',
                    handler_inner_sn: this.addInfoModalForm.handler_inner_sn,
                    handler_inner_sn_old: this.editInfoFormObj.handler_inner_sn_old,
                    handler_name: this.addInfoModalForm.handler_name,
                    handler_name_old: this.editInfoFormObj.handler_name_old,
                    organization_inner_sn: this.addInfoModalForm.organization_inner_sn,
                    organization_inner_sn_old: this.editInfoFormObj.organization_inner_sn_old,
                    organization_name: this.addInfoModalForm.organization_name,
                    organization_name_old: this.editInfoFormObj.organization_name_old,
                    type_inner_sn: this.addInfoModalForm.type_inner_sn,
                    type_inner_sn_old: this.editInfoFormObj.type_inner_sn_old,
                    type_name: this.addInfoModalForm.type_name,
                    type_name_old: this.editInfoFormObj.type_name_old,
                    warehouse_operator_inner_sn: this.addInfoModalForm.warehouse_operator_inner_sn,
                    warehouse_operator_inner_sn_old: this.editInfoFormObj.warehouse_operator_inner_sn_old,
                    warehouse_operator_name: this.addInfoModalForm.warehouse_operator_name,
                    warehouse_operator_name_old: this.editInfoFormObj.warehouse_operator_name_old,
                    status
                };
            let flag = true;
            // this.addInfoDatas = this.$refs.xTableAddInfo.getTableData().tableData
            let data = this.getStableData();
            data.forEach((item) => {
                let mdrfInnerSn = item.mdrf_inner_sn;
                if (Array.isArray(mdrfInnerSn)) {
                    item.mdrf_inner_sn = mdrfInnerSn.length > 0 ? mdrfInnerSn[mdrfInnerSn.length - 1] : '';
                }

                if (item.quantity === 0) {
                    flag = false;
                }
                item.unit_price_text = item.tax_unit_price;
                if (item.isOld) {
                    if (
                        item.batch_number !== item.batch_number_old ||
                        item.quantity_old !== item.quantity ||
                        item.unit_inner_sn_old !== item.unit_inner_sn ||
                        item.unit_price_old !== item.unit_price ||
                        item.rate_old !== item.rate ||
                        item.produce_date_old !== item.produce_date ||
                        item.overdue_date_old !== item.overdue_date ||
                        item.type_old !== item.type ||
                        item.mdrf_inner_sn_old !== item.mdrf_inner_sn ||
                        item.health_care_sn_old !== item.health_care_sn
                    ) {
                        item.action = 2; // 修改状态
                    }
                }
            });
            if (!flag) {
                this.$warning({
                    title: '提示',
                    content: '产品数量不能为0',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            let newArr = [];
            data.forEach((item, index) => {
                if (item.action) {
                    let obj = {
                        'detail_inner_sn': item.detail_inner_sn || '',
                        'vendor_inner_sn': item.vendor_inner_sn || item.dealer_inner_sn,
                        'vendor_name': item.vendor_name || item.dealer_name,
                        'product_inner_sn': item.product_inner_sn,
                        'product_name': item.name || item.product_name,
                        'specification_inner_sn': item.specification_inner_sn,
                        'specification': item.specification,
                        'unit_inner_sn':
                            item.unit_inner_sn && item.unit_inner_sn.length
                                ? item.unit_inner_sn[item.unit_inner_sn.length - 1]
                                : '',
                        'unit_inner_sn_old':
                            item.unit_inner_sn_old && item.unit_inner_sn_old.length
                                ? item.unit_inner_sn_old[item.unit_inner_sn_old.length - 1]
                                : '',
                        'unit_name': this.$Utils.UnitBracketsTrim(item.unit_name),
                        'mdrf_inner_sn': item.mdrf_sn ? item.mdrf_inner_sn : '',
                        'mdrf_sn': item.mdrf_sn || '',
                        'unit_price_text': item.tax_unit_price,
                        'rate': item.rate / 100,
                        'quantity': item.quantity,
                        'quantity_old': item.quantity_old,
                        'produce_date': item.produce_date && moment(item.produce_date).format(this.format),
                        'overdue_date': item.overdue_date && moment(item.overdue_date).format(this.format),
                        'type': item.type,
                        'sort_number': index,
                        'batch_number': item.batch_number.trim(),
                        'product_type': item.product_type,
                        'codes': item.codes || [],
                        'action': item.action,
                        'health_care_sn': item.health_care_sn,
                        'product_vendor_inner_sn':
                            item.vendor_inner_sn || item.dealer_inner_sn || item.product_vendor_inner_sn,
                        'product_vendor_name': item.vendor_name || item.dealer_name || item.product_vendor_name
                    };
                    if (typeof item.voucher_type_c !== 'undefined') {
                        obj.voucher_type_c = item.voucher_type_c;
                        obj.voucher_inner_sn_c = item.voucher_inner_sn_c;
                        obj.voucher_detail_inner_sn_c = item.voucher_detail_inner_sn_c;
                    }
                    if (!obj.product_inner_sn) return;
                    newArr.push(obj);
                }
            });
            params.details = newArr;
            params.details = params.details.concat(this.delInfoDatas);

            params.details.forEach((item) => {
                if (item.produce_date === '' || item.produce_date === 'Invalid date') {
                    delete item.produce_date;
                }
            });

            await grnEditAPI(params)
                .then((res) => {
                    this.addInfoModal.saveLoading = false;
                    if (parseFloat(res.code) === 0) {
                        // 如果单位名称改变了前端就在旧位置插入即可
                        this.editGrnTypeFlag = params.billsType !== params.billsType_old;
                        this.changeTabFn('history');
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.dblclickAddProductFn();
                })
                .catch(() => {
                    this.addInfoModal.saveLoading = false;
                });
        },
        // 【删除】入库单
        async grnDelAPIFn() {
            const curObj = this.$refs.xTable.getCurrentRow(),
                params = {
                    org_id: curObj.org_id || this.userBelong,
                    grn_inner_sn: curObj.grn_inner_sn,
                    billsType: curObj.billsType
                };
            await grnDelAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.grnListAPIFn();
                    this.editGrnTypeFlag = false;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【新增】入库单
        async grnAddAPIFn(status) {
            const _this = this;
            let warehouseName = '';
            if (!this.addInfoModalForm.warehouse_name && this.addInfoModalForm.warehouse_inner_sn) {
                warehouseName = this.wareHouseFilterData.find(
                    (e) => e.value === _this.addInfoModalForm.warehouse_inner_sn
                ).title;
            } else {
                warehouseName = this.addInfoModalForm.warehouse_name;
            }

            let params = {
                billsType: this.addInfoModalForm.billsType,
                handler_inner_sn: this.addInfoModalForm.handler_inner_sn,
                handler_name: this.addInfoModalForm.handler_name,
                org_id: this.userBelong,
                organization_inner_sn: this.addInfoModalForm.organization_inner_sn,
                organization_name: this.addInfoModalForm.organization_name,
                status,
                type_inner_sn: this.addInfoModalForm.type_inner_sn,
                type_name: this.addInfoModalForm.type_name,
                warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                warehouse_name: warehouseName
            };

            if (this.addInfoModalForm.department_inner_sn) {
                params.department_inner_sn = this.addInfoModalForm.department_inner_sn;
                params.department_name = this.addInfoModalForm.department_name;
            }

            if (this.$Utils.trim(this.addInfoModalForm.description)) {
                params.description = this.addInfoModalForm.description;
            }

            if (this.addInfoModalForm.warehouse_operator_inner_sn) {
                params.warehouse_operator_inner_sn = this.addInfoModalForm.warehouse_operator_inner_sn;
                params.warehouse_operator_name = this.addInfoModalForm.warehouse_operator_name;
            }
            let flag = true;
            params.details = [];
            this.addInfoDatas.forEach((item, index) => {
                if (item.product_inner_sn) {
                    const productType = parseFloat(item.product_type) === 1,
                        vendorInnerSn = productType ? item.vendor_inner_sn : item.dealer_inner_sn,
                        vendorName = productType ? item.vendor_name : item.dealer_name;
                    if (item.quantity === 0) {
                        flag = false;
                    }
                    // 注册证号
                    if (
                        typeof item.mdrf_inner_sn === 'undefined' ||
                        (item.mdrf_inner_sn && item.mdrf_inner_sn.length <= 0)
                    ) {
                        item.mdrf_inner_sn = '';
                        item.mdrf_sn = '';
                    }

                    // 从调单过来的
                    if (this.isSheel) {
                        params.details.push({
                            'detail_inner_sn': item.detail_inner_sn || '',
                            'product_vendor_inner_sn': vendorInnerSn || item.vendor_inner_sn,
                            'product_vendor_name': vendorName || item.vendor_name,
                            'product_inner_sn': item.product_inner_sn,
                            'product_name': item.name || item.product_name,
                            'specification_inner_sn': item.specification_inner_sn,
                            'specification': item.specification,
                            'unit_inner_sn': item.unit_inner_sn.length
                                ? item.unit_inner_sn[item.unit_inner_sn.length - 1]
                                : '',
                            'unit_name': item.unit_name,
                            'mdrf_inner_sn': Array.isArray(item.mdrf_inner_sn)
                                ? item.mdrf_inner_sn[item.mdrf_inner_sn.length - 1] || ''
                                : item.mdrf_inner_sn,
                            'mdrf_sn': item.mdrf_sn || '',
                            'unit_price_text': item.tax_unit_price,
                            'rate': item.rate / 100,
                            'quantity': item.quantity,
                            'quantity_old': item.quantity,
                            'produce_date': item.produce_date && moment(item.produce_date).format(this.format),
                            'overdue_date': item.overdue_date && moment(item.overdue_date).format(this.format),
                            'type': item.type,
                            'sort_number': index,
                            'batch_number': item.batch_number.trim(),
                            'product_type': item.product_type,
                            'codes': item.codes || [],
                            'action': 1,
                            'voucher_type_c': item.voucher_type_c || '',
                            'voucher_inner_sn_c': item.voucher_inner_sn_c || '',
                            'voucher_detail_inner_sn_c': item.voucher_detail_inner_sn_c || '',
                            'health_care_sn': item.health_care_sn
                        });
                    } else {
                        params.details.push({
                            'detail_inner_sn': item.detail_inner_sn || '',
                            'product_vendor_inner_sn': vendorInnerSn || item.vendor_inner_sn,
                            'product_vendor_name': vendorName || item.vendor_name,
                            'product_inner_sn': item.product_inner_sn,
                            'product_name': item.name || item.product_name,
                            'specification_inner_sn': item.specification_inner_sn,
                            'specification': item.specification,
                            'unit_inner_sn': item.unit_inner_sn.length
                                ? item.unit_inner_sn[item.unit_inner_sn.length - 1]
                                : '',
                            'unit_name': this.$Utils.UnitBracketsTrim(item.unit_name),
                            'mdrf_inner_sn': Array.isArray(item.mdrf_inner_sn)
                                ? item.mdrf_inner_sn[item.mdrf_inner_sn.length - 1] || ''
                                : item.mdrf_inner_sn,
                            'mdrf_sn': item.mdrf_sn || '',
                            'unit_price_text': item.tax_unit_price,
                            'rate': item.rate / 100,
                            'quantity': item.quantity,
                            'quantity_old': item.quantity,
                            'produce_date': item.produce_date && moment(item.produce_date).format(this.format),
                            'overdue_date': item.overdue_date && moment(item.overdue_date).format(this.format),
                            'type': item.type,
                            'sort_number': index,
                            'batch_number': item.batch_number.trim(),
                            'product_type': item.product_type,
                            'codes': item.codes || [],
                            'action': 1,
                            'health_care_sn': item.health_care_sn
                        });
                    }
                }
            });

            if (!flag) {
                this.$warning({
                    title: '提示',
                    content: '产品数量不能为0',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }

            params.details.forEach((item) => {
                if (item.produce_date === 'Invalid date') {
                    delete item.produce_date;
                }
            });

            await grnAddAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.godownEntryPagination.total++;
                        const { info } = res,
                            xTable = this.$refs.xTable,
                            record = xTable.getCurrentRow();
                        xTable
                            .insertAt(
                                {
                                    ...info,
                                    key: info.grn_inner_sn + '' + info.billsType,
                                    status_text: this.grnStatus[parseFloat(info.status)]
                                },
                                record
                            )
                            .then(({ row }) => {
                                this.godownEntryDatas = xTable.getTableData().tableData;
                                xTable.setCurrentRow(row);
                                this.currentTrObj = row;
                                if (this.godownEntryDatas.length > this.godownEntryPagination.pageSize) {
                                    this.godownEntryDatas.splice(this.godownEntryDatas.length - 1, 1);
                                }
                                this.closeAddInfoModalResetScrollFn();
                                this.resetAddInfoModalFn();
                            });
                        this.isSheel = false; // 调单标识重置
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.addInfoModal.saveLoading = false;
                    this.addInfoModal.auditLoading = false;
                    this.addInfoModal.godownEntryLoading = false;
                });
        },
        // 【获取储位分配单信息】
        async grnStorageAPIFn(oid, wid, beginInventory, endInventory) {
            this.modalmask = true; // 新增模态框有无黑色背景
            this.stoType = 0;
            this.storageAllocatLoading = true;
            await grnStorageAPI(oid, wid, beginInventory, endInventory)
                .then((res) => {
                    this.storageAllocatLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        const infos = info.map((item, index) => {
                            return {
                                ...item,
                                location: [], // 存储位置 - 储位分配
                                location_inner_sn: [], // 存储位置id - 储位分配
                                location_msg: '', // 位置信息 - 储位分配
                                real_quantity: 0, // 实放数量 - 储位分配
                                key: Math.random(),
                                index,
                                activeClass: '',
                                type: item.type + '',
                                produce_date: item.produce_date && moment(item.produce_date),
                                overdue_date: item.overdue_date && moment(item.overdue_date),
                                // item.name = item.product_name
                                // item.mdrfList = item.mdrf_sn ? [{value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn}] : []
                                unitList: item.unit_inner_sn
                                    ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                    : [],
                                unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                                isLoadingUnit: false // 是否加载过整棵树
                            };
                        });
                        this.storageAllocatOldDatas = infos;
                        this.locationList = [];
                        this.addInfoModalForm = {
                            ...this.currentTrObj,
                            warehouse_operator_inner_sn: '',
                            warehouse_operator_name: ''
                        };
                        this.warehouseChildrenListAPIFn(
                            this.currentTrObj.warehouse_inner_sn,
                            this.locationList,
                            infos,
                            'preview'
                        );
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.storageAllocatLoading = false;
                });
        },
        // 【获取储位分配单-修改信息】
        async stoStorageAPIFn(orgId, sasInnerSn, warehouseInnerSn) {
            this.modalmask = true;
            this.stoType = 1; // 进修改的查询，类型改为修改
            this.storageAllocatLoading = true;
            const params = {
                org_id: orgId,
                sas_inner_sn: sasInnerSn,
                warehouse_inner_sn: warehouseInnerSn
            };
            this.warehouseInfoListAPIFn(warehouseInnerSn);
            this.orgEmpDropdownListAPIFn(); // 经手人
            await stoInfoAPI({ params })
                .then((res) => {
                    this.storageAllocatLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        // this.storageAllocatModalOldForm = this.addInfoModalForm
                        this.stoQuaryNum = [];
                        const infos = info.details.map((item, index) => {
                            return {
                                ...item,
                                // location: [], // 存储位置 - 储位分配
                                location_inner_sn: [item.location_inner_sn], // 存储位置id - 储位分配
                                // location_msg: this.addInfoModalForm.warehouse_name + '\n' + item.location, // 位置信息 - 储位分配
                                // real_quantity: 0, // 实放数量 - 储位分配
                                key: Math.random(),
                                index,
                                activeClass: '',
                                type: item.type + '',
                                produce_date: item.produce_date && moment(item.produce_date),
                                overdue_date: item.overdue_date && moment(item.overdue_date),
                                // item.name = item.product_name
                                // item.mdrfList = item.mdrf_sn ? [{value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn}] : []
                                unitList: item.unit_inner_sn
                                    ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                    : [],
                                // unit_inner_sn: item.unit_inner_sn ? [item.unit_inner_sn] : [],
                                isLoadingUnit: false, // 是否加载过整棵树
                                unit: item.unit_name,
                                quantity: item.expect_quantity
                            };
                        });
                        this.stoModelStatus = info.status;
                        // this.storageAllocatOldDatas = infos
                        this.storageAllocatDatas = infos;
                        this.storageAllocatOldDatas = infos;
                        // this.locationList = []
                        this.addInfoModalForm = {
                            ...info,
                            grn_sn: info.sas_sn, // 补充，单据编号命名变化
                            warehouse_operator_inner_sn: info.operator_inner_sn
                        };
                        this.grnWeListAPIFn();
                        this.getStoQuaryNum();
                        this.making_people_name = info.making_people_name;
                        this.create_time = info.create_time;
                        this.warehouseChildrenListAPIFn(
                            info.warehouse_inner_sn,
                            this.locationList,
                            infos,
                            'previewsto'
                        );
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.storageAllocatLoading = false;
                });
        },
        // 【入库】
        async grnPutinAPIFn() {
            const curObj = this.$refs.xTable.getCurrentRow(),
                params = {
                    grn_inner_sn: curObj.grn_inner_sn,
                    billsType: curObj.billsType,
                    warehouse_inner_sn: curObj.warehouse_inner_sn,
                    warehouse_name: curObj.warehouse_name,
                    org_id: curObj.org_id || this.userBelong
                };
            this.currentTrObj = curObj;

            const PutInAPI =
                this.orderTypeData.value === grnAndOdoOrderType['PURCHASERETURN'].value
                    ? purchaseReturnPutinAPI
                    : grnPutinAPI;
            await PutInAPI(params)
                .then((res) => {
                    this.addInfoModal.godownEntryLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const _this = this,
                            { info } = res,
                            xTable = _this.$refs.xTable;
                        let infoMsg = info.msg;
                        // let infoMsg = 'HIS系统入库出错：\\n产品名称：定制式固定义齿，厂家：?厂商，规格：钴铬合金烤瓷桥未做对照，请核对！;'
                        if (infoMsg !== null && infoMsg.length > 0) {
                            this.beyondInventoryModalFn(infoMsg);
                        } else {
                            for (let i = 0; i < this.godownEntryDatas.length; i++) {
                                const item = this.godownEntryDatas[i];
                                if (curObj._XID === item._XID) {
                                    item.status = 2;
                                    item.status_text = this.grnStatus[parseFloat(item.status)];
                                    item.in_time = moment(Date()).format('YYYY-MM-DD HH:mm:ss');
                                    xTable.refreshData();
                                    xTable.loadData(this.godownEntryDatas);
                                    xTable.setCurrentRow(item);
                                    break;
                                }
                            }
                            _this.closeAddInfoModalResetScrollFn();
                            // _this.$confirm({
                            //     title: '储位分配单确认',
                            //     content: '产品已入库，是否需要生成“储位分配单”？',
                            //     centered: true,
                            //     maskClosable: false,
                            //     cancelText: '取消',
                            //     okText: '生成',
                            //     onOk () {
                            //         _this.storageAllocatModal.alert = true
                            //         _this.stoType = 0 // 储位分配单状态为新增
                            //         _this.stoModelStatus = 0 // 强制把状态转为新增
                            //         _this.$Utils.globalDragCenterFn('modal-storage-allocation-sheet')
                            //         _this.grnStorageAPIFn(_this.currentTrObj.org_id, _this.currentTrObj.warehouse_inner_sn, info.begin_inventory, info.end_inventory)
                            //         return new Promise((resolve, reject) => {
                            //             setTimeout(Math.random() > 0.5 ? resolve : reject, 800)
                            //         }).catch(() => {})
                            //     },
                            //     onCancel () {
                            //         _this.resetStorageAllocatModalInfoModalFn()
                            //         _this.resetAddInfoModalFn()
                            //     }
                            // })
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addInfoModal.godownEntryLoading = false;
                });
        },
        // 【搜索】入库单列表
        async grnListAPIFn(grnInnerSn) {
            const typeNameFlag = typeof grnInnerSn === 'object'; // 详见新增
            this.godownEntryPagination.loading = true;
            let params = {
                org_id: this.userBelong,
                pageNum: this.godownEntryPagination.current,
                pageSize: this.godownEntryPagination.pageSize,

                /// 排序
                orderByColumn: 'grn_sn',
                isAsc: 'desc'
            };

            // 判断是否是高级搜索
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                if (this.searchTables.handler_name) {
                    params.handler_name = this.searchTables.handler_name;
                }

                if (this.searchTables.warehouse_model_inner_sn) {
                    params.warehouse_model_inner_sn = this.searchTables.warehouse_model_inner_sn;
                }

                if (this.searchTables.status) {
                    params.status = this.searchTables.status;
                }

                if (this.searchTables.begin_date) {
                    params.begin_date = this.searchTables.begin_date;
                }

                if (this.searchTables.end_date) {
                    params.end_date = this.searchTables.end_date;
                }

                if (this.searchTables.key) {
                    params.key = this.searchTables.key;
                }

                if (this.searchTables.product_name) {
                    params.product_name = this.searchTables.product_name;
                }
            }

            if (this.searchTables.grn_odo_sn) {
                params.grn_sn = this.searchTables.grn_odo_sn;
            }
            await grnListAPI({ params })
                .then((res) => {
                    this.godownEntryPagination.loading = false;
                    const xTable = this.$refs.xTable;
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        let isAdd = false;
                        this.godownEntryDatas = rows.map((item, index) => {
                            let curItem = {
                                ...item,
                                index,
                                key: item.grn_inner_sn + '' + item.billsType,
                                activeClass: '',
                                status_text: this.grnStatus[parseFloat(item.status)]
                            };
                            if (typeNameFlag) {
                                if (grnInnerSn.curItem.key === curItem.key) {
                                    this.currentTrObj = curItem;
                                    isAdd = true;
                                }
                            } else {
                                if (grnInnerSn && curItem.key === grnInnerSn) {
                                    this.currentTrObj = curItem;
                                }
                            }
                            return curItem;
                        });
                        if (this.godownEntryDatas.length) {
                            this.$nextTick(() => {
                                xTable.loadData(this.godownEntryDatas);
                            });
                        }
                        if (typeNameFlag) {
                            if (!isAdd) {
                                this.godownEntryDatas.splice(this.godownEntryDatas.length - 1, 1, grnInnerSn.curItem);
                                this.currentTrObj = grnInnerSn.curItem;
                            }
                        } else {
                            if (!grnInnerSn) {
                                xTable.setCurrentRow(this.godownEntryDatas[0]);
                                this.currentTrObj = this.godownEntryDatas[0];
                            }
                        }
                        if (Object.keys(this.currentTrObj).length <= 0) {
                            this.currentTrObj = this.godownEntryDatas[0];
                        }
                        this.godownEntryPagination = {
                            ...this.godownEntryPagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.godownEntryPagination.loading = false;
                });
        },
        /* 入库单区域主要API开始 */

        /* 出库单区域主要API开始 */
        // 【获取仓库人员】
        async odoWeListAPIFn() {
            if (!this.addInfoModalForm.warehouse_inner_sn) return;

            let params = {
                wid: this.addInfoModalForm.warehouse_inner_sn
            };
            await odoWeListAPI({
                params
            }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;

                    this.formatWarehouseOperationListFn(info);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【编辑】出库单类型
        async odoTypeEditAPIFn() {
            this.grnTypeAddLoading = true;
            let status = parseFloat(this.grnTypeAddForm.status),
                params = {
                    org_id: this.currentGrnTypeListTrObj.org_id,
                    type_inner_sn: this.currentGrnTypeListTrObj.type_inner_sn,
                    name: this.grnTypeAddForm.name,
                    status
                };
            await odoTypeEditAPI(params)
                .then((res) => {
                    this.grnTypeAddLoading = false;
                    if (parseFloat(res.code) === 0) {
                        this.grnTypeAddDatas.forEach((item) => {
                            if (
                                parseFloat(item.type_inner_sn) ===
                                parseFloat(this.currentGrnTypeListTrObj.type_inner_sn)
                            ) {
                                item.name = this.grnTypeAddForm.name;
                                item.label = this.grnTypeAddForm.name;
                                item.title = this.grnTypeAddForm.name;
                                item.status = status;
                                item.status_text = status === 0 ? '无效' : '有效';
                            }
                        });
                        status === 0 && this.removeTypeIsAddFn(this.currentGrnTypeListTrObj.type_inner_sn);
                        this.grnTypeList = this.grnTypeAddDatas.filter((item) => parseFloat(item.status) === 1);
                        this.addGrnTypeListModalFn('cancel');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.grnTypeAddLoading = false;
                });
        },
        // 【删除】出库单类型
        async odoTypeDelAPIFn(curObj) {
            await odoTypeDelAPI(curObj.org_id, curObj.type_inner_sn).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const xTableAddType = this.$refs.xTableAddType,
                        curObj = xTableAddType.getCurrentRow();
                    xTableAddType.remove(curObj);
                    xTableAddType.refreshData();
                    this.grnTypeAddDatas = xTableAddType.getTableData().tableData;
                    xTableAddType.setCurrentRow(this.grnTypeAddDatas[0]);
                    this.currentGrnTypeListTrObj = this.grnTypeAddDatas.length ? this.grnTypeAddDatas[0] : {};
                    this.removeTypeIsAddFn(this.currentGrnTypeListTrObj.type_inner_sn);
                    this.grnTypeList = this.grnTypeAddDatas.filter((item) => parseFloat(item.status) === 1);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【新增】出库单类型
        async odoTypeAddAPIFn() {
            this.grnTypeAddLoading = true;
            let params = {
                org_id: this.userBelong,
                name: this.grnTypeAddForm.name,
                status: this.grnTypeAddForm.status
            };
            await odoTypeAddAPI(params)
                .then((res) => {
                    this.grnTypeAddLoading = false;
                    this.addGrnTypeListModal.loading = false;
                    if (parseFloat(res.code) === 0) {
                        const { info } = res,
                            status = parseFloat(info.status),
                            xTableAddType = this.$refs.xTableAddType;
                        let curIndex = xTableAddType.getCurrentRow();
                        if (!curIndex) {
                            curIndex = 0;
                        }
                        xTableAddType
                            .insertAt(
                                {
                                    ...info,
                                    status,
                                    checked: status === 1,
                                    status_text: status === 1 ? '有效' : '无效',
                                    key: info.type_inner_sn + '',
                                    activeClass: '',
                                    value: info.type_inner_sn + '',
                                    label: info.name,
                                    title: info.title
                                },
                                curIndex
                            )
                            .then(({ row }) => {
                                this.grnTypeAddDatas = xTableAddType.getTableData().tableData;
                                xTableAddType.setCurrentRow(row);
                                this.grnTypeList = this.grnTypeAddDatas.filter((item) => parseFloat(item.status) === 1);
                            });
                        this.addGrnTypeListModalFn('cancel');
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addGrnTypeListModal.loading = false;
                    this.grnTypeAddLoading = false;
                });
        },
        // 【查询出库单类型列表】
        async odoTypeListAPIFn(status, typeInnerSn) {
            // status: 0 - 无效   1 - 有效
            let params = {};
            if (typeof status === 'number') {
                params.status = status;
            }
            await odoTypeListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.isGrnTypeLoading = false;
                    const { list } = res;
                    this.grnTypeAddDatas = list.map((item) => {
                        const status = parseFloat(item.status),
                            cur = {
                                ...item,
                                status,
                                checked: status === 1,
                                status_text: status === 1 ? '有效' : '无效',
                                key: item.type_inner_sn + '',
                                activeClass: '',
                                value: item.type_inner_sn + '',
                                label: item.name,
                                title: item.title
                            };
                        if (this.isClickRedDashedOrder) {
                            if (cur.label === constants.redDashedOdoOrder) {
                                this.currentGrnTypeListTrObj = cur;
                                this.addInfoModalForm.type_inner_sn = cur.value;
                                this.addInfoModalTitleComputed = cur.label;
                                this.addInfoModal.title = `${cur.label} - 新增`;
                                this.addInfoModalForm.type_name = cur.label;
                                this.addInfoModalTitleFlag = true;
                            }
                        } else {
                            if (parseFloat(typeInnerSn) === parseFloat(item.type_inner_sn)) {
                                this.currentGrnTypeListTrObj = cur;
                            }
                        }
                        return cur;
                    });

                    list.length && this.$Utils.globalDragCenterFn('modal-grntype');

                    this.grnTypeList = this.grnTypeAddDatas.filter((item) => parseFloat(item.status) === 1);
                    const xTableAddType = this.$refs.xTableAddType;
                    this.grnTypeAddDatas.length &&
                        xTableAddType &&
                        xTableAddType.setCurrentRow(this.grnTypeAddDatas[0]);

                    if (this.btnName === 'add') {
                        if (this.grnTypeList.length) {
                            this.addInfoModalForm.type_inner_sn = this.grnTypeList[0].value;
                            this.addInfoModalForm.type_name = this.grnTypeList[0].label;
                        } else {
                            this.addInfoModalForm.type_inner_sn = '';
                            this.addInfoModalForm.type_name = '';
                        }
                    }
                    if (
                        this.grnTypeList.findIndex(
                            (item) => parseInt(item.type_inner_sn) === parseInt(this.addInfoModalForm.type_inner_sn)
                        ) === -1
                    ) {
                        this.addInfoModalForm.type_inner_sn = '';
                        this.addInfoModalForm.type_name = '';
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 获取【出库单详情】
        async odoInfoAPIFn() {
            this.reviewer_name = '';
            this.sheelListData = [];
            this.delInfoDatas = [];
            this.voucher_type_c = 0;
            this.voucher_inner_sn_c = 0;
            this.isLoadingDepartmentInnerSn = true;
            this.isGrnTypeLoading = true;
            const curObj = this.$refs.xTable.getCurrentRow();
            const params = {
                org_id: curObj.org_id || this.userBelong,
                odo_inner_sn: this.currentTrObj.odo_inner_sn,
                billsType: this.currentTrObj.billsType
            };
            await odoInfoAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        // if (this.grnOdoOrderIsCopyFn(info.voucher_type_c)) {
                        //     return
                        // }
                        /// 复制当前单据 ===> 将该单据的主状态置为 [0 - '正式']
                        if (this.btnName === 'copy') {
                            info.status = 0;
                            if (info.voucher_type_c !== 'NONE' || !info.voucher_type_c) {
                                this.isSheel = true;
                                this.sheelID = info.voucher_type_c;
                            }
                            this.grnOdoOrderCopyFn();
                        }
                        info.type_inner_sn = info.type_inner_sn + '';
                        this.making_people_name = info.making_people_name;
                        this.auditor_name = info.auditor_name;
                        this.reviewer_name = info.reviewer_name;
                        this.addInfoModalForm = { ...info };
                        this.addInfoModalForm.organization_name_old = info.organization_name;
                        this.addInfoModalForm.organization_inner_sn_old = info.organization_inner_sn;
                        this.addInfoModalForm.warehouse_inner_sn_old = info.warehouse_inner_sn;
                        this.addInfoModalForm.use_department = info.use_department;
                        this.addInfoModalForm.use_department_old = info.use_department;
                        if (info.type_name === constants.redDashedOdoOrder) {
                            this.addInfoModal.title = `${info.type_name} - 修改`;
                        }
                        if (typeof info.type_inner_sn !== 'undefined') {
                            this.grnTypeList = [
                                {
                                    name: info.type_name,
                                    label: info.type_name,
                                    title: info.type_name,
                                    status: info.status,
                                    status_text: '无效',
                                    key: info.type_inner_sn,
                                    value: info.type_inner_sn
                                }
                            ];
                        }
                        if (typeof info.warehouse_inner_sn !== 'undefined') {
                            this.wareHouseFilterData = [
                                {
                                    name: info.warehouse_name,
                                    label: info.warehouse_name,
                                    title: info.warehouse_name,
                                    key: info.warehouse_inner_sn,
                                    value: info.warehouse_inner_sn
                                }
                            ];
                            this.isWareHouseFilterLoading = true;
                        }
                        this.warehouseOperatorList = [
                            {
                                title: info.warehouse_operator_name,
                                value: info.warehouse_operator_inner_sn,
                                key: info.warehouse_operator_inner_sn,
                                isLoading: false // 是否加载过
                            }
                        ];

                        this.superiorDepartments = [
                            {
                                title: info.department_name,
                                value: info.department_inner_sn,
                                key: info.department_inner_sn
                            }
                        ];
                        // this.warehouseUsableTopListAPIFn(this.addInfoModalForm.warehouse_inner_sn) // 仓库
                        // this.orgDeptTopTreeAPIFn() // 部门
                        // this.odoTypeListAPIFn() // 单据类别
                        this.orgEmpDropdownListAPIFn(); // 经手人
                        // this.odoWeListAPIFn()
                        this.voucher_type_c = info.voucher_type_c;
                        this.voucher_inner_sn_c = info.voucher_inner_sn_c;
                        this.sl_inner_sn_m = info.voucher_inner_sn_c;
                        for (const key in info) {
                            if (key !== 'details') {
                                this.editInfoFormObj[key + '_old'] = info[key];
                            }
                        }

                        this.addInfoDatas = info.details.map((item, index) => {
                            item.seq = index;
                            if (item.product_type === 0) {
                                item.mdrf_inner_sn = '';
                                item.mdrf_sn = '';
                            }
                            item.focusDatas = [
                                false,
                                index === 0 && parseInt(info.status) <= 0,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false
                            ];
                            item.key = Math.random();
                            item.index = index;
                            item.activeClass = '';
                            item.type = item.type + '';
                            item.produce_date = item.produce_date && moment(item.produce_date);
                            item.overdue_date = item.overdue_date && moment(item.overdue_date);
                            item.name = item.product_name;
                            item.mdrf_inner_sn = item.mdrf_inner_sn || '';
                            item.mdrfList = item.mdrf_sn
                                ? [{ value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn }]
                                : [];
                            item.unitList = item.unit_inner_sn
                                ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                : [];
                            item.unit_inner_sn_old = item.unit_inner_sn ? [item.unit_inner_sn] : [];
                            item.unit_inner_sn = item.unit_inner_sn ? [item.unit_inner_sn] : [];
                            item.isLoadingUnit = false; // 是否加载过整棵树
                            item.hospital_inner_sn = info.organization_inner_sn; // 当调单为发放单时，需要这个字段
                            item.gv_inner_sn = info.voucher_inner_sn_c; // 当调单为发放单时，需要这个字段
                            item.grant_quantity = item.quantity; // 需要记录总数量
                            item.quantity_old = item.quantity; // 记录历史数量
                            item.batch_number_old = item.batch_number; // 记录历史批号
                            item.action = -1; // 默认为未操作
                            item.product_vendor_inner_sn = item.product_vendor_inner_sn
                                ? item.product_vendor_inner_sn
                                : item.vendor_inner_sn;
                            item.batch_locked_quantity = 0;
                            item.batchDatas = [];
                            item.tax_unit_price = item.unit_price;
                            item.money = parseFloat(item.quantity)
                                ? (item.quantity * item.tax_unit_price).toFixed(4)
                                : '';
                            item.unitPriceList = [];
                            item.inventoryBannce = 0;
                            item.rate = Math.floor(item.rate * 100);
                            item.warehouse_inner_sn = this.addInfoModalForm.warehouse_inner_sn || '';
                            item.max_batch_locked_quantity = 0;
                            if (typeof item.min_unit_info === 'undefined') {
                                item.min_unit_info = 1; // 默认值
                                item.min_measure = 1; // 默认值
                            }
                            item.average_price = 0;
                            item.average_price_compare = 0;
                            // 修复从列表过去的时候，新增产品获取不到 order_type 订单类型 0-DealerDealer 1-DealerVendor 2-hospitalDealer 3-hospitalVendor
                            // 不同的接口名字不一样，order_type === odo_type
                            if (parseFloat(info.voucher_type_c) === 2 || info.voucher_type_c !== 'NONE') {
                                item.sl_inner_sn = info.voucher_inner_sn_c;
                                item.billsType = info.billsType;
                            }
                            if (parseInt(info.status) <= 0) {
                                // 正式
                                this.contractGetContractPriceAPIFn(item);
                            }
                            this.loadUnitMinFn(item, index);
                            return item;
                        });
                        // // 修复从列表过去的时候，新增产品获取不到 order_type 订单类型 0-DealerDealer 1-DealerVendor 2-hospitalDealer 3-hospitalVendor
                        // // 不同的接口名字不一样，order_type === odo_type
                        // if (parseFloat(info.voucher_type_c) === 2) {
                        //     this.addInfoDatas.forEach(item => {
                        //         item.sl_inner_sn = info.voucher_inner_sn_c
                        //         item.billsType = info.billsType
                        //     })
                        // }
                        this.sheelListData = JSON.parse(JSON.stringify(this.addInfoDatas));
                        this.sheelListDataSame = []; // 原始数据的去重
                        this.sheelListData.forEach((item, index) => {
                            if (index === 0) {
                                this.sheelListDataSame.push(item);
                            } else {
                                let isf = true;
                                this.sheelListDataSame.forEach((itemS) => {
                                    if (
                                        item.product_type === itemS.product_type &&
                                        item.product_inner_sn === itemS.product_inner_sn &&
                                        item.vendor_inner_sn === itemS.vendor_inner_sn &&
                                        item.specification === itemS.specification
                                    ) {
                                        itemS.grant_quantity += item.grant_quantity;
                                        itemS.grant_quantity = this.reNumber(itemS.grant_quantity);
                                        isf = false;
                                    }
                                });
                                if (isf) {
                                    this.sheelListDataSame.push(item);
                                }
                            }
                        });

                        // if (info.voucher_type_c !== 0) {
                        //     this.isSheel = true
                        //     this.sheelID = info.voucher_type_c
                        // }
                        if (info.voucher_type_c !== 'NONE') {
                            this.isSheel = true;
                            this.sheelID = info.voucher_type_c;
                        }
                        const xTableAddInfo = this.$refs.xTableAddInfo;
                        xTableAddInfo.updateData();
                        this.handleResetMultipleFocus();
                        xTableAddInfo.focus();
                        xTableAddInfo.refreshScroll();
                        if (this.addInfoDatas && this.addInfoDatas.length) {
                            xTableAddInfo.setCurrentRow(this.addInfoDatas[0]);
                            this.clearCurrentBatchNUmberFn();
                            this.isAddInfoTempData = false;
                            this.addInfoTempDatas = JSON.parse(JSON.stringify(this.addInfoDatas));
                            this.batchNumberSetVal(this.addInfoDatas[0], 'init', []);
                        }

                        /// 修复用户上来直接删除数据没有触发批号选择数据，所以界面编辑首次打开批号
                        // this.batchRequestFn(this.addInfoDatas[0], 1)
                        // this.record = { ...this.addInfoDatas[0] }
                        this.$nextTick(() => {
                            let timerFn = setTimeout(() => {
                                clearTimeout(timerFn);
                                timerFn = null;
                                let _productSet = new Set();
                                this.addInfoDatas.forEach((item, index) => {
                                    const currentRowItemSplitKey = `${item.product_type}${item.vendor_inner_sn}${item.product_inner_sn}${item.specification_inner_sn}`;
                                    if (!_productSet.has(currentRowItemSplitKey)) {
                                        _productSet.add(currentRowItemSplitKey);
                                        this.record = { ...item, asynchronous: true }; /// 1. 异步请求标记，等待包装单位加载完毕
                                        this.batchRequestFn(item, index + 1);
                                    }
                                });
                            }, 1000);
                        });
                        if (this.addInfoDatas && this.addInfoDatas.length <= 0) {
                            this.addInfoLoading = false;
                        }
                    } else {
                        this.addInfoLoading = false;
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addInfoLoading = false;
                })
                .finally(() => {
                    this.isCopyLoading = false;
                });
        },
        reNumber(data) {
            let num = data.toString();
            let numA = 0;
            if (num.indexOf('.') !== -1) {
                numA = num.split('.')[1].length;
            }
            if (numA > 10) {
                data = this.$Utils.commafy(new Big(data).round(2).toNumber());
            }
            return data;
        },
        // 【编辑】出库单
        async odoEditAPIFn(status) {
            const curObj = this.addData[this.editCellRow],
                params = {
                    department_inner_sn: this.addInfoModalForm.department_inner_sn,
                    department_inner_sn_old: this.editInfoFormObj.department_inner_sn_old,
                    department_name: this.addInfoModalForm.department_name,
                    department_name_old: this.editInfoFormObj.department_name_old,
                    use_department: this.addInfoModalForm.use_department,
                    use_department_old: this.addInfoModalForm.use_department_old,
                    description: this.addInfoModalForm.description,
                    description_old: this.editInfoFormObj.description_old,
                    handler_inner_sn: this.addInfoModalForm.handler_inner_sn,
                    handler_inner_sn_old: this.editInfoFormObj.handler_inner_sn_old,
                    handler_name: this.addInfoModalForm.handler_name,
                    handler_name_old: this.editInfoFormObj.handler_name_old,
                    odo_inner_sn: curObj.odo_inner_sn,
                    odo_sn: '',
                    odo_sn_old: '',
                    billsType: this.addInfoModalForm.billsType,
                    billsType_old: this.editInfoFormObj.billsType_old,
                    org_id: this.addInfoModalForm.org_id || this.userBelong,
                    organization_inner_sn: this.addInfoModalForm.organization_inner_sn,
                    organization_inner_sn_old: this.editInfoFormObj.organization_inner_sn_old,
                    organization_name: this.addInfoModalForm.organization_name,
                    organization_name_old: this.editInfoFormObj.organization_name_old,
                    status,
                    type_inner_sn: this.addInfoModalForm.type_inner_sn,
                    type_inner_sn_old: this.editInfoFormObj.type_inner_sn_old,
                    type_name: this.addInfoModalForm.type_name,
                    type_name_old: this.editInfoFormObj.type_name_old,
                    warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                    warehouse_inner_sn_old: this.editInfoFormObj.warehouse_inner_sn_old,
                    warehouse_name: this.addInfoModalForm.warehouse_name,
                    warehouse_name_old: this.editInfoFormObj.warehouse_name_old,
                    warehouse_operator_inner_sn: this.addInfoModalForm.warehouse_operator_inner_sn,
                    warehouse_operator_inner_sn_old: this.editInfoFormObj.warehouse_operator_inner_sn_old,
                    warehouse_operator_name: this.addInfoModalForm.warehouse_operator_name,
                    warehouse_operator_name_old: this.editInfoFormObj.warehouse_operator_name_old
                };

            let newArr = [];
            let data = this.getStableData();
            data.forEach((item, index) => {
                if (item.product_inner_sn) {
                    if (this.isSheel) {
                        item.name = item.product_name; // 修复调单的时候name取不到
                    }
                    newArr.push({
                        'buying_price': item.average_price || item.buying_price || '',
                        'detail_inner_sn': item.detail_inner_sn || '',
                        'product_vendor_inner_sn':
                            item.vendor_inner_sn || item.dealer_inner_sn || item.product_vendor_inner_sn,
                        'product_vendor_name': item.vendor_name || item.dealer_name || item.product_vendor_name,
                        'product_inner_sn': item.product_inner_sn,
                        'product_name': item.name,
                        'specification_inner_sn': item.specification_inner_sn,
                        'specification': item.specification,
                        'unit_inner_sn': item.unit_inner_sn.length
                            ? item.unit_inner_sn[item.unit_inner_sn.length - 1]
                            : '',
                        'unit_name': this.$Utils.UnitBracketsTrim(item.unit_name),
                        'mdrf_inner_sn': Array.isArray(item.mdrf_inner_sn)
                            ? item.mdrf_inner_sn[item.mdrf_inner_sn.length - 1] || ''
                            : item.mdrf_inner_sn,
                        'mdrf_sn': item.mdrf_sn || '',
                        'unit_price_text': item.tax_unit_price,
                        'rate': item.rate / 100,
                        'quantity': item.quantity,
                        'produce_date': item.produce_date ? moment(item.produce_date).format(this.format) : '',
                        'overdue_date': item.overdue_date ? moment(item.overdue_date).format(this.format) : '',
                        'type': item.type,
                        'sort_number': index,
                        'batch_number': item.batch_number.trim(),
                        'product_type': item.product_type,
                        'action': item.action, // 接口加入出库单时新增字段
                        // 'quantity_old': item.quantity_old ? item.quantity_old : item.quantity, // 接口加入出库单时新增字段
                        // 'batch_number_old': item.batch_number_old ? item.batch_number_old : '', // 接口加入出库单时新增字段
                        // 'unit_inner_sn_old': item.unit_inner_sn_old && item.unit_inner_sn_old.length ? item.unit_inner_sn_old[item.unit_inner_sn_old.length - 1] : '', // 接口加入出库单时新增字段
                        'voucher_detail_inner_sn_c': item.voucher_detail_inner_sn_c,
                        'voucher_inner_sn_c': item.voucher_inner_sn_c,
                        'voucher_type_c': item.voucher_type_c,
                        'health_care_sn': item.health_care_sn
                    });
                }
            });
            // let delInfoDatasA = []
            // this.delInfoDatas.forEach(item => {
            //     let Aitem = {}
            //     Aitem.detail_inner_sn = item.detail_inner_sn
            //     Aitem.product_vendor_inner_sn = item.vendor_inner_sn || item.dealer_inner_sn
            //     Aitem.product_vendor_name = item.vendor_name || item.dealer_name
            //     Aitem.product_inner_sn = item.product_inner_sn
            //     Aitem.product_name = item.product_name
            //     Aitem.specification_inner_sn = item.specification_inner_sn
            //     Aitem.specification = item.specification
            //     Aitem.unit_inner_sn = item.unit_inner_sn[0]
            //     Aitem.unit_name = item.unit_name
            //     Aitem.mdrf_inner_sn = item.mdrf_inner_sn
            //     Aitem.mdrf_sn = item.mdrf_sn
            //     Aitem.unit_price = item.unit_price
            //     Aitem.quantity = item.quantity
            //     Aitem.produce_date = item.produce_date
            //     Aitem.overdue_date = item.overdue_date
            //     Aitem.type = item.type
            //     Aitem.sort_number = item.sort_number
            //     Aitem.batch_number = item.batch_number
            //     Aitem.product_type = item.product_type
            //     Aitem.action = 0
            //     Aitem.quantity_old = item.quantity_old
            //     Aitem.batch_number_old = item.batch_number_old
            //     Aitem.unit_inner_sn_old = item.unit_inner_sn_old && item.unit_inner_sn_old.length ? item.unit_inner_sn_old[item.unit_inner_sn_old.length - 1] : ''
            //     Aitem.voucher_detail_inner_sn_c = item.voucher_detail_inner_sn_c
            //     Aitem.voucher_inner_sn_c = item.voucher_inner_sn_c
            //     Aitem.voucher_type_c = item.voucher_type_c
            //     delInfoDatasA.push(Aitem)
            // })
            // let paramsDetails = newArr
            // // paramsDetails = paramsDetails.concat(delInfoDatasA)

            params.details = [];

            // paramsDetails.forEach(item => {
            //     if (item.action !== -1) {
            //         params.details.push(item)
            //     } else {
            //         if (item.batch_number !== item.batch_number_old) {
            //             item.action = 2
            //             params.details.push(item)
            //         }
            //     }
            // })

            params.details = newArr;
            params.details.forEach((item) => {
                if (item.produce_date === '') {
                    delete item.produce_date;
                }
                delete item.action;
            });

            await odoEditAPI(params)
                .then((res) => {
                    this.addInfoModal.saveLoading = false;
                    if (parseFloat(res.code) === 0) {
                        // 如果单位名称改变了前端就在旧位置插入即可
                        this.editGrnTypeFlag = params.billsType !== params.billsType_old;
                        this.changeTabFn('history');
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.addInfoModal.saveLoading = false;
                    this.addInfoModal.auditLoading = false;
                    this.addInfoModal.godownEntryLoading = false;
                    this.dblclickAddProductFn();
                })
                .catch((err) => {
                    if (err.response && err.response.data) {
                        this.beyondInventoryModalFn(err.response.data.message);
                    }
                    this.addInfoModal.saveLoading = false;
                    this.addInfoModal.auditLoading = false;
                    this.addInfoModal.godownEntryLoading = false;
                });
        },
        // 【删除】出库单
        async odoDelAPIFn() {
            const curObj = this.$refs.xTable.getCurrentRow(),
                params = {
                    org_id: curObj.org_id || this.userBelong,
                    odo_inner_sn: curObj.odo_inner_sn,
                    billsType: curObj.billsType
                };
            await odoDelAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.odoListAPIFn();
                    this.editGrnTypeFlag = false;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 查询价格
        async contractGetContractPriceAPIFn(record) {
            let tempUnitInnerSn = Array.isArray(record.unit_inner_sn)
                ? record.unit_inner_sn[record.unit_inner_sn.length - 1]
                : record.unit_inner_sn;
            let params = {
                billsType: this.addInfoModalForm.billsType,
                organization_inner_sn: this.addInfoModalForm.organization_inner_sn,
                product_inner_sn: record.product_inner_sn,
                product_type: record.product_type,
                product_vendor_inner_sn:
                    record.product_vendor_inner_sn || record.vendor_inner_sn || record.dealer_inner_sn,
                specification_inner_sn: record.specification_inner_sn,
                unit_inner_sn: typeof tempUnitInnerSn === 'undefined' ? 0 : tempUnitInnerSn,
                saleUse: true,
                isLast: true // 是否查询最新销售出库单价
            };

            await contractGetContractPriceAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    record.unitPriceList = list || [];
                    this.$set(this.addInfoDatas[record.seq], 'unitPriceList', list);

                    // fix: 修复同一个产品，重新将当前获取到的含税单价列表赋值给同一个产品
                    this.addInfoDatas.forEach((item, index) => {
                        let productVendorInnerSn =
                                item.product_vendor_inner_sn || item.vendor_inner_sn || item.dealer_inner_sn,
                            unitInnerSn = Array.isArray(item.unit_inner_sn)
                                ? item.unit_inner_sn[item.unit_inner_sn.length - 1]
                                : item.unit_inner_sn;
                        if (
                            item.product_type === params.product_type &&
                            productVendorInnerSn === params.product_vendor_inner_sn &&
                            item.specification_inner_sn === params.specification_inner_sn &&
                            item.product_inner_sn === params.product_inner_sn &&
                            unitInnerSn === params.unit_inner_sn
                        ) {
                            this.$set(this.addInfoDatas[index], 'unitPriceList', list);
                        }
                    });

                    if (record.unitPriceList.length && typeof record.detail_inner_sn === 'undefined') {
                        record.unit_price_text = record.unitPriceList[0];
                        record.tax_unit_price = record.unitPriceList[0];
                        record.money = parseFloat(record.quantity)
                            ? (record.quantity * record.tax_unit_price).toFixed(4)
                            : '';
                    }
                    const xTableAddInfo = this.$refs.xTableAddInfo;
                    if (xTableAddInfo) {
                        xTableAddInfo.updateData();
                    } else {
                        xTableAddInfo.updateData();
                    }
                    // this.updateFooterPlanModalFn()
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【新增】出库单
        async odoAddAPIFn(status) {
            let params = {
                org_id: this.userBelong,
                organization_inner_sn: this.addInfoModalForm.organization_inner_sn,
                organization_name: this.addInfoModalForm.organization_name,
                status,
                handler_inner_sn: this.addInfoModalForm.handler_inner_sn,
                handler_name: this.addInfoModalForm.handler_name,
                type_inner_sn: this.addInfoModalForm.type_inner_sn,
                type_name: this.addInfoModalForm.type_name,
                warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                warehouse_name: this.addInfoModalForm.warehouse_name
                // odo_sn: this.addInfoModalForm.odo_sn
            };
            if (this.addInfoModalForm.department_inner_sn) {
                params.department_inner_sn = this.addInfoModalForm.department_inner_sn;
                params.department_name = this.addInfoModalForm.department_name;
            }
            if (this.$Utils.trim(this.addInfoModalForm.description)) {
                params.description = this.addInfoModalForm.description;
            }
            if (this.addInfoModalForm.warehouse_operator_inner_sn) {
                params.warehouse_operator_inner_sn = this.addInfoModalForm.warehouse_operator_inner_sn;
                params.warehouse_operator_name = this.addInfoModalForm.warehouse_operator_name;
            }
            if (!this.c_IsGrn) {
                params.use_department = this.addInfoModalForm.use_department;
                params.use_department_old = this.addInfoModalForm.use_department_old;
            }

            if (this.isSheel && this.sheelID === 'GRANT') {
                params.billsType = 'HH';
            } else {
                params.billsType = this.addInfoModalForm.billsType;
            }
            params.details = [];
            let data = this.getStableData();
            data.forEach((item, index) => {
                // 调单
                let temporaryInsertObj = {};
                if (this.isSheel) {
                    temporaryInsertObj.voucher_detail_inner_sn_c =
                        item.voucher_detail_inner_sn_c || item.detail_inner_sn || '';
                    if (typeof item.sl_inner_sn !== 'undefined' || typeof item.gv_inner_sn !== 'undefined') {
                        temporaryInsertObj.voucher_inner_sn_c =
                            this.sheelID === 'GRANT' ? item.gv_inner_sn : item.sl_inner_sn; // 'GRANT' => gv_inner_sn   'SALE_RECORD' => sl_inner_sn 杨叔说暂时只有这两种类型
                        temporaryInsertObj.voucher_type_c = this.sheelID;
                    } else {
                        temporaryInsertObj.voucher_inner_sn_c = item.voucher_inner_sn_c;
                        temporaryInsertObj.voucher_type_c = item.voucher_type_c;
                    }
                } else {
                    temporaryInsertObj.voucher_type_c = 'NONE';
                }
                if (item.product_inner_sn) {
                    params.details.push({
                        'action': 1, // 接口加入出库单时新增字段
                        'buying_price': item.average_price || item.buying_price || '',
                        'batch_number': item.batch_number.trim(),
                        'batch_number_old': item.batch_number, // 接口加入出库单时新增字段
                        'detail_inner_sn': item.detail_inner_sn || '',
                        'mdrf_inner_sn': Array.isArray(item.mdrf_inner_sn)
                            ? item.mdrf_inner_sn[item.mdrf_inner_sn.length - 1] || ''
                            : item.mdrf_inner_sn,
                        'mdrf_sn': item.mdrf_sn || '',
                        'overdue_date': item.overdue_date ? moment(item.overdue_date).format(this.format) : '',
                        'produce_date': item.produce_date ? moment(item.produce_date).format(this.format) : '',
                        'product_inner_sn': item.product_inner_sn,
                        'product_name': item.name,
                        'product_type': item.product_type,
                        'product_vendor_inner_sn': item.vendor_inner_sn,
                        'product_vendor_name': item.vendor_name,
                        'quantity': item.quantity,
                        'quantity_old': item.quantity, // 接口加入出库单时新增字段
                        'sort_number': index,
                        'specification': item.specification,
                        'specification_inner_sn': item.specification_inner_sn,
                        'type': item.type,
                        'unit_inner_sn': Array.isArray(item.unit_inner_sn)
                            ? item.unit_inner_sn[item.unit_inner_sn.length - 1] || ''
                            : item.unit_inner_sn,
                        'unit_inner_sn_old': Array.isArray(item.unit_inner_sn)
                            ? item.unit_inner_sn[item.unit_inner_sn.length - 1] || ''
                            : item.unit_inner_sn, // 接口加入出库单时新增字段
                        // 'unit_inner_sn_old': item.unit_inner_sn.length ? item.unit_inner_sn[item.unit_inner_sn.length - 1] : '', // 接口加入出库单时新增字段
                        'unit_name': this.$Utils.UnitBracketsTrim(item.unit_name),
                        'unit_price_text': item.tax_unit_price,
                        'rate': item.rate / 100,
                        'health_care_sn': item.health_care_sn,
                        ...temporaryInsertObj
                    });
                }
            });

            params.details.forEach((item) => {
                if (item.produce_date === '' || item.produce_date === 'Invalid date') {
                    delete item.produce_date;
                }

                delete item.action; // 出库单不需要传递action，要全量传给后端
            });

            // if (this.isSheel && this.sheelID === 'GRANT') {
            //     // 发放单生成出库单
            //     this.hospitalGrantCreatodoAPIFn(status, params)
            //     return
            // } else if (this.isSheel && this.sheelID === 'SALE_RECORD') {
            //     // 销售单生成出库单
            //     this.saleRecordCreatodoAPIFn(status, params)
            //     return
            // }
            await odoAddAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.godownEntryPagination.total++;
                        const { info } = res;
                        // 不是红冲的新增插入
                        if (!this.isClickRedDashedOrder) {
                            this.changeTabFn('history');
                        } else {
                            this.$warning({
                                title: '操作提示',
                                content: `红冲单已经生成，单号：${info.odo_sn}，请前往“出库”页面确认红冲单无误后，进行出库操作以完成红冲。`,
                                okText: '知道了',
                                centered: true,
                                maskClosable: true
                            });
                            this.closeAddInfoModalResetScrollFn();
                            this.resetAddInfoModalFn();
                        }
                        this.isSheel = false; // 调单标识重置
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.addInfoModal.saveLoading = false;
                    this.addInfoModal.auditLoading = false;
                    this.addInfoModal.godownEntryLoading = false;
                })
                .catch((err) => {
                    const response = err.response;
                    if (response && response.data) {
                        this.beyondInventoryModalFn(response.data.message);
                    }
                    this.addInfoModal.saveLoading = false;
                    this.addInfoModal.auditLoading = false;
                    this.addInfoModal.godownEntryLoading = false;
                });
        },
        // 发放单生成出库单
        async hospitalGrantCreatodoAPIFn(status, params) {
            await hospitalGrantCreatodoAPI(this.userBelong, this.addInfoDatas[0].gv_inner_sn, params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.godownEntryPagination.total++;
                        const { info } = res,
                            xTable = this.$refs.xTable,
                            record = xTable.getCurrentRow();
                        xTable
                            .insertAt(
                                {
                                    ...info,
                                    key: info.odo_inner_sn + '' + info.billsType,
                                    activeClass: '',
                                    status_text: this.grnStatus[parseFloat(info.status)],
                                    grn_sn: info.odo_sn, // 为了兼容出库单
                                    in_time: info.out_time
                                },
                                record
                            )
                            .then(({ row }) => {
                                this.godownEntryDatas = xTable.getTableData().tableData;
                                xTable.setCurrentRow(row);
                                if (this.godownEntryDatas.length > this.godownEntryPagination.pageSize) {
                                    this.godownEntryDatas.splice(this.godownEntryDatas.length - 1, 1);
                                }
                                this.closeAddInfoModalResetScrollFn();
                                this.resetAddInfoModalFn();
                            });
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.addInfoModal.saveLoading = false;
                    this.addInfoModal.auditLoading = false;
                    this.addInfoModal.godownEntryLoading = false;
                })
                .catch(() => {
                    this.addInfoModal.saveLoading = false;
                    this.addInfoModal.auditLoading = false;
                    this.addInfoModal.godownEntryLoading = false;
                });
        },
        // 销售单生成出库单
        async saleRecordCreatodoAPIFn(status, params) {
            delete params.org_id;

            if (!params.billsType) {
                params.billsType = this.addInfoModalForm.billsType;
            }

            params.details = this.addInfoDatas.map((item, index) => {
                return {
                    'sl_detail_inner_sn': item.detail_inner_sn || '',
                    'product_vendor_inner_sn': item.vendor_inner_sn,
                    'product_vendor_name': item.vendor_name,
                    'product_inner_sn': item.product_inner_sn,
                    'product_name': item.name,
                    'specification_inner_sn': item.specification_inner_sn,
                    'specification': item.specification,
                    'unit_inner_sn': item.unit_inner_sn.length ? item.unit_inner_sn[item.unit_inner_sn.length - 1] : '',
                    'unit_name': item.unit_name,
                    'mdrf_inner_sn': item.mdrf_inner_sn,
                    'mdrf_sn': item.mdrf_sn,
                    'quantity': item.quantity,
                    'produce_date': item.produce_date ? moment(item.produce_date).format(this.format) : '',
                    'overdue_date': item.overdue_date ? moment(item.overdue_date).format(this.format) : '',
                    'type': item.type,
                    'unit_price_text': item.unit_price,
                    'sort_number': index,
                    'batch_number': item.batch_number.trim(),
                    'product_type': item.product_type
                };
            });

            // 新增销售单生成出库单odo_sn
            await saleRecordCreatodoAPI(this.userBelong, this.sl_inner_sn_m || this.getSheel.voucher_inner_sn_c, params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.godownEntryPagination.total++;
                        const { info } = res,
                            xTable = this.$refs.xTable,
                            record = xTable.getCurrentRow();
                        xTable
                            .insertAt(
                                {
                                    ...info,
                                    key: info.odo_inner_sn + '' + info.billsType,
                                    activeClass: '',
                                    status_text: this.grnStatus[parseFloat(info.status)],
                                    grn_sn: info.odo_sn, // 为了兼容出库单
                                    in_time: info.out_time
                                },
                                record
                            )
                            .then(({ row }) => {
                                this.godownEntryDatas = xTable.getTableData().tableData;
                                xTable.setCurrentRow(row);
                                if (this.godownEntryDatas.length > this.godownEntryPagination.pageSize) {
                                    this.godownEntryDatas.splice(this.godownEntryDatas.length - 1, 1);
                                }
                                this.closeAddInfoModalResetScrollFn();
                                this.resetAddInfoModalFn();
                            });
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.addInfoModal.saveLoading = false;
                    this.addInfoModal.auditLoading = false;
                    this.addInfoModal.godownEntryLoading = false;
                })
                .catch(() => {
                    this.addInfoModal.saveLoading = false;
                    this.addInfoModal.auditLoading = false;
                    this.addInfoModal.godownEntryLoading = false;
                });
        },
        // 【出库】
        async odoPutoutAPIFn() {
            const xTable = this.$refs.xTable,
                curObj = xTable.getCurrentRow(),
                params = {
                    // 2020-4-1 接口增加字段 warehouse_inner_sn
                    ids: [
                        {
                            odo_inner_sn: curObj.odo_inner_sn,
                            billsType: curObj.billsType,
                            warehouse_inner_sn: curObj.warehouse_inner_sn
                        }
                    ],
                    org_id: curObj.org_id || this.userBelong
                };
            const PutoutAPI =
                this.orderTypeData.value === grnAndOdoOrderType['SALERETURN'].value
                    ? saleReturnPutoutAPI
                    : odoPutoutAPI;
            await PutoutAPI(params)
                .then((res) => {
                    this.addInfoModal.godownEntryLoading = false;
                    if (parseFloat(res.code) === 0) {
                        for (let i = 0; i < this.godownEntryDatas.length; i++) {
                            const item = this.godownEntryDatas[i];
                            if (item._XID === curObj._XID) {
                                item.status = 2; // 已出库
                                item.status_text = this.grnStatus[parseFloat(item.status)];
                                item.in_time = moment(Date()).format('YYYY-MM-DD HH:mm:ss');
                                xTable.updateData();
                                xTable.setCurrentRow(item);
                                break;
                            }
                        }
                        this.closeAddInfoModalResetScrollFn();
                        this.resetAddInfoModalFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addInfoModal.godownEntryLoading = false;
                });
        },
        // 【搜索】入库单列表
        async odoListAPIFn(grnInnerSn) {
            const typeNameFlag = typeof grnInnerSn === 'object'; // 详见新增
            this.godownEntryPagination.loading = true;
            let params = {
                org_id: this.userBelong,
                pageNum: this.godownEntryPagination.current,
                pageSize: this.godownEntryPagination.pageSize,
                orderByColumn: 'odo_sn',
                isAsc: 'desc'
            };

            // 判断是否是高级搜索
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                if (this.searchTables.handler_name) {
                    params.handler_name = this.searchTables.handler_name;
                }

                if (this.searchTables.warehouse_model_inner_sn) {
                    params.warehouse_model_inner_sn = this.searchTables.warehouse_model_inner_sn;
                }

                if (this.searchTables.status) {
                    params.status = this.searchTables.status;
                }

                if (this.searchTables.begin_date) {
                    params.begin_date = this.searchTables.begin_date;
                }

                if (this.searchTables.end_date) {
                    params.end_date = this.searchTables.end_date;
                }

                if (this.searchTables.key) {
                    params.key = this.searchTables.key;
                }

                if (this.searchTables.product_name) {
                    params.product_name = this.searchTables.product_name;
                }
            }
            if (this.searchTables.grn_odo_sn) {
                params.odo_sn = this.searchTables.grn_odo_sn;
            }
            await odoListAPI({ params })
                .then((res) => {
                    this.godownEntryPagination.loading = false;
                    const xTable = this.$refs.xTable;
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        let isAdd = false;
                        this.godownEntryDatas = rows.map((item, index) => {
                            let curItem = {
                                ...item,
                                index,
                                grn_sn: item.odo_sn, // 为了兼容出库单
                                key: item.odo_inner_sn + '' + item.billsType,
                                activeClass: '',
                                status_text: this.grnStatus[parseFloat(item.status)],
                                in_time: item.out_time
                            };
                            if (typeNameFlag) {
                                if (grnInnerSn.curItem.key === curItem.key) {
                                    this.currentTrObj = curItem;
                                    isAdd = true;
                                }
                            } else {
                                if (grnInnerSn && curItem.key === grnInnerSn) {
                                    this.currentTrObj = curItem;
                                }
                            }
                            return curItem;
                        });

                        if (this.godownEntryDatas.length) {
                            this.$nextTick(() => {
                                xTable.loadData(this.godownEntryDatas);
                            });
                        }

                        if (typeNameFlag) {
                            if (!isAdd) {
                                this.godownEntryDatas.splice(this.godownEntryDatas.length - 1, 1, grnInnerSn.curItem);
                                this.currentTrObj = grnInnerSn.curItem;
                            }
                        } else {
                            if (!grnInnerSn) {
                                xTable.setCurrentRow(this.godownEntryDatas[0]);
                                this.currentTrObj = this.godownEntryDatas[0];
                            }
                        }

                        if (Object.keys(this.currentTrObj).length <= 0) {
                            this.currentTrObj = this.godownEntryDatas[0];
                        }

                        this.godownEntryPagination = {
                            ...this.godownEntryPagination,
                            total,
                            loading: false
                        };
                    } else {
                        this.$message.error(res.msg);
                    }
                    xTable.refreshColumn();
                })
                .finally(() => {
                    this.godownEntryPagination.loading = false;
                });
        },
        // 获取产品包装单位的最小倍数 【厂商】
        async platformUnitMinAPIFn(record, index, type) {
            if (record.unit_inner_sn && record.unit_inner_sn.length <= 0) return;
            await platformUnitMinAPI(
                record.product_vendor_inner_sn || record.vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn && record.unit_inner_sn.length
                    ? record.unit_inner_sn[record.unit_inner_sn.length - 1]
                    : ''
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res,
                        minUnitInfo = parseFloat(info);
                    record.min_unit_info = minUnitInfo; // 产品包装单位的最小倍数
                    const averagePrice = record.average_price || 0;
                    record.average_price_compare = averagePrice * record.min_unit_info;
                    if (
                        type !== 'add' &&
                        this.sheelListData.length &&
                        typeof this.sheelListData[index] !== 'undefined'
                    ) {
                        this.sheelListData[index].min_unit_info = minUnitInfo; // 产品包装单位的最小倍数
                        this.sheelListData[index].average_price_compare = record.average_price_compare;
                    }
                    if (this.isSheel && (this.userSystemType === 2 || this.userSystemType === 3)) {
                        record.voucher_quantity = minUnitInfo * record.toBeOutOfStock_quantity;
                    }
                    this.addInfoDatas[index].min_unit_info = minUnitInfo;
                    this.addInfoDatas[index].average_price_compare = record.average_price_compare;

                    // 同步this.addInfoTempDatas的倍率关系
                    if (this.addInfoTempDatas && this.addInfoTempDatas.length) {
                        for (let i = 0; i < this.addInfoTempDatas.length; i++) {
                            let aItem = this.addInfoTempDatas[i];
                            if (
                                aItem.detail_inner_sn === record.detail_inner_sn &&
                                (aItem.product_vendor_inner_sn || aItem.vendor_inner_sn) ===
                                    (record.product_vendor_inner_sn || record.vendor_inner_sn) &&
                                aItem.specification_inner_sn === record.specification_inner_sn &&
                                aItem.unit_inner_sn[aItem.unit_inner_sn.length - 1] ===
                                    record.unit_inner_sn[record.unit_inner_sn.length - 1] &&
                                aItem.product_type === record.product_type &&
                                aItem.product_inner_sn === record.product_inner_sn
                            ) {
                                this.addInfoTempDatas[i].min_unit_info = minUnitInfo;
                                this.addInfoTempDatas[i].average_price_compare = record.average_price_compare;
                                break;
                            }
                        }
                    }
                }
            });
        },
        // 获取产品包装单位的最小倍数 【经销商】
        async dealerUnitMinAPIFn(record, index, type) {
            if (record.unit_inner_sn && record.unit_inner_sn.length <= 0) return;
            await dealerUnitMinAPI(
                record.product_vendor_inner_sn || record.vendor_inner_sn || record.dealer_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn[record.unit_inner_sn.length - 1]
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res,
                        minUnitInfo = parseFloat(info);
                    record.min_unit_info = minUnitInfo; // 产品包装单位的最小倍数
                    const averagePrice = record.average_price || 0;
                    record.average_price_compare = averagePrice * record.min_unit_info;
                    if (type !== 'add' && this.sheelListData.length) {
                        this.sheelListData[index].min_unit_info = minUnitInfo; // 产品包装单位的最小倍数
                        this.sheelListData[index].average_price_compare = record.average_price_compare;
                    }
                    if (this.isSheel && (this.userSystemType === 2 || this.userSystemType === 3)) {
                        record.voucher_quantity = minUnitInfo * record.toBeOutOfStock_quantity;
                    }
                    this.addInfoDatas[index].min_unit_info = minUnitInfo;
                    this.addInfoDatas[index].average_price_compare = record.average_price_compare;
                }
            });
        },
        /* 出库单区域主要API结束 */

        beyondInventoryModalFn(stringDatas) {
            this.beyondInventoryModal.title = `${
                this.isClickRedDashedOrder ? constants.redDashedOdoOrder : this.$route.meta.title
            } - 提示`;
            this.beyondInventoryModal.datas = stringDatas.split('\\n');
            this.beyondInventoryModal.alert = true;
            this.$Utils.globalDragCenterFn('beyond-inventory');
        },

        loadUnitMinFn(record, index, type) {
            parseInt(record.product_type) === 0
                ? this.dealerUnitMinAPIFn(record, index, type)
                : this.platformUnitMinAPIFn(record, index, type);
        },

        // 关闭弹窗并将滚动条重置回原来的默认位置
        closeAddInfoModalResetScrollFn() {
            this.addInfoModal.alert = false;
            // this.$refs.xTableAddInfo.clearScroll();
        },

        // 删除单据类别后，如果是新增按钮，检查外层的类别是否有改动，如果一致删除之
        removeTypeIsAddFn(typeInnerSn) {
            if (this.btnName === 'add' && parseFloat(typeInnerSn) === parseFloat(this.addInfoModalForm.type_inner_sn)) {
                this.addInfoModalForm.type_inner_sn = '';
                this.addInfoModalForm.type_name = '';
            }
        },
        // 顶部仓库的搜索过滤
        changewareHouseSearchFn(value) {
            this.searchTables.warehouse_model_inner_sn = typeof value !== 'undefined' ? parseFloat(value) : '';
        },
        // 里层模态框的仓库
        changeAddInfoModalWarehouseFn(value, label) {
            if (this.addInfoModalForm.warehouse_inner_sn_compare !== this.addInfoModalForm.warehouse_inner_sn) {
                // 如果切换的不是上一次的旧值 才需要重新请求数据
                this.warehouseOperatorList = []; // 每次仓库切换实时更新仓库人员
                this.addInfoModalForm.warehouse_operator_inner_sn = '';
                this.addInfoModalForm.warehouse_operator_name = '';
            }
            this.addInfoModalForm.warehouse_name = label.length ? label[label.length - 1] : '';
            // 新增规则 调单-生成出库单 当重新选择仓库后应把产品的批号、生产日期和失效日期清空
            if (!this.c_IsGrn) {
                if (this.isSheel) {
                    this.addInfoDatas.forEach((item) => {
                        item.batch_number = '';
                        item.produce_date = '';
                        item.overdue_date = '';
                        item.batchDatas = [];
                        item.batch_locked_quantity = 0;
                        item.max_batch_locked_quantity = 0;
                    });
                }

                // 仓库改变需要清空刚刚请求过的批号列表数据
                this.clearCurrentBatchNUmberFn();
            }

            this.warehouseNameFocus = false;
        },
        clearCurrentBatchNUmberFn() {
            this.batchRepeatKeyObj = {};
            // let record = this.$refs.xTableAddInfo.getCurrentRecord()
            // if (record) {
            //     const currentRowItemSplitKey = `${record.product_type}${record.vendor_inner_sn}${record.product_inner_sn}${record.specification_inner_sn}`,
            //         currentBatchRepeatKeyObj = this.batchRepeatKeyObj[currentRowItemSplitKey]
            //     if (currentBatchRepeatKeyObj) {
            //         currentBatchRepeatKeyObj.seq = -1
            //     }
            //     // currentBatchRepeatKeyObj.batch_data = []
            //     // this.addInfoDatas.forEach(item => {
            //     //     item.batch_number = ''
            //     //     item.produce_date = ''
            //     //     item.overdue_date = ''
            //     //     item.batchDatas = []
            //     //     item.inventoryBannce = 0
            //     //     item.quantity = 0
            //     //     item.batch_locked_quantity = 0
            //     //     item.max_batch_locked_quantity = 0
            //     // })
            // }
        },
        clickAddInfoModalWarehouseFn() {
            // BUG[3615] 引起原因可能因为之前的仓库已经不在当前仓库列表中，导致仓库变化
            //           在仓库置灰的情况下点击仓库，仍然会触发此方法
            //           目前方案为置灰仓库无法点击触发此方法。
            if (
                (this.btnName === 'edit' && !this.c_IsGrn) || // 编辑出库单
                this.isEditDisabledAddInfo || // 是否禁用仓库
                this.SheelHasWarehouse
            ) {
                // 调单是否有仓库
                return;
            }
            this.relevantInstitutionsFocus = false;
            this.warehouseNameFocus = true;
            this.addInfoModalForm.warehouse_inner_sn_compare = this.addInfoModalForm.warehouse_inner_sn || '';
            if (this.isWareHouseFilterLoading) {
                this.warehouseUsableTopListAPIFn();
            }
            this.handleResetMultipleFocus();
        },
        clickSuperiorDepartmentsFn() {
            this.orgDeptTopTreeAPIFn();
            this.handleResetMultipleFocus();
        },
        // 里层模态框的部门
        changeSuperiorDepartmentsFn(value, label) {
            this.addInfoModalForm.department_inner_sn = value;
            this.addInfoModalForm.department_name = label.length ? label[label.length - 1] : '';
        },
        // 里层模态框的类别
        changeGrnTypeListSnFn(value, label) {
            this.addInfoModalTitleFlag = true;
            this.addInfoModalForm.type_name = label.length ? label[label.length - 1] : '';
        },
        clickGrnTypeListSnFn() {
            if (this.isGrnTypeLoading) {
                if (this.c_IsGrn) {
                    this.grnTypeListAPIFn();
                } else {
                    this.odoTypeListAPIFn();
                }
            }
            this.handleResetMultipleFocus();
        },
        // 里层模态框的经手人
        changeHandlerNameSnFn(value, label) {
            [...document.getElementsByClassName('ant-select-search__field__mirror')].forEach((i) => {
                i.textContent = '&nbsp;';
            });
            this.addInfoModalForm.handler_name = label.length ? label[label.length - 1] : '';
        }, // 经手人
        clickHandlerNameSnFn() {
            if (this.handlerNameList.length <= 0) {
                this.orgEmpDropdownListAPIFn();
            }
            this.handleResetMultipleFocus();
        },
        // 里层模态框的仓库人员
        changeWarehouseOperatorNameSnFn(value, label) {
            this.addInfoModalForm.warehouse_operator_name = label.length ? label[label.length - 1] : '';
        },
        clickWarehouseOperatorFn() {
            if (!this.isIncludedInWarehouseList) {
                return;
            }
            if (
                this.warehouseOperatorList.length <= 0 ||
                (this.warehouseOperatorList.length && !this.warehouseOperatorList[0].isLoading)
            ) {
                if (this.c_IsGrn) {
                    this.grnWeListAPIFn();
                } else {
                    this.odoWeListAPIFn();
                }
            }
            this.handleResetMultipleFocus();
        },
        // 新增类别
        addGrnTypeFn() {
            if (this.c_IsGrn) {
                this.addInfoModalForm.type_name
                    ? this.grnTypeListAPIFn(false, this.addInfoModalForm.type_inner_sn)
                    : this.grnTypeListAPIFn();
            } else {
                this.addInfoModalForm.type_name
                    ? this.odoTypeListAPIFn(false, this.addInfoModalForm.type_inner_sn)
                    : this.odoTypeListAPIFn();
            }
            this.addGrnTypeModal.alert = true;
            this.$Utils.globalDragCenterFn('modal-grntype');
            this.resetFocusFn();
        },
        // 页码改变的回调函数
        changePaginationFn({ currentPage, pageSize }) {
            if (this.tabChoose === 2) {
                this.stoEntryPagination.current = currentPage;
                this.stoEntryPagination.pageSize = pageSize;
                this.stoEntryPagination.loading = true;
            } else {
                this.godownEntryPagination.current = currentPage;
                this.godownEntryPagination.pageSize = pageSize;
                this.godownEntryPagination.loading = true;
            }

            if (this.isSeparateBillOrder) {
                this.searchListAPIFn();
            } else {
                if (this.c_IsGrn) {
                    if (this.tabChoose === 2) {
                        this.stoListAPIFn();
                    } else {
                        this.grnListAPIFn();
                    }
                } else {
                    this.odoListAPIFn();
                }
            }
            this.editGrnTypeFlag = false;
        },
        // 设置行属性 —— 点击每一行的事件
        customRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentTrObj = record;
                    },
                    dblclick: () => {
                        this.currentTrObj = record;
                        this.groupBtnFn('edit');
                    }
                }
            };
        },
        // customRowBillLadingFn (record) {
        //     return {
        //         on: {
        //             click: () => {
        //                 this.getHospitalGrantDetailsListFn()
        //                 this.currentTrBillLadingObj = record
        //                 this.hospitalGrantDetailListAPIFn(record)
        //             },
        //             dblclick: () => {
        //                 this.getHospitalGrantDetailsListFn()
        //                 this.currentTrBillLadingObj = record
        //                 this.hospitalGrantDetailListAPIFn(record)
        //             }
        //         }
        //     }
        // },
        checkMethod({ row }) {
            return row.status <= 0 || row.status === 'AWAITING';
        },
        customRowBillLadingFn({ row }) {
            this.getHospitalGrantDetailsListFn();
            this.currentTrBillLadingObj = row;
            this.hospitalGrantDetailListAPIFn(row);
        },
        getHospitalGrantDetailsListFn() {
            this.billLadingBotDatas = [];
            this.billLadingBotSelectedRowKeys = [];
            this.currentTrBillLadingBotObj = {};
        },
        customRowBillLadingBotFn({ record }) {
            this.currentTrBillLadingBotObj = record;
        },
        // customRowBillLadingBotFn (record) {
        //     return {
        //         on: {
        //             click: () => {
        //                 this.currentTrBillLadingBotObj = record
        //             },
        //             dblclick: () => {
        //                 this.currentTrBillLadingBotObj = record
        //             }
        //         }
        //     }
        // },
        // 表格的高亮样式显示
        rowClassFn(record) {
            if (record.key === this.currentTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        rowClassBillLadingFn(record) {
            if (record.key === this.currentTrBillLadingObj.key) {
                return 'active-tr';
            }
            return '';
        },
        rowClassBillLadingBotFn(record) {
            if (record.key === this.currentTrBillLadingBotObj.key) {
                return 'active-tr';
            }
            return '';
        },
        // cellTrAddInfoClassName ({ row }) {
        //     if (row === this.currentTrAddInfoObj) {
        //         return 'active-tr'
        //     }
        // },
        rowClassStorageAllocatFn(record) {
            if (record.index === this.currentTrStorageAllocationObj.index) {
                return 'active-tr';
            }
            return '';
        },
        cellAddInfoClickFn({ row }) {
            this.resetFocusFn();
            this.currentTrAddInfoObj = row;
            // this.resetFocusFn(column.property, rowIndex)
        },
        cellAddInfoDblclickFn({ row }, e) {
            this.resetFocusFn();
            this.currentTrAddInfoObj = row;
            if (this.isEditDisabledAddInfo) return;
            this.dblclickTableFn(e);
            // this.resetFocusFn(column.property, rowIndex)
        },
        cellClickStorageAllocatFn({ row }) {
            this.currentTrStorageAllocationObj = row;
        },
        cellDblclickStorageAllocatFn({ row }) {
            this.currentTrStorageAllocationObj = row;
            this.tableHeadFn();
        },
        rowClassGrnTypeAddFn(record) {
            if (record.key === this.currentGrnTypeListTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        cellClickAddTypeFn({ row }) {
            this.currentGrnTypeListTrObj = row;
        },
        cellDblclickAddTypeFn({ row }) {
            this.currentGrnTypeListTrObj = row;
            this.$Utils.globalDragCenterFn('modal-grntype-list');
            this.grnTypeAddBtnFn('edit');
        },
        // 表格的tab选项卡切换状态
        changeTabsFn() {
            if (this.vendorDatas.length) {
                this.currentTrObj =
                    Object.keys(this.recordCurrentTrObj.vendors).length > 0
                        ? this.recordCurrentTrObj.vendors
                        : this.vendorDatas[0];
            } else {
                this.currentTrObj = {};
            }
        },
        resetAddInfoModalFocusFn() {
            this.relevantInstitutionsFocus = false;
            this.warehouseNameFocus = false;
        },
        // 检测修改的表单信息校验
        checkEditInfoFormMsgFn() {
            const curObj =
                this.tabChoose === 1 ? this.$refs.xTable.getCurrentRow() : this.$refs.xTableSto.getCurrentRow();
            if (this.tabChoose === 1) {
                this.currentTrObj = curObj || {};
            } else {
                this.currentTrObjSto = curObj || {};
            }
            if (!curObj) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (
                (this.btnName === 'remove' || this.btnName === 'grnEntry') &&
                ((this.btnName === 'grnEntry' && parseFloat(curObj.status) === 0) ||
                    parseFloat(curObj.status) === 2 ||
                    parseFloat(curObj.status) === 3)
            ) {
                this.$warning({
                    title: '操作提示',
                    content:
                        this.tabChoose === 2
                            ? `不能对【${this.stoStatus[this.currentTrObjSto.status]}】状态的订单进行操作`
                            : `不能对【${this.grnStatus[curObj.status]}】状态的${this.setOutPutTextComputed}单进行操作`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 校验单位名称是否有值
        checkOrgNameMsgFn() {
            if (!this.addInfoModalForm.organization_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().selectOrgName}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (
                this.addInfoModalForm.organization_inner_sn === this.addInfoModalForm.organization_inner_sn_old &&
                this.addInfoModalForm.organization_name !== this.addInfoModalForm.organization_name_old
            ) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().selectOrgNameIllegal}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 检测内层弹框中的修改表单信息校验
        checkAlertEditInfoFormMsgFn(cBtnName) {
            if (!this.checkOrgNameMsgFn()) return;
            if (!this.addInfoModalForm.warehouse_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择仓库',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            // const isDepartmentInnerSn = this.userSystemType === 1 && !this.addInfoModalForm.department_inner_sn
            // if (isDepartmentInnerSn && this.addInfoModalForm.billsType === this.billsTypeObj[3]) {
            const departmentInnerSn = this.addInfoModalForm.department_inner_sn;
            if (!departmentInnerSn && departmentInnerSn !== 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择部门',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            if (!this.isSeparateBillOrder && !this.addInfoModalForm.type_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择单据类别',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            if (!this.addInfoModalForm.handler_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择经手人',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            let data = this.getStableData();
            let tempDatas = data.filter((item) => item.product_inner_sn);
            if (tempDatas.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.isClickRedDashedOrder
                        ? `请关闭弹窗重新生成${constants.redDashedOdoOrder}`
                        : '请双击表格中的空白部分添加产品。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            const grnString = '请选择或输入【数量/包装单位/含税单价/类别】'; // 入库单不需要批号
            const odoString = '请选择或输入【批号/数量/包装单位/含税单价/类别】';
            const giveOdoString = '请选择或输入【批号/数量/包装单位】';
            const selectedStr = this.c_IsGrn ? grnString : this.isGiveOdo ? giveOdoString : odoString;
            const obj = this.checkAddProductFn(tempDatas);
            const finalStr = obj.warnStr.length === 0 ? selectedStr : obj.warnStr;
            if (obj.flag) {
                this.$warning({
                    title: '操作提示',
                    content: finalStr,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            const dateModalFlag = tempDatas.every((item) => {
                if (!item.produce_date || !item.overdue_date) return true;
                return (
                    moment(item.overdue_date).format(this.format).replace(/-/g, '') -
                        moment(item.produce_date).format(this.format).replace(/-/g, '') >
                    0
                );
            });

            if (!dateModalFlag) {
                this.$warning({
                    title: '操作提示',
                    content: '失效日期不能小于等于生产日期',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            let repeatProductsObj = {},
                repeatProductBatchObj = {},
                flag = true;
            for (let i = 0; i < tempDatas.length; i++) {
                // 检测包装单位必须为一样的
                let item = tempDatas[i],
                    key = `${item.product_type}==${item.product_inner_sn}==${item.specification_inner_sn}==${
                        item.product_vendor_inner_sn || item.vendor_inner_sn || item.dealer_inner_sn
                    }`,
                    unitInnerSn =
                        item.unit_inner_sn && item.unit_inner_sn.length
                            ? item.unit_inner_sn[item.unit_inner_sn.length - 1]
                            : '',
                    batchNumber = item.batch_number || '',
                    keyBatchNumber = `${key}${batchNumber}`;

                if (!repeatProductsObj[key]) {
                    repeatProductsObj[key] = {
                        unitInnerSn,
                        index: `NO.${i + 1}`
                    };
                } else {
                    repeatProductsObj[key].index += `，NO.${i + 1}`;
                    if (repeatProductsObj[key].unitInnerSn !== unitInnerSn) {
                        this.$warning({
                            title: '操作提示',
                            content: `请将【${repeatProductsObj[key].index} - ${item.product_name}】包装单位修改为一样的。`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                }
                // 包装单位一样，批号不能相同
                if (!repeatProductBatchObj[keyBatchNumber]) {
                    repeatProductBatchObj[keyBatchNumber] = {
                        batchNumber,
                        index: `NO.${i + 1}`
                    };
                } else {
                    repeatProductBatchObj[keyBatchNumber].index += `，NO.${i + 1}`;
                    if (repeatProductBatchObj[keyBatchNumber].batchNumber === batchNumber) {
                        this.$warning({
                            title: '操作提示',
                            content: `【${repeatProductBatchObj[keyBatchNumber].index} - ${item.product_name}】批号不能出现重复。`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                }
            }
            repeatProductsObj = null;
            repeatProductBatchObj = null;
            if (!flag) return flag;
            // let isClon = false
            // let isClonWarn = ''
            let addInfoDatasClone = []; // 展示数据的去重
            tempDatas.forEach((item, index) => {
                if (!item.batch_number) {
                    item.batch_number = '';
                }
                item.kkquantity = item.quantity;
                if (index === 0) {
                    addInfoDatasClone.push(item);
                } else {
                    let isf = true;
                    addInfoDatasClone.forEach((itemS) => {
                        if (!itemS.batch_number) {
                            itemS.batch_number = '';
                        }
                        if (
                            item.product_type === itemS.product_type &&
                            item.product_inner_sn === itemS.product_inner_sn &&
                            item.vendor_inner_sn === itemS.vendor_inner_sn &&
                            item.specification === itemS.specification &&
                            item.unit === itemS.unit &&
                            item.batch_number === itemS.batch_number
                        ) {
                            itemS.kkquantity += item.quantity;
                            itemS.kkquantity = this.reNumber(itemS.kkquantity);
                            isf = false;
                        }
                    });
                    if (isf) {
                        addInfoDatasClone.push(item);
                    }
                }
            });

            // let isCompare = false
            // if (!this.c_IsGrn && !this.isSheel) {
            //     this.sheelListDataSame = JSON.stringify(this.addInfoDatas)
            //     isCompare = true
            // }

            // if (this.isSheel || isCompare) {
            //     let repeatObj = {},
            //         batchNumberReatObj = {}
            //     for (let i = 0; i < addInfoDatasClone.length; i++) {
            //         let item = addInfoDatasClone[i]
            //         for (let j = 0; j < this.sheelListDataSame.length; j++) {
            //             let cItem = this.sheelListDataSame[j]

            //             const curQuantity = item.quantity * item.min_unit_info
            //             let itemS = this.sheelListDataSame[j],
            //                 minUnitInfoQuantity = item.kkquantity * item.min_unit_info,
            //                 itemFlag = item.product_type === itemS.product_type && item.product_inner_sn === itemS.product_inner_sn && item.vendor_inner_sn === itemS.vendor_inner_sn && item.specification === itemS.specification,
            //                 key = `${item.sl_inner_sn || item.gv_inner_sn || item.voucher_inner_sn_c}${item.detail_inner_sn}${item.batch_number}`,
            //                 key2 = `${item.sl_inner_sn || item.gv_inner_sn || item.voucher_inner_sn_c}${item.detail_inner_sn}`
            //             if (repeatObj[key]) { // 统计产品名称 + 规格型号 + 厂商/经销商相同，批号相同
            //                 repeatObj[key] += minUnitInfoQuantity
            //             } else {
            //                 repeatObj[key] = minUnitInfoQuantity
            //             }
            //             if (batchNumberReatObj[key2]) { // 统计产品名称 + 规格型号 + 厂商/经销商相同，批号不同
            //                 batchNumberReatObj[key2] += item.quantity
            //             } else {
            //                 batchNumberReatObj[key2] = item.quantity
            //             }
            //             if (itemFlag && parseFloat(item.min_packing_unit_inventory) < curQuantity) {
            //                 isClon = true
            //                 isClonWarn = `【${item.product_name}】【${item.specification}】【${item.vendor_name}】的最小包装单位库存数量剩余${item.min_packing_unit_inventory}个，但此产品需要${curQuantity}个!`
            //                 break
            //             }

            //             if (itemFlag && parseFloat(repeatObj[key]) > parseFloat(item.min_packing_unit_inventory)) {
            //                 isClon = true
            //                 isClonWarn = `【${item.product_name}】【${item.specification}】【${item.vendor_name}】的数量加起来不能大于库存量${item.min_packing_unit_inventory}，当前转化为最小包装单位的数量为${this.toFixedReapeatFn(repeatObj[key])}`
            //                 break
            //             }

            //             if (itemFlag && parseFloat(item.kkquantity) > parseFloat(item.toBeOutOfStock_quantity)) {
            //                 isClon = true
            //                 isClonWarn = `【${item.product_name}】【${item.specification}】【${item.vendor_name}】的数量${item.kkquantity}不能大于待出库数量${item.toBeOutOfStock_quantity}`
            //                 break
            //             }

            //             if (itemFlag && parseFloat(batchNumberReatObj[key2]) > parseFloat(item.toBeOutOfStock_quantity)) {
            //                 isClon = true
            //                 isClonWarn = `【${item.product_name}】【${item.specification}】【${item.vendor_name}】相同产品累加的数量${this.toFixedReapeatFn(batchNumberReatObj[key2])}不能大于待出库数量${item.toBeOutOfStock_quantity}`
            //                 break
            //             }
            //         }
            //     }
            // }

            // if (isClon && !this.c_IsGrn) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: isClonWarn,
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     return false
            // }

            if (
                this.btnName === 'edit' &&
                !(cBtnName === 'audit' || cBtnName === 'godownEntry') &&
                this.checkEditInfoFormObjFn()
            ) {
                this.addInfoModalBotFn('close');
                this.resetAddInfoModalFn();
                return false;
            }

            return true;
        },
        // 检查明细条件
        checkAddProductFn(tempDatas) {
            let alertModalFlag = false;
            let warnStr = '';
            for (let i = 0; i < tempDatas.length; i++) {
                let item = tempDatas[i];
                item.tax_unit_price = parseFloat(item.unit_price_text);
                item.quantity = 0 - item.quantity;

                if (!item.unit_inner_sn || item.unit_inner_sn.length <= 0) {
                    alertModalFlag = true;
                    break;
                }

                /// 赠送单不做价格校验
                if (!this.isSeparateBillOrder || !this.isGiveOdo) {
                    if (typeof item.tax_unit_price !== 'number' || item.tax_unit_price < 0) {
                        alertModalFlag = true;
                        break;
                    }
                }

                // 销售出库单需要校验批号选择，批号选择的依据为：是否填充了buying_price字段值
                if (this.orderTypeData.value === grnAndOdoOrderType['SALEODO'].value) {
                    if (!item.buying_price && Number(item.buying_price) !== 0) {
                        alertModalFlag = true;
                        warnStr = '请手动选择批号';
                        break;
                    }
                }

                if (
                    this.orderTypeData.value === grnAndOdoOrderType['PURCHASERETURN'].value ||
                    this.orderTypeData.value === grnAndOdoOrderType['SALERETURN'].value
                ) {
                    if (item.quantity >= 0) {
                        alertModalFlag = true;
                        warnStr = '退货单产品数量必须为负数';
                        break;
                    }
                } else {
                    if (item.quantity < 0) {
                        warnStr = '产品数量不得为负数';
                        alertModalFlag = true;
                        break;
                    }
                    if (item.quantity === 0) {
                        warnStr = '产品数量必须大于0';
                        alertModalFlag = true;
                        break;
                    }
                }

                if (!item.quantity) {
                    alertModalFlag = true;
                    break;
                }

                if (!item.type) {
                    alertModalFlag = true;
                    break;
                }
            }
            return {
                flag: alertModalFlag,
                warnStr: warnStr
            };
        },
        toFixedReapeatFn(val = 1) {
            let newVal = parseFloat(val);
            if (newVal % 1 === 0) {
                return newVal;
            }
            return newVal.toFixed(2);
        },
        checkGodownEntryQuantityFn() {
            let flag = false,
                checkObj = {};
            for (let i = 0; i < this.addInfoDatas.length; i++) {
                let item = this.addInfoDatas[i],
                    key =
                        item.product_type +
                        '' +
                        item.product_inner_sn +
                        item.product_vendor_inner_sn +
                        item.specification_inner_sn +
                        item.detail_inner_sn,
                    inputQuantity = parseFloat(item.quantity), // 界面上输入的数量
                    needCompareQuantity = parseFloat(item.arrival_quantity); // 需要比对的数量
                if (this.sheelID === 'ALLOT') {
                    needCompareQuantity = this.c_IsGrn
                        ? parseFloat(item.toBeInOfStock_quantity)
                        : parseFloat(item.toBeOutOfStock_quantity);
                }
                if (!item.product_inner_sn) continue;
                if (inputQuantity > needCompareQuantity) {
                    this.$warning({
                        title: '操作提示',
                        content: `${item.product_name}的数量${inputQuantity}不能大于当前调单数量${needCompareQuantity}`,
                        okText: '知道了',
                        centered: true,
                        mask: true
                    });
                    flag = true;
                    break;
                }
                if (!checkObj[key]) {
                    checkObj[key] = inputQuantity;
                } else {
                    let inputTotalQuantity = checkObj[key] + inputQuantity;
                    if (inputTotalQuantity > needCompareQuantity) {
                        this.$warning({
                            title: '操作提示',
                            content: `${item.product_name}相同产品的相加数量${inputTotalQuantity}不能大于当前调单数量${needCompareQuantity}`,
                            okText: '知道了',
                            centered: true,
                            mask: true
                        });
                        flag = true;
                        break;
                    }
                    checkObj[key] = inputTotalQuantity;
                }
            }
            return flag;
        },
        // 检测并设置修改过的修改信息内容字段
        checkEditInfoFormObjFn() {
            let editInfoFlag = true;
            this.editInfoFormNewObj = {};
            for (let server in this.addInfoModalForm) {
                let serverItem = this.addInfoModalForm[server];
                for (let old in this.editInfoFormObj) {
                    let oldItem = this.editInfoFormObj[old];
                    if (server + '_old' === old && serverItem !== oldItem) {
                        this.editInfoFormNewObj = {
                            ...this.editInfoFormNewObj,
                            [server]: serverItem, // 新值
                            [server + '_old']: oldItem // 旧值
                        };
                        editInfoFlag = false;
                    }
                }
            }

            if (editInfoFlag) {
                // 产品明细是否修改过的判断
                const pList = this.addInfoModalForm.details,
                    cList = this.addInfoDatas;
                if (pList.length !== cList.length) {
                    editInfoFlag = false;
                } else {
                    for (let i = 0; i < pList.length; i++) {
                        const item = pList[i];
                        for (let j = 0; j < cList.length; j++) {
                            const cItem = cList[j];
                            if (i === j) {
                                const itemVal =
                                        item.batch_number +
                                        '' +
                                        item.quantity +
                                        item.unit_inner_sn +
                                        item.unit_price +
                                        item.produce_date +
                                        item.overdue_date +
                                        item.mdrf_inner_sn +
                                        item.type,
                                    cItemVal =
                                        cItem.batch_number +
                                        '' +
                                        cItem.quantity +
                                        (cItem.unit_inner_sn &&
                                        typeof cItem.unit_inner_sn === 'object' &&
                                        cItem.unit_inner_sn.length
                                            ? cItem.unit_inner_sn[0]
                                            : cItem.unit_inner_sn) +
                                        cItem.unit_price +
                                        moment(cItem.produce_date).format(this.format) +
                                        moment(cItem.overdue_date).format(this.format) +
                                        cItem.mdrf_inner_sn +
                                        cItem.type;
                                if (itemVal !== cItemVal) {
                                    editInfoFlag = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return editInfoFlag;
        },
        relevantInstitutionsKeyDownFn(e) {
            // if (this.relevantInstitutionsFocus) {
            //     if (this.isChooseOrgMdTip) return // 单位名称提示框出现【当前选择和已选择单位名称不一致】的情况，用户选择是否替换的时候，频闭掉回车键的操作
            //     this.isParentKeyDownEnter = true
            //     this.openChooseTb(e.target.value)
            //     e.stopPropagation()
            // }
            if (this.isChooseOrgMdTip || !this.relevantInstitutionsFocus) return; // 单位名称提示框出现【当前选择和已选择单位名称不一致】的情况，用户选择是否替换的时候，频闭掉回车键的操作
            this.isParentKeyDownEnter = true;
            this.openChooseTb(e.target.value);
            e.stopPropagation();
        },
        changeGrnOrg(value) {
            if (value === 'search-more') {
                this.openChooseTb(this.supplySearch);
                return;
            } else if (value === 'tips') {
                return;
            } else {
                let obj = Object.assign(
                    {},
                    this.grnOrgData.find((v) => v.key === value)
                );
                this.addInfoModalForm.organization_name = obj.name;
                this.chooseOrgFn(obj);
            }
        },
        searchGrnOrgList(value) {
            this.addInfoModalForm.organization_name = value;
            this.grnOrgData = [];
            this.grnOrgListAPIFn(1, true);
            this.grnOrgListAPIFn(3, true);
            this.grnOrgListAPIFn(2, true);
        },
        // 【相关机构列表】 【仓库管理-入库单】
        async grnOrgListAPIFn(type, isInitial) {
            let params = {};
            params.pageSize = 5;
            params.pageNum = 1;
            if (this.addInfoModalForm.organization_name && this.addInfoModalForm.organization_name.length) {
                params.name = this.addInfoModalForm.organization_name;
            }
            if (this.isCertifie) {
                params.is_certifie = true;
            }
            if (
                this.pageTypeName === 0 ||
                this.pageTypeName === 3 ||
                this.pageTypeName === 7 ||
                this.pageTypeName === 8
            ) {
                // 入库单相关机构
                params.all = this.isContractOrgChecked;
            }
            await grnOrgListAPI(type, this.$cookie.get('userbelong'), { params: params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let TempData = res.rows.map((item) => {
                            item.key = (Math.random() + 1).toString(36).substring(7);
                            return item;
                        });
                        this.grnOrgData = this.grnOrgData.concat(TempData);
                    }
                });
        },
        openChooseTb() {
            // 打开单位名称
            if (this.c_IsGrn) {
                // 单位名称页面的标识： 0-【仓库管理-入库单】  2【仓库管理-出库单】
                this.pageTypeName = 0;
            } else {
                this.pageTypeName = 2;
            }

            this.relevantInstitutionsFocus = true;
            this.searchOrgName = this.addInfoModalForm.organization_name || '';

            this.resetFocusFn();
            this.isParentKeyDownEnter = true;
            this.chooseOrgMd.alert = true;
            this.$Utils.globalDragCenterFn('choose_org_md');
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.setContractOrgCheckedFn();
                this.$refs.chooseOrgMd.pageDataInit();
            }
        },
        chooseOrgFn(resObj) {
            // 获取选中单位名称信息
            // 在新增和复制时，采购退货和销售退货单的机构改变时，就清空产品明细，其它单据只要机构类型一样就不清空产品明细，不一样才清空产品明细
            let isBillsTypeSame = resObj.billsType === this.addInfoModalForm.billsType,
                isClearReturnOrderProducts = this.isSeparateBillOrder
                    ? this.isTransferRecord && isBillsTypeSame
                    : isBillsTypeSame;
            if (!this.addInfoModalForm.organization_inner_sn || isClearReturnOrderProducts) {
                this.setOrganizationNameFn(resObj);
            } else {
                if (
                    resObj.org_id !== this.addInfoModalForm.organization_inner_sn ||
                    resObj.name !== this.addInfoModalForm.organization_name
                ) {
                    const _this = this;
                    _this.isChooseOrgMdTip = true;

                    _this.$confirm({
                        title: '操作提示',
                        content: `当前选择和已选择${
                            _this.isSeparateBillOrder && !_this.isTransferRecord ? '单位名称' : '机构类型'
                        }不一致，如确认要改变已选择单位名称，已选择单位名称的所有信息会被清空。`,
                        okText: '确认',
                        cancelText: '取消',
                        centered: true,
                        maskClosable: false,
                        mask: false,
                        onOk: () => {
                            _this.isChooseOrgMdTip = false;
                            this.addInfoModalForm = {
                                department_inner_sn: '',
                                department_name: '',
                                description: '',
                                details: '',
                                grn_sn: '',
                                odo_sn: '',
                                odo_type: '',
                                handler_inner_sn: '',
                                handler_name: '',
                                org_id: this.cookieInfo.get('userbelong'),
                                organization_inner_sn: resObj.org_id,
                                organization_name: resObj.name,
                                status: 0,
                                type_inner_sn: '',
                                type_name: '',
                                warehouse_inner_sn: '',
                                warehouse_name: '',
                                warehouse_operator_inner_sn: '',
                                warehouse_operator_name: '',
                                billsType: resObj.billsType
                            };
                            _this.isBatchLadingFlag = false;
                            _this.setOrganizationNameFn(resObj);
                            if (!_this.c_IsGrn) {
                                // 仓库改变需要清空刚刚请求过的批号列表数据
                                _this.clearCurrentBatchNUmberFn();
                            }
                            _this.addInfoDatas = [];
                            _this.addData = _this.clearAddData();
                            _this.loadVenData();
                        },
                        onCancel: () => {
                            _this.isChooseOrgMdTip = false;
                            _this.chooseOrgMd.alert = false;
                        }
                    });
                } else {
                    this.chooseOrgMd.alert = false;
                }
            }
        },
        setOrganizationNameFn(resObj) {
            this.addInfoModalForm.organization_inner_sn = resObj.org_id;
            this.addInfoModalForm.organization_inner_sn_old = resObj.org_id;
            this.addInfoModalForm.organization_name = resObj.name;
            this.addInfoModalForm.organization_name_old = resObj.name;
            if (this.c_IsGrn) {
                this.addInfoModalForm.billsType = resObj.billsType;
                this.partB.otype = resObj.billsType;
            } else {
                this.addInfoModalForm.billsType = resObj.billsType;
                this.addInfoModalForm.odo_type = resObj.billsType;
                this.partB.otype = resObj.billsType;
            }
            this.partB.oname = resObj.name;
            this.partB.oid = resObj.org_id;
            this.chooseOrgMd.alert = false;
        },
        // 创建临时的产品明细
        insertTempProductDetailFn() {
            const xTableAddInfo = this.$refs.xTableAddInfo;

            xTableAddInfo.focus();
            let tempProduct = {
                // 前面的数字key依次对应着要聚焦的输入框，后面的value对应着当前要聚焦的那个输入框
                'focusDatas': [true, false, false, false, false, false, false, false, false, false, false],
                'common_use_unit': '',
                'min_unit_info': 1,
                'vendor_inner_sn': '',
                'specification_inner_sn': '',
                'product_type': '',
                'specification_vendor_sn': '',
                'vendor_name': '',
                'specification': '',
                'id': '',
                'product_inner_sn': '',
                'product_name': '',
                'mdrf_sn': '',
                'common_use_unit_inner_sn': '',
                'activeClass': '',
                'key': '',
                'keyFormat': '',
                'partB_name': '',
                'status': 0,
                'quantity_delivered': '',
                'unit_price_text': 0,
                'money': 0,
                'discount': 0,
                'discount_unit_price': 0,
                'discount_money': 0,
                'rate': 0,
                'tax_unit_price': 0,
                'tax_amount': 0,
                'tax': 0,
                'sys_type': '',
                'unit_inner_sn': [],
                'unit_name': '',
                'unitList': [],
                'quantity': 1,
                'isLoadingUnit': false,
                'row_add_flag': true,
                'temp_add_flag': true,
                'batch_number': '',
                'unitPriceList': []
            };
            xTableAddInfo.clearCurrentRow();
            let spliceIndex = this.addInfoDatas.findIndex((item) => !item.product_inner_sn);
            if (spliceIndex > -1) {
                return;
            }
            // xTableAddInfo.insertAt(tempProduct, -1)
            this.refreshTempInsertAddProductFn([tempProduct]);
            this.relevantInstitutionsFocus = false;
            // const tableData = xTableAddInfo.getTableData().tableData,
            //     curData = tableData[tableData.length - 1]
            // this.addInfoDatas = tableData
            // xTableAddInfo.setCurrentRow(curData)
            // this.scrollTableArrowDownFn()
        },
        // 表格详情滚动到底部
        scrollTableArrowDownFn() {
            const xTableAddInfo = this.$refs.xTableAddInfo;
            // rowHeight = 38
            // xTableAddInfo.refreshScroll()
            xTableAddInfo.clearScroll().then(() => {
                xTableAddInfo.scrollToColumn(xTableAddInfo.getColumns(0));
                xTableAddInfo.scrollToRow(xTableAddInfo.getData(this.addInfoDatas.length - 1));
                // xTableAddInfo.scrollTo(0, this.addInfoDatas.length * rowHeight)
            });
        },
        // 表格的回车事件
        enterKeydownFn() {
            const xTableAddInfo = this.$refs.xTableAddInfo,
                tableData = xTableAddInfo.getTableData().tableData,
                existProductEmptyIndex = tableData.findIndex((item) => !item.product_inner_sn);

            xTableAddInfo.clearCurrentRow();
            xTableAddInfo.focus();
            if (existProductEmptyIndex <= -1) {
                // 需要创建空数据
                this.resetFocusFn();
                this.insertTempProductDetailFn();
            } else {
                // 说明有需要添加的空数据，直接调出产品添加页面
                if (!this.addProductMd.alert) {
                    this.partB['isKeyCode'] = true;
                    this.partB['keyCodeVal'] = tableData[existProductEmptyIndex].product_name || '';
                    this.dblclickTableFn();
                    setTimeout(() => {
                        this.addProductEnter = true;
                    }, 800);
                } else {
                    if (this.addProductMd.alert && this.addProductEnter) {
                        // this.addProductFn()
                    }
                }
            }
        },
        resetAddProductDetailFn(record) {
            const xTableAddInfo = this.$refs.xTableAddInfo;
            xTableAddInfo.focus();
            xTableAddInfo.clearCurrentRow();
            xTableAddInfo.setCurrentRow(record);
            let seq = xTableAddInfo.getRowIndex(record);
            if (seq > 0) {
                seq -= 1;
            } else {
                seq = 0;
            }
            // this.resetFocusFn('batch_number', seq)
        },
        addProductDetailFn(e) {
            if (this.isChooseOrgMdTip) return; // 单位名称提示框出现【当前选择和已选择单位名称不一致】的情况，用户选择是否替换的时候，频闭掉回车键的操作

            const keyCode = e.keyCode;

            // Esc 键
            if (keyCode === 27) {
                // Esc
                this.addProductZoomFn();
                return;
            }

            if (this.addInfoModalForm.status > 0) return; // 草稿和正式状态才能编辑

            const activeElement = document.activeElement,
                isOrgFocus =
                    this.$refs['relevantInstitutions'] && this.$refs['relevantInstitutions'].$el === activeElement;

            if (keyCode === 13) {
                // 回车键
                // 单位名称是否获取到了焦点
                if (isOrgFocus || this.relevantInstitutionsFocus) {
                    return;
                }
                // 仓库是否获取到了焦点
                // if (isWarehouseFocus) {
                //     this.relevantInstitutionsFocus = false
                //     this.isParentKeyDownEnter = false
                //     this.clickAddInfoModalWarehouseFn()
                //     return
                // }

                const xTableAddInfo = this.$refs.xTableAddInfo;

                if (!xTableAddInfo) return;

                const tableData = xTableAddInfo.getTableData().tableData,
                    existProductEmptyIndex = tableData.findIndex((item) => !item.product_inner_sn);

                // 其他情况添加产品
                this.$refs['warehouseFocus'].blur();
                this.isParentKeyDownEnter = false;
                this.warehouseNameFocus = false;
                // xTableAddInfo.clearCurrentRow()
                xTableAddInfo.focus();
                if (existProductEmptyIndex <= -1) {
                    // 需要创建空数据
                    this.resetFocusFn();
                    this.insertTempProductDetailFn();
                } else {
                    // 说明有需要添加的空数据，直接调出产品添加页面
                    if (!this.addProductMd.alert) {
                        if (this.gotdownInfoModal.alert || this.sheelInfoModal.alert) return;
                        this.partB['isKeyCode'] = true;
                        this.partB['keyCodeVal'] = tableData[existProductEmptyIndex].product_name || '';
                        this.dblclickTableFn();
                        setTimeout(() => {
                            this.addProductEnter = true;
                        }, 800);
                    } else {
                        // if (this.sheelInfoModal.alert) return
                        this.addProductFn();
                    }
                }
            } else {
                // 正在编辑[单位名称、仓库、批号选择]
                // if ((isOrgFocus && !isOrgValEmpty) || (this.warehouseNameFocus && !isWarehouseValEmpty) || this.chooseOrgMd.alert || this.batchInfoModal.alert) return
                // // 切换产品的焦点
                // if (this.addProductMd.alert) return
                // this.relevantInstitutionsFocus = false
                // this.warehouseNameFocus = false
                // this.$refs.xTableAddInfo.focus()
                // switch (keyCode) {
                // case 38: // ↑
                //     this.toogleVerticalFocusAndActiveRowFn(keyCode)
                //     break
                // case 40: // ↓
                //     this.toogleVerticalFocusAndActiveRowFn(keyCode)
                //     break
                // case 37: // ←
                //     this.toogleHorizontalFocusRowFn(keyCode)
                //     break
                // case 39: // →
                //     this.toogleHorizontalFocusRowFn(keyCode)
                //     break
                // }
            }
        },
        addEventProductDocumentFn() {
            document.addEventListener('keydown', this.addProductDetailFn, false);
        },
        removeEventProductDocumentFn() {
            document.removeEventListener('keydown', this.addProductDetailFn, false);
        },
        resetFormFocusFn() {
            this.relevantInstitutionsFocus = false;
        },
        handleResetMultipleFocus() {
            this.resetFocusFn();
            this.resetFormFocusFn();
        },
        // 所有光标重置
        resetFocusFn(property, rowIndex) {
            const xTableAddInfo = this.$refs.xTableAddInfo;
            for (let i = 0; i < this.addInfoDatas.length; i++) {
                let item = this.addInfoDatas[i];
                if (rowIndex === i) {
                    item.focusDatas = [
                        property === 'product_name',
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false
                    ];
                    // item.focusDatas = [property === 'product_name', property === 'batch_number', property === 'quantity', property === 'unit', property === 'unit_price_text', property === 'rate', property === 'produce_date', property === 'overdue_date', property === 'type', property === 'mdrf_inner_sn', property === 'health_care_sn']
                } else {
                    item.focusDatas = [false, false, false, false, false, false, false, false, false, false, false];
                }
            }
            this.$nextTick(() => {
                if (xTableAddInfo) {
                    xTableAddInfo.updateData();
                }
            });
        },
        // 水平方向切换焦点事件
        toogleHorizontalFocusRowFn(keyCode) {
            const xTableAddInfo = this.$refs.xTableAddInfo,
                currowData = xTableAddInfo.getCurrentRow(),
                focusDatas = currowData.focusDatas,
                rowHeight = 38;

            let currowIndex = xTableAddInfo.getRowIndex(currowData);

            if (!currowData.product_inner_sn) return;
            let curFocusIndex = focusDatas.findIndex((item) => !!item),
                isBoundary = false; // 是否是边界值，默认不是边界

            if (keyCode === 39) {
                if (curFocusIndex + 1 <= focusDatas.length - 1) {
                    curFocusIndex += 1;
                } else {
                    isBoundary = true;
                }
            } else {
                if (curFocusIndex - 1 >= 1) {
                    curFocusIndex -= 1;
                } else {
                    isBoundary = true;
                }
            }

            if (isBoundary) return;

            for (let f = 0; f < focusDatas.length; f++) {
                if (f === curFocusIndex) {
                    focusDatas[f] = true;
                } else {
                    focusDatas[f] = false;
                }
            }
            xTableAddInfo.reloadRow(
                {
                    ...currowData,
                    focusDatas
                },
                null,
                'focusDatas'
            );

            xTableAddInfo.syncData();

            this.addInfoDatas = xTableAddInfo.getTableData().tableData;

            currowIndex = curFocusIndex === 0 ? currowIndex : currowIndex + 1;
            // xTableAddInfo.clearScroll().then(() => {
            //     xTableAddInfo.scrollToColumn(xTableAddInfo.getColumns(currowIndex <= 2 ? 0 : 12))
            //     xTableAddInfo.scrollToRow(xTableAddInfo.getData(currowIndex))
            // })
            // 产品、 数量处理 [滚动条滚动到最左侧]
            if (curFocusIndex <= 3 && focusDatas[curFocusIndex]) {
                xTableAddInfo.clearScroll().then(() => {
                    // xTableAddInfo.scrollTo(0, currowIndex * rowHeight)
                    // xTableAddInfo.scrollToColumn(xTableAddInfo.getColumns(0))
                    // xTableAddInfo.scrollToRow(xTableAddInfo.getData(currowIndex))
                });
            }
            // 税率处理 [滚动条滚动到最右侧]
            if (curFocusIndex === 5 && focusDatas[curFocusIndex]) {
                xTableAddInfo.clearScroll().then(() => {
                    // xTableAddInfo.scrollToColumn(xTableAddInfo.getColumns(14))

                    // xTableAddInfo.scrollToColumn(xTableAddInfo.getColumns(currowIndex <= 4 ? currowIndex + 4 : currowIndex + 9))
                    xTableAddInfo.scrollTo(5000, currowIndex * rowHeight);
                    // xTableAddInfo.scrollTo(5000, null)
                });
            }

            // 包装单位额外处理
            if (curFocusIndex === 2 && focusDatas[curFocusIndex]) {
                this.clickUnitListFn(currowData);
            }
        },
        closeAddProductFn() {
            this.addProductEnter = false;
            this.addProductMd.alert = false;
            const xTableAddInfo = this.$refs.xTableAddInfo,
                tableData = xTableAddInfo.getTableData().tableData;

            xTableAddInfo.focus();
            xTableAddInfo.clearCurrentRow();
            xTableAddInfo.setCurrentRow(tableData[tableData.length - 1]);
        },
        addProductZoomFn() {
            this.isAddProductModalTableMax = !this.isAddProductModalTableMax;
            const xTableAddInfo = this.$refs.xTableAddInfo;
            if (xTableAddInfo) {
                xTableAddInfo.focus();
            }
            // const xTableAddInfo = this.$refs.xTableAddInfo
            // if (this.isAddProductModalTableMax) {
            //     xTableAddInfo.revert()
            // } else {
            //     xTableAddInfo.maximize()
            // }
            // this.isAddProductModalTableMax = !this.isAddProductModalTableMax
        },
        // 垂直方向切换焦点事件
        toogleVerticalFocusAndActiveRowFn(keyCode) {
            const xTableAddInfo = this.$refs.xTableAddInfo,
                tableData = xTableAddInfo.getTableData().tableData,
                rowHeight = 38;

            let currowData = xTableAddInfo.getCurrentRecord(),
                currowIndex = xTableAddInfo.getRowIndex(currowData),
                isBoundary = false; // 是否是边界值，默认不是边界

            if (keyCode === 40) {
                if (currowIndex + 1 <= tableData.length - 1) {
                    currowIndex += 1;
                } else {
                    isBoundary = true;
                }
            } else {
                if (currowIndex - 1 >= 0) {
                    currowIndex -= 1;
                } else {
                    isBoundary = true;
                }
            }

            if (isBoundary) return;

            this.resetFocusFn();

            currowData = tableData[currowIndex];
            if (!currowData.product_inner_sn) {
                currowData.focusDatas.splice(0, 1, true);
            } else {
                currowData.focusDatas.splice(1, 1, true);
            }

            xTableAddInfo.reloadRow(currowData, null, 'focusDatas');

            xTableAddInfo.syncData();
            xTableAddInfo.clearCurrentRow();
            xTableAddInfo.setCurrentRow(currowData);

            this.addInfoDatas = xTableAddInfo.getTableData().tableData;

            xTableAddInfo.clearScroll().then(() => {
                if (currowIndex <= 1) {
                    currowIndex = 0;
                }
                if (currowIndex >= tableData.length - 1) {
                    currowIndex = tableData.length;
                }
                xTableAddInfo.scrollTo(null, currowIndex * rowHeight);
            });
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
        // 注册证下拉事件
        getMdrfListFn(e, record, seq) {
            // 经销商自建产品没有注册证号
            if (parseFloat(record.product_type) === 1 && !record.productMdrfFlag) {
                this.platformProductMdrfListAPIFn(record, seq);
            } else {
                this.recordMdrf = { ...record, seq: seq - 1 };
            }
            e.stopPropagation();
        },
        changeMdrfListFn(value, label) {
            if (label.length) {
                this.recordMdrf.mdrf_sn = label[label.length - 1];
                this.recordMdrf.mdrf_inner_sn = value;
            } else {
                this.recordMdrf.mdrf_sn = '';
                this.recordMdrf.mdrf_inner_sn = null;
            }
            if (this.recordMdrf.action && this.recordMdrf.action === -1) this.recordMdrf.action = 2;
            this.addInfoDatas[this.recordMdrf.seq].mdrf_sn = this.recordMdrf.mdrf_sn;
            this.addInfoDatas[this.recordMdrf.seq].action = this.recordMdrf.action;
            this.$refs.xTableAddInfo.updateData();
        },
        /* 双击内层表格的空白部分添加产品 */
        dblclickAddProductFn() {
            if (this.isClickRedDashedOrder) return;
            this.$nextTick(() => {
                // 双击
                const applyTable = this.$refs.applyTable;
                if (applyTable && applyTable.getElementsByClassName('ant-table-placeholder').length) {
                    applyTable
                        .getElementsByClassName('ant-table-placeholder')[0]
                        .addEventListener('dblclick', this.dblclickTableFn, false);
                }
            });
        },
        // 调拨单 下半部分表格
        async AllotTransferDetailAPIFn(record) {
            const params = {
                avid: record.gv_inner_sn || record.av_inner_sn,
                pageNum: 1,
                pageSize: 10
            };
            if (this.c_IsGrn) {
                params.status = 2;
            }
            await AllotTransferDetailAPI({ params })
                .then((res) => {
                    this.billLadingBotDatas = [];
                    this.gotdownInfoModal.data = [];
                    let data = [];
                    this.DetailTableLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        data = list.map((item) => {
                            item.tvStatus = item.tvStatus === 'AWAITING' ? '待完成' : '已完成';
                            return {
                                ...item,
                                unit: item.unit_name,
                                product_vendor_name: item.vd_name,
                                product_vendor_inner_sn: item.vd_inner_sn,
                                activeClass: '',
                                status: null
                            };
                        });

                        data.forEach((v) => {
                            if (v.tvStatus === '待完成') {
                                if (this.c_IsGrn) {
                                    this.gotdownInfoModal.data.push(v);
                                } else {
                                    this.billLadingBotDatas.push(v);
                                }
                            }
                        });
                    } else {
                        this.DetailTableLoading = false;
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.DetailTableLoading = false;
                });
        },
        // 出库单 - 经销商 - 下半部分表格
        async odoTransferRecordDetailAPIFn(record) {
            this.odrder_type_m = this.addInfoModalForm.billsType;
            if (record.org_id) this.org_id_m = record.org_id;
            this.sl_inner_sn_m = record.sl_inner_sn;
            this.billLadingBotDatas = [];
            this.billLadingBotDatasAll = [];
            this.sl_inner_sn_m = record.sl_inner_sn;
            let params = {
                billsType: this.addInfoModalForm.billsType,
                pageNum: 1,
                pageSize: 100,
                sid: record.sl_inner_sn || record.voucher_inner_sn_c
            };
            await odoTransferRecordDetailAPI({ params })
                .then((res) => {
                    this.billLadingBotDatas = [];
                    this.billLadingBotDatasAll = [];
                    this.billLadingBotLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.billLadingBotDatasAll = list.map((item) => {
                            return {
                                ...item,
                                activeClass: '',
                                grant_quantity: item.quantity, // 这个是为了后边和发放单通用数据
                                unit_name: item.unit, // 这个是为了后边和发放单通用数据
                                key: item.detail_inner_sn,
                                vendor_inner_sn: item.product_vendor_inner_sn,
                                vendor_name: item.product_vendor_name,
                                produce_date: item.produce_date === '0000-00-00' ? '' : item.produce_date,
                                rate: item.rate * 100
                            };
                        });
                        if (this.sheelType) {
                            this.billLadingBotDatasAll.forEach((item) => {
                                if (item.status === 'AWAITING') {
                                    this.billLadingBotDatas.push(item);
                                }
                            });
                        } else {
                            this.billLadingBotDatas = this.billLadingBotDatasAll;
                        }
                        // if (mode !== undefined) { // 当这里为点击点开的时候 可以添加删除过的信息
                        //     this.delInfoDatas.forEach(itemS => {
                        //         let isf = true
                        //         this.billLadingBotDatas.forEach(item => {
                        //             if (item.product_type === itemS.product_type && item.product_inner_sn === itemS.product_inner_sn && item.vendor_inner_sn === itemS.vendor_inner_sn && item.specification === itemS.specification) {
                        //                 isf = false
                        //             }
                        //         })
                        //         if (isf) {
                        //             itemS.key = this.billLadingBotDatas.length + 1
                        //             itemS.product_vendor_inner_sn = itemS.vendor_inner_sn
                        //             itemS.product_vendor_name = itemS.vendor_name
                        //             itemS.produce_date = ''
                        //             itemS.overdue_date = ''
                        //             this.billLadingBotDatas.push(itemS)
                        //         }
                        //     })
                        // }
                        if (this.billLadingBotDatas.length) {
                            this.currentTrBillLadingBotObj = this.billLadingBotDatas[0];
                        } else {
                            this.currentTrBillLadingBotObj = {};
                        }
                        if (this.$refs.sheelTableAdd) {
                            this.$refs.sheelTableAdd.refreshColumn();
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.billLadingBotLoading = false;
                });
        },
        // 出库单 - 医院调单 - 下半部分表格
        async odoTransferGrantDetailAPIFn(record, mode) {
            if (record.department_inner_sn) {
                this.addInfoModalForm.department_inner_sn = record.department_inner_sn;
                this.addInfoModalForm.department_name = record.department_name;
            }
            this.billLadingBotDatas = [];
            this.billLadingBotDatasAll = [];

            let params = {
                billsType: this.addInfoModalForm.billsType,
                pageNum: 1,
                pageSize: 100,
                gvid: record.gv_inner_sn || record.voucher_inner_sn_c
            };
            await odoTransferGrantDetailAPI({ params })
                .then((res) => {
                    this.billLadingBotDatas = [];
                    this.billLadingBotDatasAll = [];
                    this.billLadingBotLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.billLadingBotDatasAll = list.map((item) => {
                            return {
                                ...item,
                                activeClass: '',
                                grant_quantity: item.grant_quantity, // 发放单不用写，销售单不知道
                                // quantity: item.grant_quantity,
                                key: item.detail_inner_sn,
                                status_name: this.sheelStatus[item.status],
                                source_type: 'copy' // 定义一个参数，凡是添加产品过去的信息，删除后都不留痕迹
                            };
                        });
                        if (this.sheelType) {
                            this.billLadingBotDatasAll.forEach((item) => {
                                if (item.status === 0) {
                                    this.billLadingBotDatas.push(item);
                                }
                            });
                        } else {
                            this.billLadingBotDatas = this.billLadingBotDatasAll;
                        }

                        if (mode !== undefined) {
                            // 当这里为点击点开的时候 可以添加删除过的信息
                            this.billLadingBotDatasAll.forEach((item) => {
                                let isf = false;
                                this.delInfoDatas.forEach((itemS) => {
                                    if (
                                        item.product_type === itemS.product_type &&
                                        item.product_inner_sn === itemS.product_inner_sn &&
                                        item.vendor_inner_sn === itemS.vendor_inner_sn &&
                                        item.specification === itemS.specification
                                    ) {
                                        isf = true;
                                    }
                                });
                                if (isf) item.status = 0;
                            });
                            this.billLadingBotDatas = [];
                            this.billLadingBotDatasAll.forEach((item) => {
                                if (item.status === 0) {
                                    this.billLadingBotDatas.push(item);
                                }
                            });
                        }
                        if (this.billLadingBotDatas.length) {
                            this.currentTrBillLadingBotObj = this.billLadingBotDatas[0];
                        } else {
                            this.currentTrBillLadingBotObj = {};
                        }
                        if (this.$refs.sheelTableAdd) {
                            this.$refs.sheelTableAdd.refreshColumn();
                        }
                    } else {
                        this.billLadingBotLoading = false;
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.billLadingBotLoading = false;
                });
        },
        async grnTransferRecordDetailAPIFn() {
            this.gotdownInfoModal.alert = true;
            this.gotdownInfoModal.loading = true;
            this.gotdownInfoModal.data = [];
            this.gotdownInfoModal.chooseData = [];
            this.$Utils.globalDragCenterFn('sheel-modal');
            const params = {
                billsType: this.addInfoModalForm.billsType,
                vId: this.getSheel.voucher_inner_sn_c || this.addInfoModalForm.voucher_inner_sn_c,
                voucherType: this.addInfoModalForm.voucher_type_c,
                organization_inner_sn: this.addInfoModalForm.organization_inner_sn
            };

            await grnTransferRecordDetailAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        list.forEach((item) => {
                            if (item.status === 'AWAITING') {
                                item.activeClass = '';
                                item.key = item.detail_inner_sn;
                                item.price = this.reNumber(item.quantity * item.unit_price_text);
                                item.source_type = 'copy';
                                this.gotdownInfoModal.data.push(item);
                            }
                        });
                        this.$refs.gotdownInfoTableAdd.refreshColumn();
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.gotdownInfoModal.loading = false;
                })
                .catch(() => {
                    this.gotdownInfoModal.loading = false;
                });
        },
        addProductParamsInitFn() {
            if (!this.addInfoModalForm.organization_inner_sn && !this.addInfoModalForm.organization_name) {
                this.$warning({
                    title: '操作提示',
                    content: '请先选择单位名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            this.partB.modalType = this.c_IsGrn ? 7 : 8;
            this.partB.otype = this.addInfoModalForm.billsType;

            if (this.orderTypeData.value === grnAndOdoOrderType['SALERETURN'].value) {
                this.partB.modalType = 7;
            }

            if (this.orderTypeData.value === grnAndOdoOrderType['PURCHASERETURN'].value) {
                this.partB.modalType = 8;
            }

            this.partB.oname = this.addInfoModalForm.organization_name;
            this.partB.oid = this.addInfoModalForm.organization_inner_sn;
            this.partB.warehouse_inner_sn = this.addInfoModalForm.warehouse_inner_sn;
        },
        dblclickTableFn(e) {
            this.resetAddInfoModalFocusFn();
            if (!this.checkOrgNameMsgFn()) return;
            if (e) {
                const filterDom = ['span', 'svg', 'path', 'input', 'i', 'li', 'ul'],
                    flag = filterDom.some((item) => item === e.target.tagName.toLowerCase()),
                    childrenFlag = filterDom.some(
                        (item) => item === e.target.children && e.target.children.tagName.toLowerCase()
                    ),
                    childrenFlagRemove = e.target.getElementsByClassName('action-btn-list-wrap').length;
                if (flag || childrenFlag || childrenFlagRemove) return;
            }

            /// 1. 拆分出入库单的调单添加产品 [除采购退货单 | 销售退货单外的调单]
            if (this.isSeparateBillOrder) {
                /// 采购退货单 | 销售退货单
                if (!this.isTransferRecord) {
                    this.addProductParamsInitFn();

                    this.addReturnListModal.alert = true;
                    let timer = setTimeout(() => {
                        this.$Utils.globalDragCenterFn('add-return-list');
                        clearTimeout(timer);
                        timer = null;
                    });
                    return;
                }

                if (this.addInfoModalForm.voucher_type_c && this.addInfoModalForm.voucher_type_c !== 'NONE') {
                    if (
                        this.orderTypeData.value === grnAndOdoOrderType['PURCHASEGRN'].value ||
                        this.orderTypeData.value === grnAndOdoOrderType['ALLOTGRN'].value ||
                        this.orderTypeData.value === grnAndOdoOrderType['SALEODO'].value ||
                        this.orderTypeData.value === grnAndOdoOrderType['ALLOTODO'].value ||
                        this.orderTypeData.value === grnAndOdoOrderType['GRANTODO'].value
                    ) {
                        if (this.c_IsGrn) {
                            this.gotdownInfoModal.alert = true;
                            this.$Utils.globalDragCenterFn('gotdown-info-modal');

                            if (this.orderTypeData.value === grnAndOdoOrderType['ALLOTGRN'].value) {
                                this.gotdownInfoModal.columns = this.DetailTableColumnsGrnAllot;
                                this.refreshBillLoadingBotColumns(
                                    this.DetailTableColumnsGrnAllot,
                                    'gotdownInfoTableAdd'
                                );
                            }
                        } else {
                            this.sheelInfoModal.alert = true;
                            this.$Utils.globalDragCenterFn('sheel-modal');

                            if (this.orderTypeData.value === grnAndOdoOrderType['SALEODO']) {
                                this.billLadingColumns = this.billLadingColumnsSale;
                                // this.billLadingBotColumns = this.billLadingBotColumnsSale
                                this.refreshBillLoadingBotColumns(this.billLadingBotColumnsSale);
                            } else if (this.orderTypeData.value === grnAndOdoOrderType['ALLOTODO'].value) {
                                // this.billLadingBotColumns = this.DetailTableColumnsOdoAllot
                                this.refreshBillLoadingBotColumns(this.DetailTableColumnsOdoAllot);
                            }
                        }

                        this.sheelType = true;
                        this.grnAndOdoTransferRecordDetailAPIFn();
                        return;
                    }
                }
            } else {
                if (this.c_IsGrn && this.isSheel) {
                    // 入库页面的入库单调单添加产品
                    this.gotdownInfoModal.alert = true;
                    this.$Utils.globalDragCenterFn('gotdown-info-modal');
                    if (this.sheelID === 'ALLOT') {
                        this.gotdownInfoModal.columns = this.DetailTableColumnsGrnAllot;
                        const t = {
                            av_inner_sn: this.getSheel.voucher_inner_sn_c || this.voucher_inner_sn_c
                        };
                        this.AllotTransferDetailAPIFn(t);
                    } else {
                        this.grnTransferRecordDetailAPIFn();
                    }
                    return;
                }

                if (!this.c_IsGrn && this.isSheel) {
                    this.sheelInfoModal.alert = true;
                    this.$Utils.globalDragCenterFn('sheel-modal');
                    switch (this.sheelID) {
                        // 医院 发放单
                        case 'GRANT': {
                            this.odoTransferGrantDetailAPIFn(this.sheelListData[0], true);
                            break;
                        }
                        // 经销商 销售单
                        case 'SALE_RECORD': {
                            this.billLadingColumns = this.billLadingColumnsSale;
                            this.billLadingBotColumns = this.billLadingBotColumnsSale;
                            this.odoTransferRecordDetailAPIFn(this.sheelListData[0], true);
                            break;
                        }
                        // 调拨单
                        case 'ALLOT': {
                            this.billLadingBotColumns = this.DetailTableColumnsOdoAllot;
                            this.AllotTransferDetailAPIFn(this.sheelListData[0]);
                            break;
                        }
                    }
                    return;
                }
            }

            /// 2. 正常添加产品
            this.addProductParamsInitFn();

            this.addProductMd.alert = true;
            let timer = setTimeout(() => {
                this.$Utils.globalDragCenterFn('add_product');
                clearTimeout(timer);
                timer = null;
            });

            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.setContractOrgCheckedFn();
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        selectAllGotdown({ checked }) {
            if (checked) {
                this.gotdownInfoModal.chooseData = [];
                this.gotdownInfoModal.data.forEach((item) => {
                    this.gotdownInfoModal.chooseData.push(item.detail_inner_sn);
                });
            } else {
                this.gotdownInfoModal.chooseData = [];
            }
        },

        selectChangeGotdown({ checked, row }) {
            if (checked) {
                this.gotdownInfoModal.chooseData.push(row.detail_inner_sn);
            } else {
                this.gotdownInfoModal.chooseData.forEach((item, index) => {
                    if (item === row.detail_inner_sn) {
                        this.gotdownInfoModal.chooseData.splice(index, 1);
                    }
                });
            }
        },
        DblClickGotdown({ row }) {
            if (typeof this.gotdownInfoModal.chooseData.find((e) => e === row.detail_inner_sn) === 'undefined') {
                this.gotdownInfoModal.chooseData.push(row.detail_inner_sn);
            }
            this.addGotdownInfo();
        },
        addProductFn() {
            if (this.addProductMd.alert) {
                this.$refs.chooseProMd.returnProList();
            }
        },
        transferOrderFn() {
            if (this.c_IsGrn) {
                if (this.tabChoose === this.tabTypesNum[0]) {
                    // 入库单tab
                    this.$refs['godown-entry-transfer'].returnList();
                }
            } else {
                this.$refs.transfer.returnList();
            }
        },
        getProReturn(dea, ven, infoModalForm) {
            // 获取回调产品
            this.addProductEnter = false;
            this.addProductMd.alert = false;
            this.addInfoLoading = true;
            this.clearRowClassNameFn();

            if (infoModalForm) {
                this.addInfoModalForm = {
                    ...this.addInfoModalForm,
                    ...infoModalForm
                };
            }

            this.partB['keyCodeVal'] = '';

            let tempDatas = [...ven, ...dea],
                oldAddDatas = tempDatas.map((item, index) => {
                    let templockedAry = this.addInfoDatas.filter(
                        (f) =>
                            `${f.product_type}${f.vendor_inner_sn}${f.product_inner_sn}${f.specification_inner_sn}` ===
                            `${item.product_type}${item.vendor_inner_sn}${item.product_inner_sn}${item.specification_inner_sn}`
                    );
                    if (templockedAry && templockedAry.length) {
                        item.batchDatas = templockedAry[0].batchDatas;
                        item.inventoryBannce = templockedAry[0].inventoryBannce;
                        item.isCopyAndDelVoucherItem = true; // 需要减掉刚刚填的数量
                    } else {
                        item.batchDatas = [];
                        item.inventoryBannce = 0;
                    }

                    if (!this.c_IsGrn) {
                        item.health_care_sn = '';
                    }

                    return {
                        ...item,
                        isReadonly: false, // 从正常的退货产品过来的产品不能编辑，从库存产品或平台产品过来的产品可编辑
                        quantity: '',
                        money: '',
                        action: 1,
                        unitPriceList: [],
                        unit_price_text: item.unit_price,
                        rate_string: item.rate * 100,
                        type: item.product_type === 1 ? '赠品' : '常规',
                        tax_unit_price: '',
                        batch_locked_quantity: 0,
                        max_batch_locked_quantity: 0,
                        mdrf_sn: '',
                        mdrf_inner_sn: '',
                        mdrfList: [],
                        warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn || '',
                        focusDatas: [
                            false,
                            index === tempDatas.length - 1,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false
                        ],
                        rate: 0
                    };
                });

            this.refreshTempInsertAddProductFn(oldAddDatas);

            // 构建最小包装单位的倍数 和 含税单价
            this.addInfoDatas.forEach((item, index) => {
                item.seq = index;
                this.getProductSnAPIFn(item, index + 1);
                if (item.healthSnData && item.healthSnData.length !== 0) {
                    item.health_care_sn = item.healthSnData[0].sn;
                }

                if (typeof item.average_price === 'undefined') {
                    item.average_price = 0;
                    item.average_price_compare = 0;
                }
                if (!item.min_unit_info) {
                    item.min_unit_info = 1;
                    this.loadUnitMinFn(item, index, 'add');
                }
                if (!this.c_IsGrn) {
                    if (item.unitPriceList.length <= 0) {
                        this.contractGetContractPriceAPIFn(item);
                    }
                }
            });

            let timer = setTimeout(() => {
                this.addInfoLoading = false;
                clearTimeout(timer);
                timer = null;
            }, 200);
        },
        rowClassNameFn({ row }) {
            if (row.row_add_flag) {
                return 'row--current';
            }
        },
        clearRowClassNameFn() {
            // 去除新增的临时背景颜色
            this.addInfoDatas.forEach((item) => {
                if (item.row_add_flag) {
                    item.row_add_flag = false;
                }
            });
        },
        // 申请时间的搜索改变事件
        changeRangePickerFn(value, dateString) {
            const [beginTime, endTime] = dateString;
            this.searchTables = {
                ...this.searchTables,
                begin_date: beginTime,
                end_date: endTime
            };
        },
        getCurrentStyle(current) {
            const style = {};
            if (current.date() === 1) {
                style.border = '1px solid #1890ff';
                style.borderRadius = '50%';
            }
            return style;
        },
        // 点击高级搜索的小图标的事件
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
            if (this.wareHouseSearchData.length <= 0) {
                this.warehouseUsableTopListAPIFn('search');
            }
            // this.rangePickerVal = moment['']
        },
        // 搜索按钮
        searchBtnFn() {
            if (!this.checkSearchInfoFormMsgFn()) return;
            this.currentTrObj = {};
            // this.godownEntryDatas = [] //修复bug 点储位的搜索不能清入库的列表
            this.godownEntryPagination = {
                ...this.godownEntryPagination,
                loading: true,
                total: 10,
                current: 1 // 对应后端的 pageSize
            };
            this.editGrnTypeFlag = false;

            if (this.isSeparateBillOrder) {
                this.searchListAPIFn();
            } else {
                if (this.c_IsGrn) {
                    if (this.tabChoose === 1) {
                        this.grnListAPIFn();
                    } else {
                        this.stoListAPIFn();
                    }
                } else {
                    this.odoListAPIFn();
                }
            }
        },
        checkSearchInfoFormMsgFn() {
            if (this.$Utils.checkInputValFn(this.searchTables.grn_odo_sn)) {
                this.$warning({
                    title: '操作提示',
                    content: `单据编号${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (this.$Utils.checkInputValFn(this.searchTables.handler_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `经手人${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        addGrnTypeModalFn(btnName) {
            switch (btnName) {
                case 'cancel': {
                    this.addGrnTypeModal.alert = false;
                    break;
                }
            }
        },
        grnTypeAddBtnFn(btnName) {
            if (btnName !== 'add' && this.grnTypeAddDatas.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    mask: false
                });
                return;
            }
            this.btnNameAddGrnTypeList = btnName;
            switch (btnName) {
                case 'add': {
                    this.addGrnTypeListModal = {
                        title: `${this.$route.meta.title} - 单据类别 - 新增`,
                        alert: true,
                        loading: false
                    };
                    this.grnTypeAddForm = {
                        name: '',
                        checked: true,
                        status: 1
                    };
                    this.addGrnTypeListModal.alert = true;
                    this.$Utils.globalDragCenterFn('modal-grntype-list');
                    break;
                }
                case 'edit': {
                    this.currentGrnTypeListTrObj = this.$refs.xTableAddType.getCurrentRow();
                    const status = parseFloat(this.currentGrnTypeListTrObj.status);
                    this.grnTypeAddForm = {
                        name: this.currentGrnTypeListTrObj.name,
                        checked: status === 1,
                        status
                    };
                    this.addGrnTypeListModal = {
                        title: `${this.$route.meta.title} - 单据类别 - 编辑`,
                        alert: true,
                        loading: false
                    };
                    this.$Utils.globalDragCenterFn('modal-grntype-list');
                    break;
                }
                case 'remove': {
                    const _this = this;
                    _this.$confirm({
                        title: '删除',
                        content: '确认要执行该操作吗？',
                        okText: '确认',
                        cancelText: '取消',
                        centered: true,
                        maskClosable: false,
                        mask: false,
                        onOk() {
                            const xTableAddType = _this.$refs.xTableAddType,
                                curObj = xTableAddType.getCurrentRow();
                            if (_this.c_IsGrn) {
                                _this.grnTypeDelAPIFn(curObj);
                            } else {
                                _this.odoTypeDelAPIFn(curObj);
                            }
                        }
                    });
                    break;
                }
            }
        },
        addGrnTypeListModalFn(btnName) {
            switch (btnName) {
                case 'cancel': {
                    this.addGrnTypeListModal.alert = false;
                    this.addGrnTypeListModal.loading = false;
                    this.grnTypeAddForm = {
                        name: '',
                        checked: true,
                        status: 1
                    };
                    break;
                }
                case 'save': {
                    const typeName = this.$Utils.trim(this.grnTypeAddForm.name);
                    if (!typeName || typeName.length <= 0) {
                        this.$warning({
                            title: '操作提示',
                            content: '类别输入不能为空',
                            okText: '知道了',
                            centered: true,
                            mask: false
                        });
                    } else {
                        this.addGrnTypeListModal.loading = true;
                        switch (this.btnNameAddGrnTypeList) {
                            case 'add': {
                                if (this.c_IsGrn) {
                                    this.grnTypeAddAPIFn();
                                } else {
                                    this.odoTypeAddAPIFn();
                                }
                                break;
                            }
                            case 'edit': {
                                if (this.c_IsGrn) {
                                    this.grnTypeEditAPIFn();
                                } else {
                                    this.odoTypeEditAPIFn();
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        },
        changeGrnTypeAddFormFn(e) {
            const checkedVal = e.target.checked;
            this.grnTypeAddForm.status = checkedVal ? 1 : 0;
            this.grnTypeAddForm.checked = checkedVal;
        },
        // 格式化新增的表单信息
        resetAddInfoModalFn() {
            this.addInfoDatas = [];
            this.isAddProductModalTableMax = false;
            this.isClickRedDashedOrder = false;
            this.addInfoModalTitleFlag = false;
            this.currentTrAddInfoObj = {};
            this.superiorDepartments = [];
            this.editInfoFormObj = {};
            this.auditor_name = '';
            this.making_people_name = JSON.parse(this.$cookie.get('EmployeeInfo')).Name;
            this.addInfoModalForm = {
                department_inner_sn: '',
                department_name: '',
                description: '',
                details: '',
                grn_sn: '',
                billsType: '',
                handler_inner_sn: '',
                hoandler_name: '',
                org_id: '',
                organization_inner_sn: '',
                organization_name: '',
                status: -1,
                type_inner_sn: '',
                type_name: '',
                warehouse_inner_sn: '',
                warehouse_name: '',
                warehouse_operator_inner_sn: '',
                warehouse_operator_name: '',
                organization_inner_sn_old: '',
                organization_name_old: ''
            };
            this.firstScan = true;
            this.sheelID = '';
            this.addData = this.clearAddData();
        },
        chooseOrgType(obj) {
            console.log(obj);
        },
        // 外层列表顶部的操作按钮
        groupBtnFn(btnName) {
            if (btnName === 'copy') {
                this.isCopyLoading = true;
            }

            /// 当前是领用出库单和调拨出入库单单位名称取用户登录信息的值
            if (
                btnName === 'add' &&
                this.isSeparateBillOrder &&
                (this.orderTypeData.value === grnAndOdoOrderType['GRANTODO'].value ||
                    this.orderTypeData.value === grnAndOdoOrderType['ALLOTODO'].value ||
                    this.orderTypeData.value === grnAndOdoOrderType['ALLOTGRN'].value)
            ) {
                this.addInfoModalForm.organization_name = this.orgName;
                this.addInfoModalForm.organization_inner_sn = this.userBelong;

                if (this.orderTypeData.value === grnAndOdoOrderType['GRANTODO'].value) {
                    this.addInfoModalForm.billsType = 'HH';
                }
            }

            this.SheelHasWarehouse = false;
            this.isSheel = false;
            this.sheelListData = [];
            this.btnName = btnName;
            this.scanQRcode();
            // if (
            //     !(btnName === 'add' || btnName === 'type' || btnName === 'bill-lading') &&
            //     !this.checkEditInfoFormMsgFn()
            // )
            //     return;

            if (btnName === 'edit' || btnName === 'copy') {
                this.resetAddInfoModalFocusFn();
                this.orgDeptTopTreeAPIFn(); // 部门
                if (this.isSeparateBillOrder) {
                    this.grnAndOdoInfoAPIFn();
                } else {
                    if (this.c_IsGrn) {
                        this.grnInfoAPIFn(); // 获取入库单详情info
                    } else {
                        this.odoInfoAPIFn(); // 获取出库单详情info
                    }
                }
            }
            if (btnName === 'add') {
                this.making_people_name = JSON.parse(localStorage.getItem('userInfo')).EmployeeInfo.Name;
                this.relevantInstitutionsFocus = true;
                this.warehouseNameFocus = false;
                // this.warehouseUsableTopListAPIFn() // 仓库
                this.orgDeptTopTreeAPIFn(); // 部门
                if (!this.isSeparateBillOrder) {
                    if (this.c_IsGrn) {
                        this.grnTypeListAPIFn();
                    } else {
                        this.odoTypeListAPIFn();
                    }
                }

                this.orgEmpDropdownListAPIFn(); // 经手人
            }
            const _this = this;
            switch (btnName) {
                case 'add': {
                    this.addInfoModal = {
                        title: `${this.$route.meta.title} - 新增`,
                        alert: true,
                        loading: false
                    };

                    this.clearCurrentBatchNUmberFn();
                    this.isLoadingDepartmentInnerSn = true;
                    this.$Utils.globalDragCenterFn('modal-add');
                    this.dblclickAddProductFn();
                    break;
                }
                // 调单
                case 'bill-lading': {
                    if (this.c_IsGrn) {
                        // 入库页面
                        this.warehouseUsableTopListAPIFn();
                        if (this.tabChoose === this.tabTypesNum[0]) {
                            // 入库单tab
                            this.godownEntryTransferModal.alert = true;
                            if (this.$refs['godown-entry-transfer']) {
                                this.$refs['godown-entry-transfer'].init();
                            }
                            this.$Utils.globalDragCenterFn('modal-godown-entry-transfer');
                        }
                    } else {
                        // 出库页面
                        this.clearCurrentBatchNUmberFn();
                        this.billLadingModal.alert = true;
                        if (this.$refs.transfer) {
                            this.$refs.transfer.init();
                        }
                        this.$Utils.globalDragCenterFn('modal-bill-lading');
                    }
                    break;
                }
                case 'edit': {
                    this.addInfoLoading = true;
                    this.isLoadingDepartmentInnerSn = true;
                    this.addInfoModal = {
                        title: `${this.$route.meta.title} - 修改`,
                        alert: true,
                        loading: false
                    };

                    this.clearCurrentBatchNUmberFn();
                    this.$Utils.globalDragCenterFn('modal-add');
                    break;
                }
                case 'remove': {
                    _this.$confirm({
                        title: '删除',
                        content: '确认要执行该操作吗？',
                        centered: true,
                        maskClosable: false,
                        onOk() {
                            if (_this.c_IsGrn) {
                                switch (_this.tabChoose) {
                                    case 1: {
                                        if (_this.isSeparateBillOrder) {
                                            _this.grnAndOdoDelAPIFn();
                                        } else {
                                            _this.grnDelAPIFn();
                                        }
                                        break;
                                    }
                                    case 2: {
                                        _this.stoDelAPIFn();
                                        break;
                                    }
                                }
                            } else {
                                if (_this.isSeparateBillOrder) {
                                    _this.grnAndOdoDelAPIFn();
                                } else {
                                    _this.odoDelAPIFn();
                                }
                            }
                            return new Promise((resolve, reject) => {
                                setTimeout(Math.random() > 0.5 ? resolve : reject, 800);
                            }).catch(() => {});
                        }
                    });
                    break;
                }
                case 'stoModify': {
                    if (this.firstCome) {
                        this.addInfoModal.alert = true;
                        this.isLoadingDepartmentInnerSn = true;
                        this.$Utils.globalDragCenterFn('modal-add', 'other');
                        this.storageAllocatModal.alert = true;
                        this.$Utils.globalDragCenterFn('modal-storage-allocation-sheet');
                        this.firstCome = false; // 用来修复bug 判断是否第一次进
                        this.$nextTick(() => {
                            setTimeout(() => {
                                this.addInfoModal.alert = false;
                            }, 100);
                        });
                    } else {
                        this.storageAllocatModal.alert = true;
                        this.$Utils.globalDragCenterFn('modal-storage-allocation-sheet');
                    }
                    // this.grnStorageAPIFn(_this.currentTrObj.org_id, info.begin_inventory, info.end_inventory)
                    this.stoStorageAPIFn(
                        this.currentTrObjSto.org_id,
                        this.currentTrObjSto.sas_inner_sn,
                        this.currentTrObjSto.warehouse_inner_sn
                    );
                    break;
                }
                case 'type': {
                    this.addGrnTypeFn();
                    break;
                }
                case 'grnEntry': {
                    if (this.c_IsGrn) {
                        this.grnPutinBtnFn();
                    } else {
                        this.odoPutoutAPIFn();
                    }
                    break;
                }
                case 'copy': {
                    break;
                }
                case 'audit-feedback': {
                    _this.$confirm({
                        title: '取消审核',
                        content: '确认要执行该操作吗？',
                        centered: true,
                        maskClosable: false,
                        okText: '确认',
                        okType: 'primary',
                        cancelText: '取消',
                        onOk() {
                            if (_this.isSeparateBillOrder) {
                                _this.grnAndOdoUnreviewAPIFn();
                            } else {
                                if (_this.c_IsGrn) {
                                    _this.grnUnreviewAPIFn();
                                } else {
                                    _this.odoUnreviewAPIFn();
                                }
                            }
                            _this.godownEntryPagination.loading = true;
                        }
                    });
                    break;
                }
                case 'generate-red-dashed': {
                    _this.$confirm({
                        title: '生成红冲',
                        content: '确认要执行该操作吗？',
                        centered: true,
                        maskClosable: false,
                        okText: '确认',
                        okType: 'danger',
                        cancelText: '取消',
                        onOk() {
                            if (_this.isSeparateBillOrder) {
                                _this.grnAndOdoRedAPIFn();
                            } else {
                                if (_this.c_IsGrn) {
                                    _this.grnRedDashedAPIFn();
                                } else {
                                    _this.odoRedDashedAPIFn();
                                }
                            }
                            _this.godownEntryPagination.loading = true;
                        }
                    });
                    break;
                }
            }
        },
        /// 出/入库单的反审核 [修改状态为正式、将审核人置空]
        grnAndOdoUnreviewSyncDataFn() {
            this.currentTrObj.auditor_name = '';
            this.currentTrObj.auditor_inner_sn = '';
            this.currentTrObj.status = 0;
            this.currentTrObj.status_text = this.grnStatus[this.currentTrObj.status];
            const xTable = this.$refs.xTable;

            for (let i = 0; i < this.godownEntryDatas.length; i++) {
                let item = this.godownEntryDatas[i];
                if (this.c_IsGrn) {
                    if (item.grn_inner_sn === this.currentTrObj.grn_inner_sn) {
                        item.status = 0;
                        item.status_text = this.grnStatus[parseFloat(item.status)];
                        item.auditor_name = '';
                        item.auditor_inner_sn = '';
                        break;
                    }
                } else {
                    if (item.odo_inner_sn === this.currentTrObj.odo_inner_sn) {
                        item.status = 0;
                        item.status_text = this.grnStatus[parseFloat(item.status)];
                        item.auditor_name = '';
                        item.auditor_inner_sn = '';
                        break;
                    }
                }
            }
            xTable && xTable.syncData();
        },
        /// 出/入库单的红冲
        grnAndOdoRedDashedFn(info) {
            this.godownEntryPagination.total++;
            this.currentTrObj.status = 4;
            this.currentTrObj.status_text = this.grnStatus[this.currentTrObj.status];

            /// 记录当前行的索引
            let index = 0;

            /// 1.0 修改当前红冲的那条数据为作废
            for (let i = 0; i < this.godownEntryDatas.length; i++) {
                let item = this.godownEntryDatas[i];
                if (this.c_IsGrn) {
                    if (item.grn_inner_sn === this.currentTrObj.grn_inner_sn) {
                        index = i;
                        item.status = 4;
                        item.status_text = this.grnStatus[parseFloat(item.status)];
                        break;
                    }
                } else {
                    if (item.odo_inner_sn === this.currentTrObj.odo_inner_sn) {
                        index = i;
                        item.status = 4;
                        item.status_text = this.grnStatus[parseFloat(item.status)];
                        break;
                    }
                }
            }

            /// 2.0 在当前行的下方创建一条
            const xTable = this.$refs.xTable,
                isMaxLen = index + 1 >= this.godownEntryDatas.length,
                record = isMaxLen ? -1 : this.godownEntryDatas[index + 1],
                tempKey = this.c_IsGrn ? info.grn_inner_sn : info.odo_inner_sn;

            xTable
                .insertAt(
                    {
                        ...info,
                        grn_sn: this.c_IsGrn ? info.grn_sn : info.odo_sn,
                        key: tempKey + '' + info.billsType,
                        status_text: this.grnStatus[parseFloat(info.status)]
                    },
                    record
                )
                .then(({ row }) => {
                    this.godownEntryDatas = xTable.getTableData().tableData;
                    xTable.setCurrentRow(row);
                    this.currentTrObj = row;
                    // if (this.godownEntryDatas.length > this.godownEntryPagination.pageSize) {
                    //     if (isMaxLen) {
                    //         this.godownEntryDatas.splice(0, 1)
                    //     } else {
                    //         this.godownEntryDatas.splice(this.godownEntryDatas.length - 1, 1)
                    //     }
                    // }
                });
        },
        /// 出/入库单 复制功能校验  [如果是调单过来的单据不能复制]
        grnOdoOrderIsCopyFn(voucherTypeC) {
            let isCopy = false; // false - 能复制  true - 不能复制
            if (this.btnName === 'copy' && this.currentTrObj.status === 4) {
                // 不是调单过来的单据
                if (voucherTypeC === 'NONE' || !voucherTypeC) {
                    isCopy = false;
                } else {
                    // 其它情况都是调单
                    this.$warning({
                        title: '操作提示',
                        content: '不能复制调单的单据！',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    isCopy = true;
                }
            }
            return isCopy;
        },
        /// 出/入库单 复制功能的弹框
        grnOdoOrderCopyFn() {
            this.addInfoLoading = true;
            this.isLoadingDepartmentInnerSn = true;
            this.addInfoModal = {
                title: `${this.$route.meta.title} - 复制`,
                alert: true,
                loading: false
            };
            this.clearCurrentBatchNUmberFn();
            this.$Utils.globalDragCenterFn('modal-add');
        },
        // 获取回调产品
        getReturn(data, allData, getInfo) {
            // 需调从调单查询的接口中获取生成出库单时的部门
            if (allData.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请勾选产品细目。',
                    okText: '知道了',
                    centered: true
                });
                return;
            }
            // 是否为入库单 - 入库 - 新增 - 单据类型为销售单
            // 这种情况在点击生成销售单时，不把部门信息回填，需用户自行选择
            // const isPutinAndSalesOrder =
            //     this.tabChoose === 1 &&
            //     this.btnName === 'bill-lading' &&
            //     getInfo.billsType === 'HD'

            this.orgDeptTopTreeAPIFn();
            this.orgEmpDropdownListAPIFn();

            // 如果当前是医院端的领用出库单查询价格，将billsType置为 [HH]
            const employeeInfo = JSON.parse(this.$cookie.get('EmployeeInfo')),
                userInfo = JSON.parse(localStorage.getItem('userInfo'));
            if (this.isSeparateBillOrder && this.orderTypeData.value === grnAndOdoOrderType['GRANTODO'].value) {
                getInfo.billsType = 'HH';
                getInfo.organization_name = getInfo.organization_name || employeeInfo.OrgName;
                getInfo.organization_inner_sn = getInfo.organization_inner_sn || this.userBelong;
            }

            this.warehouseOperatorList = [];

            this.addInfoModalForm = {
                ...this.addInfoModalForm,
                warehouse_operator_name: '',
                warehouse_operator_inner_sn: '',
                ...getInfo,
                department_inner_sn: '',
                department_name: ''
            };

            /// 如果是销售出库单调单就将调单的部门带过来
            if (
                this.isSeparateBillOrder &&
                this.orderTypeData.value === grnAndOdoOrderType['SALEODO'].value &&
                !this.addInfoModalForm.department_inner_sn
            ) {
                this.addInfoModalForm.department_inner_sn = getInfo.department_inner_sn || '';
                this.addInfoModalForm.department_name = getInfo.department_name || '';
            } else {
                this.addInfoModalForm.department_inner_sn = userInfo.Departments[0].Department_inner_sn;
                this.addInfoModalForm.department_name = userInfo.Departments[0].Name;
            }

            /// 如果是采购入库调单，采购员默认为空
            if (this.isSeparateBillOrder && this.orderTypeData.value === grnAndOdoOrderType['PURCHASEGRN'].value) {
                this.addInfoModalForm.buyer_name = undefined;
            }

            this.addInfoModalForm.handler_inner_sn = employeeInfo.Employee_inner_sn;
            this.addInfoModalForm.handler_name = employeeInfo.Name;
            this.addInfoModalForm.organization_name = getInfo.organization_name;
            this.addInfoModalForm.organization_inner_sn = getInfo.organization_inner_sn;
            this.addInfoModalForm.warehouse_inner_sn = getInfo.warehouse_inner_sn;
            this.addInfoModalForm.warehouse_name = getInfo.warehouse_name;
            this.addInfoModalForm.receipt_type = getInfo.billsType;
            this.addInfoModalForm.billsType = getInfo.billsType;
            this.getSheel.voucher_inner_sn_c = getInfo.sl_inner_sn;
            this.getSheel.voucher_type_c = getInfo.id;
            this.sheelID = getInfo.id;
            this.isSheel = true;
            this.btnName = 'add';

            if (!getInfo.warehouse_inner_sn) {
                this.SheelHasWarehouse = false;
            } else {
                this.SheelHasWarehouse = true;
            }

            if (this.isSheel && this.sheelID === 'GRANT') {
                this.wareHouseFilterData = [
                    {
                        title: getInfo.warehouse_name,
                        value: getInfo.warehouse_inner_sn,
                        key: getInfo.warehouse_inner_sn
                    }
                ];
            }

            this.addInfoModalForm.voucher_type_c = this.sheelID;

            this.isLoadingDepartmentInnerSn = true;
            this.isGrnTypeLoading = true;
            if (!this.c_IsGrn && typeof this.addInfoModalForm.department_inner_sn !== 'undefined') {
                this.superiorDepartments = [
                    {
                        title: this.addInfoModalForm.department_name,
                        value: this.addInfoModalForm.department_inner_sn,
                        key: this.addInfoModalForm.department_inner_sn
                    }
                ];
            } else {
                this.superiorDepartments = [];
                this.addInfoModalForm.department_name = '';
                this.addInfoModalForm.department_inner_sn = '';
            }

            // this.orgDeptTopTreeAPIFn() // 部门
            if (this.c_IsGrn) {
                if (this.tabChoose === this.tabTypesNum[0]) {
                    // 入库单tab
                    this.godownEntryTransferModal.saveLoading = false;
                }
            } else {
                this.billLadingModal.saveLoading = false;
            }
            this.sheelListData = [];
            this.addInfoDatas = [];
            this.sheelListDataSame = [];
            allData.forEach((item, index) => {
                data.forEach((cItem) => {
                    if (item.detail_inner_sn === cItem) {
                        let unitPrice =
                            item.unit_price_text === undefined || item.unit_price_text === null || item.unit_price_text === ''
                                ? 1
                                : item.unit_price_text;
                        item.location = []; // 存储位置 - 储位分配
                        item.location_inner_sn = []; // 存储位置id - 储位分配
                        item.location_msg = ''; // 位置信息 - 储位分配
                        item.real_quantity = 0; // 实放数量 - 储位分配
                        item.key = Math.random();
                        item.index = index;
                        item.activeClass = '';
                        item.type = '0';
                        // item.mdrf_inner_sn = ''
                        // item.mdrf_sn = ''
                        item.mdrfList = [{ value: item.mdrf_inner_sn, label: item.mdrf_sn }];
                        item.unit_price_text = unitPrice;
                        item.tax_unit_price = unitPrice;
                        item.unitPriceList = [];
                        item.rate = item.rate ? item.rate * 100 : 0;
                        // item.produce_date = ''
                        // item.overdue_date = ''
                        item.produce_date = item.produce_date
                            ? item.produce_date === '0000-00-00'
                                ? ''
                                : moment(item.produce_date)
                            : '';
                        item.overdue_date = item.overdue_date ? moment(item.overdue_date) : '';
                        item.name = item.product_name;
                        item.unit_name = item.unit_name ? item.unit_name : item.unit;
                        item.unitList = item.unit_inner_sn
                            ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                            : [];
                        item.unit_inner_sn = item.unit_inner_sn ? [item.unit_inner_sn] : [];
                        item.arrivalunitList = item.unit_inner_sn
                            ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                            : [];
                        item.deliveryunitList = item.unit_inner_sn
                            ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                            : [];
                        // item.unit_inner_sn = [item.unit_inner_sn]
                        item.arrival_unit_inner_sn = [item.unit_inner_sn];
                        item.delivery_unit_inner_sn = [item.unit_inner_sn];
                        // item.unit_name = item.unit
                        item.arrival_unit = item.unit;
                        item.delivery_unit = item.unit;
                        item.isLoadingUnit = false; // 是否加载过整棵树
                        if (!this.c_IsGrn) {
                            item.quantity = item.toBeOutOfStock_quantity || item.grant_quantity;
                        } else {
                            item.quantity = item.quantity || item.toBeInOfStock_quantity;
                        }
                        item.money = parseFloat(item.quantity) ? (item.quantity * item.tax_unit_price).toFixed(4) : '';
                        item.arrival_quantity = item.quantity;
                        item.delivery_quantity = item.quantity;
                        item.vendor_name = item.vendor_name ? item.vendor_name : item.product_vendor_name;
                        item.vendor_inner_sn = item.vendor_inner_sn
                            ? item.vendor_inner_sn
                            : item.product_vendor_inner_sn;
                        item.voucher_inner_sn_c = this.getSheel.voucher_inner_sn_c;
                        item.voucher_type_c = this.getSheel.voucher_type_c;
                        item.voucher_detail_inner_sn_c = item.detail_inner_sn;
                        item.batch_number_old = item.batch_number;
                        item.voucher_quantity = '';
                        item.warehouse_inner_sn = this.addInfoModalForm.warehouse_inner_sn || '';
                        item.warehouse_name = this.addInfoModalForm.warehouse_name || '';
                        item.focusDatas = [
                            false,
                            index === 0,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false
                        ];

                        let templockedAry = this.addInfoDatas.filter(
                            (f) =>
                                `${f.product_type}${f.vendor_inner_sn}${f.product_inner_sn}${f.specification_inner_sn}` ===
                                `${item.product_type}${item.vendor_inner_sn}${item.product_inner_sn}${item.specification_inner_sn}`
                        );
                        if (templockedAry && templockedAry.length) {
                            item.batchDatas = templockedAry[0].batchDatas;
                            item.inventoryBannce = templockedAry[0].inventoryBannce;
                            item.isCopyAndDelVoucherItem = true; // 需要减掉刚刚填的数量
                        } else {
                            item.batchDatas = [];
                            item.inventoryBannce = 0;
                        }
                        item.healthSnData = [];
                        item.batch_locked_quantity = 0;
                        item.max_batch_locked_quantity = 0;
                        if (!this.c_IsGrn) {
                            if (item.unitPriceList.length <= 0) {
                                this.contractGetContractPriceAPIFn(item);
                            }
                        }
                        this.addInfoDatas.push(item);
                        this.sheelListData.push(item);
                        this.loadUnitMinFn(item, index);
                    }
                });
            });

            this.sheelListData.forEach((item) => {
                if (this.sheelListDataSame.length === 0) {
                    this.sheelListDataSame.push(item);
                } else {
                    let isf = true;
                    this.sheelListDataSame.forEach((itemS) => {
                        if (
                            item.product_type === itemS.product_type &&
                            item.product_inner_sn === itemS.product_inner_sn &&
                            item.vendor_inner_sn === itemS.vendor_inner_sn &&
                            item.specification === itemS.specification
                        ) {
                            itemS.grant_quantity += item.grant_quantity;
                            isf = false;
                        }
                    });
                    if (isf) {
                        this.sheelListDataSame.push(item);
                    }
                }
            });

            this.isBatchLadingFlag = true;
            if (this.addInfoDatas.length) {
                this.currentTrAddInfoObj = this.addInfoDatas[0];
            } else {
                this.currentTrAddInfoObj = {};
            }

            this.addInfoModal.alert = true;
            this.isLoadingDepartmentInnerSn = true;
            this.isWareHouseFilterLoading = true;
            this.addInfoModal.title = `${this.$route.meta.title} - 新增`;
            this.$Utils.globalDragCenterFn('modal-add');

            if (this.sheelID === 'ALLOT') {
                const name = this.c_IsGrn ? '转仓入库单' : '转仓出库单';
                this.grnTypeList = [{ title: name, value: 2, key: 2 }];
                this.addInfoModalForm.type_name = name;
                this.addInfoModalForm.type_inner_sn = 2;
            }

            if (this.c_IsGrn) {
                if (this.tabChoose === this.tabTypesNum[0]) {
                    // 入库单tab
                    this.godownEntryTransferModal.alert = false;
                    this.godownEntryTransferModal.saveLoading = false;
                }
            } else {
                // this.getHospitalGrantDetailsListFn()
                // this.resetBillLadingModalInfoModalFn()
                // if (this.addInfoDatas.length) {
                //     this.$refs.xTableAddInfo.setCurrentRow(this.addInfoDatas[0])
                //     this.batchNumberSetVal(this.addInfoDatas[0], 'init', [])
                // }
                this.addInfoLoading = false;
                this.billLadingModal.alert = false;
                this.billLadingModal.saveLoading = false;
            }

            this.$nextTick(() => {
                let xTableAddInfo = this.$refs.xTableAddInfo;
                if (!xTableAddInfo) return;
                const curData = this.addInfoDatas[this.addInfoDatas.length - 1];
                xTableAddInfo.loadData(this.addInfoDatas);
                xTableAddInfo.focus();
                xTableAddInfo.setCurrentRow(curData);
                this.scrollTableArrowDownFn();
                // 调单生成出入库单时，如果仓库并不在当前用户仓库列表中，弹出不可操作提示，并把仓库value设置为name，明确仓库显示
                if (!this.isIncludedInWarehouseList) {
                    const currentWarehouse = this.allWarehouseList.find(
                        (item) => item.warehouse_model_inner_sn === this.addInfoModalForm.warehouse_inner_sn
                    );
                    this.addInfoModalForm.warehouse_inner_sn = currentWarehouse.name;
                    this.$warning({
                        title: '提示',
                        content: `您不能操作仓库：${currentWarehouse.name} ，请检查调单是否正确`,
                        centered: true
                    });
                }
            });
        },
        // 关闭新增出库单的调单回调
        afterCloseFn() {
            document.onkeydown = function () {
                // 解除扫码绑定事件
                // e.preventDefault ? e.preventDefault() : (e.returnValue = false)
            };
            this.closeAddInfoModalResetScrollFn();
            this.isBatchLadingFlag = false;
            this.resetAddInfoModalFn();
        },
        // 出入库单表格明细按钮的操作
        actionTablegroupBtnFn(e, btnName, record, seq) {
            seq = seq - 1;
            this.addInfoLoading = true;
            const xTableAddInfo = this.$refs.xTableAddInfo;
            switch (btnName) {
                case 'copy': {
                    this.clearRowClassNameFn();
                    this.resetFocusFn();
                    xTableAddInfo.clearCurrentRow();
                    this.addInfoDatas.splice(seq, 0, {
                        health_care_sn: record.health_care_sn || '',
                        source_type: 'copy',
                        location_inner_sn: [],
                        location_msg: '',
                        real_quantity: 0,
                        activeClass: '',
                        produce_date: '',
                        overdue_date: '',
                        isLoadingUnit: false,
                        // batch_number: '',
                        arrival_quantity: record.arrival_quantity,
                        quantity: '',
                        quantity_old: '',
                        mdrfList: record.mdrfList,
                        mdrf_inner_sn: record.mdrf_inner_sn,
                        mdrf_sn: record.mdrf_sn,
                        name: record.product_name,
                        product_name: record.product_name,
                        product_inner_sn: record.product_inner_sn,
                        product_type: record.product_type,
                        specification: record.specification,
                        specification_inner_sn: record.specification_inner_sn,
                        type: record.type,
                        unitList: record.unitList,
                        unit_inner_sn: record.unit_inner_sn,
                        unit_name: record.unit_name,
                        unit_price: record.unit_price_text,
                        tax_unit_price: record.tax_unit_price,
                        rate: 0,
                        unitPriceList: record.unitPriceList || [],
                        vendor_inner_sn: record.vendor_inner_sn ? record.vendor_inner_sn : record.dealer_inner_sn,
                        product_vendor_inner_sn: record.product_vendor_inner_sn
                            ? record.product_vendor_inner_sn
                            : record.vendor_inner_sn,
                        vendor_name: record.vendor_name,
                        grn_inner_sn: record.grn_inner_sn,
                        odo_inner_sn: record.odo_inner_sn,
                        org_id: record.org_id,
                        gv_inner_sn: record.gv_inner_sn,
                        detail_inner_sn: record.detail_inner_sn,
                        voucher_detail_inner_sn_c: record.voucher_detail_inner_sn_c || null,
                        voucher_inner_sn_c:
                            record.voucher_inner_sn_c || record.gv_inner_sn || record.sl_inner_sn || null,
                        voucher_type_c: record.voucher_type_c || 0,
                        focusDatas: [false, true, false, false, false, false, false, false, false, false, false],
                        action: 1,
                        min_unit_info: record.min_unit_info,
                        batchDatas: record.batchDatas,
                        batch_locked_quantity: 0,
                        max_batch_locked_quantity: 0,
                        inventoryBannce: record.inventoryBannce,
                        warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn || '',
                        isCopyAndDelVoucherItem: true // 需要减掉刚刚填的数量
                    });
                    xTableAddInfo.loadData(this.addInfoDatas);
                    // this.addInfoDatas = xTableAddInfo.getTableData().tableData
                    const row = this.addInfoDatas[this.addInfoDatas.length - 1];
                    xTableAddInfo.setCurrentRow(row);
                    // xTableAddInfo.scrollToRow(row)
                    break;
                }
                case 'remove': {
                    record.seq = seq;
                    // xTableAddInfo.remove(record)
                    const mdrfInnerSn = record.mdrf_inner_sn;
                    if (Array.isArray(mdrfInnerSn)) {
                        record.mdrf_inner_sn = mdrfInnerSn.length > 0 ? mdrfInnerSn[mdrfInnerSn.length - 1] : '';
                    }

                    let obj = {
                        'source_type': record.source_type || '',
                        'detail_inner_sn': record.detail_inner_sn || '',
                        'vendor_inner_sn': record.vendor_inner_sn || record.dealer_inner_sn,
                        'vendor_name': record.vendor_name || record.dealer_name,
                        'product_inner_sn': record.product_inner_sn,
                        'product_name': record.name || record.product_name,
                        'specification_inner_sn': record.specification_inner_sn,
                        'specification': record.specification,
                        'unit_inner_sn': record.unit_inner_sn.length
                            ? record.unit_inner_sn[record.unit_inner_sn.length - 1]
                            : '',
                        'unit_name': record.unit_name,
                        'mdrf_inner_sn': record.mdrf_inner_sn,
                        'mdrf_sn': record.mdrf_sn,
                        'unit_price_text': record.unit_price_text,
                        'quantity': record.quantity,
                        'grant_quantity': record.grant_quantity,
                        'quantity_old': record.quantity_old,
                        'produce_date': record.produce_date && moment(record.produce_date).format(this.format),
                        'overdue_date': record.overdue_date && moment(record.overdue_date).format(this.format),
                        'type': record.type,
                        'sort_number': record.sort_number,
                        'batch_number': record.batch_number,
                        'product_type': record.product_type,
                        'codes': record.codes || [],
                        'action': 0,
                        'tax_unit_price': record.tax_unit_price
                    };
                    if (typeof record.voucher_type_c !== 'undefined') {
                        obj.voucher_type_c = record.voucher_type_c;
                        obj.voucher_inner_sn_c = record.voucher_inner_sn_c;
                        obj.voucher_detail_inner_sn_c = record.voucher_detail_inner_sn_c;
                    }
                    if (
                        record.source_type !== 'copy' &&
                        typeof record.detail_inner_sn !== 'undefined' &&
                        !record.isModalAdd
                    ) {
                        this.delInfoDatas.push(obj);
                    }
                    this.addInfoDatas.splice(seq, 1);
                    // this.addInfoDatas = xTableAddInfo.getTableData().tableData
                    if (this.addInfoDatas.length) {
                        xTableAddInfo.setCurrentRow(this.addInfoDatas[seq - 1 > 0 ? seq - 1 : 0]);
                        this.isAddInfoTempData = false;
                    } else {
                        if (this.btnName === 'edit') {
                            this.isAddInfoTempData = true;
                        } else {
                            this.isAddInfoTempData = false;
                        }
                    }
                    break;
                }
                case 'send-barcode': {
                    this.barcodeChildObj = record;
                    if (record.hasBarcode) {
                        this.generateBarcodeModal.title = `${this.$route.meta.title} - 查看内部条码`;
                    } else {
                        this.generateBarcodeModal.title = `${this.$route.meta.title} - 生成内部条码`;
                    }
                    this.generateBarcodeModal.alert = true;
                    this.$Utils.globalDragCenterFn('generate-internal-barcode');
                    break;
                }
            }
            this.dblclickAddProductFn();
            this.addInfoLoading = false;
            e.stopPropagation();
        },
        // 入库单按钮的操作
        grnPutinBtnFn() {
            const _this = this;
            if (parseFloat(_this.currentTrObj.status) === 1) {
                _this.$confirm({
                    title: '入库单确认',
                    content: '产品即将入库，是否确认？',
                    centered: true,
                    maskClosable: false,
                    onOk() {
                        _this.grnPutinAPIFn();
                        return new Promise((resolve, reject) => {
                            setTimeout(Math.random() > 0.5 ? resolve : reject, 800);
                        }).catch(() => {});
                    },
                    onCancel() {
                        _this.addInfoModal.godownEntryLoading = false;
                        return new Promise((resolve, reject) => {
                            setTimeout(Math.random() > 0.5 ? resolve : reject, 1);
                        }).catch(() => {});
                    }
                });
            } else {
                this.$warning({
                    title: '操作提示',
                    content: '只有状态为【已审核】的才能入库',
                    okText: '知道了',
                    centered: true,
                    mask: false
                });
            }
        },

        /**
         * 入/出库单 底部操作按钮
         * @param btnName 操作名
         */
        addInfoModalBotFn(btnName) {
            if (
                !(btnName === 'print' || btnName === 'close' || btnName === 'godownEntry') &&
                !this.checkAlertEditInfoFormMsgFn(btnName)
            )
                return;
            // 入/出库单调单的数量校验
            if (this.btnName === 'add' && this.isSheel && btnName !== 'close') {
                if (this.checkGodownEntryQuantityFn()) {
                    return;
                }
            }
            // 出库单的批号校验
            // if (((this.btnName === 'add' || this.btnName === 'edit') && (btnName === 'save' || btnName === 'audit')) && !this.c_IsGrn && this.checkBatchinventoryBannceDataFn()) return
            // 入库单的产品id + 规格型号id + 厂商/经销商id + 批号校验
            if (this.c_IsGrn && (btnName === 'save' || btnName === 'audit') && this.checkGrnProductValidateFn()) return;

            if (
                this.orderTypeData.value === grnAndOdoOrderType['PURCHASEGRN'].value &&
                (btnName === 'save' || btnName === 'audit') &&
                this.checkPurchaseGrnOverdueDateFn()
            )
                return;

            switch (btnName) {
                case 'print': {
                    let prDatas = this.addData.map((item, index) => {
                        return {
                            ...item,
                            index: index + 1,
                            unit_inner_sn: item.unit_inner_sn.length
                                ? item.unit_inner_sn[item.unit_inner_sn.length - 1]
                                : '',
                            money: new Big(item.quantity).times(item.unit_price_text).toNumber(), // 金额(含税)
                            produce_date: item.produce_date ? moment(item.produce_date).format(this.format) : '',
                            overdue_date: item.overdue_date ? moment(item.overdue_date).format(this.format) : '',
                            batch_number: item.batch_number.trim()
                        };
                    });
                    let routeUrl = this.$router.resolve({
                        path: `/newprint/${this.c_IsGrn ? 'inboundOrder' : 'outboundOrder'}`
                    });
                    let orderTypeData = this.orderTypeData;
                    // 销售赠送单
                    if (!orderTypeData && !this.c_IsGrn && Number(this.addInfoModalForm.type_inner_sn) === 5) {
                        orderTypeData = {
                            ...grnAndOdoOrderType.GIVEODO
                        };
                    }
                    // 将打印所需的数据JSON序列化后放入localStorage
                    window.localStorage.setItem(
                        'printContent',
                        JSON.stringify({
                            printData: prDatas,
                            printForm: {
                                ...this.addInfoModalForm,
                                'type_name': this.addInfoModalTitleComputed
                            },
                            // myOrderPropObj: this.myOrderPropObj,
                            orderTypeData,
                            planOrderStatus: ['待处理', '已终止', '已生成采购订单'],
                            printTitle: `${this.setOutPutTextComputed}单`
                        })
                    );
                    window.open(routeUrl.href, '_blank');
                    this.handleResetMultipleFocus();
                    break;
                }
                case 'bill-lading': {
                    // 调单
                    // this.billLadingModal.alert = true
                    // this.$Utils.globalDragCenterFn('modal-bill-lading') // 测试
                    break;
                }
                case 'close': {
                    this.stoCloseWarn();
                    break;
                }
                case 'save': {
                    if (this.addInfoModal.saveLoading) {
                        return;
                    }
                    this.addInfoModal.saveLoading = true;
                    if (this.isClickRedDashedOrder) {
                        this.odoAddAPIFn(0);
                    } else {
                        if (this.btnName === 'add' || this.btnName === 'copy') {
                            if (this.isSeparateBillOrder) {
                                this.grnAndOdoAddAPIFn(0);
                            } else {
                                if (this.c_IsGrn) {
                                    this.grnAddAPIFn(0);
                                } else {
                                    this.odoAddAPIFn(0);
                                }
                            }
                        } else {
                            if (this.isSeparateBillOrder) {
                                this.grnAndOdoEditAPIFn(0);
                            } else {
                                if (this.c_IsGrn) {
                                    this.grnEditAPIFn(0);
                                } else {
                                    this.odoEditAPIFn(0);
                                }
                            }
                        }
                    }
                    break;
                }
                case 'audit': {
                    if (this.addInfoModal.auditLoading) {
                        return;
                    }
                    this.addInfoModal.auditLoading = true;
                    if (this.isClickRedDashedOrder) {
                        this.odoAddAPIFn(1);
                    } else {
                        if (this.btnName === 'add' || this.btnName === 'copy') {
                            if (this.isSeparateBillOrder) {
                                this.grnAndOdoAddAPIFn(1);
                            } else {
                                if (this.c_IsGrn) {
                                    this.grnAddAPIFn(1);
                                } else {
                                    this.odoAddAPIFn(1);
                                }
                            }
                        } else {
                            if (this.isSeparateBillOrder) {
                                this.grnAndOdoEditAPIFn(1);
                            } else {
                                if (this.c_IsGrn) {
                                    this.grnEditAPIFn(1);
                                } else {
                                    this.odoEditAPIFn(1);
                                }
                            }
                        }
                    }
                    break;
                }
                case 'godownEntry': {
                    if (this.addInfoModal.godownEntryLoading) {
                        return;
                    }
                    this.addInfoModal.godownEntryLoading = true;
                    if (this.c_IsGrn) {
                        this.grnPutinBtnFn();
                    } else {
                        this.odoPutoutAPIFn();
                    }
                    this.addInfoLoading = false;
                    break;
                }
                case 'review': {
                    if (this.addInfoModal.reviewLoading) {
                        return;
                    }
                    this.addInfoModal.reviewLoading = true;
                    this.odoReviewAPIFn();
                    break;
                }
                case 'redDashed': {
                    this.isClickRedDashedOrder = true;
                    this.odoTypeListAPIFn();
                    break;
                }
            }
        },
        // 导出Excel
        exportExcel(ref) {
            const isEntry = this.c_IsGrn; // 是否为入库
            let columns = this.addColumns;
            if (ref === 'xTableAddInfo') {
                columns = this.addColumns;
            }
            let typeList = ['常规', '赠品'];
            let data = this.getStableData();
            const tableData = data.map((item) => {
                // 转换table数据（包装名称文本，日期，金额计算等）
                return {
                    ...item,
                    unit: item.unit_name,
                    money: this.$Utils.commafy(new Big(item.quantity).times(item.unit_price_text).round(2).toNumber()),
                    produce_date: item.produce_date ? moment(item.produce_date).format(this.format) : '',
                    overdue_date: item.overdue_date ? moment(item.overdue_date).format(this.format) : '',
                    mdrf_inner_sn: item.mdrf_sn,
                    type: typeList[parseInt(item.type)]
                };
            });

            const form = this.addInfoModalForm;
            const fromData = {
                // 导出表单信息
                title: this.addInfoModalTitleComputed,
                desc: {
                    label: '备注',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '单据编号',
                            value: isEntry ? form.grn_sn : form.odo_sn
                        },
                        {
                            label: '单位名称',
                            value: form.organization_name
                        },
                        {
                            label: '仓库',
                            value: form.warehouse_name
                        },
                        {
                            label: '部门',
                            value: form.department_name
                        },
                        {
                            label: '单据类别',
                            value: form.type_name
                        }
                    ],
                    [
                        {
                            label: '经手人',
                            value: form.handler_name
                        },
                        {
                            label: '仓库人员',
                            value: form.warehouse_operator_name
                        },
                        {
                            label: '制单人',
                            value: form.making_people_name
                        },
                        {
                            label: '审核人',
                            value: form.auditor_name
                        }
                    ]
                ]
            };

            // 当前是出库单，且单据billsType是DH，VH，才需要额外展示使用科室
            if (
                !this.c_IsGrn &&
                (this.addInfoModalForm.billsType === 'DH' || this.addInfoModalForm.billsType === 'VH')
            ) {
                fromData.groups[1].unshift({
                    label: '使用科室',
                    value: form.use_department || ''
                });
            }

            // 当前是采购入库单，需要额外展示采购员
            if (this.isSeparateBillOrder && this.orderTypeData.value === grnAndOdoOrderType['PURCHASEGRN'].value) {
                fromData.groups[1].unshift({
                    label: '采购员',
                    value: form.buyer_name || ''
                });
            }

            this.$Utils.exportExcel(
                columns,
                tableData.map((_) => ({ ..._, rate: `${_.rate}%` })), // 出、入库单excel税率字段添加百分号
                [],
                fromData
            );
        },
        // 包装单位区域
        clickUnitListFn(record, seq) {
            // 如果是出入库单拆分出来的采购退货单或销售退货单，且是只读的属性就不能编辑
            if (this.isSeparateBillOrder && !this.isTransferRecord && record.isReadonly) return;

            record.seq = seq - 1;
            if (!record.isLoadingUnit) {
                if (!record.detail_inner_sn || this.btnName === 'add') {
                    parseFloat(record.product_type) === 1
                        ? this.platformUnitTreeFn(record, 'add')
                        : this.dealerUnitTreeAPIFn(record, 'add');
                } else if (this.btnName === 'edit' || this.btnName === 'copy') {
                    parseFloat(record.product_type) === 1
                        ? this.platformUnitTreeFn(record)
                        : this.dealerUnitTreeAPIFn(record);
                }
            }
            this.record = {
                ...record
            };
        },
        changeUnitListFn(value, selectedOptions) {
            this.record = {
                ...this.record,
                unit_inner_sn: value
            };

            !this.c_IsGrn &&
                this.loadUnitMinFn(
                    this.record,
                    this.record.seq,
                    this.record && this.record.detail_inner_sn ? '' : 'add'
                );
            if (selectedOptions.length) {
                const curSelectedOption = selectedOptions[selectedOptions.length - 1];
                this.record.unit_name = curSelectedOption.name;
                this.record.min_measure = curSelectedOption.min_measure;
            } else {
                this.record.unit_name = '';
            }
            this.addInfoDatas[this.record.seq].seq = this.record.seq;
            this.addInfoDatas[this.record.seq].unit_name = this.record.unit_name;
            this.addInfoDatas[this.record.seq].unit_inner_sn = this.record.unit_inner_sn;
            this.addInfoDatas[this.record.seq].min_measure = this.record.min_measure;
            this.addInfoDatas[this.record.seq].action = this.record.action === -1 ? 2 : this.record.action;
            const averagePrice = this.record.average_price || 0;
            this.addInfoDatas[this.record.seq].average_price_compare = averagePrice * this.record.min_unit_info;
            this.$set(this.addInfoDatas, this.record.seq, { ...this.addInfoDatas[this.record.seq] });
            this.contractGetContractPriceAPIFn(this.record);
            const xTableAddInfo = this.$refs.xTableAddInfo;
            xTableAddInfo.focus();
            // let seq = xTableAddInfo.getRowIndex(this.record)
            // xTableAddInfo.resetFocusFn('unit_price_text', seq > 0 ? seq - 1 : 0)
            xTableAddInfo.updateData();
        },
        displayUnitRender({ labels }) {
            let label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
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
                pItemDisabledFlag
            ) {
                if (!pIndexAry || !parentDepartmentNameOld) return;
                return list.map((cItem, cIndex) => {
                    let cItemDisabledFlag =
                        _this.btnName === 'edit' &&
                        (pItemDisabledFlag || (isAppendDatas === 'parentDirectors' && id === cItem.employee_inner_sn));
                    if (cItem.children && cItem.children.length > 0) {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key:
                                cItem.handler_inner_sn ||
                                cItem.employee_inner_sn ||
                                cItem.department_inner_sn ||
                                cItem.warehouse_model_inner_sn,
                            activeClass: '',
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            parentWarehouseModelInnerSn: [
                                ...parentWarehouseModelInnerSnOld,
                                cItem.warehouse_model_inner_sn
                            ],
                            value: (
                                cItem.handler_inner_sn ||
                                cItem.employee_inner_sn ||
                                cItem.department_inner_sn ||
                                cItem.warehouse_model_inner_sn
                            ).toString(),
                            disabled:
                                cItemDisabledFlag ||
                                (isAppendDatas === 'superiorDepartments' && parseInt(cItem.status) <= 0),
                            label: cItem.name,
                            isLeaf: false,
                            children: childrenIndexFn(
                                cItem.children,
                                [...pIndexAry, cIndex],
                                [...parentDepartmentNameOld, cItem.name],
                                [...parentWarehouseModelInnerSnOld, cItem.warehouse_model_inner_sn],
                                cItemDisabledFlag
                            )
                        };
                        return obj;
                    } else {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key:
                                cItem.handler_inner_sn ||
                                cItem.employee_inner_sn ||
                                cItem.department_inner_sn ||
                                cItem.warehouse_model_inner_sn,
                            activeClass: '',
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            parentWarehouseModelInnerSn: [
                                ...parentWarehouseModelInnerSnOld,
                                cItem.warehouse_model_inner_sn
                            ],
                            value: (
                                cItem.handler_inner_sn ||
                                cItem.employee_inner_sn ||
                                cItem.department_inner_sn ||
                                cItem.warehouse_model_inner_sn
                            ).toString(),
                            disabled:
                                cItemDisabledFlag ||
                                (isAppendDatas === 'superiorDepartments' && parseInt(cItem.status) <= 0),
                            label: cItem.name,
                            children: cItem.children
                        };
                        return obj;
                    }
                });
            }
            // 父级
            let datasOriginNew = datasOrigin.map((item, index) => {
                let itemDisabledFlag =
                    _this.btnName === 'edit' && isAppendDatas === 'parentDirectors' && id === item.employee_inner_sn;
                if (item.children && item.children.length > 0) {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key:
                            item.handler_inner_sn ||
                            item.employee_inner_sn ||
                            item.department_inner_sn ||
                            item.warehouse_model_inner_sn,
                        activeClass: '',
                        parentDepartmentName: [item.name],
                        parentWarehouseModelInnerSn: [item.warehouse_model_inner_sn],
                        value: (
                            item.handler_inner_sn ||
                            item.employee_inner_sn ||
                            item.department_inner_sn ||
                            item.warehouse_model_inner_sn
                        ).toString(),
                        disabled:
                            itemDisabledFlag || (isAppendDatas === 'superiorDepartments' && parseInt(item.status) <= 0),
                        label: item.name,
                        isLeaf: false,
                        children: childrenIndexFn(
                            item.children,
                            [index],
                            [item.name],
                            [item.warehouse_model_inner_sn],
                            itemDisabledFlag
                        )
                    };
                    return obj;
                } else {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key:
                            item.handler_inner_sn ||
                            item.employee_inner_sn ||
                            item.department_inner_sn ||
                            item.warehouse_model_inner_sn,
                        activeClass: '',
                        parentDepartmentName: [item.name],
                        parentWarehouseModelInnerSn: [item.warehouse_model_inner_sn],
                        value: (
                            item.handler_inner_sn ||
                            item.employee_inner_sn ||
                            item.department_inner_sn ||
                            item.warehouse_model_inner_sn
                        ).toString(),
                        disabled:
                            itemDisabledFlag || (isAppendDatas === 'superiorDepartments' && parseInt(item.status) <= 0),
                        label: item.name,
                        children: item.children
                    };
                    return obj;
                }
            });

            if (isAppendDatas) {
                switch (isAppendDatas) {
                    // 部门
                    case 'superiorDepartments': {
                        this.superiorDepartments = datasOriginNew;

                        if (this.btnName === 'add') {
                            let intersection = [];
                            let userBelong = JSON.parse(localStorage.getItem('userInfo')).Departments;
                            let departmentData = this.$Utils.flatArrayWithChildren(datasOriginNew);
                            userBelong.filter((a) => {
                                departmentData.filter((b) => {
                                    if (a.Department_inner_sn === b.department_inner_sn) {
                                        intersection.push(b);
                                    }
                                });
                            });
                            // 部门没有值时，才进行值初始化操作
                            if (
                                !this.addInfoModalForm.department_inner_sn &&
                                this.addInfoModalForm.department_inner_sn !== 0
                            ) {
                                this.addInfoModalForm.department_inner_sn = intersection[0].department_inner_sn;
                                this.addInfoModalForm.department_name = intersection[0].label;
                            }
                        }
                        break;
                    }
                    // 经手人
                    case 'allEmpDropdownList': {
                        this.handlerNameList = datasOriginNew;

                        if (this.btnName === 'add') {
                            // 默认赋值
                            this.addInfoModalForm.handler_inner_sn = JSON.parse(
                                this.$cookie.get('EmployeeInfo')
                            ).Employee_inner_sn;
                            this.addInfoModalForm.handler_name = JSON.parse(this.$cookie.get('EmployeeInfo')).Name;
                        }
                        break;
                    }
                }
            }
        },
        storageAllocatModalBotFn(btnName) {
            // 储位分配单
            switch (btnName) {
                // 取消
                case 'close': {
                    const _this = this;
                    _this.dargShow = false;
                    if (!_this.modalmask) {
                        _this.resetStorageAllocatModalInfoModalFn();
                        return;
                    }
                    if (_this.stoType === 1) {
                        // 若为编辑模式，不需要弹框提示
                        _this.resetStorageAllocatModalInfoModalFn();
                        _this.resetAddInfoModalFn();
                        return;
                    }
                    this.$confirm({
                        title: '操作提示',
                        content: '确定不进行储位分配吗？',
                        centered: true,
                        maskClosable: false,
                        onOk() {
                            _this.resetStorageAllocatModalInfoModalFn();
                            _this.resetAddInfoModalFn();
                        },
                        onCancel() {}
                    });
                    break;
                }
                // 保存
                case 'save': {
                    if (this.storageAllocatModal.saveLoading) {
                        return;
                    }
                    this.storageAllocatModal.saveLoading = true;
                    // const resFlag = this.storageAllocatTableCreateObjFn()

                    if (this.storageAllocatTableCheckFn()) {
                        // 储位分配成功
                        switch (this.stoType) {
                            case 0: {
                                this.sasAddAPIFn();
                                break;
                            }
                            case 1: {
                                this.sasEditAPIFn();
                                break;
                            }
                        }
                    } else {
                        this.storageAllocatModal.saveLoading = false;
                    }
                    break;
                }
            }
        },
        // 储位分配单打印
        printForStoreAllocation() {
            const { columns, tableData, formData } = this._genVisibleData();
            let printContent = {
                printData: tableData,
                printTable: columns,
                printType: 9,
                printForm: formData
            };
            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        formatWarehouseOperationListFn(info) {
            this.warehouseOperatorList = info.map((item) => {
                return {
                    ...item,
                    title: item.employee_name,
                    value: item.employee_inner_sn,
                    key: item.employee_inner_sn,
                    isLoading: true
                };
            });

            if (this.warehouseOperatorList.length && this.btnName === 'edit') {
                if (
                    info.some(
                        (item) => parseInt(item.employee_inner_sn) === parseInt(this.warehouseOperatorList[0].value)
                    )
                )
                    return;
                this.addInfoModalForm.warehouse_operator_name = '';
                this.addInfoModalForm.warehouse_operator_inner_sn = '';
            }

            if (
                this.c_IsGrn &&
                this.warehouseOperatorList.some(
                    (item) =>
                        parseFloat(item.employee_inner_sn) !==
                        parseFloat(this.addInfoModalForm.warehouse_operator_inner_sn)
                ) &&
                this.stoType !== 1
            ) {
                this.addInfoModalForm.warehouse_operator_inner_sn = '';
                this.addInfoModalForm.warehouse_operator_name = '';
            }
        },
        // 储位分配单导出
        exportExcelForStoreAllocation() {
            const { columns, tableData, formData } = this._genVisibleData();
            const form = {
                title: '储位分配单',
                desc: {
                    label: '备注',
                    value: formData.description
                },
                groups: [
                    [
                        {
                            label: '单据编号',
                            value: formData.sn
                        },
                        {
                            label: '仓库',
                            value: formData.warehouse_name
                        },
                        {
                            label: '作业人',
                            value: formData.warehouse_operator_name
                        },
                        {
                            label: '制单人',
                            value: formData.create_name
                        },
                        {
                            label: '制单时间',
                            value: formData.create_time
                        }
                    ]
                ]
            };
            this.$Utils.exportExcel(columns, tableData, [], form);
        },
        // 生成储位分配单打印和导出的显示数据 表单信息等
        _genVisibleData() {
            const table = this.$refs['xStorageAllocatTable'];
            const columns = table
                .getColumns()
                .map((item) => {
                    return {
                        dataIndex: item.property,
                        title: item.title,
                        key: item.property,
                        align: 'center'
                    };
                })
                .filter((item) => item.title !== '操作');
            const tableData = table.getData();
            const form = this.addInfoModalForm;
            let operatorName = ''; // 作业人名称 如果不存在就遍历获取
            if (form.warehouse_operator_name) {
                operatorName = form.warehouse_operator_name;
            } else {
                const temp =
                    this.warehouseOperatorList.find((item) => item.value === form.warehouse_operator_inner_sn) || {};
                operatorName = temp.title || '';
            }

            const isEntry = this.c_IsGrn; // 是否为入库
            let sn = '';
            if (isEntry) {
                if (this.stoType === 1 && this.modalmask) {
                    sn = form.grn_sn; // 从顶部tab进入的储位分配单编辑
                } else {
                    sn = form.sas_sn;
                }
            } else {
                sn = form.odo_sn; // 出库
            }
            return {
                columns, // 表头
                tableData: tableData.map((item, index) => {
                    // 表体数据
                    return {
                        ...item,
                        index: index + 1,
                        location_name: item.location_name || item.location // 存储位置
                    };
                }),
                formData: {
                    sn, // 单据编号
                    warehouse_name: form.warehouse_name, // 仓库
                    warehouse_operator_name: operatorName, // 作业人
                    description: form.description, // 备注
                    create_name: this.making_people_name, // 制单人
                    create_time: this.create_time // 制单时间
                }
            };
        },
        checkGrnProductValidateFn() {
            let data = this.getStableData();
            let validateObj = {},
                isValidate = false; // 默认过
            for (let i = 0; i < data.length; i++) {
                let item = data[i];
                if (!item.batch_number) {
                    item.batch_number = '';
                } else {
                    item.batch_number = item.batch_number.trim();
                }
                let key = `${item.product_inner_sn}==${item.specification_inner_sn}==${item.vendor_inner_sn}==${item.batch_number}`,
                    value = `NO.${i + 1}${item.product_name}—${item.specification}`;
                if (!validateObj[key]) {
                    validateObj[key] = value;
                } else {
                    isValidate = true;
                    this.$warning({
                        title: '操作提示',
                        content: `【${value}】和【${validateObj[key]}】批号不能填写一样。`,
                        okText: '知道了',
                        centered: true,
                        mask: true
                    });
                    break;
                }
            }
            return isValidate;
        },
        checkPurchaseGrnOverdueDateFn() {
            let validate = false;
            for (let i = 0; i < this.addInfoDatas.length; i++) {
                let item = this.addInfoDatas[i];
                let itemDate = item.overdue_date;
                let currentDate = moment().startOf('day');
                if (itemDate && itemDate < currentDate) {
                    validate = true;
                    this.$warning({
                        title: '操作提示',
                        content: `【${item.product_name}】的失效日期不能早于当前日期。`,
                        okText: '知道了',
                        centered: true,
                        mask: true
                    });
                    break;
                }
            }
            return validate;
        },
        addGotdownInfo() {
            // 调单
            if (this.gotdownInfoModal.chooseData.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请勾选产品细目。',
                    okText: '知道了',
                    centered: true,
                    mask: false
                });
            } else {
                let oldAddDatas = [];
                this.gotdownInfoModal.data.forEach((item, index) => {
                    this.gotdownInfoModal.chooseData.forEach((cItem) => {
                        let xh = index;
                        let cxh = parseFloat(cItem);
                        if (this.isSheel) {
                            xh = parseFloat(item.detail_inner_sn);
                        }
                        if (xh === cxh) {
                            item.location = []; // 存储位置 - 储位分配
                            item.location_inner_sn = []; // 存储位置id - 储位分配
                            item.location_msg = ''; // 位置信息 - 储位分配
                            item.real_quantity = 0; // 实放数量 - 储位分配
                            item.key = Math.random();
                            item.index = index;
                            item.activeClass = '';
                            item.action = 1;
                            item.type = '0';
                            item.unit_price_text = item.unit_price_text ? item.unit_price_text : 1;
                            item.quantity = item.toBeInOfStock_quantity;
                            item.tax_unit_price = item.unit_price_text;
                            item.unitPriceList = [];
                            item.produce_date = item.produce_date ? moment(item.produce_date) : '';
                            item.overdue_date = item.overdue_date ? moment(item.overdue_date) : '';
                            item.name = item.product_name;
                            item.mdrf_inner_sn = item.mdrf_inner_sn ? [item.mdrf_inner_sn] : [];
                            item.mdrfList = item.mdrf_sn
                                ? [{ value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn }]
                                : [];
                            item.unit_name = item.unit_name ? item.unit_name : item.unit;
                            item.unitList = item.unit_inner_sn
                                ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                : [];
                            item.unit_inner_sn = item.unit_inner_sn ? [item.unit_inner_sn] : [];
                            item.arrivalunitList = item.unit_inner_sn
                                ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                : [];
                            item.deliveryunitList = item.unit_inner_sn
                                ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                : [];
                            item.rate = item.rate ? item.rate : 0;
                            // item.unit_inner_sn = [item.unit_inner_sn]
                            item.arrival_unit_inner_sn = [item.unit_inner_sn];
                            item.delivery_unit_inner_sn = [item.unit_inner_sn];
                            // item.unit_name = item.unit
                            item.arrival_unit = item.unit;
                            item.delivery_unit = item.unit;
                            item.isLoadingUnit = false; // 是否加载过整棵树
                            item.arrival_quantity = item.quantity;
                            item.delivery_quantity = item.quantity;
                            if (parseFloat(item.quantity)) {
                                if (typeof item.money === 'undefined') {
                                    item.money = (item.quantity * item.tax_unit_price).toFixed(4);
                                } else {
                                    item.money = item.money.toFixed(4);
                                }
                            } else {
                                item.money = '';
                            }
                            item.vendor_name = item.vendor_name ? item.vendor_name : item.product_vendor_name;
                            item.vendor_inner_sn = item.vendor_inner_sn
                                ? item.vendor_inner_sn
                                : item.product_vendor_inner_sn;
                            item.voucher_inner_sn_c =
                                this.getSheel.voucher_inner_sn_c || this.addInfoModalForm.voucher_inner_sn_c;
                            item.voucher_type_c = this.getSheel.voucher_type_c || this.addInfoModalForm.voucher_type_c;
                            item.voucher_detail_inner_sn_c = item.detail_inner_sn;
                            item.focusDatas = [
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false
                            ];
                            oldAddDatas.push(item);
                        }
                    });
                });
                if (oldAddDatas.length) {
                    oldAddDatas[oldAddDatas.length - 1].focusDatas[1] = true;
                }
                this.refreshTempInsertAddProductFn(oldAddDatas);
                this.gotdownInfoModal.chooseData = [];
                this.gotdownInfoModal.alert = false;
            }
        },
        addSheelInfo() {
            // 调单
            if (this.billLadingBotSelectedRowKeys.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请勾选产品细目。',
                    okText: '知道了',
                    centered: true,
                    mask: false
                });
            } else {
                let oldAddDatas = [];
                this.billLadingBotDatas.forEach((item, index) => {
                    this.billLadingBotSelectedRowKeys.forEach((cItem, cIndex) => {
                        let xh = index;
                        let cxh = parseFloat(cItem);
                        if (this.isSheel) {
                            xh = parseFloat(item.detail_inner_sn);
                        }
                        if (xh === cxh) {
                            // item.kkquantity = item.quantity
                            item.mdrfList = item.mdrf_sn
                                ? [{ value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn }]
                                : [];
                            item.unitList = item.unit_inner_sn
                                ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                : [];
                            item.unit_inner_sn = item.unit_inner_sn ? [item.unit_inner_sn] : [];
                            item.unit_inner_sn_old = item.unit_inner_sn_old ? [item.unit_inner_sn_old] : [];
                            item.batch_number_old = item.batch_number;
                            item.action = 1;
                            item.type = '0';
                            item.rate = item.rate ? item.rate : 0;
                            item.tax_unit_price = item.unit_price_text;
                            item.quantity = item.toBeOutOfStock_quantity;
                            item.unitPriceList = [];
                            item.isLoadingUnit = false; // 是否加载过整棵树
                            if (typeof item.min_unit_info === 'undefined') {
                                item.min_unit_info = 1; // 默认值
                                item.min_measure = 1; // 默认值
                            }
                            item.voucher_quantity = '';
                            if (this.sheelID === 'GRANT') {
                                item.unit_price_text = 1;
                                item.name = item.product_name;
                                item.voucher_detail_inner_sn_c = item.detail_inner_sn;
                                item.voucher_type_c = this.voucher_type_c || this.getSheel.voucher_type_c;
                                item.voucher_inner_sn_c = this.voucher_inner_sn_c || item.gv_inner_sn;
                            }
                            if (this.sheelID === 'SALE_RECORD') {
                                item.vendor_name = item.product_vendor_name;
                                item.vendor_inner_sn = item.product_vendor_inner_sn;
                                item.voucher_detail_inner_sn_c = item.detail_inner_sn;
                                item.voucher_type_c = this.voucher_type_c || this.getSheel.voucher_type_c;
                                item.voucher_inner_sn_c = this.voucher_inner_sn_c || item.sl_inner_sn;
                                item.name = item.product_name;
                            }
                            if (this.sheelID === 'ALLOT') {
                                item.vendor_name = item.vd_name;
                                item.vendor_inner_sn = item.vd_inner_sn;
                                item.voucher_detail_inner_sn_c = item.detail_inner_sn;
                                item.voucher_type_c = this.voucher_type_c || this.getSheel.voucher_type_c;
                                item.voucher_inner_sn_c = this.voucher_inner_sn_c || item.av_inner_sn;
                                item.name = item.product_name;
                            }
                            item.money = parseFloat(item.quantity)
                                ? (item.quantity * item.tax_unit_price).toFixed(4)
                                : '';
                            item.produce_date = item.produce_date ? moment(item.produce_date) : '';
                            item.overdue_date = item.overdue_date ? moment(item.overdue_date) : '';
                            item.isModalAdd = true;
                            item.focusDatas = [
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false
                            ];
                            if (!this.c_IsGrn && item.unitPriceList.length <= 0) {
                                this.contractGetContractPriceAPIFn(item);
                            }
                            // this.addInfoDatas.push(item)
                            this.loadUnitMinFn(item, cIndex);
                            oldAddDatas.push(item);
                            let isf = true;
                            this.sheelListDataSame.forEach((itemS) => {
                                if (
                                    item.product_type === itemS.product_type &&
                                    item.product_inner_sn === itemS.product_inner_sn &&
                                    item.vendor_inner_sn === itemS.vendor_inner_sn &&
                                    item.specification === itemS.specification
                                ) {
                                    isf = false;
                                }
                            });
                            if (isf) this.sheelListDataSame.unshift(item);
                        }
                    });
                });
                const oldAddDataLen = oldAddDatas.length;

                if (oldAddDataLen) {
                    oldAddDatas[oldAddDataLen - 1].focusDatas[1] = true;
                    this.currentTrAddInfoObj = oldAddDatas[oldAddDataLen - 1];
                }

                this.refreshTempInsertAddProductFn(oldAddDatas);

                this.getHospitalGrantDetailsListFn();
                this.resetBillLadingModalInfoModalFn();
                this.sheelInfoModal.alert = false;
            }
        },
        resetBillLadingModalInfoModalFn() {
            // 调单数据重置
            this.billLadingModal.alert = false;
            this.billLadingDatas = [];
            this.currentTrBillLadingObj = {};
            this.billLadingPagination = {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1
            };
            this.billLadingBotDatas = [];
            this.billLadingBotSelectedRowKeys = [];
            this.currentTrBillLadingBotObj = {};
            this.billLadingBotLoading = false;
        },
        /// 刷新添加临时插入的产品数据
        refreshTempInsertAddProductFn(tempAddDatas) {
            this.addData.splice(this.editCellRow, 1);
            tempAddDatas.forEach((item, idx) => {
                item.rate_string = this.reNumber(item.rate * 100);
                this.addData.splice(this.editCellRow + idx, 0, item);
            });
            this.addData = this.addData.map((item, idx) => {
                return {
                    ...item,
                    idx: idx + 1
                };
            });
        },
        odoBatchNumberFn(record, seq) {
            // 出库单表格明细批号选择事件
            if (!this.addInfoModalForm.warehouse_inner_sn) {
                this.$warning({
                    title: '提示',
                    content: '请先选择仓库',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            this.batchRequestFn(record, seq);
            this.batchInfoModal.alert = true;
            this.$Utils.globalDragCenterFn('batch-modal');
            this.record = { ...record };
        },
        batchRequestFn(record, seq) {
            if (this.c_IsGrn && !(this.orderTypeData.value === grnAndOdoOrderType['PURCHASERETURN'].value)) return;
            if (!this.addInfoModalForm.warehouse_inner_sn) {
                this.$warning({
                    title: '提示',
                    content: '请先选择仓库',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            record.seq = seq - 1;
            this.$refs.xTableAddInfo.setCurrentRow(record);

            const currentRowItemSplitKey = `${record.product_type}${record.vendor_inner_sn}${record.product_inner_sn}${record.specification_inner_sn}`,
                // isAddAndEditSheel = (typeof record.detail_inner_sn !== 'undefined' && record.source_type !== 'copy') || this.isSheel, // 默认是修改|调单
                currentBatchRepeatKeyObj = this.batchRepeatKeyObj[currentRowItemSplitKey];
            if (
                Object.keys(this.batchRepeatKeyObj).length &&
                currentBatchRepeatKeyObj &&
                currentBatchRepeatKeyObj.seq > -1 &&
                currentBatchRepeatKeyObj.batch_data &&
                currentBatchRepeatKeyObj.batch_data.length
            ) {
                this.batchNumberSetVal(record, 'change');
                this.addInfoLoading = false;
            } else {
                // 获取批号列表
                this.odoBatchListAPIFn(record);
            }
        },
        // 增删改产品对应的批号列表里面的数据
        batchNumberSetVal(record, actionName, list) {
            // actionName: init 初始值   change 切换产品后点击批号【查看】    edit 修改值    add 初始化
            record = record || this.$refs.xTableAddInfo.getCurrentRow();

            let currentRowItemSplitKey = `${record.product_type}${record.vendor_inner_sn}${record.product_inner_sn}${record.specification_inner_sn}`,
                currentRowItemSplitLastKey = `${record.batch_number || ''}${
                    record.overdue_date ? moment(record.overdue_date).format(this.format) : ''
                }${record.produce_date ? moment(record.produce_date).format(this.format) : record.produce_date}`,
                currentRowItemKey = `${currentRowItemSplitKey}||${currentRowItemSplitLastKey}`,
                batchSeq = -1;

            // 每次进来前先构造每个产品 + 批号用掉了多少
            let { useProductBatchObj, sameProductBatchObj } = this.computedUseProductBatchFn(
                actionName,
                record.seq,
                currentRowItemSplitKey,
                actionName === 'add'
                    ? list
                    : (this.batchRepeatKeyObj[currentRowItemSplitKey] &&
                          this.batchRepeatKeyObj[currentRowItemSplitKey].batch_data) ||
                          []
            );
            let batchActionNameFn = {
                'init': () => {
                    let initBatchObj = {},
                        tempBatchObj = {};

                    for (let m = 0; m < this.addInfoDatas.length; m++) {
                        let mItem = this.addInfoDatas[m],
                            minQuantity = mItem.quantity * mItem.min_unit_info,
                            batchNumber = mItem.batch_number || '',
                            overdueDate = mItem.overdue_date ? moment(mItem.overdue_date).format(this.format) : '',
                            produceDate = mItem.produce_date ? moment(mItem.produce_date).format(this.format) : '',
                            mItemSplitKey = `${mItem.product_type}${mItem.vendor_inner_sn}${mItem.product_inner_sn}${mItem.specification_inner_sn}`,
                            mItemSplitLastKey = `${batchNumber}${overdueDate}${produceDate}`,
                            mItemKey = `${mItemSplitKey}||${mItemSplitLastKey}`;

                        if (initBatchObj[mItemSplitKey]) {
                            let mItemKeyItem = initBatchObj[mItemSplitKey][mItemKey];

                            if (mItemKeyItem) {
                                for (let l = 0; l < mItemKeyItem.batch_data.length; l++) {
                                    let lItem = mItemKeyItem.batch_data[l];
                                    lItem.batch_locked_quantity += minQuantity;
                                    lItem.max_batch_locked_quantity += minQuantity;
                                    lItem.inventoryBannce += minQuantity;
                                    lItem.quantity += minQuantity;
                                }
                            } else {
                                initBatchObj[mItemSplitKey] = {
                                    ...initBatchObj[mItemSplitKey],
                                    [mItemKey]: {
                                        batch_data: []
                                    }
                                };
                                initBatchObj[mItemSplitKey][mItemKey].batch_data.push({
                                    batch_locked_quantity: minQuantity,
                                    batch_number: batchNumber,
                                    inventoryBannce: minQuantity,
                                    is_edit: false,
                                    full_key: mItemKey,
                                    first_key: mItemSplitKey,
                                    last_key: mItemSplitLastKey,
                                    max_batch_locked_quantity: minQuantity,
                                    overdue_date: overdueDate,
                                    produce_date: produceDate,
                                    quantity: minQuantity
                                });
                            }
                        } else {
                            initBatchObj[mItemSplitKey] = {
                                [mItemKey]: {
                                    batch_data: []
                                }
                            };
                            initBatchObj[mItemSplitKey][mItemKey].batch_data.push({
                                batch_locked_quantity: minQuantity,
                                batch_number: batchNumber,
                                inventoryBannce: minQuantity,
                                is_edit: false,
                                full_key: mItemKey,
                                first_key: mItemSplitKey,
                                last_key: mItemSplitLastKey,
                                max_batch_locked_quantity: minQuantity,
                                overdue_date: overdueDate,
                                produce_date: produceDate,
                                quantity: minQuantity
                            });
                        }
                    }

                    for (let bKey in initBatchObj) {
                        let bItemKey = initBatchObj[bKey];
                        if (!tempBatchObj[bKey]) {
                            tempBatchObj[bKey] = {
                                seq: -1,
                                batch_seq: -1,
                                full_key: '',
                                first_key: '',
                                last_key: '',
                                batch_data: []
                            };
                        }
                        for (let bbKey in bItemKey) {
                            let bbItemKey = bItemKey[bbKey];
                            tempBatchObj[bKey].first_key = `${bKey}`;
                            tempBatchObj[bKey].last_key = `${bbKey.split('||')[1]}`;
                            tempBatchObj[bKey].full_key = `${bbKey}`;
                            bbItemKey.batch_data.forEach((f) => {
                                tempBatchObj[bKey].batch_data.push(f);
                            });
                        }
                    }
                    this.batchRepeatKeyObj = tempBatchObj;
                },
                'add': () => {
                    // 页面上的数量累加
                    let tempInventoryBannce = 0,
                        tempInitUseBatchObj = {},
                        tempInitSameBatchObj = {};
                    if (tempInventoryBannce <= 0 && this.btnName === 'edit') {
                        let aInventoryBannce = 0;
                        this.addInfoTempDatas.forEach((f) => {
                            let firstKeyInit = `${f.product_type}${f.vendor_inner_sn}${f.product_inner_sn}${f.specification_inner_sn}`,
                                fullKeyInit = `${firstKeyInit}||${f.batch_number || ''}${
                                    f.overdue_date ? moment(f.overdue_date).format(this.format) : ''
                                }${f.produce_date ? moment(f.produce_date).format(this.format) : ''}`,
                                quantityInit = f.quantity_old * f.min_unit_info;

                            if (tempInitUseBatchObj[fullKeyInit]) {
                                tempInitUseBatchObj[fullKeyInit] += quantityInit;
                            } else {
                                tempInitUseBatchObj[fullKeyInit] = quantityInit;
                            }

                            if (tempInitSameBatchObj[firstKeyInit]) {
                                tempInitSameBatchObj[firstKeyInit] += quantityInit;
                            } else {
                                tempInitSameBatchObj[firstKeyInit] = quantityInit;
                            }
                        });
                        // tempInventoryBannce = this.addInfoTempDatas.reduce((prev, next) => prev + (next.quantity_old * next.min_unit_info), 0)
                        tempInventoryBannce = tempInitSameBatchObj[currentRowItemSplitKey] || 0;
                        list.forEach((z) => {
                            z.inventoryBannce = record.inventoryBannce + tempInventoryBannce;
                            z.quantity_default = z.quantity;
                            aInventoryBannce = z.inventoryBannce;
                            // let tempQuantityInit = tempInitUseBatchObj[`${currentRowItemSplitKey}||${z.batch_number}${z.overdue_date}${z.produce_date}`]
                            // if (tempQuantityInit > z.inventoryBannceDefault && z.inventoryBannceDefault <= 0) { // 页面上的数据 和 默认所有批号最大的实际可用库存
                            //     z.quantity = tempQuantityInit
                            // } else { // 因为后端直接返回页面上当前批号可编辑的量【里面包含页面上已经使用过的量】，所以前端直接显示出来就行，不需要再加上odoInfoAPI回来类目明细的数量值
                            //     // z.quantity = tempQuantityInit ? tempQuantityInit + z.quantity : z.quantity
                            // }
                            // if (z.quantity > aInventoryBannce) {
                            //     z.quantity = z.inventoryBannce
                            // }
                            if (z.inventoryBannce > z.quantity_default) {
                                z.max_batch_locked_quantity = z.quantity_default;
                            } else {
                                if (z.inventoryBannce === z.quantity_default) {
                                    z.max_batch_locked_quantity = z.inventoryBannce;
                                } else {
                                    z.max_batch_locked_quantity = z.inventoryBannce - z.quantity_default;
                                }
                            }
                        });

                        this.addInfoDatas.forEach((aItem) => {
                            let firstKey = `${aItem.product_type}${aItem.vendor_inner_sn}${aItem.product_inner_sn}${aItem.specification_inner_sn}`;
                            if (firstKey === currentRowItemSplitKey) {
                                aItem.inventoryBannce = aInventoryBannce;
                                record.inventoryBannce = aInventoryBannce;
                            }
                        });
                    }

                    for (let i = 0; i < list.length; i++) {
                        let item = list[i];
                        if (!item.produce_date) {
                            item.produce_date = '';
                        }
                        let key = `${item.batch_number || ''}${item.overdue_date}${item.produce_date}`;
                        item.first_key = currentRowItemSplitKey;
                        item.last_key = key;
                        item.full_key = `${item.first_key}||${key}`;

                        if (item.inventoryBannce >= item.quantity) {
                            let tempSameProductBatchObj = sameProductBatchObj[item.first_key],
                                tempSameProductQuantitInit = tempSameProductBatchObj
                                    ? tempSameProductBatchObj.pageQuantity
                                    : 0,
                                tempUseProductBatchObj = useProductBatchObj[item.full_key],
                                tempUseProductQuantityInit = tempUseProductBatchObj
                                    ? tempUseProductBatchObj.pageQuantity
                                    : 0,
                                tempDiffQuantity = 0;

                            if (typeof item.max_batch_locked_quantity === 'undefined') {
                                item.max_batch_locked_quantity = item.quantity;
                            }
                            if (!tempSameProductBatchObj && !tempUseProductBatchObj) {
                                tempDiffQuantity = item.inventoryBannce - tempSameProductQuantitInit;
                                if (tempDiffQuantity > 0) {
                                    tempDiffQuantity =
                                        tempDiffQuantity > item.quantity ? item.quantity : tempDiffQuantity;
                                    item.batch_locked_quantity = tempDiffQuantity;
                                } else {
                                    item.batch_locked_quantity = tempUseProductQuantityInit ? 0 : item.quantity;
                                }
                            } else {
                                if (tempSameProductQuantitInit > item.inventoryBannce) {
                                    tempDiffQuantity = 0;
                                } else {
                                    if (tempUseProductQuantityInit <= 0 && this.btnName === 'add') {
                                        tempDiffQuantity = item.max_batch_locked_quantity;
                                    } else {
                                        let mySelfDiffQuantity =
                                                item.max_batch_locked_quantity - tempUseProductQuantityInit,
                                            maxDiffQuantity = item.inventoryBannce - tempSameProductQuantitInit;
                                        tempDiffQuantity =
                                            mySelfDiffQuantity > maxDiffQuantity ? maxDiffQuantity : mySelfDiffQuantity;
                                    }
                                    if (tempDiffQuantity > 0) {
                                        item.batch_locked_quantity = tempDiffQuantity;
                                    } else {
                                        item.batch_locked_quantity = 0;
                                    }
                                }
                            }

                            // item.max_batch_locked_quantity = item.quantity
                            // let tempDiffQuantity = item.inventoryBannce - tempSameProductQuantitInit
                            // if (tempDiffQuantity > 0) {
                            //     tempDiffQuantity = tempDiffQuantity > item.quantity ? item.quantity : tempDiffQuantity
                            //     item.batch_locked_quantity = tempDiffQuantity
                            // } else {
                            //     item.batch_locked_quantity = tempUseProductQuantityInit ? 0 : item.quantity
                            // }
                        } else {
                            item.quantity = item.inventoryBannce;
                            item.batch_locked_quantity = item.inventoryBannce;
                            item.max_batch_locked_quantity = item.inventoryBannce;
                        }

                        if (currentRowItemSplitLastKey === key) {
                            item.is_edit = true;
                            record.average_price = item.average_price;
                            record.average_price_compare = item.average_price * record.min_unit_info;
                        } else {
                            item.is_edit = false;
                        }
                    }
                },
                'change': () => {
                    list = this.batchRepeatKeyObj[currentRowItemSplitKey].batch_data;

                    for (let j = 0; j < list.length; j++) {
                        let jItem = list[j];
                        if (!jItem.produce_date) {
                            jItem.produce_date = '';
                        }
                        let jKey = `${jItem.batch_number || ''}${jItem.overdue_date}${jItem.produce_date}`;

                        jItem.full_key = `${jItem.first_key}||${jKey}`;
                        jItem.last_key = jKey;

                        if (jItem.inventoryBannce <= 0 && jItem.max_batch_locked_quantity <= 0) {
                            jItem.batch_locked_quantity = 0;
                        } else {
                            let tempUseProductBatchObj = useProductBatchObj[jItem.full_key],
                                tempUseProductQuantity = tempUseProductBatchObj
                                    ? tempUseProductBatchObj.pageQuantity
                                    : 0,
                                tempSameProductBatchObj = sameProductBatchObj[jItem.first_key],
                                tempSameProductQuantity = tempSameProductBatchObj
                                    ? tempSameProductBatchObj.pageQuantity
                                    : 0,
                                tempDiffQuantity = 0;
                            if (tempSameProductQuantity > jItem.inventoryBannce) {
                                tempDiffQuantity = 0;
                            } else {
                                if (
                                    tempUseProductQuantity <= 0 &&
                                    tempSameProductQuantity <= 0 &&
                                    this.btnName === 'add'
                                ) {
                                    tempDiffQuantity = jItem.max_batch_locked_quantity;
                                } else {
                                    let mySelfDiffQuantity = jItem.max_batch_locked_quantity - tempUseProductQuantity,
                                        maxDiffQuantity = jItem.inventoryBannce - tempSameProductQuantity; // 超出所有批号实际最大的库存
                                    tempDiffQuantity =
                                        mySelfDiffQuantity > maxDiffQuantity ? maxDiffQuantity : mySelfDiffQuantity;
                                }
                            }

                            if (tempDiffQuantity > 0) {
                                jItem.batch_locked_quantity = parseFloat(tempDiffQuantity.toFixed(3));
                            } else {
                                jItem.batch_locked_quantity = 0;
                            }

                            if (jItem.batch_locked_quantity > jItem.max_batch_locked_quantity) {
                                jItem.batch_locked_quantity = jItem.max_batch_locked_quantity;
                            }
                        }
                    }
                },
                'default': () => {
                    this.batchRepeatKeyObj[currentRowItemSplitKey] = {
                        seq: record.seq,
                        batch_seq: batchSeq,
                        full_key: currentRowItemKey,
                        first_key: currentRowItemSplitKey,
                        last_key: currentRowItemSplitLastKey,
                        batch_data: list.map((item) => ({
                            ...item,
                            batch_locked_quantity: item.batch_locked_quantity || 0
                        }))
                    };
                    this.batchDatas = [...this.batchRepeatKeyObj[currentRowItemSplitKey].batch_data];
                }
            };

            batchActionNameFn[actionName]();
            if (actionName === 'add' || actionName === 'change') {
                batchActionNameFn['default']();
            }
            this.isAddInfoTempData = false;
            this.resetAddProductDetailFn(record);
        },
        computedUseProductBatchFn(actionTypeName, seq, currentRowItemSplitKey, list) {
            let obj = {
                useProductBatchObj: {}, // 产品等信息 + 批号
                sameProductBatchObj: {} // 除批号外
            };

            for (let a = 0; a < this.addInfoDatas.length; a++) {
                let aItem = this.addInfoDatas[a],
                    aMinQuantity = 1;
                if (!aItem.isLoadingUnit) {
                    aMinQuantity = aItem.quantity * aItem.min_unit_info;
                } else {
                    aMinQuantity = aItem.quantity * aItem.min_measure;
                }
                let firstKey = `${aItem.product_type}${aItem.vendor_inner_sn}${aItem.product_inner_sn}${aItem.specification_inner_sn}`,
                    lastKey = `${aItem.batch_number || ''}${
                        aItem.overdue_date ? moment(aItem.overdue_date).format(this.format) : ''
                    }${aItem.produce_date ? moment(aItem.produce_date).format(this.format) : ''}`,
                    fullKey = `${firstKey}||${lastKey}`;

                // 纠正超出库存的数量
                // for (let i = 0; i < list.length; i++) {
                //     let item = list[i]
                //     if ((`${currentRowItemSplitKey}||${item.batch_number || ''}${item.overdue_date}${item.produce_date}` || item.full_key) === fullKey && aMinQuantity > item.quantity) {
                //         this.$warning({
                //             title: '操作提示',
                //             content: `【NO.${a + 1} —— ${aItem.batch_number || '空批号'}】已超出最大实际可用库存 ${item.quantity} !`,
                //             okText: '知道了',
                //             centered: true,
                //             maskClosable: false
                //         })
                //         aItem.quantity = item.quantity
                //         break
                //     }
                // }

                // 判断当前类目的批号存在批号列表中才记录使用的数据
                if (
                    list.some(
                        (f) =>
                            (`${currentRowItemSplitKey}||${f.batch_number || ''}${f.overdue_date}${
                                f.produce_date ? f.produce_date : ''
                            }` || f.full_key) === fullKey
                    )
                ) {
                    if (seq === a && lastKey) {
                        // 如果是当前自己就不加 [批号/效期必须填上]
                        aMinQuantity = 0;
                    }

                    if (obj.useProductBatchObj[fullKey]) {
                        obj.useProductBatchObj[fullKey].reduce += 1;
                        obj.useProductBatchObj[fullKey].pageQuantity += aMinQuantity;
                    } else {
                        obj.useProductBatchObj[fullKey] = {
                            reduce: 1,
                            pageQuantity: aMinQuantity
                        };
                    }

                    if (obj.sameProductBatchObj[firstKey]) {
                        obj.sameProductBatchObj[firstKey].reduce += 1;
                        obj.sameProductBatchObj[firstKey].pageQuantity += aMinQuantity;
                    } else {
                        obj.sameProductBatchObj[firstKey] = {
                            reduce: 1,
                            pageQuantity: aMinQuantity
                        };
                    }
                }
            }
            return obj;
        },
        cellClickBatchFn({ row, rowIndex }) {
            if (this.batchInventoryFn(row)) return;
            this.clearRowClassNameFn();
            this.setCurItemBatchListFn(row, rowIndex);
        },
        cellDblclickBatchFn({ row, rowIndex }) {
            if (this.batchInventoryFn(row)) return;
            this.clearRowClassNameFn();
            this.setCurItemBatchListFn(row, rowIndex);
        },
        batchInventoryFn(row) {
            if (row.batch_locked_quantity <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: `该批号暂无实际可用库存!`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return true;
            }
            return false;
        },
        setCurItemBatchListFn(record, seq) {
            // 设置当前行的批号等值
            record.seq = seq;
            let curObj = this.record,
                ischeckQuantity = false;

            if (curObj.action && curObj.action === -1) curObj.action = 2;

            for (let i = 0; i < this.addInfoDatas.length; i++) {
                const item = this.addInfoDatas[i];
                // minQuantity = item.quantity * item.min_unit_info,
                // currentRowItemSplitKey = `${item.product_type}${item.vendor_inner_sn}${item.product_inner_sn}${item.specification_inner_sn}`
                // currentRowItemSplitLastKey = `${item.batch_number || ''}${item.overdue_date && moment(item.overdue_date).format(this.format)}${item.produce_date && moment(item.produce_date).format(this.format)}`

                if (item._XID === curObj._XID) {
                    const averagePrice = record.average_price || 0;
                    // 销售出库单需要从批号中获取average_price字段值，用于buying_price字段
                    if (this.orderTypeData.value === grnAndOdoOrderType['SALEODO'].value) {
                        item.buying_price = averagePrice;
                    }
                    this.addInfoDatas.splice(i, 1, {
                        ...item,
                        // action: record.batch_number !== item.batch_number_old ? 2 : item.action,
                        batch_number: record.batch_number,
                        produce_date: record.produce_date && moment(record.produce_date),
                        overdue_date: record.overdue_date && moment(record.overdue_date),
                        min_packing_unit_inventory: record.quantity, // 最小包装单位库存
                        average_price: averagePrice,
                        average_price_compare: averagePrice * record.min_unit_info
                        // batch_locked_quantity: batchDataObj.batch_data[seq].batch_locked_quantity
                    });
                    if (this.orderTypeData.value !== grnAndOdoOrderType['PURCHASERETURN'].value) {
                        this.batchNumberSetVal(this.addInfoDatas[i], 'change');
                    }
                    break;
                }
            }

            if (!ischeckQuantity) {
                this.batchInfoModal.alert = false;
            }
        },
        displayLocationRenderFn({ labels }) {
            let label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
        },
        resetStorageAllocatModalInfoModalFn() {
            // 储位分配表单数据重置
            this.locationList = [];
            this.storageAllocatModal.alert = false;
            this.storageAllocatModal.saveLoading = false;
            this.storageAllocatModalOldForm = {
                department_inner_sn: '',
                department_name: '',
                description: '',
                details: '',
                grn_sn: '',
                odo_sn: '',
                odo_type: '',
                handler_inner_sn: '',
                handler_name: '',
                org_id: '',
                organization_inner_sn: '',
                organization_name: '',
                status: '',
                type_inner_sn: '',
                type_name: '',
                warehouse_inner_sn: '',
                warehouse_name: '',
                warehouse_operator_inner_sn: '',
                warehouse_operator_name: '',
                billsType: ''
            };
            this.storageAllocatDatas = [];
        },
        storageAllocatTableFn(record, index, btnName) {
            const xStorageAllocatTable = this.$refs.xStorageAllocatTable;
            if (btnName === 'remove') {
                // 删除
                let oldData = {
                    ...record,
                    action: 0
                };
                this.storageAllocatOldDatas.push(oldData);
                xStorageAllocatTable.remove(record);
                this.storageAllocatDatas = xStorageAllocatTable.getTableData().tableData;
                this.currentTrStorageAllocationObj = this.storageAllocatDatas.length ? this.storageAllocatDatas[0] : {};
                xStorageAllocatTable.setCurrentRow(this.currentTrStorageAllocationObj);
            } else if (btnName === 'add-storage-allocat') {
                // 新储位
                xStorageAllocatTable.clearCurrentRow();
                delete record._XID;
                xStorageAllocatTable
                    .insertAt(
                        {
                            ...record,
                            real_quantity: 0,
                            quantity: '',
                            location_inner_sn: [],
                            location_msg: '',
                            detail_inner_sn: '',
                            action: 1 // 新增为1
                        },
                        record
                    )
                    .then(({ row }) => {
                        this.storageAllocatDatas = xStorageAllocatTable.getTableData().tableData;
                        xStorageAllocatTable.setCurrentRow(row);
                        this.currentTrStorageAllocationObj = row;
                    });
            }
            this.dblclickAddProductFn();
        },
        storageAllocatTableCheckFn() {
            // 前端是否为空的格式校验
            let operatorName;
            if (this.stoType === 0) {
                operatorName = this.addInfoModalForm.warehouse_operator_name;
            } else {
                operatorName = this.addInfoModalForm.operator_name;
            }
            const contentsWarning = [];
            if (operatorName.length <= 0) {
                contentsWarning.push('请选择作业人');
            }
            if (this.storageAllocatDatas.length <= 0) {
                contentsWarning.push('储位分配单产品明细不能为空');
            }

            for (let i = 0, l = this.storageAllocatDatas.length; i < l; i += 1) {
                const item = this.storageAllocatDatas[i];
                if (!(item.quantity > 0 && item.location_inner_sn && item.location_inner_sn.length > 0)) {
                    contentsWarning.push('应放数量/存储位置不能为空');
                    break;
                }
            }

            for (let i = 0, l = this.storageAllocatDatas.length; i < l; i += 1) {
                const item = this.storageAllocatDatas[i];
                if (item.real_quantity !== 0 && item.quantity < item.real_quantity) {
                    contentsWarning.push('实放数量不为零时，应放数量不能小于实放数量');
                    break;
                }
            }

            if (contentsWarning.length > 0) {
                this.$warning({
                    title: '操作提示',
                    content: `${contentsWarning.join('；')}。`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            return true;
        },
        storageAllocatTableCreateObjFn() {
            // 新建一个对象用于做最后的参照 应放数量是否一致
            // 前端是否为空的格式校验
            if (!this.storageAllocatTableCheckFn()) return;
            const newObj = {};
            let isNumFlag = true; // 应放数量是否大于旧值的应放数量
            // 新数据的应放数量
            this.storageAllocatDatas.forEach((item, index) => {
                if (!newObj[item.key]) {
                    newObj[item.key] = {
                        quantity: item.quantity,
                        index
                    };
                } else {
                    newObj[item.key] = {
                        quantity: new Big(newObj[item.key].quantity).plus(item.quantity).toString(),
                        index
                    };
                }
            });
            if (this.modalmask && this.stoType === 0) {
                for (let i = 0; i < this.storageAllocatOldDatas.length; i++) {
                    const oldItem = this.storageAllocatOldDatas[i];
                    for (let key in newObj) {
                        const newItem = newObj[key];
                        if (parseFloat(oldItem.key) === parseFloat(key)) {
                            if (parseFloat(newItem.quantity) > parseFloat(oldItem.quantity)) {
                                isNumFlag = false;
                                this.$warning({
                                    title: '操作提示',
                                    content: `【${oldItem.product_name}】【${oldItem.specification}】应放数量不能大于已入库数量【${oldItem.quantity}】`,
                                    okText: '知道了',
                                    centered: true,
                                    maskClosable: false
                                });
                                break;
                            }
                        }
                    }
                }
            } else {
                for (let i = 0; i < this.stoQuaryNum.length; i++) {
                    let quItem = this.stoQuaryNum[i];
                    let soNum = new Big(0);
                    this.storageAllocatDatas.forEach((allItem) => {
                        if (quItem.inventory_inner_sn === allItem.inventory_inner_sn) {
                            soNum = soNum.plus(allItem.quantity);
                        }
                    });
                    soNum = soNum.toNumber();
                    if (quItem.quantity < soNum) {
                        isNumFlag = false;
                        this.$warning({
                            title: '操作提示',
                            content: `【${quItem.product_name}】【${quItem.specification}】应放数量不能大于已入库数量【${quItem.quantity}】`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                    }
                }
            }
            return isNumFlag;
        },
        clickLocationFn(record, seq) {
            record.seq = seq;
            this.recordLocation = record;
        },
        changeLocationFn(value, selectedOptions) {
            let locationMsg = ''; // this.addInfoModalForm.warehouse_name
            selectedOptions.forEach((item) => {
                locationMsg += ` ${item.label}`;
            });
            const tempLabel = selectedOptions[selectedOptions.length - 1].label,
                curItem = {
                    ...this.recordLocation,
                    location_inner_sn: value,
                    location_msg: locationMsg,
                    location: tempLabel,
                    location_name: tempLabel
                },
                xStorageAllocatTable = this.$refs.xStorageAllocatTable,
                index = this.recordLocation.seq - 1;
            this.storageAllocatDatas.splice(index, 1, curItem);
            xStorageAllocatTable.reloadRow(curItem, this.recordLocation, 'location');
            xStorageAllocatTable.setCurrentRow(this.storageAllocatDatas[index]);
        },
        cellClickFn({ row }) {
            this.currentTrObj = row;
        },
        cellDblclickFn({ row }) {
            this.currentTrObj = row;
            this.groupBtnFn('edit');
        },
        generateBarcodeModalBotFn(btnName) {
            const btnActionName = {
                'print': () => {
                    const selectChildRecords = this.$refs.generateBarcode.$refs.xBarcodeTable.getSelectRecords();
                    if (selectChildRecords.length <= 0) {
                        this.$warning({
                            title: '操作提示',
                            content: '请先勾选需要打印的条码。',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                    } else {
                        console.log(`当前要打印的是：${JSON.stringify(selectChildRecords)}`);
                    }
                },
                'close': () => {
                    this.generateBarcodeModal.alert = false;
                },
                'save': () => {
                    this.generateBarcodeModal.loading = true;
                    this.$refs.generateBarcode.grnBarcodeSaveAPIFn();
                }
            };
            btnActionName[btnName]();
        },
        saveBarcodeEmitFn(isSaveBarcodeFlag) {
            this.generateBarcodeModal.loading = false;
            if (isSaveBarcodeFlag) {
                this.generateBarcodeModal.alert = false;
                for (let i = 0; i < this.addInfoDatas.length; i++) {
                    const item = this.addInfoDatas[i];
                    if (item._XID === this.barcodeChildObj._XID) {
                        const curItem = {
                                ...item,
                                hasBarcode: true
                            },
                            xTableAddInfo = this.$refs.xTableAddInfo;
                        this.addInfoDatas.splice(i, 1, curItem);
                        xTableAddInfo.refreshData();
                        xTableAddInfo.loadData(this.addInfoDatas);
                        xTableAddInfo.setCurrentRow(curItem);
                        break;
                    }
                }
            }
        },
        // 扫码入库
        async scanQRcode() {
            let code = '';
            document.onkeydown = (e) => {
                let nextTime = new Date().getTime();
                let lastTime = this.lastTime;
                const arrCode = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
                if (arrCode.includes(e.key)) {
                    nextTime = new Date().getTime();
                    if (nextTime && lastTime && nextTime - lastTime > 1000) {
                        code = e.key;
                    } else {
                        code = code + e.key;
                    }
                }
                this.lastTime = nextTime;
                const isCodeMinLen = code.length >= 31;
                if ((e.key === 'Enter' && isCodeMinLen && this.isSheel) || isCodeMinLen) {
                    code = code.replace(/\s*/g, '');
                    this.getScanCode(code);
                    code = '';
                }
            };
        },
        // 根据条码获取信息并添加
        async getScanCode(code) {
            this.addInfoModal.saveLoading = false;
            if (code === '') {
                return false;
            }
            const params = {
                barcode: code,
                org_id: this.$vueCookie.get('userbelong')
            };

            await barcodeScanAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const info = res.info;
                    info.produce_date = info.produce_date && moment(info.produce_date);
                    info.overdue_date = info.overdue_date && moment(info.overdue_date);
                    info.vendor_name = info.product_vendor_name;
                    info.quantity = info.surplus_product_number;
                    info.quantity_old = info.quantity;
                    info.type = info.type || '0';
                    info.unit_price_text = 0;
                    info.isLoadingUnit = false; // 是否加载过整棵树
                    info.vendor_inner_sn = info.product_vendor_inner_sn;
                    info.unitList = info.unit_inner_sn ? [{ value: info.unit_inner_sn, label: info.unit_name }] : [];
                    info.unit_inner_sn = info.unit_inner_sn ? [info.unit_inner_sn] : [];
                    info.mdrfList = info.mdrf_sn
                        ? [{ value: info.mdrf_inner_sn, title: info.mdrf_sn, label: info.mdrf_sn }]
                        : [];
                    info.codeList = [];
                    info.codes = [info.code];
                    info.action = 1;
                    if (
                        !this.addInfoModalForm.organization_inner_sn ||
                        this.addInfoModalForm.organization_inner_sn === ''
                    ) {
                        // 判断机构
                        this.addInfoModalForm.organization_name = info.organization_name;
                        this.addInfoModalForm.organization_inner_sn = info.organization_inner_sn;
                        this.addInfoModalForm.organization_type = info.organization_type;
                        this.addInfoModalForm.billsType = info.billsType;
                    } else {
                        if (
                            info.organization_inner_sn + '' + info.organization_type !==
                            this.addInfoModalForm.organization_inner_sn + '' + this.addInfoModalForm.organization_type
                        ) {
                            this.$warning({
                                title: '操作提示',
                                content: '请录入当前单位名称的产品',
                                okText: '知道了',
                                centered: true,
                                maskClosable: false
                            });
                            return false;
                        }
                    }
                    let gid = info.pp_detail_inner_sn + '' + info.product_type;

                    if (this.addInfoDatas.length === 0 || this.firstScan) {
                        info.codeList.push(info.code);
                        this.addInfoDatas.push(info);
                    } else {
                        let st = true;
                        this.addInfoDatas.forEach((item) => {
                            let oldGid = item.pp_detail_inner_sn + '' + item.product_type;
                            if (oldGid === gid && info.barcode_type === item.barcode_type) {
                                st = false;
                                if (info.barcode_type === 1) {
                                    let flag = true;
                                    item.codeList.forEach((obj) => {
                                        if (info.code === obj) {
                                            flag = false;
                                            this.$warning({
                                                title: '操作提示',
                                                content: '多条码产品不能重复录入',
                                                okText: '知道了',
                                                centered: true,
                                                maskClosable: false
                                            });
                                            return false;
                                        }
                                    });
                                    if (flag) {
                                        item.codeList.push(info.code);
                                        item.quantity = this.$Utils.commafy(
                                            new Big(info.surplus_product_number).plus(item.quantity).round(3).toNumber()
                                        );
                                        item.codes.push(info.code);
                                    }
                                }
                            }
                        });
                        if (st) {
                            if (info.barcode_type === 1) {
                                info.codeList.push(info.code);
                            }
                            this.addInfoDatas.push(info);
                        }
                    }
                    this.firstScan = false;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        onSelectChange(selectedRowKeys) {
            this.billLadingBotSelectedRowKeys = selectedRowKeys;
            if (this.$refs.sheelTable) {
                this.$refs.sheelTable.rowSelection.selectedRowKeys = selectedRowKeys;
            }
            if (this.$refs.sheelTableAdd) {
                this.$refs.sheelTableAdd.rowSelection.selectedRowKeys = selectedRowKeys;
            }
        },
        // 获取医保编号/阳光流水号下拉
        async getProductSnAPIFn(row, seq) {
            if (this.isEditDisabled || this.c_IsGrn) {
                return;
            }
            this.addInfoDatas[seq - 1].healthSnData = [];
            let params = {
                vendor_inner_sn: row.vendor_inner_sn,
                product_inner_sn: row.product_inner_sn,
                specification_inner_sn: row.specification_inner_sn,
                billsType: this.addInfoModalForm.billsType,
                organization_inner_sn: this.addInfoModalForm.organization_inner_sn
            };
            await productSnListAPI(params).then((res) => {
                let { list } = res;
                this.addInfoDatas[seq - 1].healthSnData = list.map((item, index) => {
                    // 1. 最新的医保编号值 is_default 为 true 的医保编号的值
                    if (item.is_default && !row.health_care_sn) {
                        row.health_care_sn = item.sn;
                    }

                    return {
                        sn: item.sn,
                        sn_inner_sn: item.sn_inner_sn,
                        is_default: item.is_default,
                        index: index,
                        type: item.type,
                        vendor_inner_sn: item.vendor_inner_sn,
                        product_inner_sn: item.product_inner_sn,
                        specification_inner_sn: item.specification_inner_sn
                    };
                });

                // 2. 最新的医保编号值 is_default 都为 false，那就默认取第一个医保编号的值
                if (!row.health_care_sn) {
                    row.health_care_sn = list[0].sn;
                }

                this.$refs.xTableAddInfo.updateData();
            });
        },
        // 获取医保编号输入
        healthCareSnChangeFn(item, seq) {
            this.addInfoDatas[seq - 1].health_care_sn = item.sn;
            this.$refs.xTableAddInfo.updateData();
        },
        footerCellClassNameFn({ column }) {
            const redAry = ['money'];
            if (redAry.some((item) => item === column.property)) {
                return 'red bold';
            }
            if (column.property === 'index') {
                return 'total-bg bold';
            }
        },
        footerMethod({ columns }) {
            return [
                columns.map((column, columnIndex) => {
                    if (columnIndex === 0) {
                        return '合计';
                    }
                    if (column.property === 'money') {
                        return this.tax_amount_total;
                    }
                })
            ];
        },
        getTaxAmountTotal(addInfos = []) {
            this.tax_amount_total = this.$Utils.commafy(
                addInfos
                    .reduce((acc, cur) => {
                        if (typeof cur.quantity === 'number' && typeof cur.tax_unit_price === 'number') {
                            return acc.plus(new Big(cur.quantity).times(cur.tax_unit_price));
                        }
                        if (typeof cur.quantity === 'number' && typeof cur.money === 'number') {
                            // const rateMoney = parseFloat(cur.money)
                            return acc.plus(cur.money);
                        }
                        return acc;
                    }, new Big(0))
                    .round(2)
                    .toNumber()
            );
        },
        // 含税单价change
        handleTaxUnitPriceChange(row, actionTypeName, seq) {
            // 验证是否输入为无效数字
            const isValid = (v) => {
                return typeof v !== 'undefined' && v !== null && v !== '';
            };
            // 输入验证并转换为本地浮点数
            let taxUnitPrice = isValid(row.tax_unit_price) ? row.tax_unit_price : 0,
                money = isValid(row.money) ? row.money : 0,
                quantity = isValid(row.quantity) ? row.quantity : 0;
            // 是否是销售退货/采购退货单
            let isReturnSheet =
                this.orderTypeData.value === grnAndOdoOrderType['PURCHASERETURN'].value ||
                this.orderTypeData.value === grnAndOdoOrderType['SALERETURN'].value;

            if (quantity) {
                switch (actionTypeName) {
                    // ---- 含税单价 --------------------------------------------------------
                    case 'tax_unit_price': {
                        // 含税单价在任何情况下都必须为正数
                        row.tax_unit_price = taxUnitPrice < 0 ? 0 : taxUnitPrice;
                        // 为含税金额赋值
                        row.money = new Big(taxUnitPrice).times(quantity).toNumber();
                        break;
                    }
                    // ---- 含税金额 --------------------------------------------------------
                    case 'money': {
                        // 含税金额在销售退货/采购退货中必须是负数
                        // 在其他单据中必须是正数
                        if (isReturnSheet) {
                            row.money = money > 0 ? 0 : money;
                        } else {
                            row.money = money < 0 ? 0 : money;
                        }

                        // 为含税单价赋值
                        if (row.money === 0) {
                            row.tax_unit_price = 0;
                        } else {
                            row.tax_unit_price = new Big(money).div(quantity).toNumber();
                        }
                        break;
                    }
                    // ---- 数量 -------————-------------------------------------------------
                    case 'quantity': {
                        // 数量在销售退货/采购退货中必须是负数
                        // 在其他单据中必须是正数
                        if (isReturnSheet) {
                            row.quantity = quantity > 0 ? 0 : quantity;
                        } else {
                            row.quantity = quantity < 0 ? 0 : quantity;
                        }

                        // 为含税金额赋值
                        if (row.quantity === 0) {
                            row.money = 0;
                        } else {
                            row.money = new Big(taxUnitPrice).times(quantity).toNumber();
                        }
                        break;
                    }
                }
            }

            if (row.average_price && row.min_unit_info) {
                if (seq - 1 < 0) {
                    seq = 0;
                } else {
                    seq -= 1;
                }
                this.addInfoDatas[seq].average_price_compare = new Big(row.average_price)
                    .times(row.min_unit_info)
                    .toNumber();
                this.$set(this.addInfoDatas, seq, { ...this.addInfoDatas[seq] });
            }

            this.updataTable(row, actionTypeName, seq);
            this.getTaxAmountTotal(this.addInfoDatas);
        },

        // 导出Excel
        async handleExportExcel() {
            if (this.exporting) {
                return;
            }
            this.exporting = true;
            const hideMessage = this.$message.loading('导出中...');
            try {
                const service = this.c_IsGrn ? exportFinanceInboundListAPI : exportFinanceOutboundListAPI;
                const formData = {
                    org_id: this.userBelong,
                    ...this.searchTables,
                    sn: this.searchTables.grn_odo_sn,
                    organization_name: this.searchTables.key
                };
                const { data, headers } = await service(formData);
                const contentDisposition = headers['content-disposition'] || '';
                const matchedFilenames = contentDisposition.match(/fileName=(.*)/);
                const filename =
                    matchedFilenames.length > 1
                        ? decodeURIComponent(matchedFilenames[1])
                        : `${moment().format('YYYY-MM-DD')}.xlsx`;
                this.$Utils.download(data, filename);
            } catch (error) {
                console.error(error);
            } finally {
                this.exporting = false;
                hideMessage();
            }
        },

        /* 出入库接口拆分区域开始 */
        /// 加载数据列表
        getSearchListFn(params) {
            params.isAsc = 'desc'; // 排序条件 01

            /// 入库单
            if (this.c_IsGrn) {
                params.orderByColumn = 'grn_sn'; // 排序条件 02
                if (this.searchTables.grn_odo_sn) {
                    params.grn_sn = this.searchTables.grn_odo_sn;
                }
            } else {
                /// 出库单
                params.orderByColumn = 'odo_sn'; // 排序条件 02
                if (this.searchTables.grn_odo_sn) {
                    params.odo_sn = this.searchTables.grn_odo_sn;
                }
            }

            switch (this.orderTypeData.value) {
                case grnAndOdoOrderType['PURCHASEGRN'].value: {
                    return purchaseGrnListAPI({ params });
                }
                case grnAndOdoOrderType['PURCHASERETURN'].value: {
                    return purchaseReturnListAPI({ params });
                }
                case grnAndOdoOrderType['ALLOTGRN'].value: {
                    return allotGrnListAPI({ params });
                }
                case grnAndOdoOrderType['SALEODO'].value: {
                    return saleOdoListAPI({ params });
                }
                case grnAndOdoOrderType['SALERETURN'].value: {
                    return saleReturnListAPI({ params });
                }
                case grnAndOdoOrderType['ALLOTODO'].value: {
                    return allotOdoListAPI({ params });
                }
                case grnAndOdoOrderType['GRANTODO'].value: {
                    return grantOdoListAPI({ params });
                }
                case grnAndOdoOrderType['GIVEODO'].value: {
                    return giveOdoListAPI({ params });
                }
            }
        },
        // 【搜索】出入库单列表
        async searchListAPIFn(grnInnerSn) {
            const typeNameFlag = typeof grnInnerSn === 'object'; // 详见新增
            this.godownEntryPagination.loading = true;
            let params = {
                org_id: this.userBelong,
                pageNum: this.sheetPagination.pageNum,
                pageSize: this.sheetPagination.pageSize,
                key: this.searchTables.key
            };

            // 判断是否是高级搜索
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                if (this.searchTables.handler_name) {
                    params.handler_name = this.searchTables.handler_name;
                }

                if (this.searchTables.warehouse_model_inner_sn) {
                    params.warehouse_model_inner_sn = this.searchTables.warehouse_model_inner_sn;
                }

                if (this.searchTables.status) {
                    params.status = this.searchTables.status;
                }

                if (this.searchTables.begin_date) {
                    params.begin_date = this.searchTables.begin_date;
                }

                if (this.searchTables.end_date) {
                    params.end_date = this.searchTables.end_date;
                }

                if (this.searchTables.key) {
                    params.key = this.searchTables.key;
                }

                if (this.searchTables.product_name) {
                    params.product_name = this.searchTables.product_name;
                }
            }

            await this.getSearchListFn(params)
            .then((res) => {
                this.godownEntryPagination.loading = false;
                if (parseFloat(res.code) === 0) {
                    const { rows, total } = res;
                    let isAdd = false;
                    let startIdx = this.sheetPagination.pageSize * (this.sheetPagination.pageNum - 1);
                    this.calcPagination(total);
                    let data = rows.map((item, index) => {
                        let curItem = {
                            ...item,
                            idx: startIdx + index + 1,
                            index,
                            activeClass: '',
                            status_string: this.grnStatus[parseFloat(item.status)],
                            in_time: item.in_time || item.out_time
                        };
                        if (item.status === purchaseGrnOrderStatusMap.CANCEL.value || item.status === purchaseGrnOrderStatusMap.RED.value) {
                            curItem.style = {
                                color: '#ea6e70'
                            };
                        }
                        if (item.grn_inner_sn) {
                            curItem.key = `${item.grn_inner_sn}${item.billsType}`;
                        } else {
                            curItem.key = `${item.odo_inner_sn}${item.billsType}`;
                            curItem.grn_sn = item.odo_sn; // 为了兼容出库单
                        }
                        if (typeNameFlag) {
                            if (grnInnerSn.curItem.key === curItem.key) {
                                this.currentTrObj = curItem;
                                isAdd = true;
                            }
                        } else {
                            if (grnInnerSn && curItem.key === grnInnerSn) {
                                this.currentTrObj = curItem;
                            }
                        }
                        return curItem;
                    });
                    this.addData.push(...data);

                    if (typeNameFlag) {
                        if (!isAdd) {
                            this.godownEntryDatas.splice(this.godownEntryDatas.length - 1, 1, grnInnerSn.curItem);
                            this.currentTrObj = grnInnerSn.curItem;
                        }
                    } else {
                        if (!grnInnerSn) {
                            this.currentTrObj = this.godownEntryDatas[0];
                        }
                    }

                    if (this.currentTrObj && Object.keys(this.currentTrObj).length <= 0) {
                        this.currentTrObj = this.godownEntryDatas[0];
                    }
                    this.godownEntryPagination = {
                        ...this.godownEntryPagination,
                        total,
                        loading: false
                    };
                } else {
                    this.$message.error(res.msg);
                }
            }).finally(() => {
                this.godownEntryPagination.loading = false;
            });
        },
        grnAndOdoAddFn(params) {
            /// 出库单
            if (!this.c_IsGrn && this.isSheel && this.sheelID === 'GRANT') {
                params.billsType = 'HH';
            } else {
                params.billsType = this.addInfoModalForm.billsType;
            }

            if (!this.c_IsGrn && (params.billsType === 'DH' || params.billsType === 'VH')) {
                params.use_department = this.addInfoModalForm.use_department;
                params.use_department_old = this.addInfoModalForm.use_department_old;
            }

            switch (this.orderTypeData.value) {
                case grnAndOdoOrderType['PURCHASEGRN'].value: {
                    return purchaseGrnAddAPI(params);
                }
                case grnAndOdoOrderType['PURCHASERETURN'].value: {
                    return purchaseReturnAddAPI(params);
                }
                case grnAndOdoOrderType['ALLOTGRN'].value: {
                    return allotGrnAddAPI(params);
                }
                case grnAndOdoOrderType['SALEODO'].value: {
                    return saleOdoAddAPI(params);
                }
                case grnAndOdoOrderType['SALERETURN'].value: {
                    return saleReturnAddAPI(params);
                }
                case grnAndOdoOrderType['ALLOTODO'].value: {
                    return allotOdoAddAPI(params);
                }
                case grnAndOdoOrderType['GRANTODO'].value: {
                    return grantOdoAddAPI(params);
                }
                case grnAndOdoOrderType['GIVEODO'].value: {
                    return giveOdoAddAPI(params);
                }
            }
        },
        // 【新增】出入库单
        async grnAndOdoAddAPIFn(status) {
            // debugger
            const _this = this;
            let warehouseName = '';
            if (!this.addInfoModalForm.warehouse_name && this.addInfoModalForm.warehouse_inner_sn) {
                warehouseName = this.wareHouseFilterData.find(
                    (e) => e.value === _this.addInfoModalForm.warehouse_inner_sn
                ).title;
            } else {
                warehouseName = this.addInfoModalForm.warehouse_name;
            }

            let params = {
                org_id: this.userBelong,
                organization_inner_sn: this.addInfoModalForm.organization_inner_sn,
                organization_name: this.addInfoModalForm.organization_name,
                status,
                handler_inner_sn: this.addInfoModalForm.handler_inner_sn,
                handler_name: this.addInfoModalForm.handler_name,
                type_inner_sn: this.addInfoModalForm.type_inner_sn,
                type_name: this.addInfoModalForm.type_name,
                warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                warehouse_name: warehouseName
            };
            if (this.addInfoModalForm.department_inner_sn) {
                params.department_inner_sn = this.addInfoModalForm.department_inner_sn;
                params.department_name = this.addInfoModalForm.department_name;
            }
            if (this.$Utils.trim(this.addInfoModalForm.description)) {
                params.description = this.addInfoModalForm.description;
            }
            if (this.addInfoModalForm.warehouse_operator_inner_sn) {
                params.warehouse_operator_inner_sn = this.addInfoModalForm.warehouse_operator_inner_sn;
                params.warehouse_operator_name = this.addInfoModalForm.warehouse_operator_name;
            }
            if (this.orderTypeData.value === grnAndOdoOrderType['PURCHASEGRN'].value) {
                params.buyer_name = this.addInfoModalForm.buyer_name;
            }

            params.details = [];
            let data = this.getStableData();
            data.forEach((item, index) => {
                // 注册证号
                let mdrfInnerSn = item.mdrf_inner_sn;
                if (typeof mdrfInnerSn === 'undefined' || (mdrfInnerSn && mdrfInnerSn.length <= 0)) {
                    item.mdrf_inner_sn = '';
                    item.mdrf_sn = '';
                }
                if (Array.isArray(mdrfInnerSn)) {
                    item.mdrf_inner_sn = mdrfInnerSn.length ? mdrfInnerSn[mdrfInnerSn.length - 1] : '';
                }

                // 调单
                let temporaryInsertObj = {};
                if (this.isSheel) {
                    temporaryInsertObj.voucher_detail_inner_sn_c =
                        item.voucher_detail_inner_sn_c || item.detail_inner_sn || '';
                    if (typeof item.sl_inner_sn !== 'undefined' || typeof item.gv_inner_sn !== 'undefined') {
                        temporaryInsertObj.voucher_inner_sn_c =
                            this.sheelID === 'GRANT' ? item.gv_inner_sn : item.sl_inner_sn; // 'GRANT' => gv_inner_sn   'SALE_RECORD' => sl_inner_sn 杨叔说暂时只有这两种类型
                        temporaryInsertObj.voucher_type_c = this.sheelID;
                    } else {
                        temporaryInsertObj.voucher_inner_sn_c = item.voucher_inner_sn_c;
                        temporaryInsertObj.voucher_type_c = item.voucher_type_c;
                    }
                } else {
                    temporaryInsertObj.voucher_type_c = 'NONE';
                }
                if (item.product_inner_sn) {
                    let buyingPrice = item.buying_price;
                    // 销售退货单的buying_price提交值以用户输入值或调单中的值为准，不再优先取批号里的均价
                    if (this.orderTypeData.value !== grnAndOdoOrderType.SALERETURN.value) {
                        buyingPrice = item.average_price || item.buying_price || '';
                    }
                    params.details.push({
                        'action': 1, // 接口加入出库单时新增字段
                        'buying_price': buyingPrice,
                        'batch_number': item.batch_number.trim(),
                        'batch_number_old': item.batch_number, // 接口加入出库单时新增字段
                        'detail_inner_sn': item.detail_inner_sn || '',
                        'mdrf_inner_sn': item.mdrf_inner_sn || '',
                        'mdrf_sn': item.mdrf_sn || '',
                        'overdue_date': item.overdue_date ? moment(item.overdue_date).format(this.format) : '',
                        'produce_date': item.produce_date ? moment(item.produce_date).format(this.format) : '',
                        'product_inner_sn': item.product_inner_sn,
                        'product_name': item.name,
                        'product_type': item.product_type,
                        'product_vendor_inner_sn':
                            item.vendor_inner_sn ||
                            item.product_vendor_inner_sn ||
                            item.dealer_inner_sn ||
                            item.product_dealer_inner_sn,
                        'product_vendor_name':
                            item.vendor_name ||
                            item.product_vendor_name ||
                            item.dealer_name ||
                            item.product_dealer_name,
                        'quantity': item.quantity,
                        'quantity_old': item.quantity, // 接口加入出库单时新增字段
                        'sort_number': index,
                        'specification': item.specification,
                        'specification_inner_sn': item.specification_inner_sn,
                        'type': item.type,
                        'unit_inner_sn': Array.isArray(item.unit_inner_sn)
                            ? item.unit_inner_sn[item.unit_inner_sn.length - 1] || ''
                            : item.unit_inner_sn,
                        'unit_inner_sn_old': Array.isArray(item.unit_inner_sn)
                            ? item.unit_inner_sn[item.unit_inner_sn.length - 1] || ''
                            : item.unit_inner_sn, // 接口加入出库单时新增字段
                        // 'unit_inner_sn_old': item.unit_inner_sn.length ? item.unit_inner_sn[item.unit_inner_sn.length - 1] : '', // 接口加入出库单时新增字段
                        'unit_name': item.unit_name,
                        'unit_price_text': item.tax_unit_price,
                        'rate': item.rate / 100,
                        'health_care_sn': item.health_care_sn,
                        ...temporaryInsertObj
                    });
                }
            });

            params.details.forEach((item) => {
                if (item.produce_date === '' || item.produce_date === 'Invalid date') {
                    delete item.produce_date;
                }

                if (!this.c_IsGrn) {
                    delete item.action;
                }
            });

            await this.grnAndOdoAddFn(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.godownEntryPagination.total++;
                        const { info } = res;
                        // 不是红冲的新增插入
                        if (!this.isClickRedDashedOrder) {
                            this.changeTabFn('history');
                        } else {
                            this.$warning({
                                title: '操作提示',
                                content: `红冲单已经生成，单号：${info.odo_sn}，请前往“出库”页面确认红冲单无误后，进行出库操作以完成红冲。`,
                                okText: '知道了',
                                centered: true,
                                maskClosable: true
                            });
                            this.resetAddInfoModalFn();
                        }
                        this.isSheel = false; // 调单标识重置
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch((err) => {
                    const response = err.response;
                    if (response && response.data) {
                        this.beyondInventoryModalFn(response.data.message);
                    }
                })
                .finally(() => {
                    this.addInfoModal.saveLoading = false;
                    this.addInfoModal.auditLoading = false;
                    this.addInfoModal.godownEntryLoading = false;
                });
        },
        grnAndOdoDelFn(params, curObj) {
            if (this.c_IsGrn) {
                params.grn_inner_sn = curObj.grn_inner_sn;
            } else {
                params.odo_inner_sn = curObj.odo_inner_sn;
            }

            switch (this.orderTypeData.value) {
                case grnAndOdoOrderType['PURCHASEGRN'].value: {
                    return purchaseGrnDelAPI(params);
                }
                case grnAndOdoOrderType['PURCHASERETURN'].value: {
                    return purchaseReturnDelAPI(params);
                }
                case grnAndOdoOrderType['ALLOTGRN'].value: {
                    return allotGrnDelAPI(params);
                }
                case grnAndOdoOrderType['SALEODO'].value: {
                    return saleOdoDelAPI(params);
                }
                case grnAndOdoOrderType['SALERETURN'].value: {
                    return saleReturnDelAPI(params);
                }
                case grnAndOdoOrderType['ALLOTODO'].value: {
                    return allotOdoDelAPI(params);
                }
                case grnAndOdoOrderType['GRANTODO'].value: {
                    return grantOdoDelAPI(params);
                }
                case grnAndOdoOrderType['GIVEODO'].value: {
                    return giveOdoDelAPI(params);
                }
            }
        },
        // 【删除】出入库单
        async grnAndOdoDelAPIFn() {
            const curObj = this.$refs.xTable.getCurrentRow(),
                params = {
                    org_id: curObj.org_id || this.userBelong,
                    billsType: curObj.billsType
                };

            await this.grnAndOdoDelFn(params, curObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.searchListAPIFn();
                    this.editGrnTypeFlag = false;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        grnAndOdoEditFn(params) {
            if (this.c_IsGrn) {
                params.grn_inner_sn = this.addInfoModalForm.grn_inner_sn;
                params.grn_sn = '';
                params.grn_sn_old = '';
            } else {
                params.odo_inner_sn = this.addInfoModalForm.odo_inner_sn;
                params.odo_sn = '';
                params.odo_sn_old = '';
                if (this.addInfoModalForm.billsType === 'DH' || this.addInfoModalForm.billsType === 'VH') {
                    params.use_department = this.addInfoModalForm.use_department;
                    params.use_department_old = this.addInfoModalForm.use_department_old;
                }
            }

            switch (this.orderTypeData.value) {
                case grnAndOdoOrderType['PURCHASEGRN'].value: {
                    return purchaseGrnEditAPI(params);
                }
                case grnAndOdoOrderType['PURCHASERETURN'].value: {
                    return purchaseReturnEditAPI(params);
                }
                case grnAndOdoOrderType['ALLOTGRN'].value: {
                    return allotGrnEditAPI(params);
                }
                case grnAndOdoOrderType['SALEODO'].value: {
                    return saleOdoEditAPI(params);
                }
                case grnAndOdoOrderType['SALERETURN'].value: {
                    return saleReturnEditAPI(params);
                }
                case grnAndOdoOrderType['ALLOTODO'].value: {
                    return allotOdoEditAPI(params);
                }
                case grnAndOdoOrderType['GRANTODO'].value: {
                    return grantOdoEditAPI(params);
                }
                case grnAndOdoOrderType['GIVEODO'].value: {
                    return giveOdoEditAPI(params);
                }
            }
        },
        // 【编辑】出入库单
        async grnAndOdoEditAPIFn(status) {
            const params = {
                department_inner_sn: this.addInfoModalForm.department_inner_sn,
                department_inner_sn_old: this.editInfoFormObj.department_inner_sn_old,
                department_name: this.addInfoModalForm.department_name,
                department_name_old: this.editInfoFormObj.department_name_old,
                description: this.addInfoModalForm.description,
                description_old: this.editInfoFormObj.description_old,
                handler_inner_sn: this.addInfoModalForm.handler_inner_sn,
                handler_inner_sn_old: this.editInfoFormObj.handler_inner_sn_old,
                handler_name: this.addInfoModalForm.handler_name,
                handler_name_old: this.editInfoFormObj.handler_name_old,
                buyer_name: this.addInfoModalForm.buyer_name,
                billsType: this.addInfoModalForm.billsType,
                billsType_old: this.editInfoFormObj.billsType_old,
                org_id: this.addInfoModalForm.org_id || this.userBelong,
                organization_inner_sn: this.addInfoModalForm.organization_inner_sn,
                organization_inner_sn_old: this.editInfoFormObj.organization_inner_sn_old,
                organization_name: this.addInfoModalForm.organization_name,
                organization_name_old: this.editInfoFormObj.organization_name_old,
                status,
                type_inner_sn: this.addInfoModalForm.type_inner_sn,
                type_inner_sn_old: this.editInfoFormObj.type_inner_sn_old,
                type_name: this.addInfoModalForm.type_name,
                type_name_old: this.editInfoFormObj.type_name_old,
                warehouse_inner_sn: this.addInfoModalForm.warehouse_inner_sn,
                warehouse_inner_sn_old: this.editInfoFormObj.warehouse_inner_sn_old,
                warehouse_name: this.addInfoModalForm.warehouse_name,
                warehouse_name_old: this.editInfoFormObj.warehouse_name_old,
                warehouse_operator_inner_sn: this.addInfoModalForm.warehouse_operator_inner_sn,
                warehouse_operator_inner_sn_old: this.editInfoFormObj.warehouse_operator_inner_sn_old,
                warehouse_operator_name: this.addInfoModalForm.warehouse_operator_name,
                warehouse_operator_name_old: this.editInfoFormObj.warehouse_operator_name_old
            };
            let newArr = [];
            this.addInfoDatas.forEach((item, index) => {
                if (item.product_inner_sn) {
                    if (item.isOld) {
                        if (
                            item.batch_number !== item.batch_number_old ||
                            item.quantity_old !== item.quantity ||
                            item.unit_inner_sn_old !== item.unit_inner_sn ||
                            item.unit_price_old !== item.unit_price_text ||
                            item.rate_old !== item.rate ||
                            item.produce_date_old !== item.produce_date ||
                            item.overdue_date_old !== item.overdue_date ||
                            item.type_old !== item.type ||
                            item.mdrf_inner_sn_old !== item.mdrf_inner_sn ||
                            item.health_care_sn_old !== item.health_care_sn
                        ) {
                            item.action = 2; // 修改状态
                        }
                    }
                    if (this.isSheel) {
                        item.name = item.product_name; // 修复调单的时候name取不到
                    }
                    let mdrfInnerSn = item.mdrf_inner_sn;
                    if (Array.isArray(mdrfInnerSn)) {
                        mdrfInnerSn = mdrfInnerSn.length ? mdrfInnerSn[mdrfInnerSn.length - 1] : '';
                    }

                    let buyingPrice = item.buying_price;
                    // 销售退货单的buying_price提交值以原值为准，不再优先取批号里的均价
                    if (this.orderTypeData.value !== grnAndOdoOrderType.SALERETURN.value) {
                        buyingPrice = item.average_price || item.buying_price || '';
                    }
                    let objItem = {
                        'buying_price': buyingPrice,
                        'detail_inner_sn': item.detail_inner_sn || '',
                        'product_vendor_inner_sn':
                            item.vendor_inner_sn ||
                            item.product_vendor_inner_sn ||
                            item.dealer_inner_sn ||
                            item.product_dealer_inner_sn,
                        'product_vendor_name':
                            item.vendor_name ||
                            item.product_vendor_name ||
                            item.dealer_name ||
                            item.product_dealer_name,
                        'product_inner_sn': item.product_inner_sn,
                        'product_name': item.name,
                        'specification_inner_sn': item.specification_inner_sn,
                        'specification': item.specification,
                        'unit_inner_sn': item.unit_inner_sn.length
                            ? item.unit_inner_sn[item.unit_inner_sn.length - 1]
                            : '',
                        'unit_name': this.$Utils.UnitBracketsTrim(item.unit_name),
                        'mdrf_inner_sn': mdrfInnerSn,
                        'mdrf_sn': item.mdrf_sn || '',
                        'unit_price_text': item.tax_unit_price,
                        'rate': item.rate / 100,
                        'quantity': item.quantity,
                        'produce_date': item.produce_date ? moment(item.produce_date).format(this.format) : '',
                        'overdue_date': item.overdue_date ? moment(item.overdue_date).format(this.format) : '',
                        'type': item.type,
                        'sort_number': index,
                        'batch_number': item.batch_number.trim(),
                        'product_type': item.product_type,
                        'action': item.action, // 接口加入出库单时新增字段
                        // 'quantity_old': item.quantity_old ? item.quantity_old : item.quantity, // 接口加入出库单时新增字段
                        // 'batch_number_old': item.batch_number_old ? item.batch_number_old : '', // 接口加入出库单时新增字段
                        // 'unit_inner_sn_old': item.unit_inner_sn_old && item.unit_inner_sn_old.length ? item.unit_inner_sn_old[item.unit_inner_sn_old.length - 1] : '', // 接口加入出库单时新增字段
                        'voucher_detail_inner_sn_c': item.voucher_detail_inner_sn_c,
                        'voucher_inner_sn_c': item.voucher_inner_sn_c,
                        'voucher_type_c': item.voucher_type_c,
                        'health_care_sn': item.health_care_sn
                    };

                    if (parseInt(objItem.product_type) === 0) {
                        objItem.product_dealer_inner_sn =
                            item.dealer_inner_sn ||
                            item.vendor_inner_sn ||
                            item.product_vendor_inner_sn ||
                            item.product_dealer_inner_sn;
                        objItem.product_dealer_name =
                            item.dealer_name ||
                            item.vendor_name ||
                            item.product_vendor_name ||
                            item.product_dealer_name;
                    } else {
                        objItem.product_vendor_inner_sn =
                            item.dealer_inner_sn ||
                            item.vendor_inner_sn ||
                            item.product_vendor_inner_sn ||
                            item.product_dealer_inner_sn;
                        objItem.product_vendor_name =
                            item.dealer_name ||
                            item.vendor_name ||
                            item.product_vendor_name ||
                            item.product_dealer_name;
                    }

                    if (this.c_IsGrn) {
                        if (item.action >= 0) {
                            newArr.push(objItem);
                        }
                    } else {
                        newArr.push(objItem);
                    }
                }
            });

            if (this.c_IsGrn) {
                params.details = [...newArr, ...this.delInfoDatas];
            } else {
                params.details = newArr;
            }

            params.details.forEach((item) => {
                if (item.produce_date === '' || item.produce_date === 'Invalid date') {
                    delete item.produce_date;
                }

                if (!this.c_IsGrn) {
                    delete item.action;
                }
            });

            await this.grnAndOdoEditFn(params)
                .then((res) => {
                    this.addInfoModal.saveLoading = false;
                    if (parseFloat(res.code) === 0) {
                        // 如果单位名称改变了前端就在旧位置插入即可
                        this.editGrnTypeFlag = params.billsType !== params.billsType_old;
                        this.changeTabFn('history');
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.dblclickAddProductFn();
                })
                .catch((err) => {
                    if (err.response && err.response.data) {
                        this.beyondInventoryModalFn(err.response.data.message);
                    }
                })
                .finally(() => {
                    this.addInfoModal.saveLoading = false;
                    this.addInfoModal.auditLoading = false;
                    this.addInfoModal.godownEntryLoading = false;
                });
        },
        grnAndOdoInfoFn(params, curObj) {
            if (this.c_IsGrn) {
                params.grn_inner_sn = curObj.grn_inner_sn;
            } else {
                params.odo_inner_sn = curObj.odo_inner_sn;
            }

            switch (this.orderTypeData.value) {
                case grnAndOdoOrderType['PURCHASEGRN'].value: {
                    return purchaseGrnInfoAPI({ params });
                }
                case grnAndOdoOrderType['PURCHASERETURN'].value: {
                    return purchaseReturnInfoAPI({ params });
                }
                case grnAndOdoOrderType['ALLOTGRN'].value: {
                    return allotGrnInfoAPI({ params });
                }
                case grnAndOdoOrderType['SALEODO'].value: {
                    return saleOdoInfoAPI({ params });
                }
                case grnAndOdoOrderType['SALERETURN'].value: {
                    return saleReturnInfoAPI({ params });
                }
                case grnAndOdoOrderType['ALLOTODO'].value: {
                    return allotOdoInfoAPI({ params });
                }
                case grnAndOdoOrderType['GRANTODO'].value: {
                    return grantOdoInfoAPI({ params });
                }
                case grnAndOdoOrderType['GIVEODO'].value: {
                    return giveOdoInfoAPI({ params });
                }
            }
        },
        // 【获取详情】出入库单
        async grnAndOdoInfoAPIFn() {
            this.reviewer_name = '';
            this.sheelListData = [];
            this.delInfoDatas = [];
            this.voucher_type_c = 0;
            this.voucher_inner_sn_c = 0;
            this.isLoadingDepartmentInnerSn = true;
            this.isGrnTypeLoading = true;
            const curObj = this.editable.obj;
            const params = {
                org_id: curObj.org_id || this.userBelong,
                billsType: curObj.billsType
            };

            await this.grnAndOdoInfoFn(params, curObj)
                .then((res) => {
                    this.addInfoLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        // if (this.grnOdoOrderIsCopyFn(info.voucher_type_c)) {
                        //     return
                        // }
                        /// 复制当前单据 ===> 将该单据的主状态置为 [0 - '正式']
                        if (this.btnName === 'copy') {
                            info.status = 0;
                            if (info.voucher_type_c !== 'NONE' || !info.voucher_type_c) {
                                this.isSheel = true;
                                this.sheelID = info.voucher_type_c;
                            }
                            this.grnOdoOrderCopyFn();
                        }
                        this.barcodeParentObj = { ...info };
                        info.type_inner_sn = info.type_inner_sn + '';
                        this.making_people_name = info.making_people_name;
                        this.auditor_name = info.auditor_name;
                        this.addInfoModalForm = { ...info };
                        this.addInfoModalForm.organization_name_old = info.organization_name;
                        this.addInfoModalForm.organization_inner_sn_old = info.organization_inner_sn;
                        this.addInfoModalForm.use_department = info.use_department;
                        this.addInfoModalForm.use_department_old = info.use_department;
                        this.voucher_type_c = info.voucher_type_c;
                        this.voucher_inner_sn_c = info.voucher_inner_sn_c;
                        if (typeof info.type_inner_sn !== 'undefined') {
                            this.grnTypeList = [
                                {
                                    name: info.type_name,
                                    label: info.type_name,
                                    title: info.type_name,
                                    status: info.status,
                                    status_text: '无效',
                                    value: info.type_inner_sn
                                }
                            ];
                        }
                        this.warehouseOperatorList = [
                            {
                                title: info.warehouse_operator_name,
                                value: info.warehouse_operator_inner_sn,
                                key: info.warehouse_operator_inner_sn,
                                isLoading: false // 是否加载过
                            }
                        ];
                        if (typeof info.warehouse_inner_sn !== 'undefined') {
                            this.wareHouseFilterData = [
                                {
                                    name: info.warehouse_name,
                                    label: info.warehouse_name,
                                    title: info.warehouse_name,
                                    key: info.warehouse_inner_sn,
                                    value: info.warehouse_inner_sn
                                }
                            ];
                            this.isWareHouseFilterLoading = true;
                        }

                        this.superiorDepartments = [
                            {
                                title: info.department_name,
                                value: info.department_inner_sn,
                                key: info.department_inner_sn
                            }
                        ];
                        // this.warehouseUsableTopListAPIFn(this.addInfoModalForm.warehouse_inner_sn) // 仓库
                        // this.orgDeptTopTreeAPIFn() // 部门
                        // this.grnTypeListAPIFn() // 单据类别
                        this.orgEmpDropdownListAPIFn(); // 经手人
                        // this.grnWeListAPIFn()
                        for (let key in info) {
                            if (key !== 'details') {
                                this.editInfoFormObj[key + '_old'] = info[key];
                            }
                        }
                        let tempDatas = info.details.map((item, index) => {
                            if (item.product_type === 0) {
                                item.mdrf_inner_sn = '';
                                item.mdrf_sn = '';
                            }
                            item.idx = index + 1;
                            item.location = []; // 存储位置 - 储位分配
                            item.location_inner_sn = []; // 存储位置id - 储位分配
                            item.location_msg = ''; // 位置信息 - 储位分配
                            item.real_quantity = 0; // 实放数量 - 储位分配
                            item.key = Math.random();
                            item.index = index;
                            item.focusDatas = [
                                false,
                                index === 0 && parseInt(info.status) <= 0,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false
                            ];
                            item.activeClass = '';
                            item.type = item.type + '';
                            item.produce_date = item.produce_date && moment(item.produce_date);
                            item.overdue_date = item.overdue_date && moment(item.overdue_date);
                            item.name = item.product_name;
                            item.mdrfList = item.mdrf_sn
                                ? [{ value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn }]
                                : [];
                            item.unitList = item.unit_inner_sn
                                ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                : [];
                            item.unit_inner_sn_old = item.unit_inner_sn ? [item.unit_inner_sn] : [];
                            item.unit_inner_sn = item.unit_inner_sn ? [item.unit_inner_sn] : [];
                            item.isLoadingUnit = false; // 是否加载过整棵树
                            item.isOld = true;
                            item.unit_inner_sn_old = item.unit_inner_sn;
                            item.unit_price_old = item.unit_price_text;
                            item.unitPriceList = [];
                            item.rate_old = Math.floor(item.rate * 100);
                            item.produce_date_old = item.produce_date;
                            item.overdue_date_old = item.overdue_date;
                            item.type_old = item.type;
                            item.mdrf_inner_sn_old = item.mdrf_inner_sn;
                            item.voucher_type_c = info.voucher_type_c;
                            item.voucher_inner_sn_c = info.voucher_inner_sn_c;
                            item.health_care_sn_old = item.health_care_sn;
                            item.hospital_inner_sn = info.organization_inner_sn; // 当调单为发放单时，需要这个字段
                            item.gv_inner_sn = info.voucher_inner_sn_c; // 当调单为发放单时，需要这个字段
                            item.grant_quantity = item.quantity; // 需要记录总数量
                            item.quantity_old = item.quantity; // 记录历史数量
                            item.batch_number_old = item.batch_number; // 记录历史批号
                            item.action = -1; // 默认为未操作
                            item.product_vendor_inner_sn = item.product_vendor_inner_sn
                                ? item.product_vendor_inner_sn
                                : item.vendor_inner_sn;
                            item.batch_locked_quantity = 0;
                            item.batchDatas = [];
                            item.tax_unit_price = item.unit_price_text;
                            item.money = parseFloat(item.quantity)
                                ? (item.quantity * item.tax_unit_price).toFixed(4)
                                : '';
                            item.unitPriceList = [];
                            item.inventoryBannce = 0;
                            item.rate = Math.floor(item.rate * 100);
                            item.warehouse_inner_sn = this.addInfoModalForm.warehouse_inner_sn || '';
                            item.max_batch_locked_quantity = 0;
                            if (typeof item.min_unit_info === 'undefined') {
                                item.min_unit_info = 1; // 默认值
                                item.min_measure = 1; // 默认值
                            }
                            item.average_price = 0;
                            item.average_price_compare = 0;
                            // 修复从列表过去的时候，新增产品获取不到 order_type 订单类型 0-DealerDealer 1-DealerVendor 2-hospitalDealer 3-hospitalVendor
                            // 不同的接口名字不一样，order_type === odo_type
                            if (parseFloat(info.voucher_type_c) === 2 || info.voucher_type_c !== 'NONE') {
                                item.sl_inner_sn = info.voucher_inner_sn_c;
                                item.billsType = info.billsType;
                            }

                            if (!this.c_IsGrn) {
                                if (parseInt(info.status) <= 0) {
                                    // 正式
                                    this.contractGetContractPriceAPIFn(item);
                                }
                                this.loadUnitMinFn(item, index);
                            }
                            // 销售退货单重新进行编辑时，产品明细不能编辑
                            if (this.orderTypeData.value === grnAndOdoOrderType.SALERETURN.value) {
                                item.isReadonly = true;
                            }
                            return item;
                        });
                        if (info.voucher_type_c !== 'NONE') {
                            this.isSheel = true;
                            this.sheelID = info.voucher_type_c;
                        }
                        this.addData = tempDatas;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.addInfoLoading = false;
                })
                .finally(() => {
                    this.isCopyLoading = false;
                });
        },
        grnAndOdoRedFn(params, currentTrObj) {
            if (this.c_IsGrn) {
                params.grn_inner_sn = currentTrObj.grn_inner_sn;
            } else {
                params.odo_inner_sn = currentTrObj.odo_inner_sn;
            }

            switch (this.orderTypeData.value) {
                case grnAndOdoOrderType['PURCHASEGRN'].value: {
                    return purchaseGrnRedAPI(params);
                }
                case grnAndOdoOrderType['PURCHASERETURN'].value: {
                    return purchaseReturnRedAPI(params);
                }
                case grnAndOdoOrderType['ALLOTGRN'].value: {
                    return allotGrnRedAPI(params);
                }
                case grnAndOdoOrderType['SALEODO'].value: {
                    return saleOdoRedAPI(params);
                }
                case grnAndOdoOrderType['SALERETURN'].value: {
                    return saleReturnRedAPI(params);
                }
                case grnAndOdoOrderType['ALLOTODO'].value: {
                    return allotOdoRedAPI(params);
                }
                case grnAndOdoOrderType['GRANTODO'].value: {
                    return grantOdoRedAPI(params);
                }
                case grnAndOdoOrderType['GIVEODO'].value: {
                    return giveOdoRedAPI(params);
                }
            }
        },
        // 【红冲】出入库单
        async grnAndOdoRedAPIFn() {
            const params = {
                org_id: this.currentTrObj.org_id,
                billsType: this.currentTrObj.billsType
            };

            await this.grnAndOdoRedFn(params, this.currentTrObj)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        this.grnAndOdoRedDashedFn(info);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.godownEntryPagination.loading = false;
                });
        },
        grnAndOdoUnreviewFn(params, currentTrObj) {
            if (this.c_IsGrn) {
                params.grn_inner_sn = currentTrObj.grn_inner_sn;
            } else {
                params.odo_inner_sn = currentTrObj.odo_inner_sn;
            }

            switch (this.orderTypeData.value) {
                case grnAndOdoOrderType['PURCHASEGRN'].value: {
                    return purchaseGrnUnreviewAPI(params);
                }
                case grnAndOdoOrderType['PURCHASERETURN'].value: {
                    return purchaseReturnUnreviewAPI(params);
                }
                case grnAndOdoOrderType['ALLOTGRN'].value: {
                    return allotGrnUnreviewAPI(params);
                }
                case grnAndOdoOrderType['SALEODO'].value: {
                    return saleOdoUnreviewAPI(params);
                }
                case grnAndOdoOrderType['SALERETURN'].value: {
                    return saleReturnUnreviewAPI(params);
                }
                case grnAndOdoOrderType['ALLOTODO'].value: {
                    return allotOdoUnreviewAPI(params);
                }
                case grnAndOdoOrderType['GRANTODO'].value: {
                    return grantOdoUnreviewAPI(params);
                }
                case grnAndOdoOrderType['GIVEODO'].value: {
                    return giveOdoUnreviewAPI(params);
                }
            }
        },
        // 【反审核】出入库单
        async grnAndOdoUnreviewAPIFn() {
            const params = {
                org_id: this.currentTrObj.org_id,
                billsType: this.currentTrObj.billsType
            };
            await this.grnAndOdoUnreviewFn(params, this.currentTrObj)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.grnAndOdoUnreviewSyncDataFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.godownEntryPagination.loading = false;
                });
        },
        addReturnSearchDataFn(addReturnDatas, platformAddReturnData, searchData) {
            // platformAddReturnData: {'ven': [], 'dea': []}
            const { ven, dea } = platformAddReturnData,
                isAddReturnDataEmpty = addReturnDatas && addReturnDatas.length <= 0, // 采购退货和销售退货产品回传数据
                isPlatformAddReturnDataEmpty = ven.length <= 0 && dea.length <= 0; // 库存产品和平台产品回传数据

            if (isAddReturnDataEmpty && isPlatformAddReturnDataEmpty) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择产品',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }

            // 设置仓库
            if (!this.addInfoModalForm.warehouse_inner_sn && searchData) {
                this.addInfoModalForm.warehouse_inner_sn = searchData.warehouse_inner_sn;
                this.addInfoModalForm.warehouse_name = searchData.warehouse_name;
                this.partB.warehouse_inner_sn = searchData.warehouse_inner_sn;
            }

            if (!isAddReturnDataEmpty) {
                this.clearRowClassNameFn();

                this.partB['keyCodeVal'] = '';

                // 设置产品明细
                let oldAddDatas = addReturnDatas.map((item, index) => {
                    let templockedAry = this.addInfoDatas.filter(
                        (f) =>
                            `${f.product_type}${f.vendor_inner_sn}${f.product_inner_sn}${f.specification_inner_sn}` ===
                            `${item.product_type}${item.vendor_inner_sn}${item.product_inner_sn}${item.specification_inner_sn}`
                    );
                    if (templockedAry && templockedAry.length) {
                        item.batchDatas = templockedAry[0].batchDatas;
                        item.inventoryBannce = templockedAry[0].inventoryBannce;
                        item.isCopyAndDelVoucherItem = true; // 需要减掉刚刚填的数量
                    } else {
                        item.batchDatas = [];
                        item.inventoryBannce = 0;
                    }
                    if (!item.mdrf_inner_sn) {
                        item.mdrf_sn = '';
                    }
                    return {
                        ...item,
                        quantity: -item.quantity,
                        isReadonly: true, // 从正常的退货产品过来的产品不能编辑，从库存产品或平台产品过来的产品可编辑
                        action: 1,
                        unitPriceList: [],
                        batch_locked_quantity: 0,
                        max_batch_locked_quantity: 0,
                        overdue_date: item.overdue_date ? moment(item.overdue_date) : '',
                        produce_date: item.produce_date ? moment(item.produce_date) : '',
                        focusDatas: [
                            false,
                            index === addReturnDatas.length - 1,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false
                        ]
                    };
                });
                this.refreshTempInsertAddProductFn(oldAddDatas);
                // 构建最小包装单位的倍数 和 含税单价
                this.addInfoDatas.forEach((item, index) => {
                    item.seq = index;
                    if (typeof item.average_price === 'undefined') {
                        item.average_price = 0;
                        item.average_price_compare = 0;
                    }
                    if (!item.min_unit_info) {
                        item.min_unit_info = 1;
                        this.loadUnitMinFn(item, index, 'add');
                    }
                    this.handleTaxUnitPriceChange(item, 'quantity', index);
                });
            }

            if (!isPlatformAddReturnDataEmpty) {
                this.getProReturn(dea, ven);
            }

            // 关闭弹窗
            this.addReturnListModal.alert = false;
        },
        addReturnListModalFn(actionName) {
            if (actionName === 'close') {
                this.addReturnListModal.alert = false;
            } else if (actionName === 'save') {
                const orderAddReturnRef = this.$refs.orderAddReturnList;
                if (orderAddReturnRef) {
                    const { addReturnDatas, platformAddReturnData, searchData } = orderAddReturnRef.returnDataFn();
                    this.addReturnSearchDataFn(addReturnDatas, platformAddReturnData, searchData);
                }
            }
        },
        grnAndOdoTransferRecordDetailFn(params, record) {
            switch (this.orderTypeData.value) {
                case grnAndOdoOrderType['PURCHASEGRN'].value: {
                    params.vId = record.voucher_inner_sn_c;
                    params.voucherType = this.sheelID;
                    params.organization_inner_sn = record.organization_inner_sn;
                    return purchaseGrnTransferRecordDetailAPI({ params });
                }
                case grnAndOdoOrderType['ALLOTGRN'].value: {
                    this.gotdownInfoModal.columns = this.DetailTableColumnsGrnAllot;
                    delete params.billsType;

                    params.avid = record.voucher_inner_sn_c;
                    return allotGrnTransferRecordDetailAPI({ params });
                }
                case grnAndOdoOrderType['SALEODO'].value: {
                    this.billLadingColumns = this.billLadingColumnsSale;
                    this.billLadingBotColumns = this.billLadingBotColumnsSale;

                    params.sid = record.voucher_inner_sn_c;
                    return saleOdoTransferRecordDetailAPI({ params });
                }
                case grnAndOdoOrderType['ALLOTODO'].value: {
                    this.billLadingBotColumns = this.billLadingBotColumnsSale;
                    delete params.billsType;

                    params.avid = record.voucher_inner_sn_c;
                    return allotOdoTransferRecordDetailAPI({ params });
                }
                case grnAndOdoOrderType['GRANTODO'].value: {
                    delete params.billsType;

                    params.gvid = record.voucher_inner_sn_c;
                    return grantOdoTransferRecordDetailAPI({ params });
                }
            }
        },
        // 出入库单的可调拨单明细列表
        async grnAndOdoTransferRecordDetailAPIFn() {
            const record = this.addInfoModalForm;
            let params = {
                billsType: record.billsType,
                pageNum: 1,
                pageSize: 100
            };
            await this.grnAndOdoTransferRecordDetailFn(params, record)
                .then((res) => {
                    this.DetailTableDatas = [];
                    this.billLadingBotDatasAll = [];
                    this.DetailTableLoading = false;
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        this.billLadingBotDatasAll = list.map((item) => {
                            if (item.status || parseInt(item.status) >= 0) {
                                item.status_name = this.sheelStatus[item.status];
                            } else {
                                item.status = item.tvStatus;
                                item.tvStatus = item.tvStatus === 'AWAITING' ? '待完成' : '已完成';
                                item.status_name = item.tvStatus;
                            }

                            if (typeof item.grant_quantity === 'undefined') {
                                item.grant_quantity = item.quantity;
                            }

                            if (typeof item.quantity === 'undefined') {
                                item.quantity = item.grant_quantity;
                            }

                            return {
                                ...item,
                                activeClass: '',
                                // grant_quantity: item.grant_quantity || item.quantity, // 发放单不用写，销售单不知道
                                // quantity: item.grant_quantity,
                                key: item.detail_inner_sn,
                                // status_name: this.sheelStatus[item.status],
                                // tvStatus: item.status || item.tvStatus,
                                source_type: 'copy', // 定义一个参数，凡是添加产品过去的信息，删除后都不留痕迹
                                vendor_inner_sn:
                                    item.vendor_inner_sn || item.product_vendor_inner_sn || item.vd_inner_sn,
                                vendor_name: item.vendor_name || item.product_vendor_name || item.vd_name,
                                product_vendor_name: item.vendor_name || item.product_vendor_name || item.vd_name,
                                product_vendor_inner_sn:
                                    item.vendor_inner_sn || item.product_vendor_inner_sn || item.vd_inner_sn,
                                produce_date: item.produce_date === '0000-00-00' ? '' : item.produce_date,
                                rate: item.rate * 100,
                                unit: item.unit || item.unit_name,
                                unit_name: item.unit || item.unit_name,
                                price: this.reNumber(item.quantity * item.unit_price_text)
                            };
                        });

                        if (this.sheelType) {
                            this.billLadingBotDatas = [];
                            this.gotdownInfoModal.data = [];
                            this.billLadingBotDatasAll.forEach((item) => {
                                if (
                                    item.status === 'AWAITING' ||
                                    parseFloat(item.status) === 0 ||
                                    parseFloat(item.status) === 2 ||
                                    item.tvStatus === '待完成'
                                ) {
                                    this.billLadingBotDatas.push(item);
                                    this.gotdownInfoModal.data.push(item);
                                }
                            });
                        } else {
                            this.billLadingBotDatas = this.billLadingBotDatasAll;
                        }
                        // if (mode !== undefined) { // 当这里为点击点开的时候 可以添加删除过的信息
                        //     this.billLadingBotDatasAll.forEach(item => {
                        //         let isf = false
                        //         this.delInfoDatas.forEach(itemS => {
                        //             if (item.product_type === itemS.product_type && item.product_inner_sn === itemS.product_inner_sn && item.vendor_inner_sn === itemS.vendor_inner_sn && item.specification === itemS.specification) {
                        //                 isf = true
                        //             }
                        //         })
                        //         if (isf) item.status = 0
                        //     })
                        //     this.billLadingBotDatas = []
                        //     this.billLadingBotDatasAll.forEach(item => {
                        //         if (item.status === 0) {
                        //             this.billLadingBotDatas.push(item)
                        //         }
                        //     })
                        // }
                        if (this.billLadingBotDatas.length) {
                            this.currentTrBillLadingBotObj = this.billLadingBotDatas[0];
                        } else {
                            this.currentTrBillLadingBotObj = {};
                        }
                        // if (this.$refs.sheelTableAdd) {
                        //     this.$refs.sheelTableAdd.refreshColumn()
                        // }

                        if (this.$refs.xTableAddInfo) {
                            this.$refs.xTableAddInfo.focus();
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.billLadingBotLoading = false;
                });
        },
        refreshBillLoadingBotColumns(columns, refName = 'sheelTableAdd') {
            let columnTimer = setTimeout(() => {
                this.$refs[refName].reloadColumn(columns);
                clearTimeout(columnTimer);
                columnTimer = null;
            });
        },
        /* 出入库接口拆分区域结束 */

        disabledDate(current) {
            return current < moment().startOf('day');
        }
    },
    computed: {
        // 打印按钮可用状态
        printButtonEnabled() {
            // 复制单不可打印
            if (this.btnName === 'copy') {
                return false;
            }
            // 没有出（入）库时间的单据不可打印
            if (this.c_IsGrn) {
                return this.addInfoModalForm.in_time !== undefined && this.addInfoModalForm.in_time !== null;
            }
            return this.addInfoModalForm.out_time !== undefined && this.addInfoModalForm.out_time !== null;
        },
        isTableDatasEmpty() {
            return this.godownEntryDatas.length <= 0;
        },
        rowSelection() {
            const { billLadingBotSelectedRowKeys } = this;
            return {
                billLadingBotSelectedRowKeys,
                selectedRowKeys: this.billLadingBotSelectedRowKeys,
                onChange: this.onSelectChange,
                hideDefaultSelections: true,
                getCheckboxProps: (record) => ({
                    props: {
                        disabled: record.status > 0 // Column configuration not to be checked
                    }
                })
            };
        },
        scrollYNumber() {
            // 固定表格的头部
            // let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
            //     topHeight = 282
            // return clientHeight - topHeight
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                // topHeight = this.hightSearchObj.isArrow ? 177 : 116,
                topHeight = this.hightSearchObj.isArrow ? 184 : 64,
                pager = 52;
            return clientHeight - topHeight - pager - 52;
        },
        scrollYNumberSto() {
            // 固定表格的头部
            // let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
            //     topHeight = 282
            // return clientHeight - topHeight
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                // topHeight = this.hightSearchObj.isArrow ? 177 : 116,
                topHeight = this.hightSearchObj.isArrow ? 184 : 64,
                tabsHeight = 50,
                pager = 92;
            return clientHeight - topHeight - pager - tabsHeight;
        },
        scrollYAddProductNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 230,
                bottomHeight = 116;
            return clientHeight - topHeight - bottomHeight;
        },
        isEditDisabledAddInfo() {
            // 外层入库单列表修改/里层新增是否可以修改的条件
            let flag = true;
            if (this.isTableDatasEmpty) return false;
            if (this.btnName === 'add') {
                flag = false;
            } else if (parseFloat(this.currentTrObj.status) === 0 || parseFloat(this.addInfoModalForm.status) === 0) {
                flag = false;
            }
            return flag;
        },
        addInfoModalTextComputed() {
            // 单据右下角的取消文字改变
            if (this.addInfoModalForm.status <= 1 || this.btnName === 'add') {
                return '取消';
            } else {
                return '关闭';
            }
        },
        addInfoModalTitleComputed() {
            const dan = '单';
            let topTitle = '';
            if (this.isSeparateBillOrder) {
                topTitle = this.orderTypeData.label;
                if (this.isTableDatasEmpty) return topTitle;
                if (this.btnName === 'edit') {
                    /// 加上主单据的状态文字 [作废-4、红冲-5]
                    const status = parseInt(this.addInfoModalForm.status);
                    if (status === 4) {
                        topTitle += ' (作废)';
                    } else if (status === 5) {
                        topTitle += ' (红冲)';
                    }
                }
            } else {
                if (this.isTableDatasEmpty) return topTitle;
                switch (this.btnName) {
                    case 'add': {
                        topTitle = this.addInfoModalForm.type_name;
                        break;
                    }
                    case 'edit': {
                        if (this.addInfoModalTitleFlag) {
                            topTitle = this.addInfoModalForm.type_name;
                        } else {
                            topTitle = this.currentTrObj.type_name;
                        }

                        /// 加上主单据的状态文字 [作废-4、红冲-5]
                        const status = parseInt(this.addInfoModalForm.status);
                        if (status === 4) {
                            topTitle += ' (作废)';
                        } else if (status === 5) {
                            topTitle += ' (红冲)';
                        }
                        break;
                    }
                    default: {
                        topTitle = this.currentTrObj.type_name;
                    }
                }
            }
            if (topTitle) {
                return topTitle.indexOf(dan) > -1 ? topTitle : topTitle + `${dan}`;
            } else {
                return '————';
            }
        },
        /// 判断是不是将出入库拆分为七个单的标识
        isSeparateBillOrder() {
            return this.orderTypeData && this.orderTypeData.value;
        },
        // true: 入库，false: 出库
        c_IsGrn() {
            // 判断是从哪个页面过来的标识
            if (this.isSeparateBillOrder) {
                return (
                    this.orderTypeData.value === grnAndOdoOrderType['PURCHASEGRN'].value ||
                    this.orderTypeData.value === grnAndOdoOrderType['ALLOTGRN'].value ||
                    this.orderTypeData.value === grnAndOdoOrderType['PURCHASERETURN'].value
                );
            } else {
                return this.myListPropObj.pageType === 0;
            }
        },
        setOutPutTextComputed() {
            // 出入库文字设置
            return this.c_IsGrn ? '入库' : '出库';
        },
        setOutPutButtonTextComputed() {
            // 出入库按钮文字设置
            if (
                this.orderTypeData.value === grnAndOdoOrderType['PURCHASERETURN'].value ||
                this.orderTypeData.value === grnAndOdoOrderType['SALERETURN'].value
            ) {
                return '确认退货';
            } else {
                return this.c_IsGrn ? '入库' : '出库';
            }
        },
        isTransferRecord() {
            // 当前只有采购退货和销售退货的出入库单没有调单功能，同时该两类单添加产品需要单独调用可退货产品列表，而且该两类单只能改数量
            return (
                this.isSeparateBillOrder &&
                !(
                    this.orderTypeData.value === grnAndOdoOrderType['PURCHASERETURN'].value ||
                    this.orderTypeData.value === grnAndOdoOrderType['SALERETURN'].value
                )
            );
        },
        isGiveOdo() {
            // 是否是出库的赠送单
            return this.isSeparateBillOrder && this.orderTypeData.value === grnAndOdoOrderType['GIVEODO'].value;
        },
        isShowTransferRecordBtn() {
            // 是否显示顶部的调单按钮
            if (this.isSeparateBillOrder) {
                // 1. 赠送单不显示调单
                if (this.orderTypeData.value === grnAndOdoOrderType['GIVEODO'].value) {
                    return false;
                }

                // 2. 厂商账户登录，且不是采购退货或销售退货单，且不是采购入库单才显示调单
                if (this.userSystemType === 3) {
                    return (
                        this.isTransferRecord && this.orderTypeData.value !== grnAndOdoOrderType['PURCHASEGRN'].value
                    );
                }

                // 3. 除上面两种情况外，不是采购退货或销售退货单才显示
                return this.isTransferRecord;
            } else {
                if (this.c_IsGrn) {
                    return this.userSystemType !== 3; // this.tabChoose === this.tabTypesNum[0]
                }
                return !this.c_IsGrn;
            }
        },
        isNotAllot() {
            // 是否是调拨，true:不是调拨， false:是调拨
            return (
                this.isSeparateBillOrder &&
                !(
                    this.orderTypeData.value === grnAndOdoOrderType['ALLOTODO'].value ||
                    this.orderTypeData.value === grnAndOdoOrderType['ALLOTGRN'].value
                )
            );
        },
        isReturnGoods() {
            // 是采购退货和销售退货且是新增，且产品明细有值，仓库有值就不能选择仓库了
            if (
                !this.isTransferRecord &&
                (this.btnName === 'add' || this.btnName === 'copy') &&
                this.addInfoModalForm.warehouse_inner_sn
            ) {
                let tempDatas = this.addInfoDatas.filter((item) => item.product_inner_sn);
                return tempDatas && tempDatas.length;
            } else {
                return false;
            }
        },
        isShowGiveOdoColumn() {
            // 赠送单不显示 [含税单价、含税金额、类别]列
            return this.isSeparateBillOrder && this.orderTypeData.value !== grnAndOdoOrderType['GIVEODO'].value;
        },
        isShowOrgName() {
            // 是否隐藏单位名称 [现在暂时隐藏：领用出库、调拨入库和调拨出库单]
            return (
                this.isSeparateBillOrder &&
                (this.orderTypeData.value === grnAndOdoOrderType['GRANTODO'].value ||
                    this.orderTypeData.value === grnAndOdoOrderType['ALLOTODO'].value ||
                    this.orderTypeData.value === grnAndOdoOrderType['ALLOTGRN'].value)
            );
        },
        isShowAddInfoModalActionColumn() {
            // 是否显示模态框的操作栏
            if (this.isSeparateBillOrder && this.isNotAllot) {
                return this.btnName === 'add' || this.addInfoModalForm.status === 0;
            } else {
                return (
                    this.btnName === 'add' ||
                    this.addInfoModalForm.status === 0 ||
                    (this.c_IsGrn && this.addInfoModalForm.status === 1) ||
                    (this.c_IsGrn && this.addInfoModalForm.status === 2)
                );
            }
        },
        orgName() {
            const EmployeeInfo = this.$cookie.get('EmployeeInfo'),
                orgNameNo = '';
            return EmployeeInfo ? JSON.parse(EmployeeInfo).OrgName || orgNameNo : orgNameNo;
        },
        /// 是否是【作废|红冲】的单子 [作废-4 | 红冲-5状态的单据]
        isRedDashedOrder() {
            if (
                this.btnName === 'add' ||
                this.btnName === 'copy' ||
                this.godownEntryDatas.length <= 0 ||
                Object.keys(this.currentTrObj).length <= 0 ||
                Object.keys(this.addInfoModalForm).length <= 0
            )
                return false;
            const status = parseInt(this.currentTrObj.status) || parseInt(this.addInfoModalForm.status);
            return status === 4 || status === 5;
        },
        // 调单生成出入库单时，选中仓库是否包含在当前用户的仓库列表中
        isIncludedInWarehouseList() {
            if (!this.addInfoModalForm.warehouse_inner_sn) {
                return true;
            }
            return this.warehouseList.some(
                (item) => item.warehouse_model_inner_sn === this.addInfoModalForm.warehouse_inner_sn
            );
        },
        ...mapState(['warehouseList'])
    },
    watch: {
        'addInfoModal.alert': {
            handler(val) {
                if (!val) {
                    this.resetAddInfoModalFn();
                    this.removeEventProductDocumentFn();
                } else {
                    this.addEventProductDocumentFn();
                }
            },
            deep: true
        },
        addInfoDatas: {
            handler(val) {
                this.getTaxAmountTotal(val);
            },
            deep: true
        },
        warehouseList: function () {
            this.warehouseUsableTopListAPIFn();
        }
        // addProductEnter: {
        //     handler (val, oldVal) {
        //         if (!val) {
        //             this.removeEventProductDocumentFn()
        //         } else {
        //             this.addEventProductDocumentFn()
        //         }
        //     },
        //     deep: true
        // }
    },
    created() {
        this.grnStatus = [
            '正式',
            '已审核',
            `已${this.setOutPutTextComputed}`,
            `已${this.setOutPutTextComputed}复核`,
            '作废',
            '红冲'
        ];
    },
    mounted() {
        this.addInfoModalForm.organization_name = '';
        this.addInfoModalForm.organization_inner_sn = this.userBelong;

        if (this.orderTypeData.value === grnAndOdoOrderType['GRANTODO'].value) {
            this.addInfoModalForm.billsType = 'HH';
        }
        this.SheelHasWarehouse = false;
        this.isSheel = false;
        this.sheelListData = [];
        this.btnName = 'add';
        this.changeTabFn();
        this.scanQRcode();

        this.making_people_name = JSON.parse(localStorage.getItem('userInfo')).EmployeeInfo.Name;
        this.relevantInstitutionsFocus = true;
        this.warehouseNameFocus = false;
        // this.warehouseUsableTopListAPIFn() // 仓库
        this.orgDeptTopTreeAPIFn(); // 部门
        if (!this.isSeparateBillOrder) {
            if (this.c_IsGrn) {
                this.grnTypeListAPIFn();
            } else {
                this.odoTypeListAPIFn();
            }
        }

        this.orgEmpDropdownListAPIFn(); // 经手人

        this.clearCurrentBatchNUmberFn();
        this.isLoadingDepartmentInnerSn = true;

        this.grnOrgListAPIFn(1, true);
        this.grnOrgListAPIFn(3, true);
        this.grnOrgListAPIFn(2, true);
        
        // focus
        let aSelectDom = document.getElementsByClassName('focus-a-select');
        aSelectDom[0].getElementsByClassName('ant-select-search__field')[0].click();
    }
};
</script>

<style lang="less" scoped>
.and-sheet {
    .asbr-td {
        &.disabled {
            background-color: #ffeecc;
        }
    }
}
</style>

<style lang="less" scoped>
@w: 24px;
.page-warehousemanage-outputstorage {
    .form-label {
        display: inline-block;
        width: 80px !important;
        text-align: right;
        margin-right: 20px;
    }
    .hight-search-wrap {
        line-height: 1;
    }
    .form-input {
        width: 70%;
        display: inline-block;
        vertical-align: middle;
    }
}
.scoped.scoped-white .tab-table-wrap {
    background: #fff !important;
}
.order-modal-list {
    //   position: absolute;
    //   left: @w;
    //   right: @w;
    //   bottom: 57px;
    display: flex;
    flex-direction: column;
    li {
        display: flex;
        flex-direction: row;
        padding-top: 24px;

        .item-wrap,
        .item {
            display: flex;
            flex: 1;
        }

        .item-wrap {
            padding-bottom: 0;

            &:nth-of-type(n + 1) {
                .item {
                    padding-left: 16px;
                }
            }

            .item {
                padding-bottom: 0;
            }
        }

        &:last-child {
            .item-wrap {
                &:last-child {
                    padding-right: 0;
                }
            }
        }
    }
}
.modal-form-input-scoped .modal-form-input .top-list .label {
    display: flex;
    width: auto;
    max-width: 94px;
}
.dragMain {
    width: 1160px;
    height: 215px;
    margin-top: 40px;
    border: 1px solid #ccc;
    z-index: 1;
    position: absolute;
    filter: alpha(opacity=1);
    opacity: 0.1;
}
.dragMainLi {
    height: 40px;
    line-height: 40px;
    float: left;
    width: 100%;
}
.ant-modal-centered {
    display: none;
}

.ant-modal-footer .ant-btn {
    z-index: 5;
}
</style>
