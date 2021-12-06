<template>
    <div class="scoped scoped-white">
        <search-items-modal
            :modalHeading="modalHeading"
            v-show="isSearchModalVisible"
            :columns="searchModalTableHeaders"
            :items="isVendorModalVisible ? vendorModalItems : productModalItems"
            :picked="isVendorModalVisible ? vendorSelected : productSelected"
            :originalItemIndex="modalOriginalItemIndex"
            @close="closeSearchModal"
            :page="searchModalPage"
            :onChangePage="isVendorModalVisible ? changeVendorModalPage : changeProductModalPage"
            :onSearch="isVendorModalVisible ? loadVendors : planVendorListAPIFn"
        />
        <ul class="breadcrumb">
            <li><span class="turquoise">计划</span></li>
            <li>/</li>
            <li>新增</li>
        </ul>
        <div class="form-sheet">
            <div class="even-columns group">
                <div class="col">
                    <div class="col-label"><label class="form-label">部门</label></div>
                    <div class="col-input">
                        <a-tree-select
                            treeNodeFilterProp="title"
                            :showSearch="true"
                            :treeData="architectureAllTrees"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            v-model="selectedDept.value"
                            allowClear
                            @change="changeTopSearchAllTreeListFn"
                            class="input"
                            style="min-width: 120px"
                        />
                    </div>
                </div>
                <!-- <div class="col">
                    <div class="col-label"><label for="alias" class="form-label">姓名</label></div>
                    <div class="col-input">
                        <input name="alias" class="input" type="text" v-model="selectedDept.name" />
                    </div>
                </div> -->
            </div>
            <!-- <div class="even-columns group">
                <div class="col">
                    <div class="col-label"
                        ><label for="social_credit_code" class="form-label">董事姓名</label>
                    </div>
                    <div class="col-input">
                        <input
                            name="social_credit_code"
                            class="input"
                            type="text"
                            v-model="selectedDept.director_name"
                        />
                    </div>
                </div>
                <div class="col">
                    <div class="col-label"><label for="org_type_name" class="form-label">机构名称</label></div>
                    <div class="col-input"
                        ><input name="org_type_name" class="input" type="text" v-model="selectedDept.org_name"
                    /></div>
                </div>
            </div> -->
            <!-- <div class="even-columns group">
                <div class="col">
                    <div class="col-label"><label for="contact_person" class="form-label">电话</label></div>
                    <div class="col-input">
                        <input name="contact_person" class="input" type="text" v-model="selectedDept.phone"/>
                    </div>
                </div>
                <div class="col">
                    <div class="col-label"><label for="phone_number" class="form-label">地位</label></div>
                    <div class="col-input"
                        ><input name="phone_number" class="input" type="text" v-model="selectedDept.status"
                    /></div>
                </div>
            </div> -->
            <div id="products" class="tabcontent" v-bind:style="{ display: prodDisplay }">
                <div class="table-container">
                    <div
                        id="filter-menu"
                        :style="{ display: filterMenuDisplay }"
                        v-on:mouseover="filterMenuHover = true"
                        v-on:mouseleave="filterMenuHover = false"
                    >
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-1" v-model="headers[0]" />
                            <label for="checkbox-1">NO.</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-2" v-model="headers[1]" />
                            <label for="checkbox-2">产品</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-3" v-model="headers[2]" />
                            <label for="checkbox-3">规格型号</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-4" v-model="headers[3]" />
                            <label for="checkbox-4">厂商/经销商</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-5" v-model="headers[4]" />
                            <label for="checkbox-5">批号</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-6" v-model="headers[5]" />
                            <label for="checkbox-6">数量</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-7" v-model="headers[6]" />
                            <label for="checkbox-7">包装单位</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-8" v-model="headers[7]" />
                            <label for="checkbox-8">状态</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-12" v-model="headers[8]" />
                            <label for="checkbox-9"></label>
                        </div>
                    </div>
                </div>
                <table class="table-products">
                    <thead>
                        <th v-if="showColumn(0)" style="width: 33px">NO.</th>
                        <th v-if="showColumn(1)" title="Product" style="width: 248px">产品</th>
                        <th v-if="showColumn(2)" title="Specification Model" style="width: 148px">规格型号</th>
                        <th v-if="showColumn(3)" title="Product Vendor Name" style="width: 198px">厂商/经销商</th>
                        <th v-if="showColumn(4)" title="Batch Number" style="width: 128px">批号</th>
                        <th v-if="showColumn(5)" title="Quantity" style="width: 98px">数量</th>
                        <th v-if="showColumn(6)" title="Unit Inner Sn" style="width: 98px">包装单位</th>
                        <th v-if="showColumn(7)" title="Status" style="width: 98px">状态</th>
                        <th title="Filter" style="width: 100px">
                            <div id="filter-columns">
                                <img
                                    class="clickable"
                                    v-click-outside="onClickOutside"
                                    @click="toggleFilterDropdown"
                                    src="../../../public/icons/more.svg"
                                    width="16px"
                                />
                            </div>
                        </th>
                    </thead>
                    <tbody>
                        <tr v-for="(product, productIndex) in venTableData" :key="productIndex">
                            <td v-if="showColumn(0)">
                                {{ productIndex + 1 }}
                            </td>
                            <td v-if="showColumn(1)">
                                <input v-model="product.product_name" class="input" />
                            </td>
                            <td v-if="showColumn(2)">
                                {{ product.specification }}
                            </td>
                            <td v-if="showColumn(3)">
                                {{ product.product_vendor_name }}
                            </td>
                            <td v-if="showColumn(4)">
                                {{ product.batch_number }}
                            </td>
                            <td v-if="showColumn(5)">
                                <a-input-number
                                    :min="0.001"
                                    v-model="product.quantity"
                                    class="input"
                                    style="width: 100%"
                                />
                            </td>
                            <td v-if="showColumn(6)">
                                <a-cascader
                                    :allowClear="false"
                                    :options="product.unitList"
                                    v-model="product.unit_inner_sn"
                                    changeOnSelect
                                    :displayRender="displayRender"
                                    @click.stop="clickUnitListFn(product)"
                                    @change="changeUnitFn"
                                    placeholder=""
                                />
                            </td>
                            <td v-if="showColumn(7)">
                                <div>{{ product.status | formatStatus('planOrder') }}</div>
                            </td>
                            <td
                                ><img
                                    class="clickable"
                                    @click="deleteProduct(productIndex)"
                                    src="../../../public/icons/delete.svg"
                            /></td>
                        </tr>
                        <tr>
                            <td colspan="2" class="add_product"><a v-on:click="addProduct">添加计划单</a></td>
                        </tr>
                    </tbody>
                </table>

                <div class="row-terms">
                    <div class="col-terms-1">
                        <textarea
                            id="terms"
                            class="input textarea"
                            placeholder="摘要"
                            v-model="planModalForm.description"
                            rows="5"
                        ></textarea>
                    </div>
                    <div class="col-terms-2">
                        <table class="table-totals">
                            <tbody>
                                <!-- <tr>
                                    <td>Untaxed amount:</td>
                                    <td style="width: 46px">{{ products.reduce((quantity, unit_price) => quantity * unit_price, 0) }}</td>
                                </tr> -->
                                <!-- <tr>
                                    <td>Taxes:</td>
                                    <td style="width: 46px">{{ products.reduce((taxes) => taxes, 0) }}</td>
                                </tr> -->
                                <!-- <tr>
                                    <td>总计:</td>
                                    <td style="width: 46px">
                                        {{ this.total }}
                                    </td>
                                </tr> -->
                            </tbody>
                        </table>
                    </div>
                    <div class="rowIndex">
                        <ul style="display: flex; padding-top: 24px">
                            <li>
                                <div class="item" style="min-width: 140px; padding-bottom: 0">
                                    <span class="label" style="width: auto">制单人</span>
                                    <span>{{ planModalForm.creator_name || '-' }}</span>
                                </div>
                                <div class="item" style="min-width: 194px; padding-bottom: 0">
                                    <span class="label" style="width: auto">生成时间</span>
                                    <span>{{ planModalForm.create_time || '-' }}</span>
                                </div>
                            </li>
                            <li>
                                <div class="item" style="min-width: 140px; padding-bottom: 0">
                                    <span class="label" style="width: auto">审核人</span>
                                    <span>{{ planModalForm.auditor_name || '-' }}</span>
                                </div>
                                <div class="item" style="min-width: 194px; padding-bottom: 0">
                                    <span class="label" style="width: auto">审核时间</span>
                                    <span>{{ planModalForm.audit_time || '-' }}</span>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="row form-buttons">
                <button class="btn btn-filled" @click="printFn()">打印</button>
                <button class="btn btn-filled" @click="openPopup()">导出Excel</button>
                <button class="btn btn-muted">生成采购订单</button>
                <div class="right-buttons">
                    <button class="btn btn-clear" @click="clear()">取消</button>
                    <button class="btn btn-filled" @click="commitshow()">存草稿</button>
                    <button class="btn btn-filled" @click="commitshow()">保存</button>
                </div>
            </div>
            <a-modal
                title="新增"
                :visible="exportExcelMd.alert"
                :width="1140"
                okText="保存"
                :maskClosable="false"
                :data="products"
                :centered="true"
                @cancel="cancelCt"
                class="modal-form-input-scoped global-drag create-inquiry-md"
            >
                <div v-globalDrag="{ el: 'create-inquiry-md' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ exportExcelMd.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <div style="height: 32px"></div>
                </template>
                <export-excel
                    ref="enquiryListMd"
                    @done="createDone"
                    @cancel="exportExcelMd.alert = false"
                ></export-excel>
            </a-modal>
        </div>
        <table class="commit-table" v-if="undertable">
            <thead>
                <th style="width: 100px">厂商</th>
                <th style="width: 100px">单据编号</th>
                <th style="width: 100px">联系人</th>
                <th style="width: 100px">电话</th>
                <th style="width: 100px">询价时间</th>
                <th style="width: 100px">回复时间</th>
                <th style="width: 100px">状态</th>
            </thead>
            <tbody>
                <tr v-for="(product, productIndex) in products" :key="productIndex">
                    <td>{{ selectedVendor.org_name }}</td>
                    <td>{{ product.vendor_name }}</td>
                    <td>{{ product.contact_person }}</td>
                    <td>{{ product.phone_number }}</td>
                    <td>{{ product.produce_date }}</td>
                    <td>{{ product.overdue_date }}</td>
                    <td>{{ product.status }}</td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
import { platformUnitTree, planVendorListAPI, orgDeptTopTreeAPI, dealerUnitTreeAPI } from '@/service/pageAjax';
import searchItemsModal from '@/components/purchase/searchItemsModal.vue';
import exportExcel from '@/views/commonPage/buyManage/module/exportExcel.vue';
import Vue from 'vue';

export default {
    name: 'SuppliesClaim',
    components: {
        searchItemsModal,
        exportExcel
    },
    data() {
        return {
            // Properties for vendor dropdown input
            dropDownInputStyle: {
                width: '17rem',
                height: '1.5rem',
                maxHeight: '15rem',
                placeholder: 'Vendor Name',
                // outerContainerStyle: 'margin: 0; padding: 0;',
                // inputStyle: 'border-bottom: 1px solid #ccc',
                inputClass: 'input',
                itemContainerClass: 'item vendor-item'
            },

            vendorInputText: '',
            isProdActive: true,
            isInfoActive: false,
            prodDisplay: 'block',
            infoDisplay: 'none',
            products: [],
            total: 0,

            order_deadline: new Date(),
            receipt_date: new Date(),

            inputVendorName: '',
            vendors: [],
            vendorNames: [],
            selectedVendor: {
                org_name: '',
                alias: '',
                social_credit_code: '',
                org_type_name: '',
                contact_person: '',
                phone_number: ''
            },
            isSearchModalVisible: false,
            isVendorModalVisible: false,
            isProductModalVisible: false,

            // getVendorProductListAPI
            vendorSearchData: {
                name: '',
                vendor_inner_sn: '',
                vendor_name: '',
                specification: '',
                specification_vendor_sn: '',
                warehouse_inner_sn: '',
                type_inner_sn: [],
                code68_sn: []
            },
            searchModalPage: {
                loading: false,
                total: 10,
                current: 1,
                pageSize: 10,
                pageNum: 1,
                pageSizes: [10, 20, 30, 40, 50]
            },
            venTableData: [],

            filterMenuDisplay: 'none',
            filterMenuHover: false,
            showFilterMenu: false,
            headers: [true, true, true, true, true, true, true, true, true, true, true, true, true, true],

            // For search modal
            modalHeading: '',
            modalOriginalItemIndex: 0,
            searchModalTableHeaders: [],
            searchModalTableItems: [],
            vendorTableHeaders: [
                { name: '名称', width: 127 },
                { name: '别名', width: 87 },
                { name: '统一社会信用代码', width: 286 },
                { name: '机构类型', width: 142 },
                { name: '联系人', width: 142 },
                { name: '电话', width: 142 }
            ],
            productTableHeaders: [
                { name: '产品', width: 250 },
                { name: '规格型号', width: 150 },
                { name: '厂商/经销商', width: 200 },
                { name: '批号', width: 200 },
                { name: '数量', width: 200 },
                { name: '包装单位', width: 130 },
                { name: '状态', width: 100 }
                // { name: '单价', width: 100 },
                // { name: '金额', width: 100 },
                // { name: '税率', width: 100 },
                // { name: '含税单价', width: 100 },
                // { name: '含税金额', width: 100 },
                // { name: '税额', width: 100 }
            ],
            exportExcelData: [{}],
            undertable: false,
            formData: {
                contacts: '',
                description: '',
                detailList: '',
                ils_sn: '',
                partA: 1,
                partB: 2,
                phone: ''
            },
            coloums: [
                {
                    align: 'center',
                    dataIndex: 'idx',
                    fixed: 'left',
                    minWidth: '60',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: 'NO.',
                    type: 'index'
                },
                {
                    align: 'center',
                    dataIndex: 'product_name',
                    minWidth: '250',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '产品'
                },
                {
                    align: 'center',
                    dataIndex: 'specification',
                    minWidth: '150',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '规格型号'
                },
                {
                    align: 'center',
                    dataIndex: 'product_vendor_name',
                    minWidth: '200',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '厂商/经销商'
                },
                {
                    align: 'center',
                    dataIndex: 'batch_number',
                    minWidth: '130',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '批号'
                },
                {
                    align: 'center',
                    dataIndex: 'quantity',
                    minWidth: '100',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '数量'
                },
                {
                    align: 'center',
                    dataIndex: 'unit_inner_sn',
                    minWidth: '100',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '包装单位'
                },
                {
                    align: 'center',
                    dataIndex: 'status',
                    minWidth: '100',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '状态'
                }
            ],
            // For detecting click outside an element
            vcoConfig: {
                handler: this.handler,
                middleware: this.middleware,
                events: ['dblclick', 'click'],
                isActive: true,
                // Note: The default value is true. See "Detecting Iframe Clicks" section
                //       to understand why this behaviour is behind a flag.
                detectIFrame: true
            },
            exportExcelMd: {
                title: `导出 - Excel`,
                alert: false,
                loading: false
            },
            operationPlanModalObj: {
                created_time: '',
                creator_org_inner_sn: '',
                creator_org_type: '',
                creator_user_inner_sn: '',
                creator_user_name: '',
                description: '',
                doctor_employee_inner_sn: '',
                doctor_name: '',
                hospital_department_inner_sn: '',
                hospital_department_name: '',
                hospital_inner_sn: '',
                hospital_name: '',
                // id: '',
                // is_delete: '',
                // keywords: '',
                // last_modified_time: '',
                // name: '',
                operation_time: '',
                patient_name: ''
            },
            architectureAllTrees: [], // 所属部门的信息列表
            searchTables: {
                ppl_sn: '',
                status: undefined,
                department_inner_sn: '',
                creator_name: ''
            },
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
                status: ''
            },
            selectedDept: {},
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
            venPage: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            }
        };
    },
    computed: {
        vendorModalItems: function () {
            return this.vendors.map((item) => {
                return {
                    name: item.org_name ?? '',
                    alias: item.alias ?? '',
                    social_credit_code: item.social_credit_code ?? '',
                    org_type_name: item.org_type_name ?? '',
                    contact_person: item.contact_person ?? '',
                    phone_number: item.phone_number ?? ''
                };
            });
        },
        productModalItems: function () {
            return this.products.map((p, index) => {
                return {
                    name: p.name,
                    specification: p.specification,
                    vendor_name: p.vendor_name,
                    quantity: p.quantity,
                    packing_unit: '',
                    unit_price: p.unit_price,
                    amount: '',
                    tax_rate: '',
                    price_including_tax: '',
                    tax_included_amount: ''
                };
            });
        },
        productNames: function () {
            return this.products.map((p, index) => {
                return {
                    id: index,
                    name: p.name
                };
            });
        }
    },
    methods: {
        printFn() {
            console.log(this.products);
            this.formData.contacts = this.selectedVendor.contact_person;
            this.formData.phone = this.selectedVendor.phone_number;
            this.formData.description = this.selectedVendor.description;
            for (var i = 0; i < this.products.length; i++) {
                this.products[i].unit_price =
                    this.products[i].rate_unit - (this.products[i].rate_unit * this.products[i].rate) / 100;
                this.products[i].sum =
                    this.products[i].rate_unit - (this.products[i].rate_unit * this.products[i].rate) / 100;
                this.products[i].rate_price = this.products[i].rate_unit * this.products[i].quantity;
                this.products[i].rate_num =
                    (this.products[i].rate_unit * this.products[i].quantity * this.products[i].rate) / 100;
                console.log(this.products[i].packing);
            }
            let printContent = {
                printData: this.products,
                printForm: this.formData,
                printTable: this.coloums,
                printType: 4, // 1、图片；2、耗材申领；3、出入库单; 4、询价单
                pageType: 0, // 0 询价 1回复
                orgName: this.selectedVendor.org_name,
                printTitle: '询价单',
                unitList: []
            };
            console.log(this.products[0]);
            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        exportExcel() {
            this.formData.contacts = this.selectedVendor.contact_person;
            this.formData.phone = this.selectedVendor.phone_number;
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
                            value: this.products[0].orgName
                        },
                        {
                            label: '联系人',
                            value: this.selectedVendor.contact_person
                        },
                        {
                            label: '电话',
                            value: this.selectedVendor.phone_number
                        }
                    ]
                ]
            };
            this.$Utils.exportExcel(
                this.columns,
                this.products.map((item) => ({
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
        openPopup() {
            // 打开新增
            this.formData.contacts = this.selectedVendor.contact_person;
            this.formData.phone = this.selectedVendor.phone_number;
            const form = this.formData;
            const formData = {
                title: '询价单',
                contacts: form.contacts,
                phone: form.phone,
                desc: {
                    label: '摘要',
                    value: this.selectedVendor.description
                },
                groups: [
                    [
                        {
                            label: '单位名称',
                            value: this.products[0].orgName
                        },
                        {
                            label: '联系人',
                            value: this.selectedVendor.contact_person
                        },
                        {
                            label: '电话',
                            value: this.selectedVendor.phone_number
                        }
                    ]
                ]
            };
            this.$Utils.globalDragCenterFn('create-inquiry-md');
            this.exportExcelMd.alert = true;

            let exportFormData = JSON.stringify(formData);
            window.localStorage.exportFormData = exportFormData;

            console.log(this.products);
            let exportProductData = JSON.stringify(this.products);
            window.localStorage.exportProductData = exportProductData;
            // if (this.$refs.enquiryListMd) {
            //     this.$refs.enquiryListMd.refreshPage();
            // }
        },
        blankMethod: function (product, index) {},
        dropDownInputTextChanged: function (event) {
            console.log(event);
        },
        openTab: function (display) {
            this.isProdActive = display;
            this.isInfoActive = !display;
            this.prodDisplay = display ? 'block' : 'none';
            this.infoDisplay = display ? 'none' : 'block';
        },
        clickAll: function () {
            for (var i = 0; i < 12; i++) {
                this.headers[i] = true;
            }
        },
        // findDept: function (item, value) {
        //     if (item.value === value) return item;
        //     if (item.children.length === 0) continue;
        //     item.children.forEach((v) => {
        //         this.findDept(v, value);
        //     });
        // },
        // 设置后端所需要的格式
        changeTopSearchAllTreeListFn(value, label) {
            // this.architectureAllTrees.forEach((item) => {
            //     const found = this.findDept(item, value);
            //     if (!found) {
            //         this.selectedDept = Object.assign({}, found);
            //         return;
            //     }
            // });
            this.selectedDept = Object.assign(
                {},
                this.architectureAllTrees.find((v) => v.value === value)
            );
            this.planVendorListAPIFn();
            this.searchTables.department_inner_sn = value && value.length ? value[0] : '';
            this.planModalForm.department_inner_sn = value && value.length ? value[0] : '';
            this.planModalForm.department_name = label;
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

            if (isAppendDatas === 'deptTopTree') {
                // 所属部门
                this.architectureAllTrees = datasOriginNew;
                // 设置默认部门值为当前登陆人的第一个部门
                if (this.btnName === 'add' && parseFloat(this.$cookie.get('userSystemType')) === 1) {
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
                    this.planModalForm.department_inner_sn = intersection[0].department_inner_sn;
                    this.planModalForm.department_name = intersection[0].label;

                    Vue.prototype.$message.success('已加载厂商/经销商');
                }
            }
        },
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
        addProduct: function () {
            this.products.push({
                product_name: '',
                specification: '',
                product_vendor_name: '',
                quantity: 0,
                batch_number: 0,
                status: ''
                // tax: 0
            });
            console.log(this.products);
        },
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

        vendorSelected: function (vendor, index, originalItemIndex) {
            // this.selectedVendor = this.vendors.find(v => v.org_name === vendor.org_name);
            this.selectedVendor = this.vendors[index];
        },

        vendorNameSelected: function (vendor, originalIndex) {
            console.log('Vendor selected: ');
            console.log(vendor);
            this.selectedVendor = this.vendors.find((v) => v.org_id === vendor.id);
            this.loadAllProductsOfVendor();
        },
        displayPacking(rd) {
            let len = rd.labels.length;
            const label = rd.labels[len - 1];
            return label;
        },
        onChangePacking(value) {
            this.packingvalue = value;
            console.log(value);
        },
        // 【厂商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async platformUnitTreeFn(record, btnName) {
            await platformUnitTree(
                record.product_vendor_inner_sn,
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
                        if (btnName === 'add') {
                            // 赋默认值
                            const curUnit = record.unitList[0];
                            record.unit_inner_sn = [curUnit.value];
                            record.unit_name = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xTablePlanModalInfo.reloadRow(record, record, 'unit');
                        }
                        record.detail_inner_sn && !record.isLoadingUnit && this.platformUnitParentsFn(record);
                    }
                }
            });
        },
        productSelected(product, index, originalItemIndex) {
            this.products[originalItemIndex] = Object.assign({}, this.products[index]);
        },
        productNameSelected(product, originalIndex) {
            console.log('Selected product with original index ' + originalIndex);
            console.log(product);
            // let selectedProduct = this.products.find((p, index) => { index === product.id });
            let selectedProduct = {};
            for (let i = 0; i < this.products.length; i++) {
                if (i === product.id) {
                    selectedProduct = this.products[i];
                }
            }

            console.log('selectedProduct');
            console.log(selectedProduct);
            this.products[originalIndex] = Object.assign({}, selectedProduct);
        },
        showVendorSearchModal(index) {
            console.log('ShowVendorSearchModal picked');
            this.modalHeading = '部门';
            this.isVendorModalVisible = true;
            this.searchModalTableHeaders = this.vendorTableHeaders;
            this.searchModalTableItems = this.vendorModalItems;
            this.modalOriginalItemIndex = -1;
            this.loadVendors();

            this.isSearchModalVisible = true;
        },
        showProductSearchModal(index) {
            console.log('ShowProductSearchModal picked');
            this.modalHeading = '产品';
            this.isProductModalVisible = true;
            this.searchModalTableHeaders = this.productTableHeaders;
            this.searchModalTableItems = this.productModalItems;
            this.modalOriginalItemIndex = index;
            this.loadAllProductsOfVendor();

            this.isSearchModalVisible = true;
        },
        closeSearchModal() {
            this.isSearchModalVisible = false;
            this.isProductModalVisible = false;
            this.isVendorModalVisible = false;

            this.searchModalPage.loading = false;
            this.searchModalPage.total = 10;
            this.searchModalPage.pageSize = 10;
            this.searchModalPage.current = 1;
            this.searchModalPage.pageNum = 1;
        },
        changeVendorModalPage({ currentPage, pageSize }) {
            console.log('Vendor modal page changed to ' + currentPage);

            this.searchModalPage.current = currentPage;
            this.searchModalPage.pageSize = pageSize;
            this.searchModalPage.pageNum = currentPage;
            this.searchModalPage.loading = true;
            this.loadVendors();
        },
        changeProductModalPage({ currentPage, pageSize }) {
            console.log('Product modal page changed to ' + currentPage);

            this.searchModalPage.current = currentPage;
            this.searchModalPage.pageSize = pageSize;
            this.searchModalPage.pageNum = currentPage;
            this.searchModalPage.loading = true;
            this.loadAllProductsOfVendor();
        },
        calculateTotal() {
            this.total = 0;
            for (let product of this.products) {
                let subtotal = parseInt(product.quantity) * parseInt(product.unit_price);
                subtotal = isNaN(subtotal) ? 0 : subtotal;
                this.total += subtotal;
            }
        },
        deleteProduct: function (index) {
            this.products.splice(index, 1);
        },
        toggleFilterDropdown() {
            this.showFilterMenu = !this.showFilterMenu;
            if (this.showFilterMenu) {
                this.filterMenuDisplay = 'block';
            } else {
                this.filterMenuDisplay = 'none';
            }
        },
        hideFilterDropdown(event) {
            this.showFilterMenu = false;
            this.filterMenuDisplay = 'none';
        },
        showColumn(n) {
            return this.headers[n];
        },
        onClickOutside(event) {
            if (!this.filterMenuHover) {
                this.hideFilterDropdown();
            }
        },
        cancelCt() {
            this.exportExcelMd.alert = false;
            // this.$refs.enquiryListMd.refreshPage()
        },
        createDone(dataObj) {
            this.exportExcelMd.alert = false;
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
        changeUnitFn(value, selectedOptions) {
            this.record.unit_name = selectedOptions.length ? selectedOptions[selectedOptions.length - 1].name : '';
        },
        displayRender(rd) {
            let len = rd.labels.length;
            const label = rd.labels[len - 1];
            return label.split('(')[0];
        },
        // 【经销商 产品单位】 根据产品ID查询所有产品单位以树的方式返回
        async dealerUnitTreeAPIFn(record, btnName) {
            await dealerUnitTreeAPI(
                record.product_vendor_inner_sn,
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
                        if (btnName === 'add') {
                            // 赋默认值
                            const curUnit = record.unitList[0];
                            record.unit_inner_sn = [curUnit.value];
                            record.unit_name = curUnit.label;
                            record.isLoadingUnit = true;
                            this.$refs.xTablePlanModalInfo.reloadRow(record, record, 'unit');
                        }
                        record.detail_inner_sn && !record.isLoadingUnit && this.dealerUnitParentsAPIFn(record);
                    }
                }
            });
        },
        // 包装单位区域
        clickUnitListFn(record) {
            if (!record.isLoadingUnit) {
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
        changeitem(value, arr) {
            let record;
            record.unit_data.key = value;
            record.unit_inner_sn = value[value.length - 1];
            record.unit_name = arr[arr.length - 1].name;
            record.isNew = false;
        },
        openModal() {
            this.undertable = true;
        },
        commitshow() {
            if (this.undertable == true) {
                console.log('true');
                this.undertable = false;
            } else {
                console.log('false');
                this.undertable = true;
            }
        },
        clear() {
            this.products = [];
        }
    },
    filters: {
        formatStatus(value, name) {
            const planListStatus = ['草稿', '正式', '已审核', '已终止', '订单已生成'],
                planOrderStatus = ['待处理', '已终止', '已生成采购订单'];
            if (name === 'planList') {
                return planListStatus[value];
            } else if (name === 'planOrder') {
                return planOrderStatus[value];
            }
        }
    },
    mounted() {
        this.orgDeptTopTreeAPIFn();
    }
};
</script>

<style>
/* body {
    font-family: "Roboto", "Odoo Unicode Support Noto",sans-serif;
    font-size: 1.08333333rem;
    font-weight: 400;
    line-height: 1.5;
    text-align: left;
} */

.scoped {
    overflow: auto;
    text-align: left;
}

.form-sheet {
    width: 100%;
    margin: 1.2em auto;
    padding: 1.2em;
    border: 1px solid #ced4da;
    box-shadow: 0 5px 20px -15px black;
    font-size: 0.8rem;
    font-weight: bold;
    color: #666666;
}

.turquoise {
    color: #1890ff;
}

.breadcrumb {
    list-style: none;
    padding: 0;
    display: flex;
    margin: 0;
}

.breadcrumb li {
    margin: 0 0.2em;
}

.row {
    display: flex;
    margin: 0.4em auto;
}

.table-container {
    position: relative;
}

.table-products {
    /* width: 100%; */
    margin-bottom: 5em;
    overflow-x: auto;
    overflow-y: none;
    /* display: block; */

    /* position: absolute;
    z-index: 1;
    top: 0;
    left: 0; */
}

#filter-menu {
    z-index: 1;
    position: absolute;
    background-color: white;
    top: 2529pxpx;
    right: 0;
    /* left: auto; */
    /* bottom: -100px; */
    float: left;
    padding: 0.5rem 0;
    border: 1px solid #dee2e6;
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
    width: 30%;
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

.btn {
    font-weight: bold;
    text-transform: uppercase;
    border: none;
    padding: 7px 10px;
    margin: 0.2em 0.1em;
}

#filter-heading:hover,
.clickable:hover,
.btn:hover {
    cursor: pointer;
}

.btn-filled {
    background-color: #1890ff;
    color: white;
    border-radius: 4px;
    text-shadow: 0 -1px 0 rgb(0 0 0 / 12%);
    box-shadow: 0 2px 0 rgb(0 0 0 / 5%);
}

.btn-filled:hover {
    background-color: #2090e6;
}

.btn-clear {
    background-color: transparent;
    color: #1890ff;
}

.btn-clear:hover {
    background-color: #e6e6e6;
    color: black;
}

.btn-delete {
    background-color: #bf0b0b;
    color: white;
    padding: 0 auto;
}

.btn-delete:hover {
    background-color: #7a1212;
}

.form-statusbar {
    border-top: 1px solid #ced4da;
    border-bottom: 1px solid #ced4da;
}

.title > * {
    margin-top: 0;
    margin-bottom: 0;
}

.input {
    /*cursor: pointer;*/
    border-top-style: none;
    border-left-style: none;
    border-right-style: none;
    border: none;
    display: inline-block;
    width: 100%;
    outline: none;
}

.vendor-item {
    /* text-align: center; */
    padding: 1em;
}

a {
    text-decoration: none;
    color: #1890ff;
}

a:hover {
    color: #215352;
}

.col-half {
    width: 50%;
}

.form-label {
    margin: 0 5px 5px 0;
}

/* Tab Buttons */
.tab button {
    background-color: white;
    float: left;
    border: none;
    outline: none;
    cursor: pointer;
    padding: 0.5rem 1rem;
    /*transition: 0.3s;*/
    /*font-size: 17px;*/
    border: 1px solid #ced4da;
}

/* Change background color of buttons on hover */
.tab button:hover {
    background-color: #ddd;
}

/* Create an active/current tablink class */
.tab button.active {
    border-top: 1px solid black;
    border-bottom-style: none;
}

/* Style the tab content */
.tabcontent {
    /*padding: 6px 12px;*/
    border-top: 1px solid #ced4da;
}

.table-totals {
    text-align: right;
}

img {
    height: 15px;
    width: 30px;
}
#filter-columns {
    position: relative;
    display: inline-block;
    float: right;
}

.filter-menu-item {
    padding: 0.2rem 0.8rem;
    display: inherit;
    color: #666866;
}
.add_product {
    position: absolute;
    right: 25px;
}
.textarea {
    height: 30px;
}
.right-buttons {
    position: absolute;
    right: 25px;
}
.commit-table {
    width: 100%;
    background-color: white;
}
</style>
