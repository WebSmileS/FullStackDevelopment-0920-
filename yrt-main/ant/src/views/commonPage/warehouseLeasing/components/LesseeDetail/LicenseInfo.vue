<template>
    <a-spin :spinning="loading">
        <a-row :gutter="16">
            <a-col :span="10">
                <gallery :images="images" :styles="{ height: '512px' }" />
            </a-col>
            <a-col :span="14">
                <a-form layout="horizontal">
                    <a-form-item label="名称">
                        <a-input size="small" :value="formData['name']" />
                    </a-form-item>
                    <a-form-item label="统一社会信用代码">
                        <a-input size="small" :value="formData['social_credit_code']" />
                    </a-form-item>
                    <a-form-item label="类型">
                        <a-input size="small" :value="formData['enterprise_type']" />
                    </a-form-item>
                    <a-row :gutter="12">
                        <a-col :span="12">
                            <a-form-item label="法定代表人">
                                <a-input size="small" :value="formData['legal_representative']" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="12">
                            <a-form-item label="注册资本">
                                <a-input size="small" :value="formData['registered_capital']" />
                            </a-form-item>
                        </a-col>
                    </a-row>
                    <a-form-item label="住所">
                        <a-input size="small" :value="formData['address']" />
                    </a-form-item>
                    <a-row>
                        <a-col :span="12">
                            <a-form-item label="成立日期">
                                <a-input size="small" :value="formData['establish_date']" />
                            </a-form-item>
                        </a-col>
                    </a-row>
                    <a-form-item label="营业期限">
                        <a-input-group size="small">
                            <a-row>
                                <a-col :span="11">
                                    <a-input size="small" :value="formData['begin_date']" />
                                </a-col>
                                <a-col :span="2" style="text-align: center">
                                    <span>至</span>
                                </a-col>
                                <a-col :span="11">
                                    <a-input size="small" :value="formData['end_date']" />
                                </a-col>
                            </a-row>
                        </a-input-group>
                    </a-form-item>
                    <a-form-item label="经营范围">
                        <a-textarea :value="formData['business_scope']" :auto-size="{ minRows: 2, maxRows: 5 }" />
                    </a-form-item>
                    <a-row :gutter="12">
                        <a-col :span="12">
                            <a-form-item label="登记机关">
                                <a-input size="small" :value="formData['issue_organization']" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="12">
                            <a-form-item label="发证日期">
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
import { platformAgencyLicAPI } from '@/service/pageAjax';
import Gallery from '@/components/Picture/Gallery.vue';
import { enterpriseTypeMap } from './type';

export default {
    name: 'LicenseInfo',
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
         * 获取经销商营业执照信息
         */
        async loadLicenseInfo(id) {
            try {
                this.loading = true;
                const { code, info, msg } = await platformAgencyLicAPI(2, id);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.images = info.images;
                this.formData = {
                    ...info,
                    'enterprise_type':
                        info['enterprise_type'] !== undefined && info['enterprise_type'] !== null
                            ? enterpriseTypeMap[info['enterprise_type']].text
                            : ''
                };
            } catch (err) {
                this.$message.error('营业执照信息获取失败');
                throw err;
            } finally {
                this.loading = false;
            }
        }
    },
    mounted() {
        if (this.orgId) {
            this.loadLicenseInfo(this.orgId);
        }
    },
    watch: {
        orgId() {
            if (this.orgId) {
                this.loadLicenseInfo(this.orgId);
            }
        }
    }
};
</script>
