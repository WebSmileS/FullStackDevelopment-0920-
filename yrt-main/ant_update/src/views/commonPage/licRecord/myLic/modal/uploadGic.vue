<template>
    <div class="upload-count">
        <div class="">
            <ul>
                <li v-if="!hideBeginDate" class="item">
                    <span class="label">开始日期</span>
                    <a-date-picker
                        v-model="begin_date"
                        @change="changeBegin"
                        placeholder=""
                        format="YYYY-MM-DD"
                        style="width: 80%"
                    />
                </li>
                <li class="item">
                    <span class="label">截止日期</span>
                    <a-date-picker
                        v-model="end_date"
                        @change="changeEnd"
                        placeholder=""
                        format="YYYY-MM-DD"
                        style="width: 80%"
                    />
                </li>
                <li class="item">
                    <span class="tips">证照上传，仅支持jpg/png图片格式或PDF文件，单个文件大小不超过5M</span>
                </li>
                <li class="item">
                    <div class="add-box">
                        <div class="up-box">
                            <a-upload
                                :action="QiniuUrl"
                                :show-upload-list="false"
                                :multiple="true"
                                :beforeUpload="checkFile"
                                :supportServerRender="true"
                                :data="upLoadparams"
                                @change="upCertImg"
                            >
                                <div class="upload-box">
                                    <a-icon type="plus" />
                                    <div class="ant-upload-text">上传</div>
                                </div>
                            </a-upload>
                        </div>
                        <template v-if="uploadData.url && uploadData.url !== ''">
                            <picture-box
                                v-if="isPdf"
                                fileType="pdf"
                                :resourceKey="uploadData.url"
                                @preview="previewPdf($Utils.getPictureSrc(uploadData.url))"
                                @delete="deleteImg"
                            />
                            <picture-box
                                v-else
                                :resourceKey="uploadData.url"
                                :previewAble="false"
                                @delete="deleteImg"
                            />
                        </template>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import moment from 'moment';
import { getUploadTokenAPI } from '@/service/pageAjax';
import PictureBox from '@/components/Picture/PictureBox';

export default {
    name: 'uploadGic',
    props: ['chooseTrObj', 'upLoadStatus', 'downTabsKey', 'uploadDateInfo', 'hideBeginDate'],
    components: {
        PictureBox
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            preImgSt: false,
            upToken: '',
            imgSrc: '',
            upLoadparams: {},
            uploadData: {
                begin_date: moment[''],
                end_date: moment[''],
                url: '',
                fileName: ''
            },
            begin_date: moment[''],
            end_date: moment[''],
            isPdf: false
        };
    },
    methods: {
        initUploadGic() {
            this.uploadData = {
                begin_date: moment[''],
                end_date: moment[''],
                url: '',
                fileName: ''
            };
            this.begin_date = moment[''];
            this.end_date = moment[''];
        },
        changeBegin(value, dateString) {
            this.uploadData.begin_date = dateString;
        },
        changeEnd(value, dateString) {
            this.uploadData.end_date = dateString;
        },
        async getUploadTokenAPIFn() {
            // 获取上传 token
            await getUploadTokenAPI().then((res) => {
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
        async upCertImg(info) {
            // 上传注册证图片
            info.file.name = this.$Utils.formatFileNameToLowerCase(info.file.name);

            if (info.file.status === 'done') {
                const res = info.file.response;
                if (res.code === 0) {
                    let url = res.data.key;
                    this.uploadData.url = url;
                } else {
                    this.$message.error(res.msg);
                }
            }
        },
        checkFile(file) {
            let fileName = this.$Utils.formatFileNameToLowerCase(file.name);
            let index1 = fileName.lastIndexOf('.');
            let index2 = fileName.length;
            let postName = fileName.substring(index1 + 1, index2); // 后缀名

            this.uploadData.filename = fileName;
            this.isPdf = this.$Utils.checkPdf(fileName);
            this.uploadData.fileName = fileName;

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
                this.$message.error('仅支持png / jpeg 格式图片或PDF文件');
                return false;
            }
        },
        returnPageData() {
            this.$emit('getData', this.uploadData);
        },
        // 删除图片
        deleteImg() {
            this.uploadData.url = '';
        },
        disabledStartDate(startValue) {
            const endValue = this.uploadData.end_date;
            if (!startValue || !endValue) {
                return false;
            }
            return startValue.valueOf() > endValue.valueOf();
        },
        disabledEndDate(endValue) {
            const startValue = this.uploadData.begin_date;
            if (!endValue || !startValue) {
                return false;
            }
            return startValue.valueOf() >= endValue.valueOf();
        },
        previewPdf(src) {
            window.open(src, '_blank');
        }
    },
    watch: {
        upLoadStatus: function () {
            // console.log('old:' + oldVal)
            // console.log('new:' + newVal)
            this.initUploadGic();
        },
        downTabsKey: function () {
            // console.log('old:' + oldVal)
            // console.log('new:' + newVal)
            if (this.tabsKey === 'dealer' && this.downTabsKey === '3') {
                this.upTimeStatus = false;
            } else {
                this.upTimeStatus = true;
                this.uploadData = {
                    begin_date: moment[''],
                    end_date: moment[''],
                    url: ''
                };
            }
        },
        uploadDateInfo: {
            deep: true,
            handler(val) {
                val.beginDate && (this.begin_date = moment(val.beginDate));
                val.endDate && (this.end_date = moment(val.endDate));
                this.uploadData = Object.assign(this.uploadData, {
                    begin_date: val.beginDate || '',
                    end_date: val.endDate || ''
                });
            }
        }
    },
    mounted() {
        this.uploadData.begin_date = moment[''];
        this.uploadData.end_date = moment[''];
        this.getUploadTokenAPIFn();
    }
};
</script>

<style scoped lang="less">
.tips {
    font-size: 14px;
    display: inline-block;
    width: 100%;
    text-align: center;
    color: #afafaf;
}
.add-box {
    display: inline-block;
    text-align: center;
    width: 100%;
}
.up-box {
    display: inline-block;
    padding-top: 25px;
    margin-right: 10px;
}
.upload-box {
    border: 1px dashed #b8b8b8;
    padding: 10px 18px;
    border-radius: 5px;
    cursor: pointer;
    margin-top: 12px;
}
</style>
