<template>
    <div class="scoped agency-approval-container">
        <!--头部区域开始-->
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button @click="addProduct()" type="primary" size="small"> 添加产品 </a-button>
                        </li>
                        <li>
                            <a-button @click="inventorySetting()" type="primary" size="small"> 批量设置 </a-button>
                            <!-- <a-button
                                @click="sendWarning()"
                                type="primary"
                                size="small"
                                :disabled="sendSatus"
                            >
                                批量取消
                            </a-button> -->
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12"> </a-col>
            </a-row>
        </div>
        <!--头部区域结束-->
        <!-- 上半部tab切换区域开始 -->
        <div class="tab-table-wrap cur-overflow-hidden tab-table-overflowy" style="padding: 8px; background: #fff">
            <a-table
                :rowSelection="rowSelection"
                :columns="columns"
                bordered
                :dataSource="tableData"
                :scroll="{ y: tabScrollHeight }"
                :pagination="false"
            >
                <span slot="tags" slot-scope="tags">
                    <div class="blue">{{ tags }}</div>
                </span>
            </a-table>
        </div>
        <!-- 上半部tab切换区域结束 -->
        <!-- 模态框区域 -->
        <!-- 批量修改库存设置 start-->
        <a-modal
            v-model="saveConfigStatus"
            :width="300"
            :centered="true"
            :maskClosable="false"
            okText="保存"
            @ok="saveConfig"
            @cancel="closeConfig"
            class="modal-form-input-scoped global-drag validity-warning"
        >
            <div v-globalDrag="{ el: 'validity-warning' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ configTitle }}
                </h6>
            </div>
            <div class="modal-form-input">
                <div class="form-top">
                    <div class="left-wrap">
                        <ul>
                            <li class="item">
                                <span class="label" style="width: 80px">库存上限</span>
                                <a-input-number v-model="inventory.topLimit" placeholder="" class="input" />
                            </li>
                            <li class="item">
                                <span class="label" style="width: 80px">库存下限</span>
                                <a-input-number v-model="inventory.lowerLimit" placeholder="" class="input" />
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </a-modal>
        <!-- 批量修改库存设置 end -->
        <!-- 添加产品区域开始 -->
        <a-modal
            :title="addProductMd.title"
            v-model="addProductMd.alert"
            :maskClosable="false"
            :mask="false"
            :width="1000"
            :centered="true"
            @cancel="addProductMd.alert = false"
            class="modal-form-input-scoped global-drag add_product"
        >
            <div v-globalDrag="{ el: 'add_product' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ addProductMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="addProductMd.alert = false">关闭</a-button>
                <a-button :loading="addProductMd.loading" @click="addProductFn" type="primary">添加</a-button>
            </template>
            <add-product ref="chooseProMd" @returnData="getProReturn" :partB="partB"></add-product>
        </a-modal>
        <!-- 添加产品区域结束 -->
    </div>
</template>

<script>
import moment from 'moment';
import addProduct from '@/components/addProduct';
// import {} from '@/service/pageAjax'
export default {
    name: 'warning',
    components: {
        addProduct
    },
    data() {
        return {
            systemType: 0, // 机构类型
            orgInfo: {
                org_id: 0
            },
            // 预警设置
            inventory: {
                topLimit: 0,
                lowerLimit: 0
            },
            // 添加产品
            addProductMd: {
                title: `添加产品`,
                alert: false,
                loading: false
            },
            // 新增产品窗口给子组件传递值
            partB: {
                org_id: this.$cookie.get('userbelong'),
                modalType: -1, // 1 - 合同内的产品  2 - 期初库存的产品列表  3 - 采购计划  4 - 采购/销售订单  7 - 入库单  8 - 出库单
                oname: '', // 厂商/医院/经销商名称
                otype: '', // 是厂商/医院/经销商  0 - 经销商   1 - 厂商   2 - 医院
                oid: ''
            },
            // 表格选中的当前行数据
            currentTrObj: {},
            // 表头设置
            columns: [
                {
                    title: '名称',
                    dataIndex: 'name',
                    align: 'center',
                    width: '15%',
                    scopedSlots: { customRender: 'tags' }
                },
                {
                    title: '生产厂家',
                    dataIndex: 'expiration_date',
                    align: 'center',
                    width: '15%'
                },
                {
                    title: '规格',
                    dataIndex: 'social_credit_code',
                    align: 'center',
                    width: '10%'
                },
                {
                    title: '单位',
                    dataIndex: 'phone',
                    align: 'center',
                    width: '5%'
                },
                {
                    title: '预警仓库',
                    dataIndex: 'email',
                    align: 'center',
                    width: '10%'
                },
                {
                    title: '库存上限',
                    dataIndex: 'email',
                    align: 'center',
                    width: '10%'
                },
                {
                    title: '库存下限',
                    dataIndex: 'email',
                    align: 'center',
                    width: '10%'
                },
                {
                    title: '操作',
                    dataIndex: 'email',
                    align: 'center',
                    width: '10%'
                }
            ],
            editInfoFormObj: {
                cew_day: 0,
                email: ''
            },
            saveConfigStatus: false
        };
    },
    methods: {
        moment,
        // 获取预警设置信息
        /* 按钮区域的操作 */
        checkCurrentTrDataFn() {
            // 检测表格是否有选中的的数据
            if (Object.keys(this.currentTrObj).length <= 0) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    centered: true,
                    okText: '知道了',
                    maskClosable: false
                });
                return false;
            }
            return true;
        },
        /* 按钮区域的操作 */
        getCurrentStyle(current) {
            const style = {};
            if (current.date() === 1) {
                style.border = '1px solid #1890ff';
                style.borderRadius = '50%';
            }
            return style;
        },
        // 获取回调商品
        getProReturn(dea, ven) {
            const flag = this.addInfoDatas.length;
            this.addInfoDatas.push(...ven.concat(dea));

            this.addInfoDatas = this.addInfoDatas.map((item, index) => {
                const isUnitInnerSnFlag = item.unit_inner_sn && item.unit_inner_sn.length <= 0;
                if (parseFloat(item.product_type) === 1) {
                    !item.detail_inner_sn && isUnitInnerSnFlag && this.platformUnitTreeFn(item, 'add'); // 'add' - 新增标识
                } else {
                    !item.detail_inner_sn && isUnitInnerSnFlag && this.dealerUnitTreeAPIFn(item, 'add'); // 'add' - 新增标识
                }
                item.index = index;
                item.activeClass = '';
                return item;
            });

            if (flag) {
                this.currentTrAddInfoObj = this.addInfoDatas[this.addInfoDatas.length - 1];
            } else {
                this.currentTrAddInfoObj = this.addInfoDatas[0];
            }
            this.addProductMd.alert = false;
        },
        // 保存预警设置
        saveConfig() {
            // this.waringAddFn()
        },
        // 打开预警设置
        inventorySetting() {
            if (!this.checkCurrentTrDataFn()) return;
            this.saveConfigStatus = true;
            this.$Utils.globalDragCenterFn('validity-warning');
        },
        // 关闭预警设置
        closeConfig() {
            this.saveConfigStatus = false;
        },
        // 添加商品窗口
        addProduct() {
            this.addProductMd.alert = true;
            this.$Utils.globalDragCenterFn('add_product');
        },
        // 确认添加商品
        addProductFn() {}
    },
    created() {
        this.orgInfo.org_id = parseInt(this.$cookie.get('userbelong'));
        this.systemType = parseInt(this.$cookie.get('userSystemType'));
    }
};
</script>

<style lang="less" scoped>
@h: 24px;
@w: 16px;
.ant-tabs-tab {
    padding: 0 10px;
}
.scoped {
    .ant-tabs-card-content {
        height: 100%;
    }
}
.validity-chose {
    margin-bottom: 12px;
}
.agency-approval-container {
    /* 水平垂直居中 */
    height: 100%;
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    .center {
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }
    .tab-table-wrap {
        height: calc(100% - 58px);
        .ant-tabs-card-content {
            height: 100%;
        }
    }
}
.modal-form-input-scoped {
    .modal-form-input {
        .ant-input-number {
            width: 120px;
        }
        .label-1 {
            width: 80px;
        }
    }
}
</style>
