<template>
    <a-layout-header class="layout-header-wrap" :style="headerStyles">
        <a-row>
            <a-col :span="12" class="caption">
                <a-icon class="trigger" :type="collapsed ? 'menu-unfold' : 'menu-fold'" @click="toggleCollapsed" />
                <p>{{ orgName }}</p>
            </a-col>
            <a-col :span="12" :offset="12" class="nav-right-wrap">
                <ul>
                    <li class="item">
                        <a-tooltip placement="bottom">
                            <template slot="title">
                                <span>该功能在开发中，敬请期待</span>
                            </template>
                            <div>
                                <a-icon type="mail" />
                            </div>
                        </a-tooltip>
                    </li>
                    <li class="item">
                        <a-tooltip placement="bottom">
                            <template slot="title">
                                <span>点击访问视频教程</span>
                            </template>
                            <div>
                                <a href="https://v.youku.com/v_show/id_XNDQxMjUxNzY0NA==.html" target="_blank"
                                    ><a-icon type="question-circle"
                                /></a>
                            </div>
                        </a-tooltip>
                    </li>
                    <li class="item">
                        <a-dropdown placement="bottomCenter">
                            <a-tooltip placement="right">
                                <!-- <template slot="title">
                                    <span>修改账户信息</span>
                                </template> -->
                                <div>
                                    <a-icon type="user" />
                                </div>
                            </a-tooltip>
                            <a-menu slot="overlay">
                                <a-menu-item @click="editGroupFn('login-pwd')"> 登录密码 </a-menu-item>
                                <a-menu-item @click="editGroupFn('user-pic')" disabled> 用户头像 </a-menu-item>
                                <a-menu-item @click="editGroupFn('user-name')" disabled> 用户名 </a-menu-item>
                            </a-menu>
                        </a-dropdown>
                    </li>
                    <li class="item" @click="logOut">
                        <a-tooltip placement="bottom">
                            <template slot="title">
                                <span>退出</span>
                            </template>
                            <div>
                                <a-icon type="poweroff" />
                            </div>
                        </a-tooltip>
                    </li>
                </ul>
            </a-col>
        </a-row>
    </a-layout-header>
</template>

<script>
import { appMenuCollapsedWidth, appMenuWidth } from '@/config/common';
import { logoutAPI } from '../../service/pageAjax';
export default {
    name: 'LayoutHeader',
    methods: {
        toggleCollapsed() {
            if (this.collapsed) {
                // 展开
                this.$store.commit('changeAppMenuCollapsed', {
                    collapsed: false
                });
                this.$store.commit('changeAppMenuSize', {
                    width: appMenuWidth
                });
            } else {
                // 收起
                this.$store.commit('changeAppMenuCollapsed', {
                    collapsed: true
                });
                this.$store.commit('changeAppMenuSize', {
                    width: appMenuCollapsedWidth
                });
            }
        },
        logOut: async function () {
            try {
                await logoutAPI();
                this.$store.dispatch('stopReceiveOrderMsgTask');
                this.$store.commit('changeStateInfo', { info: null });
                this.$store.commit('setLastLoginUser', '');
                this.$notification.destroy();
                this.$Utils.removeCookieFn();
                this.$router.replace({ path: '/Login' });
            } catch (error) {
                console.error(error);
            }
        },
        // 顶部的修改按钮区域
        editGroupFn(btnName) {
            switch (btnName) {
                case 'login-pwd': // 登录密码
                    this.$emit('loginPwdValFn', true);
                    break;
                case 'user-pic': // 用户头像
                    break;
                case 'user-name': // 用户名
                    break;
            }
        }
    },
    computed: {
        collapsed() {
            return this.$store.state.appMenuCollapsed;
        },
        appHeaderHeight() {
            return this.$store.state.appHeaderHeight;
        },
        appMenuWidth() {
            return this.$store.state.appMenuWidth;
        },
        clientWidth() {
            return this.$store.state.clientWidth;
        },
        orgName() {
            const EmployeeInfo = this.$cookie.get('EmployeeInfo'),
                orgNameNo = '医疗耗材供应链管理系统';
            return EmployeeInfo ? JSON.parse(EmployeeInfo).OrgName || orgNameNo : orgNameNo;
        },
        headerStyles() {
            return {
                height: `${this.appHeaderHeight}px`,
                lineHeight: `${this.appHeaderHeight}px`,
                background: '#fff',
                padding: 0,
                position: 'fixed',
                top: 0,
                right: 0,
                left: `${this.appMenuWidth}px`,
                zIndex: 2
            };
        }
    }
};
</script>

<style lang="less">
@h: 48px;
@607: #60768d;

/* 中间顶部区域 */
.layout-header-wrap {
    color: #0d74c6;
    box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.15);

    .caption {
        display: flex;
        flex-direction: row;
        justify-content: flex-start;
        align-items: center;
        font-size: 24px;

        .trigger {
            display: flex;
            align-items: center;
            height: @h;
            line-height: @h;
            padding: 0 20px;
            vertical-align: middle;
            transition: all 0.3s ease;
            cursor: pointer;

            &:hover {
                color: #fff;
                background: #0d74c6;
            }
        }

        p {
            margin-bottom: 0;
            text-overflow: ellipsis;
            white-space: nowrap;
            overflow: hidden;
        }
    }

    .nav-right-wrap {
        height: @h;
        line-height: @h;
        margin-left: 0;

        ul {
            display: flex;
            flex-direction: row;
            justify-content: flex-end;
            height: 100%;
            line-height: 100%;
            margin: 0;
            padding: 0 10px 0 0;
        }

        .item {
            width: 50px;
            cursor: pointer;

            .yrt {
                padding-right: 0;
                color: #0d74c6;
            }

            div {
                display: flex;
                align-items: center;
                justify-content: center;
                height: 100%;
            }

            .anticon {
                font-size: 21px;
            }

            &:hover,
            &:hover .yrt,
            &:hover .anticon {
                color: #fff !important;
                background: #0d74c6;
            }
        }
    }
}
</style>
