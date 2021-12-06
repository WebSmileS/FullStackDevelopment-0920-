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
            :onSearch="loadVendors"
        />
        <ul class="breadcrumb">
            <li><span class="turquoise">询价单</span></li>
            <li>/</li>
            <li>新增</li>
        </ul>
        <div class="form-sheet">
            <div class="even-columns group">
                <div class="col">
                    <div class="col-label"><label class="form-label">厂商/经销商</label></div>
                    <DropDownInput
                        class="col-input"
                        :items="vendorNames"
                        :picked="vendorNameSelected"
                        :searchMorePicked="showVendorSearchModal"
                        v-model="selectedVendor.org_name"
                        :originalIndex="-1"
                    />
                </div>
                <div class="col">
                    <div class="col-label"><label for="alias" class="form-label">别名</label></div>
                    <div class="col-input">
                        <input name="alias" class="input" type="text" v-model="selectedVendor.alias" />
                    </div>
                </div>
            </div>
            <div class="even-columns group">
                <div class="col">
                    <div class="col-label"
                        ><label for="social_credit_code" class="form-label">统一社会信用代码</label>
                    </div>
                    <div class="col-input">
                        <input
                            name="social_credit_code"
                            class="input"
                            type="text"
                            v-model="selectedVendor.social_credit_code"
                        />
                    </div>
                </div>
                <div class="col">
                    <div class="col-label"><label for="org_type_name" class="form-label">机构类型</label></div>
                    <div class="col-input"
                        ><input name="org_type_name" class="input" type="text" v-model="selectedVendor.org_type_name"
                    /></div>
                </div>
            </div>
            <div class="even-columns group">
                <div class="col">
                    <div class="col-label"><label for="contact_person" class="form-label">联系人</label></div>
                    <div class="col-input">
                        <input
                            name="contact_person"
                            class="input"
                            type="text"
                            v-model="selectedVendor.contact_person"
                        />
                    </div>
                </div>
                <div class="col">
                    <div class="col-label"><label for="phone_number" class="form-label">电话</label></div>
                    <div class="col-input"
                        ><input name="phone_number" class="input" type="text" v-model="selectedVendor.phone_number"
                    /></div>
                </div>
            </div>
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
                            <label for="checkbox-4">厂商</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-5" v-model="headers[4]" />
                            <label for="checkbox-5">数量</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-6" v-model="headers[5]" />
                            <label for="checkbox-6">包装单位</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-7" v-model="headers[6]" />
                            <label for="checkbox-7">单价</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-8" v-model="headers[7]" />
                            <label for="checkbox-8">金额</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-9" v-model="headers[8]" />
                            <label for="checkbox-9">税率</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-10" v-model="headers[9]" />
                            <label for="checkbox-10">含税单价</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-11" v-model="headers[10]" />
                            <label for="checkbox-11">含税金额</label>
                        </div>
                        <div class="filter-menu-item">
                            <input type="checkbox" id="checkbox-12" v-model="headers[11]" />
                            <label for="checkbox-12">税额</label>
                        </div>
                    </div>
                </div>
                <table class="table-products">
                    <thead>
                        <th v-if="showColumn(0)" style="width: 33px">NO.</th>
                        <th v-if="showColumn(1)" title="Product" style="width: 248px">产品</th>
                        <th v-if="showColumn(2)" title="Specification Model" style="width: 148px">规格型号</th>
                        <th v-if="showColumn(3)" title="Vendor" style="width: 198px">厂商</th>
                        <th v-if="showColumn(4)" title="Quantity" style="width: 128px">数量</th>
                        <th v-if="showColumn(5)" title="Packing Unit" style="width: 98px">包装单位</th>
                        <th v-if="showColumn(6)" title="Unit Price" style="width: 98px">单价</th>
                        <th v-if="showColumn(7)" title="Amount" style="width: 98px">金额</th>
                        <th v-if="showColumn(8)" title="Tax Rate" style="width: 98px">税率</th>
                        <th v-if="showColumn(9)" title="Unit Price Included" style="width: 98px">含税单价</th>
                        <th v-if="showColumn(10)" title="Tax Included" style="width: 98px">含税金额</th>
                        <th v-if="showColumn(11)" title="Taxes" style="width: 98px">税额</th>
                        <th title="Delete" style="width: 50px">操作</th>
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
                        <tr v-for="(vendor_product, vendor_productIndex) in vendor_products" :key="vendor_productIndex">
                            <td v-if="showColumn(0)">{{vendor_productIndex+1}}</td>
                            <td v-if="showColumn(1)">
                                <div v-if="vendor_product.readOnly">{{ vendor_product.name }}</div>
                                <DropDownInput
                                    v-else
                                    :items="productNames"
                                    :picked="productSelected"
                                    :searchMorePicked="showProductSearchModal"
                                    v-model="vendor_product.name"
                                    :originalIndex="vendor_productIndex"
                                    :search="loadAllProductsOfVendor"
                                />
                            </td>
                            <td v-if="showColumn(2)">
                                <div v-if="vendor_product.readOnly">{{ vendor_product.specification }}</div>
                                <input v-else v-model="vendor_product.specification" class="input" />
                            </td>
                            <td v-if="showColumn(3)">{{ vendor_product.vendor_name }}</td>
                            <td v-if="showColumn(4)">
                                <div v-if="vendor_product.readOnly">{{ vendor_product.quantity }}</div>
                                <input v-else v-model="vendor_product.quantity" class="input" type="number" min="0" />
                            </td>
                            <td v-if="showColumn(5)">
                                <div>{{ vendor_product.unit_name }}</div>
                                <a-cascader
                                    v-if="vendor_product.packing"
                                    :options="vendor_product.packing"
                                    :displayRender="displayPacking"
                                    :defaultValue="[`${vendor_product.packing[0].value}`]"
                                    @change="onChangePacking"
                                    style="width: 100%"
                                />
                            </td>
                            <td v-if="showColumn(6)">
                                <!-- <input v-model="product.unit_price" class="input" /> -->
                                {{
                                    vendor_product.rate == '0%'
                                        ? vendor_product.rate_unit
                                        : vendor_product.rate_unit -
                                          (vendor_product.rate_unit * vendor_product.rate) / 100
                                }}
                            </td>
                            <td v-if="showColumn(7)">
                                <!-- <input v-model="product.sum" class="input" /> -->
                                {{
                                    vendor_product.rate == '0%'
                                        ? vendor_product.rate_unit
                                        : vendor_product.rate_unit -
                                          (vendor_product.rate_unit * vendor_product.rate) / 100
                                }}
                            </td>
                            <td v-if="showColumn(8)">
                                <!-- <input v-model="product.rate" class="input" /> -->
                                <div v-if="vendor_product.readOnly">{{ vendor_product.rate }}</div>
                                <a-select
                                    v-else
                                    v-model="vendor_product.rate"
                                    style="width: 68px; margin-right: 4px"
                                    onChange="{change}"
                                >
                                    <a-select-option value="0"> 0% </a-select-option>
                                    <a-select-option value="1"> 1% </a-select-option>
                                    <a-select-option value="3"> 3% </a-select-option>
                                    <a-select-option value="6"> 6% </a-select-option>
                                    <a-select-option value="9"> 9% </a-select-option>
                                    <a-select-option value="13"> 13% </a-select-option>
                                </a-select>
                            </td>
                            <td v-if="showColumn(9)">
                                <!-- <input v-model="product.rate_unit" class="input" /> -->
                                <div v-if="vendor_product.readOnly">{{ vendor_product.rate_unit }}</div>
                                <input v-else v-model="vendor_product.rate_unit" class="input" type="number" min="0" />
                            </td>
                            <td v-if="showColumn(10)">
                                <!-- <input v-model="product.rate_price" class="input" /> -->
                                {{ vendor_product.rate_unit * vendor_product.quantity }}
                            </td>
                            <td v-if="showColumn(11)">
                                <!-- <input v-model="product.rate_num" class="input" /> -->
                                {{
                                    vendor_product.rate == '0%'
                                        ? 0
                                        : (vendor_product.rate_unit * vendor_product.quantity * vendor_product.rate) /
                                          100
                                }}
                            </td>
                            <td
                                ><img
                                    class="clickable"
                                    @click="deleteProduct(vendor_productIndex)"
                                    src="../../../public/icons/delete.svg"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" class="add_product"><a v-on:click="addProduct">添加产品</a></td>
                        </tr>
                    </tbody>
                </table>

                <div class="row-terms">
                    <div class="col-terms-1">
                        <textarea
                            id="terms"
                            class="input textarea"
                            placeholder="摘要"
                            v-model="selectedVendor.description"
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
                </div>
            </div>

            <div id="OtherInfo" class="tabcontent" v-bind:style="{ display: infoDisplay }">
                <div class="even-columns group">
                    <div class="col">
                        <div class="col-label"><label for="purchase_rep" class="form-label">采购代表</label></div>
                        <div class="col-input"><input name="purchase_rep" class="input" type="text" /></div>
                    </div>
                    <div class="col">
                        <div class="col-label"><label for="payment_terms" class="form-label">付款条件</label></div>
                        <div class="col-input"><input name="payment_terms" class="input" type="text" /></div>
                    </div>
                </div>
                <div class="even-columns group">
                    <div class="col">
                        <div class="col-label"><label for="incoterm" class="form-label">术语</label></div>
                        <div class="col-input"><input name="incoterm" class="input" type="text" /></div>
                    </div>
                    <div class="col">
                        <div class="col-label"><label for="fiscal-position" class="form-label">财政状况</label></div>
                        <div class="col-input"><input name="fiscal-position" class="input" type="text" /></div>
                    </div>
                </div>
            </div>
            <div class="row form-buttons">
                <button class="btn btn-filled" @click="printFn()">打印</button>
                <button class="btn btn-filled" @click="openPopup()">导出Excel</button>
                <div class="right-buttons">
                    <button class="btn btn-clear" @click="clear()">取消</button>
                    <button class="btn btn-filled" @click="inquiryAddFn(1)">询价</button>
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
        <div>
            <div class="search-form-wrap">
                <a-row class="btn-top-wrap">
                    <a-col :span="12">
                        <!-- 按钮区域 -->
                        <ul class="btn-list-wrap">
                            <li>
                                <a-button v-if="isReply" type="primary" size="small" @click="openReply">
                                    回复
                                </a-button>
                                <a-button v-if="!isReply" type="primary" size="small" @click="openEdit">
                                    修改
                                </a-button>
                                <a-button type="danger" size="small" :disabled="isReply" @click="openDel">
                                    删除
                                </a-button>

                                <!-- 医院和经销商 -->
                                <a-button
                                    v-if="nowOrgType !== 3"
                                    type="primary"
                                    size="small"
                                    @click="handleOpenPriceModal"
                                >
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
                <a-row v-show="hightSearchObj.isArrow" class="hight-search-wrap">
                    <a-col :span="24" class="col-list">
                        <!-- 高级搜索 -->
                    </a-col>
                </a-row>
            </div>
            <div>
                <ul class="breadcrumb">
                    <li
                        ><span v-bind:class="{ turquoise: isDealerSelected }" v-on:click="moveToDealer()"
                            >经销商</span
                        ></li
                    >
                    <li>/</li>
                    <li
                        ><span v-bind:class="{ turquoise: isVendorSelected }" v-on:click="moveToVendor()"
                            >厂商</span
                        ></li
                    >
                </ul>
                <table class="commit-table" v-if="isVendorSelected">
                    <thead>
                        <th style="width: 100px">厂商/经销商<!-- We will merge Vendors and Dealers into one list that no need the above switch button to filter them --></th>
                        <th style="width: 100px">单据编号</th>
                        <th style="width: 100px">联系人</th>
                        <th style="width: 100px">电话</th>
                        <th style="width: 100px">询价时间</th>
                        <th style="width: 100px">回复时间</th>
                        <th style="width: 100px">状态</th>
                        <th style="width: 50px">操作</th>
                    </thead>
                    <tbody>
                        <tr
                            v-for="(commit_product, commit_productIndex) in vendorDatas"
                            :key="commit_productIndex"
                            v-on:click="openticket(commit_productIndex)"
                        >
                            <td style="height: 20px"
                                ><div @click="cellDblclickFn(commit_product)">{{ commit_product.partB_name }}</div></td
                            >
                            <td style="height: 20px">{{ commit_product.ilps_sn }}</td>
                            <td style="height: 20px">{{ commit_product.contacts }}</td>
                            <td style="height: 20px">{{ commit_product.phone }}</td>
                            <td style="height: 20px">{{ commit_product.ask_time }}</td>
                            <td style="height: 20px">{{ commit_product.answer_time }}</td>
                            <td style="height: 20px">{{ getState(commit_product.status) }}</td>
                            <td style="height: 20px">
                                <img
                                    class="clickable"
                                    @click="deleteDealerCommit(commit_productIndex)"
                                    src="../../../public/icons/delete.svg"
                                />
                            </td>
                        </tr>
                    </tbody>
                </table>
                <table class="commit-table" v-if="isDealerSelected">
                    <thead>
                        <th style="width: 100px">厂商/经销商<!-- We will merge Vendors and Dealers into one list that no need the above switch button to filter them --></th>
                        <th style="width: 100px">单据编号</th>
                        <th style="width: 100px">联系人</th>
                        <th style="width: 100px">电话</th>
                        <th style="width: 100px">询价时间</th>
                        <th style="width: 100px">回复时间</th>
                        <th style="width: 100px">状态</th>
                        <th style="width: 50px">操作</th>
                    </thead>
                    <tbody>
                        <tr
                            v-for="(commit_product, commit_productIndex) in dealersDatas"
                            :key="commit_productIndex"
                            v-on:click="openticket(commit_productIndex)"
                        >
                            <td style="height: 20px"
                                ><div @click="cellDblclickFn(commit_product)">{{ commit_product.partB_name }}</div></td
                            >
                            <td style="height: 20px">{{ commit_product.ilps_sn }}</td>
                            <td style="height: 20px">{{ commit_product.contacts }}</td>
                            <td style="height: 20px">{{ commit_product.phone }}</td>
                            <td style="height: 20px">{{ commit_product.ask_time }}</td>
                            <td style="height: 20px">{{ commit_product.answer_time }}</td>
                            <td style="height: 20px">{{ getState(commit_product.status) }}</td>
                            <td style="height: 20px">
                                <img
                                    class="clickable"
                                    @click="deleteVendorCommit(commit_productIndex)"
                                    src="../../../public/icons/delete.svg"
                                />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!-- tab切换区域开始 -->
            <div
                :class="hightSearchObj.isArrow ? 'top136' : 'top56'"
                class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
                style="position: unset; display: none"
            >
                <!-- 表格头部 -->
                <a-tabs @change="changeTabsFn" :activeKey="tabsActiveKey" type="card">
                    <a-tab-pane tab="询价回复" v-if="nowOrgType != 1" style="padding: 8px" :key="systemTypes[0]">
                        <vxe-grid
                            class="upper_table"
                            style="background-repeat: unset"
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
        </div>
    </div>
</template>

<script>
import { platformUnitTree, inquiryEditAPI, inquiryInfoAPI, platformUnitParents , getInquiryOrgListAPI, getVendorProductListAPI , inquiryDealerList, inquiryVendorList, inquiryUnreplyList, inquiryDelAPI , inquiryAddAPI } from '@/service/pageAjax';
import DropDownInput from '@/components/DropDownInput.vue';
import { BillsType } from '@/config/constants';
import ListPageList from '@/components/ListPageList.vue';
import searchItemsModal from '@/components/purchase/searchItemsModal.vue';
import exportExcel from '@/views/commonPage/buyManage/module/exportExcel.vue';
import Vue from 'vue';

export default {
    name: 'SuppliesClaim',
    components: {
        DropDownInput,
        searchItemsModal,
        exportExcel
    },
    data() {
        return {
            pageSize: 5,
            current: 1,
            isReply: false,
            nowOrgType: 2,
            searchKey: '',
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            object: [{}],
            part_b_obj: {},
            isDealerSelected: true,
            isVendorSelected: false,
            tabsActiveKey: 1,
            systemTypes: [1, 2, 3],
            contractStatus: ['询价草稿', '询价', '回复草稿', '已回复'],
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
            productsPagination: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1,
                pageSizes: [10, 20, 30, 40]
            },
            dealersDatas: [], // 经销商主体数据
            dealersDatas_sub: [],
            hospitalDatas: [], // 医院主体数据
            vendorDatas: [], // 厂商主体数据
            vendorDatas_sub: [],
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
            showInquiry: 2,
            vendor_products: [],
            commit_products: [],
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
                { name: '机构类型', width: 142 }
            ],
            productTableHeaders: [
                { name: '产品', width: 250 },
                { name: '规格型号', width: 150 },
                { name: '厂商', width: 200 },
                { name: '联系人', width: 200 },
                { name: '电话', width: 200 },
                { name: '数量', width: 130 },
                { name: '包装单位', width: 100 }
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
                ilps_sn: '',
                partA: '',
                partB: '',
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
                    dataIndex: 'name',
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
                    dataIndex: 'vendor_name',
                    minWidth: '200',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '厂商'
                },
                {
                    align: 'center',
                    dataIndex: 'quantity',
                    minWidth: '130',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '数量'
                },
                {
                    align: 'center',
                    dataIndex: 'unit_name',
                    minWidth: '100',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '包装单位'
                },
                {
                    align: 'center',
                    dataIndex: 'unit_price',
                    minWidth: '100',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '单价'
                },
                {
                    align: 'center',
                    dataIndex: 'sum',
                    minWidth: '100',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '金额'
                },
                {
                    align: 'center',
                    dataIndex: 'rate',
                    minWidth: '100',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '税率'
                },
                {
                    align: 'center',
                    dataIndex: 'rate_unit',
                    minWidth: '100',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '含税单价'
                },
                {
                    align: 'center',
                    dataIndex: 'rate_price',
                    minWidth: '100',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '含税金额'
                },
                {
                    align: 'center',
                    dataIndex: 'rate_num',
                    minWidth: '100',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '税额'
                },
                {
                    align: 'center',
                    dataIndex: 'inquirer_name',
                    minWidth: '60',
                    showHeaderOverflow: 'true',
                    showOverFlow: 'true',
                    title: '操作'
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
            }
        };
    },
    watch: {
        pageSize(val) {
            console.log('pageSize', val);
        },
        current(val) {
            console.log('current', val);
        },
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 253;
            return clientHeight - topHeight;
        },
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
        },
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 253;
            return clientHeight - topHeight;
        }
    },
    methods: {
        onDealerPageNumberChange(page, pageSize) {
            console.log(this.dealersDatas.length);
            this.dealersDatas_sub=[];
            for(var i=(page-1)*pageSize; i<page*pageSize; i++) {
                this.dealersDatas_sub.push(this.dealersDatas[i]);
            }
        },
        onVendorPageNumberChange(page, pageSize) {
            console.log(this.vendorDatas.length);
            this.vendorDatas_sub=[];
            for(var i=(page-1)*pageSize; i<page*pageSize; i++) {
                this.vendorDatas_sub.push(this.vendorDatas[i]);
            }
        },
        onShowSizeChange(current, pageSize) {
            console.log(current, pageSize);
        },
        printFn() {
            // console.log(this.products);
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
        openticket(index) {
            // this.vendor_products.push(this.commit_products[index]);
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
        addProduct: function () {
            console.log(this.vendor_products);
            var sn = this.vendor_products.length;
            var serial = '';
            for (let i = 0; i < 4 - sn.toString().length; i++) {
                serial = serial + '0';
            }
            serial = serial + sn;
            let date = new Date().getFullYear() + '' + (new Date().getMonth() + 1) + '' + new Date().getDate();
            let ilps_sn = 'XJ' + date + serial;
            this.vendor_products.push({
                name: '',
                description: '',
                vendor_name: this.selectedVendor.org_name,

                quantity: 0,
                price: 0,
                rate: '0%',
                rate_unit: 1
                // tax: 0,
            });
            console.log(this.vendor_products);
            this.commit_products = this.vendor_products;
            // window.localStorage.setItem('vendor_products', JSON.stringify(this.vendor_products));
        },
        async loadVendors(name) {
            let params = {};
            params.name = name ?? '';
            params.pageSize = this.searchModalPage.pageSize;
            params.pageNum = this.searchModalPage.current;
            console.log(params);

            let type = 2;
            await Promise.all([getInquiryOrgListAPI(2, params), getInquiryOrgListAPI(3, params)]).then((results) => {
                console.log(results);
                if (results[0].code === 0 || results[1].code === 0) {
                    this.vendors = [];
                    Vue.prototype.$message.success('已加载厂商/经销商');
                }

                for (let r of results) {
                    if (parseFloat(r.code) === 0) {
                        this.vendors = this.vendors.concat(
                            r.rows.map((item) => {
                                item.systemType = parseInt(type);
                                item.org_type_name = this.parseOrgNameFromBillsType(item.billsType);
                                item.contact_person = this.selectedVendor.contact_person;
                                item.phone_number = this.selectedVendor.phone_number;
                                return item;
                            })
                        );
                    }
                }
                console.log(this.vendors);
            });

            this.vendorNames = this.vendors.map((x) => {
                return {
                    id: x.org_id,
                    name: x.org_name
                };
            });
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
            console.log(this.vendors);
            this.selectedVendor = this.vendors.find((v) => v.org_name === vendor.org_name);
            this.selectedVendor = this.vendors[index];
            this.loadAllProductsOfVendor();
        },

        vendorNameSelected: function (vendor, originalIndex) {
            console.log('Vendor selected: ');
            this.selectedVendor = this.vendors.find((v) => v.org_id === vendor.id);
            this.loadAllProductsOfVendor();
        },

        // Also used in enquiryList.vue
        async loadAllProductsOfVendor(name) {
            console.log('Selected vendor: ');
            console.log(this.selectedVendor);
            let params = {};
            debugger;
            if (this.selectedVendor.org_id == null) return;
            params.name = name ?? '';
            params.org_id = this.selectedVendor.org_id;
            params.vendor_inner_sn = this.vendorSearchData.vendor_inner_sn;
            params.specification = this.vendorSearchData.specification;

            params.type_inner_sn = this.vendorSearchData.type_inner_sn.length
                ? this.vendorSearchData.type_inner_sn[this.vendorSearchData.type_inner_sn.length - 1]
                : '';
            params.pageSize = this.searchModalPage.pageSize;
            params.pageNum = this.searchModalPage.current;
            params.code68_sn = this.vendorSearchData.code68_sn.length
                ? this.vendorSearchData.code68_sn[this.vendorSearchData.code68_sn.length - 1]
                : '';
            await getVendorProductListAPI({ params }).then((res) => {
                console.log(params);
                console.log(res);
                if (parseFloat(res.code) === 0) {
                    console.log('Original products: ');
                    for (var i = 0; i < res.rows.length; i++) {
                        res.rows[i].idx = i + 1;
                    }
                    console.log(res.rows);
                    this.products = res.rows.map((item) => {
                        item.unit_name = item.min_unit;
                        item.unit_inner_sn = item.min_unit_inner_sn;
                        item.unitList =
                            item.unit_name && item.unit_inner_sn
                                ? [{ label: item.unit_name, value: item.unit_inner_sn }]
                                : [];
                        item.unit_inner_sn = [item.unit_inner_sn];
                        item.rate = '0%';
                        item.rate_unit = 1;
                        item.produce_date =
                            new Date().getFullYear() +
                            '-' +
                            (new Date().getMonth() + 1) +
                            '-' +
                            new Date().getDate() +
                            ' ' +
                            new Date().getHours() +
                            ':' +
                            new Date().getMinutes() +
                            ':' +
                            new Date().getSeconds();
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
                            overdue_date: '',
                            type: '0',
                            // vendor_orgName: this.selectedVendor.org_name,
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
                    var sn;
                    for (var i = 0; i < this.products.length; i++) {
                        var serial = '';
                        for (var j = 0; j < 4 - i.toString().length; j++) {
                            serial = serial + '0';
                        }
                        serial = serial + i;
                        var date =
                            new Date().getFullYear() + '' + (new Date().getMonth() + 1) + '' + new Date().getDate();
                        // this.products[i].ilps_sn='XJ'+date+serial;
                    }
                    console.log(this.products);
                    this.searchModalPage.total = res.total;
                    console.log('Total products: ' + this.searchModalPage.total);
                    this.searchModalPage.loading = false;
                    // this.$refs.venTable.refreshColumn();
                    this.calculateTotal();
                    Vue.prototype.$message.success('已检索产品');
                }
            });

            this.products.forEach((item, idx) => {
                this.venUnitTreeFn(item, idx);
            });
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
        async venUnitTreeFn(record, idx) {
            // 厂商商产品单位
            let oid = record.vendor_inner_sn;
            let pid = record.product_inner_sn;
            let psid = record.specification_inner_sn;
            const self = this;
            await platformUnitTree(oid, pid, psid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let circle = (item, parentName) => {
                        // 递归遍历
                        item.forEach((item) => {
                            item.label = `${item.name}(${item.measure}×${parentName})`;
                            item.value = `${item.unit_inner_sn}`;
                            if (item.children && item.children.length > 0) {
                                circle(item.children, item.name);
                            } else {
                                return;
                            }
                        });
                    };
                    res.tree.forEach((item) => {
                        item.label = item.name;
                        item.value = `${item.unit_inner_sn}`;
                        if (item.children && item.children.length > 0) {
                            circle(item.children, item.name);
                        } else {
                            return;
                        }
                    });

                    Vue.set(self.products[idx], 'packing', [res.tree[0]]);
                }
            });
        },
        productSelected(product, index, originalItemIndex) {
            this.vendor_products[originalItemIndex] = this.products[index];
            this.vendor_products.push({});
            this.vendor_products.pop();
        },
        productNameSelected(product, originalIndex) {
            // let selectedProduct = this.products.find((p, index) => { index === product.id });
            let selectedProduct = {};
            for (let i = 0; i < this.products.length; i++) {
                if (i === product.id) {
                    selectedProduct = this.products[i];
                    break;
                }
            }
            this.vendor_products[originalIndex] = Object.assign({}, selectedProduct);
            console.log(this.vendor_products);
        },
        showVendorSearchModal(index) {
            this.modalHeading = '厂商/经销商';
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
            // this.loadAllProductsOfVendor();
            console.log(this.isVendorModalVisible);
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
            this.loadAllProductsOfVendor('');
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
            this.vendor_products.splice(index, 1);
        },
        deleteVendorCommit: function (index) {
            this.vendorDatas.splice(index, 1);
            console.log(this.dealersDatas);
        },
        deleteDealerCommit: function (index) {
            this.dealersDatas.splice(index, 1);
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
        displayRender(rd) {
            let len = rd.labels.length;
            const label = rd.labels[len - 1];
            return label.split('(')[0];
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
        commitshow() {},
        clear() {
            this.products = [];
            window.localStorage.removeItem('vendor_products');
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
                // this.editMd.alert = true;
                if (this.$refs.enquiryListEditMd) {
                    this.$refs.enquiryListEditMd.refreshPage();
                }
            }
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
        handleOpenPriceModal() {
            this.priceMdKey = new Date().getTime(); // 刷新组件状态
            this.$Utils.globalDragCenterFn('inquiry-price-md');
            this.priceMd.alert = true;
        },
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
        changeTabsFn(activeKey) {
            this.tabsActiveKey = parseFloat(activeKey);
            if (this.tabsActiveKey === 1) {
                this.isReply = true;
            } else {
                this.isReply = false;
            }
            this.currentTrObj = {};
        },
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
        async cellDblclickFn(row) {
            console.log(row);
            let obj = row;
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
                let params = {
                    partA: this.currentTrObj.partA,
                    ilps_inner_sn: this.currentTrObj.ilps_inner_sn,
                    billsType: this.currentTrObj.billsType
                };
                let type = parseFloat(this.editObj.status);
                console.log('type', type);
                if (type === 3) {
                    this.preview = true;
                } else {
                    this.preview = false;
                }
                await inquiryInfoAPI({ params }).then((res) => {
                    console.log(res);
                    if (parseInt(res.code) === 0) {
                        this.oldFormData = JSON.parse(JSON.stringify(res.info));
                        this.formData = res.info;
                        // 如果是询价回复，partA_name，其它partB_name
                        this.orgName =
                            this.editObj && this.editObj.systemType === 1 ? res.info.partA_name : res.info.partB_name;
                        this.part_b_obj.id = res.info.partB;
                        this.part_b_obj.name = res.info.partB_name;
                        this.part_b_obj.systemType = this.editObj.systemType;
                        this.part_b_obj.modalType = 6;
                        this.part_b_obj.billsType = res.info.billsType;
                        this.vendor_products = [];
                        res.info.detailList.forEach((item, idx) => {
                            if (isEdit === false) {
                                item.readOnly = true;
                            } else {
                                item.readOnly = false;
                            }
                            item.isActive = '';
                            item.key = Math.random();
                            item.idx = idx + 1;
                            item.name = item.product_name;
                            item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额
                            item.rate = parseInt(item.rate * 100); // 税率
                            item.rate_unit = item.unit_price; // 含税单价 = 单价 【默认值】
                            item.unit_price = parseFloat(item.rate_unit) / (1 + parseFloat(item.rate) / 100); // 单价 = 含税单价 / (1 + 税率 / 100)
                            // item.discount_price = parseFloat(item.discount_unit) * parseFloat(item.quantity) // 折后金额 =
                            item.sum = parseFloat(item.quantity) * parseFloat(item.unit_price); // 金额 = 数量 * 单价
                            // item.discount_unit = parseFloat(item.unit_price) * parseFloat(item.discount) // 折后单价 = 单价 * 折扣
                            item.rate_price = parseFloat(item.quantity) * parseFloat(item.rate_unit); // 含税金额 = 数量 * 含税单价
                            item.rate_num = parseFloat(item.rate_price) - parseFloat(item.sum); // 税额 = 含税金额 - 金额
                            item.isEdit = true;
                            item.unit_data = {
                                key: [item.unit_inner_sn],
                                data: [
                                    {
                                        value: item.unit_inner_sn,
                                        label: item.unit_name
                                    }
                                ]
                            };
                            this.vendor_products.push(item);

                            console.log(this.vendor_products);
                        });
                    }
                });
            }
            this.clearSelect();
        },
        changeInquiry(e) {
            if (e.target.checked) {
                this.showInquiry = 1;
            } else {
                this.showInquiry = 0;
            }
            this.loadTableHos();
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
                    for(var i=0; i<this.pageSize; i++) {
                        this.dealersDatas_sub.push(this.dealersDatas[i]);
                    }
                    if (this.dealersDatas.length > 0) {
                        if (chooseObj) {
                            // 选中新增/编辑的项
                            this.currentTrObj = this.dealersDatas[this.dealersDatas.length - 1];
                        } else {
                            this.currentTrObj = this.dealersDatas[0]; // 默认选中第一
                        }
                        this.editObj = this.currentTrObj;
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
                    for(var i=0; i<this.pageSize; i++) {
                        this.vendorDatas_sub.push(this.vendorDatas[i]);
                    }
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
        async inquiryAddFn(type) {
            // 新增询价单据 type 0:草稿 1:询价
            console.log(this.formData);
            console.log(this.vendor_products);
            for (var i = 0; i < this.vendor_products.length; i++) {
                this.vendor_products[i].unit_price =
                    this.vendor_products[i].rate == '0%'
                        ? this.vendor_products[i].rate_unit
                        : this.vendor_products[i].rate_unit -
                          (this.vendor_products[i].rate_unit * this.vendor_products[i].rate) / 100;
                this.vendor_products[i].sum =
                    this.vendor_products[i].rate == '0%'
                        ? this.vendor_products[i].rate_unit
                        : this.vendor_products[i].rate_unit -
                          (this.vendor_products[i].rate_unit * this.vendor_products[i].rate) / 100;
                this.vendor_products[i].rate = this.vendor_products[i].rate == '0%' ? 0 : this.vendor_products[i].rate;
                this.vendor_products[i].rate_price =
                    this.vendor_products[i].rate_unit * this.vendor_products[i].quantity;
                this.vendor_products[i].rate_num =
                    this.vendor_products[i].rate == '0%'
                        ? 0
                        : (this.vendor_products[i].rate_unit *
                              this.vendor_products[i].quantity *
                              this.vendor_products[i].rate) /
                          100;
                this.vendor_products[i]._XID = 'row_140';
                this.vendor_products[i].unit_name = this.vendor_products[i].common_use_unit;
                this.vendor_products[i].temp_add_flag = true;
                this.vendor_products[i].discount = 1;
                this.vendor_products[i].row_add_flag = true;
                this.vendor_products[i].isloadingUnit = false;
                this.vendor_products[i].isNew = true;
                this.vendor_products[i].sys_type = this.vendor_products[i].vendor_name;
                console.log(this.vendor_products[i].unit_data);
                this.vendor_products[i].unit_data = { data: this.vendor_products[i].packing };
                delete this.vendor_products[i].packing;
                delete this.vendor_products[i].unit_inner_sn;
                this.vendor_products[i].unit_inner_sn = 1;
            }

            this.formData.partB = 2;
            this.formData.billsType = 'HV';
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
            if (dataObj.partB === '' || !dataObj.partB) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择单位名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            // 添加合同产品验证
            dataObj.detailList = JSON.parse(JSON.stringify(this.vendor_products));
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
            dataObj.contacts = this.selectedVendor.contact_person;
            dataObj.phone = this.selectedVendor.phone_number;
            console.log(dataObj);
            await inquiryAddAPI(dataObj).then((res) => {
                console.log(res);
                if (parseFloat(res.code) === 0) {
                    this.object = res.info;
                    this.object.isActive = '';
                    this.object.key =
                        this.object.billsType +
                        '' +
                        this.object.ilps_inner_sn +
                        '' +
                        this.object.partA +
                        '' +
                        this.object.partB;
                    this.object.sys_type = partB;
                    this.$emit('done', this.object);
                }
            });
        },
        moveToDealer() {
            this.isDealerSelected = true;
            this.isVendorSelected = false;
        },
        moveToVendor() {
            this.isVendorSelected = true;
            this.isDealerSelected = false;
        },
        getState(status) {
            return this.contractStatus[status];
        }
    },
    created() {
        console.log('refresh');
        var vendor_products = window.localStorage.getItem('vendor_products');
        this.commit_products = JSON.parse(vendor_products);
        this.nowOrgType = parseFloat(this.$cookie.get('userSystemType'));
        if (this.nowOrgType === 1) {
            // 医院
            this.tabsActiveKey = 2;
        }
        if (this.tabsActiveKey === 1) {
            this.isReply = true;
        }
        this.loadTable();
    },
    mounted() {

        const oid = parseInt(this.$cookie.get('userbelong'));
        this.formData.partA = oid;
        this.loadVendors();
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
r,
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
    background-color: white ;
}
</style>
r,
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
    background-color: white ;
}
</style>
r,
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
    background-color: white ;
}
.ant-pagination {
    position:absolute;
    right:0;
}
</style>
