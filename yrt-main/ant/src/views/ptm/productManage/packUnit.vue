<template>
    <div class="scoped single-tb" style="min-height: 600px">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap" style="width: 800px">
                        <li>
                            <a-button @click="groupBtnFn('addSameLevel')" type="primary" size="small">
                                新增同级单位
                            </a-button>
                            <!--<a-button-->
                            <!--@click="groupBtnFn('addParent')"-->
                            <!--type="primary"-->
                            <!--size="small"-->
                            <!--&gt;-->
                            <!--新增上级单位-->
                            <!--</a-button>-->
                            <a-button @click="groupBtnFn('addChild')" type="primary" size="small">
                                新增下级单位
                            </a-button>
                            <a-button @click="groupBtnFn('edit')" type="primary" size="small"> 修改 </a-button>
                            <a-button @click="groupBtnFn('remove')" type="danger" size="small"> 删除 </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12"></a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- 表格区域开始 -->
        <div :class="hightSearchObj.isArrow ? 'top136' : 'top56'" class="tab-table-wrap">
            <div class="table-pd-bg">
                <a-table
                    :columns="columns"
                    :dataSource="datas"
                    :scroll="{ y: 500 }"
                    :pagination="false"
                    :loading="customTypeLoading"
                    :expandedRowKeys="chooseArr"
                    @expand="expandFn"
                    :customRow="customRowFn"
                >
                </a-table>
            </div>
        </div>
        <!-- 表格区域结束 -->

        <!-- 修改区域开始 -->
        <a-modal
            v-model="customTypeInfoModal.alert"
            :title="customTypeInfoModal.title"
            :centered="true"
            :maskClosable="false"
            class="modal-form-input-scoped global-drag edit"
        >
            <div v-globalDrag="{ el: 'edit' }" class="modal-header-wrap">
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
            <div class="modal-form-input modal-list" style="padding-top: 0">
                <ul>
                    <li class="item" style="padding-bottom: 10px">
                        <span class="label" style="width: 96px"><em class="em red">*</em>单位</span>
                        <a-input v-model="editInfoFormObj.name" class="input" />
                    </li>
                    <li class="item item-description" style="padding-bottom: 10px">
                        <span class="label" style="width: 96px; top: 5px"><em class="em red">*</em>{{ numType }}</span>
                        <a-input-number class="input" v-model="editInfoFormObj.measure" :min="0" />
                    </li>
                    <li class="item">
                        <span class="label" style="width: 96px">常用</span>
                        <a-checkbox @change="changeSwitch" :disabled="isOnly" :checked="switchType" />
                    </li>
                </ul>
            </div>
        </a-modal>
        <!-- 修改区域结束 -->
    </div>
</template>

<script>
import moment from 'moment';
import {
    platformUnitAdd,
    dealerUnitAdd,
    platformUnitEdit,
    dealerUnitEdit,
    platformUnitTree,
    dealerUnitTree,
    platformUnitNextList,
    platformUnitInfo,
    dealerUnitInfo
} from '@/service/pageAjax';

export default {
    name: 'packUnit',
    props: ['packUnitObj'],
    data() {
        return {
            chooseArr: [],
            numType: '',
            switchType: false,
            isOnly: false,
            columns: [
                {
                    title: '单位',
                    dataIndex: 'name',
                    key: 'name',
                    minWidth: '500',
                    align: 'center'
                },
                {
                    title: '最小计量/倍数',
                    dataIndex: 'measure',
                    key: 'measure',
                    minWidth: '200',
                    align: 'center'
                },
                {
                    title: '单位换算关系',
                    dataIndex: 'unit_rel',
                    key: 'unit_rel',
                    minWidth: '200',
                    align: 'center'
                },
                {
                    title: '常用',
                    dataIndex: 'is_common_use',
                    key: 'is_common_use',
                    minWidth: '100',
                    align: 'center',
                    customRender: (text) => {
                        let val;
                        if (text === 1) {
                            val = '是';
                        } else {
                            val = '否';
                        }
                        return val;
                    }
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
                title: this.$route.meta.title,
                btnTitle: '保存', // 保存按钮点击确认后的弹框提醒文字
                alert: false,
                loading: false,
                isShowStatus: false // 控制状态是否显示的标识
            },
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            currentTrObj: {}, // 表格选中的当前行数据 / 标识
            searchTables: {
                begin_time: '',
                end_time: '',
                name: '',
                status: ''
            }, // 表格的搜索并向后端请求字段
            rangePickerVal: moment[''], // 申请时间的默认赋值
            editInfoFormObj: {
                // 修改信息的模态框值 - 后端返回回来的
                parent_unit_inner_sn: '',
                name: '',
                measure: ''
            },
            editInfoFormOldObj: {}, // 修改信息的模态框值 - 后端返回回来的旧值
            editInfoFormNewObj: {}, // 修改信息的模态框值 - 前端根据页面修改的值放进去 【没有修改的不要向后端传递】
            checkboxGroupStatus: [
                // { label: '失效', value: 0 },
                { label: '', value: 1 } // 有效
            ], // 修改状态的模态框的多选框
            checkboxGroupCursVal: [], // 修改状态的模态框的当前选中的多选框值
            btnName: '', // 记录点击的是哪个按钮的标识
            recordTableCurrentTr: [], // 记录点击的是哪行table的tr，只记录父级的
            isInitTableTrFlag: false, // 记录是否可以重新发送第一次请求的标识
            expandedRowsChildren: [] // 记录除父级以外的unit_inner_sn
        };
    },
    methods: {
        // 【增加】商品自定义分类
        async platformUnitAddFn() {
            let params = {
                vendor_inner_sn: this.packUnitObj.vid,
                product_inner_sn: this.packUnitObj.pid,
                specification_inner_sn: this.packUnitObj.psid,
                parent_unit_inner_sn: this.editInfoFormObj.parent_unit_inner_sn,
                name: this.editInfoFormObj.name,
                measure: this.editInfoFormObj.measure
            };

            // 判断是新增下级单位还是新增同级单位的按钮 [ 自定义分类父ID ]
            if (this.btnName === 'addChild') {
                // addChild addSameLevel
                params.parent_unit_inner_sn = this.currentTrObj.unit_inner_sn;
            } else if (this.btnName === 'addParent') {
                let parent = this.currentTrObj.parent_unit_inner_sn;
                let circle = (arr) => {
                    // 递归找出父级
                    arr.forEach((item) => {
                        if (item.unit_inner_sn === parent) {
                            params.parent_unit_inner_sn = item.parent_unit_inner_sn;
                        } else if (item.children) {
                            circle(item.children);
                        }
                    });
                };
                this.datas.forEach((item) => {
                    if (item.unit_inner_sn === parent) {
                        params.parent_unit_inner_sn = item.parent_unit_inner_sn;
                    } else if (item.children) {
                        circle(item.children);
                    }
                });
            } else {
                params.parent_unit_inner_sn = this.currentTrObj.parent_unit_inner_sn
                    ? this.currentTrObj.parent_unit_inner_sn
                    : '';
            }
            // 该字段不是必填项,如果没有值就不要向后端发送该字段了
            if (this.editInfoFormObj.measure) {
                params.measure = this.editInfoFormObj.measure;
            }
            let url;
            let nowType = this.$cookie.get('userSystemType');
            if (parseInt(nowType) === 2) {
                url = dealerUnitAdd;
                params.dealer_inner_sn = params.vendor_inner_sn;
            } else {
                url = platformUnitAdd;
            }
            if (this.switchType) {
                params.is_common_use = 1;
            } else {
                params.is_common_use = 0;
            }
            await url(params).then((res) => {
                this.customTypeInfoModal.loading = false;
                this.customTypeLoading = false;
                if (parseFloat(res.code) === 0) {
                    let { info } = res;
                    this.uncomparativeFn();
                    this.isInitTableTrFlag = true;
                    this.unitGetParentListAPIFn({
                        ...info,
                        'isSaveCommonUseUnit': true
                    });
                    this.customTypeInfoModal.alert = false;
                    // 保存成功之后，通知给父组件 [该处通知父组件的代码移到获取单位的tree数据里面用来刷新父组件的单位值]
                    // this.$emit('handlePackUnitSaveDone')
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【编辑】商品自定义分类
        async platformUnitEditFn(editInfoFormNewObj) {
            // 传递给后端的参数，没有的值就不要传递了
            let params = {
                id: this.currentTrObj.unit_inner_sn,
                ...editInfoFormNewObj
            };
            let vid = this.packUnitObj.vid;
            let pid = this.packUnitObj.pid;
            let psid = this.packUnitObj.psid;
            let url;
            let nowType = this.$cookie.get('userSystemType');
            if (parseInt(nowType) === 2) {
                url = dealerUnitEdit;
            } else {
                url = platformUnitEdit;
            }
            if (this.switchType) {
                params.is_common_use = 1;
            } else {
                params.is_common_use = 0;
            }
            await url(vid, pid, psid, params.id, params).then((res) => {
                this.customTypeInfoModal.loading = false;
                this.customTypeLoading = false;
                if (parseFloat(res.code) === 0) {
                    this.uncomparativeFn();
                    this.isInitTableTrFlag = true;
                    this.unitGetParentListAPIFn({
                        ...this.currentTrObj,
                        'isSaveCommonUseUnit': true
                    });
                    this.customTypeInfoModal.alert = false;
                    // 保存成功之后，通知给父组件
                    this.$emit('handlePackUnitSaveDone');
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据id【查询分类信息】
        async platformUnitInfoFn(id) {
            let vid = this.packUnitObj.vid;
            let pid = this.packUnitObj.pid;
            let psid = this.packUnitObj.psid;
            let url;
            let nowType = this.$cookie.get('userSystemType');
            if (parseInt(nowType) === 2) {
                url = dealerUnitInfo;
            } else {
                url = platformUnitInfo;
            }
            await url(vid, pid, psid, id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { info } = res;
                    this.editInfoFormObj = { ...info };
                    this.editInfoFormOldObj = { ...info }; // 为了比对值的临时存储
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【获取所有的最上级/顶级】自定义分类
        async unitGetParentListAPIFn(newObj) {
            this.currentTrObj = {};
            let vid = this.packUnitObj.vid;
            let pid = this.packUnitObj.pid;
            let psid = this.packUnitObj.psid;
            let url;
            let nowType = this.$cookie.get('userSystemType');
            if (parseInt(nowType) === 2) {
                url = dealerUnitTree;
            } else {
                url = platformUnitTree;
            }
            await url(vid, pid, psid).then((res) => {
                this.customTypeLoading = false;
                const { code, tree } = res;
                this.chooseArr = [];
                if (parseFloat(code) === 0) {
                    let cycle = (arr, parent) => {
                        let parentName = '';
                        let mul = '1';
                        if (parent) {
                            parentName = parent.name;
                            if (!parent.parent_unit_inner_sn) {
                                mul = parent.measure;
                            }
                        }
                        arr.forEach((item) => {
                            item.key = item.unit_inner_sn;
                            item.activeClass = '';
                            item.unit_rel = '1' + item.name + ' = ' + item.measure + ' x ' + mul + parentName;
                            this.chooseArr.push(item.key);
                            newObj && newObj.isSaveCommonUseUnit && this.getCommonUseUnitVal(item);
                            if (item.children && item.children.length > 0) {
                                cycle(item.children, item);
                            } else {
                                item.children = null;
                            }
                        });
                    };
                    tree.forEach((item) => {
                        item.key = item.unit_inner_sn;
                        item.activeClass = '';
                        item.unit_rel = '';
                        this.chooseArr.push(item.key);
                        newObj && newObj.isSaveCommonUseUnit && this.getCommonUseUnitVal(item);
                        if (item.children && item.children.length > 0) {
                            cycle(item.children, item);
                        } else {
                            item.children = null;
                        }
                    });
                    this.datas = tree;
                    // if (this.isInitTableTrFlag && this.recordTableCurrentTr.length > 0) {
                    //     this.recordTableCurrentTr.forEach(item => {
                    //         this.platformUnitNextListFn(item)
                    //     })
                    //     this.recordTableCurrentTr = []
                    //     this.expandedRowsChildren = []
                    //     this.isInitTableTrFlag = false
                    // }
                    if (newObj) {
                        this.setDatasClassFn(newObj.unit_inner_sn);
                        this.currentTrObj = newObj;
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据id【查询下级分类信息列表】
        async platformUnitNextListFn(record) {
            let vid = this.packUnitObj.vid;
            let pid = this.packUnitObj.pid;
            let psid = this.packUnitObj.psid;
            const { unit_inner_sn: unitInnerSn, parent_unit_inner_sn: parentUnitInnerSn } = record;
            await platformUnitNextList(vid, pid, psid, unitInnerSn).then((res) => {
                this.customTypeLoading = false;
                const { code, list } = res;
                if (parseFloat(code) === 0) {
                    // 追加children
                    const rowsNew = list.map((item) => {
                        item.key = item.unit_inner_sn;
                        item.activeClass = '';
                        item.children = [];
                        return item;
                    });
                    if (!parentUnitInnerSn) {
                        // 判断是不是第一级菜单
                        this.datas.forEach((item) => {
                            if (item.unit_inner_sn === unitInnerSn) {
                                item.children = rowsNew.length > 0 ? rowsNew : null;
                            }
                        });
                    } else {
                        this.loopAppendChildrenFn(
                            {
                                unitInnerSn,
                                parentUnitInnerSn
                            },
                            rowsNew,
                            this.datas
                        ); // { id , 父级id }, 子元素数据, 表格数据
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 记录常用单位的值 [记录当前的 is_common_use === 1 的常用单位值]
        getCommonUseUnitVal(commonUseObj) {
            if (commonUseObj && parseInt(commonUseObj.is_common_use) === 1) {
                this.commonUseObj = commonUseObj;
                this.$emit('handlePackUnitSaveDone', commonUseObj);
            }
        },
        // 设置行属性 —— 点击每一行的事件
        customRowFn(record) {
            return {
                class: record.activeClass,
                on: {
                    click: () => {
                        this.setDatasClassFn(record.key);
                        this.currentTrObj = record;
                    },
                    dblclick: () => {
                        this.currentTrObj = record;
                        this.groupBtnFn('edit');
                    }
                }
            };
        },
        // 点击展开图标时触发
        expandFn(expanded, record) {
            let index = this.chooseArr.indexOf(record.key);
            if (!expanded) {
                this.chooseArr.splice(index, 1);
            } else {
                this.chooseArr.push(record.key);
            }
            // if (!expanded) return
            // if (this.expandedRows.some(item => item === record.unit_inner_sn)) return // 已经请求过的就不发请求
            // this.customTypeLoading = true
            // this.platformUnitNextListFn(record)
            // this.expandedRows.push(record.unit_inner_sn)
            // if (!record.parent_unit_inner_sn) { // 修复减号不能收起的bug
            //     if (this.recordTableCurrentTr.some(item => item.unit_inner_sn === record.unit_inner_sn)) return
            //     this.recordTableCurrentTr.push(record)
            // } else { // 追加除父级以外的unit_inner_sn
            //     if (this.expandedRowsChildren.some(item => item === record.unit_inner_sn)) return
            //     this.expandedRowsChildren.push(record.unit_inner_sn)
            // }
        },
        // 递归动态插入子元素到对应的父级元素的children中 01
        loopAppendChildrenFn(recordObj, rowsNew, datas) {
            // id , 父级id, 子元素数据
            const { parentUnitInnerSn } = recordObj;
            let statusFlag = true;

            for (let i = 0; i < datas.length; i++) {
                let cItem = datas[i];
                if (cItem.unit_inner_sn === parentUnitInnerSn) {
                    // 第二级
                    this.loopAppendChildrenTreeFn(recordObj, rowsNew, cItem.children);
                    statusFlag = false;
                    break;
                }
            }

            if (statusFlag) {
                // 第三级 ...
                datas.forEach((item) => {
                    if (!item.children || rowsNew.length <= 0) {
                        this.loopAppendChildrenTreeFn(recordObj, rowsNew, item.children);
                        return;
                    }
                    item.children.forEach((cItem) => {
                        if (cItem.children) {
                            this.loopAppendChildrenTreeFn(recordObj, rowsNew, cItem.children);
                        }
                    });
                });
            }
        },
        // 递归动态插入子元素到对应的父级元素的children中 02
        loopAppendChildrenTreeFn(recordObj, rowsNew, cItem) {
            const { unitInnerSn } = recordObj;
            let statusFlag = true;

            if (!cItem) return;
            for (let j = 0; j < cItem.length; j++) {
                let cItemNew = cItem[j];
                if (cItemNew.unit_inner_sn === unitInnerSn) {
                    if (rowsNew.length > 0) {
                        cItemNew.children = rowsNew;
                    } else {
                        cItemNew.children = null;
                    }
                    statusFlag = false;
                    break;
                }
            }

            if (statusFlag) {
                // 更多级
                this.loopAppendChildrenFn(recordObj, rowsNew, cItem);
            }
        },
        // 申请时间的搜索改变事件
        changeRangePickerFn(value, dateString) {
            const [beginTime, endTime] = dateString;
            this.searchTables = {
                ...this.searchTables,
                begin_time: beginTime,
                end_time: endTime
            };
        },
        getCurrentStyle(current) {
            const style = {};
            if (current.date() === 1) {
                style.border = '1px solid #1890ff';
                style.borderRadius = '50%';
            }
            return style;
        },
        // 根据一个数组，去除另一个重复的元素
        uncomparativeFn() {
            for (let i = 0; i < this.expandedRows.length; i++) {
                const pItem = this.expandedRows[i];
                for (let j = 0; j < this.expandedRowsChildren.length; j++) {
                    const cItem = this.expandedRowsChildren[j];
                    if (pItem === cItem) {
                        this.expandedRows.splice(i, 1);
                        i--;
                        continue;
                    }
                }
            }
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
            // 单位
            let name = this.editInfoFormObj.name;
            if (name) {
                name = this.$Utils.trim(name);
            }
            if (!name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入单位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            // 最小计量/倍数
            if (!this.editInfoFormObj.measure) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入最小计量/倍数，且只能为正数',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            let v4 = /^([1-9]\d{0,3}|0)(\.\d{1,4})?$/;
            if (!v4.test(this.editInfoFormObj.measure)) {
                this.$warning({
                    title: '操作提示',
                    content: '最小计量/倍数格式错误，整数必须小于9999且最多保留4位小数',
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
            this.editInfoFormNewObj = this.editInfoFormObj;
            // for (let server in this.editInfoFormObj) {
            //     let serverItem = this.editInfoFormObj[server]
            //     for (let old in this.editInfoFormOldObj) {
            //         let oldItem = this.editInfoFormOldObj[old]
            //         if (server === old && serverItem !== oldItem) {
            //             this.editInfoFormNewObj = {
            //                 ...this.editInfoFormNewObj,
            //                 [server]: serverItem, // 新值
            //                 [server + '_old']: oldItem // 旧值
            //             }
            //             editInfoFlag = false
            //         }
            //     }
            // }
            editInfoFlag = false;
            return editInfoFlag;
        },
        // 表单默认值重置
        curtomTypeInfoModalResetFn() {
            this.editInfoFormObj = { ...this.currentTrObj.unit_inner_sn };
            this.checkboxGroupCursVal = [];
        },
        // 判断多选框修改了没有 【没有修改就不要向后端传值】
        checkboxGroupCursValChangeFn(checkedValues) {
            this.editInfoFormObj.status = parseFloat(checkedValues[0]) || 0;
        },
        // 新增/修改/删除按钮触发的函数
        customTypeInfoModalFn(btnName) {
            this.btnName = btnName; // 记录点击的是哪个按钮的标识
            // 除了新增同级单位按钮不需要选中表格中的某一行数据， 其它按钮都需要选中某一行才能进行下步操作
            if (btnName !== 'addSameLevel' && !this.checkCurrentTrDataFn()) return;
            this.curtomTypeInfoModalResetFn();
            switch (btnName) {
                // 新增上级单位
                case 'addParent': {
                    let parent = this.currentTrObj.parent_unit_inner_sn;
                    if (!parent) {
                        this.$warning({
                            title: '操作提示',
                            content: '请选择下级单位',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        return;
                    }
                    this.customTypeInfoModal = {
                        title: `${this.$route.meta.title} - 规格型号 - 包装单位 - 新增下级单位`,
                        btnTitle: '新增下级单位保存',
                        alert: true,
                        loading: false,
                        isShowStatus: false
                    };
                    this.numType = '倍数';
                    this.switchType = false;
                    this.$Utils.globalDragCenterFn('edit');
                    break;
                }
                case 'addChild': // 新增下级单位
                    this.customTypeInfoModal = {
                        title: `${this.$route.meta.title} - 规格型号 - 包装单位 - 新增下级单位`,
                        btnTitle: '新增下级单位保存',
                        alert: true,
                        loading: false,
                        isShowStatus: false
                    };
                    this.numType = '倍数';
                    this.switchType = false;
                    if (this.datas.length <= 0) {
                        this.isOnly = true;
                    } else {
                        this.isOnly = false;
                    }
                    this.$Utils.globalDragCenterFn('edit');
                    break;
                case 'addSameLevel': // 新增同级单位
                    if (this.datas.length >= 1 && !this.currentTrObj.parent_unit_inner_sn) {
                        this.$warning({
                            title: '操作提示',
                            content: '包装单位只能有一个“最小计量',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        return;
                    }
                    this.customTypeInfoModal = {
                        title: `${this.$route.meta.title} - 规格型号 - 包装单位 - 新增同级单位`,
                        btnTitle: '新增同级单位保存',
                        alert: true,
                        loading: false,
                        isShowStatus: false
                    };
                    if (this.currentTrObj.parent_unit_inner_sn) {
                        this.numType = '倍数';
                    } else {
                        this.numType = '最小计量';
                    }
                    if (this.datas.length <= 0) {
                        this.isOnly = true;
                        this.switchType = true;
                    } else {
                        this.isOnly = false;
                        this.switchType = false;
                    }
                    this.$Utils.globalDragCenterFn('edit');
                    break;
                case 'edit': // 修改
                    this.customTypeInfoModal = {
                        title: `${this.$route.meta.title} - 规格型号 - 包装单位 - 修改`,
                        btnTitle: '修改保存',
                        alert: true,
                        loading: false,
                        isShowStatus: true
                    };
                    if (this.currentTrObj.parent_unit_inner_sn) {
                        this.numType = '倍数';
                    } else {
                        this.numType = '最小计量';
                    }
                    if (this.datas.length <= 0) {
                        this.isOnly = true;
                        this.switchType = true;
                    } else if (this.currentTrObj.is_common_use === 1) {
                        this.isOnly = false;
                        this.switchType = true;
                    } else {
                        this.isOnly = false;
                        this.switchType = false;
                    }
                    this.platformUnitInfoFn(this.currentTrObj.unit_inner_sn);
                    this.$Utils.globalDragCenterFn('edit');
                    break;
                case 'remove': // 删除
                    this.$warning({
                        title: '操作提示',
                        content: '该功能在开发中，敬请期待',
                        maskClosable: false,
                        centered: true
                    });
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

            const _this = this;
            _this.customTypeInfoModal.loading = true;
            if (_this.customTypeInfoModal.isShowStatus) {
                // 修改按钮
                _this.platformUnitEditFn(_this.editInfoFormNewObj);
            } else {
                _this.platformUnitAddFn(_this.editInfoFormNewObj);
            }
        },
        customTypeInfoModalCancelFn() {
            this.customTypeInfoModal = {
                title: `${this.$route.meta.title} - 信息`,
                btnTitle: '保存',
                alert: false,
                loading: false,
                isShowStatus: false
            };
            this.editInfoFormNewObj = {};
        },
        // 设置父级背景颜色的高亮
        setDatasClassFn(key) {
            let circleClear = (arr) => {
                // 清空选中
                arr.forEach((item) => {
                    item.activeClass = '';
                    if (item.children && item.children.length > 0) {
                        circleClear(item.children);
                    }
                });
            };
            this.datas.forEach((item) => {
                // 清空选中
                item.activeClass = '';
                if (item.children && item.children.length > 0) {
                    circleClear(item.children);
                }
            });

            let circle = (arr) => {
                // 赋值递归方法
                arr.forEach((item) => {
                    if (item.key === key) {
                        item.activeClass = 'active-tr';
                    } else if (item.children && item.children.length > 0) {
                        item.activeClass = '';
                        circle(item.children);
                    }
                });
            };
            this.datas.forEach((item) => {
                if (item.key === key) {
                    item.activeClass = 'active-tr';
                } else if (item.children && item.children.length > 0) {
                    item.activeClass = '';
                    circle(item.children);
                }
            });
        },
        // 设置子级背景颜色的高亮
        setChildrenBgCurFn() {
            let tbodyEle = document.getElementsByClassName('ant-table-tbody')[0],
                trEle = tbodyEle.getElementsByTagName('tr');
            tbodyEle.onclick = function (e) {
                let ev = window.event || e,
                    target = ev.srcElement || ev.target;

                // 得到 tr
                while (target.tagName.toLowerCase() !== 'tr') {
                    target = target.parentNode;
                }

                for (let i = 0; i < trEle.length; i++) {
                    trEle[i].classList.remove('active-tr');
                }
                target.classList.add('active-tr');
            };
        },
        unitPageInit() {
            // 初始化
            this.datas = [];
            this.expandedRows = [];
            this.customTypeLoading = true;
            this.isInitTableTrFlag = false;
            this.recordTableCurrentTr = [];
            this.unitGetParentListAPIFn();
            this.setChildrenBgCurFn();
        },
        changeSwitch(status) {
            this.switchType = status.target.checked;
        },
        changeNum(val) {
            let v4 = /^(\d+|\d+\.\d{1,2})$/;
            if (v4.test(val)) {
                this.editInfoFormObj.measure = val;
            } else {
                this.editInfoFormObj.measure = 0;
            }
        }
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = 0;
            return clientHeight - topHeight;
        }
    },
    created() {
        this.unitGetParentListAPIFn();
    },
    mounted() {
        this.setChildrenBgCurFn();
    }
};
</script>

<style lang="less" scoped>
.single-tb {
    margin: -24px;
}
.modal-form-input-scoped {
    .modal-form-input {
        .label {
            width: auto;
        }
    }
}
</style>
