<template>
    <div class="common-screen-wrapper">
        <div>
            <commonHeader :headerName="'departmentWarningDetail'" :isGrid="false" :url="'departmentInfo'"/>
        </div>
        <div class="department-info-container">
            <div class="department-info-grid">
                <table>
                    <tbody>
                        <tr>
                            <th>企业名称</th>
                            <td colspan="5">xxx</td>
                            <th>预警信息</th>
                            <td>x</td>
                        </tr>
                        <tr>
                            <th>法定代表人</th>
                            <td>xxx</td>
                            <th>公司类型</th>
                            <td>xxx</td>
                            <th>联系电话</th>
                            <td>xxx</td>
                            <th>工商注册号</th>
                            <td>xxx</td>
                        </tr>
                        <tr>
                            <th>统一社会信用代码</th>
                            <td>xxx</td>
                            <th>经营状态</th>
                            <td>xxx</td>
                            <th>纳税人识别号</th>
                            <td>xxx</td>
                            <th>组织机构代码</th>
                            <td>xxx</td>
                        </tr>
                        <tr>
                            <th>营业期限</th>
                            <td>xxx</td>
                            <th>注册资本</th>
                            <td>xxx</td>
                            <th>登记机关</th>
                            <td>xxx</td>
                            <th>成立日期</th>
                            <td>xxx</td>
                        </tr>
                        <tr>
                            <th>注册地址</th>
                            <td colspan="7">xxx</td>
                        </tr>
                        <tr>
                            <th>经营范围</th>
                            <td colspan="7">xxx</td>
                        </tr>
                        <tr>
                            <th colspan="8">资证信息</th>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="department-info-process-button-container">
            <button @click="proccessButtonClick">
                <img class="process-icon" src="/images/largeScreen/icon/组 737.png">
                <div class="process-title">追溯企业供应链</div>
            </button>
        </div>
    </div>
</template>

<script>
    import commonHeader from '../header/commonHeader.vue'
    export default {
        components: {
            commonHeader,
        },
        methods: {
            proccessButtonClick() {
                this.$router.replace({
                    path: '/largeScreen/process'
                })
            }
        }
    }
</script>

<style>
    .common-screen-wrapper {
        position: absolute;
        top: 1.03rem;
        left: 0.68rem;
    }

    table {
        border-left: 0.01rem solid #2c96d1;
        border-bottom: 0.01rem solid #2c96d1;
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
        background-color: #2d98d24d;
    }

    td {
        height: 0.37rem;
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

    .department-info-container {
        height: 10.8rem;
        width: 24.24rem;
        background: #0071bc26;
        border: 1px solid #2c96d1;
    }

    .department-info-grid table {
        width: 100%;
        height: 2.3rem;
    }

    .department-info-process-button-container {
        position: absolute;
        top: 12.3rem;
        left: 11.26rem;
    }

    .department-info-process-button-container button {
        width: 2.7rem;
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: space-evenly;
        border-radius: 0.12rem;
        background: #0071bc40;

        border: 1px solid #2c96d1;

    }

    .process-title {
        font-family: "PingFang SC";
        font-weight: normal;
        font-size: 0.26rem;
        line-height: 0.45rem;
        text-align: left;
        color: #68d3e7;

    }
</style>