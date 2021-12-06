<template>
    <div class="scoped scoped-white">
        <div style="height: 21px">
            <a-breadcrumb style="float: left">
                <a-breadcrumb-item style="color: #1890ff">采购管理</a-breadcrumb-item>
                <a-breadcrumb-item>购前询价</a-breadcrumb-item>
            </a-breadcrumb>
        </div>
        <a-row class="shop-card enquiry-list-new">
            <a-row class="shop-card-pre-header">
                <h2 class="tit_box">询价单</h2>
            </a-row>
            <a-row class="shop-card-header">
                <div class="even-columns group">
                    <div class="col">
                        <div class="col-label"
                            ><label class="form-label"><em class="em red">*</em>&nbsp;单位名称:</label></div
                        >
                        <div class="col-input">
                            <a-select
                                mode="default"
                                show-search
                                style="width: 100%"
                                :value="serachOrgName"
                                :filter-option="false"
                                :show-arrow="false"
                                :not-found-content="null"
                                :default-active-first-option="false"
                                @search="searchOrgFn"
                                @change="changeOrgFn"
                            >
                                <a-spin v-if="fetchingOrg" slot="notFoundContent" size="small" />
                                <a-select-option v-for="d in tableData" :key="d.key" :value="d.key">
                                    {{ d.org_name }}
                                </a-select-option>
                                <a-select-option value="search-more">
                                    <b style="color: #1890ff">搜索更多...</b>
                                </a-select-option>
                                <a-select-option value="tips">
                                    <i>提示：输入文字搜索</i>
                                </a-select-option>
                            </a-select>
                        </div>
                    </div>
                    <div class="col">
                        <div class="col-label"><label class="form-label">联系人:</label></div>
                        <div class="col-input">
                            <a-input type="text" v-model="formData.contacts" placeholder="" />
                        </div>
                    </div>
                    <div class="col">
                        <div class="col-label"><label class="form-label">电话:</label></div>
                        <div class="col-input">
                            <a-input type="text" v-model="formData.phone" placeholder="" />
                        </div>
                    </div>
                </div>
            </a-row>
            <a-row class="shop-card-content">
                <a-row>
                    <hot-table ref="proTableComponent" :data="hotTableData" :settings="hotTableSettings">
                        <hot-column title="产品" data="ilps_sn"> </hot-column>
                        <hot-column title="规格型号" data="contacts"> </hot-column>
                        <hot-column title="厂商" data="phone"> </hot-column>
                        <hot-column title="数量" data="ask_time"> </hot-column>
                        <hot-column title="包装单位" data="status"> </hot-column>
                        <hot-column title="单价" data="status"> </hot-column>
                        <hot-column title="金额" data="status"> </hot-column>
                        <hot-column title="税率" data="status"> </hot-column>
                        <hot-column title="含税单价" data="status"> </hot-column>
                        <hot-column title="含税金额" data="status"> </hot-column>
                        <hot-column title="税额" data="status"> </hot-column>
                        <hot-column title="操作" data="status"> </hot-column>
                    </hot-table>
                </a-row>
                <!-- <vxe-grid
                    ref="xTable"
                    size="small"
                    :columns="columns"
                    min-height="100"
                    resizable
                    auto-resize
                    border
                    highlight-current-row
                    align="center"
                    :data="proTableData"
                >
                    <template v-slot:empty>
                        <div class="dblclick-wrap">
                            <span>信息栏</span>
                        </div>
                    </template>
                </vxe-grid> -->
                <!-- No need Add Product button <a-row class="card-add-row">
                    <a v-on:click="applyAddProduct">添加产品</a>
                </a-row> -->
                <a-row>
                    <a-input placeholder="摘要" class="input" />
                </a-row>
            </a-row>
            <a-row class="shop-card-footer">
                <ul class="footer-btn-list">
                    <li>
                        <a-button @click="printFn()" key="print"> 打印 </a-button>
                        <a-button @click="exportExcel()" key="exportExcel"> 导出Excel </a-button>
                    </li>
                    <li>
                        <a-button @click="cancelFn()" key="cancel">
                            {{ planModalTextComputed }}
                        </a-button>
                        <a-button @click="inquiryAddFn(1)" key="submit" type="primary"> 询价 </a-button>
                    </li>
                </ul>
            </a-row>
        </a-row>
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <!-- No need to use this button: <a-button type="primary" size="small" @click="openCreate" :disabled="isReply">
                                新增
                            </a-button> -->
                            <a-button v-if="isReply" type="primary" size="small" @click="openReply"> 回复 </a-button>
                            <a-button v-if="!isReply" type="primary" size="small" @click="openEdit"> 修改 </a-button>
                            <a-button type="danger" size="small" :disabled="isReply" @click="openDel"> 删除 </a-button>

                            <!-- 医院和经销商 -->
                            <a-button v-if="nowOrgType !== 3" type="primary" size="small" @click="handleOpenPriceModal">
                                价格查询
                            </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            placeholder="询价机构/单据编号"
                            size="small"
                            enterButton
                            class="search"
                            v-model="searchKey"
                            @search="handleSearch"
                        />
                    </div>
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
            <a-tabs @change="changeTabsFn" :activeKey="tabsActiveKey" type="card">
                <a-tab-pane tab="询价回复" v-if="nowOrgType != 1" style="padding: 8px" :key="systemTypes[0]">
                    <vxe-grid
                        ref="hosTable"
                        size="small"
                        :columns="columns_hos"
                        :height="scrollYNumber + 43"
                        resizable
                        auto-resize
                        border
                        highlight-current-row
                        @cell-click="cellClickFn"
                        @cell-dblclick="cellDblclickFn"
                        align="center"
                        :data="hospitalDatas"
                        :loading="hospitalPagination.loading"
                        :start-index="(hospitalPagination.current - 1) * hospitalPagination.pageSize"
                    >
                    </vxe-grid>
                    <vxe-pager
                        :loading="hospitalPagination.loading"
                        :current-page="hospitalPagination.current"
                        :page-size="hospitalPagination.pageSize"
                        :page-sizes="hospitalPagination.pageSizes"
                        :total="hospitalPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changePaginationFn"
                    >
                    </vxe-pager>
                    <div class="show_inquiry">
                        <a-checkbox @change="changeInquiry">显示全部</a-checkbox>
                    </div>
                </a-tab-pane>
                <a-tab-pane tab="经销商" style="padding: 8px" :key="systemTypes[1]" v-if="nowOrgType != 3">
                    <vxe-grid
                        ref="deaTable"
                        size="small"
                        :columns="columns_dea"
                        :height="scrollYNumber + 43"
                        resizable
                        auto-resize
                        border
                        highlight-current-row
                        @cell-click="cellClickFn"
                        @cell-dblclick="cellDblclickFn"
                        align="center"
                        :data="dealersDatas"
                        :loading="dealersPagination.loading"
                        :start-index="(dealersPagination.current - 1) * dealersPagination.pageSize"
                    >
                    </vxe-grid>
                    <vxe-pager
                        :loading="dealersPagination.loading"
                        :current-page="dealersPagination.current"
                        :page-size="dealersPagination.pageSize"
                        :page-sizes="dealersPagination.pageSizes"
                        :total="dealersPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changePaginationFn"
                    >
                    </vxe-pager>
                </a-tab-pane>
                <a-tab-pane tab="厂商" style="padding: 8px" v-if="nowOrgType != 3" :key="systemTypes[2]">
                    <vxe-grid
                        ref="venTable"
                        size="small"
                        :columns="columns_ven"
                        :height="scrollYNumber + 43"
                        resizable
                        auto-resize
                        border
                        highlight-current-row
                        @cell-click="cellClickFn"
                        @cell-dblclick="cellDblclickFn"
                        align="center"
                        :data="vendorDatas"
                        :loading="vendorPagination.loading"
                        :start-index="(vendorPagination.current - 1) * vendorPagination.pageSize"
                    >
                    </vxe-grid>
                    <vxe-pager
                        :loading="vendorPagination.loading"
                        :current-page="vendorPagination.current"
                        :page-size="vendorPagination.pageSize"
                        :page-sizes="vendorPagination.pageSizes"
                        :total="vendorPagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changePaginationFn"
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
                        @click="RefreshTable"
                    />
                    <!--刷新按钮区域-->
                </a-tooltip>
            </a-tabs>
            <!--表格区域-->
        </div>
        <!-- tab切换区域结束 -->

        <a-modal
            :title="addProductMd.title"
            :visible="addProductMd.alert"
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
            <add-product
                ref="chooseProMd"
                @returnData="getProReturn"
                :partB="part_b_obj"
                :metaTitle="addProductMd.title"
            ></add-product>
        </a-modal>

        <a-modal
            title="修改"
            :visible="editMd.alert"
            :width="1140"
            okText="保存"
            :maskClosable="false"
            :mask="!priceMd.alert"
            :centered="true"
            @cancel="editMd.alert = false"
            class="modal-form-input-scoped global-drag edit-inquiry-md"
            :wrapClassName="priceMd.alert ? 'inquiry-edit-wrap' : ''"
        >
            <div v-globalDrag="{ el: 'edit-inquiry-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ editMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <div style="height: 32px"></div>
            </template>
            <edit-inquiry
                ref="enquiryListEditMd"
                @done="editDone"
                @cancel="editMd.alert = false"
                :editObj="editObj"
            ></edit-inquiry>
        </a-modal>

        <a-modal
            title="回复"
            :visible="replyMd.alert"
            :width="1140"
            okText="保存"
            :maskClosable="false"
            :mask="!priceMd.alert"
            :centered="true"
            @cancel="replyMd.alert = false"
            class="modal-form-input-scoped global-drag reply-inquiry-md"
            :wrapClassName="priceMd.alert ? 'inquiry-reply-wrap' : ''"
        >
            <div v-globalDrag="{ el: 'reply-inquiry-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ replyMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <div style="height: 32px"></div>
            </template>
            <reply-inquiry
                ref="replyListEditMd"
                @done="replyDone"
                @cancel="replyMd.alert = false"
                :editObj="editObj"
            ></reply-inquiry>
        </a-modal>

        <!-- 价格查询modal -->
        <a-modal
            title="价格"
            :visible="priceMd.alert"
            :width="1140"
            okText="保存"
            :centered="true"
            :maskClosable="false"
            @cancel="priceMd.alert = false"
            class="modal-form-input-scoped global-drag inquiry-price-md"
            wrapClassName="inquiry-price-wrap"
        >
            <div v-globalDrag="{ el: 'inquiry-price-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ priceMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <div style="height: 32px"></div>
            </template>
            <price :key="priceMdKey" @cancel="priceMd.alert = false"></price>
        </a-modal>
        <!-- 价格查询modal end -->
        <a-modal
            :title="chooseOrgMd.title"
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            @cancel="chooseOrgMd.alert = false"
            class="scoped modal-form-input-scoped global-drag choose_org_md"
        >
            <div v-globalDrag="{ el: 'chooseOrgMd' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <div style="margin-top: 56px">
                <choose-org-tab
                    ref="chooseOrgMd"
                    @chooseOrg="chooseOrgFn"
                    :pageTypeName="6"
                    :partType="1"
                    :isCertifie="true"
                ></choose-org-tab>
            </div>
        </a-modal>
    </div>
</template>

<script>
import { HotTable, HotColumn } from '@handsontable/vue';

import {
    inquiryDealerList,
    inquiryVendorList,
    inquiryUnreplyList,
    inquiryDelAPI,
    getInquiryOrgListAPI,
    getVendorProductListAPI,
    platformUnitTree,
    inquiryAddAPI
} from '@/service/pageAjax';

import chooseOrgTab from '@/components/chooseOrgTab';
import addProduct from '@/components/addProduct';

import { BillsType } from '@/config/constants';

import editInquiry from './module/editInquiry';
import replyInquiry from './module/replyInquiry';
import price from './module/priceInquiry';

export default {
    name: 'enquiryList',
    components: {
        editInquiry,
        replyInquiry,
        price,
        chooseOrgTab,
        addProduct,
        HotTable,
        HotColumn
    },
    data() {
        return {
            hotTableSettings: {
                licenseKey: 'non-commercial-and-evaluation',
                stretchH: 'all',
                height: 'auto',
                className: 'htCenter htMiddle',
                rowHeaders: true
            },
            priceMdKey: 0,
            searchKey: '', // 搜索关键字 询价机构/单据编号
            isReply: false,
            isDraft: false,
            tabsActiveKey: 1,
            showInquiry: 0,
            currentTrObj: {},
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            searchTables: {
                registrant: '',
                social_credit_code: '',
                name: ''
            },
            systemTypes: [1, 2, 3], // 待回复 、 2 经销商、 3 厂商
            columns: [
                {
                    title: 'NO.',
                    dataIndex: 'idx',
                    minWidth: '40',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    type: 'index',
                    align: 'center',
                    fixed: 'left',
                    slots: {
                        default: ({ seq }) => {
                            let html = [
                                <div>
                                    <i class="red-dot-add"></i>
                                    <span>{seq}</span>
                                </div>
                            ];
                            return html;
                        }
                    }
                },
                {
                    title: '产品',
                    dataIndex: 'name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '200',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [
                                <a-select
                                    mode="default"
                                    show-search
                                    value={row.search_product_name}
                                    filter-option={false}
                                    data_row={row}
                                    show-arrow={false}
                                    not-found-content={null}
                                    default-active-first-option={false}
                                    onSearch={this.loadAllPro}
                                    onChange={this.changeVen}
                                >
                                    {this.venTableData.length &&
                                        this.venTableData.map((d) => {
                                            return (
                                                <a-select-option key={d.key} value={d.key} data_row={row.key}>
                                                    {d.product_name}
                                                </a-select-option>
                                            );
                                        })}
                                    <a-select-option key="search" value="search-more" data_row={row.key}>
                                        <b style="color: #1890ff">搜索更多...</b>
                                    </a-select-option>
                                    <a-select-option key="tips" value="tips">
                                        <i>提示：输入文字搜索</i>
                                    </a-select-option>
                                </a-select>
                            ];
                        }
                    }
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '80',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification];
                        }
                    }
                },
                {
                    title: '厂商',
                    dataIndex: 'vendor_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '180',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.vendor_name];
                        }
                    }
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '60',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            let change = (val) => {
                                if (!val) {
                                    record.quantity = 0;
                                }
                                this.countFn();
                            };
                            let dom;
                            dom = [
                                <a-input-number
                                    min={0}
                                    v-model={record.quantity}
                                    class="input"
                                    onChange={change}
                                    width="30px"
                                />
                            ];
                            return dom;
                        }
                    }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            if (record.unit_data) {
                                let val = record.unit_data.key ? record.unit_data.key : [];
                                let tree = record.unit_data.data ? record.unit_data.data : [];
                                let displayRender = (rd) => {
                                    let len = rd.labels.length;
                                    const label = rd.labels[len - 1];
                                    return label;
                                };
                                let change = (value, arr) => {
                                    record.unit_data.key = value;
                                    record.unit_inner_sn = value[value.length - 1];
                                    record.unit_name = arr[arr.length - 1].name;
                                    record.isNew = false;
                                    let newData = this.proTableData;
                                    this.proTableData = [];
                                    this.proTableData = newData;
                                };
                                return [
                                    <a-cascader
                                        allowClear={false}
                                        options={tree.length > 0 ? tree : []}
                                        value={val}
                                        displayRender={displayRender}
                                        expandTrigger="hover"
                                        placeholder=""
                                        onChange={change}
                                        changeOnSelect
                                    />
                                ];
                            } else {
                                return [];
                            }
                        }
                    }
                },
                {
                    title: '单价',
                    dataIndex: 'unit_price',
                    showOverflow: false,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.unit_price.toFixed(4)];
                        }
                    }
                },
                {
                    title: '金额',
                    dataIndex: 'sum',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            const value = !isNaN(row.sum) ? row.sum.toFixed(2) : '';
                            return [value];
                        }
                    }
                },
                {
                    title: '税率',
                    dataIndex: 'rate',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            let change = (val) => {
                                if (!val) {
                                    record.rate = 0;
                                }
                                this.countFn();
                            };
                            let dom = [
                                <a-select v-model={record.rate} style="width:68px; margin-right: 4px" onChange={change}>
                                    <a-select-option value={0}> 0% </a-select-option>
                                    <a-select-option value={1}> 1% </a-select-option>
                                    <a-select-option value={3}> 3% </a-select-option>
                                    <a-select-option value={6}> 6% </a-select-option>
                                    <a-select-option value={9}> 9% </a-select-option>
                                    <a-select-option value={13}> 13% </a-select-option>
                                </a-select>
                            ];
                            return dom;
                        }
                    }
                },
                {
                    title: '含税单价',
                    dataIndex: 'rate_unit',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            let change = (val) => {
                                if (!val) {
                                    record.rate_unit = 0;
                                }
                                this.countFn();
                            };
                            let dom = [
                                <a-input-number
                                    v-model={record.rate_unit}
                                    disabled={this.recordDisabled}
                                    class="input"
                                    style="width:100%"
                                    min={0}
                                    formatter={(value) => `${value}`.replace(/^(\\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')}
                                    parser={(value) => value.replace(/^(\\-)*(\d+)\.(\d\d\d\d).*$/, '$1$2.$3')}
                                    onChange={change}
                                />
                            ];
                            return dom;
                        }
                    }
                },
                {
                    title: '含税金额',
                    dataIndex: 'rate_price',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            const value = !isNaN(row.rate_price) ? row.rate_price.toFixed(2) : '';
                            return [value];
                        }
                    }
                },
                {
                    title: '税额',
                    dataIndex: 'rate_num',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 100,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            const value = !isNaN(row.rate_num) ? row.rate_num.toFixed(2) : '';
                            return [value];
                        }
                    }
                },
                {
                    title: '操作',
                    dataIndex: 'inquirer_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: 60,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            let delFn = () => {
                                this.proTableData.forEach((item, index) => {
                                    if (item.key === record.key) {
                                        this.proTableData.splice(index, 1);
                                    }
                                });
                            };
                            return [
                                <a-tooltip placement="top" title="删除">
                                    <i onClick={delFn} class="yrt yrt-shanchu-copy cancel del_ico red"></i>
                                </a-tooltip>
                            ];
                        }
                    }
                }
            ],
            columns_hos: [
                {
                    title: '询价机构',
                    dataIndex: 'partA_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.partA_name];
                        }
                    }
                },
                {
                    title: '单据编号',
                    dataIndex: 'ilps_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ilps_sn];
                        }
                    }
                },
                {
                    title: '联系人',
                    dataIndex: 'contacts',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.contacts];
                        }
                    }
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.phone];
                        }
                    }
                },
                {
                    title: '询价时间',
                    dataIndex: 'ask_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ask_time];
                        }
                    }
                },
                {
                    title: '回复时间',
                    dataIndex: 'answer_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.answer_time];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '10%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [this.contractStatus[row.status]];
                        }
                    }
                }
            ],
            columns_dea: [
                {
                    title: '经销商',
                    dataIndex: 'partB_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.partB_name];
                        }
                    }
                },
                {
                    title: '单据编号',
                    dataIndex: 'ilps_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ilps_sn];
                        }
                    }
                },
                {
                    title: '联系人',
                    dataIndex: 'contacts',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.contacts];
                        }
                    }
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.phone];
                        }
                    }
                },
                {
                    title: '询价时间',
                    dataIndex: 'ask_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ask_time];
                        }
                    }
                },
                {
                    title: '回复时间',
                    dataIndex: 'answer_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.answer_time];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    minWidth: '10%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let text = row.status;
                            if (parseInt(text) === 2) {
                                return [this.contractStatus[parseInt(text) - 1]];
                            } else {
                                return [this.contractStatus[text]];
                            }
                        }
                    }
                }
            ],
            columns_ven: [
                {
                    title: '厂商',
                    dataIndex: 'partB_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.partB_name];
                        }
                    }
                },
                {
                    title: '单据编号',
                    dataIndex: 'ilps_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ilps_sn];
                        }
                    }
                },
                {
                    title: '联系人',
                    dataIndex: 'contacts',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.contacts];
                        }
                    }
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.phone];
                        }
                    }
                },
                {
                    title: '询价时间',
                    dataIndex: 'ask_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ask_time];
                        }
                    }
                },
                {
                    title: '回复时间',
                    dataIndex: 'answer_time',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.answer_time];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '10%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let text = row.status;
                            if (parseInt(text) === 2) {
                                return [this.contractStatus[parseInt(text) - 1]];
                            } else {
                                return [this.contractStatus[text]];
                            }
                        }
                    }
                }
            ],
            dealersDatas: [], // 经销商主体数据
            hospitalDatas: [], // 医院主体数据
            vendorDatas: [], // 厂商主体数据
            // 医院 - 表格分页
            hospitalPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            dealersPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            // 厂商 - 表格分页
            vendorPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            contractStatus: ['询价草稿', '询价', '回复草稿', '已回复'],
            editObj: {},
            approveMd: {
                title: `${this.$route.meta.title} - 详情`,
                alert: false,
                loading: false
            },
            nowOrgType: 2, // 权限 1：医院 2：经销商 3：产商
            createTipRadio: 1,
            createTip: false,
            createMd: {
                title: `询价 - 新增`,
                alert: false,
                loading: false
            },
            editMd: {
                title: `询价 - 修改`,
                alert: false,
                loading: false
            },
            replyMd: {
                title: `询价 - 回复`,
                alert: false,
                loading: false
            },
            priceMd: {
                title: `询价 - 价格`,
                alert: false,
                loading: false
            },
            tableData: [],
            tableDataTemp: [],
            serachOrgName: '',
            selectedOrg: {},
            fetchingOrg: false,
            proTableData: [],
            planModalForm: {
                audit_time: '',
                auditor_inner_sn: '',
                auditor_name: '',
                create_time: '',
                creator_inner_sn: '',
                creator_name: '',
                department_inner_sn: '',
                department_inner_sn_old: '',
                department_name: '',
                department_name_old: '',
                description: '',
                description_old: '',
                detailList: [],
                ppl_inner_sn: '',
                ppl_sn: '',
                ppl_sn_old: '',
                status: 0
            },
            btnName: 'add',
            chooseOrgMd: {
                title: `询价 - 新增 - 选择机构`,
                alert: false,
                loading: false
            },
            orgName: '',
            cookieInfo: this.$cookie,
            addProductMd: {
                title: `询价 - 新增 - 添加产品`,
                alert: false,
                loading: false
            },
            searchVenName: '',
            selectedVen: {
                key: ''
            },
            part_b_obj: {
                systemType: '',
                modalType: '',
                id: ''
            },
            curVenKey: '',
            formData: {
                partA: parseInt(this.$cookie.get('userbelong')), // 询价机构ID
                partB: '', // 被询价机构ID
                ilps_sn: '', // 询价单编号
                contacts: '', // 联系人
                phone: '', // 电话
                detailList: '',
                description: '' // 摘要
            },
            orgSystemType: ''
        };
    },
    methods: {
        // 打开价格查询modal
        handleOpenPriceModal() {
            this.priceMdKey = new Date().getTime(); // 刷新组件状态
            this.$Utils.globalDragCenterFn('inquiry-price-md');
            this.priceMd.alert = true;
        },
        // 点击搜索
        handleSearch() {
            this.hospitalPagination = {
                ...this.hospitalPagination,
                pageNum: 1,
                current: 1
            };
            this.dealersPagination = {
                ...this.dealersPagination,
                pageNum: 1,
                current: 1
            };
            this.vendorPagination = {
                ...this.vendorPagination,
                pageNum: 1,
                current: 1
            };
            this.loadTable();
        },
        RefreshTable() {
            this.hospitalPagination.current = 1;
            this.dealersPagination.current = 1;
            this.vendorPagination.current = 1;
            this.loadTable();
        },
        loadTable(data) {
            this.loadTableHos();
            this.loadTableDel(data);
            this.loadTableVen(data);
        },
        async loadTableHos() {
            // 加载回复列表
            this.hospitalPagination.loading = true;
            let params = {};
            let all = this.showInquiry;
            params.key = this.searchKey;
            params.pageSize = this.hospitalPagination.pageSize;
            params.pageNum = this.hospitalPagination.current;
            await inquiryUnreplyList(all, { params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let data = res.rows;
                    this.hospitalPagination.loading = false;
                    let total = res.total;
                    this.hospitalPagination = {
                        ...this.hospitalPagination,
                        total
                    };
                    data.forEach((item) => {
                        item.isActive = '';
                        item.key = '1' + item.il_type + '' + item.ilps_inner_sn + '' + item.partA + '' + item.partB;
                    });
                    this.hospitalDatas = data;
                }
            });
        },
        async loadTableDel(chooseObj) {
            console.log(chooseObj);
            // 加载经销商列表
            this.dealersPagination.loading = true;
            let params = {};
            params.key = this.searchKey;
            params.pageSize = this.dealersPagination.pageSize;
            params.pageNum = this.dealersPagination.current;
            if (chooseObj) {
                let pn = parseInt(this.dealersPagination.total / params.pageSize);
                params.pageNum = pn + 1;
                this.dealersPagination.pageNum = pn + 1;
                this.dealersPagination.current = pn + 1;
            }
            await inquiryDealerList({ params }).then((res) => {
                console.log(params);
                console.log(res);
                if (parseFloat(res.code) === 0) {
                    let data = res.rows;
                    this.dealersPagination.loading = false;
                    let total = res.total;
                    this.dealersPagination = {
                        ...this.dealersPagination,
                        total
                    };
                    data.forEach((item) => {
                        item.isActive = '';
                        item.key = item.il_type + '' + item.ilps_inner_sn + '' + item.partA + '' + item.partB;
                    });
                    this.dealersDatas = data;
                    console.log(this.dealersDatas);
                    if (this.dealersDatas.length > 0) {
                        if (chooseObj) {
                            // 选中新增/编辑的项
                            this.currentTrObj = this.dealersDatas[this.dealersDatas.length - 1];
                        } else {
                            this.currentTrObj = this.dealersDatas[0]; // 默认选中第一
                        }
                        this.editObj = this.currentTrObj;
                        console.log(this.editObj);
                    }
                }
            });
        },
        async loadTableVen(chooseObj) {
            // 加载厂商列表
            this.vendorPagination.loading = true;
            let params = {};
            params.key = this.searchKey;
            params.pageSize = this.vendorPagination.pageSize;
            params.pageNum = this.vendorPagination.current;

            // 排序
            params.orderByColumn = 'ilps_inner_sn';
            params.isAsc = 'desc';

            if (chooseObj) {
                let pn = parseInt(this.dealersPagination.total / params.pageSize);
                params.pageNum = pn + 1;
                this.vendorPagination.pageNum = pn + 1;
                this.vendorPagination.current = pn + 1;
            }
            await inquiryVendorList({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let data = res.rows;
                    this.vendorPagination.loading = false;
                    let total = res.total;
                    this.vendorPagination = {
                        ...this.vendorPagination,
                        total
                    };
                    data.forEach((item) => {
                        item.isActive = '';
                        item.key = item.il_type + '' + item.ilps_inner_sn + '' + item.partA + '' + item.partB;
                    });
                    this.vendorDatas = data;
                    if (this.vendorDatas.length > 0) {
                        if (chooseObj) {
                            // 选中新增/编辑的项
                            this.currentTrObj = this.vendorDatas[this.vendorDatas.length - 1];
                        } else {
                            this.currentTrObj = this.vendorDatas[0]; // 默认选中第一
                        }
                        this.editObj = this.currentTrObj;
                    }
                }
            });
        },
        // 设置行属性 —— 点击每一行的事件
        cellClickFn(row) {
            // 单击行
            let obj = row.row || row;
            this.currentTrObj = obj;
            this.editObj = this.currentTrObj;
            this.editObj.systemType = this.tabsActiveKey;
            if (parseInt(obj.status) === 0) {
                this.isDraft = true;
            } else {
                this.isDraft = false;
            }
            this.clearSelect();
        },
        cellDblclickFn({ row }) {
            let obj = row.row || row;
            console.log(obj);
            this.currentTrObj = obj;
            this.editObj = this.currentTrObj;
            this.editObj.systemType = this.tabsActiveKey;
            let isEdit = true;
            if (parseFloat(obj.status) === 3) {
                isEdit = false;
            }
            if (this.tabsActiveKey === 1) {
                this.openReply();
            } else {
                this.openEdit(isEdit);
            }
            this.clearSelect();
        },
        clearSelect() {
            const type = this.tabsActiveKey;
            if (type === 1) {
                if (this.$refs.deaTable) {
                    this.$refs.deaTable.clearCurrentRow();
                }
                if (this.$refs.venTable) {
                    this.$refs.venTable.clearCurrentRow();
                }
            }
            if (type === 2) {
                if (this.$refs.hosTable) {
                    this.$refs.hosTable.clearCurrentRow();
                }
                if (this.$refs.venTable) {
                    this.$refs.venTable.clearCurrentRow();
                }
            }
            if (type === 3) {
                if (this.$refs.hosTable) {
                    this.$refs.hosTable.clearCurrentRow();
                }
                if (this.$refs.deaTable) {
                    this.$refs.deaTable.clearCurrentRow();
                }
            }
        },
        changeTabsFn(activeKey) {
            this.tabsActiveKey = parseFloat(activeKey);
            if (this.tabsActiveKey === 1) {
                this.isReply = true;
            } else {
                this.isReply = false;
            }
            this.currentTrObj = {};
        },
        // 页码改变的回调函数
        changePaginationFn({ currentPage, pageSize }) {
            switch (parseInt(this.tabsActiveKey)) {
                case 0:
                    break;
                case 1:
                    this.hospitalPagination.current = currentPage;
                    this.hospitalPagination.pageSize = pageSize;
                    this.hospitalPagination.loading = true;
                    this.loadTableHos();
                    break;
                case 2:
                    this.dealersPagination.current = currentPage;
                    this.dealersPagination.pageSize = pageSize;
                    this.dealersPagination.loading = true;
                    this.loadTableDel();
                    break;
                case 3:
                    this.vendorPagination.current = currentPage;
                    this.vendorPagination.pageSize = pageSize;
                    this.vendorPagination.loading = true;
                    this.loadTableVen();
                    break;
            }
        },
        changeInquiry(e) {
            if (e.target.checked) {
                this.showInquiry = 1;
            } else {
                this.showInquiry = 0;
            }
            this.loadTableHos();
        },
        cancelCt() {
            this.createMd.alert = false;
            // this.$refs.enquiryListMd.refreshPage()
        },
        /** 头部按钮 **/
        openCreate() {
            // 打开新增
            this.$Utils.globalDragCenterFn('create-inquiry-md');
            this.createMd.alert = true;
            console.log(this.$refs);
            if (this.$refs.enquiryListMd) {
                this.$refs.enquiryListMd.refreshPage();
            }
        },
        createDone(dataObj) {
            this.createMd.alert = false;
            this.tabsActiveKey = dataObj.sys_type; // 指定tab
            // 添加行
            // let table = {}
            // if (dataObj.sys_type === 2) {
            //     table = this.$refs.deaTable // 经销商table
            // } else if (dataObj.sys_type === 3) {
            //     table = this.$refs.venTable // 厂商table
            // }
            // table && table.insert(dataObj).then(({row}) => {
            //     table.setCurrentRow(row)
            // })
            this.loadTable(dataObj);
        },
        openEdit(st) {
            // 打开修改
            if (!this.currentTrObj.key) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            let type = parseFloat(this.currentTrObj.status);
            this.editObj.systemType = this.tabsActiveKey;
            if (type === 3 && !st) {
                this.$Utils.globalDragCenterFn('reply-inquiry-md');
                this.replyMd.alert = true;
                this.$nextTick(() => {
                    this.$refs.replyListEditMd && this.$refs.replyListEditMd.refreshReplyPage(true);
                });
            } else {
                this.$Utils.globalDragCenterFn('edit-inquiry-md');
                this.editMd.alert = true;
                if (this.$refs.enquiryListEditMd) {
                    this.$refs.enquiryListEditMd.refreshPage();
                }
            }
        },
        editDone(data) {
            this.editMd.alert = false;
            // 更新当前编辑的行数据
            const editObjKeys = Object.keys(this.currentTrObj);
            Object.assign(this.currentTrObj, {
                ...this.$XEUtils.pick(data, editObjKeys)
            });
            // this.loadTable(this.currentTrObj)
        },
        replyDone() {
            this.replyMd.alert = false;
            // 暂时注释这个判断逻辑，改为有没有修改操作都发送请求
            // if (!changed) return // 没有修改操作 不发送请求
            this.loadTable(this.currentTrObj);
        },
        openReply() {
            // 打开回复
            if (!this.currentTrObj.key) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            this.editObj.systemType = this.tabsActiveKey;
            this.$Utils.globalDragCenterFn('reply-inquiry-md');
            this.replyMd.alert = true;
            this.$nextTick(() => {
                this.$refs.replyListEditMd && this.$refs.replyListEditMd.refreshReplyPage(false);
            });
        },
        openDel() {
            // 打开删除
            if (!this.currentTrObj.key) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            let key = parseFloat(this.tabsActiveKey);
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.delFn(key);
                }
            });
        },
        async delFn(key) {
            // 删除
            let params = {
                partA: this.currentTrObj.partA,
                ilps_inner_sn: this.currentTrObj.ilps_inner_sn,
                billsType: this.currentTrObj.billsType
            };
            await inquiryDelAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (key === 1) {
                        this.loadTableHos();
                    }
                    if (key === 2) {
                        this.loadTableDel();
                    }
                    if (key === 3) {
                        this.loadTableVen();
                    }
                }
            });
        },
        // 【采购管理-询价】
        async getInquiryOrgListFn(type, value = '') {
            this.searchOrgName = value;
            let params = {
                name: value,
                pageSize: 5,
                pageNum: 1,
                is_certifie: true
            };
            await getInquiryOrgListAPI(type, { params: params })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let TempData = res.rows.map((item) => {
                            item.key = (Math.random() + 1).toString(36).substring(7);
                            item.systemType = parseInt(type);
                            item.org_type_name = this.parseOrgNameFromBillsType(item.billsType);
                            item.serachOrgName = item.org_name;
                            return item;
                        });
                        this.tableDataTemp = this.tableDataTemp.concat(TempData);
                    }
                })
                .finally(() => {
                    this.tableData = this.tableData.concat(this.tableDataTemp);
                    this.tableDataTemp = [];
                    this.fetchingOrg = true;
                });
        },
        changeOrgFn(value) {
            if (value === 'search-more') {
                this.openChooseTb();
                return;
            } else if (value === 'tips') {
                return;
            } else {
                this.selectedOrg = Object.assign(
                    {},
                    this.tableData.find((v) => v.key === value)
                );
                this.serachOrgName = this.selectedOrg.org_name;
                this.formData.partB = this.selectedOrg.org_id;
                this.orgSystemType = this.selectedOrg.systemType;
                this.part_b_obj.systemType = this.selectedOrg.systemType;
                this.part_b_obj.modalType = 6;
                this.part_b_obj.id = this.selectedOrg.org_id;
                this.part_b_obj.name = this.selectedOrg.org_name;
            }
        },
        searchOrgFn(value = '') {
            this.tableData = [];
            this.tableDataTemp = [];
            this.getInquiryOrgListFn(2, value);
            this.getInquiryOrgListFn(3, value);
        },
        // ****************************************************************
        // 接口区域结束
        // ****************************************************************
        /**
         * 从billsType中获取相关机构类型
         * @param billsType
         */
        parseOrgNameFromBillsType(billsType) {
            const orgTypeStr = billsType.substr(billsType.length - 1, 1);
            let orgTypeName = '';
            switch (orgTypeStr) {
                case 'H':
                    orgTypeName = '医院';
                    break;
                case 'V':
                    orgTypeName = '厂商';
                    break;
                case 'D':
                    orgTypeName = '经销商';
                    break;
            }
            return orgTypeName;
        },
        openChooseTb() {
            // 打开选择甲乙方
            this.chooseOrgMd.alert = true;
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
        },
        chooseOrgTips(org) {
            // const _this = this;
            // if (this.formData.partB && org.org_id !== this.formData.partB) {
            //     _this.$confirm({
            //         title: '操作提示',
            //         content:
            //             '当前选择和已选择相关机构不一致，如确认要改变已选择相关机构，已选择相关机构的所有信息会被清空。',
            //         okText: '确认',
            //         cancelText: '取消',
            //         centered: true,
            //         maskClosable: false,
            //         mask: false,
            //         onOk: () => {
            //             _this.chooseOrgFn(org);
            //             this.proTableData = [];
            //         }
            //     });
            // } else {
            // _this.chooseOrgFn(org);
            // }
            this.chooseOrgFn(org);
        },
        chooseOrgFn(org) {
            // 获取选中机构信息
            let systemType = org.systemType;
            this.part_b_obj.systemType = systemType;
            let id = org.org_id;
            this.selectedOrg = org;
            this.serachOrgName = org.org_name;
            let nowId = parseInt(this.cookieInfo.get('userbelong'));
            let nowType = parseInt(this.cookieInfo.get('userSystemType'));
            if (nowId === id && systemType === nowType) {
                this.$warning({
                    title: '操作提示',
                    content: '自己不能和自己询价',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            this.chooseOrgMd.alert = false;
            this.orgName = org.org_name;
            this.orgSystemType = org.systemType;
            this.formData.partB = org.org_id;
            this.part_b_obj.id = org.org_id;
            this.part_b_obj.name = org.org_name;
        },
        addProductFn() {
            // 添加商品
            this.$refs.chooseProMd.returnProList();
        },
        checkAddProduct() {
            if (
                this.selectedOrg &&
                !(this.selectedOrg.hasOwnProperty('org_name') && this.selectedOrg.org_name !== '')
            ) {
                this.$warning({
                    title: '操作提示',
                    content: '请先选择单位名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return true;
            }
            return false;
        },
        openAddProduct() {
            if (this.checkAddProduct()) return;
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn('add_product');
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        applyAddProduct() {
            if (this.checkAddProduct()) return;
            this.proTableData.push(
                Object.assign(
                    {},
                    {
                        common_use_unit: '',
                        common_use_unit_inner_sn: undefined,
                        id: null,
                        mdrf_sn: '',
                        min_unit: undefined,
                        min_unit_inner_sn: undefined,
                        product_inner_sn: undefined,
                        product_name: '',
                        product_type: undefined,
                        product_vendor_inner_sn: undefined,
                        product_vendor_name: undefined,
                        quantity: 1,
                        specification: '',
                        specification_inner_sn: undefined,
                        specification_vendor_sn: undefined,
                        vendor_inner_sn: undefined,
                        vendor_name: '',
                        warehouse_inner_sn: undefined,
                        warehouse_name: undefined,
                        children: [],
                        is_common_use: undefined,
                        is_original_package: undefined,
                        measure: undefined,
                        name: '',
                        parent: undefined,
                        parent_unit_inner_sn: undefined,
                        unit_inner_sn: undefined,
                        unit_data: [],
                        unit_key: [],
                        isActive: false,
                        discount: 1,
                        rate: 0,
                        rate_unit: 0,
                        unit_price: 0,
                        sum: 0,
                        rate_price: 0,
                        rate_num: 0,
                        idx: undefined,
                        isNew: true,
                        key: (Math.random() + 1).toString(36).substring(7)
                    }
                )
            );
            this.$refs.proTableComponent.hotInstance.updateSettings({
                data: this.proTableData
            });
        },
        getProReturn(dea, ven) {
            this.addProductMd.alert = false;
            let arr = ven.concat(dea);
            arr.forEach((item) => {
                if (item.product_type === 0) {
                    item.sys_type = item.dealer_name;
                } else if (item.product_type === 1) {
                    item.sys_type = item.vendor_name;
                }
                item.key = (Math.random() + 1).toString(36).substring(7);
                item.isActive = '';
                item.unit_inner_sn = '';
                item.quantity = 1;
                item.discount = 1;
                item.rate = 0;
                item.rate_unit = item.unit_price; // 含税单价 = 单价 【默认值】
                item.unit_price = parseFloat(item.rate_unit) / (1 + parseFloat(item.rate) / 100); // 单价 = 含税单价 / (1 + 税率 / 100)
                item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额 = 数量 * 单价
                item.rate_price = parseFloat(item.quantity) * parseFloat(item.rate_unit); // 含税金额 = 数量 * 含税单价
                item.rate_num = parseFloat(item.rate_price) - parseFloat(item.sum); // 税额 = 含税金额 - 金额
                item.isNew = true;
                item.unit_data = [];
                // this.venUnitTreeFn(item, true);
            });
        },
        async loadAllPro(value = '') {
            // 所有产品
            let params = {};
            params.org_id = parseInt(this.$cookie.get('userbelong'));
            params.name = value;
            params.pageSize = 10;
            params.pageNum = 1;
            this.searchVenName = value;
            await getVendorProductListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.venTableData = [];
                    this.venTableData = res.rows.map((item) => {
                        item.unit_name = item.min_unit;
                        item.unit_inner_sn = item.min_unit_inner_sn;
                        item.unitList =
                            item.unit_name && item.unit_inner_sn
                                ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                : [];
                        item.unit_inner_sn = [item.unit_inner_sn];

                        return {
                            ...item,
                            isActive: '',
                            activeClass: '',
                            key: Math.random(),
                            keyFormat: item.vendor_inner_sn + '' + item.product_inner_sn + item.specification_inner_sn,
                            // product_name: item.name || item.product_name,
                            name: item.product_name,
                            product_type: 1, // 0 - '经销商'  1 - '厂商'
                            sys_type: '厂商',
                            batch_number: '',
                            quantity: 1,
                            unit_price: 1,
                            money: 2,
                            produce_date: '',
                            overdue_date: '',
                            type: '0',
                            register_inner_sn: item.mdrf_inner_sn,
                            register_sn: item.mdrf_sn,
                            mdrf_inner_sn: item.mdrf_inner_sn,
                            mdrf_sn: item.mdrf_sn,
                            mdrfList: item.mdrf_sn
                                ? [{ value: item.mdrf_inner_sn, title: item.mdrf_sn, label: item.mdrf_sn }]
                                : [],
                            search_product_name: value,
                            status: 0
                        };
                    });
                    this.$refs['xTable'] && this.$refs['xTable'].refreshColumn();
                }
            });
        },
        changeVen(value, options) {
            if (value === 'search-more') {
                this.selectedVen.key = options.data.attrs['data_row'];
                this.openAddProduct();
            } else if (value === 'tips') {
                return;
            } else {
                this.selectedVen = Object.assign(
                    {},
                    this.venTableData.find((v) => v.key === value)
                );
                this.searchVenName = this.selectedVen.product_name;
                let pmKey = options.data.attrs['data_row'];
                let pmGroup = this.proTableData.map((v) => {
                    if (v.key === pmKey) {
                        this.selectedVen.search_product_name = this.selectedVen.product_name;
                        this.selectedVen.key = pmKey;
                        return this.selectedVen;
                    } else {
                        return v;
                    }
                });
                this.proTableData = pmGroup;
                this.$refs['xTable'] && this.$refs['xTable'].refreshColumn();
                this.venUnitTreeFn(this.selectedVen);
            }
        },
        async venUnitTreeFn(record) {
            // 厂商商产品单位
            let oid = record.vendor_inner_sn;
            let pid = record.product_inner_sn;
            let psid = record.specification_inner_sn;
            let comKey = record.key;

            await platformUnitTree(oid, pid, psid).then((res) => {
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
                    let unitArr;
                    unitArr = {
                        key: [res.tree[0].value],
                        data: res.tree
                    };
                    let pmGroup = this.proTableData.map((item) => {
                        if (item.key === comKey) {
                            if (item.product_type === 0) {
                                item.sys_type = item.dealer_name;
                            } else if (item.product_type === 1) {
                                item.sys_type = item.vendor_name;
                            }
                            item.isActive = '';
                            item.unit_inner_sn = '';
                            item.quantity = 1;
                            item.discount = 1;
                            item.rate = 0;
                            item.rate_unit = item.unit_price; // 含税单价 = 单价 【默认值】
                            item.unit_price = parseFloat(item.rate_unit) / (1 + parseFloat(item.rate) / 100); // 单价 = 含税单价 / (1 + 税率 / 100)
                            item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额 = 数量 * 单价
                            item.rate_price = parseFloat(item.quantity) * parseFloat(item.rate_unit); // 含税金额 = 数量 * 含税单价
                            item.rate_num = parseFloat(item.rate_price) - parseFloat(item.sum); // 税额 = 含税金额 - 金额
                            item.isNew = true;
                            item.unit_data = unitArr;
                            item.unit_inner_sn = unitArr.key[0];
                            item.unit_name = unitArr.data[0].name;
                        }
                        return item;
                    });
                    this.proTableData = pmGroup;
                    // this.$refs['xTable'] && this.$refs['xTable'].refreshColumn();
                    this.$refs.proTableComponent.hotInstance.updateSettings({
                        data: this.proTableData
                    });
                }
            });
        },
        countFn() {
            // 自动计算
            let data = JSON.parse(JSON.stringify(this.proTableData));
            data.forEach((item) => {
                item.unit_price = parseFloat(item.rate_unit) / (1 + parseFloat(item.rate) / 100); // 单价
                item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额
                item.rate_price = parseFloat(item.quantity) * parseFloat(item.rate_unit); // 含税金额
                item.rate_num = parseFloat(item.rate_price) - parseFloat(item.sum); // 税额
            });
            this.proTableData = data;
        },
        printFn() {
            let printContent = {
                printData: this.proTableData,
                printForm: this.formData,
                printTable: this.columns,
                printType: 4, // 1、图片；2、耗材申领；3、出入库单; 4、询价单
                pageType: 0, // 0 询价 1回复
                orgName: this.orgName,
                printTitle: '询价单',
                unitList: this.unitList
            };
            console.log(printContent);
            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        // 导出excel
        exportExcel() {
            const form = this.formData;
            const formData = {
                title: '询价单',
                desc: {
                    label: '摘要',
                    value: form.description
                },
                groups: [
                    [
                        {
                            label: '单位名称',
                            value: this.orgName
                        },
                        {
                            label: '联系人',
                            value: form.contacts
                        },
                        {
                            label: '电话',
                            value: form.phone
                        }
                    ]
                ]
            };
            console.log(this.columns);
            this.$Utils.exportExcel(
                this.columns,
                this.proTableData.map((item) => ({
                    ...item,
                    rate: `${item.rate}%`,
                    discount_price: item.discount_price ? item.discount_price.toFixed(2) : '0',
                    discount_unit: item.discount_unit ? item.discount_unit.toFixed(2) : '0',
                    unit_price: item.unit_price ? item.unit_price.toFixed(2) : '0',
                    rate_price: item.rate_price ? item.rate_price.toFixed(2) : '0',
                    rate_unit: item.rate_unit ? item.rate_unit.toFixed(2) : '0',
                    rate_num: item.rate_num ? item.rate_num.toFixed(2) : '0'
                })),
                [],
                formData
            );
        },
        cancelFn() {
            this.$emit('cancel');
        },
        async inquiryAddFn(type) {
            // 新增询价单据 type 0:草稿 1:询价
            console.log('FORMDATA', this.formData);
            console.log('PROTABLEDATA', this.proTableData);
            let dataObj = this.formData;
            // dataObj.ilps_sn = this.$Utils.trim(dataObj.ilps_sn)
            // if (dataObj.ilps_sn === '' || !dataObj.ilps_sn) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '请输入单据编号',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: true
            //     })
            //     return false
            // }
            // if (dataObj.partB === '' || !dataObj.partB) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '请选择相关机构',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: true
            //     });
            //     return false;
            // }
            // 添加合同产品验证
            dataObj.detailList = JSON.parse(JSON.stringify(this.proTableData));
            if (dataObj.detailList.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请添加产品',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            let proSuc = true;
            dataObj.detailList.forEach((item) => {
                let slen = String(item.rate).indexOf('.') + 1;
                if (slen > 0) {
                    this.$warning({
                        title: '操作提示',
                        content: '添加的产品税率不能为小数',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    proSuc = false;
                    throw new Error('结束循环');
                }
                if (item.unit_inner_sn === '' || !item.unit_inner_sn) {
                    this.$warning({
                        title: '操作提示',
                        content: '添加的产品单位不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    proSuc = false;
                    throw new Error('结束循环');
                }
                if (item.quantity === '' || !item.quantity) {
                    this.$warning({
                        title: '操作提示',
                        content: '添加的产品数量不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    proSuc = false;
                    throw new Error('结束循环');
                }
                if (item.rate_unit === '' || !item.rate_unit) {
                    this.$warning({
                        title: '操作提示',
                        content: '添加的产品含税单价不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    proSuc = false;
                    throw new Error('结束循环');
                }
                let nowItem = item;
                dataObj.detailList.forEach((item) => {
                    if (item.specification === nowItem.specification && item.key !== nowItem.key) {
                        if (
                            item.unit_inner_sn === nowItem.unit_inner_sn &&
                            item.name === nowItem.name &&
                            item.sys_type === nowItem.sys_type
                        ) {
                            this.$warning({
                                title: '操作提示',
                                content: '相同规格型号的产品包装单位不能相同',
                                okText: '知道了',
                                centered: true,
                                maskClosable: true
                            });
                            proSuc = false;
                            throw new Error('结束循环');
                        }
                    }
                });
                console.log(dataObj);
            });
            if (!proSuc) {
                return false;
            }
            dataObj.status = type;
            // 根据不同发起机构判断
            console.log('SYSTEMTYPE', this.$cookie.get('userSystemType'));
            console.log('ORGTYPE', this.orgSystemType);
            console.log('BILLTYPE', BillsType);
            let self = parseInt(this.$cookie.get('userSystemType'));
            let partB = parseInt(this.orgSystemType);
            if (self === 1) {
                // 医院
                if (partB === 2) {
                    // 医院对经销商
                    dataObj.billsType = BillsType.HospitalDealer;
                }
                if (partB === 3) {
                    // 医院对厂商
                    dataObj.billsType = BillsType.HospitalVendor;
                }
            }
            if (self === 2) {
                // 经销商
                if (partB === 2) {
                    // 经销商对经销商
                    dataObj.billsType = BillsType.DealerDealer;
                }
                if (partB === 3) {
                    // 经销商对厂商
                    dataObj.billsType = BillsType.DealerVendor;
                }
            }
            dataObj.detailList.forEach((item) => {
                item.rate = item.rate / 100;
                item.product_name = item.name; // 产品名称
                item.unit = item.unit_name; // 包装单位名称
                /* 传递给后端的字段取值变为 【单价 = 含税单价   含税单价 = 单价】 */
                // let unitPrice = item.unit_price.toFixed(4)
                item.unit_price = item.rate_unit;
                // item.rate_unit = unitPrice
                item.sum = item.sum.toFixed(2);
                item.rate_num = item.rate_num.toFixed(2);
            });
            console.log(dataObj);
            await inquiryAddAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let obj = res.info;
                    obj.isActive = '';
                    obj.key = obj.billsType + '' + obj.ilps_inner_sn + '' + obj.partA + '' + obj.partB;
                    obj.sys_type = partB;
                    this.$emit('done', obj);
                }
            });
            this.proTableData = [];
            this.formData.contacts = '';
            this.formData.phone = '';
            this.formData.detailList = [];
            this.formData.description = '';
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 253;
            return clientHeight - topHeight;
        },
        planModalTextComputed() {
            // 采购计划单右下角的取消文字改变
            if (this.planModalForm.status === 0 || this.planModalForm.status === 1 || this.btnName === 'add') {
                return '取消';
            } else {
                return '关闭';
            }
        }
    },
    created() {
        this.nowOrgType = parseFloat(this.$cookie.get('userSystemType'));
        if (this.nowOrgType === 1) {
            // 医院
            this.tabsActiveKey = 2;
        }
        if (this.tabsActiveKey === 1) {
            this.isReply = true;
        }
        this.loadTable();
        this.getInquiryOrgListFn(2);
        this.getInquiryOrgListFn(3);
        this.loadAllPro();
    }
};
</script>

<style lang="less">
.inquiry-price-wrap {
    // 价格modal容器
    pointer-events: none;

    .inquiry-price-md {
        pointer-events: auto;
    }
}
.inquiry-reply-wrap {
    // 回复modal容器
    pointer-events: none;

    .reply-inquiry-md {
        pointer-events: auto;
    }
}
.inquiry-edit-wrap {
    // 编辑modal容器
    pointer-events: none;

    .edit-inquiry-md {
        pointer-events: auto;
    }
}
</style>
<style lang="less">
.show_inquiry {
    position: absolute;
    margin-top: -33px;
    padding-left: 20px;
}
.create-tip-box {
    height: 108px;
    padding-top: 24px;
    text-align: center;
    .tip-q {
        margin-top: 20px;
    }
    .radio-box {
        margin-top: 20px;
    }
}
.ant-input-number {
    width: 100%;
}
.ant-layout-content {
    overflow-y: auto !important;
}
.ant-select {
    .ant-select-selection {
        width: 100%;
    }
    .ant-select-selection__rendered {
        width: 100%;
    }
}
.ant-select .ant-select-selection,
.ant-input {
    border-radius: 0;
    border-top: none;
    border-left: none;
    border-right: none;
    background: inherit;
}
.ant-input:focus {
    box-shadow: none;
}
.send-plan-order-preview {
    .ant-modal-body {
        padding: 0;
    }

    .modal-form-input ul {
        &.build-plan-parent {
            margin-bottom: 0;
            padding: 15px;

            .build-plan-parent-item {
                padding-bottom: 24px;
                margin-bottom: 24px;
                border-bottom: 4px dashed #ddd;
            }
        }
    }
}
.ant-modal.choose_org_md {
    left: calc(50% - 300px);
}
.scoped .search-form-wrap {
    margin-top: 21px;
    background: inherit;
}
.scoped.scoped-white .tab-table-wrap {
    position: relative;
    top: inherit !important;
}
.shop-card {
    width: 100%;
    margin: 1.2em auto;
    padding: 1.2em;
    border: 1px solid #ced4da;
    -webkit-box-shadow: 0 5px 20px -15px black;
    box-shadow: 0 5px 20px -15px black;
    font-size: 0.8rem;
    font-weight: bold;
    color: #666666;
}
.shop-card-pre-header {
    .hight-search-text {
        position: absolute;
        right: 10px;
        top: 0;
        font-size: 22px;

        &:hover {
            color: #1890ff;
            cursor: pointer;
        }
    }
    h3 {
        text-align: left;
        margin-top: 4px;
    }
}
.search-box {
    margin-top: 20px;
    margin-bottom: 6px;
    padding: 0 10px;
    .search-item {
        min-width: 20%;
        margin-right: 5px;
        display: inline-block;
        vertical-align: top;
        .ant-cascader-input.ant-input {
            height: 24px !important;
        }
    }
    .label {
        display: inline-block;
        min-width: 40px;
        text-align: right;
        margin-right: 10px;
    }
    .input_box {
        display: inline-block;
        width: 156px;
        .ant-input {
            line-height: 24px;
            height: 24px;
        }
    }
}
.even-columns {
    display: flex;
    gap: 1em;
}
.even-columns > * {
    flex-basis: 100%;
    display: flex;
}
.row-terms {
    display: flex;
    gap: 2em;
}
.col-terms-1 {
    flex-basis: 60%;
}
.col-terms-2 {
    flex-basis: 40%;
}
.col-label {
    text-align: right;
    width: 30%;
}
.col {
    flex-basis: 50%;
    align-items: center;
}
.col-input,
col-input > * {
    width: 80%;
    padding-right: 4em;
}
.group {
    width: 100%;
    margin: 1em 0;
}
.card-add-row {
    width: 100%;
    text-align: right;
    padding: 10px 0;

    a {
        cursor: pointer;
    }
}
.footer-btn-list {
    display: flex;
    -webkit-box-orient: horizontal;
    -webkit-box-direction: normal;
    -ms-flex-direction: row;
    flex-direction: row;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    padding-left: 0;
    margin-top: 20px;
    margin-bottom: 0;

    button + button {
        margin-left: 8px;
        margin-bottom: 0;
    }
}
</style>
