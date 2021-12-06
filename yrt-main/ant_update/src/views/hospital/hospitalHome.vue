<template>
    <div class="hospital-home">
        <div class="hospital-home-header" :style="{ height: `${headerHeight}px` }">
            <a-row :gutter="8" type="flex" justify="space-around">
                <template v-for="itemInfo in headerInfoList">
                    <a-col
                        :span="6"
                        :key="itemInfo.key"
                        class="gutter-row"
                        :style="{ height: `${headerHeight - 16}px` }"
                    >
                        <div :class="itemInfo.class">
                            <a-row class="item-info-top">
                                <a-col :span="16" :style="{ fontSize: `${headerHeight / 3}px` }">
                                    <h5 :style="{ fontSize: '1em' }">{{ itemInfo.total }}</h5>
                                    <h6 :style="{ fontSize: '0.4em' }">{{ itemInfo.key }}</h6>
                                </a-col>
                                <a-col class="icon-wrapper" :span="8" :style="{ height: `${headerHeight - 24}px` }">
                                    <div
                                        class="icon-content"
                                        :style="{
                                            background: itemInfo.color,
                                            width: `${headerHeight * 0.5}px`,
                                            height: `${headerHeight * 0.5}px`,
                                            fontSize: `${headerHeight * 0.5}px`,
                                            lineHeight: `${headerHeight * 0.5}px`
                                        }"
                                    >
                                        <img :src="itemInfo.icon" :alt="itemInfo.key" />
                                    </div>
                                </a-col>
                            </a-row>
                            <div
                                :style="{
                                    'position': 'relative',
                                    'bottom': '0px',
                                    'background': itemInfo.color,
                                    'height': '8px',
                                    'width': '100%'
                                }"
                            ></div>
                        </div>
                    </a-col>
                </template>
            </a-row>
        </div>
        <div class="hospital-home-body">
            <a-row :gutter="8" type="flex" justify="space-around">
                <a-col :span="9" class="gutter-row">
                    <div class="hospital-home-echarts" :style="{ height: `${blockHeight}px` }">
                        <h5 class="home-echarts-header"
                            >全院耗材采购一览<span class="float-right">{{ new Date().getFullYear() }}年</span></h5
                        >
                        <div id="myCharts-hospital" class="my-charts"></div>
                    </div>
                </a-col>
                <a-col :span="9" class="gutter-row">
                    <div class="hospital-home-echarts" :style="{ height: `${blockHeight}px` }">
                        <h5 class="home-echarts-header"
                            >科室采购金额一览<span class="float-right">{{ new Date().getMonth() + 1 }}月</span></h5
                        >
                        <div id="myCharts-departments" class="my-charts"></div>
                    </div>
                </a-col>
                <a-col :span="6" class="gutter-row">
                    <div class="hospital-home-echarts" :style="{ height: `${blockHeight}px` }">
                        <h5 class="home-echarts-header">快捷入口</h5>
                        <div class="quick-enter-list">
                            <ul>
                                <li
                                    v-for="quickEntryStaicItem in quickEntryStaic"
                                    :key="quickEntryStaicItem.id"
                                    @click="quickEnter(quickEntryStaicItem.url)"
                                >
                                    <div class="enter-list-top" :style="{ 'background': quickEntryStaicItem.color }">
                                        <!-- <img :src="quickEntryStaicItem.img" alt=""> -->
                                        <i :class="quickEntryStaicItem.img" class="yrt-quick-enter"></i>
                                    </div>
                                    <p>{{ quickEntryStaicItem.text }}</p>
                                </li>
                            </ul>
                        </div>
                    </div>
                </a-col>
            </a-row>
            <a-row :gutter="8" type="flex" justify="space-around" style="margin-top: 8px; padding-bottom: 8px">
                <a-col :span="9" class="gutter-row">
                    <div class="hospital-home-echarts" :style="{ height: `${blockHeight}px` }">
                        <h5 class="home-echarts-header">我的待办</h5>
                        <div class="my-charts scroll-hidden">
                            <ul class="hospital-home-taskList">
                                <li v-for="taskItem in tasksList" :key="taskItem.id" class="hospital-home-taskItem">
                                    <span>{{ taskItem.typeName }}</span>
                                    <span>{{ taskItem.task }}</span>
                                    <span @click="detailInfo(taskItem.type)" class="home-taskItem-detail">详情 >></span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </a-col>
                <a-col :span="9" class="gutter-row">
                    <div class="hospital-home-echarts" :style="{ height: `${blockHeight}px` }">
                        <h5 class="home-echarts-header"
                            >供应商采购金额一览<span class="float-right">{{ new Date().getMonth() + 1 }}月</span></h5
                        >
                        <p class="my-charts-caption"
                            >当月累计采购金额<span class="text-red">{{ curMonthAmount | formatMoney }}万元</span></p
                        >
                        <div id="myCharts-shopping-suppliers" class="my-charts"></div>
                    </div>
                </a-col>
                <a-col :span="6" class="gutter-row">
                    <div class="hospital-home-echarts warning-center" :style="{ height: `${blockHeight}px` }">
                        <h5 class="home-echarts-header">预警中心</h5>
                        <ul class="caption-wrap">
                            <li>
                                <div>预警类型</div>
                                <div>预警内容</div>
                                <div>预警时间</div>
                            </li>
                        </ul>
                        <ul class="list-wrap" v-if="warningCenterAryList.length > 0">
                            <li
                                v-for="warningCenterAryListItem in warningCenterAryList"
                                :key="warningCenterAryListItem.type_id"
                            >
                                <div class="waring-til" v-if="warningCenterAryListItem.type_id === 1"> 合同预警 </div>
                                <div class="waring-til" v-else-if="warningCenterAryListItem.type_id === 2">
                                    商品预警
                                </div>
                                <div class="waring-til" v-else-if="warningCenterAryListItem.type_id === 3">
                                    证书预警
                                </div>
                                <div>
                                    {{ warningCenterAryListItem.cert_no }}
                                </div>
                                <div>
                                    {{ warningCenterAryListItem.alarm_at }}
                                </div>
                            </li>
                        </ul>
                        <div v-else class="text-grey-5"> 暂无数据 </div>
                    </div>
                </a-col>
            </a-row>
        </div>
    </div>
</template>

<script>
import * as echarts from 'echarts/core';
import { TitleComponent, TooltipComponent, GridComponent } from 'echarts/components';
import { BarChart, LineChart, PieChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
echarts.use([TitleComponent, TooltipComponent, GridComponent, BarChart, LineChart, PieChart, CanvasRenderer]);

export default {
    name: 'hospitalHome',
    data() {
        return {
            headerInfoList: [
                {
                    key: '可采购商品数量',
                    total: 1400,
                    icon: '/images/pic/home-kcg.png',
                    class: 'headerInfo-goods',
                    color: '#FFCA39'
                },
                {
                    key: '供应商数量',
                    total: 2,
                    icon: '/images/pic/home-gys.png',
                    class: 'headerInfo-supplier',
                    color: '#1890FF'
                },
                {
                    key: '待采购商品数量',
                    total: 0,
                    icon: '/images/pic/home-dcg.png',
                    class: 'headerInfo-buyGoods',
                    color: '#4CCE5B'
                },
                {
                    key: '执行中的采购计划',
                    total: 10,
                    icon: '/images/pic/home-zxz.png',
                    class: 'headerInfo-plans',
                    color: '#FF630B'
                }
            ],
            // 全院耗材采购DOM
            myChartHospital: '',
            // 全院耗材采购数据
            allHospitalOptionObj: {
                title: {
                    text: '单位：万',
                    textStyle: {
                        color: '#4F4F51',
                        fontStyle: 'normal',
                        fontWeight: 'normal',
                        fontSize: 12,
                        lineHeight: 30
                    },
                    top: 15,
                    left: 30
                },
                tooltip: {
                    trigger: 'axis'
                },
                xAxis: [
                    {
                        type: 'category',
                        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: '采购',
                        type: 'line',
                        data: [0.0, 3.3, 0.6, 1.5, 0.41, 1.1, 1.41],
                        markPoint: {
                            data: [
                                { type: 'max', name: '最大值' },
                                { type: 'min', name: '最小值' }
                            ]
                        },
                        markLine: {
                            data: [{ type: 'average', name: '平均值' }]
                        },
                        itemStyle: {
                            color: '#1890FF'
                        },
                        lineStyle: {
                            color: '#1890FF'
                        }
                    }
                ]
            },
            // 科室采购DOM节点
            myChartDeparments: '',
            // 科室采购金额数据
            deparmentsOptionObj: {
                title: {
                    text: '单位：万',
                    textStyle: {
                        color: '#4F4F51',
                        fontStyle: 'normal',
                        fontWeight: 'normal',
                        fontSize: 12,
                        lineHeight: 30
                    },
                    top: 15,
                    left: 30
                },
                tooltip: {
                    trigger: 'axis'
                },
                xAxis: [
                    {
                        type: 'category',
                        data: ['儿科', '内一科', '手术科', '妇产科', '内二科']
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: '采购',
                        type: 'bar',
                        data: [1.1, 2.34, 5.23, 6.07, 3.01],
                        markPoint: {
                            data: [
                                { type: 'max', name: '最大值' },
                                { type: 'min', name: '最小值' }
                            ]
                        },
                        markLine: {
                            data: [{ type: 'average', name: '平均值' }]
                        },
                        itemStyle: {
                            normal: {
                                color: function (params) {
                                    let colorList = [
                                        '#2493F8',
                                        '#FFCA39',
                                        '#FF630B',
                                        '#4CCE5B',
                                        '#2493F8',
                                        '#FFCA39',
                                        '#FF630B',
                                        '#4CCE5B',
                                        '#2493F8',
                                        '#FFCA39',
                                        '#FF630B',
                                        '#4CCE5B',
                                        '#FF630B',
                                        '#4CCE5B',
                                        '#2493F8',
                                        '#FFCA39',
                                        '#FF630B',
                                        '#4CCE5B'
                                    ];
                                    return colorList[params.dataIndex];
                                }
                            }
                        },
                        barWidth: 15
                    }
                ]
            },
            // 供应商采购金额DOM
            myChartShoppingSuppliers: '',
            // 供应商采购金额一览数据
            myChartShoppingSuppliersOptionObj: {
                title: {
                    text: '单位：万',
                    textStyle: {
                        color: '#4F4F51',
                        fontStyle: 'normal',
                        fontWeight: 'normal',
                        fontSize: 12,
                        lineHeight: 30
                    },
                    bottom: '5%',
                    left: '56%'
                },
                tooltip: {
                    trigger: 'item'
                },
                legend: {
                    orient: 'vertical',
                    type: 'scroll',
                    itemGap: 20,
                    itemWidth: 15,
                    itemHeight: 15,
                    top: '25%',
                    left: '3%',
                    textStyle: {
                        color: '#000',
                        fontSize: 14
                    },
                    data: ['无采购']
                },
                series: [
                    {
                        name: '供应商采购金额',
                        type: 'pie',
                        radius: '60%',
                        center: ['60%', '50%'],
                        data: [
                            { value: 20, name: '器材' },
                            { value: 30, name: '药品' },
                            { value: 40, name: '设备' }
                        ],
                        label: {
                            align: 'left',
                            normal: {
                                formatter(v) {
                                    let text = Math.round(v.percent) + '%' + '' + v.name;
                                    if (text.length <= 8) {
                                        return text;
                                    } else if (text.length > 8 && text.length <= 16) {
                                        text = `${text.slice(0, 8)}\n${text.slice(8)}`;
                                        return text;
                                    } else if (text.length > 16 && text.length <= 24) {
                                        text = `${text.slice(0, 8)}\n${text.slice(8, 16)}\n${text.slice(16)}`;
                                        return text;
                                    } else if (text.length > 24 && text.length <= 30) {
                                        text = `${text.slice(0, 8)}\n${text.slice(8, 16)}\n${text.slice(
                                            16,
                                            24
                                        )}\n${text.slice(24)}`;
                                        return text;
                                    } else if (text.length > 30) {
                                        text = `${text.slice(0, 8)}\n${text.slice(8, 16)}\n${text.slice(
                                            16,
                                            24
                                        )}\n${text.slice(24, 30)}\n${text.slice(30)}`;
                                        return text;
                                    }
                                },
                                textStyle: {
                                    fontSize: 12,
                                    lineHeight: 16,
                                    rich: {
                                        a: {
                                            // 没有设置 `lineHeight`，则 `lineHeight` 为 16
                                        }
                                    }
                                }
                            }
                        },
                        itemStyle: {
                            normal: {
                                color: function (params) {
                                    let colorList = [
                                        '#3AA0FF',
                                        '#4ECB73',
                                        '#FBD337',
                                        '#435188',
                                        '#975FE4',
                                        '#FFCA39',
                                        '#FF630B',
                                        '#4CCE5B',
                                        '#2493F8'
                                    ];
                                    return colorList[params.dataIndex];
                                }
                            }
                        }
                    }
                ]
            },
            // 当月采购总金额
            curMonthAmount: 0,
            // 快速入口
            quickEntryStaic: [
                {
                    id: 1,
                    color: '#FF630B',
                    text: '耗材申领',
                    img: 'yrt-haocaishenling',
                    url: '/hospital/SuppliesClaim',
                    name: 'SuppliesClaim'
                },
                {
                    id: 2,
                    color: '#4CCE5B',
                    text: '合同管理',
                    img: 'yrt-hetong',
                    url: '/commonView/contractList',
                    name: 'contractList'
                },
                {
                    id: 3,
                    color: '#FABA0B ',
                    text: '采购订单',
                    img: 'yrt-dingdan',
                    url: '/commonView/purchaseOrder',
                    name: 'purchaseOrder'
                },
                {
                    id: 4,
                    color: '#2493F8',
                    text: '库存管理',
                    img: 'yrt-kucun1',
                    url: '/commonView/inventory',
                    name: 'inventory'
                },
                {
                    id: 5,
                    color: '#FABA0B ',
                    text: '效期预警',
                    img: 'yrt-shixiaoyujing',
                    url: '/commonView/expiryDateManage',
                    name: 'purchaseOrder'
                },
                {
                    id: 6,
                    color: '#2493F8',
                    text: '人力资源',
                    img: 'yrt-qunzu1',
                    url: '/ptm/HumanResources',
                    name: 'inventory'
                },
                {
                    id: 120,
                    color: '#FF630B',
                    text: '用户管理',
                    img: 'yrt-xingzhuangjiehe',
                    url: '/ptm/userManage/user',
                    name: 'SuppliesClaim'
                },
                {
                    id: 130,
                    color: '#4CCE5B',
                    text: '角色权限',
                    img: 'yrt-jiaose',
                    url: '/ptm/userManage/userRoles',
                    name: 'contractList'
                }
            ],
            // 预警中心数据
            warningCenterAryList: [],
            // 待办任务
            tasksList: [
                {
                    id: 1,
                    type: 1,
                    typeName: '询价申请',
                    task: '云南栢田医疗科技有限公司申请验收(￥0.00)'
                },
                {
                    id: 2,
                    type: 2,
                    typeName: '订单申请',
                    task: '云南栢田医疗科技有限公司申请订单(￥10.00)'
                },
                {
                    id: 3,
                    type: 1,
                    typeName: '询价申请',
                    task: 'XX生产商申请验收(￥0.00)'
                },
                {
                    id: 4,
                    type: 2,
                    typeName: '订单申请',
                    task: 'XX股份有限公司申请订单(￥56.78)'
                }
            ]
        };
    },
    computed: {
        headerHeight() {
            return Math.floor(this.$store.getters.contentHeight / 7);
        },
        blockHeight() {
            return (this.$store.getters.contentHeight - this.headerHeight - 24) / 2;
        }
    },
    watch: {
        blockHeight() {
            this.debouncedResizeChart();
        }
    },
    mounted() {
        this.drawLineHospital();
        this.drawLineDepartments();
        this.drawLineShoppingSuppliers();
        this.debouncedResizeChart = this.$XEUtils.debounce(this.resizeChart, 500);
    },
    filters: {
        // 格式化金额
        formatMoney(s, n) {
            n = n > 0 && n <= 20 ? n : 2;
            s = parseFloat((s + '').replace(/[^\d.-]/g, '')).toFixed(n) + '';
            let l = s.split('.')[0].split('').reverse(),
                r = s.split('.')[1],
                t = '';
            for (let i = 0; i < l.length; i++) {
                t += l[i] + ((i + 1) % 3 === 0 && i + 1 !== l.length ? ',' : '');
            }
            return '￥' + t.split('').reverse().join('') + '.' + r;
        }
    },
    methods: {
        // 全院耗材采购一览 echarts
        drawLineHospital() {
            // 基于准备好的dom，初始化echarts实例
            this.myChartHospital = echarts.init(document.getElementById('myCharts-hospital'));
            this.myChartHospital.setOption(this.allHospitalOptionObj);
        },
        // 科室采购金额一览 echarts
        drawLineDepartments() {
            // 基于准备好的dom，初始化echarts实例
            this.myChartDeparments = echarts.init(document.getElementById('myCharts-departments'));
            this.myChartDeparments.setOption(this.deparmentsOptionObj);
        },
        // 供应商采购金额一览 echarts
        drawLineShoppingSuppliers() {
            // 基于准备好的dom，初始化echarts实例
            this.myChartShoppingSuppliers = echarts.init(document.getElementById('myCharts-shopping-suppliers'));
            this.myChartShoppingSuppliers.setOption(this.myChartShoppingSuppliersOptionObj);
        },
        // 快捷入口
        quickEnter(url) {
            this.$router.push(url);
        },
        // 待办详情
        detailInfo(type) {
            switch (type) {
                case 1:
                    this.$router.push('/commonView/enquiryList');
                    break;
                case 2:
                    this.$router.push('/commonView/purchaseOrder');
                    break;
                default:
                    break;
            }
        },
        resizeChart() {
            this.myChartHospital.resize();
            this.myChartDeparments.resize();
            this.myChartShoppingSuppliers.resize();
        }
    }
};
</script>

<style lang="less" scoped>
.hospital-home {
    .hospital-home-header {
        width: 100%;
        background: #fff;
        padding: 8px;
        .headerInfo-goods,
        .headerInfo-supplier,
        .headerInfo-buyGoods,
        .headerInfo-plans {
            border: 1px solid #c3cedf;
            border-radius: 4px;
            min-height: 110px;
            .item-info-top {
                h5 {
                    text-align: left;
                    font-weight: bold;
                    margin: 0;
                    color: #4f4f51;
                    padding-left: 8px;
                }
                h6 {
                    text-align: left;
                    font-weight: bold;
                    margin: 0;
                    color: #4f4f51;
                    padding-left: 8px;
                }
            }
        }
    }
    .hospital-home-body {
        width: 100%;
        background: #fff;
        padding: 0 8px;
        .hospital-home-echarts {
            min-height: 220px;
            border: 1px solid #c3cedf;
            background: #fff;
            .home-echarts-header {
                padding: 8px 16px;
                text-align: left;
                width: 100%;
                font-size: 18px;
                color: #4f4f51;
                font-weight: 700;
                margin: 0;
                .float-right {
                    font-weight: normal;
                    font-size: 14px;
                    text-indent: -44px;
                    float: right;
                }
            }
            .my-charts-caption {
                font-size: 14px;
                color: #4f4f51;
                text-align: left;
                padding: 8px 16px;
                .text-red {
                    padding-left: 4px;
                    color: red;
                    font-size: 13px;
                    font-weight: 700;
                }
            }
            .quick-enter-list {
                padding: 8px 16px;
                ul {
                    display: flex;
                    flex-direction: row;
                    flex-wrap: wrap;
                    margin: 0;
                    padding: 0;
                    li {
                        width: 25%;
                        display: flex;
                        cursor: pointer;
                        align-items: center;
                        flex-direction: column;
                        padding-bottom: 15px;
                        .enter-list-top {
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            width: 62px;
                            height: 62px;
                            border-radius: 4px;
                            .yrt-quick-enter {
                                vertical-align: middle;
                                font-family: 'yrt' !important;
                                font-style: normal;
                                font-size: 40px;
                                color: #fff;
                                padding: 0;
                                -webkit-font-smoothing: antialiased;
                            }
                        }
                        p {
                            font-size: 14px;
                            color: #4f4f51;
                            padding-top: 8px;
                        }
                    }
                }
            }
            .caption-wrap {
                color: #4f4f51;
                display: flex;
                flex-direction: column;
                font-size: 13px;
                padding: 8px 16px;
                li {
                    display: flex;
                    flex-direction: row;
                    justify-content: space-between;
                    padding-bottom: 8px;
                    border-bottom: 1px dashed rgba(151, 151, 151, 0.44);
                }
            }
            .list-wrap {
                font-size: 14px;
                .waring-til {
                    color: red;
                    font-weight: 700;
                }
            }
        }
        .my-charts {
            height: calc(100% - 40px);
            position: absolute;
            top: 40px;
            width: calc(100% - 9px);
            .hospital-home-taskList {
                .hospital-home-taskItem {
                    padding: 0 16px;
                    display: flex;
                    justify-content: space-between;
                    width: 100%;
                    border: 1px solid #d7d9dc;
                    border-left: 8px solid #21ba45;
                    border-top-left-radius: 4px;
                    border-bottom-left-radius: 4px;
                    align-items: center;
                    min-height: 50px;
                    font-size: 14px;
                    margin-bottom: 16px;
                    .home-taskItem-detail {
                        color: #1890ff;
                        cursor: pointer;
                    }
                }
            }
        }
        .scroll-hidden {
            overflow: hidden;
            .hospital-home-taskList {
                padding: 0 17px;
            }
        }
        .scroll-hidden:hover {
            overflow-y: scroll;
            .hospital-home-taskList {
                padding: 0 0 0 17px;
            }
        }
    }
}
.icon-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    .icon-content {
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        img {
            width: 0.55em;
            height: 0.55em;
            max-width: 48px;
            max-height: 48px;
            margin-left: 0.07em; // 图标的视觉重心偏左，增加右移量
        }
    }
}
</style>
