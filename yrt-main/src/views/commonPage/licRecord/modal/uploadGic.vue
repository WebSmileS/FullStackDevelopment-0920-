<template>
    <div class="upload-count">
        <div class="">
            <ul>
                <li class="item">
                    <span class="label">开始日期</span>
                    <a-date-picker v-model="begin_date" @change="changeBegin" format="YYYY-MM-DD" style="width: 80%" />
                </li>
                <li class="item">
                    <span class="label">截止日期</span>
                    <a-date-picker v-model="end_date" @change="changeEnd" format="YYYY-MM-DD" style="width: 80%" />
                </li>
                <li class="item">
                    <span class="tips">证照上传，仅支持jpg/png图片格式文件，单个文件大小不超过5M</span>
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
                        <picture-box
                            v-if="uploadData.url != '' && uploadData.url"
                            :src="$Utils.getPictureSrc(uploadData.url)"
                            previewAble="false"
                            deleteAble="false"
                        />
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
    components: {
        PictureBox
    },
    props: ['chooseTrObj', 'upLoadStatus'],
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
                url: ''
            },
            begin_date: moment[''],
            end_date: moment['']
        };
    },
    methods: {
        initUploadGic() {
            this.uploadData = {
                begin_date: moment[''],
                end_date: moment[''],
                url: ''
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
        checkFile(file, fileList) {
            let fileName = file.name;
            let index1 = fileName.lastIndexOf('.');
            let index2 = fileName.length;
            let postName = fileName.substring(index1 + 1, index2); // 后缀名
            if (
                postName !== 'png' &&
                postName !== 'jpeg' &&
                postName !== 'jpg' &&
                postName !== 'PNG' &&
                postName !== 'JPEG' &&
                postName !== 'JPG'
            ) {
                this.$message.error('仅支持png / jpeg 格式图片文件');
                return false;
            }
        },
        returnPageData() {
            this.$emit('getData', this.uploadData);
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
        }
    },
    watch: {
        upLoadStatus: function (newVal, oldVal) {
            console.log(newVal);
            console.log(oldVal);
            this.initUploadGic();
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
