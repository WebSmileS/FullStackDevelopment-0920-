import Vue from 'vue';
import { paperTypeMap, paperSizeMap, alignTypeMap, verticalAlignTypeMap } from '../../type';

/**
 * @typedef {Object} Column 列配置
 * @property {string} title 列名
 * @property {string} dataIndex 列数据标识
 * @property {string} [alias] 列别名
 * @property {string} [align] 列对齐方式
 * @property {boolean} [enable] 是否启用列
 * @property {number} [width] 列宽
 * @property {number} [precision] 列数值精度
 * @property {boolean} [isKeyColumn] 是否为关键列，关键列在“自定义列”的编辑表格中会被优先渲染，方便用户识别行数据
 * @property {boolean} [isCustomColumn] 是否为自定义列
 * @property {string} [customKey] 自定义列的唯一标识，当列为自定义列时，此项必须有值
 */

/**
 *
 * @param {object} sourceData 拷贝的源数据
 * @param {object | undefined} receiver 接收拷贝数据的对象
 * @returns {object} 接受拷贝数据的对象
 */
function copyTo(sourceData, receiver = {}) {
    const stack = Object.keys(sourceData).map((key) => ({ root: receiver, key, source: sourceData[key] }));
    do {
        let current = stack.shift();
        if (current === undefined) {
            break;
        }
        if (typeof current.source === 'object') {
            // null
            if (current.source === null) {
                current.root[current.key] = null;
                continue;
            }
            // Array
            if (current.source.constructor === Array) {
                if (current.root[current.key] && current.root[current.key].splice) {
                    current.root[current.key].splice(0);
                } else {
                    current.root[current.key] = [];
                }
                for (let i = 0, l = current.source.length; i < l; i += 1) {
                    stack.push({
                        root: current.root[current.key],
                        key: i,
                        source: current.source[i]
                    });
                }
                continue;
            }
            // object
            if (!current.root[current.key]) {
                Vue.set(current.root, current.key, {});
            }
            Object.keys(current.source).forEach((key) => {
                stack.push({
                    root: current.root[current.key],
                    key,
                    source: current.source[key]
                });
            });
            continue;
        }
        // undefined, number, string, function
        Vue.set(current.root, current.key, current.source);
    } while (stack.length !== 0);

    return receiver;
}

const _defaultBaseConfig = Object.freeze({
    // 打印
    paperType: paperTypeMap.entire.value,
    paperWidth: paperSizeMap.entire.width,
    paperHeight: paperSizeMap.entire.height,
    unit: paperSizeMap.entire.unit,
    // 打印安全距离用于设置实际打印纸面可打印区域的大小
    // 比如有的打印纸左右两边是装订区域，不属于打印区域
    // 或者打印机实际可打印的宽度比纸面要小，
    // 此时的打印内容区宽高比纸张的实际宽高要小
    safeMarginTop: 0,
    safeMarginBottom: 0,
    safeMarginLeft: 15.5,
    safeMarginRight: 15.5,

    // 版面
    paddingLeft: 5,
    paddingRight: 5,
    paddingTop: 5,
    paddingBottom: 0,

    // 标题
    titleFontSize: 5,
    titleLineHeight: 6,
    titleFontBold: true,
    titleLetterSpacing: 1.5,

    // 正文
    bodyFontSize: 3.2,
    bodyLineHeight: 5,
    bodyFontBold: false,
    bodyLetterSpacing: 0,

    // 数值设置
    precision: 2,
    thousandSeparator: true
});

/* 基础设置 */
const _baseConfig = Vue.observable({ ..._defaultBaseConfig });

/* 编辑态基础设置 */
const _baseConfigEditing = Vue.observable({ ..._defaultBaseConfig });

/* 默认表格设置 */
const _defaultTableFormatter = Object.freeze({
    // 表格分页
    pageSplit: true,
    maxRecordCountPerPage: 5,

    // 表格格式
    columns: [],
    tableBorder: 1,
    tableMarginTop: 2.5,
    tableMarginBottom: 2.5,

    // 单元格
    columnPaddingLeft: 0.8,
    columnPaddingRight: 0.8,
    columnPaddingTop: 0.8,
    columnPaddingBottom: 0.8,
    columnAlign: alignTypeMap.center.value,
    columnVerticalAlign: verticalAlignTypeMap.middle.value,
    columnFontSize: 2.8,
    columnFontBold: false,

    // 表头
    headPaddingLeft: 0,
    headPaddingRight: 0,
    headPaddingTop: 0,
    headPaddingBottom: 0,
    headAlign: alignTypeMap.center.value,
    headVerticalAlign: verticalAlignTypeMap.middle.value,
    headFontSize: 3,
    headFontBold: true,

    // 合计行
    summationPaddingLeft: 1,
    summationPaddingRight: 1,
    summationPaddingTop: 1,
    summationPaddingBottom: 1,
    summationAlign: alignTypeMap.left.value,
    summationVerticalAlign: verticalAlignTypeMap.middle.value,
    summationFontSize: 3,
    summationFontBold: true,
    summationSpans: '5/7',

    // 业务模块进行初始化时应该设置此项配置
    controledFields: {
        // 需要控制精度的列
        precision: {},
        // 需要控制千位分隔符的列
        thousandSeparator: {},
        // 需要控制正负的列
        negative: {}
    }
});

/* 表格设置 */
const _tableFormatter = Vue.observable({
    ..._defaultTableFormatter,
    /** @type {Column[]} */
    columns: []
});

/* 编辑态的表格设置 */
const _tableFormatterEditing = Vue.observable({
    ..._defaultTableFormatter,
    /** @type {Column[]} */
    columns: []
});

/* 打印内容数据 */
const _contentData = Vue.observable({
    tableData: []
});

/* 编辑态的打印内容数据 */
const _contentDataEditing = Vue.observable({
    infoData: {},
    tableData: []
});

/* 默认其他设置 */
const _defaultOtherConfig = {
    autoSync: true,
    // 业务 保留负值
    keepNegative: false
};

/* 其他设置 */
const _otherConfig = Vue.observable({ ..._defaultOtherConfig });

/* 编辑态其他设置 */
const _otherConfigEditing = Vue.observable({ ..._defaultOtherConfig });

const printEditor = Vue.observable({
    configVersion: '1.1',
    prevConfigVersion: '1.0',
    moduleKey: 'default',
    moduleConfigVersion: '1.0',
    moduleConfigPrevVersion: '0.0',
    visible: true,
    initialized: false,

    baseConfigSync: true,
    baseConfigInitialized: false,

    tableFormatterSync: true,
    tableFormatterInitialized: false,

    otherConfigSync: true,
    otherConfigInitialized: false,

    contentDataSync: true,
    contentDataInitialized: false,

    /**
     * 设置编辑态的数据
     * @param {object} options
     * @param {object | undefined} options.baseConfig 基础设置
     * @param {object | undefined} options.tableFormatter 表格设置
     * @param {object | undefined} options.otherConfig 其他设置
     * @param {object | undefined} options.contentData 打印内容
     */
    handleSetDataEditing({ baseConfig, tableFormatter, otherConfig, contentData } = {}) {
        if (baseConfig) {
            copyTo(baseConfig, _baseConfigEditing);
            printEditor.baseConfigSync = false;
        }
        if (tableFormatter) {
            copyTo(tableFormatter, _tableFormatterEditing);
            printEditor.tableFormatterSync = false;
        }
        if (otherConfig) {
            copyTo(otherConfig, _otherConfigEditing);
            printEditor.otherConfigSync = false;
        }
        if (contentData) {
            copyTo(contentData, _contentDataEditing);
            printEditor.contentDataSync = false;
        }
    },

    /**
     * 同步配置数据
     */
    handleSyncData() {
        if (!printEditor.baseConfigSync) {
            copyTo(_baseConfigEditing, _baseConfig);
            printEditor.baseConfigSync = true;
        }
        if (!printEditor.tableFormatterSync) {
            copyTo(_tableFormatterEditing, _tableFormatter);
            printEditor.tableFormatterSync = true;
        }
        if (!printEditor.otherConfigSync) {
            copyTo(_otherConfigEditing, _otherConfig);
            printEditor.otherConfigSync = true;
        }
        if (!printEditor.contentDataSync) {
            copyTo(_contentDataEditing, _contentData);
            printEditor.contentDataSync = true;
        }
    },

    /**
     * 初始化
     * @param {object} options
     * @param {string | undefined} options.moduleKey 模块标识，可用于区别不同功能模块不同的设置数据
     * @param {string | undefined} options.moduleConfigVersion 模块配置版本标识，可用于区别不同功能模块不同版本的设置数据
     * @param {string | undefined} options.moduleConfigPrevVersion 模块配置上一版本标识，可用于区别不同功能模块不同版本的设置数据
     * @param {object | undefined} options.baseConfig 基础设置
     * @param {object | undefined} options.tableFormatter 表格设置
     * @param {object | undefined} options.otherConfig 其他设置
     * @param {object | undefined} options.contentData 打印内容
     * @todo 打开页面时，子组件调用此方法进行数据初始化，载入本地已保存配置时同样调用了此方法，考虑合并逻辑
     */
    handleInitialize({
        moduleKey,
        moduleConfigVersion,
        moduleConfigPrevVersion,
        baseConfig,
        tableFormatter,
        otherConfig,
        contentData
    } = {}) {
        if (moduleKey) {
            printEditor.moduleKey = moduleKey;
        }
        if (moduleConfigVersion) {
            printEditor.moduleConfigVersion = moduleConfigVersion;
        }
        if (moduleConfigPrevVersion) {
            printEditor.moduleConfigPrevVersion = moduleConfigPrevVersion;
        }
        if (baseConfig) {
            printEditor.baseConfigInitialized = false;
            copyTo(baseConfig, _baseConfig);
            copyTo(baseConfig, _baseConfigEditing);
        }
        if (tableFormatter) {
            printEditor.tableFormatterInitialized = false;
            copyTo(tableFormatter, _tableFormatter);
            copyTo(tableFormatter, _tableFormatterEditing);
        }
        if (otherConfig) {
            printEditor.otherConfigInitialized = false;
            copyTo(otherConfig, _otherConfig);
            copyTo(otherConfig, _otherConfigEditing);
        }
        if (contentData) {
            printEditor.contentDataInitialized = false;
            copyTo(contentData, _contentData);
            copyTo(contentData, _contentDataEditing);
        }
        Vue.nextTick(() => {
            printEditor.baseConfigInitialized = true;
            printEditor.tableFormatterInitialized = true;
            printEditor.otherConfigInitialized = true;
            printEditor.contentDataInitialized = true;
        });
    }
});

export default {
    // 编辑器状态
    printEditor,

    // 初始基础配置
    defaultBaseConfig: _defaultBaseConfig,

    // 基础配置（更新频率较低，且每次更新都重新渲染打印内容视图）
    baseConfig: _baseConfig,

    // 编辑态的基础配置（更新频率较高，其更新时只应该影响编辑器视图，当值同步至baseConfig时才影响打印内容视图）
    baseConfigEditing: _baseConfigEditing,

    // 表格设置
    tableFormatter: _tableFormatter,

    // 编辑态的表格设置
    tableFormatterEditing: _tableFormatterEditing,

    // 打印数据
    contentData: _contentData,

    // 编辑态的打印数据
    contentDataEditing: _contentDataEditing,

    // 其他设置
    otherConfig: _otherConfig,

    // 编辑态其他设置
    otherConfigEditing: _otherConfigEditing
};
