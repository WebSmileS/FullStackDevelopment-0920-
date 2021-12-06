<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :sm="22" :md="22" :lg="18" :xl="18" :xxl="15">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="groupBtnFn('add')" type="primary" size="small"> 新增 </a-button>
                            <a-button @click="groupBtnFn('edit')" type="primary" size="small"> 修改 </a-button>
                            <a-button @click="groupBtnFn('remove')" type="danger" size="small"> 删除 </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :sm="4" :md="2" :lg="6" :xl="6" :xxl="9">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-model="searchTables.sl_sn"
                            placeholder="单据编号"
                            size="small"
                            enterButton
                            class="search"
                            @search="searchBtnFn"
                        />
                        <div @click.stop="hightSearchIconFn" class="hight-search-text">
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
                            <span class="label">收货单位</span>
                            <a-input v-model="searchTables.organization_name" type="text" />
                        </li>
                        <li v-if="btnName !== 'add'">
                            <span class="label">单据编号</span>
                            <a-input v-model="searchTables.sl_sn" type="text" placeholder="" />
                        </li>

                        <li>
                            <span class="label">状态</span>
                            <a-select v-model="searchTables.status" type="text" :options="statusOption" placeholder="">
                            </a-select>
                        </li>
                        <li>
                            <span class="label">销售员</span>
                            <a-input v-model="searchTables.seller_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <a-button @click="searchBtnFn" type="primary"> 搜索 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab主体区域开始 -->
        <div
            :class="hightSearchObj.isArrow ? 'top136' : 'top56'"
            class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
            style="padding: 8px; background: #fff"
        >
            <vxe-grid
                ref="salesTable"
                size="small"
                :height="scrollYNumber - 1"
                border
                resizable
                auto-resize
                highlight-current-row
                @cell-click="cellClickFn"
                @cell-dblclick="cellDblclickFn"
                align="center"
                :data="salesListDatas"
                :loading="salesListStatus.loading"
                show-overflow
                show-header-overflow
                showHeader
                :columns="salesListColumns"
                :start-index="(salesListStatus.current - 1) * salesListStatus.pageSize"
            >
            </vxe-grid>
            <vxe-pager
                :loading="salesListStatus.loading"
                :current-page="salesListStatus.current"
                :page-size="salesListStatus.pageSize"
                :page-sizes="salesListStatus.pageSizes"
                :total="salesListStatus.total"
                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                style="background: #ffffff"
                @page-change="changePageFn"
            >
            </vxe-pager>
        </div>
        <!-- tab主体区域结束 -->

        <!-- 销售单添加/编辑产品 -->

        <a-modal
            v-model="infoModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-form-input-scoped-td modal-add global-drag"
            :width="1200"
            :zIndex="3"
            :afterClose="afterClosePlanmodalFn"
        >
            <div v-globalDrag="{ el: 'modal-add' }" class="modal-header-wrap" @dblclick="modalFullscreen">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ infoModal.title }}
                </h6>
                <span
                    class="modal-fullscreen"
                    v-if="isFullscreen"
                    @click="modalFullscreen"
                    @mouseover="fullscreenMouseOver(false)"
                    @mouseout="fullscreenMouseOver(true)"
                >
                    <i data-v-4a953340="" aria-label="最小化" class="modal-fullscreen-icon anticon anticon-arrows-alt">
                        <svg
                            width="20px"
                            height="20px"
                            viewBox="0 0 256 256"
                            version="1.1"
                            xmlns="http://www.w3.org/2000/svg"
                            xmlns:xlink="http://www.w3.org/1999/xlink"
                        >
                            <title>还原</title>
                            <g id="还原" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                                <g
                                    id="编组"
                                    transform="translate(128.500000, 127.500000) scale(-1, 1) translate(-128.500000, -127.500000) translate(42.000000, 41.000000)"
                                    fill="#ffffff"
                                >
                                    <path
                                        d="M173,47 L173,173 L47,173 L47,47 L173,47 Z M159,62 L62,62 L62,159 L159,159 L159,62 Z"
                                        id="形状结合"
                                    ></path>
                                    <path
                                        d="M126,0 L126,52.672 L107.545,52.672 L107.545455,17.5454545 L17.5454545,17.5454545 L17.5454545,107.545455 L56.803,107.545 L56.803,126 L0,126 L0,0 L126,0 Z"
                                        id="形状结合"
                                    ></path>
                                </g>
                            </g>
                        </svg>
                    </i>
                </span>
                <span
                    class="modal-fullscreen"
                    v-else
                    @click="modalFullscreen"
                    @mouseover="fullscreenMouseOver(false)"
                    @mouseout="fullscreenMouseOver(true)"
                >
                    <i data-v-4a953340="" aria-label="最小化" class="modal-fullscreen-icon anticon anticon-arrows-alt">
                        <svg
                            width="20px"
                            height="20px"
                            viewBox="0 0 256 256"
                            version="1.1"
                            xmlns="http://www.w3.org/2000/svg"
                            xmlns:xlink="http://www.w3.org/1999/xlink"
                        >
                            <title>全屏</title>
                            <g id="全屏" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                                <path
                                    d="M214,41 L214,214 L41,214 L41,41 L214,41 Z M198,57 L57,57 L57,198 L198,198 L198,57 Z"
                                    id="形状结合"
                                    fill="#ffffff"
                                ></path>
                            </g>
                        </svg>
                    </i>
                </span>
            </div>
            <template slot="footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button @click="planModalGroupBtnSendFn('print')" key="print"> 打印 </a-button>
                        <a-button @click="planModalGroupBtnSendFn('exportExcel')" key="exportExcel">
                            导出Excel
                        </a-button>
                    </li>
                    <li>
                        <a-button @click="planModalGroupBtnSendFn('cancel')" key="cancel">
                            {{ planModalTextComputed }}
                        </a-button>

                        <a-button
                            v-show="!infoModal.isadd && infoAllData.status < 2"
                            @click="editSave(1)"
                            key="submit"
                            type="primary"
                            :loading="infoModal.loadingSave"
                        >
                            保存
                        </a-button>
                        <a-button
                            v-show="!infoModal.isadd && infoAllData.status === 1"
                            @click="editSave(2)"
                            key="audit"
                            type="primary"
                            :loading="infoModal.loadingAudit"
                        >
                            审核
                        </a-button>

                        <a-button
                            v-show="infoModal.isadd"
                            @click="addSave()"
                            key="submitPo"
                            type="primary"
                            :loading="infoModal.loadingSave"
                        >
                            保存
                        </a-button>
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
                    <span style="font-size: 24px; color: #444">销售单</span>
                </h6>
                <ul class="top-list" style="'width:60%'">
                    <li class="item">
                        <span class="label"><em class="em red">*</em>收货单位</span>
                        <a-input
                            class="input"
                            type="text"
                            :disabled="btnName === 'edit'"
                            readonly="readonly"
                            v-model="infoAllData.organization_name"
                            @click="openChooseTb"
                            placeholder=""
                        />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>销售员</span>
                        <a-tree-select
                            :disabled="isEditDisabled"
                            treeNodeFilterProp="title"
                            searchPlaceholder=""
                            :showSearch="true"
                            :treeData="handlerNameList"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            v-model="infoAllData.seller_inner_sn"
                            @change="changeHandlerNameSnFn"
                            :allowClear="false"
                            class="input"
                            style="min-width: 170px"
                        >
                        </a-tree-select>
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>销售部门</span>
                        <a-tree-select
                            treeNodeFilterProp="title"
                            :disabled="isEditDisabled"
                            :showSearch="true"
                            :treeData="superiorDepartments"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            v-model="infoAllData.department_inner_sn"
                            @change="changeDepartNameSnFn"
                            :allowClear="false"
                            class="input"
                            style="min-width: 170px"
                        >
                        </a-tree-select>
                    </li>
                    <li class="item">
                        <span class="label" v-if="!infoModal.isadd">单据编号</span>
                        <a-input
                            v-if="!infoModal.isadd"
                            v-model="infoAllData.sl_sn"
                            disabled="true"
                            placeholder=""
                            class="input"
                        />
                    </li>
                </ul>
                <ul class="top-list" style="'width:60%'">
                    <li class="item">
                        <span class="label">采购方联系人</span>
                        <a-input
                            v-model="infoAllData.purchaser_linkman"
                            :disabled="isEditDisabled"
                            placeholder=""
                            class="input"
                        />
                    </li>
                    <li class="item">
                        <span class="label">联系电话</span>
                        <a-input
                            v-model="infoAllData.purchaser_phone"
                            :disabled="isEditDisabled"
                            placeholder=""
                            class="input"
                        />
                    </li>
                    <li class="item">
                        <span class="label">销售方联系人</span>
                        <a-input
                            v-model="infoAllData.seller_linkman"
                            :disabled="isEditDisabled"
                            placeholder=""
                            class="input"
                        />
                    </li>
                    <li class="item">
                        <span class="label">联系电话</span>
                        <a-input
                            v-model="infoAllData.seller_phone"
                            :disabled="isEditDisabled"
                            placeholder=""
                            class="input"
                        />
                    </li>
                </ul>
                <div class="tab-table-wrap cur-overflow-hidden" style="padding-bottom: 0; margin-top: 0">
                    <div class="top-add-product">
                        <a-tooltip placement="topLeft" title="添加产品">
                            <a-button
                                :disabled="isEditDisabled"
                                @click="dblclickTableFn"
                                icon="plus"
                                style="height: 24px; padding: 0 4px"
                            ></a-button>
                        </a-tooltip>
                    </div>
                    <div class="tableFather" style="height: 230px">
                        <vxe-grid
                            ref="xGreatSendSaleTable"
                            size="small"
                            :data="infoData"
                            :loading="planModalPagination.loading"
                            height="auto"
                            border
                            resizable
                            auto-resize
                            show-overflow
                            show-header-overflow
                            highlight-current-row
                            @cell-click="cellGreatSendSaleClickFn"
                            @cell-dblclick="cellGreatSendSaleDblclickFn"
                            align="center"
                            show-footer
                            :footer-method="footerMethodFn"
                            :footer-cell-class-name="footerCellClassNameFn"
                        >
                            <vxe-table-column field="index" title="NO." width="60" :resizable="false" fixed="left">
                                <template v-slot="{ row, seq }">
                                    <div>
                                        <span :class="row.action === 1 ? 'no-add' : ''">{{ seq }}</span>
                                    </div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="product_name"
                                title="产品"
                                width="200"
                                align="center"
                                :cell-render="{ name: 'product_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="specification"
                                title="规格型号"
                                width="180"
                                align="center"
                                :cell-render="{ name: 'specification' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="product_vendor_name"
                                title="厂商"
                                width="180"
                                align="center"
                                :cell-render="{ name: 'product_vendor_name' }"
                            ></vxe-table-column>
                            <vxe-table-column
                                field="quantity"
                                title="数量"
                                show-overflow="true"
                                showHeaderOverflow="true"
                                align="center"
                                width="120"
                            >
                                <template v-slot="{ row }">
                                    <a-input-number
                                        :disabled="isEditDisabled"
                                        :min="0.001"
                                        :formatter="
                                            (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')
                                        "
                                        :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d).*$/, '$1$2.$3')"
                                        v-model="row.quantity"
                                        class="input"
                                        style="width: 100%"
                                        @change="updateFooterPlanModalFn(row)"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_name" title="包装单位" width="120">
                                <template v-slot="{ row }">
                                    <a-cascader
                                        :allowClear="false"
                                        :disabled="isEditDisabled"
                                        :options="row.unitList"
                                        v-model="row.unit_inner_sn"
                                        changeOnSelect
                                        :displayRender="displayUnitRender"
                                        @click.stop="clickUnitListFn(row)"
                                        @change="changeUnitListFn"
                                        placeholder=""
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                field="batch_number"
                                title="批号"
                                show-overflow="true"
                                showHeaderOverflow="true"
                                align="center"
                                width="150"
                            >
                                <template v-slot="{ row }">
                                    <a-input
                                        :disabled="isEditDisabled"
                                        v-model="row.batch_number"
                                        class="input"
                                        style="width: 100%"
                                        @change="rushInput"
                                    />
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="unit_price" title="单价" width="150">
                                <template v-slot="{ row }">
                                    <div>{{ record_unit_price(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="money" title="金额" align="center" width="150">
                                <template v-slot="{ row }">
                                    <div>{{ record_money_total(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <!-- <vxe-table-column
                            field="discount"
                            title="折扣"
                            align="center"
                            width="12%"
                        >
                            <template v-slot="{ row }">
                                <a-input-number
                                    :min="0.01"
                                    :step="0.01"
                                    :disabled="isEditDisabled"
                                    v-model="row.discount"
                                    class="input"
                                    style="width:100%"
                                    @change="updateFooterPlanModalFn(row)"
                                />
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            field="discount_unit_price"
                            title="折后单价"
                            align="center"
                            width="10%"
                        >
                            <template v-slot="{ row }">
                                <div>{{ record_discount_unit_price_total(row) }}</div>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column
                            field="discount_money"
                            title="折后金额"
                            align="center"
                            width="10%"
                        >
                            <template v-slot="{ row }">
                                <div>{{ record_discount_money_total(row) }}</div>
                            </template>
                        </vxe-table-column> -->
                            <vxe-table-column field="rate" title="税率" align="center" width="120">
                                <template v-slot="{ row }">
                                    <a-select :disabled="isEditDisabled" v-model="row.rate" @change="tableRateChangeFn">
                                        <a-select-option :value="0"> 0% </a-select-option>
                                        <a-select-option :value="1"> 1% </a-select-option>
                                        <a-select-option :value="3"> 3% </a-select-option>
                                        <a-select-option :value="6"> 6% </a-select-option>
                                        <a-select-option :value="9"> 9% </a-select-option>
                                        <a-select-option :value="13"> 13% </a-select-option>
                                    </a-select>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax_unit_price" title="含税单价" align="center" width="150">
                                <template v-slot="{ row }">
                                    <a-dropdown :disabled="isEditDisabled" :trigger="['click']">
                                        <a-input-number
                                            :min="0"
                                            :formatter="
                                                (value) => `${value}`.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')
                                            "
                                            :parser="(value) => value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')"
                                            v-model="row.tax_unit_price"
                                            class="input"
                                            style="width: 100%"
                                            @change="updateFooterPlanModalFn(row)"
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
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax_amount" title="含税金额" align="center" width="150">
                                <template v-slot="{ row }">
                                    <div>{{ record_tax_amount_total(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="tax" title="税额" align="center" width="150">
                                <template v-slot="{ row }">
                                    <div>{{ record_tax_total(row) }}</div>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="health_care_sn" title="医保编号" align="center" width="150">
                                <template v-slot="{ row, seq }">
                                    <a-dropdown
                                        :disabled="isEditDisabled || row.product_type === 0"
                                        :trigger="['click']"
                                    >
                                        <a-input
                                            class="input"
                                            style="width: 100%"
                                            v-model="row.health_care_sn"
                                            @click="getProductSnAPIFn(true, row)"
                                        >
                                        </a-input>
                                        <a-menu
                                            v-if="healthAndSunSnModal.data.length"
                                            slot="overlay"
                                            style="maxheight: 200px"
                                        >
                                            <a-menu-item
                                                v-for="item in healthAndSunSnModal.data"
                                                :key="item.index"
                                                @click="healthCareSnChangeFn(item, seq)"
                                            >
                                                <a href="javascript:;">{{ item.sn }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column field="sun_sn" title="阳光流水号" align="center" width="150">
                                <template v-slot="{ row, seq }">
                                    <a-dropdown
                                        :disabled="isEditDisabled || row.product_type === 0"
                                        :trigger="['click']"
                                    >
                                        <a-input
                                            class="input"
                                            style="width: 100%"
                                            v-model="row.sun_sn"
                                            @click="getProductSnAPIFn(false, row)"
                                        >
                                        </a-input>
                                        <a-menu
                                            v-if="healthAndSunSnModal.data.length"
                                            slot="overlay"
                                            style="maxheight: 200px"
                                        >
                                            <a-menu-item
                                                v-for="item in healthAndSunSnModal.data"
                                                :key="item.index"
                                                @click="sunSnChangeFn(item, seq)"
                                            >
                                                <a href="javascript:;">{{ item.sn }}</a>
                                            </a-menu-item>
                                        </a-menu>
                                    </a-dropdown>
                                </template>
                            </vxe-table-column>
                            <vxe-table-column
                                v-if="!isEditDisabled"
                                field="action"
                                title="操作"
                                width="80"
                                :resizable="false"
                                fixed="right"
                            >
                                <template v-slot="{ row, seq }">
                                    <ul class="action-btn-list-wrap" style="position: relative">
                                        <li>
                                            <a-tooltip class="tooltip" placement="top" title="删除">
                                                <i
                                                    @click.stop="planModalActionBtnSendFn('remove', row, seq)"
                                                    class="yrt yrt-shanchu-copy cancel"
                                                    style="cursor: pointer"
                                                ></i>
                                            </a-tooltip>
                                        </li>
                                    </ul>
                                </template>
                            </vxe-table-column>
                            <template v-slot:empty>
                                <div v-if="isEditDisabled" class="dblclick-wrap">
                                    <span>暂无数据</span>
                                </div>
                                <div v-else @dblclick.stop="dblclickTableFn" class="dblclick-wrap">
                                    <span>双击添加产品</span>
                                </div>
                            </template>
                        </vxe-grid>
                    </div>
                </div>
                <ul class="order-modal-list">
                    <!-- <li>

                        <div style="display:flex;align-items:center;margin-right:16px">
                            <span class="label"
                                  style="width:auto">收款日期</span>
                            <a-date-picker
                                v-model="infoAllData.collection_date"
                                class="input"
                                :disabled="isEditDisabled"
                                placeholder=""
                                format="YYYY-MM-DD"
                            />

                        </div>
                    </li> -->
                    <li>
                        <span class="label" style="width: 66px">摘要</span>
                        <a-input
                            v-model="infoAllData.description"
                            placeholder=""
                            :disabled="isEditDisabled"
                            class="input"
                        />
                    </li>
                    <li>
                        <div class="item-wrap">
                            <div class="item">
                                <span class="label" style="width: auto">制单人</span>
                                <span>{{ infoAllData.making_people_name || '-' }}</span>
                            </div>
                            <div class="item">
                                <span class="label" style="width: auto">生成时间</span>
                                <span>{{ infoAllData.billing_time || '-' }}</span>
                            </div>
                        </div>
                        <div class="item-wrap">
                            <div class="item">
                                <span class="label" style="width: auto">审核人</span>
                                <span>{{ infoAllData.auditor_name || '-' }}</span>
                            </div>
                            <div class="item" style="display: none">
                                <span class="label" style="width: auto">审核时间</span>
                                <span>{{ infoAllData.audit_time || '-' }}</span>
                            </div>
                            <div class="item">
                                <span class="label" style="width: auto">收款日期</span>
                                <span>
                                    <a-date-picker
                                        v-model="infoAllData.collection_date"
                                        class="input"
                                        :disabled="isEditDisabled"
                                        placeholder=""
                                        format="YYYY-MM-DD"
                                    />
                                </span>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 销售单添加/编辑产品 -->

        <!-- 选择相关机构区域开始 -->
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
                <chooseOrgTab
                    ref="chooseOrgMd"
                    @chooseOrg="chooseOrgFn"
                    :partType="1"
                    :pageTypeName="pageTypeName"
                ></chooseOrgTab>
            </div>
        </a-modal>
        <!-- 选择相关机构区域结束 -->

        <!-- 添加产品区域开始 -->
        <a-modal
            :title="addProductMd.title"
            v-model="addProductMd.alert"
            :maskClosable="false"
            :mask="false"
            :width="1000"
            :centered="true"
            @cancel="addProductMd.alert = false"
            class="modal-form-input-scoped global-drag add_product"
        >
            <div v-globalDrag="{ el: 'add_product' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addProductMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="addProductMd.alert = false">关闭</a-button>
                <a-button :loading="addProductMd.loading" @click="addProductFn" type="primary">添加</a-button>
            </template>
            <add-product ref="chooseProMd" @returnData="getProReturn" :partB="partC"></add-product>
        </a-modal>
        <!-- 添加产品区域结束 -->
    </div>
</template>

<script>
import moment from 'moment';
import addProduct from '@/components/addProduct';
import {
    orgDeptTopTreeAPI,
    orgEmpDropdownListAPI,
    saleRecordDelAPI,
    saleRecordAddAPI,
    saleRecordEditAPI,
    saleRecordSearchAPI,
    saleRecordInfoAPI,
    // orderOrgListAPI,
    dealerUnitTreeAPI,
    platformUnitParents,
    platformUnitTree,
    contractGetContractPriceAPI,
    productSnListAPI
} from '@/service/pageAjax';
import { BillsType } from '@/config/constants';
import chooseOrgTab from '@/components/chooseOrgTab';

export default {
    name: 'sales',
    data() {
        return {
            billsType: '',
            isEditDisabled: true, // 为true为不可编辑
            planModalOrgSn: [], // 新增model里的相关机构默认值  // 添加产品
            planModalOrgs: [],
            pageType: 0,
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            currentTrObj: {}, // 表格选中的当前行数据
            searchTables: {
                sl_sn: '',
                status: [-1],
                organization_name: '',
                seller_name: ''
            }, // 表格的搜索并向后端请求字段
            salesListDatas: [],
            salesListStatus: {
                // 记录当前哪条数据选中
                loading: false, // 记录当前是否在加载中
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            salesListColumns: [
                { field: 'sl_sn', title: '单据编号', width: '15%' },
                { field: 'organization_name', title: '收货单位', width: '15%' },
                { field: 'seller_name', title: '销售员', width: '15%' },
                { field: 'making_people_name', title: '制单人', width: '15%' },
                { field: 'auditor_name', title: '审核人', width: '15%' },
                { field: 'status_text', title: '状态', width: '10%' },
                { field: 'department_name', title: '部门', width: '15%' }
            ],
            statusOption: [
                { label: '全部', value: -1 },
                // {label: '草稿', value: 0},
                { label: '正式', value: 1 },
                { label: '已审核', value: 2 },
                // {label: '已终止', value: 3},
                { label: '已生成出库单', value: 4 }
                // {label: '已出库', value: 5},
                // {label: '已配送', value: 6},
                // {label: '已送达', value: 7},
                // {label: '已收货', value: 8}
                // {label: '已开票', value: 3},
                // {label: '已过账', value: 3},
                // {label: '已结清', value: 3},
            ],
            planOrderStatus: ['处理中', '已完全处理', '已计划采购', '已终止处理'],
            btnName: '', // 当前点击的是哪个顶部按钮
            infoModal: {
                title: `${this.$route.meta.title} - 销售单`,
                alert: false,
                loading: false,
                saveLoading: false,
                loadingSave: false,
                loadingAudit: false,
                isadd: true // true为新增 false 为编辑
            },
            infoData: [], // 列表数据
            infoDataEdit: [], // 编辑过的产品数据
            infoDataBak: [],
            infoDataDet: [],
            infoAllData: {
                // 外框总数据
                collection_date: moment[''], // 收款日期
                department_inner_sn: '', // 部门内部编号
                department_name: '', // 部门名称
                details: [], // 销售单明细列表
                billsType: '', // 订单类型
                org_id: this.$cookie.get('userbelong'), // 当前机构ID
                organization_inner_sn: 0, // 相关机构ID
                organization_name: '', // 相关机构名称
                purchaser_linkman: '', // 采购方联系人
                purchaser_phone: '', // 采购方联系电话
                seller_inner_sn: '', // 销售员ID
                seller_linkman: '', // 销售方联系人
                seller_name: '', // 销售员名称
                seller_phone: '', // 销售方联系电话
                warehouse_inner_sn: '', // 仓库内部ID
                health_care_sn: '',
                sun_sn: '',
                description: '',
                audit_time: '',
                auditor_name: '',
                making_people_name: '',
                billing_time: ''
            },
            addInfoModalForm: {
                department_inner_sn: '',
                department_name: '',
                description: '',
                details: '',
                grn_sn: '',
                grn_type: '',
                odo_sn: '',
                odo_type: '',
                handler_inner_sn: '',
                handler_name: '',
                org_id: this.$cookie.get('userbelong'),
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

            planModalPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            record: {},
            handlerNameList: [], // 销售部门列表
            superiorDepartments: [], // 销售员列表
            addProductMd: {
                title: `${this.$route.meta.title} - 新增 - 添加产品`,
                alert: false,
                loading: false
            },
            partC: {
                // 新增产品窗口给子组件传递值
                partA_id: '',
                partB_id: '',
                billsType: '',
                oname: '',
                modalType: 4, // 1 - 合同内的产品  2 - 期初库存的产品列表  3 - 采购计划  4 - 采购/销售订单
                isProcurementSalesFlag: true // true - 销售单  false - 采购单
            },
            chooseOrgMd: {
                title: `${this.$route.meta.title} - 选择机构`,
                alert: false,
                loading: false
            },
            healthAndSunSnModal: {
                data: []
            },
            isFullscreen: false
        };
    },
    components: {
        chooseOrgTab,
        addProduct
    },

    computed: {
        planModalTextComputed() {
            // 销售订单右下角的取消文字改变
            if (this.infoAllData.status === 1 || this.btnName === 'add') {
                return '取消';
            } else {
                return '关闭';
            }
        },
        scrollYNumber() {
            // 固定表格的头部
            // let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
            //     topHeight = 282
            // return clientHeight - topHeight
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 177 : 116,
                pager = 52;
            return clientHeight - topHeight - pager;
        },
        orgName() {
            const EmployeeInfo = this.$cookie.get('EmployeeInfo'),
                orgNameNo = '';
            return EmployeeInfo ? JSON.parse(EmployeeInfo).OrgName || orgNameNo : orgNameNo;
        },
        userSystemType() {
            // 0-平台、 1-医院 、 2-经销商、 3-厂商
            return parseFloat(this.$cookie.get('userSystemType'));
        }
    },
    methods: {
        selectOrganizationFn(value, label) {
            const values = value.split('-');
            this.infoAllData.organization_inner_sn = values[0];
            this.infoAllData.organization_name = label;
            this.infoAllData.billsType = values[1];
            this.billsType = values[1];
            this.planModalOrgSn = [value];
            this.partC.oname = this.orgName; // label.length ? label : ''
            this.partC.partB_id = this.$cookie.get('userbelong');
            this.partC.partA_id = values[0];
            this.partC.billsType = values[1];
            if (this.btnName === 'add') {
                this.infoData = [];
                this.$refs.xGreatSendSaleTable && this.$refs.xGreatSendSaleTable.remove();
            }
        },
        changeUnitListFn(value, selectedOptions) {
            this.record.unit_name = selectedOptions.length ? selectedOptions[selectedOptions.length - 1].name : '';
            this.contractGetContractPriceAPIFn(this.record);
        },
        rushInput() {
            const xGreatSendSaleTable = this.$refs.xGreatSendSaleTable;
            if (xGreatSendSaleTable) {
                xGreatSendSaleTable.updateData();
            }
            this.collectInfoDataOfEdit();
        },
        afterClosePlanmodalFn() {
            this.partC.oname = '';
            const xGreatSendSaleTable = this.$refs.xGreatSendSaleTable;
            if (xGreatSendSaleTable) {
                xGreatSendSaleTable.refreshScroll();
                this.infoData = [];
                xGreatSendSaleTable.clearData();
            }
            if (this.isFullscreen) {
                this.modalFullscreen();
            }
        },
        filtersTwo(value) {
            // 保留两位小数
            if (typeof value === 'number') {
                return this.$XEUtils.commafy(value, { fixed: 2 });
            }
            return '';
        },
        // 点击高级搜索的小图标的事件
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        searchBtnFn() {
            if (!this.checkSearchInfoFormMsgFn()) return;
            this.initTablesPaginationFn();
            this.saleListFn();
        },
        checkSearchInfoFormMsgFn() {
            if (this.$Utils.checkInputValFn(this.searchTables.organization_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `相关机构${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (this.hightSearchObj.isArrow && this.$Utils.checkInputValFn(this.searchTables.sl_sn)) {
                this.$warning({
                    title: '操作提示',
                    content: `单据编号${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            if (this.hightSearchObj.isArrow && this.$Utils.checkInputValFn(this.searchTables.seller_name)) {
                this.$warning({
                    title: '操作提示',
                    content: `销售员${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        displayUnitRender({ labels }) {
            let label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
        },
        cellGreatSendSaleClickFn({ row }) {
            this.currentTrPlanModalObj = row;
        },
        cellGreatSendSaleDblclickFn({ row }, e) {
            this.currentTrPlanModalObj = row;
            if (this.isEditDisabled || !(this.btnName === 'edit' || this.btnName === 'add')) return;
            this.dblclickTableFn(e);
        },
        initTablesPaginationFn() {
            this.currentTrObj = {};
            this.salesListStatus = {
                ...this.salesListStatus,
                loading: true,
                current: 1
            };
        },
        reObjName(obj, value) {
            let Str = '';
            obj.forEach((item) => {
                if (item.value === value) {
                    Str = item.label;
                }
            });
            return Str;
        },
        rePlanModalId(obj, value) {
            let Str = '';
            obj.forEach((item) => {
                if (item.value === value) {
                    Str = item.org_id;
                }
            });
            return Str;
        },

        // 查看【销售订单列表】
        async saleListFn() {
            this.salesListStatus.loading = true;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                pageNum: this.salesListStatus.current,
                pageSize: this.salesListStatus.pageSize,
                sl_sn: this.searchTables.sl_sn,
                // 排序
                orderByColumn: 'sl_inner_sn',
                isAsc: 'desc'
            };
            if (this.hightSearchObj.isArrow) {
                // 高级搜索
                params.status = Array.isArray(this.searchTables.status)
                    ? this.searchTables.status[this.searchTables.status.length - 1]
                    : this.searchTables.status;
                if (params.status < 0) {
                    // 搜索全部
                    delete params.status;
                }
                if (this.searchTables.organization_name.length) {
                    params.organization_name = this.searchTables.organization_name;
                }
                if (this.searchTables.seller_name.length) {
                    params.seller_name = this.searchTables.seller_name;
                }
            }
            await saleRecordSearchAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows, total } = res;
                        this.salesListDatas = rows.map((item, index) => {
                            let itemObj = {
                                ...item,
                                index,
                                status_text: this.reObjName(this.statusOption, item.status)
                            };
                            return itemObj;
                        });
                        this.salesListStatus = {
                            ...this.salesListStatus,
                            total,
                            loading: false
                        };
                        this.currentTrObj = this.salesListDatas[0];
                        this.$refs.salesTable.setCurrentRow(this.salesListDatas[0]);
                    } else {
                        this.salesListStatus.loading = false;
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.salesListStatus.loading = false;
                });
        },
        // 查询价格
        async contractGetContractPriceAPIFn(record) {
            let tempUnitInnerSn = Array.isArray(record.unit_inner_sn)
                ? record.unit_inner_sn[record.unit_inner_sn.length - 1]
                : record.unit_inner_sn;
            let params = {
                billsType: this.infoAllData.billsType,
                organization_inner_sn: this.infoAllData.organization_inner_sn,
                product_inner_sn: record.product_inner_sn,
                product_type: record.product_type,
                product_vendor_inner_sn:
                    record.product_vendor_inner_sn || record.vendor_inner_sn || record.dealer_inner_sn,
                specification_inner_sn: record.specification_inner_sn,
                unit_inner_sn: typeof tempUnitInnerSn === 'undefined' ? 0 : tempUnitInnerSn,
                saleUse: true
            };

            await contractGetContractPriceAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    record.unitPriceList = list || [];
                    if (record.unitPriceList.length && typeof record.detail_inner_sn === 'undefined') {
                        record.unit_price = record.unitPriceList[0];
                        record.tax_unit_price = record.unitPriceList[0];
                    }
                    if (this.$refs.xTablePlanModalInfo) {
                        this.$refs.xTablePlanModalInfo.updateData();
                    } else {
                        this.$refs.xGreatSendSaleTable.updateData();
                    }
                    this.updateFooterPlanModalFn(record);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        cellClickFn({ row }) {
            this.currentTrObj = row;
        },
        cellDblclickFn({ row }) {
            this.currentTrObj = row;
            this.groupBtnFn('edit');
        },
        // 页码改变的回调函数
        changePageFn({ currentPage, pageSize }) {
            this.salesListStatus.current = currentPage;
            this.salesListStatus.pageSize = pageSize;
            this.salesListStatus.loading = true;
            this.saleListFn();
        },
        // 顶部按钮的事件
        groupBtnFn(btnName) {
            // if (this.salesListDatas.length === 0) {
            //     return
            // }
            this.btnName = btnName;
            this.resetSalesModalFn(); // 重置模块框
            if (this.btnName === 'add') {
                //
                // const cookie = this.$cookie
                // this.orderOrgListAPIFn(cookie.get('userSystemType'), cookie.get('userbelong'))
            }

            if (this.btnName !== 'add' && !this.checkSecltedTrFn()) return;
            switch (btnName) {
                // 新增
                case 'add': {
                    this.infoAllData.department_inner_sn = JSON.parse(
                        localStorage.getItem('userInfo')
                    ).Departments[0].Department_inner_sn;
                    this.infoAllData.department_name = JSON.parse(localStorage.getItem('userInfo')).Departments[0].Name;
                    this.infoAllData.seller_inner_sn = JSON.parse(
                        localStorage.getItem('userInfo')
                    ).Departments[0].Employee_inner_sn;
                    this.infoAllData.seller_name = JSON.parse(localStorage.getItem('userInfo')).EmployeeInfo.Name;
                    this.infoAllData.making_people_name = JSON.parse(
                        localStorage.getItem('userInfo')
                    ).EmployeeInfo.Name;
                    this.planModalOrgSn = [];
                    this.infoModal = {
                        title: `${this.$route.meta.title} - 新增`,
                        alert: true,
                        loading: false,
                        isadd: true
                    };
                    this.isEditDisabled = false;
                    this.$Utils.globalDragCenterFn('modal-add');
                    this.$nextTick(() => {
                        this.orgDeptTopTreeAPIFn(); // 所属部门
                        this.orgEmpDropdownListAPIFn();
                    });
                    if (this.$refs.xGreatSendSaleTable) {
                        this.$refs.xGreatSendSaleTable.updateFooter();
                    }
                    break;
                }
                // 修改
                case 'edit': {
                    this.infoModal = {
                        title: `${this.$route.meta.title} - 修改`,
                        alert: true,
                        loading: false,
                        isadd: false
                    };
                    this.$Utils.globalDragCenterFn('modal-add');
                    this.$nextTick(() => {
                        this.orgDeptTopTreeAPIFn(); // 所属部门
                        this.orgEmpDropdownListAPIFn();
                    });
                    this.saleInfoFn();
                    break;
                }
                // 删除
                case 'remove': {
                    const that = this;
                    if (that.currentTrObj.status > 1) {
                        that.$warning({
                            title: '操作提示',
                            content: `不能对【${that.reObjName(
                                this.statusOption,
                                that.currentTrObj.status
                            )}】状态的订单进行操作。`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        return;
                    }
                    that.$confirm({
                        title: '删除',
                        content: '确认要执行该操作吗？',
                        centered: true,
                        maskClosable: false,
                        mask: false,
                        onOk() {
                            that.delAPIFn();
                        }
                    });
                    break;
                }
            }
        },
        checkSecltedTrFn() {
            let flag = true;
            if (Object.keys(this.currentTrObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
            }
            return flag;
        },
        planModalActionBtnSendFn(btnName, record, index) {
            if (btnName === 'remove') {
                if (!record.action || record.action !== 1) {
                    record.action = 0;
                    record.unit_inner_sn = record.unit_inner_sn[record.unit_inner_sn.length - 1];
                    this.infoDataDet.push(record);
                    this.infoDataEdit.forEach((item, idx) => {
                        if (item === `${record.detail_inner_sn}-${record.product_type}`) {
                            this.infoDataEdit.splice(idx, 1);
                        }
                    });
                }
                const xGreatSendSaleTable = this.$refs.xGreatSendSaleTable;
                xGreatSendSaleTable.remove(record);
                this.planModalDatas = xGreatSendSaleTable.getTableData().tableData;
                xGreatSendSaleTable.setCurrentRow(this.planModalDatas[0]);
                this.infoData.splice(index - 1, 1);
                this.$refs.xGreatSendSaleTable.updateData();
            }
        },
        dblclickTableFn(e) {
            const filterDom = ['span', 'svg', 'path', 'input', 'i', 'li', 'ul'],
                flag = filterDom.some((item) => item === e.target.tagName.toLowerCase()),
                childrenFlag = filterDom.some(
                    (item) => item === e.target.children && e.target.children.tagName.toLowerCase()
                ),
                childrenFlagRemove = e.target.getElementsByClassName('action-btn-list-wrap').length;
            if (flag || childrenFlag || childrenFlagRemove) return;
            if (this.checkPlanModalOrgSnFn()) return;
            this.addProductMd.alert = true;
            let timer = setTimeout(() => {
                this.$Utils.globalDragCenterFn('add_product');
                clearTimeout(timer);
                timer = null;
            });
            this.planModalOrgs.forEach((item) => {
                if (this.planModalOrgSn[0] === item.value) {
                    this.partC.billsType = item.billsType;
                    this.partC.partA_id = item.org_id; // 相关机构ID
                }
            });
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        // 检测相关机构填值了没有
        checkPlanModalOrgSnFn() {
            let flag = false;
            if (this.planModalOrgSn.length <= 0) {
                flag = true;
                this.$warning({
                    title: '操作提示',
                    content: '请选择左上方的【相关机构】，再添加商品。',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
            } else {
                this.partC.partB_id = this.$cookie.get('userbelong');
                this.partC.modalType = 4;
                flag = false;
            }
            return flag;
        },
        changeHandlerNameSnFn(value, label) {
            this.infoAllData.seller_name = label.length ? label[label.length - 1] : '';
        },
        changeDepartNameSnFn(value, label) {
            this.infoAllData.department_name = label.length ? label[label.length - 1] : '';
        },
        // 重置模态框
        resetSalesModalFn() {
            this.infoData = [];
            this.infoDataBak = [];
            this.infoDataDet = [];
            this.infoDataEdit = [];
            this.infoAllData.collection_date = moment[''];
            this.infoAllData.department_inner_sn = '';
            this.infoAllData.department_name = '';
            this.infoAllData.details = [];
            this.infoAllData.billsType = '';
            this.infoAllData.org_id = this.$cookie.get('userbelong');
            this.infoAllData.organization_inner_sn = 0;
            this.infoAllData.organization_name = '';
            this.infoAllData.purchaser_linkman = '';
            this.infoAllData.purchaser_phone = '';
            this.infoAllData.seller_inner_sn = '';
            this.infoAllData.seller_linkman = '';
            this.infoAllData.seller_name = '';
            this.infoAllData.seller_phone = '';
            this.infoAllData.warehouse_inner_sn = '';
            this.infoAllData.description = '';
            this.infoAllData.audit_time = '';
            this.infoAllData.auditor_name = '';
            this.infoAllData.making_people_name = '';
            this.infoAllData.billing_time = '';
        },
        // 【删除】订单
        async delAPIFn() {
            this.salesListStatus.loading = true;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                sl_inner_sn: this.currentTrObj.sl_inner_sn,
                billsType: this.currentTrObj.billsType
            };
            await saleRecordDelAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.saleListFn();
                    } else {
                        this.$message.error(res.msg);
                        this.salesListStatus.loading = false;
                    }
                })
                .catch(() => {
                    this.salesListStatus.loading = false;
                });
        },
        // 查看【销售订单明细信息】
        async saleInfoFn() {
            let params = {
                org_id: this.$cookie.get('userbelong'),
                sl_inner_sn: this.currentTrObj.sl_inner_sn,
                billsType: this.currentTrObj.billsType // 订单类型
            };
            await saleRecordInfoAPI({ params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        info.collection_date = info.collection_date ? moment(info.collection_date) : '';
                        this.infoAllData = info;
                        this.infoAllData.billsType = info.billsType;
                        if (info.status < 2) {
                            this.isEditDisabled = false;
                        } else {
                            this.isEditDisabled = true;
                        }
                        if (info.voucher_inner_sn_c) {
                            // 该字段有值说明是从销售订单生成销售单过来的，置为“可读”
                            this.isEditDisabled = true;
                        }
                        const infoData = info.details.map((item) => {
                            return {
                                ...item,
                                unit_inner_sn: [item.unit_inner_sn],
                                rate: this.reNumber(item.rate * 100),
                                unitList: item.unit_inner_sn
                                    ? [{ value: item.unit_inner_sn, label: item.unit_name }]
                                    : [],
                                action: 2,
                                quantity_old: item.quantity,
                                unitPriceList: [],
                                tax_unit_price: item.unit_price
                            };
                        });
                        this.infoData = infoData;
                        for (let i = 0; i < this.infoData.length; i++) {
                            let item = this.infoData[i];
                            if (parseInt(info.status) <= 1) {
                                // 是草稿和正式
                                this.contractGetContractPriceAPIFn(item);
                            }
                        }
                        // 备份数据，用于在编辑时进行前后对比
                        this.infoDataBak = infoData.slice(0);
                        this.planModalOrgSn = [info.organization_inner_sn + '-' + info.billsType];
                        this.partC.partA_id = info.organization_inner_sn;
                        this.billsType = info.billsType;
                        this.partC.billsType = info.billsType;
                        // 检测相关机构是否在相关机构列表可以碰撞到值
                        const orgSnFlag = this.planModalOrgs.some((item) => item.value === this.planModalOrgSn);
                        if (!orgSnFlag) {
                            this.planModalOrgs.push({
                                label: info.organization_name || '',
                                name: info.organization_name || '',
                                title: info.organization_name || '',
                                // value: info.organization_inner_sn,
                                value: this.planModalOrgSn,
                                billsType: info.billsType,
                                org_id: info.organization_inner_sn
                            });
                        }

                        if (this.userSystemType === 2) {
                            // 经销商
                            if (
                                info.billsType === BillsType.DealerDealer ||
                                info.billsType === BillsType.HospitalDealer
                            ) {
                                // 经销商对经销商 医院对经销商 => 把机构名称读取
                                this.partC.oname = this.orgName;
                            } else {
                                this.partC.oname = info.organization_name || '';
                            }
                        } else if (this.userSystemType === 3) {
                            if (
                                info.billsType === BillsType.DealerVendor ||
                                info.billsType === BillsType.HospitalVendor
                            ) {
                                // 经销商对厂商 医院对厂商 => 把机构名称读取
                                this.partC.oname = this.orgName;
                            } else {
                                this.partC.oname = info.organization_name || '';
                            }
                        }
                        this.$nextTick(() => {
                            if (this.infoData.length > 0)
                                this.$refs.xGreatSendSaleTable.setCurrentRow(this.infoData[0]);
                        });
                        this.infoModal = {
                            ...this.infoModal,
                            loading: false
                        };
                        this.$refs.xGreatSendSaleTable.updateData();
                    } else {
                        this.$message.error(res.msg);
                        this.infoModal.loading = false;
                    }
                })
                .catch(() => {
                    this.infoModal.loading = false;
                });
        },
        changeUnitPriceFn(row, curPrice) {
            row.tax_unit_price = curPrice;
            this.updateFooterPlanModalFn();
        },
        reNumber(data) {
            let num = data.toString();
            let numA = 0;
            if (num.indexOf('.') !== -1) {
                numA = num.split('.')[1].length;
            }
            if (numA === 3) {
                data += 0.0001;
            }
            if (numA > 10) {
                data = this.$XEUtils.commafy(data, { fixed: 4 });
            }
            return parseFloat(data);
        },
        // 【添加销售订单】
        async editSave(mode) {
            if (!this.checkPlanModalFormSendFn('edit')) return;

            if ((mode === 1 && this.infoModal.loadingSave) || (mode === 2 && this.infoModal.loadingAudit)) {
                return;
            }

            /**
             * 新增时，将所有添加的产品赋予details中
             * 编辑时，将添加的产品，编辑过的产品，以及删除的产品添加到details中，编辑过的产品是指action=2，且这条记录任意的字段值有改变过，才会被加入到infoDataOfEdit中
             * 删除的产品infoDataDet也是针对于action=2的产品record，只有这种record被删除时，才会被加入到infoDataDet。
             */
            const details = [];
            this.infoAllData.department_id = this.infoAllData.department_inner_sn;
            this.infoAllData.collection_date =
                this.infoAllData.collection_date && Object.keys(this.infoAllData.collection_date).length
                    ? moment(this.infoAllData.collection_date).format('YYYY-MM-DD')
                    : '';

            this.infoData.forEach((item) => {
                let detailItem = {
                    ...item,
                    unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                    unit: item.unit_name,
                    rate: this.reNumber(item.rate / 100),
                    unit_price:
                        item.tax_unit_price === 'undefined' || isNaN(item.tax_unit_price)
                            ? 0
                            : parseFloat(item.tax_unit_price).toFixed(4) // 传递给后端的字段取值变为 【单价 = 含税单价   含税单价 = 单价】
                };
                if (item.action === 1) {
                    details.push(detailItem);
                } else if (item.action === 2) {
                    const itemPrimaryKey = `${item.detail_inner_sn}-${item.product_type}`;
                    if (
                        this.infoDataEdit.includes(itemPrimaryKey) ||
                        item.unit_price.toString() !== item.tax_unit_price.toString()
                    ) {
                        details.push(detailItem);
                    }
                }
            });

            // this.infoData.concat(this.infoDataDet)
            // this.infoAllData.details = this.infoData.map((item) => {
            //     return {
            //         ...item,
            //         unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
            //         unit: item.unit_name,
            //         rate: this.reNumber(item.rate / 100)
            //         // min_unit_for_sale_quantity: 0,
            //         // min_unit_for_sale_quantity_old: 0
            //     }
            // })
            this.infoAllData.details = details.concat(this.infoDataDet);
            let params = {
                ...this.infoAllData,
                status: mode
            };

            await saleRecordEditAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.currentTrObj.status_text = this.reObjName(this.statusOption, mode);
                        this.currentTrObj.department_name = this.infoAllData.department_name;
                        let EmployeeInfo = this.$cookie.get('EmployeeInfo');
                        if (mode === 2) this.currentTrObj.auditor_name = JSON.parse(EmployeeInfo).Name;
                        this.resetSalesModalFn();
                        this.infoModal.alert = false;
                        this.infoModal.loading = false;
                        this.saleListFn();
                    } else {
                        this.$message.error(res.msg);
                        this.infoModal.loading = false;
                    }
                })
                .catch(() => {
                    this.infoModal.loading = false;
                })
                .finally(() => {
                    this.resetBtnLoadingStatus();
                });
        },
        // 重置操作按钮的loading状态
        resetBtnLoadingStatus() {
            this.infoModal = {
                ...this.infoModal,
                loadingSave: false,
                loadingAudit: false
            };
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
        async addSave() {
            if (!this.checkPlanModalFormSendFn('add')) return;
            if (this.infoModal.loadingSave) {
                return;
            }
            this.infoAllData.department_id = this.infoAllData.department_inner_sn;
            // this.infoAllData.organization_name = this.infoAllData.organization_name; // this.reObjName(this.planModalOrgs, this.planModalOrgSn)
            // this.infoAllData.organization_inner_sn = this.infoAllData.organization_inner_sn; // this.rePlanModalId(this.planModalOrgs, this.planModalOrgSn)
            this.infoAllData.collection_date =
                this.infoAllData.collection_date && Object.keys(this.infoAllData.collection_date).length
                    ? moment(this.infoAllData.collection_date).format('YYYY-MM-DD')
                    : '';
            this.infoAllData.org_id = parseFloat(this.$cookie.get('userbelong'));
            this.infoAllData.billsType = this.billsType;
            this.infoAllData.details = this.infoData.map((item) => {
                return {
                    ...item,
                    unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                    unit: item.unit_name,
                    rate: this.reNumber(item.rate / 100),
                    unit_price:
                        item.tax_unit_price === 'undefined' || isNaN(item.tax_unit_price)
                            ? 0
                            : parseFloat(item.tax_unit_price).toFixed(4) // 传递给后端的字段取值变为 【单价 = 含税单价   含税单价 = 单价】
                    // min_unit_for_sale_quantity: 0
                };
            });

            let params = {
                ...this.infoAllData
            };
            this.infoModal.loadingSave = true;
            await saleRecordAddAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res,
                            infoNew = {
                                ...info,
                                status_text: this.reObjName(this.statusOption, info.status)
                            };
                        this.salesListStatus.total++;
                        const salesTable = this.$refs.salesTable,
                            recordDelar = salesTable.getCurrentRow();
                        salesTable.insertAt(infoNew, recordDelar).then(({ row }) => {
                            this.salesListDatas = salesTable.getTableData().tableData;
                            salesTable.setCurrentRow(row);
                            this.currentTrObj = row;
                            if (this.salesListDatas.length > this.salesListStatus.pageSize) {
                                this.salesListDatas.splice(this.salesListDatas.length - 1, 1);
                            }
                        });

                        this.resetSalesModalFn();
                        this.infoModal.alert = false;
                        this.infoModal.loading = false;
                    } else {
                        this.$message.error(res.msg);
                        this.infoModal.loading = false;
                    }
                })
                .finally(() => {
                    this.resetBtnLoadingStatus();
                });
        },
        addProductFn() {
            this.$refs.chooseProMd.returnProList();
        },
        clearRowClassNameFn() {
            // 去除新增的临时背景颜色
            this.infoData.forEach((item) => {
                if (item.row_add_flag) {
                    item.row_add_flag = false;
                }
            });
        },

        // 获取回调商品
        getProReturn(dea, ven) {
            dea.forEach((item) => {
                item.product_vendor_name = item.dealer_name;
                item.product_vendor_inner_sn = item.dealer_inner_sn || item.dealer_b_inner_sn;
                // 注明是新增
                item.action = 1;
                // 批号
                item.batch_number = '';
                item.sl_inner_sn = this.infoAllData.sl_inner_sn;
            });
            ven.forEach((item) => {
                item.product_vendor_name = item.vendor_name;
                item.product_vendor_inner_sn = item.vendor_inner_sn;
                // 注明是新增
                item.action = 1;
                // 批号
                item.batch_number = '';
                item.sl_inner_sn = this.infoAllData.sl_inner_sn;
            });
            this.addProductMd.alert = false;
            this.planModalPagination.loading = true;
            this.clearRowClassNameFn();
            const xTableAddInfo = this.$refs.xGreatSendSaleTable,
                oldAddDatas = [...ven, ...dea].map((item) => {
                    return {
                        ...item,
                        unitPriceList: []
                    };
                }),
                len = this.infoData.length;
            let curObj = xTableAddInfo.getCurrentRow();
            if (!curObj) {
                for (let i = 0; i < len; i++) {
                    const item = this.infoData[i];
                    if (item.temp_add_flag) {
                        if (!curObj) {
                            curObj = item;
                        }
                        delete item.temp_add_flag;
                    }
                }
                xTableAddInfo.reloadData(this.infoData);
            }
            xTableAddInfo.clearCurrentRow();
            if (len) {
                for (let i = 0; i < len; i++) {
                    const item = this.infoData[i];
                    if (item._XID === curObj._XID) {
                        this.infoData.splice(i, 0, ...oldAddDatas);
                        xTableAddInfo.refreshData();
                        xTableAddInfo.loadData(this.infoData);
                        break;
                    }
                }
            } else {
                // 完全新增
                this.infoData.push(...oldAddDatas);
                xTableAddInfo.loadData(this.infoData);
            }
            for (let i = 0; i < this.infoData.length; i++) {
                let item = this.infoData[i];
                if (item.unitPriceList.length <= 0) {
                    // 是草稿和正式
                    this.contractGetContractPriceAPIFn(item);
                }
            }
            let timer = setTimeout(() => {
                this.planModalPagination.loading = false;
                clearTimeout(timer);
                timer = null;
            }, 200);
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
                    const { list } = res;
                    this.treeDatasIndexAryFn(list, 'deptTopTree');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 递归加上索引
        treeDatasIndexAryFn(datasOrigin, isAppendDatas, id) {
            // datasOrigin: 源数据  isAppendDatas: 将数据放在哪里  id: 当前点击的是哪个id标识
            const _this = this;
            // 格式化部门名称
            function formatDepartmentNameFn(strComma) {
                // 研发部门,行政部门,其他
                if (!strComma) return strComma;
                return strComma.replace(/,/g, ' ');
            }
            // 子级递归
            function childrenIndexFn(list, pIndexAry, parentDepartmentNameOld, pItemDisabledFlag) {
                if (!pIndexAry || !parentDepartmentNameOld) return;
                return list.map((cItem, cIndex) => {
                    let cItemDisabledFlag =
                        _this.btnName === 'edit' &&
                        (pItemDisabledFlag || (isAppendDatas === 'parentDirectors' && id === cItem.employee_inner_sn));
                    if (cItem.children && cItem.children.length > 0) {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn,
                            activeClass: '',
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            department_name_new: formatDepartmentNameFn(cItem.department_name),
                            disabled:
                                cItemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(cItem.status) <= 0),
                            value: (cItem.employee_inner_sn || cItem.department_inner_sn).toString(),
                            label: cItem.name,
                            isLeaf: false,
                            children: childrenIndexFn(
                                cItem.children,
                                [...pIndexAry, cIndex],
                                [...parentDepartmentNameOld, cItem.name],
                                cItemDisabledFlag
                            )
                        };
                        if (obj.key === _this.infoKey) {
                            // 新增下级同级的时候选中新增的那一条
                            _this.currentTrObj = obj;
                        }
                        return obj;
                    } else {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.employee_inner_sn || cItem.department_inner_sn,
                            activeClass: '',
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.name],
                            department_name_new: formatDepartmentNameFn(cItem.department_name),
                            disabled:
                                cItemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(cItem.status) <= 0),
                            value: (cItem.employee_inner_sn || cItem.department_inner_sn).toString(),
                            label: cItem.name,
                            children: cItem.children
                        };
                        if (obj.key === _this.infoKey) {
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
                if (item.children && item.children.length > 0) {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn,
                        activeClass: '',
                        parentDepartmentName: [item.name],
                        department_name_new: formatDepartmentNameFn(item.department_name),
                        disabled: itemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(item.status) <= 0),
                        value: (item.employee_inner_sn || item.department_inner_sn).toString(),
                        label: item.name,
                        isLeaf: false,
                        children: childrenIndexFn(item.children, [index], [item.name], itemDisabledFlag)
                    };
                    if (obj.key === _this.infoKey) {
                        // 新增下级同级的时候选中新增的那一条
                        _this.currentTrObj = obj;
                    }
                    return obj;
                } else {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.employee_inner_sn || item.department_inner_sn,
                        activeClass: '',
                        parentDepartmentName: [item.name],
                        department_name_new: formatDepartmentNameFn(item.department_name),
                        disabled: itemDisabledFlag || (isAppendDatas === 'deptTopTree' && parseInt(item.status) <= 0),
                        value: (item.employee_inner_sn || item.department_inner_sn).toString(),
                        label: item.name,
                        children: item.children
                    };
                    if (obj.key === _this.infoKey) {
                        // 新增下级同级的时候选中新增的那一条
                        _this.currentTrObj = obj;
                    }
                    return obj;
                }
            });

            if (isAppendDatas) {
                switch (isAppendDatas) {
                    case 'deptTopTree': // 所属部门
                        this.superiorDepartments = datasOriginNew;
                        // this.architectureAllTrees = datasOriginNew

                        break;
                    case 'allEmpDropdownList': // 经手人
                        this.handlerNameList = datasOriginNew;

                        break;
                }
            }
        },
        record_money_total(record) {
            // 金额 = 数量 * 单价
            const rate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100,
                nextItem = (parseFloat(record.tax_unit_price) / (1 + rate)) * parseFloat(record.quantity);
            if (!isNaN(nextItem)) {
                return nextItem.toFixed(2);
                // return this.$XEUtils.commafy(nextItem, {fixed: 2})
            } else {
                return '';
            }
        },
        record_discount_unit_price_total(record) {
            const nextItem = parseFloat(record.unit_price) * parseFloat(record.discount);
            if (!isNaN(nextItem)) {
                return this.$XEUtils.commafy(nextItem, { fixed: 2 });
            } else {
                return '';
            }
        },
        updateFooterPlanModalFn(row) {
            this.collectInfoDataOfEdit(row);
            this.$refs.xGreatSendSaleTable.updateFooter();
        },
        // 收集编辑过的产品列表数据infoDataEdit
        collectInfoDataOfEdit(row) {
            if (!row && !this.currentTrPlanModalObj['_XID']) {
                console.log('当前没有选中记录');
                return;
            }
            const currentRow = row || this.currentTrPlanModalObj;
            const action = currentRow.action;
            const detailInnerSn = currentRow.detail_inner_sn;
            const productType = currentRow.product_type;
            const primaryKey = `${detailInnerSn}-${productType}`;
            if (action === 2 && !this.infoDataEdit.includes(primaryKey)) {
                this.infoDataEdit.push(primaryKey);
            }
        },
        discount_money_total() {
            return this.infoData.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    nextItem = parseFloat(next.unit_price) * parseFloat(next.quantity) * parseFloat(next.discount);
                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return prevItem + nextItem;
                } else {
                    return '';
                }
            }, 0);
        },
        record_discount_money_total(record) {
            const nextItem = parseFloat(record.unit_price) * parseFloat(record.quantity) * parseFloat(record.discount);
            if (!isNaN(nextItem)) {
                return this.$XEUtils.commafy(nextItem, { fixed: 2 });
            } else {
                return '';
            }
        },
        rate_total() {
            return this.infoData.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    nextItem = parseFloat(next.rate);
                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return prevItem + nextItem;
                } else {
                    return '';
                }
            }, 0);
        },
        tax_unit_price_total() {
            return this.infoData.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    nextItem = parseFloat(next.unit_price) * parseFloat(next.discount) * (1 + parseFloat(next.rate));
                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return (prevItem + nextItem).toFixed(2);
                } else {
                    return '';
                }
            }, 0);
        },
        record_unit_price(record) {
            // 单价 = 含税单价 / (1 + 税率 / 100)
            const rate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100;
            let nextItem = parseFloat(record.tax_unit_price) / (1 + rate);
            if (!isNaN(nextItem)) {
                if (nextItem < 0) nextItem = 0;
                return nextItem.toFixed(4);
                // return this.$XEUtils.commafy(nextItem, {fixed: 4})
            } else {
                return '';
            }
        },
        record_tax_unit_price_total(record) {
            const rate = record.rate <= 0 ? 0 : parseFloat(record.rate) / 100;
            let nextItem = parseFloat(record.unit_price) * parseFloat(record.discount) * (1 + rate);
            if (!isNaN(nextItem)) {
                // nextItem = this.reNumber(nextItem)
                return this.$XEUtils.commafy(nextItem, { fixed: 2 });
            } else {
                return '';
            }
        },
        tax_amount_total() {
            return this.infoData.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    taxUnitPrice = next.tax_unit_price || 0,
                    nextItem = parseFloat(next.quantity) * parseFloat(taxUnitPrice);
                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return (prevItem + nextItem).toFixed(2);
                } else {
                    return '';
                }
            }, 0);
        },
        record_tax_amount_total(record) {
            // 含税金额 = 数量 * 含税单价
            let nextItem = record.quantity * parseFloat(record.tax_unit_price);
            if (!isNaN(nextItem)) {
                // nextItem = this.reNumber(nextItem)
                return this.$XEUtils.commafy(nextItem, { fixed: 2 });
            } else {
                return '';
            }
        },
        tax_total() {
            return this.infoData.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    rate = next.rate < 0 ? 0 : parseFloat(next.rate) / 100,
                    unitPrice = ((parseFloat(next.tax_unit_price) / (1 + rate)) * parseFloat(next.quantity)).toFixed(2),
                    nextItem =
                        (parseFloat(next.quantity) * parseFloat(next.tax_unit_price)).toFixed(2) -
                        parseFloat(unitPrice);

                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return (prevItem + nextItem).toFixed(2);
                } else {
                    return '';
                }
            }, 0);
        },
        record_tax_total(record) {
            // 税额 = 含税金额 - 金额
            const rate = record.rate < 0 ? 0 : parseFloat(record.rate) / 100,
                unitPrice = ((parseFloat(record.tax_unit_price) / (1 + rate)) * parseFloat(record.quantity)).toFixed(2);
            let nextItem =
                (parseFloat(record.quantity) * parseFloat(record.tax_unit_price)).toFixed(2) - parseFloat(unitPrice);
            if (!isNaN(nextItem)) {
                // nextItem = this.reNumber(nextItem)
                if (nextItem < 0) nextItem = 0;
                return nextItem.toFixed(2);
                // return this.$XEUtils.commafy(nextItem, {fixed: 2})
            } else {
                return '';
            }
        },
        footerCellClassNameFn({ column }) {
            const redAry = ['money', 'discount_money', 'tax_amount', 'tax'];
            if (redAry.some((item) => item === column.property)) {
                return 'red bold';
            }
            if (column.property === 'index') {
                return 'total-bg bold';
            }
        },
        footerMethodFn({ columns }) {
            return [
                columns.map((column, columnIndex) => {
                    if (columnIndex === 0) {
                        return '合计';
                    }
                    if (column.property === 'money') {
                        return this.$XEUtils.commafy(this.money_total(), { fixed: 2 });
                    }
                    if (column.property === 'discount_money') {
                        return this.$XEUtils.commafy(this.discount_money_total(), { fixed: 2 });
                    }
                    if (column.property === 'tax_amount') {
                        return this.$XEUtils.commafy(this.tax_amount_total(), { fixed: 2 });
                    }
                    if (column.property === 'tax') {
                        return this.tax_total();
                        // return this.$XEUtils.commafy(this.tax_total(), {fixed: 2})
                    }
                })
            ];
        },
        money_total() {
            return this.infoData.reduce((prev, next) => {
                let prevItem = parseFloat(prev),
                    taxUnitPrice = next.tax_unit_price || 0,
                    nextItem = (parseFloat(taxUnitPrice) / (1 + next.rate / 100)) * parseFloat(next.quantity);

                if (!isNaN(prevItem) && !isNaN(nextItem)) {
                    return (prevItem + nextItem).toFixed(2);
                } else {
                    return '';
                }
            }, 0);
        },
        // 包装单位区域
        clickUnitListFn(record) {
            this.collectInfoDataOfEdit(record);
            if (!record.isLoadingUnit && !this.isEditDisabled) {
                if (!record.detail_inner_sn || this.btnName === 'add') {
                    parseFloat(record.product_type) === 1
                        ? this.platformUnitTreeFn(record, 'add')
                        : this.dealerUnitTreeAPIFn(record, 'add');
                } else if (this.btnName === 'edit') {
                    parseFloat(record.product_type) === 1
                        ? this.platformUnitTreeFn(record)
                        : this.dealerUnitTreeAPIFn(record);
                }
            }
            this.record = record;
        },

        // 【厂商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async platformUnitTreeFn(record, btnName) {
            await platformUnitTree(
                record.vendor_inner_sn || record.vendor_b_inner_sn || record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let circle = (item, parentName) => {
                        // 递归遍历
                        item.forEach((item) => {
                            item.label = `${item.name}(${item.measure}×${parentName})`;
                            item.value = item.unit_inner_sn;
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item.name);
                            } else {
                                item.children = null;
                            }
                        });
                    };
                    res.tree.forEach((item) => {
                        item.label = item.name;
                        item.value = item.unit_inner_sn;
                        if (item.children && item.children.length > 0) {
                            circle(item.children, item.name);
                        } else {
                            item.children = null;
                        }
                    });
                    if (res.tree.length) {
                        record.unitList = res.tree;
                        const curUnit = record.unitList[0];
                        if (btnName === 'add') {
                            // 赋默认值
                            record.unit_inner_sn = [curUnit.value];
                            record.unit_name = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xGreatSendSaleTable.reloadRow(record, record, 'unit');
                        }
                        if (btnName === 'great-plan') {
                            record.unit_inner_sn_new = [curUnit.value];
                            record.unit = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xGreatSendSaleTable.reloadRow(record, record, 'unit');
                            this.setPackgeMultipleFn();
                        } else {
                            record.detail_inner_sn && !record.isLoadingUnit && this.platformUnitParentsFn(record);
                        }
                    }
                }
            });
        },
        // 【经销商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async dealerUnitTreeAPIFn(record, btnName) {
            await dealerUnitTreeAPI(
                record.dealer_inner_sn ||
                    record.dealer_b_inner_sn ||
                    record.product_vendor_inner_sn ||
                    record.product_dealer_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn
            ).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let circle = (item, parentName) => {
                        // 递归遍历
                        item.forEach((item) => {
                            item.label = `${item.name}(${item.measure}×${parentName})`;
                            item.value = item.unit_inner_sn;
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item.name);
                            } else {
                                item.children = null;
                            }
                        });
                    };
                    res.tree.forEach((item) => {
                        item.label = item.name;
                        item.value = item.unit_inner_sn;
                        if (item.children && item.children.length > 0) {
                            circle(item.children, item.name);
                        } else {
                            item.children = null;
                        }
                    });
                    if (res.tree.length) {
                        record.unitList = res.tree;
                        const curUnit = record.unitList[0];
                        if (btnName === 'add') {
                            // 赋默认值
                            record.unit_inner_sn = [curUnit.value];
                            record.unit_name = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xGreatSendSaleTable.reloadRow(record, record, 'unit');
                        }

                        if (btnName === 'great-plan') {
                            record.unit_inner_sn_new = [curUnit.value];
                            record.unit = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xGreatSendPlanTable.reloadRow(record, record, 'unit');
                            this.setPackgeMultipleFn();
                        } else {
                            record.detail_inner_sn && !record.isLoadingUnit && this.dealerUnitParentsAPIFn(record);
                        }
                    }
                }
            });
        },
        // 【厂商 产品单位】 根据id查询所有上级产品单位
        async platformUnitParentsFn(record) {
            await platformUnitParents(
                record.product_vendor_inner_sn,
                record.product_inner_sn,
                record.specification_inner_sn,
                record.unit_inner_sn
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
        planModalGroupBtnSendFn(btnName) {
            if (
                !(btnName === 'print' || btnName === 'cancel' || btnName === 'exportExcel') &&
                !this.checkPlanModalFormSendFn(btnName)
            )
                return;
            switch (btnName) {
                case 'exportExcel': // 导出excel
                    this.exportExcel();
                    break;
                case 'print': // 打印
                    this.handlePrint();
                    break;
                case 'cancel': // 取消
                    this.closeSalesModalFn();
                    this.resetSalesModalFn();
                    break;
                case 'saveDraft': // 存草稿
                    if (this.planModal.loadingSaveDraft) {
                        return;
                    }
                    this.planModal.loadingSaveDraft = true;
                    this.orderSaleBuildAPIFn(0);
                    break;
                case 'save': // 保存
                    if (this.planModal.loadingSave) {
                        return;
                    }
                    this.planModal.loadingSave = true;
                    this.orderSaleBuildAPIFn(1);
                    break;
                case 'savePo':
                    // if (!this.planModalForm.so_sn) {
                    //     this.$warning({
                    //         title: '操作提示',
                    //         content: '请填写右上角的单据编号再保存。',
                    //         okText: '知道了',
                    //         centered: true,
                    //         maskClosable: false
                    //     })
                    //     return false
                    // }
                    if (this.planModal.loadingSave) {
                        return;
                    }
                    this.planModal.loadingSave = true;
                    this.orderSaleBuildAPIFn(1);
                    break;
                case 'audit': // 审核
                    // this.planModal.loadingAudit = true
                    // this.orderSaleBuildAPIFn(2)
                    break;
            }
        },
        // 打印
        handlePrint() {
            const { columns, tableData } = this._genVisibleTableData();
            let printContent = {
                printData: tableData,
                printTable: columns,
                printType: 8,
                printForm: {
                    ...this.infoAllData,
                    collection_date:
                        this.infoAllData.collection_date && Object.keys(this.infoAllData.collection_date).length
                            ? moment(this.infoAllData.collection_date).format('YYYY-MM-DD')
                            : '',
                    so_sn: this.infoAllData.sl_sn, // 单据编号
                    creator_name: this.infoAllData.making_people_name, // 制单人
                    create_time: this.infoAllData.billing_time // 生成时间
                },
                // 合计对象
                totalObj: {
                    // 合计金额
                    moneyTotal: this.$XEUtils.commafy(this.money_total(), { fixed: 2 }),
                    // 折后金额
                    discountMoneyTotal: this.$XEUtils.commafy(this.discount_money_total(), { fixed: 2 }),
                    // 含税金额
                    taxAmountTotal: this.$XEUtils.commafy(this.tax_amount_total(), { fixed: 2 }),
                    // 税额
                    taxTotal: this.tax_total()
                    // taxTotal: this.$XEUtils.commafy(this.tax_total(), {fixed: 2})
                }
            };
            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        // 导出
        exportExcel() {
            const { columns, tableData = [], footerData } = this._genVisibleTableData();
            const form = this.infoAllData;
            const formData = {
                title: '销售单',
                desc: {
                    label: '摘要',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '相关机构',
                            value: form.organization_name
                        },
                        {
                            label: '销售员',
                            value: form.seller_name
                        },
                        {
                            label: '销售部门',
                            value: form.department_name
                        },
                        {
                            label: '单据编号',
                            value: form.sl_sn
                        }
                    ],
                    [
                        {
                            label: '采购方联系人',
                            value: form.purchaser_linkman
                        },
                        {
                            label: '联系电话',
                            value: form.purchaser_phone
                        },
                        {
                            label: '销售方联系人',
                            value: form.seller_linkman
                        },
                        {
                            label: '联系电话',
                            value: form.seller_phone
                        }
                    ],
                    [
                        {
                            label: '收款日期',
                            // value: form.collection_date
                            value:
                                form.collection_date && Object.keys(form.collection_date).length
                                    ? moment(form.collection_date).format('YYYY-MM-DD')
                                    : ''
                        },
                        {
                            label: '制单人',
                            value: form.making_people_name
                        },
                        {
                            label: '生成时间',
                            value: form.billing_time
                        },
                        {
                            label: '审核人',
                            value: form.auditor_name
                        }
                    ]
                ]
            };
            this.$Utils.exportExcel(
                columns,
                tableData.map((i) => ({ ...i, rate: `${i.rate}%` })),
                footerData,
                formData
            );
        },
        // 生成打印和导出table的显示数据（状态文本名称，包装名称文本，金额计算等）
        _genVisibleTableData() {
            const table = this.$refs['xGreatSendSaleTable'];
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
            const { tableData, footerData } = table.getTableData();
            return {
                columns, // 表头
                footerData, // 表尾数据
                tableData: tableData.map((item, index) => {
                    // 表体数据
                    return {
                        ...item,
                        index: index + 1,
                        money: this.record_money_total(item),
                        discount_unit_price: this.record_discount_unit_price_total(item),
                        discount_money: this.record_discount_money_total(item),
                        // tax_unit_price: this.record_tax_unit_price_total(item), // 含税单价
                        unit_price: this.record_unit_price(item),
                        tax_amount: this.record_tax_amount_total(item),
                        tax: this.record_tax_total(item)
                    };
                })
            };
        },
        // 关闭采购计划单模态框
        closeSalesModalFn() {
            this.infoModal = {
                title: `${this.$route.meta.title} - 销售单`,
                alert: false,
                loading: false,
                saveLoading: false
            };
        },

        /**
         * 【相关机构列表】
         *  isSale: 是否销售订单 0-不是 1-是
         */
        async orderOrgListAPIFn(value) {
            // 默认赋值
            if (this.btnName === 'add') {
                if (this.planModalOrgs.length) {
                    const curOrg = value;
                    const arySplitValue = curOrg.split('-');
                    this.planModalOrgSn = [curOrg];
                    this.infoAllData.partA = arySplitValue[0];
                    this.infoAllData.organization_inner_sn = arySplitValue[0];
                    this.billsType = arySplitValue[1];
                    this.partC.partA_id = arySplitValue[0];
                    this.partC.billsType = arySplitValue[1];
                    if (this.userSystemType === 2) {
                        // 经销商对经销商 医院对经销商 => 把机构名称读取
                        if (this.billsType === BillsType.DealerDealer || this.billsType === BillsType.DealerHospital) {
                            this.partC.oname = this.orgName;
                        } else {
                            this.partC.oname = this.planModalOrgs[0].name;
                        }
                    } else if (this.userSystemType === 3) {
                        // 经销商对厂商 医院对厂商 => 把机构名称读取
                        if (this.billsType === BillsType.DealerVendor || this.billsType === BillsType.HospitalVendor) {
                            this.partC.oname = this.orgName;
                        } else {
                            this.partC.oname = this.planModalOrgs[0].name;
                        }
                    }
                } else {
                    this.planModalOrgSn = [];
                    this.infoAllData.partA = '';
                    this.billsType = '';
                    this.partC.oname = '';
                }
            }
            this.infoAllData.billsType = this.partC.billsType;
        },
        changePlanUnitListFn(value, selectedOptions) {
            const len = selectedOptions.length,
                unit = len ? selectedOptions[len - 1].label : '';
            this.greatSendDatas.forEach((item) => {
                if (item.key === this.record.key) {
                    item.unit = unit;
                }
            });
            if (len) {
                const recordUnitSn = value[value.length - 1],
                    curUnitSn = this.record.min_unit_inner_sn;
                if (parseFloat(recordUnitSn) === parseFloat(curUnitSn)) {
                    this.setPackgeMultipleFn();
                } else {
                    parseFloat(this.record.product_type) === 1
                        ? this.platformUnitMinAPIFn(this.record, recordUnitSn)
                        : this.dealerUnitMinAPIFn(this.record, recordUnitSn);
                }
            }
        },
        checkPlanModalFormSendFn() {
            let flag = true;
            if (this.infoData.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请双击表格内容部分添加产品',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            if (!this.infoAllData.seller_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择销售员',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            if (!this.infoAllData.department_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择销售部门',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }
            // if (this.infoAllData.purchaser_phone && !this.$Utils.regExpFn().mobilePhone.test(this.infoAllData.purchaser_phone)) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '采购方联系电话格式输入有误',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     flag = false
            //     return
            // }
            // if (this.infoAllData.seller_phone && !this.$Utils.regExpFn().mobilePhone.test(this.infoAllData.seller_phone)) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '销售方联系电话格式输入有误',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     flag = false
            //     return
            // }
            // 判断表格主体的数量 - 包装单位
            const datas = this.infoData;
            if (!datas.every((item) => item.quantity && item.unit_inner_sn.length)) {
                this.$warning({
                    title: '操作提示',
                    content: '请填写数量/包装单位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                flag = false;
                return;
            }

            if (flag) {
                let isRepeatObj = {};
                for (let i = 0; i < datas.length; i++) {
                    const item = datas[i],
                        len = 3, // 小数的有效长度是2位
                        quantityNew = (item.quantity + '').split('.'),
                        quantityLen = quantityNew.length >= 2 ? quantityNew[quantityNew.length - 1].length : 0,
                        unitPriceNew = (item.tax_unit_price + '').split('.'),
                        unitPriceLen = unitPriceNew.length >= 2 ? unitPriceNew[unitPriceNew.length - 1].length : 0,
                        discountNew = (item.discount + '').split('.'),
                        discountLen = discountNew.length >= 2 ? discountNew[discountNew.length - 1].length : 0,
                        rateNew = (item.rate + '').split('.'),
                        rateLen = rateNew.length >= 2 ? rateNew[rateNew.length - 1].length : 0;
                    if (quantityLen > len) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】数量的小数长度只能是三位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (unitPriceLen > 4) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】含税单价的小数长度只能是四位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (discountLen > len) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】折扣的小数长度只能是两位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }
                    if (rateLen > len) {
                        this.$warning({
                            title: '操作提示',
                            content: `【NO.${i + 1} - ${item.product_name}】税率的小数长度只能是两位`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        flag = false;
                        break;
                    }

                    if (flag) {
                        // 产品只能出现唯一
                        const key = `${item.product_type}==${item.product_inner_sn}==${item.specification_inner_sn}==${
                            item.product_vendor_inner_sn || item.vendor_inner_sn || item.dealer_inner_sn
                        }`;
                        if (!isRepeatObj[key]) {
                            isRepeatObj[key] = `NO.${i + 1}`;
                        } else {
                            this.$warning({
                                title: '操作提示',
                                content: `${isRepeatObj[key]}和NO.${i + 1}产品相同，不能重复添加。`,
                                okText: '知道了',
                                centered: true,
                                maskClosable: true
                            });
                            flag = false;
                            break;
                        }
                    }
                }
                isRepeatObj = null;
            }

            if (flag) {
                this.detailList = datas.map((item) => {
                    return {
                        detail_inner_sn: item.detail_inner_sn || '',
                        product_vendor_inner_sn:
                            item.product_vendor_inner_sn || item.vendor_inner_sn || item.dealer_inner_sn,
                        product_vendor_name: item.product_vendor_name || item.vendor_name || item.dealer_name,
                        product_inner_sn: item.product_inner_sn,
                        product_name: item.product_name,
                        specification_inner_sn: item.specification_inner_sn,
                        specification: item.specification,
                        unit_inner_sn: item.unit_inner_sn[item.unit_inner_sn.length - 1],
                        unit: item.unit_name,
                        product_type: item.product_type,
                        quantity: item.quantity,
                        unit_price: item.tax_unit_price || 0,
                        discount: item.discount,
                        rate: item.rate,
                        sort_number: item.index,
                        mdrf_sn: item.mdrf_sn,
                        mdrf_inner_sn: item.mdrf_inner_sn,
                        batch_number: item.batch_number
                    };
                });
            }
            return flag;
        },
        openChooseTb() {
            // 打开相关机构
            this.pageTypeName = 8;
            this.chooseOrgMd.alert = true;
            this.$Utils.globalDragCenterFn('choose_org_md');
            this.$nextTick(() => {
                if (this.$refs.chooseOrgMd) {
                    this.$refs.chooseOrgMd.setContractOrgCheckedFn();
                    this.$refs.chooseOrgMd.pageDataInit();
                }
            });
        },
        chooseOrgFn(resObj) {
            // 获取选中相关机构信息
            const objValue = resObj.org_id + '-' + resObj.billsType,
                objLabel = resObj.name;
            this.orderOrgListAPIFn(objValue, objLabel);
            this.selectOrganizationFn(objValue, objLabel);
            this.chooseOrgMd.alert = false;
        },
        // 获取医保编号/阳光流水号下拉
        async getProductSnAPIFn(isHealthCare, row) {
            if (this.isEditDisabled) {
                return;
            }
            console.log(row);
            this.healthAndSunSnModal.data = [];
            let params = {
                vendor_inner_sn: row.product_vendor_inner_sn,
                product_inner_sn: row.product_inner_sn,
                specification_inner_sn: row.specification_inner_sn,
                pageNum: 1,
                pageSize: 10,
                type: isHealthCare ? 'HEALTH_CARE_SN' : 'SUN_SN'
            };
            await productSnListAPI({ params }).then((res) => {
                this.healthAndSunSnModal.data = res.list.map((item, index) => {
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
            });
        },
        // 获取医保编号输入
        healthCareSnChangeFn(item, seq) {
            this.infoData[seq - 1].health_care_sn = item.sn;
            this.$refs.xGreatSendSaleTable.updateData();
        },
        // 获取阳光流水号输入
        sunSnChangeFn(item, seq) {
            this.infoData[seq - 1].sun_sn = item.sn;
            this.$refs.xGreatSendSaleTable.updateData();
        },
        modalFullscreen() {
            const clientHeight = document.body.clientHeight;
            const clientWidth = document.body.clientWidth;
            if (!this.isFullscreen) {
                for (let i = 0; i < document.getElementsByClassName('ant-modal').length; i++) {
                    document.getElementsByClassName('ant-modal')[i].style.height = clientHeight + 24 + 'px';
                    document.getElementsByClassName('ant-modal')[i].style.width = clientWidth + 'px';
                    document.getElementsByClassName('ant-modal')[i].style.top = '0px';
                    document.getElementsByClassName('ant-modal')[i].style.left = '0px';
                    document.getElementsByClassName('ant-modal-content')[i].style.height = clientHeight + 'px';
                    document.getElementsByClassName('ant-modal-content')[i].style.width = clientWidth + 'px';
                    document.getElementsByClassName('ant-modal-body')[i].style.height = clientHeight - 57 + 'px';
                    document.getElementsByClassName('ant-modal-body')[i].style.width = clientWidth + 'px';
                    document.getElementsByClassName('tableFather')[i].style.height = clientHeight - 420 + 'px';
                    this.isFullscreen = true;
                }
            } else {
                for (let i = 0; i < document.getElementsByClassName('ant-modal').length; i++) {
                    document.getElementsByClassName('ant-modal')[i].style.height = '673px';
                    document.getElementsByClassName('ant-modal')[i].style.width = '1200px';
                    document.getElementsByClassName('ant-modal')[i].style.top = '64.5px';
                    document.getElementsByClassName('ant-modal')[i].style.left = '168px';
                    document.getElementsByClassName('ant-modal-content')[i].style.height = '649px';
                    document.getElementsByClassName('ant-modal-content')[i].style.width = '1200px';
                    document.getElementsByClassName('ant-modal-body')[i].style.height = '592px';
                    document.getElementsByClassName('ant-modal-body')[i].style.width = '1200px';
                    document.getElementsByClassName('tableFather')[i].style.height = '230px';
                    this.isFullscreen = false;
                }
            }
        },
        fullscreenMouseOver(isOut) {
            for (let i = 0; i < document.getElementsByClassName('modal-fullscreen').length; i++) {
                document.getElementsByClassName('modal-fullscreen')[i].style.background = isOut ? '' : '#1387e0';
            }
        },
        /** 修改或新增订单时，税率改变触发的方法 */
        tableRateChangeFn(e) {
            const record = this.$refs.xGreatSendSaleTable.getCurrentRecord();
            const index = this.$refs.xGreatSendSaleTable.getRowIndex(record);
            let v = e.target.value ? parseInt(e.target.value) : 0;

            v = v > 100 ? 100 : v < 0 ? 0 : v;
            this.infoData[index].rate = v;
            this.updateFooterPlanModalFn(record);
        }
    },
    created() {
        this.partC.oname = '';
        this.planModalOrgSn = [];
        this.saleListFn();
        this.pageType = parseFloat(this.$cookie.get('userbelong'));
    }
};
</script>

<style lang="less" scoped>
// 序号小红点
.no-add {
    position: relative;
    &::after {
        content: '';
        display: block;
        position: absolute;
        right: -8px;
        top: -2px;
        width: 6px;
        height: 6px;
        border-radius: 100%;
        background: red;
    }
}
.scoped .bot-search-form-wrap {
    padding: 0;
    max-height: 300px;
    border: none;
    .bot-hight-search-wrap {
        padding: 0 16px 16px;
        .form li:last-child {
            width: auto;
            .ant-btn {
                margin-right: 24px;
            }
        }
    }
    .bot-product-item {
        padding: 10px;
        position: absolute;
        left: 575px;
        text-align: left;
    }
}
@w: 24px;
.order-modal-list {
    position: absolute;
    left: @w;
    right: @w;
    bottom: 57px;
    display: flex;
    flex-direction: column;
    li {
        display: flex;
        flex-direction: row;
        align-items: center;
        padding-top: 24px;
        .item-wrap {
            display: flex;
            align-items: center;
            padding-right: 50px;
            .item {
                padding-bottom: 0;
                padding-right: @w;
            }
        }
        &:last-child {
            padding-bottom: 24px;
        }
    }
}
.modal-form-input-scoped .modal-form-input .top-list .label {
    display: flex;
    width: auto;
    max-width: 94px;
}
// 顶部按钮多的样式重新调整左右间距
.scoped .btn-top-wrap .btn-list-wrap li,
.scoped .btn-top-wrap .btn-list-wrap li .ant-btn {
    margin-right: 10px;
}
.order-modal-list li {
    padding-top: 18px;
}

.modal-fullscreen {
    display: inline-block;
    position: absolute;
    right: 56px;
    top: 0px;
    width: 56px;
    font-size: 16px;
    font-style: normal;
    text-align: center;
    text-transform: none;
    text-rendering: auto;
    height: 48px;
    line-height: 48px;
    color: #fff;
}
</style>
