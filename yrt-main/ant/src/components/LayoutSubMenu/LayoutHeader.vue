<template>
    <a-layout-header
        class="layout-header-wrap"
        :style="headerStyles"
        :class="{'and-dark' : theme ==='dark', 'and-light' : theme ==='light' }"
    >
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
                                <span>{{ theme === 'dark' ? '白色' : '深色' }}</span>
                            </template>
                            <div @click="changeTheme">
                                <!-- <img src="/images/icons/Weather/Sun.svg" style="color: #0d74c6"/> -->
                                <i class="anticon anticon-mail">
                                    <svg width="24px" height="24px" viewBox="0 0 24 24" v-if="theme === 'dark'">
                                        <g id="Stockholm-icons-/-Weather-/-Sun">
                                            <path d="M12,15 C10.3431458,15 9,13.6568542 9,12 C9,10.3431458 10.3431458,9 12,9 C13.6568542,9 15,10.3431458 15,12 C15,13.6568542 13.6568542,15 12,15 Z" id="Oval-8" fill="currentColor"></path>
                                            <path d="M19.5,10.5 L21,10.5 C21.8284271,10.5 22.5,11.1715729 22.5,12 C22.5,12.8284271 21.8284271,13.5 21,13.5 L19.5,13.5 C18.6715729,13.5 18,12.8284271 18,12 C18,11.1715729 18.6715729,10.5 19.5,10.5 Z M16.0606602,5.87132034 L17.1213203,4.81066017 C17.7071068,4.22487373 18.6568542,4.22487373 19.2426407,4.81066017 C19.8284271,5.39644661 19.8284271,6.34619408 19.2426407,6.93198052 L18.1819805,7.99264069 C17.5961941,8.57842712 16.6464466,8.57842712 16.0606602,7.99264069 C15.4748737,7.40685425 15.4748737,6.45710678 16.0606602,5.87132034 Z M16.0606602,18.1819805 C15.4748737,17.5961941 15.4748737,16.6464466 16.0606602,16.0606602 C16.6464466,15.4748737 17.5961941,15.4748737 18.1819805,16.0606602 L19.2426407,17.1213203 C19.8284271,17.7071068 19.8284271,18.6568542 19.2426407,19.2426407 C18.6568542,19.8284271 17.7071068,19.8284271 17.1213203,19.2426407 L16.0606602,18.1819805 Z M3,10.5 L4.5,10.5 C5.32842712,10.5 6,11.1715729 6,12 C6,12.8284271 5.32842712,13.5 4.5,13.5 L3,13.5 C2.17157288,13.5 1.5,12.8284271 1.5,12 C1.5,11.1715729 2.17157288,10.5 3,10.5 Z M12,1.5 C12.8284271,1.5 13.5,2.17157288 13.5,3 L13.5,4.5 C13.5,5.32842712 12.8284271,6 12,6 C11.1715729,6 10.5,5.32842712 10.5,4.5 L10.5,3 C10.5,2.17157288 11.1715729,1.5 12,1.5 Z M12,18 C12.8284271,18 13.5,18.6715729 13.5,19.5 L13.5,21 C13.5,21.8284271 12.8284271,22.5 12,22.5 C11.1715729,22.5 10.5,21.8284271 10.5,21 L10.5,19.5 C10.5,18.6715729 11.1715729,18 12,18 Z M4.81066017,4.81066017 C5.39644661,4.22487373 6.34619408,4.22487373 6.93198052,4.81066017 L7.99264069,5.87132034 C8.57842712,6.45710678 8.57842712,7.40685425 7.99264069,7.99264069 C7.40685425,8.57842712 6.45710678,8.57842712 5.87132034,7.99264069 L4.81066017,6.93198052 C4.22487373,6.34619408 4.22487373,5.39644661 4.81066017,4.81066017 Z M4.81066017,19.2426407 C4.22487373,18.6568542 4.22487373,17.7071068 4.81066017,17.1213203 L5.87132034,16.0606602 C6.45710678,15.4748737 7.40685425,15.4748737 7.99264069,16.0606602 C8.57842712,16.6464466 8.57842712,17.5961941 7.99264069,18.1819805 L6.93198052,19.2426407 C6.34619408,19.8284271 5.39644661,19.8284271 4.81066017,19.2426407 Z" fill="currentColor" opacity="0.7"></path>
                                        </g>
                                    </svg>
                                    <svg width="24px" height="24px" viewBox="0 0 24 24" v-if="theme === 'light'">
                                        <g id="Stockholm-icons-/-Weather-/-Moon">
                                            <path d="M12.0700837,4.0003006 C11.3895108,5.17692613 11,6.54297551 11,8 C11,12.3948932 14.5439081,15.9620623 18.9299163,15.9996994 C17.5467214,18.3910707 14.9612535,20 12,20 C7.581722,20 4,16.418278 4,12 C4,7.581722 7.581722,4 12,4 C12.0233848,4 12.0467462,4.00010034 12.0700837,4.0003006 Z" id="Combined-Shape" fill="currentColor"></path>
                                        </g>
                                    </svg>
                                </i>
                            </div>
                        </a-tooltip>
                    </li>
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
                this.$store.commit('stopReceiveOrderMsgTask');
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
        },
        changeTheme() {
            this.$store.commit('changeTheme');
        }
    },
    computed: {
        theme() {
            return this.$store.state.theme;
        },
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
                background: '#1a1a27',
                color: '#3699FF',
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
        font-size: 20px;

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
                font-size: 20px;
            }

            &:hover,
            &:hover .yrt,
            &:hover .anticon {
                color: #fff !important;
                background: #0d74c6;
            }
        }
    }
    &.and-light {
        background: #fff !important;
    }
}
</style>
