<template>
    <div class="form-table">
        <h6 class="caption">付款凭证图片</h6>
        <div class="form-photo">
            <a-upload
                v-if="isUpload"
                :action="QiniuUrl"
                :show-upload-list="false"
                :multiple="true"
                :beforeUpload="checkFile"
                :supportServerRender="true"
                :data="upLoadparams"
                @change="upGoodsImg"
            >
                <div>
                    <a-button :disabled="isSend" class="btn-nom mgr-10" type="primary">上传付款凭证</a-button>
                </div>
            </a-upload>
            <div class="bottom-tabs-content-right bottom-pictures-wrapper scroll-vertical-wrapper">
                <div v-if="invoicePaymentImgDatas.length > 0" class="scroll-vertical-content">
                    <picture-box
                        v-for="(item, index) of invoicePaymentImgDatas"
                        :key="item"
                        :resourceKey="item.url"
                        @preview="previewImg($Utils.getPictureSrc(item.url))"
                        @delete="deleteImg(index)"
                        :deleteAble="isUpload"
                    >
                    </picture-box>
                </div>
                <div v-else class="no-data-box" :style="{ 'margin-top': isUpload ? '10px' : '20px' }">
                    <div><a-icon type="file-excel" /></div>
                    暂无图片
                </div>
            </div>
        </div>

        <!-- 图片预览 模态框区域 -->
        <a-modal
            title="图片预览"
            :visible="preImgSt"
            :width="previewWid"
            cancelText="关闭"
            okText="打印"
            @ok="printFn"
            @cancel="closePreview"
            :maskClosable="false"
            :centered="true"
            class="modal-form-input-scoped global-drag imgModal"
        >
            <div v-globalDrag="{ el: 'imgModal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 图片预览
                </h6>
            </div>
            <div ref="printData" class="preview-box">
                <img :src="imgSrc" />
            </div>
        </a-modal>
    </div>
</template>

<script>
import PictureBox from '@/components/Picture/PictureBox';

import { getUploadTokenAPI } from '@/service/pageAjax';

export default {
    name: 'invoicePhoto', // 预付款的模态框中间部分付款凭证图片
    props: {
        isUpload: {
            // 图片是否能上传且可编辑
            type: Boolean,
            default: false
        },
        invoicePaymentImgDatas: {
            // 图片数据
            type: Array,
            default: function () {
                return [];
            }
        }
    },
    components: {
        PictureBox
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            upLoadparams: {},
            preImgSt: false,
            imgSrc: ''
        };
    },
    methods: {
        async getUploadTokenAPIFn() {
            // 获取上传 token
            await getUploadTokenAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.upLoadparams = {
                        token: res.data
                    };
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async checkFile(file) {
            let fileName = this.$Utils.formatFileNameToLowerCase(file.name);
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
                let msg = '仅支持png / jpeg 格式图片文件';
                this.$message.error(msg);
                return Promise.reject(msg);
            }
            await this.getUploadTokenAPIFn();
        },
        upGoodsImg(info) {
            // 上传图片
            info.file.name = this.$Utils.formatFileNameToLowerCase(info.file.name);

            if (info.file.status === 'done') {
                const res = info.file.response;
                if (res.Code || res.code === 0) {
                    const { data } = res;
                    this.invoicePaymentImgDatas.push({
                        ...data,
                        url: data.key,
                        file_name: info.file.name
                    });

                    this.$emit('getInvoicePaymentImgData', this.invoicePaymentImgDatas);
                } else {
                    this.$message.error(res.msg);
                }
            }
        },
        previewImg(src) {
            // 图片预览
            let that = this;
            if (src && src !== '') {
                let img = new Image();
                img.src = src;
                img.onload = function () {
                    that.previewWid = img.width + 50;
                    let orderWidth = document.body.clientWidth; // 最大不能超过屏幕宽度
                    if (that.previewWid > orderWidth) {
                        that.previewWid = orderWidth - 246;
                    }
                    if (that.previewWid < 300) {
                        that.previewWid = 300;
                    }
                    that.preImgSt = true;
                    that.imgSrc = src;
                    that.$Utils.globalDragCenterFn('imgModal');
                };
            }
        },
        deleteImg(index) {
            // 删除图片
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '您确定要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.invoicePaymentImgDatas.splice(index, 1);
                }
            });
        },
        printFn() {
            // 打印
            let printContent = {
                printData: this.$refs.printData.innerHTML,
                printType: 1 // printType  1、图片；2、耗材申领；3、出入库单; 4、询价单
            };
            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        closePreview() {
            this.preImgSt = false;
        }
    }
};
</script>

<style lang="less" scoped>
.form-table {
    .caption {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.65);
        margin-top: 20px;
    }

    .form-photo {
        height: 180px;
        overflow: auto;
        border: 1px solid #dadada;
        margin: 10px 0 20px;
        padding: 10px 10px 2px;
    }
}

.preview-box {
    display: block;
    text-align: center;
    max-height: 78vh;
    overflow-y: auto;
}

.no-data-box {
    font-size: 18px;
    color: #ccc;
}
</style>
