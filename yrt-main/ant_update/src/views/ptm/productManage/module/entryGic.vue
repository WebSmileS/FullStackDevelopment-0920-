<template>
    <div class="modal-list">
        <div class="img-count">
            <a-tabs :activeKey="activeTab" @change="toggleImgTab" type="card">
                <a-tab-pane tab="注册证" key="cert">
                    <div class="img-box">
                        <div v-if="currentCertImg.url !== ''" class="float_num"
                            >{{ certPicIndex }} / {{ certPicTotal }}</div
                        >
                        <div v-if="currentCertImg.url !== ''" class="left_arrow" @click="flipPic('left')"
                            ><a-icon type="double-left"
                        /></div>
                        <div v-if="currentCertImg.url !== ''" class="right_arrow" @click="flipPic('right')"
                            ><a-icon type="double-right"
                        /></div>
                        <a v-if="currentCertImg.url !== ''" :href="currentCertImg.url" target="_blank">
                            <img v-if="currentCertImg.url !== '' && !currentCertImg.isPdf" :src="currentCertImg.url" />
                            <pdfPreview
                                v-if="currentCertImg.url !== '' && currentCertImg.isPdf"
                                :type="'big'"
                                :url="currentCertImg.url"
                            />
                        </a>
                        <div v-else class="no-data-box">
                            <div><a-icon type="file-excel" /></div>
                            暂无图片
                        </div>
                    </div>
                    <div class="upload_img_box">
                        <div class="up_cp" v-if="!gicMdrfObj.isOnlyView">
                            <a-upload
                                :action="QiniuUrl"
                                :show-upload-list="false"
                                :multiple="true"
                                :beforeUpload="checkFile"
                                :supportServerRender="true"
                                :data="uploadParams"
                                @change="upMdrfImg"
                            >
                                <div class="upload-box">
                                    <a-icon type="plus" />
                                </div>
                            </a-upload>
                        </div>
                        <div class="img_list">
                            <div
                                v-for="(item, index) of mdrfImglist"
                                class="img_box"
                                :class="{ active: item.active }"
                                :key="index"
                            >
                                <picture-box
                                    imageWidth="52px"
                                    imageHeight="68px"
                                    :resourceKey="item.url"
                                    :previewAble="false"
                                    :isThumbnail="false"
                                    :fileType="$Utils.checkPdf(item.file_name) ? 'pdf' : 'image'"
                                    @delete="delImg(index)"
                                    @maskclick="chooseImg(index)"
                                />
                            </div>
                        </div>
                    </div>
                </a-tab-pane>
                <a-tab-pane tab="营业执照" key="bizLicense">
                    <div class="img-box">
                        <div v-if="currentBizImg.url !== ''" class="float_num"
                            >{{ bizPicIndex }} / {{ bizPicTotal }}</div
                        >
                        <div v-if="currentBizImg.url !== ''" class="left_arrow" @click="flipPic('left')"
                            ><a-icon type="double-left"
                        /></div>
                        <div v-if="currentBizImg.url !== ''" class="right_arrow" @click="flipPic('right')"
                            ><a-icon type="double-right"
                        /></div>
                        <a v-if="currentBizImg.url !== ''" :href="currentBizImg.url" target="_blank">
                            <img v-if="currentBizImg.url !== '' && !currentBizImg.isPdf" :src="currentBizImg.url" />
                            <pdfPreview
                                v-if="currentBizImg.url !== '' && currentBizImg.isPdf"
                                :type="'big'"
                                :url="currentBizImg.url"
                            />
                        </a>
                        <div v-else class="no-data-box">
                            <div><a-icon type="file-excel" /></div>
                            暂无图片
                        </div>
                    </div>
                    <div class="upload_img_box">
                        <div class="img_list">
                            <div
                                v-for="(item, index) of bizLicenseImgs"
                                class="img_box"
                                :class="{ active: item.active }"
                                :key="index"
                            >
                                <picture-box
                                    imageWidth="52px"
                                    imageHeight="68px"
                                    :resourceKey="item.url"
                                    :previewAble="false"
                                    :deleteAble="false"
                                    :isThumbnail="false"
                                    :fileType="$Utils.checkPdf(item.file_name) ? 'pdf' : 'image'"
                                    @maskclick="chooseImg(index, 'bizLicense')"
                                />
                            </div>
                        </div>
                    </div>
                </a-tab-pane>
                <a-tab-pane tab="医疗器械生产许可证" key="prodLicense">
                    <div class="img-box">
                        <div v-if="currentProdImg.url !== ''" class="float_num"
                            >{{ prodPicIndex }} / {{ prodPicTotal }}</div
                        >
                        <div v-if="currentProdImg.url !== ''" class="left_arrow" @click="flipPic('left')"
                            ><a-icon type="double-left"
                        /></div>
                        <div v-if="currentProdImg.url !== ''" class="right_arrow" @click="flipPic('right')"
                            ><a-icon type="double-right"
                        /></div>
                        <a v-if="currentProdImg.url !== ''" :href="currentProdImg.url" target="_blank">
                            <img v-if="currentProdImg.url !== '' && !currentProdImg.isPdf" :src="currentProdImg.url" />
                            <pdfPreview
                                v-if="currentProdImg.url !== '' && currentProdImg.isPdf"
                                :url="currentProdImg.url"
                                :type="'big'"
                            />
                        </a>
                        <div v-else class="no-data-box">
                            <div><a-icon type="file-excel" /></div>
                            暂无图片
                        </div>
                    </div>
                    <div class="upload_img_box">
                        <div class="img_list">
                            <div
                                v-for="(item, index) of prodLicenseImgs"
                                class="img_box"
                                :class="{ active: item.active }"
                                :key="index"
                            >
                                <picture-box
                                    imageWidth="52px"
                                    imageHeight="68px"
                                    :resourceKey="item.url"
                                    :previewAble="false"
                                    :deleteAble="false"
                                    :isThumbnail="false"
                                    :fileType="$Utils.checkPdf(item.file_name) ? 'pdf' : 'image'"
                                    @maskclick="chooseImg(index, 'prodLicense')"
                                />
                            </div>
                        </div>
                    </div>
                </a-tab-pane>
            </a-tabs>
        </div>

        <a-form :key="formKey" class="info-count">
            <a-form-item label="生产企业名称" v-bind="formItemLayout">
                <a-input :disabled="true" v-model="formData.vendor_name" />
            </a-form-item>
            <a-form-item class="must-issue" v-bind="formItemLayout" label="注册号" has-feedback>
                <a-input v-model="formData.register_sn" :disabled="registerSnDisabled" />
            </a-form-item>
            <a-form-item v-bind="formItemLayout" label="企业注册地址" has-feedback>
                <a-input :disabled="true" v-model="formData.address" />
            </a-form-item>
            <a-form-item v-bind="formItemLayout" label="生产地址" has-feedback>
                <a-input v-model="formData.production_address" :disabled="productionAddressDisabled" />
            </a-form-item>
            <a-form-item class="must-issue" v-bind="formItemLayout" label="产品名称" has-feedback>
                <a-input :disabled="true" v-model="formData.product_name" />
            </a-form-item>
            <a-form-item label="型号、规格" v-bind="formItemLayout">
                <a-textarea
                    v-model="formData.specifications"
                    placeholder="请输入规格型号，多型号请用 , 分隔"
                    :autosize="textAreaSet"
                    :disabled="specificationsDisabled"
                />
            </a-form-item>
            <a-form-item v-bind="formItemLayout" label="产品标准" has-feedback>
                <a-input v-model="formData.standard" :disabled="standardDisabled" />
            </a-form-item>
            <a-form-item label="性能结构及组成" v-bind="formItemLayout">
                <a-textarea
                    v-model="formData.performance_structure"
                    :autosize="textAreaSet"
                    :disabled="performanceStructureDisabled"
                />
            </a-form-item>
            <a-form-item label="适用范围" v-bind="formItemLayout">
                <a-textarea
                    v-model="formData.application_range"
                    :autosize="textAreaSet"
                    :disabled="applicationRangeDisabled"
                />
            </a-form-item>
            <a-form-item label="禁忌症" v-bind="formItemLayout">
                <a-textarea
                    v-model="formData.contraindication"
                    :autosize="textAreaSet"
                    :disabled="contraindicationDisabled"
                />
            </a-form-item>
            <a-form-item label="备注" v-bind="formItemLayout">
                <a-textarea v-model="formData.description" :autosize="textAreaSet" :disabled="descriptionDisabled" />
            </a-form-item>
            <a-form-item label="有效期" class="must-issue" v-bind="formItemLayout">
                <a-range-picker
                    :value="validDate"
                    :ranges="defaultValue"
                    class="input"
                    :placeholder="['开始日期', '结束日期']"
                    format="YYYY-MM-DD"
                    @change="changeStDate"
                    :disabled="validDateDisabled"
                />
            </a-form-item>
            <a-row>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half" label="发证部门" has-feedback>
                        <a-input v-model="formData.issue_organization" :disabled="issueOrganizationDisabled" />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item v-bind="formItemLayout_half" class="must-issue" label="发证日期" has-feedback>
                        <a-date-picker
                            v-model="issueDate"
                            format="YYYY-MM-DD"
                            @change="changeIsDate"
                            :disabled="issueDateDisabled"
                        />
                    </a-form-item>
                </a-col>
            </a-row>
        </a-form>
    </div>
</template>

<script>
import moment from 'moment';
import pdfPreview from '@/components/pdfPreview';
import PictureBox from '@/components/Picture/PictureBox';
import { getUploadTokenAPI, platformOrgImgListAPI } from '@/service/pageAjax';
export default {
    name: 'entryGic',
    props: ['gicMdrfObj', 'chooseTrObj'],
    components: {
        PictureBox,
        pdfPreview
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            activeTab: 'cert',
            currentCertImg: {
                // 当前显示的注册证图片
                url: '',
                isPdf: false
            },
            currentBizImg: {
                // 当前显示的营业执照图片
                url: '',
                isPdf: false
            },
            currentProdImg: {
                // 当前显示的医疗器械生产许可证图片
                url: '',
                isPdf: false
            },
            validDate: '',
            issueDate: '',
            uploadParams: {},
            mdrfImglist: [], // 注册证图片数组
            bizLicenseImgs: [], // 营业执照图片数组
            prodLicenseImgs: [], // 医疗器械生产许可证图片数组
            defaultValue: '',
            formKey: 0,
            formItemLayout: {
                labelCol: { span: 5 },
                wrapperCol: { span: 19 }
            },
            formItemLayout_half: {
                labelCol: { span: 10 },
                wrapperCol: { span: 14 }
            },
            formItemLayout_long: {
                labelCol: { span: 10 },
                wrapperCol: { span: 14 }
            },
            formItemLayout_short: {
                labelCol: { span: 3 },
                wrapperCol: { span: 9 }
            },
            textAreaSet: {
                minRows: 2,
                maxRows: 2
            },
            formData: {
                application_range: '',
                contraindication: '',
                description: '',
                images: [],
                issue_date: '',
                issue_organization: '',
                new: true,
                performance_structure: '',
                product_name: '',
                register_sn: '',
                specifications: '',
                production_address: '',
                standard: '',
                vendor_name: '',
                address: '',
                begin_date: '',
                end_date: ''
            },
            certPicIndex: 0, // 注册证当前显示图片索引
            bizPicIndex: 0, // 营业执照当前显示图片索引
            prodPicIndex: 0, // 医疗器械生产许可证当前显示图片索引
            newDate: null,
            defaultPickerValue: null,
            registerSnDisabled: false,
            productionAddressDisabled: false,
            specificationsDisabled: false,
            standardDisabled: false,
            performanceStructureDisabled: false,
            applicationRangeDisabled: false,
            contraindicationDisabled: false,
            descriptionDisabled: false,
            validDateDisabled: false,
            issueOrganizationDisabled: false,
            issueDateDisabled: false
        };
    },
    computed: {
        // 注册证图片总数
        certPicTotal() {
            return this.mdrfImglist.length;
        },
        // 营业执照图片总数
        bizPicTotal() {
            return this.bizLicenseImgs.length;
        },
        // 医疗器械生产许可证图片总数
        prodPicTotal() {
            return this.prodLicenseImgs.length;
        }
    },
    methods: {
        async refreshPage() {
            this.formKey = new Date().getTime(); // 重新渲染form表单dom 防止时间选择等控件会缓存
            const gicMdrfData = JSON.parse(JSON.stringify(this.gicMdrfObj));

            await this.getOtherCertImgs();

            if (gicMdrfData.new) {
                this.formData = {
                    application_range: '',
                    contraindication: '',
                    description: '',
                    images: [],
                    issue_date: '',
                    issue_organization: '',
                    new: true,
                    performance_structure: '',
                    product_name: gicMdrfData.name || gicMdrfData.product_name,
                    register_sn: '',
                    specifications: '',
                    standard: '',
                    vendor_name: gicMdrfData.vendor_name,
                    address: gicMdrfData.address,
                    production_address: '',
                    begin_date: '',
                    end_date: ''
                };
                this.currentCertImg.url = '';
                this.currentCertImg.isPdf = false;
                // this.defaultValue = [moment('2019-01-01'), moment('2019-01-01')]
                this.issueDate = '';
                this.mdrfImglist = [];
                this.validDate = '';
                this.certPicIndex = 0;
            } else {
                this.currentCertImg.url = '';
                this.currentCertImg.isPdf = false;
                this.formData = this.gicMdrfObj;
                if (!this.gicMdrfObj.begin_date || !this.gicMdrfObj.end_date) {
                    this.validDate = '';
                } else {
                    this.validDate = [moment(this.gicMdrfObj.begin_date), moment(this.gicMdrfObj.end_date)];
                }
                this.issueDate = (this.gicMdrfObj.issue_date && moment(this.gicMdrfObj.issue_date)) || '';
                this.mdrfImglist = this.gicMdrfObj.images || [];
                if (this.mdrfImglist.length > 0) {
                    this.mdrfImglist[0].active = true;
                    this.currentCertImg.url = this.$Utils.getPictureSrc(this.mdrfImglist[0].url);
                    this.currentCertImg.isPdf = this.$Utils.checkPdf(this.mdrfImglist[0].file_name);
                }

                if (this.certPicTotal) {
                    this.certPicIndex = 1;
                }
            }
            this.activeTab = 'cert';
            // 仅查看
            if (gicMdrfData.isOnlyView) {
                this.setFormDisableStatus();
            }
        },
        async getUploadTokenAPIFn() {
            // 获取上传 token
            await getUploadTokenAPI().then((res) => {
                const { code, data } = res;
                if (parseFloat(code) === 0) {
                    this.upToken = data;
                    this.uploadParams = {
                        token: this.upToken
                    };
                } else {
                    this.upToken = '';
                    this.$message.error(res.msg);
                }
            });
        },
        checkFile(file) {
            let fileName = this.$Utils.formatFileNameToLowerCase(file.name);
            let index1 = fileName.lastIndexOf('.');
            let index2 = fileName.length;
            let postName = fileName.substring(index1 + 1, index2); // 后缀名

            if (
                postName !== 'jpeg' &&
                postName !== 'JPEG' &&
                postName !== 'jpg' &&
                postName !== 'JPG' &&
                postName !== 'png' &&
                postName !== 'PNG' &&
                postName !== 'pdf' &&
                postName !== 'PDF'
            ) {
                alert('仅支持png / jpeg / pdf');
                return false;
            }
        },
        /**
         * 切换图片Tab
         */
        toggleImgTab(tabKey) {
            this.activeTab = tabKey;
        },
        async upMdrfImg(info) {
            // 上传注册证登记表图片
            const { file } = info;
            file.name = this.$Utils.formatFileNameToLowerCase(file.name);

            if (info.file.status === 'done') {
                const res = info.file.response;
                if (res.Code || res.code === 0) {
                    this.mdrfImglist.forEach((item) => {
                        item.active = false;
                    });
                    let url = res.data.key;
                    let item = {
                        url: url,
                        active: true,
                        file_name: file.name
                    };
                    this.currentCertImg.url = this.$Utils.getPictureSrc(url);
                    this.currentCertImg.isPdf = this.$Utils.checkPdf(file.name);
                    this.mdrfImglist.push(item);
                    this.certPicIndex = this.mdrfImglist.length;
                } else {
                    this.$message.error(res.msg);
                }
            }
        },
        flipPic(type) {
            /**
             * cert
             * bizLicense
             * prodLicense
             */
            switch (this.activeTab) {
                case 'cert':
                    if (type === 'left') {
                        if (this.certPicIndex !== 1) {
                            this.certPicIndex = this.certPicIndex - 1;
                            this.mdrfImglist.forEach((item) => {
                                item.active = false;
                            });
                        }
                    }
                    if (type === 'right') {
                        if (this.certPicIndex < this.certPicTotal) {
                            this.certPicIndex = this.certPicIndex + 1;
                            this.mdrfImglist.forEach((item) => {
                                item.active = false;
                            });
                        }
                    }
                    this.mdrfImglist[this.certPicIndex - 1].active = true;
                    this.currentCertImg.url = this.$Utils.getPictureSrc(this.mdrfImglist[this.certPicIndex - 1].url);
                    this.currentCertImg.isPdf = this.$Utils.checkPdf(this.mdrfImglist[this.certPicIndex - 1].file_name);
                    break;
                case 'bizLicense':
                    if (type === 'left') {
                        if (this.bizPicIndex !== 1) {
                            this.bizPicIndex = this.bizPicIndex - 1;
                            this.bizLicenseImgs.forEach((item) => {
                                item.active = false;
                            });
                        }
                    }
                    if (type === 'right') {
                        if (this.bizPicIndex < this.bizPicTotal) {
                            this.bizPicIndex = this.bizPicIndex + 1;
                            this.bizLicenseImgs.forEach((item) => {
                                item.active = false;
                            });
                        }
                    }
                    this.bizLicenseImgs[this.bizPicIndex - 1].active = true;
                    this.currentBizImg.url = this.$Utils.getPictureSrc(this.bizLicenseImgs[this.bizPicIndex - 1].url);
                    this.currentBizImg.isPdf = this.$Utils.checkPdf(
                        this.bizLicenseImgs[this.bizPicIndex - 1].file_name
                    );
                    break;
                case 'prodLicense':
                    if (type === 'left') {
                        if (this.prodPicIndex !== 1) {
                            this.prodPicIndex = this.prodPicIndex - 1;
                            this.prodLicenseImgs.forEach((item) => {
                                item.active = false;
                            });
                        }
                    }
                    if (type === 'right') {
                        if (this.prodPicIndex < this.prodPicTotal) {
                            this.prodPicIndex = this.prodPicIndex + 1;
                            this.prodLicenseImgs.forEach((item) => {
                                item.active = false;
                            });
                        }
                    }
                    this.prodLicenseImgs[this.prodPicIndex - 1].active = true;
                    this.currentProdImg.url = this.$Utils.getPictureSrc(
                        this.prodLicenseImgs[this.prodPicIndex - 1].url
                    );
                    this.currentProdImg.isPdf = this.$Utils.checkPdf(
                        this.prodLicenseImgs[this.prodPicIndex - 1].file_name
                    );
                    break;
                default:
                    break;
            }
        },
        delImg(idx) {
            this.mdrfImglist.forEach((item, index) => {
                if (index === idx) {
                    this.mdrfImglist.splice(index, 1);
                }
                if (this.mdrfImglist.length > 0) {
                    this.mdrfImglist[0].active = true;
                    this.currentCertImg.url = this.$Utils.getPictureSrc(this.mdrfImglist[0].url);
                    this.currentCertImg.isPdf = this.$Utils.checkPdf(this.mdrfImglist[0].file_name);
                } else {
                    this.currentCertImg.url = '';
                    this.currentCertImg.isPdf = false;
                }
                this.certPicIndex = 1;
            });
        },
        chooseImg(idx, type) {
            switch (type) {
                case 'bizLicense':
                    this.bizLicenseImgs.forEach((item, index) => {
                        if (index === idx) {
                            this.currentBizImg.url = this.$Utils.getPictureSrc(item.url);
                            this.currentBizImg.isPdf = this.$Utils.checkPdf(item.file_name);
                            item.active = true;
                        } else {
                            item.active = false;
                        }
                    });
                    this.bizPicIndex = idx + 1;
                    break;
                case 'prodLicense':
                    this.prodLicenseImgs.forEach((item, index) => {
                        if (index === idx) {
                            this.currentProdImg.url = this.$Utils.getPictureSrc(item.url);
                            this.currentProdImg.isPdf = this.$Utils.checkPdf(item.file_name);
                            item.active = true;
                        } else {
                            item.active = false;
                        }
                    });
                    this.prodPicIndex = idx + 1;
                    break;
                default:
                    this.mdrfImglist.forEach((item, index) => {
                        if (index === idx) {
                            this.currentCertImg.url = this.$Utils.getPictureSrc(item.url);
                            this.currentCertImg.isPdf = this.$Utils.checkPdf(item.file_name);
                            item.active = true;
                        } else {
                            item.active = false;
                        }
                    });
                    this.certPicIndex = idx + 1;
            }
        },
        changeStDate(date, dateString) {
            // 有效期
            this.validDate = date;
            this.formData.begin_date = dateString[0];
            this.formData.end_date = dateString[1];
        },
        changeIsDate(date, dateString) {
            this.formData.issue_date = dateString;
        },
        returnPageData() {
            try {
                const data = JSON.parse(JSON.stringify(this.formData));
                data.images = this.mdrfImglist
                    .filter((img) => !img.nonRegistrationCert)
                    .map((img) => {
                        return this.$XEUtils.omit(img, ['active']); // 排除不需要传递的属性值
                    });
                this.$emit('getData', data);
            } catch (ex) {
                console.error(ex);
            }
        },
        /**
         * 回传父级组件“营业执照”和“医疗器械生产许可证”的图片数组，用于打印
         */
        returnOtherCertImgs(imgs) {
            this.$emit('returnOtherCertImgs', imgs);
        },
        /**
         * 设置表单的disable状态，在只查看注册证时，需将表单项全部disabled
         */
        setFormDisableStatus() {
            this.registerSnDisabled = true;
            this.productionAddressDisabled = true;
            this.specificationsDisabled = true;
            this.standardDisabled = true;
            this.performanceStructureDisabled = true;
            this.applicationRangeDisabled = true;
            this.contraindicationDisabled = true;
            this.descriptionDisabled = true;
            this.validDateDisabled = true;
            this.issueOrganizationDisabled = true;
            this.issueDateDisabled = true;
        },
        async getOtherCertImgs() {
            let bizLicenseImgs = [];
            let prodLicenseImgs = [];
            try {
                if (this.gicMdrfObj.vendor_inner_sn) {
                    const bizLicenseRes = await platformOrgImgListAPI(3, this.gicMdrfObj.vendor_inner_sn, 1);
                    const prodLicenseRes = await platformOrgImgListAPI(3, this.gicMdrfObj.vendor_inner_sn, 2);
                    bizLicenseImgs = bizLicenseRes.list.map((i) => ({
                        ...i,
                        nonRegistrationCert: true,
                        isBizLicense: true
                    }));
                    prodLicenseImgs = prodLicenseRes.list.map((i) => ({
                        ...i,
                        nonRegistrationCert: true,
                        isProdLicense: true
                    }));
                    this.bizLicenseImgs = bizLicenseImgs;
                    this.prodLicenseImgs = prodLicenseImgs;
                    if (this.bizLicenseImgs.length) {
                        this.bizPicIndex = 1;
                        this.bizLicenseImgs[0].active = true;
                        this.currentBizImg.url = this.$Utils.getPictureSrc(this.bizLicenseImgs[0].url);
                        this.currentBizImg.isPdf = this.$Utils.checkPdf(this.bizLicenseImgs[0].file_name);
                    } else {
                        this.bizPicIndex = 0;
                        this.currentBizImg.url = '';
                        this.currentBizImg.isPdf = false;
                    }
                    if (this.prodLicenseImgs.length) {
                        this.prodPicIndex = 1;
                        this.prodLicenseImgs[0].active = true;
                        this.currentProdImg.url = this.$Utils.getPictureSrc(this.prodLicenseImgs[0].url);
                        this.currentProdImg.isPdf = this.$Utils.checkPdf(this.prodLicenseImgs[0].file_name);
                    } else {
                        this.prodPicIndex = 0;
                        this.currentProdImg.url = '';
                        this.currentProdImg.isPdf = false;
                    }
                    this.returnOtherCertImgs(bizLicenseImgs.concat(prodLicenseImgs));
                }
            } catch (ex) {
                console.error(ex);
            }
        }
    },
    mounted() {
        this.getUploadTokenAPIFn();
        this.refreshPage();
    }
};
</script>
<style scoped lang="less">
/* 暂无数据 */
.no-data-box {
    margin-top: 50%;
    color: #b2b2b2;
    font-size: 26px;
    text-align: center;
    i {
        font-size: 48px;
    }
}
.pic-type-name {
    text-align: center;
    color: #ff4d4f;
}
.upload_img_box {
    border: 1px dashed #b8b8b8;
    height: 90px;
    margin-top: 14px;
    padding-left: 10px;
    /deep/ .picture-box-mask {
        i {
            font-size: 18px;
        }
    }
    .up_cp {
        width: 13%;
        display: inline-block;
        padding-top: 19px;
    }
    .img_list {
        vertical-align: top;
        width: 87%;
        overflow-x: auto;
        height: 100%;
        display: inline-block;
        padding-top: 10px;
        .name {
            position: absolute;
        }
        .img_box {
            &.active {
                border: 2px solid #45a9ff;
            }
            margin-bottom: 10px;
            position: relative;
            width: 52px;
            height: 68px;
            margin-right: 10px;
            vertical-align: top;
            overflow: hidden;
            border: 1px solid #b8b8b8;
            display: inline-block;
            img {
                width: 100%;
                height: 100%;
            }
        }
    }
}
.upload-box {
    border: 1px dashed #b8b8b8;
    padding: 14px 18px;
    border-radius: 5px;
    cursor: pointer;
}
.modal-list {
    .ant-form-item {
        margin-bottom: 8px;
    }
    .img-count {
        position: relative;
        display: inline-block;
        vertical-align: top;
        width: 46%;
        margin-right: 1%;
        .img-box {
            border: 1px solid #d0d0d0;
            border-right-width: 4px;
            width: 510px;
            // height: 680px;
            height: 624px;
            img {
                width: 100%;
                height: 100%;
            }
        }
        .img-item {
            background: darkcyan;
            height: 480px;
            img {
                width: 100%;
                height: 100%;
            }
        }
    }
    .info-count {
        display: inline-block;
        vertical-align: top;
        width: 53%;
    }
}
.modal-form-input {
    ul {
        padding-left: 0;
        margin-bottom: 0;
    }

    .item {
        display: flex;
        flex-direction: row;
        align-items: center;
        padding-bottom: 24px;
    }

    .label {
        width: 16%;
        text-align: left;

        &.letter-space {
            letter-spacing: 14px;
        }
    }

    .input {
        width: 84%;
        height: 24px;
        line-height: 24px;
        resize: none;

        .ant-cascader-input {
            height: 24px;
            line-height: 24px;
        }
    }

    .item-children {
        display: flex;
        flex-direction: row;
        width: 50%;

        &:nth-of-type(1) {
            padding-right: 5px;
        }
        &:nth-of-type(2) {
            padding-left: 5px;
        }

        .label {
            width: 45%;
        }

        .input {
            width: 90%;
        }
    }
}
.img-box:hover {
    .left_arrow {
        display: block;
    }
    .right_arrow {
        display: block;
    }
}
.float_num {
    height: 30px;
    min-width: 40px;
    padding: 0 12px;
    background: #108ee9;
    color: #fff;
    position: absolute;
    left: 12px;
    top: 56px;
    text-align: center;
    line-height: 30px;
    border-radius: 6px;
    opacity: 0.8;
    z-index: 10;
}
.left_arrow {
    display: none;
    cursor: pointer;
    height: 50px;
    min-width: 40px;
    padding: 0 12px;
    color: #fff;
    position: absolute;
    left: 12px;
    top: 300px;
    font-size: 28px;
    text-align: center;
    line-height: 50px;
    opacity: 0.6;
    background: #999999;
    border-radius: 25px;
    z-index: 10;
}
.right_arrow {
    display: none;
    cursor: pointer;
    height: 50px;
    min-width: 40px;
    padding: 0 12px;
    color: #fff;
    position: absolute;
    right: 12px;
    top: 300px;
    font-size: 28px;
    text-align: center;
    line-height: 50px;
    opacity: 0.6;
    background: #999999;
    border-radius: 25px;
    z-index: 10;
}
</style>
