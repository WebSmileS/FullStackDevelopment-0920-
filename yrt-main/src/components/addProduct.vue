<template>
    <div class="scoped" :style="'margin: -24px;height: ' + (520 - ipadHeight) + 'px;'">
        <div class="tabs-product-wrap">
            <div class="tabs-product-header">
                <ul class="tabs-product-nav">
                    <li @click="changeTabsFn('ven')" :class="activeTab === 'ven' ? 'active-tab' : ''">厂商产品</li>
                    <li v-if="false" @click="changeTabsFn('dea')" :class="activeTab === 'dea' ? 'active-tab' : ''">
                        经销商自建产品
                    </li>
                </ul>
                <div
                    v-if="
                        contractProductCheckable &&
                        (this.partB.modalType === 7 || this.partB.modalType === 11 || this.partB.modalType === 4)
                    "
                    slot="tabBarExtraContent"
                    class="add-product-checkbox"
                >
                    <a-checkbox :checked="isContractOrgChecked" @change="onContractOrgChangeFn">
                        合同内产品
                    </a-checkbox>
                </div>
            </div>
            <ul class="tabs-product-content">
                <!-- 厂商产品 -->
                <li v-show="activeTab === 'ven'">
                    <div class="search-box">
                        <div class="search-item">
                            <span class="label" style="width: 65px">名称</span>
                            <div class="input_box">
                                <a-input
                                    type="text"
                                    v-model="searchDataVen.name"
                                    @change="searchDataVenChangeFn"
                                    placeholder=""
                                />
                            </div>
                        </div>
                        <div class="search-item" style="margin-left: 36px">
                            <span class="label">厂商</span>
                            <div class="input_box" style="position: relative; margin-right: 10px">
                                <a-input
                                    type="text"
                                    readOnly
                                    v-model="searchDataVen.vendor_name"
                                    @click.stop="openChooseOrg()"
                                    :disabled="isVendor"
                                    placeholder=""
                                />
                                <span
                                    v-if="!isVendor"
                                    v-show="searchDataVen.vendor_name"
                                    @click="clearSearchDataVenFn('vendor')"
                                    class="close"
                                    >X</span
                                >
                            </div>
                        </div>
                        <div class="search-item" v-if="partB.modalType !== 10">
                            <span class="label">自定义类型</span>
                            <div class="input_box input" style="margin-right: 10px">
                                <a-cascader
                                    v-if="userSystemType === 2"
                                    v-model="searchDataVen.type_inner_sn"
                                    :options="categoryList"
                                    :loadData="loadCategory"
                                    placeholder=""
                                    changeOnSelect
                                    :displayRender="displayRender"
                                />
                                <a-cascader
                                    v-else
                                    v-model="searchDataVen.type_inner_sn"
                                    :options="categoryList"
                                    :loadData="loadCategory"
                                    placeholder=""
                                    :displayRender="displayRender"
                                />
                            </div>
                        </div>
                        <div class="search-item" v-if="partB.modalType === 8">
                            <span class="label">仓库</span>
                            <div class="input_box input" style="margin-right: 10px">
                                <a-select
                                    :options="auth_warehouse_list"
                                    v-model="searchDataVen.warehouse_inner_sn"
                                    :allowClear="true"
                                    :disabled="isWarehouseDisabled"
                                    placeholder=""
                                />
                            </div>
                        </div>
                    </div>
                    <div class="search-box" v-if="partB.modalType !== 10">
                        <div class="search-item" style="margin-left: 10px">
                            <span class="label">产品分类</span>
                            <div class="input_box" style="margin-right: 10px">
                                <a-cascader
                                    v-model="searchDataVen.code68_sn"
                                    :options="op68List"
                                    :loadData="load68data"
                                    placeholder=""
                                    changeOnSelect
                                    :displayRender="displayRender"
                                />
                            </div>
                        </div>
                        <div class="search-item">
                            <span class="label" style="width: 65px">产品编码</span>
                            <div class="input_box">
                                <a-input type="text" v-model="searchDataVen.specification_vendor_sn" placeholder="" />
                            </div>
                        </div>
                        <div class="search-item" style="margin-left: 14px">
                            <span class="label" style="width: 65px">规格型号</span>
                            <div class="input_box">
                                <a-input type="text" v-model="searchDataVen.specification" placeholder="" />
                            </div>
                        </div>
                        <div class="search-item" style="min-width: 7%">
                            <a-button
                                class="ant-btn-sm"
                                @keydown.enter="searchDataKeyDownFn"
                                @click.self="searchDataFn($event, 3)"
                                style="width: 65px; margin-left: 20px"
                                type="primary"
                                >搜索</a-button
                            >
                        </div>
                    </div>
                    <vxe-grid
                        ref="venTable"
                        size="small"
                        :keyboard-config="{ isArrow: true }"
                        :columns="currentVendorColumns"
                        :height="scrollYVenNumber - ipadHeight"
                        :sync-resize="activeTab"
                        style="margin: 0 22px"
                        resizable
                        auto-resize
                        border
                        highlight-current-row
                        align="center"
                        :data="venTableData"
                        :loading="venPage.loading"
                        :start-index="(venPage.current - 1) * venPage.pageSize"
                        row-id="keyFormat"
                        @checkbox-change="checkboxChangeFn"
                        @radio-change="radioChangeFn"
                        @checkbox-all="checkboxAllFn"
                        :checkbox-config="{ reserve: true, highlight: true, range: false }"
                        @cell-click="partB.isKeyCode ? clickAddFn : () => {}"
                        @cell-dblclick="DblClickAddFn"
                        @current-change="currentChangeFn"
                    >
                    </vxe-grid>
                    <vxe-pager
                        style="margin: 0 22px"
                        :loading="venPage.loading"
                        :current-page="venPage.current"
                        :page-size="venPage.pageSize"
                        :page-sizes="venPage.pageSizes"
                        :total="venPage.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changeVenPagi"
                    >
                    </vxe-pager>
                </li>
                <!-- 经销商自建产品 -->
                <li v-show="activeTab === 'dea' && !isShowDealerTabPane">
                    <div class="search-box">
                        <div class="search-item">
                            <span class="label">名称</span>
                            <div class="input_box" style="margin-right: 10px">
                                <a-input
                                    type="text"
                                    v-model="searchDataDea.name"
                                    @change="searchDataVenChangeFn"
                                    placeholder=""
                                />
                            </div>
                        </div>
                        <div class="search-item">
                            <span class="label">经销商</span>
                            <div class="input_box" style="position: relative; margin-right: 10px">
                                <a-input
                                    :disabled="isDealer"
                                    type="text"
                                    readOnly
                                    v-model="searchDataDea.dealer_name"
                                    @click.stop="openChooseOrg()"
                                    placeholder=""
                                />
                                <span
                                    v-if="!isDealer"
                                    v-show="searchDataDea.dealer_name"
                                    @click="clearSearchDataVenFn('dealer')"
                                    class="close"
                                    >X</span
                                >
                            </div>
                        </div>
                        <div class="search-item">
                            <span class="label">产品分类</span>
                            <div class="input_box" style="margin-right: 10px">
                                <a-cascader
                                    v-model="searchDataDea.code68_sn"
                                    :options="op68List"
                                    :loadData="load68data"
                                    changeOnSelect
                                    placeholder=""
                                    :displayRender="displayRender"
                                />
                            </div>
                        </div>
                        <div class="search-item" style="min-width: 8%">
                            <a-button
                                class="ant-btn-sm"
                                @keydown.enter="searchDataKeyDownFn"
                                @click.self="searchDataFn($event, 2)"
                                style="width: 66px"
                                type="primary"
                                >搜索</a-button
                            >
                        </div>
                    </div>
                    <!-- <div class="search-box">
                            <div class="search-item">
                                <span class="label" style="width: 65px">产品编码</span>
                                <div class="input_box">
                                    <a-input type='text'
                                             v-model="searchDataDea.specification_vendor_sn"
                                             placeholder="" />
                                </div>
                            </div>
                        </div> -->
                    <vxe-grid
                        ref="deaTable"
                        size="small"
                        style="margin: 0 22px"
                        :keyboard-config="{ isArrow: true }"
                        :columns="dealerColumns"
                        :height="scrollYDeaNumber - ipadHeight"
                        :sync-resize="activeTab"
                        border
                        resizable
                        auto-resize
                        highlight-current-row
                        align="center"
                        :data="deaTableData"
                        :loading="deaPage.loading"
                        :start-index="(deaPage.current - 1) * deaPage.pageSize"
                        row-id="keyFormat"
                        @checkbox-change="checkboxChangeFn"
                        @radio-change="radioChangeFn"
                        @checkbox-all="checkboxAllFn"
                        :checkbox-config="{ reserve: true, highlight: true, range: true }"
                        @cell-click="partB.isKeyCode ? clickAddFn : () => {}"
                        @cell-dblclick="DblClickAddFn"
                        @current-change="currentChangeFn"
                    >
                    </vxe-grid>
                    <vxe-pager
                        style="margin: 0 22px"
                        :loading="deaPage.loading"
                        :current-page="deaPage.current"
                        :page-size="deaPage.pageSize"
                        :page-sizes="deaPage.pageSizes"
                        :total="deaPage.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changeDeaPagi"
                    >
                    </vxe-pager>
                </li>
            </ul>
        </div>

        <a-modal
            v-model="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            @cancel="chooseOrgMd.alert = false"
            :zIndex="3"
            class="scoped modal-form-input-scoped global-drag choose_org_md_add"
        >
            <div v-globalDrag="{ el: 'choose_org_md_add' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <div style="margin-top: 56px">
                <choose-org-tab
                    ref="chooseOrgMd"
                    @chooseOrg="chooseOrgFn"
                    :pageTypeName="1"
                    :partType="chooseOrgType"
                ></choose-org-tab>
            </div>
        </a-modal>
    </div>
</template>

<script>
import { mapState } from 'vuex';
import { grnAndOdoOrderType } from '@/config/constants';

import {
    odoDealerListAPI,
    odoVendorListAPI,
    dealerGetNextListAPI,
    dealerGetParentListAPI,
    grnVendorListAPI,
    grnDealerListAPI,
    orderVendorListAPI,
    orderDealerListAPI,
    planVendorListAPI,
    planDealerListAPI,
    initialDealerListAPI,
    hospitalApplyProductListAPI,
    productDealerListContractAPI,
    productVendorListContractAPI,
    getVendorList,
    categoryGetNextListAPI,
    categoryGetParentListAPI,
    code68ParentListAPI,
    code68ChildrenListAPI,
    insDealerListAPI,
    getVendorProductListAPI,
    getVendorProductDropdownAPI,
    receiptVendorListAPI,
    receiptDealerListAPI,
    grnNoticeProductListAPI,
    odoNoticeProductListAPI
} from '@/service/pageAjax';
import chooseOrgTab from '@/components/chooseOrgTab';

export default {
    name: 'addProduct',
    /**
     * =======================================================================
     *                              PartB
     * =======================================================================
     *  /// 键盘事件需要用到的值区域开始
     * isKeyCode                -> true-触发了键盘事件，需要将值传入输入框，false-不处理
     * keyCodeVal               -> 触发了键盘事件对应的值
     *  /// 键盘事件需要用到的值区域结束
     * billsType                -> 对应data中billsTypeObj
     * isProcurementSalesFlag   -> 是否为销售，销售为true，采购为false
     * modalType                ->   1 - 合同内的产品
     *                               2 - 期初库存的产品列表
     *                               3 - 采购计划
     *                               4 - 采购/销售订单
     *                               6 - 询价单
     *                               7 - 入库单，代储代配
     *                               8 - 出库单
     *                               9 - 验货单
     *                               12 - 代储代配
     * oname                    -> 采购中为相关机构名称，销售中为登录机构名称
     * partA_id                 -> 采购中为登录机构id，销售中为相关机构id
     * partB_id                 -> 采购中为相关机构id，销售中为登录机构id
     * warehouseDatas           -> 出库单的仓库列表值，不一定存在
     * ======================================================================
     *                                      以上为*销售/采购订单*中partB的信息
     *                                              louxiaochen - 2020/10/28
     * ======================================================================
     */
    props: {
        partB: Object,
        metaTitle: String,
        multiple: {
            type: Boolean,
            default: true
        },
        contractProductCheckable: {
            type: Boolean,
            default: true
        },
        // 仓库是否只读，默认false-可编辑
        isWarehouseParentDisabled: {
            type: Boolean,
            default: false
        },
        scrollYVenNumber: {
            type: Number,
            default: 315
        },
        scrollYDeaNumber: {
            type: Number,
            default: 355
        },
        // 出入库单的单据 [采购退货、销售退货]
        orderTypeData: {
            type: Object,
            required: false
        },
        // 代储代配通知类型
        warehouseLeasingNoticeType: {
            type: String,
            required: false,
            validator(value) {
                return ['grn', 'odo'].indexOf(value) !== -1;
            }
        },
        // 代储代配经销商innersn
        warehouseLeasingDealerInnerSn: {
            required: false,
            validator(value) {
                return typeof value === 'string' || typeof value === 'number';
            }
        }
    },
    components: {
        chooseOrgTab
    },
    data() {
        return {
            billsTypeObj: {
                0: 'DD',
                1: 'DV',
                2: 'DH',
                3: 'HH',
                4: 'HD',
                5: 'HV',
                6: 'VD',
                7: 'VH',
                8: 'VV'
            }, // 入库单单据类型
            employeeInfo: JSON.parse(this.$cookie.get('EmployeeInfo')),
            userSystemType: parseFloat(this.$cookie.get('userSystemType')), // 0-平台、1-医院 、2-经销商、3-厂商
            isShowDealerTabPane: false,
            isDealer: false,
            isVendor: false,
            chooseOrgType: 'ven',
            chooseOrgMd: {
                title: this.metaTitle ? `${this.metaTitle} - 选择机构` : `${this.$route.meta.title} - 新增 - 选择机构`,
                alert: false,
                loading: false
            },
            searchName: '',
            selectedRowKeysDea: [],
            selectedRowsDea: [], // 选中tab经销商
            selectedRowKeysVen: [],
            selectedRowsVen: [], // 选中tab厂商
            searchDataVen: {
                name: '',
                vendor_inner_sn: '', // 产商ID
                vendor_name: '', //
                specification: '',
                specification_vendor_sn: '',
                warehouse_inner_sn: '',
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [] // 医疗器械68分类编号
            },
            searchDataDea: {
                name: '',
                dealer_inner_sn: '', // 经销商id
                dealer_name: '',
                specification_vendor_sn: '',
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [] // 医疗器械68分类编号
            },
            vendorList: [],
            categoryList: [],
            categoryListDea: [],
            op68List: [],
            defaultVendorColumns: [
                {
                    title: '',
                    dataIndex: '',
                    minWidth: '50',
                    type: this.multiple ? 'checkbox' : 'radio',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    title: '名称',
                    dataIndex:
                        this.partB.modalType === 1 ||
                        this.partB.modalType === 3 ||
                        this.partB.modalType === 4 ||
                        this.partB.modalType === 7
                            ? 'product_name'
                            : 'name',
                    minWidth: '200',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            let data = row.name || row.product_name;
                            return [data];
                        }
                    }
                },
                {
                    title: '产品编码',
                    dataIndex: 'specification_vendor_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '150',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification_vendor_sn];
                        }
                    }
                },
                {
                    title: '规格型号',
                    dataIndex: this.partB.modalType === 1 ? 'specification_name' : 'specification',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '200',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let data = row.specification_name || row.specification;
                            return [data];
                        }
                    }
                },
                {
                    title: '厂商',
                    dataIndex: 'vendor_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '190',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.vendor_name];
                        }
                    }
                },
                {
                    title: '注册证号',
                    dataIndex: 'mdrf_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '140',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.mdrf_sn];
                        }
                    }
                }
            ],
            vendorPlanColumns: [
                {
                    title: '',
                    dataIndex: '',
                    minWidth: '50',
                    type: this.multiple ? 'checkbox' : 'radio',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    title: '名称',
                    dataIndex:
                        this.partB.modalType === 1 ||
                        this.partB.modalType === 3 ||
                        this.partB.modalType === 4 ||
                        this.partB.modalType === 7
                            ? 'product_name'
                            : 'name',
                    minWidth: '230',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            let data = row.name || row.product_name;
                            return [data];
                        }
                    }
                },
                {
                    title: '产品编码',
                    dataIndex: 'specification_vendor_sn',
                    minWidth: '150',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.specification_vendor_sn];
                        }
                    }
                },
                {
                    title: '规格型号',
                    dataIndex: this.partB.modalType === 1 ? 'specification_name' : 'specification',
                    minWidth: '120',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            let data = row.specification_name || row.specification;
                            return [data];
                        }
                    }
                },
                {
                    title: '厂商',
                    dataIndex: 'product_vendor_name',
                    minWidth: '150',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.product_vendor_name];
                        }
                    }
                },
                {
                    title: '供应商',
                    dataIndex: 'vendor_name',
                    minWidth: '150',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.vendor_name];
                        }
                    }
                },
                {
                    title: '注册证号',
                    dataIndex: 'mdrf_sn',
                    minWidth: '150',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.mdrf_sn];
                        }
                    }
                }
            ],
            vendorCertColumns: [
                {
                    title: '',
                    dataIndex: '',
                    minWidth: '50',
                    type: 'checkbox',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    title: '名称',
                    dataIndex:
                        this.partB.modalType === 1 ||
                        this.partB.modalType === 3 ||
                        this.partB.modalType === 4 ||
                        this.partB.modalType === 7
                            ? 'product_name'
                            : 'name',
                    minWidth: '400',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            let data = row.name || row.product_name;
                            return [data];
                        }
                    }
                },
                {
                    title: '厂商',
                    dataIndex: 'vendor_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '300',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.vendor_name];
                        }
                    }
                },
                {
                    title: '注册证号',
                    dataIndex: 'mdrf_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '150',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.mdrf_sn];
                        }
                    }
                }
            ],
            dealerColumns: [
                {
                    title: '',
                    dataIndex: '',
                    minWidth: '50',
                    type: this.multiple ? 'checkbox' : 'radio',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center'
                },
                {
                    title: '名称',
                    dataIndex:
                        this.partB.modalType === 1 ||
                        this.partB.modalType === 3 ||
                        this.partB.modalType === 4 ||
                        this.partB.modalType === 7
                            ? 'product_name'
                            : 'name',
                    minWidth: '350',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            let data = row.name || row.product_name;
                            return [data];
                        }
                    }
                },
                // 修复2888时隐藏 出库单和入库单自建产品没有产品编码，其它页面的未知
                // {
                //     title: '产品编码',
                //     dataIndex: 'specification_vendor_sn',
                //     minWidth: '200',
                //     align: 'center',
                //     showOverflow: true,
                //     showHeaderOverflow: true,
                //     slots: {
                //         default: ({ row }) => {
                //             return [row.specification_vendor_sn]
                //         }
                //     }
                // },
                {
                    title: '规格型号',
                    dataIndex: this.partB.modalType === 1 ? 'specification_name' : 'specification',
                    minWidth: '100',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            let data = row.specification_name || row.specification;
                            return [data];
                        }
                    }
                },
                {
                    title: '经销商',
                    dataIndex: 'vendor_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '300',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.vendor_name];
                        }
                    }
                }
            ],
            // 仓库（库存量） 表格列
            warehouseColumn: {
                title: '仓库（库存量）',
                dataIndex: 'warehouse_name',
                showOverflow: true,
                showHeaderOverflow: true,
                minWidth: '200',
                align: 'center',
                slots: {
                    default: ({ row }) => {
                        return `${row.warehouse_name}（${row.warehouseQuantity}${row.unit_name}）`;
                    }
                }
            },
            currentVendorColumns: [],
            hosTableData: [],
            deaTableData: [],
            venTableData: [],
            activeTab: 'ven',
            tabName: [],
            deaPage: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            venPage: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            isContractOrgChecked: true,
            selectAllCheckboxRecords: {
                0: [],
                1: []
            },
            selectedRadioRecord: undefined, // 单选模式下
            ipadHeight: parseFloat(localStorage.getItem('isIpadHeight')) > 0 ? 57 : 0,
            parentIsKeybordTabAdd: false, // -> 父组件触发了回车事件的标识，停止子组件的默认行为
            auth_warehouse_list: []
        };
    },
    methods: {
        // 修改合同内产品状态值
        onContractOrgChangeFn(e) {
            this.isContractOrgChecked = e.target.checked;
            this.pageDataInit(true); // true => 说明用户在操作合同内产品单选框，不需要清空重置数据
        },
        checkboxChangeFn({ checked, row }) {
            if (this.partB.isKeyCode) {
                // 将初始化或者上下键移动添加那条数据删掉，但是要注意万一上下键移动的当前条和选中的是同一条数据，就不用删除了,所以需要初始化
                row.isKeybordTabAdd = false;

                if (checked) {
                    if (this.activeTab === 'ven') {
                        this.$refs.venTable.setCurrentRow(row);
                    } else if (this.activeTab === 'dea') {
                        this.$refs.deaTable.setCurrentRow(row);
                    }
                }
            }
            this.clearKeybordTabAddRowFn(checked, row);
            this.setCheckboxReserveRecordsFn([row], checked);
        },
        radioChangeFn({ row }) {
            if (this.partB.isKeyCode) {
                row.isKeybordTabAdd = false;
                if (this.activeTab === 'ven') {
                    this.$refs.venTable.setCurrentRow(row);
                } else if (this.activeTab === 'dea') {
                    this.$refs.deaTable.setCurrentRow(row);
                }
            }
            this.selectedRadioRecord = row;
        },
        clearKeybordTabAddRowFn() {
            if (this.partB.isKeyCode) {
                for (let key in this.selectAllCheckboxRecords) {
                    let records = this.selectAllCheckboxRecords[key];
                    if (typeof records === 'object') {
                        if (records && records.length) {
                            for (let i = 0; i < records.length; i++) {
                                let rtem = records[i],
                                    productType = parseInt(rtem.product_type),
                                    productTypeName = productType === 0 ? 'dealer' : 'vendor',
                                    curKeyFormat = rtem.keyFormat + productTypeName;

                                // if (row && row._XID === rtem._XID) continue
                                // if (row && typeof row.isKeybordTabAdd !== 'undefined' && !row.isKeybordTabAdd) continue
                                if (rtem.isKeybordTabAdd) {
                                    delete this.selectAllCheckboxRecords[curKeyFormat];
                                    records.splice(i, 1);
                                    i--;
                                }
                            }
                        }
                    }
                }
            }
        },
        checkboxAllFn({ checked, records }) {
            if (!checked) {
                if (this.activeTab === 'ven') {
                    records = this.$refs.venTable.getTableData().tableData;
                } else if (this.activeTab === 'dea') {
                    records = this.$refs.deaTable.getTableData().tableData;
                }
            }
            this.setCheckboxReserveRecordsFn(
                records.map((item) => {
                    return {
                        ...item,
                        isKeybordTabAdd: false
                    };
                }),
                checked
            );
        },
        searchDataVenChangeFn(e) {
            if (this.partB.isKeyCode) {
                let val = e.target.value;
                if (!val) {
                    this.partB.keyCodeVal = '';
                    this.searchDataVen.name = '';
                } else {
                    this.partB.keyCodeVal = val;
                    this.searchDataVen.name = val;
                }
            }
        },
        currentChangeFn({ row }) {
            if (this.partB.isKeyCode) {
                /// 1. 是否存储当前键盘上下移动选中的当前行，默认可以存储，但是存在勾选的时候，就不存储
                let isCacheCurrentChangeRow = false;
                for (let key in this.selectAllCheckboxRecords) {
                    let records = this.selectAllCheckboxRecords[key];
                    if (typeof records === 'object') {
                        if (records && records.length) {
                            isCacheCurrentChangeRow = records.some(
                                (item) => typeof item.isKeybordTabAdd !== 'undefined' && !item.isKeybordTabAdd
                            );
                        }
                    }
                }
                if (isCacheCurrentChangeRow) return;

                /// 2. 每次只有键盘上下键移动选中单条明细，回车只能添加一条数据
                this.selectAllCheckboxRecords = {
                    0: [],
                    1: []
                };
                row.isKeybordTabAdd = true;
                this.setCheckboxReserveRecordsFn([row], true);
            }
        },
        clickAddFn(data) {
            if (this.partB.isKeyCode) {
                let row = data.row || data;
                if (this.deaTableData && this.deaTableData.length) {
                    this.$refs.deaTable.setCurrentRow(row);
                } else if (this.venTableData && this.venTableData.length) {
                    let isChecked = this.$refs.venTable.isCheckedByCheckboxRow(row);
                    if (isChecked) {
                        // this.setCheckboxReserveRecordsFn([row], false)
                        this.$refs.venTable.setCurrentRow(row);
                    } else {
                        this.$refs.venTable.clearCurrentRow();
                    }
                    this.setCheckboxReserveRecordsFn([row], isChecked);
                }
            }
        },
        DblClickAddFn({ row }) {
            /// 如果是采购退货或销售退货添加产品，就屏蔽掉双击事件
            if (this.isReturnOrder) return;

            if (this.multiple) {
                this.setCheckboxReserveRecordsFn([row], true);
            } else {
                this.selectedRadioRecord = row;
            }
            this.returnProList();
        },
        initReturnProductWarehouseFn(warehouseInnerSn) {
            this.venTableData = [];
            this.deaTableData = [];
            this.deaPage = {
                loading: true,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageSizes: [10, 20, 30, 40],
                pageNum: 1
            };
            this.venPage = {
                loading: true,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageSizes: [10, 20, 30, 40],
                pageNum: 1
            };

            // 采购退货
            if (this.isSeparateBillOrder === grnAndOdoOrderType['PURCHASERETURN'].value) {
                if (warehouseInnerSn) {
                    this.searchDataVen.warehouse_inner_sn = warehouseInnerSn;
                }
                this.odoVendorListAPIFn();
                // this.odoDealerListAPIFn()
            }

            // 销售退货
            if (this.isSeparateBillOrder === grnAndOdoOrderType['SALERETURN'].value) {
                this.grnVendorListAPIFn();
                // this.grnDealerListAPIFn()
            }
        },
        setReturnProductWarehouseFn(item) {
            if (
                this.isReturnOrder &&
                item.warehouse_inner_sn &&
                parseInt(item.warehouse_inner_sn) !== parseInt(this.searchDataVen.warehouse_inner_sn)
            ) {
                this.$emit('returnWarehouseData', {
                    'warehouse_name': item.warehouse_name,
                    'warehouse_inner_sn': item.warehouse_inner_sn
                });

                this.initReturnProductWarehouseFn(item.warehouse_inner_sn);
            }
        },
        setCheckboxReserveRecordsFn(records, checked) {
            if (checked && records && records.length > 0) {
                /// 设置采购退货或销售退货第一个产品的仓库值
                this.setReturnProductWarehouseFn(records[0]);
            }

            for (let i = 0; i < records.length; i++) {
                let item = records[i],
                    productType = parseInt(item.product_type),
                    productTypeName = productType === 0 ? 'dealer' : 'vendor',
                    curKeyFormat = item.keyFormat + productTypeName;

                if (checked) {
                    // 勾选上
                    const compareKeyFormatAry = Object.values(this.selectAllCheckboxRecords).slice(2);
                    if (compareKeyFormatAry.some((cItem) => cItem === curKeyFormat)) continue;
                    item.isLoadingUnit = false;
                    item.row_add_flag = true; // 新增行标识
                    item.temp_add_flag = true; // 新增标识
                    item.batch_number = ''; // 新增『批号』字段
                    this.selectAllCheckboxRecords[productType].push(item);
                    this.selectAllCheckboxRecords[curKeyFormat] = curKeyFormat;
                } else {
                    for (let k = 0; k < this.selectAllCheckboxRecords[productType].length; k++) {
                        let selectItem = this.selectAllCheckboxRecords[productType][k];
                        if (selectItem.keyFormat + productTypeName === curKeyFormat) {
                            this.selectAllCheckboxRecords[productType].splice(k, 1);
                            delete this.selectAllCheckboxRecords[curKeyFormat];
                        }
                    }
                }
            }
        },
        clearTabCheckCurrentFn() {
            this.selectAllCheckboxRecords = {
                0: [],
                1: []
            };
            this.selectAllCheckboxRecords[0] = [];
            this.selectAllCheckboxRecords[1] = [];
        },
        clearCheckboxRowFn() {
            this.venTableData = [];
            this.deaTableData = [];
            this.selectedRowsDea = [];
            this.selectedRowKeysDea = [];
            this.selectedRowsVen = [];
            this.selectedRowKeysVen = [];
            this.clearTabCheckCurrentFn();

            const venTable = this.$refs.venTable,
                deaTable = this.$refs.deaTable;
            if (venTable) {
                venTable.clearCheckboxReserve();
                venTable.clearCheckboxRow();
            }
            if (deaTable) {
                deaTable.clearCheckboxReserve();
                deaTable.clearCheckboxRow();
            }
        },
        returnProList() {
            if (this.chooseOrgMd.alert) return;
            if (this.multiple) {
                let newDeaData = this.selectAllCheckboxRecords[0],
                    newVenData = this.selectAllCheckboxRecords[1],
                    isNewDataEmpty = newDeaData.length <= 0 && newVenData.length <= 0;

                if (isNewDataEmpty && !this.isReturnOrder) {
                    this.$warning({
                        title: '操作提示',
                        content: '请选择产品',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                if (this.partB.isKeyCode) {
                    const obj = this.filterActiveTabFn(newDeaData, newVenData);
                    this.$emit('returnData', obj.newDeaData, obj.newVenData);
                } else {
                    this.$emit('returnData', newDeaData, newVenData);
                }

                if (!this.isReturnOrder) {
                    this.resetDataFn();
                    this.clearCheckboxRowFn();
                }
            } else {
                this.$emit('returnData', this.selectedRadioRecord);
                this.resetDataFn();
            }
        },
        filterActiveTabFn(newDeaData, newVenData) {
            if (this.activeTab === 'ven') {
                newDeaData = newDeaData.filter((item) => !item.isKeybordTabAdd);
            } else if (this.activeTab === 'dea') {
                newVenData = newVenData.filter((item) => !item.isKeybordTabAdd);
            }
            return {
                newDeaData,
                newVenData
            };
        },
        openChooseOrg() {
            // 打开选择机构
            this.chooseOrgMd.alert = true;
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
            this.$Utils.globalDragCenterFn('choose_org_md_add');
        },
        chooseOrgFn(org) {
            this.chooseOrgMd.alert = false;
            let sys = parseInt(org.systemType);
            if (sys === 2) {
                this.searchDataDea.dealer_inner_sn = org.org_id;
                this.searchDataDea.dealer_name = org.org_name;
                // this.changePagenationFn(2, 'dealer')
            } else if (sys === 3) {
                this.searchDataVen.vendor_inner_sn = org.org_id;
                this.searchDataVen.vendor_name = org.org_name;
                // this.changePagenationFn(3, 'vendor')
            }
        },
        displayRender(obj) {
            let len = obj.labels.length;
            const label = obj.labels[len - 1];
            return label;
        },
        async getVendorListFn() {
            // 获取厂商
            await getVendorList().then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.vendorList = res.list;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async dealerGetParentListAPIFn() {
            // 获取自定义类型列表
            await dealerGetParentListAPI(this.$cookie.get('userbelong')).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    this.categoryListDea = list.map((item) => {
                        // let st
                        // if (item.is_leaf === 0) {
                        //     st = true
                        // } else if (item.is_leaf === 1) {
                        //     st = false
                        // }
                        item.label = item.type;
                        item.value = item.type_inner_sn; // tid
                        item.isLeaf = item.is_leaf === 1;
                        return item;
                    });
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async dealerGetNextListAPIFn(options) {
            // 加载自定义类型列表子集
            const targetOption = options[options.length - 1];
            targetOption.loading = true;
            await dealerGetNextListAPI(targetOption.dealer_inner_sn, targetOption.type_inner_sn).then((res) => {
                targetOption.loading = false;
                if (parseFloat(res.code) === 0) {
                    const { list } = res;
                    if (list.length > 0) {
                        targetOption.children = list.map((item) => {
                            // let st
                            // if (item.is_leaf === 0) {
                            //     st = true
                            // } else if (item.is_leaf === 1) {
                            //     st = false
                            // }
                            item.label = item.type;
                            item.value = item.type_inner_sn;
                            item.isLeaf = item.is_leaf === 1;
                            return item;
                        });
                    } else {
                        targetOption.isLeaf = true;
                    }
                    targetOption.loading = false;
                    this.categoryListDea = [...this.categoryListDea];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async getCategoryListFn() {
            // 获取自定义类型列表
            await categoryGetParentListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    this.categoryList = rows.map((item) => ({
                        value: item['type_inner_sn'],
                        label: item.type,
                        isLeaf: item['is_leaf'] === 1
                    }));
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async loadCategory(options) {
            // 加载自定义类型列表子集
            const targetOption = options[options.length - 1];
            targetOption.loading = true;
            const id = targetOption.value;
            await categoryGetNextListAPI(id).then((res) => {
                targetOption.loading = false;
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    if (rows.length > 0) {
                        targetOption.children = rows.map((item) => {
                            // let st
                            // if (item.is_leaf === 0) {
                            //     st = true
                            // } else if (item.is_leaf === 1) {
                            //     st = false
                            // }
                            item.label = item.type;
                            item.value = item.type_inner_sn;
                            item.isLeaf = item.is_leaf === 1;
                            return item;
                        });
                    } else {
                        targetOption.isLeaf = true;
                    }
                    targetOption.loading = false;
                    this.categoryList = [...this.categoryList];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async get68ParentListFn() {
            // 获取68分类
            await code68ParentListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    let newArray = [];
                    rows.forEach((item) => {
                        // let st
                        // if (item.is_leaf === 0) {
                        //     st = true
                        // } else if (item.is_leaf === 1) {
                        //     st = false
                        // }
                        let obj = {
                            label: '(' + item.code68_sn + ') ' + item.name,
                            value: item.code68_sn,
                            isLeaf: item.is_leaf === 1
                        };
                        newArray.push(obj);
                    });
                    this.op68List = newArray;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async load68data(options) {
            // 加载68分
            const targetOption = options[options.length - 1];
            targetOption.loading = true;
            const id = targetOption.value;
            await code68ChildrenListAPI(id).then((res) => {
                targetOption.loading = false;
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    if (rows.length > 0) {
                        targetOption.children = rows.map((item) => {
                            // let st
                            // if (item.is_leaf === 0) {
                            //     st = true
                            // } else if (item.is_leaf === 1) {
                            //     st = false
                            // }
                            item.label = '(' + item.code68_sn + ') ' + item.name;
                            item.value = item.code68_sn;
                            item.isLeaf = item.is_leaf === 1;
                            return item;
                        });
                    } else {
                        targetOption.isLeaf = true;
                    }
                    targetOption.loading = false;
                    this.op68List = [...this.op68List];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        changeTabsFn(key) {
            // 切换tab
            this.activeTab = key;
            this.chooseOrgType = key;
            this.tableFocusFn();
        },
        tableFocusFn() {
            if (this.partB.isKeyCode) {
                if (this.activeTab === 'dea') {
                    this.$refs.deaTable.focus();
                    if (this.deaTableData && this.deaTableData.length) {
                        this.$refs.deaTable.clearCurrentRow();
                        const row = this.deaTableData[0];
                        row.isKeybordTabAdd = true;
                        this.$refs.deaTable.setCurrentRow(row);
                        this.setCheckboxReserveRecordsFn([row], true);
                    } else {
                        this.clearTabCheckCurrentFn();
                    }
                } else if (this.activeTab === 'ven') {
                    this.$refs.venTable.focus();
                    if (this.venTableData && this.venTableData.length) {
                        this.$refs.venTable.clearCurrentRow();
                        const row = this.venTableData[0];
                        row.isKeybordTabAdd = true;
                        this.$refs.venTable.setCurrentRow(row);
                        this.setCheckboxReserveRecordsFn([row], true);
                    } else {
                        this.clearTabCheckCurrentFn();
                    }
                }
            }
        },
        changeDeaPagi({ currentPage, pageSize }) {
            // 经销商翻页
            // this.selectedRowsDea = []
            this.deaPage.current = currentPage;
            this.deaPage.pageSize = pageSize;
            this.deaPage.loading = true;
            this.clearKeybordTabAddRowFn();
            this.changePagenationFn(2, 'dealer');
        },
        // 厂商翻页
        changeVenPagi({ currentPage, pageSize }) {
            // this.selectedRowsVen = []
            this.venPage.current = currentPage;
            this.venPage.pageSize = pageSize;
            this.venPage.loading = true;
            this.clearKeybordTabAddRowFn();
            this.changePagenationFn(3, 'vendor');
        },
        checkInput(val) {
            if (this.$Utils.checkInputValFn(val)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
        },
        searchDataKeyDownFn(e) {
            if (e.keyCode === 13) {
                // 回车
                this.parentIsKeybordTabAdd = true;
            }
        },
        searchDataDefaultFn() {
            if (this.partB.isKeyCode) {
                this.searchDataVen.name = this.partB.keyCodeVal;
                this.searchDataDea.name = this.partB.keyCodeVal;
                this.clearKeybordTabAddRowFn();
            }
            if (parseInt(this.partB.modalType) === 8) {
                this.searchDataVen.warehouse_inner_sn = this.partB.warehouse_inner_sn;
            }
        },
        searchDataFn(e, type) {
            /// 父级组件触发enter时候，需要阻止click事件
            if (this.parentIsKeybordTabAdd) {
                setTimeout(() => {
                    this.parentIsKeybordTabAdd = false;
                });
                return;
            }
            this.clearTabCheckCurrentFn();
            if (type === 3) {
                // 厂商
                this.selectedRowsVen = [];
                this.$refs.venTable.clearSelectReserve();
                this.$refs.venTable.reloadData(this.venTableData);
                this.changePagenationFn(3, 'vendor', true);
            }
            if (type === 2) {
                // 经销
                this.selectedRowsDea = [];
                this.$refs.deaTable.clearSelectReserve();
                this.$refs.deaTable.reloadData(this.deaTableData);
                this.changePagenationFn(2, 'dealer', true);
            }
        },
        changePagenationFn(type, typeName, init) {
            // 根据type改变不同API的翻页
            if (init) {
                if (typeName === 'dealer') {
                    this.deaPage.loading = true;
                    this.deaPage.current = 1;
                    this.deaPage.pageSize = 10;
                    this.deaPage.pageNum = 1;
                    if (
                        this.$Utils.checkInputValFn(this.searchDataDea.dealer_name) ||
                        this.$Utils.checkInputValFn(this.searchDataDea.specification_vendor_sn)
                    ) {
                        this.$warning({
                            title: '操作提示',
                            content: `${this.$Utils.tips().specialChart}`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        this.deaPage.loading = false;
                        return false;
                    }
                }
                if (typeName === 'vendor') {
                    this.venPage.loading = true;
                    this.venPage.current = 1;
                    this.venPage.pageSize = 10;
                    this.venPage.pageNum = 1;
                    if (
                        this.$Utils.checkInputValFn(this.searchDataVen.name) ||
                        this.$Utils.checkInputValFn(this.searchDataVen.specification_vendor_sn)
                    ) {
                        this.$warning({
                            title: '操作提示',
                            content: `${this.$Utils.tips().specialChart}`,
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        this.venPage.loading = false;
                        return false;
                    }
                }
            }
            // typeName === 'dealer' && (this.deaPage.loading = true)
            // typeName === 'vendor' && (this.venPage.loading = true)
            if (typeof this.partB.modalType === 'undefined') {
                this.loadTable(type);
            } else {
                switch (this.partB.modalType) {
                    case 1:
                        // typeName === 'dealer' && this.hospitalApplyProductListAPIFn(2)
                        typeName === 'vendor' && this.hospitalApplyProductListAPIFn(3);
                        break;
                    case 2:
                        // typeName === 'dealer' && this.initialDealerListAPIFn()
                        // typeName === 'vendor' && this.loadAllPro()
                        typeName === 'vendor' && this.grnVendorListAPIFn();
                        break;
                    case 3:
                        // typeName === 'dealer' && this.planDealerListAPIFn()
                        typeName === 'vendor' && this.planVendorListAPIFn();
                        break;
                    case 4:
                        // typeName === 'dealer' && this.orderDealerListAPIFn()
                        typeName === 'vendor' && this.orderVendorListAPIFn();
                        break;
                    case 5:
                        this.loadAllPro();
                        break;
                    case 6:
                        this.loadAllPro('inq');
                        break;
                    case 7:
                        // typeName === 'dealer' && this.grnDealerListAPIFn()
                        typeName === 'vendor' && this.grnVendorListAPIFn();
                        break;
                    case 8:
                        // typeName === 'dealer' && this.odoDealerListAPIFn()
                        typeName === 'vendor' && this.odoVendorListAPIFn();
                        break;
                    case 9:
                        // typeName === 'dealer' && this.insDealerListAPIFn()
                        typeName === 'vendor' && this.loadAllPro();
                        break;
                    case 10:
                        this.loadCertPro();
                        break;
                    case 11:
                        // typeName === 'dealer' && this.receiptDealerListAPIFn()
                        typeName === 'vendor' && this.receiptVendorListAPIFn();
                        break;
                    case 12:
                        this.grnAndOdoNoticeProductListAPIFn();
                        break;
                }
            }
        },
        // 【获取】经销商产品列表
        async initialDealerListAPIFn() {
            let params = {
                org_id: this.$cookie.get('userbelong'),
                name: this.searchDataDea.name,
                dealer_inner_sn: this.searchDataDea.dealer_inner_sn,
                type_inner_sn:
                    this.searchDataDea.type_inner_sn.length > 0
                        ? this.searchDataDea.type_inner_sn[this.searchDataDea.type_inner_sn.length - 1]
                        : '',
                code68_sn:
                    this.searchDataDea.code68_sn.length > 0
                        ? this.searchDataDea.code68_sn[this.searchDataDea.code68_sn.length - 1]
                        : '',
                pageSize: this.deaPage.pageSize,
                pageNum: this.deaPage.current
            };

            await initialDealerListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.deaTableData = rows.map((item) => {
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];

                            return {
                                ...item,
                                vendor_name: item.dealer_name,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat:
                                    item.dealer_inner_sn +
                                    '' +
                                    item.product_inner_sn +
                                    '' +
                                    item.product_type +
                                    '' +
                                    item.specification_inner_sn,
                                product_name: item.name || item.product_name
                            };
                        });
                        this.deaPage.total = res.total;
                        this.deaPage.loading = false;
                        this.$refs.deaTable.refreshColumn();
                    } else {
                        this.$message.error(res.msg);
                        this.deaPage.loading = false;
                    }
                })
                .catch(() => {
                    this.deaPage.loading = false;
                });
        },
        // 【获取经销商自建产品列表】
        async odoDealerListAPIFn() {
            let params = {
                // 修复2892 查其它经销商都正确，查天津经销商莫名出错
                // org_id: this.partB.oid || this.$cookie.get('userbelong'),
                org_id: parseFloat(this.partB.org_id) || this.$cookie.get('userbelong'),
                organization_inner_sn: this.partB.oid || this.partB.org_id,
                name: this.searchDataDea.name,
                // vendor_inner_sn: this.searchDataDea.dealer_inner_sn,
                type_inner_sn: this.searchDataDea.type_inner_sn.length
                    ? this.searchDataDea.type_inner_sn[this.searchDataDea.type_inner_sn.length - 1]
                    : '',
                code68_sn: this.searchDataDea.code68_sn.length
                    ? this.searchDataDea.code68_sn[this.searchDataDea.code68_sn.length - 1]
                    : '',
                billsType: this.partB.otype,
                pageSize: this.deaPage.pageSize,
                pageNum: this.deaPage.current
            };

            await odoDealerListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.deaTableData = rows.map((item) => {
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];

                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat:
                                    item.vendor_inner_sn +
                                    '' +
                                    item.product_inner_sn +
                                    item.specification_inner_sn +
                                    item.mdrf_inner_sn +
                                    item.product_type,
                                product_name: item.name || item.product_name,
                                name: item.name || item.product_name,
                                vendor_name: item.dealer_name || item.vendor_name,
                                dealer_name: item.dealer_name || item.vendor_name,
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                batch_number: '',
                                quantity: 1,
                                product_type: 0,
                                unit_price: 1,
                                money: 2,
                                produce_date: '',
                                overdue_date: '',
                                type: '0',
                                mdrfList: [{ value: item.mdrf_inner_sn, label: item.mdrf_sn, title: item.mdrf_sn }],
                                register_inner_sn: item.mdrf_inner_sn,
                                register_sn: item.mdrf_sn,
                                status: 0
                            };
                        });
                        this.deaPage.total = res.total;
                        this.deaPage.loading = false;
                        this.$refs.deaTable.refreshColumn();
                        this.tableFocusFn();
                    } else {
                        this.$message.error(res.msg);
                        this.deaPage.loading = false;
                    }
                })
                .catch(() => {
                    this.deaPage.loading = false;
                });
        },
        // 【获取厂商产品列表】
        async odoVendorListAPIFn() {
            let params = {
                org_id: this.$cookie.get('userbelong'),
                organization_inner_sn: this.partB.oid || this.partB.org_id,
                name: this.searchDataVen.name,
                warehouse_inner_sn: this.searchDataVen.warehouse_inner_sn,
                specification: this.searchDataVen.specification,
                vendor_inner_sn: this.searchDataVen.vendor_inner_sn,
                specification_vendor_sn: this.searchDataVen.specification_vendor_sn,
                type_inner_sn: this.searchDataVen.type_inner_sn.length
                    ? this.searchDataVen.type_inner_sn[this.searchDataVen.type_inner_sn.length - 1]
                    : '',
                code68_sn: this.searchDataVen.code68_sn.length
                    ? this.searchDataVen.code68_sn[this.searchDataVen.code68_sn.length - 1]
                    : '',
                billsType: this.partB.otype,
                pageSize: this.venPage.pageSize,
                pageNum: this.venPage.current
            };

            // 出库单：产品表格内需要展示仓库和库存，此时加载带仓库字段的列表数据
            if (this.partB.modalType === 8) {
                params.level = 1;
            }

            await odoVendorListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.venTableData = rows.map((item) => {
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];

                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat:
                                    item.vendor_inner_sn +
                                    '' +
                                    item.product_inner_sn +
                                    item.specification_inner_sn +
                                    item.mdrf_inner_sn +
                                    item.product_type,
                                product_name: item.name || item.product_name,
                                name: item.name || item.product_name,
                                vendor_name: item.dealer_name || item.vendor_name,
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                batch_number: '',
                                product_type: 1,
                                quantity: 1,
                                unit_price: 1,
                                money: 2,
                                produce_date: '',
                                overdue_date: '',
                                type: '0',
                                mdrfList: [{ value: item.mdrf_inner_sn, label: item.mdrf_sn, title: item.mdrf_sn }],
                                register_inner_sn: item.mdrf_inner_sn,
                                register_sn: item.mdrf_sn,
                                status: 0,
                                warehouseQuantity: item.quantity
                            };
                        });
                        this.venPage.total = res.total;
                        this.venPage.loading = false;
                        this.$refs.venTable.refreshColumn();
                        this.tableFocusFn();
                    } else {
                        this.$message.error(res.msg);
                        this.venPage.loading = false;
                    }
                })
                .catch(() => {
                    this.venPage.loading = false;
                });
        },
        // 【获取厂商产品列表】
        async grnVendorListAPIFn() {
            const otype = typeof this.partB.otype !== 'undefined' ? this.partB.otype : this.partB.billsType;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                organization_inner_sn: this.partB.oid || this.partB.org_id || this.partB.partB_id,
                name: this.searchDataVen.name,
                specification: this.searchDataVen.specification,
                vendor_inner_sn: this.searchDataVen.vendor_inner_sn,
                specification_vendor_sn: this.searchDataVen.specification_vendor_sn,
                type_inner_sn: this.searchDataVen.type_inner_sn.length
                    ? this.searchDataVen.type_inner_sn[this.searchDataVen.type_inner_sn.length - 1]
                    : '',
                code68_sn: this.searchDataVen.code68_sn.length
                    ? this.searchDataVen.code68_sn[this.searchDataVen.code68_sn.length - 1]
                    : '',
                billsType: otype,
                onlyContrctProduct: this.isContractOrgChecked,
                pageSize: this.venPage.pageSize,
                pageNum: this.venPage.current
            };
            if (otype === 1 || otype === 5 || otype === 3) {
                // 入库单
                params.vendor_inner_sn = this.partB.oid;
                params.vendor_name = '';
            }

            if (
                ((this.userSystemType === 1 && otype === this.billsTypeObj[3]) ||
                    (this.userSystemType === 2 && otype === this.billsTypeObj[2])) &&
                !this.searchDataVen.vendor_inner_sn &&
                !this.searchDataVen.vendor_name
            ) {
                // 如果相关机构是医院不传这两个值
                params.vendor_inner_sn = '';
                params.vendor_name = '';
            }

            // 经销商登录(userSystemType=2)，相关机构为厂商（otype=partB.billstye='DV'）
            // if (this.userSystemType === 2 && otype === this.billsTypeObj[1]) {
            //     // 添加产品时只显示相关机构厂商的产品，不论合同内外
            //     params.vendor_inner_sn = this.partB.partB_id
            // }
            await grnVendorListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.venTableData = rows.map((item) => {
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];

                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat: item.id,
                                // keyFormat: item.vendor_inner_sn + '' + item.product_inner_sn + item.specification_inner_sn + item.mdrf_inner_sn + item.product_type,
                                product_name: item.name || item.product_name,
                                name: item.name || item.product_name,
                                vendor_name: item.dealer_name || item.vendor_name,
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                batch_number: '',
                                product_type: 1,
                                quantity: 1,
                                unit_price: 1,
                                money: 2,
                                produce_date: '',
                                overdue_date: '',
                                type: '0',
                                mdrfList: [{ value: item.mdrf_inner_sn, label: item.mdrf_sn, title: item.mdrf_sn }],
                                register_inner_sn: item.mdrf_inner_sn,
                                register_sn: item.mdrf_sn,
                                status: 0
                            };
                        });
                        this.venPage.total = res.total;
                        this.venPage.loading = false;
                        this.$refs.venTable.refreshColumn();
                        this.tableFocusFn();
                    } else {
                        this.$message.error(res.msg);
                        this.venPage.loading = false;
                    }
                })
                .catch(() => {
                    this.venPage.loading = false;
                });
        },
        // 【获取厂商产品列表】
        async receiptVendorListAPIFn() {
            const otype = typeof this.partB.otype !== 'undefined' ? this.partB.otype : this.partB.billsType;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                organization_inner_sn: this.partB.oid || this.partB.org_id,
                name: this.searchDataVen.name,
                specification: this.searchDataVen.specification,
                vendor_inner_sn: this.searchDataVen.vendor_inner_sn,
                specification_vendor_sn: this.searchDataVen.specification_vendor_sn,
                type_inner_sn: this.searchDataVen.type_inner_sn.length
                    ? this.searchDataVen.type_inner_sn[this.searchDataVen.type_inner_sn.length - 1]
                    : '',
                code68_sn: this.searchDataVen.code68_sn.length
                    ? this.searchDataVen.code68_sn[this.searchDataVen.code68_sn.length - 1]
                    : '',
                billsType: otype,
                onlyContrctProduct: this.isContractOrgChecked,
                pageSize: this.venPage.pageSize,
                pageNum: this.venPage.current
            };
            if (otype === 1 || otype === 5 || otype === 3) {
                // 入库单
                params.vendor_inner_sn = this.partB.oid;
                params.vendor_name = '';
            }

            await receiptVendorListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.venTableData = rows.map((item) => {
                            const isUnitFlag = item.common_use_unit && item.common_use_unit_inner_sn;
                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                // keyFormat: item.vendor_inner_sn + '' + item.product_inner_sn + item.specification_inner_sn + item.mdrf_inner_sn + item.product_type,
                                // 没有唯一主键字段返回，需自行拼接组成主键keyFormat
                                keyFormat:
                                    item.vendor_inner_sn +
                                    '' +
                                    item.product_inner_sn +
                                    item.specification_inner_sn +
                                    item.mdrf_sn +
                                    item.product_type +
                                    item.specification,
                                product_name: item.name || item.product_name,
                                name: item.name || item.product_name,
                                vendor_name: item.dealer_name || item.vendor_name,
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                unit_inner_sn: isUnitFlag ? [item.common_use_unit_inner_sn] : [],
                                unit_name: isUnitFlag ? item.common_use_unit : '',
                                unitList: isUnitFlag
                                    ? [{ label: item.common_use_unit, value: item.common_use_unit_inner_sn }]
                                    : [],
                                batch_number: '',
                                product_type: 1,
                                quantity: 1,
                                unit_price: 1,
                                money: 2,
                                produce_date: '',
                                overdue_date: '',
                                type: '0',
                                mdrfList: [{ value: item.mdrf_inner_sn, label: item.mdrf_sn, title: item.mdrf_sn }],
                                register_inner_sn: item.mdrf_inner_sn,
                                register_sn: item.mdrf_sn,
                                status: 0
                            };
                        });
                        this.venPage.total = res.total;
                        this.venPage.loading = false;
                        if (this.$refs.venTable) {
                            this.$refs.venTable.refreshColumn();
                        }
                    } else {
                        this.$message.error(res.msg);
                        this.venPage.loading = false;
                    }
                })
                .catch(() => {
                    this.venPage.loading = false;
                });
        },
        // 【获取经销商自建产品列表】
        async receiptDealerListAPIFn() {
            const otype = typeof this.partB.otype !== 'undefined' ? this.partB.otype : this.partB.billsType;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                organization_inner_sn: this.partB.oid || this.partB.org_id,
                name: this.searchDataDea.name,
                specification: this.searchDataDea.specification,
                dealer_inner_sn: this.searchDataDea.dealer_inner_sn,
                type_inner_sn: this.searchDataDea.type_inner_sn.length
                    ? this.searchDataDea.type_inner_sn[this.searchDataDea.type_inner_sn.length - 1]
                    : '',
                code68_sn: this.searchDataDea.code68_sn.length
                    ? this.searchDataDea.code68_sn[this.searchDataDea.code68_sn.length - 1]
                    : '',
                billsType: otype,
                onlyContrctProduct: this.isContractOrgChecked,
                pageSize: this.deaPage.pageSize,
                pageNum: this.deaPage.current
            };

            // if (otype === 0 || otype === 4) { // 入库单 => 经销商
            //     params.dealer_inner_sn = this.partB.oid
            //     params.dealer_name = ''
            // } else {
            //     params.dealer_inner_sn = this.searchDataDea.dealer_inner_sn
            // }
            await receiptDealerListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.deaTableData = rows.map((item) => {
                            const isUnitFlag = item.common_use_unit && item.common_use_unit_inner_sn;
                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat:
                                    item.dealer_inner_sn +
                                    '' +
                                    item.product_inner_sn +
                                    item.product_type +
                                    '' +
                                    item.specification_inner_sn,
                                product_name: item.name || item.product_name,
                                name: item.name || item.product_name,
                                vendor_name: item.dealer_name || item.vendor_name,
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                unit_inner_sn: isUnitFlag ? [item.common_use_unit_inner_sn] : [],
                                unit_name: isUnitFlag ? item.common_use_unit : '',
                                unitList: isUnitFlag
                                    ? [{ label: item.common_use_unit, value: item.common_use_unit_inner_sn }]
                                    : [],
                                product_type: 0,
                                batch_number: '',
                                quantity: 1,
                                unit_price: 1,
                                money: 2,
                                produce_date: '',
                                overdue_date: '',
                                type: '0',
                                mdrf_inner_sn: '',
                                mdrf_sn: '',
                                mdrfList: [],
                                status: 0
                            };
                        });
                        this.deaPage.total = res.total;
                        this.deaPage.loading = false;
                        if (this.$refs.deaTable) {
                            this.$refs.deaTable.refreshColumn();
                        }
                    } else {
                        this.$message.error(res.msg);
                        this.deaPage.loading = false;
                    }
                })
                .catch(() => {
                    this.deaPage.loading = false;
                });
        },
        // 【获取经销商自建产品列表】
        async grnDealerListAPIFn() {
            const otype = typeof this.partB.otype !== 'undefined' ? this.partB.otype : this.partB.billsType;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                organization_inner_sn: this.partB.oid || this.partB.org_id || this.partB.partB_id,
                name: this.searchDataDea.name,
                specification: this.searchDataDea.specification,
                dealer_inner_sn: this.searchDataDea.dealer_inner_sn,
                type_inner_sn: this.searchDataDea.type_inner_sn.length
                    ? this.searchDataDea.type_inner_sn[this.searchDataDea.type_inner_sn.length - 1]
                    : '',
                code68_sn: this.searchDataDea.code68_sn.length
                    ? this.searchDataDea.code68_sn[this.searchDataDea.code68_sn.length - 1]
                    : '',
                billsType: otype,
                onlyContrctProduct: this.isContractOrgChecked,
                pageSize: this.deaPage.pageSize,
                pageNum: this.deaPage.current
            };

            // if (otype === 0 || otype === 4) { // 入库单 => 经销商
            //     params.dealer_inner_sn = this.partB.oid
            //     params.dealer_name = ''
            // } else {
            //     params.dealer_inner_sn = this.searchDataDea.dealer_inner_sn
            // }

            // 经销商登录(userSystemType=2)，相关机构为经销商（otype=partB.billstye='DD'）
            // if (this.userSystemType === 2 && otype === this.billsTypeObj[0]) {
            //     // 添加产品时只显示相关机构经销商的产品，不论合同内外
            //     params.dealer_inner_sn = this.partB.partB_id
            // }

            if (
                ((this.userSystemType === 1 && otype === this.billsTypeObj[3]) ||
                    (this.userSystemType === 2 &&
                        otype === this.billsTypeObj[2] &&
                        this.searchDataDea.dealer_inner_sn !== parseInt(this.partB.org_id))) &&
                !this.searchDataDea.dealer_inner_sn &&
                !this.searchDataDea.dealer_name
            ) {
                // 如果相关机构是医院不传这两个值
                params.dealer_inner_sn = '';
                params.dealer_name = '';
            }

            await grnDealerListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.deaTableData = rows.map((item) => {
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];

                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat: item.id,
                                // keyFormat: item.dealer_inner_sn + '' + item.product_inner_sn + item.product_type + '' + item.specification_inner_sn,
                                product_name: item.name || item.product_name,
                                name: item.name || item.product_name,
                                vendor_name: item.dealer_name || item.vendor_name,
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                product_type: 0,
                                batch_number: '',
                                quantity: 1,
                                unit_price: 1,
                                money: 2,
                                produce_date: '',
                                overdue_date: '',
                                type: '0',
                                mdrf_inner_sn: '',
                                mdrf_sn: '',
                                mdrfList: [],
                                status: 0
                            };
                        });
                        this.deaPage.total = res.total;
                        this.deaPage.loading = false;
                        this.$refs.deaTable.refreshColumn();
                        this.tableFocusFn();
                    } else {
                        this.$message.error(res.msg);
                        this.deaPage.loading = false;
                    }
                })
                .catch(() => {
                    this.deaPage.loading = false;
                });
        },
        // 【获取验货单经销商自建产品列表】
        async insDealerListAPIFn() {
            let params = {
                name: this.searchDataDea.name,
                dealer_inner_sn: this.partB.oid,
                code68_sn: this.searchDataDea.code68_sn.length
                    ? this.searchDataDea.code68_sn[this.searchDataDea.code68_sn.length - 1]
                    : '',
                pageSize: this.deaPage.pageSize,
                pageNum: this.deaPage.current
            };
            this.searchDataDea.dealer_name = this.partB.oname;
            await insDealerListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.deaTableData = rows.map((item) => {
                            const isUnitFlag = item.common_use_unit && item.common_use_unit_inner_sn;
                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat:
                                    item.dealer_inner_sn +
                                    '' +
                                    item.product_inner_sn +
                                    item.product_type +
                                    '' +
                                    item.specification_inner_sn,
                                product_name: item.name || item.product_name,
                                name: item.name || item.product_name,
                                vendor_name: item.dealer_name || item.vendor_name,
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                unit_inner_sn: isUnitFlag ? [item.common_use_unit_inner_sn] : [],
                                unit_name: isUnitFlag ? item.common_use_unit : '',
                                unitList: isUnitFlag
                                    ? [{ label: item.common_use_unit, value: item.common_use_unit_inner_sn }]
                                    : [],
                                product_type: 0,
                                batch_number: '',
                                quantity: 1,
                                unit_price: 1,
                                money: 2,
                                produce_date: '',
                                overdue_date: '',
                                type: '0',
                                mdrf_inner_sn: '',
                                mdrf_sn: '',
                                mdrfList: [],
                                status: 0
                            };
                        });
                        this.deaPage.total = res.total;
                        this.deaPage.loading = false;
                        this.$refs.deaTable.refreshColumn();
                    } else {
                        this.$message.error(res.msg);
                        this.deaPage.loading = false;
                    }
                })
                .catch(() => {
                    this.deaPage.loading = false;
                });
        },
        // 【厂商】产品列表
        async planVendorListAPIFn() {
            let params = {
                product_name: this.searchDataVen.name,
                product_vendor_id: this.searchDataVen.vendor_inner_sn,
                specification_vendor_sn: this.searchDataVen.specification_vendor_sn,
                specification: this.searchDataVen.specification,
                type_inner_sn: this.searchDataVen.type_inner_sn.length
                    ? this.searchDataVen.type_inner_sn[this.searchDataVen.type_inner_sn.length - 1]
                    : '',
                code68_sn: this.searchDataVen.code68_sn.length
                    ? this.searchDataVen.code68_sn[this.searchDataVen.code68_sn.length - 1]
                    : '',
                pageSize: this.venPage.pageSize,
                pageNum: this.venPage.current
            };

            await planVendorListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let { rows } = res;
                        this.venTableData = rows.map((item) => {
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];

                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat:
                                    item.vendor_inner_sn +
                                    '' +
                                    item.product_inner_sn +
                                    item.product_type +
                                    '' +
                                    item.specification_inner_sn +
                                    item.vendor_name,
                                mdrf_sn: item.mdrf_sn || item.register_sn,
                                mdrf_inner_sn: item.mdrf_inner_sn || item.register_inner_sn,
                                product_name: item.name || item.product_name,
                                product_type: 1,
                                quantity: 1,
                                status: 0
                            };
                        });
                        this.venPage.total = res.total;
                        this.venPage.loading = false;
                        this.$refs.venTable.refreshColumn();
                    } else {
                        this.$message.error(res.msg);
                        this.venPage.loading = false;
                    }
                })
                .catch(() => {
                    this.venPage.loading = false;
                });
        },
        // 【经销商】自建产品列表
        async planDealerListAPIFn() {
            let params = {
                product_name: this.searchDataDea.name,
                product_vendor_id: this.searchDataDea.dealer_inner_sn,
                type_inner_sn: this.searchDataDea.type_inner_sn.length
                    ? this.searchDataDea.type_inner_sn[this.searchDataDea.type_inner_sn.length - 1]
                    : '',
                code68_sn: this.searchDataDea.code68_sn.length
                    ? this.searchDataDea.code68_sn[this.searchDataDea.code68_sn.length - 1]
                    : '',
                pageSize: this.deaPage.pageSize,
                pageNum: this.deaPage.current
            };

            await planDealerListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.deaTableData = rows.map((item) => {
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];

                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat:
                                    item.dealer_inner_sn +
                                    '' +
                                    item.product_inner_sn +
                                    item.product_type +
                                    '' +
                                    item.specification_inner_sn,
                                product_name: item.name || item.product_name,
                                vendor_name: item.dealer_name,
                                product_type: 0,
                                quantity: 1,
                                status: 0,
                                specification: item.specification_name || item.specification
                            };
                        });
                        this.deaPage.total = res.total;
                        this.deaPage.loading = false;
                        this.$refs.deaTable.refreshColumn();
                    } else {
                        this.$message.error(res.msg);
                        this.deaPage.loading = false;
                    }
                })
                .catch(() => {
                    this.deaPage.loading = false;
                });
        },
        // 【厂商】产品列表
        async orderVendorListAPIFn() {
            let SalesFlag = this.partB.isProcurementSalesFlag;
            let params = {
                org_id: SalesFlag ? this.partB.partB_id : this.partB.partA_id,
                organization_inner_sn: SalesFlag ? this.partB.partA_id : this.partB.partB_id,
                isPurchase: !SalesFlag,
                name: this.searchDataVen.name,
                specification: this.searchDataVen.specification,
                vendor_inner_sn: this.searchDataVen.vendor_inner_sn,
                specification_vendor_sn: this.searchDataVen.specification_vendor_sn,
                type_inner_sn: this.searchDataVen.type_inner_sn.length
                    ? this.searchDataVen.type_inner_sn[this.searchDataVen.type_inner_sn.length - 1]
                    : '',
                code68_sn: this.searchDataVen.code68_sn.length
                    ? this.searchDataVen.code68_sn[this.searchDataVen.code68_sn.length - 1]
                    : '',
                billsType: this.partB.billsType,
                onlyContrctProduct: this.isContractOrgChecked,
                pageSize: this.venPage.pageSize,
                pageNum: this.venPage.current
            };

            // 采购订单，相关机构为厂商
            // if (!SalesFlag && (this.partB.billsType === 'DV' || this.partB.billsType === 'HV')) {
            //     params.vendor_inner_sn = this.partB.partB_id
            // }

            await orderVendorListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let { rows } = res;
                        this.venTableData = rows.map((item) => {
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];

                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat: !isNaN(item.vendor_inner_sn)
                                    ? item.vendor_inner_sn +
                                      '' +
                                      item.product_inner_sn +
                                      1 +
                                      item.specification_inner_sn
                                    : item.vendor_b_inner_sn + '' + item.product_inner_sn + 1,
                                product_name: item.product_name,
                                partB_name: item.vendor_name || item.dealer_name,
                                mdrf_sn: item.mdrf_sn,
                                product_type: 1,
                                status: 0,
                                quantity_delivered: '',
                                unit_price: 1,
                                money: 1,
                                discount: 1,
                                discount_unit_price: 1,
                                discount_money: 1,
                                rate: 0,
                                tax_unit_price: 1,
                                tax_amount: 1,
                                tax: 1,
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                quantity: 1
                            };
                        });
                        this.venPage.total = res.total;
                        this.venPage.loading = false;
                        this.$refs.venTable.refreshColumn();
                        this.tableFocusFn();
                    } else {
                        this.$message.error(res.msg);
                        this.venPage.loading = false;
                    }
                })
                .catch(() => {
                    this.venPage.loading = false;
                });
        },
        // 【经销商】自建产品列表
        async orderDealerListAPIFn() {
            let SalesFlag = this.partB.isProcurementSalesFlag;
            let params = {
                org_id: SalesFlag ? this.partB.partB_id : this.partB.partA_id,
                organization_inner_sn: SalesFlag ? this.partB.partA_id : this.partB.partB_id,
                isPurchase: !SalesFlag,
                name: this.searchDataDea.name,
                specification: this.searchDataDea.specification,
                // dealer_inner_sn: this.searchDataDea.dealer_inner_sn,
                type_inner_sn: this.searchDataDea.type_inner_sn.length
                    ? this.searchDataDea.type_inner_sn[this.searchDataDea.type_inner_sn.length - 1]
                    : '',
                code68_sn: this.searchDataDea.code68_sn.length
                    ? this.searchDataDea.code68_sn[this.searchDataDea.code68_sn.length - 1]
                    : '',
                billsType: this.partB.billsType,
                onlyContrctProduct: this.isContractOrgChecked,
                pageSize: this.deaPage.pageSize,
                pageNum: this.deaPage.current
            };

            // const otype = this.partB.billsType
            // if (this.partB.modalType === 4 && otype === 0) { // 采购单 => 经销商
            //     params.product_vendor_id = this.partB.partB_id
            //     // params.product_vendor_id = this.partB.isProcurementSalesFlag ? this.partB.partA_id : this.partB.partB_id
            //     params.dealer_name = ''
            // } else {
            //     params.product_vendor_id = this.searchDataDea.dealer_inner_sn
            // }

            // 采购订单，相关机构为经销商
            // if (!SalesFlag && (this.partB.billsType === 'DD' || this.partB.billsType === 'HD')) {
            //     params.dealer_inner_sn = this.partB.partB_id
            // }
            // 销售订单，相关机构为经销商
            if (SalesFlag && (this.partB.billsType === 'DD' || this.partB.billsType === 'DH')) {
                params.dealer_inner_sn = '';
            }

            await orderDealerListAPI({
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.deaTableData = rows.map((item) => {
                            const isDealerInnerSn = typeof item.dealer_inner_sn !== 'undefined';
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];

                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat: isDealerInnerSn
                                    ? item.dealer_inner_sn +
                                      '' +
                                      item.product_inner_sn +
                                      0 +
                                      item.specification_inner_sn
                                    : item.dealer_b_inner_sn +
                                      '' +
                                      item.product_inner_sn +
                                      0 +
                                      item.specification_inner_sn,
                                product_name: item.name || item.product_name,
                                partB_name: item.vendor_name || item.dealer_name,
                                vendor_name: item.dealer_name,
                                vendor_inner_sn: isDealerInnerSn ? item.dealer_inner_sn : item.dealer_b_inner_sn,
                                product_type: 0,
                                status: 0,
                                quantity_delivered: '-',
                                unit_price: 1,
                                money: 1,
                                discount: 1,
                                discount_unit_price: 1,
                                discount_money: 1,
                                rate: 0,
                                tax_unit_price: 1,
                                tax_amount: 1,
                                tax: 1,
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                quantity: 1
                            };
                        });
                        this.deaPage.total = res.total;
                        this.deaPage.loading = false;
                        this.$refs.deaTable.refreshColumn();
                        this.tableFocusFn();
                    } else {
                        this.$message.error(res.msg);
                        this.deaPage.loading = false;
                    }
                })
                .catch(() => {
                    this.deaPage.loading = false;
                });
        },
        // 【获取】有效合同内产品列表(分页)
        async hospitalApplyProductListAPIFn(type) {
            let productType = -1, // 产品类型 0:经销商(自建)产品 1:厂商产品
                params = {};
            if (type === 2) {
                // 经销商自建产品
                params = {
                    name: this.searchDataDea.name,
                    vendor_inner_sn: this.searchDataDea.dealer_inner_sn,
                    type_inner_sn:
                        this.searchDataDea.type_inner_sn && this.searchDataDea.type_inner_sn.length
                            ? this.searchDataDea.type_inner_sn[this.searchDataDea.type_inner_sn.length - 1]
                            : '',
                    code68_sn:
                        this.searchDataDea.code68_sn && this.searchDataDea.code68_sn.length
                            ? this.searchDataDea.code68_sn[this.searchDataDea.code68_sn.length - 1]
                            : ''
                };
                params.pageSize = this.deaPage.pageSize;
                params.pageNum = this.deaPage.current;
                productType = 0;
            } else if (type === 3) {
                // 厂商
                params = {
                    name: this.searchDataVen.name,
                    vendor_inner_sn: this.searchDataVen.vendor_inner_sn,
                    specification: this.searchDataVen.specification,
                    specification_vendor_sn: this.searchDataVen.specification_vendor_sn,
                    type_inner_sn:
                        this.searchDataVen.type_inner_sn && this.searchDataVen.type_inner_sn.length
                            ? this.searchDataVen.type_inner_sn[this.searchDataVen.type_inner_sn.length - 1]
                            : '',
                    code68_sn:
                        this.searchDataVen.code68_sn && this.searchDataVen.code68_sn.length
                            ? this.searchDataVen.code68_sn[this.searchDataVen.code68_sn.length - 1]
                            : ''
                };
                params.pageSize = this.venPage.pageSize;
                params.pageNum = this.venPage.current;
                productType = 1;
            }
            await hospitalApplyProductListAPI(this.partB.id, productType, {
                params
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        if (type === 2) {
                            this.deaTableData = rows.map((item) => {
                                item.isActive = '';
                                item.key = Math.random();
                                // item.keyFormat = item.vendor_inner_sn + '' + item.product_inner_sn + item.unit_inner_sn
                                item.keyFormat = item.id;
                                item.plan_quantity = 1;
                                item.unit_name = item.min_unit;
                                item.unit_inner_sn = item.min_unit_inner_sn;
                                item.unitList =
                                    item.unit_name && item.unit_inner_sn
                                        ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                        : [];
                                item.unit_inner_sn = [item.unit_inner_sn];
                                // item.product_type = 1
                                // item.mdrf_sn = item.register_sn // 后台接口字段改了
                                item.vendor_name = item.vendor_name || item.dealer_name;
                                item.sys_type = parseFloat(item.product_type) === 0 ? '经销商' : '厂商';
                                item.loading = false;
                                item.status = 0;
                                item.switch = parseFloat(item.status) === 0;
                                return item;
                            });
                            this.deaPage.total = res.total;
                            this.deaPage.loading = false;
                            this.$refs.deaTable.refreshColumn();
                            this.$refs.venTable.updateData();
                        } else if (type === 3) {
                            this.venTableData = rows.map((item) => {
                                item.isActive = '';
                                item.key = Math.random();
                                // item.keyFormat = item.vendor_inner_sn + '' + item.product_inner_sn + item.unit_inner_sn
                                item.keyFormat = item.id;
                                item.plan_quantity = 1;
                                item.unit_name = item.min_unit;
                                item.unit_inner_sn = item.min_unit_inner_sn;
                                item.unitList =
                                    item.unit_name && item.unit_inner_sn
                                        ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                        : [];
                                item.unit_inner_sn = [item.unit_inner_sn];
                                // item.product_type = 0
                                // item.mdrf_sn = item.register_sn || '' // 后台接口字段改了
                                item.vendor_name = item.vendor_name || item.dealer_name;
                                item.sys_type = parseFloat(item.product_type) === 0 ? '经销商' : '厂商';
                                item.loading = false;
                                item.status = 0;
                                item.switch = parseFloat(item.status) === 0;
                                return item;
                            });
                            this.venPage.total = res.total;
                            this.venPage.loading = false;
                            this.$refs.venTable.refreshColumn();
                        }
                    } else {
                        this.$message.error(res.msg);
                        this.deaPage.loading = false;
                        this.venPage.loading = false;
                    }
                })
                .catch(() => {
                    this.deaPage.loading = false;
                    this.venPage.loading = false;
                });
        },
        // 【代储代配】入库、出库通知产品列表(分页)
        async grnAndOdoNoticeProductListAPIFn() {
            const data = {
                'code68_sn': this.searchDataVen.code68_sn.length
                    ? this.searchDataVen.code68_sn[this.searchDataVen.code68_sn.length - 1]
                    : '',
                'dealer_inner_sn': this.warehouseLeasingDealerInnerSn,
                'prodict_vendor_inner_sn': this.searchDataVen.vendor_inner_sn,
                'product_name': this.searchDataVen.name,
                'specification': this.searchDataVen.specification,
                'specification_vendor_sn': this.searchDataVen.specification_vendor_sn,
                'type_inner_sn': this.searchDataVen.type_inner_sn.length
                    ? this.searchDataVen.type_inner_sn[this.searchDataVen.type_inner_sn.length - 1]
                    : ''
            };
            let loader;
            if (this.warehouseLeasingNoticeType === 'grn') {
                loader = grnNoticeProductListAPI;
            }
            if (this.warehouseLeasingNoticeType === 'odo') {
                loader = odoNoticeProductListAPI;
            }

            if (!loader) {
                throw new Error('未定义出入库类型');
            }

            await loader(this.venPage.current, this.venPage.pageSize, data)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { rows } = res;
                        this.venTableData = rows.map((item) => {
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];

                            return {
                                ...item,
                                activeClass: '',
                                key: Math.random(),
                                keyFormat:
                                    item.vendor_inner_sn +
                                    '' +
                                    item.product_inner_sn +
                                    item.specification_inner_sn +
                                    item.mdrf_inner_sn +
                                    item.product_type,
                                product_name: item.name || item.product_name,
                                name: item.name || item.product_name,
                                vendor_name: item.dealer_name || item.vendor_name,
                                sys_type: parseFloat(item.product_type) === 0 ? '经销商' : '厂商',
                                batch_number: '',
                                product_type: 1,
                                quantity: 1,
                                unit_price: 1,
                                money: 2,
                                produce_date: '',
                                overdue_date: '',
                                type: '0',
                                mdrfList: [{ value: item.mdrf_inner_sn, label: item.mdrf_sn, title: item.mdrf_sn }],
                                register_inner_sn: item.mdrf_inner_sn,
                                register_sn: item.mdrf_sn,
                                status: 0,
                                warehouseQuantity: item.quantity
                            };
                        });
                        this.venPage.total = res.total;
                        this.venPage.loading = false;
                        this.$refs.venTable.refreshColumn();
                        this.tableFocusFn();
                    } else {
                        this.$message.error(res.msg);
                        this.venPage.loading = false;
                    }
                })
                .catch(() => {
                    this.venPage.loading = false;
                });
        },
        async loadTable(type) {
            let systemType = this.partB.systemType;
            // let oid = parseInt(this.$cookie.get('userbelong'))
            let selfOrgID = parseInt(this.$cookie.get('userbelong'));
            let params = {};
            if (type === 2) {
                // 经销商自建产品
                params = JSON.parse(JSON.stringify(this.searchDataDea));
                params.dealer_inner_sn = this.partB.id;
                params.pageSize = this.deaPage.pageSize;
                params.pageNum = this.deaPage.current;
                params.type_inner_sn = this.searchDataDea.type_inner_sn.length
                    ? this.searchDataDea.type_inner_sn[this.searchDataDea.type_inner_sn.length - 1]
                    : '';
                params.code68_sn = this.searchDataDea.code68_sn.length
                    ? this.searchDataDea.code68_sn[this.searchDataDea.code68_sn.length - 1]
                    : '';
                params.org_id = selfOrgID;
                let oid = params.dealer_inner_sn;
                await productDealerListContractAPI(systemType, oid, { params }).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.deaTableData = res.rows.map((item) => {
                            if (!item.name) {
                                item.name = item.product_name;
                            }
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];
                            item.isActive = '';
                            item.product_type = typeof item.product_type === 'undefined' ? 0 : item.product_type;
                            item.key = Math.random();
                            item.keyFormat =
                                item.vendor_inner_sn + '' + item.product_inner_sn + item.specification_inner_sn;
                            return item;
                        });
                        this.deaPage.total = res.total;
                        this.deaPage.loading = false;
                        this.venPage.loading = false;
                        this.$refs.deaTable.refreshColumn();
                        this.tableFocusFn();
                    }
                });
            } else if (type === 3) {
                // 厂商
                params = JSON.parse(JSON.stringify(this.searchDataVen));
                params.pageSize = this.venPage.pageSize;
                params.pageNum = this.venPage.current;
                params.specification = this.searchDataVen.specification;
                params.type_inner_sn = this.searchDataVen.type_inner_sn.length
                    ? this.searchDataVen.type_inner_sn[this.searchDataVen.type_inner_sn.length - 1]
                    : '';
                params.code68_sn = this.searchDataVen.code68_sn.length
                    ? this.searchDataVen.code68_sn[this.searchDataVen.code68_sn.length - 1]
                    : '';
                params.org_id = selfOrgID;
                let oid = this.partB.id;
                await productVendorListContractAPI(systemType, oid, { params }).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.venTableData = res.rows.map((item) => {
                            item.unit_name = item.min_unit;
                            item.unit_inner_sn = item.min_unit_inner_sn;
                            item.unitList =
                                item.unit_name && item.unit_inner_sn
                                    ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                    : [];
                            item.unit_inner_sn = [item.unit_inner_sn];
                            item.name = item.product_name;
                            item.isActive = '';
                            item.product_type = typeof item.product_type === 'undefined' ? 1 : item.product_type;
                            item.key = Math.random();
                            item.keyFormat =
                                item.vendor_inner_sn + '' + item.product_inner_sn + item.specification_inner_sn;
                            return item;
                        });
                        this.venPage.total = res.total;
                        this.venPage.loading = false;
                        this.deaPage.loading = false;
                        this.$refs.venTable.refreshColumn();
                        this.tableFocusFn();
                    }
                });
            }
        },
        async loadAllPro() {
            // 所有产品
            let params = {};
            params = JSON.parse(JSON.stringify(this.searchDataVen));
            params.org_id = parseInt(this.$cookie.get('userbelong'));
            if (this.partB.modalType === 2) {
                // 期初库存
                params.vendor_inner_sn = this.searchDataVen.vendor_inner_sn;
            }
            const otype = parseFloat(this.partB.otype);
            if (this.partB.modalType === 7 && (otype === this.billsTypeObj[1] || otype === this.billsTypeObj[5])) {
                // 入库单
                params.vendor_inner_sn = this.partB.oid;
                params.vendor_name = '';
            } else if (this.partB.modalType === 9 && (otype === 1 || otype === 5)) {
                // 验货单
                params.vendor_inner_sn = this.partB.oid;
                params.vendor_name = '';
            } else {
                params.vendor_inner_sn = this.searchDataVen.vendor_inner_sn;
            }
            params.specification = this.searchDataVen.specification;
            params.pageSize = this.venPage.pageSize;
            params.pageNum = this.venPage.current;
            params.type_inner_sn = this.searchDataVen.type_inner_sn.length
                ? this.searchDataVen.type_inner_sn[this.searchDataVen.type_inner_sn.length - 1]
                : '';
            params.code68_sn = this.searchDataVen.code68_sn.length
                ? this.searchDataVen.code68_sn[this.searchDataVen.code68_sn.length - 1]
                : '';
            await getVendorProductListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
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
                            status: 0
                        };
                    });
                    this.venPage.total = res.total;
                    this.venPage.loading = false;
                    this.$refs.venTable.refreshColumn();
                }
            });
        },
        async loadCertPro() {
            let params = {};
            params = JSON.parse(JSON.stringify(this.searchDataVen));
            this.isVendor = true;
            this.searchDataVen.vendor_name = this.partB.name;
            params.vid = this.partB.id;
            params.pageSize = this.venPage.pageSize;
            params.pageNum = this.venPage.current;
            await getVendorProductDropdownAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.venTableData = res.rows.map((item) => {
                        return {
                            ...item,
                            isActive: '',
                            activeClass: '',
                            key: Math.random(),
                            keyFormat: item.vendor_inner_sn + '' + item.product_inner_sn,
                            // product_name: item.name || item.product_name,
                            name: item.product_name,
                            product_type: 1, // 0 - '经销商'  1 - '厂商'
                            mdrf_sn: item.mdrf_sn
                        };
                    });
                    this.venPage.total = res.total;
                    this.venPage.loading = false;
                    this.$refs.venTable.refreshColumn();
                }
            });
        },
        // onSelectChangeProduct ({ selection, seq, data }, event) {
        //     const isChecked = event.target.checked
        //     const itemIndex = seq - 1
        //     const item = data[itemIndex]
        //     const itemKey = item.keyFormat

        //     switch (this.activeTab) {
        //     // 厂商产品
        //     case 'ven':
        //         if (isChecked) {
        //             this.selectedRowsVen.push(item)
        //         } else {
        //             this.selectedRowsVen.forEach((row, idx) => {
        //                 if (row.keyFormat === itemKey) {
        //                     this.selectedRowsVen.splice(idx, 1)
        //                 }
        //             })
        //         }
        //         break
        //         // 经销商自建产品
        //     case 'dea':
        //         if (isChecked) {
        //             this.selectedRowsDea.push(item)
        //         } else {
        //             this.selectedRowsDea.forEach((row, idx) => {
        //                 if (row.keyFormat === itemKey) {
        //                     this.selectedRowsDea.splice(idx, 1)
        //                 }
        //             })
        //         }
        //         break

        //     default: break
        //     }

        //     // if (this.selectedRowsDea.length === 0) {
        //     //     let newObj = {
        //     //         key: this.deaPage.current,
        //     //         data: selection
        //     //     }
        //     //     this.selectedRowsDea.push(newObj)
        //     // } else {
        //     //     let st = true
        //     //     this.selectedRowsDea.forEach(item => {
        //     //         if (item.key === this.deaPage.current) {
        //     //             item.data = selection
        //     //             st = false
        //     //         }
        //     //     })
        //     //     if (st) {
        //     //         let newObj = {
        //     //             key: this.deaPage.current,
        //     //             data: selection
        //     //         }
        //     //         this.selectedRowsDea.push(newObj)
        //     //     }
        //     // }
        // },
        // // 全选
        // onSelectAllChangeProduct ({selection, seq, data}, event) {
        //     // 全选按钮开关状态
        //     const isChecked = event.target.checked

        //     switch (this.activeTab) {
        //     // 厂商产品Tab
        //     case 'ven':
        //         if (isChecked) {
        //             // 选中全选
        //             selection.forEach(selectedRow => {
        //                 let isContain = false
        //                 this.selectedRowsVen.forEach(row => {
        //                     if (selectedRow.keyFormat === row.keyFormat) {
        //                         isContain = true
        //                     }
        //                 })
        //                 if (!isContain) {
        //                     this.selectedRowsVen.push(selectedRow)
        //                 }
        //             })
        //         } else {
        //             // 取消全选
        //             this.venTableData.forEach(canceledRow => {
        //                 let isNeedCancel = false
        //                 let cancelItemIndex
        //                 this.selectedRowsVen.forEach((row, idx) => {
        //                     if (canceledRow.keyFormat === row.keyFormat) {
        //                         isNeedCancel = true
        //                         cancelItemIndex = idx
        //                     }
        //                 })
        //                 if (isNeedCancel) {
        //                     this.selectedRowsVen.splice(cancelItemIndex, 1)
        //                 }
        //             })
        //         }
        //         break
        //         // 经销商自建产品Tab
        //     case 'dea':
        //         if (isChecked) {
        //             // 选中全选
        //             selection.forEach(selectedRow => {
        //                 let isContain = false
        //                 this.selectedRowsDea.forEach(row => {
        //                     if (selectedRow.keyFormat === row.keyFormat) {
        //                         isContain = true
        //                     }
        //                 })
        //                 if (!isContain) {
        //                     this.selectedRowsDea.push(selectedRow)
        //                 }
        //             })
        //         } else {
        //             // 取消全选
        //             this.deaTableData.forEach(canceledRow => {
        //                 let isNeedCancel = false
        //                 let cancelItemIndex
        //                 this.selectedRowsDea.forEach((row, idx) => {
        //                     if (canceledRow.keyFormat === row.keyFormat) {
        //                         isNeedCancel = true
        //                         cancelItemIndex = idx
        //                     }
        //                 })
        //                 if (isNeedCancel) {
        //                     this.selectedRowsDea.splice(cancelItemIndex, 1)
        //                 }
        //             })
        //         }
        //         break

        //     default: break
        //     }
        // },
        resetDataFn() {
            // 默认清空数据
            this.hosTableData = [];
            this.venTableData = [];
            this.deaTableData = [];
            this.deaPage = {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            };
            this.venPage = {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            };
            this.searchDataVen = {
                name: '',
                vendor_inner_sn: '', // 产商ID
                vendor_name: '', //
                specification: '',
                specification_vendor_sn: '',
                warehouse_inner_sn: '',
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [] // 医疗器械68分类编号
            };
            this.searchDataDea = {
                name: '',
                dealer_inner_sn: '', // 经销商id
                dealer_name: '',
                specification_vendor_sn: '',
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [] // 医疗器械68分类编号
            };
            this.selectAllCheckboxRecords = {
                0: [],
                1: []
            };
            this.selectedRadioRecord = undefined;
        },

        /// isChangeContractOrg: true => 说明用户在操作合同内产品单选框，不需要清空重置数据
        // 初始化
        pageDataInit(isChangeContractOrg) {
            this.searchDataVen.warehouse_inner_sn = this.partB.warehouse_inner_sn || '';

            // 获取当前有权限的仓库
            this.initWarehouseListFn();

            // 【经销商端】调用自己的自定义类型列表
            if (this.userSystemType === 2) {
                this.dealerGetParentListAPIFn();
            }

            this.getCategoryListFn();
            this.get68ParentListFn();

            if (!isChangeContractOrg) {
                this.resetDataFn();
            }

            if (this.partB.modalType === 2) {
                this.isShowDealerTabPane = true;
            }

            // 初始化从上层组件传递过来的搜索值
            // 注意：谨慎调整此行代码的前后顺序，其逻辑依赖于前边的某一行代码（具体哪行自己找，不想吃面条）
            this.searchDataDefaultFn();

            // 无用户输入的产品名，并且当前机构为经销商时，不立刻进行列表数据的加载（基于性能考虑）
            // 注意：谨慎调整此行代码的前后顺序，其逻辑依赖于上一行的searchDataDefaultFn逻辑
            const needLoadList = !(this.searchDataVen.name.length === 0 && this.userSystemType === 2);

            this.clearCheckboxRowFn();
            this.deaPage = {
                loading: needLoadList,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageSizes: [10, 20, 30, 40],
                pageNum: 1
            };
            this.venPage = {
                loading: needLoadList,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageSizes: [10, 20, 30, 40],
                pageNum: 1
            };

            this.tabName = [
                { name: '厂商产品', key: 'ven' },
                { name: '经销商自建产品', key: 'dea' }
            ];
            // 初始化判断 partType(代表机构类型)
            // this.activeTab = 'ven'
            if (!this.partB.modalType) {
                // 全部合同商品
                let systemType = parseFloat(this.partB.systemType);
                if (systemType === 2) {
                    // 经销商
                    this.isDealer = true;
                    this.isVendor = false;
                    if (needLoadList) {
                        // this.loadTable(2)
                        this.loadTable(3);
                    }
                    this.tabName = [
                        { name: '厂商产品', key: 'ven' },
                        { name: '经销商自建产品', key: 'dea' }
                    ];
                    // this.searchDataDea.dealer_name = this.partB.name
                    // this.searchDataDea.vendor_name = ''
                }
                if (systemType === 3) {
                    // 厂商
                    this.isVendor = false;
                    this.isDealer = false;
                    if (needLoadList) {
                        this.loadTable(3);
                    }
                    this.tabName = [{ name: '厂商产品', key: 'ven' }];
                    // 隐藏经销商自建产品tab
                    this.isShowDealerTabPane = true;
                }
                this.setSearchValFn();
            } else {
                switch (
                    this.partB.modalType // 1 - 合同内的产品  2 - 期初库存的产品列表  3 - 采购计划 5 - 骨科产品 6 - 询价产品 7 - 入库单 8 - 出库单 - 10 注册证复制
                ) {
                    case 1:
                        if (needLoadList) {
                            // this.hospitalApplyProductListAPIFn(2)
                            this.hospitalApplyProductListAPIFn(3);
                        }
                        break;
                    case 2:
                        this.setSearchValFn();
                        needLoadList && this.grnVendorListAPIFn();
                        // this.loadAllPro()
                        // this.initialDealerListAPIFn()
                        break;
                    case 3:
                        this.currentVendorColumns = [...this.vendorPlanColumns];
                        if (needLoadList) {
                            this.planVendorListAPIFn();
                            // this.planDealerListAPIFn()
                        }
                        break;
                    case 4:
                        this.setSearchValFn();
                        if (needLoadList) {
                            this.orderVendorListAPIFn();
                            // this.orderDealerListAPIFn()
                        }
                        break;
                    case 5:
                        this.setSearchValFn();
                        needLoadList && this.loadAllPro();
                        break;
                    case 6:
                        this.setSearchValFn();
                        needLoadList && this.loadAllPro('inq');
                        break;
                    case 7:
                        this.setSearchValFn();
                        if (needLoadList) {
                            this.grnVendorListAPIFn();
                            // this.grnDealerListAPIFn()
                        }
                        break;
                    case 8:
                        {
                            let tempColumns = [...this.defaultVendorColumns];
                            tempColumns.splice(2, 0, { ...this.warehouseColumn });
                            tempColumns[3] = { ...tempColumns[3] };
                            tempColumns[3].minWidth = 100;
                            this.currentVendorColumns = tempColumns;
                            this.setSearchValFn();
                            if (needLoadList) {
                                this.odoVendorListAPIFn();
                                // this.odoDealerListAPIFn()
                            }
                        }
                        break;
                    case 9:
                        this.setSearchValFn();
                        if (needLoadList) {
                            this.loadAllPro();
                            // this.insDealerListAPIFn()
                        }
                        break;
                    case 10:
                        this.currentVendorColumns = [...this.vendorCertColumns];
                        needLoadList && this.loadCertPro();
                        break;
                    case 11:
                        this.setSearchValFn();
                        if (needLoadList) {
                            this.receiptVendorListAPIFn();
                            // this.receiptDealerListAPIFn()
                        }
                        break;
                    // 代储代配
                    case 12:
                        this.setSearchValFn();
                        if (needLoadList) {
                            this.grnAndOdoNoticeProductListAPIFn();
                        }
                }

                // 如果是厂商登录，则只显示厂商产品tab
                this.tabName =
                    this.userSystemType === 3
                        ? [{ name: '厂商产品', key: 'ven' }]
                        : [
                              { name: '厂商产品', key: 'ven' },
                              { name: '经销商自建产品', key: 'dea' }
                          ];

                const grnType = this.partB.otype || this.partB.billsType;
                const isShowDealerTab = grnType === this.billsTypeObj[1] || grnType === this.billsTypeObj[5]; // 入库单的相关机构类型是厂商隐藏经销商自建产品tab

                if (
                    this.partB.modalType === 2 ||
                    (this.partB.modalType === 4 && grnType === this.billsTypeObj[3]) ||
                    (this.partB.modalType === 4 && grnType === this.billsTypeObj[6]) || // VD - 销售/采购
                    (this.partB.modalType === 4 && grnType === this.billsTypeObj[7]) || // VH - 销售/采购
                    (this.partB.modalType === 4 && grnType === this.billsTypeObj[8]) || // VV - 销售/采购
                    (this.partB.modalType === 4 && isShowDealerTab) ||
                    this.partB.modalType === 5 ||
                    this.partB.modalType === 6 ||
                    (this.partB.modalType === 7 && isShowDealerTab) ||
                    (this.partB.modalType === 7 && grnType === this.billsTypeObj[6]) || // VD - 入库
                    (this.partB.modalType === 7 && grnType === this.billsTypeObj[7]) || // VH - 入库
                    (this.partB.modalType === 7 && grnType === this.billsTypeObj[8]) || // VV - 入库
                    (this.partB.modalType === 11 && (grnType === 3 || grnType === 1)) ||
                    (this.partB.modalType === 8 && grnType === this.billsTypeObj[1]) || // DV
                    (this.partB.modalType === 8 && grnType === this.billsTypeObj[6]) || // VD - 出库
                    (this.partB.modalType === 8 && grnType === this.billsTypeObj[7]) || // VH - 出库
                    (this.partB.modalType === 8 && grnType === this.billsTypeObj[8]) || // VV - 出库
                    (this.partB.modalType === 9 && isShowDealerTab) ||
                    this.partB.modalType === 10 ||
                    this.partB.modalType === 12
                ) {
                    this.tabName = [{ name: '厂商产品', key: 'ven' }];
                    this.isShowDealerTabPane = true;
                }
                // if (this.partB.modalType === 9 && isShowDealerTab) {
                //     this.tabName = [{name: '厂商产品', key: 'ven'}]
                // }
            }
            if (!isChangeContractOrg) {
                this.activeTab = this.tabName[0].key;
            }
            // !(this.partB.modalType === 2 || this.partB.modalType === 3 || this.partB.modalType === 4 || this.partB.modalType === 7) && this.getVendorListFn()
        },
        // 清除经销商/厂商名称
        clearSearchDataVenFn(inputName) {
            if (inputName === 'vendor') {
                this.searchDataVen.name = '';
                this.searchDataVen.vendor_inner_sn = '';
                this.searchDataVen.vendor_name = '';
                this.searchDataVen.specification = '';
                this.searchDataVen.specification_vendor_sn = '';
                this.searchDataVen.type_inner_sn = '';
                this.searchDataVen.warehouse_inner_sn = '';
                this.searchDataVen.code68_sn = '';
            } else if (inputName === 'dealer') {
                this.searchDataDea.name = '';
                this.searchDataDea.dealer_inner_sn = '';
                this.searchDataDea.dealer_name = '';
                this.searchDataDea.specification_vendor_sn = '';
                this.searchDataDea.type_inner_sn = '';
                this.searchDataDea.code68_sn = '';
            }
        },
        // 默认值赋值
        setSearchValFn() {
            const otype = typeof this.partB.otype !== 'undefined' ? this.partB.otype : this.partB.billsType;
            const billsTypeObj = this.billsTypeObj;
            const modalType = this.partB.modalType;
            const userSystemType = this.userSystemType;
            const partBSystemType = this.partB.systemType;

            let isReadonlyDealerName = false; // 只显示经销商自建产品的名称，不隐藏经销商自建产品的tab选项卡
            this.isShowDealerTabPane = false; // 每次重置经销商选项卡

            this.clearCheckboxRowFn();

            // 正式版本控制
            // if ((modalType === 6 || modalType === 7 || modalType === 8 || modalType === 11) && otype === billsTypeObj[1]) { // 询价/出库 == 选择厂商
            //     this.isVendor = true
            // } else {
            //     // 类型为厂商相关 || ((采购 | 销售) && 类型为HH)
            //     this.isVendor = (otype === billsTypeObj[1] || otype === billsTypeObj[5]) || (modalType === 4 && otype === billsTypeObj[3])
            // }

            // if (this.isVendor) {
            //     this.changeTabsFn('ven')
            //     this.searchDataVen.vendor_name = this.partB.oname || this.partB.name
            //     this.searchDataVen.vendor_inner_sn = this.partB.oid || this.partB.id
            //     // 控制经销商Tab选择项卡的是否可操作
            //     this.isShowDealerTabPane = true
            // } else {
            //     this.searchDataVen.vendor_name = ''
            //     this.searchDataVen.vendor_inner_sn = ''
            //     this.isVendor = false
            //     // louxiaochen : BUG[3599] 添加乙方信息(partBSystemType),当其为厂商(3)时，不显示经销商自建产品
            //     this.isShowDealerTabPane = modalType === 5 || modalType === 6 || userSystemType === 3 || partBSystemType === 3
            // }

            // [BUG-3824]
            if (partBSystemType === 3) {
                this.isShowDealerTabPane = true;
            }

            // 需要修改所有选择产品界面中对厂商只选择自己产品的限制
            if (otype === this.billsTypeObj[1] || otype === this.billsTypeObj[5] || otype === this.billsTypeObj[8]) {
                this.searchDataVen.vendor_name = '';
                this.searchDataVen.vendor_inner_sn = '';
                this.isVendor = false;
                this.isShowDealerTabPane = true;
                this.changeTabsFn('ven');
            }

            // 询价时没有经销商tab
            if (modalType === 6) {
                this.isShowDealerTabPane = true;
            }

            if (
                (modalType === 7 || modalType === 8 || modalType === 11) &&
                ((userSystemType === 1 && otype === billsTypeObj[4]) ||
                    (userSystemType === 2 && otype === billsTypeObj[0]))
            ) {
                this.isDealer = true;
            } else {
                // (采购 | 销售 | 验货) && 类型为经销商相关
                this.isDealer =
                    (modalType === 4 || modalType === 9) &&
                    (otype === billsTypeObj[0] || otype === billsTypeObj[2] || otype === billsTypeObj[4]);
            }

            if (
                (modalType === 7 || modalType === 8 || modalType === 4) &&
                userSystemType === 2 &&
                otype === billsTypeObj[2]
            ) {
                // 当前登录账号是经销商端，且选择入库单和出库单的相关机构为医院时，将产品列表的经销商自建产品名称禁用掉，把当前登录的经销商名称带到经销商自建产品的输入框内
                this.isDealer = true;
                isReadonlyDealerName = true;
            }

            // 当前登录账号是经销商端并处于采购订单添加产品时
            if (modalType === 4 && userSystemType === 2) {
                this.isDealer = true;
            }

            if (this.isDealer) {
                if (isReadonlyDealerName) {
                    this.searchDataDea.dealer_name = this.employeeInfo.OrgName;
                } else {
                    this.searchDataDea.dealer_name = this.partB.oname;
                }
                this.searchDataDea.dealer_inner_sn = this.partB.oid;
            } else {
                this.searchDataDea.dealer_name = '';
                this.searchDataDea.dealer_inner_sn = '';
            }

            if (modalType === 7 && (otype === billsTypeObj[0] || otype === billsTypeObj[2])) {
                this.isDealer = true;
                this.searchDataDea.dealer_inner_sn = '';
            }

            // louxiaochen:BUG[3537] 在乙方为经销商时锁定搜索框
            if (partBSystemType === 2) {
                this.isDealer = true;
                this.searchDataDea.dealer_name = this.partB.name;
            }
        },
        // 默认是否勾选上合同内产品
        setContractOrgCheckedFn() {
            if (this.partB.modalType !== 7 && this.partB.modalType !== 11 && this.partB.modalType !== 4) return;
            // 默认不勾选合同，为了查询速度考虑
            if (this.userSystemType === 2) {
                this.isContractOrgChecked = false;
            } else {
                this.isContractOrgChecked = true;
            }
        },
        initWarehouseListFn() {
            /// 只有出库单才有仓库 【只有调拨单 - 待调入的是全量仓库，其它出库单都是自己用户权限仓库】
            if (this.partB.modalType === 8) {
                if (!this.partB.warehouseDatas) {
                    // 获取当前用户权限的仓库
                    const warehouseListStore = this.$store.state.warehouseList;
                    if (warehouseListStore.length !== 0) {
                        this.auth_warehouse_list = warehouseListStore.map((v) => {
                            return {
                                value: v.warehouse_model_inner_sn,
                                title: v.title
                            };
                        });
                    }
                } else {
                    // 待调入: 获取全量的仓库  待调出: 获取当前用户权限的仓库
                    this.auth_warehouse_list = this.partB.warehouseDatas;
                }
            }
        }
    },
    watch: {
        partB: {
            handler(newVal) {
                if (newVal) {
                    this.partB = newVal;

                    if (this.isReturnOrder) {
                        this.isContractOrgChecked = false;
                        this.initReturnProductWarehouseFn(newVal.warehouse_inner_sn);
                    }
                }
            },
            immediate: true,
            deep: true
        }
    },
    computed: {
        ...mapState(['warehouseList']),
        /// 判断是不是将出入库拆分为七个单的标识
        isSeparateBillOrder() {
            return this.orderTypeData && this.orderTypeData.value;
        },
        /// 判断是不是出入库拆分单的采购退货或销售退货标识
        isReturnOrder() {
            return (
                this.isSeparateBillOrder === grnAndOdoOrderType['PURCHASERETURN'].value ||
                this.isSeparateBillOrder === grnAndOdoOrderType['SALERETURN'].value
            );
        },
        isWarehouseDisabled() {
            /// 判断是否是拆分出来的出入库单依据
            if (this.isSeparateBillOrder) {
                if (this.isWarehouseParentDisabled) {
                    return this.isWarehouseParentDisabled;
                } else {
                    return (
                        (this.partB &&
                            this.partB.warehouse_inner_sn &&
                            this.partB.warehouse_inner_sn.toString().length > 0) ||
                        this.searchDataVen.warehouse_inner_sn.toString().length > 0
                    );
                }
            }
            return false;
        }
    },
    created() {
        // 初始化默认表头
        this.currentVendorColumns = [...this.defaultVendorColumns];
    },
    mounted() {
        this.clearSearchDataVenFn('vendor');
        // this.clearSearchDataVenFn('dealer')
        if (this.userSystemType === 2) {
            this.isContractOrgChecked = false;
        } else {
            this.isContractOrgChecked = true;
        }
        this.pageDataInit();
    }
};
</script>

<style scoped lang="less">
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

// 厂商/经销商
.input_box {
    .close {
        position: absolute;
        right: 6px;
        transform: scale(0.9) translateY(-50%);
        top: 50%;
        z-index: 2;
        width: 15px;
        height: 15px;
        border-radius: 50%;
        background: #b9b9b9;
        font-size: 6px;
        color: #fff;
        text-align: center;
        line-height: 15px;
        display: none;
        cursor: pointer;
    }
    &:hover .close {
        display: block;
    }
}
</style>
