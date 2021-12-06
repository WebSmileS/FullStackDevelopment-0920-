<template>
    <div>
        <table>
            <thead>
                <tr>
                    <th class="table-first-head">机构名称</th>
                    <th class="table-first-head">社会统一代码</th>
                    <th class="table-first-head">预警类型</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(i, index) in tableData" :key="index">
                    <td class="table-first-row">{{ i.org_name}}</td>
                    <td class="table-second-row">{{ i.scc }}</td>
                    <td class="table-third-row">{{ i.status }}</td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
export default {
    data () {
        return {
            // 表格数据
            tableData: {
                // 机构名称
                org_name: '',
                // 机构信用代码
                scc: '',
                // 机构预警类型
                status: ''
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
                    org_name: m.orgName,
                    scc: m.social_credit_code,
                    status: m.warnType
                }
            })

            // 开发初始情况，只取前6个机构
            this.tableData = this.tableData.slice(0, 6)
        }
    }
}
</script>

<style scoped>
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
  background-color:#2c97d12e ;
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

.table-first-row {
  width: 1.8rem;
  opacity: 1;
}

.table-second-row {
  width: 1.84rem;
  opacity: 1;

}

.table-third-row {
  width: 2.08rem;
  opacity: 1;

}

</style>
