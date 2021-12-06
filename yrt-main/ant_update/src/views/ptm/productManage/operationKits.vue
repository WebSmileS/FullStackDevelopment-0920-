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
                                :disabled="tableData.length > 0 && dealerBtnStatus"
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
                                v-else
                                :disabled="tableData.length > 0 && !dealerBtnStatus"
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
                            <span class="label">关键词</span>
                            <a-input v-model="searchData.search_name" type="text" placeholder="" />
                        </li>
                        <li>
                            <span class="label">类别</span>
                            <a-select allowClear="true" style="width: 180px" @change="changeSel">
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
                                style="width: 180px"
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
                :height="scrollYNumber + 82"
                resizable
                auto-resize
                border
                highlight-current-row
                @cell-click="cellClickFn"
                @cell-dblclick="cellDblclickFn"
                align="center"
                :columns="userSystemType === 0 ? columns : columnDealer"
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
            class="modal-form-input-scoped global-drag create-contract-md"
        >
            <div v-globalDrag="{ el: 'create-contract-md' }" class="modal-header-wrap">
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
            <create-op-kits ref="copyOpMd" :isCopy="true" :editObj="chooseTrObj" @returnData="saveFn"></create-op-kits>
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
            <look-op-kits ref="customMd" :partType="editPartType" :editObj="lookEditObj"></look-op-kits>
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
    surgeryPackageListAPI,
    surgeryPackageAddAPI,
    surgeryPackageEditAPI,
    surgeryPackageDelAPI,
    surgeryBrandListAPI,
    approveAddAPI,
    surgeryCustDictSearchScopeAPI,
    surgeryCustDealerPackageListAPI,
    surgeryCustDictPackageStatusAPI,
    surgeryCustDealerPackageDelAPI,
    surgeryCustDealerPackageSaveAPI
} from '@/service/pageAjax';

import createOpKits from './module/createOpKits';
import editsOpKits from './module/editOpkits';
import lookOpKits from './module/lookOpKits';

export default {
    name: 'operationKits',
    components: {
        createOpKits,
        editsOpKits,
        lookOpKits
    },
    data() {
        return {
            privateCustomSwitch: false, // 页面是否触发定制按钮的标识【true - 触发了】
            editPartType: 2,
            editObj: {},
            lookEditObj: {},
            searchScopeList: [],
            isChooseTr: false,
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            searchData: {
                search_name: '',
                type_inner_sn: '',
                brand_inner_sn: '',
                searchScope: ''
            },
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
                    dataIndex: 'ois_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '230',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ois_sn];
                        }
                    }
                },
                {
                    title: '名称',
                    dataIndex: 'name',
                    showOverflow: true,
                    showHeaderOverflow: true,
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
                    showHeaderOverflow: true,
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
                    showHeaderOverflow: true,
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
                    showHeaderOverflow: true,
                    minWidth: '100',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [this.statusName[row.status]];
                        }
                    }
                }
            ],
            columnDealer: [
                {
                    title: '编号',
                    dataIndex: 'ois_sn',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '30%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.ois_sn];
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
            dealerBtnStatus: true,
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
            this.userSystemType === 0 ? this.loadTable() : this.surgeryCustDealerPackageListAPIFn();
        },
        async surgeryCustDictPackageStatusAPIFn() {
            await surgeryCustDictPackageStatusAPI()
                .then((res) => {
                    if (res.code === 0) {
                        const { list } = res;
                        list.forEach((item) => {
                            this.statusDealerName[item.name] = item.text;
                        });
                        this.surgeryCustDealerPackageListAPIFn();
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
            }
        },
        async surgeryCustDealerPackageListAPIFn(chooseObj) {
            // 加载节点
            if (this.$Utils.checkInputValFn(this.searchData.search_name)) {
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
            await surgeryCustDealerPackageListAPI(
                this.$cookie.get('userbelong'),
                params,
                this.pageInit.current,
                this.pageInit.pageSize
            )
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let total = res.total;
                        this.pageInit = {
                            ...this.pageInit,
                            total
                        };
                        this.tableData = res.rows.map((item) => {
                            item.isActive = '';
                            item.key = item.id + '' + item.platform_ois_inner_sn;
                            return item;
                        });

                        this.$nextTick(() => {
                            this.$refs.xTable.reloadData(this.tableData);
                        });

                        if (this.tableData.length > 0) {
                            if (chooseObj && Object.keys(chooseObj).length > 0) {
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
                            this.curItemIsCust(this.tableData[0]);
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
            if (this.$Utils.checkInputValFn(this.searchData.search_name)) {
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
                orderByColumn: 'ois_inner_sn',
                isAsc: 'desc'
            };

            params.pageSize = this.pageInit.pageSize;
            params.pageNum = this.pageInit.current;
            await surgeryPackageListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let total = res.total;
                    this.pageInit = {
                        ...this.pageInit,
                        total
                    };
                    this.tableData = res.rows.map((item) => {
                        item.isActive = '';
                        item.key = item.ois_inner_sn;
                        return item;
                    });
                    if (this.tableData.length > 0) {
                        this.$refs.xTable.reloadColumn(this.columns);
                        this.$refs.xTable.loadData(this.tableData);

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
            this.userSystemType === 0 ? this.loadTable() : this.surgeryCustDealerPackageListAPIFn();
            this.isChooseTr = false;
        },
        cellClickFn(row) {
            // 单击行
            let obj = row.row || row;
            this.curItemIsCust(obj);
            this.chooseTrEvent(obj);
        },
        cellDblclickFn(row) {
            let obj = row.row || row;
            this.curItemIsCust(obj);
            this.chooseTrEvent(obj);
            if (this.userSystemType === 0 || (this.userSystemType === 2 && obj.is_cust)) {
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
            this.$Utils.globalDragCenterFn('create-contract-md');
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
            if (this.createMd.loading) {
                return;
            }
            this.createMd.loading = true;
            await surgeryPackageAddAPI(dataObj)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.createMd.alert = false;
                        this.copyMd.alert = false;
                        let obj = res.info;
                        obj.isActive = '';
                        if (this.userSystemType === 0) {
                            obj.key = obj.ois_inner_sn;
                        } else {
                            obj.id = null;
                            obj.is_cust = false;
                            obj.is_delete = false;
                            obj.keywords = null;
                            obj.owner_dealer_inner_sn = null;
                            obj.owner_dealer_name = null;
                            obj.platform_ois_inner_sn = obj.ois_inner_sn;
                            obj.key = obj.ois_inner_sn;
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
                            this.$refs.xTable.setCurrentRow(obj); // 默认选中
                        }
                        // this.loadTable(this.chooseTrObj)
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.createMd.loading = false;
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
            this.editObj.actionTypeName = !(
                this.userSystemType === 0 ||
                (this.userSystemType === 2 && this.editObj.is_cust)
            );
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
                        this.tableData.splice(i, 0, privateCustomInitObj); // 定制: 原因:骨科手术包名称重复
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
            this.userSystemType === 0
                ? this.surgeryPackageEditAPIFn(dataObj)
                : this.surgeryCustDealerPackageSaveAPIFn(dataObj);
        },
        async surgeryPackageEditAPIFn(dataObj) {
            // 修改
            await surgeryPackageEditAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.editMd.alert = false;
                    this.loadTable(this.chooseTrObj);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async surgeryCustDealerPackageSaveAPIFn(dataObj) {
            // 修改
            const _row = this.dealerBtnStatus ? this.$refs.xTable.getCurrentRow() : this.$refs.editOpMd.customInfoCopy;
            let newDataObj = {
                'brand_inner_sn': dataObj.brand_inner_sn || _row.brand_inner_sn,
                'brand_name': dataObj.brand_name || _row.brand_name,
                'description': dataObj.description || _row.description,
                'id': _row.id,
                'name': dataObj.name || _row.name,
                'ois_sn': dataObj.ois_sn || _row.ois_sn,
                'owner_dealer_inner_sn': _row.owner_dealer_inner_sn,
                'owner_dealer_name': _row.owner_dealer_name,
                'platform_ois_inner_sn': _row.platform_ois_inner_sn,
                'status': _row.status,
                'keywords': dataObj.keywords || _row.keywords,
                'type_inner_sn': dataObj.type_inner_sn || _row.type_inner_sn,
                'type_name': dataObj.type_name || _row.type_name
            };
            newDataObj.details = dataObj.details.map((item, index) => {
                return {
                    'id': item.id,
                    'ois_id': item.ois_id,
                    'product_inner_sn': item.product_inner_sn || '',
                    'product_name': item.product_name || '',
                    'product_vendor_inner_sn': item.product_vendor_inner_sn || '',
                    'product_vendor_name': item.product_vendor_name || '',
                    'quantity': item.quantity || '',
                    'sort_number': index || '',
                    'specification': item.specification || '',
                    'specification_inner_sn': item.specification_inner_sn || '',
                    'unit': item.unit_name || item.unit || '',
                    'unit_inner_sn': item.unit_inner_sn || ''
                };
            });
            newDataObj.kitIds = dataObj.kits.map((item) => {
                return item.id;
            });

            await surgeryCustDealerPackageSaveAPI(newDataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.editMd.alert = false;
                    this.createMd.alert = false;
                    this.copyMd.alert = false;
                    let obj = res.info;
                    obj.isActive = '';
                    if (this.userSystemType === 0) {
                        obj.key = obj.ois_inner_sn;
                    } else {
                        // obj.id = null
                        obj.is_cust = true;
                        obj.is_delete = false;
                        // obj.keywords = null
                        // obj.owner_dealer_inner_sn = null
                        // obj.owner_dealer_name = null
                        // obj.platform_ois_inner_sn = obj.ois_inner_sn || obj.platform_ois_inner_sn
                        obj.key = obj.ois_inner_sn || obj.id;
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
        curItemIsCust(row) {
            if (this.userSystemType === 0) {
                return true;
            }
            this.dealerBtnStatus = row.is_cust;
        },
        openDelContract() {
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
                    _this.surgeryCustDealerPackageDelAPIFn();
                } else {
                    _this.$warning({
                        title: '删除',
                        content: '只能删除【所属范围为私有】的手术包',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                }
            }
        },
        async surgeryCustDealerPackageDelAPIFn() {
            // 删除
            this.pageInit.loading = true;
            let pid = this.chooseTrObj.id;
            await surgeryCustDealerPackageDelAPI(pid)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.surgeryCustDealerPackageListAPIFn();
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
            let pid = this.chooseTrObj.ois_inner_sn;
            await surgeryPackageDelAPI(pid)
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
            this.$Utils.globalDragCenterFn('copy-contract-md');
            this.copyMd.alert = true;
            this.$nextTick(() => {
                if (this.$refs.copyOpMd) {
                    this.$refs.copyOpMd.pageRefresh();
                }
            });
        },
        sendApproveBack() {
            this.$refs.createApproveMd.returnData();
        },
        async approveAdd(data) {
            await approveAddAPI(data).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.createApproveMd.alert = false;
                    this.loadTable();
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
        }
    },
    mounted() {
        this.getBrandList();
        this.userSystemType === 0 ? this.loadTable() : this.surgeryCustDictPackageStatusAPIFn();
    }
};
</script>

<style lang="less" scoped>
.tab-table-wrap {
    background: #fff;
    padding-top: 0px !important;
    border: 1px solid rgba(0, 0, 0, 0.09);
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
.scoped .search-form-wrap .hight-search-wrap .form li {
    width: auto;

    &:last-child {
        padding-right: 0;
    }
}
</style>
