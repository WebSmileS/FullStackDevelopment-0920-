<template>
    <div class="warning-set-form">
        <p class="warn-advance">
            <span class="red">*</span>
            <span>提前</span>
            <a-input-number
                :disabled="!isPlatform"
                class="input-number"
                :class="isPlatform ? '' : 'disabled'"
                size="small"
                :min="0"
                :max="99999"
                v-model="days"
                @change="changeInput"
            />
            <span>天，预警</span>
        </p>
        <p class="warn-tips">提示：预警天数不能＜0，此处设置后全平台都按照这个天数执行预警</p>
    </div>
</template>

<script>
import { identification } from '../mixins/identification.js';

export default {
    name: 'warningSetForm', // 预警设置表单内容 = 模态框中的表单
    props: {
        parentDays: {
            // 父级预警天数
            type: Number,
            default: 0
        },
        tabsActiveKey: {
            // 模态框窗口tab选项卡当前的key标识，必传
            type: String,
            required: true
        }
    },
    data() {
        return {
            days: 0
        };
    },
    methods: {
        changeInput(val) {
            this.$emit('formInput', {
                'tabsActiveKey': this.tabsActiveKey,
                'activeDays': val
            });
        }
    },
    mixins: [identification],
    watch: {
        parentDays: {
            handler() {
                this.days = this.parentDays;
            },
            immediate: true
        }
    }
};
</script>

<style lang="less" scoped>
.warning-set-form {
    padding: 40px 20px;
    font-size: 14px;

    .warn-advance {
        display: flex;
        align-items: center;
        padding-bottom: 20px;
        color: #666;

        .red {
            margin: 4px 4px 0 0;
            color: #f00;
        }

        .input-number {
            margin: 0 10px;
            color: #555;
            font-weight: 600;

            &.disabled {
                color: #999;
            }
        }
    }

    .warn-tips {
        color: #999;
    }
}
</style>
