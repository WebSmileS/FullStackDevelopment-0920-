<template>
    <div>
        <div class="modal-form-input" style="padding-top: 0">
            <ul class="top-list" style="width: 100%">
                <li class="item">
                    <span class="label">发票日期</span>
                    <div class="input">
                        <a-range-picker
                            v-model="rangePickerVal"
                            :placeholder="['', '']"
                            @change="changeRangePickerFn"
                            size="small"
                        >
                        </a-range-picker>
                    </div>
                </li>
                <li class="item">
                    <span class="label" style="width: auto; max-width: auto">{{ snLabel }}</span>
                    <div class="input">
                        <a-input placeholder="" size="small" v-model="invoice_no" />
                    </div>
                </li>
                <li style="width: auto">
                    <a-button @click="handleSearch" size="small" type="primary"> 搜索 </a-button>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import moment from 'moment';

export default {
    name: 'returnSearch',
    props: {
        snLabel: {
            type: String,
            default: ''
        },
        isInbound: {
            type: Boolean, // 是否是付款单-true  收款单-false
            default: true
        },
        /// 搜索值
        searchData: {
            type: Object,
            required: true
        },
        // 表单info数据
        formData: {
            type: Object,
            retuired: true
        }
    },
    data() {
        return {
            rangePickerVal: moment[''], // 发票日期
            invoice_no: '',
            searchBeginDate: '',
            searchEndDate: ''
        };
    },
    methods: {
        changeRangePickerFn(value, dateString) {
            const [beginTime, endTime] = dateString;
            this.searchBeginDate = beginTime;
            this.searchEndDate = endTime;
        },
        handleSearch() {
            this.$emit('searchData', {
                begin_date: this.searchBeginDate,
                end_date: this.searchEndDate,
                invoice_no: this.invoice_no
            });
        }
    }
};
</script>

<style lang="less" scoped>
.input {
    position: relative;

    .close {
        position: absolute;
        right: 6px;
        transform: scale(0.9) translateY(-50%);
        top: 50%;
        z-index: 2;
        width: 15px;
        height: 15px;
        border-radius: 50%;
        background: #b9b9b9;
        font-size: 6px;
        color: #fff;
        text-align: center;
        line-height: 15px;
        display: none;
        cursor: pointer;
    }
    &:hover .close {
        display: block;
    }
}
</style>
