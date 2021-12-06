<template>
    <div class="scoped" style="margin: -24px; height: 576px; position: relative">
        <div class="search-box">
            <div class="category-wrap">
                <ul class="category-ul">
                    <li>
                        <span class="label"><em class="em red">*</em>名称</span>
                        <div class="input_box">
                            <a-input type="text" v-model="formData.name" placeholder="" />
                        </div>
                    </li>
                    <li>
                        <span class="label"><em class="em red">*</em>类别</span>
                        <div class="input_box">
                            <a-select v-model="formData.type_inner_sn" style="width: 200px" @change="changeSel">
                                <a-select-option value="0">颅骨类</a-select-option>
                                <a-select-option value="1">脊柱类</a-select-option>
                                <a-select-option value="2">关节类</a-select-option>
                                <a-select-option value="3">创伤类</a-select-option>
                            </a-select>
                        </div>
                    </li>
                    <li>
                        <span class="label"><em class="em red">*</em>品牌</span>
                        <div class="input_box">
                            <a-select
                                v-model="defaultBrand"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                style="width: 200px; display: inline-block; vertical-align: bottom"
                                @change="changeBrand"
                            >
                                <a-select-option
                                    v-for="(item, key) of brandList"
                                    :key="key"
                                    :value="item.brand_inner_sn"
                                    >{{ item.name }}</a-select-option
                                >
                            </a-select>
                            <a-tooltip placement="topLeft" title="新增品牌">
                                <a-button
                                    :disabled="isEditDisabledAddInfo"
                                    @click="addBrandFn"
                                    icon="plus"
                                    style="height: 24px; padding: 0 8px; margin-left: 10px; vertical-align: bottom"
                                ></a-button>
                            </a-tooltip>
                        </div>
                    </li>
                </ul>
                <ul class="category-ul">
                    <li v-if="userSystemType === 2">
                        <span class="label"><em class="em"></em>关键字</span>
                        <div class="input_box">
                            <a-input
                                class="text_a"
                                v-model="formData.keywords"
                                type="text"
                                :maxLength="100"
                                placeholder="默认为名称的拼音首字母"
                            />
                        </div>
                    </li>
                    <li>
                        <span class="label"><em class="em"></em>编号</span>
                        <div class="input_box">
                            <a-input type="text" v-model="formData.kit_sn" placeholder="" />
                        </div>
                    </li>
                </ul>
                <ul class="category-ul">
                    <li>
                        <span class="label"><em class="em"></em>产品描述</span>
                        <div class="input_box">
                            <a-input type="text" v-model="formData.description" placeholder="" />
                        </div>
                    </li>
                </ul>
            </div>
        </div>
        <div class="tab-table-wrap">
            <div @dblclick="openAddPro">
                <vxe-grid
                    ref="xTable"
                    size="small"
                    border
                    show-overflow
                    show-header-overflow
                    showHeader
                    :columns="toolColumns"
                    height="426"
                    resizable
                    auto-resize
                    highlight-current-row="false"
                    align="center"
                    :data="toolTableData"
                    :loading="toolPage.loading"
                >
                    <template v-slot:empty>
                        <div>双击此处添加工具！</div>
                    </template>
                </vxe-grid>
            </div>
        </div>
        <a-modal
            :title="addProductMd.title"
            :visible="addProductMd.alert"
            :maskClosable="false"
            :mask="false"
            :width="1000"
            :centered="true"
            @cancel="addProductMd.alert = false"
            class="modal-form-input-scoped global-drag"
            :class="addProductName"
        >
            <div
                v-if="addProductName == 'add_product_ct'"
                v-globalDrag="{ el: 'add_product_ct' }"
                class="modal-header-wrap"
            >
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addProductMd.title }}
                </h6>
            </div>
            <div
                v-if="addProductName == 'add_product_cp'"
                v-globalDrag="{ el: 'add_product_cp' }"
                class="modal-header-wrap"
            >
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addProductMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="addProductMd.alert = false">关闭</a-button>
                <a-button :loading="addProductMd.loading" @click="addProductFn" type="primary">添加</a-button>
            </template>
            <add-product ref="chooseProMd" @returnData="getProReturn" :partB="part_b_obj"></add-product>
        </a-modal>
        <!-- 新增类别区域开始 -->
        <a-modal
            v-model="addGrnTypeModal.alert"
            :centered="true"
            :maskClosable="false"
            class="scoped modal-form-input-scoped modal-brand-md global-drag"
            :width="600"
        >
            <div v-globalDrag="{ el: 'modal-brand-md' }" class="modal-header-wrap">
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
                    <!--头部区域开始-->
                    <div class="search-form-wrap" style="padding: 0; border: none">
                        <a-row class="btn-top-wrap">
                            <a-col :span="24">
                                <!-- 按钮区域 -->
                                <ul class="btn-list-wrap">
                                    <li>
                                        <a-button @click="grnTypeAddBtnFn('add')" type="primary" size="small">
                                            新增
                                        </a-button>
                                        <a-button @click="grnTypeAddBtnFn('edit')" type="primary" size="small">
                                            修改
                                        </a-button>
                                        <a-button
                                            :disabled="userSystemType === 2"
                                            @click="delBrandFn()"
                                            type="danger"
                                            size="small"
                                        >
                                            删除
                                        </a-button>
                                    </li>
                                </ul>
                            </a-col>
                        </a-row>
                    </div>
                    <!--头部区域结束-->

                    <!-- tab切换区域开始 -->
                    <div class="tab-table-wrap cur-overflow-hidden tab-table-overflowy" style="position: static">
                        <!-- 表格头部 -->
                        <a-table
                            :loading="brandAddLoading"
                            :columns="brandAddColumns"
                            :dataSource="brandAddDatas"
                            :pagination="false"
                            :rowClassName="rowClassGrnTypeAddFn"
                            :scroll="{ y: 500 }"
                            :customRow="customRowGrnTypeAddFn"
                        >
                            <!--状态重写-->
                            <div slot="status_text" slot-scope="text">
                                <span v-if="text == '无效'" style="color: red">{{ text }}</span>
                                <span v-else>{{ text }}</span>
                            </div>
                        </a-table>
                        <!--表格区域-->
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
            class="scoped modal-form-input-scoped modal-brand-list global-drag"
            :width="500"
        >
            <div v-globalDrag="{ el: 'modal-brand-list' }" class="modal-header-wrap">
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
                        <span class="label" style="width: 54px"><em class="em red">*</em>品牌</span>
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
    </div>
</template>

<script>
import addProduct from '@/components/addProduct';
import {
    platformUnitTree,
    surgeryKitInfoAPI,
    surgeryBrandListAPI,
    surgeryBrandAddAPI,
    surgeryBrandEditAPI,
    surgeryBrandDelAPI,
    platformUnitParents
} from '@/service/pageAjax';

export default {
    name: 'createToolsKits',
    props: ['isCopy', 'editObj'],
    components: {
        addProduct
    },
    data() {
        return {
            formData: {
                name: '',
                kit_sn: '',
                description: '',
                type_inner_sn: '',
                brand_inner_sn: '',
                details: [],
                keywords: ''
            },
            activeTab: 'stuff',
            part_b_obj: {},
            tabName: [
                { name: '材料', key: 'stuff' },
                { name: '工具', key: 'tool' }
            ],
            toolPage: { loading: false },
            stuffPage: { loading: false },
            addProductName: '',
            toolColumns: [
                {
                    title: 'NO.',
                    dataIndex: 'sort_number',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '50',
                    align: 'center',
                    slots: {
                        default: ({ row, seq }) => {
                            let cls = '';
                            if (!row.detail_inner_sn) {
                                cls = 'red-dot-add';
                            }
                            let html = [
                                <div>
                                    <i class={cls}></i>
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
                    minWidth: '230',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.name];
                        }
                    }
                },
                {
                    title: '产品编码',
                    dataIndex: 'specification_vendor_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification_vendor_sn];
                        }
                    }
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '200',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification];
                        }
                    }
                },
                {
                    title: '厂商',
                    dataIndex: 'product_vendor_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '150',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.product_vendor_name];
                        }
                    }
                },
                {
                    title: '数量',
                    dataIndex: 'quantity',
                    minWidth: '100',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            let obj;
                            this.toolTableData.forEach((item) => {
                                if (item.key === record.key) {
                                    obj = item;
                                }
                            });
                            let changeInput = (num) => {
                                obj.quantity = num;
                            };
                            let dom;
                            if (obj.quantity && obj.quantity !== '') {
                                dom = [<a-input-number min={0} v-model={obj.quantity} class="input" />];
                            } else {
                                dom = [<a-input-number min={0} class="input" onChange={changeInput} />];
                            }
                            return dom;
                        }
                    }
                },
                {
                    title: '包装单位',
                    dataIndex: 'unit_inner_sn',
                    minWidth: '100',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            if (record.unit_data) {
                                let val = record.unit_data.key;
                                let tree = record.unit_data.data;
                                let displayRender = (rd) => {
                                    let len = rd.labels.length;
                                    const label = rd.labels[len - 1];
                                    return label ? label.split('(')[0] : '';
                                };
                                let change = (value, arr) => {
                                    record.unit_data.key = value;
                                    record.unit_inner_sn = value[value.length - 1];
                                    record.unit_name = arr[arr.length - 1].name;
                                    record.isNew = false;
                                    let newData = this.toolTableData;
                                    this.toolTableData = [];
                                    this.toolTableData = newData;
                                };
                                let clickFn = () => {
                                    if (record.isEdit) {
                                        this.venUnitTreeFn(record, 'edit');
                                        record.isEdit = false;
                                    }
                                };
                                return [
                                    <a-cascader
                                        onClick={clickFn}
                                        allowClear={false}
                                        options={tree}
                                        value={val}
                                        displayRender={displayRender}
                                        expandTrigger="hover"
                                        placeholder=""
                                        onChange={change}
                                        changeOnSelect
                                    />
                                ];
                            }
                        }
                    }
                },
                {
                    title: '操作',
                    dataIndex: 'inquirer_name',
                    minWidth: '70',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            let record = row;
                            let delFn = () => {
                                this.toolTableData.forEach((item, index) => {
                                    if (item.key === record.key) {
                                        this.toolTableData.splice(index, 1);
                                    }
                                });
                                this.toolTableData.forEach((item, index) => {
                                    item.sort_number = index + 1;
                                });
                            };
                            return [
                                <a-tooltip placement="top" title="删除">
                                    <i onClick={delFn} class="yrt yrt-shanchu-copy cancel del_ico"></i>
                                </a-tooltip>
                            ];
                        }
                    }
                }
            ],
            toolTableData: [],
            stuffTableData: [],
            unitList: [],
            brandList: [],
            addProductMd: {
                title: `${this.$route.meta.title} - 新增 - 添加产品`,
                alert: false,
                loading: false
            },
            addGrnTypeModal: {
                title: `${this.$route.meta.title} - 品牌`,
                alert: false,
                loading: false
            },
            addGrnTypeListModal: {
                title: `${this.$route.meta.title} - 品牌 - 修改`,
                alert: false,
                loading: false
            },
            grnTypeAddForm: {
                name: '',
                status: 1,
                checked: true
            },
            currentBrandTrObj: {},
            brandAddLoading: false,
            brandAddColumns: [
                {
                    title: '品牌',
                    dataIndex: 'name',
                    key: 'name',
                    width: '100',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    width: '80',
                    align: 'center',
                    scopedSlots: { customRender: 'status_text' }
                }
            ],
            brandAddDatas: [],
            defaultBrand: []
        };
    },
    methods: {
        async pageRefresh() {
            this.getBrandList();
            if (!this.isCopy) {
                this.formData = {
                    name: '',
                    kit_sn: '',
                    description: '',
                    type_inner_sn: '',
                    brand_inner_sn: '',
                    details: [],
                    keywords: ''
                };
                this.defaultBrand = [];
                this.toolTableData = [];
                this.addProductName = 'add_product_ct';
            } else {
                this.addProductName = 'add_product_cp';
                this.toolTableData = [];
                await surgeryKitInfoAPI(this.editObj.kit_inner_sn).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.oldFormData = JSON.parse(JSON.stringify(res.info));
                        this.oldFormData.type_inner_sn.toString();
                        this.formData = {
                            name: res.info.name,
                            kit_sn: res.info.kit_sn,
                            description: res.info.description,
                            type_inner_sn: res.info.type_inner_sn.toString(),
                            brand_inner_sn: res.info.brand_inner_sn,
                            details: [],
                            keywords: res.info.keywords || ''
                        };
                        this.defaultBrand = res.info.brand_name;
                        res.info.details.forEach((item, idx) => {
                            item.isActive = '';
                            item.key = Math.random();
                            item.idx = idx + 1;
                            item.name = item.product_name;
                            item.product_vendor_inner_sn = item.product_vendor_inner_sn || item.vendor_inner_sn;
                            item.product_vendor_name = item.product_vendor_name || item.vendor_name;
                            item.isEdit = true;
                            item.isNew = false;
                            item.unit_data = {
                                key: [item.unit_inner_sn],
                                data: [
                                    {
                                        value: item.unit_inner_sn,
                                        label: item.unit_name
                                    }
                                ]
                            };
                            this.toolTableData.push(item);
                        });
                    }
                });
            }
            this.activeTab = 'stuff';
            const oid = parseInt(this.$cookie.get('userbelong'));
            this.part_b_obj.systemType = 3;
            this.part_b_obj.modalType = 5;
            this.part_b_obj.id = oid;
        },
        returnData() {
            let data = this.formData;
            data.name = this.$Utils.trim(data.name);
            if (data.name === '' || !data.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (data.type_inner_sn === '' || !data.type_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择类别',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (data.brand_inner_sn === '' || !data.type_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择品牌',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            let details = this.toolTableData.concat(this.stuffTableData);
            if (details.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请双击添加工具',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (this.checkSingleProFn() && this.checkGroupProFn(details)) {
                data.details = details;
                this.$emit('returnData', data);
            }
        },
        async getBrandList(chooseObj) {
            // 获取品牌列表
            await surgeryBrandListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    let newList = [];
                    res.list.forEach((item) => {
                        item.key = item.brand_inner_sn;
                        if (parseInt(item.status) === 1) {
                            item.status_text = '有效';
                            newList.push(item);
                        } else {
                            item.status_text = '无效';
                        }
                    });
                    this.brandList = newList;
                    this.brandAddDatas = res.list;
                    if (chooseObj) {
                        chooseObj.key = chooseObj.brand_inner_sn;
                        if (parseInt(chooseObj.status) === 1) {
                            chooseObj.status_text = '有效';
                        } else {
                            chooseObj.status_text = '无效';
                        }
                        this.currentBrandTrObj = chooseObj;
                    }
                }
            });
        },
        changeBrand(val) {
            // 切换类型
            this.formData.brand_inner_sn = val;
            this.defaultBrand = val;
        },
        addBrandFn() {
            this.addGrnTypeModal.alert = true;
            this.$Utils.globalDragCenterFn('modal-brand-md');
        },
        grnTypeAddBtnFn(btnName) {
            if (btnName !== 'add') {
                let bid = this.currentBrandTrObj.brand_inner_sn;
                if (!bid) {
                    this.$warning({
                        title: '操作提示',
                        content: this.$Utils.tips().noSelected,
                        okText: '知道了',
                        centered: true,
                        mask: false
                    });
                    return;
                }
            }
            this.btnNameAddGrnTypeList = btnName;
            switch (btnName) {
                case 'add': {
                    this.addGrnTypeListModal = {
                        title: `${this.$route.meta.title} - 品牌 - 新增`,
                        alert: true,
                        loading: false
                    };
                    this.grnTypeAddForm = {
                        name: '',
                        checked: true,
                        status: 1
                    };
                    this.addGrnTypeListModal.alert = true;
                    this.$Utils.globalDragCenterFn('modal-brand-list');
                    break;
                }
                case 'edit': {
                    const status = parseFloat(this.currentBrandTrObj.status);
                    this.grnTypeAddForm = {
                        name: this.currentBrandTrObj.name,
                        checked: status === 1,
                        status
                    };
                    this.addGrnTypeListModal = {
                        title: `${this.$route.meta.title} - 品牌 - 修改`,
                        alert: true,
                        loading: false
                    };
                    this.$Utils.globalDragCenterFn('modal-brand-list');
                    break;
                }
                case 'remove': {
                    const _this = this;
                    _this.$confirm({
                        title: '删除',
                        content: '确认要执行该操作吗？',
                        centered: true,
                        maskClosable: false,
                        mask: false,
                        onOk() {
                            _this.grnTypeDelAPIFn();
                        }
                    });
                    break;
                }
            }
        },
        changeGrnTypeAddFormFn(e) {
            const checkedVal = e.target.checked;
            this.grnTypeAddForm.status = checkedVal ? 1 : 0;
            this.grnTypeAddForm.checked = checkedVal;
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
                    let name = this.$Utils.trim(this.grnTypeAddForm.name);
                    if (name && this.$Utils.checkInputValFn(name)) {
                        this.$warning({
                            title: '操作提示',
                            content: '品牌中请不要输入特殊字符',
                            centered: true,
                            okText: '知道了',
                            maskClosable: false
                        });
                        return false;
                    }
                    if (!name || name === '') {
                        this.$warning({
                            title: '操作提示',
                            content: '品牌输入不能为空',
                            okText: '知道了',
                            centered: true,
                            mask: false
                        });
                        return false;
                    } else {
                        this.addGrnTypeListModal.loading = true;
                        switch (this.btnNameAddGrnTypeList) {
                            case 'add':
                                this.grnTypeAddAPIFn();
                                break;
                            case 'edit':
                                this.grnTypeEditAPIFn();
                                break;
                        }
                    }
                    break;
                }
            }
        },
        async grnTypeAddAPIFn() {
            // 添加品牌
            let params = this.grnTypeAddForm;
            await surgeryBrandAddAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.addGrnTypeListModal.loading = false;
                    this.addGrnTypeListModal.alert = false;
                    this.getBrandList(res.info);
                } else {
                    this.addGrnTypeListModal.loading = false;
                    this.$confirm({
                        title: '提示',
                        content: res.msg,
                        centered: true,
                        maskClosable: false,
                        mask: false
                    });
                }
            });
        },
        async grnTypeEditAPIFn() {
            // 编辑品牌
            let bid = this.currentBrandTrObj.brand_inner_sn;
            let params = {
                brand_inner_sn: bid,
                name: this.grnTypeAddForm.name,
                name_old: this.grnTypeAddForm.name,
                status: this.grnTypeAddForm.status,
                status_old: this.grnTypeAddForm.status_old
            };
            await surgeryBrandEditAPI(params)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.addGrnTypeListModal.loading = false;
                        this.addGrnTypeListModal.alert = false;
                        this.currentBrandTrObj.status = params.status;
                        this.currentBrandTrObj.name = params.name;
                        if (params.status === 0 && this.formData.brand_inner_sn === bid) {
                            this.formData.brand_inner_sn = '';
                        }
                        this.getBrandList();
                    } else {
                        if (res.detail && res.detail.indexOf('uk_orthopaedic_ois_brand_name') > -1) {
                            this.$warning({
                                title: '操作提示',
                                content: `不能和已经存在的品牌名称一样`,
                                okText: '知道了',
                                centered: true
                            });
                        } else {
                            this.$message.error(res.msg);
                        }
                    }
                })
                .finally(() => {
                    this.addGrnTypeListModal.loading = false;
                });
        },
        async delBrandFn() {
            // 删除
            let bid = this.currentBrandTrObj.brand_inner_sn;
            if (!bid) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    mask: false
                });
                return;
            }
            await surgeryBrandDelAPI(bid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.addGrnTypeListModal.loading = false;
                    this.addGrnTypeListModal.alert = false;
                    this.getBrandList();
                    this.this.currentBrandTrObj = {};
                }
            });
        },
        rowClassGrnTypeAddFn(record) {
            if (record.key === this.currentBrandTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        customRowGrnTypeAddFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentBrandTrObj = record;
                        this.rowClassGrnTypeAddFn(record);
                    },
                    dblclick: () => {
                        this.currentBrandTrObj = record;
                        this.rowClassGrnTypeAddFn(record);
                        this.$Utils.globalDragCenterFn('modal-brand-list');
                        this.grnTypeAddBtnFn('edit');
                    }
                }
            };
        },
        checkSingleItemProFn(arry, tabName) {
            // 单个产品类目校验
            let singleItemFlag = arry.every((item) => item.quantity > 0 && item.unit_inner_sn);
            if (!singleItemFlag) {
                this.changeTabsFn(tabName);
                this.$warning({
                    title: '操作提示',
                    content: '添加的产品数量/包装单位不能为空',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
            }
            return singleItemFlag;
        },
        checkSingleProFn() {
            // 单个产品校验
            let singleFlag = this.checkSingleItemProFn(this.toolTableData, 'tool');
            return singleFlag;
        },
        checkGroupProFn(details) {
            // 每项类目之间比较
            let itemFlag = true,
                arrs = details.length,
                isRepeatObj = {};
            for (let i = 0; i < arrs; i++) {
                let item = details[i];
                if (typeof item.vendor_inner_sn === 'undefined') {
                    item.vendor_inner_sn = item.product_vendor_inner_sn;
                }
                if (typeof item.vendor_name === 'undefined') {
                    item.vendor_name = item.product_vendor_name;
                }
                item.key_new = '' + item.product_inner_sn + item.specification_inner_sn + item.vendor_inner_sn;
                let key = item.key_new + item.product_name + item.specification + item.vendor_name + item.unit_inner_sn;
                if (!isRepeatObj[key]) {
                    isRepeatObj[key] = {
                        repeatNum: 1,
                        index: i + 1
                    };
                } else {
                    itemFlag = false;
                    isRepeatObj[key].repeatNum += 1;
                    this.$warning({
                        title: '操作提示',
                        content: `NO.${isRepeatObj[key].index}和NO.${i + 1}产品相同，不能重复添加。`,
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    break;
                }
            }
            return itemFlag;
        },
        changeSel(val) {
            // 切换类型
            this.formData.type_inner_sn = val;
        },
        changeTabsFn(val) {
            this.activeTab = val;
        },
        openAddPro(e) {
            // 打开选择产品
            this.dblclickTableFn(e);
            // if (this.$refs.chooseProMd) {
            //    this.$refs.chooseProMd.pageDataInit()
            // }
        },
        dblclickTableFn(e) {
            const filterDom = ['span', 'svg', 'path', 'input', 'i', 'li', 'ul'],
                flag = filterDom.some((item) => item === e.target.tagName.toLowerCase()),
                childrenFlag = filterDom.some(
                    (item) => item === e.target.children && e.target.children.tagName.toLowerCase()
                );
            if (flag || childrenFlag) return;
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn(this.addProductName);
        },
        addProductFn() {
            // 添加商品
            this.$refs.chooseProMd.returnProList();
        },
        getProReturn(dea, ven) {
            // 获取回调商品
            let arr = ven.concat(dea);
            let newArr = JSON.parse(JSON.stringify(arr));
            this.toolTableData.forEach((item) => {
                item.isActive = '';
            });
            newArr.forEach((item) => {
                item.isActive = 'active-tr';
                item.unit_inner_sn = '';
                item.quantity = '';
                item.key = Math.random();
                item.sort_number = this.toolTableData.length + 1;
                item.type = 0;
                item.product_vendor_inner_sn = item.product_vendor_inner_sn || item.vendor_inner_sn;
                item.product_vendor_name = item.product_vendor_name || item.vendor_name;
                item.isNew = true;
                this.venUnitTreeFn(item, 'add');
            });
            this.addProductMd.alert = false;

            if (this.$refs.xTable) {
                /**
                 * 添加完工具之后，Table需要滚动到最新添加的产品位置
                 */
                this.$Utils.scrollToAddedRow('xTable', this);
            }
        },
        async venUnitTreeFn(record, type) {
            // 厂商商产品单位
            let oid = record.product_vendor_inner_sn;
            let pid = record.product_inner_sn;
            let psid = record.specification_inner_sn;
            await platformUnitTree(oid, pid, psid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (type === 'add') {
                        this.toolTableData.push(record);
                    }
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
                    this.toolTableData.forEach((item) => {
                        if (item.key === record.key) {
                            item.unit_data = unitArr;
                            if (type === 'add') {
                                item.unit_inner_sn = unitArr.key[0];
                                item.unit_name = unitArr.data[0].name;
                            }
                        }
                    });
                    if (type === 'edit') {
                        this.getUnitParent(record);
                    }
                    if (this.$refs.xTable) {
                        this.$refs.xTable.refreshColumn();
                    }
                }
            });
        },
        getUnitParent(unitObj) {
            let oid = unitObj.product_vendor_inner_sn;
            let pid = unitObj.product_inner_sn;
            let psid = unitObj.specification_inner_sn;
            let pupid = unitObj.unit_inner_sn;
            platformUnitParents(oid, pid, psid, pupid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let def = [];
                    let circle = (item) => {
                        // 递归遍历
                        def.unshift(item.unit_inner_sn);
                        if (item.parent) {
                            circle(item.parent);
                        }
                    };
                    def.unshift(res.info.unit_inner_sn);
                    if (res.info.parent) {
                        circle(res.info.parent);
                    }
                    this.toolTableData.forEach((item) => {
                        if (item.key === unitObj.key) {
                            item.unit_data.key = def;
                        }
                    });
                }
            });
        }
    },
    computed: {
        userSystemType() {
            return parseFloat(this.$cookie.get('userSystemType'));
        }
    },
    created() {
        this.pageRefresh();
    }
};
</script>

<style scoped lang="less">
.ant-input-number {
    width: 82px !important;
}
.del_ico {
    color: red;
    font-size: 16px;
}
.search-box {
    margin-top: 20px;
    margin-bottom: 6px;
    padding: 0 24px;
}
.mark_box {
    padding: 10px 22px;
    position: absolute;
    bottom: 10px;
    .label {
        display: inline-block;
        vertical-align: top;
        width: 40px;
    }
    .text_a {
        display: inline-block;
        vertical-align: top;
        width: 1032px;
        min-height: 100px;
        max-height: 100px;
        resize: none;
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
        padding-top: 24px;

        &:nth-of-type(2) {
            .item-wrap {
                display: flex;
                padding-right: 50px;
                .item {
                    padding-bottom: 0;
                    padding-right: @w;
                }
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

.category-wrap {
    display: flex;
    flex-direction: column;

    .category-ul {
        display: flex;
        padding-left: 0;
        margin-bottom: 16px;

        li,
        div {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex: 1;
        }

        .label {
            display: flex;
            justify-content: flex-end;
            width: 56px;
            margin-right: 6px;
            font-size: 14px;
            color: #7b8ba0;
        }

        .red {
            position: relative;
            top: 2px;
            left: -4px;
            font-size: 16px;
        }

        .input_box,
        .ant-input {
            height: 24px;
        }
    }
}

i.yrt.yrt-shanchu-copy.cancel.del_ico {
    display: block;
    margin: auto;
    padding-right: 0;
}
</style>
