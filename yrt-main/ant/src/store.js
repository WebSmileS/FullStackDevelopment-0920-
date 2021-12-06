import Vue from 'vue';
import Vuex from 'vuex';
import VueCookie from 'vue-cookie';

import { startReceiveOrderMsgTask } from './config/ws';
// import { Stats } from 'fs'
import { createUserMenuMap } from '@/config/utilsForUserMenu';

import { appHeaderHeight, appMenuWidth } from '@/config/common';

import { userWarehouseListAPI, orgDeptAllTreeAPI, orgEmpDropdownListAPI } from '@/service/pageAjax';
import { cookie } from 'xe-utils/methods';

Vue.use(Vuex);

Vue.config.devtools = process.env.NODE_ENV === 'development';

const cookieTheme = VueCookie.get('theme');

/**
 * 部门数据
 * 注意：其数据结构为a-tree-select的treeData数据结构
 * @typedef {object} Department
 * @property {string} label - 名称
 * @property {string | number} key - id
 * @property {string | number} value - id
 * @property {boolean} disabled - 部门是否被禁用
 * @property {Department[] | undefined} children - 子部门
 */

/**
 * 人员数据
 * 注意：其数据结构为a-select的options数据结构
 * @typedef {object} Employee
 * @property {string} label - 名称
 * @property {string | number} key - 人员id
 */

const store = new Vuex.Store({
    state: {
        userinfo: {
            belong: 0,
            system_type: 0,
            password_cycle: 0,
            user_inner_sn: 0
        }, // 登录信息
        isLogin: '', // 是否已登录
        menuURI: '', // 左侧菜单的URI
        userMenu: [],
        isNewUserInfo: 'init', // 获取当前登录用户最新的信息
        orderMsgWebSocket: null,
        lastLoginUser: '', // 表示上一个登陆用户，该值用于判断在用户凭证过期时，是否删除消息推送的notification框, 拼接规则：user_inner_sn-systemType
        warehouseList: [], // 当前用户能够看到的仓库数据，非全量
        /** @type {Department[]} */
        departmentsOfUserOrganization: null, // 当前登录用户所属机构下的部门
        /** @type {Employee[]} */
        employeesOfUserOrganization: null, // 当前登录用户所属机构下的人员

        // theme
        theme: cookieTheme === null ? 'dark' : cookieTheme,

        // 菜单收起状态
        appMenuCollapsed: false,
        // 菜单顶栏尺寸
        appHeaderHeight,
        appMenuWidth,

        // 窗口宽高
        clientWidth: document.documentElement.clientWidth || document.body.clientWidth,
        clientHeight: document.documentElement.clientHeight || document.body.clientHeight
    },
    getters: {
        userMenuMap(state) {
            return createUserMenuMap(state.userMenu);
        },
        // 内容宽
        contentWidth(state) {
            return state.clientWidth - state.appMenuWidth;
        },
        // 内容高
        contentHeight(state) {
            return state.clientHeight - state.appHeaderHeight;
        }
    },
    mutations: {
        changeClientSize(state, item) {
            state.clientWidth = item.width;
            state.clientHeight = item.height;
        },
        changeAppMenuCollapsed(state, item) {
            state.appMenuCollapsed = item.collapsed;
        },
        changeAppMenuSize(state, item) {
            state.appMenuWidth = item.width;
        },
        changeStateInfo(state, item) {
            state.userinfo = item.info;
            state.isLogin = item.isLogin;
        },
        changeUserMenuData(state, menu = []) {
            state.userMenu = menu;
        },
        setMenuItemURIFn(state, URI) {
            state.menuURI = URI;
        },
        setIsNewUserInfoFn(state, info) {
            state.isNewUserInfo = info;
        },
        setOrderMsgWebSocket(state, socket) {
            state.orderMsgWebSocket = socket;
        },
        setLastLoginUser(state, info) {
            state.lastLoginUser = info;
        },
        setWarehouseList(state, list = []) {
            if (list === null) {
                state.warehouseList = [];
                return;
            }
            state.warehouseList = list.map((item) => ({
                ...item,
                title: item.name,
                value: item.warehouse_model_inner_sn,
                key: item.warehouse_model_inner_sn
            }));
        },
        /**
         * 更新当前登录用户所属机构的部门数据
         * 注意：其数据结构为a-tree-select的treeData数据结构
         * @param {Object} state -
         * @param {Department[]} data - 部门数据
         */
        setDepartmentsOfUserOrganization(state, data) {
            if (typeof data !== 'object' || data.constructor !== Array) {
                state = null;
                return;
            }
            const treeArray = [];
            const treeParse = (list, resultArray) => {
                list.forEach((item) => {
                    const result = {
                        label: item.name,
                        key: item.department_inner_sn,
                        value: item.department_inner_sn,
                        disabled: item.status !== 1,
                        children: undefined
                    };
                    if (item.children && item.children.length) {
                        result.children = [];
                        treeParse(item.children, result.children);
                    }
                    resultArray.push(result);
                });
            };
            treeParse(data, treeArray);
            state.departmentsOfUserOrganization = treeArray;
        },
        changeTheme(state) {
            state.theme = state.theme === 'dark' ? 'light' : 'dark';
            VueCookie.set('theme', state.theme);
        },
        /**
         * 更新当前登录人员所属机构下的人员列表数据
         * @param {object} state
         * @param {Employee[]} data - 人员列表数据
         */
        setEmployeesOfUserOrganization(state, data) {
            state.employeesOfUserOrganization = [...data];
        },
        async refreshUserWarehouseListFn(state) {
            let res = await userWarehouseListAPI();
            if (res && parseInt(res.code) === 0) {
                const { list } = res;
                if (!list) {
                    state.warehouseList = [];
                } else {
                    state.warehouseList = list.map((item) => ({
                        ...item,
                        title: item.name,
                        value: item.warehouse_model_inner_sn,
                        key: item.warehouse_model_inner_sn
                    }));
                }
            } else {
                state.warehouseList = [];
            }
        }
    },
    actions: {
        startReceiveOrderMsgTask({ commit }) {
            const ws = startReceiveOrderMsgTask();
            commit('setOrderMsgWebSocket', ws);
        },
        stopReceiveOrderMsgTask({ commit, state }) {
            if (state.orderMsgWebSocket) {
                state.orderMsgWebSocket.clear();
            }
            commit('setOrderMsgWebSocket', null);
        },
        /**
         * 获取当前登录用户机构下的部门
         */
        async loadDepartmentsOfUserOrganization({ commit }, { systemType, belong }) {
            const { code, msg, list } = await orgDeptAllTreeAPI(systemType, belong);
            if (code !== 0) {
                throw new Error(msg);
            }
            commit('setDepartmentsOfUserOrganization', list);
        },
        /**
         * 获取当前登录用户机构下的人员
         */
        async loadEmployeesOfUserOrganization({ commit }) {
            const { code, msg, list } = await orgEmpDropdownListAPI();
            if (code !== 0) {
                throw new Error(msg);
            }
            commit(
                'setEmployeesOfUserOrganization',
                list.map((employee) => ({ key: employee.employee_inner_sn, label: employee.name }))
            );
        }
    }
});
export default store;
