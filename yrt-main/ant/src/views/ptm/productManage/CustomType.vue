<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="24">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="groupBtnFn('addChild')" type="primary" size="small">
                                新增子分类
                            </a-button>
                            <a-button @click="groupBtnFn('addSameLevel')" type="primary" size="small">
                                新增同级分类
                            </a-button>
                        </li>
                        <li>
                            <a-button @click="groupBtnFn('edit')" type="primary" size="small"> 修改 </a-button>
                            <a-button @click="groupBtnFn('remove')" type="danger" size="small"> 删除 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- 表格区域开始 -->
        <div :class="hightSearchObj.isArrow ? 'top136' : 'top56'" class="tab-table-wrap tab-table-overflowy">
            <div class="table-pd-bg" style="padding: 8px">
                <a-table
                    :columns="columns"
                    :dataSource="datas"
                    :pagination="false"
                    bordered
                    :loading="customTypeLoading"
                    :scroll="{ y: scrollYNumber }"
                    :customRow="customRowFn"
                    :rowClassName="rowClassFn"
                    :expandedRowKeys="expandedRows"
                    @expand="expandFn"
                >
                    <!--审核状态重写-->
                    <div
                        slot="status_text"
                        slot-scope="text, record"
                        :class="{
                            'red': record.status !== 1
                        }"
                        >{{ text }}</div
                    >
                </a-table>
            </div>
        </div>
        <!-- 表格区域结束 -->

        <!-- 修改区域开始 -->
        <a-modal
            v-model="customTypeInfoModal.alert"
            :centered="true"
            :maskClosable="false"
            class="modal-form-input-scoped custom-type global-drag"
        >
            <div v-globalDrag="{ el: 'custom-type' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ customTypeInfoModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="customTypeInfoModalCancelFn"> 取消 </a-button>
                <a-button
                    key="submit"
                    type="primary"
                    :loading="customTypeInfoModal.loading"
                    @click="customTypeInfoModalOkFn"
                >
                    保存
                </a-button>
            </template>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>类型</span>
                        <a-input v-model="editInfoFormObj.type" placeholder="" class="input" />
                    </li>
                    <li v-show="customTypeInfoModal.isShowStatus" class="item">
                        <span class="label"><em class="em"></em>有效</span>
                        <a-checkbox-group
                            :options="checkboxGroupStatus"
                            v-model="checkboxGroupCursVal"
                            @change="checkboxGroupCursValChangeFn"
                            class="input"
                        />
                    </li>
                    <li class="item item-description">
                        <span class="label"><em class="em"></em>备注</span>
                        <a-textarea
                            v-model="editInfoFormObj.description"
                            :autosize="{ minRows: 4, maxRows: 6 }"
                            class="input"
                            placeholder=""
                        />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 修改区域结束 -->
    </div>
</template>

<script>
import {
    dealerTypeDelAPI,
    dealerGetNextListAPI,
    dealerTypeInfoAPI,
    dealerTypeEditAPI,
    dealerTypeAddAPI,
    dealerGetParentListAPI,
    categoryAddAPI,
    categoryEditAPI,
    categoryGetParentListAPI,
    categoryGetNextListAPI,
    categoryGetInfoAPI
} from '@/service/pageAjax';

export default {
    name: 'CustomType',
    data() {
        return {
            userSystemType: parseFloat(this.$cookie.get('userSystemType')), // 0-平台、1-医院 、2-经销商、3-厂商
            columns: [
                {
                    title: '类型',
                    dataIndex: 'type',
                    key: 'type',
                    width: '60em',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'status_text',
                    key: 'status_text',
                    align: 'center',
                    scopedSlots: { customRender: 'status_text' }
                }
            ],
            datas: [],
            menuItemData: {
                class: true,
                arrow: false
            },
            customTypeLoading: true, // 表格loading动画配置
            statusAry: ['失效', '有效'], // 状态: 0-失效  1-有效
            expandedRows: [], // 表格是否发过请求标识的容器
            customTypeInfoModal: {
                title: `${this.$route.meta.title}`,
                btnTitle: '保存', // 保存按钮点击确认后的弹框提醒文字
                alert: false,
                loading: false,
                isShowStatus: false // 控制状态是否显示的标识
            },
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            editInfoFormObj: {
                // 修改信息的模态框值 - 后端返回回来的
                description: '',
                is_leaf: '',
                parent: '',
                parent_type_inner_sn: '',
                status: '',
                type: '',
                type_inner_sn: ''
            },
            editInfoFormOldObj: {}, // 修改信息的模态框值 - 后端返回回来的旧值
            editInfoFormNewObj: {}, // 修改信息的模态框值 - 前端根据页面修改的值放进去 【没有修改的不要向后端传递】
            checkboxGroupStatus: [
                // { label: '失效', value: 0 },
                { label: '', value: 1 } // 有效
            ], // 修改状态的模态框的多选框
            checkboxGroupCursVal: [], // 修改状态的模态框的当前选中的多选框值
            btnName: '', // 记录点击的是哪个按钮的标识
            infoKey: '' // 记录点击的是哪一行数据的key
        };
    },
    methods: {
        // 【新增】产品类型
        async dealerTypeAddAPIFn() {
            let params = {
                // status: 1,
                type: this.editInfoFormObj.type
            };

            // 判断是新增子分类还是新增同级分类的按钮 [ 自定义分类父ID ]
            if (this.btnName === 'addChild') {
                // addChild addSameLevel
                params.parent_type_inner_sn = this.currentTrObj.type_inner_sn;
            } else {
                params.parent_type_inner_sn = this.currentTrObj.parent_type_inner_sn
                    ? this.currentTrObj.parent_type_inner_sn
                    : 0;
            }

            // 该字段不是必填项,如果没有值就不要向后端发送该字段了
            if (this.editInfoFormObj.description) {
                params.description = this.editInfoFormObj.description;
            }
            await dealerTypeAddAPI(this.$cookie.get('userbelong'), params).then((res) => {
                this.customTypeInfoModal.loading = false;
                this.customTypeLoading = false;
                if (parseFloat(res.code) === 0) {
                    let { info } = res;
                    info['key'] = info.type_inner_sn;
                    info['status_text'] = this.statusAry[parseFloat(info.status)];
                    info['loadingChildrenFlag'] = false;
                    info['children'] = parseFloat(info.is_leaf) === 1 ? null : []; // 注： is_leaf 1：没有下一级叶子节点   0：有

                    this.infoKey = info['key']; // 选中当前新增的那一条数据的key

                    if (!this.currentTrObj.parent_type_inner_sn) {
                        if (this.btnName === 'addChild') {
                            // 下级插入
                            if (
                                !this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]]
                                    .children ||
                                this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]].children
                                    .length <= 0
                            ) {
                                this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]].children =
                                    [];
                            }
                            this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]].children.push(
                                info
                            );
                        } else if (this.btnName === 'addSameLevel') {
                            // 同级插入
                            this.datas.push(info);
                        }
                        this.treeDatasIndexAryFn(this.datas);
                    } else {
                        this.getDatasLocationFn(info, this.datas[this.currentTrObj.indexAry[0]], 0);
                    }

                    this.customTypeInfoModal.alert = false;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【增加】商品自定义分类
        async categoryAddAPIFn() {
            let params = {
                status: 1, // 该字段在界面上没有展示, 后端又需要填写该值,前端暂时处理为有效的状态: 1  - (无效的为: 0) // this.editInfoFormObj.status
                type: this.editInfoFormObj.type
            };

            // 判断是新增子分类还是新增同级分类的按钮 [ 自定义分类父ID ]
            if (this.btnName === 'addChild') {
                // addChild addSameLevel
                params.parent_type_inner_sn = this.currentTrObj.type_inner_sn;
            } else {
                params.parent_type_inner_sn = this.currentTrObj.parent_type_inner_sn
                    ? this.currentTrObj.parent_type_inner_sn
                    : 0;
            }

            // 该字段不是必填项,如果没有值就不要向后端发送该字段了
            if (this.editInfoFormObj.description) {
                params.description = this.editInfoFormObj.description;
            }
            await categoryAddAPI(params).then((res) => {
                this.customTypeInfoModal.loading = false;
                this.customTypeLoading = false;
                if (parseFloat(res.code) === 0) {
                    let { info } = res;
                    info['key'] = info.type_inner_sn;
                    info['status_text'] = this.statusAry[parseFloat(info.status)];
                    info['loadingChildrenFlag'] = false;
                    info['children'] = parseFloat(info.is_leaf) === 1 ? null : []; // 注： is_leaf 1：没有下一级叶子节点   0：有

                    this.infoKey = info['key']; // 选中当前新增的那一条数据的key

                    if (!this.currentTrObj.parent_type_inner_sn) {
                        if (this.btnName === 'addChild') {
                            // 下级插入
                            if (
                                !this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]]
                                    .children ||
                                this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]].children
                                    .length <= 0
                            ) {
                                this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]].children =
                                    [];
                            }
                            this.datas[this.currentTrObj.indexAry[this.currentTrObj.indexAry.length - 1]].children.push(
                                info
                            );
                        } else if (this.btnName === 'addSameLevel') {
                            // 同级插入
                            this.datas.push(info);
                        }
                        this.treeDatasIndexAryFn(this.datas);
                    } else {
                        this.getDatasLocationFn(info, this.datas[this.currentTrObj.indexAry[0]], 0);
                    }

                    this.customTypeInfoModal.alert = false;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【删除】产品类型
        async dealerTypeDelAPIFn() {
            await dealerTypeDelAPI(this.currentTrObj.dealer_inner_sn, this.currentTrObj.type_inner_sn)
                .then((res) => {
                    this.customTypeLoading = false;
                    if (parseFloat(res.code) === 0) {
                        if (!this.currentTrObj.parent_type_inner_sn) {
                            for (let i = 0; i < this.datas.length; i++) {
                                const item = this.datas[i];
                                if (parseFloat(this.currentTrObj.type_inner_sn) === parseFloat(item.type_inner_sn)) {
                                    this.datas.splice(i, 1);
                                    break;
                                }
                            }
                            if (this.datas.length) {
                                this.treeDatasIndexAryFn(this.datas);
                                this.currentTrObj = this.datas[0];
                            } else {
                                this.currentTrObj = {};
                            }
                        } else {
                            this.getDatasLocationFn(this.currentTrObj, this.datas[this.currentTrObj.indexAry[0]], 0);
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.customTypeLoading = false;
                });
        },
        // 【修改】产品类型
        async dealerTypeEditAPIFn(editInfoFormNewObj) {
            // 传递给后端的参数，没有的值就不要传递了
            let params = {
                ...editInfoFormNewObj
            };

            await dealerTypeEditAPI(this.currentTrObj.dealer_inner_sn, this.currentTrObj.type_inner_sn, params).then(
                (res) => {
                    this.customTypeInfoModal.loading = false;
                    this.customTypeLoading = false;
                    if (parseFloat(res.code) === 0) {
                        this.getDatasLocationFn(this.currentTrObj, this.datas[this.currentTrObj.indexAry[0]], 0);
                        this.customTypeInfoModal.alert = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                }
            );
        },
        // 【编辑】商品自定义分类
        async categoryEditAPIFn(editInfoFormNewObj) {
            // 传递给后端的参数，没有的值就不要传递了
            let params = {
                id: this.currentTrObj.type_inner_sn,
                ...editInfoFormNewObj
            };

            await categoryEditAPI(params.id, params).then((res) => {
                this.customTypeInfoModal.loading = false;
                this.customTypeLoading = false;
                if (parseFloat(res.code) === 0) {
                    this.getDatasLocationFn(this.currentTrObj, this.datas[this.currentTrObj.indexAry[0]], 0);
                    this.customTypeInfoModal.alert = false;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【查询】分类信息
        async dealerTypeInfoAPIFn(oid, tid) {
            await dealerTypeInfoAPI(oid, tid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    // 多选框赋值 1:有效赋值
                    this.checkboxGroupCursVal =
                        parseFloat(info.status) === 1 ? [this.checkboxGroupStatus[0].value] : [];
                    this.editInfoFormObj = { ...info };
                    this.editInfoFormOldObj = { ...info }; // 为了比对值的临时存储
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据id【查询分类信息】
        async categoryGetInfoAPIFn(id) {
            await categoryGetInfoAPI(id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    // 多选框赋值 1:有效赋值
                    this.checkboxGroupCursVal =
                        parseFloat(info.status) === 1 ? [this.checkboxGroupStatus[0].value] : [];
                    this.editInfoFormObj = { ...info };
                    this.editInfoFormOldObj = { ...info }; // 为了比对值的临时存储
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【顶级】产品分类
        async dealerGetParentListAPIFn() {
            await dealerGetParentListAPI(this.$cookie.get('userbelong'))
                .then((res) => {
                    this.customTypeLoading = false;
                    const { code, list } = res;
                    if (parseFloat(code) === 0) {
                        this.datas = list.map((item, index) => {
                            item.key = item.type_inner_sn;
                            item.activeClass = '';
                            item.status_text = this.statusAry[parseFloat(item.status)];
                            item.indexAry = [index];
                            item.parentDepartmentName = [item.type];
                            item.children = item.is_leaf === 0 ? [] : null; // is_leaf 1：没有下一级叶子节点   0：有
                            item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                            return item;
                        });
                        if (this.datas.length) {
                            this.currentTrObj = this.datas[0];
                        } else {
                            this.currentTrObj = {};
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.customTypeLoading = false;
                });
        },
        // 【获取所有的最上级/顶级】自定义分类
        async categoryGetParentListAPIFn() {
            await categoryGetParentListAPI()
                .then((res) => {
                    this.customTypeLoading = false;
                    const { code, list } = res;
                    if (parseFloat(code) === 0) {
                        this.datas = list.map((item, index) => {
                            item.key = item.type_inner_sn;
                            item.activeClass = '';
                            item.status_text = this.statusAry[parseFloat(item.status)];
                            item.indexAry = [index];
                            item.parentDepartmentName = [item.type];
                            item.children = item.is_leaf === 0 ? [] : null; // is_leaf 1：没有下一级叶子节点   0：有
                            item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                            return item;
                        });
                        if (this.datas.length) {
                            this.currentTrObj = this.datas[0];
                        } else {
                            this.currentTrObj = {};
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.customTypeLoading = false;
                });
        },
        // 根据id【查询下级分类信息列表】
        async dealerGetNextListAPIFn(record, isAddFlag) {
            await dealerGetNextListAPI(record.dealer_inner_sn, record.type_inner_sn).then((res) => {
                this.customTypeLoading = false;
                const { code, list } = res;
                if (parseFloat(code) === 0) {
                    const rowsNew = list.map((item, index) => {
                        item.key = item.type_inner_sn;
                        item.activeClass = '';
                        item.status_text = this.statusAry[parseFloat(item.status)];
                        item.indexAry = [...record.indexAry, index];
                        item.parentDepartmentName = [...record.parentDepartmentName, item.type];
                        item.children = item.is_leaf === 0 ? [] : null; // is_leaf 1：没有下一级叶子节点   0：有
                        item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                        return item;
                    });
                    record.children = rowsNew.length > 0 ? rowsNew : null;
                    record.loadingChildrenFlag = true;
                    if (isAddFlag) {
                        console.log(isAddFlag);
                        switch (this.userSystemType) {
                            case 0: // 平台
                                this.categoryAddAPIFn(record, this.datas);
                                break;
                            case 2: // 经销商
                                this.dealerTypeAddAPIFn(record, this.datas);
                                break;
                        }
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据id【查询下级分类信息列表】
        async categoryGetNextListAPIFn(record, isAddFlag) {
            console.log(record);
            await categoryGetNextListAPI(record && record.type_inner_sn).then((res) => {
                this.customTypeLoading = false;
                const { code, list } = res;
                if (parseFloat(code) === 0) {
                    const rowsNew = list.map((item, index) => {
                        item.key = item.type_inner_sn;
                        item.activeClass = '';
                        item.status_text = this.statusAry[parseFloat(item.status)];
                        item.indexAry = [...record.indexAry, index];
                        item.parentDepartmentName = [...record.parentDepartmentName, item.type];
                        item.children = item.is_leaf === 0 ? [] : null; // is_leaf 1：没有下一级叶子节点   0：有
                        item.loadingChildrenFlag = false; // 判断是否有加载过子级菜单【默认没有】
                        return item;
                    });
                    record.children = rowsNew.length > 0 ? rowsNew : null;
                    record.loadingChildrenFlag = true;
                    if (isAddFlag) {
                        switch (this.userSystemType) {
                            case 0: // 平台
                                this.categoryAddAPIFn(record, this.datas);
                                break;
                            case 2: // 经销商
                                this.dealerTypeAddAPIFn(record, this.datas);
                                break;
                        }
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 设置行属性 —— 点击每一行的事件
        customRowFn(record) {
            return {
                on: {
                    click: () => {
                        this.currentTrObj = record;
                    },
                    dblclick: (e) => {
                        if ((e.srcElement || e.target).classList.contains('ant-table-row-expand-icon')) return;
                        this.currentTrObj = record;
                        this.groupBtnFn('edit');
                    }
                }
            };
        },
        // 点击展开图标时触发
        expandFn(expanded, record, isAddFlag) {
            console.log(isAddFlag);
            if (expanded) {
                this.expandedRows.push(record.type_inner_sn);
            } else {
                this.expandedRows = this.expandedRows.filter((item) => item !== record.type_inner_sn);
            }
            if (!record.loadingChildrenFlag) {
                this.customTypeLoading = true;
                switch (this.userSystemType) {
                    case 0: // 平台
                        this.categoryGetNextListAPIFn(record, isAddFlag);
                        break;
                    case 2: // 经销商
                        this.dealerGetNextListAPIFn(record, isAddFlag);
                        break;
                }
            }
        },
        // 获取数据的索引值【坐标】
        getDatasLocationFn(record, list, i) {
            // record: 后端数据   list: 要改变的数据源  i: 索引
            let num = this.currentTrObj.indexAry;
            if (num.length <= 1) {
                // 只有一级数据
                switch (this.btnName) {
                    case 'addChild': // 新增下级部门
                        break;
                    case 'addSameLevel': // 新增同级部门
                        break;
                    case 'edit': // 修改
                        this.datas.splice(num[num.length - 1], 1, {
                            ...list,
                            status: this.editInfoFormObj.status,
                            status_text: this.statusAry[parseFloat(this.editInfoFormObj.status)],
                            type: this.editInfoFormObj.type
                        });
                        break;
                }
                return;
            }
            ++i; // 多级数据
            list.children.forEach((item, index) => {
                if (index === num[i]) {
                    switch (this.btnName) {
                        case 'addChild': // 新增下级部门
                            if (i === num.length - 1) {
                                // 根据索引的值来判断
                                if (!item.children) {
                                    item.children = [];
                                }
                                item.children.push({
                                    ...record
                                });
                                this.treeDatasIndexAryFn(this.datas);
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas);
                            }
                            break;
                        case 'addSameLevel': // 新增同级部门
                            if (num.length === 2) {
                                this.datas[num[num.length - 2]].children.push({
                                    ...record
                                });
                                this.treeDatasIndexAryFn(this.datas);
                                break;
                            }
                            if (i === num.length - 2) {
                                // 根据索引的值来判断
                                if (!item.children) {
                                    item.children = [];
                                }
                                item.children.push({
                                    ...record
                                });
                                this.treeDatasIndexAryFn(this.datas);
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas);
                            }
                            break;
                        case 'edit': // 修改
                            if (i === num.length - 1) {
                                // 根据索引的值来判断
                                list.children.splice(num[num.length - 1], 1, {
                                    ...item,
                                    status: this.editInfoFormObj.status,
                                    status_text: this.statusAry[parseFloat(this.editInfoFormObj.status)],
                                    type: this.editInfoFormObj.type
                                });
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas);
                            }
                            break;
                        case 'remove':
                            if (num.length === 2) {
                                this.datas[num[num.length - 2]].children.splice(num[num.length - 1], 1);
                                if (
                                    this.datas[num[num.length - 2]].children &&
                                    this.datas[num[num.length - 2]].children.length <= 0
                                ) {
                                    this.datas[num[num.length - 2]].children = null;
                                }
                            } else if (i === num.length - 2) {
                                // 根据索引的值来判断
                                item.children.splice(num[num.length - 1], 1);
                                if (item.children && item.children.length <= 0) {
                                    item.children = null;
                                }
                            } else {
                                this.getDatasLocationFn(record, item, i, this.datas);
                            }
                            this.treeDatasIndexAryFn(this.datas);
                            if (this.datas.length) {
                                this.currentTrObj = this.datas[0];
                            } else {
                                this.currentTrObj = {};
                            }
                            break;
                    }
                }
            });
        },
        // 递归加上索引
        treeDatasIndexAryFn(datasOrigin) {
            // datasOrigin: 源数据  isAppendDatas: 将数据放在哪里
            const _this = this;
            // 子级递归
            function childrenIndexFn(list, pIndexAry, parentDepartmentNameOld) {
                if (!pIndexAry || !parentDepartmentNameOld) return;
                return list.map((cItem, cIndex) => {
                    if (cItem.children && cItem.children.length > 0) {
                        let obj = {
                            ...cItem,
                            indexAry: [...pIndexAry, cIndex],
                            key: cItem.type_inner_sn,
                            activeClass: '',
                            status_text: _this.statusAry[parseFloat(cItem.status)],
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.type],
                            children: childrenIndexFn(
                                cItem.children,
                                [...pIndexAry, cIndex],
                                [...parentDepartmentNameOld, cItem.name]
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
                            key: cItem.type_inner_sn,
                            activeClass: '',
                            status_text: _this.statusAry[parseFloat(cItem.status)],
                            parentDepartmentName: [...parentDepartmentNameOld, cItem.type],
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
                if (item.children && item.children.length > 0) {
                    let obj = {
                        ...item,
                        indexAry: [index],
                        key: item.type_inner_sn,
                        activeClass: '',
                        status_text: _this.statusAry[parseFloat(item.status)],
                        parentDepartmentName: [item.type],
                        children: childrenIndexFn(item.children, [index], [item.type])
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
                        key: item.type_inner_sn,
                        activeClass: '',
                        status_text: _this.statusAry[parseFloat(item.status)],
                        parentDepartmentName: [item.type],
                        children: item.children
                    };
                    if (obj.key === _this.infoKey) {
                        // 新增下级同级的时候选中新增的那一条
                        _this.currentTrObj = obj;
                    }
                    return obj;
                }
            });

            this.datas = datasOriginNew;
        },
        // 表格的高亮样式显示
        rowClassFn(record) {
            if (record.key === this.currentTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        // 检测表格是否有选中的的数据
        checkCurrentTrDataFn() {
            if (Object.keys(this.currentTrObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        /* 顶部按钮的操作区域 */
        groupBtnFn(btnName) {
            this.customTypeInfoModalFn(btnName);
        },
        // 检测修改的表单信息校验
        checkEditInfoFormMsgFn() {
            if (!this.$Utils.trim(this.editInfoFormObj.type)) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入类型',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        // 检测并设置修改过的修改信息内容字段
        checkEditInfoFormObjFn() {
            let editInfoFlag = true;
            this.editInfoFormNewObj = {};
            for (let server in this.editInfoFormObj) {
                let serverItem = this.editInfoFormObj[server];
                for (let old in this.editInfoFormOldObj) {
                    let oldItem = this.editInfoFormOldObj[old];
                    if (server === old && serverItem !== oldItem) {
                        this.editInfoFormNewObj = {
                            ...this.editInfoFormNewObj,
                            [server]: serverItem, // 新值
                            [server + '_old']: oldItem // 旧值
                        };
                        editInfoFlag = false;
                    }
                }
            }
            return editInfoFlag;
        },
        // 表单默认值重置
        curtomTypeInfoModalResetFn() {
            this.editInfoFormObj = { ...this.currentTrObj.type_inner_sn };
            this.checkboxGroupCursVal = [];
            this.infoKey = '';
        },
        // 判断多选框修改了没有 【没有修改就不要向后端传值】
        checkboxGroupCursValChangeFn(checkedValues) {
            this.editInfoFormObj.status = parseFloat(checkedValues[0]) || 0;
        },
        // 新增/修改/删除按钮触发的函数
        customTypeInfoModalFn(btnName) {
            this.btnName = btnName; // 记录点击的是哪个按钮的标识
            if (this.btnName !== 'addSameLevel' && !this.checkCurrentTrDataFn()) return; // 除了新增同级分类按钮不需要选中表格中的某一行数据， 其它按钮都需要选中某一行才能进行下步操作
            this.curtomTypeInfoModalResetFn();
            switch (this.btnName) {
                case 'addChild': // 新增子分类
                    this.customTypeInfoModal = {
                        title: `${this.$route.meta.title} - 新增子分类`,
                        btnTitle: '新增子分类保存',
                        alert: true,
                        loading: false,
                        isShowStatus: false
                    };
                    this.$Utils.globalDragCenterFn('custom-type');
                    break;
                case 'addSameLevel': // 新增同级分类
                    this.customTypeInfoModal = {
                        title: `${this.$route.meta.title} - 新增同级分类`,
                        btnTitle: '新增同级分类保存',
                        alert: true,
                        loading: false,
                        isShowStatus: false
                    };
                    this.$Utils.globalDragCenterFn('custom-type');
                    break;
                case 'edit': // 修改
                    this.customTypeInfoModal = {
                        title: `${this.$route.meta.title} - 修改`,
                        btnTitle: '修改保存',
                        alert: true,
                        loading: false,
                        isShowStatus: true
                    };
                    this.$Utils.globalDragCenterFn('custom-type');
                    switch (this.userSystemType) {
                        case 0: // 平台
                            this.categoryGetInfoAPIFn(this.currentTrObj.type_inner_sn);
                            break;
                        case 2: // 经销商
                            this.dealerTypeInfoAPIFn(
                                this.currentTrObj.dealer_inner_sn,
                                this.currentTrObj.type_inner_sn
                            );
                            break;
                    }
                    break;
                case 'remove': // 删除
                    switch (this.userSystemType) {
                        case 0: // 平台
                            this.$warning({
                                title: '操作提示',
                                content: '不能删除',
                                maskClosable: false,
                                centered: true
                            });
                            break;
                        case 2: // 经销商
                            this.$warning({
                                title: '操作提示',
                                content: '不能删除',
                                maskClosable: false,
                                centered: true
                            });
                            break;
                        // const _this = this
                        // _this.$confirm({
                        //     title: '删除',
                        //     content: '确认要执行该操作吗？',
                        //     centered: true,
                        //     maskClosable: false,
                        //     onOk () {
                        //         _this.dealerTypeDelAPIFn()
                        //         // _this.getDatasLocationFn(_this.currentTrObj, _this.datas[_this.currentTrObj.indexAry[0]], 0) // 测试专用
                        //         return new Promise((resolve, reject) => {
                        //             setTimeout(Math.random() > 0.5 ? resolve : reject, 800)
                        //         }).catch(() => {})
                        //     }
                        // })
                        // break
                    }
                    break;
            }
        },
        // 模态框操作
        customTypeInfoModalOkFn() {
            if (!this.checkEditInfoFormMsgFn()) return; // 前端格式校验

            if (this.customTypeInfoModal.isShowStatus && this.checkEditInfoFormObjFn()) {
                // 修改按钮触发的保存要处理后端传值校验
                this.customTypeInfoModalCancelFn();
                return;
            }

            this.customTypeInfoModal.loading = true;
            if (this.customTypeInfoModal.isShowStatus) {
                // 修改按钮
                switch (this.userSystemType) {
                    case 0: // 平台
                        this.categoryEditAPIFn(this.editInfoFormNewObj, this.datas);
                        break;
                    case 2: // 经销商
                        this.dealerTypeEditAPIFn(this.editInfoFormNewObj, this.datas);
                        break;
                }
            } else {
                // 判断是否有加载过子级菜单
                if (this.currentTrObj.loadingChildrenFlag) {
                    // 加载过
                    switch (this.userSystemType) {
                        case 0: // 平台
                            this.categoryAddAPIFn(null, this.datas);
                            break;
                        case 2: // 经销商
                            this.dealerTypeAddAPIFn(null, this.datas);
                            break;
                    }
                } else {
                    // 没有加载过
                    if (this.btnName === 'addSameLevel') {
                        switch (this.userSystemType) {
                            case 0: // 平台
                                this.categoryAddAPIFn(null, this.datas);
                                break;
                            case 2: // 经销商
                                this.dealerTypeAddAPIFn(null, this.datas);
                                break;
                        }
                        return;
                    }
                    this.expandFn(true, this.currentTrObj, true);
                }
            }
        },
        customTypeInfoModalCancelFn() {
            this.customTypeInfoModal = {
                title: `${this.$route.meta.title}`,
                btnTitle: '保存',
                alert: false,
                loading: false,
                isShowStatus: false
            };
            this.editInfoFormNewObj = {};
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 173;
            return clientHeight - topHeight;
        }
    },
    created() {
        switch (this.userSystemType) {
            case 0: // 平台
                this.categoryGetParentListAPIFn();
                break;
            case 2: // 经销商
                this.dealerGetParentListAPIFn();
                break;
        }
    }
};
</script>

<style lang="less" scoped>
.modal-form-input-scoped {
    .modal-form-input {
        .label {
            width: auto;
        }
    }
}
</style>
