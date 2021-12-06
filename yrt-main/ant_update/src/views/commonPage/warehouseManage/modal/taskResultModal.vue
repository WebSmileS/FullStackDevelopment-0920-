<template>
    <a-modal
        :title="title"
        :visible="visible"
        :maskClosable="false"
        :closable="false"
        :mask="true"
        :width="1000"
        :centered="true"
        @cancel="handleCancel"
        class="modal-form-input-scoped global-drag import-progress-modal"
    >
        <div v-globalDrag="{ el: 'import-progress-modal' }" class="modal-header-wrap">
            <h6 v-if="isOngoing">
                <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                任务进行中
            </h6>
        </div>
        <template slot="footer">
            <a-button v-if="errorExcel && errorExcel.url && !isOngoing" @click="handleDownload">
                <a-icon type="download" />下载错误文件
            </a-button>
            <a-button :disabled="isOngoing" :loading="isOngoing" @click="handleDone">
                <template v-if="isOngoing">进行中</template>
                <template v-else>完成</template>
            </a-button>
        </template>
        <a-alert
            v-if="task.status === 'exception' || task.status === 'success'"
            :type="alertType"
            :message="task.errMessage"
            banner
        />
        <a-progress :percent="task.percent" :status="task.status" />
    </a-modal>
</template>

<script>
export default {
    name: 'import-progress-modal',
    props: ['visible', 'task', 'errorExcel'],
    computed: {
        title() {
            return this.task.type === 'import' ? '导入任务' : '建账任务';
        },
        isOngoing() {
            // 任务进行中或文件上传中
            return !!this.task.id || this.task.status === 'uploading';
        },
        alertType() {
            switch (this.task.status) {
                case 'exception':
                    return 'error';
                case 'success':
                    return 'success';
                default:
                    return 'info';
            }
        }
    },
    methods: {
        handleCancel() {
            //todo 取消导入的逻辑
        },
        handleDone() {
            this.$emit('on-done');
        },

        handleDownload() {
            const a = document.createElement('a');
            a.download = this.errorExcel.filename;
            a.href = this.errorExcel.url;
            a.click();
        }
    }
};
</script>
