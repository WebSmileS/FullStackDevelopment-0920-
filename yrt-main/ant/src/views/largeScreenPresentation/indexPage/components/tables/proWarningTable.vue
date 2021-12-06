<template>
    <div>
        <table>
            <tbody>
                <tr v-for="(i, index) in tableData" :key="index">
                    <td class="table-first-row">{{ i.product_name }}</td>
                    <td class="table-second-row">{{ i.vendor_name }}</td>
                    <td class="table-third-row">{{ i.status }}</td>
                    <td class="table-fourth-row">{{ i.date }}</td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
import moment from 'moment'

export default {
    data () {
        return {
            // 表格数据
            tableData: {
                // 产品名称
                product_name: '',
                // 厂商名称
                vendor_name: '',
                // 产品预警类型
                status: '',
                // 产品预警时间
                date: ''
            }
        }
    },
    methods: {
        /**
         * init
         * @abstract 初始化方法，由父组件调用，为表格数据赋值
         * @param { Array } orgWarnInfo 机构预警信息数据
         */
        init (orgWarnInfo) {
            this.tableData = orgWarnInfo.map(m => {
                return {
                    product_name: m.productName,
                    vendor_name: m.orgName,
                    status: m.warnType,
                    date: moment(m.expireAt).format('YYYY-MM-DD')
                }
            })

            // 开发初始情况，只取前6个机构
            this.tableData = this.tableData.slice(0, 8)
        }
    }
}
</script>

<style scoped>
table {
  border-left: 0.01rem solid #2c96d1;
  border-bottom: 0.01rem solid #2c96d1;
  width: 5.7rem;
  table-layout: fixed;
}

td {
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

.table-first-row {
  width: 1.20rem;
  height: 0.38rem;
  opacity: 1;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

.table-second-row {
  width: 1.8rem;
  height: 0.38rem;
  opacity: 1;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

.table-third-row {
  width: 1.40rem;
  height: 0.38rem;
  opacity: 1;

}

.table-fourth-row {
  width: 1.33rem;
  height: 0.38rem;
  opacity: 1;

}

</style>
