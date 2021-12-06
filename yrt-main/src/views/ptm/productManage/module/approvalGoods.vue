<template>
    <div class="modal-list" style="margin: -24px">
        <div class="table-part">
            <a-table
                class="nu-table"
                style="top: 0; height: 360px"
                :columns="columns"
                :dataSource="tableData"
                :scroll="{ y: 300 }"
                :pagination="false"
            >
                <template slot="beforeEdit" slot-scope="text, record">
                    <picture-box
                        v-if="record.property_name === '图片' && text !== ''"
                        :styles="{ marginRight: 0, marginBottom: 0 }"
                        imageWidth="60px"
                        imageHeight="60px"
                        :resourceKey="text"
                        :deleteAble="false"
                        @preview="previewImg($Utils.getPictureSrc(text))"
                    />
                    <template v-else>{{ text }}</template>
                </template>
                <template slot="afterEdit" slot-scope="text, record">
                    <picture-box
                        v-if="record.property_name === '图片' && text !== ''"
                        :styles="{ marginRight: 0, marginBottom: 0 }"
                        imageWidth="60px"
                        imageHeight="60px"
                        :resourceKey="text"
                        :deleteAble="false"
                        @preview="previewImg($Utils.getPictureSrc(text))"
                    />
                    <template v-else>{{ text }}</template>
                </template>
            </a-table>
        </div>
        <div class="part part-2">
            <span class="label" style="margin-right: 10px">仅显示差异属性</span>
            <a-checkbox @change="changeSwitch" :checked="switchType" />
        </div>
        <div class="part img_count" v-if="imageList && imageList.length > 0">
            <div class="img_box" v-for="(item, idx) of imageList" :key="idx">
                <!-- PDF文件 -->
                <picture-box
                    v-if="$Utils.checkPdf(item.file_name)"
                    fileType="pdf"
                    imageWidth="60px"
                    imageHeight="60px"
                    :resourceKey="item.url"
                    :deleteAble="false"
                    @preview="previewPdf($Utils.getPictureSrc(item.url))"
                />
                <!-- 非PDF文件 -->
                <picture-box
                    v-else
                    imageWidth="60px"
                    imageHeight="60px"
                    :resourceKey="item.url"
                    :deleteAble="false"
                    @preview="previewImg($Utils.getPictureSrc(item.url))"
                />
            </div>
        </div>
        <div class="part part-3">
            <a-radio-group :options="options" @change="onChangeRadio" :value="chooseRadio" />
        </div>
        <div class="part part-4">
            <span class="label" style="margin-right: 10px; width: 30px; padding-top: 6px">说明</span>
            <a-textarea :disabled="isNo" class="text-a" v-model="explain" placeholder="" :autosize="textAreaSet" />
        </div>
        <div class="button_cont">
            <a-button @click="aduditFn" type="primary" style="float: right; right: 22px; top: 13px">确定</a-button>
            <a-button @click="cancelFn" style="float: right; right: 34px; top: 13px">取消</a-button>
        </div>
        <a-modal
            title="图片预览"
            :visible="preImgSt"
            :width="previewWid"
            cancelText="关闭"
            okText="打印"
            @ok="printFn"
            @cancel="closePreview"
            :mask="false"
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
                <img style="width: 100%" :src="imgSrc" />
            </div>
        </a-modal>
    </div>
</template>

<script>
import PictureBox from '@/components/Picture/PictureBox';
import { productTmpDetailAPI, productAuditAPI, platformProductSyncAPI } from '@/service/pageAjax';

export default {
    name: 'approvalGoods',
    props: ['dataObj'],
    components: {
        PictureBox
    },
    data() {
        return {
            leftTitle: `${this.$route.meta.title}`,
            preImgSt: false,
            previewWid: '',
            imgSrc: '',
            columns: [
                // 平台
                {
                    title: '属性名称',
                    dataIndex: 'property_name',
                    align: 'center',
                    width: '40%'
                },
                {
                    title: '修改前',
                    dataIndex: 'old_value',
                    align: 'center',
                    width: '30%',
                    scopedSlots: {
                        customRender: 'beforeEdit'
                    }
                },
                {
                    title: '修改后',
                    dataIndex: 'new_value',
                    align: 'center',
                    width: '30%',
                    scopedSlots: {
                        customRender: 'afterEdit'
                    }
                }
            ],
            options: [
                { label: '通过', value: 0 },
                { label: '不通过', value: 1 },
                { label: '已有相同审核', value: 2 }
            ],
            textAreaSet: {
                minRows: 4,
                maxRows: 6
            },
            chooseRadio: 0,
            explain: '',
            chooseObj: {},
            infoData: {},
            tableData: [],
            checkData: [],
            allData: [],
            switchType: true,
            isNo: true,
            storageCon: [
                {
                    value: 0,
                    name: '其它'
                },
                {
                    value: 1,
                    name: '常温'
                },
                {
                    value: 2,
                    name: '保温'
                },
                {
                    value: 3,
                    name: '阴凉'
                },
                {
                    value: 4,
                    name: '冷藏'
                },
                {
                    value: 5,
                    name: '冷冻'
                },
                {
                    value: 6,
                    name: '无菌'
                },
                {
                    value: 7,
                    name: '特储'
                }
            ],
            healthCare: [
                {
                    value: 0,
                    name: 'A型'
                },
                {
                    value: 1,
                    name: 'B型'
                },
                {
                    value: 2,
                    name: 'C型'
                }
            ],
            goodsType: ['医疗器械', '非医疗器械'],
            x68Type: ['Ⅰ类', 'Ⅱ类', 'Ⅲ类'],
            imageList: []
        };
    },
    methods: {
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
        getInfo() {
            this.$emit('getInfo', true);
        },
        async initPage(obj) {
            this.chooseObj = obj;
            this.allData = [];
            this.checkData = [];
            this.chooseRadio = 0;
            this.switchType = true;
            this.tableData = [];
            this.explain = '';
            this.isNo = true;
            this.imageList = obj.images;
            let params = {
                org_id: obj.operation_organization,
                source: obj.source,
                vendor_inner_sn: obj.vendor_inner_sn,
                product_inner_sn: obj.product_inner_sn,
                operation: obj.operation,
                audit_type: obj.audit_type,
                image_inner_sn: obj.image_inner_sn,
                specification_inner_sn: obj.specification_inner_sn,
                mdrf_inner_sn: obj.mdrf_inner_sn
            };
            await productTmpDetailAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    res.info.forEach((item) => {
                        if (item.property_name === '存储条件') {
                            if (item.new_value !== '') {
                                item.new_value = this.storageCon[parseInt(item.new_value)].name;
                            }
                            if (item.old_value !== '') {
                                item.old_value = this.storageCon[parseInt(item.old_value)].name;
                            }
                        }
                        if (item.property_name === '医保类别') {
                            if (item.new_value !== '') {
                                item.new_value = this.healthCare[parseInt(item.new_value)].name;
                            }
                            if (item.old_value !== '') {
                                item.old_value = this.healthCare[parseInt(item.old_value)].name;
                            }
                        }
                        if (item.property_name === '产品类型') {
                            if (item.new_value !== '') {
                                item.new_value = this.goodsType[parseInt(item.new_value)];
                            }
                            if (item.old_value !== '') {
                                item.old_value = this.goodsType[parseInt(item.old_value)];
                            }
                        }
                        if (item.property_name === '医疗器械分类目录类别') {
                            if (item.new_value !== '') {
                                item.new_value = this.x68Type[parseInt(item.new_value) - 1];
                            }
                            if (item.old_value !== '') {
                                item.old_value = this.x68Type[parseInt(item.old_value) - 1];
                            }
                        }
                    });
                    this.allData = JSON.parse(JSON.stringify(res.info));
                    let newArr = [];
                    this.allData.forEach((item) => {
                        if (item.match === false) {
                            newArr.push(item);
                        }
                    });
                    this.checkData = newArr;
                    this.tableData = newArr;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        onChangeRadio(e) {
            this.chooseRadio = e.target.value;
            if (parseInt(this.chooseRadio) === 1) {
                this.isNo = false;
            } else {
                this.isNo = true;
            }
        },
        cancelFn() {
            this.$emit('close');
        },
        async aduditFn() {
            let obj = this.chooseObj;
            let params = {
                org_id: obj.operation_organization,
                source: obj.source,
                vendor_inner_sn: obj.vendor_inner_sn,
                product_inner_sn: obj.product_inner_sn,
                operation: obj.operation,
                audit_type: obj.audit_type,
                auditStatus: this.chooseRadio,
                description: this.explain,
                image_inner_sn: obj.image_inner_sn,
                specification_inner_sn: obj.specification_inner_sn,
                mdrf_inner_sn: obj.mdrf_inner_sn
            };
            const { code, msg } = await productAuditAPI(params);
            if (code !== 0) {
                this.$message.error(msg);
                throw new Error(msg);
            }
            this.$message.success('审核成功');
            this.$emit('done', this.chooseRadio, obj.audit_type);
            if (params.auditStatus === 0) {
                const { code, msg } = await platformProductSyncAPI(params.vendor_inner_sn, params.product_inner_sn);
                if (code !== 0) {
                    this.$message.error(msg);
                    throw new Error(msg);
                }
                this.$message.success('已自动同步产品信息');
            }
        },
        changeSwitch(status) {
            this.switchType = status.target.checked;
            if (this.switchType) {
                this.tableData = this.checkData;
            } else {
                this.tableData = this.allData;
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
                    if (that.previewWid > 1600) {
                        that.previewWid = 1600;
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
        previewPdf(src) {
            window.open(src, '_blank');
        },
        closePreview() {
            this.preImgSt = false;
        },
        handleCancel() {
            this.previewVisible = false;
        },
        handlePreview(file) {
            this.previewImage = file.url || file.thumbUrl;
            this.previewVisible = true;
        }
    },
    mounted() {
        this.getInfo();
    }
};
</script>

<style scoped lang="less">
.modal-list {
    padding-bottom: 40px;
}
.part {
    padding: 10px 22px;
}
.button_cont {
    position: absolute;
    width: 100%;
    bottom: 25px;
}
.text-a {
    display: inline-block;
    width: 94%;
}
.label {
    display: inline-block;
    width: 12%;
    vertical-align: top;
}
textarea {
    resize: none;
}
.img_box {
    display: inline-block;
    width: 60px;
    height: 80px;
    margin: 0 6px;
    margin-bottom: 12px;
}
.preview-box {
    text-align: center;
    display: block;
    max-height: 78vh;
    overflow-y: auto;
}
</style>
