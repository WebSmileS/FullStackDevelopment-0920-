<template>
    <div class="scoped">
        <!-- 注册信息修改区域开始 -->
        <div class="modal-form-input-scoped modal-form-input-media">
            <div class="modal-form-header">
                <img class="logo" src="/images/logo/main-logo.png" alt="医润通欢迎您" />
                <h5 class="text">账户注册</h5>
            </div>
            <div class="modal-form-input">
                <ul>
                    <li class="item">
                        <span class="label"><em class="em"></em>账户类型</span>
                        <a-radio-group
                            name="radioGroup"
                            :value="editInfoFormObj.system_type"
                            @change="changeAccountTypeFn"
                            class="input"
                        >
                            <a-radio
                                v-for="(systemTypeItem, systemTypeIndex) in systemTypeAry"
                                :key="systemTypeIndex"
                                :value="systemTypeItem.type"
                                style="margin-right: 30px"
                                >{{ systemTypeItem.name }}</a-radio
                            >
                        </a-radio-group>
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>行政区划</span>
                        <a-cascader
                            :options="provinces"
                            :loadData="provinceLoads"
                            v-model="provinceDefaults"
                            @change="changeProvincesFn"
                            changeOnSelect
                            placeholder=""
                            :allowClear="false"
                            class="input"
                        />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>{{ orgName }}名称</span>
                        <a-input v-model="editInfoFormObj.name" placeholder="" class="input" />
                    </li>
                    <li class="item" v-show="editInfoFormObj.system_type === 1">
                        <!--只有医院才显示别名-->
                        <span class="label letter-space"><em class="em"></em>别名</span>
                        <a-input v-model="editInfoFormObj.alias" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>统一社会信用代码</span>
                        <a-input
                            v-model="editInfoFormObj.social_credit_code"
                            placeholder="字母或数字组合的18位"
                            class="input"
                        />
                    </li>
                    <li class="item adress-wrap">
                        <span class="label"><em class="em"></em>地址</span>
                        <a-input v-model="editInfoFormObj.address" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>联系人</span>
                        <a-input v-model="editInfoFormObj.registrant" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label"><em class="em red">*</em>手机号码</span>
                        <a-input v-model="editInfoFormObj.registrant_phone" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label letter-space"><em class="em"></em>邮箱</span>
                        <a-input v-model="editInfoFormObj.registrant_email" placeholder="" class="input" />
                    </li>
                </ul>
            </div>
            <div class="modal-form-footer">
                <a-button class="btn" @click="registCancelFn">取消</a-button>
                <a-button :loading="registOkLoadingFlag" type="primary" @click="registOkFn">提交</a-button>
            </div>
        </div>
        <!-- 注册信息修改区域结束 -->

        <!-- 机构注册审核信息修改区域开始 -->
        <a-modal
            v-model="registInfoModal.alert"
            :title="registInfoModal.title"
            :afterClose="afterCloseFn"
            class="modal-form-input-scoped"
        >
            <template slot="footer">
                <a-button key="submit" type="primary" @click="afterCloseFn"> 直接跳转到登录页 </a-button>
            </template>
            <div class="modal-form-input">
                <p>恭喜您已成功注册医润通账户，</p>
                <p>
                    页面将在
                    <span class="green" style="font-size: 20px">{{ registInfoModal.time }}s</span> 后跳转到登录页
                </p>
            </div>
        </a-modal>
        <!-- 机构注册审核信息修改区域结束 -->
    </div>
</template>

<script>
import { provinceAreaAPI, cityAreaAPI, countyAreaAPI, registAddAPI } from '@/service/pageAjax';
export default {
    name: 'Regist',
    data() {
        return {
            orgName: '医院',
            systemTypeAry: [
                { name: '医院', type: 1 },
                { name: '经销商', type: 2 },
                { name: '厂商', type: 3 }
            ],
            // 省 - 市 - 区
            provinces: [],
            provinceDefaults: [], // 北京：[110000, 110100, 110101]
            editInfoFormObj: {
                // 修改信息的模态框值
                administrative_division_sn: '',
                system_type: 1,
                name: '',
                alias: '',
                address: '',
                registrant: '',
                registrant_phone: '',
                registrant_email: '',
                description: '',
                social_credit_code: ''
            },
            registOkLoadingFlag: false, // 按钮loading状态
            // 修改弹框的配置
            registInfoModal: {
                title: `${this.$route.meta.title} - 成功提示`,
                alert: false,
                time: 10
            }
        };
    },
    methods: {
        // 注册新机构
        async registAddAPIFn() {
            await registAddAPI({
                ...this.editInfoFormObj,
                address: this.editInfoFormObj.address || ''
            })
                .then((res) => {
                    this.registOkLoadingFlag = false;
                    if (parseFloat(res.code) === 0) {
                        this.$emit('confirmReg', true);
                        this.$message.success(
                            '您已成功注册医润通系统,审核通过后初始账号信息将通过短信方式发送至您的手机。',
                            5
                        );
                        this.redirectLoginFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .catch(() => {
                    this.registOkLoadingFlag = false;
                });
        },

        // 获取全部省份列表
        async provinceAreaAPIFn() {
            await provinceAreaAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const datas = res.province.map((item) => {
                        item.value = item.administrative_division_sn;
                        item.label = item.name;
                        item.isLeaf = item.is_leaf === 1; // is_leaf 1：没有下一级叶子节点   0：有
                        return item;
                    });

                    this.provinces = [...datas];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据省份id查询所有的下级城市信息
        async cityAreaAPIFn(targetOption) {
            await cityAreaAPI(targetOption.value || targetOption).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const datas = res.city.map((item) => {
                        item.value = item.administrative_division_sn;
                        item.label = item.name;
                        item.isLeaf = item.is_leaf === 1; // is_leaf 1：没有下一级叶子节点   0：有
                        return item;
                    });

                    targetOption.loading = false;
                    targetOption.children = [...datas];
                    this.provinces = [...this.provinces];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 根据城市id查询所有的下级区域信息
        async countyAreaAPIFn(targetOption) {
            await countyAreaAPI(targetOption.value || targetOption).then((res) => {
                if (parseFloat(res.code) === 0) {
                    const datas = res.county.map((item) => {
                        item.value = item.administrative_division_sn;
                        item.label = item.name;
                        // item.isLeaf = item.is_leaf === 1 // is_leaf 1：没有下一级叶子节点   0：有
                        return item;
                    });

                    targetOption.loading = false;
                    targetOption.children = [...datas];
                    this.provinces = [...this.provinces];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 异步加载省市区数据
        provinceLoads(selectedOptions) {
            const targetOption = selectedOptions[selectedOptions.length - 1];
            targetOption.loading = true;
            if (!targetOption.parent_administrative_division_sn) {
                this.cityAreaAPIFn(targetOption);
            } else {
                this.countyAreaAPIFn(targetOption);
            }
        },
        // 省市区数据选择完成后的回调
        changeProvincesFn(value) {
            this.editInfoFormObj.administrative_division_sn = value[value.length - 1];
        },
        // 账户类型切换获取值
        changeAccountTypeFn(e) {
            const value = parseFloat(e.target.value);
            this.editInfoFormObj.system_type = value;
            this.orgName = this.systemTypeAry[value - 1].name;
        },
        // 检测修改的表单信息校验
        checkEditInfoFormMsgFn() {
            const regExpFn = this.$Utils.regExpFn(),
                mobilePhone = regExpFn.mobilePhone,
                email = regExpFn.email,
                socialCreditCode = regExpFn.socialCreditCode;

            this.editInfoFormObj.name = this.$Utils.trim(this.editInfoFormObj.name);
            this.editInfoFormObj.social_credit_code = this.$Utils.trim(this.editInfoFormObj.social_credit_code);
            this.editInfoFormObj.address = this.$Utils.trim(this.editInfoFormObj.address);
            this.editInfoFormObj.registrant = this.$Utils.trim(this.editInfoFormObj.registrant);
            this.editInfoFormObj.registrant_phone = this.$Utils.trim(this.editInfoFormObj.registrant_phone);

            // 行政区域
            if (!this.editInfoFormObj.administrative_division_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择行政区划',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 医院名称
            if (!this.editInfoFormObj.name) {
                this.$warning({
                    title: '操作提示',
                    content: `请输入${this.orgName}名称`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            // 医院名称 02
            if (!this.$Utils.trim(this.editInfoFormObj.name)) {
                this.$warning({
                    title: '操作提示',
                    content: `请输入${this.orgName}名称`,
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                this.editInfoFormObj.name = this.$Utils.trim(this.editInfoFormObj.name);
                return false;
            }
            // 医院名称 03
            if (this.$Utils.checkInputValFn(this.editInfoFormObj.name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.orgName}名称中请不要输入特殊字符`,
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return false;
            }

            // 统一社会信用代码 01
            if (!this.editInfoFormObj.social_credit_code) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入统一社会信用代码',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 统一社会信用代码 02
            if (this.editInfoFormObj.social_credit_code && this.editInfoFormObj.social_credit_code.length !== 18) {
                this.$warning({
                    title: '操作提示',
                    content: '统一社会信用代码有效长度是18位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 统一社会信用代码 03
            if (
                this.editInfoFormObj.social_credit_code &&
                !socialCreditCode.test(this.editInfoFormObj.social_credit_code)
            ) {
                this.$warning({
                    title: '操作提示',
                    content: '统一社会信用代码格式为字母或数字组合的18位',
                    cancelText: '取消',
                    okText: '确认',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }
            // 地址
            // if (!this.editInfoFormObj.address) {
            //     this.$warning({
            //         title: '操作提示',
            //         content: '请输入地址',
            //         okText: '知道了',
            //         centered: true,
            //         maskClosable: false
            //     })
            //     return false
            // }

            // 联系人
            if (!this.editInfoFormObj.registrant) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入联系人',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 手机号码 01
            if (!this.editInfoFormObj.registrant_phone) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入手机号码',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 手机号码 02
            if (this.editInfoFormObj.registrant_phone.length > 11) {
                this.$warning({
                    title: '操作提示',
                    content: '手机号码有效长度是11位',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 手机号码 03
            if (!mobilePhone.test(this.editInfoFormObj.registrant_phone)) {
                this.$warning({
                    title: '操作提示',
                    content: '手机号码格式输入有误',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            // 邮箱
            if (this.editInfoFormObj.registrant_email) {
                if (!email.test(this.editInfoFormObj.registrant_email)) {
                    this.$warning({
                        title: '操作提示',
                        content: '邮箱格式输入有误',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }
            }

            return true;
        },
        // 模态框操作
        registOkFn() {
            if (!this.checkEditInfoFormMsgFn()) return; // 前端格式校验
            this.registOkLoadingFlag = true;
            this.registAddAPIFn();
        },
        registCancelFn() {
            this.$emit('cancelReg', false);
            this.orgName = '医院';
            this.redirectLoginFn();
        },
        // 重置页面状态
        resetPageValFn() {
            this.provinceDefaults = [];
            this.registOkLoadingFlag = false;
            this.registInfoModal = {
                title: `${this.$route.meta.title} - 成功提示`,
                alert: false,
                time: 10
            };
            this.orgName = '医院';
            this.editInfoFormObj = {
                administrative_division_sn: '',
                system_type: 1,
                name: '',
                alias: '',
                address: '',
                registrant: '',
                registrant_phone: '',
                registrant_email: '',
                description: '',
                social_credit_code: ''
            };
        },
        // 跳转到登录页面
        redirectLoginFn() {
            this.resetPageValFn();
        },
        // 关闭后的回调
        afterCloseFn() {
            this.redirectLoginFn();
        }
    },
    created() {
        this.resetPageValFn();
        this.provinceAreaAPIFn();
    }
};
</script>

<style lang="less" scoped>
@white: #fff;
@w: 48px;

.scoped {
    display: flex;
    align-items: center;
    justify-content: center;

    .modal-form-input-scoped {
        position: relative;
        min-width: 640px;
        min-height: 604px;
        background: @white;
        border-radius: 8px;
        overflow: hidden;
    }

    // 头部区域
    .modal-form-header {
        display: flex;
        flex-direction: row;
        justify-content: flex-start;
        align-items: center;
        min-height: 64px;
        padding: 0 23px;
        background: #2090e6;
        color: @white;

        .logo {
            width: 140px;
            height: 33px;
            margin-right: 10px;
        }

        .text {
            text-indent: 0;
            font-size: 18px;
            color: @white;
            margin-bottom: 0;
        }
    }

    // 中间主体部分
    .modal-form-input {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 28px @w @w;

        ul {
            width: 100%;
        }

        .label {
            width: 154px;
            padding-right: 28px;

            em {
                position: relative;
                top: 2px;
                font-style: normal;
            }

            &.letter-space {
                letter-spacing: 23px;
                padding-right: 6px;
            }
        }

        .input {
            text-align: left;
        }
    }

    // 底部按钮
    .modal-form-footer {
        position: absolute;
        left: 0;
        right: 0;
        bottom: 0;
        display: flex;
        flex-direction: row;
        justify-content: flex-end;
        align-items: center;
        min-height: 56px;
        background: #e3e6e9;
        padding: 0 @w;
        border-radius: 0 0 8px 8px;

        .btn {
            margin-right: 13px;
        }
    }
}

@media only screen and (max-width: 800px) {
    #app {
        .scoped {
            overflow: auto;
        }

        .modal-form-input-media {
            .modal-form-input {
                padding: 28px;
            }

            .label {
                width: 45%;
            }

            .modal-form-footer {
                padding: 0 28px;
            }

            .modal-form-header {
                justify-content: center;
                padding: 0;
            }
        }
    }
}
</style>
