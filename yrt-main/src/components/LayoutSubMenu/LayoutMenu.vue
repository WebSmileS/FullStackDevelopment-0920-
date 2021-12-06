<template>
    <a-layout-sider
        class="layout-menu-left-wrap"
        :class="{ 'slider-menu-skeleton': loadingSkeletonShow }"
        :trigger="null"
        collapsible
        v-model="collapsed"
        theme="light"
        :style="siderStyles"
    >
        <!-- logo区域 -->
        <div class="sider-pic">
            <h1 class="logo">
                <img
                    src="/images/logo/main-logo.png"
                    alt=""
                    class="logo-right"
                    :style="{ 'height': collapsed ? '15px' : '32px' }"
                />
            </h1>
            <ul class="user-pic" v-if="!loadingSkeletonShow">
                <li>
                    <img
                        class="defaults-img"
                        :src="
                            employeeInfo.Photo_url
                                ? '/v1/image/get/' + employeeInfo.Photo_url + '-200'
                                : '/images/logo/logo-large.jpg'
                        "
                        alt=""
                    />
                </li>
                <li v-show="!collapsed">
                    <span>{{ employeeInfo.Name || '-' }}</span>
                </li>
                <li v-show="!collapsed">
                    <span>{{ employeeInfo.Position || '-' }}</span>
                </li>
            </ul>
        </div>

        <!-- 左侧导航列表区域 -->
        <div class="slider-menu">
            <!-- 导航骨架屏应用 -->
            <layout-menu-skeleton
                v-if="loadingSkeletonShow"
                :collapsed="collapsed"
                :loadingSkeletonShow="loadingSkeletonShow"
            ></layout-menu-skeleton>
            <!-- 导航菜单DOM-TREE -->
            <parent-tree v-else :collapsed="collapsed"> </parent-tree>
        </div>
    </a-layout-sider>
</template>

<script>
import { appMenuWidth, appMenuCollapsedWidth } from '@/config/common';
import ParentTree from '@/components/LayoutSubMenu/ParentTree';
import LayoutMenuSkeleton from '@/components/LayoutSubMenu/LayoutMenuSkeleton';
import { userUserInfoAPI, menuUserMenuAPI } from '@/service/pageAjax';

export default {
    name: 'LayoutMenu',
    data() {
        return {
            loadingSkeletonShow: false, // 是否显示骨架屏的标识
            infoObj: {
                Photo_url: '',
                Name: '',
                Position: ''
            }
        };
    },
    methods: {
        // <当前>获取用户信息
        async userUserInfoAPIFn() {
            await userUserInfoAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const { EmployeeInfo, UserInfo, warehouseList } = res.data;
                    this.infoObj.Name = EmployeeInfo.Name || UserInfo.UserName;
                    this.infoObj.Photo_url = EmployeeInfo.Photo_url;
                    this.infoObj.Position = EmployeeInfo.Position;
                    this.$store.commit('changeStateInfo', {
                        info: {
                            belong: UserInfo.Belong,
                            systemType: UserInfo.System_type,
                            passwordCycle: UserInfo.Password_cycle,
                            userInnerSn: UserInfo.User_inner_sn
                        },
                        isLogin: res.isLogin
                    });
                    this.$store.commit('setIsNewUserInfoFn', '');
                    this.$store.commit('setLastLoginUser', `${UserInfo.User_inner_sn}-${UserInfo.System_type}`);
                    this.$store.commit('setWarehouseList', warehouseList);
                    localStorage.setItem('userInfo', JSON.stringify(res.data));
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 登录成功后获取【全部左侧导航菜单列表】
        async menuUserMenuAPIFn() {
            // 机构类型: 0-平台 1-医院 2-经销商 3-厂商
            // let systemType = parseFloat(this.$cookie.get('userSystemType'))
            // let terminalName = systemType === 0 ? 'ptmweb' : 'yrtweb' // ptmweb -> system_type:0   yrtweb -> system_type:[1 3]
            let terminalName = 'yrtweb';
            await menuUserMenuAPI(terminalName)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.$store.commit('changeUserMenuData', res.data);
                        this.loadingSkeletonShow = false;
                    } else {
                        this.$message.error(res.msg);
                        this.loadingSkeletonShow = false;
                    }
                })
                .catch(() => {
                    this.loadingSkeletonShow = false;
                });
        }
    },
    components: {
        ParentTree,
        LayoutMenuSkeleton
    },
    computed: {
        collapsed() {
            return this.$store.state.appMenuCollapsed;
        },
        appMenuWidth() {
            return this.$store.state.appMenuWidth;
        },
        siderStyles() {
            return {
                height: '100vh',
                position: 'fixed',
                left: 0,
                maxWidth: `${appMenuWidth}px`,
                minWidth: `${appMenuCollapsedWidth}px`,
                width: `${this.appMenuWidth}px`
            };
        },
        // 动态获取顶部的用户头像 + 用户姓名 + 用户职位的信息
        employeeInfo() {
            if (this.$store.state.isNewUserInfo) {
                this.userUserInfoAPIFn();
            }
            return this.infoObj;
        }
    },
    created() {
        this.loadingSkeletonShow = true;

        // 解决未分配权限导致读取缓存的菜单数据
        this.$store.commit('changeUserMenuData', []);

        this.userUserInfoAPIFn();
        this.menuUserMenuAPIFn();
    }
};
</script>

<style lang="less">
@h: 48px;
@white: #fff;
@607: #60768d;

/* 左侧导航区域 */
.layout-menu-left-wrap {
    .sider-pic {
        .logo {
            display: flex;
            align-items: center;
            justify-content: center;
            height: @h;
            background: #0d74c6;
            margin-bottom: 0;
            overflow: hidden;
            .logo-left {
                height: 36px;
            }
            .logo-right {
                height: 32px;
                margin: 4px 16px 0 9px;
            }
        }

        /* 用户头像 */

        .user-pic {
            display: flex;
            flex-direction: row;
            align-items: center;
            text-align: left;
            padding: 14px;
            margin-bottom: 0;
            background: #f8fafb;

            li {
                color: @607;

                &:nth-of-type(1) {
                    width: 48px;
                    height: 48px;
                    border-radius: 50%;
                    overflow: hidden;

                    img {
                        display: block;
                        width: 100%;
                        height: 100%;
                    }
                }

                &:nth-of-type(2) {
                    padding: 0 10px 0 18px;
                    font-size: 16px;
                    font-weight: 600;
                }

                &:nth-of-type(3) {
                    font-size: 12px;
                }
            }
        }
    }

    /* 导航列表区域 */
    .slider-menu {
        position: fixed;
        top: 124px;
        left: 0;
        bottom: 0;
        width: 240px;
        overflow: auto;
    }

    /* 导航列表区域 -- 给骨架屏挪位置 */
    &.slider-menu-skeleton {
        .slider-menu {
            top: 48px;
        }
    }

    /* 默认样式替换 01 */
    .ant-menu .yrt {
        color: #1890ff;
    }

    .ant-menu-inline,
    .ant-menu-vertical,
    .ant-menu-inline,
    .ant-menu-vertical-left {
        border-right: none;
    }

    /* 默认样式替换 02 */
    .ant-menu-inline {
        .ant-menu-item:not(:last-child) {
            margin: 0;
        }

        .ant-menu-item,
        .ant-menu-submenu-title {
            width: auto;
            text-align: left;
            margin: 0;
        }

        .ant-menu-item {
            padding-right: 34px;

            &:after {
                display: none;
            }

            &:before {
                &:extend(.ant-menu-item:after);
                content: '';
                position: absolute;
                left: 0;
                top: 0;
                bottom: 0;
                border-left: 3px solid #006dc3;
                transform: scaleY(0.0001);
            }
        }

        .ant-menu-item-selected {
            &:after {
                display: none;
            }

            &:before {
                transform: scaleY(1);
            }
        }

        .ant-menu-submenu-title {
            color: #595959;

            &:hover {
                color: #333;

                span span {
                    font-weight: 600;
                }
            }
        }
    }

    .ant-menu-vertical {
        .ant-menu-item:not(:last-child) {
            margin: 0;
        }
    }
}
</style>
