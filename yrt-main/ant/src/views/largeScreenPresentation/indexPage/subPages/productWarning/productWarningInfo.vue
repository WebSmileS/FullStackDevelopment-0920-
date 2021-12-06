<template>
    <div class="common-screen-wrapper">
        <div>
            <commonHeader :headerName="'productWarningDetail'" :isGrid="false" :url="'productWarningInfo'" />
        </div>
        <div class="department-info-container">
            <div class="department-info-grid">
                <table>
                    <tbody>
                        <tr>
                            <th>产品名称</th>
                            <td colspan="3">xxx</td>
                            <th rowspan="4">预警信息</th>
                            <td rowspan="4">x</td>
                            <th rowspan="4">产品照片</th>
                            <td rowspan="4">x</td>
                        </tr>
                        <tr>
                            <th>生产企业</th>
                            <td>xxx</td>
                            <th>规格型号</th>
                            <td>xxx</td>
                        </tr>
                        <tr>
                            <th>销售企业</th>
                            <td>xxx</td>
                            <th>批号</th>
                            <td>xxx</td>
                        </tr>
                        <tr>
                            <th>申请科室</th>
                            <td>xxx</td>
                            <th>数量</th>
                            <td>xxx</td>
                        </tr>
                        <tr>
                            <th colspan="8">资证信息</th>
                        </tr>
                    </tbody>
                </table>
            </div>
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
        height: 11.72rem;
        width: 24.24rem;
        background: #0071bc26;
        border: 1px solid #2c96d1;
    }

    .department-info-grid table {
        width: 100%;
        height: 2.3rem;
    }

</style>