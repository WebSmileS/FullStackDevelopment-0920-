<template>
    <div id="app" v-cloak>
        <a-modal
            v-model="reLogin"
            :maskClosable="false"
            :width="328"
            :centered="true"
            :footer="null"
            @cancel="cancelReLogin()"
            class="re-login-modal modal-form-input-scoped global-drag"
        >
            <div v-globalDrag="{ el: 're-login-modal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ reLoginTitle }}
                </h6>
            </div>
            <div class="re-login">
                <a-form id="components-form-demo-normal-login" :form="form" class="login-form" @submit="handleSubmit">
                    <a-form-item
                        label="账号"
                        :label-col="{ span: 4 }"
                        :wrapper-col="{ span: 19 }"
                        style="padding: 0 24px"
                    >
                        <a-input
                            v-decorator="[
                                'userName',
                                { rules: [{ required: true, message: '请输入用户名/手机号！' }] }
                            ]"
                            placeholder="用户名/手机号码"
                            style="font-size: 20px"
                        >
                            <a-icon slot="prefix" type="user" style="color: rgba(0, 0, 0, 1)" />
                        </a-input>
                    </a-form-item>
                    <a-form-item
                        label="密码"
                        :label-col="{ span: 4 }"
                        :wrapper-col="{ span: 19 }"
                        style="padding: 0 24px"
                    >
                        <a-input
                            v-decorator="['password', { rules: [{ required: true, message: '请输入密码！' }] }]"
                            type="password"
                            placeholder="密码"
                        >
                            <a-icon slot="prefix" type="lock" style="color: rgba(0, 0, 0, 1)" />
                        </a-input>
                    </a-form-item>
                    <a-form-item style="background: #e3e6e9; padding: 6px 24px; text-align: right">
                        <a-button @click="cancelReLogin()" html-type="button" class="login-form-button">
                            取消
                        </a-button>
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
        </a-modal>
        <router-view v-if="isRouterAlive" />
    </div>
</template>

<script>
import { loginAPI } from '@/service/pageAjax';
import { mapActions, mapState } from 'vuex';
export default {
    name: 'App',
    data() {
        return {
            reLoginTitle: '登录',
            loadingSubmitFlag: false,
            isRouterAlive: true
        };
    },
    beforeCreate() {
        this.form = this.$form.createForm(this);

        /// 判断当前可视高度为平板 0-默认的pc  1-平板的高度
        localStorage.setItem(
            'isIpadHeight',
            (document.body.clientHeight || document.documentElement.clientHeight) > 680 ? 0 : 1
        );
        localStorage.setItem(
            'QiniuUrl',
            document.location.protocol === 'http:' ? 'http://upload-z2.qiniup.com' : 'https://upload-z2.qiniup.com'
        );
    },
    created() {
        if (!location.hash.includes('/Login')) {
            this.startReceiveOrderMsgTask();
        }
    },
    methods: {
        // 重新登陆
        async reLoginFn() {
            let params = {
                userName: this.$Utils.trim(this.form.getFieldValue('userName')),
                password: this.$Utils.trim(this.form.getFieldValue('password'))
            };
            await loginAPI(params)
                .then((res) => {
                    if (res.code === 0) {
                        res.isLogin = '';
                        window.userCookieInfo = res.data;

                        /**
                         * 上一个登陆用户和当前用户进行对比，看是否是同一用户，如果为同一用户，则消息推送的notification保留，否则清空
                         */
                        if (
                            this.lastLoginUser !== `${res.data.UserInfo.User_inner_sn}-${res.data.UserInfo.System_type}`
                        ) {
                            this.$notification.destroy();
                        }

                        this.$cookie.set('loginDatas', JSON.stringify(res.data)); // 登陆后新的返回数据字符串，使用时需要JSON格式化
                        this.$cookie.set('loginStatus', 'true'); // 登陆状态
                        this.$cookie.set('userbelong', res.data.UserInfo.Belong); // 用户归属机构（机构ID）
                        this.$cookie.set('userSystemType', res.data.UserInfo.System_type); // 用户机构类型
                        this.$cookie.set('userPasswordCycle', res.data.UserInfo.Password_cycle); // 用户密码过期事件
                        this.$cookie.set('userInnerSn', res.data.UserInfo.User_inner_sn); // 用户登陆ID
                        this.$cookie.set('EmployeeInfo', JSON.stringify(res.data.EmployeeInfo)); // 人员信息
                        this.$cookie.set('UserInfo', JSON.stringify(res.data.UserInfo)); // 用户账号信息
                        this.$store.commit('changeStateInfo', res);
                        this.$store.commit(
                            'setLastLoginUser',
                            `${res.data.UserInfo.User_inner_sn}-${res.data.UserInfo.System_type}`
                        );
                        this.startReceiveOrderMsgTask();
                        this.reload();
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.loadingSubmitFlag = false;
                })
                .catch(() => {
                    this.$message.error('登陆失败！');
                    this.loadingSubmitFlag = false;
                });
        },
        reload() {
            this.isRouterAlive = false;
            this.$nextTick(function () {
                this.isRouterAlive = true;
            });
        },
        handleSubmit(e) {
            e.preventDefault();
            this.form.validateFields((err) => {
                if (!err) {
                    this.loadingSubmitFlag = true;
                    this.reLoginFn();
                }
            });
        },
        cancelReLogin() {
            this.clearStateVuexFn();
            this.$Utils.removeCookieFn();
            this.$router.replace({ path: '/Login' });
        },
        clearStateVuexFn() {
            // 清空vuex - state
            this.$store.commit('changeStateInfo', {
                info: {},
                isLogin: ''
            });
        },
        ...mapActions(['startReceiveOrderMsgTask', 'stopReceiveOrderMsgTask'])
    },
    computed: {
        reLogin() {
            let flag = false,
                storeState = this.$store.state;
            if (storeState.isLogin && !location.hash.includes('/Login')) {
                flag = true;
                this.$Utils.globalDragCenterFn('re-login-modal');
            } else {
                flag = false;
            }
            return flag;
        },
        ...mapState(['lastLoginUser'])
    },
    watch: {
        $route: {
            handler(to) {
                this.$store.commit('setMenuItemURIFn', to.path);
            },
            deep: true
        },
        '$store.state.isLogin': function (currentLoginStatus) {
            if (currentLoginStatus) {
                this.stopReceiveOrderMsgTask();
            }
        }
    }
};
</script>

<style lang="less">
// antd的Drawer组件存在bug，收起后不能完全清除组件自身所添加至body元素上的内联样式
// 其中width属性的遗留影响较大，故手动重置
body {
    width: 100% !important;
}

.ant-modal-wrap {
    z-index: 3 !important;
}
.ant-modal-mask {
    z-index: 3 !important;
}
[v-cloak] {
    display: none !important;
}
/* 统一浏览器默认样式 */
ul,
ol,
li {
    list-style: none;
}

/* 字体图标开始 */
.yrt,
.yrt-operation-path-tree-icon {
    font-family: 'yrt' !important;
    font-style: normal;
    font-size: 16px;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
}
.yrt {
    color: #778ca2;
    padding-right: 16px;
}
/* 字体图标结束 */

/* 左侧导航的局部样式微调 */
.ant-menu-submenu-placement-rightTop,
.ant-tooltip-placement-right {
    .ant-menu-submenu-content,
    .ant-tooltip-content {
        .ant-menu-item,
        .ant-tooltip-inner,
        .ant-menu-item-selected {
            display: flex;
            align-items: center;

            &:hover {
                span,
                .yrt {
                    color: #1890ff;
                }
            }
        }
    }
}
.ant-menu-submenu-placement-rightTop {
    .ant-menu-submenu-content {
        .ant-menu-item-selected {
            span,
            .yrt {
                color: #1890ff;
            }
        }
    }
}
.ant-tooltip-placement-right {
    .ant-tooltip-content {
        .ant-tooltip-inner {
            span,
            .yrt {
                color: @white;
            }
        }
    }
}
.layout-menu-left-wrap {
    .ant-menu-inline,
    .ant-menu-vertical {
        .ant-menu-item {
            display: flex;
            align-items: center;
            padding-right: 34px;
        }

        .ant-menu-submenu-title span,
        .ant-tooltip-open,
        .ant-menu-item {
            display: flex;
            align-items: center;
        }
    }

    .ant-menu-item-active:hover span {
        color: #333;
        font-weight: 600;
    }

    .ant-menu-item-selected:hover span {
        color: @white;
    }

    .ant-menu-vertical {
        .yrt {
            padding-right: 26px;
        }
    }
}

.ant-menu-vertical > .ant-menu-item,
.ant-menu-submenu-vertical {
    .yrt {
        color: #448ef7;
    }

    span span {
        color: #595959;
    }
}

.ant-menu-item:hover,
.ant-menu-item-active,
.ant-menu:not(.ant-menu-inline) .ant-menu-submenu-open,
.ant-menu-submenu-active,
.ant-menu-item-selected,
.ant-menu-submenu-title:hover {
    color: #595959;
}

.ant-menu {
    .ant-menu-item:hover {
        span {
            color: #595959;
        }
    }
}

.ant-menu-submenu-placement-rightTop .ant-menu-submenu-content .ant-menu-item:hover span {
    color: #333;
    font-weight: 600;
}

.ant-menu:not(.ant-menu-horizontal) {
    .ant-menu-item-selected,
    .ant-menu-item-selected:hover {
        font-size: 14px;
        background: #3699FF;

        span span,
        span {
            color: @white;
            font-weight: 600;
        }

        .yrt {
            color: @white;
            font-weight: normal;
        }
    }
}

.ant-menu-vertical > .ant-menu-item:hover,
.ant-menu-item:hover,
.ant-menu-submenu-title:hover {
    background: #f0f2f5;
}

.ant-table-fixed-header .ant-table-body-inner {
    overflow: auto !important;
}

// 登陆主体部分
.login-container {
    height: 100%;
    width: 100%;
    .ant-input-affix-wrapper {
        .ant-input {
            border-radius: 44px;
            height: 48px;
            line-height: 48px;
            /*font-size: 20px !important;*/
            &:not(:first-child) {
                /*font-size: 20px !important;*/
                padding-left: 44px;
                padding-right: 30px;
            }
        }
        .ant-input-prefix {
            font-size: 21px;
            color: #0e4799;
        }
    }
}
// 登陆-注册模态框
@keyframes loginUp {
    from {
        top: 100%;
    }
    to {
        top: 50%;
        transform: translateY(-50%);
    }
}
@keyframes loginDown {
    from {
        top: 50%;
        display: block;
        transform: translateY(-50%);
    }
    to {
        top: 100%;
        display: none;
    }
}
.modal-regist {
    width: 100%;
    border-radius: 8px;
    position: fixed;
    animation: loginDown 0.2s ease;
    animation-iteration-count: 1;
}
.regist-show {
    width: 100%;
    border-radius: 8px;
    position: fixed;
    top: 50%;
    transform: translateY(-50%);
    z-index: 3;
    animation: loginUp 0.2s ease;
    animation-iteration-count: 1;
}
// app主体
#app {
    font-family: 'Microsoft YaHei', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    height: 100%;
    width: 100%;

    .layout-content-wrap {
        position: absolute;
        left: 0;
        right: 0;
        top: 0;
        bottom: 0;
    }

    /* 默认图片背景颜色设置 */
    .defaults-img {
        background: rgba(0, 0, 0, 0.2);
    }

    /* 隐藏 */
    .hide {
        display: none;
    }

    /* 边距样式 */
    .mgr-10 {
        margin-right: 10px;
    }
    .mgr-15 {
        margin-right: 15px;
    }
    .mgr-20 {
        margin-right: 20px;
    }
    .mgl-10 {
        margin-left: 10px;
    }
    .mgl-15 {
        margin-left: 15px;
    }
    .mgl-20 {
        margin-left: 20px;
    }
    .mgb-10 {
        margin-bottom: 10px;
    }

    .pdl-10 {
        padding-left: 10px;
    }
    .pdl-15 {
        padding-left: 10px;
    }
    .pdl-20 {
        padding-left: 10px;
    }
    .pdr-10 {
        padding-right: 10px;
    }
    .pdr-15 {
        padding-right: 10px;
    }
    .pdr-20 {
        padding-right: 10px;
    }

    /* 位置 */
    .align-c {
        text-align: center;
    }
    .align-l {
        text-align: left;
    }
    .align-r {
        text-align: right;
    }

    /* 暂无数据 */
    .no-data-box {
        color: #b2b2b2;
        font-size: 26px;
        text-align: center;
        i {
            font-size: 48px;
        }
    }
    /* 表格行选中 */
    .ant-table-body .ant-table-row.active {
        background: #dbf2ff !important;
    }
    .ant-table-body .ant-table-row.active:hover td {
        color: rgba(0, 0, 0, 0.65);
    }
    .ant-table-body tr.ant-table-row-level-0 td {
        padding: 9px 16px !important;
    }
    .print-content .ant-table-body tr.ant-table-row-level-0 td {
        padding: 9px 4px !important;
    }
    /* 按钮公用样式 */
    .btn-nom {
        // 普通按钮
        height: 24px;
        min-width: 76px;
        &.cut {
            margin-right: 18px;
        }
        &.cut:after {
            content: '';
            position: absolute;
            right: -11px;
            top: 21%;
            width: 1px;
            height: 13px;
            background: #dfe0df;
        }
    }
    .btn-input {
        height: 24px;
        min-width: 48px;
        margin-right: -12px;
        border-radius: 0 5px 5px 0;
    }
    /* 高级搜索 */
    .adv-search {
        display: inline-block;
        color: #afafaf;
        i {
            font-size: 22px;
            vertical-align: bottom;
        }
        i:hover {
            color: #1890ff;
        }
    }
    .high-search-box {
        padding-top: 14px;
        margin-left: -12px;
        .col-list {
            padding-bottom: 0px;
        }
        .ant-select-selection--single {
            height: 24px !important;
        }
        .ant-select-selection-selected-value {
            line-height: 24px;
        }
        .form {
            display: flex;
            flex-direction: row;
            margin-bottom: 0;
            padding-left: 0;
            li {
                &:extend(.center);
                justify-content: flex-start !important;
                width: 46%;
                &:last-child {
                    width: 16%;
                    .ant-btn {
                        min-width: 76px;
                        margin-left: 20px;
                    }
                }
                input {
                    width: 74%;
                    height: 24px;
                }
                .ant-cascader-input {
                    width: 100%;
                }
                .ant-cascader-picker {
                    width: 80%;
                }
                .ant-select {
                    width: 70%;
                    height: 24px;
                }
            }
            .label {
                text-align: right;
                display: flex;
                min-width: 5%;
                margin-right: 10px;
                display: inline-block;
            }
            .ant-calendar-picker {
                width: 70%;
                height: 24px;
                .ant-input {
                    height: 100%;
                    line-height: 100%;
                }
            }
        }
    }
    /* 图片上传 样式1 */
    .info-count {
        .ant-upload-list-picture-card {
            .ant-upload-list-item {
                width: 280px;
                height: 280px;
            }
        }
        .ant-upload.ant-upload-select-picture-card {
            width: 280px;
            height: 280px;
        }
        .ant-upload.ant-upload-select-picture-card > .ant-upload {
            font-size: 60px;
            color: #afafaf;
        }
        .ant-upload.ant-upload-select-picture-card > .ant-upload:hover {
            color: #1890ff;
        }
        .ant-upload-list-picture-card .ant-upload-list-item-actions .anticon-eye-o {
            font-size: 32px;
            width: 32px;
        }
        .ant-upload-list-picture-card .ant-upload-list-item-actions .anticon-delete {
            font-size: 30px;
            width: 32px;
        }
        .ant-upload-list-item-info > span {
            height: 100%;
        }
    }
}
/* form 表单 */
.ant-form-item-label,
.ant-select-selection__rendered {
    line-height: 24px;
}

.ant-select,
.ant-select-selection--single,
.ant-select-selection--multiple {
    display: flex;
    flex: 1;
    overflow: hidden;
}

.ant-select-selection--single {
    height: 24px;
}

/* 路由导航切换显示 */
#nav {
    padding: 30px;

    a {
        font-weight: bold;
        color: #2c3e50;

        &.router-link-exact-active {
            color: #42b983;
        }
    }
}

/* 注册审核 + 68分类 + 自定义类型的公共样式 */
@h: 24px;
@w: 16px;
@white: #fff;
@red: #f00;
@green: #0aaf55;
@orange: #fa8c16;
@gray: #aaaaaa;

/* 水平垂直居中 */
.center {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
}

/* 颜色定义 */
.blue {
    color: #448ef7 !important;
}

.red {
    color: @red!important;
}

.green {
    color: @green!important;
}

.orange {
    color: @orange!important;
}

.gray {
    color: @gray!important;
}

.bold {
    font-weight: 600;
}

.total-bg {
    background: @red;
    color: @white;
}

.ant-table-fixed-header > .ant-table-content > .ant-table-scroll > .ant-table-body {
    overflow-y: auto !important;
}
.only_show .ant-upload-list-item .anticon-close {
    display: none !important;
}
.scoped {
    .ant-table-tbody > tr.active-tr {
        background: #dbf2ff;
    }
    .ant-table-tbody > tr > td {
        padding: 9px 16px !important;
    }
    &.modal-form-input-scoped-td {
        .ant-table-tbody > tr > td {
            padding: 4px 16px !important;
        }
    }
    .tab-table-wrap {
        position: relative;
        .top-add-product {
            position: absolute;
            top: -23px;
            z-index: 5;
        }

        .ant-cascader-input.ant-input {
            height: 24px !important;
        }
        .ant-input.input {
            height: 24px !important;
        }
        .ant-input-number.input {
            height: 24px !important;
        }
        .ant-input-number-input {
            height: 23px;
        }
        .ant-input-number {
            line-height: 0.5 !important;
        }
    }
    .search-form-wrap {
        min-height: 48px;
        padding: 17px;
        background: @white;
        border-radius: 4px;

        .hight-search-wrap {
            padding-top: 37px;

            .form {
                display: flex;
                flex-direction: row;
                margin-bottom: 0;
                padding-left: 0;

                li {
                    &:extend(.center);
                    justify-content: flex-start;
                    width: 30%;
                    padding-right: 48px;

                    .ant-input,
                    .ant-cascader-picker {
                        display: flex;
                        flex: 1;
                        height: 24px;
                        text-align: left;
                    }

                    &:last-child {
                        width: 10%;
                        padding-right: 0;

                        .ant-btn {
                            min-width: 76px;
                            height: 24px;
                        }
                    }
                }

                .label {
                    display: flex;
                    padding-right: 16px;
                }

                .ant-calendar-picker-wrap {
                    width: 42%;
                    height: 24px;

                    .ant-input,
                    .ant-calendar-picker {
                        display: flex;
                        flex: 1;
                        height: 100%;
                        line-height: 100%;
                    }
                }
            }

            & {
                .form-wrap {
                    display: flex;

                    .form-label {
                        padding-right: 14px;
                    }

                    .form-input {
                        display: flex;
                        flex: 1;
                    }
                }
            }
        }
    }
    .ant-table-header {
        overflow-y: auto !important;
    }
    .ant-table-selection {
        text-align: left !important;
    }

    .btn-top-wrap {
        &:extend(.center);

        .btn-list-wrap,
        .input-search-wrap,
        .hight-search-text {
            display: flex;
            flex-direction: row;
        }

        .btn-list-wrap {
            margin-bottom: 0;
            padding-left: 0;

            li {
                position: relative;
                margin-right: @w;

                &::before {
                    content: '';
                    position: absolute;
                    right: 0;
                    top: 50%;
                    width: 1px;
                    height: 13px;
                    transform: translateY(-50%);
                    background: #dfe0df;
                }

                .ant-btn {
                    min-width: 76px;
                    height: 24px;
                    margin-right: @w;
                }

                &:last-child {
                    margin-right: 0;
                    &::before {
                        display: none;
                    }

                    .ant-btn:last-child {
                        margin-right: 0;
                    }
                }
            }
        }

        .input-search-wrap {
            justify-content: flex-end;

            .search {
                width: 70%;
                height: @h;
                margin-right: 23px;
            }

            .hight-search-text {
                font-size: 14px;
                color: rgba(0, 0, 0, 0.65);
                opacity: 0.44;

                span {
                    padding-right: @w;
                }
                &:extend(.center);

                .anticon {
                    font-size: 24px;
                }

                &:hover {
                    color: #1890ff;
                    opacity: 1;
                    cursor: pointer;
                }
            }
        }
    }

    /* tab切换 + 表格区域 */
    .ant-table-fixed-right {
        bottom: 0;
        z-index: 9;
        table {
            width: 100%;
        }
    }
    &.scoped-white {
        position: absolute;
        left: 0;
        right: 0;
        top: 0;
        bottom: 0;
        .ant-tabs-card {
            height: 100%;
        }
        .tab-table-wrap {
            position: absolute;
            left: 0;
            right: 0;
            top: 58px;
            bottom: 0;

            &.top136 {
                top: 118px;
            }
            &.top139 {
                top: 119px;
            }
            &.top56 {
                top: 58px;
            }
            .ant-tabs-card-content {
                height: calc(100% - 40px);
                overflow-y: auto;
            }

            /* 组织架构页面的滚动条控制01 */
            &.cur-overflow-y {
                .ant-tabs-card-content {
                    overflow-y: auto;
                }
            }

            /* 组织架构页面的滚动条控制02 */
            &.cur-overflow-hidden {
                .ant-tabs-card-content {
                    overflow-y: hidden;
                }
            }
        }

        .table-pd-bg {
            height: 100%;
            border: 1px solid #e8e8e8;
            background: @white;
            /*overflow-y: scroll;*/
        }
    }

    .tab-table-wrap {
        margin-top: 4px;
        &.no_left {
            .ant-table-body {
                tr {
                    td {
                        &:nth-of-type(1) {
                            //
                            text-align: center !important;
                        }
                    }
                }
            }
        }
        &.tab-table-overflowy {
            // 68分类 、自定义类型的滚动条控制
            .ant-table-fixed-header .ant-table-scroll {
                .ant-table-header {
                    height: 59px;
                    overflow: auto !important;
                }
                .ant-table-body {
                    overflow-y: auto !important;
                }
            }
        }

        .ant-tabs-nav-scroll {
            display: flex;

            .ant-tabs-nav {
                padding-left: 10px;
            }
        }

        .ant-tabs-bar {
            margin-bottom: 0;
        }

        .ant-tabs-card-content {
            border: 1px solid #e8e8e8;
            border-top: none;
            background: @white;
        }

        // 刷新按钮样式
        .refresh-btn {
            margin-right: @w;
        }

        .ant-dropdown-link {
            text-decoration: none;
            color: rgba(0, 0, 0, 0.85);

            .anticon {
                font-size: 12px;
            }
        }

        /* 表格区域的样式重置 */
        .ant-table-thead {
            tr {
                th {
                    // 表格的头部单元格的高度
                    padding: 10px 16px;
                }
            }
        }
        .ant-table-body {
            table {
                table-layout: fixed; // 必须，表格宽度不随文字增多而变长
            }

            background: @white; // 表格背景颜色设置

            tr {
                &.active-tr {
                    // 鼠标选中表格单元行背景颜色高亮显示
                    background: #dbf2ff;
                }

                th {
                    // 表格的头部单元格的高度
                    padding: 10px 16px;
                }

                td {
                    // 文字超出部分显示省略号
                    cursor: pointer;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    overflow: hidden;

                    padding: 8px 16px; // 表格的单元格的上下padding

                    &:nth-of-type(1) {
                        // 单元格居左显示
                        text-align: left !important;
                    }
                }

                &.ant-table-expanded-row > td {
                    background: transparent !important;
                }
            }
        }

        // 表格的分页右边距
        .ant-table-pagination.ant-pagination {
            margin: 16px;
        }

        /* 68分类表格样式重置 */
        &.no-cursor-pointer {
            padding-top: 0;

            .ant-table-body {
                tr {
                    td {
                        // 鼠标手势重置 - 只有鼠标经过加号图标才是手型
                        cursor: text;
                    }
                }
            }
        }
    }

    .ant-table-scroll {
        table {
            width: 100%;
        }
    }
}

:global {
    // 鼠标经过表格的单元格背景颜色控制
    .ant-table-thead > tr:not(.ant-table-expanded-row):hover > td,
    .ant-table-thead > tr.ant-table-row-hover:not(.ant-table-expanded-row) > td,
    .ant-table-tbody > tr.ant-table-row-hover:not(.ant-table-expanded-row):hover > td,
    .ant-table-tbody > tr:not(.ant-table-expanded-row):hover > td {
        background: transparent !important;
    }

    .ant-table-tbody > tr:not(.active-tr):hover > td {
        background: #f0faff !important;
    }
}

/* 架构图 */
.architecture-diagram-wrap {
    // 左上角的多选框
    .architecture-diagram-checkbox-group {
        position: fixed;
        z-index: 2;

        .ant-checkbox-group {
            display: flex;
            flex-direction: column;
            text-align: left;
            background: rgba(255, 255, 255, 0.85);
        }

        .ant-checkbox-group-item {
            height: 28px;

            span {
                &.ant-checkbox {
                    // 隐藏复选框
                    display: none;
                }

                &:nth-of-type(2) {
                    display: inline-block;
                    width: 90px;
                    font-size: 12px;
                    color: @white;
                    // margin-left: 10px;
                    text-align: center;
                }
            }

            &:nth-of-type(1) {
                span {
                    &:nth-of-type(2) {
                        background-image: linear-gradient(180deg, #ff81a7 0%, #e01d6a 100%);
                        box-shadow: 0 2px 8px 0;
                    }
                }
            }

            &:nth-of-type(2) {
                span {
                    &:nth-of-type(2) {
                        background-image: linear-gradient(0deg, #f47c63 0%, #ffa988 100%);
                        box-shadow: 0 2px 8px 0;
                    }
                }
            }

            &:nth-of-type(3) {
                span {
                    &:nth-of-type(2) {
                        background-image: linear-gradient(180deg, #61f7e2 0%, #00acb2 100%);
                        box-shadow: 0 2px 8px 0;
                    }
                }
            }

            &:nth-of-type(4) {
                span {
                    &:nth-of-type(2) {
                        background-image: linear-gradient(180deg, #d8d4d4 0%, #a3a1a1 100%);
                        box-shadow: 0 2px 8px 0;
                    }
                }
            }
        }
    }

    // 中间主体的架构图
    .org-tree-container {
        .org-tree-node-label {
            .org-tree-node-label-inner {
                padding: 0;
                border-radius: 8px;
            }
        }

        // 自定义样式
        .org-tree-node-children {
            &:before {
                border-left-color: #1890ff;
            }
        }

        .org-tree-node,
        .org-tree-node:not(:first-child),
        .org-tree-node:not(:last-child) {
            &:after,
            &:before {
                border-left-color: #1890ff;
                border-top-color: #1890ff;
            }
        }

        .architecture-diagram {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-width: 128px;
            height: 80px;
            border-radius: 8px;
            padding: 0 5px;
            color: @white;
            cursor: pointer;
            overflow: hidden;

            &.on-personnel {
                // 在职人员
                background-image: linear-gradient(180deg, #ff81a7 0%, #e01d6a 100%);
                box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);

                &.cur {
                    border: 4px solid transparent;
                    border-image: linear-gradient(#e63d72, #7d103a) 4 4;
                }
            }

            &.stop-personnel {
                // 停薪留职人员
                background-image: linear-gradient(179deg, #fcc09b 0%, #ff8153 100%);
                box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);

                &.cur {
                    border: 4px solid transparent;
                    border-image: linear-gradient(#fe733f, #901900) 4 4;
                }
            }

            &.trial-personnel {
                // 试用人员
                background-image: linear-gradient(180deg, #61f7e2 0%, #00acb2 100%);
                box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);

                &.cur {
                    border: 4px solid transparent;
                    border-image: linear-gradient(#00a3a6, #006b6e) 4 4;
                }
            }

            &.departure-personnel {
                // 已离职
                background-image: linear-gradient(180deg, #d8d4d4 0%, #a3a1a1 100%);
                box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);

                &.cur {
                    border: 4px solid transparent;
                    border-image: linear-gradient(#a5a5a5, #545454) 4 4;
                }
            }

            &.department-defaults {
                // 默认根颜色
                cursor: text;
                background-image: linear-gradient(0deg, #933bff 0%, #d77efc 100%) !important;
                box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15) !important;
            }

            &:hover {
                // 鼠标经过颜色
                box-shadow: 0 3px 8px 0 rgba(0, 0, 0, 0.3);
            }

            .font-size {
                &:nth-of-type(1) {
                    font-size: 16px;
                    font-weight: 700;
                }

                &:nth-of-type(2),
                &:nth-of-type(3) {
                    font-size: 12px;
                }
            }
        }
    }

    /* 架构图 */
    &.architecture-plan-wrap {
        .ant-checkbox-group-item {
            &:nth-of-type(2) {
                span {
                    &:nth-of-type(2) {
                        background-image: linear-gradient(180deg, #d8d4d4 0%, #a3a1a1 100%);
                        box-shadow: 0 2px 8px 0;
                    }
                }
            }
        }

        // 中间主体的架构图
        .org-tree-container {
            .org-tree-node-label {
                .org-tree-node-label-inner {
                    padding: 0;
                    border-radius: 8px;
                }
            }

            // 自定义样式
            .org-tree-node-children {
                &:before {
                    border-left-color: #1890ff;
                }
            }

            .org-tree-node,
            .org-tree-node:not(:first-child),
            .org-tree-node:not(:last-child) {
                &:after,
                &:before {
                    border-left-color: #1890ff;
                    border-top-color: #1890ff;
                }
            }

            .architecture-diagram {
                color: @white;

                &.department-yes {
                    // 部门有效
                    background-image: linear-gradient(180deg, #ff81a7 0%, #e01d6a 100%);
                    box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);

                    &.cur {
                        border: 4px solid transparent;
                        border-image: linear-gradient(#e63d72, #7d103a) 4 4;
                    }
                }

                &.department-no {
                    // 部门无效
                    background-image: linear-gradient(180deg, #d8d4d4 0%, #a3a1a1 100%);
                    box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.15);

                    &.cur {
                        border: 4px solid transparent;
                        border-image: linear-gradient(#a5a5a5, #545454) 4 4;
                    }
                }

                &:hover {
                    // 鼠标经过颜色
                    box-shadow: 0 3px 8px 0 rgba(0, 0, 0, 0.3);
                }
            }
        }
    }
}

/* 人力资源新增/修改弹框区域 */
.departments-item {
    .ant-select-selection--multiple {
        width: 100%;
        padding-bottom: 0;
    }

    .input,
    .ant-select-selection__rendered {
        height: auto !important;
        min-height: 24px !important;
    }

    .ant-input {
        height: 24px;
    }

    .item-children {
        height: 24px !important;
    }
}

.human-resources-modal-form {
    min-width: 862px !important;

    .form-top {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
    }

    .right-wrap {
        display: flex;
        flex-direction: column;
        align-items: center;
        width: 254px;
        margin-left: 28px;
        text-align: center;
    }

    .right {
        position: relative;
        left: 14px;
        width: 200px;
        height: 200px;
        margin: auto;
        background: #e6e4e4;
        border: 1px solid #ccc;
        font-size: 24px;
        color: #b6b7b7;

        .img {
            display: block;
            width: 100%;
            height: 100%;
        }

        .look-remove-wrap {
            position: absolute;
            left: 0;
            right: 0;
            top: 0;
            bottom: 0;
            z-index: 2;
            display: flex;
            flex-direction: row;
            justify-content: center;
            align-items: center;
            background: rgba(0, 0, 0, 0.61);
            color: @white;
            font-size: 32px;

            .btn-icon {
                margin: 0 5px;
                cursor: pointer;
            }
        }

        span {
            position: absolute;
            left: 50%;
            top: 50%;
            z-index: 1;
            transform: translate(-50%, -50%);
        }
    }

    .user-pic,
    .form-bottom {
        width: 100%;
    }

    .description-span {
        padding-right: 10px;
    }

    .upload-wrap {
        margin: 24px 0 0 14px;
    }
}

/*平台产品 表格*/
.product-page {
    .ant-tabs-nav {
        padding-left: 10px;
    }
    .ant-table-header {
        overflow-y: auto !important;
    }
    .ant-table-tbody {
        tr {
            &:hover {
                td {
                    background-color: #e1f5ff;
                }
            }
            &.active:hover {
                td {
                    color: rgba(0, 0, 0, 0.65);
                }
            }
        }
    }
    .table-part {
        .ant-table-thead {
            tr {
                th {
                    // 表格的头部单元格的高度
                    padding: 10px 16px;
                    /*padding-bottom: 24px;*/
                }
            }
        }
        .ant-table-body {
            overflow-y: auto !important;
            table {
                table-layout: fixed; // 必须，表格宽度不随文字增多而变长
                width: 100% !important;
            }

            background: @white; // 表格背景颜色设置

            tr {
                &.active-tr {
                    // 表格单元行背景颜色高亮显示
                    background: #dbf2ff;
                }

                th {
                    // 表格的头部单元格的高度
                    padding: 10px 16px;
                }

                td {
                    // 文字超出部分显示省略号
                    cursor: pointer;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    overflow: hidden;

                    padding: 8px 16px; // 表格的单元格的上下padding

                    &:nth-of-type(1) {
                        // 单元格居左显示
                        text-align: left !important;
                    }
                }
            }
        }
        // 表格的分页右边距
        .ant-table-pagination.ant-pagination {
            margin: 16px;
        }
    }
    .tab-table-wrap.more {
        background: #fff;
        height: 274px;
        .ant-tabs {
            margin-left: 4px;
            margin-right: 4px;
            margin-bottom: 4px;
            height: 250px;
            border-bottom: 1px solid #e4e4e4;
        }
        .ant-tabs .ant-tabs-top-content > .ant-tabs-tabpane {
            border-left: 1px solid #e4e4e4;
            border-right: 1px solid #e4e4e4;
            // border-bottom: 1px solid #e4e4e4;
        }
    }
    .tab-table-wrap.more2 {
        .ant-tabs {
            border-bottom: 1px solid #e4e4e4;
        }
        .ant-tabs .ant-tabs-top-content > .ant-tabs-tabpane {
            border-left: 1px solid #e4e4e4;
            border-right: 1px solid #e4e4e4;
            /*height: 205px;*/
        }
    }
}
/* 平台-机构管理-机构认证 */
.agency-approval-container {
    .ant-tabs {
        height: 100%;
        .ant-tabs-top-content {
            height: 100%;
        }
        .ant-tabs-content {
            height: 100%;
        }
        .ant-tabs-card-content {
            height: calc(100% - 48px);
        }
        .ant-tabs-tabpane-active {
            height: 100%;
        }
    }
}
/* 资证管理 */
.mylic-manage-container {
    height: 100%;
    .btn-top-wrap {
        align-items: left;
        justify-content: left;
    }
    .ant-tabs {
        height: 100%;
        .ant-tabs-top-content {
            height: 100%;
        }
        .ant-tabs-content {
            height: 100%;
        }
        .ant-tabs-card-content {
            height: calc(100% - 31px);
        }
        .ant-tabs-tabpane-active {
            height: calc(100% - 31px);
        }
        .agency-down-tabs {
            .ant-tabs-card-content {
                height: 204px !important;
            }
        }
    }
}
.agency-entry {
    .modal-list {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        margin-top: 48px;
        .ant-form-item {
            margin-bottom: 8px;
        }
        .img-count {
            display: inline-block;
            vertical-align: top;
            width: 40%;
            .gallery {
                width: 360px;
                height: 480px;
                background: #d8d8d8;
                border: 1px solid #979797;
                overflow: hidden;
                img {
                    width: 100%;
                    height: 100%;
                }
            }
            .slick-slider {
                width: 360px;
                height: 480px;
            }
        }
        .info-count {
            display: flex;
            flex: 1;
        }
        /* 暂无数据 */
        .no-data-box {
            margin-top: 40%;
            color: #b2b2b2;
            font-size: 26px;
            text-align: center;
            i {
                font-size: 48px;
            }
        }
        .ant-input-disabled {
            background: #fff;
            color: rgba(0, 0, 0, 0.65);
            cursor: default;
        }
        .ant-select-disabled {
            color: rgba(0, 0, 0, 0.65);
            cursor: default;
            .ant-select-selection {
                background: #fff;
                cursor: default;
            }
        }
        .ant-cascader-picker-disabled {
            background: #fff;
            color: rgba(0, 0, 0, 0.65);
            cursor: default;
        }
        .ant-tree-checkbox-disabled {
            color: rgba(0, 0, 0, 0.65);
            cursor: default;
        }
        .ant-tree-treenode-disabled {
            color: rgba(0, 0, 0, 0.65);
            cursor: default;
            span:not(.ant-tree-switcher) {
                color: rgba(0, 0, 0, 0.65);
                cursor: default;
            }
        }
    }
}
// 信息修改
.agency-edit {
    .ant-select {
        height: 24px;
        .ant-select-enabled {
            .ant-select-selection__rendered {
                line-height: 24px;
            }
        }
    }
    .ant-input-disabled {
        background: #fff;
        color: rgba(0, 0, 0, 0.65);
        cursor: default;
    }
    .ant-select-disabled {
        color: rgba(0, 0, 0, 0.65);
        cursor: default;
        .ant-select-selection {
            background: #fff;
            cursor: default;
        }
    }
    .ant-cascader-picker-disabled {
        background: #fff;
        color: rgba(0, 0, 0, 0.65);
        cursor: default;
    }
}
// 图片上传
.agency-upload {
    .upload-count {
        margin-top: 48px;
    }
}
/* 模态框区域 */
.modal-form-input-scoped {
    padding-bottom: 0 !important;

    &.modal-add {
        .tab-table-wrap {
            padding-bottom: 24px;
            .ant-table-tbody > tr:not(.active-tr):hover > td {
                background: #f0faff !important;
            }
        }
    }

    .ant-modal-header {
        height: 48px;
        padding: 0;

        .ant-modal-title {
            line-height: 48px;
            text-indent: 16px;
            background: #2090e6;
            color: @white;
            font-size: 14px;
        }
    }

    .ant-modal-body {
        padding: 24px;
        .tabs-wrap {
            padding-top: 24px;
        }
    }

    &.modal-form-input-scoped-td {
        .tab-table-wrap .ant-table-body tr td {
            padding: 4px 16px !important;
        }
    }

    //  间距调整
    &.modal-form-input-scoped-8 {
        .ant-modal-body {
            padding: 8px;
        }
        .ant-tabs-bar {
            margin: 0;
        }
        .ant-tabs .ant-tabs-top-content {
            height: 557px;
            padding: 8px;
            border: 1px solid #e8e8e8;
            border-top: none;
        }
        .tab-table-overflowy {
            table {
                .yrt {
                    display: block;
                    width: 38px;
                    height: 100%;
                    padding-right: 0;
                    font-size: 18px;
                    cursor: pointer;

                    &.yrt-shanchu-copy {
                        color: #f00;
                    }
                }
            }
        }
        .tab-table-wrap .ant-table-body tr td {
            padding: 5px 16px;
        }
    }

    .single-tb .ant-modal-body {
        padding: 0 !important;
    }

    .ant-modal-content {
        .ant-modal-close {
            height: 48px;
            line-height: 48px;

            &:hover {
                background: #1387e0;
            }
        }
        .ant-modal-close-x {
            height: 48px;
            line-height: 48px;
            color: @white;
        }
    }

    .ant-modal-footer {
        padding: 12px 24px;
        background: #e3e6e9;

        .ant-btn {
            min-width: 76px;
        }

        // 入库单 - 新增
        .footer-btn-list {
            display: flex;
            flex-direction: row;
            justify-content: space-between;
            padding-left: 0;
            margin-bottom: 0;
        }
    }

    .modal-form-input {
        ul {
            padding-left: 0;
            margin-bottom: 0;
        }

        .item {
            display: flex;
            flex-direction: row;
            align-items: center;
            padding-bottom: 24px;

            &.item-description {
                align-items: flex-start;

                .label {
                    position: relative;
                    top: 2px;
                }
            }

            &:last-child {
                padding-bottom: 0;
            }
        }

        .item-agency {
            display: flex;
            flex-direction: row;
            align-items: center;
            padding-bottom: 24px;
            .label-short {
                display: flex;
                align-items: center;
                justify-content: flex-end;
                width: 90px;
                line-height: 14px;
                text-align: right;
                padding-right: 10px;
            }
            .ant-calendar-picker-input {
                height: 24px;
                .ant-input {
                    height: 24px;
                }
            }
            .ant-calendar-picker-icon {
                right: 7px;
            }
            .ant-input {
                line-height: 0.5;
            }
            textarea {
                height: 64px;
                line-height: 1;
            }
        }

        .label {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            width: 140px;
            line-height: 14px;
            text-align: right;
            padding-right: 10px;

            .em {
                width: 14px;
                height: 14px;
                line-height: 20px;
                font-size: 21px;
                padding-right: 10px;
            }
        }

        .input {
            display: flex;
            flex: 1;
            height: 24px;
            line-height: 24px;
            resize: none;

            .ant-cascader-input {
                height: 24px;
                line-height: 24px;
            }

            &.ant-calendar-picker {
                div,
                .ant-input {
                    width: 100%;
                    height: 100%;
                    line-height: 100%;
                }
            }

            .ant-select-selection {
                width: 100%;
            }
        }

        .item-children {
            position: relative;
            display: flex;
            flex-direction: row;
            width: 50%;

            &.item-children-agency-approval {
                width: 63%;
                .label-short {
                    width: 90px;
                }
            }

            &:nth-of-type(1) {
                padding-right: 8px;
            }
            &:nth-of-type(2) {
                padding-left: 8px;
            }

            .label {
                width: 140px;
            }
            .input-short {
                width: 164px;
            }
            .input,
            .ant-select {
                display: flex;
                flex: 1;
            }

            .ant-select-selection--single {
                width: 100%;
                height: 100%;
            }

            .ant-select-selection__rendered {
                line-height: 22px;
            }

            .unit {
                // 单位 => 详见仓库建模弹框的区域类别
                position: absolute;
                right: 0;
                top: 50%;
                color: #888;
                font-size: 12px;
                transform: translateY(-50%);
            }
        }

        .form-bottom {
            padding-top: 24px;
        }

        .top-list {
            display: flex;
            flex-direction: row;

            li {
                display: flex;
                flex-direction: row;
                width: 100%;
            }

            .item {
                padding-right: 16px;
                &:last-child {
                    padding: 0 0 24px 0;
                }
            }

            .label {
                width: 66px;
                max-width: 90px;
            }

            .title {
                h6 {
                    display: flex;
                    justify-content: flex-end;
                    flex: 1;
                    margin-bottom: 0;
                    font-size: 24px;
                    color: #444;
                    padding: 0 16px 24px 0;
                }

                .item-wrap {
                    display: flex;
                    flex: 1;
                }
            }

            .item-wrap {
                display: flex;
                flex-direction: row;
            }
        }
    }
}
.modal-tip {
    position: absolute;
    bottom: 18px;
    font-size: 14px;
    color: #888;
}
/* 模态框表单区域--2 */
.search-box {
    .ant-cascader-input {
        height: 24px;
        line-height: 24px;
    }
}
.modal-list,
.upload-count {
    /*margin-left: -4px;*/
    /*margin-top: -5px;*/
    /*margin-bottom: -13px;*/
    .ant-carousel .slick-slide {
        text-align: center;
        background: none !important;
        overflow: hidden;
    }
    .ant-modal-body {
        padding-left: 20px;
    }
    .ant-input-number-input {
        height: 24px !important;
    }
    .ant-form-item-control {
        line-height: 37px !important;
    }
    textarea {
        margin-top: 8px;
        margin-bottom: 8px !important;
    }
    .ant-calendar-picker-input {
        height: 24px;
        line-height: 18px;
    }
    .ant-select-selection__rendered {
        line-height: 24px !important;
    }
    .ant-form-item-label {
        line-height: 35px !important;
    }
    input.ant-input {
        height: 24px !important;
        line-height: 0.5 !important;
    }
    .ant-input-number {
        height: 24px !important;
    }
    .ant-select-selection--single {
        height: 24px !important;
    }
    .ant-form-item-label label {
        color: #778ca2 !important;
    }
    .ant-form-item-label label:after {
        content: ' ';
        margin: 0 8px 0 2px;
        position: relative;
        top: -0.5px;
    }
    .must-issue {
        .ant-form-item-label label:before {
            content: '*';
            color: #f00;
            margin-right: 4px;
            display: inline-block;
            vertical-align: middle;
            font-size: 21px;
        }
    }
    ul {
        padding: 0;
    }
    li {
        &.item {
            margin-bottom: 12px;
        }
        .label {
            display: inline-block;
            text-align: right;
            width: 16%;
            padding-right: 10px;
        }
    }
}

/* 营业执照相关的左侧浮动按钮 */
.btn-fixed-wrap {
    z-index: 1;
    display: flex;
    flex-direction: column;
    background: rgba(255, 255, 255, 0.85);
    margin-bottom: 0;
    padding: 0 10px;

    li {
        margin-bottom: 7px;

        .ant-btn {
            min-width: 90px;
            height: 24px;
        }
    }
}

/* 全局可以拖拽的顶部样式重置 */
.ant-modal-centered {
    .global-drag {
        position: absolute;
        padding-bottom: 0;

        .ant-modal-body {
            @h48: 48px;
            .modal-header-wrap {
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                display: flex;
                align-items: center;
                height: @h48;
                background: #2090e6;

                h6 {
                    width: 100%;
                    height: @h48;
                    line-height: @h48;
                    color: @white;
                    font-size: 14px;
                    text-indent: 24px;
                    cursor: move;
                    margin-bottom: 0;
                }
            }
            .modal-form-input {
                padding-top: @h48;

                &.for-single {
                    h6 {
                        display: flex;
                        justify-content: center;
                        font-size: 24px;
                        color: #444;
                        padding-bottom: 24px;
                        margin-bottom: 0;
                    }

                    .before-issuing {
                        padding-left: 10px;
                    }
                }
            }
        }
        .agency-base-scope {
            .modal-form-input {
                padding-top: 16px;
            }
        }
    }
}
/* table select 选择框样式复原 */
/*.table-select-center .tab-table-wrap .ant-table-body tr td.ant-table-selection-column {*/
/*text-align: center!important;*/
/*}*/
/* 底部加号样式 */
.footer-add-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 38px;
    line-height: 38px;
    text-align: center;
    background: #f8fafc;
    border: 1px dashed #d3d4d6;
    border-radius: 4px;
    margin-top: 8px;

    .yrt {
        display: block;
        width: 60px;
        height: 38px;
        padding-right: 0;

        &:hover {
            color: #2090e6;
            cursor: pointer;
        }
    }
}
/* 星号 * */
.asterisk {
    position: absolute;
    top: 13px;
    width: 14px;
    height: 14px;
    line-height: 20px;
    font-size: 21px;
    font-weight: normal;
}
/* 编辑 - 删除 - 保存 - 取消 【按钮】 */
.action-btn-list-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    padding-left: 0;
    margin-bottom: 0;

    li {
        &:extend(.action-btn-list-wrap);
        text-align: center;

        i {
            &:extend(.action-btn-list-wrap);
            width: 30px;
            height: 30px;
            font-size: 18px;
            padding-right: 0;
            margin: 0 4px;
            cursor: pointer;

            &.edit {
                color: #1890ff;
            }

            &.save {
                color: #52c41a;
            }

            &.cancel {
                color: #f5222d;
            }

            &.disabled {
                color: #bfbcbc;
                cursor: not-allowed;
            }
        }
    }
}
.time_line_box {
    // 头像流程图
    .ant-timeline-item-head-custom {
        left: 7px !important;
    }
    .ant-timeline-item-content {
        padding-left: 10px !important;
        top: -5px !important;
    }
    .ant-timeline-item-tail {
        left: 7px !important;
    }
}
.print-content {
    width: 100%;
    padding-bottom: 24px;
    @h48: 48px;
    img {
        max-width: 100%;
    }
    .ant-modal {
        top: 0px;
    }
    .tab-table-wrap {
        .ant-table-body {
            tr td:nth-of-type(1) {
                text-align: center !important;
            }
        }
    }
    .modal-form-input {
        padding-top: 20px;

        &.for-single {
            h6 {
                display: flex;
                justify-content: center;
                font-size: 24px;
                color: #444;
                padding-bottom: 24px;
                margin-bottom: 0;
            }

            .before-issuing {
                padding-left: 10px;
            }
        }
    }
    .modal-form-input {
        ul {
            padding-left: 0;
            margin-bottom: 0;
        }

        .item {
            display: flex;
            flex-direction: row;
            align-items: center;
            padding-bottom: 24px;

            &.item-description {
                align-items: flex-start;

                .label {
                    position: relative;
                    top: 2px;
                }
            }

            &:last-child {
                padding-bottom: 0;
            }
        }

        .item-agency {
            display: flex;
            flex-direction: row;
            align-items: center;
            padding-bottom: 24px;
            .label-short {
                display: flex;
                align-items: center;
                justify-content: flex-end;
                width: 90px;
                line-height: 14px;
                text-align: right;
                padding-right: 10px;
            }
            .ant-calendar-picker-input {
                height: 24px;
                .ant-input {
                    height: 24px;
                }
            }
            .ant-calendar-picker-icon {
                right: 7px;
            }
            .ant-input {
                line-height: 0.5;
            }
            textarea {
                height: 64px;
                line-height: 1;
            }
        }

        .label {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            width: 140px;
            line-height: 14px;
            text-align: right;
            padding-right: 10px;

            .em {
                width: 14px;
                height: 14px;
                line-height: 20px;
                font-size: 21px;
                padding-right: 10px;
            }
        }

        .input {
            display: flex;
            flex: 1;
            height: 24px;
            line-height: 24px;
            resize: none;

            .ant-cascader-input {
                height: 24px;
                line-height: 24px;
            }

            &.ant-calendar-picker {
                div,
                .ant-input {
                    width: 100%;
                    height: 100%;
                    line-height: 100%;
                }
            }

            .ant-select-selection {
                width: 100%;
            }
        }

        .item-children {
            position: relative;
            display: flex;
            flex-direction: row;
            width: 50%;

            &.item-children-agency-approval {
                width: 63%;
                .label-short {
                    width: 90px;
                }
            }

            &:nth-of-type(1) {
                padding-right: 8px;
            }
            &:nth-of-type(2) {
                padding-left: 8px;
            }

            .label {
                width: 140px;
            }
            .input-short {
                width: 164px;
            }
            .input,
            .ant-select {
                display: flex;
                flex: 1;
            }

            .ant-select-selection--single {
                width: 100%;
                height: 100%;
            }

            .ant-select-selection__rendered {
                line-height: 22px;
            }

            .unit {
                // 单位 => 详见仓库建模弹框的区域类别
                position: absolute;
                right: 0;
                top: 50%;
                color: #888;
                font-size: 12px;
                transform: translateY(-50%);
            }
        }

        .form-bottom {
            padding-top: 24px;
        }

        .top-list {
            display: flex;
            flex-direction: row;

            li {
                display: flex;
                flex-direction: row;
                width: 100%;
            }

            .item {
                padding-right: 16px;
                &:last-child {
                    padding: 0 0 24px 0;
                }
            }

            .label {
                width: 66px;
                max-width: 140px;
            }

            .title {
                h6 {
                    display: flex;
                    justify-content: flex-end;
                    flex: 1;
                    margin-bottom: 0;
                    font-size: 24px;
                    color: #444;
                    padding: 0 16px 24px 0;
                }

                .item-wrap {
                    display: flex;
                    flex: 1;
                }
            }

            .item-wrap {
                display: flex;
                flex-direction: row;
            }
        }
    }
}
.re-login-modal {
    .ant-modal-body {
        padding: 24px 0 0 0;
    }
    .modal-header-wrap {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        display: flex;
        align-items: center;
        height: 48px;
        background: #2090e6;

        h6 {
            width: 100%;
            height: 48px;
            line-height: 48px;
            color: @white;
            font-size: 14px;
            text-indent: 24px;
            cursor: move;
            margin-bottom: 0;
        }
    }
    .re-login {
        margin-top: 48px;
        .ant-form-item-label {
            line-height: 39px;
            color: #7b8ba0;
            label {
                color: #7b8ba0;
            }
        }
        .login-form-button {
            margin-left: 8px;
        }
    }
}
.validity-warning {
    .ant-input-number-input {
        height: 24px !important;
    }
}

/* vue-easytable 公用样式重写 */
.align-items-center {
    .v-table-body-cell {
        display: flex;
        justify-content: center;
        align-items: center;
    }
}
.v-table-body-cell {
    color: rgba(0, 0, 0, 0.65);
}
.empty-dblclick-button {
    position: relative;
    z-index: 99;

    &:hover {
        color: #0d74c6;
        cursor: pointer;
    }
}
.v-table-btable {
    tr {
        td {
            cursor: pointer;
        }
    }
}
.easytable-active-tr {
    // 表格高亮颜色
    background: #dbf2ff;
}
.v-table-htable {
    .v-table-title-cell {
        height: 40px !important;
        font-weight: 500;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.85);
        background: #f0f2f5;
    }
}
// 表格分页样式
.v-page-ul {
    display: flex;
    justify-content: flex-end;
    margin: 15px;
}
.v-page-li-active {
    background-color: transparent;
    border-color: #1890ff;
    font-weight: 500;

    a {
        color: #1890ff;
    }
}
.v-dropdown a,
.v-dropdown a:visited {
    color: rgba(0, 0, 0, 0.65);
}

.vxe-table .vxe-body--row.row--current,
.vxe-table .vxe-body--column.active-tr {
    // 【选中颜色】
    background: #dbf2ff;
}
.vxe-table.size--small {
    font-size: 14px;
}
.vxe-table .vxe-body--row:hover,
.vxe-table .vxe-body--row:hover .vxe-body--column {
    cursor: pointer;
    background: #f0faff;
}
.vxe-table.size--small .vxe-body--column.col--ellipsis {
    height: 36px;
}
.barcode-table.vxe-table.size--small .vxe-body--column.col--ellipsis .vxe-cell {
    max-height: 300px;
    padding: 10px;
}
.vxe-table .vxe-cell {
    font-size: 12px;
    padding: 0 4px;

    .ant-input-number-input {
        padding-left: 3px;
    }
}
.vxe-table.size--small .vxe-body--column:not(.col--ellipsis) {
    padding: 7px 0;
}
.vxe-table.size--small .vxe-header--column.col--ellipsis {
    height: 36px;
}
.vxe-table.t--border .vxe-footer--column,
.vxe-table.t--border .vxe-header--column {
    // , .vxe-table .vxe-body--row .vxe-body--column
    border-bottom: 1px solid #e8e8e8;
}
// 去掉树表格的边框线样式
.operation-path-tree-table.t--border .vxe-footer--column,
.operation-path-tree-table.t--border .vxe-header--column,
.operation-path-tree-table .vxe-body--row .vxe-body--column {
    border: none;
}
.vxe-table.t--border .vxe-body--column,
.vxe-table.t--border .vxe-footer--column,
.vxe-table.t--border .vxe-header--column {
    border-right: 1px solid #e8e8e8;
}
.red-dot-add {
    position: absolute;
    left: 39px;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #f00;

    &.hide {
        display: none;
    }
}

/* 解决表格暂无数据的空白焦点区域 */
// .vxe-table.has--height  {
//     .vxe-table--empty-placeholder,
//     .vxe-table--empty-block,
//     .dblclick-wrap,
//     .vxe-table--empty-content {
//         position: absolute;
//         left: 0;
//         right: 0;
//         top: 0!important;
//         bottom: 17px;
//         z-index: 3;
//         display: flex;
//         justify-content: center;
//         align-items: center;
//         width: 100%!important;
//         transform: translateY(0)!important;
//         cursor: pointer;
//     }
// }

/* 解决模态框拖拽超出屏幕的时候隐藏滚动条 */
.ant-modal-wrap {
    overflow: hidden;
}

/* 税率显示百分号 */
.rate-wrap {
    display: flex;
    align-items: center;
    justify-content: center;

    .tip {
        color: rgba(0, 0, 0, 0.6);
        padding: 5px;
    }
}

/* 模态框左上角的logo图片显示 */
.modal-logo-left {
    display: inline-block;
    width: 36px;
    margin-right: 6px;
}

.nu-table .ant-table-header {
    overflow: auto !important;
    padding-bottom: 20px;
    margin-bottom: 0px !important;
}

.img-del-ico {
    position: absolute;
    top: -58px;
    right: 0px;
    height: 40px;
    width: 40px;
}

.img-pre-ico {
    width: 100px !important;
    font-size: 36px !important;
}

.scoped .search-form-wrap .hight-search-wrap .form-operation .ant-select-selection__choice {
    width: auto;
}

// 双击添加产品区域
.vxe-table .vxe-table--empty-content {
    position: relative;
    width: 100%;
    height: 100%;
    padding-top: 30px;

    .dblclick-wrap {
        position: absolute;
        left: 0;
        right: 0;
        top: 0;
        bottom: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
    }
}

// 表格工具栏
.my-vxe-toolbar {
    .vxe-toolbar.size--small {
        height: auto;
    }

    .tool-bar-icon {
        position: absolute;
        left: 40px;
        top: 0;
        font-size: 18px;
        width: 24px;
        height: 24px;
        border: 1px solid #ddd;
        text-align: center;
        border-radius: 4px;
        cursor: pointer;

        .yrt {
            display: block;
            padding-right: 0;
            font-size: 12px;
            margin-top: 3px;
        }
    }
}

// 效期预警页面的资证过期背景颜色标红
tr.ant-table-row.ant-table-row-level-0.current-bg {
    background: #ffe9e9;
}

// 空批号自己撑开本身高度
.ant-select-dropdown-menu-item {
    min-height: 32px;
}

/* tab选项卡重写 */
.tabs-product-wrap {
    display: flex;
    flex-direction: column;
    padding-top: 16px;

    .tabs-product-header {
        position: relative;
        display: flex;
        justify-content: space-between;
        align-items: center;
        height: 40px;

        &::after {
            content: '';
            position: absolute;
            bottom: -1px;
            left: 0;
            right: 0;
            height: 1px;
            background: #e8e8e8;
        }
    }

    .tabs-product-nav {
        display: flex;
        height: 100%;
        padding-left: 10px;
        margin-bottom: 0;

        li {
            position: relative;
            display: flex;
            align-items: center;
            margin-right: 2px;
            padding: 0 16px;
            background: #fafafa;
            border: 1px solid #e8e8e8;
            border-bottom: 0;
            border-radius: 4px 4px 0 0;
            cursor: pointer;

            &:hover,
            &.active-tab {
                color: #1890ff;
            }

            &.active-tab {
                background: #fff;
                border-bottom: none;

                &::after {
                    content: '';
                    position: absolute;
                    bottom: -1px;
                    left: 0;
                    right: 0;
                    z-index: 1;
                    height: 1px;
                    background: #fff;
                }
            }
        }
    }

    .tabs-product-content {
        padding-left: 0;
        margin-bottom: 0;

        .search-box {
            margin: 16px 0;
        }
    }
}

// 模态框右侧顶部的放大缩小的按钮
.my-modal-zoom,
.my-modal-zoom.ant-modal {
    position: absolute;
    left: 0 !important;
    right: 0;
    top: 0 !important;
    bottom: 0;
    width: 100% !important;
    padding-bottom: 0;

    .ant-modal {
        position: absolute;
        left: 0 !important;
        right: 0;
        top: 0 !important;
        bottom: 0;
        width: 100% !important;
        padding-bottom: 0;
    }

    .ant-modal-content {
        width: 100%;
    }

    .ant-modal-content,
    .modal-form-input {
        height: 100%;
    }

    .ant-modal-body {
        width: 100%;
        height: calc(100% - 57px);
    }
}
.modal-header-zoom.ant-btn {
    position: absolute;
    top: 0;
    right: 56px;
    width: 56px;
    height: 48px;
}

// BaseModal模块
.base-modal {
    .ant-modal-body {
        padding: 48px 24px 24px;
    }
}

// 表格底部的合计
.vxe-table.size--small .vxe-footer--column:not(.col--ellipsis) {
    padding: 4px 0;
}

// 表格的红冲文字样式
tr.vxe-body--row.red-dashed-odo-order,
tr.vxe-body--row.red-dashed-odo-order.row--current {
    color: #ea6e70;
}

// 采购记录查询表格文字样式 未审核
tr.vxe-body--row.un-certificated,
tr.vxe-body--row.un-certificated.row--current {
    color: #ea6e70;
}

// 采购记录查询表格文字样式 执行中
tr.vxe-body--row.in-process,
tr.vxe-body--row.in-process.row--current {
    color: #1890ff;
}

// 表格的分页样式
.vxe-pager {
    font-size: 12px;
}

// 平板尺寸调整
@media only screen and (max-width: 1026px) {
    .scoped .btn-top-wrap .btn-list-wrap li {
        margin-right: 8px;

        .ant-btn {
            margin-right: 8px;
            min-width: 70px;
        }
    }

    .scoped .search-form-wrap .hight-search-wrap .form li {
        width: 23%;
        padding-right: 1%;
    }

    .ant-modal-centered .ant-modal {
        width: 1000px !important;
        height: 480px !important;
        transform-origin: 0 0 !important;
        top: -20px !important;
    }

    .modal-form-input {
        height: 398px !important;
        // height: 452px!important;
    }
}

/***********************************************************************
     * 2021/04/08 louxiaochen 调整前端各类输入框disabled样式，字体颜色为正常黑色 */
.ant-select-disabled {
    color: rgba(0, 0, 0, 0.65);
}
.ant-input-disabled {
    color: rgba(0, 0, 0, 0.65);
}
.ant-input-number-disabled {
    color: rgba(0, 0, 0, 0.65);
}
.ant-cascader-picker-disabled {
    color: rgba(0, 0, 0, 0.65);
}
/***********************************************************************/

// 表格顶部的排序
.sortable-column-wrap {
    position: absolute;
    left: 0;
    top: 50%;
    bottom: 0;
    right: 0;
    transform: translateY(-50%);
    cursor: pointer;

    .custom-sort {
        font-size: 14px;
    }
}
</style>
