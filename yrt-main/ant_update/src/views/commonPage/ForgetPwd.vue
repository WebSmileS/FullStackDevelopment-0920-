<template>
    <div class="scoped">
        <!-- 忘记密码区域开始 -->
        <div class="modal-form-input-scoped modal-form-input-media">
            <div class="modal-form-header">
                <img class="logo" src="../../assets/logo-top.png" alt="" />
                <h5 class="text">忘记密码</h5>
            </div>
            <div class="modal-form-input" v-if="stepOne">
                <ul>
                    <li class="item">
                        <span class="label" style="width: 120px"><em class="em red">*</em>手机号码</span>
                        <a-input v-model="editInfoFormObj.registrant_phone" placeholder="" class="input" />
                    </li>
                    <li class="item">
                        <span class="label" style="width: 120px"><em class="em red">*</em>验证码</span>
                        <a-input
                            v-model="editInfoFormObj.verifyCode"
                            placeholder=""
                            class="input"
                            :disabled="verifyEnable"
                        />
                        <a-button
                            type="primary"
                            size="small"
                            style="width: 86px"
                            :disabled="verifyEnableBtn"
                            @click="getVerifyCode"
                            >{{ infoText }}</a-button
                        >
                    </li>
                </ul>
            </div>
            <div class="modal-form-input" v-if="stepTwo">
                <ul>
                    <li class="item">
                        <span class="label" style="width: 120px"><em class="em red">*</em>修改密码</span>
                        <a-input
                            type="password"
                            class="input"
                            style="width: 260px"
                            placeholder="密码为6-16位字符，需包含字母和数字"
                            v-model="editInfoFormObj.password"
                        />
                    </li>
                    <li class="item">
                        <span class="label" style="width: 120px"><em class="em red">*</em>确认密码</span>
                        <a-input
                            type="password"
                            placeholder="密码为6-16位字符，需包含字母和数字"
                            style="width: 260px"
                            class="input"
                            v-model="editInfoFormObj.confirmPassword"
                        />
                    </li>
                </ul>
            </div>
            <div class="modal-form-footer">
                <a-button class="btn" @click="fPwdCancelFn">取消</a-button>
                <a-button :loading="confirmBtnLoading" type="primary" @click="fPwdOkFn">{{ confirmBtn }}</a-button>
            </div>
        </div>
        <!-- 忘记密码区域结束 -->
    </div>
</template>

<script>
import { userChangePasswordAPI, userGetVerifyCodeAPI, userVerifyCodeAPI } from '@/service/pageAjax';
export default {
    name: 'ForgetPwd',
    data() {
        return {
            // 修改信息的模态框值
            editInfoFormObj: {
                registrant_phone: '',
                verifyCode: '',
                password: '',
                confirmPassword: ''
            },
            infoText: '获取验证码',
            confirmBtnLoading: false, // 按钮loading状态
            // 修改弹框的配置
            fPwdInfoModal: {
                title: `${this.$route.meta.title} - 成功提示`,
                alert: false,
                time: 10
            },
            // 验证码输入框初次不可输入
            verifyEnable: true,
            // 获取验证码
            verifyEnableBtn: false,
            // 步骤按钮
            confirmBtn: '下一步',
            stepOne: true,
            stepTwo: false,
            // 验证码倒计时interval
            timer: null
        };
    },
    methods: {
        // 获取手机验证码
        async getVerifyCodeAPIFn() {
            try {
                const res = await userGetVerifyCodeAPI(this.editInfoFormObj.registrant_phone);
                if (parseFloat(res.Code || res.code) === 0) {
                    this.$message.success('验证码发送成功，请在5分钟内尽快输入。', 5);
                    return true;
                } else {
                    this.registOkLoadingFlag = false;
                    this.verifyEnable = true;
                    this.verifyEnableBtn = false;
                    this.$message.error(res.msg || res.Msg);
                    return false;
                }
            } catch (error) {
                console.error(error);
                this.verifyEnable = true;
                this.verifyEnableBtn = false;
                return false;
            }
        },
        // 验证手机验证码
        async userVerifyCodeAPIFn() {
            const params = {
                Name: this.editInfoFormObj.registrant_phone,
                VerifyCode: this.editInfoFormObj.verifyCode
            };
            await userVerifyCodeAPI(params).then((res) => {
                if (parseFloat(res.Code || res.code) === 0) {
                    this.stepOne = false;
                    this.stepTwo = true;
                    this.confirmBtn = '提交';
                } else {
                    this.registOkLoadingFlag = false;
                    this.$message.error(res.msg || res.Msg);
                }
            });
        },
        // 修改密码
        async userChangePasswordAPIFn() {
            if (this.confirmBtnLoading) {
                return;
            }

            this.confirmBtnLoading = true;
            try {
                const params = {
                    Name: this.editInfoFormObj.registrant_phone,
                    Password: this.editInfoFormObj.password,
                    VerifyCode: this.editInfoFormObj.verifyCode
                };
                const res = await userChangePasswordAPI(params);

                if (parseFloat(res.Code || res.code) === 0) {
                    this.$message.success('修改成功，请重新登陆。', 5);
                    this.$emit('confirmForgetPwd', true);
                } else {
                    this.registOkLoadingFlag = false;
                    this.$message.error(res.msg || res.Msg);
                }
            } catch (error) {
                console.error(error);
            } finally {
                this.confirmBtnLoading = false;
            }
        },
        // 检测修改的表单信息校验
        checkEditInfoFormMsgFn(type) {
            this.editInfoFormObj.registrant_phone = this.$Utils.trim(this.editInfoFormObj.registrant_phone);
            this.editInfoFormObj.password = this.$Utils.trim(this.editInfoFormObj.password);
            this.editInfoFormObj.confirmPassword = this.$Utils.trim(this.editInfoFormObj.confirmPassword);

            const phoneRegExp = this.$Utils.regExpFn().mobilePhone;
            const passwordRegExp = this.$Utils.regExpFn().password;
            const { password, verifyCode, confirmPassword, registrant_phone: phone } = this.editInfoFormObj;

            // 校验密码
            if (type === 2) {
                if (!password) {
                    this.$warning({
                        title: '操作提示',
                        content: '密码不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }

                if (!confirmPassword) {
                    this.$warning({
                        title: '操作提示',
                        content: '确认密码不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }

                if (
                    password.length < 6 ||
                    password.length > 16 ||
                    confirmPassword.length < 6 ||
                    confirmPassword.length > 16
                ) {
                    this.$warning({
                        title: '操作提示',
                        content: '密码长度为6-16位，请重新输入',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }

                if (!passwordRegExp.test(password) || !passwordRegExp.test(confirmPassword)) {
                    this.$warning({
                        title: '操作提示',
                        content: '密码中必须包含字母和数字',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }

                // 密码验证
                if (password !== confirmPassword) {
                    this.$warning({
                        title: '操作提示',
                        content: '两次输入密码不相同，请重新输入',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }
            } else {
                // 校验手机号
                // 手机号码 01
                if (!phone) {
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
                if (phone.length > 11) {
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
                if (!phoneRegExp.test(phone)) {
                    this.$warning({
                        title: '操作提示',
                        content: '手机号码格式输入有误',
                        okText: '知道了',
                        centered: true,
                        maskClosable: false
                    });
                    return false;
                }

                // 校验验证码
                if (type === 1) {
                    // 验证码 01
                    if (!verifyCode) {
                        this.$warning({
                            title: '操作提示',
                            content: '请输入验证码',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        return false;
                    }
                    // 验证码 02
                    if (verifyCode.length !== 6) {
                        this.$warning({
                            title: '操作提示',
                            content: '请输入6位数字验证码',
                            okText: '知道了',
                            centered: true,
                            maskClosable: false
                        });
                        return false;
                    }
                }
            }

            return true;
        },

        // 获取手机验证码
        async getVerifyCode() {
            if (!this.checkEditInfoFormMsgFn(0)) return; // 前端格式校验

            // 防止瞬时点击
            if (!this.verifyEnable && this.verifyEnableBtn) {
                return;
            }

            this.verifyEnable = false;
            this.verifyEnableBtn = true;
            const isSuccess = await this.getVerifyCodeAPIFn();

            if (isSuccess) {
                this.countdown();
            }
        },
        // 倒计时，计数器开始执行
        countdown() {
            let seconds = 60;
            this.timer = setInterval(() => {
                seconds--;
                this.infoText = seconds + 's';
                if (seconds <= 0) {
                    clearInterval(this.timer);
                    this.infoText = '获取验证码';
                    this.verifyEnableBtn = false;
                }
            }, 1000);
        },
        // 重置页面状态
        resetPageValFn() {
            this.provinceDefaults = [];
            this.confirmBtnLoading = false;
            this.stepOne = true;
            this.stepTwo = false;
            this.fPwdInfoModal = {
                title: `${this.$route.meta.title} - 成功提示`,
                alert: false,
                time: 10
            };
            this.editInfoFormObj = {
                registrant_phone: '',
                verifyCode: '',
                password: '',
                confirmPassword: ''
            };
            this.infoText = '获取验证码';
            this.verifyEnable = true;
            this.verifyEnableBtn = false;
            clearInterval(this.timer);
        },
        // 模态框操作
        fPwdOkFn() {
            if (this.stepOne) {
                if (!this.checkEditInfoFormMsgFn(1)) return; // 前端格式校验
                this.userVerifyCodeAPIFn();
            } else {
                if (!this.checkEditInfoFormMsgFn(2)) return; // 前端格式校验
                this.userChangePasswordAPIFn();
            }
        },
        // 取消忘记密码
        fPwdCancelFn() {
            this.resetPageValFn();
            this.$emit('cancelForgetPwd', false);
        }
    },
    created() {
        this.resetPageValFn();
    },
    beforeDestroy() {
        // 清除验证码倒计时定时器
        clearInterval(this.timer);
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
        min-width: 400px;
        min-height: 250px;
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
            height: 30px;
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
