<template>
    <div>
        <div class="city-button-group">
            <button class="capsuleItem capsuleItem-pink" :autofocus="true">
                <img class="capsuleImg" src="/images/largeScreen/icon/组 96@2x.png">
                <div class="capsuleName">生产商</div>
            </button>
            <button class="capsuleItem capsuleItem-yellow">
                <img class="capsuleImg" src="/images/largeScreen/icon/组 98@2x.png">
                <div class="capsuleName">经销商</div>
            </button>
            <button class="capsuleItem capsuleItem-green">
                <img class="capsuleImg" src="/images/largeScreen/icon/组 101@2x.png">
                <div class="capsuleName">医院</div>
            </button>
            <button class="capsuleItem capsuleItem-blue">
                <img class="capsuleImg" src="/images/largeScreen/icon/组 115@2x.png">
                <div class="capsuleName">科室</div>
            </button>
            <button class="capsuleItem capsuleItem-purple">
                <img class="capsuleImg" src="/images/largeScreen/icon/组 116@2x.png">
                <div class="capsuleName">回收商</div>
            </button>
        </div>
        <div class="city-grid-container">
            <table>
                <thead>
                    <tr>
                        <th>企业名称</th>
                        <th>社会统一代码</th>
                        <th>联系电话</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="index in 18" :key="index">
                        <td>某某医疗设备生产公司</td>
                        <td>51370100MJD681777B A0411</td>
                        <td>13888888888</td>
                    </tr>
                </tbody>
            </table>
            <a-pagination show-quick-jumper size="small" :default-current="2" :total="500" disabled show-less-items
                @change="onChange" />
        </div>
    </div>
</template>

<script>
    export default {
        methods: {

        },
        mounted() {

        }
    }
</script>

<style>
    .city-button-group {
        display: flex;
        flex-direction: row;
        align-content: center;
        justify-content: space-evenly;
        margin-top: 0.5rem;
    }

    .city-button-group .capsuleItem {
        border: none;
        height: 0.66rem;

    }


    .city-button-group .capsuleItem:focus {
        width: 2.08rem;
        height: 0.86rem;
        border-color: #0071bc;
        border-width: 1px;
        border-radius: 0.18rem 0.18rem 0rem 0rem;
        background: #0071bc73;
    }

    .city-button-group .capsuleName {
        font-family: "PingFang SC";
        font-weight: 500;
        font-size: 0.26rem;
        line-height: 0.24rem;
        text-align: center;
        color: #fff;
        width: 1rem;
        margin-left: 0.7rem;
    }

    .capsuleItem-pink {
        background: linear-gradient(90deg, #fba993, #ef77a8);
    }

    .capsuleItem-yellow {
        background: linear-gradient(90deg, #facd20, #fbb03b);
    }

    .capsuleItem-green {
        background: linear-gradient(90deg, #0fe18a, #00a99d);
    }

    .capsuleItem-blue {
        background: linear-gradient(90deg, #0fe1d9, #29abe2);
    }

    .capsuleItem-purple {
        background: linear-gradient(90deg, #b98dff, #7f58e6);
    }

    /* ----------- AntD分页的样式覆盖 ------------------------------------------------- */

    /* 分页输入框 样式覆盖*/
    .ant-pagination.mini .ant-pagination-options-quick-jumper input {
        background-color: #00000000;
        border-color: #0071BC;
    }

    /* 分页输入框描述 样式覆盖*/
    .ant-pagination.mini .ant-pagination-options-quick-jumper {
        color: #9AD5EF;
    }

    /* 分页页数 样式覆盖*/
    .ant-pagination.mini .ant-pagination-item:not(.ant-pagination-item-active) {
        background-color: #0071bd33;
        margin-right: 0.1rem !important;
        margin-left: 0.1rem !important;
    }

    .ant-pagination-item {
        background-color: #0071bd33;
    }

    .ant-pagination-prev,
    .ant-pagination-next {
        margin-right: 0.1rem !important;
        margin-left: 0.1rem !important;
        background-color: #0071bd33;
    }

    .ant-pagination.mini .ant-pagination-jump-prev,
    .ant-pagination.mini .ant-pagination-jump-next {
        background-color: #0071bd33;

    }

    .ant-pagination-item-active {
        color: #ffffff;
        border-color: #1890ff;
        font-weight: 500;
    }

    .anticon svg {
        color: #9AD5EF;
    }

    .ant-pagination-item-ellipsis {
        color: #9AD5EF;
    }

    /* 分页页数数字 样式覆盖*/
    .ant-pagination-item a {
        color: #9AD5EF;
    }
</style>

<style scoped>
    table {
        border-left: 0.01rem solid #2c96d1;
        border-bottom: 0.01rem solid #2c96d1;
        width: 11.64rem;
        max-height: 10.63rem;
        margin-bottom: 0.3rem;
    }

    th {
        height: 0.32rem;
        padding: 0px;
        opacity: 1;
        font-size: 0.14rem;
        font-family: PingFang SC, PingFang SC-Medium;
        font-weight: 400;
        text-align: center;
        color: #ffffff;
        border-top: 0.01rem solid #2c96d1;
        border-right: 0.01rem solid #2c96d1;
        background-color: #2c97d12e;
    }

    td {
        height: 0.55rem;
        padding: 0px;
        opacity: 1;
        font-size: 0.14rem;
        font-family: PingFang SC, PingFang SC-Regular;
        font-weight: 400;
        text-align: center;
        color: #ffffff;
        border-top: 0.01rem solid #2c96d1;
        border-right: 0.01rem solid #2c96d1;
    }
</style>