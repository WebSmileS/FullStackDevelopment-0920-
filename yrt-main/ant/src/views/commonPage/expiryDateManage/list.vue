<template>
    <div class="scoped scoped-white">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button type="primary" size="small" @click="changeWarningSetIsOpenFn(true)">
                                预警设置
                            </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12"></a-col>
            </a-row>
        </div>
        <!--头部区域结束-->

        <!-- tab切换区域开始 -->
        <div
            :class="hightSearchObj.isArrow ? 'top136' : 'top56'"
            class="tab-table-wrap cur-overflow-hidden tab-table-overflowy"
        >
            <a-tabs type="card" v-model="tabsActiveKey">
                <a-tab-pane :tab="tabPane(0)" style="padding: 8px" :key="tabPane(0, 'value')">
                    <early-warning-table
                        v-show="tabsActiveKey === tabPane(0, 'value')"
                        :refName="tabPane(0, 'value')"
                        :isRefresh="isCERTIFICATIONEARLYRefresh"
                        :scrollYNumber="scrollYNumber"
                    ></early-warning-table>
                </a-tab-pane>
                <a-tab-pane :tab="tabPane(1)" style="padding: 8px" :key="tabPane(1, 'value')">
                    <early-warning-table
                        v-show="tabsActiveKey === tabPane(1, 'value')"
                        :refName="tabPane(1, 'value')"
                        :isRefresh="isPRODUCTREGISTRATIONRefresh"
                        :scrollYNumber="scrollYNumber"
                    ></early-warning-table>
                </a-tab-pane>
                <a-tab-pane v-if="!isPlatform" :tab="tabPane(2)" style="padding: 8px" :key="tabPane(2, 'value')">
                    <early-warning-table
                        v-show="tabsActiveKey === tabPane(2, 'value')"
                        :refName="tabPane(2, 'value')"
                        :isRefresh="isPRODUCTEXPIRYRefresh"
                        :scrollYNumber="scrollYNumber"
                    ></early-warning-table>
                </a-tab-pane>
                <a-tooltip slot="tabBarExtraContent" placement="left">
                    <template slot="title">
                        <span>刷新</span>
                    </template>
                    <a-button
                        type="primary"
                        shape="circle"
                        icon="reload"
                        size="small"
                        class="refresh-btn"
                        @click="initTableFn"
                    />
                </a-tooltip>
            </a-tabs>
        </div>
        <!-- tab切换区域结束 -->

        <!-- 预警设置区域开始 -->
        <warning-set
            :isOpen="isWarningSetOpen"
            :tabsActiveKey="tabsActiveKey"
            @closeWarningSet="changeWarningSetIsOpenFn(false)"
            @saveWarningSet="saveWarningSetFn"
        ></warning-set>
        <!-- 预警设置区域结束 -->
    </div>
</template>

<script>
import earlyWarningTable from './module/earlyWarningTable';
import warningSet from './module/warningSet';
import { expiryDateWarning } from '@/config/constants';
import { tabPane } from './mixins/tabPane.js';
import { identification } from './mixins/identification.js';

export default {
    name: 'list', // 资证效期预警 - 列表页面
    components: {
        earlyWarningTable,
        warningSet
    },
    data() {
        return {
            searchFormObj: {
                partB_name: '',
                order_sn: '',
                buyer_id: '',
                department_id: '',
                createTimeRange: [] // 订单生成时间区间（空数组或[moment, moment]）
            },
            hightSearchObj: {
                isArrow: false // 是否展开高级搜索的状态标识
            },
            isWarningSetOpen: false,
            tabsActiveKey: expiryDateWarning[0].value,
            isCERTIFICATIONEARLYRefresh: false,
            isPRODUCTREGISTRATIONRefresh: false,
            isPRODUCTEXPIRYRefresh: false
        };
    },
    methods: {
        // changeTabsActiveKey (activeKey) {
        //     this.tabsActiveKey = activeKey
        // },
        changeWarningSetIsOpenFn(isOpen) {
            this.isWarningSetOpen = isOpen;
        },
        saveWarningSetFn(dataObj) {
            this.changeWarningSetIsOpenFn(dataObj.isOpen);
        },
        initTableFn() {
            this[`is${this.tabsActiveKey}Refresh`] = true;
            let timer = setTimeout(() => {
                clearTimeout(timer);
                this[`is${this.tabsActiveKey}Refresh`] = false;
            }, 500);
        }
    },
    mixins: [tabPane, identification],
    computed: {
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                topHeight = this.hightSearchObj.isArrow ? 214 : 154,
                pager = 52;
            return clientHeight - topHeight - pager;
        }
    }
};
</script>

<style lang="less" scoped>
@w: 24px;
.order-modal-list {
    //   position: absolute;
    //   left: @w;
    //   right: @w;
    //   bottom: 57px;
    display: flex;
    flex-direction: row;

    li {
        display: flex;
        flex-direction: row;
        padding: 24px 0;

        &:nth-of-type(1),
        &:nth-of-type(2) {
            flex: 1;

            .item-wrap {
                flex: 1;
            }
        }

        .item-wrap {
            display: flex;
            padding-right: 16px;

            .item {
                padding-bottom: 0;
                padding-right: @w;
            }
        }

        &:last-child {
            .item-wrap {
                &:last-child {
                    padding-right: 0;
                }
            }
        }
    }
}
.modal-form-input-scoped .modal-form-input .top-list .label {
    display: flex;
    width: auto;
    max-width: 94px;
}
// 顶部按钮多的样式重新调整左右间距
.scoped .btn-top-wrap .btn-list-wrap li,
.scoped .btn-top-wrap .btn-list-wrap li .ant-btn {
    margin-right: 10px;
}
// 高级搜索栏
ul#searchForm {
    li.search-form-item {
        width: auto;
        min-width: 280px;
        &.search-button {
            min-width: auto;
        }
        span.label,
        input.ant-input {
            display: inline-block;
        }
        span.label {
            min-width: 72px;
        }
    }
}
</style>
