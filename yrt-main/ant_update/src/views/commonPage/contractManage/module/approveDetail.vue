<template>
    <div class="modal-tabs-pg">
        <div>
            <div class="tab_box">
                <a-tabs :activeKey="activeTab" @change="changeTabsFn" type="card">
                    <a-tab-pane v-for="item of tabName" :key="item.key" :tab="item.name">
                        <div v-show="item.key == 'basic'" class="modal-form-input modal-list">
                            <ul>
                                <li class="item">
                                    <span class="label">合同名称</span>
                                    <a-input class="input" disabled="true" v-model="formData.name" />
                                </li>
                                <li class="item">
                                    <span class="label">合同编号</span>
                                    <a-input class="input" disabled="true" v-model="formData.contract_sn" />
                                </li>
                                <li class="item">
                                    <div class="item-children">
                                        <span class="label">甲方</span>
                                        <a-input
                                            disabled="true"
                                            readonly="readonly"
                                            class="input"
                                            @click="openChooseTb"
                                            v-model="part_a_obj.name"
                                        />
                                    </div>
                                    <div class="item-children">
                                        <span class="label">电话</span>
                                        <a-input disabled="true" class="input" v-model="formData.partA_phone" />
                                    </div>
                                </li>
                                <li class="item">
                                    <span class="label">地址</span>
                                    <a-input disabled="true" class="input" v-model="formData.partA_address" />
                                </li>
                                <li class="item">
                                    <div class="item-children">
                                        <span class="label">乙方</span>
                                        <a-input
                                            disabled="true"
                                            readonly="readonly"
                                            class="input"
                                            @click="openChooseTb"
                                            v-model="part_b_obj.name"
                                        />
                                    </div>
                                    <div class="item-children">
                                        <span class="label">电话</span>
                                        <a-input disabled="true" class="input" v-model="formData.partB_phone" />
                                    </div>
                                </li>
                                <li class="item">
                                    <span class="label">地址</span>
                                    <a-input disabled="true" class="input" v-model="formData.partB_address" />
                                </li>
                                <li class="item">
                                    <div class="item-children" style="width: 35%">
                                        <span class="label">回款周期</span>
                                        <a-input-number
                                            disabled="true"
                                            readonly="readonly"
                                            style="width: 170px"
                                            class="input"
                                            v-model="formData.payment_period"
                                        />
                                        <span class="tail">天</span>
                                    </div>
                                    <div class="item-children" style="width: 65%">
                                        <span class="label">有效期</span>
                                        <a-range-picker
                                            disabled="true"
                                            :value="validDate"
                                            class="input"
                                            :placeholder="['开始日期', '结束日期']"
                                            format="YYYY-MM-DD"
                                        />
                                    </div>
                                </li>
                                <li class="item">
                                    <span class="label">销售代表</span>
                                    <a-input disabled="true" class="input" v-model="formData.employee_name" />
                                </li>
                                <li class="item" v-if="saleArea">
                                    <span class="label">销售区域</span>
                                    <a-tree-select
                                        treeNodeFilterProp="title"
                                        :showSearch="true"
                                        style="min-height: 32px"
                                        :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                        :treeData="depList"
                                        v-model="departments"
                                        disabled="true"
                                        multiple
                                        treeDefaultExpandAll
                                        class="input"
                                    >
                                    </a-tree-select>
                                </li>
                                <li class="item">
                                    <div class="item-children" style="width: 33%">
                                        <span class="label">签订日期</span>
                                        <a-date-picker
                                            style="width: 182px"
                                            :value="signDate"
                                            placeholder=""
                                            format="YYYY-MM-DD"
                                            @change="changeSignDate"
                                        />
                                    </div>
                                    <div class="item-children" style="width: 65%">
                                        <span class="label" style="margin-left: 24px">录入人：</span
                                        >{{ formData.inputer_name }}
                                    </div>
                                </li>
                            </ul>
                        </div>
                        <div v-show="item.key == 'main'">
                            <div class="main_box">
                                <a-textarea
                                    disabled="true"
                                    v-model="formData.content"
                                    style="min-height: 462px; max-height: 462px; resize: none"
                                />
                            </div>
                        </div>
                        <div v-show="item.key == 'product'" class="product_box scoped">
                            <!--<div class="btn_count">-->
                            <!--<a-button class="ant-btn-sm" @click="openAddPro" style="margin-right: 10px" type="primary">添加</a-button>-->
                            <!--<a-button class="ant-btn-sm" @click="delProList" style="margin-right: 10px" type="danger">删除</a-button>-->
                            <!--<a-button class="ant-btn-sm" type="primary">销售冲突分析</a-button>-->
                            <!--</div>-->
                            <div class="tab-table-wrap">
                                <vxe-grid
                                    ref="xTableContractProduct"
                                    size="small"
                                    :columns="proColumns"
                                    height="464"
                                    resizable
                                    auto-resize
                                    border
                                    highlight-current-row
                                    align="center"
                                    :data="proTableData"
                                    :loading="productPage.loading"
                                    @cell-dblclick="cellDblclickFn"
                                >
                                </vxe-grid>
                            </div>
                        </div>
                        <div v-show="item.key == 'accessory'" class="accessory_box scoped">
                            <div class="upload_box only_show">
                                <a-upload-dragger
                                    name="file"
                                    :multiple="true"
                                    :action="QiniuUrl"
                                    :data="upLoadparams"
                                    disabled="true"
                                    :remove="
                                        () => {
                                            return false;
                                        }
                                    "
                                    :defaultFileList="defaultFileList"
                                    @change="uploadChange"
                                >
                                    <p class="ant-upload-drag-icon" style="margin-bottom: 5px">
                                        <a-icon type="inbox" />
                                    </p>
                                    <p class="ant-upload-text">单击或拖动文件到此区域上传</p>
                                    <p class="ant-upload-hint">请上传小于40M的附件</p>
                                </a-upload-dragger>
                            </div>
                        </div>
                    </a-tab-pane>
                </a-tabs>
            </div>
            <div
                class="approve_box"
                v-if="this.editObj.status === 0 || this.editObj.status === 1 || this.editObj.status === 2"
            >
                <div class="top_box">
                    <a-button v-if="!isLook" type="primary" size="small" @click="openComment"> 评论 </a-button>
                </div>
                <div class="time_line_box">
                    <a-timeline>
                        <a-timeline-item v-for="(item, index) of commentList" :key="index">
                            <div slot="dot" class="head_icon">
                                <img src="../../../../../public/images/logo/custom_head.png" />
                                <!--{{item.approver_name}}-->
                            </div>
                            <div class="info_box">
                                <div class="tit_box">
                                    <span :class="{ 'focus': item.status == 2 }" class="name_box"
                                        >{{ item.approver_name }}（{{ commentStatus[item.status] }}）</span
                                    >
                                    <span class="date_box">{{ item.approve_time }}</span>
                                </div>
                                <div class="operate_box">
                                    <div class="des_box" v-if="item.description">
                                        <div class="key">审批意见：</div>
                                        <div class="txt">{{ item.description }}</div>
                                    </div>
                                    <div v-for="(itm, idx) of item.approveComment" :key="idx" class="comments_box">
                                        <div>
                                            评论：<span class="time_box">{{ itm.comment_time }}</span></div
                                        >
                                        <div class="text_box">{{ itm.comment }}</div>
                                    </div>
                                </div>
                            </div>
                        </a-timeline-item>
                    </a-timeline>
                    <div class="no_comment" v-if="commentList.length <= 0"> 暂无审批流程 </div>
                </div>
                <div class="approve_count" v-if="isApprove">
                    <div class="choose_box">
                        <span style="margin-right: 12px">审批结果</span>
                        <a-radio-group v-model="approveRadio">
                            <a-radio :value="1">同意</a-radio>
                            <a-radio :value="2">不同意</a-radio>
                            <a-radio :value="3">待定</a-radio>
                        </a-radio-group>
                    </div>
                    <div class="op_area">
                        <a-textarea v-model="approveDescription" style="resize: none" placeholder="" :rows="4" />
                    </div>
                </div>
            </div>
        </div>
        <a-button v-if="activeTab == 'main'" type="primary" style="float: left; left: 24px; top: 13px"
            >合同范本</a-button
        >
        <a-button
            @click="contractApproveFn"
            v-if="isApprove"
            type="primary"
            style="float: right; right: 22px; top: 13px"
            >保存</a-button
        >
        <a-button @click="cancelFn" style="float: right; right: 34px; top: 13px">关闭</a-button>
        <a-button v-if="editObj.c_type === 'look'" @click="exportExcelFn" style="float: right; right: 44px; top: 13px"
            >导出Excel</a-button
        >
        <a-modal
            :title="addCommentMd.title"
            :visible="addCommentMd.alert"
            :maskClosable="false"
            :width="300"
            :centered="true"
            @cancel="addCommentMd.alert = false"
            class="modal-form-input-scoped global-drag approve-comment-md"
        >
            <div v-globalDrag="{ el: 'approve-comment-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addCommentMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="addCommentMd.alert = false">取消</a-button>
                <a-button :loading="addCommentMd.loading" type="primary" @click="saveComment">保存</a-button>
            </template>
            <div class="comment_box">
                <a-textarea v-model="approveComment" style="resize: none" placeholder="" :rows="4" />
            </div>
        </a-modal>
        <a-modal
            title="录入"
            :visible="entryStVisible"
            :width="1150"
            :maskClosable="false"
            :centered="true"
            @cancel="entryStVisible = false"
            class="modal-form-input-scoped global-drag entryApproveDetailModal"
        >
            <div v-globalDrag="{ el: 'entryApproveDetailModal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    医疗器械注册证（登记表） - 查看
                </h6>
            </div>
            <entry-gic
                ref="mdrfGicPage"
                :gicMdrfObj="gicMdrfObj"
                @returnOtherCertImgs="getOtherCertImgsForPrint"
            ></entry-gic>
            <template slot="footer">
                <a-button @click="handlePrint" type="primary">
                    <span>打印</span>
                </a-button>
                <a-button key="back" @click="entryStVisible = false">
                    <span>关闭</span>
                </a-button>
            </template>
        </a-modal>
    </div>
</template>

<script>
import {
    contractInfoAPI,
    platformAgenecyBaseInfoAPI,
    orgDeptAllTreeAPI,
    getUploadFileTokenAPI,
    dealerUnitTreeAPI,
    platformUnitTree,
    contractApproveCommentlistAPI,
    contractApproveAPI,
    approveCommentAddAPI,
    orgAllEmpDropdownListAPI,
    platformProductMdrfNewestListAPI
} from '@/service/pageAjax';

import entryGic from '@/views/ptm/productManage/module/entryGic';
import moment from 'moment';

export default {
    name: 'approveDetail',
    props: ['partType', 'editObj'],
    components: {
        entryGic
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            entryStVisible: false,
            upToken: '',
            approveRadio: 1,
            upLoadparams: {},
            userSystemType: '', // 当前机构
            addCommentMd: {
                title: `${this.$route.meta.title} - 评论`,
                alert: false,
                loading: false
            },
            departList: [],
            textAreaSet: {
                minRows: 4,
                maxRows: 6
            },
            activeTab: 'basic',
            tabName: [
                {
                    name: '基本信息',
                    key: 'basic'
                },
                {
                    name: '内容',
                    key: 'main'
                },
                {
                    name: '相关产品',
                    key: 'product'
                },
                {
                    name: '附件',
                    key: 'accessory'
                }
            ],
            empList: [],
            departments: [],
            depList: [],
            saleArea: false,
            formData: {
                name: '', // 合同名称
                contract_sn: '', // 合同编号
                hospital_inner_sn: '', // 医院内部编码
                dealer_inner_sn: '', // 经销商内部编码
                payment_period: '', // 回款周期
                begin_date: '',
                end_date: '',
                employee_inner_sn: '', // 销售代表内部编号
                hospital_department_inner_sn: '', // 销售区域
                sign_date: null, // 签订日期
                content: '' // 内容
            },
            validDate: null,
            signDate: null,
            isPartA: true,
            isPartB: false,
            part_a_obj: {},
            part_b_obj: {},
            department_inner_sn: '',
            proColumns: [
                {
                    title: 'NO.',
                    field: 'sort_number',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '7%',
                    align: 'center',
                    slots: {
                        default: ({ seq }) => {
                            let html = [
                                <div>
                                    <span>{seq}</span>
                                </div>
                            ];
                            return html;
                        }
                    }
                },
                {
                    title: '名称',
                    field: 'name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'name',
                    minWidth: '19%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.name];
                        }
                    }
                },
                {
                    title: '别名',
                    field: 'a',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'a',
                    minWidth: '10%',
                    align: 'center'
                },
                {
                    title: '规格型号',
                    field: 'specification_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'specification',
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification];
                        }
                    }
                },
                {
                    title: '厂商/经销商',
                    field: 'sys_type',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'sys_type',
                    minWidth: '15%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.sys_type];
                        }
                    }
                },
                {
                    title: '单位',
                    field: 'unit_name',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'unit_name',
                    minWidth: '6%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.unit_name];
                        }
                    }
                },
                {
                    title: '含税单价（元）',
                    field: 'price',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    key: 'price',
                    minWidth: '10%',
                    align: 'center',
                    slots: {
                        default: ({ row }) => {
                            return [row.price];
                        }
                    }
                },
                {
                    title: '注册证',
                    field: 'hasCertImage',
                    filed: 'hasCertImage',
                    align: 'center',
                    minWidth: '8%',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [
                                row.hasCertImage ? <a-tag color="#87d068">有</a-tag> : <a-tag color="#f50">无</a-tag>
                            ];
                        }
                    }
                },
                {
                    title: '操作',
                    field: 'inquirer_name',
                    minWidth: '10%',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            this.chooseTrObj = row;
                            return [
                                <div>
                                    {row.product_type === 1 ? (
                                        <a-tooltip placement="top" title="注册证">
                                            <i
                                                onClick={() => this.showRegistrationCertificate(row)}
                                                class="yrt yrt-zhucezheng zhucezheng_ico"
                                            ></i>
                                        </a-tooltip>
                                    ) : null}
                                </div>
                            ];
                        }
                    }
                }
            ],
            chooseTrObj: {},
            isChooseTr: false,
            proTableData: [
                {
                    code68_name: '栓塞器材',
                    code68_sn: 687703,
                    create_employee_inner_sn: 1,
                    dealer_inner_sn: 3,
                    dealer_name: '龙山医疗经销商',
                    description: '',
                    health_care_sn: '',
                    health_care_type: 0,
                    isActive: '',
                    is_delete: 0,
                    key: '31',
                    name: '雷火灸(商品名：赵氏雷火灸)',
                    price: '22',
                    product_inner_sn: 1,
                    product_sn: '',
                    specifications:
                        '25g×100支,25g×50支,25g×10支,25g×5支,25g×3支,25g×2支,25g×1支,13g×100支,13g×50支,13g×10支,13g×6支,13g×4支,13g×2支,13g×1支',
                    status: 1,
                    storage_condition: 0,
                    product_type: 2,
                    type_inner_sn: 1,
                    type_name: '自定义分类1',
                    unit_inner_sn: ['zhejiang', 'hangzhou', 'xihu']
                }
            ],
            unitList: [],
            productPage: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageSize
                pageSize: 10, // 对应后端的 pageNum
                pageNum: 1
            },
            uploadList: [], // 附件数据
            defaultFileList: [],
            commentList: [],
            commentStatus: ['发起申请 ', '未处理', '审批中', '同意', '不同意'],
            isApproveFinished: false,
            isApprove: false, // 判断是否当前审批
            approverIsn: '',
            approveDescription: '',
            approveComment: '',
            isLook: false,
            gicMdrfObj: {
                // 注册证相关信息
                address: '',
                application_range: null,
                contraindication: null,
                description: null,
                images: [],
                issue_date: null,
                issue_organization: null,
                new: false,
                performance_structure: null,
                product_name: '',
                production_address: null,
                register_sn: null,
                specifications: '',
                standard: null,
                vendor_name: '',
                isOnlyView: true // 是否只查看注册证信息
            },
            otherCertImgs: [] // 针对于打印的，“营业执照”和“医疗器械生产许可证”的图片数组，从entry-gic组件中回传回来
        };
    },
    methods: {
        async pageRefresh() {
            // 页面初始化
            if (this.editObj.c_type === 'look') {
                // 预览合同
                this.isLook = true;
            }
            this.proTableData = [];
            const type = this.editObj.contract_type;
            let sysTypeA;
            let sysTypeB;
            if (type === 0) {
                // 医院经销商
                sysTypeA = 1;
                sysTypeB = 2;
            }
            if (type === 1) {
                // 医院厂商
                sysTypeA = 1;
                sysTypeB = 3;
            }
            if (type === 2) {
                // 经销商厂商
                sysTypeA = 2;
                sysTypeB = 3;
            }
            if (type === 3) {
                // 经销商经销商
                sysTypeA = 2;
                sysTypeB = 2;
            }
            this.approveDescription = '';
            this.approveRadio = 1;
            this.getApproveComFn(); // 审批加载
            this.activeTab = 'basic';
            let params = {
                partA: this.editObj.partA,
                partB: this.editObj.partB,
                contract_inner_sn: this.editObj.contract_inner_sn,
                contract_type: this.editObj.contract_type
            };
            await contractInfoAPI({ params: params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.formData = res.info;
                    // this.oldFormData = JSON.parse(JSON.stringify(res.info))
                    this.part_a_obj.name = res.info.partA_name;
                    this.part_a_obj.id = res.info.partA;
                    this.part_a_obj.systemType = sysTypeA;
                    this.part_b_obj.name = res.info.partB_name;
                    this.part_b_obj.id = res.info.partB;
                    this.part_b_obj.systemType = sysTypeB;
                    this.orgDeptAllTreeAPIFn();
                    this.getSelEmp(this.part_b_obj.systemType, this.part_b_obj.id);
                    this.departments = [];
                    if (this.part_a_obj.systemType === 1) {
                        this.saleArea = true;
                        if (res.info.hospital_dept_inner_sn) {
                            // 销售区域
                            let arr1 = res.info.hospital_dept_inner_sn.split(',');
                            let art2 = [];
                            arr1.forEach((item) => {
                                let num = parseInt(item);
                                art2.push(num);
                            });
                            this.departments = art2;
                        }
                    } else {
                        this.saleArea = false;
                    }
                    this.validDate = [moment(res.info.begin_date), moment(res.info.end_date)];
                    if (res.info.sign_date) {
                        this.signDate = moment(res.info.sign_date);
                    } else {
                        this.signDate = null;
                    }
                    // 产品
                    this.proTableData = res.info.details;
                    this.proTableData.forEach((item) => {
                        item.name = item.product_name;
                        item.key = item.product_inner_sn + '' + item.specification_inner_sn;
                        item.specification = item.specification_name;
                        if (item.product_vendor_name) {
                            item.sys_type = item.product_vendor_name;
                            // this.venUnitTreeFn(item)
                        } else {
                            // item.sys_type = item.dealer_name
                            item.sys_type = item.product_vendor_name;
                            item.dealer_inner_sn = item.partB;
                            // this.dearUnitTreeFn(item)
                        }
                    });
                    this.loadDataFn();
                    // 附件
                    this.uploadList = res.info.attachments;
                    this.defaultFileList = [];
                    this.uploadList.forEach((item, index) => {
                        let obj = {
                            uid: index,
                            name: item.attachment_name,
                            url: '/v1/att/get/' + item.attachment_url + '?filename=' + item.attachment_name
                        };
                        this.defaultFileList.push(obj);
                    });
                }
            });
        },
        changeTabsFn(val) {
            // tab切换
            this.activeTab = val;
        },
        openChooseTb() {
            // 打开选择甲乙方
            this.chooseOrgMd.alert = true;
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
        },
        /** 基本信息**/
        displayRender(obj) {
            let len = obj.labels.length;
            const label = obj.labels[len - (len - 1)];
            return label ? label.split('(')[0] : '';
        },
        async getSelfOrgInfo() {
            // 获取当前机构信息
            let systemType = this.$cookie.get('userSystemType');
            this.userSystemType = systemType;
            let id = this.$cookie.get('userbelong');
            await platformAgenecyBaseInfoAPI(systemType, id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (res.info) {
                        if (this.partType === 1) {
                            this.part_a_obj = res.info;
                            this.part_a_obj.systemType = parseFloat(systemType);
                            this.isPartA = true;
                            this.isPartB = false;
                            this.orgDeptAllTreeAPIFn();
                        } else if (this.partType === 2) {
                            this.part_b_obj = res.info;
                            this.part_b_obj.systemType = parseFloat(systemType);
                            this.isPartA = false;
                            this.isPartB = true;
                        }
                    }
                }
            });
        },
        async getSelEmp(systemType, oid) {
            // 获取机构员工
            await orgAllEmpDropdownListAPI(systemType, oid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.empList = res.list;
                }
            });
        },
        // 根据机构ID查询【所有部门】
        async orgDeptAllTreeAPIFn() {
            let systemType = this.part_a_obj.systemType;
            let id = this.part_a_obj.id;
            if (id && systemType) {
                await orgDeptAllTreeAPI(systemType, id).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { list } = res;
                        let circle = (arr) => {
                            // 递归
                            arr.forEach((item) => {
                                item.key = item.department_inner_sn;
                                item.value = item.department_inner_sn;
                                item.title = item.name;
                                if (item.children && item.children.length > 0) {
                                    circle(item.children);
                                }
                            });
                        };
                        list.forEach((item) => {
                            item.key = item.department_inner_sn;
                            item.value = item.department_inner_sn;
                            item.title = item.name;
                            if (item.children && item.children.length > 0) {
                                circle(item.children);
                            }
                        });
                        this.depList = list;
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            }
        },
        /** 基本信息结束**/

        /** 相关产品**/
        changeProPagi() {},
        customRowFn(record) {
            // 设置行属性 —— 点击每一行的事件
            return {
                class: record.isActive,
                on: {
                    click: () => {
                        this.chooseTrObj = record;
                        this.chooseTrEvent(record);
                        if (record.dealer_inner_sn) {
                            this.dearUnitTreeFn(record);
                        } else if (record.vendor_inner_sn) {
                            this.venUnitTreeFn(record);
                        }
                    }
                }
            };
        },
        async dearUnitTreeFn(record) {
            // 经销商产品单位
            let oid = record.dealer_inner_sn;
            let pid = record.product_inner_sn;
            let psid = record.specification_inner_sn;
            await dealerUnitTreeAPI(oid, pid, psid).then((res) => {
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
                    let unitArr = {
                        key: record.key,
                        data: res.tree
                    };
                    let addSt = true;
                    this.unitList.forEach((item) => {
                        if (item.key === unitArr.key) {
                            addSt = false;
                        }
                    });
                    if (addSt) {
                        this.unitList.push(unitArr);
                    }
                }
            });
        },
        async venUnitTreeFn(record) {
            // 厂商商产品单位
            let oid = record.vendor_inner_sn;
            let pid = record.product_inner_sn;
            let psid = record.specification_inner_sn;
            await platformUnitTree(oid, pid, psid).then((res) => {
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
                    let unitArr = {
                        key: record.key,
                        data: res.tree
                    };
                    let addSt = true;
                    this.unitList.forEach((item) => {
                        if (item.key === unitArr.key) {
                            addSt = false;
                        }
                    });
                    if (addSt) {
                        this.unitList.push(unitArr);
                    }
                }
            });
        },
        chooseTrEvent(record) {
            // 选中事件
            this.chooseTr(record.key);
        },
        chooseTr(id) {
            // 选中行渲染
            this.isChooseTr = true;
            this.proTableData.forEach((item) => {
                if (item.key === id) {
                    item.isActive = 'active-tr';
                } else {
                    item.isActive = '';
                }
            });
        },
        openAddPro() {
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn('add_product');
            if (this.$refs.chooseProMd) {
                this.$refs.chooseProMd.pageDataInit();
            }
        },
        addProductFn() {
            // 添加商品
            this.$refs.chooseProMd.returnProList();
        },
        getProReturn(dea, ven) {
            // 获取回调商品
            let arr = ven.concat(dea);
            arr.forEach((item) => {
                if (item.product_type === 0) {
                    item.sys_type = '经销商';
                } else if (item.product_type === 1) {
                    item.sys_type = '厂商';
                }
                item.isActive = '';
                item.unit_inner_sn = '';
                item.price = '';
                this.proTableData.push(item);
            });
            this.loadDataFn();
        },
        getOtherCertImgsForPrint(imgs) {
            this.otherCertImgs = imgs;
        },
        delProList() {
            // 删除商品
            let key = this.chooseTrObj.key;
            if (!key) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择商品',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            this.proTableData.forEach((item, index) => {
                if (item.key === key) {
                    this.proTableData.splice(index, 1);
                }
            });
            this.loadDataFn();
        },
        /** 相关产品结束**/

        /** 附件上传**/
        async getUploadTokenAPIFn() {
            // 获取上传 token
            await getUploadFileTokenAPI().then((res) => {
                const { code, data } = res;
                if (parseFloat(code) === 0) {
                    this.upToken = data;
                    this.upLoadparams = {
                        token: this.upToken
                    };
                } else {
                    this.upToken = '';
                    this.$message.error(res.msg);
                }
            });
        },
        uploadChange(info) {
            let size = info.file.size;
            if (size > 41943040) {
                this.$warning({
                    title: '操作提示',
                    content: '请上传小于40M的文件',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }

            const { file } = info;
            file.name = this.$Utils.formatFileNameToLowerCase(file.name);

            if (info.file.status === 'done') {
                // 添加
                const res = info.file.response;
                let key = res.data.key;
                let item = {
                    attachment_name: file.name,
                    attachment_url: key
                };
                this.uploadList.push(item);
            } else if (info.file.status === 'removed') {
                // 删除
                const res = info.file.response;
                let key = res.data.key;
                this.uploadList.forEach((item, index) => {
                    if (item.attachment_url === key) {
                        this.uploadList.splice(index, 1);
                    }
                });
            }
            // console.log(this.uploadList)
        },
        /** 审批模块**/
        async getApproveComFn() {
            // 获取合同审批流程
            let params = {
                partA: this.editObj.partA,
                partB: this.editObj.partB,
                contract_inner_sn: this.editObj.contract_inner_sn,
                contract_type: this.editObj.contract_type
            };
            await contractApproveCommentlistAPI({ params: params }).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.commentList = res.list;
                    let nowId = this.$cookie.get('userInnerSn');
                    this.isApprove = false;
                    this.commentList.forEach((item) => {
                        if (item.status === 2) {
                            if (item.approver_inner_sn === parseInt(nowId)) {
                                this.isApprove = true;
                            } else {
                                this.isApprove = false;
                            }
                        }
                        if (item.approver_inner_sn === parseInt(nowId)) {
                            this.approverIsn = item.approve_inner_sn;
                        }
                    });
                }
            });
        },
        async contractApproveFn() {
            // 审批合同
            let params = {
                partA: this.editObj.partA,
                partB: this.editObj.partB,
                approve_inner_sn: this.approverIsn,
                contract_inner_sn: this.editObj.contract_inner_sn,
                contract_type: this.editObj.contract_type,
                status: this.approveRadio,
                description: this.approveDescription
            };
            await contractApproveAPI(params).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.$emit('done');
                }
            });
        },
        openComment() {
            this.approveComment = '';
            this.$Utils.globalDragCenterFn('approve-comment-md');
            this.addCommentMd.alert = true;
        },
        async saveComment() {
            if (!this.approveComment || this.approveComment === '') {
                this.$warning({
                    title: '操作提示',
                    content: '请输入评论内容',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            let params = {
                partA: this.editObj.partA,
                partB: this.editObj.partB,
                approve_inner_sn: this.approverIsn,
                contract_inner_sn: this.editObj.contract_inner_sn,
                contract_type: this.editObj.contract_type,
                comment: this.approveComment
            };
            await approveCommentAddAPI(params).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.addCommentMd.alert = false;
                    this.getApproveComFn();
                }
            });
        },
        cancelFn() {
            this.$emit('cancel');
        },
        async showRegistrationCertificate(row) {
            const { product_vendor_inner_sn: vid, product_inner_sn: pid } = row;

            await platformProductMdrfNewestListAPI(vid, pid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (res.info) {
                        this.gicMdrfObj = {
                            ...res.info,
                            vendor_inner_sn: row.product_vendor_inner_sn,
                            isOnlyView: true
                        };
                        this.$Utils.globalDragCenterFn('entryApproveDetailModal');
                        this.entryStVisible = true;
                        this.$nextTick(() => {
                            if (this.$refs.mdrfGicPage) {
                                this.$refs.mdrfGicPage.refreshPage();
                            }
                        });
                    } else {
                        this.$warning({
                            title: '操作提示',
                            content: '该产品无注册证',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                    }
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        cellDblclickFn(row) {
            let obj = row.row || row;
            if (obj.product_type === 1) {
                this.showRegistrationCertificate(obj);
            } else {
                this.$warning({
                    title: '操作提示',
                    content: '只有厂商产品能够查看注册证',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
            }
        },
        handlePrint() {
            const otherCertImgs = this.otherCertImgs;
            if (!this.gicMdrfObj.images.length && !otherCertImgs.length) {
                this.$warning({
                    title: '操作提示',
                    content: '暂无注册证可打印',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            const printContent = JSON.stringify({
                printData: this.gicMdrfObj.images.concat(otherCertImgs).map((i) => this.$Utils.getPictureSrc(i.url)),
                printType: 11 // printType  11、多张图片
            });
            window.localStorage.printContent = printContent;
            const routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        loadDataFn() {
            this.$nextTick(() => {
                if (this.$refs.xTableContractProduct) {
                    this.$refs.xTableContractProduct[0].loadData(this.proTableData);
                }
                // this.$refs.xTableContractProduct.loadData(this.proTableData)
            });
        },
        exportExcelFn() {
            this.$nextTick(() => {
                const xTable = this.$refs.xTableContractProduct;
                if (xTable && xTable.length) {
                    const tableData = xTable[0].getTableData().tableData.map((item, index) => {
                        return {
                            ...item,
                            sort_number: index + 1,
                            hasCertImage: item.hasCertImage ? '有' : '无'
                        };
                    });
                    if (tableData && tableData.length) {
                        const column = xTable[0].getColumns().filter((item) => item.title !== '操作');
                        this.$Utils.exportExcel(column, tableData);
                    } else {
                        this.$warning({
                            title: '操作提示',
                            content: '暂无数据可供导出！',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                    }
                }
            });
        }
    },
    created() {
        this.getUploadTokenAPIFn();
        this.getSelfOrgInfo();
        this.pageRefresh();
    }
};
</script>

<style scoped lang="less">
.zhucezheng_ico {
    padding-right: 0 !important;
    font-size: 16px !important;
}
.no_comment {
    font-size: 28px;
    writing-mode: lr-tb;
    width: 24px;
    text-align: center;
    color: #c3c3c3;
    padding-left: 120px;
    margin-top: 20px;
}
.product_box {
    height: 480px;
}
.accessory_box {
    height: 480px;
    padding: 0 16px;
    .upload_box {
        height: 155px;
    }
}
.btn_count {
    height: 40px;
    padding-left: 12px;
    .ant-btn {
        min-width: 76px;
    }
}
.tail {
    margin-left: 4px;
}
.modal-list li.item {
    margin-bottom: 0px !important;
}
.main_box {
    height: 480px;
    padding: 0px 24px;
}
.modal-form-input-scoped {
    .modal-form-input {
        padding-top: 0px !important;
        padding-right: 24px;
        padding-bottom: 24px;
        li.item {
            .item-children:first-child {
                width: 70%;
            }
            .item-children:last-child {
                .label {
                    width: 80px;
                }
            }
        }
        .label {
            width: 100px;
        }
        .ant-select {
            width: 173px;
        }
        .ant-calendar-picker-input {
            height: 24px !important;
        }
    }
}
.modal-tabs-pg {
    margin: -24px;
    .tab_box {
        display: inline-block;
        width: 70%;
        vertical-align: top;
        border-right: 1px solid #e7e7e7;
    }
    .approve_box {
        display: inline-block;
        width: 30%;
        vertical-align: top;
        padding-left: 10px;
        .top_box {
            margin-top: 16px;
            margin-bottom: 16px;
        }
        .time_line_box {
            padding: 12px 10px;
            height: 365px;
            overflow-y: auto;
            .head_icon {
                width: 32px;
                height: 32px;
                border-radius: 15px;
                line-height: 32px;
                font-size: 12px;
                color: #fff;
            }
            .info_box {
                font-size: 12px;
                min-height: 50px;
                .tit_box {
                    .name_box {
                        &.focus {
                            color: #2090e6;
                        }
                        font-size: 14px;
                        color: #888;
                    }
                }
                .date_box {
                    color: #888;
                    float: right;
                }
                .operate_box {
                    color: #888;
                    padding-top: 6px;
                    .des_box {
                        .key {
                            display: inline-block;
                            vertical-align: top;
                            width: 23%;
                        }
                        .txt {
                            display: inline-block;
                            vertical-align: top;
                            width: 77%;
                            color: #333;
                        }
                    }
                    .comments_box {
                        margin-top: 6px;
                        .time_box {
                            float: right;
                        }
                        .text_box {
                            text-indent: 12px;
                            color: #333;
                        }
                    }
                }
            }
        }
        .approve_count {
            padding-bottom: 20px;
            padding-right: 24px;
            .choose_box {
                margin-bottom: 6px;
            }
        }
    }
    .info-count {
        padding: 0px 24px 14px 24px;
    }
}
</style>
