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
                                    disabled
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
                                <a-input type="text" disabled v-model="formData.ois_sn" placeholder="" />
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
                <a-tabs :activeKey="activeTab" @change="changeTabsFn" type="card">
                    <a-tab-pane v-for="item of tabName" :key="item.key" :tab="item.name">
                        <vxe-grid
                            v-if="item.key == 'tool'"
                            size="small"
                            ref="xToolLookTable"
                            height="386"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            align="center"
                            :data="toolTableData"
                            :columns="toolColumns"
                            @cell-click="cellClickFn"
                            @cell-dblclick="cellDblClickFn"
                            show-overflow
                            show-header-overflow
                            showHeader
                        ></vxe-grid>
                        <vxe-grid
                            v-if="item.key == 'stuff'"
                            size="small"
                            ref="xStuffLookTable"
                            height="386"
                            border
                            resizable
                            auto-resize
                            highlight-current-row
                            align="center"
                            :data="stuffTableData"
                            :columns="stuffColumns"
                            show-overflow
                            show-header-overflow
                            showHeader
                        ></vxe-grid>
                    </a-tab-pane>
                </a-tabs>
            </div>
            <a-modal
                title="查看"
                :visible="lookMd.alert"
                :width="1000"
                okText="保存"
                :maskClosable="false"
                :mask="false"
                :centered="true"
                @cancel="lookMd.alert = false"
                class="modal-form-input-scoped global-drag look-tool-md"
            >
                <div v-globalDrag="{ el: 'look-tool-md' }" class="modal-header-wrap">
                    <h6>
                        <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                        {{ lookMd.title }}
                    </h6>
                </div>
                <template slot="footer">
                    <a-button @click="lookMd.alert = false">关闭</a-button>
                </template>
                <look-tools ref="editOpMd" @returnData="editOpFn" :editObj="lookTrObj"></look-tools>
            </a-modal>
        </div>
    </a-spin>
</template>

<script>
import lookTools from './lookToolsKits';
import {
    surgeryPackageInfoAPI,
    surgeryCustDealePackageGetAPI,
    surgeryCustDealePackageGetPlatformPackageAPI
} from '@/service/pageAjax';

export default {
    name: 'lookOpKits',
    props: ['editObj'],
    components: {
        lookTools
    },
    data() {
        return {
            spinning: false,
            formData: {
                name: '',
                description: '',
                type_inner_sn: '',
                details: [],
                keywords: '',
                ois_sn: ''
            },
            lookMd: {
                title: `${this.$route.meta.title} - 工具包详情`,
                alert: false,
                loading: false
            },
            lookTrObj: {},
            defaultBrand: '',
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
                    type: 'seq',
                    align: 'center',
                    width: '6%'
                },
                {
                    title: '编号',
                    field: 'kit_sn',
                    width: '35%',
                    align: 'center'
                },
                {
                    title: '名称',
                    field: 'name',
                    width: '30%',
                    align: 'center'
                },
                {
                    title: '类别',
                    field: 'type_name',
                    width: '16%',
                    align: 'center'
                },
                {
                    title: '品牌',
                    field: 'brand_name',
                    width: '13%',
                    align: 'center'
                }
            ],
            stuffColumns: [
                {
                    title: 'NO.',
                    type: 'seq',
                    align: 'center',
                    width: '6%'
                },
                {
                    title: '产品',
                    field: 'name',
                    width: '20%',
                    align: 'center'
                },
                {
                    title: '产品编码',
                    field: 'specification_vendor_sn',
                    width: '18%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    field: 'specification',
                    width: '16%',
                    align: 'center'
                },
                {
                    title: '厂商',
                    field: 'product_vendor_name',
                    width: '18%',
                    align: 'center'
                },
                {
                    title: '数量',
                    field: 'quantity',
                    width: '10%',
                    align: 'center'
                },
                {
                    title: '包装单位',
                    field: 'unit_name',
                    width: '12%',
                    align: 'center'
                }
            ],
            toolTableData: [],
            stuffTableData: [],
            addProductMd: {
                title: `${this.$route.meta.title} - 手术包 - 添加产品`,
                alert: false,
                loading: false
            },
            unitList: []
        };
    },
    methods: {
        async pageRefresh(obj) {
            let dataObj;
            if (obj) {
                dataObj = obj;
            } else {
                dataObj = this.editObj;
            }
            this.activeTab = 'stuff';
            this.toolTableData = [];
            this.stuffTableData = [];
            await surgeryPackageInfoAPI(dataObj.ois_inner_sn)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.formData = {
                            name: res.info.name,
                            description: res.info.description,
                            type_inner_sn: res.info.type_inner_sn.toString(),
                            ois_sn: res.info.ois_sn,
                            details: []
                        };
                        this.defaultBrand = res.info.brand_name;
                        res.info.details.forEach((item, idx) => {
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
                            this.stuffTableData.push(item);
                        });
                        res.info.kits.forEach((item) => {
                            item.key = item.kit_inner_sn;
                            this.toolTableData.push(item);
                        });
                    }
                    this.initXtableScrollFn();
                })
                .finally(() => {
                    this.spinning = false;
                });
        },
        async surgeryCustDealePackageGetAPIFn(obj) {
            let dataObj;
            if (obj) {
                dataObj = obj;
            } else {
                dataObj = this.editObj;
            }
            this.activeTab = 'stuff';
            this.toolTableData = [];
            this.stuffTableData = [];
            await surgeryCustDealePackageGetAPI(dataObj.id || dataObj.platform_ois_inner_sn)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.formData = {
                            name: res.info.name,
                            description: res.info.description,
                            type_inner_sn: res.info.type_inner_sn.toString(),
                            ois_sn: res.info.ois_sn,
                            keywords: res.info.keywords,
                            details: []
                        };
                        this.defaultBrand = res.info.brand_name;
                        res.info.details.forEach((item, idx) => {
                            item.isActive = '';
                            item.key =
                                Math.random() +
                                '' +
                                item.id +
                                item.brand_inner_sn +
                                item.owner_dealer_inner_sn +
                                item.type_inner_sn;
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
                            this.stuffTableData.push(item);
                        });
                        res.info.kits.forEach((item) => {
                            item.key = item.kit_inner_sn;
                            this.toolTableData.push(item);
                        });
                    }
                    this.initXtableScrollFn();
                })
                .finally(() => {
                    this.spinning = false;
                });
        },
        async surgeryCustDealePackageGetPlatformPackageAPIFn(obj) {
            let dataObj;
            if (obj) {
                dataObj = obj;
            } else {
                dataObj = this.editObj;
            }
            this.activeTab = 'stuff';
            this.toolTableData = [];
            this.stuffTableData = [];
            await surgeryCustDealePackageGetPlatformPackageAPI(dataObj.id || dataObj.platform_ois_inner_sn)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.formData = {
                            name: res.info.name,
                            description: res.info.description,
                            type_inner_sn: res.info.type_inner_sn.toString(),
                            ois_sn: res.info.ois_sn,
                            keywords: res.info.keywords,
                            details: []
                        };
                        this.defaultBrand = res.info.brand_name;
                        res.info.details.forEach((item, idx) => {
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
                            this.stuffTableData.push(item);
                        });
                        res.info.kits.forEach((item) => {
                            item.key = item.kit_inner_sn;
                            this.toolTableData.push(item);
                        });
                    }
                    this.initXtableScrollFn();
                })
                .finally(() => {
                    this.spinning = false;
                });
        },
        cellClickFn({ row }) {
            if (this.editObj && this.editObj.actionTypeName) {
                this.lookTrObj = { ...row, actionTypeName: this.editObj.actionTypeName };
            } else {
                this.lookTrObj = { ...row };
            }
        },
        cellDblClickFn({ row }) {
            if (this.editObj && this.editObj.actionTypeName) {
                this.lookTrObj = { ...row, actionTypeName: this.editObj.actionTypeName };
            } else {
                this.lookTrObj = { ...row };
            }
            this.openLook();
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
        changeTabsFn(val) {
            this.activeTab = val;
        },
        openLook() {
            this.$Utils.globalDragCenterFn('look-tool-md');
            this.lookMd.alert = true;
            this.$nextTick(() => {
                if (this.$refs.editOpMd) {
                    this.$refs.editOpMd.initPageFn();
                }
            });
        },
        surgeryOperationPlanFn() {
            this.spinning = true;
            this.toolTableData = [];
            this.stuffTableData = [];
            const info = {
                ...this.editObj
            };
            this.oldFormData = JSON.parse(JSON.stringify(info));
            this.oldFormData.type_inner_sn.toString();
            this.formData = {
                name: info.name,
                ois_sn: info.ois_sn,
                description: info.description,
                type_inner_sn: info.type_inner_sn.toString(),
                brand_inner_sn: info.brand_inner_sn,
                keywords: info.keywords,
                details: []
            };
            this.defaultBrand = info.brand_name;
            info.details.forEach((item, idx) => {
                item.isActive = '';
                item.key =
                    Math.random() +
                    '' +
                    item.id +
                    item.brand_inner_sn +
                    item.owner_dealer_inner_sn +
                    item.type_inner_sn;
                item.idx = idx + 1;
                item.name = item.product_name;
                item.isEdit = true;
                item.unit_name = item.unit;
                item.unit_data = {
                    key: [item.unit_inner_sn],
                    data: [
                        {
                            value: item.unit_inner_sn,
                            label: item.unit
                        }
                    ]
                };
                this.stuffTableData.push(item);
            });

            info.kits.forEach((item) => {
                this.toolTableData.push({
                    ...item,
                    key: item.id
                });
            });
            this.spinning = false;
        },
        initPageFn() {
            if (this.editObj.actionTypeName === 'operation-plan') {
                // 手术计划
                this.surgeryOperationPlanFn();
            } else {
                this.spinning = true;
                if (parseFloat(this.$cookie.get('userSystemType')) === 2) {
                    this.editObj.is_cust
                        ? this.surgeryCustDealePackageGetAPIFn()
                        : this.surgeryCustDealePackageGetPlatformPackageAPIFn();
                } else {
                    this.pageRefresh();
                }
            }
        },
        initXtableScrollFn() {
            let xStuffLookTable = this.$refs.xStuffLookTable,
                xToolLookTable = this.$refs.xToolLookTable;
            if (xStuffLookTable) {
                xStuffLookTable[0].refreshColumn();
            }
            if (xToolLookTable) {
                xToolLookTable[0].refreshColumn();
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
.mark_box {
    padding: 10px 22px;
    position: absolute;
    bottom: 10px;
    display: flex;
    width: 100%;

    .label {
        display: flex;
        vertical-align: top;
        width: 60px;
    }
    .text_a {
        display: flex;
        flex: 1;
        min-height: 100px;
        max-height: 100px;
        resize: none;
    }

    &.description {
        padding: 0 22px;
        bottom: 50px;

        .text_a {
            display: flex;
            flex: 1;
            min-height: 70px;
            max-height: 70px;
        }
    }
}

.keywords {
    padding: 4px 22px;
    position: absolute;
    bottom: 0;
    display: flex;
    width: 100%;

    .label {
        display: flex;
        vertical-align: middle;
        width: 60px;
    }
    .text_a {
        display: flex;
        flex: 1;
        min-height: 30px;
        max-height: 30px;
        resize: none;
    }
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
