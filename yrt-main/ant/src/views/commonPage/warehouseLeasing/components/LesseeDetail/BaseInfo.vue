<template>
    <a-spin :spinning="loading">
        <a-row :gutter="16">
            <a-col :span="10">
                <gallery :styles="{ height: '512px' }" :images="images" />
            </a-col>
            <a-col :span="14">
                <a-form layout="horizontal">
                    <a-form-item label="名称">
                        <a-input size="small" :value="formData['name']" />
                    </a-form-item>
                    <a-row :gutter="12">
                        <a-col :span="12">
                            <a-form-item label="统一社会信用代码">
                                <a-input size="small" :value="formData['social_credit_code']" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="12">
                            <a-form-item label="企业类型">
                                <a-input size="small" :value="formData['enterprise_type']" />
                            </a-form-item>
                        </a-col>
                    </a-row>
                    <a-form-item label="行政区划">
                        <a-input size="small" :value="formData['administractive_division']" />
                    </a-form-item>
                    <a-row :gutter="12">
                        <a-col :span="12">
                            <a-form-item label="成立日期">
                                <a-input size="small" :value="formData['establish_date']" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="12">
                            <a-form-item label="注册资本">
                                <a-input size="small" :value="formData['registered_capital']" />
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
                    <a-form-item label="开户银行">
                        <a-input size="small" :value="formData['bank_name']" />
                    </a-form-item>
                    <a-form-item label="银行账号">
                        <a-input size="small" :value="formData['bank_account']" />
                    </a-form-item>
                    <a-row :gutter="12">
                        <a-col :span="12">
                            <a-form-item label="电话">
                                <a-input size="small" :value="formData['phone']" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="12">
                            <a-form-item label="邮箱">
                                <a-input size="small" :value="formData['email']" />
                            </a-form-item>
                        </a-col>
                    </a-row>
                    <a-form-item label="地址">
                        <a-input size="small" :value="formData['address']" />
                    </a-form-item>
                    <a-form-item label="备注">
                        <a-textarea :value="formData['description']" :auto-size="{ minRows: 2, maxRows: 5 }" />
                    </a-form-item>
                </a-form>
            </a-col>
        </a-row>
    </a-spin>
</template>

<script>
import { listAreaAPI, platformAgenecyBaseInfoAPI, platformOrgImgListAPI } from '@/service/pageAjax';
import Gallery from '@/components/Picture/Gallery.vue';
import { enterpriseTypeMap } from './type';

export default {
    name: 'BaseInfo',
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
         * 获取经销商基本信息
         */
        async loadBaseInfo(id) {
            try {
                this.loading = true;
                const { code, info, msg } = await platformAgenecyBaseInfoAPI(2, id);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.formData = {
                    ...info,
                    'enterprise_type':
                        info['enterprise_type'] !== undefined && info['enterprise_type'] !== null
                            ? enterpriseTypeMap[info['enterprise_type']].text
                            : ''
                };
                await this.loadAreaInfo(info['administrative_division_sn']);
                await this.loadAuthorizationImages(id);
            } catch (err) {
                this.$message.error('基础信息获取失败');
                throw err;
            } finally {
                this.loading = false;
            }
        },
        /**
         * 加载经销商授权书图片列表
         * @param {string | number} id 机构id
         * @returns {Promise}
         */
        async loadAuthorizationImages(id) {
            try {
                const { code, list, msg } = await platformOrgImgListAPI(2, id, 9);
                if (code !== 0) {
                    throw new Error(msg);
                }
                this.images = list;
            } catch (err) {
                this.$message.error('授权书获取失败');
            }
        },
        /**
         * 加载行政区划信息
         * @param {string | number} id 机构id
         * @returns {Promise}
         */
        async loadAreaInfo(id) {
            try {
                this.loading = true;
                const { code, area, msg } = await listAreaAPI(id);
                let name = area.name;
                let current = area.parent;
                if (code !== 0) {
                    throw new Error(msg);
                }
                while (current) {
                    name = `${current.name}/${name}`;
                    current = current.parent;
                }
                this.formData = {
                    ...this.formData,
                    'administractive_division': name
                };
            } catch (err) {
                this.$message.error('行政区划信息获取失败');
                throw err;
            } finally {
                this.loading = false;
            }
        }
    },
    mounted() {
        if (this.orgId) {
            this.loadBaseInfo(this.orgId);
        }
    },
    watch: {
        orgId() {
            if (this.orgId) {
                this.loadBaseInfo(this.orgId);
            }
        }
    }
};
</script>
