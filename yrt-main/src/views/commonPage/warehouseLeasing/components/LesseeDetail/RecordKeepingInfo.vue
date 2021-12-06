<template>
    <a-spin :spinning="loading">
        <a-row :gutter="16">
            <a-col :span="10">
                <gallery :images="images" :styles="{ height: '512px' }" />
            </a-col>
            <a-col :span="14">
                <a-form layout="horizontal">
                    <a-form-item label="企业名称">
                        <a-input size="small" :value="formData['name']" />
                    </a-form-item>
                    <a-row :gutter="12">
                        <a-col :span="12">
                            <a-form-item label="备案编号">
                                <a-input size="small" :value="formData['record_sn']" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="12">
                            <a-form-item label="经营方式">
                                <a-input size="small" :value="formData['operation_mode']" />
                            </a-form-item>
                        </a-col>
                    </a-row>
                    <a-row :gutter="12">
                        <a-col :span="12">
                            <a-form-item label="法定代表人">
                                <a-input size="small" :value="formData['legal_representative']" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="12">
                            <a-form-item label="企业负责人">
                                <a-input size="small" :value="formData['principal']" />
                            </a-form-item>
                        </a-col>
                    </a-row>
                    <a-form-item label="住所">
                        <a-input size="small" :value="formData['address']" />
                    </a-form-item>
                    <a-form-item label="经营场所">
                        <a-input size="small" :value="formData['premises']" />
                    </a-form-item>
                    <a-form-item label="库房地址">
                        <a-input size="small" :value="formData['warehouse_address']" />
                    </a-form-item>
                    <a-row :gutter="12">
                        <a-col :span="12">
                            <a-form-item label="备案部门">
                                <a-input size="small" :value="formData['issue_organization']" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="12">
                            <a-form-item label="备案日期">
                                <a-input size="small" :value="formData['issue_date']" />
                            </a-form-item>
                        </a-col>
                    </a-row>
                </a-form>
            </a-col>
        </a-row>
    </a-spin>
</template>

<script>
import { platformAgencyDealerSecAPI } from '@/service/pageAjax';
import Gallery from '@/components/Picture/Gallery.vue';
import { operationModeMap } from './type';

export default {
    name: 'RecordKeepingInfo',
    components: {
        Gallery
    },
    props: {
        orgId: {
            required: true,
            validator(value) {
                return typeof value === 'string' || typeof value === 'number';
            }
        }
    },
    data() {
        return {
            loading: false,
            formData: {},
            images: []
        };
    },
    methods: {
        /**
         * 获取经销商第二类医疗器械经营备案凭证信息
         */
        async loadRecordKeepingInfo(id) {
            try {
                this.loading = true;
                const { code, info, msg } = await platformAgencyDealerSecAPI(id);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.images = info.images;
                this.formData = {
                    ...info,
                    'operation_mode':
                        info['operation_mode'] !== null && info['operation_mode'] !== undefined
                            ? operationModeMap[info['operation_mode']].text
                            : ''
                };
            } catch (err) {
                this.$message.error('第二类医疗器械经营备案凭证信息获取失败');
                throw err;
            } finally {
                this.loading = false;
            }
        }
    },
    mounted() {
        if (this.orgId) {
            this.loadRecordKeepingInfo(this.orgId);
        }
    },
    watch: {
        orgId() {
            if (this.orgId) {
                this.loadRecordKeepingInfo(this.orgId);
            }
        }
    }
};
</script>
