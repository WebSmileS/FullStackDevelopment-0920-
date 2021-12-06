import axios from 'axios';
import Vue from 'vue';
import { NeedResHeaderEndpoints } from '../config/constants';
import Utils from '@/config/utils';

// 当用户网络慢用户狂点按钮发送服务时，客户端取消重复发送请求
const CancelToken = axios.CancelToken;
// 用户发送承载请求的容器
let serviceUrlRepeat = {};

// 全局默认配置
// 设置 POST 请求头
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
axios.defaults.timeout = 10000;
axios.defaults.baseURL = '/'; // api 即vue.config.js 中配置的地址

// 配置 CORS 跨域
axios.defaults.withCredentials = true;
axios.defaults.crossDomain = true;

// 移除 重复请求
const removeRepeatServiceUrlFn = (key, isCancelRepeatUrl = false) => {
    if (key) {
        if (serviceUrlRepeat[key] && isCancelRepeatUrl) {
            serviceUrlRepeat[key]('频繁操作请求！');
        }
        // 手动重置已经发送的服务
        delete serviceUrlRepeat[key];
    } else {
        // 强制重置已经发送的服务
        serviceUrlRepeat = {};
    }
};

// 请求发起前拦截器
axios.interceptors.request.use(
    (config) => {
        const { url, method, params, data } = config,
            serviceUrlKey = url + '&' + method + Utils.joinParamsFn(params || data);

        removeRepeatServiceUrlFn(serviceUrlKey, true);
        config.cancelToken = new CancelToken(function executor(c) {
            serviceUrlRepeat[serviceUrlKey] = c;
        });

        // 全局body设置token
        /* if (Utils.getJwt().length > 0) {
        if (typeof config.data === 'string') {
            config.data += `&jwt=${Utils.getJwt()}`
        } else {
            if (config.data) {
                config.data.jwt = Utils.getJwt()
            }
        }
    } */

        // 全局的header设置token
        // config.headers['token'] = 'token'

        // 这句不能省，不然后面的请求就无法成功发起，因为读不到配置参数
        return config;
    },
    (error) => {
        // 异常处理
        return Promise.reject(error);
    }
);

// 响应拦截
axios.interceptors.response.use(
    (response) => {
        if (response.config) {
            const { url, method, params, data } = response.config;
            removeRepeatServiceUrlFn(url + '&' + method + Utils.joinParamsFn(params || data));
        }

        // 全局登录过滤，如果没有登录，直接跳转到登录 URL
        if (response.data.code === '401') {
            // 未登录
            // Vue.prototype.$Store.commit('changeStateInfo', {info: {}, isLogin: Math.random()})
            Vue.$router.replace('/Login');
            return false;
        }

        // 如果需要响应头的数据，直接返回response
        if (NeedResHeaderEndpoints.includes(response.config.url)) {
            return response;
        }
        // 这里返回的 response.data 是被 axios 包装过的一成，所以在这里抽取出来
        return response.data;
    },
    (error) => {
        if (
            error.code === 'ECONNABORTED' ||
            (error.message && (error.message === 'Network Error' || error.message.includes('timeout')))
        ) {
            Vue.prototype.$message.error('网络超时，请检查网络后重新刷新页面再试。');
        } else if (error.response) {
            const statusCode = parseFloat(error.response.status);
            const statusMsg = error.response.data.message;
            switch (statusCode) {
                case 401:
                case 403:
                    Vue.prototype.$Store.commit('changeStateInfo', { info: {}, isLogin: Math.random() });
                    break;
                case 404:
                case 504:
                    Vue.prototype.$message.error(`【${statusCode}】 - 服务器异常情况，请稍后刷新页面再试`);
                    break;
                default:
                    Vue.prototype.$message.error(`【${statusCode}】 - ${statusMsg}`);
                    break;
            }
        } else if (error.request) {
            Vue.prototype.$message.error(error.message || '客户端异常情况，请稍后刷新页面再试');
        } else {
            if (axios.isCancel(error)) {
                removeRepeatServiceUrlFn();
                // Vue.prototype.$message.error(error.message)
            } else {
                Vue.prototype.$message.error(`【${error}】 - 网络超时`);
            }
        }

        return Promise.reject(error);
    }
);

// 导出
export default axios;
