<template>
    <div class="scoped" style="margin: -24px">
        <div class="half_part">
            <div class="tit">甲方</div>
            <div class="tab-table-wrap" style="padding: 0">
                <a-table
                    :columns="columns_a"
                    :dataSource="tableData_a"
                    :rowSelection="{ selectedRowKeys: selectedRowKeysA, onChange: onSelectChangeA }"
                    :scroll="{ y: 140 }"
                    :pagination="false"
                >
                </a-table>
            </div>
        </div>
        <div class="half_part">
            <div class="tit">乙方</div>
            <div class="tab-table-wrap" style="padding: 0">
                <a-table
                    :columns="columns_b"
                    :dataSource="tableData_b"
                    :rowSelection="{ selectedRowKeys: selectedRowKeysB, onChange: onSelectChangeB }"
                    :scroll="{ y: 140 }"
                    :pagination="false"
                >
                </a-table>
            </div>
        </div>
    </div>
</template>

<script>
import { commUserAPI } from '@/service/pageAjax';

export default {
    name: 'chooseOrgPop',
    props: ['approveType'],
    data() {
        return {
            columns_a: [
                {
                    title: '姓名',
                    dataIndex: 'uname',
                    align: 'center',
                    width: '100%'
                }
            ],
            tableData_a: [],
            selectedRowKeysA: [],
            selectedRowsA: [],
            columns_b: [
                {
                    title: '姓名',
                    dataIndex: 'uname',
                    align: 'center',
                    width: '100%'
                }
            ],
            tableData_b: [],
            selectedRowKeysB: [],
            selectedRowsB: []
        };
    },
    methods: {
        pageRefresh() {
            this.getUserList();
        },
        returnData() {
            let dataArr = this.selectedRowsA.concat(this.selectedRowsB);
            if (dataArr.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请添加审批人',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            this.$emit('returnData', dataArr);
        },
        onSelectChangeA(selectedRowKeys, selectedRows) {
            // 甲方选择
            this.selectedRowKeysA = selectedRowKeys;
            this.selectedRowsA = selectedRows;
        },
        onSelectChangeB(selectedRowKeys, selectedRows) {
            // 乙方选择
            this.selectedRowKeysB = selectedRowKeys;
            this.selectedRowsB = selectedRows;
        },
        async getUserList() {
            const type = this.approveType.contract_type;
            let sysTypeA;
            let sysTypeB;
            if (type === 0) {
                // 医院经销商
                sysTypeA = 1;
                sysTypeB = 2;
            }
            if (type === 1) {
                // 医院厂商
                sysTypeA = 1;
                sysTypeB = 3;
            }
            if (type === 2) {
                // 经销商厂商
                sysTypeA = 2;
                sysTypeB = 3;
            }
            if (type === 3) {
                // 经销商经销商
                sysTypeA = 2;
                sysTypeB = 2;
            }
            let oidA = this.approveType.partA;
            await commUserAPI(sysTypeA, oidA).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let data = res.list;
                    data.forEach((item) => {
                        item.key = item.uid;
                        item.type = '甲方';
                    });
                    this.tableData_a = data;
                }
            });
            let oidB = this.approveType.partB;
            await commUserAPI(sysTypeB, oidB).then((res) => {
                if (parseFloat(res.code) === 0) {
                    let data = res.list;
                    data.forEach((item) => {
                        item.key = item.uid;
                        item.type = '乙方';
                    });
                    this.tableData_b = data;
                }
            });
        },
        clearChoose() {
            this.selectedRowKeysA = [];
            this.selectedRowsA = [];
            this.selectedRowKeysB = [];
            this.selectedRowsB = [];
        }
    },
    mounted() {
        this.pageRefresh();
    }
};
</script>

<style scoped lang="less">
.half_part {
    display: inline-block;
    width: 49.5%;
    vertical-align: top;
    border: 1px solid #e7e7e7;
    .tit {
        text-align: center;
        line-height: 32px;
        border: 1px solid #e7e7e7;
        border-bottom: none;
        border-right: none;
        font-size: 14px;
        font-weight: bolder;
    }
}
.ant-table-header {
    overflow-y: auto !important;
}
</style>
