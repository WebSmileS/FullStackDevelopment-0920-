<template>
    <a-spin :spinning="spinning">
        <div class="scoped" style="margin: -24px; height: 576px; position: relative">
            <div class="search-box">
                <div class="category-wrap">
                    <ul class="category-ul">
                        <li>
                            <span class="label"><em class="em red">*</em>名称</span>
                            <div class="input_box">
                                <a-input type="text" disabled v-model="formData.name" placeholder="" />
                            </div>
                        </li>
                        <li>
                            <span class="label"><em class="em red">*</em>类别</span>
                            <div class="input_box">
                                <a-select
                                    v-model="formData.type_inner_sn"
                                    disabled
                                    style="width: 200px"
                                    @change="changeSel"
                                >
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
                                    disabled
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
                                    disabled
                                    :maxLength="100"
                                    placeholder="默认为名称的拼音首字母"
                                />
                            </div>
                        </li>
                        <li>
                            <span class="label"><em class="em"></em>编号</span>
                            <div class="input_box">
                                <a-input type="text" disabled v-model="formData.kit_sn" placeholder="" />
                            </div>
                        </li>
                    </ul>
                    <ul class="category-ul">
                        <li>
                            <span class="label"><em class="em"></em>产品描述</span>
                            <div class="input_box">
                                <a-input type="text" disabled v-model="formData.description" placeholder="" />
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="tab-table-wrap">
                <vxe-grid
                    ref="xLookToolsTable"
                    size="small"
                    border
                    show-overflow
                    show-header-overflow
                    showHeader
                    :columns="toolColumns"
                    height="390"
                    resizable
                    auto-resize
                    highlight-current-row="false"
                    align="center"
                    :data="toolTableData"
                    :loading="toolPage.loading"
                >
                </vxe-grid>
            </div>
        </div>
    </a-spin>
</template>

<script>
import {
    surgeryKitInfoAPI,
    surgeryCustDealePackageGetPlatformKitAPI,
    surgeryCustDealerKitGetAPI,
    surgeryBrandListAPI
} from '@/service/pageAjax';

export default {
    name: 'editOpkits',
    props: ['editObj'],
    data() {
        return {
            spinning: false,
            formData: {
                name: '',
                kit_sn: '',
                description: '',
                type_inner_sn: '',
                keywords: '',
                details: []
            },
            oldFormData: {},
            activeTab: 'stuff',
            part_b_obj: {},
            tabName: [
                { name: '材料', key: 'stuff' },
                { name: '工具', key: 'tool' }
            ],
            toolPage: { loading: false },
            stuffPage: { loading: false },
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
                            if (row.isNew) {
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
                            return [row.quantity];
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
                            return [row.unit_name];
                        }
                    }
                }
            ],
            toolTableData: [],
            stuffTableData: [],
            addProductMd: {
                title: `${this.$route.meta.title} - 修改 - 添加产品`,
                alert: false,
                loading: false
            },
            unitList: [],
            brandList: [],
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
            defaultBrand: ''
        };
    },
    methods: {
        async pageRefresh() {
            this.toolTableData = [];
            this.stuffTableData = [];
            this.getBrandList();
            this.spinning = true;
            const oid = parseInt(this.$cookie.get('userbelong'));
            this.part_b_obj.systemType = 3;
            this.part_b_obj.modalType = 5;
            this.part_b_obj.id = oid;
            this.activeTab = 'stuff';
            await surgeryKitInfoAPI(this.editObj.kit_inner_sn)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.oldFormData = JSON.parse(JSON.stringify(res.info));
                        this.oldFormData.type_inner_sn.toString();
                        this.formData = {
                            name: res.info.name,
                            description: res.info.description,
                            type_inner_sn: res.info.type_inner_sn.toString(),
                            brand_inner_sn: res.info.brand_inner_sn,
                            kit_sn: res.info.kit_sn || '',
                            details: []
                        };
                        this.defaultBrand = res.info.brand_name;
                        this.toolTableData = [];
                        this.stuffTableData = [];
                        this.toolTableData = res.info.details.map((item, idx) => {
                            item.isActive = '';
                            item.key = Math.random();
                            item.idx = idx + 1;
                            item.name = item.product_name;
                            item.isEdit = true;
                            item.unit_name = item.unit || item.unit_name || '';
                            item.unit_data = {
                                key: [item.unit_inner_sn],
                                data: [
                                    {
                                        value: item.unit_inner_sn,
                                        label: item.unit || item.unit_name || ''
                                    }
                                ]
                            };
                            return item;
                        });
                        this.$refs.xLookToolsTable && this.$refs.xLookToolsTable.refreshColumn();
                    }
                })
                .finally(() => {
                    this.spinning = false;
                });
        },
        async surgeryCustDealePackageGetPlatformKitAPIFn() {
            this.toolTableData = [];
            this.stuffTableData = [];
            this.getBrandList();
            this.spinning = true;
            const oid = parseInt(this.$cookie.get('userbelong'));
            this.part_b_obj.systemType = 3;
            this.part_b_obj.modalType = 5;
            this.part_b_obj.id = oid;
            this.activeTab = 'stuff';
            await surgeryCustDealePackageGetPlatformKitAPI(this.editObj.platform_kit_inner_sn)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.oldFormData = JSON.parse(JSON.stringify(res.info));
                        this.oldFormData.type_inner_sn.toString();
                        this.formData = {
                            name: res.info.name,
                            description: res.info.description,
                            type_inner_sn: res.info.type_inner_sn.toString(),
                            brand_inner_sn: res.info.brand_inner_sn,
                            keywords: res.info.keywords || '',
                            kit_sn: res.info.kit_sn || '',
                            details: []
                        };
                        this.defaultBrand = res.info.brand_name;
                        this.toolTableData = [];
                        this.stuffTableData = [];
                        this.toolTableData = res.info.details.map((item, idx) => {
                            item.isActive = '';
                            item.key = Math.random();
                            item.idx = idx + 1;
                            item.name = item.product_name;
                            item.isEdit = true;
                            item.unit_name = item.unit || item.unit_name || '';
                            item.unit_data = {
                                key: [item.unit_inner_sn],
                                data: [
                                    {
                                        value: item.unit_inner_sn,
                                        label: item.unit || item.unit_name || ''
                                    }
                                ]
                            };
                            return item;
                        });
                        this.$refs.xLookToolsTable && this.$refs.xLookToolsTable.refreshColumn();
                    }
                })
                .finally(() => {
                    this.spinning = false;
                });
        },
        async surgeryCustDealerKitGetAPIFn() {
            this.toolTableData = [];
            this.stuffTableData = [];
            this.getBrandList();
            this.spinning = true;
            const oid = parseInt(this.$cookie.get('userbelong'));
            this.part_b_obj.systemType = 3;
            this.part_b_obj.modalType = 5;
            this.part_b_obj.id = oid;
            this.activeTab = 'stuff';
            await surgeryCustDealerKitGetAPI(this.editObj.id)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.oldFormData = JSON.parse(JSON.stringify(res.info));
                        this.oldFormData.type_inner_sn.toString();
                        this.formData = {
                            name: res.info.name,
                            description: res.info.description,
                            type_inner_sn: res.info.type_inner_sn.toString(),
                            brand_inner_sn: res.info.brand_inner_sn,
                            keywords: res.info.keywords || '',
                            kit_sn: res.info.kit_sn || '',
                            details: []
                        };
                        this.defaultBrand = res.info.brand_name;
                        this.toolTableData = [];
                        this.stuffTableData = [];
                        this.toolTableData = res.info.details.map((item, idx) => {
                            item.isActive = '';
                            item.key = Math.random();
                            item.idx = idx + 1;
                            item.name = item.product_name;
                            item.isEdit = true;
                            item.unit_name = item.unit || item.unit_name || '';
                            item.unit_data = {
                                key: [item.unit_inner_sn],
                                data: [
                                    {
                                        value: item.unit_inner_sn,
                                        label: item.unit || item.unit_name || ''
                                    }
                                ]
                            };
                            return item;
                        });
                        this.$refs.xLookToolsTable && this.$refs.xLookToolsTable.refreshColumn();
                    }
                })
                .finally(() => {
                    this.spinning = false;
                });
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
        changeSel(val) {
            // 切换类型
            this.formData.type_inner_sn = val;
        },
        changeTabsFn(val) {
            this.activeTab = val;
        },
        openAddPro() {
            // 打开选择产品
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn('add_product_edit');
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        customRowFn(record) {
            // 设置行属性 —— 点击每一行的事件
            return {
                class: record.isActive,
                on: {
                    click: () => {
                        this.currentTrObj = record;
                        this.chooseTrEvent(record);
                    },
                    dblclick: () => {
                        this.chooseTrEvent(record);
                        this.currentTrObj = record;
                        this.openAddPro();
                    }
                }
            };
        },
        chooseTrEvent(record) {
            // 选中事件
            this.chooseTr(record.key);
        },
        chooseTr(id) {
            // 选中行渲染
            this.isChooseTr = true;
            if (this.activeTab === 'tool') {
                this.toolTableData.forEach((item) => {
                    if (item.key === id) {
                        item.isActive = 'active-tr';
                    } else {
                        item.isActive = '';
                    }
                });
            }
            if (this.activeTab === 'stuff') {
                this.stuffTableData.forEach((item) => {
                    if (item.key === id) {
                        item.isActive = 'active-tr';
                    } else {
                        item.isActive = '';
                    }
                });
            }
        },
        defaultDataFn() {
            const info = this.editObj;
            this.toolTableData = [];
            this.stuffTableData = [];
            this.getBrandList();
            const oid = parseInt(this.$cookie.get('userbelong'));
            this.part_b_obj.systemType = 3;
            this.part_b_obj.modalType = 5;
            this.part_b_obj.id = oid;
            this.activeTab = 'stuff';
            this.oldFormData = JSON.parse(JSON.stringify(info));
            this.oldFormData.type_inner_sn.toString();
            this.formData = {
                name: info.name,
                description: info.description,
                type_inner_sn: info.type_inner_sn.toString(),
                brand_inner_sn: info.brand_inner_sn,
                kit_sn: info.kit_sn || '',
                details: []
            };
            this.defaultBrand = info.brand_name;
            this.toolTableData = [];
            this.stuffTableData = [];
            this.toolTableData = info.details.map((item, idx) => {
                item.isActive = '';
                item.key = Math.random();
                item.idx = idx + 1;
                item.name = item.product_name;
                item.isEdit = true;
                item.unit_name = item.unit || item.unit_name || '';
                item.unit_data = {
                    key: [item.unit_inner_sn],
                    data: [
                        {
                            value: item.unit_inner_sn,
                            label: item.unit || item.unit_name || ''
                        }
                    ]
                };
                return item;
            });
            this.$refs.xLookToolsTable && this.$refs.xLookToolsTable.refreshColumn();
        },
        initPageFn() {
            if (this.editObj && this.editObj.actionTypeName === 'operation-plan') {
                // 手术计划直接取全量
                this.defaultDataFn();
            } else {
                if (this.userSystemType === 0) {
                    this.pageRefresh();
                } else {
                    this.editObj.is_cust
                        ? this.surgeryCustDealerKitGetAPIFn()
                        : this.surgeryCustDealePackageGetPlatformKitAPIFn();
                }
            }
        }
    },
    computed: {
        userSystemType() {
            return parseFloat(this.$cookie.get('userSystemType'));
        }
    }
};
</script>

<style scoped lang="less">
.del_ico {
    color: red;
    font-size: 16px;
}
.search-box {
    margin-top: 20px;
    margin-bottom: 6px;
    padding: 0 24px;
}
.keywords {
    padding: 10px 22px;
    position: absolute;
    bottom: 10px;
    .label {
        display: inline-block;
        vertical-align: middle;
        width: 60px;
    }
    .text_a {
        display: inline-block;
        vertical-align: middle;
        width: 1000px;
        min-height: 30px;
        max-height: 30px;
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
</style>
