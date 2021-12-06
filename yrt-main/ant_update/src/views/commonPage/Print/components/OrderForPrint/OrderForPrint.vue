<template>
    <div class="order-for-print">
        <!-- 整页视图 -->
        <div v-show="entireViewShow">
            <div
                class="order-for-print-order-wrapper"
                :style="{
                    ...pageWrapperStyle,
                    height: entireViewShow && !tableFormatter.pageSplit ? 'auto' : pageWrapperStyle.height
                }"
            >
                <div ref="container" class="order-for-print-order">
                    <div>
                        <slot name="header" :records="contentData.tableData" :page="1" :pageTotal="1"></slot>
                    </div>
                    <div>
                        <slot name="table" :records="contentData.tableData" :page="1" :pageTotal="1"></slot>
                    </div>
                    <div>
                        <slot name="footer" :records="contentData.tableData" :page="1" :pageTotal="1"></slot>
                    </div>
                </div>
            </div>
        </div>
        <!-- 分页视图，依赖于整页视图的第一次渲染 -->
        <div v-show="tableFormatter.pageSplit">
            <div
                v-for="(pageData, pageIndex) in pagesData"
                :key="pageIndex"
                class="order-for-print-order-wrapper multiple-page"
                :style="pageWrapperStyle"
            >
                <div class="order-for-print-order">
                    <slot name="header" :records="pageData" :page="pageIndex + 1" :pageTotal="pagesData.length"></slot>
                    <slot name="table" :records="pageData" :page="pageIndex + 1" :pageTotal="pagesData.length"></slot>
                    <slot name="footer" :records="pageData" :page="pageIndex + 1" :pageTotal="pagesData.length"></slot>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { unitTypeMap } from '../../type';

/**
 * 打印固定格式(头 + 表格 + 脚，头和脚会出现在每一页中，表格的数据行被分页)的单据时，提供自动排版和分页功能
 */
export default {
    name: 'OrderForPrint',
    inject: ['baseConfig', 'tableFormatter', 'contentData'],
    data() {
        return {
            entireViewShow: true,
            pagesData: []
        };
    },
    computed: {
        pageWrapperStyle() {
            const {
                paperWidth,
                paperHeight,
                unit,
                safeMarginTop,
                safeMarginBottom,
                safeMarginLeft,
                safeMarginRight,
                paddingLeft,
                paddingRight,
                paddingTop,
                paddingBottom
            } = this.baseConfig;

            let printWidth = paperWidth - safeMarginLeft - safeMarginRight;
            let printHeight = paperHeight - safeMarginTop - safeMarginBottom;

            // 由于chrome浏览器（firefox则能正确的匹配高度）在计算视图时，会将长度单位换算为像素，存在小数位的舍入情况，
            // 所以内容区的高度需要减去一个单位的高度，使其实际渲染的高度略小于打印纸的高度,
            // 防止同一页的内容打印预览计算时被自动插入分页符
            if (unit === unitTypeMap.mm.value) {
                printHeight -= 1;
            }
            if (unit === unitTypeMap.cm.value) {
                printHeight -= 0.1;
            }

            return {
                width: `${printWidth}${unit}`,
                height: `${printHeight}${unit}`,
                overflow: 'hidden',
                paddingTop: `${paddingTop}${unit}`,
                paddingRight: `${paddingRight}${unit}`,
                paddingBottom: `${paddingBottom}${unit}`,
                paddingLeft: `${paddingLeft}${unit}`,
                pageBreakAfter: 'always'
            };
        }
    },
    watch: {
        /**
         * 基础配置发生变化时，更新视图
         */
        baseConfig: {
            deep: true,
            handler() {
                this.handleUpdateView();
            }
        },
        /**
         * 表格配置发生变化时，更新视图
         */
        tableFormatter: {
            deep: true,
            handler() {
                this.handleUpdateView();
            }
        },
        /**
         * 表格数据发生变化时，更新视图
         */
        contentData: {
            deep: true,
            handler() {
                this.handleUpdateView();
            }
        }
    },
    methods: {
        /**
         * 根据未分页视图的渲染结果计算分页信息
         * @returns {number[]}
         */
        calculateSplitInfo() {
            const container = this.$refs.container;
            const maxCountPerPage = Math.max(this.tableFormatter.maxRecordCountPerPage, 1);
            const [header, tableContainer, footer] = container.childNodes;
            const table = tableContainer.childNodes[0];
            const rowHeights = []; // 表格各行高度
            const indexForSplit = []; // 每一页起点记录(即第一条记录)对应的数组序号
            const usableContentHeight = container.clientHeight;
            const headerHeight = header.clientHeight;
            const footerHeight = footer.clientHeight;
            table.childNodes.forEach((_) => rowHeights.push(_.clientHeight));
            const tableHeaderHeight = rowHeights.shift(); // 表格第一行为表头
            const tableSummationHeight = rowHeights.pop(); // 表格最后一行为合计栏
            const heightForTableRows =
                usableContentHeight - headerHeight - footerHeight - tableHeaderHeight - tableSummationHeight;
            let stackHeight = 0;
            let stackLength = 0;
            indexForSplit.push(0);
            for (let i = 0, l = rowHeights.length; i < l; i += 1) {
                let h = rowHeights.shift();
                let currentStackHeight = stackHeight + h;
                let currentStackLength = stackLength + 1;
                if (currentStackLength > maxCountPerPage || currentStackHeight > heightForTableRows) {
                    if (i !== 0) {
                        indexForSplit.push(i);
                    }
                    stackHeight = h;
                    stackLength = 1;
                    continue;
                }
                stackHeight = currentStackHeight;
                stackLength = currentStackLength;
            }
            return indexForSplit;
        },
        /**
         * 根据已渲染的未分页视图，异步地进行分页
         */
        handleSplitPage() {
            // 节流，避免短时间内的多次分页调用
            clearTimeout(this.splitPageTimer);
            this.splitPageTimer = setTimeout(async () => {
                // 渲染未分页的完整视图
                this.entireViewShow = true;

                // 等待渲染完成
                await this.$nextTick();

                // 进行分页
                const indexArray = this.calculateSplitInfo();
                const pagesData = splitPageWithIndex(indexArray, this.contentData.tableData);
                this.pagesData = pagesData;

                // 隐藏未分页的完整视图
                if (this.tableFormatter.pageSplit) {
                    this.entireViewShow = false;
                }
            }, 10);
        },

        /**
         * 更新视图
         */
        handleUpdateView() {
            // 若配置中开启分页，则进行分页更新
            // 否则显示完整视图
            if (this.tableFormatter.pageSplit) {
                this.handleSplitPage();
            } else {
                this.entireViewShow = true;
            }
        }
    },
    mounted() {
        if (this.tableFormatter.pageSplit) {
            this.handleSplitPage();
        }
    }
};

/**
 * 根据分页序号划分各页的数据
 * @param {number[]} indexForSplit 分页序号数组，序号代表一页数据的起始序号
 * @param {object[]} dataArrayForSplit 数据数组
 * @returns {object[][]} 分页后数据数组
 */
function splitPageWithIndex(indexForSplit, dataArrayForSplit) {
    const pagesData = [];
    let prevIndex = 0;
    for (let i = 1, l = indexForSplit.length; i < l; i++) {
        pagesData.push(dataArrayForSplit.slice(prevIndex, indexForSplit[i]));
        prevIndex = indexForSplit[i];
    }
    pagesData.push(dataArrayForSplit.slice(prevIndex));
    return pagesData;
    // const indexStack = [...indexForSplit]
    // const pagesData = []
    // let pageData = []
    // for (let i = 0, l = dataArrayForSplit.length; i < l; i++) {
    //     if (i === indexStack[0]) {
    //         pagesData.push(pageData)
    //         pageData = [dataArrayForSplit[i]]
    //         indexStack.shift()
    //         continue
    //     }
    //     pageData.push(dataArrayForSplit[i])
    // }
    // if (pageData.length > 0) {
    //     pagesData.push(pageData)
    // }
    // return pagesData
}
</script>

<style lang="less">
.order-for-print-order-wrapper {
    margin-top: 24px;
    background-color: #fff;
    &:first-child {
        margin-top: 0;
    }
}
@media print {
    .order-for-print-order-wrapper {
        margin: 0;
    }
}
div.order-for-print-order {
    @colorBlack: #000000;
    width: 100%;
    height: 100%;
    color: @colorBlack;
    h1,
    h2,
    h3,
    h4,
    h5,
    h6,
    p,
    div,
    span {
        color: @colorBlack;
    }
    table {
        border-color: @colorBlack;
    }
    @page {
        margin-top: 0;
        margin-bottom: 0;
        margin-left: 0;
        margin-right: 0;
    }
}
</style>

<style lang="less">
/* 出入库单的打印样式 */
// header
div.order-for-print-order {
    .title {
        margin-top: 0;
        margin-bottom: 0;
    }
    .sub-title {
        text-align: right;
        margin-bottom: 0;
    }
    .table-header {
        @headerHeight: 3.6mm;
        display: flex;
        justify-content: space-between;
        flex-wrap: wrap;
        min-height: @headerHeight;
        margin-top: 0;
        margin-bottom: 0;
        > div {
            min-height: @headerHeight;
            text-align: left;
        }
    }
}

// footer
div.order-for-print-order {
    .float-wrapper {
        &:before,
        &:after {
            content: '';
            display: block;
            clear: both;
        }
    }
    .float-wrapper-left {
        > div {
            float: left;
        }
    }
    .float-wrapper-right {
        > div {
            float: right;
        }
    }
    div.footer1,
    div.footer2,
    div.footer3,
    div.footer4 {
        margin-bottom: 0;
    }
    div.footer1 {
        display: flex;
        .left-wrapper {
            flex-grow: 1;
            > div {
                margin-right: 1.5em;
            }
            > div:last-child {
                margin-right: 0;
            }
        }
        .underline {
            display: inline-block;
            border-bottom: 1px solid #000;
        }
        .underline-null {
            min-width: 4.5em;
        }
        div.pager {
            min-width: 6em;
            text-align: right;
        }
    }
    div.footer2,
    div.footer4 {
        text-align: left;
    }
    div.footer3 {
        display: flex;
        .float-wrapper {
            text-align: left;
        }
        .float-wrapper-right {
            flex-grow: 1;
            text-align: right;
            > div {
                margin-left: 1em;
            }
        }
    }
}

// table
div.order-for-print-order {
    table {
        margin-top: 0;
        margin-bottom: 0;
    }
    td {
        word-break: break-word;
    }
}
</style>
