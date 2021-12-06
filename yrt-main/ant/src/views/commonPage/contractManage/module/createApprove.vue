<template>
    <div class="scoped" style="margin: -24px; height: 400px">
        <div class="btn-top-wrap">
            <ul class="btn-list-wrap" style="width: 100%">
                <li>
                    <a-button type="primary" size="small" @click="openAddPop"> 添加审批人 </a-button>
                    <a-button type="danger" size="small" @click="delTr"> 删除 </a-button>
                </li>
                <li>
                    <a-button type="primary" size="small" @click="onMoveFn(1)"> 上移 </a-button>
                </li>
                <a-button type="primary" size="small" @click="onMoveFn(0)"> 下移 </a-button>
            </ul>
        </div>
        <div class="tab-table-wrap">
            <a-table
                :columns="columns"
                :dataSource="tableData"
                :scroll="{ y: 140 }"
                :pagination="false"
                :customRow="customRow"
                :rowClassName="rowClassFn"
            >
            </a-table>
        </div>
        <!--发起审批-->
        <a-modal
            title="发起审批"
            :visible="chooseOrgPopMd.alert"
            :width="450"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="chooseOrgPopMd.alert = false"
            class="modal-form-input-scoped global-drag choose-pop-md"
        >
            <div v-globalDrag="{ el: 'choose-pop-md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgPopMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="chooseOrgPopMd.alert = false">取消</a-button>
                <a-button :loading="chooseOrgPopMd.loading" type="primary" @click="sendPopBack">确认</a-button>
            </template>
            <choose-org-pop :approveType="approveType" @returnData="getAppPop" ref="chooseOrgPopMd"></choose-org-pop>
        </a-modal>
    </div>
</template>

<script>
import chooseOrgPop from './chooseOrgPop';

export default {
    name: 'createApprove',
    props: ['approveType'],
    components: {
        chooseOrgPop
    },
    data() {
        return {
            chooseOrgPopMd: {
                title: `${this.$route.meta.title} - 发起审批 - 添加审批人`,
                alert: false,
                loading: false
            },
            chooseTrObj: '',
            columns: [
                {
                    title: '审批人',
                    dataIndex: 'uname',
                    align: 'center',
                    width: '70%'
                },
                {
                    title: '甲方/乙方',
                    dataIndex: 'type',
                    align: 'center',
                    width: '30%'
                }
            ],
            tableData: []
        };
    },
    methods: {
        refreshPage() {
            this.tableData = [];
        },
        returnData() {
            if (this.tableData.length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: '请添加审批人',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            let popArr = [];
            this.tableData.forEach((item) => {
                let obj = {
                    approver_inner_sn: item.uid.toString(),
                    approver_name: item.uname
                };
                popArr.push(obj);
            });
            // popArr = popArr.join(',')
            let dataObj = {
                approver_inner_sn: popArr,
                partA: this.approveType.partA,
                partB: this.approveType.partB,
                contract_inner_sn: this.approveType.contract_inner_sn,
                contract_type: this.approveType.contract_type
            };
            this.$emit('returnData', dataObj);
        },
        getAppPop(data) {
            data.forEach((item) => {
                item.isActive = '';
            });
            this.chooseOrgPopMd.alert = false;
            this.tableData = data;
        },
        sendPopBack() {
            this.$refs.chooseOrgPopMd.returnData();
        },
        openAddPop() {
            // 打开选人
            this.$Utils.globalDragCenterFn('choose-pop-md');
            this.chooseOrgPopMd.alert = true;
            if (this.$refs.chooseOrgPopMd) {
                this.$refs.chooseOrgPopMd.pageRefresh();
            }
        },
        customRow(record) {
            // 自定义行
            return {
                class: record.isActive,
                on: {
                    // 事件
                    click: () => {
                        // 点击行
                        this.chooseTrObj = record;
                    }
                }
            };
        },
        rowClassFn(record) {
            if (record.key === this.chooseTrObj.key) {
                return 'active-tr';
            }
            return '';
        },
        delTr() {
            // 删除
            if (!this.chooseTrObj.key) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            let code = this.chooseTrObj.key;
            this.tableData.forEach((item, index) => {
                if (item.key === code) {
                    this.tableData.splice(index, 1);
                }
            });
        },
        onMoveFn(dir) {
            // 上下移动 dir传1表示上移，传0表示下移
            if (!this.chooseTrObj.key) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            let code = this.chooseTrObj.key;
            let moveComm = (curIndex, nextIndex) => {
                let arr = this.tableData;
                arr[curIndex] = arr.splice(nextIndex, 1, arr[curIndex])[0];
                return arr;
            };
            this.tableData.some((val, index) => {
                if (val.key === code) {
                    if (dir === 1 && index === 0) {
                        // this.$message.error('已在顶部')
                    } else if (dir === 0 && index === this.tableData.length - 1) {
                        // this.$message.error('已在底部')
                    } else {
                        let nextIndex = dir === 1 ? index - 1 : index + 1;
                        this.tableData = moveComm(index, nextIndex);
                    }
                    return true;
                }
                return false;
            });
        },
        cancelFn() {
            if (this.$refs.chooseOrgPopMd) {
                this.$refs.chooseOrgPopMd.clearChoose();
            }
        }
    },
    created() {
        this.refreshPage();
    }
};
</script>

<style scoped lang="less">
.btn-top-wrap {
    padding: 24px 24px;
    .ant-btn {
        min-width: 76px;
    }
}
</style>
