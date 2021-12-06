<template>
    <div>
        <div class="process-list-title" :style="`background: linear-gradient(90deg,${this.gardientColorStart}, ${this.gardientColorEnd})`">
            {{ orgTypeName }}
        </div>
        <div class="process-list-container">
            <ul>
                <li 
                    class="process-list-item" 
                    @click="onSelected"
                    v-for="(i, index) in data" 
                    :key="index"
                    :value="i.name"
                >
                    {{ i.name }}
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import {
    screenOrgsInfoAPI
} from '@/service/pageAjax'

export default {
    props: {
        iconType: String
    },
    data () {
        return {
            gardientColorStart: '',
            gardientColorEnd: '',
            orgTypeName: '',
            data: [
                {name: 'xxxxxxxx'},
                {name: 'xxxxxxxx'},
                {name: 'xxxxxxxx'},
                {name: 'xxxxxxxx'},
                {name: 'xxxxxxxx'},
                {name: 'xxxxxxxx'},
                {name: 'xxxxxxxx'}
            ]
        }
    },
    methods: {
        async getOrgAPIFn (type) {
            await screenOrgsInfoAPI(parseInt(type))
               
        },
        onSelected () {
            this.$emit('selected')
        }
    },
    mounted () {
        switch (this.iconType) {
        // 生产商
        case 'ven':
            this.gardientColorStart = `#FBA993`
            this.gardientColorEnd = `#EF77A8`
            this.orgTypeName = '生产商' 
            this.getOrgAPIFn(3)
            break
            // 经销商
        case 'dea':
            this.gardientColorStart = `#FACD20`
            this.gardientColorEnd = `#FBB03B`
            this.orgTypeName = '经销商' 
            this.getOrgAPIFn(2)
            break
            // 医院
        case 'hos':
            this.gardientColorStart = `#0FE18A`
            this.gardientColorEnd = `#00A99D`
            this.orgTypeName = '医院' 
            this.getOrgAPIFn(1)
            break
            // 科室
        case 'cel':
            this.gardientColorStart = `#0FE1D9`
            this.gardientColorEnd = `#29ABE2`
            this.orgTypeName = '科室' 
            break
            // 回收商
        case 'rec':
            this.gardientColorStart = `#B98DFF`
            this.gardientColorEnd = `#7F58E6`
            this.orgTypeName = '回收商' 
            break
        }
    }
}
</script>

<style>
.process-list-container {
    height: 7.71rem;
    width: 2.78rem;
    overflow: auto;
    background-color: #0071bcbf;
    border: solid;
    border-color: #2C96D1;
    color: white;
}

.process-list-container ul {
    padding: 0 0 0 0;
}

.process-list-title {
    width: 100%;
    height: 0.55rem;
    border-top-right-radius: 12px;
    border-top-left-radius: 12px;
    font-family: "PingFang SC";
    font-weight: normal;
    font-size: 0.30rem;
    line-height: 0.5287rem;
    text-align: center;
    color: #fff;
}

.process-list-item {
    font-family: "PingFang SC";
    font-weight: normal;
    font-size: 0.22rem;
    line-height: 0.5287rem;
    text-align: center;
    color: #fff;
    margin-top: 0.12rem;
    margin-bottom: 0.12rem;
}

.process-list-item:hover {
    background-color: #2C96D1;
    cursor: grab;
}
</style>
