<template>
    <div class="login-container">
        <div class="login-body">
            <div class="login-title">
                <img src="/images/pic/login-logo.png" alt="医润通欢迎您" />
            </div>
            <div class="login-form">
                <a-form id="components-form-demo-normal-login" :form="form" class="login-form" @submit="handleSubmit">
                    <a-form-item>
                        <a-input
                            v-decorator="[
                                'userName',
                                { rules: [{ required: true, message: '请输入用户名/手机号！' }, {}] }
                            ]"
                            placeholder="用户名/手机号码"
                        >
                            <a-icon slot="prefix" type="user" style="color: rgba(14, 71, 153, 1)" />
                        </a-input>
                    </a-form-item>
                    <a-form-item>
                        <a-input
                            v-decorator="['password', { rules: [{ required: true, message: '请输入密码！' }] }]"
                            type="password"
                            placeholder="密码"
                        >
                            <a-icon slot="prefix" type="lock" style="color: rgba(14, 71, 153, 1)" />
                        </a-input>
                    </a-form-item>
                    <a-form-item class="login-forget-pwd">
                        <a-checkbox
                            class="login-form-remenber"
                            v-decorator="[
                                'remember',
                                {
                                    valuePropName: 'checked',
                                    initialValue: true
                                }
                            ]"
                        >
                            记住密码
                        </a-checkbox>
                        <a class="login-form-forgot" href="javascript:;" @click="forgetPwdFn"> 不记得密码？ </a>
                    </a-form-item>
                    <a-form-item>
                        <a-button
                            :loading="loadingSubmitFlag"
                            type="primary"
                            html-type="submit"
                            class="login-form-button"
                        >
                            登录
                        </a-button>
                    </a-form-item>
                </a-form>
            </div>
            <div class="login-regist">没有账号？<a href="javascript:;" @click="regNow">立即注册</a></div>
            <div class="login-copyRight">
                <img class="login-tagline" src="/images/pic/login-tagline.png" alt="医润通欢迎您" />
                <p class="login-copyRight-aboutUs">
                    &copy;2016-2019 医修技术服务(北京)有限公司 All rights reserved.
                    <a style="color: #fff" href="https://beian.miit.gov.cn">{{ icp_code }}</a>
                </p>
                <p class="login-copyRight-right">本站最佳浏览分辨率: 1920*1080 或以上 最佳浏览器：Chrome</p>
            </div>
        </div>
        <div v-if="regShow" :class="regTrue ? 'regist-show' : 'modal-regist'">
            <regist-page v-on="{ confirmReg: regOkFn, cancelReg: cancelRegFn }"> </regist-page>
        </div>
        <div v-if="forgetPwd" :class="fPwdTrue ? 'regist-show' : 'modal-regist'" style="">
            <forget-pwd v-on="{ confirmForgetPwd: fPwdOkFn, cancelForgetPwd: cancelForgetPwdFn }"> </forget-pwd>
        </div>
    </div>
</template>
<script>
import { loginAPI } from '@/service/pageAjax';
import { mapActions } from 'vuex';
import registPage from './Regist';
import forgetPwd from './ForgetPwd';
export default {
    name: 'login',
    components: {
        registPage,
        forgetPwd
    },
    data() {
        return {
            regShow: false,
            regTrue: false,
            loadingSubmitFlag: false,
            forgetPwd: false,
            fPwdTrue: false,
            icp_code: ''
        };
    },
    beforeCreate() {
        let res = {
            info: {
                belong: this.$cookie.get('userbelong'),
                systemType: this.$cookie.get('userSystemType'),
                passwordCycle: this.$cookie.get('userPasswordCycle'),
                userInnerSn: this.$cookie.get('userInnerSn')
            },
            isLogin: ''
        };
        if (this.$cookie.get('loginStatus')) {
            this.$store.commit('changeStateInfo', res);
            this.$router.replace({ path: '/' });
        } else {
            this.$store.commit('changeStateInfo', { info: null, isLogin: Math.random() });
        }
        this.form = this.$form.createForm(this);
    },
    mounted() {
        if (window.location.hostname.includes('btyrt')) {
            this.icp_code = '京ICP备16056628号-3';
        } else {
            this.icp_code = '京ICP备16056628号-1';
        }
    },
    methods: {
        // 登陆
        async loginAPIFn() {
            let params = {
                userName: this.$Utils.trim(this.form.getFieldValue('userName')),
                password: this.$Utils.trim(this.form.getFieldValue('password'))
            };
            await loginAPI(params)
                .then((res) => {
                    if (res.code === 0) {
                        res.isLogin = '';
                        window.userCookieInfo = res.data;
                        this.$cookie.set('loginDatas', JSON.stringify(res.data));
                        this.$cookie.set('loginStatus', 'true');
                        // this.$cookie.set('userbelong', res.info[0].belong, { expires: '1D' })
                        // this.$cookie.set('userSystemType', res.info[0].system_type, { expires: '1D' })
                        // this.$cookie.set('userPasswordCycle', res.info[0].password_cycle, { expires: '1D' })
                        // this.$cookie.set('userInnerSn', res.info[0].user_inner_sn, { expires: '1D' })
                        this.$cookie.set('userbelong', res.data.UserInfo.Belong); // 用户归属机构（机构ID）
                        this.$cookie.set('userSystemType', res.data.UserInfo.System_type); // 用户机构类型
                        this.$cookie.set('userPasswordCycle', res.data.UserInfo.Password_cycle); // 用户密码过期事件
                        this.$cookie.set('userInnerSn', res.data.UserInfo.User_inner_sn); // 用户登陆ID
                        this.$cookie.set('EmployeeInfo', JSON.stringify(res.data.EmployeeInfo)); // 人员信息
                        this.$cookie.set('UserInfo', JSON.stringify(res.data.UserInfo)); // 用户账号信息
                        this.$store.commit('changeStateInfo', {
                            info: {
                                belong: res.data.UserInfo.Belong,
                                systemType: res.data.UserInfo.System_type,
                                passwordCycle: res.data.UserInfo.Password_cycle,
                                userInnerSn: res.data.UserInfo.User_inner_sn
                            },
                            isLogin: res.isLogin
                        });
                        this.$store.commit(
                            'setLastLoginUser',
                            `${res.data.UserInfo.User_inner_sn}-${res.data.UserInfo.System_type}`
                        );
                        this.$store.commit('setDepartmentsOfUserOrganization', null);
                        this.$store.commit('setEmployeesOfUserOrganization', null);
                        this.$store.commit('setWarehouseList', []);
                        this.$store.dispatch('loadDepartmentsOfUserOrganization');
                        this.$store.dispatch('loadEmployeesOfUserOrganization');
                        this.$store.dispatch('loadUserWarehouseList');
                        this.startReceiveOrderMsgTask();
                        this.$router.replace({ path: '/' });
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.loadingSubmitFlag = false;
                })
                .catch(() => {
                    this.$message.error('登录失败！');
                    this.loadingSubmitFlag = false;
                });
        },
        handleSubmit(e) {
            e.preventDefault();
            this.form.validateFields((err) => {
                if (!err) {
                    this.loadingSubmitFlag = true;
                    this.loginAPIFn();
                }
            });
        },
        // 立即注册
        regNow() {
            this.regShow = true;
            this.regTrue = true;
            this.fPwdTrue = false;
        },
        // 忘记密码
        forgetPwdFn() {
            this.forgetPwd = true;
            this.fPwdTrue = true;
        },
        regOkFn() {
            this.regTrue = false;
        },
        // 忘记密码-确认
        fPwdOkFn() {
            this.fPwdTrue = false;
            this.forgetPwd = false;
        },
        cancelRegFn() {
            this.regTrue = false;
        },
        // 取消忘记密码
        cancelForgetPwdFn() {
            this.fPwdTrue = false;
        },
        ...mapActions(['startReceiveOrderMsgTask'])
    }
};
</script>
<style lang="less" scoped>
.login-container {
    background: url('/images/pic/loginBg.jpg');
    background-size: cover;
    overflow: hidden;
    min-height: 768px;
}
.login-body {
    width: 100%;
    height: 100%;
    min-height: 768px;
    .login-title {
        position: relative;
        top: 12%;
        display: flex;
        justify-content: center;

        img {
            width: 221px;
            height: 140px;
        }

        .login-title-center {
            height: 100px;
            border: 1px solid #fff;
            color: white;
            margin: 10px 58px 0 58px;
        }
        .login-title-right {
            color: white;
            p {
                margin: 0px;
                padding: 0px;
            }
            .login-tl-up {
                height: 50px;
                line-height: 42px;
                font-size: 50px;
                margin-top: 14px;
                margin-bottom: 14px;
            }
            .login-tl-down {
                height: 30px;
                line-height: 32px;
                font-size: 23px;
            }
        }
    }
    .login-form {
        position: relative;
        top: 20%;
        margin: 0 auto;
        width: 415px;
        .login-forget-pwd {
            width: 100%;
            display: inline-block;
            .login-form-forgot,
            .login-form-remenber {
                color: white;
            }
            .login-form-remenber {
                float: left;
            }
            .login-form-forgot {
                float: right;
                color: #1890ff;
                line-height: 20px;
                text-decoration: underline;
            }
        }
        .login-form-button {
            border: none;
            line-height: 48px;
            color: #fff;
            font-size: 20px;
            font-family: PingFangSC-Semibold;
            width: 298px;
            height: 48px;
            border-radius: 28px;
            background: #1890ff;
            box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.15);
        }
    }
    .login-regist {
        position: relative;
        top: 21%;
        color: #fff;
        font-size: 14px;
        a {
            color: #1890ff;
            line-height: 20px;
            text-decoration: underline;
        }
    }
    .login-copyRight {
        position: absolute;
        left: 0;
        right: 0;
        bottom: 3%;
        color: #fff;
        font-size: 12px;
        font-family: PingFangSC-Regular;

        .login-tagline {
            width: 23%;
        }

        .login-copyRight-aboutUs {
            margin: 10px 0;
        }
    }
}

@media only screen and (min-height: 700px) and (max-height: 840px) {
    .login-body {
        .login-copyRight {
            .login-tagline {
                width: 14%;
            }
        }
    }
}

@media only screen and (max-width: 800px) {
    #app {
        .login-title-center,
        .login-tl-down {
            display: none;
        }

        .login-title {
            flex-direction: column;
            width: 100%;

            .login-title-left {
                height: 100px;
                line-height: 0;
            }

            .login-title-right {
                height: 100px;
                line-height: 0;

                .login-tl-up {
                    height: auto;
                    line-height: 18px;
                    font-size: 18px;
                }
            }

            img {
                width: 188px;
                height: 120px;
                margin: auto;
            }
        }

        .login-body {
            .login-copyRight {
                .login-tagline {
                    width: 48%;
                }

                .login-copyRight-aboutUs {
                    margin-top: 2%;
                }
            }
        }
    }
}

@media only screen and (min-height: 701px) and (max-height: 750px) {
    .login-body {
        .login-title {
            top: 10%;

            img {
                width: 205px;
                height: 130px;
            }
        }

        .login-regist a {
            color: #95cbfd;
        }

        .login-copyRight {
            bottom: 0;

            .login-copyRight-aboutUs {
                margin: 3px 0;
            }
        }
    }
}

@media only screen and (max-height: 700px) {
    .login-body {
        .login-form,
        .login-regist {
            top: 16%;
        }

        .login-title {
            top: 9%;

            img {
                width: 189px;
                height: 120px;
            }
        }
    }

    .login-tagline {
        display: none;
    }
}

@media only screen and (max-height: 620px) {
    .login-body {
        .login-form {
            width: auto;
        }
    }
}

@media only screen and (max-width: 750px) {
    .login-body {
        .login-form {
            width: auto;
            margin: 0 1%;
        }

        .login-tagline {
            display: block;
            margin: auto;
        }

        .login-copyRight {
            display: block;
            position: relative;
            top: 23%;
        }
    }
}
</style>
