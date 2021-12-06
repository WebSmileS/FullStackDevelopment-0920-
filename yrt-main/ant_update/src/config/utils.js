import Vue from 'vue';
import VueCookie from 'vue-cookie';
import moment from 'moment';
import exportExcel from './exportExcel';
import { pictureAPIHost } from './constants';
Vue.use(VueCookie);
class Utils {
    constructor() {
        this.pattern = new RegExp('[*<>?]');
        // this.pattern = new RegExp("[%--`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]")
        this.patternSimple = new RegExp('[<>?]');
    }

    /**
     *
     * @param {object} element
     * <div
     *      v-globalDrag={el: 'your class name'}
     *      class="modal-header-wrap"
     *  >
     *      <h6>{{ yourTitle }}</h6>
     *  </div>
     * @param {boolean} isChild
     */
    globalDragCenterFn(element = 'global-drag', isChild, mode = '') {
        Vue.nextTick(() => {
            let timer = setTimeout(() => {
                let clientWidth = document.documentElement.clientWidth || document.body.clientWidth,
                    clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
                this.modalDrag = document.getElementsByClassName(element)[0];
                if (!this.modalDrag) return;
                this.modalDrag.style.left = (clientWidth - this.modalDrag.offsetWidth) / 2 + 'px';
                this.modalDrag.style.top = (clientHeight - this.modalDrag.offsetHeight) / 2 + 'px';
                switch (mode) {
                    case 'left':
                        this.modalDrag.style.left = '50px';
                        this.modalDrag.style.top = '50px';
                        break;
                    case 'right':
                        this.modalDrag.style.left = clientWidth - this.modalDrag.offsetWidth - 50 + 'px';
                        this.modalDrag.style.top = clientHeight - this.modalDrag.clientHeight - 50 + 'px';
                        break;
                }
                if (isChild) {
                    // this.modalDrag.style.top = (clientHeight - this.modalDrag.offsetHeight) / 2 - 62.5 + 'px'
                }
                clearTimeout(timer);
                timer = null;
            }, 0);
        });
    }
    debounce(func, delay) {
        // 延时监听触发器
        let timer;
        return function (...args) {
            if (timer) {
                clearTimeout(timer);
            }
            timer = setTimeout(() => {
                func.apply(this, args);
            }, delay);
        };
    }
    trim(s) {
        // 去左右空格
        if (!s) {
            return null;
        } else {
            return s.replace(/(^\s*)|(\s*$)/g, '');
        }
    }
    // 去除字符串里特殊符号（中英文）
    stripSpecialStr(s) {
        let rs = '';
        for (let i = 0; i < s.length; i++) {
            rs += s.substr(i, 1).replace(this.pattern, '');
        }
        return rs;
    }

    /**
     * 添加数值分隔符
     * @param {number | string} n 数值
     * @param {*} splitChar 分隔符
     * @returns {string}
     */
    commafy(n, splitChar = ',') {
        const [integralString, decimalString] = String(n).split('.');
        const result = [];
        let i;
        let j;
        let l = integralString.length;
        for (i = l - 1, j = 0; i >= 0; i -= 1, j += 1) {
            if (integralString[i] === '-') {
                result.unshift('-');
                break;
            }
            if (j === 3) {
                result.unshift(splitChar);
                j = 0;
            }
            result.unshift(integralString[i]);
        }
        if (decimalString && decimalString.length) {
            return `${result.join('')}.${decimalString}`;
        }
        return result.join('');
    }

    tips() {
        // 提示语
        return {
            noSelected: '请先选中表格中一行数据，再进行相关操作。',
            noCheckbox: '请先勾选表格中的复选框，再进行该操作。',
            specialChart: '不能输入特殊字符',
            selectOrgName: '请选择相关机构。',
            selectOrgNameIllegal: '被编辑过的相关机构名称不存在。'
        };
    }
    checkObjEqualFn(a, b) {
        let res = false;
        for (let server in b) {
            let serverItem = b[server];
            for (let old in a) {
                let oldItem = a[old];
                if (server === old && serverItem === oldItem) {
                    res = true;
                } else if (server === old && serverItem !== oldItem) {
                    if (serverItem === null && oldItem === undefined) {
                        res = true;
                    } else if (serverItem === undefined && oldItem === null) {
                        res = true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return res;
    }
    // 检测input输入框传递过来的值是否包含'<>/'，并返回Boolean
    // regExpTypeName: 该字段表示用哪个正则进行判断，可选值为['pattern', 'patternSimple']
    checkInputValFn(val = '', regExpTypeName = 'pattern') {
        return val.search(this[regExpTypeName]) > -1;
    }
    // 根据key值获取图片src
    getPictureSrc(resourceKey) {
        return `${pictureAPIHost}/${resourceKey}`;
    }
    // 根据key值和大小（默认200*200）获取图片缩略图src
    getPictureThumbnailSrc(resourceKey, size = 200) {
        return `${pictureAPIHost}/${resourceKey}-${size}`;
    }
    // 清除页面cookie
    removeCookieFn() {
        VueCookie.delete('loginDatas');
        VueCookie.delete('loginStatus');
        VueCookie.delete('userbelong');
        VueCookie.delete('userSystemType');
        VueCookie.delete('userPasswordCycle');
        VueCookie.delete('userInnerSn');
        VueCookie.delete('EmployeeInfo');
        VueCookie.delete('UserInfo');
        VueCookie.delete('bjyixiu');
    }
    // 页面的正则
    regExpFn() {
        return {
            socialCreditCode: /^(\d|[a-zA-Z]){18}$/, // 统一社会信用码
            mobilePhone: /^1\d{10}$/, // 手机号码： mobilePhone = /^1\d{10}$/
            email: /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/, // 邮箱
            password: /^(?=.*[a-zA-Z])(?=.*\d)[\s\S]{6,16}$/ // 密码
        };
    }
    // 导出excel表格
    exportExcel(thData, tdData, footerData, formData) {
        exportExcel.toExcel(thData, tdData, footerData, formData);
    }

    // 判断是不是小数
    checkFloatNumFn(num) {
        return (num + '').indexOf('.') !== -1;
    }

    checkPdf(file) {
        if (!file) return false;
        if (typeof file === 'undefined' || file.length === 0) {
            return false;
        } else if (file.substring(file.length - 3) === 'pdf') {
            return true;
        } else {
            return false;
        }
    }

    // 对比两个数组中 每一项共有的属性是否相同
    // 用于编辑弹框中 检查新旧列表是否有修改的操作
    checkListChanged(newList = [], oldList = []) {
        if (!Array.isArray(newList) || !Array.isArray(oldList)) return;

        let changed = false;
        if (newList.length === oldList.length) {
            // 检查列表每一项是否有变化
            newList.forEach((newItem, index) => {
                const newKeys = Object.keys(newItem);
                const oldItem = oldList[index];
                for (let key of newKeys) {
                    const newValue = newItem[key];
                    const oldValue = oldItem[key]; // 原始值
                    if (Object.hasOwnProperty.call(oldItem, key) && newValue !== oldValue) {
                        changed = true;
                        // console.log(key)
                        // console.log(oldValue)
                        // console.log(newValue)
                        break;
                    }
                }
            });
        } else {
            changed = true;
        }

        return changed;
    }

    // 乘法函数，用来得到精确的乘法结果
    accMul(arg1, arg2) {
        let m = 0;
        let s1 = arg1.toString();
        let s2 = arg2.toString();
        try {
            if (s1.includes('.')) {
                m += s1.split('.')[1].length;
            }
        } catch (e) {
            console.error(e);
        }
        try {
            if (s2.includes('.')) {
                m += s2.split('.')[1].length;
            }
        } catch (e) {
            console.error(e);
        }
        return (Number(s1.replace('.', '')) * Number(s2.replace('.', ''))) / Math.pow(10, m);
    }
    // 加法函数，用来得到精确的加法结果
    accAdd(arg1, arg2) {
        let r1 = 0;
        let r2 = 0;
        let m;
        let s1 = arg1.toString();
        let s2 = arg2.toString();
        try {
            if (s1.split('.')[1] !== undefined) {
                r1 = s1.split('.')[1].length;
            }
        } catch (e) {
            console.error(e);
        }

        try {
            if (s2.split('.')[1] !== undefined) {
                r2 = s2.split('.')[1].length;
            }
        } catch (e) {
            console.error(e);
        }
        m = Math.pow(10, Math.max(r1, r2));
        return (this.accMul(arg1, m) + this.accMul(arg2, m)) / m;
    }

    /**
     * 添加完产品之后，Table需要滚动到最新添加的产品位置
     * @param tableRefName vxe-table/vxe-grid的ref
     * @param instance Vue组件实例
     */
    scrollToAddedRow(tableRefName, instance) {
        if (instance && instance.$refs && instance.$refs[tableRefName]) {
            const refs = instance.$refs;
            const table = Array.isArray(refs[tableRefName]) ? refs[tableRefName][0] : refs[tableRefName];
            const { tableData } = table.getTableData();
            let timer = setTimeout(() => {
                // 38：表格行的高度
                table.scrollTo(0, tableData.length * 38);
                clearTimeout(timer);
                timer = null;
            }, 800);
        }
    }

    /**
     * 数组平面化
     * @param {*} array
     */
    flatArrayWithChildren(array) {
        if (!array || array.length === 0) {
            console.log('utils/flatArrayWithChildren recieves an invalid object');
            return [];
        }
        let flatten = [];
        function recur(arr) {
            arr.forEach((element) => {
                flatten.push(element);

                if (element.children && element.children.length !== 0) {
                    recur(element.children);
                }
            });
        }
        recur(array);
        return flatten;
    }

    /**
     * 将单位树转换为数组
     * ** 注意：单位树中父节点对应小单位，子节点对应大单位
     * @param {array} units 单位树数组
     * @param {string} key 单位id的键
     * @param {string} parentKey 父级单位id的键
     * @param {string} nameKey 单位名称的键
     * @param {string} measureKey 单位换算倍率（相较于父单位的倍率）的键
     * @typedef Option
     * @type {object}
     * @property {string} label 文本化标签
     * @property {string} name 单位名称
     * @property {string} value 单位id
     * @returns {Option[]}
     */
    parseUnitTreeToArray(
        units,
        key = 'unit_inner_sn',
        parentKey = 'parent_unit_inner_sn',
        nameKey = 'name',
        measureKey = 'measure'
    ) {
        const unitMap = new Map();
        const result = [];
        const recursion = (nodes) => {
            for (let i = 0, l = nodes.length; i < l; i += 1) {
                unitMap.set(nodes[i][key], nodes[i]);
                const parent = unitMap.get(nodes[i][parentKey]);
                let label;
                if (parent) {
                    label = `${nodes[i][nameKey]}（1${nodes[i][nameKey]} = ${nodes[i][measureKey]} x ${parent[nameKey]}）`;
                } else {
                    label = nodes[i][nameKey];
                }
                result.push({
                    label,
                    name: nodes[i][nameKey],
                    value: nodes[i][key]
                });
                if (nodes[i].children && nodes[i].children.length > 0) {
                    recursion(nodes[i].children);
                }
            }
        };
        recursion(units);

        return result;
    }

    /**
     * 将后端的树形数据转换为AntD Cascader级联选择所需的数据格式
     * @abstract 转换为{ value:'', label:'', children: [] }形式
     * @param {Array} tree 输入的原始树形数组数据
     * @param {String} valueText 将作为Value项的数据名称
     * @param {String} labelText 将作为Label项的数据名称
     */
    TreeToCascader(tree, valueText, labelText) {
        function recur(arr) {
            return arr.map((item) => {
                if (item.children.length !== 0) {
                    item.children = recur(item.children);
                } else {
                    item.children = null;
                }
                return {
                    value: item[`${valueText}`],
                    label: item[`${labelText}`],
                    children: item.children
                };
            });
        }
        tree = recur(tree);

        return tree;
    }

    /**
     * 返回当前时间
     * @returns {string(date-time)} 当前时间
     */
    getNowFormatDate() {
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = '0' + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = '0' + strDate;
        }
        var currentdate = year + '-' + month + '-' + strDate;
        return currentdate;
    }

    /**
     * 将阿拉伯数字转换为中文大写数字
     * @param {Number} n 需要转换的数字
     */
    moneyToCapital(n) {
        if (n === 0) {
            return '零';
        }
        if (!/^(0|[1-9]\d*)(\.\d+)?$/.test(n)) {
            return '';
        }
        let unit = '仟佰拾亿仟佰拾万仟佰拾元角分';
        let str = '';
        n += '00';
        let p = n.indexOf('.');
        if (p >= 0) {
            n = n.substring(0, p) + n.substr(p + 1, 2);
        }
        unit = unit.substr(unit.length - n.length);
        for (let i = 0, l = n.length; i < l; i++) {
            str += '零壹贰叁肆伍陆柒捌玖'.charAt(n.charAt(i)) + unit.charAt(i);
        }
        return str
            .replace(/零(仟|佰|拾|角)/g, '零')
            .replace(/(零)+/g, '零')
            .replace(/零(万|亿|元)/g, '$1')
            .replace(/(亿)万|壹(拾)/g, '$1$2')
            .replace(/^元零?|零分/g, '')
            .replace(/元$/g, '元整');
    }

    /**
     * 文件下载，常用于post请求文件流进行下载的场景
     * @param {Blob} blob 二进制文件数据
     * @param {String} filename 文件名
     */
    download(blob, filename) {
        const url = window.URL.createObjectURL(new Blob([blob]));
        const a = document.createElement('a');
        a.download = filename;
        a.href = url;
        a.click();
        window.URL.revokeObjectURL(a.href);
    }

    /**
     * 在响应头里获取文件名称
     * @param {*} headers
     * @returns
     */
    getFilenameByResHeader(headers, extName = 'xlsx') {
        const contentDisposition = headers['content-disposition'] || '';
        const matchedFilenames = contentDisposition.match(/fileName=(.*)/);
        const filename =
            matchedFilenames.length > 1
                ? decodeURIComponent(matchedFilenames[1])
                : `${moment().format('YYYY-MM-DD')}.${extName}`;

        return filename;
    }

    /**
     * 拼接 params 参数 [注：该方法主要是解决api名称相同且请求方式相同、参数不一致的情况]
     * @param {object} params  {id: 8, partB: 77}
     * @return {string}        '?id=8&partB=77'
     */
    joinParamsFn = (params) => {
        let newParams = '';
        if (params && Object.keys(params).length) {
            for (let key in params) {
                let objItem = params[key];
                if (newParams) {
                    newParams += `&${key}=${objItem}`;
                } else {
                    newParams += `?${key}=${objItem}`;
                }
            }
        }
        return newParams;
    };

    /**
     * 去掉单位括号内容
     * @abstract 由于单位加上括号内换算内容，对后端数据库来说过长，所以精简单位，去掉括号内容返回后端
     * @example g(100xmg) => g
     * @param {String} originalUnit
     * @return {String} caculated_unit
     */
    UnitBracketsTrim(originalUnit) {
        var index = originalUnit.indexOf('(');
        if (index === -1) {
            return originalUnit;
        } else {
            return originalUnit.substring(0, index);
        }
    }

    /**
     * 将文件名中的后缀名转化为小写
     * @param {string} fileName 文件名称
     * @return {String} 返回文件名称
     */
    formatFileNameToLowerCase(fileName) {
        if (fileName) {
            let sufixNameIndex = fileName.lastIndexOf('.');
            if (sufixNameIndex !== -1) {
                let prefixName = fileName.substring(0, sufixNameIndex),
                    sufixName = fileName.substring(sufixNameIndex, fileName.length).toLowerCase();
                return `${prefixName}${sufixName}`;
            }
            return fileName;
        } else {
            return fileName;
        }
    }
}

export default new Utils();
