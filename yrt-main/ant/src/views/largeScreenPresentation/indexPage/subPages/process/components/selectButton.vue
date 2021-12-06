<template>
    <div class="orgProcessButtonContainer" @click="onSelected">
        <div class="orgProcessButtons" :style="`background: linear-gradient(90deg,${this.gardientColorStart}, ${this.gardientColorEnd})`">
            <div class="orgProcessButtonsInline">
                <img :src="imgPath" class="orgIcon">
            </div>
        </div>
        <div class="orgTypeName"> {{ orgTypeName }}</div>
        <div class="orgDetailName"> {{ orgDetailName }}</div>
    </div>
</template>

<script>
export default {
    props: {
        iconType: String
    },
    data () {
        return {
            imgPath: '',
            gardientColorStart: '',
            gardientColorEnd: '',
            orgTypeName: '',
            orgDetailName: ''
        }
    },
    methods: {
        onSelected () {
            this.$emit('selected')
        }
    },
    mounted () {
        switch (this.iconType) {
        // 生产商
        case 'ven':
            this.imgPath = '/images/largeScreen/icon/组 418.png'
            this.gardientColorStart = `#FBA993`
            this.gardientColorEnd = `#EF77A8`
            this.orgTypeName = '生产商' 
            break
            // 经销商
        case 'dea':
            this.imgPath = '/images/largeScreen/icon/组 420.png'
            this.gardientColorStart = `#FACD20`
            this.gardientColorEnd = `#FBB03B`
            this.orgTypeName = '经销商' 
            break
            // 医院
        case 'hos':
            this.imgPath = '/images/largeScreen/icon/组 423.png'
            this.gardientColorStart = `#0FE18A`
            this.gardientColorEnd = `#00A99D`
            this.orgTypeName = '医院' 
            break
            // 科室
        case 'cel':
            this.imgPath = '/images/largeScreen/icon/组 412.png'
            this.gardientColorStart = `#0FE1D9`
            this.gardientColorEnd = `#29ABE2`
            this.orgTypeName = '科室' 
            break
            // 回收商
        case 'rec':
            this.imgPath = '/images/largeScreen/icon/组 425.png'
            this.gardientColorStart = `#B98DFF`
            this.gardientColorEnd = `#7F58E6`
            this.orgTypeName = '回收商' 
            break
        }
    }
}
</script>

<style>

.orgProcessButtons {
    height: 1.72rem;
    width: 1.72rem;
    border-radius: 50%;
}

.orgProcessButtonsInline {
    height: 1.54rem;
    width: 1.54rem;
    top: 0.09rem;
    left: 0.09rem;
    border-style: solid;
    border-color: #fff;
    border-radius: 50%;
    position: relative;
}

.orgIcon {
    height: 0.8rem;
    margin-top: 0.3rem;
}

.orgTypeName {
    font-family: "PingFang SC";
    font-weight: 500;
    font-size: 0.44rem;
    line-height: 0.53rem;
    text-align: center;
    margin-top: 0.2rem;
    color: #fff;

}

.orgDetailName {
    font-family: "PingFang SC";
    font-weight: 500;
    font-size: 0.28rem;
    line-height: 0.53rem;
    text-align: center;
    margin-top: 0.2rem;
    color: #fff;

}
</style>
