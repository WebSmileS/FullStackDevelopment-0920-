import Vue from 'vue';
import '@/styles/index.less';
import {
    Button,
    Icon,
    Layout,
    Dropdown,
    Menu,
    Pagination,
    Cascader,
    Checkbox,
    DatePicker,
    Form,
    Input,
    Radio,
    Select,
    Switch,
    TimePicker,
    TreeSelect,
    Upload,
    Popover,
    Table,
    LocaleProvider,
    List,
    Tabs,
    Tooltip,
    Tree,
    Row,
    Col,
    Alert,
    Drawer,
    Modal,
    message,
    Popconfirm,
    Progress,
    Skeleton,
    Spin,
    Carousel,
    InputNumber,
    Tag,
    notification
} from 'ant-design-vue';
import moment from 'moment';
import 'moment/locale/zh-cn';
import * as echarts from 'echarts/core';
import VueCookie from 'vue-cookie';
import Vue2OrgTree from 'vue2-org-tree';
import Utils from '@/config/utils';
import XEUtils from 'xe-utils';
import VXETable from 'vxe-table';
import 'vxe-table/lib/index.css';
import draggable from 'vuedraggable';
import vxeRenderers from './vxeRenderers';
import App from './App';
import router from './router';
import store from './store';
import scroll from 'vue-seamless-scroll';

Vue.use(Button);
Vue.use(Icon);
Vue.use(Layout);
Vue.use(Dropdown);
Vue.use(Menu);
Vue.use(Pagination);
Vue.use(Cascader);
Vue.use(Checkbox);
Vue.use(DatePicker);
Vue.use(Form);
Vue.use(Input);
Vue.use(Radio);
Vue.use(Select);
Vue.use(Switch);
Vue.use(TimePicker);
Vue.use(TreeSelect);
Vue.use(Upload);
Vue.use(Popover);
Vue.use(Tabs);
Vue.use(Tooltip);
Vue.use(Tree);
Vue.use(Alert);
Vue.use(Drawer);
Vue.use(Modal);
Vue.use(message);
Vue.use(Popconfirm);
Vue.use(Progress);
Vue.use(Skeleton);
Vue.use(Spin);
Vue.use(LocaleProvider);
Vue.use(List);
Vue.use(Row);
Vue.use(Col);
Vue.use(Carousel);
Vue.use(InputNumber);
Vue.use(Table);
Vue.use(Tag);
Vue.use(notification);
Vue.config.productionTip = false;

// 默认语言为 en-US，如果你需要设置其他语言，推荐在入口文件全局设置 locale
moment.locale('zh-cn');

// 【大屏】引入echart组件
Vue.prototype.$echarts = echarts;
// 【】
Vue.use(scroll);

Vue.prototype.$Store = store;
// 处理按需引入的弹框组件
Vue.prototype.$info = Modal.info;
Vue.prototype.$confirm = Modal.confirm;
Vue.prototype.$warning = Modal.warning;
Vue.prototype.$success = Modal.success;
Vue.prototype.$error = Modal.error;
Vue.prototype.$message = {
    info: message.info,
    warning: message.warning,
    warn: message.warn,
    success: message.success,
    error: message.error,
    loading: message.loading
};
Vue.prototype.$notification = {
    info: notification.info,
    warning: notification.warning,
    warn: notification.warn,
    success: notification.success,
    error: notification.error,
    destroy: notification.destroy,
    open: notification.open,
    close: notification.close
};

// VueCookie
Vue.prototype.$vueCookie = VueCookie;
Vue.use(VueCookie);

// Vue2OrgTree
Vue.use(Vue2OrgTree);

/* 公用方法库 */
Vue.prototype.$Utils = Utils;

// vxe-table依赖于xe-utils
Vue.prototype.$XEUtils = XEUtils;
// vxe-table自定义渲染器
vxeRenderers.forEach(([name, options]) => VXETable.renderer.add(name, options));
Vue.use(VXETable);

Vue.use(draggable);

Vue.config.devtools = process.env.NODE_ENV === 'development';

/* vue全局指令 */
// 修改弹窗的顶部拖拽  => 使用方法：v-globalDrag="{ el: 'global-drag' }"
Vue.directive('globalDrag', (el, binding) => {
    Vue.nextTick(() => {
        let element = binding.value ? binding.value.el : 'global-drag',
            obj = document.getElementsByClassName(element)[0];
        el.onmousedown = (ev) => {
            let disX = ev.clientX - obj.offsetLeft,
                disY = ev.clientY - obj.offsetTop;
            document.onmousemove = (ev) => {
                let l = ev.clientX - disX;
                let t = ev.clientY - disY;

                // 此处的判断是为了防止拖拽框被拖出屏幕可视区域
                // if (l <= 0) {
                //     l = 0
                // } else if (l > document.documentElement.clientWidth - obj.offsetWidth) {
                //     l = document.documentElement.clientWidth - obj.offsetWidth
                // }

                // if (t <= 0) {
                //     t = 0
                // } else if (t > document.documentElement.clientHeight - obj.offsetHeight) {
                //     t = document.documentElement.clientHeight - obj.offsetHeight
                // }

                obj.style.left = l + 'px';
                obj.style.top = t + 'px';
            };
            document.onmouseup = () => {
                document.onmousemove = document.onmouseup = null;
            };
            ev.stopPropagation();
        };
    });
});

// 光标聚焦
Vue.directive('focus', {
    inserted(el) {
        Vue.nextTick(() => {
            el.focus();
        });
    },
    update(el, binding) {
        if (binding.value) {
            el.focus();
        }
    }
});

new Vue({
    router,
    store,
    render: (h) => h(App)
}).$mount('#app');
