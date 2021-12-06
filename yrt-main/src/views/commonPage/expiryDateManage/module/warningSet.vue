<template>
    <div>
        <a-modal
            :title="warningSetMd.title"
            v-model="isOpen"
            :maskClosable="false"
            :width="580"
            :centered="true"
            @cancel="closeWarningSetFn"
            class="modal-form-input-scoped global-drag warning-set-model"
        >
            <div v-globalDrag="{ el: 'warning-set-model' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ warningSetMd.title }}
                </h6>
            </div>
            <template slot="footer">
                <a-button @click="closeWarningSetFn">关闭</a-button>
                <a-button
                    v-if="isPlatform"
                    :loading="warningSetMd.loading"
                    @click="platformWarningSetupSaveAPIFn"
                    type="primary"
                    >保存</a-button
                >
            </template>
            <a-spin :spinning="spinning">
                <div>
                    <a-tabs type="card" v-model="tabsActiveKey">
                        <a-tab-pane :tab="tabPane(0)" :key="tabPane(0, 'value')">
                            <warning-set-form
                                :parentDays="certificationDays"
                                :tabsActiveKey="tabsActiveKey"
                                @formInput="getFormInputVal"
                            ></warning-set-form>
                        </a-tab-pane>
                        <a-tab-pane :tab="tabPane(1)" :key="tabPane(1, 'value')">
                            <warning-set-form
                                :parentDays="productRegisterDays"
                                :tabsActiveKey="tabsActiveKey"
                                @formInput="getFormInputVal"
                            ></warning-set-form>
                        </a-tab-pane>
                        <a-tab-pane :tab="tabPane(2)" :key="tabPane(2, 'value')">
                            <warning-set-form
                                :parentDays="productExpiryDays"
                                :tabsActiveKey="tabsActiveKey"
                                @formInput="getFormInputVal"
                            ></warning-set-form>
                        </a-tab-pane>
                    </a-tabs>
                </div>
            </a-spin>
        </a-modal>
    </div>
</template>

<script>
import { platformWarningSetupInfoAPI, platformWarningSetupSaveAPI, orgWarningSetupInfoAPI } from '@/service/pageAjax';

import { identification } from '../mixins/identification.js';

import warningSetForm from './warningSetForm';
import { tabPane } from '../mixins/tabPane.js';
import { expiryDateWarning } from '@/config/constants';

export default {
    name: 'warningSet', // 预警设置 = 模态框
    props: {
        isOpen: {
            // 模态框窗口是否打开标识， true、false
            type: Boolean,
            default: true
        },
        tabsActiveKey: {
            // 模态框窗口tab选项卡当前的key标识，必传
            type: String,
            required: true
        }
    },
    components: {
        warningSetForm
    },
    data() {
        return {
            spinning: false,
            warningSetMd: {
                title: `${this.$route.meta.title} - 预警设置`,
                loading: false
            },
            certificationDays: 0,
            productRegisterDays: 0,
            productExpiryDays: 0
        };
    },
    methods: {
        async getWarningSetupInfoAPIFn() {
            this.spinning = true;
            const service = this.isPlatform ? await platformWarningSetupInfoAPI : await orgWarningSetupInfoAPI;
            service()
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        const { info } = res;
                        if (info && info.certWarningSetup && Object.keys(info.certWarningSetup).length) {
                            const { certWarningSetup, productMdrfWaringSetup, productValidityWarningSetup } = info;
                            this.certificationDays = certWarningSetup.cew_day;
                            this.productRegisterDays = productMdrfWaringSetup.cew_day;
                            this.productExpiryDays = productValidityWarningSetup.cew_day;
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.spinning = false;
                });
        },
        async platformWarningSetupSaveAPIFn() {
            this.warningSetMd.loading = true;
            await platformWarningSetupSaveAPI({
                'certWarningSetup': {
                    'cew_day': this.certificationDays
                },
                'productMdrfWaringSetup': {
                    'cew_day': this.productRegisterDays
                },
                'productValidityWarningSetup': {
                    'cew_day': this.productExpiryDays
                }
            })
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.closeWarningSetFn();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.warningSetMd.loading = false;
                });
        },
        closeWarningSetFn() {
            this.$emit('closeWarningSet', false);
        },
        getFormInputVal(dataObj) {
            const { tabsActiveKey, activeDays } = dataObj;

            switch (tabsActiveKey) {
                case expiryDateWarning['0'].value:
                    this.certificationDays = activeDays;
                    break;
                case expiryDateWarning['1'].value:
                    this.productRegisterDays = activeDays;
                    break;
                case expiryDateWarning['2'].value:
                    this.productExpiryDays = activeDays;
                    break;
            }
        }
        // changeActiveFn (activeKey) {
        //     this.$emit('changeTabsActiveKey', activeKey)
        // }
    },
    mixins: [tabPane, identification],
    watch: {
        isOpen: {
            handler(newVal) {
                if (newVal) {
                    this.$Utils.globalDragCenterFn('warning-set-model');
                    this.getWarningSetupInfoAPIFn();
                }
            },
            immediate: true
        }
    }
};
</script>

<style lang="less" scoped></style>
