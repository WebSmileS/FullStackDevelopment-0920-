<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button type="primary" size="small" @click="openCreate"> 新增 </a-button>
                            <a-button
                                v-if="userSystemType === 2"
                                :disabled="isPrivateCustomDisabled"
                                type="primary"
                                size="small"
                                @click="openEdit"
                            >
                                定制
                            </a-button>
                            <a-button v-if="userSystemType === 0" type="primary" size="small" @click="openEdit">
                                修改
                            </a-button>
                            <a-button
                                v-if="userSystemType === 2"
                                :disabled="!isPrivateCustomDisabled"
                                type="primary"
                                size="small"
                                @click="openEdit"
                            >
                                修改
                            </a-button>
                            <a-button type="danger" size="small" @click="openDelContract"> 删除 </a-button>
                        </li>
                        <li v-if="userSystemType === 0">
                            <a-button type="primary" size="small" @click="openCopy"> 复制 </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-input-search
                            v-if="userSystemType === 0"
                            v-show="!hightSearchObj.isArrow"
                            v-model="searchData.search_name"
                            placeholder="名称"
                            @search="initPage"
                            size="small"
                            enterButton
                            class="search"
                        />
                        <a-input-search
                            v-else
                            v-show="!hightSearchObj.isArrow"
                            v-model="searchData.search_name"
                            placeholder="名称/关键字"
                            @search="initPage"
                            size="small"
                            enterButton
                            class="search"
                        />
                        <div @click="hightSearchIconFn" class="hight-search-text">
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
                            <span v-if="userSystemType === 0" class="label">关键词</span>
                            <span v-else class="label">关键词</span>
                            <a-input v-model="searchData.search_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">编号</span>
                            <a-input v-model="searchData.kit_sn" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">类别</span>
                            <a-select allowClear="true" @change="changeSel">
                                <a-select-option value="0">颅骨类</a-select-option>
                                <a-select-option value="1">脊柱类</a-select-option>
                                <a-select-option value="2">关节类</a-select-option>
                                <a-select-option value="3">创伤类</a-select-option>
                            </a-select>
                        </li>
                        <li>
                            <span class="label">品牌</span>
                            <a-select
                                v-model="searchData.brand_inner_sn"
                                allowClear="true"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                @change="changeBrand"
                            >
                                <a-select-option
                                    v-for="(item, key) of brandList"
                                    :key="key"
                                    :value="item.brand_inner_sn"
                                    >{{ item.name }}</a-select-option
                                >
                            </a-select>
                        </li>
                        <li v-if="userSystemType === 2">
                            <span class="label">所属范围</span>
                            <a-select
                                v-model="searchData.searchScope"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                style="width: 120px"
                                @change="changeSearchScope"
                            >
                                <a-select-option v-for="item of searchScopeList" :key="item.name" :value="item.name">{{
                                    item.text
                                }}</a-select-option>
                            </a-select>
                        </li>
                        <li>
                            <a-button @click="initPage" type="primary"> 搜索 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->
        <div
            :class="hightSearchObj.isArrow ? 'top136' : 'top56'"
            class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
            style="padding: 8px"
        >
            <!-- 表格头部 -->
            <vxe-grid
                style="margin-top: 8px"
                ref="xTable"
                size="small"
                :columns="userSystemType === 0 ? columns : columnTools"
                :height="scrollYNumber + 82"
                resizable
                auto-resize
                border
                highlight-current-row
                @cell-click="cellClickFn"
                @cell-dblclick="cellDblclickFn"
                align="center"
                :data="tableData"
                :loading="pageInit.loading"
            >
            </vxe-grid>
            <vxe-pager
                :loading="pageInit.loading"
                :current-page="pageInit.current"
                :page-size="pageInit.pageSize"
                :page-sizes="pageInit.pageSizes"
                :total="pageInit.total"
                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                @page-change="changePaginationFn"
            >
            </vxe-pager>
            <!--表格区域-->
        </div>
        <!-- modal区 -->
        <!--新增合同-->
        <a-modal
            title="新增"
            :visible="createMd.alert"
            :width="1120"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="createMd.alert = false"
            class="modal-form-input-scoped global-drag create-tools-md"
        >
            <div v-globalDrag="{ el: 'create-tools-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ createMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="createMd.alert = false">取消</a-button>
                <a-button :loading="createMd.loading" type="primary" @click="sendCreateBack">保存</a-button>
            </template>
            <create-op-kits ref="createOpMd" :isCopy="false" @returnData="saveFn"></create-op-kits>
        </a-modal>
        <a-modal
            title="复制"
            :visible="copyMd.alert"
            :width="1120"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="copyMd.alert = false"
            class="modal-form-input-scoped global-drag copy-contract-md"
        >
            <div v-globalDrag="{ el: 'copy-contract-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ copyMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="copyMd.alert = false">取消</a-button>
                <a-button :loading="copyMd.loading" type="primary" @click="sendCopyBack">保存</a-button>
            </template>
            <create-op-kits
                ref="copyOpMd"
                :isCopy="true"
                :editObj="createToolsKitsObj"
                @returnData="saveFn"
            ></create-op-kits>
        </a-modal>
        <a-modal
            title="查看"
            :visible="customMd.alert"
            :width="1120"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="customMd.alert = false"
            class="modal-form-input-scoped global-drag custom-md"
        >
            <div v-globalDrag="{ el: 'custom-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ customMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="customMd.alert = false">关闭</a-button>
                <a-button :loading="customMd.loading" type="primary" @click="sendCustomBack">定制</a-button>
            </template>
            <look-tools-kits ref="customMd" :partType="editPartType" :editObj="lookEditObj"></look-tools-kits>
        </a-modal>
        <a-modal
            title="修改"
            :visible="editMd.alert"
            :width="1120"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="editMd.alert = false"
            class="modal-form-input-scoped global-drag edit-contract-md"
        >
            <div v-globalDrag="{ el: 'edit-contract-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ editMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="editMd.alert = false">取消</a-button>
                <a-button :loading="editMd.loading" type="primary" @click="sendEditBack">保存</a-button>
            </template>
            <edits-op-kits
                ref="editOpMd"
                @initData="initDataFn"
                @returnData="editOpFn"
                :partType="editPartType"
                :editObj="editObj"
            ></edits-op-kits>
        </a-modal>
    </div>
</template>

<script>
import {
    surgeryKitListAPI,
    surgeryKitAddAPI,
    surgeryKitEditAPI,
    surgeryKitDelAPI,
    surgeryBrandListAPI,
    approveAddAPI,
    surgeryCustDictPackageStatusAPI,
    surgeryCustDictSearchScopeAPI,
    surgeryCustDealerKitListAPI,
    surgeryCustDealerKitDelAPI,
    surgeryCustDealerKitSaveAPI
} from '@/service/pageAjax';

import createOpKits from './module/createToolsKits';
import editsOpKits from './module/editToolsKits';
import lookToolsKits from './module/lookToolsKits';

export default {
    name: 'toolsKits',
    components: {
        createOpKits,
        editsOpKits,
        lookToolsKits
    },
    data() {
        return {
            editPartType: 2,
            editObj: {},
            lookEditObj: {},
            createToolsKitsObj: {},
            isChooseTr: false,
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            searchData: {
                search_name: '',
                type_inner_sn: '',
                brand_inner_sn: '',
                kit_sn: '',
                searchScope: ''
            },
            searchScopeList: [],
            pageInit: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageNum
                pageSize: 10, // 对应后端的 pageSize
                pageSizes: [10, 20, 30, 40]
            },
            columns: [
                {
                    title: '编号',
                    dataIndex: 'kit_sn',
                    showOverflow: true,
                    minWidth: '230',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.kit_sn];
                        }
                    }
                },
                {
                    title: '名称',
                    dataIndex: 'name',
                    showOverflow: true,
                    minWidth: '220',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.name];
                        }
                    }
                },
                {
                    title: '类别',
                    dataIndex: 'type_name',
                    showOverflow: true,
                    minWidth: '200',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.type_name];
                        }
                    }
                },
                {
                    title: '品牌',
                    dataIndex: 'brand_name',
                    showOverflow: true,
                    minWidth: '250',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.brand_name];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    showOverflow: true,
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [this.statusName[row.status]];
                        }
                    }
                }
            ],
            columnTools: [
                {
                    title: '编号',
                    dataIndex: 'kit_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '30%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.kit_sn];
                        }
                    }
                },
                {
                    title: '名称',
                    dataIndex: 'name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '25%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.name];
                        }
                    }
                },
                {
                    title: '类别',
                    dataIndex: 'type_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '10%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.type_name];
                        }
                    }
                },
                {
                    title: '品牌',
                    dataIndex: 'brand_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.brand_name];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '10%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [this.statusDealerName[row.status]];
                        }
                    }
                },
                {
                    title: '所属范围',
                    dataIndex: 'is_cust',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '10%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.is_cust ? this.custName[1] : this.custName[0]];
                        }
                    }
                }
            ],
            statusName: ['无效', '有效'],
            statusDealerName: {},
            custName: ['平台', '私有'],
            partStatus: {
                partA: '',
                partB: ''
            },
            chooseTrObj: {},
            tableData: [],
            createMd: {
                title: `${this.$route.meta.title} - 新增`,
                alert: false,
                loading: false
            },
            copyMd: {
                title: `${this.$route.meta.title} - 新增`,
                alert: false,
                loading: false
            },
            customMd: {
                title: `${this.$route.meta.title} - 查看`,
                alert: false,
                loading: false
            },
            editMd: {
                title: `${this.$route.meta.title} - 修改`,
                alert: false,
                loading: false
            },
            createApproveMd: {
                title: `${this.$route.meta.title} - 发起审批`,
                alert: false,
                loading: false
            },
            approveType: {},
            brandList: []
        };
    },
    methods: {
        /** 表格方法区 **/
        initPage() {
            this.pageInit.current = 1;
            this.userSystemType === 0 ? this.loadTable() : this.surgeryCustDealerKitListAPIFn();
        },
        async surgeryCustDictPackageStatusAPIFn() {
            await surgeryCustDictPackageStatusAPI()
                .then((res) => {
                    if (res.code === 0) {
                        const { list } = res;
                        list.forEach((item) => {
                            this.statusDealerName[item.name] = item.text;
                        });
                        this.surgeryCustDealerKitListAPIFn();
                        this.surgeryCustDictSearchScopeAPIFn();
                    }
                })
                .finally(() => {});
        },
        async surgeryCustDictSearchScopeAPIFn() {
            const res = await surgeryCustDictSearchScopeAPI();
            if (res.code === 0) {
                this.searchScopeList = res.list;
                if (this.searchScopeList && this.searchScopeList.length > 0) {
                    this.searchData.searchScope = this.searchScopeList[0]['name'];
                }
                this.surgeryCustDealerKitListAPIFn();
            }
        },
        async surgeryCustDealerKitListAPIFn(chooseObj) {
            if (
                this.$Utils.checkInputValFn(this.searchData.search_name) ||
                this.$Utils.checkInputValFn(this.searchData.kit_sn)
            ) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                this.pageInit.loading = false;
                return false;
            }
            this.pageInit.loading = true;
            let params = {};
            if (this.searchData.search_name) {
                params.q = this.searchData.search_name;
            }
            if (this.searchData.brand_inner_sn) {
                params.brands = [this.searchData.brand_inner_sn];
            }
            if (this.searchData.searchScope) {
                params.searchScope = this.searchData.searchScope;
            }
            if (this.searchData.type_inner_sn) {
                params.types = [this.searchData.type_inner_sn];
            }
            if (this.searchData.kit_sn) {
                params.kit_sn = this.searchData.kit_sn;
            }
            await surgeryCustDealerKitListAPI(
                this.$cookie.get('userbelong'),
                params,
                this.pageInit.current,
                this.pageInit.pageSize
            )
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let total = res.total;
                        const xTable = this.$refs.xTable;
                        this.pageInit = {
                            ...this.pageInit,
                            total
                        };
                        this.tableData = res.rows.map((item) => {
                            item.isActive = '';
                            item.key = item.id + '' + item.platform_kit_inner_sn;
                            return item;
                        });

                        this.$nextTick(() => {
                            xTable && xTable.reloadData(this.tableData);
                        });

                        if (this.tableData.length > 0) {
                            const firstChooseTrObj = this.tableData[0];
                            if (chooseObj) {
                                // 选中新增/编辑的项
                                this.tableData.forEach((item) => {
                                    if (item.key === this.chooseTrObj.key) {
                                        xTable && xTable.setCurrentRow(item); // 默认选中
                                        this.cellClickFn(item);
                                    }
                                });
                            } else {
                                xTable && xTable.setCurrentRow(firstChooseTrObj); // 默认选中第一
                                this.cellClickFn(firstChooseTrObj);
                            }
                        } else {
                            this.tableData = [];
                            this.chooseTrObj = {};
                        }
                    }
                })
                .finally(() => {
                    this.pageInit.loading = false;
                });
        },
        async loadTable(chooseObj) {
            if (
                this.$Utils.checkInputValFn(this.searchData.search_name) ||
                this.$Utils.checkInputValFn(this.searchData.kit_sn)
            ) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                this.pageInit.loading = false;
                return false;
            }
            this.pageInit.loading = true;
            let params = {
                ...this.searchData,

                // 排序
                orderByColumn: 'kit_inner_sn',
                isAsc: 'desc'
            };
            params.pageSize = this.pageInit.pageSize;
            params.pageNum = this.pageInit.current;
            await surgeryKitListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let total = res.total;
                    this.pageInit = {
                        ...this.pageInit,
                        total
                    };
                    this.tableData = res.rows.map((item) => {
                        item.isActive = '';
                        item.key = item.kit_inner_sn;
                        return item;
                    });
                    if (this.tableData.length > 0) {
                        if (chooseObj) {
                            // 选中新增/编辑的项
                            this.tableData.forEach((item) => {
                                if (item.key === this.chooseTrObj.key) {
                                    this.$refs.xTable.setCurrentRow(item); // 默认选中
                                    this.cellClickFn(item);
                                }
                            });
                        } else {
                            this.$refs.xTable.setCurrentRow(this.tableData[0]); // 默认选中第一
                            this.cellClickFn(this.tableData[0]);
                        }
                    } else {
                        this.tableData = [];
                        this.chooseTrObj = {};
                    }
                    this.pageInit.loading = false;
                    this.$refs.xTable.refreshColumn(); // 防止错位
                }
                this.pageInit.loading = false;
            });
        },
        async getBrandList() {
            // 获取品牌列表
            await surgeryBrandListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    let newList = [];
                    res.list.forEach((item) => {
                        item.key = item.brand_inner_sn;
                        newList.push(item);
                    });
                    this.brandList = newList;
                }
            });
        },
        changePaginationFn({ currentPage, pageSize }) {
            this.pageInit.current = currentPage;
            this.pageInit.pageSize = pageSize;
            this.pageInit.loading = true;
            this.userSystemType === 0 ? this.loadTable() : this.surgeryCustDealerKitListAPIFn();
            this.isChooseTr = false;
        },
        cellClickFn(row) {
            // 单击行
            let obj = row.row || row;
            this.chooseTrEvent(obj);
            // this.isChooseTr = true
            // this.chooseTrObj = obj
            // this.$nextTick(() => {
            //     this.$refs.xTable && this.$refs.xTable.setCurrentRow(obj) // 默认选中
            // })
        },
        cellDblclickFn(row) {
            let obj = row.row || row;
            this.chooseTrEvent(obj);
            if (this.isPrivateCustomDisabled) {
                this.openEdit();
            } else {
                this.customLookFn();
            }
        },
        chooseTrEvent(record) {
            // 选中事件
            this.isChooseTr = true;
            this.chooseTrObj = record;
            // this.editObj = record
            this.$nextTick(() => {
                this.$refs.xTable && this.$refs.xTable.setCurrentRow(record); // 默认选中
            });
        },
        /** 表格方法区结束 **/

        /** 头部按钮方法区 **/
        hightSearchIconFn() {
            this.hightSearchObj.isArrow = !this.hightSearchObj.isArrow;
        },
        changeBrand(val) {
            // 切换类型
            this.searchData.brand_inner_sn = val;
        },
        changeSel(val) {
            // 切换类型
            this.searchData.type_inner_sn = val;
        },
        changeSearchScope(val) {
            // 所属范围
            this.searchData.searchScope = val;
        },
        openCreate() {
            // 打开新增
            this.$Utils.globalDragCenterFn('create-tools-md');
            this.createTip = false;
            this.createMd.alert = true;
            if (this.$refs.createOpMd) {
                this.$refs.createOpMd.pageRefresh();
            }
        },
        sendCreateBack() {
            // 发送回调请求
            this.$refs.createOpMd.returnData();
            // this.createMd.alert = false
        },
        sendCopyBack() {
            this.$refs.copyOpMd.returnData();
        },
        async saveFn(dataObj) {
            // 新增
            await surgeryKitAddAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.createMd.alert = false;
                    this.copyMd.alert = false;
                    let obj = res.info;
                    obj.isActive = '';
                    if (this.userSystemType === 0) {
                        obj.key = obj.kit_inner_sn;
                    } else {
                        obj.id = null;
                        obj.is_cust = false;
                        obj.is_delete = false;
                        obj.keywords = null;
                        obj.owner_dealer_inner_sn = null;
                        obj.owner_dealer_name = null;
                        obj.platform_kit_inner_sn = obj.kit_inner_sn;
                        obj.key = obj.id + '' + obj.kit_inner_sn;
                        obj.status = 'VALID';
                    }

                    this.chooseTrObj = obj;
                    const curRow = this.$refs.xTable.getCurrentRow();
                    this.pageInit.total += 1;
                    this.tableData.forEach((item, idx) => {
                        if (item.key === curRow.key) {
                            this.tableData.splice(idx, 0, obj);
                            if (this.tableData.length + 1 > this.pageInit.pageSize) {
                                this.tableData.splice(this.tableData.length - 1, 1);
                            }
                            this.$refs.xTable.reloadData(this.tableData);
                            this.$refs.xTable.setCurrentRow(obj); // 默认选中
                            this.cellClickFn(obj);
                            throw new Error('forEach循环结束');
                        } else if (this.tableData.length <= 0) {
                            this.tableData.push(obj);
                        }
                    });
                    if (this.tableData.length <= 0) {
                        this.tableData.push(obj);
                        this.$refs.xTable.reloadData(this.tableData);
                        this.$refs.xTable.setCurrentRow(this.tableData[0]); // 默认选中
                    }
                    // this.loadTable(this.chooseTrObj)
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        getNowDate() {
            let date = new Date();
            let seperator1 = '-';
            let year = date.getFullYear();
            let month = date.getMonth() + 1;
            let strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = '0' + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = '0' + strDate;
            }
            let currentdate = year + seperator1 + month + seperator1 + strDate;
            return currentdate;
        },
        customLookFn() {
            if (!this.chooseTrObj.key) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            this.editPartType = 2;
            this.lookEditObj = this.chooseTrObj;
            this.$Utils.globalDragCenterFn('custom-md');
            this.customMd.alert = true;
            this.$nextTick(() => {
                if (this.$refs.customMd) {
                    this.$refs.customMd.initPageFn();
                }
            });
        },
        sendCustomBack() {
            // 弹框定制
            this.customMd.alert = false;
            this.openEdit();
        },
        openEdit() {
            // 打开修改
            if (Object.keys(this.chooseTrObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            this.editPartType = 2;
            this.editObj = this.chooseTrObj;
            this.editObj.actionTypeName = !this.isPrivateCustomDisabled;
            this.$Utils.globalDragCenterFn('edit-contract-md');
            this.editMd.alert = true;
            this.$nextTick(() => {
                if (this.$refs.editOpMd) {
                    this.$refs.editOpMd.initPageFn();
                }
            });
        },
        sendEditBack() {
            // 发送回调请求
            this.$refs.editOpMd.returnData();
        },
        initDataFn(privateCustomInitObj) {
            if (privateCustomInitObj && Object.keys(privateCustomInitObj).length) {
                this.chooseTrObj = privateCustomInitObj;
                this.pageInit.total += 1;
                const curRow = this.$refs.xTable.getCurrentRow();
                for (let i = 0; i < this.tableData.length; i++) {
                    if (this.tableData[i].key === curRow.key) {
                        this.tableData.splice(i, 0, privateCustomInitObj); // 定制: 原因:骨科工具包名称重复
                        if (this.tableData.length + 1 > this.pageInit.pageSize) {
                            this.tableData.splice(this.tableData.length - 1, 1);
                        }
                        this.$refs.xTable.reloadData(this.tableData);
                        this.cellClickFn(privateCustomInitObj);
                        break;
                    }
                }
            }
        },
        editOpFn(dataObj) {
            // 修改
            this.userSystemType === 0 ? this.surgeryKitEditAPIFn(dataObj) : this.surgeryCustDealerKitSaveAPIFn(dataObj);
        },
        async surgeryKitEditAPIFn(dataObj) {
            // 修改
            await surgeryKitEditAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.editMd.alert = false;
                    for (let i = 0; i < this.tableData.length; i++) {
                        let item = this.tableData[i];
                        if (item.key === this.chooseTrObj.key) {
                            if (typeof dataObj.brand_inner_sn !== 'undefined') {
                                item.brand_inner_sn = dataObj.brand_inner_sn;
                            }
                            if (typeof dataObj.brand_name !== 'undefined') {
                                item.brand_name = dataObj.brand_name;
                            }
                            if (typeof dataObj.name !== 'undefined') {
                                item.name = dataObj.name;
                            }
                            if (typeof dataObj.kit_sn !== 'undefined') {
                                item.kit_sn = dataObj.kit_sn;
                            }
                            if (typeof dataObj.type_inner_sn !== 'undefined') {
                                item.type_inner_sn = dataObj.type_inner_sn;
                            }
                            if (typeof dataObj.type_name !== 'undefined') {
                                item.type_name = dataObj.type_name;
                            }
                            if (typeof dataObj.status !== 'undefined') {
                                item.status = dataObj.status;
                            }
                            if (typeof dataObj.is_cust !== 'undefined') {
                                item.is_cust = dataObj.is_cust;
                            }
                            // this.$refs.xTable.updateData()
                            break;
                        }
                    }
                    // this.loadTable(this.chooseTrObj)
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async surgeryCustDealerKitSaveAPIFn(dataObj) {
            // 修改
            const _row = this.isPrivateCustomDisabled
                ? this.$refs.xTable.getCurrentRow()
                : this.$refs.editOpMd.customInfoCopy;
            let newDataObj = {
                'brand_inner_sn':
                    typeof dataObj.brand_inner_sn !== 'undefined' ? dataObj.brand_inner_sn : _row.brand_inner_sn,
                'brand_name': typeof dataObj.brand_name !== 'undefined' ? dataObj.brand_name : _row.brand_name,
                'description': typeof dataObj.description !== 'undefined' ? dataObj.description : _row.description,
                'id': _row.id,
                'name': typeof dataObj.name !== 'undefined' ? dataObj.name : _row.name,
                'kit_sn': typeof dataObj.kit_sn !== 'undefined' ? dataObj.kit_sn : _row.kit_sn,
                'owner_dealer_inner_sn': _row.owner_dealer_inner_sn,
                'owner_dealer_name': _row.owner_dealer_name,
                'platform_kit_inner_sn': _row.platform_kit_inner_sn,
                'status': _row.status,
                'keywords': typeof dataObj.keywords !== 'undefined' ? dataObj.keywords : _row.keywords,
                'type_inner_sn':
                    typeof dataObj.type_inner_sn !== 'undefined' ? dataObj.type_inner_sn : _row.type_inner_sn,
                'type_name': typeof dataObj.type_name !== 'undefined' ? dataObj.type_name : _row.type_name
            };
            newDataObj.details = dataObj.details.map((item, index) => {
                return {
                    'id': item.id,
                    'kit_id': item.kit_id,
                    'product_inner_sn': item.product_inner_sn || '',
                    'product_name': item.product_name || '',
                    'product_vendor_inner_sn': item.product_vendor_inner_sn || '',
                    'product_vendor_name': item.product_vendor_name || '',
                    'quantity': item.quantity || '',
                    'sort_number': index,
                    'specification': item.specification || '',
                    'specification_inner_sn': item.specification_inner_sn || '',
                    'specification_vendor_sn': item.specification_vendor_sn || '',
                    'unit': item.unit || item.unit_name || '',
                    'unit_inner_sn': item.unit_inner_sn || ''
                };
            });

            await surgeryCustDealerKitSaveAPI(newDataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.editMd.alert = false;
                    this.createMd.alert = false;
                    this.copyMd.alert = false;
                    let obj = res.info;
                    obj.isActive = '';
                    if (this.userSystemType === 0) {
                        obj.key = obj.kit_inner_sn;
                    } else {
                        obj.is_cust = true;
                        obj.is_delete = false;
                        obj.key = obj.id + '' + obj.platform_kit_inner_sn;
                        obj.status = 'VALID';
                    }

                    this.chooseTrObj = obj;
                    const curRow = this.$refs.xTable.getCurrentRow();
                    for (let i = 0; i < this.tableData.length; i++) {
                        if (this.tableData[i].key === curRow.key) {
                            this.tableData.splice(i, 1, obj);
                            if (this.tableData.length + 1 > this.pageInit.pageSize) {
                                this.tableData.splice(this.tableData.length - 1, 1);
                            }
                            this.$refs.xTable.reloadData(this.tableData);
                            this.cellClickFn(obj);
                            break;
                        } else if (this.tableData.length <= 0) {
                            this.tableData.push(obj);
                            this.$refs.xTable.reloadData(this.tableData);
                            this.cellClickFn(obj);
                            break;
                        }
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        openDelContract() {
            if (Object.keys(this.chooseTrObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            const _this = this;
            if (this.userSystemType === 0) {
                _this.$confirm({
                    title: '删除',
                    content: '确认要执行该操作吗？',
                    centered: true,
                    maskClosable: false,
                    onOk() {
                        _this.delFn();
                    }
                });
            } else {
                if (this.$refs.xTable.getCurrentRow().is_cust) {
                    _this.surgeryCustDealerKitDelAPIFn();
                } else {
                    _this.$warning({
                        title: '删除',
                        content: '只能删除【所属范围为私有】的工具包',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                }
            }
        },
        async surgeryCustDealerKitDelAPIFn() {
            // 删除
            this.pageInit.loading = true;
            let pid = this.chooseTrObj.id;
            await surgeryCustDealerKitDelAPI(pid)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.surgeryCustDealerKitListAPIFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.pageInit.loading = false;
                });
        },
        async delFn() {
            // 删除
            this.pageInit.loading = true;
            let pid = this.chooseTrObj.kit_inner_sn;
            await surgeryKitDelAPI(pid)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.loadTable();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.pageInit.loading = false;
                });
        },
        openCopy() {
            // 复制
            if (!this.chooseTrObj.key) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            this.createToolsKitsObj = { ...this.chooseTrObj };
            this.$Utils.globalDragCenterFn('copy-contract-md');
            this.copyMd.alert = true;
            if (this.$refs.copyOpMd) {
                this.$refs.copyOpMd.pageRefresh();
            }
        },
        sendApproveBack() {
            this.$refs.createApproveMd.returnData();
        },
        async approveAdd(data) {
            await approveAddAPI(data).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.createApproveMd.alert = false;
                    this.userSystemType === 0 ? this.loadTable() : this.surgeryCustDealerKitListAPIFn();
                    this.cancelFn();
                }
            });
        },
        cancelFn() {
            this.$refs.createApproveMd.cancelFn();
            this.createApproveMd.alert = false;
        }
        /** 头部按钮方法区结束 **/
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 253;
            if (this.hightSearchObj.isArrow) {
                topHeight = 313;
            }
            return clientHeight - topHeight;
        },
        userSystemType() {
            return parseFloat(this.$cookie.get('userSystemType'));
        },
        isPrivateCustomDisabled() {
            if (this.userSystemType === 0) return true;
            return this.tableData.length > 0 && this.chooseTrObj.is_cust;
        }
    },
    created() {},
    mounted() {
        this.getBrandList();
        this.userSystemType === 0 ? this.loadTable() : this.surgeryCustDictPackageStatusAPIFn();
    }
};
</script>

<style lang="less" scoped>
.scoped .search-form-wrap .hight-search-wrap .form li {
    padding-right: 16px;
}

.tab-table-wrap {
    background: #fff;
    padding-top: 0px !important;
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
</style>
