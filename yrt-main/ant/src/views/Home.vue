<template>
    <div class="home">
        <a-locale-provider :locale="locale">
            <a-layout id="components-layout-demo-fixed-sider">
                <!--左侧导航区域开始-->
                <layout-menu class="home-layout-menu" :collapsed="collapsed" />
                <!--左侧导航区域结束-->
                <!--右侧主要展示区域开始-->
                <a-layout class="layout-content-wrap defaults" :style="{ marginLeft: `${appMenuWidth}px` }">
                    <layout-header @loginPwdValFn="loginPwdValFn" />
                    <a-layout-content
                        :style="{
                            position: 'absolute',
                            left: 0,
                            right: 0,
                            top: 0,
                            bottom: 0,
                            zIndex: 1,
                            overflowY: 'hidden',
                            margin: '51px 4px 0',
                            background: 'transparent'
                        }"
                    >
                        <router-view />
                        <!--中间主体内容展示区域-->
                    </a-layout-content>
                </a-layout>
                <!--右侧主要展示区域结束-->
            </a-layout>
        </a-locale-provider>

        <!-- 登录密码修改区域开始 -->
        <a-modal
            v-model="loginPwdInfoModal.alert"
            :width="400"
            :centered="true"
            :maskClosable="false"
            class="modal-form-input-scoped login-pwd global-drag"
        >
            <div v-globalDrag="{ el: 'login-pwd' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ loginPwdInfoModal.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="loginPwdInfoModalCancelFn">取消</a-button>
                <a-button
                    key="submit"
                    type="primary"
                    :loading="loginPwdInfoModal.loading"
                    @click="loginPwdInfoModalSaveFn"
                    >保存</a-button
                >
            </template>
            <div class="modal-form-input">
                <div class="form-top">
                    <div class="left-wrap">
                        <ul>
                            <li class="item">
                                <span class="label"><em class="em red">*</em>当前密码</span>
                                <a-input v-model="loginPwdInfoModal.old" placeholder="" class="input" type="password" />
                            </li>
                            <li class="item">
                                <span class="label"><em class="em red">*</em>登录密码</span>
                                <a-input
                                    v-model="loginPwdInfoModal.password"
                                    placeholder=""
                                    class="input"
                                    type="password"
                                />
                            </li>
                            <li class="item">
                                <span class="label"><em class="em red">*</em>再次输入登录密码</span>
                                <a-input
                                    v-model="loginPwdInfoModal.passwordAgain"
                                    placeholder=""
                                    class="input"
                                    type="password"
                                />
                            </li>
                        </ul>
                    </div>
                    <div style="text-align: center; margin-top: 15px; color: #ccc"
                        >密码为6-16位字符，需同时包含字母和数字</div
                    >
                </div>
            </div>
        </a-modal>
        <!-- 登录密码修改区域结束 -->
    </div>
</template>

<script>
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'; /* 国际化-中文 */
import LayoutMenu from '@/components/LayoutSubMenu/LayoutMenu';
import LayoutHeader from '@/components/LayoutSubMenu/LayoutHeader';
import { userChangeMyPasswordAPI } from '@/service/pageAjax';

export default {
    name: 'home',
    computed: {
        clientWidth() {
            return this.$store.state.clientWidth;
        },
        collapsed() {
            return this.$store.state.appMenuCollapsed;
        },
        appMenuWidth() {
            return this.$store.state.appMenuWidth;
        }
    },
    data() {
        return {
            locale: zhCN,
            loginPwdInfoModal: {
                title: `登录密码 - 修改`,
                alert: false,
                loading: false,
                old: '',
                password: '',
                passwordAgain: ''
            }
        };
    },
    methods: {
        // 用户修改自己的登录密码
        async userChangeMyPasswordAPIFn() {
            this.loginPwdInfoModal.loading = true;
            await userChangeMyPasswordAPI({
                old: this.loginPwdInfoModal.old,
                password: this.loginPwdInfoModal.password
            })
                .then((res) => {
                    this.loginPwdInfoModal.loading = false;
                    const { code } = res;
                    if (parseFloat(code) === 0) {
                        this.$message.success('已成功修改登录密码，请重新登录', 5);
                        this.loginPwdInfoModal = {
                            title: `登录密码 - 修改`,
                            alert: false,
                            loading: false,
                            old: '',
                            password: '',
                            passwordAgain: ''
                        };
                        this.$Utils.removeCookieFn();
                        this.$router.replace({ path: '/Login' });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.loginPwdInfoModal.loading = false;
                });
        },
        loginPwdValFn() {
            this.loginPwdInfoModal = {
                title: `登录密码 - 修改`,
                alert: true,
                loading: false,
                password: '',
                passwordAgain: ''
            };
            this.$Utils.globalDragCenterFn('login-pwd');
        },
        loginPwdInfoModalCancelFn() {
            this.loginPwdInfoModal = {
                title: `登录密码 - 修改`,
                alert: false,
                loading: false,
                password: '',
                passwordAgain: ''
            };
        },
        loginPwdInfoModalSaveFn() {
            this.loginPwdInfoModal.password = this.$Utils.trim(this.loginPwdInfoModal.password);
            this.loginPwdInfoModal.passwordAgain = this.$Utils.trim(this.loginPwdInfoModal.passwordAgain);
            const {
                old: oldPassword, // 当前密码
                password, // 登陆密码
                passwordAgain // 确认密码
            } = this.loginPwdInfoModal;
            const passwordRegExp = this.$Utils.regExpFn().password;

            if (!oldPassword) {
                this.$warning({
                    title: '操作提示',
                    content: '【当前密码】输入不能为空',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            if (!password) {
                this.$warning({
                    title: '操作提示',
                    content: '【登录密码】输入不能为空',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            if (password.length < 6 || password.length > 16 || passwordAgain.length < 6 || passwordAgain.length > 16) {
                this.$warning({
                    title: '操作提示',
                    content: '【登录密码】长度为6-16位',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            if (this.$Utils.checkInputValFn(password)) {
                this.$warning({
                    title: '操作提示',
                    content: '【登录密码】不能输入非法字符',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            if (!passwordAgain) {
                this.$warning({
                    title: '操作提示',
                    content: '【再次输入登录密码】输入不能为空',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            if (this.$Utils.checkInputValFn(passwordAgain)) {
                this.$warning({
                    title: '操作提示',
                    content: '【登录密码】不能输入非法字符',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }

            if (!passwordRegExp.test(password) || !passwordRegExp.test(passwordAgain)) {
                this.$warning({
                    title: '操作提示',
                    content: '密码中必须包含字母和数字',
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                return false;
            }

            if (password !== passwordAgain) {
                this.$warning({
                    title: '操作提示',
                    content: '两次登录密码输入不一致',
                    okText: '知道了',
                    maskClosable: false,
                    centered: true
                });
                return false;
            }
            this.userChangeMyPasswordAPIFn();
        }
    },
    watch: {},
    mounted() {
        this.debouncedFnResize = this.$XEUtils.debounce(() => {
            const width = document.documentElement.clientWidth || document.body.clientWidth;
            this.$store.commit('changeClientSize', {
                width,
                height: document.documentElement.clientHeight || document.body.clientHeight
            });
            // 根据宽度切换菜单收起状态
            if (parseFloat(width) <= 1024 && !this.collapsed) {
                this.$store.commit('changeAppMenuCollapsed', { collapsed: true });
            }
            if (parseFloat(width) > 1024 && this.collapsed) {
                this.$store.commit('changeAppMenuCollapsed', { collapsed: false });
            }
        }, 300);
        window.addEventListener('resize', this.debouncedFnResize, false);
    },
    beforeDestroy() {
        window.removeEventListener('resize', this.debouncedFnResize, false);
    },
    components: {
        LayoutMenu,
        LayoutHeader
    }
};
</script>

<style lang="less" scoped>
/* 修改浏览器默认滚动条样式 */
/*滚动条样式*/
.home-layout-menu {
    ::-webkit-scrollbar {
        width: 4px;
        height: 4px;
    }
    ::-webkit-scrollbar-thumb {
        border-radius: 5px;
        -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        background: rgba(0, 0, 0, 0.2);
    }
    ::-webkit-scrollbar-track {
        -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        border-radius: 0;
        background: rgba(0, 0, 0, 0.1);
    }
}
</style>
