<template>
    <a-modal
        :title="title"
        :visible="visible"
        :maskClosable="false"
        :mask="false"
        :width="1000"
        :centered="true"
        @cancel="handleCancel"
        class="modal-form-input-scoped global-drag add_product"
    >
        <div v-globalDrag="{ el: 'add_product' }" class="modal-header-wrap">
            <h6>
                <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                {{ title }}
            </h6>
        </div>
        <template slot="footer">
            <a-button @click="handleCancel">关闭</a-button>
            <a-button :loading="submitting" @click="handleSubmit" type="primary">添加</a-button>
        </template>
        <add-product ref="chooseProModal" @returnData="getProReturn" :partB="partB" :multiple="false"></add-product>
    </a-modal>
</template>

<script>
import addProduct from '@/components/addProduct';

export default {
    name: 'add-product-modal',
    props: {
        visible: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            partB: {
                // 新增产品窗口给子组件传递值
                org_id: this.$cookie.get('userbelong'),
                modalType: 2, // 1 - 合同内的产品  2 - 期初库存的产品列表  3 - 采购计划  4 - 采购/销售订单  7 - 入库单  8 - 出库单
                oname: '', // 厂商/医院/经销商名称
                otype: '', // 是厂商/医院/经销商  0 - 经销商   1 - 厂商   2 - 医院
                oid: ''
            },
            title: `${this.$route.meta.title} - 新增 - 添加产品`,
            submitting: false
        };
    },
    methods: {
        getProReturn(selectedProduct) {
            this.$emit('on-return', selectedProduct);
        },

        handleCancel() {
            this.$emit('on-cancel');
        },

        // 添加（提交）
        handleSubmit() {
            this.$refs.chooseProModal.returnProList();
        }
    },
    created() {
        const systemType = this.$cookie.get('userSystemType');
        switch (systemType) {
            case '1':
                this.partB.otype = 'HH';
                break;
            case '2':
                this.partB.otype = 'DD';
                break;
            case '3':
                this.partB.otype = 'VV';
                break;
        }
    },
    components: {
        addProduct
    }
};
</script>
